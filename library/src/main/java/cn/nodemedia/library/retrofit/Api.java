package cn.nodemedia.library.retrofit;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.nodemedia.library.App;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.utils.NetUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class Api {

    public static final int DEFAULT_TIMEOUT = 5000;

    public Retrofit retrofit;

    //构造方法私有
    private Api(String baseUrl) {

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
                        android.util.Log.e("OkHttpManager", String.format("Request %s on %s", request.url(), chain.connection()));
                        //Log.e("OkHttpManager", String.format("Request headers %n%s", request.headers()));
                        Response response = chain.proceed(request);
                        long t2 = System.nanoTime();
                        android.util.Log.e("OkHttpManager", String.format("Response Time %.1fms", (t2 - t1) / 1e6d));
                        //Log.e("OkHttpManager", String.format("Response for %s in %.1fms", response.request().url(), (t2 - t1) / 1e6d));
                        //Log.e("OkHttpManager", String.format("Response headers %n%s", response.headers()));
                        return response;
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
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
                })
                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(JSONConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        //movieService = retrofit.create(ApiService.class);
    }

// private static Map<String, Api> mAPIMap = new HashMap<>();

// public static Api getInstance(String baseUrl) {
// if (mAPIMap.containsKey(baseUrl)) {
// return mAPIMap.get(baseUrl);
// } else {
// Api api = SingletonHolder.INSTANCE;
// mAPIMap.put(baseUrl, api);
// return api;
// }
// }

    //获取单例
    public static Api getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final Api INSTANCE = new Api("");
    }

//    public static boolean upload(Context context, String uploadUrl, String fileUrl) {
//        String baseUrl = getBaseUrl(context, 0);//TODO 此方法需放在异步线程中
//        if (baseUrl != null) {
//            Map<String, RequestBody> map = new HashMap<>();
//            //map.put("param",RequestBody.create(MediaType.parse("text/plain"), "parma"));
//            if (!TextUtils.isEmpty(fileUrl)) {
//                File file = new File(fileUrl);
//                RequestBody fileBody = RequestBody.create(MediaType.parse("*/*"), file);
//                map.put("image\"; filename=\"" + file.getName() + "", fileBody);
//            }
//
//            Call<AbsT<UploadInfo>> uploadCall = RetrofitClient.getAPIClient(baseUrl).upload(uploadUrl, map);
//            uploadCall.enqueue(new Callback<AbsT<UploadInfo>>() {
//                @Override
//                public void onResponse(retrofit.Response<AbsT<UploadInfo>> response, Retrofit retrofit) {
//                    if (response != null && response.isSuccess()) {
//                        Log.e("访问成功");
//                        Log.e("Response:" + JSON.toJSONString(response.body()));
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    Log.e(t.getMessage(), t);
//                }
//            });
//        }
//        return false;
//    }
//
//    public static boolean download(Context context, String downloadUrl, final String fileUrl) {
//        String baseUrl = getBaseUrl(context, 0);//TODO 此方法需放在异步线程中
//        if (baseUrl != null) {
//            Call<InputStream> uploadCall = RetrofitClient.getAPIClient(baseUrl).download(downloadUrl);
//            uploadCall.enqueue(new Callback<InputStream>() {
//                @Override
//                public void onResponse(retrofit.Response<InputStream> response, Retrofit retrofit) {
//                    if (response != null && response.isSuccess()) {
//                        Log.e("访问成功");
//                        FileOutputStream fileOuputStream = null;
//                        try {
//                            int len;
//                            int size = 1024;
//                            byte[] buf;
//
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            buf = new byte[size];
//                            while ((len = response.body().read(buf, 0, size)) != -1) {
//                                bos.write(buf, 0, len);
//                            }
//                            buf = bos.toByteArray();
//
//                            fileOuputStream = new FileOutputStream(fileUrl);
//                            fileOuputStream.write(buf);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            try {
//                                fileOuputStream.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    Log.e(t.getMessage(), t);
//                }
//            });
//        }
//        return false;
//    }
}