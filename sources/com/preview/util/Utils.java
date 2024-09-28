package com.preview.util;

import android.content.Context;
import android.util.TypedValue;

public class Utils {
    public static int dp2px(Context context, int dipValue) {
        return (int) TypedValue.applyDimension(1, (float) dipValue, context.getResources().getDisplayMetrics());
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
