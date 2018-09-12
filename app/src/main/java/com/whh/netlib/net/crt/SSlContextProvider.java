package com.whh.netlib.net.crt;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 提供证书获取认证类, 此类适格式为crt和cer的证书
 * 接受传值:<BR/>
 * 对外传值:<BR/>
 */
public class SSlContextProvider {
    private static final String TAG = SSlContextProvider.class.getSimpleName();

    private static SSlContextProvider instance;

    private SSLContext sslcontext;
    private TrustManagerFactory tmf;

    private SSlContextProvider() {

    }

    public static SSlContextProvider getInstance() {
        if (instance == null) {
            instance = new SSlContextProvider();
        }
        return instance;
    }

    /**
     * 此方法需要Application中做初始化，以后的网络请求中都不需要再次调用，直接使用已创建的实例
     *
     * @param context
     */
    public void initialize(Context context) {
        try {
            InputStream[] ins = new InputStream[]{context.getAssets().open("ssl/server_2017.cer"), context.getAssets().open("ssl/server_2018.cer")};
//            InputStream[] ins = new InputStream[]{context.getAssets().open("ssl/server_2017.cer")};
//            InputStream[] ins = new InputStream[]{context.getAssets().open("ssl/server_2018.cer")};
            holderSSL(ins);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 证书加载
     */
    private void holderSSL(InputStream[] ins) {
        try {
            // 信任指定证书
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
            for (int i = 0; i < ins.length; i++) {
                Certificate certificate = cf.generateCertificate(ins[i]);
                keystore.setCertificateEntry(Integer.toString(i), certificate);
                try {
                    if (ins[i] != null) {
                        ins[i].close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            // Create an SSLContext that uses our TrustManager
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SSLSocketFactory getSocketFactory() {
        if (sslcontext == null) {
            throw new NullPointerException("SSLContext null point exception! Please initialize first.");
        }
        return sslcontext.getSocketFactory();
    }

    public X509TrustManager getTrustManager() {
        if (tmf == null) {
            throw new NullPointerException("TrustManagerFactory null point exception! Please initialize first.");
        }
        TrustManager[] managers = tmf.getTrustManagers();
        if (managers.length != 1 || !(managers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(managers));
        }
        X509TrustManager manager = (X509TrustManager) managers[0];
        return manager;
    }
}
