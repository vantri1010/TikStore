package com.google.android.exoplayer2.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class NalUnitUtil {
    public static final float[] ASPECT_RATIO_IDC_VALUES = {1.0f, 1.0f, 1.0909091f, 0.90909094f, 1.4545455f, 1.2121212f, 2.1818182f, 1.8181819f, 2.909091f, 2.4242425f, 1.6363636f, 1.3636364f, 1.939394f, 1.6161616f, 1.3333334f, 1.5f, 2.0f};
    public static final int EXTENDED_SAR = 255;
    private static final int H264_NAL_UNIT_TYPE_SEI = 6;
    private static final int H264_NAL_UNIT_TYPE_SPS = 7;
    private static final int H265_NAL_UNIT_TYPE_PREFIX_SEI = 39;
    public static final byte[] NAL_START_CODE = {0, 0, 0, 1};
    private static final String TAG = "NalUnitUtil";
    private static int[] scratchEscapePositions = new int[10];
    private static final Object scratchEscapePositionsLock = new Object();

    public static final class SpsData {
        public final int constraintsFlagsAndReservedZero2Bits;
        public final boolean deltaPicOrderAlwaysZeroFlag;
        public final boolean frameMbsOnlyFlag;
        public final int frameNumLength;
        public final int height;
        public final int levelIdc;
        public final int picOrderCntLsbLength;
        public final int picOrderCountType;
        public final float pixelWidthAspectRatio;
        public final int profileIdc;
        public final boolean separateColorPlaneFlag;
        public final int seqParameterSetId;
        public final int width;

        public SpsData(int profileIdc2, int constraintsFlagsAndReservedZero2Bits2, int levelIdc2, int seqParameterSetId2, int width2, int height2, float pixelWidthAspectRatio2, boolean separateColorPlaneFlag2, boolean frameMbsOnlyFlag2, int frameNumLength2, int picOrderCountType2, int picOrderCntLsbLength2, boolean deltaPicOrderAlwaysZeroFlag2) {
            this.profileIdc = profileIdc2;
            this.constraintsFlagsAndReservedZero2Bits = constraintsFlagsAndReservedZero2Bits2;
            this.levelIdc = levelIdc2;
            this.seqParameterSetId = seqParameterSetId2;
            this.width = width2;
            this.height = height2;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio2;
            this.separateColorPlaneFlag = separateColorPlaneFlag2;
            this.frameMbsOnlyFlag = frameMbsOnlyFlag2;
            this.frameNumLength = frameNumLength2;
            this.picOrderCountType = picOrderCountType2;
            this.picOrderCntLsbLength = picOrderCntLsbLength2;
            this.deltaPicOrderAlwaysZeroFlag = deltaPicOrderAlwaysZeroFlag2;
        }
    }

    public static final class PpsData {
        public final boolean bottomFieldPicOrderInFramePresentFlag;
        public final int picParameterSetId;
        public final int seqParameterSetId;

        public PpsData(int picParameterSetId2, int seqParameterSetId2, boolean bottomFieldPicOrderInFramePresentFlag2) {
            this.picParameterSetId = picParameterSetId2;
            this.seqParameterSetId = seqParameterSetId2;
            this.bottomFieldPicOrderInFramePresentFlag = bottomFieldPicOrderInFramePresentFlag2;
        }
    }

    public static int unescapeStream(byte[] data, int limit) {
        int unescapedLength;
        synchronized (scratchEscapePositionsLock) {
            int position = 0;
            int scratchEscapeCount = 0;
            while (position < limit) {
                try {
                    position = findNextUnescapeIndex(data, position, limit);
                    if (position < limit) {
                        if (scratchEscapePositions.length <= scratchEscapeCount) {
                            scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
                        }
                        scratchEscapePositions[scratchEscapeCount] = position;
                        position += 3;
                        scratchEscapeCount++;
                    }
                } finally {
                }
            }
            unescapedLength = limit - scratchEscapeCount;
            int escapedPosition = 0;
            int unescapedPosition = 0;
            for (int i = 0; i < scratchEscapeCount; i++) {
                int copyLength = scratchEscapePositions[i] - escapedPosition;
                System.arraycopy(data, escapedPosition, data, unescapedPosition, copyLength);
                int unescapedPosition2 = unescapedPosition + copyLength;
                int unescapedPosition3 = unescapedPosition2 + 1;
                data[unescapedPosition2] = 0;
                unescapedPosition = unescapedPosition3 + 1;
                data[unescapedPosition3] = 0;
                escapedPosition += copyLength + 3;
            }
            System.arraycopy(data, escapedPosition, data, unescapedPosition, unescapedLength - unescapedPosition);
        }
        return unescapedLength;
    }

    public static void discardToSps(ByteBuffer data) {
        int length = data.position();
        int consecutiveZeros = 0;
        for (int offset = 0; offset + 1 < length; offset++) {
            int value = data.get(offset) & 255;
            if (consecutiveZeros == 3) {
                if (value == 1 && (data.get(offset + 1) & 31) == 7) {
                    ByteBuffer offsetData = data.duplicate();
                    offsetData.position(offset - 3);
                    offsetData.limit(length);
                    data.position(0);
                    data.put(offsetData);
                    return;
                }
            } else if (value == 0) {
                consecutiveZeros++;
            }
            if (value != 0) {
                consecutiveZeros = 0;
            }
        }
        data.clear();
    }

    public static boolean isNalUnitSei(String mimeType, byte nalUnitHeaderFirstByte) {
        if ("video/avc".equals(mimeType) && (nalUnitHeaderFirstByte & 31) == 6) {
            return true;
        }
        if (!MimeTypes.VIDEO_H265.equals(mimeType) || ((nalUnitHeaderFirstByte & 126) >> 1) != 39) {
            return false;
        }
        return true;
    }

    public static int getNalUnitType(byte[] data, int offset) {
        return data[offset + 3] & 31;
    }

    public static int getH265NalUnitType(byte[] data, int offset) {
        return (data[offset + 3] & 126) >> 1;
    }

    public static SpsData parseSpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        boolean separateColorPlaneFlag;
        int chromaFormatIdc;
        boolean deltaPicOrderAlwaysZeroFlag;
        int picOrderCntLsbLength;
        int frameHeight;
        int frameWidth;
        float pixelWidthHeightRatio;
        int subWidthC;
        int cropUnitY;
        int picOrderCntLsbLength2;
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int profileIdc = data.readBits(8);
        int constraintsFlagsAndReservedZero2Bits = data.readBits(8);
        int levelIdc = data.readBits(8);
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        boolean separateColorPlaneFlag2 = false;
        if (profileIdc == 100 || profileIdc == 110 || profileIdc == 122 || profileIdc == 244 || profileIdc == 44 || profileIdc == 83 || profileIdc == 86 || profileIdc == 118 || profileIdc == 128 || profileIdc == 138) {
            int chromaFormatIdc2 = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc2 == 3) {
                separateColorPlaneFlag2 = data.readBit();
            }
            data.readUnsignedExpGolombCodedInt();
            data.readUnsignedExpGolombCodedInt();
            data.skipBit();
            if (data.readBit()) {
                int limit = chromaFormatIdc2 != 3 ? 8 : 12;
                int i = 0;
                while (i < limit) {
                    if (data.readBit()) {
                        skipScalingList(data, i < 6 ? 16 : 64);
                    }
                    i++;
                }
            }
            chromaFormatIdc = chromaFormatIdc2;
            separateColorPlaneFlag = separateColorPlaneFlag2;
        } else {
            chromaFormatIdc = 1;
            separateColorPlaneFlag = false;
        }
        int frameNumLength = data.readUnsignedExpGolombCodedInt() + 4;
        int picOrderCntType = data.readUnsignedExpGolombCodedInt();
        int picOrderCntLsbLength3 = 0;
        int subHeightC = 1;
        if (picOrderCntType == 0) {
            picOrderCntLsbLength = data.readUnsignedExpGolombCodedInt() + 4;
            deltaPicOrderAlwaysZeroFlag = false;
        } else if (picOrderCntType == 1) {
            boolean deltaPicOrderAlwaysZeroFlag2 = data.readBit();
            data.readSignedExpGolombCodedInt();
            data.readSignedExpGolombCodedInt();
            long numRefFramesInPicOrderCntCycle = (long) data.readUnsignedExpGolombCodedInt();
            int i2 = 0;
            while (true) {
                picOrderCntLsbLength2 = picOrderCntLsbLength3;
                if (((long) i2) >= numRefFramesInPicOrderCntCycle) {
                    break;
                }
                data.readUnsignedExpGolombCodedInt();
                i2++;
                picOrderCntLsbLength3 = picOrderCntLsbLength2;
            }
            deltaPicOrderAlwaysZeroFlag = deltaPicOrderAlwaysZeroFlag2;
            picOrderCntLsbLength = picOrderCntLsbLength2;
        } else {
            deltaPicOrderAlwaysZeroFlag = false;
            picOrderCntLsbLength = 0;
        }
        data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        int picWidthInMbs = data.readUnsignedExpGolombCodedInt() + 1;
        boolean frameMbsOnlyFlag = data.readBit();
        int frameHeightInMbs = (true - (frameMbsOnlyFlag)) * (data.readUnsignedExpGolombCodedInt() + 1);
        if (!frameMbsOnlyFlag) {
            data.skipBit();
        }
        data.skipBit();
        int frameWidth2 = picWidthInMbs * 16;
        int frameHeight2 = frameHeightInMbs * 16;
        if (data.readBit()) {
            int frameCropLeftOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropRightOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropTopOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropBottomOffset = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc == 0) {
                subWidthC = 1;
                cropUnitY = true - frameMbsOnlyFlag;
            } else {
                int subWidthC2 = chromaFormatIdc == 3 ? 1 : 2;
                if (chromaFormatIdc == 1) {
                    subHeightC = 2;
                }
                cropUnitY = (true - frameMbsOnlyFlag) * subHeightC;
                subWidthC = subWidthC2;
            }
            frameWidth = frameWidth2 - ((frameCropLeftOffset + frameCropRightOffset) * subWidthC);
            frameHeight = frameHeight2 - ((frameCropTopOffset + frameCropBottomOffset) * cropUnitY);
        } else {
            frameWidth = frameWidth2;
            frameHeight = frameHeight2;
        }
        float pixelWidthHeightRatio2 = 1.0f;
        if (data.readBit() && data.readBit()) {
            int aspectRatioIdc = data.readBits(8);
            if (aspectRatioIdc == 255) {
                int sarWidth = data.readBits(16);
                int sarHeight = data.readBits(16);
                if (!(sarWidth == 0 || sarHeight == 0)) {
                    pixelWidthHeightRatio2 = ((float) sarWidth) / ((float) sarHeight);
                }
                pixelWidthHeightRatio = pixelWidthHeightRatio2;
            } else {
                float[] fArr = ASPECT_RATIO_IDC_VALUES;
                if (aspectRatioIdc < fArr.length) {
                    pixelWidthHeightRatio = fArr[aspectRatioIdc];
                } else {
                    Log.w(TAG, "Unexpected aspect_ratio_idc value: " + aspectRatioIdc);
                }
            }
            int i3 = chromaFormatIdc;
            int i4 = profileIdc;
            return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth, frameHeight, pixelWidthHeightRatio, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength, deltaPicOrderAlwaysZeroFlag);
        }
        pixelWidthHeightRatio = 1.0f;
        int i32 = chromaFormatIdc;
        int i42 = profileIdc;
        return new SpsData(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc, seqParameterSetId, frameWidth, frameHeight, pixelWidthHeightRatio, separateColorPlaneFlag, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength, deltaPicOrderAlwaysZeroFlag);
    }

    public static PpsData parsePpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int picParameterSetId = data.readUnsignedExpGolombCodedInt();
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        return new PpsData(picParameterSetId, seqParameterSetId, data.readBit());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00a2, code lost:
        r5 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int findNalUnit(byte[] r7, int r8, int r9, boolean[] r10) {
        /*
            int r0 = r9 - r8
            r1 = 0
            r2 = 1
            if (r0 < 0) goto L_0x0008
            r3 = 1
            goto L_0x0009
        L_0x0008:
            r3 = 0
        L_0x0009:
            com.google.android.exoplayer2.util.Assertions.checkState(r3)
            if (r0 != 0) goto L_0x000f
            return r9
        L_0x000f:
            r3 = 2
            if (r10 == 0) goto L_0x0042
            boolean r4 = r10[r1]
            if (r4 == 0) goto L_0x001c
            clearPrefixFlags(r10)
            int r1 = r8 + -3
            return r1
        L_0x001c:
            if (r0 <= r2) goto L_0x002c
            boolean r4 = r10[r2]
            if (r4 == 0) goto L_0x002c
            byte r4 = r7[r8]
            if (r4 != r2) goto L_0x002c
            clearPrefixFlags(r10)
            int r1 = r8 + -2
            return r1
        L_0x002c:
            if (r0 <= r3) goto L_0x0042
            boolean r4 = r10[r3]
            if (r4 == 0) goto L_0x0042
            byte r4 = r7[r8]
            if (r4 != 0) goto L_0x0042
            int r4 = r8 + 1
            byte r4 = r7[r4]
            if (r4 != r2) goto L_0x0042
            clearPrefixFlags(r10)
            int r1 = r8 + -1
            return r1
        L_0x0042:
            int r4 = r9 + -1
            int r5 = r8 + 2
        L_0x0046:
            if (r5 >= r4) goto L_0x006c
            byte r6 = r7[r5]
            r6 = r6 & 254(0xfe, float:3.56E-43)
            if (r6 == 0) goto L_0x004f
            goto L_0x0069
        L_0x004f:
            int r6 = r5 + -2
            byte r6 = r7[r6]
            if (r6 != 0) goto L_0x0067
            int r6 = r5 + -1
            byte r6 = r7[r6]
            if (r6 != 0) goto L_0x0067
            byte r6 = r7[r5]
            if (r6 != r2) goto L_0x0067
            if (r10 == 0) goto L_0x0064
            clearPrefixFlags(r10)
        L_0x0064:
            int r1 = r5 + -2
            return r1
        L_0x0067:
            int r5 = r5 + -2
        L_0x0069:
            int r5 = r5 + 3
            goto L_0x0046
        L_0x006c:
            if (r10 == 0) goto L_0x00cc
            if (r0 <= r3) goto L_0x0085
            int r5 = r9 + -3
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x0083
            int r5 = r9 + -2
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x0083
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != r2) goto L_0x0083
            goto L_0x00a2
        L_0x0083:
            r5 = 0
            goto L_0x00a3
        L_0x0085:
            if (r0 != r3) goto L_0x0098
            boolean r5 = r10[r3]
            if (r5 == 0) goto L_0x0083
            int r5 = r9 + -2
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x0083
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != r2) goto L_0x0083
            goto L_0x00a2
        L_0x0098:
            boolean r5 = r10[r2]
            if (r5 == 0) goto L_0x0083
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != r2) goto L_0x0083
        L_0x00a2:
            r5 = 1
        L_0x00a3:
            r10[r1] = r5
            if (r0 <= r2) goto L_0x00b4
            int r5 = r9 + -2
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x00c0
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x00c0
            goto L_0x00be
        L_0x00b4:
            boolean r5 = r10[r3]
            if (r5 == 0) goto L_0x00c0
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x00c0
        L_0x00be:
            r5 = 1
            goto L_0x00c1
        L_0x00c0:
            r5 = 0
        L_0x00c1:
            r10[r2] = r5
            int r5 = r9 + -1
            byte r5 = r7[r5]
            if (r5 != 0) goto L_0x00ca
            r1 = 1
        L_0x00ca:
            r10[r3] = r1
        L_0x00cc:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.NalUnitUtil.findNalUnit(byte[], int, int, boolean[]):int");
    }

    public static void clearPrefixFlags(boolean[] prefixFlags) {
        prefixFlags[0] = false;
        prefixFlags[1] = false;
        prefixFlags[2] = false;
    }

    private static int findNextUnescapeIndex(byte[] bytes, int offset, int limit) {
        for (int i = offset; i < limit - 2; i++) {
            if (bytes[i] == 0 && bytes[i + 1] == 0 && bytes[i + 2] == 3) {
                return i;
            }
        }
        return limit;
    }

    private static void skipScalingList(ParsableNalUnitBitArray bitArray, int size) {
        int lastScale = 8;
        int nextScale = 8;
        for (int i = 0; i < size; i++) {
            if (nextScale != 0) {
                nextScale = ((lastScale + bitArray.readSignedExpGolombCodedInt()) + 256) % 256;
            }
            lastScale = nextScale == 0 ? lastScale : nextScale;
        }
    }

    private NalUnitUtil() {
    }
}
