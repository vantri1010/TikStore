package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestFutureTarget<R> implements FutureTarget<R>, RequestListener<R> {
    private static final Waiter DEFAULT_WAITER = new Waiter();
    private final boolean assertBackgroundThread;
    private GlideException exception;
    private final int height;
    private boolean isCancelled;
    private boolean loadFailed;
    private Request request;
    private R resource;
    private boolean resultReceived;
    private final Waiter waiter;
    private final int width;

    public RequestFutureTarget(int width2, int height2) {
        this(width2, height2, true, DEFAULT_WAITER);
    }

    RequestFutureTarget(int width2, int height2, boolean assertBackgroundThread2, Waiter waiter2) {
        this.width = width2;
        this.height = height2;
        this.assertBackgroundThread = assertBackgroundThread2;
        this.waiter = waiter2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
        if (r0 == null) goto L_0x0021;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        r0.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0021, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean cancel(boolean r4) {
        /*
            r3 = this;
            r0 = 0
            monitor-enter(r3)
            boolean r1 = r3.isDone()     // Catch:{ all -> 0x0022 }
            if (r1 == 0) goto L_0x000b
            r1 = 0
            monitor-exit(r3)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x000b:
            r1 = 1
            r3.isCancelled = r1     // Catch:{ all -> 0x0022 }
            com.bumptech.glide.request.RequestFutureTarget$Waiter r2 = r3.waiter     // Catch:{ all -> 0x0022 }
            r2.notifyAll(r3)     // Catch:{ all -> 0x0022 }
            if (r4 == 0) goto L_0x001b
            com.bumptech.glide.request.Request r2 = r3.request     // Catch:{ all -> 0x0022 }
            r0 = r2
            r2 = 0
            r3.request = r2     // Catch:{ all -> 0x0022 }
        L_0x001b:
            monitor-exit(r3)     // Catch:{ all -> 0x0022 }
            if (r0 == 0) goto L_0x0021
            r0.clear()
        L_0x0021:
            return r1
        L_0x0022:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.RequestFutureTarget.cancel(boolean):boolean");
    }

    public synchronized boolean isCancelled() {
        return this.isCancelled;
    }

    public synchronized boolean isDone() {
        return this.isCancelled || this.resultReceived || this.loadFailed;
    }

    public R get() throws InterruptedException, ExecutionException {
        try {
            return doGet((Long) null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }
    }

    public R get(long time, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(Long.valueOf(timeUnit.toMillis(time)));
    }

    public void getSize(SizeReadyCallback cb) {
        cb.onSizeReady(this.width, this.height);
    }

    public void removeCallback(SizeReadyCallback cb) {
    }

    public synchronized void setRequest(Request request2) {
        this.request = request2;
    }

    public synchronized Request getRequest() {
        return this.request;
    }

    public void onLoadCleared(Drawable placeholder) {
    }

    public void onLoadStarted(Drawable placeholder) {
    }

    public synchronized void onLoadFailed(Drawable errorDrawable) {
    }

    public synchronized void onResourceReady(R r, Transition<? super R> transition) {
    }

    private synchronized R doGet(Long timeoutMillis) throws ExecutionException, InterruptedException, TimeoutException {
        if (this.assertBackgroundThread && !isDone()) {
            Util.assertBackgroundThread();
        }
        if (this.isCancelled) {
            throw new CancellationException();
        } else if (this.loadFailed) {
            throw new ExecutionException(this.exception);
        } else if (this.resultReceived) {
            return this.resource;
        } else {
            if (timeoutMillis == null) {
                this.waiter.waitForTimeout(this, 0);
            } else if (timeoutMillis.longValue() > 0) {
                long now = System.currentTimeMillis();
                long deadline = timeoutMillis.longValue() + now;
                while (!isDone() && now < deadline) {
                    this.waiter.waitForTimeout(this, deadline - now);
                    now = System.currentTimeMillis();
                }
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            } else if (this.loadFailed) {
                throw new ExecutionException(this.exception);
            } else if (this.isCancelled) {
                throw new CancellationException();
            } else if (this.resultReceived) {
                return this.resource;
            } else {
                throw new TimeoutException();
            }
        }
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public synchronized boolean onLoadFailed(GlideException e, Object model, Target<R> target, boolean isFirstResource) {
        this.loadFailed = true;
        this.exception = e;
        this.waiter.notifyAll(this);
        return false;
    }

    public synchronized boolean onResourceReady(R resource2, Object model, Target<R> target, DataSource dataSource, boolean isFirstResource) {
        this.resultReceived = true;
        this.resource = resource2;
        this.waiter.notifyAll(this);
        return false;
    }

    static class Waiter {
        Waiter() {
        }

        /* access modifiers changed from: package-private */
        public void waitForTimeout(Object toWaitOn, long timeoutMillis) throws InterruptedException {
            toWaitOn.wait(timeoutMillis);
        }

        /* access modifiers changed from: package-private */
        public void notifyAll(Object toNotify) {
            toNotify.notifyAll();
        }
    }
}
