package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.Base64;
import com.alibaba.fastjson.util.IOUtils;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;

public final class JSONReaderScanner extends JSONLexerBase {
    public static int BUF_INIT_LEN = 8192;
    private static final ThreadLocal<SoftReference<char[]>> BUF_REF_LOCAL = new ThreadLocal<>();
    private char[] buf;
    private int bufLength;
    private Reader reader;

    public JSONReaderScanner(String input) {
        this(input, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONReaderScanner(String input, int features) {
        this((Reader) new StringReader(input), features);
    }

    public JSONReaderScanner(char[] input, int inputLength) {
        this(input, inputLength, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONReaderScanner(Reader reader2) {
        this(reader2, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONReaderScanner(Reader reader2, int features) {
        this.reader = reader2;
        this.features = features;
        SoftReference<char[]> bufRef = BUF_REF_LOCAL.get();
        if (bufRef != null) {
            this.buf = bufRef.get();
            BUF_REF_LOCAL.set((Object) null);
        }
        if (this.buf == null) {
            this.buf = new char[BUF_INIT_LEN];
        }
        try {
            this.bufLength = reader2.read(this.buf);
            this.bp = -1;
            next();
            if (this.ch == 65279) {
                next();
            }
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    public JSONReaderScanner(char[] input, int inputLength, int features) {
        this((Reader) new CharArrayReader(input, 0, inputLength), features);
    }

    public final char charAt(int index) {
        int i = this.bufLength;
        if (index >= i) {
            if (i != -1) {
                int rest = i - this.bp;
                if (rest > 0) {
                    System.arraycopy(this.buf, this.bp, this.buf, 0, rest);
                }
                try {
                    int read = this.reader.read(this.buf, rest, this.buf.length - rest);
                    this.bufLength = read;
                    if (read == 0) {
                        throw new JSONException("illegal stat, textLength is zero");
                    } else if (read == -1) {
                        return 26;
                    } else {
                        this.bufLength = read + rest;
                        index -= this.bp;
                        this.np -= this.bp;
                        this.bp = 0;
                    }
                } catch (IOException e) {
                    throw new JSONException(e.getMessage(), e);
                }
            } else if (index < this.sp) {
                return this.buf[index];
            } else {
                return 26;
            }
        }
        return this.buf[index];
    }

    public final int indexOf(char ch, int startIndex) {
        int offset = startIndex - this.bp;
        while (ch != charAt(this.bp + offset)) {
            if (ch == 26) {
                return -1;
            }
            offset++;
        }
        return this.bp + offset;
    }

    public final String addSymbol(int offset, int len, int hash, SymbolTable symbolTable) {
        return symbolTable.addSymbol(this.buf, offset, len, hash);
    }

    public final char next() {
        int index = this.bp + 1;
        this.bp = index;
        int i = this.bufLength;
        if (index >= i) {
            if (i == -1) {
                return 26;
            }
            if (this.sp > 0) {
                int offset = this.bufLength - this.sp;
                if (this.ch == '\"') {
                    offset--;
                }
                char[] cArr = this.buf;
                System.arraycopy(cArr, offset, cArr, 0, this.sp);
            }
            this.np = -1;
            int i2 = this.sp;
            this.bp = i2;
            index = i2;
            try {
                int startPos = this.bp;
                int readLength = this.buf.length - startPos;
                if (readLength == 0) {
                    char[] newBuf = new char[(this.buf.length * 2)];
                    System.arraycopy(this.buf, 0, newBuf, 0, this.buf.length);
                    this.buf = newBuf;
                    readLength = newBuf.length - startPos;
                }
                int read = this.reader.read(this.buf, this.bp, readLength);
                this.bufLength = read;
                if (read == 0) {
                    throw new JSONException("illegal stat, textLength is zero");
                } else if (read == -1) {
                    this.ch = 26;
                    return 26;
                } else {
                    this.bufLength = read + this.bp;
                }
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        }
        char c = this.buf[index];
        this.ch = c;
        return c;
    }

    /* access modifiers changed from: protected */
    public final void copyTo(int offset, int count, char[] dest) {
        System.arraycopy(this.buf, offset, dest, 0, count);
    }

    public byte[] bytesValue() {
        return Base64.decodeFast(this.buf, this.np + 1, this.sp);
    }

    /* access modifiers changed from: protected */
    public final void arrayCopy(int srcPos, char[] dest, int destPos, int length) {
        System.arraycopy(this.buf, srcPos, dest, destPos, length);
    }

    public final String stringVal() {
        if (this.hasSpecial) {
            return new String(this.sbuf, 0, this.sp);
        }
        int offset = this.np + 1;
        if (offset < 0) {
            throw new IllegalStateException();
        } else if (offset <= this.buf.length - this.sp) {
            return new String(this.buf, offset, this.sp);
        } else {
            throw new IllegalStateException();
        }
    }

    public final String subString(int offset, int count) {
        if (count >= 0) {
            return new String(this.buf, offset, count);
        }
        throw new StringIndexOutOfBoundsException(count);
    }

    public final String numberString() {
        int offset = this.np;
        if (offset == -1) {
            offset = 0;
        }
        char chLocal = charAt((this.sp + offset) - 1);
        int sp = this.sp;
        if (chLocal == 'L' || chLocal == 'S' || chLocal == 'B' || chLocal == 'F' || chLocal == 'D') {
            sp--;
        }
        return new String(this.buf, offset, sp);
    }

    public void close() {
        super.close();
        BUF_REF_LOCAL.set(new SoftReference(this.buf));
        this.buf = null;
        IOUtils.close(this.reader);
    }

    public boolean isEOF() {
        if (this.bufLength == -1 || this.bp == this.buf.length) {
            return true;
        }
        return this.ch == 26 && this.bp + 1 == this.buf.length;
    }
}
