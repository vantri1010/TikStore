package org.webrtc.ali.voiceengine;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import java.util.Arrays;
import java.util.List;
import org.webrtc.ali.Logging;

public final class WebRtcAudioUtils {
    private static final String[] BLACKLISTED_AEC_MODELS = new String[0];
    private static final String[] BLACKLISTED_NS_MODELS = new String[0];
    private static final String[] BLACKLISTED_OPEN_SL_ES_MODELS = new String[0];
    private static final int DEFAULT_SAMPLE_RATE_HZ = 16000;
    private static final String TAG = "WebRtcAudioUtils";
    private static int defaultSampleRateHz = 16000;
    private static boolean isDefaultSampleRateOverridden = false;
    private static boolean useWebRtcBasedAcousticEchoCanceler = false;
    private static boolean useWebRtcBasedNoiseSuppressor = false;

    public static synchronized void setWebRtcBasedAcousticEchoCanceler(boolean enable) {
        synchronized (WebRtcAudioUtils.class) {
            useWebRtcBasedAcousticEchoCanceler = enable;
        }
    }

    public static synchronized void setWebRtcBasedNoiseSuppressor(boolean enable) {
        synchronized (WebRtcAudioUtils.class) {
            useWebRtcBasedNoiseSuppressor = enable;
        }
    }

    public static synchronized void setWebRtcBasedAutomaticGainControl(boolean enable) {
        synchronized (WebRtcAudioUtils.class) {
            Logging.w(TAG, "setWebRtcBasedAutomaticGainControl() is deprecated");
        }
    }

    public static synchronized boolean useWebRtcBasedAcousticEchoCanceler() {
        boolean z;
        synchronized (WebRtcAudioUtils.class) {
            if (useWebRtcBasedAcousticEchoCanceler) {
                Logging.w(TAG, "Overriding default behavior; now using WebRTC AEC!");
            }
            z = useWebRtcBasedAcousticEchoCanceler;
        }
        return z;
    }

    public static synchronized boolean useWebRtcBasedNoiseSuppressor() {
        boolean z;
        synchronized (WebRtcAudioUtils.class) {
            if (useWebRtcBasedNoiseSuppressor) {
                Logging.w(TAG, "Overriding default behavior; now using WebRTC NS!");
            }
            z = useWebRtcBasedNoiseSuppressor;
        }
        return z;
    }

    public static synchronized boolean useWebRtcBasedAutomaticGainControl() {
        synchronized (WebRtcAudioUtils.class) {
        }
        return true;
    }

    public static boolean isAcousticEchoCancelerSupported() {
        return WebRtcAudioEffects.canUseAcousticEchoCanceler();
    }

    public static boolean isNoiseSuppressorSupported() {
        return WebRtcAudioEffects.canUseNoiseSuppressor();
    }

    public static boolean isAutomaticGainControlSupported() {
        return false;
    }

    public static synchronized void setDefaultSampleRateHz(int sampleRateHz) {
        synchronized (WebRtcAudioUtils.class) {
            isDefaultSampleRateOverridden = true;
            defaultSampleRateHz = sampleRateHz;
        }
    }

    public static synchronized boolean isDefaultSampleRateOverridden() {
        boolean z;
        synchronized (WebRtcAudioUtils.class) {
            z = isDefaultSampleRateOverridden;
        }
        return z;
    }

    public static synchronized int getDefaultSampleRateHz() {
        int i;
        synchronized (WebRtcAudioUtils.class) {
            i = defaultSampleRateHz;
        }
        return i;
    }

    public static List<String> getBlackListedModelsForAecUsage() {
        return Arrays.asList(BLACKLISTED_AEC_MODELS);
    }

    public static List<String> getBlackListedModelsForNsUsage() {
        return Arrays.asList(BLACKLISTED_NS_MODELS);
    }

    public static boolean runningOnJellyBeanMR1OrHigher() {
        return Build.VERSION.SDK_INT >= 17;
    }

    public static boolean runningOnJellyBeanMR2OrHigher() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean runningOnLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean runningOnMarshmallowOrHigher() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean runningOnNougatOrHigher() {
        return Build.VERSION.SDK_INT >= 24;
    }

    public static String getThreadInfo() {
        return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
    }

    public static boolean runningOnEmulator() {
        return Build.HARDWARE.equals("goldfish") && Build.BRAND.startsWith("generic_");
    }

    public static boolean deviceIsBlacklistedForOpenSLESUsage() {
        return Arrays.asList(BLACKLISTED_OPEN_SL_ES_MODELS).contains(Build.MODEL);
    }

    public static void logDeviceInfo(String tag) {
        Logging.d(tag, "Android SDK: " + Build.VERSION.SDK_INT + ", Release: " + Build.VERSION.RELEASE + ", Brand: " + Build.BRAND + ", Device: " + Build.DEVICE + ", Id: " + Build.ID + ", Hardware: " + Build.HARDWARE + ", Manufacturer: " + Build.MANUFACTURER + ", Model: " + Build.MODEL + ", Product: " + Build.PRODUCT);
    }

    public static boolean hasPermission(Context context, String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid()) == 0;
    }
}
