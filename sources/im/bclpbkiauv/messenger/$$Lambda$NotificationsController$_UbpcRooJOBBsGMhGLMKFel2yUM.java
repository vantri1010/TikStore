package im.bclpbkiauv.messenger;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM implements Runnable {
    public static final /* synthetic */ $$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM INSTANCE = new $$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM();

    private /* synthetic */ $$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }
}
