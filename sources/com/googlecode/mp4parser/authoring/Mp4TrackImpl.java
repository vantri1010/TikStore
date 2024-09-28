package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.SampleFlags;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.coremedia.iso.boxes.mdat.SampleList;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mp4TrackImpl extends AbstractTrack {
    private List<CompositionTimeToSample.Entry> compositionTimeEntries;
    private long[] decodingTimes;
    IsoFile[] fragments;
    private String handler;
    private List<SampleDependencyTypeBox.Entry> sampleDependencies;
    private SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    private SubSampleInformationBox subSampleInformationBox = null;
    private long[] syncSamples;
    TrackBox trackBox;
    private TrackMetaData trackMetaData;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public Mp4TrackImpl(String name, TrackBox trackBox2, IsoFile... fragments2) {
        super(name);
        MovieFragmentBox movieFragmentBox;
        Iterator<TrackFragmentBox> it;
        List<TrackFragmentBox> trafs;
        String str;
        List<MovieFragmentBox> movieFragmentBoxes;
        TrackFragmentBox traf;
        Iterator<TrackRunBox> it2;
        Iterator<TrackExtendsBox> it3;
        SampleFlags sampleFlags;
        long j;
        MovieFragmentBox movieFragmentBox2;
        SubSampleInformationBox.SubSampleEntry se;
        Mp4TrackImpl mp4TrackImpl = this;
        TrackBox trackBox3 = trackBox2;
        IsoFile[] isoFileArr = fragments2;
        mp4TrackImpl.syncSamples = new long[0];
        mp4TrackImpl.trackMetaData = new TrackMetaData();
        long trackId = trackBox2.getTrackHeaderBox().getTrackId();
        mp4TrackImpl.samples = new SampleList(trackBox3, isoFileArr);
        SampleTableBox stbl = trackBox2.getMediaBox().getMediaInformationBox().getSampleTableBox();
        mp4TrackImpl.handler = trackBox2.getMediaBox().getHandlerBox().getHandlerType();
        List<TimeToSampleBox.Entry> decodingTimeEntries = new ArrayList<>();
        mp4TrackImpl.compositionTimeEntries = new ArrayList();
        mp4TrackImpl.sampleDependencies = new ArrayList();
        decodingTimeEntries.addAll(stbl.getTimeToSampleBox().getEntries());
        if (stbl.getCompositionTimeToSample() != null) {
            mp4TrackImpl.compositionTimeEntries.addAll(stbl.getCompositionTimeToSample().getEntries());
        }
        if (stbl.getSampleDependencyTypeBox() != null) {
            mp4TrackImpl.sampleDependencies.addAll(stbl.getSampleDependencyTypeBox().getEntries());
        }
        if (stbl.getSyncSampleBox() != null) {
            mp4TrackImpl.syncSamples = stbl.getSyncSampleBox().getSampleNumber();
        }
        String str2 = SubSampleInformationBox.TYPE;
        mp4TrackImpl.subSampleInformationBox = (SubSampleInformationBox) Path.getPath((AbstractContainerBox) stbl, str2);
        List<MovieFragmentBox> movieFragmentBoxes2 = new ArrayList<>();
        movieFragmentBoxes2.addAll(((Box) trackBox2.getParent()).getParent().getBoxes(MovieFragmentBox.class));
        int length = isoFileArr.length;
        int i = 0;
        while (i < length) {
            String str3 = str2;
            List<MovieFragmentBox> movieFragmentBoxes3 = movieFragmentBoxes2;
            movieFragmentBoxes3.addAll(isoFileArr[i].getBoxes(MovieFragmentBox.class));
            i++;
            mp4TrackImpl = this;
            trackBox3 = trackBox2;
            movieFragmentBoxes2 = movieFragmentBoxes3;
        }
        mp4TrackImpl.sampleDescriptionBox = stbl.getSampleDescriptionBox();
        List<Long> syncSampleList = trackBox2.getParent().getBoxes(MovieExtendsBox.class);
        if (syncSampleList.size() > 0) {
            Iterator<Long> iterator = syncSampleList.iterator();
            while (iterator.hasNext()) {
                MovieExtendsBox mvex = (MovieExtendsBox) iterator.next();
                List<TrackExtendsBox> trackExtendsBoxes = mvex.getBoxes(TrackExtendsBox.class);
                Iterator<TrackExtendsBox> it4 = trackExtendsBoxes.iterator();
                while (it4.hasNext()) {
                    TrackExtendsBox trex = it4.next();
                    if (trex.getTrackId() == trackId) {
                        List<Long> movieExtendsBoxes = syncSampleList;
                        List<SubSampleInformationBox> subss = Path.getPaths(((Box) trackBox2.getParent()).getParent(), "/moof/traf/subs");
                        if (subss.size() > 0) {
                            mp4TrackImpl.subSampleInformationBox = new SubSampleInformationBox();
                        }
                        List<Long> syncSampleList2 = new LinkedList<>();
                        long sampleNumber = 1;
                        Iterator<MovieFragmentBox> it5 = movieFragmentBoxes2.iterator();
                        while (it5.hasNext()) {
                            List<SubSampleInformationBox> subss2 = subss;
                            Iterator<Long> it6 = iterator;
                            MovieExtendsBox mvex2 = mvex;
                            List<TrackExtendsBox> trackExtendsBoxes2 = trackExtendsBoxes;
                            MovieFragmentBox movieFragmentBox3 = it5.next();
                            List<TrackFragmentBox> trafs2 = movieFragmentBox3.getBoxes(TrackFragmentBox.class);
                            Iterator<TrackFragmentBox> it7 = trafs2.iterator();
                            while (it7.hasNext()) {
                                TrackFragmentBox traf2 = it7.next();
                                if (traf2.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                                    SubSampleInformationBox subs = (SubSampleInformationBox) Path.getPath((AbstractContainerBox) traf2, str2);
                                    long trackId2 = trackId;
                                    MovieFragmentBox movieFragmentBox4 = movieFragmentBox3;
                                    if (subs != null) {
                                        trafs = trafs2;
                                        it = it7;
                                        long difFromLastFragment = (sampleNumber - ((long) 0)) - 1;
                                        for (SubSampleInformationBox.SubSampleEntry subSampleEntry : subs.getEntries()) {
                                            SubSampleInformationBox.SubSampleEntry se2 = new SubSampleInformationBox.SubSampleEntry();
                                            se2.getSubsampleEntries().addAll(subSampleEntry.getSubsampleEntries());
                                            if (difFromLastFragment != 0) {
                                                movieFragmentBox2 = movieFragmentBox4;
                                                se = se2;
                                                se.setSampleDelta(subSampleEntry.getSampleDelta() + difFromLastFragment);
                                                difFromLastFragment = 0;
                                            } else {
                                                movieFragmentBox2 = movieFragmentBox4;
                                                se = se2;
                                                se.setSampleDelta(subSampleEntry.getSampleDelta());
                                            }
                                            mp4TrackImpl.subSampleInformationBox.getEntries().add(se);
                                            movieFragmentBox4 = movieFragmentBox2;
                                        }
                                        movieFragmentBox = movieFragmentBox4;
                                    } else {
                                        movieFragmentBox = movieFragmentBox4;
                                        trafs = trafs2;
                                        it = it7;
                                    }
                                    List<TrackRunBox> truns = traf2.getBoxes(TrackRunBox.class);
                                    Iterator<TrackRunBox> it8 = truns.iterator();
                                    while (it8.hasNext()) {
                                        TrackRunBox trun = it8.next();
                                        TrackFragmentHeaderBox tfhd = ((TrackFragmentBox) trun.getParent()).getTrackFragmentHeaderBox();
                                        boolean first = true;
                                        for (TrackRunBox.Entry entry : trun.getEntries()) {
                                            List<TrackRunBox> truns2 = truns;
                                            if (trun.isSampleDurationPresent()) {
                                                if (decodingTimeEntries.size() != 0) {
                                                    it2 = it8;
                                                    if (decodingTimeEntries.get(decodingTimeEntries.size() - 1).getDelta() != entry.getSampleDuration()) {
                                                        str = str2;
                                                        movieFragmentBoxes = movieFragmentBoxes2;
                                                        traf = traf2;
                                                        it3 = it4;
                                                        j = 1;
                                                    } else {
                                                        TimeToSampleBox.Entry e = decodingTimeEntries.get(decodingTimeEntries.size() - 1);
                                                        str = str2;
                                                        movieFragmentBoxes = movieFragmentBoxes2;
                                                        traf = traf2;
                                                        it3 = it4;
                                                        e.setCount(e.getCount() + 1);
                                                    }
                                                } else {
                                                    it2 = it8;
                                                    str = str2;
                                                    movieFragmentBoxes = movieFragmentBoxes2;
                                                    traf = traf2;
                                                    it3 = it4;
                                                    j = 1;
                                                }
                                                decodingTimeEntries.add(new TimeToSampleBox.Entry(j, entry.getSampleDuration()));
                                            } else {
                                                it2 = it8;
                                                str = str2;
                                                movieFragmentBoxes = movieFragmentBoxes2;
                                                traf = traf2;
                                                it3 = it4;
                                                if (tfhd.hasDefaultSampleDuration()) {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1, tfhd.getDefaultSampleDuration()));
                                                } else {
                                                    decodingTimeEntries.add(new TimeToSampleBox.Entry(1, trex.getDefaultSampleDuration()));
                                                }
                                            }
                                            if (trun.isSampleCompositionTimeOffsetPresent()) {
                                                if (mp4TrackImpl.compositionTimeEntries.size() != 0) {
                                                    List<CompositionTimeToSample.Entry> list = mp4TrackImpl.compositionTimeEntries;
                                                    if (((long) list.get(list.size() - 1).getOffset()) == entry.getSampleCompositionTimeOffset()) {
                                                        List<CompositionTimeToSample.Entry> list2 = mp4TrackImpl.compositionTimeEntries;
                                                        CompositionTimeToSample.Entry e2 = list2.get(list2.size() - 1);
                                                        e2.setCount(e2.getCount() + 1);
                                                    }
                                                }
                                                mp4TrackImpl.compositionTimeEntries.add(new CompositionTimeToSample.Entry(1, CastUtils.l2i(entry.getSampleCompositionTimeOffset())));
                                            }
                                            if (trun.isSampleFlagsPresent()) {
                                                sampleFlags = entry.getSampleFlags();
                                            } else if (first && trun.isFirstSampleFlagsPresent()) {
                                                sampleFlags = trun.getFirstSampleFlags();
                                            } else if (tfhd.hasDefaultSampleFlags()) {
                                                sampleFlags = tfhd.getDefaultSampleFlags();
                                            } else {
                                                sampleFlags = trex.getDefaultSampleFlags();
                                            }
                                            if (sampleFlags != null && !sampleFlags.isSampleIsDifferenceSample()) {
                                                syncSampleList2.add(Long.valueOf(sampleNumber));
                                            }
                                            sampleNumber++;
                                            first = false;
                                            it4 = it3;
                                            truns = truns2;
                                            it8 = it2;
                                            traf2 = traf;
                                            movieFragmentBoxes2 = movieFragmentBoxes;
                                            str2 = str;
                                        }
                                    }
                                    trackId = trackId2;
                                    trafs2 = trafs;
                                    it7 = it;
                                    movieFragmentBox3 = movieFragmentBox;
                                } else {
                                    long j2 = trackId;
                                    String str4 = str2;
                                    List<MovieFragmentBox> list3 = movieFragmentBoxes2;
                                    List<TrackFragmentBox> list4 = trafs2;
                                    Iterator<TrackFragmentBox> it9 = it7;
                                    TrackFragmentBox trackFragmentBox = traf2;
                                    Iterator<TrackExtendsBox> it10 = it4;
                                }
                            }
                            subss = subss2;
                            iterator = it6;
                            mvex = mvex2;
                            trackExtendsBoxes = trackExtendsBoxes2;
                        }
                        List<SubSampleInformationBox> list5 = subss;
                        long[] oldSS = mp4TrackImpl.syncSamples;
                        Iterator<Long> it11 = iterator;
                        long[] jArr = new long[(mp4TrackImpl.syncSamples.length + syncSampleList2.size())];
                        mp4TrackImpl.syncSamples = jArr;
                        MovieExtendsBox mvex3 = mvex;
                        List<TrackExtendsBox> trackExtendsBoxes3 = trackExtendsBoxes;
                        System.arraycopy(oldSS, 0, jArr, 0, oldSS.length);
                        int i2 = oldSS.length;
                        for (Long syncSampleNumber : syncSampleList2) {
                            mp4TrackImpl.syncSamples[i2] = syncSampleNumber.longValue();
                            i2++;
                        }
                        syncSampleList = movieExtendsBoxes;
                        iterator = it11;
                        mvex = mvex3;
                        trackExtendsBoxes = trackExtendsBoxes3;
                    } else {
                        String str5 = str2;
                        List<MovieFragmentBox> list6 = movieFragmentBoxes2;
                        List<Long> list7 = syncSampleList;
                        Iterator<Long> it12 = iterator;
                        MovieExtendsBox movieExtendsBox = mvex;
                        List<TrackExtendsBox> list8 = trackExtendsBoxes;
                        Iterator<TrackExtendsBox> it13 = it4;
                    }
                }
            }
            new ArrayList();
            new ArrayList();
            for (MovieFragmentBox movieFragmentBox5 : movieFragmentBoxes2) {
                for (TrackFragmentBox traf3 : movieFragmentBox5.getBoxes(TrackFragmentBox.class)) {
                    if (traf3.getTrackFragmentHeaderBox().getTrackId() == trackId) {
                        TrackFragmentBox trackFragmentBox2 = traf3;
                        mp4TrackImpl.sampleGroups = mp4TrackImpl.getSampleGroups(Path.getPaths((Container) traf3, SampleGroupDescriptionBox.TYPE), Path.getPaths((Container) traf3, SampleToGroupBox.TYPE), mp4TrackImpl.sampleGroups);
                    }
                }
            }
            long j3 = trackId;
            List<MovieFragmentBox> list9 = movieFragmentBoxes2;
            List<Long> list10 = syncSampleList;
        } else {
            List<MovieFragmentBox> list11 = movieFragmentBoxes2;
            List<Long> list12 = syncSampleList;
            mp4TrackImpl.sampleGroups = mp4TrackImpl.getSampleGroups(stbl.getBoxes(SampleGroupDescriptionBox.class), stbl.getBoxes(SampleToGroupBox.class), mp4TrackImpl.sampleGroups);
        }
        mp4TrackImpl.decodingTimes = TimeToSampleBox.blowupTimeToSamples(decodingTimeEntries);
        MediaHeaderBox mdhd = trackBox2.getMediaBox().getMediaHeaderBox();
        TrackHeaderBox tkhd = trackBox2.getTrackHeaderBox();
        mp4TrackImpl.trackMetaData.setTrackId(tkhd.getTrackId());
        mp4TrackImpl.trackMetaData.setCreationTime(mdhd.getCreationTime());
        mp4TrackImpl.trackMetaData.setLanguage(mdhd.getLanguage());
        mp4TrackImpl.trackMetaData.setModificationTime(mdhd.getModificationTime());
        mp4TrackImpl.trackMetaData.setTimescale(mdhd.getTimescale());
        mp4TrackImpl.trackMetaData.setHeight(tkhd.getHeight());
        mp4TrackImpl.trackMetaData.setWidth(tkhd.getWidth());
        mp4TrackImpl.trackMetaData.setLayer(tkhd.getLayer());
        mp4TrackImpl.trackMetaData.setMatrix(tkhd.getMatrix());
        EditListBox elst = (EditListBox) Path.getPath((AbstractContainerBox) trackBox3, "edts/elst");
        MovieHeaderBox mvhd = (MovieHeaderBox) Path.getPath((AbstractContainerBox) trackBox3, "../mvhd");
        if (elst != null) {
            for (EditListBox.Entry e3 : elst.getEntries()) {
                mp4TrackImpl.edits.add(new Edit(e3.getMediaTime(), mdhd.getTimescale(), e3.getMediaRate(), ((double) e3.getSegmentDuration()) / ((double) mvhd.getTimescale())));
                mp4TrackImpl = this;
                TrackBox trackBox4 = trackBox2;
            }
        }
    }

    private Map<GroupEntry, long[]> getSampleGroups(List<SampleGroupDescriptionBox> sgdbs, List<SampleToGroupBox> sbgps, Map<GroupEntry, long[]> sampleGroups) {
        Map<GroupEntry, long[]> map = sampleGroups;
        for (SampleGroupDescriptionBox sgdb : sgdbs) {
            boolean sampleNum = false;
            Iterator<SampleToGroupBox> it = sbgps.iterator();
            while (true) {
                int i = 0;
                if (it.hasNext()) {
                    SampleToGroupBox sbgp = it.next();
                    if (sbgp.getGroupingType().equals(sgdb.getGroupEntries().get(0).getType())) {
                        boolean found = true;
                        int sampleNum2 = 0;
                        for (SampleToGroupBox.Entry entry : sbgp.getEntries()) {
                            if (entry.getGroupDescriptionIndex() > 0) {
                                GroupEntry groupEntry = sgdb.getGroupEntries().get(entry.getGroupDescriptionIndex() - 1);
                                long[] samples2 = map.get(groupEntry);
                                if (samples2 == null) {
                                    samples2 = new long[i];
                                }
                                long[] nuSamples = new long[(CastUtils.l2i(entry.getSampleCount()) + samples2.length)];
                                System.arraycopy(samples2, i, nuSamples, i, samples2.length);
                                int i2 = 0;
                                while (((long) i2) < entry.getSampleCount()) {
                                    nuSamples[samples2.length + i2] = (long) (sampleNum2 + i2);
                                    i2++;
                                    found = found;
                                }
                                map.put(groupEntry, nuSamples);
                            }
                            sampleNum2 = (int) (((long) sampleNum2) + entry.getSampleCount());
                            found = found;
                            i = 0;
                        }
                        sampleNum = found;
                    }
                } else if (!sampleNum) {
                    throw new RuntimeException("Could not find SampleToGroupBox for " + sgdb.getGroupEntries().get(0).getType() + ".");
                }
            }
        }
        return map;
    }

    public void close() throws IOException {
        Container c = this.trackBox.getParent();
        if (c instanceof BasicContainer) {
            ((BasicContainer) c).close();
        }
        for (IsoFile fragment : this.fragments) {
            fragment.close();
        }
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public synchronized long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.compositionTimeEntries;
    }

    public long[] getSyncSamples() {
        if (this.syncSamples.length == this.samples.size()) {
            return null;
        }
        return this.syncSamples;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.sampleDependencies;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return this.handler;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.subSampleInformationBox;
    }
}
