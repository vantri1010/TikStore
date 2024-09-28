package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$lKAbhJoiBr4UbdgDiNAK1QkOwAM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$lKAbhJoiBr4UbdgDiNAK1QkOwAM implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$lKAbhJoiBr4UbdgDiNAK1QkOwAM INSTANCE = new $$Lambda$MessagesController$lKAbhJoiBr4UbdgDiNAK1QkOwAM();

    private /* synthetic */ $$Lambda$MessagesController$lKAbhJoiBr4UbdgDiNAK1QkOwAM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
