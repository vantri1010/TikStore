package com.baidu.location.indoor.mapversion.c;

import android.content.Context;
import android.location.Location;
import com.baidu.location.BDLocation;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.king.zxing.util.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

public class a {
    private static a a;
    /* access modifiers changed from: private */
    public c b;
    /* access modifiers changed from: private */
    public String c;
    /* access modifiers changed from: private */
    public boolean d = false;
    /* access modifiers changed from: private */
    public String e = "rn";
    private b f;
    private String g = CoordinateType.GCJ02;
    private HashMap<String, d> h = new HashMap<>();
    private HashMap<String, double[][]> i = new HashMap<>();
    private d j = null;

    /* renamed from: com.baidu.location.indoor.mapversion.c.a$a  reason: collision with other inner class name */
    public static class C0013a {
        public double a;
        public double b;
        public double c;
        public double d;
        public double e;
        public double f;
        public double g;
        public double h;

        public C0013a(String str) {
            a(str);
        }

        public void a(String str) {
            String[] split = str.trim().split("\\|");
            this.a = Double.valueOf(split[0]).doubleValue();
            this.b = Double.valueOf(split[1]).doubleValue();
            this.c = Double.valueOf(split[2]).doubleValue();
            this.d = Double.valueOf(split[3]).doubleValue();
            this.e = Double.valueOf(split[4]).doubleValue();
            this.f = Double.valueOf(split[5]).doubleValue();
            this.g = Double.valueOf(split[6]).doubleValue();
            this.h = Double.valueOf(split[7]).doubleValue();
        }
    }

    private class b extends Thread {
        private String b;
        private String c;

        public b(String str, String str2) {
            this.b = str;
            this.c = str2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:30:0x011d A[Catch:{ Exception -> 0x0127 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r7 = this;
                r0 = 0
                java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a r2 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = r2.e     // Catch:{ Exception -> 0x0127 }
                r1.<init>(r2)     // Catch:{ Exception -> 0x0127 }
                boolean r2 = r1.exists()     // Catch:{ Exception -> 0x0127 }
                if (r2 == 0) goto L_0x0018
                boolean r2 = r1.isDirectory()     // Catch:{ Exception -> 0x0127 }
                if (r2 != 0) goto L_0x001b
            L_0x0018:
                r1.mkdirs()     // Catch:{ Exception -> 0x0127 }
            L_0x001b:
                java.net.URL r2 = new java.net.URL     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = "http://loc.map.baidu.com/cfgs/indoorloc/indoorroadnet"
                r2.<init>(r3)     // Catch:{ Exception -> 0x0127 }
                java.net.URLConnection r2 = r2.openConnection()     // Catch:{ Exception -> 0x0127 }
                java.net.HttpURLConnection r2 = (java.net.HttpURLConnection) r2     // Catch:{ Exception -> 0x0127 }
                r3 = 1
                r2.setDoInput(r3)     // Catch:{ Exception -> 0x0127 }
                r2.setDoOutput(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = "POST"
                r2.setRequestMethod(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = "Accept-encoding"
                java.lang.String r4 = "gzip"
                r2.addRequestProperty(r3, r4)     // Catch:{ Exception -> 0x0127 }
                java.io.OutputStream r3 = r2.getOutputStream()     // Catch:{ Exception -> 0x0127 }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0127 }
                r4.<init>()     // Catch:{ Exception -> 0x0127 }
                java.lang.String r5 = "bldg="
                r4.append(r5)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r5 = r7.b     // Catch:{ Exception -> 0x0127 }
                r4.append(r5)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r5 = "&md5="
                r4.append(r5)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r5 = r7.c     // Catch:{ Exception -> 0x0127 }
                if (r5 != 0) goto L_0x005a
                java.lang.String r5 = "null"
                goto L_0x005c
            L_0x005a:
                java.lang.String r5 = r7.c     // Catch:{ Exception -> 0x0127 }
            L_0x005c:
                r4.append(r5)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0127 }
                byte[] r4 = r4.getBytes()     // Catch:{ Exception -> 0x0127 }
                r3.write(r4)     // Catch:{ Exception -> 0x0127 }
                int r3 = r2.getResponseCode()     // Catch:{ Exception -> 0x0127 }
                r4 = 0
                r5 = 200(0xc8, float:2.8E-43)
                if (r3 != r5) goto L_0x00ce
                java.lang.String r3 = "Hash"
                java.lang.String r3 = r2.getHeaderField(r3)     // Catch:{ Exception -> 0x0127 }
                java.io.InputStream r2 = r2.getInputStream()     // Catch:{ Exception -> 0x0127 }
                java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a r5 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                java.lang.String r6 = r7.b     // Catch:{ Exception -> 0x0127 }
                java.lang.String r5 = r5.a((java.lang.String) r6, (java.lang.String) r3)     // Catch:{ Exception -> 0x0127 }
                r4.<init>(r1, r5)     // Catch:{ Exception -> 0x0127 }
                java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0127 }
                r1.<init>(r4)     // Catch:{ Exception -> 0x0127 }
                r5 = 1024(0x400, float:1.435E-42)
                byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x0127 }
            L_0x0093:
                int r6 = r2.read(r5)     // Catch:{ Exception -> 0x0127 }
                if (r6 < 0) goto L_0x009d
                r1.write(r5, r0, r6)     // Catch:{ Exception -> 0x0127 }
                goto L_0x0093
            L_0x009d:
                r1.flush()     // Catch:{ Exception -> 0x0127 }
                r1.close()     // Catch:{ Exception -> 0x0127 }
                java.lang.String r1 = com.baidu.location.indoor.mapversion.c.a.a((java.io.File) r4)     // Catch:{ Exception -> 0x0127 }
                boolean r1 = r1.equalsIgnoreCase(r3)     // Catch:{ Exception -> 0x0127 }
                if (r1 == 0) goto L_0x00c7
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = r7.b     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = r7.c     // Catch:{ Exception -> 0x0127 }
                r1.b((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = r7.b     // Catch:{ Exception -> 0x0127 }
                java.lang.String unused = r1.c = r2     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                boolean r1 = r1.d()     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = "OK"
                r4 = r2
                goto L_0x0115
            L_0x00c7:
                java.lang.String r1 = "Download failed"
                r4.delete()     // Catch:{ Exception -> 0x0127 }
                r4 = r1
                goto L_0x0114
            L_0x00ce:
                r1 = 304(0x130, float:4.26E-43)
                if (r3 != r1) goto L_0x00fd
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = r7.b     // Catch:{ Exception -> 0x0127 }
                java.lang.String unused = r1.c = r2     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                boolean r1 = r1.d()     // Catch:{ Exception -> 0x0127 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0127 }
                r2.<init>()     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = "No roadnet update for "
                r2.append(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = r7.b     // Catch:{ Exception -> 0x0127 }
                r2.append(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = ","
                r2.append(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r3 = r7.c     // Catch:{ Exception -> 0x0127 }
                r2.append(r3)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r4 = r2.toString()     // Catch:{ Exception -> 0x0127 }
                goto L_0x0115
            L_0x00fd:
                r1 = 204(0xcc, float:2.86E-43)
                if (r3 != r1) goto L_0x0114
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0127 }
                r1.<init>()     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = "Not found bldg "
                r1.append(r2)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r2 = r7.b     // Catch:{ Exception -> 0x0127 }
                r1.append(r2)     // Catch:{ Exception -> 0x0127 }
                java.lang.String r4 = r1.toString()     // Catch:{ Exception -> 0x0127 }
            L_0x0114:
                r1 = 0
            L_0x0115:
                com.baidu.location.indoor.mapversion.c.a r2 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a$c r2 = r2.b     // Catch:{ Exception -> 0x0127 }
                if (r2 == 0) goto L_0x012b
                com.baidu.location.indoor.mapversion.c.a r2 = com.baidu.location.indoor.mapversion.c.a.this     // Catch:{ Exception -> 0x0127 }
                com.baidu.location.indoor.mapversion.c.a$c r2 = r2.b     // Catch:{ Exception -> 0x0127 }
                r2.a(r1, r4)     // Catch:{ Exception -> 0x0127 }
                goto L_0x012b
            L_0x0127:
                r1 = move-exception
                r1.printStackTrace()
            L_0x012b:
                com.baidu.location.indoor.mapversion.c.a r1 = com.baidu.location.indoor.mapversion.c.a.this
                boolean unused = r1.d = r0
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.mapversion.c.a.b.run():void");
        }
    }

    public interface c {
        void a(boolean z, String str);
    }

    public static class d implements Serializable {
        public String a;
        public String b;
        public C0013a c;
        public C0013a d;
        public C0013a e;
        public C0013a f = this.d;
        public short[][] g;
        private String h = CoordinateType.GCJ02;

        public d(String str) {
            this.a = str;
        }

        public double a(double d2) {
            return (d2 + this.f.d) * this.f.c;
        }

        public C0013a a() {
            return this.f;
        }

        public void a(String str) {
            C0013a aVar;
            if (str != null) {
                String lowerCase = str.toLowerCase();
                this.h = lowerCase;
                if (lowerCase.startsWith(CoordinateType.WGS84)) {
                    aVar = this.c;
                } else if (this.h.startsWith(BDLocation.BDLOCATION_GCJ02_TO_BD09)) {
                    aVar = this.e;
                } else if (this.h.startsWith(CoordinateType.GCJ02)) {
                    aVar = this.d;
                } else {
                    return;
                }
                this.f = aVar;
            }
        }

        public double b(double d2) {
            return (d2 + this.f.f) * this.f.e;
        }

        public void b(String str) {
            String[] split = str.split("\\t");
            this.b = split[1];
            this.c = new C0013a(split[2]);
            this.e = new C0013a(split[3]);
            C0013a aVar = new C0013a(split[4]);
            this.d = aVar;
            this.f = aVar;
            int i = (int) aVar.g;
            int[] iArr = new int[2];
            iArr[1] = (int) this.f.h;
            iArr[0] = i;
            this.g = (short[][]) Array.newInstance(short.class, iArr);
            for (int i2 = 0; ((double) i2) < this.f.g; i2++) {
                for (int i3 = 0; ((double) i3) < this.f.h; i3++) {
                    this.g[i2][i3] = (short) (split[5].charAt((((int) this.f.h) * i2) + i3) - '0');
                }
            }
        }

        public double c(double d2) {
            return (d2 / this.f.c) - this.f.d;
        }

        public double d(double d2) {
            return (d2 / this.f.e) - this.f.f;
        }
    }

    private a(Context context) {
        this.e = new File(context.getCacheDir(), this.e).getAbsolutePath();
    }

    public static a a() {
        return a;
    }

    public static a a(Context context) {
        if (a == null) {
            a = new a(context);
        }
        return a;
    }

    public static String a(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MappedByteBuffer map = fileInputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(map);
            String bigInteger = new BigInteger(1, instance.digest()).toString(16);
            fileInputStream.close();
            for (int length = 32 - bigInteger.length(); length > 0; length--) {
                bigInteger = "0" + bigInteger;
            }
            return bigInteger;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: private */
    public String a(String str, String str2) {
        return d(str) + "_" + str2;
    }

    /* access modifiers changed from: private */
    public void b(String str, String str2) {
        try {
            File file = new File(this.e + "/" + a(str, str2));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public String d(String str) {
        return str;
    }

    /* access modifiers changed from: private */
    public boolean d() {
        String str = this.c;
        if (str == null) {
            return false;
        }
        File f2 = f(str);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (!d.a(f2, byteArrayOutputStream)) {
            return false;
        }
        this.h.clear();
        this.i.clear();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (readLine.split("\\t")[1].split("_")[0].equals("geo")) {
                    j(readLine);
                } else {
                    d dVar = new d(this.c);
                    dVar.b(readLine);
                    dVar.a(this.g);
                    this.h.put(dVar.b.toLowerCase(), dVar);
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        bufferedReader.close();
        return true;
    }

    private String e(String str) {
        File file = new File(this.e);
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles(new b(this, str));
            int i2 = 0;
            if (listFiles == null || listFiles.length != 1) {
                while (listFiles != null && i2 < listFiles.length) {
                    listFiles[i2].delete();
                    i2++;
                }
            } else {
                String[] split = listFiles[0].getName().split("_");
                if (split.length < 2) {
                    return null;
                }
                return split[1];
            }
        }
        return null;
    }

    private File f(String str) {
        String e2 = e(str);
        return new File(this.e + "/" + a(str, e2));
    }

    private boolean g(String str) {
        File f2 = f(str);
        return f2.exists() && f2.length() > 0;
    }

    private boolean h(String str) {
        return System.currentTimeMillis() - f(str).lastModified() > 1296000000;
    }

    private ArrayList<Double> i(String str) {
        double d2;
        ArrayList<Double> arrayList = new ArrayList<>();
        int i2 = 0;
        while (i2 < str.length()) {
            if (str.charAt(i2) == ',') {
                int i3 = i2 + 1;
                i2 += 2;
                d2 = (double) Integer.valueOf(str.substring(i3, i2)).intValue();
            } else if (str.charAt(i2) == '.') {
                int i4 = i2 + 1;
                i2 += 4;
                d2 = Double.valueOf(str.substring(i4, i2)).doubleValue();
            } else {
                int i5 = i2 + 2;
                double intValue = (double) Integer.valueOf(str.substring(i2, i5)).intValue();
                i2 = i5;
                d2 = intValue;
            }
            arrayList.add(Double.valueOf(d2));
        }
        return arrayList;
    }

    private void j(String str) {
        String[] split = str.split("\\t");
        String lowerCase = split[1].split("_")[1].toLowerCase();
        try {
            if (this.h.containsKey(lowerCase)) {
                ArrayList<Double> i2 = i(split[5]);
                int length = this.h.get(lowerCase).g.length;
                int length2 = this.h.get(lowerCase).g[0].length;
                int[] iArr = new int[2];
                iArr[1] = length2;
                iArr[0] = length;
                double[][] dArr = (double[][]) Array.newInstance(double.class, iArr);
                int i3 = 0;
                for (int i4 = 0; i4 < length; i4++) {
                    for (int i5 = 0; i5 < length2; i5++) {
                        if (this.h.get(lowerCase).g[i4][i5] <= 0 || this.h.get(lowerCase).g[i4][i5] == 9) {
                            dArr[i4][i5] = 0.0d;
                        } else {
                            dArr[i4][i5] = i2.get(i3).doubleValue();
                            i3++;
                        }
                    }
                }
                this.i.put(lowerCase.toLowerCase(), dArr);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void k(String str) {
        if (!this.d) {
            this.d = true;
            b bVar = new b(str, e(str));
            this.f = bVar;
            bVar.start();
        }
    }

    public void a(double d2, double d3) {
        double d4 = d2;
        double d5 = d3;
        if (this.j == null) {
            float[] fArr = new float[2];
            float[] fArr2 = new float[2];
            double d6 = d2;
            Location.distanceBetween(d3, d6, d3, d4 + 0.01d, fArr);
            Location.distanceBetween(d3, d6, d5 + 0.01d, d2, fArr2);
            d dVar = new d("outdoor");
            this.j = dVar;
            dVar.b = "out";
            d dVar2 = this.j;
            dVar2.f = new C0013a("0|1.0|" + (((double) fArr[0]) / 0.01d) + LogUtils.VERTICAL + (-d4) + LogUtils.VERTICAL + (((double) fArr2[0]) / 0.01d) + LogUtils.VERTICAL + (-d3) + "|0|0");
        }
    }

    public void a(String str) {
        this.g = str;
    }

    public void a(String str, c cVar) {
        String str2 = this.c;
        if (str2 == null || !str.equals(str2)) {
            this.b = cVar;
            if (!g(str) || h(str)) {
                k(str);
                return;
            }
            this.c = str;
            d();
            c cVar2 = this.b;
            if (cVar2 != null) {
                cVar2.a(true, "OK");
            }
        }
    }

    public d b(String str) {
        return this.h.get(str.toLowerCase());
    }

    public void b() {
        this.h.clear();
        this.i.clear();
        this.c = null;
        this.d = false;
    }

    public d c() {
        return this.j;
    }

    public double[][] c(String str) {
        return this.i.get(str.toLowerCase());
    }
}
