package im.bclpbkiauv.ui.actionbar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class ChatActionBarMenuSubItem extends LinearLayout {
    private ImageView imageView;
    private MryTextView textView;

    public ChatActionBarMenuSubItem(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setGravity(17);
        setOrientation(1);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueIcon), PorterDuff.Mode.MULTIPLY);
        addView(this.imageView, LayoutHelper.createLinear(20, 20));
        MryTextView mryTextView = new MryTextView(context);
        this.textView = mryTextView;
        mryTextView.setTextSize(12.0f);
        this.textView.setMaxLines(1);
        this.textView.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        addView(this.textView, LayoutHelper.createLinear(-2, -2, 0.0f, (float) AndroidUtilities.dp(2.0f), 0.0f, 0.0f));
    }

    public void setTextAndIcon(CharSequence text, int icon) {
        this.textView.setText(text);
        if (icon != 0) {
            this.imageView.setImageResource(icon);
            this.imageView.setVisibility(0);
            return;
        }
        this.imageView.setVisibility(4);
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

    public void setIcon(int resId) {
        this.imageView.setImageResource(resId);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
