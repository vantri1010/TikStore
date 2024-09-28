package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$hnzEpQBiUiBgFIF5QmqAetH9_Ko  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$hnzEpQBiUiBgFIF5QmqAetH9_Ko implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$hnzEpQBiUiBgFIF5QmqAetH9_Ko INSTANCE = new $$Lambda$MessagesController$hnzEpQBiUiBgFIF5QmqAetH9_Ko();

    private /* synthetic */ $$Lambda$MessagesController$hnzEpQBiUiBgFIF5QmqAetH9_Ko() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$159(tLObject, tL_error);
    }
}
