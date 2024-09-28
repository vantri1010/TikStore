package com.bumptech.glide.request;

import com.bumptech.glide.request.RequestCoordinator;

public class ThumbnailRequestCoordinator implements RequestCoordinator, Request {
    private volatile Request full;
    private RequestCoordinator.RequestState fullState = RequestCoordinator.RequestState.CLEARED;
    private boolean isRunningDuringBegin;
    private final RequestCoordinator parent;
    private final Object requestLock;
    private volatile Request thumb;
    private RequestCoordinator.RequestState thumbState = RequestCoordinator.RequestState.CLEARED;

    public ThumbnailRequestCoordinator(Object requestLock2, RequestCoordinator parent2) {
        this.requestLock = requestLock2;
        this.parent = parent2;
    }

    public void setRequests(Request full2, Request thumb2) {
        this.full = full2;
        this.thumb = thumb2;
    }

    public boolean canSetImage(Request request) {
        boolean z;
        synchronized (this.requestLock) {
            z = parentCanSetImage() && (request.equals(this.full) || this.fullState != RequestCoordinator.RequestState.SUCCESS);
        }
        return z;
    }

    private boolean parentCanSetImage() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator == null || requestCoordinator.canSetImage(this);
    }

    public boolean canNotifyStatusChanged(Request request) {
        boolean z;
        synchronized (this.requestLock) {
            z = parentCanNotifyStatusChanged() && request.equals(this.full) && !isResourceSet();
        }
        return z;
    }

    public boolean canNotifyCleared(Request request) {
        boolean z;
        synchronized (this.requestLock) {
            z = parentCanNotifyCleared() && request.equals(this.full) && this.fullState != RequestCoordinator.RequestState.PAUSED;
        }
        return z;
    }

    private boolean parentCanNotifyCleared() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator == null || requestCoordinator.canNotifyCleared(this);
    }

    private boolean parentCanNotifyStatusChanged() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator == null || requestCoordinator.canNotifyStatusChanged(this);
    }

    public boolean isAnyResourceSet() {
        boolean z;
        synchronized (this.requestLock) {
            if (!parentIsAnyResourceSet()) {
                if (!isResourceSet()) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onRequestSuccess(com.bumptech.glide.request.Request r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.requestLock
            monitor-enter(r0)
            com.bumptech.glide.request.Request r1 = r2.thumb     // Catch:{ all -> 0x002d }
            boolean r1 = r3.equals(r1)     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x0011
            com.bumptech.glide.request.RequestCoordinator$RequestState r1 = com.bumptech.glide.request.RequestCoordinator.RequestState.SUCCESS     // Catch:{ all -> 0x002d }
            r2.thumbState = r1     // Catch:{ all -> 0x002d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return
        L_0x0011:
            com.bumptech.glide.request.RequestCoordinator$RequestState r1 = com.bumptech.glide.request.RequestCoordinator.RequestState.SUCCESS     // Catch:{ all -> 0x002d }
            r2.fullState = r1     // Catch:{ all -> 0x002d }
            com.bumptech.glide.request.RequestCoordinator r1 = r2.parent     // Catch:{ all -> 0x002d }
            if (r1 == 0) goto L_0x001e
            com.bumptech.glide.request.RequestCoordinator r1 = r2.parent     // Catch:{ all -> 0x002d }
            r1.onRequestSuccess(r2)     // Catch:{ all -> 0x002d }
        L_0x001e:
            com.bumptech.glide.request.RequestCoordinator$RequestState r1 = r2.thumbState     // Catch:{ all -> 0x002d }
            boolean r1 = r1.isComplete()     // Catch:{ all -> 0x002d }
            if (r1 != 0) goto L_0x002b
            com.bumptech.glide.request.Request r1 = r2.thumb     // Catch:{ all -> 0x002d }
            r1.clear()     // Catch:{ all -> 0x002d }
        L_0x002b:
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return
        L_0x002d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.ThumbnailRequestCoordinator.onRequestSuccess(com.bumptech.glide.request.Request):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onRequestFailed(com.bumptech.glide.request.Request r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.requestLock
            monitor-enter(r0)
            com.bumptech.glide.request.Request r1 = r2.full     // Catch:{ all -> 0x0020 }
            boolean r1 = r3.equals(r1)     // Catch:{ all -> 0x0020 }
            if (r1 != 0) goto L_0x0011
            com.bumptech.glide.request.RequestCoordinator$RequestState r1 = com.bumptech.glide.request.RequestCoordinator.RequestState.FAILED     // Catch:{ all -> 0x0020 }
            r2.thumbState = r1     // Catch:{ all -> 0x0020 }
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return
        L_0x0011:
            com.bumptech.glide.request.RequestCoordinator$RequestState r1 = com.bumptech.glide.request.RequestCoordinator.RequestState.FAILED     // Catch:{ all -> 0x0020 }
            r2.fullState = r1     // Catch:{ all -> 0x0020 }
            com.bumptech.glide.request.RequestCoordinator r1 = r2.parent     // Catch:{ all -> 0x0020 }
            if (r1 == 0) goto L_0x001e
            com.bumptech.glide.request.RequestCoordinator r1 = r2.parent     // Catch:{ all -> 0x0020 }
            r1.onRequestFailed(r2)     // Catch:{ all -> 0x0020 }
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return
        L_0x0020:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.ThumbnailRequestCoordinator.onRequestFailed(com.bumptech.glide.request.Request):void");
    }

    private boolean parentIsAnyResourceSet() {
        RequestCoordinator requestCoordinator = this.parent;
        return requestCoordinator != null && requestCoordinator.isAnyResourceSet();
    }

    public void begin() {
        synchronized (this.requestLock) {
            this.isRunningDuringBegin = true;
            try {
                if (!(this.fullState == RequestCoordinator.RequestState.SUCCESS || this.thumbState == RequestCoordinator.RequestState.RUNNING)) {
                    this.thumbState = RequestCoordinator.RequestState.RUNNING;
                    this.thumb.begin();
                }
                if (this.isRunningDuringBegin && this.fullState != RequestCoordinator.RequestState.RUNNING) {
                    this.fullState = RequestCoordinator.RequestState.RUNNING;
                    this.full.begin();
                }
            } finally {
                this.isRunningDuringBegin = false;
            }
        }
    }

    public void clear() {
        synchronized (this.requestLock) {
            this.isRunningDuringBegin = false;
            this.fullState = RequestCoordinator.RequestState.CLEARED;
            this.thumbState = RequestCoordinator.RequestState.CLEARED;
            this.thumb.clear();
            this.full.clear();
        }
    }

    public void pause() {
        synchronized (this.requestLock) {
            if (!this.thumbState.isComplete()) {
                this.thumbState = RequestCoordinator.RequestState.PAUSED;
                this.thumb.pause();
            }
            if (!this.fullState.isComplete()) {
                this.fullState = RequestCoordinator.RequestState.PAUSED;
                this.full.pause();
            }
        }
    }

    public boolean isRunning() {
        boolean z;
        synchronized (this.requestLock) {
            z = this.fullState == RequestCoordinator.RequestState.RUNNING;
        }
        return z;
    }

    public boolean isComplete() {
        boolean z;
        synchronized (this.requestLock) {
            z = this.fullState == RequestCoordinator.RequestState.SUCCESS;
        }
        return z;
    }

    private boolean isResourceSet() {
        boolean z;
        synchronized (this.requestLock) {
            if (this.fullState != RequestCoordinator.RequestState.SUCCESS) {
                if (this.thumbState != RequestCoordinator.RequestState.SUCCESS) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    public boolean isCleared() {
        boolean z;
        synchronized (this.requestLock) {
            z = this.fullState == RequestCoordinator.RequestState.CLEARED;
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x002e A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEquivalentTo(com.bumptech.glide.request.Request r5) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof com.bumptech.glide.request.ThumbnailRequestCoordinator
            r1 = 0
            if (r0 == 0) goto L_0x0032
            r0 = r5
            com.bumptech.glide.request.ThumbnailRequestCoordinator r0 = (com.bumptech.glide.request.ThumbnailRequestCoordinator) r0
            com.bumptech.glide.request.Request r2 = r4.full
            if (r2 != 0) goto L_0x0011
            com.bumptech.glide.request.Request r2 = r0.full
            if (r2 != 0) goto L_0x0030
            goto L_0x001b
        L_0x0011:
            com.bumptech.glide.request.Request r2 = r4.full
            com.bumptech.glide.request.Request r3 = r0.full
            boolean r2 = r2.isEquivalentTo(r3)
            if (r2 == 0) goto L_0x0030
        L_0x001b:
            com.bumptech.glide.request.Request r2 = r4.thumb
            if (r2 != 0) goto L_0x0024
            com.bumptech.glide.request.Request r2 = r0.thumb
            if (r2 != 0) goto L_0x0030
            goto L_0x002e
        L_0x0024:
            com.bumptech.glide.request.Request r2 = r4.thumb
            com.bumptech.glide.request.Request r3 = r0.thumb
            boolean r2 = r2.isEquivalentTo(r3)
            if (r2 == 0) goto L_0x0030
        L_0x002e:
            r1 = 1
            goto L_0x0031
        L_0x0030:
        L_0x0031:
            return r1
        L_0x0032:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.ThumbnailRequestCoordinator.isEquivalentTo(com.bumptech.glide.request.Request):boolean");
    }
}
