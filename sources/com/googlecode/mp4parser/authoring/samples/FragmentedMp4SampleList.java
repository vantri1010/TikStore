package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackExtendsBox;
import com.coremedia.iso.boxes.fragment.TrackFragmentBox;
import com.coremedia.iso.boxes.fragment.TrackRunBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Path;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentedMp4SampleList extends AbstractList<Sample> {
    private List<TrackFragmentBox> allTrafs;
    private int[] firstSamples;
    IsoFile[] fragments;
    private SoftReference<Sample>[] sampleCache;
    private int size_ = -1;
    Container topLevel;
    TrackBox trackBox = null;
    TrackExtendsBox trex = null;
    private Map<TrackRunBox, SoftReference<ByteBuffer>> trunDataCache = new HashMap();

    public FragmentedMp4SampleList(long track, Container topLevel2, IsoFile... fragments2) {
        this.topLevel = topLevel2;
        this.fragments = fragments2;
        for (TrackBox tb : Path.getPaths(topLevel2, "moov[0]/trak")) {
            if (tb.getTrackHeaderBox().getTrackId() == track) {
                this.trackBox = tb;
            }
        }
        if (this.trackBox != null) {
            for (TrackExtendsBox box : Path.getPaths(topLevel2, "moov[0]/mvex[0]/trex")) {
                if (box.getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    this.trex = box;
                }
            }
            this.sampleCache = (SoftReference[]) Array.newInstance(SoftReference.class, size());
            initAllFragments();
            return;
        }
        throw new RuntimeException("This MP4 does not contain track " + track);
    }

    private List<TrackFragmentBox> initAllFragments() {
        List<TrackFragmentBox> list = this.allTrafs;
        if (list != null) {
            return list;
        }
        List<TrackFragmentBox> trafs = new ArrayList<>();
        for (MovieFragmentBox moof : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : moof.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    trafs.add(trackFragmentBox);
                }
            }
        }
        IsoFile[] isoFileArr = this.fragments;
        if (isoFileArr != null) {
            for (IsoFile fragment : isoFileArr) {
                for (MovieFragmentBox moof2 : fragment.getBoxes(MovieFragmentBox.class)) {
                    for (TrackFragmentBox trackFragmentBox2 : moof2.getBoxes(TrackFragmentBox.class)) {
                        if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                            trafs.add(trackFragmentBox2);
                        }
                    }
                }
            }
        }
        this.allTrafs = trafs;
        int firstSample = 1;
        this.firstSamples = new int[trafs.size()];
        for (int i = 0; i < this.allTrafs.size(); i++) {
            this.firstSamples[i] = firstSample;
            firstSample += getTrafSize(this.allTrafs.get(i));
        }
        return trafs;
    }

    private int getTrafSize(TrackFragmentBox traf) {
        List<Box> boxes = traf.getBoxes();
        int size = 0;
        for (int i = 0; i < boxes.size(); i++) {
            Box b = boxes.get(i);
            if (b instanceof TrackRunBox) {
                size += CastUtils.l2i(((TrackRunBox) b).getSampleCount());
            }
        }
        return size;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.googlecode.mp4parser.authoring.Sample get(int r28) {
        /*
            r27 = this;
            r7 = r27
            java.lang.ref.SoftReference<com.googlecode.mp4parser.authoring.Sample>[] r0 = r7.sampleCache
            r1 = r0[r28]
            if (r1 == 0) goto L_0x0014
            r0 = r0[r28]
            java.lang.Object r0 = r0.get()
            com.googlecode.mp4parser.authoring.Sample r0 = (com.googlecode.mp4parser.authoring.Sample) r0
            r1 = r0
            if (r0 == 0) goto L_0x0014
            return r1
        L_0x0014:
            int r8 = r28 + 1
            int[] r0 = r7.firstSamples
            int r0 = r0.length
            int r0 = r0 + -1
            r9 = r0
        L_0x001c:
            int[] r0 = r7.firstSamples
            r0 = r0[r9]
            int r0 = r8 - r0
            if (r0 < 0) goto L_0x0195
            java.util.List<com.coremedia.iso.boxes.fragment.TrackFragmentBox> r0 = r7.allTrafs
            java.lang.Object r0 = r0.get(r9)
            r10 = r0
            com.coremedia.iso.boxes.fragment.TrackFragmentBox r10 = (com.coremedia.iso.boxes.fragment.TrackFragmentBox) r10
            int[] r0 = r7.firstSamples
            r0 = r0[r9]
            int r11 = r8 - r0
            r0 = 0
            com.coremedia.iso.boxes.Container r1 = r10.getParent()
            r12 = r1
            com.coremedia.iso.boxes.fragment.MovieFragmentBox r12 = (com.coremedia.iso.boxes.fragment.MovieFragmentBox) r12
            java.util.List r1 = r10.getBoxes()
            java.util.Iterator r1 = r1.iterator()
            r13 = r0
        L_0x0044:
            boolean r0 = r1.hasNext()
            if (r0 == 0) goto L_0x018d
            java.lang.Object r0 = r1.next()
            r14 = r0
            com.coremedia.iso.boxes.Box r14 = (com.coremedia.iso.boxes.Box) r14
            boolean r0 = r14 instanceof com.coremedia.iso.boxes.fragment.TrackRunBox
            if (r0 == 0) goto L_0x0189
            r15 = r14
            com.coremedia.iso.boxes.fragment.TrackRunBox r15 = (com.coremedia.iso.boxes.fragment.TrackRunBox) r15
            java.util.List r0 = r15.getEntries()
            int r0 = r0.size()
            int r2 = r11 - r13
            if (r0 >= r2) goto L_0x006e
            java.util.List r0 = r15.getEntries()
            int r0 = r0.size()
            int r13 = r13 + r0
            goto L_0x0044
        L_0x006e:
            java.util.List r3 = r15.getEntries()
            com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox r16 = r10.getTrackFragmentHeaderBox()
            boolean r17 = r15.isSampleSizePresent()
            boolean r18 = r16.hasDefaultSampleSize()
            r0 = 0
            if (r17 != 0) goto L_0x009e
            if (r18 == 0) goto L_0x008b
            long r0 = r16.getDefaultSampleSize()
            r19 = r0
            goto L_0x00a0
        L_0x008b:
            com.coremedia.iso.boxes.fragment.TrackExtendsBox r2 = r7.trex
            if (r2 == 0) goto L_0x0096
            long r0 = r2.getDefaultSampleSize()
            r19 = r0
            goto L_0x00a0
        L_0x0096:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            java.lang.String r4 = "File doesn't contain trex box but track fragments aren't fully self contained. Cannot determine sample size."
            r2.<init>(r4)
            throw r2
        L_0x009e:
            r19 = r0
        L_0x00a0:
            java.util.Map<com.coremedia.iso.boxes.fragment.TrackRunBox, java.lang.ref.SoftReference<java.nio.ByteBuffer>> r0 = r7.trunDataCache
            java.lang.Object r0 = r0.get(r15)
            r21 = r0
            java.lang.ref.SoftReference r21 = (java.lang.ref.SoftReference) r21
            if (r21 == 0) goto L_0x00b3
            java.lang.Object r0 = r21.get()
            java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
            goto L_0x00b4
        L_0x00b3:
            r0 = 0
        L_0x00b4:
            r1 = r0
            if (r1 != 0) goto L_0x0134
            r4 = 0
            boolean r0 = r16.hasBaseDataOffset()
            if (r0 == 0) goto L_0x00cb
            long r22 = r16.getBaseDataOffset()
            long r4 = r4 + r22
            com.coremedia.iso.boxes.Container r0 = r12.getParent()
            r2 = r0
            goto L_0x00cd
        L_0x00cb:
            r0 = r12
            r2 = r0
        L_0x00cd:
            boolean r0 = r15.isDataOffsetPresent()
            if (r0 == 0) goto L_0x00db
            int r0 = r15.getDataOffset()
            r6 = r1
            long r0 = (long) r0
            long r4 = r4 + r0
            goto L_0x00dc
        L_0x00db:
            r6 = r1
        L_0x00dc:
            r0 = 0
            java.util.Iterator r1 = r3.iterator()
            r22 = r6
            r6 = r0
        L_0x00e4:
            boolean r0 = r1.hasNext()
            if (r0 != 0) goto L_0x0110
            long r0 = (long) r6
            java.nio.ByteBuffer r0 = r2.getByteBuffer(r4, r0)     // Catch:{ IOException -> 0x0105 }
            r1 = r0
            java.util.Map<com.coremedia.iso.boxes.fragment.TrackRunBox, java.lang.ref.SoftReference<java.nio.ByteBuffer>> r0 = r7.trunDataCache     // Catch:{ IOException -> 0x0101 }
            r23 = r2
            java.lang.ref.SoftReference r2 = new java.lang.ref.SoftReference     // Catch:{ IOException -> 0x00ff }
            r2.<init>(r1)     // Catch:{ IOException -> 0x00ff }
            r0.put(r15, r2)     // Catch:{ IOException -> 0x00ff }
            r22 = r1
            goto L_0x0136
        L_0x00ff:
            r0 = move-exception
            goto L_0x010a
        L_0x0101:
            r0 = move-exception
            r23 = r2
            goto L_0x010a
        L_0x0105:
            r0 = move-exception
            r23 = r2
            r1 = r22
        L_0x010a:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        L_0x0110:
            r23 = r2
            java.lang.Object r0 = r1.next()
            com.coremedia.iso.boxes.fragment.TrackRunBox$Entry r0 = (com.coremedia.iso.boxes.fragment.TrackRunBox.Entry) r0
            if (r17 == 0) goto L_0x0129
            r24 = r1
            long r1 = (long) r6
            long r25 = r0.getSampleSize()
            long r1 = r1 + r25
            int r6 = (int) r1
            r2 = r23
            r1 = r24
            goto L_0x00e4
        L_0x0129:
            r24 = r1
            long r1 = (long) r6
            long r1 = r1 + r19
            int r6 = (int) r1
            r2 = r23
            r1 = r24
            goto L_0x00e4
        L_0x0134:
            r22 = r1
        L_0x0136:
            r0 = 0
            r1 = 0
        L_0x0138:
            int r2 = r11 - r13
            if (r1 < r2) goto L_0x016c
            if (r17 == 0) goto L_0x014d
            int r1 = r11 - r13
            java.lang.Object r1 = r3.get(r1)
            com.coremedia.iso.boxes.fragment.TrackRunBox$Entry r1 = (com.coremedia.iso.boxes.fragment.TrackRunBox.Entry) r1
            long r1 = r1.getSampleSize()
            r23 = r1
            goto L_0x0151
        L_0x014d:
            r1 = r19
            r23 = r1
        L_0x0151:
            r5 = r22
            r6 = r0
            com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList$1 r25 = new com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList$1
            r1 = r25
            r2 = r27
            r26 = r8
            r8 = r3
            r3 = r23
            r1.<init>(r3, r5, r6)
            java.lang.ref.SoftReference<com.googlecode.mp4parser.authoring.Sample>[] r2 = r7.sampleCache
            java.lang.ref.SoftReference r3 = new java.lang.ref.SoftReference
            r3.<init>(r1)
            r2[r28] = r3
            return r1
        L_0x016c:
            r26 = r8
            r8 = r3
            if (r17 == 0) goto L_0x017f
            long r2 = (long) r0
            java.lang.Object r4 = r8.get(r1)
            com.coremedia.iso.boxes.fragment.TrackRunBox$Entry r4 = (com.coremedia.iso.boxes.fragment.TrackRunBox.Entry) r4
            long r4 = r4.getSampleSize()
            long r2 = r2 + r4
            int r0 = (int) r2
            goto L_0x0183
        L_0x017f:
            long r2 = (long) r0
            long r2 = r2 + r19
            int r0 = (int) r2
        L_0x0183:
            int r1 = r1 + 1
            r3 = r8
            r8 = r26
            goto L_0x0138
        L_0x0189:
            r26 = r8
            goto L_0x0044
        L_0x018d:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "Couldn't find sample in the traf I was looking"
            r0.<init>(r1)
            throw r0
        L_0x0195:
            r26 = r8
            int r9 = r9 + -1
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList.get(int):com.googlecode.mp4parser.authoring.Sample");
    }

    public int size() {
        int i = this.size_;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (MovieFragmentBox moof : this.topLevel.getBoxes(MovieFragmentBox.class)) {
            for (TrackFragmentBox trackFragmentBox : moof.getBoxes(TrackFragmentBox.class)) {
                if (trackFragmentBox.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                    i2 = (int) (((long) i2) + trackFragmentBox.getBoxes(TrackRunBox.class).get(0).getSampleCount());
                }
            }
        }
        for (IsoFile fragment : this.fragments) {
            for (MovieFragmentBox moof2 : fragment.getBoxes(MovieFragmentBox.class)) {
                for (TrackFragmentBox trackFragmentBox2 : moof2.getBoxes(TrackFragmentBox.class)) {
                    if (trackFragmentBox2.getTrackFragmentHeaderBox().getTrackId() == this.trackBox.getTrackHeaderBox().getTrackId()) {
                        i2 = (int) (((long) i2) + trackFragmentBox2.getBoxes(TrackRunBox.class).get(0).getSampleCount());
                    }
                }
            }
        }
        this.size_ = i2;
        return i2;
    }
}
