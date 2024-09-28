package com.baidu.location.b;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.baidu.location.Jni;
import com.baidu.location.e.j;
import com.baidu.location.f;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import com.googlecode.mp4parser.boxes.dece.BaseLocationBox;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

public abstract class i {
    public static String c = null;
    public com.baidu.location.e.i a = null;
    public com.baidu.location.e.a b = null;
    final Handler d = new a();
    private boolean e = true;
    private boolean f = true;
    private boolean g = false;
    /* access modifiers changed from: private */
    public String h = null;
    /* access modifiers changed from: private */
    public String i = null;
    private boolean j = false;

    public class a extends Handler {
        public a() {
        }

        public void handleMessage(Message message) {
            if (f.isServing) {
                int i = message.what;
                if (i == 21) {
                    i.this.a(message);
                } else if (i == 62 || i == 63) {
                    i.this.a();
                }
            }
        }
    }

    class b extends e {
        String a = null;
        String b = null;
        long c = 0;
        long d = 0;

        public b() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = k.e();
            if (!((!k.h && !k.j) || i.this.h == null || i.this.i == null)) {
                this.b += String.format(Locale.CHINA, "&ki=%s&sn=%s", new Object[]{i.this.h, i.this.i});
            }
            if (j.a().b()) {
                this.b += "&enc=2";
            }
            String encodeTp4 = Jni.encodeTp4(this.b);
            this.b = null;
            if (this.a == null) {
                this.a = w.b();
            }
            this.k.put(BaseLocationBox.TYPE, encodeTp4);
            if (this.a != null) {
                this.k.put("up", this.a);
            }
            this.k.put("trtm", String.format(Locale.CHINA, "%d", new Object[]{Long.valueOf(System.currentTimeMillis())}));
        }

        public void a(String str, long j) {
            this.b = str;
            this.d = System.currentTimeMillis();
            this.c = j;
            ExecutorService b2 = v.a().b();
            if (k.b()) {
                a(b2, false, (String) null);
            } else if (b2 != null) {
                a(b2, k.f);
            } else {
                c(k.f);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:45:0x00e5  */
        /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void a(boolean r10) {
            /*
                r9 = this;
                java.lang.String r0 = "enc"
                java.lang.String r1 = "HttpStatus error"
                r2 = 63
                if (r10 == 0) goto L_0x00d4
                java.lang.String r10 = r9.j
                if (r10 == 0) goto L_0x00d4
                java.lang.String r10 = r9.j     // Catch:{ Exception -> 0x00d3 }
                com.baidu.location.b.i.c = r10     // Catch:{ Exception -> 0x00d3 }
                boolean r3 = r10.contains(r0)     // Catch:{ Exception -> 0x00d3 }
                if (r3 == 0) goto L_0x003c
                com.baidu.location.b.j r3 = com.baidu.location.b.j.a()     // Catch:{ Exception -> 0x00d3 }
                boolean r3 = r3.b()     // Catch:{ Exception -> 0x00d3 }
                if (r3 == 0) goto L_0x003c
                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0038 }
                r3.<init>(r10)     // Catch:{ Exception -> 0x0038 }
                boolean r4 = r3.has(r0)     // Catch:{ Exception -> 0x0038 }
                if (r4 == 0) goto L_0x003c
                java.lang.String r0 = r3.getString(r0)     // Catch:{ Exception -> 0x0038 }
                com.baidu.location.b.j r3 = com.baidu.location.b.j.a()     // Catch:{ Exception -> 0x0038 }
                java.lang.String r10 = r3.a(r0)     // Catch:{ Exception -> 0x0038 }
                goto L_0x003c
            L_0x0038:
                r0 = move-exception
                r0.printStackTrace()     // Catch:{ Exception -> 0x00d3 }
            L_0x003c:
                com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation     // Catch:{ Exception -> 0x0071 }
                r0.<init>((java.lang.String) r10)     // Catch:{ Exception -> 0x0071 }
                int r3 = r0.getLocType()     // Catch:{ Exception -> 0x0071 }
                r4 = 161(0xa1, float:2.26E-43)
                if (r3 != r4) goto L_0x0050
                com.baidu.location.b.h r3 = com.baidu.location.b.h.a()     // Catch:{ Exception -> 0x0071 }
                r3.a((java.lang.String) r10)     // Catch:{ Exception -> 0x0071 }
            L_0x0050:
                com.baidu.location.e.b r10 = com.baidu.location.e.b.a()     // Catch:{ Exception -> 0x0071 }
                int r10 = r10.h()     // Catch:{ Exception -> 0x0071 }
                r0.setOperators(r10)     // Catch:{ Exception -> 0x0071 }
                com.baidu.location.b.n r10 = com.baidu.location.b.n.a()     // Catch:{ Exception -> 0x0071 }
                boolean r10 = r10.d()     // Catch:{ Exception -> 0x0071 }
                if (r10 == 0) goto L_0x007e
                com.baidu.location.b.n r10 = com.baidu.location.b.n.a()     // Catch:{ Exception -> 0x0071 }
                float r10 = r10.e()     // Catch:{ Exception -> 0x0071 }
                r0.setDirection(r10)     // Catch:{ Exception -> 0x0071 }
                goto L_0x007e
            L_0x0071:
                r10 = move-exception
                r10.printStackTrace()     // Catch:{ Exception -> 0x00d3 }
                com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation     // Catch:{ Exception -> 0x00d3 }
                r0.<init>()     // Catch:{ Exception -> 0x00d3 }
                r10 = 0
                r0.setLocType(r10)     // Catch:{ Exception -> 0x00d3 }
            L_0x007e:
                r10 = 0
                r9.a = r10     // Catch:{ Exception -> 0x00d3 }
                int r10 = r0.getLocType()     // Catch:{ Exception -> 0x00d3 }
                if (r10 != 0) goto L_0x00a7
                double r3 = r0.getLatitude()     // Catch:{ Exception -> 0x00d3 }
                r5 = 1
                int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r10 != 0) goto L_0x00a7
                double r3 = r0.getLongitude()     // Catch:{ Exception -> 0x00d3 }
                int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r10 != 0) goto L_0x00a7
                com.baidu.location.b.i r10 = com.baidu.location.b.i.this     // Catch:{ Exception -> 0x00d3 }
                android.os.Handler r10 = r10.d     // Catch:{ Exception -> 0x00d3 }
                android.os.Message r10 = r10.obtainMessage(r2)     // Catch:{ Exception -> 0x00d3 }
                r10.obj = r1     // Catch:{ Exception -> 0x00d3 }
            L_0x00a3:
                r10.sendToTarget()     // Catch:{ Exception -> 0x00d3 }
                goto L_0x00e1
            L_0x00a7:
                long r3 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00d3 }
                long r5 = r9.d     // Catch:{ Exception -> 0x00d3 }
                long r3 = r3 - r5
                r5 = 1000(0x3e8, double:4.94E-321)
                long r3 = r3 / r5
                r5 = 0
                int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r10 >= 0) goto L_0x00b8
                r3 = r5
            L_0x00b8:
                long r7 = r9.c     // Catch:{ Exception -> 0x00d3 }
                int r10 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r10 >= 0) goto L_0x00c0
                r9.c = r5     // Catch:{ Exception -> 0x00d3 }
            L_0x00c0:
                long r5 = r9.c     // Catch:{ Exception -> 0x00d3 }
                long r5 = r5 + r3
                r0.setDelayTime(r5)     // Catch:{ Exception -> 0x00d3 }
                com.baidu.location.b.i r10 = com.baidu.location.b.i.this     // Catch:{ Exception -> 0x00d3 }
                android.os.Handler r10 = r10.d     // Catch:{ Exception -> 0x00d3 }
                r3 = 21
                android.os.Message r10 = r10.obtainMessage(r3)     // Catch:{ Exception -> 0x00d3 }
                r10.obj = r0     // Catch:{ Exception -> 0x00d3 }
                goto L_0x00a3
            L_0x00d3:
                r10 = move-exception
            L_0x00d4:
                com.baidu.location.b.i r10 = com.baidu.location.b.i.this
                android.os.Handler r10 = r10.d
                android.os.Message r10 = r10.obtainMessage(r2)
                r10.obj = r1
                r10.sendToTarget()
            L_0x00e1:
                java.util.Map r10 = r9.k
                if (r10 == 0) goto L_0x00ea
                java.util.Map r10 = r9.k
                r10.clear()
            L_0x00ea:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.i.b.a(boolean):void");
        }
    }

    public String a(String str) {
        String m;
        com.baidu.location.e.i iVar;
        if (this.h == null) {
            this.h = com.baidu.location.a.a.b(f.getServiceContext());
        }
        if (this.i == null) {
            this.i = com.baidu.location.a.a.c(f.getServiceContext());
        }
        com.baidu.location.e.a aVar = this.b;
        if (aVar == null || !aVar.a()) {
            this.b = com.baidu.location.e.b.a().f();
        }
        com.baidu.location.e.i iVar2 = this.a;
        if (iVar2 == null || !iVar2.j()) {
            this.a = j.a().p();
        }
        Location h2 = com.baidu.location.e.f.a().j() ? com.baidu.location.e.f.a().h() : null;
        com.baidu.location.e.a aVar2 = this.b;
        if ((aVar2 == null || aVar2.d() || this.b.c()) && (((iVar = this.a) == null || iVar.a() == 0) && h2 == null)) {
            return null;
        }
        String b2 = b();
        if (h.a().d() == -2) {
            b2 = b2 + "&imo=1";
        }
        int b3 = k.b(f.getServiceContext());
        if (b3 >= 0) {
            b2 = b2 + "&lmd=" + b3;
            if (Build.VERSION.SDK_INT >= 28 && !this.j) {
                this.j = true;
                try {
                    if (f.getServiceContext().getPackageManager().hasSystemFeature("android.hardware.wifi.rtt")) {
                        b2 = b2 + "&rtt=1";
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }
        com.baidu.location.e.i iVar3 = this.a;
        if ((iVar3 == null || iVar3.a() == 0) && (m = j.a().m()) != null) {
            b2 = m + b2;
        }
        String str2 = b2;
        if (!this.f) {
            return k.a(this.b, this.a, h2, str2, 0);
        }
        this.f = false;
        return k.a(this.b, this.a, h2, str2, 0, true);
    }

    public abstract void a();

    public abstract void a(Message message);

    public String b() {
        String str;
        String d2 = a.a().d();
        if (j.j()) {
            str = "&cn=32";
        } else {
            str = String.format(Locale.CHINA, "&cn=%d", new Object[]{Integer.valueOf(com.baidu.location.e.b.a().e())});
        }
        if (Build.VERSION.SDK_INT >= 18) {
            String d3 = k.d();
            if (!TextUtils.isEmpty(d3)) {
                str = str + "&qcip6c=" + d3;
            }
        }
        if (this.e) {
            this.e = false;
            int i2 = Build.VERSION.SDK_INT;
        } else if (!this.g) {
            String e2 = w.e();
            if (e2 != null) {
                str = str + e2;
            }
            this.g = true;
        }
        return str + d2;
    }
}
