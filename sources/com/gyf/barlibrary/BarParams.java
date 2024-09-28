package com.gyf.barlibrary;

import android.database.ContentObserver;
import android.view.View;
import java.util.HashMap;
import java.util.Map;

public class BarParams implements Cloneable {
    public BarHide barHide = BarHide.FLAG_SHOW_BAR;
    public boolean darkFont = false;
    public boolean fits = false;
    @Deprecated
    public boolean fixMarginAtBottom = false;
    public int flymeOSStatusBarFontColor;
    public boolean fullScreen = false;
    public boolean fullScreenTemp = false;
    public boolean isSupportActionBar = false;
    public boolean keyboardEnable = false;
    public int keyboardMode = 18;
    public KeyboardPatch keyboardPatch;
    float navigationBarAlpha = 0.0f;
    public int navigationBarColor = -16777216;
    public int navigationBarColorTemp = this.navigationBarColor;
    public int navigationBarColorTransform = -16777216;
    public boolean navigationBarEnable = true;
    public View navigationBarView;
    public boolean navigationBarWithKitkatEnable = true;
    public ContentObserver navigationStatusObserver;
    public OnKeyboardListener onKeyboardListener;
    public float statusBarAlpha = 0.0f;
    public int statusBarColor = 0;
    public int statusBarColorContentView = 0;
    public int statusBarColorContentViewTransform = -16777216;
    public int statusBarColorTransform = -16777216;
    public float statusBarContentViewAlpha = 0.0f;
    public boolean statusBarFlag = true;
    public View statusBarView;
    public View statusBarViewByHeight;
    public boolean systemWindows = false;
    public int titleBarHeight;
    public int titleBarPaddingTopHeight;
    public View titleBarView;
    public View titleBarViewMarginTop;
    public boolean titleBarViewMarginTopFlag = false;
    public float viewAlpha = 0.0f;
    public Map<View, Map<Integer, Integer>> viewMap = new HashMap();

    /* access modifiers changed from: protected */
    public BarParams clone() {
        try {
            return (BarParams) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
