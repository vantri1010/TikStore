package com.google.android.exoplayer2.source;

import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.IOException;

public interface MediaSource {

    public interface SourceInfoRefreshListener {
        void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj);
    }

    void addEventListener(Handler handler, MediaSourceEventListener mediaSourceEventListener);

    MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator, long j);

    Object getTag();

    void maybeThrowSourceInfoRefreshError() throws IOException;

    void prepareSource(SourceInfoRefreshListener sourceInfoRefreshListener, TransferListener transferListener);

    void releasePeriod(MediaPeriod mediaPeriod);

    void releaseSource(SourceInfoRefreshListener sourceInfoRefreshListener);

    void removeEventListener(MediaSourceEventListener mediaSourceEventListener);

    public static final class MediaPeriodId {
        public final int adGroupIndex;
        public final int adIndexInAdGroup;
        public final long endPositionUs;
        public final Object periodUid;
        public final long windowSequenceNumber;

        public MediaPeriodId(Object periodUid2) {
            this(periodUid2, -1);
        }

        public MediaPeriodId(Object periodUid2, long windowSequenceNumber2) {
            this(periodUid2, -1, -1, windowSequenceNumber2, C.TIME_UNSET);
        }

        public MediaPeriodId(Object periodUid2, long windowSequenceNumber2, long endPositionUs2) {
            this(periodUid2, -1, -1, windowSequenceNumber2, endPositionUs2);
        }

        public MediaPeriodId(Object periodUid2, int adGroupIndex2, int adIndexInAdGroup2, long windowSequenceNumber2) {
            this(periodUid2, adGroupIndex2, adIndexInAdGroup2, windowSequenceNumber2, C.TIME_UNSET);
        }

        private MediaPeriodId(Object periodUid2, int adGroupIndex2, int adIndexInAdGroup2, long windowSequenceNumber2, long endPositionUs2) {
            this.periodUid = periodUid2;
            this.adGroupIndex = adGroupIndex2;
            this.adIndexInAdGroup = adIndexInAdGroup2;
            this.windowSequenceNumber = windowSequenceNumber2;
            this.endPositionUs = endPositionUs2;
        }

        public MediaPeriodId copyWithPeriodUid(Object newPeriodUid) {
            if (this.periodUid.equals(newPeriodUid)) {
                return this;
            }
            return new MediaPeriodId(newPeriodUid, this.adGroupIndex, this.adIndexInAdGroup, this.windowSequenceNumber, this.endPositionUs);
        }

        public boolean isAd() {
            return this.adGroupIndex != -1;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MediaPeriodId periodId = (MediaPeriodId) obj;
            if (this.periodUid.equals(periodId.periodUid) && this.adGroupIndex == periodId.adGroupIndex && this.adIndexInAdGroup == periodId.adIndexInAdGroup && this.windowSequenceNumber == periodId.windowSequenceNumber && this.endPositionUs == periodId.endPositionUs) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((((((((17 * 31) + this.periodUid.hashCode()) * 31) + this.adGroupIndex) * 31) + this.adIndexInAdGroup) * 31) + ((int) this.windowSequenceNumber)) * 31) + ((int) this.endPositionUs);
        }
    }

    /* renamed from: com.google.android.exoplayer2.source.MediaSource$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Object $default$getTag(MediaSource _this) {
            return null;
        }
    }
}
