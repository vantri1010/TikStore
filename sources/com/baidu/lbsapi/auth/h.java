package com.baidu.lbsapi.auth;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

class h implements HostnameVerifier {
    final /* synthetic */ g a;

    h(g gVar) {
        this.a = gVar;
    }

    public boolean verify(String str, SSLSession sSLSession) {
        if ("api.map.baidu.com".equals(str)) {
            return true;
        }
        return HttpsURLConnection.getDefaultHostnameVerifier().verify(str, sSLSession);
    }
}
