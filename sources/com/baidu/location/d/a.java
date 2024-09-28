package com.baidu.location.d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.Jni;
import com.baidu.location.LocationClient;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class a {
    /* access modifiers changed from: private */
    public ArrayList<BDNotifyListener> a = null;
    private float b = Float.MAX_VALUE;
    private BDLocation c = null;
    private long d = 0;
    /* access modifiers changed from: private */
    public LocationClient e = null;
    private Context f = null;
    private int g = 0;
    private long h = 0;
    private boolean i = false;
    private PendingIntent j = null;
    private AlarmManager k = null;
    private C0007a l = null;
    private b m;
    private boolean n;

    /* renamed from: com.baidu.location.d.a$a  reason: collision with other inner class name */
    public class C0007a extends BroadcastReceiver {
        public C0007a() {
        }

        public void onReceive(Context context, Intent intent) {
            if (a.this.a != null && !a.this.a.isEmpty()) {
                a.this.e.requestNotifyLocation();
            }
        }
    }

    public class b implements BDLocationListener {
        public b() {
        }

        public void onReceiveLocation(BDLocation bDLocation) {
            if (a.this.a != null && a.this.a.size() > 0) {
                a.this.a(bDLocation);
            }
        }
    }

    public a(Context context, LocationClient locationClient) {
        b bVar = new b();
        this.m = bVar;
        this.n = false;
        this.f = context;
        this.e = locationClient;
        locationClient.registerNotifyLocationListener(bVar);
        this.k = (AlarmManager) this.f.getSystemService(NotificationCompat.CATEGORY_ALARM);
        this.l = new C0007a();
        this.n = false;
    }

    private void a(long j2) {
        try {
            if (this.j != null) {
                this.k.cancel(this.j);
            }
            PendingIntent broadcast = PendingIntent.getBroadcast(this.f, 0, new Intent("android.com.baidu.location.TIMER.NOTIFY"), 134217728);
            this.j = broadcast;
            if (broadcast != null) {
                this.k.set(0, System.currentTimeMillis() + j2, this.j);
            }
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation) {
        BDLocation bDLocation2 = bDLocation;
        if (bDLocation.getLocType() != 61 && bDLocation.getLocType() != 161 && bDLocation.getLocType() != 65) {
            a(120000);
        } else if (System.currentTimeMillis() - this.d >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && this.a != null) {
            this.c = bDLocation2;
            this.d = System.currentTimeMillis();
            float[] fArr = new float[1];
            Iterator<BDNotifyListener> it = this.a.iterator();
            float f2 = Float.MAX_VALUE;
            while (it.hasNext()) {
                BDNotifyListener next = it.next();
                BDNotifyListener bDNotifyListener = next;
                Location.distanceBetween(bDLocation.getLatitude(), bDLocation.getLongitude(), next.mLatitudeC, next.mLongitudeC, fArr);
                float radius = (fArr[0] - bDNotifyListener.mRadius) - bDLocation.getRadius();
                if (radius > 0.0f) {
                    if (radius < f2) {
                        f2 = radius;
                    }
                } else if (bDNotifyListener.Notified < 3) {
                    bDNotifyListener.Notified++;
                    bDNotifyListener.onNotify(bDLocation2, fArr[0]);
                    if (bDNotifyListener.Notified < 3) {
                        this.i = true;
                    }
                }
            }
            if (f2 < this.b) {
                this.b = f2;
            }
            this.g = 0;
            c();
        }
    }

    private boolean b() {
        ArrayList<BDNotifyListener> arrayList = this.a;
        boolean z = false;
        if (arrayList != null && !arrayList.isEmpty()) {
            Iterator<BDNotifyListener> it = this.a.iterator();
            while (it.hasNext()) {
                if (it.next().Notified < 3) {
                    z = true;
                }
            }
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0045, code lost:
        if (((long) r2) > ((r7.h + ((long) r0)) - java.lang.System.currentTimeMillis())) goto L_0x0049;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void c() {
        /*
            r7 = this;
            boolean r0 = r7.b()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            float r0 = r7.b
            r1 = 1167867904(0x459c4000, float:5000.0)
            r2 = 10000(0x2710, float:1.4013E-41)
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0016
            r0 = 600000(0x927c0, float:8.40779E-40)
            goto L_0x002c
        L_0x0016:
            r1 = 1148846080(0x447a0000, float:1000.0)
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x0020
            r0 = 120000(0x1d4c0, float:1.68156E-40)
            goto L_0x002c
        L_0x0020:
            r1 = 1140457472(0x43fa0000, float:500.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x002a
            r0 = 60000(0xea60, float:8.4078E-41)
            goto L_0x002c
        L_0x002a:
            r0 = 10000(0x2710, float:1.4013E-41)
        L_0x002c:
            boolean r1 = r7.i
            r3 = 0
            if (r1 == 0) goto L_0x0034
            r7.i = r3
            goto L_0x0035
        L_0x0034:
            r2 = r0
        L_0x0035:
            int r0 = r7.g
            if (r0 == 0) goto L_0x0048
            long r4 = r7.h
            long r0 = (long) r0
            long r4 = r4 + r0
            long r0 = java.lang.System.currentTimeMillis()
            long r4 = r4 - r0
            long r0 = (long) r2
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0048
            goto L_0x0049
        L_0x0048:
            r3 = 1
        L_0x0049:
            if (r3 == 0) goto L_0x0059
            r7.g = r2
            long r0 = java.lang.System.currentTimeMillis()
            r7.h = r0
            int r0 = r7.g
            long r0 = (long) r0
            r7.a((long) r0)
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.d.a.c():void");
    }

    public int a(BDNotifyListener bDNotifyListener) {
        if (this.a == null) {
            this.a = new ArrayList<>();
        }
        this.a.add(bDNotifyListener);
        bDNotifyListener.isAdded = true;
        bDNotifyListener.mNotifyCache = this;
        if (!this.n) {
            this.f.registerReceiver(this.l, new IntentFilter("android.com.baidu.location.TIMER.NOTIFY"), "android.permission.ACCESS_FINE_LOCATION", (Handler) null);
            this.n = true;
        }
        if (bDNotifyListener.mCoorType == null) {
            return 1;
        }
        if (!bDNotifyListener.mCoorType.equals(CoordinateType.GCJ02)) {
            double[] coorEncrypt = Jni.coorEncrypt(bDNotifyListener.mLongitude, bDNotifyListener.mLatitude, bDNotifyListener.mCoorType + "2gcj");
            bDNotifyListener.mLongitudeC = coorEncrypt[0];
            bDNotifyListener.mLatitudeC = coorEncrypt[1];
        }
        if (this.c == null || System.currentTimeMillis() - this.d > 30000) {
            this.e.requestNotifyLocation();
        } else {
            float[] fArr = new float[1];
            Location.distanceBetween(this.c.getLatitude(), this.c.getLongitude(), bDNotifyListener.mLatitudeC, bDNotifyListener.mLongitudeC, fArr);
            float radius = (fArr[0] - bDNotifyListener.mRadius) - this.c.getRadius();
            if (radius > 0.0f) {
                if (radius < this.b) {
                    this.b = radius;
                }
            } else if (bDNotifyListener.Notified < 3) {
                bDNotifyListener.Notified++;
                bDNotifyListener.onNotify(this.c, fArr[0]);
                if (bDNotifyListener.Notified < 3) {
                    this.i = true;
                }
            }
        }
        c();
        return 1;
    }

    public void a() {
        PendingIntent pendingIntent = this.j;
        if (pendingIntent != null) {
            this.k.cancel(pendingIntent);
        }
        this.c = null;
        this.d = 0;
        if (this.n) {
            this.f.unregisterReceiver(this.l);
        }
        this.n = false;
    }

    public void b(BDNotifyListener bDNotifyListener) {
        if (bDNotifyListener.mCoorType != null) {
            if (!bDNotifyListener.mCoorType.equals(CoordinateType.GCJ02)) {
                double[] coorEncrypt = Jni.coorEncrypt(bDNotifyListener.mLongitude, bDNotifyListener.mLatitude, bDNotifyListener.mCoorType + "2gcj");
                bDNotifyListener.mLongitudeC = coorEncrypt[0];
                bDNotifyListener.mLatitudeC = coorEncrypt[1];
            }
            if (this.c == null || System.currentTimeMillis() - this.d > 300000) {
                this.e.requestNotifyLocation();
            } else {
                float[] fArr = new float[1];
                Location.distanceBetween(this.c.getLatitude(), this.c.getLongitude(), bDNotifyListener.mLatitudeC, bDNotifyListener.mLongitudeC, fArr);
                float radius = (fArr[0] - bDNotifyListener.mRadius) - this.c.getRadius();
                if (radius > 0.0f) {
                    if (radius < this.b) {
                        this.b = radius;
                    }
                } else if (bDNotifyListener.Notified < 3) {
                    bDNotifyListener.Notified++;
                    bDNotifyListener.onNotify(this.c, fArr[0]);
                    if (bDNotifyListener.Notified < 3) {
                        this.i = true;
                    }
                }
            }
            c();
        }
    }

    public int c(BDNotifyListener bDNotifyListener) {
        PendingIntent pendingIntent;
        ArrayList<BDNotifyListener> arrayList = this.a;
        if (arrayList == null) {
            return 0;
        }
        if (arrayList.contains(bDNotifyListener)) {
            this.a.remove(bDNotifyListener);
        }
        if (this.a.size() != 0 || (pendingIntent = this.j) == null) {
            return 1;
        }
        this.k.cancel(pendingIntent);
        return 1;
    }
}
