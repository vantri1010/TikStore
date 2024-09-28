package com.baidu.location.b;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import com.baidu.android.bbalbs.common.util.CommonParam;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.LocationClientOption;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import com.googlecode.mp4parser.boxes.dece.BaseLocationBox;
import com.king.zxing.util.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;

public class c {
    private static Class<?> i = null;
    private static char[] r = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.".toCharArray();
    String a = null;
    String b = null;
    b c = new b();
    private Context d = null;
    private TelephonyManager e = null;
    private com.baidu.location.e.a f = new com.baidu.location.e.a();
    private WifiManager g = null;
    private C0005c h = null;
    private String j = null;
    private String k = null;
    /* access modifiers changed from: private */
    public LocationClientOption l;
    /* access modifiers changed from: private */
    public a m;
    private String n = null;
    /* access modifiers changed from: private */
    public String o = null;
    /* access modifiers changed from: private */
    public String p = null;
    private boolean q = false;
    private long s = 0;
    /* access modifiers changed from: private */
    public boolean t = false;

    public interface a {
        void onReceiveLocation(BDLocation bDLocation);
    }

    class b extends e {
        String a = null;

        b() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = k.e();
            if (!(c.this.o == null || c.this.p == null)) {
                this.a += String.format(Locale.CHINA, "&ki=%s&sn=%s", new Object[]{c.this.o, c.this.p});
            }
            String str = this.a + "&enc=2";
            this.a = str;
            String encodeTp4 = Jni.encodeTp4(str);
            this.a = null;
            this.k.put(BaseLocationBox.TYPE, encodeTp4);
            this.k.put("trtm", String.format(Locale.CHINA, "%d", new Object[]{Long.valueOf(System.currentTimeMillis())}));
        }

        public void a(String str) {
            this.a = str;
            c(k.f);
        }

        public void a(boolean z) {
            BDLocation bDLocation;
            if (!z || this.j == null) {
                c.this.c(63);
            } else {
                try {
                    String str = this.j;
                    if (str.contains("\"enc\"")) {
                        try {
                            JSONObject jSONObject = new JSONObject(str);
                            if (jSONObject.has("enc")) {
                                str = j.a().a(jSONObject.getString("enc"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        bDLocation = new BDLocation(str);
                    } catch (Exception e2) {
                        bDLocation = new BDLocation();
                        bDLocation.setLocType(63);
                    }
                    if (bDLocation.getLocType() == 161) {
                        bDLocation.setCoorType(c.this.l.coorType);
                        bDLocation.setLocationID(Jni.en1(c.this.a + ";" + c.this.b + ";" + bDLocation.getTime()));
                        bDLocation.setRoadLocString(0.0f, 0.0f);
                        boolean unused = c.this.t = true;
                        c.this.m.onReceiveLocation(bDLocation);
                    } else {
                        c.this.c(bDLocation.getLocType());
                    }
                } catch (Exception e3) {
                    c.this.c(63);
                    e3.printStackTrace();
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
        }
    }

    /* renamed from: com.baidu.location.b.c$c  reason: collision with other inner class name */
    protected class C0005c {
        public List<ScanResult> a = null;
        public String b = null;
        private long d = 0;
        private String e = null;

        public C0005c(List<ScanResult> list) {
            this.a = list;
            this.d = System.currentTimeMillis();
            try {
                b();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        private void b() {
            if (a() >= 1) {
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

        /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00d7  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00e1  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x00f5  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x00ff A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:59:0x0101  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x00ef A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x00f1 A[EDGE_INSN: B:80:0x00f1->B:54:0x00f1 ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String a(int r25, java.lang.String r26) {
            /*
                r24 = this;
                r1 = r24
                r2 = r26
                int r0 = r24.a()
                r3 = 0
                r4 = 2
                if (r0 >= r4) goto L_0x000d
                return r3
            L_0x000d:
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                int r0 = android.os.Build.VERSION.SDK_INT
                r5 = 19
                r6 = 1
                r7 = 0
                if (r0 < r5) goto L_0x002b
                long r10 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x0023 }
                r12 = 1000(0x3e8, double:4.94E-321)
                long r10 = r10 / r12
                goto L_0x0025
            L_0x0023:
                r0 = move-exception
                r10 = r7
            L_0x0025:
                int r0 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
                if (r0 <= 0) goto L_0x002c
                r5 = 1
                goto L_0x002d
            L_0x002b:
                r10 = r7
            L_0x002c:
                r5 = 0
            L_0x002d:
                java.lang.StringBuffer r12 = new java.lang.StringBuffer
                r0 = 512(0x200, float:7.175E-43)
                r12.<init>(r0)
                java.util.List<android.net.wifi.ScanResult> r0 = r1.a
                int r13 = r0.size()
                r17 = r7
                r0 = 0
                r14 = 0
                r15 = 1
                r16 = 0
                r19 = 0
            L_0x0043:
                java.lang.String r7 = ""
                java.lang.String r8 = "|"
                if (r14 >= r13) goto L_0x00ef
                java.util.List<android.net.wifi.ScanResult> r3 = r1.a
                java.lang.Object r3 = r3.get(r14)
                if (r3 == 0) goto L_0x00e5
                java.util.List<android.net.wifi.ScanResult> r3 = r1.a
                java.lang.Object r3 = r3.get(r14)
                android.net.wifi.ScanResult r3 = (android.net.wifi.ScanResult) r3
                int r3 = r3.level
                if (r3 != 0) goto L_0x0060
                goto L_0x00e5
            L_0x0060:
                int r3 = r0 + 1
                if (r15 == 0) goto L_0x006b
                java.lang.String r0 = "&wf="
                r12.append(r0)
                r15 = 0
                goto L_0x006e
            L_0x006b:
                r12.append(r8)
            L_0x006e:
                java.util.List<android.net.wifi.ScanResult> r0 = r1.a
                java.lang.Object r0 = r0.get(r14)
                android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0
                java.lang.String r0 = r0.BSSID
                java.lang.String r9 = ":"
                java.lang.String r0 = r0.replace(r9, r7)
                r12.append(r0)
                if (r2 == 0) goto L_0x008b
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x008b
                r19 = r3
            L_0x008b:
                java.util.List<android.net.wifi.ScanResult> r0 = r1.a
                java.lang.Object r0 = r0.get(r14)
                android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0
                int r0 = r0.level
                if (r0 >= 0) goto L_0x0098
                int r0 = -r0
            L_0x0098:
                java.util.Locale r9 = java.util.Locale.CHINA
                java.lang.Object[] r2 = new java.lang.Object[r6]
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r20 = 0
                r2[r20] = r0
                java.lang.String r0 = ";%d;"
                java.lang.String r0 = java.lang.String.format(r9, r0, r2)
                r12.append(r0)
                int r2 = r16 + 1
                if (r5 == 0) goto L_0x00da
                java.util.List<android.net.wifi.ScanResult> r0 = r1.a     // Catch:{ all -> 0x00c7 }
                java.lang.Object r0 = r0.get(r14)     // Catch:{ all -> 0x00c7 }
                android.net.wifi.ScanResult r0 = (android.net.wifi.ScanResult) r0     // Catch:{ all -> 0x00c7 }
                r21 = r7
                long r6 = r0.timestamp     // Catch:{ all -> 0x00c5 }
                long r6 = r10 - r6
                r22 = 1000000(0xf4240, double:4.940656E-318)
                long r6 = r6 / r22
                goto L_0x00cc
            L_0x00c5:
                r0 = move-exception
                goto L_0x00ca
            L_0x00c7:
                r0 = move-exception
                r21 = r7
            L_0x00ca:
                r6 = 0
            L_0x00cc:
                java.lang.Long r0 = java.lang.Long.valueOf(r6)
                r4.add(r0)
                int r0 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
                if (r0 <= 0) goto L_0x00dc
                r17 = r6
                goto L_0x00dc
            L_0x00da:
                r21 = r7
            L_0x00dc:
                r6 = r25
                if (r2 <= r6) goto L_0x00e1
                goto L_0x00f1
            L_0x00e1:
                r16 = r2
                r0 = r3
                goto L_0x00e7
            L_0x00e5:
                r6 = r25
            L_0x00e7:
                int r14 = r14 + 1
                r2 = r26
                r3 = 0
                r6 = 1
                goto L_0x0043
            L_0x00ef:
                r21 = r7
            L_0x00f1:
                r0 = r19
                if (r0 <= 0) goto L_0x00fd
                java.lang.String r2 = "&wf_n="
                r12.append(r2)
                r12.append(r0)
            L_0x00fd:
                if (r15 == 0) goto L_0x0101
                r2 = 0
                return r2
            L_0x0101:
                r2 = 10
                int r0 = (r17 > r2 ? 1 : (r17 == r2 ? 0 : -1))
                if (r0 <= 0) goto L_0x0183
                int r0 = r4.size()
                if (r0 <= 0) goto L_0x0183
                r2 = 0
                java.lang.Object r0 = r4.get(r2)
                java.lang.Long r0 = (java.lang.Long) r0
                long r5 = r0.longValue()
                r10 = 0
                int r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
                if (r0 <= 0) goto L_0x0183
                java.lang.StringBuffer r0 = new java.lang.StringBuffer
                r3 = 128(0x80, float:1.794E-43)
                r0.<init>(r3)
                java.lang.String r3 = "&wf_ut="
                r0.append(r3)
                java.lang.Object r3 = r4.get(r2)
                java.lang.Long r3 = (java.lang.Long) r3
                java.util.Iterator r4 = r4.iterator()
                r6 = 1
            L_0x0135:
                boolean r5 = r4.hasNext()
                if (r5 == 0) goto L_0x017c
                java.lang.Object r5 = r4.next()
                java.lang.Long r5 = (java.lang.Long) r5
                if (r6 == 0) goto L_0x0150
                long r5 = r5.longValue()
                r0.append(r5)
                r7 = r21
                r6 = 0
                r13 = 0
                goto L_0x0176
            L_0x0150:
                long r9 = r5.longValue()
                long r13 = r3.longValue()
                long r9 = r9 - r13
                r13 = 0
                int r5 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
                if (r5 == 0) goto L_0x0174
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r7 = r21
                r5.append(r7)
                r5.append(r9)
                java.lang.String r5 = r5.toString()
                r0.append(r5)
                goto L_0x0176
            L_0x0174:
                r7 = r21
            L_0x0176:
                r0.append(r8)
                r21 = r7
                goto L_0x0135
            L_0x017c:
                java.lang.String r0 = r0.toString()
                r12.append(r0)
            L_0x0183:
                java.lang.String r0 = r12.toString()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.c.C0005c.a(int, java.lang.String):java.lang.String");
        }
    }

    public c(Context context, LocationClientOption locationClientOption, a aVar) {
        StringBuilder sb;
        String str = null;
        Context applicationContext = context.getApplicationContext();
        this.d = applicationContext;
        try {
            k.ax = applicationContext.getPackageName();
        } catch (Exception e2) {
        }
        this.q = true;
        if (1 != 0) {
            this.l = new LocationClientOption(locationClientOption);
            this.m = aVar;
            this.a = this.d.getPackageName();
            this.b = null;
            try {
                this.e = (TelephonyManager) this.d.getSystemService("phone");
                this.g = (WifiManager) this.d.getApplicationContext().getSystemService("wifi");
            } catch (Exception e3) {
            }
            this.k = "&" + this.a + "&" + null;
            try {
                this.b = CommonParam.getCUID(this.d);
            } catch (Throwable th) {
                this.b = null;
                this.e = null;
                this.g = null;
            }
            if (this.b != null) {
                k.o = "" + this.b;
                sb = new StringBuilder();
                sb.append("&prod=");
                sb.append(this.l.prodName);
                sb.append(LogUtils.COLON);
                sb.append(this.a);
                sb.append("|&cu=");
                str = this.b;
            } else {
                sb = new StringBuilder();
                sb.append("&prod=");
                sb.append(this.l.prodName);
                sb.append(LogUtils.COLON);
                sb.append(this.a);
                sb.append("|&im=");
            }
            sb.append(str);
            sb.append("&coor=");
            sb.append(locationClientOption.getCoorType());
            this.j = sb.toString();
            StringBuffer stringBuffer = new StringBuffer(256);
            stringBuffer.append("&fw=");
            stringBuffer.append("8.22");
            stringBuffer.append("&sdk=");
            stringBuffer.append("8.22");
            stringBuffer.append("&lt=1");
            stringBuffer.append("&mb=");
            stringBuffer.append(Build.MODEL);
            stringBuffer.append("&resid=");
            stringBuffer.append("12");
            locationClientOption.getAddrType();
            if (locationClientOption.getAddrType() != null && locationClientOption.getAddrType().equals("all")) {
                this.j += "&addr=allj2";
                if (locationClientOption.isNeedNewVersionRgc) {
                    stringBuffer.append("&adtp=n2");
                }
            }
            if (locationClientOption.isNeedAptag || locationClientOption.isNeedAptagd) {
                this.j += "&sema=";
                if (locationClientOption.isNeedAptag) {
                    this.j += "aptag|";
                }
                if (locationClientOption.isNeedAptagd) {
                    this.j += "aptagd2|";
                }
                this.o = com.baidu.location.a.a.b(this.d);
                this.p = com.baidu.location.a.a.c(this.d);
            }
            stringBuffer.append("&first=1");
            stringBuffer.append("&os=A");
            stringBuffer.append(Build.VERSION.SDK);
            this.j += stringBuffer.toString();
        }
    }

    private int a(int i2) {
        if (i2 == Integer.MAX_VALUE) {
            return -1;
        }
        return i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0136 A[Catch:{ Exception -> 0x02c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0179 A[Catch:{ Exception -> 0x02c7 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:48:0x01a3=Splitter:B:48:0x01a3, B:73:0x0216=Splitter:B:73:0x0216, B:54:0x01b9=Splitter:B:54:0x01b9, B:79:0x022c=Splitter:B:79:0x022c} */
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
            if (r0 == 0) goto L_0x0064
            r0 = r2
            android.telephony.CellInfoGsm r0 = (android.telephony.CellInfoGsm) r0
            android.telephony.CellIdentityGsm r11 = r0.getCellIdentity()
            int r12 = r11.getMcc()
            int r12 = r1.a((int) r12)
            r5.c = r12
            int r12 = r11.getMnc()
            int r12 = r1.a((int) r12)
            r5.d = r12
            int r12 = r11.getLac()
            int r12 = r1.a((int) r12)
            r5.a = r12
            int r11 = r11.getCid()
            int r11 = r1.a((int) r11)
            long r11 = (long) r11
            r5.b = r11
            r5.i = r10
            android.telephony.CellSignalStrengthGsm r0 = r0.getCellSignalStrength()
            int r0 = r0.getAsuLevel()
            r5.h = r0
            r5.k = r6
        L_0x0061:
            r0 = 1
            goto L_0x012b
        L_0x0064:
            boolean r0 = r2 instanceof android.telephony.CellInfoCdma
            if (r0 == 0) goto L_0x00e6
            r0 = r2
            android.telephony.CellInfoCdma r0 = (android.telephony.CellInfoCdma) r0
            android.telephony.CellIdentityCdma r11 = r0.getCellIdentity()
            int r12 = r11.getLatitude()
            r5.e = r12
            int r12 = r11.getLongitude()
            r5.f = r12
            int r12 = r11.getSystemId()
            int r12 = r1.a((int) r12)
            r5.d = r12
            int r12 = r11.getNetworkId()
            int r12 = r1.a((int) r12)
            r5.a = r12
            int r11 = r11.getBasestationId()
            int r11 = r1.a((int) r11)
            long r11 = (long) r11
            r5.b = r11
            r11 = 99
            r5.i = r11
            android.telephony.CellSignalStrengthCdma r0 = r0.getCellSignalStrength()
            int r0 = r0.getCdmaDbm()
            r5.h = r0
            r5.k = r9
            com.baidu.location.e.a r0 = r1.f
            if (r0 == 0) goto L_0x00b9
            int r0 = r0.c
            if (r0 <= 0) goto L_0x00b9
            com.baidu.location.e.a r0 = r1.f
            int r0 = r0.c
            r5.c = r0
            goto L_0x0061
        L_0x00b9:
            r11 = -1
            android.telephony.TelephonyManager r0 = r1.e     // Catch:{ Exception -> 0x00df }
            java.lang.String r0 = r0.getNetworkOperator()     // Catch:{ Exception -> 0x00df }
            if (r0 == 0) goto L_0x00e0
            int r12 = r0.length()     // Catch:{ Exception -> 0x00df }
            if (r12 <= 0) goto L_0x00e0
            int r12 = r0.length()     // Catch:{ Exception -> 0x00df }
            if (r12 < r8) goto L_0x00e0
            java.lang.String r0 = r0.substring(r7, r8)     // Catch:{ Exception -> 0x00df }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x00df }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x00df }
            if (r0 >= 0) goto L_0x00dd
            goto L_0x00e0
        L_0x00dd:
            r11 = r0
            goto L_0x00e0
        L_0x00df:
            r0 = move-exception
        L_0x00e0:
            if (r11 <= 0) goto L_0x0061
            r5.c = r11
            goto L_0x0061
        L_0x00e6:
            boolean r0 = r2 instanceof android.telephony.CellInfoLte
            if (r0 == 0) goto L_0x012a
            r0 = r2
            android.telephony.CellInfoLte r0 = (android.telephony.CellInfoLte) r0
            android.telephony.CellIdentityLte r11 = r0.getCellIdentity()
            int r12 = r11.getMcc()
            int r12 = r1.a((int) r12)
            r5.c = r12
            int r12 = r11.getMnc()
            int r12 = r1.a((int) r12)
            r5.d = r12
            int r12 = r11.getTac()
            int r12 = r1.a((int) r12)
            r5.a = r12
            int r11 = r11.getCi()
            int r11 = r1.a((int) r11)
            long r11 = (long) r11
            r5.b = r11
            r5.i = r10
            android.telephony.CellSignalStrengthLte r0 = r0.getCellSignalStrength()
            int r0 = r0.getAsuLevel()
            r5.h = r0
            r5.k = r8
            goto L_0x0061
        L_0x012a:
            r0 = 0
        L_0x012b:
            r11 = 18
            if (r3 < r11) goto L_0x02c8
            if (r0 != 0) goto L_0x02c8
            boolean r0 = r2 instanceof android.telephony.CellInfoWcdma     // Catch:{ Exception -> 0x02c7 }
            r3 = 4
            if (r0 == 0) goto L_0x0179
            r0 = r2
            android.telephony.CellInfoWcdma r0 = (android.telephony.CellInfoWcdma) r0     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellIdentityWcdma r0 = r0.getCellIdentity()     // Catch:{ Exception -> 0x02c7 }
            int r4 = r0.getMcc()     // Catch:{ Exception -> 0x02c7 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02c7 }
            r5.c = r4     // Catch:{ Exception -> 0x02c7 }
            int r4 = r0.getMnc()     // Catch:{ Exception -> 0x02c7 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02c7 }
            r5.d = r4     // Catch:{ Exception -> 0x02c7 }
            int r4 = r0.getLac()     // Catch:{ Exception -> 0x02c7 }
            int r4 = r1.a((int) r4)     // Catch:{ Exception -> 0x02c7 }
            r5.a = r4     // Catch:{ Exception -> 0x02c7 }
            int r0 = r0.getCid()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02c7 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x02c7 }
            r5.b = r6     // Catch:{ Exception -> 0x02c7 }
            r5.i = r10     // Catch:{ Exception -> 0x02c7 }
            r0 = r2
            android.telephony.CellInfoWcdma r0 = (android.telephony.CellInfoWcdma) r0     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellSignalStrengthWcdma r0 = r0.getCellSignalStrength()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r0.getAsuLevel()     // Catch:{ Exception -> 0x02c7 }
            r5.h = r0     // Catch:{ Exception -> 0x02c7 }
            r5.k = r3     // Catch:{ Exception -> 0x02c7 }
            goto L_0x02c8
        L_0x0179:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02c7 }
            r11 = 29
            if (r0 < r11) goto L_0x02c8
            boolean r0 = r2 instanceof android.telephony.CellInfoTdscdma     // Catch:{ Exception -> 0x02c7 }
            r11 = 28
            r12 = 5
            if (r0 == 0) goto L_0x01eb
            r0 = r2
            android.telephony.CellInfoTdscdma r0 = (android.telephony.CellInfoTdscdma) r0     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellIdentityTdscdma r3 = r0.getCellIdentity()     // Catch:{ Exception -> 0x02c7 }
            java.lang.String r0 = r3.getMccString()     // Catch:{ Exception -> 0x02c7 }
            if (r0 == 0) goto L_0x01a3
            java.lang.String r0 = r3.getMccString()     // Catch:{ all -> 0x01a2 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x01a2 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01a2 }
            r5.c = r0     // Catch:{ all -> 0x01a2 }
            goto L_0x01a3
        L_0x01a2:
            r0 = move-exception
        L_0x01a3:
            java.lang.String r0 = r3.getMncString()     // Catch:{ Exception -> 0x02c7 }
            if (r0 == 0) goto L_0x01b9
            java.lang.String r0 = r3.getMncString()     // Catch:{ all -> 0x01b8 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x01b8 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x01b8 }
            r5.d = r0     // Catch:{ all -> 0x01b8 }
            goto L_0x01b9
        L_0x01b8:
            r0 = move-exception
        L_0x01b9:
            int r0 = r3.getLac()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02c7 }
            r5.a = r0     // Catch:{ Exception -> 0x02c7 }
            int r0 = r3.getCid()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02c7 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x02c7 }
            r5.b = r3     // Catch:{ Exception -> 0x02c7 }
            r5.i = r10     // Catch:{ Exception -> 0x02c7 }
            r0 = r2
            android.telephony.CellInfoTdscdma r0 = (android.telephony.CellInfoTdscdma) r0     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellSignalStrengthTdscdma r0 = r0.getCellSignalStrength()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r0.getAsuLevel()     // Catch:{ Exception -> 0x02c7 }
            r5.h = r0     // Catch:{ Exception -> 0x02c7 }
            r5.k = r12     // Catch:{ Exception -> 0x02c7 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02c7 }
            if (r0 < r11) goto L_0x02c8
            int r0 = r18.getCellConnectionStatus()     // Catch:{ Exception -> 0x02c7 }
            r5.j = r0     // Catch:{ Exception -> 0x02c7 }
            goto L_0x02c8
        L_0x01eb:
            boolean r0 = r2 instanceof android.telephony.CellInfoNr     // Catch:{ Exception -> 0x02c7 }
            if (r0 == 0) goto L_0x02c8
            r0 = r2
            android.telephony.CellInfoNr r0 = (android.telephony.CellInfoNr) r0     // Catch:{ all -> 0x01fa }
            android.telephony.CellIdentity r0 = r0.getCellIdentity()     // Catch:{ all -> 0x01fa }
            android.telephony.CellIdentityNr r0 = (android.telephony.CellIdentityNr) r0     // Catch:{ all -> 0x01fa }
            r4 = r0
            goto L_0x01fe
        L_0x01fa:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ Exception -> 0x02c7 }
        L_0x01fe:
            if (r4 == 0) goto L_0x02c8
            java.lang.String r0 = r4.getMccString()     // Catch:{ Exception -> 0x02c7 }
            if (r0 == 0) goto L_0x0216
            java.lang.String r0 = r4.getMccString()     // Catch:{ all -> 0x0215 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x0215 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0215 }
            r5.c = r0     // Catch:{ all -> 0x0215 }
            goto L_0x0216
        L_0x0215:
            r0 = move-exception
        L_0x0216:
            java.lang.String r0 = r4.getMncString()     // Catch:{ Exception -> 0x02c7 }
            if (r0 == 0) goto L_0x022c
            java.lang.String r0 = r4.getMncString()     // Catch:{ all -> 0x022b }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ all -> 0x022b }
            int r0 = r0.intValue()     // Catch:{ all -> 0x022b }
            r5.d = r0     // Catch:{ all -> 0x022b }
            goto L_0x022c
        L_0x022b:
            r0 = move-exception
        L_0x022c:
            int r0 = r4.getTac()     // Catch:{ Exception -> 0x02c7 }
            int r0 = r1.a((int) r0)     // Catch:{ Exception -> 0x02c7 }
            r5.a = r0     // Catch:{ Exception -> 0x02c7 }
            long r13 = r4.getNci()     // Catch:{ Exception -> 0x02c7 }
            r15 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r0 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0249
            long r13 = r4.getNci()     // Catch:{ Exception -> 0x02c7 }
            r5.b = r13     // Catch:{ Exception -> 0x02c7 }
        L_0x0249:
            r5.i = r10     // Catch:{ Exception -> 0x02c7 }
            r0 = 6
            r5.k = r0     // Catch:{ Exception -> 0x02c7 }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02c7 }
            if (r4 < r11) goto L_0x0258
            int r4 = r18.getCellConnectionStatus()     // Catch:{ Exception -> 0x02c7 }
            r5.j = r4     // Catch:{ Exception -> 0x02c7 }
        L_0x0258:
            r4 = r2
            android.telephony.CellInfoNr r4 = (android.telephony.CellInfoNr) r4     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellSignalStrength r4 = r4.getCellSignalStrength()     // Catch:{ Exception -> 0x02c7 }
            android.telephony.CellSignalStrengthNr r4 = (android.telephony.CellSignalStrengthNr) r4     // Catch:{ Exception -> 0x02c7 }
            int r10 = r4.getAsuLevel()     // Catch:{ Exception -> 0x02c7 }
            r5.h = r10     // Catch:{ Exception -> 0x02c7 }
            java.util.Locale r10 = java.util.Locale.US     // Catch:{ Exception -> 0x02c7 }
            java.lang.String r11 = "%d|%d|%d|%d|%d|%d|%d|%d"
            r13 = 8
            java.lang.Object[] r13 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x02c7 }
            int r14 = r4.getCsiRsrp()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x02c7 }
            r13[r7] = r14     // Catch:{ Exception -> 0x02c7 }
            int r7 = r4.getCsiRsrq()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x02c7 }
            r13[r9] = r7     // Catch:{ Exception -> 0x02c7 }
            int r7 = r4.getCsiSinr()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x02c7 }
            r13[r6] = r7     // Catch:{ Exception -> 0x02c7 }
            int r6 = r4.getDbm()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x02c7 }
            r13[r8] = r6     // Catch:{ Exception -> 0x02c7 }
            int r6 = r4.getLevel()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x02c7 }
            r13[r3] = r6     // Catch:{ Exception -> 0x02c7 }
            int r3 = r4.getSsRsrp()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02c7 }
            r13[r12] = r3     // Catch:{ Exception -> 0x02c7 }
            int r3 = r4.getSsRsrq()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02c7 }
            r13[r0] = r3     // Catch:{ Exception -> 0x02c7 }
            r0 = 7
            int r3 = r4.getSsSinr()     // Catch:{ Exception -> 0x02c7 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x02c7 }
            r13[r0] = r3     // Catch:{ Exception -> 0x02c7 }
            java.lang.String r0 = java.lang.String.format(r10, r11, r13)     // Catch:{ Exception -> 0x02c7 }
            r5.m = r0     // Catch:{ Exception -> 0x02c7 }
            goto L_0x02c8
        L_0x02c7:
            r0 = move-exception
        L_0x02c8:
            long r3 = android.os.SystemClock.elapsedRealtimeNanos()     // Catch:{ Error -> 0x02dd }
            long r6 = r18.getTimeStamp()     // Catch:{ Error -> 0x02dd }
            long r3 = r3 - r6
            r6 = 1000000(0xf4240, double:4.940656E-318)
            long r3 = r3 / r6
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ Error -> 0x02dd }
            long r6 = r6 - r3
            r5.g = r6     // Catch:{ Error -> 0x02dd }
            goto L_0x02e4
        L_0x02dd:
            r0 = move-exception
            long r2 = java.lang.System.currentTimeMillis()
            r5.g = r2
        L_0x02e4:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.c.a(android.telephony.CellInfo):com.baidu.location.e.a");
    }

    private void a(CellLocation cellLocation) {
        if (cellLocation != null && this.e != null) {
            com.baidu.location.e.a aVar = new com.baidu.location.e.a();
            String networkOperator = this.e.getNetworkOperator();
            if (networkOperator != null && networkOperator.length() > 0) {
                try {
                    if (networkOperator.length() >= 3) {
                        int intValue = Integer.valueOf(networkOperator.substring(0, 3)).intValue();
                        if (intValue < 0) {
                            intValue = this.f.c;
                        }
                        aVar.c = intValue;
                    }
                    String substring = networkOperator.substring(3);
                    if (substring != null) {
                        char[] charArray = substring.toCharArray();
                        int i2 = 0;
                        while (true) {
                            if (i2 >= charArray.length) {
                                break;
                            } else if (!Character.isDigit(charArray[i2])) {
                                break;
                            } else {
                                i2++;
                            }
                        }
                        int intValue2 = Integer.valueOf(substring.substring(0, i2)).intValue();
                        if (intValue2 < 0) {
                            intValue2 = this.f.d;
                        }
                        aVar.d = intValue2;
                    }
                } catch (Exception e2) {
                }
            }
            if (cellLocation instanceof GsmCellLocation) {
                GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                aVar.a = gsmCellLocation.getLac();
                aVar.b = (long) gsmCellLocation.getCid();
                aVar.i = 'g';
            } else if (cellLocation instanceof CdmaCellLocation) {
                aVar.i = 'c';
                if (i == null) {
                    try {
                        i = Class.forName("android.telephony.cdma.CdmaCellLocation");
                    } catch (Exception e3) {
                        i = null;
                        return;
                    }
                }
                Class<?> cls = i;
                if (cls != null && cls.isInstance(cellLocation)) {
                    try {
                        int systemId = ((CdmaCellLocation) cellLocation).getSystemId();
                        if (systemId < 0) {
                            systemId = -1;
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
                    }
                }
            }
            if (aVar.b()) {
                this.f = aVar;
            } else {
                this.f = null;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x002c A[Catch:{ all -> 0x005a }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0064 A[Catch:{ Exception -> 0x008b }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x008f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ab A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String b(int r5) {
        /*
            r4 = this;
            r0 = 0
            com.baidu.location.e.a r1 = r4.d()     // Catch:{ all -> 0x005a }
            if (r1 == 0) goto L_0x0011
            boolean r2 = r1.b()     // Catch:{ all -> 0x005a }
            if (r2 != 0) goto L_0x000e
            goto L_0x0011
        L_0x000e:
            r4.f = r1     // Catch:{ all -> 0x005a }
            goto L_0x0020
        L_0x0011:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x005a }
            r2 = 28
            if (r1 > r2) goto L_0x0020
            android.telephony.TelephonyManager r1 = r4.e     // Catch:{ all -> 0x005a }
            android.telephony.CellLocation r1 = r1.getCellLocation()     // Catch:{ all -> 0x005a }
            r4.a((android.telephony.CellLocation) r1)     // Catch:{ all -> 0x005a }
        L_0x0020:
            com.baidu.location.e.a r1 = r4.f     // Catch:{ all -> 0x005a }
            if (r1 == 0) goto L_0x0033
            com.baidu.location.e.a r1 = r4.f     // Catch:{ all -> 0x005a }
            boolean r1 = r1.b()     // Catch:{ all -> 0x005a }
            if (r1 == 0) goto L_0x0033
            com.baidu.location.e.a r1 = r4.f     // Catch:{ all -> 0x005a }
            java.lang.String r1 = r1.h()     // Catch:{ all -> 0x005a }
            goto L_0x0034
        L_0x0033:
            r1 = r0
        L_0x0034:
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x0058 }
            if (r2 != 0) goto L_0x005c
            com.baidu.location.e.a r2 = r4.f     // Catch:{ all -> 0x0058 }
            if (r2 == 0) goto L_0x005c
            com.baidu.location.e.a r2 = r4.f     // Catch:{ all -> 0x0058 }
            java.lang.String r2 = r2.l     // Catch:{ all -> 0x0058 }
            if (r2 == 0) goto L_0x005c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0058 }
            r2.<init>()     // Catch:{ all -> 0x0058 }
            r2.append(r1)     // Catch:{ all -> 0x0058 }
            com.baidu.location.e.a r3 = r4.f     // Catch:{ all -> 0x0058 }
            java.lang.String r3 = r3.l     // Catch:{ all -> 0x0058 }
            r2.append(r3)     // Catch:{ all -> 0x0058 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x0058 }
            goto L_0x005c
        L_0x0058:
            r2 = move-exception
            goto L_0x005c
        L_0x005a:
            r1 = move-exception
            r1 = r0
        L_0x005c:
            r4.h = r0     // Catch:{ Exception -> 0x008b }
            boolean r2 = r4.e()     // Catch:{ Exception -> 0x008b }
            if (r2 == 0) goto L_0x0089
            com.baidu.location.b.c$c r2 = new com.baidu.location.b.c$c     // Catch:{ Exception -> 0x008b }
            android.net.wifi.WifiManager r3 = r4.g     // Catch:{ Exception -> 0x008b }
            java.util.List r3 = r3.getScanResults()     // Catch:{ Exception -> 0x008b }
            r2.<init>(r3)     // Catch:{ Exception -> 0x008b }
            r4.h = r2     // Catch:{ Exception -> 0x008b }
            java.lang.String r3 = r4.f()     // Catch:{ Exception -> 0x008b }
            java.lang.String r5 = r2.a(r5, r3)     // Catch:{ Exception -> 0x008b }
            com.baidu.location.LocationClientOption r2 = r4.l     // Catch:{ Exception -> 0x0087 }
            boolean r2 = r2.isOnceLocation()     // Catch:{ Exception -> 0x0087 }
            if (r2 == 0) goto L_0x008d
            android.net.wifi.WifiManager r2 = r4.g     // Catch:{ Exception -> 0x0087 }
            r2.startScan()     // Catch:{ Exception -> 0x0087 }
            goto L_0x008d
        L_0x0087:
            r2 = move-exception
            goto L_0x008d
        L_0x0089:
            r5 = r0
            goto L_0x008d
        L_0x008b:
            r5 = move-exception
            goto L_0x0089
        L_0x008d:
            if (r1 != 0) goto L_0x0094
            if (r5 != 0) goto L_0x0094
            r4.n = r0
            return r0
        L_0x0094:
            if (r5 == 0) goto L_0x00a9
            if (r1 != 0) goto L_0x009a
            r1 = r5
            goto L_0x00a9
        L_0x009a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            r2.append(r5)
            java.lang.String r1 = r2.toString()
        L_0x00a9:
            if (r1 != 0) goto L_0x00ac
            return r0
        L_0x00ac:
            r4.n = r1
            java.lang.String r5 = r4.j
            if (r5 == 0) goto L_0x00c7
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r0 = r4.n
            r5.append(r0)
            java.lang.String r0 = r4.j
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            r4.n = r5
        L_0x00c7:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            java.lang.String r0 = r4.j
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.c.b(int):java.lang.String");
    }

    /* access modifiers changed from: private */
    public void c(int i2) {
        if (this.l.isOnceLocation()) {
            BDLocation bDLocation = new BDLocation();
            bDLocation.setLocType(i2);
            a aVar = this.m;
            if (aVar != null) {
                aVar.onReceiveLocation(bDLocation);
            }
        }
    }

    private com.baidu.location.e.a d() {
        if (Integer.valueOf(Build.VERSION.SDK_INT).intValue() < 17) {
            return null;
        }
        try {
            List<CellInfo> allCellInfo = this.e.getAllCellInfo();
            if (allCellInfo == null || allCellInfo.size() <= 0) {
                return null;
            }
            com.baidu.location.e.a aVar = null;
            for (CellInfo next : allCellInfo) {
                if (next.isRegistered()) {
                    boolean z = false;
                    if (aVar != null) {
                        z = true;
                    }
                    com.baidu.location.e.a a2 = a(next);
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

    private boolean e() {
        try {
            return this.g.isWifiEnabled() || (Build.VERSION.SDK_INT > 17 && this.g.isScanAlwaysAvailable());
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
    }

    private String f() {
        WifiManager wifiManager = this.g;
        if (wifiManager == null) {
            return null;
        }
        try {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo == null) {
                return null;
            }
            String bssid = connectionInfo.getBSSID();
            String replace = bssid != null ? bssid.replace(LogUtils.COLON, "") : null;
            if (replace == null || replace.length() == 12) {
                return new String(replace);
            }
            return null;
        } catch (Exception e2) {
            return null;
        }
    }

    public void a() {
        b();
    }

    public String b() {
        try {
            return b(15);
        } catch (Exception e2) {
            return null;
        }
    }

    public void c() {
        String str = this.n;
        if (str == null) {
            c(62);
        } else if (this.q) {
            this.c.a(str);
        }
    }
}
