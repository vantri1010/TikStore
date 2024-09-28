package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class TextColorCell extends FrameLayout {
    private static Paint colorPaint;
    public static final int[] colors = {-1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925};
    public static final int[] colorsToSave = {SupportMenu.CATEGORY_MASK, -29183, InputDeviceCompat.SOURCE_ANY, -16711936, -16711681, -16776961, -2984711, -65281, -1};
    private float alpha = 1.0f;
    private int currentColor;
    private boolean needDivider;
    private TextView textView;

    public TextColorCell(Context context) {
        super(context);
        if (colorPaint == null) {
            colorPaint = new Paint(1);
        }
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    public void setAlpha(float value) {
        this.alpha = value;
        invalidate();
    }

    public float getAlpha() {
        return this.alpha;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setTextAndColor(String text, int color, boolean divider) {
        this.textView.setText(text);
        this.needDivider = divider;
        this.currentColor = color;
        setWillNotDraw(!divider && color == 0);
        invalidate();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            float[] fArr2 = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr2[0] = f;
            animators.add(ObjectAnimator.ofFloat(this, "alpha", fArr2));
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (!value) {
            f = 0.5f;
        }
        setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        int i = this.currentColor;
        if (i != 0) {
            colorPaint.setColor(i);
            colorPaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) (LocaleController.isRTL ? AndroidUtilities.dp(33.0f) : getMeasuredWidth() - AndroidUtilities.dp(33.0f)), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(10.0f), colorPaint);
        }
    }
}
