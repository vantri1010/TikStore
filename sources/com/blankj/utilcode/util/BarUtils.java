package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.drawerlayout.widget.DrawerLayout;
import im.bclpbkiauv.tgnet.ConnectionsManager;

public final class BarUtils {
    private static final int KEY_OFFSET = -123;
    private static final String TAG_OFFSET = "TAG_OFFSET";
    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";

    private BarUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int getStatusBarHeight() {
        Resources resources = Utils.getApp().getResources();
        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));
    }

    public static void setStatusBarVisibility(Activity activity, boolean isVisible) {
        if (activity != null) {
            setStatusBarVisibility(activity.getWindow(), isVisible);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setStatusBarVisibility(Window window, boolean isVisible) {
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (isVisible) {
            window.clearFlags(1024);
            showStatusBarView(window);
            addMarginTopEqualStatusBarHeight(window);
        } else {
            window.addFlags(1024);
            hideStatusBarView(window);
            subtractMarginTopEqualStatusBarHeight(window);
        }
    }

    public static boolean isStatusBarVisible(Activity activity) {
        if (activity != null) {
            return (activity.getWindow().getAttributes().flags & 1024) == 0;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setStatusBarLightMode(Activity activity, boolean isLightMode) {
        if (activity != null) {
            setStatusBarLightMode(activity.getWindow(), isLightMode);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setStatusBarLightMode(Window window, boolean isLightMode) {
        int vis;
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 23) {
            View decorView = window.getDecorView();
            int vis2 = decorView.getSystemUiVisibility();
            if (isLightMode) {
                vis = vis2 | 8192;
            } else {
                vis = vis2 & -8193;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    public static boolean isStatusBarLightMode(Activity activity) {
        if (activity != null) {
            return isStatusBarLightMode(activity.getWindow());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isStatusBarLightMode(Window window) {
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT < 23 || (window.getDecorView().getSystemUiVisibility() & 8192) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void addMarginTopEqualStatusBarHeight(View view) {
        if (view == null) {
            throw new NullPointerException("Argument 'view' of type View (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19) {
            view.setTag(TAG_OFFSET);
            Object haveSetOffset = view.getTag(KEY_OFFSET);
            if (haveSetOffset == null || !((Boolean) haveSetOffset).booleanValue()) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
                view.setTag(KEY_OFFSET, true);
            }
        }
    }

    public static void subtractMarginTopEqualStatusBarHeight(View view) {
        Object haveSetOffset;
        if (view == null) {
            throw new NullPointerException("Argument 'view' of type View (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19 && (haveSetOffset = view.getTag(KEY_OFFSET)) != null && ((Boolean) haveSetOffset).booleanValue()) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin - getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
            view.setTag(KEY_OFFSET, false);
        }
    }

    private static void addMarginTopEqualStatusBarHeight(Window window) {
        View withTag;
        if (Build.VERSION.SDK_INT >= 19 && (withTag = window.getDecorView().findViewWithTag(TAG_OFFSET)) != null) {
            addMarginTopEqualStatusBarHeight(withTag);
        }
    }

    private static void subtractMarginTopEqualStatusBarHeight(Window window) {
        View withTag;
        if (Build.VERSION.SDK_INT >= 19 && (withTag = window.getDecorView().findViewWithTag(TAG_OFFSET)) != null) {
            subtractMarginTopEqualStatusBarHeight(withTag);
        }
    }

    public static View setStatusBarColor(Activity activity, int color) {
        if (activity != null) {
            return setStatusBarColor(activity, color, false);
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static View setStatusBarColor(Activity activity, int color, boolean isDecor) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT < 19) {
            return null;
        } else {
            transparentStatusBar(activity);
            return applyStatusBarColor(activity, color, isDecor);
        }
    }

    public static View setStatusBarColor(Window window, int color) {
        if (window != null) {
            return setStatusBarColor(window, color, false);
        }
        throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static View setStatusBarColor(Window window, int color, boolean isDecor) {
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT < 19) {
            return null;
        } else {
            transparentStatusBar(window);
            return applyStatusBarColor(window, color, isDecor);
        }
    }

    public static void setStatusBarColor(View fakeStatusBar, int color) {
        Activity activity;
        if (fakeStatusBar == null) {
            throw new NullPointerException("Argument 'fakeStatusBar' of type View (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19 && (activity = getActivityByView(fakeStatusBar)) != null) {
            transparentStatusBar(activity);
            fakeStatusBar.setVisibility(0);
            ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = getStatusBarHeight();
            fakeStatusBar.setBackgroundColor(color);
        }
    }

    public static void setStatusBarCustom(View fakeStatusBar) {
        Activity activity;
        if (fakeStatusBar == null) {
            throw new NullPointerException("Argument 'fakeStatusBar' of type View (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19 && (activity = getActivityByView(fakeStatusBar)) != null) {
            transparentStatusBar(activity);
            fakeStatusBar.setVisibility(0);
            ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
            if (layoutParams == null) {
                fakeStatusBar.setLayoutParams(new ViewGroup.LayoutParams(-1, getStatusBarHeight()));
                return;
            }
            layoutParams.width = -1;
            layoutParams.height = getStatusBarHeight();
        }
    }

    public static void setStatusBarColor4Drawer(DrawerLayout drawer, View fakeStatusBar, int color) {
        if (drawer == null) {
            throw new NullPointerException("Argument 'drawer' of type DrawerLayout (#0 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fakeStatusBar != null) {
            setStatusBarColor4Drawer(drawer, fakeStatusBar, color, false);
        } else {
            throw new NullPointerException("Argument 'fakeStatusBar' of type View (#1 out of 3, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void setStatusBarColor4Drawer(DrawerLayout drawer, View fakeStatusBar, int color, boolean isTop) {
        Activity activity;
        if (drawer == null) {
            throw new NullPointerException("Argument 'drawer' of type DrawerLayout (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (fakeStatusBar == null) {
            throw new NullPointerException("Argument 'fakeStatusBar' of type View (#1 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19 && (activity = getActivityByView(fakeStatusBar)) != null) {
            transparentStatusBar(activity);
            drawer.setFitsSystemWindows(false);
            setStatusBarColor(fakeStatusBar, color);
            int count = drawer.getChildCount();
            for (int i = 0; i < count; i++) {
                drawer.getChildAt(i).setFitsSystemWindows(false);
            }
            if (isTop) {
                hideStatusBarView(activity);
            } else {
                setStatusBarColor(activity, color, false);
            }
        }
    }

    private static View applyStatusBarColor(Activity activity, int color, boolean isDecor) {
        return applyStatusBarColor(activity.getWindow(), color, isDecor);
    }

    private static View applyStatusBarColor(Window window, int color, boolean isDecor) {
        ViewGroup parent;
        if (isDecor) {
            parent = (ViewGroup) window.getDecorView();
        } else {
            parent = (ViewGroup) window.findViewById(16908290);
        }
        View fakeStatusBarView = parent.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == 8) {
                fakeStatusBarView.setVisibility(0);
            }
            fakeStatusBarView.setBackgroundColor(color);
            return fakeStatusBarView;
        }
        View fakeStatusBarView2 = createStatusBarView(window.getContext(), color);
        parent.addView(fakeStatusBarView2);
        return fakeStatusBarView2;
    }

    private static void hideStatusBarView(Activity activity) {
        hideStatusBarView(activity.getWindow());
    }

    private static void hideStatusBarView(Window window) {
        View fakeStatusBarView = ((ViewGroup) window.getDecorView()).findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(8);
        }
    }

    private static void showStatusBarView(Window window) {
        View fakeStatusBarView = ((ViewGroup) window.getDecorView()).findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(0);
        }
    }

    private static View createStatusBarView(Context context, int color) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(-1, getStatusBarHeight()));
        statusBarView.setBackgroundColor(color);
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    public static void transparentStatusBar(Activity activity) {
        transparentStatusBar(activity.getWindow());
    }

    public static void transparentStatusBar(Window window) {
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(ConnectionsManager.FileTypeFile);
                window.addFlags(Integer.MIN_VALUE);
                window.getDecorView().setSystemUiVisibility(1280 | window.getDecorView().getSystemUiVisibility());
                window.setStatusBarColor(0);
                return;
            }
            window.addFlags(ConnectionsManager.FileTypeFile);
        }
    }

    public static int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (Utils.getApp().getTheme().resolveAttribute(16843499, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, Utils.getApp().getResources().getDisplayMetrics());
        }
        return 0;
    }

    public static void setNotificationBarVisibility(boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = Build.VERSION.SDK_INT <= 16 ? "expand" : "expandNotificationsPanel";
        } else {
            methodName = Build.VERSION.SDK_INT <= 16 ? "collapse" : "collapsePanels";
        }
        invokePanels(methodName);
    }

    private static void invokePanels(String methodName) {
        try {
            Class.forName("android.app.StatusBarManager").getMethod(methodName, new Class[0]).invoke(Utils.getApp().getSystemService("statusbar"), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNavBarHeight() {
        Resources res = Utils.getApp().getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setNavBarVisibility(Activity activity, boolean isVisible) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19) {
            setNavBarVisibility(activity.getWindow(), isVisible);
        }
    }

    public static void setNavBarVisibility(Window window, boolean isVisible) {
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int count = decorView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = decorView.getChildAt(i);
                int id = child.getId();
                if (id != -1 && "navigationBarBackground".equals(Utils.getApp().getResources().getResourceEntryName(id))) {
                    child.setVisibility(isVisible ? 0 : 4);
                }
            }
            if (isVisible) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & -4611);
            } else {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | 4610);
            }
        }
    }

    public static boolean isNavBarVisible(Activity activity) {
        if (activity != null) {
            return isNavBarVisible(activity.getWindow());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isNavBarVisible(Window window) {
        if (window != null) {
            boolean isVisible = false;
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int i = 0;
            int count = decorView.getChildCount();
            while (true) {
                if (i < count) {
                    View child = decorView.getChildAt(i);
                    int id = child.getId();
                    if (id != -1 && "navigationBarBackground".equals(Utils.getApp().getResources().getResourceEntryName(id)) && child.getVisibility() == 0) {
                        isVisible = true;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (!isVisible) {
                return isVisible;
            }
            return (decorView.getSystemUiVisibility() & 2) == 0;
        }
        throw new NullPointerException("Argument 'window' of type Window (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setNavBarColor(Activity activity, int color) {
        if (activity != null) {
            setNavBarColor(activity.getWindow(), color);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setNavBarColor(Window window, int color) {
        if (window != null) {
            window.addFlags(Integer.MIN_VALUE);
            window.setNavigationBarColor(color);
            return;
        }
        throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getNavBarColor(Activity activity) {
        if (activity != null) {
            return getNavBarColor(activity.getWindow());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getNavBarColor(Window window) {
        if (window != null) {
            return window.getNavigationBarColor();
        }
        throw new NullPointerException("Argument 'window' of type Window (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isSupportNavBar() {
        if (Build.VERSION.SDK_INT >= 17) {
            WindowManager wm = (WindowManager) Utils.getApp().getSystemService("window");
            if (wm == null) {
                return false;
            }
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            if (realSize.y == size.y && realSize.x == size.x) {
                return false;
            }
            return true;
        }
        boolean menu = ViewConfiguration.get(Utils.getApp()).hasPermanentMenuKey();
        boolean back = KeyCharacterMap.deviceHasKey(4);
        if (menu || back) {
            return false;
        }
        return true;
    }

    public static void setNavBarLightMode(Activity activity, boolean isLightMode) {
        if (activity != null) {
            setStatusBarLightMode(activity.getWindow(), isLightMode);
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void setNavBarLightMode(Window window, boolean isLightMode) {
        int vis;
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT >= 26) {
            View decorView = window.getDecorView();
            int vis2 = decorView.getSystemUiVisibility();
            if (isLightMode) {
                vis = vis2 | 16;
            } else {
                vis = vis2 & -17;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }

    public static boolean isNavBarLightMode(Activity activity) {
        if (activity != null) {
            return isStatusBarLightMode(activity.getWindow());
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isNavBarLightMode(Window window) {
        if (window == null) {
            throw new NullPointerException("Argument 'window' of type Window (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT < 26 || (window.getDecorView().getSystemUiVisibility() & 16) == 0) {
            return false;
        } else {
            return true;
        }
    }

    private static Activity getActivityByView(View view) {
        if (view != null) {
            for (Context context = view.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
            }
            Log.e("BarUtils", "the view's Context is not an Activity.");
            return null;
        }
        throw new NullPointerException("Argument 'view' of type View (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }
}
