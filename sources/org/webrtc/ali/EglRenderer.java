package org.webrtc.ali;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.webrtc.ali.EglBase;
import org.webrtc.ali.RendererCommon;
import org.webrtc.ali.VideoRenderer;

public class EglRenderer implements VideoRenderer.Callbacks {
    private static final long LOG_INTERVAL_SEC = 4;
    private static final int MAX_SURFACE_CLEAR_COUNT = 3;
    private static final String TAG = "EglRenderer";
    /* access modifiers changed from: private */
    public GlTextureFrameBuffer bitmapTextureFramebuffer;
    /* access modifiers changed from: private */
    public RendererCommon.GlDrawer drawer;
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final EglSurfaceCreation eglSurfaceCreationRunnable = new EglSurfaceCreation();
    private final Object fpsReductionLock = new Object();
    /* access modifiers changed from: private */
    public final ArrayList<FrameListenerAndParams> frameListeners = new ArrayList<>();
    private final Object frameLock = new Object();
    private int framesDropped;
    private int framesReceived;
    private int framesRendered;
    /* access modifiers changed from: private */
    public final Object handlerLock = new Object();
    private float layoutAspectRatio;
    private final Object layoutLock = new Object();
    /* access modifiers changed from: private */
    public final Runnable logStatisticsRunnable = new Runnable() {
        public void run() {
            EglRenderer.this.logStatistics();
            synchronized (EglRenderer.this.handlerLock) {
                if (EglRenderer.this.renderThreadHandler != null) {
                    EglRenderer.this.renderThreadHandler.removeCallbacks(EglRenderer.this.logStatisticsRunnable);
                    EglRenderer.this.renderThreadHandler.postDelayed(EglRenderer.this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(EglRenderer.LOG_INTERVAL_SEC));
                }
            }
        }
    };
    private long minRenderPeriodNs;
    private boolean mirror;
    private final String name;
    private long nextFrameTimeNs;
    private VideoRenderer.I420Frame pendingFrame;
    private final Runnable renderFrameRunnable = new Runnable() {
        public void run() {
            EglRenderer.this.renderFrameOnRenderThread();
        }
    };
    private long renderSwapBufferTimeNs;
    /* access modifiers changed from: private */
    public Handler renderThreadHandler;
    private long renderTimeNs;
    private final Object statisticsLock = new Object();
    private long statisticsStartTimeNs;
    /* access modifiers changed from: private */
    public final RendererCommon.YuvUploader yuvUploader = new RendererCommon.YuvUploader();

    public interface FrameListener {
        void onFrame(Bitmap bitmap);
    }

    private static class FrameListenerAndParams {
        public final boolean applyFpsReduction;
        public final RendererCommon.GlDrawer drawer;
        public final FrameListener listener;
        public final float scale;

        public FrameListenerAndParams(FrameListener listener2, float scale2, RendererCommon.GlDrawer drawer2, boolean applyFpsReduction2) {
            this.listener = listener2;
            this.scale = scale2;
            this.drawer = drawer2;
            this.applyFpsReduction = applyFpsReduction2;
        }
    }

    private class EglSurfaceCreation implements Runnable {
        private Object surface;

        private EglSurfaceCreation() {
        }

        public synchronized void setSurface(Object surface2) {
            this.surface = surface2;
        }

        public synchronized void run() {
            if (!(this.surface == null || EglRenderer.this.eglBase == null || EglRenderer.this.eglBase.hasSurface())) {
                if (this.surface instanceof Surface) {
                    EglRenderer.this.eglBase.createSurface((Surface) this.surface);
                } else if (this.surface instanceof SurfaceTexture) {
                    EglRenderer.this.eglBase.createSurface((SurfaceTexture) this.surface);
                } else {
                    throw new IllegalStateException("Invalid surface: " + this.surface);
                }
                EglRenderer.this.eglBase.makeCurrent();
                GLES20.glPixelStorei(3317, 1);
            }
        }
    }

    public EglRenderer(String name2) {
        this.name = name2;
    }

    public void init(final EglBase.Context sharedContext, final int[] configAttributes, RendererCommon.GlDrawer drawer2) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                logD("Initializing EglRenderer");
                this.drawer = drawer2;
                HandlerThread renderThread = new HandlerThread(this.name + TAG);
                renderThread.start();
                Handler handler = new Handler(renderThread.getLooper());
                this.renderThreadHandler = handler;
                ThreadUtils.invokeAtFrontUninterruptibly(handler, (Runnable) new Runnable() {
                    public void run() {
                        if (sharedContext == null) {
                            EglRenderer.this.logD("EglBase10.create context");
                            EglBase unused = EglRenderer.this.eglBase = EglBase.createEgl10(configAttributes);
                            return;
                        }
                        EglRenderer.this.logD("EglBase.create shared context");
                        EglBase unused2 = EglRenderer.this.eglBase = EglBase.create(sharedContext, configAttributes);
                    }
                });
                this.renderThreadHandler.post(this.eglSurfaceCreationRunnable);
                resetStatistics(System.nanoTime());
                this.renderThreadHandler.postDelayed(this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(LOG_INTERVAL_SEC));
            } else {
                throw new IllegalStateException(this.name + "Already initialized");
            }
        }
    }

    public void createEglSurface(Surface surface) {
        createEglSurfaceInternal(surface);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture);
    }

    private void createEglSurfaceInternal(Object surface) {
        this.eglSurfaceCreationRunnable.setSurface(surface);
        postToRenderThread(this.eglSurfaceCreationRunnable);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003e, code lost:
        org.webrtc.ali.ThreadUtils.awaitUninterruptibly(r0);
        r2 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0043, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        if (r5.pendingFrame == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        org.webrtc.ali.VideoRenderer.renderFrameDone(r5.pendingFrame);
        r5.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0050, code lost:
        logD("Releasing done.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0055, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void release() {
        /*
            r5 = this;
            java.lang.String r0 = "Releasing."
            r5.logD(r0)
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch
            r1 = 1
            r0.<init>(r1)
            java.lang.Object r1 = r5.handlerLock
            monitor-enter(r1)
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            if (r2 != 0) goto L_0x0019
            java.lang.String r2 = "Already released"
            r5.logD(r2)     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            return
        L_0x0019:
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            java.lang.Runnable r3 = r5.logStatisticsRunnable     // Catch:{ all -> 0x0059 }
            r2.removeCallbacks(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.ali.EglRenderer$4 r3 = new org.webrtc.ali.EglRenderer$4     // Catch:{ all -> 0x0059 }
            r3.<init>(r0)     // Catch:{ all -> 0x0059 }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x0059 }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.ali.EglRenderer$5 r4 = new org.webrtc.ali.EglRenderer$5     // Catch:{ all -> 0x0059 }
            r4.<init>(r2)     // Catch:{ all -> 0x0059 }
            r3.post(r4)     // Catch:{ all -> 0x0059 }
            r3 = 0
            r5.renderThreadHandler = r3     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            org.webrtc.ali.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r2 = r5.frameLock
            monitor-enter(r2)
            org.webrtc.ali.VideoRenderer$I420Frame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            if (r1 == 0) goto L_0x004f
            org.webrtc.ali.VideoRenderer$I420Frame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            org.webrtc.ali.VideoRenderer.renderFrameDone(r1)     // Catch:{ all -> 0x0056 }
            r5.pendingFrame = r3     // Catch:{ all -> 0x0056 }
        L_0x004f:
            monitor-exit(r2)     // Catch:{ all -> 0x0056 }
            java.lang.String r1 = "Releasing done."
            r5.logD(r1)
            return
        L_0x0056:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0056 }
            throw r1
        L_0x0059:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.EglRenderer.release():void");
    }

    private void resetStatistics(long currentTimeNs) {
        synchronized (this.statisticsLock) {
            this.statisticsStartTimeNs = currentTimeNs;
            this.framesReceived = 0;
            this.framesDropped = 0;
            this.framesRendered = 0;
            this.renderTimeNs = 0;
            this.renderSwapBufferTimeNs = 0;
        }
    }

    public void printStackTrace() {
        Thread renderThread;
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                renderThread = null;
            } else {
                renderThread = this.renderThreadHandler.getLooper().getThread();
            }
            if (renderThread != null) {
                StackTraceElement[] renderStackTrace = renderThread.getStackTrace();
                if (renderStackTrace.length > 0) {
                    logD("EglRenderer stack trace:");
                    for (StackTraceElement traceElem : renderStackTrace) {
                        logD(traceElem.toString());
                    }
                }
            }
        }
    }

    public void setMirror(boolean mirror2) {
        logD("setMirror: " + mirror2);
        synchronized (this.layoutLock) {
            this.mirror = mirror2;
        }
    }

    public void setLayoutAspectRatio(float layoutAspectRatio2) {
        logD("setLayoutAspectRatio: " + layoutAspectRatio2);
        synchronized (this.layoutLock) {
            this.layoutAspectRatio = layoutAspectRatio2;
        }
    }

    public void setFpsReduction(float fps) {
        logD("setFpsReduction: " + fps);
        synchronized (this.fpsReductionLock) {
            long previousRenderPeriodNs = this.minRenderPeriodNs;
            if (fps <= 0.0f) {
                this.minRenderPeriodNs = Long.MAX_VALUE;
            } else {
                this.minRenderPeriodNs = (long) (((float) TimeUnit.SECONDS.toNanos(1)) / fps);
            }
            if (this.minRenderPeriodNs != previousRenderPeriodNs) {
                this.nextFrameTimeNs = System.nanoTime();
            }
        }
    }

    public void disableFpsReduction() {
        setFpsReduction(Float.POSITIVE_INFINITY);
    }

    public void pauseVideo() {
        setFpsReduction(0.0f);
    }

    public void addFrameListener(FrameListener listener, float scale) {
        addFrameListener(listener, scale, (RendererCommon.GlDrawer) null, false);
    }

    public void addFrameListener(FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
        addFrameListener(listener, scale, drawerParam, false);
    }

    public void addFrameListener(FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam, boolean applyFpsReduction) {
        final RendererCommon.GlDrawer glDrawer = drawerParam;
        final FrameListener frameListener = listener;
        final float f = scale;
        final boolean z = applyFpsReduction;
        postToRenderThread(new Runnable() {
            public void run() {
                RendererCommon.GlDrawer listenerDrawer = glDrawer;
                if (listenerDrawer == null) {
                    listenerDrawer = EglRenderer.this.drawer;
                }
                EglRenderer.this.frameListeners.add(new FrameListenerAndParams(frameListener, f, listenerDrawer, z));
            }
        });
    }

    public void removeFrameListener(final FrameListener listener) {
        if (Thread.currentThread() != this.renderThreadHandler.getLooper().getThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            postToRenderThread(new Runnable() {
                public void run() {
                    latch.countDown();
                    Iterator<FrameListenerAndParams> iter = EglRenderer.this.frameListeners.iterator();
                    while (iter.hasNext()) {
                        if (iter.next().listener == listener) {
                            iter.remove();
                        }
                    }
                }
            });
            ThreadUtils.awaitUninterruptibly(latch);
            return;
        }
        throw new RuntimeException("removeFrameListener must not be called on the render thread.");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0037, code lost:
        if (r3 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0039, code lost:
        r0 = r6.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003b, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r6.framesDropped++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0041, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrame(org.webrtc.ali.VideoRenderer.I420Frame r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.statisticsLock
            monitor-enter(r0)
            int r1 = r6.framesReceived     // Catch:{ all -> 0x004d }
            r2 = 1
            int r1 = r1 + r2
            r6.framesReceived = r1     // Catch:{ all -> 0x004d }
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            java.lang.Object r1 = r6.handlerLock
            monitor-enter(r1)
            android.os.Handler r0 = r6.renderThreadHandler     // Catch:{ all -> 0x004a }
            if (r0 != 0) goto L_0x001b
            java.lang.String r0 = "Dropping frame - Not initialized or already released."
            r6.logD(r0)     // Catch:{ all -> 0x004a }
            org.webrtc.ali.VideoRenderer.renderFrameDone(r7)     // Catch:{ all -> 0x004a }
            monitor-exit(r1)     // Catch:{ all -> 0x004a }
            return
        L_0x001b:
            java.lang.Object r0 = r6.frameLock     // Catch:{ all -> 0x004a }
            monitor-enter(r0)     // Catch:{ all -> 0x004a }
            org.webrtc.ali.VideoRenderer$I420Frame r3 = r6.pendingFrame     // Catch:{ all -> 0x0047 }
            if (r3 == 0) goto L_0x0024
            r3 = 1
            goto L_0x0025
        L_0x0024:
            r3 = 0
        L_0x0025:
            if (r3 == 0) goto L_0x002c
            org.webrtc.ali.VideoRenderer$I420Frame r4 = r6.pendingFrame     // Catch:{ all -> 0x0047 }
            org.webrtc.ali.VideoRenderer.renderFrameDone(r4)     // Catch:{ all -> 0x0047 }
        L_0x002c:
            r6.pendingFrame = r7     // Catch:{ all -> 0x0047 }
            android.os.Handler r4 = r6.renderThreadHandler     // Catch:{ all -> 0x0047 }
            java.lang.Runnable r5 = r6.renderFrameRunnable     // Catch:{ all -> 0x0047 }
            r4.post(r5)     // Catch:{ all -> 0x0047 }
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            monitor-exit(r1)     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x0046
            java.lang.Object r0 = r6.statisticsLock
            monitor-enter(r0)
            int r1 = r6.framesDropped     // Catch:{ all -> 0x0043 }
            int r1 = r1 + r2
            r6.framesDropped = r1     // Catch:{ all -> 0x0043 }
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            goto L_0x0046
        L_0x0043:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            throw r1
        L_0x0046:
            return
        L_0x0047:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0047 }
            throw r2     // Catch:{ all -> 0x004a }
        L_0x004a:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004a }
            throw r0
        L_0x004d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.EglRenderer.renderFrame(org.webrtc.ali.VideoRenderer$I420Frame):void");
    }

    public void releaseEglSurface(final Runnable completionCallback) {
        this.eglSurfaceCreationRunnable.setSurface((Object) null);
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.removeCallbacks(this.eglSurfaceCreationRunnable);
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        if (EglRenderer.this.eglBase != null) {
                            EglRenderer.this.eglBase.detachCurrent();
                            EglRenderer.this.eglBase.releaseSurface();
                        }
                        completionCallback.run();
                    }
                });
                return;
            }
            completionCallback.run();
        }
    }

    private void postToRenderThread(Runnable runnable) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.post(runnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void clearSurfaceOnRenderThread(float r, float g, float b, float a) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null && eglBase2.hasSurface()) {
            logD("clearSurface");
            GLES20.glClearColor(r, g, b, a);
            GLES20.glClear(16384);
            this.eglBase.swapBuffers();
        }
    }

    public void clearImage() {
        clearImage(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clearImage(float r, float g, float b, float a) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                final float f = r;
                final float f2 = g;
                final float f3 = b;
                final float f4 = a;
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        EglRenderer.this.clearSurfaceOnRenderThread(f, f2, f3, f4);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        if (r2 == null) goto L_0x0193;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001a, code lost:
        if (r2.hasSurface() != false) goto L_0x001e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        r2 = r1.fpsReductionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002a, code lost:
        if (r1.minRenderPeriodNs != Long.MAX_VALUE) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002c, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0034, code lost:
        if (r1.minRenderPeriodNs > 0) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0036, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0038, code lost:
        r4 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0040, code lost:
        if (r4 >= r1.nextFrameTimeNs) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0042, code lost:
        logD("Skipping frame rendering - fps reduction is active.");
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004a, code lost:
        r6 = r1.nextFrameTimeNs + r1.minRenderPeriodNs;
        r1.nextFrameTimeNs = r6;
        r1.nextFrameTimeNs = java.lang.Math.max(r6, r4);
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0059, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x005a, code lost:
        r5 = java.lang.System.nanoTime();
        r7 = org.webrtc.ali.RendererCommon.rotateTextureMatrix(r3.samplingMatrix, (float) r3.rotationDegree);
        r8 = r1.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0069, code lost:
        monitor-enter(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006f, code lost:
        if (r1.layoutAspectRatio <= 0.0f) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0071, code lost:
        r2 = ((float) r3.rotatedWidth()) / ((float) r3.rotatedHeight());
        r10 = org.webrtc.ali.RendererCommon.getLayoutMatrix(r1.mirror, r2, r1.layoutAspectRatio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0088, code lost:
        if (r2 <= r1.layoutAspectRatio) goto L_0x0099;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008a, code lost:
        r11 = (int) (((float) r3.rotatedHeight()) * r1.layoutAspectRatio);
        r12 = r3.rotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0099, code lost:
        r11 = r3.rotatedWidth();
        r12 = (int) (((float) r3.rotatedWidth()) / r1.layoutAspectRatio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a6, code lost:
        r2 = r11;
        r23 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ac, code lost:
        if (r1.mirror == false) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ae, code lost:
        r2 = org.webrtc.ali.RendererCommon.horizontalFlipMatrix();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b3, code lost:
        r2 = org.webrtc.ali.RendererCommon.identityMatrix();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b7, code lost:
        r10 = r2;
        r2 = r3.rotatedWidth();
        r23 = r3.rotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c5, code lost:
        r13 = org.webrtc.ali.RendererCommon.multiplyMatrices(r7, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00c9, code lost:
        monitor-exit(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ca, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00cd, code lost:
        if (r3.yuvFrame == false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00cf, code lost:
        r8 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00d0, code lost:
        if (r8 != false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d2, code lost:
        r10 = r1.frameListeners.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00dc, code lost:
        if (r10.hasNext() == false) goto L_0x00f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00de, code lost:
        r11 = r10.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00e8, code lost:
        if (r11.scale == 0.0f) goto L_0x00d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ea, code lost:
        if (r4 != false) goto L_0x00f0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ee, code lost:
        if (r11.applyFpsReduction != false) goto L_0x00d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00f0, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00f3, code lost:
        if (r8 == false) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00f5, code lost:
        r0 = r1.yuvUploader.uploadYuvData(r3.width, r3.height, r3.yuvStrides, r3.yuvPlanes);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0105, code lost:
        r10 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0106, code lost:
        if (r4 == false) goto L_0x0180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0108, code lost:
        android.opengl.GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        android.opengl.GLES20.glClear(16384);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0112, code lost:
        if (r3.yuvFrame == false) goto L_0x012e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0114, code lost:
        r1.drawer.drawYuv(r10, r13, r2, r23, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x012e, code lost:
        r1.drawer.drawOes(r3.textureId, r13, r2, r23, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x014b, code lost:
        r11 = java.lang.System.nanoTime();
        r1.eglBase.swapBuffers();
        r14 = java.lang.System.nanoTime();
        r9 = r1.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x015a, code lost:
        monitor-enter(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
        r1.framesRendered++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0161, code lost:
        r17 = r2;
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:?, code lost:
        r1.renderTimeNs += r14 - r5;
        r1.renderSwapBufferTimeNs += r14 - r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0175, code lost:
        monitor-exit(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0177, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0178, code lost:
        r17 = r2;
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x017c, code lost:
        monitor-exit(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x017d, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x017e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0180, code lost:
        r17 = r2;
        r16 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0184, code lost:
        r3 = r16;
        notifyCallbacks(r3, r10, r7, r4);
        org.webrtc.ali.VideoRenderer.renderFrameDone(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x018c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0193, code lost:
        logD("Dropping frame - No surface");
        org.webrtc.ali.VideoRenderer.renderFrameDone(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x019b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        r2 = r1.eglBase;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r24 = this;
            r1 = r24
            java.lang.Object r2 = r1.frameLock
            monitor-enter(r2)
            org.webrtc.ali.VideoRenderer$I420Frame r0 = r1.pendingFrame     // Catch:{ all -> 0x019c }
            if (r0 != 0) goto L_0x000b
            monitor-exit(r2)     // Catch:{ all -> 0x019c }
            return
        L_0x000b:
            org.webrtc.ali.VideoRenderer$I420Frame r0 = r1.pendingFrame     // Catch:{ all -> 0x019c }
            r3 = r0
            r0 = 0
            r1.pendingFrame = r0     // Catch:{ all -> 0x019c }
            monitor-exit(r2)     // Catch:{ all -> 0x019c }
            org.webrtc.ali.EglBase r2 = r1.eglBase
            if (r2 == 0) goto L_0x0193
            boolean r2 = r2.hasSurface()
            if (r2 != 0) goto L_0x001e
            goto L_0x0193
        L_0x001e:
            java.lang.Object r2 = r1.fpsReductionLock
            monitor-enter(r2)
            long r4 = r1.minRenderPeriodNs     // Catch:{ all -> 0x0190 }
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x002e
            r4 = 0
            goto L_0x0059
        L_0x002e:
            long r4 = r1.minRenderPeriodNs     // Catch:{ all -> 0x0190 }
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 > 0) goto L_0x0038
            r4 = 1
            goto L_0x0059
        L_0x0038:
            long r4 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0190 }
            long r6 = r1.nextFrameTimeNs     // Catch:{ all -> 0x0190 }
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x004a
            java.lang.String r6 = "Skipping frame rendering - fps reduction is active."
            r1.logD(r6)     // Catch:{ all -> 0x0190 }
            r6 = 0
            r4 = r6
            goto L_0x0059
        L_0x004a:
            long r6 = r1.nextFrameTimeNs     // Catch:{ all -> 0x0190 }
            long r8 = r1.minRenderPeriodNs     // Catch:{ all -> 0x0190 }
            long r6 = r6 + r8
            r1.nextFrameTimeNs = r6     // Catch:{ all -> 0x0190 }
            long r6 = java.lang.Math.max(r6, r4)     // Catch:{ all -> 0x0190 }
            r1.nextFrameTimeNs = r6     // Catch:{ all -> 0x0190 }
            r6 = 1
            r4 = r6
        L_0x0059:
            monitor-exit(r2)     // Catch:{ all -> 0x0190 }
            long r5 = java.lang.System.nanoTime()
            float[] r2 = r3.samplingMatrix
            int r7 = r3.rotationDegree
            float r7 = (float) r7
            float[] r7 = org.webrtc.ali.RendererCommon.rotateTextureMatrix(r2, r7)
            java.lang.Object r8 = r1.layoutLock
            monitor-enter(r8)
            float r2 = r1.layoutAspectRatio     // Catch:{ all -> 0x018d }
            r9 = 0
            int r2 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x00aa
            int r2 = r3.rotatedWidth()     // Catch:{ all -> 0x018d }
            float r2 = (float) r2     // Catch:{ all -> 0x018d }
            int r10 = r3.rotatedHeight()     // Catch:{ all -> 0x018d }
            float r10 = (float) r10     // Catch:{ all -> 0x018d }
            float r2 = r2 / r10
            boolean r10 = r1.mirror     // Catch:{ all -> 0x018d }
            float r11 = r1.layoutAspectRatio     // Catch:{ all -> 0x018d }
            float[] r10 = org.webrtc.ali.RendererCommon.getLayoutMatrix(r10, r2, r11)     // Catch:{ all -> 0x018d }
            float r11 = r1.layoutAspectRatio     // Catch:{ all -> 0x018d }
            int r11 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r11 <= 0) goto L_0x0099
            int r11 = r3.rotatedHeight()     // Catch:{ all -> 0x018d }
            float r11 = (float) r11     // Catch:{ all -> 0x018d }
            float r12 = r1.layoutAspectRatio     // Catch:{ all -> 0x018d }
            float r11 = r11 * r12
            int r11 = (int) r11     // Catch:{ all -> 0x018d }
            int r12 = r3.rotatedHeight()     // Catch:{ all -> 0x018d }
            goto L_0x00a6
        L_0x0099:
            int r11 = r3.rotatedWidth()     // Catch:{ all -> 0x018d }
            int r12 = r3.rotatedWidth()     // Catch:{ all -> 0x018d }
            float r12 = (float) r12     // Catch:{ all -> 0x018d }
            float r13 = r1.layoutAspectRatio     // Catch:{ all -> 0x018d }
            float r12 = r12 / r13
            int r12 = (int) r12     // Catch:{ all -> 0x018d }
        L_0x00a6:
            r2 = r11
            r23 = r12
            goto L_0x00c5
        L_0x00aa:
            boolean r2 = r1.mirror     // Catch:{ all -> 0x018d }
            if (r2 == 0) goto L_0x00b3
            float[] r2 = org.webrtc.ali.RendererCommon.horizontalFlipMatrix()     // Catch:{ all -> 0x018d }
            goto L_0x00b7
        L_0x00b3:
            float[] r2 = org.webrtc.ali.RendererCommon.identityMatrix()     // Catch:{ all -> 0x018d }
        L_0x00b7:
            r10 = r2
            int r2 = r3.rotatedWidth()     // Catch:{ all -> 0x018d }
            r11 = r2
            int r2 = r3.rotatedHeight()     // Catch:{ all -> 0x018d }
            r12 = r2
            r2 = r11
            r23 = r12
        L_0x00c5:
            float[] r13 = org.webrtc.ali.RendererCommon.multiplyMatrices(r7, r10)     // Catch:{ all -> 0x018d }
            monitor-exit(r8)     // Catch:{ all -> 0x018d }
            r8 = 0
            boolean r10 = r3.yuvFrame
            if (r10 == 0) goto L_0x00f3
            r8 = r4
            if (r8 != 0) goto L_0x00f3
            java.util.ArrayList<org.webrtc.ali.EglRenderer$FrameListenerAndParams> r10 = r1.frameListeners
            java.util.Iterator r10 = r10.iterator()
        L_0x00d8:
            boolean r11 = r10.hasNext()
            if (r11 == 0) goto L_0x00f3
            java.lang.Object r11 = r10.next()
            org.webrtc.ali.EglRenderer$FrameListenerAndParams r11 = (org.webrtc.ali.EglRenderer.FrameListenerAndParams) r11
            float r12 = r11.scale
            int r12 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r12 == 0) goto L_0x00f2
            if (r4 != 0) goto L_0x00f0
            boolean r12 = r11.applyFpsReduction
            if (r12 != 0) goto L_0x00f2
        L_0x00f0:
            r8 = 1
            goto L_0x00f3
        L_0x00f2:
            goto L_0x00d8
        L_0x00f3:
            if (r8 == 0) goto L_0x0104
            org.webrtc.ali.RendererCommon$YuvUploader r0 = r1.yuvUploader
            int r10 = r3.width
            int r11 = r3.height
            int[] r12 = r3.yuvStrides
            java.nio.ByteBuffer[] r14 = r3.yuvPlanes
            int[] r0 = r0.uploadYuvData(r10, r11, r12, r14)
            goto L_0x0105
        L_0x0104:
        L_0x0105:
            r10 = r0
            if (r4 == 0) goto L_0x0180
            android.opengl.GLES20.glClearColor(r9, r9, r9, r9)
            r0 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r0)
            boolean r0 = r3.yuvFrame
            if (r0 == 0) goto L_0x012e
            org.webrtc.ali.RendererCommon$GlDrawer r11 = r1.drawer
            r16 = 0
            r17 = 0
            org.webrtc.ali.EglBase r0 = r1.eglBase
            int r18 = r0.surfaceWidth()
            org.webrtc.ali.EglBase r0 = r1.eglBase
            int r19 = r0.surfaceHeight()
            r12 = r10
            r14 = r2
            r15 = r23
            r11.drawYuv(r12, r13, r14, r15, r16, r17, r18, r19)
            goto L_0x014b
        L_0x012e:
            org.webrtc.ali.RendererCommon$GlDrawer r14 = r1.drawer
            int r15 = r3.textureId
            r19 = 0
            r20 = 0
            org.webrtc.ali.EglBase r0 = r1.eglBase
            int r21 = r0.surfaceWidth()
            org.webrtc.ali.EglBase r0 = r1.eglBase
            int r22 = r0.surfaceHeight()
            r16 = r13
            r17 = r2
            r18 = r23
            r14.drawOes(r15, r16, r17, r18, r19, r20, r21, r22)
        L_0x014b:
            long r11 = java.lang.System.nanoTime()
            org.webrtc.ali.EglBase r0 = r1.eglBase
            r0.swapBuffers()
            long r14 = java.lang.System.nanoTime()
            java.lang.Object r9 = r1.statisticsLock
            monitor-enter(r9)
            int r0 = r1.framesRendered     // Catch:{ all -> 0x0177 }
            int r0 = r0 + 1
            r1.framesRendered = r0     // Catch:{ all -> 0x0177 }
            r17 = r2
            r16 = r3
            long r2 = r1.renderTimeNs     // Catch:{ all -> 0x017e }
            long r18 = r14 - r5
            long r2 = r2 + r18
            r1.renderTimeNs = r2     // Catch:{ all -> 0x017e }
            long r2 = r1.renderSwapBufferTimeNs     // Catch:{ all -> 0x017e }
            long r18 = r14 - r11
            long r2 = r2 + r18
            r1.renderSwapBufferTimeNs = r2     // Catch:{ all -> 0x017e }
            monitor-exit(r9)     // Catch:{ all -> 0x017e }
            goto L_0x0184
        L_0x0177:
            r0 = move-exception
            r17 = r2
            r16 = r3
        L_0x017c:
            monitor-exit(r9)     // Catch:{ all -> 0x017e }
            throw r0
        L_0x017e:
            r0 = move-exception
            goto L_0x017c
        L_0x0180:
            r17 = r2
            r16 = r3
        L_0x0184:
            r3 = r16
            r1.notifyCallbacks(r3, r10, r7, r4)
            org.webrtc.ali.VideoRenderer.renderFrameDone(r3)
            return
        L_0x018d:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x018d }
            throw r0
        L_0x0190:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0190 }
            throw r0
        L_0x0193:
            java.lang.String r0 = "Dropping frame - No surface"
            r1.logD(r0)
            org.webrtc.ali.VideoRenderer.renderFrameDone(r3)
            return
        L_0x019c:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x019c }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.EglRenderer.renderFrameOnRenderThread():void");
    }

    private void notifyCallbacks(VideoRenderer.I420Frame frame, int[] yuvTextures, float[] texMatrix, boolean wasRendered) {
        int scaledHeight;
        EglRenderer eglRenderer = this;
        VideoRenderer.I420Frame i420Frame = frame;
        if (!eglRenderer.frameListeners.isEmpty()) {
            float[] bitmapMatrix = RendererCommon.multiplyMatrices(RendererCommon.multiplyMatrices(texMatrix, eglRenderer.mirror ? RendererCommon.horizontalFlipMatrix() : RendererCommon.identityMatrix()), RendererCommon.verticalFlipMatrix());
            Iterator<FrameListenerAndParams> it = eglRenderer.frameListeners.iterator();
            while (it.hasNext()) {
                FrameListenerAndParams listenerAndParams = it.next();
                if (wasRendered || !listenerAndParams.applyFpsReduction) {
                    it.remove();
                    int scaledWidth = (int) (listenerAndParams.scale * ((float) frame.rotatedWidth()));
                    int scaledHeight2 = (int) (listenerAndParams.scale * ((float) frame.rotatedHeight()));
                    if (scaledWidth == 0 || scaledHeight2 == 0) {
                        int i = scaledHeight2;
                        listenerAndParams.listener.onFrame((Bitmap) null);
                        eglRenderer = this;
                    } else {
                        if (eglRenderer.bitmapTextureFramebuffer == null) {
                            eglRenderer.bitmapTextureFramebuffer = new GlTextureFrameBuffer(6408);
                        }
                        eglRenderer.bitmapTextureFramebuffer.setSize(scaledWidth, scaledHeight2);
                        GLES20.glBindFramebuffer(36160, eglRenderer.bitmapTextureFramebuffer.getFrameBufferId());
                        GLES20.glFramebufferTexture2D(36160, 36064, 3553, eglRenderer.bitmapTextureFramebuffer.getTextureId(), 0);
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GLES20.glClear(16384);
                        if (i420Frame.yuvFrame) {
                            scaledHeight = scaledHeight2;
                            listenerAndParams.drawer.drawYuv(yuvTextures, bitmapMatrix, frame.rotatedWidth(), frame.rotatedHeight(), 0, 0, scaledWidth, scaledHeight2);
                        } else {
                            scaledHeight = scaledHeight2;
                            listenerAndParams.drawer.drawOes(i420Frame.textureId, bitmapMatrix, frame.rotatedWidth(), frame.rotatedHeight(), 0, 0, scaledWidth, scaledHeight2);
                        }
                        int scaledHeight3 = scaledHeight;
                        ByteBuffer bitmapBuffer = ByteBuffer.allocateDirect(scaledWidth * scaledHeight3 * 4);
                        GLES20.glViewport(0, 0, scaledWidth, scaledHeight3);
                        GLES20.glReadPixels(0, 0, scaledWidth, scaledHeight3, 6408, 5121, bitmapBuffer);
                        GLES20.glBindFramebuffer(36160, 0);
                        GlUtil.checkNoGLES2Error("EglRenderer.notifyCallbacks");
                        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight3, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(bitmapBuffer);
                        listenerAndParams.listener.onFrame(bitmap);
                        eglRenderer = this;
                    }
                }
            }
        }
    }

    private String averageTimeAsString(long sumTimeNs, int count) {
        if (count <= 0) {
            return "NA";
        }
        return TimeUnit.NANOSECONDS.toMicros(sumTimeNs / ((long) count)) + " μs";
    }

    /* access modifiers changed from: private */
    public void logStatistics() {
        long currentTimeNs = System.nanoTime();
        synchronized (this.statisticsLock) {
            long elapsedTimeNs = currentTimeNs - this.statisticsStartTimeNs;
            if (elapsedTimeNs > 0) {
                float renderFps = ((float) (((long) this.framesRendered) * TimeUnit.SECONDS.toNanos(1))) / ((float) elapsedTimeNs);
                logD("Duration: " + TimeUnit.NANOSECONDS.toMillis(elapsedTimeNs) + " ms. Frames received: " + this.framesReceived + ". Dropped: " + this.framesDropped + ". Rendered: " + this.framesRendered + ". Render fps: " + String.format(Locale.US, "%.1f", new Object[]{Float.valueOf(renderFps)}) + ". Average render time: " + averageTimeAsString(this.renderTimeNs, this.framesRendered) + ". Average swapBuffer time: " + averageTimeAsString(this.renderSwapBufferTimeNs, this.framesRendered) + ".");
                resetStatistics(currentTimeNs);
            }
        }
    }

    /* access modifiers changed from: private */
    public void logD(String string) {
        Logging.d(TAG, this.name + string);
    }
}
