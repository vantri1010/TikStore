package com.google.android.exoplayer2.source;

import android.os.Handler;
import android.os.Message;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ConcatenatingMediaSource extends CompositeMediaSource<MediaSourceHolder> {
    private static final int MSG_ADD = 0;
    private static final int MSG_MOVE = 2;
    private static final int MSG_NOTIFY_LISTENER = 4;
    private static final int MSG_ON_COMPLETION = 5;
    private static final int MSG_REMOVE = 1;
    private static final int MSG_SET_SHUFFLE_ORDER = 3;
    private final boolean isAtomic;
    private boolean listenerNotificationScheduled;
    private final Map<MediaPeriod, MediaSourceHolder> mediaSourceByMediaPeriod;
    private final Map<Object, MediaSourceHolder> mediaSourceByUid;
    private final List<MediaSourceHolder> mediaSourceHolders;
    private final List<MediaSourceHolder> mediaSourcesPublic;
    private EventDispatcher<Runnable> pendingOnCompletionActions;
    private final Timeline.Period period;
    private int periodCount;
    private Handler playbackThreadHandler;
    private ShuffleOrder shuffleOrder;
    private final boolean useLazyPreparation;
    private final Timeline.Window window;
    private int windowCount;

    public ConcatenatingMediaSource(MediaSource... mediaSources) {
        this(false, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic2, MediaSource... mediaSources) {
        this(isAtomic2, new ShuffleOrder.DefaultShuffleOrder(0), mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic2, ShuffleOrder shuffleOrder2, MediaSource... mediaSources) {
        this(isAtomic2, false, shuffleOrder2, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic2, boolean useLazyPreparation2, ShuffleOrder shuffleOrder2, MediaSource... mediaSources) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        this.shuffleOrder = shuffleOrder2.getLength() > 0 ? shuffleOrder2.cloneAndClear() : shuffleOrder2;
        this.mediaSourceByMediaPeriod = new IdentityHashMap();
        this.mediaSourceByUid = new HashMap();
        this.mediaSourcesPublic = new ArrayList();
        this.mediaSourceHolders = new ArrayList();
        this.pendingOnCompletionActions = new EventDispatcher<>();
        this.isAtomic = isAtomic2;
        this.useLazyPreparation = useLazyPreparation2;
        this.window = new Timeline.Window();
        this.period = new Timeline.Period();
        addMediaSources(Arrays.asList(mediaSources));
    }

    public final synchronized void addMediaSource(MediaSource mediaSource) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource);
    }

    public final synchronized void addMediaSource(MediaSource mediaSource, Handler handler, Runnable actionOnCompletion) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, handler, actionOnCompletion);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource) {
        addPublicMediaSources(index, Collections.singletonList(mediaSource), (Handler) null, (Runnable) null);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource, Handler handler, Runnable actionOnCompletion) {
        addPublicMediaSources(index, Collections.singletonList(mediaSource), handler, actionOnCompletion);
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources) {
        addPublicMediaSources(this.mediaSourcesPublic.size(), mediaSources, (Handler) null, (Runnable) null);
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources, Handler handler, Runnable actionOnCompletion) {
        addPublicMediaSources(this.mediaSourcesPublic.size(), mediaSources, handler, actionOnCompletion);
    }

    public final synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources) {
        addPublicMediaSources(index, mediaSources, (Handler) null, (Runnable) null);
    }

    public final synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources, Handler handler, Runnable actionOnCompletion) {
        addPublicMediaSources(index, mediaSources, handler, actionOnCompletion);
    }

    public final synchronized void removeMediaSource(int index) {
        removePublicMediaSources(index, index + 1, (Handler) null, (Runnable) null);
    }

    public final synchronized void removeMediaSource(int index, Handler handler, Runnable actionOnCompletion) {
        removePublicMediaSources(index, index + 1, handler, actionOnCompletion);
    }

    public final synchronized void removeMediaSourceRange(int fromIndex, int toIndex) {
        removePublicMediaSources(fromIndex, toIndex, (Handler) null, (Runnable) null);
    }

    public final synchronized void removeMediaSourceRange(int fromIndex, int toIndex, Handler handler, Runnable actionOnCompletion) {
        removePublicMediaSources(fromIndex, toIndex, handler, actionOnCompletion);
    }

    public final synchronized void moveMediaSource(int currentIndex, int newIndex) {
        movePublicMediaSource(currentIndex, newIndex, (Handler) null, (Runnable) null);
    }

    public final synchronized void moveMediaSource(int currentIndex, int newIndex, Handler handler, Runnable actionOnCompletion) {
        movePublicMediaSource(currentIndex, newIndex, handler, actionOnCompletion);
    }

    public final synchronized void clear() {
        removeMediaSourceRange(0, getSize());
    }

    public final synchronized void clear(Handler handler, Runnable actionOnCompletion) {
        removeMediaSourceRange(0, getSize(), handler, actionOnCompletion);
    }

    public final synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public final synchronized MediaSource getMediaSource(int index) {
        return this.mediaSourcesPublic.get(index).mediaSource;
    }

    public final synchronized void setShuffleOrder(ShuffleOrder shuffleOrder2) {
        setPublicShuffleOrder(shuffleOrder2, (Handler) null, (Runnable) null);
    }

    public final synchronized void setShuffleOrder(ShuffleOrder shuffleOrder2, Handler handler, Runnable actionOnCompletion) {
        setPublicShuffleOrder(shuffleOrder2, handler, actionOnCompletion);
    }

    public Object getTag() {
        return null;
    }

    public final synchronized void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        this.playbackThreadHandler = new Handler(new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return ConcatenatingMediaSource.this.handleMessage(message);
            }
        });
        if (this.mediaSourcesPublic.isEmpty()) {
            notifyListener();
        } else {
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
            addMediaSourcesInternal(0, this.mediaSourcesPublic);
            scheduleListenerNotification();
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public final MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        MediaSourceHolder holder = this.mediaSourceByUid.get(getMediaSourceHolderUid(id.periodUid));
        if (holder == null) {
            holder = new MediaSourceHolder(new DummyMediaSource());
            holder.hasStartedPreparing = true;
        }
        DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(holder.mediaSource, id, allocator, startPositionUs);
        this.mediaSourceByMediaPeriod.put(mediaPeriod, holder);
        holder.activeMediaPeriods.add(mediaPeriod);
        if (!holder.hasStartedPreparing) {
            holder.hasStartedPreparing = true;
            prepareChildSource(holder, holder.mediaSource);
        } else if (holder.isPrepared) {
            mediaPeriod.createPeriod(id.copyWithPeriodUid(getChildPeriodUid(holder, id.periodUid)));
        }
        return mediaPeriod;
    }

    public final void releasePeriod(MediaPeriod mediaPeriod) {
        MediaSourceHolder holder = (MediaSourceHolder) Assertions.checkNotNull(this.mediaSourceByMediaPeriod.remove(mediaPeriod));
        ((DeferredMediaPeriod) mediaPeriod).releasePeriod();
        holder.activeMediaPeriods.remove(mediaPeriod);
        maybeReleaseChildSource(holder);
    }

    public final synchronized void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.mediaSourceHolders.clear();
        this.mediaSourceByUid.clear();
        this.shuffleOrder = this.shuffleOrder.cloneAndClear();
        this.windowCount = 0;
        this.periodCount = 0;
        if (this.playbackThreadHandler != null) {
            this.playbackThreadHandler.removeCallbacksAndMessages((Object) null);
            this.playbackThreadHandler = null;
        }
    }

    /* access modifiers changed from: protected */
    public final void onChildSourceInfoRefreshed(MediaSourceHolder mediaSourceHolder, MediaSource mediaSource, Timeline timeline, Object manifest) {
        updateMediaSourceInternal(mediaSourceHolder, timeline);
    }

    /* access modifiers changed from: protected */
    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSourceHolder mediaSourceHolder, MediaSource.MediaPeriodId mediaPeriodId) {
        for (int i = 0; i < mediaSourceHolder.activeMediaPeriods.size(); i++) {
            if (mediaSourceHolder.activeMediaPeriods.get(i).id.windowSequenceNumber == mediaPeriodId.windowSequenceNumber) {
                return mediaPeriodId.copyWithPeriodUid(getPeriodUid(mediaSourceHolder, mediaPeriodId.periodUid));
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int getWindowIndexForChildWindowIndex(MediaSourceHolder mediaSourceHolder, int windowIndex) {
        return mediaSourceHolder.firstWindowIndexInChild + windowIndex;
    }

    private void addPublicMediaSources(int index, Collection<MediaSource> mediaSources, Handler handler, Runnable actionOnCompletion) {
        boolean z = true;
        if ((handler == null) != (actionOnCompletion == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        List<MediaSourceHolder> mediaSourceHolders2 = new ArrayList<>(mediaSources.size());
        for (MediaSource mediaSource2 : mediaSources) {
            mediaSourceHolders2.add(new MediaSourceHolder(mediaSource2));
        }
        this.mediaSourcesPublic.addAll(index, mediaSourceHolders2);
        if (this.playbackThreadHandler != null && !mediaSources.isEmpty()) {
            this.playbackThreadHandler.obtainMessage(0, new MessageData(index, mediaSourceHolders2, handler, actionOnCompletion)).sendToTarget();
        } else if (actionOnCompletion != null && handler != null) {
            handler.post(actionOnCompletion);
        }
    }

    private void removePublicMediaSources(int fromIndex, int toIndex, Handler handler, Runnable actionOnCompletion) {
        boolean z = false;
        if ((handler == null) == (actionOnCompletion == null)) {
            z = true;
        }
        Assertions.checkArgument(z);
        Util.removeRange(this.mediaSourcesPublic, fromIndex, toIndex);
        Handler handler2 = this.playbackThreadHandler;
        if (handler2 != null) {
            handler2.obtainMessage(1, new MessageData(fromIndex, Integer.valueOf(toIndex), handler, actionOnCompletion)).sendToTarget();
        } else if (actionOnCompletion != null && handler != null) {
            handler.post(actionOnCompletion);
        }
    }

    private void movePublicMediaSource(int currentIndex, int newIndex, Handler handler, Runnable actionOnCompletion) {
        boolean z = true;
        if ((handler == null) != (actionOnCompletion == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        List<MediaSourceHolder> list = this.mediaSourcesPublic;
        list.add(newIndex, list.remove(currentIndex));
        Handler handler2 = this.playbackThreadHandler;
        if (handler2 != null) {
            handler2.obtainMessage(2, new MessageData(currentIndex, Integer.valueOf(newIndex), handler, actionOnCompletion)).sendToTarget();
        } else if (actionOnCompletion != null && handler != null) {
            handler.post(actionOnCompletion);
        }
    }

    private void setPublicShuffleOrder(ShuffleOrder shuffleOrder2, Handler handler, Runnable actionOnCompletion) {
        boolean z = true;
        if ((handler == null) != (actionOnCompletion == null)) {
            z = false;
        }
        Assertions.checkArgument(z);
        Handler playbackThreadHandler2 = this.playbackThreadHandler;
        if (playbackThreadHandler2 != null) {
            int size = getSize();
            if (shuffleOrder2.getLength() != size) {
                shuffleOrder2 = shuffleOrder2.cloneAndClear().cloneAndInsert(0, size);
            }
            playbackThreadHandler2.obtainMessage(3, new MessageData(0, shuffleOrder2, handler, actionOnCompletion)).sendToTarget();
            return;
        }
        this.shuffleOrder = shuffleOrder2.getLength() > 0 ? shuffleOrder2.cloneAndClear() : shuffleOrder2;
        if (actionOnCompletion != null && handler != null) {
            handler.post(actionOnCompletion);
        }
    }

    /* access modifiers changed from: private */
    public boolean handleMessage(Message msg) {
        int i = msg.what;
        if (i == 0) {
            MessageData<Collection<MediaSourceHolder>> addMessage = (MessageData) Util.castNonNull(msg.obj);
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(addMessage.index, ((Collection) addMessage.customData).size());
            addMediaSourcesInternal(addMessage.index, (Collection) addMessage.customData);
            scheduleListenerNotification(addMessage.handler, addMessage.actionOnCompletion);
        } else if (i == 1) {
            MessageData<Integer> removeMessage = (MessageData) Util.castNonNull(msg.obj);
            int fromIndex = removeMessage.index;
            int toIndex = ((Integer) removeMessage.customData).intValue();
            if (fromIndex == 0 && toIndex == this.shuffleOrder.getLength()) {
                this.shuffleOrder = this.shuffleOrder.cloneAndClear();
            } else {
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(fromIndex, toIndex);
            }
            for (int index = toIndex - 1; index >= fromIndex; index--) {
                removeMediaSourceInternal(index);
            }
            scheduleListenerNotification(removeMessage.handler, removeMessage.actionOnCompletion);
        } else if (i == 2) {
            MessageData<Integer> moveMessage = (MessageData) Util.castNonNull(msg.obj);
            ShuffleOrder cloneAndRemove = this.shuffleOrder.cloneAndRemove(moveMessage.index, moveMessage.index + 1);
            this.shuffleOrder = cloneAndRemove;
            this.shuffleOrder = cloneAndRemove.cloneAndInsert(((Integer) moveMessage.customData).intValue(), 1);
            moveMediaSourceInternal(moveMessage.index, ((Integer) moveMessage.customData).intValue());
            scheduleListenerNotification(moveMessage.handler, moveMessage.actionOnCompletion);
        } else if (i == 3) {
            MessageData<ShuffleOrder> shuffleOrderMessage = (MessageData) Util.castNonNull(msg.obj);
            this.shuffleOrder = (ShuffleOrder) shuffleOrderMessage.customData;
            scheduleListenerNotification(shuffleOrderMessage.handler, shuffleOrderMessage.actionOnCompletion);
        } else if (i == 4) {
            notifyListener();
        } else if (i == 5) {
            ((EventDispatcher) Util.castNonNull(msg.obj)).dispatch($$Lambda$OJugHprsUFfqZRhdKwrL9G7ru30.INSTANCE);
        } else {
            throw new IllegalStateException();
        }
        return true;
    }

    private void scheduleListenerNotification() {
        scheduleListenerNotification((Handler) null, (Runnable) null);
    }

    private void scheduleListenerNotification(Handler handler, Runnable actionOnCompletion) {
        if (!this.listenerNotificationScheduled) {
            ((Handler) Assertions.checkNotNull(this.playbackThreadHandler)).obtainMessage(4).sendToTarget();
            this.listenerNotificationScheduled = true;
        }
        if (actionOnCompletion != null && handler != null) {
            this.pendingOnCompletionActions.addListener(handler, actionOnCompletion);
        }
    }

    private void notifyListener() {
        this.listenerNotificationScheduled = false;
        EventDispatcher<Runnable> actionsOnCompletion = this.pendingOnCompletionActions;
        this.pendingOnCompletionActions = new EventDispatcher<>();
        refreshSourceInfo(new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder, this.isAtomic), (Object) null);
        ((Handler) Assertions.checkNotNull(this.playbackThreadHandler)).obtainMessage(5, actionsOnCompletion).sendToTarget();
    }

    private void addMediaSourcesInternal(int index, Collection<MediaSourceHolder> mediaSourceHolders2) {
        for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders2) {
            addMediaSourceInternal(index, mediaSourceHolder);
            index++;
        }
    }

    private void addMediaSourceInternal(int newIndex, MediaSourceHolder newMediaSourceHolder) {
        if (newIndex > 0) {
            MediaSourceHolder previousHolder = this.mediaSourceHolders.get(newIndex - 1);
            newMediaSourceHolder.reset(newIndex, previousHolder.firstWindowIndexInChild + previousHolder.timeline.getWindowCount(), previousHolder.firstPeriodIndexInChild + previousHolder.timeline.getPeriodCount());
        } else {
            newMediaSourceHolder.reset(newIndex, 0, 0);
        }
        correctOffsets(newIndex, 1, newMediaSourceHolder.timeline.getWindowCount(), newMediaSourceHolder.timeline.getPeriodCount());
        this.mediaSourceHolders.add(newIndex, newMediaSourceHolder);
        this.mediaSourceByUid.put(newMediaSourceHolder.uid, newMediaSourceHolder);
        if (!this.useLazyPreparation) {
            newMediaSourceHolder.hasStartedPreparing = true;
            prepareChildSource(newMediaSourceHolder, newMediaSourceHolder.mediaSource);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaSourceInternal(com.google.android.exoplayer2.source.ConcatenatingMediaSource.MediaSourceHolder r17, com.google.android.exoplayer2.Timeline r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r8 = r18
            if (r1 == 0) goto L_0x00c4
            com.google.android.exoplayer2.source.ConcatenatingMediaSource$DeferredTimeline r9 = r1.timeline
            com.google.android.exoplayer2.Timeline r2 = r9.getTimeline()
            if (r2 != r8) goto L_0x0011
            return
        L_0x0011:
            int r2 = r18.getWindowCount()
            int r3 = r9.getWindowCount()
            int r10 = r2 - r3
            int r2 = r18.getPeriodCount()
            int r3 = r9.getPeriodCount()
            int r11 = r2 - r3
            r2 = 0
            r12 = 1
            if (r10 != 0) goto L_0x002b
            if (r11 == 0) goto L_0x0031
        L_0x002b:
            int r3 = r1.childIndex
            int r3 = r3 + r12
            r0.correctOffsets(r3, r2, r10, r11)
        L_0x0031:
            boolean r3 = r1.isPrepared
            if (r3 == 0) goto L_0x003d
            com.google.android.exoplayer2.source.ConcatenatingMediaSource$DeferredTimeline r2 = r9.cloneWithUpdatedTimeline(r8)
            r1.timeline = r2
            goto L_0x00be
        L_0x003d:
            boolean r3 = r18.isEmpty()
            if (r3 == 0) goto L_0x0050
            java.lang.Object r2 = com.google.android.exoplayer2.source.ConcatenatingMediaSource.DeferredTimeline.DUMMY_ID
            com.google.android.exoplayer2.source.ConcatenatingMediaSource$DeferredTimeline r2 = com.google.android.exoplayer2.source.ConcatenatingMediaSource.DeferredTimeline.createWithRealTimeline(r8, r2)
            r1.timeline = r2
            goto L_0x00be
        L_0x0050:
            java.util.List<com.google.android.exoplayer2.source.DeferredMediaPeriod> r3 = r1.activeMediaPeriods
            int r3 = r3.size()
            if (r3 > r12) goto L_0x005a
            r3 = 1
            goto L_0x005b
        L_0x005a:
            r3 = 0
        L_0x005b:
            com.google.android.exoplayer2.util.Assertions.checkState(r3)
            java.util.List<com.google.android.exoplayer2.source.DeferredMediaPeriod> r3 = r1.activeMediaPeriods
            boolean r3 = r3.isEmpty()
            if (r3 == 0) goto L_0x0068
            r3 = 0
            goto L_0x0070
        L_0x0068:
            java.util.List<com.google.android.exoplayer2.source.DeferredMediaPeriod> r3 = r1.activeMediaPeriods
            java.lang.Object r3 = r3.get(r2)
            com.google.android.exoplayer2.source.DeferredMediaPeriod r3 = (com.google.android.exoplayer2.source.DeferredMediaPeriod) r3
        L_0x0070:
            r13 = r3
            com.google.android.exoplayer2.Timeline$Window r3 = r0.window
            r8.getWindow(r2, r3)
            com.google.android.exoplayer2.Timeline$Window r2 = r0.window
            long r2 = r2.getDefaultPositionUs()
            if (r13 == 0) goto L_0x008b
            long r4 = r13.getPreparePositionUs()
            r6 = 0
            int r14 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x008b
            r2 = r4
            r14 = r2
            goto L_0x008c
        L_0x008b:
            r14 = r2
        L_0x008c:
            com.google.android.exoplayer2.Timeline$Window r3 = r0.window
            com.google.android.exoplayer2.Timeline$Period r4 = r0.period
            r5 = 0
            r2 = r18
            r6 = r14
            android.util.Pair r2 = r2.getPeriodPosition(r3, r4, r5, r6)
            java.lang.Object r3 = r2.first
            java.lang.Object r4 = r2.second
            java.lang.Long r4 = (java.lang.Long) r4
            long r4 = r4.longValue()
            com.google.android.exoplayer2.source.ConcatenatingMediaSource$DeferredTimeline r6 = com.google.android.exoplayer2.source.ConcatenatingMediaSource.DeferredTimeline.createWithRealTimeline(r8, r3)
            r1.timeline = r6
            if (r13 == 0) goto L_0x00be
            r13.overridePreparePositionUs(r4)
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r6 = r13.id
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r7 = r13.id
            java.lang.Object r7 = r7.periodUid
            java.lang.Object r7 = getChildPeriodUid(r1, r7)
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r6 = r6.copyWithPeriodUid(r7)
            r13.createPeriod(r6)
        L_0x00be:
            r1.isPrepared = r12
            r16.scheduleListenerNotification()
            return
        L_0x00c4:
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            r2.<init>()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ConcatenatingMediaSource.updateMediaSourceInternal(com.google.android.exoplayer2.source.ConcatenatingMediaSource$MediaSourceHolder, com.google.android.exoplayer2.Timeline):void");
    }

    private void removeMediaSourceInternal(int index) {
        MediaSourceHolder holder = this.mediaSourceHolders.remove(index);
        this.mediaSourceByUid.remove(holder.uid);
        Timeline oldTimeline = holder.timeline;
        correctOffsets(index, -1, -oldTimeline.getWindowCount(), -oldTimeline.getPeriodCount());
        holder.isRemoved = true;
        maybeReleaseChildSource(holder);
    }

    private void moveMediaSourceInternal(int currentIndex, int newIndex) {
        int startIndex = Math.min(currentIndex, newIndex);
        int endIndex = Math.max(currentIndex, newIndex);
        int windowOffset = this.mediaSourceHolders.get(startIndex).firstWindowIndexInChild;
        int periodOffset = this.mediaSourceHolders.get(startIndex).firstPeriodIndexInChild;
        List<MediaSourceHolder> list = this.mediaSourceHolders;
        list.add(newIndex, list.remove(currentIndex));
        for (int i = startIndex; i <= endIndex; i++) {
            MediaSourceHolder holder = this.mediaSourceHolders.get(i);
            holder.firstWindowIndexInChild = windowOffset;
            holder.firstPeriodIndexInChild = periodOffset;
            windowOffset += holder.timeline.getWindowCount();
            periodOffset += holder.timeline.getPeriodCount();
        }
    }

    private void correctOffsets(int startIndex, int childIndexUpdate, int windowOffsetUpdate, int periodOffsetUpdate) {
        this.windowCount += windowOffsetUpdate;
        this.periodCount += periodOffsetUpdate;
        for (int i = startIndex; i < this.mediaSourceHolders.size(); i++) {
            this.mediaSourceHolders.get(i).childIndex += childIndexUpdate;
            this.mediaSourceHolders.get(i).firstWindowIndexInChild += windowOffsetUpdate;
            this.mediaSourceHolders.get(i).firstPeriodIndexInChild += periodOffsetUpdate;
        }
    }

    private void maybeReleaseChildSource(MediaSourceHolder mediaSourceHolder) {
        if (mediaSourceHolder.isRemoved && mediaSourceHolder.hasStartedPreparing && mediaSourceHolder.activeMediaPeriods.isEmpty()) {
            releaseChildSource(mediaSourceHolder);
        }
    }

    private static Object getMediaSourceHolderUid(Object periodUid) {
        return ConcatenatedTimeline.getChildTimelineUidFromConcatenatedUid(periodUid);
    }

    private static Object getChildPeriodUid(MediaSourceHolder holder, Object periodUid) {
        Object childUid = ConcatenatedTimeline.getChildPeriodUidFromConcatenatedUid(periodUid);
        return childUid.equals(DeferredTimeline.DUMMY_ID) ? holder.timeline.replacedId : childUid;
    }

    private static Object getPeriodUid(MediaSourceHolder holder, Object childPeriodUid) {
        if (holder.timeline.replacedId.equals(childPeriodUid)) {
            childPeriodUid = DeferredTimeline.DUMMY_ID;
        }
        return ConcatenatedTimeline.getConcatenatedUid(holder.uid, childPeriodUid);
    }

    static final class MediaSourceHolder implements Comparable<MediaSourceHolder> {
        public final List<DeferredMediaPeriod> activeMediaPeriods = new ArrayList();
        public int childIndex;
        public int firstPeriodIndexInChild;
        public int firstWindowIndexInChild;
        public boolean hasStartedPreparing;
        public boolean isPrepared;
        public boolean isRemoved;
        public final MediaSource mediaSource;
        public DeferredTimeline timeline;
        public final Object uid = new Object();

        public MediaSourceHolder(MediaSource mediaSource2) {
            this.mediaSource = mediaSource2;
            this.timeline = DeferredTimeline.createWithDummyTimeline(mediaSource2.getTag());
        }

        public void reset(int childIndex2, int firstWindowIndexInChild2, int firstPeriodIndexInChild2) {
            this.childIndex = childIndex2;
            this.firstWindowIndexInChild = firstWindowIndexInChild2;
            this.firstPeriodIndexInChild = firstPeriodIndexInChild2;
            this.hasStartedPreparing = false;
            this.isPrepared = false;
            this.isRemoved = false;
            this.activeMediaPeriods.clear();
        }

        public int compareTo(MediaSourceHolder other) {
            return this.firstPeriodIndexInChild - other.firstPeriodIndexInChild;
        }
    }

    private static final class MessageData<T> {
        public final Runnable actionOnCompletion;
        public final T customData;
        public final Handler handler;
        public final int index;

        public MessageData(int index2, T customData2, Handler handler2, Runnable actionOnCompletion2) {
            this.index = index2;
            this.customData = customData2;
            this.handler = handler2;
            this.actionOnCompletion = actionOnCompletion2;
        }
    }

    private static final class ConcatenatedTimeline extends AbstractConcatenatedTimeline {
        private final HashMap<Object, Integer> childIndexByUid = new HashMap<>();
        private final int[] firstPeriodInChildIndices;
        private final int[] firstWindowInChildIndices;
        private final int periodCount;
        private final Timeline[] timelines;
        private final Object[] uids;
        private final int windowCount;

        public ConcatenatedTimeline(Collection<MediaSourceHolder> mediaSourceHolders, int windowCount2, int periodCount2, ShuffleOrder shuffleOrder, boolean isAtomic) {
            super(isAtomic, shuffleOrder);
            this.windowCount = windowCount2;
            this.periodCount = periodCount2;
            int childCount = mediaSourceHolders.size();
            this.firstPeriodInChildIndices = new int[childCount];
            this.firstWindowInChildIndices = new int[childCount];
            this.timelines = new Timeline[childCount];
            this.uids = new Object[childCount];
            int index = 0;
            for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
                this.timelines[index] = mediaSourceHolder.timeline;
                this.firstPeriodInChildIndices[index] = mediaSourceHolder.firstPeriodIndexInChild;
                this.firstWindowInChildIndices[index] = mediaSourceHolder.firstWindowIndexInChild;
                this.uids[index] = mediaSourceHolder.uid;
                this.childIndexByUid.put(this.uids[index], Integer.valueOf(index));
                index++;
            }
        }

        /* access modifiers changed from: protected */
        public int getChildIndexByPeriodIndex(int periodIndex) {
            return Util.binarySearchFloor(this.firstPeriodInChildIndices, periodIndex + 1, false, false);
        }

        /* access modifiers changed from: protected */
        public int getChildIndexByWindowIndex(int windowIndex) {
            return Util.binarySearchFloor(this.firstWindowInChildIndices, windowIndex + 1, false, false);
        }

        /* access modifiers changed from: protected */
        public int getChildIndexByChildUid(Object childUid) {
            Integer index = this.childIndexByUid.get(childUid);
            if (index == null) {
                return -1;
            }
            return index.intValue();
        }

        /* access modifiers changed from: protected */
        public Timeline getTimelineByChildIndex(int childIndex) {
            return this.timelines[childIndex];
        }

        /* access modifiers changed from: protected */
        public int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.firstPeriodInChildIndices[childIndex];
        }

        /* access modifiers changed from: protected */
        public int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.firstWindowInChildIndices[childIndex];
        }

        /* access modifiers changed from: protected */
        public Object getChildUidByChildIndex(int childIndex) {
            return this.uids[childIndex];
        }

        public int getWindowCount() {
            return this.windowCount;
        }

        public int getPeriodCount() {
            return this.periodCount;
        }
    }

    private static final class DeferredTimeline extends ForwardingTimeline {
        /* access modifiers changed from: private */
        public static final Object DUMMY_ID = new Object();
        /* access modifiers changed from: private */
        public final Object replacedId;

        public static DeferredTimeline createWithDummyTimeline(Object windowTag) {
            return new DeferredTimeline(new DummyTimeline(windowTag), DUMMY_ID);
        }

        public static DeferredTimeline createWithRealTimeline(Timeline timeline, Object firstPeriodUid) {
            return new DeferredTimeline(timeline, firstPeriodUid);
        }

        private DeferredTimeline(Timeline timeline, Object replacedId2) {
            super(timeline);
            this.replacedId = replacedId2;
        }

        public DeferredTimeline cloneWithUpdatedTimeline(Timeline timeline) {
            return new DeferredTimeline(timeline, this.replacedId);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
            this.timeline.getPeriod(periodIndex, period, setIds);
            if (Util.areEqual(period.uid, this.replacedId)) {
                period.uid = DUMMY_ID;
            }
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            return this.timeline.getIndexOfPeriod(DUMMY_ID.equals(uid) ? this.replacedId : uid);
        }

        public Object getUidOfPeriod(int periodIndex) {
            Object uid = this.timeline.getUidOfPeriod(periodIndex);
            return Util.areEqual(uid, this.replacedId) ? DUMMY_ID : uid;
        }
    }

    private static final class DummyTimeline extends Timeline {
        private final Object tag;

        public DummyTimeline(Object tag2) {
            this.tag = tag2;
        }

        public int getWindowCount() {
            return 1;
        }

        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, boolean setTag, long defaultPositionProjectionUs) {
            return window.set(this.tag, C.TIME_UNSET, C.TIME_UNSET, false, true, 0, C.TIME_UNSET, 0, 0, 0);
        }

        public int getPeriodCount() {
            return 1;
        }

        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIds) {
            return period.set(0, DeferredTimeline.DUMMY_ID, 0, C.TIME_UNSET, 0);
        }

        public int getIndexOfPeriod(Object uid) {
            return uid == DeferredTimeline.DUMMY_ID ? 0 : -1;
        }

        public Object getUidOfPeriod(int periodIndex) {
            return DeferredTimeline.DUMMY_ID;
        }
    }

    private static final class DummyMediaSource extends BaseMediaSource {
        private DummyMediaSource() {
        }

        /* access modifiers changed from: protected */
        public void prepareSourceInternal(TransferListener mediaTransferListener) {
        }

        public Object getTag() {
            return null;
        }

        /* access modifiers changed from: protected */
        public void releaseSourceInternal() {
        }

        public void maybeThrowSourceInfoRefreshError() throws IOException {
        }

        public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
            throw new UnsupportedOperationException();
        }

        public void releasePeriod(MediaPeriod mediaPeriod) {
        }
    }
}
