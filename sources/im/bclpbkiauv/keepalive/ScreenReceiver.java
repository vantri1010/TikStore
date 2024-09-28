package im.bclpbkiauv.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;

public class ScreenReceiver extends BroadcastReceiver {
    private CheckTopTask mCheckTopTask = new CheckTopTask(ApplicationLoader.applicationContext);
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                CheckTopTask.startForeground(context);
                if (this.mHandler != null) {
                    this.mHandler.postDelayed(this.mCheckTopTask, 3000);
                }
            } else if ("android.intent.action.USER_PRESENT".equals(action) || "android.intent.action.SCREEN_ON".equals(action)) {
                OnePxActivity onePxActivity = OnePxActivity.instance != null ? (OnePxActivity) OnePxActivity.instance.get() : null;
                if (onePxActivity != null) {
                    onePxActivity.finishSelf();
                }
                if (this.mHandler != null) {
                    this.mHandler.removeCallbacks(this.mCheckTopTask);
                }
            }
        } catch (Throwable e) {
            FileLog.e("ScreenReceiver onReceive error:" + e.toString());
        }
    }
}
