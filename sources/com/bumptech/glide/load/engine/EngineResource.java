package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;

class EngineResource<Z> implements Resource<Z> {
    private int acquired;
    private final boolean isMemoryCacheable;
    private final boolean isRecyclable;
    private boolean isRecycled;
    private final Key key;
    private final ResourceListener listener;
    private final Resource<Z> resource;

    interface ResourceListener {
        void onResourceReleased(Key key, EngineResource<?> engineResource);
    }

    EngineResource(Resource<Z> toWrap, boolean isMemoryCacheable2, boolean isRecyclable2, Key key2, ResourceListener listener2) {
        this.resource = (Resource) Preconditions.checkNotNull(toWrap);
        this.isMemoryCacheable = isMemoryCacheable2;
        this.isRecyclable = isRecyclable2;
        this.key = key2;
        this.listener = (ResourceListener) Preconditions.checkNotNull(listener2);
    }

    /* access modifiers changed from: package-private */
    public Resource<Z> getResource() {
        return this.resource;
    }

    /* access modifiers changed from: package-private */
    public boolean isMemoryCacheable() {
        return this.isMemoryCacheable;
    }

    public Class<Z> getResourceClass() {
        return this.resource.getResourceClass();
    }

    public Z get() {
        return this.resource.get();
    }

    public int getSize() {
        return this.resource.getSize();
    }

    public synchronized void recycle() {
        if (this.acquired > 0) {
            throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
        } else if (!this.isRecycled) {
            this.isRecycled = true;
            if (this.isRecyclable) {
                this.resource.recycle();
            }
        } else {
            throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void acquire() {
        if (!this.isRecycled) {
            this.acquired++;
        } else {
            throw new IllegalStateException("Cannot acquire a recycled resource");
        }
    }

    /* access modifiers changed from: package-private */
    public void release() {
        boolean release = false;
        synchronized (this) {
            if (this.acquired > 0) {
                int i = this.acquired - 1;
                this.acquired = i;
                if (i == 0) {
                    release = true;
                }
            } else {
                throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
            }
        }
        if (release) {
            this.listener.onResourceReleased(this.key, this);
        }
    }

    public synchronized String toString() {
        return "EngineResource{isMemoryCacheable=" + this.isMemoryCacheable + ", listener=" + this.listener + ", key=" + this.key + ", acquired=" + this.acquired + ", isRecycled=" + this.isRecycled + ", resource=" + this.resource + '}';
    }
}
