package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$b6OErMBdudTJ4g8xNHgHjN1Hr6E  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$b6OErMBdudTJ4g8xNHgHjN1Hr6E implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$b6OErMBdudTJ4g8xNHgHjN1Hr6E INSTANCE = new $$Lambda$MessagesController$b6OErMBdudTJ4g8xNHgHjN1Hr6E();

    private /* synthetic */ $$Lambda$MessagesController$b6OErMBdudTJ4g8xNHgHjN1Hr6E() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$164(tLObject, tL_error);
    }
}
