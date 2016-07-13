package cn.nodemedia.leadlive.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.leadlive.bean.FollowInfo;
import cn.nodemedia.leadlive.bean.LiveInfo;
import cn.nodemedia.leadlive.bean.UserInfo;
import cn.nodemedia.library.App;
import cn.nodemedia.library.bean.Abs;
import cn.nodemedia.library.bean.AbsL;
import cn.nodemedia.library.bean.AbsT;
import cn.nodemedia.library.bean.UploadInfo;
import cn.nodemedia.library.retrofit.JSONConverterFactory;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.utils.MD5;
import cn.nodemedia.library.utils.NetUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 网络接口
 * Created by Bining on 16/6/12.
 */
public class HttpUtils {

    private static final int DEFAULT_TIMEOUT = 5000;

    private static HttpUtils httpUtils;
    private static Map<String, Retrofit> mRetrofitMap = new HashMap<>();

    private Retrofit getRetrofit(String baseUrl) {

        if (mRetrofitMap.containsKey(baseUrl)) {
            return mRetrofitMap.get(baseUrl);
        }

        File cacheFile = new File(App.app().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new Interceptor() {//设置拦截器
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //请求拦截器，可用来做日志记录
                        Request request = chain.request();
                        long t1 = System.nanoTime();
                        Log.e(String.format("Request %s on %s method %s ", request.url(), chain.connection(), request.method()));
                        if (request.method().equals("POST")) {
                            Log.e(String.format("Request body %s ", request.body().toString()));
                        }
                        // Log.e(String.format("Request headers %n%s", request.headers()));
                        Response response = chain.proceed(request);
                        long t2 = System.nanoTime();
                        Log.e(String.format("Request Time %.1fms", (t2 - t1) / 1e6d));
                        // Log.e("OkHttpManager", String.format("Response for %s in %.1fms", response.request().url(), (t2 - t1) / 1e6d));
                        // Log.e("OkHttpManager", String.format("Response headers %n%s", response.headers()));
                        return response;
                    }
                }).addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        if (!NetUtils.isConnected()) {
                            request = request.newBuilder()
                                    .cacheControl(CacheControl.FORCE_CACHE)
                                    .build();
                            Log.d("no network");
                        }

                        Response originalResponse = chain.proceed(request);
                        if (!NetUtils.isConnected()) {
                            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", request.cacheControl().toString())
                                    .removeHeader("Pragma")
                                    .build();
                        } else {
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                                    .removeHeader("Pragma")
                                    .build();
                        }
                    }
                }).cache(cache)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(JSONConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        mRetrofitMap.put(baseUrl, retrofit);

        return retrofit;
        //movieService = retrofit.create(ApiService.class);
    }

    public interface ApiService {

        @FormUrlEncoded
        @POST(Constants.HTTP_LOGIN)
        Observable<AbsT<UserInfo>> login(@Field("json") String param);

        @FormUrlEncoded
        @POST(Constants.HTTP_REGISTER)
        Observable<AbsT<UserInfo>> register(@Field("json") String param);

        @GET(Constants.HTTP_USERINFO_GET)
        Observable<AbsT<UserInfo>> getUserInfo(@Query("json") String json);

        @FormUrlEncoded
        @POST(Constants.HTTP_USERINFO_POST)
        Observable<Abs> postUserInfo(@Field("json") String json);

        @GET(Constants.HTTP_FOLLOW_GET)
        Observable<AbsL<FollowInfo>> getFollowList(@Query("json") String json);

        @FormUrlEncoded
        @POST(Constants.HTTP_FOLLOW_POST)
        Observable<Abs> postFollow(@Field("json") String json);

        @GET(Constants.HTTP_LIVE_GET)
        Observable<AbsL<LiveInfo>> getLiveList(@Query("json") String json);

        @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
        Observable<String> getWXToken(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code, @Query("grant_type") String grant_type);

        @GET("https://api.weixin.qq.com/sns/userinfo")
        Observable<String> getWXUserInfo(@Query("access_token") String access_token, @Query("openid") String openid);

        @GET("https://api.weibo.com/2/users/show.json")
        Observable<String> getSinaUserInfo(@Query("access_token") String access_token, @Query("uid") String uid);

        @GET("get/{id}")
        Call<Object> getCall(@Path("id") int groupId, @Query("param") String param, @QueryMap Map<String, String> params);

        @GET
        Observable<Object> getObservable(@Url String URL, @Query("param") String param);

        @POST("post/body")
        Call<Object> postBody(@Body String body);

        @FormUrlEncoded
        @POST("/post/field")
        Call<Object> postField(@Field("param") String param);

        @Multipart
        @POST("uploadTest")
        Call<Object> postMultipart(@Part("param") RequestBody requestBody);

        @Multipart
        @POST("/{path}")
        Call<AbsT<UploadInfo>> upload(@Path("path") String uploadUrl, @QueryMap Map<String, RequestBody> params);

        @Headers({"Content-Type: */*"})
        @GET("{path}")
        Call<InputStream> download(@Header("X-LC-Session") String sesssion, @Path("path") String downloadUrl);

    }

    private synchronized static ApiService getApiService() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils.getRetrofit(Constants.DNS_ADDRESS).create(ApiService.class);
    }

    /**
     * 用户登陆
     *
     * @param uname 登陆帐号或OpenId
     * @param pwds  密码
     * @param type  登陆类型 1：注册用登陆 2：三方登陆
     */
    public static Observable<AbsT<UserInfo>> login(String uname, String pwds, int type) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("uname", uname);
        sortMap.put("pwds", TextUtils.isEmpty(pwds) ? "" : MD5.md5(pwds));
        sortMap.put("type", type);

        return getApiService().login(getHttpParams(sortMap));
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
    public static Observable<AbsT<UserInfo>> register(String uname, String pwds, String nick, String faces, String utype) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("uname", uname);
        sortMap.put("pwds", TextUtils.isEmpty(pwds) ? "" : MD5.md5(pwds));
        sortMap.put("nick", nick);
        sortMap.put("faces", faces);
        sortMap.put("utype", utype);

        return getApiService().register(getHttpParams(sortMap));
    }

    /**
     * 获取用户详细信息
     *
     * @param userid 登陆用户ID
     */
    public static Observable<AbsT<UserInfo>> getUserInfo(int userid) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);

        return getApiService().getUserInfo(getHttpParams(sortMap));
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
    public static Observable<Abs> postUserInfo(int userid, String key, String val) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("key", key);
        sortMap.put("val", val);

        return getApiService().postUserInfo(getHttpParams(sortMap));
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
    public static Observable<AbsL<FollowInfo>> getFollowList(int userid, int type, int minid, String gettype) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("type", type);
        sortMap.put("minid", minid);
        sortMap.put("gettype", gettype);

        return getApiService().getFollowList(getHttpParams(sortMap));
    }

    /**
     * 关注用户(取消关注)
     *
     * @param userid   登陆用户ID
     * @param followid 被关注用户ID
     */
    public static Observable<Abs> postFollow(int userid, int followid) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("userid", userid);
        sortMap.put("followid", followid);

        return getApiService().postFollow(getHttpParams(sortMap));
    }

    /**
     * 获取直播列表
     *
     * @param type 类型 1：最新（默认） 2：关注  3：热门
     * @param page 分页页数
     */
    public static Observable<AbsL<LiveInfo>> getLiveList(int type, int page) {
        Map<String, Object> sortMap = new HashMap<>();
        sortMap.put("type", type);
        sortMap.put("page", page);
        sortMap.put("pagesize", 20);

        return getApiService().getLiveList(getHttpParams(sortMap));
    }

    private static String getHttpParams(Map<String, Object> params) {
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
        String tempStr = "";
        //	使用 Map按key进行排序
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            tempStr += entry.getValue();
        }
        tempStr = tempStr + "NodeMedia@2016";
        Log.e("tempStr:" + tempStr);
        treeMap.put("sign", MD5.md5(tempStr));

        return JSON.toJSONString(treeMap);
    }

    public static Observable<String> getWXToken(String code) {
        return getApiService().getWXToken("", "", code, "authorization_code");
    }

    public static Observable<String> getWXUserInfo(String authToken, String openId) {
        return getApiService().getWXUserInfo(authToken, openId);
    }

    public static Observable<String> getSinaUserInfo(String authToken, String uid) {
        return getApiService().getSinaUserInfo(authToken, uid);
    }

    private static class JsonInfo {
        public JsonInfo(String json) {
            this.json = json;
        }

        public String json;
    }

    // public class RxSchedulers {
    // public static <T> Observable.Transformer<T, T> io_main() {
    // return tObservable -> tObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    // }
    // }

    //    OkHttpUtils.get(Urls.URL_METHOD) // 请求方式和请求url, get请求不需要拼接参数，支持get，post，put，delete，head，options请求
    //    .tag(this)               // 请求的 tag, 主要用于取消对应的请求
    //    .connTimeOut(10000)      // 设置当前请求的连接超时时间
    //    .readTimeOut(10000)      // 设置当前请求的读取超时时间
    //    .writeTimeOut(10000)     // 设置当前请求的写入超时时间
    //    .cacheKey("cacheKey")    // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
    //    .cacheMode(CacheMode.DEFAULT) // 缓存模式，详细请看第四部分，缓存介绍
    //    目前提供了四种CacheMode缓存模式
    //    DEFAULT: 按照HTTP协议的默认缓存规则，例如有304响应头时缓存
    //    REQUEST_FAILED_READ_CACHE：先请求网络，如果请求网络失败，则读取缓存，如果读取缓存失败，本次请求失败。该缓存模式的使用，会根据实际情况，导致onResponse,onError,onAfter三个方法调用不只一次，具体请在三个方法返回的参数中进行判断。
    //    IF_NONE_CACHE_REQUEST：如果缓存不存在才请求网络，否则使用缓存。
    //    FIRST_CACHE_THEN_REQUEST：先使用缓存，不管是否存在，仍然请求网络，如果网络顺利，会导致onResponse方法执行两次，第一次isFromCache为true，第二次isFromCache为false。使用时根据实际情况，对onResponse,onError,onAfter三个方法进行具体判断。
    //    .setCertificates(getAssets().open("srca.cer")) // 自签名https的证书，可变参数，可以设置多个
    //    .addInterceptor(interceptor)            // 添加自定义拦截器
    //    .headers("header1", "headerValue1")     // 添加请求头参数
    //    .headers("header2", "headerValue2")     // 支持多请求头参数同时添加
    //    .params("param1", "paramValue1")        // 添加请求参数
    //    .params("param2", "paramValue2")        // 支持多请求参数同时添加
    //    .params("file1", new File("filepath1")) // 可以添加文件上传
    //    .params("file2", new File("filepath2")) // 支持多文件同时添加上传
    //    .addCookie("aaa", "bbb")                // 这里可以传递自己想传的Cookie
    //    .addCookie(cookie)                      // 可以自己构建cookie
    //    .addCookies(cookies)                    // 可以一次传递批量的cookie
    //    //这里给出的泛型为 RequestInfo，同时传递一个泛型的 class对象，即可自动将数据结果转成对象返回
    //    .execute(new DialogCallback<RequestInfo>(this, RequestInfo.class) {
    //        @Override
    //        public void onBefore(BaseRequest request) {
    //            // UI线程 请求网络之前调用
    //            // 可以显示对话框，添加/修改/移除 请求参数
    //        }
    //
    //        @Override
    //        public RequestInfo parseNetworkResponse(Response response) throws Exception{
    //            // 子线程，可以做耗时操作
    //            // 根据传递进来的 response 对象，把数据解析成需要的 RequestInfo 类型并返回
    //            // 可以根据自己的需要，抛出异常，在onError中处理
    //            return null;
    //        }
    //
    //        @Override
    //        public void onResponse(boolean isFromCache, RequestInfo requestInfo, Request request, @Nullable Response response) {
    //            // UI 线程，请求成功后回调
    //            // isFromCache 表示当前回调是否来自于缓存
    //            // requestInfo 返回泛型约定的实体类型参数
    //            // request     本次网络的请求信息，如果需要查看请求头或请求参数可以从此对象获取
    //            // response    本次网络访问的结果对象，包含了响应头，响应码等，如果数据来自于缓存，该对象为null
    //        }
    //
    //        @Override
    //        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
    //            // UI 线程，请求失败后回调
    //            // isFromCache 表示当前回调是否来自于缓存
    //            // call        本次网络的请求对象，可以根据该对象拿到 request
    //            // response    本次网络访问的结果对象，包含了响应头，响应码等，如果网络异常 或者数据来自于缓存，该对象为null
    //            // e           本次网络访问的异常信息，如果服务器内部发生了错误，响应码为 400~599之间，该异常为 null
    //        }
    //
    //        @Override
    //        public void onAfter(boolean isFromCache, @Nullable RequestInfo requestInfo, Call call, @Nullable Response response, @Nullable Exception e) {
    //            // UI 线程，请求结束后回调，无论网络请求成功还是失败，都会调用，可以用于关闭显示对话框
    //            // isFromCache 表示当前回调是否来自于缓存
    //            // requestInfo 返回泛型约定的实体类型参数，如果网络请求失败，该对象为　null
    //            // call        本次网络的请求对象，可以根据该对象拿到 request
    //            // response    本次网络访问的结果对象，包含了响应头，响应码等，如果网络异常 或者数据来自于缓存，该对象为null
    //            // e           本次网络访问的异常信息，如果服务器内部发生了错误，响应码为 400~599之间，该异常为 null
    //        }
    //
    //        @Override
    //        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    //            // UI 线程，文件上传过程中回调，只有请求方式包含请求体才回调（GET,HEAD不会回调）
    //            // currentSize  当前上传的大小（单位字节）
    //            // totalSize 　 需要上传的总大小（单位字节）
    //            // progress     当前上传的进度，范围　0.0f ~ 1.0f
    //            // networkSpeed 当前上传的网速（单位秒）
    //        }
    //
    //        @Override
    //        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    //            // UI 线程，文件下载过程中回调
    //            //参数含义同　上传相同
    //        }
    //    });

}
