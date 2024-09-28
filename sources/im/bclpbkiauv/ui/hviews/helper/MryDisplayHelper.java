package im.bclpbkiauv.ui.hviews.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
import java.lang.reflect.Method;
import java.util.Locale;

public class MryDisplayHelper {
    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final String HUAWAI_DISPLAY_NOTCH_STATUS = "display_notch_status";
    private static final String TAG = "QMUIDisplayHelper";
    private static final String VIVO_NAVIGATION_GESTURE = "navigation_gesture_on";
    private static final String XIAOMI_DISPLAY_NOTCH_STATUS = "force_black";
    private static final String XIAOMI_FULLSCREEN_GESTURE = "force_fsg_nav_bar";
    private static Boolean sHasCamera = null;

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dpToPx(int dpValue) {
        return (int) ((((float) dpValue) * DENSITY) + 0.5f);
    }

    public static int pxToDp(float pxValue) {
        return (int) ((pxValue / DENSITY) + 0.5f);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static float getFontDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context) {
        int screenHeight = getDisplayMetrics(context).heightPixels;
        if (!MryDeviceHelper.isXiaomi() || !xiaomiNavigationGestureEnabled(context)) {
            return screenHeight;
        }
        return screenHeight + getResourceNavHeight(context);
    }

    public static int[] getRealScreenSize(Context context) {
        return doGetRealScreenSize(context);
    }

    private static int[] doGetRealScreenSize(Context context) {
        int[] size = new int[2];
        Display d = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        try {
            widthPixels = ((Integer) Display.class.getMethod("getRawWidth", new Class[0]).invoke(d, new Object[0])).intValue();
            heightPixels = ((Integer) Display.class.getMethod("getRawHeight", new Class[0]).invoke(d, new Object[0])).intValue();
        } catch (Exception e) {
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                d.getRealSize(realSize);
                Display.class.getMethod("getRealSize", new Class[]{Point.class}).invoke(d, new Object[]{realSize});
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception e2) {
            }
        }
        size[0] = widthPixels;
        size[1] = heightPixels;
        return size;
    }

    public static int getUsefulScreenWidth(Activity activity) {
        return getUsefulScreenWidth(activity, MryNotchHelper.hasNotch(activity));
    }

    public static int getUsefulScreenWidth(View view) {
        return getUsefulScreenWidth(view.getContext(), MryNotchHelper.hasNotch(view));
    }

    public static int getUsefulScreenWidth(Context context, boolean hasNotch) {
        boolean isLandscape = false;
        int result = getRealScreenSize(context)[0];
        if (context.getResources().getConfiguration().orientation == 2) {
            isLandscape = true;
        }
        if (!hasNotch) {
            if (!isLandscape || !MryDeviceHelper.isEssentialPhone() || Build.VERSION.SDK_INT >= 26) {
                return result;
            }
            return result - (StatusBarUtils.getStatusBarHeight(context) * 2);
        } else if (!isLandscape || !MryDeviceHelper.isHuawei() || huaweiIsNotchSetToShowInSetting(context)) {
            return result;
        } else {
            return result - MryNotchHelper.getNotchSizeInHuawei(context)[1];
        }
    }

    public static int getUsefulScreenHeight(Activity activity) {
        return getUsefulScreenHeight(activity, MryNotchHelper.hasNotch(activity));
    }

    public static int getUsefulScreenHeight(View view) {
        return getUsefulScreenHeight(view.getContext(), MryNotchHelper.hasNotch(view));
    }

    private static int getUsefulScreenHeight(Context context, boolean hasNotch) {
        boolean isPortrait = true;
        int result = getRealScreenSize(context)[1];
        if (context.getResources().getConfiguration().orientation != 1) {
            isPortrait = false;
        }
        if (hasNotch || !isPortrait || !MryDeviceHelper.isEssentialPhone() || Build.VERSION.SDK_INT >= 26) {
            return result;
        }
        return result - (StatusBarUtils.getStatusBarHeight(context) * 2);
    }

    public static boolean isNavMenuExist(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(4);
        if (hasMenuKey || hasBackKey) {
            return false;
        }
        return true;
    }

    public static int dp2px(Context context, int dp) {
        return (int) (((double) (getDensity(context) * ((float) dp))) + 0.5d);
    }

    public static int sp2px(Context context, int sp) {
        return (int) (((double) (getFontDensity(context) * ((float) sp))) + 0.5d);
    }

    public static int px2dp(Context context, int px) {
        return (int) (((double) (((float) px) / getDensity(context))) + 0.5d);
    }

    public static int px2sp(Context context, int px) {
        return (int) (((double) (((float) px) / getFontDensity(context))) + 0.5d);
    }

    public static boolean hasStatusBar(Context context) {
        if (!(context instanceof Activity) || (((Activity) context).getWindow().getAttributes().flags & 1024) != 1024) {
            return true;
        }
        return false;
    }

    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(16843499, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        if (MryDeviceHelper.isXiaomi()) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
            return 0;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            int x = Integer.parseInt(c.getField("status_bar_height").get(c.newInstance()).toString());
            if (x > 0) {
                return context.getResources().getDimensionPixelSize(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNavMenuHeight(Context context) {
        if (!isNavMenuExist(context)) {
            return 0;
        }
        int resourceNavHeight = getResourceNavHeight(context);
        if (resourceNavHeight >= 0) {
            return resourceNavHeight;
        }
        return getRealScreenSize(context)[1] - getScreenHeight(context);
    }

    private static int getResourceNavHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    public static final boolean hasCamera(Context context) {
        if (sHasCamera == null) {
            PackageManager pckMgr = context.getPackageManager();
            sHasCamera = Boolean.valueOf(pckMgr.hasSystemFeature("android.hardware.camera.front") || pckMgr.hasSystemFeature("android.hardware.camera"));
        }
        return sHasCamera.booleanValue();
    }

    public static boolean hasHardwareMenuKey(Context context) {
        if (Build.VERSION.SDK_INT < 11) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 14) {
            return ViewConfiguration.get(context).hasPermanentMenuKey();
        }
        return false;
    }

    public static boolean hasInternet(Context context) {
        return ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo() != null;
    }

    public static boolean isPackageExist(Context context, String pckName) {
        try {
            if (context.getPackageManager().getPackageInfo(pckName, 0) != null) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public static boolean isSdcardReady() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getCurCountryLan(Context context) {
        Locale sysLocale;
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 24) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        return sysLocale.getLanguage() + "-" + sysLocale.getCountry();
    }

    public static boolean isZhCN(Context context) {
        Locale sysLocale;
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= 24) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        return sysLocale.getCountry().equalsIgnoreCase("CN");
    }

    public static void setFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(512);
        window.addFlags(1024);
    }

    public static void cancelFullScreen(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(1024);
        window.clearFlags(512);
    }

    public static boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags & 1024) == 1024;
    }

    public static boolean isElevationSupported() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean hasNavigationBar(Context context) {
        if (!deviceHasNavigationBar()) {
            return false;
        }
        if (MryDeviceHelper.isVivo()) {
            return vivoNavigationGestureEnabled(context);
        }
        return true;
    }

    private static boolean deviceHasNavigationBar() {
        try {
            Method getWmServiceMethod = Class.forName("android.view.WindowManagerGlobal").getDeclaredMethod("getWindowManagerService", new Class[0]);
            getWmServiceMethod.setAccessible(true);
            Object iWindowManager = getWmServiceMethod.invoke((Object) null, new Object[0]);
            Method hasNavBarMethod = iWindowManager.getClass().getDeclaredMethod("hasNavigationBar", new Class[0]);
            hasNavBarMethod.setAccessible(true);
            return ((Boolean) hasNavBarMethod.invoke(iWindowManager, new Object[0])).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean vivoNavigationGestureEnabled(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), VIVO_NAVIGATION_GESTURE, 0) != 0) {
            return true;
        }
        return false;
    }

    public static boolean xiaomiNavigationGestureEnabled(Context context) {
        if (Settings.Global.getInt(context.getContentResolver(), XIAOMI_FULLSCREEN_GESTURE, 0) != 0) {
            return true;
        }
        return false;
    }

    public static boolean huaweiIsNotchSetToShowInSetting(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), HUAWAI_DISPLAY_NOTCH_STATUS, 0) == 0) {
            return true;
        }
        return false;
    }

    public static boolean xiaomiIsNotchSetToShowInSetting(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), XIAOMI_DISPLAY_NOTCH_STATUS, 0) == 0;
    }
}
