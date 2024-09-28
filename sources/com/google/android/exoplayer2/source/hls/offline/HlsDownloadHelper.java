package com.google.android.exoplayer2.source.hls.offline;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class HlsDownloadHelper extends DownloadHelper<HlsPlaylist> {
    private final DataSource.Factory manifestDataSourceFactory;
    private int[] renditionGroups;

    public HlsDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, RenderersFactory renderersFactory) {
        this(uri, manifestDataSourceFactory2, DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS, renderersFactory, (DrmSessionManager<FrameworkMediaCrypto>) null);
    }

    public HlsDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, DefaultTrackSelector.Parameters trackSelectorParameters, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        super(DownloadAction.TYPE_HLS, uri, (String) null, trackSelectorParameters, renderersFactory, drmSessionManager);
        this.manifestDataSourceFactory = manifestDataSourceFactory2;
    }

    /* access modifiers changed from: protected */
    public HlsPlaylist loadManifest(Uri uri) throws IOException {
        return (HlsPlaylist) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new HlsPlaylistParser(), uri, 4);
    }

    /* access modifiers changed from: protected */
    public TrackGroupArray[] getTrackGroupArrays(HlsPlaylist playlist) {
        Assertions.checkNotNull(playlist);
        if (playlist instanceof HlsMediaPlaylist) {
            this.renditionGroups = new int[0];
            return new TrackGroupArray[]{TrackGroupArray.EMPTY};
        }
        HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) playlist;
        TrackGroup[] trackGroups = new TrackGroup[3];
        this.renditionGroups = new int[3];
        int trackGroupIndex = 0;
        if (!masterPlaylist.variants.isEmpty()) {
            this.renditionGroups[0] = 0;
            trackGroups[0] = new TrackGroup(toFormats(masterPlaylist.variants));
            trackGroupIndex = 0 + 1;
        }
        if (!masterPlaylist.audios.isEmpty()) {
            this.renditionGroups[trackGroupIndex] = 1;
            trackGroups[trackGroupIndex] = new TrackGroup(toFormats(masterPlaylist.audios));
            trackGroupIndex++;
        }
        if (!masterPlaylist.subtitles.isEmpty()) {
            this.renditionGroups[trackGroupIndex] = 2;
            trackGroups[trackGroupIndex] = new TrackGroup(toFormats(masterPlaylist.subtitles));
            trackGroupIndex++;
        }
        return new TrackGroupArray[]{new TrackGroupArray((TrackGroup[]) Arrays.copyOf(trackGroups, trackGroupIndex))};
    }

    /* access modifiers changed from: protected */
    public StreamKey toStreamKey(int periodIndex, int trackGroupIndex, int trackIndexInTrackGroup) {
        return new StreamKey(this.renditionGroups[trackGroupIndex], trackIndexInTrackGroup);
    }

    private static Format[] toFormats(List<HlsMasterPlaylist.HlsUrl> hlsUrls) {
        Format[] formats = new Format[hlsUrls.size()];
        for (int i = 0; i < hlsUrls.size(); i++) {
            formats[i] = hlsUrls.get(i).format;
        }
        return formats;
    }
}
