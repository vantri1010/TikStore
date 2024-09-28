package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class DefaultMp4SampleList extends AbstractList<Sample> {
    private static final long MAX_MAP_SIZE = 268435456;
    ByteBuffer[][] cache = null;
    int[] chunkNumsStartSampleNum;
    long[] chunkOffsets;
    long[] chunkSizes;
    int lastChunk = 0;
    long[][] sampleOffsetsWithinChunks;
    SampleSizeBox ssb;
    Container topLevel;
    TrackBox trackBox = null;

    public DefaultMp4SampleList(long track, Container topLevel2) {
        int currentChunkNo;
        long j = track;
        Container container = topLevel2;
        this.topLevel = container;
        MovieBox movieBox = container.getBoxes(MovieBox.class).get(0);
        List<TrackBox> trackBoxes = movieBox.getBoxes(TrackBox.class);
        for (TrackBox tb : trackBoxes) {
            MovieBox movieBox2 = movieBox;
            List<TrackBox> trackBoxes2 = trackBoxes;
            long j2 = j;
            if (tb.getTrackHeaderBox().getTrackId() == j2) {
                this.trackBox = tb;
                j = j2;
                movieBox = movieBox2;
                trackBoxes = trackBoxes2;
                Container container2 = topLevel2;
            } else {
                j = j2;
                movieBox = movieBox2;
                trackBoxes = trackBoxes2;
                Container container3 = topLevel2;
            }
        }
        TrackBox trackBox2 = this.trackBox;
        if (trackBox2 != null) {
            long[] chunkOffsets2 = trackBox2.getSampleTableBox().getChunkOffsetBox().getChunkOffsets();
            this.chunkOffsets = chunkOffsets2;
            this.chunkSizes = new long[chunkOffsets2.length];
            this.cache = new ByteBuffer[chunkOffsets2.length][];
            this.sampleOffsetsWithinChunks = new long[chunkOffsets2.length][];
            this.ssb = this.trackBox.getSampleTableBox().getSampleSizeBox();
            List<SampleToChunkBox.Entry> s2chunkEntries = this.trackBox.getSampleTableBox().getSampleToChunkBox().getEntries();
            SampleToChunkBox.Entry[] entries = (SampleToChunkBox.Entry[]) s2chunkEntries.toArray(new SampleToChunkBox.Entry[s2chunkEntries.size()]);
            int nextSamplePerChunk = 0 + 1;
            SampleToChunkBox.Entry next = entries[0];
            int currentChunkNo2 = 0;
            int currentSamplePerChunk = 0;
            long nextFirstChunk = next.getFirstChunk();
            int nextSamplePerChunk2 = CastUtils.l2i(next.getSamplesPerChunk());
            int currentSampleNo = 1;
            int lastSampleNo = size();
            while (true) {
                currentChunkNo2++;
                MovieBox movieBox3 = movieBox;
                List<TrackBox> trackBoxes3 = trackBoxes;
                if (((long) currentChunkNo2) == nextFirstChunk) {
                    int currentSamplePerChunk2 = nextSamplePerChunk2;
                    if (entries.length > nextSamplePerChunk) {
                        SampleToChunkBox.Entry next2 = entries[nextSamplePerChunk];
                        nextSamplePerChunk2 = CastUtils.l2i(next2.getSamplesPerChunk());
                        nextFirstChunk = next2.getFirstChunk();
                        currentSamplePerChunk = currentSamplePerChunk2;
                        nextSamplePerChunk++;
                    } else {
                        nextSamplePerChunk2 = -1;
                        nextFirstChunk = Long.MAX_VALUE;
                        currentSamplePerChunk = currentSamplePerChunk2;
                    }
                }
                List<SampleToChunkBox.Entry> s2chunkEntries2 = s2chunkEntries;
                this.sampleOffsetsWithinChunks[currentChunkNo2 - 1] = new long[currentSamplePerChunk];
                int i = currentSampleNo + currentSamplePerChunk;
                currentSampleNo = i;
                if (i > lastSampleNo) {
                    break;
                }
                long j3 = track;
                movieBox = movieBox3;
                trackBoxes = trackBoxes3;
                s2chunkEntries = s2chunkEntries2;
            }
            this.chunkNumsStartSampleNum = new int[(currentChunkNo2 + 1)];
            int nextSamplePerChunk3 = 0 + 1;
            SampleToChunkBox.Entry next3 = entries[0];
            int currentChunkNo3 = 0;
            int currentSamplePerChunk3 = 0;
            long nextFirstChunk2 = next3.getFirstChunk();
            int nextSamplePerChunk4 = CastUtils.l2i(next3.getSamplesPerChunk());
            int currentSampleNo2 = 1;
            while (true) {
                currentChunkNo = currentChunkNo3 + 1;
                this.chunkNumsStartSampleNum[currentChunkNo3] = currentSampleNo2;
                if (((long) currentChunkNo) == nextFirstChunk2) {
                    int currentSamplePerChunk4 = nextSamplePerChunk4;
                    if (entries.length > nextSamplePerChunk3) {
                        next3 = entries[nextSamplePerChunk3];
                        nextSamplePerChunk4 = CastUtils.l2i(next3.getSamplesPerChunk());
                        nextFirstChunk2 = next3.getFirstChunk();
                        currentSamplePerChunk3 = currentSamplePerChunk4;
                        nextSamplePerChunk3++;
                    } else {
                        nextSamplePerChunk4 = -1;
                        nextFirstChunk2 = Long.MAX_VALUE;
                        currentSamplePerChunk3 = currentSamplePerChunk4;
                    }
                }
                int currentSamplePerChunk5 = currentSampleNo2 + currentSamplePerChunk3;
                currentSampleNo2 = currentSamplePerChunk5;
                if (currentSamplePerChunk5 > lastSampleNo) {
                    break;
                }
                SampleToChunkBox.Entry entry = next3;
                int i2 = nextSamplePerChunk3;
                long j4 = track;
                currentChunkNo3 = currentChunkNo;
            }
            this.chunkNumsStartSampleNum[currentChunkNo] = Integer.MAX_VALUE;
            int currentChunkNo4 = 0;
            long sampleSum = 0;
            int i3 = 1;
            while (true) {
                int lastSampleNo2 = lastSampleNo;
                SampleToChunkBox.Entry next4 = next3;
                int currentChunkNo5 = currentChunkNo4;
                if (((long) i3) <= this.ssb.getSampleCount()) {
                    currentChunkNo4 = currentChunkNo5;
                    while (i3 == this.chunkNumsStartSampleNum[currentChunkNo4]) {
                        int s2cIndex = nextSamplePerChunk3;
                        int i4 = lastSampleNo2;
                        currentChunkNo4++;
                        sampleSum = 0;
                    }
                    long[] jArr = this.chunkSizes;
                    int i5 = currentChunkNo4 - 1;
                    jArr[i5] = jArr[i5] + this.ssb.getSampleSizeAtIndex(i3 - 1);
                    this.sampleOffsetsWithinChunks[currentChunkNo4 - 1][i3 - this.chunkNumsStartSampleNum[currentChunkNo4 - 1]] = sampleSum;
                    sampleSum += this.ssb.getSampleSizeAtIndex(i3 - 1);
                    i3++;
                    next3 = next4;
                    nextSamplePerChunk3 = nextSamplePerChunk3;
                    lastSampleNo = lastSampleNo2;
                } else {
                    return;
                }
            }
        } else {
            throw new RuntimeException("This MP4 does not contain track " + track);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized int getChunkForSample(int index) {
        int sampleNum = index + 1;
        if (sampleNum >= this.chunkNumsStartSampleNum[this.lastChunk] && sampleNum < this.chunkNumsStartSampleNum[this.lastChunk + 1]) {
            return this.lastChunk;
        } else if (sampleNum < this.chunkNumsStartSampleNum[this.lastChunk]) {
            this.lastChunk = 0;
            while (this.chunkNumsStartSampleNum[this.lastChunk + 1] <= sampleNum) {
                this.lastChunk++;
            }
            return this.lastChunk;
        } else {
            this.lastChunk++;
            while (this.chunkNumsStartSampleNum[this.lastChunk + 1] <= sampleNum) {
                this.lastChunk++;
            }
            return this.lastChunk;
        }
    }

    public Sample get(int index) {
        long offsetWithInChunk;
        ByteBuffer[] chunkBuffers;
        ByteBuffer correctPartOfChunk;
        ByteBuffer[] chunkBuffers2;
        ByteBuffer[] chunkBuffers3;
        int i = index;
        if (((long) i) < this.ssb.getSampleCount()) {
            int chunkNumber = getChunkForSample(index);
            int chunkStartSample = this.chunkNumsStartSampleNum[chunkNumber] - 1;
            long chunkOffset = this.chunkOffsets[CastUtils.l2i((long) chunkNumber)];
            long[] sampleOffsetsWithinChunk = this.sampleOffsetsWithinChunks[CastUtils.l2i((long) chunkNumber)];
            long offsetWithInChunk2 = sampleOffsetsWithinChunk[i - chunkStartSample];
            ByteBuffer[] chunkBuffers4 = this.cache[CastUtils.l2i((long) chunkNumber)];
            if (chunkBuffers4 == null) {
                List<ByteBuffer> _chunkBuffers = new ArrayList<>();
                long currentStart = 0;
                int i2 = 0;
                while (i2 < sampleOffsetsWithinChunk.length) {
                    try {
                        long offsetWithInChunk3 = offsetWithInChunk2;
                        chunkBuffers2 = chunkBuffers4;
                        long chunkOffset2 = chunkOffset;
                        if ((sampleOffsetsWithinChunk[i2] + this.ssb.getSampleSizeAtIndex(i2 + chunkStartSample)) - currentStart > MAX_MAP_SIZE) {
                            _chunkBuffers.add(this.topLevel.getByteBuffer(chunkOffset2 + currentStart, sampleOffsetsWithinChunk[i2] - currentStart));
                            currentStart = sampleOffsetsWithinChunk[i2];
                        }
                        i2++;
                        offsetWithInChunk2 = offsetWithInChunk3;
                        chunkOffset = chunkOffset2;
                        chunkBuffers4 = chunkBuffers2;
                    } catch (IOException e) {
                        e = e;
                        long j = offsetWithInChunk2;
                        ByteBuffer[] byteBufferArr = chunkBuffers4;
                        long j2 = chunkOffset;
                        throw new IndexOutOfBoundsException(e.getMessage());
                    }
                }
                offsetWithInChunk = offsetWithInChunk2;
                long j3 = chunkOffset;
                try {
                    chunkBuffers2 = chunkBuffers4;
                } catch (IOException e2) {
                    e = e2;
                    ByteBuffer[] byteBufferArr2 = chunkBuffers4;
                    throw new IndexOutOfBoundsException(e.getMessage());
                }
                try {
                    _chunkBuffers.add(this.topLevel.getByteBuffer(chunkOffset + currentStart, (-currentStart) + sampleOffsetsWithinChunk[sampleOffsetsWithinChunk.length - 1] + this.ssb.getSampleSizeAtIndex((sampleOffsetsWithinChunk.length + chunkStartSample) - 1)));
                    chunkBuffers3 = (ByteBuffer[]) _chunkBuffers.toArray(new ByteBuffer[_chunkBuffers.size()]);
                } catch (IOException e3) {
                    e = e3;
                    ByteBuffer[] byteBufferArr3 = chunkBuffers2;
                    throw new IndexOutOfBoundsException(e.getMessage());
                }
                try {
                    this.cache[CastUtils.l2i((long) chunkNumber)] = chunkBuffers3;
                    chunkBuffers = chunkBuffers3;
                } catch (IOException e4) {
                    e = e4;
                    throw new IndexOutOfBoundsException(e.getMessage());
                }
            } else {
                offsetWithInChunk = offsetWithInChunk2;
                long j4 = chunkOffset;
                chunkBuffers = chunkBuffers4;
            }
            int length = chunkBuffers.length;
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    correctPartOfChunk = null;
                    break;
                }
                ByteBuffer chunkBuffer = chunkBuffers[i3];
                if (offsetWithInChunk < ((long) chunkBuffer.limit())) {
                    correctPartOfChunk = chunkBuffer;
                    break;
                }
                offsetWithInChunk -= (long) chunkBuffer.limit();
                i3++;
            }
            final ByteBuffer finalCorrectPartOfChunk = correctPartOfChunk;
            final long finalOffsetWithInChunk = offsetWithInChunk;
            final long sampleSizeAtIndex = this.ssb.getSampleSizeAtIndex(i);
            return new Sample() {
                public void writeTo(WritableByteChannel channel) throws IOException {
                    channel.write(asByteBuffer());
                }

                public long getSize() {
                    return sampleSizeAtIndex;
                }

                public ByteBuffer asByteBuffer() {
                    return (ByteBuffer) ((ByteBuffer) finalCorrectPartOfChunk.position(CastUtils.l2i(finalOffsetWithInChunk))).slice().limit(CastUtils.l2i(sampleSizeAtIndex));
                }

                public String toString() {
                    return "DefaultMp4Sample(size:" + sampleSizeAtIndex + SQLBuilder.PARENTHESES_RIGHT;
                }
            };
        }
        throw new IndexOutOfBoundsException();
    }

    public int size() {
        return CastUtils.l2i(this.trackBox.getSampleTableBox().getSampleSizeBox().getSampleCount());
    }
}
