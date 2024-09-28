package io.openinstall.sdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

public abstract class m extends a {
    private Runnable a = null;
    private final Handler b = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public volatile boolean c = true;
    /* access modifiers changed from: private */
    public volatile boolean d = false;

    protected m() {
    }

    public abstract void a();

    public abstract void b();

    public void onActivityPaused(Activity activity) {
        super.onActivityPaused(activity);
        this.d = true;
        Runnable runnable = this.a;
        if (runnable != null) {
            this.b.removeCallbacks(runnable);
        }
        n nVar = new n(this);
        this.a = nVar;
        this.b.postDelayed(nVar, 500);
    }

    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        boolean z = !this.c;
        this.c = true;
        this.d = false;
        Runnable runnable = this.a;
        if (runnable != null) {
            this.b.removeCallbacks(runnable);
            this.a = null;
        }
        if (z) {
            a();
        }
    }
}
