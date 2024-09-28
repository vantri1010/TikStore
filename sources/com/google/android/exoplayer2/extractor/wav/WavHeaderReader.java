package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.WavUtil;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class WavHeaderReader {
    private static final String TAG = "WavHeaderReader";

    public static WavHeader peek(ExtractorInput input) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        Assertions.checkNotNull(input);
        ParsableByteArray scratch = new ParsableByteArray(16);
        if (ChunkHeader.peek(extractorInput, scratch).id != WavUtil.RIFF_FOURCC) {
            return null;
        }
        extractorInput.peekFully(scratch.data, 0, 4);
        scratch.setPosition(0);
        int riffFormat = scratch.readInt();
        if (riffFormat != WavUtil.WAVE_FOURCC) {
            Log.e(TAG, "Unsupported RIFF format: " + riffFormat);
            return null;
        }
        ChunkHeader chunkHeader = ChunkHeader.peek(extractorInput, scratch);
        while (chunkHeader.id != WavUtil.FMT_FOURCC) {
            extractorInput.advancePeekPosition((int) chunkHeader.size);
            chunkHeader = ChunkHeader.peek(extractorInput, scratch);
        }
        Assertions.checkState(chunkHeader.size >= 16);
        extractorInput.peekFully(scratch.data, 0, 16);
        scratch.setPosition(0);
        int type = scratch.readLittleEndianUnsignedShort();
        int numChannels = scratch.readLittleEndianUnsignedShort();
        int sampleRateHz = scratch.readLittleEndianUnsignedIntToInt();
        int averageBytesPerSecond = scratch.readLittleEndianUnsignedIntToInt();
        int blockAlignment = scratch.readLittleEndianUnsignedShort();
        int bitsPerSample = scratch.readLittleEndianUnsignedShort();
        int expectedBlockAlignment = (numChannels * bitsPerSample) / 8;
        if (blockAlignment == expectedBlockAlignment) {
            int encoding = WavUtil.getEncodingForType(type, bitsPerSample);
            if (encoding == 0) {
                Log.e(TAG, "Unsupported WAV format: " + bitsPerSample + " bit/sample, type " + type);
                return null;
            }
            extractorInput.advancePeekPosition(((int) chunkHeader.size) - 16);
            int i = expectedBlockAlignment;
            int i2 = bitsPerSample;
            int i3 = blockAlignment;
            return new WavHeader(numChannels, sampleRateHz, averageBytesPerSecond, blockAlignment, bitsPerSample, encoding);
        }
        throw new ParserException("Expected block alignment: " + expectedBlockAlignment + "; got: " + blockAlignment);
    }

    public static void skipToData(ExtractorInput input, WavHeader wavHeader) throws IOException, InterruptedException {
        Assertions.checkNotNull(input);
        Assertions.checkNotNull(wavHeader);
        input.resetPeekPosition();
        ParsableByteArray scratch = new ParsableByteArray(8);
        ChunkHeader chunkHeader = ChunkHeader.peek(input, scratch);
        while (chunkHeader.id != Util.getIntegerCodeForString("data")) {
            Log.w(TAG, "Ignoring unknown WAV chunk: " + chunkHeader.id);
            long bytesToSkip = chunkHeader.size + 8;
            if (chunkHeader.id == Util.getIntegerCodeForString("RIFF")) {
                bytesToSkip = 12;
            }
            if (bytesToSkip <= 2147483647L) {
                input.skipFully((int) bytesToSkip);
                chunkHeader = ChunkHeader.peek(input, scratch);
            } else {
                throw new ParserException("Chunk is too large (~2GB+) to skip; id: " + chunkHeader.id);
            }
        }
        input.skipFully(8);
        wavHeader.setDataBounds(input.getPosition(), chunkHeader.size);
    }

    private WavHeaderReader() {
    }

    private static final class ChunkHeader {
        public static final int SIZE_IN_BYTES = 8;
        public final int id;
        public final long size;

        private ChunkHeader(int id2, long size2) {
            this.id = id2;
            this.size = size2;
        }

        public static ChunkHeader peek(ExtractorInput input, ParsableByteArray scratch) throws IOException, InterruptedException {
            input.peekFully(scratch.data, 0, 8);
            scratch.setPosition(0);
            return new ChunkHeader(scratch.readInt(), scratch.readLittleEndianUnsignedInt());
        }
    }
}
