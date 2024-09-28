package com.baidu.location.c;

import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.util.Base64;
import com.baidu.location.Jni;
import com.baidu.location.b.v;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.json.JSONArray;
import org.json.JSONObject;

public class h {
    private static Object a = new Object();
    private static h b = null;
    private Handler c = null;
    /* access modifiers changed from: private */
    public String d = null;
    private int e = 24;
    private a f = null;
    private long g = 0;

    private class a extends e {
        private boolean b = false;
        private int c = 0;
        private JSONArray d = null;
        private JSONArray e = null;

        a() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = k.g();
            this.k.clear();
            this.k.put("qt", "cltrw");
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("data", this.d);
                jSONObject.put("frt", this.c);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            String encodeOfflineLocationUpdateRequest = Jni.encodeOfflineLocationUpdateRequest(jSONObject.toString());
            Map map = this.k;
            map.put("cltr[0]", "" + encodeOfflineLocationUpdateRequest);
            this.k.put("cfg", 1);
            this.k.put("info", Jni.encode(com.baidu.location.g.b.a().c()));
            this.k.put("trtm", String.format(Locale.CHINA, "%d", new Object[]{Long.valueOf(System.currentTimeMillis())}));
        }

        public void a(boolean z) {
            boolean z2;
            JSONObject jSONObject;
            if (z && this.j != null) {
                try {
                    jSONObject = new JSONObject(this.j);
                    z2 = true;
                } catch (Exception e2) {
                    jSONObject = null;
                    z2 = false;
                }
                if (z2 && jSONObject != null) {
                    try {
                        jSONObject.put(TtmlNode.TAG_TT, System.currentTimeMillis());
                        jSONObject.put("data", this.e);
                        try {
                            File file = new File(h.this.d, "wcnf.dat");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
                            bufferedWriter.write(new String(Base64.encode(jSONObject.toString().getBytes(), 0), "UTF-8"));
                            bufferedWriter.flush();
                            bufferedWriter.close();
                        } catch (Exception e3) {
                            e3.printStackTrace();
                        }
                    } catch (Exception e4) {
                    }
                }
            }
            this.b = false;
        }

        public void a(boolean z, JSONArray jSONArray, JSONArray jSONArray2) {
            if (!this.b) {
                this.b = true;
                if (z) {
                    this.c = 1;
                } else {
                    this.c = 0;
                }
                this.d = jSONArray;
                this.e = jSONArray2;
                ExecutorService c2 = v.a().c();
                if (c2 != null) {
                    a(c2, k.g());
                } else {
                    c(k.g());
                }
            }
        }
    }

    private class b {
        public String a = null;
        public int b = 0;

        b(String str, int i) {
            this.a = str;
            this.b = i;
        }
    }

    public static h a() {
        h hVar;
        synchronized (a) {
            if (b == null) {
                b = new h();
            }
            hVar = b;
        }
        return hVar;
    }

    private Object a(Object obj, String str) throws Exception {
        return obj.getClass().getField(str).get(obj);
    }

    private List<b> a(List<WifiConfiguration> list) {
        int i;
        if (list == null || list.size() <= 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (WifiConfiguration next : list) {
            String str = next.SSID;
            try {
                i = ((Integer) a(next, "numAssociation")).intValue();
            } catch (Throwable th) {
                i = 0;
            }
            if (i > 0 && str != null) {
                arrayList.add(new b(str, i));
            }
        }
        return arrayList;
    }

    private void a(boolean z, JSONArray jSONArray, JSONArray jSONArray2) {
        if (this.f == null) {
            this.f = new a();
        }
        if (!k.b()) {
            this.f.a(z, jSONArray, jSONArray2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x015d A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:106:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:107:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00de A[Catch:{ Exception -> 0x01c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f6 A[Catch:{ Exception -> 0x01c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0197 A[Catch:{ Exception -> 0x01c3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01bd A[ADDED_TO_REGION, Catch:{ Exception -> 0x01c3 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void d() {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "data"
            java.lang.String r2 = "frq"
            java.lang.String r3 = "tt"
            java.lang.String r4 = "cfg"
            java.lang.String r5 = "ison"
            java.lang.String r6 = r1.d
            if (r6 == 0) goto L_0x01c7
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r7 = r1.d     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r8 = "wcnf.dat"
            r6.<init>(r7, r8)     // Catch:{ Exception -> 0x01c3 }
            long r7 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x01c3 }
            boolean r9 = r6.exists()     // Catch:{ Exception -> 0x01c3 }
            r10 = 0
            java.lang.String r13 = "num"
            r14 = 0
            java.lang.String r12 = "ssid"
            if (r9 == 0) goto L_0x00d9
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00d2 }
            java.io.FileReader r15 = new java.io.FileReader     // Catch:{ Exception -> 0x00d2 }
            r15.<init>(r6)     // Catch:{ Exception -> 0x00d2 }
            r9.<init>(r15)     // Catch:{ Exception -> 0x00d2 }
            java.lang.StringBuffer r6 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x00d2 }
            r6.<init>()     // Catch:{ Exception -> 0x00d2 }
        L_0x0039:
            java.lang.String r15 = r9.readLine()     // Catch:{ Exception -> 0x00d2 }
            if (r15 == 0) goto L_0x0043
            r6.append(r15)     // Catch:{ Exception -> 0x00d2 }
            goto L_0x0039
        L_0x0043:
            r9.close()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x00d2 }
            if (r6 == 0) goto L_0x00da
            java.lang.String r9 = new java.lang.String     // Catch:{ Exception -> 0x00d2 }
            byte[] r6 = r6.getBytes()     // Catch:{ Exception -> 0x00d2 }
            byte[] r6 = android.util.Base64.decode(r6, r14)     // Catch:{ Exception -> 0x00d2 }
            r9.<init>(r6)     // Catch:{ Exception -> 0x00d2 }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x00d2 }
            r6.<init>(r9)     // Catch:{ Exception -> 0x00d2 }
            boolean r9 = r6.has(r5)     // Catch:{ Exception -> 0x00d2 }
            if (r9 == 0) goto L_0x006c
            int r5 = r6.getInt(r5)     // Catch:{ Exception -> 0x00d2 }
            if (r5 != 0) goto L_0x006c
            r5 = 0
            goto L_0x006d
        L_0x006c:
            r5 = 1
        L_0x006d:
            boolean r9 = r6.has(r4)     // Catch:{ Exception -> 0x00cf }
            if (r9 == 0) goto L_0x0083
            org.json.JSONObject r4 = r6.getJSONObject(r4)     // Catch:{ Exception -> 0x00cf }
            boolean r9 = r4.has(r2)     // Catch:{ Exception -> 0x00cf }
            if (r9 == 0) goto L_0x0083
            int r2 = r4.getInt(r2)     // Catch:{ Exception -> 0x00cf }
            r1.e = r2     // Catch:{ Exception -> 0x00cf }
        L_0x0083:
            boolean r2 = r6.has(r3)     // Catch:{ Exception -> 0x00cf }
            if (r2 == 0) goto L_0x008d
            long r7 = r6.getLong(r3)     // Catch:{ Exception -> 0x00cf }
        L_0x008d:
            boolean r2 = r6.has(r0)     // Catch:{ Exception -> 0x00cf }
            if (r2 == 0) goto L_0x00cd
            org.json.JSONArray r0 = r6.getJSONArray(r0)     // Catch:{ Exception -> 0x00cf }
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x00cf }
            r2.<init>()     // Catch:{ Exception -> 0x00cf }
            int r3 = r0.length()     // Catch:{ Exception -> 0x00cb }
            r4 = 0
        L_0x00a1:
            if (r4 >= r3) goto L_0x00dc
            org.json.JSONObject r6 = r0.getJSONObject(r4)     // Catch:{ Exception -> 0x00cb }
            boolean r9 = r6.has(r12)     // Catch:{ Exception -> 0x00cb }
            if (r9 == 0) goto L_0x00c7
            boolean r9 = r6.has(r13)     // Catch:{ Exception -> 0x00cb }
            if (r9 == 0) goto L_0x00c7
            com.baidu.location.c.h$b r9 = new com.baidu.location.c.h$b     // Catch:{ Exception -> 0x00cb }
            java.lang.String r15 = r6.getString(r12)     // Catch:{ Exception -> 0x00cb }
            int r14 = r6.getInt(r13)     // Catch:{ Exception -> 0x00cb }
            r9.<init>(r15, r14)     // Catch:{ Exception -> 0x00cb }
            java.lang.String r6 = r6.getString(r12)     // Catch:{ Exception -> 0x00cb }
            r2.put(r6, r9)     // Catch:{ Exception -> 0x00cb }
        L_0x00c7:
            int r4 = r4 + 1
            r14 = 0
            goto L_0x00a1
        L_0x00cb:
            r0 = move-exception
            goto L_0x00d5
        L_0x00cd:
            r2 = 0
            goto L_0x00dc
        L_0x00cf:
            r0 = move-exception
            r2 = 0
            goto L_0x00d5
        L_0x00d2:
            r0 = move-exception
            r2 = 0
            r5 = 1
        L_0x00d5:
            r0.printStackTrace()     // Catch:{ Exception -> 0x01c3 }
            goto L_0x00dc
        L_0x00d9:
            r7 = r10
        L_0x00da:
            r2 = 0
            r5 = 1
        L_0x00dc:
            if (r5 != 0) goto L_0x00e4
            int r0 = r1.e     // Catch:{ Exception -> 0x01c3 }
            int r0 = r0 * 4
            r1.e = r0     // Catch:{ Exception -> 0x01c3 }
        L_0x00e4:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x01c3 }
            long r3 = r3 - r7
            int r0 = r1.e     // Catch:{ Exception -> 0x01c3 }
            int r0 = r0 * 60
            int r0 = r0 * 60
            int r0 = r0 * 1000
            long r5 = (long) r0     // Catch:{ Exception -> 0x01c3 }
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x01c7
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()     // Catch:{ Exception -> 0x01c3 }
            java.util.List r0 = r0.d()     // Catch:{ Exception -> 0x01c3 }
            java.util.List r0 = r1.a((java.util.List<android.net.wifi.WifiConfiguration>) r0)     // Catch:{ Exception -> 0x01c3 }
            int r3 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r3 != 0) goto L_0x0142
            if (r0 == 0) goto L_0x01b8
            int r2 = r0.size()     // Catch:{ Exception -> 0x01c3 }
            if (r2 <= 0) goto L_0x01b8
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x01c3 }
            r2.<init>()     // Catch:{ Exception -> 0x01c3 }
            org.json.JSONArray r3 = new org.json.JSONArray     // Catch:{ Exception -> 0x01c3 }
            r3.<init>()     // Catch:{ Exception -> 0x01c3 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x01c3 }
        L_0x011c:
            boolean r4 = r0.hasNext()     // Catch:{ Exception -> 0x01c3 }
            if (r4 == 0) goto L_0x013e
            java.lang.Object r4 = r0.next()     // Catch:{ Exception -> 0x01c3 }
            com.baidu.location.c.h$b r4 = (com.baidu.location.c.h.b) r4     // Catch:{ Exception -> 0x01c3 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x01c3 }
            r5.<init>()     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r6 = r4.a     // Catch:{ Exception -> 0x01c3 }
            r5.put(r12, r6)     // Catch:{ Exception -> 0x01c3 }
            int r4 = r4.b     // Catch:{ Exception -> 0x01c3 }
            r5.put(r13, r4)     // Catch:{ Exception -> 0x01c3 }
            r2.put(r5)     // Catch:{ Exception -> 0x01c3 }
            r3.put(r5)     // Catch:{ Exception -> 0x01c3 }
            goto L_0x011c
        L_0x013e:
            r12 = r2
            r14 = 1
            goto L_0x01bb
        L_0x0142:
            if (r0 == 0) goto L_0x01b8
            int r3 = r0.size()     // Catch:{ Exception -> 0x01c3 }
            if (r3 <= 0) goto L_0x01b8
            org.json.JSONArray r3 = new org.json.JSONArray     // Catch:{ Exception -> 0x01c3 }
            r3.<init>()     // Catch:{ Exception -> 0x01c3 }
            if (r2 == 0) goto L_0x01b9
            int r4 = r2.size()     // Catch:{ Exception -> 0x01c3 }
            if (r4 <= 0) goto L_0x01b9
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x01c3 }
            r16 = 0
        L_0x015d:
            boolean r4 = r0.hasNext()     // Catch:{ Exception -> 0x01c3 }
            if (r4 == 0) goto L_0x01b5
            java.lang.Object r4 = r0.next()     // Catch:{ Exception -> 0x01c3 }
            com.baidu.location.c.h$b r4 = (com.baidu.location.c.h.b) r4     // Catch:{ Exception -> 0x01c3 }
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x01c3 }
            r5.<init>()     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r6 = r4.a     // Catch:{ Exception -> 0x01c3 }
            r5.put(r12, r6)     // Catch:{ Exception -> 0x01c3 }
            int r6 = r4.b     // Catch:{ Exception -> 0x01c3 }
            r5.put(r13, r6)     // Catch:{ Exception -> 0x01c3 }
            r3.put(r5)     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r5 = r4.a     // Catch:{ Exception -> 0x01c3 }
            boolean r5 = r2.containsKey(r5)     // Catch:{ Exception -> 0x01c3 }
            if (r5 == 0) goto L_0x0194
            int r5 = r4.b     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r6 = r4.a     // Catch:{ Exception -> 0x01c3 }
            java.lang.Object r6 = r2.get(r6)     // Catch:{ Exception -> 0x01c3 }
            com.baidu.location.c.h$b r6 = (com.baidu.location.c.h.b) r6     // Catch:{ Exception -> 0x01c3 }
            int r6 = r6.b     // Catch:{ Exception -> 0x01c3 }
            if (r5 == r6) goto L_0x0192
            goto L_0x0194
        L_0x0192:
            r5 = 0
            goto L_0x0195
        L_0x0194:
            r5 = 1
        L_0x0195:
            if (r5 == 0) goto L_0x015d
            if (r16 != 0) goto L_0x019e
            org.json.JSONArray r16 = new org.json.JSONArray     // Catch:{ Exception -> 0x01c3 }
            r16.<init>()     // Catch:{ Exception -> 0x01c3 }
        L_0x019e:
            r5 = r16
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x01c3 }
            r6.<init>()     // Catch:{ Exception -> 0x01c3 }
            java.lang.String r7 = r4.a     // Catch:{ Exception -> 0x01c3 }
            r6.put(r12, r7)     // Catch:{ Exception -> 0x01c3 }
            int r4 = r4.b     // Catch:{ Exception -> 0x01c3 }
            r6.put(r13, r4)     // Catch:{ Exception -> 0x01c3 }
            r5.put(r6)     // Catch:{ Exception -> 0x01c3 }
            r16 = r5
            goto L_0x015d
        L_0x01b5:
            r12 = r16
            goto L_0x01ba
        L_0x01b8:
            r3 = 0
        L_0x01b9:
            r12 = 0
        L_0x01ba:
            r14 = 0
        L_0x01bb:
            if (r12 == 0) goto L_0x01c7
            if (r3 == 0) goto L_0x01c7
            r1.a(r14, r12, r3)     // Catch:{ Exception -> 0x01c3 }
            goto L_0x01c7
        L_0x01c3:
            r0 = move-exception
            r0.printStackTrace()
        L_0x01c7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.h.d():void");
    }

    public void b() {
        if (this.c == null) {
            this.c = new i(this);
        }
        this.d = k.i();
    }

    public void c() {
        Handler handler;
        if (System.currentTimeMillis() - this.g > 3600000 && (handler = this.c) != null) {
            handler.sendEmptyMessage(1);
            this.g = System.currentTimeMillis();
        }
    }
}
