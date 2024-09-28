package com.google.android.exoplayer2.extractor.mp4;

import android.util.Pair;
import com.coremedia.iso.boxes.MetaBox;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.AvcConfig;
import com.google.android.exoplayer2.video.ColorInfo;
import com.google.android.exoplayer2.video.HevcConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class AtomParsers {
    private static final int MAX_GAPLESS_TRIM_SIZE_SAMPLES = 3;
    private static final String TAG = "AtomParsers";
    private static final int TYPE_clcp = Util.getIntegerCodeForString("clcp");
    private static final int TYPE_mdta = Util.getIntegerCodeForString("mdta");
    private static final int TYPE_meta = Util.getIntegerCodeForString(MetaBox.TYPE);
    private static final int TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    private static final int TYPE_soun = Util.getIntegerCodeForString("soun");
    private static final int TYPE_subt = Util.getIntegerCodeForString("subt");
    private static final int TYPE_text = Util.getIntegerCodeForString("text");
    private static final int TYPE_vide = Util.getIntegerCodeForString("vide");
    private static final byte[] opusMagic = Util.getUtf8Bytes("OpusHead");

    private interface SampleSizeBox {
        int getSampleCount();

        boolean isFixedSampleSize();

        int readNextSampleSize();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: long[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: long[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.google.android.exoplayer2.extractor.mp4.Track parseTrak(com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom r31, com.google.android.exoplayer2.extractor.mp4.Atom.LeafAtom r32, long r33, com.google.android.exoplayer2.drm.DrmInitData r35, boolean r36, boolean r37) throws com.google.android.exoplayer2.ParserException {
        /*
            r0 = r31
            int r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_mdia
            com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom r1 = r0.getContainerAtomOfType(r1)
            int r2 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_hdlr
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r2 = r1.getLeafAtomOfType(r2)
            com.google.android.exoplayer2.util.ParsableByteArray r2 = r2.data
            int r2 = parseHdlr(r2)
            int r2 = getTrackTypeForHdlr(r2)
            r3 = 0
            r4 = -1
            if (r2 != r4) goto L_0x001d
            return r3
        L_0x001d:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_tkhd
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r4 = r0.getLeafAtomOfType(r4)
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r4.data
            com.google.android.exoplayer2.extractor.mp4.AtomParsers$TkhdData r18 = parseTkhd(r4)
            r4 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r6 = (r33 > r4 ? 1 : (r33 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0039
            long r6 = r18.duration
            r19 = r6
            goto L_0x003b
        L_0x0039:
            r19 = r33
        L_0x003b:
            r15 = r32
            com.google.android.exoplayer2.util.ParsableByteArray r6 = r15.data
            long r21 = parseMvhd(r6)
            int r6 = (r19 > r4 ? 1 : (r19 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x004f
            r4 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r23 = r4
            goto L_0x005c
        L_0x004f:
            r10 = 1000000(0xf4240, double:4.940656E-318)
            r8 = r19
            r12 = r21
            long r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r8, r10, r12)
            r23 = r4
        L_0x005c:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_minf
            com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom r4 = r1.getContainerAtomOfType(r4)
            int r5 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stbl
            com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom r14 = r4.getContainerAtomOfType(r5)
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_mdhd
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r4 = r1.getLeafAtomOfType(r4)
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r4.data
            android.util.Pair r13 = parseMdhd(r4)
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stsd
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r4 = r14.getLeafAtomOfType(r4)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r4.data
            int r6 = r18.id
            int r7 = r18.rotationDegrees
            java.lang.Object r4 = r13.second
            r8 = r4
            java.lang.String r8 = (java.lang.String) r8
            r9 = r35
            r10 = r37
            com.google.android.exoplayer2.extractor.mp4.AtomParsers$StsdData r12 = parseStsd(r5, r6, r7, r8, r9, r10)
            r4 = 0
            r5 = 0
            if (r36 != 0) goto L_0x00ae
            int r6 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_edts
            com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom r6 = r0.getContainerAtomOfType(r6)
            android.util.Pair r6 = parseEdts(r6)
            java.lang.Object r7 = r6.first
            r4 = r7
            long[] r4 = (long[]) r4
            java.lang.Object r7 = r6.second
            r5 = r7
            long[] r5 = (long[]) r5
            r25 = r4
            r26 = r5
            goto L_0x00b2
        L_0x00ae:
            r25 = r4
            r26 = r5
        L_0x00b2:
            com.google.android.exoplayer2.Format r4 = r12.format
            if (r4 != 0) goto L_0x00bd
            r30 = r12
            r28 = r13
            r29 = r14
            goto L_0x00f7
        L_0x00bd:
            com.google.android.exoplayer2.extractor.mp4.Track r27 = new com.google.android.exoplayer2.extractor.mp4.Track
            int r4 = r18.id
            java.lang.Object r3 = r13.first
            java.lang.Long r3 = (java.lang.Long) r3
            long r6 = r3.longValue()
            com.google.android.exoplayer2.Format r10 = r12.format
            int r11 = r12.requiredSampleTransformation
            com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox[] r8 = r12.trackEncryptionBoxes
            int r9 = r12.nalUnitLengthFieldLength
            r3 = r27
            r5 = r2
            r16 = r8
            r17 = r9
            r8 = r21
            r28 = r10
            r29 = r11
            r10 = r23
            r30 = r12
            r12 = r28
            r28 = r13
            r13 = r29
            r29 = r14
            r14 = r16
            r15 = r17
            r16 = r25
            r17 = r26
            r3.<init>(r4, r5, r6, r8, r10, r12, r13, r14, r15, r16, r17)
        L_0x00f7:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseTrak(com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom, com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom, long, com.google.android.exoplayer2.drm.DrmInitData, boolean, boolean):com.google.android.exoplayer2.extractor.mp4.Track");
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x05d0  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x028e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.google.android.exoplayer2.extractor.mp4.TrackSampleTable parseStbl(com.google.android.exoplayer2.extractor.mp4.Track r72, com.google.android.exoplayer2.extractor.mp4.Atom.ContainerAtom r73, com.google.android.exoplayer2.extractor.GaplessInfoHolder r74) throws com.google.android.exoplayer2.ParserException {
        /*
            r9 = r72
            r10 = r73
            r11 = r74
            int r0 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stsz
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r12 = r10.getLeafAtomOfType(r0)
            if (r12 == 0) goto L_0x0015
            com.google.android.exoplayer2.extractor.mp4.AtomParsers$StszSampleSizeBox r0 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$StszSampleSizeBox
            r0.<init>(r12)
            r13 = r0
            goto L_0x0023
        L_0x0015:
            int r0 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stz2
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r0 = r10.getLeafAtomOfType(r0)
            if (r0 == 0) goto L_0x05f9
            com.google.android.exoplayer2.extractor.mp4.AtomParsers$Stz2SampleSizeBox r1 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$Stz2SampleSizeBox
            r1.<init>(r0)
            r13 = r1
        L_0x0023:
            int r14 = r13.getSampleCount()
            r0 = 0
            if (r14 != 0) goto L_0x0041
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r15 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable
            long[] r2 = new long[r0]
            int[] r3 = new int[r0]
            r4 = 0
            long[] r5 = new long[r0]
            int[] r6 = new int[r0]
            r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r0 = r15
            r1 = r72
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r15
        L_0x0041:
            r1 = 0
            int r2 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stco
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r2 = r10.getLeafAtomOfType(r2)
            if (r2 != 0) goto L_0x0054
            r1 = 1
            int r3 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_co64
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r2 = r10.getLeafAtomOfType(r3)
            r15 = r1
            r7 = r2
            goto L_0x0056
        L_0x0054:
            r15 = r1
            r7 = r2
        L_0x0056:
            com.google.android.exoplayer2.util.ParsableByteArray r8 = r7.data
            int r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stsc
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r1 = r10.getLeafAtomOfType(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r6 = r1.data
            int r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stts
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r1 = r10.getLeafAtomOfType(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r1.data
            int r1 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_stss
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r4 = r10.getLeafAtomOfType(r1)
            r1 = 0
            if (r4 == 0) goto L_0x0074
            com.google.android.exoplayer2.util.ParsableByteArray r2 = r4.data
            goto L_0x0075
        L_0x0074:
            r2 = r1
        L_0x0075:
            int r3 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ctts
            com.google.android.exoplayer2.extractor.mp4.Atom$LeafAtom r3 = r10.getLeafAtomOfType(r3)
            if (r3 == 0) goto L_0x007f
            com.google.android.exoplayer2.util.ParsableByteArray r1 = r3.data
        L_0x007f:
            com.google.android.exoplayer2.extractor.mp4.AtomParsers$ChunkIterator r0 = new com.google.android.exoplayer2.extractor.mp4.AtomParsers$ChunkIterator
            r0.<init>(r6, r8, r15)
            r17 = r3
            r3 = 12
            r5.setPosition(r3)
            int r18 = r5.readUnsignedIntToInt()
            r3 = 1
            int r18 = r18 + -1
            int r20 = r5.readUnsignedIntToInt()
            int r3 = r5.readUnsignedIntToInt()
            r22 = 0
            r23 = 0
            r24 = 0
            if (r1 == 0) goto L_0x00ae
            r25 = r4
            r4 = 12
            r1.setPosition(r4)
            int r23 = r1.readUnsignedIntToInt()
            goto L_0x00b0
        L_0x00ae:
            r25 = r4
        L_0x00b0:
            r4 = -1
            r26 = 0
            if (r2 == 0) goto L_0x00d3
            r27 = r4
            r4 = 12
            r2.setPosition(r4)
            int r26 = r2.readUnsignedIntToInt()
            if (r26 <= 0) goto L_0x00cd
            int r4 = r2.readUnsignedIntToInt()
            r19 = 1
            int r4 = r4 + -1
            r19 = r2
            goto L_0x00d7
        L_0x00cd:
            r2 = 0
            r19 = r2
            r4 = r27
            goto L_0x00d7
        L_0x00d3:
            r27 = r4
            r19 = r2
        L_0x00d7:
            boolean r2 = r13.isFixedSampleSize()
            if (r2 == 0) goto L_0x00f4
            com.google.android.exoplayer2.Format r2 = r9.format
            java.lang.String r2 = r2.sampleMimeType
            r27 = r4
            java.lang.String r4 = "audio/raw"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x00f6
            if (r18 != 0) goto L_0x00f6
            if (r23 != 0) goto L_0x00f6
            if (r26 != 0) goto L_0x00f6
            r2 = 1
            goto L_0x00f7
        L_0x00f4:
            r27 = r4
        L_0x00f6:
            r2 = 0
        L_0x00f7:
            r28 = r2
            r2 = 0
            r29 = 0
            if (r28 != 0) goto L_0x028e
            long[] r4 = new long[r14]
            r31 = r2
            int[] r2 = new int[r14]
            r32 = r6
            long[] r6 = new long[r14]
            r33 = r7
            int[] r7 = new int[r14]
            r34 = 0
            r36 = 0
            r37 = 0
            r11 = r26
            r10 = r27
            r26 = r34
            r9 = r37
            r34 = r8
            r35 = r12
            r12 = r18
            r18 = r22
            r22 = r24
            r8 = r3
            r3 = r31
            r71 = r36
            r36 = r15
            r15 = r20
            r20 = r71
        L_0x012f:
            r37 = r5
            java.lang.String r5 = "AtomParsers"
            if (r9 >= r14) goto L_0x01e3
            r24 = 1
        L_0x0137:
            if (r20 != 0) goto L_0x0152
            boolean r31 = r0.moveNext()
            r24 = r31
            if (r31 == 0) goto L_0x0152
            r38 = r14
            r31 = r15
            long r14 = r0.offset
            r26 = r14
            int r14 = r0.numSamples
            r20 = r14
            r15 = r31
            r14 = r38
            goto L_0x0137
        L_0x0152:
            r38 = r14
            r31 = r15
            if (r24 != 0) goto L_0x0172
            java.lang.String r14 = "Unexpected end of chunk data"
            com.google.android.exoplayer2.util.Log.w(r5, r14)
            r14 = r9
            long[] r4 = java.util.Arrays.copyOf(r4, r14)
            int[] r2 = java.util.Arrays.copyOf(r2, r14)
            long[] r6 = java.util.Arrays.copyOf(r6, r14)
            int[] r7 = java.util.Arrays.copyOf(r7, r14)
            r9 = r20
            goto L_0x01eb
        L_0x0172:
            if (r1 == 0) goto L_0x018a
        L_0x0174:
            if (r18 != 0) goto L_0x0183
            if (r23 <= 0) goto L_0x0183
            int r18 = r1.readUnsignedIntToInt()
            int r22 = r1.readInt()
            int r23 = r23 + -1
            goto L_0x0174
        L_0x0183:
            int r5 = r18 + -1
            r18 = r5
            r5 = r22
            goto L_0x018c
        L_0x018a:
            r5 = r22
        L_0x018c:
            r4[r9] = r26
            int r14 = r13.readNextSampleSize()
            r2[r9] = r14
            r14 = r2[r9]
            if (r14 <= r3) goto L_0x019a
            r3 = r2[r9]
        L_0x019a:
            long r14 = (long) r5
            long r14 = r29 + r14
            r6[r9] = r14
            if (r19 != 0) goto L_0x01a3
            r14 = 1
            goto L_0x01a4
        L_0x01a3:
            r14 = 0
        L_0x01a4:
            r7[r9] = r14
            if (r9 != r10) goto L_0x01b5
            r14 = 1
            r7[r9] = r14
            int r11 = r11 + -1
            if (r11 <= 0) goto L_0x01b5
            int r15 = r19.readUnsignedIntToInt()
            int r10 = r15 + -1
        L_0x01b5:
            long r14 = (long) r8
            long r29 = r29 + r14
            int r15 = r31 + -1
            if (r15 != 0) goto L_0x01c9
            if (r12 <= 0) goto L_0x01c9
            int r14 = r37.readUnsignedIntToInt()
            int r8 = r37.readInt()
            int r12 = r12 + -1
            r15 = r14
        L_0x01c9:
            r14 = r2[r9]
            r39 = r2
            r22 = r3
            long r2 = (long) r14
            long r26 = r26 + r2
            int r20 = r20 + -1
            int r9 = r9 + 1
            r3 = r22
            r14 = r38
            r2 = r39
            r22 = r5
            r5 = r37
            goto L_0x012f
        L_0x01e3:
            r39 = r2
            r38 = r14
            r31 = r15
            r9 = r20
        L_0x01eb:
            r20 = r3
            r15 = r22
            r22 = r2
            long r2 = (long) r15
            long r2 = r29 + r2
            r24 = 1
        L_0x01f6:
            if (r23 <= 0) goto L_0x0207
            int r38 = r1.readUnsignedIntToInt()
            if (r38 == 0) goto L_0x0201
            r24 = 0
            goto L_0x0207
        L_0x0201:
            r1.readInt()
            int r23 = r23 + -1
            goto L_0x01f6
        L_0x0207:
            if (r11 != 0) goto L_0x0221
            if (r31 != 0) goto L_0x0221
            if (r9 != 0) goto L_0x0221
            if (r12 != 0) goto L_0x0221
            if (r18 != 0) goto L_0x0221
            if (r24 != 0) goto L_0x0214
            goto L_0x0221
        L_0x0214:
            r39 = r1
            r40 = r2
            r38 = r18
            r3 = r31
            r31 = r15
            r15 = r72
            goto L_0x027a
        L_0x0221:
            r39 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r40 = r2
            java.lang.String r2 = "Inconsistent stbl box for track "
            r1.append(r2)
            r2 = r15
            r15 = r72
            int r3 = r15.id
            r1.append(r3)
            java.lang.String r3 = ": remainingSynchronizationSamples "
            r1.append(r3)
            r1.append(r11)
            java.lang.String r3 = ", remainingSamplesAtTimestampDelta "
            r1.append(r3)
            r3 = r31
            r1.append(r3)
            r31 = r2
            java.lang.String r2 = ", remainingSamplesInChunk "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r2 = ", remainingTimestampDeltaChanges "
            r1.append(r2)
            r1.append(r12)
            java.lang.String r2 = ", remainingSamplesAtTimestampOffset "
            r1.append(r2)
            r2 = r18
            r1.append(r2)
            if (r24 != 0) goto L_0x026a
            java.lang.String r18 = ", ctts invalid"
            goto L_0x026c
        L_0x026a:
            java.lang.String r18 = ""
        L_0x026c:
            r38 = r2
            r2 = r18
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.google.android.exoplayer2.util.Log.w(r5, r1)
        L_0x027a:
            r27 = r10
            r26 = r11
            r18 = r12
            r9 = r22
            r24 = r31
            r11 = r40
            r22 = r3
            r10 = r8
            r8 = r7
            r7 = r6
            r6 = r14
            r14 = r4
            goto L_0x02e9
        L_0x028e:
            r39 = r1
            r31 = r2
            r37 = r5
            r32 = r6
            r33 = r7
            r34 = r8
            r35 = r12
            r38 = r14
            r36 = r15
            r15 = r9
            int r1 = r0.length
            long[] r1 = new long[r1]
            int r2 = r0.length
            int[] r2 = new int[r2]
        L_0x02a9:
            boolean r4 = r0.moveNext()
            if (r4 == 0) goto L_0x02bc
            int r4 = r0.index
            long r5 = r0.offset
            r1[r4] = r5
            int r4 = r0.index
            int r5 = r0.numSamples
            r2[r4] = r5
            goto L_0x02a9
        L_0x02bc:
            com.google.android.exoplayer2.Format r4 = r15.format
            int r4 = r4.pcmEncoding
            com.google.android.exoplayer2.Format r5 = r15.format
            int r5 = r5.channelCount
            int r4 = com.google.android.exoplayer2.util.Util.getPcmFrameSize(r4, r5)
            long r5 = (long) r3
            com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker$Results r5 = com.google.android.exoplayer2.extractor.mp4.FixedSampleSizeRechunker.rechunk(r4, r1, r2, r5)
            long[] r6 = r5.offsets
            int[] r7 = r5.sizes
            int r8 = r5.maximumSize
            long[] r9 = r5.timestamps
            int[] r10 = r5.flags
            long r11 = r5.duration
            r14 = r6
            r6 = r38
            r38 = r22
            r22 = r20
            r20 = r8
            r8 = r10
            r10 = r3
            r71 = r9
            r9 = r7
            r7 = r71
        L_0x02e9:
            r42 = 1000000(0xf4240, double:4.940656E-318)
            long r1 = r15.timescale
            r40 = r11
            r44 = r1
            long r47 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r40, r42, r44)
            long[] r1 = r15.editListDurations
            if (r1 == 0) goto L_0x05d0
            boolean r1 = r74.hasGaplessInfo()
            if (r1 == 0) goto L_0x0311
            r31 = r0
            r58 = r6
            r46 = r10
            r59 = r11
            r40 = r13
            r41 = r14
            r12 = r7
            r11 = r8
            r14 = r9
            goto L_0x05df
        L_0x0311:
            long[] r1 = r15.editListDurations
            int r1 = r1.length
            r2 = 1
            if (r1 != r2) goto L_0x03e0
            int r1 = r15.type
            if (r1 != r2) goto L_0x03e0
            int r1 = r7.length
            r2 = 2
            if (r1 < r2) goto L_0x03e0
            long[] r1 = r15.editListMediaTimes
            r2 = 0
            r51 = r1[r2]
            long[] r1 = r15.editListDurations
            r40 = r1[r2]
            long r1 = r15.timescale
            long r4 = r15.movieTimescale
            r42 = r1
            r44 = r4
            long r1 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r40, r42, r44)
            long r55 = r51 + r1
            r40 = r7
            r41 = r11
            r43 = r51
            r45 = r55
            boolean r1 = canApplyEditWithGaplessInfo(r40, r41, r43, r45)
            if (r1 == 0) goto L_0x03d6
            long r57 = r11 - r55
            r1 = 0
            r2 = r7[r1]
            long r40 = r51 - r2
            com.google.android.exoplayer2.Format r1 = r15.format
            int r1 = r1.sampleRate
            long r1 = (long) r1
            long r3 = r15.timescale
            r42 = r1
            r44 = r3
            long r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r40, r42, r44)
            com.google.android.exoplayer2.Format r1 = r15.format
            int r1 = r1.sampleRate
            long r1 = (long) r1
            r59 = r11
            r12 = r10
            long r10 = r15.timescale
            r40 = r57
            r42 = r1
            r44 = r10
            long r10 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r40, r42, r44)
            r1 = 0
            int r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x0382
            int r3 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0379
            goto L_0x0382
        L_0x0379:
            r31 = r0
            r10 = r6
            r11 = r8
            r46 = r12
            r12 = r7
            goto L_0x03e9
        L_0x0382:
            r1 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r3 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r3 > 0) goto L_0x03ca
            int r3 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r3 > 0) goto L_0x03ca
            int r1 = (int) r4
            r3 = r74
            r3.encoderDelay = r1
            int r1 = (int) r10
            r3.encoderPadding = r1
            long r1 = r15.timescale
            r40 = r4
            r3 = 1000000(0xf4240, double:4.940656E-318)
            com.google.android.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r7, r3, r1)
            long[] r1 = r15.editListDurations
            r2 = 0
            r61 = r1[r2]
            r63 = 1000000(0xf4240, double:4.940656E-318)
            long r1 = r15.movieTimescale
            r65 = r1
            long r42 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r61, r63, r65)
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r16 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable
            r31 = r0
            r0 = r16
            r1 = r72
            r2 = r14
            r3 = r9
            r4 = r20
            r5 = r7
            r44 = r10
            r10 = r6
            r6 = r8
            r11 = r8
            r46 = r12
            r12 = r7
            r7 = r42
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r16
        L_0x03ca:
            r31 = r0
            r40 = r4
            r44 = r10
            r46 = r12
            r10 = r6
            r12 = r7
            r11 = r8
            goto L_0x03e9
        L_0x03d6:
            r31 = r0
            r46 = r10
            r59 = r11
            r10 = r6
            r12 = r7
            r11 = r8
            goto L_0x03e9
        L_0x03e0:
            r31 = r0
            r46 = r10
            r59 = r11
            r10 = r6
            r12 = r7
            r11 = r8
        L_0x03e9:
            long[] r0 = r15.editListDurations
            int r0 = r0.length
            r1 = 1
            if (r0 != r1) goto L_0x0431
            long[] r0 = r15.editListDurations
            r1 = 0
            r2 = r0[r1]
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x0431
            long[] r0 = r15.editListMediaTimes
            r40 = r0[r1]
            r0 = 0
        L_0x03ff:
            int r1 = r12.length
            if (r0 >= r1) goto L_0x0414
            r1 = r12[r0]
            long r3 = r1 - r40
            r5 = 1000000(0xf4240, double:4.940656E-318)
            long r7 = r15.timescale
            long r1 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r3, r5, r7)
            r12[r0] = r1
            int r0 = r0 + 1
            goto L_0x03ff
        L_0x0414:
            long r1 = r59 - r40
            r3 = 1000000(0xf4240, double:4.940656E-318)
            long r5 = r15.timescale
            long r42 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r1, r3, r5)
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r16 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable
            r0 = r16
            r1 = r72
            r2 = r14
            r3 = r9
            r4 = r20
            r5 = r12
            r6 = r11
            r7 = r42
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r16
        L_0x0431:
            int r0 = r15.type
            r1 = 1
            if (r0 != r1) goto L_0x0438
            r0 = 1
            goto L_0x0439
        L_0x0438:
            r0 = 0
        L_0x0439:
            r7 = r0
            r0 = 0
            r1 = 0
            r2 = 0
            long[] r3 = r15.editListDurations
            int r3 = r3.length
            int[] r8 = new int[r3]
            long[] r3 = r15.editListDurations
            int r3 = r3.length
            int[] r6 = new int[r3]
            r3 = 0
            r5 = r0
            r4 = r1
        L_0x044a:
            long[] r0 = r15.editListDurations
            int r0 = r0.length
            if (r3 >= r0) goto L_0x04bc
            long[] r0 = r15.editListMediaTimes
            r40 = r13
            r41 = r14
            r13 = r0[r3]
            r0 = -1
            int r42 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r42 == 0) goto L_0x04ab
            long[] r0 = r15.editListDurations
            r49 = r0[r3]
            long r0 = r15.timescale
            r42 = r9
            r43 = r10
            long r9 = r15.movieTimescale
            r51 = r0
            r53 = r9
            long r0 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r49, r51, r53)
            r9 = 1
            int r10 = com.google.android.exoplayer2.util.Util.binarySearchCeil((long[]) r12, (long) r13, (boolean) r9, (boolean) r9)
            r8[r3] = r10
            long r9 = r13 + r0
            r44 = r0
            r0 = 0
            int r1 = com.google.android.exoplayer2.util.Util.binarySearchCeil((long[]) r12, (long) r9, (boolean) r7, (boolean) r0)
            r6[r3] = r1
        L_0x0483:
            r1 = r8[r3]
            r9 = r6[r3]
            if (r1 >= r9) goto L_0x0497
            r1 = r8[r3]
            r1 = r11[r1]
            r9 = 1
            r1 = r1 & r9
            if (r1 != 0) goto L_0x0498
            r1 = r8[r3]
            int r1 = r1 + r9
            r8[r3] = r1
            goto L_0x0483
        L_0x0497:
            r9 = 1
        L_0x0498:
            r1 = r6[r3]
            r10 = r8[r3]
            int r1 = r1 - r10
            int r5 = r5 + r1
            r1 = r8[r3]
            if (r4 == r1) goto L_0x04a4
            r1 = 1
            goto L_0x04a5
        L_0x04a4:
            r1 = 0
        L_0x04a5:
            r1 = r1 | r2
            r2 = r6[r3]
            r4 = r2
            r2 = r1
            goto L_0x04b1
        L_0x04ab:
            r42 = r9
            r43 = r10
            r0 = 0
            r9 = 1
        L_0x04b1:
            int r3 = r3 + 1
            r13 = r40
            r14 = r41
            r9 = r42
            r10 = r43
            goto L_0x044a
        L_0x04bc:
            r42 = r9
            r43 = r10
            r40 = r13
            r41 = r14
            r0 = 0
            r9 = 1
            r14 = r43
            if (r5 == r14) goto L_0x04cc
            r3 = 1
            goto L_0x04cd
        L_0x04cc:
            r3 = 0
        L_0x04cd:
            r9 = r2 | r3
            if (r9 == 0) goto L_0x04d4
            long[] r1 = new long[r5]
            goto L_0x04d6
        L_0x04d4:
            r1 = r41
        L_0x04d6:
            r10 = r1
            if (r9 == 0) goto L_0x04dc
            int[] r1 = new int[r5]
            goto L_0x04de
        L_0x04dc:
            r1 = r42
        L_0x04de:
            r13 = r1
            if (r9 == 0) goto L_0x04e2
            goto L_0x04e4
        L_0x04e2:
            r0 = r20
        L_0x04e4:
            if (r9 == 0) goto L_0x04e9
            int[] r1 = new int[r5]
            goto L_0x04ea
        L_0x04e9:
            r1 = r11
        L_0x04ea:
            r3 = r1
            long[] r2 = new long[r5]
            r43 = 0
            r1 = 0
            r16 = 0
            r71 = r16
            r16 = r0
            r0 = r71
        L_0x04f8:
            r21 = r4
            long[] r4 = r15.editListDurations
            int r4 = r4.length
            if (r0 >= r4) goto L_0x0595
            long[] r4 = r15.editListMediaTimes
            r55 = r4[r0]
            r4 = r8[r0]
            r45 = r5
            r5 = r6[r0]
            if (r9 == 0) goto L_0x051f
            r57 = r6
            int r6 = r5 - r4
            r58 = r14
            r14 = r41
            java.lang.System.arraycopy(r14, r4, r10, r1, r6)
            r14 = r42
            java.lang.System.arraycopy(r14, r4, r13, r1, r6)
            java.lang.System.arraycopy(r11, r4, r3, r1, r6)
            goto L_0x0525
        L_0x051f:
            r57 = r6
            r58 = r14
            r14 = r42
        L_0x0525:
            r6 = r4
            r42 = r3
            r3 = r16
        L_0x052a:
            if (r6 >= r5) goto L_0x0571
            r51 = 1000000(0xf4240, double:4.940656E-318)
            r61 = r4
            r62 = r5
            long r4 = r15.movieTimescale
            r49 = r43
            r53 = r4
            long r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r49, r51, r53)
            r49 = r12[r6]
            long r63 = r49 - r55
            r65 = 1000000(0xf4240, double:4.940656E-318)
            r69 = r7
            r70 = r8
            long r7 = r15.timescale
            r67 = r7
            long r7 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r63, r65, r67)
            long r49 = r4 + r7
            r2[r1] = r49
            if (r9 == 0) goto L_0x055f
            r63 = r2
            r2 = r13[r1]
            if (r2 <= r3) goto L_0x0561
            r3 = r14[r6]
            goto L_0x0561
        L_0x055f:
            r63 = r2
        L_0x0561:
            int r1 = r1 + 1
            int r6 = r6 + 1
            r4 = r61
            r5 = r62
            r2 = r63
            r7 = r69
            r8 = r70
            goto L_0x052a
        L_0x0571:
            r63 = r2
            r61 = r4
            r62 = r5
            r69 = r7
            r70 = r8
            long[] r2 = r15.editListDurations
            r4 = r2[r0]
            long r43 = r43 + r4
            int r0 = r0 + 1
            r16 = r3
            r4 = r21
            r3 = r42
            r5 = r45
            r6 = r57
            r2 = r63
            r42 = r14
            r14 = r58
            goto L_0x04f8
        L_0x0595:
            r63 = r2
            r45 = r5
            r57 = r6
            r69 = r7
            r70 = r8
            r58 = r14
            r14 = r42
            r42 = r3
            r51 = 1000000(0xf4240, double:4.940656E-318)
            long r2 = r15.movieTimescale
            r49 = r43
            r53 = r2
            long r49 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r49, r51, r53)
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r51 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable
            r0 = r51
            r52 = r1
            r1 = r72
            r53 = r63
            r2 = r10
            r3 = r13
            r4 = r16
            r5 = r53
            r54 = r57
            r6 = r42
            r55 = r69
            r56 = r70
            r7 = r49
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r51
        L_0x05d0:
            r31 = r0
            r58 = r6
            r46 = r10
            r59 = r11
            r40 = r13
            r41 = r14
            r12 = r7
            r11 = r8
            r14 = r9
        L_0x05df:
            long r0 = r15.timescale
            r2 = 1000000(0xf4240, double:4.940656E-318)
            com.google.android.exoplayer2.util.Util.scaleLargeTimestampsInPlace(r12, r2, r0)
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r9 = new com.google.android.exoplayer2.extractor.mp4.TrackSampleTable
            r0 = r9
            r1 = r72
            r2 = r41
            r3 = r14
            r4 = r20
            r5 = r12
            r6 = r11
            r7 = r47
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            return r9
        L_0x05f9:
            com.google.android.exoplayer2.ParserException r1 = new com.google.android.exoplayer2.ParserException
            java.lang.String r2 = "Track has no sample table size information"
            r1.<init>((java.lang.String) r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseStbl(com.google.android.exoplayer2.extractor.mp4.Track, com.google.android.exoplayer2.extractor.mp4.Atom$ContainerAtom, com.google.android.exoplayer2.extractor.GaplessInfoHolder):com.google.android.exoplayer2.extractor.mp4.TrackSampleTable");
    }

    public static Metadata parseUdta(Atom.LeafAtom udtaAtom, boolean isQuickTime) {
        if (isQuickTime) {
            return null;
        }
        ParsableByteArray udtaData = udtaAtom.data;
        udtaData.setPosition(8);
        while (udtaData.bytesLeft() >= 8) {
            int atomPosition = udtaData.getPosition();
            int atomSize = udtaData.readInt();
            if (udtaData.readInt() == Atom.TYPE_meta) {
                udtaData.setPosition(atomPosition);
                return parseUdtaMeta(udtaData, atomPosition + atomSize);
            }
            udtaData.setPosition(atomPosition + atomSize);
        }
        return null;
    }

    public static Metadata parseMdtaFromMeta(Atom.ContainerAtom meta) {
        Atom.LeafAtom hdlrAtom = meta.getLeafAtomOfType(Atom.TYPE_hdlr);
        Atom.LeafAtom keysAtom = meta.getLeafAtomOfType(Atom.TYPE_keys);
        Atom.LeafAtom ilstAtom = meta.getLeafAtomOfType(Atom.TYPE_ilst);
        if (hdlrAtom == null || keysAtom == null || ilstAtom == null || parseHdlr(hdlrAtom.data) != TYPE_mdta) {
            return null;
        }
        ParsableByteArray keys = keysAtom.data;
        keys.setPosition(12);
        int entryCount = keys.readInt();
        String[] keyNames = new String[entryCount];
        for (int i = 0; i < entryCount; i++) {
            int entrySize = keys.readInt();
            keys.skipBytes(4);
            keyNames[i] = keys.readString(entrySize - 8);
        }
        ParsableByteArray ilst = ilstAtom.data;
        ilst.setPosition(8);
        ArrayList<Metadata.Entry> entries = new ArrayList<>();
        while (ilst.bytesLeft() > 8) {
            int atomPosition = ilst.getPosition();
            int atomSize = ilst.readInt();
            int keyIndex = ilst.readInt() - 1;
            if (keyIndex < 0 || keyIndex >= keyNames.length) {
                Log.w(TAG, "Skipped metadata with unknown key index: " + keyIndex);
            } else {
                Metadata.Entry entry = MetadataUtil.parseMdtaMetadataEntryFromIlst(ilst, atomPosition + atomSize, keyNames[keyIndex]);
                if (entry != null) {
                    entries.add(entry);
                }
            }
            ilst.setPosition(atomPosition + atomSize);
        }
        if (entries.isEmpty()) {
            return null;
        }
        return new Metadata((List<? extends Metadata.Entry>) entries);
    }

    private static Metadata parseUdtaMeta(ParsableByteArray meta, int limit) {
        meta.skipBytes(12);
        while (meta.getPosition() < limit) {
            int atomPosition = meta.getPosition();
            int atomSize = meta.readInt();
            if (meta.readInt() == Atom.TYPE_ilst) {
                meta.setPosition(atomPosition);
                return parseIlst(meta, atomPosition + atomSize);
            }
            meta.setPosition(atomPosition + atomSize);
        }
        return null;
    }

    private static Metadata parseIlst(ParsableByteArray ilst, int limit) {
        ilst.skipBytes(8);
        ArrayList<Metadata.Entry> entries = new ArrayList<>();
        while (ilst.getPosition() < limit) {
            Metadata.Entry entry = MetadataUtil.parseIlstElement(ilst);
            if (entry != null) {
                entries.add(entry);
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        return new Metadata((List<? extends Metadata.Entry>) entries);
    }

    private static long parseMvhd(ParsableByteArray mvhd) {
        int i = 8;
        mvhd.setPosition(8);
        if (Atom.parseFullAtomVersion(mvhd.readInt()) != 0) {
            i = 16;
        }
        mvhd.skipBytes(i);
        return mvhd.readUnsignedInt();
    }

    private static TkhdData parseTkhd(ParsableByteArray tkhd) {
        long duration;
        int rotationDegrees;
        int durationByteCount = 8;
        tkhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(tkhd.readInt());
        tkhd.skipBytes(version == 0 ? 8 : 16);
        int trackId = tkhd.readInt();
        tkhd.skipBytes(4);
        boolean durationUnknown = true;
        int durationPosition = tkhd.getPosition();
        if (version == 0) {
            durationByteCount = 4;
        }
        int i = 0;
        while (true) {
            if (i >= durationByteCount) {
                break;
            } else if (tkhd.data[durationPosition + i] != -1) {
                durationUnknown = false;
                break;
            } else {
                i++;
            }
        }
        if (durationUnknown) {
            tkhd.skipBytes(durationByteCount);
            duration = C.TIME_UNSET;
        } else {
            duration = version == 0 ? tkhd.readUnsignedInt() : tkhd.readUnsignedLongToLong();
            if (duration == 0) {
                duration = C.TIME_UNSET;
            }
        }
        tkhd.skipBytes(16);
        int a00 = tkhd.readInt();
        int a01 = tkhd.readInt();
        tkhd.skipBytes(4);
        int a10 = tkhd.readInt();
        int a11 = tkhd.readInt();
        if (a00 == 0 && a01 == 65536 && a10 == (-65536) && a11 == 0) {
            rotationDegrees = 90;
        } else if (a00 == 0 && a01 == (-65536) && a10 == 65536 && a11 == 0) {
            rotationDegrees = 270;
        } else if (a00 == (-65536) && a01 == 0 && a10 == 0 && a11 == (-65536)) {
            rotationDegrees = 180;
        } else {
            rotationDegrees = 0;
        }
        return new TkhdData(trackId, duration, rotationDegrees);
    }

    private static int parseHdlr(ParsableByteArray hdlr) {
        hdlr.setPosition(16);
        return hdlr.readInt();
    }

    private static int getTrackTypeForHdlr(int hdlr) {
        if (hdlr == TYPE_soun) {
            return 1;
        }
        if (hdlr == TYPE_vide) {
            return 2;
        }
        if (hdlr == TYPE_text || hdlr == TYPE_sbtl || hdlr == TYPE_subt || hdlr == TYPE_clcp) {
            return 3;
        }
        if (hdlr == TYPE_meta) {
            return 4;
        }
        return -1;
    }

    private static Pair<Long, String> parseMdhd(ParsableByteArray mdhd) {
        int i = 8;
        mdhd.setPosition(8);
        int version = Atom.parseFullAtomVersion(mdhd.readInt());
        mdhd.skipBytes(version == 0 ? 8 : 16);
        long timescale = mdhd.readUnsignedInt();
        if (version == 0) {
            i = 4;
        }
        mdhd.skipBytes(i);
        int languageCode = mdhd.readUnsignedShort();
        return Pair.create(Long.valueOf(timescale), "" + ((char) (((languageCode >> 10) & 31) + 96)) + ((char) (((languageCode >> 5) & 31) + 96)) + ((char) ((languageCode & 31) + 96)));
    }

    private static StsdData parseStsd(ParsableByteArray stsd, int trackId, int rotationDegrees, String language, DrmInitData drmInitData, boolean isQuickTime) throws ParserException {
        int childAtomType;
        ParsableByteArray parsableByteArray = stsd;
        parsableByteArray.setPosition(12);
        int numberOfEntries = stsd.readInt();
        StsdData out = new StsdData(numberOfEntries);
        for (int i = 0; i < numberOfEntries; i++) {
            int childStartPosition = stsd.getPosition();
            int childAtomSize = stsd.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType2 = stsd.readInt();
            if (childAtomType2 == Atom.TYPE_avc1 || childAtomType2 == Atom.TYPE_avc3 || childAtomType2 == Atom.TYPE_encv || childAtomType2 == Atom.TYPE_mp4v || childAtomType2 == Atom.TYPE_hvc1 || childAtomType2 == Atom.TYPE_hev1 || childAtomType2 == Atom.TYPE_s263 || childAtomType2 == Atom.TYPE_vp08) {
                childAtomType = childAtomType2;
            } else if (childAtomType2 == Atom.TYPE_vp09) {
                childAtomType = childAtomType2;
            } else if (childAtomType2 == Atom.TYPE_mp4a || childAtomType2 == Atom.TYPE_enca || childAtomType2 == Atom.TYPE_ac_3 || childAtomType2 == Atom.TYPE_ec_3 || childAtomType2 == Atom.TYPE_dtsc || childAtomType2 == Atom.TYPE_dtse || childAtomType2 == Atom.TYPE_dtsh || childAtomType2 == Atom.TYPE_dtsl || childAtomType2 == Atom.TYPE_samr || childAtomType2 == Atom.TYPE_sawb || childAtomType2 == Atom.TYPE_lpcm || childAtomType2 == Atom.TYPE_sowt || childAtomType2 == Atom.TYPE__mp3 || childAtomType2 == Atom.TYPE_alac || childAtomType2 == Atom.TYPE_alaw || childAtomType2 == Atom.TYPE_ulaw || childAtomType2 == Atom.TYPE_Opus || childAtomType2 == Atom.TYPE_fLaC) {
                int i2 = childAtomType2;
                parseAudioSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, isQuickTime, drmInitData, out, i);
                parsableByteArray.setPosition(childStartPosition + childAtomSize);
            } else if (childAtomType2 == Atom.TYPE_TTML || childAtomType2 == Atom.TYPE_tx3g || childAtomType2 == Atom.TYPE_wvtt || childAtomType2 == Atom.TYPE_stpp || childAtomType2 == Atom.TYPE_c608) {
                parseTextSampleEntry(stsd, childAtomType2, childStartPosition, childAtomSize, trackId, language, out);
                int i3 = childAtomType2;
                parsableByteArray.setPosition(childStartPosition + childAtomSize);
            } else {
                if (childAtomType2 == Atom.TYPE_camm) {
                    out.format = Format.createSampleFormat(Integer.toString(trackId), MimeTypes.APPLICATION_CAMERA_MOTION, (String) null, -1, (DrmInitData) null);
                    int i4 = childAtomType2;
                } else {
                    int i5 = childAtomType2;
                }
                parsableByteArray.setPosition(childStartPosition + childAtomSize);
            }
            parseVideoSampleEntry(stsd, childAtomType, childStartPosition, childAtomSize, trackId, rotationDegrees, drmInitData, out, i);
            parsableByteArray.setPosition(childStartPosition + childAtomSize);
        }
        return out;
    }

    private static void parseTextSampleEntry(ParsableByteArray parent, int atomType, int position, int atomSize, int trackId, String language, StsdData out) throws ParserException {
        String mimeType;
        ParsableByteArray parsableByteArray = parent;
        int i = atomType;
        StsdData stsdData = out;
        parsableByteArray.setPosition(position + 8 + 8);
        List<byte[]> initializationData = null;
        long subsampleOffsetUs = Long.MAX_VALUE;
        if (i == Atom.TYPE_TTML) {
            mimeType = MimeTypes.APPLICATION_TTML;
        } else if (i == Atom.TYPE_tx3g) {
            mimeType = MimeTypes.APPLICATION_TX3G;
            int sampleDescriptionLength = (atomSize - 8) - 8;
            byte[] sampleDescriptionData = new byte[sampleDescriptionLength];
            parsableByteArray.readBytes(sampleDescriptionData, 0, sampleDescriptionLength);
            initializationData = Collections.singletonList(sampleDescriptionData);
        } else if (i == Atom.TYPE_wvtt) {
            mimeType = MimeTypes.APPLICATION_MP4VTT;
        } else if (i == Atom.TYPE_stpp) {
            mimeType = MimeTypes.APPLICATION_TTML;
            subsampleOffsetUs = 0;
        } else if (i == Atom.TYPE_c608) {
            mimeType = MimeTypes.APPLICATION_MP4CEA608;
            stsdData.requiredSampleTransformation = 1;
        } else {
            throw new IllegalStateException();
        }
        stsdData.format = Format.createTextSampleFormat(Integer.toString(trackId), mimeType, (String) null, -1, 0, language, -1, (DrmInitData) null, subsampleOffsetUs, initializationData);
    }

    private static void parseVideoSampleEntry(ParsableByteArray parent, int atomType, int position, int size, int trackId, int rotationDegrees, DrmInitData drmInitData, StsdData out, int entryIndex) throws ParserException {
        DrmInitData drmInitData2;
        int atomType2;
        DrmInitData drmInitData3;
        ParsableByteArray parsableByteArray = parent;
        int i = position;
        int i2 = size;
        DrmInitData drmInitData4 = drmInitData;
        StsdData stsdData = out;
        parsableByteArray.setPosition(i + 8 + 8);
        parsableByteArray.skipBytes(16);
        int width = parent.readUnsignedShort();
        int height = parent.readUnsignedShort();
        parsableByteArray.skipBytes(50);
        int childPosition = parent.getPosition();
        int atomType3 = atomType;
        if (atomType3 == Atom.TYPE_encv) {
            Pair<Integer, TrackEncryptionBox> sampleEntryEncryptionData = parseSampleEntryEncryptionData(parsableByteArray, i, i2);
            if (sampleEntryEncryptionData != null) {
                atomType3 = ((Integer) sampleEntryEncryptionData.first).intValue();
                if (drmInitData4 == null) {
                    drmInitData3 = null;
                } else {
                    drmInitData3 = drmInitData4.copyWithSchemeType(((TrackEncryptionBox) sampleEntryEncryptionData.second).schemeType);
                }
                drmInitData4 = drmInitData3;
                stsdData.trackEncryptionBoxes[entryIndex] = (TrackEncryptionBox) sampleEntryEncryptionData.second;
            }
            parsableByteArray.setPosition(childPosition);
            drmInitData2 = drmInitData4;
            atomType2 = atomType3;
        } else {
            drmInitData2 = drmInitData4;
            atomType2 = atomType3;
        }
        boolean pixelWidthHeightRatioFromPasp = false;
        float pixelWidthHeightRatio = 1.0f;
        int childPosition2 = childPosition;
        List<byte[]> initializationData = null;
        String mimeType = null;
        byte[] projectionData = null;
        int stereoMode = -1;
        while (childPosition2 - i < i2) {
            parsableByteArray.setPosition(childPosition2);
            int childStartPosition = parent.getPosition();
            int childAtomSize = parent.readInt();
            if (childAtomSize == 0 && parent.getPosition() - i == i2) {
                break;
            }
            boolean z = false;
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_avcC) {
                if (mimeType == null) {
                    z = true;
                }
                Assertions.checkState(z);
                parsableByteArray.setPosition(childStartPosition + 8);
                AvcConfig avcConfig = AvcConfig.parse(parent);
                List<byte[]> initializationData2 = avcConfig.initializationData;
                stsdData.nalUnitLengthFieldLength = avcConfig.nalUnitLengthFieldLength;
                if (!pixelWidthHeightRatioFromPasp) {
                    pixelWidthHeightRatio = avcConfig.pixelWidthAspectRatio;
                }
                mimeType = "video/avc";
                initializationData = initializationData2;
            } else if (childAtomType == Atom.TYPE_hvcC) {
                if (mimeType == null) {
                    z = true;
                }
                Assertions.checkState(z);
                parsableByteArray.setPosition(childStartPosition + 8);
                HevcConfig hevcConfig = HevcConfig.parse(parent);
                List<byte[]> initializationData3 = hevcConfig.initializationData;
                stsdData.nalUnitLengthFieldLength = hevcConfig.nalUnitLengthFieldLength;
                mimeType = MimeTypes.VIDEO_H265;
                initializationData = initializationData3;
            } else if (childAtomType == Atom.TYPE_vpcC) {
                if (mimeType == null) {
                    z = true;
                }
                Assertions.checkState(z);
                mimeType = atomType2 == Atom.TYPE_vp08 ? MimeTypes.VIDEO_VP8 : MimeTypes.VIDEO_VP9;
            } else if (childAtomType == Atom.TYPE_d263) {
                if (mimeType == null) {
                    z = true;
                }
                Assertions.checkState(z);
                mimeType = MimeTypes.VIDEO_H263;
            } else if (childAtomType == Atom.TYPE_esds) {
                if (mimeType == null) {
                    z = true;
                }
                Assertions.checkState(z);
                Pair<String, byte[]> mimeTypeAndInitializationData = parseEsdsFromParent(parsableByteArray, childStartPosition);
                initializationData = Collections.singletonList(mimeTypeAndInitializationData.second);
                mimeType = (String) mimeTypeAndInitializationData.first;
            } else if (childAtomType == Atom.TYPE_pasp) {
                pixelWidthHeightRatio = parsePaspFromParent(parsableByteArray, childStartPosition);
                pixelWidthHeightRatioFromPasp = true;
            } else if (childAtomType == Atom.TYPE_sv3d) {
                projectionData = parseProjFromParent(parsableByteArray, childStartPosition, childAtomSize);
            } else if (childAtomType == Atom.TYPE_st3d) {
                int version = parent.readUnsignedByte();
                parsableByteArray.skipBytes(3);
                if (version == 0) {
                    int layout = parent.readUnsignedByte();
                    if (layout == 0) {
                        stereoMode = 0;
                    } else if (layout == 1) {
                        stereoMode = 1;
                    } else if (layout == 2) {
                        stereoMode = 2;
                    } else if (layout == 3) {
                        stereoMode = 3;
                    }
                }
            }
            childPosition2 += childAtomSize;
        }
        if (mimeType != null) {
            int i3 = childPosition2;
            stsdData.format = Format.createVideoSampleFormat(Integer.toString(trackId), mimeType, (String) null, -1, -1, width, height, -1.0f, initializationData, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, (ColorInfo) null, drmInitData2);
        }
    }

    private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom edtsAtom) {
        if (edtsAtom != null) {
            Atom.LeafAtom leafAtomOfType = edtsAtom.getLeafAtomOfType(Atom.TYPE_elst);
            Atom.LeafAtom elst = leafAtomOfType;
            if (leafAtomOfType != null) {
                ParsableByteArray elstData = elst.data;
                elstData.setPosition(8);
                int version = Atom.parseFullAtomVersion(elstData.readInt());
                int entryCount = elstData.readUnsignedIntToInt();
                long[] editListDurations = new long[entryCount];
                long[] editListMediaTimes = new long[entryCount];
                int i = 0;
                while (i < entryCount) {
                    editListDurations[i] = version == 1 ? elstData.readUnsignedLongToLong() : elstData.readUnsignedInt();
                    editListMediaTimes[i] = version == 1 ? elstData.readLong() : (long) elstData.readInt();
                    if (elstData.readShort() == 1) {
                        elstData.skipBytes(2);
                        i++;
                    } else {
                        throw new IllegalArgumentException("Unsupported media rate.");
                    }
                }
                return Pair.create(editListDurations, editListMediaTimes);
            }
        }
        return Pair.create((Object) null, (Object) null);
    }

    private static float parsePaspFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8);
        return ((float) parent.readUnsignedIntToInt()) / ((float) parent.readUnsignedIntToInt());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: byte[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseAudioSampleEntry(com.google.android.exoplayer2.util.ParsableByteArray r29, int r30, int r31, int r32, int r33, java.lang.String r34, boolean r35, com.google.android.exoplayer2.drm.DrmInitData r36, com.google.android.exoplayer2.extractor.mp4.AtomParsers.StsdData r37, int r38) throws com.google.android.exoplayer2.ParserException {
        /*
            r0 = r29
            r1 = r31
            r2 = r32
            r15 = r34
            r3 = r36
            r14 = r37
            int r4 = r1 + 8
            r5 = 8
            int r4 = r4 + r5
            r0.setPosition(r4)
            r4 = 0
            r6 = 6
            if (r35 == 0) goto L_0x0021
            int r4 = r29.readUnsignedShort()
            r0.skipBytes(r6)
            r13 = r4
            goto L_0x0025
        L_0x0021:
            r0.skipBytes(r5)
            r13 = r4
        L_0x0025:
            r12 = 2
            r4 = 16
            r11 = 1
            if (r13 == 0) goto L_0x0047
            if (r13 != r11) goto L_0x002e
            goto L_0x0047
        L_0x002e:
            if (r13 != r12) goto L_0x0046
            r0.skipBytes(r4)
            double r4 = r29.readDouble()
            long r4 = java.lang.Math.round(r4)
            int r5 = (int) r4
            int r4 = r29.readUnsignedIntToInt()
            r6 = 20
            r0.skipBytes(r6)
            goto L_0x0059
        L_0x0046:
            return
        L_0x0047:
            int r5 = r29.readUnsignedShort()
            r0.skipBytes(r6)
            int r6 = r29.readUnsignedFixedPoint1616()
            if (r13 != r11) goto L_0x0057
            r0.skipBytes(r4)
        L_0x0057:
            r4 = r5
            r5 = r6
        L_0x0059:
            int r6 = r29.getPosition()
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_enca
            r16 = 0
            r8 = r30
            if (r8 != r7) goto L_0x0091
            android.util.Pair r7 = parseSampleEntryEncryptionData(r0, r1, r2)
            if (r7 == 0) goto L_0x008b
            java.lang.Object r9 = r7.first
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r8 = r9.intValue()
            if (r3 != 0) goto L_0x0078
            r9 = r16
            goto L_0x0082
        L_0x0078:
            java.lang.Object r9 = r7.second
            com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox r9 = (com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox) r9
            java.lang.String r9 = r9.schemeType
            com.google.android.exoplayer2.drm.DrmInitData r9 = r3.copyWithSchemeType(r9)
        L_0x0082:
            r3 = r9
            com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox[] r9 = r14.trackEncryptionBoxes
            java.lang.Object r10 = r7.second
            com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox r10 = (com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox) r10
            r9[r38] = r10
        L_0x008b:
            r0.setPosition(r6)
            r9 = r3
            r10 = r8
            goto L_0x0093
        L_0x0091:
            r9 = r3
            r10 = r8
        L_0x0093:
            r3 = 0
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ac_3
            if (r10 != r7) goto L_0x009c
            java.lang.String r3 = "audio/ac3"
            goto L_0x0101
        L_0x009c:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ec_3
            if (r10 != r7) goto L_0x00a4
            java.lang.String r3 = "audio/eac3"
            goto L_0x0101
        L_0x00a4:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dtsc
            if (r10 != r7) goto L_0x00ab
            java.lang.String r3 = "audio/vnd.dts"
            goto L_0x0101
        L_0x00ab:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dtsh
            if (r10 == r7) goto L_0x00ff
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dtsl
            if (r10 != r7) goto L_0x00b4
            goto L_0x00ff
        L_0x00b4:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dtse
            if (r10 != r7) goto L_0x00bb
            java.lang.String r3 = "audio/vnd.dts.hd;profile=lbr"
            goto L_0x0101
        L_0x00bb:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_samr
            if (r10 != r7) goto L_0x00c2
            java.lang.String r3 = "audio/3gpp"
            goto L_0x0101
        L_0x00c2:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_sawb
            if (r10 != r7) goto L_0x00c9
            java.lang.String r3 = "audio/amr-wb"
            goto L_0x0101
        L_0x00c9:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_lpcm
            if (r10 == r7) goto L_0x00fc
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_sowt
            if (r10 != r7) goto L_0x00d2
            goto L_0x00fc
        L_0x00d2:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE__mp3
            if (r10 != r7) goto L_0x00d9
            java.lang.String r3 = "audio/mpeg"
            goto L_0x0101
        L_0x00d9:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_alac
            if (r10 != r7) goto L_0x00e0
            java.lang.String r3 = "audio/alac"
            goto L_0x0101
        L_0x00e0:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_alaw
            if (r10 != r7) goto L_0x00e7
            java.lang.String r3 = "audio/g711-alaw"
            goto L_0x0101
        L_0x00e7:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ulaw
            if (r10 != r7) goto L_0x00ee
            java.lang.String r3 = "audio/g711-mlaw"
            goto L_0x0101
        L_0x00ee:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_Opus
            if (r10 != r7) goto L_0x00f5
            java.lang.String r3 = "audio/opus"
            goto L_0x0101
        L_0x00f5:
            int r7 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_fLaC
            if (r10 != r7) goto L_0x0101
            java.lang.String r3 = "audio/flac"
            goto L_0x0101
        L_0x00fc:
            java.lang.String r3 = "audio/raw"
            goto L_0x0101
        L_0x00ff:
            java.lang.String r3 = "audio/vnd.dts.hd"
        L_0x0101:
            r7 = 0
            r8 = r3
            r17 = r4
            r18 = r5
            r19 = r7
            r7 = r6
        L_0x010a:
            int r3 = r7 - r1
            r4 = -1
            if (r3 >= r2) goto L_0x0286
            r0.setPosition(r7)
            int r6 = r29.readInt()
            r3 = 0
            if (r6 <= 0) goto L_0x011b
            r5 = 1
            goto L_0x011c
        L_0x011b:
            r5 = 0
        L_0x011c:
            java.lang.String r11 = "childAtomSize should be positive"
            com.google.android.exoplayer2.util.Assertions.checkArgument(r5, r11)
            int r11 = r29.readInt()
            int r5 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_esds
            if (r11 == r5) goto L_0x022e
            if (r35 == 0) goto L_0x013f
            int r5 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_wave
            if (r11 != r5) goto L_0x013f
            r27 = r8
            r21 = r9
            r22 = r10
            r5 = r11
            r24 = r13
            r20 = 1
            r23 = 2
            r13 = r7
            goto L_0x023c
        L_0x013f:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dac3
            if (r11 != r4) goto L_0x0162
            int r3 = r7 + 8
            r0.setPosition(r3)
            java.lang.String r3 = java.lang.Integer.toString(r33)
            com.google.android.exoplayer2.Format r3 = com.google.android.exoplayer2.audio.Ac3Util.parseAc3AnnexFFormat(r0, r3, r15, r9)
            r14.format = r3
            r27 = r8
            r21 = r9
            r22 = r10
            r5 = r11
            r24 = r13
            r20 = 1
            r23 = 2
            r13 = r7
            goto L_0x022b
        L_0x0162:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dec3
            if (r11 != r4) goto L_0x0185
            int r3 = r7 + 8
            r0.setPosition(r3)
            java.lang.String r3 = java.lang.Integer.toString(r33)
            com.google.android.exoplayer2.Format r3 = com.google.android.exoplayer2.audio.Ac3Util.parseEAc3AnnexFFormat(r0, r3, r15, r9)
            r14.format = r3
            r27 = r8
            r21 = r9
            r22 = r10
            r5 = r11
            r24 = r13
            r20 = 1
            r23 = 2
            r13 = r7
            goto L_0x022b
        L_0x0185:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_ddts
            if (r11 != r4) goto L_0x01c7
            java.lang.String r3 = java.lang.Integer.toString(r33)
            r5 = 0
            r21 = -1
            r22 = -1
            r23 = 0
            r24 = 0
            r4 = r8
            r25 = r6
            r6 = r21
            r26 = r7
            r7 = r22
            r27 = r8
            r8 = r17
            r21 = r9
            r9 = r18
            r22 = r10
            r10 = r23
            r28 = r11
            r20 = 1
            r11 = r21
            r23 = 2
            r12 = r24
            r24 = r13
            r13 = r34
            com.google.android.exoplayer2.Format r3 = com.google.android.exoplayer2.Format.createAudioSampleFormat(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r14.format = r3
            r6 = r25
            r13 = r26
            r5 = r28
            goto L_0x022b
        L_0x01c7:
            r25 = r6
            r26 = r7
            r27 = r8
            r21 = r9
            r22 = r10
            r28 = r11
            r24 = r13
            r20 = 1
            r23 = 2
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_alac
            r5 = r28
            if (r5 != r4) goto L_0x01f1
            r6 = r25
            byte[] r4 = new byte[r6]
            r13 = r26
            r0.setPosition(r13)
            r0.readBytes(r4, r3, r6)
            r19 = r4
            r8 = r27
            goto L_0x027a
        L_0x01f1:
            r6 = r25
            r13 = r26
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dOps
            if (r5 != r4) goto L_0x0216
            int r4 = r6 + -8
            byte[] r7 = opusMagic
            int r8 = r7.length
            int r8 = r8 + r4
            byte[] r8 = new byte[r8]
            int r9 = r7.length
            java.lang.System.arraycopy(r7, r3, r8, r3, r9)
            int r7 = r13 + 8
            r0.setPosition(r7)
            byte[] r3 = opusMagic
            int r3 = r3.length
            r0.readBytes(r8, r3, r4)
            r19 = r8
            r8 = r27
            goto L_0x027a
        L_0x0216:
            int r4 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_dfLa
            if (r6 != r4) goto L_0x022b
            int r4 = r6 + -12
            byte[] r7 = new byte[r4]
            int r8 = r13 + 12
            r0.setPosition(r8)
            r0.readBytes(r7, r3, r4)
            r19 = r7
            r8 = r27
            goto L_0x027a
        L_0x022b:
            r8 = r27
            goto L_0x027a
        L_0x022e:
            r27 = r8
            r21 = r9
            r22 = r10
            r5 = r11
            r24 = r13
            r20 = 1
            r23 = 2
            r13 = r7
        L_0x023c:
            int r3 = com.google.android.exoplayer2.extractor.mp4.Atom.TYPE_esds
            if (r5 != r3) goto L_0x0242
            r7 = r13
            goto L_0x0246
        L_0x0242:
            int r7 = findEsdsPosition(r0, r13, r6)
        L_0x0246:
            r3 = r7
            if (r3 == r4) goto L_0x0277
            android.util.Pair r4 = parseEsdsFromParent(r0, r3)
            java.lang.Object r7 = r4.first
            r8 = r7
            java.lang.String r8 = (java.lang.String) r8
            java.lang.Object r7 = r4.second
            r19 = r7
            byte[] r19 = (byte[]) r19
            java.lang.String r7 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x0279
            android.util.Pair r7 = com.google.android.exoplayer2.util.CodecSpecificDataUtil.parseAacAudioSpecificConfig(r19)
            java.lang.Object r9 = r7.first
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r18 = r9.intValue()
            java.lang.Object r9 = r7.second
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r17 = r9.intValue()
            goto L_0x0279
        L_0x0277:
            r8 = r27
        L_0x0279:
        L_0x027a:
            int r7 = r13 + r6
            r9 = r21
            r10 = r22
            r13 = r24
            r11 = 1
            r12 = 2
            goto L_0x010a
        L_0x0286:
            r27 = r8
            r21 = r9
            r22 = r10
            r24 = r13
            r23 = 2
            r13 = r7
            com.google.android.exoplayer2.Format r3 = r14.format
            if (r3 != 0) goto L_0x02d5
            r12 = r27
            if (r12 == 0) goto L_0x02cf
            java.lang.String r3 = "audio/raw"
            boolean r3 = r3.equals(r12)
            if (r3 == 0) goto L_0x02a4
            r10 = 2
            goto L_0x02a5
        L_0x02a4:
            r10 = -1
        L_0x02a5:
            java.lang.String r3 = java.lang.Integer.toString(r33)
            r5 = 0
            r6 = -1
            r7 = -1
            if (r19 != 0) goto L_0x02b1
            r11 = r16
            goto L_0x02b6
        L_0x02b1:
            java.util.List r4 = java.util.Collections.singletonList(r19)
            r11 = r4
        L_0x02b6:
            r16 = 0
            r4 = r12
            r8 = r17
            r9 = r18
            r20 = r12
            r12 = r21
            r23 = r13
            r13 = r16
            r0 = r14
            r14 = r34
            com.google.android.exoplayer2.Format r3 = com.google.android.exoplayer2.Format.createAudioSampleFormat(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            r0.format = r3
            goto L_0x02da
        L_0x02cf:
            r20 = r12
            r23 = r13
            r0 = r14
            goto L_0x02da
        L_0x02d5:
            r23 = r13
            r0 = r14
            r20 = r27
        L_0x02da:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.AtomParsers.parseAudioSampleEntry(com.google.android.exoplayer2.util.ParsableByteArray, int, int, int, int, java.lang.String, boolean, com.google.android.exoplayer2.drm.DrmInitData, com.google.android.exoplayer2.extractor.mp4.AtomParsers$StsdData, int):void");
    }

    private static int findEsdsPosition(ParsableByteArray parent, int position, int size) {
        int childAtomPosition = parent.getPosition();
        while (childAtomPosition - position < size) {
            parent.setPosition(childAtomPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_esds) {
                return childAtomPosition;
            }
            childAtomPosition += childAtomSize;
        }
        return -1;
    }

    private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray parent, int position) {
        parent.setPosition(position + 8 + 4);
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        parent.skipBytes(2);
        int flags = parent.readUnsignedByte();
        if ((flags & 128) != 0) {
            parent.skipBytes(2);
        }
        if ((flags & 64) != 0) {
            parent.skipBytes(parent.readUnsignedShort());
        }
        if ((flags & 32) != 0) {
            parent.skipBytes(2);
        }
        parent.skipBytes(1);
        parseExpandableClassSize(parent);
        String mimeType = MimeTypes.getMimeTypeFromMp4ObjectType(parent.readUnsignedByte());
        if (MimeTypes.AUDIO_MPEG.equals(mimeType) || MimeTypes.AUDIO_DTS.equals(mimeType) || MimeTypes.AUDIO_DTS_HD.equals(mimeType)) {
            return Pair.create(mimeType, (Object) null);
        }
        parent.skipBytes(12);
        parent.skipBytes(1);
        int initializationDataSize = parseExpandableClassSize(parent);
        byte[] initializationData = new byte[initializationDataSize];
        parent.readBytes(initializationData, 0, initializationDataSize);
        return Pair.create(mimeType, initializationData);
    }

    private static Pair<Integer, TrackEncryptionBox> parseSampleEntryEncryptionData(ParsableByteArray parent, int position, int size) {
        Pair<Integer, TrackEncryptionBox> result;
        int childPosition = parent.getPosition();
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            Assertions.checkArgument(childAtomSize > 0, "childAtomSize should be positive");
            if (parent.readInt() == Atom.TYPE_sinf && (result = parseCommonEncryptionSinfFromParent(parent, childPosition, childAtomSize)) != null) {
                return result;
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    static Pair<Integer, TrackEncryptionBox> parseCommonEncryptionSinfFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        int schemeInformationBoxPosition = -1;
        int schemeInformationBoxSize = 0;
        String schemeType = null;
        Integer dataFormat = null;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            int childAtomType = parent.readInt();
            if (childAtomType == Atom.TYPE_frma) {
                dataFormat = Integer.valueOf(parent.readInt());
            } else if (childAtomType == Atom.TYPE_schm) {
                parent.skipBytes(4);
                schemeType = parent.readString(4);
            } else if (childAtomType == Atom.TYPE_schi) {
                schemeInformationBoxPosition = childPosition;
                schemeInformationBoxSize = childAtomSize;
            }
            childPosition += childAtomSize;
        }
        if (!C.CENC_TYPE_cenc.equals(schemeType) && !C.CENC_TYPE_cbc1.equals(schemeType) && !C.CENC_TYPE_cens.equals(schemeType) && !C.CENC_TYPE_cbcs.equals(schemeType)) {
            return null;
        }
        boolean z = true;
        Assertions.checkArgument(dataFormat != null, "frma atom is mandatory");
        Assertions.checkArgument(schemeInformationBoxPosition != -1, "schi atom is mandatory");
        TrackEncryptionBox encryptionBox = parseSchiFromParent(parent, schemeInformationBoxPosition, schemeInformationBoxSize, schemeType);
        if (encryptionBox == null) {
            z = false;
        }
        Assertions.checkArgument(z, "tenc atom is mandatory");
        return Pair.create(dataFormat, encryptionBox);
    }

    private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray parent, int position, int size, String schemeType) {
        byte[] constantIv;
        ParsableByteArray parsableByteArray = parent;
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parsableByteArray.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_tenc) {
                int version = Atom.parseFullAtomVersion(parent.readInt());
                boolean defaultIsProtected = true;
                parsableByteArray.skipBytes(1);
                int defaultCryptByteBlock = 0;
                int defaultSkipByteBlock = 0;
                if (version == 0) {
                    parsableByteArray.skipBytes(1);
                } else {
                    int patternByte = parent.readUnsignedByte();
                    defaultCryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                    defaultSkipByteBlock = patternByte & 15;
                }
                if (parent.readUnsignedByte() != 1) {
                    defaultIsProtected = false;
                }
                int defaultPerSampleIvSize = parent.readUnsignedByte();
                byte[] defaultKeyId = new byte[16];
                parsableByteArray.readBytes(defaultKeyId, 0, defaultKeyId.length);
                if (!defaultIsProtected || defaultPerSampleIvSize != 0) {
                    constantIv = null;
                } else {
                    int constantIvSize = parent.readUnsignedByte();
                    byte[] constantIv2 = new byte[constantIvSize];
                    parsableByteArray.readBytes(constantIv2, 0, constantIvSize);
                    constantIv = constantIv2;
                }
                byte[] bArr = defaultKeyId;
                return new TrackEncryptionBox(defaultIsProtected, schemeType, defaultPerSampleIvSize, defaultKeyId, defaultCryptByteBlock, defaultSkipByteBlock, constantIv);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static byte[] parseProjFromParent(ParsableByteArray parent, int position, int size) {
        int childPosition = position + 8;
        while (childPosition - position < size) {
            parent.setPosition(childPosition);
            int childAtomSize = parent.readInt();
            if (parent.readInt() == Atom.TYPE_proj) {
                return Arrays.copyOfRange(parent.data, childPosition, childPosition + childAtomSize);
            }
            childPosition += childAtomSize;
        }
        return null;
    }

    private static int parseExpandableClassSize(ParsableByteArray data) {
        int currentByte = data.readUnsignedByte();
        int size = currentByte & 127;
        while ((currentByte & 128) == 128) {
            currentByte = data.readUnsignedByte();
            size = (size << 7) | (currentByte & 127);
        }
        return size;
    }

    private static boolean canApplyEditWithGaplessInfo(long[] timestamps, long duration, long editStartTime, long editEndTime) {
        int lastIndex = timestamps.length - 1;
        int latestDelayIndex = Util.constrainValue(3, 0, lastIndex);
        int earliestPaddingIndex = Util.constrainValue(timestamps.length - 3, 0, lastIndex);
        if (timestamps[0] > editStartTime || editStartTime >= timestamps[latestDelayIndex] || timestamps[earliestPaddingIndex] >= editEndTime || editEndTime > duration) {
            return false;
        }
        return true;
    }

    private AtomParsers() {
    }

    private static final class ChunkIterator {
        private final ParsableByteArray chunkOffsets;
        private final boolean chunkOffsetsAreLongs;
        public int index;
        public final int length;
        private int nextSamplesPerChunkChangeIndex;
        public int numSamples;
        public long offset;
        private int remainingSamplesPerChunkChanges;
        private final ParsableByteArray stsc;

        public ChunkIterator(ParsableByteArray stsc2, ParsableByteArray chunkOffsets2, boolean chunkOffsetsAreLongs2) {
            this.stsc = stsc2;
            this.chunkOffsets = chunkOffsets2;
            this.chunkOffsetsAreLongs = chunkOffsetsAreLongs2;
            chunkOffsets2.setPosition(12);
            this.length = chunkOffsets2.readUnsignedIntToInt();
            stsc2.setPosition(12);
            this.remainingSamplesPerChunkChanges = stsc2.readUnsignedIntToInt();
            Assertions.checkState(stsc2.readInt() != 1 ? false : true, "first_chunk must be 1");
            this.index = -1;
        }

        public boolean moveNext() {
            long j;
            int i = this.index + 1;
            this.index = i;
            if (i == this.length) {
                return false;
            }
            if (this.chunkOffsetsAreLongs) {
                j = this.chunkOffsets.readUnsignedLongToLong();
            } else {
                j = this.chunkOffsets.readUnsignedInt();
            }
            this.offset = j;
            if (this.index == this.nextSamplesPerChunkChangeIndex) {
                this.numSamples = this.stsc.readUnsignedIntToInt();
                this.stsc.skipBytes(4);
                int i2 = this.remainingSamplesPerChunkChanges - 1;
                this.remainingSamplesPerChunkChanges = i2;
                this.nextSamplesPerChunkChangeIndex = i2 > 0 ? this.stsc.readUnsignedIntToInt() - 1 : -1;
            }
            return true;
        }
    }

    private static final class TkhdData {
        /* access modifiers changed from: private */
        public final long duration;
        /* access modifiers changed from: private */
        public final int id;
        /* access modifiers changed from: private */
        public final int rotationDegrees;

        public TkhdData(int id2, long duration2, int rotationDegrees2) {
            this.id = id2;
            this.duration = duration2;
            this.rotationDegrees = rotationDegrees2;
        }
    }

    private static final class StsdData {
        public static final int STSD_HEADER_SIZE = 8;
        public Format format;
        public int nalUnitLengthFieldLength;
        public int requiredSampleTransformation = 0;
        public final TrackEncryptionBox[] trackEncryptionBoxes;

        public StsdData(int numberOfEntries) {
            this.trackEncryptionBoxes = new TrackEncryptionBox[numberOfEntries];
        }
    }

    static final class StszSampleSizeBox implements SampleSizeBox {
        private final ParsableByteArray data;
        private final int fixedSampleSize = this.data.readUnsignedIntToInt();
        private final int sampleCount = this.data.readUnsignedIntToInt();

        public StszSampleSizeBox(Atom.LeafAtom stszAtom) {
            ParsableByteArray parsableByteArray = stszAtom.data;
            this.data = parsableByteArray;
            parsableByteArray.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            int i = this.fixedSampleSize;
            return i == 0 ? this.data.readUnsignedIntToInt() : i;
        }

        public boolean isFixedSampleSize() {
            return this.fixedSampleSize != 0;
        }
    }

    static final class Stz2SampleSizeBox implements SampleSizeBox {
        private int currentByte;
        private final ParsableByteArray data;
        private final int fieldSize = (this.data.readUnsignedIntToInt() & 255);
        private final int sampleCount = this.data.readUnsignedIntToInt();
        private int sampleIndex;

        public Stz2SampleSizeBox(Atom.LeafAtom stz2Atom) {
            ParsableByteArray parsableByteArray = stz2Atom.data;
            this.data = parsableByteArray;
            parsableByteArray.setPosition(12);
        }

        public int getSampleCount() {
            return this.sampleCount;
        }

        public int readNextSampleSize() {
            int i = this.fieldSize;
            if (i == 8) {
                return this.data.readUnsignedByte();
            }
            if (i == 16) {
                return this.data.readUnsignedShort();
            }
            int i2 = this.sampleIndex;
            this.sampleIndex = i2 + 1;
            if (i2 % 2 != 0) {
                return this.currentByte & 15;
            }
            int readUnsignedByte = this.data.readUnsignedByte();
            this.currentByte = readUnsignedByte;
            return (readUnsignedByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
        }

        public boolean isFixedSampleSize() {
            return false;
        }
    }
}
