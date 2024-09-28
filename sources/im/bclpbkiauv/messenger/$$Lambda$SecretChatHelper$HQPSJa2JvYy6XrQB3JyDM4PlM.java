package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$SecretChatHelper$HQPSJ-a2JvYy6XrQB3-JyDM4PlM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$SecretChatHelper$HQPSJa2JvYy6XrQB3JyDM4PlM implements Comparator {
    public static final /* synthetic */ $$Lambda$SecretChatHelper$HQPSJa2JvYy6XrQB3JyDM4PlM INSTANCE = new $$Lambda$SecretChatHelper$HQPSJa2JvYy6XrQB3JyDM4PlM();

    private /* synthetic */ $$Lambda$SecretChatHelper$HQPSJa2JvYy6XrQB3JyDM4PlM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Message) obj).seq_out, ((TLRPC.Message) obj2).seq_out);
    }
}
