package com.google.android.exoplayer2.source.hls.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.offline.SegmentDownloader;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public final class HlsDownloader extends SegmentDownloader<HlsPlaylist> {
    public HlsDownloader(Uri playlistUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        super(playlistUri, streamKeys, constructorHelper);
    }

    /* access modifiers changed from: protected */
    public HlsPlaylist getManifest(DataSource dataSource, DataSpec dataSpec) throws IOException {
        return loadManifest(dataSource, dataSpec);
    }

    /* access modifiers changed from: protected */
    public List<SegmentDownloader.Segment> getSegments(DataSource dataSource, HlsPlaylist playlist, boolean allowIncompleteList) throws IOException {
        String baseUri = playlist.baseUri;
        ArrayList<DataSpec> mediaPlaylistDataSpecs = new ArrayList<>();
        if (playlist instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) playlist;
            addMediaPlaylistDataSpecs(baseUri, masterPlaylist.variants, mediaPlaylistDataSpecs);
            addMediaPlaylistDataSpecs(baseUri, masterPlaylist.audios, mediaPlaylistDataSpecs);
            addMediaPlaylistDataSpecs(baseUri, masterPlaylist.subtitles, mediaPlaylistDataSpecs);
        } else {
            mediaPlaylistDataSpecs.add(SegmentDownloader.getCompressibleDataSpec(Uri.parse(baseUri)));
        }
        ArrayList<SegmentDownloader.Segment> segments = new ArrayList<>();
        HashSet<Uri> seenEncryptionKeyUris = new HashSet<>();
        Iterator<DataSpec> it = mediaPlaylistDataSpecs.iterator();
        while (it.hasNext()) {
            DataSpec mediaPlaylistDataSpec = it.next();
            segments.add(new SegmentDownloader.Segment(0, mediaPlaylistDataSpec));
            try {
                HlsMediaPlaylist mediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, mediaPlaylistDataSpec);
                HlsMediaPlaylist.Segment lastInitSegment = null;
                List<HlsMediaPlaylist.Segment> hlsSegments = mediaPlaylist.segments;
                for (int i = 0; i < hlsSegments.size(); i++) {
                    HlsMediaPlaylist.Segment segment = hlsSegments.get(i);
                    HlsMediaPlaylist.Segment initSegment = segment.initializationSegment;
                    if (!(initSegment == null || initSegment == lastInitSegment)) {
                        lastInitSegment = initSegment;
                        addSegment(mediaPlaylist, initSegment, seenEncryptionKeyUris, segments);
                    }
                    addSegment(mediaPlaylist, segment, seenEncryptionKeyUris, segments);
                }
            } catch (IOException e) {
                if (!allowIncompleteList) {
                    throw e;
                }
            }
        }
        return segments;
    }

    private void addMediaPlaylistDataSpecs(String baseUri, List<HlsMasterPlaylist.HlsUrl> urls, List<DataSpec> out) {
        for (int i = 0; i < urls.size(); i++) {
            out.add(SegmentDownloader.getCompressibleDataSpec(UriUtil.resolveToUri(baseUri, urls.get(i).url)));
        }
    }

    private static HlsPlaylist loadManifest(DataSource dataSource, DataSpec dataSpec) throws IOException {
        return (HlsPlaylist) ParsingLoadable.load(dataSource, new HlsPlaylistParser(), dataSpec, 4);
    }

    private void addSegment(HlsMediaPlaylist mediaPlaylist, HlsMediaPlaylist.Segment segment, HashSet<Uri> seenEncryptionKeyUris, ArrayList<SegmentDownloader.Segment> out) {
        HlsMediaPlaylist hlsMediaPlaylist = mediaPlaylist;
        HlsMediaPlaylist.Segment segment2 = segment;
        ArrayList<SegmentDownloader.Segment> arrayList = out;
        String baseUri = hlsMediaPlaylist.baseUri;
        long startTimeUs = hlsMediaPlaylist.startTimeUs + segment2.relativeStartTimeUs;
        if (segment2.fullSegmentEncryptionKeyUri != null) {
            Uri keyUri = UriUtil.resolveToUri(baseUri, segment2.fullSegmentEncryptionKeyUri);
            if (seenEncryptionKeyUris.add(keyUri)) {
                arrayList.add(new SegmentDownloader.Segment(startTimeUs, SegmentDownloader.getCompressibleDataSpec(keyUri)));
            }
        } else {
            HashSet<Uri> hashSet = seenEncryptionKeyUris;
        }
        arrayList.add(new SegmentDownloader.Segment(startTimeUs, new DataSpec(UriUtil.resolveToUri(baseUri, segment2.url), segment2.byterangeOffset, segment2.byterangeLength, (String) null)));
    }
}
