package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.HintMediaHeaderBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.NullMediaHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SubtitleMediaHeaderBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.TrackReferenceTypeBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.googlecode.mp4parser.boxes.dece.SampleEncryptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleToGroupBox;
import com.googlecode.mp4parser.util.CastUtils;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultMp4Builder implements Mp4Builder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static Logger LOG = Logger.getLogger(DefaultMp4Builder.class.getName());
    Set<StaticChunkOffsetBox> chunkOffsetBoxes = new HashSet();
    private FragmentIntersectionFinder intersectionFinder;
    Set<SampleAuxiliaryInformationOffsetsBox> sampleAuxiliaryInformationOffsetsBoxes = new HashSet();
    HashMap<Track, List<Sample>> track2Sample = new HashMap<>();
    HashMap<Track, long[]> track2SampleSizes = new HashMap<>();

    private static long sum(int[] ls) {
        long rc = 0;
        for (int i : ls) {
            rc += (long) i;
        }
        return rc;
    }

    private static long sum(long[] ls) {
        long rc = 0;
        for (long l : ls) {
            rc += l;
        }
        return rc;
    }

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public void setIntersectionFinder(FragmentIntersectionFinder intersectionFinder2) {
        this.intersectionFinder = intersectionFinder2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: com.coremedia.iso.boxes.Container} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: com.coremedia.iso.boxes.Container} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.coremedia.iso.boxes.Container build(com.googlecode.mp4parser.authoring.Movie r23) {
        /*
            r22 = this;
            r7 = r22
            r8 = r23
            com.googlecode.mp4parser.authoring.builder.FragmentIntersectionFinder r0 = r7.intersectionFinder
            if (r0 != 0) goto L_0x0010
            com.googlecode.mp4parser.authoring.builder.TwoSecondIntersectionFinder r0 = new com.googlecode.mp4parser.authoring.builder.TwoSecondIntersectionFinder
            r1 = 2
            r0.<init>(r8, r1)
            r7.intersectionFinder = r0
        L_0x0010:
            java.util.logging.Logger r0 = LOG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "Creating movie "
            r1.<init>(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r0.fine(r1)
            java.util.List r0 = r23.getTracks()
            java.util.Iterator r0 = r0.iterator()
        L_0x002b:
            boolean r1 = r0.hasNext()
            if (r1 != 0) goto L_0x013f
            com.googlecode.mp4parser.BasicContainer r0 = new com.googlecode.mp4parser.BasicContainer
            r0.<init>()
            r9 = r0
            com.coremedia.iso.boxes.FileTypeBox r0 = r22.createFileTypeBox(r23)
            r9.addBox(r0)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r10 = r0
            java.util.List r0 = r23.getTracks()
            java.util.Iterator r1 = r0.iterator()
        L_0x004c:
            boolean r0 = r1.hasNext()
            if (r0 != 0) goto L_0x0130
            com.coremedia.iso.boxes.MovieBox r11 = r7.createMovieBox(r8, r10)
            r9.addBox(r11)
            java.lang.String r0 = "trak/mdia/minf/stbl/stsz"
            java.util.List r12 = com.googlecode.mp4parser.util.Path.getPaths((com.coremedia.iso.boxes.Box) r11, (java.lang.String) r0)
            r0 = 0
            java.util.Iterator r2 = r12.iterator()
            r13 = r0
        L_0x0066:
            boolean r0 = r2.hasNext()
            if (r0 != 0) goto L_0x011f
            com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder$InterleaveChunkMdat r15 = new com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder$InterleaveChunkMdat
            r6 = 0
            r0 = r15
            r1 = r22
            r2 = r23
            r3 = r10
            r4 = r13
            r0.<init>(r1, r2, r3, r4, r6)
            r9.addBox(r0)
            long r3 = r0.getDataOffset()
            java.util.Set<com.coremedia.iso.boxes.StaticChunkOffsetBox> r1 = r7.chunkOffsetBoxes
            java.util.Iterator r1 = r1.iterator()
        L_0x0086:
            boolean r2 = r1.hasNext()
            if (r2 != 0) goto L_0x0103
            java.util.Set<com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox> r1 = r7.sampleAuxiliaryInformationOffsetsBoxes
            java.util.Iterator r2 = r1.iterator()
        L_0x0092:
            boolean r1 = r2.hasNext()
            if (r1 != 0) goto L_0x0099
            return r9
        L_0x0099:
            java.lang.Object r1 = r2.next()
            r5 = r1
            com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox r5 = (com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox) r5
            long r15 = r5.getSize()
            r17 = 44
            long r15 = r15 + r17
            r1 = r5
        L_0x00a9:
            r6 = r1
            r17 = r1
            com.coremedia.iso.boxes.Box r17 = (com.coremedia.iso.boxes.Box) r17
            com.coremedia.iso.boxes.Container r1 = r17.getParent()
            r17 = r1
            com.coremedia.iso.boxes.Container r17 = (com.coremedia.iso.boxes.Container) r17
            java.util.List r17 = r17.getBoxes()
            java.util.Iterator r17 = r17.iterator()
        L_0x00be:
            boolean r18 = r17.hasNext()
            if (r18 != 0) goto L_0x00c7
            r19 = r0
            goto L_0x00d4
        L_0x00c7:
            java.lang.Object r18 = r17.next()
            r19 = r0
            r0 = r18
            com.coremedia.iso.boxes.Box r0 = (com.coremedia.iso.boxes.Box) r0
            if (r0 != r6) goto L_0x00f8
        L_0x00d4:
            boolean r0 = r1 instanceof com.coremedia.iso.boxes.Box
            if (r0 != 0) goto L_0x00f3
            long[] r0 = r5.getOffsets()
            r6 = 0
        L_0x00dd:
            r18 = r1
            int r1 = r0.length
            if (r6 < r1) goto L_0x00e8
            r5.setOffsets(r0)
            r0 = r19
            goto L_0x0092
        L_0x00e8:
            r20 = r0[r6]
            long r20 = r20 + r15
            r0[r6] = r20
            int r6 = r6 + 1
            r1 = r18
            goto L_0x00dd
        L_0x00f3:
            r18 = r1
            r0 = r19
            goto L_0x00a9
        L_0x00f8:
            r18 = r1
            long r20 = r0.getSize()
            long r15 = r15 + r20
            r0 = r19
            goto L_0x00be
        L_0x0103:
            r19 = r0
            java.lang.Object r0 = r1.next()
            com.coremedia.iso.boxes.StaticChunkOffsetBox r0 = (com.coremedia.iso.boxes.StaticChunkOffsetBox) r0
            long[] r5 = r0.getChunkOffsets()
            r2 = 0
        L_0x0110:
            int r6 = r5.length
            if (r2 < r6) goto L_0x0117
            r0 = r19
            goto L_0x0086
        L_0x0117:
            r15 = r5[r2]
            long r15 = r15 + r3
            r5[r2] = r15
            int r2 = r2 + 1
            goto L_0x0110
        L_0x011f:
            java.lang.Object r0 = r2.next()
            com.coremedia.iso.boxes.SampleSizeBox r0 = (com.coremedia.iso.boxes.SampleSizeBox) r0
            long[] r1 = r0.getSampleSizes()
            long r3 = sum((long[]) r1)
            long r13 = r13 + r3
            goto L_0x0066
        L_0x0130:
            java.lang.Object r0 = r1.next()
            com.googlecode.mp4parser.authoring.Track r0 = (com.googlecode.mp4parser.authoring.Track) r0
            int[] r2 = r7.getChunkSizes(r0, r8)
            r10.put(r0, r2)
            goto L_0x004c
        L_0x013f:
            java.lang.Object r1 = r0.next()
            com.googlecode.mp4parser.authoring.Track r1 = (com.googlecode.mp4parser.authoring.Track) r1
            java.util.List r2 = r1.getSamples()
            r7.putSamples(r1, r2)
            int r3 = r2.size()
            long[] r3 = new long[r3]
            r4 = 0
        L_0x0153:
            int r5 = r3.length
            if (r4 < r5) goto L_0x015d
            java.util.HashMap<com.googlecode.mp4parser.authoring.Track, long[]> r4 = r7.track2SampleSizes
            r4.put(r1, r3)
            goto L_0x002b
        L_0x015d:
            java.lang.Object r5 = r2.get(r4)
            com.googlecode.mp4parser.authoring.Sample r5 = (com.googlecode.mp4parser.authoring.Sample) r5
            long r9 = r5.getSize()
            r3[r4] = r9
            int r4 = r4 + 1
            goto L_0x0153
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder.build(com.googlecode.mp4parser.authoring.Movie):com.coremedia.iso.boxes.Container");
    }

    /* access modifiers changed from: protected */
    public List<Sample> putSamples(Track track, List<Sample> samples) {
        return this.track2Sample.put(track, samples);
    }

    /* access modifiers changed from: protected */
    public FileTypeBox createFileTypeBox(Movie movie) {
        List<String> minorBrands = new LinkedList<>();
        minorBrands.add("isom");
        minorBrands.add("iso2");
        minorBrands.add(VisualSampleEntry.TYPE3);
        return new FileTypeBox("isom", 0, minorBrands);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x011c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.coremedia.iso.boxes.MovieBox createMovieBox(com.googlecode.mp4parser.authoring.Movie r20, java.util.Map<com.googlecode.mp4parser.authoring.Track, int[]> r21) {
        /*
            r19 = this;
            com.coremedia.iso.boxes.MovieBox r0 = new com.coremedia.iso.boxes.MovieBox
            r0.<init>()
            com.coremedia.iso.boxes.MovieHeaderBox r1 = new com.coremedia.iso.boxes.MovieHeaderBox
            r1.<init>()
            java.util.Date r2 = new java.util.Date
            r2.<init>()
            r1.setCreationTime(r2)
            java.util.Date r2 = new java.util.Date
            r2.<init>()
            r1.setModificationTime(r2)
            com.googlecode.mp4parser.util.Matrix r2 = r20.getMatrix()
            r1.setMatrix(r2)
            long r2 = r19.getTimescale(r20)
            r4 = 0
            java.util.List r6 = r20.getTracks()
            java.util.Iterator r6 = r6.iterator()
        L_0x002f:
            boolean r7 = r6.hasNext()
            if (r7 != 0) goto L_0x00a5
            r1.setDuration(r4)
            r1.setTimescale(r2)
            r6 = 0
            java.util.List r8 = r20.getTracks()
            java.util.Iterator r8 = r8.iterator()
        L_0x0045:
            boolean r9 = r8.hasNext()
            if (r9 != 0) goto L_0x0081
            r8 = 1
            long r8 = r8 + r6
            r10 = r8
            r1.setNextTrackId(r8)
            r0.addBox(r1)
            java.util.List r6 = r20.getTracks()
            java.util.Iterator r9 = r6.iterator()
        L_0x005d:
            boolean r6 = r9.hasNext()
            if (r6 != 0) goto L_0x006d
            com.coremedia.iso.boxes.Box r6 = r19.createUdta(r20)
            if (r6 == 0) goto L_0x006c
            r0.addBox(r6)
        L_0x006c:
            return r0
        L_0x006d:
            java.lang.Object r6 = r9.next()
            com.googlecode.mp4parser.authoring.Track r6 = (com.googlecode.mp4parser.authoring.Track) r6
            r12 = r19
            r13 = r20
            r14 = r21
            com.coremedia.iso.boxes.TrackBox r7 = r12.createTrackBox(r6, r13, r14)
            r0.addBox(r7)
            goto L_0x005d
        L_0x0081:
            r12 = r19
            r13 = r20
            r14 = r21
            java.lang.Object r9 = r8.next()
            com.googlecode.mp4parser.authoring.Track r9 = (com.googlecode.mp4parser.authoring.Track) r9
            com.googlecode.mp4parser.authoring.TrackMetaData r10 = r9.getTrackMetaData()
            long r10 = r10.getTrackId()
            int r15 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r15 >= 0) goto L_0x00a2
            com.googlecode.mp4parser.authoring.TrackMetaData r10 = r9.getTrackMetaData()
            long r10 = r10.getTrackId()
            goto L_0x00a3
        L_0x00a2:
            r10 = r6
        L_0x00a3:
            r6 = r10
            goto L_0x0045
        L_0x00a5:
            r12 = r19
            r13 = r20
            r14 = r21
            java.lang.Object r7 = r6.next()
            com.googlecode.mp4parser.authoring.Track r7 = (com.googlecode.mp4parser.authoring.Track) r7
            r8 = 0
            java.util.List r10 = r7.getEdits()
            if (r10 == 0) goto L_0x00f8
            java.util.List r10 = r7.getEdits()
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x00c8
            r17 = r0
            r18 = r1
            goto L_0x00fc
        L_0x00c8:
            r10 = 0
            java.util.List r15 = r7.getEdits()
            java.util.Iterator r15 = r15.iterator()
        L_0x00d2:
            boolean r16 = r15.hasNext()
            if (r16 != 0) goto L_0x00e3
            long r15 = r19.getTimescale(r20)
            long r15 = r15 * r10
            r17 = r0
            r18 = r1
            goto L_0x0111
        L_0x00e3:
            java.lang.Object r16 = r15.next()
            com.googlecode.mp4parser.authoring.Edit r16 = (com.googlecode.mp4parser.authoring.Edit) r16
            r17 = r0
            r18 = r1
            double r0 = r16.getSegmentDuration()
            long r0 = (long) r0
            long r10 = r10 + r0
            r0 = r17
            r1 = r18
            goto L_0x00d2
        L_0x00f8:
            r17 = r0
            r18 = r1
        L_0x00fc:
            long r0 = r7.getDuration()
            long r10 = r19.getTimescale(r20)
            long r0 = r0 * r10
            com.googlecode.mp4parser.authoring.TrackMetaData r10 = r7.getTrackMetaData()
            long r10 = r10.getTimescale()
            long r15 = r0 / r10
        L_0x0111:
            int r0 = (r15 > r4 ? 1 : (r15 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x011c
            r4 = r15
            r0 = r17
            r1 = r18
            goto L_0x002f
        L_0x011c:
            r0 = r17
            r1 = r18
            goto L_0x002f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder.createMovieBox(com.googlecode.mp4parser.authoring.Movie, java.util.Map):com.coremedia.iso.boxes.MovieBox");
    }

    /* access modifiers changed from: protected */
    public Box createUdta(Movie movie) {
        return null;
    }

    /* access modifiers changed from: protected */
    public TrackBox createTrackBox(Track track, Movie movie, Map<Track, int[]> chunks) {
        TrackBox trackBox = new TrackBox();
        TrackHeaderBox tkhd = new TrackHeaderBox();
        tkhd.setEnabled(true);
        tkhd.setInMovie(true);
        tkhd.setInPreview(true);
        tkhd.setInPoster(true);
        tkhd.setMatrix(track.getTrackMetaData().getMatrix());
        tkhd.setAlternateGroup(track.getTrackMetaData().getGroup());
        tkhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        if (track.getEdits() == null || track.getEdits().isEmpty()) {
            tkhd.setDuration((track.getDuration() * getTimescale(movie)) / track.getTrackMetaData().getTimescale());
        } else {
            long d = 0;
            for (Edit edit : track.getEdits()) {
                d += (long) edit.getSegmentDuration();
            }
            tkhd.setDuration(track.getTrackMetaData().getTimescale() * d);
        }
        tkhd.setHeight(track.getTrackMetaData().getHeight());
        tkhd.setWidth(track.getTrackMetaData().getWidth());
        tkhd.setLayer(track.getTrackMetaData().getLayer());
        tkhd.setModificationTime(new Date());
        tkhd.setTrackId(track.getTrackMetaData().getTrackId());
        tkhd.setVolume(track.getTrackMetaData().getVolume());
        trackBox.addBox(tkhd);
        trackBox.addBox(createEdts(track, movie));
        MediaBox mdia = new MediaBox();
        trackBox.addBox(mdia);
        MediaHeaderBox mdhd = new MediaHeaderBox();
        mdhd.setCreationTime(track.getTrackMetaData().getCreationTime());
        mdhd.setDuration(track.getDuration());
        mdhd.setTimescale(track.getTrackMetaData().getTimescale());
        mdhd.setLanguage(track.getTrackMetaData().getLanguage());
        mdia.addBox(mdhd);
        HandlerBox hdlr = new HandlerBox();
        mdia.addBox(hdlr);
        hdlr.setHandlerType(track.getHandler());
        MediaInformationBox minf = new MediaInformationBox();
        if (track.getHandler().equals("vide")) {
            minf.addBox(new VideoMediaHeaderBox());
        } else if (track.getHandler().equals("soun")) {
            minf.addBox(new SoundMediaHeaderBox());
        } else if (track.getHandler().equals("text")) {
            minf.addBox(new NullMediaHeaderBox());
        } else if (track.getHandler().equals("subt")) {
            minf.addBox(new SubtitleMediaHeaderBox());
        } else if (track.getHandler().equals(TrackReferenceTypeBox.TYPE1)) {
            minf.addBox(new HintMediaHeaderBox());
        } else if (track.getHandler().equals("sbtl")) {
            minf.addBox(new NullMediaHeaderBox());
        }
        DataInformationBox dinf = new DataInformationBox();
        DataReferenceBox dref = new DataReferenceBox();
        dinf.addBox(dref);
        DataEntryUrlBox url = new DataEntryUrlBox();
        url.setFlags(1);
        dref.addBox(url);
        minf.addBox(dinf);
        minf.addBox(createStbl(track, movie, chunks));
        mdia.addBox(minf);
        return trackBox;
    }

    /* access modifiers changed from: protected */
    public Box createEdts(Track track, Movie movie) {
        if (track.getEdits() == null || track.getEdits().size() <= 0) {
            return null;
        }
        EditListBox elst = new EditListBox();
        elst.setVersion(1);
        List<EditListBox.Entry> entries = new ArrayList<>();
        for (Edit edit : track.getEdits()) {
            entries.add(new EditListBox.Entry(elst, Math.round(edit.getSegmentDuration() * ((double) movie.getTimescale())), (edit.getMediaTime() * track.getTrackMetaData().getTimescale()) / edit.getTimeScale(), edit.getMediaRate()));
        }
        elst.setEntries(entries);
        EditBox edts = new EditBox();
        edts.addBox(elst);
        return edts;
    }

    /* access modifiers changed from: protected */
    public Box createStbl(Track track, Movie movie, Map<Track, int[]> chunks) {
        long j;
        DefaultMp4Builder defaultMp4Builder = this;
        Track track2 = track;
        Map<Track, int[]> map = chunks;
        SampleTableBox stbl = new SampleTableBox();
        defaultMp4Builder.createStsd(track2, stbl);
        defaultMp4Builder.createStts(track2, stbl);
        defaultMp4Builder.createCtts(track2, stbl);
        defaultMp4Builder.createStss(track2, stbl);
        defaultMp4Builder.createSdtp(track2, stbl);
        defaultMp4Builder.createStsc(track2, map, stbl);
        defaultMp4Builder.createStsz(track2, stbl);
        defaultMp4Builder.createStco(track2, movie, map, stbl);
        Map<String, List<GroupEntry>> groupEntryFamilies = new HashMap<>();
        for (Map.Entry<GroupEntry, long[]> sg : track.getSampleGroups().entrySet()) {
            SampleTableBox stbl2 = stbl;
            String type = sg.getKey().getType();
            List<GroupEntry> groupEntries = groupEntryFamilies.get(type);
            if (groupEntries == null) {
                groupEntries = new ArrayList<>();
                groupEntryFamilies.put(type, groupEntries);
            }
            groupEntries.add(sg.getKey());
            defaultMp4Builder = this;
            track2 = track;
            map = chunks;
            stbl = stbl2;
        }
        for (Map.Entry<String, List<GroupEntry>> sg2 : groupEntryFamilies.entrySet()) {
            SampleGroupDescriptionBox sgdb = new SampleGroupDescriptionBox();
            sgdb.setGroupEntries(sg2.getValue());
            SampleToGroupBox sbgp = new SampleToGroupBox();
            sbgp.setGroupingType(sg2.getKey());
            int i = 0;
            SampleToGroupBox.Entry last = null;
            while (i < track.getSamples().size()) {
                int index = 0;
                int j2 = 0;
                while (j2 < sg2.getValue().size()) {
                    SampleTableBox stbl3 = stbl;
                    if (Arrays.binarySearch(track.getSampleGroups().get((GroupEntry) sg2.getValue().get(j2)), (long) i) >= 0) {
                        index = j2 + 1;
                    }
                    j2++;
                    Track track3 = track;
                    map = chunks;
                    stbl = stbl3;
                }
                if (last == null) {
                    j = 1;
                } else if (last.getGroupDescriptionIndex() != index) {
                    j = 1;
                } else {
                    last.setSampleCount(last.getSampleCount() + 1);
                    i++;
                    defaultMp4Builder = this;
                    track2 = track;
                }
                SampleToGroupBox.Entry last2 = new SampleToGroupBox.Entry(j, index);
                sbgp.getEntries().add(last2);
                last = last2;
                i++;
                defaultMp4Builder = this;
                track2 = track;
            }
            stbl.addBox(sgdb);
            stbl.addBox(sbgp);
        }
        if (track2 instanceof CencEncryptedTrack) {
            defaultMp4Builder.createCencBoxes((CencEncryptedTrack) track2, stbl, map.get(track2));
        }
        defaultMp4Builder.createSubs(track2, stbl);
        return stbl;
    }

    /* access modifiers changed from: protected */
    public void createSubs(Track track, SampleTableBox stbl) {
        if (track.getSubsampleInformationBox() != null) {
            stbl.addBox(track.getSubsampleInformationBox());
        }
    }

    /* access modifiers changed from: protected */
    public void createCencBoxes(CencEncryptedTrack track, SampleTableBox stbl, int[] chunkSizes) {
        SampleTableBox sampleTableBox = stbl;
        int[] iArr = chunkSizes;
        SampleAuxiliaryInformationSizesBox saiz = new SampleAuxiliaryInformationSizesBox();
        saiz.setAuxInfoType(C.CENC_TYPE_cenc);
        saiz.setFlags(1);
        List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries = track.getSampleEncryptionEntries();
        if (track.hasSubSampleEncryption()) {
            short[] sizes = new short[sampleEncryptionEntries.size()];
            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = (short) sampleEncryptionEntries.get(i).getSize();
            }
            saiz.setSampleInfoSizes(sizes);
        } else {
            saiz.setDefaultSampleInfoSize(8);
            saiz.setSampleCount(track.getSamples().size());
        }
        SampleAuxiliaryInformationOffsetsBox saio = new SampleAuxiliaryInformationOffsetsBox();
        SampleEncryptionBox senc = new SampleEncryptionBox();
        senc.setSubSampleEncryption(track.hasSubSampleEncryption());
        senc.setEntries(sampleEncryptionEntries);
        long offset = (long) senc.getOffsetToFirstIV();
        int index = 0;
        long[] offsets = new long[iArr.length];
        for (int i2 = 0; i2 < iArr.length; i2++) {
            offsets[i2] = offset;
            int j = 0;
            while (j < iArr[i2]) {
                offset += (long) sampleEncryptionEntries.get(index).getSize();
                j++;
                index++;
            }
        }
        saio.setOffsets(offsets);
        sampleTableBox.addBox(saiz);
        sampleTableBox.addBox(saio);
        sampleTableBox.addBox(senc);
        this.sampleAuxiliaryInformationOffsetsBoxes.add(saio);
    }

    /* access modifiers changed from: protected */
    public void createStsd(Track track, SampleTableBox stbl) {
        stbl.addBox(track.getSampleDescriptionBox());
    }

    /* access modifiers changed from: protected */
    public void createStco(Track track, Movie movie, Map<Track, int[]> chunks, SampleTableBox stbl) {
        StaticChunkOffsetBox stco;
        int[] tracksChunkSizes;
        long[] chunkOffset;
        int i;
        Track track2 = track;
        Map<Track, int[]> map = chunks;
        int[] chunkSizes = map.get(track2);
        StaticChunkOffsetBox stco2 = new StaticChunkOffsetBox();
        this.chunkOffsetBoxes.add(stco2);
        long offset = 0;
        long[] chunkOffset2 = new long[chunkSizes.length];
        if (LOG.isLoggable(Level.FINE)) {
            Logger logger = LOG;
            logger.fine("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId());
        }
        int i2 = 0;
        while (i2 < chunkSizes.length) {
            SampleTableBox sampleTableBox = stbl;
            if (LOG.isLoggable(Level.FINER)) {
                Logger logger2 = LOG;
                logger2.finer("Calculating chunk offsets for track_" + track.getTrackMetaData().getTrackId() + " chunk " + i2);
            }
            for (Track current : movie.getTracks()) {
                if (LOG.isLoggable(Level.FINEST)) {
                    Logger logger3 = LOG;
                    StringBuilder sb = new StringBuilder("Adding offsets of track_");
                    tracksChunkSizes = chunkSizes;
                    stco = stco2;
                    sb.append(current.getTrackMetaData().getTrackId());
                    logger3.finest(sb.toString());
                } else {
                    tracksChunkSizes = chunkSizes;
                    stco = stco2;
                }
                int[] chunkSizes2 = map.get(current);
                long firstSampleOfChunk = 0;
                int j = 0;
                while (j < i2) {
                    int i3 = i2;
                    firstSampleOfChunk += (long) chunkSizes2[j];
                    j++;
                    track2 = track;
                    Map<Track, int[]> map2 = chunks;
                }
                if (current == track2) {
                    chunkOffset2[i2] = offset;
                }
                int j2 = CastUtils.l2i(firstSampleOfChunk);
                while (true) {
                    chunkOffset = chunkOffset2;
                    i = i2;
                    if (((long) j2) >= ((long) chunkSizes2[i2]) + firstSampleOfChunk) {
                        break;
                    }
                    offset += this.track2SampleSizes.get(current)[j2];
                    j2++;
                    Track track3 = track;
                    Map<Track, int[]> map3 = chunks;
                    chunkOffset2 = chunkOffset;
                    i2 = i;
                }
                track2 = track;
                map = chunks;
                chunkSizes = tracksChunkSizes;
                stco2 = stco;
                chunkOffset2 = chunkOffset;
                i2 = i;
            }
            i2++;
        }
        stco2.setChunkOffsets(chunkOffset2);
        stbl.addBox(stco2);
    }

    /* access modifiers changed from: protected */
    public void createStsz(Track track, SampleTableBox stbl) {
        SampleSizeBox stsz = new SampleSizeBox();
        stsz.setSampleSizes(this.track2SampleSizes.get(track));
        stbl.addBox(stsz);
    }

    /* access modifiers changed from: protected */
    public void createStsc(Track track, Map<Track, int[]> chunks, SampleTableBox stbl) {
        int[] tracksChunkSizes = chunks.get(track);
        SampleToChunkBox stsc = new SampleToChunkBox();
        stsc.setEntries(new LinkedList());
        long lastChunkSize = -2147483648L;
        int i = 0;
        while (i < tracksChunkSizes.length) {
            SampleTableBox sampleTableBox = stbl;
            if (lastChunkSize != ((long) tracksChunkSizes[i])) {
                List<SampleToChunkBox.Entry> entries = stsc.getEntries();
                SampleToChunkBox.Entry entry = r9;
                SampleToChunkBox.Entry entry2 = new SampleToChunkBox.Entry((long) (i + 1), (long) tracksChunkSizes[i], 1);
                entries.add(entry);
                lastChunkSize = (long) tracksChunkSizes[i];
            }
            i++;
            Track track2 = track;
        }
        stbl.addBox(stsc);
    }

    /* access modifiers changed from: protected */
    public void createSdtp(Track track, SampleTableBox stbl) {
        if (track.getSampleDependencies() != null && !track.getSampleDependencies().isEmpty()) {
            SampleDependencyTypeBox sdtp = new SampleDependencyTypeBox();
            sdtp.setEntries(track.getSampleDependencies());
            stbl.addBox(sdtp);
        }
    }

    /* access modifiers changed from: protected */
    public void createStss(Track track, SampleTableBox stbl) {
        long[] syncSamples = track.getSyncSamples();
        if (syncSamples != null && syncSamples.length > 0) {
            SyncSampleBox stss = new SyncSampleBox();
            stss.setSampleNumber(syncSamples);
            stbl.addBox(stss);
        }
    }

    /* access modifiers changed from: protected */
    public void createCtts(Track track, SampleTableBox stbl) {
        List<CompositionTimeToSample.Entry> compositionTimeToSampleEntries = track.getCompositionTimeEntries();
        if (compositionTimeToSampleEntries != null && !compositionTimeToSampleEntries.isEmpty()) {
            CompositionTimeToSample ctts = new CompositionTimeToSample();
            ctts.setEntries(compositionTimeToSampleEntries);
            stbl.addBox(ctts);
        }
    }

    /* access modifiers changed from: protected */
    public void createStts(Track track, SampleTableBox stbl) {
        TimeToSampleBox.Entry lastEntry = null;
        List<TimeToSampleBox.Entry> entries = new ArrayList<>();
        for (long delta : track.getSampleDurations()) {
            if (lastEntry == null || lastEntry.getDelta() != delta) {
                lastEntry = new TimeToSampleBox.Entry(1, delta);
                entries.add(lastEntry);
            } else {
                lastEntry.setCount(lastEntry.getCount() + 1);
            }
        }
        TimeToSampleBox stts = new TimeToSampleBox();
        stts.setEntries(entries);
        stbl.addBox(stts);
    }

    /* access modifiers changed from: package-private */
    public int[] getChunkSizes(Track track, Movie movie) {
        long end;
        long[] referenceChunkStarts = this.intersectionFinder.sampleNumbers(track);
        int[] chunkSizes = new int[referenceChunkStarts.length];
        for (int i = 0; i < referenceChunkStarts.length; i++) {
            long start = referenceChunkStarts[i] - 1;
            if (referenceChunkStarts.length == i + 1) {
                end = (long) track.getSamples().size();
            } else {
                end = referenceChunkStarts[i + 1] - 1;
            }
            chunkSizes[i] = CastUtils.l2i(end - start);
        }
        return chunkSizes;
    }

    public long getTimescale(Movie movie) {
        long timescale = movie.getTracks().iterator().next().getTrackMetaData().getTimescale();
        for (Track track : movie.getTracks()) {
            timescale = gcd(track.getTrackMetaData().getTimescale(), timescale);
        }
        return timescale;
    }

    private class InterleaveChunkMdat implements Box {
        List<List<Sample>> chunkList;
        long contentSize;
        Container parent;
        List<Track> tracks;

        private InterleaveChunkMdat(Movie movie, Map<Track, int[]> chunks, long contentSize2) {
            this.chunkList = new ArrayList();
            this.contentSize = contentSize2;
            this.tracks = movie.getTracks();
            for (int i = 0; i < chunks.values().iterator().next().length; i++) {
                for (Track track : this.tracks) {
                    int[] chunkSizes = chunks.get(track);
                    long firstSampleOfChunk = 0;
                    for (int j = 0; j < i; j++) {
                        firstSampleOfChunk += (long) chunkSizes[j];
                    }
                    this.chunkList.add(DefaultMp4Builder.this.track2Sample.get(track).subList(CastUtils.l2i(firstSampleOfChunk), CastUtils.l2i(((long) chunkSizes[i]) + firstSampleOfChunk)));
                }
            }
        }

        /* synthetic */ InterleaveChunkMdat(DefaultMp4Builder defaultMp4Builder, Movie movie, Map map, long j, InterleaveChunkMdat interleaveChunkMdat) {
            this(movie, map, j);
        }

        public Container getParent() {
            return this.parent;
        }

        public void setParent(Container parent2) {
            this.parent = parent2;
        }

        public long getOffset() {
            throw new RuntimeException("Doesn't have any meaning for programmatically created boxes");
        }

        public void parse(DataSource dataSource, ByteBuffer header, long contentSize2, BoxParser boxParser) throws IOException {
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long getDataOffset() {
            /*
                r7 = this;
                r0 = r7
                r1 = 16
            L_0x0004:
                boolean r3 = r0 instanceof com.coremedia.iso.boxes.Box
                if (r3 != 0) goto L_0x0009
                return r1
            L_0x0009:
                r3 = r0
                com.coremedia.iso.boxes.Box r3 = (com.coremedia.iso.boxes.Box) r3
                com.coremedia.iso.boxes.Container r3 = r3.getParent()
                java.util.List r3 = r3.getBoxes()
                java.util.Iterator r3 = r3.iterator()
            L_0x0018:
                boolean r4 = r3.hasNext()
                if (r4 != 0) goto L_0x001f
                goto L_0x0028
            L_0x001f:
                java.lang.Object r4 = r3.next()
                com.coremedia.iso.boxes.Box r4 = (com.coremedia.iso.boxes.Box) r4
                if (r0 != r4) goto L_0x0030
            L_0x0028:
                r3 = r0
                com.coremedia.iso.boxes.Box r3 = (com.coremedia.iso.boxes.Box) r3
                com.coremedia.iso.boxes.Container r0 = r3.getParent()
                goto L_0x0004
            L_0x0030:
                long r5 = r4.getSize()
                long r1 = r1 + r5
                goto L_0x0018
            */
            throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder.InterleaveChunkMdat.getDataOffset():long");
        }

        public String getType() {
            return MediaDataBox.TYPE;
        }

        public long getSize() {
            return this.contentSize + 16;
        }

        private boolean isSmallBox(long contentSize2) {
            return 8 + contentSize2 < 4294967296L;
        }

        public void getBox(WritableByteChannel writableByteChannel) throws IOException {
            ByteBuffer bb = ByteBuffer.allocate(16);
            long size = getSize();
            if (isSmallBox(size)) {
                IsoTypeWriter.writeUInt32(bb, size);
            } else {
                IsoTypeWriter.writeUInt32(bb, 1);
            }
            bb.put(IsoFile.fourCCtoBytes(MediaDataBox.TYPE));
            if (isSmallBox(size)) {
                bb.put(new byte[8]);
            } else {
                IsoTypeWriter.writeUInt64(bb, size);
            }
            bb.rewind();
            writableByteChannel.write(bb);
            for (List<Sample> samples : this.chunkList) {
                for (Sample sample : samples) {
                    sample.writeTo(writableByteChannel);
                }
            }
        }
    }
}
