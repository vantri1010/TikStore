package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$w3J7kia4N8bi-0X_hv55hbxCevA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$w3J7kia4N8bi0X_hv55hbxCevA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$w3J7kia4N8bi0X_hv55hbxCevA INSTANCE = new $$Lambda$MessagesController$w3J7kia4N8bi0X_hv55hbxCevA();

    private /* synthetic */ $$Lambda$MessagesController$w3J7kia4N8bi0X_hv55hbxCevA() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$processUpdates$263(tLObject, tL_error);
    }
}
