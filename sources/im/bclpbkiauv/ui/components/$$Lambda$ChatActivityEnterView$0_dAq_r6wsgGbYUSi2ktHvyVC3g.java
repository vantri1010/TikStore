package im.bclpbkiauv.ui.components;

import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.ui.components.AlertsCreator;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g INSTANCE = new $$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$0_dAq_r6wsgGbYUSi2ktHvyVC3g() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
