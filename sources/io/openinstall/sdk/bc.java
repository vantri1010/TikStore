package io.openinstall.sdk;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Callable;

public abstract class bc extends bh implements Callable<bf> {
    private static final Handler a = new Handler(Looper.getMainLooper());

    public bc(h hVar) {
        super(hVar);
    }

    /* access modifiers changed from: protected */
    public void a() {
    }

    /* access modifiers changed from: protected */
    public void a(bf bfVar) {
    }

    public void b() {
        a();
        c();
    }

    /* access modifiers changed from: protected */
    public void b(bf bfVar) {
        a.post(new be(this, bfVar));
    }

    /* access modifiers changed from: protected */
    public void c() {
        e().execute(new bd(this));
    }
}
