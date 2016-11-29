package cn.nodemedia.leadlive.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.bean.FollowInfo;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.bean.UserInfo;
import okhttp3.MultipartBody;
import rx.schedulers.Schedulers;
import xyz.tanwb.airship.App;
import xyz.tanwb.airship.bean.FileInfo;
import xyz.tanwb.airship.retrofit.HttpRequestBody;
import xyz.tanwb.airship.retrofit.RetrofitManager;
import xyz.tanwb.airship.rxjava.schedulers.AndroidSchedulers;
import xyz.tanwb.airship.utils.FileUtils;
import xyz.tanwb.airship.utils.Log;
import xyz.tanwb.airship.utils.MD5;

/**
 * 网络接口
 * Created by Bining on 16/6/12.
 */
public class HttpUtils {

    private static RetrofitManager mInstance; //单例

    private static RetrofitManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManager(getBaseUrl());
                }
            }
        }
        return mInstance;
    }

    private static String getBaseUrl() {
        if (App.isDebug()) {
            return Constants.DNS_ADDRESS_DEV;
        } else {
            return Constants.DNS_ADDRESS_ONLINE;
        }
    }

    /**
     * 上传文件
     *
     * @param typePath 文件保存路径 可选:user|system|apps|chat|file
     * @param filePath 文件本地路径
     */
    public static void upload(String typePath, String filePath, HttpCallback<FileInfo> asyncCallBack) {
        asyncCallBack.setHttpMark("upload");
        asyncCallBack.openProgress();
        File file = new File(filePath);
        HttpRequestBody requestBody = new HttpRequestBody(file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("photos", file.getName(), requestBody);
        // 若需配量上传:List<MultipartBody.Part> parts = new ArrayList<>(filePaths.length);
        getInstance().getService().upload(typePath, part).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(asyncCallBack);
    }

    /**
     * 文件下载
     *
     * @param path 文件路径(格式:dlfile/***.***)
     */
    public static void download(String path, HttpCallback<String> asyncCallBack) {
        asyncCallBack.setHttpMark(FileUtils.getFileSuffix(path, false));
        asyncCallBack.openProgress();
        getInstance().getService().download(path).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(asyncCallBack);
    }

    /**
     * Get 请求
     *
     * @param httpUrl 请求路径/方法名
     * @param params  参数
     */
    private static void getHttp(String httpUrl, Map<String, Object> params, HttpCallback<?> asyncCallBack) {
        getInstance().getService().getHttp(httpUrl, params)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(asyncCallBack);
    }

    /**
     * Get 请求
     *
     * @param httpUrl 请求路径/方法名
     * @param params  参数
     */
    private static void getHttpToJson(String httpUrl, Map<String, Object> params, HttpCallback<?> asyncCallBack) {
        getInstance().getService().getHttpToJson(httpUrl, getParamsJson(params))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(asyncCallBack);
    }

    /**
     * Post 请求
     *
     * @param httpUrl 请求路径/方法名
     * @param params  参数
     */
    private static void postHttpToJson(String httpUrl, Map<String, Object> params, HttpCallback<?> asyncCallBack) {
        getInstance().getService().postHttpToJson(httpUrl, getParamsJson(params))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(asyncCallBack);
    }

    private static String getParamsJson(Map<String, Object> params) {
        TreeMap<String, Object> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        });
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            treeMap.put(entry.getKey(), entry.getValue());
        }
        treeMap.put("temptime", System.currentTimeMillis());
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            stringBuilder.append(entry.getValue());
        }
        stringBuilder.append("NodeMedia@2016");
        treeMap.put("sign", MD5.md5(stringBuilder.toString()));

        Log.e("HttpParams:" + JSON.toJSONString(treeMap));
        return JSON.toJSONString(treeMap);
    }

    /**
     * 用户登陆
     *
     * @param uname 登陆帐号或OpenId
     * @param pwds  密码
     * @param type  登陆类型 1：注册登陆 2：三方登陆
     */
    public static void login(String uname, String pwds, int type, HttpCallback<UserInfo> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("uname", uname);
        sortMap.put("pwds", TextUtils.isEmpty(pwds) ? "" : MD5.md5(pwds));
        sortMap.put("type", type);
        postHttpToJson(Constants.HTTP_LOGIN, sortMap, asyncCallBack);
    }

    /**
     * 用户注册
     *
     * @param uname 登陆帐号或OpenId
     * @param pwds  密码
     * @param nick  昵称
     * @param faces 头像
     * @param utype 注册类型 QQ WeiXin Sina RenRen DouBan
     */
    public static void register(String uname, String pwds, String nick, String faces, String utype, HttpCallback<UserInfo> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("uname", uname);
        sortMap.put("pwds", TextUtils.isEmpty(pwds) ? "" : MD5.md5(pwds));
        sortMap.put("nick", nick);
        sortMap.put("faces", faces);
        sortMap.put("utype", utype);
        postHttpToJson(Constants.HTTP_REGISTER, sortMap, asyncCallBack);
    }

    /**
     * 获取用户详细信息
     *
     * @param userid 登陆用户ID
     */
    public static void getUserInfo(int userid, HttpCallback<UserInfo> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        getHttpToJson(Constants.HTTP_USERINFO_GET, sortMap, asyncCallBack);
    }

    /**
     * 编辑用户资料
     *
     * @param userid 登陆用户ID
     * @param key    修改字段
     *               修改字段描述
     *               -- nickname 用户昵称
     *               -- faces 用户头像
     *               -- sex 性别 1：男 2：女
     *               -- autograph 签名
     *               -- phone 绑定手机号
     *               -- images 直播形象照
     *               -- cityid 家乡
     *               -- birthday 生日
     *               -- emotion 情感
     *               -- occupation 职业
     * @param val    修改值
     */
    public static void editUserInfo(int userid, String key, String val, HttpCallback<Object> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("key", key);
        sortMap.put("val", val);
        postHttpToJson(Constants.HTTP_USERINFO_POST, sortMap, asyncCallBack);
    }

    /**
     * 获取关注用户列表
     *
     * @param userid  登陆用户ID
     *                当 type == 1时，为下拉刷新，minid为最大数据标识ID
     *                当 type == 2时，为上滑加载更多，minid为列表中最小数据ID
     * @param type    刷新类型
     * @param minid   数据标识ID
     * @param gettype 获取类型 follow 关注列表 fans粉丝列表
     */
    public static void getFollowList(int userid, int type, int minid, String gettype, HttpCallback<List<FollowInfo>> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("type", type);
        sortMap.put("minid", minid);
        sortMap.put("gettype", gettype);
        getHttpToJson(Constants.HTTP_FOLLOW_GET, sortMap, asyncCallBack);
    }

    /**
     * 关注或取消关注用户
     *
     * @param userid   登陆用户ID
     * @param followid 被关注用户ID
     */
    public static void postFollow(int userid, int followid, HttpCallback<Object> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("followid", followid);
        postHttpToJson(Constants.HTTP_FOLLOW_POST, sortMap, asyncCallBack);
    }

    /**
     * 获取直播列表
     *
     * @param type 类型 1：最新（默认） 2：关注  3：热门
     * @param page 分页页数
     */
    public static void getLiveList(int type, int page, HttpCallback<List<LiveInfo>> asyncCallBack) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("type", type);
        sortMap.put("page", page);
        sortMap.put("pagesize", 20);
        getHttpToJson(Constants.HTTP_LIVE_GET, sortMap, asyncCallBack);
    }

    public static void getWXToken(String code, HttpCallback<String> asyncCallBack) {
        asyncCallBack.setToJson(false);
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("appid", "");
        sortMap.put("secret", "");
        sortMap.put("code", code);
        sortMap.put("grant_type", "authorization_code");
        getHttp("https://api.weixin.qq.com/sns/oauth2/access_token", sortMap, asyncCallBack);
    }

    public static void getWXUserInfo(String authToken, String openId, HttpCallback<String> asyncCallBack) {
        asyncCallBack.setToJson(false);
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("access_token", authToken);
        sortMap.put("openid", openId);
        getHttp("https://api.weixin.qq.com/sns/userinfo", sortMap, asyncCallBack);
    }

    public static void getSinaUserInfo(String authToken, String uid, HttpCallback<String> asyncCallBack) {
        asyncCallBack.setToJson(false);
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("access_token", authToken);
        sortMap.put("uid", uid);
        getHttp("https://api.weibo.com/2/users/show.json", sortMap, asyncCallBack);
    }

}
