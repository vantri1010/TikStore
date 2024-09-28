package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public final class ScreenUtils {
    private ScreenUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    public static int getAppScreenWidth() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int getAppScreenHeight() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
        if (wm == null) {
            return -1;
        }
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static float getScreenDensity() {
        return Utils.getApp().getResources().getDisplayMetrics().density;
    }

    public static int getScreenDensityDpi() {
        return Utils.getApp().getResources().getDisplayMetrics().densityDpi;
    }

    public static void setFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(1024);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setNonFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(1024);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void toggleFullScreen(Activity activity) {
        if (activity != null) {
            boolean isFullScreen = isFullScreen(activity);
            Window window = activity.getWindow();
            if (isFullScreen) {
                window.clearFlags(1024);
            } else {
                window.addFlags(1024);
            }
        } else {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean isFullScreen(Activity activity) {
        if (activity != null) {
            return (activity.getWindow().getAttributes().flags & 1024) == 1024;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setLandscape(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(0);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setPortrait(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(1);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isLandscape() {
        return Utils.getApp().getResources().getConfiguration().orientation == 2;
    }

    public static boolean isPortrait() {
        return Utils.getApp().getResources().getConfiguration().orientation == 1;
    }

    public static int getScreenRotation(Activity activity) {
        if (activity != null) {
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == 1) {
                return 90;
            }
            if (rotation == 2) {
                return 180;
            }
            if (rotation != 3) {
                return 0;
            }
            return 270;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap screenShot(Activity activity) {
        if (activity != null) {
            return screenShot(activity, false);
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static Bitmap screenShot(Activity activity, boolean isDeleteStatusBar) {
        Bitmap ret;
        if (activity != null) {
            View decorView = activity.getWindow().getDecorView();
            boolean drawingCacheEnabled = decorView.isDrawingCacheEnabled();
            boolean willNotCacheDrawing = decorView.willNotCacheDrawing();
            decorView.setDrawingCacheEnabled(true);
            decorView.setWillNotCacheDrawing(false);
            Bitmap bmp = decorView.getDrawingCache();
            if (bmp == null) {
                decorView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                decorView.layout(0, 0, decorView.getMeasuredWidth(), decorView.getMeasuredHeight());
                decorView.buildDrawingCache();
                bmp = Bitmap.createBitmap(decorView.getDrawingCache());
            }
            if (bmp == null) {
                return null;
            }
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (isDeleteStatusBar) {
                Resources resources = activity.getResources();
                int statusBarHeight = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
                ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight);
            } else {
                ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
            }
            decorView.destroyDrawingCache();
            decorView.setWillNotCacheDrawing(willNotCacheDrawing);
            decorView.setDrawingCacheEnabled(drawingCacheEnabled);
            return ret;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isScreenLock() {
        KeyguardManager km = (KeyguardManager) Utils.getApp().getSystemService("keyguard");
        if (km == null) {
            return false;
        }
        return km.inKeyguardRestrictedInputMode();
    }

    public static void setSleepDuration(int duration) {
        Settings.System.putInt(Utils.getApp().getContentResolver(), "screen_off_timeout", duration);
    }

    public static int getSleepDuration() {
        try {
            return Settings.System.getInt(Utils.getApp().getContentResolver(), "screen_off_timeout");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -123;
        }
    }
}
