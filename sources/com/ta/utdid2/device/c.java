package com.ta.utdid2.device;

import android.content.Context;
import android.os.Binder;
import android.provider.Settings;
import android.text.TextUtils;
import com.ta.utdid2.a.a.b;
import com.ta.utdid2.a.a.d;
import com.ta.utdid2.a.a.e;
import com.ta.utdid2.a.a.f;
import com.ta.utdid2.a.a.g;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class c {
    private static c a = null;
    private static final Object e = new Object();
    private static final String k = (".UTSystemConfig" + File.separator + "Global");

    /* renamed from: a  reason: collision with other field name */
    private com.ta.utdid2.b.a.c f20a = null;

    /* renamed from: a  reason: collision with other field name */
    private d f21a = null;
    private com.ta.utdid2.b.a.c b = null;

    /* renamed from: b  reason: collision with other field name */
    private Pattern f22b = Pattern.compile("[^0-9a-zA-Z=/+]+");
    private String h = null;
    private String i = "xx_utdid_key";
    private String j = "xx_utdid_domain";
    private Context mContext = null;

    private c(Context context) {
        this.mContext = context;
        this.b = new com.ta.utdid2.b.a.c(context, k, "Alvin2", false, true);
        this.f20a = new com.ta.utdid2.b.a.c(context, ".DataStorage", "ContextData", false, true);
        this.f21a = new d();
        this.i = String.format("K_%d", new Object[]{Integer.valueOf(g.a(this.i))});
        this.j = String.format("D_%d", new Object[]{Integer.valueOf(g.a(this.j))});
    }

    private void c() {
        com.ta.utdid2.b.a.c cVar = this.b;
        if (cVar != null) {
            if (g.a(cVar.getString("UTDID2"))) {
                String string = this.b.getString("UTDID");
                if (!g.a(string)) {
                    f(string);
                }
            }
            boolean z = false;
            boolean z2 = true;
            if (!g.a(this.b.getString("DID"))) {
                this.b.remove("DID");
                z = true;
            }
            if (!g.a(this.b.getString("EI"))) {
                this.b.remove("EI");
                z = true;
            }
            if (!g.a(this.b.getString("SI"))) {
                this.b.remove("SI");
            } else {
                z2 = z;
            }
            if (z2) {
                this.b.commit();
            }
        }
    }

    public static c a(Context context) {
        if (context != null && a == null) {
            synchronized (e) {
                if (a == null) {
                    c cVar = new c(context);
                    a = cVar;
                    cVar.c();
                }
            }
        }
        return a;
    }

    private void f(String str) {
        com.ta.utdid2.b.a.c cVar;
        if (b(str)) {
            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            }
            if (str.length() == 24 && (cVar = this.b) != null) {
                cVar.putString("UTDID2", str);
                this.b.commit();
            }
        }
    }

    private void g(String str) {
        com.ta.utdid2.b.a.c cVar;
        if (str != null && (cVar = this.f20a) != null && !str.equals(cVar.getString(this.i))) {
            this.f20a.putString(this.i, str);
            this.f20a.commit();
        }
    }

    private void h(String str) {
        if (f() && b(str)) {
            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            }
            if (24 == str.length()) {
                String str2 = null;
                try {
                    str2 = Settings.System.getString(this.mContext.getContentResolver(), "mqBRboGZkQPcAkyk");
                } catch (Exception e2) {
                }
                if (!b(str2)) {
                    try {
                        Settings.System.putString(this.mContext.getContentResolver(), "mqBRboGZkQPcAkyk", str);
                    } catch (Exception e3) {
                    }
                }
            }
        }
    }

    private void i(String str) {
        String str2;
        try {
            str2 = Settings.System.getString(this.mContext.getContentResolver(), "dxCRMxhQkdGePGnp");
        } catch (Exception e2) {
            str2 = null;
        }
        if (!str.equals(str2)) {
            try {
                Settings.System.putString(this.mContext.getContentResolver(), "dxCRMxhQkdGePGnp", str);
            } catch (Exception e3) {
            }
        }
    }

    private void j(String str) {
        if (f() && str != null) {
            i(str);
        }
    }

    private String g() {
        com.ta.utdid2.b.a.c cVar = this.b;
        if (cVar == null) {
            return null;
        }
        String string = cVar.getString("UTDID2");
        if (g.a(string) || this.f21a.c(string) == null) {
            return null;
        }
        return string;
    }

    private boolean b(String str) {
        if (str != null) {
            if (str.endsWith("\n")) {
                str = str.substring(0, str.length() - 1);
            }
            if (24 != str.length() || this.f22b.matcher(str).find()) {
                return false;
            }
            return true;
        }
        return false;
    }

    public synchronized String getValue() {
        if (this.h != null) {
            return this.h;
        }
        return h();
    }

    public synchronized String h() {
        String i2 = i();
        this.h = i2;
        if (!TextUtils.isEmpty(i2)) {
            return this.h;
        }
        try {
            byte[] c = c();
            if (c != null) {
                String encodeToString = b.encodeToString(c, 2);
                this.h = encodeToString;
                f(encodeToString);
                String c2 = this.f21a.c(c);
                if (c2 != null) {
                    j(c2);
                    g(c2);
                }
                return this.h;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00eb, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.String i() {
        /*
            r6 = this;
            monitor-enter(r6)
            java.lang.String r0 = ""
            android.content.Context r1 = r6.mContext     // Catch:{ Exception -> 0x0010 }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ Exception -> 0x0010 }
            java.lang.String r2 = "mqBRboGZkQPcAkyk"
            java.lang.String r0 = android.provider.Settings.System.getString(r1, r2)     // Catch:{ Exception -> 0x0010 }
            goto L_0x0011
        L_0x0010:
            r1 = move-exception
        L_0x0011:
            boolean r1 = r6.b((java.lang.String) r0)     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x0019
            monitor-exit(r6)
            return r0
        L_0x0019:
            com.ta.utdid2.device.e r0 = new com.ta.utdid2.device.e     // Catch:{ all -> 0x00ec }
            r0.<init>()     // Catch:{ all -> 0x00ec }
            r1 = 0
            r2 = 0
            android.content.Context r3 = r6.mContext     // Catch:{ Exception -> 0x002e }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Exception -> 0x002e }
            java.lang.String r4 = "dxCRMxhQkdGePGnp"
            java.lang.String r3 = android.provider.Settings.System.getString(r3, r4)     // Catch:{ Exception -> 0x002e }
            goto L_0x0030
        L_0x002e:
            r3 = move-exception
            r3 = r2
        L_0x0030:
            boolean r4 = com.ta.utdid2.a.a.g.a((java.lang.String) r3)     // Catch:{ all -> 0x00ec }
            if (r4 != 0) goto L_0x008a
            java.lang.String r4 = r0.e(r3)     // Catch:{ all -> 0x00ec }
            boolean r5 = r6.b((java.lang.String) r4)     // Catch:{ all -> 0x00ec }
            if (r5 == 0) goto L_0x0045
            r6.h(r4)     // Catch:{ all -> 0x00ec }
            monitor-exit(r6)
            return r4
        L_0x0045:
            java.lang.String r4 = r0.d(r3)     // Catch:{ all -> 0x00ec }
            boolean r5 = r6.b((java.lang.String) r4)     // Catch:{ all -> 0x00ec }
            if (r5 == 0) goto L_0x006c
            com.ta.utdid2.device.d r5 = r6.f21a     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = r5.c((java.lang.String) r4)     // Catch:{ all -> 0x00ec }
            boolean r5 = com.ta.utdid2.a.a.g.a((java.lang.String) r4)     // Catch:{ all -> 0x00ec }
            if (r5 != 0) goto L_0x006c
            r6.j(r4)     // Catch:{ all -> 0x00ec }
            android.content.Context r4 = r6.mContext     // Catch:{ Exception -> 0x006b }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ Exception -> 0x006b }
            java.lang.String r5 = "dxCRMxhQkdGePGnp"
            java.lang.String r3 = android.provider.Settings.System.getString(r4, r5)     // Catch:{ Exception -> 0x006b }
            goto L_0x006c
        L_0x006b:
            r4 = move-exception
        L_0x006c:
            com.ta.utdid2.device.d r4 = r6.f21a     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = r4.d(r3)     // Catch:{ all -> 0x00ec }
            boolean r5 = r6.b((java.lang.String) r4)     // Catch:{ all -> 0x00ec }
            if (r5 == 0) goto L_0x0089
            r6.h = r4     // Catch:{ all -> 0x00ec }
            r6.f(r4)     // Catch:{ all -> 0x00ec }
            r6.g(r3)     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r6.h     // Catch:{ all -> 0x00ec }
            r6.h(r0)     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r6.h     // Catch:{ all -> 0x00ec }
            monitor-exit(r6)
            return r0
        L_0x0089:
            goto L_0x008b
        L_0x008a:
            r1 = 1
        L_0x008b:
            java.lang.String r3 = r6.g()     // Catch:{ all -> 0x00ec }
            boolean r4 = r6.b((java.lang.String) r3)     // Catch:{ all -> 0x00ec }
            if (r4 == 0) goto L_0x00aa
            com.ta.utdid2.device.d r0 = r6.f21a     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r0.c((java.lang.String) r3)     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00a0
            r6.j(r0)     // Catch:{ all -> 0x00ec }
        L_0x00a0:
            r6.h(r3)     // Catch:{ all -> 0x00ec }
            r6.g(r0)     // Catch:{ all -> 0x00ec }
            r6.h = r3     // Catch:{ all -> 0x00ec }
            monitor-exit(r6)
            return r3
        L_0x00aa:
            com.ta.utdid2.b.a.c r3 = r6.f20a     // Catch:{ all -> 0x00ec }
            java.lang.String r4 = r6.i     // Catch:{ all -> 0x00ec }
            java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x00ec }
            boolean r4 = com.ta.utdid2.a.a.g.a((java.lang.String) r3)     // Catch:{ all -> 0x00ec }
            if (r4 != 0) goto L_0x00ea
            java.lang.String r0 = r0.d(r3)     // Catch:{ all -> 0x00ec }
            boolean r4 = r6.b((java.lang.String) r0)     // Catch:{ all -> 0x00ec }
            if (r4 != 0) goto L_0x00c8
            com.ta.utdid2.device.d r0 = r6.f21a     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r0.d(r3)     // Catch:{ all -> 0x00ec }
        L_0x00c8:
            boolean r3 = r6.b((java.lang.String) r0)     // Catch:{ all -> 0x00ec }
            if (r3 == 0) goto L_0x00ea
            com.ta.utdid2.device.d r3 = r6.f21a     // Catch:{ all -> 0x00ec }
            java.lang.String r3 = r3.c((java.lang.String) r0)     // Catch:{ all -> 0x00ec }
            boolean r4 = com.ta.utdid2.a.a.g.a((java.lang.String) r0)     // Catch:{ all -> 0x00ec }
            if (r4 != 0) goto L_0x00ea
            r6.h = r0     // Catch:{ all -> 0x00ec }
            if (r1 == 0) goto L_0x00e1
            r6.j(r3)     // Catch:{ all -> 0x00ec }
        L_0x00e1:
            java.lang.String r0 = r6.h     // Catch:{ all -> 0x00ec }
            r6.f(r0)     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = r6.h     // Catch:{ all -> 0x00ec }
            monitor-exit(r6)
            return r0
        L_0x00ea:
            monitor-exit(r6)
            return r2
        L_0x00ec:
            r0 = move-exception
            monitor-exit(r6)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.device.c.i():java.lang.String");
    }

    /* renamed from: c  reason: collision with other method in class */
    private byte[] m5c() throws Exception {
        String str;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
        int nextInt = new Random().nextInt();
        byte[] bytes = d.getBytes(currentTimeMillis);
        byte[] bytes2 = d.getBytes(nextInt);
        byteArrayOutputStream.write(bytes, 0, 4);
        byteArrayOutputStream.write(bytes2, 0, 4);
        byteArrayOutputStream.write(3);
        byteArrayOutputStream.write(0);
        try {
            str = e.a(this.mContext);
        } catch (Exception e2) {
            str = "" + new Random().nextInt();
        }
        byteArrayOutputStream.write(d.getBytes(g.a(str)), 0, 4);
        byteArrayOutputStream.write(d.getBytes(g.a(b(byteArrayOutputStream.toByteArray()))));
        return byteArrayOutputStream.toByteArray();
    }

    public static String b(byte[] bArr) throws Exception {
        Mac instance = Mac.getInstance("HmacSHA1");
        instance.init(new SecretKeySpec(f.a(new byte[]{69, 114, 116, -33, 125, -54, -31, 86, -11, 11, -78, -96, -17, -99, 64, 23, -95, -126, -82, -64, 113, 116, -16, -103, 49, -30, 9, -39, 33, -80, -68, -78, -117, 53, 30, -122, 64, -104, 74, -49, 106, 85, -38, -93}), instance.getAlgorithm()));
        return b.encodeToString(instance.doFinal(bArr), 2);
    }

    private boolean f() {
        return this.mContext.checkPermission("android.permission.WRITE_SETTINGS", Binder.getCallingPid(), Binder.getCallingUid()) == 0;
    }
}
