package cn.nodemedia.library;

/**
 * 静态变量
 * Created by Bining.
 */
public class Constants {

    public final static String USERATTRID = "UserAttrId";//用户唯一标识ID(服务端分配)

    public final static String HTTP_JSONCALL = "jsoncall/";
    public final static String HTTP_UPLOADCALL = "f-ul/";//http://域名(IP)[:端口]/应用路径/f-ul/文件路径（user|system|apps|chat|file)
    public final static String HTTP_DLFILECALL = "dlfile/";//http://域名(IP)[:端口]/应用路径/dlfile/文件路径
    public final static String HTTP_IMAGECALL = "image/";//http://域名(IP)[:端口]/应用路径/image/图片路径/图片名称.jpg

    public final static String LOCATION = "Location";//用户定位
    public final static String LOCATIONCITY = "LocationCity";//用户所在城市
    public final static String LOCATIONCITYTEMP = "LocationCityTemp";//用户所在城市(临时存储)
    public final static String LOCATIONCITYCODE = "LocationCityCode";//用户所在城市Code
    public final static String LOCATIONCITYCODETEMP = "LocationCityCodeTemp";//用户所在城市Code(临时存储)
    public final static String LOCATIONADDRESS = "LocationAddress";//用户位置
    public final static String LOCATIONDETAILADDRESS = "LocationDetailAddress";//用户详细位置
    public final static String LOCATIONLONG = "LocationLong";//用户经度位置
    public final static String LOCATIONLAT = "LocationLat";//用户纬度位置

}
