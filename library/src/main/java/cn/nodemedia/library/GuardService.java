package cn.nodemedia.library;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import cn.nodemedia.library.utils.Log;

public class GuardService extends Service {

    private static final int MQTT_CONNECT_ALIVE = 60000; // ConnectAlive Interval in MS

    private Context context;
    private AlarmManager mAlarmManager; // Alarm manager to perform repeating tasks

    public static void actionGuard(Context context) {
        context.startService(new Intent(context, GuardService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("GuardService onCreate");
        context = this;
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (!hasGuardAlives()) {
            starGuardAlives();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("GuardService onStartCommand");
        if (!App.isServiceRunning("cn.nodemedia.library.MqttService")) {
            MQTTService.actionConnect(this);
        }
        flags = START_STICKY;// or START_REDELIVER_INTENT
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("GuardService onDestroy");
        if (hasGuardAlives()) {
            stopGuardAlives();
        }
        // 销毁时重新启动Service
        actionGuard(context);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void starGuardAlives() {
        Intent i = new Intent(this, GuardService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + MQTT_CONNECT_ALIVE, MQTT_CONNECT_ALIVE, pi);
    }

    private void stopGuardAlives() {
        Intent i = new Intent(this, GuardService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.cancel(pi);
    }

    private synchronized boolean hasGuardAlives() {
        Intent i = new Intent(this, GuardService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

}
