package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.app.NotificationCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class CheckBox extends View {
    private static Paint backgroundPaint = null;
    private static Paint checkPaint = null;
    private static Paint eraser = null;
    private static Paint eraser2 = null;
    private static Paint paint = null;
    private static final float progressBounceDiff = 0.2f;
    private boolean attachedToWindow;
    private Canvas bitmapCanvas;
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private Bitmap checkBitmap;
    private Canvas checkCanvas;
    private Drawable checkDrawable;
    private int checkOffset;
    /* access modifiers changed from: private */
    public String checkedText;
    private int color;
    private boolean drawBackground;
    private Bitmap drawBitmap;
    private boolean hasBorder;
    private boolean isCheckAnimation = true;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private float progress;
    private int size = 22;
    private TextPaint textPaint;

    public CheckBox(Context context, int resId) {
        super(context);
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(0);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint3 = new Paint(1);
            eraser2 = paint3;
            paint3.setColor(0);
            eraser2.setStyle(Paint.Style.STROKE);
            eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint4 = new Paint(1);
            backgroundPaint = paint4;
            paint4.setColor(-1);
            backgroundPaint.setStyle(Paint.Style.STROKE);
        }
        eraser2.setStrokeWidth((float) AndroidUtilities.dp(28.0f));
        backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        TextPaint textPaint2 = new TextPaint(1);
        this.textPaint = textPaint2;
        textPaint2.setTextSize((float) AndroidUtilities.dp(18.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.checkDrawable = context.getResources().getDrawable(resId).mutate();
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 0 && this.drawBitmap == null) {
            try {
                this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Bitmap.Config.ARGB_4444);
                this.bitmapCanvas = new Canvas(this.drawBitmap);
                this.checkBitmap = Bitmap.createBitmap(AndroidUtilities.dp((float) this.size), AndroidUtilities.dp((float) this.size), Bitmap.Config.ARGB_4444);
                this.checkCanvas = new Canvas(this.checkBitmap);
            } catch (Throwable th) {
            }
        }
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public void setDrawBackground(boolean value) {
        this.drawBackground = value;
    }

    public void setHasBorder(boolean value) {
        this.hasBorder = value;
    }

    public void setCheckOffset(int value) {
        this.checkOffset = value;
    }

    public void setSize(int size2) {
        this.size = size2;
        if (size2 == 40) {
            this.textPaint.setTextSize((float) AndroidUtilities.dp(24.0f));
        }
    }

    public void setStrokeWidth(int value) {
        backgroundPaint.setStrokeWidth((float) value);
    }

    public void setStrokeColor(int color2) {
        backgroundPaint.setColor(color2);
    }

    public float getProgress() {
        return this.progress;
    }

    public void setColor(int backgroundColor, int checkColor) {
        this.color = backgroundColor;
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY));
        this.textPaint.setColor(checkColor);
        invalidate();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.color = backgroundColor;
        invalidate();
    }

    public void setCheckColor(int checkColor) {
        this.checkDrawable.setColorFilter(new PorterDuffColorFilter(checkColor, PorterDuff.Mode.MULTIPLY));
        this.textPaint.setColor(checkColor);
        invalidate();
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        this.isCheckAnimation = newCheckedState;
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(CheckBox.this.checkAnimator)) {
                    ObjectAnimator unused = CheckBox.this.checkAnimator = null;
                }
                if (!CheckBox.this.isChecked) {
                    String unused2 = CheckBox.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setDuration(300);
        this.checkAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
    }

    public void setNum(int num) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(int num, boolean checked, boolean animated) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
            invalidate();
        }
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (!this.attachedToWindow || !animated) {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
                return;
            }
            animateToCheckedState(checked);
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (getVisibility() == 0 && this.drawBitmap != null && this.checkBitmap != null) {
            if (this.drawBackground || this.progress != 0.0f) {
                eraser2.setStrokeWidth((float) AndroidUtilities.dp((float) (this.size + 6)));
                this.drawBitmap.eraseColor(0);
                float rad = (float) (getMeasuredWidth() / 2);
                float f = this.progress;
                float roundProgress = f >= 0.5f ? 1.0f : f / 0.5f;
                float f2 = this.progress;
                float checkProgress = f2 < 0.5f ? 0.0f : (f2 - 0.5f) / 0.5f;
                float roundProgressCheckState = this.isCheckAnimation ? this.progress : 1.0f - this.progress;
                if (roundProgressCheckState < progressBounceDiff) {
                    rad -= (((float) AndroidUtilities.dp(2.0f)) * roundProgressCheckState) / progressBounceDiff;
                } else if (roundProgressCheckState < 0.4f) {
                    rad -= ((float) AndroidUtilities.dp(2.0f)) - ((((float) AndroidUtilities.dp(2.0f)) * (roundProgressCheckState - progressBounceDiff)) / progressBounceDiff);
                }
                if (this.drawBackground) {
                    paint.setColor(0);
                    canvas2.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(1.0f)), paint);
                    canvas2.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad - ((float) AndroidUtilities.dp(1.0f)), backgroundPaint);
                }
                paint.setColor(this.color);
                if (this.hasBorder) {
                    rad -= (float) AndroidUtilities.dp(2.0f);
                }
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), rad, paint);
                this.bitmapCanvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (1.0f - roundProgress) * rad, eraser);
                canvas2.drawBitmap(this.drawBitmap, 0.0f, 0.0f, (Paint) null);
                this.checkBitmap.eraseColor(0);
                String str = this.checkedText;
                if (str != null) {
                    this.checkCanvas.drawText(this.checkedText, (float) ((getMeasuredWidth() - ((int) Math.ceil((double) this.textPaint.measureText(str)))) / 2), (float) AndroidUtilities.dp(this.size == 40 ? 28.0f : 21.0f), this.textPaint);
                } else {
                    int w = this.checkDrawable.getIntrinsicWidth();
                    int h = this.checkDrawable.getIntrinsicHeight();
                    int x = (getMeasuredWidth() - w) / 2;
                    int y = (getMeasuredHeight() - h) / 2;
                    Drawable drawable = this.checkDrawable;
                    int i = this.checkOffset;
                    drawable.setBounds(x, y + i, x + w, y + h + i);
                    this.checkDrawable.draw(this.checkCanvas);
                }
                this.checkCanvas.drawCircle((float) ((getMeasuredWidth() / 2) - AndroidUtilities.dp(2.5f)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(4.0f)), ((float) ((getMeasuredWidth() + AndroidUtilities.dp(6.0f)) / 2)) * (1.0f - checkProgress), eraser2);
                canvas2.drawBitmap(this.checkBitmap, 0.0f, 0.0f, (Paint) null);
            }
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.CheckBox");
        info.setCheckable(true);
        info.setChecked(this.isChecked);
    }
}
