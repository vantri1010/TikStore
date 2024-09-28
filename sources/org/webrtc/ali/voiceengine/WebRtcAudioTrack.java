package org.webrtc.ali.voiceengine;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import com.google.android.exoplayer2.util.MimeTypes;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.nio.ByteBuffer;
import org.webrtc.ali.ContextUtils;
import org.webrtc.ali.Logging;

public class WebRtcAudioTrack {
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioTrack";
    private static WebRtcAudioTrackErrorCallback errorCallback = null;
    /* access modifiers changed from: private */
    public static volatile boolean speakerMute = false;
    private final AudioManager audioManager;
    private AudioTrackThread audioThread = null;
    /* access modifiers changed from: private */
    public AudioTrack audioTrack = null;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    /* access modifiers changed from: private */
    public final long nativeAudioTrack;
    private int streamType;

    public interface WebRtcAudioTrackErrorCallback {
        void onWebRtcAudioTrackError(String str);

        void onWebRtcAudioTrackInitError(String str);

        void onWebRtcAudioTrackStartError(String str);
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeGetPlayoutData(int i, long j);

    public static void setErrorCallback(WebRtcAudioTrackErrorCallback errorCallback2) {
        Logging.d(TAG, "Set error callback");
        errorCallback = errorCallback2;
    }

    private class AudioTrackThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioTrackThread(String name) {
            super(name);
        }

        public void run() {
            int bytesWritten;
            Process.setThreadPriority(-19);
            Logging.d(WebRtcAudioTrack.TAG, "AudioTrackThread" + WebRtcAudioUtils.getThreadInfo());
            try {
                WebRtcAudioTrack.this.audioTrack.play();
                boolean z = true;
                WebRtcAudioTrack.assertTrue(WebRtcAudioTrack.this.audioTrack.getPlayState() == 3);
                int sizeInBytes = WebRtcAudioTrack.this.byteBuffer.capacity();
                while (this.keepAlive) {
                    WebRtcAudioTrack webRtcAudioTrack = WebRtcAudioTrack.this;
                    webRtcAudioTrack.nativeGetPlayoutData(sizeInBytes, webRtcAudioTrack.nativeAudioTrack);
                    WebRtcAudioTrack.assertTrue(sizeInBytes <= WebRtcAudioTrack.this.byteBuffer.remaining());
                    if (WebRtcAudioTrack.speakerMute) {
                        WebRtcAudioTrack.this.byteBuffer.clear();
                        WebRtcAudioTrack.this.byteBuffer.put(WebRtcAudioTrack.this.emptyBytes);
                        WebRtcAudioTrack.this.byteBuffer.position(0);
                    }
                    if (WebRtcAudioUtils.runningOnLollipopOrHigher()) {
                        bytesWritten = writeOnLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, sizeInBytes);
                    } else {
                        bytesWritten = writePreLollipop(WebRtcAudioTrack.this.audioTrack, WebRtcAudioTrack.this.byteBuffer, sizeInBytes);
                    }
                    if (bytesWritten != sizeInBytes) {
                        Logging.e(WebRtcAudioTrack.TAG, "AudioTrack.write failed: " + bytesWritten);
                        if (bytesWritten == -3) {
                            this.keepAlive = false;
                            WebRtcAudioTrack webRtcAudioTrack2 = WebRtcAudioTrack.this;
                            webRtcAudioTrack2.reportWebRtcAudioTrackError("AudioTrack.write failed: " + bytesWritten);
                        }
                    }
                    WebRtcAudioTrack.this.byteBuffer.rewind();
                }
                try {
                    WebRtcAudioTrack.this.audioTrack.stop();
                } catch (IllegalStateException e) {
                    Logging.e(WebRtcAudioTrack.TAG, "AudioTrack.stop failed: " + e.getMessage());
                }
                if (WebRtcAudioTrack.this.audioTrack.getPlayState() != 1) {
                    z = false;
                }
                WebRtcAudioTrack.assertTrue(z);
                WebRtcAudioTrack.this.audioTrack.flush();
            } catch (IllegalStateException e2) {
                WebRtcAudioTrack webRtcAudioTrack3 = WebRtcAudioTrack.this;
                webRtcAudioTrack3.reportWebRtcAudioTrackStartError("AudioTrack.play failed: " + e2.getMessage());
                WebRtcAudioTrack.this.releaseAudioResources();
            }
        }

        private int writeOnLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
            return audioTrack.write(byteBuffer, sizeInBytes, 0);
        }

        private int writePreLollipop(AudioTrack audioTrack, ByteBuffer byteBuffer, int sizeInBytes) {
            return audioTrack.write(byteBuffer.array(), byteBuffer.arrayOffset(), sizeInBytes);
        }

        public void joinThread() {
            this.keepAlive = false;
            while (isAlive()) {
                try {
                    join();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    WebRtcAudioTrack(long nativeAudioTrack2) {
        Logging.d(TAG, "ctor" + WebRtcAudioUtils.getThreadInfo());
        this.nativeAudioTrack = nativeAudioTrack2;
        this.audioManager = (AudioManager) ContextUtils.getApplicationContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
    }

    private boolean initPlayout(int streamType2, int sampleRate, int channels) {
        Logging.d(TAG, "initPlayout(sampleRate=" + sampleRate + ", channels=" + channels + SQLBuilder.PARENTHESES_RIGHT);
        this.byteBuffer = ByteBuffer.allocateDirect((sampleRate / 100) * channels * 2);
        StringBuilder sb = new StringBuilder();
        sb.append("byteBuffer.capacity: ");
        sb.append(this.byteBuffer.capacity());
        Logging.d(TAG, sb.toString());
        this.emptyBytes = new byte[this.byteBuffer.capacity()];
        nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioTrack);
        int channelConfig = channelCountToConfiguration(channels);
        int minBufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRate, channelConfig, 2);
        Logging.d(TAG, "AudioTrack.getMinBufferSize: " + minBufferSizeInBytes);
        if (minBufferSizeInBytes < this.byteBuffer.capacity()) {
            reportWebRtcAudioTrackInitError("AudioTrack.getMinBufferSize returns an invalid value.");
            return false;
        } else if (this.audioTrack != null) {
            reportWebRtcAudioTrackInitError("Conflict with existing AudioTrack.");
            return false;
        } else {
            this.streamType = streamType2;
            try {
                if (WebRtcAudioUtils.runningOnLollipopOrHigher()) {
                    this.audioTrack = createAudioTrackOnLollipopOrHigher(sampleRate, channelConfig, minBufferSizeInBytes);
                } else {
                    this.audioTrack = new AudioTrack(streamType2, sampleRate, channelConfig, 2, minBufferSizeInBytes, 1);
                }
                AudioTrack audioTrack2 = this.audioTrack;
                if (audioTrack2 == null || audioTrack2.getState() != 1) {
                    reportWebRtcAudioTrackInitError("Initialization of audio track failed.");
                    releaseAudioResources();
                    return false;
                }
                logMainParameters();
                logMainParametersExtended();
                return true;
            } catch (IllegalArgumentException e) {
                reportWebRtcAudioTrackInitError(e.getMessage());
                releaseAudioResources();
                return false;
            }
        }
    }

    private boolean startPlayout() {
        Logging.d(TAG, "startPlayout");
        assertTrue(this.audioTrack != null);
        assertTrue(this.audioThread == null);
        if (this.audioTrack.getState() != 1) {
            reportWebRtcAudioTrackStartError("AudioTrack instance is not successfully initialized.");
            return false;
        }
        AudioTrackThread audioTrackThread = new AudioTrackThread("AudioTrackJavaThread");
        this.audioThread = audioTrackThread;
        audioTrackThread.start();
        return true;
    }

    private boolean stopPlayout() {
        Logging.d(TAG, "stopPlayout");
        assertTrue(this.audioThread != null);
        logUnderrunCount();
        this.audioThread.joinThread();
        this.audioThread = null;
        releaseAudioResources();
        return true;
    }

    private int getStreamMaxVolume() {
        Logging.d(TAG, "getStreamMaxVolume");
        assertTrue(this.audioManager != null);
        return this.audioManager.getStreamMaxVolume(0);
    }

    private boolean setStreamVolume(int volume) {
        Logging.d(TAG, "setStreamVolume(" + volume + SQLBuilder.PARENTHESES_RIGHT);
        assertTrue(this.audioManager != null);
        if (isVolumeFixed()) {
            Logging.e(TAG, "The device implements a fixed volume policy.");
            return false;
        }
        this.audioManager.setStreamVolume(0, volume, 0);
        return true;
    }

    private boolean isVolumeFixed() {
        if (!WebRtcAudioUtils.runningOnLollipopOrHigher()) {
            return false;
        }
        return this.audioManager.isVolumeFixed();
    }

    private int getStreamVolume() {
        Logging.d(TAG, "getStreamVolume");
        assertTrue(this.audioManager != null);
        return this.audioManager.getStreamVolume(0);
    }

    private void logMainParameters() {
        Logging.d(TAG, "AudioTrack: session ID: " + this.audioTrack.getAudioSessionId() + ", channels: " + this.audioTrack.getChannelCount() + ", sample rate: " + this.audioTrack.getSampleRate() + ", max gain: " + AudioTrack.getMaxVolume());
    }

    private AudioTrack createAudioTrackOnLollipopOrHigher(int sampleRateInHz, int channelConfig, int bufferSizeInBytes) {
        Logging.d(TAG, "createAudioTrackOnLollipopOrHigher");
        int nativeOutputSampleRate = AudioTrack.getNativeOutputSampleRate(0);
        Logging.d(TAG, "nativeOutputSampleRate: " + nativeOutputSampleRate);
        if (sampleRateInHz != nativeOutputSampleRate) {
            Logging.w(TAG, "Unable to use fast mode since requested sample rate is not native");
        }
        return new AudioTrack(new AudioAttributes.Builder().setUsage(2).setContentType(1).build(), new AudioFormat.Builder().setEncoding(2).setSampleRate(sampleRateInHz).setChannelMask(channelConfig).build(), bufferSizeInBytes, 1, 0);
    }

    private void logMainParametersExtended() {
        if (WebRtcAudioUtils.runningOnMarshmallowOrHigher()) {
            Logging.d(TAG, "AudioTrack: buffer size in frames: " + this.audioTrack.getBufferSizeInFrames());
        }
        if (WebRtcAudioUtils.runningOnNougatOrHigher()) {
            Logging.d(TAG, "AudioTrack: buffer capacity in frames: " + this.audioTrack.getBufferCapacityInFrames());
        }
    }

    private void logUnderrunCount() {
        if (WebRtcAudioUtils.runningOnNougatOrHigher()) {
            Logging.d(TAG, "underrun count: " + this.audioTrack.getUnderrunCount());
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private int channelCountToConfiguration(int channels) {
        return channels == 1 ? 4 : 12;
    }

    public static void setSpeakerMute(boolean mute) {
        Logging.w(TAG, "setSpeakerMute(" + mute + SQLBuilder.PARENTHESES_RIGHT);
        speakerMute = mute;
    }

    /* access modifiers changed from: private */
    public void releaseAudioResources() {
        AudioTrack audioTrack2 = this.audioTrack;
        if (audioTrack2 != null) {
            audioTrack2.release();
            this.audioTrack = null;
        }
    }

    private void reportWebRtcAudioTrackInitError(String errorMessage) {
        Logging.e(TAG, "Init error: " + errorMessage);
        WebRtcAudioTrackErrorCallback webRtcAudioTrackErrorCallback = errorCallback;
        if (webRtcAudioTrackErrorCallback != null) {
            webRtcAudioTrackErrorCallback.onWebRtcAudioTrackInitError(errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioTrackStartError(String errorMessage) {
        Logging.e(TAG, "Start error: " + errorMessage);
        WebRtcAudioTrackErrorCallback webRtcAudioTrackErrorCallback = errorCallback;
        if (webRtcAudioTrackErrorCallback != null) {
            webRtcAudioTrackErrorCallback.onWebRtcAudioTrackStartError(errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioTrackError(String errorMessage) {
        Logging.e(TAG, "Run-time playback error: " + errorMessage);
        WebRtcAudioTrackErrorCallback webRtcAudioTrackErrorCallback = errorCallback;
        if (webRtcAudioTrackErrorCallback != null) {
            webRtcAudioTrackErrorCallback.onWebRtcAudioTrackError(errorMessage);
        }
    }
}
