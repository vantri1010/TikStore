package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import kotlin.UByte;

final class FloatResamplingAudioProcessor implements AudioProcessor {
    private static final int FLOAT_NAN_AS_INT = Float.floatToIntBits(Float.NaN);
    private static final double PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR = 4.656612875245797E-10d;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int sampleRateHz = -1;
    private int sourceEncoding = 0;

    public boolean configure(int sampleRateHz2, int channelCount2, int encoding) throws AudioProcessor.UnhandledFormatException {
        if (!Util.isEncodingHighResolutionIntegerPcm(encoding)) {
            throw new AudioProcessor.UnhandledFormatException(sampleRateHz2, channelCount2, encoding);
        } else if (this.sampleRateHz == sampleRateHz2 && this.channelCount == channelCount2 && this.sourceEncoding == encoding) {
            return false;
        } else {
            this.sampleRateHz = sampleRateHz2;
            this.channelCount = channelCount2;
            this.sourceEncoding = encoding;
            return true;
        }
    }

    public boolean isActive() {
        return Util.isEncodingHighResolutionIntegerPcm(this.sourceEncoding);
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 4;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        boolean isInput32Bit = this.sourceEncoding == 1073741824;
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int size = limit - position;
        int resampledSize = isInput32Bit ? size : (size / 3) * 4;
        if (this.buffer.capacity() < resampledSize) {
            this.buffer = ByteBuffer.allocateDirect(resampledSize).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        if (isInput32Bit) {
            for (int i = position; i < limit; i += 4) {
                writePcm32BitFloat((inputBuffer.get(i) & 255) | ((inputBuffer.get(i + 1) & UByte.MAX_VALUE) << 8) | ((inputBuffer.get(i + 2) & UByte.MAX_VALUE) << 16) | ((inputBuffer.get(i + 3) & UByte.MAX_VALUE) << 24), this.buffer);
            }
        } else {
            for (int i2 = position; i2 < limit; i2 += 3) {
                writePcm32BitFloat(((inputBuffer.get(i2) & UByte.MAX_VALUE) << 8) | ((inputBuffer.get(i2 + 1) & UByte.MAX_VALUE) << 16) | ((inputBuffer.get(i2 + 2) & UByte.MAX_VALUE) << 24), this.buffer);
            }
        }
        inputBuffer.position(inputBuffer.limit());
        this.buffer.flip();
        this.outputBuffer = this.buffer;
    }

    public void queueEndOfStream() {
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer2 = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer2;
    }

    public boolean isEnded() {
        return this.inputEnded && this.outputBuffer == EMPTY_BUFFER;
    }

    public void flush() {
        this.outputBuffer = EMPTY_BUFFER;
        this.inputEnded = false;
    }

    public void reset() {
        flush();
        this.sampleRateHz = -1;
        this.channelCount = -1;
        this.sourceEncoding = 0;
        this.buffer = EMPTY_BUFFER;
    }

    private static void writePcm32BitFloat(int pcm32BitInt, ByteBuffer buffer2) {
        int floatBits = Float.floatToIntBits((float) (((double) pcm32BitInt) * PCM_32_BIT_INT_TO_PCM_32_BIT_FLOAT_FACTOR));
        if (floatBits == FLOAT_NAN_AS_INT) {
            floatBits = Float.floatToIntBits(0.0f);
        }
        buffer2.putInt(floatBits);
    }
}
