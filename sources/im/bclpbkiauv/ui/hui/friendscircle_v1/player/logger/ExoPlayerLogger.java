package im.bclpbkiauv.ui.hui.friendscircle_v1.player.logger;

import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.metadata.id3.CommentFrame;
import com.google.android.exoplayer2.metadata.id3.GeobFrame;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;
import com.google.android.exoplayer2.metadata.id3.UrlLinkFrame;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public final class ExoPlayerLogger implements Player.EventListener, AudioRendererEventListener, VideoRendererEventListener, AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener, DefaultDrmSessionManager.EventListener, MetadataRenderer.Output {
    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final String TAG = "EventLogger";
    private static final NumberFormat TIME_FORMAT;
    private final Timeline.Period period = new Timeline.Period();
    private final long startTimeMs = SystemClock.elapsedRealtime();
    private final MappingTrackSelector trackSelector;
    private final Timeline.Window window = new Timeline.Window();

    public /* synthetic */ void onAudioSinkUnderrun(int i, long j, long j2) {
        AudioRendererEventListener.CC.$default$onAudioSinkUnderrun(this, i, j, j2);
    }

    public /* synthetic */ void onDrmSessionAcquired() {
        DefaultDrmSessionEventListener.CC.$default$onDrmSessionAcquired(this);
    }

    public /* synthetic */ void onDrmSessionReleased() {
        DefaultDrmSessionEventListener.CC.$default$onDrmSessionReleased(this);
    }

    public /* synthetic */ void onPositionDiscontinuity(int i) {
        Player.EventListener.CC.$default$onPositionDiscontinuity(this, i);
    }

    public /* synthetic */ void onSeekProcessed() {
        Player.EventListener.CC.$default$onSeekProcessed(this);
    }

    public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
        Player.EventListener.CC.$default$onShuffleModeEnabledChanged(this, z);
    }

    public /* synthetic */ void onTimelineChanged(Timeline timeline, Object obj, int i) {
        Player.EventListener.CC.$default$onTimelineChanged(this, timeline, obj, i);
    }

    static {
        NumberFormat instance = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT = instance;
        instance.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    public ExoPlayerLogger(MappingTrackSelector trackSelector2) {
        this.trackSelector = trackSelector2;
    }

    public void onLoadingChanged(boolean isLoading) {
        Log.d(TAG, "loading [" + isLoading + "]");
    }

    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        Log.d(TAG, "state [" + getSessionTimeString() + ", " + playWhenReady + ", " + getStateString(state) + "]");
    }

    public void onRepeatModeChanged(int repeatMode) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    public void onPlayerError(ExoPlaybackException e) {
        Log.e(TAG, "playerFailed [" + getSessionTimeString() + "]", e);
    }

    public void onTracksChanged(TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        String str;
        String str2;
        ExoPlayerLogger exoPlayerLogger;
        ExoPlayerLogger exoPlayerLogger2 = this;
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = exoPlayerLogger2.trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            Log.d(TAG, "Tracks []");
            return;
        }
        Log.d(TAG, "Tracks [");
        int rendererIndex = 0;
        while (true) {
            str = "  ]";
            str2 = " [";
            if (rendererIndex >= mappedTrackInfo.length) {
                break;
            }
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            if (rendererTrackGroups.length > 0) {
                Log.d(TAG, "  Renderer:" + rendererIndex + str2);
                int groupIndex = 0;
                while (groupIndex < rendererTrackGroups.length) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    TrackGroupArray rendererTrackGroups2 = rendererTrackGroups;
                    String str3 = str;
                    String adaptiveSupport = getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false));
                    Log.d(TAG, "    Group:" + groupIndex + ", adaptive_supported=" + adaptiveSupport + str2);
                    int trackIndex = 0;
                    while (trackIndex < trackGroup.length) {
                        String status = getTrackStatusString(trackSelection, trackGroup, trackIndex);
                        String adaptiveSupport2 = adaptiveSupport;
                        String formatSupport = getFormatSupportString(mappedTrackInfo.getTrackFormatSupport(rendererIndex, groupIndex, trackIndex));
                        String str4 = str2;
                        StringBuilder sb = new StringBuilder();
                        sb.append("      ");
                        sb.append(status);
                        sb.append(" Track:");
                        sb.append(trackIndex);
                        sb.append(", ");
                        String str5 = status;
                        sb.append(Format.toLogString(trackGroup.getFormat(trackIndex)));
                        sb.append(", supported=");
                        sb.append(formatSupport);
                        Log.d(TAG, sb.toString());
                        trackIndex++;
                        str2 = str4;
                        adaptiveSupport = adaptiveSupport2;
                    }
                    String str6 = str2;
                    Log.d(TAG, "    ]");
                    groupIndex++;
                    rendererTrackGroups = rendererTrackGroups2;
                    str = str3;
                }
                String str7 = str;
                if (trackSelection != null) {
                    int selectionIndex = 0;
                    while (true) {
                        if (selectionIndex >= trackSelection.length()) {
                            exoPlayerLogger = this;
                            break;
                        }
                        Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                        if (metadata != null) {
                            Log.d(TAG, "    Metadata [");
                            exoPlayerLogger = this;
                            exoPlayerLogger.printMetadata(metadata, "      ");
                            Log.d(TAG, "    ]");
                            break;
                        }
                        selectionIndex++;
                    }
                } else {
                    exoPlayerLogger = this;
                }
                Log.d(TAG, str7);
            } else {
                exoPlayerLogger = exoPlayerLogger2;
                TrackGroupArray trackGroupArray = rendererTrackGroups;
            }
            rendererIndex++;
            exoPlayerLogger2 = exoPlayerLogger;
        }
        String str8 = str2;
        String str9 = str;
        ExoPlayerLogger exoPlayerLogger3 = exoPlayerLogger2;
        String str10 = str9;
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnassociatedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            Log.d(TAG, "  Renderer:None [");
            int groupIndex2 = 0;
            while (groupIndex2 < unassociatedTrackGroups.length) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("    Group:");
                sb2.append(groupIndex2);
                String str11 = str8;
                sb2.append(str11);
                Log.d(TAG, sb2.toString());
                TrackGroup trackGroup2 = unassociatedTrackGroups.get(groupIndex2);
                int trackIndex2 = 0;
                while (trackIndex2 < trackGroup2.length) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo2 = mappedTrackInfo;
                    String status2 = getTrackStatusString(false);
                    TrackGroupArray unassociatedTrackGroups2 = unassociatedTrackGroups;
                    String formatSupport2 = getFormatSupportString(0);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("      ");
                    sb3.append(status2);
                    sb3.append(" Track:");
                    sb3.append(trackIndex2);
                    sb3.append(", ");
                    String str12 = status2;
                    sb3.append(Format.toLogString(trackGroup2.getFormat(trackIndex2)));
                    sb3.append(", supported=");
                    sb3.append(formatSupport2);
                    Log.d(TAG, sb3.toString());
                    trackIndex2++;
                    mappedTrackInfo = mappedTrackInfo2;
                    unassociatedTrackGroups = unassociatedTrackGroups2;
                }
                TrackGroupArray trackGroupArray2 = unassociatedTrackGroups;
                Log.d(TAG, "    ]");
                groupIndex2++;
                str8 = str11;
            }
            TrackGroupArray trackGroupArray3 = unassociatedTrackGroups;
            Log.d(TAG, str10);
        } else {
            TrackGroupArray trackGroupArray4 = unassociatedTrackGroups;
        }
        Log.d(TAG, "]");
    }

    public void onMetadata(Metadata metadata) {
        Log.d(TAG, "onMetadata [");
        printMetadata(metadata, "  ");
        Log.d(TAG, "]");
    }

    public void onAudioEnabled(DecoderCounters counters) {
        Log.d(TAG, "audioEnabled [" + getSessionTimeString() + "]");
    }

    public void onAudioSessionId(int audioSessionId) {
        Log.d(TAG, "audioSessionId [" + audioSessionId + "]");
    }

    public void onAudioDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        Log.d(TAG, "audioDecoderInitialized [" + getSessionTimeString() + ", " + decoderName + "]");
    }

    public void onAudioInputFormatChanged(Format format) {
        Log.d(TAG, "audioFormatChanged [" + getSessionTimeString() + ", " + Format.toLogString(format) + "]");
    }

    public void onAudioDisabled(DecoderCounters counters) {
        Log.d(TAG, "audioDisabled [" + getSessionTimeString() + "]");
    }

    public void onVideoEnabled(DecoderCounters counters) {
        Log.d(TAG, "videoEnabled [" + getSessionTimeString() + "]");
    }

    public void onVideoDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
        Log.d(TAG, "videoDecoderInitialized [" + getSessionTimeString() + ", " + decoderName + "]");
    }

    public void onVideoInputFormatChanged(Format format) {
        Log.d(TAG, "videoFormatChanged [" + getSessionTimeString() + ", " + Format.toLogString(format) + "]");
    }

    public void onVideoDisabled(DecoderCounters counters) {
        Log.d(TAG, "videoDisabled [" + getSessionTimeString() + "]");
    }

    public void onDroppedFrames(int count, long elapsed) {
        Log.d(TAG, "droppedFrames [" + getSessionTimeString() + ", " + count + "]");
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    }

    public void onRenderedFirstFrame(Surface surface) {
    }

    public void onDrmSessionManagerError(Exception e) {
        printInternalError("drmSessionManagerError", e);
    }

    public void onDrmKeysRestored() {
        Log.d(TAG, "drmKeysRestored [" + getSessionTimeString() + "]");
    }

    public void onDrmKeysRemoved() {
        Log.d(TAG, "drmKeysRemoved [" + getSessionTimeString() + "]");
    }

    public void onDrmKeysLoaded() {
        Log.d(TAG, "drmKeysLoaded [" + getSessionTimeString() + "]");
    }

    public void onLoadError(IOException error) {
        printInternalError("loadError", error);
    }

    private void printInternalError(String type, Exception e) {
        Log.e(TAG, "internalError [" + getSessionTimeString() + ", " + type + "]", e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            Metadata.Entry entry = metadata.get(i);
            if (entry instanceof TextInformationFrame) {
                TextInformationFrame textInformationFrame = (TextInformationFrame) entry;
                Log.d(TAG, prefix + String.format("%s: value=%s", new Object[]{textInformationFrame.id, textInformationFrame.value}));
            } else if (entry instanceof UrlLinkFrame) {
                UrlLinkFrame urlLinkFrame = (UrlLinkFrame) entry;
                Log.d(TAG, prefix + String.format("%s: url=%s", new Object[]{urlLinkFrame.id, urlLinkFrame.url}));
            } else if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                Log.d(TAG, prefix + String.format("%s: owner=%s", new Object[]{privFrame.id, privFrame.owner}));
            } else if (entry instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) entry;
                Log.d(TAG, prefix + String.format("%s: mimeType=%s, filename=%s, description=%s", new Object[]{geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description}));
            } else if (entry instanceof ApicFrame) {
                ApicFrame apicFrame = (ApicFrame) entry;
                Log.d(TAG, prefix + String.format("%s: mimeType=%s, description=%s", new Object[]{apicFrame.id, apicFrame.mimeType, apicFrame.description}));
            } else if (entry instanceof CommentFrame) {
                CommentFrame commentFrame = (CommentFrame) entry;
                Log.d(TAG, prefix + String.format("%s: language=%s, description=%s", new Object[]{commentFrame.id, commentFrame.language, commentFrame.description}));
            } else if (entry instanceof Id3Frame) {
                Log.d(TAG, prefix + String.format("%s", new Object[]{((Id3Frame) entry).id}));
            } else if (entry instanceof EventMessage) {
                EventMessage eventMessage = (EventMessage) entry;
                Log.d(TAG, prefix + String.format("EMSG: scheme=%s, id=%d, value=%s", new Object[]{eventMessage.schemeIdUri, Long.valueOf(eventMessage.id), eventMessage.value}));
            }
        }
    }

    private String getSessionTimeString() {
        return getTimeString(SystemClock.elapsedRealtime() - this.startTimeMs);
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }

    private static String getStateString(int state) {
        if (state == 1) {
            return "I";
        }
        if (state == 2) {
            return "B";
        }
        if (state == 3) {
            return "R";
        }
        if (state != 4) {
            return "?";
        }
        return ExifInterface.LONGITUDE_EAST;
    }

    private static String getFormatSupportString(int formatSupport) {
        if (formatSupport == 0) {
            return "NO";
        }
        if (formatSupport == 1) {
            return "NO_UNSUPPORTED_TYPE";
        }
        if (formatSupport == 3) {
            return "NO_EXCEEDS_CAPABILITIES";
        }
        if (formatSupport != 4) {
            return "?";
        }
        return "YES";
    }

    private static String getAdaptiveSupportString(int trackCount, int adaptiveSupport) {
        if (trackCount < 2) {
            return "N/A";
        }
        if (adaptiveSupport == 0) {
            return "NO";
        }
        if (adaptiveSupport == 8) {
            return "YES_NOT_SEAMLESS";
        }
        if (adaptiveSupport != 16) {
            return "?";
        }
        return "YES";
    }

    private static String getTrackStatusString(TrackSelection selection, TrackGroup group, int trackIndex) {
        return getTrackStatusString((selection == null || selection.getTrackGroup() != group || selection.indexOf(trackIndex) == -1) ? false : true);
    }

    private static String getTrackStatusString(boolean enabled) {
        return enabled ? "[X]" : "[ ]";
    }

    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
    }

    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
    }

    public void onLoadStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadCanceled(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadError(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
    }

    public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
    }

    public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onDownstreamFormatChanged(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }
}
