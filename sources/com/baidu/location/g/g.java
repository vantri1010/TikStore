package com.baidu.location.g;

class g implements Runnable {
    final /* synthetic */ String a;
    final /* synthetic */ boolean b;
    final /* synthetic */ e c;

    g(e eVar, String str, boolean z) {
        this.c = eVar;
        this.a = str;
        this.b = z;
    }

    /* JADX WARNING: type inference failed for: r0v32, types: [java.net.URLConnection] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01af A[SYNTHETIC, Splitter:B:104:0x01af] */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01bb A[SYNTHETIC, Splitter:B:109:0x01bb] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01d3 A[SYNTHETIC, Splitter:B:119:0x01d3] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x01df A[SYNTHETIC, Splitter:B:124:0x01df] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x01eb A[SYNTHETIC, Splitter:B:129:0x01eb] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x01f9 A[LOOP:0: B:1:0x001e->B:135:0x01f9, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0207 A[SYNTHETIC, Splitter:B:141:0x0207] */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0213 A[SYNTHETIC, Splitter:B:146:0x0213] */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x021f A[SYNTHETIC, Splitter:B:151:0x021f] */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x022a A[EDGE_INSN: B:160:0x022a->B:156:0x022a ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01a3 A[SYNTHETIC, Splitter:B:99:0x01a3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "gzip"
            java.lang.String r3 = "close baos IOException!"
            java.lang.String r4 = "close is IOException!"
            java.lang.String r5 = "close os IOException!"
            com.baidu.location.g.e r0 = r1.c
            java.lang.String r6 = com.baidu.location.g.k.e()
            r0.h = r6
            com.baidu.location.g.e r0 = r1.c
            r0.a()
            com.baidu.location.g.e r0 = r1.c
            int r0 = r0.i
            r6 = 0
            r7 = r0
            r8 = r6
        L_0x001e:
            r9 = 0
            r10 = 1
            if (r7 <= 0) goto L_0x022a
            java.net.URL r0 = new java.net.URL     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            com.baidu.location.g.e r11 = r1.c     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.String r11 = r11.h     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            r0.<init>(r11)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.StringBuffer r11 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            r11.<init>()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            com.baidu.location.g.e r12 = r1.c     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.util.Map<java.lang.String, java.lang.Object> r12 = r12.k     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.util.Set r12 = r12.entrySet()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.util.Iterator r12 = r12.iterator()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
        L_0x003c:
            boolean r13 = r12.hasNext()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            if (r13 == 0) goto L_0x0063
            java.lang.Object r13 = r12.next()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.Object r14 = r13.getKey()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            r11.append(r14)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.String r14 = "="
            r11.append(r14)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.Object r13 = r13.getValue()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            r11.append(r13)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.String r13 = "&"
            r11.append(r13)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            goto L_0x003c
        L_0x0063:
            int r12 = r11.length()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            if (r12 <= 0) goto L_0x0071
            int r12 = r11.length()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            int r12 = r12 - r10
            r11.deleteCharAt(r12)     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
        L_0x0071:
            java.net.URLConnection r0 = r0.openConnection()     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            r12 = r0
            java.net.HttpURLConnection r12 = (java.net.HttpURLConnection) r12     // Catch:{ Exception -> 0x01c1, Error -> 0x0191, all -> 0x018b }
            java.lang.String r0 = "POST"
            r12.setRequestMethod(r0)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setDoInput(r10)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setDoOutput(r10)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setUseCaches(r9)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            int r0 = com.baidu.location.g.a.b     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setConnectTimeout(r0)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            int r0 = com.baidu.location.g.a.b     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setReadTimeout(r0)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            java.lang.String r0 = "Content-Type"
            java.lang.String r8 = "application/x-www-form-urlencoded; charset=utf-8"
            r12.setRequestProperty(r0, r8)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            java.lang.String r0 = "Accept-Charset"
            java.lang.String r8 = "UTF-8"
            r12.setRequestProperty(r0, r8)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            java.lang.String r0 = "Accept-Encoding"
            r12.setRequestProperty(r0, r2)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            java.lang.String r0 = com.baidu.location.g.k.ax     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            if (r0 == 0) goto L_0x00ae
            java.lang.String r0 = "bd-loc-android"
            java.lang.String r8 = com.baidu.location.g.k.ax     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setRequestProperty(r0, r8)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
        L_0x00ae:
            java.lang.String r0 = r1.a     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            if (r0 != 0) goto L_0x00bd
            java.lang.String r0 = "Host"
            java.lang.String r8 = r1.a     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            r12.setRequestProperty(r0, r8)     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
        L_0x00bd:
            java.io.OutputStream r8 = r12.getOutputStream()     // Catch:{ Exception -> 0x0185, Error -> 0x017f, all -> 0x0178 }
            java.lang.String r0 = r11.toString()     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            r8.write(r0)     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            r8.flush()     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            int r0 = r12.getResponseCode()     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            r11 = 200(0xc8, float:2.8E-43)
            if (r0 != r11) goto L_0x0139
            java.io.InputStream r11 = r12.getInputStream()     // Catch:{ Exception -> 0x0173, Error -> 0x016e, all -> 0x0168 }
            java.lang.String r0 = r12.getContentEncoding()     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            if (r0 == 0) goto L_0x00f2
            boolean r0 = r0.contains(r2)     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            if (r0 == 0) goto L_0x00f2
            java.util.zip.GZIPInputStream r0 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            java.io.BufferedInputStream r13 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            r13.<init>(r11)     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            r0.<init>(r13)     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            r11 = r0
        L_0x00f2:
            java.io.ByteArrayOutputStream r13 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            r13.<init>()     // Catch:{ Exception -> 0x0136, Error -> 0x0133, all -> 0x012f }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
        L_0x00fb:
            int r14 = r11.read(r0)     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            r15 = -1
            if (r14 == r15) goto L_0x0106
            r13.write(r0, r9, r14)     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            goto L_0x00fb
        L_0x0106:
            com.baidu.location.g.e r0 = r1.c     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            java.lang.String r14 = new java.lang.String     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            byte[] r15 = r13.toByteArray()     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            java.lang.String r9 = "utf-8"
            r14.<init>(r15, r9)     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            r0.j = r14     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            boolean r0 = r1.b     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            if (r0 == 0) goto L_0x0121
            com.baidu.location.g.e r0 = r1.c     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            byte[] r9 = r13.toByteArray()     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            r0.m = r9     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
        L_0x0121:
            com.baidu.location.g.e r0 = r1.c     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            r0.a((boolean) r10)     // Catch:{ Exception -> 0x012d, Error -> 0x012b, all -> 0x0128 }
            r9 = 1
            goto L_0x013c
        L_0x0128:
            r0 = move-exception
            r2 = r0
            goto L_0x016c
        L_0x012b:
            r0 = move-exception
            goto L_0x0171
        L_0x012d:
            r0 = move-exception
            goto L_0x0176
        L_0x012f:
            r0 = move-exception
            r2 = r0
            r13 = r6
            goto L_0x016c
        L_0x0133:
            r0 = move-exception
            r13 = r6
            goto L_0x0171
        L_0x0136:
            r0 = move-exception
            r13 = r6
            goto L_0x0176
        L_0x0139:
            r11 = r6
            r13 = r11
            r9 = 0
        L_0x013c:
            if (r12 == 0) goto L_0x0141
            r12.disconnect()
        L_0x0141:
            if (r8 == 0) goto L_0x014d
            r8.close()     // Catch:{ Exception -> 0x0147 }
            goto L_0x014d
        L_0x0147:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r5)
        L_0x014d:
            if (r11 == 0) goto L_0x0159
            r11.close()     // Catch:{ Exception -> 0x0153 }
            goto L_0x0159
        L_0x0153:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r4)
        L_0x0159:
            if (r13 == 0) goto L_0x0165
            r13.close()     // Catch:{ Exception -> 0x015f }
            goto L_0x0165
        L_0x015f:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r3)
        L_0x0165:
            r8 = r12
            goto L_0x01f6
        L_0x0168:
            r0 = move-exception
            r2 = r0
            r11 = r6
            r13 = r11
        L_0x016c:
            r6 = r8
            goto L_0x017c
        L_0x016e:
            r0 = move-exception
            r11 = r6
            r13 = r11
        L_0x0171:
            r9 = r8
            goto L_0x0183
        L_0x0173:
            r0 = move-exception
            r11 = r6
            r13 = r11
        L_0x0176:
            r9 = r8
            goto L_0x0189
        L_0x0178:
            r0 = move-exception
            r2 = r0
            r11 = r6
            r13 = r11
        L_0x017c:
            r8 = r12
            goto L_0x0200
        L_0x017f:
            r0 = move-exception
            r9 = r6
            r11 = r9
            r13 = r11
        L_0x0183:
            r8 = r12
            goto L_0x0195
        L_0x0185:
            r0 = move-exception
            r9 = r6
            r11 = r9
            r13 = r11
        L_0x0189:
            r8 = r12
            goto L_0x01c5
        L_0x018b:
            r0 = move-exception
            r2 = r0
            r11 = r6
            r13 = r11
            goto L_0x0200
        L_0x0191:
            r0 = move-exception
            r9 = r6
            r11 = r9
            r13 = r11
        L_0x0195:
            java.lang.String r0 = com.baidu.location.g.a.a     // Catch:{ all -> 0x01fd }
            java.lang.String r12 = "NetworkCommunicationError!"
            android.util.Log.d(r0, r12)     // Catch:{ all -> 0x01fd }
            if (r8 == 0) goto L_0x01a1
            r8.disconnect()
        L_0x01a1:
            if (r9 == 0) goto L_0x01ad
            r9.close()     // Catch:{ Exception -> 0x01a7 }
            goto L_0x01ad
        L_0x01a7:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r5)
        L_0x01ad:
            if (r11 == 0) goto L_0x01b9
            r11.close()     // Catch:{ Exception -> 0x01b3 }
            goto L_0x01b9
        L_0x01b3:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r4)
        L_0x01b9:
            if (r13 == 0) goto L_0x01f5
            r13.close()     // Catch:{ Exception -> 0x01bf }
            goto L_0x01f5
        L_0x01bf:
            r0 = move-exception
            goto L_0x01f0
        L_0x01c1:
            r0 = move-exception
            r9 = r6
            r11 = r9
            r13 = r11
        L_0x01c5:
            java.lang.String r0 = com.baidu.location.g.a.a     // Catch:{ all -> 0x01fd }
            java.lang.String r12 = "NetworkCommunicationException!"
            android.util.Log.d(r0, r12)     // Catch:{ all -> 0x01fd }
            if (r8 == 0) goto L_0x01d1
            r8.disconnect()
        L_0x01d1:
            if (r9 == 0) goto L_0x01dd
            r9.close()     // Catch:{ Exception -> 0x01d7 }
            goto L_0x01dd
        L_0x01d7:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r5)
        L_0x01dd:
            if (r11 == 0) goto L_0x01e9
            r11.close()     // Catch:{ Exception -> 0x01e3 }
            goto L_0x01e9
        L_0x01e3:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r4)
        L_0x01e9:
            if (r13 == 0) goto L_0x01f5
            r13.close()     // Catch:{ Exception -> 0x01ef }
            goto L_0x01f5
        L_0x01ef:
            r0 = move-exception
        L_0x01f0:
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r3)
        L_0x01f5:
            r9 = 0
        L_0x01f6:
            if (r9 == 0) goto L_0x01f9
            goto L_0x022a
        L_0x01f9:
            int r7 = r7 + -1
            goto L_0x001e
        L_0x01fd:
            r0 = move-exception
            r2 = r0
            r6 = r9
        L_0x0200:
            if (r8 == 0) goto L_0x0205
            r8.disconnect()
        L_0x0205:
            if (r6 == 0) goto L_0x0211
            r6.close()     // Catch:{ Exception -> 0x020b }
            goto L_0x0211
        L_0x020b:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r5)
        L_0x0211:
            if (r11 == 0) goto L_0x021d
            r11.close()     // Catch:{ Exception -> 0x0217 }
            goto L_0x021d
        L_0x0217:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r4)
        L_0x021d:
            if (r13 == 0) goto L_0x0229
            r13.close()     // Catch:{ Exception -> 0x0223 }
            goto L_0x0229
        L_0x0223:
            r0 = move-exception
            java.lang.String r0 = com.baidu.location.g.a.a
            android.util.Log.d(r0, r3)
        L_0x0229:
            throw r2
        L_0x022a:
            if (r7 > 0) goto L_0x023c
            int r0 = com.baidu.location.g.e.p
            int r0 = r0 + r10
            com.baidu.location.g.e.p = r0
            com.baidu.location.g.e r0 = r1.c
            r0.j = r6
            com.baidu.location.g.e r0 = r1.c
            r2 = 0
            r0.a((boolean) r2)
            goto L_0x023f
        L_0x023c:
            r2 = 0
            com.baidu.location.g.e.p = r2
        L_0x023f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.g.run():void");
    }
}
