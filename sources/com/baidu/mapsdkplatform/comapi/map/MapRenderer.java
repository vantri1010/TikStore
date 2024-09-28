package com.baidu.mapsdkplatform.comapi.map;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MapRenderer implements GLSurfaceView.Renderer {
    private static final String d = MapRenderer.class.getSimpleName();
    public int a;
    public int b;
    public int c;
    private long e;
    private a f;
    private final l g;

    public interface a {
        void f();
    }

    public MapRenderer(l lVar, a aVar) {
        this.f = aVar;
        this.g = lVar;
    }

    private void a(GL10 gl10) {
        GLES20.glClear(16640);
        GLES20.glClearColor(0.85f, 0.8f, 0.8f, 0.0f);
    }

    private boolean a() {
        return this.e != 0;
    }

    public static native void nativeInit(long j);

    public static native int nativeRender(long j);

    public static native void nativeResize(long j, int i, int i2);

    public void a(long j) {
        this.e = j;
    }

    public void onDrawFrame(GL10 gl10) {
        if (!a()) {
            a(gl10);
            return;
        }
        if (this.c <= 1) {
            nativeResize(this.e, this.a, this.b);
            this.c++;
        }
        this.f.f();
        int nativeRender = nativeRender(this.e);
        if (this.g.a() != null) {
            if (this.g.a().h != null) {
                for (n next : this.g.a().h) {
                    if (this.g.a() != null) {
                        ad J = this.g.a().J();
                        if (next != null) {
                            next.a(gl10, J);
                        }
                    } else {
                        return;
                    }
                }
            }
            l lVar = this.g;
            if (nativeRender == 1) {
                lVar.requestRender();
            } else if (!lVar.a().b()) {
                if (lVar.getRenderMode() != 0) {
                    lVar.setRenderMode(0);
                }
            } else if (lVar.getRenderMode() != 1) {
                lVar.setRenderMode(1);
            }
        }
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2) {
        long j = this.e;
        if (j != 0) {
            nativeResize(j, i, i2);
        }
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
        nativeInit(this.e);
        if (a()) {
            this.f.f();
        }
    }
}
