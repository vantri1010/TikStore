package im.bclpbkiauv.tel;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0016¨\u0006\u0006"}, d2 = {"im/bclpbkiauv/tel/CallInterceptorKt$deleteIncomingCallLog$1", "Landroid/database/ContentObserver;", "onChange", "", "selfChange", "", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: CallInterceptor.kt */
public final class CallInterceptorKt$deleteIncomingCallLog$1 extends ContentObserver {
    final /* synthetic */ Context $context;
    final /* synthetic */ String $incomingNumber;
    final /* synthetic */ Uri $uri;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    CallInterceptorKt$deleteIncomingCallLog$1(Context $captured_local_variable$0, Uri $captured_local_variable$1, String $captured_local_variable$2, Handler $super_call_param$3) {
        super($super_call_param$3);
        this.$context = $captured_local_variable$0;
        this.$uri = $captured_local_variable$1;
        this.$incomingNumber = $captured_local_variable$2;
    }

    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        boolean z = true;
        if (this.$context.getContentResolver().delete(this.$uri, "number=?", new String[]{this.$incomingNumber}) <= 0) {
            z = false;
        }
        boolean z2 = z;
        this.$context.getContentResolver().unregisterContentObserver(this);
    }
}
