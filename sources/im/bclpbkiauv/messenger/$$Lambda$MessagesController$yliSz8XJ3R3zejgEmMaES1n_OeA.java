package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$yliSz8XJ3R3zejgEmMaES1n_OeA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$yliSz8XJ3R3zejgEmMaES1n_OeA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$yliSz8XJ3R3zejgEmMaES1n_OeA INSTANCE = new $$Lambda$MessagesController$yliSz8XJ3R3zejgEmMaES1n_OeA();

    private /* synthetic */ $$Lambda$MessagesController$yliSz8XJ3R3zejgEmMaES1n_OeA() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$37(tLObject, tL_error);
    }
}
