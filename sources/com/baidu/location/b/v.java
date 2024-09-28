package com.baidu.location.b;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class v {
    private ExecutorService a;
    private ExecutorService b;

    private static class a {
        /* access modifiers changed from: private */
        public static v a = new v();
    }

    private v() {
    }

    public static v a() {
        return a.a;
    }

    public synchronized ExecutorService b() {
        if (this.a == null || this.a.isShutdown()) {
            this.a = null;
            this.a = Executors.newSingleThreadExecutor();
        }
        return this.a;
    }

    public synchronized ExecutorService c() {
        if (this.b == null || this.b.isShutdown()) {
            this.b = null;
            this.b = Executors.newFixedThreadPool(2);
        }
        return this.b;
    }

    public void d() {
        ExecutorService executorService = this.a;
        if (executorService != null) {
            executorService.shutdown();
        }
        ExecutorService executorService2 = this.b;
        if (executorService2 != null) {
            executorService2.shutdown();
        }
    }
}
