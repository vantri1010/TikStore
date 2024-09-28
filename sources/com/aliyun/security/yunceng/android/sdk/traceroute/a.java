package com.aliyun.security.yunceng.android.sdk.traceroute;

import android.content.Context;
import com.aliyun.security.yunceng.android.sdk.traceroute.YCNetTraceRoute;
import com.aliyun.security.yunceng.android.sdk.traceroute.b;
import com.aliyun.security.yunceng.android.sdk.traceroute.c;
import java.net.InetAddress;

public class a implements YCNetTraceRoute.a, b.C0003b, c.a {
    private String a;
    private String b;
    private YCNetDiagnosisListener c;
    private Context d;
    private InetAddress e;
    private final StringBuilder f = new StringBuilder(1024);

    public a() {
    }

    public a(Context context, String theDormain, String thePort, YCNetDiagnosisListener theListener) {
        this.d = context;
        this.a = theDormain;
        this.b = thePort;
        this.c = theListener;
    }

    public String a() {
        if (this.a == "") {
            return "";
        }
        d("{ ");
        b();
        if (d.b(this.d).booleanValue()) {
            new c(this.e, this).a(this.a, this.b);
            new b(11, this).a(this.a, false);
            new YCNetTraceRoute(this).a(this.a);
        }
        d("}");
        YCNetDiagnosisListener yCNetDiagnosisListener = this.c;
        if (yCNetDiagnosisListener != null) {
            yCNetDiagnosisListener.OnNetDiagnosisFinished(this.f.toString());
        }
        return this.f.toString();
    }

    private void d(String stepInfo) {
        this.f.append(stepInfo);
    }

    public void a(String log) {
        d(log);
    }

    public void b(String log) {
        d(log);
    }

    public void c(String log) {
        d(log);
    }

    private void b() {
        String netType = d.a(this.d);
        d("\"NetType\":\"" + netType + "\"");
        if (d.b(this.d).booleanValue()) {
            if (d.c.equals(netType)) {
                d(", \"LocalIP\":\"" + d.d(this.d) + "\"");
                d(", \"Gateway\":\"" + d.e(this.d) + "\"");
            } else {
                d(", \"LocalIP\":\"" + d.a() + "\"");
                d(", \"Gateway\":\"127.0.0.1\"");
            }
            d(", \"DnsServers\":[{\"1\":\"" + d.a("dns1") + "\"}, {\"2\":\"" + d.a("dns2") + "\"}]");
            StringBuilder sb = new StringBuilder();
            sb.append(", \"Domain\":\"");
            sb.append(this.a);
            sb.append("\"");
            d(sb.toString());
            e(this.a);
            d(", \"RemotePort\":\"" + this.b + "\"");
        }
    }

    private void e(String _dormain) {
        InetAddress b2 = d.b(_dormain);
        this.e = b2;
        if (b2 != null) {
            d(", \"RemoteIP\":\"" + this.e.getHostAddress() + "\"");
            return;
        }
        d(", \"RemoteIP\":\"0.0.0.0\"");
    }
}
