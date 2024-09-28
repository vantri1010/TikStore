package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.Id3Peeker;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class FlacExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = $$Lambda$FlacExtractor$hclvvK8AqHrca9y8kXj1zx0IKB4.INSTANCE;
    private static final byte[] FLAC_SIGNATURE = {102, 76, 97, 67, 0, 0, 0, 34};
    public static final int FLAG_DISABLE_ID3_METADATA = 1;
    private FlacDecoderJni decoderJni;
    private ExtractorOutput extractorOutput;
    private FlacBinarySearchSeeker flacBinarySearchSeeker;
    private Metadata id3Metadata;
    private final Id3Peeker id3Peeker;
    private final boolean isId3MetadataDisabled;
    private ParsableByteArray outputBuffer;
    private ByteBuffer outputByteBuffer;
    private BinarySearchSeeker.OutputFrameHolder outputFrameHolder;
    private boolean readPastStreamInfo;
    private FlacStreamInfo streamInfo;
    private TrackOutput trackOutput;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new FlacExtractor()};
    }

    public FlacExtractor() {
        this(0);
    }

    public FlacExtractor(int flags) {
        this.id3Peeker = new Id3Peeker();
        this.isId3MetadataDisabled = (flags & 1) != 0;
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        this.extractorOutput.endTracks();
        try {
            this.decoderJni = new FlacDecoderJni();
        } catch (FlacDecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        if (input.getPosition() == 0) {
            this.id3Metadata = peekId3Data(input);
        }
        return peekFlacSignature(input);
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (input.getPosition() == 0 && !this.isId3MetadataDisabled && this.id3Metadata == null) {
            this.id3Metadata = peekId3Data(input);
        }
        this.decoderJni.setData(input);
        readPastStreamInfo(input);
        FlacBinarySearchSeeker flacBinarySearchSeeker2 = this.flacBinarySearchSeeker;
        if (flacBinarySearchSeeker2 != null && flacBinarySearchSeeker2.isSeeking()) {
            return handlePendingSeek(input, seekPosition);
        }
        long lastDecodePosition = this.decoderJni.getDecodePosition();
        try {
            this.decoderJni.decodeSampleWithBacktrackPosition(this.outputByteBuffer, lastDecodePosition);
            int outputSize = this.outputByteBuffer.limit();
            if (outputSize == 0) {
                return -1;
            }
            writeLastSampleToOutput(outputSize, this.decoderJni.getLastFrameTimestamp());
            if (this.decoderJni.isEndOfData()) {
                return -1;
            }
            return 0;
        } catch (FlacDecoderJni.FlacFrameDecodeException e) {
            throw new IOException("Cannot read frame at position " + lastDecodePosition, e);
        }
    }

    public void seek(long position, long timeUs) {
        if (position == 0) {
            this.readPastStreamInfo = false;
        }
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        if (flacDecoderJni != null) {
            flacDecoderJni.reset(position);
        }
        FlacBinarySearchSeeker flacBinarySearchSeeker2 = this.flacBinarySearchSeeker;
        if (flacBinarySearchSeeker2 != null) {
            flacBinarySearchSeeker2.setSeekTargetUs(timeUs);
        }
    }

    public void release() {
        this.flacBinarySearchSeeker = null;
        FlacDecoderJni flacDecoderJni = this.decoderJni;
        if (flacDecoderJni != null) {
            flacDecoderJni.release();
            this.decoderJni = null;
        }
    }

    private Metadata peekId3Data(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        return this.id3Peeker.peekId3Data(input, this.isId3MetadataDisabled ? Id3Decoder.NO_FRAMES_PREDICATE : null);
    }

    private boolean peekFlacSignature(ExtractorInput input) throws IOException, InterruptedException {
        byte[] bArr = FLAC_SIGNATURE;
        byte[] header = new byte[bArr.length];
        input.peekFully(header, 0, bArr.length);
        return Arrays.equals(header, FLAC_SIGNATURE);
    }

    private void readPastStreamInfo(ExtractorInput input) throws InterruptedException, IOException {
        if (!this.readPastStreamInfo) {
            FlacStreamInfo streamInfo2 = decodeStreamInfo(input);
            this.readPastStreamInfo = true;
            if (this.streamInfo == null) {
                updateFlacStreamInfo(input, streamInfo2);
            }
        }
    }

    private void updateFlacStreamInfo(ExtractorInput input, FlacStreamInfo streamInfo2) {
        this.streamInfo = streamInfo2;
        outputSeekMap(input, streamInfo2);
        outputFormat(streamInfo2);
        ParsableByteArray parsableByteArray = new ParsableByteArray(streamInfo2.maxDecodedFrameSize());
        this.outputBuffer = parsableByteArray;
        ByteBuffer wrap = ByteBuffer.wrap(parsableByteArray.data);
        this.outputByteBuffer = wrap;
        this.outputFrameHolder = new BinarySearchSeeker.OutputFrameHolder(wrap);
    }

    private FlacStreamInfo decodeStreamInfo(ExtractorInput input) throws InterruptedException, IOException {
        try {
            FlacStreamInfo streamInfo2 = this.decoderJni.decodeMetadata();
            if (streamInfo2 != null) {
                return streamInfo2;
            }
            throw new IOException("Metadata decoding failed");
        } catch (IOException e) {
            this.decoderJni.reset(0);
            input.setRetryPosition(0, e);
            throw e;
        }
    }

    private void outputSeekMap(ExtractorInput input, FlacStreamInfo streamInfo2) {
        SeekMap seekMap;
        if (this.decoderJni.getSeekPosition(0) != -1) {
            seekMap = new FlacSeekMap(streamInfo2.durationUs(), this.decoderJni);
        } else {
            seekMap = getSeekMapForNonSeekTableFlac(input, streamInfo2);
        }
        this.extractorOutput.seekMap(seekMap);
    }

    private SeekMap getSeekMapForNonSeekTableFlac(ExtractorInput input, FlacStreamInfo streamInfo2) {
        long inputLength = input.getLength();
        if (inputLength == -1) {
            return new SeekMap.Unseekable(streamInfo2.durationUs());
        }
        FlacBinarySearchSeeker flacBinarySearchSeeker2 = new FlacBinarySearchSeeker(streamInfo2, this.decoderJni.getDecodePosition(), inputLength, this.decoderJni);
        this.flacBinarySearchSeeker = flacBinarySearchSeeker2;
        return flacBinarySearchSeeker2.getSeekMap();
    }

    private void outputFormat(FlacStreamInfo streamInfo2) {
        FlacStreamInfo flacStreamInfo = streamInfo2;
        this.trackOutput.format(Format.createAudioSampleFormat((String) null, MimeTypes.AUDIO_RAW, (String) null, streamInfo2.bitRate(), streamInfo2.maxDecodedFrameSize(), flacStreamInfo.channels, flacStreamInfo.sampleRate, Util.getPcmEncoding(flacStreamInfo.bitsPerSample), 0, 0, (List<byte[]>) null, (DrmInitData) null, 0, (String) null, this.isId3MetadataDisabled ? null : this.id3Metadata));
    }

    private int handlePendingSeek(ExtractorInput input, PositionHolder seekPosition) throws InterruptedException, IOException {
        int seekResult = this.flacBinarySearchSeeker.handlePendingSeek(input, seekPosition, this.outputFrameHolder);
        ByteBuffer outputByteBuffer2 = this.outputFrameHolder.byteBuffer;
        if (seekResult == 0 && outputByteBuffer2.limit() > 0) {
            writeLastSampleToOutput(outputByteBuffer2.limit(), this.outputFrameHolder.timeUs);
        }
        return seekResult;
    }

    private void writeLastSampleToOutput(int size, long lastSampleTimestamp) {
        this.outputBuffer.setPosition(0);
        this.trackOutput.sampleData(this.outputBuffer, size);
        this.trackOutput.sampleMetadata(lastSampleTimestamp, 1, size, 0, (TrackOutput.CryptoData) null);
    }

    private static final class FlacSeekMap implements SeekMap {
        private final FlacDecoderJni decoderJni;
        private final long durationUs;

        public FlacSeekMap(long durationUs2, FlacDecoderJni decoderJni2) {
            this.durationUs = durationUs2;
            this.decoderJni = decoderJni2;
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekMap.SeekPoints getSeekPoints(long timeUs) {
            return new SeekMap.SeekPoints(new SeekPoint(timeUs, this.decoderJni.getSeekPosition(timeUs)));
        }

        public long getDurationUs() {
            return this.durationUs;
        }
    }
}
