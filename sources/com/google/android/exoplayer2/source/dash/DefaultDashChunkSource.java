package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Track;
import com.google.android.exoplayer2.extractor.rawcc.RawCcExtractor;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.ChunkExtractorWrapper;
import com.google.android.exoplayer2.source.chunk.ChunkHolder;
import com.google.android.exoplayer2.source.chunk.ContainerMediaChunk;
import com.google.android.exoplayer2.source.chunk.InitializationChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.SingleSampleMediaChunk;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.RangedUri;
import com.google.android.exoplayer2.source.dash.manifest.Representation;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultDashChunkSource implements DashChunkSource {
    private final int[] adaptationSetIndices;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private long liveEdgeTimeUs = C.TIME_UNSET;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    private final PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler;
    protected final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;
    private final int trackType;

    public static final class Factory implements DashChunkSource.Factory {
        private final DataSource.Factory dataSourceFactory;
        private final int maxSegmentsPerLoad;

        public Factory(DataSource.Factory dataSourceFactory2) {
            this(dataSourceFactory2, 1);
        }

        public Factory(DataSource.Factory dataSourceFactory2, int maxSegmentsPerLoad2) {
            this.dataSourceFactory = dataSourceFactory2;
            this.maxSegmentsPerLoad = maxSegmentsPerLoad2;
        }

        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, long elapsedRealtimeOffsetMs, boolean enableEventMessageTrack, boolean enableCea608Track, PlayerEmsgHandler.PlayerTrackEmsgHandler playerEmsgHandler, TransferListener transferListener) {
            TransferListener transferListener2 = transferListener;
            DataSource dataSource = this.dataSourceFactory.createDataSource();
            if (transferListener2 != null) {
                dataSource.addTransferListener(transferListener2);
            }
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndices, trackSelection, trackType, dataSource, elapsedRealtimeOffsetMs, this.maxSegmentsPerLoad, enableEventMessageTrack, enableCea608Track, playerEmsgHandler);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower2, DashManifest manifest2, int periodIndex2, int[] adaptationSetIndices2, TrackSelection trackSelection2, int trackType2, DataSource dataSource2, long elapsedRealtimeOffsetMs2, int maxSegmentsPerLoad2, boolean enableEventMessageTrack, boolean enableCea608Track, PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler2) {
        TrackSelection trackSelection3 = trackSelection2;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower2;
        this.manifest = manifest2;
        this.adaptationSetIndices = adaptationSetIndices2;
        this.trackSelection = trackSelection3;
        this.trackType = trackType2;
        this.dataSource = dataSource2;
        this.periodIndex = periodIndex2;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs2;
        this.maxSegmentsPerLoad = maxSegmentsPerLoad2;
        this.playerTrackEmsgHandler = playerTrackEmsgHandler2;
        long periodDurationUs = manifest2.getPeriodDurationUs(periodIndex2);
        List<Representation> representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection2.length()];
        int i = 0;
        while (i < this.representationHolders.length) {
            int i2 = i;
            this.representationHolders[i2] = new RepresentationHolder(periodDurationUs, trackType2, representations.get(trackSelection3.getIndexInTrackGroup(i)), enableEventMessageTrack, enableCea608Track, playerTrackEmsgHandler2);
            i = i2 + 1;
            long j = elapsedRealtimeOffsetMs2;
            int i3 = maxSegmentsPerLoad2;
            PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler3 = playerTrackEmsgHandler2;
            representations = representations;
        }
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        long j = positionUs;
        for (RepresentationHolder representationHolder : this.representationHolders) {
            if (representationHolder.segmentIndex != null) {
                long segmentNum = representationHolder.getSegmentNum(j);
                long firstSyncUs = representationHolder.getSegmentStartTimeUs(segmentNum);
                return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, (firstSyncUs >= j || segmentNum >= ((long) (representationHolder.getSegmentCount() + -1))) ? firstSyncUs : representationHolder.getSegmentStartTimeUs(1 + segmentNum));
            }
        }
        return j;
    }

    public void updateManifest(DashManifest newManifest, int newPeriodIndex) {
        try {
            this.manifest = newManifest;
            this.periodIndex = newPeriodIndex;
            long periodDurationUs = newManifest.getPeriodDurationUs(newPeriodIndex);
            List<Representation> representations = getRepresentations();
            for (int i = 0; i < this.representationHolders.length; i++) {
                this.representationHolders[i] = this.representationHolders[i].copyWithNewRepresentation(periodDurationUs, representations.get(this.trackSelection.getIndexInTrackGroup(i)));
            }
        } catch (BehindLiveWindowException e) {
            this.fatalError = e;
        }
    }

    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException == null) {
            this.manifestLoaderErrorThrower.maybeThrowError();
            return;
        }
        throw iOException;
    }

    public int getPreferredQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (this.fatalError != null || this.trackSelection.length() < 2) {
            return queue.size();
        }
        return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
    }

    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<? extends MediaChunk> queue, ChunkHolder out) {
        MediaChunk mediaChunk;
        ChunkHolder chunkHolder;
        int maxSegmentCount;
        RangedUri pendingInitializationUri;
        RangedUri pendingIndexUri;
        MediaChunkIterator[] chunkIterators;
        int i;
        ChunkHolder chunkHolder2 = out;
        if (this.fatalError == null) {
            long bufferedDurationUs = loadPositionUs - playbackPositionUs;
            long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(playbackPositionUs);
            long presentationPositionUs = C.msToUs(this.manifest.availabilityStartTimeMs) + C.msToUs(this.manifest.getPeriod(this.periodIndex).startMs) + loadPositionUs;
            PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler2 = this.playerTrackEmsgHandler;
            if (playerTrackEmsgHandler2 == null || !playerTrackEmsgHandler2.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs)) {
                long nowUnixTimeUs = getNowUnixTimeUs();
                if (queue.isEmpty()) {
                    mediaChunk = null;
                    List<? extends MediaChunk> list = queue;
                } else {
                    mediaChunk = (MediaChunk) queue.get(queue.size() - 1);
                }
                MediaChunk previous = mediaChunk;
                MediaChunkIterator[] chunkIterators2 = new MediaChunkIterator[this.trackSelection.length()];
                int i2 = 0;
                while (i2 < chunkIterators2.length) {
                    RepresentationHolder representationHolder = this.representationHolders[i2];
                    if (representationHolder.segmentIndex == null) {
                        chunkIterators2[i2] = MediaChunkIterator.EMPTY;
                        i = i2;
                        chunkIterators = chunkIterators2;
                    } else {
                        long firstAvailableSegmentNum = representationHolder.getFirstAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
                        long lastAvailableSegmentNum = representationHolder.getLastAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs);
                        i = i2;
                        RepresentationHolder representationHolder2 = representationHolder;
                        chunkIterators = chunkIterators2;
                        long segmentNum = getSegmentNum(representationHolder, previous, loadPositionUs, firstAvailableSegmentNum, lastAvailableSegmentNum);
                        if (segmentNum < firstAvailableSegmentNum) {
                            chunkIterators[i] = MediaChunkIterator.EMPTY;
                        } else {
                            chunkIterators[i] = new RepresentationSegmentIterator(representationHolder2, segmentNum, lastAvailableSegmentNum);
                        }
                    }
                    i2 = i + 1;
                    List<? extends MediaChunk> list2 = queue;
                    chunkIterators2 = chunkIterators;
                }
                int i3 = i2;
                long nowUnixTimeUs2 = nowUnixTimeUs;
                this.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, chunkIterators2);
                RepresentationHolder representationHolder3 = this.representationHolders[this.trackSelection.getSelectedIndex()];
                if (representationHolder3.extractorWrapper != null) {
                    Representation selectedRepresentation = representationHolder3.representation;
                    if (representationHolder3.extractorWrapper.getSampleFormats() == null) {
                        pendingInitializationUri = selectedRepresentation.getInitializationUri();
                    } else {
                        pendingInitializationUri = null;
                    }
                    if (representationHolder3.segmentIndex == null) {
                        pendingIndexUri = selectedRepresentation.getIndexUri();
                    } else {
                        pendingIndexUri = null;
                    }
                    if (!(pendingInitializationUri == null && pendingIndexUri == null)) {
                        Representation representation = selectedRepresentation;
                        chunkHolder2.chunk = newInitializationChunk(representationHolder3, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
                        return;
                    }
                }
                long periodDurationUs = representationHolder3.periodDurationUs;
                long j = C.TIME_UNSET;
                boolean periodEnded = periodDurationUs != C.TIME_UNSET;
                if (representationHolder3.getSegmentCount() == 0) {
                    chunkHolder2.endOfStream = periodEnded;
                    return;
                }
                long firstAvailableSegmentNum2 = representationHolder3.getFirstAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs2);
                long lastAvailableSegmentNum2 = representationHolder3.getLastAvailableSegmentNum(this.manifest, this.periodIndex, nowUnixTimeUs2);
                updateLiveEdgeTimeUs(representationHolder3, lastAvailableSegmentNum2);
                long lastAvailableSegmentNum3 = lastAvailableSegmentNum2;
                long j2 = nowUnixTimeUs2;
                boolean periodEnded2 = periodEnded;
                RepresentationHolder representationHolder4 = representationHolder3;
                long segmentNum2 = getSegmentNum(representationHolder3, previous, loadPositionUs, firstAvailableSegmentNum2, lastAvailableSegmentNum3);
                if (segmentNum2 < firstAvailableSegmentNum2) {
                    this.fatalError = new BehindLiveWindowException();
                    return;
                }
                if (segmentNum2 > lastAvailableSegmentNum3) {
                    long j3 = segmentNum2;
                    chunkHolder = chunkHolder2;
                    long j4 = presentationPositionUs;
                } else if (this.missingLastSegment && segmentNum2 >= lastAvailableSegmentNum3) {
                    RepresentationHolder representationHolder5 = representationHolder4;
                    long j5 = segmentNum2;
                    chunkHolder = chunkHolder2;
                    long j6 = presentationPositionUs;
                } else if (!periodEnded2 || representationHolder4.getSegmentStartTimeUs(segmentNum2) < periodDurationUs) {
                    int maxSegmentCount2 = (int) Math.min((long) this.maxSegmentsPerLoad, (lastAvailableSegmentNum3 - segmentNum2) + 1);
                    if (periodDurationUs != C.TIME_UNSET) {
                        while (maxSegmentCount2 > 1 && representationHolder4.getSegmentStartTimeUs((((long) maxSegmentCount2) + segmentNum2) - 1) >= periodDurationUs) {
                            maxSegmentCount2--;
                        }
                        maxSegmentCount = maxSegmentCount2;
                    } else {
                        maxSegmentCount = maxSegmentCount2;
                    }
                    if (queue.isEmpty()) {
                        j = loadPositionUs;
                    }
                    long j7 = presentationPositionUs;
                    long presentationPositionUs2 = j;
                    long j8 = segmentNum2;
                    RepresentationHolder representationHolder6 = representationHolder4;
                    chunkHolder2.chunk = newMediaChunk(representationHolder4, this.dataSource, this.trackType, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), segmentNum2, maxSegmentCount, presentationPositionUs2);
                    return;
                } else {
                    chunkHolder2.endOfStream = true;
                    return;
                }
                chunkHolder.endOfStream = periodEnded2;
            }
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        SeekMap seekMap;
        if (chunk instanceof InitializationChunk) {
            int trackIndex = this.trackSelection.indexOf(((InitializationChunk) chunk).trackFormat);
            RepresentationHolder representationHolder = this.representationHolders[trackIndex];
            if (representationHolder.segmentIndex == null && (seekMap = representationHolder.extractorWrapper.getSeekMap()) != null) {
                this.representationHolders[trackIndex] = representationHolder.copyWithNewSegmentIndex(new DashWrappingSegmentIndex((ChunkIndex) seekMap, representationHolder.representation.presentationTimeOffsetUs));
            }
        }
        PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler2 = this.playerTrackEmsgHandler;
        if (playerTrackEmsgHandler2 != null) {
            playerTrackEmsgHandler2.onChunkLoadCompleted(chunk);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
        r1 = r9.representationHolders[r9.trackSelection.indexOf(r10.trackFormat)];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onChunkLoadError(com.google.android.exoplayer2.source.chunk.Chunk r10, boolean r11, java.lang.Exception r12, long r13) {
        /*
            r9 = this;
            r0 = 0
            if (r11 != 0) goto L_0x0004
            return r0
        L_0x0004:
            com.google.android.exoplayer2.source.dash.PlayerEmsgHandler$PlayerTrackEmsgHandler r1 = r9.playerTrackEmsgHandler
            r2 = 1
            if (r1 == 0) goto L_0x0010
            boolean r1 = r1.maybeRefreshManifestOnLoadingError(r10)
            if (r1 == 0) goto L_0x0010
            return r2
        L_0x0010:
            com.google.android.exoplayer2.source.dash.manifest.DashManifest r1 = r9.manifest
            boolean r1 = r1.dynamic
            if (r1 != 0) goto L_0x0053
            boolean r1 = r10 instanceof com.google.android.exoplayer2.source.chunk.MediaChunk
            if (r1 == 0) goto L_0x0053
            boolean r1 = r12 instanceof com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
            if (r1 == 0) goto L_0x0053
            r1 = r12
            com.google.android.exoplayer2.upstream.HttpDataSource$InvalidResponseCodeException r1 = (com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException) r1
            int r1 = r1.responseCode
            r3 = 404(0x194, float:5.66E-43)
            if (r1 != r3) goto L_0x0053
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$RepresentationHolder[] r1 = r9.representationHolders
            com.google.android.exoplayer2.trackselection.TrackSelection r3 = r9.trackSelection
            com.google.android.exoplayer2.Format r4 = r10.trackFormat
            int r3 = r3.indexOf((com.google.android.exoplayer2.Format) r4)
            r1 = r1[r3]
            int r3 = r1.getSegmentCount()
            r4 = -1
            if (r3 == r4) goto L_0x0053
            if (r3 == 0) goto L_0x0053
            long r4 = r1.getFirstSegmentNum()
            long r6 = (long) r3
            long r4 = r4 + r6
            r6 = 1
            long r4 = r4 - r6
            r6 = r10
            com.google.android.exoplayer2.source.chunk.MediaChunk r6 = (com.google.android.exoplayer2.source.chunk.MediaChunk) r6
            long r6 = r6.getNextChunkIndex()
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x0053
            r9.missingLastSegment = r2
            return r2
        L_0x0053:
            r3 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r1 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x006c
            com.google.android.exoplayer2.trackselection.TrackSelection r1 = r9.trackSelection
            com.google.android.exoplayer2.Format r3 = r10.trackFormat
            int r3 = r1.indexOf((com.google.android.exoplayer2.Format) r3)
            boolean r1 = r1.blacklist(r3, r13)
            if (r1 == 0) goto L_0x006c
            r0 = 1
            goto L_0x006d
        L_0x006c:
        L_0x006d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.dash.DefaultDashChunkSource.onChunkLoadError(com.google.android.exoplayer2.source.chunk.Chunk, boolean, java.lang.Exception, long):boolean");
    }

    private long getSegmentNum(RepresentationHolder representationHolder, MediaChunk previousChunk, long loadPositionUs, long firstAvailableSegmentNum, long lastAvailableSegmentNum) {
        if (previousChunk != null) {
            RepresentationHolder representationHolder2 = representationHolder;
            long j = loadPositionUs;
            return previousChunk.getNextChunkIndex();
        }
        RepresentationHolder representationHolder3 = representationHolder;
        long j2 = loadPositionUs;
        return Util.constrainValue(representationHolder.getSegmentNum(loadPositionUs), firstAvailableSegmentNum, lastAvailableSegmentNum);
    }

    private ArrayList<Representation> getRepresentations() {
        List<AdaptationSet> manifestAdaptationSets = this.manifest.getPeriod(this.periodIndex).adaptationSets;
        ArrayList<Representation> representations = new ArrayList<>();
        for (int adaptationSetIndex : this.adaptationSetIndices) {
            representations.addAll(manifestAdaptationSets.get(adaptationSetIndex).representations);
        }
        return representations;
    }

    private void updateLiveEdgeTimeUs(RepresentationHolder representationHolder, long lastAvailableSegmentNum) {
        this.liveEdgeTimeUs = this.manifest.dynamic ? representationHolder.getSegmentEndTimeUs(lastAvailableSegmentNum) : C.TIME_UNSET;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (this.manifest.dynamic && this.liveEdgeTimeUs != C.TIME_UNSET) {
            return this.liveEdgeTimeUs - playbackPositionUs;
        }
        return C.TIME_UNSET;
    }

    /* access modifiers changed from: protected */
    public Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource2, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        RepresentationHolder representationHolder2 = representationHolder;
        RangedUri rangedUri = initializationUri;
        String baseUrl = representationHolder2.representation.baseUrl;
        if (rangedUri != null) {
            requestUri = rangedUri.attemptMerge(indexUri, baseUrl);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            RangedUri rangedUri2 = indexUri;
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource2, new DataSpec(requestUri.resolveUri(baseUrl), requestUri.start, requestUri.length, representationHolder2.representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, representationHolder2.extractorWrapper);
    }

    /* access modifiers changed from: protected */
    public Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource2, int trackType2, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long firstSegmentNum, int maxSegmentCount, long seekTimeUs) {
        RangedUri mergedSegmentUri;
        RepresentationHolder representationHolder2 = representationHolder;
        long j = firstSegmentNum;
        Representation representation = representationHolder2.representation;
        long startTimeUs = representationHolder2.getSegmentStartTimeUs(j);
        RangedUri segmentUri = representationHolder2.getSegmentUrl(j);
        String baseUrl = representation.baseUrl;
        if (representationHolder2.extractorWrapper == null) {
            RangedUri rangedUri = segmentUri;
            String str = baseUrl;
            Representation representation2 = representation;
            return new SingleSampleMediaChunk(dataSource2, new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, representationHolder2.getSegmentEndTimeUs(j), firstSegmentNum, trackType2, trackFormat);
        }
        RangedUri segmentUri2 = segmentUri;
        String baseUrl2 = baseUrl;
        Representation representation3 = representation;
        int i = 1;
        int segmentCount = 1;
        RangedUri segmentUri3 = segmentUri2;
        while (i < maxSegmentCount && (mergedSegmentUri = segmentUri3.attemptMerge(representationHolder2.getSegmentUrl(firstSegmentNum + ((long) i)), baseUrl2)) != null) {
            segmentUri3 = mergedSegmentUri;
            segmentCount++;
            i++;
        }
        long endTimeUs = representationHolder2.getSegmentEndTimeUs((firstSegmentNum + ((long) segmentCount)) - 1);
        long periodDurationUs = representationHolder.periodDurationUs;
        long clippedEndTimeUs = (periodDurationUs == C.TIME_UNSET || periodDurationUs > endTimeUs) ? -9223372036854775807L : periodDurationUs;
        DataSpec dataSpec = new DataSpec(segmentUri3.resolveUri(baseUrl2), segmentUri3.start, segmentUri3.length, representation3.getCacheKey());
        Representation representation4 = representation3;
        long sampleOffsetUs = -representation4.presentationTimeOffsetUs;
        long j2 = sampleOffsetUs;
        Representation representation5 = representation4;
        RangedUri rangedUri2 = segmentUri3;
        String str2 = baseUrl2;
        return new ContainerMediaChunk(dataSource2, dataSpec, trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, seekTimeUs, clippedEndTimeUs, firstSegmentNum, segmentCount, sampleOffsetUs, representationHolder2.extractorWrapper);
    }

    protected static final class RepresentationSegmentIterator extends BaseMediaChunkIterator {
        private final RepresentationHolder representationHolder;

        public RepresentationSegmentIterator(RepresentationHolder representation, long firstAvailableSegmentNum, long lastAvailableSegmentNum) {
            super(firstAvailableSegmentNum, lastAvailableSegmentNum);
            this.representationHolder = representation;
        }

        public DataSpec getDataSpec() {
            checkInBounds();
            Representation representation = this.representationHolder.representation;
            RangedUri segmentUri = this.representationHolder.getSegmentUrl(getCurrentIndex());
            Uri resolvedUri = segmentUri.resolveUri(representation.baseUrl);
            return new DataSpec(resolvedUri, segmentUri.start, segmentUri.length, representation.getCacheKey());
        }

        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentStartTimeUs(getCurrentIndex());
        }

        public long getChunkEndTimeUs() {
            checkInBounds();
            return this.representationHolder.getSegmentEndTimeUs(getCurrentIndex());
        }
    }

    protected static final class RepresentationHolder {
        final ChunkExtractorWrapper extractorWrapper;
        /* access modifiers changed from: private */
        public final long periodDurationUs;
        public final Representation representation;
        public final DashSegmentIndex segmentIndex;
        private final long segmentNumShift;

        RepresentationHolder(long periodDurationUs2, int trackType, Representation representation2, boolean enableEventMessageTrack, boolean enableCea608Track, TrackOutput playerEmsgTrackOutput) {
            this(periodDurationUs2, representation2, createExtractorWrapper(trackType, representation2, enableEventMessageTrack, enableCea608Track, playerEmsgTrackOutput), 0, representation2.getIndex());
        }

        private RepresentationHolder(long periodDurationUs2, Representation representation2, ChunkExtractorWrapper extractorWrapper2, long segmentNumShift2, DashSegmentIndex segmentIndex2) {
            this.periodDurationUs = periodDurationUs2;
            this.representation = representation2;
            this.segmentNumShift = segmentNumShift2;
            this.extractorWrapper = extractorWrapper2;
            this.segmentIndex = segmentIndex2;
        }

        /* access modifiers changed from: package-private */
        public RepresentationHolder copyWithNewRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            long newSegmentNumShift;
            long j = newPeriodDurationUs;
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            if (oldIndex == null) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, oldIndex);
            } else if (!oldIndex.isExplicit()) {
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, newIndex);
            } else {
                int oldIndexSegmentCount = oldIndex.getSegmentCount(j);
                if (oldIndexSegmentCount == 0) {
                    return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, this.segmentNumShift, newIndex);
                }
                long oldIndexLastSegmentNum = (oldIndex.getFirstSegmentNum() + ((long) oldIndexSegmentCount)) - 1;
                long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, j);
                long newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
                long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
                long newSegmentNumShift2 = this.segmentNumShift;
                if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                    newSegmentNumShift = newSegmentNumShift2 + ((oldIndexLastSegmentNum + 1) - newIndexFirstSegmentNum);
                } else if (oldIndexEndTimeUs >= newIndexStartTimeUs) {
                    newSegmentNumShift = newSegmentNumShift2 + (oldIndex.getSegmentNum(newIndexStartTimeUs, j) - newIndexFirstSegmentNum);
                } else {
                    throw new BehindLiveWindowException();
                }
                long j2 = newIndexStartTimeUs;
                long j3 = newIndexFirstSegmentNum;
                return new RepresentationHolder(newPeriodDurationUs, newRepresentation, this.extractorWrapper, newSegmentNumShift, newIndex);
            }
        }

        /* access modifiers changed from: package-private */
        public RepresentationHolder copyWithNewSegmentIndex(DashSegmentIndex segmentIndex2) {
            return new RepresentationHolder(this.periodDurationUs, this.representation, this.extractorWrapper, this.segmentNumShift, segmentIndex2);
        }

        public long getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getSegmentCount() {
            return this.segmentIndex.getSegmentCount(this.periodDurationUs);
        }

        public long getSegmentStartTimeUs(long segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(long segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public long getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(long segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }

        public long getFirstAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            DashManifest dashManifest = manifest;
            if (getSegmentCount() != -1 || dashManifest.timeShiftBufferDepthMs == C.TIME_UNSET) {
                return getFirstSegmentNum();
            }
            return Math.max(getFirstSegmentNum(), getSegmentNum(((nowUnixTimeUs - C.msToUs(dashManifest.availabilityStartTimeMs)) - C.msToUs(manifest.getPeriod(periodIndex).startMs)) - C.msToUs(dashManifest.timeShiftBufferDepthMs)));
        }

        public long getLastAvailableSegmentNum(DashManifest manifest, int periodIndex, long nowUnixTimeUs) {
            int availableSegmentCount = getSegmentCount();
            if (availableSegmentCount == -1) {
                return getSegmentNum((nowUnixTimeUs - C.msToUs(manifest.availabilityStartTimeMs)) - C.msToUs(manifest.getPeriod(periodIndex).startMs)) - 1;
            }
            return (getFirstSegmentNum() + ((long) availableSegmentCount)) - 1;
        }

        private static boolean mimeTypeIsWebm(String mimeType) {
            return mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM) || mimeType.startsWith(MimeTypes.APPLICATION_WEBM);
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
        }

        private static ChunkExtractorWrapper createExtractorWrapper(int trackType, Representation representation2, boolean enableEventMessageTrack, boolean enableCea608Track, TrackOutput playerEmsgTrackOutput) {
            Extractor extractor;
            List<Format> closedCaptionFormats;
            String containerMimeType = representation2.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                return null;
            }
            if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                extractor = new RawCcExtractor(representation2.format);
            } else if (mimeTypeIsWebm(containerMimeType)) {
                extractor = new MatroskaExtractor(1);
            } else {
                int flags = 0;
                if (enableEventMessageTrack) {
                    flags = 0 | 4;
                }
                if (enableCea608Track) {
                    closedCaptionFormats = Collections.singletonList(Format.createTextSampleFormat((String) null, MimeTypes.APPLICATION_CEA608, 0, (String) null));
                } else {
                    closedCaptionFormats = Collections.emptyList();
                }
                extractor = new FragmentedMp4Extractor(flags, (TimestampAdjuster) null, (Track) null, (DrmInitData) null, closedCaptionFormats, playerEmsgTrackOutput);
            }
            return new ChunkExtractorWrapper(extractor, trackType, representation2.format);
        }
    }
}
