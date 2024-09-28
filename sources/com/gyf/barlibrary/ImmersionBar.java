package com.gyf.barlibrary;

import android.app.Activity;
import android.app.Dialog;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.baidu.mapapi.UIMsg;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImmersionBar {
    private static final String NAVIGATIONBAR_IS_MIN = "navigationbar_is_min";
    private static Map<String, BarParams> mMap = new HashMap();
    private static Map<String, ArrayList<String>> mTagKeyMap = new HashMap();
    private static Map<String, BarParams> mTagMap = new HashMap();
    /* access modifiers changed from: private */
    public Activity mActivity;
    private String mActivityName;
    /* access modifiers changed from: private */
    public BarParams mBarParams;
    /* access modifiers changed from: private */
    public BarConfig mConfig;
    /* access modifiers changed from: private */
    public ViewGroup mContentView;
    private ViewGroup mDecorView;
    private Dialog mDialog;
    private String mFragmentName;
    private String mImmersionBarName;
    private Window mWindow;

    private ImmersionBar(Activity activity) {
        Activity activity2 = (Activity) new WeakReference<>(activity).get();
        this.mActivity = activity2;
        this.mWindow = activity2.getWindow();
        String name = activity.getClass().getName();
        this.mActivityName = name;
        this.mImmersionBarName = name;
        initParams();
    }

    private ImmersionBar(Fragment fragment) {
        this((Activity) fragment.getActivity(), fragment);
    }

    private ImmersionBar(Activity activity, Fragment fragment) {
        if (activity != null) {
            WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
            WeakReference<Fragment> fragmentWeakReference = new WeakReference<>(fragment);
            Activity activity2 = (Activity) activityWeakReference.get();
            this.mActivity = activity2;
            this.mWindow = activity2.getWindow();
            this.mActivityName = this.mActivity.getClass().getName();
            String str = this.mActivityName + "_AND_" + ((Fragment) fragmentWeakReference.get()).getClass().getName();
            this.mFragmentName = str;
            this.mImmersionBarName = str;
            initParams();
            return;
        }
        throw new IllegalArgumentException("Activity不能为空!!!");
    }

    private ImmersionBar(DialogFragment dialogFragment, Dialog dialog) {
        WeakReference<DialogFragment> dialogFragmentWeakReference = new WeakReference<>(dialogFragment);
        WeakReference<Dialog> dialogWeakReference = new WeakReference<>(dialog);
        this.mActivity = ((DialogFragment) dialogFragmentWeakReference.get()).getActivity();
        Dialog dialog2 = (Dialog) dialogWeakReference.get();
        this.mDialog = dialog2;
        this.mWindow = dialog2.getWindow();
        this.mActivityName = this.mActivity.getClass().getName();
        this.mImmersionBarName = this.mActivityName + "_AND_" + ((DialogFragment) dialogFragmentWeakReference.get()).getClass().getName();
        initParams();
    }

    private ImmersionBar(Activity activity, Dialog dialog, String dialogTag) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        WeakReference<Dialog> dialogWeakReference = new WeakReference<>(dialog);
        this.mActivity = (Activity) activityWeakReference.get();
        Dialog dialog2 = (Dialog) dialogWeakReference.get();
        this.mDialog = dialog2;
        this.mWindow = dialog2.getWindow();
        this.mActivityName = this.mActivity.getClass().getName();
        this.mImmersionBarName = this.mActivityName + "_AND_" + dialogTag;
        initParams();
    }

    private void initParams() {
        ViewGroup viewGroup = (ViewGroup) this.mWindow.getDecorView();
        this.mDecorView = viewGroup;
        this.mContentView = (ViewGroup) viewGroup.findViewById(16908290);
        this.mConfig = new BarConfig(this.mActivity);
        if (mMap.get(this.mImmersionBarName) == null) {
            this.mBarParams = new BarParams();
            if (!isEmpty(this.mFragmentName)) {
                if (mMap.get(this.mActivityName) != null) {
                    if (Build.VERSION.SDK_INT == 19 || OSUtils.isEMUI3_1()) {
                        this.mBarParams.statusBarView = mMap.get(this.mActivityName).statusBarView;
                        this.mBarParams.navigationBarView = mMap.get(this.mActivityName).navigationBarView;
                    }
                    this.mBarParams.keyboardPatch = mMap.get(this.mActivityName).keyboardPatch;
                } else {
                    throw new IllegalArgumentException("在Fragment里使用时，请先在加载Fragment的Activity里初始化！！！");
                }
            }
            mMap.put(this.mImmersionBarName, this.mBarParams);
            return;
        }
        this.mBarParams = mMap.get(this.mImmersionBarName);
    }

    public static ImmersionBar with(Activity activity) {
        if (activity != null) {
            return new ImmersionBar(activity);
        }
        throw new IllegalArgumentException("Activity不能为null");
    }

    public static ImmersionBar with(Fragment fragment) {
        if (fragment != null) {
            return new ImmersionBar(fragment);
        }
        throw new IllegalArgumentException("Fragment不能为null");
    }

    public static ImmersionBar with(Activity activity, Fragment fragment) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity不能为null");
        } else if (fragment != null) {
            return new ImmersionBar(activity, fragment);
        } else {
            throw new IllegalArgumentException("Fragment不能为null");
        }
    }

    public static ImmersionBar with(DialogFragment dialogFragment, Dialog dialog) {
        if (dialogFragment == null) {
            throw new IllegalArgumentException("DialogFragment不能为null");
        } else if (dialog != null) {
            return new ImmersionBar(dialogFragment, dialog);
        } else {
            throw new IllegalArgumentException("Dialog不能为null");
        }
    }

    public static ImmersionBar with(Activity activity, Dialog dialog, String dialogTag) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity不能为null");
        } else if (dialog == null) {
            throw new IllegalArgumentException("Dialog不能为null");
        } else if (!isEmpty(dialogTag)) {
            return new ImmersionBar(activity, dialog, dialogTag);
        } else {
            throw new IllegalArgumentException("tag不能为null或空");
        }
    }

    public ImmersionBar transparentStatusBar() {
        this.mBarParams.statusBarColor = 0;
        return this;
    }

    public ImmersionBar transparentNavigationBar() {
        this.mBarParams.navigationBarColor = 0;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        this.mBarParams.fullScreen = true;
        return this;
    }

    public ImmersionBar transparentBar() {
        this.mBarParams.statusBarColor = 0;
        this.mBarParams.navigationBarColor = 0;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        this.mBarParams.fullScreen = true;
        return this;
    }

    public ImmersionBar statusBarColor(int statusBarColor) {
        return statusBarColorInt(ContextCompat.getColor(this.mActivity, statusBarColor));
    }

    public ImmersionBar statusBarColor(int statusBarColor, float alpha) {
        return statusBarColorInt(ContextCompat.getColor(this.mActivity, statusBarColor), alpha);
    }

    public ImmersionBar statusBarColor(int statusBarColor, int statusBarColorTransform, float alpha) {
        return statusBarColorInt(ContextCompat.getColor(this.mActivity, statusBarColor), ContextCompat.getColor(this.mActivity, statusBarColorTransform), alpha);
    }

    public ImmersionBar statusBarColor(String statusBarColor) {
        return statusBarColorInt(Color.parseColor(statusBarColor));
    }

    public ImmersionBar statusBarColor(String statusBarColor, float alpha) {
        return statusBarColorInt(Color.parseColor(statusBarColor), alpha);
    }

    public ImmersionBar statusBarColor(String statusBarColor, String statusBarColorTransform, float alpha) {
        return statusBarColorInt(Color.parseColor(statusBarColor), Color.parseColor(statusBarColorTransform), alpha);
    }

    public ImmersionBar statusBarColorInt(int statusBarColor) {
        this.mBarParams.statusBarColor = statusBarColor;
        return this;
    }

    public ImmersionBar statusBarColorInt(int statusBarColor, float alpha) {
        this.mBarParams.statusBarColor = statusBarColor;
        this.mBarParams.statusBarAlpha = alpha;
        return this;
    }

    public ImmersionBar statusBarColorInt(int statusBarColor, int statusBarColorTransform, float alpha) {
        this.mBarParams.statusBarColor = statusBarColor;
        this.mBarParams.statusBarColorTransform = statusBarColorTransform;
        this.mBarParams.statusBarAlpha = alpha;
        return this;
    }

    public ImmersionBar navigationBarColor(int navigationBarColor) {
        return navigationBarColorInt(ContextCompat.getColor(this.mActivity, navigationBarColor));
    }

    public ImmersionBar navigationBarColor(int navigationBarColor, float navigationAlpha) {
        return navigationBarColorInt(ContextCompat.getColor(this.mActivity, navigationBarColor), navigationAlpha);
    }

    public ImmersionBar navigationBarColor(int navigationBarColor, int navigationBarColorTransform, float navigationAlpha) {
        return navigationBarColorInt(ContextCompat.getColor(this.mActivity, navigationBarColor), ContextCompat.getColor(this.mActivity, navigationBarColorTransform), navigationAlpha);
    }

    public ImmersionBar navigationBarColor(String navigationBarColor) {
        return navigationBarColorInt(Color.parseColor(navigationBarColor));
    }

    public ImmersionBar navigationBarColor(String navigationBarColor, float navigationAlpha) {
        return navigationBarColorInt(Color.parseColor(navigationBarColor), navigationAlpha);
    }

    public ImmersionBar navigationBarColor(String navigationBarColor, String navigationBarColorTransform, float navigationAlpha) {
        return navigationBarColorInt(Color.parseColor(navigationBarColor), Color.parseColor(navigationBarColorTransform), navigationAlpha);
    }

    public ImmersionBar navigationBarColorInt(int navigationBarColor) {
        this.mBarParams.navigationBarColor = navigationBarColor;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        return this;
    }

    public ImmersionBar navigationBarColorInt(int navigationBarColor, float navigationAlpha) {
        this.mBarParams.navigationBarColor = navigationBarColor;
        this.mBarParams.navigationBarAlpha = navigationAlpha;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        return this;
    }

    public ImmersionBar navigationBarColorInt(int navigationBarColor, int navigationBarColorTransform, float navigationAlpha) {
        this.mBarParams.navigationBarColor = navigationBarColor;
        this.mBarParams.navigationBarColorTransform = navigationBarColorTransform;
        this.mBarParams.navigationBarAlpha = navigationAlpha;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        return this;
    }

    public ImmersionBar barColor(int barColor) {
        return barColorInt(ContextCompat.getColor(this.mActivity, barColor));
    }

    public ImmersionBar barColor(int barColor, float barAlpha) {
        return barColorInt(ContextCompat.getColor(this.mActivity, barColor), (float) barColor);
    }

    public ImmersionBar barColor(int barColor, int barColorTransform, float barAlpha) {
        return barColorInt(ContextCompat.getColor(this.mActivity, barColor), ContextCompat.getColor(this.mActivity, barColorTransform), barAlpha);
    }

    public ImmersionBar barColor(String barColor) {
        return barColorInt(Color.parseColor(barColor));
    }

    public ImmersionBar barColor(String barColor, float barAlpha) {
        return barColorInt(Color.parseColor(barColor), barAlpha);
    }

    public ImmersionBar barColor(String barColor, String barColorTransform, float barAlpha) {
        return barColorInt(Color.parseColor(barColor), Color.parseColor(barColorTransform), barAlpha);
    }

    public ImmersionBar barColorInt(int barColor) {
        this.mBarParams.statusBarColor = barColor;
        this.mBarParams.navigationBarColor = barColor;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        return this;
    }

    public ImmersionBar barColorInt(int barColor, float barAlpha) {
        this.mBarParams.statusBarColor = barColor;
        this.mBarParams.navigationBarColor = barColor;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        this.mBarParams.statusBarAlpha = barAlpha;
        this.mBarParams.navigationBarAlpha = barAlpha;
        return this;
    }

    public ImmersionBar barColorInt(int barColor, int barColorTransform, float barAlpha) {
        this.mBarParams.statusBarColor = barColor;
        this.mBarParams.navigationBarColor = barColor;
        BarParams barParams = this.mBarParams;
        barParams.navigationBarColorTemp = barParams.navigationBarColor;
        this.mBarParams.statusBarColorTransform = barColorTransform;
        this.mBarParams.navigationBarColorTransform = barColorTransform;
        this.mBarParams.statusBarAlpha = barAlpha;
        this.mBarParams.navigationBarAlpha = barAlpha;
        return this;
    }

    public ImmersionBar statusBarColorTransform(int statusBarColorTransform) {
        return statusBarColorTransformInt(ContextCompat.getColor(this.mActivity, statusBarColorTransform));
    }

    public ImmersionBar statusBarColorTransform(String statusBarColorTransform) {
        return statusBarColorTransformInt(Color.parseColor(statusBarColorTransform));
    }

    public ImmersionBar statusBarColorTransformInt(int statusBarColorTransform) {
        this.mBarParams.statusBarColorTransform = statusBarColorTransform;
        return this;
    }

    public ImmersionBar navigationBarColorTransform(int navigationBarColorTransform) {
        return navigationBarColorTransformInt(ContextCompat.getColor(this.mActivity, navigationBarColorTransform));
    }

    public ImmersionBar navigationBarColorTransform(String navigationBarColorTransform) {
        return navigationBarColorTransformInt(Color.parseColor(navigationBarColorTransform));
    }

    public ImmersionBar navigationBarColorTransformInt(int navigationBarColorTransform) {
        this.mBarParams.navigationBarColorTransform = navigationBarColorTransform;
        return this;
    }

    public ImmersionBar barColorTransform(int barColorTransform) {
        return barColorTransformInt(ContextCompat.getColor(this.mActivity, barColorTransform));
    }

    public ImmersionBar barColorTransform(String barColorTransform) {
        return barColorTransformInt(Color.parseColor(barColorTransform));
    }

    public ImmersionBar barColorTransformInt(int barColorTransform) {
        this.mBarParams.statusBarColorTransform = barColorTransform;
        this.mBarParams.navigationBarColorTransform = barColorTransform;
        return this;
    }

    public ImmersionBar addViewSupportTransformColor(View view) {
        return addViewSupportTransformColorInt(view, this.mBarParams.statusBarColorTransform);
    }

    public ImmersionBar addViewSupportTransformColor(View view, int viewColorAfterTransform) {
        return addViewSupportTransformColorInt(view, ContextCompat.getColor(this.mActivity, viewColorAfterTransform));
    }

    public ImmersionBar addViewSupportTransformColor(View view, int viewColorBeforeTransform, int viewColorAfterTransform) {
        return addViewSupportTransformColorInt(view, ContextCompat.getColor(this.mActivity, viewColorBeforeTransform), ContextCompat.getColor(this.mActivity, viewColorAfterTransform));
    }

    public ImmersionBar addViewSupportTransformColor(View view, String viewColorAfterTransform) {
        return addViewSupportTransformColorInt(view, Color.parseColor(viewColorAfterTransform));
    }

    public ImmersionBar addViewSupportTransformColor(View view, String viewColorBeforeTransform, String viewColorAfterTransform) {
        return addViewSupportTransformColorInt(view, Color.parseColor(viewColorBeforeTransform), Color.parseColor(viewColorAfterTransform));
    }

    public ImmersionBar addViewSupportTransformColorInt(View view, int viewColorAfterTransform) {
        if (view != null) {
            Map<Integer, Integer> map = new HashMap<>();
            map.put(Integer.valueOf(this.mBarParams.statusBarColor), Integer.valueOf(viewColorAfterTransform));
            this.mBarParams.viewMap.put(view, map);
            return this;
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar addViewSupportTransformColorInt(View view, int viewColorBeforeTransform, int viewColorAfterTransform) {
        if (view != null) {
            Map<Integer, Integer> map = new HashMap<>();
            map.put(Integer.valueOf(viewColorBeforeTransform), Integer.valueOf(viewColorAfterTransform));
            this.mBarParams.viewMap.put(view, map);
            return this;
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar viewAlpha(float viewAlpha) {
        this.mBarParams.viewAlpha = viewAlpha;
        return this;
    }

    public ImmersionBar removeSupportView(View view) {
        if (view != null) {
            if (this.mBarParams.viewMap.get(view).size() != 0) {
                this.mBarParams.viewMap.remove(view);
            }
            return this;
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar removeSupportAllView() {
        if (this.mBarParams.viewMap.size() != 0) {
            this.mBarParams.viewMap.clear();
        }
        return this;
    }

    public ImmersionBar fullScreen(boolean isFullScreen) {
        this.mBarParams.fullScreen = isFullScreen;
        return this;
    }

    public ImmersionBar statusBarAlpha(float statusAlpha) {
        this.mBarParams.statusBarAlpha = statusAlpha;
        return this;
    }

    public ImmersionBar navigationBarAlpha(float navigationAlpha) {
        this.mBarParams.navigationBarAlpha = navigationAlpha;
        return this;
    }

    public ImmersionBar barAlpha(float barAlpha) {
        this.mBarParams.statusBarAlpha = barAlpha;
        this.mBarParams.navigationBarAlpha = barAlpha;
        return this;
    }

    public ImmersionBar statusBarDarkFont(boolean isDarkFont) {
        return statusBarDarkFont(isDarkFont, 0.0f);
    }

    public ImmersionBar statusBarDarkFont(boolean isDarkFont, float statusAlpha) {
        this.mBarParams.darkFont = isDarkFont;
        if (!isDarkFont) {
            this.mBarParams.flymeOSStatusBarFontColor = 0;
        }
        if (isSupportStatusBarDarkFont()) {
            this.mBarParams.statusBarAlpha = 0.0f;
        } else {
            this.mBarParams.statusBarAlpha = statusAlpha;
        }
        return this;
    }

    public ImmersionBar flymeOSStatusBarFontColor(int flymeOSStatusBarFontColor) {
        this.mBarParams.flymeOSStatusBarFontColor = ContextCompat.getColor(this.mActivity, flymeOSStatusBarFontColor);
        return this;
    }

    public ImmersionBar flymeOSStatusBarFontColor(String flymeOSStatusBarFontColor) {
        this.mBarParams.flymeOSStatusBarFontColor = Color.parseColor(flymeOSStatusBarFontColor);
        return this;
    }

    public ImmersionBar flymeOSStatusBarFontColorInt(int flymeOSStatusBarFontColor) {
        this.mBarParams.flymeOSStatusBarFontColor = flymeOSStatusBarFontColor;
        return this;
    }

    public ImmersionBar hideBar(BarHide barHide) {
        this.mBarParams.barHide = barHide;
        if (Build.VERSION.SDK_INT == 19 || OSUtils.isEMUI3_1()) {
            if (this.mBarParams.barHide == BarHide.FLAG_HIDE_NAVIGATION_BAR || this.mBarParams.barHide == BarHide.FLAG_HIDE_BAR) {
                this.mBarParams.navigationBarColor = 0;
                this.mBarParams.fullScreenTemp = true;
            } else {
                BarParams barParams = this.mBarParams;
                barParams.navigationBarColor = barParams.navigationBarColorTemp;
                this.mBarParams.fullScreenTemp = false;
            }
        }
        return this;
    }

    public ImmersionBar fitsSystemWindows(boolean fits) {
        this.mBarParams.fits = fits;
        return this;
    }

    public ImmersionBar fitsSystemWindows(boolean fits, int statusBarColorContentView) {
        return fitsSystemWindows(fits, statusBarColorContentView, 17170444, 0.0f);
    }

    public ImmersionBar fitsSystemWindows(boolean fits, int statusBarColorContentView, int statusBarColorContentViewTransform, float statusBarContentViewAlpha) {
        this.mBarParams.fits = fits;
        this.mBarParams.statusBarColorContentView = ContextCompat.getColor(this.mActivity, statusBarColorContentView);
        this.mBarParams.statusBarColorContentViewTransform = ContextCompat.getColor(this.mActivity, statusBarColorContentViewTransform);
        this.mBarParams.statusBarContentViewAlpha = statusBarContentViewAlpha;
        this.mBarParams.statusBarColorContentView = ContextCompat.getColor(this.mActivity, statusBarColorContentView);
        this.mContentView.setBackgroundColor(ColorUtils.blendARGB(this.mBarParams.statusBarColorContentView, this.mBarParams.statusBarColorContentViewTransform, this.mBarParams.statusBarContentViewAlpha));
        return this;
    }

    public ImmersionBar statusBarView(View view) {
        if (view != null) {
            this.mBarParams.statusBarViewByHeight = view;
            return this;
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar statusBarView(int viewId) {
        View view = this.mActivity.findViewById(viewId);
        if (view != null) {
            return statusBarView(view);
        }
        throw new IllegalArgumentException("未找到viewId");
    }

    public ImmersionBar statusBarView(int viewId, View rootView) {
        View view = rootView.findViewById(viewId);
        if (view != null) {
            return statusBarView(view);
        }
        throw new IllegalArgumentException("未找到viewId");
    }

    public ImmersionBar supportActionBar(boolean isSupportActionBar) {
        this.mBarParams.isSupportActionBar = isSupportActionBar;
        return this;
    }

    public ImmersionBar titleBar(View view) {
        if (view != null) {
            return titleBar(view, true);
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar titleBar(View view, boolean statusBarFlag) {
        if (view != null) {
            this.mBarParams.titleBarView = view;
            this.mBarParams.statusBarFlag = statusBarFlag;
            setTitleBar();
            return this;
        }
        throw new IllegalArgumentException("View参数不能为空");
    }

    public ImmersionBar titleBar(int viewId) {
        View view = this.mActivity.findViewById(viewId);
        if (view != null) {
            return titleBar(view, true);
        }
        throw new IllegalArgumentException("参数错误");
    }

    public ImmersionBar titleBar(int viewId, boolean statusBarFlag) {
        View view = this.mActivity.findViewById(viewId);
        if (view != null) {
            return titleBar(view, statusBarFlag);
        }
        throw new IllegalArgumentException("参数错误");
    }

    public ImmersionBar titleBar(int viewId, View rootView) {
        View view = rootView.findViewById(viewId);
        if (view != null) {
            return titleBar(view, true);
        }
        throw new IllegalArgumentException("参数错误");
    }

    public ImmersionBar titleBar(int viewId, View rootView, boolean statusBarFlag) {
        View view = rootView.findViewById(viewId);
        if (view != null) {
            return titleBar(view, statusBarFlag);
        }
        throw new IllegalArgumentException("参数错误");
    }

    public ImmersionBar titleBarMarginTop(int viewId) {
        return titleBarMarginTop(this.mActivity.findViewById(viewId));
    }

    public ImmersionBar titleBarMarginTop(int viewId, View rootView) {
        return titleBarMarginTop(rootView.findViewById(viewId));
    }

    public ImmersionBar titleBarMarginTop(View view) {
        if (view != null) {
            this.mBarParams.titleBarViewMarginTop = view;
            if (!this.mBarParams.titleBarViewMarginTopFlag) {
                setTitleBarMarginTop();
            }
            return this;
        }
        throw new IllegalArgumentException("参数错误");
    }

    public ImmersionBar statusBarColorTransformEnable(boolean statusBarFlag) {
        this.mBarParams.statusBarFlag = statusBarFlag;
        return this;
    }

    public ImmersionBar reset() {
        BarParams barParamsTemp = this.mBarParams;
        this.mBarParams = new BarParams();
        if (Build.VERSION.SDK_INT == 19 || OSUtils.isEMUI3_1()) {
            this.mBarParams.statusBarView = barParamsTemp.statusBarView;
            this.mBarParams.navigationBarView = barParamsTemp.navigationBarView;
        }
        this.mBarParams.keyboardPatch = barParamsTemp.keyboardPatch;
        mMap.put(this.mImmersionBarName, this.mBarParams);
        return this;
    }

    public ImmersionBar addTag(String tag) {
        String tag2 = this.mActivityName + "_TAG_" + tag;
        if (!isEmpty(tag2)) {
            mTagMap.put(tag2, this.mBarParams.clone());
            ArrayList<String> tagList = mTagKeyMap.get(this.mActivityName);
            if (tagList == null) {
                tagList = new ArrayList<>();
                tagList.add(tag2);
            } else if (!tagList.contains(tag2)) {
                tagList.add(tag2);
            }
            mTagKeyMap.put(this.mActivityName, tagList);
        }
        return this;
    }

    public ImmersionBar getTag(String tag) {
        if (!isEmpty(tag)) {
            Map<String, BarParams> map = mTagMap;
            BarParams barParams = map.get(this.mActivityName + "_TAG_" + tag);
            if (barParams != null) {
                this.mBarParams = barParams.clone();
            }
        }
        return this;
    }

    public ImmersionBar keyboardEnable(boolean enable) {
        return keyboardEnable(enable, 18);
    }

    public ImmersionBar keyboardEnable(boolean enable, int keyboardMode) {
        this.mBarParams.keyboardEnable = enable;
        this.mBarParams.keyboardMode = keyboardMode;
        return this;
    }

    public ImmersionBar keyboardMode(int keyboardMode) {
        this.mBarParams.keyboardMode = keyboardMode;
        return this;
    }

    public ImmersionBar setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        if (this.mBarParams.onKeyboardListener == null) {
            this.mBarParams.onKeyboardListener = onKeyboardListener;
        }
        return this;
    }

    public ImmersionBar navigationBarEnable(boolean navigationBarEnable) {
        this.mBarParams.navigationBarEnable = navigationBarEnable;
        return this;
    }

    public ImmersionBar navigationBarWithKitkatEnable(boolean navigationBarWithKitkatEnable) {
        this.mBarParams.navigationBarWithKitkatEnable = navigationBarWithKitkatEnable;
        return this;
    }

    @Deprecated
    public ImmersionBar fixMarginAtBottom(boolean fixMarginAtBottom) {
        this.mBarParams.fixMarginAtBottom = fixMarginAtBottom;
        return this;
    }

    public void init() {
        mMap.put(this.mImmersionBarName, this.mBarParams);
        initBar();
        setStatusBarView();
        transformView();
        keyboardEnable();
        registerEMUI3_x();
    }

    public void destroy() {
        unRegisterEMUI3_x();
        if (this.mBarParams.keyboardPatch != null) {
            this.mBarParams.keyboardPatch.disable(this.mBarParams.keyboardMode);
            this.mBarParams.keyboardPatch = null;
        }
        if (this.mDecorView != null) {
            this.mDecorView = null;
        }
        if (this.mContentView != null) {
            this.mContentView = null;
        }
        if (this.mConfig != null) {
            this.mConfig = null;
        }
        if (this.mWindow != null) {
            this.mWindow = null;
        }
        if (this.mDialog != null) {
            this.mDialog = null;
        }
        if (this.mActivity != null) {
            this.mActivity = null;
        }
        if (!isEmpty(this.mImmersionBarName)) {
            if (this.mBarParams != null) {
                this.mBarParams = null;
            }
            ArrayList<String> tagList = mTagKeyMap.get(this.mActivityName);
            if (tagList != null && tagList.size() > 0) {
                Iterator<String> it = tagList.iterator();
                while (it.hasNext()) {
                    mTagMap.remove(it.next());
                }
                mTagKeyMap.remove(this.mActivityName);
            }
            mMap.remove(this.mImmersionBarName);
        }
    }

    private void initBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            int uiFlags = 256;
            if (Build.VERSION.SDK_INT < 21 || OSUtils.isEMUI3_1()) {
                initBarBelowLOLLIPOP();
                solveNavigation();
            } else {
                uiFlags = setStatusBarDarkFont(initBarAboveLOLLIPOP(256));
                supportActionBar();
            }
            this.mWindow.getDecorView().setSystemUiVisibility(hideBar(uiFlags));
        }
        if (OSUtils.isMIUI6Later() != 0) {
            setMIUIStatusBarDarkFont(this.mWindow, this.mBarParams.darkFont);
        }
        if (!OSUtils.isFlymeOS4Later()) {
            return;
        }
        if (this.mBarParams.flymeOSStatusBarFontColor != 0) {
            FlymeOSStatusBarFontUtils.setStatusBarDarkIcon(this.mActivity, this.mBarParams.flymeOSStatusBarFontColor);
        } else if (Build.VERSION.SDK_INT < 23) {
            FlymeOSStatusBarFontUtils.setStatusBarDarkIcon(this.mActivity, this.mBarParams.darkFont);
        }
    }

    private int initBarAboveLOLLIPOP(int uiFlags) {
        int uiFlags2 = uiFlags | 1024;
        if (this.mBarParams.fullScreen && this.mBarParams.navigationBarEnable) {
            uiFlags2 |= 512;
        }
        this.mWindow.clearFlags(ConnectionsManager.FileTypeFile);
        if (this.mConfig.hasNavigtionBar()) {
            this.mWindow.clearFlags(134217728);
        }
        this.mWindow.addFlags(Integer.MIN_VALUE);
        if (this.mBarParams.statusBarFlag) {
            this.mWindow.setStatusBarColor(ColorUtils.blendARGB(this.mBarParams.statusBarColor, this.mBarParams.statusBarColorTransform, this.mBarParams.statusBarAlpha));
        } else {
            this.mWindow.setStatusBarColor(ColorUtils.blendARGB(this.mBarParams.statusBarColor, 0, this.mBarParams.statusBarAlpha));
        }
        if (this.mBarParams.navigationBarEnable) {
            this.mWindow.setNavigationBarColor(ColorUtils.blendARGB(this.mBarParams.navigationBarColor, this.mBarParams.navigationBarColorTransform, this.mBarParams.navigationBarAlpha));
        }
        return uiFlags2;
    }

    private void initBarBelowLOLLIPOP() {
        this.mWindow.addFlags(ConnectionsManager.FileTypeFile);
        setupStatusBarView();
        if (this.mConfig.hasNavigtionBar()) {
            if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
                this.mWindow.clearFlags(134217728);
            } else {
                this.mWindow.addFlags(134217728);
            }
            setupNavBarView();
        }
    }

    private void setupStatusBarView() {
        if (this.mBarParams.statusBarView == null) {
            this.mBarParams.statusBarView = new View(this.mActivity);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, this.mConfig.getStatusBarHeight());
        params.gravity = 48;
        this.mBarParams.statusBarView.setLayoutParams(params);
        if (this.mBarParams.statusBarFlag) {
            this.mBarParams.statusBarView.setBackgroundColor(ColorUtils.blendARGB(this.mBarParams.statusBarColor, this.mBarParams.statusBarColorTransform, this.mBarParams.statusBarAlpha));
        } else {
            this.mBarParams.statusBarView.setBackgroundColor(ColorUtils.blendARGB(this.mBarParams.statusBarColor, 0, this.mBarParams.statusBarAlpha));
        }
        this.mBarParams.statusBarView.setVisibility(0);
        ViewGroup viewGroup = (ViewGroup) this.mBarParams.statusBarView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mBarParams.statusBarView);
        }
        this.mDecorView.addView(this.mBarParams.statusBarView);
    }

    private void setupNavBarView() {
        FrameLayout.LayoutParams params;
        if (this.mBarParams.navigationBarView == null) {
            this.mBarParams.navigationBarView = new View(this.mActivity);
        }
        if (this.mConfig.isNavigationAtBottom()) {
            params = new FrameLayout.LayoutParams(-1, this.mConfig.getNavigationBarHeight());
            params.gravity = 80;
        } else {
            params = new FrameLayout.LayoutParams(this.mConfig.getNavigationBarWidth(), -1);
            params.gravity = GravityCompat.END;
        }
        this.mBarParams.navigationBarView.setLayoutParams(params);
        if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
            this.mBarParams.navigationBarView.setBackgroundColor(0);
        } else if (this.mBarParams.fullScreen || this.mBarParams.navigationBarColorTransform != 0) {
            this.mBarParams.navigationBarView.setBackgroundColor(ColorUtils.blendARGB(this.mBarParams.navigationBarColor, this.mBarParams.navigationBarColorTransform, this.mBarParams.navigationBarAlpha));
        } else {
            this.mBarParams.navigationBarView.setBackgroundColor(ColorUtils.blendARGB(this.mBarParams.navigationBarColor, -16777216, this.mBarParams.navigationBarAlpha));
        }
        this.mBarParams.navigationBarView.setVisibility(0);
        ViewGroup viewGroup = (ViewGroup) this.mBarParams.navigationBarView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.mBarParams.navigationBarView);
        }
        this.mDecorView.addView(this.mBarParams.navigationBarView);
    }

    private void solveNavigation() {
        int count = this.mContentView.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = this.mContentView.getChildAt(i);
            if (childView instanceof ViewGroup) {
                if (childView instanceof DrawerLayout) {
                    View childAt1 = ((DrawerLayout) childView).getChildAt(0);
                    if (childAt1 != null) {
                        this.mBarParams.systemWindows = childAt1.getFitsSystemWindows();
                        if (this.mBarParams.systemWindows) {
                            this.mContentView.setPadding(0, 0, 0, 0);
                            return;
                        }
                    } else {
                        continue;
                    }
                } else {
                    this.mBarParams.systemWindows = childView.getFitsSystemWindows();
                    if (this.mBarParams.systemWindows) {
                        this.mContentView.setPadding(0, 0, 0, 0);
                        return;
                    }
                }
            }
        }
        if (!this.mConfig.hasNavigtionBar() || this.mBarParams.fullScreenTemp || this.mBarParams.fullScreen) {
            if (this.mBarParams.isSupportActionBar) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight() + 10, 0, 0);
            } else if (this.mBarParams.fits) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), 0, 0);
            } else {
                this.mContentView.setPadding(0, 0, 0, 0);
            }
        } else if (this.mConfig.isNavigationAtBottom()) {
            if (!this.mBarParams.isSupportActionBar) {
                if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
                    if (this.mBarParams.fits) {
                        this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), 0, 0);
                    } else {
                        this.mContentView.setPadding(0, 0, 0, 0);
                    }
                } else if (this.mBarParams.fits) {
                    this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), 0, this.mConfig.getNavigationBarHeight());
                } else {
                    this.mContentView.setPadding(0, 0, 0, this.mConfig.getNavigationBarHeight());
                }
            } else if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight() + 10, 0, 0);
            } else {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight() + 10, 0, this.mConfig.getNavigationBarHeight());
            }
        } else if (!this.mBarParams.isSupportActionBar) {
            if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
                if (this.mBarParams.fits) {
                    this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), 0, 0);
                } else {
                    this.mContentView.setPadding(0, 0, 0, 0);
                }
            } else if (this.mBarParams.fits) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), this.mConfig.getNavigationBarWidth(), 0);
            } else {
                this.mContentView.setPadding(0, 0, this.mConfig.getNavigationBarWidth(), 0);
            }
        } else if (!this.mBarParams.navigationBarEnable || !this.mBarParams.navigationBarWithKitkatEnable) {
            this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight() + 10, 0, 0);
        } else {
            this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight() + 10, this.mConfig.getNavigationBarWidth(), 0);
        }
    }

    private void registerEMUI3_x() {
        if ((OSUtils.isEMUI3_1() || OSUtils.isEMUI3_0()) && this.mConfig.hasNavigtionBar() && this.mBarParams.navigationBarEnable && this.mBarParams.navigationBarWithKitkatEnable) {
            if (this.mBarParams.navigationStatusObserver == null && this.mBarParams.navigationBarView != null) {
                this.mBarParams.navigationStatusObserver = new ContentObserver(new Handler()) {
                    public void onChange(boolean selfChange) {
                        if (Settings.System.getInt(ImmersionBar.this.mActivity.getContentResolver(), ImmersionBar.NAVIGATIONBAR_IS_MIN, 0) == 1) {
                            ImmersionBar.this.mBarParams.navigationBarView.setVisibility(8);
                            ImmersionBar.this.mContentView.setPadding(0, ImmersionBar.this.mContentView.getPaddingTop(), 0, 0);
                            return;
                        }
                        ImmersionBar.this.mBarParams.navigationBarView.setVisibility(0);
                        if (ImmersionBar.this.mBarParams.systemWindows) {
                            ImmersionBar.this.mContentView.setPadding(0, ImmersionBar.this.mContentView.getPaddingTop(), 0, 0);
                        } else if (ImmersionBar.this.mConfig.isNavigationAtBottom()) {
                            ImmersionBar.this.mContentView.setPadding(0, ImmersionBar.this.mContentView.getPaddingTop(), 0, ImmersionBar.this.mConfig.getNavigationBarHeight());
                        } else {
                            ImmersionBar.this.mContentView.setPadding(0, ImmersionBar.this.mContentView.getPaddingTop(), ImmersionBar.this.mConfig.getNavigationBarWidth(), 0);
                        }
                    }
                };
            }
            this.mActivity.getContentResolver().registerContentObserver(Settings.System.getUriFor(NAVIGATIONBAR_IS_MIN), true, this.mBarParams.navigationStatusObserver);
        }
    }

    private void unRegisterEMUI3_x() {
        if ((OSUtils.isEMUI3_1() || OSUtils.isEMUI3_0()) && this.mConfig.hasNavigtionBar() && this.mBarParams.navigationBarEnable && this.mBarParams.navigationBarWithKitkatEnable && this.mBarParams.navigationStatusObserver != null && this.mBarParams.navigationBarView != null) {
            this.mActivity.getContentResolver().unregisterContentObserver(this.mBarParams.navigationStatusObserver);
        }
    }

    /* renamed from: com.gyf.barlibrary.ImmersionBar$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$gyf$barlibrary$BarHide;

        static {
            int[] iArr = new int[BarHide.values().length];
            $SwitchMap$com$gyf$barlibrary$BarHide = iArr;
            try {
                iArr[BarHide.FLAG_HIDE_BAR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$gyf$barlibrary$BarHide[BarHide.FLAG_HIDE_STATUS_BAR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$gyf$barlibrary$BarHide[BarHide.FLAG_HIDE_NAVIGATION_BAR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$gyf$barlibrary$BarHide[BarHide.FLAG_SHOW_BAR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private int hideBar(int uiFlags) {
        if (Build.VERSION.SDK_INT >= 16) {
            int i = AnonymousClass4.$SwitchMap$com$gyf$barlibrary$BarHide[this.mBarParams.barHide.ordinal()];
            if (i == 1) {
                uiFlags |= UIMsg.m_AppUI.MSG_COMPASS_DISPLAY;
            } else if (i == 2) {
                uiFlags |= 1028;
            } else if (i == 3) {
                uiFlags |= 514;
            } else if (i == 4) {
                uiFlags |= 0;
            }
        }
        return uiFlags | 4096;
    }

    private int setStatusBarDarkFont(int uiFlags) {
        if (Build.VERSION.SDK_INT < 23 || !this.mBarParams.darkFont) {
            return uiFlags;
        }
        return uiFlags | 8192;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.Integer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.Integer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void transformView() {
        /*
            r10 = this;
            com.gyf.barlibrary.BarParams r0 = r10.mBarParams
            java.util.Map<android.view.View, java.util.Map<java.lang.Integer, java.lang.Integer>> r0 = r0.viewMap
            int r0 = r0.size()
            if (r0 == 0) goto L_0x009a
            com.gyf.barlibrary.BarParams r0 = r10.mBarParams
            java.util.Map<android.view.View, java.util.Map<java.lang.Integer, java.lang.Integer>> r0 = r0.viewMap
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r1 = r0.iterator()
        L_0x0016:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x009a
            java.lang.Object r2 = r1.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            java.lang.Object r3 = r2.getKey()
            android.view.View r3 = (android.view.View) r3
            java.lang.Object r4 = r2.getValue()
            java.util.Map r4 = (java.util.Map) r4
            com.gyf.barlibrary.BarParams r5 = r10.mBarParams
            int r5 = r5.statusBarColor
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            com.gyf.barlibrary.BarParams r6 = r10.mBarParams
            int r6 = r6.statusBarColorTransform
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            java.util.Set r7 = r4.entrySet()
            java.util.Iterator r7 = r7.iterator()
        L_0x0046:
            boolean r8 = r7.hasNext()
            if (r8 == 0) goto L_0x0061
            java.lang.Object r8 = r7.next()
            java.util.Map$Entry r8 = (java.util.Map.Entry) r8
            java.lang.Object r9 = r8.getKey()
            r5 = r9
            java.lang.Integer r5 = (java.lang.Integer) r5
            java.lang.Object r9 = r8.getValue()
            r6 = r9
            java.lang.Integer r6 = (java.lang.Integer) r6
            goto L_0x0046
        L_0x0061:
            if (r3 == 0) goto L_0x0098
            com.gyf.barlibrary.BarParams r7 = r10.mBarParams
            float r7 = r7.viewAlpha
            r8 = 0
            float r7 = r7 - r8
            float r7 = java.lang.Math.abs(r7)
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 != 0) goto L_0x0085
            int r7 = r5.intValue()
            int r8 = r6.intValue()
            com.gyf.barlibrary.BarParams r9 = r10.mBarParams
            float r9 = r9.statusBarAlpha
            int r7 = androidx.core.graphics.ColorUtils.blendARGB(r7, r8, r9)
            r3.setBackgroundColor(r7)
            goto L_0x0098
        L_0x0085:
            int r7 = r5.intValue()
            int r8 = r6.intValue()
            com.gyf.barlibrary.BarParams r9 = r10.mBarParams
            float r9 = r9.viewAlpha
            int r7 = androidx.core.graphics.ColorUtils.blendARGB(r7, r8, r9)
            r3.setBackgroundColor(r7)
        L_0x0098:
            goto L_0x0016
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.gyf.barlibrary.ImmersionBar.transformView():void");
    }

    private void setStatusBarView() {
        if (Build.VERSION.SDK_INT >= 19 && this.mBarParams.statusBarViewByHeight != null) {
            ViewGroup.LayoutParams params = this.mBarParams.statusBarViewByHeight.getLayoutParams();
            params.height = this.mConfig.getStatusBarHeight();
            this.mBarParams.statusBarViewByHeight.setLayoutParams(params);
        }
    }

    private void setTitleBar() {
        if (Build.VERSION.SDK_INT >= 19 && this.mBarParams.titleBarView != null) {
            final ViewGroup.LayoutParams layoutParams = this.mBarParams.titleBarView.getLayoutParams();
            if (layoutParams.height == -2 || layoutParams.height == -1) {
                this.mBarParams.titleBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        ImmersionBar.this.mBarParams.titleBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (ImmersionBar.this.mBarParams.titleBarHeight == 0) {
                            ImmersionBar.this.mBarParams.titleBarHeight = ImmersionBar.this.mBarParams.titleBarView.getHeight() + ImmersionBar.this.mConfig.getStatusBarHeight();
                        }
                        if (ImmersionBar.this.mBarParams.titleBarPaddingTopHeight == 0) {
                            ImmersionBar.this.mBarParams.titleBarPaddingTopHeight = ImmersionBar.this.mBarParams.titleBarView.getPaddingTop() + ImmersionBar.this.mConfig.getStatusBarHeight();
                        }
                        layoutParams.height = ImmersionBar.this.mBarParams.titleBarHeight;
                        ImmersionBar.this.mBarParams.titleBarView.setPadding(ImmersionBar.this.mBarParams.titleBarView.getPaddingLeft(), ImmersionBar.this.mBarParams.titleBarPaddingTopHeight, ImmersionBar.this.mBarParams.titleBarView.getPaddingRight(), ImmersionBar.this.mBarParams.titleBarView.getPaddingBottom());
                        ImmersionBar.this.mBarParams.titleBarView.setLayoutParams(layoutParams);
                    }
                });
                return;
            }
            if (this.mBarParams.titleBarHeight == 0) {
                this.mBarParams.titleBarHeight = layoutParams.height + this.mConfig.getStatusBarHeight();
            }
            if (this.mBarParams.titleBarPaddingTopHeight == 0) {
                BarParams barParams = this.mBarParams;
                barParams.titleBarPaddingTopHeight = barParams.titleBarView.getPaddingTop() + this.mConfig.getStatusBarHeight();
            }
            layoutParams.height = this.mBarParams.titleBarHeight;
            this.mBarParams.titleBarView.setPadding(this.mBarParams.titleBarView.getPaddingLeft(), this.mBarParams.titleBarPaddingTopHeight, this.mBarParams.titleBarView.getPaddingRight(), this.mBarParams.titleBarView.getPaddingBottom());
            this.mBarParams.titleBarView.setLayoutParams(layoutParams);
        }
    }

    private void setTitleBarMarginTop() {
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mBarParams.titleBarViewMarginTop.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + this.mConfig.getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
            this.mBarParams.titleBarViewMarginTopFlag = true;
        }
    }

    private void supportActionBar() {
        if (Build.VERSION.SDK_INT >= 21 && !OSUtils.isEMUI3_1()) {
            int count = this.mContentView.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = this.mContentView.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    this.mBarParams.systemWindows = childView.getFitsSystemWindows();
                    if (this.mBarParams.systemWindows) {
                        this.mContentView.setPadding(0, 0, 0, 0);
                        return;
                    }
                }
            }
            if (this.mBarParams.isSupportActionBar) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight() + this.mConfig.getActionBarHeight(), 0, 0);
            } else if (this.mBarParams.fits) {
                this.mContentView.setPadding(0, this.mConfig.getStatusBarHeight(), 0, 0);
            } else {
                this.mContentView.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void keyboardEnable() {
        if (Build.VERSION.SDK_INT >= 19) {
            if (this.mBarParams.keyboardPatch == null) {
                this.mBarParams.keyboardPatch = KeyboardPatch.patch(this.mActivity, this.mWindow);
            }
            this.mBarParams.keyboardPatch.setBarParams(this.mBarParams);
            if (this.mBarParams.keyboardEnable) {
                this.mBarParams.keyboardPatch.enable(this.mBarParams.keyboardMode);
            } else {
                this.mBarParams.keyboardPatch.disable(this.mBarParams.keyboardMode);
            }
        }
    }

    private void setMIUIStatusBarDarkFont(Window window, boolean darkFont) {
        if (window != null) {
            Class clazz = window.getClass();
            try {
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                int darkModeFlag = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
                if (darkFont) {
                    extraFlagField.invoke(window, new Object[]{Integer.valueOf(darkModeFlag), Integer.valueOf(darkModeFlag)});
                    return;
                }
                extraFlagField.invoke(window, new Object[]{0, Integer.valueOf(darkModeFlag)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setTitleBar(final Activity activity, final View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp.height == -2) {
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        lp.height = view.getHeight() + ImmersionBar.getStatusBarHeight(activity);
                        View view = view;
                        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + ImmersionBar.getStatusBarHeight(activity), view.getPaddingRight(), view.getPaddingBottom());
                    }
                });
                return;
            }
            lp.height += getStatusBarHeight(activity);
            view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(activity), view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    public static void setStatusBarView(Activity activity, View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = getStatusBarHeight(activity);
            view.setLayoutParams(params);
        }
    }

    public static void setTitleBarMarginTop(Activity activity, View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(activity), layoutParams.rightMargin, layoutParams.bottomMargin);
        }
    }

    public static void setFitsSystemWindows(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(16908290);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    public static boolean hasNavigationBar(Activity activity) {
        return new BarConfig(activity).hasNavigtionBar();
    }

    public static int getNavigationBarHeight(Activity activity) {
        return new BarConfig(activity).getNavigationBarHeight();
    }

    public static int getNavigationBarWidth(Activity activity) {
        return new BarConfig(activity).getNavigationBarWidth();
    }

    public static boolean isNavigationAtBottom(Activity activity) {
        return new BarConfig(activity).isNavigationAtBottom();
    }

    public static int getStatusBarHeight(Activity activity) {
        return new BarConfig(activity).getStatusBarHeight();
    }

    public static int getActionBarHeight(Activity activity) {
        return new BarConfig(activity).getActionBarHeight();
    }

    public static boolean isSupportStatusBarDarkFont() {
        if (OSUtils.isMIUI6Later() || OSUtils.isFlymeOS4Later() || Build.VERSION.SDK_INT >= 23) {
            return true;
        }
        return false;
    }

    public static void hideStatusBar(Window window) {
        window.setFlags(1024, 1024);
    }

    public BarParams getBarParams() {
        return this.mBarParams;
    }

    public BarParams getTagBarParams(String tag) {
        if (isEmpty(tag)) {
            return null;
        }
        Map<String, BarParams> map = mTagMap;
        return map.get(this.mActivityName + "_TAG_" + tag);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
