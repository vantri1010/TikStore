package com.baidu.mapsdkplatform.comjni.engine;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import java.util.List;

public class a {
    private static final String a = a.class.getSimpleName();
    private static SparseArray<List<Handler>> b = new SparseArray<>();

    public static void a(int i, int i2, int i3, long j) {
        synchronized (b) {
            List<Handler> list = b.get(i);
            if (list != null && !list.isEmpty()) {
                for (Handler obtain : list) {
                    Message.obtain(obtain, i, i2, i3, Long.valueOf(j)).sendToTarget();
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0029, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(int r2, android.os.Handler r3) {
        /*
            android.util.SparseArray<java.util.List<android.os.Handler>> r0 = b
            monitor-enter(r0)
            if (r3 != 0) goto L_0x0007
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            return
        L_0x0007:
            android.util.SparseArray<java.util.List<android.os.Handler>> r1 = b     // Catch:{ all -> 0x002a }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x002a }
            java.util.List r1 = (java.util.List) r1     // Catch:{ all -> 0x002a }
            if (r1 == 0) goto L_0x001b
            boolean r2 = r1.contains(r3)     // Catch:{ all -> 0x002a }
            if (r2 != 0) goto L_0x0028
            r1.add(r3)     // Catch:{ all -> 0x002a }
            goto L_0x0028
        L_0x001b:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x002a }
            r1.<init>()     // Catch:{ all -> 0x002a }
            r1.add(r3)     // Catch:{ all -> 0x002a }
            android.util.SparseArray<java.util.List<android.os.Handler>> r3 = b     // Catch:{ all -> 0x002a }
            r3.put(r2, r1)     // Catch:{ all -> 0x002a }
        L_0x0028:
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            return
        L_0x002a:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002a }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comjni.engine.a.a(int, android.os.Handler):void");
    }

    public static void b(int i, Handler handler) {
        synchronized (b) {
            if (handler != null) {
                handler.removeCallbacksAndMessages((Object) null);
                List list = b.get(i);
                if (list != null) {
                    list.remove(handler);
                }
            }
        }
    }
}
