package im.bclpbkiauv.ui.utils;

import android.content.Context;
import android.content.Intent;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;

public class OpenInstallUitl {
    private static volatile OpenInstallUitl Instance = null;
    public static final String TAG = "OpenInstallUitl";
    private AppInstallAdapter installCallback;
    private AppWakeUpAdapter wakeUpcallback;

    public static OpenInstallUitl getInstance() {
        if (Instance == null) {
            synchronized (OpenInstallUitl.class) {
                if (Instance == null) {
                    Instance = new OpenInstallUitl();
                }
            }
        }
        return Instance;
    }

    public static void init(Context context) {
    }

    public static void reportRegister() {
    }

    public static void reportEffectPoint(String pointId, long pointValue) {
    }

    private OpenInstallUitl() {
    }

    public void getInstallOrWakeUp(Intent intent) {
    }

    public void onDestroy() {
        this.wakeUpcallback = null;
        this.installCallback = null;
    }
}
