package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.content.DialogInterface;
import com.zhy.http.okhttp.OkHttpUtils;

/* renamed from: im.bclpbkiauv.ui.hui.friendscircle_v1.ui.-$$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA implements DialogInterface.OnDismissListener {
    public static final /* synthetic */ $$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA INSTANCE = new $$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA();

    private /* synthetic */ $$Lambda$FcPublishActivity$4$TcBFwxHHznY06darEiGvYbFptiA() {
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();
    }
}
