package io.openinstall.sdk;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import io.reactivex.annotations.SchedulerSupport;
import java.lang.ref.WeakReference;
import java.util.concurrent.DelayQueue;

public class an {
    private final boolean a = c.a().i().booleanValue();
    private ClipboardManager b;
    private final DelayQueue<am> c = new DelayQueue<>();
    private WeakReference<Activity> d = null;
    private int e = 0;

    public an(Context context) {
        try {
            this.b = (ClipboardManager) context.getSystemService("clipboard");
        } catch (Throwable th) {
        }
    }

    private ClipData f() {
        ClipDescription clipDescription;
        ClipData clipData = null;
        try {
            clipDescription = this.b.getPrimaryClipDescription();
        } catch (Throwable th) {
            clipDescription = null;
        }
        if (clipDescription == null) {
            return g();
        }
        boolean hasMimeType = clipDescription.hasMimeType("text/plain");
        if (Build.VERSION.SDK_INT >= 16) {
            hasMimeType |= clipDescription.hasMimeType("text/html");
        }
        if (!hasMimeType) {
            return ClipData.newPlainText(SchedulerSupport.CUSTOM, "don't match");
        }
        try {
            clipData = this.b.getPrimaryClip();
        } catch (Throwable th2) {
        }
        return clipData == null ? g() : clipData;
    }

    private ClipData g() {
        if (!c()) {
            return null;
        }
        int i = this.e + 1;
        this.e = i;
        if (i < 3) {
            return null;
        }
        this.e = 0;
        return ClipData.newPlainText(SchedulerSupport.CUSTOM, "app focus");
    }

    public void a() {
        if (this.a) {
            this.c.offer(am.a());
        }
    }

    public void a(WeakReference<Activity> weakReference) {
        this.d = weakReference;
    }

    public void b() {
        if (this.a) {
            this.c.offer(am.a());
            this.c.offer(am.b());
        }
    }

    public boolean c() {
        Activity activity;
        WeakReference<Activity> weakReference = this.d;
        if (weakReference == null || (activity = (Activity) weakReference.get()) == null) {
            return false;
        }
        return activity.hasWindowFocus();
    }

    public ClipData d() {
        if (this.b == null) {
            return null;
        }
        return f();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0047, code lost:
        r0 = r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.ClipData e() {
        /*
            r7 = this;
            android.content.ClipboardManager r0 = r7.b
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            boolean r0 = r7.a
            r2 = 1
            if (r0 == 0) goto L_0x0011
            android.content.ClipData r0 = r7.f()
            r3 = 2
            goto L_0x0013
        L_0x0011:
            r0 = r1
            r3 = 1
        L_0x0013:
            if (r0 != 0) goto L_0x004b
            java.util.concurrent.DelayQueue<io.openinstall.sdk.am> r0 = r7.c     // Catch:{ InterruptedException -> 0x0022 }
            r4 = 1000(0x3e8, double:4.94E-321)
            java.util.concurrent.TimeUnit r6 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ InterruptedException -> 0x0022 }
            java.util.concurrent.Delayed r0 = r0.poll(r4, r6)     // Catch:{ InterruptedException -> 0x0022 }
            io.openinstall.sdk.am r0 = (io.openinstall.sdk.am) r0     // Catch:{ InterruptedException -> 0x0022 }
            goto L_0x0024
        L_0x0022:
            r0 = move-exception
            r0 = r1
        L_0x0024:
            android.content.ClipData r4 = r7.f()
            int r3 = r3 + r2
            if (r0 == 0) goto L_0x0040
            boolean r0 = r0.c()
            if (r0 == 0) goto L_0x0040
            if (r4 != 0) goto L_0x0047
            boolean r0 = io.openinstall.sdk.cb.a
            if (r0 == 0) goto L_0x0047
            r0 = 0
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r1 = "疑似应用处于后台不可见状态下调用init，并且接着调用其它api，数据大概率丢失，请检查代码"
            io.openinstall.sdk.cb.b(r1, r0)
            goto L_0x0047
        L_0x0040:
            boolean r0 = r7.a
            if (r0 != 0) goto L_0x0049
            r0 = 3
            if (r3 < r0) goto L_0x0049
        L_0x0047:
            r0 = r4
            goto L_0x004b
        L_0x0049:
            r0 = r4
            goto L_0x0013
        L_0x004b:
            java.util.concurrent.DelayQueue<io.openinstall.sdk.am> r1 = r7.c
            r1.clear()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.an.e():android.content.ClipData");
    }
}
