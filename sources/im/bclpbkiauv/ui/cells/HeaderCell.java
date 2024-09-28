package im.bclpbkiauv.ui.cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class HeaderCell extends FrameLayout {
    private int height;
    private TextView textView;
    private SimpleTextView textView2;

    public HeaderCell(Context context) {
        this(context, false, 21, 15, false);
    }

    public HeaderCell(Context context, int padding) {
        this(context, false, padding, 15, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public HeaderCell(Context context, boolean dialog, int padding, int topMargin, boolean text2) {
        super(context);
        int i = padding;
        int i2 = topMargin;
        this.height = 40;
        TextView textView3 = new TextView(getContext());
        this.textView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i3 = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setMinHeight(AndroidUtilities.dp((float) (this.height - i2)));
        if (dialog) {
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        } else {
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
        }
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) i, (float) i2, (float) i, 0.0f));
        if (text2) {
            SimpleTextView simpleTextView = new SimpleTextView(getContext());
            this.textView2 = simpleTextView;
            simpleTextView.setTextSize(12);
            this.textView2.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            addView(this.textView2, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 3 : i3) | 48, (float) i, 21.0f, (float) i, 0.0f));
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setHeight(int value) {
        TextView textView3 = this.textView;
        this.height = value;
        textView3.setMinHeight(AndroidUtilities.dp((float) value) - ((FrameLayout.LayoutParams) this.textView.getLayoutParams()).topMargin);
    }

    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        float f = 1.0f;
        if (animators != null) {
            TextView textView3 = this.textView;
            float[] fArr = new float[1];
            if (!value) {
                f = 0.5f;
            }
            fArr[0] = f;
            animators.add(ObjectAnimator.ofFloat(textView3, "alpha", fArr));
            return;
        }
        TextView textView4 = this.textView;
        if (!value) {
            f = 0.5f;
        }
        textView4.setAlpha(f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setText2(String text) {
        SimpleTextView simpleTextView = this.textView2;
        if (simpleTextView != null) {
            simpleTextView.setText(text);
        }
    }

    public SimpleTextView getTextView2() {
        return this.textView2;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        AccessibilityNodeInfo.CollectionItemInfo collection;
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= 19 && (collection = info.getCollectionItemInfo()) != null) {
            info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(collection.getRowIndex(), collection.getRowSpan(), collection.getColumnIndex(), collection.getColumnSpan(), true));
        }
    }
}
