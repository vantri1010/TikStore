package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import im.bclpbkiauv.messenger.R;

public class MryRoundButtonDrawable extends GradientDrawable {
    private ColorStateList mFillColors;
    private boolean mRadiusAdjustBounds = true;
    private ColorStateList mStrokeColors;
    private int mStrokeWidth = 0;

    public void setBgData(ColorStateList colors) {
        int currentColor;
        if (hasNativeStateListAPI()) {
            super.setColor(colors);
            return;
        }
        this.mFillColors = colors;
        if (colors == null) {
            currentColor = 0;
        } else {
            currentColor = colors.getColorForState(getState(), 0);
        }
        setColor(currentColor);
    }

    public void setStrokeData(int width, ColorStateList colors) {
        int currentColor;
        this.mStrokeWidth = width;
        this.mStrokeColors = colors;
        if (hasNativeStateListAPI()) {
            super.setStroke(width, colors);
            return;
        }
        if (colors == null) {
            currentColor = 0;
        } else {
            currentColor = colors.getColorForState(getState(), 0);
        }
        setStroke(width, currentColor);
    }

    public int getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeWidth(int width) {
        setStrokeData(width, this.mStrokeColors);
    }

    public void setStrokeColors(ColorStateList colors) {
        setStrokeData(this.mStrokeWidth, colors);
    }

    private boolean hasNativeStateListAPI() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public void setIsRadiusAdjustBounds(boolean isRadiusAdjustBounds) {
        this.mRadiusAdjustBounds = isRadiusAdjustBounds;
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] stateSet) {
        boolean superRet = super.onStateChange(stateSet);
        ColorStateList colorStateList = this.mFillColors;
        if (colorStateList != null) {
            setColor(colorStateList.getColorForState(stateSet, 0));
            superRet = true;
        }
        ColorStateList colorStateList2 = this.mStrokeColors;
        if (colorStateList2 == null) {
            return superRet;
        }
        setStroke(this.mStrokeWidth, colorStateList2.getColorForState(stateSet, 0));
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.mStrokeColors;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isStateful() {
        /*
            r1 = this;
            android.content.res.ColorStateList r0 = r1.mFillColors
            if (r0 == 0) goto L_0x000a
            boolean r0 = r0.isStateful()
            if (r0 != 0) goto L_0x001a
        L_0x000a:
            android.content.res.ColorStateList r0 = r1.mStrokeColors
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.isStateful()
            if (r0 != 0) goto L_0x001a
        L_0x0014:
            boolean r0 = super.isStateful()
            if (r0 == 0) goto L_0x001c
        L_0x001a:
            r0 = 1
            goto L_0x001d
        L_0x001c:
            r0 = 0
        L_0x001d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable.isStateful():boolean");
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        if (this.mRadiusAdjustBounds) {
            setCornerRadius((float) (Math.min(r.width(), r.height()) / 2));
        }
    }

    public static MryRoundButtonDrawable fromAttributeSet(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MryRoundButton, defStyleAttr, 0);
        ColorStateList colorBg = typedArray.getColorStateList(0);
        ColorStateList colorBorder = typedArray.getColorStateList(1);
        int borderWidth = typedArray.getDimensionPixelSize(2, 0);
        boolean isRadiusAdjustBounds = typedArray.getBoolean(4, false);
        int mRadius = typedArray.getDimensionPixelSize(5, 0);
        int mRadiusTopLeft = typedArray.getDimensionPixelSize(8, 0);
        int mRadiusTopRight = typedArray.getDimensionPixelSize(9, 0);
        int mRadiusBottomLeft = typedArray.getDimensionPixelSize(6, 0);
        int mRadiusBottomRight = typedArray.getDimensionPixelSize(7, 0);
        typedArray.recycle();
        MryRoundButtonDrawable bg = new MryRoundButtonDrawable();
        bg.setBgData(colorBg);
        bg.setStrokeData(borderWidth, colorBorder);
        if (mRadiusTopLeft > 0 || mRadiusTopRight > 0 || mRadiusBottomLeft > 0 || mRadiusBottomRight > 0) {
            TypedArray typedArray2 = typedArray;
            bg.setCornerRadii(new float[]{(float) mRadiusTopLeft, (float) mRadiusTopLeft, (float) mRadiusTopRight, (float) mRadiusTopRight, (float) mRadiusBottomRight, (float) mRadiusBottomRight, (float) mRadiusBottomLeft, (float) mRadiusBottomLeft});
            isRadiusAdjustBounds = false;
        } else {
            bg.setCornerRadius((float) mRadius);
            if (mRadius > 0) {
                isRadiusAdjustBounds = false;
                TypedArray typedArray3 = typedArray;
            } else {
                TypedArray typedArray4 = typedArray;
            }
        }
        bg.setIsRadiusAdjustBounds(isRadiusAdjustBounds);
        return bg;
    }
}
