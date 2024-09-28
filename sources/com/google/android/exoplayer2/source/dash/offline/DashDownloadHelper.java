package com.google.android.exoplayer2.source.dash.offline;

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
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import java.io.IOException;
import java.util.List;

public final class DashDownloadHelper extends DownloadHelper<DashManifest> {
    private final DataSource.Factory manifestDataSourceFactory;

    public DashDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, RenderersFactory renderersFactory) {
        this(uri, manifestDataSourceFactory2, DownloadHelper.DEFAULT_TRACK_SELECTOR_PARAMETERS, renderersFactory, (DrmSessionManager<FrameworkMediaCrypto>) null);
    }

    public DashDownloadHelper(Uri uri, DataSource.Factory manifestDataSourceFactory2, DefaultTrackSelector.Parameters trackSelectorParameters, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        super(DownloadAction.TYPE_DASH, uri, (String) null, trackSelectorParameters, renderersFactory, drmSessionManager);
        this.manifestDataSourceFactory = manifestDataSourceFactory2;
    }

    /* access modifiers changed from: protected */
    public DashManifest loadManifest(Uri uri) throws IOException {
        return (DashManifest) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new DashManifestParser(), uri, 4);
    }

    public TrackGroupArray[] getTrackGroupArrays(DashManifest manifest) {
        int periodCount = manifest.getPeriodCount();
        TrackGroupArray[] trackGroupArrays = new TrackGroupArray[periodCount];
        for (int periodIndex = 0; periodIndex < periodCount; periodIndex++) {
            List<AdaptationSet> adaptationSets = manifest.getPeriod(periodIndex).adaptationSets;
            TrackGroup[] trackGroups = new TrackGroup[adaptationSets.size()];
            for (int i = 0; i < trackGroups.length; i++) {
                List<Representation> representations = adaptationSets.get(i).representations;
                Format[] formats = new Format[representations.size()];
                int representationsCount = representations.size();
                for (int j = 0; j < representationsCount; j++) {
                    formats[j] = representations.get(j).format;
                }
                trackGroups[i] = new TrackGroup(formats);
            }
            trackGroupArrays[periodIndex] = new TrackGroupArray(trackGroups);
        }
        return trackGroupArrays;
    }

    /* access modifiers changed from: protected */
    public StreamKey toStreamKey(int periodIndex, int trackGroupIndex, int trackIndexInTrackGroup) {
        return new StreamKey(periodIndex, trackGroupIndex, trackIndexInTrackGroup);
    }
}
