package com.baidu.android.bbalbs.common.util;

import android.content.Context;
import android.text.TextUtils;

public final class c {
    private static b b;
    private static String c = "";
    private static volatile String d = "";
    private final Context a;

    private c(Context context) {
        this.a = context.getApplicationContext();
    }

    static String a() {
        if (TextUtils.isEmpty(c)) {
            c = "0newiqr3mini0";
        }
        return c;
    }

    public static String a(Context context) {
        return b(context).a();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0033, code lost:
        if (r3 >= r0) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0035, code lost:
        r1.append("0");
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
        c = r1.toString().trim();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0047, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r0 = "mini0".length() - r3.length();
        r1 = new java.lang.StringBuffer();
        r1.append("0newiqr3");
        r1.append(r3);
        r3 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(java.lang.String r3) {
        /*
            java.lang.String r0 = "mini0"
            int r0 = r0.length()
            boolean r0 = com.baidu.android.bbalbs.common.util.d.a((java.lang.String) r3, (int) r0)
            if (r0 == 0) goto L_0x004d
            java.lang.Class<com.baidu.android.bbalbs.common.util.b> r0 = com.baidu.android.bbalbs.common.util.b.class
            monitor-enter(r0)
            java.lang.String r1 = d     // Catch:{ all -> 0x004a }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x0048
            d = r3     // Catch:{ all -> 0x004a }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            java.lang.String r0 = "mini0"
            int r0 = r0.length()
            int r1 = r3.length()
            int r0 = r0 - r1
            java.lang.StringBuffer r1 = new java.lang.StringBuffer
            r1.<init>()
            java.lang.String r2 = "0newiqr3"
            r1.append(r2)
            r1.append(r3)
            r3 = 0
        L_0x0033:
            if (r3 >= r0) goto L_0x003d
            java.lang.String r2 = "0"
            r1.append(r2)
            int r3 = r3 + 1
            goto L_0x0033
        L_0x003d:
            java.lang.String r3 = r1.toString()
            java.lang.String r3 = r3.trim()
            c = r3
            return
        L_0x0048:
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            return
        L_0x004a:
            r3 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            throw r3
        L_0x004d:
            java.lang.IllegalArgumentException r3 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "expect src only letter or number , less than "
            r0.append(r1)
            java.lang.String r1 = "mini0"
            int r1 = r1.length()
            int r1 = r1 + 1
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r3.<init>(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.android.bbalbs.common.util.c.a(java.lang.String):void");
    }

    private b b() {
        b b2 = b.b(this.a);
        boolean z = b2 == null;
        if (b2 == null) {
            a b3 = a.b(this.a);
            if (b3 == null) {
                b2 = b.a(this.a, a());
            } else {
                b3.c();
                b2 = b.a(b3);
            }
        }
        if (z) {
            b2.a(this.a);
        }
        a.a(this.a);
        return b2;
    }

    private static b b(Context context) {
        if (b == null) {
            synchronized (b.class) {
                if (b == null) {
                    b = new c(context).b();
                }
            }
        }
        return b;
    }
}
