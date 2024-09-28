package com.google.android.exoplayer2.util;

import kotlin.UByte;

public final class ParsableNalUnitBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    private byte[] data;

    public ParsableNalUnitBitArray(byte[] data2, int offset, int limit) {
        reset(data2, offset, limit);
    }

    public void reset(byte[] data2, int offset, int limit) {
        this.data = data2;
        this.byteOffset = offset;
        this.byteLimit = limit;
        this.bitOffset = 0;
        assertValidOffset();
    }

    public void skipBit() {
        int i = 1;
        int i2 = this.bitOffset + 1;
        this.bitOffset = i2;
        if (i2 == 8) {
            this.bitOffset = 0;
            int i3 = this.byteOffset;
            if (shouldSkipByte(i3 + 1)) {
                i = 2;
            }
            this.byteOffset = i3 + i;
        }
        assertValidOffset();
    }

    public void skipBits(int numBits) {
        int oldByteOffset = this.byteOffset;
        int numBytes = numBits / 8;
        int i = this.byteOffset + numBytes;
        this.byteOffset = i;
        int i2 = this.bitOffset + (numBits - (numBytes * 8));
        this.bitOffset = i2;
        if (i2 > 7) {
            this.byteOffset = i + 1;
            this.bitOffset = i2 - 8;
        }
        int i3 = oldByteOffset + 1;
        while (i3 <= this.byteOffset) {
            if (shouldSkipByte(i3)) {
                this.byteOffset++;
                i3 += 2;
            }
            i3++;
        }
        assertValidOffset();
    }

    public boolean canReadBits(int numBits) {
        int oldByteOffset = this.byteOffset;
        int numBytes = numBits / 8;
        int newByteOffset = this.byteOffset + numBytes;
        int newBitOffset = (this.bitOffset + numBits) - (numBytes * 8);
        if (newBitOffset > 7) {
            newByteOffset++;
            newBitOffset -= 8;
        }
        int i = oldByteOffset + 1;
        while (i <= newByteOffset && newByteOffset < this.byteLimit) {
            if (shouldSkipByte(i)) {
                newByteOffset++;
                i += 2;
            }
            i++;
        }
        int i2 = this.byteLimit;
        if (newByteOffset < i2) {
            return true;
        }
        if (newByteOffset == i2 && newBitOffset == 0) {
            return true;
        }
        return false;
    }

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        int i;
        int i2;
        int returnValue = 0;
        this.bitOffset += numBits;
        while (true) {
            i = this.bitOffset;
            i2 = 2;
            if (i <= 8) {
                break;
            }
            int i3 = i - 8;
            this.bitOffset = i3;
            byte[] bArr = this.data;
            int i4 = this.byteOffset;
            returnValue |= (bArr[i4] & UByte.MAX_VALUE) << i3;
            if (!shouldSkipByte(i4 + 1)) {
                i2 = 1;
            }
            this.byteOffset = i4 + i2;
        }
        byte[] bArr2 = this.data;
        int i5 = this.byteOffset;
        int returnValue2 = (returnValue | ((bArr2[i5] & UByte.MAX_VALUE) >> (8 - i))) & (-1 >>> (32 - numBits));
        if (i == 8) {
            this.bitOffset = 0;
            if (!shouldSkipByte(i5 + 1)) {
                i2 = 1;
            }
            this.byteOffset = i5 + i2;
        }
        assertValidOffset();
        return returnValue2;
    }

    public boolean canReadExpGolombCodedNum() {
        int initialByteOffset = this.byteOffset;
        int initialBitOffset = this.bitOffset;
        int leadingZeros = 0;
        while (this.byteOffset < this.byteLimit && !readBit()) {
            leadingZeros++;
        }
        boolean hitLimit = this.byteOffset == this.byteLimit;
        this.byteOffset = initialByteOffset;
        this.bitOffset = initialBitOffset;
        if (hitLimit || !canReadBits((leadingZeros * 2) + 1)) {
            return false;
        }
        return true;
    }

    public int readUnsignedExpGolombCodedInt() {
        return readExpGolombCodeNum();
    }

    public int readSignedExpGolombCodedInt() {
        int codeNum = readExpGolombCodeNum();
        return (codeNum % 2 == 0 ? -1 : 1) * ((codeNum + 1) / 2);
    }

    private int readExpGolombCodeNum() {
        int leadingZeros = 0;
        while (!readBit()) {
            leadingZeros++;
        }
        return ((1 << leadingZeros) - 1) + (leadingZeros > 0 ? readBits(leadingZeros) : 0);
    }

    private boolean shouldSkipByte(int offset) {
        if (2 <= offset && offset < this.byteLimit) {
            byte[] bArr = this.data;
            return bArr[offset] == 3 && bArr[offset + -2] == 0 && bArr[offset + -1] == 0;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r1 = r2.byteLimit;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void assertValidOffset() {
        /*
            r2 = this;
            int r0 = r2.byteOffset
            if (r0 < 0) goto L_0x0010
            int r1 = r2.byteLimit
            if (r0 < r1) goto L_0x000e
            if (r0 != r1) goto L_0x0010
            int r0 = r2.bitOffset
            if (r0 != 0) goto L_0x0010
        L_0x000e:
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            com.google.android.exoplayer2.util.Assertions.checkState(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.ParsableNalUnitBitArray.assertValidOffset():void");
    }
}
