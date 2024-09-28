package io.openinstall.sdk;

import android.text.TextUtils;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class bh {
    private static final ThreadFactory a = new bi();
    private static final RejectedExecutionHandler b = new bj();
    private static final ThreadPoolExecutor c = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(30), a, b);
    private static final ThreadPoolExecutor d = new ThreadPoolExecutor(3, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(30), a, b);
    private final String e = c.a().c();
    private final d f;
    private final f g;
    private final j h;
    private final g i;
    private final z j;
    private final av k;
    private final ai l;

    public bh(h hVar) {
        this.f = hVar.c();
        this.g = hVar.b();
        this.h = hVar.d();
        this.i = hVar.e();
        this.j = hVar.g();
        this.k = hVar.a();
        this.l = hVar.f();
    }

    /* access modifiers changed from: protected */
    public void a(bb bbVar) {
        if (bbVar instanceof ay) {
            String d2 = ((ay) bbVar).d();
            if (!TextUtils.isEmpty(d2)) {
                g b2 = g.b(d2);
                if (!this.i.equals(b2)) {
                    this.i.a(b2);
                    this.g.a(this.i);
                }
                if (!TextUtils.isEmpty(this.i.h())) {
                    this.j.b(this.e, this.i.h());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public ThreadPoolExecutor d() {
        return d;
    }

    /* access modifiers changed from: protected */
    public ThreadPoolExecutor e() {
        return c;
    }

    /* access modifiers changed from: protected */
    public String f() {
        return this.e;
    }

    /* access modifiers changed from: protected */
    public d g() {
        return this.f;
    }

    /* access modifiers changed from: protected */
    public f h() {
        return this.g;
    }

    /* access modifiers changed from: protected */
    public j i() {
        return this.h;
    }

    /* access modifiers changed from: protected */
    public g j() {
        return this.i;
    }

    /* access modifiers changed from: protected */
    public z k() {
        return this.j;
    }

    /* access modifiers changed from: protected */
    public av l() {
        return this.k;
    }

    /* access modifiers changed from: protected */
    public ai m() {
        return this.l;
    }
}
