package com.baidu.location.indoor.mapversion.b;

import android.os.Build;
import com.baidu.location.indoor.mapversion.IndoorJni;
import com.baidu.location.indoor.mapversion.c.a;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class a {
    private static Lock a = new ReentrantLock();

    public static boolean a() {
        return IndoorJni.a && Build.VERSION.SDK_INT > 19;
    }

    public static synchronized boolean a(String str) {
        Lock lock;
        String str2 = str;
        synchronized (a.class) {
            if (!a()) {
                return false;
            }
            a.d b = com.baidu.location.indoor.mapversion.c.a.a().b(str2);
            double[][] c = com.baidu.location.indoor.mapversion.c.a.a().c(str2);
            if (b == null) {
                return false;
            }
            b.a(CoordinateType.GCJ02);
            short[][] sArr = b.g;
            double d = b.a().a;
            double d2 = b.a().b;
            a.d c2 = com.baidu.location.indoor.mapversion.c.a.a().c();
            if (c2 == null) {
                return false;
            }
            double a2 = c2.a(-b.a().d);
            double b2 = c2.b(-b.a().f);
            a.lock();
            try {
                IndoorJni.setPfRdnt(str, sArr, d, d2, (int) b.f.g, (int) b.f.h, a2, b2);
                IndoorJni.setPfGeoMap(c, str2, (int) b.f.g, (int) b.f.h);
                lock = a;
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    lock = a;
                } catch (Throwable th) {
                    a.unlock();
                    throw th;
                }
            }
            lock.unlock();
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0091, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized double[] a(double r19, double r21, double r23, double r25, double r27) {
        /*
            r0 = r19
            r2 = r21
            java.lang.Class<com.baidu.location.indoor.mapversion.b.a> r4 = com.baidu.location.indoor.mapversion.b.a.class
            monitor-enter(r4)
            boolean r5 = a()     // Catch:{ all -> 0x0098 }
            if (r5 != 0) goto L_0x0010
            r0 = 0
            monitor-exit(r4)
            return r0
        L_0x0010:
            com.baidu.location.indoor.mapversion.c.a r5 = com.baidu.location.indoor.mapversion.c.a.a()     // Catch:{ all -> 0x0098 }
            r5.a((double) r0, (double) r2)     // Catch:{ all -> 0x0098 }
            com.baidu.location.indoor.mapversion.c.a r5 = com.baidu.location.indoor.mapversion.c.a.a()     // Catch:{ all -> 0x0098 }
            com.baidu.location.indoor.mapversion.c.a$d r5 = r5.c()     // Catch:{ all -> 0x0098 }
            double r6 = r5.a((double) r0)     // Catch:{ all -> 0x0098 }
            double r8 = r5.b((double) r2)     // Catch:{ all -> 0x0098 }
            r0 = 12
            double[] r1 = new double[r0]     // Catch:{ all -> 0x0098 }
            r2 = 0
            r10 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            r1[r2] = r10     // Catch:{ all -> 0x0098 }
            r3 = 1
            r1[r3] = r10     // Catch:{ all -> 0x0098 }
            r18 = 2
            r1[r18] = r10     // Catch:{ all -> 0x0098 }
            r0 = 3
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 4
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 5
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 6
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 7
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 8
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 9
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 10
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            r0 = 11
            r1[r0] = r10     // Catch:{ all -> 0x0098 }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0098 }
            r0.lock()     // Catch:{ all -> 0x0098 }
            long r16 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0071 }
            r10 = r23
            r12 = r25
            r14 = r27
            double[] r1 = com.baidu.location.indoor.mapversion.IndoorJni.setPfGps(r6, r8, r10, r12, r14, r16)     // Catch:{ Exception -> 0x0071 }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0098 }
        L_0x006b:
            r0.unlock()     // Catch:{ all -> 0x0098 }
            goto L_0x0078
        L_0x006f:
            r0 = move-exception
            goto L_0x0092
        L_0x0071:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x006f }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0098 }
            goto L_0x006b
        L_0x0078:
            r6 = r1[r2]     // Catch:{ all -> 0x0098 }
            r8 = 0
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 != 0) goto L_0x0090
            r6 = r1[r3]     // Catch:{ all -> 0x0098 }
            double r6 = r5.c(r6)     // Catch:{ all -> 0x0098 }
            r8 = r1[r18]     // Catch:{ all -> 0x0098 }
            double r8 = r5.d(r8)     // Catch:{ all -> 0x0098 }
            r1[r3] = r6     // Catch:{ all -> 0x0098 }
            r1[r18] = r8     // Catch:{ all -> 0x0098 }
        L_0x0090:
            monitor-exit(r4)
            return r1
        L_0x0092:
            java.util.concurrent.locks.Lock r1 = a     // Catch:{ all -> 0x0098 }
            r1.unlock()     // Catch:{ all -> 0x0098 }
            throw r0     // Catch:{ all -> 0x0098 }
        L_0x0098:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.mapversion.b.a.a(double, double, double, double, double):double[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0092, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized double[] a(com.baidu.location.BDLocation r17) {
        /*
            java.lang.Class<com.baidu.location.indoor.mapversion.b.a> r1 = com.baidu.location.indoor.mapversion.b.a.class
            monitor-enter(r1)
            boolean r0 = a()     // Catch:{ all -> 0x0093 }
            if (r0 != 0) goto L_0x000c
            r0 = 0
            monitor-exit(r1)
            return r0
        L_0x000c:
            com.baidu.location.indoor.mapversion.c.a r0 = com.baidu.location.indoor.mapversion.c.a.a()     // Catch:{ all -> 0x0093 }
            com.baidu.location.indoor.mapversion.c.a$d r2 = r0.c()     // Catch:{ all -> 0x0093 }
            r0 = 12
            double[] r3 = new double[r0]     // Catch:{ all -> 0x0093 }
            r4 = 0
            r5 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            r3[r4] = r5     // Catch:{ all -> 0x0093 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x0093 }
            r8 = 2
            r3[r8] = r5     // Catch:{ all -> 0x0093 }
            r0 = 3
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 4
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 5
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 6
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 7
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 8
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 9
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 10
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            r0 = 11
            r3[r0] = r5     // Catch:{ all -> 0x0093 }
            if (r2 == 0) goto L_0x0091
            double r5 = r17.getLongitude()     // Catch:{ all -> 0x0093 }
            double r9 = r2.a((double) r5)     // Catch:{ all -> 0x0093 }
            double r5 = r17.getLatitude()     // Catch:{ all -> 0x0093 }
            double r11 = r2.b((double) r5)     // Catch:{ all -> 0x0093 }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0093 }
            r0.lock()     // Catch:{ all -> 0x0093 }
            r13 = 4620693217682128896(0x4020000000000000, double:8.0)
            long r15 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x006b }
            double[] r3 = com.baidu.location.indoor.mapversion.IndoorJni.setPfWf(r9, r11, r13, r15)     // Catch:{ Exception -> 0x006b }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0093 }
        L_0x0065:
            r0.unlock()     // Catch:{ all -> 0x0093 }
            goto L_0x0072
        L_0x0069:
            r0 = move-exception
            goto L_0x008b
        L_0x006b:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0069 }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0093 }
            goto L_0x0065
        L_0x0072:
            r4 = r3[r4]     // Catch:{ all -> 0x0093 }
            r9 = 0
            int r0 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0091
            r4 = r3[r7]     // Catch:{ all -> 0x0093 }
            double r4 = r2.c(r4)     // Catch:{ all -> 0x0093 }
            r9 = r3[r8]     // Catch:{ all -> 0x0093 }
            double r9 = r2.d(r9)     // Catch:{ all -> 0x0093 }
            r3[r7] = r4     // Catch:{ all -> 0x0093 }
            r3[r8] = r9     // Catch:{ all -> 0x0093 }
            goto L_0x0091
        L_0x008b:
            java.util.concurrent.locks.Lock r2 = a     // Catch:{ all -> 0x0093 }
            r2.unlock()     // Catch:{ all -> 0x0093 }
            throw r0     // Catch:{ all -> 0x0093 }
        L_0x0091:
            monitor-exit(r1)
            return r3
        L_0x0093:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.mapversion.b.a.a(com.baidu.location.BDLocation):double[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0084, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized double[] a(java.lang.String r15, double r16, double r18, double r20) {
        /*
            java.lang.Class<com.baidu.location.indoor.mapversion.b.a> r1 = com.baidu.location.indoor.mapversion.b.a.class
            monitor-enter(r1)
            boolean r0 = a()     // Catch:{ all -> 0x0085 }
            if (r0 != 0) goto L_0x000c
            r0 = 0
            monitor-exit(r1)
            return r0
        L_0x000c:
            com.baidu.location.indoor.mapversion.c.a r0 = com.baidu.location.indoor.mapversion.c.a.a()     // Catch:{ all -> 0x0085 }
            com.baidu.location.indoor.mapversion.c.a$d r2 = r0.c()     // Catch:{ all -> 0x0085 }
            r0 = 12
            double[] r3 = new double[r0]     // Catch:{ all -> 0x0085 }
            r4 = 0
            r5 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            r3[r4] = r5     // Catch:{ all -> 0x0085 }
            r7 = 1
            r3[r7] = r5     // Catch:{ all -> 0x0085 }
            r8 = 2
            r3[r8] = r5     // Catch:{ all -> 0x0085 }
            r0 = 3
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 4
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 5
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 6
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 7
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 8
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 9
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 10
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            r0 = 11
            r3[r0] = r5     // Catch:{ all -> 0x0085 }
            if (r2 == 0) goto L_0x0083
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0085 }
            r0.lock()     // Catch:{ all -> 0x0085 }
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x005d }
            r9 = r18
            r11 = r20
            double[] r3 = com.baidu.location.indoor.mapversion.IndoorJni.setPfDr(r9, r11, r13)     // Catch:{ Exception -> 0x005d }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0085 }
        L_0x0057:
            r0.unlock()     // Catch:{ all -> 0x0085 }
            goto L_0x0064
        L_0x005b:
            r0 = move-exception
            goto L_0x007d
        L_0x005d:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x005b }
            java.util.concurrent.locks.Lock r0 = a     // Catch:{ all -> 0x0085 }
            goto L_0x0057
        L_0x0064:
            r4 = r3[r4]     // Catch:{ all -> 0x0085 }
            r9 = 0
            int r0 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0083
            r4 = r3[r7]     // Catch:{ all -> 0x0085 }
            double r4 = r2.c(r4)     // Catch:{ all -> 0x0085 }
            r9 = r3[r8]     // Catch:{ all -> 0x0085 }
            double r9 = r2.d(r9)     // Catch:{ all -> 0x0085 }
            r3[r7] = r4     // Catch:{ all -> 0x0085 }
            r3[r8] = r9     // Catch:{ all -> 0x0085 }
            goto L_0x0083
        L_0x007d:
            java.util.concurrent.locks.Lock r2 = a     // Catch:{ all -> 0x0085 }
            r2.unlock()     // Catch:{ all -> 0x0085 }
            throw r0     // Catch:{ all -> 0x0085 }
        L_0x0083:
            monitor-exit(r1)
            return r3
        L_0x0085:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.mapversion.b.a.a(java.lang.String, double, double, double):double[]");
    }

    public static void b() {
        if (a()) {
            a.lock();
            try {
                IndoorJni.initPf();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                a.unlock();
                throw th;
            }
            a.unlock();
        }
    }

    public static void c() {
        if (a()) {
            a.lock();
            try {
                IndoorJni.resetPf();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                a.unlock();
                throw th;
            }
            a.unlock();
        }
    }
}
