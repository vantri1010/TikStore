package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.CheckBoxSquare;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class CheckBoxCell extends FrameLayout {
    private CheckBoxSquare checkBox;
    private boolean isMultiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public CheckBoxCell(Context context, int type) {
        this(context, type, 17);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CheckBoxCell(Context context, int type, int padding) {
        super(context);
        Context context2 = context;
        int i = type;
        int i2 = padding;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        boolean z = true;
        textView2.setTextColor(Theme.getColor(i == 1 ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(i == 1 ? Theme.key_dialogTextLink : Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i3 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        if (i == 2) {
            addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 29), 0.0f, (float) (!LocaleController.isRTL ? 0 : 29), 0.0f));
        } else {
            addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? i2 : (i2 - 17) + 46), 0.0f, (float) (LocaleController.isRTL ? (i2 - 17) + 46 : i2), 0.0f));
        }
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(i == 1 ? Theme.key_dialogTextBlue : Theme.key_windowBackgroundWhiteValueText));
        this.valueTextView.setTextSize(1, 14.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -1.0f, (LocaleController.isRTL ? 3 : 5) | 48, (float) i2, 0.0f, (float) i2, 0.0f));
        CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, i != 1 ? false : z);
        this.checkBox = checkBoxSquare;
        if (i == 2) {
            addView(checkBoxSquare, LayoutHelper.createFrame(18.0f, 18.0f, (!LocaleController.isRTL ? 3 : i3) | 48, 0.0f, 15.0f, 0.0f, 0.0f));
        } else {
            addView(checkBoxSquare, LayoutHelper.createFrame(18.0f, 18.0f, (!LocaleController.isRTL ? 3 : i3) | 48, (float) (LocaleController.isRTL ? 0 : i2), 16.0f, (float) (LocaleController.isRTL ? i2 : 0), 0.0f));
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
            return;
        }
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), 1073741824));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setText(CharSequence text, String value, boolean checked, boolean divider) {
        this.textView.setText(text);
        this.checkBox.setChecked(checked, false);
        this.valueTextView.setText(value);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setMultiline(boolean value) {
        this.isMultiline = value;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) this.checkBox.getLayoutParams();
        if (this.isMultiline) {
            this.textView.setLines(0);
            this.textView.setMaxLines(0);
            this.textView.setSingleLine(false);
            this.textView.setEllipsize((TextUtils.TruncateAt) null);
            this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(5.0f));
            layoutParams.height = -2;
            layoutParams.topMargin = AndroidUtilities.dp(10.0f);
            layoutParams1.topMargin = AndroidUtilities.dp(12.0f);
        } else {
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, 0, 0, 0);
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            layoutParams1.topMargin = AndroidUtilities.dp(15.0f);
        }
        this.textView.setLayoutParams(layoutParams);
        this.checkBox.setLayoutParams(layoutParams1);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        float f = 1.0f;
        this.textView.setAlpha(enabled ? 1.0f : 0.5f);
        this.valueTextView.setAlpha(enabled ? 1.0f : 0.5f);
        CheckBoxSquare checkBoxSquare = this.checkBox;
        if (!enabled) {
            f = 0.5f;
        }
        checkBoxSquare.setAlpha(f);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public CheckBoxSquare getCheckBox() {
        return this.checkBox;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.CheckBox");
        info.setCheckable(true);
        info.setChecked(isChecked());
    }
}
