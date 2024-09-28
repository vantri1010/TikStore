package im.bclpbkiauv.ui.bottom;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class UIUtils {
    public static int dip2Px(Context context, int dip) {
        return (int) ((((float) dip) * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) ((spValue * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int getColor(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return context.getResources().getDrawable(resId);
    }
}
