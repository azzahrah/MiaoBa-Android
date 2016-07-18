package cn.nodemedia.library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.nodemedia.library.bean.ChatMessage;
import cn.nodemedia.library.bean.MQTTQueue;
import cn.nodemedia.library.utils.EncryptToBase64Util;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.utils.NetUtils;
import cn.nodemedia.library.utils.SharedUtils;

/**
 * MQTT消息进程
 * Created by Bining.
 */
public class MQTTService extends Service implements MqttCallback {

    public static final int MQTT_QOS_0 = 0; // QOS Level 0 ( Delivery Once no confirmation )
    public static final int MQTT_QOS_1 = 1; // QOS Level 1 ( Delevery at least Once with confirmation )
    public static final int MQTT_QOS_2 = 2; // QOS Level 2 ( Delivery only once with confirmation with handshake )

    private static final String MQTT_URL_FORMAT = "tcp://%s:%d";
    private static final String MQTT_CLIENT_ID = "%s-APP";

    private static final String MQTT_BROKER = "test.simpleway.com.cn";
    private static final int MQTT_PORT = 9400;

    private static final String ACTION_CONNECT = "MQTTService.START"; // Action to start
    private static final String ACTION_RECONNECT = "MQTTService.RECONNECT"; // Action to reconnect
    private static final int MQTT_CONNECT_ALIVE = 120000; // ConnectAlive Interval in MS

    private Context context;

    // private String mDeviceId;       // Device ID, Secure.ANDROID_ID
    private Handler mConnHandler;     // Seperate Handler thread for networking
    private AlarmManager mAlarmManager; // Alarm manager to perform repeating tasks

    private MqttClient mqttClient;// Mqtt Client
    private MqttClientPersistence mqttClientPersistence;// Defaults to FileStore
    private MqttConnectOptions mqttConnectOptions;// Connection Options

    private String openId;
    private String authToken;
    private String clientId;
    private boolean Connecting = false;// 是否正在连接MQTT
    private int ConnectionCount = 0;

    private BlockingQueue<MQTTQueue> analysisData;
    private AnalysisThread analysisThread;
    private Map<String, String> openIdMap = new HashMap<>();

    /**
     * Start MQTT Client
     */
    public static void actionConnect(Context context) {
        Intent i = new Intent(context, MQTTService.class);
        i.setAction(ACTION_CONNECT);
        context.startService(i);
    }

    /**
     * Reconnect MQTT Client
     */
    public static void actionReconnect(Context context) {
        Intent i = new Intent(context, MQTTService.class);
        i.setAction(ACTION_RECONNECT);
        context.startService(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MQTTService onCreate");
        context = this;
        // mDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        HandlerThread thread = new HandlerThread("MQTTService[" + new Random().nextInt() + "]");
        thread.start();
        mConnHandler = new Handler(thread.getLooper());
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("MQTTService onStartCommand");
        String action = intent.getAction();
        if (action == null || action.equals(ACTION_CONNECT)) {
            Log.e("Received ACTION_CONNECT");
            connect();
            if (!App.isServiceRunning("cn.nodemedia.library.GuardService")) {
                GuardService.actionGuard(this);
            }
        } else if (action.equals(ACTION_RECONNECT)) {
            Log.e("Received ACTION_RECONNECT");
            reconnectIfNecessary();
        }
        flags = START_STICKY;// or START_REDELIVER_INTENT
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MQTTService onDestroy");
        if (hasConnectAlives()) {
            stopConnectAlives();
        }
        // 销毁时重新启动Service
        actionConnect(context);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return stub;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * 连接到broker与适当的数据存储
     */
    private synchronized void connect() {
        if (isConnected()) {
            Log.e("MQTT Client 已经连接,忽略本次连接请求");
            return;
        }

        if (!NetUtils.isConnected()) {
            Log.e("网络环境异常,忽略本次连接请求");
            if (hasConnectAlives()) {
                stopConnectAlives();
            }
            return;
        } else {
            if (!hasConnectAlives()) {
                starConnectAlives();
            }
        }

        openId = SharedUtils.getInt(BaseConstants.USEROPENID, 0) + "";
        authToken = SharedUtils.getString(BaseConstants.USERAUTHTOKEN, null);

        if (TextUtils.isEmpty(openId) || TextUtils.isEmpty(authToken)) {
            Log.e("MQTT Client 未获取到连接标识,忽略本次连接请求");
            return;
        }

        clientId = String.format(Locale.US, MQTT_CLIENT_ID, openId);

        String url = String.format(Locale.US, MQTT_URL_FORMAT, MQTT_BROKER, MQTT_PORT);

        Log.e("Connecting with URL: " + url + " and ClientID: " + clientId);
        try {
            if (mqttClientPersistence == null) {
                mqttClientPersistence = new MqttDefaultFilePersistence(getCacheDir().getAbsolutePath());
                // mqttClientPersistence = new MemoryPersistence();
            }
            if (mqttConnectOptions == null) {
                mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setConnectionTimeout(10);//设置连接超时时间
                mqttConnectOptions.setKeepAliveInterval(30);//设置心跳维持间隔时间
                mqttConnectOptions.setCleanSession(true);//设置是否清除Session
            }
            mqttClient = new MqttClient(url, clientId, mqttClientPersistence);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mConnHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttConnectOptions.setUserName(clientId); // 参见用户名说明
                    mqttConnectOptions.setPassword(authToken.toCharArray()); // 参见授权码说明
                    mqttClient.connect(mqttConnectOptions);
                    mqttClient.setCallback(MQTTService.this);
                    // String message = "{\"openId\":\"" + memData.openId + "\",\"time\":\"" + System.currentTimeMillis() + "\",\"state\":" + 1 + "}";
                    // MqttTopic topics = mqttClient.getTopic(Constants.COMMONTOPIC);
                    // options.setWill(topics, message.getBytes(), 0, false);
                    Log.d("MQTT服务连接成功");
                } catch (MqttException exception) {
                    Log.e("MQTT服务异常:" + exception.getMessage());
                    if (exception.getReasonCode() == 4) {
                        // EventBus.getDefault().post(new EventNotice(Constants.HTTP_LOGOUT));
                    } else {
                        try {
                            // DbUtils.getInstance(context).delete(weightAddress);
                            mqttClient = null;
                            ConnectionCount++;
                            if (ConnectionCount <= 5) {
                                Log.e("MQTT将于1秒后重连,重连次数: " + ConnectionCount);
                                mConnHandler.postDelayed(this, 1000);
                            } else {
                                ConnectionCount = 0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private synchronized void reconnectIfNecessary() {
        if (isConnected()) {
            mConnHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mqttClient.disconnect();
                        mqttClient = null;

                        connect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            connect();
        }
    }

    private synchronized boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    private void starConnectAlives() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_CONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MQTT_CONNECT_ALIVE, MQTT_CONNECT_ALIVE, pi);
    }

    private void stopConnectAlives() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_CONNECT);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.cancel(pi);
    }

    private synchronized boolean hasConnectAlives() {
        Intent i = new Intent();
        i.setClass(this, MQTTService.class);
        i.setAction(ACTION_CONNECT);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    MQTTServiceAIDL.Stub stub = new MQTTServiceAIDL.Stub() {

        @Override
        public boolean publishMsg(String topic, String content) throws RemoteException {
            Log.e("发送消息》》主题：" + topic + " 消息：" + content);
            try {
                MqttMessage mqttMessage = new MqttMessage(EncryptToBase64Util.encryptMessageData(content.getBytes("UTF-8"), authToken));

                mqttMessage.setQos(MQTT_QOS_2);
                mqttMessage.setRetained(false);
                MqttTopic topics = mqttClient.getTopic(topic);

                IMqttDeliveryToken iMqttDeliveryToken = topics.publish(mqttMessage);
                Log.e("消息交付完成》》消息ID：" + iMqttDeliveryToken.getMessageId() + " 消息状态：" + iMqttDeliveryToken.isComplete());

                return true;
            } catch (UnsupportedEncodingException | MqttException e) {
                return false;
            }
        }
    };

    /**
     * 与 broker 失去连接
     */
    @Override
    public void connectionLost(Throwable throwable) {
        Log.e("MQTT Client与 Broker 失去连接.\t异常描述:" + throwable.getMessage());
        mqttClient = null;
        if (NetUtils.isConnected()) {
            reconnectIfNecessary();
        }
    }

    /**
     * 发布消息完成
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.e("消息交付完成》》消息ID：" + iMqttDeliveryToken.getMessageId() + " 消息状态：" + iMqttDeliveryToken.isComplete());
    }

    /**
     * 从 broker 收到消息
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.e("收到消息》》Topic:\t" + topic + "  Message:\t" + new String(mqttMessage.getPayload()) + "  QoS:\t" + mqttMessage.getQos());
        if (analysisData == null) {
            analysisData = new LinkedBlockingQueue<>();
        }
        analysisData.put(new MQTTQueue(topic, mqttMessage));

        mConnHandler.post(new Runnable() {
            @Override
            public void run() {
                if (analysisData != null && analysisData.size() > 0) {
                    try {
                        MQTTQueue mqttQueue = analysisData.poll(1, TimeUnit.SECONDS);
                        if (mqttQueue != null) {
                            String msgTopic = mqttQueue.topic;
                            String msgJson = new String(EncryptToBase64Util.decryptMessageData(mqttQueue.message.getPayload(), authToken));
                            Log.e("收到消息》》主题：" + msgTopic + " 消息：" + msgJson);
                            ChatMessage chatMessage = JSON.parseObject(msgJson, ChatMessage.class);
                            chatMessage.topic = msgTopic;
                            chatMessage.state = 0;

                            switch (chatMessage.m8) {
                                case 2001:
                                    break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // if (analysisThread == null || !analysisThread.isAlive()) {
        // analysisThread = new AnalysisThread();
        // analysisThread.start();
        // }
    }

    /**
     * 接收消息线程
     */
    public class AnalysisThread extends Thread {

        @Override
        public void run() {
            try {
                while (analysisData != null) {
                    MQTTQueue mqttQueue = analysisData.take();// //如果队列为空,会阻塞当前线程.
                    if (mqttQueue != null) {
                        String msgTopic = mqttQueue.topic;
                        String msgJson = new String(EncryptToBase64Util.decryptMessageData(mqttQueue.message.getPayload(), authToken));
                        android.util.Log.e("AnalysisThread", "收到消息》》主题：" + msgTopic + " 消息：" + msgJson);
                        ChatMessage chatMessage = JSON.parseObject(msgJson, ChatMessage.class);
                        chatMessage.topic = msgTopic;
                        chatMessage.state = 0;

                        switch (chatMessage.m8) {
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
