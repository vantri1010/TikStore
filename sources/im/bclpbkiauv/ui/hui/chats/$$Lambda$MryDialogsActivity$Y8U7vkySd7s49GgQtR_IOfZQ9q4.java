package im.bclpbkiauv.ui.hui.chats;

import android.content.DialogInterface;
import im.bclpbkiauv.messenger.MessagesController;

/* renamed from: im.bclpbkiauv.ui.hui.chats.-$$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4 INSTANCE = new $$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4();

    private /* synthetic */ $$Lambda$MryDialogsActivity$Y8U7vkySd7s49GgQtR_IOfZQ9q4() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
