package com.baidu.mapsdkplatform.comapi.map;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import com.baidu.mapapi.common.EnvironmentUtilities;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.o;
import java.util.concurrent.atomic.AtomicBoolean;

public class ae extends TextureView implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, TextureView.SurfaceTextureListener, o.a {
    public static int a;
    public static int b;
    public static int c;
    private GestureDetector d;
    private Handler e;
    private boolean f = false;
    private SurfaceTexture g;
    /* access modifiers changed from: private */
    public o h = null;
    /* access modifiers changed from: private */
    public e i;

    public ae(Context context, ab abVar, String str, int i2) {
        super(context);
        a(context, abVar, str, i2);
    }

    private void a(Context context, ab abVar, String str, int i2) {
        setSurfaceTextureListener(this);
        if (context != null) {
            this.d = new GestureDetector(context, this);
            EnvironmentUtilities.initAppDirectory(context);
            if (this.i == null) {
                this.i = new e(context, str, i2);
            }
            this.i.a(context.hashCode());
            this.i.a();
            this.i.a(abVar);
            e();
            this.i.a(this.e);
            this.i.f();
            return;
        }
        throw new RuntimeException("BDMapSDKException: when you create an mapview, the context can not be null");
    }

    private void e() {
        this.e = new af(this);
    }

    public int a() {
        e eVar = this.i;
        if (eVar == null) {
            return 0;
        }
        if (c <= 1) {
            MapRenderer.nativeResize(eVar.j, a, b);
            c++;
        }
        return MapRenderer.nativeRender(this.i.j);
    }

    public void a(int i2) {
        synchronized (this) {
            if (this.i.h != null) {
                for (n next : this.i.h) {
                    if (next != null) {
                        next.f();
                    }
                }
            }
            if (this.i != null) {
                this.i.b(this.e);
                this.i.c(i2);
                this.i = null;
            }
            this.e.removeCallbacksAndMessages((Object) null);
            if (this.h != null) {
                this.h.c();
                this.h = null;
            }
            if (this.g != null) {
                if (Build.VERSION.SDK_INT >= 19) {
                    this.g.release();
                }
                this.g = null;
            }
        }
    }

    public void a(String str, Rect rect) {
        o oVar;
        e eVar = this.i;
        if (eVar != null && eVar.i != null) {
            if (rect != null) {
                int i2 = rect.left;
                int i3 = b < rect.bottom ? 0 : b - rect.bottom;
                int width = rect.width();
                int height = rect.height();
                if (i2 >= 0 && i3 >= 0 && width > 0 && height > 0) {
                    if (width > a) {
                        width = Math.abs(rect.width()) - (rect.right - a);
                    }
                    if (height > b) {
                        height = Math.abs(rect.height()) - (rect.bottom - b);
                    }
                    if (i2 > SysOSUtil.getScreenSizeX() || i3 > SysOSUtil.getScreenSizeY()) {
                        this.i.i.a(str, (Bundle) null);
                        o oVar2 = this.h;
                        if (oVar2 != null) {
                            oVar2.a();
                            return;
                        }
                        return;
                    }
                    a = width;
                    b = height;
                    Bundle bundle = new Bundle();
                    bundle.putInt("x", i2);
                    bundle.putInt("y", i3);
                    bundle.putInt("width", width);
                    bundle.putInt("height", height);
                    this.i.i.a(str, bundle);
                    oVar = this.h;
                    if (oVar == null) {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                this.i.i.a(str, (Bundle) null);
                oVar = this.h;
                if (oVar == null) {
                    return;
                }
            }
            oVar.a();
        }
    }

    public e b() {
        return this.i;
    }

    public void c() {
        e eVar = this.i;
        if (eVar != null && eVar.i != null) {
            if (this.i.h != null) {
                for (n next : this.i.h) {
                    if (next != null) {
                        next.d();
                    }
                }
            }
            this.i.i.g();
            this.i.i.d();
            this.i.i.n();
            o oVar = this.h;
            if (oVar != null) {
                oVar.a();
            }
            if (this.i.b()) {
                this.f = true;
            }
        }
    }

    public void d() {
        e eVar = this.i;
        if (eVar != null && eVar.i != null) {
            this.f = false;
            this.i.i.c();
            synchronized (this) {
                this.i.i.c();
                if (this.h != null) {
                    this.h.b();
                }
            }
        }
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        e eVar = this.i;
        if (eVar == null || eVar.i == null || !this.i.k) {
            return true;
        }
        GeoPoint b2 = this.i.b((int) motionEvent.getX(), (int) motionEvent.getY());
        if (b2 != null) {
            if (this.i.h != null) {
                for (n next : this.i.h) {
                    if (next != null) {
                        next.b(b2);
                    }
                }
            }
            if (this.i.f) {
                ad E = this.i.E();
                E.a += 1.0f;
                if (!this.i.g) {
                    E.d = b2.getLongitudeE6();
                    E.e = b2.getLatitudeE6();
                }
                BaiduMap.mapStatusReason |= 1;
                this.i.a(E, 300);
                e.m = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        e eVar = this.i;
        if (eVar == null || eVar.i == null || !this.i.k) {
            return true;
        }
        if (!this.i.e) {
            return false;
        }
        float sqrt = (float) Math.sqrt((double) ((f2 * f2) + (f3 * f3)));
        if (sqrt <= 500.0f) {
            return false;
        }
        BaiduMap.mapStatusReason |= 1;
        this.i.A();
        this.i.a(34, (int) (sqrt * 0.6f), ((int) motionEvent2.getX()) | (((int) motionEvent2.getY()) << 16));
        this.i.M();
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {
        e eVar = this.i;
        if (eVar != null && eVar.i != null && this.i.k) {
            String a2 = this.i.i.a(-1, (int) motionEvent.getX(), (int) motionEvent.getY(), this.i.l);
            if (this.i.h != null) {
                if (a2 == null || a2.equals("")) {
                    for (n next : this.i.h) {
                        GeoPoint b2 = this.i.b((int) motionEvent.getX(), (int) motionEvent.getY());
                        if (next != null) {
                            next.c(b2);
                        }
                    }
                    return;
                }
                for (n next2 : this.i.h) {
                    if (next2.b(a2)) {
                        this.i.p = true;
                    } else {
                        next2.c(this.i.b((int) motionEvent.getX(), (int) motionEvent.getY()));
                    }
                }
            }
        }
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x006a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onSingleTapConfirmed(android.view.MotionEvent r7) {
        /*
            r6 = this;
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            r1 = 1
            if (r0 == 0) goto L_0x00a6
            com.baidu.mapsdkplatform.comjni.map.basemap.a r0 = r0.i
            if (r0 == 0) goto L_0x00a6
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            boolean r0 = r0.k
            if (r0 != 0) goto L_0x0011
            goto L_0x00a6
        L_0x0011:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            if (r0 != 0) goto L_0x0018
            return r1
        L_0x0018:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            com.baidu.mapsdkplatform.comjni.map.basemap.a r0 = r0.i
            r2 = -1
            float r3 = r7.getX()
            int r3 = (int) r3
            float r4 = r7.getY()
            int r4 = (int) r4
            com.baidu.mapsdkplatform.comapi.map.e r5 = r6.i
            int r5 = r5.l
            java.lang.String r0 = r0.a((int) r2, (int) r3, (int) r4, (int) r5)
            r2 = 0
            if (r0 == 0) goto L_0x007c
            java.lang.String r3 = ""
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x007c
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0057 }
            r3.<init>(r0)     // Catch:{ JSONException -> 0x0057 }
            java.lang.String r0 = "px"
            float r2 = r7.getX()     // Catch:{ JSONException -> 0x0054 }
            int r2 = (int) r2     // Catch:{ JSONException -> 0x0054 }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x0054 }
            java.lang.String r0 = "py"
            float r7 = r7.getY()     // Catch:{ JSONException -> 0x0054 }
            int r7 = (int) r7     // Catch:{ JSONException -> 0x0054 }
            r3.put(r0, r7)     // Catch:{ JSONException -> 0x0054 }
            goto L_0x005c
        L_0x0054:
            r7 = move-exception
            r2 = r3
            goto L_0x0058
        L_0x0057:
            r7 = move-exception
        L_0x0058:
            r7.printStackTrace()
            r3 = r2
        L_0x005c:
            com.baidu.mapsdkplatform.comapi.map.e r7 = r6.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r7 = r7.h
            java.util.Iterator r7 = r7.iterator()
        L_0x0064:
            boolean r0 = r7.hasNext()
            if (r0 == 0) goto L_0x00a6
            java.lang.Object r0 = r7.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r3 == 0) goto L_0x0064
            if (r0 == 0) goto L_0x0064
            java.lang.String r2 = r3.toString()
            r0.a((java.lang.String) r2)
            goto L_0x0064
        L_0x007c:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            java.util.Iterator r0 = r0.iterator()
        L_0x0084:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x00a6
            java.lang.Object r2 = r0.next()
            com.baidu.mapsdkplatform.comapi.map.n r2 = (com.baidu.mapsdkplatform.comapi.map.n) r2
            if (r2 == 0) goto L_0x0084
            com.baidu.mapsdkplatform.comapi.map.e r3 = r6.i
            float r4 = r7.getX()
            int r4 = (int) r4
            float r5 = r7.getY()
            int r5 = (int) r5
            com.baidu.mapapi.model.inner.GeoPoint r3 = r3.b((int) r4, (int) r5)
            r2.a((com.baidu.mapapi.model.inner.GeoPoint) r3)
            goto L_0x0084
        L_0x00a6:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.ae.onSingleTapConfirmed(android.view.MotionEvent):boolean");
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i2, int i3) {
        if (this.i != null) {
            SurfaceTexture surfaceTexture2 = this.g;
            if (surfaceTexture2 == null) {
                this.g = surfaceTexture;
                o oVar = new o(this.g, this, new AtomicBoolean(true), this);
                this.h = oVar;
                oVar.start();
                a = i2;
                b = i3;
                ad E = this.i.E();
                if (E != null) {
                    if (E.f == 0 || E.f == -1 || E.f == (E.j.left - E.j.right) / 2) {
                        E.f = -1;
                    }
                    if (E.g == 0 || E.g == -1 || E.g == (E.j.bottom - E.j.top) / 2) {
                        E.g = -1;
                    }
                    E.j.left = 0;
                    E.j.top = 0;
                    E.j.bottom = i3;
                    E.j.right = i2;
                    this.i.a(E);
                    this.i.a(a, b);
                    return;
                }
                return;
            }
            setSurfaceTexture(surfaceTexture2);
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i2, int i3) {
        e eVar = this.i;
        if (eVar != null) {
            a = i2;
            b = i3;
            c = 1;
            ad E = eVar.E();
            if (E.f == 0 || E.f == -1 || E.f == (E.j.left - E.j.right) / 2) {
                E.f = -1;
            }
            if (E.g == 0 || E.g == -1 || E.g == (E.j.bottom - E.j.top) / 2) {
                E.g = -1;
            }
            E.j.left = 0;
            E.j.top = 0;
            E.j.bottom = i3;
            E.j.right = i2;
            this.i.a(E);
            this.i.a(a, b);
            MapRenderer.nativeResize(this.i.j, i2, i3);
        }
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        o oVar;
        if (this.f && (oVar = this.h) != null) {
            oVar.a();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        e eVar = this.i;
        if (eVar == null || eVar.i == null) {
            return true;
        }
        super.onTouchEvent(motionEvent);
        if (this.i.h != null) {
            for (n next : this.i.h) {
                if (next != null) {
                    next.a(motionEvent);
                }
            }
        }
        if (this.d.onTouchEvent(motionEvent)) {
            return true;
        }
        return this.i.a(motionEvent);
    }
}
