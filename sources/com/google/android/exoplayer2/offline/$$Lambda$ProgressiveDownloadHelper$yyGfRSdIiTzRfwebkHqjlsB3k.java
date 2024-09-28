package com.google.android.exoplayer2.offline;

import android.os.Handler;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

/* renamed from: com.google.android.exoplayer2.offline.-$$Lambda$ProgressiveDownloadHelper$yyGfRS-dIiTzRfwebkHqjls-B3k  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ProgressiveDownloadHelper$yyGfRSdIiTzRfwebkHqjlsB3k implements RenderersFactory {
    public static final /* synthetic */ $$Lambda$ProgressiveDownloadHelper$yyGfRSdIiTzRfwebkHqjlsB3k INSTANCE = new $$Lambda$ProgressiveDownloadHelper$yyGfRSdIiTzRfwebkHqjlsB3k();

    private /* synthetic */ $$Lambda$ProgressiveDownloadHelper$yyGfRSdIiTzRfwebkHqjlsB3k() {
    }

    public final Renderer[] createRenderers(Handler handler, VideoRendererEventListener videoRendererEventListener, AudioRendererEventListener audioRendererEventListener, TextOutput textOutput, MetadataOutput metadataOutput, DrmSessionManager drmSessionManager) {
        return ProgressiveDownloadHelper.lambda$new$0(handler, videoRendererEventListener, audioRendererEventListener, textOutput, metadataOutput, drmSessionManager);
    }
}
