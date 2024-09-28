package im.bclpbkiauv.tel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0017¨\u0006\t"}, d2 = {"Lim/bclpbkiauv/tel/IncomingCallReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: CallInterceptor.kt */
public class IncomingCallReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Intent it = intent;
            if (context != null) {
                Context c = context;
                String state = it.getStringExtra(RemoteConfigConstants.ResponseFieldKey.STATE);
                String incomingNumber = null;
                if (Intrinsics.areEqual((Object) TelephonyManager.EXTRA_STATE_RINGING, (Object) state)) {
                    incomingNumber = intent.getStringExtra("incoming_number");
                }
                if ((Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT == 28) && incomingNumber != null) {
                    Intent service = new Intent(c, CallApiBelow26And28Service.class);
                    service.putExtra("callState", state);
                    service.putExtra("incomingNumber", incomingNumber);
                    c.startService(service);
                }
            }
        }
    }
}
