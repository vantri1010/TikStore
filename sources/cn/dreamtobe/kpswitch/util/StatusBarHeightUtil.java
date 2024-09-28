package cn.dreamtobe.kpswitch.util;

import android.content.Context;
import android.util.Log;

public class StatusBarHeightUtil {
    private static boolean INIT = false;
    private static final String STATUS_BAR_DEF_PACKAGE = "android";
    private static final String STATUS_BAR_DEF_TYPE = "dimen";
    private static int STATUS_BAR_HEIGHT = 50;
    private static final String STATUS_BAR_NAME = "status_bar_height";

    public static synchronized int getStatusBarHeight(Context context) {
        int resourceId;
        int resourceId2;
        synchronized (StatusBarHeightUtil.class) {
            if (!INIT && (resourceId2 = context.getResources().getIdentifier(STATUS_BAR_NAME, STATUS_BAR_DEF_TYPE, STATUS_BAR_DEF_PACKAGE)) > 0) {
                int dimensionPixelSize = context.getResources().getDimensionPixelSize(resourceId2);
                STATUS_BAR_HEIGHT = dimensionPixelSize;
                INIT = true;
                Log.d("StatusBarHeightUtil", String.format("Get status bar height %d", new Object[]{Integer.valueOf(dimensionPixelSize)}));
            }
            resourceId = STATUS_BAR_HEIGHT;
        }
        return resourceId;
    }
}
