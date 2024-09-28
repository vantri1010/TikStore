package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.BaseMediaChunkIterator;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.chunk.DataChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.BaseTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

class HlsChunkSource {
    private final DataSource encryptionDataSource;
    private byte[] encryptionIv;
    private String encryptionIvString;
    private byte[] encryptionKey;
    private Uri encryptionKeyUri;
    private HlsMasterPlaylist.HlsUrl expectedPlaylistUrl;
    private final HlsExtractorFactory extractorFactory;
    private IOException fatalError;
    private boolean independentSegments;
    private boolean isTimestampMaster;
    private long liveEdgeInPeriodTimeUs = C.TIME_UNSET;
    private final DataSource mediaDataSource;
    private final List<Format> muxedCaptionFormats;
    private final HlsPlaylistTracker playlistTracker;
    private byte[] scratchSpace;
    private boolean seenExpectedPlaylistError;
    private final TimestampAdjusterProvider timestampAdjusterProvider;
    private final TrackGroup trackGroup;
    private TrackSelection trackSelection;
    private final HlsMasterPlaylist.HlsUrl[] variants;

    public static final class HlsChunkHolder {
        public Chunk chunk;
        public boolean endOfStream;
        public HlsMasterPlaylist.HlsUrl playlist;

        public HlsChunkHolder() {
            clear();
        }

        public void clear() {
            this.chunk = null;
            this.endOfStream = false;
            this.playlist = null;
        }
    }

    public HlsChunkSource(HlsExtractorFactory extractorFactory2, HlsPlaylistTracker playlistTracker2, HlsMasterPlaylist.HlsUrl[] variants2, HlsDataSourceFactory dataSourceFactory, TransferListener mediaTransferListener, TimestampAdjusterProvider timestampAdjusterProvider2, List<Format> muxedCaptionFormats2) {
        this.extractorFactory = extractorFactory2;
        this.playlistTracker = playlistTracker2;
        this.variants = variants2;
        this.timestampAdjusterProvider = timestampAdjusterProvider2;
        this.muxedCaptionFormats = muxedCaptionFormats2;
        Format[] variantFormats = new Format[variants2.length];
        int[] initialTrackSelection = new int[variants2.length];
        for (int i = 0; i < variants2.length; i++) {
            variantFormats[i] = variants2[i].format;
            initialTrackSelection[i] = i;
        }
        DataSource createDataSource = dataSourceFactory.createDataSource(1);
        this.mediaDataSource = createDataSource;
        if (mediaTransferListener != null) {
            createDataSource.addTransferListener(mediaTransferListener);
        }
        this.encryptionDataSource = dataSourceFactory.createDataSource(3);
        TrackGroup trackGroup2 = new TrackGroup(variantFormats);
        this.trackGroup = trackGroup2;
        this.trackSelection = new InitializationTrackSelection(trackGroup2, initialTrackSelection);
    }

    public void maybeThrowError() throws IOException {
        IOException iOException = this.fatalError;
        if (iOException == null) {
            HlsMasterPlaylist.HlsUrl hlsUrl = this.expectedPlaylistUrl;
            if (hlsUrl != null && this.seenExpectedPlaylistError) {
                this.playlistTracker.maybeThrowPlaylistRefreshError(hlsUrl);
                return;
            }
            return;
        }
        throw iOException;
    }

    public TrackGroup getTrackGroup() {
        return this.trackGroup;
    }

    public void selectTracks(TrackSelection trackSelection2) {
        this.trackSelection = trackSelection2;
    }

    public TrackSelection getTrackSelection() {
        return this.trackSelection;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void setIsTimestampMaster(boolean isTimestampMaster2) {
        this.isTimestampMaster = isTimestampMaster2;
    }

    public void getNextChunk(long playbackPositionUs, long loadPositionUs, List<HlsMediaChunk> queue, HlsChunkHolder out) {
        HlsMediaChunk hlsMediaChunk;
        int i;
        long timeToLiveEdgeUs;
        long bufferedDurationUs;
        long startOfPlaylistInPeriodUs;
        int selectedVariantIndex;
        HlsMasterPlaylist.HlsUrl selectedUrl;
        long chunkMediaSequence;
        HlsMediaPlaylist.Segment segment;
        long j = loadPositionUs;
        HlsChunkHolder hlsChunkHolder = out;
        if (queue.isEmpty()) {
            List<HlsMediaChunk> list = queue;
            hlsMediaChunk = null;
        } else {
            hlsMediaChunk = queue.get(queue.size() - 1);
        }
        HlsMediaChunk previous = hlsMediaChunk;
        if (previous == null) {
            i = -1;
        } else {
            i = this.trackGroup.indexOf(previous.trackFormat);
        }
        int oldVariantIndex = i;
        long bufferedDurationUs2 = j - playbackPositionUs;
        long timeToLiveEdgeUs2 = resolveTimeToLiveEdgeUs(playbackPositionUs);
        if (previous == null || this.independentSegments) {
            timeToLiveEdgeUs = timeToLiveEdgeUs2;
            bufferedDurationUs = bufferedDurationUs2;
        } else {
            long subtractedDurationUs = previous.getDurationUs();
            long j2 = bufferedDurationUs2;
            bufferedDurationUs = Math.max(0, bufferedDurationUs2 - subtractedDurationUs);
            if (timeToLiveEdgeUs2 != C.TIME_UNSET) {
                timeToLiveEdgeUs = Math.max(0, timeToLiveEdgeUs2 - subtractedDurationUs);
            } else {
                timeToLiveEdgeUs = timeToLiveEdgeUs2;
            }
        }
        this.trackSelection.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, timeToLiveEdgeUs, queue, createMediaChunkIterators(previous, j));
        int selectedVariantIndex2 = this.trackSelection.getSelectedIndexInTrackGroup();
        boolean switchingVariant = oldVariantIndex != selectedVariantIndex2;
        HlsMasterPlaylist.HlsUrl selectedUrl2 = this.variants[selectedVariantIndex2];
        if (!this.playlistTracker.isSnapshotValid(selectedUrl2)) {
            hlsChunkHolder.playlist = selectedUrl2;
            this.seenExpectedPlaylistError &= this.expectedPlaylistUrl == selectedUrl2;
            this.expectedPlaylistUrl = selectedUrl2;
            return;
        }
        HlsMediaPlaylist mediaPlaylist = this.playlistTracker.getPlaylistSnapshot(selectedUrl2, true);
        this.independentSegments = mediaPlaylist.hasIndependentSegments;
        updateLiveEdgeTimeUs(mediaPlaylist);
        long startOfPlaylistInPeriodUs2 = mediaPlaylist.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
        int oldVariantIndex2 = oldVariantIndex;
        HlsMediaPlaylist mediaPlaylist2 = mediaPlaylist;
        HlsMasterPlaylist.HlsUrl selectedUrl3 = selectedUrl2;
        HlsMediaChunk previous2 = previous;
        long chunkMediaSequence2 = getChunkMediaSequence(previous, switchingVariant, mediaPlaylist, startOfPlaylistInPeriodUs2, loadPositionUs);
        if (chunkMediaSequence2 >= mediaPlaylist2.mediaSequence) {
            chunkMediaSequence = chunkMediaSequence2;
            selectedVariantIndex = selectedVariantIndex2;
            startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs2;
            selectedUrl = selectedUrl3;
        } else if (previous2 == null || !switchingVariant) {
            this.fatalError = new BehindLiveWindowException();
            return;
        } else {
            int selectedVariantIndex3 = oldVariantIndex2;
            HlsMasterPlaylist.HlsUrl selectedUrl4 = this.variants[selectedVariantIndex3];
            HlsMediaPlaylist mediaPlaylist3 = this.playlistTracker.getPlaylistSnapshot(selectedUrl4, true);
            mediaPlaylist2 = mediaPlaylist3;
            selectedVariantIndex = selectedVariantIndex3;
            startOfPlaylistInPeriodUs = mediaPlaylist3.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
            selectedUrl = selectedUrl4;
            chunkMediaSequence = previous2.getNextChunkIndex();
        }
        int chunkIndex = (int) (chunkMediaSequence - mediaPlaylist2.mediaSequence);
        if (chunkIndex < mediaPlaylist2.segments.size()) {
            this.seenExpectedPlaylistError = false;
            this.expectedPlaylistUrl = null;
            HlsMediaPlaylist.Segment segment2 = mediaPlaylist2.segments.get(chunkIndex);
            if (segment2.fullSegmentEncryptionKeyUri != null) {
                Uri keyUri = UriUtil.resolveToUri(mediaPlaylist2.baseUri, segment2.fullSegmentEncryptionKeyUri);
                if (!keyUri.equals(this.encryptionKeyUri)) {
                    Uri uri = keyUri;
                    HlsMediaPlaylist.Segment segment3 = segment2;
                    int i2 = chunkIndex;
                    hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment2.encryptionIV, selectedVariantIndex, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
                    return;
                }
                Uri keyUri2 = keyUri;
                segment = segment2;
                int i3 = chunkIndex;
                if (!Util.areEqual(segment.encryptionIV, this.encryptionIvString)) {
                    setEncryptionData(keyUri2, segment.encryptionIV, this.encryptionKey);
                }
            } else {
                segment = segment2;
                int i4 = chunkIndex;
                clearEncryptionData();
            }
            DataSpec initDataSpec = null;
            HlsMediaPlaylist.Segment initSegment = segment.initializationSegment;
            if (initSegment != null) {
                long j3 = bufferedDurationUs;
                initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist2.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, (String) null);
            }
            long segmentStartTimeInPeriodUs = startOfPlaylistInPeriodUs + segment.relativeStartTimeUs;
            int discontinuitySequence = mediaPlaylist2.discontinuitySequence + segment.relativeDiscontinuitySequence;
            TimestampAdjuster timestampAdjuster = this.timestampAdjusterProvider.getAdjuster(discontinuitySequence);
            TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
            int i5 = discontinuitySequence;
            HlsMediaPlaylist hlsMediaPlaylist = mediaPlaylist2;
            hlsChunkHolder.chunk = new HlsMediaChunk(this.extractorFactory, this.mediaDataSource, new DataSpec(UriUtil.resolveToUri(mediaPlaylist2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, (String) null), initDataSpec, selectedUrl, this.muxedCaptionFormats, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), segmentStartTimeInPeriodUs, segmentStartTimeInPeriodUs + segment.durationUs, chunkMediaSequence, discontinuitySequence, segment.hasGapTag, this.isTimestampMaster, timestampAdjuster, previous2, segment.drmInitData, this.encryptionKey, this.encryptionIv);
        } else if (mediaPlaylist2.hasEndTag) {
            hlsChunkHolder.endOfStream = true;
        } else {
            boolean z = true;
            hlsChunkHolder.playlist = selectedUrl;
            boolean z2 = this.seenExpectedPlaylistError;
            if (this.expectedPlaylistUrl != selectedUrl) {
                z = false;
            }
            this.seenExpectedPlaylistError = z & z2;
            this.expectedPlaylistUrl = selectedUrl;
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean maybeBlacklistTrack(Chunk chunk, long blacklistDurationMs) {
        TrackSelection trackSelection2 = this.trackSelection;
        return trackSelection2.blacklist(trackSelection2.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), blacklistDurationMs);
    }

    public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl url, long blacklistDurationMs) {
        int trackSelectionIndex;
        int trackGroupIndex = this.trackGroup.indexOf(url.format);
        if (trackGroupIndex == -1 || (trackSelectionIndex = this.trackSelection.indexOf(trackGroupIndex)) == -1) {
            return true;
        }
        this.seenExpectedPlaylistError |= this.expectedPlaylistUrl == url;
        if (blacklistDurationMs == C.TIME_UNSET || this.trackSelection.blacklist(trackSelectionIndex, blacklistDurationMs)) {
            return true;
        }
        return false;
    }

    public MediaChunkIterator[] createMediaChunkIterators(HlsMediaChunk previous, long loadPositionUs) {
        int i;
        HlsChunkSource hlsChunkSource = this;
        HlsMediaChunk hlsMediaChunk = previous;
        if (hlsMediaChunk == null) {
            i = -1;
        } else {
            i = hlsChunkSource.trackGroup.indexOf(hlsMediaChunk.trackFormat);
        }
        int oldVariantIndex = i;
        MediaChunkIterator[] chunkIterators = new MediaChunkIterator[hlsChunkSource.trackSelection.length()];
        int i2 = 0;
        while (i2 < chunkIterators.length) {
            int variantIndex = hlsChunkSource.trackSelection.getIndexInTrackGroup(i2);
            HlsMasterPlaylist.HlsUrl variantUrl = hlsChunkSource.variants[variantIndex];
            if (!hlsChunkSource.playlistTracker.isSnapshotValid(variantUrl)) {
                chunkIterators[i2] = MediaChunkIterator.EMPTY;
            } else {
                HlsMediaPlaylist playlist = hlsChunkSource.playlistTracker.getPlaylistSnapshot(variantUrl, false);
                long startOfPlaylistInPeriodUs = playlist.startTimeUs - hlsChunkSource.playlistTracker.getInitialStartTimeUs();
                long startOfPlaylistInPeriodUs2 = startOfPlaylistInPeriodUs;
                long chunkMediaSequence = getChunkMediaSequence(previous, variantIndex != oldVariantIndex, playlist, startOfPlaylistInPeriodUs, loadPositionUs);
                if (chunkMediaSequence < playlist.mediaSequence) {
                    chunkIterators[i2] = MediaChunkIterator.EMPTY;
                } else {
                    chunkIterators[i2] = new HlsMediaPlaylistSegmentIterator(playlist, startOfPlaylistInPeriodUs2, (int) (chunkMediaSequence - playlist.mediaSequence));
                }
            }
            i2++;
            hlsChunkSource = this;
            HlsMediaChunk hlsMediaChunk2 = previous;
        }
        return chunkIterators;
    }

    private long getChunkMediaSequence(HlsMediaChunk previous, boolean switchingVariant, HlsMediaPlaylist mediaPlaylist, long startOfPlaylistInPeriodUs, long loadPositionUs) {
        HlsMediaChunk hlsMediaChunk = previous;
        HlsMediaPlaylist hlsMediaPlaylist = mediaPlaylist;
        if (hlsMediaChunk != null && !switchingVariant) {
            return previous.getNextChunkIndex();
        }
        long endOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs + hlsMediaPlaylist.durationUs;
        long targetPositionInPeriodUs = (hlsMediaChunk == null || this.independentSegments) ? loadPositionUs : hlsMediaChunk.startTimeUs;
        if (!hlsMediaPlaylist.hasEndTag && targetPositionInPeriodUs >= endOfPlaylistInPeriodUs) {
            return hlsMediaPlaylist.mediaSequence + ((long) hlsMediaPlaylist.segments.size());
        }
        return ((long) Util.binarySearchFloor(hlsMediaPlaylist.segments, Long.valueOf(targetPositionInPeriodUs - startOfPlaylistInPeriodUs), true, !this.playlistTracker.isLive() || hlsMediaChunk == null)) + hlsMediaPlaylist.mediaSequence;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (this.liveEdgeInPeriodTimeUs != C.TIME_UNSET) {
            return this.liveEdgeInPeriodTimeUs - playbackPositionUs;
        }
        return C.TIME_UNSET;
    }

    private void updateLiveEdgeTimeUs(HlsMediaPlaylist mediaPlaylist) {
        long j;
        if (mediaPlaylist.hasEndTag) {
            j = C.TIME_UNSET;
        } else {
            j = mediaPlaylist.getEndTimeUs() - this.playlistTracker.getInitialStartTimeUs();
        }
        this.liveEdgeInPeriodTimeUs = j;
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri keyUri, String iv, int variantIndex, int trackSelectionReason, Object trackSelectionData) {
        return new EncryptionKeyChunk(this.encryptionDataSource, new DataSpec(keyUri, 0, -1, (String) null, 1), this.variants[variantIndex].format, trackSelectionReason, trackSelectionData, this.scratchSpace, iv);
    }

    private void setEncryptionData(Uri keyUri, String iv, byte[] secretKey) {
        String trimmedIv;
        if (Util.toLowerInvariant(iv).startsWith("0x")) {
            trimmedIv = iv.substring(2);
        } else {
            trimmedIv = iv;
        }
        byte[] ivData = new BigInteger(trimmedIv, 16).toByteArray();
        byte[] ivDataWithPadding = new byte[16];
        int offset = ivData.length > 16 ? ivData.length - 16 : 0;
        System.arraycopy(ivData, offset, ivDataWithPadding, (ivDataWithPadding.length - ivData.length) + offset, ivData.length - offset);
        this.encryptionKeyUri = keyUri;
        this.encryptionKey = secretKey;
        this.encryptionIvString = iv;
        this.encryptionIv = ivDataWithPadding;
    }

    private void clearEncryptionData() {
        this.encryptionKeyUri = null;
        this.encryptionKey = null;
        this.encryptionIvString = null;
        this.encryptionIv = null;
    }

    private static final class InitializationTrackSelection extends BaseTrackSelection {
        private int selectedIndex;

        public InitializationTrackSelection(TrackGroup group, int[] tracks) {
            super(group, tracks);
            this.selectedIndex = indexOf(group.getFormat(0));
        }

        public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIterators) {
            long nowMs = SystemClock.elapsedRealtime();
            if (isBlacklisted(this.selectedIndex, nowMs)) {
                for (int i = this.length - 1; i >= 0; i--) {
                    if (!isBlacklisted(i, nowMs)) {
                        this.selectedIndex = i;
                        return;
                    }
                }
                throw new IllegalStateException();
            }
        }

        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        public int getSelectionReason() {
            return 0;
        }

        public Object getSelectionData() {
            return null;
        }
    }

    private static final class EncryptionKeyChunk extends DataChunk {
        public final String iv;
        private byte[] result;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] scratchSpace, String iv2) {
            super(dataSource, dataSpec, 3, trackFormat, trackSelectionReason, trackSelectionData, scratchSpace);
            this.iv = iv2;
        }

        /* access modifiers changed from: protected */
        public void consume(byte[] data, int limit) throws IOException {
            this.result = Arrays.copyOf(data, limit);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    private static final class HlsMediaPlaylistSegmentIterator extends BaseMediaChunkIterator {
        private final HlsMediaPlaylist playlist;
        private final long startOfPlaylistInPeriodUs;

        public HlsMediaPlaylistSegmentIterator(HlsMediaPlaylist playlist2, long startOfPlaylistInPeriodUs2, int chunkIndex) {
            super((long) chunkIndex, (long) (playlist2.segments.size() - 1));
            this.playlist = playlist2;
            this.startOfPlaylistInPeriodUs = startOfPlaylistInPeriodUs2;
        }

        public DataSpec getDataSpec() {
            checkInBounds();
            HlsMediaPlaylist.Segment segment = this.playlist.segments.get((int) getCurrentIndex());
            return new DataSpec(UriUtil.resolveToUri(this.playlist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, (String) null);
        }

        public long getChunkStartTimeUs() {
            checkInBounds();
            return this.startOfPlaylistInPeriodUs + this.playlist.segments.get((int) getCurrentIndex()).relativeStartTimeUs;
        }

        public long getChunkEndTimeUs() {
            checkInBounds();
            HlsMediaPlaylist.Segment segment = this.playlist.segments.get((int) getCurrentIndex());
            return segment.durationUs + this.startOfPlaylistInPeriodUs + segment.relativeStartTimeUs;
        }
    }
}
