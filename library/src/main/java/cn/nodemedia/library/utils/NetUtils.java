package cn.nodemedia.library.utils;

import cn.nodemedia.library.App;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 网络操作
 * Created by Bining.
 */
public class NetUtils {

    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_TYPE_3G = "3g";
    public static final String NETWORK_TYPE_2G = "2g";
    public static final String NETWORK_TYPE_WAP = "wap";
    public static final String NETWORK_TYPE_UNKNOWN = "unknown";
    public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

    public static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) App.app().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    /**
     * 判断是否已经连接或正在连接
     */
    public static boolean isActiveNetwork() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * 判断网络是否已经连接
     */
    public static boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnected() && info.getState() == NetworkInfo.State.CONNECTED;
    }

    /**
     * 获取网络类型
     */
    public static int getNetworkType() {
        NetworkInfo networkInfo = getNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    /**
     * 获取网络类型名称
     */
    @SuppressWarnings("deprecation")
    public static String getNetworkTypeName() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeName = networkInfo.getTypeName();
            if ("WIFI".equalsIgnoreCase(typeName)) {
                return NETWORK_TYPE_WIFI;
            } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                return TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork() ? NETWORK_TYPE_3G : NETWORK_TYPE_2G)
                        : NETWORK_TYPE_WAP;
            } else {
                return NETWORK_TYPE_UNKNOWN;
            }
        }
        return NETWORK_TYPE_DISCONNECT;
    }

    public static boolean isConnectedMobile() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean isConnectedWifi() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 是否连接快速移动网络
     */
    private static boolean isFastMobileNetwork() {
        TelephonyManager telephonyManager = (TelephonyManager) App.app().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }

    private static WifiInfo getWifiInfo() {
        WifiManager wifiManager = (WifiManager) App.app().getSystemService(Context.WIFI_SERVICE);
        return wifiManager == null ? null : wifiManager.getConnectionInfo();
    }

    /**
     * 获取WIFI名称
     */
    public static String getWifiName() {
        WifiInfo wifiInfo = getWifiInfo();
        String wifiName = wifiInfo.getSSID();
        if (wifiName != null) {
            if (!wifiName.contains("<unknown ssid>") && wifiName.length() > 2) {
                if (wifiName.startsWith("\"") && wifiName.endsWith("\"")) {
                    wifiName = wifiName.subSequence(1, wifiName.length() - 1).toString();
                }
                return wifiName;
            }
        }
        return null;
    }

    /**
     * 获取WIFI IP地址
     */
    public static String getWifiIpAddress() {
        WifiInfo localWifiInfo = getWifiInfo();
        return localWifiInfo != null ? convertIntToIp(localWifiInfo.getIpAddress()) : null;
    }

    /**
     * 格式化IP地址
     */
    private static String convertIntToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    /**
     * 检测gps状态
     */
    public static boolean checkGPSStatus() {
        LocationManager lm = (LocationManager) App.app().getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
