package com.google.android.exoplayer2.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.ConditionVariable;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.AudioTrackPositionTracker;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class DefaultAudioSink implements AudioSink {
    private static final int AC3_BUFFER_MULTIPLICATION_FACTOR = 2;
    private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
    private static final int ERROR_BAD_VALUE = -2;
    private static final long MAX_BUFFER_DURATION_US = 750000;
    private static final long MIN_BUFFER_DURATION_US = 250000;
    private static final int MODE_STATIC = 0;
    private static final int MODE_STREAM = 1;
    private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000;
    private static final int START_IN_SYNC = 1;
    private static final int START_NEED_SYNC = 2;
    private static final int START_NOT_SET = 0;
    private static final int STATE_INITIALIZED = 1;
    private static final String TAG = "AudioTrack";
    private static final int WRITE_NON_BLOCKING = 1;
    public static boolean enablePreV21AudioSessionWorkaround = false;
    public static boolean failOnSpuriousAudioTimestamp = false;
    private AudioProcessor[] activeAudioProcessors;
    private PlaybackParameters afterDrainPlaybackParameters;
    private AudioAttributes audioAttributes;
    private final AudioCapabilities audioCapabilities;
    private final AudioProcessorChain audioProcessorChain;
    private int audioSessionId;
    private AudioTrack audioTrack;
    private final AudioTrackPositionTracker audioTrackPositionTracker;
    private AuxEffectInfo auxEffectInfo;
    private ByteBuffer avSyncHeader;
    private int bufferSize;
    private int bytesUntilNextAvSync;
    private boolean canApplyPlaybackParameters;
    private final ChannelMappingAudioProcessor channelMappingAudioProcessor;
    private int drainingAudioProcessorIndex;
    private final boolean enableConvertHighResIntPcmToFloat;
    private int framesPerEncodedSample;
    private boolean handledEndOfStream;
    private ByteBuffer inputBuffer;
    private int inputSampleRate;
    private boolean isInputPcm;
    private AudioTrack keepSessionIdAudioTrack;
    /* access modifiers changed from: private */
    public long lastFeedElapsedRealtimeMs;
    /* access modifiers changed from: private */
    public AudioSink.Listener listener;
    private ByteBuffer outputBuffer;
    private ByteBuffer[] outputBuffers;
    private int outputChannelConfig;
    private int outputEncoding;
    private int outputPcmFrameSize;
    private int outputSampleRate;
    private int pcmFrameSize;
    private PlaybackParameters playbackParameters;
    private final ArrayDeque<PlaybackParametersCheckpoint> playbackParametersCheckpoints;
    private long playbackParametersOffsetUs;
    private long playbackParametersPositionUs;
    private boolean playing;
    private byte[] preV21OutputBuffer;
    private int preV21OutputBufferOffset;
    private boolean processingEnabled;
    /* access modifiers changed from: private */
    public final ConditionVariable releasingConditionVariable;
    private boolean shouldConvertHighResIntPcmToFloat;
    private int startMediaTimeState;
    private long startMediaTimeUs;
    private long submittedEncodedFrames;
    private long submittedPcmBytes;
    private final AudioProcessor[] toFloatPcmAvailableAudioProcessors;
    private final AudioProcessor[] toIntPcmAvailableAudioProcessors;
    private final TrimmingAudioProcessor trimmingAudioProcessor;
    private boolean tunneling;
    private float volume;
    private long writtenEncodedFrames;
    private long writtenPcmBytes;

    public interface AudioProcessorChain {
        PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters);

        AudioProcessor[] getAudioProcessors();

        long getMediaDuration(long j);

        long getSkippedOutputFrameCount();
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface StartMediaTimeState {
    }

    public static final class InvalidAudioTrackTimestampException extends RuntimeException {
        private InvalidAudioTrackTimestampException(String message) {
            super(message);
        }
    }

    public static class DefaultAudioProcessorChain implements AudioProcessorChain {
        private final AudioProcessor[] audioProcessors;
        private final SilenceSkippingAudioProcessor silenceSkippingAudioProcessor = new SilenceSkippingAudioProcessor();
        private final SonicAudioProcessor sonicAudioProcessor;

        public DefaultAudioProcessorChain(AudioProcessor... audioProcessors2) {
            this.audioProcessors = (AudioProcessor[]) Arrays.copyOf(audioProcessors2, audioProcessors2.length + 2);
            SonicAudioProcessor sonicAudioProcessor2 = new SonicAudioProcessor();
            this.sonicAudioProcessor = sonicAudioProcessor2;
            AudioProcessor[] audioProcessorArr = this.audioProcessors;
            audioProcessorArr[audioProcessors2.length] = this.silenceSkippingAudioProcessor;
            audioProcessorArr[audioProcessors2.length + 1] = sonicAudioProcessor2;
        }

        public AudioProcessor[] getAudioProcessors() {
            return this.audioProcessors;
        }

        public PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters) {
            this.silenceSkippingAudioProcessor.setEnabled(playbackParameters.skipSilence);
            return new PlaybackParameters(this.sonicAudioProcessor.setSpeed(playbackParameters.speed), this.sonicAudioProcessor.setPitch(playbackParameters.pitch), playbackParameters.skipSilence);
        }

        public long getMediaDuration(long playoutDuration) {
            return this.sonicAudioProcessor.scaleDurationForSpeedup(playoutDuration);
        }

        public long getSkippedOutputFrameCount() {
            return this.silenceSkippingAudioProcessor.getSkippedFrames();
        }
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities2, AudioProcessor[] audioProcessors) {
        this(audioCapabilities2, audioProcessors, false);
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities2, AudioProcessor[] audioProcessors, boolean enableConvertHighResIntPcmToFloat2) {
        this(audioCapabilities2, (AudioProcessorChain) new DefaultAudioProcessorChain(audioProcessors), enableConvertHighResIntPcmToFloat2);
    }

    public DefaultAudioSink(AudioCapabilities audioCapabilities2, AudioProcessorChain audioProcessorChain2, boolean enableConvertHighResIntPcmToFloat2) {
        this.audioCapabilities = audioCapabilities2;
        this.audioProcessorChain = (AudioProcessorChain) Assertions.checkNotNull(audioProcessorChain2);
        this.enableConvertHighResIntPcmToFloat = enableConvertHighResIntPcmToFloat2;
        this.releasingConditionVariable = new ConditionVariable(true);
        this.audioTrackPositionTracker = new AudioTrackPositionTracker(new PositionTrackerListener());
        this.channelMappingAudioProcessor = new ChannelMappingAudioProcessor();
        this.trimmingAudioProcessor = new TrimmingAudioProcessor();
        ArrayList<AudioProcessor> toIntPcmAudioProcessors = new ArrayList<>();
        Collections.addAll(toIntPcmAudioProcessors, new AudioProcessor[]{new ResamplingAudioProcessor(), this.channelMappingAudioProcessor, this.trimmingAudioProcessor});
        Collections.addAll(toIntPcmAudioProcessors, audioProcessorChain2.getAudioProcessors());
        this.toIntPcmAvailableAudioProcessors = (AudioProcessor[]) toIntPcmAudioProcessors.toArray(new AudioProcessor[0]);
        this.toFloatPcmAvailableAudioProcessors = new AudioProcessor[]{new FloatResamplingAudioProcessor()};
        this.volume = 1.0f;
        this.startMediaTimeState = 0;
        this.audioAttributes = AudioAttributes.DEFAULT;
        this.audioSessionId = 0;
        this.auxEffectInfo = new AuxEffectInfo(0, 0.0f);
        this.playbackParameters = PlaybackParameters.DEFAULT;
        this.drainingAudioProcessorIndex = -1;
        this.activeAudioProcessors = new AudioProcessor[0];
        this.outputBuffers = new ByteBuffer[0];
        this.playbackParametersCheckpoints = new ArrayDeque<>();
    }

    public void setListener(AudioSink.Listener listener2) {
        this.listener = listener2;
    }

    public boolean supportsOutput(int channelCount, int encoding) {
        if (!Util.isEncodingLinearPcm(encoding)) {
            AudioCapabilities audioCapabilities2 = this.audioCapabilities;
            if (audioCapabilities2 == null || !audioCapabilities2.supportsEncoding(encoding) || (channelCount != -1 && channelCount > this.audioCapabilities.getMaxChannelCount())) {
                return false;
            }
            return true;
        } else if (encoding != 4 || Util.SDK_INT >= 21) {
            return true;
        } else {
            return false;
        }
    }

    public long getCurrentPositionUs(boolean sourceEnded) {
        if (!isInitialized() || this.startMediaTimeState == 0) {
            return Long.MIN_VALUE;
        }
        return this.startMediaTimeUs + applySkipping(applySpeedup(Math.min(this.audioTrackPositionTracker.getCurrentPositionUs(sourceEnded), framesToDurationUs(getWrittenFrames()))));
    }

    public void configure(int inputEncoding, int inputChannelCount, int inputSampleRate2, int specifiedBufferSize, int[] outputChannels, int trimStartFrames, int trimEndFrames) throws AudioSink.ConfigurationException {
        int[] outputChannels2;
        int i = inputEncoding;
        boolean flush = false;
        this.inputSampleRate = inputSampleRate2;
        int channelCount = inputChannelCount;
        int channelCount2 = inputSampleRate2;
        this.isInputPcm = Util.isEncodingLinearPcm(inputEncoding);
        boolean z = true;
        this.shouldConvertHighResIntPcmToFloat = this.enableConvertHighResIntPcmToFloat && supportsOutput(channelCount, 1073741824) && Util.isEncodingHighResolutionIntegerPcm(inputEncoding);
        if (this.isInputPcm) {
            this.pcmFrameSize = Util.getPcmFrameSize(i, channelCount);
        }
        int sampleRate = inputEncoding;
        boolean processingEnabled2 = this.isInputPcm && i != 4;
        if (!processingEnabled2 || this.shouldConvertHighResIntPcmToFloat) {
            z = false;
        }
        this.canApplyPlaybackParameters = z;
        if (Util.SDK_INT < 21 && channelCount == 8 && outputChannels == null) {
            outputChannels2 = new int[6];
            for (int i2 = 0; i2 < outputChannels2.length; i2++) {
                outputChannels2[i2] = i2;
            }
        } else {
            outputChannels2 = outputChannels;
        }
        if (processingEnabled2) {
            this.trimmingAudioProcessor.setTrimFrameCount(trimStartFrames, trimEndFrames);
            this.channelMappingAudioProcessor.setChannelMap(outputChannels2);
            AudioProcessor[] availableAudioProcessors = getAvailableAudioProcessors();
            int length = availableAudioProcessors.length;
            int encoding = sampleRate;
            int sampleRate2 = channelCount2;
            int channelCount3 = channelCount;
            boolean flush2 = false;
            int i3 = 0;
            while (i3 < length) {
                AudioProcessor audioProcessor = availableAudioProcessors[i3];
                try {
                    flush2 |= audioProcessor.configure(sampleRate2, channelCount3, encoding);
                    if (audioProcessor.isActive()) {
                        channelCount3 = audioProcessor.getOutputChannelCount();
                        sampleRate2 = audioProcessor.getOutputSampleRateHz();
                        encoding = audioProcessor.getOutputEncoding();
                    }
                    i3++;
                } catch (AudioProcessor.UnhandledFormatException e) {
                    throw new AudioSink.ConfigurationException((Throwable) e);
                }
            }
            flush = flush2;
            channelCount = channelCount3;
            channelCount2 = sampleRate2;
            sampleRate = encoding;
        } else {
            int i4 = trimStartFrames;
            int i5 = trimEndFrames;
        }
        int channelConfig = getChannelConfig(channelCount, this.isInputPcm);
        if (channelConfig == 0) {
            throw new AudioSink.ConfigurationException("Unsupported channel count: " + channelCount);
        } else if (flush || !isInitialized() || this.outputEncoding != sampleRate || this.outputSampleRate != channelCount2 || this.outputChannelConfig != channelConfig) {
            flush();
            this.processingEnabled = processingEnabled2;
            this.outputSampleRate = channelCount2;
            this.outputChannelConfig = channelConfig;
            this.outputEncoding = sampleRate;
            this.outputPcmFrameSize = this.isInputPcm ? Util.getPcmFrameSize(sampleRate, channelCount) : -1;
            this.bufferSize = specifiedBufferSize != 0 ? specifiedBufferSize : getDefaultBufferSize();
        }
    }

    private int getDefaultBufferSize() {
        if (this.isInputPcm) {
            int minBufferSize = AudioTrack.getMinBufferSize(this.outputSampleRate, this.outputChannelConfig, this.outputEncoding);
            Assertions.checkState(minBufferSize != -2);
            return Util.constrainValue(minBufferSize * 4, ((int) durationUsToFrames(250000)) * this.outputPcmFrameSize, (int) Math.max((long) minBufferSize, durationUsToFrames(MAX_BUFFER_DURATION_US) * ((long) this.outputPcmFrameSize)));
        }
        int rate = getMaximumEncodedRateBytesPerSecond(this.outputEncoding);
        if (this.outputEncoding == 5) {
            rate *= 2;
        }
        return (int) ((((long) rate) * 250000) / 1000000);
    }

    private void setupAudioProcessors() {
        ArrayList<AudioProcessor> newAudioProcessors = new ArrayList<>();
        for (AudioProcessor audioProcessor : getAvailableAudioProcessors()) {
            if (audioProcessor.isActive()) {
                newAudioProcessors.add(audioProcessor);
            } else {
                audioProcessor.flush();
            }
        }
        int count = newAudioProcessors.size();
        this.activeAudioProcessors = (AudioProcessor[]) newAudioProcessors.toArray(new AudioProcessor[count]);
        this.outputBuffers = new ByteBuffer[count];
        flushAudioProcessors();
    }

    private void flushAudioProcessors() {
        int i = 0;
        while (true) {
            AudioProcessor[] audioProcessorArr = this.activeAudioProcessors;
            if (i < audioProcessorArr.length) {
                AudioProcessor audioProcessor = audioProcessorArr[i];
                audioProcessor.flush();
                this.outputBuffers[i] = audioProcessor.getOutput();
                i++;
            } else {
                return;
            }
        }
    }

    private void initialize() throws AudioSink.InitializationException {
        this.releasingConditionVariable.block();
        AudioTrack initializeAudioTrack = initializeAudioTrack();
        this.audioTrack = initializeAudioTrack;
        int audioSessionId2 = initializeAudioTrack.getAudioSessionId();
        if (enablePreV21AudioSessionWorkaround && Util.SDK_INT < 21) {
            AudioTrack audioTrack2 = this.keepSessionIdAudioTrack;
            if (!(audioTrack2 == null || audioSessionId2 == audioTrack2.getAudioSessionId())) {
                releaseKeepSessionIdAudioTrack();
            }
            if (this.keepSessionIdAudioTrack == null) {
                this.keepSessionIdAudioTrack = initializeKeepSessionIdAudioTrack(audioSessionId2);
            }
        }
        if (this.audioSessionId != audioSessionId2) {
            this.audioSessionId = audioSessionId2;
            AudioSink.Listener listener2 = this.listener;
            if (listener2 != null) {
                listener2.onAudioSessionId(audioSessionId2);
            }
        }
        this.playbackParameters = this.canApplyPlaybackParameters ? this.audioProcessorChain.applyPlaybackParameters(this.playbackParameters) : PlaybackParameters.DEFAULT;
        setupAudioProcessors();
        this.audioTrackPositionTracker.setAudioTrack(this.audioTrack, this.outputEncoding, this.outputPcmFrameSize, this.bufferSize);
        setVolumeInternal();
        if (this.auxEffectInfo.effectId != 0) {
            this.audioTrack.attachAuxEffect(this.auxEffectInfo.effectId);
            this.audioTrack.setAuxEffectSendLevel(this.auxEffectInfo.sendLevel);
        }
    }

    public void play() {
        this.playing = true;
        if (isInitialized()) {
            this.audioTrackPositionTracker.start();
            this.audioTrack.play();
        }
    }

    public void handleDiscontinuity() {
        if (this.startMediaTimeState == 1) {
            this.startMediaTimeState = 2;
        }
    }

    public boolean handleBuffer(ByteBuffer buffer, long presentationTimeUs) throws AudioSink.InitializationException, AudioSink.WriteException {
        ByteBuffer byteBuffer = buffer;
        long j = presentationTimeUs;
        ByteBuffer byteBuffer2 = this.inputBuffer;
        Assertions.checkArgument(byteBuffer2 == null || byteBuffer == byteBuffer2);
        if (!isInitialized()) {
            initialize();
            if (this.playing) {
                play();
            }
        }
        if (!this.audioTrackPositionTracker.mayHandleBuffer(getWrittenFrames())) {
            return false;
        }
        if (this.inputBuffer == null) {
            if (!buffer.hasRemaining()) {
                return true;
            }
            if (!this.isInputPcm && this.framesPerEncodedSample == 0) {
                int framesPerEncodedSample2 = getFramesPerEncodedSample(this.outputEncoding, byteBuffer);
                this.framesPerEncodedSample = framesPerEncodedSample2;
                if (framesPerEncodedSample2 == 0) {
                    return true;
                }
            }
            if (this.afterDrainPlaybackParameters != null) {
                if (!drainAudioProcessorsToEndOfStream()) {
                    return false;
                }
                PlaybackParameters newPlaybackParameters = this.afterDrainPlaybackParameters;
                this.afterDrainPlaybackParameters = null;
                PlaybackParameters newPlaybackParameters2 = this.audioProcessorChain.applyPlaybackParameters(newPlaybackParameters);
                ArrayDeque<PlaybackParametersCheckpoint> arrayDeque = this.playbackParametersCheckpoints;
                PlaybackParametersCheckpoint playbackParametersCheckpoint = r12;
                PlaybackParametersCheckpoint playbackParametersCheckpoint2 = new PlaybackParametersCheckpoint(newPlaybackParameters2, Math.max(0, j), framesToDurationUs(getWrittenFrames()));
                arrayDeque.add(playbackParametersCheckpoint);
                setupAudioProcessors();
            }
            if (this.startMediaTimeState == 0) {
                this.startMediaTimeUs = Math.max(0, j);
                this.startMediaTimeState = 1;
            } else {
                long expectedPresentationTimeUs = this.startMediaTimeUs + inputFramesToDurationUs(getSubmittedFrames() - this.trimmingAudioProcessor.getTrimmedFrameCount());
                if (this.startMediaTimeState == 1 && Math.abs(expectedPresentationTimeUs - j) > 200000) {
                    Log.e(TAG, "Discontinuity detected [expected " + expectedPresentationTimeUs + ", got " + j + "]");
                    this.startMediaTimeState = 2;
                }
                if (this.startMediaTimeState == 2) {
                    long adjustmentUs = j - expectedPresentationTimeUs;
                    this.startMediaTimeUs += adjustmentUs;
                    this.startMediaTimeState = 1;
                    AudioSink.Listener listener2 = this.listener;
                    if (!(listener2 == null || adjustmentUs == 0)) {
                        listener2.onPositionDiscontinuity();
                    }
                }
            }
            if (this.isInputPcm) {
                this.submittedPcmBytes += (long) buffer.remaining();
            } else {
                this.submittedEncodedFrames += (long) this.framesPerEncodedSample;
            }
            this.inputBuffer = byteBuffer;
        }
        if (this.processingEnabled) {
            processBuffers(j);
        } else {
            writeBuffer(this.inputBuffer, j);
        }
        if (!this.inputBuffer.hasRemaining()) {
            this.inputBuffer = null;
            return true;
        } else if (!this.audioTrackPositionTracker.isStalled(getWrittenFrames())) {
            return false;
        } else {
            Log.w(TAG, "Resetting stalled audio track");
            flush();
            return true;
        }
    }

    private void processBuffers(long avSyncPresentationTimeUs) throws AudioSink.WriteException {
        ByteBuffer input;
        int count = this.activeAudioProcessors.length;
        int index = count;
        while (index >= 0) {
            if (index > 0) {
                input = this.outputBuffers[index - 1];
            } else {
                input = this.inputBuffer;
                if (input == null) {
                    input = AudioProcessor.EMPTY_BUFFER;
                }
            }
            if (index == count) {
                writeBuffer(input, avSyncPresentationTimeUs);
            } else {
                AudioProcessor audioProcessor = this.activeAudioProcessors[index];
                audioProcessor.queueInput(input);
                ByteBuffer output = audioProcessor.getOutput();
                this.outputBuffers[index] = output;
                if (output.hasRemaining()) {
                    index++;
                }
            }
            if (!input.hasRemaining()) {
                index--;
            } else {
                return;
            }
        }
    }

    private void writeBuffer(ByteBuffer buffer, long avSyncPresentationTimeUs) throws AudioSink.WriteException {
        if (buffer.hasRemaining()) {
            ByteBuffer byteBuffer = this.outputBuffer;
            boolean z = true;
            if (byteBuffer != null) {
                Assertions.checkArgument(byteBuffer == buffer);
            } else {
                this.outputBuffer = buffer;
                if (Util.SDK_INT < 21) {
                    int bytesRemaining = buffer.remaining();
                    byte[] bArr = this.preV21OutputBuffer;
                    if (bArr == null || bArr.length < bytesRemaining) {
                        this.preV21OutputBuffer = new byte[bytesRemaining];
                    }
                    int originalPosition = buffer.position();
                    buffer.get(this.preV21OutputBuffer, 0, bytesRemaining);
                    buffer.position(originalPosition);
                    this.preV21OutputBufferOffset = 0;
                }
            }
            int bytesRemaining2 = buffer.remaining();
            int bytesWritten = 0;
            if (Util.SDK_INT < 21) {
                int bytesToWrite = this.audioTrackPositionTracker.getAvailableBufferSize(this.writtenPcmBytes);
                if (bytesToWrite > 0 && (bytesWritten = this.audioTrack.write(this.preV21OutputBuffer, this.preV21OutputBufferOffset, Math.min(bytesRemaining2, bytesToWrite))) > 0) {
                    this.preV21OutputBufferOffset += bytesWritten;
                    buffer.position(buffer.position() + bytesWritten);
                }
            } else if (this.tunneling) {
                if (avSyncPresentationTimeUs == C.TIME_UNSET) {
                    z = false;
                }
                Assertions.checkState(z);
                bytesWritten = writeNonBlockingWithAvSyncV21(this.audioTrack, buffer, bytesRemaining2, avSyncPresentationTimeUs);
            } else {
                bytesWritten = writeNonBlockingV21(this.audioTrack, buffer, bytesRemaining2);
            }
            this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
            if (bytesWritten >= 0) {
                if (this.isInputPcm) {
                    this.writtenPcmBytes += (long) bytesWritten;
                }
                if (bytesWritten == bytesRemaining2) {
                    if (!this.isInputPcm) {
                        this.writtenEncodedFrames += (long) this.framesPerEncodedSample;
                    }
                    this.outputBuffer = null;
                    return;
                }
                return;
            }
            throw new AudioSink.WriteException(bytesWritten);
        }
    }

    public void playToEndOfStream() throws AudioSink.WriteException {
        if (!this.handledEndOfStream && isInitialized() && drainAudioProcessorsToEndOfStream()) {
            this.audioTrackPositionTracker.handleEndOfStream(getWrittenFrames());
            this.audioTrack.stop();
            this.bytesUntilNextAvSync = 0;
            this.handledEndOfStream = true;
        }
    }

    private boolean drainAudioProcessorsToEndOfStream() throws AudioSink.WriteException {
        boolean audioProcessorNeedsEndOfStream = false;
        if (this.drainingAudioProcessorIndex == -1) {
            this.drainingAudioProcessorIndex = this.processingEnabled ? 0 : this.activeAudioProcessors.length;
            audioProcessorNeedsEndOfStream = true;
        }
        while (true) {
            int i = this.drainingAudioProcessorIndex;
            AudioProcessor[] audioProcessorArr = this.activeAudioProcessors;
            if (i < audioProcessorArr.length) {
                AudioProcessor audioProcessor = audioProcessorArr[i];
                if (audioProcessorNeedsEndOfStream) {
                    audioProcessor.queueEndOfStream();
                }
                processBuffers(C.TIME_UNSET);
                if (!audioProcessor.isEnded()) {
                    return false;
                }
                audioProcessorNeedsEndOfStream = true;
                this.drainingAudioProcessorIndex++;
            } else {
                ByteBuffer byteBuffer = this.outputBuffer;
                if (byteBuffer != null) {
                    writeBuffer(byteBuffer, C.TIME_UNSET);
                    if (this.outputBuffer != null) {
                        return false;
                    }
                }
                this.drainingAudioProcessorIndex = -1;
                return true;
            }
        }
    }

    public boolean isEnded() {
        return !isInitialized() || (this.handledEndOfStream && !hasPendingData());
    }

    public boolean hasPendingData() {
        return isInitialized() && this.audioTrackPositionTracker.hasPendingData(getWrittenFrames());
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters2) {
        if (!isInitialized() || this.canApplyPlaybackParameters) {
            PlaybackParameters lastSetPlaybackParameters = this.afterDrainPlaybackParameters;
            if (lastSetPlaybackParameters == null) {
                lastSetPlaybackParameters = !this.playbackParametersCheckpoints.isEmpty() ? this.playbackParametersCheckpoints.getLast().playbackParameters : this.playbackParameters;
            }
            if (!playbackParameters2.equals(lastSetPlaybackParameters)) {
                if (isInitialized()) {
                    this.afterDrainPlaybackParameters = playbackParameters2;
                } else {
                    this.playbackParameters = this.audioProcessorChain.applyPlaybackParameters(playbackParameters2);
                }
            }
            return this.playbackParameters;
        }
        PlaybackParameters playbackParameters3 = PlaybackParameters.DEFAULT;
        this.playbackParameters = playbackParameters3;
        return playbackParameters3;
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }

    public void setAudioAttributes(AudioAttributes audioAttributes2) {
        if (!this.audioAttributes.equals(audioAttributes2)) {
            this.audioAttributes = audioAttributes2;
            if (!this.tunneling) {
                flush();
                this.audioSessionId = 0;
            }
        }
    }

    public void setAudioSessionId(int audioSessionId2) {
        if (this.audioSessionId != audioSessionId2) {
            this.audioSessionId = audioSessionId2;
            flush();
        }
    }

    public void setAuxEffectInfo(AuxEffectInfo auxEffectInfo2) {
        if (!this.auxEffectInfo.equals(auxEffectInfo2)) {
            int effectId = auxEffectInfo2.effectId;
            float sendLevel = auxEffectInfo2.sendLevel;
            if (this.audioTrack != null) {
                if (this.auxEffectInfo.effectId != effectId) {
                    this.audioTrack.attachAuxEffect(effectId);
                }
                if (effectId != 0) {
                    this.audioTrack.setAuxEffectSendLevel(sendLevel);
                }
            }
            this.auxEffectInfo = auxEffectInfo2;
        }
    }

    public void enableTunnelingV21(int tunnelingAudioSessionId) {
        Assertions.checkState(Util.SDK_INT >= 21);
        if (!this.tunneling || this.audioSessionId != tunnelingAudioSessionId) {
            this.tunneling = true;
            this.audioSessionId = tunnelingAudioSessionId;
            flush();
        }
    }

    public void disableTunneling() {
        if (this.tunneling) {
            this.tunneling = false;
            this.audioSessionId = 0;
            flush();
        }
    }

    public void setVolume(float volume2) {
        if (this.volume != volume2) {
            this.volume = volume2;
            setVolumeInternal();
        }
    }

    private void setVolumeInternal() {
        if (isInitialized()) {
            if (Util.SDK_INT >= 21) {
                setVolumeInternalV21(this.audioTrack, this.volume);
            } else {
                setVolumeInternalV3(this.audioTrack, this.volume);
            }
        }
    }

    public void pause() {
        this.playing = false;
        if (isInitialized() && this.audioTrackPositionTracker.pause()) {
            this.audioTrack.pause();
        }
    }

    public void flush() {
        if (isInitialized()) {
            this.submittedPcmBytes = 0;
            this.submittedEncodedFrames = 0;
            this.writtenPcmBytes = 0;
            this.writtenEncodedFrames = 0;
            this.framesPerEncodedSample = 0;
            PlaybackParameters playbackParameters2 = this.afterDrainPlaybackParameters;
            if (playbackParameters2 != null) {
                this.playbackParameters = playbackParameters2;
                this.afterDrainPlaybackParameters = null;
            } else if (!this.playbackParametersCheckpoints.isEmpty()) {
                this.playbackParameters = this.playbackParametersCheckpoints.getLast().playbackParameters;
            }
            this.playbackParametersCheckpoints.clear();
            this.playbackParametersOffsetUs = 0;
            this.playbackParametersPositionUs = 0;
            this.trimmingAudioProcessor.resetTrimmedFrameCount();
            this.inputBuffer = null;
            this.outputBuffer = null;
            flushAudioProcessors();
            this.handledEndOfStream = false;
            this.drainingAudioProcessorIndex = -1;
            this.avSyncHeader = null;
            this.bytesUntilNextAvSync = 0;
            this.startMediaTimeState = 0;
            if (this.audioTrackPositionTracker.isPlaying()) {
                this.audioTrack.pause();
            }
            final AudioTrack toRelease = this.audioTrack;
            this.audioTrack = null;
            this.audioTrackPositionTracker.reset();
            this.releasingConditionVariable.close();
            new Thread() {
                public void run() {
                    try {
                        toRelease.flush();
                        toRelease.release();
                    } finally {
                        DefaultAudioSink.this.releasingConditionVariable.open();
                    }
                }
            }.start();
        }
    }

    public void reset() {
        flush();
        releaseKeepSessionIdAudioTrack();
        for (AudioProcessor audioProcessor : this.toIntPcmAvailableAudioProcessors) {
            audioProcessor.reset();
        }
        for (AudioProcessor audioProcessor2 : this.toFloatPcmAvailableAudioProcessors) {
            audioProcessor2.reset();
        }
        this.audioSessionId = 0;
        this.playing = false;
    }

    private void releaseKeepSessionIdAudioTrack() {
        if (this.keepSessionIdAudioTrack != null) {
            final AudioTrack toRelease = this.keepSessionIdAudioTrack;
            this.keepSessionIdAudioTrack = null;
            new Thread() {
                public void run() {
                    toRelease.release();
                }
            }.start();
        }
    }

    private long applySpeedup(long positionUs) {
        PlaybackParametersCheckpoint checkpoint = null;
        while (!this.playbackParametersCheckpoints.isEmpty() && positionUs >= this.playbackParametersCheckpoints.getFirst().positionUs) {
            checkpoint = this.playbackParametersCheckpoints.remove();
        }
        if (checkpoint != null) {
            this.playbackParameters = checkpoint.playbackParameters;
            this.playbackParametersPositionUs = checkpoint.positionUs;
            this.playbackParametersOffsetUs = checkpoint.mediaTimeUs - this.startMediaTimeUs;
        }
        if (this.playbackParameters.speed == 1.0f) {
            return (this.playbackParametersOffsetUs + positionUs) - this.playbackParametersPositionUs;
        }
        if (this.playbackParametersCheckpoints.isEmpty()) {
            return this.playbackParametersOffsetUs + this.audioProcessorChain.getMediaDuration(positionUs - this.playbackParametersPositionUs);
        }
        return this.playbackParametersOffsetUs + Util.getMediaDurationForPlayoutDuration(positionUs - this.playbackParametersPositionUs, this.playbackParameters.speed);
    }

    private long applySkipping(long positionUs) {
        return framesToDurationUs(this.audioProcessorChain.getSkippedOutputFrameCount()) + positionUs;
    }

    private boolean isInitialized() {
        return this.audioTrack != null;
    }

    private long inputFramesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.inputSampleRate);
    }

    private long framesToDurationUs(long frameCount) {
        return (1000000 * frameCount) / ((long) this.outputSampleRate);
    }

    private long durationUsToFrames(long durationUs) {
        return (((long) this.outputSampleRate) * durationUs) / 1000000;
    }

    /* access modifiers changed from: private */
    public long getSubmittedFrames() {
        return this.isInputPcm ? this.submittedPcmBytes / ((long) this.pcmFrameSize) : this.submittedEncodedFrames;
    }

    /* access modifiers changed from: private */
    public long getWrittenFrames() {
        return this.isInputPcm ? this.writtenPcmBytes / ((long) this.outputPcmFrameSize) : this.writtenEncodedFrames;
    }

    private AudioTrack initializeAudioTrack() throws AudioSink.InitializationException {
        AudioTrack audioTrack2;
        if (Util.SDK_INT >= 21) {
            audioTrack2 = createAudioTrackV21();
        } else {
            int streamType = Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
            if (this.audioSessionId == 0) {
                audioTrack2 = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1);
            } else {
                audioTrack2 = new AudioTrack(streamType, this.outputSampleRate, this.outputChannelConfig, this.outputEncoding, this.bufferSize, 1, this.audioSessionId);
            }
        }
        int state = audioTrack2.getState();
        if (state == 1) {
            return audioTrack2;
        }
        try {
            audioTrack2.release();
        } catch (Exception e) {
        }
        throw new AudioSink.InitializationException(state, this.outputSampleRate, this.outputChannelConfig, this.bufferSize);
    }

    private AudioTrack createAudioTrackV21() {
        AudioAttributes attributes;
        if (this.tunneling) {
            attributes = new AudioAttributes.Builder().setContentType(3).setFlags(16).setUsage(1).build();
        } else {
            attributes = this.audioAttributes.getAudioAttributesV21();
        }
        AudioFormat format = new AudioFormat.Builder().setChannelMask(this.outputChannelConfig).setEncoding(this.outputEncoding).setSampleRate(this.outputSampleRate).build();
        int i = this.audioSessionId;
        return new AudioTrack(attributes, format, this.bufferSize, 1, i != 0 ? i : 0);
    }

    private AudioTrack initializeKeepSessionIdAudioTrack(int audioSessionId2) {
        return new AudioTrack(3, 4000, 4, 2, 2, 0, audioSessionId2);
    }

    private AudioProcessor[] getAvailableAudioProcessors() {
        return this.shouldConvertHighResIntPcmToFloat ? this.toFloatPcmAvailableAudioProcessors : this.toIntPcmAvailableAudioProcessors;
    }

    private static int getChannelConfig(int channelCount, boolean isInputPcm2) {
        if (Util.SDK_INT <= 28 && !isInputPcm2) {
            if (channelCount == 7) {
                channelCount = 8;
            } else if (channelCount == 3 || channelCount == 4 || channelCount == 5) {
                channelCount = 6;
            }
        }
        if (Util.SDK_INT <= 26 && "fugu".equals(Util.DEVICE) && !isInputPcm2 && channelCount == 1) {
            channelCount = 2;
        }
        return Util.getAudioTrackChannelConfig(channelCount);
    }

    private static int getMaximumEncodedRateBytesPerSecond(int encoding) {
        if (encoding == 5) {
            return 80000;
        }
        if (encoding == 6) {
            return 768000;
        }
        if (encoding == 7) {
            return 192000;
        }
        if (encoding == 8) {
            return 2250000;
        }
        if (encoding == 14) {
            return 3062500;
        }
        throw new IllegalArgumentException();
    }

    private static int getFramesPerEncodedSample(int encoding, ByteBuffer buffer) {
        if (encoding == 7 || encoding == 8) {
            return DtsUtil.parseDtsAudioSampleCount(buffer);
        }
        if (encoding == 5) {
            return Ac3Util.getAc3SyncframeAudioSampleCount();
        }
        if (encoding == 6) {
            return Ac3Util.parseEAc3SyncframeAudioSampleCount(buffer);
        }
        if (encoding == 14) {
            int syncframeOffset = Ac3Util.findTrueHdSyncframeOffset(buffer);
            if (syncframeOffset == -1) {
                return 0;
            }
            return Ac3Util.parseTrueHdSyncframeAudioSampleCount(buffer, syncframeOffset) * 16;
        }
        throw new IllegalStateException("Unexpected audio encoding: " + encoding);
    }

    private static int writeNonBlockingV21(AudioTrack audioTrack2, ByteBuffer buffer, int size) {
        return audioTrack2.write(buffer, size, 1);
    }

    private int writeNonBlockingWithAvSyncV21(AudioTrack audioTrack2, ByteBuffer buffer, int size, long presentationTimeUs) {
        if (this.avSyncHeader == null) {
            ByteBuffer allocate = ByteBuffer.allocate(16);
            this.avSyncHeader = allocate;
            allocate.order(ByteOrder.BIG_ENDIAN);
            this.avSyncHeader.putInt(1431633921);
        }
        if (this.bytesUntilNextAvSync == 0) {
            this.avSyncHeader.putInt(4, size);
            this.avSyncHeader.putLong(8, 1000 * presentationTimeUs);
            this.avSyncHeader.position(0);
            this.bytesUntilNextAvSync = size;
        }
        int avSyncHeaderBytesRemaining = this.avSyncHeader.remaining();
        if (avSyncHeaderBytesRemaining > 0) {
            int result = audioTrack2.write(this.avSyncHeader, avSyncHeaderBytesRemaining, 1);
            if (result < 0) {
                this.bytesUntilNextAvSync = 0;
                return result;
            } else if (result < avSyncHeaderBytesRemaining) {
                return 0;
            }
        }
        int result2 = writeNonBlockingV21(audioTrack2, buffer, size);
        if (result2 < 0) {
            this.bytesUntilNextAvSync = 0;
            return result2;
        }
        this.bytesUntilNextAvSync -= result2;
        return result2;
    }

    private static void setVolumeInternalV21(AudioTrack audioTrack2, float volume2) {
        audioTrack2.setVolume(volume2);
    }

    private static void setVolumeInternalV3(AudioTrack audioTrack2, float volume2) {
        audioTrack2.setStereoVolume(volume2, volume2);
    }

    private static final class PlaybackParametersCheckpoint {
        /* access modifiers changed from: private */
        public final long mediaTimeUs;
        /* access modifiers changed from: private */
        public final PlaybackParameters playbackParameters;
        /* access modifiers changed from: private */
        public final long positionUs;

        private PlaybackParametersCheckpoint(PlaybackParameters playbackParameters2, long mediaTimeUs2, long positionUs2) {
            this.playbackParameters = playbackParameters2;
            this.mediaTimeUs = mediaTimeUs2;
            this.positionUs = positionUs2;
        }
    }

    private final class PositionTrackerListener implements AudioTrackPositionTracker.Listener {
        private PositionTrackerListener() {
        }

        public void onPositionFramesMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = "Spurious audio timestamp (frame position mismatch): " + audioTimestampPositionFrames + ", " + audioTimestampSystemTimeUs + ", " + systemTimeUs + ", " + playbackPositionUs + ", " + DefaultAudioSink.this.getSubmittedFrames() + ", " + DefaultAudioSink.this.getWrittenFrames();
            if (!DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                Log.w(DefaultAudioSink.TAG, message);
                return;
            }
            throw new InvalidAudioTrackTimestampException(message);
        }

        public void onSystemTimeUsMismatch(long audioTimestampPositionFrames, long audioTimestampSystemTimeUs, long systemTimeUs, long playbackPositionUs) {
            String message = "Spurious audio timestamp (system clock mismatch): " + audioTimestampPositionFrames + ", " + audioTimestampSystemTimeUs + ", " + systemTimeUs + ", " + playbackPositionUs + ", " + DefaultAudioSink.this.getSubmittedFrames() + ", " + DefaultAudioSink.this.getWrittenFrames();
            if (!DefaultAudioSink.failOnSpuriousAudioTimestamp) {
                Log.w(DefaultAudioSink.TAG, message);
                return;
            }
            throw new InvalidAudioTrackTimestampException(message);
        }

        public void onInvalidLatency(long latencyUs) {
            Log.w(DefaultAudioSink.TAG, "Ignoring impossibly large audio latency: " + latencyUs);
        }

        public void onUnderrun(int bufferSize, long bufferSizeMs) {
            if (DefaultAudioSink.this.listener != null) {
                DefaultAudioSink.this.listener.onUnderrun(bufferSize, bufferSizeMs, SystemClock.elapsedRealtime() - DefaultAudioSink.this.lastFeedElapsedRealtimeMs);
            }
        }
    }
}
