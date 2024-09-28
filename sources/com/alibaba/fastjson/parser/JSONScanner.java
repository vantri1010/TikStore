package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public final class JSONScanner extends JSONLexerBase {
    protected static final char[] typeFieldName = ("\"" + JSON.DEFAULT_TYPE_KEY + "\":\"").toCharArray();
    public final int ISO8601_LEN_0;
    public final int ISO8601_LEN_1;
    public final int ISO8601_LEN_2;
    private final String text;

    public JSONScanner(String input) {
        this(input, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONScanner(String input, int features) {
        this.ISO8601_LEN_0 = "0000-00-00".length();
        this.ISO8601_LEN_1 = "0000-00-00T00:00:00".length();
        this.ISO8601_LEN_2 = "0000-00-00T00:00:00.000".length();
        this.features = features;
        this.text = input;
        this.bp = -1;
        next();
        if (this.ch == 65279) {
            next();
        }
    }

    public final char charAt(int index) {
        if (index >= this.text.length()) {
            return 26;
        }
        return this.text.charAt(index);
    }

    public final char next() {
        int i = this.bp + 1;
        this.bp = i;
        char charAt = charAt(i);
        this.ch = charAt;
        return charAt;
    }

    public JSONScanner(char[] input, int inputLength) {
        this(input, inputLength, JSON.DEFAULT_PARSER_FEATURE);
    }

    public JSONScanner(char[] input, int inputLength, int features) {
        this(new String(input, 0, inputLength), features);
    }

    /* access modifiers changed from: protected */
    public final void copyTo(int offset, int count, char[] dest) {
        this.text.getChars(offset, offset + count, dest, 0);
    }

    public final int indexOf(char ch, int startIndex) {
        return this.text.indexOf(ch, startIndex);
    }

    public final String addSymbol(int offset, int len, int hash, SymbolTable symbolTable) {
        return symbolTable.addSymbol(this.text, offset, len, hash);
    }

    public byte[] bytesValue() {
        return Base64.decodeFast(this.text, this.np + 1, this.sp);
    }

    public final String stringVal() {
        if (!this.hasSpecial) {
            return subString(this.np + 1, this.sp);
        }
        return new String(this.sbuf, 0, this.sp);
    }

    public final String subString(int offset, int count) {
        char[] chars = new char[count];
        for (int i = offset; i < offset + count; i++) {
            chars[i - offset] = this.text.charAt(i);
        }
        return new String(chars);
    }

    public final String numberString() {
        char chLocal = charAt((this.np + this.sp) - 1);
        int sp = this.sp;
        if (chLocal == 'L' || chLocal == 'S' || chLocal == 'B' || chLocal == 'F' || chLocal == 'D') {
            sp--;
        }
        return subString(this.np, sp);
    }

    public boolean scanISO8601DateIfMatch() {
        return scanISO8601DateIfMatch(true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:141:0x038d A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x038f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean scanISO8601DateIfMatch(boolean r36) {
        /*
            r35 = this;
            r9 = r35
            java.lang.String r0 = r9.text
            int r0 = r0.length()
            int r1 = r9.bp
            int r10 = r0 - r1
            r11 = 2
            r12 = 13
            r13 = 57
            r14 = 5
            r15 = 1
            r8 = 48
            if (r36 != 0) goto L_0x00bc
            if (r10 <= r12) goto L_0x00bc
            int r0 = r9.bp
            char r0 = r9.charAt(r0)
            int r1 = r9.bp
            int r1 = r1 + r15
            char r1 = r9.charAt(r1)
            int r2 = r9.bp
            int r2 = r2 + r11
            char r2 = r9.charAt(r2)
            int r3 = r9.bp
            int r3 = r3 + 3
            char r3 = r9.charAt(r3)
            int r4 = r9.bp
            int r4 = r4 + 4
            char r4 = r9.charAt(r4)
            int r5 = r9.bp
            int r5 = r5 + r14
            char r5 = r9.charAt(r5)
            int r6 = r9.bp
            int r6 = r6 + r10
            int r6 = r6 - r15
            char r6 = r9.charAt(r6)
            int r12 = r9.bp
            int r12 = r12 + r10
            int r12 = r12 - r11
            char r12 = r9.charAt(r12)
            r11 = 47
            if (r0 != r11) goto L_0x00ba
            r15 = 68
            if (r1 != r15) goto L_0x00ba
            r15 = 97
            if (r2 != r15) goto L_0x00ba
            r15 = 116(0x74, float:1.63E-43)
            if (r3 != r15) goto L_0x00ba
            r15 = 101(0x65, float:1.42E-43)
            if (r4 != r15) goto L_0x00ba
            r15 = 40
            if (r5 != r15) goto L_0x00ba
            if (r6 != r11) goto L_0x00ba
            r11 = 41
            if (r12 != r11) goto L_0x00ba
            r11 = -1
            r15 = 6
        L_0x0074:
            if (r15 >= r10) goto L_0x008d
            int r14 = r9.bp
            int r14 = r14 + r15
            char r14 = r9.charAt(r14)
            r7 = 43
            if (r14 != r7) goto L_0x0084
            r7 = r15
            r11 = r7
            goto L_0x0089
        L_0x0084:
            if (r14 < r8) goto L_0x008d
            if (r14 <= r13) goto L_0x0089
            goto L_0x008d
        L_0x0089:
            int r15 = r15 + 1
            r14 = 5
            goto L_0x0074
        L_0x008d:
            r7 = -1
            if (r11 != r7) goto L_0x0092
            r7 = 0
            return r7
        L_0x0092:
            int r7 = r9.bp
            int r7 = r7 + 6
            int r8 = r11 - r7
            java.lang.String r8 = r9.subString(r7, r8)
            long r13 = java.lang.Long.parseLong(r8)
            java.util.Locale r15 = java.util.Locale.getDefault()
            r20 = r0
            java.util.TimeZone r0 = java.util.TimeZone.getDefault()
            java.util.Calendar r0 = java.util.Calendar.getInstance(r0, r15)
            r9.calendar = r0
            java.util.Calendar r0 = r9.calendar
            r0.setTimeInMillis(r13)
            r0 = 5
            r9.token = r0
            r0 = 1
            return r0
        L_0x00ba:
            r20 = r0
        L_0x00bc:
            r11 = 17
            r12 = 8
            r14 = 12
            r15 = 11
            r7 = 14
            if (r10 == r12) goto L_0x03cf
            if (r10 == r7) goto L_0x03cf
            if (r10 != r11) goto L_0x00d2
            r11 = 8
            r13 = 14
            goto L_0x03d3
        L_0x00d2:
            int r0 = r9.ISO8601_LEN_0
            if (r10 >= r0) goto L_0x00d8
            r0 = 0
            return r0
        L_0x00d8:
            r0 = 0
            int r1 = r9.bp
            int r1 = r1 + 4
            char r1 = r9.charAt(r1)
            r6 = 45
            if (r1 == r6) goto L_0x00e6
            return r0
        L_0x00e6:
            int r1 = r9.bp
            int r1 = r1 + 7
            char r1 = r9.charAt(r1)
            if (r1 == r6) goto L_0x00f1
            return r0
        L_0x00f1:
            int r0 = r9.bp
            char r28 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 1
            int r0 = r0 + r1
            char r29 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 2
            int r0 = r0 + r1
            char r30 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 3
            char r31 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 5
            int r0 = r0 + r1
            char r32 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 6
            char r33 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r12
            char r12 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 9
            char r34 = r9.charAt(r0)
            r20 = r28
            r21 = r29
            r22 = r30
            r23 = r31
            r24 = r32
            r25 = r33
            r26 = r12
            r27 = r34
            boolean r0 = checkDate(r20, r21, r22, r23, r24, r25, r26, r27)
            if (r0 != 0) goto L_0x0147
            r19 = 0
            return r19
        L_0x0147:
            r19 = 0
            r0 = r35
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = r31
            r5 = r32
            r6 = r33
            r11 = 0
            r13 = 14
            r7 = r12
            r21 = r12
            r12 = 48
            r8 = r34
            r0.setCalendar(r1, r2, r3, r4, r5, r6, r7, r8)
            int r0 = r9.bp
            int r0 = r0 + 10
            char r7 = r9.charAt(r0)
            r0 = 84
            if (r7 == r0) goto L_0x01a8
            r0 = 32
            if (r7 != r0) goto L_0x0177
            if (r36 != 0) goto L_0x0177
            goto L_0x01a8
        L_0x0177:
            r0 = 34
            if (r7 == r0) goto L_0x0181
            r0 = 26
            if (r7 != r0) goto L_0x0180
            goto L_0x0181
        L_0x0180:
            return r11
        L_0x0181:
            java.util.Calendar r0 = r9.calendar
            r0.set(r15, r11)
            java.util.Calendar r0 = r9.calendar
            r0.set(r14, r11)
            java.util.Calendar r0 = r9.calendar
            r1 = 13
            r0.set(r1, r11)
            java.util.Calendar r0 = r9.calendar
            r0.set(r13, r11)
            int r0 = r9.bp
            int r0 = r0 + 10
            r9.bp = r0
            char r0 = r9.charAt(r0)
            r9.ch = r0
            r0 = 5
            r9.token = r0
            r0 = 1
            return r0
        L_0x01a8:
            int r0 = r9.ISO8601_LEN_1
            if (r10 >= r0) goto L_0x01ad
            return r11
        L_0x01ad:
            int r0 = r9.bp
            r1 = 13
            int r0 = r0 + r1
            char r0 = r9.charAt(r0)
            r8 = 58
            if (r0 == r8) goto L_0x01bb
            return r11
        L_0x01bb:
            int r0 = r9.bp
            int r0 = r0 + 16
            char r0 = r9.charAt(r0)
            if (r0 == r8) goto L_0x01c6
            return r11
        L_0x01c6:
            int r0 = r9.bp
            int r0 = r0 + r15
            char r22 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r14
            char r23 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r13
            char r24 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 15
            char r25 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 17
            int r0 = r0 + r1
            char r19 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 18
            char r26 = r9.charAt(r0)
            r0 = r35
            r1 = r22
            r2 = r23
            r3 = r24
            r4 = r25
            r5 = r19
            r6 = r26
            boolean r0 = r0.checkTime(r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x0209
            return r11
        L_0x0209:
            int[] r0 = digits
            r0 = r0[r22]
            int r0 = r0 * 10
            int[] r1 = digits
            r1 = r1[r23]
            int r0 = r0 + r1
            int[] r1 = digits
            r1 = r1[r24]
            int r1 = r1 * 10
            int[] r2 = digits
            r2 = r2[r25]
            int r1 = r1 + r2
            int[] r2 = digits
            r2 = r2[r19]
            int r2 = r2 * 10
            int[] r3 = digits
            r3 = r3[r26]
            int r2 = r2 + r3
            java.util.Calendar r3 = r9.calendar
            r3.set(r15, r0)
            java.util.Calendar r3 = r9.calendar
            r3.set(r14, r1)
            java.util.Calendar r3 = r9.calendar
            r4 = 13
            r3.set(r4, r2)
            int r3 = r9.bp
            int r3 = r3 + 19
            char r3 = r9.charAt(r3)
            r4 = 46
            if (r3 != r4) goto L_0x03b4
            int r4 = r9.ISO8601_LEN_2
            if (r10 >= r4) goto L_0x024c
            return r11
        L_0x024c:
            int r4 = r9.bp
            int r4 = r4 + 20
            char r4 = r9.charAt(r4)
            if (r4 < r12) goto L_0x03ae
            r5 = 57
            if (r4 <= r5) goto L_0x0261
            r16 = r0
            r20 = r1
            r0 = 0
            goto L_0x03b3
        L_0x0261:
            int[] r5 = digits
            r5 = r5[r4]
            r6 = 1
            int r14 = r9.bp
            int r14 = r14 + 21
            char r14 = r9.charAt(r14)
            if (r14 < r12) goto L_0x027d
            r15 = 57
            if (r14 > r15) goto L_0x027d
            int r15 = r5 * 10
            int[] r16 = digits
            r16 = r16[r14]
            int r5 = r15 + r16
            r6 = 2
        L_0x027d:
            r14 = 2
            if (r6 != r14) goto L_0x0297
            int r14 = r9.bp
            int r14 = r14 + 22
            char r14 = r9.charAt(r14)
            if (r14 < r12) goto L_0x0297
            r15 = 57
            if (r14 > r15) goto L_0x0297
            int r15 = r5 * 10
            int[] r16 = digits
            r16 = r16[r14]
            int r5 = r15 + r16
            r6 = 3
        L_0x0297:
            java.util.Calendar r14 = r9.calendar
            r14.set(r13, r5)
            r13 = 0
            int r14 = r9.bp
            int r14 = r14 + 20
            int r14 = r14 + r6
            char r14 = r9.charAt(r14)
            r15 = 43
            if (r14 == r15) goto L_0x02b5
            r15 = 45
            if (r14 != r15) goto L_0x02af
            goto L_0x02b7
        L_0x02af:
            r16 = r0
            r20 = r1
            goto L_0x037b
        L_0x02b5:
            r15 = 45
        L_0x02b7:
            int r15 = r9.bp
            int r15 = r15 + 20
            int r15 = r15 + r6
            r16 = 1
            int r15 = r15 + 1
            char r15 = r9.charAt(r15)
            if (r15 < r12) goto L_0x03a8
            r11 = 49
            if (r15 <= r11) goto L_0x02d1
            r16 = r0
            r20 = r1
            r0 = 0
            goto L_0x03ad
        L_0x02d1:
            int r11 = r9.bp
            int r11 = r11 + 20
            int r11 = r11 + r6
            r16 = 2
            int r11 = r11 + 2
            char r11 = r9.charAt(r11)
            if (r11 < r12) goto L_0x03a2
            r12 = 57
            if (r11 <= r12) goto L_0x02ea
            r16 = r0
            r20 = r1
            goto L_0x03a6
        L_0x02ea:
            int r12 = r9.bp
            int r12 = r12 + 20
            int r12 = r12 + r6
            int r12 = r12 + 3
            char r12 = r9.charAt(r12)
            if (r12 != r8) goto L_0x0324
            int r8 = r9.bp
            int r8 = r8 + 20
            int r8 = r8 + r6
            int r8 = r8 + 4
            char r8 = r9.charAt(r8)
            r16 = r0
            r0 = 48
            if (r8 == r0) goto L_0x030b
            r17 = 0
            return r17
        L_0x030b:
            r17 = 0
            int r0 = r9.bp
            int r0 = r0 + 20
            int r0 = r0 + r6
            r18 = 5
            int r0 = r0 + 5
            char r0 = r9.charAt(r0)
            r20 = r1
            r1 = 48
            if (r0 == r1) goto L_0x0321
            return r17
        L_0x0321:
            r0 = 6
            r13 = r0
            goto L_0x0340
        L_0x0324:
            r16 = r0
            r20 = r1
            r1 = 48
            if (r12 != r1) goto L_0x033e
            int r0 = r9.bp
            int r0 = r0 + 20
            int r0 = r0 + r6
            int r0 = r0 + 4
            char r0 = r9.charAt(r0)
            if (r0 == r1) goto L_0x033b
            r1 = 0
            return r1
        L_0x033b:
            r0 = 5
            r13 = r0
            goto L_0x0340
        L_0x033e:
            r0 = 3
            r13 = r0
        L_0x0340:
            int[] r0 = digits
            r0 = r0[r15]
            int r0 = r0 * 10
            int[] r1 = digits
            r1 = r1[r11]
            int r0 = r0 + r1
            int r0 = r0 * 3600
            int r0 = r0 * 1000
            r1 = 45
            if (r14 != r1) goto L_0x0354
            int r0 = -r0
        L_0x0354:
            java.util.Calendar r1 = r9.calendar
            java.util.TimeZone r1 = r1.getTimeZone()
            int r1 = r1.getRawOffset()
            if (r1 == r0) goto L_0x0379
            java.lang.String[] r1 = java.util.TimeZone.getAvailableIDs(r0)
            int r8 = r1.length
            if (r8 <= 0) goto L_0x0376
            r8 = 0
            r17 = r1[r8]
            java.util.TimeZone r8 = java.util.TimeZone.getTimeZone(r17)
            r17 = r0
            java.util.Calendar r0 = r9.calendar
            r0.setTimeZone(r8)
            goto L_0x037b
        L_0x0376:
            r17 = r0
            goto L_0x037b
        L_0x0379:
            r17 = r0
        L_0x037b:
            int r0 = r9.bp
            int r1 = r6 + 20
            int r1 = r1 + r13
            int r0 = r0 + r1
            char r0 = r9.charAt(r0)
            r1 = 26
            if (r0 == r1) goto L_0x038f
            r1 = 34
            if (r0 == r1) goto L_0x038f
            r1 = 0
            return r1
        L_0x038f:
            int r1 = r9.bp
            int r8 = r6 + 20
            int r8 = r8 + r13
            int r1 = r1 + r8
            r9.bp = r1
            char r1 = r9.charAt(r1)
            r9.ch = r1
            r1 = 5
            r9.token = r1
            r1 = 1
            return r1
        L_0x03a2:
            r16 = r0
            r20 = r1
        L_0x03a6:
            r0 = 0
            return r0
        L_0x03a8:
            r16 = r0
            r20 = r1
            r0 = 0
        L_0x03ad:
            return r0
        L_0x03ae:
            r16 = r0
            r20 = r1
            r0 = 0
        L_0x03b3:
            return r0
        L_0x03b4:
            r16 = r0
            r20 = r1
            r0 = 0
            java.util.Calendar r1 = r9.calendar
            r1.set(r13, r0)
            int r0 = r9.bp
            int r0 = r0 + 19
            r9.bp = r0
            char r0 = r9.charAt(r0)
            r9.ch = r0
            r0 = 5
            r9.token = r0
            r0 = 1
            return r0
        L_0x03cf:
            r11 = 8
            r13 = 14
        L_0x03d3:
            if (r36 == 0) goto L_0x03d7
            r0 = 0
            return r0
        L_0x03d7:
            int r0 = r9.bp
            char r12 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 1
            int r0 = r0 + r1
            char r21 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 2
            int r0 = r0 + r1
            char r17 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 3
            char r22 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 4
            char r23 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 5
            int r0 = r0 + r1
            char r24 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 6
            char r25 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 7
            char r26 = r9.charAt(r0)
            r1 = r12
            r2 = r21
            r3 = r17
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = r25
            r8 = r26
            boolean r0 = checkDate(r1, r2, r3, r4, r5, r6, r7, r8)
            if (r0 != 0) goto L_0x042c
            r0 = 0
            return r0
        L_0x042c:
            r0 = r35
            r1 = r12
            r2 = r21
            r3 = r17
            r4 = r22
            r5 = r23
            r6 = r24
            r7 = r25
            r8 = r26
            r0.setCalendar(r1, r2, r3, r4, r5, r6, r7, r8)
            if (r10 == r11) goto L_0x04ef
            int r0 = r9.bp
            int r0 = r0 + r11
            char r7 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 9
            char r8 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + 10
            char r11 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r15
            char r28 = r9.charAt(r0)
            int r0 = r9.bp
            int r0 = r0 + r14
            char r29 = r9.charAt(r0)
            int r0 = r9.bp
            r1 = 13
            int r0 = r0 + r1
            char r30 = r9.charAt(r0)
            r0 = r35
            r1 = r7
            r2 = r8
            r3 = r11
            r4 = r28
            r5 = r29
            r6 = r30
            boolean r0 = r0.checkTime(r1, r2, r3, r4, r5, r6)
            if (r0 != 0) goto L_0x0483
            r0 = 0
            return r0
        L_0x0483:
            r0 = 17
            if (r10 != r0) goto L_0x04cc
            int r0 = r9.bp
            int r0 = r0 + r13
            char r0 = r9.charAt(r0)
            int r1 = r9.bp
            int r1 = r1 + 15
            char r1 = r9.charAt(r1)
            int r2 = r9.bp
            int r2 = r2 + 16
            char r2 = r9.charAt(r2)
            r3 = 48
            if (r0 < r3) goto L_0x04ca
            r4 = 57
            if (r0 <= r4) goto L_0x04a8
            r3 = 0
            goto L_0x04cb
        L_0x04a8:
            if (r1 < r3) goto L_0x04c8
            if (r1 <= r4) goto L_0x04ae
            r3 = 0
            goto L_0x04c9
        L_0x04ae:
            if (r2 < r3) goto L_0x04c6
            if (r2 <= r4) goto L_0x04b3
            goto L_0x04c6
        L_0x04b3:
            int[] r3 = digits
            r3 = r3[r0]
            int r3 = r3 * 100
            int[] r4 = digits
            r4 = r4[r1]
            int r4 = r4 * 10
            int r3 = r3 + r4
            int[] r4 = digits
            r4 = r4[r2]
            int r3 = r3 + r4
            goto L_0x04cd
        L_0x04c6:
            r3 = 0
            return r3
        L_0x04c8:
            r3 = 0
        L_0x04c9:
            return r3
        L_0x04ca:
            r3 = 0
        L_0x04cb:
            return r3
        L_0x04cc:
            r3 = 0
        L_0x04cd:
            int[] r0 = digits
            r0 = r0[r7]
            int r0 = r0 * 10
            int[] r1 = digits
            r1 = r1[r8]
            int r0 = r0 + r1
            int[] r1 = digits
            r1 = r1[r11]
            int r1 = r1 * 10
            int[] r2 = digits
            r2 = r2[r28]
            int r1 = r1 + r2
            int[] r2 = digits
            r2 = r2[r29]
            int r2 = r2 * 10
            int[] r4 = digits
            r4 = r4[r30]
            int r2 = r2 + r4
            goto L_0x04f3
        L_0x04ef:
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
        L_0x04f3:
            java.util.Calendar r4 = r9.calendar
            r4.set(r15, r0)
            java.util.Calendar r4 = r9.calendar
            r4.set(r14, r1)
            java.util.Calendar r4 = r9.calendar
            r5 = 13
            r4.set(r5, r2)
            java.util.Calendar r4 = r9.calendar
            r4.set(r13, r3)
            r4 = 5
            r9.token = r4
            r4 = 1
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.JSONScanner.scanISO8601DateIfMatch(boolean):boolean");
    }

    private boolean checkTime(char h0, char h1, char m0, char m1, char s0, char s1) {
        if (h0 == '0') {
            if (h1 < '0' || h1 > '9') {
                return false;
            }
        } else if (h0 == '1') {
            if (h1 < '0' || h1 > '9') {
                return false;
            }
        } else if (h0 != '2' || h1 < '0' || h1 > '4') {
            return false;
        }
        if (m0 < '0' || m0 > '5') {
            if (!(m0 == '6' && m1 == '0')) {
                return false;
            }
        } else if (m1 < '0' || m1 > '9') {
            return false;
        }
        if (s0 < '0' || s0 > '5') {
            if (s0 == '6' && s1 == '0') {
                return true;
            }
            return false;
        } else if (s1 < '0' || s1 > '9') {
            return false;
        } else {
            return true;
        }
    }

    private void setCalendar(char y0, char y1, char y2, char y3, char M0, char M1, char d0, char d1) {
        this.calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        int year = (digits[y0] * 1000) + (digits[y1] * 100) + (digits[y2] * 10) + digits[y3];
        int day = (digits[d0] * 10) + digits[d1];
        this.calendar.set(1, year);
        this.calendar.set(2, ((digits[M0] * 10) + digits[M1]) - 1);
        this.calendar.set(5, day);
    }

    static boolean checkDate(char y0, char y1, char y2, char y3, char M0, char M1, int d0, int d1) {
        if ((y0 != '1' && y0 != '2') || y1 < '0' || y1 > '9' || y2 < '0' || y2 > '9' || y3 < '0' || y3 > '9') {
            return false;
        }
        if (M0 == '0') {
            if (M1 < '1' || M1 > '9') {
                return false;
            }
        } else if (M0 != '1') {
            return false;
        } else {
            if (!(M1 == '0' || M1 == '1' || M1 == '2')) {
                return false;
            }
        }
        if (d0 == 48) {
            if (d1 < 49 || d1 > 57) {
                return false;
            }
            return true;
        } else if (d0 == 49 || d0 == 50) {
            if (d1 < 48 || d1 > 57) {
                return false;
            }
            return true;
        } else if (d0 != 51) {
            return false;
        } else {
            if (d1 == 48 || d1 == 49) {
                return true;
            }
            return false;
        }
    }

    public boolean isEOF() {
        if (this.bp != this.text.length()) {
            return this.ch == 26 && this.bp + 1 == this.text.length();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public final void arrayCopy(int srcPos, char[] dest, int destPos, int length) {
        this.text.getChars(srcPos, srcPos + length, dest, destPos);
    }
}
