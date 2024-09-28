package io.openinstall.sdk;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class d {
    private volatile e a = null;
    private final CountDownLatch b = new CountDownLatch(1);
    private final LinkedBlockingQueue<Object> c = new LinkedBlockingQueue<>(1);
    private final Object d = new Object();

    public Object a(long j) throws InterruptedException {
        return this.c.poll(j, TimeUnit.SECONDS);
    }

    public void a() {
        if (this.a == null || this.a == e.a || this.a == e.b) {
            this.c.offer(this.d);
        }
    }

    public synchronized void a(e eVar) {
        this.a = eVar;
    }

    public void a(String str, long j) {
        if (this.a == null || this.a == e.a || this.a == e.b) {
            this.c.offer(this.d);
            try {
                this.b.await(j, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                if (cb.a) {
                    cb.b("%s awaitInit interrupted", str);
                }
            }
        }
    }

    public boolean b() {
        return this.a == e.d;
    }

    public boolean c() {
        return this.a == e.e || this.a == e.d;
    }

    public synchronized e d() {
        return this.a;
    }

    public void e() {
        this.b.countDown();
    }
}
