package com.mp4parser.iso23001.part7;

import com.coremedia.iso.Hex;
import com.king.zxing.util.LogUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.math.BigInteger;
import java.util.Arrays;

public class CencSampleAuxiliaryDataFormat {
    public byte[] iv = new byte[0];
    public Pair[] pairs = null;

    public interface Pair {
        int clear();

        long encrypted();
    }

    public int getSize() {
        int size = this.iv.length;
        Pair[] pairArr = this.pairs;
        if (pairArr == null || pairArr.length <= 0) {
            return size;
        }
        return size + 2 + (pairArr.length * 6);
    }

    public Pair createPair(int clear, long encrypted) {
        if (clear <= 127) {
            if (encrypted <= 127) {
                return new ByteBytePair(clear, encrypted);
            }
            if (encrypted <= 32767) {
                return new ByteShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new ByteIntPair(clear, encrypted);
            }
            return new ByteLongPair(clear, encrypted);
        } else if (clear <= 32767) {
            if (encrypted <= 127) {
                return new ShortBytePair(clear, encrypted);
            }
            if (encrypted <= 32767) {
                return new ShortShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new ShortIntPair(clear, encrypted);
            }
            return new ShortLongPair(clear, encrypted);
        } else if (encrypted <= 127) {
            return new IntBytePair(clear, encrypted);
        } else {
            if (encrypted <= 32767) {
                return new IntShortPair(clear, encrypted);
            }
            if (encrypted <= 2147483647L) {
                return new IntIntPair(clear, encrypted);
            }
            return new IntLongPair(clear, encrypted);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CencSampleAuxiliaryDataFormat entry = (CencSampleAuxiliaryDataFormat) o;
        if (!new BigInteger(this.iv).equals(new BigInteger(entry.iv))) {
            return false;
        }
        Pair[] pairArr = this.pairs;
        if (pairArr == null ? entry.pairs == null : Arrays.equals(pairArr, entry.pairs)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        byte[] bArr = this.iv;
        int i = 0;
        int hashCode = (bArr != null ? Arrays.hashCode(bArr) : 0) * 31;
        Pair[] pairArr = this.pairs;
        if (pairArr != null) {
            i = Arrays.hashCode(pairArr);
        }
        return hashCode + i;
    }

    public String toString() {
        return "Entry{iv=" + Hex.encodeHex(this.iv) + ", pairs=" + Arrays.toString(this.pairs) + '}';
    }

    private class ByteBytePair extends AbstractPair {
        private byte clear;
        private byte encrypted;

        public ByteBytePair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (byte) clear2;
            this.encrypted = (byte) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ByteShortPair extends AbstractPair {
        private byte clear;
        private short encrypted;

        public ByteShortPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (byte) clear2;
            this.encrypted = (short) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ByteIntPair extends AbstractPair {
        private byte clear;
        private int encrypted;

        public ByteIntPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (byte) clear2;
            this.encrypted = (int) encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ByteLongPair extends AbstractPair {
        private byte clear;
        private long encrypted;

        public ByteLongPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (byte) clear2;
            this.encrypted = encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private class ShortBytePair extends AbstractPair {
        private short clear;
        private byte encrypted;

        public ShortBytePair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (short) clear2;
            this.encrypted = (byte) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortShortPair extends AbstractPair {
        private short clear;
        private short encrypted;

        public ShortShortPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (short) clear2;
            this.encrypted = (short) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortIntPair extends AbstractPair {
        private short clear;
        private int encrypted;

        public ShortIntPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (short) clear2;
            this.encrypted = (int) encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class ShortLongPair extends AbstractPair {
        private short clear;
        private long encrypted;

        public ShortLongPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = (short) clear2;
            this.encrypted = encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private class IntBytePair extends AbstractPair {
        private int clear;
        private byte encrypted;

        public IntBytePair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = clear2;
            this.encrypted = (byte) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntShortPair extends AbstractPair {
        private int clear;
        private short encrypted;

        public IntShortPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = clear2;
            this.encrypted = (short) ((int) encrypted2);
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntIntPair extends AbstractPair {
        private int clear;
        private int encrypted;

        public IntIntPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = clear2;
            this.encrypted = (int) encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return (long) this.encrypted;
        }
    }

    private class IntLongPair extends AbstractPair {
        private int clear;
        private long encrypted;

        public IntLongPair(int clear2, long encrypted2) {
            super(CencSampleAuxiliaryDataFormat.this, (AbstractPair) null);
            this.clear = clear2;
            this.encrypted = encrypted2;
        }

        public int clear() {
            return this.clear;
        }

        public long encrypted() {
            return this.encrypted;
        }
    }

    private abstract class AbstractPair implements Pair {
        private AbstractPair() {
        }

        /* synthetic */ AbstractPair(CencSampleAuxiliaryDataFormat cencSampleAuxiliaryDataFormat, AbstractPair abstractPair) {
            this();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Pair pair = (Pair) o;
            if (clear() == pair.clear() && encrypted() == pair.encrypted()) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "P(" + clear() + LogUtils.VERTICAL + encrypted() + SQLBuilder.PARENTHESES_RIGHT;
        }
    }
}
