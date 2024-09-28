package com.google.android.exoplayer2.mediacodec;

import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import java.util.Collections;
import java.util.List;

public interface MediaCodecSelector {
    public static final MediaCodecSelector DEFAULT = new MediaCodecSelector() {
        public List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean requiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
            List<MediaCodecInfo> decoderInfos = MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder);
            if (decoderInfos.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singletonList(decoderInfos.get(0));
        }

        public MediaCodecInfo getPassthroughDecoderInfo() throws MediaCodecUtil.DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    };
    public static final MediaCodecSelector DEFAULT_WITH_FALLBACK = new MediaCodecSelector() {
        public List<MediaCodecInfo> getDecoderInfos(String mimeType, boolean requiresSecureDecoder) throws MediaCodecUtil.DecoderQueryException {
            return MediaCodecUtil.getDecoderInfos(mimeType, requiresSecureDecoder);
        }

        public MediaCodecInfo getPassthroughDecoderInfo() throws MediaCodecUtil.DecoderQueryException {
            return MediaCodecUtil.getPassthroughDecoderInfo();
        }
    };

    List<MediaCodecInfo> getDecoderInfos(String str, boolean z) throws MediaCodecUtil.DecoderQueryException;

    MediaCodecInfo getPassthroughDecoderInfo() throws MediaCodecUtil.DecoderQueryException;
}
