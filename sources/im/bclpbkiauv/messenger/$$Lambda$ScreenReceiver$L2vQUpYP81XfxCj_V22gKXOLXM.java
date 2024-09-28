package im.bclpbkiauv.messenger;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$ScreenReceiver$L2-vQUpYP81XfxCj_V22gKXOLXM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ScreenReceiver$L2vQUpYP81XfxCj_V22gKXOLXM implements Runnable {
    public static final /* synthetic */ $$Lambda$ScreenReceiver$L2vQUpYP81XfxCj_V22gKXOLXM INSTANCE = new $$Lambda$ScreenReceiver$L2vQUpYP81XfxCj_V22gKXOLXM();

    private /* synthetic */ $$Lambda$ScreenReceiver$L2vQUpYP81XfxCj_V22gKXOLXM() {
    }

    public final void run() {
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.screenchangenotify, 0);
    }
}
