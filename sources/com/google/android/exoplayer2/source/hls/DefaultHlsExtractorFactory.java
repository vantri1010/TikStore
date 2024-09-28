package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer2.extractor.mp4.Track;
import com.google.android.exoplayer2.extractor.ts.Ac3Extractor;
import com.google.android.exoplayer2.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class DefaultHlsExtractorFactory implements HlsExtractorFactory {
    public static final String AAC_FILE_EXTENSION = ".aac";
    public static final String AC3_FILE_EXTENSION = ".ac3";
    public static final String CMF_FILE_EXTENSION_PREFIX = ".cmf";
    public static final String EC3_FILE_EXTENSION = ".ec3";
    public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
    public static final String MP3_FILE_EXTENSION = ".mp3";
    public static final String MP4_FILE_EXTENSION = ".mp4";
    public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
    public static final String VTT_FILE_EXTENSION = ".vtt";
    public static final String WEBVTT_FILE_EXTENSION = ".webvtt";
    private final int payloadReaderFactoryFlags;

    public DefaultHlsExtractorFactory() {
        this(0);
    }

    public DefaultHlsExtractorFactory(int payloadReaderFactoryFlags2) {
        this.payloadReaderFactoryFlags = payloadReaderFactoryFlags2;
    }

    public Pair<Extractor, Boolean> createExtractor(Extractor previousExtractor, Uri uri, Format format, List<Format> muxedCaptionFormats, DrmInitData drmInitData, TimestampAdjuster timestampAdjuster, Map<String, List<String>> map, ExtractorInput extractorInput) throws InterruptedException, IOException {
        List<Format> list;
        Extractor extractor = previousExtractor;
        Format format2 = format;
        List<Format> list2 = muxedCaptionFormats;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        ExtractorInput extractorInput2 = extractorInput;
        if (extractor == null) {
            Extractor extractorByFileExtension = createExtractorByFileExtension(uri, format, muxedCaptionFormats, drmInitData, timestampAdjuster);
            extractorInput.resetPeekPosition();
            if (sniffQuietly(extractorByFileExtension, extractorInput2)) {
                return buildResult(extractorByFileExtension);
            }
            if (!(extractorByFileExtension instanceof WebvttExtractor)) {
                WebvttExtractor webvttExtractor = new WebvttExtractor(format2.language, timestampAdjuster2);
                if (sniffQuietly(webvttExtractor, extractorInput2)) {
                    return buildResult(webvttExtractor);
                }
            }
            if (!(extractorByFileExtension instanceof AdtsExtractor)) {
                AdtsExtractor adtsExtractor = new AdtsExtractor();
                if (sniffQuietly(adtsExtractor, extractorInput2)) {
                    return buildResult(adtsExtractor);
                }
            }
            if (!(extractorByFileExtension instanceof Ac3Extractor)) {
                Ac3Extractor ac3Extractor = new Ac3Extractor();
                if (sniffQuietly(ac3Extractor, extractorInput2)) {
                    return buildResult(ac3Extractor);
                }
            }
            if (!(extractorByFileExtension instanceof Mp3Extractor)) {
                Mp3Extractor mp3Extractor = new Mp3Extractor(0, 0);
                if (sniffQuietly(mp3Extractor, extractorInput2)) {
                    return buildResult(mp3Extractor);
                }
            }
            if (!(extractorByFileExtension instanceof FragmentedMp4Extractor)) {
                if (list2 != null) {
                    list = list2;
                } else {
                    list = Collections.emptyList();
                }
                FragmentedMp4Extractor fragmentedMp4Extractor = new FragmentedMp4Extractor(0, timestampAdjuster, (Track) null, drmInitData, list);
                if (sniffQuietly(fragmentedMp4Extractor, extractorInput2)) {
                    return buildResult(fragmentedMp4Extractor);
                }
            }
            if (!(extractorByFileExtension instanceof TsExtractor)) {
                TsExtractor tsExtractor = createTsExtractor(this.payloadReaderFactoryFlags, format2, list2, timestampAdjuster2);
                if (sniffQuietly(tsExtractor, extractorInput2)) {
                    return buildResult(tsExtractor);
                }
            }
            return buildResult(extractorByFileExtension);
        } else if ((extractor instanceof TsExtractor) || (extractor instanceof FragmentedMp4Extractor)) {
            return buildResult(previousExtractor);
        } else {
            if (extractor instanceof WebvttExtractor) {
                return buildResult(new WebvttExtractor(format2.language, timestampAdjuster2));
            }
            if (extractor instanceof AdtsExtractor) {
                return buildResult(new AdtsExtractor());
            }
            if (extractor instanceof Ac3Extractor) {
                return buildResult(new Ac3Extractor());
            }
            if (extractor instanceof Mp3Extractor) {
                return buildResult(new Mp3Extractor());
            }
            throw new IllegalArgumentException("Unexpected previousExtractor type: " + previousExtractor.getClass().getSimpleName());
        }
    }

    private Extractor createExtractorByFileExtension(Uri uri, Format format, List<Format> muxedCaptionFormats, DrmInitData drmInitData, TimestampAdjuster timestampAdjuster) {
        List<Format> list;
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment == null) {
            lastPathSegment = "";
        }
        if (MimeTypes.TEXT_VTT.equals(format.sampleMimeType) || lastPathSegment.endsWith(WEBVTT_FILE_EXTENSION) || lastPathSegment.endsWith(VTT_FILE_EXTENSION)) {
            return new WebvttExtractor(format.language, timestampAdjuster);
        }
        if (lastPathSegment.endsWith(AAC_FILE_EXTENSION)) {
            return new AdtsExtractor();
        }
        if (lastPathSegment.endsWith(AC3_FILE_EXTENSION) || lastPathSegment.endsWith(EC3_FILE_EXTENSION)) {
            return new Ac3Extractor();
        }
        if (lastPathSegment.endsWith(MP3_FILE_EXTENSION)) {
            return new Mp3Extractor(0, 0);
        }
        if (!lastPathSegment.endsWith(".mp4") && !lastPathSegment.startsWith(M4_FILE_EXTENSION_PREFIX, lastPathSegment.length() - 4) && !lastPathSegment.startsWith(".mp4", lastPathSegment.length() - 5) && !lastPathSegment.startsWith(CMF_FILE_EXTENSION_PREFIX, lastPathSegment.length() - 5)) {
            return createTsExtractor(this.payloadReaderFactoryFlags, format, muxedCaptionFormats, timestampAdjuster);
        }
        if (muxedCaptionFormats != null) {
            list = muxedCaptionFormats;
        } else {
            list = Collections.emptyList();
        }
        return new FragmentedMp4Extractor(0, timestampAdjuster, (Track) null, drmInitData, list);
    }

    private static TsExtractor createTsExtractor(int userProvidedPayloadReaderFactoryFlags, Format format, List<Format> muxedCaptionFormats, TimestampAdjuster timestampAdjuster) {
        int payloadReaderFactoryFlags2 = userProvidedPayloadReaderFactoryFlags | 16;
        if (muxedCaptionFormats != null) {
            payloadReaderFactoryFlags2 |= 32;
        } else {
            muxedCaptionFormats = Collections.singletonList(Format.createTextSampleFormat((String) null, MimeTypes.APPLICATION_CEA608, 0, (String) null));
        }
        String codecs = format.codecs;
        if (!TextUtils.isEmpty(codecs)) {
            if (!MimeTypes.AUDIO_AAC.equals(MimeTypes.getAudioMediaMimeType(codecs))) {
                payloadReaderFactoryFlags2 |= 2;
            }
            if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(codecs))) {
                payloadReaderFactoryFlags2 |= 4;
            }
        }
        return new TsExtractor(2, timestampAdjuster, new DefaultTsPayloadReaderFactory(payloadReaderFactoryFlags2, muxedCaptionFormats));
    }

    private static Pair<Extractor, Boolean> buildResult(Extractor extractor) {
        return new Pair<>(extractor, Boolean.valueOf((extractor instanceof AdtsExtractor) || (extractor instanceof Ac3Extractor) || (extractor instanceof Mp3Extractor)));
    }

    private static boolean sniffQuietly(Extractor extractor, ExtractorInput input) throws InterruptedException, IOException {
        boolean result = false;
        try {
            result = extractor.sniff(input);
        } catch (EOFException e) {
        } catch (Throwable th) {
            input.resetPeekPosition();
            throw th;
        }
        input.resetPeekPosition();
        return result;
    }
}
