package com.baidu.mapsdkplatform.comapi.map;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.baidu.mapapi.common.EnvironmentUtilities;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.MapRenderer;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class l extends GLSurfaceView implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, MapRenderer.a {
    private static final String a = l.class.getSimpleName();
    private Handler b;
    private MapRenderer c;
    /* access modifiers changed from: private */
    public int d;
    /* access modifiers changed from: private */
    public int e;
    private GestureDetector f;
    /* access modifiers changed from: private */
    public e g;

    static class a {
        float a;
        float b;
        float c;
        float d;
        boolean e;
        float f;
        float g;
        double h;

        a() {
        }

        public String toString() {
            return "MultiTouch{x1=" + this.a + ", x2=" + this.b + ", y1=" + this.c + ", y2=" + this.d + ", mTwoTouch=" + this.e + ", centerX=" + this.f + ", centerY=" + this.g + ", length=" + this.h + '}';
        }
    }

    public l(Context context, ab abVar, String str, int i) {
        super(context);
        if (context != null) {
            setEGLContextClientVersion(2);
            this.f = new GestureDetector(context, this);
            EnvironmentUtilities.initAppDirectory(context);
            if (this.g == null) {
                this.g = new e(context, str, i);
            }
            this.g.a(context.hashCode());
            g();
            this.g.a();
            this.g.a(abVar);
            h();
            this.g.a(this.b);
            this.g.f();
            setBackgroundColor(0);
            return;
        }
        throw new RuntimeException("BDMapSDKException: when you create an mapview, the context can not be null");
    }

    private static boolean a(int i, int i2, int i3, int i4, int i5, int i6) {
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl10.eglInitialize(eglGetDisplay, new int[2]);
        int[] iArr = new int[1];
        return egl10.eglChooseConfig(eglGetDisplay, new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344}, new EGLConfig[100], 100, iArr) && iArr[0] > 0;
    }

    private void g() {
        try {
            if (a(8, 8, 8, 8, 24, 0)) {
                setEGLConfigChooser(8, 8, 8, 8, 24, 0);
            } else if (a(5, 6, 5, 0, 24, 0)) {
                setEGLConfigChooser(5, 6, 5, 0, 24, 0);
            } else {
                setEGLConfigChooser(true);
            }
        } catch (IllegalArgumentException e2) {
            setEGLConfigChooser(true);
        }
        MapRenderer mapRenderer = new MapRenderer(this, this);
        this.c = mapRenderer;
        mapRenderer.a(this.g.j);
        setRenderer(this.c);
        setRenderMode(1);
    }

    private void h() {
        this.b = new m(this);
    }

    public e a() {
        return this.g;
    }

    public void a(float f2, float f3) {
        e eVar = this.g;
        if (eVar != null && eVar.i != null) {
            this.g.b(f2, f3);
        }
    }

    public void a(int i) {
        int i2;
        if (this.g != null) {
            Message message = new Message();
            message.what = 50;
            message.obj = Long.valueOf(this.g.j);
            boolean q = this.g.q();
            if (i == 3) {
                i2 = 0;
            } else {
                if (q) {
                    i2 = 1;
                }
                this.b.sendMessage(message);
            }
            message.arg1 = i2;
            this.b.sendMessage(message);
        }
    }

    public void a(String str, Rect rect) {
        e eVar = this.g;
        if (eVar != null && eVar.i != null) {
            if (rect != null) {
                int i = rect.left;
                int i2 = this.e < rect.bottom ? 0 : this.e - rect.bottom;
                int width = rect.width();
                int height = rect.height();
                if (i >= 0 && i2 >= 0 && width > 0 && height > 0) {
                    if (width > this.d) {
                        width = Math.abs(rect.width()) - (rect.right - this.d);
                    }
                    if (height > this.e) {
                        height = Math.abs(rect.height()) - (rect.bottom - this.e);
                    }
                    if (i > SysOSUtil.getScreenSizeX() || i2 > SysOSUtil.getScreenSizeY()) {
                        this.g.i.a(str, (Bundle) null);
                        requestRender();
                        return;
                    }
                    this.d = width;
                    this.e = height;
                    Bundle bundle = new Bundle();
                    bundle.putInt("x", i);
                    bundle.putInt("y", i2);
                    bundle.putInt("width", width);
                    bundle.putInt("height", height);
                    this.g.i.a(str, bundle);
                } else {
                    return;
                }
            } else {
                this.g.i.a(str, (Bundle) null);
            }
            requestRender();
        }
    }

    public boolean a(float f2, float f3, float f4, float f5) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null) {
            return false;
        }
        return this.g.a(f2, f3, f4, f5);
    }

    public void b() {
        e eVar = this.g;
        if (eVar != null) {
            eVar.u();
        }
    }

    public void b(int i) {
        e eVar = this.g;
        if (eVar != null) {
            if (eVar.h != null) {
                for (n next : this.g.h) {
                    if (next != null) {
                        next.f();
                    }
                }
            }
            this.g.b(this.b);
            this.g.c(i);
            this.g = null;
        }
        Handler handler = this.b;
        if (handler != null) {
            handler.removeCallbacksAndMessages((Object) null);
        }
    }

    public boolean b(float f2, float f3) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null) {
            return false;
        }
        return this.g.d(f2, f3);
    }

    public void c() {
        e eVar = this.g;
        if (eVar != null) {
            eVar.v();
        }
    }

    public boolean c(float f2, float f3) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null) {
            return false;
        }
        return this.g.c(f2, f3);
    }

    public void d() {
        getHolder().setFormat(-3);
        this.g.i.s();
    }

    public boolean d(float f2, float f3) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null) {
            return false;
        }
        return this.g.c((int) f2, (int) f3);
    }

    public void e() {
        getHolder().setFormat(-1);
        this.g.i.t();
    }

    public void f() {
    }

    public boolean onDoubleTap(MotionEvent motionEvent) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null || !this.g.k) {
            return true;
        }
        GeoPoint b2 = this.g.b((int) motionEvent.getX(), (int) motionEvent.getY());
        if (b2 != null) {
            if (this.g.h != null) {
                for (n next : this.g.h) {
                    if (next != null) {
                        next.b(b2);
                    }
                }
            }
            if (this.g.f) {
                ad E = this.g.E();
                E.a += 1.0f;
                if (!this.g.g) {
                    E.d = b2.getLongitudeE6();
                    E.e = b2.getLatitudeE6();
                }
                BaiduMap.mapStatusReason |= 1;
                this.g.a(E, 300);
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
        e eVar = this.g;
        if (eVar == null || eVar.i == null || !this.g.k) {
            return true;
        }
        if (!this.g.e) {
            return false;
        }
        float sqrt = (float) Math.sqrt((double) ((f2 * f2) + (f3 * f3)));
        if (sqrt <= 500.0f) {
            return false;
        }
        BaiduMap.mapStatusReason |= 1;
        this.g.A();
        this.g.a(34, (int) (sqrt * 0.6f), ((int) motionEvent2.getX()) | (((int) motionEvent2.getY()) << 16));
        this.g.M();
        return true;
    }

    public void onLongPress(MotionEvent motionEvent) {
        e eVar = this.g;
        if (eVar != null && eVar.i != null && this.g.k) {
            String a2 = this.g.i.a(-1, (int) motionEvent.getX(), (int) motionEvent.getY(), this.g.l);
            if (a2 == null || a2.equals("")) {
                if (this.g.h != null) {
                    for (n next : this.g.h) {
                        GeoPoint b2 = this.g.b((int) motionEvent.getX(), (int) motionEvent.getY());
                        if (next != null) {
                            next.c(b2);
                        }
                    }
                }
            } else if (this.g.h != null) {
                for (n next2 : this.g.h) {
                    if (next2 != null) {
                        if (next2.b(a2)) {
                            this.g.p = true;
                        } else {
                            next2.c(this.g.b((int) motionEvent.getX(), (int) motionEvent.getY()));
                        }
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        e eVar = this.g;
        if (eVar != null && eVar.i != null) {
            this.g.i.c();
        }
    }

    public void onResume() {
        super.onResume();
        e eVar = this.g;
        if (eVar != null && eVar.i != null) {
            if (this.g.h != null) {
                for (n next : this.g.h) {
                    if (next != null) {
                        next.d();
                    }
                }
            }
            this.g.i.g();
            this.g.i.d();
            this.g.i.n();
            setRenderMode(1);
        }
    }

    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
        return false;
    }

    public void onShowPress(MotionEvent motionEvent) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onSingleTapConfirmed(android.view.MotionEvent r7) {
        /*
            r6 = this;
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.g
            r1 = 1
            if (r0 == 0) goto L_0x00ac
            com.baidu.mapsdkplatform.comjni.map.basemap.a r0 = r0.i
            if (r0 == 0) goto L_0x00ac
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.g
            boolean r0 = r0.k
            if (r0 != 0) goto L_0x0011
            goto L_0x00ac
        L_0x0011:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.g
            com.baidu.mapsdkplatform.comjni.map.basemap.a r0 = r0.i
            r2 = -1
            float r3 = r7.getX()
            int r3 = (int) r3
            float r4 = r7.getY()
            int r4 = (int) r4
            com.baidu.mapsdkplatform.comapi.map.e r5 = r6.g
            int r5 = r5.l
            java.lang.String r0 = r0.a((int) r2, (int) r3, (int) r4, (int) r5)
            r2 = 0
            if (r0 == 0) goto L_0x007b
            java.lang.String r3 = ""
            boolean r3 = r0.equals(r3)
            if (r3 != 0) goto L_0x007b
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0050 }
            r3.<init>(r0)     // Catch:{ JSONException -> 0x0050 }
            java.lang.String r0 = "px"
            float r2 = r7.getX()     // Catch:{ JSONException -> 0x004d }
            int r2 = (int) r2     // Catch:{ JSONException -> 0x004d }
            r3.put(r0, r2)     // Catch:{ JSONException -> 0x004d }
            java.lang.String r0 = "py"
            float r7 = r7.getY()     // Catch:{ JSONException -> 0x004d }
            int r7 = (int) r7     // Catch:{ JSONException -> 0x004d }
            r3.put(r0, r7)     // Catch:{ JSONException -> 0x004d }
            goto L_0x0055
        L_0x004d:
            r7 = move-exception
            r2 = r3
            goto L_0x0051
        L_0x0050:
            r7 = move-exception
        L_0x0051:
            r7.printStackTrace()
            r3 = r2
        L_0x0055:
            com.baidu.mapsdkplatform.comapi.map.e r7 = r6.g
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r7 = r7.h
            if (r7 == 0) goto L_0x00ac
            com.baidu.mapsdkplatform.comapi.map.e r7 = r6.g
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r7 = r7.h
            java.util.Iterator r7 = r7.iterator()
        L_0x0063:
            boolean r0 = r7.hasNext()
            if (r0 == 0) goto L_0x00ac
            java.lang.Object r0 = r7.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r3 == 0) goto L_0x0063
            if (r0 == 0) goto L_0x0063
            java.lang.String r2 = r3.toString()
            r0.a((java.lang.String) r2)
            goto L_0x0063
        L_0x007b:
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.g
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            if (r0 == 0) goto L_0x00ac
            com.baidu.mapsdkplatform.comapi.map.e r0 = r6.g
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            java.util.Iterator r0 = r0.iterator()
        L_0x0089:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x00ac
            java.lang.Object r2 = r0.next()
            com.baidu.mapsdkplatform.comapi.map.n r2 = (com.baidu.mapsdkplatform.comapi.map.n) r2
            if (r2 != 0) goto L_0x0098
            goto L_0x0089
        L_0x0098:
            com.baidu.mapsdkplatform.comapi.map.e r3 = r6.g
            float r4 = r7.getX()
            int r4 = (int) r4
            float r5 = r7.getY()
            int r5 = (int) r5
            com.baidu.mapapi.model.inner.GeoPoint r3 = r3.b((int) r4, (int) r5)
            r2.a((com.baidu.mapapi.model.inner.GeoPoint) r3)
            goto L_0x0089
        L_0x00ac:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.l.onSingleTapConfirmed(android.view.MotionEvent):boolean");
    }

    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        e eVar = this.g;
        if (eVar == null || eVar.i == null) {
            return true;
        }
        super.onTouchEvent(motionEvent);
        if (this.g.h != null) {
            for (n next : this.g.h) {
                if (next != null) {
                    next.a(motionEvent);
                }
            }
        }
        if (this.f.onTouchEvent(motionEvent)) {
            return true;
        }
        return this.g.a(motionEvent);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        super.surfaceChanged(surfaceHolder, i, i2, i3);
        e eVar = this.g;
        if (eVar != null && eVar.i != null) {
            this.c.a = i2;
            this.c.b = i3;
            this.d = i2;
            this.e = i3;
            this.c.c = 0;
            ad E = this.g.E();
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
            this.g.a(E);
            this.g.a(this.d, this.e);
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
        if (surfaceHolder != null && !surfaceHolder.getSurface().isValid()) {
            surfaceDestroyed(surfaceHolder);
        }
    }
}
