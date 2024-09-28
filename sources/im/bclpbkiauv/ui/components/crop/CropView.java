package im.bclpbkiauv.ui.components.crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.components.crop.CropAreaView;
import im.bclpbkiauv.ui.components.crop.CropGestureDetector;

public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    private static final float EPSILON = 1.0E-5f;
    private static final float MAX_SCALE = 30.0f;
    private static final int RESULT_SIDE = 1280;
    /* access modifiers changed from: private */
    public boolean animating = false;
    /* access modifiers changed from: private */
    public CropAreaView areaView;
    private View backView;
    private Bitmap bitmap;
    private float bottomPadding;
    private CropGestureDetector detector;
    /* access modifiers changed from: private */
    public boolean freeform;
    private boolean hasAspectRatioDialog;
    /* access modifiers changed from: private */
    public ImageView imageView;
    private RectF initialAreaRect = new RectF();
    private CropViewListener listener;
    private Matrix presentationMatrix = new Matrix();
    private RectF previousAreaRect = new RectF();
    private float rotationStartScale;
    private CropState state;
    private Matrix tempMatrix = new Matrix();
    private CropRectangle tempRect = new CropRectangle();

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);
    }

    private class CropState {
        private float baseRotation;
        private float height;
        private Matrix matrix;
        private float minimumScale;
        private float orientation;
        private float rotation;
        private float scale;
        private float width;
        private float x;
        private float y;

        private CropState(Bitmap bitmap, int bRotation) {
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) bRotation;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        /* access modifiers changed from: private */
        public void updateBitmap(Bitmap bitmap, int rotation2) {
            this.scale *= this.width / ((float) bitmap.getWidth());
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            updateMinimumScale();
            float[] values = new float[9];
            this.matrix.getValues(values);
            this.matrix.reset();
            Matrix matrix2 = this.matrix;
            float f = this.scale;
            matrix2.postScale(f, f);
            this.matrix.postTranslate(values[2], values[5]);
            CropView.this.updateMatrix();
        }

        /* access modifiers changed from: private */
        public boolean hasChanges() {
            return Math.abs(this.x) > CropView.EPSILON || Math.abs(this.y) > CropView.EPSILON || Math.abs(this.scale - this.minimumScale) > CropView.EPSILON || Math.abs(this.rotation) > CropView.EPSILON || Math.abs(this.orientation) > CropView.EPSILON;
        }

        /* access modifiers changed from: private */
        public float getWidth() {
            return this.width;
        }

        /* access modifiers changed from: private */
        public float getHeight() {
            return this.height;
        }

        /* access modifiers changed from: private */
        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        /* access modifiers changed from: private */
        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        /* access modifiers changed from: private */
        public void translate(float x2, float y2) {
            this.x += x2;
            this.y += y2;
            this.matrix.postTranslate(x2, y2);
        }

        /* access modifiers changed from: private */
        public float getX() {
            return this.x;
        }

        /* access modifiers changed from: private */
        public float getY() {
            return this.y;
        }

        /* access modifiers changed from: private */
        public void scale(float s, float pivotX, float pivotY) {
            this.scale *= s;
            this.matrix.postScale(s, s, pivotX, pivotY);
        }

        /* access modifiers changed from: private */
        public float getScale() {
            return this.scale;
        }

        private float getMinimumScale() {
            return this.minimumScale;
        }

        /* access modifiers changed from: private */
        public void rotate(float angle, float pivotX, float pivotY) {
            this.rotation += angle;
            this.matrix.postRotate(angle, pivotX, pivotY);
        }

        /* access modifiers changed from: private */
        public float getRotation() {
            return this.rotation;
        }

        /* access modifiers changed from: private */
        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        /* access modifiers changed from: private */
        public float getBaseRotation() {
            return this.baseRotation;
        }

        /* access modifiers changed from: private */
        public void reset(CropAreaView areaView, float orient, boolean freeform) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = orient;
            updateMinimumScale();
            float f = this.minimumScale;
            this.scale = f;
            this.matrix.postScale(f, f);
        }

        private void updateMinimumScale() {
            float w = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
            float h = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / w;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / w, CropView.this.areaView.getCropHeight() / h);
            }
        }

        /* access modifiers changed from: private */
        public void getConcatMatrix(Matrix toMatrix) {
            toMatrix.postConcat(this.matrix);
        }

        /* access modifiers changed from: private */
        public Matrix getMatrix() {
            Matrix m = new Matrix();
            m.set(this.matrix);
            return m;
        }
    }

    public CropView(Context context) {
        super(context);
        View view = new View(context);
        this.backView = view;
        view.setBackgroundColor(-16777216);
        this.backView.setVisibility(4);
        addView(this.backView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setDrawingCacheEnabled(true);
        this.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    public CropView(Context context, boolean isFcCrop) {
        super(context);
        View view = new View(context);
        this.backView = view;
        view.setBackgroundColor(-16777216);
        this.backView.setVisibility(4);
        addView(this.backView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setDrawingCacheEnabled(true);
        this.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        if (isFcCrop) {
            this.areaView = new CropAreaView(context, true);
        } else {
            this.areaView = new CropAreaView(context);
        }
        this.areaView.setListener(this);
        addView(this.areaView);
    }

    public boolean isReady() {
        return !this.detector.isScaling() && !this.detector.isDragging() && !this.areaView.isDragging();
    }

    public void setListener(CropViewListener l) {
        this.listener = l;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
        this.areaView.setBottomPadding(value);
    }

    public void setAspectRatio(float ratio) {
        this.areaView.setActualRect(ratio);
    }

    public void setBitmap(Bitmap b, int rotation, boolean fform, boolean same) {
        this.freeform = fform;
        if (b == null) {
            this.bitmap = null;
            this.state = null;
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.bitmap = b;
        CropState cropState = this.state;
        if (cropState == null || !same) {
            this.state = new CropState(this.bitmap, rotation);
            this.imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    CropView.this.reset();
                    CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState.updateBitmap(b, rotation);
        }
        this.imageView.setImageBitmap(this.bitmap);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void hideBackView() {
        this.backView.setVisibility(4);
    }

    public void showBackView() {
        this.backView.setVisibility(0);
    }

    public void setFreeform(boolean fform) {
        this.areaView.setFreeform(fform);
        this.freeform = fform;
    }

    public void show() {
        this.backView.setVisibility(0);
        this.imageView.setVisibility(0);
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.backView.setVisibility(4);
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false);
        this.areaView.invalidate();
    }

    public void reset() {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(this.bitmap, this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix();
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        this.presentationMatrix.reset();
        this.presentationMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        this.presentationMatrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(this.presentationMatrix);
        this.presentationMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        this.imageView.setImageMatrix(this.presentationMatrix);
    }

    private void fillAreaView(RectF targetRect, boolean allowZoomOut) {
        boolean ensureFit;
        float scale;
        RectF rectF = targetRect;
        int i = 0;
        float[] currentScale = {1.0f};
        float scale2 = Math.max(targetRect.width() / this.areaView.getCropWidth(), targetRect.height() / this.areaView.getCropHeight());
        float newScale = this.state.getScale() * scale2;
        if (newScale > 30.0f) {
            scale = 30.0f / this.state.getScale();
            ensureFit = true;
        } else {
            scale = scale2;
            ensureFit = false;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        float x = ((targetRect.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth();
        float y = ((targetRect.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + ((float) i)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        final boolean animEnsureFit = ensureFit;
        $$Lambda$CropView$Q7RRR8xykYILKf4H5S613uGYGY r8 = r0;
        float f = newScale;
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        $$Lambda$CropView$Q7RRR8xykYILKf4H5S613uGYGY r0 = new ValueAnimator.AnimatorUpdateListener(scale, currentScale, x, y) {
            private final /* synthetic */ float f$1;
            private final /* synthetic */ float[] f$2;
            private final /* synthetic */ float f$3;
            private final /* synthetic */ float f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CropView.this.lambda$fillAreaView$0$CropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
            }
        };
        animator.addUpdateListener(r8);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animEnsureFit) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, animator, true);
        this.initialAreaRect.set(rectF);
    }

    public /* synthetic */ void lambda$fillAreaView$0$CropView(float targetScale, float[] currentScale, float x, float y, ValueAnimator animation) {
        float deltaScale = (((targetScale - 1.0f) * ((Float) animation.getAnimatedValue()).floatValue()) + 1.0f) / currentScale[0];
        currentScale[0] = currentScale[0] * deltaScale;
        this.state.scale(deltaScale, x, y);
        updateMatrix();
    }

    private float fitScale(RectF contentRect, float scale, float ratio) {
        float scaledW = contentRect.width() * ratio;
        float scaledH = contentRect.height() * ratio;
        float scaledX = (contentRect.width() - scaledW) / 2.0f;
        float scaledY = (contentRect.height() - scaledH) / 2.0f;
        contentRect.set(contentRect.left + scaledX, contentRect.top + scaledY, contentRect.left + scaledX + scaledW, contentRect.top + scaledY + scaledH);
        return scale * ratio;
    }

    private void fitTranslation(RectF contentRect, RectF boundsRect, PointF translation, float radians) {
        RectF rectF = contentRect;
        RectF rectF2 = boundsRect;
        PointF pointF = translation;
        float f = radians;
        float frameLeft = rectF2.left;
        float frameTop = rectF2.top;
        float frameRight = rectF2.right;
        float frameBottom = rectF2.bottom;
        if (rectF.left > frameLeft) {
            frameRight += rectF.left - frameLeft;
            frameLeft = rectF.left;
        }
        if (rectF.top > frameTop) {
            frameBottom += rectF.top - frameTop;
            frameTop = rectF.top;
        }
        if (rectF.right < frameRight) {
            frameLeft += rectF.right - frameRight;
        }
        if (rectF.bottom < frameBottom) {
            frameTop += rectF.bottom - frameBottom;
        }
        float deltaX = boundsRect.centerX() - ((boundsRect.width() / 2.0f) + frameLeft);
        float deltaY = boundsRect.centerY() - ((boundsRect.height() / 2.0f) + frameTop);
        float xCompY = (float) (Math.cos(1.5707963267948966d - ((double) f)) * ((double) deltaX));
        float yCompY = (float) (Math.sin(((double) f) + 1.5707963267948966d) * ((double) deltaY));
        pointF.set(pointF.x + ((float) (Math.sin(1.5707963267948966d - ((double) f)) * ((double) deltaX))) + ((float) (Math.cos(((double) f) + 1.5707963267948966d) * ((double) deltaY))), pointF.y + xCompY + yCompY);
    }

    public RectF calculateBoundingBox(float w, float h, float rotation) {
        RectF result = new RectF(0.0f, 0.0f, w, h);
        Matrix m = new Matrix();
        m.postRotate(rotation, w / 2.0f, h / 2.0f);
        m.mapRect(result);
        return result;
    }

    public float scaleWidthToMaxSize(RectF sizeRect, RectF maxSizeRect) {
        float w = maxSizeRect.width();
        if (((float) Math.floor((double) ((sizeRect.height() * w) / sizeRect.width()))) <= maxSizeRect.height()) {
            return w;
        }
        return (float) Math.floor((double) ((sizeRect.width() * maxSizeRect.height()) / sizeRect.height()));
    }

    private class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        /* access modifiers changed from: package-private */
        public void setRect(RectF rect) {
            this.coords[0] = rect.left;
            this.coords[1] = rect.top;
            this.coords[2] = rect.right;
            this.coords[3] = rect.top;
            this.coords[4] = rect.right;
            this.coords[5] = rect.bottom;
            this.coords[6] = rect.left;
            this.coords[7] = rect.bottom;
        }

        /* access modifiers changed from: package-private */
        public void applyMatrix(Matrix m) {
            m.mapPoints(this.coords);
        }

        /* access modifiers changed from: package-private */
        public void getRect(RectF rect) {
            float[] fArr = this.coords;
            rect.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated) {
        fitContentInBounds(allowScale, maximize, animated, false);
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated, boolean fast) {
        float targetScale;
        if (this.state != null) {
            float boundsW = this.areaView.getCropWidth();
            float boundsH = this.areaView.getCropHeight();
            float contentW = this.state.getOrientedWidth();
            float contentH = this.state.getOrientedHeight();
            float rotation = this.state.getRotation();
            float radians = (float) Math.toRadians((double) rotation);
            RectF boundsRect = calculateBoundingBox(boundsW, boundsH, rotation);
            RectF contentRect = new RectF(0.0f, 0.0f, contentW, contentH);
            float scale = this.state.getScale();
            this.tempRect.setRect(contentRect);
            Matrix matrix = this.state.getMatrix();
            matrix.preTranslate(((boundsW - contentW) / 2.0f) / scale, ((boundsH - contentH) / 2.0f) / scale);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(contentRect.centerX(), contentRect.centerY());
            Matrix matrix2 = this.tempMatrix;
            matrix2.setConcat(matrix2, matrix);
            this.tempMatrix.preTranslate(-contentRect.centerX(), -contentRect.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-rotation, contentW / 2.0f, contentH / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(contentRect);
            PointF targetTranslation = new PointF(this.state.getX(), this.state.getY());
            float targetScale2 = scale;
            if (!contentRect.contains(boundsRect)) {
                if (allowScale && (boundsRect.width() > contentRect.width() || boundsRect.height() > contentRect.height())) {
                    targetScale2 = fitScale(contentRect, scale, boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect));
                }
                fitTranslation(contentRect, boundsRect, targetTranslation, radians);
                targetScale = targetScale2;
            } else if (!maximize || this.rotationStartScale <= 0.0f) {
                targetScale = targetScale2;
            } else {
                float ratio = boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect);
                if (this.state.getScale() * ratio < this.rotationStartScale) {
                    ratio = 1.0f;
                }
                float targetScale3 = fitScale(contentRect, scale, ratio);
                fitTranslation(contentRect, boundsRect, targetTranslation, radians);
                targetScale = targetScale3;
            }
            float dx = targetTranslation.x - this.state.getX();
            float dy = targetTranslation.y - this.state.getY();
            if (animated) {
                float animScale = targetScale / scale;
                float animDX = dx;
                float animDY = dy;
                if (Math.abs(animScale - 1.0f) >= EPSILON || Math.abs(animDX) >= EPSILON || Math.abs(animDY) >= EPSILON) {
                    PointF pointF = targetTranslation;
                    this.animating = true;
                    float f = boundsW;
                    float boundsW2 = dy;
                    float f2 = boundsH;
                    ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    float f3 = contentW;
                    float contentW2 = dx;
                    Matrix matrix3 = matrix;
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(animDX, new float[]{1.0f, 0.0f, 0.0f}, animDY, animScale) {
                        private final /* synthetic */ float f$1;
                        private final /* synthetic */ float[] f$2;
                        private final /* synthetic */ float f$3;
                        private final /* synthetic */ float f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            CropView.this.lambda$fitContentInBounds$1$CropView(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                        }
                    });
                    float f4 = scale;
                    RectF rectF = contentRect;
                    final boolean z = fast;
                    RectF rectF2 = boundsRect;
                    final boolean z2 = allowScale;
                    float f5 = radians;
                    final boolean z3 = maximize;
                    float f6 = rotation;
                    final boolean z4 = animated;
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            boolean unused = CropView.this.animating = false;
                            if (!z) {
                                CropView.this.fitContentInBounds(z2, z3, z4, true);
                            }
                        }
                    });
                    animator.setInterpolator(this.areaView.getInterpolator());
                    animator.setDuration(fast ? 100 : 200);
                    animator.start();
                    return;
                }
                return;
            }
            Matrix matrix4 = matrix;
            RectF rectF3 = contentRect;
            RectF rectF4 = boundsRect;
            float f7 = radians;
            float f8 = rotation;
            float f9 = boundsW;
            float f10 = boundsH;
            float f11 = contentW;
            this.state.translate(dx, dy);
            this.state.scale(targetScale / scale, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public /* synthetic */ void lambda$fitContentInBounds$1$CropView(float animDX, float[] currentValues, float animDY, float animScale, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        float deltaX = (animDX * value) - currentValues[1];
        currentValues[1] = currentValues[1] + deltaX;
        float deltaY = (animDY * value) - currentValues[2];
        currentValues[2] = currentValues[2] + deltaY;
        this.state.translate(currentValues[0] * deltaX, currentValues[0] * deltaY);
        float deltaScale = (((animScale - 1.0f) * value) + 1.0f) / currentValues[0];
        currentValues[0] = currentValues[0] * deltaScale;
        this.state.scale(deltaScale, 0.0f, 0.0f);
        updateMatrix();
    }

    public void rotate90Degrees() {
        if (this.state != null) {
            this.areaView.resetAnimator();
            resetRotationStartScale();
            float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
            boolean fform = this.freeform;
            boolean z = true;
            if (!this.freeform || this.areaView.getLockAspectRatio() <= 0.0f) {
                this.areaView.setBitmap(this.bitmap, (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
            } else {
                CropAreaView cropAreaView = this.areaView;
                cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
                CropAreaView cropAreaView2 = this.areaView;
                cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
                fform = false;
            }
            this.state.reset(this.areaView, orientation, fform);
            updateMatrix();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                if (!(orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f)) {
                    z = false;
                }
                cropViewListener.onChange(z);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.animating || this.areaView.onTouchEvent(event)) {
            return true;
        }
        int action = event.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            return this.detector.onTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        this.state.translate(this.previousAreaRect.centerX() - this.areaView.getCropCenterX(), this.previousAreaRect.centerY() - this.areaView.getCropCenterY());
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    public void onDrag(float dx, float dy) {
        if (!this.animating) {
            this.state.translate(dx, dy);
            updateMatrix();
        }
    }

    public void onFling(float startX, float startY, float velocityX, float velocityY) {
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
            resetRotationStartScale();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float scale, float x, float y) {
        if (!this.animating) {
            if (this.state.getScale() * scale > 30.0f) {
                scale = 30.0f / this.state.getScale();
            }
            this.state.scale(scale, ((x - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((y - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < EPSILON) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    public void setRotation(float angle) {
        this.state.rotate(angle - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public Bitmap getResult() {
        CropState cropState = this.state;
        if (cropState == null || (!cropState.hasChanges() && this.state.getBaseRotation() < EPSILON && this.freeform)) {
            return this.bitmap;
        }
        RectF cropRect = new RectF();
        this.areaView.getCropRect(cropRect);
        int width = (int) Math.ceil((double) scaleWidthToMaxSize(cropRect, new RectF(0.0f, 0.0f, 1280.0f, 1280.0f)));
        int height = (int) Math.ceil((double) (((float) width) / this.areaView.getAspectRatio()));
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        matrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(matrix);
        float scale = ((float) width) / this.areaView.getCropWidth();
        matrix.postScale(scale, scale);
        matrix.postTranslate((float) (width / 2), (float) (height / 2));
        new Canvas(resultBitmap).drawBitmap(this.bitmap, matrix, new Paint(2));
        return resultBitmap;
    }

    private void setLockedAspectRatio(float aspectRatio) {
        this.areaView.setLockedAspectRatio(aspectRatio);
        RectF targetRect = new RectF();
        this.areaView.calculateRect(targetRect, aspectRatio);
        fillAreaView(targetRect, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.areaView.getLockAspectRatio() > 0.0f) {
            this.areaView.setLockedAspectRatio(0.0f);
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onAspectLock(false);
            }
        } else if (!this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            String[] actions = new String[8];
            Integer[][] ratios = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
            actions[0] = LocaleController.getString("CropOriginal", R.string.CropOriginal);
            actions[1] = LocaleController.getString("CropSquare", R.string.CropSquare);
            int i = 2;
            for (Integer[] ratioPair : ratios) {
                if (this.areaView.getAspectRatio() > 1.0f) {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[0], ratioPair[1]});
                } else {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[1], ratioPair[0]});
                }
                i++;
            }
            AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(actions, new DialogInterface.OnClickListener(ratios) {
                private final /* synthetic */ Integer[][] f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    CropView.this.lambda$showAspectRatioDialog$2$CropView(this.f$1, dialogInterface, i);
                }
            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    CropView.this.lambda$showAspectRatioDialog$3$CropView(dialogInterface);
                }
            });
            dialog.show();
        }
    }

    public /* synthetic */ void lambda$showAspectRatioDialog$2$CropView(Integer[][] ratios, DialogInterface dialog12, int which) {
        this.hasAspectRatioDialog = false;
        if (which == 0) {
            setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
        } else if (which != 1) {
            Integer[] ratioPair = ratios[which - 2];
            if (this.areaView.getAspectRatio() > 1.0f) {
                setLockedAspectRatio(((float) ratioPair[0].intValue()) / ((float) ratioPair[1].intValue()));
            } else {
                setLockedAspectRatio(((float) ratioPair[1].intValue()) / ((float) ratioPair[0].intValue()));
            }
        } else {
            setLockedAspectRatio(1.0f);
        }
    }

    public /* synthetic */ void lambda$showAspectRatioDialog$3$CropView(DialogInterface dialog1) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        float w = this.areaView.getCropWidth();
        CropState cropState = this.state;
        if (cropState != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            this.state.scale(this.areaView.getCropWidth() / w, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }
}
