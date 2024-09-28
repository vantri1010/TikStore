package im.bclpbkiauv.ui.components.banner.util;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class BannerLifecycleObserverAdapter implements LifecycleObserver {
    private final LifecycleOwner mLifecycleOwner;
    private final BannerLifecycleObserver mObserver;

    public BannerLifecycleObserverAdapter(LifecycleOwner lifecycleOwner, BannerLifecycleObserver observer) {
        this.mLifecycleOwner = lifecycleOwner;
        this.mObserver = observer;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mObserver.onStart(this.mLifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mObserver.onStop(this.mLifecycleOwner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mObserver.onDestroy(this.mLifecycleOwner);
    }
}
