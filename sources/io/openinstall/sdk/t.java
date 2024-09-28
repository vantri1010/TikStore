package io.openinstall.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

public class t extends Handler {
    protected d a;
    protected g b;
    protected String c;
    protected av d;
    protected f e;
    protected z f;
    private final p g;
    private long h;
    private int i = 0;

    public t(Context context, Looper looper, h hVar) {
        super(looper);
        this.a = hVar.c();
        this.b = hVar.e();
        this.c = c.a().c();
        this.d = hVar.a();
        this.e = hVar.b();
        this.f = hVar.g();
        this.g = new p(context, this.c);
        this.h = this.e.b("FM_last_time");
    }

    private void a(boolean z) {
        if (z || b(false)) {
            d();
        }
    }

    private void b() {
        this.i = 0;
    }

    private boolean b(l lVar) {
        if (lVar.b() == 2 && !this.b.f()) {
            if (cb.a) {
                cb.b("eventStatsEnabled is false", new Object[0]);
            }
            return false;
        } else if (lVar.b() == 1 && !this.b.f()) {
            if (cb.a) {
                cb.b("eventStatsEnabled is false", new Object[0]);
            }
            return false;
        } else if (lVar.b() != 0 || this.b.d()) {
            return true;
        } else {
            if (cb.a) {
                cb.b("registerStatsEnabled is false", new Object[0]);
            }
            return false;
        }
    }

    private boolean b(boolean z) {
        if (!this.a.b() || this.b.g() == null) {
            if (!z) {
                this.a.a();
            }
            return false;
        }
        if (z) {
            if (!this.b.f() && !this.b.d()) {
                this.g.d();
                return false;
            } else if (this.g.a()) {
                return false;
            }
        }
        if (this.g.b()) {
            return true;
        }
        return this.b.g().longValue() * 1000 < System.currentTimeMillis() - this.h;
    }

    private void c() {
        int i2 = this.i;
        if (i2 < 10) {
            this.i = i2 + 1;
        }
    }

    private void c(l lVar) {
        boolean c2;
        if (!b(lVar)) {
            c2 = false;
        } else {
            this.g.a(lVar);
            c2 = lVar.c();
        }
        a(c2);
    }

    private void d() {
        if (!this.a.b()) {
            this.a.a();
            return;
        }
        bb a2 = this.d.a(this.g.e());
        a(a2);
        this.h = System.currentTimeMillis();
        if (a2 instanceof ay) {
            if (((ay) a2).a() == 0) {
                if (cb.a) {
                    cb.a("statEvents success", new Object[0]);
                }
                b();
                this.g.c();
            }
            this.e.a("FM_last_time", this.h);
            return;
        }
        if (cb.a) {
            cb.c("statEvents fail : %s", a2.f());
        }
        c();
    }

    public void a() {
        Message obtain = Message.obtain();
        obtain.what = 23;
        obtain.obj = null;
        sendMessage(obtain);
    }

    /* access modifiers changed from: protected */
    public void a(bb bbVar) {
        if (bbVar instanceof ay) {
            String d2 = ((ay) bbVar).d();
            if (!TextUtils.isEmpty(d2)) {
                g b2 = g.b(d2);
                if (!this.b.equals(b2)) {
                    this.b.a(b2);
                    this.e.a(this.b);
                }
                if (!TextUtils.isEmpty(this.b.h())) {
                    this.f.b(this.c, this.b.h());
                }
            }
        }
    }

    public void a(l lVar) {
        Message obtain = Message.obtain();
        obtain.what = 21;
        obtain.obj = lVar;
        sendMessage(obtain);
    }

    public void handleMessage(Message message) {
        if (message.what == 21) {
            c((l) message.obj);
        } else if (message.what == 23 && this.i < 10 && b(true)) {
            d();
        }
    }
}
