package io.openinstall.sdk;

import android.os.SystemClock;
import java.util.concurrent.TimeUnit;

class s extends m {
    long a = SystemClock.uptimeMillis();
    final /* synthetic */ q b;

    s(q qVar) {
        this.b = qVar;
    }

    public void a() {
        this.a = SystemClock.uptimeMillis();
    }

    public void b() {
        this.b.a(TimeUnit.MILLISECONDS.toSeconds(SystemClock.uptimeMillis() - this.a));
    }
}
