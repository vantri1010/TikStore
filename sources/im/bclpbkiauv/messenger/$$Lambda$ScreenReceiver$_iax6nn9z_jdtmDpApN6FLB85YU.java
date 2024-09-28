package im.bclpbkiauv.messenger;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU implements Runnable {
    public static final /* synthetic */ $$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU INSTANCE = new $$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU();

    private /* synthetic */ $$Lambda$ScreenReceiver$_iax6nn9z_jdtmDpApN6FLB85YU() {
    }

    public final void run() {
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.screenchangenotify, 1);
    }
}
