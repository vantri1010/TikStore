package io.openinstall.sdk;

import java.util.concurrent.TimeUnit;

class r implements Runnable {
    final /* synthetic */ q a;

    r(q qVar) {
        this.a = qVar;
    }

    public void run() {
        int i = 100;
        while (this.a.b) {
            try {
                this.a.a.poll((long) i, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
            this.a.f.a();
            i = Math.min(i * 10, 10000);
        }
    }
}
