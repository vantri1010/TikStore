package com.google.android.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsUtil;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import java.io.IOException;

public final class SsDownloadHelper extends DownloadHelper<SsManifest> {
    private final DataSource.Factory manifestDataSourceFactory;

    public SsDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, RenderersFactory renderersFactory) {
        this(uri, manifestDataSourceFactory2, DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS, renderersFactory, (DrmSessionManager<FrameworkMediaCrypto>) null);
    }

    public SsDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, DefaultTrackSelector.Parameters trackSelectorParameters, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        super(DownloadAction.TYPE_SS, uri, (String) null, trackSelectorParameters, renderersFactory, drmSessionManager);
        this.manifestDataSourceFactory = manifestDataSourceFactory2;
    }

    /* access modifiers changed from: protected */
    public SsManifest loadManifest(Uri uri) throws IOException {
        return (SsManifest) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new SsManifestParser(), SsUtil.fixManifestUri(uri), 4);
    }

    /* access modifiers changed from: protected */
    public TrackGroupArray[] getTrackGroupArrays(SsManifest manifest) {
        SsManifest.StreamElement[] streamElements = manifest.streamElements;
        TrackGroup[] trackGroups = new TrackGroup[streamElements.length];
        for (int i = 0; i < streamElements.length; i++) {
            trackGroups[i] = new TrackGroup(streamElements[i].formats);
        }
        return new TrackGroupArray[]{new TrackGroupArray(trackGroups)};
    }

    /* access modifiers changed from: protected */
    public StreamKey toStreamKey(int periodIndex, int trackGroupIndex, int trackIndexInTrackGroup) {
        return new StreamKey(trackGroupIndex, trackIndexInTrackGroup);
    }
}
