package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.Base64;
import com.alibaba.fastjson.util.IOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import kotlin.UByte;
import kotlin.text.Typography;

public final class SerializeWriter extends Writer {
    private static final ThreadLocal<SoftReference<char[]>> bufLocal = new ThreadLocal<>();
    protected char[] buf;
    protected int count;
    private int features;
    private final Writer writer;

    public SerializeWriter() {
        this((Writer) null);
    }

    public SerializeWriter(Writer writer2) {
        this.writer = writer2;
        this.features = JSON.DEFAULT_GENERATE_FEATURE;
        SoftReference<char[]> ref = bufLocal.get();
        if (ref != null) {
            this.buf = ref.get();
            bufLocal.set((Object) null);
        }
        if (this.buf == null) {
            this.buf = new char[1024];
        }
    }

    public SerializeWriter(SerializerFeature... features2) {
        this((Writer) null, features2);
    }

    public SerializeWriter(Writer writer2, SerializerFeature... features2) {
        this.writer = writer2;
        SoftReference<char[]> ref = bufLocal.get();
        if (ref != null) {
            this.buf = ref.get();
            bufLocal.set((Object) null);
        }
        if (this.buf == null) {
            this.buf = new char[1024];
        }
        int featuresValue = 0;
        for (SerializerFeature feature : features2) {
            featuresValue |= feature.getMask();
        }
        this.features = featuresValue;
    }

    public int getBufferLength() {
        return this.buf.length;
    }

    public SerializeWriter(int initialSize) {
        this((Writer) null, initialSize);
    }

    public SerializeWriter(Writer writer2, int initialSize) {
        this.writer = writer2;
        if (initialSize > 0) {
            this.buf = new char[initialSize];
            return;
        }
        throw new IllegalArgumentException("Negative initial size: " + initialSize);
    }

    public void config(SerializerFeature feature, boolean state) {
        if (state) {
            this.features |= feature.getMask();
        } else {
            this.features &= ~feature.getMask();
        }
    }

    public boolean isEnabled(SerializerFeature feature) {
        return SerializerFeature.isEnabled(this.features, feature);
    }

    public void write(int c) {
        int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                flush();
                newcount = 1;
            }
        }
        this.buf[this.count] = (char) c;
        this.count = newcount;
    }

    public void write(char c) {
        int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                flush();
                newcount = 1;
            }
        }
        this.buf[this.count] = c;
        this.count = newcount;
    }

    public void write(char[] c, int off, int len) {
        if (off < 0 || off > c.length || len < 0 || off + len > c.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len != 0) {
            int newcount = this.count + len;
            if (newcount > this.buf.length) {
                if (this.writer == null) {
                    expandCapacity(newcount);
                } else {
                    do {
                        char[] cArr = this.buf;
                        int length = cArr.length;
                        int i = this.count;
                        int rest = length - i;
                        System.arraycopy(c, off, cArr, i, rest);
                        this.count = this.buf.length;
                        flush();
                        len -= rest;
                        off += rest;
                    } while (len > this.buf.length);
                    newcount = len;
                }
            }
            System.arraycopy(c, off, this.buf, this.count, len);
            this.count = newcount;
        }
    }

    public void expandCapacity(int minimumCapacity) {
        int newCapacity = ((this.buf.length * 3) / 2) + 1;
        if (newCapacity < minimumCapacity) {
            newCapacity = minimumCapacity;
        }
        char[] newValue = new char[newCapacity];
        System.arraycopy(this.buf, 0, newValue, 0, this.count);
        this.buf = newValue;
    }

    public void write(String str, int off, int len) {
        int newcount = this.count + len;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                do {
                    char[] cArr = this.buf;
                    int length = cArr.length;
                    int i = this.count;
                    int rest = length - i;
                    str.getChars(off, off + rest, cArr, i);
                    this.count = this.buf.length;
                    flush();
                    len -= rest;
                    off += rest;
                } while (len > this.buf.length);
                newcount = len;
            }
        }
        str.getChars(off, off + len, this.buf, this.count);
        this.count = newcount;
    }

    public void writeTo(Writer out) throws IOException {
        if (this.writer == null) {
            out.write(this.buf, 0, this.count);
            return;
        }
        throw new UnsupportedOperationException("writer not null");
    }

    public void writeTo(OutputStream out, String charsetName) throws IOException {
        writeTo(out, Charset.forName(charsetName));
    }

    public void writeTo(OutputStream out, Charset charset) throws IOException {
        if (this.writer == null) {
            out.write(new String(this.buf, 0, this.count).getBytes(charset.name()));
            return;
        }
        throw new UnsupportedOperationException("writer not null");
    }

    public SerializeWriter append(CharSequence csq) {
        String s = csq == null ? "null" : csq.toString();
        write(s, 0, s.length());
        return this;
    }

    public SerializeWriter append(CharSequence csq, int start, int end) {
        String s = (csq == null ? "null" : csq).subSequence(start, end).toString();
        write(s, 0, s.length());
        return this;
    }

    public SerializeWriter append(char c) {
        write(c);
        return this;
    }

    public void reset() {
        this.count = 0;
    }

    public char[] toCharArray() {
        if (this.writer == null) {
            int i = this.count;
            char[] newValue = new char[i];
            System.arraycopy(this.buf, 0, newValue, 0, i);
            return newValue;
        }
        throw new UnsupportedOperationException("writer not null");
    }

    public byte[] toBytes(String charsetName) {
        if (this.writer == null) {
            if (charsetName == null) {
                charsetName = "UTF-8";
            }
            try {
                return new String(this.buf, 0, this.count).getBytes(charsetName);
            } catch (UnsupportedEncodingException e) {
                throw new JSONException("toBytes error", e);
            }
        } else {
            throw new UnsupportedOperationException("writer not null");
        }
    }

    public int size() {
        return this.count;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    public void close() {
        if (this.writer != null && this.count > 0) {
            flush();
        }
        if (this.buf.length <= 8192) {
            bufLocal.set(new SoftReference(this.buf));
        }
        this.buf = null;
    }

    public void write(String text) {
        if (text == null) {
            writeNull();
        } else {
            write(text, 0, text.length());
        }
    }

    public void writeInt(int i) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            return;
        }
        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);
        int newcount = this.count + size;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                char[] chars = new char[size];
                IOUtils.getChars((long) i, size, chars);
                write(chars, 0, chars.length);
                return;
            }
        }
        IOUtils.getChars((long) i, newcount, this.buf);
        this.count = newcount;
    }

    public void writeByteArray(byte[] bytes) {
        byte[] bArr = bytes;
        int bytesLen = bArr.length;
        boolean singleQuote = isEnabled(SerializerFeature.UseSingleQuotes);
        char quote = singleQuote ? '\'' : Typography.quote;
        if (bytesLen == 0) {
            write(singleQuote ? "''" : "\"\"");
            return;
        }
        char[] CA = Base64.CA;
        int eLen = (bytesLen / 3) * 3;
        int offset = this.count;
        int newcount = this.count + ((((bytesLen - 1) / 3) + 1) << 2) + 2;
        if (newcount > this.buf.length) {
            if (this.writer != null) {
                write(quote);
                int i = 0;
                while (i < eLen) {
                    int s = i + 1;
                    int s2 = s + 1;
                    int i2 = ((bArr[i] & UByte.MAX_VALUE) << 16) | ((bArr[s] & UByte.MAX_VALUE) << 8) | (bArr[s2] & 255);
                    write(CA[(i2 >>> 18) & 63]);
                    write(CA[(i2 >>> 12) & 63]);
                    write(CA[(i2 >>> 6) & 63]);
                    write(CA[i2 & 63]);
                    i = s2 + 1;
                }
                int left = bytesLen - eLen;
                if (left > 0) {
                    int i3 = (left == 2 ? (bArr[bytesLen - 1] & UByte.MAX_VALUE) << 2 : 0) | ((bArr[eLen] & UByte.MAX_VALUE) << 10);
                    write(CA[i3 >> 12]);
                    write(CA[(i3 >>> 6) & 63]);
                    write(left == 2 ? CA[i3 & 63] : '=');
                    write('=');
                }
                write(quote);
                return;
            }
            expandCapacity(newcount);
        }
        this.count = newcount;
        int offset2 = offset + 1;
        this.buf[offset] = quote;
        int i4 = 0;
        int d = offset2;
        while (i4 < eLen) {
            int s3 = i4 + 1;
            int s4 = s3 + 1;
            int i5 = ((bArr[i4] & UByte.MAX_VALUE) << 16) | ((bArr[s3] & UByte.MAX_VALUE) << 8);
            int s5 = s4 + 1;
            int i6 = i5 | (bArr[s4] & 255);
            char[] cArr = this.buf;
            int d2 = d + 1;
            cArr[d] = CA[(i6 >>> 18) & 63];
            int d3 = d2 + 1;
            cArr[d2] = CA[(i6 >>> 12) & 63];
            int d4 = d3 + 1;
            cArr[d3] = CA[(i6 >>> 6) & 63];
            d = d4 + 1;
            cArr[d4] = CA[i6 & 63];
            i4 = s5;
        }
        int left2 = bytesLen - eLen;
        if (left2 > 0) {
            int i7 = ((bArr[eLen] & UByte.MAX_VALUE) << 10) | (left2 == 2 ? (bArr[bytesLen - 1] & UByte.MAX_VALUE) << 2 : 0);
            char[] cArr2 = this.buf;
            cArr2[newcount - 5] = CA[i7 >> 12];
            cArr2[newcount - 4] = CA[(i7 >>> 6) & 63];
            cArr2[newcount - 3] = left2 == 2 ? CA[i7 & 63] : '=';
            this.buf[newcount - 2] = '=';
        }
        this.buf[newcount - 1] = quote;
    }

    public void writeLongAndChar(long i, char c) throws IOException {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            write(c);
            return;
        }
        int newcount0 = this.count + (i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i));
        int newcount1 = newcount0 + 1;
        if (newcount1 > this.buf.length) {
            if (this.writer != null) {
                writeLong(i);
                write(c);
                return;
            }
            expandCapacity(newcount1);
        }
        IOUtils.getChars(i, newcount0, this.buf);
        this.buf[newcount0] = c;
        this.count = newcount1;
    }

    public void writeLong(long i) {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            return;
        }
        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);
        int newcount = this.count + size;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else {
                char[] chars = new char[size];
                IOUtils.getChars(i, size, chars);
                write(chars, 0, chars.length);
                return;
            }
        }
        IOUtils.getChars(i, newcount, this.buf);
        this.count = newcount;
    }

    public void writeNull() {
        write("null");
    }

    private void writeStringWithDoubleQuote(String text, char seperator) {
        writeStringWithDoubleQuote(text, seperator, true);
    }

    private void writeStringWithDoubleQuote(String text, char seperator, boolean checkSpecial) {
        String str = text;
        char c = seperator;
        if (str == null) {
            writeNull();
            if (c != 0) {
                write(c);
                return;
            }
            return;
        }
        int len = text.length();
        int newcount = this.count + len + 2;
        if (c != 0) {
            newcount++;
        }
        char c2 = 10;
        char c3 = 12;
        char c4 = 8;
        char c5 = '\\';
        if (newcount > this.buf.length) {
            if (this.writer != null) {
                write((char) Typography.quote);
                for (int i = 0; i < text.length(); i++) {
                    char ch = str.charAt(i);
                    if (isEnabled(SerializerFeature.BrowserCompatible)) {
                        if (ch == 8 || ch == 12 || ch == 10 || ch == 13 || ch == 9 || ch == '\"' || ch == '/' || ch == '\\') {
                            write('\\');
                            write(IOUtils.replaceChars[ch]);
                        } else {
                            if (ch < ' ') {
                                write('\\');
                                write('u');
                                write('0');
                                write('0');
                                write(IOUtils.ASCII_CHARS[ch * 2]);
                                write(IOUtils.ASCII_CHARS[(ch * 2) + 1]);
                            } else if (ch >= 127) {
                                write('\\');
                                write('u');
                                write(IOUtils.DIGITS[(ch >>> 12) & 15]);
                                write(IOUtils.DIGITS[(ch >>> 8) & 15]);
                                write(IOUtils.DIGITS[(ch >>> 4) & 15]);
                                write(IOUtils.DIGITS[ch & 15]);
                            }
                        }
                    } else if ((ch < IOUtils.specicalFlags_doubleQuotes.length && IOUtils.specicalFlags_doubleQuotes[ch] != 0) || (ch == '/' && isEnabled(SerializerFeature.WriteSlashAsSpecial))) {
                        write('\\');
                        write(IOUtils.replaceChars[ch]);
                    }
                    write(ch);
                }
                write((char) Typography.quote);
                if (c != 0) {
                    write(c);
                    return;
                }
                return;
            }
            expandCapacity(newcount);
        }
        int i2 = this.count;
        int start = i2 + 1;
        int end = start + len;
        char[] cArr = this.buf;
        cArr[i2] = Typography.quote;
        str.getChars(0, len, cArr, start);
        this.count = newcount;
        if (isEnabled(SerializerFeature.BrowserCompatible)) {
            int lastSpecialIndex = -1;
            int i3 = start;
            while (i3 < end) {
                char ch2 = this.buf[i3];
                if (ch2 == '\"' || ch2 == '/' || ch2 == c5) {
                    lastSpecialIndex = i3;
                    newcount++;
                } else if (ch2 == 8 || ch2 == 12 || ch2 == 10 || ch2 == 13 || ch2 == 9) {
                    lastSpecialIndex = i3;
                    newcount++;
                } else if (ch2 < ' ') {
                    lastSpecialIndex = i3;
                    newcount += 5;
                } else if (ch2 >= 127) {
                    lastSpecialIndex = i3;
                    newcount += 5;
                }
                i3++;
                c5 = '\\';
            }
            if (newcount > this.buf.length) {
                expandCapacity(newcount);
            }
            this.count = newcount;
            int i4 = lastSpecialIndex;
            while (i4 >= start) {
                char[] cArr2 = this.buf;
                char ch3 = cArr2[i4];
                if (ch3 == c4 || ch3 == c3 || ch3 == c2 || ch3 == 13 || ch3 == 9) {
                    char[] cArr3 = this.buf;
                    System.arraycopy(cArr3, i4 + 1, cArr3, i4 + 2, (end - i4) - 1);
                    char[] cArr4 = this.buf;
                    cArr4[i4] = '\\';
                    cArr4[i4 + 1] = IOUtils.replaceChars[ch3];
                    end++;
                } else if (ch3 == '\"' || ch3 == '/' || ch3 == '\\') {
                    char[] cArr5 = this.buf;
                    System.arraycopy(cArr5, i4 + 1, cArr5, i4 + 2, (end - i4) - 1);
                    char[] cArr6 = this.buf;
                    cArr6[i4] = '\\';
                    cArr6[i4 + 1] = ch3;
                    end++;
                } else if (ch3 < ' ') {
                    System.arraycopy(cArr2, i4 + 1, cArr2, i4 + 6, (end - i4) - 1);
                    char[] cArr7 = this.buf;
                    cArr7[i4] = '\\';
                    cArr7[i4 + 1] = 'u';
                    cArr7[i4 + 2] = '0';
                    cArr7[i4 + 3] = '0';
                    cArr7[i4 + 4] = IOUtils.ASCII_CHARS[ch3 * 2];
                    this.buf[i4 + 5] = IOUtils.ASCII_CHARS[(ch3 * 2) + 1];
                    end += 5;
                } else if (ch3 >= 127) {
                    System.arraycopy(cArr2, i4 + 1, cArr2, i4 + 6, (end - i4) - 1);
                    char[] cArr8 = this.buf;
                    cArr8[i4] = '\\';
                    cArr8[i4 + 1] = 'u';
                    cArr8[i4 + 2] = IOUtils.DIGITS[(ch3 >>> 12) & 15];
                    this.buf[i4 + 3] = IOUtils.DIGITS[(ch3 >>> 8) & 15];
                    this.buf[i4 + 4] = IOUtils.DIGITS[(ch3 >>> 4) & 15];
                    this.buf[i4 + 5] = IOUtils.DIGITS[ch3 & 15];
                    end += 5;
                }
                i4--;
                c2 = 10;
                c3 = 12;
                c4 = 8;
            }
            if (c != 0) {
                char[] cArr9 = this.buf;
                int i5 = this.count;
                cArr9[i5 - 2] = Typography.quote;
                cArr9[i5 - 1] = c;
                return;
            }
            this.buf[this.count - 1] = Typography.quote;
            return;
        }
        int specialCount = 0;
        int lastSpecialIndex2 = -1;
        int firstSpecialIndex = -1;
        char lastSpecial = 0;
        if (checkSpecial) {
            for (int i6 = start; i6 < end; i6++) {
                char ch4 = this.buf[i6];
                if (ch4 == 8232) {
                    specialCount++;
                    lastSpecialIndex2 = i6;
                    lastSpecial = ch4;
                    newcount += 4;
                    if (firstSpecialIndex == -1) {
                        firstSpecialIndex = i6;
                    }
                } else if (ch4 >= ']') {
                    if (ch4 >= 127 && ch4 <= 160) {
                        if (firstSpecialIndex == -1) {
                            firstSpecialIndex = i6;
                        }
                        specialCount++;
                        lastSpecialIndex2 = i6;
                        lastSpecial = ch4;
                        newcount += 4;
                    }
                } else if (isSpecial(ch4, this.features)) {
                    specialCount++;
                    lastSpecialIndex2 = i6;
                    lastSpecial = ch4;
                    if (ch4 < IOUtils.specicalFlags_doubleQuotes.length && IOUtils.specicalFlags_doubleQuotes[ch4] == 4) {
                        newcount += 4;
                    }
                    if (firstSpecialIndex == -1) {
                        firstSpecialIndex = i6;
                    }
                }
            }
            if (specialCount > 0) {
                int newcount2 = newcount + specialCount;
                if (newcount2 > this.buf.length) {
                    expandCapacity(newcount2);
                }
                this.count = newcount2;
                if (specialCount != 1) {
                    if (specialCount > 1) {
                        int bufIndex = firstSpecialIndex;
                        for (int i7 = firstSpecialIndex - start; i7 < text.length(); i7++) {
                            char ch5 = str.charAt(i7);
                            if ((ch5 < IOUtils.specicalFlags_doubleQuotes.length && IOUtils.specicalFlags_doubleQuotes[ch5] != 0) || (ch5 == '/' && isEnabled(SerializerFeature.WriteSlashAsSpecial))) {
                                int bufIndex2 = bufIndex + 1;
                                this.buf[bufIndex] = '\\';
                                if (IOUtils.specicalFlags_doubleQuotes[ch5] == 4) {
                                    char[] cArr10 = this.buf;
                                    int bufIndex3 = bufIndex2 + 1;
                                    cArr10[bufIndex2] = 'u';
                                    int bufIndex4 = bufIndex3 + 1;
                                    cArr10[bufIndex3] = IOUtils.DIGITS[(ch5 >>> 12) & 15];
                                    int bufIndex5 = bufIndex4 + 1;
                                    this.buf[bufIndex4] = IOUtils.DIGITS[(ch5 >>> 8) & 15];
                                    int bufIndex6 = bufIndex5 + 1;
                                    this.buf[bufIndex5] = IOUtils.DIGITS[(ch5 >>> 4) & 15];
                                    this.buf[bufIndex6] = IOUtils.DIGITS[ch5 & 15];
                                    end += 5;
                                    bufIndex = bufIndex6 + 1;
                                } else {
                                    this.buf[bufIndex2] = IOUtils.replaceChars[ch5];
                                    end++;
                                    bufIndex = bufIndex2 + 1;
                                }
                            } else if (ch5 == 8232) {
                                char[] cArr11 = this.buf;
                                int bufIndex7 = bufIndex + 1;
                                cArr11[bufIndex] = '\\';
                                int bufIndex8 = bufIndex7 + 1;
                                cArr11[bufIndex7] = 'u';
                                int bufIndex9 = bufIndex8 + 1;
                                cArr11[bufIndex8] = IOUtils.DIGITS[(ch5 >>> 12) & 15];
                                int bufIndex10 = bufIndex9 + 1;
                                this.buf[bufIndex9] = IOUtils.DIGITS[(ch5 >>> 8) & 15];
                                int bufIndex11 = bufIndex10 + 1;
                                this.buf[bufIndex10] = IOUtils.DIGITS[(ch5 >>> 4) & 15];
                                this.buf[bufIndex11] = IOUtils.DIGITS[ch5 & 15];
                                end += 5;
                                bufIndex = bufIndex11 + 1;
                            } else {
                                this.buf[bufIndex] = ch5;
                                bufIndex++;
                            }
                        }
                    }
                } else if (lastSpecial == 8232) {
                    char[] cArr12 = this.buf;
                    System.arraycopy(cArr12, lastSpecialIndex2 + 1, cArr12, lastSpecialIndex2 + 6, (end - lastSpecialIndex2) - 1);
                    char[] cArr13 = this.buf;
                    cArr13[lastSpecialIndex2] = '\\';
                    int lastSpecialIndex3 = lastSpecialIndex2 + 1;
                    cArr13[lastSpecialIndex3] = 'u';
                    int lastSpecialIndex4 = lastSpecialIndex3 + 1;
                    cArr13[lastSpecialIndex4] = '2';
                    int lastSpecialIndex5 = lastSpecialIndex4 + 1;
                    cArr13[lastSpecialIndex5] = '0';
                    int lastSpecialIndex6 = lastSpecialIndex5 + 1;
                    cArr13[lastSpecialIndex6] = '2';
                    cArr13[lastSpecialIndex6 + 1] = '8';
                    int i8 = len;
                } else {
                    char ch6 = lastSpecial;
                    if (ch6 >= IOUtils.specicalFlags_doubleQuotes.length || IOUtils.specicalFlags_doubleQuotes[ch6] != 4) {
                        char[] cArr14 = this.buf;
                        System.arraycopy(cArr14, lastSpecialIndex2 + 1, cArr14, lastSpecialIndex2 + 2, (end - lastSpecialIndex2) - 1);
                        char[] cArr15 = this.buf;
                        cArr15[lastSpecialIndex2] = '\\';
                        cArr15[lastSpecialIndex2 + 1] = IOUtils.replaceChars[ch6];
                    } else {
                        char[] cArr16 = this.buf;
                        System.arraycopy(cArr16, lastSpecialIndex2 + 1, cArr16, lastSpecialIndex2 + 6, (end - lastSpecialIndex2) - 1);
                        int bufIndex12 = lastSpecialIndex2;
                        int i9 = len;
                        char[] cArr17 = this.buf;
                        int bufIndex13 = bufIndex12 + 1;
                        cArr17[bufIndex12] = '\\';
                        int bufIndex14 = bufIndex13 + 1;
                        cArr17[bufIndex13] = 'u';
                        int bufIndex15 = bufIndex14 + 1;
                        cArr17[bufIndex14] = IOUtils.DIGITS[(ch6 >>> 12) & 15];
                        int bufIndex16 = bufIndex15 + 1;
                        this.buf[bufIndex15] = IOUtils.DIGITS[(ch6 >>> 8) & 15];
                        int bufIndex17 = bufIndex16 + 1;
                        this.buf[bufIndex16] = IOUtils.DIGITS[(ch6 >>> 4) & 15];
                        int i10 = bufIndex17 + 1;
                        this.buf[bufIndex17] = IOUtils.DIGITS[ch6 & 15];
                    }
                }
            }
        }
        if (c != 0) {
            char[] cArr18 = this.buf;
            int i11 = this.count;
            cArr18[i11 - 2] = Typography.quote;
            cArr18[i11 - 1] = c;
            return;
        }
        this.buf[this.count - 1] = Typography.quote;
    }

    public void write(boolean value) {
        if (value) {
            write("true");
        } else {
            write("false");
        }
    }

    public void writeFieldValue(char seperator, String name, long value) {
        if (value == Long.MIN_VALUE || !isEnabled(SerializerFeature.QuoteFieldNames)) {
            writeFieldValue1(seperator, name, value);
            return;
        }
        char keySeperator = isEnabled(SerializerFeature.UseSingleQuotes) ? '\'' : Typography.quote;
        int intSize = value < 0 ? IOUtils.stringSize(-value) + 1 : IOUtils.stringSize(value);
        int nameLen = name.length();
        int newcount = this.count + nameLen + 4 + intSize;
        if (newcount > this.buf.length) {
            if (this.writer != null) {
                write(seperator);
                writeFieldName(name);
                writeLong(value);
                return;
            }
            expandCapacity(newcount);
        }
        int start = this.count;
        this.count = newcount;
        char[] cArr = this.buf;
        cArr[start] = seperator;
        int nameEnd = start + nameLen + 1;
        cArr[start + 1] = keySeperator;
        name.getChars(0, nameLen, cArr, start + 2);
        char[] cArr2 = this.buf;
        cArr2[nameEnd + 1] = keySeperator;
        cArr2[nameEnd + 2] = ':';
        IOUtils.getChars(value, this.count, cArr2);
    }

    public void writeFieldValue1(char seperator, String name, long value) {
        write(seperator);
        writeFieldName(name);
        writeLong(value);
    }

    public void writeFieldValue(char seperator, String name, String value) {
        if (!isEnabled(SerializerFeature.QuoteFieldNames)) {
            write(seperator);
            writeFieldName(name);
            if (value == null) {
                writeNull();
            } else {
                writeString(value);
            }
        } else if (isEnabled(SerializerFeature.UseSingleQuotes)) {
            write(seperator);
            writeFieldName(name);
            if (value == null) {
                writeNull();
            } else {
                writeString(value);
            }
        } else if (isEnabled(SerializerFeature.BrowserCompatible)) {
            write(seperator);
            writeStringWithDoubleQuote(name, ':');
            writeStringWithDoubleQuote(value, 0);
        } else {
            writeFieldValueStringWithDoubleQuote(seperator, name, value, true);
        }
    }

    private void writeFieldValueStringWithDoubleQuote(char seperator, String name, String value, boolean checkSpecial) {
        int valueLen;
        int newcount;
        int newcount2;
        String str = name;
        String str2 = value;
        boolean z = checkSpecial;
        int nameLen = name.length();
        int newcount3 = this.count;
        if (str2 == null) {
            valueLen = 4;
            newcount = newcount3 + nameLen + 8;
        } else {
            valueLen = value.length();
            newcount = newcount3 + nameLen + valueLen + 6;
        }
        if (newcount > this.buf.length) {
            if (this.writer != null) {
                write(seperator);
                writeStringWithDoubleQuote(str, ':', z);
                writeStringWithDoubleQuote(str2, 0, z);
                return;
            }
            expandCapacity(newcount);
        }
        char[] cArr = this.buf;
        int i = this.count;
        cArr[i] = seperator;
        int nameStart = i + 2;
        int nameEnd = nameStart + nameLen;
        cArr[i + 1] = Typography.quote;
        str.getChars(0, nameLen, cArr, nameStart);
        this.count = newcount;
        char[] cArr2 = this.buf;
        cArr2[nameEnd] = Typography.quote;
        int index = nameEnd + 1;
        int index2 = index + 1;
        cArr2[index] = ':';
        if (str2 == null) {
            int index3 = index2 + 1;
            cArr2[index2] = 'n';
            int index4 = index3 + 1;
            cArr2[index3] = 'u';
            int index5 = index4 + 1;
            cArr2[index4] = 'l';
            int i2 = index5 + 1;
            cArr2[index5] = 'l';
            return;
        }
        cArr2[index2] = Typography.quote;
        int valueStart = index2 + 1;
        int valueEnd = valueStart + valueLen;
        str2.getChars(0, valueLen, cArr2, valueStart);
        if (!z || isEnabled(SerializerFeature.DisableCheckSpecialChar)) {
            int i3 = valueLen;
        } else {
            int specialCount = 0;
            int lastSpecialIndex = -1;
            int firstSpecialIndex = -1;
            char lastSpecial = 0;
            int i4 = valueStart;
            while (i4 < valueEnd) {
                char ch = this.buf[i4];
                if (ch == 8232) {
                    specialCount++;
                    char lastSpecial2 = ch;
                    newcount += 4;
                    int lastSpecialIndex2 = i4;
                    if (firstSpecialIndex == -1) {
                        firstSpecialIndex = i4;
                        lastSpecial = lastSpecial2;
                        lastSpecialIndex = lastSpecialIndex2;
                    } else {
                        lastSpecial = lastSpecial2;
                        lastSpecialIndex = lastSpecialIndex2;
                    }
                } else if (ch >= ']') {
                    if (ch >= 127 && ch <= 160) {
                        if (firstSpecialIndex == -1) {
                            firstSpecialIndex = i4;
                        }
                        specialCount++;
                        newcount += 4;
                        lastSpecial = ch;
                        lastSpecialIndex = i4;
                    }
                } else if (isSpecial(ch, this.features)) {
                    specialCount++;
                    char lastSpecial3 = ch;
                    int lastSpecialIndex3 = i4;
                    if (ch < IOUtils.specicalFlags_doubleQuotes.length) {
                        char c = ch;
                        if (IOUtils.specicalFlags_doubleQuotes[ch] == 4) {
                            newcount += 4;
                        }
                    }
                    if (firstSpecialIndex == -1) {
                        firstSpecialIndex = i4;
                        lastSpecial = lastSpecial3;
                        lastSpecialIndex = lastSpecialIndex3;
                    } else {
                        lastSpecial = lastSpecial3;
                        lastSpecialIndex = lastSpecialIndex3;
                    }
                }
                i4++;
                String str3 = name;
                boolean z2 = checkSpecial;
            }
            if (specialCount > 0) {
                int newcount4 = newcount + specialCount;
                if (newcount4 > this.buf.length) {
                    expandCapacity(newcount4);
                }
                this.count = newcount4;
                if (specialCount == 1) {
                    char lastSpecial4 = lastSpecial;
                    if (lastSpecial4 == 8232) {
                        int i5 = nameLen;
                        newcount2 = newcount4;
                        char[] cArr3 = this.buf;
                        System.arraycopy(cArr3, lastSpecialIndex + 1, cArr3, lastSpecialIndex + 6, (valueEnd - lastSpecialIndex) - 1);
                        char[] cArr4 = this.buf;
                        cArr4[lastSpecialIndex] = '\\';
                        int lastSpecialIndex4 = lastSpecialIndex + 1;
                        cArr4[lastSpecialIndex4] = 'u';
                        int lastSpecialIndex5 = lastSpecialIndex4 + 1;
                        cArr4[lastSpecialIndex5] = '2';
                        int lastSpecialIndex6 = lastSpecialIndex5 + 1;
                        cArr4[lastSpecialIndex6] = '0';
                        int lastSpecialIndex7 = lastSpecialIndex6 + 1;
                        cArr4[lastSpecialIndex7] = '2';
                        cArr4[lastSpecialIndex7 + 1] = '8';
                        int i6 = valueLen;
                    } else {
                        newcount2 = newcount4;
                        char ch2 = lastSpecial4;
                        if (ch2 >= IOUtils.specicalFlags_doubleQuotes.length || IOUtils.specicalFlags_doubleQuotes[ch2] != 4) {
                            char[] cArr5 = this.buf;
                            System.arraycopy(cArr5, lastSpecialIndex + 1, cArr5, lastSpecialIndex + 2, (valueEnd - lastSpecialIndex) - 1);
                            char[] cArr6 = this.buf;
                            cArr6[lastSpecialIndex] = '\\';
                            cArr6[lastSpecialIndex + 1] = IOUtils.replaceChars[ch2];
                        } else {
                            int srcPos = lastSpecialIndex + 1;
                            int i7 = valueLen;
                            char[] cArr7 = this.buf;
                            System.arraycopy(cArr7, srcPos, cArr7, lastSpecialIndex + 6, (valueEnd - lastSpecialIndex) - 1);
                            int bufIndex = lastSpecialIndex;
                            int i8 = srcPos;
                            char[] cArr8 = this.buf;
                            int bufIndex2 = bufIndex + 1;
                            cArr8[bufIndex] = '\\';
                            int bufIndex3 = bufIndex2 + 1;
                            cArr8[bufIndex2] = 'u';
                            int bufIndex4 = bufIndex3 + 1;
                            cArr8[bufIndex3] = IOUtils.DIGITS[(ch2 >>> 12) & 15];
                            int bufIndex5 = bufIndex4 + 1;
                            this.buf[bufIndex4] = IOUtils.DIGITS[(ch2 >>> 8) & 15];
                            int bufIndex6 = bufIndex5 + 1;
                            this.buf[bufIndex5] = IOUtils.DIGITS[(ch2 >>> 4) & 15];
                            int i9 = bufIndex6 + 1;
                            this.buf[bufIndex6] = IOUtils.DIGITS[ch2 & 15];
                        }
                    }
                } else {
                    newcount2 = newcount4;
                    int i10 = valueLen;
                    char c2 = lastSpecial;
                    if (specialCount > 1) {
                        int bufIndex7 = firstSpecialIndex;
                        for (int i11 = firstSpecialIndex - valueStart; i11 < value.length(); i11++) {
                            char ch3 = str2.charAt(i11);
                            if ((ch3 < IOUtils.specicalFlags_doubleQuotes.length && IOUtils.specicalFlags_doubleQuotes[ch3] != 0) || (ch3 == '/' && isEnabled(SerializerFeature.WriteSlashAsSpecial))) {
                                int bufIndex8 = bufIndex7 + 1;
                                this.buf[bufIndex7] = '\\';
                                if (IOUtils.specicalFlags_doubleQuotes[ch3] == 4) {
                                    char[] cArr9 = this.buf;
                                    int bufIndex9 = bufIndex8 + 1;
                                    cArr9[bufIndex8] = 'u';
                                    int bufIndex10 = bufIndex9 + 1;
                                    cArr9[bufIndex9] = IOUtils.DIGITS[(ch3 >>> 12) & 15];
                                    int bufIndex11 = bufIndex10 + 1;
                                    this.buf[bufIndex10] = IOUtils.DIGITS[(ch3 >>> 8) & 15];
                                    int bufIndex12 = bufIndex11 + 1;
                                    this.buf[bufIndex11] = IOUtils.DIGITS[(ch3 >>> 4) & 15];
                                    this.buf[bufIndex12] = IOUtils.DIGITS[ch3 & 15];
                                    valueEnd += 5;
                                    bufIndex7 = bufIndex12 + 1;
                                } else {
                                    this.buf[bufIndex8] = IOUtils.replaceChars[ch3];
                                    valueEnd++;
                                    bufIndex7 = bufIndex8 + 1;
                                }
                            } else if (ch3 == 8232) {
                                char[] cArr10 = this.buf;
                                int bufIndex13 = bufIndex7 + 1;
                                cArr10[bufIndex7] = '\\';
                                int bufIndex14 = bufIndex13 + 1;
                                cArr10[bufIndex13] = 'u';
                                int bufIndex15 = bufIndex14 + 1;
                                cArr10[bufIndex14] = IOUtils.DIGITS[(ch3 >>> 12) & 15];
                                int bufIndex16 = bufIndex15 + 1;
                                this.buf[bufIndex15] = IOUtils.DIGITS[(ch3 >>> 8) & 15];
                                int bufIndex17 = bufIndex16 + 1;
                                this.buf[bufIndex16] = IOUtils.DIGITS[(ch3 >>> 4) & 15];
                                this.buf[bufIndex17] = IOUtils.DIGITS[ch3 & 15];
                                valueEnd += 5;
                                bufIndex7 = bufIndex17 + 1;
                            } else {
                                this.buf[bufIndex7] = ch3;
                                bufIndex7++;
                            }
                        }
                        int i12 = newcount2;
                    }
                }
                int i13 = newcount2;
            } else {
                int i14 = valueLen;
                char c3 = lastSpecial;
            }
        }
        this.buf[this.count - 1] = Typography.quote;
    }

    static final boolean isSpecial(char ch, int features2) {
        if (ch == ' ') {
            return false;
        }
        if (ch == '/' && SerializerFeature.isEnabled(features2, SerializerFeature.WriteSlashAsSpecial)) {
            return true;
        }
        if (ch > '#' && ch != '\\') {
            return false;
        }
        if (ch <= 31 || ch == '\\' || ch == '\"') {
            return true;
        }
        return false;
    }

    public void writeString(String text) {
        if (isEnabled(SerializerFeature.UseSingleQuotes)) {
            writeStringWithSingleQuote(text);
        } else {
            writeStringWithDoubleQuote(text, 0);
        }
    }

    private void writeStringWithSingleQuote(String text) {
        String str = text;
        if (str == null) {
            int newcount = this.count + 4;
            if (newcount > this.buf.length) {
                expandCapacity(newcount);
            }
            "null".getChars(0, 4, this.buf, this.count);
            this.count = newcount;
            return;
        }
        int len = text.length();
        int newcount2 = this.count + len + 2;
        char c = 13;
        char c2 = '\\';
        if (newcount2 > this.buf.length) {
            if (this.writer != null) {
                write('\'');
                for (int i = 0; i < text.length(); i++) {
                    char ch = str.charAt(i);
                    if (ch <= 13 || ch == '\\' || ch == '\'' || (ch == '/' && isEnabled(SerializerFeature.WriteSlashAsSpecial))) {
                        write('\\');
                        write(IOUtils.replaceChars[ch]);
                    } else {
                        write(ch);
                    }
                }
                write('\'');
                return;
            }
            expandCapacity(newcount2);
        }
        int i2 = this.count;
        int start = i2 + 1;
        int end = start + len;
        char[] cArr = this.buf;
        cArr[i2] = '\'';
        str.getChars(0, len, cArr, start);
        this.count = newcount2;
        int specialCount = 0;
        int lastSpecialIndex = -1;
        char lastSpecial = 0;
        for (int i3 = start; i3 < end; i3++) {
            char ch2 = this.buf[i3];
            if (ch2 <= 13 || ch2 == '\\' || ch2 == '\'' || (ch2 == '/' && isEnabled(SerializerFeature.WriteSlashAsSpecial))) {
                specialCount++;
                lastSpecialIndex = i3;
                lastSpecial = ch2;
            }
        }
        int newcount3 = newcount2 + specialCount;
        if (newcount3 > this.buf.length) {
            expandCapacity(newcount3);
        }
        this.count = newcount3;
        if (specialCount == 1) {
            char[] cArr2 = this.buf;
            System.arraycopy(cArr2, lastSpecialIndex + 1, cArr2, lastSpecialIndex + 2, (end - lastSpecialIndex) - 1);
            char[] cArr3 = this.buf;
            cArr3[lastSpecialIndex] = '\\';
            cArr3[lastSpecialIndex + 1] = IOUtils.replaceChars[lastSpecial];
        } else if (specialCount > 1) {
            char[] cArr4 = this.buf;
            System.arraycopy(cArr4, lastSpecialIndex + 1, cArr4, lastSpecialIndex + 2, (end - lastSpecialIndex) - 1);
            char[] cArr5 = this.buf;
            cArr5[lastSpecialIndex] = '\\';
            int lastSpecialIndex2 = lastSpecialIndex + 1;
            cArr5[lastSpecialIndex2] = IOUtils.replaceChars[lastSpecial];
            int end2 = end + 1;
            int i4 = lastSpecialIndex2 - 2;
            while (i4 >= start) {
                char ch3 = this.buf[i4];
                if (ch3 > c && ch3 != c2 && ch3 != '\'') {
                    if (ch3 == '/') {
                        if (!isEnabled(SerializerFeature.WriteSlashAsSpecial)) {
                        }
                    }
                    i4--;
                    c = 13;
                }
                char[] cArr6 = this.buf;
                System.arraycopy(cArr6, i4 + 1, cArr6, i4 + 2, (end2 - i4) - 1);
                char[] cArr7 = this.buf;
                c2 = '\\';
                cArr7[i4] = '\\';
                cArr7[i4 + 1] = IOUtils.replaceChars[ch3];
                end2++;
                i4--;
                c = 13;
            }
        }
        this.buf[this.count - 1] = '\'';
    }

    public void writeFieldName(String key) {
        writeFieldName(key, false);
    }

    public void writeFieldName(String key, boolean checkSpecial) {
        if (key == null) {
            write("null:");
        } else if (isEnabled(SerializerFeature.UseSingleQuotes)) {
            if (isEnabled(SerializerFeature.QuoteFieldNames)) {
                writeStringWithSingleQuote(key);
                write(':');
                return;
            }
            writeKeyWithSingleQuoteIfHasSpecial(key);
        } else if (isEnabled(SerializerFeature.QuoteFieldNames)) {
            writeStringWithDoubleQuote(key, ':', checkSpecial);
        } else {
            writeKeyWithDoubleQuoteIfHasSpecial(key);
        }
    }

    private void writeKeyWithDoubleQuoteIfHasSpecial(String text) {
        String str = text;
        byte[] specicalFlags_doubleQuotes = IOUtils.specicalFlags_doubleQuotes;
        int len = text.length();
        int newcount = this.count + len + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else if (len == 0) {
                write((char) Typography.quote);
                write((char) Typography.quote);
                write(':');
                return;
            } else {
                boolean hasSpecial = false;
                int i = 0;
                while (true) {
                    if (i < len) {
                        char ch = str.charAt(i);
                        if (ch < specicalFlags_doubleQuotes.length && specicalFlags_doubleQuotes[ch] != 0) {
                            hasSpecial = true;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                if (hasSpecial) {
                    write((char) Typography.quote);
                }
                for (int i2 = 0; i2 < len; i2++) {
                    char ch2 = str.charAt(i2);
                    if (ch2 >= specicalFlags_doubleQuotes.length || specicalFlags_doubleQuotes[ch2] == 0) {
                        write(ch2);
                    } else {
                        write('\\');
                        write(IOUtils.replaceChars[ch2]);
                    }
                }
                if (hasSpecial) {
                    write((char) Typography.quote);
                }
                write(':');
                return;
            }
        }
        if (len == 0) {
            int i3 = this.count;
            if (i3 + 3 > this.buf.length) {
                expandCapacity(i3 + 3);
            }
            char[] cArr = this.buf;
            int i4 = this.count;
            int i5 = i4 + 1;
            this.count = i5;
            cArr[i4] = Typography.quote;
            int i6 = i5 + 1;
            this.count = i6;
            cArr[i5] = Typography.quote;
            this.count = i6 + 1;
            cArr[i6] = ':';
            return;
        }
        int newCount = this.count;
        int end = newCount + len;
        str.getChars(0, len, this.buf, newCount);
        this.count = newcount;
        boolean hasSpecial2 = false;
        int i7 = newCount;
        while (i7 < end) {
            char[] cArr2 = this.buf;
            char ch3 = cArr2[i7];
            if (ch3 < specicalFlags_doubleQuotes.length && specicalFlags_doubleQuotes[ch3] != 0) {
                if (!hasSpecial2) {
                    newcount += 3;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr3 = this.buf;
                    System.arraycopy(cArr3, i7 + 1, cArr3, i7 + 3, (end - i7) - 1);
                    char[] cArr4 = this.buf;
                    System.arraycopy(cArr4, 0, cArr4, 1, i7);
                    char[] cArr5 = this.buf;
                    cArr5[newCount] = Typography.quote;
                    int i8 = i7 + 1;
                    cArr5[i8] = '\\';
                    i7 = i8 + 1;
                    cArr5[i7] = IOUtils.replaceChars[ch3];
                    end += 2;
                    this.buf[this.count - 2] = Typography.quote;
                    hasSpecial2 = true;
                } else {
                    newcount++;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr6 = this.buf;
                    System.arraycopy(cArr6, i7 + 1, cArr6, i7 + 2, end - i7);
                    char[] cArr7 = this.buf;
                    cArr7[i7] = '\\';
                    i7++;
                    cArr7[i7] = IOUtils.replaceChars[ch3];
                    end++;
                }
            }
            i7++;
        }
        this.buf[this.count - 1] = ':';
    }

    private void writeKeyWithSingleQuoteIfHasSpecial(String text) {
        String str = text;
        byte[] specicalFlags_singleQuotes = IOUtils.specicalFlags_singleQuotes;
        int len = text.length();
        int newcount = this.count + len + 1;
        if (newcount > this.buf.length) {
            if (this.writer == null) {
                expandCapacity(newcount);
            } else if (len == 0) {
                write('\'');
                write('\'');
                write(':');
                return;
            } else {
                boolean hasSpecial = false;
                int i = 0;
                while (true) {
                    if (i < len) {
                        char ch = str.charAt(i);
                        if (ch < specicalFlags_singleQuotes.length && specicalFlags_singleQuotes[ch] != 0) {
                            hasSpecial = true;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                if (hasSpecial) {
                    write('\'');
                }
                for (int i2 = 0; i2 < len; i2++) {
                    char ch2 = str.charAt(i2);
                    if (ch2 >= specicalFlags_singleQuotes.length || specicalFlags_singleQuotes[ch2] == 0) {
                        write(ch2);
                    } else {
                        write('\\');
                        write(IOUtils.replaceChars[ch2]);
                    }
                }
                if (hasSpecial) {
                    write('\'');
                }
                write(':');
                return;
            }
        }
        if (len == 0) {
            int i3 = this.count;
            if (i3 + 3 > this.buf.length) {
                expandCapacity(i3 + 3);
            }
            char[] cArr = this.buf;
            int i4 = this.count;
            int i5 = i4 + 1;
            this.count = i5;
            cArr[i4] = '\'';
            int i6 = i5 + 1;
            this.count = i6;
            cArr[i5] = '\'';
            this.count = i6 + 1;
            cArr[i6] = ':';
            return;
        }
        int newCount = this.count;
        int end = newCount + len;
        str.getChars(0, len, this.buf, newCount);
        this.count = newcount;
        boolean hasSpecial2 = false;
        int i7 = newCount;
        while (i7 < end) {
            char[] cArr2 = this.buf;
            char ch3 = cArr2[i7];
            if (ch3 < specicalFlags_singleQuotes.length && specicalFlags_singleQuotes[ch3] != 0) {
                if (!hasSpecial2) {
                    newcount += 3;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr3 = this.buf;
                    System.arraycopy(cArr3, i7 + 1, cArr3, i7 + 3, (end - i7) - 1);
                    char[] cArr4 = this.buf;
                    System.arraycopy(cArr4, 0, cArr4, 1, i7);
                    char[] cArr5 = this.buf;
                    cArr5[newCount] = '\'';
                    int i8 = i7 + 1;
                    cArr5[i8] = '\\';
                    i7 = i8 + 1;
                    cArr5[i7] = IOUtils.replaceChars[ch3];
                    end += 2;
                    this.buf[this.count - 2] = '\'';
                    hasSpecial2 = true;
                } else {
                    newcount++;
                    if (newcount > cArr2.length) {
                        expandCapacity(newcount);
                    }
                    this.count = newcount;
                    char[] cArr6 = this.buf;
                    System.arraycopy(cArr6, i7 + 1, cArr6, i7 + 2, end - i7);
                    char[] cArr7 = this.buf;
                    cArr7[i7] = '\\';
                    i7++;
                    cArr7[i7] = IOUtils.replaceChars[ch3];
                    end++;
                }
            }
            i7++;
        }
        this.buf[newcount - 1] = ':';
    }

    public void flush() {
        Writer writer2 = this.writer;
        if (writer2 != null) {
            try {
                writer2.write(this.buf, 0, this.count);
                this.writer.flush();
                this.count = 0;
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        }
    }
}
