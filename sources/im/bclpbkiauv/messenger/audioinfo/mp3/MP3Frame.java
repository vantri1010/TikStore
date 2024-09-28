package im.bclpbkiauv.messenger.audioinfo.mp3;

import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import kotlin.UByte;
import kotlin.jvm.internal.ShortCompanionObject;

public class MP3Frame {
    private final byte[] bytes;
    private final Header header;

    static final class CRC16 {
        private short crc = -1;

        CRC16() {
        }

        public void update(int value, int length) {
            int i;
            int mask = 1 << (length - 1);
            do {
                boolean z = false;
                boolean z2 = (this.crc & ShortCompanionObject.MIN_VALUE) == 0;
                if ((value & mask) == 0) {
                    z = true;
                }
                if (z2 ^ z) {
                    short s = (short) (this.crc << 1);
                    this.crc = s;
                    this.crc = (short) (s ^ 32773);
                } else {
                    this.crc = (short) (this.crc << 1);
                }
                i = mask >>> 1;
                mask = i;
            } while (i != 0);
        }

        public void update(byte value) {
            update(value, 8);
        }

        public short getValue() {
            return this.crc;
        }

        public void reset() {
            this.crc = -1;
        }
    }

    public static class Header {
        private static final int[][] BITRATES = {new int[]{0, 0, 0, 0, 0}, new int[]{32000, 32000, 32000, 32000, 8000}, new int[]{64000, 48000, 40000, 48000, AudioEditConstant.ExportSampleRate}, new int[]{96000, 56000, 48000, 56000, 24000}, new int[]{128000, 64000, 56000, 64000, 32000}, new int[]{160000, 80000, 64000, 80000, 40000}, new int[]{192000, 96000, 80000, 96000, 48000}, new int[]{224000, 112000, 96000, 112000, 56000}, new int[]{256000, 128000, 112000, 128000, 64000}, new int[]{288000, 160000, 128000, 144000, 80000}, new int[]{320000, 192000, 160000, 160000, 96000}, new int[]{352000, 224000, 192000, 176000, 112000}, new int[]{384000, 256000, 224000, 192000, 128000}, new int[]{416000, 320000, 256000, 224000, 144000}, new int[]{448000, 384000, 320000, 256000, 160000}, new int[]{-1, -1, -1, -1, -1}};
        private static final int[][] BITRATES_COLUMN = {new int[]{-1, 4, 4, 3}, new int[]{-1, -1, -1, -1}, new int[]{-1, 4, 4, 3}, new int[]{-1, 2, 1, 0}};
        private static final int[][] FREQUENCIES = {new int[]{11025, -1, 22050, 44100}, new int[]{12000, -1, 24000, 48000}, new int[]{8000, -1, AudioEditConstant.ExportSampleRate, 32000}, new int[]{-1, -1, -1, -1}};
        private static final int MPEG_BITRATE_FREE = 0;
        private static final int MPEG_BITRATE_RESERVED = 15;
        public static final int MPEG_CHANNEL_MODE_MONO = 3;
        private static final int MPEG_FRQUENCY_RESERVED = 3;
        public static final int MPEG_LAYER_1 = 3;
        public static final int MPEG_LAYER_2 = 2;
        public static final int MPEG_LAYER_3 = 1;
        private static final int MPEG_LAYER_RESERVED = 0;
        public static final int MPEG_PROTECTION_CRC = 0;
        public static final int MPEG_VERSION_1 = 3;
        public static final int MPEG_VERSION_2 = 2;
        public static final int MPEG_VERSION_2_5 = 0;
        private static final int MPEG_VERSION_RESERVED = 1;
        private static final int[][] SIDE_INFO_SIZES = {new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{9, -1, 9, 17}};
        private static final int[][] SIZE_COEFFICIENTS = {new int[]{-1, 72, 144, 12}, new int[]{-1, -1, -1, -1}, new int[]{-1, 72, 144, 12}, new int[]{-1, 144, 144, 12}};
        private static final int[] SLOT_SIZES = {-1, 1, 1, 4};
        private final int bitrate;
        private final int channelMode;
        private final int frequency;
        private final int layer;
        private final int padding;
        private final int protection;
        private final int version;

        public Header(int b1, int b2, int b3) throws MP3Exception {
            int i = (b1 >> 3) & 3;
            this.version = i;
            if (i != 1) {
                int i2 = (b1 >> 1) & 3;
                this.layer = i2;
                if (i2 != 0) {
                    int i3 = (b2 >> 4) & 15;
                    this.bitrate = i3;
                    if (i3 == 15) {
                        throw new MP3Exception("Reserved bitrate");
                    } else if (i3 != 0) {
                        int i4 = (b2 >> 2) & 3;
                        this.frequency = i4;
                        if (i4 != 3) {
                            this.channelMode = (b3 >> 6) & 3;
                            this.padding = (b2 >> 1) & 1;
                            int i5 = b1 & 1;
                            this.protection = i5;
                            int minFrameSize = i5 == 0 ? 4 + 2 : 4;
                            minFrameSize = this.layer == 1 ? minFrameSize + getSideInfoSize() : minFrameSize;
                            if (getFrameSize() < minFrameSize) {
                                throw new MP3Exception("Frame size must be at least " + minFrameSize);
                            }
                            return;
                        }
                        throw new MP3Exception("Reserved frequency");
                    } else {
                        throw new MP3Exception("Free bitrate");
                    }
                } else {
                    throw new MP3Exception("Reserved layer");
                }
            } else {
                throw new MP3Exception("Reserved version");
            }
        }

        public int getVersion() {
            return this.version;
        }

        public int getLayer() {
            return this.layer;
        }

        public int getFrequency() {
            return FREQUENCIES[this.frequency][this.version];
        }

        public int getChannelMode() {
            return this.channelMode;
        }

        public int getProtection() {
            return this.protection;
        }

        public int getSampleCount() {
            if (this.layer == 3) {
                return 384;
            }
            return 1152;
        }

        public int getFrameSize() {
            return (((SIZE_COEFFICIENTS[this.version][this.layer] * getBitrate()) / getFrequency()) + this.padding) * SLOT_SIZES[this.layer];
        }

        public int getBitrate() {
            return BITRATES[this.bitrate][BITRATES_COLUMN[this.version][this.layer]];
        }

        public int getDuration() {
            return (int) getTotalDuration((long) getFrameSize());
        }

        public long getTotalDuration(long totalSize) {
            long duration = ((((long) getSampleCount()) * totalSize) * 1000) / ((long) (getFrameSize() * getFrequency()));
            if (getVersion() == 3 || getChannelMode() != 3) {
                return duration;
            }
            return duration / 2;
        }

        public boolean isCompatible(Header header) {
            return this.layer == header.layer && this.version == header.version && this.frequency == header.frequency && this.channelMode == header.channelMode;
        }

        public int getSideInfoSize() {
            return SIDE_INFO_SIZES[this.channelMode][this.version];
        }

        public int getXingOffset() {
            return getSideInfoSize() + 4;
        }

        public int getVBRIOffset() {
            return 36;
        }
    }

    MP3Frame(Header header2, byte[] bytes2) {
        this.header = header2;
        this.bytes = bytes2;
    }

    /* access modifiers changed from: package-private */
    public boolean isChecksumError() {
        if (this.header.getProtection() != 0 || this.header.getLayer() != 1) {
            return false;
        }
        CRC16 crc16 = new CRC16();
        crc16.update(this.bytes[2]);
        crc16.update(this.bytes[3]);
        int sideInfoSize = this.header.getSideInfoSize();
        for (int i = 0; i < sideInfoSize; i++) {
            crc16.update(this.bytes[i + 6]);
        }
        byte[] bArr = this.bytes;
        if (((bArr[5] & 255) | ((bArr[4] & UByte.MAX_VALUE) << 8)) != crc16.getValue()) {
            return true;
        }
        return false;
    }

    public int getSize() {
        return this.bytes.length;
    }

    public Header getHeader() {
        return this.header;
    }

    /* access modifiers changed from: package-private */
    public boolean isXingFrame() {
        int xingOffset = this.header.getXingOffset();
        byte[] bArr = this.bytes;
        if (bArr.length < xingOffset + 12 || xingOffset < 0 || bArr.length < xingOffset + 8) {
            return false;
        }
        if (bArr[xingOffset] == 88 && bArr[xingOffset + 1] == 105 && bArr[xingOffset + 2] == 110 && bArr[xingOffset + 3] == 103) {
            return true;
        }
        byte[] bArr2 = this.bytes;
        if (bArr2[xingOffset] == 73 && bArr2[xingOffset + 1] == 110 && bArr2[xingOffset + 2] == 102 && bArr2[xingOffset + 3] == 111) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isVBRIFrame() {
        int vbriOffset = this.header.getVBRIOffset();
        byte[] bArr = this.bytes;
        if (bArr.length >= vbriOffset + 26 && bArr[vbriOffset] == 86 && bArr[vbriOffset + 1] == 66 && bArr[vbriOffset + 2] == 82 && bArr[vbriOffset + 3] == 73) {
            return true;
        }
        return false;
    }

    public int getNumberOfFrames() {
        if (isXingFrame()) {
            int xingOffset = this.header.getXingOffset();
            byte[] bArr = this.bytes;
            if ((bArr[xingOffset + 7] & 1) == 0) {
                return -1;
            }
            return (bArr[xingOffset + 11] & UByte.MAX_VALUE) | ((bArr[xingOffset + 8] & UByte.MAX_VALUE) << 24) | ((bArr[xingOffset + 9] & UByte.MAX_VALUE) << 16) | ((bArr[xingOffset + 10] & UByte.MAX_VALUE) << 8);
        } else if (!isVBRIFrame()) {
            return -1;
        } else {
            int vbriOffset = this.header.getVBRIOffset();
            byte[] bArr2 = this.bytes;
            return (bArr2[vbriOffset + 17] & UByte.MAX_VALUE) | ((bArr2[vbriOffset + 14] & UByte.MAX_VALUE) << 24) | ((bArr2[vbriOffset + 15] & UByte.MAX_VALUE) << 16) | ((bArr2[vbriOffset + 16] & UByte.MAX_VALUE) << 8);
        }
    }
}
