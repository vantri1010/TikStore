package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$UbT8lAEOJxs-JcZ9dUXdZe1lBFs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$UbT8lAEOJxsJcZ9dUXdZe1lBFs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$UbT8lAEOJxsJcZ9dUXdZe1lBFs INSTANCE = new $$Lambda$MessagesController$UbT8lAEOJxsJcZ9dUXdZe1lBFs();

    private /* synthetic */ $$Lambda$MessagesController$UbT8lAEOJxsJcZ9dUXdZe1lBFs() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$157(tLObject, tL_error);
    }
}
