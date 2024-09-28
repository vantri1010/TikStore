package com.google.android.exoplayer2.source.hls.playlist;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class HlsMasterPlaylist extends HlsPlaylist {
    public static final HlsMasterPlaylist EMPTY = new HlsMasterPlaylist("", Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), (Format) null, Collections.emptyList(), false, Collections.emptyMap());
    public static final int GROUP_INDEX_AUDIO = 1;
    public static final int GROUP_INDEX_SUBTITLE = 2;
    public static final int GROUP_INDEX_VARIANT = 0;
    public final List<HlsUrl> audios;
    public final Format muxedAudioFormat;
    public final List<Format> muxedCaptionFormats;
    public final List<HlsUrl> subtitles;
    public final Map<String, String> variableDefinitions;
    public final List<HlsUrl> variants;

    public static final class HlsUrl {
        public final Format format;
        public final String url;

        public static HlsUrl createMediaPlaylistHlsUrl(String url2) {
            return new HlsUrl(url2, Format.createContainerFormat("0", (String) null, MimeTypes.APPLICATION_M3U8, (String) null, (String) null, -1, 0, (String) null));
        }

        public HlsUrl(String url2, Format format2) {
            this.url = url2;
            this.format = format2;
        }
    }

    public HlsMasterPlaylist(String baseUri, List<String> tags, List<HlsUrl> variants2, List<HlsUrl> audios2, List<HlsUrl> subtitles2, Format muxedAudioFormat2, List<Format> muxedCaptionFormats2, boolean hasIndependentSegments, Map<String, String> variableDefinitions2) {
        super(baseUri, tags, hasIndependentSegments);
        this.variants = Collections.unmodifiableList(variants2);
        this.audios = Collections.unmodifiableList(audios2);
        this.subtitles = Collections.unmodifiableList(subtitles2);
        this.muxedAudioFormat = muxedAudioFormat2;
        this.muxedCaptionFormats = muxedCaptionFormats2 != null ? Collections.unmodifiableList(muxedCaptionFormats2) : null;
        this.variableDefinitions = Collections.unmodifiableMap(variableDefinitions2);
    }

    public HlsMasterPlaylist copy(List<StreamKey> streamKeys) {
        return new HlsMasterPlaylist(this.baseUri, this.tags, copyRenditionsList(this.variants, 0, streamKeys), copyRenditionsList(this.audios, 1, streamKeys), copyRenditionsList(this.subtitles, 2, streamKeys), this.muxedAudioFormat, this.muxedCaptionFormats, this.hasIndependentSegments, this.variableDefinitions);
    }

    public static HlsMasterPlaylist createSingleVariantMasterPlaylist(String variantUrl) {
        List<HlsUrl> variant = Collections.singletonList(HlsUrl.createMediaPlaylistHlsUrl(variantUrl));
        List<HlsUrl> emptyList = Collections.emptyList();
        return new HlsMasterPlaylist((String) null, Collections.emptyList(), variant, emptyList, emptyList, (Format) null, (List<Format>) null, false, Collections.emptyMap());
    }

    private static List<HlsUrl> copyRenditionsList(List<HlsUrl> renditions, int groupIndex, List<StreamKey> streamKeys) {
        List<HlsUrl> copiedRenditions = new ArrayList<>(streamKeys.size());
        for (int i = 0; i < renditions.size(); i++) {
            HlsUrl rendition = renditions.get(i);
            int j = 0;
            while (true) {
                if (j >= streamKeys.size()) {
                    break;
                }
                StreamKey streamKey = streamKeys.get(j);
                if (streamKey.groupIndex == groupIndex && streamKey.trackIndex == i) {
                    copiedRenditions.add(rendition);
                    break;
                }
                j++;
            }
        }
        return copiedRenditions;
    }
}
