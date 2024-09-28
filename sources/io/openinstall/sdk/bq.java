package io.openinstall.sdk;

import android.util.Pair;
import java.util.concurrent.LinkedBlockingQueue;

class bq implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bo b;

    bq(bo boVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = boVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        this.b.c.a(this.b.a);
        this.a.offer(Pair.create("gR", this.b.c.a()));
    }
}
