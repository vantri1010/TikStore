package io.openinstall.sdk;

class n implements Runnable {
    final /* synthetic */ m a;

    n(m mVar) {
        this.a = mVar;
    }

    public void run() {
        if (this.a.c && this.a.d) {
            boolean unused = this.a.c = false;
            this.a.b();
        }
    }
}
