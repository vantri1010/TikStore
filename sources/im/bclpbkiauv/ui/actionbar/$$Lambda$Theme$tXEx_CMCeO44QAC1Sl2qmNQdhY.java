package im.bclpbkiauv.ui.actionbar;

import im.bclpbkiauv.messenger.NotificationCenter;

/* renamed from: im.bclpbkiauv.ui.actionbar.-$$Lambda$Theme$tXEx_CMCeO44-QAC1Sl2qmNQdhY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Theme$tXEx_CMCeO44QAC1Sl2qmNQdhY implements Runnable {
    public static final /* synthetic */ $$Lambda$Theme$tXEx_CMCeO44QAC1Sl2qmNQdhY INSTANCE = new $$Lambda$Theme$tXEx_CMCeO44QAC1Sl2qmNQdhY();

    private /* synthetic */ $$Lambda$Theme$tXEx_CMCeO44QAC1Sl2qmNQdhY() {
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didSetNewTheme, false);
    }
}
