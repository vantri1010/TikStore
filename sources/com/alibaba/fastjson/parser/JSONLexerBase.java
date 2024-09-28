package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.util.IOUtils;
import com.google.android.exoplayer2.C;
import com.serenegiant.usb.UVCCamera;
import java.io.Closeable;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import kotlin.text.Typography;

public abstract class JSONLexerBase implements JSONLexer, Closeable {
    private static final Map<String, Integer> DEFAULT_KEYWORDS;
    protected static final int INT_MULTMIN_RADIX_TEN = -214748364;
    protected static final int INT_N_MULTMAX_RADIX_TEN = -214748364;
    protected static final long MULTMIN_RADIX_TEN = -922337203685477580L;
    protected static final long N_MULTMAX_RADIX_TEN = -922337203685477580L;
    private static final ThreadLocal<SoftReference<char[]>> SBUF_REF_LOCAL = new ThreadLocal<>();
    protected static final int[] digits = new int[103];
    protected static final char[] typeFieldName = ("\"" + JSON.DEFAULT_TYPE_KEY + "\":\"").toCharArray();
    protected static boolean[] whitespaceFlags;
    protected int bp;
    protected Calendar calendar = null;
    protected char ch;
    protected int eofPos;
    protected int features = JSON.DEFAULT_PARSER_FEATURE;
    protected boolean hasSpecial;
    protected Map<String, Integer> keywods = DEFAULT_KEYWORDS;
    public int matchStat = 0;
    protected int np;
    protected int pos;
    protected char[] sbuf;
    protected int sp;
    protected int token;

    public abstract String addSymbol(int i, int i2, int i3, SymbolTable symbolTable);

    /* access modifiers changed from: protected */
    public abstract void arrayCopy(int i, char[] cArr, int i2, int i3);

    public abstract byte[] bytesValue();

    public abstract char charAt(int i);

    /* access modifiers changed from: protected */
    public abstract void copyTo(int i, int i2, char[] cArr);

    public abstract int indexOf(char c, int i);

    public abstract boolean isEOF();

    public abstract char next();

    public abstract String numberString();

    public abstract String stringVal();

    public abstract String subString(int i, int i2);

    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("null", 8);
        map.put("new", 9);
        map.put("true", 6);
        map.put("false", 7);
        map.put("undefined", 23);
        DEFAULT_KEYWORDS = map;
        boolean[] zArr = new boolean[256];
        whitespaceFlags = zArr;
        zArr[32] = true;
        zArr[10] = true;
        zArr[13] = true;
        zArr[9] = true;
        zArr[12] = true;
        zArr[8] = true;
        for (int i = 48; i <= 57; i++) {
            digits[i] = i - 48;
        }
        for (int i2 = 97; i2 <= 102; i2++) {
            digits[i2] = (i2 - 97) + 10;
        }
        for (int i3 = 65; i3 <= 70; i3++) {
            digits[i3] = (i3 - 65) + 10;
        }
    }

    /* access modifiers changed from: protected */
    public void lexError(String key, Object... args) {
        this.token = 1;
    }

    public JSONLexerBase() {
        SoftReference<char[]> sbufRef = SBUF_REF_LOCAL.get();
        if (sbufRef != null) {
            this.sbuf = sbufRef.get();
            SBUF_REF_LOCAL.set((Object) null);
        }
        if (this.sbuf == null) {
            this.sbuf = new char[64];
        }
    }

    public final void nextToken() {
        this.sp = 0;
        while (true) {
            this.pos = this.bp;
            char c = this.ch;
            if (c == '\"') {
                scanString();
                return;
            } else if (c == ',') {
                next();
                this.token = 16;
                return;
            } else if (c < '0' || c > '9') {
                char c2 = this.ch;
                if (c2 == '-') {
                    scanNumber();
                    return;
                }
                if (!(c2 == 12 || c2 == 13 || c2 == ' ')) {
                    if (c2 == ':') {
                        next();
                        this.token = 17;
                        return;
                    } else if (c2 == '[') {
                        next();
                        this.token = 14;
                        return;
                    } else if (c2 == ']') {
                        next();
                        this.token = 15;
                        return;
                    } else if (c2 == 'f') {
                        scanFalse();
                        return;
                    } else if (c2 == 'n') {
                        scanNullOrNew();
                        return;
                    } else if (c2 == '{') {
                        next();
                        this.token = 12;
                        return;
                    } else if (c2 == '}') {
                        next();
                        this.token = 13;
                        return;
                    } else if (c2 == 'S') {
                        scanSet();
                        return;
                    } else if (c2 == 'T') {
                        scanTreeSet();
                        return;
                    } else if (c2 == 't') {
                        scanTrue();
                        return;
                    } else if (c2 != 'u') {
                        switch (c2) {
                            case 8:
                            case 9:
                            case 10:
                                break;
                            default:
                                switch (c2) {
                                    case '\'':
                                        if (isEnabled(Feature.AllowSingleQuotes)) {
                                            scanStringSingleQuote();
                                            return;
                                        }
                                        throw new JSONException("Feature.AllowSingleQuotes is false");
                                    case '(':
                                        next();
                                        this.token = 10;
                                        return;
                                    case ')':
                                        next();
                                        this.token = 11;
                                        return;
                                    default:
                                        if (!isEOF()) {
                                            lexError("illegal.char", String.valueOf(this.ch));
                                            next();
                                            return;
                                        } else if (this.token != 20) {
                                            this.token = 20;
                                            int i = this.eofPos;
                                            this.bp = i;
                                            this.pos = i;
                                            return;
                                        } else {
                                            throw new JSONException("EOF error");
                                        }
                                }
                        }
                    } else {
                        scanUndefined();
                        return;
                    }
                }
                next();
            } else {
                scanNumber();
                return;
            }
        }
    }

    public final void nextToken(int expect) {
        this.sp = 0;
        while (true) {
            if (expect == 2) {
                char c = this.ch;
                if (c < '0' || c > '9') {
                    char c2 = this.ch;
                    if (c2 == '\"') {
                        this.pos = this.bp;
                        scanString();
                        return;
                    } else if (c2 == '[') {
                        this.token = 14;
                        next();
                        return;
                    } else if (c2 == '{') {
                        this.token = 12;
                        next();
                        return;
                    }
                } else {
                    this.pos = this.bp;
                    scanNumber();
                    return;
                }
            } else if (expect == 4) {
                char c3 = this.ch;
                if (c3 == '\"') {
                    this.pos = this.bp;
                    scanString();
                    return;
                } else if (c3 < '0' || c3 > '9') {
                    char c4 = this.ch;
                    if (c4 == '[') {
                        this.token = 14;
                        next();
                        return;
                    } else if (c4 == '{') {
                        this.token = 12;
                        next();
                        return;
                    }
                } else {
                    this.pos = this.bp;
                    scanNumber();
                    return;
                }
            } else if (expect == 12) {
                char c5 = this.ch;
                if (c5 == '{') {
                    this.token = 12;
                    next();
                    return;
                } else if (c5 == '[') {
                    this.token = 14;
                    next();
                    return;
                }
            } else if (expect != 18) {
                if (expect != 20) {
                    switch (expect) {
                        case 14:
                            char c6 = this.ch;
                            if (c6 == '[') {
                                this.token = 14;
                                next();
                                return;
                            } else if (c6 == '{') {
                                this.token = 12;
                                next();
                                return;
                            }
                            break;
                        case 15:
                            if (this.ch == ']') {
                                this.token = 15;
                                next();
                                return;
                            }
                            break;
                        case 16:
                            char c7 = this.ch;
                            if (c7 == ',') {
                                this.token = 16;
                                next();
                                return;
                            } else if (c7 == '}') {
                                this.token = 13;
                                next();
                                return;
                            } else if (c7 == ']') {
                                this.token = 15;
                                next();
                                return;
                            } else if (c7 == 26) {
                                this.token = 20;
                                return;
                            }
                            break;
                    }
                }
                if (this.ch == 26) {
                    this.token = 20;
                    return;
                }
            } else {
                nextIdent();
                return;
            }
            char c8 = this.ch;
            if (c8 == ' ' || c8 == 10 || c8 == 13 || c8 == 9 || c8 == 12 || c8 == 8) {
                next();
            } else {
                nextToken();
                return;
            }
        }
    }

    public final void nextIdent() {
        while (isWhitespace(this.ch)) {
            next();
        }
        char c = this.ch;
        if (c == '_' || Character.isLetter(c)) {
            scanIdent();
        } else {
            nextToken();
        }
    }

    public final void nextTokenWithColon() {
        nextTokenWithChar(':');
    }

    public final void nextTokenWithChar(char expect) {
        this.sp = 0;
        while (true) {
            char c = this.ch;
            if (c == expect) {
                next();
                nextToken();
                return;
            } else if (c == ' ' || c == 10 || c == 13 || c == 9 || c == 12 || c == 8) {
                next();
            } else {
                throw new JSONException("not match " + expect + " - " + this.ch);
            }
        }
    }

    public final int token() {
        return this.token;
    }

    public final String tokenName() {
        return JSONToken.name(this.token);
    }

    public final int pos() {
        return this.pos;
    }

    public final int getBufferPosition() {
        return this.bp;
    }

    public final String stringDefaultValue() {
        if (isEnabled(Feature.InitStringFieldAsEmpty)) {
            return "";
        }
        return null;
    }

    public final Number integerValue() throws NumberFormatException {
        long limit;
        long result = 0;
        boolean negative = false;
        if (this.np == -1) {
            this.np = 0;
        }
        int i = this.np;
        int max = this.np + this.sp;
        char type = ' ';
        char charAt = charAt(max - 1);
        if (charAt == 'B') {
            max--;
            type = 'B';
        } else if (charAt == 'L') {
            max--;
            type = 'L';
        } else if (charAt == 'S') {
            max--;
            type = 'S';
        }
        if (charAt(this.np) == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else {
            limit = C.TIME_UNSET;
        }
        if (i < max) {
            result = (long) (-digits[charAt(i)]);
            i++;
        }
        while (i < max) {
            int i2 = i + 1;
            int digit = digits[charAt(i)];
            if (result < -922337203685477580L) {
                return new BigInteger(numberString());
            }
            long result2 = result * 10;
            if (result2 < ((long) digit) + limit) {
                return new BigInteger(numberString());
            }
            result = result2 - ((long) digit);
            i = i2;
        }
        if (!negative) {
            long result3 = -result;
            if (result3 > 2147483647L || type == 'L') {
                return Long.valueOf(result3);
            }
            if (type == 'S') {
                return Short.valueOf((short) ((int) result3));
            }
            if (type == 'B') {
                return Byte.valueOf((byte) ((int) result3));
            }
            return Integer.valueOf((int) result3);
        } else if (i <= this.np + 1) {
            throw new NumberFormatException(numberString());
        } else if (result < -2147483648L || type == 'L') {
            return Long.valueOf(result);
        } else {
            if (type == 'S') {
                return Short.valueOf((short) ((int) result));
            }
            if (type == 'B') {
                return Byte.valueOf((byte) ((int) result));
            }
            return Integer.valueOf((int) result);
        }
    }

    public final void nextTokenWithColon(int expect) {
        nextTokenWithChar(':');
    }

    public float floatValue() {
        return Float.parseFloat(numberString());
    }

    public double doubleValue() {
        return Double.parseDouble(numberString());
    }

    public void config(Feature feature, boolean state) {
        this.features = Feature.config(this.features, feature, state);
    }

    public final boolean isEnabled(Feature feature) {
        return Feature.isEnabled(this.features, feature);
    }

    public final char getCurrent() {
        return this.ch;
    }

    public final String scanSymbol(SymbolTable symbolTable) {
        skipWhitespace();
        char c = this.ch;
        if (c == '\"') {
            return scanSymbol(symbolTable, Typography.quote);
        }
        if (c == '\'') {
            if (isEnabled(Feature.AllowSingleQuotes)) {
                return scanSymbol(symbolTable, '\'');
            }
            throw new JSONException("syntax error");
        } else if (c == '}') {
            next();
            this.token = 13;
            return null;
        } else if (c == ',') {
            next();
            this.token = 16;
            return null;
        } else if (c == 26) {
            this.token = 20;
            return null;
        } else if (isEnabled(Feature.AllowUnQuotedFieldNames)) {
            return scanSymbolUnQuoted(symbolTable);
        } else {
            throw new JSONException("syntax error");
        }
    }

    public final String scanSymbol(SymbolTable symbolTable, char c) {
        String str;
        int i;
        this.np = this.bp;
        this.sp = 0;
        boolean z = false;
        int i2 = 0;
        while (true) {
            char next = next();
            if (next == c) {
                this.token = 4;
                if (!z) {
                    int i3 = this.np;
                    if (i3 == -1) {
                        i = 0;
                    } else {
                        i = i3 + 1;
                    }
                    str = addSymbol(i, this.sp, i2, symbolTable);
                } else {
                    str = symbolTable.addSymbol(this.sbuf, 0, this.sp, i2);
                }
                this.sp = 0;
                next();
                return str;
            } else if (next == 26) {
                throw new JSONException("unclosed.str");
            } else if (next == '\\') {
                if (!z) {
                    int i4 = this.sp;
                    char[] cArr = this.sbuf;
                    if (i4 >= cArr.length) {
                        int length = cArr.length * 2;
                        if (i4 <= length) {
                            i4 = length;
                        }
                        char[] cArr2 = new char[i4];
                        char[] cArr3 = this.sbuf;
                        System.arraycopy(cArr3, 0, cArr2, 0, cArr3.length);
                        this.sbuf = cArr2;
                    }
                    arrayCopy(this.np + 1, this.sbuf, 0, this.sp);
                    z = true;
                }
                char next2 = next();
                if (next2 == '\"') {
                    i2 = (i2 * 31) + 34;
                    putChar(Typography.quote);
                } else if (next2 != '\'') {
                    if (next2 != 'F') {
                        if (next2 == '\\') {
                            i2 = (i2 * 31) + 92;
                            putChar('\\');
                        } else if (next2 == 'b') {
                            i2 = (i2 * 31) + 8;
                            putChar(8);
                        } else if (next2 != 'f') {
                            if (next2 == 'n') {
                                i2 = (i2 * 31) + 10;
                                putChar(10);
                            } else if (next2 == 'r') {
                                i2 = (i2 * 31) + 13;
                                putChar(13);
                            } else if (next2 != 'x') {
                                switch (next2) {
                                    case '/':
                                        i2 = (i2 * 31) + 47;
                                        putChar('/');
                                        break;
                                    case '0':
                                        i2 = (i2 * 31) + next2;
                                        putChar(0);
                                        break;
                                    case '1':
                                        i2 = (i2 * 31) + next2;
                                        putChar(1);
                                        break;
                                    case '2':
                                        i2 = (i2 * 31) + next2;
                                        putChar(2);
                                        break;
                                    case '3':
                                        i2 = (i2 * 31) + next2;
                                        putChar(3);
                                        break;
                                    case '4':
                                        i2 = (i2 * 31) + next2;
                                        putChar(4);
                                        break;
                                    case '5':
                                        i2 = (i2 * 31) + next2;
                                        putChar(5);
                                        break;
                                    case '6':
                                        i2 = (i2 * 31) + next2;
                                        putChar(6);
                                        break;
                                    case '7':
                                        i2 = (i2 * 31) + next2;
                                        putChar(7);
                                        break;
                                    default:
                                        switch (next2) {
                                            case 't':
                                                i2 = (i2 * 31) + 9;
                                                putChar(9);
                                                break;
                                            case 'u':
                                                int parseInt = Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16);
                                                i2 = (i2 * 31) + parseInt;
                                                putChar((char) parseInt);
                                                break;
                                            case 'v':
                                                i2 = (i2 * 31) + 11;
                                                putChar(11);
                                                break;
                                            default:
                                                this.ch = next2;
                                                throw new JSONException("unclosed.str.lit");
                                        }
                                }
                            } else {
                                char next3 = next();
                                this.ch = next3;
                                char next4 = next();
                                this.ch = next4;
                                int[] iArr = digits;
                                char c2 = (char) ((iArr[next3] * 16) + iArr[next4]);
                                i2 = (i2 * 31) + c2;
                                putChar(c2);
                            }
                        }
                    }
                    i2 = (i2 * 31) + 12;
                    putChar(12);
                } else {
                    i2 = (i2 * 31) + 39;
                    putChar('\'');
                }
            } else {
                i2 = (i2 * 31) + next;
                if (!z) {
                    this.sp++;
                } else {
                    int i5 = this.sp;
                    char[] cArr4 = this.sbuf;
                    if (i5 == cArr4.length) {
                        putChar(next);
                    } else {
                        this.sp = i5 + 1;
                        cArr4[i5] = next;
                    }
                }
            }
        }
    }

    public final void resetStringPosition() {
        this.sp = 0;
    }

    public final String scanSymbolUnQuoted(SymbolTable symbolTable) {
        boolean[] firstIdentifierFlags = IOUtils.firstIdentifierFlags;
        char first = this.ch;
        if (this.ch >= firstIdentifierFlags.length || firstIdentifierFlags[first]) {
            boolean[] identifierFlags = IOUtils.identifierFlags;
            int hash = first;
            this.np = this.bp;
            this.sp = 1;
            while (true) {
                char chLocal = next();
                if (chLocal < identifierFlags.length && !identifierFlags[chLocal]) {
                    break;
                }
                hash = (hash * 31) + chLocal;
                this.sp++;
            }
            this.ch = charAt(this.bp);
            this.token = 18;
            if (this.sp == 4 && hash == 3392903 && charAt(this.np) == 'n' && charAt(this.np + 1) == 'u' && charAt(this.np + 2) == 'l' && charAt(this.np + 3) == 'l') {
                return null;
            }
            return addSymbol(this.np, this.sp, hash, symbolTable);
        }
        throw new JSONException("illegal identifier : " + this.ch);
    }

    public final void scanString() {
        this.np = this.bp;
        this.hasSpecial = false;
        while (true) {
            char next = next();
            if (next == '\"') {
                this.token = 4;
                this.ch = next();
                return;
            } else if (next == 26) {
                throw new JSONException("unclosed string : " + next);
            } else if (next == '\\') {
                if (!this.hasSpecial) {
                    this.hasSpecial = true;
                    int i = this.sp;
                    char[] cArr = this.sbuf;
                    if (i >= cArr.length) {
                        int length = cArr.length * 2;
                        if (i <= length) {
                            i = length;
                        }
                        char[] cArr2 = new char[i];
                        char[] cArr3 = this.sbuf;
                        System.arraycopy(cArr3, 0, cArr2, 0, cArr3.length);
                        this.sbuf = cArr2;
                    }
                    copyTo(this.np + 1, this.sp, this.sbuf);
                }
                char next2 = next();
                if (next2 == '\"') {
                    putChar(Typography.quote);
                } else if (next2 != '\'') {
                    if (next2 != 'F') {
                        if (next2 == '\\') {
                            putChar('\\');
                        } else if (next2 == 'b') {
                            putChar(8);
                        } else if (next2 != 'f') {
                            if (next2 == 'n') {
                                putChar(10);
                            } else if (next2 == 'r') {
                                putChar(13);
                            } else if (next2 != 'x') {
                                switch (next2) {
                                    case '/':
                                        putChar('/');
                                        break;
                                    case '0':
                                        putChar(0);
                                        break;
                                    case '1':
                                        putChar(1);
                                        break;
                                    case '2':
                                        putChar(2);
                                        break;
                                    case '3':
                                        putChar(3);
                                        break;
                                    case '4':
                                        putChar(4);
                                        break;
                                    case '5':
                                        putChar(5);
                                        break;
                                    case '6':
                                        putChar(6);
                                        break;
                                    case '7':
                                        putChar(7);
                                        break;
                                    default:
                                        switch (next2) {
                                            case 't':
                                                putChar(9);
                                                break;
                                            case 'u':
                                                putChar((char) Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16));
                                                break;
                                            case 'v':
                                                putChar(11);
                                                break;
                                            default:
                                                this.ch = next2;
                                                throw new JSONException("unclosed string : " + next2);
                                        }
                                }
                            } else {
                                char next3 = next();
                                char next4 = next();
                                int[] iArr = digits;
                                putChar((char) ((iArr[next3] * 16) + iArr[next4]));
                            }
                        }
                    }
                    putChar(12);
                } else {
                    putChar('\'');
                }
            } else if (!this.hasSpecial) {
                this.sp++;
            } else {
                int i2 = this.sp;
                char[] cArr4 = this.sbuf;
                if (i2 == cArr4.length) {
                    putChar(next);
                } else {
                    this.sp = i2 + 1;
                    cArr4[i2] = next;
                }
            }
        }
    }

    public Calendar getCalendar() {
        return this.calendar;
    }

    public final int intValue() {
        int limit;
        int i;
        if (this.np == -1) {
            this.np = 0;
        }
        int result = 0;
        boolean negative = false;
        int i2 = this.np;
        int i3 = this.np;
        int max = this.sp + i3;
        if (charAt(i3) == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            i2++;
        } else {
            limit = UVCCamera.PU_BRIGHTNESS;
        }
        if (i2 < max) {
            result = -digits[charAt(i2)];
            i2++;
        }
        while (true) {
            if (i2 >= max) {
                break;
            }
            i = i2 + 1;
            int i4 = charAt(i2);
            if (i4 == 76 || i4 == 83 || i4 == 66) {
                i2 = i;
            } else {
                int digit = digits[i4];
                if (result >= -214748364) {
                    int result2 = result * 10;
                    if (result2 >= limit + digit) {
                        result = result2 - digit;
                        i2 = i;
                    } else {
                        throw new NumberFormatException(numberString());
                    }
                } else {
                    throw new NumberFormatException(numberString());
                }
            }
        }
        i2 = i;
        if (!negative) {
            return -result;
        }
        if (i2 > this.np + 1) {
            return result;
        }
        throw new NumberFormatException(numberString());
    }

    public void close() {
        if (this.sbuf.length <= 8192) {
            SBUF_REF_LOCAL.set(new SoftReference(this.sbuf));
        }
        this.sbuf = null;
    }

    public final boolean isRef() {
        if (this.sp == 4 && charAt(this.np + 1) == '$' && charAt(this.np + 2) == 'r' && charAt(this.np + 3) == 'e' && charAt(this.np + 4) == 'f') {
            return true;
        }
        return false;
    }

    public String scanString(char expectNextChar) {
        this.matchStat = 0;
        int offset = 0 + 1;
        char chLocal = charAt(this.bp + 0);
        if (chLocal == 'n') {
            if (charAt(this.bp + offset) == 'u' && charAt(this.bp + offset + 1) == 'l' && charAt(this.bp + offset + 2) == 'l') {
                int offset2 = offset + 3;
                int offset3 = offset2 + 1;
                if (charAt(this.bp + offset2) == expectNextChar) {
                    this.bp += offset3 - 1;
                    next();
                    this.matchStat = 3;
                    return null;
                }
                this.matchStat = -1;
                return null;
            }
            this.matchStat = -1;
            return null;
        } else if (chLocal != '\"') {
            this.matchStat = -1;
            return stringDefaultValue();
        } else {
            boolean hasSpecial2 = false;
            int startIndex = this.bp + 1;
            int endIndex = indexOf(Typography.quote, startIndex);
            if (endIndex != -1) {
                String stringVal = subString(this.bp + 1, endIndex - startIndex);
                int i = this.bp + 1;
                while (true) {
                    if (i >= endIndex) {
                        break;
                    } else if (charAt(i) == '\\') {
                        hasSpecial2 = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (hasSpecial2) {
                    this.matchStat = -1;
                    return stringDefaultValue();
                }
                int i2 = this.bp;
                int offset4 = offset + (endIndex - (i2 + 1)) + 1;
                int offset5 = offset4 + 1;
                char chLocal2 = charAt(i2 + offset4);
                String strVal = stringVal;
                if (chLocal2 == expectNextChar) {
                    this.bp += offset5 - 1;
                    next();
                    this.matchStat = 3;
                    return strVal;
                }
                this.matchStat = -1;
                return strVal;
            }
            throw new JSONException("unclosed str");
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Enum<?> scanEnum(java.lang.Class<?> r3, com.alibaba.fastjson.parser.SymbolTable r4, char r5) {
        /*
            r2 = this;
            java.lang.String r0 = r2.scanSymbolWithSeperator(r4, r5)
            if (r0 != 0) goto L_0x0008
            r1 = 0
            return r1
        L_0x0008:
            java.lang.Enum r1 = java.lang.Enum.valueOf(r3, r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONLexerBase.scanEnum(java.lang.Class, com.alibaba.fastjson.parser.SymbolTable, char):java.lang.Enum");
    }

    public String scanSymbolWithSeperator(SymbolTable symbolTable, char serperator) {
        this.matchStat = 0;
        int offset = 0 + 1;
        char chLocal = charAt(this.bp + 0);
        if (chLocal == 'n') {
            if (charAt(this.bp + offset) == 'u' && charAt(this.bp + offset + 1) == 'l' && charAt(this.bp + offset + 2) == 'l') {
                int offset2 = offset + 3;
                int offset3 = offset2 + 1;
                if (charAt(this.bp + offset2) == serperator) {
                    this.bp += offset3 - 1;
                    next();
                    this.matchStat = 3;
                    return null;
                }
                this.matchStat = -1;
                return null;
            }
            this.matchStat = -1;
            return null;
        } else if (chLocal != '\"') {
            this.matchStat = -1;
            return null;
        } else {
            int hash = 0;
            while (true) {
                int offset4 = offset + 1;
                char chLocal2 = charAt(this.bp + offset);
                if (chLocal2 == '\"') {
                    int i = this.bp;
                    int start = i + 0 + 1;
                    String strVal = addSymbol(start, ((i + offset4) - start) - 1, hash, symbolTable);
                    int offset5 = offset4 + 1;
                    if (charAt(this.bp + offset4) == serperator) {
                        this.bp += offset5 - 1;
                        next();
                        this.matchStat = 3;
                        return strVal;
                    }
                    this.matchStat = -1;
                    return strVal;
                }
                hash = (hash * 31) + chLocal2;
                if (chLocal2 == '\\') {
                    this.matchStat = -1;
                    return null;
                }
                offset = offset4;
            }
        }
    }

    public int scanInt(char expectNext) {
        int offset;
        char chLocal;
        this.matchStat = 0;
        int offset2 = 0 + 1;
        char chLocal2 = charAt(this.bp + 0);
        if (chLocal2 < '0' || chLocal2 > '9') {
            this.matchStat = -1;
            return 0;
        }
        int value = digits[chLocal2];
        while (true) {
            offset = offset2 + 1;
            chLocal = charAt(this.bp + offset2);
            if (chLocal >= '0' && chLocal <= '9') {
                value = (value * 10) + digits[chLocal];
                offset2 = offset;
            }
        }
        if (chLocal == '.') {
            this.matchStat = -1;
            return 0;
        } else if (value < 0) {
            this.matchStat = -1;
            return 0;
        } else if (chLocal == expectNext) {
            this.bp += offset - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else {
            this.matchStat = -1;
            return value;
        }
    }

    public long scanLong(char expectNextChar) {
        int offset;
        char chLocal;
        this.matchStat = 0;
        int offset2 = 0 + 1;
        char chLocal2 = charAt(this.bp + 0);
        if (chLocal2 < '0' || chLocal2 > '9') {
            char c = expectNextChar;
            this.matchStat = -1;
            return 0;
        }
        long value = (long) digits[chLocal2];
        while (true) {
            offset = offset2 + 1;
            chLocal = charAt(this.bp + offset2);
            if (chLocal >= '0' && chLocal <= '9') {
                value = (10 * value) + ((long) digits[chLocal]);
                offset2 = offset;
            }
        }
        if (chLocal == '.') {
            this.matchStat = -1;
            return 0;
        } else if (value < 0) {
            this.matchStat = -1;
            return 0;
        } else if (chLocal == expectNextChar) {
            this.bp += offset - 1;
            next();
            this.matchStat = 3;
            this.token = 16;
            return value;
        } else {
            this.matchStat = -1;
            return value;
        }
    }

    public final void scanTrue() {
        if (this.ch == 't') {
            next();
            if (this.ch == 'r') {
                next();
                if (this.ch == 'u') {
                    next();
                    if (this.ch == 'e') {
                        next();
                        char c = this.ch;
                        if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8 || c == ':') {
                            this.token = 6;
                            return;
                        }
                        throw new JSONException("scan true error");
                    }
                    throw new JSONException("error parse true");
                }
                throw new JSONException("error parse true");
            }
            throw new JSONException("error parse true");
        }
        throw new JSONException("error parse true");
    }

    public final void scanTreeSet() {
        if (this.ch == 'T') {
            next();
            if (this.ch == 'r') {
                next();
                if (this.ch == 'e') {
                    next();
                    if (this.ch == 'e') {
                        next();
                        if (this.ch == 'S') {
                            next();
                            if (this.ch == 'e') {
                                next();
                                if (this.ch == 't') {
                                    next();
                                    char c = this.ch;
                                    if (c == ' ' || c == 10 || c == 13 || c == 9 || c == 12 || c == 8 || c == '[' || c == '(') {
                                        this.token = 22;
                                        return;
                                    }
                                    throw new JSONException("scan set error");
                                }
                                throw new JSONException("error parse true");
                            }
                            throw new JSONException("error parse true");
                        }
                        throw new JSONException("error parse true");
                    }
                    throw new JSONException("error parse true");
                }
                throw new JSONException("error parse true");
            }
            throw new JSONException("error parse true");
        }
        throw new JSONException("error parse true");
    }

    public final void scanNullOrNew() {
        if (this.ch == 'n') {
            next();
            char c = this.ch;
            if (c == 'u') {
                next();
                if (this.ch == 'l') {
                    next();
                    if (this.ch == 'l') {
                        next();
                        char c2 = this.ch;
                        if (c2 == ' ' || c2 == ',' || c2 == '}' || c2 == ']' || c2 == 10 || c2 == 13 || c2 == 9 || c2 == 26 || c2 == 12 || c2 == 8) {
                            this.token = 8;
                            return;
                        }
                        throw new JSONException("scan true error");
                    }
                    throw new JSONException("error parse true");
                }
                throw new JSONException("error parse true");
            } else if (c == 'e') {
                next();
                if (this.ch == 'w') {
                    next();
                    char c3 = this.ch;
                    if (c3 == ' ' || c3 == ',' || c3 == '}' || c3 == ']' || c3 == 10 || c3 == 13 || c3 == 9 || c3 == 26 || c3 == 12 || c3 == 8) {
                        this.token = 9;
                        return;
                    }
                    throw new JSONException("scan true error");
                }
                throw new JSONException("error parse w");
            } else {
                throw new JSONException("error parse e");
            }
        } else {
            throw new JSONException("error parse null or new");
        }
    }

    public final void scanUndefined() {
        if (this.ch == 'u') {
            next();
            if (this.ch == 'n') {
                next();
                if (this.ch == 'd') {
                    next();
                    if (this.ch == 'e') {
                        next();
                        if (this.ch == 'f') {
                            next();
                            if (this.ch == 'i') {
                                next();
                                if (this.ch == 'n') {
                                    next();
                                    if (this.ch == 'e') {
                                        next();
                                        if (this.ch == 'd') {
                                            next();
                                            char c = this.ch;
                                            if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8) {
                                                this.token = 23;
                                                return;
                                            }
                                            throw new JSONException("scan false error");
                                        }
                                        throw new JSONException("error parse false");
                                    }
                                    throw new JSONException("error parse false");
                                }
                                throw new JSONException("error parse false");
                            }
                            throw new JSONException("error parse false");
                        }
                        throw new JSONException("error parse false");
                    }
                    throw new JSONException("error parse false");
                }
                throw new JSONException("error parse false");
            }
            throw new JSONException("error parse false");
        }
        throw new JSONException("error parse false");
    }

    public final void scanFalse() {
        if (this.ch == 'f') {
            next();
            if (this.ch == 'a') {
                next();
                if (this.ch == 'l') {
                    next();
                    if (this.ch == 's') {
                        next();
                        if (this.ch == 'e') {
                            next();
                            char c = this.ch;
                            if (c == ' ' || c == ',' || c == '}' || c == ']' || c == 10 || c == 13 || c == 9 || c == 26 || c == 12 || c == 8 || c == ':') {
                                this.token = 7;
                                return;
                            }
                            throw new JSONException("scan false error");
                        }
                        throw new JSONException("error parse false");
                    }
                    throw new JSONException("error parse false");
                }
                throw new JSONException("error parse false");
            }
            throw new JSONException("error parse false");
        }
        throw new JSONException("error parse false");
    }

    public final void scanIdent() {
        this.np = this.bp - 1;
        this.hasSpecial = false;
        do {
            this.sp++;
            next();
        } while (Character.isLetterOrDigit(this.ch));
        Integer tok = getKeyword(stringVal());
        if (tok != null) {
            this.token = tok.intValue();
        } else {
            this.token = 18;
        }
    }

    public final boolean isBlankInput() {
        int i = 0;
        while (true) {
            char chLocal = charAt(i);
            if (chLocal == 26) {
                return true;
            }
            if (!isWhitespace(chLocal)) {
                return false;
            }
            i++;
        }
    }

    public final void skipWhitespace() {
        while (true) {
            char c = this.ch;
            boolean[] zArr = whitespaceFlags;
            if (c < zArr.length && zArr[c]) {
                next();
            } else {
                return;
            }
        }
    }

    private final void scanStringSingleQuote() {
        this.np = this.bp;
        this.hasSpecial = false;
        while (true) {
            char next = next();
            if (next == '\'') {
                this.token = 4;
                next();
                return;
            } else if (next == 26) {
                throw new JSONException("unclosed single-quote string");
            } else if (next == '\\') {
                if (!this.hasSpecial) {
                    this.hasSpecial = true;
                    int i = this.sp;
                    char[] cArr = this.sbuf;
                    if (i > cArr.length) {
                        char[] cArr2 = new char[(i * 2)];
                        System.arraycopy(cArr, 0, cArr2, 0, cArr.length);
                        this.sbuf = cArr2;
                    }
                    copyTo(this.np + 1, this.sp, this.sbuf);
                }
                char next2 = next();
                if (next2 == '\"') {
                    putChar(Typography.quote);
                } else if (next2 != '\'') {
                    if (next2 != 'F') {
                        if (next2 == '\\') {
                            putChar('\\');
                        } else if (next2 == 'b') {
                            putChar(8);
                        } else if (next2 != 'f') {
                            if (next2 == 'n') {
                                putChar(10);
                            } else if (next2 == 'r') {
                                putChar(13);
                            } else if (next2 != 'x') {
                                switch (next2) {
                                    case '/':
                                        putChar('/');
                                        break;
                                    case '0':
                                        putChar(0);
                                        break;
                                    case '1':
                                        putChar(1);
                                        break;
                                    case '2':
                                        putChar(2);
                                        break;
                                    case '3':
                                        putChar(3);
                                        break;
                                    case '4':
                                        putChar(4);
                                        break;
                                    case '5':
                                        putChar(5);
                                        break;
                                    case '6':
                                        putChar(6);
                                        break;
                                    case '7':
                                        putChar(7);
                                        break;
                                    default:
                                        switch (next2) {
                                            case 't':
                                                putChar(9);
                                                break;
                                            case 'u':
                                                putChar((char) Integer.parseInt(new String(new char[]{next(), next(), next(), next()}), 16));
                                                break;
                                            case 'v':
                                                putChar(11);
                                                break;
                                            default:
                                                this.ch = next2;
                                                throw new JSONException("unclosed single-quote string");
                                        }
                                }
                            } else {
                                char next3 = next();
                                char next4 = next();
                                int[] iArr = digits;
                                putChar((char) ((iArr[next3] * 16) + iArr[next4]));
                            }
                        }
                    }
                    putChar(12);
                } else {
                    putChar('\'');
                }
            } else if (!this.hasSpecial) {
                this.sp++;
            } else {
                int i2 = this.sp;
                char[] cArr3 = this.sbuf;
                if (i2 == cArr3.length) {
                    putChar(next);
                } else {
                    this.sp = i2 + 1;
                    cArr3[i2] = next;
                }
            }
        }
    }

    public final void scanSet() {
        if (this.ch == 'S') {
            next();
            if (this.ch == 'e') {
                next();
                if (this.ch == 't') {
                    next();
                    char c = this.ch;
                    if (c == ' ' || c == 10 || c == 13 || c == 9 || c == 12 || c == 8 || c == '[' || c == '(') {
                        this.token = 21;
                        return;
                    }
                    throw new JSONException("scan set error");
                }
                throw new JSONException("error parse true");
            }
            throw new JSONException("error parse true");
        }
        throw new JSONException("error parse true");
    }

    /* access modifiers changed from: protected */
    public final void putChar(char ch2) {
        int i = this.sp;
        char[] cArr = this.sbuf;
        if (i == cArr.length) {
            char[] newsbuf = new char[(cArr.length * 2)];
            System.arraycopy(cArr, 0, newsbuf, 0, cArr.length);
            this.sbuf = newsbuf;
        }
        char[] newsbuf2 = this.sbuf;
        int i2 = this.sp;
        this.sp = i2 + 1;
        newsbuf2[i2] = ch2;
    }

    public final void scanNumber() {
        this.np = this.bp;
        if (this.ch == '-') {
            this.sp++;
            next();
        }
        while (true) {
            char c = this.ch;
            if (c < '0' || c > '9') {
                boolean isDouble = false;
            } else {
                this.sp++;
                next();
            }
        }
        boolean isDouble2 = false;
        if (this.ch == '.') {
            this.sp++;
            next();
            isDouble2 = true;
            while (true) {
                char c2 = this.ch;
                if (c2 < '0' || c2 > '9') {
                    break;
                }
                this.sp++;
                next();
            }
        }
        char c3 = this.ch;
        if (c3 == 'L') {
            this.sp++;
            next();
        } else if (c3 == 'S') {
            this.sp++;
            next();
        } else if (c3 == 'B') {
            this.sp++;
            next();
        } else if (c3 == 'F') {
            this.sp++;
            next();
            isDouble2 = true;
        } else if (c3 == 'D') {
            this.sp++;
            next();
            isDouble2 = true;
        } else if (c3 == 'e' || c3 == 'E') {
            this.sp++;
            next();
            char c4 = this.ch;
            if (c4 == '+' || c4 == '-') {
                this.sp++;
                next();
            }
            while (true) {
                char c5 = this.ch;
                if (c5 < '0' || c5 > '9') {
                    char c6 = this.ch;
                } else {
                    this.sp++;
                    next();
                }
            }
            char c62 = this.ch;
            if (c62 == 'D' || c62 == 'F') {
                this.sp++;
                next();
            }
            isDouble2 = true;
        }
        if (isDouble2) {
            this.token = 3;
        } else {
            this.token = 2;
        }
    }

    public final long longValue() throws NumberFormatException {
        long limit;
        int i;
        long result = 0;
        boolean negative = false;
        int i2 = this.np;
        int i3 = this.np;
        int max = this.sp + i3;
        if (charAt(i3) == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i2++;
        } else {
            limit = C.TIME_UNSET;
        }
        if (i2 < max) {
            result = (long) (-digits[charAt(i2)]);
            i2++;
        }
        while (true) {
            if (i2 >= max) {
                break;
            }
            i = i2 + 1;
            int i4 = charAt(i2);
            if (i4 == 76 || i4 == 83 || i4 == 66) {
                i2 = i;
            } else {
                int digit = digits[i4];
                if (result >= -922337203685477580L) {
                    long result2 = result * 10;
                    if (result2 >= ((long) digit) + limit) {
                        result = result2 - ((long) digit);
                        i2 = i;
                    } else {
                        throw new NumberFormatException(numberString());
                    }
                } else {
                    throw new NumberFormatException(numberString());
                }
            }
        }
        i2 = i;
        if (!negative) {
            return -result;
        }
        if (i2 > this.np + 1) {
            return result;
        }
        throw new NumberFormatException(numberString());
    }

    public final Number decimalValue(boolean decimal) {
        char chLocal = charAt((this.np + this.sp) - 1);
        if (chLocal == 'F') {
            return Float.valueOf(Float.parseFloat(numberString()));
        }
        if (chLocal == 'D') {
            return Double.valueOf(Double.parseDouble(numberString()));
        }
        if (decimal) {
            return decimalValue();
        }
        return Double.valueOf(doubleValue());
    }

    public final BigDecimal decimalValue() {
        return new BigDecimal(numberString());
    }

    public static final boolean isWhitespace(char ch2) {
        return ch2 == ' ' || ch2 == 10 || ch2 == 13 || ch2 == 9 || ch2 == 12 || ch2 == 8;
    }

    public Integer getKeyword(String key) {
        return this.keywods.get(key);
    }
}
