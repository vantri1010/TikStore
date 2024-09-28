package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$MessagesController$HoAhZcnmry4ytudOGaageDjV5q4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$HoAhZcnmry4ytudOGaageDjV5q4 implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$HoAhZcnmry4ytudOGaageDjV5q4 INSTANCE = new $$Lambda$MessagesController$HoAhZcnmry4ytudOGaageDjV5q4();

    private /* synthetic */ $$Lambda$MessagesController$HoAhZcnmry4ytudOGaageDjV5q4() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
    }
}
