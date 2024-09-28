package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.DialogInterface;
import com.bjz.comm.net.utils.RxHelper;

/* renamed from: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA implements DialogInterface.OnDismissListener {
    public static final /* synthetic */ $$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA INSTANCE = new $$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA();

    private /* synthetic */ $$Lambda$FcPublishActivity$4$94hU7wFRxmxLXDRyvYclLOi_ukA() {
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        RxHelper.getInstance().lambda$sendSimpleRequest$0$RxHelper(FcPublishActivity.TAG);
    }
}
