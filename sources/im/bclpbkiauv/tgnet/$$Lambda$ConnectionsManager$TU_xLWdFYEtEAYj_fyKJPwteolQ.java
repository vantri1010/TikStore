package im.bclpbkiauv.tgnet;

import im.bclpbkiauv.messenger.NotificationCenter;

/* renamed from: im.bclpbkiauv.tgnet.-$$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ implements Runnable {
    public static final /* synthetic */ $$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ INSTANCE = new $$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ();

    private /* synthetic */ $$Lambda$ConnectionsManager$TU_xLWdFYEtEAYj_fyKJPwteolQ() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShowAlert, 3);
    }
}
