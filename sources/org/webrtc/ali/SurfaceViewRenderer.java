package org.webrtc.ali;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.concurrent.CountDownLatch;
import org.webrtc.ali.EglBase;
import org.webrtc.ali.EglRenderer;
import org.webrtc.ali.RendererCommon;
import org.webrtc.ali.VideoRenderer;

public class SurfaceViewRenderer extends SurfaceView implements SurfaceHolder.Callback, VideoRenderer.Callbacks {
    private static final String TAG = "SurfaceViewRenderer";
    private final EglRenderer eglRenderer;
    private boolean enableFixedSize;
    private int frameRotation;
    private boolean isFirstFrameRendered;
    private boolean isRenderingPaused = false;
    private final Object layoutLock = new Object();
    private RendererCommon.RendererEvents rendererEvents;
    private final String resourceName;
    private int rotatedFrameHeight;
    private int rotatedFrameWidth;
    private int surfaceHeight;
    private int surfaceWidth;
    private final RendererCommon.VideoLayoutMeasure videoLayoutMeasure = new RendererCommon.VideoLayoutMeasure();

    public SurfaceViewRenderer(Context context) {
        super(context);
        String resourceName2 = getResourceName();
        this.resourceName = resourceName2;
        this.eglRenderer = new EglRenderer(resourceName2);
        getHolder().addCallback(this);
    }

    public SurfaceViewRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        String resourceName2 = getResourceName();
        this.resourceName = resourceName2;
        this.eglRenderer = new EglRenderer(resourceName2);
        getHolder().addCallback(this);
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents2) {
        init(sharedContext, rendererEvents2, EglBase.CONFIG_PLAIN, new GlRectDrawer());
    }

    public void init(EglBase.Context sharedContext, RendererCommon.RendererEvents rendererEvents2, int[] configAttributes, RendererCommon.GlDrawer drawer) {
        ThreadUtils.checkIsOnMainThread();
        this.rendererEvents = rendererEvents2;
        synchronized (this.layoutLock) {
            this.rotatedFrameWidth = 0;
            this.rotatedFrameHeight = 0;
            this.frameRotation = 0;
        }
        this.eglRenderer.init(sharedContext, configAttributes, drawer);
    }

    public void release() {
        this.eglRenderer.release();
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
        this.eglRenderer.addFrameListener(listener, scale, drawerParam);
    }

    public void addFrameListener(EglRenderer.FrameListener listener, float scale) {
        this.eglRenderer.addFrameListener(listener, scale);
    }

    public void removeFrameListener(EglRenderer.FrameListener listener) {
        this.eglRenderer.removeFrameListener(listener);
    }

    public void setEnableHardwareScaler(boolean enabled) {
        ThreadUtils.checkIsOnMainThread();
        this.enableFixedSize = enabled;
        updateSurfaceSize();
    }

    public void setMirror(boolean mirror) {
        this.eglRenderer.setMirror(mirror);
    }

    public void setScalingType(RendererCommon.ScalingType scalingType) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingType);
    }

    public void setScalingType(RendererCommon.ScalingType scalingTypeMatchOrientation, RendererCommon.ScalingType scalingTypeMismatchOrientation) {
        ThreadUtils.checkIsOnMainThread();
        this.videoLayoutMeasure.setScalingType(scalingTypeMatchOrientation, scalingTypeMismatchOrientation);
    }

    public void setFpsReduction(float fps) {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = fps == 0.0f;
        }
        this.eglRenderer.setFpsReduction(fps);
    }

    public void disableFpsReduction() {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = false;
        }
        this.eglRenderer.disableFpsReduction();
    }

    public void pauseVideo() {
        synchronized (this.layoutLock) {
            this.isRenderingPaused = true;
        }
        this.eglRenderer.pauseVideo();
    }

    public void renderFrame(VideoRenderer.I420Frame frame) {
        updateFrameDimensionsAndReportEvents(frame);
        this.eglRenderer.renderFrame(frame);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthSpec, int heightSpec) {
        Point size;
        ThreadUtils.checkIsOnMainThread();
        synchronized (this.layoutLock) {
            size = this.videoLayoutMeasure.measure(widthSpec, heightSpec, this.rotatedFrameWidth, this.rotatedFrameHeight);
        }
        setMeasuredDimension(size.x, size.y);
        logD("onMeasure(). New size: " + size.x + "x" + size.y);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.setLayoutAspectRatio(((float) (right - left)) / ((float) (bottom - top)));
        updateSurfaceSize();
    }

    /* access modifiers changed from: private */
    public void updateSurfaceSize() {
        int drawnFrameHeight;
        int drawnFrameWidth;
        ThreadUtils.checkIsOnMainThread();
        synchronized (this.layoutLock) {
            if (!this.enableFixedSize || this.rotatedFrameWidth == 0 || this.rotatedFrameHeight == 0 || getWidth() == 0 || getHeight() == 0) {
                this.surfaceHeight = 0;
                this.surfaceWidth = 0;
                getHolder().setSizeFromLayout();
            } else {
                float layoutAspectRatio = ((float) getWidth()) / ((float) getHeight());
                if (((float) this.rotatedFrameWidth) / ((float) this.rotatedFrameHeight) > layoutAspectRatio) {
                    drawnFrameWidth = (int) (((float) this.rotatedFrameHeight) * layoutAspectRatio);
                    drawnFrameHeight = this.rotatedFrameHeight;
                } else {
                    drawnFrameWidth = this.rotatedFrameWidth;
                    drawnFrameHeight = (int) (((float) this.rotatedFrameWidth) / layoutAspectRatio);
                }
                int width = Math.min(getWidth(), drawnFrameWidth);
                int height = Math.min(getHeight(), drawnFrameHeight);
                logD("updateSurfaceSize. Layout size: " + getWidth() + "x" + getHeight() + ", frame size: " + this.rotatedFrameWidth + "x" + this.rotatedFrameHeight + ", requested surface size: " + width + "x" + height + ", old surface size: " + this.surfaceWidth + "x" + this.surfaceHeight);
                if (!(width == this.surfaceWidth && height == this.surfaceHeight)) {
                    this.surfaceWidth = width;
                    this.surfaceHeight = height;
                    getHolder().setFixedSize(width, height);
                }
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        ThreadUtils.checkIsOnMainThread();
        this.eglRenderer.createEglSurface(holder.getSurface());
        this.surfaceHeight = 0;
        this.surfaceWidth = 0;
        updateSurfaceSize();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        ThreadUtils.checkIsOnMainThread();
        final CountDownLatch completionLatch = new CountDownLatch(1);
        this.eglRenderer.releaseEglSurface(new Runnable() {
            public void run() {
                completionLatch.countDown();
            }
        });
        ThreadUtils.awaitUninterruptibly(completionLatch);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ThreadUtils.checkIsOnMainThread();
        logD("surfaceChanged: format: " + format + " size: " + width + "x" + height);
    }

    private String getResourceName() {
        try {
            return getResources().getResourceEntryName(getId()) + ": ";
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    public void clearImage() {
        this.eglRenderer.clearImage();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0086, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFrameDimensionsAndReportEvents(org.webrtc.ali.VideoRenderer.I420Frame r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.layoutLock
            monitor-enter(r0)
            boolean r1 = r5.isRenderingPaused     // Catch:{ all -> 0x0087 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return
        L_0x0009:
            boolean r1 = r5.isFirstFrameRendered     // Catch:{ all -> 0x0087 }
            if (r1 != 0) goto L_0x001e
            r1 = 1
            r5.isFirstFrameRendered = r1     // Catch:{ all -> 0x0087 }
            java.lang.String r1 = "Reporting first rendered frame."
            r5.logD(r1)     // Catch:{ all -> 0x0087 }
            org.webrtc.ali.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0087 }
            if (r1 == 0) goto L_0x001e
            org.webrtc.ali.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0087 }
            r1.onFirstFrameRendered()     // Catch:{ all -> 0x0087 }
        L_0x001e:
            int r1 = r5.rotatedFrameWidth     // Catch:{ all -> 0x0087 }
            int r2 = r6.rotatedWidth()     // Catch:{ all -> 0x0087 }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.rotatedFrameHeight     // Catch:{ all -> 0x0087 }
            int r2 = r6.rotatedHeight()     // Catch:{ all -> 0x0087 }
            if (r1 != r2) goto L_0x0034
            int r1 = r5.frameRotation     // Catch:{ all -> 0x0087 }
            int r2 = r6.rotationDegree     // Catch:{ all -> 0x0087 }
            if (r1 == r2) goto L_0x0085
        L_0x0034:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0087 }
            r1.<init>()     // Catch:{ all -> 0x0087 }
            java.lang.String r2 = "Reporting frame resolution changed to "
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            int r2 = r6.width     // Catch:{ all -> 0x0087 }
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            java.lang.String r2 = "x"
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            int r2 = r6.height     // Catch:{ all -> 0x0087 }
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            java.lang.String r2 = " with rotation "
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            int r2 = r6.rotationDegree     // Catch:{ all -> 0x0087 }
            r1.append(r2)     // Catch:{ all -> 0x0087 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0087 }
            r5.logD(r1)     // Catch:{ all -> 0x0087 }
            org.webrtc.ali.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0087 }
            if (r1 == 0) goto L_0x006d
            org.webrtc.ali.RendererCommon$RendererEvents r1 = r5.rendererEvents     // Catch:{ all -> 0x0087 }
            int r2 = r6.width     // Catch:{ all -> 0x0087 }
            int r3 = r6.height     // Catch:{ all -> 0x0087 }
            int r4 = r6.rotationDegree     // Catch:{ all -> 0x0087 }
            r1.onFrameResolutionChanged(r2, r3, r4)     // Catch:{ all -> 0x0087 }
        L_0x006d:
            int r1 = r6.rotatedWidth()     // Catch:{ all -> 0x0087 }
            r5.rotatedFrameWidth = r1     // Catch:{ all -> 0x0087 }
            int r1 = r6.rotatedHeight()     // Catch:{ all -> 0x0087 }
            r5.rotatedFrameHeight = r1     // Catch:{ all -> 0x0087 }
            int r1 = r6.rotationDegree     // Catch:{ all -> 0x0087 }
            r5.frameRotation = r1     // Catch:{ all -> 0x0087 }
            org.webrtc.ali.SurfaceViewRenderer$2 r1 = new org.webrtc.ali.SurfaceViewRenderer$2     // Catch:{ all -> 0x0087 }
            r1.<init>()     // Catch:{ all -> 0x0087 }
            r5.post(r1)     // Catch:{ all -> 0x0087 }
        L_0x0085:
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return
        L_0x0087:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.ali.SurfaceViewRenderer.updateFrameDimensionsAndReportEvents(org.webrtc.ali.VideoRenderer$I420Frame):void");
    }

    private void logD(String string) {
        Logging.d(TAG, this.resourceName + string);
    }
}
