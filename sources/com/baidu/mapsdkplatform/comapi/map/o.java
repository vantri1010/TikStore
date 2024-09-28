package com.baidu.mapsdkplatform.comapi.map;

import android.graphics.SurfaceTexture;
import android.opengl.GLUtils;
import java.lang.Thread;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class o extends Thread {
    private AtomicBoolean a;
    private SurfaceTexture b;
    private a c;
    private EGL10 d;
    private EGLDisplay e = EGL10.EGL_NO_DISPLAY;
    private EGLContext f = EGL10.EGL_NO_CONTEXT;
    private EGLSurface g = EGL10.EGL_NO_SURFACE;
    private GL10 h;
    private int i = 1;
    private boolean j = false;
    private final ae k;

    public interface a {
        int a();
    }

    public o(SurfaceTexture surfaceTexture, a aVar, AtomicBoolean atomicBoolean, ae aeVar) {
        this.b = surfaceTexture;
        this.c = aVar;
        this.a = atomicBoolean;
        this.k = aeVar;
    }

    private boolean a(int i2, int i3, int i4, int i5, int i6, int i7) {
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        this.d = egl10;
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        this.e = eglGetDisplay;
        if (eglGetDisplay != EGL10.EGL_NO_DISPLAY) {
            if (this.d.eglInitialize(this.e, new int[2])) {
                EGLConfig[] eGLConfigArr = new EGLConfig[100];
                int[] iArr = new int[1];
                if (!this.d.eglChooseConfig(this.e, new int[]{12352, 4, 12324, i2, 12323, i3, 12322, i4, 12321, i5, 12325, i6, 12326, i7, 12344}, eGLConfigArr, 100, iArr) || iArr[0] <= 0) {
                    return false;
                }
                this.f = this.d.eglCreateContext(this.e, eGLConfigArr[0], EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                EGLSurface eglCreateWindowSurface = this.d.eglCreateWindowSurface(this.e, eGLConfigArr[0], this.b, (int[]) null);
                this.g = eglCreateWindowSurface;
                if (eglCreateWindowSurface == EGL10.EGL_NO_SURFACE || this.f == EGL10.EGL_NO_CONTEXT) {
                    if (this.d.eglGetError() != 12299) {
                        GLUtils.getEGLErrorString(this.d.eglGetError());
                    } else {
                        throw new RuntimeException("eglCreateWindowSurface returned EGL_BAD_NATIVE_WINDOW. ");
                    }
                }
                EGL10 egl102 = this.d;
                EGLDisplay eGLDisplay = this.e;
                EGLSurface eGLSurface = this.g;
                if (egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.f)) {
                    this.h = (GL10) this.f.getGL();
                    return true;
                }
                String eGLErrorString = GLUtils.getEGLErrorString(this.d.eglGetError());
                throw new RuntimeException("eglMakeCurrent failed : " + eGLErrorString);
            }
            throw new RuntimeException("eglInitialize failed : " + GLUtils.getEGLErrorString(this.d.eglGetError()));
        }
        throw new RuntimeException("eglGetdisplay failed : " + GLUtils.getEGLErrorString(this.d.eglGetError()));
    }

    private static boolean b(int i2, int i3, int i4, int i5, int i6, int i7) {
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl10.eglInitialize(eglGetDisplay, new int[2]);
        int[] iArr = new int[1];
        return egl10.eglChooseConfig(eglGetDisplay, new int[]{12324, i2, 12323, i3, 12322, i4, 12321, i5, 12325, i6, 12326, i7, 12344}, new EGLConfig[100], 100, iArr) && iArr[0] > 0;
    }

    private void d() {
        try {
            boolean b2 = b(5, 6, 5, 0, 24, 0);
            a(8, 8, 8, 0, 24, 0);
        } catch (IllegalArgumentException e2) {
            a(8, 8, 8, 0, 24, 0);
        }
        if (this.k.b() != null) {
            MapRenderer.nativeInit(this.k.b().j);
            MapRenderer.nativeResize(this.k.b().j, ae.a, ae.b);
        }
    }

    private void e() {
        if (this.g != EGL10.EGL_NO_SURFACE) {
            this.d.eglMakeCurrent(this.e, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            this.d.eglDestroySurface(this.e, this.g);
            this.g = EGL10.EGL_NO_SURFACE;
        }
        if (this.f != EGL10.EGL_NO_CONTEXT) {
            this.d.eglDestroyContext(this.e, this.f);
            this.f = EGL10.EGL_NO_CONTEXT;
        }
        if (this.e != EGL10.EGL_NO_DISPLAY) {
            this.d.eglTerminate(this.e);
            this.e = EGL10.EGL_NO_DISPLAY;
        }
    }

    public void a() {
        this.i = 1;
        this.j = false;
        synchronized (this) {
            if (getState() == Thread.State.WAITING) {
                notify();
            }
        }
    }

    public void b() {
        this.i = 0;
        synchronized (this) {
            this.j = true;
        }
    }

    public void c() {
        this.j = true;
        synchronized (this) {
            if (getState() == Thread.State.WAITING) {
                notify();
            }
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processLoop(RegionMaker.java:225)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public void run() {
        /*
            r6 = this;
            r6.d()
        L_0x0003:
            com.baidu.mapsdkplatform.comapi.map.o$a r0 = r6.c
            if (r0 == 0) goto L_0x0083
            int r0 = r6.i
            r1 = 1
            if (r0 != r1) goto L_0x0072
            boolean r0 = r6.j
            if (r0 != 0) goto L_0x0072
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r6.k
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.b()
            if (r0 != 0) goto L_0x0019
            goto L_0x0083
        L_0x0019:
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r6.k
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.b()
            monitor-enter(r0)
            monitor-enter(r6)     // Catch:{ all -> 0x006f }
            boolean r1 = r6.j     // Catch:{ all -> 0x006c }
            if (r1 != 0) goto L_0x002d
            com.baidu.mapsdkplatform.comapi.map.o$a r1 = r6.c     // Catch:{ all -> 0x006c }
            int r1 = r1.a()     // Catch:{ all -> 0x006c }
            r6.i = r1     // Catch:{ all -> 0x006c }
        L_0x002d:
            monitor-exit(r6)     // Catch:{ all -> 0x006c }
            com.baidu.mapsdkplatform.comapi.map.ae r1 = r6.k     // Catch:{ all -> 0x006f }
            com.baidu.mapsdkplatform.comapi.map.e r1 = r1.b()     // Catch:{ all -> 0x006f }
            if (r1 == 0) goto L_0x0061
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r2 = r1.h     // Catch:{ all -> 0x006f }
            if (r2 == 0) goto L_0x0061
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r2 = r1.h     // Catch:{ all -> 0x006f }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x006f }
        L_0x0040:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x006f }
            if (r3 == 0) goto L_0x0061
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x006f }
            com.baidu.mapsdkplatform.comapi.map.n r3 = (com.baidu.mapsdkplatform.comapi.map.n) r3     // Catch:{ all -> 0x006f }
            if (r3 != 0) goto L_0x004f
            goto L_0x0040
        L_0x004f:
            com.baidu.mapsdkplatform.comapi.map.ad r4 = r1.J()     // Catch:{ all -> 0x006f }
            javax.microedition.khronos.opengles.GL10 r5 = r6.h     // Catch:{ all -> 0x006f }
            if (r5 != 0) goto L_0x0059
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            return
        L_0x0059:
            if (r3 == 0) goto L_0x0040
            javax.microedition.khronos.opengles.GL10 r5 = r6.h     // Catch:{ all -> 0x006f }
            r3.a((javax.microedition.khronos.opengles.GL10) r5, (com.baidu.mapsdkplatform.comapi.map.ad) r4)     // Catch:{ all -> 0x006f }
            goto L_0x0040
        L_0x0061:
            javax.microedition.khronos.egl.EGL10 r1 = r6.d     // Catch:{ all -> 0x006f }
            javax.microedition.khronos.egl.EGLDisplay r2 = r6.e     // Catch:{ all -> 0x006f }
            javax.microedition.khronos.egl.EGLSurface r3 = r6.g     // Catch:{ all -> 0x006f }
            r1.eglSwapBuffers(r2, r3)     // Catch:{ all -> 0x006f }
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            goto L_0x007f
        L_0x006c:
            r1 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x006c }
            throw r1     // Catch:{ all -> 0x006f }
        L_0x006f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006f }
            throw r1
        L_0x0072:
            monitor-enter(r6)     // Catch:{ InterruptedException -> 0x007b }
            r6.wait()     // Catch:{ all -> 0x0078 }
            monitor-exit(r6)     // Catch:{ all -> 0x0078 }
            goto L_0x007f
        L_0x0078:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0078 }
            throw r0     // Catch:{ InterruptedException -> 0x007b }
        L_0x007b:
            r0 = move-exception
            r0.printStackTrace()
        L_0x007f:
            boolean r0 = r6.j
            if (r0 == 0) goto L_0x0003
        L_0x0083:
            r6.e()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.o.run():void");
    }
}
