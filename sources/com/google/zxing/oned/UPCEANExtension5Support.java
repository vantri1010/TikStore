package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.EnumMap;
import java.util.Map;

final class UPCEANExtension5Support {
    private static final int[] CHECK_DIGIT_ENCODINGS = {24, 20, 18, 17, 12, 6, 3, 10, 9, 5};
    private final int[] decodeMiddleCounters = new int[4];
    private final StringBuilder decodeRowStringBuffer = new StringBuilder();

    UPCEANExtension5Support() {
    }

    /* access modifiers changed from: package-private */
    public Result decodeRow(int rowNumber, BitArray row, int[] extensionStartRange) throws NotFoundException {
        StringBuilder sb = this.decodeRowStringBuffer;
        StringBuilder result = sb;
        sb.setLength(0);
        int end = decodeMiddle(row, extensionStartRange, result);
        String sb2 = result.toString();
        String resultString = sb2;
        Map<ResultMetadataType, Object> extensionData = parseExtensionString(sb2);
        Result extensionResult = new Result(resultString, (byte[]) null, new ResultPoint[]{new ResultPoint(((float) (extensionStartRange[0] + extensionStartRange[1])) / 2.0f, (float) rowNumber), new ResultPoint((float) end, (float) rowNumber)}, BarcodeFormat.UPC_EAN_EXTENSION);
        if (extensionData != null) {
            extensionResult.putAllMetadata(extensionData);
        }
        return extensionResult;
    }

    private int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
        int[] iArr = this.decodeMiddleCounters;
        int[] counters = iArr;
        iArr[0] = 0;
        counters[1] = 0;
        counters[2] = 0;
        counters[3] = 0;
        int end = row.getSize();
        int rowOffset = startRange[1];
        int lgPatternFound = 0;
        for (int x = 0; x < 5 && rowOffset < end; x++) {
            int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEANReader.L_AND_G_PATTERNS);
            resultString.append((char) ((bestMatch % 10) + 48));
            for (int counter : counters) {
                rowOffset += counter;
            }
            if (bestMatch >= 10) {
                lgPatternFound |= 1 << (4 - x);
            }
            if (x != 4) {
                rowOffset = row.getNextUnset(row.getNextSet(rowOffset));
            }
        }
        if (resultString.length() == 5) {
            if (extensionChecksum(resultString.toString()) == determineCheckDigit(lgPatternFound)) {
                return rowOffset;
            }
            throw NotFoundException.getNotFoundInstance();
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int extensionChecksum(CharSequence s) {
        int length = s.length();
        int sum = 0;
        for (int i = length - 2; i >= 0; i -= 2) {
            sum += s.charAt(i) - '0';
        }
        int sum2 = sum * 3;
        for (int i2 = length - 1; i2 >= 0; i2 -= 2) {
            sum2 += s.charAt(i2) - '0';
        }
        return (sum2 * 3) % 10;
    }

    private static int determineCheckDigit(int lgPatternFound) throws NotFoundException {
        for (int d = 0; d < 10; d++) {
            if (lgPatternFound == CHECK_DIGIT_ENCODINGS[d]) {
                return d;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static Map<ResultMetadataType, Object> parseExtensionString(String raw) {
        if (raw.length() != 5) {
            return null;
        }
        Object parseExtension5String = parseExtension5String(raw);
        Object value = parseExtension5String;
        if (parseExtension5String == null) {
            return null;
        }
        Map<ResultMetadataType, Object> enumMap = new EnumMap<>(ResultMetadataType.class);
        Map<ResultMetadataType, Object> result = enumMap;
        enumMap.put(ResultMetadataType.SUGGESTED_PRICE, value);
        return result;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0039, code lost:
        if (r6.equals("90000") != false) goto L_0x003d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String parseExtension5String(java.lang.String r6) {
        /*
            r0 = 0
            char r1 = r6.charAt(r0)
            r2 = 48
            java.lang.String r3 = ""
            r4 = 1
            if (r1 == r2) goto L_0x0051
            r2 = 53
            if (r1 == r2) goto L_0x004d
            r2 = 57
            if (r1 == r2) goto L_0x0015
            goto L_0x0054
        L_0x0015:
            r1 = -1
            int r2 = r6.hashCode()
            r5 = 2
            switch(r2) {
                case 54118329: goto L_0x0033;
                case 54395376: goto L_0x0029;
                case 54395377: goto L_0x001f;
                default: goto L_0x001e;
            }
        L_0x001e:
            goto L_0x003c
        L_0x001f:
            java.lang.String r0 = "99991"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001e
            r0 = 1
            goto L_0x003d
        L_0x0029:
            java.lang.String r0 = "99990"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x001e
            r0 = 2
            goto L_0x003d
        L_0x0033:
            java.lang.String r2 = "90000"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x001e
            goto L_0x003d
        L_0x003c:
            r0 = -1
        L_0x003d:
            if (r0 == 0) goto L_0x004b
            if (r0 == r4) goto L_0x0048
            if (r0 == r5) goto L_0x0045
            goto L_0x0054
        L_0x0045:
            java.lang.String r6 = "Used"
            return r6
        L_0x0048:
            java.lang.String r6 = "0.00"
            return r6
        L_0x004b:
            r6 = 0
            return r6
        L_0x004d:
            java.lang.String r3 = "$"
            goto L_0x0054
        L_0x0051:
            java.lang.String r3 = "£"
        L_0x0054:
            java.lang.String r6 = r6.substring(r4)
            int r6 = java.lang.Integer.parseInt(r6)
            int r0 = r6 / 100
            java.lang.String r0 = java.lang.String.valueOf(r0)
            int r6 = r6 % 100
            r1 = 10
            if (r6 >= r1) goto L_0x0073
            java.lang.String r6 = java.lang.String.valueOf(r6)
            java.lang.String r1 = "0"
            java.lang.String r6 = r1.concat(r6)
            goto L_0x0077
        L_0x0073:
            java.lang.String r6 = java.lang.String.valueOf(r6)
        L_0x0077:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r0)
            r0 = 46
            r1.append(r0)
            r1.append(r6)
            java.lang.String r6 = r1.toString()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.UPCEANExtension5Support.parseExtension5String(java.lang.String):java.lang.String");
    }
}
