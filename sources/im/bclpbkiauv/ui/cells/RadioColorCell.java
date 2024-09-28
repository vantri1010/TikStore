package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadioButton;

public class RadioColorCell extends FrameLayout {
    private RadioButton radioButton;
    private TextView textView;

    public RadioColorCell(Context context) {
        super(context);
        RadioButton radioButton2 = new RadioButton(context);
        this.radioButton = radioButton2;
        radioButton2.setSize(AndroidUtilities.dp(20.0f));
        this.radioButton.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        int i = 5;
        addView(this.radioButton, LayoutHelper.createFrame(22.0f, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 18), 14.0f, (float) (LocaleController.isRTL ? 18 : 0), 0.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 21 : 51), 13.0f, (float) (LocaleController.isRTL ? 51 : 21), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
    }

    public void setCheckColor(int color1, int color2) {
        this.radioButton.setColor(color1, color2);
    }

    public void setTextAndValue(String text, boolean checked) {
        this.textView.setText(text);
        this.radioButton.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.radioButton.setChecked(checked, animated);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.RadioButton");
        info.setCheckable(true);
        info.setChecked(this.radioButton.isChecked());
    }
}
