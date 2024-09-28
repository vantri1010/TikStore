package com.baidu.location.b;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.baidu.location.Jni;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import com.googlecode.mp4parser.boxes.dece.BaseLocationBox;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.json.JSONObject;

public class h {
    private static Object c = new Object();
    private static h d = null;
    private static final String e = (k.j() + "/hst.db");
    a a = null;
    a b = null;
    /* access modifiers changed from: private */
    public SQLiteDatabase f = null;
    /* access modifiers changed from: private */
    public boolean g = false;
    private String h = null;
    private int i = -2;

    class a extends e {
        private String b = null;
        private String c = null;
        private boolean d = true;
        private boolean e = false;

        a() {
            this.k = new HashMap();
        }

        public void a() {
            this.i = 1;
            this.h = k.e();
            String encodeTp4 = Jni.encodeTp4(this.c);
            this.c = null;
            this.k.put(BaseLocationBox.TYPE, encodeTp4);
        }

        public void a(String str, String str2) {
            if (!h.this.g) {
                boolean unused = h.this.g = true;
                this.b = str;
                this.c = str2;
                ExecutorService c2 = v.a().c();
                if (c2 != null) {
                    a(c2, k.f);
                } else {
                    c(k.f);
                }
            }
        }

        public void a(boolean z) {
            if (z && this.j != null) {
                try {
                    String str = this.j;
                    if (this.d) {
                        JSONObject jSONObject = new JSONObject(str);
                        JSONObject jSONObject2 = jSONObject.has("content") ? jSONObject.getJSONObject("content") : null;
                        if (jSONObject2 != null && jSONObject2.has("imo")) {
                            Long valueOf = Long.valueOf(jSONObject2.getJSONObject("imo").getString("mac"));
                            int i = jSONObject2.getJSONObject("imo").getInt("mv");
                            if (Jni.encode3(this.b).longValue() == valueOf.longValue()) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(TtmlNode.TAG_TT, Integer.valueOf((int) (System.currentTimeMillis() / 1000)));
                                contentValues.put("hst", Integer.valueOf(i));
                                try {
                                    SQLiteDatabase a2 = h.this.f;
                                    if (a2.update("hstdata", contentValues, "id = \"" + valueOf + "\"", (String[]) null) <= 0) {
                                        contentValues.put(TtmlNode.ATTR_ID, valueOf);
                                        h.this.f.insert("hstdata", (String) null, contentValues);
                                    }
                                } catch (Exception e2) {
                                }
                                Bundle bundle = new Bundle();
                                bundle.putByteArray("mac", this.b.getBytes());
                                bundle.putInt("hotspot", i);
                                h.this.a(bundle);
                            }
                        }
                    }
                } catch (Exception e3) {
                }
            } else if (this.d) {
                h.this.f();
            }
            if (this.k != null) {
                this.k.clear();
            }
            boolean unused = h.this.g = false;
        }
    }

    public static h a() {
        h hVar;
        synchronized (c) {
            if (d == null) {
                d = new h();
            }
            hVar = d;
        }
        return hVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(boolean r5) {
        /*
            r4 = this;
            com.baidu.location.e.b r0 = com.baidu.location.e.b.a()
            com.baidu.location.e.a r0 = r0.f()
            com.baidu.location.e.j r1 = com.baidu.location.e.j.a()
            com.baidu.location.e.i r1 = r1.p()
            java.lang.StringBuffer r2 = new java.lang.StringBuffer
            r3 = 1024(0x400, float:1.435E-42)
            r2.<init>(r3)
            if (r0 == 0) goto L_0x0026
            boolean r3 = r0.b()
            if (r3 == 0) goto L_0x0026
            java.lang.String r0 = r0.h()
            r2.append(r0)
        L_0x0026:
            if (r1 == 0) goto L_0x0036
            int r0 = r1.a()
            r3 = 1
            if (r0 <= r3) goto L_0x0036
            r0 = 15
            java.lang.String r0 = r1.a((int) r0)
            goto L_0x0048
        L_0x0036:
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()
            java.lang.String r0 = r0.m()
            if (r0 == 0) goto L_0x004b
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()
            java.lang.String r0 = r0.m()
        L_0x0048:
            r2.append(r0)
        L_0x004b:
            if (r5 == 0) goto L_0x0052
            java.lang.String r5 = "&imo=1"
            r2.append(r5)
        L_0x0052:
            com.baidu.location.g.b r5 = com.baidu.location.g.b.a()
            r0 = 0
            java.lang.String r5 = r5.a((boolean) r0)
            r2.append(r5)
            com.baidu.location.b.a r5 = com.baidu.location.b.a.a()
            java.lang.String r5 = r5.d()
            r2.append(r5)
            java.lang.String r5 = r2.toString()
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.h.a(boolean):java.lang.String");
    }

    /* access modifiers changed from: private */
    public void a(Bundle bundle) {
        a.a().a(bundle, 406);
    }

    /* access modifiers changed from: private */
    public void f() {
        Bundle bundle = new Bundle();
        bundle.putInt("hotspot", -1);
        a(bundle);
    }

    public void a(String str) {
        if (!this.g) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONObject jSONObject2 = jSONObject.has("content") ? jSONObject.getJSONObject("content") : null;
                if (jSONObject2 != null && jSONObject2.has("imo")) {
                    Long valueOf = Long.valueOf(jSONObject2.getJSONObject("imo").getString("mac"));
                    int i2 = jSONObject2.getJSONObject("imo").getInt("mv");
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TtmlNode.TAG_TT, Integer.valueOf((int) (System.currentTimeMillis() / 1000)));
                    contentValues.put("hst", Integer.valueOf(i2));
                    SQLiteDatabase sQLiteDatabase = this.f;
                    if (sQLiteDatabase.update("hstdata", contentValues, "id = \"" + valueOf + "\"", (String[]) null) <= 0) {
                        contentValues.put(TtmlNode.ATTR_ID, valueOf);
                        this.f.insert("hstdata", (String) null, contentValues);
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public void b() {
        try {
            File file = new File(e);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (file.exists()) {
                SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(file, (SQLiteDatabase.CursorFactory) null);
                this.f = openOrCreateDatabase;
                openOrCreateDatabase.execSQL("CREATE TABLE IF NOT EXISTS hstdata(id Long PRIMARY KEY,hst INT,tt INT);");
                this.f.setVersion(1);
            }
        } catch (Exception e2) {
            this.f = null;
        }
    }

    public void c() {
        SQLiteDatabase sQLiteDatabase = this.f;
        if (sQLiteDatabase != null) {
            try {
                sQLiteDatabase.close();
            } catch (Exception e2) {
            } catch (Throwable th) {
                this.f = null;
                throw th;
            }
            this.f = null;
        }
    }

    /* JADX WARNING: type inference failed for: r3v2, types: [java.lang.String[], android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r3v3, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r3v4, types: [android.database.Cursor] */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0076, code lost:
        if (r3 != 0) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0088, code lost:
        if (r3 == 0) goto L_0x008b;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:40:0x0086=Splitter:B:40:0x0086, B:32:0x0078=Splitter:B:32:0x0078} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int d() {
        /*
            r8 = this;
            monitor-enter(r8)
            r0 = -3
            boolean r1 = r8.g     // Catch:{ all -> 0x008f }
            if (r1 == 0) goto L_0x0008
            monitor-exit(r8)
            return r0
        L_0x0008:
            boolean r1 = com.baidu.location.e.j.j()     // Catch:{ Exception -> 0x007c }
            if (r1 == 0) goto L_0x008b
            android.database.sqlite.SQLiteDatabase r1 = r8.f     // Catch:{ Exception -> 0x007c }
            if (r1 == 0) goto L_0x008b
            com.baidu.location.e.j r1 = com.baidu.location.e.j.a()     // Catch:{ Exception -> 0x007c }
            android.net.wifi.WifiInfo r1 = r1.l()     // Catch:{ Exception -> 0x007c }
            if (r1 == 0) goto L_0x008b
            java.lang.String r2 = r1.getBSSID()     // Catch:{ Exception -> 0x007c }
            if (r2 == 0) goto L_0x008b
            java.lang.String r1 = r1.getBSSID()     // Catch:{ Exception -> 0x007c }
            java.lang.String r2 = ":"
            java.lang.String r3 = ""
            java.lang.String r1 = r1.replace(r2, r3)     // Catch:{ Exception -> 0x007c }
            java.lang.Long r2 = com.baidu.location.Jni.encode3(r1)     // Catch:{ Exception -> 0x007c }
            java.lang.String r3 = r8.h     // Catch:{ Exception -> 0x007c }
            r4 = -2
            if (r3 == 0) goto L_0x0046
            java.lang.String r3 = r8.h     // Catch:{ Exception -> 0x007c }
            boolean r3 = r1.equals(r3)     // Catch:{ Exception -> 0x007c }
            if (r3 == 0) goto L_0x0046
            int r3 = r8.i     // Catch:{ Exception -> 0x007c }
            if (r3 <= r4) goto L_0x0046
            int r0 = r8.i     // Catch:{ Exception -> 0x007c }
            goto L_0x008b
        L_0x0046:
            r3 = 0
            android.database.sqlite.SQLiteDatabase r5 = r8.f     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            r6.<init>()     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            java.lang.String r7 = "select * from hstdata where id = \""
            r6.append(r7)     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            r6.append(r2)     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            java.lang.String r2 = "\";"
            r6.append(r2)     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            java.lang.String r2 = r6.toString()     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            android.database.Cursor r3 = r5.rawQuery(r2, r3)     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            if (r3 == 0) goto L_0x0075
            boolean r2 = r3.moveToFirst()     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            if (r2 == 0) goto L_0x0075
            r2 = 1
            int r0 = r3.getInt(r2)     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            r8.h = r1     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            r8.i = r0     // Catch:{ Exception -> 0x0087, all -> 0x007e }
            goto L_0x0076
        L_0x0075:
            r0 = -2
        L_0x0076:
            if (r3 == 0) goto L_0x008b
        L_0x0078:
            r3.close()     // Catch:{ Exception -> 0x007c }
            goto L_0x008b
        L_0x007c:
            r1 = move-exception
            goto L_0x008b
        L_0x007e:
            r1 = move-exception
            if (r3 == 0) goto L_0x0086
            r3.close()     // Catch:{ Exception -> 0x0085 }
            goto L_0x0086
        L_0x0085:
            r2 = move-exception
        L_0x0086:
            throw r1     // Catch:{ Exception -> 0x007c }
        L_0x0087:
            r1 = move-exception
            if (r3 == 0) goto L_0x008b
            goto L_0x0078
        L_0x008b:
            r8.i = r0     // Catch:{ all -> 0x008f }
            monitor-exit(r8)
            return r0
        L_0x008f:
            r0 = move-exception
            monitor-exit(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.h.d():int");
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [java.lang.String[], android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r3v1, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r3v2, types: [android.database.Cursor] */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0088, code lost:
        if (r3 == 0) goto L_0x009d;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void e() {
        /*
            r10 = this;
            boolean r0 = r10.g
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            boolean r0 = com.baidu.location.e.j.j()     // Catch:{ Exception -> 0x00bc }
            if (r0 == 0) goto L_0x00b8
            android.database.sqlite.SQLiteDatabase r0 = r10.f     // Catch:{ Exception -> 0x00bc }
            if (r0 == 0) goto L_0x00b8
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()     // Catch:{ Exception -> 0x00bc }
            android.net.wifi.WifiInfo r0 = r0.l()     // Catch:{ Exception -> 0x00bc }
            if (r0 == 0) goto L_0x00b8
            java.lang.String r1 = r0.getBSSID()     // Catch:{ Exception -> 0x00bc }
            if (r1 == 0) goto L_0x00b8
            java.lang.String r0 = r0.getBSSID()     // Catch:{ Exception -> 0x00bc }
            java.lang.String r1 = ":"
            java.lang.String r2 = ""
            java.lang.String r0 = r0.replace(r1, r2)     // Catch:{ Exception -> 0x00bc }
            java.lang.Long r1 = com.baidu.location.Jni.encode3(r0)     // Catch:{ Exception -> 0x00bc }
            r2 = 0
            r3 = 0
            r4 = 1
            android.database.sqlite.SQLiteDatabase r5 = r10.f     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r6.<init>()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.String r7 = "select * from hstdata where id = \""
            r6.append(r7)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r6.append(r1)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.String r1 = "\";"
            r6.append(r1)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            android.database.Cursor r3 = r5.rawQuery(r1, r3)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            if (r3 == 0) goto L_0x0087
            boolean r1 = r3.moveToFirst()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            if (r1 == 0) goto L_0x0087
            int r1 = r3.getInt(r4)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r5 = 2
            int r5 = r3.getInt(r5)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            long r8 = (long) r5     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            long r6 = r6 - r8
            r8 = 259200(0x3f480, double:1.28062E-318)
            int r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x0070
            goto L_0x0087
        L_0x0070:
            android.os.Bundle r5 = new android.os.Bundle     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r5.<init>()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.String r6 = "mac"
            byte[] r7 = r0.getBytes()     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r5.putByteArray(r6, r7)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            java.lang.String r6 = "hotspot"
            r5.putInt(r6, r1)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            r10.a((android.os.Bundle) r5)     // Catch:{ Exception -> 0x0099, all -> 0x0090 }
            goto L_0x0088
        L_0x0087:
            r2 = 1
        L_0x0088:
            if (r3 == 0) goto L_0x009d
        L_0x008a:
            r3.close()     // Catch:{ Exception -> 0x008e }
            goto L_0x009d
        L_0x008e:
            r1 = move-exception
            goto L_0x009d
        L_0x0090:
            r0 = move-exception
            if (r3 == 0) goto L_0x0098
            r3.close()     // Catch:{ Exception -> 0x0097 }
            goto L_0x0098
        L_0x0097:
            r1 = move-exception
        L_0x0098:
            throw r0     // Catch:{ Exception -> 0x00bc }
        L_0x0099:
            r1 = move-exception
            if (r3 == 0) goto L_0x009d
            goto L_0x008a
        L_0x009d:
            if (r2 == 0) goto L_0x00bd
            com.baidu.location.b.h$a r1 = r10.a     // Catch:{ Exception -> 0x00bc }
            if (r1 != 0) goto L_0x00aa
            com.baidu.location.b.h$a r1 = new com.baidu.location.b.h$a     // Catch:{ Exception -> 0x00bc }
            r1.<init>()     // Catch:{ Exception -> 0x00bc }
            r10.a = r1     // Catch:{ Exception -> 0x00bc }
        L_0x00aa:
            com.baidu.location.b.h$a r1 = r10.a     // Catch:{ Exception -> 0x00bc }
            if (r1 == 0) goto L_0x00bd
            com.baidu.location.b.h$a r1 = r10.a     // Catch:{ Exception -> 0x00bc }
            java.lang.String r2 = r10.a((boolean) r4)     // Catch:{ Exception -> 0x00bc }
            r1.a(r0, r2)     // Catch:{ Exception -> 0x00bc }
            goto L_0x00bd
        L_0x00b8:
            r10.f()     // Catch:{ Exception -> 0x00bc }
            goto L_0x00bd
        L_0x00bc:
            r0 = move-exception
        L_0x00bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.h.e():void");
    }
}
