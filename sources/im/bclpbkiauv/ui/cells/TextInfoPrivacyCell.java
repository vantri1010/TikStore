package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private int fixedSize;
    private String linkTextColorKey;
    private TextView textView;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21);
    }

    public TextInfoPrivacyCell(Context context, int padding) {
        super(context);
        this.linkTextColorKey = Theme.key_windowBackgroundWhiteLinkText;
        this.bottomPadding = 17;
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 12.0f);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setLinkTextColor(Theme.getColor(this.linkTextColorKey));
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -2.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) padding, 0.0f, (float) padding, 0.0f));
    }

    public void setLinkTextColorKey(String key) {
        this.linkTextColorKey = key;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.fixedSize != 0) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) this.fixedSize), 1073741824));
        } else {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        }
    }

    public void setBottomPadding(int value) {
        this.bottomPadding = value;
    }

    public void setFixedSize(int size) {
        this.fixedSize = size;
    }

    public void setText(CharSequence text) {
        if (text == null) {
            this.textView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        } else {
            this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp((float) this.bottomPadding));
        }
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setTextColor(String key) {
        this.textView.setTextColor(Theme.getColor(key));
        this.textView.setTag(key);
    }

    public TextView getTextView() {
        return this.textView;
    }

    public int length() {
        return this.textView.length();
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        if (animators != null) {
            TextView textView2 = this.textView;
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView2, "alpha", fArr));
            return;
        }
        TextView textView3 = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView3.setAlpha(f);
    }
}
