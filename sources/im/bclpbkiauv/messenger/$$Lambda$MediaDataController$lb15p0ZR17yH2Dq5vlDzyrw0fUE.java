package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE INSTANCE = new $$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE();

    private /* synthetic */ $$Lambda$MediaDataController$lb15p0ZR17yH2Dq5vlDzyrw0fUE() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$29(tLObject, tL_error);
    }
}
