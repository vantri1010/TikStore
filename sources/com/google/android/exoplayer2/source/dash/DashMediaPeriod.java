package com.google.android.exoplayer2.source.dash;

import android.util.Pair;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.chunk.ChunkSampleStream;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.Descriptor;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

final class DashMediaPeriod implements MediaPeriod, SequenceableLoader.Callback<ChunkSampleStream<DashChunkSource>>, ChunkSampleStream.ReleaseCallback<DashChunkSource> {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final DashChunkSource.Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final long elapsedRealtimeOffsetMs;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private EventSampleStream[] eventSampleStreams = new EventSampleStream[0];
    private List<EventStream> eventStreams;
    final int id;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private boolean notifiedReadingStarted;
    private int periodIndex;
    private final PlayerEmsgHandler playerEmsgHandler;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private final IdentityHashMap<ChunkSampleStream<DashChunkSource>, PlayerEmsgHandler.PlayerTrackEmsgHandler> trackEmsgHandlerBySampleStream = new IdentityHashMap<>();
    private final TrackGroupInfo[] trackGroupInfos;
    private final TrackGroupArray trackGroups;
    private final TransferListener transferListener;

    public /* synthetic */ List<StreamKey> getStreamKeys(TrackSelection trackSelection) {
        return MediaPeriod.CC.$default$getStreamKeys(this, trackSelection);
    }

    public DashMediaPeriod(int id2, DashManifest manifest2, int periodIndex2, DashChunkSource.Factory chunkSourceFactory2, TransferListener transferListener2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, MediaSourceEventListener.EventDispatcher eventDispatcher2, long elapsedRealtimeOffsetMs2, LoaderErrorThrower manifestLoaderErrorThrower2, Allocator allocator2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, PlayerEmsgHandler.PlayerEmsgCallback playerEmsgCallback) {
        DashManifest dashManifest = manifest2;
        Allocator allocator3 = allocator2;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory3 = compositeSequenceableLoaderFactory2;
        this.id = id2;
        this.manifest = dashManifest;
        this.periodIndex = periodIndex2;
        this.chunkSourceFactory = chunkSourceFactory2;
        this.transferListener = transferListener2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
        this.eventDispatcher = eventDispatcher2;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs2;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower2;
        this.allocator = allocator3;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory3;
        this.playerEmsgHandler = new PlayerEmsgHandler(dashManifest, playerEmsgCallback, allocator3);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory3.createCompositeSequenceableLoader(this.sampleStreams);
        Period period = manifest2.getPeriod(periodIndex2);
        this.eventStreams = period.eventStreams;
        Pair<TrackGroupArray, TrackGroupInfo[]> result = buildTrackGroups(period.adaptationSets, this.eventStreams);
        this.trackGroups = (TrackGroupArray) result.first;
        this.trackGroupInfos = (TrackGroupInfo[]) result.second;
        eventDispatcher2.mediaPeriodCreated();
    }

    public void updateManifest(DashManifest manifest2, int periodIndex2) {
        this.manifest = manifest2;
        this.periodIndex = periodIndex2;
        this.playerEmsgHandler.updateManifest(manifest2);
        ChunkSampleStream<DashChunkSource>[] chunkSampleStreamArr = this.sampleStreams;
        if (chunkSampleStreamArr != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : chunkSampleStreamArr) {
                sampleStream.getChunkSource().updateManifest(manifest2, periodIndex2);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = manifest2.getPeriod(periodIndex2).eventStreams;
        for (EventSampleStream eventSampleStream : this.eventSampleStreams) {
            Iterator<EventStream> it = this.eventStreams.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                EventStream eventStream = it.next();
                if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                    boolean z = true;
                    int lastPeriodIndex = manifest2.getPeriodCount() - 1;
                    if (!manifest2.dynamic || periodIndex2 != lastPeriodIndex) {
                        z = false;
                    }
                    eventSampleStream.updateEventStream(eventStream, z);
                }
            }
        }
    }

    public void release() {
        this.playerEmsgHandler.release();
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release(this);
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public synchronized void onSampleStreamReleased(ChunkSampleStream<DashChunkSource> stream) {
        PlayerEmsgHandler.PlayerTrackEmsgHandler trackEmsgHandler = this.trackEmsgHandlerBySampleStream.remove(stream);
        if (trackEmsgHandler != null) {
            trackEmsgHandler.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback2, long positionUs) {
        this.callback = callback2;
        callback2.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int[] streamIndexToTrackGroupIndex = getStreamIndexToTrackGroupIndex(selections);
        releaseDisabledStreams(selections, mayRetainStreamFlags, streams);
        releaseOrphanEmbeddedStreams(selections, streams, streamIndexToTrackGroupIndex);
        selectNewStreams(selections, streams, streamResetFlags, positionUs, streamIndexToTrackGroupIndex);
        ArrayList<ChunkSampleStream<DashChunkSource>> sampleStreamList = new ArrayList<>();
        ArrayList<EventSampleStream> eventSampleStreamList = new ArrayList<>();
        for (ChunkSampleStream<DashChunkSource> stream : streams) {
            if (stream instanceof ChunkSampleStream) {
                sampleStreamList.add(stream);
            } else if (stream instanceof EventSampleStream) {
                eventSampleStreamList.add((EventSampleStream) stream);
            }
        }
        ChunkSampleStream<DashChunkSource>[] newSampleStreamArray = newSampleStreamArray(sampleStreamList.size());
        this.sampleStreams = newSampleStreamArray;
        sampleStreamList.toArray(newSampleStreamArray);
        EventSampleStream[] eventSampleStreamArr = new EventSampleStream[eventSampleStreamList.size()];
        this.eventSampleStreams = eventSampleStreamArr;
        eventSampleStreamList.toArray(eventSampleStreamArr);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        if (this.notifiedReadingStarted) {
            return C.TIME_UNSET;
        }
        this.eventDispatcher.readingStarted();
        this.notifiedReadingStarted = true;
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        for (EventSampleStream sampleStream2 : this.eventSampleStreams) {
            sampleStream2.seekToUs(positionUs);
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            if (sampleStream.primaryTrackType == 2) {
                return sampleStream.getAdjustedSeekPositionUs(positionUs, seekParameters);
            }
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private int[] getStreamIndexToTrackGroupIndex(TrackSelection[] selections) {
        int[] streamIndexToTrackGroupIndex = new int[selections.length];
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] != null) {
                streamIndexToTrackGroupIndex[i] = this.trackGroups.indexOf(selections[i].getTrackGroup());
            } else {
                streamIndexToTrackGroupIndex[i] = -1;
            }
        }
        return streamIndexToTrackGroupIndex;
    }

    private void releaseDisabledStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams) {
        for (int i = 0; i < selections.length; i++) {
            if (selections[i] == null || !mayRetainStreamFlags[i]) {
                if (streams[i] instanceof ChunkSampleStream) {
                    streams[i].release(this);
                } else if (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) {
                    streams[i].release();
                }
                streams[i] = null;
            }
        }
    }

    private void releaseOrphanEmbeddedStreams(TrackSelection[] selections, SampleStream[] streams, int[] streamIndexToTrackGroupIndex) {
        boolean mayRetainStream;
        for (int i = 0; i < selections.length; i++) {
            if ((streams[i] instanceof EmptySampleStream) || (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream)) {
                int primaryStreamIndex = getPrimaryStreamIndex(i, streamIndexToTrackGroupIndex);
                if (primaryStreamIndex == -1) {
                    mayRetainStream = streams[i] instanceof EmptySampleStream;
                } else {
                    mayRetainStream = (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) && streams[i].parent == streams[primaryStreamIndex];
                }
                if (!mayRetainStream) {
                    if (streams[i] instanceof ChunkSampleStream.EmbeddedSampleStream) {
                        streams[i].release();
                    }
                    streams[i] = null;
                }
            }
        }
    }

    private void selectNewStreams(TrackSelection[] selections, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, int[] streamIndexToTrackGroupIndex) {
        for (int i = 0; i < selections.length; i++) {
            if (streams[i] == null && selections[i] != null) {
                streamResetFlags[i] = true;
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[streamIndexToTrackGroupIndex[i]];
                if (trackGroupInfo.trackGroupCategory == 0) {
                    streams[i] = buildSampleStream(trackGroupInfo, selections[i], positionUs);
                } else if (trackGroupInfo.trackGroupCategory == 2) {
                    streams[i] = new EventSampleStream(this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex), selections[i].getTrackGroup().getFormat(0), this.manifest.dynamic);
                }
            }
        }
        for (int i2 = 0; i2 < selections.length; i2++) {
            if (streams[i2] == null && selections[i2] != null) {
                TrackGroupInfo trackGroupInfo2 = this.trackGroupInfos[streamIndexToTrackGroupIndex[i2]];
                if (trackGroupInfo2.trackGroupCategory == 1) {
                    int primaryStreamIndex = getPrimaryStreamIndex(i2, streamIndexToTrackGroupIndex);
                    if (primaryStreamIndex == -1) {
                        streams[i2] = new EmptySampleStream();
                    } else {
                        streams[i2] = streams[primaryStreamIndex].selectEmbeddedTrack(positionUs, trackGroupInfo2.trackType);
                    }
                }
            }
        }
    }

    private int getPrimaryStreamIndex(int embeddedStreamIndex, int[] streamIndexToTrackGroupIndex) {
        int embeddedTrackGroupIndex = streamIndexToTrackGroupIndex[embeddedStreamIndex];
        if (embeddedTrackGroupIndex == -1) {
            return -1;
        }
        int primaryTrackGroupIndex = this.trackGroupInfos[embeddedTrackGroupIndex].primaryTrackGroupIndex;
        for (int i = 0; i < streamIndexToTrackGroupIndex.length; i++) {
            int trackGroupIndex = streamIndexToTrackGroupIndex[i];
            if (trackGroupIndex == primaryTrackGroupIndex && this.trackGroupInfos[trackGroupIndex].trackGroupCategory == 0) {
                return i;
            }
        }
        return -1;
    }

    private static Pair<TrackGroupArray, TrackGroupInfo[]> buildTrackGroups(List<AdaptationSet> adaptationSets, List<EventStream> eventStreams2) {
        int[][] groupedAdaptationSetIndices = getGroupedAdaptationSetIndices(adaptationSets);
        int primaryGroupCount = groupedAdaptationSetIndices.length;
        boolean[] primaryGroupHasEventMessageTrackFlags = new boolean[primaryGroupCount];
        boolean[] primaryGroupHasCea608TrackFlags = new boolean[primaryGroupCount];
        int totalGroupCount = primaryGroupCount + identifyEmbeddedTracks(primaryGroupCount, adaptationSets, groupedAdaptationSetIndices, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags) + eventStreams2.size();
        TrackGroup[] trackGroups2 = new TrackGroup[totalGroupCount];
        TrackGroupInfo[] trackGroupInfos2 = new TrackGroupInfo[totalGroupCount];
        buildManifestEventTrackGroupInfos(eventStreams2, trackGroups2, trackGroupInfos2, buildPrimaryAndEmbeddedTrackGroupInfos(adaptationSets, groupedAdaptationSetIndices, primaryGroupCount, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags, trackGroups2, trackGroupInfos2));
        return Pair.create(new TrackGroupArray(trackGroups2), trackGroupInfos2);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> adaptationSets) {
        int adaptationSetCount = adaptationSets.size();
        SparseIntArray idToIndexMap = new SparseIntArray(adaptationSetCount);
        for (int i = 0; i < adaptationSetCount; i++) {
            idToIndexMap.put(adaptationSets.get(i).id, i);
        }
        int[][] groupedAdaptationSetIndices = new int[adaptationSetCount][];
        boolean[] adaptationSetUsedFlags = new boolean[adaptationSetCount];
        int groupCount = 0;
        for (int i2 = 0; i2 < adaptationSetCount; i2++) {
            if (!adaptationSetUsedFlags[i2]) {
                adaptationSetUsedFlags[i2] = true;
                Descriptor adaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(adaptationSets.get(i2).supplementalProperties);
                if (adaptationSetSwitchingProperty == null) {
                    groupedAdaptationSetIndices[groupCount] = new int[]{i2};
                    groupCount++;
                } else {
                    String[] extraAdaptationSetIds = Util.split(adaptationSetSwitchingProperty.value, ",");
                    int[] adaptationSetIndices = new int[(extraAdaptationSetIds.length + 1)];
                    adaptationSetIndices[0] = i2;
                    int outputIndex = 1;
                    for (String parseInt : extraAdaptationSetIds) {
                        int extraIndex = idToIndexMap.get(Integer.parseInt(parseInt), -1);
                        if (extraIndex != -1) {
                            adaptationSetUsedFlags[extraIndex] = true;
                            adaptationSetIndices[outputIndex] = extraIndex;
                            outputIndex++;
                        }
                    }
                    if (outputIndex < adaptationSetIndices.length) {
                        adaptationSetIndices = Arrays.copyOf(adaptationSetIndices, outputIndex);
                    }
                    groupedAdaptationSetIndices[groupCount] = adaptationSetIndices;
                    groupCount++;
                }
            }
        }
        return groupCount < adaptationSetCount ? (int[][]) Arrays.copyOf(groupedAdaptationSetIndices, groupCount) : groupedAdaptationSetIndices;
    }

    private static int identifyEmbeddedTracks(int primaryGroupCount, List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags) {
        int numEmbeddedTrack = 0;
        for (int i = 0; i < primaryGroupCount; i++) {
            if (hasEventMessageTrack(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasEventMessageTrackFlags[i] = true;
                numEmbeddedTrack++;
            }
            if (hasCea608Track(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasCea608TrackFlags[i] = true;
                numEmbeddedTrack++;
            }
        }
        return numEmbeddedTrack;
    }

    private static int buildPrimaryAndEmbeddedTrackGroupInfos(List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, int primaryGroupCount, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags, TrackGroup[] trackGroups2, TrackGroupInfo[] trackGroupInfos2) {
        int trackGroupCount;
        int cea608TrackGroupIndex;
        List<AdaptationSet> list = adaptationSets;
        int trackGroupCount2 = 0;
        int i = 0;
        while (i < primaryGroupCount) {
            int[] adaptationSetIndices = groupedAdaptationSetIndices[i];
            List<Representation> representations = new ArrayList<>();
            for (int adaptationSetIndex : adaptationSetIndices) {
                representations.addAll(list.get(adaptationSetIndex).representations);
            }
            Format[] formats = new Format[representations.size()];
            for (int j = 0; j < formats.length; j++) {
                formats[j] = representations.get(j).format;
            }
            AdaptationSet firstAdaptationSet = list.get(adaptationSetIndices[0]);
            int trackGroupCount3 = trackGroupCount2 + 1;
            if (primaryGroupHasEventMessageTrackFlags[i]) {
                trackGroupCount = trackGroupCount3 + 1;
            } else {
                trackGroupCount = trackGroupCount3;
                trackGroupCount3 = -1;
            }
            if (primaryGroupHasCea608TrackFlags[i]) {
                cea608TrackGroupIndex = trackGroupCount;
                trackGroupCount++;
            } else {
                cea608TrackGroupIndex = -1;
            }
            trackGroups2[trackGroupCount2] = new TrackGroup(formats);
            trackGroupInfos2[trackGroupCount2] = TrackGroupInfo.primaryTrack(firstAdaptationSet.type, adaptationSetIndices, trackGroupCount2, trackGroupCount3, cea608TrackGroupIndex);
            if (trackGroupCount3 != -1) {
                trackGroups2[trackGroupCount3] = new TrackGroup(Format.createSampleFormat(firstAdaptationSet.id + ":emsg", MimeTypes.APPLICATION_EMSG, (String) null, -1, (DrmInitData) null));
                trackGroupInfos2[trackGroupCount3] = TrackGroupInfo.embeddedEmsgTrack(adaptationSetIndices, trackGroupCount2);
            }
            if (cea608TrackGroupIndex != -1) {
                trackGroups2[cea608TrackGroupIndex] = new TrackGroup(Format.createTextSampleFormat(firstAdaptationSet.id + ":cea608", MimeTypes.APPLICATION_CEA608, 0, (String) null));
                trackGroupInfos2[cea608TrackGroupIndex] = TrackGroupInfo.embeddedCea608Track(adaptationSetIndices, trackGroupCount2);
            }
            i++;
            trackGroupCount2 = trackGroupCount;
        }
        return trackGroupCount2;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> eventStreams2, TrackGroup[] trackGroups2, TrackGroupInfo[] trackGroupInfos2, int existingTrackGroupCount) {
        int i = 0;
        while (i < eventStreams2.size()) {
            trackGroups2[existingTrackGroupCount] = new TrackGroup(Format.createSampleFormat(eventStreams2.get(i).id(), MimeTypes.APPLICATION_EMSG, (String) null, -1, (DrmInitData) null));
            trackGroupInfos2[existingTrackGroupCount] = TrackGroupInfo.mpdEventTrack(i);
            i++;
            existingTrackGroupCount++;
        }
    }

    /* JADX WARNING: type inference failed for: r0v13, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.exoplayer2.source.chunk.ChunkSampleStream<com.google.android.exoplayer2.source.dash.DashChunkSource> buildSampleStream(com.google.android.exoplayer2.source.dash.DashMediaPeriod.TrackGroupInfo r32, com.google.android.exoplayer2.trackselection.TrackSelection r33, long r34) {
        /*
            r31 = this;
            r12 = r31
            r13 = r32
            r0 = 0
            r1 = 2
            int[] r2 = new int[r1]
            com.google.android.exoplayer2.Format[] r1 = new com.google.android.exoplayer2.Format[r1]
            int r3 = r13.embeddedEventMessageTrackGroupIndex
            r4 = -1
            r5 = 1
            r6 = 0
            if (r3 == r4) goto L_0x0013
            r3 = 1
            goto L_0x0014
        L_0x0013:
            r3 = 0
        L_0x0014:
            r27 = r3
            if (r27 == 0) goto L_0x002c
            com.google.android.exoplayer2.source.TrackGroupArray r3 = r12.trackGroups
            int r7 = r13.embeddedEventMessageTrackGroupIndex
            com.google.android.exoplayer2.source.TrackGroup r3 = r3.get(r7)
            com.google.android.exoplayer2.Format r3 = r3.getFormat(r6)
            r1[r0] = r3
            int r3 = r0 + 1
            r7 = 4
            r2[r0] = r7
            r0 = r3
        L_0x002c:
            int r3 = r13.embeddedCea608TrackGroupIndex
            if (r3 == r4) goto L_0x0031
            goto L_0x0032
        L_0x0031:
            r5 = 0
        L_0x0032:
            r28 = r5
            if (r28 == 0) goto L_0x004b
            com.google.android.exoplayer2.source.TrackGroupArray r3 = r12.trackGroups
            int r4 = r13.embeddedCea608TrackGroupIndex
            com.google.android.exoplayer2.source.TrackGroup r3 = r3.get(r4)
            com.google.android.exoplayer2.Format r3 = r3.getFormat(r6)
            r1[r0] = r3
            int r3 = r0 + 1
            r4 = 3
            r2[r0] = r4
            r11 = r3
            goto L_0x004c
        L_0x004b:
            r11 = r0
        L_0x004c:
            int r0 = r2.length
            if (r11 >= r0) goto L_0x005f
            java.lang.Object[] r0 = java.util.Arrays.copyOf(r1, r11)
            r1 = r0
            com.google.android.exoplayer2.Format[] r1 = (com.google.android.exoplayer2.Format[]) r1
            int[] r2 = java.util.Arrays.copyOf(r2, r11)
            r29 = r1
            r30 = r2
            goto L_0x0063
        L_0x005f:
            r29 = r1
            r30 = r2
        L_0x0063:
            com.google.android.exoplayer2.source.dash.manifest.DashManifest r0 = r12.manifest
            boolean r0 = r0.dynamic
            if (r0 == 0) goto L_0x0072
            if (r27 == 0) goto L_0x0072
            com.google.android.exoplayer2.source.dash.PlayerEmsgHandler r0 = r12.playerEmsgHandler
            com.google.android.exoplayer2.source.dash.PlayerEmsgHandler$PlayerTrackEmsgHandler r0 = r0.newPlayerTrackEmsgHandler()
            goto L_0x0073
        L_0x0072:
            r0 = 0
        L_0x0073:
            r10 = r0
            com.google.android.exoplayer2.source.dash.DashChunkSource$Factory r14 = r12.chunkSourceFactory
            com.google.android.exoplayer2.upstream.LoaderErrorThrower r15 = r12.manifestLoaderErrorThrower
            com.google.android.exoplayer2.source.dash.manifest.DashManifest r0 = r12.manifest
            int r1 = r12.periodIndex
            int[] r2 = r13.adaptationSetIndices
            int r3 = r13.trackType
            long r4 = r12.elapsedRealtimeOffsetMs
            com.google.android.exoplayer2.upstream.TransferListener r6 = r12.transferListener
            r16 = r0
            r17 = r1
            r18 = r2
            r19 = r33
            r20 = r3
            r21 = r4
            r23 = r27
            r24 = r28
            r25 = r10
            r26 = r6
            com.google.android.exoplayer2.source.dash.DashChunkSource r14 = r14.createDashChunkSource(r15, r16, r17, r18, r19, r20, r21, r23, r24, r25, r26)
            com.google.android.exoplayer2.source.chunk.ChunkSampleStream r0 = new com.google.android.exoplayer2.source.chunk.ChunkSampleStream
            int r2 = r13.trackType
            com.google.android.exoplayer2.upstream.Allocator r7 = r12.allocator
            com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy r15 = r12.loadErrorHandlingPolicy
            com.google.android.exoplayer2.source.MediaSourceEventListener$EventDispatcher r8 = r12.eventDispatcher
            r1 = r0
            r3 = r30
            r4 = r29
            r5 = r14
            r6 = r31
            r16 = r8
            r8 = r34
            r13 = r10
            r10 = r15
            r15 = r11
            r11 = r16
            r1.<init>((int) r2, (int[]) r3, (com.google.android.exoplayer2.Format[]) r4, r5, r6, (com.google.android.exoplayer2.upstream.Allocator) r7, (long) r8, (com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy) r10, (com.google.android.exoplayer2.source.MediaSourceEventListener.EventDispatcher) r11)
            monitor-enter(r31)
            java.util.IdentityHashMap<com.google.android.exoplayer2.source.chunk.ChunkSampleStream<com.google.android.exoplayer2.source.dash.DashChunkSource>, com.google.android.exoplayer2.source.dash.PlayerEmsgHandler$PlayerTrackEmsgHandler> r0 = r12.trackEmsgHandlerBySampleStream     // Catch:{ all -> 0x00c2 }
            r0.put(r1, r13)     // Catch:{ all -> 0x00c2 }
            monitor-exit(r31)     // Catch:{ all -> 0x00c2 }
            return r1
        L_0x00c2:
            r0 = move-exception
            monitor-exit(r31)     // Catch:{ all -> 0x00c2 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.DashMediaPeriod.buildSampleStream(com.google.android.exoplayer2.source.dash.DashMediaPeriod$TrackGroupInfo, com.google.android.exoplayer2.trackselection.TrackSelection, long):com.google.android.exoplayer2.source.chunk.ChunkSampleStream");
    }

    private static Descriptor findAdaptationSetSwitchingProperty(List<Descriptor> descriptors) {
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = descriptors.get(i);
            if ("urn:mpeg:dash:adaptation-set-switching:2016".equals(descriptor.schemeIdUri)) {
                return descriptor;
            }
        }
        return null;
    }

    private static boolean hasEventMessageTrack(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Representation> representations = adaptationSets.get(i).representations;
            for (int j = 0; j < representations.size(); j++) {
                if (!representations.get(j).inbandEventStreams.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasCea608Track(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Descriptor> descriptors = adaptationSets.get(i).accessibilityDescriptors;
            for (int j = 0; j < descriptors.size(); j++) {
                if ("urn:scte:dash:cc:cea-608:2015".equals(descriptors.get(j).schemeIdUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }

    private static final class TrackGroupInfo {
        private static final int CATEGORY_EMBEDDED = 1;
        private static final int CATEGORY_MANIFEST_EVENTS = 2;
        private static final int CATEGORY_PRIMARY = 0;
        public final int[] adaptationSetIndices;
        public final int embeddedCea608TrackGroupIndex;
        public final int embeddedEventMessageTrackGroupIndex;
        public final int eventStreamGroupIndex;
        public final int primaryTrackGroupIndex;
        public final int trackGroupCategory;
        public final int trackType;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        public @interface TrackGroupCategory {
        }

        public static TrackGroupInfo primaryTrack(int trackType2, int[] adaptationSetIndices2, int primaryTrackGroupIndex2, int embeddedEventMessageTrackGroupIndex2, int embeddedCea608TrackGroupIndex2) {
            return new TrackGroupInfo(trackType2, 0, adaptationSetIndices2, primaryTrackGroupIndex2, embeddedEventMessageTrackGroupIndex2, embeddedCea608TrackGroupIndex2, -1);
        }

        public static TrackGroupInfo embeddedEmsgTrack(int[] adaptationSetIndices2, int primaryTrackGroupIndex2) {
            return new TrackGroupInfo(4, 1, adaptationSetIndices2, primaryTrackGroupIndex2, -1, -1, -1);
        }

        public static TrackGroupInfo embeddedCea608Track(int[] adaptationSetIndices2, int primaryTrackGroupIndex2) {
            return new TrackGroupInfo(3, 1, adaptationSetIndices2, primaryTrackGroupIndex2, -1, -1, -1);
        }

        public static TrackGroupInfo mpdEventTrack(int eventStreamIndex) {
            return new TrackGroupInfo(4, 2, (int[]) null, -1, -1, -1, eventStreamIndex);
        }

        private TrackGroupInfo(int trackType2, int trackGroupCategory2, int[] adaptationSetIndices2, int primaryTrackGroupIndex2, int embeddedEventMessageTrackGroupIndex2, int embeddedCea608TrackGroupIndex2, int eventStreamGroupIndex2) {
            this.trackType = trackType2;
            this.adaptationSetIndices = adaptationSetIndices2;
            this.trackGroupCategory = trackGroupCategory2;
            this.primaryTrackGroupIndex = primaryTrackGroupIndex2;
            this.embeddedEventMessageTrackGroupIndex = embeddedEventMessageTrackGroupIndex2;
            this.embeddedCea608TrackGroupIndex = embeddedCea608TrackGroupIndex2;
            this.eventStreamGroupIndex = eventStreamGroupIndex2;
        }
    }
}
