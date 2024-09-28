package im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast;

import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ToastBlackStyle {
    public int getGravity() {
        return 17;
    }

    public int getXOffset() {
        return 0;
    }

    public int getYOffset() {
        return 0;
    }

    public int getZ() {
        return 30;
    }

    public int getMaxLines() {
        return 5;
    }

    public int getPaddingEnd() {
        return getPaddingStart();
    }

    public int getPaddingBottom() {
        return getPaddingTop();
    }

    public int getCornerRadius() {
        return AndroidUtilities.dp(9.0f);
    }

    public int getBackgroundColor() {
        return Theme.ACTION_BAR_PHOTO_VIEWER_COLOR;
    }

    public int getTextColor() {
        return -1;
    }

    public float getTextSize() {
        return 14.0f;
    }

    public int getPaddingStart() {
        return AndroidUtilities.dp(16.0f);
    }

    public int getPaddingTop() {
        return AndroidUtilities.dp(10.0f);
    }
}
