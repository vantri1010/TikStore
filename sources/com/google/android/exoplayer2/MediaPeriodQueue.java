package com.google.android.exoplayer2;

import android.util.Pair;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;

final class MediaPeriodQueue {
    private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
    private int length;
    private MediaPeriodHolder loading;
    private long nextWindowSequenceNumber;
    private Object oldFrontPeriodUid;
    private long oldFrontPeriodWindowSequenceNumber;
    private final Timeline.Period period = new Timeline.Period();
    private MediaPeriodHolder playing;
    private MediaPeriodHolder reading;
    private int repeatMode;
    private boolean shuffleModeEnabled;
    private Timeline timeline = Timeline.EMPTY;
    private final Timeline.Window window = new Timeline.Window();

    public void setTimeline(Timeline timeline2) {
        this.timeline = timeline2;
    }

    public boolean updateRepeatMode(int repeatMode2) {
        this.repeatMode = repeatMode2;
        return updateForPlaybackModeChange();
    }

    public boolean updateShuffleModeEnabled(boolean shuffleModeEnabled2) {
        this.shuffleModeEnabled = shuffleModeEnabled2;
        return updateForPlaybackModeChange();
    }

    public boolean isLoading(MediaPeriod mediaPeriod) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        return mediaPeriodHolder != null && mediaPeriodHolder.mediaPeriod == mediaPeriod;
    }

    public void reevaluateBuffer(long rendererPositionUs) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder != null) {
            mediaPeriodHolder.reevaluateBuffer(rendererPositionUs);
        }
    }

    public boolean shouldLoadNextMediaPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        return mediaPeriodHolder == null || (!mediaPeriodHolder.info.isFinal && this.loading.isFullyBuffered() && this.loading.info.durationUs != C.TIME_UNSET && this.length < 100);
    }

    public MediaPeriodInfo getNextMediaPeriodInfo(long rendererPositionUs, PlaybackInfo playbackInfo) {
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder == null) {
            return getFirstMediaPeriodInfo(playbackInfo);
        }
        return getFollowingMediaPeriodInfo(mediaPeriodHolder, rendererPositionUs);
    }

    public MediaPeriod enqueueNextMediaPeriod(RendererCapabilities[] rendererCapabilities, TrackSelector trackSelector, Allocator allocator, MediaSource mediaSource, MediaPeriodInfo info) {
        long rendererPositionOffsetUs;
        MediaPeriodHolder mediaPeriodHolder = this.loading;
        if (mediaPeriodHolder == null) {
            rendererPositionOffsetUs = info.startPositionUs;
        } else {
            rendererPositionOffsetUs = mediaPeriodHolder.getRendererOffset() + this.loading.info.durationUs;
        }
        MediaPeriodHolder mediaPeriodHolder2 = new MediaPeriodHolder(rendererCapabilities, rendererPositionOffsetUs, trackSelector, allocator, mediaSource, info);
        if (this.loading != null) {
            Assertions.checkState(hasPlayingPeriod());
            this.loading.setNext(mediaPeriodHolder2);
        }
        this.oldFrontPeriodUid = null;
        this.loading = mediaPeriodHolder2;
        this.length++;
        return mediaPeriodHolder2.mediaPeriod;
    }

    public MediaPeriodHolder getLoadingPeriod() {
        return this.loading;
    }

    public MediaPeriodHolder getPlayingPeriod() {
        return this.playing;
    }

    public MediaPeriodHolder getReadingPeriod() {
        return this.reading;
    }

    public MediaPeriodHolder getFrontPeriod() {
        return hasPlayingPeriod() ? this.playing : this.loading;
    }

    public boolean hasPlayingPeriod() {
        return this.playing != null;
    }

    public MediaPeriodHolder advanceReadingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.reading;
        Assertions.checkState((mediaPeriodHolder == null || mediaPeriodHolder.getNext() == null) ? false : true);
        MediaPeriodHolder next = this.reading.getNext();
        this.reading = next;
        return next;
    }

    public MediaPeriodHolder advancePlayingPeriod() {
        MediaPeriodHolder mediaPeriodHolder = this.playing;
        if (mediaPeriodHolder != null) {
            if (mediaPeriodHolder == this.reading) {
                this.reading = mediaPeriodHolder.getNext();
            }
            this.playing.release();
            int i = this.length - 1;
            this.length = i;
            if (i == 0) {
                this.loading = null;
                this.oldFrontPeriodUid = this.playing.uid;
                this.oldFrontPeriodWindowSequenceNumber = this.playing.info.id.windowSequenceNumber;
            }
            this.playing = this.playing.getNext();
        } else {
            MediaPeriodHolder mediaPeriodHolder2 = this.loading;
            this.playing = mediaPeriodHolder2;
            this.reading = mediaPeriodHolder2;
        }
        return this.playing;
    }

    public boolean removeAfter(MediaPeriodHolder mediaPeriodHolder) {
        Assertions.checkState(mediaPeriodHolder != null);
        boolean removedReading = false;
        this.loading = mediaPeriodHolder;
        while (mediaPeriodHolder.getNext() != null) {
            mediaPeriodHolder = mediaPeriodHolder.getNext();
            if (mediaPeriodHolder == this.reading) {
                this.reading = this.playing;
                removedReading = true;
            }
            mediaPeriodHolder.release();
            this.length--;
        }
        this.loading.setNext((MediaPeriodHolder) null);
        return removedReading;
    }

    public void clear(boolean keepFrontPeriodUid) {
        MediaPeriodHolder front = getFrontPeriod();
        if (front != null) {
            this.oldFrontPeriodUid = keepFrontPeriodUid ? front.uid : null;
            this.oldFrontPeriodWindowSequenceNumber = front.info.id.windowSequenceNumber;
            front.release();
            removeAfter(front);
        } else if (!keepFrontPeriodUid) {
            this.oldFrontPeriodUid = null;
        }
        this.playing = null;
        this.loading = null;
        this.reading = null;
        this.length = 0;
    }

    public boolean updateQueuedPeriods(MediaSource.MediaPeriodId playingPeriodId, long rendererPositionUs) {
        int periodIndex = this.timeline.getIndexOfPeriod(playingPeriodId.periodUid);
        MediaPeriodHolder previousPeriodHolder = null;
        for (MediaPeriodHolder periodHolder = getFrontPeriod(); periodHolder != null; periodHolder = periodHolder.getNext()) {
            if (previousPeriodHolder == null) {
                long previousDurationUs = periodHolder.info.durationUs;
                periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info);
                if (!canKeepAfterMediaPeriodHolder(periodHolder, previousDurationUs)) {
                    return true ^ removeAfter(periodHolder);
                }
            } else if (periodIndex == -1 || !periodHolder.uid.equals(this.timeline.getUidOfPeriod(periodIndex))) {
                return true ^ removeAfter(previousPeriodHolder);
            } else {
                MediaPeriodInfo periodInfo = getFollowingMediaPeriodInfo(previousPeriodHolder, rendererPositionUs);
                if (periodInfo == null) {
                    return true ^ removeAfter(previousPeriodHolder);
                }
                periodHolder.info = getUpdatedMediaPeriodInfo(periodHolder.info);
                if (!canKeepMediaPeriodHolder(periodHolder, periodInfo)) {
                    return true ^ removeAfter(previousPeriodHolder);
                }
                if (!canKeepAfterMediaPeriodHolder(periodHolder, periodInfo.durationUs)) {
                    return true ^ removeAfter(periodHolder);
                }
            }
            if (periodHolder.info.isLastInTimelinePeriod) {
                periodIndex = this.timeline.getNextPeriodIndex(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            }
            previousPeriodHolder = periodHolder;
        }
        return true;
    }

    public MediaPeriodInfo getUpdatedMediaPeriodInfo(MediaPeriodInfo info) {
        long durationUs;
        MediaSource.MediaPeriodId id = info.id;
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean isLastInTimeline = isLastInTimeline(id, isLastInPeriod);
        this.timeline.getPeriodByUid(info.id.periodUid, this.period);
        if (id.isAd()) {
            durationUs = this.period.getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup);
        } else {
            durationUs = (id.endPositionUs == C.TIME_UNSET || id.endPositionUs == Long.MIN_VALUE) ? this.period.getDurationUs() : id.endPositionUs;
        }
        return new MediaPeriodInfo(id, info.startPositionUs, info.contentPositionUs, durationUs, isLastInPeriod, isLastInTimeline);
    }

    public MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs) {
        return resolveMediaPeriodIdForAds(periodUid, positionUs, resolvePeriodIndexToWindowSequenceNumber(periodUid));
    }

    private MediaSource.MediaPeriodId resolveMediaPeriodIdForAds(Object periodUid, long positionUs, long windowSequenceNumber) {
        long endPositionUs;
        long j = positionUs;
        Object obj = periodUid;
        this.timeline.getPeriodByUid(periodUid, this.period);
        int adGroupIndex = this.period.getAdGroupIndexForPositionUs(j);
        if (adGroupIndex != -1) {
            return new MediaSource.MediaPeriodId(periodUid, adGroupIndex, this.period.getFirstAdIndexToPlay(adGroupIndex), windowSequenceNumber);
        }
        int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(j);
        if (nextAdGroupIndex == -1) {
            endPositionUs = -9223372036854775807L;
        } else {
            endPositionUs = this.period.getAdGroupTimeUs(nextAdGroupIndex);
        }
        return new MediaSource.MediaPeriodId(periodUid, windowSequenceNumber, endPositionUs);
    }

    private long resolvePeriodIndexToWindowSequenceNumber(Object periodUid) {
        int oldFrontPeriodIndex;
        int windowIndex = this.timeline.getPeriodByUid(periodUid, this.period).windowIndex;
        Object obj = this.oldFrontPeriodUid;
        if (obj != null && (oldFrontPeriodIndex = this.timeline.getIndexOfPeriod(obj)) != -1 && this.timeline.getPeriod(oldFrontPeriodIndex, this.period).windowIndex == windowIndex) {
            return this.oldFrontPeriodWindowSequenceNumber;
        }
        for (MediaPeriodHolder mediaPeriodHolder = getFrontPeriod(); mediaPeriodHolder != null; mediaPeriodHolder = mediaPeriodHolder.getNext()) {
            if (mediaPeriodHolder.uid.equals(periodUid)) {
                return mediaPeriodHolder.info.id.windowSequenceNumber;
            }
        }
        for (MediaPeriodHolder mediaPeriodHolder2 = getFrontPeriod(); mediaPeriodHolder2 != null; mediaPeriodHolder2 = mediaPeriodHolder2.getNext()) {
            int indexOfHolderInTimeline = this.timeline.getIndexOfPeriod(mediaPeriodHolder2.uid);
            if (indexOfHolderInTimeline != -1 && this.timeline.getPeriod(indexOfHolderInTimeline, this.period).windowIndex == windowIndex) {
                return mediaPeriodHolder2.info.id.windowSequenceNumber;
            }
        }
        long j = this.nextWindowSequenceNumber;
        this.nextWindowSequenceNumber = 1 + j;
        return j;
    }

    private boolean canKeepMediaPeriodHolder(MediaPeriodHolder periodHolder, MediaPeriodInfo info) {
        MediaPeriodInfo periodHolderInfo = periodHolder.info;
        return periodHolderInfo.startPositionUs == info.startPositionUs && periodHolderInfo.id.equals(info.id);
    }

    private boolean canKeepAfterMediaPeriodHolder(MediaPeriodHolder periodHolder, long previousDurationUs) {
        return previousDurationUs == C.TIME_UNSET || previousDurationUs == periodHolder.info.durationUs;
    }

    private boolean updateForPlaybackModeChange() {
        MediaPeriodHolder lastValidPeriodHolder = getFrontPeriod();
        if (lastValidPeriodHolder == null) {
            return true;
        }
        int nextPeriodIndex = this.timeline.getIndexOfPeriod(lastValidPeriodHolder.uid);
        while (true) {
            nextPeriodIndex = this.timeline.getNextPeriodIndex(nextPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            while (lastValidPeriodHolder.getNext() != null && !lastValidPeriodHolder.info.isLastInTimelinePeriod) {
                lastValidPeriodHolder = lastValidPeriodHolder.getNext();
            }
            MediaPeriodHolder nextMediaPeriodHolder = lastValidPeriodHolder.getNext();
            if (nextPeriodIndex == -1 || nextMediaPeriodHolder == null || this.timeline.getIndexOfPeriod(nextMediaPeriodHolder.uid) != nextPeriodIndex) {
                boolean readingPeriodRemoved = removeAfter(lastValidPeriodHolder);
                lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
            } else {
                lastValidPeriodHolder = nextMediaPeriodHolder;
            }
        }
        boolean readingPeriodRemoved2 = removeAfter(lastValidPeriodHolder);
        lastValidPeriodHolder.info = getUpdatedMediaPeriodInfo(lastValidPeriodHolder.info);
        if (!readingPeriodRemoved2 || !hasPlayingPeriod()) {
            return true;
        }
        return false;
    }

    private MediaPeriodInfo getFirstMediaPeriodInfo(PlaybackInfo playbackInfo) {
        return getMediaPeriodInfo(playbackInfo.periodId, playbackInfo.contentPositionUs, playbackInfo.startPositionUs);
    }

    private MediaPeriodInfo getFollowingMediaPeriodInfo(MediaPeriodHolder mediaPeriodHolder, long rendererPositionUs) {
        long startPositionUs;
        long windowSequenceNumber;
        Object nextPeriodUid;
        long startPositionUs2;
        long windowSequenceNumber2;
        MediaPeriodInfo mediaPeriodInfo = mediaPeriodHolder.info;
        long bufferedDurationUs = (mediaPeriodHolder.getRendererOffset() + mediaPeriodInfo.durationUs) - rendererPositionUs;
        if (mediaPeriodInfo.isLastInTimelinePeriod) {
            int currentPeriodIndex = this.timeline.getIndexOfPeriod(mediaPeriodInfo.id.periodUid);
            int nextPeriodIndex = this.timeline.getNextPeriodIndex(currentPeriodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (nextPeriodIndex == -1) {
                return null;
            }
            int nextWindowIndex = this.timeline.getPeriod(nextPeriodIndex, this.period, true).windowIndex;
            Object nextPeriodUid2 = this.period.uid;
            long windowSequenceNumber3 = mediaPeriodInfo.id.windowSequenceNumber;
            if (this.timeline.getWindow(nextWindowIndex, this.window).firstPeriodIndex == nextPeriodIndex) {
                Timeline timeline2 = this.timeline;
                Timeline.Window window2 = this.window;
                long windowSequenceNumber4 = windowSequenceNumber3;
                Pair<Object, Long> defaultPosition = timeline2.getPeriodPosition(window2, this.period, nextWindowIndex, C.TIME_UNSET, Math.max(0, bufferedDurationUs));
                if (defaultPosition == null) {
                    return null;
                }
                Object nextPeriodUid3 = defaultPosition.first;
                startPositionUs2 = ((Long) defaultPosition.second).longValue();
                MediaPeriodHolder nextMediaPeriodHolder = mediaPeriodHolder.getNext();
                if (nextMediaPeriodHolder == null || !nextMediaPeriodHolder.uid.equals(nextPeriodUid3)) {
                    nextPeriodUid = nextPeriodUid3;
                    Pair<Object, Long> pair = defaultPosition;
                    windowSequenceNumber2 = this.nextWindowSequenceNumber;
                    long j = windowSequenceNumber4;
                    this.nextWindowSequenceNumber = windowSequenceNumber2 + 1;
                } else {
                    nextPeriodUid = nextPeriodUid3;
                    windowSequenceNumber2 = nextMediaPeriodHolder.info.id.windowSequenceNumber;
                }
                windowSequenceNumber = windowSequenceNumber2;
            } else {
                windowSequenceNumber = windowSequenceNumber3;
                startPositionUs2 = 0;
                nextPeriodUid = nextPeriodUid2;
            }
            long j2 = startPositionUs2;
            int i = nextWindowIndex;
            return getMediaPeriodInfo(resolveMediaPeriodIdForAds(nextPeriodUid, j2, windowSequenceNumber), j2, startPositionUs2);
        }
        MediaSource.MediaPeriodId currentPeriodId = mediaPeriodInfo.id;
        this.timeline.getPeriodByUid(currentPeriodId.periodUid, this.period);
        if (currentPeriodId.isAd()) {
            int adGroupIndex = currentPeriodId.adGroupIndex;
            int adCountInCurrentAdGroup = this.period.getAdCountInAdGroup(adGroupIndex);
            if (adCountInCurrentAdGroup == -1) {
                return null;
            }
            int nextAdIndexInAdGroup = this.period.getNextAdIndexToPlay(adGroupIndex, currentPeriodId.adIndexInAdGroup);
            if (nextAdIndexInAdGroup >= adCountInCurrentAdGroup) {
                long startPositionUs3 = mediaPeriodInfo.contentPositionUs;
                if (this.period.getAdGroupCount() == 1 && this.period.getAdGroupTimeUs(0) == 0) {
                    Timeline timeline3 = this.timeline;
                    Timeline.Window window3 = this.window;
                    Timeline.Period period2 = this.period;
                    Pair<Object, Long> defaultPosition2 = timeline3.getPeriodPosition(window3, period2, period2.windowIndex, C.TIME_UNSET, Math.max(0, bufferedDurationUs));
                    if (defaultPosition2 == null) {
                        return null;
                    }
                    startPositionUs = ((Long) defaultPosition2.second).longValue();
                } else {
                    startPositionUs = startPositionUs3;
                }
                return getMediaPeriodInfoForContent(currentPeriodId.periodUid, startPositionUs, currentPeriodId.windowSequenceNumber);
            } else if (!this.period.isAdAvailable(adGroupIndex, nextAdIndexInAdGroup)) {
                int i2 = nextAdIndexInAdGroup;
                return null;
            } else {
                int i3 = nextAdIndexInAdGroup;
                return getMediaPeriodInfoForAd(currentPeriodId.periodUid, adGroupIndex, nextAdIndexInAdGroup, mediaPeriodInfo.contentPositionUs, currentPeriodId.windowSequenceNumber);
            }
        } else {
            int nextAdGroupIndex = this.period.getAdGroupIndexForPositionUs(mediaPeriodInfo.id.endPositionUs);
            if (nextAdGroupIndex == -1) {
                return getMediaPeriodInfoForContent(currentPeriodId.periodUid, mediaPeriodInfo.durationUs, currentPeriodId.windowSequenceNumber);
            }
            int adIndexInAdGroup = this.period.getFirstAdIndexToPlay(nextAdGroupIndex);
            if (!this.period.isAdAvailable(nextAdGroupIndex, adIndexInAdGroup)) {
                return null;
            }
            return getMediaPeriodInfoForAd(currentPeriodId.periodUid, nextAdGroupIndex, adIndexInAdGroup, mediaPeriodInfo.durationUs, currentPeriodId.windowSequenceNumber);
        }
    }

    private MediaPeriodInfo getMediaPeriodInfo(MediaSource.MediaPeriodId id, long contentPositionUs, long startPositionUs) {
        this.timeline.getPeriodByUid(id.periodUid, this.period);
        if (!id.isAd()) {
            return getMediaPeriodInfoForContent(id.periodUid, startPositionUs, id.windowSequenceNumber);
        } else if (!this.period.isAdAvailable(id.adGroupIndex, id.adIndexInAdGroup)) {
            return null;
        } else {
            return getMediaPeriodInfoForAd(id.periodUid, id.adGroupIndex, id.adIndexInAdGroup, contentPositionUs, id.windowSequenceNumber);
        }
    }

    private MediaPeriodInfo getMediaPeriodInfoForAd(Object periodUid, int adGroupIndex, int adIndexInAdGroup, long contentPositionUs, long windowSequenceNumber) {
        MediaSource.MediaPeriodId id = new MediaSource.MediaPeriodId(periodUid, adGroupIndex, adIndexInAdGroup, windowSequenceNumber);
        return new MediaPeriodInfo(id, adIndexInAdGroup == this.period.getFirstAdIndexToPlay(adGroupIndex) ? this.period.getAdResumePositionUs() : 0, contentPositionUs, this.timeline.getPeriodByUid(id.periodUid, this.period).getAdDurationUs(id.adGroupIndex, id.adIndexInAdGroup), false, false);
    }

    private MediaPeriodInfo getMediaPeriodInfoForContent(Object periodUid, long startPositionUs, long windowSequenceNumber) {
        int nextAdGroupIndex = this.period.getAdGroupIndexAfterPositionUs(startPositionUs);
        long endPositionUs = nextAdGroupIndex != -1 ? this.period.getAdGroupTimeUs(nextAdGroupIndex) : -9223372036854775807L;
        MediaSource.MediaPeriodId id = new MediaSource.MediaPeriodId(periodUid, windowSequenceNumber, endPositionUs);
        boolean isLastInPeriod = isLastInPeriod(id);
        boolean z = isLastInPeriod;
        MediaSource.MediaPeriodId mediaPeriodId = id;
        return new MediaPeriodInfo(id, startPositionUs, C.TIME_UNSET, (endPositionUs == C.TIME_UNSET || endPositionUs == Long.MIN_VALUE) ? this.period.durationUs : endPositionUs, isLastInPeriod, isLastInTimeline(id, isLastInPeriod));
    }

    private boolean isLastInPeriod(MediaSource.MediaPeriodId id) {
        return !id.isAd() && id.endPositionUs == C.TIME_UNSET;
    }

    private boolean isLastInTimeline(MediaSource.MediaPeriodId id, boolean isLastMediaPeriodInPeriod) {
        int periodIndex = this.timeline.getIndexOfPeriod(id.periodUid);
        if (!this.timeline.getWindow(this.timeline.getPeriod(periodIndex, this.period).windowIndex, this.window).isDynamic) {
            return this.timeline.isLastPeriod(periodIndex, this.period, this.window, this.repeatMode, this.shuffleModeEnabled) && isLastMediaPeriodInPeriod;
        }
    }
}
