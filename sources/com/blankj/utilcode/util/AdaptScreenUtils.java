package com.blankj.utilcode.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class AdaptScreenUtils {
    private static List<Field> sMetricsFields;

    private AdaptScreenUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Resources adaptWidth(Resources resources, int designWidth) {
        applyDisplayMetrics(resources, (((float) resources.getDisplayMetrics().widthPixels) * 72.0f) / ((float) designWidth));
        return resources;
    }

    public static Resources adaptHeight(Resources resources, int designHeight) {
        return adaptHeight(resources, designHeight, false);
    }

    public static Resources adaptHeight(Resources resources, int designHeight, boolean includeNavBar) {
        applyDisplayMetrics(resources, (((float) (resources.getDisplayMetrics().heightPixels + (includeNavBar ? getNavBarHeight(resources) : 0))) * 72.0f) / ((float) designHeight));
        return resources;
    }

    private static int getNavBarHeight(Resources resources) {
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static Resources closeAdapt(Resources resources) {
        applyDisplayMetrics(resources, Resources.getSystem().getDisplayMetrics().density * 72.0f);
        return resources;
    }

    public static int pt2Px(float ptValue) {
        return (int) (((double) ((Utils.getApp().getResources().getDisplayMetrics().xdpi * ptValue) / 72.0f)) + 0.5d);
    }

    public static int px2Pt(float pxValue) {
        return (int) (((double) ((72.0f * pxValue) / Utils.getApp().getResources().getDisplayMetrics().xdpi)) + 0.5d);
    }

    private static void applyDisplayMetrics(Resources resources, float newXdpi) {
        resources.getDisplayMetrics().xdpi = newXdpi;
        Utils.getApp().getResources().getDisplayMetrics().xdpi = newXdpi;
        applyOtherDisplayMetrics(resources, newXdpi);
    }

    static void preLoad() {
        applyDisplayMetrics(Resources.getSystem(), Resources.getSystem().getDisplayMetrics().xdpi);
    }

    private static void applyOtherDisplayMetrics(Resources resources, float newXdpi) {
        if (sMetricsFields == null) {
            sMetricsFields = new ArrayList();
            Class resCls = resources.getClass();
            Field[] declaredFields = resCls.getDeclaredFields();
            while (declaredFields != null && declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    if (field.getType().isAssignableFrom(DisplayMetrics.class)) {
                        field.setAccessible(true);
                        DisplayMetrics tmpDm = getMetricsFromField(resources, field);
                        if (tmpDm != null) {
                            sMetricsFields.add(field);
                            tmpDm.xdpi = newXdpi;
                        }
                    }
                }
                resCls = resCls.getSuperclass();
                if (resCls != null) {
                    declaredFields = resCls.getDeclaredFields();
                } else {
                    return;
                }
            }
            return;
        }
        applyMetricsFields(resources, newXdpi);
    }

    private static void applyMetricsFields(Resources resources, float newXdpi) {
        for (Field metricsField : sMetricsFields) {
            try {
                DisplayMetrics dm = (DisplayMetrics) metricsField.get(resources);
                if (dm != null) {
                    dm.xdpi = newXdpi;
                }
            } catch (Exception e) {
                Log.e("AdaptScreenUtils", "applyMetricsFields: " + e);
            }
        }
    }

    private static DisplayMetrics getMetricsFromField(Resources resources, Field field) {
        try {
            return (DisplayMetrics) field.get(resources);
        } catch (Exception e) {
            Log.e("AdaptScreenUtils", "getMetricsFromField: " + e);
            return null;
        }
    }
}
