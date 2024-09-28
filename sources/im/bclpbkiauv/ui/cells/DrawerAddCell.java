package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class DrawerAddCell extends FrameLayout {
    private TextView textView;

    public DrawerAddCell(Context context) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(34.0f));
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 23.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.textView.setText(LocaleController.getString("AddAccount", R.string.AddAccount));
        Drawable drawable = getResources().getDrawable(R.drawable.account_add);
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon), PorterDuff.Mode.MULTIPLY));
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
    }
}
