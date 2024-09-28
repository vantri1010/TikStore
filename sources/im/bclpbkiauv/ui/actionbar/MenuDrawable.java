package im.bclpbkiauv.ui.actionbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class MenuDrawable extends Drawable {
    private boolean animationInProgress;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private boolean reverseAngle;
    private boolean rotateToBack = true;

    public MenuDrawable() {
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void setRotateToBack(boolean value) {
        this.rotateToBack = value;
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        float f = this.currentRotation;
        if (f == 1.0f) {
            this.reverseAngle = true;
        } else if (f == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            float f2 = this.currentRotation;
            if (f2 < rotation) {
                this.currentAnimationTime = (int) (f2 * 300.0f);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f2) * 300.0f);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = rotation;
        } else {
            this.currentRotation = rotation;
            this.finalRotation = rotation;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        float startXDiff;
        float startYDiff;
        float endXDiff;
        float endYDiff;
        Canvas canvas2 = canvas;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                int currentTimeMillis = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                this.currentAnimationTime = currentTimeMillis;
                if (currentTimeMillis >= 300) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) currentTimeMillis) / 300.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) currentTimeMillis) / 300.0f);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        int color1 = Theme.getColor(Theme.key_actionBarDefaultIcon);
        if (this.rotateToBack) {
            canvas2.rotate(this.currentRotation * ((float) (this.reverseAngle ? -180 : 180)));
            this.paint.setColor(color1);
            canvas.drawLine((float) (-AndroidUtilities.dp(9.0f)), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation), 0.0f, this.paint);
            endYDiff = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            endXDiff = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
            startYDiff = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
            startXDiff = ((float) (-AndroidUtilities.dp(9.0f))) + (((float) AndroidUtilities.dp(7.5f)) * Math.abs(this.currentRotation));
        } else {
            canvas2.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : TsExtractor.TS_STREAM_TYPE_E_AC3)));
            this.paint.setColor(AndroidUtilities.getOffsetColor(color1, Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), this.currentRotation, 1.0f));
            canvas.drawLine((((float) AndroidUtilities.dp(1.0f)) * this.currentRotation) + ((float) (-AndroidUtilities.dp(9.0f))), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation), 0.0f, this.paint);
            endYDiff = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            endXDiff = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation));
            startYDiff = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(3.0f)) * Math.abs(this.currentRotation));
            startXDiff = ((float) (-AndroidUtilities.dp(9.0f))) + (((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation));
        }
        Canvas canvas3 = canvas;
        float f = startXDiff;
        float f2 = endXDiff;
        canvas3.drawLine(f, -startYDiff, f2, -endYDiff, this.paint);
        canvas3.drawLine(f, startYDiff, f2, endYDiff, this.paint);
        canvas.restore();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
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
