package com.baidu.location;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import com.baidu.location.b.c;
import com.baidu.location.g.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public final class LocationClient implements c.a {
    public static final int CONNECT_HOT_SPOT_FALSE = 0;
    public static final int CONNECT_HOT_SPOT_TRUE = 1;
    public static final int CONNECT_HOT_SPOT_UNKNOWN = -1;
    public static final int LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS = 1;
    public static final int LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI = 2;
    public static final int LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN = 9;
    public static final int LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION = 4;
    public static final int LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET = 3;
    public static final int LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE = 7;
    public static final int LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI = 6;
    public static final int LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH = 5;
    public static final int LOC_DIAGNOSTIC_TYPE_SERVER_FAIL = 8;
    private Boolean A = false;
    private Boolean B = false;
    /* access modifiers changed from: private */
    public Boolean C = true;
    private boolean D;
    /* access modifiers changed from: private */
    public c E = null;
    /* access modifiers changed from: private */
    public boolean F = false;
    /* access modifiers changed from: private */
    public boolean G = false;
    private boolean H = false;
    private ServiceConnection I = new b(this);
    private long a = 0;
    private String b = null;
    /* access modifiers changed from: private */
    public LocationClientOption c = new LocationClientOption();
    /* access modifiers changed from: private */
    public LocationClientOption d = new LocationClientOption();
    /* access modifiers changed from: private */
    public boolean e = false;
    /* access modifiers changed from: private */
    public Context f = null;
    /* access modifiers changed from: private */
    public Messenger g = null;
    /* access modifiers changed from: private */
    public a h;
    /* access modifiers changed from: private */
    public final Messenger i;
    /* access modifiers changed from: private */
    public ArrayList<BDLocationListener> j = null;
    /* access modifiers changed from: private */
    public ArrayList<BDAbstractLocationListener> k = null;
    private BDLocation l = null;
    private boolean m = false;
    /* access modifiers changed from: private */
    public boolean n = false;
    /* access modifiers changed from: private */
    public boolean o = false;
    /* access modifiers changed from: private */
    public b p = null;
    /* access modifiers changed from: private */
    public boolean q = false;
    /* access modifiers changed from: private */
    public final Object r = new Object();
    private long s = 0;
    private long t = 0;
    private com.baidu.location.d.a u = null;
    private BDLocationListener v = null;
    private String w = null;
    private String x;
    private boolean y = false;
    /* access modifiers changed from: private */
    public boolean z = true;

    private static class a extends Handler {
        private final WeakReference<LocationClient> a;

        a(Looper looper, LocationClient locationClient) {
            super(looper);
            this.a = new WeakReference<>(locationClient);
        }

        public void handleMessage(Message message) {
            LocationClient locationClient = (LocationClient) this.a.get();
            if (locationClient != null) {
                int i = message.what;
                int i2 = 21;
                boolean z = true;
                if (i == 21) {
                    Bundle data = message.getData();
                    data.setClassLoader(BDLocation.class.getClassLoader());
                    BDLocation bDLocation = (BDLocation) data.getParcelable("locStr");
                    if (locationClient.G || !locationClient.F || bDLocation.getLocType() != 66) {
                        if (!locationClient.G && locationClient.F) {
                            boolean unused = locationClient.G = true;
                            return;
                        } else if (!locationClient.G) {
                            boolean unused2 = locationClient.G = true;
                        }
                    } else {
                        return;
                    }
                } else if (i == 303) {
                    Bundle data2 = message.getData();
                    int i3 = data2.getInt("loctype");
                    int i4 = data2.getInt("diagtype");
                    byte[] byteArray = data2.getByteArray("diagmessage");
                    if (i3 > 0 && i4 > 0 && byteArray != null && locationClient.k != null) {
                        Iterator it = locationClient.k.iterator();
                        while (it.hasNext()) {
                            ((BDAbstractLocationListener) it.next()).onLocDiagnosticMessage(i3, i4, new String(byteArray, "UTF-8"));
                        }
                        return;
                    }
                    return;
                } else if (i == 406) {
                    Bundle data3 = message.getData();
                    byte[] byteArray2 = data3.getByteArray("mac");
                    String str = null;
                    if (byteArray2 != null) {
                        str = new String(byteArray2, "UTF-8");
                    }
                    int i5 = data3.getInt("hotspot", -1);
                    if (locationClient.k != null) {
                        Iterator it2 = locationClient.k.iterator();
                        while (it2.hasNext()) {
                            ((BDAbstractLocationListener) it2.next()).onConnectHotSpotMessage(str, i5);
                        }
                        return;
                    }
                    return;
                } else if (i == 701) {
                    locationClient.a((BDLocation) message.obj);
                    return;
                } else if (i == 1300) {
                    locationClient.f(message);
                    return;
                } else if (i != 1400) {
                    i2 = 26;
                    if (i != 26) {
                        if (i != 27) {
                            if (i != 54) {
                                z = false;
                                if (i != 55) {
                                    if (i == 703) {
                                        Bundle data4 = message.getData();
                                        int i6 = data4.getInt(TtmlNode.ATTR_ID, 0);
                                        if (i6 > 0) {
                                            locationClient.a(i6, (Notification) data4.getParcelable("notification"));
                                            return;
                                        }
                                        return;
                                    } else if (i != 704) {
                                        switch (i) {
                                            case 1:
                                                locationClient.b();
                                                return;
                                            case 2:
                                                locationClient.c();
                                                return;
                                            case 3:
                                                locationClient.c(message);
                                                return;
                                            case 4:
                                                locationClient.f();
                                                return;
                                            case 5:
                                                locationClient.e(message);
                                                return;
                                            case 6:
                                                locationClient.h(message);
                                                return;
                                            case 7:
                                                return;
                                            case 8:
                                                locationClient.d(message);
                                                return;
                                            case 9:
                                                locationClient.a(message);
                                                return;
                                            case 10:
                                                locationClient.b(message);
                                                return;
                                            case 11:
                                                locationClient.e();
                                                return;
                                            case 12:
                                                locationClient.a();
                                                return;
                                            default:
                                                super.handleMessage(message);
                                                return;
                                        }
                                    } else {
                                        try {
                                            locationClient.a(message.getData().getBoolean("removenotify"));
                                            return;
                                        } catch (Exception e) {
                                            return;
                                        }
                                    }
                                } else if (!locationClient.c.location_change_notify) {
                                    return;
                                }
                            } else if (!locationClient.c.location_change_notify) {
                                return;
                            }
                            boolean unused3 = locationClient.q = z;
                            return;
                        }
                        locationClient.i(message);
                        return;
                    }
                } else {
                    locationClient.g(message);
                    return;
                }
                locationClient.a(message, i2);
            }
        }
    }

    private class b implements Runnable {
        private b() {
        }

        /* synthetic */ b(LocationClient locationClient, b bVar) {
            this();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:29:0x008f, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0091, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r5 = this;
                com.baidu.location.LocationClient r0 = com.baidu.location.LocationClient.this
                java.lang.Object r0 = r0.r
                monitor-enter(r0)
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                r2 = 0
                boolean unused = r1.o = r2     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                android.os.Messenger r1 = r1.g     // Catch:{ all -> 0x0092 }
                if (r1 == 0) goto L_0x0090
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                android.os.Messenger r1 = r1.i     // Catch:{ all -> 0x0092 }
                if (r1 != 0) goto L_0x001e
                goto L_0x0090
            L_0x001e:
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                java.util.ArrayList r1 = r1.j     // Catch:{ all -> 0x0092 }
                r2 = 1
                if (r1 == 0) goto L_0x0033
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                java.util.ArrayList r1 = r1.j     // Catch:{ all -> 0x0092 }
                int r1 = r1.size()     // Catch:{ all -> 0x0092 }
                if (r1 >= r2) goto L_0x0048
            L_0x0033:
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                java.util.ArrayList r1 = r1.k     // Catch:{ all -> 0x0092 }
                if (r1 == 0) goto L_0x008e
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                java.util.ArrayList r1 = r1.k     // Catch:{ all -> 0x0092 }
                int r1 = r1.size()     // Catch:{ all -> 0x0092 }
                if (r1 >= r2) goto L_0x0048
                goto L_0x008e
            L_0x0048:
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                boolean r1 = r1.n     // Catch:{ all -> 0x0092 }
                if (r1 == 0) goto L_0x007e
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient$b r1 = r1.p     // Catch:{ all -> 0x0092 }
                if (r1 != 0) goto L_0x0064
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient$b r2 = new com.baidu.location.LocationClient$b     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient r3 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                r2.<init>()     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient.b unused = r1.p = r2     // Catch:{ all -> 0x0092 }
            L_0x0064:
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient$a r1 = r1.h     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient r2 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient$b r2 = r2.p     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient r3 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClientOption r3 = r3.c     // Catch:{ all -> 0x0092 }
                int r3 = r3.scanSpan     // Catch:{ all -> 0x0092 }
                long r3 = (long) r3     // Catch:{ all -> 0x0092 }
                r1.postDelayed(r2, r3)     // Catch:{ all -> 0x0092 }
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x007e:
                com.baidu.location.LocationClient r1 = com.baidu.location.LocationClient.this     // Catch:{ all -> 0x0092 }
                com.baidu.location.LocationClient$a r1 = r1.h     // Catch:{ all -> 0x0092 }
                r2 = 4
                android.os.Message r1 = r1.obtainMessage(r2)     // Catch:{ all -> 0x0092 }
                r1.sendToTarget()     // Catch:{ all -> 0x0092 }
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x008e:
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x0090:
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                return
            L_0x0092:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0092 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.LocationClient.b.run():void");
        }
    }

    public LocationClient(Context context) {
        this.f = context;
        this.c = new LocationClientOption();
        this.h = new a(Looper.getMainLooper(), this);
        this.i = new Messenger(this.h);
    }

    public LocationClient(Context context, LocationClientOption locationClientOption) {
        this.f = context;
        this.c = locationClientOption;
        this.d = new LocationClientOption(locationClientOption);
        this.h = new a(Looper.getMainLooper(), this);
        this.i = new Messenger(this.h);
    }

    /* access modifiers changed from: private */
    public void a() {
        Message obtain = Message.obtain((Handler) null, 28);
        try {
            obtain.replyTo = this.i;
            this.g.send(obtain);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2, Notification notification) {
        try {
            Intent intent = new Intent(this.f, f.class);
            intent.putExtra("notification", notification);
            intent.putExtra(TtmlNode.ATTR_ID, i2);
            intent.putExtra("command", 1);
            if (Build.VERSION.SDK_INT >= 26) {
                this.f.startForegroundService(intent);
            } else {
                this.f.startService(intent);
            }
            this.H = true;
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        if (message != null && message.obj != null) {
            BDNotifyListener bDNotifyListener = (BDNotifyListener) message.obj;
            if (this.u == null) {
                this.u = new com.baidu.location.d.a(this.f, this);
            }
            this.u.a(bDNotifyListener);
        }
    }

    /* access modifiers changed from: private */
    public void a(Message message, int i2) {
        if (this.e) {
            try {
                Bundle data = message.getData();
                data.setClassLoader(BDLocation.class.getClassLoader());
                BDLocation bDLocation = (BDLocation) data.getParcelable("locStr");
                this.l = bDLocation;
                if (bDLocation.getLocType() == 61) {
                    this.s = System.currentTimeMillis();
                }
                b(i2);
            } catch (Exception e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation) {
        if (!this.z) {
            this.l = bDLocation;
            if (!this.G && bDLocation.getLocType() == 161) {
                this.F = true;
            }
            ArrayList<BDLocationListener> arrayList = this.j;
            if (arrayList != null) {
                Iterator<BDLocationListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().onReceiveLocation(bDLocation);
                }
            }
            ArrayList<BDAbstractLocationListener> arrayList2 = this.k;
            if (arrayList2 != null) {
                Iterator<BDAbstractLocationListener> it2 = arrayList2.iterator();
                while (it2.hasNext()) {
                    it2.next().onReceiveLocation(bDLocation);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        try {
            Intent intent = new Intent(this.f, f.class);
            intent.putExtra("removenotify", z2);
            intent.putExtra("command", 2);
            this.f.startService(intent);
            this.H = true;
        } catch (Exception e2) {
        }
    }

    private boolean a(int i2) {
        if (this.g != null && this.e) {
            try {
                this.g.send(Message.obtain((Handler) null, i2));
                return true;
            } catch (Exception e2) {
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void b() {
        if (!this.e) {
            if (this.C.booleanValue()) {
                boolean c2 = k.c(this.f);
                if (this.d.isOnceLocation()) {
                    c2 = true;
                }
                if (c2) {
                    try {
                        new c(this).start();
                    } catch (Throwable th) {
                    }
                }
            }
            if (!this.d.isOnceLocation()) {
                this.C = false;
                this.b = this.f.getPackageName();
                this.w = this.b + "_bdls_v2.9";
                Intent intent = new Intent(this.f, f.class);
                try {
                    intent.putExtra("debug_dev", this.D);
                } catch (Exception e2) {
                }
                if (this.c == null) {
                    this.c = new LocationClientOption();
                }
                intent.putExtra("cache_exception", this.c.isIgnoreCacheException);
                intent.putExtra("kill_process", this.c.isIgnoreKillProcess);
                try {
                    this.f.bindService(intent, this.I, 1);
                } catch (Exception e3) {
                    e3.printStackTrace();
                    this.e = false;
                }
            }
        }
    }

    private void b(int i2) {
        if (this.l.getCoorType() == null) {
            this.l.setCoorType(this.c.coorType);
        }
        if (this.m || ((this.c.location_change_notify && this.l.getLocType() == 61) || this.l.getLocType() == 66 || this.l.getLocType() == 67 || this.y || this.l.getLocType() == 161)) {
            ArrayList<BDLocationListener> arrayList = this.j;
            if (arrayList != null) {
                Iterator<BDLocationListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().onReceiveLocation(this.l);
                }
            }
            ArrayList<BDAbstractLocationListener> arrayList2 = this.k;
            if (arrayList2 != null) {
                Iterator<BDAbstractLocationListener> it2 = arrayList2.iterator();
                while (it2.hasNext()) {
                    it2.next().onReceiveLocation(this.l);
                }
            }
            if (this.l.getLocType() != 66 && this.l.getLocType() != 67) {
                this.m = false;
                this.t = System.currentTimeMillis();
            }
        }
    }

    /* access modifiers changed from: private */
    public void b(Message message) {
        if (message != null && message.obj != null) {
            BDNotifyListener bDNotifyListener = (BDNotifyListener) message.obj;
            com.baidu.location.d.a aVar = this.u;
            if (aVar != null) {
                aVar.c(bDNotifyListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        if (this.e && this.g != null) {
            Message obtain = Message.obtain((Handler) null, 12);
            obtain.replyTo = this.i;
            try {
                this.g.send(obtain);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                this.f.unbindService(this.I);
                if (this.H) {
                    try {
                        this.f.stopService(new Intent(this.f, f.class));
                    } catch (Exception e3) {
                    }
                    this.H = false;
                }
            } catch (Exception e4) {
            }
            synchronized (this.r) {
                try {
                    if (this.o) {
                        this.h.removeCallbacks(this.p);
                        this.o = false;
                    }
                } catch (Exception e5) {
                }
            }
            com.baidu.location.d.a aVar = this.u;
            if (aVar != null) {
                aVar.a();
            }
            this.g = null;
            this.n = false;
            this.y = false;
            this.e = false;
            this.F = false;
            this.G = false;
        }
    }

    /* access modifiers changed from: private */
    public void c(Message message) {
        this.n = false;
        if (message != null && message.obj != null) {
            LocationClientOption locationClientOption = (LocationClientOption) message.obj;
            if (!this.c.optionEquals(locationClientOption)) {
                if (this.c.scanSpan != locationClientOption.scanSpan) {
                    try {
                        synchronized (this.r) {
                            if (this.o) {
                                this.h.removeCallbacks(this.p);
                                this.o = false;
                            }
                            if (locationClientOption.scanSpan >= 1000 && !this.o) {
                                if (this.p == null) {
                                    this.p = new b(this, (b) null);
                                }
                                this.h.postDelayed(this.p, (long) locationClientOption.scanSpan);
                                this.o = true;
                            }
                        }
                    } catch (Exception e2) {
                    }
                }
                this.c = new LocationClientOption(locationClientOption);
                if (this.g != null) {
                    try {
                        Message obtain = Message.obtain((Handler) null, 15);
                        obtain.replyTo = this.i;
                        obtain.setData(d());
                        this.g.send(obtain);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public Bundle d() {
        if (this.c == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putString("packName", this.b);
        bundle.putString("prodName", this.c.prodName);
        bundle.putString("coorType", this.c.coorType);
        bundle.putString("addrType", this.c.addrType);
        bundle.putBoolean("openGPS", this.c.openGps);
        bundle.putBoolean("location_change_notify", this.c.location_change_notify);
        bundle.putInt("scanSpan", this.c.scanSpan);
        bundle.putBoolean("enableSimulateGps", this.c.enableSimulateGps);
        bundle.putInt("timeOut", this.c.timeOut);
        bundle.putInt("priority", this.c.priority);
        bundle.putBoolean("map", this.A.booleanValue());
        bundle.putBoolean("import", this.B.booleanValue());
        bundle.putBoolean("needDirect", this.c.mIsNeedDeviceDirect);
        bundle.putBoolean("isneedaptag", this.c.isNeedAptag);
        bundle.putBoolean("isneedpoiregion", this.c.isNeedPoiRegion);
        bundle.putBoolean("isneedregular", this.c.isNeedRegular);
        bundle.putBoolean("isneedaptagd", this.c.isNeedAptagd);
        bundle.putBoolean("isneedaltitude", this.c.isNeedAltitude);
        bundle.putBoolean("isneednewrgc", this.c.isNeedNewVersionRgc);
        bundle.putInt("autoNotifyMaxInterval", this.c.a());
        bundle.putInt("autoNotifyMinTimeInterval", this.c.getAutoNotifyMinTimeInterval());
        bundle.putInt("autoNotifyMinDistance", this.c.getAutoNotifyMinDistance());
        bundle.putFloat("autoNotifyLocSensitivity", this.c.b());
        bundle.putInt("wifitimeout", this.c.wifiCacheTimeOut);
        return bundle;
    }

    /* access modifiers changed from: private */
    public void d(Message message) {
        if (message != null && message.obj != null) {
            this.v = (BDLocationListener) message.obj;
        }
    }

    /* access modifiers changed from: private */
    public void e() {
        if (this.g != null) {
            Message obtain = Message.obtain((Handler) null, 22);
            try {
                obtain.replyTo = this.i;
                this.g.send(obtain);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void e(Message message) {
        if (message != null && message.obj != null) {
            BDLocationListener bDLocationListener = (BDLocationListener) message.obj;
            if (this.j == null) {
                this.j = new ArrayList<>();
            }
            if (!this.j.contains(bDLocationListener)) {
                this.j.add(bDLocationListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        LocationClientOption locationClientOption;
        if (this.g != null) {
            if ((System.currentTimeMillis() - this.s > 3000 || (((locationClientOption = this.c) != null && !locationClientOption.location_change_notify) || this.n)) && (!this.y || System.currentTimeMillis() - this.t > 20000 || this.n)) {
                Message obtain = Message.obtain((Handler) null, 22);
                if (this.n) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isWaitingLocTag", this.n);
                    this.n = false;
                    obtain.setData(bundle);
                }
                try {
                    obtain.replyTo = this.i;
                    this.g.send(obtain);
                    this.a = System.currentTimeMillis();
                    this.m = true;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            synchronized (this.r) {
                if (this.c != null && this.c.scanSpan >= 1000 && !this.o) {
                    if (this.p == null) {
                        this.p = new b(this, (b) null);
                    }
                    this.h.postDelayed(this.p, (long) this.c.scanSpan);
                    this.o = true;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void f(Message message) {
        if (message != null && message.obj != null) {
            BDAbstractLocationListener bDAbstractLocationListener = (BDAbstractLocationListener) message.obj;
            if (this.k == null) {
                this.k = new ArrayList<>();
            }
            if (!this.k.contains(bDAbstractLocationListener)) {
                this.k.add(bDAbstractLocationListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void g(Message message) {
        if (message != null && message.obj != null) {
            BDAbstractLocationListener bDAbstractLocationListener = (BDAbstractLocationListener) message.obj;
            ArrayList<BDAbstractLocationListener> arrayList = this.k;
            if (arrayList != null && arrayList.contains(bDAbstractLocationListener)) {
                this.k.remove(bDAbstractLocationListener);
            }
        }
    }

    public static BDLocation getBDLocationInCoorType(BDLocation bDLocation, String str) {
        BDLocation bDLocation2 = new BDLocation(bDLocation);
        double[] coorEncrypt = Jni.coorEncrypt(bDLocation.getLongitude(), bDLocation.getLatitude(), str);
        bDLocation2.setLatitude(coorEncrypt[1]);
        bDLocation2.setLongitude(coorEncrypt[0]);
        return bDLocation2;
    }

    /* access modifiers changed from: private */
    public void h(Message message) {
        if (message != null && message.obj != null) {
            BDLocationListener bDLocationListener = (BDLocationListener) message.obj;
            ArrayList<BDLocationListener> arrayList = this.j;
            if (arrayList != null && arrayList.contains(bDLocationListener)) {
                this.j.remove(bDLocationListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void i(Message message) {
        try {
            Bundle data = message.getData();
            data.setClassLoader(BDLocation.class.getClassLoader());
            BDLocation bDLocation = (BDLocation) data.getParcelable("locStr");
            if (this.v == null) {
                return;
            }
            if (this.c == null || !this.c.isDisableCache() || bDLocation.getLocType() != 65) {
                this.v.onReceiveLocation(bDLocation);
            }
        } catch (Exception e2) {
        }
    }

    public void disableAssistantLocation() {
        com.baidu.location.b.k.a().b();
    }

    public void disableLocInForeground(boolean z2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("removenotify", z2);
        Message obtainMessage = this.h.obtainMessage(704);
        obtainMessage.setData(bundle);
        obtainMessage.sendToTarget();
    }

    public void enableAssistantLocation(WebView webView) {
        com.baidu.location.b.k.a().a(this.f, webView, this);
    }

    public void enableLocInForeground(int i2, Notification notification) {
        if (i2 <= 0 || notification == null) {
            Log.e("baidu_location_Client", "can not startLocInForeground if the param is unlegal");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(TtmlNode.ATTR_ID, i2);
        bundle.putParcelable("notification", notification);
        Message obtainMessage = this.h.obtainMessage(703);
        obtainMessage.setData(bundle);
        obtainMessage.sendToTarget();
    }

    public String getAccessKey() {
        try {
            String b2 = com.baidu.location.a.a.b(this.f);
            this.x = b2;
            if (!TextUtils.isEmpty(b2)) {
                return String.format("KEY=%s", new Object[]{this.x});
            }
            throw new IllegalStateException("please setting key from Manifest.xml");
        } catch (Exception e2) {
            return null;
        }
    }

    public BDLocation getLastKnownLocation() {
        return this.l;
    }

    public LocationClientOption getLocOption() {
        return this.c;
    }

    public String getVersion() {
        return "8.2.2";
    }

    public boolean isStarted() {
        return this.e;
    }

    public void onReceiveLightLocString(String str) {
    }

    public void onReceiveLocation(BDLocation bDLocation) {
        if ((!this.G || this.F) && bDLocation != null) {
            Message obtainMessage = this.h.obtainMessage(701);
            obtainMessage.obj = bDLocation;
            obtainMessage.sendToTarget();
        }
    }

    public void registerLocationListener(BDAbstractLocationListener bDAbstractLocationListener) {
        if (bDAbstractLocationListener != null) {
            Message obtainMessage = this.h.obtainMessage(1300);
            obtainMessage.obj = bDAbstractLocationListener;
            obtainMessage.sendToTarget();
            return;
        }
        throw new IllegalStateException("please set a non-null listener");
    }

    public void registerLocationListener(BDLocationListener bDLocationListener) {
        if (bDLocationListener != null) {
            Message obtainMessage = this.h.obtainMessage(5);
            obtainMessage.obj = bDLocationListener;
            obtainMessage.sendToTarget();
            return;
        }
        throw new IllegalStateException("please set a non-null listener");
    }

    public void registerNotify(BDNotifyListener bDNotifyListener) {
        Message obtainMessage = this.h.obtainMessage(9);
        obtainMessage.obj = bDNotifyListener;
        obtainMessage.sendToTarget();
    }

    public void registerNotifyLocationListener(BDLocationListener bDLocationListener) {
        Message obtainMessage = this.h.obtainMessage(8);
        obtainMessage.obj = bDLocationListener;
        obtainMessage.sendToTarget();
    }

    public void removeNotifyEvent(BDNotifyListener bDNotifyListener) {
        Message obtainMessage = this.h.obtainMessage(10);
        obtainMessage.obj = bDNotifyListener;
        obtainMessage.sendToTarget();
    }

    public boolean requestHotSpotState() {
        if (this.g != null && this.e) {
            try {
                this.g.send(Message.obtain((Handler) null, 406));
                return true;
            } catch (Exception e2) {
            }
        }
        return false;
    }

    public int requestLocation() {
        ArrayList<BDAbstractLocationListener> arrayList;
        if (this.g == null || this.i == null) {
            return 1;
        }
        ArrayList<BDLocationListener> arrayList2 = this.j;
        if ((arrayList2 == null || arrayList2.size() < 1) && ((arrayList = this.k) == null || arrayList.size() < 1)) {
            return 2;
        }
        if (System.currentTimeMillis() - this.a < 1000) {
            return 6;
        }
        this.n = true;
        Message obtainMessage = this.h.obtainMessage(4);
        obtainMessage.arg1 = 0;
        obtainMessage.sendToTarget();
        return 0;
    }

    public void requestNotifyLocation() {
        this.h.obtainMessage(11).sendToTarget();
    }

    public int requestOfflineLocation() {
        ArrayList<BDAbstractLocationListener> arrayList;
        if (this.g == null || this.i == null) {
            return 1;
        }
        ArrayList<BDLocationListener> arrayList2 = this.j;
        if ((arrayList2 == null || arrayList2.size() < 1) && ((arrayList = this.k) == null || arrayList.size() < 1)) {
            return 2;
        }
        this.h.obtainMessage(12).sendToTarget();
        return 0;
    }

    public void restart() {
        stop();
        this.z = false;
        this.h.sendEmptyMessageDelayed(1, 1000);
    }

    public void setLocOption(LocationClientOption locationClientOption) {
        if (locationClientOption == null) {
            locationClientOption = new LocationClientOption();
        }
        if (locationClientOption.a() > 0) {
            locationClientOption.setScanSpan(0);
            locationClientOption.setLocationNotify(true);
        }
        this.d = new LocationClientOption(locationClientOption);
        Message obtainMessage = this.h.obtainMessage(3);
        obtainMessage.obj = locationClientOption;
        obtainMessage.sendToTarget();
    }

    public void start() {
        this.z = false;
        if (!k.b()) {
            this.h.obtainMessage(1).sendToTarget();
        }
    }

    public boolean startIndoorMode() {
        boolean a2 = a(110);
        if (a2) {
            this.y = true;
        }
        return a2;
    }

    public void stop() {
        this.z = true;
        this.h.obtainMessage(2).sendToTarget();
        this.E = null;
    }

    public boolean stopIndoorMode() {
        boolean a2 = a(111);
        if (a2) {
            this.y = false;
        }
        return a2;
    }

    public void unRegisterLocationListener(BDAbstractLocationListener bDAbstractLocationListener) {
        if (bDAbstractLocationListener != null) {
            Message obtainMessage = this.h.obtainMessage(1400);
            obtainMessage.obj = bDAbstractLocationListener;
            obtainMessage.sendToTarget();
            return;
        }
        throw new IllegalStateException("please set a non-null listener");
    }

    public void unRegisterLocationListener(BDLocationListener bDLocationListener) {
        if (bDLocationListener != null) {
            Message obtainMessage = this.h.obtainMessage(6);
            obtainMessage.obj = bDLocationListener;
            obtainMessage.sendToTarget();
            return;
        }
        throw new IllegalStateException("please set a non-null listener");
    }

    public boolean updateLocation(Location location) {
        if (this.g == null || this.i == null || location == null) {
            return false;
        }
        try {
            Message obtain = Message.obtain((Handler) null, 57);
            obtain.obj = location;
            this.g.send(obtain);
            return true;
        } catch (Exception e2) {
            e2.printStackTrace();
            return true;
        }
    }
}
