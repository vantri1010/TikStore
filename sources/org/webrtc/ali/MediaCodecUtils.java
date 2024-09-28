package org.webrtc.ali;

import android.media.MediaCodecInfo;

class MediaCodecUtils {
    static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka = 2141391874;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka = 2141391873;
    static final int COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka = 2141391875;
    static final int[] DECODER_COLOR_FORMATS = {19, 21, 2141391872, COLOR_QCOM_FORMATYVU420PackedSemiPlanar32m4ka, COLOR_QCOM_FORMATYVU420PackedSemiPlanar16m4ka, COLOR_QCOM_FORMATYVU420PackedSemiPlanar64x32Tile2m8ka, COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m};
    static final int[] ENCODER_COLOR_FORMATS = {19, 21, 2141391872, COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m};
    static final String EXYNOS_PREFIX = "OMX.Exynos.";
    static final String INTEL_PREFIX = "OMX.Intel.";
    static final String NVIDIA_PREFIX = "OMX.Nvidia.";
    static final String QCOM_PREFIX = "OMX.qcom.";
    private static final String TAG = "MediaCodecUtils";

    static Integer selectColorFormat(int[] supportedColorFormats, MediaCodecInfo.CodecCapabilities capabilities) {
        for (int supportedColorFormat : supportedColorFormats) {
            for (int codecColorFormat : capabilities.colorFormats) {
                if (codecColorFormat == supportedColorFormat) {
                    return Integer.valueOf(codecColorFormat);
                }
            }
        }
        return null;
    }

    static boolean codecSupportsType(MediaCodecInfo info, VideoCodecType type) {
        for (String mimeType : info.getSupportedTypes()) {
            if (type.mimeType().equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private MediaCodecUtils() {
    }
}
