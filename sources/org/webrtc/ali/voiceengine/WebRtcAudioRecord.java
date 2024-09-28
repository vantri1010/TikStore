package org.webrtc.ali.voiceengine;

import android.media.AudioRecord;
import android.os.Process;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.nio.ByteBuffer;
import org.webrtc.ali.ContextUtils;
import org.webrtc.ali.Logging;
import org.webrtc.ali.ThreadUtils;

public class WebRtcAudioRecord {
    private static final long AUDIO_RECORD_THREAD_JOIN_TIMEOUT_MS = 2000;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int BUFFERS_PER_SECOND = 100;
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "WebRtcAudioRecord";
    private static WebRtcAudioRecordErrorCallback errorCallback = null;
    /* access modifiers changed from: private */
    public static volatile boolean microphoneMute = false;
    /* access modifiers changed from: private */
    public AudioRecord audioRecord = null;
    private int audioSource;
    private AudioRecordThread audioThread = null;
    /* access modifiers changed from: private */
    public ByteBuffer byteBuffer;
    private WebRtcAudioEffects effects = null;
    /* access modifiers changed from: private */
    public byte[] emptyBytes;
    /* access modifiers changed from: private */
    public final long nativeAudioRecord;
    private final ThreadUtils.ThreadChecker threadChecker;

    public enum AudioRecordStartErrorCode {
        AUDIO_RECORD_START_EXCEPTION,
        AUDIO_RECORD_START_STATE_MISMATCH
    }

    public interface WebRtcAudioRecordErrorCallback {
        void onWebRtcAudioRecordError(String str);

        void onWebRtcAudioRecordInitError(String str);

        void onWebRtcAudioRecordStartError(AudioRecordStartErrorCode audioRecordStartErrorCode, String str);
    }

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer2, long j);

    /* access modifiers changed from: private */
    public native void nativeDataIsRecorded(int i, long j);

    public static void setErrorCallback(WebRtcAudioRecordErrorCallback errorCallback2) {
        Logging.d(TAG, "Set error callback");
        errorCallback = errorCallback2;
    }

    private class AudioRecordThread extends Thread {
        private volatile boolean keepAlive = true;

        public AudioRecordThread(String name) {
            super(name);
        }

        public void run() {
            Process.setThreadPriority(-19);
            Logging.d(WebRtcAudioRecord.TAG, "AudioRecordThread" + WebRtcAudioUtils.getThreadInfo());
            WebRtcAudioRecord.assertTrue(WebRtcAudioRecord.this.audioRecord.getRecordingState() == 3);
            long nanoTime = System.nanoTime();
            while (this.keepAlive) {
                int bytesRead = WebRtcAudioRecord.this.audioRecord.read(WebRtcAudioRecord.this.byteBuffer, WebRtcAudioRecord.this.byteBuffer.capacity());
                if (bytesRead == WebRtcAudioRecord.this.byteBuffer.capacity()) {
                    if (WebRtcAudioRecord.microphoneMute) {
                        WebRtcAudioRecord.this.byteBuffer.clear();
                        WebRtcAudioRecord.this.byteBuffer.put(WebRtcAudioRecord.this.emptyBytes);
                    }
                    WebRtcAudioRecord webRtcAudioRecord = WebRtcAudioRecord.this;
                    webRtcAudioRecord.nativeDataIsRecorded(bytesRead, webRtcAudioRecord.nativeAudioRecord);
                } else {
                    String errorMessage = "AudioRecord.read failed: " + bytesRead;
                    Logging.e(WebRtcAudioRecord.TAG, errorMessage);
                    if (bytesRead == -3) {
                        this.keepAlive = false;
                        WebRtcAudioRecord.this.reportWebRtcAudioRecordError(errorMessage);
                    }
                }
            }
            try {
                if (WebRtcAudioRecord.this.audioRecord != null) {
                    WebRtcAudioRecord.this.audioRecord.stop();
                }
            } catch (IllegalStateException e) {
                Logging.e(WebRtcAudioRecord.TAG, "AudioRecord.stop failed: " + e.getMessage());
            }
        }

        public void stopThread() {
            Logging.d(WebRtcAudioRecord.TAG, "stopThread");
            this.keepAlive = false;
        }
    }

    WebRtcAudioRecord(long nativeAudioRecord2) {
        ThreadUtils.ThreadChecker threadChecker2 = new ThreadUtils.ThreadChecker();
        this.threadChecker = threadChecker2;
        threadChecker2.checkIsOnValidThread();
        Logging.d(TAG, "ctor" + WebRtcAudioUtils.getThreadInfo());
        this.nativeAudioRecord = nativeAudioRecord2;
        this.effects = WebRtcAudioEffects.create();
    }

    private boolean enableBuiltInAEC(boolean enable) {
        this.threadChecker.checkIsOnValidThread();
        Logging.d(TAG, "enableBuiltInAEC(" + enable + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setAEC(enable);
        }
        Logging.e(TAG, "Built-in AEC is not supported on this platform");
        return false;
    }

    private boolean enableBuiltInNS(boolean enable) {
        this.threadChecker.checkIsOnValidThread();
        Logging.d(TAG, "enableBuiltInNS(" + enable + ')');
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            return webRtcAudioEffects.setNS(enable);
        }
        Logging.e(TAG, "Built-in NS is not supported on this platform");
        return false;
    }

    private int initRecording(int audioSource2, int sampleRate, int channels) {
        int i = sampleRate;
        int i2 = channels;
        this.threadChecker.checkIsOnValidThread();
        Logging.d(TAG, "initRecording(sampleRate=" + i + ", channels=" + i2 + SQLBuilder.PARENTHESES_RIGHT);
        if (!WebRtcAudioUtils.hasPermission(ContextUtils.getApplicationContext(), "android.permission.RECORD_AUDIO")) {
            reportWebRtcAudioRecordInitError("RECORD_AUDIO permission is missing");
            return -2;
        } else if (this.audioRecord != null) {
            reportWebRtcAudioRecordInitError("InitRecording called twice without StopRecording.");
            return -1;
        } else {
            int framesPerBuffer = i / 100;
            this.byteBuffer = ByteBuffer.allocateDirect(i2 * 2 * framesPerBuffer);
            Logging.d(TAG, "byteBuffer.capacity: " + this.byteBuffer.capacity());
            this.audioSource = audioSource2;
            this.emptyBytes = new byte[this.byteBuffer.capacity()];
            nativeCacheDirectBufferAddress(this.byteBuffer, this.nativeAudioRecord);
            int channelConfig = channelCountToConfiguration(i2);
            int minBufferSize = AudioRecord.getMinBufferSize(i, channelConfig, 2);
            if (minBufferSize == -1 || minBufferSize == -2) {
                reportWebRtcAudioRecordInitError("AudioRecord.getMinBufferSize failed: " + minBufferSize);
                return -1;
            }
            Logging.d(TAG, "AudioRecord.getMinBufferSize: " + minBufferSize);
            int bufferSizeInBytes = Math.max(minBufferSize * 2, this.byteBuffer.capacity());
            Logging.d(TAG, "bufferSizeInBytes: " + bufferSizeInBytes);
            try {
                int i3 = bufferSizeInBytes;
                try {
                    AudioRecord audioRecord2 = new AudioRecord(audioSource2, sampleRate, channelConfig, 2, bufferSizeInBytes);
                    this.audioRecord = audioRecord2;
                    if (audioRecord2 == null || audioRecord2.getState() != 1) {
                        reportWebRtcAudioRecordInitError("Failed to create a new AudioRecord instance");
                        releaseAudioResources();
                        return -1;
                    }
                    WebRtcAudioEffects webRtcAudioEffects = this.effects;
                    if (webRtcAudioEffects != null) {
                        webRtcAudioEffects.enable(this.audioRecord.getAudioSessionId());
                    }
                    logMainParameters();
                    logMainParametersExtended();
                    return framesPerBuffer;
                } catch (IllegalArgumentException e) {
                    e = e;
                }
            } catch (IllegalArgumentException e2) {
                e = e2;
                int i4 = bufferSizeInBytes;
                reportWebRtcAudioRecordInitError("AudioRecord ctor error: " + e.getMessage());
                releaseAudioResources();
                return -1;
            }
        }
    }

    private boolean startRecording() {
        this.threadChecker.checkIsOnValidThread();
        Logging.d(TAG, "startRecording");
        assertTrue(this.audioRecord != null);
        assertTrue(this.audioThread == null);
        try {
            this.audioRecord.startRecording();
            int numberOfStateChecks = 0;
            while (this.audioRecord.getRecordingState() != 3 && (numberOfStateChecks = numberOfStateChecks + 1) < 2) {
                threadSleep(200);
            }
            if (this.audioRecord.getRecordingState() != 3) {
                AudioRecordStartErrorCode audioRecordStartErrorCode = AudioRecordStartErrorCode.AUDIO_RECORD_START_STATE_MISMATCH;
                reportWebRtcAudioRecordStartError(audioRecordStartErrorCode, "AudioRecord.startRecording failed - incorrect state :" + this.audioRecord.getRecordingState());
                return false;
            }
            AudioRecordThread audioRecordThread = new AudioRecordThread("AudioRecordJavaThread");
            this.audioThread = audioRecordThread;
            audioRecordThread.start();
            return true;
        } catch (IllegalStateException e) {
            AudioRecordStartErrorCode audioRecordStartErrorCode2 = AudioRecordStartErrorCode.AUDIO_RECORD_START_EXCEPTION;
            reportWebRtcAudioRecordStartError(audioRecordStartErrorCode2, "AudioRecord.startRecording failed: " + e.getMessage());
            return false;
        }
    }

    private boolean stopRecording() {
        this.threadChecker.checkIsOnValidThread();
        Logging.d(TAG, "stopRecording");
        assertTrue(this.audioThread != null);
        this.audioThread.stopThread();
        if (!ThreadUtils.joinUninterruptibly(this.audioThread, 2000)) {
            Logging.e(TAG, "Join of AudioRecordJavaThread timed out");
        }
        this.audioThread = null;
        WebRtcAudioEffects webRtcAudioEffects = this.effects;
        if (webRtcAudioEffects != null) {
            webRtcAudioEffects.release();
        }
        releaseAudioResources();
        return true;
    }

    private void logMainParameters() {
        Logging.d(TAG, "AudioRecord: session ID: " + this.audioRecord.getAudioSessionId() + ", channels: " + this.audioRecord.getChannelCount() + ", sample rate: " + this.audioRecord.getSampleRate());
    }

    private void logMainParametersExtended() {
        if (WebRtcAudioUtils.runningOnMarshmallowOrHigher()) {
            Logging.d(TAG, "AudioRecord: buffer size in frames: " + this.audioRecord.getBufferSizeInFrames());
        }
    }

    /* access modifiers changed from: private */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }

    private int channelCountToConfiguration(int channels) {
        return channels == 1 ? 16 : 12;
    }

    public static void setMicrophoneMute(boolean mute) {
        Logging.w(TAG, "setMicrophoneMute(" + mute + SQLBuilder.PARENTHESES_RIGHT);
        microphoneMute = mute;
    }

    private void releaseAudioResources() {
        AudioRecord audioRecord2 = this.audioRecord;
        if (audioRecord2 != null) {
            audioRecord2.release();
            this.audioRecord = null;
        }
    }

    private void reportWebRtcAudioRecordInitError(String errorMessage) {
        Logging.e(TAG, "Init recording error: " + errorMessage);
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordInitError(errorMessage);
        }
    }

    private void reportWebRtcAudioRecordStartError(AudioRecordStartErrorCode errorCode, String errorMessage) {
        Logging.e(TAG, "Start recording error: " + errorCode + ". " + errorMessage);
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordStartError(errorCode, errorMessage);
        }
    }

    /* access modifiers changed from: private */
    public void reportWebRtcAudioRecordError(String errorMessage) {
        Logging.e(TAG, "Run-time recording error: " + errorMessage);
        WebRtcAudioRecordErrorCallback webRtcAudioRecordErrorCallback = errorCallback;
        if (webRtcAudioRecordErrorCallback != null) {
            webRtcAudioRecordErrorCallback.onWebRtcAudioRecordError(errorMessage);
        }
    }

    private void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Logging.e(TAG, "Thread.sleep failed: " + e.getMessage());
        }
    }
}
