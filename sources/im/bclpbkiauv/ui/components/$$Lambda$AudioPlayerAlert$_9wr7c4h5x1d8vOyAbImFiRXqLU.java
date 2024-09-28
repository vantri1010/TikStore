package im.bclpbkiauv.ui.components;

import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.ui.components.SeekBarView;

/* renamed from: im.bclpbkiauv.ui.components.-$$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU implements SeekBarView.SeekBarViewDelegate {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU INSTANCE = new $$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$_9wr7c4h5x1d8vOyAbImFiRXqLU() {
    }

    public final void onSeekBarDrag(float f) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f);
    }
}
