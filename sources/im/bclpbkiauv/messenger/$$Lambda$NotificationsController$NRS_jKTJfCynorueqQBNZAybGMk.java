package im.bclpbkiauv.messenger;

import android.graphics.ImageDecoder;

/* renamed from: im.bclpbkiauv.messenger.-$$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk implements ImageDecoder.OnHeaderDecodedListener {
    public static final /* synthetic */ $$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk INSTANCE = new $$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk();

    private /* synthetic */ $$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk() {
    }

    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor($$Lambda$NotificationsController$Y8xgMMXw6mZCGvmd4fYMnt4S1aY.INSTANCE);
    }
}
