package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ManageChatTextCell extends FrameLayout {
    private boolean divider;
    private ImageView imageView;
    private SimpleTextView textView;
    private SimpleTextView valueTextView;

    public ManageChatTextCell(Context context) {
        super(context);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(16);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView);
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.valueTextView = simpleTextView2;
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        this.valueTextView.setTextSize(16);
        this.valueTextView.setGravity(LocaleController.isRTL ? 3 : i);
        addView(this.valueTextView);
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        addView(this.imageView);
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public SimpleTextView getValueTextView() {
        return this.valueTextView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = AndroidUtilities.dp(48.0f);
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(24.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(95.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        setMeasuredDimension(width, AndroidUtilities.dp(56.0f) + (this.divider ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getTextHeight()) / 2;
        int viewLeft = LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0;
        SimpleTextView simpleTextView = this.valueTextView;
        simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.valueTextView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        int viewLeft2 = LocaleController.isRTL == 0 ? AndroidUtilities.dp(71.0f) : AndroidUtilities.dp(24.0f);
        SimpleTextView simpleTextView2 = this.textView;
        simpleTextView2.layout(viewLeft2, viewTop2, simpleTextView2.getMeasuredWidth() + viewLeft2, this.textView.getMeasuredHeight() + viewTop2);
        int viewTop3 = (height - this.imageView.getMeasuredHeight()) / 2;
        int viewLeft3 = !LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(21.0f);
        ImageView imageView2 = this.imageView;
        imageView2.layout(viewLeft3, viewTop3, imageView2.getMeasuredWidth() + viewLeft3, this.imageView.getMeasuredHeight() + viewTop3);
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public void setColors(String icon, String text) {
        this.textView.setTextColor(Theme.getColor(text));
        this.textView.setTag(text);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon), PorterDuff.Mode.MULTIPLY));
        this.imageView.setTag(icon);
    }

    public void setText(String text, String value, int resId, boolean needDivider) {
        this.textView.setText(text);
        if (value != null) {
            this.valueTextView.setText(value);
            this.valueTextView.setVisibility(0);
        } else {
            this.valueTextView.setVisibility(4);
        }
        this.imageView.setImageResource(resId);
        this.divider = needDivider;
        setWillNotDraw(!needDivider);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.divider) {
            canvas.drawLine((float) AndroidUtilities.dp(71.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
