package com.aliyun.security.yunceng.android.sdk.traceroute;

public class YCNetTraceRoute {
    static boolean a = true;
    private a b;

    public interface a {
        void a(String str);
    }

    public native String startJNICTraceRoute(String str);

    public YCNetTraceRoute(a listener) {
        this.b = listener;
    }

    public void a(String host) {
        this.b.a(", \"TraceRoute\":[");
        if (a) {
            if (host == "127.0.0.1") {
                try {
                    this.b.a("{\"1\":\"127.0.0.1\", \"Delay\":0}");
                } catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                }
            } else {
                this.b.a(startJNICTraceRoute(host));
            }
        }
        this.b.a("]");
    }

    static {
        try {
            System.loadLibrary("yunceng");
        } catch (Exception e) {
        }
    }
}
