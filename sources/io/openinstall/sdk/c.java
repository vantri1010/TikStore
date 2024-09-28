package io.openinstall.sdk;

import android.content.ClipData;
import android.content.Context;
import com.fm.openinstall.Configuration;

public class c {
    private static final c a = new c();
    private Context b;
    private String c;
    private String d;
    private Configuration e;
    private Boolean f;
    private ClipData g;
    private Boolean h;
    private Boolean i;
    private Runnable j;

    private c() {
    }

    public static c a() {
        return a;
    }

    public void a(ClipData clipData) {
        this.g = clipData;
    }

    public void a(Context context) {
        this.b = context.getApplicationContext();
    }

    public void a(Configuration configuration) {
        this.e = configuration;
    }

    public void a(Boolean bool) {
        this.f = bool;
    }

    public void a(Runnable runnable) {
        this.j = runnable;
    }

    public void a(String str) {
        this.c = str;
    }

    public Context b() {
        return this.b;
    }

    public void b(Boolean bool) {
        this.h = bool;
    }

    public void b(String str) {
        this.d = str;
    }

    public String c() {
        return this.c;
    }

    public String d() {
        return this.d;
    }

    public Configuration e() {
        if (this.e == null) {
            this.e = Configuration.getDefault();
        }
        return this.e;
    }

    public Boolean f() {
        if (this.f == null) {
            this.f = Boolean.valueOf(cc.b(this.b));
        }
        return this.f;
    }

    public ClipData g() {
        return this.g;
    }

    public Boolean h() {
        if (this.h == null) {
            this.h = true;
        }
        return this.h;
    }

    public Boolean i() {
        if (this.i == null) {
            this.i = Boolean.valueOf(cc.c(this.b));
        }
        return this.i;
    }

    public Runnable j() {
        return this.j;
    }
}
