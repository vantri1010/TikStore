package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import im.bclpbkiauv.phoneformat.PhoneFormat;

public class CallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getStringExtra(RemoteConfigConstants.ResponseFieldKey.STATE))) {
                String phoneNumber = intent.getStringExtra("incoming_number");
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveCall, PhoneFormat.stripExceptNumbers(phoneNumber));
            }
        }
    }
}
