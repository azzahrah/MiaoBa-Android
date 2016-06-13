package cn.nodemedia.leadlive;

/**
 * 静态变量
 * Created by Bining.
 */
public class Constants {

    public final static String APPVERSION = "AppVersion";//App版本号（根据是否与当前版本号相符来判断是否第一次使用APP)）

    public final static String USERACCOUNT = "UserAccount";//用户帐号
    public final static String USERCAPTCHA = "UserCaptcha";//用户验证码
    public final static String USERPWD = "UserPwd";//用户密码
    public final static String USERPWDSAVE = "UserIsSavePwd";//用户密码是否保存
    public final static String USEROPENID = "UserOpenId";//用户唯一开放ID
    public final static String USERISPAYPWD = "UserIsPAYPwd";//用户是否设置支付密码
    public final static String USERISLOGIN = "UserIsLogin";//用户是否已登陆
    public final static String USERNICKNAME = "UserNickName";//用户昵称

    public final static String PAYMENTWXPAY = "wxpay";//微信支付
    public final static String PAYMENTALIPAY = "alipay";//支付宝支付
    public final static String PAYMENTUnionPAY = "unionpay";//银联支付
    public final static String PAYMENTLOCALPAY = "local";//本地支付

    public final static String DNS_ADDRESS = "http://api.nodemedia.cn"; // DEV
    // public final static String DNS_ADDRESS = "http://jiuhao.simpleway.com.cn"; // ONLINE

    // public final static String HTTP_ = "";//

    public final static String HTTP_HOMECALL = DNS_ADDRESS + "/api/home/";

    public final static String HTTP_LOGIN = HTTP_HOMECALL + "login.html";//用户登陆 登陆帐号(String uname) 密码，加密后传递(String pwds) 登陆类型 1：注册用登陆 2：三方登陆(int type) 请求时间戳(int temptime) 签名加密串(String sign) >>UserInfo

    public final static String HTTP_USERINFO_GET = HTTP_HOMECALL + "/userinfo.html";//获取用户详细信息 登陆用户ID(int userid) 请求时间戳(int temptime) 签名加密串(String sign) >>UserInfo
    public final static String HTTP_USERINFO_POST = HTTP_HOMECALL + "userinfo.html";//编辑用户资料 登陆用户ID(int userid) 修改字段(String key) 修改内容(String val) 请求时间戳(int temptime) 签名加密串(String sign) >>null
    public final static String HTTP_FOLLOW_GET = HTTP_HOMECALL + "follow.html";//获取关注用户列表 登陆用户ID(int userid) 刷新类型(int type) 数据标识ID(int minid) 获取类型 follow 关注列表 fans粉丝列表(String gettype) 请求时间戳(int temptime) 签名加密串(String sign) >>FollowInfo
    public final static String HTTP_FOLLOW_POST = HTTP_HOMECALL + "follow.html";//关注用户 登陆用户ID(int userid) 被关注用户ID(int followid) 请求时间戳(int temptime) 签名加密串(String sign) >>null

    public final static String HTTP_LIVE_GET = HTTP_HOMECALL + "live.html";//获取直播列表 类型 1：最新（默认） 2：关注 3：热门(int type) 分页页数(int page) 每页显示条数(int pagesize) 请求时间戳(int temptime) 签名加密串(String sign) >>LiveInfo
    public final static String HTTP_LIVE_POST = HTTP_HOMECALL + "live/info.html";//发布直播 用户标识ID(int userid) 定位信息(String location) 标题(String title) 请求时间戳(int temptime) 签名加密串(String sign) >>null
    public final static String HTTP_LIVE_DEL = HTTP_HOMECALL + "live/info.html";//退出发布直播 直播标识ID(int id) 用户标识ID(int userid) 请求时间戳(int temptime) 签名加密串(String sign) >>null

}
