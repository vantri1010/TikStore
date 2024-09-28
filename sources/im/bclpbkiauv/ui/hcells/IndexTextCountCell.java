package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;

public class IndexTextCountCell extends FrameLayout {
    private TextView countText;
    private ImageView imageView;
    private boolean needDivider;
    private SimpleTextView textView;

    public IndexTextCountCell(Context context) {
        super(context);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.imageView.setBackgroundResource(R.mipmap.fmt_contacts_icon_bg);
        addView(this.imageView);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(14);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        TextView textView2 = new TextView(context);
        this.countText = textView2;
        textView2.setTextColor(-1);
        this.countText.setTextSize(12.0f);
        this.countText.setGravity(17);
        this.countText.setText("99");
        this.countText.setBackgroundResource(R.drawable.shape_contacts_unread);
        addView(this.countText);
        setFocusable(true);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), Integer.MIN_VALUE));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        if (this.countText.getVisibility() == 0) {
            this.countText.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0f), 1073741824));
        }
        setMeasuredDimension(width, AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - AndroidUtilities.dp(36.5f)) / 2;
        int viewLeft = AndroidUtilities.dp(15.0f);
        this.imageView.layout(viewLeft, viewTop, AndroidUtilities.dp(36.5f) + viewLeft, AndroidUtilities.dp(36.5f) + viewTop);
        int viewLeft2 = AndroidUtilities.dp(60.0f);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(viewLeft2, viewTop2, simpleTextView.getMeasuredWidth() + viewLeft2, this.textView.getMeasuredHeight() + viewTop2);
        if (this.countText.getVisibility() == 0) {
            int viewTop3 = (height - this.countText.getMeasuredHeight()) / 2;
            int viewLeft3 = (width - this.countText.getMeasuredWidth()) - AndroidUtilities.dp(36.0f);
            TextView textView2 = this.countText;
            textView2.layout(viewLeft3, viewTop3, textView2.getMeasuredWidth() + viewLeft3, this.countText.getMeasuredHeight() + viewTop3);
        }
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setColors(String icon, String text) {
        this.textView.setTextColor(Theme.getColor(text));
        this.textView.setTag(text);
        if (icon != null) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon), PorterDuff.Mode.MULTIPLY));
            this.imageView.setTag(icon);
        }
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.imageView.setImageResource(resId);
        this.textView.setText(text);
        this.needDivider = divider;
    }

    public void setCount(int count) {
        this.countText.setVisibility(count <= 0 ? 8 : 0);
        this.countText.setText(String.valueOf(count));
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f;
        int i;
        if (this.needDivider) {
            float f2 = 68.0f;
            if (LocaleController.isRTL) {
                f = 0.0f;
            } else {
                f = (float) AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 68.0f : 20.0f);
            }
            float measuredHeight = (float) (getMeasuredHeight() - 1);
            int measuredWidth = getMeasuredWidth();
            if (LocaleController.isRTL) {
                if (this.imageView.getVisibility() != 0) {
                    f2 = 20.0f;
                }
                i = AndroidUtilities.dp(f2);
            } else {
                i = 0;
            }
            canvas.drawLine(f, measuredHeight, (float) (measuredWidth - i), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
