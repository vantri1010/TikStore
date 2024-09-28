package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import java.io.IOException;

public interface HlsPlaylistTracker {

    public interface Factory {
        HlsPlaylistTracker createTracker(HlsDataSourceFactory hlsDataSourceFactory, LoadErrorHandlingPolicy loadErrorHandlingPolicy, HlsPlaylistParserFactory hlsPlaylistParserFactory);
    }

    public interface PlaylistEventListener {
        void onPlaylistChanged();

        boolean onPlaylistError(HlsMasterPlaylist.HlsUrl hlsUrl, long j);
    }

    public interface PrimaryPlaylistListener {
        void onPrimaryPlaylistRefreshed(HlsMediaPlaylist hlsMediaPlaylist);
    }

    void addListener(PlaylistEventListener playlistEventListener);

    long getInitialStartTimeUs();

    HlsMasterPlaylist getMasterPlaylist();

    HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl hlsUrl, boolean z);

    boolean isLive();

    boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl hlsUrl);

    void maybeThrowPlaylistRefreshError(HlsMasterPlaylist.HlsUrl hlsUrl) throws IOException;

    void maybeThrowPrimaryPlaylistRefreshError() throws IOException;

    void refreshPlaylist(HlsMasterPlaylist.HlsUrl hlsUrl);

    void removeListener(PlaylistEventListener playlistEventListener);

    void start(Uri uri, MediaSourceEventListener.EventDispatcher eventDispatcher, PrimaryPlaylistListener primaryPlaylistListener);

    void stop();

    public static final class PlaylistStuckException extends IOException {
        public final String url;

        public PlaylistStuckException(String url2) {
            this.url = url2;
        }
    }

    public static final class PlaylistResetException extends IOException {
        public final String url;

        public PlaylistResetException(String url2) {
            this.url = url2;
        }
    }
}
