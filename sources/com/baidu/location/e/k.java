package com.baidu.location.e;

import com.baidu.location.b.l;
import com.baidu.location.b.t;
import com.baidu.location.b.x;
import com.baidu.location.e.j;
import com.baidu.location.indoor.g;
import com.google.android.exoplayer2.DefaultRenderersFactory;

class k implements Runnable {
    final /* synthetic */ boolean a;
    final /* synthetic */ j.a b;

    k(j.a aVar, boolean z) {
        this.b = aVar;
        this.a = z;
    }

    public void run() {
        if (!j.this.j) {
            boolean unused = j.this.j = this.a;
        }
        j.this.s();
        l.c().i();
        if (g.a().e()) {
            g.a().a.obtainMessage(41).sendToTarget();
        }
        if (System.currentTimeMillis() - t.b() <= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            x.a().c();
        }
    }
}
