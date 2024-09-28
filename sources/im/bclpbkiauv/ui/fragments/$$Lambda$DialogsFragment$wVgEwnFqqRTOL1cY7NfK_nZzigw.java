package im.bclpbkiauv.ui.fragments;

import android.content.DialogInterface;
import im.bclpbkiauv.messenger.MessagesController;

/* renamed from: im.bclpbkiauv.ui.fragments.-$$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw INSTANCE = new $$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw();

    private /* synthetic */ $$Lambda$DialogsFragment$wVgEwnFqqRTOL1cY7NfK_nZzigw() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
