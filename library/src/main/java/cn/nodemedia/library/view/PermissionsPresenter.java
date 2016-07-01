package cn.nodemedia.library.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.library.utils.Log;

/**
 * Android 6.0 权限
 * Created by Bining on 16/6/28.
 */
public abstract class PermissionsPresenter<T extends BaseView> extends BasePresenter<T> {

    private final static int REQUEST_PERMISSIONS = 60;

    private List<String> permissionsList = new ArrayList<>();

    /**
     * 获取权限
     */
    public void questPermissions(String[] permissions) {
        permissionsList.clear();
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            // 判断是否有权限
            if (ContextCompat.checkSelfPermission(mView.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // 判断是否需要 向用户解释，为什么要申请该权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) mView.getContext(), permission)) {
                    permissionsNeeded.add(permission);
                }
            }
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) mView.getContext(), permissionsList.toArray(new String[permissionsList.size()]), REQUEST_PERMISSIONS);
                    }
                });
                return;
            }

            // 请求权限
            ActivityCompat.requestPermissions((Activity) mView.getContext(), permissionsList.toArray(new String[permissionsList.size()]), REQUEST_PERMISSIONS);
            return;
        }

        onPermissionsSuccess();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(mView.getContext())
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                boolean result = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        result = false;
                    }
                }
                if (result) {
                    onPermissionsSuccess();
                } else {
                    onPermissionsFailure("程序所需权限被拒绝");
                }
                break;
        }
    }

    public abstract void onPermissionsSuccess();

    public void onPermissionsFailure(String strMsg) {
        Log.e(strMsg);
    }

    // Android 6.0 需要申请的权限
    // android.permission_group.CALENDAR
    // READ_CALENDAR 允许程序读取用户日历数据
    // WRITE_CALENDAR 允许程序写入但不读取用户日历数据
    // android.permission_group.CAMERA
    // CAMERA 请求访问使用照相设备
    // android.permission_group.CONTACTS
    // READ_CONTACTS 允许程序读取用户联系人数据
    // WRITE_CONTACTS 允许程序写入但不读取用户联系人数据
    // GET_ACCOUNTS 访问一个帐户列表在Accounts Service中
    // android.permission_group.LOCATION
    // ACCESS_FINE_LOCATION 通过GPS芯片接收卫星的定位信息,定位精度达10米以内.
    // ACCESS_COARSE_LOCATION 访问CellID或WiFi,只要当前设备可以接收到基站的服务信号,便可获得位置信息.
    // android.permission_group.MICROPHONE
    // RECORD_AUDIO 允许程序录制音频(
    // android.permission_group.PHONE
    // READ_PHONE_STATE 允许程序访问电话状态
    // CALL_PHONE 允许程序从非系统拨号器里输入电话号码
    // READ_CALL_LOG 允许程序读取用户的联系人数据
    // WRITE_CALL_LOG 允许程序写入(但是不能读)用户的联系人数据
    // ADD_VOICEMAIL 允许应用程序添加语音邮件系统
    // USE_SIP 允许程序使用SIP视频服务
    // PROCESS_OUTGOING_CALLS 允许程序监视、修改有关播出电话
    // android.permission_group.SENSORS
    // BODY_SENSORS 允许人体传感器
    // android.permission-group.SMS
    // SEND_SMS 允许程序发送短信
    // RECEIVE_SMS 允许程序接收短信
    // READ_SMS 允许程序读取短信内容
    // RECEIVE_WAP_PUSH 允许程序接收WAP PUSH信息
    // RECEIVE_MMS 允许程序接收彩信
    // android.permission-group.STORAGE
    // READ_EXTERNAL_STORAGE 程序可以读取设备外部存储空间的文件(内置SDcard和外置SDCard).
    // WRITE_EXTERNAL_STORAGE 允许程序写入外部存储(内置SDcard和外置SDCard).

    //    不需要手动申请的权限
    //    android.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    //    android.permission.ACCESS_NETWORK_STATE
    //    android.permission.ACCESS_NOTIFICATION_POLICY
    //    android.permission.ACCESS_WIFI_STATE
    //    android.permission.ACCESS_WIMAX_STATE
    //    android.permission.BLUETOOTH
    //    android.permission.BLUETOOTH_ADMIN
    //    android.permission.BROADCAST_STICKY
    //    android.permission.CHANGE_NETWORK_STATE
    //    android.permission.CHANGE_WIFI_MULTICAST_STATE
    //    android.permission.CHANGE_WIFI_STATE
    //    android.permission.CHANGE_WIMAX_STATE
    //    android.permission.DISABLE_KEYGUARD
    //    android.permission.EXPAND_STATUS_BAR
    //    android.permission.FLASHLIGHT
    //    android.permission.GET_ACCOUNTS
    //    android.permission.GET_PACKAGE_SIZE
    //    android.permission.INTERNET
    //    android.permission.KILL_BACKGROUND_PROCESSES
    //    android.permission.MODIFY_AUDIO_SETTINGS
    //    android.permission.NFC
    //    android.permission.READ_SYNC_SETTINGS
    //    android.permission.READ_SYNC_STATS
    //    android.permission.RECEIVE_BOOT_COMPLETED
    //    android.permission.REORDER_TASKS
    //    android.permission.REQUEST_INSTALL_PACKAGES
    //    android.permission.SET_TIME_ZONE
    //    android.permission.SET_WALLPAPER
    //    android.permission.SET_WALLPAPER_HINTS
    //    android.permission.SUBSCRIBED_FEEDS_READ
    //    android.permission.TRANSMIT_IR
    //    android.permission.USE_FINGERPRINT
    //    android.permission.VIBRATE
    //    android.permission.WAKE_LOCK
    //    android.permission.WRITE_SYNC_SETTINGS
    //    com.android.alarm.permission.SET_ALARM
    //    com.android.launcher.permission.INSTALL_SHORTCUT
    //    com.android.launcher.permission.UNINSTALL_SHORTCUT
}
