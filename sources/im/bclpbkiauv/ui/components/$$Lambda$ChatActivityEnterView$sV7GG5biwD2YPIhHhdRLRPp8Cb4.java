package im.bclpbkiauv.ui.components;

import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.ui.components.AlertsCreator;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4 INSTANCE = new $$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$sV7GG5biwD2YPIhHhdRLRPp8Cb4() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
