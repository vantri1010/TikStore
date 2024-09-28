package com.baidu.mapsdkplatform.comapi.favrite;

import android.os.Bundle;
import android.text.TextUtils;
import com.baidu.mapapi.UIMsg;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.model.inner.Point;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class a {
    private static a b = null;
    private com.baidu.mapsdkplatform.comjni.map.favorite.a a = null;
    private boolean c = false;
    private boolean d = false;
    private Vector<String> e = null;
    private Vector<String> f = null;
    private boolean g = false;
    private c h = new c();
    private b i = new b();

    /* renamed from: com.baidu.mapsdkplatform.comapi.favrite.a$a  reason: collision with other inner class name */
    class C0019a implements Comparator<String> {
        C0019a() {
        }

        /* renamed from: a */
        public int compare(String str, String str2) {
            return str2.compareTo(str);
        }
    }

    private class b {
        private long b;
        private long c;

        private b() {
        }

        /* access modifiers changed from: private */
        public void a() {
            this.b = System.currentTimeMillis();
        }

        /* access modifiers changed from: private */
        public void b() {
            this.c = System.currentTimeMillis();
        }

        /* access modifiers changed from: private */
        public boolean c() {
            return this.c - this.b > 1000;
        }
    }

    private class c {
        private String b;
        private long c;
        private long d;

        private c() {
            this.c = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
            this.d = 0;
        }

        /* access modifiers changed from: private */
        public String a() {
            return this.b;
        }

        /* access modifiers changed from: private */
        public void a(String str) {
            this.b = str;
            this.d = System.currentTimeMillis();
        }

        /* access modifiers changed from: private */
        public boolean b() {
            return TextUtils.isEmpty(this.b);
        }

        /* access modifiers changed from: private */
        public boolean c() {
            return true;
        }
    }

    private a() {
    }

    public static a a() {
        if (b == null) {
            synchronized (a.class) {
                if (b == null) {
                    a aVar = new a();
                    b = aVar;
                    aVar.h();
                }
            }
        }
        return b;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r0.a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean g() {
        /*
            com.baidu.mapsdkplatform.comapi.favrite.a r0 = b
            if (r0 == 0) goto L_0x0010
            com.baidu.mapsdkplatform.comjni.map.favorite.a r0 = r0.a
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.d()
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.favrite.a.g():boolean");
    }

    private boolean h() {
        if (this.a == null) {
            com.baidu.mapsdkplatform.comjni.map.favorite.a aVar = new com.baidu.mapsdkplatform.comjni.map.favorite.a();
            this.a = aVar;
            if (aVar.a() == 0) {
                this.a = null;
                return false;
            }
            j();
            i();
        }
        return true;
    }

    private boolean i() {
        if (this.a == null) {
            return false;
        }
        String str = SysOSUtil.getModuleFileName() + "/";
        this.a.a(1);
        return this.a.a(str, "fav_poi", "fifo", 10, UIMsg.d_ResultType.VERSION_CHECK, -1);
    }

    private void j() {
        this.c = false;
        this.d = false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:59:0x010a, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int a(java.lang.String r7, com.baidu.mapsdkplatform.comapi.favrite.FavSyncPoi r8) {
        /*
            r6 = this;
            monitor-enter(r6)
            com.baidu.mapsdkplatform.comjni.map.favorite.a r0 = r6.a     // Catch:{ all -> 0x010b }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r6)
            return r1
        L_0x0008:
            r0 = -1
            if (r7 == 0) goto L_0x0109
            java.lang.String r2 = ""
            boolean r2 = r7.equals(r2)     // Catch:{ all -> 0x010b }
            if (r2 != 0) goto L_0x0109
            if (r8 != 0) goto L_0x0017
            goto L_0x0109
        L_0x0017:
            r6.j()     // Catch:{ all -> 0x010b }
            java.util.ArrayList r2 = r6.e()     // Catch:{ all -> 0x010b }
            if (r2 == 0) goto L_0x0025
            int r3 = r2.size()     // Catch:{ all -> 0x010b }
            goto L_0x0026
        L_0x0025:
            r3 = 0
        L_0x0026:
            r4 = 1
            int r3 = r3 + r4
            r5 = 500(0x1f4, float:7.0E-43)
            if (r3 <= r5) goto L_0x002f
            r7 = -2
            monitor-exit(r6)
            return r7
        L_0x002f:
            if (r2 == 0) goto L_0x0058
            int r3 = r2.size()     // Catch:{ all -> 0x010b }
            if (r3 <= 0) goto L_0x0058
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x010b }
        L_0x003b:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x010b }
            if (r3 == 0) goto L_0x0058
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x010b }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x010b }
            com.baidu.mapsdkplatform.comapi.favrite.FavSyncPoi r3 = r6.b(r3)     // Catch:{ all -> 0x010b }
            if (r3 != 0) goto L_0x004e
            goto L_0x003b
        L_0x004e:
            java.lang.String r3 = r3.b     // Catch:{ all -> 0x010b }
            boolean r3 = r7.equals(r3)     // Catch:{ all -> 0x010b }
            if (r3 == 0) goto L_0x003b
            monitor-exit(r6)
            return r0
        L_0x0058:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.<init>()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r8.b = r7     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = java.lang.String.valueOf(r2)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r2.<init>()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r2.append(r7)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r3 = "_"
            r2.append(r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            int r3 = r8.hashCode()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r2.append(r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r2 = r2.toString()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r8.h = r7     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r8.a = r2     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "bdetail"
            boolean r3 = r8.i     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "uspoiname"
            java.lang.String r3 = r8.b     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r7.<init>()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r3 = "x"
            com.baidu.mapapi.model.inner.Point r5 = r8.c     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            int r5 = r5.getmPtx()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r7.put(r3, r5)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r3 = "y"
            com.baidu.mapapi.model.inner.Point r5 = r8.c     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            int r5 = r5.getmPty()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r7.put(r3, r5)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r3 = "pt"
            r0.put(r3, r7)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "ncityid"
            java.lang.String r3 = r8.e     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "npoitype"
            int r3 = r8.g     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "uspoiuid"
            java.lang.String r3 = r8.f     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "addr"
            java.lang.String r3 = r8.d     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = "addtimesec"
            java.lang.String r3 = r8.h     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r0.put(r7, r3)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r7.<init>()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r3 = "Fav_Sync"
            r7.put(r3, r0)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r0 = "Fav_Content"
            java.lang.String r8 = r8.j     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            r7.put(r0, r8)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            com.baidu.mapsdkplatform.comjni.map.favorite.a r8 = r6.a     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            java.lang.String r7 = r7.toString()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            boolean r7 = r8.a(r2, r7)     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            if (r7 == 0) goto L_0x00f9
            r6.j()     // Catch:{ JSONException -> 0x0103, all -> 0x00fe }
            g()     // Catch:{ all -> 0x010b }
            monitor-exit(r6)
            return r4
        L_0x00f9:
            g()     // Catch:{ all -> 0x010b }
            monitor-exit(r6)
            return r1
        L_0x00fe:
            r7 = move-exception
            g()     // Catch:{ all -> 0x010b }
            throw r7     // Catch:{ all -> 0x010b }
        L_0x0103:
            r7 = move-exception
            g()     // Catch:{ all -> 0x010b }
            monitor-exit(r6)
            return r1
        L_0x0109:
            monitor-exit(r6)
            return r0
        L_0x010b:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.favrite.a.a(java.lang.String, com.baidu.mapsdkplatform.comapi.favrite.FavSyncPoi):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0027, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean a(java.lang.String r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            com.baidu.mapsdkplatform.comjni.map.favorite.a r0 = r2.a     // Catch:{ all -> 0x0028 }
            r1 = 0
            if (r0 != 0) goto L_0x0008
            monitor-exit(r2)
            return r1
        L_0x0008:
            if (r3 == 0) goto L_0x0026
            java.lang.String r0 = ""
            boolean r0 = r3.equals(r0)     // Catch:{ all -> 0x0028 }
            if (r0 == 0) goto L_0x0013
            goto L_0x0026
        L_0x0013:
            boolean r0 = r2.c(r3)     // Catch:{ all -> 0x0028 }
            if (r0 != 0) goto L_0x001b
            monitor-exit(r2)
            return r1
        L_0x001b:
            r2.j()     // Catch:{ all -> 0x0028 }
            com.baidu.mapsdkplatform.comjni.map.favorite.a r0 = r2.a     // Catch:{ all -> 0x0028 }
            boolean r3 = r0.a((java.lang.String) r3)     // Catch:{ all -> 0x0028 }
            monitor-exit(r2)
            return r3
        L_0x0026:
            monitor-exit(r2)
            return r1
        L_0x0028:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.favrite.a.a(java.lang.String):boolean");
    }

    public FavSyncPoi b(String str) {
        if (!(this.a == null || str == null || str.equals(""))) {
            try {
                if (!c(str)) {
                    return null;
                }
                FavSyncPoi favSyncPoi = new FavSyncPoi();
                String b2 = this.a.b(str);
                if (b2 != null) {
                    if (!b2.equals("")) {
                        JSONObject jSONObject = new JSONObject(b2);
                        JSONObject optJSONObject = jSONObject.optJSONObject("Fav_Sync");
                        String optString = jSONObject.optString("Fav_Content");
                        favSyncPoi.b = optJSONObject.optString("uspoiname");
                        JSONObject optJSONObject2 = optJSONObject.optJSONObject("pt");
                        favSyncPoi.c = new Point(optJSONObject2.optInt("x"), optJSONObject2.optInt("y"));
                        favSyncPoi.e = optJSONObject.optString("ncityid");
                        favSyncPoi.f = optJSONObject.optString("uspoiuid");
                        favSyncPoi.g = optJSONObject.optInt("npoitype");
                        favSyncPoi.d = optJSONObject.optString("addr");
                        favSyncPoi.h = optJSONObject.optString("addtimesec");
                        favSyncPoi.i = optJSONObject.optBoolean("bdetail");
                        favSyncPoi.j = optString;
                        favSyncPoi.a = str;
                        return favSyncPoi;
                    }
                }
                return null;
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            } catch (JSONException e3) {
                e3.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void b() {
        a aVar = b;
        if (aVar != null) {
            com.baidu.mapsdkplatform.comjni.map.favorite.a aVar2 = aVar.a;
            if (aVar2 != null) {
                aVar2.b();
                b.a = null;
            }
            b = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a0, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00a5, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean b(java.lang.String r6, com.baidu.mapsdkplatform.comapi.favrite.FavSyncPoi r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            com.baidu.mapsdkplatform.comjni.map.favorite.a r0 = r5.a     // Catch:{ all -> 0x00a6 }
            r1 = 0
            if (r0 == 0) goto L_0x00a4
            if (r6 == 0) goto L_0x00a4
            java.lang.String r0 = ""
            boolean r0 = r6.equals(r0)     // Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x00a4
            if (r7 != 0) goto L_0x0014
            goto L_0x00a4
        L_0x0014:
            boolean r0 = r5.c(r6)     // Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x001c
            monitor-exit(r5)
            return r1
        L_0x001c:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00a1 }
            r0.<init>()     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "uspoiname"
            java.lang.String r3 = r7.b     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00a1 }
            r2.<init>()     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r3 = "x"
            com.baidu.mapapi.model.inner.Point r4 = r7.c     // Catch:{ JSONException -> 0x00a1 }
            int r4 = r4.getmPtx()     // Catch:{ JSONException -> 0x00a1 }
            r2.put(r3, r4)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r3 = "y"
            com.baidu.mapapi.model.inner.Point r4 = r7.c     // Catch:{ JSONException -> 0x00a1 }
            int r4 = r4.getmPty()     // Catch:{ JSONException -> 0x00a1 }
            r2.put(r3, r4)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r3 = "pt"
            r0.put(r3, r2)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "ncityid"
            java.lang.String r3 = r7.e     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "npoitype"
            int r3 = r7.g     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "uspoiuid"
            java.lang.String r3 = r7.f     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "addr"
            java.lang.String r3 = r7.d     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = java.lang.String.valueOf(r2)     // Catch:{ JSONException -> 0x00a1 }
            r7.h = r2     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "addtimesec"
            java.lang.String r3 = r7.h     // Catch:{ JSONException -> 0x00a1 }
            r0.put(r2, r3)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r2 = "bdetail"
            r0.put(r2, r1)     // Catch:{ JSONException -> 0x00a1 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00a1 }
            r2.<init>()     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r3 = "Fav_Sync"
            r2.put(r3, r0)     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r0 = "Fav_Content"
            java.lang.String r7 = r7.j     // Catch:{ JSONException -> 0x00a1 }
            r2.put(r0, r7)     // Catch:{ JSONException -> 0x00a1 }
            r5.j()     // Catch:{ JSONException -> 0x00a1 }
            com.baidu.mapsdkplatform.comjni.map.favorite.a r7 = r5.a     // Catch:{ JSONException -> 0x00a1 }
            if (r7 == 0) goto L_0x009f
            com.baidu.mapsdkplatform.comjni.map.favorite.a r7 = r5.a     // Catch:{ JSONException -> 0x00a1 }
            java.lang.String r0 = r2.toString()     // Catch:{ JSONException -> 0x00a1 }
            boolean r6 = r7.b(r6, r0)     // Catch:{ JSONException -> 0x00a1 }
            if (r6 == 0) goto L_0x009f
            r1 = 1
        L_0x009f:
            monitor-exit(r5)
            return r1
        L_0x00a1:
            r6 = move-exception
            monitor-exit(r5)
            return r1
        L_0x00a4:
            monitor-exit(r5)
            return r1
        L_0x00a6:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.favrite.a.b(java.lang.String, com.baidu.mapsdkplatform.comapi.favrite.FavSyncPoi):boolean");
    }

    public synchronized boolean c() {
        if (this.a == null) {
            return false;
        }
        j();
        boolean c2 = this.a.c();
        g();
        return c2;
    }

    public boolean c(String str) {
        return this.a != null && str != null && !str.equals("") && this.a.c(str);
    }

    public ArrayList<String> d() {
        String b2;
        if (this.a == null) {
            return null;
        }
        if (this.d && this.f != null) {
            return new ArrayList<>(this.f);
        }
        try {
            Bundle bundle = new Bundle();
            this.a.a(bundle);
            String[] stringArray = bundle.getStringArray("rstString");
            if (stringArray != null) {
                if (this.f == null) {
                    this.f = new Vector<>();
                } else {
                    this.f.clear();
                }
                for (int i2 = 0; i2 < stringArray.length; i2++) {
                    if (!stringArray[i2].equals("data_version") && (b2 = this.a.b(stringArray[i2])) != null) {
                        if (!b2.equals("")) {
                            this.f.add(stringArray[i2]);
                        }
                    }
                }
                if (this.f.size() > 0) {
                    try {
                        Collections.sort(this.f, new C0019a());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    this.d = true;
                }
            } else if (this.f != null) {
                this.f.clear();
                this.f = null;
            }
            if (this.f == null) {
                return null;
            }
            if (this.f.isEmpty()) {
                return null;
            }
            return new ArrayList<>(this.f);
        } catch (Exception e3) {
            return null;
        }
    }

    public ArrayList<String> e() {
        if (this.a == null) {
            return null;
        }
        if (this.c && this.e != null) {
            return new ArrayList<>(this.e);
        }
        try {
            Bundle bundle = new Bundle();
            this.a.a(bundle);
            String[] stringArray = bundle.getStringArray("rstString");
            if (stringArray != null) {
                if (this.e == null) {
                    this.e = new Vector<>();
                } else {
                    this.e.clear();
                }
                for (String str : stringArray) {
                    if (!str.equals("data_version")) {
                        this.e.add(str);
                    }
                }
                if (this.e.size() > 0) {
                    try {
                        Collections.sort(this.e, new C0019a());
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    this.c = true;
                }
            } else if (this.e != null) {
                this.e.clear();
                this.e = null;
            }
            Vector<String> vector = this.e;
            if (vector == null || vector.size() == 0) {
                return null;
            }
            return new ArrayList<>(this.e);
        } catch (Exception e3) {
            return null;
        }
    }

    public String f() {
        String b2;
        if (this.i.c() && !this.h.c() && !this.h.b()) {
            return this.h.a();
        }
        this.i.a();
        if (this.a == null) {
            return null;
        }
        ArrayList<String> d2 = d();
        JSONObject jSONObject = new JSONObject();
        if (d2 != null) {
            try {
                JSONArray jSONArray = new JSONArray();
                int i2 = 0;
                Iterator<String> it = d2.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    if (next != null && !next.equals("data_version") && (b2 = this.a.b(next)) != null && !b2.equals("")) {
                        JSONObject optJSONObject = new JSONObject(b2).optJSONObject("Fav_Sync");
                        optJSONObject.put("key", next);
                        jSONArray.put(i2, optJSONObject);
                        i2++;
                    }
                }
                if (i2 > 0) {
                    jSONObject.put("favcontents", jSONArray);
                    jSONObject.put("favpoinum", i2);
                }
            } catch (JSONException e2) {
                return null;
            }
        }
        this.i.b();
        this.h.a(jSONObject.toString());
        return this.h.a();
    }
}
