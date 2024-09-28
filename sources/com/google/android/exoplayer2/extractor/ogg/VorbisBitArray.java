package com.google.android.exoplayer2.extractor.ogg;

import kotlin.UByte;

final class VorbisBitArray {
    private int bitOffset;
    private final int byteLimit;
    private int byteOffset;
    private final byte[] data;

    public VorbisBitArray(byte[] data2) {
        this.data = data2;
        this.byteLimit = data2.length;
    }

    public void reset() {
        this.byteOffset = 0;
        this.bitOffset = 0;
    }

    public boolean readBit() {
        boolean returnValue = (((this.data[this.byteOffset] & UByte.MAX_VALUE) >> this.bitOffset) & 1) == 1;
        skipBits(1);
        return returnValue;
    }

    public int readBits(int numBits) {
        int tempByteOffset = this.byteOffset;
        int bitsRead = Math.min(numBits, 8 - this.bitOffset);
        int tempByteOffset2 = tempByteOffset + 1;
        int returnValue = ((this.data[tempByteOffset] & UByte.MAX_VALUE) >> this.bitOffset) & (255 >> (8 - bitsRead));
        while (bitsRead < numBits) {
            returnValue |= (this.data[tempByteOffset2] & UByte.MAX_VALUE) << bitsRead;
            bitsRead += 8;
            tempByteOffset2++;
        }
        int returnValue2 = returnValue & (-1 >>> (32 - numBits));
        skipBits(numBits);
        return returnValue2;
    }

    public void skipBits(int numBits) {
        int numBytes = numBits / 8;
        int i = this.byteOffset + numBytes;
        this.byteOffset = i;
        int i2 = this.bitOffset + (numBits - (numBytes * 8));
        this.bitOffset = i2;
        if (i2 > 7) {
            this.byteOffset = i + 1;
            this.bitOffset = i2 - 8;
        }
        assertValidOffset();
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public void setPosition(int position) {
        int i = position / 8;
        this.byteOffset = i;
        this.bitOffset = position - (i * 8);
        assertValidOffset();
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
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
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ogg.VorbisBitArray.assertValidOffset():void");
    }
}
