package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import java.io.IOException;
import java.nio.ByteBuffer;

final class FlacBinarySearchSeeker extends BinarySearchSeeker {
    private final FlacDecoderJni decoderJni;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FlacBinarySearchSeeker(com.google.android.exoplayer2.util.FlacStreamInfo r19, long r20, long r22, com.google.android.exoplayer2.ext.flac.FlacDecoderJni r24) {
        /*
            r18 = this;
            r0 = r19
            com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker$FlacSeekTimestampConverter r2 = new com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker$FlacSeekTimestampConverter
            r2.<init>(r0)
            com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker$FlacTimestampSeeker r3 = new com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker$FlacTimestampSeeker
            r1 = 0
            r14 = r24
            r3.<init>(r14)
            long r4 = r19.durationUs()
            long r8 = r0.totalSamples
            long r15 = r19.getApproxBytesPerFrame()
            int r1 = r0.minFrameSize
            r6 = 1
            int r17 = java.lang.Math.max(r6, r1)
            r6 = 0
            r1 = r18
            r10 = r20
            r12 = r22
            r14 = r15
            r16 = r17
            r1.<init>(r2, r3, r4, r6, r8, r10, r12, r14, r16)
            java.lang.Object r1 = com.google.android.exoplayer2.util.Assertions.checkNotNull(r24)
            com.google.android.exoplayer2.ext.flac.FlacDecoderJni r1 = (com.google.android.exoplayer2.ext.flac.FlacDecoderJni) r1
            r2 = r18
            r2.decoderJni = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ext.flac.FlacBinarySearchSeeker.<init>(com.google.android.exoplayer2.util.FlacStreamInfo, long, long, com.google.android.exoplayer2.ext.flac.FlacDecoderJni):void");
    }

    /* access modifiers changed from: protected */
    public void onSeekOperationFinished(boolean foundTargetFrame, long resultPosition) {
        if (!foundTargetFrame) {
            this.decoderJni.reset(resultPosition);
        }
    }

    private static final class FlacTimestampSeeker implements BinarySearchSeeker.TimestampSeeker {
        private final FlacDecoderJni decoderJni;

        public /* synthetic */ void onSeekFinished() {
            BinarySearchSeeker.TimestampSeeker.CC.$default$onSeekFinished(this);
        }

        private FlacTimestampSeeker(FlacDecoderJni decoderJni2) {
            this.decoderJni = decoderJni2;
        }

        public BinarySearchSeeker.TimestampSearchResult searchForTimestamp(ExtractorInput input, long targetSampleIndex, BinarySearchSeeker.OutputFrameHolder outputFrameHolder) throws IOException, InterruptedException {
            BinarySearchSeeker.OutputFrameHolder outputFrameHolder2 = outputFrameHolder;
            ByteBuffer outputBuffer = outputFrameHolder2.byteBuffer;
            long searchPosition = input.getPosition();
            this.decoderJni.reset(searchPosition);
            try {
                this.decoderJni.decodeSampleWithBacktrackPosition(outputBuffer, searchPosition);
                if (outputBuffer.limit() == 0) {
                    return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
                }
                long lastFrameSampleIndex = this.decoderJni.getLastFrameFirstSampleIndex();
                long nextFrameSampleIndex = this.decoderJni.getNextFrameFirstSampleIndex();
                long nextFrameSamplePosition = this.decoderJni.getDecodePosition();
                if (lastFrameSampleIndex <= targetSampleIndex && nextFrameSampleIndex > targetSampleIndex) {
                    outputFrameHolder2.timeUs = this.decoderJni.getLastFrameTimestamp();
                    return BinarySearchSeeker.TimestampSearchResult.targetFoundResult(input.getPosition());
                } else if (nextFrameSampleIndex <= targetSampleIndex) {
                    return BinarySearchSeeker.TimestampSearchResult.underestimatedResult(nextFrameSampleIndex, nextFrameSamplePosition);
                } else {
                    return BinarySearchSeeker.TimestampSearchResult.overestimatedResult(lastFrameSampleIndex, searchPosition);
                }
            } catch (FlacDecoderJni.FlacFrameDecodeException e) {
                return BinarySearchSeeker.TimestampSearchResult.NO_TIMESTAMP_IN_RANGE_RESULT;
            }
        }
    }

    private static final class FlacSeekTimestampConverter implements BinarySearchSeeker.SeekTimestampConverter {
        private final FlacStreamInfo streamInfo;

        public FlacSeekTimestampConverter(FlacStreamInfo streamInfo2) {
            this.streamInfo = streamInfo2;
        }

        public long timeUsToTargetTime(long timeUs) {
            return ((FlacStreamInfo) Assertions.checkNotNull(this.streamInfo)).getSampleIndex(timeUs);
        }
    }
}
