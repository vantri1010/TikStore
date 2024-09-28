package io.openinstall.sdk;

import android.os.SystemClock;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class am implements Delayed {
    private final long a;
    private final boolean b;

    private am(long j, boolean z) {
        this.a = SystemClock.elapsedRealtime() + j;
        this.b = z;
    }

    public static am a() {
        return new am(0, false);
    }

    public static am b() {
        return new am(800, true);
    }

    public int a(am amVar) {
        int i = (this.a > amVar.a ? 1 : (this.a == amVar.a ? 0 : -1));
        if (i < 0) {
            return -1;
        }
        return i > 0 ? 1 : 0;
    }

    /* renamed from: a */
    public int compareTo(Delayed delayed) {
        return a((am) delayed);
    }

    public boolean c() {
        return this.b;
    }

    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(this.a - SystemClock.elapsedRealtime(), TimeUnit.MILLISECONDS);
    }
}
