package com.baidu.location.c;

import android.content.SharedPreferences;
import com.baidu.location.Jni;
import com.baidu.location.b.v;
import com.baidu.location.f;
import com.baidu.location.g.b;
import com.baidu.location.g.c;
import com.baidu.location.g.j;
import com.baidu.location.g.k;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.json.JSONObject;

public class e {
    private static e i = null;
    private static final String l = (j.a + "/conlts.dat");
    private static int m = -1;
    private static int n = -1;
    private static int o = 0;
    public boolean a = true;
    public boolean b = true;
    public boolean c = false;
    public boolean d = true;
    public boolean e = true;
    public boolean f = true;
    public boolean g = true;
    public boolean h = false;
    private a j = null;
    /* access modifiers changed from: private */
    public String k = "https://loc.map.baidu.com/cfgs/loc/commcfgs";

    class a extends com.baidu.location.g.e {
        String a = null;
        boolean b = false;
        boolean c = false;

        public a() {
            this.k = new HashMap();
        }

        public void a() {
            String str;
            Map map;
            this.h = k.e();
            this.i = 2;
            String encode = Jni.encode(this.a);
            this.a = null;
            if (this.b) {
                map = this.k;
                str = "grid";
            } else {
                map = this.k;
                str = "conf";
            }
            map.put("qt", str);
            this.k.put("req", encode);
        }

        public void a(String str, boolean z) {
            if (!this.c) {
                this.c = true;
                this.a = str;
                this.b = z;
                ExecutorService c2 = v.a().c();
                if (z) {
                    a(c2, true, "loc.map.baidu.com");
                } else if (c2 != null) {
                    a(c2, e.this.k);
                } else {
                    c(e.this.k);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:11:0x0021  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void a(boolean r2) {
            /*
                r1 = this;
                if (r2 == 0) goto L_0x0017
                java.lang.String r2 = r1.j
                if (r2 == 0) goto L_0x0017
                boolean r2 = r1.b
                if (r2 == 0) goto L_0x0012
                com.baidu.location.c.e r2 = com.baidu.location.c.e.this
                byte[] r0 = r1.m
                r2.a((byte[]) r0)
                goto L_0x001d
            L_0x0012:
                com.baidu.location.c.e r2 = com.baidu.location.c.e.this
                java.lang.String r0 = r1.j
                goto L_0x001a
            L_0x0017:
                com.baidu.location.c.e r2 = com.baidu.location.c.e.this
                r0 = 0
            L_0x001a:
                r2.b((java.lang.String) r0)
            L_0x001d:
                java.util.Map r2 = r1.k
                if (r2 == 0) goto L_0x0026
                java.util.Map r2 = r1.k
                r2.clear()
            L_0x0026:
                r2 = 0
                r1.c = r2
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.e.a.a(boolean):void");
        }
    }

    private e() {
    }

    public static e a() {
        if (i == null) {
            i = new e();
        }
        return i;
    }

    private void a(int i2) {
        boolean z = true;
        this.a = (i2 & 1) == 1;
        this.b = (i2 & 2) == 2;
        this.c = (i2 & 4) == 4;
        this.d = (i2 & 8) == 8;
        this.f = (i2 & 65536) == 65536;
        if ((i2 & 131072) != 131072) {
            z = false;
        }
        this.g = z;
        if ((i2 & 16) == 16) {
            this.e = false;
        }
    }

    private void a(JSONObject jSONObject) {
        if (jSONObject != null) {
            boolean z = true;
            int i2 = 14400000;
            int i3 = 10;
            try {
                if (jSONObject.has("ipen") && jSONObject.getInt("ipen") == 0) {
                    z = false;
                }
                if (jSONObject.has("ipvt")) {
                    i2 = jSONObject.getInt("ipvt");
                }
                if (jSONObject.has("ipvn")) {
                    i3 = jSONObject.getInt("ipvn");
                }
                SharedPreferences.Editor edit = f.getServiceContext().getSharedPreferences("MapCoreServicePre", 0).edit();
                edit.putBoolean("ipLocInfoUpload", z);
                edit.putInt("ipValidTime", i2);
                edit.putInt("ipLocInfoUploadTimesPerDay", i3);
                edit.commit();
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(byte[] bArr) {
        byte[] bArr2 = bArr;
        int i2 = 0;
        if (bArr2 != null) {
            try {
                if (bArr2.length < 640) {
                    k.x = false;
                    k.u = k.s + 0.025d;
                    k.t = k.r - 0.025d;
                } else {
                    k.x = true;
                    k.t = Double.longBitsToDouble(((((long) bArr2[7]) & 255) << 56) | ((((long) bArr2[6]) & 255) << 48) | ((((long) bArr2[5]) & 255) << 40) | ((((long) bArr2[4]) & 255) << 32) | ((((long) bArr2[3]) & 255) << 24) | ((((long) bArr2[2]) & 255) << 16) | ((((long) bArr2[1]) & 255) << 8) | (((long) bArr2[0]) & 255));
                    k.u = Double.longBitsToDouble(((((long) bArr2[15]) & 255) << 56) | ((((long) bArr2[14]) & 255) << 48) | ((((long) bArr2[13]) & 255) << 40) | ((((long) bArr2[12]) & 255) << 32) | ((((long) bArr2[11]) & 255) << 24) | ((((long) bArr2[10]) & 255) << 16) | ((((long) bArr2[9]) & 255) << 8) | (255 & ((long) bArr2[8])));
                    k.w = new byte[625];
                    while (i2 < 625) {
                        k.w[i2] = bArr2[i2 + 16];
                        i2++;
                    }
                }
                i2 = 1;
            } catch (Exception e2) {
                return;
            }
        }
        if (i2 != 0) {
            g();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0039 A[SYNTHETIC, Splitter:B:16:0x0039] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0051 A[Catch:{ Exception -> 0x0405 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(java.lang.String r18) {
        /*
            r17 = this;
            r0 = r18
            java.lang.String r1 = "shak"
            java.lang.String r2 = "gpc"
            java.lang.String r3 = "zxd"
            java.lang.String r4 = "ab"
            java.lang.String r5 = "wf"
            java.lang.String r6 = "up"
            java.lang.String r7 = "gps"
            java.lang.String r8 = "is_check_Per"
            java.lang.String r9 = "ipconf"
            r10 = 1
            r11 = 0
            if (r0 == 0) goto L_0x040b
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ Exception -> 0x0407 }
            r12.<init>(r0)     // Catch:{ Exception -> 0x0407 }
            boolean r0 = r12.has(r9)     // Catch:{ Exception -> 0x0407 }
            if (r0 == 0) goto L_0x0031
            org.json.JSONObject r0 = r12.getJSONObject(r9)     // Catch:{ Exception -> 0x0030 }
            r9 = r17
            r9.a((org.json.JSONObject) r0)     // Catch:{ Exception -> 0x002e }
            goto L_0x0033
        L_0x002e:
            r0 = move-exception
            goto L_0x0033
        L_0x0030:
            r0 = move-exception
        L_0x0031:
            r9 = r17
        L_0x0033:
            boolean r0 = r12.has(r8)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x0043
            int r0 = r12.getInt(r8)     // Catch:{ Exception -> 0x0042 }
            if (r0 <= 0) goto L_0x0043
            com.baidu.location.g.k.ay = r10     // Catch:{ Exception -> 0x0042 }
            goto L_0x0043
        L_0x0042:
            r0 = move-exception
        L_0x0043:
            java.lang.String r0 = "ver"
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Exception -> 0x0405 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0405 }
            int r8 = com.baidu.location.g.k.y     // Catch:{ Exception -> 0x0405 }
            if (r0 <= r8) goto L_0x040d
            com.baidu.location.g.k.y = r0     // Catch:{ Exception -> 0x0405 }
            boolean r0 = r12.has(r7)     // Catch:{ Exception -> 0x0405 }
            java.lang.String r13 = "\\|"
            r8 = 2
            java.lang.String r14 = ""
            if (r0 == 0) goto L_0x0150
            java.lang.String r0 = r12.getString(r7)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r7 = r0.length     // Catch:{ Exception -> 0x0405 }
            r15 = 10
            if (r7 <= r15) goto L_0x0150
            r7 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x007f
            r7 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x007f
            r7 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.z = r7     // Catch:{ Exception -> 0x0405 }
        L_0x007f:
            r7 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x0093
            r7 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x0093
            r7 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.A = r7     // Catch:{ Exception -> 0x0405 }
        L_0x0093:
            r7 = r0[r8]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x00a7
            r7 = r0[r8]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x00a7
            r7 = r0[r8]     // Catch:{ Exception -> 0x0405 }
            float r7 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.B = r7     // Catch:{ Exception -> 0x0405 }
        L_0x00a7:
            r7 = 3
            r16 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r16 == 0) goto L_0x00bc
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x00bc
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            float r7 = java.lang.Float.parseFloat(r8)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.C = r7     // Catch:{ Exception -> 0x0405 }
        L_0x00bc:
            r7 = 4
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x00d1
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x00d1
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.D = r7     // Catch:{ Exception -> 0x0405 }
        L_0x00d1:
            r7 = 5
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x00e6
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x00e6
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r8)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.E = r7     // Catch:{ Exception -> 0x0405 }
        L_0x00e6:
            r7 = 6
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x00fb
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x00fb
            r7 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.F = r7     // Catch:{ Exception -> 0x0405 }
        L_0x00fb:
            r7 = 7
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x0110
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x0110
            r7 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.G = r7     // Catch:{ Exception -> 0x0405 }
        L_0x0110:
            r7 = 8
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x0126
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x0126
            r7 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.H = r7     // Catch:{ Exception -> 0x0405 }
        L_0x0126:
            r7 = 9
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            if (r8 == 0) goto L_0x013c
            r8 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            boolean r8 = r8.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r8 != 0) goto L_0x013c
            r7 = r0[r7]     // Catch:{ Exception -> 0x0405 }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.I = r7     // Catch:{ Exception -> 0x0405 }
        L_0x013c:
            r7 = r0[r15]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x0150
            r7 = r0[r15]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x0150
            r0 = r0[r15]     // Catch:{ Exception -> 0x0405 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.J = r0     // Catch:{ Exception -> 0x0405 }
        L_0x0150:
            boolean r0 = r12.has(r6)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x01b4
            java.lang.String r0 = r12.getString(r6)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r6 = r0.length     // Catch:{ Exception -> 0x0405 }
            r7 = 3
            if (r6 <= r7) goto L_0x01b4
            r6 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r6 == 0) goto L_0x0176
            r6 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r6 = r6.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r6 != 0) goto L_0x0176
            r6 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            float r6 = java.lang.Float.parseFloat(r6)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.K = r6     // Catch:{ Exception -> 0x0405 }
        L_0x0176:
            r6 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r6 == 0) goto L_0x018a
            r6 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r6 = r6.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r6 != 0) goto L_0x018a
            r6 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            float r6 = java.lang.Float.parseFloat(r6)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.L = r6     // Catch:{ Exception -> 0x0405 }
        L_0x018a:
            r6 = 2
            r7 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x019f
            r7 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x019f
            r7 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            float r6 = java.lang.Float.parseFloat(r7)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.M = r6     // Catch:{ Exception -> 0x0405 }
        L_0x019f:
            r6 = 3
            r7 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            if (r7 == 0) goto L_0x01b4
            r7 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            boolean r7 = r7.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r7 != 0) goto L_0x01b4
            r0 = r0[r6]     // Catch:{ Exception -> 0x0405 }
            float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.N = r0     // Catch:{ Exception -> 0x0405 }
        L_0x01b4:
            boolean r0 = r12.has(r5)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x0218
            java.lang.String r0 = r12.getString(r5)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r5 = r0.length     // Catch:{ Exception -> 0x0405 }
            r6 = 3
            if (r5 <= r6) goto L_0x0218
            r5 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r5 == 0) goto L_0x01da
            r5 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r5 = r5.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r5 != 0) goto L_0x01da
            r5 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            int r5 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.O = r5     // Catch:{ Exception -> 0x0405 }
        L_0x01da:
            r5 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r5 == 0) goto L_0x01ee
            r5 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r5 = r5.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r5 != 0) goto L_0x01ee
            r5 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            float r5 = java.lang.Float.parseFloat(r5)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.Q = r5     // Catch:{ Exception -> 0x0405 }
        L_0x01ee:
            r5 = 2
            r6 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            if (r6 == 0) goto L_0x0203
            r6 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            boolean r6 = r6.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r6 != 0) goto L_0x0203
            r6 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            int r5 = java.lang.Integer.parseInt(r6)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.R = r5     // Catch:{ Exception -> 0x0405 }
        L_0x0203:
            r5 = 3
            r6 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            if (r6 == 0) goto L_0x0218
            r6 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            boolean r6 = r6.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r6 != 0) goto L_0x0218
            r0 = r0[r5]     // Catch:{ Exception -> 0x0405 }
            float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.S = r0     // Catch:{ Exception -> 0x0405 }
        L_0x0218:
            boolean r0 = r12.has(r4)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x027c
            java.lang.String r0 = r12.getString(r4)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r4 = r0.length     // Catch:{ Exception -> 0x0405 }
            r5 = 3
            if (r4 <= r5) goto L_0x027c
            r4 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r4 == 0) goto L_0x023e
            r4 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r4 = r4.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r4 != 0) goto L_0x023e
            r4 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            float r4 = java.lang.Float.parseFloat(r4)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.T = r4     // Catch:{ Exception -> 0x0405 }
        L_0x023e:
            r4 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r4 == 0) goto L_0x0252
            r4 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r4 = r4.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r4 != 0) goto L_0x0252
            r4 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            float r4 = java.lang.Float.parseFloat(r4)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.U = r4     // Catch:{ Exception -> 0x0405 }
        L_0x0252:
            r4 = 2
            r5 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            if (r5 == 0) goto L_0x0267
            r5 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            boolean r5 = r5.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r5 != 0) goto L_0x0267
            r5 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            int r4 = java.lang.Integer.parseInt(r5)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.V = r4     // Catch:{ Exception -> 0x0405 }
        L_0x0267:
            r4 = 3
            r5 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            if (r5 == 0) goto L_0x027c
            r5 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            boolean r5 = r5.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r5 != 0) goto L_0x027c
            r0 = r0[r4]     // Catch:{ Exception -> 0x0405 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.W = r0     // Catch:{ Exception -> 0x0405 }
        L_0x027c:
            boolean r0 = r12.has(r3)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x02f5
            java.lang.String r0 = r12.getString(r3)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r3 = r0.length     // Catch:{ Exception -> 0x0405 }
            r4 = 4
            if (r3 <= r4) goto L_0x02f5
            r3 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x02a2
            r3 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x02a2
            r3 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.as = r3     // Catch:{ Exception -> 0x0405 }
        L_0x02a2:
            r3 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x02b6
            r3 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x02b6
            r3 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            float r3 = java.lang.Float.parseFloat(r3)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.at = r3     // Catch:{ Exception -> 0x0405 }
        L_0x02b6:
            r3 = 2
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            if (r4 == 0) goto L_0x02cb
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            boolean r4 = r4.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r4 != 0) goto L_0x02cb
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            int r3 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.au = r3     // Catch:{ Exception -> 0x0405 }
        L_0x02cb:
            r3 = 3
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            if (r4 == 0) goto L_0x02e0
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            boolean r4 = r4.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r4 != 0) goto L_0x02e0
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            int r3 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.av = r3     // Catch:{ Exception -> 0x0405 }
        L_0x02e0:
            r3 = 4
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            if (r4 == 0) goto L_0x02f5
            r4 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            boolean r4 = r4.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r4 != 0) goto L_0x02f5
            r0 = r0[r3]     // Catch:{ Exception -> 0x0405 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.aw = r0     // Catch:{ Exception -> 0x0405 }
        L_0x02f5:
            boolean r0 = r12.has(r2)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x03a5
            java.lang.String r0 = r12.getString(r2)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r2 = r0.length     // Catch:{ Exception -> 0x0405 }
            r3 = 5
            if (r2 <= r3) goto L_0x03a5
            r2 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r2 == 0) goto L_0x0320
            r2 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r2 = r2.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r2 != 0) goto L_0x0320
            r2 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0405 }
            if (r2 <= 0) goto L_0x031e
            com.baidu.location.g.k.ab = r10     // Catch:{ Exception -> 0x0405 }
            goto L_0x0320
        L_0x031e:
            com.baidu.location.g.k.ab = r11     // Catch:{ Exception -> 0x0405 }
        L_0x0320:
            r2 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r2 == 0) goto L_0x0339
            r2 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r2 = r2.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r2 != 0) goto L_0x0339
            r2 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0405 }
            if (r2 <= 0) goto L_0x0337
            com.baidu.location.g.k.ac = r10     // Catch:{ Exception -> 0x0405 }
            goto L_0x0339
        L_0x0337:
            com.baidu.location.g.k.ac = r11     // Catch:{ Exception -> 0x0405 }
        L_0x0339:
            r2 = 2
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x034e
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x034e
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            int r2 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.ad = r2     // Catch:{ Exception -> 0x0405 }
        L_0x034e:
            r2 = 3
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x0363
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x0363
            r2 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.af = r2     // Catch:{ Exception -> 0x0405 }
        L_0x0363:
            r2 = 4
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x0390
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x0390
            r2 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ Exception -> 0x0405 }
            if (r2 <= 0) goto L_0x038e
            long r2 = (long) r2     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.al = r2     // Catch:{ Exception -> 0x0405 }
            long r2 = com.baidu.location.g.k.al     // Catch:{ Exception -> 0x0405 }
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 * r4
            r4 = 60
            long r2 = r2 * r4
            com.baidu.location.g.k.ah = r2     // Catch:{ Exception -> 0x0405 }
            long r2 = com.baidu.location.g.k.ah     // Catch:{ Exception -> 0x0405 }
            r4 = 2
            long r2 = r2 >> r4
            com.baidu.location.g.k.am = r2     // Catch:{ Exception -> 0x0405 }
            goto L_0x0390
        L_0x038e:
            com.baidu.location.g.k.p = r11     // Catch:{ Exception -> 0x0405 }
        L_0x0390:
            r2 = 5
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            if (r3 == 0) goto L_0x03a5
            r3 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            boolean r3 = r3.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r3 != 0) goto L_0x03a5
            r0 = r0[r2]     // Catch:{ Exception -> 0x0405 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.ao = r0     // Catch:{ Exception -> 0x0405 }
        L_0x03a5:
            boolean r0 = r12.has(r1)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x03f4
            java.lang.String r0 = r12.getString(r1)     // Catch:{ Exception -> 0x0405 }
            java.lang.String[] r0 = r0.split(r13)     // Catch:{ Exception -> 0x0405 }
            int r1 = r0.length     // Catch:{ Exception -> 0x0405 }
            r2 = 2
            if (r1 <= r2) goto L_0x03f4
            r1 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            if (r1 == 0) goto L_0x03cb
            r1 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            boolean r1 = r1.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r1 != 0) goto L_0x03cb
            r1 = r0[r11]     // Catch:{ Exception -> 0x0405 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.ap = r1     // Catch:{ Exception -> 0x0405 }
        L_0x03cb:
            r1 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            if (r1 == 0) goto L_0x03df
            r1 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            boolean r1 = r1.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r1 != 0) goto L_0x03df
            r1 = r0[r10]     // Catch:{ Exception -> 0x0405 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.aq = r1     // Catch:{ Exception -> 0x0405 }
        L_0x03df:
            r1 = 2
            r2 = r0[r1]     // Catch:{ Exception -> 0x0405 }
            if (r2 == 0) goto L_0x03f4
            r2 = r0[r1]     // Catch:{ Exception -> 0x0405 }
            boolean r2 = r2.equals(r14)     // Catch:{ Exception -> 0x0405 }
            if (r2 != 0) goto L_0x03f4
            r0 = r0[r1]     // Catch:{ Exception -> 0x0405 }
            float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.ar = r0     // Catch:{ Exception -> 0x0405 }
        L_0x03f4:
            java.lang.String r0 = "dmx"
            boolean r0 = r12.has(r0)     // Catch:{ Exception -> 0x0405 }
            if (r0 == 0) goto L_0x040e
            java.lang.String r0 = "dmx"
            int r0 = r12.getInt(r0)     // Catch:{ Exception -> 0x0405 }
            com.baidu.location.g.k.an = r0     // Catch:{ Exception -> 0x0405 }
            goto L_0x040e
        L_0x0405:
            r0 = move-exception
            goto L_0x040f
        L_0x0407:
            r0 = move-exception
            r9 = r17
            goto L_0x040f
        L_0x040b:
            r9 = r17
        L_0x040d:
            r10 = 0
        L_0x040e:
            r11 = r10
        L_0x040f:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.e.a(java.lang.String):boolean");
    }

    private void b(int i2) {
        File file = new File(l);
        if (!file.exists()) {
            i();
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(4);
            int readInt = randomAccessFile.readInt();
            int readInt2 = randomAccessFile.readInt();
            randomAccessFile.seek((long) ((readInt * o) + 128));
            byte[] bytes = (b.e + 0).getBytes();
            randomAccessFile.writeInt(bytes.length);
            randomAccessFile.write(bytes, 0, bytes.length);
            randomAccessFile.writeInt(i2);
            if (readInt2 == o) {
                randomAccessFile.seek(8);
                randomAccessFile.writeInt(readInt2 + 1);
            }
            randomAccessFile.close();
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void b(String str) {
        int i2;
        n = -1;
        if (str != null) {
            try {
                if (a(str)) {
                    f();
                }
            } catch (Exception e2) {
            }
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("ctr")) {
                    n = Integer.parseInt(jSONObject.getString("ctr"));
                }
            } catch (Exception e3) {
            }
            try {
                j();
                if (n != -1) {
                    i2 = n;
                    b(n);
                } else {
                    i2 = m != -1 ? m : -1;
                }
                if (i2 != -1) {
                    a(i2);
                }
            } catch (Exception e4) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        String str = "&ver=" + k.y + "&usr=" + b.a().b() + "&app=" + b.e + "&prod=" + b.f;
        if (this.j == null) {
            this.j = new a();
        }
        if (!k.b()) {
            this.j.a(str, false);
        }
    }

    private void f() {
        String str = j.a + "/config.dat";
        byte[] bytes = String.format(Locale.CHINA, "{\"ver\":\"%d\",\"gps\":\"%.1f|%.1f|%.1f|%.1f|%d|%d|%d|%d|%d|%d|%d\",\"up\":\"%.1f|%.1f|%.1f|%.1f\",\"wf\":\"%d|%.1f|%d|%.1f\",\"ab\":\"%.2f|%.2f|%d|%d\",\"gpc\":\"%d|%d|%d|%d|%d|%d\",\"zxd\":\"%.1f|%.1f|%d|%d|%d\",\"shak\":\"%d|%d|%.1f\",\"dmx\":%d}", new Object[]{Integer.valueOf(k.y), Float.valueOf(k.z), Float.valueOf(k.A), Float.valueOf(k.B), Float.valueOf(k.C), Integer.valueOf(k.D), Integer.valueOf(k.E), Integer.valueOf(k.F), Integer.valueOf(k.G), Integer.valueOf(k.H), Integer.valueOf(k.I), Integer.valueOf(k.J), Float.valueOf(k.K), Float.valueOf(k.L), Float.valueOf(k.M), Float.valueOf(k.N), Integer.valueOf(k.O), Float.valueOf(k.Q), Integer.valueOf(k.R), Float.valueOf(k.S), Float.valueOf(k.T), Float.valueOf(k.U), Integer.valueOf(k.V), Integer.valueOf(k.W), Integer.valueOf(k.ab ? 1 : 0), Integer.valueOf(k.ac ? 1 : 0), Integer.valueOf(k.ad), Integer.valueOf(k.af), Long.valueOf(k.al), Integer.valueOf(k.ao), Float.valueOf(k.as), Float.valueOf(k.at), Integer.valueOf(k.au), Integer.valueOf(k.av), Integer.valueOf(k.aw), Integer.valueOf(k.ap), Integer.valueOf(k.aq), Float.valueOf(k.ar), Integer.valueOf(k.an)}).getBytes();
        try {
            File file = new File(str);
            if (!file.exists()) {
                File file2 = new File(j.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(0);
            randomAccessFile2.writeBoolean(true);
            randomAccessFile2.seek(2);
            randomAccessFile2.writeInt(bytes.length);
            randomAccessFile2.write(bytes);
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    private void g() {
        try {
            File file = new File(j.a + "/config.dat");
            if (!file.exists()) {
                File file2 = new File(j.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(1);
            randomAccessFile2.writeBoolean(true);
            randomAccessFile2.seek(1024);
            randomAccessFile2.writeDouble(k.t);
            randomAccessFile2.writeDouble(k.u);
            randomAccessFile2.writeBoolean(k.x);
            if (k.x && k.w != null) {
                randomAccessFile2.write(k.w);
            }
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    private void h() {
        try {
            File file = new File(j.a + "/config.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                if (randomAccessFile.readBoolean()) {
                    randomAccessFile.seek(2);
                    int readInt = randomAccessFile.readInt();
                    byte[] bArr = new byte[readInt];
                    randomAccessFile.read(bArr, 0, readInt);
                    a(new String(bArr));
                }
                randomAccessFile.seek(1);
                if (randomAccessFile.readBoolean()) {
                    randomAccessFile.seek(1024);
                    k.t = randomAccessFile.readDouble();
                    k.u = randomAccessFile.readDouble();
                    k.x = randomAccessFile.readBoolean();
                    if (k.x) {
                        k.w = new byte[625];
                        randomAccessFile.read(k.w, 0, 625);
                    }
                }
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
        if (k.p) {
            boolean z = f.isServing;
        }
    }

    private void i() {
        try {
            File file = new File(l);
            if (!file.exists()) {
                File file2 = new File(j.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (!file.createNewFile()) {
                    file = null;
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(0);
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(128);
                randomAccessFile.writeInt(0);
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    private void j() {
        try {
            File file = new File(l);
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(4);
                int readInt = randomAccessFile.readInt();
                if (readInt > 3000) {
                    randomAccessFile.close();
                    o = 0;
                    i();
                    return;
                }
                int readInt2 = randomAccessFile.readInt();
                randomAccessFile.seek(128);
                byte[] bArr = new byte[readInt];
                int i2 = 0;
                while (true) {
                    if (i2 >= readInt2) {
                        break;
                    }
                    randomAccessFile.seek((long) ((readInt * i2) + 128));
                    int readInt3 = randomAccessFile.readInt();
                    if (readInt3 > 0 && readInt3 < readInt) {
                        randomAccessFile.read(bArr, 0, readInt3);
                        int i3 = readInt3 - 1;
                        if (bArr[i3] == 0) {
                            String str = new String(bArr, 0, i3);
                            b.a();
                            if (str.equals(b.e)) {
                                m = randomAccessFile.readInt();
                                o = i2;
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                    i2++;
                }
                if (i2 == readInt2) {
                    o = readInt2;
                }
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    public void b() {
        h();
    }

    public void c() {
    }

    public void d() {
        if (System.currentTimeMillis() - c.a().d() > 604800000) {
            c.a().c(System.currentTimeMillis());
            com.baidu.location.f.a.a().postDelayed(new f(this), 1000);
        }
    }
}
