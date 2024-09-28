package com.scwang.smartrefresh.layout.footer;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.core.graphics.ColorUtils;
import com.scwang.smartrefresh.layout.R;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;
import com.scwang.smartrefresh.layout.util.SmartUtil;

public class BallPulseFooter extends InternalAbstract implements RefreshFooter {
    protected int mAnimatingColor;
    protected float mCircleSpacing;
    protected TimeInterpolator mInterpolator;
    protected boolean mIsStarted;
    protected boolean mManualAnimationColor;
    protected boolean mManualNormalColor;
    protected int mNormalColor;
    protected Paint mPaint;
    protected long mStartTime;

    public BallPulseFooter(Context context) {
        this(context, (AttributeSet) null);
    }

    public BallPulseFooter(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mNormalColor = -1118482;
        this.mAnimatingColor = -1615546;
        this.mStartTime = 0;
        this.mIsStarted = false;
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        setMinimumHeight(SmartUtil.dp2px(60.0f));
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(-1);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
        this.mSpinnerStyle = SpinnerStyle.Translate;
        this.mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.BallPulseFooter_srlClassicsSpinnerStyle, this.mSpinnerStyle.ordinal)];
        if (ta.hasValue(R.styleable.BallPulseFooter_srlNormalColor)) {
            setNormalColor(ta.getColor(R.styleable.BallPulseFooter_srlNormalColor, 0));
        }
        if (ta.hasValue(R.styleable.BallPulseFooter_srlAnimatingColor)) {
            setAnimatingColor(ta.getColor(R.styleable.BallPulseFooter_srlAnimatingColor, 0));
        }
        ta.recycle();
        this.mCircleSpacing = (float) SmartUtil.dp2px(4.0f);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int width = getWidth();
        int height = getHeight();
        float f = this.mCircleSpacing;
        float radius = (((float) Math.min(width, height)) - (f * 2.0f)) / 6.0f;
        float x = (((float) width) / 2.0f) - ((radius * 2.0f) + f);
        float y = ((float) height) / 2.0f;
        long now = System.currentTimeMillis();
        int i = 0;
        while (i < 3) {
            long time = (now - this.mStartTime) - ((long) ((i + 1) * 120));
            float percent = this.mInterpolator.getInterpolation(time > 0 ? ((float) (time % 750)) / 750.0f : 0.0f);
            canvas.save();
            canvas2.translate(x + (radius * 2.0f * ((float) i)) + (this.mCircleSpacing * ((float) i)), y);
            int width2 = width;
            int height2 = height;
            if (((double) percent) < 0.5d) {
                float scale = 1.0f - ((percent * 2.0f) * 0.7f);
                canvas2.scale(scale, scale);
            } else {
                float scale2 = ((percent * 2.0f) * 0.7f) - 0.4f;
                canvas2.scale(scale2, scale2);
            }
            canvas2.drawCircle(0.0f, 0.0f, radius, this.mPaint);
            canvas.restore();
            i++;
            width = width2;
            height = height2;
        }
        int i2 = height;
        super.dispatchDraw(canvas);
        if (this.mIsStarted) {
            invalidate();
        }
    }

    public void onStartAnimator(RefreshLayout layout, int height, int maxDragHeight) {
        if (!this.mIsStarted) {
            invalidate();
            this.mIsStarted = true;
            this.mStartTime = System.currentTimeMillis();
            this.mPaint.setColor(this.mAnimatingColor);
        }
    }

    public int onFinish(RefreshLayout layout, boolean success) {
        this.mIsStarted = false;
        this.mStartTime = 0;
        this.mPaint.setColor(this.mNormalColor);
        return 0;
    }

    @Deprecated
    public void setPrimaryColors(int... colors) {
        if (!this.mManualAnimationColor && colors.length > 1) {
            setAnimatingColor(colors[0]);
            this.mManualAnimationColor = false;
        }
        if (!this.mManualNormalColor) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(-1711276033, colors[0]));
            }
            this.mManualNormalColor = false;
        }
    }

    public BallPulseFooter setSpinnerStyle(SpinnerStyle mSpinnerStyle) {
        this.mSpinnerStyle = mSpinnerStyle;
        return this;
    }

    public BallPulseFooter setNormalColor(int color) {
        this.mNormalColor = color;
        this.mManualNormalColor = true;
        if (!this.mIsStarted) {
            this.mPaint.setColor(color);
        }
        return this;
    }

    public BallPulseFooter setAnimatingColor(int color) {
        this.mAnimatingColor = color;
        this.mManualAnimationColor = true;
        if (this.mIsStarted) {
            this.mPaint.setColor(color);
        }
        return this;
    }
}
