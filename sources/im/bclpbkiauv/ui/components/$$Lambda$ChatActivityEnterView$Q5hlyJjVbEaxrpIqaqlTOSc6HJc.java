package im.bclpbkiauv.ui.components;

import im.bclpbkiauv.messenger.MediaController;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc INSTANCE = new $$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$Q5hlyJjVbEaxrpIqaqlTOSc6HJc() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
