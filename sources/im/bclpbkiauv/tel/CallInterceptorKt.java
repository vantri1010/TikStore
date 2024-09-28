package im.bclpbkiauv.tel;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.telecom.Call;
import androidx.core.app.ActivityCompat;
import com.google.android.exoplayer2.util.MimeTypes;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0002\u001a\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001a\u0010\u0010\t\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003¨\u0006\n"}, d2 = {"deleteIncomingCallLog", "", "context", "Landroid/content/Context;", "incomingNumber", "", "getIncomingNumberByDetails", "details", "Landroid/telecom/Call$Details;", "turnSilent", "HMessagesPrj_prodRelease"}, k = 2, mv = {1, 1, 16})
/* compiled from: CallInterceptor.kt */
public final class CallInterceptorKt {
    /* access modifiers changed from: private */
    public static final void deleteIncomingCallLog(Context context, String incomingNumber) {
        if (ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_CALL_LOG") == 0) {
            Uri uri = Uri.parse("content://call_log/calls");
            context.getContentResolver().registerContentObserver(uri, true, new CallInterceptorKt$deleteIncomingCallLog$1(context, uri, incomingNumber, new Handler()));
        }
    }

    /* access modifiers changed from: private */
    public static final void turnSilent(Context context) {
        try {
            Object systemService = context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (systemService != null) {
                AudioManager audioManager = (AudioManager) systemService;
                int streamVolume = audioManager.getStreamVolume(2);
                audioManager.setStreamVolume(2, 0, 8);
                Object systemService2 = context.getSystemService("notification");
                if (systemService2 != null) {
                    NotificationManager notificationManager = (NotificationManager) systemService2;
                    return;
                }
                throw new TypeCastException("null cannot be cast to non-null type android.app.NotificationManager");
            }
            throw new TypeCastException("null cannot be cast to non-null type android.media.AudioManager");
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public static final String getIncomingNumberByDetails(Call.Details details) {
        Uri handle = details.getHandle();
        Intrinsics.checkExpressionValueIsNotNull(handle, "details.handle");
        String schemeSpecificPart = handle.getSchemeSpecificPart();
        if (schemeSpecificPart != null) {
            return schemeSpecificPart;
        }
        try {
            Parcelable par = details.getIntentExtras().getParcelable("android.telecom.extra.INCOMING_CALL_ADDRESS");
            if (par == null) {
                return schemeSpecificPart;
            }
            Parcelable parcelable = par;
            return Uri.decode(((Uri) par).getSchemeSpecificPart());
        } catch (Exception e) {
            return schemeSpecificPart;
        }
    }
}
