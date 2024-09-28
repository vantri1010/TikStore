package im.bclpbkiauv.ui.hui.contacts;

import im.bclpbkiauv.tgnet.TLRPCContacts;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.ui.hui.contacts.-$$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgD-pQE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgDpQE implements Comparator {
    public static final /* synthetic */ $$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgDpQE INSTANCE = new $$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgDpQE();

    private /* synthetic */ $$Lambda$NewFriendsActivity$kCZf66diXSxnY8K550uSCgDpQE() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((TLRPCContacts.ContactApplyInfo) obj2).date, ((TLRPCContacts.ContactApplyInfo) obj).date);
    }
}
