package im.bclpbkiauv.ui;

import im.bclpbkiauv.ui.components.toast.ToastUtils;
import io.reactivex.functions.Consumer;

/* renamed from: im.bclpbkiauv.ui.-$$Lambda$ChatActivity$uEw-GMdyqmvmBM0E4BGnLh-Vwa0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivity$uEwGMdyqmvmBM0E4BGnLhVwa0 implements Consumer {
    public static final /* synthetic */ $$Lambda$ChatActivity$uEwGMdyqmvmBM0E4BGnLhVwa0 INSTANCE = new $$Lambda$ChatActivity$uEwGMdyqmvmBM0E4BGnLhVwa0();

    private /* synthetic */ $$Lambda$ChatActivity$uEwGMdyqmvmBM0E4BGnLhVwa0() {
    }

    public final void accept(Object obj) {
        ToastUtils.show((CharSequence) ((Throwable) obj).getMessage());
    }
}
