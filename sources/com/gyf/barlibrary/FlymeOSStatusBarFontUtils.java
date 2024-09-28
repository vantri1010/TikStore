package com.gyf.barlibrary;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FlymeOSStatusBarFontUtils {
    private static int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    private static Method mSetStatusBarColorIcon;
    private static Method mSetStatusBarDarkIcon;
    private static Field mStatusBarColorFiled;

    static {
        SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0;
        Class<Activity> cls = Activity.class;
        try {
            mSetStatusBarColorIcon = cls.getMethod("setStatusBarDarkIcon", new Class[]{Integer.TYPE});
        } catch (NoSuchMethodException e) {
        }
        Class<Activity> cls2 = Activity.class;
        try {
            mSetStatusBarDarkIcon = cls2.getMethod("setStatusBarDarkIcon", new Class[]{Boolean.TYPE});
        } catch (NoSuchMethodException e2) {
        }
        try {
            mStatusBarColorFiled = WindowManager.LayoutParams.class.getField("statusBarColor");
        } catch (NoSuchFieldException e3) {
        }
        try {
            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = View.class.getField("SYSTEM_UI_FLAG_LIGHT_STATUS_BAR").getInt((Object) null);
        } catch (IllegalAccessException | NoSuchFieldException e4) {
        }
    }

    public static boolean isBlackColor(int color, int level) {
        return toGrey(color) < level;
    }

    public static int toGrey(int rgb) {
        return (((((16711680 & rgb) >> 16) * 38) + (((65280 & rgb) >> 8) * 75)) + ((rgb & 255) * 15)) >> 7;
    }

    public static void setStatusBarDarkIcon(Activity activity, int color) {
        Method method = mSetStatusBarColorIcon;
        if (method != null) {
            try {
                method.invoke(activity, new Object[]{Integer.valueOf(color)});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            }
        } else {
            boolean whiteColor = isBlackColor(color, 50);
            if (mStatusBarColorFiled != null) {
                setStatusBarDarkIcon(activity, whiteColor, whiteColor);
                setStatusBarDarkIcon(activity.getWindow(), color);
                return;
            }
            setStatusBarDarkIcon(activity, whiteColor);
        }
    }

    public static void setStatusBarDarkIcon(Window window, int color) {
        try {
            setStatusBarColor(window, color);
            if (Build.VERSION.SDK_INT > 22) {
                setStatusBarDarkIcon(window.getDecorView(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatusBarDarkIcon(Activity activity, boolean dark) {
        setStatusBarDarkIcon(activity, dark, true);
    }

    private static boolean changeMeizuFlag(WindowManager.LayoutParams winParams, String flagName, boolean on) {
        int meizuFlags;
        try {
            Field f = winParams.getClass().getDeclaredField(flagName);
            f.setAccessible(true);
            int bits = f.getInt(winParams);
            Field f2 = winParams.getClass().getDeclaredField("meizuFlags");
            f2.setAccessible(true);
            int meizuFlags2 = f2.getInt(winParams);
            int oldFlags = meizuFlags2;
            if (on) {
                meizuFlags = meizuFlags2 | bits;
            } else {
                meizuFlags = meizuFlags2 & (~bits);
            }
            if (oldFlags == meizuFlags) {
                return false;
            }
            f2.setInt(winParams, meizuFlags);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return false;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return false;
        } catch (Throwable e4) {
            e4.printStackTrace();
            return false;
        }
    }

    private static void setStatusBarDarkIcon(View view, boolean dark) {
        int newVis;
        int oldVis = view.getSystemUiVisibility();
        int newVis2 = oldVis;
        if (dark) {
            newVis = newVis2 | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            newVis = newVis2 & (~SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (newVis != oldVis) {
            view.setSystemUiVisibility(newVis);
        }
    }

    private static void setStatusBarColor(Window window, int color) {
        WindowManager.LayoutParams winParams = window.getAttributes();
        Field field = mStatusBarColorFiled;
        if (field != null) {
            try {
                if (field.getInt(winParams) != color) {
                    mStatusBarColorFiled.set(winParams, Integer.valueOf(color));
                    window.setAttributes(winParams);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setStatusBarDarkIcon(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT < 23) {
            changeMeizuFlag(window.getAttributes(), "MEIZU_FLAG_DARK_STATUS_BAR_ICON", dark);
            return;
        }
        View decorView = window.getDecorView();
        if (decorView != null) {
            setStatusBarDarkIcon(decorView, dark);
            setStatusBarColor(window, 0);
        }
    }

    private static void setStatusBarDarkIcon(Activity activity, boolean dark, boolean flag) {
        Method method = mSetStatusBarDarkIcon;
        if (method != null) {
            try {
                method.invoke(activity, new Object[]{Boolean.valueOf(dark)});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            }
        } else if (flag) {
            setStatusBarDarkIcon(activity.getWindow(), dark);
        }
    }
}
