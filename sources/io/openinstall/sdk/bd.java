package io.openinstall.sdk;

import io.openinstall.sdk.bf;

class bd implements Runnable {
    final /* synthetic */ bc a;

    bd(bc bcVar) {
        this.a = bcVar;
    }

    public void run() {
        bf bfVar;
        try {
            this.a.g().a();
            bfVar = (bf) this.a.call();
        } catch (Exception e) {
            bfVar = bf.a.REQUEST_FAIL.a(e.getMessage());
        }
        this.a.b(bfVar);
    }
}
