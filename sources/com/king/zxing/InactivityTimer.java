package com.king.zxing;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import com.king.zxing.util.LogUtils;
import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;

final class InactivityTimer {
    private static final long INACTIVITY_DELAY_MS = 300000;
    private static final String TAG = InactivityTimer.class.getSimpleName();
    private final Activity activity;
    private AsyncTask<Object, Object, Object> inactivityTask;
    private final BroadcastReceiver powerStatusReceiver = new PowerStatusReceiver(this);
    private boolean registered = false;

    InactivityTimer(Activity activity2) {
        this.activity = activity2;
        onActivity();
    }

    /* access modifiers changed from: package-private */
    public void onActivity() {
        cancel();
        InactivityAsyncTask inactivityAsyncTask = new InactivityAsyncTask(this.activity);
        this.inactivityTask = inactivityAsyncTask;
        try {
            inactivityAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
        } catch (RejectedExecutionException e) {
            LogUtils.w("Couldn't schedule inactivity task; ignoring");
        }
    }

    /* access modifiers changed from: package-private */
    public void onPause() {
        cancel();
        if (this.registered) {
            this.activity.unregisterReceiver(this.powerStatusReceiver);
            this.registered = false;
            return;
        }
        LogUtils.w("PowerStatusReceiver was never registered?");
    }

    /* access modifiers changed from: package-private */
    public void onResume() {
        if (this.registered) {
            LogUtils.w("PowerStatusReceiver was already registered?");
        } else {
            this.activity.registerReceiver(this.powerStatusReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            this.registered = true;
        }
        onActivity();
    }

    /* access modifiers changed from: private */
    public void cancel() {
        AsyncTask<Object, Object, Object> asyncTask = this.inactivityTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.inactivityTask = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void shutdown() {
        cancel();
    }

    private static class PowerStatusReceiver extends BroadcastReceiver {
        private WeakReference<InactivityTimer> weakReference;

        public PowerStatusReceiver(InactivityTimer inactivityTimer) {
            this.weakReference = new WeakReference<>(inactivityTimer);
        }

        public void onReceive(Context context, Intent intent) {
            InactivityTimer inactivityTimer;
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) && (inactivityTimer = (InactivityTimer) this.weakReference.get()) != null) {
                if (intent.getIntExtra("plugged", -1) <= 0) {
                    inactivityTimer.onActivity();
                } else {
                    inactivityTimer.cancel();
                }
            }
        }
    }

    private static class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
        private WeakReference<Activity> weakReference;

        public InactivityAsyncTask(Activity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        /* access modifiers changed from: protected */
        public Object doInBackground(Object... objects) {
            try {
                Thread.sleep(InactivityTimer.INACTIVITY_DELAY_MS);
                LogUtils.i("Finishing activity due to inactivity");
                Activity activity = (Activity) this.weakReference.get();
                if (activity == null) {
                    return null;
                }
                activity.finish();
                return null;
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
