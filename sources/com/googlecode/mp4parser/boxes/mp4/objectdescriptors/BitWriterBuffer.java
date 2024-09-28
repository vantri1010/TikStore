package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import androidx.core.view.InputDeviceCompat;
import java.nio.ByteBuffer;

public class BitWriterBuffer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private ByteBuffer buffer;
    int initialPos;
    int position = 0;

    public BitWriterBuffer(ByteBuffer buffer2) {
        this.buffer = buffer2;
        this.initialPos = buffer2.position();
    }

    public void writeBits(int i, int numBits) {
        int i2 = this.position;
        int left = 8 - (i2 % 8);
        int i3 = 1;
        if (numBits <= left) {
            int current = this.buffer.get(this.initialPos + (i2 / 8));
            int current2 = (current < 0 ? current + 256 : current) + (i << (left - numBits));
            this.buffer.put(this.initialPos + (this.position / 8), (byte) (current2 > 127 ? current2 + InputDeviceCompat.SOURCE_ANY : current2));
            this.position += numBits;
        } else {
            int bitsSecondWrite = numBits - left;
            writeBits(i >> bitsSecondWrite, left);
            writeBits(((1 << bitsSecondWrite) - 1) & i, bitsSecondWrite);
        }
        ByteBuffer byteBuffer = this.buffer;
        int i4 = this.initialPos;
        int i5 = this.position;
        int i6 = i4 + (i5 / 8);
        if (i5 % 8 <= 0) {
            i3 = 0;
        }
        byteBuffer.position(i6 + i3);
    }
}
