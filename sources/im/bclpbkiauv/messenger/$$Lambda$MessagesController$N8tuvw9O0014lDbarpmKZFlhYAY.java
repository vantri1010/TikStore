package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$N8tuvw9O0014lDbarpmKZFlhYAY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$N8tuvw9O0014lDbarpmKZFlhYAY implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$N8tuvw9O0014lDbarpmKZFlhYAY INSTANCE = new $$Lambda$MessagesController$N8tuvw9O0014lDbarpmKZFlhYAY();

    private /* synthetic */ $$Lambda$MessagesController$N8tuvw9O0014lDbarpmKZFlhYAY() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
