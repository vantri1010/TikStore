package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class MryRoundButton extends MryAlphaButton {
    private boolean isStroke;
    private float mRadius;
    private float mRadiusBottomLeft;
    private float mRadiusBottomRight;
    private float mRadiusTopLeft;
    private float mRadiusTopRight;
    private MryRoundButtonDrawable mRoundBg;

    public MryRoundButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryRoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryRoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MryRoundButton, defStyleAttr, 0);
        ColorStateList colorBg = typedArray.getColorStateList(0);
        ColorStateList colorBorder = typedArray.getColorStateList(1);
        int borderWidth = typedArray.getDimensionPixelSize(2, 0);
        boolean isRadiusAdjustBounds = typedArray.getBoolean(4, false);
        this.isStroke = typedArray.getBoolean(3, true);
        this.mRadius = (float) typedArray.getDimensionPixelSize(5, 0);
        this.mRadiusTopLeft = (float) typedArray.getDimensionPixelSize(8, 0);
        this.mRadiusTopRight = (float) typedArray.getDimensionPixelSize(9, 0);
        this.mRadiusBottomLeft = (float) typedArray.getDimensionPixelSize(6, 0);
        this.mRadiusBottomRight = (float) typedArray.getDimensionPixelSize(7, 0);
        typedArray.recycle();
        MryRoundButtonDrawable mryRoundButtonDrawable = new MryRoundButtonDrawable();
        this.mRoundBg = mryRoundButtonDrawable;
        mryRoundButtonDrawable.setBgData(colorBg);
        this.mRoundBg.setStrokeData(borderWidth, colorBorder);
        setIsRadiusAdjustBounds(isRadiusAdjustBounds);
        setRadius(this.mRadiusTopLeft, this.mRadiusTopRight, this.mRadiusBottomLeft, this.mRadiusBottomRight);
        if (this.isStroke) {
            setPrimaryRoundStrokeStyle(this.mRadius);
        } else {
            setPrimaryRoundFillStyle(this.mRadius);
        }
        setBackground(this.mRoundBg);
        int[] padding = {getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom()};
        int i = padding[3];
        padding[2] = i;
        padding[1] = i;
        padding[0] = i;
        if (i == 0) {
            setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        } else {
            setPadding(padding[0], padding[1], padding[2], padding[3]);
        }
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        setGravity(17);
    }

    public void setBackgroundColor(int color) {
        this.mRoundBg.setBgData(ColorStateList.valueOf(color));
    }

    public void setBackgroundColor(String colorThemeKey) {
        this.mRoundBg.setBgData(ColorStateList.valueOf(Theme.getColor(colorThemeKey)));
    }

    public void resetBackground() {
        setBackground(this.mRoundBg);
    }

    public void setBgData(ColorStateList colors) {
        this.mRoundBg.setBgData(colors);
    }

    public void setStrokeData(int width, String strokeColorKey) {
        setStrokeData(width, Theme.getColor(strokeColorKey));
    }

    public void setStrokeData(int width, int strokeColor) {
        setStrokeData(width, ColorStateList.valueOf(strokeColor));
    }

    public void setStrokeData(int width, ColorStateList colors) {
        this.mRoundBg.setStrokeData(width, colors);
    }

    public void setStrokeWidth(int width) {
        this.mRoundBg.setStrokeWidth(width);
    }

    public int getStrokeWidth() {
        return this.mRoundBg.getStrokeWidth();
    }

    public void setStrokeColors(ColorStateList colors) {
        this.mRoundBg.setStrokeColors(colors);
    }

    public void setIsRadiusAdjustBounds(boolean isRadiusAdjustBounds) {
        this.mRoundBg.setIsRadiusAdjustBounds(isRadiusAdjustBounds);
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        setRadius(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void setRadius(float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        this.mRadiusTopLeft = topLeftRadius;
        this.mRadiusTopRight = topRightRadius;
        this.mRadiusBottomLeft = bottomLeftRadius;
        this.mRadiusBottomRight = bottomRightRadius;
        if (topLeftRadius > 0.0f || topRightRadius > 0.0f || bottomLeftRadius > 0.0f || bottomRightRadius > 0.0f) {
            float f = this.mRadiusTopLeft;
            float f2 = this.mRadiusTopRight;
            float f3 = this.mRadiusBottomLeft;
            float f4 = this.mRadiusBottomRight;
            this.mRoundBg.setCornerRadii(new float[]{f, f, f2, f2, f3, f3, f4, f4});
            return;
        }
        this.mRoundBg.setCornerRadius(this.mRadius);
        if (this.mRadius == 0.0f) {
            this.mRoundBg.setIsRadiusAdjustBounds(false);
        }
    }

    public void setPrimaryRoundFillStyle(float radius) {
        setStrokeData(0, 0);
        setRadius(radius);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton));
        setTextColor(-1);
    }

    public void setPrimaryRoundStrokeStyle(float radius) {
        setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
        setRadius(radius);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
    }

    public void setPrimaryRadiusAdjustBoundsFillStyle() {
        setStrokeData(0, 0);
        setIsRadiusAdjustBounds(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton));
        setTextColor(-1);
    }

    public void setPrimaryRadiusAdjustBoundsStrokeStyle() {
        setStrokeData(1, ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton)));
        setIsRadiusAdjustBounds(true);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
    }

    public void setRoundBgGradientColors(int[] colorArr) {
        this.mRoundBg.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        this.mRoundBg.setGradientType(0);
        this.mRoundBg.setColors(colorArr);
        setBackground(this.mRoundBg);
    }

    public void setRadiusAdjustBoundsFillStyle(String bgColorKey) {
        setRadiusAdjustBoundsFillStyle(Theme.getColor(bgColorKey));
    }

    public void setRadiusAdjustBoundsFillStyle(int bgColorKey) {
        setRadiusAdjustBoundsFillStyle(bgColorKey, -1);
    }

    public void setRadiusAdjustBoundsFillStyle(String bgColorKey, String textColorKey) {
        setRadiusAdjustBoundsFillStyle(Theme.getColor(bgColorKey), textColorKey);
    }

    public void setRadiusAdjustBoundsFillStyle(int bgColorKey, String textColorKey) {
        setRadiusAdjustBoundsFillStyle(bgColorKey, Theme.getColor(textColorKey));
    }

    public void setRadiusAdjustBoundsFillStyle(String bgColorKey, int textColor) {
        setRadiusAdjustBoundsFillStyle(Theme.getColor(bgColorKey), textColor);
    }

    public void setRadiusAdjustBoundsFillStyle(int bgColorKey, int textColor) {
        setStrokeData(0, 0);
        setIsRadiusAdjustBounds(true);
        setBackgroundColor(bgColorKey);
        setTextColor(textColor);
    }
}
