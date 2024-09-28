package com.google.android.exoplayer2.metadata.id3;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kotlin.UByte;

public final class Id3Decoder implements MetadataDecoder {
    private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
    private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
    private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
    private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
    private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
    private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
    private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
    private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
    public static final int ID3_HEADER_LENGTH = 10;
    public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
    public static final FramePredicate NO_FRAMES_PREDICATE = $$Lambda$Id3Decoder$7M0gBIGKaTbyTVXWCb62bIHyc.INSTANCE;
    private static final String TAG = "Id3Decoder";
    private final FramePredicate framePredicate;

    public interface FramePredicate {
        boolean evaluate(int i, int i2, int i3, int i4, int i5);
    }

    static /* synthetic */ boolean lambda$static$0(int majorVersion, int id0, int id1, int id2, int id3) {
        return false;
    }

    public Id3Decoder() {
        this((FramePredicate) null);
    }

    public Id3Decoder(FramePredicate framePredicate2) {
        this.framePredicate = framePredicate2;
    }

    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        return decode(buffer.array(), buffer.limit());
    }

    public Metadata decode(byte[] data, int size) {
        List<Id3Frame> id3Frames = new ArrayList<>();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        Id3Header id3Header = decodeHeader(id3Data);
        if (id3Header == null) {
            return null;
        }
        int startPosition = id3Data.getPosition();
        int frameHeaderSize = id3Header.majorVersion == 2 ? 6 : 10;
        int framesSize = id3Header.framesSize;
        if (id3Header.isUnsynchronized) {
            framesSize = removeUnsynchronization(id3Data, id3Header.framesSize);
        }
        id3Data.setLimit(startPosition + framesSize);
        boolean unsignedIntFrameSizeHack = false;
        if (!validateFrames(id3Data, id3Header.majorVersion, frameHeaderSize, false)) {
            if (id3Header.majorVersion != 4 || !validateFrames(id3Data, 4, frameHeaderSize, true)) {
                Log.w(TAG, "Failed to validate ID3 tag with majorVersion=" + id3Header.majorVersion);
                return null;
            }
            unsignedIntFrameSizeHack = true;
        }
        while (id3Data.bytesLeft() >= frameHeaderSize) {
            Id3Frame frame = decodeFrame(id3Header.majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, this.framePredicate);
            if (frame != null) {
                id3Frames.add(frame);
            }
        }
        return new Metadata((List<? extends Metadata.Entry>) id3Frames);
    }

    private static Id3Header decodeHeader(ParsableByteArray data) {
        if (data.bytesLeft() < 10) {
            Log.w(TAG, "Data too short to be an ID3 tag");
            return null;
        }
        int id = data.readUnsignedInt24();
        if (id != ID3_TAG) {
            Log.w(TAG, "Unexpected first three bytes of ID3 tag header: " + id);
            return null;
        }
        int majorVersion = data.readUnsignedByte();
        boolean isUnsynchronized = true;
        data.skipBytes(1);
        int flags = data.readUnsignedByte();
        int framesSize = data.readSynchSafeInt();
        if (majorVersion == 2) {
            if ((flags & 64) != 0) {
                Log.w(TAG, "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
                return null;
            }
        } else if (majorVersion == 3) {
            if ((flags & 64) != 0) {
                int extendedHeaderSize = data.readInt();
                data.skipBytes(extendedHeaderSize);
                framesSize -= extendedHeaderSize + 4;
            }
        } else if (majorVersion == 4) {
            if ((flags & 64) != 0) {
                int extendedHeaderSize2 = data.readSynchSafeInt();
                data.skipBytes(extendedHeaderSize2 - 4);
                framesSize -= extendedHeaderSize2;
            }
            if ((flags & 16) != 0) {
                framesSize -= 10;
            }
        } else {
            Log.w(TAG, "Skipped ID3 tag with unsupported majorVersion=" + majorVersion);
            return null;
        }
        if (majorVersion >= 4 || (flags & 128) == 0) {
            isUnsynchronized = false;
        }
        return new Id3Header(majorVersion, isUnsynchronized, framesSize);
    }

    private static boolean validateFrames(ParsableByteArray id3Data, int majorVersion, int frameHeaderSize, boolean unsignedIntFrameSizeHack) {
        int flags;
        long frameSize;
        int id;
        ParsableByteArray parsableByteArray = id3Data;
        int i = majorVersion;
        int startPosition = id3Data.getPosition();
        while (true) {
            try {
                boolean z = true;
                if (id3Data.bytesLeft() >= frameHeaderSize) {
                    if (i >= 3) {
                        try {
                            id = id3Data.readInt();
                            frameSize = id3Data.readUnsignedInt();
                            flags = id3Data.readUnsignedShort();
                        } catch (Throwable th) {
                            th = th;
                        }
                    } else {
                        id = id3Data.readUnsignedInt24();
                        frameSize = (long) id3Data.readUnsignedInt24();
                        flags = 0;
                    }
                    if (id == 0 && frameSize == 0 && flags == 0) {
                        parsableByteArray.setPosition(startPosition);
                        return true;
                    }
                    if (i == 4 && !unsignedIntFrameSizeHack) {
                        if ((8421504 & frameSize) != 0) {
                            parsableByteArray.setPosition(startPosition);
                            return false;
                        }
                        frameSize = (frameSize & 255) | (((frameSize >> 8) & 255) << 7) | (((frameSize >> 16) & 255) << 14) | (((frameSize >> 24) & 255) << 21);
                    }
                    boolean hasGroupIdentifier = false;
                    boolean hasDataLength = false;
                    if (i == 4) {
                        hasGroupIdentifier = (flags & 64) != 0;
                        if ((flags & 1) == 0) {
                            z = false;
                        }
                        hasDataLength = z;
                    } else if (i == 3) {
                        hasGroupIdentifier = (flags & 32) != 0;
                        if ((flags & 128) == 0) {
                            z = false;
                        }
                        hasDataLength = z;
                    }
                    int minimumFrameSize = 0;
                    if (hasGroupIdentifier) {
                        minimumFrameSize = 0 + 1;
                    }
                    if (hasDataLength) {
                        minimumFrameSize += 4;
                    }
                    if (frameSize < ((long) minimumFrameSize)) {
                        parsableByteArray.setPosition(startPosition);
                        return false;
                    } else if (((long) id3Data.bytesLeft()) < frameSize) {
                        parsableByteArray.setPosition(startPosition);
                        return false;
                    } else {
                        parsableByteArray.skipBytes((int) frameSize);
                    }
                } else {
                    parsableByteArray.setPosition(startPosition);
                    return true;
                }
            } catch (Throwable th2) {
                th = th2;
                int i2 = frameHeaderSize;
                parsableByteArray.setPosition(startPosition);
                throw th;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01bc, code lost:
        if (r13 == 67) goto L_0x01be;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.google.android.exoplayer2.metadata.id3.Id3Frame decodeFrame(int r23, com.google.android.exoplayer2.util.ParsableByteArray r24, boolean r25, int r26, com.google.android.exoplayer2.metadata.id3.Id3Decoder.FramePredicate r27) {
        /*
            r7 = r23
            r8 = r24
            int r9 = r24.readUnsignedByte()
            int r10 = r24.readUnsignedByte()
            int r11 = r24.readUnsignedByte()
            r0 = 3
            if (r7 < r0) goto L_0x0018
            int r1 = r24.readUnsignedByte()
            goto L_0x0019
        L_0x0018:
            r1 = 0
        L_0x0019:
            r13 = r1
            r14 = 4
            if (r7 != r14) goto L_0x003f
            int r1 = r24.readUnsignedIntToInt()
            if (r25 != 0) goto L_0x003d
            r2 = r1 & 255(0xff, float:3.57E-43)
            int r3 = r1 >> 8
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 7
            r2 = r2 | r3
            int r3 = r1 >> 16
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 14
            r2 = r2 | r3
            int r3 = r1 >> 24
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 21
            r1 = r2 | r3
            r15 = r1
            goto L_0x004c
        L_0x003d:
            r15 = r1
            goto L_0x004c
        L_0x003f:
            if (r7 != r0) goto L_0x0047
            int r1 = r24.readUnsignedIntToInt()
            r15 = r1
            goto L_0x004c
        L_0x0047:
            int r1 = r24.readUnsignedInt24()
            r15 = r1
        L_0x004c:
            if (r7 < r0) goto L_0x0053
            int r1 = r24.readUnsignedShort()
            goto L_0x0054
        L_0x0053:
            r1 = 0
        L_0x0054:
            r6 = r1
            r16 = 0
            if (r9 != 0) goto L_0x006b
            if (r10 != 0) goto L_0x006b
            if (r11 != 0) goto L_0x006b
            if (r13 != 0) goto L_0x006b
            if (r15 != 0) goto L_0x006b
            if (r6 != 0) goto L_0x006b
            int r0 = r24.limit()
            r8.setPosition(r0)
            return r16
        L_0x006b:
            int r1 = r24.getPosition()
            int r5 = r1 + r15
            int r1 = r24.limit()
            java.lang.String r4 = "Id3Decoder"
            if (r5 <= r1) goto L_0x0086
            java.lang.String r0 = "Frame size exceeds remaining tag data"
            com.google.android.exoplayer2.util.Log.w(r4, r0)
            int r0 = r24.limit()
            r8.setPosition(r0)
            return r16
        L_0x0086:
            if (r27 == 0) goto L_0x009f
            r1 = r27
            r2 = r23
            r3 = r9
            r12 = r4
            r4 = r10
            r14 = r5
            r5 = r11
            r18 = r12
            r12 = r6
            r6 = r13
            boolean r1 = r1.evaluate(r2, r3, r4, r5, r6)
            if (r1 != 0) goto L_0x00a3
            r8.setPosition(r14)
            return r16
        L_0x009f:
            r18 = r4
            r14 = r5
            r12 = r6
        L_0x00a3:
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 1
            if (r7 != r0) goto L_0x00d2
            r0 = r12 & 128(0x80, float:1.794E-43)
            if (r0 == 0) goto L_0x00b1
            r0 = 1
            goto L_0x00b2
        L_0x00b1:
            r0 = 0
        L_0x00b2:
            r1 = r0
            r0 = r12 & 64
            if (r0 == 0) goto L_0x00b9
            r0 = 1
            goto L_0x00ba
        L_0x00b9:
            r0 = 0
        L_0x00ba:
            r2 = r0
            r0 = r12 & 32
            if (r0 == 0) goto L_0x00c2
            r17 = 1
            goto L_0x00c4
        L_0x00c2:
            r17 = 0
        L_0x00c4:
            r5 = r17
            r4 = r1
            r17 = r1
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r5
            goto L_0x0115
        L_0x00d2:
            r0 = 4
            if (r7 != r0) goto L_0x010b
            r0 = r12 & 64
            if (r0 == 0) goto L_0x00db
            r0 = 1
            goto L_0x00dc
        L_0x00db:
            r0 = 0
        L_0x00dc:
            r5 = r0
            r0 = r12 & 8
            if (r0 == 0) goto L_0x00e3
            r0 = 1
            goto L_0x00e4
        L_0x00e3:
            r0 = 0
        L_0x00e4:
            r1 = r0
            r0 = r12 & 4
            if (r0 == 0) goto L_0x00eb
            r0 = 1
            goto L_0x00ec
        L_0x00eb:
            r0 = 0
        L_0x00ec:
            r2 = r0
            r0 = r12 & 2
            if (r0 == 0) goto L_0x00f3
            r0 = 1
            goto L_0x00f4
        L_0x00f3:
            r0 = 0
        L_0x00f4:
            r3 = r0
            r0 = r12 & 1
            if (r0 == 0) goto L_0x00fc
            r17 = 1
            goto L_0x00fe
        L_0x00fc:
            r17 = 0
        L_0x00fe:
            r4 = r17
            r17 = r1
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r5
            goto L_0x0115
        L_0x010b:
            r17 = r1
            r19 = r2
            r20 = r3
            r21 = r4
            r22 = r5
        L_0x0115:
            if (r17 != 0) goto L_0x0259
            if (r19 == 0) goto L_0x011d
            r2 = r18
            goto L_0x025b
        L_0x011d:
            if (r22 == 0) goto L_0x0124
            int r15 = r15 + -1
            r8.skipBytes(r6)
        L_0x0124:
            if (r21 == 0) goto L_0x012c
            int r15 = r15 + -4
            r0 = 4
            r8.skipBytes(r0)
        L_0x012c:
            if (r20 == 0) goto L_0x0132
            int r15 = removeUnsynchronization(r8, r15)
        L_0x0132:
            r0 = 84
            r1 = 2
            r2 = 88
            if (r9 != r0) goto L_0x0147
            if (r10 != r2) goto L_0x0147
            if (r11 != r2) goto L_0x0147
            if (r7 == r1) goto L_0x0141
            if (r13 != r2) goto L_0x0147
        L_0x0141:
            com.google.android.exoplayer2.metadata.id3.TextInformationFrame r0 = decodeTxxxFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x0147:
            if (r9 != r0) goto L_0x015c
            java.lang.String r0 = getFrameId(r7, r9, r10, r11, r13)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            com.google.android.exoplayer2.metadata.id3.TextInformationFrame r1 = decodeTextInformationFrame(r8, r15, r0)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r0 = r1
            goto L_0x021f
        L_0x0154:
            r0 = move-exception
            goto L_0x0255
        L_0x0157:
            r0 = move-exception
            r2 = r18
            goto L_0x024b
        L_0x015c:
            r3 = 87
            if (r9 != r3) goto L_0x016e
            if (r10 != r2) goto L_0x016e
            if (r11 != r2) goto L_0x016e
            if (r7 == r1) goto L_0x0168
            if (r13 != r2) goto L_0x016e
        L_0x0168:
            com.google.android.exoplayer2.metadata.id3.UrlLinkFrame r0 = decodeWxxxFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x016e:
            if (r9 != r3) goto L_0x017b
            java.lang.String r0 = getFrameId(r7, r9, r10, r11, r13)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            com.google.android.exoplayer2.metadata.id3.UrlLinkFrame r1 = decodeUrlLinkFrame(r8, r15, r0)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r0 = r1
            goto L_0x021f
        L_0x017b:
            r2 = 73
            r3 = 80
            if (r9 != r3) goto L_0x0191
            r4 = 82
            if (r10 != r4) goto L_0x0191
            if (r11 != r2) goto L_0x0191
            r4 = 86
            if (r13 != r4) goto L_0x0191
            com.google.android.exoplayer2.metadata.id3.PrivFrame r0 = decodePrivFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x0191:
            r4 = 71
            r5 = 79
            if (r9 != r4) goto L_0x01a9
            r4 = 69
            if (r10 != r4) goto L_0x01a9
            if (r11 != r5) goto L_0x01a9
            r4 = 66
            if (r13 == r4) goto L_0x01a3
            if (r7 != r1) goto L_0x01a9
        L_0x01a3:
            com.google.android.exoplayer2.metadata.id3.GeobFrame r0 = decodeGeobFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x01a9:
            r4 = 65
            r6 = 67
            if (r7 != r1) goto L_0x01b6
            if (r9 != r3) goto L_0x01c4
            if (r10 != r2) goto L_0x01c4
            if (r11 != r6) goto L_0x01c4
            goto L_0x01be
        L_0x01b6:
            if (r9 != r4) goto L_0x01c4
            if (r10 != r3) goto L_0x01c4
            if (r11 != r2) goto L_0x01c4
            if (r13 != r6) goto L_0x01c4
        L_0x01be:
            com.google.android.exoplayer2.metadata.id3.ApicFrame r0 = decodeApicFrame(r8, r15, r7)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x01c4:
            r2 = 77
            if (r9 != r6) goto L_0x01d5
            if (r10 != r5) goto L_0x01d5
            if (r11 != r2) goto L_0x01d5
            if (r13 == r2) goto L_0x01d0
            if (r7 != r1) goto L_0x01d5
        L_0x01d0:
            com.google.android.exoplayer2.metadata.id3.CommentFrame r0 = decodeCommentFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x01d5:
            if (r9 != r6) goto L_0x01ef
            r1 = 72
            if (r10 != r1) goto L_0x01ef
            if (r11 != r4) goto L_0x01ef
            if (r13 != r3) goto L_0x01ef
            r1 = r24
            r2 = r15
            r3 = r23
            r4 = r25
            r5 = r26
            r6 = r27
            com.google.android.exoplayer2.metadata.id3.ChapterFrame r0 = decodeChapterFrame(r1, r2, r3, r4, r5, r6)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x01ef:
            if (r9 != r6) goto L_0x0207
            if (r10 != r0) goto L_0x0207
            if (r11 != r5) goto L_0x0207
            if (r13 != r6) goto L_0x0207
            r1 = r24
            r2 = r15
            r3 = r23
            r4 = r25
            r5 = r26
            r6 = r27
            com.google.android.exoplayer2.metadata.id3.ChapterTocFrame r0 = decodeChapterTOCFrame(r1, r2, r3, r4, r5, r6)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x0207:
            if (r9 != r2) goto L_0x0216
            r1 = 76
            if (r10 != r1) goto L_0x0216
            if (r11 != r1) goto L_0x0216
            if (r13 != r0) goto L_0x0216
            com.google.android.exoplayer2.metadata.id3.MlltFrame r0 = decodeMlltFrame(r8, r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            goto L_0x021f
        L_0x0216:
            java.lang.String r0 = getFrameId(r7, r9, r10, r11, r13)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            com.google.android.exoplayer2.metadata.id3.BinaryFrame r1 = decodeBinaryFrame(r8, r15, r0)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r0 = r1
        L_0x021f:
            if (r0 != 0) goto L_0x0246
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r1.<init>()     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            java.lang.String r2 = "Failed to decode frame: id="
            r1.append(r2)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            java.lang.String r2 = getFrameId(r7, r9, r10, r11, r13)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r1.append(r2)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            java.lang.String r2 = ", frameSize="
            r1.append(r2)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r1.append(r15)     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            java.lang.String r1 = r1.toString()     // Catch:{ UnsupportedEncodingException -> 0x0157 }
            r2 = r18
            com.google.android.exoplayer2.util.Log.w(r2, r1)     // Catch:{ UnsupportedEncodingException -> 0x0244 }
            goto L_0x0246
        L_0x0244:
            r0 = move-exception
            goto L_0x024b
        L_0x0246:
            r8.setPosition(r14)
            return r0
        L_0x024b:
            java.lang.String r1 = "Unsupported character encoding"
            com.google.android.exoplayer2.util.Log.w(r2, r1)     // Catch:{ all -> 0x0154 }
            r8.setPosition(r14)
            return r16
        L_0x0255:
            r8.setPosition(r14)
            throw r0
        L_0x0259:
            r2 = r18
        L_0x025b:
            java.lang.String r0 = "Skipping unsupported compressed or encrypted frame"
            com.google.android.exoplayer2.util.Log.w(r2, r0)
            r8.setPosition(r14)
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.metadata.id3.Id3Decoder.decodeFrame(int, com.google.android.exoplayer2.util.ParsableByteArray, boolean, int, com.google.android.exoplayer2.metadata.id3.Id3Decoder$FramePredicate):com.google.android.exoplayer2.metadata.id3.Id3Frame");
    }

    private static TextInformationFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int valueStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new TextInformationFrame("TXXX", description, decodeStringIfValid(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding), charset));
    }

    private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, (String) null, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int urlStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new UrlLinkFrame("WXXX", description, decodeStringIfValid(data, urlStartIndex, indexOfZeroByte(data, urlStartIndex), "ISO-8859-1"));
    }

    private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        return new UrlLinkFrame(id, (String) null, new String(data, 0, indexOfZeroByte(data, 0), "ISO-8859-1"));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        return new PrivFrame(new String(data, 0, ownerEndIndex, "ISO-8859-1"), copyOfRangeIfValid(data, ownerEndIndex + 1, data.length));
    }

    private static GeobFrame decodeGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1");
        int filenameStartIndex = mimeTypeEndIndex + 1;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = decodeStringIfValid(data, filenameStartIndex, filenameEndIndex, charset);
        int descriptionStartIndex = delimiterLength(encoding) + filenameEndIndex;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, decodeStringIfValid(data, descriptionStartIndex, descriptionEndIndex, charset), copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    private static ApicFrame decodeApicFrame(ParsableByteArray id3Data, int frameSize, int majorVersion) throws UnsupportedEncodingException {
        int mimeTypeEndIndex;
        String mimeType;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        if (majorVersion == 2) {
            mimeTypeEndIndex = 2;
            mimeType = "image/" + Util.toLowerInvariant(new String(data, 0, 3, "ISO-8859-1"));
            if ("image/jpg".equals(mimeType)) {
                mimeType = "image/jpeg";
            }
        } else {
            mimeTypeEndIndex = indexOfZeroByte(data, 0);
            String mimeType2 = Util.toLowerInvariant(new String(data, 0, mimeTypeEndIndex, "ISO-8859-1"));
            if (mimeType2.indexOf(47) == -1) {
                mimeType = "image/" + mimeType2;
            } else {
                mimeType = mimeType2;
            }
        }
        int descriptionStartIndex = mimeTypeEndIndex + 2;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), data[mimeTypeEndIndex + 1] & 255, copyOfRangeIfValid(data, delimiterLength(encoding) + descriptionEndIndex, data.length));
    }

    private static CommentFrame decodeCommentFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 4) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[3];
        id3Data.readBytes(data, 0, 3);
        String language = new String(data, 0, 3);
        byte[] data2 = new byte[(frameSize - 4)];
        id3Data.readBytes(data2, 0, frameSize - 4);
        int descriptionEndIndex = indexOfEos(data2, 0, encoding);
        String description = new String(data2, 0, descriptionEndIndex, charset);
        int textStartIndex = delimiterLength(encoding) + descriptionEndIndex;
        return new CommentFrame(language, description, decodeStringIfValid(data2, textStartIndex, indexOfEos(data2, textStartIndex, encoding), charset));
    }

    private static ChapterFrame decodeChapterFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate2) throws UnsupportedEncodingException {
        long startOffset;
        long endOffset;
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int chapterIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String chapterId = new String(parsableByteArray.data, framePosition, chapterIdEndIndex - framePosition, "ISO-8859-1");
        parsableByteArray.setPosition(chapterIdEndIndex + 1);
        int startTime = id3Data.readInt();
        int endTime = id3Data.readInt();
        long startOffset2 = id3Data.readUnsignedInt();
        if (startOffset2 == 4294967295L) {
            startOffset = -1;
        } else {
            startOffset = startOffset2;
        }
        long endOffset2 = id3Data.readUnsignedInt();
        if (endOffset2 == 4294967295L) {
            endOffset = -1;
        } else {
            endOffset = endOffset2;
        }
        ArrayList<Id3Frame> subFrames = new ArrayList<>();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate2);
            if (frame != null) {
                subFrames.add(frame);
            }
        }
        int i = majorVersion;
        boolean z = unsignedIntFrameSizeHack;
        int i2 = frameHeaderSize;
        FramePredicate framePredicate3 = framePredicate2;
        Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
        subFrames.toArray(subFrameArray);
        int i3 = limit;
        ArrayList<Id3Frame> arrayList = subFrames;
        return new ChapterFrame(chapterId, startTime, endTime, startOffset, endOffset, subFrameArray);
    }

    private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate2) throws UnsupportedEncodingException {
        ParsableByteArray parsableByteArray = id3Data;
        int framePosition = id3Data.getPosition();
        int elementIdEndIndex = indexOfZeroByte(parsableByteArray.data, framePosition);
        String elementId = new String(parsableByteArray.data, framePosition, elementIdEndIndex - framePosition, "ISO-8859-1");
        parsableByteArray.setPosition(elementIdEndIndex + 1);
        int ctocFlags = id3Data.readUnsignedByte();
        boolean isOrdered = false;
        boolean isRoot = (ctocFlags & 2) != 0;
        if ((ctocFlags & 1) != 0) {
            isOrdered = true;
        }
        int childCount = id3Data.readUnsignedByte();
        String[] children = new String[childCount];
        for (int i = 0; i < childCount; i++) {
            int startIndex = id3Data.getPosition();
            int endIndex = indexOfZeroByte(parsableByteArray.data, startIndex);
            children[i] = new String(parsableByteArray.data, startIndex, endIndex - startIndex, "ISO-8859-1");
            parsableByteArray.setPosition(endIndex + 1);
        }
        ArrayList arrayList = new ArrayList();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, parsableByteArray, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate2);
            if (frame != null) {
                arrayList.add(frame);
            }
        }
        int i2 = majorVersion;
        boolean z = unsignedIntFrameSizeHack;
        int i3 = frameHeaderSize;
        FramePredicate framePredicate3 = framePredicate2;
        Id3Frame[] subFrameArray = new Id3Frame[arrayList.size()];
        arrayList.toArray(subFrameArray);
        return new ChapterTocFrame(elementId, isRoot, isOrdered, children, subFrameArray);
    }

    private static MlltFrame decodeMlltFrame(ParsableByteArray id3Data, int frameSize) {
        int mpegFramesBetweenReference = id3Data.readUnsignedShort();
        int bytesBetweenReference = id3Data.readUnsignedInt24();
        int millisecondsBetweenReference = id3Data.readUnsignedInt24();
        int bitsForBytesDeviation = id3Data.readUnsignedByte();
        int bitsForMillisecondsDeviation = id3Data.readUnsignedByte();
        ParsableBitArray references = new ParsableBitArray();
        references.reset(id3Data);
        int referencesCount = ((frameSize - 10) * 8) / (bitsForBytesDeviation + bitsForMillisecondsDeviation);
        int[] bytesDeviations = new int[referencesCount];
        int[] millisecondsDeviations = new int[referencesCount];
        for (int i = 0; i < referencesCount; i++) {
            int bytesDeviation = references.readBits(bitsForBytesDeviation);
            int millisecondsDeviation = references.readBits(bitsForMillisecondsDeviation);
            bytesDeviations[i] = bytesDeviation;
            millisecondsDeviations[i] = millisecondsDeviation;
        }
        int[] iArr = bytesDeviations;
        return new MlltFrame(mpegFramesBetweenReference, bytesBetweenReference, millisecondsBetweenReference, bytesDeviations, millisecondsDeviations);
    }

    private static BinaryFrame decodeBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, 0, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static int removeUnsynchronization(ParsableByteArray data, int length) {
        byte[] bytes = data.data;
        for (int i = data.getPosition(); i + 1 < length; i++) {
            if ((bytes[i] & UByte.MAX_VALUE) == 255 && bytes[i + 1] == 0) {
                System.arraycopy(bytes, i + 2, bytes, i + 1, (length - i) - 2);
                length--;
            }
        }
        return length;
    }

    private static String getCharsetName(int encodingByte) {
        if (encodingByte == 1) {
            return C.UTF16_NAME;
        }
        if (encodingByte == 2) {
            return "UTF-16BE";
        }
        if (encodingByte != 3) {
            return "ISO-8859-1";
        }
        return "UTF-8";
    }

    private static String getFrameId(int majorVersion, int frameId0, int frameId1, int frameId2, int frameId3) {
        Locale locale = Locale.US;
        if (majorVersion == 2) {
            return String.format(locale, "%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2)});
        }
        return String.format(locale, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)});
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding == 0 || encoding == 3) {
            return terminationPos;
        }
        while (terminationPos < data.length - 1) {
            if (terminationPos % 2 == 0 && data[terminationPos + 1] == 0) {
                return terminationPos;
            }
            terminationPos = indexOfZeroByte(data, terminationPos + 1);
        }
        return data.length;
    }

    private static int indexOfZeroByte(byte[] data, int fromIndex) {
        for (int i = fromIndex; i < data.length; i++) {
            if (data[i] == 0) {
                return i;
            }
        }
        return data.length;
    }

    private static int delimiterLength(int encodingByte) {
        return (encodingByte == 0 || encodingByte == 3) ? 1 : 2;
    }

    private static byte[] copyOfRangeIfValid(byte[] data, int from, int to) {
        if (to <= from) {
            return Util.EMPTY_BYTE_ARRAY;
        }
        return Arrays.copyOfRange(data, from, to);
    }

    private static String decodeStringIfValid(byte[] data, int from, int to, String charsetName) throws UnsupportedEncodingException {
        if (to <= from || to > data.length) {
            return "";
        }
        return new String(data, from, to - from, charsetName);
    }

    private static final class Id3Header {
        /* access modifiers changed from: private */
        public final int framesSize;
        /* access modifiers changed from: private */
        public final boolean isUnsynchronized;
        /* access modifiers changed from: private */
        public final int majorVersion;

        public Id3Header(int majorVersion2, boolean isUnsynchronized2, int framesSize2) {
            this.majorVersion = majorVersion2;
            this.isUnsynchronized = isUnsynchronized2;
            this.framesSize = framesSize2;
        }
    }
}
