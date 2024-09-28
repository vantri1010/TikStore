package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import com.aliyun.security.yunceng.android.sdk.YunCengUtil;

public class UMID {
    private h a = null;
    private f b = null;
    private e c = null;
    private g d = null;
    private PhoneMisc e = null;
    private i f = null;
    private j g = null;

    private native void SetSessionPrefix(String str);

    private native String getNativeUUID();

    private native void setRuntimeUMID(int i, int i2, int i3, int i4, int i5);

    private native void setStableUMID(String str);

    public UMID(Context ct) {
        this.a = new h(ct);
        this.b = new f(ct);
        this.c = new e(ct);
        this.e = new PhoneMisc(ct);
        this.f = new i(ct);
        this.d = new g(ct);
        this.g = new j(ct);
    }

    static {
        System.loadLibrary("yunceng");
    }

    public void a() {
        setStableUMID(String.format("%s#%s", new Object[]{this.e.e(), this.a.b()}));
    }

    private void c() {
        SetSessionPrefix(String.format("%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%d#%d#%d#%d#%d", new Object[]{this.a.a(), this.a.b(), this.a.d(), this.f.c(), this.b.c(), this.b.e(), this.b.a(), this.b.b(), this.b.d(), this.f.a(), this.f.b(), Integer.valueOf(this.g.a), Integer.valueOf(this.g.b), Integer.valueOf(this.g.c), Integer.valueOf(this.g.d), Integer.valueOf(this.g.f)}));
    }

    private void d() {
        int simulator = 0;
        if (this.g.a != 10) {
            simulator = 1;
        }
        setRuntimeUMID(simulator, this.g.b, this.g.c, this.g.d, this.g.e);
    }

    public String b() {
        new YunCengUtil();
        this.g.a();
        c();
        d();
        return String.format("A#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%d#%d#%d#%d", new Object[]{this.b.a(), getNativeUUID(), this.b.d(), this.a.b(), this.e.b(), this.a.d(), this.a.f(), this.b.f(), this.b.g(), this.c.b(), this.b.h(), this.d.b(), this.e.d(), this.e.c(), this.a.c(), this.a.g(), this.a.i(), this.b.b(), this.b.c(), this.d.c(), this.e.a(), this.e.e(), this.f.d(), this.f.a(), this.f.c(), this.f.b(), Integer.valueOf(this.g.a), Integer.valueOf(this.g.b), Integer.valueOf(this.g.c), Integer.valueOf(this.g.d), Integer.valueOf(this.g.f)});
    }
}
