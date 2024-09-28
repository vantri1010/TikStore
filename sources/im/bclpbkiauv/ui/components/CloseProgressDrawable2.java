package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    private int side;

    public CloseProgressDrawable2() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStyle(Paint.Style.STROKE);
        this.side = AndroidUtilities.dp(8.0f);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public boolean isAnimating() {
        return this.animating;
    }

    public void setColor(int value) {
        this.paint.setColor(value);
    }

    public void setSide(int value) {
        this.side = value;
    }

    public void draw(Canvas canvas) {
        float progress4;
        float progress3;
        float progress2;
        float progress1;
        Canvas canvas2 = canvas;
        long newTime = System.currentTimeMillis();
        long j = this.lastFrameTime;
        float f = 0.0f;
        if (j != 0) {
            long dt = newTime - j;
            if (this.animating || this.angle != 0.0f) {
                float f2 = this.angle + (((float) (360 * dt)) / 500.0f);
                this.angle = f2;
                if (this.animating || f2 < 720.0f) {
                    float f3 = this.angle;
                    this.angle = f3 - ((float) (((int) (f3 / 720.0f)) * 720));
                } else {
                    this.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas2.rotate(-45.0f);
        float f4 = this.angle;
        if (f4 < 0.0f || f4 >= 90.0f) {
            float f5 = this.angle;
            if (f5 < 90.0f || f5 >= 180.0f) {
                float f6 = this.angle;
                if (f6 < 180.0f || f6 >= 270.0f) {
                    float f7 = this.angle;
                    if (f7 < 270.0f || f7 >= 360.0f) {
                        float f8 = this.angle;
                        if (f8 < 360.0f || f8 >= 450.0f) {
                            float f9 = this.angle;
                            if (f9 < 450.0f || f9 >= 540.0f) {
                                float f10 = this.angle;
                                if (f10 < 540.0f || f10 >= 630.0f) {
                                    float f11 = this.angle;
                                    if (f11 < 630.0f || f11 >= 720.0f) {
                                        progress1 = 1.0f;
                                        progress2 = 1.0f;
                                        progress3 = 1.0f;
                                        progress4 = 0.0f;
                                    } else {
                                        progress1 = 1.0f;
                                        progress2 = 1.0f;
                                        progress3 = (f11 - 630.0f) / 90.0f;
                                        progress4 = 0.0f;
                                    }
                                } else {
                                    progress1 = 1.0f;
                                    progress2 = (f10 - 540.0f) / 90.0f;
                                    progress3 = 0.0f;
                                    progress4 = 0.0f;
                                }
                            } else {
                                progress1 = (f9 - 450.0f) / 90.0f;
                                progress2 = 0.0f;
                                progress3 = 0.0f;
                                progress4 = 0.0f;
                            }
                        } else {
                            progress1 = 0.0f;
                            progress2 = 0.0f;
                            progress3 = 0.0f;
                            progress4 = 1.0f - ((f8 - 360.0f) / 90.0f);
                        }
                    } else {
                        progress1 = 0.0f;
                        progress2 = 0.0f;
                        progress3 = 0.0f;
                        progress4 = (f7 - 270.0f) / 90.0f;
                    }
                } else {
                    progress1 = 0.0f;
                    progress2 = 0.0f;
                    progress3 = 1.0f - ((f6 - 180.0f) / 90.0f);
                    progress4 = 0.0f;
                }
            } else {
                progress1 = 0.0f;
                progress2 = 1.0f - ((f5 - 90.0f) / 90.0f);
                progress3 = 1.0f;
                progress4 = 0.0f;
            }
        } else {
            progress1 = 1.0f - (f4 / 90.0f);
            progress2 = 1.0f;
            progress3 = 1.0f;
            progress4 = 0.0f;
        }
        if (progress1 != 0.0f) {
            canvas.drawLine(0.0f, 0.0f, 0.0f, ((float) this.side) * progress1, this.paint);
        }
        if (progress2 != 0.0f) {
            canvas.drawLine(((float) (-this.side)) * progress2, 0.0f, 0.0f, 0.0f, this.paint);
        }
        if (progress3 != 0.0f) {
            canvas.drawLine(0.0f, ((float) (-this.side)) * progress3, 0.0f, 0.0f, this.paint);
        }
        if (progress4 != 1.0f) {
            int i = this.side;
            canvas.drawLine(((float) i) * progress4, 0.0f, (float) i, 0.0f, this.paint);
        }
        canvas.restore();
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        RectF rectF = this.rect;
        int i2 = this.side;
        rectF.set((float) (cx - i2), (float) (cy - i2), (float) (cx + i2), (float) (cy + i2));
        RectF rectF2 = this.rect;
        float f12 = this.angle;
        if (f12 >= 360.0f) {
            f = f12 - 360.0f;
        }
        float f13 = f - 45.0f;
        float f14 = this.angle;
        canvas.drawArc(rectF2, f13, f14 < 360.0f ? f14 : 720.0f - f14, false, this.paint);
        this.lastFrameTime = newTime;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
        this.paint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
