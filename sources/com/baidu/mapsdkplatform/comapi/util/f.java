package com.baidu.mapsdkplatform.comapi.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import java.io.File;

public final class f {
    private final boolean a;
    private final String b;
    private final String c;
    private final String d;
    private final String e;
    private final String f;

    f(Context context) {
        String str;
        if (Build.VERSION.SDK_INT >= 29) {
            File externalFilesDir = context.getExternalFilesDir((String) null);
            str = ((externalFilesDir == null || !externalFilesDir.exists()) ? context.getFilesDir() : externalFilesDir).getPath();
        } else {
            str = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        this.b = str;
        this.a = false;
        this.c = this.b + File.separator + "BaiduMapSDKNew";
        this.d = context.getCacheDir().getAbsolutePath();
        this.e = "";
        this.f = "";
    }

    f(String str, boolean z, String str2, Context context) {
        this.a = z;
        this.b = str;
        this.c = this.b + File.separator + "BaiduMapSDKNew";
        this.d = this.c + File.separator + "cache";
        this.e = context.getCacheDir().getAbsolutePath();
        this.f = str2;
    }

    public String a() {
        return this.b;
    }

    public String b() {
        return this.b + File.separator + "BaiduMapSDKNew";
    }

    public String c() {
        return this.d;
    }

    public String d() {
        return this.e;
    }

    public boolean equals(Object obj) {
        if (obj == null || !f.class.isInstance(obj)) {
            return false;
        }
        return this.b.equals(((f) obj).b);
    }
}
