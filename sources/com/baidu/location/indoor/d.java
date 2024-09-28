package com.baidu.location.indoor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import com.king.zxing.util.LogUtils;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import kotlin.UByte;

public class d {
    private static final char[] a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static d b = null;
    private Context c;
    private boolean d = false;
    private boolean e = false;
    /* access modifiers changed from: private */
    public BluetoothAdapter f;
    private boolean g = false;
    private b h;
    private boolean i = false;
    private String j = null;
    private long k = -1;
    /* access modifiers changed from: private */
    public HashMap<String, ScanResult> l = new HashMap<>();
    private Handler m = new Handler();
    private Runnable n = new e(this);
    private Object o = null;

    public static class a implements Comparable<a> {
        public String a;
        public int b;
        public long c;

        public a(String str, int i, long j) {
            this.a = str;
            this.b = i;
            this.c = j / 1000000;
        }

        /* renamed from: a */
        public int compareTo(a aVar) {
            return Math.abs(this.b) > Math.abs(aVar.b) ? 1 : 0;
        }

        public String toString() {
            return this.a.toUpperCase() + ";" + this.b + ";" + this.c;
        }
    }

    public interface b {
        void a(boolean z, String str, String str2, String str3);
    }

    private d(Context context) {
        this.c = context;
        if (this.f == null) {
            try {
                if (Build.VERSION.SDK_INT > 18) {
                    this.f = ((BluetoothManager) context.getSystemService("bluetooth")).getAdapter();
                    this.g = this.c.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
                    return;
                }
                this.f = BluetoothAdapter.getDefaultAdapter();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static d a(Context context) {
        if (b == null) {
            b = new d(context);
        }
        return b;
    }

    private String a(List<a> list, int i2) {
        StringBuilder sb = new StringBuilder();
        Collections.sort(list);
        sb.append(list.get(0).toString());
        int i3 = 1;
        while (i3 < list.size() && i3 < i2) {
            sb.append(LogUtils.VERTICAL);
            sb.append(list.get(i3).toString());
            i3++;
        }
        return sb.toString();
    }

    public static String a(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i2 = 0; i2 < bArr.length; i2++) {
            byte b2 = bArr[i2] & UByte.MAX_VALUE;
            int i3 = i2 * 2;
            char[] cArr2 = a;
            cArr[i3] = cArr2[b2 >>> 4];
            cArr[i3 + 1] = cArr2[b2 & 15];
        }
        return new String(cArr);
    }

    /* access modifiers changed from: private */
    public void a(HashMap<String, ScanResult> hashMap) {
        ArrayList<ScanResult> arrayList = new ArrayList<>(hashMap.values());
        ArrayList arrayList2 = new ArrayList();
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        HashMap hashMap4 = new HashMap();
        ArrayList arrayList3 = new ArrayList();
        for (ScanResult scanResult : arrayList) {
            arrayList3.add(new a(scanResult.getDevice().getAddress().replaceAll(LogUtils.COLON, ""), scanResult.getRssi(), scanResult.getTimestampNanos()));
            if (this.d) {
                scanResult.getScanRecord().getAdvertiseFlags();
                byte[] bytes = scanResult.getScanRecord().getBytes();
                if (bytes.length >= 26) {
                    String a2 = a(Arrays.copyOfRange(bytes, 9, 25));
                    arrayList2.add(a2);
                    hashMap2.put(a2, scanResult.getDevice().getName());
                    hashMap3.put(a2, a(Arrays.copyOfRange(bytes, 0, 9)));
                    if (hashMap4.get(a2) == null) {
                        hashMap4.put(a2, 0);
                    }
                    hashMap4.put(a2, Integer.valueOf(((Integer) hashMap4.get(a2)).intValue() + 1));
                }
            }
        }
        String str = null;
        int i2 = 0;
        for (String str2 : hashMap4.keySet()) {
            if (((Integer) hashMap4.get(str2)).intValue() > i2) {
                i2 = ((Integer) hashMap4.get(str2)).intValue();
                str = str2;
            }
        }
        boolean z = i2 > 3;
        b bVar = this.h;
        if (bVar != null && this.d) {
            bVar.a(z, str, (String) hashMap2.get(str), (String) hashMap3.get(str));
            this.d = false;
        }
        if (arrayList3.size() > 3) {
            this.j = a((List<a>) arrayList3, 32);
            this.k = System.currentTimeMillis();
        }
        if (this.i) {
            a(true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0069 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean h() {
        /*
            r7 = this;
            java.lang.String r0 = ""
            java.io.File r1 = new java.io.File
            android.content.Context r2 = r7.c
            java.io.File r2 = r2.getCacheDir()
            java.lang.String r3 = "ibct"
            r1.<init>(r2, r3)
            boolean r2 = r1.exists()
            r3 = 0
            if (r2 != 0) goto L_0x0017
            return r3
        L_0x0017:
            r2 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0040 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ Exception -> 0x0040 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x0040 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0040 }
            r1 = r2
            r2 = r0
        L_0x0024:
            java.lang.String r1 = r4.readLine()     // Catch:{ Exception -> 0x003e }
            if (r1 == 0) goto L_0x003a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x003e }
            r5.<init>()     // Catch:{ Exception -> 0x003e }
            r5.append(r2)     // Catch:{ Exception -> 0x003e }
            r5.append(r1)     // Catch:{ Exception -> 0x003e }
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x003e }
            goto L_0x0024
        L_0x003a:
            r4.close()     // Catch:{ Exception -> 0x0040 }
            goto L_0x0048
        L_0x003e:
            r2 = move-exception
            goto L_0x0044
        L_0x0040:
            r1 = move-exception
            r6 = r2
            r2 = r1
            r1 = r6
        L_0x0044:
            r2.printStackTrace()
            r2 = r1
        L_0x0048:
            if (r2 == 0) goto L_0x006c
            java.lang.String r1 = r2.trim()
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0055
            goto L_0x006c
        L_0x0055:
            java.lang.Long r0 = java.lang.Long.valueOf(r2)     // Catch:{ Exception -> 0x006b }
            long r0 = r0.longValue()     // Catch:{ Exception -> 0x006b }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x006b }
            long r4 = r4 - r0
            r0 = 259200(0x3f480, double:1.28062E-318)
            int r2 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r2 >= 0) goto L_0x006c
            r0 = 1
            return r0
        L_0x006b:
            r0 = move-exception
        L_0x006c:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.d.h():boolean");
    }

    private void i() {
        try {
            FileWriter fileWriter = new FileWriter(new File(this.c.getCacheDir(), "ibct"));
            fileWriter.write(System.currentTimeMillis() + "");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e2) {
        }
    }

    public void a(boolean z) {
        if (this.f != null) {
            try {
                if (Build.VERSION.SDK_INT < 21) {
                    return;
                }
                if (z) {
                    this.o = new f(this);
                    this.f.getBluetoothLeScanner().startScan((ScanCallback) this.o);
                    this.m.postDelayed(this.n, 3000);
                    this.d = true;
                    return;
                }
                if (this.h != null) {
                    this.f.getBluetoothLeScanner().stopScan((ScanCallback) this.o);
                }
                this.d = false;
            } catch (Exception e2) {
            }
        }
    }

    public boolean a() {
        BluetoothAdapter bluetoothAdapter = this.f;
        if (bluetoothAdapter != null && this.g) {
            try {
                return bluetoothAdapter.isEnabled();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public boolean a(b bVar) {
        if (this.d || this.e) {
            return false;
        }
        this.e = true;
        if (!a() || h()) {
            return false;
        }
        i();
        this.h = bVar;
        a(true);
        return true;
    }

    public boolean b() {
        if (!a()) {
            return false;
        }
        a(true);
        this.i = true;
        return true;
    }

    public void c() {
        this.e = false;
        this.d = false;
    }

    public void d() {
        this.i = false;
    }

    public String e() {
        return this.j;
    }

    public long f() {
        return this.k;
    }

    public boolean g() {
        return System.currentTimeMillis() - this.k <= 20000;
    }
}
