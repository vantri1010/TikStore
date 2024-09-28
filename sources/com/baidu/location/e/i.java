package com.baidu.location.e;

import android.net.wifi.ScanResult;
import android.text.TextUtils;
import com.baidu.location.g.k;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.king.zxing.util.LogUtils;
import java.util.List;
import java.util.regex.Pattern;

public class i {
    public List<ScanResult> a = null;
    private long b = 0;
    private long c = 0;
    private boolean d = false;
    private boolean e;

    public i(List<ScanResult> list, long j) {
        this.b = j;
        this.a = list;
        this.c = System.currentTimeMillis();
        try {
            n();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return Pattern.compile("wpa|wep", 2).matcher(str).find();
    }

    private String b(String str) {
        return str != null ? (str.contains("&") || str.contains(";")) ? str.replace("&", "_").replace(";", "_") : str : str;
    }

    private int m() {
        List<ScanResult> list = this.a;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    private void n() {
        if (m() >= 1) {
            int size = this.a.size() - 1;
            boolean z = true;
            while (size >= 1 && z) {
                boolean z2 = false;
                for (int i = 0; i < size; i++) {
                    if (this.a.get(i) != null) {
                        int i2 = i + 1;
                        if (this.a.get(i2) != null && this.a.get(i).level < this.a.get(i2).level) {
                            List<ScanResult> list = this.a;
                            list.set(i2, list.get(i));
                            this.a.set(i, this.a.get(i2));
                            z2 = true;
                        }
                    }
                }
                size--;
                z = z2;
            }
        }
    }

    public int a() {
        List<ScanResult> list = this.a;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String a(int i) {
        return a(i, false, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0319, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0060, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c5, code lost:
        r26 = r4;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01fe A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x021c A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0316 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0318 A[ExcHandler: Error (e java.lang.Error), Splitter:B:35:0x0079] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0056 A[SYNTHETIC, Splitter:B:18:0x0056] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005f A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:18:0x0056] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0096 A[SYNTHETIC, Splitter:B:43:0x0096] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00d4 A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x00df A[SYNTHETIC, Splitter:B:66:0x00df] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0102 A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x010a A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0119 A[Catch:{ Error -> 0x0318, Exception -> 0x005f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String a(int r30, boolean r31, boolean r32) {
        /*
            r29 = this;
            r1 = r29
            int r0 = r29.a()
            r2 = 0
            r3 = 1
            if (r0 >= r3) goto L_0x000b
            return r2
        L_0x000b:
            java.util.Random r4 = new java.util.Random     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            r4.<init>()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            java.lang.StringBuffer r5 = new java.lang.StringBuffer     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            r0 = 512(0x200, float:7.175E-43)
            r5.<init>(r0)     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            r6.<init>()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            android.net.wifi.WifiInfo r0 = r0.l()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            java.lang.String r7 = ":"
            java.lang.String r9 = ""
            if (r0 == 0) goto L_0x004b
            java.lang.String r10 = r0.getBSSID()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            if (r10 == 0) goto L_0x004b
            java.lang.String r10 = r0.getBSSID()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            java.lang.String r10 = r10.replace(r7, r9)     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            int r0 = r0.getRssi()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            com.baidu.location.e.j r11 = com.baidu.location.e.j.a()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            java.lang.String r11 = r11.n()     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            if (r0 >= 0) goto L_0x0047
            int r0 = -r0
        L_0x0047:
            r12 = r11
            r11 = r10
            r10 = r0
            goto L_0x004e
        L_0x004b:
            r11 = r2
            r12 = r11
            r10 = -1
        L_0x004e:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Error -> 0x031d, Exception -> 0x031b }
            r13 = 17
            r14 = 0
            if (r0 < r13) goto L_0x006c
            long r16 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x0063, Exception -> 0x005f }
            r18 = 1000(0x3e8, double:4.94E-321)
            long r16 = r16 / r18
            goto L_0x0066
        L_0x005f:
            r0 = move-exception
            r2 = 0
            goto L_0x031c
        L_0x0063:
            r0 = move-exception
            r16 = r14
        L_0x0066:
            int r0 = (r16 > r14 ? 1 : (r16 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x006e
            r0 = 1
            goto L_0x006f
        L_0x006c:
            r16 = r14
        L_0x006e:
            r0 = 0
        L_0x006f:
            if (r0 == 0) goto L_0x0078
            if (r0 == 0) goto L_0x0077
            if (r31 == 0) goto L_0x0077
            r0 = 1
            goto L_0x0078
        L_0x0077:
            r0 = 0
        L_0x0078:
            r13 = r0
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            int r0 = r0.size()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r14 = r30
            if (r0 <= r14) goto L_0x0084
            goto L_0x0085
        L_0x0084:
            r14 = r0
        L_0x0085:
            r8 = 0
            r15 = 0
            r20 = 0
            r22 = 1
            r23 = 0
            r24 = 0
            r25 = 0
        L_0x0091:
            java.lang.String r2 = "|"
            if (r8 >= r14) goto L_0x021a
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 == 0) goto L_0x0207
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            int r0 = r0.level     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 != 0) goto L_0x00ac
            goto L_0x0207
        L_0x00ac:
            if (r13 == 0) goto L_0x00d7
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Exception -> 0x00c4, Error -> 0x0318 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x00c4, Error -> 0x0318 }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Exception -> 0x00c4, Error -> 0x0318 }
            r26 = r4
            long r3 = r0.timestamp     // Catch:{ Exception -> 0x00c2, Error -> 0x0318 }
            long r3 = r16 - r3
            r27 = 1000000(0xf4240, double:4.940656E-318)
            long r3 = r3 / r27
            goto L_0x00c9
        L_0x00c2:
            r0 = move-exception
            goto L_0x00c7
        L_0x00c4:
            r0 = move-exception
            r26 = r4
        L_0x00c7:
            r3 = 0
        L_0x00c9:
            java.lang.Long r0 = java.lang.Long.valueOf(r3)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r6.add(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            int r0 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r0 <= 0) goto L_0x00d9
            r20 = r3
            goto L_0x00d9
        L_0x00d7:
            r26 = r4
        L_0x00d9:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r3 = 23
            if (r0 < r3) goto L_0x0100
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ all -> 0x00fc }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x00fc }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ all -> 0x00fc }
            boolean r0 = r0.is80211mcResponder()     // Catch:{ all -> 0x00fc }
            if (r0 == 0) goto L_0x0100
            if (r15 != 0) goto L_0x00f5
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch:{ all -> 0x00fc }
            r0.<init>()     // Catch:{ all -> 0x00fc }
            r15 = r0
        L_0x00f5:
            r15.append(r8)     // Catch:{ all -> 0x00fc }
            r15.append(r2)     // Catch:{ all -> 0x00fc }
            goto L_0x0100
        L_0x00fc:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x0100:
            if (r22 == 0) goto L_0x010a
            java.lang.String r0 = "&wf="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r22 = 0
            goto L_0x010d
        L_0x010a:
            r5.append(r2)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x010d:
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r0.BSSID     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 == 0) goto L_0x01fe
            java.lang.String r0 = r0.replace(r7, r9)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.util.List<android.net.wifi.ScanResult> r2 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            int r2 = r2.level     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r2 >= 0) goto L_0x012d
            int r2 = -r2
        L_0x012d:
            java.util.Locale r3 = java.util.Locale.CHINA     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r4 = ";%d;"
            r27 = r7
            r31 = r13
            r7 = 1
            java.lang.Object[] r13 = new java.lang.Object[r7]     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r7 = 0
            r13[r7] = r2     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r2 = java.lang.String.format(r3, r4, r13)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r2)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            int r24 = r24 + 1
            if (r11 == 0) goto L_0x0164
            boolean r0 = r11.equals(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 == 0) goto L_0x0164
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r0.capabilities     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            boolean r0 = r1.a((java.lang.String) r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r1.e = r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r23 = r24
            r0 = 1
            goto L_0x0165
        L_0x0164:
            r0 = 0
        L_0x0165:
            if (r0 != 0) goto L_0x01e8
            r0 = 30
            r2 = 2
            r3 = r25
            if (r3 != 0) goto L_0x01ab
            r4 = 10
            r7 = r26
            int r4 = r7.nextInt(r4)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r4 != r2) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r2 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r2 == 0) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r2 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r2 = (android.net.wifi.ScanResult) r2     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r2 = r2.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r2 >= r0) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r0 = r0.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r0 = r1.b((java.lang.String) r0)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            r5.append(r0)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            r25 = 1
            goto L_0x0210
        L_0x01a9:
            r0 = move-exception
            goto L_0x020e
        L_0x01ab:
            r7 = r26
            r4 = 1
            if (r3 != r4) goto L_0x020e
            r13 = 20
            int r13 = r7.nextInt(r13)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r13 != r4) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r4 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r4 = r4.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r4 = (android.net.wifi.ScanResult) r4     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r4 = r4.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r4 == 0) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r4 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r4 = r4.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r4 = (android.net.wifi.ScanResult) r4     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r4 = r4.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            if (r4 >= r0) goto L_0x020e
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r0 = r0.SSID     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            java.lang.String r0 = r1.b((java.lang.String) r0)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            r5.append(r0)     // Catch:{ Exception -> 0x01a9, Error -> 0x0318 }
            r25 = 2
            goto L_0x0210
        L_0x01e8:
            r3 = r25
            r7 = r26
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r0.SSID     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r1.b((java.lang.String) r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            goto L_0x020e
        L_0x01fe:
            r27 = r7
            r31 = r13
            r3 = r25
            r7 = r26
            goto L_0x0210
        L_0x0207:
            r27 = r7
            r31 = r13
            r3 = r25
            r7 = r4
        L_0x020e:
            r25 = r3
        L_0x0210:
            int r8 = r8 + 1
            r13 = r31
            r4 = r7
            r7 = r27
            r3 = 1
            goto L_0x0091
        L_0x021a:
            if (r22 != 0) goto L_0x0316
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r0.<init>()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r3 = "&wf_n="
            r0.append(r3)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r3 = r23
            r0.append(r3)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r0.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r15 == 0) goto L_0x0240
            java.lang.String r0 = "&wf_mc="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r15.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x0240:
            if (r11 == 0) goto L_0x0259
            r4 = -1
            if (r10 == r4) goto L_0x0259
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r0.<init>()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r4 = "&wf_rs="
            r0.append(r4)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r0.append(r10)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = r0.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x0259:
            r7 = 10
            int r0 = (r20 > r7 ? 1 : (r20 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x02d4
            int r0 = r6.size()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 <= 0) goto L_0x02d4
            r4 = 0
            java.lang.Object r0 = r6.get(r4)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Long r0 = (java.lang.Long) r0     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r7 = r0.longValue()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r10 = 0
            int r0 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x02d4
            java.lang.StringBuffer r0 = new java.lang.StringBuffer     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r4 = 128(0x80, float:1.794E-43)
            r0.<init>(r4)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r4 = "&wf_ut="
            r0.append(r4)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r4 = 0
            java.lang.Object r7 = r6.get(r4)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Long r7 = (java.lang.Long) r7     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r8 = 1
        L_0x028e:
            boolean r10 = r6.hasNext()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r10 == 0) goto L_0x02cc
            java.lang.Object r10 = r6.next()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.Long r10 = (java.lang.Long) r10     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r8 == 0) goto L_0x02a7
            long r10 = r10.longValue()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r0.append(r10)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r8 = 0
            r13 = 0
            goto L_0x02c8
        L_0x02a7:
            long r10 = r10.longValue()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r13 = r7.longValue()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r10 = r10 - r13
            r13 = 0
            int r15 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x02c8
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r15.<init>()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r15.append(r9)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r15.append(r10)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r10 = r15.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r0.append(r10)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x02c8:
            r0.append(r2)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            goto L_0x028e
        L_0x02cc:
            java.lang.String r0 = r0.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            goto L_0x02d5
        L_0x02d4:
            r4 = 0
        L_0x02d5:
            java.lang.String r0 = "&wf_st="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r6 = r1.b     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r6)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = "&wf_et="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r6 = r1.c     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r6)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = "&wf_vt="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            long r6 = com.baidu.location.e.j.a     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r6)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r3 <= 0) goto L_0x0307
            r2 = 1
            r1.d = r2     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            java.lang.String r0 = "&wf_en="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            boolean r0 = r1.e     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            if (r0 == 0) goto L_0x0303
            r3 = 1
            goto L_0x0304
        L_0x0303:
            r3 = 0
        L_0x0304:
            r5.append(r3)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x0307:
            if (r12 == 0) goto L_0x0311
            java.lang.String r0 = "&wf_gw="
            r5.append(r0)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            r5.append(r12)     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
        L_0x0311:
            java.lang.String r0 = r5.toString()     // Catch:{ Error -> 0x0318, Exception -> 0x005f }
            return r0
        L_0x0316:
            r2 = 0
            return r2
        L_0x0318:
            r0 = move-exception
            r2 = 0
            goto L_0x031e
        L_0x031b:
            r0 = move-exception
        L_0x031c:
            return r2
        L_0x031d:
            r0 = move-exception
        L_0x031e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.i.a(int, boolean, boolean):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0089 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(long r20) {
        /*
            r19 = this;
            r1 = r19
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 1
            r3 = 1000(0x3e8, double:4.94E-321)
            r5 = 0
            r6 = 0
            r8 = 17
            if (r0 < r8) goto L_0x001e
            long r8 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x0016, Exception -> 0x0014 }
            long r8 = r8 / r3
            goto L_0x0018
        L_0x0014:
            r0 = move-exception
            goto L_0x0017
        L_0x0016:
            r0 = move-exception
        L_0x0017:
            r8 = r6
        L_0x0018:
            int r0 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x001f
            r10 = 1
            goto L_0x0020
        L_0x001e:
            r8 = r6
        L_0x001f:
            r10 = 0
        L_0x0020:
            if (r10 != 0) goto L_0x0023
            return r5
        L_0x0023:
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            if (r0 == 0) goto L_0x0089
            int r0 = r0.size()
            if (r0 != 0) goto L_0x002e
            goto L_0x0089
        L_0x002e:
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            int r0 = r0.size()
            r11 = 16
            if (r0 <= r11) goto L_0x0039
            goto L_0x003a
        L_0x0039:
            r11 = r0
        L_0x003a:
            r13 = r6
            r15 = r13
            r12 = 0
        L_0x003d:
            if (r12 >= r11) goto L_0x0078
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            java.lang.Object r0 = r0.get(r12)
            if (r0 == 0) goto L_0x0073
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            java.lang.Object r0 = r0.get(r12)
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0
            int r0 = r0.level
            if (r0 != 0) goto L_0x0054
            goto L_0x0073
        L_0x0054:
            if (r10 == 0) goto L_0x0073
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Exception -> 0x006a, Error -> 0x0068 }
            java.lang.Object r0 = r0.get(r12)     // Catch:{ Exception -> 0x006a, Error -> 0x0068 }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Exception -> 0x006a, Error -> 0x0068 }
            long r6 = r0.timestamp     // Catch:{ Exception -> 0x006a, Error -> 0x0068 }
            long r6 = r8 - r6
            r17 = 1000000(0xf4240, double:4.940656E-318)
            long r6 = r6 / r17
            goto L_0x006d
        L_0x0068:
            r0 = move-exception
            goto L_0x006b
        L_0x006a:
            r0 = move-exception
        L_0x006b:
            r6 = 0
        L_0x006d:
            long r13 = r13 + r6
            int r0 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
            if (r0 <= 0) goto L_0x0073
            r15 = r6
        L_0x0073:
            int r12 = r12 + 1
            r6 = 0
            goto L_0x003d
        L_0x0078:
            long r6 = (long) r11
            long r13 = r13 / r6
            long r15 = r15 * r3
            int r0 = (r15 > r20 ? 1 : (r15 == r20 ? 0 : -1))
            if (r0 > 0) goto L_0x0088
            long r13 = r13 * r3
            int r0 = (r13 > r20 ? 1 : (r13 == r20 ? 0 : -1))
            if (r0 <= 0) goto L_0x0087
            goto L_0x0088
        L_0x0087:
            r2 = 0
        L_0x0088:
            return r2
        L_0x0089:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.i.a(long):boolean");
    }

    public boolean a(i iVar) {
        List<ScanResult> list = this.a;
        if (list == null || iVar == null || iVar.a == null) {
            return false;
        }
        int size = (list.size() < iVar.a.size() ? this.a : iVar.a).size();
        for (int i = 0; i < size; i++) {
            if (this.a.get(i) != null) {
                String str = this.a.get(i).BSSID;
                String str2 = iVar.a.get(i).BSSID;
                if (!TextUtils.isEmpty(str) && !str.equals(str2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String b() {
        try {
            return a(k.O, true, true);
        } catch (Exception e2) {
            return null;
        }
    }

    public String b(int i) {
        if (i == 0) {
            return null;
        }
        int i2 = 1;
        if (a() < 1) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer(256);
        int size = this.a.size();
        if (size > k.O) {
            size = k.O;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            if (this.a.get(i4) != null) {
                if (!((i2 & i) == 0 || this.a.get(i4).BSSID == null)) {
                    stringBuffer.append(i3 == 0 ? "&ssid=" : LogUtils.VERTICAL);
                    stringBuffer.append(this.a.get(i4).BSSID.replace(LogUtils.COLON, ""));
                    stringBuffer.append(";");
                    stringBuffer.append(b(this.a.get(i4).SSID));
                    i3++;
                }
                i2 <<= 1;
            }
        }
        return stringBuffer.toString();
    }

    public boolean b(i iVar) {
        List<ScanResult> list = this.a;
        if (list == null || iVar == null || iVar.a == null) {
            return false;
        }
        int size = (list.size() < iVar.a.size() ? this.a : iVar.a).size();
        for (int i = 0; i < size; i++) {
            if (this.a.get(i) != null) {
                String str = this.a.get(i).BSSID;
                int i2 = this.a.get(i).level;
                String str2 = iVar.a.get(i).BSSID;
                int i3 = iVar.a.get(i).level;
                if ((!TextUtils.isEmpty(str) && !str.equals(str2)) || i2 != i3) {
                    return false;
                }
            }
        }
        return true;
    }

    public String c() {
        try {
            return a(k.O, true, false);
        } catch (Exception e2) {
            return null;
        }
    }

    public boolean c(i iVar) {
        return j.a(iVar, this);
    }

    public String d() {
        try {
            return a(15);
        } catch (Exception e2) {
            return null;
        }
    }

    public boolean e() {
        return a((long) k.ag);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x002e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x002f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long f() {
        /*
            r13 = this;
            java.util.List<android.net.wifi.ScanResult> r0 = r13.a
            r1 = 0
            if (r0 == 0) goto L_0x007b
            int r0 = r0.size()
            if (r0 != 0) goto L_0x000e
            goto L_0x007b
        L_0x000e:
            r3 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 17
            r6 = 0
            if (r0 < r5) goto L_0x002a
            long r7 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x0022, Exception -> 0x0020 }
            r9 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r9
            goto L_0x0024
        L_0x0020:
            r0 = move-exception
            goto L_0x0023
        L_0x0022:
            r0 = move-exception
        L_0x0023:
            r7 = r1
        L_0x0024:
            int r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x002b
            r0 = 1
            goto L_0x002c
        L_0x002a:
            r7 = r1
        L_0x002b:
            r0 = 0
        L_0x002c:
            if (r0 != 0) goto L_0x002f
            return r1
        L_0x002f:
            java.util.List<android.net.wifi.ScanResult> r5 = r13.a
            int r5 = r5.size()
            r9 = 16
            if (r5 <= r9) goto L_0x003b
            r5 = 16
        L_0x003b:
            if (r6 >= r5) goto L_0x0071
            java.util.List<android.net.wifi.ScanResult> r9 = r13.a
            java.lang.Object r9 = r9.get(r6)
            if (r9 == 0) goto L_0x006e
            java.util.List<android.net.wifi.ScanResult> r9 = r13.a
            java.lang.Object r9 = r9.get(r6)
            android.net.wifi.ScanResult r9 = (android.net.wifi.ScanResult) r9
            int r9 = r9.level
            if (r9 != 0) goto L_0x0052
            goto L_0x006e
        L_0x0052:
            if (r0 == 0) goto L_0x006e
            java.util.List<android.net.wifi.ScanResult> r9 = r13.a     // Catch:{ Exception -> 0x0067, Error -> 0x0065 }
            java.lang.Object r9 = r9.get(r6)     // Catch:{ Exception -> 0x0067, Error -> 0x0065 }
            android.net.wifi.ScanResult r9 = (android.net.wifi.ScanResult) r9     // Catch:{ Exception -> 0x0067, Error -> 0x0065 }
            long r9 = r9.timestamp     // Catch:{ Exception -> 0x0067, Error -> 0x0065 }
            long r9 = r7 - r9
            r11 = 1000000(0xf4240, double:4.940656E-318)
            long r9 = r9 / r11
            goto L_0x0069
        L_0x0065:
            r9 = move-exception
            goto L_0x0068
        L_0x0067:
            r9 = move-exception
        L_0x0068:
            r9 = r1
        L_0x0069:
            int r11 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r11 >= 0) goto L_0x006e
            r3 = r9
        L_0x006e:
            int r6 = r6 + 1
            goto L_0x003b
        L_0x0071:
            if (r0 == 0) goto L_0x0074
            goto L_0x0075
        L_0x0074:
            r3 = r1
        L_0x0075:
            int r0 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x007a
            goto L_0x007b
        L_0x007a:
            r1 = r3
        L_0x007b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.i.f():long");
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x002e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x002f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long g() {
        /*
            r19 = this;
            r1 = r19
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            r2 = 0
            if (r0 == 0) goto L_0x0085
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0010
            goto L_0x0085
        L_0x0010:
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 17
            r5 = 0
            if (r0 < r4) goto L_0x002a
            long r6 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x0021, Exception -> 0x001f }
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r8
            goto L_0x0023
        L_0x001f:
            r0 = move-exception
            goto L_0x0022
        L_0x0021:
            r0 = move-exception
        L_0x0022:
            r6 = r2
        L_0x0023:
            int r0 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x002b
            r0 = 1
            r4 = 1
            goto L_0x002c
        L_0x002a:
            r6 = r2
        L_0x002b:
            r4 = 0
        L_0x002c:
            if (r4 != 0) goto L_0x002f
            return r2
        L_0x002f:
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            int r0 = r0.size()
            r8 = 16
            if (r0 <= r8) goto L_0x003a
            goto L_0x003b
        L_0x003a:
            r8 = r0
        L_0x003b:
            r9 = r2
            r11 = r9
            r13 = r11
        L_0x003e:
            r15 = 1
            if (r5 >= r8) goto L_0x007c
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            java.lang.Object r0 = r0.get(r5)
            if (r0 == 0) goto L_0x0077
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a
            java.lang.Object r0 = r0.get(r5)
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0
            int r0 = r0.level
            if (r0 != 0) goto L_0x0057
            goto L_0x0077
        L_0x0057:
            if (r4 == 0) goto L_0x0077
            java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ Exception -> 0x006d, Error -> 0x006b }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ Exception -> 0x006d, Error -> 0x006b }
            android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ Exception -> 0x006d, Error -> 0x006b }
            long r2 = r0.timestamp     // Catch:{ Exception -> 0x006d, Error -> 0x006b }
            long r2 = r6 - r2
            r17 = 1000000(0xf4240, double:4.940656E-318)
            long r2 = r2 / r17
            goto L_0x0070
        L_0x006b:
            r0 = move-exception
            goto L_0x006e
        L_0x006d:
            r0 = move-exception
        L_0x006e:
            r2 = 0
        L_0x0070:
            long r13 = r13 + r2
            long r9 = r9 + r15
            int r0 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x0077
            r11 = r2
        L_0x0077:
            int r5 = r5 + 1
            r2 = 0
            goto L_0x003e
        L_0x007c:
            int r0 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r0 <= 0) goto L_0x0084
            long r13 = r13 - r11
            long r9 = r9 - r15
            long r11 = r13 / r9
        L_0x0084:
            return r11
        L_0x0085:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.i.g():long");
    }

    public int h() {
        int i;
        for (int i2 = 0; i2 < a(); i2++) {
            if (this.a.get(i2) != null && (i = -this.a.get(i2).level) > 0) {
                return i;
            }
        }
        return 0;
    }

    public boolean i() {
        return this.d;
    }

    public boolean j() {
        return System.currentTimeMillis() - this.c > 0 && System.currentTimeMillis() - this.c < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }

    public boolean k() {
        return System.currentTimeMillis() - this.c > 0 && System.currentTimeMillis() - this.c < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }

    public boolean l() {
        return System.currentTimeMillis() - this.c > 0 && System.currentTimeMillis() - this.b < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
    }
}
