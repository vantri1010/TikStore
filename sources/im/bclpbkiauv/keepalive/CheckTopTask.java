package im.bclpbkiauv.keepalive;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import im.bclpbkiauv.messenger.FileLog;
import java.util.List;

class CheckTopTask implements Runnable {
    private Context context;

    CheckTopTask(Context context2) {
        this.context = context2;
    }

    static void startForeground(Context context2) {
        try {
            Intent intent = new Intent(context2, OnePxActivity.class);
            intent.addFlags(805306368);
            context2.startActivity(intent);
        } catch (Exception e) {
            FileLog.e("CheckTopTask startForeground error:" + e.toString());
        }
    }

    public void run() {
        if (!isForeground(this.context)) {
            startForeground(this.context);
        }
    }

    private boolean isForeground(Context context2) {
        try {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context2.getSystemService("activity")).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                int myPid = Process.myPid();
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (runningAppProcessInfo.pid == myPid) {
                        if (runningAppProcessInfo.importance <= 100) {
                            return true;
                        }
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e("CheckTopTask isForeground error:" + e.toString());
        }
        return false;
    }
}
