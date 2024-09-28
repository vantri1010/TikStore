package io.openinstall.sdk;

import android.util.Pair;
import java.util.concurrent.LinkedBlockingQueue;

class br implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bo b;

    br(bo boVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = boVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        this.a.offer(Pair.create("aI", this.b.i().k()));
    }
}
