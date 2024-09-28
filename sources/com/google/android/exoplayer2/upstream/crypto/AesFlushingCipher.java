package com.google.android.exoplayer2.upstream.crypto;

import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AesFlushingCipher {
    private final int blockSize;
    private final Cipher cipher;
    private final byte[] flushedBlock;
    private int pendingXorBytes;
    private final byte[] zerosBlock;

    public AesFlushingCipher(int mode, byte[] secretKey, long nonce, long offset) {
        try {
            Cipher instance = Cipher.getInstance("AES/CTR/NoPadding");
            this.cipher = instance;
            int blockSize2 = instance.getBlockSize();
            this.blockSize = blockSize2;
            this.zerosBlock = new byte[blockSize2];
            this.flushedBlock = new byte[blockSize2];
            int startPadding = (int) (offset % ((long) blockSize2));
            this.cipher.init(mode, new SecretKeySpec(secretKey, Util.splitAtFirst(this.cipher.getAlgorithm(), "/")[0]), new IvParameterSpec(getInitializationVector(nonce, offset / ((long) blockSize2))));
            if (startPadding != 0) {
                updateInPlace(new byte[startPadding], 0, startPadding);
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateInPlace(byte[] data, int offset, int length) {
        update(data, offset, length, data, offset);
    }

    public void update(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        int inOffset2 = inOffset;
        int length2 = length;
        int outOffset2 = outOffset;
        do {
            int i = this.pendingXorBytes;
            if (i > 0) {
                out[outOffset2] = (byte) (in[inOffset2] ^ this.flushedBlock[this.blockSize - i]);
                outOffset2++;
                inOffset2++;
                this.pendingXorBytes = i - 1;
                length2--;
            } else {
                int written = nonFlushingUpdate(in, inOffset2, length2, out, outOffset2);
                if (length2 != written) {
                    int bytesToFlush = length2 - written;
                    boolean z = false;
                    Assertions.checkState(bytesToFlush < this.blockSize);
                    int outOffset3 = outOffset2 + written;
                    int i2 = this.blockSize - bytesToFlush;
                    this.pendingXorBytes = i2;
                    if (nonFlushingUpdate(this.zerosBlock, 0, i2, this.flushedBlock, 0) == this.blockSize) {
                        z = true;
                    }
                    Assertions.checkState(z);
                    int i3 = 0;
                    while (i3 < bytesToFlush) {
                        out[outOffset3] = this.flushedBlock[i3];
                        i3++;
                        outOffset3++;
                    }
                    return;
                }
                return;
            }
        } while (length2 != 0);
    }

    private int nonFlushingUpdate(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        try {
            return this.cipher.update(in, inOffset, length, out, outOffset);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getInitializationVector(long nonce, long counter) {
        return ByteBuffer.allocate(16).putLong(nonce).putLong(counter).array();
    }
}
