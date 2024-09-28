package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import androidx.core.app.NotificationCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;
import java.util.ArrayList;
import java.util.Locale;

public class NumberTextView extends View {
    /* access modifiers changed from: private */
    public ObjectAnimator animator;
    private int currentNumber = 1;
    private ArrayList<StaticLayout> letters = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<StaticLayout> oldLetters = new ArrayList<>();
    private float progress = 0.0f;
    private TextPaint textPaint = new TextPaint(1);

    public NumberTextView(Context context) {
        super(context);
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setNumber(int number, boolean animated) {
        int i = number;
        if (this.currentNumber != i || !animated) {
            ObjectAnimator objectAnimator = this.animator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
                this.animator = null;
            }
            this.oldLetters.clear();
            this.oldLetters.addAll(this.letters);
            this.letters.clear();
            String oldText = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(this.currentNumber)});
            String text = String.format(Locale.US, "%d", new Object[]{Integer.valueOf(number)});
            boolean forwardAnimation = i > this.currentNumber;
            this.currentNumber = i;
            this.progress = 0.0f;
            int a = 0;
            while (a < text.length()) {
                String ch = text.substring(a, a + 1);
                String oldCh = (this.oldLetters.isEmpty() || a >= oldText.length()) ? null : oldText.substring(a, a + 1);
                if (oldCh == null || !oldCh.equals(ch)) {
                    TextPaint textPaint2 = this.textPaint;
                    String str = oldCh;
                    this.letters.add(new StaticLayout(ch, textPaint2, (int) Math.ceil((double) textPaint2.measureText(ch)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false));
                } else {
                    this.letters.add(this.oldLetters.get(a));
                    this.oldLetters.set(a, (Object) null);
                }
                a++;
            }
            if (animated && !this.oldLetters.isEmpty()) {
                float[] fArr = new float[2];
                fArr[0] = forwardAnimation ? -1.0f : 1.0f;
                fArr[1] = 0.0f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
                this.animator = ofFloat;
                ofFloat.setDuration(150);
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator unused = NumberTextView.this.animator = null;
                        NumberTextView.this.oldLetters.clear();
                    }
                });
                this.animator.start();
            }
            invalidate();
        }
    }

    public void setTextSize(int size) {
        this.textPaint.setTextSize((float) AndroidUtilities.dp((float) size));
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    public void setTextColor(int value) {
        this.textPaint.setColor(value);
        invalidate();
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        this.oldLetters.clear();
        this.letters.clear();
        setNumber(this.currentNumber, false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (!this.letters.isEmpty()) {
            float height = (float) this.letters.get(0).getHeight();
            canvas.save();
            canvas.translate((float) getPaddingLeft(), (((float) getMeasuredHeight()) - height) / 2.0f);
            int count = Math.max(this.letters.size(), this.oldLetters.size());
            int a = 0;
            while (a < count) {
                canvas.save();
                StaticLayout layout = null;
                StaticLayout old = a < this.oldLetters.size() ? this.oldLetters.get(a) : null;
                if (a < this.letters.size()) {
                    layout = this.letters.get(a);
                }
                float f = this.progress;
                if (f > 0.0f) {
                    if (old != null) {
                        this.textPaint.setAlpha((int) (f * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress - 1.0f) * height);
                        old.draw(canvas);
                        canvas.restore();
                        if (layout != null) {
                            this.textPaint.setAlpha((int) ((1.0f - this.progress) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
                        }
                    } else {
                        this.textPaint.setAlpha(255);
                    }
                } else if (f < 0.0f) {
                    if (old != null) {
                        this.textPaint.setAlpha((int) ((-f) * 255.0f));
                        canvas.save();
                        canvas.translate(0.0f, (this.progress + 1.0f) * height);
                        old.draw(canvas);
                        canvas.restore();
                    }
                    if (layout != null) {
                        if (a == count - 1 || old != null) {
                            this.textPaint.setAlpha((int) ((this.progress + 1.0f) * 255.0f));
                            canvas.translate(0.0f, this.progress * height);
                        } else {
                            this.textPaint.setAlpha(255);
                        }
                    }
                } else if (layout != null) {
                    this.textPaint.setAlpha(255);
                }
                if (layout != null) {
                    layout.draw(canvas);
                }
                canvas.restore();
                canvas.translate(layout != null ? layout.getLineWidth(0) : old.getLineWidth(0) + ((float) AndroidUtilities.dp(1.0f)), 0.0f);
                a++;
            }
            canvas.restore();
        }
    }
}
