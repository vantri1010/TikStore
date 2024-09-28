package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.MrySwitch;

public class NotificationsCheckCell extends FrameLayout {
    private MrySwitch checkBox;
    private int currentHeight;
    private boolean drawLine;
    private boolean isMultiline;
    private boolean needDivider;
    private TextView textView;
    private TextView valueTextView;

    public NotificationsCheckCell(Context context) {
        this(context, 21, 70);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NotificationsCheckCell(Context context, int padding, int height) {
        super(context);
        Context context2 = context;
        this.drawLine = true;
        setWillNotDraw(false);
        this.currentHeight = height;
        TextView textView2 = new TextView(context2);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 21.0f, (float) (((this.currentHeight - 70) / 2) + 13), LocaleController.isRTL ? 21.0f : 80.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.valueTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setPadding(0, 0, 0, 0);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 21.0f, (float) (((this.currentHeight - 70) / 2) + 38), LocaleController.isRTL ? 21.0f : 100.0f, 0.0f));
        MrySwitch mrySwitch = new MrySwitch(context2);
        this.checkBox = mrySwitch;
        mrySwitch.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
        int iWidth = getResources().getDimensionPixelOffset(R.dimen.switch_width);
        addView(this.checkBox, LayoutHelper.createFrameByPx(iWidth, AndroidUtilities.dp(40.0f), (LocaleController.isRTL ? 3 : i) | 16, AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(61.0f) - iWidth, 0));
        this.checkBox.setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isMultiline) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.currentHeight), 1073741824));
        }
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, boolean divider) {
        setTextAndValueAndCheck(text, value, checked, 0, false, divider);
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, int iconType, boolean divider) {
        setTextAndValueAndCheck(text, value, checked, iconType, false, divider);
    }

    public void setTextAndValueAndCheck(String text, CharSequence value, boolean checked, int iconType, boolean multiline, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.checkBox.setChecked(checked, iconType, false);
        this.valueTextView.setVisibility(0);
        this.needDivider = divider;
        this.isMultiline = multiline;
        if (multiline) {
            this.valueTextView.setLines(0);
            this.valueTextView.setMaxLines(0);
            this.valueTextView.setSingleLine(false);
            this.valueTextView.setEllipsize((TextUtils.TruncateAt) null);
            this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        } else {
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setPadding(0, 0, 0, 0);
        }
        this.checkBox.setContentDescription(text);
    }

    public void setTextAndValueAndCheck(String text, boolean checked, boolean divider) {
        this.textView.setText(text);
        this.checkBox.setChecked(checked, 0, false);
        this.valueTextView.setVisibility(8);
        this.needDivider = divider;
        this.isMultiline = false;
        this.checkBox.setContentDescription(text);
    }

    public void setDrawLine(boolean value) {
        this.drawLine = value;
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked, true);
    }

    public void setChecked(boolean checked, int iconType) {
        this.checkBox.setChecked(checked, iconType, true);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
        if (this.drawLine) {
            int x = LocaleController.isRTL ? AndroidUtilities.dp(76.0f) : (getMeasuredWidth() - AndroidUtilities.dp(76.0f)) - 1;
            int y = (getMeasuredHeight() - AndroidUtilities.dp(22.0f)) / 2;
            canvas.drawRect((float) x, (float) y, (float) (x + 2), (float) (AndroidUtilities.dp(22.0f) + y), Theme.dividerPaint);
        }
    }
}
