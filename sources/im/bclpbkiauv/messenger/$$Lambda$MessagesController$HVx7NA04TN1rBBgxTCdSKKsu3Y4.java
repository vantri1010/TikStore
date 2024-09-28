package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$HVx7NA04TN1rBBgxTCdSKKsu3Y4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$HVx7NA04TN1rBBgxTCdSKKsu3Y4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$HVx7NA04TN1rBBgxTCdSKKsu3Y4 INSTANCE = new $$Lambda$MessagesController$HVx7NA04TN1rBBgxTCdSKKsu3Y4();

    private /* synthetic */ $$Lambda$MessagesController$HVx7NA04TN1rBBgxTCdSKKsu3Y4() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$166(tLObject, tL_error);
    }
}
