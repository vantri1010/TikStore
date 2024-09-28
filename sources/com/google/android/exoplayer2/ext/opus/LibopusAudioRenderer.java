package com.google.android.exoplayer2.ext.opus;

import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.SimpleDecoderAudioRenderer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.List;

public final class LibopusAudioRenderer extends SimpleDecoderAudioRenderer {
    private static final int DEFAULT_INPUT_BUFFER_SIZE = 5760;
    private static final int NUM_BUFFERS = 16;
    private OpusDecoder decoder;

    public LibopusAudioRenderer() {
        this((Handler) null, (AudioRendererEventListener) null, new AudioProcessor[0]);
    }

    public LibopusAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, audioProcessors);
    }

    public LibopusAudioRenderer(Handler eventHandler, AudioRendererEventListener eventListener, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, AudioProcessor... audioProcessors) {
        super(eventHandler, eventListener, (AudioCapabilities) null, drmSessionManager, playClearSamplesWithoutKeys, audioProcessors);
    }

    /* access modifiers changed from: protected */
    public int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format) {
        if (!MimeTypes.AUDIO_OPUS.equalsIgnoreCase(format.sampleMimeType)) {
            return 0;
        }
        if (!supportsOutput(format.channelCount, 2)) {
            return 1;
        }
        if (!supportsFormatDrm(drmSessionManager, format.drmInitData)) {
            return 2;
        }
        return 4;
    }

    /* access modifiers changed from: protected */
    public OpusDecoder createDecoder(Format format, ExoMediaCrypto mediaCrypto) throws OpusDecoderException {
        OpusDecoder opusDecoder = new OpusDecoder(16, 16, format.maxInputSize != -1 ? format.maxInputSize : DEFAULT_INPUT_BUFFER_SIZE, format.initializationData, mediaCrypto);
        this.decoder = opusDecoder;
        return opusDecoder;
    }

    /* access modifiers changed from: protected */
    public Format getOutputFormat() {
        return Format.createAudioSampleFormat((String) null, MimeTypes.AUDIO_RAW, (String) null, -1, -1, this.decoder.getChannelCount(), this.decoder.getSampleRate(), 2, (List<byte[]>) null, (DrmInitData) null, 0, (String) null);
    }
}
