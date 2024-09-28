package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class TsBinarySearchSeeker extends BinarySearchSeeker {
    private static final int MINIMUM_SEARCH_RANGE_BYTES = 940;
    private static final long SEEK_TOLERANCE_US = 100000;
    private static final int TIMESTAMP_SEARCH_BYTES = 112800;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TsBinarySearchSeeker(com.google.android.exoplayer2.util.TimestampAdjuster r20, long r21, long r23, int r25) {
        /*
            r19 = this;
            com.google.android.exoplayer2.extractor.BinarySearchSeeker$DefaultSeekTimestampConverter r1 = new com.google.android.exoplayer2.extractor.BinarySearchSeeker$DefaultSeekTimestampConverter
            r1.<init>()
            com.google.android.exoplayer2.extractor.ts.TsBinarySearchSeeker$TsPcrSeeker r2 = new com.google.android.exoplayer2.extractor.ts.TsBinarySearchSeeker$TsPcrSeeker
            r15 = r20
            r13 = r25
            r2.<init>(r13, r15)
            r3 = 1
            long r7 = r21 + r3
            r5 = 0
            r9 = 0
            r16 = 188(0xbc, double:9.3E-322)
            r18 = 940(0x3ac, float:1.317E-42)
            r0 = r19
            r3 = r21
            r11 = r23
            r13 = r16
            r15 = r18
            r0.<init>(r1, r2, r3, r5, r7, r9, r11, r13, r15)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.TsBinarySearchSeeker.<init>(com.google.android.exoplayer2.util.TimestampAdjuster, long, long, int):void");
    }

    private static final class TsPcrSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final ParsableByteArray packetBuffer = new ParsableByteArray();
        private final int pcrPid;
        private final TimestampAdjuster pcrTimestampAdjuster;

        public TsPcrSeeker(int pcrPid2, TimestampAdjuster pcrTimestampAdjuster2) {
            this.pcrPid = pcrPid2;
            this.pcrTimestampAdjuster = pcrTimestampAdjuster2;
        }

        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetTimestamp, BinarySearchSeeker.OutputFrameHolder outputFrameHolder) throws IOException, InterruptedException {
            long inputPosition = input.getPosition();
            int bytesToSearch = (int) Math.min(112800, input.getLength() - inputPosition);
            this.packetBuffer.reset(bytesToSearch);
            input.peekFully(this.packetBuffer.data, 0, bytesToSearch);
            return searchForPcrValueInBuffer(this.packetBuffer, targetTimestamp, inputPosition);
        }

        private BinarySearchSeeker.TimestampSearchResult searchForPcrValueInBuffer(ParsableByteArray packetBuffer2, long targetPcrTimeUs, long bufferStartOffset) {
            long endOfLastPacketPosition;
            int limit;
            ParsableByteArray parsableByteArray = packetBuffer2;
            long j = bufferStartOffset;
            int limit2 = packetBuffer2.limit();
            long startOfLastPacketPosition = -1;
            long pcrValue = -1;
            long lastPcrTimeUsInRange = C.TIME_UNSET;
            while (true) {
                if (packetBuffer2.bytesLeft() < 188) {
                    long j2 = startOfLastPacketPosition;
                    endOfLastPacketPosition = pcrValue;
                    break;
                }
                int startOfPacket = TsUtil.findSyncBytePosition(parsableByteArray.data, packetBuffer2.getPosition(), limit2);
                int endOfPacket = startOfPacket + TsExtractor.TS_PACKET_SIZE;
                if (endOfPacket > limit2) {
                    int i = limit2;
                    long j3 = startOfLastPacketPosition;
                    endOfLastPacketPosition = pcrValue;
                    break;
                }
                long j4 = pcrValue;
                long pcrValue2 = TsUtil.readPcrFromPacket(parsableByteArray, startOfPacket, this.pcrPid);
                if (pcrValue2 != C.TIME_UNSET) {
                    long pcrTimeUs = this.pcrTimestampAdjuster.adjustTsTimestamp(pcrValue2);
                    if (pcrTimeUs > targetPcrTimeUs) {
                        if (lastPcrTimeUsInRange == C.TIME_UNSET) {
                            return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(pcrTimeUs, j);
                        }
                        return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(j + startOfLastPacketPosition);
                    } else if (pcrTimeUs + TsBinarySearchSeeker.SEEK_TOLERANCE_US > targetPcrTimeUs) {
                        int i2 = limit2;
                        long j5 = startOfLastPacketPosition;
                        return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(((long) startOfPacket) + j);
                    } else {
                        limit = limit2;
                        long j6 = startOfLastPacketPosition;
                        startOfLastPacketPosition = (long) startOfPacket;
                        lastPcrTimeUsInRange = pcrTimeUs;
                    }
                } else {
                    limit = limit2;
                    long j7 = startOfLastPacketPosition;
                }
                parsableByteArray.setPosition(endOfPacket);
                pcrValue = (long) endOfPacket;
                limit2 = limit;
            }
            if (lastPcrTimeUsInRange != C.TIME_UNSET) {
                return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(lastPcrTimeUsInRange, j + endOfLastPacketPosition);
            }
            return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
        }

        public void onSeekFinished() {
            this.packetBuffer.reset(Util.EMPTY_BYTE_ARRAY);
        }
    }
}
