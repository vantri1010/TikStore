package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.util.Util;

final class MediaPeriodInfo {
    public final long contentPositionUs;
    public final long durationUs;
    public final MediaSource.MediaPeriodId id;
    public final boolean isFinal;
    public final boolean isLastInTimelinePeriod;
    public final long startPositionUs;

    MediaPeriodInfo(MediaSource.MediaPeriodId id2, long startPositionUs2, long contentPositionUs2, long durationUs2, boolean isLastInTimelinePeriod2, boolean isFinal2) {
        this.id = id2;
        this.startPositionUs = startPositionUs2;
        this.contentPositionUs = contentPositionUs2;
        this.durationUs = durationUs2;
        this.isLastInTimelinePeriod = isLastInTimelinePeriod2;
        this.isFinal = isFinal2;
    }

    public MediaPeriodInfo copyWithStartPositionUs(long startPositionUs2) {
        return new MediaPeriodInfo(this.id, startPositionUs2, this.contentPositionUs, this.durationUs, this.isLastInTimelinePeriod, this.isFinal);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaPeriodInfo that = (MediaPeriodInfo) o;
        if (this.startPositionUs == that.startPositionUs && this.contentPositionUs == that.contentPositionUs && this.durationUs == that.durationUs && this.isLastInTimelinePeriod == that.isLastInTimelinePeriod && this.isFinal == that.isFinal && Util.areEqual(this.id, that.id)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((((((((17 * 31) + this.id.hashCode()) * 31) + ((int) this.startPositionUs)) * 31) + ((int) this.contentPositionUs)) * 31) + ((int) this.durationUs)) * 31) + (this.isLastInTimelinePeriod ? 1 : 0)) * 31) + (this.isFinal ? 1 : 0);
    }
}
