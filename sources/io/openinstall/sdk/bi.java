package io.openinstall.sdk;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

final class bi implements ThreadFactory {
    private final AtomicInteger a = new AtomicInteger(1);

    bi() {
    }

    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "ot#" + this.a);
    }
}
