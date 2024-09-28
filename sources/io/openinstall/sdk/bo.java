package io.openinstall.sdk;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class bo extends bc {
    /* access modifiers changed from: private */
    public final Context a = c.a().b();
    /* access modifiers changed from: private */
    public final ah b = new ah();
    /* access modifiers changed from: private */
    public final ad c = new ad();
    private final ae d = new ae(this.a);
    private final WeakReference<Activity> e;
    private al f;

    public bo(h hVar, WeakReference<Activity> weakReference) {
        super(hVar);
        this.e = weakReference;
    }

    /* access modifiers changed from: private */
    public al a(al alVar) {
        if (alVar == null || alVar.c() == 0) {
            al b2 = h().b();
            if (b2 != null) {
                return b2;
            }
        } else {
            h().a(alVar);
        }
        return alVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0111  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(java.util.Map<java.lang.String, java.lang.Object> r10) {
        /*
            r9 = this;
            io.openinstall.sdk.c r0 = io.openinstall.sdk.c.a()
            com.fm.openinstall.Configuration r0 = r0.e()
            java.util.concurrent.LinkedBlockingQueue r1 = new java.util.concurrent.LinkedBlockingQueue
            r1.<init>()
            io.openinstall.sdk.al r2 = r9.f
            java.lang.String r3 = "获取到 %s"
            r4 = 0
            r5 = 1
            if (r2 != 0) goto L_0x0023
            java.util.concurrent.ThreadPoolExecutor r2 = r9.e()
            io.openinstall.sdk.bp r6 = new io.openinstall.sdk.bp
            r6.<init>(r9, r1)
            r2.execute(r6)
            r2 = 1
            goto L_0x0065
        L_0x0023:
            io.openinstall.sdk.al r2 = r9.a((io.openinstall.sdk.al) r2)
            r6 = 2
            boolean r6 = r2.c((int) r6)
            if (r6 == 0) goto L_0x0043
            java.lang.String r2 = r2.b()
            java.lang.String r6 = "pbH"
            r10.put(r6, r2)
            boolean r2 = io.openinstall.sdk.cb.a
            if (r2 == 0) goto L_0x005d
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r6
            io.openinstall.sdk.cb.a(r3, r2)
            goto L_0x005d
        L_0x0043:
            boolean r6 = r2.c((int) r5)
            if (r6 == 0) goto L_0x005d
            java.lang.String r2 = r2.a()
            java.lang.String r6 = "pbT"
            r10.put(r6, r2)
            boolean r2 = io.openinstall.sdk.cb.a
            if (r2 == 0) goto L_0x005d
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r6
            io.openinstall.sdk.cb.a(r3, r2)
        L_0x005d:
            io.openinstall.sdk.ai r2 = r9.m()
            r2.a((boolean) r4)
            r2 = 0
        L_0x0065:
            int r2 = r2 + r5
            java.util.concurrent.ThreadPoolExecutor r6 = r9.e()
            io.openinstall.sdk.bq r7 = new io.openinstall.sdk.bq
            r7.<init>(r9, r1)
            r6.execute(r7)
            int r2 = r2 + r5
            java.util.concurrent.ThreadPoolExecutor r6 = r9.e()
            io.openinstall.sdk.br r7 = new io.openinstall.sdk.br
            r7.<init>(r9, r1)
            r6.execute(r7)
            boolean r6 = r0.isSimulatorDisabled()
            if (r6 != 0) goto L_0x0093
            int r2 = r2 + 1
            java.util.concurrent.ThreadPoolExecutor r6 = r9.e()
            io.openinstall.sdk.bs r7 = new io.openinstall.sdk.bs
            r7.<init>(r9, r1)
            r6.execute(r7)
        L_0x0093:
            boolean r6 = r0.isAdEnabled()
            if (r6 == 0) goto L_0x011f
            java.lang.String r6 = r0.getMacAddress()
            boolean r6 = com.fm.openinstall.Configuration.isPresent(r6)
            java.lang.String r7 = "mA"
            if (r6 == 0) goto L_0x00ad
            java.lang.String r6 = r0.getMacAddress()
        L_0x00a9:
            r10.put(r7, r6)
            goto L_0x00ba
        L_0x00ad:
            boolean r6 = r0.isMacDisabled()
            if (r6 != 0) goto L_0x00ba
            io.openinstall.sdk.ae r6 = r9.d
            java.lang.String r6 = r6.a()
            goto L_0x00a9
        L_0x00ba:
            java.lang.String r6 = r0.getImei()
            boolean r6 = com.fm.openinstall.Configuration.isPresent(r6)
            java.lang.String r7 = "im"
            if (r6 == 0) goto L_0x00ce
            java.lang.String r6 = r0.getImei()
        L_0x00ca:
            r10.put(r7, r6)
            goto L_0x00db
        L_0x00ce:
            boolean r6 = r0.isImeiDisabled()
            if (r6 != 0) goto L_0x00db
            io.openinstall.sdk.ae r6 = r9.d
            java.lang.String r6 = r6.b()
            goto L_0x00ca
        L_0x00db:
            java.lang.String r6 = r0.getGaid()
            boolean r6 = com.fm.openinstall.Configuration.isPresent(r6)
            if (r6 == 0) goto L_0x00ef
            java.lang.String r6 = r0.getGaid()
            java.lang.String r7 = "ga"
            r10.put(r7, r6)
            goto L_0x00fd
        L_0x00ef:
            int r2 = r2 + 1
            java.util.concurrent.ThreadPoolExecutor r6 = r9.e()
            io.openinstall.sdk.bt r7 = new io.openinstall.sdk.bt
            r7.<init>(r9, r1)
            r6.execute(r7)
        L_0x00fd:
            java.lang.String r6 = r0.getOaid()
            boolean r6 = com.fm.openinstall.Configuration.isPresent(r6)
            if (r6 == 0) goto L_0x0111
            java.lang.String r0 = r0.getOaid()
            java.lang.String r6 = "oa"
            r10.put(r6, r0)
            goto L_0x011f
        L_0x0111:
            int r2 = r2 + 1
            java.util.concurrent.ThreadPoolExecutor r0 = r9.e()
            io.openinstall.sdk.bu r6 = new io.openinstall.sdk.bu
            r6.<init>(r9, r1)
            r0.execute(r6)
        L_0x011f:
            if (r2 <= 0) goto L_0x0168
            r0 = 0
            r6 = 1
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ InterruptedException -> 0x012e }
            java.lang.Object r6 = r1.poll(r6, r8)     // Catch:{ InterruptedException -> 0x012e }
            android.util.Pair r6 = (android.util.Pair) r6     // Catch:{ InterruptedException -> 0x012e }
            r0 = r6
            goto L_0x012f
        L_0x012e:
            r6 = move-exception
        L_0x012f:
            if (r0 == 0) goto L_0x011f
            int r2 = r2 + -1
            java.lang.Object r6 = r0.first
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x011f
            java.lang.Object r6 = r0.second
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x011f
            java.lang.Object r6 = r0.second
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r7 = "false"
            boolean r6 = r7.equalsIgnoreCase(r6)
            if (r6 != 0) goto L_0x011f
            java.lang.Object r6 = r0.first
            java.lang.Object r7 = r0.second
            r10.put(r6, r7)
            boolean r6 = io.openinstall.sdk.cb.a
            if (r6 == 0) goto L_0x011f
            java.lang.Object[] r6 = new java.lang.Object[r5]
            java.lang.Object r0 = r0.first
            r6[r4] = r0
            io.openinstall.sdk.cb.a(r3, r6)
            goto L_0x011f
        L_0x0168:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.bo.a(java.util.Map):void");
    }

    private void o() {
        String a2 = TextUtils.isEmpty(j().h()) ? k().a(f()) : j().h();
        if (cb.a) {
            cb.a("opid = %s", a2);
        }
    }

    /* access modifiers changed from: protected */
    public void a() {
        super.a();
        boolean booleanValue = c.a().f().booleanValue();
        al a2 = al.a(c.a().g());
        boolean z = a2 == null || a2.c() == 0;
        if (booleanValue && z) {
            System.currentTimeMillis();
            e c2 = h().c(c.a().c());
            if (c2 == e.a || c2 == e.c || c2 == e.e) {
                m().a(this.e);
                a2 = m().b();
                System.currentTimeMillis();
            }
        }
        this.f = a2;
    }

    /* renamed from: n */
    public bf call() {
        e eVar;
        d dVar;
        System.currentTimeMillis();
        e d2 = g().d();
        if (d2 == null) {
            d2 = h().c(f());
        }
        if (d2 == e.a) {
            h().c();
        }
        if (d2 == e.a || d2 == e.c || d2 == e.e) {
            g().a(e.b);
            Map synchronizedMap = Collections.synchronizedMap(new HashMap());
            synchronizedMap.put("md", i().g());
            synchronizedMap.put("bI", i().h());
            synchronizedMap.put("buiD", i().i());
            synchronizedMap.put("bd", i().j());
            synchronizedMap.put("loI", i().l());
            String d3 = c.a().d();
            if (!TextUtils.isEmpty(d3)) {
                synchronizedMap.put("cC", d3);
            }
            a((Map<String, Object>) synchronizedMap);
            bb a2 = l().a((Map<String, ?>) synchronizedMap);
            int i = 1;
            while (!(a2 instanceof ay)) {
                try {
                    g().a((long) i);
                } catch (InterruptedException e2) {
                }
                a2 = l().a((Map<String, ?>) synchronizedMap);
                i = Math.min(i * 2, 60);
            }
            ay ayVar = (ay) a2;
            a(ayVar);
            if (ayVar.a() == 0) {
                h().a("FM_init_data", ayVar.c());
                h().a("FM_init_msg", ayVar.b());
                dVar = g();
                eVar = e.d;
            } else {
                h().a("FM_init_msg", ayVar.b());
                dVar = g();
                eVar = e.e;
            }
            dVar.a(eVar);
            h().a((al) null);
            g().e();
            h().a(f(), g().d());
        } else {
            if (d2 == e.d) {
                j().a(h().a());
                g().a(d2);
                g().e();
                m().a(false);
            }
            System.currentTimeMillis();
            return bf.a();
        }
        o();
        System.currentTimeMillis();
        return bf.a();
    }
}
