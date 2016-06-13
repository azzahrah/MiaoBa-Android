package cn.nodemedia.leadlive;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.lzy.okhttputils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

import cn.nodemedia.library.App;
import cn.nodemedia.library.bean.EventBusInfo;
import cn.nodemedia.library.glide.GlideUtils;
import cn.nodemedia.library.utils.Log;

/**
 * MyApplication
 * Created by Bining.
 */
public class MyApplication extends Application {

    private List<Activity> activityList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        App.Ext.init(this, true);
        GlideUtils.initGlide(this, Constants.DNS_ADDRESS);

        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        //HttpHeaders headers = new HttpHeaders();
        //headers.put("commonHeaderKey1", "commonHeaderValue1");    //所有的 header 都 不支持 中文
        //headers.put("commonHeaderKey2", "commonHeaderValue2");
        //HttpParams params = new HttpParams();
        //params.put("commonParamsKey1", "commonParamsValue1");     //所有的 params 都 支持 中文
        //params.put("commonParamsKey2", "这里支持中文参数");
        OkHttpUtils.getInstance()//
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS);            //全局的写入超时时间
        //.setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
        //.setCookieStore(new PersistentCookieStore())                       //cookie持久化存储，如果cookie不过期，则一直有效
        //.addCommonHeaders(headers)                                         //设置全局公共头
        //.addCommonParams(params);                                          //设置全局公共参数

        //注册广播
        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);// 时间改变广播,每分钟发出一次
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                EventBus.getDefault().post(new EventBusInfo(ConnectivityManager.CONNECTIVITY_ACTION));
            } else {
                //时间改变广播
            }
        }
    }

    public void addActivity(Activity activity) {
        Log.d("Activity创建 " + activity.getClass().getName());
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        Log.d("Activity移除 " + activity.getClass().getName());
        activityList.remove(activity);
    }

    public void removeActivity(String activityName) {
        Log.d("Activity移除 " + activityName);
        for (Activity activity : activityList) {
            if (activity.getClass().getName().equals(activityName)) {
                activity.finish();
            }
        }
    }

    public void exit() {
        for (Activity activity : activityList) {
            Log.d("Activit销毁 " + activity.getClass().getName());
            activity.finish();
        }
    }

    public void exitOtherActivity(Activity activity) {
        for (Activity a : activityList) {
            if (a != activity) {
                Log.d("Activit销毁 " + a.getClass().getName());
                a.finish();
            }
        }
    }
}
