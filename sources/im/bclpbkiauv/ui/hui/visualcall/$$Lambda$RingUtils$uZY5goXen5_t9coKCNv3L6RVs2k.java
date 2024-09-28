package im.bclpbkiauv.ui.hui.visualcall;

import android.media.SoundPool;

/* renamed from: im.bclpbkiauv.ui.hui.visualcall.-$$Lambda$RingUtils$uZY5goXen5_t9coKCNv3L6RVs2k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$RingUtils$uZY5goXen5_t9coKCNv3L6RVs2k implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ $$Lambda$RingUtils$uZY5goXen5_t9coKCNv3L6RVs2k INSTANCE = new $$Lambda$RingUtils$uZY5goXen5_t9coKCNv3L6RVs2k();

    private /* synthetic */ $$Lambda$RingUtils$uZY5goXen5_t9coKCNv3L6RVs2k() {
    }

    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        soundPool.play(RingUtils.spConnectingId, 1.0f, 1.0f, 1, -1, 1.0f);
    }
}
