package com.google.android.exoplayer2.extractor;

import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.util.Util;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.util.Arrays;

public final class ChunkIndex implements SeekMap {
    private final long durationUs;
    public final long[] durationsUs;
    public final int length;
    public final long[] offsets;
    public final int[] sizes;
    public final long[] timesUs;

    public ChunkIndex(int[] sizes2, long[] offsets2, long[] durationsUs2, long[] timesUs2) {
        this.sizes = sizes2;
        this.offsets = offsets2;
        this.durationsUs = durationsUs2;
        this.timesUs = timesUs2;
        int length2 = sizes2.length;
        this.length = length2;
        if (length2 > 0) {
            this.durationUs = durationsUs2[length2 - 1] + timesUs2[length2 - 1];
        } else {
            this.durationUs = 0;
        }
    }

    public int getChunkIndex(long timeUs) {
        return Util.binarySearchFloor(this.timesUs, timeUs, true, true);
    }

    public boolean isSeekable() {
        return true;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        int chunkIndex = getChunkIndex(timeUs);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[chunkIndex], this.offsets[chunkIndex]);
        if (seekPoint.timeUs >= timeUs || chunkIndex == this.length - 1) {
            return new SeekMap.SeekPoints(seekPoint);
        }
        return new SeekMap.SeekPoints(seekPoint, new SeekPoint(this.timesUs[chunkIndex + 1], this.offsets[chunkIndex + 1]));
    }

    public String toString() {
        return "ChunkIndex(length=" + this.length + ", sizes=" + Arrays.toString(this.sizes) + ", offsets=" + Arrays.toString(this.offsets) + ", timeUs=" + Arrays.toString(this.timesUs) + ", durationsUs=" + Arrays.toString(this.durationsUs) + SQLBuilder.PARENTHESES_RIGHT;
    }
}
