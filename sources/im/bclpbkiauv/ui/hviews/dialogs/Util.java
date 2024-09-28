package im.bclpbkiauv.ui.hviews.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Util {
    public static float sp2px(Context context, float spVal) {
        return TypedValue.applyDimension(2, spVal, context.getResources().getDisplayMetrics());
    }

    public static float dp2px(Context context, float dpVal) {
        return TypedValue.applyDimension(1, dpVal, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context context, float px) {
        return (int) ((px / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int i = outMetrics.heightPixels;
        return widthPixels;
    }

    public static int getScreenHeight(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int i = outMetrics.widthPixels;
        return outMetrics.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return res.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
