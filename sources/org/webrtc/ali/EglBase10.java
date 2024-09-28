package org.webrtc.ali;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.webrtc.ali.EglBase;

class EglBase10 extends EglBase {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private final EGL10 egl = ((EGL10) EGLContext.getEGL());
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;

    public static class Context extends EglBase.Context {
        /* access modifiers changed from: private */
        public final EGLContext eglContext;

        public Context(EGLContext eglContext2) {
            this.eglContext = eglContext2;
        }
    }

    public EglBase10(Context sharedContext, int[] configAttributes) {
        EGLDisplay eglDisplay2 = getEglDisplay();
        this.eglDisplay = eglDisplay2;
        EGLConfig eglConfig2 = getEglConfig(eglDisplay2, configAttributes);
        this.eglConfig = eglConfig2;
        this.eglContext = createEglContext(sharedContext, this.eglDisplay, eglConfig2);
    }

    public void createSurface(Surface surface) {
        createSurfaceInternal(new SurfaceHolder(surface) {
            private final Surface surface;

            {
                this.surface = surface;
            }

            public void addCallback(SurfaceHolder.Callback callback) {
            }

            public void removeCallback(SurfaceHolder.Callback callback) {
            }

            public boolean isCreating() {
                return false;
            }

            @Deprecated
            public void setType(int i) {
            }

            public void setFixedSize(int i, int i2) {
            }

            public void setSizeFromLayout() {
            }

            public void setFormat(int i) {
            }

            public void setKeepScreenOn(boolean b) {
            }

            public Canvas lockCanvas() {
                return null;
            }

            public Canvas lockCanvas(Rect rect) {
                return null;
            }

            public void unlockCanvasAndPost(Canvas canvas) {
            }

            public Rect getSurfaceFrame() {
                return null;
            }

            public Surface getSurface() {
                return this.surface;
            }
        });
    }

    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture);
    }

    private void createSurfaceInternal(Object nativeWindow) {
        if ((nativeWindow instanceof SurfaceHolder) || (nativeWindow instanceof SurfaceTexture)) {
            checkIsNotReleased();
            if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
                EGLSurface eglCreateWindowSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, nativeWindow, new int[]{12344});
                this.eglSurface = eglCreateWindowSurface;
                if (eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                    throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(this.egl.eglGetError()));
                }
                return;
            }
            throw new RuntimeException("Already has an EGLSurface");
        }
        throw new IllegalStateException("Input must be either a SurfaceHolder or SurfaceTexture");
    }

    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (this.eglSurface == EGL10.EGL_NO_SURFACE) {
            EGLSurface eglCreatePbufferSurface = this.egl.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[]{12375, width, 12374, height, 12344});
            this.eglSurface = eglCreatePbufferSurface;
            if (eglCreatePbufferSurface == EGL10.EGL_NO_SURFACE) {
                throw new RuntimeException("Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
            return;
        }
        throw new RuntimeException("Already has an EGLSurface");
    }

    public EglBase.Context getEglBaseContext() {
        return new Context(this.eglContext);
    }

    public boolean hasSurface() {
        return this.eglSurface != EGL10.EGL_NO_SURFACE;
    }

    public int surfaceWidth() {
        int[] widthArray = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, widthArray);
        return widthArray[0];
    }

    public int surfaceHeight() {
        int[] heightArray = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, heightArray);
        return heightArray[0];
    }

    public void releaseSurface() {
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    private void checkIsNotReleased() {
        if (this.eglDisplay == EGL10.EGL_NO_DISPLAY || this.eglContext == EGL10.EGL_NO_CONTEXT || this.eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    public void release() {
        checkIsNotReleased();
        releaseSurface();
        detachCurrent();
        this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
        this.egl.eglTerminate(this.eglDisplay);
        this.eglContext = EGL10.EGL_NO_CONTEXT;
        this.eglDisplay = EGL10.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    public void makeCurrent() {
        checkIsNotReleased();
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
                }
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't make current");
    }

    public void detachCurrent() {
        synchronized (EglBase.lock) {
            if (!this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
                throw new RuntimeException("eglDetachCurrent failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
            }
        }
    }

    public void swapBuffers() {
        checkIsNotReleased();
        if (this.eglSurface != EGL10.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't swap buffers");
    }

    private EGLDisplay getEglDisplay() {
        EGLDisplay eglDisplay2 = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay2 != EGL10.EGL_NO_DISPLAY) {
            if (this.egl.eglInitialize(eglDisplay2, new int[2])) {
                return eglDisplay2;
            }
            throw new RuntimeException("Unable to initialize EGL10: 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
        throw new RuntimeException("Unable to get EGL10 display: 0x" + Integer.toHexString(this.egl.eglGetError()));
    }

    private EGLConfig getEglConfig(EGLDisplay eglDisplay2, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!this.egl.eglChooseConfig(eglDisplay2, configAttributes, configs, configs.length, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed: 0x" + Integer.toHexString(this.egl.eglGetError()));
        } else if (numConfigs[0] > 0) {
            EGLConfig eglConfig2 = configs[0];
            if (eglConfig2 != null) {
                return eglConfig2;
            }
            throw new RuntimeException("eglChooseConfig returned null");
        } else {
            throw new RuntimeException("Unable to find any matching EGL config");
        }
    }

    private EGLContext createEglContext(Context sharedContext, EGLDisplay eglDisplay2, EGLConfig eglConfig2) {
        EGLContext rootContext;
        EGLContext eglContext2;
        if (sharedContext == null || sharedContext.eglContext != EGL10.EGL_NO_CONTEXT) {
            int[] contextAttributes = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
            if (sharedContext == null) {
                rootContext = EGL10.EGL_NO_CONTEXT;
            } else {
                rootContext = sharedContext.eglContext;
            }
            synchronized (EglBase.lock) {
                eglContext2 = this.egl.eglCreateContext(eglDisplay2, eglConfig2, rootContext, contextAttributes);
            }
            if (eglContext2 != EGL10.EGL_NO_CONTEXT) {
                return eglContext2;
            }
            throw new RuntimeException("Failed to create EGL context: 0x" + Integer.toHexString(this.egl.eglGetError()));
        }
        throw new RuntimeException("Invalid sharedContext");
    }
}
