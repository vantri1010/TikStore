package org.webrtc.ali.voiceengine;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.util.MimeTypes;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.util.Timer;
import java.util.TimerTask;
import org.webrtc.ali.ContextUtils;
import org.webrtc.ali.Logging;

public class WebRtcAudioManager {
    private static final String[] AUDIO_MODES = {"MODE_NORMAL", "MODE_RINGTONE", "MODE_IN_CALL", "MODE_IN_COMMUNICATION"};
    private static final int BITS_PER_SAMPLE = 16;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_FRAME_PER_BUFFER = 256;
    private static final String TAG = "WebRtcAudioManager";
    private static boolean blacklistDeviceForOpenSLESUsage = false;
    private static boolean blacklistDeviceForOpenSLESUsageIsOverridden = false;
    public static int sMode = 3;
    private static boolean useStereoInput = false;
    private static boolean useStereoOutput = false;
    /* access modifiers changed from: private */
    public final AudioManager audioManager;
    private boolean hardwareAEC;
    private boolean hardwareAGC;
    private boolean hardwareNS;
    private boolean initialized = false;
    private int inputBufferSize;
    private int inputChannels;
    private boolean lowLatencyInput;
    private boolean lowLatencyOutput;
    private final long nativeAudioManager;
    private int nativeChannels;
    private int nativeSampleRate;
    private int outputBufferSize;
    private int outputChannels;
    private boolean proAudio;
    private int sampleRate;
    private final VolumeLogger volumeLogger;

    private native void nativeCacheAudioParameters(int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i4, int i5, long j);

    public static synchronized void setBlacklistDeviceForOpenSLESUsage(boolean enable) {
        synchronized (WebRtcAudioManager.class) {
            blacklistDeviceForOpenSLESUsageIsOverridden = true;
            blacklistDeviceForOpenSLESUsage = enable;
        }
    }

    public static synchronized void setStereoOutput(boolean enable) {
        synchronized (WebRtcAudioManager.class) {
            Logging.w(TAG, "Overriding default output behavior: setStereoOutput(" + enable + ')');
            useStereoOutput = enable;
        }
    }

    public static synchronized void setStereoInput(boolean enable) {
        synchronized (WebRtcAudioManager.class) {
            Logging.w(TAG, "Overriding default input behavior: setStereoInput(" + enable + ')');
            useStereoInput = enable;
        }
    }

    public static synchronized boolean getStereoOutput() {
        boolean z;
        synchronized (WebRtcAudioManager.class) {
            z = useStereoOutput;
        }
        return z;
    }

    public static synchronized boolean getStereoInput() {
        boolean z;
        synchronized (WebRtcAudioManager.class) {
            z = useStereoInput;
        }
        return z;
    }

    private static class VolumeLogger {
        private static final String THREAD_NAME = "WebRtcVolumeLevelLoggerThread";
        private static final int TIMER_PERIOD_IN_SECONDS = 30;
        /* access modifiers changed from: private */
        public final AudioManager audioManager;
        private Timer timer;

        public VolumeLogger(AudioManager audioManager2) {
            this.audioManager = audioManager2;
        }

        public void start() {
            Timer timer2 = new Timer(THREAD_NAME);
            this.timer = timer2;
            timer2.schedule(new LogVolumeTask(this.audioManager.getStreamMaxVolume(2), this.audioManager.getStreamMaxVolume(0)), 0, 30000);
        }

        private class LogVolumeTask extends TimerTask {
            private final int maxRingVolume;
            private final int maxVoiceCallVolume;

            LogVolumeTask(int maxRingVolume2, int maxVoiceCallVolume2) {
                this.maxRingVolume = maxRingVolume2;
                this.maxVoiceCallVolume = maxVoiceCallVolume2;
            }

            public void run() {
                int mode = VolumeLogger.this.audioManager.getMode();
                if (mode == 1) {
                    Logging.d(WebRtcAudioManager.TAG, "STREAM_RING stream volume: " + VolumeLogger.this.audioManager.getStreamVolume(2) + " (max=" + this.maxRingVolume + SQLBuilder.PARENTHESES_RIGHT);
                } else if (mode == 3) {
                    Logging.d(WebRtcAudioManager.TAG, "VOICE_CALL stream volume: " + VolumeLogger.this.audioManager.getStreamVolume(0) + " (max=" + this.maxVoiceCallVolume + SQLBuilder.PARENTHESES_RIGHT);
                }
            }
        }

        /* access modifiers changed from: private */
        public void stop() {
            Timer timer2 = this.timer;
            if (timer2 != null) {
                timer2.cancel();
                this.timer = null;
            }
        }
    }

    WebRtcAudioManager(long nativeAudioManager2, boolean ManualConfigSampleRate, int sample_Rate, boolean isManualConfigAEC, boolean isUseHardwareAEC) {
        Logging.d(TAG, "ctor" + WebRtcAudioUtils.getThreadInfo());
        long j = nativeAudioManager2;
        this.nativeAudioManager = j;
        AudioManager audioManager2 = (AudioManager) ContextUtils.getApplicationContext().getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.audioManager = audioManager2;
        this.volumeLogger = new VolumeLogger(audioManager2);
        storeAudioParameters(ManualConfigSampleRate, sample_Rate, isManualConfigAEC, isUseHardwareAEC);
        int i = this.sampleRate;
        int i2 = this.outputChannels;
        int i3 = this.inputChannels;
        boolean z = this.hardwareAEC;
        boolean z2 = this.hardwareAGC;
        boolean z3 = this.hardwareNS;
        boolean z4 = this.lowLatencyOutput;
        boolean z5 = this.lowLatencyInput;
        boolean z6 = this.proAudio;
        nativeCacheAudioParameters(i, i2, i3, z, z2, z3, z4, z5, z6, this.outputBufferSize, this.inputBufferSize, j);
    }

    public void setMode(int mode) {
        AudioManager audioManager2 = this.audioManager;
        if (audioManager2 != null) {
            audioManager2.setMode(audioManager2.isSpeakerphoneOn() ? mode : 3);
            sMode = mode;
        }
    }

    private void resetSpeakerphone() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                boolean isSpeakerphoneOn = WebRtcAudioManager.this.audioManager.isSpeakerphoneOn();
                if (isSpeakerphoneOn) {
                    WebRtcAudioManager.this.audioManager.setSpeakerphoneOn(!isSpeakerphoneOn);
                    WebRtcAudioManager.this.audioManager.setSpeakerphoneOn(isSpeakerphoneOn);
                }
            }
        });
    }

    private boolean init() {
        Logging.d(TAG, "init" + WebRtcAudioUtils.getThreadInfo());
        if (this.initialized) {
            return true;
        }
        Logging.d(TAG, "audio mode is: " + AUDIO_MODES[this.audioManager.getMode()]);
        this.initialized = true;
        this.volumeLogger.start();
        return true;
    }

    private void dispose() {
        Logging.d(TAG, "dispose" + WebRtcAudioUtils.getThreadInfo());
        if (this.initialized) {
            this.volumeLogger.stop();
        }
    }

    private boolean isCommunicationModeEnabled() {
        return this.audioManager.getMode() == 3;
    }

    private boolean isDeviceBlacklistedForOpenSLESUsage() {
        boolean blacklisted;
        if (blacklistDeviceForOpenSLESUsageIsOverridden) {
            blacklisted = blacklistDeviceForOpenSLESUsage;
        } else {
            blacklisted = WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage();
        }
        if (blacklisted) {
            Logging.e(TAG, Build.MODEL + " is blacklisted for OpenSL ES usage!");
        }
        return blacklisted;
    }

    private void storeAudioParameters(boolean ManualConfigSampleRate, int sample_Rate, boolean isManualConfigAEC, boolean isUseHardwareAEC) {
        int i;
        int i2;
        int i3 = 2;
        this.outputChannels = getStereoOutput() ? 2 : 1;
        if (!getStereoInput()) {
            i3 = 1;
        }
        this.inputChannels = i3;
        if (ManualConfigSampleRate) {
            this.sampleRate = sample_Rate;
        } else {
            this.sampleRate = getNativeOutputSampleRate();
        }
        if (isManualConfigAEC) {
            this.hardwareAEC = isUseHardwareAEC;
        } else {
            this.hardwareAEC = isAcousticEchoCancelerSupported();
        }
        this.hardwareAGC = false;
        this.hardwareNS = isNoiseSuppressorSupported();
        this.lowLatencyOutput = isLowLatencyOutputSupported();
        this.lowLatencyInput = isLowLatencyInputSupported();
        this.proAudio = isProAudioSupported();
        if (this.lowLatencyOutput) {
            i = getLowLatencyOutputFramesPerBuffer();
        } else {
            i = getMinOutputFrameSize(this.sampleRate, this.outputChannels);
        }
        this.outputBufferSize = i;
        if (this.lowLatencyInput) {
            i2 = getLowLatencyInputFramesPerBuffer();
        } else {
            i2 = getMinInputFrameSize(this.sampleRate, this.inputChannels);
        }
        this.inputBufferSize = i2;
    }

    private boolean hasEarpiece() {
        return ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    private boolean isLowLatencyOutputSupported() {
        return ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    public boolean isLowLatencyInputSupported() {
        return WebRtcAudioUtils.runningOnLollipopOrHigher() && isLowLatencyOutputSupported();
    }

    private boolean isProAudioSupported() {
        return WebRtcAudioUtils.runningOnMarshmallowOrHigher() && ContextUtils.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.audio.pro");
    }

    private int getNativeOutputSampleRate() {
        int sampleRateHz;
        if (WebRtcAudioUtils.runningOnEmulator()) {
            Logging.d(TAG, "Running emulator, overriding sample rate to 8 kHz.");
            return 8000;
        } else if (WebRtcAudioUtils.isDefaultSampleRateOverridden()) {
            Logging.d(TAG, "Default sample rate is overriden to " + WebRtcAudioUtils.getDefaultSampleRateHz() + " Hz");
            return WebRtcAudioUtils.getDefaultSampleRateHz();
        } else {
            if (WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher()) {
                sampleRateHz = getSampleRateOnJellyBeanMR10OrHigher();
            } else {
                sampleRateHz = WebRtcAudioUtils.getDefaultSampleRateHz();
            }
            Logging.d(TAG, "Sample rate is set to " + sampleRateHz + " Hz");
            return sampleRateHz;
        }
    }

    private int getSampleRateOnJellyBeanMR10OrHigher() {
        String sampleRateString = this.audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
        if (sampleRateString == null) {
            return WebRtcAudioUtils.getDefaultSampleRateHz();
        }
        return Integer.parseInt(sampleRateString);
    }

    private int getLowLatencyOutputFramesPerBuffer() {
        String framesPerBuffer;
        assertTrue(isLowLatencyOutputSupported());
        if (WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher() && (framesPerBuffer = this.audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")) != null) {
            return Integer.parseInt(framesPerBuffer);
        }
        return 256;
    }

    private static boolean isAcousticEchoCancelerSupported() {
        return WebRtcAudioEffects.canUseAcousticEchoCanceler();
    }

    private static boolean isNoiseSuppressorSupported() {
        return WebRtcAudioEffects.canUseNoiseSuppressor();
    }

    private static int getMinOutputFrameSize(int sampleRateInHz, int numChannels) {
        return AudioTrack.getMinBufferSize(sampleRateInHz, numChannels == 1 ? 4 : 12, 2) / (numChannels * 2);
    }

    private int getLowLatencyInputFramesPerBuffer() {
        assertTrue(isLowLatencyInputSupported());
        return getLowLatencyOutputFramesPerBuffer();
    }

    private static int getMinInputFrameSize(int sampleRateInHz, int numChannels) {
        return AudioRecord.getMinBufferSize(sampleRateInHz, numChannels == 1 ? 16 : 12, 2) / (numChannels * 2);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }
}
