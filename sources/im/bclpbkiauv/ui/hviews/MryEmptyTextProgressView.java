package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;

public class MryEmptyTextProgressView extends FrameLayout {
    private final LinearLayout emptyLayout;
    private boolean inLayout;
    private RadialProgressView progressBar;
    private boolean showAtCenter;
    private TextView textView;

    public MryEmptyTextProgressView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public MryEmptyTextProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryEmptyTextProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.emptyLayout = linearLayout;
        linearLayout.setOrientation(1);
        addView(this.emptyLayout, LayoutHelper.createFrame(-2, -2.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.textView.setGravity(17);
        this.textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), 0);
        this.textView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 1));
        setOnTouchListener($$Lambda$MryEmptyTextProgressView$k8_RwblPKOrXDaRgKd7SWSxTFE.INSTANCE);
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    public void showProgress() {
        this.emptyLayout.setVisibility(4);
        this.progressBar.setVisibility(0);
    }

    public void showTextView() {
        this.emptyLayout.setVisibility(0);
        this.progressBar.setVisibility(4);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setProgressBarColor(int color) {
        this.progressBar.setProgressColor(color);
    }

    public void setTopImage(int resId) {
        if (resId == 0) {
            this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, getContext().getResources().getDrawable(resId).mutate(), (Drawable) null, (Drawable) null);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(1.0f));
    }

    public void setTextSize(int size) {
        this.textView.setTextSize(1, (float) size);
    }

    public void setShowAtCenter(boolean value) {
        this.showAtCenter = value;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int y;
        this.inLayout = true;
        int width = r - l;
        int height = b - t;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int x = (width - child.getMeasuredWidth()) / 2;
                if (this.showAtCenter) {
                    y = (((height / 2) - child.getMeasuredHeight()) / 2) + getPaddingTop();
                } else {
                    y = ((height - child.getMeasuredHeight()) / 2) + getPaddingTop();
                }
                child.layout(x, y, child.getMeasuredWidth() + x, child.getMeasuredHeight() + y);
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
