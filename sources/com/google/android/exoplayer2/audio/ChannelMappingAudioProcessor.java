package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.util.Assertions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

final class ChannelMappingAudioProcessor implements AudioProcessor {
    private boolean active;
    private ByteBuffer buffer = EMPTY_BUFFER;
    private int channelCount = -1;
    private boolean inputEnded;
    private ByteBuffer outputBuffer = EMPTY_BUFFER;
    private int[] outputChannels;
    private int[] pendingOutputChannels;
    private int sampleRateHz = -1;

    public void setChannelMap(int[] outputChannels2) {
        this.pendingOutputChannels = outputChannels2;
    }

    public boolean configure(int sampleRateHz2, int channelCount2, int encoding) throws AudioProcessor.UnhandledFormatException {
        boolean outputChannelsChanged = !Arrays.equals(this.pendingOutputChannels, this.outputChannels);
        int[] iArr = this.pendingOutputChannels;
        this.outputChannels = iArr;
        if (iArr == null) {
            this.active = false;
            return outputChannelsChanged;
        } else if (encoding != 2) {
            throw new AudioProcessor.UnhandledFormatException(sampleRateHz2, channelCount2, encoding);
        } else if (!outputChannelsChanged && this.sampleRateHz == sampleRateHz2 && this.channelCount == channelCount2) {
            return false;
        } else {
            this.sampleRateHz = sampleRateHz2;
            this.channelCount = channelCount2;
            this.active = channelCount2 != this.outputChannels.length;
            int i = 0;
            while (true) {
                int[] iArr2 = this.outputChannels;
                if (i >= iArr2.length) {
                    return true;
                }
                int channelIndex = iArr2[i];
                if (channelIndex < channelCount2) {
                    this.active |= channelIndex != i;
                    i++;
                } else {
                    throw new AudioProcessor.UnhandledFormatException(sampleRateHz2, channelCount2, encoding);
                }
            }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public int getOutputChannelCount() {
        int[] iArr = this.outputChannels;
        return iArr == null ? this.channelCount : iArr.length;
    }

    public int getOutputEncoding() {
        return 2;
    }

    public int getOutputSampleRateHz() {
        return this.sampleRateHz;
    }

    public void queueInput(ByteBuffer inputBuffer) {
        Assertions.checkState(this.outputChannels != null);
        int position = inputBuffer.position();
        int limit = inputBuffer.limit();
        int outputSize = this.outputChannels.length * ((limit - position) / (this.channelCount * 2)) * 2;
        if (this.buffer.capacity() < outputSize) {
            this.buffer = ByteBuffer.allocateDirect(outputSize).order(ByteOrder.nativeOrder());
        } else {
            this.buffer.clear();
        }
        while (position < limit) {
            for (int channelIndex : this.outputChannels) {
                this.buffer.putShort(inputBuffer.getShort((channelIndex * 2) + position));
            }
            position += this.channelCount * 2;
        }
        inputBuffer.position(limit);
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
        this.buffer = EMPTY_BUFFER;
        this.channelCount = -1;
        this.sampleRateHz = -1;
        this.outputChannels = null;
        this.pendingOutputChannels = null;
        this.active = false;
    }
}
