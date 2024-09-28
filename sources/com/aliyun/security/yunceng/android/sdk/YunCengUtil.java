package com.aliyun.security.yunceng.android.sdk;

import android.content.Context;
import com.king.zxing.util.LogUtils;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class YunCengUtil {
    final int a;
    final int b;
    final int c;
    final int d;
    final int e;
    final int f;
    final int g;
    final int h;
    final int i;
    final int j;
    final int k;
    final int l;
    final int m;
    private Context n;

    private native int GetNDPercentRaw();

    private native int GetNDTargetNumRaw();

    private native int GetOstepRsPort(String str, int i2);

    private native boolean tian_ta_le(int i2);

    public native void SetAppNameRaw(String str);

    public native void SetCachePath(String str);

    public YunCengUtil() {
        this.n = null;
        this.a = 1;
        this.b = 2;
        this.c = 3;
        this.d = 4;
        this.e = 5;
        this.f = 6;
        this.g = 7;
        this.h = 8;
        this.i = 9;
        this.j = 10;
        this.k = 11;
        this.l = 12;
        this.m = 13;
        this.n = a.a();
    }

    public boolean a() {
        return tian_ta_le(1);
    }

    public boolean b() {
        return tian_ta_le(2);
    }

    public boolean c() {
        return tian_ta_le(3);
    }

    public boolean d() {
        return tian_ta_le(4);
    }

    public boolean e() {
        return tian_ta_le(5);
    }

    public boolean f() {
        return tian_ta_le(6);
    }

    public boolean g() {
        return tian_ta_le(7);
    }

    public boolean h() {
        return tian_ta_le(8);
    }

    public boolean i() {
        return tian_ta_le(9);
    }

    public boolean j() {
        return tian_ta_le(10);
    }

    public boolean k() {
        return tian_ta_le(11);
    }

    public boolean l() {
        return tian_ta_le(12);
    }

    public boolean m() {
        return tian_ta_le(13);
    }

    public Context n() {
        return this.n;
    }

    public void o() {
        File cache_path;
        Context context = this.n;
        if (context != null && (cache_path = context.getCacheDir()) != null) {
            SetCachePath(cache_path.toString());
        }
    }

    public void p() {
        String appname;
        String pkgname;
        Context context = this.n;
        if (context != null) {
            try {
                appname = context.getApplicationInfo().loadLabel(this.n.getPackageManager()).toString().replace(' ', '-').replace('#', '-');
                pkgname = this.n.getPackageName();
            } catch (Exception e2) {
                appname = "DefaultAppName";
                pkgname = "DefaultPkgName";
            }
            SetAppNameRaw(appname + "##" + pkgname);
        }
    }

    public boolean a(String str) {
        for (int i2 = 0; i2 < str.length(); i2++) {
            if (!Character.isDigit(str.charAt(i2))) {
                return false;
            }
        }
        return true;
    }

    public int q() {
        return GetNDTargetNumRaw();
    }

    public int r() {
        return GetNDPercentRaw();
    }

    private URL a(URL url, String host, int port) {
        try {
            URI uri = url.toURI();
            return new URI(uri.getScheme(), host + LogUtils.COLON + port, uri.getPath(), uri.getQuery(), uri.getFragment()).toURL();
        } catch (Exception e2) {
            return url;
        }
    }

    public URL a(URL url) {
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = url.getDefaultPort();
        }
        int localport = GetOstepRsPort(host, port);
        if (localport != 0) {
            return a(url, "127.0.0.1", localport);
        }
        return url;
    }
}
