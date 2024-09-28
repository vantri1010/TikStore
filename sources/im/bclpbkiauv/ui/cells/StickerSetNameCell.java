package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.ColorSpanUnderline;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class StickerSetNameCell extends FrameLayout {
    private ImageView buttonView;
    private boolean empty;
    private boolean isEmoji;
    private TextView textView;
    private TextView urlTextView;

    public StickerSetNameCell(Context context, boolean emoji) {
        super(context);
        this.isEmoji = emoji;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setSingleLine(true);
        addView(this.textView, LayoutHelper.createFrame(-2.0f, -2.0f, 51, emoji ? 15.0f : 17.0f, 4.0f, 57.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.urlTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName));
        this.urlTextView.setTextSize(1, 12.0f);
        this.urlTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.urlTextView.setSingleLine(true);
        this.urlTextView.setVisibility(4);
        addView(this.urlTextView, LayoutHelper.createFrame(-2.0f, -2.0f, 53, 17.0f, 6.0f, 17.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.buttonView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiPanelStickerSetNameIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.buttonView, LayoutHelper.createFrame(24.0f, 24.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
    }

    public void setUrl(CharSequence text, int searchLength) {
        if (text != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            try {
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_chat_emojiPanelStickerSetNameHighlight)), 0, searchLength, 33);
                builder.setSpan(new ColorSpanUnderline(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName)), searchLength, text.length(), 33);
            } catch (Exception e) {
            }
            this.urlTextView.setText(builder);
            this.urlTextView.setVisibility(0);
            return;
        }
        this.urlTextView.setVisibility(8);
    }

    public void setText(CharSequence text, int resId) {
        setText(text, resId, 0, 0);
    }

    public void setText(CharSequence text, int resId, int index, int searchLength) {
        if (text == null) {
            this.empty = true;
            this.textView.setText("");
            this.buttonView.setVisibility(4);
            return;
        }
        if (searchLength != 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            try {
                builder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_chat_emojiPanelStickerSetNameHighlight)), index, index + searchLength, 33);
            } catch (Exception e) {
            }
            this.textView.setText(builder);
        } else {
            TextView textView2 = this.textView;
            textView2.setText(Emoji.replaceEmoji(text, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
        }
        if (resId != 0) {
            this.buttonView.setImageResource(resId);
            this.buttonView.setVisibility(0);
            return;
        }
        this.buttonView.setVisibility(4);
    }

    public void setOnIconClickListener(View.OnClickListener onIconClickListener) {
        this.buttonView.setOnClickListener(onIconClickListener);
    }

    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.empty) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(1, 1073741824));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.isEmoji ? 28.0f : 24.0f), 1073741824));
        }
    }
}
