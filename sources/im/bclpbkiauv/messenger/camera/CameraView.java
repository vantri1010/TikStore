package im.bclpbkiauv.messenger.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private CameraSession cameraSession;
    private int clipBottom;
    private int clipTop;
    private int cx;
    private int cy;
    private CameraViewDelegate delegate;
    private int focusAreaSize;
    private float focusProgress = 1.0f;
    private boolean initialFrontface;
    private boolean initied;
    private float innerAlpha;
    private Paint innerPaint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isFrontface;
    private long lastDrawTime;
    private Matrix matrix = new Matrix();
    private boolean mirror;
    private float outerAlpha;
    private Paint outerPaint = new Paint(1);
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform = new Matrix();

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    public CameraView(Context context, boolean frontface) {
        super(context, (AttributeSet) null);
        this.isFrontface = frontface;
        this.initialFrontface = frontface;
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        textureView2.setSurfaceTextureListener(this);
        addView(this.textureView);
        this.focusAreaSize = AndroidUtilities.dp(96.0f);
        this.outerPaint.setColor(-1);
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.innerPaint.setColor(Integer.MAX_VALUE);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    public void setMirror(boolean value) {
        this.mirror = value;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (cameraInfos.get(a).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
            this.cameraSession = null;
        }
        this.initied = false;
        this.isFrontface = !this.isFrontface;
        initCamera();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        r1 = r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initCamera() {
        /*
            r17 = this;
            r0 = r17
            r1 = 0
            im.bclpbkiauv.messenger.camera.CameraController r2 = im.bclpbkiauv.messenger.camera.CameraController.getInstance()
            java.util.ArrayList r2 = r2.getCameras()
            if (r2 != 0) goto L_0x000e
            return
        L_0x000e:
            r3 = 0
        L_0x000f:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x0030
            java.lang.Object r4 = r2.get(r3)
            im.bclpbkiauv.messenger.camera.CameraInfo r4 = (im.bclpbkiauv.messenger.camera.CameraInfo) r4
            boolean r5 = r0.isFrontface
            if (r5 == 0) goto L_0x0023
            int r5 = r4.frontCamera
            if (r5 != 0) goto L_0x002b
        L_0x0023:
            boolean r5 = r0.isFrontface
            if (r5 != 0) goto L_0x002d
            int r5 = r4.frontCamera
            if (r5 != 0) goto L_0x002d
        L_0x002b:
            r1 = r4
            goto L_0x0030
        L_0x002d:
            int r3 = r3 + 1
            goto L_0x000f
        L_0x0030:
            if (r1 != 0) goto L_0x0033
            return
        L_0x0033:
            r3 = 1068149419(0x3faaaaab, float:1.3333334)
            r4 = 1071877689(0x3fe38e39, float:1.7777778)
            android.graphics.Point r5 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r5 = r5.x
            android.graphics.Point r6 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r6 = r6.y
            int r5 = java.lang.Math.max(r5, r6)
            float r5 = (float) r5
            android.graphics.Point r6 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r6 = r6.x
            android.graphics.Point r7 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r7 = r7.y
            int r6 = java.lang.Math.min(r6, r7)
            float r6 = (float) r6
            float r5 = r5 / r6
            boolean r6 = r0.initialFrontface
            r7 = 3
            r8 = 4
            r9 = 1036831949(0x3dcccccd, float:0.1)
            r10 = 9
            r11 = 16
            if (r6 == 0) goto L_0x006b
            im.bclpbkiauv.messenger.camera.Size r6 = new im.bclpbkiauv.messenger.camera.Size
            r6.<init>(r11, r10)
            r12 = 480(0x1e0, float:6.73E-43)
            r13 = 270(0x10e, float:3.78E-43)
            goto L_0x0088
        L_0x006b:
            float r6 = r5 - r3
            float r6 = java.lang.Math.abs(r6)
            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r6 >= 0) goto L_0x007f
            im.bclpbkiauv.messenger.camera.Size r6 = new im.bclpbkiauv.messenger.camera.Size
            r6.<init>(r8, r7)
            r12 = 1280(0x500, float:1.794E-42)
            r13 = 960(0x3c0, float:1.345E-42)
            goto L_0x0088
        L_0x007f:
            im.bclpbkiauv.messenger.camera.Size r6 = new im.bclpbkiauv.messenger.camera.Size
            r6.<init>(r11, r10)
            r12 = 1280(0x500, float:1.794E-42)
            r13 = 720(0x2d0, float:1.009E-42)
        L_0x0088:
            android.view.TextureView r14 = r0.textureView
            int r14 = r14.getWidth()
            if (r14 <= 0) goto L_0x00ba
            android.view.TextureView r14 = r0.textureView
            int r14 = r14.getHeight()
            if (r14 <= 0) goto L_0x00ba
            android.graphics.Point r14 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r14 = r14.x
            android.graphics.Point r15 = im.bclpbkiauv.messenger.AndroidUtilities.displaySize
            int r15 = r15.y
            int r14 = java.lang.Math.min(r14, r15)
            int r15 = r6.getHeight()
            int r15 = r15 * r14
            int r16 = r6.getWidth()
            int r15 = r15 / r16
            java.util.ArrayList r10 = r1.getPreviewSizes()
            im.bclpbkiauv.messenger.camera.Size r10 = im.bclpbkiauv.messenger.camera.CameraController.chooseOptimalSize(r10, r14, r15, r6)
            r0.previewSize = r10
        L_0x00ba:
            java.util.ArrayList r10 = r1.getPictureSizes()
            im.bclpbkiauv.messenger.camera.Size r10 = im.bclpbkiauv.messenger.camera.CameraController.chooseOptimalSize(r10, r12, r13, r6)
            int r14 = r10.getWidth()
            r15 = 1280(0x500, float:1.794E-42)
            if (r14 < r15) goto L_0x00fe
            int r14 = r10.getHeight()
            if (r14 < r15) goto L_0x00fe
            float r14 = r5 - r3
            float r14 = java.lang.Math.abs(r14)
            int r9 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x00e1
            im.bclpbkiauv.messenger.camera.Size r9 = new im.bclpbkiauv.messenger.camera.Size
            r9.<init>(r7, r8)
            r6 = r9
            goto L_0x00e9
        L_0x00e1:
            im.bclpbkiauv.messenger.camera.Size r7 = new im.bclpbkiauv.messenger.camera.Size
            r8 = 9
            r7.<init>(r8, r11)
            r6 = r7
        L_0x00e9:
            java.util.ArrayList r7 = r1.getPictureSizes()
            im.bclpbkiauv.messenger.camera.Size r7 = im.bclpbkiauv.messenger.camera.CameraController.chooseOptimalSize(r7, r13, r12, r6)
            int r8 = r7.getWidth()
            if (r8 < r15) goto L_0x00fd
            int r8 = r7.getHeight()
            if (r8 >= r15) goto L_0x00fe
        L_0x00fd:
            r10 = r7
        L_0x00fe:
            android.view.TextureView r7 = r0.textureView
            android.graphics.SurfaceTexture r7 = r7.getSurfaceTexture()
            im.bclpbkiauv.messenger.camera.Size r8 = r0.previewSize
            if (r8 == 0) goto L_0x0135
            if (r7 == 0) goto L_0x0135
            int r8 = r8.getWidth()
            im.bclpbkiauv.messenger.camera.Size r9 = r0.previewSize
            int r9 = r9.getHeight()
            r7.setDefaultBufferSize(r8, r9)
            im.bclpbkiauv.messenger.camera.CameraSession r8 = new im.bclpbkiauv.messenger.camera.CameraSession
            im.bclpbkiauv.messenger.camera.Size r9 = r0.previewSize
            r11 = 256(0x100, float:3.59E-43)
            r8.<init>(r1, r9, r10, r11)
            r0.cameraSession = r8
            im.bclpbkiauv.messenger.camera.CameraController r8 = im.bclpbkiauv.messenger.camera.CameraController.getInstance()
            im.bclpbkiauv.messenger.camera.CameraSession r9 = r0.cameraSession
            im.bclpbkiauv.messenger.camera.-$$Lambda$CameraView$JlUdUkYdMsTdduDGv5tem1IIVGk r11 = new im.bclpbkiauv.messenger.camera.-$$Lambda$CameraView$JlUdUkYdMsTdduDGv5tem1IIVGk
            r11.<init>()
            im.bclpbkiauv.messenger.camera.-$$Lambda$CameraView$4kfiZBFlUmNT2NMOifHie58-woA r14 = new im.bclpbkiauv.messenger.camera.-$$Lambda$CameraView$4kfiZBFlUmNT2NMOifHie58-woA
            r14.<init>()
            r8.open(r9, r7, r11, r14)
        L_0x0135:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.camera.CameraView.initCamera():void");
    }

    public /* synthetic */ void lambda$initCamera$0$CameraView() {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setInitied();
        }
        checkPreviewMatrix();
    }

    public /* synthetic */ void lambda$initCamera$1$CameraView() {
        CameraViewDelegate cameraViewDelegate = this.delegate;
        if (cameraViewDelegate != null) {
            cameraViewDelegate.onCameraCreated(this.cameraSession.cameraInfo.camera);
        }
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        initCamera();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession == null) {
            return false;
        }
        CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        CameraSession cameraSession2;
        if (!this.initied && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied()) {
            CameraViewDelegate cameraViewDelegate = this.delegate;
            if (cameraViewDelegate != null) {
                cameraViewDelegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int value) {
        this.clipTop = value;
    }

    public void setClipBottom(int value) {
        this.clipBottom = value;
    }

    private void checkPreviewMatrix() {
        Size size = this.previewSize;
        if (size != null) {
            adjustAspectRatio(size.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int previewWidth, int previewHeight, int rotation) {
        float scale;
        int i = previewWidth;
        int i2 = previewHeight;
        int i3 = rotation;
        this.txform.reset();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float viewCenterX = (float) (viewWidth / 2);
        float viewCenterY = (float) (viewHeight / 2);
        if (i3 == 0 || i3 == 2) {
            scale = Math.max(((float) ((this.clipTop + viewHeight) + this.clipBottom)) / ((float) i), ((float) viewWidth) / ((float) i2));
        } else {
            scale = Math.max(((float) ((this.clipTop + viewHeight) + this.clipBottom)) / ((float) i2), ((float) viewWidth) / ((float) i));
        }
        this.txform.postScale((((float) i2) * scale) / ((float) viewWidth), (((float) i) * scale) / ((float) viewHeight), viewCenterX, viewCenterY);
        if (1 == i3 || 3 == i3) {
            this.txform.postRotate((float) ((i3 - 2) * 90), viewCenterX, viewCenterY);
        } else if (2 == i3) {
            this.txform.postRotate(180.0f, viewCenterX, viewCenterY);
        }
        if (this.mirror) {
            this.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
        }
        int i4 = this.clipTop;
        if (i4 != 0) {
            this.txform.postTranslate(0.0f, (float) ((-i4) / 2));
        } else {
            int i5 = this.clipBottom;
            if (i5 != 0) {
                this.txform.postTranslate(0.0f, (float) (i5 / 2));
            }
        }
        this.textureView.setTransform(this.txform);
        Matrix matrix2 = new Matrix();
        matrix2.postRotate((float) this.cameraSession.getDisplayOrientation());
        matrix2.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
        matrix2.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
        matrix2.invert(this.matrix);
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(((float) this.focusAreaSize) * coefficient).intValue();
        int left = clamp(((int) x) - (areaSize / 2), 0, getWidth() - areaSize);
        int top = clamp(((int) y) - (areaSize / 2), 0, getHeight() - areaSize);
        RectF rectF = new RectF((float) left, (float) top, (float) (left + areaSize), (float) (top + areaSize));
        this.matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void focusToPoint(int x, int y) {
        Rect focusRect = calculateTapArea((float) x, (float) y, 1.0f);
        Rect meteringRect = calculateTapArea((float) x, (float) y, 1.5f);
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.focusToRect(focusRect, meteringRect);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = x;
        this.cy = y;
        this.lastDrawTime = System.currentTimeMillis();
        invalidate();
    }

    public void setZoom(float value) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setZoom(value);
        }
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        this.delegate = cameraViewDelegate;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public CameraSession getCameraSession() {
        return this.cameraSession;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    public Matrix getMatrix() {
        return this.txform;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (!(this.focusProgress == 1.0f && this.innerAlpha == 0.0f && this.outerAlpha == 0.0f)) {
            int baseRad = AndroidUtilities.dp(30.0f);
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastDrawTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            this.lastDrawTime = newTime;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolated = this.interpolator.getInterpolation(this.focusProgress);
            canvas2.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) + (((float) baseRad) * (1.0f - interpolated)), this.outerPaint);
            canvas2.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) * interpolated, this.innerPaint);
            float f = this.focusProgress;
            if (f < 1.0f) {
                float f2 = f + (((float) dt) / 200.0f);
                this.focusProgress = f2;
                if (f2 > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else {
                float f3 = this.innerAlpha;
                if (f3 != 0.0f) {
                    float f4 = f3 - (((float) dt) / 150.0f);
                    this.innerAlpha = f4;
                    if (f4 < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    float f5 = this.outerAlpha;
                    if (f5 != 0.0f) {
                        float f6 = f5 - (((float) dt) / 150.0f);
                        this.outerAlpha = f6;
                        if (f6 < 0.0f) {
                            this.outerAlpha = 0.0f;
                        }
                        invalidate();
                    }
                }
            }
        }
        return result;
    }
}
