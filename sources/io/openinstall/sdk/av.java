package io.openinstall.sdk;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class av {
    private static volatile av a;
    private final aw b;
    private final ax c = new ax();
    private final h d;
    private Map<String, Object> e;

    private av(h hVar, aw awVar) {
        this.d = hVar;
        this.b = awVar;
    }

    public static av a(h hVar) {
        if (a == null) {
            synchronized (av.class) {
                if (a == null) {
                    a = new av(hVar, new aw());
                }
            }
        }
        return a;
    }

    private bb a(String str, String str2) {
        bb b2 = b(b(au.b(), str), str2, false);
        if (b2.e()) {
            au.a();
        }
        return b2;
    }

    private bb a(String str, String str2, boolean z) {
        bb b2 = b(b(au.c(), str), str2, z);
        if (b2.e()) {
            au.a();
        }
        return b2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x006e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Map<java.lang.String, java.lang.Object> a() {
        /*
            r8 = this;
            io.openinstall.sdk.h r0 = r8.d
            io.openinstall.sdk.j r0 = r0.d()
            io.openinstall.sdk.h r1 = r8.d
            io.openinstall.sdk.g r1 = r1.e()
            io.openinstall.sdk.h r2 = r8.d
            io.openinstall.sdk.z r2 = r2.g()
            io.openinstall.sdk.c r3 = io.openinstall.sdk.c.a()
            com.fm.openinstall.Configuration r3 = r3.e()
            io.openinstall.sdk.c r4 = io.openinstall.sdk.c.a()
            java.lang.String r4 = r4.c()
            java.util.Map<java.lang.String, java.lang.Object> r5 = r8.e
            if (r5 != 0) goto L_0x00b0
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r8.e = r5
            java.lang.String r5 = r3.getSerialNumber()
            boolean r5 = com.fm.openinstall.Configuration.isPresent(r5)
            java.lang.String r6 = "sN"
            if (r5 == 0) goto L_0x0043
            java.util.Map<java.lang.String, java.lang.Object> r5 = r8.e
            java.lang.String r7 = r3.getSerialNumber()
        L_0x003f:
            r5.put(r6, r7)
            goto L_0x0058
        L_0x0043:
            io.openinstall.sdk.c r5 = io.openinstall.sdk.c.a()
            java.lang.Boolean r5 = r5.h()
            boolean r5 = r5.booleanValue()
            if (r5 == 0) goto L_0x0058
            java.util.Map<java.lang.String, java.lang.Object> r5 = r8.e
            java.lang.String r7 = r0.b()
            goto L_0x003f
        L_0x0058:
            java.lang.String r5 = r3.getAndroidId()
            boolean r5 = com.fm.openinstall.Configuration.isPresent(r5)
            java.lang.String r6 = "andI"
            if (r5 == 0) goto L_0x006e
            java.util.Map<java.lang.String, java.lang.Object> r5 = r8.e
            java.lang.String r3 = r3.getAndroidId()
            r5.put(r6, r3)
            goto L_0x0077
        L_0x006e:
            java.util.Map<java.lang.String, java.lang.Object> r3 = r8.e
            java.lang.String r5 = r0.a()
            r3.put(r6, r5)
        L_0x0077:
            java.util.Map<java.lang.String, java.lang.Object> r3 = r8.e
            java.lang.String r5 = r0.d()
            java.lang.String r6 = "Pk"
            r3.put(r6, r5)
            java.util.Map<java.lang.String, java.lang.Object> r3 = r8.e
            java.lang.String r5 = r0.c()
            java.lang.String r6 = "cF"
            r3.put(r6, r5)
            java.util.Map<java.lang.String, java.lang.Object> r3 = r8.e
            java.lang.String r5 = r0.e()
            java.lang.String r6 = "ver"
            r3.put(r6, r5)
            java.util.Map<java.lang.String, java.lang.Object> r3 = r8.e
            java.lang.Integer r0 = r0.f()
            java.lang.String r0 = java.lang.String.valueOf(r0)
            java.lang.String r5 = "verI"
            r3.put(r5, r0)
            java.util.Map<java.lang.String, java.lang.Object> r0 = r8.e
            java.lang.String r3 = "apV"
            java.lang.String r5 = "2.8.1"
            r0.put(r3, r5)
        L_0x00b0:
            java.lang.String r0 = r1.h()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x00bf
            java.lang.String r0 = r2.a((java.lang.String) r4)
            goto L_0x00c3
        L_0x00bf:
            java.lang.String r0 = r1.h()
        L_0x00c3:
            java.util.Map<java.lang.String, java.lang.Object> r1 = r8.e
            java.lang.String r2 = "iI"
            r1.put(r2, r0)
            java.util.Map<java.lang.String, java.lang.Object> r0 = r8.e
            long r1 = java.lang.System.currentTimeMillis()
            java.lang.String r1 = java.lang.String.valueOf(r1)
            java.lang.String r2 = "ts"
            r0.put(r2, r1)
            java.util.Map<java.lang.String, java.lang.Object> r0 = r8.e
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.av.a():java.util.Map");
    }

    private bb b(String str, String str2, boolean z) {
        String e2 = e(a());
        byte[] bytes = str2.getBytes(ac.c);
        HashMap hashMap = new HashMap();
        if (z) {
            hashMap.put("content-type", "text/plain;charset=utf-8");
            hashMap.put("content-length", String.valueOf(bytes.length));
        }
        return this.b.a(str, e2, bytes, hashMap);
    }

    private String b(String str, String str2) {
        String c2 = c.a().c();
        return "https://" + str + "/api/" + "v2_5" + "/android/" + c2 + str2;
    }

    private String e(Map<String, ?> map) {
        if (map == null) {
            return "";
        }
        HashMap hashMap = new HashMap(map);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : hashMap.entrySet()) {
            String str = (String) entry.getKey();
            Object value = entry.getValue();
            if (!(str == null || value == null)) {
                String a2 = this.c.a(str);
                if (!TextUtils.isEmpty(a2)) {
                    if (value instanceof String) {
                        String b2 = this.c.b((String) value);
                        if (!TextUtils.isEmpty(b2)) {
                            sb.append(a2);
                            sb.append("=");
                            sb.append(b2);
                            sb.append("&");
                        }
                    } else if (value instanceof List) {
                        for (String b3 : (List) value) {
                            String b4 = this.c.b(b3);
                            if (!TextUtils.isEmpty(b4)) {
                                sb.append(a2);
                                sb.append("=");
                                sb.append(b4);
                                sb.append("&");
                            }
                        }
                    }
                }
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public bb a(String str) {
        return a("/stats/events", str, true);
    }

    public bb a(Map<String, ?> map) {
        return a("/init", e(map));
    }

    public bb b(Map<String, ?> map) {
        return a("/decode-wakeup-url", e(map));
    }

    public bb c(Map<String, ?> map) {
        return a("/stats/wakeup", e(map), false);
    }

    public bb d(Map<String, ?> map) {
        return a("/share/report", e(map), false);
    }
}
