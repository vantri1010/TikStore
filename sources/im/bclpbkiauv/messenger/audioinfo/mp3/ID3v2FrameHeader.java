package im.bclpbkiauv.messenger.audioinfo.mp3;

import java.io.IOException;
import kotlin.UByte;

public class ID3v2FrameHeader {
    private int bodySize;
    private boolean compression;
    private int dataLengthIndicator;
    private boolean encryption;
    private String frameId;
    private int headerSize;
    private boolean unsynchronization;

    public ID3v2FrameHeader(ID3v2TagBody input) throws IOException, ID3v2Exception {
        int groupingIdentityMask;
        int encryptionMask;
        int compressionMask;
        long startPosition = input.getPosition();
        ID3v2DataInput data = input.getData();
        if (input.getTagHeader().getVersion() == 2) {
            this.frameId = new String(data.readFully(3), "ISO-8859-1");
        } else {
            this.frameId = new String(data.readFully(4), "ISO-8859-1");
        }
        if (input.getTagHeader().getVersion() == 2) {
            this.bodySize = ((data.readByte() & UByte.MAX_VALUE) << 16) | ((data.readByte() & UByte.MAX_VALUE) << 8) | (data.readByte() & UByte.MAX_VALUE);
        } else if (input.getTagHeader().getVersion() == 3) {
            this.bodySize = data.readInt();
        } else {
            this.bodySize = data.readSyncsafeInt();
        }
        if (input.getTagHeader().getVersion() > 2) {
            data.readByte();
            int formatFlags = data.readByte();
            int unsynchronizationMask = 0;
            int dataLengthIndicatorMask = 0;
            if (input.getTagHeader().getVersion() == 3) {
                compressionMask = 128;
                encryptionMask = 64;
                groupingIdentityMask = 32;
            } else {
                groupingIdentityMask = 64;
                compressionMask = 8;
                encryptionMask = 4;
                unsynchronizationMask = 2;
                dataLengthIndicatorMask = 1;
            }
            boolean z = false;
            this.compression = (formatFlags & compressionMask) != 0;
            this.unsynchronization = (formatFlags & unsynchronizationMask) != 0;
            this.encryption = (formatFlags & encryptionMask) != 0 ? true : z;
            if (input.getTagHeader().getVersion() == 3) {
                if (this.compression) {
                    this.dataLengthIndicator = data.readInt();
                    this.bodySize -= 4;
                }
                if (this.encryption) {
                    data.readByte();
                    this.bodySize--;
                }
                if ((formatFlags & groupingIdentityMask) != 0) {
                    data.readByte();
                    this.bodySize--;
                }
            } else {
                if ((formatFlags & groupingIdentityMask) != 0) {
                    data.readByte();
                    this.bodySize--;
                }
                if (this.encryption) {
                    data.readByte();
                    this.bodySize--;
                }
                if ((formatFlags & dataLengthIndicatorMask) != 0) {
                    this.dataLengthIndicator = data.readSyncsafeInt();
                    this.bodySize -= 4;
                }
            }
        }
        this.headerSize = (int) (input.getPosition() - startPosition);
    }

    public String getFrameId() {
        return this.frameId;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getBodySize() {
        return this.bodySize;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public boolean isEncryption() {
        return this.encryption;
    }

    public boolean isUnsynchronization() {
        return this.unsynchronization;
    }

    public int getDataLengthIndicator() {
        return this.dataLengthIndicator;
    }

    public boolean isValid() {
        for (int i = 0; i < this.frameId.length(); i++) {
            if ((this.frameId.charAt(i) < 'A' || this.frameId.charAt(i) > 'Z') && (this.frameId.charAt(i) < '0' || this.frameId.charAt(i) > '9')) {
                return false;
            }
        }
        if (this.bodySize > 0) {
            return true;
        }
        return false;
    }

    public boolean isPadding() {
        for (int i = 0; i < this.frameId.length(); i++) {
            if (this.frameId.charAt(0) != 0) {
                return false;
            }
        }
        if (this.bodySize == 0) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format("%s[id=%s, bodysize=%d]", new Object[]{getClass().getSimpleName(), this.frameId, Integer.valueOf(this.bodySize)});
    }
}
