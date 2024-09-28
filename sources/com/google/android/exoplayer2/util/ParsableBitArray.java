package com.google.android.exoplayer2.util;

import androidx.core.view.MotionEventCompat;
import kotlin.UByte;

public final class ParsableBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    public byte[] data;

    public ParsableBitArray() {
        this.data = Util.EMPTY_BYTE_ARRAY;
    }

    public ParsableBitArray(byte[] data2) {
        this(data2, data2.length);
    }

    public ParsableBitArray(byte[] data2, int limit) {
        this.data = data2;
        this.byteLimit = limit;
    }

    public void reset(byte[] data2) {
        reset(data2, data2.length);
    }

    public void reset(ParsableByteArray parsableByteArray) {
        reset(parsableByteArray.data, parsableByteArray.limit());
        setPosition(parsableByteArray.getPosition() * 8);
    }

    public void reset(byte[] data2, int limit) {
        this.data = data2;
        this.byteOffset = 0;
        this.bitOffset = 0;
        this.byteLimit = limit;
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public int getBytePosition() {
        Assertions.checkState(this.bitOffset == 0);
        return this.byteOffset;
    }

    public void setPosition(int position) {
        int i = position / 8;
        this.byteOffset = i;
        this.bitOffset = position - (i * 8);
        assertValidOffset();
    }

    public void skipBit() {
        int i = this.bitOffset + 1;
        this.bitOffset = i;
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset++;
        }
        assertValidOffset();
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

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        int i;
        if (numBits == 0) {
            return 0;
        }
        int returnValue = 0;
        this.bitOffset += numBits;
        while (true) {
            i = this.bitOffset;
            if (i <= 8) {
                break;
            }
            int i2 = i - 8;
            this.bitOffset = i2;
            byte[] bArr = this.data;
            int i3 = this.byteOffset;
            this.byteOffset = i3 + 1;
            returnValue |= (bArr[i3] & UByte.MAX_VALUE) << i2;
        }
        byte[] bArr2 = this.data;
        int i4 = this.byteOffset;
        int returnValue2 = (returnValue | ((bArr2[i4] & UByte.MAX_VALUE) >> (8 - i))) & (-1 >>> (32 - numBits));
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset = i4 + 1;
        }
        assertValidOffset();
        return returnValue2;
    }

    public void readBits(byte[] buffer, int offset, int numBits) {
        int to = (numBits >> 3) + offset;
        for (int i = offset; i < to; i++) {
            byte[] bArr = this.data;
            int i2 = this.byteOffset;
            int i3 = i2 + 1;
            this.byteOffset = i3;
            byte b = bArr[i2];
            int i4 = this.bitOffset;
            buffer[i] = (byte) (b << i4);
            buffer[i] = (byte) (((255 & bArr[i3]) >> (8 - i4)) | buffer[i]);
        }
        int bitsLeft = numBits & 7;
        if (bitsLeft != 0) {
            buffer[to] = (byte) (buffer[to] & (255 >> bitsLeft));
            int i5 = this.bitOffset;
            if (i5 + bitsLeft > 8) {
                byte b2 = buffer[to];
                byte[] bArr2 = this.data;
                int i6 = this.byteOffset;
                this.byteOffset = i6 + 1;
                buffer[to] = (byte) (b2 | ((bArr2[i6] & UByte.MAX_VALUE) << i5));
                this.bitOffset = i5 - 8;
            }
            int i7 = this.bitOffset + bitsLeft;
            this.bitOffset = i7;
            byte[] bArr3 = this.data;
            int i8 = this.byteOffset;
            buffer[to] = (byte) (buffer[to] | ((byte) (((255 & bArr3[i8]) >> (8 - i7)) << (8 - bitsLeft))));
            if (i7 == 8) {
                this.bitOffset = 0;
                this.byteOffset = i8 + 1;
            }
            assertValidOffset();
        }
    }

    public void byteAlign() {
        if (this.bitOffset != 0) {
            this.bitOffset = 0;
            this.byteOffset++;
            assertValidOffset();
        }
    }

    public void readBytes(byte[] buffer, int offset, int length) {
        Assertions.checkState(this.bitOffset == 0);
        System.arraycopy(this.data, this.byteOffset, buffer, offset, length);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void skipBytes(int length) {
        Assertions.checkState(this.bitOffset == 0);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void putInt(int value, int numBits) {
        int remainingBitsToRead = numBits;
        if (numBits < 32) {
            value &= (1 << numBits) - 1;
        }
        int firstByteReadSize = Math.min(8 - this.bitOffset, numBits);
        int i = this.bitOffset;
        int firstByteRightPaddingSize = (8 - i) - firstByteReadSize;
        int firstByteBitmask = (MotionEventCompat.ACTION_POINTER_INDEX_MASK >> i) | ((1 << firstByteRightPaddingSize) - 1);
        byte[] bArr = this.data;
        int i2 = this.byteOffset;
        bArr[i2] = (byte) (bArr[i2] & firstByteBitmask);
        bArr[i2] = (byte) (bArr[i2] | ((value >>> (numBits - firstByteReadSize)) << firstByteRightPaddingSize));
        int remainingBitsToRead2 = remainingBitsToRead - firstByteReadSize;
        int currentByteIndex = i2 + 1;
        while (remainingBitsToRead2 > 8) {
            this.data[currentByteIndex] = (byte) (value >>> (remainingBitsToRead2 - 8));
            remainingBitsToRead2 -= 8;
            currentByteIndex++;
        }
        int lastByteRightPaddingSize = 8 - remainingBitsToRead2;
        byte[] bArr2 = this.data;
        bArr2[currentByteIndex] = (byte) (bArr2[currentByteIndex] & ((1 << lastByteRightPaddingSize) - 1));
        bArr2[currentByteIndex] = (byte) (bArr2[currentByteIndex] | ((value & ((1 << remainingBitsToRead2) - 1)) << lastByteRightPaddingSize));
        skipBits(numBits);
        assertValidOffset();
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
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.util.ParsableBitArray.assertValidOffset():void");
    }
}
