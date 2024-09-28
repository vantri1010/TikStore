package im.bclpbkiauv.ui.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class MessageBackgroundDrawable extends Drawable {
    public static final float ANIMATION_DURATION = 200.0f;
    private boolean animationInProgress;
    private float currentAnimationProgress;
    private int finalRadius;
    private boolean isSelected;
    private long lastAnimationTime;
    private Paint paint;
    private float touchX = -1.0f;
    private float touchY = -1.0f;

    public MessageBackgroundDrawable(int color) {
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        paint2.setColor(color);
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setSelected(boolean selected, boolean animated) {
        float f = 1.0f;
        if (this.isSelected != selected) {
            this.isSelected = selected;
            this.animationInProgress = false;
            if (0 != 0) {
                this.lastAnimationTime = SystemClock.uptimeMillis();
            } else {
                if (!selected) {
                    f = 0.0f;
                }
                this.currentAnimationProgress = f;
            }
            calcRadius();
            invalidateSelf();
        } else if (this.animationInProgress && 0 == 0) {
            if (!selected) {
                f = 0.0f;
            }
            this.currentAnimationProgress = f;
            this.animationInProgress = false;
        }
    }

    private void calcRadius() {
        float y1;
        float x1;
        int i;
        float x2;
        Rect bounds = getBounds();
        if (this.touchX < 0.0f || this.touchY < 0.0f) {
            x1 = (float) bounds.centerX();
            y1 = (float) bounds.centerY();
        } else {
            x1 = this.touchX;
            y1 = this.touchY;
        }
        this.finalRadius = 0;
        for (int a = 0; a < 4; a++) {
            if (a == 0) {
                x2 = (float) bounds.left;
                i = bounds.top;
            } else if (a == 1) {
                x2 = (float) bounds.left;
                i = bounds.bottom;
            } else if (a != 2) {
                x2 = (float) bounds.right;
                i = bounds.bottom;
            } else {
                x2 = (float) bounds.right;
                i = bounds.top;
            }
            float y2 = (float) i;
            this.finalRadius = Math.max(this.finalRadius, (int) Math.ceil(Math.sqrt((double) (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))))));
        }
    }

    public void setTouchCoords(float x, float y) {
        this.touchX = x;
        this.touchY = y;
        calcRadius();
        invalidateSelf();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        calcRadius();
    }

    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        calcRadius();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    public void draw(Canvas canvas) {
        float x1;
        float x12;
        if (this.animationInProgress) {
            long newTime = SystemClock.uptimeMillis();
            long dt = newTime - this.lastAnimationTime;
            this.lastAnimationTime = newTime;
            if (this.isSelected) {
                float f = this.currentAnimationProgress + (((float) dt) / 200.0f);
                this.currentAnimationProgress = f;
                if (f >= 1.0f) {
                    this.touchX = -1.0f;
                    this.touchY = -1.0f;
                    this.currentAnimationProgress = 1.0f;
                    this.animationInProgress = false;
                }
                invalidateSelf();
            } else {
                float f2 = this.currentAnimationProgress - (((float) dt) / 200.0f);
                this.currentAnimationProgress = f2;
                if (f2 <= 0.0f) {
                    this.touchX = -1.0f;
                    this.touchY = -1.0f;
                    this.currentAnimationProgress = 0.0f;
                    this.animationInProgress = false;
                }
                invalidateSelf();
            }
        }
        float f3 = this.currentAnimationProgress;
        if (f3 == 1.0f) {
            canvas.drawRect(getBounds(), this.paint);
        } else if (f3 != 0.0f) {
            if (this.touchX < 0.0f || this.touchY < 0.0f) {
                Rect bounds = getBounds();
                x12 = (float) bounds.centerX();
                x1 = (float) bounds.centerY();
            } else {
                x12 = this.touchX;
                x1 = this.touchY;
            }
            canvas.drawCircle(x12, x1, ((float) this.finalRadius) * CubicBezierInterpolator.EASE_OUT.getInterpolation(this.currentAnimationProgress), this.paint);
        }
    }
}
