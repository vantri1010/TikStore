package com.baidu.location.b;

import android.location.Location;

class e implements Runnable {
    final /* synthetic */ Location a;
    final /* synthetic */ d b;

    e(d dVar, Location location) {
        this.b = dVar;
        this.a = location;
    }

    public void run() {
        this.b.b(this.a);
    }
}
