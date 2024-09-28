package im.bclpbkiauv.ui.components.toast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;

public final class SupportToast extends BaseToast {
    private final ToastHelper mToastHelper;

    public SupportToast(Context context) {
        super(context);
        this.mToastHelper = new ToastHelper(this, context);
    }

    public void show() {
        this.mToastHelper.show();
    }

    public void cancel() {
        this.mToastHelper.cancel();
    }

    private static final class ToastHelper extends Handler {
        private boolean isShow;
        private final String mPackageName;
        private final Toast mToast;
        private final WindowHelper mWindowHelper;

        ToastHelper(Toast toast, Context context) {
            super(Looper.getMainLooper());
            this.mToast = toast;
            this.mPackageName = context.getPackageName();
            this.mWindowHelper = WindowHelper.register(this, context);
        }

        public void handleMessage(Message msg) {
            cancel();
        }

        /* access modifiers changed from: package-private */
        public void show() {
            if (!this.isShow) {
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.height = -2;
                params.width = -2;
                params.format = -3;
                params.windowAnimations = 16973828;
                params.flags = 152;
                params.packageName = this.mPackageName;
                params.gravity = this.mToast.getGravity();
                params.x = this.mToast.getXOffset();
                params.y = this.mToast.getYOffset();
                try {
                    this.mWindowHelper.getWindowManager().addView(this.mToast.getView(), params);
                    this.isShow = true;
                    sendEmptyMessageDelayed(0, this.mToast.getDuration() == 1 ? 3500 : AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                } catch (WindowManager.BadTokenException | IllegalStateException | NullPointerException e) {
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void cancel() {
            removeMessages(0);
            if (this.isShow) {
                try {
                    this.mWindowHelper.getWindowManager().removeViewImmediate(this.mToast.getView());
                } catch (IllegalArgumentException | NullPointerException e) {
                }
                this.isShow = false;
            }
        }
    }

    private static final class WindowHelper implements Application.ActivityLifecycleCallbacks {
        private final ArrayMap<String, Activity> mActivitySet = new ArrayMap<>();
        private String mCurrentTag;
        private final ToastHelper mToastHelper;

        private WindowHelper(ToastHelper toast) {
            this.mToastHelper = toast;
        }

        static WindowHelper register(ToastHelper toast, Context context) {
            WindowHelper window = new WindowHelper(toast);
            if (context instanceof Application) {
                ((Application) context).registerActivityLifecycleCallbacks(window);
            }
            return window;
        }

        /* access modifiers changed from: package-private */
        public WindowManager getWindowManager() throws NullPointerException {
            Activity activity;
            String str = this.mCurrentTag;
            if (str != null && (activity = this.mActivitySet.get(str)) != null) {
                return getWindowManagerObject(activity);
            }
            throw null;
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            String objectTag = getObjectTag(activity);
            this.mCurrentTag = objectTag;
            this.mActivitySet.put(objectTag, activity);
        }

        public void onActivityStarted(Activity activity) {
            this.mCurrentTag = getObjectTag(activity);
        }

        public void onActivityResumed(Activity activity) {
            this.mCurrentTag = getObjectTag(activity);
        }

        public void onActivityPaused(Activity activity) {
            this.mToastHelper.cancel();
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
            this.mActivitySet.remove(getObjectTag(activity));
            if (getObjectTag(activity).equals(this.mCurrentTag)) {
                this.mCurrentTag = null;
            }
        }

        private static String getObjectTag(Object object) {
            return object.getClass().getName() + Integer.toHexString(object.hashCode());
        }

        private static WindowManager getWindowManagerObject(Activity activity) {
            return (WindowManager) activity.getSystemService("window");
        }
    }
}
