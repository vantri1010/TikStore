package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class TextPriceCell extends FrameLayout {
    private TextView textView;
    private TextView valueTextView;

    public TextPriceCell(Context context) {
        super(context);
        setWillNotDraw(false);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.valueTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.valueTextView, LayoutHelper.createFrame(-2.0f, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 21.0f, 0.0f, 21.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(40.0f));
        int availableWidth = ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - AndroidUtilities.dp(34.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth / 2, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec((availableWidth - this.valueTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextValueColor(int color) {
        this.valueTextView.setTextColor(color);
    }

    public void setTextAndValue(String text, String value, boolean bold) {
        this.textView.setText(text);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        if (bold) {
            setTag(Theme.key_windowBackgroundWhiteBlackText);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        } else {
            setTag(Theme.key_windowBackgroundWhiteGrayText2);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.textView.setTypeface(Typeface.DEFAULT);
            this.valueTextView.setTypeface(Typeface.DEFAULT);
        }
        requestLayout();
    }
}
