package com.google.android.exoplayer2;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.ColorInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Format implements Parcelable {
    public static final Parcelable.Creator<Format> CREATOR = new Parcelable.Creator<Format>() {
        public Format createFromParcel(Parcel in) {
            return new Format(in);
        }

        public Format[] newArray(int size) {
            return new Format[size];
        }
    };
    public static final int NO_VALUE = -1;
    public static final long OFFSET_SAMPLE_RELATIVE = Long.MAX_VALUE;
    public final int accessibilityChannel;
    public final int bitrate;
    public final int channelCount;
    public final String codecs;
    public final ColorInfo colorInfo;
    public final String containerMimeType;
    public final DrmInitData drmInitData;
    public final int encoderDelay;
    public final int encoderPadding;
    public final float frameRate;
    private int hashCode;
    public final int height;
    public final String id;
    public final List<byte[]> initializationData;
    public final String label;
    public final String language;
    public final int maxInputSize;
    public final Metadata metadata;
    public final int pcmEncoding;
    public final float pixelWidthHeightRatio;
    public final byte[] projectionData;
    public final int rotationDegrees;
    public final String sampleMimeType;
    public final int sampleRate;
    public final int selectionFlags;
    public final int stereoMode;
    public final long subsampleOffsetUs;
    public final int width;

    @Deprecated
    public static Format createVideoContainerFormat(String id2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int width2, int height2, float frameRate2, List<byte[]> initializationData2, int selectionFlags2) {
        return createVideoContainerFormat(id2, (String) null, containerMimeType2, sampleMimeType2, codecs2, bitrate2, width2, height2, frameRate2, initializationData2, selectionFlags2);
    }

    public static Format createVideoContainerFormat(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int width2, int height2, float frameRate2, List<byte[]> initializationData2, int selectionFlags2) {
        return new Format(id2, label2, containerMimeType2, sampleMimeType2, codecs2, bitrate2, -1, width2, height2, frameRate2, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, selectionFlags2, (String) null, -1, Long.MAX_VALUE, initializationData2, (DrmInitData) null, (Metadata) null);
    }

    public static Format createVideoSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int width2, int height2, float frameRate2, List<byte[]> initializationData2, DrmInitData drmInitData2) {
        return createVideoSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, maxInputSize2, width2, height2, frameRate2, initializationData2, -1, -1.0f, drmInitData2);
    }

    public static Format createVideoSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int width2, int height2, float frameRate2, List<byte[]> initializationData2, int rotationDegrees2, float pixelWidthHeightRatio2, DrmInitData drmInitData2) {
        return createVideoSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, maxInputSize2, width2, height2, frameRate2, initializationData2, rotationDegrees2, pixelWidthHeightRatio2, (byte[]) null, -1, (ColorInfo) null, drmInitData2);
    }

    public static Format createVideoSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int width2, int height2, float frameRate2, List<byte[]> initializationData2, int rotationDegrees2, float pixelWidthHeightRatio2, byte[] projectionData2, int stereoMode2, ColorInfo colorInfo2, DrmInitData drmInitData2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, codecs2, bitrate2, maxInputSize2, width2, height2, frameRate2, rotationDegrees2, pixelWidthHeightRatio2, projectionData2, stereoMode2, colorInfo2, -1, -1, -1, -1, -1, 0, (String) null, -1, Long.MAX_VALUE, initializationData2, drmInitData2, (Metadata) null);
    }

    @Deprecated
    public static Format createAudioContainerFormat(String id2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int channelCount2, int sampleRate2, List<byte[]> initializationData2, int selectionFlags2, String language2) {
        return createAudioContainerFormat(id2, (String) null, containerMimeType2, sampleMimeType2, codecs2, bitrate2, channelCount2, sampleRate2, initializationData2, selectionFlags2, language2);
    }

    public static Format createAudioContainerFormat(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int channelCount2, int sampleRate2, List<byte[]> initializationData2, int selectionFlags2, String language2) {
        return new Format(id2, label2, containerMimeType2, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, channelCount2, sampleRate2, -1, -1, -1, selectionFlags2, language2, -1, Long.MAX_VALUE, initializationData2, (DrmInitData) null, (Metadata) null);
    }

    public static Format createAudioSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int channelCount2, int sampleRate2, List<byte[]> initializationData2, DrmInitData drmInitData2, int selectionFlags2, String language2) {
        return createAudioSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, maxInputSize2, channelCount2, sampleRate2, -1, initializationData2, drmInitData2, selectionFlags2, language2);
    }

    public static Format createAudioSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int channelCount2, int sampleRate2, int pcmEncoding2, List<byte[]> initializationData2, DrmInitData drmInitData2, int selectionFlags2, String language2) {
        return createAudioSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, maxInputSize2, channelCount2, sampleRate2, pcmEncoding2, -1, -1, initializationData2, drmInitData2, selectionFlags2, language2, (Metadata) null);
    }

    public static Format createAudioSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int channelCount2, int sampleRate2, int pcmEncoding2, int encoderDelay2, int encoderPadding2, List<byte[]> initializationData2, DrmInitData drmInitData2, int selectionFlags2, String language2, Metadata metadata2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, codecs2, bitrate2, maxInputSize2, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, channelCount2, sampleRate2, pcmEncoding2, encoderDelay2, encoderPadding2, selectionFlags2, language2, -1, Long.MAX_VALUE, initializationData2, drmInitData2, metadata2);
    }

    @Deprecated
    public static Format createTextContainerFormat(String id2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2) {
        return createTextContainerFormat(id2, (String) null, containerMimeType2, sampleMimeType2, codecs2, bitrate2, selectionFlags2, language2);
    }

    public static Format createTextContainerFormat(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2) {
        return createTextContainerFormat(id2, label2, containerMimeType2, sampleMimeType2, codecs2, bitrate2, selectionFlags2, language2, -1);
    }

    public static Format createTextContainerFormat(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2, int accessibilityChannel2) {
        return new Format(id2, label2, containerMimeType2, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, selectionFlags2, language2, accessibilityChannel2, Long.MAX_VALUE, (List<byte[]>) null, (DrmInitData) null, (Metadata) null);
    }

    public static Format createTextSampleFormat(String id2, String sampleMimeType2, int selectionFlags2, String language2) {
        return createTextSampleFormat(id2, sampleMimeType2, selectionFlags2, language2, (DrmInitData) null);
    }

    public static Format createTextSampleFormat(String id2, String sampleMimeType2, int selectionFlags2, String language2, DrmInitData drmInitData2) {
        return createTextSampleFormat(id2, sampleMimeType2, (String) null, -1, selectionFlags2, language2, -1, drmInitData2, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2, int accessibilityChannel2, DrmInitData drmInitData2) {
        return createTextSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, selectionFlags2, language2, accessibilityChannel2, drmInitData2, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2, DrmInitData drmInitData2, long subsampleOffsetUs2) {
        return createTextSampleFormat(id2, sampleMimeType2, codecs2, bitrate2, selectionFlags2, language2, -1, drmInitData2, subsampleOffsetUs2, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2, int accessibilityChannel2, DrmInitData drmInitData2, long subsampleOffsetUs2, List<byte[]> initializationData2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, selectionFlags2, language2, accessibilityChannel2, subsampleOffsetUs2, initializationData2, drmInitData2, (Metadata) null);
    }

    public static Format createImageSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, List<byte[]> initializationData2, String language2, DrmInitData drmInitData2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, selectionFlags2, language2, -1, Long.MAX_VALUE, initializationData2, drmInitData2, (Metadata) null);
    }

    @Deprecated
    public static Format createContainerFormat(String id2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2) {
        return createContainerFormat(id2, (String) null, containerMimeType2, sampleMimeType2, codecs2, bitrate2, selectionFlags2, language2);
    }

    public static Format createContainerFormat(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int selectionFlags2, String language2) {
        return new Format(id2, label2, containerMimeType2, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, selectionFlags2, language2, -1, Long.MAX_VALUE, (List<byte[]>) null, (DrmInitData) null, (Metadata) null);
    }

    public static Format createSampleFormat(String id2, String sampleMimeType2, long subsampleOffsetUs2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, (String) null, -1, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, 0, (String) null, -1, subsampleOffsetUs2, (List<byte[]>) null, (DrmInitData) null, (Metadata) null);
    }

    public static Format createSampleFormat(String id2, String sampleMimeType2, String codecs2, int bitrate2, DrmInitData drmInitData2) {
        return new Format(id2, (String) null, (String) null, sampleMimeType2, codecs2, bitrate2, -1, -1, -1, -1.0f, -1, -1.0f, (byte[]) null, -1, (ColorInfo) null, -1, -1, -1, -1, -1, 0, (String) null, -1, Long.MAX_VALUE, (List<byte[]>) null, drmInitData2, (Metadata) null);
    }

    Format(String id2, String label2, String containerMimeType2, String sampleMimeType2, String codecs2, int bitrate2, int maxInputSize2, int width2, int height2, float frameRate2, int rotationDegrees2, float pixelWidthHeightRatio2, byte[] projectionData2, int stereoMode2, ColorInfo colorInfo2, int channelCount2, int sampleRate2, int pcmEncoding2, int encoderDelay2, int encoderPadding2, int selectionFlags2, String language2, int accessibilityChannel2, long subsampleOffsetUs2, List<byte[]> initializationData2, DrmInitData drmInitData2, Metadata metadata2) {
        this.id = id2;
        this.label = label2;
        this.containerMimeType = containerMimeType2;
        this.sampleMimeType = sampleMimeType2;
        this.codecs = codecs2;
        this.bitrate = bitrate2;
        this.maxInputSize = maxInputSize2;
        this.width = width2;
        this.height = height2;
        this.frameRate = frameRate2;
        int i = rotationDegrees2;
        this.rotationDegrees = i == -1 ? 0 : i;
        this.pixelWidthHeightRatio = 1.0f;
        this.projectionData = projectionData2;
        this.stereoMode = stereoMode2;
        this.colorInfo = colorInfo2;
        this.channelCount = channelCount2;
        this.sampleRate = sampleRate2;
        this.pcmEncoding = pcmEncoding2;
        int i2 = encoderDelay2;
        this.encoderDelay = i2 == -1 ? 0 : i2;
        int i3 = encoderPadding2;
        this.encoderPadding = i3 == -1 ? 0 : i3;
        this.selectionFlags = selectionFlags2;
        this.language = language2;
        this.accessibilityChannel = accessibilityChannel2;
        this.subsampleOffsetUs = subsampleOffsetUs2;
        this.initializationData = initializationData2 == null ? Collections.emptyList() : initializationData2;
        this.drmInitData = drmInitData2;
        this.metadata = metadata2;
    }

    Format(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
        this.containerMimeType = in.readString();
        this.sampleMimeType = in.readString();
        this.codecs = in.readString();
        this.bitrate = in.readInt();
        this.maxInputSize = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.frameRate = in.readFloat();
        this.rotationDegrees = in.readInt();
        this.pixelWidthHeightRatio = in.readFloat();
        this.projectionData = Util.readBoolean(in) ? in.createByteArray() : null;
        this.stereoMode = in.readInt();
        this.colorInfo = (ColorInfo) in.readParcelable(ColorInfo.class.getClassLoader());
        this.channelCount = in.readInt();
        this.sampleRate = in.readInt();
        this.pcmEncoding = in.readInt();
        this.encoderDelay = in.readInt();
        this.encoderPadding = in.readInt();
        this.selectionFlags = in.readInt();
        this.language = in.readString();
        this.accessibilityChannel = in.readInt();
        this.subsampleOffsetUs = in.readLong();
        int initializationDataSize = in.readInt();
        this.initializationData = new ArrayList(initializationDataSize);
        for (int i = 0; i < initializationDataSize; i++) {
            this.initializationData.add(in.createByteArray());
        }
        this.drmInitData = (DrmInitData) in.readParcelable(DrmInitData.class.getClassLoader());
        this.metadata = (Metadata) in.readParcelable(Metadata.class.getClassLoader());
    }

    public Format copyWithMaxInputSize(int maxInputSize2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, maxInputSize2, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithSubsampleOffsetUs(long subsampleOffsetUs2) {
        return new Format(this.id, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, subsampleOffsetUs2, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithContainerInfo(String id2, String label2, String sampleMimeType2, String codecs2, int bitrate2, int width2, int height2, int selectionFlags2, String language2) {
        return new Format(id2, label2, this.containerMimeType, sampleMimeType2, codecs2, bitrate2, this.maxInputSize, width2, height2, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, selectionFlags2, language2, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.android.exoplayer2.Format copyWithManifestFormatInfo(com.google.android.exoplayer2.Format r40) {
        /*
            r39 = this;
            r0 = r39
            r1 = r40
            if (r0 != r1) goto L_0x0007
            return r0
        L_0x0007:
            java.lang.String r2 = r0.sampleMimeType
            int r2 = com.google.android.exoplayer2.util.MimeTypes.getTrackType(r2)
            java.lang.String r13 = r1.id
            java.lang.String r3 = r1.label
            if (r3 == 0) goto L_0x0014
            goto L_0x0016
        L_0x0014:
            java.lang.String r3 = r0.label
        L_0x0016:
            r5 = r3
            java.lang.String r3 = r0.language
            r4 = 3
            r6 = 1
            if (r2 == r4) goto L_0x001f
            if (r2 != r6) goto L_0x0028
        L_0x001f:
            java.lang.String r4 = r1.language
            if (r4 == 0) goto L_0x0028
            java.lang.String r3 = r1.language
            r32 = r3
            goto L_0x002a
        L_0x0028:
            r32 = r3
        L_0x002a:
            int r3 = r0.bitrate
            r4 = -1
            if (r3 != r4) goto L_0x0031
            int r3 = r1.bitrate
        L_0x0031:
            r9 = r3
            java.lang.String r3 = r0.codecs
            if (r3 != 0) goto L_0x0047
            java.lang.String r4 = r1.codecs
            java.lang.String r4 = com.google.android.exoplayer2.util.Util.getCodecsOfType(r4, r2)
            java.lang.String[] r7 = com.google.android.exoplayer2.util.Util.splitCodecs(r4)
            int r7 = r7.length
            if (r7 != r6) goto L_0x0047
            r3 = r4
            r33 = r3
            goto L_0x0049
        L_0x0047:
            r33 = r3
        L_0x0049:
            float r3 = r0.frameRate
            r4 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x0059
            r4 = 2
            if (r2 != r4) goto L_0x0059
            float r3 = r1.frameRate
            r34 = r3
            goto L_0x005b
        L_0x0059:
            r34 = r3
        L_0x005b:
            int r3 = r0.selectionFlags
            int r4 = r1.selectionFlags
            r35 = r3 | r4
            r24 = r35
            com.google.android.exoplayer2.drm.DrmInitData r3 = r1.drmInitData
            com.google.android.exoplayer2.drm.DrmInitData r4 = r0.drmInitData
            com.google.android.exoplayer2.drm.DrmInitData r36 = com.google.android.exoplayer2.drm.DrmInitData.createSessionCreationData(r3, r4)
            r30 = r36
            com.google.android.exoplayer2.Format r37 = new com.google.android.exoplayer2.Format
            r3 = r37
            java.lang.String r6 = r0.containerMimeType
            java.lang.String r7 = r0.sampleMimeType
            int r10 = r0.maxInputSize
            int r11 = r0.width
            int r12 = r0.height
            int r14 = r0.rotationDegrees
            float r15 = r0.pixelWidthHeightRatio
            byte[] r4 = r0.projectionData
            r16 = r4
            int r4 = r0.stereoMode
            r17 = r4
            com.google.android.exoplayer2.video.ColorInfo r4 = r0.colorInfo
            r18 = r4
            int r4 = r0.channelCount
            r19 = r4
            int r4 = r0.sampleRate
            r20 = r4
            int r4 = r0.pcmEncoding
            r21 = r4
            int r4 = r0.encoderDelay
            r22 = r4
            int r4 = r0.encoderPadding
            r23 = r4
            int r4 = r0.accessibilityChannel
            r26 = r4
            r38 = r2
            long r1 = r0.subsampleOffsetUs
            r27 = r1
            java.util.List<byte[]> r1 = r0.initializationData
            r29 = r1
            com.google.android.exoplayer2.metadata.Metadata r1 = r0.metadata
            r31 = r1
            r4 = r13
            r8 = r33
            r1 = r13
            r13 = r34
            r25 = r32
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r26, r27, r29, r30, r31)
            return r37
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.Format.copyWithManifestFormatInfo(com.google.android.exoplayer2.Format):com.google.android.exoplayer2.Format");
    }

    public Format copyWithGaplessInfo(int encoderDelay2, int encoderPadding2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, encoderDelay2, encoderPadding2, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithFrameRate(float frameRate2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, frameRate2, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithDrmInitData(DrmInitData drmInitData2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, drmInitData2, this.metadata);
    }

    public Format copyWithMetadata(Metadata metadata2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, metadata2);
    }

    public Format copyWithRotationDegrees(int rotationDegrees2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, rotationDegrees2, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithBitrate(int bitrate2) {
        String str = this.id;
        return new Format(str, this.label, this.containerMimeType, this.sampleMimeType, this.codecs, bitrate2, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public int getPixelCount() {
        int i;
        int i2 = this.width;
        if (i2 == -1 || (i = this.height) == -1) {
            return -1;
        }
        return i2 * i;
    }

    public String toString() {
        return "Format(" + this.id + ", " + this.label + ", " + this.containerMimeType + ", " + this.sampleMimeType + ", " + this.codecs + ", " + this.bitrate + ", " + this.language + ", [" + this.width + ", " + this.height + ", " + this.frameRate + "], [" + this.channelCount + ", " + this.sampleRate + "])";
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int i = 17 * 31;
            String str = this.id;
            int i2 = 0;
            int result = (i + (str == null ? 0 : str.hashCode())) * 31;
            String str2 = this.containerMimeType;
            int result2 = (result + (str2 == null ? 0 : str2.hashCode())) * 31;
            String str3 = this.sampleMimeType;
            int result3 = (result2 + (str3 == null ? 0 : str3.hashCode())) * 31;
            String str4 = this.codecs;
            int result4 = (((((((((((result3 + (str4 == null ? 0 : str4.hashCode())) * 31) + this.bitrate) * 31) + this.width) * 31) + this.height) * 31) + this.channelCount) * 31) + this.sampleRate) * 31;
            String str5 = this.language;
            int result5 = (((result4 + (str5 == null ? 0 : str5.hashCode())) * 31) + this.accessibilityChannel) * 31;
            DrmInitData drmInitData2 = this.drmInitData;
            int result6 = (result5 + (drmInitData2 == null ? 0 : drmInitData2.hashCode())) * 31;
            Metadata metadata2 = this.metadata;
            int result7 = (result6 + (metadata2 == null ? 0 : metadata2.hashCode())) * 31;
            String str6 = this.label;
            if (str6 != null) {
                i2 = str6.hashCode();
            }
            this.hashCode = ((((((((((((((((((((result7 + i2) * 31) + this.maxInputSize) * 31) + ((int) this.subsampleOffsetUs)) * 31) + Float.floatToIntBits(this.frameRate)) * 31) + Float.floatToIntBits(this.pixelWidthHeightRatio)) * 31) + this.rotationDegrees) * 31) + this.stereoMode) * 31) + this.pcmEncoding) * 31) + this.encoderDelay) * 31) + this.encoderPadding) * 31) + this.selectionFlags;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        int i;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Format other = (Format) obj;
        int i2 = this.hashCode;
        if ((i2 == 0 || (i = other.hashCode) == 0 || i2 == i) && this.bitrate == other.bitrate && this.maxInputSize == other.maxInputSize && this.width == other.width && this.height == other.height && Float.compare(this.frameRate, other.frameRate) == 0 && this.rotationDegrees == other.rotationDegrees && Float.compare(this.pixelWidthHeightRatio, other.pixelWidthHeightRatio) == 0 && this.stereoMode == other.stereoMode && this.channelCount == other.channelCount && this.sampleRate == other.sampleRate && this.pcmEncoding == other.pcmEncoding && this.encoderDelay == other.encoderDelay && this.encoderPadding == other.encoderPadding && this.subsampleOffsetUs == other.subsampleOffsetUs && this.selectionFlags == other.selectionFlags && Util.areEqual(this.id, other.id) && Util.areEqual(this.label, other.label) && Util.areEqual(this.language, other.language) && this.accessibilityChannel == other.accessibilityChannel && Util.areEqual(this.containerMimeType, other.containerMimeType) && Util.areEqual(this.sampleMimeType, other.sampleMimeType) && Util.areEqual(this.codecs, other.codecs) && Util.areEqual(this.drmInitData, other.drmInitData) && Util.areEqual(this.metadata, other.metadata) && Util.areEqual(this.colorInfo, other.colorInfo) && Arrays.equals(this.projectionData, other.projectionData) && initializationDataEquals(other)) {
            return true;
        }
        return false;
    }

    public boolean initializationDataEquals(Format other) {
        if (this.initializationData.size() != other.initializationData.size()) {
            return false;
        }
        for (int i = 0; i < this.initializationData.size(); i++) {
            if (!Arrays.equals(this.initializationData.get(i), other.initializationData.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static String toLogString(Format format) {
        if (format == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("id=");
        builder.append(format.id);
        builder.append(", mimeType=");
        builder.append(format.sampleMimeType);
        if (format.bitrate != -1) {
            builder.append(", bitrate=");
            builder.append(format.bitrate);
        }
        if (format.codecs != null) {
            builder.append(", codecs=");
            builder.append(format.codecs);
        }
        if (!(format.width == -1 || format.height == -1)) {
            builder.append(", res=");
            builder.append(format.width);
            builder.append("x");
            builder.append(format.height);
        }
        if (format.frameRate != -1.0f) {
            builder.append(", fps=");
            builder.append(format.frameRate);
        }
        if (format.channelCount != -1) {
            builder.append(", channels=");
            builder.append(format.channelCount);
        }
        if (format.sampleRate != -1) {
            builder.append(", sample_rate=");
            builder.append(format.sampleRate);
        }
        if (format.language != null) {
            builder.append(", language=");
            builder.append(format.language);
        }
        if (format.label != null) {
            builder.append(", label=");
            builder.append(format.label);
        }
        return builder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.label);
        dest.writeString(this.containerMimeType);
        dest.writeString(this.sampleMimeType);
        dest.writeString(this.codecs);
        dest.writeInt(this.bitrate);
        dest.writeInt(this.maxInputSize);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.frameRate);
        dest.writeInt(this.rotationDegrees);
        dest.writeFloat(this.pixelWidthHeightRatio);
        Util.writeBoolean(dest, this.projectionData != null);
        byte[] bArr = this.projectionData;
        if (bArr != null) {
            dest.writeByteArray(bArr);
        }
        dest.writeInt(this.stereoMode);
        dest.writeParcelable(this.colorInfo, flags);
        dest.writeInt(this.channelCount);
        dest.writeInt(this.sampleRate);
        dest.writeInt(this.pcmEncoding);
        dest.writeInt(this.encoderDelay);
        dest.writeInt(this.encoderPadding);
        dest.writeInt(this.selectionFlags);
        dest.writeString(this.language);
        dest.writeInt(this.accessibilityChannel);
        dest.writeLong(this.subsampleOffsetUs);
        int initializationDataSize = this.initializationData.size();
        dest.writeInt(initializationDataSize);
        for (int i = 0; i < initializationDataSize; i++) {
            dest.writeByteArray(this.initializationData.get(i));
        }
        dest.writeParcelable(this.drmInitData, 0);
        dest.writeParcelable(this.metadata, 0);
    }
}
