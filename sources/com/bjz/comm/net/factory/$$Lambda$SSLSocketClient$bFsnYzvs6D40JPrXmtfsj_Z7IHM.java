package com.bjz.comm.net.factory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/* renamed from: com.bjz.comm.net.factory.-$$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM implements HostnameVerifier {
    public static final /* synthetic */ $$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM INSTANCE = new $$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM();

    private /* synthetic */ $$Lambda$SSLSocketClient$bFsnYzvs6D40JPrXmtfsj_Z7IHM() {
    }

    public final boolean verify(String str, SSLSession sSLSession) {
        return SSLSocketClient.lambda$getHostnameVerifier$0(str, sSLSession);
    }
}
