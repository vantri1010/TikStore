package com.scwang.smartrefresh.layout.internal;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import com.zhy.http.okhttp.OkHttpUtils;

public class ProgressDrawable extends PaintDrawable implements Animatable, ValueAnimator.AnimatorUpdateListener {
    protected int mHeight = 0;
    protected Path mPath = new Path();
    protected int mProgressDegree = 0;
    protected ValueAnimator mValueAnimator;
    protected int mWidth = 0;

    public ProgressDrawable() {
        ValueAnimator ofInt = ValueAnimator.ofInt(new int[]{30, 3600});
        this.mValueAnimator = ofInt;
        ofInt.setDuration(OkHttpUtils.DEFAULT_MILLISECONDS);
        this.mValueAnimator.setInterpolator((TimeInterpolator) null);
        this.mValueAnimator.setRepeatCount(-1);
        this.mValueAnimator.setRepeatMode(1);
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        this.mProgressDegree = (((Integer) animation.getAnimatedValue()).intValue() / 30) * 30;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        float r = Math.max(1.0f, ((float) width) / 22.0f);
        if (!(this.mWidth == width && this.mHeight == height)) {
            this.mPath.reset();
            this.mPath.addCircle(((float) width) - r, ((float) height) / 2.0f, r, Path.Direction.CW);
            this.mPath.addRect(((float) width) - (r * 5.0f), (((float) height) / 2.0f) - r, ((float) width) - r, (((float) height) / 2.0f) + r, Path.Direction.CW);
            this.mPath.addCircle(((float) width) - (5.0f * r), ((float) height) / 2.0f, r, Path.Direction.CW);
            this.mWidth = width;
            this.mHeight = height;
        }
        canvas.save();
        canvas2.rotate((float) this.mProgressDegree, ((float) width) / 2.0f, ((float) height) / 2.0f);
        for (int i = 0; i < 12; i++) {
            this.mPaint.setAlpha((i + 5) * 17);
            canvas2.rotate(30.0f, ((float) width) / 2.0f, ((float) height) / 2.0f);
            canvas2.drawPath(this.mPath, this.mPaint);
        }
        canvas.restore();
    }

    public void start() {
        if (!this.mValueAnimator.isRunning()) {
            this.mValueAnimator.addUpdateListener(this);
            this.mValueAnimator.start();
        }
    }

    public void stop() {
        if (this.mValueAnimator.isRunning()) {
            this.mValueAnimator.removeAllListeners();
            this.mValueAnimator.removeAllUpdateListeners();
            this.mValueAnimator.cancel();
        }
    }

    public boolean isRunning() {
        return this.mValueAnimator.isRunning();
    }
}
