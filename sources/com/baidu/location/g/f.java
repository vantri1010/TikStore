package com.baidu.location.g;

class f implements Runnable {
    final /* synthetic */ e a;

    f(e eVar) {
        this.a = eVar;
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00e2 A[SYNTHETIC, Splitter:B:61:0x00e2] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00ec A[SYNTHETIC, Splitter:B:66:0x00ec] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00f8 A[LOOP:0: B:1:0x0013->B:72:0x00f8, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0105 A[SYNTHETIC, Splitter:B:78:0x0105] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x010f A[SYNTHETIC, Splitter:B:83:0x010f] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0118 A[EDGE_INSN: B:92:0x0118->B:88:0x0118 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r12 = this;
            com.baidu.location.g.e r0 = r12.a
            java.lang.String r1 = com.baidu.location.g.k.e()
            r0.h = r1
            com.baidu.location.g.e r0 = r12.a
            r0.a()
            com.baidu.location.g.e r0 = r12.a
            int r0 = r0.i
            r1 = 0
            r2 = r1
        L_0x0013:
            r3 = 1
            r4 = 0
            if (r0 <= 0) goto L_0x0118
            java.net.URL r5 = new java.net.URL     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            com.baidu.location.g.e r6 = r12.a     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            java.lang.String r6 = r6.h     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            r5.<init>(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            java.net.URLConnection r5 = r5.openConnection()     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ Exception -> 0x00d1, all -> 0x00ce }
            java.lang.String r2 = "GET"
            r5.setRequestMethod(r2)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setDoInput(r3)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setDoOutput(r3)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setUseCaches(r4)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            int r2 = com.baidu.location.g.a.b     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setConnectTimeout(r2)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            int r2 = com.baidu.location.g.a.b     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setReadTimeout(r2)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            java.lang.String r2 = "Content-Type"
            java.lang.String r6 = "application/x-www-form-urlencoded; charset=utf-8"
            r5.setRequestProperty(r2, r6)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            java.lang.String r2 = "Accept-Charset"
            java.lang.String r6 = "UTF-8"
            r5.setRequestProperty(r2, r6)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            java.lang.String r2 = com.baidu.location.g.k.ax     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            if (r2 == 0) goto L_0x0057
            java.lang.String r2 = "bd-loc-android"
            java.lang.String r6 = com.baidu.location.g.k.ax     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r5.setRequestProperty(r2, r6)     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
        L_0x0057:
            int r2 = r5.getResponseCode()     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r6 = 200(0xc8, float:2.8E-43)
            if (r2 != r6) goto L_0x00a4
            java.io.InputStream r2 = r5.getInputStream()     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            java.io.ByteArrayOutputStream r6 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x009e, all -> 0x009a }
            r6.<init>()     // Catch:{ Exception -> 0x009e, all -> 0x009a }
            r7 = 1024(0x400, float:1.435E-42)
            byte[] r7 = new byte[r7]     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
        L_0x006c:
            int r8 = r2.read(r7)     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r9 = -1
            if (r8 == r9) goto L_0x0077
            r6.write(r7, r4, r8)     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            goto L_0x006c
        L_0x0077:
            r2.close()     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r6.close()     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            com.baidu.location.g.e r7 = r12.a     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            java.lang.String r8 = new java.lang.String     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            byte[] r9 = r6.toByteArray()     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            java.lang.String r10 = "utf-8"
            r8.<init>(r9, r10)     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r7.j = r8     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            com.baidu.location.g.e r7 = r12.a     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r7.a((boolean) r3)     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r5.disconnect()     // Catch:{ Exception -> 0x0098, all -> 0x0096 }
            r7 = 1
            goto L_0x00aa
        L_0x0096:
            r0 = move-exception
            goto L_0x009c
        L_0x0098:
            r7 = move-exception
            goto L_0x00a0
        L_0x009a:
            r0 = move-exception
            r6 = r1
        L_0x009c:
            r1 = r2
            goto L_0x00c7
        L_0x009e:
            r6 = move-exception
            r6 = r1
        L_0x00a0:
            r11 = r5
            r5 = r2
            r2 = r11
            goto L_0x00d4
        L_0x00a4:
            r5.disconnect()     // Catch:{ Exception -> 0x00c9, all -> 0x00c5 }
            r2 = r1
            r6 = r2
            r7 = 0
        L_0x00aa:
            if (r5 == 0) goto L_0x00af
            r5.disconnect()
        L_0x00af:
            if (r2 == 0) goto L_0x00b9
            r2.close()     // Catch:{ Exception -> 0x00b5 }
            goto L_0x00b9
        L_0x00b5:
            r2 = move-exception
            r2.printStackTrace()
        L_0x00b9:
            if (r6 == 0) goto L_0x00c3
            r6.close()     // Catch:{ Exception -> 0x00bf }
            goto L_0x00c3
        L_0x00bf:
            r2 = move-exception
            r2.printStackTrace()
        L_0x00c3:
            r2 = r5
            goto L_0x00f5
        L_0x00c5:
            r0 = move-exception
            r6 = r1
        L_0x00c7:
            r2 = r5
            goto L_0x00fe
        L_0x00c9:
            r2 = move-exception
            r6 = r1
            r2 = r5
            r5 = r6
            goto L_0x00d4
        L_0x00ce:
            r0 = move-exception
            r6 = r1
            goto L_0x00fe
        L_0x00d1:
            r5 = move-exception
            r5 = r1
            r6 = r5
        L_0x00d4:
            java.lang.String r7 = com.baidu.location.g.a.a     // Catch:{ all -> 0x00fc }
            java.lang.String r8 = "NetworkCommunicationException!"
            android.util.Log.d(r7, r8)     // Catch:{ all -> 0x00fc }
            if (r2 == 0) goto L_0x00e0
            r2.disconnect()
        L_0x00e0:
            if (r5 == 0) goto L_0x00ea
            r5.close()     // Catch:{ Exception -> 0x00e6 }
            goto L_0x00ea
        L_0x00e6:
            r5 = move-exception
            r5.printStackTrace()
        L_0x00ea:
            if (r6 == 0) goto L_0x00f4
            r6.close()     // Catch:{ Exception -> 0x00f0 }
            goto L_0x00f4
        L_0x00f0:
            r5 = move-exception
            r5.printStackTrace()
        L_0x00f4:
            r7 = 0
        L_0x00f5:
            if (r7 == 0) goto L_0x00f8
            goto L_0x0118
        L_0x00f8:
            int r0 = r0 + -1
            goto L_0x0013
        L_0x00fc:
            r0 = move-exception
            r1 = r5
        L_0x00fe:
            if (r2 == 0) goto L_0x0103
            r2.disconnect()
        L_0x0103:
            if (r1 == 0) goto L_0x010d
            r1.close()     // Catch:{ Exception -> 0x0109 }
            goto L_0x010d
        L_0x0109:
            r1 = move-exception
            r1.printStackTrace()
        L_0x010d:
            if (r6 == 0) goto L_0x0117
            r6.close()     // Catch:{ Exception -> 0x0113 }
            goto L_0x0117
        L_0x0113:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0117:
            throw r0
        L_0x0118:
            if (r0 > 0) goto L_0x0129
            int r0 = com.baidu.location.g.e.p
            int r0 = r0 + r3
            com.baidu.location.g.e.p = r0
            com.baidu.location.g.e r0 = r12.a
            r0.j = r1
            com.baidu.location.g.e r0 = r12.a
            r0.a((boolean) r4)
            goto L_0x012b
        L_0x0129:
            com.baidu.location.g.e.p = r4
        L_0x012b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.f.run():void");
    }
}
