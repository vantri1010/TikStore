package org.webrtc.ali;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

public class HardwareVideoDecoderFactory implements VideoDecoderFactory {
    private static final String TAG = "HardwareVideoDecoderFactory";

    public VideoDecoder createDecoder(String codecType) {
        VideoCodecType type = VideoCodecType.valueOf(codecType);
        MediaCodecInfo info = findCodecForType(type);
        if (info == null) {
            return null;
        }
        return new HardwareVideoDecoder(info.getName(), type, MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())).intValue());
    }

    private MediaCodecInfo findCodecForType(VideoCodecType type) {
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        for (int i = 0; i < MediaCodecList.getCodecCount(); i++) {
            MediaCodecInfo info = null;
            try {
                info = MediaCodecList.getCodecInfoAt(i);
            } catch (IllegalArgumentException e) {
                Logging.e(TAG, "Cannot retrieve encoder codec info", e);
            }
            if (info != null && !info.isEncoder() && isSupportedCodec(info, type)) {
                return info;
            }
        }
        return null;
    }

    private boolean isSupportedCodec(MediaCodecInfo info, VideoCodecType type) {
        if (MediaCodecUtils.codecSupportsType(info, type) && MediaCodecUtils.selectColorFormat(MediaCodecUtils.DECODER_COLOR_FORMATS, info.getCapabilitiesForType(type.mimeType())) != null) {
            return isHardwareSupported(info, type);
        }
        return false;
    }

    /* renamed from: org.webrtc.ali.HardwareVideoDecoderFactory$1  reason: invalid class name */
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

    private boolean isHardwareSupported(MediaCodecInfo info, VideoCodecType type) {
        String name = info.getName();
        int i = AnonymousClass1.$SwitchMap$org$webrtc$ali$VideoCodecType[type.ordinal()];
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return false;
                }
                if (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Intel.") || name.startsWith("OMX.Exynos.")) {
                    return true;
                }
                return false;
            } else if (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Exynos.")) {
                return true;
            } else {
                return false;
            }
        } else if (name.startsWith("OMX.qcom.") || name.startsWith("OMX.Intel.") || name.startsWith("OMX.Exynos.") || name.startsWith("OMX.Nvidia.")) {
            return true;
        } else {
            return false;
        }
    }
}
