package com.baidu.location.b;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;

public class k {
    /* access modifiers changed from: private */
    public static long j = 12000;
    public e a;
    private Context b;
    /* access modifiers changed from: private */
    public WebView c;
    /* access modifiers changed from: private */
    public LocationClient d;
    /* access modifiers changed from: private */
    public a e;
    /* access modifiers changed from: private */
    public List<b> f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public long h;
    /* access modifiers changed from: private */
    public BDLocation i;
    /* access modifiers changed from: private */
    public f k;
    /* access modifiers changed from: private */
    public boolean l;

    private class a extends Handler {
        a(Looper looper) {
            super(looper);
        }

        private String a(BDLocation bDLocation) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("latitude", bDLocation.getLatitude());
                jSONObject.put("longitude", bDLocation.getLongitude());
                jSONObject.put("radius", (double) bDLocation.getRadius());
                jSONObject.put("errorcode", 1);
                if (bDLocation.hasAltitude()) {
                    jSONObject.put("altitude", bDLocation.getAltitude());
                }
                if (bDLocation.hasSpeed()) {
                    jSONObject.put("speed", (double) (bDLocation.getSpeed() / 3.6f));
                }
                if (bDLocation.getLocType() == 61) {
                    jSONObject.put("direction", (double) bDLocation.getDirection());
                }
                if (bDLocation.getBuildingName() != null) {
                    jSONObject.put("buildingname", bDLocation.getBuildingName());
                }
                if (bDLocation.getBuildingID() != null) {
                    jSONObject.put("buildingid", bDLocation.getBuildingID());
                }
                if (bDLocation.getFloor() != null) {
                    jSONObject.put("floor", bDLocation.getFloor());
                }
                return jSONObject.toString();
            } catch (Exception e) {
                return null;
            }
        }

        private void a(String str) {
            if (k.this.l) {
                k.this.e.removeCallbacks(k.this.k);
                boolean unused = k.this.l = false;
            }
            if (k.this.f != null && k.this.f.size() > 0) {
                Iterator it = k.this.f.iterator();
                while (it.hasNext()) {
                    try {
                        b bVar = (b) it.next();
                        if (bVar.b() != null) {
                            k.this.c.loadUrl("javascript:" + bVar.b() + "('" + str + "')");
                        }
                        it.remove();
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:40:0x011c  */
        /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r10) {
            /*
                r9 = this;
                int r0 = r10.what
                r1 = 2
                java.lang.String r2 = "errorcode"
                r3 = 0
                r4 = 0
                switch(r0) {
                    case 1: goto L_0x00bf;
                    case 2: goto L_0x00b2;
                    case 3: goto L_0x0087;
                    case 4: goto L_0x0032;
                    case 5: goto L_0x001d;
                    case 6: goto L_0x000c;
                    default: goto L_0x000a;
                }
            L_0x000a:
                goto L_0x0162
            L_0x000c:
                org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ Exception -> 0x0019 }
                r10.<init>()     // Catch:{ Exception -> 0x0019 }
                r10.put(r2, r1)     // Catch:{ Exception -> 0x0019 }
                java.lang.String r4 = r10.toString()     // Catch:{ Exception -> 0x0019 }
                goto L_0x001a
            L_0x0019:
                r10 = move-exception
            L_0x001a:
                if (r4 == 0) goto L_0x0162
                goto L_0x002d
            L_0x001d:
                org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ Exception -> 0x002a }
                r10.<init>()     // Catch:{ Exception -> 0x002a }
                r10.put(r2, r3)     // Catch:{ Exception -> 0x002a }
                java.lang.String r4 = r10.toString()     // Catch:{ Exception -> 0x002a }
                goto L_0x002b
            L_0x002a:
                r10 = move-exception
            L_0x002b:
                if (r4 == 0) goto L_0x0162
            L_0x002d:
                r9.a((java.lang.String) r4)
                goto L_0x0162
            L_0x0032:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.List r10 = r10.f
                if (r10 == 0) goto L_0x0048
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.List r10 = r10.f
                r10.clear()
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.List unused = r10.f = r4
            L_0x0048:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.LocationClient r10 = r10.d
                com.baidu.location.b.k r0 = com.baidu.location.b.k.this
                com.baidu.location.b.k$e r0 = r0.a
                r10.unRegisterLocationListener((com.baidu.location.BDAbstractLocationListener) r0)
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                r0 = 0
                long unused = r10.h = r0
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.BDLocation unused = r10.i = r4
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r10 = r10.k
                if (r10 == 0) goto L_0x0080
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                boolean r10 = r10.l
                if (r10 == 0) goto L_0x0080
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$a r10 = r10.e
                com.baidu.location.b.k r0 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r0 = r0.k
                r10.removeCallbacks(r0)
            L_0x0080:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                boolean unused = r10.l = r3
                goto L_0x0162
            L_0x0087:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.List r10 = r10.f
                if (r10 != 0) goto L_0x009a
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                java.util.List unused = r10.f = r0
                goto L_0x00a3
            L_0x009a:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                java.util.List r10 = r10.f
                r10.clear()
            L_0x00a3:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.LocationClient r10 = r10.d
                com.baidu.location.b.k r0 = com.baidu.location.b.k.this
                com.baidu.location.b.k$e r0 = r0.a
                r10.registerLocationListener((com.baidu.location.BDAbstractLocationListener) r0)
                goto L_0x0162
            L_0x00b2:
                java.lang.Object r10 = r10.obj
                com.baidu.location.BDLocation r10 = (com.baidu.location.BDLocation) r10
                java.lang.String r10 = r9.a((com.baidu.location.BDLocation) r10)
                r9.a((java.lang.String) r10)
                goto L_0x0162
            L_0x00bf:
                java.lang.Object r10 = r10.obj
                com.baidu.location.b.k$b r10 = (com.baidu.location.b.k.b) r10
                com.baidu.location.b.k r0 = com.baidu.location.b.k.this
                java.util.List r0 = r0.f
                if (r0 == 0) goto L_0x00d4
                com.baidu.location.b.k r0 = com.baidu.location.b.k.this
                java.util.List r0 = r0.f
                r0.add(r10)
            L_0x00d4:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.LocationClient r10 = r10.d
                if (r10 == 0) goto L_0x0162
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.LocationClient r10 = r10.d
                int r10 = r10.requestLocation()
                r0 = 1
                if (r10 == 0) goto L_0x0119
                long r5 = java.lang.System.currentTimeMillis()
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                long r7 = r10.h
                long r5 = r5 - r7
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.BDLocation r10 = r10.i
                if (r10 == 0) goto L_0x0119
                r7 = 10000(0x2710, double:4.9407E-320)
                int r10 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r10 > 0) goto L_0x0119
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$a r10 = r10.e
                android.os.Message r10 = r10.obtainMessage(r1)
                com.baidu.location.b.k r1 = com.baidu.location.b.k.this
                com.baidu.location.BDLocation r1 = r1.i
                r10.obj = r1
                r10.sendToTarget()
                r10 = 0
                goto L_0x011a
            L_0x0119:
                r10 = 1
            L_0x011a:
                if (r10 == 0) goto L_0x0162
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                boolean r10 = r10.l
                if (r10 == 0) goto L_0x0138
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$a r10 = r10.e
                com.baidu.location.b.k r1 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r1 = r1.k
                r10.removeCallbacks(r1)
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                boolean unused = r10.l = r3
            L_0x0138:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r10 = r10.k
                if (r10 != 0) goto L_0x014a
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r1 = new com.baidu.location.b.k$f
                r1.<init>()
                com.baidu.location.b.k.f unused = r10.k = r1
            L_0x014a:
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                com.baidu.location.b.k$a r10 = r10.e
                com.baidu.location.b.k r1 = com.baidu.location.b.k.this
                com.baidu.location.b.k$f r1 = r1.k
                long r2 = com.baidu.location.b.k.j
                r10.postDelayed(r1, r2)
                com.baidu.location.b.k r10 = com.baidu.location.b.k.this
                boolean unused = r10.l = r0
            L_0x0162:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.k.a.handleMessage(android.os.Message):void");
        }
    }

    private class b {
        private String b = null;
        private String c = null;
        private long d = 0;

        b(String str) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("action")) {
                    this.b = jSONObject.getString("action");
                }
                if (jSONObject.has("callback")) {
                    this.c = jSONObject.getString("callback");
                }
                if (jSONObject.has("timeout")) {
                    long j = jSONObject.getLong("timeout");
                    if (j >= 1000) {
                        long unused = k.j = j;
                    }
                }
                this.d = System.currentTimeMillis();
            } catch (Exception e) {
                this.b = null;
                this.c = null;
            }
        }

        public String a() {
            return this.b;
        }

        public String b() {
            return this.c;
        }
    }

    private static final class c {
        /* access modifiers changed from: private */
        public static final k a = new k();
    }

    private class d {
        private d() {
        }

        @JavascriptInterface
        public void sendMessage(String str) {
            if (str != null && k.this.g) {
                b bVar = new b(str);
                if (bVar.a() != null && bVar.a().equals("requestLoc") && k.this.e != null) {
                    Message obtainMessage = k.this.e.obtainMessage(1);
                    obtainMessage.obj = bVar;
                    obtainMessage.sendToTarget();
                }
            }
        }

        @JavascriptInterface
        public void showLog(String str) {
        }
    }

    public class e extends BDAbstractLocationListener {
        public e() {
        }

        public void onReceiveLocation(BDLocation bDLocation) {
            Message message;
            String str;
            if (k.this.g && bDLocation != null) {
                BDLocation bDLocation2 = new BDLocation(bDLocation);
                int locType = bDLocation2.getLocType();
                String coorType = bDLocation2.getCoorType();
                if (locType == 61 || locType == 161 || locType == 66) {
                    if (coorType != null) {
                        if (coorType.equals(CoordinateType.GCJ02)) {
                            bDLocation2 = LocationClient.getBDLocationInCoorType(bDLocation2, "gcj2wgs");
                        } else {
                            if (coorType.equals(BDLocation.BDLOCATION_GCJ02_TO_BD09)) {
                                str = BDLocation.BDLOCATION_BD09_TO_GCJ02;
                            } else if (coorType.equals("bd09ll")) {
                                str = BDLocation.BDLOCATION_BD09LL_TO_GCJ02;
                            }
                            bDLocation2 = LocationClient.getBDLocationInCoorType(LocationClient.getBDLocationInCoorType(bDLocation2, str), "gcj2wgs");
                        }
                    }
                    long unused = k.this.h = System.currentTimeMillis();
                    BDLocation unused2 = k.this.i = new BDLocation(bDLocation2);
                    message = k.this.e.obtainMessage(2);
                    message.obj = bDLocation2;
                } else {
                    message = k.this.e.obtainMessage(5);
                }
                message.sendToTarget();
            }
        }
    }

    private class f implements Runnable {
        private f() {
        }

        public void run() {
            boolean unused = k.this.l = false;
            k.this.e.obtainMessage(6).sendToTarget();
        }
    }

    private k() {
        this.b = null;
        this.d = null;
        this.a = new e();
        this.e = null;
        this.f = null;
        this.g = false;
        this.h = 0;
        this.i = null;
        this.k = null;
        this.l = false;
    }

    public static k a() {
        return c.a;
    }

    private void a(WebView webView) {
        webView.addJavascriptInterface(new d(), "BaiduLocAssistant");
    }

    public void a(Context context, WebView webView, LocationClient locationClient) {
        if (!this.g && Integer.valueOf(Build.VERSION.SDK_INT).intValue() >= 17) {
            this.b = context;
            this.c = webView;
            this.d = locationClient;
            a aVar = new a(Looper.getMainLooper());
            this.e = aVar;
            aVar.obtainMessage(3).sendToTarget();
            webView.getSettings().setJavaScriptEnabled(true);
            a(this.c);
            this.g = true;
        }
    }

    public void b() {
        if (this.g) {
            this.e.obtainMessage(4).sendToTarget();
            this.g = false;
        }
    }
}
