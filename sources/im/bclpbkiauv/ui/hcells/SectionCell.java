package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class SectionCell extends FrameLayout {
    private TextView righTextView;
    private TextView textView;

    public SectionCell(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor(Theme.key_graySectionText));
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
        TextView textView3 = new TextView(getContext());
        this.righTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.righTextView.setTextColor(Theme.getColor(Theme.key_graySectionText));
        this.righTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
        addView(this.righTextView, LayoutHelper.createFrame(-2.0f, -1.0f, (LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(45.0f), 1073741824));
    }

    public void setText(String text) {
        this.textView.setText(text);
        this.righTextView.setVisibility(8);
    }

    public void setText(String left, String right, View.OnClickListener onClickListener) {
        this.textView.setText(left);
        this.righTextView.setText(right);
        this.righTextView.setOnClickListener(onClickListener);
        this.righTextView.setVisibility(0);
    }
}
