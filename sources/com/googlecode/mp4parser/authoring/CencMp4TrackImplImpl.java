package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.Container;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.authoring.tracks.CencEncryptedTrack;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox;
import com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CencMp4TrackImplImpl extends Mp4TrackImpl implements CencEncryptedTrack {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private UUID defaultKeyId;
    private List<CencSampleAuxiliaryDataFormat> sampleEncryptionEntries = new ArrayList();

    /* JADX WARNING: type inference failed for: r4v4, types: [com.coremedia.iso.boxes.Box] */
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public CencMp4TrackImplImpl(java.lang.String r36, com.coremedia.iso.boxes.TrackBox r37, com.coremedia.iso.IsoFile... r38) throws java.io.IOException {
        /*
            r35 = this;
            r0 = r35
            r1 = r37
            r35.<init>(r36, r37, r38)
            java.lang.String r2 = "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schm[0]"
            com.coremedia.iso.boxes.Box r2 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r2)
            com.coremedia.iso.boxes.SchemeTypeBox r2 = (com.coremedia.iso.boxes.SchemeTypeBox) r2
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0.sampleEncryptionEntries = r3
            com.coremedia.iso.boxes.TrackHeaderBox r3 = r37.getTrackHeaderBox()
            long r3 = r3.getTrackId()
            com.coremedia.iso.boxes.Container r5 = r37.getParent()
            java.lang.Class<com.coremedia.iso.boxes.fragment.MovieExtendsBox> r6 = com.coremedia.iso.boxes.fragment.MovieExtendsBox.class
            java.util.List r5 = r5.getBoxes(r6)
            int r5 = r5.size()
            java.lang.String r6 = "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schi[0]/tenc[0]"
            if (r5 <= 0) goto L_0x0173
            com.coremedia.iso.boxes.Container r5 = r37.getParent()
            com.coremedia.iso.boxes.Box r5 = (com.coremedia.iso.boxes.Box) r5
            com.coremedia.iso.boxes.Container r5 = r5.getParent()
            java.lang.Class<com.coremedia.iso.boxes.fragment.MovieFragmentBox> r7 = com.coremedia.iso.boxes.fragment.MovieFragmentBox.class
            java.util.List r5 = r5.getBoxes(r7)
            java.util.Iterator r5 = r5.iterator()
        L_0x0045:
            boolean r7 = r5.hasNext()
            if (r7 != 0) goto L_0x0051
            r17 = r2
            r18 = r3
            goto L_0x0239
        L_0x0051:
            java.lang.Object r7 = r5.next()
            com.coremedia.iso.boxes.fragment.MovieFragmentBox r7 = (com.coremedia.iso.boxes.fragment.MovieFragmentBox) r7
            java.lang.Class<com.coremedia.iso.boxes.fragment.TrackFragmentBox> r8 = com.coremedia.iso.boxes.fragment.TrackFragmentBox.class
            java.util.List r8 = r7.getBoxes(r8)
            java.util.Iterator r9 = r8.iterator()
        L_0x0061:
            boolean r10 = r9.hasNext()
            if (r10 != 0) goto L_0x0068
            goto L_0x0045
        L_0x0068:
            java.lang.Object r10 = r9.next()
            com.coremedia.iso.boxes.fragment.TrackFragmentBox r10 = (com.coremedia.iso.boxes.fragment.TrackFragmentBox) r10
            com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox r11 = r10.getTrackFragmentHeaderBox()
            long r11 = r11.getTrackId()
            int r13 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r13 != 0) goto L_0x0163
            com.coremedia.iso.boxes.Box r11 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r6)
            com.mp4parser.iso23001.part7.TrackEncryptionBox r11 = (com.mp4parser.iso23001.part7.TrackEncryptionBox) r11
            java.util.UUID r12 = r11.getDefault_KID()
            r0.defaultKeyId = r12
            com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox r12 = r10.getTrackFragmentHeaderBox()
            boolean r12 = r12.hasBaseDataOffset()
            if (r12 == 0) goto L_0x00a3
            com.coremedia.iso.boxes.Container r12 = r37.getParent()
            com.coremedia.iso.boxes.Box r12 = (com.coremedia.iso.boxes.Box) r12
            com.coremedia.iso.boxes.Container r12 = r12.getParent()
            com.coremedia.iso.boxes.fragment.TrackFragmentHeaderBox r13 = r10.getTrackFragmentHeaderBox()
            long r13 = r13.getBaseDataOffset()
            goto L_0x00a6
        L_0x00a3:
            r12 = r7
            r13 = 0
        L_0x00a6:
            com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair r15 = new com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair
            r15.<init>(r10)
            com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair r15 = r15.invoke()
            com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox r16 = r15.getSaio()
            r17 = r2
            com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox r2 = r15.getSaiz()
            r18 = r3
            long[] r3 = r16.getOffsets()
            java.lang.Class<com.coremedia.iso.boxes.fragment.TrackRunBox> r4 = com.coremedia.iso.boxes.fragment.TrackRunBox.class
            java.util.List r4 = r10.getBoxes(r4)
            r20 = 0
            r21 = 0
            r34 = r21
            r21 = r5
            r5 = r34
        L_0x00d2:
            r22 = r7
            int r7 = r3.length
            if (r5 < r7) goto L_0x00e0
            r2 = r17
            r3 = r18
            r5 = r21
            r7 = r22
            goto L_0x0061
        L_0x00e0:
            java.lang.Object r7 = r4.get(r5)
            com.coremedia.iso.boxes.fragment.TrackRunBox r7 = (com.coremedia.iso.boxes.fragment.TrackRunBox) r7
            java.util.List r7 = r7.getEntries()
            int r7 = r7.size()
            r23 = r3[r5]
            r25 = 0
            r27 = r20
            r28 = r3
            r29 = r4
            r3 = r25
            r25 = r8
            r8 = r27
        L_0x00fe:
            r26 = r9
            int r9 = r20 + r7
            if (r8 < r9) goto L_0x014a
            long r8 = r13 + r23
            java.nio.ByteBuffer r9 = r12.getByteBuffer(r8, r3)
            r8 = r20
        L_0x010c:
            r27 = r10
            int r10 = r20 + r7
            if (r8 < r10) goto L_0x0123
            int r20 = r20 + r7
            int r5 = r5 + 1
            r7 = r22
            r8 = r25
            r9 = r26
            r10 = r27
            r3 = r28
            r4 = r29
            goto L_0x00d2
        L_0x0123:
            short r10 = r2.getSize(r8)
            r30 = r5
            java.util.List<com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat> r5 = r0.sampleEncryptionEntries
            r31 = r7
            int r7 = r11.getDefaultIvSize()
            r32 = r11
            r33 = r12
            long r11 = (long) r10
            com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat r7 = r0.parseCencAuxDataFormat(r7, r9, r11)
            r5.add(r7)
            int r8 = r8 + 1
            r10 = r27
            r5 = r30
            r7 = r31
            r11 = r32
            r12 = r33
            goto L_0x010c
        L_0x014a:
            r30 = r5
            r31 = r7
            r27 = r10
            r32 = r11
            r33 = r12
            short r5 = r2.getSize(r8)
            long r9 = (long) r5
            long r3 = r3 + r9
            int r8 = r8 + 1
            r9 = r26
            r10 = r27
            r5 = r30
            goto L_0x00fe
        L_0x0163:
            r17 = r2
            r18 = r3
            r21 = r5
            r22 = r7
            r25 = r8
            r26 = r9
            r27 = r10
            goto L_0x0061
        L_0x0173:
            r17 = r2
            r18 = r3
            com.coremedia.iso.boxes.Box r2 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r6)
            com.mp4parser.iso23001.part7.TrackEncryptionBox r2 = (com.mp4parser.iso23001.part7.TrackEncryptionBox) r2
            java.util.UUID r3 = r2.getDefault_KID()
            r0.defaultKeyId = r3
            java.lang.String r3 = "mdia[0]/minf[0]/stbl[0]/stco[0]"
            com.coremedia.iso.boxes.Box r3 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r3)
            com.coremedia.iso.boxes.ChunkOffsetBox r3 = (com.coremedia.iso.boxes.ChunkOffsetBox) r3
            if (r3 != 0) goto L_0x0196
            java.lang.String r4 = "mdia[0]/minf[0]/stbl[0]/co64[0]"
            com.coremedia.iso.boxes.Box r4 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r4)
            r3 = r4
            com.coremedia.iso.boxes.ChunkOffsetBox r3 = (com.coremedia.iso.boxes.ChunkOffsetBox) r3
        L_0x0196:
            com.coremedia.iso.boxes.SampleTableBox r4 = r37.getSampleTableBox()
            com.coremedia.iso.boxes.SampleToChunkBox r4 = r4.getSampleToChunkBox()
            long[] r5 = r3.getChunkOffsets()
            int r5 = r5.length
            long[] r4 = r4.blowup(r5)
            com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair r5 = new com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair
            java.lang.String r6 = "mdia[0]/minf[0]/stbl[0]"
            com.coremedia.iso.boxes.Box r6 = com.googlecode.mp4parser.util.Path.getPath((com.googlecode.mp4parser.AbstractContainerBox) r1, (java.lang.String) r6)
            com.coremedia.iso.boxes.Container r6 = (com.coremedia.iso.boxes.Container) r6
            r5.<init>(r6)
            com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl$FindSaioSaizPair r5 = r5.invoke()
            com.mp4parser.iso14496.part12.SampleAuxiliaryInformationOffsetsBox r6 = r5.saio
            com.mp4parser.iso14496.part12.SampleAuxiliaryInformationSizesBox r7 = r5.saiz
            com.coremedia.iso.boxes.Container r8 = r37.getParent()
            com.coremedia.iso.boxes.MovieBox r8 = (com.coremedia.iso.boxes.MovieBox) r8
            com.coremedia.iso.boxes.Container r8 = r8.getParent()
            long[] r9 = r6.getOffsets()
            int r9 = r9.length
            r10 = 1
            if (r9 != r10) goto L_0x022b
            long[] r9 = r6.getOffsets()
            r10 = 0
            r10 = r9[r10]
            r9 = 0
            int r12 = r7.getDefaultSampleInfoSize()
            if (r12 <= 0) goto L_0x01ed
            int r12 = r7.getSampleCount()
            int r13 = r7.getDefaultSampleInfoSize()
            int r12 = r12 * r13
            int r9 = r9 + r12
            r13 = r9
            goto L_0x01f5
        L_0x01ed:
            r12 = 0
        L_0x01ee:
            int r13 = r7.getSampleCount()
            if (r12 < r13) goto L_0x021d
            r13 = r9
        L_0x01f5:
            long r14 = (long) r13
            java.nio.ByteBuffer r14 = r8.getByteBuffer(r10, r14)
            r9 = 0
        L_0x01fb:
            int r12 = r7.getSampleCount()
            if (r9 < r12) goto L_0x0202
            goto L_0x0239
        L_0x0202:
            java.util.List<com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat> r12 = r0.sampleEncryptionEntries
            int r15 = r2.getDefaultIvSize()
            short r1 = r7.getSize(r9)
            r20 = r10
            long r10 = (long) r1
            com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat r1 = r0.parseCencAuxDataFormat(r15, r14, r10)
            r12.add(r1)
            int r9 = r9 + 1
            r1 = r37
            r10 = r20
            goto L_0x01fb
        L_0x021d:
            r20 = r10
            short[] r1 = r7.getSampleInfoSizes()
            short r1 = r1[r12]
            int r9 = r9 + r1
            int r12 = r12 + 1
            r1 = r37
            goto L_0x01ee
        L_0x022b:
            long[] r1 = r6.getOffsets()
            int r1 = r1.length
            int r9 = r4.length
            if (r1 != r9) goto L_0x02b0
            r1 = 0
            r9 = 0
        L_0x0235:
            int r10 = r4.length
            if (r9 < r10) goto L_0x023a
        L_0x0239:
            return
        L_0x023a:
            long[] r10 = r6.getOffsets()
            r11 = r10[r9]
            r13 = 0
            int r10 = r7.getDefaultSampleInfoSize()
            if (r10 <= 0) goto L_0x0256
            int r10 = r7.getSampleCount()
            r15 = r5
            r16 = r6
            long r5 = (long) r10
            r20 = r4[r9]
            long r5 = r5 * r20
            long r13 = r13 + r5
            goto L_0x0265
        L_0x0256:
            r15 = r5
            r16 = r6
            r5 = 0
        L_0x025a:
            r20 = r13
            long r13 = (long) r5
            r22 = r4[r9]
            int r6 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1))
            if (r6 < 0) goto L_0x029c
            r13 = r20
        L_0x0265:
            java.nio.ByteBuffer r6 = r8.getByteBuffer(r11, r13)
            r5 = 0
        L_0x026a:
            r22 = r11
            long r10 = (long) r5
            r20 = r4[r9]
            int r12 = (r10 > r20 ? 1 : (r10 == r20 ? 0 : -1))
            if (r12 < 0) goto L_0x027f
            long r10 = (long) r1
            r20 = r4[r9]
            long r10 = r10 + r20
            int r1 = (int) r10
            int r9 = r9 + 1
            r5 = r15
            r6 = r16
            goto L_0x0235
        L_0x027f:
            int r10 = r1 + r5
            short r10 = r7.getSize(r10)
            long r10 = (long) r10
            java.util.List<com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat> r12 = r0.sampleEncryptionEntries
            r24 = r3
            int r3 = r2.getDefaultIvSize()
            com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat r3 = r0.parseCencAuxDataFormat(r3, r6, r10)
            r12.add(r3)
            int r5 = r5 + 1
            r11 = r22
            r3 = r24
            goto L_0x026a
        L_0x029c:
            r24 = r3
            r22 = r11
            int r3 = r1 + r5
            short r3 = r7.getSize(r3)
            long r10 = (long) r3
            long r13 = r20 + r10
            int r5 = r5 + 1
            r11 = r22
            r3 = r24
            goto L_0x025a
        L_0x02b0:
            r24 = r3
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r3 = "Number of saio offsets must be either 1 or number of chunks"
            r1.<init>(r3)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl.<init>(java.lang.String, com.coremedia.iso.boxes.TrackBox, com.coremedia.iso.IsoFile[]):void");
    }

    private CencSampleAuxiliaryDataFormat parseCencAuxDataFormat(int ivSize, ByteBuffer chunksCencSampleAuxData, long auxInfoSize) {
        CencSampleAuxiliaryDataFormat cadf = new CencSampleAuxiliaryDataFormat();
        if (auxInfoSize > 0) {
            cadf.iv = new byte[ivSize];
            chunksCencSampleAuxData.get(cadf.iv);
            if (auxInfoSize > ((long) ivSize)) {
                cadf.pairs = new CencSampleAuxiliaryDataFormat.Pair[IsoTypeReader.readUInt16(chunksCencSampleAuxData)];
                for (int i = 0; i < cadf.pairs.length; i++) {
                    cadf.pairs[i] = cadf.createPair(IsoTypeReader.readUInt16(chunksCencSampleAuxData), IsoTypeReader.readUInt32(chunksCencSampleAuxData));
                }
            }
        }
        return cadf;
    }

    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    public boolean hasSubSampleEncryption() {
        return false;
    }

    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.sampleEncryptionEntries;
    }

    public String toString() {
        return "CencMp4TrackImpl{handler='" + getHandler() + '\'' + '}';
    }

    public String getName() {
        return "enc(" + super.getName() + SQLBuilder.PARENTHESES_RIGHT;
    }

    private class FindSaioSaizPair {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private Container container;
        /* access modifiers changed from: private */
        public SampleAuxiliaryInformationOffsetsBox saio;
        /* access modifiers changed from: private */
        public SampleAuxiliaryInformationSizesBox saiz;

        static {
            Class<CencMp4TrackImplImpl> cls = CencMp4TrackImplImpl.class;
        }

        public FindSaioSaizPair(Container container2) {
            this.container = container2;
        }

        public SampleAuxiliaryInformationSizesBox getSaiz() {
            return this.saiz;
        }

        public SampleAuxiliaryInformationOffsetsBox getSaio() {
            return this.saio;
        }

        public FindSaioSaizPair invoke() {
            List<SampleAuxiliaryInformationSizesBox> saizs = this.container.getBoxes(SampleAuxiliaryInformationSizesBox.class);
            List<SampleAuxiliaryInformationOffsetsBox> saios = this.container.getBoxes(SampleAuxiliaryInformationOffsetsBox.class);
            this.saiz = null;
            this.saio = null;
            for (int i = 0; i < saizs.size(); i++) {
                if (!(this.saiz == null && saizs.get(i).getAuxInfoType() == null) && !C.CENC_TYPE_cenc.equals(saizs.get(i).getAuxInfoType())) {
                    SampleAuxiliaryInformationSizesBox sampleAuxiliaryInformationSizesBox = this.saiz;
                    if (sampleAuxiliaryInformationSizesBox == null || sampleAuxiliaryInformationSizesBox.getAuxInfoType() != null || !C.CENC_TYPE_cenc.equals(saizs.get(i).getAuxInfoType())) {
                        throw new RuntimeException("Are there two cenc labeled saiz?");
                    }
                    this.saiz = saizs.get(i);
                } else {
                    this.saiz = saizs.get(i);
                }
                if (!(this.saio == null && saios.get(i).getAuxInfoType() == null) && !C.CENC_TYPE_cenc.equals(saios.get(i).getAuxInfoType())) {
                    SampleAuxiliaryInformationOffsetsBox sampleAuxiliaryInformationOffsetsBox = this.saio;
                    if (sampleAuxiliaryInformationOffsetsBox == null || sampleAuxiliaryInformationOffsetsBox.getAuxInfoType() != null || !C.CENC_TYPE_cenc.equals(saios.get(i).getAuxInfoType())) {
                        throw new RuntimeException("Are there two cenc labeled saio?");
                    }
                    this.saio = saios.get(i);
                } else {
                    this.saio = saios.get(i);
                }
            }
            return this;
        }
    }
}
