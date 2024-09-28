package com.google.android.exoplayer2.util;

import android.os.SystemClock;
import android.view.Surface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.litesuits.orm.db.assit.SQLBuilder;
import io.reactivex.annotations.SchedulerSupport;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class EventLogger implements AnalyticsListener {
    private static final String DEFAULT_TAG = "EventLogger";
    private static final int MAX_TIMELINE_ITEM_LINES = 3;
    private static final NumberFormat TIME_FORMAT;
    private final Timeline.Period period;
    private final long startTimeMs;
    private final String tag;
    private final MappingTrackSelector trackSelector;
    private final Timeline.Window window;

    public /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        AnalyticsListener.CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    public /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
        AnalyticsListener.CC.$default$onVolumeChanged(this, eventTime, f);
    }

    static {
        NumberFormat instance = NumberFormat.getInstance(Locale.US);
        TIME_FORMAT = instance;
        instance.setMinimumFractionDigits(2);
        TIME_FORMAT.setMaximumFractionDigits(2);
        TIME_FORMAT.setGroupingUsed(false);
    }

    public EventLogger(MappingTrackSelector trackSelector2) {
        this(trackSelector2, DEFAULT_TAG);
    }

    public EventLogger(MappingTrackSelector trackSelector2, String tag2) {
        this.trackSelector = trackSelector2;
        this.tag = tag2;
        this.window = new Timeline.Window();
        this.period = new Timeline.Period();
        this.startTimeMs = SystemClock.elapsedRealtime();
    }

    public void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean isLoading) {
        logd(eventTime, "loading", Boolean.toString(isLoading));
    }

    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int state) {
        logd(eventTime, RemoteConfigConstants.ResponseFieldKey.STATE, playWhenReady + ", " + getStateString(state));
    }

    public void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int repeatMode) {
        logd(eventTime, "repeatMode", getRepeatModeString(repeatMode));
    }

    public void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean shuffleModeEnabled) {
        logd(eventTime, "shuffleModeEnabled", Boolean.toString(shuffleModeEnabled));
    }

    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int reason) {
        logd(eventTime, "positionDiscontinuity", getDiscontinuityReasonString(reason));
    }

    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "seekStarted");
    }

    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        logd(eventTime, "playbackParameters", Util.formatInvariant("speed=%.2f, pitch=%.2f, skipSilence=%s", Float.valueOf(playbackParameters.speed), Float.valueOf(playbackParameters.pitch), Boolean.valueOf(playbackParameters.skipSilence)));
    }

    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int reason) {
        int periodCount = eventTime.timeline.getPeriodCount();
        int windowCount = eventTime.timeline.getWindowCount();
        logd("timelineChanged [" + getEventTimeString(eventTime) + ", periodCount=" + periodCount + ", windowCount=" + windowCount + ", reason=" + getTimelineChangeReasonString(reason));
        for (int i = 0; i < Math.min(periodCount, 3); i++) {
            eventTime.timeline.getPeriod(i, this.period);
            logd("  period [" + getTimeString(this.period.getDurationMs()) + "]");
        }
        if (periodCount > 3) {
            logd("  ...");
        }
        for (int i2 = 0; i2 < Math.min(windowCount, 3); i2++) {
            eventTime.timeline.getWindow(i2, this.window);
            logd("  window [" + getTimeString(this.window.getDurationMs()) + ", " + this.window.isSeekable + ", " + this.window.isDynamic + "]");
        }
        if (windowCount > 3) {
            logd("  ...");
        }
        logd("]");
    }

    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException e) {
        loge(eventTime, "playerFailed", e);
    }

    public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray ignored, TrackSelectionArray trackSelections) {
        String str;
        String str2;
        MappingTrackSelector mappingTrackSelector = this.trackSelector;
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mappingTrackSelector != null ? mappingTrackSelector.getCurrentMappedTrackInfo() : null;
        if (mappedTrackInfo == null) {
            logd(eventTime, "tracksChanged", "[]");
            return;
        }
        AnalyticsListener.EventTime eventTime2 = eventTime;
        StringBuilder sb = new StringBuilder();
        sb.append("tracksChanged [");
        sb.append(getEventTimeString(eventTime));
        String str3 = ", ";
        sb.append(str3);
        logd(sb.toString());
        int rendererCount = mappedTrackInfo.getRendererCount();
        int rendererIndex = 0;
        while (true) {
            str = "  ]";
            str2 = " [";
            if (rendererIndex >= rendererCount) {
                break;
            }
            TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
            TrackSelection trackSelection = trackSelections.get(rendererIndex);
            int rendererCount2 = rendererCount;
            if (rendererTrackGroups.length > 0) {
                logd("  Renderer:" + rendererIndex + str2);
                int groupIndex = 0;
                while (groupIndex < rendererTrackGroups.length) {
                    TrackGroup trackGroup = rendererTrackGroups.get(groupIndex);
                    TrackGroupArray rendererTrackGroups2 = rendererTrackGroups;
                    String str4 = str;
                    String adaptiveSupport = getAdaptiveSupportString(trackGroup.length, mappedTrackInfo.getAdaptiveSupport(rendererIndex, groupIndex, false));
                    logd("    Group:" + groupIndex + ", adaptive_supported=" + adaptiveSupport + str2);
                    int trackIndex = 0;
                    while (trackIndex < trackGroup.length) {
                        String status = getTrackStatusString(trackSelection, trackGroup, trackIndex);
                        String adaptiveSupport2 = adaptiveSupport;
                        String formatSupport = getFormatSupportString(mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex));
                        String str5 = str2;
                        logd("      " + status + " Track:" + trackIndex + str3 + Format.toLogString(trackGroup.getFormat(trackIndex)) + ", supported=" + formatSupport);
                        trackIndex++;
                        str2 = str5;
                        adaptiveSupport = adaptiveSupport2;
                        trackGroup = trackGroup;
                    }
                    String str6 = adaptiveSupport;
                    String str7 = str2;
                    logd("    ]");
                    groupIndex++;
                    TrackSelectionArray trackSelectionArray = trackSelections;
                    rendererTrackGroups = rendererTrackGroups2;
                    str = str4;
                }
                String str8 = str;
                TrackGroupArray trackGroupArray = rendererTrackGroups;
                if (trackSelection != null) {
                    int selectionIndex = 0;
                    while (true) {
                        if (selectionIndex >= trackSelection.length()) {
                            break;
                        }
                        Metadata metadata = trackSelection.getFormat(selectionIndex).metadata;
                        if (metadata != null) {
                            logd("    Metadata [");
                            printMetadata(metadata, "      ");
                            logd("    ]");
                            break;
                        }
                        selectionIndex++;
                    }
                }
                logd(str8);
            }
            rendererIndex++;
            AnalyticsListener.EventTime eventTime3 = eventTime;
            rendererCount = rendererCount2;
        }
        String str9 = str;
        String str10 = str2;
        TrackGroupArray unassociatedTrackGroups = mappedTrackInfo.getUnmappedTrackGroups();
        if (unassociatedTrackGroups.length > 0) {
            logd("  Renderer:None [");
            int groupIndex2 = 0;
            while (groupIndex2 < unassociatedTrackGroups.length) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("    Group:");
                sb2.append(groupIndex2);
                String str11 = str10;
                sb2.append(str11);
                logd(sb2.toString());
                TrackGroup trackGroup2 = unassociatedTrackGroups.get(groupIndex2);
                int trackIndex2 = 0;
                while (trackIndex2 < trackGroup2.length) {
                    String status2 = getTrackStatusString(false);
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo2 = mappedTrackInfo;
                    String formatSupport2 = getFormatSupportString(0);
                    logd("      " + status2 + " Track:" + trackIndex2 + str3 + Format.toLogString(trackGroup2.getFormat(trackIndex2)) + ", supported=" + formatSupport2);
                    trackIndex2++;
                    mappedTrackInfo = mappedTrackInfo2;
                    str3 = str3;
                }
                String str12 = str3;
                logd("    ]");
                groupIndex2++;
                str10 = str11;
            }
            logd(str9);
        }
        logd("]");
    }

    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "seekProcessed");
    }

    public void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        logd("metadata [" + getEventTimeString(eventTime) + ", ");
        printMetadata(metadata, "  ");
        logd("]");
    }

    public void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderEnabled", getTrackTypeString(trackType));
    }

    public void onAudioSessionId(AnalyticsListener.EventTime eventTime, int audioSessionId) {
        logd(eventTime, "audioSessionId", Integer.toString(audioSessionId));
    }

    public void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int trackType, String decoderName, long initializationDurationMs) {
        logd(eventTime, "decoderInitialized", getTrackTypeString(trackType) + ", " + decoderName);
    }

    public void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int trackType, Format format) {
        logd(eventTime, "decoderInputFormatChanged", getTrackTypeString(trackType) + ", " + Format.toLogString(format));
    }

    public void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int trackType, DecoderCounters counters) {
        logd(eventTime, "decoderDisabled", getTrackTypeString(trackType));
    }

    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        loge(eventTime, "audioTrackUnderrun", bufferSize + ", " + bufferSizeMs + ", " + elapsedSinceLastFeedMs + "]", (Throwable) null);
    }

    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int count, long elapsedMs) {
        logd(eventTime, "droppedFrames", Integer.toString(count));
    }

    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        logd(eventTime, "videoSizeChanged", width + ", " + height);
    }

    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface) {
        logd(eventTime, "renderedFirstFrame", String.valueOf(surface));
    }

    public void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodCreated");
    }

    public void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodReleased");
    }

    public void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
        printInternalError(eventTime, "loadError", error);
    }

    public void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onReadingStarted(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "mediaPeriodReadingStarted");
    }

    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
    }

    public void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int width, int height) {
        logd(eventTime, "surfaceSizeChanged", width + ", " + height);
    }

    public void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        logd(eventTime, "upstreamDiscarded", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        logd(eventTime, "downstreamFormatChanged", Format.toLogString(mediaLoadData.trackFormat));
    }

    public void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmSessionAcquired");
    }

    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception e) {
        printInternalError(eventTime, "drmSessionManagerError", e);
    }

    public void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysRestored");
    }

    public void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysRemoved");
    }

    public void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmKeysLoaded");
    }

    public void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        logd(eventTime, "drmSessionReleased");
    }

    /* access modifiers changed from: protected */
    public void logd(String msg) {
        Log.d(this.tag, msg);
    }

    /* access modifiers changed from: protected */
    public void loge(String msg, Throwable tr) {
        Log.e(this.tag, msg, tr);
    }

    private void logd(AnalyticsListener.EventTime eventTime, String eventName) {
        logd(getEventString(eventTime, eventName));
    }

    private void logd(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription) {
        logd(getEventString(eventTime, eventName, eventDescription));
    }

    private void loge(AnalyticsListener.EventTime eventTime, String eventName, Throwable throwable) {
        loge(getEventString(eventTime, eventName), throwable);
    }

    private void loge(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription, Throwable throwable) {
        loge(getEventString(eventTime, eventName, eventDescription), throwable);
    }

    private void printInternalError(AnalyticsListener.EventTime eventTime, String type, Exception e) {
        loge(eventTime, "internalError", type, e);
    }

    private void printMetadata(Metadata metadata, String prefix) {
        for (int i = 0; i < metadata.length(); i++) {
            logd(prefix + metadata.get(i));
        }
    }

    private String getEventString(AnalyticsListener.EventTime eventTime, String eventName) {
        return eventName + " [" + getEventTimeString(eventTime) + "]";
    }

    private String getEventString(AnalyticsListener.EventTime eventTime, String eventName, String eventDescription) {
        return eventName + " [" + getEventTimeString(eventTime) + ", " + eventDescription + "]";
    }

    private String getEventTimeString(AnalyticsListener.EventTime eventTime) {
        String windowPeriodString = "window=" + eventTime.windowIndex;
        if (eventTime.mediaPeriodId != null) {
            windowPeriodString = windowPeriodString + ", period=" + eventTime.timeline.getIndexOfPeriod(eventTime.mediaPeriodId.periodUid);
            if (eventTime.mediaPeriodId.isAd()) {
                windowPeriodString = (windowPeriodString + ", adGroup=" + eventTime.mediaPeriodId.adGroupIndex) + ", ad=" + eventTime.mediaPeriodId.adIndexInAdGroup;
            }
        }
        return getTimeString(eventTime.realtimeMs - this.startTimeMs) + ", " + getTimeString(eventTime.currentPlaybackPositionMs) + ", " + windowPeriodString;
    }

    private static String getTimeString(long timeMs) {
        return timeMs == C.TIME_UNSET ? "?" : TIME_FORMAT.format((double) (((float) timeMs) / 1000.0f));
    }

    private static String getStateString(int state) {
        if (state == 1) {
            return "IDLE";
        }
        if (state == 2) {
            return "BUFFERING";
        }
        if (state == 3) {
            return "READY";
        }
        if (state != 4) {
            return "?";
        }
        return "ENDED";
    }

    private static String getFormatSupportString(int formatSupport) {
        if (formatSupport == 0) {
            return "NO";
        }
        if (formatSupport == 1) {
            return "NO_UNSUPPORTED_TYPE";
        }
        if (formatSupport == 2) {
            return "NO_UNSUPPORTED_DRM";
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

    private static String getRepeatModeString(int repeatMode) {
        if (repeatMode == 0) {
            return "OFF";
        }
        if (repeatMode == 1) {
            return "ONE";
        }
        if (repeatMode != 2) {
            return "?";
        }
        return "ALL";
    }

    private static String getDiscontinuityReasonString(int reason) {
        if (reason == 0) {
            return "PERIOD_TRANSITION";
        }
        if (reason == 1) {
            return "SEEK";
        }
        if (reason == 2) {
            return "SEEK_ADJUSTMENT";
        }
        if (reason == 3) {
            return "AD_INSERTION";
        }
        if (reason != 4) {
            return "?";
        }
        return "INTERNAL";
    }

    private static String getTimelineChangeReasonString(int reason) {
        if (reason == 0) {
            return "PREPARED";
        }
        if (reason == 1) {
            return "RESET";
        }
        if (reason != 2) {
            return "?";
        }
        return "DYNAMIC";
    }

    private static String getTrackTypeString(int trackType) {
        switch (trackType) {
            case 0:
                return "default";
            case 1:
                return MimeTypes.BASE_TYPE_AUDIO;
            case 2:
                return MimeTypes.BASE_TYPE_VIDEO;
            case 3:
                return "text";
            case 4:
                return TtmlNode.TAG_METADATA;
            case 5:
                return "camera motion";
            case 6:
                return SchedulerSupport.NONE;
            default:
                if (trackType < 10000) {
                    return "?";
                }
                return "custom (" + trackType + SQLBuilder.PARENTHESES_RIGHT;
        }
    }
}
