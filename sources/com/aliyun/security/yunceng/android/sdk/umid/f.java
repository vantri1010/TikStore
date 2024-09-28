package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import com.stripe.android.model.Card;

class f {
    private Build a = new Build();
    private DisplayMetrics b;

    f(Context ct) {
        this.b = ct.getResources().getDisplayMetrics();
    }

    public String a() {
        return this.a != null ? Build.MODEL.replace('#', '-') : Card.UNKNOWN;
    }

    public String b() {
        return this.a != null ? Build.PRODUCT.replace('#', '-') : Card.UNKNOWN;
    }

    public String c() {
        return this.a != null ? Build.BOARD.replace('#', '-') : Card.UNKNOWN;
    }

    public String d() {
        if (this.a == null) {
            return Card.UNKNOWN;
        }
        return Build.CPU_ABI + Build.CPU_ABI2;
    }

    public String e() {
        return this.a != null ? Build.HOST.replace('#', '-') : Card.UNKNOWN;
    }

    public String f() {
        return this.a != null ? Build.ID : Card.UNKNOWN;
    }

    public String g() {
        return this.a != null ? Build.VERSION.RELEASE : Card.UNKNOWN;
    }

    public String h() {
        if (this.b == null) {
            return "0*0";
        }
        return this.b.heightPixels + "*" + this.b.widthPixels;
    }
}
