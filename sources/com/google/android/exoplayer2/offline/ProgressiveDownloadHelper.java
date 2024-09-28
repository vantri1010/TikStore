package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public final class ProgressiveDownloadHelper extends DownloadHelper<Void> {
    public ProgressiveDownloadHelper(Uri uri) {
        this(uri, (String) null);
    }

    public ProgressiveDownloadHelper(Uri uri, String cacheKey) {
        super(DownloadAction.TYPE_PROGRESSIVE, uri, cacheKey, DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS, $$Lambda$ProgressiveDownloadHelper$yyGfRSdIiTzRfwebkHqjlsB3k.INSTANCE, (DrmSessionManager<FrameworkMediaCrypto>) null);
    }

    static /* synthetic */ Renderer[] lambda$new$0(Handler handler, VideoRendererEventListener videoListener, AudioRendererEventListener audioListener, TextOutput metadata, MetadataOutput text, DrmSessionManager drm) {
        return new Renderer[0];
    }

    /* access modifiers changed from: protected */
    public Void loadManifest(Uri uri) {
        return null;
    }

    /* access modifiers changed from: protected */
    public TrackGroupArray[] getTrackGroupArrays(Void manifest) {
        return new TrackGroupArray[]{TrackGroupArray.EMPTY};
    }

    /* access modifiers changed from: protected */
    public StreamKey toStreamKey(int periodIndex, int trackGroupIndex, int trackIndexInTrackGroup) {
        return new StreamKey(periodIndex, trackGroupIndex, trackIndexInTrackGroup);
    }
}
