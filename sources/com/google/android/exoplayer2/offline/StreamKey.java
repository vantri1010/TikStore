package com.google.android.exoplayer2.offline;

public final class StreamKey implements Comparable<StreamKey> {
    public final int groupIndex;
    public final int periodIndex;
    public final int trackIndex;

    public StreamKey(int groupIndex2, int trackIndex2) {
        this(0, groupIndex2, trackIndex2);
    }

    public StreamKey(int periodIndex2, int groupIndex2, int trackIndex2) {
        this.periodIndex = periodIndex2;
        this.groupIndex = groupIndex2;
        this.trackIndex = trackIndex2;
    }

    public String toString() {
        return this.periodIndex + "." + this.groupIndex + "." + this.trackIndex;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StreamKey that = (StreamKey) o;
        if (this.periodIndex == that.periodIndex && this.groupIndex == that.groupIndex && this.trackIndex == that.trackIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((this.periodIndex * 31) + this.groupIndex) * 31) + this.trackIndex;
    }

    public int compareTo(StreamKey o) {
        int result = this.periodIndex - o.periodIndex;
        if (result != 0) {
            return result;
        }
        int result2 = this.groupIndex - o.groupIndex;
        if (result2 == 0) {
            return this.trackIndex - o.trackIndex;
        }
        return result2;
    }
}
