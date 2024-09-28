package com.google.android.exoplayer2.source.hls;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public final class HlsMediaPeriod implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final TransferListener mediaTransferListener;
    private boolean notifiedReadingStarted;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap<>();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public /* synthetic */ List<StreamKey> getStreamKeys(TrackSelection trackSelection) {
        return MediaPeriod.CC.$default$getStreamKeys(this, trackSelection);
    }

    public HlsMediaPeriod(HlsExtractorFactory extractorFactory2, HlsPlaylistTracker playlistTracker2, HlsDataSourceFactory dataSourceFactory2, TransferListener mediaTransferListener2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, MediaSourceEventListener.EventDispatcher eventDispatcher2, Allocator allocator2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, boolean allowChunklessPreparation2) {
        this.extractorFactory = extractorFactory2;
        this.playlistTracker = playlistTracker2;
        this.dataSourceFactory = dataSourceFactory2;
        this.mediaTransferListener = mediaTransferListener2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
        this.eventDispatcher = eventDispatcher2;
        this.allocator = allocator2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.allowChunklessPreparation = allowChunklessPreparation2;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory2.createCompositeSequenceableLoader(new SequenceableLoader[0]);
        eventDispatcher2.mediaPeriodCreated();
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.release();
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public void prepare(MediaPeriod.Callback callback2, long positionUs) {
        this.callback = callback2;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers;
        int i;
        TrackSelection[] trackSelectionArr = selections;
        SampleStream[] sampleStreamArr = streams;
        int[] streamChildIndices = new int[trackSelectionArr.length];
        int[] selectionChildIndices = new int[trackSelectionArr.length];
        for (int i2 = 0; i2 < trackSelectionArr.length; i2++) {
            if (sampleStreamArr[i2] == null) {
                i = -1;
            } else {
                i = this.streamWrapperIndices.get(sampleStreamArr[i2]).intValue();
            }
            streamChildIndices[i2] = i;
            selectionChildIndices[i2] = -1;
            if (trackSelectionArr[i2] != null) {
                TrackGroup trackGroup = trackSelectionArr[i2].getTrackGroup();
                int j = 0;
                while (true) {
                    HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
                    if (j >= hlsSampleStreamWrapperArr.length) {
                        break;
                    } else if (hlsSampleStreamWrapperArr[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i2] = j;
                        break;
                    } else {
                        j++;
                    }
                }
            }
        }
        boolean forceReset = false;
        this.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[trackSelectionArr.length];
        SampleStream[] childStreams = new SampleStream[trackSelectionArr.length];
        TrackSelection[] childSelections = new TrackSelection[trackSelectionArr.length];
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers2 = new HlsSampleStreamWrapper[this.sampleStreamWrappers.length];
        int newEnabledSampleStreamWrapperCount = 0;
        int i3 = 0;
        while (i3 < this.sampleStreamWrappers.length) {
            for (int j2 = 0; j2 < trackSelectionArr.length; j2++) {
                TrackSelection trackSelection = null;
                childStreams[j2] = streamChildIndices[j2] == i3 ? sampleStreamArr[j2] : null;
                if (selectionChildIndices[j2] == i3) {
                    trackSelection = trackSelectionArr[j2];
                }
                childSelections[j2] = trackSelection;
            }
            HlsSampleStreamWrapper sampleStreamWrapper = this.sampleStreamWrappers[i3];
            HlsSampleStreamWrapper sampleStreamWrapper2 = sampleStreamWrapper;
            int i4 = i3;
            HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers3 = newEnabledSampleStreamWrappers2;
            int newEnabledSampleStreamWrapperCount2 = newEnabledSampleStreamWrapperCount;
            TrackSelection[] childSelections2 = childSelections;
            boolean wasReset = sampleStreamWrapper.selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs, forceReset);
            boolean wrapperEnabled = false;
            for (int j3 = 0; j3 < trackSelectionArr.length; j3++) {
                if (selectionChildIndices[j3] == i4) {
                    Assertions.checkState(childStreams[j3] != null);
                    newStreams[j3] = childStreams[j3];
                    wrapperEnabled = true;
                    this.streamWrapperIndices.put(childStreams[j3], Integer.valueOf(i4));
                } else if (streamChildIndices[j3] == i4) {
                    Assertions.checkState(childStreams[j3] == null);
                }
            }
            if (wrapperEnabled) {
                newEnabledSampleStreamWrappers = newEnabledSampleStreamWrappers3;
                int newEnabledSampleStreamWrapperCount3 = newEnabledSampleStreamWrapperCount2;
                newEnabledSampleStreamWrappers[newEnabledSampleStreamWrapperCount3] = sampleStreamWrapper2;
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount3 + 1;
                if (newEnabledSampleStreamWrapperCount3 == 0) {
                    HlsSampleStreamWrapper sampleStreamWrapper3 = sampleStreamWrapper2;
                    sampleStreamWrapper3.setIsTimestampMaster(true);
                    if (!wasReset) {
                        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                        if (hlsSampleStreamWrapperArr2.length != 0 && sampleStreamWrapper3 == hlsSampleStreamWrapperArr2[0]) {
                        }
                    }
                    this.timestampAdjusterProvider.reset();
                    forceReset = true;
                } else {
                    sampleStreamWrapper2.setIsTimestampMaster(false);
                }
            } else {
                newEnabledSampleStreamWrappers = newEnabledSampleStreamWrappers3;
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
            }
            i3 = i4 + 1;
            sampleStreamArr = streams;
            newEnabledSampleStreamWrappers2 = newEnabledSampleStreamWrappers;
            childSelections = childSelections2;
        }
        int i5 = i3;
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr3 = (HlsSampleStreamWrapper[]) Arrays.copyOf(newEnabledSampleStreamWrappers2, newEnabledSampleStreamWrapperCount);
        this.enabledSampleStreamWrappers = hlsSampleStreamWrapperArr3;
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(hlsSampleStreamWrapperArr3);
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        if (this.trackGroups != null) {
            return this.compositeSequenceableLoader.continueLoading(positionUs);
        }
        for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
            wrapper.continuePreparing();
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        if (this.notifiedReadingStarted) {
            return C.TIME_UNSET;
        }
        this.eventDispatcher.readingStarted();
        this.notifiedReadingStarted = true;
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.enabledSampleStreamWrappers;
        if (hlsSampleStreamWrapperArr.length > 0) {
            boolean forceReset = hlsSampleStreamWrapperArr[0].seekToUs(positionUs, false);
            int i = 1;
            while (true) {
                HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                if (i >= hlsSampleStreamWrapperArr2.length) {
                    break;
                }
                hlsSampleStreamWrapperArr2[i].seekToUs(positionUs, forceReset);
                i++;
            }
            if (forceReset) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onPrepared() {
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
                int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
                int j = 0;
                while (j < wrapperTrackGroupCount) {
                    trackGroupArray[trackGroupIndex] = sampleStreamWrapper2.getTrackGroups().get(j);
                    j++;
                    trackGroupIndex++;
                }
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl url) {
        this.playlistTracker.refreshPlaylist(url);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        this.callback.onContinueLoadingRequested(this);
    }

    public void onPlaylistChanged() {
        this.callback.onContinueLoadingRequested(this);
    }

    public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl url, long blacklistDurationMs) {
        boolean noBlacklistingFailure = true;
        for (HlsSampleStreamWrapper streamWrapper : this.sampleStreamWrappers) {
            noBlacklistingFailure &= streamWrapper.onPlaylistError(url, blacklistDurationMs);
        }
        this.callback.onContinueLoadingRequested(this);
        return noBlacklistingFailure;
    }

    private void buildAndPrepareSampleStreamWrappers(long positionUs) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsMasterPlaylist.HlsUrl> audioRenditions = masterPlaylist.audios;
        List<HlsMasterPlaylist.HlsUrl> subtitleRenditions = masterPlaylist.subtitles;
        int i = 1;
        int wrapperCount = audioRenditions.size() + 1 + subtitleRenditions.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[wrapperCount];
        this.pendingPrepareCount = wrapperCount;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, positionUs);
        int currentWrapperIndex = 1;
        int i2 = 0;
        while (i2 < audioRenditions.size()) {
            HlsMasterPlaylist.HlsUrl audioRendition = audioRenditions.get(i2);
            HlsMasterPlaylist.HlsUrl[] hlsUrlArr = new HlsMasterPlaylist.HlsUrl[i];
            hlsUrlArr[0] = audioRendition;
            HlsMasterPlaylist.HlsUrl audioRendition2 = audioRendition;
            int i3 = i2;
            HlsMasterPlaylist masterPlaylist2 = masterPlaylist;
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, hlsUrlArr, (Format) null, Collections.emptyList(), positionUs);
            int currentWrapperIndex2 = currentWrapperIndex + 1;
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            Format renditionFormat = audioRendition2.format;
            if (!this.allowChunklessPreparation || renditionFormat.codecs == null) {
                sampleStreamWrapper.continuePreparing();
            } else {
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup(audioRendition2.format)), 0, TrackGroupArray.EMPTY);
            }
            i2 = i3 + 1;
            currentWrapperIndex = currentWrapperIndex2;
            masterPlaylist = masterPlaylist2;
            i = 1;
        }
        int i4 = i2;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist;
        char c = 0;
        int i5 = 0;
        while (i5 < subtitleRenditions.size()) {
            HlsMasterPlaylist.HlsUrl url = subtitleRenditions.get(i5);
            HlsMasterPlaylist.HlsUrl[] hlsUrlArr2 = new HlsMasterPlaylist.HlsUrl[1];
            hlsUrlArr2[c] = url;
            HlsSampleStreamWrapper sampleStreamWrapper2 = buildSampleStreamWrapper(3, hlsUrlArr2, (Format) null, Collections.emptyList(), positionUs);
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper2;
            sampleStreamWrapper2.prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup(url.format)), 0, TrackGroupArray.EMPTY);
            i5++;
            currentWrapperIndex++;
            c = 0;
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist masterPlaylist, long positionUs) {
        List<HlsMasterPlaylist.HlsUrl> selectedVariants;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist;
        List<HlsMasterPlaylist.HlsUrl> selectedVariants2 = new ArrayList<>(hlsMasterPlaylist.variants);
        ArrayList<HlsMasterPlaylist.HlsUrl> definiteVideoVariants = new ArrayList<>();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < selectedVariants2.size(); i++) {
            HlsMasterPlaylist.HlsUrl variant = selectedVariants2.get(i);
            Format format = variant.format;
            if (format.height > 0 || Util.getCodecsOfType(format.codecs, 2) != null) {
                definiteVideoVariants.add(variant);
            } else if (Util.getCodecsOfType(format.codecs, 1) != null) {
                arrayList.add(variant);
            }
        }
        if (definiteVideoVariants.isEmpty() == 0) {
            selectedVariants = definiteVideoVariants;
        } else {
            if (arrayList.size() < selectedVariants2.size()) {
                selectedVariants2.removeAll(arrayList);
            }
            selectedVariants = selectedVariants2;
        }
        Assertions.checkArgument(!selectedVariants.isEmpty());
        HlsMasterPlaylist.HlsUrl[] variants = (HlsMasterPlaylist.HlsUrl[]) selectedVariants.toArray(new HlsMasterPlaylist.HlsUrl[0]);
        String codecs = variants[0].format.codecs;
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, variants, hlsMasterPlaylist.muxedAudioFormat, hlsMasterPlaylist.muxedCaptionFormats, positionUs);
        this.sampleStreamWrappers[0] = sampleStreamWrapper;
        if (!this.allowChunklessPreparation || codecs == null) {
            sampleStreamWrapper.setIsTimestampMaster(true);
            sampleStreamWrapper.continuePreparing();
            return;
        }
        boolean variantsContainVideoCodecs = Util.getCodecsOfType(codecs, 2) != null;
        boolean variantsContainAudioCodecs = Util.getCodecsOfType(codecs, 1) != null;
        List<TrackGroup> muxedTrackGroups = new ArrayList<>();
        if (variantsContainVideoCodecs) {
            Format[] videoFormats = new Format[selectedVariants.size()];
            for (int i2 = 0; i2 < videoFormats.length; i2++) {
                videoFormats[i2] = deriveVideoFormat(variants[i2].format);
            }
            muxedTrackGroups.add(new TrackGroup(videoFormats));
            if (!variantsContainAudioCodecs) {
            } else if (hlsMasterPlaylist.muxedAudioFormat != null || hlsMasterPlaylist.audios.isEmpty()) {
                boolean z = variantsContainVideoCodecs;
                muxedTrackGroups.add(new TrackGroup(deriveAudioFormat(variants[0].format, hlsMasterPlaylist.muxedAudioFormat, false)));
            } else {
                boolean z2 = variantsContainVideoCodecs;
            }
            List<Format> ccFormats = hlsMasterPlaylist.muxedCaptionFormats;
            if (ccFormats != null) {
                for (int i3 = 0; i3 < ccFormats.size(); i3++) {
                    muxedTrackGroups.add(new TrackGroup(ccFormats.get(i3)));
                }
            }
        } else {
            if (variantsContainAudioCodecs) {
                Format[] audioFormats = new Format[selectedVariants.size()];
                for (int i4 = 0; i4 < audioFormats.length; i4++) {
                    audioFormats[i4] = deriveAudioFormat(variants[i4].format, hlsMasterPlaylist.muxedAudioFormat, true);
                }
                muxedTrackGroups.add(new TrackGroup(audioFormats));
            } else {
                throw new IllegalArgumentException("Unexpected codecs attribute: " + codecs);
            }
        }
        TrackGroup id3TrackGroup = new TrackGroup(Format.createSampleFormat("ID3", MimeTypes.APPLICATION_ID3, (String) null, -1, (DrmInitData) null));
        muxedTrackGroups.add(id3TrackGroup);
        sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[]) muxedTrackGroups.toArray(new TrackGroup[0])), 0, new TrackGroupArray(id3TrackGroup));
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, HlsMasterPlaylist.HlsUrl[] variants, Format muxedAudioFormat, List<Format> muxedCaptionFormats, long positionUs) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, variants, this.dataSourceFactory, this.mediaTransferListener, this.timestampAdjusterProvider, muxedCaptionFormats), this.allocator, positionUs, muxedAudioFormat, this.loadErrorHandlingPolicy, this.eventDispatcher);
    }

    private static Format deriveVideoFormat(Format variantFormat) {
        String codecs = Util.getCodecsOfType(variantFormat.codecs, 2);
        return Format.createVideoContainerFormat(variantFormat.id, variantFormat.label, variantFormat.containerMimeType, MimeTypes.getMediaMimeType(codecs), codecs, variantFormat.bitrate, variantFormat.width, variantFormat.height, variantFormat.frameRate, (List<byte[]>) null, variantFormat.selectionFlags);
    }

    private static Format deriveAudioFormat(Format variantFormat, Format mediaTagFormat, boolean isPrimaryTrackInVariant) {
        String codecs;
        Format format = variantFormat;
        Format format2 = mediaTagFormat;
        int channelCount = -1;
        int selectionFlags = 0;
        String language = null;
        String label = null;
        if (format2 != null) {
            codecs = format2.codecs;
            channelCount = format2.channelCount;
            selectionFlags = format2.selectionFlags;
            language = format2.language;
            label = format2.label;
        } else {
            codecs = Util.getCodecsOfType(format.codecs, 1);
            if (isPrimaryTrackInVariant) {
                channelCount = format.channelCount;
                selectionFlags = format.selectionFlags;
                language = format.label;
                label = format.label;
            }
        }
        return Format.createAudioContainerFormat(format.id, label, format.containerMimeType, MimeTypes.getMediaMimeType(codecs), codecs, isPrimaryTrackInVariant ? format.bitrate : -1, channelCount, -1, (List<byte[]>) null, selectionFlags, language);
    }
}
