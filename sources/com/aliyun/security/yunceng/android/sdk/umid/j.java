package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import com.aliyun.security.yunceng.android.sdk.YunCeng;
import com.aliyun.security.yunceng.android.sdk.YunCengUtil;

public class j {
    public int a = 0;
    public int b = 0;
    public int c = 0;
    public int d = 0;
    public int e = 0;
    public int f = 0;
    private Context g = null;
    private YunCengUtil h = null;

    public j(Context _ct) {
        this.g = _ct;
        this.h = new YunCengUtil();
    }

    public void a() {
        if (this.h.d()) {
            this.a = new CheckEmulator().a();
        }
        if (this.h.g() && new CheckHook().a()) {
            YunCeng.reportInfo(17, "hook", "find_hook", 0);
            this.c = 1;
        }
        if (this.h.h()) {
            new CheckSign().a(this.g);
        }
        if (this.h.i() && new c().a()) {
            YunCeng.reportInfo(19, "root", "find_root", 0);
            this.b = 1;
        }
        if (this.h.j()) {
            d cv = new d(this.g);
            if (cv.b()) {
                int resean = cv.a();
                this.e = 1;
                this.f = resean;
            }
        }
        if (this.h.k() && new CheckDebug().a()) {
            this.d = 1;
        }
        if (this.h.l()) {
            String base_station = new b(this.g).a();
            if (base_station.length() > 10) {
                YunCeng.reportInfo(23, "base_station", base_station, 0);
            }
        }
        if (this.h.m()) {
            String labels = new a(this.g).a();
            if (labels.length() != 0) {
                YunCeng.reportInfo(24, "app_labels", labels, 0);
            }
        }
    }
}
