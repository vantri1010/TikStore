package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import com.baidu.mapapi.UIMsg;
import com.blankj.utilcode.util.Utils;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.lang.reflect.Field;

public final class ToastUtils {
    private static final int COLOR_DEFAULT = -16777217;
    private static final String NULL = "null";
    /* access modifiers changed from: private */
    public static IToast iToast;
    private static int sBgColor = COLOR_DEFAULT;
    private static int sBgResource = -1;
    /* access modifiers changed from: private */
    public static int sGravity = -1;
    /* access modifiers changed from: private */
    public static int sMsgColor = COLOR_DEFAULT;
    /* access modifiers changed from: private */
    public static int sMsgTextSize = -1;
    /* access modifiers changed from: private */
    public static int sXOffset = -1;
    /* access modifiers changed from: private */
    public static int sYOffset = -1;

    interface IToast {
        void cancel();

        View getView();

        void setDuration(int i);

        void setGravity(int i, int i2, int i3);

        void setText(int i);

        void setText(CharSequence charSequence);

        void setView(View view);

        void show();
    }

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    public static void setBgColor(int backgroundColor) {
        sBgColor = backgroundColor;
    }

    public static void setBgResource(int bgResource) {
        sBgResource = bgResource;
    }

    public static void setMsgColor(int msgColor) {
        sMsgColor = msgColor;
    }

    public static void setMsgTextSize(int textSize) {
        sMsgTextSize = textSize;
    }

    public static void showShort(CharSequence text) {
        show(text == null ? NULL : text, 0);
    }

    public static void showShort(int resId) {
        show(resId, 0);
    }

    public static void showShort(int resId, Object... args) {
        show(resId, 0, args);
    }

    public static void showShort(String format, Object... args) {
        show(format, 0, args);
    }

    public static void showLong(CharSequence text) {
        show(text == null ? NULL : text, 1);
    }

    public static void showLong(int resId) {
        show(resId, 1);
    }

    public static void showLong(int resId, Object... args) {
        show(resId, 1, args);
    }

    public static void showLong(String format, Object... args) {
        show(format, 1, args);
    }

    public static View showCustomShort(int layoutId) {
        return showCustomShort(getView(layoutId));
    }

    public static View showCustomShort(View view) {
        show(view, 0);
        return view;
    }

    public static View showCustomLong(int layoutId) {
        return showCustomLong(getView(layoutId));
    }

    public static View showCustomLong(View view) {
        show(view, 1);
        return view;
    }

    public static void cancel() {
        IToast iToast2 = iToast;
        if (iToast2 != null) {
            iToast2.cancel();
        }
    }

    private static void show(int resId, int duration) {
        show(resId, duration, null);
    }

    private static void show(int resId, int duration, Object... args) {
        try {
            CharSequence text = Utils.getApp().getResources().getText(resId);
            if (args != null) {
                text = String.format(text.toString(), args);
            }
            show(text, duration);
        } catch (Exception e) {
            show((CharSequence) String.valueOf(resId), duration);
        }
    }

    private static void show(String format, int duration, Object... args) {
        String text = format;
        if (text == null) {
            text = NULL;
        } else if (args != null) {
            text = String.format(format, args);
        }
        show((CharSequence) text, duration);
    }

    private static void show(final CharSequence text, final int duration) {
        Utils.runOnUiThread(new Runnable() {
            public void run() {
                ToastUtils.cancel();
                IToast unused = ToastUtils.iToast = ToastFactory.makeToast(Utils.getApp(), text, duration);
                View toastView = ToastUtils.iToast.getView();
                if (toastView != null) {
                    TextView tvMessage = (TextView) toastView.findViewById(16908299);
                    if (ToastUtils.sMsgColor != ToastUtils.COLOR_DEFAULT) {
                        tvMessage.setTextColor(ToastUtils.sMsgColor);
                    }
                    if (ToastUtils.sMsgTextSize != -1) {
                        tvMessage.setTextSize((float) ToastUtils.sMsgTextSize);
                    }
                    if (!(ToastUtils.sGravity == -1 && ToastUtils.sXOffset == -1 && ToastUtils.sYOffset == -1)) {
                        ToastUtils.iToast.setGravity(ToastUtils.sGravity, ToastUtils.sXOffset, ToastUtils.sYOffset);
                    }
                    ToastUtils.setBg(tvMessage);
                    ToastUtils.iToast.show();
                }
            }
        });
    }

    private static void show(final View view, final int duration) {
        Utils.runOnUiThread(new Runnable() {
            public void run() {
                ToastUtils.cancel();
                IToast unused = ToastUtils.iToast = ToastFactory.newToast(Utils.getApp());
                ToastUtils.iToast.setView(view);
                ToastUtils.iToast.setDuration(duration);
                if (!(ToastUtils.sGravity == -1 && ToastUtils.sXOffset == -1 && ToastUtils.sYOffset == -1)) {
                    ToastUtils.iToast.setGravity(ToastUtils.sGravity, ToastUtils.sXOffset, ToastUtils.sYOffset);
                }
                ToastUtils.setBg();
                ToastUtils.iToast.show();
            }
        });
    }

    /* access modifiers changed from: private */
    public static void setBg() {
        if (sBgResource != -1) {
            iToast.getView().setBackgroundResource(sBgResource);
        } else if (sBgColor != COLOR_DEFAULT) {
            View toastView = iToast.getView();
            Drawable background = toastView.getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (Build.VERSION.SDK_INT >= 16) {
                toastView.setBackground(new ColorDrawable(sBgColor));
            } else {
                toastView.setBackgroundDrawable(new ColorDrawable(sBgColor));
            }
        }
    }

    /* access modifiers changed from: private */
    public static void setBg(TextView tvMsg) {
        if (sBgResource != -1) {
            iToast.getView().setBackgroundResource(sBgResource);
            tvMsg.setBackgroundColor(0);
        } else if (sBgColor != COLOR_DEFAULT) {
            View toastView = iToast.getView();
            Drawable tvBg = toastView.getBackground();
            Drawable msgBg = tvMsg.getBackground();
            if (tvBg != null && msgBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
                tvMsg.setBackgroundColor(0);
            } else if (tvBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (msgBg != null) {
                msgBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                toastView.setBackgroundColor(sBgColor);
            }
        }
    }

    private static View getView(int layoutId) {
        return ((LayoutInflater) Utils.getApp().getSystemService("layout_inflater")).inflate(layoutId, (ViewGroup) null);
    }

    static class ToastFactory {
        ToastFactory() {
        }

        static IToast makeToast(Context context, CharSequence text, int duration) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(makeNormalToast(context, text, duration));
            }
            return new ToastWithoutNotification(makeNormalToast(context, text, duration));
        }

        static IToast newToast(Context context) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                return new SystemToast(new Toast(context));
            }
            return new ToastWithoutNotification(new Toast(context));
        }

        private static Toast makeNormalToast(Context context, CharSequence text, int duration) {
            Toast toast = Toast.makeText(context, "", duration);
            toast.setText(text);
            return toast;
        }
    }

    static class SystemToast extends AbsToast {
        SystemToast(Toast toast) {
            super(toast);
            if (Build.VERSION.SDK_INT == 25) {
                try {
                    Field mTNField = Toast.class.getDeclaredField("mTN");
                    mTNField.setAccessible(true);
                    Object mTN = mTNField.get(toast);
                    Field mTNmHandlerField = mTNField.getType().getDeclaredField("mHandler");
                    mTNmHandlerField.setAccessible(true);
                    mTNmHandlerField.set(mTN, new SafeHandler((Handler) mTNmHandlerField.get(mTN)));
                } catch (Exception e) {
                }
            }
        }

        public void show() {
            this.mToast.show();
        }

        public void cancel() {
            this.mToast.cancel();
        }

        static class SafeHandler extends Handler {
            private Handler impl;

            SafeHandler(Handler impl2) {
                this.impl = impl2;
            }

            public void handleMessage(Message msg) {
                this.impl.handleMessage(msg);
            }

            public void dispatchMessage(Message msg) {
                try {
                    this.impl.dispatchMessage(msg);
                } catch (Exception e) {
                    Log.e("ToastUtils", e.toString());
                }
            }
        }
    }

    static class ToastWithoutNotification extends AbsToast {
        private static final Utils.OnActivityDestroyedListener LISTENER = new Utils.OnActivityDestroyedListener() {
            public void onActivityDestroyed(Activity activity) {
                if (ToastUtils.iToast != null) {
                    activity.getWindow().getDecorView().setVisibility(8);
                    ToastUtils.iToast.cancel();
                }
            }
        };
        private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        private View mView;
        private WindowManager mWM;

        ToastWithoutNotification(Toast toast) {
            super(toast);
        }

        public void show() {
            Utils.runOnUiThreadDelayed(new Runnable() {
                public void run() {
                    ToastWithoutNotification.this.realShow();
                }
            }, 300);
        }

        /* access modifiers changed from: private */
        public void realShow() {
            if (this.mToast != null) {
                View view = this.mToast.getView();
                this.mView = view;
                if (view != null) {
                    Context context = this.mToast.getView().getContext();
                    if (Build.VERSION.SDK_INT < 25) {
                        this.mWM = (WindowManager) context.getSystemService("window");
                        this.mParams.type = UIMsg.m_AppUI.MSG_APP_VERSION_FORCE;
                    } else {
                        Context topActivityOrApp = Utils.getTopActivityOrApp();
                        if (!(topActivityOrApp instanceof Activity)) {
                            Log.e("ToastUtils", "Couldn't get top Activity.");
                            return;
                        }
                        Activity topActivity = (Activity) topActivityOrApp;
                        if (topActivity.isFinishing() || topActivity.isDestroyed()) {
                            Log.e("ToastUtils", topActivity + " is useless");
                            return;
                        }
                        this.mWM = topActivity.getWindowManager();
                        this.mParams.type = 99;
                        Utils.getActivityLifecycle().addOnActivityDestroyedListener(topActivity, LISTENER);
                    }
                    this.mParams.height = -2;
                    this.mParams.width = -2;
                    this.mParams.format = -3;
                    this.mParams.windowAnimations = 16973828;
                    this.mParams.setTitle("ToastWithoutNotification");
                    this.mParams.flags = 152;
                    this.mParams.packageName = Utils.getApp().getPackageName();
                    this.mParams.gravity = this.mToast.getGravity();
                    if ((this.mParams.gravity & 7) == 7) {
                        this.mParams.horizontalWeight = 1.0f;
                    }
                    if ((this.mParams.gravity & 112) == 112) {
                        this.mParams.verticalWeight = 1.0f;
                    }
                    this.mParams.x = this.mToast.getXOffset();
                    this.mParams.y = this.mToast.getYOffset();
                    this.mParams.horizontalMargin = this.mToast.getHorizontalMargin();
                    this.mParams.verticalMargin = this.mToast.getVerticalMargin();
                    try {
                        if (this.mWM != null) {
                            this.mWM.addView(this.mView, this.mParams);
                        }
                    } catch (Exception e) {
                    }
                    Utils.runOnUiThreadDelayed(new Runnable() {
                        public void run() {
                            ToastWithoutNotification.this.cancel();
                        }
                    }, this.mToast.getDuration() == 0 ? AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS : 3500);
                }
            }
        }

        public void cancel() {
            try {
                if (this.mWM != null) {
                    this.mWM.removeViewImmediate(this.mView);
                }
            } catch (Exception e) {
            }
            this.mView = null;
            this.mWM = null;
            this.mToast = null;
        }
    }

    static abstract class AbsToast implements IToast {
        Toast mToast;

        AbsToast(Toast toast) {
            this.mToast = toast;
        }

        public void setView(View view) {
            this.mToast.setView(view);
        }

        public View getView() {
            return this.mToast.getView();
        }

        public void setDuration(int duration) {
            this.mToast.setDuration(duration);
        }

        public void setGravity(int gravity, int xOffset, int yOffset) {
            this.mToast.setGravity(gravity, xOffset, yOffset);
        }

        public void setText(int resId) {
            this.mToast.setText(resId);
        }

        public void setText(CharSequence s) {
            this.mToast.setText(s);
        }
    }
}
