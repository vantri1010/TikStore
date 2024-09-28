package com.gyf.barlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

class BarConfig {
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private final int mActionBarHeight;
    private final boolean mHasNavigationBar;
    private final boolean mInPortrait;
    private final int mNavigationBarHeight;
    private final int mNavigationBarWidth;
    private final float mSmallestWidthDp;
    private final int mStatusBarHeight;

    public BarConfig(Activity activity) {
        Resources res = activity.getResources();
        boolean z = false;
        this.mInPortrait = res.getConfiguration().orientation == 1;
        this.mSmallestWidthDp = getSmallestWidthDp(activity);
        this.mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
        this.mActionBarHeight = getActionBarHeight(activity);
        this.mNavigationBarHeight = getNavigationBarHeight(activity);
        this.mNavigationBarWidth = getNavigationBarWidth(activity);
        this.mHasNavigationBar = this.mNavigationBarHeight > 0 ? true : z;
    }

    private int getActionBarHeight(Context context) {
        if (Build.VERSION.SDK_INT < 14) {
            return 0;
        }
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(16843499, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
    }

    private int getNavigationBarHeight(Context context) {
        String key;
        Resources res = context.getResources();
        if (Build.VERSION.SDK_INT < 14 || !hasNavBar((Activity) context)) {
            return 0;
        }
        if (this.mInPortrait) {
            key = NAV_BAR_HEIGHT_RES_NAME;
        } else {
            key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
        }
        return getInternalDimensionSize(res, key);
    }

    private int getNavigationBarWidth(Context context) {
        Resources res = context.getResources();
        if (Build.VERSION.SDK_INT < 14 || !hasNavBar((Activity) context)) {
            return 0;
        }
        return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
    }

    private static boolean hasNavBar(Activity activity) {
        Display d = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            d.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        return realWidth - displayMetrics.widthPixels > 0 || realHeight - displayMetrics.heightPixels > 0;
    }

    private int getInternalDimensionSize(Resources res, String key) {
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            int resourceId = Integer.parseInt(clazz.getField(key).get(clazz.newInstance()).toString());
            if (resourceId > 0) {
                return res.getDimensionPixelSize(resourceId);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private float getSmallestWidthDp(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 16) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        return Math.min(((float) metrics.widthPixels) / metrics.density, ((float) metrics.heightPixels) / metrics.density);
    }

    public boolean isNavigationAtBottom() {
        return this.mSmallestWidthDp >= 600.0f || this.mInPortrait;
    }

    public int getStatusBarHeight() {
        return this.mStatusBarHeight;
    }

    public int getActionBarHeight() {
        return this.mActionBarHeight;
    }

    public boolean hasNavigtionBar() {
        return this.mHasNavigationBar;
    }

    public int getNavigationBarHeight() {
        return this.mNavigationBarHeight;
    }

    public int getNavigationBarWidth() {
        return this.mNavigationBarWidth;
    }
}
