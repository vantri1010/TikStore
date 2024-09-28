package io.openinstall.sdk;

import android.util.Pair;
import java.util.concurrent.LinkedBlockingQueue;

class bn implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bm b;

    bn(bm bmVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = bmVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        String str;
        LinkedBlockingQueue linkedBlockingQueue;
        String a2;
        al b2 = this.b.m().b(true);
        if (b2 == null || !b2.c(2)) {
            str = "pbT";
            if (b2 == null || !b2.c(1)) {
                this.a.offer(Pair.create(str, (String) null));
                return;
            } else {
                linkedBlockingQueue = this.a;
                a2 = b2.a();
            }
        } else {
            linkedBlockingQueue = this.a;
            a2 = b2.b();
            str = "pbH";
        }
        linkedBlockingQueue.offer(Pair.create(str, a2));
    }
}
