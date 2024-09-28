package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class CheckBoxBase {
    private static Paint eraser;
    private static Paint paint;
    private boolean attachedToWindow;
    private String background2ColorKey;
    private float backgroundAlpha;
    private String backgroundColorKey;
    private Paint backgroundPaint;
    private Canvas bitmapCanvas;
    private Rect bounds;
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private String checkColorKey;
    private Paint checkPaint;
    /* access modifiers changed from: private */
    public String checkedText;
    private int drawBackgroundAsArc;
    private Bitmap drawBitmap;
    private boolean drawUnchecked;
    private boolean enabled;
    /* access modifiers changed from: private */
    public boolean isChecked;
    private View parentView;
    private Path path;
    private float progress;
    private ProgressDelegate progressDelegate;
    private RectF rect;
    private float size;
    private TextPaint textPaint;
    private boolean useDefaultCheck;

    public interface ProgressDelegate {
        void setProgress(float f);
    }

    public CheckBoxBase(View parent) {
        this(parent, 21);
    }

    public CheckBoxBase(View parent, int sz) {
        this.bounds = new Rect();
        this.rect = new RectF();
        this.path = new Path();
        this.enabled = true;
        this.backgroundAlpha = 1.0f;
        this.checkColorKey = Theme.key_checkboxCheck;
        this.backgroundColorKey = Theme.key_divider;
        this.background2ColorKey = Theme.key_chat_serviceBackground;
        this.drawUnchecked = true;
        this.size = 21.0f;
        this.parentView = parent;
        this.size = (float) sz;
        if (paint == null) {
            paint = new Paint(1);
            Paint paint2 = new Paint(1);
            eraser = paint2;
            paint2.setColor(Theme.getColor(Theme.key_switchTrackBlueChecked));
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        Paint paint3 = new Paint(1);
        this.checkPaint = paint3;
        paint3.setStrokeCap(Paint.Cap.ROUND);
        this.checkPaint.setStyle(Paint.Style.STROKE);
        this.checkPaint.setStrokeJoin(Paint.Join.ROUND);
        this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
        Paint paint4 = new Paint(1);
        this.backgroundPaint = paint4;
        paint4.setStyle(Paint.Style.STROKE);
        this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
        this.bitmapCanvas = new Canvas(this.drawBitmap);
    }

    public void onAttachedToWindow() {
        this.attachedToWindow = true;
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.bounds.left = x;
        this.bounds.top = y;
        this.bounds.right = x + width;
        this.bounds.bottom = y + height;
    }

    public void setDrawUnchecked(boolean value) {
        this.drawUnchecked = value;
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
            ProgressDelegate progressDelegate2 = this.progressDelegate;
            if (progressDelegate2 != null) {
                progressDelegate2.setProgress(value);
            }
        }
    }

    private void invalidate() {
        if (this.parentView.getParent() != null) {
            ((View) this.parentView.getParent()).invalidate();
        }
        this.parentView.invalidate();
    }

    public void setProgressDelegate(ProgressDelegate delegate) {
        this.progressDelegate = delegate;
    }

    public float getProgress() {
        return this.progress;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setDrawBackgroundAsArc(int type) {
        this.drawBackgroundAsArc = type;
        if (type == 4 || type == 5) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.9f));
            if (type == 5) {
                this.checkPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            }
        } else if (type == 3) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.2f));
        } else if (type != 0) {
            this.backgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        }
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(CheckBoxBase.this.checkAnimator)) {
                    ObjectAnimator unused = CheckBoxBase.this.checkAnimator = null;
                }
                if (!CheckBoxBase.this.isChecked) {
                    String unused2 = CheckBoxBase.this.checkedText = null;
                }
            }
        });
        this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.checkAnimator.setDuration(200);
        this.checkAnimator.start();
    }

    public void setColor(String background, String background2, String check) {
        this.backgroundColorKey = background;
        this.background2ColorKey = background2;
        this.checkColorKey = check;
    }

    public void setUseDefaultCheck(boolean value) {
        this.useDefaultCheck = value;
    }

    public void setBackgroundAlpha(float alpha) {
        this.backgroundAlpha = alpha;
    }

    public void setNum(int num) {
        if (num >= 0) {
            this.checkedText = "" + (num + 1);
        } else if (this.checkAnimator == null) {
            this.checkedText = null;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(-1, checked, animated);
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

    public void draw(Canvas canvas) {
        float outerRad;
        int i;
        String str;
        int startAngle;
        int sweepAngle;
        Canvas canvas2 = canvas;
        Bitmap bitmap = this.drawBitmap;
        if (bitmap != null) {
            bitmap.eraseColor(0);
            float rad = (float) AndroidUtilities.dp(this.size / 2.0f);
            float outerRad2 = rad;
            if (this.drawBackgroundAsArc != 0) {
                outerRad = outerRad2 - ((float) AndroidUtilities.dp(0.2f));
            } else {
                outerRad = outerRad2;
            }
            float outerRad3 = this.progress;
            float roundProgress = outerRad3 >= 0.5f ? 1.0f : outerRad3 / 0.5f;
            int cx = this.bounds.centerX();
            int cy = this.bounds.centerY();
            if (this.backgroundColorKey != null) {
                if (this.drawUnchecked) {
                    paint.setColor(0);
                    if (this.isChecked) {
                        this.backgroundPaint.setColor(0);
                    } else {
                        this.backgroundPaint.setColor(Theme.getColor(this.backgroundColorKey));
                    }
                } else {
                    Paint paint2 = this.backgroundPaint;
                    String str2 = this.background2ColorKey;
                    if (str2 == null) {
                        str2 = this.checkColorKey;
                    }
                    paint2.setColor(AndroidUtilities.getOffsetColor(ViewCompat.MEASURED_SIZE_MASK, Theme.getColor(str2), this.progress, this.backgroundAlpha));
                }
            } else if (this.drawUnchecked) {
                paint.setColor(Color.argb((int) (this.backgroundAlpha * 25.0f), 0, 0, 0));
                this.backgroundPaint.setColor(AndroidUtilities.getOffsetColor(-1, Theme.getColor(this.checkColorKey), this.progress, this.backgroundAlpha));
            } else {
                Paint paint3 = this.backgroundPaint;
                String str3 = this.background2ColorKey;
                if (str3 == null) {
                    str3 = this.checkColorKey;
                }
                paint3.setColor(AndroidUtilities.getOffsetColor(ViewCompat.MEASURED_SIZE_MASK, Theme.getColor(str3), this.progress, this.backgroundAlpha));
            }
            if (this.drawUnchecked) {
                int i2 = this.drawBackgroundAsArc;
                if (i2 == 6 || i2 == 7) {
                    canvas2.drawCircle((float) cx, (float) cy, rad - ((float) AndroidUtilities.dp(1.0f)), paint);
                    canvas2.drawCircle((float) cx, (float) cy, rad - ((float) AndroidUtilities.dp(1.5f)), this.backgroundPaint);
                } else {
                    canvas2.drawCircle((float) cx, (float) cy, rad, paint);
                }
            }
            paint.setColor(Theme.getColor(this.checkColorKey));
            int i3 = this.drawBackgroundAsArc;
            if (i3 == 7) {
                i = 7;
            } else if (i3 == 0) {
                canvas2.drawCircle((float) cx, (float) cy, rad, this.backgroundPaint);
                float f = outerRad;
                i = 7;
            } else {
                this.rect.set(((float) cx) - outerRad, ((float) cy) - outerRad, ((float) cx) + outerRad, ((float) cy) + outerRad);
                int startAngle2 = this.drawBackgroundAsArc;
                if (startAngle2 == 6) {
                    startAngle = 0;
                    sweepAngle = (int) (this.progress * -360.0f);
                } else if (startAngle2 == 1) {
                    startAngle = -90;
                    sweepAngle = (int) (this.progress * -270.0f);
                } else {
                    startAngle = 90;
                    sweepAngle = (int) (this.progress * 270.0f);
                }
                if (this.drawBackgroundAsArc == 6) {
                    int color = Theme.getColor(Theme.key_dialogBackground);
                    int alpha = Color.alpha(color);
                    this.backgroundPaint.setColor(color);
                    this.backgroundPaint.setAlpha((int) (((float) alpha) * this.progress));
                    int i4 = alpha;
                    int i5 = color;
                    int sweepAngle2 = sweepAngle;
                    float f2 = outerRad;
                    i = 7;
                    canvas.drawArc(this.rect, (float) startAngle, (float) sweepAngle, false, this.backgroundPaint);
                    int color2 = Theme.getColor(Theme.key_chat_attachPhotoBackground);
                    int alpha2 = Color.alpha(color2);
                    this.backgroundPaint.setColor(color2);
                    this.backgroundPaint.setAlpha((int) (((float) alpha2) * this.progress));
                    int i6 = alpha2;
                    canvas.drawArc(this.rect, (float) startAngle, (float) sweepAngle2, false, this.backgroundPaint);
                } else {
                    float f3 = outerRad;
                    i = 7;
                    canvas.drawArc(this.rect, (float) startAngle, (float) sweepAngle, false, this.backgroundPaint);
                }
            }
            if (roundProgress > 0.0f) {
                float f4 = this.progress;
                float checkProgress = f4 < 0.5f ? 0.0f : (f4 - 0.5f) / 0.5f;
                int i7 = this.drawBackgroundAsArc;
                if (i7 == 6 || i7 == i || (!this.drawUnchecked && this.backgroundColorKey != null)) {
                    paint.setColor(Theme.getColor(this.backgroundColorKey));
                } else {
                    paint.setColor(Theme.getColor(this.enabled ? Theme.key_checkbox : Theme.key_checkboxDisabled));
                }
                if (this.useDefaultCheck || (str = this.checkColorKey) == null) {
                    this.checkPaint.setColor(Theme.getColor(Theme.key_checkboxCheck));
                } else {
                    this.checkPaint.setColor(Theme.getColor(str));
                }
                float rad2 = rad - ((float) AndroidUtilities.dp(0.5f));
                this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getHeight() / 2), rad2, paint);
                this.bitmapCanvas.drawCircle((float) (this.drawBitmap.getWidth() / 2), (float) (this.drawBitmap.getWidth() / 2), (1.0f - roundProgress) * rad2, eraser);
                Bitmap bitmap2 = this.drawBitmap;
                canvas2.drawBitmap(bitmap2, (float) (cx - (bitmap2.getWidth() / 2)), (float) (cy - (this.drawBitmap.getHeight() / 2)), (Paint) null);
                if (checkProgress == 0.0f) {
                    return;
                }
                if (this.checkedText != null) {
                    if (this.textPaint == null) {
                        TextPaint textPaint2 = new TextPaint(1);
                        this.textPaint = textPaint2;
                        textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    }
                    int color3 = Theme.getColor(this.checkColorKey);
                    int alpha3 = Color.alpha(color3);
                    this.textPaint.setColor(color3);
                    this.textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                    canvas.save();
                    canvas2.scale(checkProgress, 1.0f, (float) cx, (float) cy);
                    canvas2.drawText(this.checkedText, (float) (cx - (((int) Math.ceil((double) this.textPaint.measureText(this.checkedText))) / 2)), (float) AndroidUtilities.dp(18.0f), this.textPaint);
                    canvas.restore();
                    return;
                }
                this.path.reset();
                float scale = this.drawBackgroundAsArc == 5 ? 0.8f : 1.0f;
                float checkSide = ((float) AndroidUtilities.dp(9.0f * scale)) * checkProgress;
                float smallCheckSide = ((float) AndroidUtilities.dp(scale * 4.0f)) * checkProgress;
                int x = cx - AndroidUtilities.dp(1.5f);
                int y = AndroidUtilities.dp(4.0f) + cy;
                float side = (float) Math.sqrt((double) ((smallCheckSide * smallCheckSide) / 2.0f));
                this.path.moveTo(((float) x) - side, ((float) y) - side);
                this.path.lineTo((float) x, (float) y);
                float side2 = (float) Math.sqrt((double) ((checkSide * checkSide) / 2.0f));
                this.path.lineTo(((float) x) + side2, ((float) y) - side2);
                canvas2.drawPath(this.path, this.checkPaint);
            }
        }
    }
}
