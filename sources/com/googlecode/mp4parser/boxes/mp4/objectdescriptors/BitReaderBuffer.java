package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;
import kotlin.UByte;

public class BitReaderBuffer {
    private ByteBuffer buffer;
    int initialPos;
    int position;

    public BitReaderBuffer(ByteBuffer buffer2) {
        this.buffer = buffer2;
        this.initialPos = buffer2.position();
    }

    public boolean readBool() {
        return readBits(1) == 1;
    }

    public int readBits(int i) {
        int then;
        byte b = this.buffer.get(this.initialPos + (this.position / 8));
        int v = b < 0 ? b + UByte.MIN_VALUE : b;
        int i2 = this.position;
        int left = 8 - (i2 % 8);
        if (i <= left) {
            then = ((v << (i2 % 8)) & 255) >> ((i2 % 8) + (left - i));
            this.position = i2 + i;
        } else {
            int then2 = i - left;
            then = (readBits(left) << then2) + readBits(then2);
        }
        this.buffer.position(this.initialPos + ((int) Math.ceil(((double) this.position) / 8.0d)));
        return then;
    }

    public int getPosition() {
        return this.position;
    }

    public int byteSync() {
        int left = 8 - (this.position % 8);
        if (left == 8) {
            left = 0;
        }
        readBits(left);
        return left;
    }

    public int remainingBits() {
        return (this.buffer.limit() * 8) - this.position;
    }
}
