package io.openinstall.sdk;

import android.util.Pair;
import io.openinstall.sdk.af;
import java.util.concurrent.LinkedBlockingQueue;

class bt implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bo b;

    bt(bo boVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = boVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        af.a a2 = af.a(this.b.a);
        this.a.offer(Pair.create("ga", (a2 == null || a2.b()) ? null : a2.a()));
    }
}
