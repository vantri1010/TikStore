package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$Un5HQYcNJjKKbosJCSBzE_C41hk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Un5HQYcNJjKKbosJCSBzE_C41hk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Un5HQYcNJjKKbosJCSBzE_C41hk INSTANCE = new $$Lambda$MessagesController$Un5HQYcNJjKKbosJCSBzE_C41hk();

    private /* synthetic */ $$Lambda$MessagesController$Un5HQYcNJjKKbosJCSBzE_C41hk() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$unblockUser$65(tLObject, tL_error);
    }
}
