package com.google.android.exoplayer2.source.hls;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class HlsMediaChunk extends MediaChunk {
    public static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
    private static final AtomicInteger uidSource = new AtomicInteger();
    public final int discontinuitySequenceNumber;
    private final DrmInitData drmInitData;
    private Extractor extractor;
    private final HlsExtractorFactory extractorFactory;
    private final boolean hasGapTag;
    public final HlsMasterPlaylist.HlsUrl hlsUrl;
    private final ParsableByteArray id3Data;
    private final Id3Decoder id3Decoder;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private boolean initLoadCompleted;
    private int initSegmentBytesLoaded;
    private final boolean isEncrypted;
    private final boolean isMasterTimestampSource;
    private volatile boolean loadCanceled;
    private boolean loadCompleted;
    private final List<Format> muxedCaptionFormats;
    private int nextLoadPosition;
    private HlsSampleStreamWrapper output;
    private final Extractor previousExtractor;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsMediaChunk(com.google.android.exoplayer2.source.hls.HlsExtractorFactory r17, com.google.android.exoplayer2.upstream.DataSource r18, com.google.android.exoplayer2.upstream.DataSpec r19, com.google.android.exoplayer2.upstream.DataSpec r20, com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl r21, java.util.List<com.google.android.exoplayer2.Format> r22, int r23, java.lang.Object r24, long r25, long r27, long r29, int r31, boolean r32, boolean r33, com.google.android.exoplayer2.util.TimestampAdjuster r34, com.google.android.exoplayer2.source.hls.HlsMediaChunk r35, com.google.android.exoplayer2.drm.DrmInitData r36, byte[] r37, byte[] r38) {
        /*
            r16 = this;
            r12 = r16
            r13 = r18
            r14 = r21
            r15 = r31
            r10 = r35
            r11 = r37
            r8 = r38
            com.google.android.exoplayer2.upstream.DataSource r1 = buildDataSource(r13, r11, r8)
            com.google.android.exoplayer2.Format r3 = r14.format
            r0 = r16
            r2 = r19
            r4 = r23
            r5 = r24
            r6 = r25
            r8 = r27
            r13 = r10
            r10 = r29
            r0.<init>(r1, r2, r3, r4, r5, r6, r8, r10)
            r12.discontinuitySequenceNumber = r15
            r0 = r20
            r12.initDataSpec = r0
            r12.hlsUrl = r14
            r1 = r33
            r12.isMasterTimestampSource = r1
            r2 = r34
            r12.timestampAdjuster = r2
            r3 = 1
            r4 = 0
            if (r37 == 0) goto L_0x003c
            r5 = 1
            goto L_0x003d
        L_0x003c:
            r5 = 0
        L_0x003d:
            r12.isEncrypted = r5
            r5 = r32
            r12.hasGapTag = r5
            r6 = r17
            r12.extractorFactory = r6
            r7 = r22
            r12.muxedCaptionFormats = r7
            r8 = r36
            r12.drmInitData = r8
            r9 = 0
            if (r13 == 0) goto L_0x0073
            com.google.android.exoplayer2.metadata.id3.Id3Decoder r10 = r13.id3Decoder
            r12.id3Decoder = r10
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r13.id3Data
            r12.id3Data = r10
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r10 = r13.hlsUrl
            if (r10 != r14) goto L_0x0064
            boolean r10 = r13.loadCompleted
            if (r10 != 0) goto L_0x0063
            goto L_0x0064
        L_0x0063:
            r3 = 0
        L_0x0064:
            r12.shouldSpliceIn = r3
            int r4 = r13.discontinuitySequenceNumber
            if (r4 != r15) goto L_0x0070
            if (r3 == 0) goto L_0x006d
            goto L_0x0070
        L_0x006d:
            com.google.android.exoplayer2.extractor.Extractor r3 = r13.extractor
            goto L_0x0071
        L_0x0070:
            r3 = 0
        L_0x0071:
            r9 = r3
            goto L_0x0085
        L_0x0073:
            com.google.android.exoplayer2.metadata.id3.Id3Decoder r3 = new com.google.android.exoplayer2.metadata.id3.Id3Decoder
            r3.<init>()
            r12.id3Decoder = r3
            com.google.android.exoplayer2.util.ParsableByteArray r3 = new com.google.android.exoplayer2.util.ParsableByteArray
            r10 = 10
            r3.<init>((int) r10)
            r12.id3Data = r3
            r12.shouldSpliceIn = r4
        L_0x0085:
            r12.previousExtractor = r9
            r3 = r18
            r12.initDataSource = r3
            java.util.concurrent.atomic.AtomicInteger r4 = uidSource
            int r4 = r4.getAndIncrement()
            r12.uid = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaChunk.<init>(com.google.android.exoplayer2.source.hls.HlsExtractorFactory, com.google.android.exoplayer2.upstream.DataSource, com.google.android.exoplayer2.upstream.DataSpec, com.google.android.exoplayer2.upstream.DataSpec, com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl, java.util.List, int, java.lang.Object, long, long, long, int, boolean, boolean, com.google.android.exoplayer2.util.TimestampAdjuster, com.google.android.exoplayer2.source.hls.HlsMediaChunk, com.google.android.exoplayer2.drm.DrmInitData, byte[], byte[]):void");
    }

    public void init(HlsSampleStreamWrapper output2) {
        this.output = output2;
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public void load() throws IOException, InterruptedException {
        maybeLoadInitData();
        if (!this.loadCanceled) {
            if (!this.hasGapTag) {
                loadMedia();
            }
            this.loadCompleted = true;
        }
    }

    private void maybeLoadInitData() throws IOException, InterruptedException {
        DataSpec dataSpec;
        DefaultExtractorInput input;
        if (!this.initLoadCompleted && (dataSpec = this.initDataSpec) != null) {
            try {
                input = prepareExtraction(this.initDataSource, dataSpec.subrange((long) this.initSegmentBytesLoaded));
                int result = 0;
                while (result == 0) {
                    if (this.loadCanceled) {
                        break;
                    }
                    result = this.extractor.read(input, (PositionHolder) null);
                }
                this.initSegmentBytesLoaded = (int) (input.getPosition() - this.initDataSpec.absoluteStreamPosition);
                Util.closeQuietly(this.initDataSource);
                this.initLoadCompleted = true;
            } catch (Throwable th) {
                Util.closeQuietly(this.initDataSource);
                throw th;
            }
        }
    }

    private void loadMedia() throws IOException, InterruptedException {
        boolean skipLoadedBytes;
        DataSpec loadDataSpec;
        ExtractorInput input;
        if (this.isEncrypted) {
            loadDataSpec = this.dataSpec;
            skipLoadedBytes = this.nextLoadPosition != 0;
        } else {
            loadDataSpec = this.dataSpec.subrange((long) this.nextLoadPosition);
            skipLoadedBytes = false;
        }
        if (!this.isMasterTimestampSource) {
            this.timestampAdjuster.waitUntilInitialized();
        } else if (this.timestampAdjuster.getFirstSampleTimestampUs() == Long.MAX_VALUE) {
            this.timestampAdjuster.setFirstSampleTimestampUs(this.startTimeUs);
        }
        try {
            input = prepareExtraction(this.dataSource, loadDataSpec);
            if (skipLoadedBytes) {
                input.skipFully(this.nextLoadPosition);
            }
            int result = 0;
            while (result == 0) {
                if (this.loadCanceled) {
                    break;
                }
                result = this.extractor.read(input, (PositionHolder) null);
            }
            this.nextLoadPosition = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            Util.closeQuietly((DataSource) this.dataSource);
        } catch (Throwable th) {
            Util.closeQuietly((DataSource) this.dataSource);
            throw th;
        }
    }

    private DefaultExtractorInput prepareExtraction(DataSource dataSource, DataSpec dataSpec) throws IOException, InterruptedException {
        DataSpec dataSpec2 = dataSpec;
        DefaultExtractorInput extractorInput = new DefaultExtractorInput(dataSource, dataSpec2.absoluteStreamPosition, dataSource.open(dataSpec));
        if (this.extractor == null) {
            long id3Timestamp = peekId3PrivTimestamp(extractorInput);
            extractorInput.resetPeekPosition();
            Pair<Extractor, Boolean> extractorData = this.extractorFactory.createExtractor(this.previousExtractor, dataSpec2.uri, this.trackFormat, this.muxedCaptionFormats, this.drmInitData, this.timestampAdjuster, dataSource.getResponseHeaders(), extractorInput);
            Extractor extractor2 = (Extractor) extractorData.first;
            this.extractor = extractor2;
            boolean z = true;
            boolean reusingExtractor = extractor2 == this.previousExtractor;
            if (((Boolean) extractorData.second).booleanValue()) {
                this.output.setSampleOffsetUs(id3Timestamp != C.TIME_UNSET ? this.timestampAdjuster.adjustTsTimestamp(id3Timestamp) : this.startTimeUs);
            }
            if (!reusingExtractor || this.initDataSpec == null) {
                z = false;
            }
            this.initLoadCompleted = z;
            this.output.init(this.uid, this.shouldSpliceIn, reusingExtractor);
            if (!reusingExtractor) {
                this.extractor.init(this.output);
            }
        }
        return extractorInput;
    }

    private long peekId3PrivTimestamp(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        try {
            input.peekFully(this.id3Data.data, 0, 10);
            this.id3Data.reset(10);
            if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                return C.TIME_UNSET;
            }
            this.id3Data.skipBytes(3);
            int id3Size = this.id3Data.readSynchSafeInt();
            int requiredCapacity = id3Size + 10;
            if (requiredCapacity > this.id3Data.capacity()) {
                byte[] data = this.id3Data.data;
                this.id3Data.reset(requiredCapacity);
                System.arraycopy(data, 0, this.id3Data.data, 0, 10);
            }
            input.peekFully(this.id3Data.data, 10, id3Size);
            Metadata metadata = this.id3Decoder.decode(this.id3Data.data, id3Size);
            if (metadata == null) {
                return C.TIME_UNSET;
            }
            int metadataLength = metadata.length();
            for (int i = 0; i < metadataLength; i++) {
                Metadata.Entry frame = metadata.get(i);
                if (frame instanceof PrivFrame) {
                    PrivFrame privFrame = (PrivFrame) frame;
                    if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                        System.arraycopy(privFrame.privateData, 0, this.id3Data.data, 0, 8);
                        this.id3Data.reset(8);
                        return this.id3Data.readLong() & 8589934591L;
                    }
                }
            }
            return C.TIME_UNSET;
        } catch (EOFException e) {
            return C.TIME_UNSET;
        }
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] fullSegmentEncryptionKey, byte[] encryptionIv) {
        if (fullSegmentEncryptionKey != null) {
            return new Aes128DataSource(dataSource, fullSegmentEncryptionKey, encryptionIv);
        }
        return dataSource;
    }
}
