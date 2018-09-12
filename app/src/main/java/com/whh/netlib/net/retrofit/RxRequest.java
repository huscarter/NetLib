package com.whh.netlib.net.retrofit;


import com.google.gson.Gson;
import com.whh.netlib.Config;
import com.whh.netlib.net.IService;
import com.whh.netlib.net.http.OkHttpClientProvider;
import com.whh.netlib.util.PreferenceUtil;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 网络Retrofit对外接口
 * 接受传值:<BR/>
 * 对外传值:<BR/>
 */

public class RxRequest {

    protected static final String TAG = RxRequest.class.getSimpleName();

    /**
     * 请求接口的版本号
     */
    public static final int DEFAULT_VERSION = 1;

    /**
     * 默认请求是比不重新请求
     */
    public static final boolean RETRY_REQUEST = false;


    public RxRequest() {
        //
    }

    /**
     * 返回实体类http,增加版本号
     *
     * @return
     */
    public static IService createLib() {
        return createSSL(IService.class, 4, getToken());
    }

    /**
     * 返回实体类http,增加版本号
     *
     * @return
     */
    public static IService createLib(int version) {
        return createSSL(IService.class, version, getToken());
    }

    /**
     * 返回实体类http,增加版本号
     *
     * @param c
     * @param <T>
     * @return
     */
    public static <T> T createLib(Class<T> c, int version) {
        return createSSL(c, version, getToken());
    }

    /**
     * 返回实体类,http自定token
     *
     * @param c
     * @param <T>
     * @retur
     */
    public static <T> T createLib(Class<T> c, String token) {
        return createSSL(c, DEFAULT_VERSION, token);
    }

    /**
     * 返回实体类,http自定token
     *
     * @param c
     * @param <T>
     * @retur
     */
    public static <T> T createLib(Class<T> c, String token, int version) {
        return createSSL(c, version, token, RETRY_REQUEST);
    }

    /**
     * 最底层获取请求实体的方法,http
     * 返回实体类,http自定token
     *
     * @param clazz
     * @param <T>
     * @retur
     */
    public static <T> T createLib(Class<T> clazz, int version, String token, boolean retry) {
        Retrofit.Builder builder = getRetrofitBuilder(version, token, retry);
        return builder.build().create(clazz);
    }

    /**
     * 获取最底层的Retrofit，没有添加转换器，如果需要返回 Gson 格式请自己添加GsonConverterFactory
     *
     * @param version
     * @param token
     * @param retry
     * @return
     */
    public static Retrofit.Builder getRetrofitBuilder(int version, String token, boolean retry) {
        return new Retrofit.Builder()
                .baseUrl(Config.IP)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(OkHttpClientProvider.getInstance().getClient(version, token));
    }

    //---------------------------------使用SSL方法分界线--------------------------------------------------------------

    /**
     * 发送网络请求方法
     *
     * @param version 版本
     * @return
     */
    public static <T> T createSSL(Class<T> clazz, int version) {
        return createSSL(clazz, version, getToken());
    }

    /**
     * 发送网络请求方法
     *
     * @param version 版本
     * @return
     */
    public static <T> T createSSL(Class<T> clazz, int version, String token) {
        return createSSL(clazz, version, token, RETRY_REQUEST);
    }

    /**
     * 最底层获取请求实体的方法,https
     * 返回实体类,http自定token
     *
     * @param clazz
     * @param <T>
     * @retur
     */
    public static <T> T createSSL(Class<T> clazz, int version, String token, boolean retry) {
        Retrofit.Builder builder = getSSLRetrofitBuilder(version, token, retry);
        return builder.build().create(clazz);
    }

    /**
     * 获取最底层的Retrofit，没有添加转换器，如果需要返回 Gson 格式请自己添加GsonConverterFactory
     *
     * @param version 服务器接口版本
     * @param token 用户token
     * @param retry 重复请求次数
     * @return
     */
    public static Retrofit.Builder getSSLRetrofitBuilder(int version, String token, boolean retry) {
        return new Retrofit.Builder()
                .baseUrl(Config.IP)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(OkHttpClientProvider.getInstance().getClientSSL(version, token));
    }


    //------------------------------------公共方法------------------------------------------------------------

    public static void unsubscribeIfNotNull(CompositeDisposable subscription) {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    public static CompositeDisposable getSubscription(CompositeDisposable subscription) {
        if (subscription == null || subscription.isDisposed()) {
            return new CompositeDisposable();
        }
        return subscription;
    }

    /**
     * 获取token
     *
     * @return
     */
    public static String getToken() {
        //JLog.e(TAG,"token:"+PreferenceUtil.getString(Config.TOKEN, ""));
        return PreferenceUtil.getString(Config.TOKEN, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxODM1ODU4NzYyMSIsImlzcyI6Imh0dHA6XC9cL2FwaS5kZXYtMy5qc2MubG9jXC9hcGlcL2F1dGhcL2xvZ2luIiwiaWF0IjoiMTUxMjU0MDIyOSIsImV4cCI6IjE1MTMxNDUwMjkiLCJuYmYiOiIxNTEyNTQwMjI5IiwianRpIjoiN2VlZjg2ZmFmODQyODM5NzhhMzMxNmMxOTM5M2MxMDUifQ.Jam5qtlQ-NDsPYYy_TR_GP-FskwEKo1dA1GAczCTmvU");
    }

    /**
     * 获取HttpClient
     *
     * @return
     */
    public static OkHttpClient getClient() {
        OkHttpClient client = OkHttpClientProvider.getInstance().getClient(DEFAULT_VERSION, getToken());
        return client;
    }

}
