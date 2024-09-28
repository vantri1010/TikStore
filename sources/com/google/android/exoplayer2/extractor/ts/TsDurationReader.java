package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class TsDurationReader {
    private static final int TIMESTAMP_SEARCH_BYTES = 112800;
    private long durationUs = C.TIME_UNSET;
    private long firstPcrValue = C.TIME_UNSET;
    private boolean isDurationRead;
    private boolean isFirstPcrValueRead;
    private boolean isLastPcrValueRead;
    private long lastPcrValue = C.TIME_UNSET;
    private final ParsableByteArray packetBuffer = new ParsableByteArray();
    private final TimestampAdjuster pcrTimestampAdjuster = new TimestampAdjuster(0);

    TsDurationReader() {
    }

    public boolean isDurationReadFinished() {
        return this.isDurationRead;
    }

    public int readDuration(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        if (pcrPid <= 0) {
            return finishReadDuration(input);
        }
        if (!this.isLastPcrValueRead) {
            return readLastPcrValue(input, seekPositionHolder, pcrPid);
        }
        if (this.lastPcrValue == C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        if (!this.isFirstPcrValueRead) {
            return readFirstPcrValue(input, seekPositionHolder, pcrPid);
        }
        long j = this.firstPcrValue;
        if (j == C.TIME_UNSET) {
            return finishReadDuration(input);
        }
        this.durationUs = this.pcrTimestampAdjuster.adjustTsTimestamp(this.lastPcrValue) - this.pcrTimestampAdjuster.adjustTsTimestamp(j);
        return finishReadDuration(input);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public TimestampAdjuster getPcrTimestampAdjuster() {
        return this.pcrTimestampAdjuster;
    }

    private int finishReadDuration(ExtractorInput input) {
        this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        this.isDurationRead = true;
        input.resetPeekPosition();
        return 0;
    }

    private int readFirstPcrValue(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        int bytesToSearch = (int) Math.min(112800, input.getLength());
        if (input.getPosition() != ((long) 0)) {
            seekPositionHolder.position = (long) 0;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.firstPcrValue = readFirstPcrValueFromBuffer(this.packetBuffer, pcrPid);
        this.isFirstPcrValueRead = true;
        return 0;
    }

    private long readFirstPcrValueFromBuffer(ParsableByteArray packetBuffer2, int pcrPid) {
        int searchStartPosition = packetBuffer2.getPosition();
        int searchEndPosition = packetBuffer2.limit();
        for (int searchPosition = searchStartPosition; searchPosition < searchEndPosition; searchPosition++) {
            if (packetBuffer2.data[searchPosition] == 71) {
                long pcrValue = TsUtil.readPcrFromPacket(packetBuffer2, searchPosition, pcrPid);
                if (pcrValue != C.TIME_UNSET) {
                    return pcrValue;
                }
            }
        }
        return C.TIME_UNSET;
    }

    private int readLastPcrValue(ExtractorInput input, PositionHolder seekPositionHolder, int pcrPid) throws IOException, InterruptedException {
        long inputLength = input.getLength();
        int bytesToSearch = (int) Math.min(112800, inputLength);
        long searchStartPosition = inputLength - ((long) bytesToSearch);
        if (input.getPosition() != searchStartPosition) {
            seekPositionHolder.position = searchStartPosition;
            return 1;
        }
        this.packetBuffer.reset(bytesToSearch);
        input.resetPeekPosition();
        input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
        this.lastPcrValue = readLastPcrValueFromBuffer(this.packetBuffer, pcrPid);
        this.isLastPcrValueRead = true;
        return 0;
    }

    private long readLastPcrValueFromBuffer(ParsableByteArray packetBuffer2, int pcrPid) {
        int searchStartPosition = packetBuffer2.getPosition();
        for (int searchPosition = packetBuffer2.limit() - 1; searchPosition >= searchStartPosition; searchPosition--) {
            if (packetBuffer2.data[searchPosition] == 71) {
                long pcrValue = TsUtil.readPcrFromPacket(packetBuffer2, searchPosition, pcrPid);
                if (pcrValue != C.TIME_UNSET) {
                    return pcrValue;
                }
            }
        }
        return C.TIME_UNSET;
    }
}
