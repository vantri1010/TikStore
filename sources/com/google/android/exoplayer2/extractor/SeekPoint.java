package com.google.android.exoplayer2.extractor;

public final class SeekPoint {
    public static final SeekPoint START = new SeekPoint(0, 0);
    public final long position;
    public final long timeUs;

    public SeekPoint(long timeUs2, long position2) {
        this.timeUs = timeUs2;
        this.position = position2;
    }

    public String toString() {
        return "[timeUs=" + this.timeUs + ", position=" + this.position + "]";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SeekPoint other = (SeekPoint) obj;
        if (this.timeUs == other.timeUs && this.position == other.position) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((int) this.timeUs) * 31) + ((int) this.position);
    }
}
