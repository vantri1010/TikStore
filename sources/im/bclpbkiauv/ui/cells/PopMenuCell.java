package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class PopMenuCell extends FrameLayout {
    private TextView textView;

    public PopMenuCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(29.0f));
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 19.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setTextAndIcon(String text, int resId) {
        try {
            this.textView.setText(text);
            this.textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(resId).mutate(), (Drawable) null, (Drawable) null, (Drawable) null);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
