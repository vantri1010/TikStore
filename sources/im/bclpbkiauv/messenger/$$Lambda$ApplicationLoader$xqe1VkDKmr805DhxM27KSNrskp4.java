package im.bclpbkiauv.messenger;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4 implements Runnable {
    public static final /* synthetic */ $$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4 INSTANCE = new $$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4();

    private /* synthetic */ $$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4() {
    }

    public final void run() {
        GcmPushListenerService.sendUPushRegistrationToServer(ApplicationLoader.strDeviceKey);
    }
}
