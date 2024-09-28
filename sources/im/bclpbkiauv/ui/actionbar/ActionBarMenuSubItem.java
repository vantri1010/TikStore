package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ActionBarMenuSubItem extends FrameLayout {
    private ImageView imageView;
    private TextView textView;

    public ActionBarMenuSubItem(Context context) {
        super(context);
        setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
        setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(20.0f), 0);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.MULTIPLY));
        int i = 5;
        addView(this.imageView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        this.textView.setTextSize(1, 15.0f);
        addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        this.textView.setText(text);
        if (icon != 0) {
            this.imageView.setImageResource(icon);
            this.imageView.setVisibility(0);
            this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0);
            return;
        }
        this.imageView.setVisibility(4);
        this.textView.setPadding(0, 0, 0, 0);
    }

    public void setColors(int text, int icon) {
        this.textView.setTextColor(text);
        this.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setIconColor(int color) {
        this.imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setIconColor(int color, PorterDuff.Mode mode) {
        this.imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    public void setIcon(int resId) {
        this.imageView.setImageResource(resId);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
