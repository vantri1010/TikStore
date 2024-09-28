package cn.dreamtobe.kpswitch.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.tgnet.ConnectionsManager;

public class ViewUtil {
    private static final String TAG = "ViewUtil";

    public static boolean refreshHeight(View view, int aimHeight) {
        if (view.isInEditMode()) {
            return false;
        }
        Log.d(TAG, String.format("refresh Height %d %d", new Object[]{Integer.valueOf(view.getHeight()), Integer.valueOf(aimHeight)}));
        if (view.getHeight() == aimHeight || Math.abs(view.getHeight() - aimHeight) == StatusBarHeightUtil.getStatusBarHeight(view.getContext())) {
            return false;
        }
        int validPanelHeight = KeyboardUtil.getValidPanelHeight(view.getContext());
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, validPanelHeight));
        } else {
            layoutParams.height = validPanelHeight;
            view.requestLayout();
        }
        return true;
    }

    public static boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags & 1024) != 0;
    }

    public static boolean isTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT < 19 || (activity.getWindow().getAttributes().flags & ConnectionsManager.FileTypeFile) == 0) {
            return false;
        }
        return true;
    }

    static boolean isFitsSystemWindows(Activity activity) {
        if (Build.VERSION.SDK_INT >= 16) {
            return ((ViewGroup) activity.findViewById(16908290)).getChildAt(0).getFitsSystemWindows();
        }
        return false;
    }
}
