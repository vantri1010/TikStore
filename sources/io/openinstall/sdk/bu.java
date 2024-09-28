package io.openinstall.sdk;

import android.util.Pair;
import java.util.concurrent.LinkedBlockingQueue;

class bu implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bo b;

    bu(bo boVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = boVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        this.b.b.a(this.b.a);
        this.a.offer(Pair.create("oa", this.b.b.a()));
    }
}
