package im.bclpbkiauv.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import im.bclpbkiauv.messenger.FileLog;

public class MonitorReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        FileLog.d("MonitorReceiver onReceive(): intent: " + intent.toUri(0));
        try {
            context.startService(new Intent(context, DaemonService.class));
        } catch (Throwable e) {
            FileLog.e("MonitorReceiver onReceive error:" + e.toString());
        }
    }
}
