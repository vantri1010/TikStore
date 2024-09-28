package im.bclpbkiauv.ui;

import android.content.DialogInterface;
import im.bclpbkiauv.messenger.MessagesController;

/* renamed from: im.bclpbkiauv.ui.-$$Lambda$DialogsActivity$1-sAm09X9J2p_78gnDPzFYeLFck  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$1sAm09X9J2p_78gnDPzFYeLFck implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$1sAm09X9J2p_78gnDPzFYeLFck INSTANCE = new $$Lambda$DialogsActivity$1sAm09X9J2p_78gnDPzFYeLFck();

    private /* synthetic */ $$Lambda$DialogsActivity$1sAm09X9J2p_78gnDPzFYeLFck() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
