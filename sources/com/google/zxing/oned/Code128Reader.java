package com.google.zxing.oned;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class Code128Reader extends OneDReader {
    private static final int CODE_CODE_A = 101;
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_FNC_1 = 102;
    private static final int CODE_FNC_2 = 97;
    private static final int CODE_FNC_3 = 96;
    private static final int CODE_FNC_4_A = 101;
    private static final int CODE_FNC_4_B = 100;
    static final int[][] CODE_PATTERNS = {new int[]{2, 1, 2, 2, 2, 2}, new int[]{2, 2, 2, 1, 2, 2}, new int[]{2, 2, 2, 2, 2, 1}, new int[]{1, 2, 1, 2, 2, 3}, new int[]{1, 2, 1, 3, 2, 2}, new int[]{1, 3, 1, 2, 2, 2}, new int[]{1, 2, 2, 2, 1, 3}, new int[]{1, 2, 2, 3, 1, 2}, new int[]{1, 3, 2, 2, 1, 2}, new int[]{2, 2, 1, 2, 1, 3}, new int[]{2, 2, 1, 3, 1, 2}, new int[]{2, 3, 1, 2, 1, 2}, new int[]{1, 1, 2, 2, 3, 2}, new int[]{1, 2, 2, 1, 3, 2}, new int[]{1, 2, 2, 2, 3, 1}, new int[]{1, 1, 3, 2, 2, 2}, new int[]{1, 2, 3, 1, 2, 2}, new int[]{1, 2, 3, 2, 2, 1}, new int[]{2, 2, 3, 2, 1, 1}, new int[]{2, 2, 1, 1, 3, 2}, new int[]{2, 2, 1, 2, 3, 1}, new int[]{2, 1, 3, 2, 1, 2}, new int[]{2, 2, 3, 1, 1, 2}, new int[]{3, 1, 2, 1, 3, 1}, new int[]{3, 1, 1, 2, 2, 2}, new int[]{3, 2, 1, 1, 2, 2}, new int[]{3, 2, 1, 2, 2, 1}, new int[]{3, 1, 2, 2, 1, 2}, new int[]{3, 2, 2, 1, 1, 2}, new int[]{3, 2, 2, 2, 1, 1}, new int[]{2, 1, 2, 1, 2, 3}, new int[]{2, 1, 2, 3, 2, 1}, new int[]{2, 3, 2, 1, 2, 1}, new int[]{1, 1, 1, 3, 2, 3}, new int[]{1, 3, 1, 1, 2, 3}, new int[]{1, 3, 1, 3, 2, 1}, new int[]{1, 1, 2, 3, 1, 3}, new int[]{1, 3, 2, 1, 1, 3}, new int[]{1, 3, 2, 3, 1, 1}, new int[]{2, 1, 1, 3, 1, 3}, new int[]{2, 3, 1, 1, 1, 3}, new int[]{2, 3, 1, 3, 1, 1}, new int[]{1, 1, 2, 1, 3, 3}, new int[]{1, 1, 2, 3, 3, 1}, new int[]{1, 3, 2, 1, 3, 1}, new int[]{1, 1, 3, 1, 2, 3}, new int[]{1, 1, 3, 3, 2, 1}, new int[]{1, 3, 3, 1, 2, 1}, new int[]{3, 1, 3, 1, 2, 1}, new int[]{2, 1, 1, 3, 3, 1}, new int[]{2, 3, 1, 1, 3, 1}, new int[]{2, 1, 3, 1, 1, 3}, new int[]{2, 1, 3, 3, 1, 1}, new int[]{2, 1, 3, 1, 3, 1}, new int[]{3, 1, 1, 1, 2, 3}, new int[]{3, 1, 1, 3, 2, 1}, new int[]{3, 3, 1, 1, 2, 1}, new int[]{3, 1, 2, 1, 1, 3}, new int[]{3, 1, 2, 3, 1, 1}, new int[]{3, 3, 2, 1, 1, 1}, new int[]{3, 1, 4, 1, 1, 1}, new int[]{2, 2, 1, 4, 1, 1}, new int[]{4, 3, 1, 1, 1, 1}, new int[]{1, 1, 1, 2, 2, 4}, new int[]{1, 1, 1, 4, 2, 2}, new int[]{1, 2, 1, 1, 2, 4}, new int[]{1, 2, 1, 4, 2, 1}, new int[]{1, 4, 1, 1, 2, 2}, new int[]{1, 4, 1, 2, 2, 1}, new int[]{1, 1, 2, 2, 1, 4}, new int[]{1, 1, 2, 4, 1, 2}, new int[]{1, 2, 2, 1, 1, 4}, new int[]{1, 2, 2, 4, 1, 1}, new int[]{1, 4, 2, 1, 1, 2}, new int[]{1, 4, 2, 2, 1, 1}, new int[]{2, 4, 1, 2, 1, 1}, new int[]{2, 2, 1, 1, 1, 4}, new int[]{4, 1, 3, 1, 1, 1}, new int[]{2, 4, 1, 1, 1, 2}, new int[]{1, 3, 4, 1, 1, 1}, new int[]{1, 1, 1, 2, 4, 2}, new int[]{1, 2, 1, 1, 4, 2}, new int[]{1, 2, 1, 2, 4, 1}, new int[]{1, 1, 4, 2, 1, 2}, new int[]{1, 2, 4, 1, 1, 2}, new int[]{1, 2, 4, 2, 1, 1}, new int[]{4, 1, 1, 2, 1, 2}, new int[]{4, 2, 1, 1, 1, 2}, new int[]{4, 2, 1, 2, 1, 1}, new int[]{2, 1, 2, 1, 4, 1}, new int[]{2, 1, 4, 1, 2, 1}, new int[]{4, 1, 2, 1, 2, 1}, new int[]{1, 1, 1, 1, 4, 3}, new int[]{1, 1, 1, 3, 4, 1}, new int[]{1, 3, 1, 1, 4, 1}, new int[]{1, 1, 4, 1, 1, 3}, new int[]{1, 1, 4, 3, 1, 1}, new int[]{4, 1, 1, 1, 1, 3}, new int[]{4, 1, 1, 3, 1, 1}, new int[]{1, 1, 3, 1, 4, 1}, new int[]{1, 1, 4, 1, 3, 1}, new int[]{3, 1, 1, 1, 4, 1}, new int[]{4, 1, 1, 1, 3, 1}, new int[]{2, 1, 1, 4, 1, 2}, new int[]{2, 1, 1, 2, 1, 4}, new int[]{2, 1, 1, 2, 3, 2}, new int[]{2, 3, 3, 1, 1, 1, 2}};
    private static final int CODE_SHIFT = 98;
    private static final int CODE_START_A = 103;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;
    private static final float MAX_AVG_VARIANCE = 0.25f;
    private static final float MAX_INDIVIDUAL_VARIANCE = 0.7f;

    private static int[] findStartPattern(BitArray row) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        int[] counters = new int[6];
        int patternStart = rowOffset;
        boolean isWhite = false;
        for (int i = rowOffset; i < width; i++) {
            boolean z = true;
            if (row.get(i) != isWhite) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == 5) {
                    float bestVariance = MAX_AVG_VARIANCE;
                    int bestMatch = -1;
                    for (int startCode = 103; startCode <= 105; startCode++) {
                        float patternMatchVariance = patternMatchVariance(counters, CODE_PATTERNS[startCode], MAX_INDIVIDUAL_VARIANCE);
                        float variance = patternMatchVariance;
                        if (patternMatchVariance < bestVariance) {
                            bestVariance = variance;
                            bestMatch = startCode;
                        }
                    }
                    if (bestMatch < 0 || !row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
                        patternStart += counters[0] + counters[1];
                        System.arraycopy(counters, 2, counters, 0, counterPosition - 1);
                        counters[counterPosition - 1] = 0;
                        counters[counterPosition] = 0;
                        counterPosition--;
                    } else {
                        return new int[]{patternStart, i, bestMatch};
                    }
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                if (isWhite) {
                    z = false;
                }
                isWhite = z;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray row, int[] counters, int rowOffset) throws NotFoundException {
        recordPattern(row, rowOffset, counters);
        float bestVariance = MAX_AVG_VARIANCE;
        int bestMatch = -1;
        int d = 0;
        while (true) {
            int[][] iArr = CODE_PATTERNS;
            if (d >= iArr.length) {
                break;
            }
            float patternMatchVariance = patternMatchVariance(counters, iArr[d], MAX_INDIVIDUAL_VARIANCE);
            float variance = patternMatchVariance;
            if (patternMatchVariance < bestVariance) {
                bestVariance = variance;
                bestMatch = d;
            }
            d++;
        }
        if (bestMatch >= 0) {
            return bestMatch;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x019d, code lost:
        r5 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.Result decodeRow(int r25, com.google.zxing.common.BitArray r26, java.util.Map<com.google.zxing.DecodeHintType, ?> r27) throws com.google.zxing.NotFoundException, com.google.zxing.FormatException, com.google.zxing.ChecksumException {
        /*
            r24 = this;
            r0 = r26
            r1 = r27
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0012
            com.google.zxing.DecodeHintType r4 = com.google.zxing.DecodeHintType.ASSUME_GS1
            boolean r1 = r1.containsKey(r4)
            if (r1 == 0) goto L_0x0012
            r1 = 1
            goto L_0x0013
        L_0x0012:
            r1 = 0
        L_0x0013:
            int[] r4 = findStartPattern(r26)
            r5 = 2
            r6 = r4[r5]
            java.util.ArrayList r7 = new java.util.ArrayList
            r8 = 20
            r7.<init>(r8)
            byte r9 = (byte) r6
            java.lang.Byte r9 = java.lang.Byte.valueOf(r9)
            r7.add(r9)
            switch(r6) {
                case 103: goto L_0x0039;
                case 104: goto L_0x0035;
                case 105: goto L_0x0031;
                default: goto L_0x002c;
            }
        L_0x002c:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x0031:
            r12 = 99
            goto L_0x003c
        L_0x0035:
            r12 = 100
            goto L_0x003c
        L_0x0039:
            r12 = 101(0x65, float:1.42E-43)
        L_0x003c:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>(r8)
            r8 = r4[r3]
            r14 = r4[r2]
            r15 = 6
            int[] r2 = new int[r15]
            r9 = 0
            r16 = 0
            r17 = 1
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r23 = r12
            r12 = r8
            r8 = r14
            r14 = r23
        L_0x0063:
            if (r16 != 0) goto L_0x0200
            int r12 = decodeCode(r0, r2, r8)
            byte r5 = (byte) r12
            java.lang.Byte r5 = java.lang.Byte.valueOf(r5)
            r7.add(r5)
            r5 = 106(0x6a, float:1.49E-43)
            if (r12 == r5) goto L_0x007a
            r17 = 1
        L_0x007a:
            if (r12 == r5) goto L_0x0082
            int r20 = r20 + 1
            int r21 = r20 * r12
            int r6 = r6 + r21
        L_0x0082:
            r21 = r8
            r11 = 0
        L_0x0086:
            if (r11 >= r15) goto L_0x008f
            r22 = r2[r11]
            int r21 = r21 + r22
            int r11 = r11 + 1
            goto L_0x0086
        L_0x008f:
            switch(r12) {
                case 103: goto L_0x009d;
                case 104: goto L_0x009d;
                case 105: goto L_0x009d;
                default: goto L_0x0092;
            }
        L_0x0092:
            r11 = 96
            java.lang.String r15 = "]C1"
            switch(r14) {
                case 99: goto L_0x01a7;
                case 100: goto L_0x0133;
                case 101: goto L_0x00a2;
                default: goto L_0x0099;
            }
        L_0x0099:
            r10 = 100
            goto L_0x01e2
        L_0x009d:
            com.google.zxing.FormatException r0 = com.google.zxing.FormatException.getFormatInstance()
            throw r0
        L_0x00a2:
            r10 = 64
            if (r12 >= r10) goto L_0x00bd
            if (r9 != r3) goto L_0x00af
            int r5 = r12 + 32
            char r5 = (char) r5
            r13.append(r5)
            goto L_0x00b7
        L_0x00af:
            int r5 = r12 + 32
            int r5 = r5 + 128
            char r5 = (char) r5
            r13.append(r5)
        L_0x00b7:
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x00bd:
            if (r12 >= r11) goto L_0x00d4
            if (r9 != r3) goto L_0x00c8
            int r5 = r12 + -64
            char r5 = (char) r5
            r13.append(r5)
            goto L_0x00ce
        L_0x00c8:
            int r5 = r12 + 64
            char r5 = (char) r5
            r13.append(r5)
        L_0x00ce:
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x00d4:
            if (r12 == r5) goto L_0x00d8
            r17 = 0
        L_0x00d8:
            if (r12 == r5) goto L_0x012c
            switch(r12) {
                case 96: goto L_0x012a;
                case 97: goto L_0x012a;
                case 98: goto L_0x0121;
                case 99: goto L_0x0119;
                case 100: goto L_0x0111;
                case 101: goto L_0x00f2;
                case 102: goto L_0x00de;
                default: goto L_0x00dd;
            }
        L_0x00dd:
            goto L_0x012e
        L_0x00de:
            if (r1 == 0) goto L_0x012e
            int r5 = r13.length()
            if (r5 != 0) goto L_0x00eb
            r13.append(r15)
            goto L_0x019d
        L_0x00eb:
            r5 = 29
            r13.append(r5)
            goto L_0x019d
        L_0x00f2:
            if (r3 != 0) goto L_0x00fe
            if (r9 == 0) goto L_0x00fe
            r3 = 1
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x00fe:
            if (r3 == 0) goto L_0x010a
            if (r9 == 0) goto L_0x010a
            r3 = 0
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x010a:
            r5 = 0
            r9 = 1
            r10 = 100
            goto L_0x01e3
        L_0x0111:
            r5 = 0
            r10 = 100
            r14 = 100
            goto L_0x01e3
        L_0x0119:
            r5 = 0
            r10 = 100
            r14 = 99
            goto L_0x01e3
        L_0x0121:
            r5 = 1
            r10 = 100
            r14 = 100
            goto L_0x01e3
        L_0x012a:
            goto L_0x019d
        L_0x012c:
            r16 = 1
        L_0x012e:
            r5 = 0
            r10 = 100
            goto L_0x01e3
        L_0x0133:
            if (r12 >= r11) goto L_0x014c
            if (r9 != r3) goto L_0x013e
            int r5 = r12 + 32
            char r5 = (char) r5
            r13.append(r5)
            goto L_0x0146
        L_0x013e:
            int r5 = r12 + 32
            int r5 = r5 + 128
            char r5 = (char) r5
            r13.append(r5)
        L_0x0146:
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x014c:
            if (r12 == r5) goto L_0x0150
            r17 = 0
        L_0x0150:
            if (r12 == r5) goto L_0x01a1
            switch(r12) {
                case 96: goto L_0x019c;
                case 97: goto L_0x019c;
                case 98: goto L_0x0194;
                case 99: goto L_0x018d;
                case 100: goto L_0x0170;
                case 101: goto L_0x0168;
                case 102: goto L_0x0156;
                default: goto L_0x0155;
            }
        L_0x0155:
            goto L_0x01a3
        L_0x0156:
            if (r1 == 0) goto L_0x01a3
            int r5 = r13.length()
            if (r5 != 0) goto L_0x0162
            r13.append(r15)
            goto L_0x019d
        L_0x0162:
            r5 = 29
            r13.append(r5)
            goto L_0x019d
        L_0x0168:
            r5 = 0
            r10 = 100
            r14 = 101(0x65, float:1.42E-43)
            goto L_0x01e3
        L_0x0170:
            if (r3 != 0) goto L_0x017c
            if (r9 == 0) goto L_0x017c
            r3 = 1
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x017c:
            if (r3 == 0) goto L_0x0187
            if (r9 == 0) goto L_0x0187
            r3 = 0
            r5 = 0
            r9 = 0
            r10 = 100
            goto L_0x01e3
        L_0x0187:
            r5 = 0
            r9 = 1
            r10 = 100
            goto L_0x01e3
        L_0x018d:
            r5 = 0
            r10 = 100
            r14 = 99
            goto L_0x01e3
        L_0x0194:
            r5 = 1
            r10 = 100
            r14 = 101(0x65, float:1.42E-43)
            goto L_0x01e3
        L_0x019c:
        L_0x019d:
            r5 = 0
            r10 = 100
            goto L_0x01e3
        L_0x01a1:
            r16 = 1
        L_0x01a3:
            r5 = 0
            r10 = 100
            goto L_0x01e3
        L_0x01a7:
            r10 = 100
            if (r12 >= r10) goto L_0x01b8
            r5 = 10
            if (r12 >= r5) goto L_0x01b4
            r5 = 48
            r13.append(r5)
        L_0x01b4:
            r13.append(r12)
            goto L_0x01e2
        L_0x01b8:
            if (r12 == r5) goto L_0x01bc
            r17 = 0
        L_0x01bc:
            if (r12 == r5) goto L_0x01de
            switch(r12) {
                case 100: goto L_0x01d9;
                case 101: goto L_0x01d4;
                case 102: goto L_0x01c2;
                default: goto L_0x01c1;
            }
        L_0x01c1:
            goto L_0x01e2
        L_0x01c2:
            if (r1 == 0) goto L_0x01e2
            int r5 = r13.length()
            if (r5 != 0) goto L_0x01ce
            r13.append(r15)
            goto L_0x01e2
        L_0x01ce:
            r5 = 29
            r13.append(r5)
            goto L_0x01e2
        L_0x01d4:
            r5 = 0
            r14 = 101(0x65, float:1.42E-43)
            goto L_0x01e3
        L_0x01d9:
            r5 = 0
            r14 = 100
            goto L_0x01e3
        L_0x01de:
            r5 = 0
            r16 = 1
            goto L_0x01e3
        L_0x01e2:
            r5 = 0
        L_0x01e3:
            if (r18 == 0) goto L_0x01ef
            r11 = 101(0x65, float:1.42E-43)
            if (r14 != r11) goto L_0x01ec
            r14 = 100
            goto L_0x01f1
        L_0x01ec:
            r14 = 101(0x65, float:1.42E-43)
            goto L_0x01f1
        L_0x01ef:
            r11 = 101(0x65, float:1.42E-43)
        L_0x01f1:
            r18 = r5
            r5 = 2
            r15 = 6
            r23 = r12
            r12 = r8
            r8 = r21
            r21 = r19
            r19 = r23
            goto L_0x0063
        L_0x0200:
            int r1 = r8 - r12
            int r2 = r0.getNextUnset(r8)
            int r3 = r26.getSize()
            int r5 = r2 - r12
            r8 = 2
            int r5 = r5 / r8
            int r5 = r5 + r2
            int r3 = java.lang.Math.min(r3, r5)
            r5 = 0
            boolean r0 = r0.isRange(r2, r3, r5)
            if (r0 == 0) goto L_0x0291
            r3 = r21
            int r20 = r20 * r3
            int r6 = r6 - r20
            int r6 = r6 % 103
            if (r6 != r3) goto L_0x028c
            int r0 = r13.length()
            if (r0 == 0) goto L_0x0287
            if (r0 <= 0) goto L_0x023e
            if (r17 == 0) goto L_0x023e
            r2 = 99
            if (r14 != r2) goto L_0x0239
            int r2 = r0 + -2
            r13.delete(r2, r0)
            goto L_0x023e
        L_0x0239:
            int r2 = r0 + -1
            r13.delete(r2, r0)
        L_0x023e:
            r0 = 1
            r2 = r4[r0]
            r0 = 0
            r3 = r4[r0]
            int r2 = r2 + r3
            float r0 = (float) r2
            r2 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r2
            float r3 = (float) r12
            float r1 = (float) r1
            float r1 = r1 / r2
            float r3 = r3 + r1
            int r1 = r7.size()
            byte[] r2 = new byte[r1]
            r5 = 0
        L_0x0254:
            if (r5 >= r1) goto L_0x0265
            java.lang.Object r4 = r7.get(r5)
            java.lang.Byte r4 = (java.lang.Byte) r4
            byte r4 = r4.byteValue()
            r2[r5] = r4
            int r5 = r5 + 1
            goto L_0x0254
        L_0x0265:
            com.google.zxing.Result r1 = new com.google.zxing.Result
            java.lang.String r4 = r13.toString()
            r5 = 2
            com.google.zxing.ResultPoint[] r5 = new com.google.zxing.ResultPoint[r5]
            com.google.zxing.ResultPoint r6 = new com.google.zxing.ResultPoint
            r7 = r25
            float r7 = (float) r7
            r6.<init>(r0, r7)
            r0 = 0
            r5[r0] = r6
            com.google.zxing.ResultPoint r0 = new com.google.zxing.ResultPoint
            r0.<init>(r3, r7)
            r3 = 1
            r5[r3] = r0
            com.google.zxing.BarcodeFormat r0 = com.google.zxing.BarcodeFormat.CODE_128
            r1.<init>(r4, r2, r5, r0)
            return r1
        L_0x0287:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
        L_0x028c:
            com.google.zxing.ChecksumException r0 = com.google.zxing.ChecksumException.getChecksumInstance()
            throw r0
        L_0x0291:
            com.google.zxing.NotFoundException r0 = com.google.zxing.NotFoundException.getNotFoundInstance()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.Code128Reader.decodeRow(int, com.google.zxing.common.BitArray, java.util.Map):com.google.zxing.Result");
    }
}
