package io.openinstall.sdk;

class be implements Runnable {
    final /* synthetic */ bf a;
    final /* synthetic */ bc b;

    be(bc bcVar, bf bfVar) {
        this.b = bcVar;
        this.a = bfVar;
    }

    public void run() {
        this.b.a(this.a);
    }
}
