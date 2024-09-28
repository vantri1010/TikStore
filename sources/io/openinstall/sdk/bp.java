package io.openinstall.sdk;

import android.util.Pair;
import java.util.concurrent.LinkedBlockingQueue;

class bp implements Runnable {
    final /* synthetic */ LinkedBlockingQueue a;
    final /* synthetic */ bo b;

    bp(bo boVar, LinkedBlockingQueue linkedBlockingQueue) {
        this.b = boVar;
        this.a = linkedBlockingQueue;
    }

    public void run() {
        String str;
        LinkedBlockingQueue linkedBlockingQueue;
        String str2;
        LinkedBlockingQueue linkedBlockingQueue2;
        String a2;
        al a3 = this.b.a(this.b.m().b(true));
        if (!this.b.m().a() || a3 != null) {
            if (a3 == null || !a3.c(2)) {
                str = "pbT";
                if (a3 == null || !a3.c(1)) {
                    linkedBlockingQueue = this.a;
                    str2 = null;
                } else {
                    linkedBlockingQueue2 = this.a;
                    a2 = a3.a();
                }
            } else {
                linkedBlockingQueue2 = this.a;
                a2 = a3.b();
                str = "pbH";
            }
            linkedBlockingQueue2.offer(Pair.create(str, a2));
            this.b.m().a(false);
        }
        linkedBlockingQueue = this.a;
        str2 = String.valueOf(false);
        str = "pbR";
        linkedBlockingQueue.offer(Pair.create(str, str2));
        this.b.m().a(false);
    }
}
