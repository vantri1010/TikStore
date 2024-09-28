package io.openinstall.sdk;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

final class bj implements RejectedExecutionHandler {
    bj() {
    }

    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
        if (cb.a) {
            cb.b("Task " + runnable.toString() + " rejected from " + threadPoolExecutor.toString(), new Object[0]);
        }
    }
}
