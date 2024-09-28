package com.baidu.location.e;

import android.os.Build;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.baidu.location.f;
import com.baidu.location.g.k;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.king.zxing.util.LogUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class b {
    public static int a = 0;
    public static int b = 0;
    private static b c = null;
    private static Class<?> k = null;
    private TelephonyManager d = null;
    /* access modifiers changed from: private */
    public a e = new a();
    private a f = null;
    private List<a> g = null;
    private C0008b h = null;
    private boolean i = false;
    private boolean j = false;
    private a l;
    private long m = 0;
    /* access modifiers changed from: private */
    public Handler n = new Handler();

    private class a extends TelephonyManager.CellInfoCallback {
        private a() {
        }

        public void onCellInfo(List<CellInfo> list) {
            if (list != null) {
                b.this.n.post(new c(this));
            }
        }

        public void onError(int i, Throwable th) {
            if (th != null) {
                th.printStackTrace();
            }
        }
    }

    /* renamed from: com.baidu.location.e.b$b  reason: collision with other inner class name */
    private class C0008b extends PhoneStateListener {
        public C0008b() {
        }

        public void onCellInfoChanged(List<CellInfo> list) {
            if (list != null) {
                b.this.n.post(new d(this));
            }
        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            a c;
            int cdmaDbm;
            if (b.this.e != null) {
                if (b.this.e.i == 'g') {
                    c = b.this.e;
                    cdmaDbm = signalStrength.getGsmSignalStrength();
                } else if (b.this.e.i == 'c') {
                    c = b.this.e;
                    cdmaDbm = signalStrength.getCdmaDbm();
                } else {
                    return;
                }
                c.h = cdmaDbm;
            }
        }
    }

    private b() {
    }

    private int a(int i2) {
        if (i2 == Integer.MAX_VALUE) {
            return -1;
        }
        return i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0140, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 28) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0065, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 28) goto L_0x0067;
     */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0150 A[Catch:{ Exception -> 0x02e6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x019d A[Catch:{ Exception -> 0x02e6 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:57:0x01c5=Splitter:B:57:0x01c5, B:82:0x0235=Splitter:B:82:0x0235, B:63:0x01db=Splitter:B:63:0x01db, B:88:0x024b=Splitter:B:88:0x024b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.baidu.location.e.a a(android.telephony.CellInfo r18) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            int r0 = android.os.Build.VERSION.SDK_INT
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            int r3 = r0.intValue()
            r4 = 0
            r0 = 17
            if (r3 >= r0) goto L_0x0014
            return r4
        L_0x0014:
            com.baidu.location.e.a r5 = new com.baidu.location.e.a
            r5.<init>()
            boolean r0 = r2 instanceof android.telephony.CellInfoGsm
            r6 = 2
            r7 = 0
            r8 = 3
            r9 = 1
            r10 = 103(0x67, float:1.44E-43)
            r11 = 28
            if (r0 == 0) goto L_0x0070
            r0 = r2
            android.telephony.CellInfoGsm r0 = (android.telephony.CellInfoGsm) r0
            android.telephony.CellIdentityGsm r12 = r0.getCellIdentity()
            int r13 = r12.getMcc()
            int r13 = r1.a((int) r13)
            r5.c = r13
            int r13 = r12.getMnc()
            int r13 = r1.a((int) r13)
            r5.d = r13
            int r13 = r12.getLac()
            int r13 = r1.a((int) r13)
            r5.a = r13
            int r12 = r12.getCid()
            int r12 = r1.a((int) r12)
            long r12 = (long) r12
            r5.b = r12
            r5.i = r10
            android.telephony.CellSignalStrengthGsm r0 = r0.getCellSignalStrength()
            int r0 = r0.getAsuLevel()
            r5.h = r0
            r5.k = r6
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r11) goto L_0x006d
        L_0x0067:
            int r0 = r18.getCellConnectionStatus()
            r5.j = r0
        L_0x006d:
            r0 = 1
            goto L_0x0145
        L_0x0070:
            boolean r0 = r2 instanceof android.telephony.CellInfoCdma
            if (r0 == 0) goto L_0x00fc
            r0 = r2
            android.telephony.CellInfoCdma r0 = (android.telephony.CellInfoCdma) r0
            android.telephony.CellIdentityCdma r12 = r0.getCellIdentity()
            int r13 = r12.getLatitude()
            r5.e = r13
            int r13 = r12.getLongitude()
            r5.f = r13
            int r13 = r12.getSystemId()
            int r13 = r1.a((int) r13)
            r5.d = r13
            int r13 = r12.getNetworkId()
            int r13 = r1.a((int) r13)
            r5.a = r13
            int r12 = r12.getBasestationId()
            int r12 = r1.a((int) r12)
            long r12 = (long) r12
            r5.b = r12
            r12 = 99
            r5.i = r12
            android.telephony.CellSignalStrengthCdma r0 = r0.getCellSignalStrength()
            int r0 = r0.getCdmaDbm()
            r5.h = r0
            r5.k = r9
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r11) goto L_0x00c0
            int r0 = r18.getCellConnectionStatus()
            r5.j = r0
        L_0x00c0:
            com.baidu.location.e.a r0 = r1.e
            if (r0 == 0) goto L_0x00cf
            int r0 = r0.c
            if (r0 <= 0) goto L_0x00cf
            com.baidu.location.e.a r0 = r1.e
            int r0 = r0.c
            r5.c = r0
            goto L_0x006d
        L_0x00cf:
            r12 = -1
            android.telephony.TelephonyManager r0 = r1.d     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r0 = r0.getNetworkOperator()     // Catch:{ Exception -> 0x00f5 }
            if (r0 == 0) goto L_0x00f6
            int r13 = r0.length()     // Catch:{ Exception -> 0x00f5 }
            if (r13 <= 0) goto L_0x00f6
            int r13 = r0.length()     // Catch:{ Exception -> 0x00f5 }
            if (r13 < r8) goto L_0x00f6
            java.lang.String r0 = r0.substring(r7, r8)     // Catch:{ Exception -> 0x00f5 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00f5 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x00f5 }
            if (r0 >= 0) goto L_0x00f3
            goto L_0x00f6
        L_0x00f3:
            r12 = r0
            goto L_0x00f6
        L_0x00f5:
            r0 = move-exception
        L_0x00f6:
            if (r12 <= 0) goto L_0x006d
            r5.c = r12
            goto L_0x006d
        L_0x00fc:
            boolean r0 = r2 instanceof android.telephony.CellInfoLte
            if (r0 == 0) goto L_0x0144
            r0 = r2
            android.telephony.CellInfoLte r0 = (android.telephony.CellInfoLte) r0
            android.telephony.CellIdentityLte r12 = r0.getCellIdentity()
            int r13 = r12.getMcc()
            int r13 = r1.a((int) r13)
            r5.c = r13
            int r13 = r12.getMnc()
            int r13 = r1.a((int) r13)
            r5.d = r13
            int r13 = r12.getTac()
            int r13 = r1.a((int) r13)
            r5.a = r13
            int r12 = r12.getCi()
            int r12 = r1.a((int) r12)
            long r12 = (long) r12
            r5.b = r12
            r5.i = r10
            android.telephony.CellSignalStrengthLte r0 = r0.getCellSignalStrength()
            int r0 = r0.getAsuLevel()
            r5.h = r0
            r5.k = r8
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r11) goto L_0x006d
            goto L_0x0067
        L_0x0144:
            r0 = 0
        L_0x0145:
            r12 = 18
            if (r3 < r12) goto L_0x02e7
            if (r0 != 0) goto L_0x02e7
            boolean r0 = r2 instanceof android.telephony.CellInfoWcdma     // Catch:{ Exception -> 0x02e6 }
            r3 = 4
            if (r0 == 0) goto L_0x019d
            r0 = r2
            android.telephony.CellInfoWcdma r0 = (android.telephony.CellInfoWcdma) r0     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellIdentityWcdma r0 = r0.getCellIdentity()     // Catch:{ Exception -> 0x02e6 }
            int r4 = r0.getMcc()     // Catch:{ Exception -> 0x02e6 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02e6 }
            r5.c = r4     // Catch:{ Exception -> 0x02e6 }
            int r4 = r0.getMnc()     // Catch:{ Exception -> 0x02e6 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02e6 }
            r5.d = r4     // Catch:{ Exception -> 0x02e6 }
            int r4 = r0.getLac()     // Catch:{ Exception -> 0x02e6 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02e6 }
            r5.a = r4     // Catch:{ Exception -> 0x02e6 }
            int r0 = r0.getCid()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02e6 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x02e6 }
            r5.b = r6     // Catch:{ Exception -> 0x02e6 }
            r5.i = r10     // Catch:{ Exception -> 0x02e6 }
            r0 = r2
            android.telephony.CellInfoWcdma r0 = (android.telephony.CellInfoWcdma) r0     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellSignalStrengthWcdma r0 = r0.getCellSignalStrength()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r0.getAsuLevel()     // Catch:{ Exception -> 0x02e6 }
            r5.h = r0     // Catch:{ Exception -> 0x02e6 }
            r5.k = r3     // Catch:{ Exception -> 0x02e6 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02e6 }
            if (r0 < r11) goto L_0x02e7
            int r0 = r18.getCellConnectionStatus()     // Catch:{ Exception -> 0x02e6 }
        L_0x0199:
            r5.j = r0     // Catch:{ Exception -> 0x02e6 }
            goto L_0x02e7
        L_0x019d:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02e6 }
            r12 = 29
            if (r0 < r12) goto L_0x02e7
            boolean r0 = r2 instanceof android.telephony.CellInfoTdscdma     // Catch:{ Exception -> 0x02e6 }
            r12 = 5
            if (r0 == 0) goto L_0x020a
            r0 = r2
            android.telephony.CellInfoTdscdma r0 = (android.telephony.CellInfoTdscdma) r0     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellIdentityTdscdma r3 = r0.getCellIdentity()     // Catch:{ Exception -> 0x02e6 }
            java.lang.String r0 = r3.getMccString()     // Catch:{ Exception -> 0x02e6 }
            if (r0 == 0) goto L_0x01c5
            java.lang.String r0 = r3.getMccString()     // Catch:{ all -> 0x01c4 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x01c4 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01c4 }
            r5.c = r0     // Catch:{ all -> 0x01c4 }
            goto L_0x01c5
        L_0x01c4:
            r0 = move-exception
        L_0x01c5:
            java.lang.String r0 = r3.getMncString()     // Catch:{ Exception -> 0x02e6 }
            if (r0 == 0) goto L_0x01db
            java.lang.String r0 = r3.getMncString()     // Catch:{ all -> 0x01da }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x01da }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01da }
            r5.d = r0     // Catch:{ all -> 0x01da }
            goto L_0x01db
        L_0x01da:
            r0 = move-exception
        L_0x01db:
            int r0 = r3.getLac()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02e6 }
            r5.a = r0     // Catch:{ Exception -> 0x02e6 }
            int r0 = r3.getCid()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02e6 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x02e6 }
            r5.b = r3     // Catch:{ Exception -> 0x02e6 }
            r5.i = r10     // Catch:{ Exception -> 0x02e6 }
            r0 = r2
            android.telephony.CellInfoTdscdma r0 = (android.telephony.CellInfoTdscdma) r0     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellSignalStrengthTdscdma r0 = r0.getCellSignalStrength()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r0.getAsuLevel()     // Catch:{ Exception -> 0x02e6 }
            r5.h = r0     // Catch:{ Exception -> 0x02e6 }
            r5.k = r12     // Catch:{ Exception -> 0x02e6 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02e6 }
            if (r0 < r11) goto L_0x02e7
            int r0 = r18.getCellConnectionStatus()     // Catch:{ Exception -> 0x02e6 }
            goto L_0x0199
        L_0x020a:
            boolean r0 = r2 instanceof android.telephony.CellInfoNr     // Catch:{ Exception -> 0x02e6 }
            if (r0 == 0) goto L_0x02e7
            r0 = r2
            android.telephony.CellInfoNr r0 = (android.telephony.CellInfoNr) r0     // Catch:{ all -> 0x0219 }
            android.telephony.CellIdentity r0 = r0.getCellIdentity()     // Catch:{ all -> 0x0219 }
            android.telephony.CellIdentityNr r0 = (android.telephony.CellIdentityNr) r0     // Catch:{ all -> 0x0219 }
            r4 = r0
            goto L_0x021d
        L_0x0219:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ Exception -> 0x02e6 }
        L_0x021d:
            if (r4 == 0) goto L_0x02e7
            java.lang.String r0 = r4.getMccString()     // Catch:{ Exception -> 0x02e6 }
            if (r0 == 0) goto L_0x0235
            java.lang.String r0 = r4.getMccString()     // Catch:{ all -> 0x0234 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0234 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0234 }
            r5.c = r0     // Catch:{ all -> 0x0234 }
            goto L_0x0235
        L_0x0234:
            r0 = move-exception
        L_0x0235:
            java.lang.String r0 = r4.getMncString()     // Catch:{ Exception -> 0x02e6 }
            if (r0 == 0) goto L_0x024b
            java.lang.String r0 = r4.getMncString()     // Catch:{ all -> 0x024a }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x024a }
            int r0 = r0.intValue()     // Catch:{ all -> 0x024a }
            r5.d = r0     // Catch:{ all -> 0x024a }
            goto L_0x024b
        L_0x024a:
            r0 = move-exception
        L_0x024b:
            int r0 = r4.getTac()     // Catch:{ Exception -> 0x02e6 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02e6 }
            r5.a = r0     // Catch:{ Exception -> 0x02e6 }
            long r13 = r4.getNci()     // Catch:{ Exception -> 0x02e6 }
            r15 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r0 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0268
            long r13 = r4.getNci()     // Catch:{ Exception -> 0x02e6 }
            r5.b = r13     // Catch:{ Exception -> 0x02e6 }
        L_0x0268:
            r5.i = r10     // Catch:{ Exception -> 0x02e6 }
            r0 = 6
            r5.k = r0     // Catch:{ Exception -> 0x02e6 }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02e6 }
            if (r4 < r11) goto L_0x0277
            int r4 = r18.getCellConnectionStatus()     // Catch:{ Exception -> 0x02e6 }
            r5.j = r4     // Catch:{ Exception -> 0x02e6 }
        L_0x0277:
            r4 = r2
            android.telephony.CellInfoNr r4 = (android.telephony.CellInfoNr) r4     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellSignalStrength r4 = r4.getCellSignalStrength()     // Catch:{ Exception -> 0x02e6 }
            android.telephony.CellSignalStrengthNr r4 = (android.telephony.CellSignalStrengthNr) r4     // Catch:{ Exception -> 0x02e6 }
            int r10 = r4.getAsuLevel()     // Catch:{ Exception -> 0x02e6 }
            r5.h = r10     // Catch:{ Exception -> 0x02e6 }
            java.util.Locale r10 = java.util.Locale.US     // Catch:{ Exception -> 0x02e6 }
            java.lang.String r11 = "%d|%d|%d|%d|%d|%d|%d|%d"
            r13 = 8
            java.lang.Object[] r13 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x02e6 }
            int r14 = r4.getCsiRsrp()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x02e6 }
            r13[r7] = r14     // Catch:{ Exception -> 0x02e6 }
            int r7 = r4.getCsiRsrq()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x02e6 }
            r13[r9] = r7     // Catch:{ Exception -> 0x02e6 }
            int r7 = r4.getCsiSinr()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x02e6 }
            r13[r6] = r7     // Catch:{ Exception -> 0x02e6 }
            int r6 = r4.getDbm()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x02e6 }
            r13[r8] = r6     // Catch:{ Exception -> 0x02e6 }
            int r6 = r4.getLevel()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x02e6 }
            r13[r3] = r6     // Catch:{ Exception -> 0x02e6 }
            int r3 = r4.getSsRsrp()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02e6 }
            r13[r12] = r3     // Catch:{ Exception -> 0x02e6 }
            int r3 = r4.getSsRsrq()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02e6 }
            r13[r0] = r3     // Catch:{ Exception -> 0x02e6 }
            r0 = 7
            int r3 = r4.getSsSinr()     // Catch:{ Exception -> 0x02e6 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02e6 }
            r13[r0] = r3     // Catch:{ Exception -> 0x02e6 }
            java.lang.String r0 = java.lang.String.format(r10, r11, r13)     // Catch:{ Exception -> 0x02e6 }
            r5.m = r0     // Catch:{ Exception -> 0x02e6 }
            goto L_0x02e7
        L_0x02e6:
            r0 = move-exception
        L_0x02e7:
            long r3 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x02fc }
            long r6 = r18.getTimeStamp()     // Catch:{ Error -> 0x02fc }
            long r3 = r3 - r6
            r6 = 1000000(0xf4240, double:4.940656E-318)
            long r3 = r3 / r6
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Error -> 0x02fc }
            long r6 = r6 - r3
            r5.g = r6     // Catch:{ Error -> 0x02fc }
            goto L_0x0303
        L_0x02fc:
            r0 = move-exception
            long r2 = java.lang.System.currentTimeMillis()
            r5.g = r2
        L_0x0303:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.a(android.telephony.CellInfo):com.baidu.location.e.a");
    }

    private a a(CellLocation cellLocation) {
        return a(cellLocation, false);
    }

    private a a(CellLocation cellLocation, boolean z) {
        if (cellLocation == null || this.d == null) {
            return null;
        }
        a aVar = new a();
        if (z) {
            aVar.f();
        }
        aVar.g = System.currentTimeMillis();
        try {
            String networkOperator = this.d.getNetworkOperator();
            if (networkOperator != null && networkOperator.length() > 0) {
                int i2 = -1;
                if (networkOperator.length() >= 3) {
                    i2 = Integer.valueOf(networkOperator.substring(0, 3)).intValue();
                    aVar.c = i2 < 0 ? this.e.c : i2;
                }
                String substring = networkOperator.substring(3);
                if (substring != null) {
                    char[] charArray = substring.toCharArray();
                    int i3 = 0;
                    while (true) {
                        if (i3 >= charArray.length) {
                            break;
                        } else if (!Character.isDigit(charArray[i3])) {
                            break;
                        } else {
                            i3++;
                        }
                    }
                    i2 = Integer.valueOf(substring.substring(0, i3)).intValue();
                }
                if (i2 < 0) {
                    i2 = this.e.d;
                }
                aVar.d = i2;
            }
            a = this.d.getSimState();
        } catch (Exception e2) {
            b = 1;
        }
        if (cellLocation instanceof GsmCellLocation) {
            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            aVar.a = gsmCellLocation.getLac();
            aVar.b = (long) gsmCellLocation.getCid();
            aVar.i = 'g';
        } else if (cellLocation instanceof CdmaCellLocation) {
            aVar.i = 'c';
            if (k == null) {
                try {
                    k = Class.forName("android.telephony.cdma.CdmaCellLocation");
                } catch (Exception e3) {
                    k = null;
                    return aVar;
                }
            }
            Class<?> cls = k;
            if (cls != null && cls.isInstance(cellLocation)) {
                try {
                    int systemId = ((CdmaCellLocation) cellLocation).getSystemId();
                    if (systemId < 0) {
                        systemId = this.e.d;
                    }
                    aVar.d = systemId;
                    aVar.b = (long) ((CdmaCellLocation) cellLocation).getBaseStationId();
                    aVar.a = ((CdmaCellLocation) cellLocation).getNetworkId();
                    int baseStationLatitude = ((CdmaCellLocation) cellLocation).getBaseStationLatitude();
                    if (baseStationLatitude < Integer.MAX_VALUE) {
                        aVar.e = baseStationLatitude;
                    }
                    int baseStationLongitude = ((CdmaCellLocation) cellLocation).getBaseStationLongitude();
                    if (baseStationLongitude < Integer.MAX_VALUE) {
                        aVar.f = baseStationLongitude;
                    }
                } catch (Exception e4) {
                    b = 3;
                    return aVar;
                }
            }
        }
        c(aVar);
        return aVar;
    }

    public static synchronized b a() {
        b bVar;
        synchronized (b.class) {
            if (c == null) {
                c = new b();
            }
            bVar = c;
        }
        return bVar;
    }

    private void c(a aVar) {
        if (aVar.b()) {
            a aVar2 = this.e;
            if (aVar2 == null || !aVar2.a(aVar)) {
                this.e = aVar;
                if (aVar.b()) {
                    int size = this.g.size();
                    a aVar3 = size == 0 ? null : this.g.get(size - 1);
                    if (aVar3 == null || aVar3.b != this.e.b || aVar3.a != this.e.a) {
                        this.g.add(this.e);
                        if (this.g.size() > 3) {
                            this.g.remove(0);
                        }
                        j();
                        this.j = false;
                        return;
                    }
                    return;
                }
                List<a> list = this.g;
                if (list != null) {
                    list.clear();
                }
            }
        }
    }

    private String d(a aVar) {
        String str;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = null;
        if (Integer.valueOf(Build.VERSION.SDK_INT).intValue() >= 17) {
            try {
                List<CellInfo> allCellInfo = this.d.getAllCellInfo();
                if (allCellInfo != null && allCellInfo.size() > 0) {
                    sb.append("&nc=");
                    for (CellInfo next : allCellInfo) {
                        if (!next.isRegistered()) {
                            a a2 = a(next);
                            if (a2 != null) {
                                if (a2.a != -1) {
                                    if (a2.b != -1) {
                                        if (aVar.a != a2.a) {
                                            str = a2.a + LogUtils.VERTICAL + a2.b + LogUtils.VERTICAL + a2.h + ";";
                                        } else {
                                            str = LogUtils.VERTICAL + a2.b + LogUtils.VERTICAL + a2.h + ";";
                                        }
                                        sb.append(str);
                                    }
                                }
                                if (Build.VERSION.SDK_INT > 28 && a2.k == 6 && a2.m != null) {
                                    if (sb2 == null) {
                                        StringBuilder sb3 = new StringBuilder();
                                        try {
                                            sb3.append("&ncnr=");
                                            sb2 = sb3;
                                        } catch (Throwable th) {
                                            sb2 = sb3;
                                        }
                                    }
                                    sb2.append(a2.g());
                                    sb2.append("_");
                                    sb2.append(a2.m);
                                    sb2.append(";");
                                }
                            }
                        }
                    }
                }
            } catch (Throwable th2) {
            }
        }
        if (sb2 == null) {
            return sb.toString();
        }
        return sb.toString() + sb2.toString();
    }

    private void i() {
        String i2 = k.i();
        if (i2 != null) {
            File file = new File(i2 + File.separator + "lcvif2.dat");
            if (file.exists()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    long j2 = 0;
                    randomAccessFile.seek(0);
                    if (System.currentTimeMillis() - randomAccessFile.readLong() > DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
                        randomAccessFile.close();
                        file.delete();
                        return;
                    }
                    randomAccessFile.readInt();
                    int i3 = 0;
                    while (i3 < 3) {
                        long readLong = randomAccessFile.readLong();
                        int readInt = randomAccessFile.readInt();
                        int readInt2 = randomAccessFile.readInt();
                        int readInt3 = randomAccessFile.readInt();
                        long readLong2 = randomAccessFile.readLong();
                        int readInt4 = randomAccessFile.readInt();
                        char c2 = readInt4 == 1 ? 'g' : 0;
                        if (readInt4 == 2) {
                            c2 = 'c';
                        }
                        if (readLong != j2) {
                            a aVar = new a(readInt3, readLong2, readInt, readInt2, 0, c2, -1);
                            aVar.g = readLong;
                            if (aVar.b()) {
                                this.j = true;
                                this.g.add(aVar);
                            }
                        }
                        i3++;
                        j2 = 0;
                    }
                    randomAccessFile.close();
                } catch (Exception e2) {
                    file.delete();
                }
            }
        }
    }

    private void j() {
        if (this.g != null || this.f != null) {
            if (this.g == null && this.f != null) {
                LinkedList linkedList = new LinkedList();
                this.g = linkedList;
                linkedList.add(this.f);
            }
            String i2 = k.i();
            if (i2 != null && this.g != null) {
                File file = new File(i2 + File.separator + "lcvif2.dat");
                int size = this.g.size();
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(0);
                    randomAccessFile.writeLong(this.g.get(size - 1).g);
                    randomAccessFile.writeInt(size);
                    for (int i3 = 0; i3 < 3 - size; i3++) {
                        randomAccessFile.writeLong(0);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeInt(-1);
                        randomAccessFile.writeLong(-1);
                        randomAccessFile.writeInt(2);
                    }
                    for (int i4 = 0; i4 < size; i4++) {
                        randomAccessFile.writeLong(this.g.get(i4).g);
                        randomAccessFile.writeInt(this.g.get(i4).c);
                        randomAccessFile.writeInt(this.g.get(i4).d);
                        randomAccessFile.writeInt(this.g.get(i4).a);
                        randomAccessFile.writeLong(this.g.get(i4).b);
                        if (this.g.get(i4).i == 'g') {
                            randomAccessFile.writeInt(1);
                        } else if (this.g.get(i4).i == 'c') {
                            randomAccessFile.writeInt(2);
                        } else {
                            randomAccessFile.writeInt(3);
                        }
                    }
                    randomAccessFile.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void k() {
        CellLocation cellLocation;
        a l2 = l();
        if (l2 != null) {
            c(l2);
        }
        if (Build.VERSION.SDK_INT > 28) {
            return;
        }
        if (l2 == null || !l2.b()) {
            try {
                cellLocation = this.d.getCellLocation();
            } catch (Throwable th) {
                cellLocation = null;
            }
            if (cellLocation != null) {
                a(cellLocation);
            }
        }
    }

    private a l() {
        if (Integer.valueOf(Build.VERSION.SDK_INT).intValue() < 17) {
            return null;
        }
        try {
            a = this.d.getSimState();
            List<CellInfo> allCellInfo = this.d.getAllCellInfo();
            if (allCellInfo == null || allCellInfo.size() <= 0) {
                return null;
            }
            a aVar = null;
            for (CellInfo next : allCellInfo) {
                if (next.isRegistered()) {
                    boolean z = false;
                    if (aVar != null) {
                        z = true;
                    }
                    a a2 = a(next);
                    if (a2 != null) {
                        if (!a2.b()) {
                            a2 = null;
                        } else if (z && aVar != null) {
                            aVar.l = a2.i();
                            return aVar;
                        }
                        if (aVar == null) {
                            aVar = a2;
                        }
                    }
                }
            }
            return aVar;
        } catch (Throwable th) {
            return null;
        }
    }

    public String a(a aVar) {
        String str = "";
        try {
            String d2 = d(aVar);
            int intValue = Integer.valueOf(Build.VERSION.SDK_INT).intValue();
            if ((d2 != null && !d2.equals(str) && !d2.equals("&nc=")) || intValue >= 17) {
                return d2;
            }
            str = d2;
            if (str == null || !str.equals("&nc=")) {
                return str;
            }
            return null;
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public String b(a aVar) {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&nw=");
        stringBuffer.append(aVar.i);
        stringBuffer.append(String.format(Locale.CHINA, "&cl=%d|%d|%d|%d&cl_s=%d", new Object[]{Integer.valueOf(aVar.c), Integer.valueOf(aVar.d), Integer.valueOf(aVar.a), Long.valueOf(aVar.b), Integer.valueOf(aVar.h)}));
        if (aVar.e < Integer.MAX_VALUE && aVar.f < Integer.MAX_VALUE) {
            stringBuffer.append(String.format(Locale.CHINA, "&cdmall=%.6f|%.6f", new Object[]{Double.valueOf(((double) aVar.f) / 14400.0d), Double.valueOf(((double) aVar.e) / 14400.0d)}));
        }
        stringBuffer.append("&cl_t=");
        stringBuffer.append(aVar.g);
        stringBuffer.append("&clp=");
        stringBuffer.append(aVar.k);
        if (aVar.m != null) {
            stringBuffer.append("&clnrs=");
            stringBuffer.append(aVar.m);
        }
        if (Build.VERSION.SDK_INT >= 28 && aVar.j != Integer.MAX_VALUE) {
            stringBuffer.append("&cl_cs=");
            stringBuffer.append(aVar.j);
        }
        try {
            if (this.g != null && this.g.size() > 0) {
                int size = this.g.size();
                stringBuffer.append("&clt=");
                for (int i2 = 0; i2 < size; i2++) {
                    a aVar2 = this.g.get(i2);
                    if (aVar2 != null) {
                        if (aVar2.c != aVar.c) {
                            stringBuffer.append(aVar2.c);
                        }
                        stringBuffer.append(LogUtils.VERTICAL);
                        if (aVar2.d != aVar.d) {
                            stringBuffer.append(aVar2.d);
                        }
                        stringBuffer.append(LogUtils.VERTICAL);
                        if (aVar2.a != aVar.a) {
                            stringBuffer.append(aVar2.a);
                        }
                        stringBuffer.append(LogUtils.VERTICAL);
                        if (aVar2.b != aVar.b) {
                            stringBuffer.append(aVar2.b);
                        }
                        stringBuffer.append(LogUtils.VERTICAL);
                        stringBuffer.append((System.currentTimeMillis() - aVar2.g) / 1000);
                        stringBuffer.append(";");
                    }
                }
            }
        } catch (Exception e2) {
        }
        if (a > 100) {
            a = 0;
        }
        int i3 = a + (b << 8);
        stringBuffer.append("&cs=" + i3);
        if (aVar.l != null) {
            stringBuffer.append(aVar.l);
        }
        return stringBuffer.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void b() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.i     // Catch:{ all -> 0x004d }
            r1 = 1
            if (r0 != r1) goto L_0x0008
            monitor-exit(r4)
            return
        L_0x0008:
            boolean r0 = com.baidu.location.f.isServing     // Catch:{ all -> 0x004d }
            if (r0 != 0) goto L_0x000e
            monitor-exit(r4)
            return
        L_0x000e:
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ all -> 0x004d }
            java.lang.String r2 = "phone"
            java.lang.Object r0 = r0.getSystemService(r2)     // Catch:{ all -> 0x004d }
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0     // Catch:{ all -> 0x004d }
            r4.d = r0     // Catch:{ all -> 0x004d }
            java.util.LinkedList r0 = new java.util.LinkedList     // Catch:{ all -> 0x004d }
            r0.<init>()     // Catch:{ all -> 0x004d }
            r4.g = r0     // Catch:{ all -> 0x004d }
            com.baidu.location.e.b$b r0 = new com.baidu.location.e.b$b     // Catch:{ all -> 0x004d }
            r0.<init>()     // Catch:{ all -> 0x004d }
            r4.h = r0     // Catch:{ all -> 0x004d }
            r4.i()     // Catch:{ all -> 0x004d }
            android.telephony.TelephonyManager r0 = r4.d     // Catch:{ all -> 0x004d }
            if (r0 == 0) goto L_0x004b
            com.baidu.location.e.b$b r0 = r4.h     // Catch:{ all -> 0x004d }
            if (r0 != 0) goto L_0x0036
            goto L_0x004b
        L_0x0036:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x004d }
            r2 = 29
            if (r0 >= r2) goto L_0x0047
            android.telephony.TelephonyManager r0 = r4.d     // Catch:{ Exception -> 0x0046 }
            com.baidu.location.e.b$b r2 = r4.h     // Catch:{ Exception -> 0x0046 }
            r3 = 1280(0x500, float:1.794E-42)
            r0.listen(r2, r3)     // Catch:{ Exception -> 0x0046 }
            goto L_0x0047
        L_0x0046:
            r0 = move-exception
        L_0x0047:
            r4.i = r1     // Catch:{ all -> 0x004d }
            monitor-exit(r4)
            return
        L_0x004b:
            monitor-exit(r4)
            return
        L_0x004d:
            r0 = move-exception
            monitor-exit(r4)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.e.b.b():void");
    }

    public synchronized void c() {
        if (this.i) {
            if (!(this.h == null || this.d == null)) {
                this.d.listen(this.h, 0);
            }
            this.h = null;
            this.d = null;
            this.g.clear();
            this.g = null;
            j();
            this.i = false;
        }
    }

    public boolean d() {
        return this.j;
    }

    public int e() {
        TelephonyManager telephonyManager = this.d;
        if (telephonyManager == null) {
            return 0;
        }
        try {
            return telephonyManager.getNetworkType();
        } catch (Exception e2) {
            return 0;
        }
    }

    public a f() {
        a aVar = this.e;
        if ((aVar == null || !aVar.a() || !this.e.b()) && this.d != null) {
            try {
                k();
                if (Build.VERSION.SDK_INT >= 29 && System.currentTimeMillis() - this.m > 30000) {
                    this.m = System.currentTimeMillis();
                    if (this.l == null) {
                        this.l = new a();
                    }
                    this.d.requestCellInfoUpdate(f.getServiceContext().getMainExecutor(), this.l);
                }
            } catch (Exception e2) {
            }
        }
        a aVar2 = this.e;
        if (aVar2 != null && aVar2.e()) {
            this.f = null;
            this.f = new a(this.e.a, this.e.b, this.e.c, this.e.d, this.e.h, this.e.i, this.e.j);
        }
        a aVar3 = this.e;
        if (aVar3 != null && aVar3.d() && this.f != null && this.e.i == 'g') {
            this.e.d = this.f.d;
            this.e.c = this.f.c;
        }
        return this.e;
    }

    public String g() {
        int i2 = -1;
        try {
            if (this.d != null) {
                i2 = this.d.getSimState();
            }
        } catch (Exception e2) {
        }
        return "&sim=" + i2;
    }

    public int h() {
        return 0;
    }
}
