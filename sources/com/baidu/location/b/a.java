package com.baidu.location.b;

import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.baidu.location.e.j;
import com.baidu.location.f;
import com.baidu.location.g.k;
import com.baidu.location.indoor.g;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.king.zxing.util.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class a {
    public static long c = 0;
    public static int d = -1;
    private static a f = null;
    public boolean a;
    boolean b;
    int e;
    private ArrayList<C0004a> g;
    private boolean h;
    private BDLocation i;
    private BDLocation j;
    private BDLocation k;
    private boolean l;
    /* access modifiers changed from: private */
    public boolean m;
    private b n;

    /* renamed from: com.baidu.location.b.a$a  reason: collision with other inner class name */
    private class C0004a {
        public String a = null;
        public Messenger b = null;
        public LocationClientOption c = new LocationClientOption();
        public int d;

        public C0004a(Message message) {
            boolean z = false;
            this.d = 0;
            this.b = message.replyTo;
            this.a = message.getData().getString("packName");
            this.c.prodName = message.getData().getString("prodName");
            com.baidu.location.g.b.a().a(this.c.prodName, this.a);
            this.c.coorType = message.getData().getString("coorType");
            this.c.addrType = message.getData().getString("addrType");
            this.c.enableSimulateGps = message.getData().getBoolean("enableSimulateGps", false);
            k.m = k.m || this.c.enableSimulateGps;
            if (!k.g.equals("all")) {
                k.g = this.c.addrType;
            }
            this.c.openGps = message.getData().getBoolean("openGPS");
            this.c.scanSpan = message.getData().getInt("scanSpan");
            this.c.timeOut = message.getData().getInt("timeOut");
            this.c.priority = message.getData().getInt("priority");
            this.c.location_change_notify = message.getData().getBoolean("location_change_notify");
            this.c.mIsNeedDeviceDirect = message.getData().getBoolean("needDirect", false);
            this.c.isNeedAltitude = message.getData().getBoolean("isneedaltitude", false);
            this.c.isNeedNewVersionRgc = message.getData().getBoolean("isneednewrgc", false);
            k.i = k.i || this.c.isNeedNewVersionRgc;
            k.h = k.h || message.getData().getBoolean("isneedaptag", false);
            k.j = k.j || message.getData().getBoolean("isneedaptagd", false);
            k.S = message.getData().getFloat("autoNotifyLocSensitivity", 0.5f);
            int i = message.getData().getInt("wifitimeout", Integer.MAX_VALUE);
            if (i < k.ag) {
                k.ag = i;
            }
            int i2 = message.getData().getInt("autoNotifyMaxInterval", 0);
            if (i2 >= k.X) {
                k.X = i2;
            }
            int i3 = message.getData().getInt("autoNotifyMinDistance", 0);
            if (i3 >= k.Z) {
                k.Z = i3;
            }
            int i4 = message.getData().getInt("autoNotifyMinTimeInterval", 0);
            if (i4 >= k.Y) {
                k.Y = i4;
            }
            if (this.c.mIsNeedDeviceDirect || this.c.isNeedAltitude) {
                n.a().a(this.c.mIsNeedDeviceDirect);
                n.a().b();
            }
            a.this.b = (a.this.b || this.c.isNeedAltitude) ? true : z;
        }

        /* access modifiers changed from: private */
        public void a(int i) {
            Message obtain = Message.obtain((Handler) null, i);
            try {
                if (this.b != null) {
                    this.b.send(obtain);
                }
                this.d = 0;
            } catch (Exception e2) {
                if (e2 instanceof DeadObjectException) {
                    this.d++;
                }
            }
        }

        /* access modifiers changed from: private */
        public void a(int i, Bundle bundle) {
            Message obtain = Message.obtain((Handler) null, i);
            obtain.setData(bundle);
            try {
                if (this.b != null) {
                    this.b.send(obtain);
                }
                this.d = 0;
            } catch (Exception e2) {
                if (e2 instanceof DeadObjectException) {
                    this.d++;
                }
                e2.printStackTrace();
            }
        }

        private void a(int i, String str, BDLocation bDLocation) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(str, bDLocation);
            bundle.setClassLoader(BDLocation.class.getClassLoader());
            Message obtain = Message.obtain((Handler) null, i);
            obtain.setData(bundle);
            try {
                if (this.b != null) {
                    this.b.send(obtain);
                }
                this.d = 0;
            } catch (Exception e2) {
                if (e2 instanceof DeadObjectException) {
                    this.d++;
                }
            }
        }

        public void a() {
            a(111);
        }

        public void a(BDLocation bDLocation) {
            a(bDLocation, 21);
        }

        public void a(BDLocation bDLocation, int i) {
            String str;
            BDLocation bDLocation2 = new BDLocation(bDLocation);
            if (g.a().e()) {
                bDLocation2.setIndoorLocMode(true);
            }
            if (i == 21) {
                a(27, "locStr", bDLocation2);
            }
            if (this.c.coorType != null && !this.c.coorType.equals(CoordinateType.GCJ02)) {
                double longitude = bDLocation2.getLongitude();
                double latitude = bDLocation2.getLatitude();
                if (!(longitude == Double.MIN_VALUE || latitude == Double.MIN_VALUE)) {
                    if ((bDLocation2.getCoorType() != null && bDLocation2.getCoorType().equals(CoordinateType.GCJ02)) || bDLocation2.getCoorType() == null) {
                        double[] coorEncrypt = Jni.coorEncrypt(longitude, latitude, this.c.coorType);
                        bDLocation2.setLongitude(coorEncrypt[0]);
                        bDLocation2.setLatitude(coorEncrypt[1]);
                        str = this.c.coorType;
                    } else if (bDLocation2.getCoorType() != null && bDLocation2.getCoorType().equals(CoordinateType.WGS84) && !this.c.coorType.equals("bd09ll")) {
                        double[] coorEncrypt2 = Jni.coorEncrypt(longitude, latitude, "wgs842mc");
                        bDLocation2.setLongitude(coorEncrypt2[0]);
                        bDLocation2.setLatitude(coorEncrypt2[1]);
                        str = "wgs84mc";
                    }
                    bDLocation2.setCoorType(str);
                }
            }
            a(i, "locStr", bDLocation2);
        }

        public void b() {
            if (this.c.location_change_notify) {
                a(k.b ? 54 : 55);
            }
        }
    }

    private class b implements Runnable {
        final /* synthetic */ a a;
        private int b;
        private boolean c;

        public void run() {
            if (!this.c) {
                this.b++;
                boolean unused = this.a.m = false;
            }
        }
    }

    private a() {
        this.g = null;
        this.h = false;
        this.a = false;
        this.b = false;
        this.i = null;
        this.j = null;
        this.e = 0;
        this.k = null;
        this.l = false;
        this.m = false;
        this.n = null;
        this.g = new ArrayList<>();
    }

    private C0004a a(Messenger messenger) {
        ArrayList<C0004a> arrayList = this.g;
        if (arrayList == null) {
            return null;
        }
        Iterator<C0004a> it = arrayList.iterator();
        while (it.hasNext()) {
            C0004a next = it.next();
            if (next.b.equals(messenger)) {
                return next;
            }
        }
        return null;
    }

    public static a a() {
        if (f == null) {
            f = new a();
        }
        return f;
    }

    private void a(C0004a aVar) {
        int i2;
        if (aVar != null) {
            if (a(aVar.b) != null) {
                i2 = 14;
            } else {
                this.g.add(aVar);
                i2 = 13;
            }
            aVar.a(i2);
        }
    }

    private void b(String str) {
        Intent intent = new Intent("com.baidu.location.flp.log");
        intent.setPackage("com.baidu.baidulocationdemo");
        intent.putExtra("data", str);
        intent.putExtra("pack", com.baidu.location.g.b.e);
        intent.putExtra("tag", RemoteConfigConstants.ResponseFieldKey.STATE);
        f.getServiceContext().sendBroadcast(intent);
    }

    private void f() {
        g();
        e();
    }

    private void g() {
        Iterator<C0004a> it = this.g.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            C0004a next = it.next();
            if (next.c.openGps) {
                z2 = true;
            }
            if (next.c.location_change_notify) {
                z = true;
            }
        }
        k.a = z;
        if (this.h != z2) {
            this.h = z2;
            com.baidu.location.e.f.a().a(this.h);
        }
    }

    public void a(Bundle bundle, int i2) {
        Iterator<C0004a> it = this.g.iterator();
        while (it.hasNext()) {
            try {
                C0004a next = it.next();
                next.a(i2, bundle);
                if (next.d > 4) {
                    it.remove();
                }
            } catch (Exception e2) {
                return;
            }
        }
    }

    public void a(Message message) {
        if (message != null && message.replyTo != null) {
            c = System.currentTimeMillis();
            this.a = true;
            j.a().b();
            a(new C0004a(message));
            f();
            if (this.l) {
                b(TtmlNode.START);
                this.e = 0;
            }
        }
    }

    public void a(BDLocation bDLocation) {
        b(bDLocation);
    }

    public void a(String str) {
        c(new BDLocation(str));
    }

    public void a(boolean z) {
        this.a = z;
        d = z ? 1 : 0;
    }

    public void b() {
        this.g.clear();
        this.i = null;
        f();
    }

    public void b(Message message) {
        C0004a a2 = a(message.replyTo);
        if (a2 != null) {
            this.g.remove(a2);
        }
        n.a().c();
        f();
        if (this.l) {
            b("stop");
            this.e = 0;
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:10:0x0030=Splitter:B:10:0x0030, B:32:0x0099=Splitter:B:32:0x0099} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(com.baidu.location.BDLocation r14) {
        /*
            r13 = this;
            r0 = 4
            r1 = 66
            r2 = 61
            r3 = 0
            r4 = 161(0xa1, float:2.26E-43)
            if (r14 == 0) goto L_0x004b
            int r5 = r14.getLocType()
            if (r5 != r4) goto L_0x004b
            com.baidu.location.a.a r5 = com.baidu.location.a.a.a()
            boolean r5 = r5.b()
            if (r5 != 0) goto L_0x004b
            com.baidu.location.BDLocation r5 = r13.j
            if (r5 != 0) goto L_0x002a
            com.baidu.location.BDLocation r5 = new com.baidu.location.BDLocation
            r5.<init>()
            r13.j = r5
            r6 = 505(0x1f9, float:7.08E-43)
            r5.setLocType(r6)
        L_0x002a:
            java.util.ArrayList<com.baidu.location.b.a$a> r5 = r13.g
            java.util.Iterator r5 = r5.iterator()
        L_0x0030:
            boolean r6 = r5.hasNext()     // Catch:{ Exception -> 0x0049 }
            if (r6 == 0) goto L_0x00b0
            java.lang.Object r6 = r5.next()     // Catch:{ Exception -> 0x0049 }
            com.baidu.location.b.a$a r6 = (com.baidu.location.b.a.C0004a) r6     // Catch:{ Exception -> 0x0049 }
            com.baidu.location.BDLocation r7 = r13.j     // Catch:{ Exception -> 0x0049 }
            r6.a((com.baidu.location.BDLocation) r7)     // Catch:{ Exception -> 0x0049 }
            int r6 = r6.d     // Catch:{ Exception -> 0x0049 }
            if (r6 <= r0) goto L_0x0030
            r5.remove()     // Catch:{ Exception -> 0x0049 }
            goto L_0x0030
        L_0x0049:
            r0 = move-exception
            goto L_0x00b0
        L_0x004b:
            boolean r5 = r14.hasAltitude()
            if (r5 != 0) goto L_0x0082
            boolean r5 = r13.b
            if (r5 == 0) goto L_0x0082
            int r5 = r14.getLocType()
            if (r5 == r4) goto L_0x0061
            int r5 = r14.getLocType()
            if (r5 != r1) goto L_0x0082
        L_0x0061:
            com.baidu.location.c.a r5 = com.baidu.location.c.a.a()
            double r6 = r14.getLongitude()
            double r8 = r14.getLatitude()
            double[] r5 = r5.a((double) r6, (double) r8)
            r6 = r5[r3]
            com.baidu.location.c.a.a()
            r8 = 4666722622711529472(0x40c3878000000000, double:9999.0)
            int r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r5 >= 0) goto L_0x0082
            r14.setAltitude(r6)
        L_0x0082:
            int r5 = r14.getLocType()
            if (r5 != r2) goto L_0x0093
            com.baidu.location.c.a r5 = com.baidu.location.c.a.a()
            int r5 = r5.a((com.baidu.location.BDLocation) r14)
            r14.setGpsAccuracyStatus(r5)
        L_0x0093:
            java.util.ArrayList<com.baidu.location.b.a$a> r5 = r13.g
            java.util.Iterator r5 = r5.iterator()
        L_0x0099:
            boolean r6 = r5.hasNext()     // Catch:{ Exception -> 0x0049 }
            if (r6 == 0) goto L_0x00b0
            java.lang.Object r6 = r5.next()     // Catch:{ Exception -> 0x0049 }
            com.baidu.location.b.a$a r6 = (com.baidu.location.b.a.C0004a) r6     // Catch:{ Exception -> 0x0049 }
            r6.a((com.baidu.location.BDLocation) r14)     // Catch:{ Exception -> 0x0049 }
            int r6 = r6.d     // Catch:{ Exception -> 0x0049 }
            if (r6 <= r0) goto L_0x0099
            r5.remove()     // Catch:{ Exception -> 0x0049 }
            goto L_0x0099
        L_0x00b0:
            boolean r0 = com.baidu.location.b.l.g
            if (r0 == 0) goto L_0x00b6
            com.baidu.location.b.l.g = r3
        L_0x00b6:
            int r5 = com.baidu.location.g.k.X
            r6 = 10000(0x2710, float:1.4013E-41)
            if (r5 < r6) goto L_0x0107
            int r5 = r14.getLocType()
            if (r5 == r2) goto L_0x00ce
            int r2 = r14.getLocType()
            if (r2 == r4) goto L_0x00ce
            int r2 = r14.getLocType()
            if (r2 != r1) goto L_0x0107
        L_0x00ce:
            com.baidu.location.BDLocation r1 = r13.i
            if (r1 == 0) goto L_0x0100
            r2 = 1
            float[] r2 = new float[r2]
            double r4 = r1.getLatitude()
            com.baidu.location.BDLocation r1 = r13.i
            double r6 = r1.getLongitude()
            double r8 = r14.getLatitude()
            double r10 = r14.getLongitude()
            r12 = r2
            android.location.Location.distanceBetween(r4, r6, r8, r10, r12)
            r1 = r2[r3]
            int r2 = com.baidu.location.g.k.Z
            float r2 = (float) r2
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 > 0) goto L_0x00f7
            if (r0 != 0) goto L_0x00f7
            return
        L_0x00f7:
            r0 = 0
            r13.i = r0
            com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation
            r0.<init>((com.baidu.location.BDLocation) r14)
            goto L_0x0105
        L_0x0100:
            com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation
            r0.<init>((com.baidu.location.BDLocation) r14)
        L_0x0105:
            r13.i = r0
        L_0x0107:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.a.b(com.baidu.location.BDLocation):void");
    }

    public void c() {
        Iterator<C0004a> it = this.g.iterator();
        while (it.hasNext()) {
            it.next().a();
        }
    }

    public void c(BDLocation bDLocation) {
        Address a2 = l.c().a(bDLocation);
        String f2 = l.c().f();
        List<Poi> g2 = l.c().g();
        PoiRegion h2 = l.c().h();
        if (a2 != null) {
            bDLocation.setAddr(a2);
        }
        if (f2 != null) {
            bDLocation.setLocationDescribe(f2);
        }
        if (g2 != null) {
            bDLocation.setPoiList(g2);
        }
        if (h2 != null) {
            bDLocation.setPoiRegion(h2);
        }
        if (g.a().f() && g.a().g() != null) {
            bDLocation.setFloor(g.a().g());
            bDLocation.setIndoorLocMode(true);
            if (g.a().h() != null) {
                bDLocation.setBuildingID(g.a().h());
            }
        }
        a(bDLocation);
        l.c().c(bDLocation);
    }

    public boolean c(Message message) {
        C0004a a2 = a(message.replyTo);
        boolean z = false;
        if (a2 == null) {
            return false;
        }
        int i2 = a2.c.scanSpan;
        a2.c.scanSpan = message.getData().getInt("scanSpan", a2.c.scanSpan);
        if (a2.c.scanSpan < 1000) {
            n.a().c();
            this.a = false;
        } else {
            this.a = true;
        }
        if (a2.c.scanSpan > 999 && i2 < 1000) {
            if (a2.c.mIsNeedDeviceDirect || a2.c.isNeedAltitude) {
                n.a().a(a2.c.mIsNeedDeviceDirect);
                n.a().b();
            }
            if (this.b || a2.c.isNeedAltitude) {
                z = true;
            }
            this.b = z;
            z = true;
        }
        a2.c.openGps = message.getData().getBoolean("openGPS", a2.c.openGps);
        String string = message.getData().getString("coorType");
        LocationClientOption locationClientOption = a2.c;
        if (string == null || string.equals("")) {
            string = a2.c.coorType;
        }
        locationClientOption.coorType = string;
        String string2 = message.getData().getString("addrType");
        LocationClientOption locationClientOption2 = a2.c;
        if (string2 == null || string2.equals("")) {
            string2 = a2.c.addrType;
        }
        locationClientOption2.addrType = string2;
        if (!k.g.equals(a2.c.addrType)) {
            l.c().j();
        }
        a2.c.timeOut = message.getData().getInt("timeOut", a2.c.timeOut);
        a2.c.location_change_notify = message.getData().getBoolean("location_change_notify", a2.c.location_change_notify);
        a2.c.priority = message.getData().getInt("priority", a2.c.priority);
        int i3 = message.getData().getInt("wifitimeout", Integer.MAX_VALUE);
        if (i3 < k.ag) {
            k.ag = i3;
        }
        f();
        return z;
    }

    public int d(Message message) {
        C0004a a2;
        if (message == null || message.replyTo == null || (a2 = a(message.replyTo)) == null || a2.c == null) {
            return 1;
        }
        return a2.c.priority;
    }

    public String d() {
        StringBuffer stringBuffer = new StringBuffer(256);
        if (this.g.isEmpty()) {
            return "&prod=" + com.baidu.location.g.b.f + LogUtils.COLON + com.baidu.location.g.b.e;
        }
        C0004a aVar = this.g.get(0);
        if (aVar.c.prodName != null) {
            stringBuffer.append(aVar.c.prodName);
        }
        if (aVar.a != null) {
            stringBuffer.append(LogUtils.COLON);
            stringBuffer.append(aVar.a);
            stringBuffer.append(LogUtils.VERTICAL);
        }
        String stringBuffer2 = stringBuffer.toString();
        if (stringBuffer2 == null || stringBuffer2.equals("")) {
            return null;
        }
        return "&prod=" + stringBuffer2;
    }

    public int e(Message message) {
        C0004a a2;
        if (message == null || message.replyTo == null || (a2 = a(message.replyTo)) == null || a2.c == null) {
            return 1000;
        }
        return a2.c.scanSpan;
    }

    public void e() {
        Iterator<C0004a> it = this.g.iterator();
        while (it.hasNext()) {
            it.next().b();
        }
    }
}
