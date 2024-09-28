package com.google.android.exoplayer2.extractor.mp3;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

final class XingSeeker implements Mp3Extractor.Seeker {
    private static final String TAG = "XingSeeker";
    private final long dataEndPosition;
    private final long dataSize;
    private final long dataStartPosition;
    private final long durationUs;
    private final long[] tableOfContents;
    private final int xingFrameSize;

    public static XingSeeker create(long inputLength, long position, MpegAudioHeader mpegAudioHeader, ParsableByteArray frame) {
        long j = inputLength;
        MpegAudioHeader mpegAudioHeader2 = mpegAudioHeader;
        int samplesPerFrame = mpegAudioHeader2.samplesPerFrame;
        int sampleRate = mpegAudioHeader2.sampleRate;
        int flags = frame.readInt();
        if ((flags & 1) != 1) {
            return null;
        }
        int readUnsignedIntToInt = frame.readUnsignedIntToInt();
        int frameCount = readUnsignedIntToInt;
        if (readUnsignedIntToInt == 0) {
            return null;
        }
        long durationUs2 = Util.scaleLargeTimestamp((long) frameCount, ((long) samplesPerFrame) * 1000000, (long) sampleRate);
        if ((flags & 6) != 6) {
            return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs2);
        }
        long dataSize2 = (long) frame.readUnsignedIntToInt();
        long[] tableOfContents2 = new long[100];
        for (int i = 0; i < 100; i++) {
            tableOfContents2[i] = (long) frame.readUnsignedByte();
        }
        if (!(j == -1 || j == position + dataSize2)) {
            Log.w(TAG, "XING data size mismatch: " + j + ", " + (position + dataSize2));
        }
        long j2 = dataSize2;
        long[] jArr = tableOfContents2;
        return new XingSeeker(position, mpegAudioHeader2.frameSize, durationUs2, dataSize2, tableOfContents2);
    }

    private XingSeeker(long dataStartPosition2, int xingFrameSize2, long durationUs2) {
        this(dataStartPosition2, xingFrameSize2, durationUs2, -1, (long[]) null);
    }

    private XingSeeker(long dataStartPosition2, int xingFrameSize2, long durationUs2, long dataSize2, long[] tableOfContents2) {
        this.dataStartPosition = dataStartPosition2;
        this.xingFrameSize = xingFrameSize2;
        this.durationUs = durationUs2;
        this.tableOfContents = tableOfContents2;
        this.dataSize = dataSize2;
        this.dataEndPosition = dataSize2 != -1 ? dataStartPosition2 + dataSize2 : -1;
    }

    public boolean isSeekable() {
        return this.tableOfContents != null;
    }

    public SeekMap.SeekPoints getSeekPoints(long timeUs) {
        double scaledPosition;
        if (!isSeekable()) {
            return new SeekMap.SeekPoints(new SeekPoint(0, this.dataStartPosition + ((long) this.xingFrameSize)));
        }
        long timeUs2 = Util.constrainValue(timeUs, 0, this.durationUs);
        double percent = (((double) timeUs2) * 100.0d) / ((double) this.durationUs);
        if (percent <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            scaledPosition = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        } else if (percent >= 100.0d) {
            scaledPosition = 256.0d;
        } else {
            int prevTableIndex = (int) percent;
            long[] tableOfContents2 = (long[]) Assertions.checkNotNull(this.tableOfContents);
            double prevScaledPosition = (double) tableOfContents2[prevTableIndex];
            scaledPosition = (((prevTableIndex == 99 ? 256.0d : (double) tableOfContents2[prevTableIndex + 1]) - prevScaledPosition) * (percent - ((double) prevTableIndex))) + prevScaledPosition;
        }
        return new SeekMap.SeekPoints(new SeekPoint(timeUs2, this.dataStartPosition + Util.constrainValue(Math.round((scaledPosition / 256.0d) * ((double) this.dataSize)), (long) this.xingFrameSize, this.dataSize - 1)));
    }

    public long getTimeUs(long position) {
        double interpolateFraction;
        long positionOffset = position - this.dataStartPosition;
        if (!isSeekable()) {
            return 0;
        } else if (positionOffset <= ((long) this.xingFrameSize)) {
            long j = positionOffset;
            return 0;
        } else {
            long[] tableOfContents2 = (long[]) Assertions.checkNotNull(this.tableOfContents);
            double scaledPosition = (((double) positionOffset) * 256.0d) / ((double) this.dataSize);
            int prevTableIndex = Util.binarySearchFloor(tableOfContents2, (long) scaledPosition, true, true);
            long prevTimeUs = getTimeUsForTableIndex(prevTableIndex);
            long prevScaledPosition = tableOfContents2[prevTableIndex];
            long nextTimeUs = getTimeUsForTableIndex(prevTableIndex + 1);
            long nextScaledPosition = prevTableIndex == 99 ? 256 : tableOfContents2[prevTableIndex + 1];
            if (prevScaledPosition == nextScaledPosition) {
                long[] jArr = tableOfContents2;
                interpolateFraction = 0.0d;
                long j2 = positionOffset;
            } else {
                long j3 = positionOffset;
                long[] jArr2 = tableOfContents2;
                interpolateFraction = (scaledPosition - ((double) prevScaledPosition)) / ((double) (nextScaledPosition - prevScaledPosition));
            }
            return Math.round(((double) (nextTimeUs - prevTimeUs)) * interpolateFraction) + prevTimeUs;
        }
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public long getDataEndPosition() {
        return this.dataEndPosition;
    }

    private long getTimeUsForTableIndex(int tableIndex) {
        return (this.durationUs * ((long) tableIndex)) / 100;
    }
}
