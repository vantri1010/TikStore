package com.google.android.exoplayer2.ext.flac;

import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.ext.flac.FlacDecoderJni;
import com.google.android.exoplayer2.util.FlacStreamInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

final class FlacDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException> {
    private final FlacDecoderJni decoderJni;
    private final int maxOutputBufferSize;

    public FlacDecoder(int numInputBuffers, int numOutputBuffers, int maxInputBufferSize, List<byte[]> initializationData) throws FlacDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        if (initializationData.size() == 1) {
            FlacDecoderJni flacDecoderJni = new FlacDecoderJni();
            this.decoderJni = flacDecoderJni;
            flacDecoderJni.setData(ByteBuffer.wrap(initializationData.get(0)));
            try {
                FlacStreamInfo streamInfo = this.decoderJni.decodeMetadata();
                if (streamInfo != null) {
                    setInitialInputBufferSize(maxInputBufferSize != -1 ? maxInputBufferSize : streamInfo.maxFrameSize);
                    this.maxOutputBufferSize = streamInfo.maxDecodedFrameSize();
                    return;
                }
                throw new FlacDecoderException("Metadata decoding failed");
            } catch (IOException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new FlacDecoderException("Initialization data must be of length 1");
        }
    }

    public String getName() {
        return "libflac";
    }

    /* access modifiers changed from: protected */
    public DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    /* access modifiers changed from: protected */
    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    /* access modifiers changed from: protected */
    public FlacDecoderException createUnexpectedDecodeException(Throwable error) {
        return new FlacDecoderException("Unexpected decode error", error);
    }

    /* access modifiers changed from: protected */
    public FlacDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        if (reset) {
            this.decoderJni.flush();
        }
        this.decoderJni.setData(inputBuffer.data);
        try {
            this.decoderJni.decodeSample(outputBuffer.init(inputBuffer.timeUs, this.maxOutputBufferSize));
            return null;
        } catch (FlacDecoderJni.FlacFrameDecodeException e) {
            return new FlacDecoderException("Frame decoding failed", e);
        } catch (IOException | InterruptedException e2) {
            throw new IllegalStateException(e2);
        }
    }

    public void release() {
        super.release();
        this.decoderJni.release();
    }
}
