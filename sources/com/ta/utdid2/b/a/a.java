package com.ta.utdid2.b.a;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import kotlin.text.Typography;
import org.xmlpull.v1.XmlSerializer;

class a implements XmlSerializer {
    private static String a = "xmlpull.org/v1/doc/features.html#indent-output";

    /* renamed from: a  reason: collision with other field name */
    private static final String[] f5a = {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&quot;", null, null, null, "&amp;", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&lt;", null, "&gt;", null};

    /* renamed from: a  reason: collision with other field name */
    private OutputStream f6a;

    /* renamed from: a  reason: collision with other field name */
    private Writer f7a;

    /* renamed from: a  reason: collision with other field name */
    private ByteBuffer f8a = ByteBuffer.allocate(8192);

    /* renamed from: a  reason: collision with other field name */
    private CharsetEncoder f9a;

    /* renamed from: a  reason: collision with other field name */
    private final char[] f10a = new char[8192];
    private boolean e;
    private int mPos;

    a() {
    }

    private void append(char c) throws IOException {
        int i = this.mPos;
        if (i >= 8191) {
            flush();
            i = this.mPos;
        }
        this.f10a[i] = c;
        this.mPos = i + 1;
    }

    private void a(String str, int i, int i2) throws IOException {
        if (i2 > 8192) {
            int i3 = i2 + i;
            while (i < i3) {
                int i4 = i + 8192;
                a(str, i, i4 < i3 ? 8192 : i3 - i);
                i = i4;
            }
            return;
        }
        int i5 = this.mPos;
        if (i5 + i2 > 8192) {
            flush();
            i5 = this.mPos;
        }
        str.getChars(i, i + i2, this.f10a, i5);
        this.mPos = i5 + i2;
    }

    private void append(char[] buf, int i, int length) throws IOException {
        if (length > 8192) {
            int i2 = i + length;
            while (i < i2) {
                int i3 = i + 8192;
                append(buf, i, i3 < i2 ? 8192 : i2 - i);
                i = i3;
            }
            return;
        }
        int i4 = this.mPos;
        if (i4 + length > 8192) {
            flush();
            i4 = this.mPos;
        }
        System.arraycopy(buf, i, this.f10a, i4, length);
        this.mPos = i4 + length;
    }

    private void append(String str) throws IOException {
        a(str, 0, str.length());
    }

    private void a(String str) throws IOException {
        String str2;
        int length = str.length();
        String[] strArr = f5a;
        char length2 = (char) strArr.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt < length2 && (str2 = strArr[charAt]) != null) {
                if (i2 < i) {
                    a(str, i2, i - i2);
                }
                i2 = i + 1;
                append(str2);
            }
            i++;
        }
        if (i2 < i) {
            a(str, i2, i - i2);
        }
    }

    private void a(char[] cArr, int i, int i2) throws IOException {
        String str;
        String[] strArr = f5a;
        char length = (char) strArr.length;
        int i3 = i2 + i;
        int i4 = i;
        while (i < i3) {
            char c = cArr[i];
            if (c < length && (str = strArr[c]) != null) {
                if (i4 < i) {
                    append(cArr, i4, i - i4);
                }
                i4 = i + 1;
                append(str);
            }
            i++;
        }
        if (i4 < i) {
            append(cArr, i4, i - i4);
        }
    }

    public XmlSerializer attribute(String namespace, String name, String value) throws IOException, IllegalArgumentException, IllegalStateException {
        append(' ');
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        append("=\"");
        a(value);
        append((char) Typography.quote);
        return this;
    }

    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void comment(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void docdecl(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {
        flush();
    }

    public XmlSerializer endTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.e) {
            append(" />\n");
        } else {
            append("</");
            if (namespace != null) {
                append(namespace);
                append(':');
            }
            append(name);
            append(">\n");
        }
        this.e = false;
        return this;
    }

    public void entityRef(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    private void a() throws IOException {
        int position = this.f8a.position();
        if (position > 0) {
            this.f8a.flip();
            this.f6a.write(this.f8a.array(), 0, position);
            this.f8a.clear();
        }
    }

    public void flush() throws IOException {
        int i = this.mPos;
        if (i > 0) {
            if (this.f6a != null) {
                CharBuffer wrap = CharBuffer.wrap(this.f10a, 0, i);
                CoderResult encode = this.f9a.encode(wrap, this.f8a, true);
                while (!encode.isError()) {
                    if (encode.isOverflow()) {
                        a();
                        encode = this.f9a.encode(wrap, this.f8a, true);
                    } else {
                        a();
                        this.f6a.flush();
                    }
                }
                throw new IOException(encode.toString());
            }
            this.f7a.write(this.f10a, 0, i);
            this.f7a.flush();
            this.mPos = 0;
        }
    }

    public int getDepth() {
        throw new UnsupportedOperationException();
    }

    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String getNamespace() {
        throw new UnsupportedOperationException();
    }

    public String getPrefix(String namespace, boolean generatePrefix) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    public void ignorableWhitespace(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void processingInstruction(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    static String d() {
        return "http://" + a;
    }

    public void setFeature(String name, boolean state) throws IllegalArgumentException, IllegalStateException {
        if (!name.equals(d())) {
            throw new UnsupportedOperationException();
        }
    }

    public void setOutput(OutputStream os, String encoding) throws IOException, IllegalArgumentException, IllegalStateException {
        if (os != null) {
            try {
                this.f9a = Charset.forName(encoding).newEncoder();
                this.f6a = os;
            } catch (IllegalCharsetNameException e2) {
                throw ((UnsupportedEncodingException) new UnsupportedEncodingException(encoding).initCause(e2));
            } catch (UnsupportedCharsetException e3) {
                throw ((UnsupportedEncodingException) new UnsupportedEncodingException(encoding).initCause(e3));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        this.f7a = writer;
    }

    public void setPrefix(String prefix, String namespace) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException, IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void startDocument(String encoding, Boolean standalone) throws IOException, IllegalArgumentException, IllegalStateException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8' standalone='");
        sb.append(standalone.booleanValue() ? "yes" : "no");
        sb.append("' ?>\n");
        append(sb.toString());
    }

    public XmlSerializer startTag(String namespace, String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.e) {
            append(">\n");
        }
        append((char) Typography.less);
        if (namespace != null) {
            append(namespace);
            append(':');
        }
        append(name);
        this.e = true;
        return this;
    }

    public XmlSerializer text(char[] buf, int start, int len) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.e) {
            append(">");
            this.e = false;
        }
        a(buf, start, len);
        return this;
    }

    public XmlSerializer text(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        if (this.e) {
            append(">");
            this.e = false;
        }
        a(text);
        return this;
    }
}
