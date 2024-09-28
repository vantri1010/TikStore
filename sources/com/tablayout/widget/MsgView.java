package com.tablayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.R;

public class MsgView extends TextView {
    private int backgroundColor;
    private Context context;
    private int cornerRadius;
    private GradientDrawable gd_background;
    private boolean isRadiusHalfHeight;
    private boolean isWidthHeightEqual;
    private int strokeColor;
    private int strokeWidth;

    public MsgView(Context context2) {
        this(context2, (AttributeSet) null);
    }

    public MsgView(Context context2, AttributeSet attrs) {
        this(context2, attrs, 0);
    }

    public MsgView(Context context2, AttributeSet attrs, int defStyleAttr) {
        super(context2, attrs, defStyleAttr);
        this.gd_background = new GradientDrawable();
        this.context = context2;
        obtainAttributes(context2, attrs);
    }

    private void obtainAttributes(Context context2, AttributeSet attrs) {
        TypedArray ta = context2.obtainStyledAttributes(attrs, R.styleable.MsgView);
        this.backgroundColor = ta.getColor(0, 0);
        this.cornerRadius = ta.getDimensionPixelSize(1, 0);
        this.strokeWidth = ta.getDimensionPixelSize(5, 0);
        this.strokeColor = ta.getColor(4, 0);
        this.isRadiusHalfHeight = ta.getBoolean(2, false);
        this.isWidthHeightEqual = ta.getBoolean(3, false);
        ta.recycle();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isWidthHeightEqual() || getWidth() <= 0 || getHeight() <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int measureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(getWidth(), getHeight()), 1073741824);
        super.onMeasure(measureSpec, measureSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isRadiusHalfHeight()) {
            setCornerRadius(getHeight() / 2);
        } else {
            setBgSelector();
        }
    }

    public void setBackgroundColor(int backgroundColor2) {
        this.backgroundColor = backgroundColor2;
        setBgSelector();
    }

    public void setWidth(int width) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
        lp.width = dp2px((float) width);
        lp.height = dp2px((float) width);
        setLayoutParams(lp);
    }

    public void setCornerRadius(int cornerRadius2) {
        this.cornerRadius = dp2px((float) cornerRadius2);
        setBgSelector();
    }

    public void setStrokeWidth(int strokeWidth2) {
        this.strokeWidth = dp2px((float) strokeWidth2);
        setBgSelector();
    }

    public void setStrokeColor(int strokeColor2) {
        this.strokeColor = strokeColor2;
        setBgSelector();
    }

    public void setIsRadiusHalfHeight(boolean isRadiusHalfHeight2) {
        this.isRadiusHalfHeight = isRadiusHalfHeight2;
        setBgSelector();
    }

    public void setIsWidthHeightEqual(boolean isWidthHeightEqual2) {
        this.isWidthHeightEqual = isWidthHeightEqual2;
        setBgSelector();
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getCornerRadius() {
        return this.cornerRadius;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public int getStrokeColor() {
        return this.strokeColor;
    }

    public boolean isRadiusHalfHeight() {
        return this.isRadiusHalfHeight;
    }

    public boolean isWidthHeightEqual() {
        return this.isWidthHeightEqual;
    }

    /* access modifiers changed from: protected */
    public int dp2px(float dp) {
        return (int) ((dp * this.context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    /* access modifiers changed from: protected */
    public int sp2px(float sp) {
        return (int) ((sp * this.context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    private void setDrawable(GradientDrawable gd, int color, int strokeColor2) {
        gd.setColor(color);
        gd.setCornerRadius((float) this.cornerRadius);
        gd.setStroke(this.strokeWidth, strokeColor2);
    }

    public void setBgSelector() {
        StateListDrawable bg = new StateListDrawable();
        setDrawable(this.gd_background, this.backgroundColor, this.strokeColor);
        bg.addState(new int[]{-16842919}, this.gd_background);
        if (Build.VERSION.SDK_INT >= 16) {
            setBackground(bg);
        } else {
            setBackgroundDrawable(bg);
        }
    }
}
