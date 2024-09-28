package im.bclpbkiauv.ui.components.toast;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.MryRoundButtonDrawable;

public class ToastBlackStyle {
    public int getGravity() {
        return 48;
    }

    public int getXOffset() {
        return 0;
    }

    public int getYOffset() {
        return AndroidUtilities.dp(59.0f);
    }

    public int getZ() {
        return 30;
    }

    public int getMaxLines() {
        return 5;
    }

    public int getPaddingStart() {
        return AndroidUtilities.dp(15.0f);
    }

    public int getPaddingEnd() {
        return getPaddingStart();
    }

    public int getPaddingTop() {
        return AndroidUtilities.dp(10.0f);
    }

    public int getPaddingBottom() {
        return getPaddingTop();
    }

    public int getCornerRadius() {
        return AndroidUtilities.dp(23.0f);
    }

    public int getBackgroundColor() {
        return Theme.getColor(Theme.key_windowBackgroundWhite);
    }

    public Drawable getBackground() {
        MryRoundButtonDrawable bg = new MryRoundButtonDrawable();
        bg.setIsRadiusAdjustBounds(true);
        bg.setStroke(0, 0);
        bg.setBgData(ColorStateList.valueOf(Theme.getColor(Theme.key_windowBackgroundWhite)));
        return bg;
    }

    public int getTextColor() {
        return Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
    }

    public float getTextSize() {
        return 15.0f;
    }

    public int getMinimumWidth() {
        return 0;
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(45.0f);
    }

    public Typeface getTypeFace() {
        return null;
    }
}
