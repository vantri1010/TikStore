package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

final class SampleMetadataQueue {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteFirstIndex;
    private int capacity = 1000;
    private TrackOutput.CryptoData[] cryptoDatas = new TrackOutput.CryptoData[1000];
    private int[] flags = new int[1000];
    private Format[] formats = new Format[1000];
    private boolean isLastSampleQueued;
    private long largestDiscardedTimestampUs = Long.MIN_VALUE;
    private long largestQueuedTimestampUs = Long.MIN_VALUE;
    private int length;
    private long[] offsets = new long[1000];
    private int readPosition;
    private int relativeFirstIndex;
    private int[] sizes = new int[1000];
    private int[] sourceIds = new int[1000];
    private long[] timesUs = new long[1000];
    private Format upstreamFormat;
    private boolean upstreamFormatRequired = true;
    private boolean upstreamKeyframeRequired = true;
    private int upstreamSourceId;

    public static final class SampleExtrasHolder {
        public TrackOutput.CryptoData cryptoData;
        public long offset;
        public int size;
    }

    public void reset(boolean resetUpstreamFormat) {
        this.length = 0;
        this.absoluteFirstIndex = 0;
        this.relativeFirstIndex = 0;
        this.readPosition = 0;
        this.upstreamKeyframeRequired = true;
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        this.isLastSampleQueued = false;
        if (resetUpstreamFormat) {
            this.upstreamFormat = null;
            this.upstreamFormatRequired = true;
        }
    }

    public int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public long discardUpstreamSamples(int discardFromIndex) {
        int discardCount = getWriteIndex() - discardFromIndex;
        boolean z = false;
        Assertions.checkArgument(discardCount >= 0 && discardCount <= this.length - this.readPosition);
        int i = this.length - discardCount;
        this.length = i;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(i));
        if (discardCount == 0 && this.isLastSampleQueued) {
            z = true;
        }
        this.isLastSampleQueued = z;
        int i2 = this.length;
        if (i2 == 0) {
            return 0;
        }
        int relativeLastWriteIndex = getRelativeIndex(i2 - 1);
        return this.offsets[relativeLastWriteIndex] + ((long) this.sizes[relativeLastWriteIndex]);
    }

    public void sourceId(int sourceId) {
        this.upstreamSourceId = sourceId;
    }

    public int getFirstIndex() {
        return this.absoluteFirstIndex;
    }

    public int getReadIndex() {
        return this.absoluteFirstIndex + this.readPosition;
    }

    public int peekSourceId() {
        return hasNextSample() ? this.sourceIds[getRelativeIndex(this.readPosition)] : this.upstreamSourceId;
    }

    public synchronized boolean hasNextSample() {
        return this.readPosition != this.length;
    }

    public synchronized Format getUpstreamFormat() {
        return this.upstreamFormatRequired ? null : this.upstreamFormat;
    }

    public synchronized long getLargestQueuedTimestampUs() {
        return this.largestQueuedTimestampUs;
    }

    public synchronized boolean isLastSampleQueued() {
        return this.isLastSampleQueued;
    }

    public synchronized long getFirstTimestampUs() {
        return this.length == 0 ? Long.MIN_VALUE : this.timesUs[this.relativeFirstIndex];
    }

    public synchronized void rewind() {
        this.readPosition = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0022, code lost:
        return -3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(com.google.android.exoplayer2.FormatHolder r7, com.google.android.exoplayer2.decoder.DecoderInputBuffer r8, boolean r9, boolean r10, com.google.android.exoplayer2.Format r11, com.google.android.exoplayer2.source.SampleMetadataQueue.SampleExtrasHolder r12) {
        /*
            r6 = this;
            monitor-enter(r6)
            boolean r0 = r6.hasNextSample()     // Catch:{ all -> 0x006f }
            r1 = -5
            r2 = -3
            r3 = -4
            if (r0 != 0) goto L_0x0029
            if (r10 != 0) goto L_0x0023
            boolean r0 = r6.isLastSampleQueued     // Catch:{ all -> 0x006f }
            if (r0 == 0) goto L_0x0011
            goto L_0x0023
        L_0x0011:
            com.google.android.exoplayer2.Format r0 = r6.upstreamFormat     // Catch:{ all -> 0x006f }
            if (r0 == 0) goto L_0x0021
            if (r9 != 0) goto L_0x001b
            com.google.android.exoplayer2.Format r0 = r6.upstreamFormat     // Catch:{ all -> 0x006f }
            if (r0 == r11) goto L_0x0021
        L_0x001b:
            com.google.android.exoplayer2.Format r0 = r6.upstreamFormat     // Catch:{ all -> 0x006f }
            r7.format = r0     // Catch:{ all -> 0x006f }
            monitor-exit(r6)
            return r1
        L_0x0021:
            monitor-exit(r6)
            return r2
        L_0x0023:
            r0 = 4
            r8.setFlags(r0)     // Catch:{ all -> 0x006f }
            monitor-exit(r6)
            return r3
        L_0x0029:
            int r0 = r6.readPosition     // Catch:{ all -> 0x006f }
            int r0 = r6.getRelativeIndex(r0)     // Catch:{ all -> 0x006f }
            if (r9 != 0) goto L_0x0067
            com.google.android.exoplayer2.Format[] r4 = r6.formats     // Catch:{ all -> 0x006f }
            r4 = r4[r0]     // Catch:{ all -> 0x006f }
            if (r4 == r11) goto L_0x0038
            goto L_0x0067
        L_0x0038:
            boolean r1 = r8.isFlagsOnly()     // Catch:{ all -> 0x006f }
            if (r1 == 0) goto L_0x0040
            monitor-exit(r6)
            return r2
        L_0x0040:
            long[] r1 = r6.timesUs     // Catch:{ all -> 0x006f }
            r4 = r1[r0]     // Catch:{ all -> 0x006f }
            r8.timeUs = r4     // Catch:{ all -> 0x006f }
            int[] r1 = r6.flags     // Catch:{ all -> 0x006f }
            r1 = r1[r0]     // Catch:{ all -> 0x006f }
            r8.setFlags(r1)     // Catch:{ all -> 0x006f }
            int[] r1 = r6.sizes     // Catch:{ all -> 0x006f }
            r1 = r1[r0]     // Catch:{ all -> 0x006f }
            r12.size = r1     // Catch:{ all -> 0x006f }
            long[] r1 = r6.offsets     // Catch:{ all -> 0x006f }
            r4 = r1[r0]     // Catch:{ all -> 0x006f }
            r12.offset = r4     // Catch:{ all -> 0x006f }
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData[] r1 = r6.cryptoDatas     // Catch:{ all -> 0x006f }
            r1 = r1[r0]     // Catch:{ all -> 0x006f }
            r12.cryptoData = r1     // Catch:{ all -> 0x006f }
            int r1 = r6.readPosition     // Catch:{ all -> 0x006f }
            int r1 = r1 + 1
            r6.readPosition = r1     // Catch:{ all -> 0x006f }
            monitor-exit(r6)
            return r3
        L_0x0067:
            com.google.android.exoplayer2.Format[] r2 = r6.formats     // Catch:{ all -> 0x006f }
            r2 = r2[r0]     // Catch:{ all -> 0x006f }
            r7.format = r2     // Catch:{ all -> 0x006f }
            monitor-exit(r6)
            return r1
        L_0x006f:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.read(com.google.android.exoplayer2.FormatHolder, com.google.android.exoplayer2.decoder.DecoderInputBuffer, boolean, boolean, com.google.android.exoplayer2.Format, com.google.android.exoplayer2.source.SampleMetadataQueue$SampleExtrasHolder):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0039, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int advanceTo(long r9, boolean r11, boolean r12) {
        /*
            r8 = this;
            monitor-enter(r8)
            int r0 = r8.readPosition     // Catch:{ all -> 0x003a }
            int r0 = r8.getRelativeIndex(r0)     // Catch:{ all -> 0x003a }
            boolean r1 = r8.hasNextSample()     // Catch:{ all -> 0x003a }
            r7 = -1
            if (r1 == 0) goto L_0x0038
            long[] r1 = r8.timesUs     // Catch:{ all -> 0x003a }
            r2 = r1[r0]     // Catch:{ all -> 0x003a }
            int r1 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0038
            long r1 = r8.largestQueuedTimestampUs     // Catch:{ all -> 0x003a }
            int r3 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r3 <= 0) goto L_0x001f
            if (r12 != 0) goto L_0x001f
            goto L_0x0038
        L_0x001f:
            int r1 = r8.length     // Catch:{ all -> 0x003a }
            int r2 = r8.readPosition     // Catch:{ all -> 0x003a }
            int r3 = r1 - r2
            r1 = r8
            r2 = r0
            r4 = r9
            r6 = r11
            int r1 = r1.findSampleBefore(r2, r3, r4, r6)     // Catch:{ all -> 0x003a }
            if (r1 != r7) goto L_0x0031
            monitor-exit(r8)
            return r7
        L_0x0031:
            int r2 = r8.readPosition     // Catch:{ all -> 0x003a }
            int r2 = r2 + r1
            r8.readPosition = r2     // Catch:{ all -> 0x003a }
            monitor-exit(r8)
            return r1
        L_0x0038:
            monitor-exit(r8)
            return r7
        L_0x003a:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.advanceTo(long, boolean, boolean):int");
    }

    public synchronized int advanceToEnd() {
        int skipCount;
        skipCount = this.length - this.readPosition;
        this.readPosition = this.length;
        return skipCount;
    }

    public synchronized boolean setReadPosition(int sampleIndex) {
        if (this.absoluteFirstIndex > sampleIndex || sampleIndex > this.absoluteFirstIndex + this.length) {
            return false;
        }
        this.readPosition = sampleIndex - this.absoluteFirstIndex;
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0038, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long discardTo(long r10, boolean r12, boolean r13) {
        /*
            r9 = this;
            monitor-enter(r9)
            int r0 = r9.length     // Catch:{ all -> 0x0039 }
            r1 = -1
            if (r0 == 0) goto L_0x0037
            long[] r0 = r9.timesUs     // Catch:{ all -> 0x0039 }
            int r3 = r9.relativeFirstIndex     // Catch:{ all -> 0x0039 }
            r3 = r0[r3]     // Catch:{ all -> 0x0039 }
            int r0 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r0 >= 0) goto L_0x0012
            goto L_0x0037
        L_0x0012:
            if (r13 == 0) goto L_0x0020
            int r0 = r9.readPosition     // Catch:{ all -> 0x0039 }
            int r3 = r9.length     // Catch:{ all -> 0x0039 }
            if (r0 == r3) goto L_0x0020
            int r0 = r9.readPosition     // Catch:{ all -> 0x0039 }
            int r0 = r0 + 1
            r5 = r0
            goto L_0x0023
        L_0x0020:
            int r0 = r9.length     // Catch:{ all -> 0x0039 }
            r5 = r0
        L_0x0023:
            int r4 = r9.relativeFirstIndex     // Catch:{ all -> 0x0039 }
            r3 = r9
            r6 = r10
            r8 = r12
            int r0 = r3.findSampleBefore(r4, r5, r6, r8)     // Catch:{ all -> 0x0039 }
            r3 = -1
            if (r0 != r3) goto L_0x0031
            monitor-exit(r9)
            return r1
        L_0x0031:
            long r1 = r9.discardSamples(r0)     // Catch:{ all -> 0x0039 }
            monitor-exit(r9)
            return r1
        L_0x0037:
            monitor-exit(r9)
            return r1
        L_0x0039:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.discardTo(long, boolean, boolean):long");
    }

    public synchronized long discardToRead() {
        if (this.readPosition == 0) {
            return -1;
        }
        return discardSamples(this.readPosition);
    }

    public synchronized long discardToEnd() {
        if (this.length == 0) {
            return -1;
        }
        return discardSamples(this.length);
    }

    public synchronized boolean format(Format format) {
        if (format == null) {
            this.upstreamFormatRequired = true;
            return false;
        }
        this.upstreamFormatRequired = false;
        if (Util.areEqual(format, this.upstreamFormat)) {
            return false;
        }
        this.upstreamFormat = format;
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00e3, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void commitSample(long r17, int r19, long r20, int r22, com.google.android.exoplayer2.extractor.TrackOutput.CryptoData r23) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            monitor-enter(r16)
            boolean r0 = r1.upstreamKeyframeRequired     // Catch:{ all -> 0x00e4 }
            r4 = 0
            if (r0 == 0) goto L_0x0012
            r0 = r19 & 1
            if (r0 != 0) goto L_0x0010
            monitor-exit(r16)
            return
        L_0x0010:
            r1.upstreamKeyframeRequired = r4     // Catch:{ all -> 0x00e4 }
        L_0x0012:
            boolean r0 = r1.upstreamFormatRequired     // Catch:{ all -> 0x00e4 }
            r5 = 1
            if (r0 != 0) goto L_0x0019
            r0 = 1
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            com.google.android.exoplayer2.util.Assertions.checkState(r0)     // Catch:{ all -> 0x00e4 }
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r19 & r0
            if (r0 == 0) goto L_0x0025
            r0 = 1
            goto L_0x0026
        L_0x0025:
            r0 = 0
        L_0x0026:
            r1.isLastSampleQueued = r0     // Catch:{ all -> 0x00e4 }
            long r6 = r1.largestQueuedTimestampUs     // Catch:{ all -> 0x00e4 }
            long r6 = java.lang.Math.max(r6, r2)     // Catch:{ all -> 0x00e4 }
            r1.largestQueuedTimestampUs = r6     // Catch:{ all -> 0x00e4 }
            int r0 = r1.length     // Catch:{ all -> 0x00e4 }
            int r0 = r1.getRelativeIndex(r0)     // Catch:{ all -> 0x00e4 }
            long[] r6 = r1.timesUs     // Catch:{ all -> 0x00e4 }
            r6[r0] = r2     // Catch:{ all -> 0x00e4 }
            long[] r6 = r1.offsets     // Catch:{ all -> 0x00e4 }
            r6[r0] = r20     // Catch:{ all -> 0x00e4 }
            int[] r6 = r1.sizes     // Catch:{ all -> 0x00e4 }
            r6[r0] = r22     // Catch:{ all -> 0x00e4 }
            int[] r6 = r1.flags     // Catch:{ all -> 0x00e4 }
            r6[r0] = r19     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData[] r6 = r1.cryptoDatas     // Catch:{ all -> 0x00e4 }
            r6[r0] = r23     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.Format[] r6 = r1.formats     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.Format r7 = r1.upstreamFormat     // Catch:{ all -> 0x00e4 }
            r6[r0] = r7     // Catch:{ all -> 0x00e4 }
            int[] r6 = r1.sourceIds     // Catch:{ all -> 0x00e4 }
            int r7 = r1.upstreamSourceId     // Catch:{ all -> 0x00e4 }
            r6[r0] = r7     // Catch:{ all -> 0x00e4 }
            int r6 = r1.length     // Catch:{ all -> 0x00e4 }
            int r6 = r6 + r5
            r1.length = r6     // Catch:{ all -> 0x00e4 }
            int r5 = r1.capacity     // Catch:{ all -> 0x00e4 }
            if (r6 != r5) goto L_0x00e2
            int r5 = r1.capacity     // Catch:{ all -> 0x00e4 }
            int r5 = r5 + 1000
            int[] r6 = new int[r5]     // Catch:{ all -> 0x00e4 }
            long[] r7 = new long[r5]     // Catch:{ all -> 0x00e4 }
            long[] r8 = new long[r5]     // Catch:{ all -> 0x00e4 }
            int[] r9 = new int[r5]     // Catch:{ all -> 0x00e4 }
            int[] r10 = new int[r5]     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData[] r11 = new com.google.android.exoplayer2.extractor.TrackOutput.CryptoData[r5]     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.Format[] r12 = new com.google.android.exoplayer2.Format[r5]     // Catch:{ all -> 0x00e4 }
            int r13 = r1.capacity     // Catch:{ all -> 0x00e4 }
            int r14 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            int r13 = r13 - r14
            long[] r14 = r1.offsets     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r7, r4, r13)     // Catch:{ all -> 0x00e4 }
            long[] r14 = r1.timesUs     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r8, r4, r13)     // Catch:{ all -> 0x00e4 }
            int[] r14 = r1.flags     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r9, r4, r13)     // Catch:{ all -> 0x00e4 }
            int[] r14 = r1.sizes     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r10, r4, r13)     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData[] r14 = r1.cryptoDatas     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r11, r4, r13)     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.Format[] r14 = r1.formats     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r12, r4, r13)     // Catch:{ all -> 0x00e4 }
            int[] r14 = r1.sourceIds     // Catch:{ all -> 0x00e4 }
            int r15 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r14, r15, r6, r4, r13)     // Catch:{ all -> 0x00e4 }
            int r14 = r1.relativeFirstIndex     // Catch:{ all -> 0x00e4 }
            long[] r15 = r1.offsets     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r7, r13, r14)     // Catch:{ all -> 0x00e4 }
            long[] r15 = r1.timesUs     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r8, r13, r14)     // Catch:{ all -> 0x00e4 }
            int[] r15 = r1.flags     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r9, r13, r14)     // Catch:{ all -> 0x00e4 }
            int[] r15 = r1.sizes     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r10, r13, r14)     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData[] r15 = r1.cryptoDatas     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r11, r13, r14)     // Catch:{ all -> 0x00e4 }
            com.google.android.exoplayer2.Format[] r15 = r1.formats     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r12, r13, r14)     // Catch:{ all -> 0x00e4 }
            int[] r15 = r1.sourceIds     // Catch:{ all -> 0x00e4 }
            java.lang.System.arraycopy(r15, r4, r6, r13, r14)     // Catch:{ all -> 0x00e4 }
            r1.offsets = r7     // Catch:{ all -> 0x00e4 }
            r1.timesUs = r8     // Catch:{ all -> 0x00e4 }
            r1.flags = r9     // Catch:{ all -> 0x00e4 }
            r1.sizes = r10     // Catch:{ all -> 0x00e4 }
            r1.cryptoDatas = r11     // Catch:{ all -> 0x00e4 }
            r1.formats = r12     // Catch:{ all -> 0x00e4 }
            r1.sourceIds = r6     // Catch:{ all -> 0x00e4 }
            r1.relativeFirstIndex = r4     // Catch:{ all -> 0x00e4 }
            int r4 = r1.capacity     // Catch:{ all -> 0x00e4 }
            r1.length = r4     // Catch:{ all -> 0x00e4 }
            r1.capacity = r5     // Catch:{ all -> 0x00e4 }
        L_0x00e2:
            monitor-exit(r16)
            return
        L_0x00e4:
            r0 = move-exception
            monitor-exit(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.commitSample(long, int, long, int, com.google.android.exoplayer2.extractor.TrackOutput$CryptoData):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000f, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean attemptSplice(long r9) {
        /*
            r8 = this;
            monitor-enter(r8)
            int r0 = r8.length     // Catch:{ all -> 0x004b }
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x0010
            long r3 = r8.largestDiscardedTimestampUs     // Catch:{ all -> 0x004b }
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x000e
            r1 = 1
        L_0x000e:
            monitor-exit(r8)
            return r1
        L_0x0010:
            long r3 = r8.largestDiscardedTimestampUs     // Catch:{ all -> 0x004b }
            int r0 = r8.readPosition     // Catch:{ all -> 0x004b }
            long r5 = r8.getLargestTimestamp(r0)     // Catch:{ all -> 0x004b }
            long r3 = java.lang.Math.max(r3, r5)     // Catch:{ all -> 0x004b }
            int r0 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x0022
            monitor-exit(r8)
            return r1
        L_0x0022:
            int r0 = r8.length     // Catch:{ all -> 0x004b }
            int r1 = r8.length     // Catch:{ all -> 0x004b }
            int r1 = r1 - r2
            int r1 = r8.getRelativeIndex(r1)     // Catch:{ all -> 0x004b }
        L_0x002b:
            int r5 = r8.readPosition     // Catch:{ all -> 0x004b }
            if (r0 <= r5) goto L_0x0043
            long[] r5 = r8.timesUs     // Catch:{ all -> 0x004b }
            r6 = r5[r1]     // Catch:{ all -> 0x004b }
            int r5 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r5 < 0) goto L_0x0043
            int r0 = r0 + -1
            int r1 = r1 + -1
            r5 = -1
            if (r1 != r5) goto L_0x002b
            int r5 = r8.capacity     // Catch:{ all -> 0x004b }
            int r1 = r5 + -1
            goto L_0x002b
        L_0x0043:
            int r5 = r8.absoluteFirstIndex     // Catch:{ all -> 0x004b }
            int r5 = r5 + r0
            r8.discardUpstreamSamples(r5)     // Catch:{ all -> 0x004b }
            monitor-exit(r8)
            return r2
        L_0x004b:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.SampleMetadataQueue.attemptSplice(long):boolean");
    }

    private int findSampleBefore(int relativeStartIndex, int length2, long timeUs, boolean keyframe) {
        int sampleCountToTarget = -1;
        int searchIndex = relativeStartIndex;
        for (int i = 0; i < length2 && this.timesUs[searchIndex] <= timeUs; i++) {
            if (!keyframe || (this.flags[searchIndex] & 1) != 0) {
                sampleCountToTarget = i;
            }
            searchIndex++;
            if (searchIndex == this.capacity) {
                searchIndex = 0;
            }
        }
        return sampleCountToTarget;
    }

    private long discardSamples(int discardCount) {
        this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(discardCount));
        this.length -= discardCount;
        this.absoluteFirstIndex += discardCount;
        int i = this.relativeFirstIndex + discardCount;
        this.relativeFirstIndex = i;
        int i2 = this.capacity;
        if (i >= i2) {
            this.relativeFirstIndex = i - i2;
        }
        int i3 = this.readPosition - discardCount;
        this.readPosition = i3;
        if (i3 < 0) {
            this.readPosition = 0;
        }
        if (this.length != 0) {
            return this.offsets[this.relativeFirstIndex];
        }
        int i4 = this.relativeFirstIndex;
        if (i4 == 0) {
            i4 = this.capacity;
        }
        int relativeLastDiscardIndex = i4 - 1;
        return this.offsets[relativeLastDiscardIndex] + ((long) this.sizes[relativeLastDiscardIndex]);
    }

    private long getLargestTimestamp(int length2) {
        if (length2 == 0) {
            return Long.MIN_VALUE;
        }
        long largestTimestampUs = Long.MIN_VALUE;
        int relativeSampleIndex = getRelativeIndex(length2 - 1);
        for (int i = 0; i < length2; i++) {
            largestTimestampUs = Math.max(largestTimestampUs, this.timesUs[relativeSampleIndex]);
            if ((this.flags[relativeSampleIndex] & 1) != 0) {
                break;
            }
            relativeSampleIndex--;
            if (relativeSampleIndex == -1) {
                relativeSampleIndex = this.capacity - 1;
            }
        }
        return largestTimestampUs;
    }

    private int getRelativeIndex(int offset) {
        int relativeIndex = this.relativeFirstIndex + offset;
        int i = this.capacity;
        return relativeIndex < i ? relativeIndex : relativeIndex - i;
    }
}
