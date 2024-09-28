package im.bclpbkiauv.ui;

import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.GcmPushListenerService;

/* renamed from: im.bclpbkiauv.ui.-$$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA implements Runnable {
    public static final /* synthetic */ $$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA INSTANCE = new $$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA();

    private /* synthetic */ $$Lambda$LoginActivity$kWWVGtZDWVAjoGoiOhEBiwLBwjA() {
    }

    public final void run() {
        GcmPushListenerService.sendUPushRegistrationToServer(ApplicationLoader.strDeviceKey);
    }
}
