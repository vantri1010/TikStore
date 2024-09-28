package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class TextSettingCell extends FrameLayout {
    private ImageView imageView;
    private int leftPadding;
    private boolean needDivider;
    private SimpleTextView textView;
    private ImageView valueImageView;
    private TextView valueTextView;

    public TextSettingCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public TextSettingCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSettingCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCell(context, 23);
    }

    public void initCell(Context context, int left) {
        this.leftPadding = left;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(14);
        int i = 5;
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 16));
        TextView textView2 = new TextView(context);
        this.valueTextView = textView2;
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        this.valueTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundValueText1));
        this.valueTextView.setTextSize(2, 12.0f);
        this.valueTextView.setLineSpacing((float) AndroidUtilities.dp(5.0f), 1.0f);
        TextView textView3 = this.valueTextView;
        if (LocaleController.isRTL) {
            i = 3;
        }
        textView3.setGravity(i);
        this.valueTextView.setPadding(0, 0, AndroidUtilities.dp(16.0f), 0);
        addView(this.valueTextView, LayoutHelper.createFrame(-2, -2, 16));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.imageView, LayoutHelper.createFrame(-2, -2, 16));
        ImageView imageView3 = new ImageView(context);
        this.valueImageView = imageView3;
        imageView3.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.valueImageView, LayoutHelper.createFrame(-2, -2, 16));
        setFocusable(true);
        setMinimumHeight(AndroidUtilities.dp(55.0f));
    }

    public SimpleTextView getTextView() {
        return this.textView;
    }

    public TextView getValueTextView() {
        return this.valueTextView;
    }

    public ImageView getValueImageView() {
        return this.valueImageView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp((float) (this.leftPadding + 71)), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
        if (this.imageView.getVisibility() == 0) {
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(55.0f), Integer.MIN_VALUE));
        }
        if (this.valueImageView.getVisibility() == 0) {
            this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(55.0f), Integer.MIN_VALUE));
        }
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec((((width - AndroidUtilities.dp((float) (this.leftPadding + 20))) - this.textView.getTextWidth()) - this.imageView.getMeasuredWidth()) - this.valueImageView.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
        if (this.valueTextView.getMeasuredHeight() > AndroidUtilities.dp(55.0f)) {
            TextView textView2 = this.valueTextView;
            textView2.setPadding(textView2.getPaddingLeft(), AndroidUtilities.dp(10.0f), this.valueTextView.getPaddingRight(), AndroidUtilities.dp(10.0f));
            this.valueTextView.invalidate();
        }
        if (this.valueTextView.getMeasuredHeight() > this.textView.getMeasuredHeight()) {
            height = this.valueTextView.getMeasuredHeight();
        } else {
            height = this.textView.getMeasuredHeight();
        }
        if (height < AndroidUtilities.dp(50.0f)) {
            height = AndroidUtilities.dp(50.0f);
        }
        setMeasuredDimension(width, (this.needDivider ? 1 : 0) + height);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int viewLeft;
        int viewLeft2;
        int height = bottom - top;
        int width = right - left;
        int viewTop = (height - this.valueTextView.getMeasuredHeight()) / 2;
        if (LocaleController.isRTL) {
            viewLeft = AndroidUtilities.dp((float) this.leftPadding);
        } else {
            viewLeft = this.textView.getTextWidth() + (this.imageView.getVisibility() == 0 ? this.imageView.getMeasuredWidth() + AndroidUtilities.dp((float) (this.leftPadding + 10)) : AndroidUtilities.dp((float) (this.leftPadding + 10)));
        }
        TextView textView2 = this.valueTextView;
        textView2.layout(viewLeft, viewTop, textView2.getMeasuredWidth() + viewLeft, this.valueTextView.getMeasuredHeight() + viewTop);
        int viewTop2 = (height - this.textView.getTextHeight()) / 2;
        if (LocaleController.isRTL != 0) {
            viewLeft2 = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 71.0f : (float) this.leftPadding);
        } else {
            viewLeft2 = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 60.0f : (float) this.leftPadding);
        }
        SimpleTextView simpleTextView = this.textView;
        simpleTextView.layout(viewLeft2, viewTop2, simpleTextView.getMeasuredWidth() + viewLeft2, this.textView.getMeasuredHeight() + viewTop2);
        if (this.imageView.getVisibility() == 0) {
            int viewTop3 = (height - this.imageView.getMeasuredHeight()) / 2;
            int viewLeft3 = !LocaleController.isRTL ? AndroidUtilities.dp(21.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(21.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(viewLeft3, viewTop3, imageView2.getMeasuredWidth() + viewLeft3, this.imageView.getMeasuredHeight() + viewTop3);
        }
        if (this.valueImageView.getVisibility() == 0) {
            int viewTop4 = (height - this.valueImageView.getMeasuredHeight()) / 2;
            int viewLeft4 = LocaleController.isRTL ? AndroidUtilities.dp(23.0f) : (width - this.valueImageView.getMeasuredWidth()) - AndroidUtilities.dp(16.0f);
            ImageView imageView3 = this.valueImageView;
            imageView3.layout(viewLeft4, viewTop4, imageView3.getMeasuredWidth() + viewLeft4, this.valueImageView.getMeasuredHeight() + viewTop4);
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

    public void setText(String text, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setText(String text, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        int i = 8;
        this.imageView.setVisibility(8);
        this.valueTextView.setVisibility(8);
        ImageView imageView2 = this.valueImageView;
        if (arrow) {
            i = 0;
        }
        imageView2.setVisibility(i);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndIcon(String text, int resId, int rightId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.imageView.setImageResource(resId);
        this.imageView.setVisibility(0);
        this.valueTextView.setVisibility(8);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageResource(rightId);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        this.valueImageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValue(String text, String value, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        int i = 0;
        this.valueTextView.setVisibility(0);
        this.imageView.setVisibility(8);
        ImageView imageView2 = this.valueImageView;
        if (!arrow) {
            i = 8;
        }
        imageView2.setVisibility(i);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(8);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(arrow ? 0 : 8);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.imageView.setVisibility(4);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, int resId, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setVisibility(4);
        this.valueImageView.setVisibility(arrow ? 0 : 8);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        this.valueTextView.setVisibility(0);
        this.valueImageView.setVisibility(arrow ? 0 : 8);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.imageView.setVisibility(0);
        this.imageView.setImageResource(resId);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, String value, boolean divider, boolean arrow) {
        this.textView.setText(text);
        this.valueTextView.setText(value);
        int i = 0;
        this.valueTextView.setVisibility(0);
        ImageView imageView2 = this.valueImageView;
        if (!arrow) {
            i = 8;
        }
        imageView2.setVisibility(i);
        this.valueImageView.setImageResource(R.mipmap.icon_arrow_right);
        this.imageView.setVisibility(8);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setTextAndValueDrawable(String text, Drawable drawable, boolean divider) {
        this.textView.setText(text);
        this.valueTextView.setText((CharSequence) null);
        this.valueImageView.setVisibility(0);
        this.valueImageView.setImageDrawable(drawable);
        this.valueTextView.setVisibility(8);
        this.imageView.setVisibility(8);
        this.imageView.setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
        this.needDivider = divider;
        setWillNotDraw(!divider);
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
