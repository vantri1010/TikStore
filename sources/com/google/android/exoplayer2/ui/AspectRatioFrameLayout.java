package com.google.android.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AspectRatioFrameLayout extends FrameLayout {
    private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01f;
    public static final int RESIZE_MODE_FILL = 3;
    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
    public static final int RESIZE_MODE_FIXED_WIDTH = 1;
    public static final int RESIZE_MODE_ZOOM = 4;
    /* access modifiers changed from: private */
    public AspectRatioListener aspectRatioListener;
    private final AspectRatioUpdateDispatcher aspectRatioUpdateDispatcher = new AspectRatioUpdateDispatcher();
    private boolean drawingReady;
    private Matrix matrix = new Matrix();
    private int resizeMode = 0;
    private int rotation;
    private float videoAspectRatio;

    public interface AspectRatioListener {
        void onAspectRatioUpdated(float f, float f2, boolean z);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResizeMode {
    }

    public AspectRatioFrameLayout(Context context) {
        super(context);
    }

    public void setAspectRatio(float widthHeightRatio, int rotation2) {
        if (this.videoAspectRatio != widthHeightRatio) {
            this.videoAspectRatio = widthHeightRatio;
            this.rotation = rotation2;
            requestLayout();
        }
    }

    public void setAspectRatioListener(AspectRatioListener listener) {
        this.aspectRatioListener = listener;
    }

    public int getResizeMode() {
        return this.resizeMode;
    }

    public void setResizeMode(int resizeMode2) {
        if (this.resizeMode != resizeMode2) {
            this.resizeMode = resizeMode2;
            requestLayout();
        }
    }

    public void setDrawingReady(boolean value) {
        if (this.drawingReady != value) {
            this.drawingReady = value;
        }
    }

    public float getAspectRatio() {
        return this.videoAspectRatio;
    }

    public int getVideoRotation() {
        return this.rotation;
    }

    public boolean isDrawingReady() {
        return this.drawingReady;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.videoAspectRatio > 0.0f) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            float viewAspectRatio = ((float) width) / ((float) height);
            float aspectDeformation = (this.videoAspectRatio / viewAspectRatio) - 1.0f;
            if (Math.abs(aspectDeformation) <= MAX_ASPECT_RATIO_DEFORMATION_FRACTION) {
                this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, false);
                return;
            }
            int i = this.resizeMode;
            if (i != 0) {
                if (i == 1) {
                    height = (int) (((float) width) / this.videoAspectRatio);
                } else if (i == 2) {
                    width = (int) (((float) height) * this.videoAspectRatio);
                } else if (i != 3) {
                    if (i == 4) {
                        if (aspectDeformation > 0.0f) {
                            width = (int) (((float) height) * this.videoAspectRatio);
                        } else {
                            height = (int) (((float) width) / this.videoAspectRatio);
                        }
                    }
                } else if (aspectDeformation <= 0.0f) {
                    height = (int) (((float) width) / this.videoAspectRatio);
                } else {
                    width = (int) (((float) height) * this.videoAspectRatio);
                }
            } else if (aspectDeformation > 0.0f) {
                height = (int) (((float) width) / this.videoAspectRatio);
            } else {
                width = (int) (((float) height) * this.videoAspectRatio);
            }
            this.aspectRatioUpdateDispatcher.scheduleUpdate(this.videoAspectRatio, viewAspectRatio, true);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
            int count = getChildCount();
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof TextureView) {
                    this.matrix.reset();
                    int px = getWidth() / 2;
                    int py = getHeight() / 2;
                    this.matrix.postRotate((float) this.rotation, (float) px, (float) py);
                    int i2 = this.rotation;
                    if (i2 == 90 || i2 == 270) {
                        float ratio = ((float) getHeight()) / ((float) getWidth());
                        this.matrix.postScale(1.0f / ratio, ratio, (float) px, (float) py);
                    }
                    ((TextureView) child).setTransform(this.matrix);
                    return;
                }
            }
        }
    }

    private final class AspectRatioUpdateDispatcher implements Runnable {
        private boolean aspectRatioMismatch;
        private boolean isScheduled;
        private float naturalAspectRatio;
        private float targetAspectRatio;

        private AspectRatioUpdateDispatcher() {
        }

        public void scheduleUpdate(float targetAspectRatio2, float naturalAspectRatio2, boolean aspectRatioMismatch2) {
            this.targetAspectRatio = targetAspectRatio2;
            this.naturalAspectRatio = naturalAspectRatio2;
            this.aspectRatioMismatch = aspectRatioMismatch2;
            if (!this.isScheduled) {
                this.isScheduled = true;
                AspectRatioFrameLayout.this.post(this);
            }
        }

        public void run() {
            this.isScheduled = false;
            if (AspectRatioFrameLayout.this.aspectRatioListener != null) {
                AspectRatioFrameLayout.this.aspectRatioListener.onAspectRatioUpdated(this.targetAspectRatio, this.naturalAspectRatio, this.aspectRatioMismatch);
            }
        }
    }
}
