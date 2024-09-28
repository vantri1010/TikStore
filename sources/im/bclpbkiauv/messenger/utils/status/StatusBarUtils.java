package im.bclpbkiauv.messenger.utils.status;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtils {
    public static final int TYPE_FLYME = 1;
    public static final int TYPE_M = 3;
    public static final int TYPE_MIUI = 0;

    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= 21) {
            activity.getWindow().setStatusBarColor(colorId);
        } else if (Build.VERSION.SDK_INT >= 19) {
            setTranslucentStatus(activity);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setTintColor(colorId);
        }
    }

    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.getDecorView().setSystemUiVisibility(1280);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window window2 = activity.getWindow();
            WindowManager.LayoutParams attributes = window2.getAttributes();
            attributes.flags |= ConnectionsManager.FileTypeFile;
            window2.setAttributes(attributes);
        }
    }

    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        ViewGroup rootView;
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup winContent = (ViewGroup) activity.findViewById(16908290);
            if (winContent.getChildCount() > 0 && (rootView = (ViewGroup) winContent.getChildAt(0)) != null) {
                rootView.setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            setStatusBarFontIconDark(activity, 3, dark);
        } else if (OSUtils.isMiui()) {
            setStatusBarFontIconDark(activity, 0, dark);
        } else if (!OSUtils.isFlyme()) {
            return false;
        } else {
            setStatusBarFontIconDark(activity, 1, dark);
        }
        return true;
    }

    public static boolean setStatusBarFontIconDark(Activity activity, int type, boolean dark) {
        if (type == 0) {
            return setMiuiUI(activity, dark);
        }
        if (type != 1) {
            return setCommonUI(activity, dark);
        }
        return setFlymeUI(activity, dark);
    }

    public static boolean setCommonUI(Activity activity, boolean dark) {
        View decorView;
        int vis;
        if (Build.VERSION.SDK_INT < 23 || (decorView = activity.getWindow().getDecorView()) == null) {
            return false;
        }
        int vis2 = decorView.getSystemUiVisibility();
        if (dark) {
            vis = vis2 | 8192;
        } else {
            vis = vis2 & -8193;
        }
        if (decorView.getSystemUiVisibility() == vis) {
            return true;
        }
        decorView.setSystemUiVisibility(vis);
        return true;
    }

    public static boolean setFlymeUI(Activity activity, boolean dark) {
        int value;
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt((Object) null);
            int value2 = meizuFlags.getInt(lp);
            if (dark) {
                value = value2 | bit;
            } else {
                value = value2 & (~bit);
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            int darkModeFlag = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
            extraFlagField.setAccessible(true);
            if (dark) {
                extraFlagField.invoke(window, new Object[]{Integer.valueOf(darkModeFlag), Integer.valueOf(darkModeFlag)});
            } else {
                extraFlagField.invoke(window, new Object[]{0, Integer.valueOf(darkModeFlag)});
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
