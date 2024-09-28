package io.openinstall.sdk;

import android.content.Context;
import io.openinstall.sdk.i;

public class h {
    private final av a;
    private final f b;
    private final d c = new d();
    private final j d;
    private final ai e;
    private final g f = new g();
    private final z g;

    public h() {
        Context b2 = c.a().b();
        this.b = new f(new i().a(b2.getApplicationContext(), "FM_config", (i.b) null));
        this.a = av.a(this);
        this.d = j.a(b2.getApplicationContext(), this.b);
        this.e = ai.a(b2.getApplicationContext());
        this.g = z.a(b2.getApplicationContext());
    }

    public av a() {
        return this.a;
    }

    public f b() {
        return this.b;
    }

    public d c() {
        return this.c;
    }

    public j d() {
        return this.d;
    }

    public g e() {
        return this.f;
    }

    public ai f() {
        return this.e;
    }

    public z g() {
        return this.g;
    }
}
