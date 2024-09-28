package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

final class VbriSeeker implements Mp3Extractor.Seeker {
    private static final String TAG = "VbriSeeker";
    private final long dataEndPosition;
    private final long durationUs;
    private final long[] positions;
    private final long[] timesUs;

    public static VbriSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        int segmentSize;
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        ParsableByteArray parsableByteArray = frame;
        parsableByteArray.skipBytes(10);
        int numFrames = frame.readInt();
        if (numFrames <= 0) {
            return null;
        }
        int sampleRate = mpegAudioHeader2.sampleRate;
        long durationUs2 = Util.scaleLargeTimestamp((long) numFrames, 1000000 * ((long) (sampleRate >= 32000 ? 1152 : 576)), (long) sampleRate);
        int entryCount = frame.readUnsignedShort();
        int scale = frame.readUnsignedShort();
        int entrySize = frame.readUnsignedShort();
        parsableByteArray.skipBytes(2);
        long minPosition = position + ((long) mpegAudioHeader2.frameSize);
        long[] timesUs2 = new long[entryCount];
        long[] positions2 = new long[entryCount];
        long position2 = position;
        int index = 0;
        while (index < entryCount) {
            int sampleRate2 = sampleRate;
            long durationUs3 = durationUs2;
            timesUs2[index] = (((long) index) * durationUs2) / ((long) entryCount);
            long position3 = position2;
            positions2[index] = Math.max(position3, minPosition);
            if (entrySize == 1) {
                segmentSize = frame.readUnsignedByte();
            } else if (entrySize == 2) {
                segmentSize = frame.readUnsignedShort();
            } else if (entrySize == 3) {
                segmentSize = frame.readUnsignedInt24();
            } else if (entrySize != 4) {
                return null;
            } else {
                segmentSize = frame.readUnsignedIntToInt();
            }
            int i = segmentSize;
            position2 = position3 + ((long) (segmentSize * scale));
            index++;
            MpegAudioHeader mpegAudioHeader3 = mpegAudioHeader;
            ParsableByteArray parsableByteArray2 = frame;
            sampleRate = sampleRate2;
            durationUs2 = durationUs3;
        }
        long durationUs4 = durationUs2;
        long position4 = position2;
        if (!(j == -1 || j == position4)) {
            Log.w(TAG, "VBRI data size mismatch: " + j + ", " + position4);
        }
        long j2 = minPosition;
        return new VbriSeeker(timesUs2, positions2, durationUs4, position4);
    }

    private VbriSeeker(long[] timesUs2, long[] positions2, long durationUs2, long dataEndPosition2) {
        this.timesUs = timesUs2;
        this.positions = positions2;
        this.durationUs = durationUs2;
        this.dataEndPosition = dataEndPosition2;
    }

    public boolean isSeekable() {
        return true;
    }

    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        int tableIndex = Util.binarySearchFloor(this.timesUs, timeUs, true, true);
        SeekPoint seekPoint = new SeekPoint(this.timesUs[tableIndex], this.positions[tableIndex]);
        if (seekPoint.timeUs < timeUs) {
            long[] jArr = this.timesUs;
            if (tableIndex != jArr.length - 1) {
                return new SeekMap.SeekPoints(seekPoint, new SeekPoint(jArr[tableIndex + 1], this.positions[tableIndex + 1]));
            }
        }
        return new SeekMap.SeekPoints(seekPoint);
    }

    public long getTimeUs(long position) {
        return this.timesUs[Util.binarySearchFloor(this.positions, position, true, true)];
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getDataEndPosition() {
        return this.dataEndPosition;
    }
}
