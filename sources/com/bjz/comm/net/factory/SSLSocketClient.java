package com.bjz.comm.net.factory;

import com.android.tools.r8.annotations.SynthesizedClassMap;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@SynthesizedClassMap({$$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM.class})
public class SSLSocketClient {
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init((KeyManager[]) null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }

    public static HostnameVerifier getHostnameVerifier() {
        return $$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM.INSTANCE;
    }

    static /* synthetic */ boolean lambda$getHostnameVerifier$0(String s, SSLSession sslSession) {
        return true;
    }
}
