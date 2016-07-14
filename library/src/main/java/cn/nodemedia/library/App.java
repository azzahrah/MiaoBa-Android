package cn.nodemedia.library;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cn.nodemedia.library.utils.Log;
import dalvik.system.DexFile;

/**
 * 应用操作 Created by Bining.
 */
public class App {

    private static class MockApplication extends Application {
        public MockApplication(Context baseContext) {
            this.attachBaseContext(baseContext);
        }
    }

    public static class Ext {
        private static boolean debug;
        private static Application app;

        private Ext() {
        }

        public static void init(Application app) {
            init(app, false);
        }

        public static void init(Application app, boolean debug) {
            if (Ext.app == null) {
                Ext.app = app;
            }
            Ext.debug = debug;
        }
    }

    public static Application app() {
        if (Ext.app == null) {
            try {
                // 在IDE进行布局预览时使用
                Class<?> renderActionClass = Class.forName("com.android.layoutlib.bridge.impl.RenderAction");
                Method method = renderActionClass.getDeclaredMethod("getCurrentContext");
                Context context = (Context) method.invoke(null);
                Ext.app = new MockApplication(context);
            } catch (Throwable ignored) {
                throw new RuntimeException("please invoke x.Ext.init(app) on Application#onCreate()"
                        + " and register your Application in manifest.");
            }
        }
        return Ext.app;
    }

    public static boolean isDebug() {
        return Ext.debug;
    }

    private static PackageInfo getPackageInfo() {
        try {
            PackageManager e = app().getPackageManager();
            return e.getPackageInfo(app().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用名称
     */
    public static String getAppName() {
        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            int labelRes = packageInfo.applicationInfo.labelRes;
            return app().getResources().getString(labelRes);
        }
        return null;
    }

    /**
     * 获取App包名
     */
    public static String getPackageName() {
        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            return packageInfo.packageName;
        }
        return null;
    }

    /**
     * 获取App版本
     */
    public static String getVersionName() {
        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }

    /**
     * 获取App版本号
     */
    public static int getVersionCode() {
        PackageInfo packageInfo = getPackageInfo();
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    /**
     * 获取手机信息 需要权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     */
    public static TelephonyManager getTelephonyInfo() {
        TelephonyManager tm = (TelephonyManager) app().getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = ").append(tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = ").append(tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = ").append(tm.getLine1Number());
        sb.append("\nNetworkCountryIso = ").append(tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = ").append(tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = ").append(tm.getNetworkOperatorName());
        sb.append("\nNetworkType = ").append(tm.getNetworkType());
        sb.append("\nPhoneType = ").append(tm.getPhoneType());
        sb.append("\nSimCountryIso = ").append(tm.getSimCountryIso());
        sb.append("\nSimOperator = ").append(tm.getSimOperator());
        sb.append("\nSimOperatorName = ").append(tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = ").append(tm.getSimSerialNumber());
        sb.append("\nSimState = ").append(tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = ").append(tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = ").append(tm.getVoiceMailNumber());
        return tm;
    }

    /**
     * 获取手机可用的cpu数
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static ActivityManager.RunningAppProcessInfo getProcessInfo(String processName) {
        ActivityManager activityManager = (ActivityManager) app().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
        if (appProcessList != null && appProcessList.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
                if (appProcess.processName.equals(processName)) {
                    return appProcess;
                }
            }
        }
        return null;
    }

    /**
     * 判断某个应用当前是否正在运行
     *
     * @param processName 应用包名
     */
    public static boolean isNamedProcess(String processName) {
        int pid = android.os.Process.myPid();
        ActivityManager.RunningAppProcessInfo appProcess = getProcessInfo(processName);
        return appProcess != null && appProcess.pid == pid;
    }

    /**
     * 判断程序是否在后台运行
     */
    public static boolean isAppInBackground() {
        ActivityManager.RunningAppProcessInfo appProcess = getProcessInfo(getPackageName());
        return appProcess != null
                && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    /**
     * 判断服务是否运行
     *
     * @param serviceName 服务名称
     * @return true为运行，false为不在运行
     */
    public static boolean isServiceRunning(String serviceName) {
        ActivityManager activityManager = (ActivityManager) app().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceInfoList != null && serviceInfoList.size() > 0) {
            for (ActivityManager.RunningServiceInfo serviceInfo : serviceInfoList) {
                Log.e(serviceInfo.service.getClassName());
                if (serviceInfo.service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 安装apk
     *
     * @param fileName apk文件的绝对路径
     */
    public static void installAPK(String fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
        app().startActivity(intent);
    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent;
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction(Intent.ACTION_VIEW);
        }
        activity.startActivity(intent);
    }

    /**
     * 判断当前线程是否为UI线程
     */
    public static boolean isUIThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    /**
     * 获取AndroidManifest.xml中<meta-data>元素的值
     *
     * @param name 元素名称
     */
    @SuppressWarnings("unchecked")
    public static <T> T getMetaData(String name) {
        try {
            ApplicationInfo ai = app().getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return (T) ai.metaData.get(name);
            }
        } catch (Exception e) {
            System.out.print("Couldn't find meta-data: " + name);
        }
        return null;
    }

    /**
     * 获取某一个包名下的所有类名
     *
     * @param packageName 包名
     * @return 类名列表
     * @throws IOException
     */
    public static List<String> getPackageAllClassName(Context context, String packageName) throws IOException {
        String sourcePath = context.getApplicationInfo().sourceDir;
        List<String> paths = new ArrayList<String>();
        if (sourcePath != null) {
            DexFile dexfile = new DexFile(sourcePath);
            Enumeration<String> entries = dexfile.entries();

            while (entries.hasMoreElements()) {
                String element = entries.nextElement();
                if (element.contains(packageName)) {
                    paths.add(element);
                }
            }
        }
        return paths;
    }

    /**
     * 打开软键盘
     */
    public static void openKeybord(View view) {
        InputMethodManager imm = (InputMethodManager) app().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 2);
        imm.toggleSoftInput(2, 1);
    }

    /**
     * 关闭虚拟键盘
     */
    public static void hideSoftInputFromWindow(View... views) {
        InputMethodManager imm = (InputMethodManager) app().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (views != null && views.length > 0) {
            for (View view : views) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
