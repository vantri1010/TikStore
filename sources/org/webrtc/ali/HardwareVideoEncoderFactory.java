package org.webrtc.ali;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardwareVideoEncoderFactory implements VideoEncoderFactory {
    private static final String H264_CONSTRAINED_BASELINE_3_1 = "42001f";
    private static final String H264_CONSTRAINED_HIGH_3_1 = "640c1f";
    private static final String H264_FMTP_LEVEL_ASYMMETRY_ALLOWED = "level-asymmetry-allowed";
    private static final String H264_FMTP_PACKETIZATION_MODE = "packetization-mode";
    private static final String H264_FMTP_PROFILE_LEVEL_ID = "profile-level-id";
    private static final List<String> H264_HW_EXCEPTION_MODELS = Arrays.asList(new String[]{"SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4"});
    private static final String H264_LEVEL_3_1 = "1f";
    private static final String H264_PROFILE_CONSTRAINED_BASELINE = "4200";
    private static final String H264_PROFILE_CONSTRAINED_HIGH = "640c";
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_L_MS = 15000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS = 20000;
    private static final int QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_N_MS = 15000;
    private static final String TAG = "HardwareVideoEncoderFactory";
    private final boolean enableH264HighProfile;
    private final boolean enableIntelVp8Encoder;

    public HardwareVideoEncoderFactory(boolean enableIntelVp8Encoder2, boolean enableH264HighProfile2) {
        this.enableIntelVp8Encoder = enableIntelVp8Encoder2;
        this.enableH264HighProfile = enableH264HighProfile2;
    }

    public VideoEncoder createEncoder(VideoCodecInfo input) {
        VideoCodecType type = VideoCodecType.valueOf(input.name);
        MediaCodecInfo info = findCodecForType(type);
        if (info == null) {
            return null;
        }
        String codecName = info.getName();
        return new HardwareVideoEncoder(codecName, type, MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())).intValue(), getKeyFrameIntervalSec(type), getForcedKeyFrameIntervalMs(type, codecName), createBitrateAdjuster(type, codecName));
    }

    public VideoCodecInfo[] getSupportedCodecs() {
        List<VideoCodecInfo> supportedCodecInfos = new ArrayList<>();
        VideoCodecType[] videoCodecTypeArr = {VideoCodecType.VP8, VideoCodecType.VP9, VideoCodecType.H264};
        for (int i = 0; i < 3; i++) {
            VideoCodecType type = videoCodecTypeArr[i];
            MediaCodecInfo codec = findCodecForType(type);
            if (codec != null) {
                String name = type.name();
                if (type == VideoCodecType.H264 && isH264HighProfileSupported(codec)) {
                    supportedCodecInfos.add(new VideoCodecInfo(0, name, getCodecProperties(type, true)));
                }
                supportedCodecInfos.add(new VideoCodecInfo(0, name, getCodecProperties(type, false)));
            }
        }
        return (VideoCodecInfo[]) supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
    }

    private MediaCodecInfo findCodecForType(VideoCodecType type) {
        for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
            MediaCodecInfo info = null;
            try {
                info = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.e(TAG, "Cannot retrieve encoder codec info", e);
            }
            if (info != null && info.isEncoder() && isSupportedCodec(info, type)) {
                return info;
            }
        }
        return null;
    }

    private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecType type) {
        if (MediaCodecUtils.codecSupportsType(info, type) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())) != null) {
            return isHardwareSupportedInCurrentSdk(info, type);
        }
        return false;
    }

    /* renamed from: org.webrtc.ali.HardwareVideoEncoderFactory$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$ali$VideoCodecType;

        static {
            int[] iArr = new int[VideoCodecType.values().length];
            $SwitchMap$org$webrtc$ali$VideoCodecType = iArr;
            try {
                iArr[VideoCodecType.VP8.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$ali$VideoCodecType[VideoCodecType.VP9.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$webrtc$ali$VideoCodecType[VideoCodecType.H264.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private boolean isHardwareSupportedInCurrentSdk(MediaCodecInfo info, VideoCodecType type) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$ali$VideoCodecType[type.ordinal()];
        if (i == 1) {
            return isHardwareSupportedInCurrentSdkVp8(info);
        }
        if (i == 2) {
            return isHardwareSupportedInCurrentSdkVp9(info);
        }
        if (i != 3) {
            return false;
        }
        return isHardwareSupportedInCurrentSdkH264(info);
    }

    private boolean isHardwareSupportedInCurrentSdkVp8(MediaCodecInfo info) {
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") && Build.VERSION.SDK_INT >= 19) || (name.startsWith("OMX.Exynos.") && Build.VERSION.SDK_INT >= 23) || (name.startsWith("OMX.Intel.") && Build.VERSION.SDK_INT >= 21 && this.enableIntelVp8Encoder);
    }

    private boolean isHardwareSupportedInCurrentSdkVp9(MediaCodecInfo info) {
        String name = info.getName();
        return (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.")) && Build.VERSION.SDK_INT >= 24;
    }

    private boolean isHardwareSupportedInCurrentSdkH264(MediaCodecInfo info) {
        if (H264_HW_EXCEPTION_MODELS.contains(Build.MODEL)) {
            return false;
        }
        String name = info.getName();
        if ((!name.startsWith("OMX.qcom.") || Build.VERSION.SDK_INT < 19) && (!name.startsWith("OMX.Exynos.") || Build.VERSION.SDK_INT < 21)) {
            return false;
        }
        return true;
    }

    private int getKeyFrameIntervalSec(VideoCodecType type) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$ali$VideoCodecType[type.ordinal()];
        if (i == 1 || i == 2) {
            return 100;
        }
        if (i == 3) {
            return 20;
        }
        throw new IllegalArgumentException("Unsupported VideoCodecType " + type);
    }

    private int getForcedKeyFrameIntervalMs(VideoCodecType type, String codecName) {
        if (type != VideoCodecType.VP8 || !codecName.startsWith("OMX.qcom.")) {
            return 0;
        }
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            return 15000;
        }
        if (Build.VERSION.SDK_INT == 23) {
            return QCOM_VP8_KEY_FRAME_INTERVAL_ANDROID_M_MS;
        }
        if (Build.VERSION.SDK_INT > 23) {
            return 15000;
        }
        return 0;
    }

    private BitrateAdjuster createBitrateAdjuster(VideoCodecType type, String codecName) {
        if (!codecName.startsWith("OMX.Exynos.")) {
            return new BaseBitrateAdjuster();
        }
        if (type == VideoCodecType.VP8) {
            return new DynamicBitrateAdjuster();
        }
        return new FramerateBitrateAdjuster();
    }

    private boolean isH264HighProfileSupported(MediaCodecInfo info) {
        return this.enableH264HighProfile && info.getName().startsWith("OMX.qcom.");
    }

    private Map<String, String> getCodecProperties(VideoCodecType type, boolean highProfile) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$ali$VideoCodecType[type.ordinal()];
        if (i == 1 || i == 2) {
            return new HashMap();
        }
        if (i == 3) {
            Map<String, String> properties = new HashMap<>();
            properties.put(H264_FMTP_LEVEL_ASYMMETRY_ALLOWED, "1");
            properties.put(H264_FMTP_PACKETIZATION_MODE, "1");
            properties.put(H264_FMTP_PROFILE_LEVEL_ID, highProfile ? H264_CONSTRAINED_HIGH_3_1 : H264_CONSTRAINED_BASELINE_3_1);
            return properties;
        }
        throw new IllegalArgumentException("Unsupported codec: " + type);
    }
}
