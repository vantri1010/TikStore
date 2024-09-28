package im.bclpbkiauv.ui.components;

import android.view.View;
import im.bclpbkiauv.messenger.MediaController;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8 implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8 INSTANCE = new $$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$Xxm2j3lWao5EOHV_XLM5F0T6Ch8() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playPreviousMessage();
    }
}
