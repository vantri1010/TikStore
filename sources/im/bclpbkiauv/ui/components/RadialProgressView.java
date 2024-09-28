package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class RadialProgressView extends View {
    private static final float risingTime = 500.0f;
    private static final float rotationTime = 2000.0f;
    private AccelerateInterpolator accelerateInterpolator;
    private RectF cicleRect;
    private float currentCircleLength;
    private float currentProgressTime;
    private DecelerateInterpolator decelerateInterpolator;
    private long lastUpdateTime;
    private int progressColor;
    private Paint progressPaint;
    private float radOffset;
    private boolean risingCircleLength;
    private int size;
    private boolean useSelfAlpha;

    public RadialProgressView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RadialProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.cicleRect = new RectF();
        this.size = AndroidUtilities.dp(40.0f);
        this.progressColor = Theme.getColor(Theme.key_progressCircle);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.accelerateInterpolator = new AccelerateInterpolator();
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.progressPaint.setColor(this.progressColor);
    }

    public void setUseSelfAlpha(boolean value) {
        this.useSelfAlpha = value;
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        if (this.useSelfAlpha) {
            Drawable background = getBackground();
            int a = (int) (255.0f * alpha);
            if (background != null) {
                background.setAlpha(a);
            }
            this.progressPaint.setAlpha(a);
        }
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        float f = this.radOffset + (((float) (360 * dt)) / rotationTime);
        this.radOffset = f;
        this.radOffset = f - ((float) (((int) (f / 360.0f)) * 360));
        float f2 = this.currentProgressTime + ((float) dt);
        this.currentProgressTime = f2;
        if (f2 >= risingTime) {
            this.currentProgressTime = risingTime;
        }
        if (this.risingCircleLength) {
            this.currentCircleLength = (this.accelerateInterpolator.getInterpolation(this.currentProgressTime / risingTime) * 266.0f) + 4.0f;
        } else {
            this.currentCircleLength = 4.0f - ((1.0f - this.decelerateInterpolator.getInterpolation(this.currentProgressTime / risingTime)) * 270.0f);
        }
        if (this.currentProgressTime == risingTime) {
            if (this.risingCircleLength) {
                this.radOffset += 270.0f;
                this.currentCircleLength = -266.0f;
            }
            this.risingCircleLength = !this.risingCircleLength;
            this.currentProgressTime = 0.0f;
        }
        invalidate();
    }

    public void setSize(int value) {
        this.size = value;
        invalidate();
    }

    public void setStrokeWidth(float value) {
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(value));
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        this.progressPaint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x = (getMeasuredWidth() - this.size) / 2;
        int measuredHeight = getMeasuredHeight();
        int i = this.size;
        int y = (measuredHeight - i) / 2;
        this.cicleRect.set((float) x, (float) y, (float) (x + i), (float) (i + y));
        canvas.drawArc(this.cicleRect, this.radOffset, this.currentCircleLength, false, this.progressPaint);
        updateAnimation();
    }
}
