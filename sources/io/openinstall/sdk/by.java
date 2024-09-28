package io.openinstall.sdk;

import io.openinstall.sdk.bf;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class by implements Runnable {
    final /* synthetic */ bx a;

    by(bx bxVar) {
        this.a = bxVar;
    }

    public void run() {
        bf bfVar;
        System.currentTimeMillis();
        Future submit = this.a.d().submit(this.a);
        try {
            bfVar = (bf) submit.get((long) this.a.n(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            submit.cancel(true);
            bfVar = bf.a.REQUEST_TIMEOUT.a();
        } catch (Exception e2) {
            bfVar = bf.a.REQUEST_FAIL.a(e2.getMessage());
        }
        this.a.b(bfVar);
        System.currentTimeMillis();
    }
}
