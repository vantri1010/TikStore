package im.bclpbkiauv.phoneformat;

public class PhoneRule {
    public int byte8;
    public String desensitizationFormat;
    public int flag12;
    public int flag13;
    public String format;
    public boolean hasIntlPrefix;
    public boolean hasTrunkPrefix;
    public int maxLen;
    public int maxVal;
    public int minVal;
    public int otherFlag;
    public int prefixLen;

    /* access modifiers changed from: package-private */
    public String format(String str, String intlPrefix, String trunkPrefix) {
        boolean hadC = false;
        boolean hadN = false;
        boolean hasOpen = false;
        int spot = 0;
        StringBuilder res = new StringBuilder(20);
        for (int i = 0; i < this.format.length(); i++) {
            char ch = this.format.charAt(i);
            if (ch != '#') {
                if (ch != '(') {
                    if (ch == 'c') {
                        hadC = true;
                        if (intlPrefix != null) {
                            res.append(intlPrefix);
                        }
                    } else if (ch == 'n') {
                        hadN = true;
                        if (trunkPrefix != null) {
                            res.append(trunkPrefix);
                        }
                    }
                } else if (spot < str.length()) {
                    hasOpen = true;
                }
                if (!(ch == ' ' && i > 0 && ((this.format.charAt(i - 1) == 'n' && trunkPrefix == null) || (this.format.charAt(i - 1) == 'c' && intlPrefix == null))) && (spot < str.length() || (hasOpen && ch == ')'))) {
                    res.append(this.format.substring(i, i + 1));
                    if (ch == ')') {
                        hasOpen = false;
                    }
                }
            } else if (spot < str.length()) {
                res.append(str.substring(spot, spot + 1));
                spot++;
            } else if (hasOpen) {
                res.append(" ");
            }
        }
        if (intlPrefix != null && !hadC) {
            res.insert(0, String.format("%s ", new Object[]{intlPrefix}));
        } else if (trunkPrefix != null && !hadN) {
            res.insert(0, trunkPrefix);
        }
        return res.toString();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String desensitization(java.lang.String r17, java.lang.String r18, java.lang.String r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            r2 = r19
            java.lang.String r3 = r0.desensitizationFormat
            r4 = 0
            r5 = 1
            if (r3 != 0) goto L_0x006e
            java.lang.String r3 = r0.format
            if (r3 == 0) goto L_0x006e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            int r3 = r3.length()
            r6.<init>(r3)
            r3 = r6
            java.lang.String r6 = r0.format
            java.lang.String r7 = "-"
            java.lang.String[] r6 = r6.split(r7)
            int r7 = r6.length
            if (r7 != r5) goto L_0x0030
            r7 = r6[r4]
            r8 = 3
            java.lang.String r7 = r0.handleDesensitizationSpecial(r7, r8)
            r3.append(r7)
            goto L_0x0068
        L_0x0030:
            int r7 = r6.length
            r8 = 2
            if (r7 != r8) goto L_0x0043
            r7 = r6[r4]
            r3.append(r7)
            r7 = r6[r5]
            java.lang.String r7 = r0.handleDesensitizationSpecial(r7, r8)
            r3.append(r7)
            goto L_0x0068
        L_0x0043:
            r7 = r6[r4]
            r3.append(r7)
            r7 = 1
        L_0x0049:
            int r8 = r6.length
            int r8 = r8 - r5
            if (r7 >= r8) goto L_0x0061
            r8 = r6[r7]
            r9 = 0
        L_0x0050:
            int r10 = r8.length()
            if (r9 >= r10) goto L_0x005e
            java.lang.String r10 = "*"
            r3.append(r10)
            int r9 = r9 + 1
            goto L_0x0050
        L_0x005e:
            int r7 = r7 + 1
            goto L_0x0049
        L_0x0061:
            int r7 = r6.length
            int r7 = r7 - r5
            r7 = r6[r7]
            r3.append(r7)
        L_0x0068:
            java.lang.String r7 = r3.toString()
            r0.desensitizationFormat = r7
        L_0x006e:
            r3 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r10 = 20
            r9.<init>(r10)
            r10 = 0
        L_0x007a:
            java.lang.String r11 = r0.desensitizationFormat
            int r11 = r11.length()
            if (r10 >= r11) goto L_0x010c
            java.lang.String r11 = r0.desensitizationFormat
            char r11 = r11.charAt(r10)
            r12 = 35
            if (r11 == r12) goto L_0x00eb
            r12 = 40
            r13 = 110(0x6e, float:1.54E-43)
            r14 = 99
            if (r11 == r12) goto L_0x00a9
            if (r11 == r14) goto L_0x00a0
            if (r11 == r13) goto L_0x0099
            goto L_0x00b0
        L_0x0099:
            r6 = 1
            if (r2 == 0) goto L_0x00a6
            r9.append(r2)
            goto L_0x00a6
        L_0x00a0:
            r3 = 1
            if (r1 == 0) goto L_0x00a6
            r9.append(r1)
        L_0x00a6:
            r13 = r17
            goto L_0x0108
        L_0x00a9:
            int r12 = r17.length()
            if (r8 >= r12) goto L_0x00b0
            r7 = 1
        L_0x00b0:
            r12 = 32
            if (r11 != r12) goto L_0x00ce
            if (r10 <= 0) goto L_0x00ce
            java.lang.String r12 = r0.format
            int r15 = r10 + -1
            char r12 = r12.charAt(r15)
            if (r12 != r13) goto L_0x00c2
            if (r2 == 0) goto L_0x00a6
        L_0x00c2:
            java.lang.String r12 = r0.format
            int r13 = r10 + -1
            char r12 = r12.charAt(r13)
            if (r12 != r14) goto L_0x00ce
            if (r1 == 0) goto L_0x00a6
        L_0x00ce:
            int r12 = r17.length()
            r13 = 41
            if (r8 < r12) goto L_0x00da
            if (r7 == 0) goto L_0x00a6
            if (r11 != r13) goto L_0x00a6
        L_0x00da:
            java.lang.String r12 = r0.desensitizationFormat
            int r14 = r10 + 1
            java.lang.String r12 = r12.substring(r10, r14)
            r9.append(r12)
            if (r11 != r13) goto L_0x00a6
            r7 = 0
            r13 = r17
            goto L_0x0108
        L_0x00eb:
            int r12 = r17.length()
            if (r8 >= r12) goto L_0x00ff
            int r12 = r8 + 1
            r13 = r17
            java.lang.String r12 = r13.substring(r8, r12)
            r9.append(r12)
            int r8 = r8 + 1
            goto L_0x0108
        L_0x00ff:
            r13 = r17
            if (r7 == 0) goto L_0x0108
            java.lang.String r12 = " "
            r9.append(r12)
        L_0x0108:
            int r10 = r10 + 1
            goto L_0x007a
        L_0x010c:
            r13 = r17
            if (r1 == 0) goto L_0x0120
            if (r3 != 0) goto L_0x0120
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r1
            java.lang.String r10 = "%s "
            java.lang.String r5 = java.lang.String.format(r10, r5)
            r9.insert(r4, r5)
            goto L_0x0127
        L_0x0120:
            if (r2 == 0) goto L_0x0127
            if (r6 != 0) goto L_0x0127
            r9.insert(r4, r2)
        L_0x0127:
            java.lang.String r4 = r9.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.phoneformat.PhoneRule.desensitization(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    private String handleDesensitizationSpecial(String str, int type) {
        if (str == null) {
            return "";
        }
        StringBuilder res = new StringBuilder(str.length());
        if (str.length() == 0) {
            return "";
        }
        if (str.length() == 1) {
            return str;
        }
        if (str.length() == 2) {
            if (type == 1) {
                return "#*";
            }
            return type == 2 ? "*#" : str;
        } else if (str.length() == 3) {
            if (type == 1) {
                return "#**";
            }
            return type == 2 ? "**#" : "#*#";
        } else if (str.length() == 4) {
            if (type == 1) {
                return "#***";
            }
            return type == 2 ? "***#" : "#**#";
        } else if (str.length() == 5) {
            if (type == 1) {
                return "##***";
            }
            return type == 2 ? "***##" : "#***#";
        } else if (str.length() == 6) {
            if (type == 1) {
                return "##****";
            }
            return type == 2 ? "****##" : "#****#";
        } else if (str.length() != 7) {
            if (type == 1) {
                res.append("####");
                for (int i = 4; i < str.length(); i++) {
                    res.append("*");
                }
            } else if (type == 2) {
                for (int i2 = 0; i2 < str.length() - 4; i2++) {
                    res.append("*");
                }
                res.append("####");
            } else {
                res.append("##");
                for (int i3 = 2; i3 < str.length() - 2; i3++) {
                    res.append("*");
                }
            }
            return res.toString();
        } else if (type == 1) {
            return "###****";
        } else {
            return type == 2 ? "****###" : "##****#";
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasIntlPrefix() {
        return (this.flag12 & 2) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean hasTrunkPrefix() {
        return (this.flag12 & 1) != 0;
    }
}
