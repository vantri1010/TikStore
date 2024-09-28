package com.google.zxing.client.result;

public final class ExpandedProductResultParser extends ResultParser {
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0209, code lost:
        if (r3.equals("10") != false) goto L_0x0221;
     */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0229  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0246  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0250  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x025a  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0262  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0271  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.zxing.client.result.ExpandedProductParsedResult parse(com.google.zxing.Result r34) {
        /*
            r33 = this;
            com.google.zxing.BarcodeFormat r0 = r34.getBarcodeFormat()
            com.google.zxing.BarcodeFormat r1 = com.google.zxing.BarcodeFormat.RSS_EXPANDED
            r2 = 0
            if (r0 == r1) goto L_0x000a
            return r2
        L_0x000a:
            java.lang.String r0 = getMassagedText(r34)
            r1 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            java.util.HashMap r15 = new java.util.HashMap
            r15.<init>()
            r16 = 0
            r19 = r3
            r20 = r4
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r25 = r9
            r26 = r10
            r27 = r11
            r28 = r12
            r29 = r13
            r30 = r14
            r14 = r16
            r3 = r2
        L_0x003d:
            int r4 = r0.length()
            if (r14 >= r4) goto L_0x0275
            java.lang.String r4 = findAIvalue(r14, r0)
            r3 = r4
            if (r4 != 0) goto L_0x004b
            return r2
        L_0x004b:
            int r4 = r3.length()
            r5 = 2
            int r4 = r4 + r5
            int r4 = r4 + r14
            r6 = r4
            java.lang.String r4 = findValue(r4, r0)
            int r7 = r4.length()
            int r14 = r6 + r7
            r6 = -1
            int r7 = r3.hashCode()
            r8 = 1536(0x600, float:2.152E-42)
            r9 = 0
            r10 = 4
            r11 = 3
            if (r7 == r8) goto L_0x0216
            r8 = 1537(0x601, float:2.154E-42)
            if (r7 == r8) goto L_0x020c
            r8 = 1567(0x61f, float:2.196E-42)
            if (r7 == r8) goto L_0x0203
            r5 = 1568(0x620, float:2.197E-42)
            if (r7 == r5) goto L_0x01f9
            r5 = 1570(0x622, float:2.2E-42)
            if (r7 == r5) goto L_0x01ef
            r5 = 1572(0x624, float:2.203E-42)
            if (r7 == r5) goto L_0x01e5
            r5 = 1574(0x626, float:2.206E-42)
            if (r7 == r5) goto L_0x01db
            switch(r7) {
                case 1567966: goto L_0x01d1;
                case 1567967: goto L_0x01c6;
                case 1567968: goto L_0x01bb;
                case 1567969: goto L_0x01af;
                case 1567970: goto L_0x01a3;
                case 1567971: goto L_0x0197;
                case 1567972: goto L_0x018b;
                case 1567973: goto L_0x017f;
                case 1567974: goto L_0x0173;
                case 1567975: goto L_0x0167;
                default: goto L_0x0084;
            }
        L_0x0084:
            switch(r7) {
                case 1568927: goto L_0x015b;
                case 1568928: goto L_0x014f;
                case 1568929: goto L_0x0143;
                case 1568930: goto L_0x0137;
                case 1568931: goto L_0x012b;
                case 1568932: goto L_0x011f;
                case 1568933: goto L_0x0113;
                case 1568934: goto L_0x0107;
                case 1568935: goto L_0x00fb;
                case 1568936: goto L_0x00ef;
                default: goto L_0x0087;
            }
        L_0x0087:
            switch(r7) {
                case 1575716: goto L_0x00e3;
                case 1575717: goto L_0x00d7;
                case 1575718: goto L_0x00cb;
                case 1575719: goto L_0x00bf;
                default: goto L_0x008a;
            }
        L_0x008a:
            switch(r7) {
                case 1575747: goto L_0x00b3;
                case 1575748: goto L_0x00a7;
                case 1575749: goto L_0x009b;
                case 1575750: goto L_0x008f;
                default: goto L_0x008d;
            }
        L_0x008d:
            goto L_0x0220
        L_0x008f:
            java.lang.String r5 = "3933"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 34
            goto L_0x0221
        L_0x009b:
            java.lang.String r5 = "3932"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 33
            goto L_0x0221
        L_0x00a7:
            java.lang.String r5 = "3931"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 32
            goto L_0x0221
        L_0x00b3:
            java.lang.String r5 = "3930"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 31
            goto L_0x0221
        L_0x00bf:
            java.lang.String r5 = "3923"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 30
            goto L_0x0221
        L_0x00cb:
            java.lang.String r5 = "3922"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 29
            goto L_0x0221
        L_0x00d7:
            java.lang.String r5 = "3921"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 28
            goto L_0x0221
        L_0x00e3:
            java.lang.String r5 = "3920"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 27
            goto L_0x0221
        L_0x00ef:
            java.lang.String r5 = "3209"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 26
            goto L_0x0221
        L_0x00fb:
            java.lang.String r5 = "3208"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 25
            goto L_0x0221
        L_0x0107:
            java.lang.String r5 = "3207"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 24
            goto L_0x0221
        L_0x0113:
            java.lang.String r5 = "3206"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 23
            goto L_0x0221
        L_0x011f:
            java.lang.String r5 = "3205"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 22
            goto L_0x0221
        L_0x012b:
            java.lang.String r5 = "3204"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 21
            goto L_0x0221
        L_0x0137:
            java.lang.String r5 = "3203"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 20
            goto L_0x0221
        L_0x0143:
            java.lang.String r5 = "3202"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 19
            goto L_0x0221
        L_0x014f:
            java.lang.String r5 = "3201"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 18
            goto L_0x0221
        L_0x015b:
            java.lang.String r5 = "3200"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 17
            goto L_0x0221
        L_0x0167:
            java.lang.String r5 = "3109"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 16
            goto L_0x0221
        L_0x0173:
            java.lang.String r5 = "3108"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 15
            goto L_0x0221
        L_0x017f:
            java.lang.String r5 = "3107"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 14
            goto L_0x0221
        L_0x018b:
            java.lang.String r5 = "3106"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 13
            goto L_0x0221
        L_0x0197:
            java.lang.String r5 = "3105"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 12
            goto L_0x0221
        L_0x01a3:
            java.lang.String r5 = "3104"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 11
            goto L_0x0221
        L_0x01af:
            java.lang.String r5 = "3103"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 10
            goto L_0x0221
        L_0x01bb:
            java.lang.String r5 = "3102"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 9
            goto L_0x0221
        L_0x01c6:
            java.lang.String r5 = "3101"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 8
            goto L_0x0221
        L_0x01d1:
            java.lang.String r5 = "3100"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 7
            goto L_0x0221
        L_0x01db:
            java.lang.String r5 = "17"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 6
            goto L_0x0221
        L_0x01e5:
            java.lang.String r5 = "15"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 5
            goto L_0x0221
        L_0x01ef:
            java.lang.String r5 = "13"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 4
            goto L_0x0221
        L_0x01f9:
            java.lang.String r5 = "11"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 3
            goto L_0x0221
        L_0x0203:
            java.lang.String r7 = "10"
            boolean r7 = r3.equals(r7)
            if (r7 == 0) goto L_0x008d
            goto L_0x0221
        L_0x020c:
            java.lang.String r5 = "01"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 1
            goto L_0x0221
        L_0x0216:
            java.lang.String r5 = "00"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x008d
            r5 = 0
            goto L_0x0221
        L_0x0220:
            r5 = -1
        L_0x0221:
            switch(r5) {
                case 0: goto L_0x0271;
                case 1: goto L_0x026e;
                case 2: goto L_0x026a;
                case 3: goto L_0x0266;
                case 4: goto L_0x0262;
                case 5: goto L_0x025e;
                case 6: goto L_0x025a;
                case 7: goto L_0x0250;
                case 8: goto L_0x0250;
                case 9: goto L_0x0250;
                case 10: goto L_0x0250;
                case 11: goto L_0x0250;
                case 12: goto L_0x0250;
                case 13: goto L_0x0250;
                case 14: goto L_0x0250;
                case 15: goto L_0x0250;
                case 16: goto L_0x0250;
                case 17: goto L_0x0246;
                case 18: goto L_0x0246;
                case 19: goto L_0x0246;
                case 20: goto L_0x0246;
                case 21: goto L_0x0246;
                case 22: goto L_0x0246;
                case 23: goto L_0x0246;
                case 24: goto L_0x0246;
                case 25: goto L_0x0246;
                case 26: goto L_0x0246;
                case 27: goto L_0x023e;
                case 28: goto L_0x023e;
                case 29: goto L_0x023e;
                case 30: goto L_0x023e;
                case 31: goto L_0x0229;
                case 32: goto L_0x0229;
                case 33: goto L_0x0229;
                case 34: goto L_0x0229;
                default: goto L_0x0224;
            }
        L_0x0224:
            r15.put(r3, r4)
            goto L_0x003d
        L_0x0229:
            int r5 = r4.length()
            if (r5 >= r10) goto L_0x0230
            return r2
        L_0x0230:
            java.lang.String r28 = r4.substring(r11)
            java.lang.String r30 = r4.substring(r9, r11)
            java.lang.String r29 = r3.substring(r11)
            goto L_0x003d
        L_0x023e:
            r28 = r4
            java.lang.String r29 = r3.substring(r11)
            goto L_0x003d
        L_0x0246:
            r25 = r4
            java.lang.String r26 = "LB"
            java.lang.String r27 = r3.substring(r11)
            goto L_0x003d
        L_0x0250:
            r25 = r4
            java.lang.String r26 = "KG"
            java.lang.String r27 = r3.substring(r11)
            goto L_0x003d
        L_0x025a:
            r24 = r4
            goto L_0x003d
        L_0x025e:
            r23 = r4
            goto L_0x003d
        L_0x0262:
            r22 = r4
            goto L_0x003d
        L_0x0266:
            r21 = r4
            goto L_0x003d
        L_0x026a:
            r20 = r4
            goto L_0x003d
        L_0x026e:
            r1 = r4
            goto L_0x003d
        L_0x0271:
            r19 = r4
            goto L_0x003d
        L_0x0275:
            com.google.zxing.client.result.ExpandedProductParsedResult r2 = new com.google.zxing.client.result.ExpandedProductParsedResult
            r3 = r2
            r4 = r0
            r5 = r1
            r6 = r19
            r7 = r20
            r8 = r21
            r9 = r22
            r10 = r23
            r11 = r24
            r12 = r25
            r13 = r26
            r31 = r14
            r14 = r27
            r32 = r15
            r15 = r28
            r16 = r29
            r17 = r30
            r18 = r32
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.ExpandedProductResultParser.parse(com.google.zxing.Result):com.google.zxing.client.result.ExpandedProductParsedResult");
    }

    private static String findAIvalue(int i, String rawText) {
        if (rawText.charAt(i) != '(') {
            return null;
        }
        CharSequence rawTextAux = rawText.substring(i + 1);
        StringBuilder buf = new StringBuilder();
        for (int index = 0; index < rawTextAux.length(); index++) {
            char charAt = rawTextAux.charAt(index);
            char currentChar = charAt;
            if (charAt == ')') {
                return buf.toString();
            }
            if (currentChar < '0' || currentChar > '9') {
                return null;
            }
            buf.append(currentChar);
        }
        return buf.toString();
    }

    private static String findValue(int i, String rawText) {
        StringBuilder buf = new StringBuilder();
        String rawTextAux = rawText.substring(i);
        for (int index = 0; index < rawTextAux.length(); index++) {
            char charAt = rawTextAux.charAt(index);
            char c = charAt;
            if (charAt == '(') {
                if (findAIvalue(index, rawTextAux) != null) {
                    break;
                }
                buf.append('(');
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
