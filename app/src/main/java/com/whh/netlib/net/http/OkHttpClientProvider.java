package com.whh.netlib.net.http;


import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.whh.netlib.BuildConfig;
import com.whh.netlib.Config;
import com.whh.netlib.net.crt.SSlContextProvider;

import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 配置okhttp初始化信息,请求分3步,第一步连接,第二部发送请求,第三步获取数据
 * 接受传值:<BR/>
 * 对外传值:<BR/>
 */

public class OkHttpClientProvider {
    private static final String TAG = OkHttpClientProvider.class.getSimpleName();

    /**
     * 请求头中的User-Agent定义
     */
    public static String UA = "AndroidPlatform/" + BuildConfig.VERSION_NAME + "(Android" + Build.VERSION.RELEASE + ";" + Build.MODEL + ")";

    /**
     * 设置连接超时时间
     */
    public static final int OKHTTP_CLIENT_CONNECT_TIMEOUT = 30;
    /**
     * 设置写入超时时间
     */
    public static final int OKHTTP_CLIENT_WRITE_TIMEOUT = 30;
    /**
     * 设置读取时间
     */
    public static final int OKHTTP_CLIENT_READ_TIMEOUT = 30;

    private static OkHttpClientProvider instance;

    /**
     * okhttp client 池,根据version和token有无创建不同的client
     */
    private Map<String, OkHttpClient> clients = new Hashtable<>();

    /**
     * 单例构造函数
     */
    private OkHttpClientProvider() {
        //
    }

    public static OkHttpClientProvider getInstance() {
        if (instance == null) {
            synchronized (OkHttpClientProvider.class) {
                if (instance == null) {
                    instance = new OkHttpClientProvider();
                }
            }
        }
        return instance;
    }

    /**
     * 通过version和token获取okhttp client
     * 有token的key为token[version],无token的key为[version]
     *
     * @param version
     * @param token
     * @return
     */
    public OkHttpClient getClient(int version, String token) {
        String target = getUniqueKey(version, token);

        OkHttpClient client;
        if (clients.get(target) != null) {
            client = clients.get(target);
        } else {
            client = createClient(version, token);
        }
        return client;

    }

    /**
     * 创建client 并添加到client池。
     * 这里为了减少client的创建，只判别version和token两个条件，retry被忽略
     *
     * @param version
     * @param token
     * @return
     */
    private OkHttpClient createClient(int version, String token) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        config(builder, version, token);

        OkHttpClient client = builder.build();
        clients.put(getUniqueKey(version, token), client);
        return client;
    }

    /**
     * 通过version和token获取okhttp client
     * 有token的key为token[version],无token的key为[version]
     *
     * @param version
     * @param token
     * @return
     */
    public OkHttpClient getClientSSL(int version, String token) {
        String target = getUniqueKey(version, token);
        OkHttpClient client;
        if (clients.get(target) != null) {
            client = clients.get(target);
        } else {
            client = createClientSSL(version, token);
        }
        return client;

    }

    /**
     * Close and remove all idle connections in the pool,if the pool is too big.
     *
     * @param client
     */
    private void evictClient(OkHttpClient client) {
        if (client.connectionPool().connectionCount() > 30) {
            client.connectionPool().evictAll();
        }
    }

    /**
     * 一般退出app调用，清空okhttp client缓存，因为期header保有上个帐号的token
     */
    public void clear() {
        for (String key : clients.keySet()) {
            if (clients.get(key) != null) {
                clients.get(key).connectionPool().evictAll();
            }
        }
        clients.clear();
    }

    /**
     * 创建client 并添加到client池。
     * 这里为了减少client的创建，只判别version和token两个条件，retry被忽略
     *
     * @param version
     * @param token
     * @return
     */
    private OkHttpClient createClientSSL(int version, String token) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        config(builder, version, token);
        builder.sslSocketFactory(SSlContextProvider.getInstance().getSocketFactory(), SSlContextProvider.getInstance().getTrustManager());

        OkHttpClient client = builder.build();

        clients.put(getUniqueKey(version, token), client);

        return client;
    }

    /**
     * 生成client对应的key
     *
     * @return
     */
    private String getUniqueKey(int version, String token) {
        return ((token == null || token.length() == 0) ? "" : token) + version;
    }

    /**
     * 配置网络延迟，header和logger等
     *
     * @param builder
     * @param version
     * @param token
     */
    private void config(OkHttpClient.Builder builder, int version, String token) {
        builder.connectTimeout(OKHTTP_CLIENT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(OKHTTP_CLIENT_WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(OKHTTP_CLIENT_READ_TIMEOUT, TimeUnit.SECONDS);
//        builder.retryOnConnectionFailure(retry);

        // to add header
        builder.addNetworkInterceptor(new HeaderInterceptor(version, token));

        // to add log
        if (Config.DEBUG) {
            // 自定义的log日志
            builder.addNetworkInterceptor(new LoggingInterceptor());
        }

    }

    /**
     * Header拦截器
     */
    private class HeaderInterceptor implements Interceptor {
        private int version;
        private String token;

        public HeaderInterceptor(final int version, final String token) {
            this.version = version;
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request.Builder builder = chain.request().newBuilder();

            // to add agent
            builder.addHeader("User-Agent", UA);
            //为解决 http://blog.csdn.net/zhangteng22/article/details/52233126 问题
            builder.addHeader("Connection", "close");
            builder.addHeader("Accept", "application/vnd.trading.v" + version + "+json");
            if (null == token || "".equals(token) || "null".equals(token)) {
                // no token
            } else {
                builder.addHeader("Authorization", "Bearer {" + token + "}");
            }

            Response response = chain.proceed(builder.build());
            return response;
        }
    }

    /**
     * log拦截器
     */
    private class LoggingInterceptor implements Interceptor {
        private static final String TAG = "OkHttp";
        private final Charset UTF8 = Charset.forName("UTF-8");

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t_start = System.nanoTime();
            Log.v(TAG, String.format("request %s on %s %n %s body:%s",request.url(), chain.connection(), request.headers(), new Gson().toJson(request.body())));

            Response response = chain.proceed(request);

            long t_end = System.nanoTime();
            // 打印请求耗时
//            JLog.v(TAG, String.format("response for %s in %.1fms%n%s",
//                    response.request().url(), (t_end - t_start) / 1e6d, response.headers()));

            ResponseBody response_body = response.body();
            long content_length = response_body.contentLength();

            MediaType media_type = response.body().contentType();
            /**
             * 解决使用body.string()只能使用一次的问题
             */
//            if(MediaType.parse("application/json").equals(media_type)){ // 因为打印json，具有可读性
            BufferedSource source = response_body.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            if (media_type != null && charset != null) {
                try {
                    charset = media_type.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    e.printStackTrace();
                    return response;
                }
            }
            // content type 已经做了可读性分析，此处判断可省略
            if (!isPlaintext(buffer)) {
                Log.v(TAG, "response body: Body omitted.(Maybe is a file)");
                return response;
            }
            if (content_length != 0) {
                try {
                    // normal json
//                    JLog.v(TAG, String.format("response for %s in %.1fms%n%s",
//                            response.request().url(), (t_end - t_start) / 1e6d, response.headers())
//                            + "response body: " + buffer.clone().readString(charset));

//                     format json
                    Log.v(TAG, String.format("response for %s in %.1fms%n%s",
                            response.request().url(), (t_end - t_start) / 1e6d, response.headers())
                            + "response body: " + new JSONObject(buffer.clone().readString(charset)).toString(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return response;
        }

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        private boolean isPlaintext(Buffer buffer) {
            try {
                Buffer prefix = new Buffer();
                long byte_count = buffer.size() < 64 ? buffer.size() : 64;
                buffer.copyTo(prefix, 0, byte_count);
                for (int i = 0; i < 16; i++) {
                    if (prefix.exhausted()) {
                        break;
                    }
                    int code_point = prefix.readUtf8CodePoint();
                    if (Character.isISOControl(code_point) && !Character.isWhitespace(code_point)) {
                        return false;
                    }
                }
                return true;
            } catch (EOFException e) {
                return false;
            }
        }
    }

}
