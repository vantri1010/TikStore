package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class SonicAudioProcessor implements AudioProcessor {
    private static final float CLOSE_THRESHOLD = 0.01f;
    public static final float MAXIMUM_PITCH = 8.0f;
    public static final float MAXIMUM_SPEED = 8.0f;
    public static final float MINIMUM_PITCH = 0.1f;
    public static final float MINIMUM_SPEED = 0.1f;
    private static final int MIN_BYTES_FOR_SPEEDUP_CALCULATION = 1024;
    public static final int SAMPLE_RATE_NO_CHANGE = -1;
    private ByteBuffer buffer;
    private int channelCount = -1;
    private long inputBytes;
    private boolean inputEnded;
    private ByteBuffer outputBuffer;
    private long outputBytes;
    private int outputSampleRateHz = -1;
    private int pendingOutputSampleRateHz;
    private float pitch = 1.0f;
    private int sampleRateHz = -1;
    private ShortBuffer shortBuffer;
    private Sonic sonic;
    private float speed = 1.0f;

    public SonicAudioProcessor() {
        ByteBuffer byteBuffer = EMPTY_BUFFER;
        this.buffer = byteBuffer;
        this.shortBuffer = byteBuffer.asShortBuffer();
        this.outputBuffer = EMPTY_BUFFER;
        this.pendingOutputSampleRateHz = -1;
    }

    public float setSpeed(float speed2) {
        float speed3 = Util.constrainValue(speed2, 0.1f, 8.0f);
        if (this.speed != speed3) {
            this.speed = speed3;
            this.sonic = null;
        }
        flush();
        return speed3;
    }

    public float setPitch(float pitch2) {
        float pitch3 = Util.constrainValue(pitch2, 0.1f, 8.0f);
        if (this.pitch != pitch3) {
            this.pitch = pitch3;
            this.sonic = null;
        }
        flush();
        return pitch3;
    }

    public void setOutputSampleRateHz(int sampleRateHz2) {
        this.pendingOutputSampleRateHz = sampleRateHz2;
    }

    public long scaleDurationForSpeedup(long duration) {
        long j = this.outputBytes;
        if (j < 1024) {
            return (long) (((double) this.speed) * ((double) duration));
        }
        int i = this.outputSampleRateHz;
        int i2 = this.sampleRateHz;
        if (i == i2) {
            return Util.scaleLargeTimestamp(duration, this.inputBytes, j);
        }
        return Util.scaleLargeTimestamp(duration, this.inputBytes * ((long) i), j * ((long) i2));
    }

    public boolean configure(int sampleRateHz2, int channelCount2, int encoding) throws AudioProcessor.UnhandledFormatException {
        if (encoding == 2) {
            int outputSampleRateHz2 = this.pendingOutputSampleRateHz;
            if (outputSampleRateHz2 == -1) {
                outputSampleRateHz2 = sampleRateHz2;
            }
            if (this.sampleRateHz == sampleRateHz2 && this.channelCount == channelCount2 && this.outputSampleRateHz == outputSampleRateHz2) {
                return false;
            }
            this.sampleRateHz = sampleRateHz2;
            this.channelCount = channelCount2;
            this.outputSampleRateHz = outputSampleRateHz2;
            this.sonic = null;
            return true;
        }
        throw new AudioProcessor.UnhandledFormatException(sampleRateHz2, channelCount2, encoding);
    }

    public boolean isActive() {
        return this.sampleRateHz != -1 && (Math.abs(this.speed - 1.0f) >= CLOSE_THRESHOLD || Math.abs(this.pitch - 1.0f) >= CLOSE_THRESHOLD || this.outputSampleRateHz != this.sampleRateHz);
    }

    public int getOutputChannelCount() {
        return this.channelCount;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.outputSampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        Assertions.checkState(this.sonic != null);
        if (inputBuffer.hasRemaining()) {
            ShortBuffer shortBuffer2 = inputBuffer.asShortBuffer();
            int inputSize = inputBuffer.remaining();
            this.inputBytes += (long) inputSize;
            this.sonic.queueInput(shortBuffer2);
            inputBuffer.position(inputBuffer.position() + inputSize);
        }
        int outputSize = this.sonic.getFramesAvailable() * this.channelCount * 2;
        if (outputSize > 0) {
            if (this.buffer.capacity() < outputSize) {
                ByteBuffer order = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder());
                this.buffer = order;
                this.shortBuffer = order.asShortBuffer();
            } else {
                this.buffer.clear();
                this.shortBuffer.clear();
            }
            this.sonic.getOutput(this.shortBuffer);
            this.outputBytes += (long) outputSize;
            this.buffer.limit(outputSize);
            this.outputBuffer = this.buffer;
        }
    }

    public void queueEndOfStream() {
        Assertions.checkState(this.sonic != null);
        this.sonic.queueEndOfStream();
        this.inputEnded = true;
    }

    public ByteBuffer getOutput() {
        ByteBuffer outputBuffer2 = this.outputBuffer;
        this.outputBuffer = EMPTY_BUFFER;
        return outputBuffer2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.sonic;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isEnded() {
        /*
            r1 = this;
            boolean r0 = r1.inputEnded
            if (r0 == 0) goto L_0x0010
            com.google.android.exoplayer2.audio.Sonic r0 = r1.sonic
            if (r0 == 0) goto L_0x000e
            int r0 = r0.getFramesAvailable()
            if (r0 != 0) goto L_0x0010
        L_0x000e:
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.SonicAudioProcessor.isEnded():boolean");
    }

    public void flush() {
        if (isActive()) {
            Sonic sonic2 = this.sonic;
            if (sonic2 == null) {
                this.sonic = new Sonic(this.sampleRateHz, this.channelCount, this.speed, this.pitch, this.outputSampleRateHz);
            } else {
                sonic2.flush();
            }
        }
        this.outputBuffer = EMPTY_BUFFER;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
    }

    public void reset() {
        this.speed = 1.0f;
        this.pitch = 1.0f;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputSampleRateHz = -1;
        ByteBuffer byteBuffer = EMPTY_BUFFER;
        this.buffer = byteBuffer;
        this.shortBuffer = byteBuffer.asShortBuffer();
        this.outputBuffer = EMPTY_BUFFER;
        this.pendingOutputSampleRateHz = -1;
        this.sonic = null;
        this.inputBytes = 0;
        this.outputBytes = 0;
        this.inputEnded = false;
    }
}
