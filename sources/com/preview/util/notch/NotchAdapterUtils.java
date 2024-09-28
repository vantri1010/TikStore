package com.preview.util.notch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class NotchAdapterUtils {
    public static final String TAG = NotchAdapterUtils.class.getSimpleName();

    public static void adapter(Activity activity, int cutOutMode) {
        if (activity != null) {
            adapter(activity.getWindow(), cutOutMode);
        }
    }

    public static void adapter(Window window, int cutOutMode) {
        if (window == null || !isNotch(window)) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            adapterP(window, cutOutMode);
        } else if (Build.VERSION.SDK_INT >= 26) {
            adapterO(window, cutOutMode);
        }
    }

    private static void adapterP(Window window, int cutOutMode) {
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = cutOutMode;
            window.setAttributes(lp);
        }
    }

    private static void adapterO(Window window, int cutOutMode) {
        if (window != null) {
            if (OSUtils.isMiui()) {
                adapterOWithMIUI(window, cutOutMode);
            } else if (OSUtils.isEmui()) {
                adapterOWithEMUI(window, cutOutMode);
            }
        }
    }

    private static void adapterOWithMIUI(Window window, int cutOutMode) {
        String methodName;
        if (window != null) {
            if (cutOutMode == 2) {
                methodName = "clearExtraFlags";
            } else if (cutOutMode != 0) {
                methodName = "addExtraFlags";
            } else if ((window.getAttributes().flags & ConnectionsManager.FileTypeFile) > 0) {
                methodName = "addExtraFlags";
            } else {
                methodName = "clearExtraFlags";
            }
            Class<Window> cls = Window.class;
            try {
                cls.getMethod(methodName, new Class[]{Integer.TYPE}).invoke(window, new Object[]{768});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void adapterOWithEMUI(Window window, int cutOutMode) {
        String methodName;
        if (window != null) {
            if (cutOutMode == 2) {
                methodName = "clearHwFlags";
            } else if (cutOutMode != 0) {
                methodName = "addHwFlags";
            } else if ((window.getAttributes().flags & ConnectionsManager.FileTypeFile) > 0) {
                methodName = "addHwFlags";
            } else {
                methodName = "clearHwFlags";
            }
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            try {
                Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
                Object layoutParamsExObj = layoutParamsExCls.getConstructor(new Class[]{WindowManager.LayoutParams.class}).newInstance(new Object[]{layoutParams});
                layoutParamsExCls.getMethod(methodName, new Class[]{Integer.TYPE}).invoke(layoutParamsExObj, new Object[]{65536});
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static boolean isNotch(Window window) {
        DisplayCutout displayCutout;
        List<Rect> rects;
        if (Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
            if (windowInsets == null || (displayCutout = windowInsets.getDisplayCutout()) == null || (rects = displayCutout.getBoundingRects()) == null || rects.size() <= 0) {
                return false;
            }
            return true;
        } else if (OSUtils.isMiui()) {
            return isNotchOnMIUI();
        } else {
            if (OSUtils.isEmui()) {
                return isNotchOnEMUI(window.getContext());
            }
            if (OSUtils.isVivo()) {
                return isNotchOnVIVO(window.getContext());
            }
            if (OSUtils.isOppo()) {
                return isNotchOnOPPO(window.getContext());
            }
            return false;
        }
    }

    public static boolean isNotchOnMIUI() {
        return "1".equals(OSUtils.getProp("ro.miui.notch"));
    }

    public static boolean isNotchOnEMUI(Context context) {
        if (context == null) {
            return false;
        }
        try {
            Class HwNotchSizeUtil = context.getClassLoader().loadClass("com.huawei.android.util.HwNotchSizeUtil");
            return ((Boolean) HwNotchSizeUtil.getMethod("hasNotchOnHuawei", new Class[0]).invoke(HwNotchSizeUtil, new Object[0])).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return false;
        } catch (Exception e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public static boolean isNotchOnVIVO(Context context) {
        if (context == null) {
            return false;
        }
        try {
            Class FtFeature = context.getClassLoader().loadClass("android.util.FtFeature");
            return ((Boolean) FtFeature.getMethod("isFeatureSupport", new Class[]{Integer.TYPE}).invoke(FtFeature, new Object[]{32})).booleanValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return false;
        } catch (Exception e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public static boolean isNotchOnOPPO(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
}
