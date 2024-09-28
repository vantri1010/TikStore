package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class TextSettingsCell extends FrameLayout {
    private boolean canDisable;
    private boolean needDivider;
    private TextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingsCell(Context context) {
        this(context, 21);
    }

    public TextSettingsCell(Context context, int padding) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) padding, 0.0f, (float) padding, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, (float) padding, 0.0f, (float) padding, 0.0f));
        ImageView imageView = new ImageView(context);
        this.valueImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.valueImageView.setVisibility(4);
        this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.valueImageView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 3 : i) | 16, (float) padding, 0.0f, (float) padding, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        int width2 = availableWidth / 2;
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        }
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
            width = (availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f);
        } else {
            width = availableWidth;
        }
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public TextView getTextView() {
        return this.textView;
    }

    public void setCanDisable(boolean value) {
        this.canDisable = value;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextValueColor(int color) {
        this.valueTextView.setTextColor(color);
    }

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(4);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueImageView.setVisibility(4);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(!divider);
        requestLayout();
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        if (resId != 0) {
            this.valueImageView.setVisibility(0);
            this.valueImageView.setImageResource(resId);
        } else {
            this.valueImageView.setVisibility(4);
        }
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        setEnabled(value);
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            fArr[0] = value ? 1.0f : 0.5f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            if (this.valueTextView.getVisibility() == 0) {
                TextView textView3 = this.valueTextView;
                float[] fArr2 = new float[1];
                fArr2[0] = value ? 1.0f : 0.5f;
                animators.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr2));
            }
            if (this.valueImageView.getVisibility() == 0) {
                ImageView imageView = this.valueImageView;
                float[] fArr3 = new float[1];
                if (!value) {
                    f = 0.5f;
                }
                fArr3[0] = f;
                animators.add(ObjectAnimator.ofFloat(imageView, "alpha", fArr3));
                return;
            }
            return;
        }
        this.textView.setAlpha(value ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha(value ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView2 = this.valueImageView;
            if (!value) {
                f = 0.5f;
            }
            imageView2.setAlpha(f);
        }
    }

    public void setEnabled(boolean value) {
        super.setEnabled(value);
        float f = 0.5f;
        this.textView.setAlpha((value || !this.canDisable) ? 1.0f : 0.5f);
        if (this.valueTextView.getVisibility() == 0) {
            this.valueTextView.setAlpha((value || !this.canDisable) ? 1.0f : 0.5f);
        }
        if (this.valueImageView.getVisibility() == 0) {
            ImageView imageView = this.valueImageView;
            if (value || !this.canDisable) {
                f = 1.0f;
            }
            imageView.setAlpha(f);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(isEnabled());
    }
}
