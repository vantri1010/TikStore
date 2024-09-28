package com.blankj.utilcode.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import com.google.android.exoplayer2.C;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public final class Utils {
    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
    /* access modifiers changed from: private */
    public static final Handler UTIL_HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService UTIL_POOL = ThreadUtils.getCachedPool();
    private static Application sApplication;

    public interface Callback<T> {
        void onCall(T t);
    }

    public interface Func1<Ret, Par> {
        Ret call(Par par);
    }

    public interface OnActivityDestroyedListener {
        void onActivityDestroyed(Activity activity);
    }

    public interface OnAppStatusChangedListener {
        void onBackground(Activity activity);

        void onForeground(Activity activity);
    }

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(Context context) {
        if (context == null) {
            init(getApplicationByReflect());
        } else {
            init((Application) context.getApplicationContext());
        }
    }

    public static void init(Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
            sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            UTIL_POOL.execute(new Runnable() {
                public void run() {
                    AdaptScreenUtils.preLoad();
                }
            });
        } else if (app != null && app.getClass() != sApplication.getClass()) {
            sApplication.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            ACTIVITY_LIFECYCLE.mActivityList.clear();
            sApplication = app;
            app.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        }
    }

    public static Application getApp() {
        Application application = sApplication;
        if (application != null) {
            return application;
        }
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    static ActivityLifecycleImpl getActivityLifecycle() {
        return ACTIVITY_LIFECYCLE;
    }

    static LinkedList<Activity> getActivityList() {
        return ACTIVITY_LIFECYCLE.mActivityList;
    }

    static Context getTopActivityOrApp() {
        if (!isAppForeground()) {
            return getApp();
        }
        Activity topActivity = ACTIVITY_LIFECYCLE.getTopActivity();
        return topActivity == null ? getApp() : topActivity;
    }

    static boolean isAppForeground() {
        List<ActivityManager.RunningAppProcessInfo> info;
        ActivityManager am = (ActivityManager) getApp().getSystemService("activity");
        if (am == null || (info = am.getRunningAppProcesses()) == null || info.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == 100 && aInfo.processName.equals(getApp().getPackageName())) {
                return true;
            }
        }
        return false;
    }

    static <T> Task<T> doAsync(Task<T> task) {
        UTIL_POOL.execute(task);
        return task;
    }

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            UTIL_HANDLER.post(runnable);
        }
    }

    public static void runOnUiThreadDelayed(Runnable runnable, long delayMillis) {
        UTIL_HANDLER.postDelayed(runnable, delayMillis);
    }

    static String getCurrentProcessName() {
        String name = getCurrentProcessNameByFile();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        String name2 = getCurrentProcessNameByAms();
        if (!TextUtils.isEmpty(name2)) {
            return name2;
        }
        return getCurrentProcessNameByReflect();
    }

    static void fixSoftInputLeaks(Window window) {
        InputMethodManager imm = (InputMethodManager) getApp().getSystemService("input_method");
        if (imm != null) {
            for (String leakView : new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"}) {
                try {
                    Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                    if (!leakViewField.isAccessible()) {
                        leakViewField.setAccessible(true);
                    }
                    Object obj = leakViewField.get(imm);
                    if (obj instanceof View) {
                        if (((View) obj).getRootView() == window.getDecorView().getRootView()) {
                            leakViewField.set(imm, (Object) null);
                        }
                    }
                } catch (Throwable th) {
                }
            }
        }
    }

    static SPUtils getSpUtils4Utils() {
        return SPUtils.getInstance("Utils");
    }

    private static String getCurrentProcessNameByFile() {
        try {
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(new File("/proc/" + Process.myPid() + "/cmdline")));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String getCurrentProcessNameByAms() {
        List<ActivityManager.RunningAppProcessInfo> info;
        ActivityManager am = (ActivityManager) getApp().getSystemService("activity");
        if (am == null || (info = am.getRunningAppProcesses()) == null || info.size() == 0) {
            return "";
        }
        int pid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.pid == pid && aInfo.processName != null) {
                return aInfo.processName;
            }
        }
        return "";
    }

    private static String getCurrentProcessNameByReflect() {
        try {
            Application app = getApp();
            Field loadedApkField = app.getClass().getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(app);
            Field activityThreadField = loadedApk.getClass().getDeclaredField("mActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThread = activityThreadField.get(loadedApk);
            return (String) activityThread.getClass().getDeclaredMethod("getProcessName", new Class[0]).invoke(activityThread, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Application getApplicationByReflect() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object app = activityThread.getMethod("getApplication", new Class[0]).invoke(activityThread.getMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]), new Object[0]);
            if (app != null) {
                return (Application) app;
            }
            throw new NullPointerException("u should init first");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new NullPointerException("u should init first");
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            throw new NullPointerException("u should init first");
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            throw new NullPointerException("u should init first");
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            throw new NullPointerException("u should init first");
        }
    }

    /* access modifiers changed from: private */
    public static void setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT < 26 || !ValueAnimator.areAnimatorsEnabled()) {
            try {
                Field sDurationScaleField = ValueAnimator.class.getDeclaredField("sDurationScale");
                sDurationScaleField.setAccessible(true);
                if (((Float) sDurationScaleField.get((Object) null)).floatValue() == 0.0f) {
                    sDurationScaleField.set((Object) null, Float.valueOf(1.0f));
                    Log.i("Utils", "setAnimatorsEnabled: Animators are enabled now!");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static final class TransActivity extends FragmentActivity {
        private static final Map<TransActivity, TransActivityDelegate> CALLBACK_MAP = new HashMap();
        private static TransActivityDelegate sDelegate;

        public static void start(Func1<Void, Intent> consumer, TransActivityDelegate delegate) {
            if (delegate != null) {
                Intent starter = new Intent(Utils.getApp(), TransActivity.class);
                starter.addFlags(C.ENCODING_PCM_MU_LAW);
                if (consumer != null) {
                    consumer.call(starter);
                }
                Utils.getApp().startActivity(starter);
                sDelegate = delegate;
            }
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle savedInstanceState) {
            overridePendingTransition(0, 0);
            TransActivityDelegate transActivityDelegate = sDelegate;
            if (transActivityDelegate == null) {
                super.onCreate(savedInstanceState);
                finish();
                return;
            }
            CALLBACK_MAP.put(this, transActivityDelegate);
            sDelegate.onCreateBefore(this, savedInstanceState);
            super.onCreate(savedInstanceState);
            sDelegate.onCreated(this, savedInstanceState);
            sDelegate = null;
        }

        /* access modifiers changed from: protected */
        public void onStart() {
            super.onStart();
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onStarted(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onResume() {
            super.onResume();
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onResumed(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onPause() {
            overridePendingTransition(0, 0);
            super.onPause();
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onPaused(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onStopped(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onSaveInstanceState(this, outState);
            }
        }

        /* access modifiers changed from: protected */
        public void onDestroy() {
            super.onDestroy();
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onDestroy(this);
                CALLBACK_MAP.remove(this);
            }
        }

        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
            }
        }

        /* access modifiers changed from: protected */
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback != null) {
                callback.onActivityResult(this, requestCode, resultCode, data);
            }
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            TransActivityDelegate callback = CALLBACK_MAP.get(this);
            if (callback == null) {
                return super.dispatchTouchEvent(ev);
            }
            if (callback.dispatchTouchEvent(this, ev)) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        public static abstract class TransActivityDelegate {
            public void onCreateBefore(Activity activity, Bundle savedInstanceState) {
            }

            public void onCreated(Activity activity, Bundle savedInstanceState) {
            }

            public void onStarted(Activity activity) {
            }

            public void onDestroy(Activity activity) {
            }

            public void onResumed(Activity activity) {
            }

            public void onPaused(Activity activity) {
            }

            public void onStopped(Activity activity) {
            }

            public void onSaveInstanceState(Activity activity, Bundle outState) {
            }

            public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
            }

            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            }

            public boolean dispatchTouchEvent(Activity activity, MotionEvent ev) {
                return false;
            }
        }
    }

    static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {
        final LinkedList<Activity> mActivityList = new LinkedList<>();
        private int mConfigCount = 0;
        final Map<Activity, List<OnActivityDestroyedListener>> mDestroyedListenerMap = new HashMap();
        private int mForegroundCount = 0;
        private boolean mIsBackground = false;
        final List<OnAppStatusChangedListener> mStatusListeners = new ArrayList();

        ActivityLifecycleImpl() {
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LanguageUtils.applyLanguage(activity);
            Utils.setAnimatorsEnabled();
            setTopActivity(activity);
        }

        public void onActivityStarted(Activity activity) {
            if (!this.mIsBackground) {
                setTopActivity(activity);
            }
            int i = this.mConfigCount;
            if (i < 0) {
                this.mConfigCount = i + 1;
            } else {
                this.mForegroundCount++;
            }
        }

        public void onActivityResumed(Activity activity) {
            setTopActivity(activity);
            if (this.mIsBackground) {
                this.mIsBackground = false;
                postStatus(activity, true);
            }
            processHideSoftInputOnActivityDestroy(activity, false);
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                this.mConfigCount--;
            } else {
                int i = this.mForegroundCount - 1;
                this.mForegroundCount = i;
                if (i <= 0) {
                    this.mIsBackground = true;
                    postStatus(activity, false);
                }
            }
            processHideSoftInputOnActivityDestroy(activity, true);
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
            this.mActivityList.remove(activity);
            consumeOnActivityDestroyedListener(activity);
            Utils.fixSoftInputLeaks(activity.getWindow());
        }

        /* access modifiers changed from: package-private */
        public Activity getTopActivity() {
            if (!this.mActivityList.isEmpty()) {
                for (int i = this.mActivityList.size() - 1; i >= 0; i--) {
                    Activity activity = this.mActivityList.get(i);
                    if (activity != null && !activity.isFinishing() && (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed())) {
                        return activity;
                    }
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        /* access modifiers changed from: package-private */
        public void addOnAppStatusChangedListener(OnAppStatusChangedListener listener) {
            this.mStatusListeners.add(listener);
        }

        /* access modifiers changed from: package-private */
        public void removeOnAppStatusChangedListener(OnAppStatusChangedListener listener) {
            this.mStatusListeners.remove(listener);
        }

        /* access modifiers changed from: package-private */
        public void removeOnActivityDestroyedListener(Activity activity) {
            if (activity != null) {
                this.mDestroyedListenerMap.remove(activity);
            }
        }

        /* access modifiers changed from: package-private */
        public void addOnActivityDestroyedListener(Activity activity, OnActivityDestroyedListener listener) {
            if (activity != null && listener != null) {
                List<OnActivityDestroyedListener> listeners = this.mDestroyedListenerMap.get(activity);
                if (listeners == null) {
                    listeners = new CopyOnWriteArrayList<>();
                    this.mDestroyedListenerMap.put(activity, listeners);
                } else if (listeners.contains(listener)) {
                    return;
                }
                listeners.add(listener);
            }
        }

        private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
            if (isSave) {
                activity.getWindow().getDecorView().setTag(-123, Integer.valueOf(activity.getWindow().getAttributes().softInputMode));
                activity.getWindow().setSoftInputMode(3);
                return;
            }
            final Object tag = activity.getWindow().getDecorView().getTag(-123);
            if (tag instanceof Integer) {
                Utils.runOnUiThreadDelayed(new Runnable() {
                    public void run() {
                        Window window = activity.getWindow();
                        if (window != null) {
                            window.setSoftInputMode(((Integer) tag).intValue());
                        }
                    }
                }, 100);
            }
        }

        private void postStatus(Activity activity, boolean isForeground) {
            if (!this.mStatusListeners.isEmpty()) {
                for (OnAppStatusChangedListener statusListener : this.mStatusListeners) {
                    if (isForeground) {
                        statusListener.onForeground(activity);
                    } else {
                        statusListener.onBackground(activity);
                    }
                }
            }
        }

        private void setTopActivity(Activity activity) {
            if (!this.mActivityList.contains(activity)) {
                this.mActivityList.addLast(activity);
            } else if (!this.mActivityList.getLast().equals(activity)) {
                this.mActivityList.remove(activity);
                this.mActivityList.addLast(activity);
            }
        }

        private void consumeOnActivityDestroyedListener(Activity activity) {
            Iterator<Map.Entry<Activity, List<OnActivityDestroyedListener>>> iterator = this.mDestroyedListenerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Activity, List<OnActivityDestroyedListener>> entry = iterator.next();
                if (entry.getKey() == activity) {
                    for (OnActivityDestroyedListener listener : entry.getValue()) {
                        listener.onActivityDestroyed(activity);
                    }
                    iterator.remove();
                }
            }
        }

        private Activity getTopActivityByReflect() {
            try {
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]);
                Field mActivityListField = activityThreadClass.getDeclaredField("mActivityList");
                mActivityListField.setAccessible(true);
                Map activities = (Map) mActivityListField.get(currentActivityThreadMethod);
                if (activities == null) {
                    return null;
                }
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
                return null;
            } catch (Exception e) {
                Log.e("Utils", e.getMessage());
            }
        }
    }

    public static final class FileProvider4UtilCode extends FileProvider {
        public boolean onCreate() {
            Utils.init(getContext());
            try {
                Class.forName("com.blankj.utildebug.DebugUtils");
                return true;
            } catch (ClassNotFoundException e) {
                return true;
            }
        }
    }

    public static abstract class Task<Result> implements Runnable {
        private static final int CANCELLED = 2;
        private static final int COMPLETING = 1;
        private static final int EXCEPTIONAL = 3;
        private static final int NEW = 0;
        /* access modifiers changed from: private */
        public Callback<Result> mCallback;
        private volatile int state = 0;

        /* access modifiers changed from: package-private */
        public abstract Result doInBackground();

        public Task(Callback<Result> callback) {
            this.mCallback = callback;
        }

        public void run() {
            try {
                final Result t = doInBackground();
                if (this.state == 0) {
                    this.state = 1;
                    Utils.UTIL_HANDLER.post(new Runnable() {
                        public void run() {
                            Task.this.mCallback.onCall(t);
                        }
                    });
                }
            } catch (Throwable th) {
                if (this.state == 0) {
                    this.state = 3;
                }
            }
        }

        public void cancel() {
            this.state = 2;
        }

        public boolean isDone() {
            return this.state != 0;
        }

        public boolean isCanceled() {
            return this.state == 2;
        }
    }
}
