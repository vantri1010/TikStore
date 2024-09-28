package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$ContactsController$kvAfqPIMyIv-BnU9JylebvT-Iy8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContactsController$kvAfqPIMyIvBnU9JylebvTIy8 implements Comparator {
    public static final /* synthetic */ $$Lambda$ContactsController$kvAfqPIMyIvBnU9JylebvTIy8 INSTANCE = new $$Lambda$ContactsController$kvAfqPIMyIvBnU9JylebvTIy8();

    private /* synthetic */ $$Lambda$ContactsController$kvAfqPIMyIvBnU9JylebvTIy8() {
    }

    public final int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$28((TLRPC.Contact) obj, (TLRPC.Contact) obj2);
    }
}
