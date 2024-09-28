package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class TextInfoCell extends FrameLayout {
    private TextView textView;

    public TextInfoCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5));
        this.textView.setTextSize(1, 13.0f);
        this.textView.setGravity(17);
        this.textView.setPadding(0, AndroidUtilities.dp(19.0f), 0, AndroidUtilities.dp(19.0f));
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, 17, 17.0f, 0.0f, 17.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
