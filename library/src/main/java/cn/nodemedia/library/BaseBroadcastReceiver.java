package cn.nodemedia.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import cn.nodemedia.library.utils.NetUtils;

/**
 * 广播监听
 * Created by Bining on 16/7/8.
 */
public class BaseBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {//网络改变
            if (NetUtils.isConnected()) {
                MQTTService.actionConnect(context);
            }
        } else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {//开机
            MQTTService.actionConnect(context);
        }
    }
}
