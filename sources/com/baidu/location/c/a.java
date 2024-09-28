package com.baidu.location.c;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.b.v;
import com.baidu.location.g.b;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class a {
    private static Object b = new Object();
    private static a c = null;
    private static final String d = (k.j() + "/gal.db");
    /* access modifiers changed from: private */
    public static Lock f = new ReentrantLock();
    C0006a a = null;
    /* access modifiers changed from: private */
    public SQLiteDatabase e = null;
    /* access modifiers changed from: private */
    public boolean g = false;
    private Map<String, Integer> h = new HashMap();
    private String i = null;
    private int j = -1;
    private String k = null;
    private double l = Double.MAX_VALUE;
    private double m = Double.MAX_VALUE;

    /* renamed from: com.baidu.location.c.a$a  reason: collision with other inner class name */
    class C0006a extends e {
        int a;
        int b;
        int c;
        int d;
        double e;

        C0006a() {
            this.k = new HashMap();
        }

        public void a() {
            String str;
            Map map;
            this.h = "http://loc.map.baidu.com/gpsz";
            String format = String.format(Locale.CHINESE, "&is_vdr=1&x=%d&y=%d%s", new Object[]{Integer.valueOf(this.a), Integer.valueOf(this.b), b.a().c()});
            String encode = Jni.encode(format);
            if (encode.contains("err!")) {
                try {
                    encode = new String(Base64.encode(format.getBytes(), 0), "UTF-8");
                } catch (Exception e2) {
                    encode = "err2!";
                }
                map = this.k;
                str = "gpszb";
            } else {
                map = this.k;
                str = "gpsz";
            }
            map.put(str, encode);
        }

        public void a(double d2, double d3, double d4) {
            if (!a.this.g) {
                double[] coorEncrypt = Jni.coorEncrypt(d2, d3, "gcj2wgs");
                this.a = (int) Math.floor(coorEncrypt[0] * 100.0d);
                this.b = (int) Math.floor(coorEncrypt[1] * 100.0d);
                this.c = (int) Math.floor(d2 * 100.0d);
                this.d = (int) Math.floor(d3 * 100.0d);
                this.e = d4;
                boolean unused = a.this.g = true;
                if (!k.b()) {
                    b(v.a().c());
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:35:0x012a A[Catch:{ Exception -> 0x0137 }] */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x016f A[Catch:{ Exception -> 0x023c }] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0138 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void a(boolean r31) {
            /*
                r30 = this;
                r1 = r30
                java.lang.String r2 = "galdata_new"
                java.lang.String r3 = "locStateData"
                java.lang.String r4 = ":"
                java.lang.String r5 = "info"
                java.lang.String r0 = "height"
                if (r31 == 0) goto L_0x0244
                java.lang.String r7 = r1.j
                if (r7 == 0) goto L_0x0244
                java.util.concurrent.locks.Lock r7 = com.baidu.location.c.a.f
                r7.lock()
                org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ Exception -> 0x023c }
                java.lang.String r8 = r1.j     // Catch:{ Exception -> 0x023c }
                r7.<init>(r8)     // Catch:{ Exception -> 0x023c }
                boolean r8 = r7.has(r0)     // Catch:{ Exception -> 0x023c }
                java.lang.String r9 = "id"
                java.lang.String r10 = "\""
                java.lang.String r11 = "id = \""
                java.lang.String r12 = "%d,%d"
                java.lang.String r13 = "E"
                java.lang.String r15 = ","
                r14 = 2
                r16 = 1
                if (r8 == 0) goto L_0x015e
                java.lang.String r0 = r7.getString(r0)     // Catch:{ Exception -> 0x023c }
                boolean r8 = r0.contains(r15)     // Catch:{ Exception -> 0x023c }
                if (r8 == 0) goto L_0x015e
                java.lang.String r0 = r0.trim()     // Catch:{ Exception -> 0x023c }
                java.lang.String[] r8 = r0.split(r15)     // Catch:{ Exception -> 0x023c }
                int r0 = r8.length     // Catch:{ Exception -> 0x023c }
                r18 = r7
                double r6 = (double) r0     // Catch:{ Exception -> 0x023c }
                double r6 = java.lang.Math.sqrt(r6)     // Catch:{ Exception -> 0x023c }
                int r6 = (int) r6     // Catch:{ Exception -> 0x023c }
                int r7 = r6 * r6
                if (r7 != r0) goto L_0x015a
                int r0 = r1.c     // Catch:{ Exception -> 0x023c }
                int r7 = r6 + -1
                int r19 = r7 / 2
                int r19 = r0 - r19
                int r0 = r1.d     // Catch:{ Exception -> 0x023c }
                int r7 = r7 / r14
                int r7 = r0 - r7
                r14 = 0
            L_0x0062:
                if (r14 >= r6) goto L_0x015a
                r20 = r3
                r3 = 0
            L_0x0067:
                if (r3 >= r6) goto L_0x0147
                android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ Exception -> 0x023c }
                r0.<init>()     // Catch:{ Exception -> 0x023c }
                int r21 = r14 * r6
                int r21 = r21 + r3
                r22 = r6
                r6 = r8[r21]     // Catch:{ Exception -> 0x023c }
                boolean r6 = r6.contains(r13)     // Catch:{ Exception -> 0x023c }
                r23 = r13
                java.lang.String r13 = "sigma"
                r24 = r15
                java.lang.String r15 = "aldata"
                r25 = 4666723172467343360(0x40c3880000000000, double:10000.0)
                if (r6 == 0) goto L_0x009c
                java.lang.Double r6 = java.lang.Double.valueOf(r25)     // Catch:{ Exception -> 0x023c }
                r0.put(r15, r6)     // Catch:{ Exception -> 0x023c }
                java.lang.Double r6 = java.lang.Double.valueOf(r25)     // Catch:{ Exception -> 0x023c }
            L_0x0094:
                r0.put(r13, r6)     // Catch:{ Exception -> 0x023c }
                r21 = r4
                r27 = r8
                goto L_0x00de
            L_0x009c:
                r6 = r8[r21]     // Catch:{ Exception -> 0x023c }
                boolean r6 = r6.contains(r4)     // Catch:{ Exception -> 0x023c }
                if (r6 != 0) goto L_0x00b2
                r6 = r8[r21]     // Catch:{ Exception -> 0x023c }
                java.lang.Double r6 = java.lang.Double.valueOf(r6)     // Catch:{ Exception -> 0x023c }
                r0.put(r15, r6)     // Catch:{ Exception -> 0x023c }
                java.lang.Double r6 = java.lang.Double.valueOf(r25)     // Catch:{ Exception -> 0x023c }
                goto L_0x0094
            L_0x00b2:
                r6 = r8[r21]     // Catch:{ Exception -> 0x023c }
                java.lang.String[] r6 = r6.split(r4)     // Catch:{ Exception -> 0x023c }
                r21 = r4
                int r4 = r6.length     // Catch:{ Exception -> 0x023c }
                r27 = r8
                r8 = 2
                if (r4 != r8) goto L_0x00d0
                r4 = 0
                r8 = r6[r4]     // Catch:{ Exception -> 0x023c }
                java.lang.Double r4 = java.lang.Double.valueOf(r8)     // Catch:{ Exception -> 0x023c }
                r0.put(r15, r4)     // Catch:{ Exception -> 0x023c }
                r4 = r6[r16]     // Catch:{ Exception -> 0x023c }
                r0.put(r13, r4)     // Catch:{ Exception -> 0x023c }
                goto L_0x00de
            L_0x00d0:
                java.lang.Double r4 = java.lang.Double.valueOf(r25)     // Catch:{ Exception -> 0x023c }
                r0.put(r15, r4)     // Catch:{ Exception -> 0x023c }
                java.lang.Double r4 = java.lang.Double.valueOf(r25)     // Catch:{ Exception -> 0x023c }
                r0.put(r13, r4)     // Catch:{ Exception -> 0x023c }
            L_0x00de:
                java.lang.String r4 = "tt"
                long r25 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x023c }
                r28 = 1000(0x3e8, double:4.94E-321)
                r8 = r5
                long r5 = r25 / r28
                int r6 = (int) r5     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r5 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x023c }
                r0.put(r4, r5)     // Catch:{ Exception -> 0x023c }
                int r4 = r19 + r3
                int r5 = r7 + r14
                java.util.Locale r6 = java.util.Locale.CHINESE     // Catch:{ Exception -> 0x023c }
                r13 = 2
                java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x023c }
                r13 = 0
                r15[r13] = r4     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x023c }
                r15[r16] = r4     // Catch:{ Exception -> 0x023c }
                java.lang.String r4 = java.lang.String.format(r6, r12, r15)     // Catch:{ Exception -> 0x023c }
                com.baidu.location.c.a r5 = com.baidu.location.c.a.this     // Catch:{ Exception -> 0x0137 }
                android.database.sqlite.SQLiteDatabase r5 = r5.e     // Catch:{ Exception -> 0x0137 }
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0137 }
                r6.<init>()     // Catch:{ Exception -> 0x0137 }
                r6.append(r11)     // Catch:{ Exception -> 0x0137 }
                r6.append(r4)     // Catch:{ Exception -> 0x0137 }
                r6.append(r10)     // Catch:{ Exception -> 0x0137 }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0137 }
                r13 = 0
                int r5 = r5.update(r2, r0, r6, r13)     // Catch:{ Exception -> 0x0137 }
                if (r5 > 0) goto L_0x0138
                r0.put(r9, r4)     // Catch:{ Exception -> 0x0137 }
                com.baidu.location.c.a r4 = com.baidu.location.c.a.this     // Catch:{ Exception -> 0x0137 }
                android.database.sqlite.SQLiteDatabase r4 = r4.e     // Catch:{ Exception -> 0x0137 }
                r4.insert(r2, r13, r0)     // Catch:{ Exception -> 0x0137 }
                goto L_0x0138
            L_0x0137:
                r0 = move-exception
            L_0x0138:
                int r3 = r3 + 1
                r5 = r8
                r4 = r21
                r6 = r22
                r13 = r23
                r15 = r24
                r8 = r27
                goto L_0x0067
            L_0x0147:
                r21 = r4
                r22 = r6
                r27 = r8
                r23 = r13
                r24 = r15
                r8 = r5
                int r14 = r14 + 1
                r3 = r20
                r8 = r27
                goto L_0x0062
            L_0x015a:
                r20 = r3
                r8 = r5
                goto L_0x0163
            L_0x015e:
                r20 = r3
                r8 = r5
                r18 = r7
            L_0x0163:
                r23 = r13
                r24 = r15
                r2 = r18
                boolean r0 = r2.has(r8)     // Catch:{ Exception -> 0x023c }
                if (r0 == 0) goto L_0x023d
                java.lang.String r0 = r2.getString(r8)     // Catch:{ Exception -> 0x023c }
                r2 = r24
                boolean r3 = r0.contains(r2)     // Catch:{ Exception -> 0x023c }
                if (r3 == 0) goto L_0x023d
                java.lang.String r0 = r0.trim()     // Catch:{ Exception -> 0x023c }
                java.lang.String[] r2 = r0.split(r2)     // Catch:{ Exception -> 0x023c }
                int r0 = r2.length     // Catch:{ Exception -> 0x023c }
                double r3 = (double) r0     // Catch:{ Exception -> 0x023c }
                double r3 = java.lang.Math.sqrt(r3)     // Catch:{ Exception -> 0x023c }
                int r3 = (int) r3     // Catch:{ Exception -> 0x023c }
                int r4 = r3 * r3
                if (r4 != r0) goto L_0x023d
                int r0 = r1.c     // Catch:{ Exception -> 0x023c }
                int r4 = r3 + -1
                int r5 = r4 / 2
                int r5 = r0 - r5
                int r0 = r1.d     // Catch:{ Exception -> 0x023c }
                r6 = 2
                int r4 = r4 / r6
                int r4 = r0 - r4
                r6 = 0
            L_0x019d:
                if (r6 >= r3) goto L_0x023d
                r7 = 0
            L_0x01a0:
                if (r7 >= r3) goto L_0x022d
                android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ Exception -> 0x023c }
                r0.<init>()     // Catch:{ Exception -> 0x023c }
                int r8 = r6 * r3
                int r8 = r8 + r7
                r13 = r2[r8]     // Catch:{ Exception -> 0x023c }
                r14 = r23
                boolean r13 = r13.contains(r14)     // Catch:{ Exception -> 0x023c }
                java.lang.String r15 = "state"
                if (r13 == 0) goto L_0x01bf
                r8 = -1
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x023c }
            L_0x01bb:
                r0.put(r15, r8)     // Catch:{ Exception -> 0x023c }
                goto L_0x01ce
            L_0x01bf:
                r8 = r2[r8]     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x023c }
                int r8 = r8.intValue()     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x023c }
                goto L_0x01bb
            L_0x01ce:
                int r8 = r5 + r7
                int r13 = r4 + r6
                java.util.Locale r15 = java.util.Locale.CHINESE     // Catch:{ Exception -> 0x023c }
                r18 = r2
                r19 = r3
                r2 = 2
                java.lang.Object[] r3 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x023c }
                r17 = 0
                r3[r17] = r8     // Catch:{ Exception -> 0x023c }
                java.lang.Integer r8 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x023c }
                r3[r16] = r8     // Catch:{ Exception -> 0x023c }
                java.lang.String r3 = java.lang.String.format(r15, r12, r3)     // Catch:{ Exception -> 0x023c }
                com.baidu.location.c.a r8 = com.baidu.location.c.a.this     // Catch:{ Exception -> 0x021d }
                android.database.sqlite.SQLiteDatabase r8 = r8.e     // Catch:{ Exception -> 0x021d }
                java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021d }
                r13.<init>()     // Catch:{ Exception -> 0x021d }
                r13.append(r11)     // Catch:{ Exception -> 0x021d }
                r13.append(r3)     // Catch:{ Exception -> 0x021d }
                r13.append(r10)     // Catch:{ Exception -> 0x021d }
                java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x021d }
                r15 = r20
                r2 = 0
                int r8 = r8.update(r15, r0, r13, r2)     // Catch:{ Exception -> 0x021b }
                if (r8 > 0) goto L_0x0221
                r0.put(r9, r3)     // Catch:{ Exception -> 0x021b }
                com.baidu.location.c.a r3 = com.baidu.location.c.a.this     // Catch:{ Exception -> 0x021b }
                android.database.sqlite.SQLiteDatabase r3 = r3.e     // Catch:{ Exception -> 0x021b }
                r3.insert(r15, r2, r0)     // Catch:{ Exception -> 0x021b }
                goto L_0x0221
            L_0x021b:
                r0 = move-exception
                goto L_0x0221
            L_0x021d:
                r0 = move-exception
                r15 = r20
                r2 = 0
            L_0x0221:
                int r7 = r7 + 1
                r23 = r14
                r20 = r15
                r2 = r18
                r3 = r19
                goto L_0x01a0
            L_0x022d:
                r18 = r2
                r19 = r3
                r15 = r20
                r14 = r23
                r2 = 0
                int r6 = r6 + 1
                r2 = r18
                goto L_0x019d
            L_0x023c:
                r0 = move-exception
            L_0x023d:
                java.util.concurrent.locks.Lock r0 = com.baidu.location.c.a.f
                r0.unlock()
            L_0x0244:
                java.util.Map r0 = r1.k
                if (r0 == 0) goto L_0x024d
                java.util.Map r0 = r1.k
                r0.clear()
            L_0x024d:
                com.baidu.location.c.a r0 = com.baidu.location.c.a.this
                r2 = 0
                boolean unused = r0.g = r2
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.a.C0006a.a(boolean):void");
        }
    }

    public static a a() {
        a aVar;
        synchronized (b) {
            if (c == null) {
                c = new a();
            }
            aVar = c;
        }
        return aVar;
    }

    private void a(double d2, double d3, double d4) {
        if (this.a == null) {
            this.a = new C0006a();
        }
        this.a.a(d2, d3, d4);
    }

    public int a(BDLocation bDLocation) {
        double d2;
        float f2;
        if (bDLocation != null) {
            f2 = bDLocation.getRadius();
            d2 = bDLocation.getAltitude();
        } else {
            d2 = 0.0d;
            f2 = 0.0f;
        }
        if (this.e == null || f2 <= 0.0f || d2 <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || bDLocation == null) {
            return 0;
        }
        double d3 = a(bDLocation.getLongitude(), bDLocation.getLatitude())[0];
        if (d3 == Double.MAX_VALUE) {
            return 0;
        }
        double gpsSwiftRadius = Jni.getGpsSwiftRadius(f2, d2, d3);
        if (gpsSwiftRadius > 50.0d) {
            return 3;
        }
        return gpsSwiftRadius > 20.0d ? 2 : 1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00de, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e2, code lost:
        r18 = r7;
        r3 = Double.MAX_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e5, code lost:
        r12 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00e7, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00e8, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ec, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x010c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x010d, code lost:
        r2 = r0;
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0112, code lost:
        r3 = Double.MAX_VALUE;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:36:0x00c3, B:55:0x00f1] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e7 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:15:0x007d] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0106 A[SYNTHETIC, Splitter:B:61:0x0106] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x010c A[ExcHandler: all (th java.lang.Throwable), PHI: r18 
      PHI: (r18v3 android.database.Cursor) = (r18v5 android.database.Cursor), (r18v15 android.database.Cursor) binds: [B:55:0x00f1, B:36:0x00c3] A[DONT_GENERATE, DONT_INLINE], Splitter:B:36:0x00c3] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x011a A[SYNTHETIC, Splitter:B:72:0x011a] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0124 A[SYNTHETIC, Splitter:B:79:0x0124] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x013b  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0146  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x014a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double[] a(double r20, double r22) {
        /*
            r19 = this;
            r8 = r19
            r0 = 2
            double[] r9 = new double[r0]
            android.database.sqlite.SQLiteDatabase r1 = r8.e
            r10 = 0
            r11 = 1
            r12 = 9218868437227405311(0x7fefffffffffffff, double:1.7976931348623157E308)
            r14 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            if (r1 == 0) goto L_0x0131
            r1 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            int r3 = (r20 > r1 ? 1 : (r20 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0131
            int r3 = (r22 > r1 ? 1 : (r22 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x0131
            java.util.Locale r1 = java.util.Locale.CHINESE
            java.lang.Object[] r2 = new java.lang.Object[r0]
            r3 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r5 = r20 * r3
            double r5 = java.lang.Math.floor(r5)
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r10] = r5
            double r3 = r3 * r22
            double r3 = java.lang.Math.floor(r3)
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r11] = r3
            java.lang.String r3 = "%d,%d"
            java.lang.String r6 = java.lang.String.format(r1, r3, r2)
            java.lang.String r1 = r8.k
            if (r1 == 0) goto L_0x0059
            boolean r1 = r1.equals(r6)
            if (r1 == 0) goto L_0x0059
            double r12 = r8.l
            double r0 = r8.m
            r2 = r14
            goto L_0x0137
        L_0x0059:
            java.util.concurrent.locks.Lock r1 = f
            r1.lock()
            r1 = 0
            android.database.sqlite.SQLiteDatabase r2 = r8.e     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            r3.<init>()     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            java.lang.String r4 = "select * from galdata_new where id = \""
            r3.append(r4)     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            r3.append(r6)     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            java.lang.String r4 = "\";"
            r3.append(r4)     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            android.database.Cursor r7 = r2.rawQuery(r3, r1)     // Catch:{ Exception -> 0x0120, all -> 0x0116 }
            if (r7 == 0) goto L_0x00ef
            boolean r1 = r7.moveToFirst()     // Catch:{ Exception -> 0x00eb, all -> 0x00e7 }
            if (r1 == 0) goto L_0x00ef
            double r1 = r7.getDouble(r11)     // Catch:{ Exception -> 0x00eb, all -> 0x00e7 }
            double r3 = r7.getDouble(r0)     // Catch:{ Exception -> 0x00e1, all -> 0x00e7 }
            r0 = 3
            int r0 = r7.getInt(r0)     // Catch:{ Exception -> 0x00dd, all -> 0x00e7 }
            int r5 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r5 != 0) goto L_0x0095
            r1 = r12
        L_0x0095:
            r16 = 0
            int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r5 > 0) goto L_0x009c
            goto L_0x009d
        L_0x009c:
            r12 = r3
        L_0x009d:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00d6, all -> 0x00e7 }
            r16 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 / r16
            long r10 = (long) r0     // Catch:{ Exception -> 0x00d6, all -> 0x00e7 }
            long r3 = r3 - r10
            boolean r0 = r8.g     // Catch:{ Exception -> 0x00d6, all -> 0x00e7 }
            if (r0 != 0) goto L_0x00c7
            r10 = 604800(0x93a80, double:2.98811E-318)
            int r0 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x00c7
            r10 = 4620817066822205440(0x402070a3e0000000, double:8.220000267028809)
            r4 = r1
            r1 = r19
            r2 = r20
            r14 = r4
            r4 = r22
            r0 = r6
            r18 = r7
            r6 = r10
            r1.a(r2, r4, r6)     // Catch:{ Exception -> 0x00d4, all -> 0x010c }
            goto L_0x00cb
        L_0x00c7:
            r14 = r1
            r0 = r6
            r18 = r7
        L_0x00cb:
            r8.k = r0     // Catch:{ Exception -> 0x00d4, all -> 0x010c }
            r8.l = r14     // Catch:{ Exception -> 0x00d4, all -> 0x010c }
            r8.m = r12     // Catch:{ Exception -> 0x00d4, all -> 0x010c }
            r1 = r12
            r12 = r14
            goto L_0x0104
        L_0x00d4:
            r0 = move-exception
            goto L_0x00da
        L_0x00d6:
            r0 = move-exception
            r14 = r1
            r18 = r7
        L_0x00da:
            r3 = r12
            r12 = r14
            goto L_0x0113
        L_0x00dd:
            r0 = move-exception
            r18 = r7
            goto L_0x00e5
        L_0x00e1:
            r0 = move-exception
            r18 = r7
            r3 = r12
        L_0x00e5:
            r12 = r1
            goto L_0x0113
        L_0x00e7:
            r0 = move-exception
            r18 = r7
            goto L_0x010d
        L_0x00eb:
            r0 = move-exception
            r18 = r7
            goto L_0x0112
        L_0x00ef:
            r18 = r7
            boolean r0 = r8.g     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            if (r0 != 0) goto L_0x0103
            r6 = 4620817066822205440(0x402070a3e0000000, double:8.220000267028809)
            r1 = r19
            r2 = r20
            r4 = r22
            r1.a(r2, r4, r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
        L_0x0103:
            r1 = r12
        L_0x0104:
            if (r18 == 0) goto L_0x012a
            r18.close()     // Catch:{ Exception -> 0x010a }
            goto L_0x012a
        L_0x010a:
            r0 = move-exception
            goto L_0x012a
        L_0x010c:
            r0 = move-exception
        L_0x010d:
            r2 = r0
            r1 = r18
            goto L_0x0118
        L_0x0111:
            r0 = move-exception
        L_0x0112:
            r3 = r12
        L_0x0113:
            r1 = r18
            goto L_0x0122
        L_0x0116:
            r0 = move-exception
            r2 = r0
        L_0x0118:
            if (r1 == 0) goto L_0x011f
            r1.close()     // Catch:{ Exception -> 0x011e }
            goto L_0x011f
        L_0x011e:
            r0 = move-exception
        L_0x011f:
            throw r2
        L_0x0120:
            r0 = move-exception
            r3 = r12
        L_0x0122:
            if (r1 == 0) goto L_0x0129
            r1.close()     // Catch:{ Exception -> 0x0128 }
            goto L_0x0129
        L_0x0128:
            r0 = move-exception
        L_0x0129:
            r1 = r3
        L_0x012a:
            java.util.concurrent.locks.Lock r0 = f
            r0.unlock()
            r0 = r1
            goto L_0x0132
        L_0x0131:
            r0 = r12
        L_0x0132:
            r2 = 4666723172467343360(0x40c3880000000000, double:10000.0)
        L_0x0137:
            int r4 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x013f
            r4 = 0
            r9[r4] = r2
            goto L_0x0142
        L_0x013f:
            r4 = 0
            r9[r4] = r12
        L_0x0142:
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x014a
            r4 = 1
            r9[r4] = r2
            goto L_0x014d
        L_0x014a:
            r4 = 1
            r9[r4] = r0
        L_0x014d:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.a.a(double, double):double[]");
    }

    public void b() {
        SQLiteDatabase sQLiteDatabase;
        try {
            File file = new File(d);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists()) {
                SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(file, (SQLiteDatabase.CursorFactory) null);
                this.e = openOrCreateDatabase;
                Cursor rawQuery = openOrCreateDatabase.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='galdata'", (String[]) null);
                if (rawQuery.moveToFirst()) {
                    if (rawQuery.getInt(0) == 0) {
                        this.e.execSQL("CREATE TABLE IF NOT EXISTS galdata_new(id CHAR(40) PRIMARY KEY,aldata DOUBLE, sigma DOUBLE,tt INT);");
                        sQLiteDatabase = this.e;
                    } else {
                        this.e.execSQL("DROP TABLE galdata");
                        this.e.execSQL("CREATE TABLE galdata_new(id CHAR(40) PRIMARY KEY,aldata DOUBLE, sigma DOUBLE,tt INT);");
                        sQLiteDatabase = this.e;
                    }
                    sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS locStateData(id CHAR(40) PRIMARY KEY,state INT);");
                }
                this.e.setVersion(1);
                rawQuery.close();
            }
        } catch (Exception e2) {
            this.e = null;
        }
    }

    public void c() {
        SQLiteDatabase sQLiteDatabase = this.e;
        if (sQLiteDatabase != null) {
            try {
                sQLiteDatabase.close();
            } catch (Exception e2) {
            } catch (Throwable th) {
                this.e = null;
                throw th;
            }
            this.e = null;
        }
    }
}
