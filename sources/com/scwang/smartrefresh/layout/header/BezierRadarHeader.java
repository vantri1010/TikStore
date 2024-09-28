package com.scwang.smartrefresh.layout.header;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class BezierRadarHeader extends InternalAbstract implements RefreshHeader {
    protected static final byte PROPERTY_DOT_ALPHA = 2;
    protected static final byte PROPERTY_RADAR_ANGLE = 4;
    protected static final byte PROPERTY_RADAR_SCALE = 0;
    protected static final byte PROPERTY_RIPPLE_RADIUS = 3;
    protected static final byte PROPERTY_WAVE_HEIGHT = 1;
    protected int mAccentColor;
    protected Animator mAnimatorSet;
    protected float mDotAlpha;
    protected float mDotFraction;
    protected float mDotRadius;
    protected boolean mEnableHorizontalDrag;
    protected boolean mManualAccentColor;
    protected boolean mManualPrimaryColor;
    protected Paint mPaint;
    protected Path mPath;
    protected int mPrimaryColor;
    protected int mRadarAngle;
    protected float mRadarCircle;
    protected float mRadarRadius;
    protected RectF mRadarRect;
    protected float mRadarScale;
    protected float mRippleRadius;
    protected int mWaveHeight;
    protected int mWaveOffsetX;
    protected int mWaveOffsetY;
    protected boolean mWavePulling;
    protected int mWaveTop;

    public BezierRadarHeader(Context context) {
        this(context, (AttributeSet) null);
    }

    public BezierRadarHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mEnableHorizontalDrag = false;
        this.mWaveOffsetX = -1;
        this.mWaveOffsetY = 0;
        this.mRadarAngle = 0;
        this.mRadarRadius = 0.0f;
        this.mRadarCircle = 0.0f;
        this.mRadarScale = 0.0f;
        this.mRadarRect = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        this.mSpinnerStyle = SpinnerStyle.FixedBehind;
        this.mPath = new Path();
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mDotRadius = (float) SmartUtil.dp2px(7.0f);
        this.mRadarRadius = (float) SmartUtil.dp2px(20.0f);
        this.mRadarCircle = (float) SmartUtil.dp2px(7.0f);
        this.mPaint.setStrokeWidth((float) SmartUtil.dp2px(3.0f));
        setMinimumHeight(SmartUtil.dp2px(100.0f));
        if (isInEditMode()) {
            this.mWaveTop = 1000;
            this.mRadarScale = 1.0f;
            this.mRadarAngle = 270;
        } else {
            this.mRadarScale = 0.0f;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BezierRadarHeader);
        this.mEnableHorizontalDrag = ta.getBoolean(R.styleable.BezierRadarHeader_srlEnableHorizontalDrag, this.mEnableHorizontalDrag);
        setAccentColor(ta.getColor(R.styleable.BezierRadarHeader_srlAccentColor, -1));
        setPrimaryColor(ta.getColor(R.styleable.BezierRadarHeader_srlPrimaryColor, -14540254));
        this.mManualAccentColor = ta.hasValue(R.styleable.BezierRadarHeader_srlAccentColor);
        this.mManualPrimaryColor = ta.hasValue(R.styleable.BezierRadarHeader_srlPrimaryColor);
        ta.recycle();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Animator animator = this.mAnimatorSet;
        if (animator != null) {
            animator.removeAllListeners();
            this.mAnimatorSet.end();
            this.mAnimatorSet = null;
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = isInEditMode() ? getHeight() : this.mWaveOffsetY;
        drawWave(canvas, width);
        drawDot(canvas, width, height);
        drawRadar(canvas, width, height);
        drawRipple(canvas, width, height);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void drawWave(Canvas canvas, int width) {
        this.mPath.reset();
        this.mPath.lineTo(0.0f, (float) this.mWaveTop);
        Path path = this.mPath;
        int i = this.mWaveOffsetX;
        float f = i >= 0 ? (float) i : ((float) width) / 2.0f;
        int i2 = this.mWaveTop;
        path.quadTo(f, (float) (this.mWaveHeight + i2), (float) width, (float) i2);
        this.mPath.lineTo((float) width, 0.0f);
        this.mPaint.setColor(this.mPrimaryColor);
        canvas.drawPath(this.mPath, this.mPaint);
    }

    /* access modifiers changed from: protected */
    public void drawDot(Canvas canvas, int width, int height) {
        int i = width;
        int i2 = height;
        float f = 0.0f;
        if (this.mDotAlpha > 0.0f) {
            this.mPaint.setColor(this.mAccentColor);
            float x = SmartUtil.px2dp(height);
            float f2 = 7.0f;
            float f3 = this.mDotFraction;
            float wide = (((((float) i) * 1.0f) / 7.0f) * f3) - (f3 > 1.0f ? ((f3 - 1.0f) * ((((float) i) * 1.0f) / 7.0f)) / f3 : 0.0f);
            float f4 = (float) i2;
            float f5 = this.mDotFraction;
            float f6 = 2.0f;
            if (f5 > 1.0f) {
                f = (((f5 - 1.0f) * ((float) i2)) / 2.0f) / f5;
            }
            float high = f4 - f;
            int i3 = 0;
            while (i3 < 7) {
                float index = (((float) i3) + 1.0f) - 4.0f;
                float high2 = high;
                this.mPaint.setAlpha((int) (((double) (this.mDotAlpha * (1.0f - ((Math.abs(index) / f2) * f6)) * 255.0f)) * (1.0d - (1.0d / Math.pow((((double) x) / 800.0d) + 1.0d, 15.0d)))));
                float radius = this.mDotRadius * (1.0f - (1.0f / ((x / 10.0f) + 1.0f)));
                f6 = 2.0f;
                canvas.drawCircle(((((float) i) / 2.0f) - (radius / 2.0f)) + (wide * index), high2 / 2.0f, radius, this.mPaint);
                i3++;
                high = high2;
                f2 = 7.0f;
            }
            Canvas canvas2 = canvas;
            float f7 = high;
            this.mPaint.setAlpha(255);
            return;
        }
        Canvas canvas3 = canvas;
    }

    /* access modifiers changed from: protected */
    public void drawRadar(Canvas canvas, int width, int height) {
        Canvas canvas2 = canvas;
        int i = width;
        int i2 = height;
        if (this.mAnimatorSet != null || isInEditMode()) {
            float f = this.mRadarRadius;
            float f2 = this.mRadarScale;
            float radius = f * f2;
            float circle = this.mRadarCircle * f2;
            this.mPaint.setColor(this.mAccentColor);
            this.mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(((float) i) / 2.0f, ((float) i2) / 2.0f, radius, this.mPaint);
            this.mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(((float) i) / 2.0f, ((float) i2) / 2.0f, radius + circle, this.mPaint);
            this.mPaint.setColor((this.mPrimaryColor & ViewCompat.MEASURED_SIZE_MASK) | 1426063360);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mRadarRect.set((((float) i) / 2.0f) - radius, (((float) i2) / 2.0f) - radius, (((float) i) / 2.0f) + radius, (((float) i2) / 2.0f) + radius);
            canvas.drawArc(this.mRadarRect, 270.0f, (float) this.mRadarAngle, true, this.mPaint);
            float radius2 = radius + circle;
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mRadarRect.set((((float) i) / 2.0f) - radius2, (((float) i2) / 2.0f) - radius2, (((float) i) / 2.0f) + radius2, (((float) i2) / 2.0f) + radius2);
            canvas.drawArc(this.mRadarRect, 270.0f, (float) this.mRadarAngle, false, this.mPaint);
            this.mPaint.setStyle(Paint.Style.FILL);
        }
    }

    /* access modifiers changed from: protected */
    public void drawRipple(Canvas canvas, int width, int height) {
        if (this.mRippleRadius > 0.0f) {
            this.mPaint.setColor(this.mAccentColor);
            canvas.drawCircle(((float) width) / 2.0f, ((float) height) / 2.0f, this.mRippleRadius, this.mPaint);
        }
    }

    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        this.mWaveOffsetY = offset;
        if (isDragging || this.mWavePulling) {
            this.mWavePulling = true;
            this.mWaveTop = Math.min(height, offset);
            this.mWaveHeight = (int) (((float) Math.max(0, offset - height)) * 1.9f);
            this.mDotFraction = percent;
            invalidate();
        }
    }

    public void onReleased(RefreshLayout refreshLayout, int height, int maxDragHeight) {
        this.mWaveTop = height - 1;
        this.mWavePulling = false;
        Interpolator interpolatorDecelerate = new SmartUtil(SmartUtil.INTERPOLATOR_DECELERATE);
        ValueAnimator animatorDotAlpha = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorDotAlpha.addUpdateListener(new AnimatorUpdater(PROPERTY_DOT_ALPHA));
        ValueAnimator animatorRadarScale = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorRadarScale.addUpdateListener(new AnimatorUpdater((byte) 0));
        ValueAnimator mRadarAnimator = ValueAnimator.ofInt(new int[]{0, 360});
        mRadarAnimator.setDuration(720);
        mRadarAnimator.setRepeatCount(-1);
        mRadarAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRadarAnimator.addUpdateListener(new AnimatorUpdater(PROPERTY_RADAR_ANGLE));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(new Animator[]{animatorDotAlpha, animatorRadarScale, mRadarAnimator});
        animatorSet.start();
        int i = this.mWaveHeight;
        ValueAnimator animatorWave = ValueAnimator.ofInt(new int[]{i, 0, -((int) (((float) i) * 0.8f)), 0, -((int) (((float) i) * 0.4f)), 0});
        animatorWave.addUpdateListener(new AnimatorUpdater(PROPERTY_WAVE_HEIGHT));
        animatorWave.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_DECELERATE));
        animatorWave.setDuration(800);
        animatorWave.start();
        this.mAnimatorSet = animatorSet;
    }

    public int onFinish(RefreshLayout layout, boolean success) {
        Animator animator = this.mAnimatorSet;
        if (animator != null) {
            animator.removeAllListeners();
            this.mAnimatorSet.end();
            this.mAnimatorSet = null;
        }
        int width = getWidth();
        int height = this.mWaveOffsetY;
        ValueAnimator animator2 = ValueAnimator.ofFloat(new float[]{this.mRadarRadius, (float) Math.sqrt((double) ((width * width) + (height * height)))});
        animator2.setDuration(400);
        animator2.addUpdateListener(new AnimatorUpdater(PROPERTY_RIPPLE_RADIUS));
        animator2.start();
        return 400;
    }

    /* renamed from: com.scwang.smartrefresh.layout.header.BezierRadarHeader$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState;

        static {
            int[] iArr = new int[RefreshState.values().length];
            $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState = iArr;
            try {
                iArr[RefreshState.None.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[RefreshState.PullDownToRefresh.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        int i = AnonymousClass1.$SwitchMap$com$scwang$smartrefresh$layout$constant$RefreshState[newState.ordinal()];
        if (i == 1 || i == 2) {
            this.mDotAlpha = 1.0f;
            this.mRadarScale = 0.0f;
            this.mRippleRadius = 0.0f;
        }
    }

    @Deprecated
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0 && !this.mManualPrimaryColor) {
            setPrimaryColor(colors[0]);
            this.mManualPrimaryColor = false;
        }
        if (colors.length > 1 && !this.mManualAccentColor) {
            setAccentColor(colors[1]);
            this.mManualAccentColor = false;
        }
    }

    public boolean isSupportHorizontalDrag() {
        return this.mEnableHorizontalDrag;
    }

    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        this.mWaveOffsetX = offsetX;
        invalidate();
    }

    public BezierRadarHeader setPrimaryColor(int color) {
        this.mPrimaryColor = color;
        this.mManualPrimaryColor = true;
        return this;
    }

    public BezierRadarHeader setAccentColor(int color) {
        this.mAccentColor = color;
        this.mManualAccentColor = true;
        return this;
    }

    public BezierRadarHeader setPrimaryColorId(int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public BezierRadarHeader setAccentColorId(int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public BezierRadarHeader setEnableHorizontalDrag(boolean enable) {
        this.mEnableHorizontalDrag = enable;
        if (!enable) {
            this.mWaveOffsetX = -1;
        }
        return this;
    }

    protected class AnimatorUpdater implements ValueAnimator.AnimatorUpdateListener {
        byte propertyName;

        AnimatorUpdater(byte name) {
            this.propertyName = name;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            byte b = this.propertyName;
            if (b == 0) {
                BezierRadarHeader.this.mRadarScale = ((Float) animation.getAnimatedValue()).floatValue();
            } else if (1 == b) {
                if (BezierRadarHeader.this.mWavePulling) {
                    animation.cancel();
                    return;
                } else {
                    BezierRadarHeader.this.mWaveHeight = ((Integer) animation.getAnimatedValue()).intValue() / 2;
                }
            } else if (2 == b) {
                BezierRadarHeader.this.mDotAlpha = ((Float) animation.getAnimatedValue()).floatValue();
            } else if (3 == b) {
                BezierRadarHeader.this.mRippleRadius = ((Float) animation.getAnimatedValue()).floatValue();
            } else if (4 == b) {
                BezierRadarHeader.this.mRadarAngle = ((Integer) animation.getAnimatedValue()).intValue();
            }
            BezierRadarHeader.this.invalidate();
        }
    }
}
