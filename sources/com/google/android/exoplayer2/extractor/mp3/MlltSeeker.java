package com.google.android.exoplayer2.extractor.mp3;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.metadata.id3.MlltFrame;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

final class MlltSeeker implements Mp3Extractor.Seeker {
    private final long durationUs;
    private final long[] referencePositions;
    private final long[] referenceTimesMs;

    public static MlltSeeker create(long firstFramePosition, MlltFrame mlltFrame) {
        int referenceCount = mlltFrame.bytesDeviations.length;
        long[] referencePositions2 = new long[(referenceCount + 1)];
        long[] referenceTimesMs2 = new long[(referenceCount + 1)];
        referencePositions2[0] = firstFramePosition;
        referenceTimesMs2[0] = 0;
        long position = firstFramePosition;
        long timeMs = 0;
        for (int i = 1; i <= referenceCount; i++) {
            position += (long) (mlltFrame.bytesBetweenReference + mlltFrame.bytesDeviations[i - 1]);
            timeMs += (long) (mlltFrame.millisecondsBetweenReference + mlltFrame.millisecondsDeviations[i - 1]);
            referencePositions2[i] = position;
            referenceTimesMs2[i] = timeMs;
        }
        return new MlltSeeker(referencePositions2, referenceTimesMs2);
    }

    private MlltSeeker(long[] referencePositions2, long[] referenceTimesMs2) {
        this.referencePositions = referencePositions2;
        this.referenceTimesMs = referenceTimesMs2;
        this.durationUs = C.msToUs(referenceTimesMs2[referenceTimesMs2.length - 1]);
    }

    public boolean isSeekable() {
        return true;
    }

    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        Pair<Long, Long> timeMsAndPosition = linearlyInterpolate(C.usToMs(Util.constrainValue(timeUs, 0, this.durationUs)), this.referenceTimesMs, this.referencePositions);
        return new SeekMap.SeekPoints(new SeekPoint(C.msToUs(((Long) timeMsAndPosition.first).longValue()), ((Long) timeMsAndPosition.second).longValue()));
    }

    public long getTimeUs(long position) {
        return C.msToUs(((Long) linearlyInterpolate(position, this.referencePositions, this.referenceTimesMs).second).longValue());
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    private static Pair<Long, Long> linearlyInterpolate(long x, long[] xReferences, long[] yReferences) {
        long j = x;
        long[] jArr = xReferences;
        int previousReferenceIndex = Util.binarySearchFloor(jArr, j, true, true);
        long xPreviousReference = jArr[previousReferenceIndex];
        long yPreviousReference = yReferences[previousReferenceIndex];
        int nextReferenceIndex = previousReferenceIndex + 1;
        if (nextReferenceIndex == jArr.length) {
            return Pair.create(Long.valueOf(xPreviousReference), Long.valueOf(yPreviousReference));
        }
        long xNextReference = jArr[nextReferenceIndex];
        long yNextReference = yReferences[nextReferenceIndex];
        double proportion = xNextReference == xPreviousReference ? FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE : (((double) j) - ((double) xPreviousReference)) / ((double) (xNextReference - xPreviousReference));
        double d = proportion;
        return Pair.create(Long.valueOf(x), Long.valueOf(((long) (((double) (yNextReference - yPreviousReference)) * proportion)) + yPreviousReference));
    }

    public long getDataEndPosition() {
        return -1;
    }
}
