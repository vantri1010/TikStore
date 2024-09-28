package im.bclpbkiauv.ui.components;

import im.bclpbkiauv.messenger.MediaController;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4 implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4 INSTANCE = new $$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$rpH_n2Nj5ZoAzBsQTu5UdlTDaM4() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
