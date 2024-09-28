package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import kotlin.UByte;

final class PsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 1000;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 20000;

    public PsBinarySearchSeeker(TimestampAdjuster scrTimestampAdjuster, long streamDurationUs, long inputLength) {
        super(new BinarySearchSeeker.DefaultSeekTimestampConverter(), new PsScrSeeker(scrTimestampAdjuster), streamDurationUs, 0, streamDurationUs + 1, 0, inputLength, 188, 1000);
    }

    private static final class PsScrSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final ParsableByteArray packetBuffer;
        private final TimestampAdjuster scrTimestampAdjuster;

        private PsScrSeeker(TimestampAdjuster scrTimestampAdjuster2) {
            this.scrTimestampAdjuster = scrTimestampAdjuster2;
            this.packetBuffer = new ParsableByteArray();
        }

        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp, BinarySearchSeeker.OutputFrameHolder outputFrameHolder) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min(20000, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForScrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }

        private BinarySearchSeeker.TimestampSearchResult searchForScrValueInBuffer(ParsableByteArray packetBuffer2, long targetScrTimeUs, long bufferStartOffset) {
            ParsableByteArray parsableByteArray = packetBuffer2;
            long j = bufferStartOffset;
            int startOfLastPacketPosition = -1;
            int endOfLastPacketPosition = -1;
            long lastScrTimeUsInRange = C.TIME_UNSET;
            while (packetBuffer2.bytesLeft() >= 4) {
                if (PsBinarySearchSeeker.peekIntAtPosition(parsableByteArray.data, packetBuffer2.getPosition()) != 442) {
                    parsableByteArray.skipBytes(1);
                } else {
                    parsableByteArray.skipBytes(4);
                    long scrValue = PsDurationReader.readScrValueFromPack(packetBuffer2);
                    if (scrValue != C.TIME_UNSET) {
                        long scrTimeUs = this.scrTimestampAdjuster.adjustTsTimestamp(scrValue);
                        if (scrTimeUs > targetScrTimeUs) {
                            if (lastScrTimeUsInRange == C.TIME_UNSET) {
                                return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(scrTimeUs, j);
                            }
                            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(((long) startOfLastPacketPosition) + j);
                        } else if (PsBinarySearchSeeker.SEEK_TOLERANCE_US + scrTimeUs > targetScrTimeUs) {
                            return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(((long) packetBuffer2.getPosition()) + j);
                        } else {
                            lastScrTimeUsInRange = scrTimeUs;
                            startOfLastPacketPosition = packetBuffer2.getPosition();
                        }
                    }
                    skipToEndOfCurrentPack(packetBuffer2);
                    endOfLastPacketPosition = packetBuffer2.getPosition();
                }
            }
            if (lastScrTimeUsInRange != C.TIME_UNSET) {
                return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(lastScrTimeUsInRange, ((long) endOfLastPacketPosition) + j);
            }
            return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        private static void skipToEndOfCurrentPack(ParsableByteArray packetBuffer2) {
            int limit = packetBuffer2.limit();
            if (packetBuffer2.bytesLeft() < 10) {
                packetBuffer2.setPosition(limit);
                return;
            }
            packetBuffer2.skipBytes(9);
            int packStuffingLength = packetBuffer2.readUnsignedByte() & 7;
            if (packetBuffer2.bytesLeft() < packStuffingLength) {
                packetBuffer2.setPosition(limit);
                return;
            }
            packetBuffer2.skipBytes(packStuffingLength);
            if (packetBuffer2.bytesLeft() < 4) {
                packetBuffer2.setPosition(limit);
                return;
            }
            if (PsBinarySearchSeeker.peekIntAtPosition(packetBuffer2.data, packetBuffer2.getPosition()) == 443) {
                packetBuffer2.skipBytes(4);
                int systemHeaderLength = packetBuffer2.readUnsignedShort();
                if (packetBuffer2.bytesLeft() < systemHeaderLength) {
                    packetBuffer2.setPosition(limit);
                    return;
                }
                packetBuffer2.skipBytes(systemHeaderLength);
            }
            while (packetBuffer2.bytesLeft() >= 4 && (nextStartCode = PsBinarySearchSeeker.peekIntAtPosition(packetBuffer2.data, packetBuffer2.getPosition())) != 442 && nextStartCode != 441 && (nextStartCode >>> 8) == 1) {
                packetBuffer2.skipBytes(4);
                if (packetBuffer2.bytesLeft() < 2) {
                    packetBuffer2.setPosition(limit);
                    return;
                } else {
                    packetBuffer2.setPosition(Math.min(packetBuffer2.limit(), packetBuffer2.getPosition() + packetBuffer2.readUnsignedShort()));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static int peekIntAtPosition(byte[] data, int position) {
        return ((data[position] & UByte.MAX_VALUE) << 24) | ((data[position + 1] & UByte.MAX_VALUE) << 16) | ((data[position + 2] & UByte.MAX_VALUE) << 8) | (data[position + 3] & UByte.MAX_VALUE);
    }
}
