package cn.nodemedia.library.model;

/**
 * Retrofit 使用实例
 * http://tanwb@simpleway.com.cn/svn/cloudsmall/trunk
 *
 * @author Bining
 *         在Retrofit 2.0中，不管 response 是否能被解析。onResponse总是会被调用。但是在结果不能背解析的情况下，response.body()会返回null。别忘了处理这种情况。
 *         如果response存在什么问题，比如404什么的，onResponse也会被调用。你可以从response.errorBody().string()中获取错误信息的主体。
 */
public class RetrofitClient {

//    public interface APIInterface {
//
//        @GET("get/{id}")
//        Call<Object> getCall(@Path("id") int groupId, @Query("param") String param, @QueryMap Map<String, String> params);
//
//        @GET
//        Observable<Object> getObservable(@Url String URL, @Query("param") String param);
//
//        @POST("post/body")
//        Call<Object> postBody(@Body String body);
//
//        @FormUrlEncoded
//        @POST("/post/field")
//        Call<Object> postField(@Field("param") String param);
//
//        @Multipart
//        @POST("uploadTest")
//        Call<Object> postMultipart(@Part("param") RequestBody requestBody);
//
//        @GET(Constants.HTTP_DNSADDRESS)
//        Call<AbsT<
// ServerAddress>> getDNSAddress();
//
//        @Multipart
//        @POST(Constants.HTTP_FILECALL + "/{path}")
//        Call<AbsT<UploadInfo>> upload(@Path("path") String uploadUrl, @QueryMap Map<String, RequestBody> params);
//
//        @Headers({"Content-Type: */*"})
//        @GET("{path}")
//        Call<InputStream> download(@Path("path") String downloadUrl);
//    }
//
//    private static Map<String, APIInterface> mAPIMap = new HashMap<>();
//
//    public static APIInterface getAPIClient(String baseUrl) {
//        if (mAPIMap.containsKey(baseUrl)) {
//            return mAPIMap.get(baseUrl);
//        } else {
//            Retrofit client = new Retrofit.Builder()
//                    .baseUrl(baseUrl + Constants.HTTP_BASECALL)//指定基本连接地址
//                    .addConverterFactory(JSONConverterFactory.create())//指定序列化工厂
//                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//指定RxJava转化工厂
//                    .client(OkHttpManager.getInstance().getOkHttpClient())//指定OKHttp实例
//                    .build();
//            APIInterface mAPIInterface = client.create(APIInterface.class);
//            mAPIMap.put(baseUrl, mAPIInterface);
//            return mAPIInterface;
//        }
//    }
//
//    /**
//     * 获取基础连接地址
//     */
//    public static String getBaseUrl(Context context, int cycleIndex) {
//        try {
//            WeightAddress weightAddress = DbUtils.getInstance(context).findFirst(Selector.from(WeightAddress.class).where("type", "=", "0").orderBy("weight", true));
//            if (weightAddress != null) {
//                return "http://" + weightAddress.host + ":" + weightAddress.port;
//            } else {
//                cycleIndex++;
//                if (cycleIndex <= 3) {
//                    Call<AbsT<ServerAddress>> dnsCall = getAPIClient(Constants.DNS_ADDRESS).getDNSAddress();
//                    retrofit.Response<AbsT<ServerAddress>> response = dnsCall.execute();
//                    if (response.isSuccess() && response.body() != null && response.body().isSuccess() && response.body().target != null) {
//                        ServerAddress mServerAddress = response.body().target;
//                        for (WeightAddress w : mServerAddress.api) {
//                            w.type = 0;
//                        }
//                        for (WeightAddress w : mServerAddress.message) {
//                            w.type = 1;
//                        }
//                        DbUtils.getInstance(context).deleteAll(WeightAddress.class);
//                        DbUtils.getInstance(context).saveAll(mServerAddress.api);
//                        DbUtils.getInstance(context).saveAll(mServerAddress.message);
//                    }
//                    return getBaseUrl(context, cycleIndex);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
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