package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.util.Math;
import com.googlecode.mp4parser.util.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SyncSampleIntersectFinderImpl implements FragmentIntersectionFinder {
    private static Logger LOG = Logger.getLogger(SyncSampleIntersectFinderImpl.class.getName());
    private final int minFragmentDurationSeconds;
    private Movie movie;
    private Track referenceTrack;

    public SyncSampleIntersectFinderImpl(Movie movie2, Track referenceTrack2, int minFragmentDurationSeconds2) {
        this.movie = movie2;
        this.referenceTrack = referenceTrack2;
        this.minFragmentDurationSeconds = minFragmentDurationSeconds2;
    }

    static String getFormat(Track track) {
        SampleEntry se = track.getSampleDescriptionBox().getSampleEntry();
        String type = se.getType();
        if (type.equals(VisualSampleEntry.TYPE_ENCRYPTED) || type.equals(AudioSampleEntry.TYPE_ENCRYPTED) || type.equals(VisualSampleEntry.TYPE_ENCRYPTED)) {
            return ((OriginalFormatBox) Path.getPath((Box) se, "sinf/frma")).getDataFormat();
        }
        return type;
    }

    public long[] sampleNumbers(Track track) {
        Track track2 = track;
        if ("vide".equals(track.getHandler())) {
            if (track.getSyncSamples() == null || track.getSyncSamples().length <= 0) {
                throw new RuntimeException("Video Tracks need sync samples. Only tracks other than video may have no sync samples.");
            }
            List<long[]> times = getSyncSamplesTimestamps(this.movie, track2);
            return getCommonIndices(track.getSyncSamples(), getTimes(track2, this.movie), track.getTrackMetaData().getTimescale(), (long[][]) times.toArray(new long[times.size()][]));
        } else if ("soun".equals(track.getHandler())) {
            if (this.referenceTrack == null) {
                for (Track candidate : this.movie.getTracks()) {
                    if (candidate.getSyncSamples() != null && "vide".equals(candidate.getHandler()) && candidate.getSyncSamples().length > 0) {
                        this.referenceTrack = candidate;
                    }
                }
            }
            Track track3 = this.referenceTrack;
            if (track3 != null) {
                long[] refSyncSamples = sampleNumbers(track3);
                int refSampleCount = this.referenceTrack.getSamples().size();
                long[] syncSamples = new long[refSyncSamples.length];
                long sc = 192000;
                Iterator<Track> it = this.movie.getTracks().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        int i = refSampleCount;
                        break;
                    }
                    Track testTrack = it.next();
                    if (getFormat(track).equals(getFormat(testTrack))) {
                        AudioSampleEntry ase = (AudioSampleEntry) testTrack.getSampleDescriptionBox().getSampleEntry();
                        if (ase.getSampleRate() < 192000) {
                            long minSampleRate = ase.getSampleRate();
                            AudioSampleEntry audioSampleEntry = ase;
                            double stretch = ((double) ((long) testTrack.getSamples().size())) / ((double) refSampleCount);
                            int refSampleCount2 = refSampleCount;
                            Track track4 = testTrack;
                            long samplesPerFrame = testTrack.getSampleDurations()[0];
                            int i2 = 0;
                            while (i2 < syncSamples.length) {
                                syncSamples[i2] = (long) Math.ceil(((double) (refSyncSamples[i2] - 1)) * stretch * ((double) samplesPerFrame));
                                i2++;
                                refSampleCount2 = refSampleCount2;
                                stretch = stretch;
                                Track track5 = track;
                            }
                            sc = minSampleRate;
                        } else {
                            Track track6 = testTrack;
                            AudioSampleEntry audioSampleEntry2 = ase;
                            Track track7 = track;
                        }
                    } else {
                        Track track8 = testTrack;
                        Track track9 = track;
                    }
                }
                long samplesPerFrame2 = track.getSampleDurations()[0];
                double factor = ((double) ((AudioSampleEntry) track.getSampleDescriptionBox().getSampleEntry()).getSampleRate()) / ((double) sc);
                if (factor == Math.rint(factor)) {
                    int i3 = 0;
                    while (i3 < syncSamples.length) {
                        syncSamples[i3] = (long) (((((double) syncSamples[i3]) * factor) / ((double) samplesPerFrame2)) + 1.0d);
                        i3++;
                        Track track10 = track;
                        sc = sc;
                    }
                    return syncSamples;
                }
                throw new RuntimeException("Sample rates must be a multiple of the lowest sample rate to create a correct file!");
            }
            throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
        } else {
            for (Track candidate2 : this.movie.getTracks()) {
                if (candidate2.getSyncSamples() != null && candidate2.getSyncSamples().length > 0) {
                    long[] refSyncSamples2 = sampleNumbers(candidate2);
                    int refSampleCount3 = candidate2.getSamples().size();
                    long[] syncSamples2 = new long[refSyncSamples2.length];
                    double stretch2 = ((double) ((long) track.getSamples().size())) / ((double) refSampleCount3);
                    for (int i4 = 0; i4 < syncSamples2.length; i4++) {
                        syncSamples2[i4] = ((long) Math.ceil(((double) (refSyncSamples2[i4] - 1)) * stretch2)) + 1;
                    }
                    return syncSamples2;
                }
            }
            throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
        }
    }

    public static List<long[]> getSyncSamplesTimestamps(Movie movie2, Track track) {
        long[] currentTrackSyncSamples;
        List<long[]> times = new LinkedList<>();
        for (Track currentTrack : movie2.getTracks()) {
            if (currentTrack.getHandler().equals(track.getHandler()) && (currentTrackSyncSamples = currentTrack.getSyncSamples()) != null && currentTrackSyncSamples.length > 0) {
                times.add(getTimes(currentTrack, movie2));
            }
        }
        return times;
    }

    public long[] getCommonIndices(long[] syncSamples, long[] syncSampleTimes, long timeScale, long[]... otherTracksTimes) {
        char c;
        List<Long> finalSampleList;
        long[] jArr = syncSamples;
        long[] jArr2 = syncSampleTimes;
        long[][] jArr3 = otherTracksTimes;
        List<Long> nuSyncSamples = new LinkedList<>();
        List<Long> nuSyncSampleTimes = new LinkedList<>();
        int i = 0;
        while (true) {
            c = 0;
            if (i >= jArr2.length) {
                break;
            }
            boolean foundInEveryRef = true;
            int length = jArr3.length;
            for (int i2 = 0; i2 < length; i2++) {
                foundInEveryRef &= Arrays.binarySearch(jArr3[i2], jArr2[i]) >= 0;
            }
            if (foundInEveryRef) {
                nuSyncSamples.add(Long.valueOf(jArr[i]));
                nuSyncSampleTimes.add(Long.valueOf(jArr2[i]));
            }
            i++;
        }
        if (((double) nuSyncSamples.size()) < ((double) jArr.length) * 0.25d) {
            String log = String.valueOf("") + String.format("%5d - Common:  [", new Object[]{Integer.valueOf(nuSyncSamples.size())});
            for (Long longValue : nuSyncSamples) {
                long l = longValue.longValue();
                log = String.valueOf(log) + String.format("%10d,", new Object[]{Long.valueOf(l)});
                c = 0;
            }
            LOG.warning(String.valueOf(log) + "]");
            StringBuilder sb = new StringBuilder(String.valueOf(""));
            Object[] objArr = new Object[1];
            objArr[c] = Integer.valueOf(jArr.length);
            sb.append(String.format("%5d - In    :  [", objArr));
            String log2 = sb.toString();
            for (long l2 : jArr) {
                log2 = String.valueOf(log2) + String.format("%10d,", new Object[]{Long.valueOf(l2)});
            }
            LOG.warning(String.valueOf(log2) + "]");
            LOG.warning("There are less than 25% of common sync samples in the given track.");
            throw new RuntimeException("There are less than 25% of common sync samples in the given track.");
        }
        if (((double) nuSyncSamples.size()) < ((double) jArr.length) * 0.5d) {
            LOG.fine("There are less than 50% of common sync samples in the given track. This is implausible but I'm ok to continue.");
        } else if (nuSyncSamples.size() < jArr.length) {
            LOG.finest("Common SyncSample positions vs. this tracks SyncSample positions: " + nuSyncSamples.size() + " vs. " + jArr.length);
        }
        List<Long> finalSampleList2 = new LinkedList<>();
        if (this.minFragmentDurationSeconds > 0) {
            long lastSyncSampleTime = -1;
            Iterator<Long> nuSyncSamplesIterator = nuSyncSamples.iterator();
            Iterator<Long> nuSyncSampleTimesIterator = nuSyncSampleTimes.iterator();
            while (true) {
                if (!nuSyncSamplesIterator.hasNext()) {
                    long j = lastSyncSampleTime;
                    break;
                } else if (!nuSyncSampleTimesIterator.hasNext()) {
                    break;
                } else {
                    long curSyncSample = nuSyncSamplesIterator.next().longValue();
                    long curSyncSampleTime = nuSyncSampleTimesIterator.next().longValue();
                    if (lastSyncSampleTime != -1) {
                        long lastSyncSampleTime2 = lastSyncSampleTime;
                        if ((curSyncSampleTime - lastSyncSampleTime) / timeScale < ((long) this.minFragmentDurationSeconds)) {
                            lastSyncSampleTime = lastSyncSampleTime2;
                        }
                    }
                    finalSampleList2.add(Long.valueOf(curSyncSample));
                    lastSyncSampleTime = curSyncSampleTime;
                }
            }
            finalSampleList = finalSampleList2;
        } else {
            finalSampleList = nuSyncSamples;
        }
        long[] finalSampleArray = new long[finalSampleList.size()];
        for (int i3 = 0; i3 < finalSampleArray.length; i3++) {
            finalSampleArray[i3] = finalSampleList.get(i3).longValue();
        }
        return finalSampleArray;
    }

    private static long[] getTimes(Track track, Movie m) {
        long[] syncSamples = track.getSyncSamples();
        long[] syncSampleTimes = new long[syncSamples.length];
        long currentDuration = 0;
        int currentSyncSampleIndex = 0;
        long scalingFactor = calculateTracktimesScalingFactor(m, track);
        for (int currentSample = 1; ((long) currentSample) <= syncSamples[syncSamples.length - 1]; currentSample++) {
            if (((long) currentSample) == syncSamples[currentSyncSampleIndex]) {
                syncSampleTimes[currentSyncSampleIndex] = currentDuration * scalingFactor;
                currentSyncSampleIndex++;
            }
            currentDuration += track.getSampleDurations()[currentSample - 1];
        }
        return syncSampleTimes;
    }

    private static long calculateTracktimesScalingFactor(Movie m, Track track) {
        long timeScale = 1;
        for (Track track1 : m.getTracks()) {
            if (track1.getHandler().equals(track.getHandler()) && track1.getTrackMetaData().getTimescale() != track.getTrackMetaData().getTimescale()) {
                timeScale = Math.lcm(timeScale, track1.getTrackMetaData().getTimescale());
            }
        }
        return timeScale;
    }
}
