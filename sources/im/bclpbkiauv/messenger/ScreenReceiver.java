package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import im.bclpbkiauv.tgnet.ConnectionsManager;

public class ScreenReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("screen off");
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(true, true);
            ApplicationLoader.isScreenOn = false;
            AndroidUtilities.runOnUIThread($$Lambda$ScreenReceiver$L2vQUpYP81XfxCj_V22gKXOLXM.INSTANCE);
        } else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("screen on");
            }
            ConnectionsManager.getInstance(UserConfig.selectedAccount).setAppPaused(false, true);
            ApplicationLoader.isScreenOn = true;
            AndroidUtilities.runOnUIThread($$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU.INSTANCE);
        }
    }
}
