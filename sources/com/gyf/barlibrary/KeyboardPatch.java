package com.gyf.barlibrary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;

public class KeyboardPatch {
    /* access modifiers changed from: private */
    public int actionBarHeight;
    /* access modifiers changed from: private */
    public int keyboardHeightPrevious;
    private Activity mActivity;
    /* access modifiers changed from: private */
    public BarParams mBarParams;
    /* access modifiers changed from: private */
    public View mChildView;
    /* access modifiers changed from: private */
    public View mContentView;
    /* access modifiers changed from: private */
    public View mDecorView;
    private Window mWindow;
    /* access modifiers changed from: private */
    public boolean navigationAtBottom;
    /* access modifiers changed from: private */
    public int navigationBarHeight;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    /* access modifiers changed from: private */
    public int paddingBottom;
    /* access modifiers changed from: private */
    public int paddingLeft;
    /* access modifiers changed from: private */
    public int paddingRight;
    /* access modifiers changed from: private */
    public int paddingTop;
    /* access modifiers changed from: private */
    public int statusBarHeight;

    private KeyboardPatch(Activity activity) {
        this(activity, ((FrameLayout) activity.getWindow().getDecorView().findViewById(16908290)).getChildAt(0));
    }

    private KeyboardPatch(Activity activity, View contentView) {
        this(activity, (Dialog) null, "", contentView);
    }

    private KeyboardPatch(Activity activity, Dialog dialog, String tag) {
        this(activity, dialog, tag, dialog.getWindow().findViewById(16908290));
    }

    private KeyboardPatch(Activity activity, Dialog dialog, String tag, View contentView) {
        View view;
        BarParams barParams;
        this.onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int keyboardHeight;
                int diff;
                int keyboardHeight2;
                if (KeyboardPatch.this.navigationAtBottom) {
                    Rect r = new Rect();
                    KeyboardPatch.this.mDecorView.getWindowVisibleDisplayFrame(r);
                    boolean isPopup = false;
                    if (KeyboardPatch.this.mBarParams.systemWindows) {
                        int keyboardHeight3 = (KeyboardPatch.this.mContentView.getHeight() - r.bottom) - KeyboardPatch.this.navigationBarHeight;
                        if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                            if (keyboardHeight3 > KeyboardPatch.this.navigationBarHeight) {
                                isPopup = true;
                            }
                            KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight3);
                        }
                    } else if (KeyboardPatch.this.mChildView != null) {
                        if (KeyboardPatch.this.mBarParams.isSupportActionBar) {
                            diff = ((KeyboardPatch.this.mContentView.getHeight() + KeyboardPatch.this.statusBarHeight) + KeyboardPatch.this.actionBarHeight) - r.bottom;
                        } else if (KeyboardPatch.this.mBarParams.fits) {
                            diff = (KeyboardPatch.this.mContentView.getHeight() + KeyboardPatch.this.statusBarHeight) - r.bottom;
                        } else {
                            diff = KeyboardPatch.this.mContentView.getHeight() - r.bottom;
                        }
                        if (KeyboardPatch.this.mBarParams.fullScreen) {
                            keyboardHeight2 = diff - KeyboardPatch.this.navigationBarHeight;
                        } else {
                            keyboardHeight2 = diff;
                        }
                        if (KeyboardPatch.this.mBarParams.fullScreen && diff == KeyboardPatch.this.navigationBarHeight) {
                            diff -= KeyboardPatch.this.navigationBarHeight;
                        }
                        if (keyboardHeight2 != KeyboardPatch.this.keyboardHeightPrevious) {
                            KeyboardPatch.this.mContentView.setPadding(KeyboardPatch.this.paddingLeft, KeyboardPatch.this.paddingTop, KeyboardPatch.this.paddingRight, KeyboardPatch.this.paddingBottom + diff);
                            int unused = KeyboardPatch.this.keyboardHeightPrevious = keyboardHeight2;
                            if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                                if (keyboardHeight2 > KeyboardPatch.this.navigationBarHeight) {
                                    isPopup = true;
                                }
                                KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight2);
                            }
                        }
                    } else {
                        int diff2 = KeyboardPatch.this.mContentView.getHeight() - r.bottom;
                        if (!KeyboardPatch.this.mBarParams.navigationBarEnable || !KeyboardPatch.this.mBarParams.navigationBarWithKitkatEnable) {
                            keyboardHeight = diff2;
                        } else {
                            if (Build.VERSION.SDK_INT == 19 || OSUtils.isEMUI3_1()) {
                                keyboardHeight = diff2 - KeyboardPatch.this.navigationBarHeight;
                            } else if (!KeyboardPatch.this.mBarParams.fullScreen) {
                                keyboardHeight = diff2;
                            } else {
                                keyboardHeight = diff2 - KeyboardPatch.this.navigationBarHeight;
                            }
                            if (KeyboardPatch.this.mBarParams.fullScreen && diff2 == KeyboardPatch.this.navigationBarHeight) {
                                diff2 -= KeyboardPatch.this.navigationBarHeight;
                            }
                        }
                        if (keyboardHeight != KeyboardPatch.this.keyboardHeightPrevious) {
                            if (KeyboardPatch.this.mBarParams.isSupportActionBar) {
                                KeyboardPatch.this.mContentView.setPadding(0, KeyboardPatch.this.statusBarHeight + KeyboardPatch.this.actionBarHeight, 0, diff2);
                            } else if (KeyboardPatch.this.mBarParams.fits) {
                                KeyboardPatch.this.mContentView.setPadding(0, KeyboardPatch.this.statusBarHeight, 0, diff2);
                            } else {
                                KeyboardPatch.this.mContentView.setPadding(0, 0, 0, diff2);
                            }
                            int unused2 = KeyboardPatch.this.keyboardHeightPrevious = keyboardHeight;
                            if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                                if (keyboardHeight > KeyboardPatch.this.navigationBarHeight) {
                                    isPopup = true;
                                }
                                KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight);
                            }
                        }
                    }
                }
            }
        };
        this.mActivity = activity;
        Window window = dialog != null ? dialog.getWindow() : activity.getWindow();
        this.mWindow = window;
        this.mDecorView = window.getDecorView();
        if (contentView != null) {
            view = contentView;
        } else {
            view = this.mWindow.getDecorView().findViewById(16908290);
        }
        this.mContentView = view;
        if (dialog != null) {
            barParams = ImmersionBar.with(activity, dialog, tag).getBarParams();
        } else {
            barParams = ImmersionBar.with(activity).getBarParams();
        }
        this.mBarParams = barParams;
        if (barParams == null) {
            throw new IllegalArgumentException("先使用ImmersionBar初始化");
        }
    }

    private KeyboardPatch(Activity activity, Window window) {
        this.onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int keyboardHeight;
                int diff;
                int keyboardHeight2;
                if (KeyboardPatch.this.navigationAtBottom) {
                    Rect r = new Rect();
                    KeyboardPatch.this.mDecorView.getWindowVisibleDisplayFrame(r);
                    boolean isPopup = false;
                    if (KeyboardPatch.this.mBarParams.systemWindows) {
                        int keyboardHeight3 = (KeyboardPatch.this.mContentView.getHeight() - r.bottom) - KeyboardPatch.this.navigationBarHeight;
                        if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                            if (keyboardHeight3 > KeyboardPatch.this.navigationBarHeight) {
                                isPopup = true;
                            }
                            KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight3);
                        }
                    } else if (KeyboardPatch.this.mChildView != null) {
                        if (KeyboardPatch.this.mBarParams.isSupportActionBar) {
                            diff = ((KeyboardPatch.this.mContentView.getHeight() + KeyboardPatch.this.statusBarHeight) + KeyboardPatch.this.actionBarHeight) - r.bottom;
                        } else if (KeyboardPatch.this.mBarParams.fits) {
                            diff = (KeyboardPatch.this.mContentView.getHeight() + KeyboardPatch.this.statusBarHeight) - r.bottom;
                        } else {
                            diff = KeyboardPatch.this.mContentView.getHeight() - r.bottom;
                        }
                        if (KeyboardPatch.this.mBarParams.fullScreen) {
                            keyboardHeight2 = diff - KeyboardPatch.this.navigationBarHeight;
                        } else {
                            keyboardHeight2 = diff;
                        }
                        if (KeyboardPatch.this.mBarParams.fullScreen && diff == KeyboardPatch.this.navigationBarHeight) {
                            diff -= KeyboardPatch.this.navigationBarHeight;
                        }
                        if (keyboardHeight2 != KeyboardPatch.this.keyboardHeightPrevious) {
                            KeyboardPatch.this.mContentView.setPadding(KeyboardPatch.this.paddingLeft, KeyboardPatch.this.paddingTop, KeyboardPatch.this.paddingRight, KeyboardPatch.this.paddingBottom + diff);
                            int unused = KeyboardPatch.this.keyboardHeightPrevious = keyboardHeight2;
                            if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                                if (keyboardHeight2 > KeyboardPatch.this.navigationBarHeight) {
                                    isPopup = true;
                                }
                                KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight2);
                            }
                        }
                    } else {
                        int diff2 = KeyboardPatch.this.mContentView.getHeight() - r.bottom;
                        if (!KeyboardPatch.this.mBarParams.navigationBarEnable || !KeyboardPatch.this.mBarParams.navigationBarWithKitkatEnable) {
                            keyboardHeight = diff2;
                        } else {
                            if (Build.VERSION.SDK_INT == 19 || OSUtils.isEMUI3_1()) {
                                keyboardHeight = diff2 - KeyboardPatch.this.navigationBarHeight;
                            } else if (!KeyboardPatch.this.mBarParams.fullScreen) {
                                keyboardHeight = diff2;
                            } else {
                                keyboardHeight = diff2 - KeyboardPatch.this.navigationBarHeight;
                            }
                            if (KeyboardPatch.this.mBarParams.fullScreen && diff2 == KeyboardPatch.this.navigationBarHeight) {
                                diff2 -= KeyboardPatch.this.navigationBarHeight;
                            }
                        }
                        if (keyboardHeight != KeyboardPatch.this.keyboardHeightPrevious) {
                            if (KeyboardPatch.this.mBarParams.isSupportActionBar) {
                                KeyboardPatch.this.mContentView.setPadding(0, KeyboardPatch.this.statusBarHeight + KeyboardPatch.this.actionBarHeight, 0, diff2);
                            } else if (KeyboardPatch.this.mBarParams.fits) {
                                KeyboardPatch.this.mContentView.setPadding(0, KeyboardPatch.this.statusBarHeight, 0, diff2);
                            } else {
                                KeyboardPatch.this.mContentView.setPadding(0, 0, 0, diff2);
                            }
                            int unused2 = KeyboardPatch.this.keyboardHeightPrevious = keyboardHeight;
                            if (KeyboardPatch.this.mBarParams.onKeyboardListener != null) {
                                if (keyboardHeight > KeyboardPatch.this.navigationBarHeight) {
                                    isPopup = true;
                                }
                                KeyboardPatch.this.mBarParams.onKeyboardListener.onKeyboardChange(isPopup, keyboardHeight);
                            }
                        }
                    }
                }
            }
        };
        this.mActivity = activity;
        this.mWindow = window;
        View decorView = window.getDecorView();
        this.mDecorView = decorView;
        FrameLayout frameLayout = (FrameLayout) decorView.findViewById(16908290);
        View childAt = frameLayout.getChildAt(0);
        this.mChildView = childAt;
        childAt = childAt == null ? frameLayout : childAt;
        this.mContentView = childAt;
        this.paddingLeft = childAt.getPaddingLeft();
        this.paddingTop = this.mContentView.getPaddingTop();
        this.paddingRight = this.mContentView.getPaddingRight();
        this.paddingBottom = this.mContentView.getPaddingBottom();
        BarConfig barConfig = new BarConfig(this.mActivity);
        this.statusBarHeight = barConfig.getStatusBarHeight();
        this.navigationBarHeight = barConfig.getNavigationBarHeight();
        this.actionBarHeight = barConfig.getActionBarHeight();
        this.navigationAtBottom = barConfig.isNavigationAtBottom();
    }

    public static KeyboardPatch patch(Activity activity) {
        return new KeyboardPatch(activity);
    }

    public static KeyboardPatch patch(Activity activity, View contentView) {
        return new KeyboardPatch(activity, contentView);
    }

    public static KeyboardPatch patch(Activity activity, Dialog dialog, String tag) {
        return new KeyboardPatch(activity, dialog, tag);
    }

    public static KeyboardPatch patch(Activity activity, Dialog dialog, String tag, View contentView) {
        return new KeyboardPatch(activity, dialog, tag, contentView);
    }

    protected static KeyboardPatch patch(Activity activity, Window window) {
        return new KeyboardPatch(activity, window);
    }

    /* access modifiers changed from: protected */
    public void setBarParams(BarParams barParams) {
        this.mBarParams = barParams;
    }

    public void enable() {
        enable(18);
    }

    public void enable(int mode) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.mWindow.setSoftInputMode(mode);
            this.mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
    }

    public void disable() {
        disable(18);
    }

    public void disable(int mode) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.mWindow.setSoftInputMode(mode);
            this.mDecorView.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
    }
}
