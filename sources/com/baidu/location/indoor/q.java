package com.baidu.location.indoor;

import com.baidu.location.BDLocation;
import com.baidu.location.indoor.p;

class q implements Runnable {
    final /* synthetic */ p a;

    q(p pVar) {
        this.a = pVar;
    }

    public void run() {
        p pVar = this.a;
        p.b a2 = pVar.a(pVar.e);
        if (!(a2 == null || this.a.a == null)) {
            p pVar2 = this.a;
            p.b unused = pVar2.e = pVar2.e.b(a2);
            long currentTimeMillis = System.currentTimeMillis();
            if (!a2.b(2.0E-6d) && currentTimeMillis - this.a.k > this.a.b) {
                BDLocation bDLocation = new BDLocation(this.a.c);
                bDLocation.setLatitude(this.a.e.a);
                bDLocation.setLongitude(this.a.e.b);
                this.a.a.a(bDLocation);
                long unused2 = this.a.k = currentTimeMillis;
            }
        }
        this.a.m.postDelayed(this.a.o, 450);
    }
}
