package com.baidu.location.c;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.baidu.location.f;

public class d {
    private static d d = null;
    /* access modifiers changed from: private */
    public boolean a = false;
    /* access modifiers changed from: private */
    public String b = null;
    private a c = null;
    /* access modifiers changed from: private */
    public int e = -1;

    public class a extends BroadcastReceiver {
        public a() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:21:0x005d A[Catch:{ Exception -> 0x0077 }] */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x006d A[Catch:{ Exception -> 0x0077 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r6, android.content.Intent r7) {
            /*
                r5 = this;
                java.lang.String r6 = r7.getAction()
                r0 = 0
                java.lang.String r1 = "android.intent.action.BATTERY_CHANGED"
                boolean r6 = r6.equals(r1)     // Catch:{ Exception -> 0x0077 }
                if (r6 == 0) goto L_0x007d
                com.baidu.location.c.d r6 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                r1 = 0
                boolean unused = r6.a = r1     // Catch:{ Exception -> 0x0077 }
                java.lang.String r6 = "status"
                int r6 = r7.getIntExtra(r6, r1)     // Catch:{ Exception -> 0x0077 }
                java.lang.String r2 = "plugged"
                int r1 = r7.getIntExtra(r2, r1)     // Catch:{ Exception -> 0x0077 }
                java.lang.String r2 = "level"
                r3 = -1
                int r2 = r7.getIntExtra(r2, r3)     // Catch:{ Exception -> 0x0077 }
                java.lang.String r4 = "scale"
                int r7 = r7.getIntExtra(r4, r3)     // Catch:{ Exception -> 0x0077 }
                if (r2 <= 0) goto L_0x0039
                if (r7 <= 0) goto L_0x0039
                com.baidu.location.c.d r3 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                int r2 = r2 * 100
                int r2 = r2 / r7
                int unused = r3.e = r2     // Catch:{ Exception -> 0x0077 }
                goto L_0x003e
            L_0x0039:
                com.baidu.location.c.d r7 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                int unused = r7.e = r3     // Catch:{ Exception -> 0x0077 }
            L_0x003e:
                r7 = 2
                if (r6 == r7) goto L_0x0055
                r2 = 3
                if (r6 == r2) goto L_0x004d
                r2 = 4
                if (r6 == r2) goto L_0x004d
                com.baidu.location.c.d r6 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                java.lang.String unused = r6.b = r0     // Catch:{ Exception -> 0x0077 }
                goto L_0x005a
            L_0x004d:
                com.baidu.location.c.d r6 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                java.lang.String r2 = "3"
            L_0x0051:
                java.lang.String unused = r6.b = r2     // Catch:{ Exception -> 0x0077 }
                goto L_0x005a
            L_0x0055:
                com.baidu.location.c.d r6 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                java.lang.String r2 = "4"
                goto L_0x0051
            L_0x005a:
                r6 = 1
                if (r1 == r6) goto L_0x006d
                if (r1 == r7) goto L_0x0060
                goto L_0x007d
            L_0x0060:
                com.baidu.location.c.d r7 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                java.lang.String r1 = "5"
                java.lang.String unused = r7.b = r1     // Catch:{ Exception -> 0x0077 }
                com.baidu.location.c.d r7 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
            L_0x0069:
                boolean unused = r7.a = r6     // Catch:{ Exception -> 0x0077 }
                goto L_0x007d
            L_0x006d:
                com.baidu.location.c.d r7 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                java.lang.String r1 = "6"
                java.lang.String unused = r7.b = r1     // Catch:{ Exception -> 0x0077 }
                com.baidu.location.c.d r7 = com.baidu.location.c.d.this     // Catch:{ Exception -> 0x0077 }
                goto L_0x0069
            L_0x0077:
                r6 = move-exception
                com.baidu.location.c.d r6 = com.baidu.location.c.d.this
                java.lang.String unused = r6.b = r0
            L_0x007d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.d.a.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    private d() {
    }

    public static synchronized d a() {
        d dVar;
        synchronized (d.class) {
            if (d == null) {
                d = new d();
            }
            dVar = d;
        }
        return dVar;
    }

    public void b() {
        this.c = new a();
        try {
            f.getServiceContext().registerReceiver(this.c, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        } catch (Exception e2) {
        }
    }

    public void c() {
        if (this.c != null) {
            try {
                f.getServiceContext().unregisterReceiver(this.c);
            } catch (Exception e2) {
            }
        }
        this.c = null;
    }

    public String d() {
        return this.b;
    }

    public boolean e() {
        return this.a;
    }

    public int f() {
        return this.e;
    }
}
