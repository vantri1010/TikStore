package com.alivc.rtc;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

public class AppFrontBackHelper {
    private static final int MSG_SWITCH_BACK_OR_FRONT = 1;
    private Handler.Callback mCallback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what == 1 && (msg.obj instanceof Object[])) {
                Object[] objects = (Object[]) msg.obj;
                if ((objects[0] instanceof Activity) && (objects[1] instanceof Boolean)) {
                    AppFrontBackHelper.this.onFrontOrBack((Activity) objects[0], ((Boolean) objects[1]).booleanValue());
                }
            }
            return false;
        }
    };
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsBack;
    private Application.ActivityLifecycleCallbacks mLifecycleCallback = new Application.ActivityLifecycleCallbacks() {
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        public void onActivityStarted(Activity activity) {
            AppFrontBackHelper.this.sendMsgToHandler(activity, false);
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
            AppFrontBackHelper.this.sendMsgToHandler(activity, true);
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
        }
    };
    private OnAppStatusListener mListener;

    public interface OnAppStatusListener {
        void onBack();

        void onFront();
    }

    public void bindApplication(Application application, OnAppStatusListener listener) {
        if (application != null) {
            HandlerThread handlerThread = new HandlerThread("AppFrontBackHelper-HandlerThread");
            this.mHandlerThread = handlerThread;
            handlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), this.mCallback);
            this.mListener = listener;
            application.registerActivityLifecycleCallbacks(this.mLifecycleCallback);
        }
    }

    public void unBindApplication(Application application) {
        if (application != null) {
            application.unregisterActivityLifecycleCallbacks(this.mLifecycleCallback);
        }
        this.mListener = null;
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages((Object) null);
        }
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        this.mHandler = null;
        this.mHandlerThread = null;
    }

    /* access modifiers changed from: private */
    public void onFrontOrBack(Activity activity, boolean isBack) {
        OnAppStatusListener onAppStatusListener;
        boolean back = isBackground(activity);
        if (!(this.mIsBack == back || (onAppStatusListener = this.mListener) == null)) {
            if (isBack) {
                onAppStatusListener.onBack();
            } else {
                onAppStatusListener.onFront();
            }
        }
        this.mIsBack = back;
    }

    /* access modifiers changed from: private */
    public void sendMsgToHandler(Activity activity, boolean isBack) {
        Handler handler = this.mHandler;
        if (handler != null) {
            Message msg = Message.obtain(handler, 1);
            msg.obj = new Object[]{activity, Boolean.valueOf(isBack)};
            this.mHandler.sendMessage(msg);
        }
    }

    public static boolean isBackground(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        for (ActivityManager.RunningAppProcessInfo appProcess : ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses()) {
            if (TextUtils.equals(appProcess.processName, context.getPackageName())) {
                boolean isBackground = (appProcess.importance == 100 || appProcess.importance == 200) ? false : true;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
