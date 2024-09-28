package com.baidu.android.bbalbs.common.util;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.android.bbalbs.common.a.b;
import java.io.File;

final class a {
    private static final String e;
    private String a;
    private String b;
    private int c = 0;
    private int d = 2;

    static {
        String str = new String(b.a(new byte[]{77, 122, 65, 121, 77, 84, 73, 120, 77, 68, 73, 61}));
        String str2 = new String(b.a(new byte[]{90, 71, 108, 106, 100, 87, 82, 112, 89, 87, 73, 61}));
        e = str + str2;
    }

    a() {
    }

    static boolean a(Context context) {
        File c2 = c(context);
        if (c2.exists()) {
            return c2.delete();
        }
        return false;
    }

    static a b(Context context) {
        return d(d.a(c(context)));
    }

    public static boolean b(int i) {
        return i >= 14;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0089 A[SYNTHETIC, Splitter:B:40:0x0089] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0096 A[SYNTHETIC, Splitter:B:48:0x0096] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.baidu.android.bbalbs.common.util.a c(java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            android.util.JsonReader r0 = new android.util.JsonReader     // Catch:{ IOException -> 0x0092, all -> 0x0086 }
            java.io.StringReader r2 = new java.io.StringReader     // Catch:{ IOException -> 0x0092, all -> 0x0086 }
            r2.<init>(r9)     // Catch:{ IOException -> 0x0092, all -> 0x0086 }
            r0.<init>(r2)     // Catch:{ IOException -> 0x0092, all -> 0x0086 }
            r0.beginObject()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            java.lang.String r9 = "ZGV2aWNlaWQ="
            java.lang.String r9 = g(r9)     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            java.lang.String r2 = "dmVy"
            java.lang.String r2 = g(r2)     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            r3 = 2
            java.lang.String r4 = "0"
            java.lang.String r5 = ""
            r6 = 2
        L_0x0027:
            boolean r7 = r0.hasNext()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            if (r7 == 0) goto L_0x004c
            java.lang.String r7 = r0.nextName()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            boolean r8 = r9.equals(r7)     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            if (r8 == 0) goto L_0x003c
            java.lang.String r5 = r0.nextString()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            goto L_0x0027
        L_0x003c:
            boolean r7 = r2.equals(r7)     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            if (r7 == 0) goto L_0x0047
            int r6 = r0.nextInt()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            goto L_0x0027
        L_0x0047:
            java.lang.String r4 = r0.nextString()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            goto L_0x0027
        L_0x004c:
            r0.endObject()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            r9 = 0
            if (r6 != r3) goto L_0x005d
            boolean r2 = android.text.TextUtils.isEmpty(r4)     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
            if (r2 == 0) goto L_0x0059
            goto L_0x005d
        L_0x0059:
            int r9 = r4.length()     // Catch:{ IOException -> 0x0084, all -> 0x0081 }
        L_0x005d:
            r0.close()     // Catch:{ Exception -> 0x0061 }
            goto L_0x0065
        L_0x0061:
            r0 = move-exception
            com.baidu.android.bbalbs.common.util.d.a((java.lang.Throwable) r0)
        L_0x0065:
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            if (r0 != 0) goto L_0x0080
            com.baidu.android.bbalbs.common.util.a r0 = new com.baidu.android.bbalbs.common.util.a
            r0.<init>()
            r0.a((java.lang.String) r5)
            r0.a((int) r9)
            boolean r9 = r0.d()
            if (r9 != 0) goto L_0x007f
            r0.b((java.lang.String) r4)
        L_0x007f:
            return r0
        L_0x0080:
            return r1
        L_0x0081:
            r9 = move-exception
            r1 = r0
            goto L_0x0087
        L_0x0084:
            r9 = move-exception
            goto L_0x0094
        L_0x0086:
            r9 = move-exception
        L_0x0087:
            if (r1 == 0) goto L_0x0091
            r1.close()     // Catch:{ Exception -> 0x008d }
            goto L_0x0091
        L_0x008d:
            r0 = move-exception
            com.baidu.android.bbalbs.common.util.d.a((java.lang.Throwable) r0)
        L_0x0091:
            throw r9
        L_0x0092:
            r9 = move-exception
            r0 = r1
        L_0x0094:
            if (r0 == 0) goto L_0x009e
            r0.close()     // Catch:{ Exception -> 0x009a }
            goto L_0x009e
        L_0x009a:
            r9 = move-exception
            com.baidu.android.bbalbs.common.util.d.a((java.lang.Throwable) r9)
        L_0x009e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.android.bbalbs.common.util.a.c(java.lang.String):com.baidu.android.bbalbs.common.util.a");
    }

    private static File c(Context context) {
        return new File(context.getFilesDir(), "libcuid.so");
    }

    static a d(String str) {
        return c(f(str));
    }

    public static boolean e(String str) {
        return TextUtils.isEmpty(str);
    }

    private static String f(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new String(com.baidu.android.bbalbs.common.a.a.b(e, e, b.a(str.getBytes())));
        } catch (Exception e2) {
            d.a((Throwable) e2);
            return "";
        }
    }

    private static String g(String str) {
        return new String(b.a(str.getBytes()));
    }

    public String a() {
        return this.a;
    }

    public void a(int i) {
        this.c = i;
    }

    public void a(String str) {
        this.a = str;
    }

    public String b() {
        return this.b;
    }

    public void b(String str) {
        this.b = str;
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        String str;
        if (d()) {
            str = "O";
        } else if (!e()) {
            return false;
        } else {
            str = "0";
        }
        this.b = str;
        return true;
    }

    public boolean d() {
        return b(this.c);
    }

    public boolean e() {
        return e(this.b);
    }
}
