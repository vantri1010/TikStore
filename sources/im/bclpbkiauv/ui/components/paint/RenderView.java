package im.bclpbkiauv.ui.components.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.components.Size;
import im.bclpbkiauv.ui.components.paint.Painting;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class RenderView extends TextureView {
    /* access modifiers changed from: private */
    public Bitmap bitmap;
    private Brush brush;
    private int color;
    private RenderViewDelegate delegate;
    private Input input = new Input(this);
    /* access modifiers changed from: private */
    public CanvasInternal internal;
    /* access modifiers changed from: private */
    public int orientation;
    /* access modifiers changed from: private */
    public Painting painting;
    /* access modifiers changed from: private */
    public DispatchQueue queue;
    /* access modifiers changed from: private */
    public boolean shuttingDown;
    /* access modifiers changed from: private */
    public boolean transformedBitmap;
    /* access modifiers changed from: private */
    public UndoStore undoStore;
    private float weight;

    public interface RenderViewDelegate {
        void onBeganDrawing();

        void onFinishedDrawing(boolean z);

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting paint, Bitmap b, int rotation) {
        super(context);
        this.bitmap = b;
        this.orientation = rotation;
        this.painting = paint;
        paint.setRenderView(this);
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (surface != null && RenderView.this.internal == null) {
                    CanvasInternal unused = RenderView.this.internal = new CanvasInternal(surface);
                    RenderView.this.internal.setBufferSize(width, height);
                    RenderView.this.updateTransform();
                    RenderView.this.internal.requestRender();
                    if (RenderView.this.painting.isPaused()) {
                        RenderView.this.painting.onResume();
                    }
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.setBufferSize(width, height);
                    RenderView.this.updateTransform();
                    RenderView.this.internal.requestRender();
                    RenderView.this.internal.postRunnable(new Runnable() {
                        public void run() {
                            if (RenderView.this.internal != null) {
                                RenderView.this.internal.requestRender();
                            }
                        }
                    });
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (RenderView.this.internal != null && !RenderView.this.shuttingDown) {
                    RenderView.this.painting.onPause(new Runnable() {
                        public void run() {
                            RenderView.this.internal.shutdown();
                            CanvasInternal unused = RenderView.this.internal = null;
                        }
                    });
                }
                return true;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        this.painting.setDelegate(new Painting.PaintingDelegate() {
            public void contentChanged(RectF rect) {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.scheduleRedraw();
                }
            }

            public void strokeCommited() {
            }

            public UndoStore requestUndoStore() {
                return RenderView.this.undoStore;
            }

            public DispatchQueue requestDispatchQueue() {
                return RenderView.this.queue;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return false;
        }
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null || !canvasInternal.initialized || !this.internal.ready) {
            return true;
        }
        this.input.process(event);
        return true;
    }

    public void setUndoStore(UndoStore store) {
        this.undoStore = store;
    }

    public void setQueue(DispatchQueue dispatchQueue) {
        this.queue = dispatchQueue;
    }

    public void setDelegate(RenderViewDelegate renderViewDelegate) {
        this.delegate = renderViewDelegate;
    }

    public Painting getPainting() {
        return this.painting;
    }

    private float brushWeightForSize(float size) {
        float paintingWidth = this.painting.getSize().width;
        return (0.00390625f * paintingWidth) + (0.043945312f * paintingWidth * size);
    }

    public int getCurrentColor() {
        return this.color;
    }

    public void setColor(int value) {
        this.color = value;
    }

    public float getCurrentWeight() {
        return this.weight;
    }

    public void setBrushSize(float size) {
        this.weight = brushWeightForSize(size);
    }

    public Brush getCurrentBrush() {
        return this.brush;
    }

    public void setBrush(Brush value) {
        Painting painting2 = this.painting;
        this.brush = value;
        painting2.setBrush(value);
    }

    /* access modifiers changed from: private */
    public void updateTransform() {
        Matrix matrix = new Matrix();
        float scale = this.painting != null ? ((float) getWidth()) / this.painting.getSize().width : 1.0f;
        if (scale <= 0.0f) {
            scale = 1.0f;
        }
        Size paintingSize = getPainting().getSize();
        matrix.preTranslate(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        matrix.preScale(scale, -scale);
        matrix.preTranslate((-paintingSize.width) / 2.0f, (-paintingSize.height) / 2.0f);
        this.input.setMatrix(matrix);
        this.painting.setRenderProjection(GLMatrix.MultiplyMat4f(GLMatrix.LoadOrtho(0.0f, (float) this.internal.bufferWidth, 0.0f, (float) this.internal.bufferHeight, -1.0f, 1.0f), GLMatrix.LoadGraphicsMatrix(matrix)));
    }

    public boolean shouldDraw() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        return renderViewDelegate == null || renderViewDelegate.shouldDraw();
    }

    public void onBeganDrawing() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onBeganDrawing();
        }
    }

    public void onFinishedDrawing(boolean moved) {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onFinishedDrawing(moved);
        }
    }

    public void shutdown() {
        this.shuttingDown = true;
        if (this.internal != null) {
            performInContext(new Runnable() {
                public void run() {
                    RenderView.this.painting.cleanResources(RenderView.this.transformedBitmap);
                    RenderView.this.internal.shutdown();
                    CanvasInternal unused = RenderView.this.internal = null;
                }
            });
        }
        setVisibility(8);
    }

    private class CanvasInternal extends DispatchQueue {
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        /* access modifiers changed from: private */
        public int bufferHeight;
        /* access modifiers changed from: private */
        public int bufferWidth;
        /* access modifiers changed from: private */
        public Runnable drawRunnable = new Runnable() {
            public void run() {
                if (CanvasInternal.this.initialized && !RenderView.this.shuttingDown) {
                    boolean unused = CanvasInternal.this.setCurrentContext();
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GLES20.glClear(16384);
                    RenderView.this.painting.render();
                    GLES20.glBlendFunc(1, 771);
                    CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                    if (!CanvasInternal.this.ready) {
                        RenderView.this.queue.postRunnable(new Runnable() {
                            public void run() {
                                boolean unused = CanvasInternal.this.ready = true;
                            }
                        }, 200);
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        /* access modifiers changed from: private */
        public boolean initialized;
        private long lastRenderCallTime;
        /* access modifiers changed from: private */
        public boolean ready;
        /* access modifiers changed from: private */
        public Runnable scheduledRunnable;
        private SurfaceTexture surfaceTexture;

        public CanvasInternal(SurfaceTexture surface) {
            super("CanvasInternal");
            this.surfaceTexture = surface;
        }

        public void run() {
            if (RenderView.this.bitmap != null && !RenderView.this.bitmap.isRecycled()) {
                this.initialized = initGL();
                super.run();
            }
        }

        private boolean initGL() {
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    EGL10 egl103 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    GLES20.glEnable(3042);
                    GLES20.glDisable(3024);
                    GLES20.glDisable(2960);
                    GLES20.glDisable(2929);
                    RenderView.this.painting.setupShaders();
                    checkBitmap();
                    RenderView.this.painting.setBitmap(RenderView.this.bitmap);
                    Utils.HasGLError();
                    return true;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        private Bitmap createBitmap(Bitmap bitmap, float scale) {
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postRotate((float) RenderView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private void checkBitmap() {
            Size paintingSize = RenderView.this.painting.getSize();
            if (((float) RenderView.this.bitmap.getWidth()) != paintingSize.width || ((float) RenderView.this.bitmap.getHeight()) != paintingSize.height || RenderView.this.orientation != 0) {
                float bitmapWidth = (float) RenderView.this.bitmap.getWidth();
                if (RenderView.this.orientation % 360 == 90 || RenderView.this.orientation % 360 == 270) {
                    bitmapWidth = (float) RenderView.this.bitmap.getHeight();
                }
                RenderView renderView = RenderView.this;
                Bitmap unused = renderView.bitmap = createBitmap(renderView.bitmap, paintingSize.width / bitmapWidth);
                int unused2 = RenderView.this.orientation = 0;
                boolean unused3 = RenderView.this.transformedBitmap = true;
            }
        }

        /* access modifiers changed from: private */
        public boolean setCurrentContext() {
            if (!this.initialized) {
                return false;
            }
            if (this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                return true;
            }
            EGL10 egl102 = this.egl10;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglSurface;
            if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                return false;
            }
            return true;
        }

        public void setBufferSize(int width, int height) {
            this.bufferWidth = width;
            this.bufferHeight = height;
        }

        public void requestRender() {
            postRunnable(new Runnable() {
                public void run() {
                    CanvasInternal.this.drawRunnable.run();
                }
            });
        }

        public void scheduleRedraw() {
            Runnable runnable = this.scheduledRunnable;
            if (runnable != null) {
                cancelRunnable(runnable);
                this.scheduledRunnable = null;
            }
            AnonymousClass3 r0 = new Runnable() {
                public void run() {
                    Runnable unused = CanvasInternal.this.scheduledRunnable = null;
                    CanvasInternal.this.drawRunnable.run();
                }
            };
            this.scheduledRunnable = r0;
            postRunnable(r0, 1);
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void shutdown() {
            postRunnable(new Runnable() {
                public void run() {
                    CanvasInternal.this.finish();
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                    }
                }
            });
        }

        public Bitmap getTexture() {
            if (!this.initialized) {
                return null;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Bitmap[] object = new Bitmap[1];
            try {
                postRunnable(new Runnable() {
                    public void run() {
                        object[0] = RenderView.this.painting.getPaintingData(new RectF(0.0f, 0.0f, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false).bitmap;
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            return object[0];
        }
    }

    public Bitmap getResultBitmap() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            return canvasInternal.getTexture();
        }
        return null;
    }

    public void performInContext(final Runnable action) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            canvasInternal.postRunnable(new Runnable() {
                public void run() {
                    if (RenderView.this.internal != null && RenderView.this.internal.initialized) {
                        boolean unused = RenderView.this.internal.setCurrentContext();
                        action.run();
                    }
                }
            });
        }
    }
}
