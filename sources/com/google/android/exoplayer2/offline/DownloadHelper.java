package com.google.android.exoplayer2.offline;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public abstract class DownloadHelper<T> {
    public static final DefaultTrackSelector.Parameters DEFAULT_TRACK_SELECTOR_PARAMETERS = new DefaultTrackSelector.ParametersBuilder().setForceHighestSupportedBitrate(true).build();
    private final String cacheKey;
    private int currentTrackSelectionPeriodIndex;
    private final String downloadType;
    private List<TrackSelection>[][] immutableTrackSelectionsByPeriodAndRenderer;
    private T manifest;
    private MappingTrackSelector.MappedTrackInfo[] mappedTrackInfos;
    private final RendererCapabilities[] rendererCapabilities;
    private final SparseIntArray scratchSet;
    private TrackGroupArray[] trackGroupArrays;
    private List<TrackSelection>[][] trackSelectionsByPeriodAndRenderer;
    private final DefaultTrackSelector trackSelector = new DefaultTrackSelector((TrackSelection.Factory) new DownloadTrackSelection.Factory());
    private final Uri uri;

    public interface Callback {
        void onPrepareError(DownloadHelper<?> downloadHelper, IOException iOException);

        void onPrepared(DownloadHelper<?> downloadHelper);
    }

    /* access modifiers changed from: protected */
    public abstract TrackGroupArray[] getTrackGroupArrays(T t);

    /* access modifiers changed from: protected */
    public abstract T loadManifest(Uri uri2) throws IOException;

    /* access modifiers changed from: protected */
    public abstract StreamKey toStreamKey(int i, int i2, int i3);

    public DownloadHelper(String downloadType2, Uri uri2, String cacheKey2, DefaultTrackSelector.Parameters trackSelectorParameters, RenderersFactory renderersFactory, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        this.downloadType = downloadType2;
        this.uri = uri2;
        this.cacheKey = cacheKey2;
        this.rendererCapabilities = Util.getRendererCapabilities(renderersFactory, drmSessionManager);
        this.scratchSet = new SparseIntArray();
        this.trackSelector.setParameters(trackSelectorParameters);
        this.trackSelector.init($$Lambda$DownloadHelper$7Zk9CqspX3ZiYWCChYkf8AiTY.INSTANCE, new DummyBandwidthMeter());
    }

    static /* synthetic */ void lambda$new$0() {
    }

    public final void prepare(Callback callback) {
        new Thread(new Runnable(new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper()), callback) {
            private final /* synthetic */ Handler f$1;
            private final /* synthetic */ DownloadHelper.Callback f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DownloadHelper.this.lambda$prepare$3$DownloadHelper(this.f$1, this.f$2);
            }
        }).start();
    }

    public /* synthetic */ void lambda$prepare$3$DownloadHelper(Handler handler, Callback callback) {
        try {
            T loadManifest = loadManifest(this.uri);
            this.manifest = loadManifest;
            TrackGroupArray[] trackGroupArrays2 = getTrackGroupArrays(loadManifest);
            this.trackGroupArrays = trackGroupArrays2;
            initializeTrackSelectionLists(trackGroupArrays2.length, this.rendererCapabilities.length);
            this.mappedTrackInfos = new MappingTrackSelector.MappedTrackInfo[this.trackGroupArrays.length];
            for (int i = 0; i < this.trackGroupArrays.length; i++) {
                this.trackSelector.onSelectionActivated(runTrackSelection(i).info);
                this.mappedTrackInfos[i] = (MappingTrackSelector.MappedTrackInfo) Assertions.checkNotNull(this.trackSelector.getCurrentMappedTrackInfo());
            }
            handler.post(new Runnable(callback) {
                private final /* synthetic */ DownloadHelper.Callback f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DownloadHelper.this.lambda$null$1$DownloadHelper(this.f$1);
                }
            });
        } catch (IOException e) {
            handler.post(new Runnable(callback, e) {
                private final /* synthetic */ DownloadHelper.Callback f$1;
                private final /* synthetic */ IOException f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DownloadHelper.this.lambda$null$2$DownloadHelper(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$DownloadHelper(Callback callback) {
        callback.onPrepared(this);
    }

    public /* synthetic */ void lambda$null$2$DownloadHelper(Callback callback, IOException e) {
        callback.onPrepareError(this, e);
    }

    public final T getManifest() {
        Assertions.checkNotNull(this.manifest);
        return this.manifest;
    }

    public final int getPeriodCount() {
        Assertions.checkNotNull(this.trackGroupArrays);
        return this.trackGroupArrays.length;
    }

    public final TrackGroupArray getTrackGroups(int periodIndex) {
        Assertions.checkNotNull(this.trackGroupArrays);
        return this.trackGroupArrays[periodIndex];
    }

    public final MappingTrackSelector.MappedTrackInfo getMappedTrackInfo(int periodIndex) {
        Assertions.checkNotNull(this.mappedTrackInfos);
        return this.mappedTrackInfos[periodIndex];
    }

    public final List<TrackSelection> getTrackSelections(int periodIndex, int rendererIndex) {
        Assertions.checkNotNull(this.immutableTrackSelectionsByPeriodAndRenderer);
        return this.immutableTrackSelectionsByPeriodAndRenderer[periodIndex][rendererIndex];
    }

    public final void clearTrackSelections(int periodIndex) {
        Assertions.checkNotNull(this.trackSelectionsByPeriodAndRenderer);
        for (int i = 0; i < this.rendererCapabilities.length; i++) {
            this.trackSelectionsByPeriodAndRenderer[periodIndex][i].clear();
        }
    }

    public final void replaceTrackSelections(int periodIndex, DefaultTrackSelector.Parameters trackSelectorParameters) {
        clearTrackSelections(periodIndex);
        addTrackSelection(periodIndex, trackSelectorParameters);
    }

    public final void addTrackSelection(int periodIndex, DefaultTrackSelector.Parameters trackSelectorParameters) {
        Assertions.checkNotNull(this.trackGroupArrays);
        Assertions.checkNotNull(this.trackSelectionsByPeriodAndRenderer);
        this.trackSelector.setParameters(trackSelectorParameters);
        runTrackSelection(periodIndex);
    }

    public final DownloadAction getDownloadAction(byte[] data) {
        Assertions.checkNotNull(this.trackSelectionsByPeriodAndRenderer);
        Assertions.checkNotNull(this.trackGroupArrays);
        List<StreamKey> streamKeys = new ArrayList<>();
        int periodCount = this.trackSelectionsByPeriodAndRenderer.length;
        for (int periodIndex = 0; periodIndex < periodCount; periodIndex++) {
            for (List<TrackSelection> trackSelectionList : this.trackSelectionsByPeriodAndRenderer[periodIndex]) {
                for (int selectionIndex = 0; selectionIndex < trackSelectionList.size(); selectionIndex++) {
                    TrackSelection trackSelection = trackSelectionList.get(selectionIndex);
                    int trackGroupIndex = this.trackGroupArrays[periodIndex].indexOf(trackSelection.getTrackGroup());
                    int trackCount = trackSelection.length();
                    for (int trackListIndex = 0; trackListIndex < trackCount; trackListIndex++) {
                        streamKeys.add(toStreamKey(periodIndex, trackGroupIndex, trackSelection.getIndexInTrackGroup(trackListIndex)));
                    }
                }
            }
        }
        return DownloadAction.createDownloadAction(this.downloadType, this.uri, streamKeys, this.cacheKey, data);
    }

    public final DownloadAction getRemoveAction() {
        return DownloadAction.createRemoveAction(this.downloadType, this.uri, this.cacheKey);
    }

    @EnsuresNonNull({"trackSelectionsByPeriodAndRenderer"})
    private void initializeTrackSelectionLists(int periodCount, int rendererCount) {
        int[] iArr = new int[2];
        iArr[1] = rendererCount;
        iArr[0] = periodCount;
        this.trackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance(List.class, iArr);
        int[] iArr2 = new int[2];
        iArr2[1] = rendererCount;
        iArr2[0] = periodCount;
        this.immutableTrackSelectionsByPeriodAndRenderer = (List[][]) Array.newInstance(List.class, iArr2);
        for (int i = 0; i < periodCount; i++) {
            for (int j = 0; j < rendererCount; j++) {
                this.trackSelectionsByPeriodAndRenderer[i][j] = new ArrayList();
                this.immutableTrackSelectionsByPeriodAndRenderer[i][j] = Collections.unmodifiableList(this.trackSelectionsByPeriodAndRenderer[i][j]);
            }
        }
    }

    @RequiresNonNull({"trackGroupArrays", "trackSelectionsByPeriodAndRenderer"})
    private TrackSelectorResult runTrackSelection(int periodIndex) {
        MediaSource.MediaPeriodId dummyMediaPeriodId = new MediaSource.MediaPeriodId(new Object());
        Timeline dummyTimeline = Timeline.EMPTY;
        this.currentTrackSelectionPeriodIndex = periodIndex;
        try {
            TrackSelectorResult trackSelectorResult = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroupArrays[periodIndex], dummyMediaPeriodId, dummyTimeline);
            for (int i = 0; i < trackSelectorResult.length; i++) {
                TrackSelection newSelection = trackSelectorResult.selections.get(i);
                if (newSelection != null) {
                    List<TrackSelection> existingSelectionList = this.trackSelectionsByPeriodAndRenderer[this.currentTrackSelectionPeriodIndex][i];
                    boolean mergedWithExistingSelection = false;
                    int j = 0;
                    while (true) {
                        if (j >= existingSelectionList.size()) {
                            break;
                        }
                        TrackSelection existingSelection = existingSelectionList.get(j);
                        if (existingSelection.getTrackGroup() == newSelection.getTrackGroup()) {
                            this.scratchSet.clear();
                            for (int k = 0; k < existingSelection.length(); k++) {
                                this.scratchSet.put(existingSelection.getIndexInTrackGroup(k), 0);
                            }
                            for (int k2 = 0; k2 < newSelection.length(); k2++) {
                                this.scratchSet.put(newSelection.getIndexInTrackGroup(k2), 0);
                            }
                            int[] mergedTracks = new int[this.scratchSet.size()];
                            for (int k3 = 0; k3 < this.scratchSet.size(); k3++) {
                                mergedTracks[k3] = this.scratchSet.keyAt(k3);
                            }
                            existingSelectionList.set(j, new DownloadTrackSelection(existingSelection.getTrackGroup(), mergedTracks));
                            mergedWithExistingSelection = true;
                        } else {
                            j++;
                        }
                    }
                    if (!mergedWithExistingSelection) {
                        existingSelectionList.add(newSelection);
                    }
                }
            }
            return trackSelectorResult;
        } catch (ExoPlaybackException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static final class DownloadTrackSelection extends BaseTrackSelection {

        private static final class Factory implements TrackSelection.Factory {
            @Deprecated
            public /* synthetic */ TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int... iArr) {
                return TrackSelection.Factory.CC.$default$createTrackSelection(this, trackGroup, bandwidthMeter, iArr);
            }

            private Factory() {
            }

            public TrackSelection[] createTrackSelections(TrackSelection.Definition[] definitions, BandwidthMeter bandwidthMeter) {
                TrackSelection[] selections = new TrackSelection[definitions.length];
                for (int i = 0; i < definitions.length; i++) {
                    selections[i] = definitions[i] == null ? null : new DownloadTrackSelection(definitions[i].group, definitions[i].tracks);
                }
                return selections;
            }
        }

        public DownloadTrackSelection(TrackGroup trackGroup, int[] tracks) {
            super(trackGroup, tracks);
        }

        public int getSelectedIndex() {
            return 0;
        }

        public int getSelectionReason() {
            return 0;
        }

        public Object getSelectionData() {
            return null;
        }
    }

    private static final class DummyBandwidthMeter implements BandwidthMeter {
        private DummyBandwidthMeter() {
        }

        public long getBitrateEstimate() {
            return 0;
        }

        public TransferListener getTransferListener() {
            return null;
        }

        public void addEventListener(Handler eventHandler, BandwidthMeter.EventListener eventListener) {
        }

        public void removeEventListener(BandwidthMeter.EventListener eventListener) {
        }
    }
}
