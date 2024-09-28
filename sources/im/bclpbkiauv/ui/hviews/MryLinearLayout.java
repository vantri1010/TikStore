package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import im.bclpbkiauv.ui.hviews.helper.MryLayout;
import im.bclpbkiauv.ui.hviews.helper.MryLayoutHelper;

public class MryLinearLayout extends MryAlphaLinearLayout implements MryLayout {
    private MryLayoutHelper mLayoutHelper;

    public MryLinearLayout(Context context) {
        super(context);
        init(context, (AttributeSet) null, 0);
    }

    public MryLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MryLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mLayoutHelper = new MryLayoutHelper(context, attrs, defStyleAttr, this);
        setChangeAlphaWhenPress(false);
        setChangeAlphaWhenDisable(false);
    }

    public void updateTopDivider(int topInsetLeft, int topInsetRight, int topDividerHeight, int topDividerColor) {
        this.mLayoutHelper.updateTopDivider(topInsetLeft, topInsetRight, topDividerHeight, topDividerColor);
        invalidate();
    }

    public void updateBottomDivider(int bottomInsetLeft, int bottomInsetRight, int bottomDividerHeight, int bottomDividerColor) {
        this.mLayoutHelper.updateBottomDivider(bottomInsetLeft, bottomInsetRight, bottomDividerHeight, bottomDividerColor);
        invalidate();
    }

    public void updateLeftDivider(int leftInsetTop, int leftInsetBottom, int leftDividerWidth, int leftDividerColor) {
        this.mLayoutHelper.updateLeftDivider(leftInsetTop, leftInsetBottom, leftDividerWidth, leftDividerColor);
        invalidate();
    }

    public void updateRightDivider(int rightInsetTop, int rightInsetBottom, int rightDividerWidth, int rightDividerColor) {
        this.mLayoutHelper.updateRightDivider(rightInsetTop, rightInsetBottom, rightDividerWidth, rightDividerColor);
        invalidate();
    }

    public void onlyShowTopDivider(int topInsetLeft, int topInsetRight, int topDividerHeight, int topDividerColor) {
        this.mLayoutHelper.onlyShowTopDivider(topInsetLeft, topInsetRight, topDividerHeight, topDividerColor);
        invalidate();
    }

    public void onlyShowBottomDivider(int bottomInsetLeft, int bottomInsetRight, int bottomDividerHeight, int bottomDividerColor) {
        this.mLayoutHelper.onlyShowBottomDivider(bottomInsetLeft, bottomInsetRight, bottomDividerHeight, bottomDividerColor);
        invalidate();
    }

    public void onlyShowLeftDivider(int leftInsetTop, int leftInsetBottom, int leftDividerWidth, int leftDividerColor) {
        this.mLayoutHelper.onlyShowLeftDivider(leftInsetTop, leftInsetBottom, leftDividerWidth, leftDividerColor);
        invalidate();
    }

    public void onlyShowRightDivider(int rightInsetTop, int rightInsetBottom, int rightDividerWidth, int rightDividerColor) {
        this.mLayoutHelper.onlyShowRightDivider(rightInsetTop, rightInsetBottom, rightDividerWidth, rightDividerColor);
        invalidate();
    }

    public void setTopDividerAlpha(int dividerAlpha) {
        this.mLayoutHelper.setTopDividerAlpha(dividerAlpha);
        invalidate();
    }

    public void setBottomDividerAlpha(int dividerAlpha) {
        this.mLayoutHelper.setBottomDividerAlpha(dividerAlpha);
        invalidate();
    }

    public void setLeftDividerAlpha(int dividerAlpha) {
        this.mLayoutHelper.setLeftDividerAlpha(dividerAlpha);
        invalidate();
    }

    public void setRightDividerAlpha(int dividerAlpha) {
        this.mLayoutHelper.setRightDividerAlpha(dividerAlpha);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureSpec2 = this.mLayoutHelper.getMeasuredWidthSpec(widthMeasureSpec);
        int heightMeasureSpec2 = this.mLayoutHelper.getMeasuredHeightSpec(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec2, heightMeasureSpec2);
        int minW = this.mLayoutHelper.handleMiniWidth(widthMeasureSpec2, getMeasuredWidth());
        int minH = this.mLayoutHelper.handleMiniHeight(heightMeasureSpec2, getMeasuredHeight());
        if (widthMeasureSpec2 != minW || heightMeasureSpec2 != minH) {
            super.onMeasure(minW, minH);
        }
    }

    public void setRadiusAndShadow(int radius, int shadowElevation, float shadowAlpha) {
        this.mLayoutHelper.setRadiusAndShadow(radius, shadowElevation, shadowAlpha);
    }

    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, float shadowAlpha) {
        this.mLayoutHelper.setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, shadowAlpha);
    }

    public void setRadiusAndShadow(int radius, int hideRadiusSide, int shadowElevation, int shadowColor, float shadowAlpha) {
        this.mLayoutHelper.setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, shadowColor, shadowAlpha);
    }

    public void setRadius(int radius) {
        this.mLayoutHelper.setRadius(radius);
    }

    public void setRadius(int radius, int hideRadiusSide) {
        this.mLayoutHelper.setRadius(radius, hideRadiusSide);
    }

    public int getRadius() {
        return this.mLayoutHelper.getRadius();
    }

    public void setOutlineInset(int left, int top, int right, int bottom) {
        this.mLayoutHelper.setOutlineInset(left, top, right, bottom);
    }

    public void setBorderColor(int borderColor) {
        this.mLayoutHelper.setBorderColor(borderColor);
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.mLayoutHelper.setBorderWidth(borderWidth);
        invalidate();
    }

    public void setShowBorderOnlyBeforeL(boolean showBorderOnlyBeforeL) {
        this.mLayoutHelper.setShowBorderOnlyBeforeL(showBorderOnlyBeforeL);
        invalidate();
    }

    public void setHideRadiusSide(int hideRadiusSide) {
        this.mLayoutHelper.setHideRadiusSide(hideRadiusSide);
    }

    public int getHideRadiusSide() {
        return this.mLayoutHelper.getHideRadiusSide();
    }

    public boolean setWidthLimit(int widthLimit) {
        if (!this.mLayoutHelper.setWidthLimit(widthLimit)) {
            return true;
        }
        requestLayout();
        invalidate();
        return true;
    }

    public boolean setHeightLimit(int heightLimit) {
        if (!this.mLayoutHelper.setHeightLimit(heightLimit)) {
            return true;
        }
        requestLayout();
        invalidate();
        return true;
    }

    public void setUseThemeGeneralShadowElevation() {
        this.mLayoutHelper.setUseThemeGeneralShadowElevation();
    }

    public void setOutlineExcludePadding(boolean outlineExcludePadding) {
        this.mLayoutHelper.setOutlineExcludePadding(outlineExcludePadding);
    }

    public void updateBottomSeparatorColor(int color) {
        this.mLayoutHelper.updateBottomSeparatorColor(color);
    }

    public void updateLeftSeparatorColor(int color) {
        this.mLayoutHelper.updateLeftSeparatorColor(color);
    }

    public void updateRightSeparatorColor(int color) {
        this.mLayoutHelper.updateRightSeparatorColor(color);
    }

    public void updateTopSeparatorColor(int color) {
        this.mLayoutHelper.updateTopSeparatorColor(color);
    }

    public void setShadowElevation(int elevation) {
        this.mLayoutHelper.setShadowElevation(elevation);
    }

    public int getShadowElevation() {
        return this.mLayoutHelper.getShadowElevation();
    }

    public void setShadowAlpha(float shadowAlpha) {
        this.mLayoutHelper.setShadowAlpha(shadowAlpha);
    }

    public void setShadowColor(int shadowColor) {
        this.mLayoutHelper.setShadowColor(shadowColor);
    }

    public int getShadowColor() {
        return this.mLayoutHelper.getShadowColor();
    }

    public void setOuterNormalColor(int color) {
        this.mLayoutHelper.setOuterNormalColor(color);
    }

    public float getShadowAlpha() {
        return this.mLayoutHelper.getShadowAlpha();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.mLayoutHelper.drawDividers(canvas, getWidth(), getHeight());
        this.mLayoutHelper.dispatchRoundBorderDraw(canvas);
    }

    public boolean hasBorder() {
        return this.mLayoutHelper.hasBorder();
    }

    public boolean hasLeftSeparator() {
        return this.mLayoutHelper.hasLeftSeparator();
    }

    public boolean hasTopSeparator() {
        return this.mLayoutHelper.hasTopSeparator();
    }

    public boolean hasRightSeparator() {
        return this.mLayoutHelper.hasRightSeparator();
    }

    public boolean hasBottomSeparator() {
        return this.mLayoutHelper.hasBottomSeparator();
    }
}
