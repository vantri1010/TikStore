package com.baidu.location.indoor;

import com.baidu.location.BDLocation;

class r implements Runnable {
    final /* synthetic */ p a;

    r(p pVar) {
        this.a = pVar;
    }

    public void run() {
        if (!(this.a.j == null || this.a.a == null)) {
            BDLocation bDLocation = new BDLocation(this.a.c);
            bDLocation.setLatitude(this.a.j.getLatitude());
            bDLocation.setLongitude(this.a.j.getLongitude());
            this.a.a.a(bDLocation);
        }
        this.a.m.postDelayed(this.a.o, this.a.b);
    }
}
