package com.baidu.location.indoor;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import com.baidu.location.b.v;
import com.baidu.location.g.b;
import com.baidu.location.g.e;
import com.baidu.location.g.k;
import io.reactivex.annotations.SchedulerSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class a extends e {
    private static HashMap<String, Long> a = new HashMap<>();
    private static Object v = new Object();
    private static a w = null;
    private String b = "http://loc.map.baidu.com/indoorlocbuildinginfo.php";
    private final SimpleDateFormat c = new SimpleDateFormat("yyyyMM");
    private Context d;
    private boolean e;
    /* access modifiers changed from: private */
    public String f;
    private HashSet<String> q;
    private C0010a r;
    /* access modifiers changed from: private */
    public String s = null;
    private Handler t;
    private Runnable u;

    /* renamed from: com.baidu.location.indoor.a$a  reason: collision with other inner class name */
    public interface C0010a {
        void a(boolean z);
    }

    public a(Context context) {
        this.d = context;
        this.q = new HashSet<>();
        this.e = false;
        this.k = new HashMap();
        this.t = new Handler();
        this.u = new b(this);
    }

    private String a(Date date) {
        File file = new File(this.d.getCacheDir(), k.a((this.f + this.c.format(date)).getBytes(), false));
        if (!file.isFile()) {
            return null;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String str = "";
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str = str + readLine + "\n";
            }
            bufferedReader.close();
            if (!str.equals("")) {
                return new String(Base64.decode(str.getBytes(), 0));
            }
        } catch (Exception e2) {
        }
        return null;
    }

    private Date d() {
        Calendar instance = Calendar.getInstance();
        instance.add(2, -1);
        return instance.getTime();
    }

    private void d(String str) {
        for (String lowerCase : str.split(",")) {
            this.q.add(lowerCase.toLowerCase());
        }
    }

    private void e() {
        try {
            Date d2 = d();
            File file = new File(this.d.getCacheDir(), k.a((this.f + this.c.format(d2)).getBytes(), false));
            if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e2) {
        }
    }

    private void e(String str) {
        try {
            FileWriter fileWriter = new FileWriter(new File(this.d.getCacheDir(), k.a((this.f + this.c.format(new Date())).getBytes(), false)));
            fileWriter.write(new String(Base64.encode(str.getBytes(), 0), "UTF-8"));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e2) {
        }
    }

    private void f(String str) {
        try {
            FileWriter fileWriter = new FileWriter(new File(this.d.getCacheDir(), "buildings"), true);
            fileWriter.write(str + "\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void a() {
        this.h = this.b;
        this.k.clear();
        this.k.put("bid", SchedulerSupport.NONE);
        this.k.put("bldg", this.f);
        this.k.put("mb", Build.MODEL);
        this.k.put("msdk", "2.0");
        this.k.put("cuid", b.a().c);
        this.k.put("anchors", "v1");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(boolean r6) {
        /*
            r5 = this;
            java.lang.String r0 = "anchorinfo"
            r1 = 1
            r2 = 0
            if (r6 == 0) goto L_0x0045
            java.lang.String r6 = r5.j
            if (r6 == 0) goto L_0x0045
            java.lang.String r6 = r5.j     // Catch:{ Exception -> 0x0044 }
            java.lang.String r3 = new java.lang.String     // Catch:{ Exception -> 0x0044 }
            byte[] r6 = r6.getBytes()     // Catch:{ Exception -> 0x0044 }
            byte[] r6 = android.util.Base64.decode(r6, r2)     // Catch:{ Exception -> 0x0044 }
            r3.<init>(r6)     // Catch:{ Exception -> 0x0044 }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ Exception -> 0x0044 }
            r6.<init>(r3)     // Catch:{ Exception -> 0x0044 }
            boolean r3 = r6.has(r0)     // Catch:{ Exception -> 0x0044 }
            if (r3 == 0) goto L_0x0045
            java.lang.String r6 = r6.optString(r0)     // Catch:{ Exception -> 0x0044 }
            if (r6 == 0) goto L_0x0045
            java.lang.String r0 = ""
            boolean r0 = r6.equals(r0)     // Catch:{ Exception -> 0x0044 }
            if (r0 != 0) goto L_0x0045
            java.util.HashSet<java.lang.String> r0 = r5.q     // Catch:{ Exception -> 0x0044 }
            r0.clear()     // Catch:{ Exception -> 0x0044 }
            r5.d(r6)     // Catch:{ Exception -> 0x0044 }
            r5.e(r6)     // Catch:{ Exception -> 0x0044 }
            r5.e()     // Catch:{ Exception -> 0x0042 }
        L_0x0040:
            r6 = 1
            goto L_0x0046
        L_0x0042:
            r6 = move-exception
            goto L_0x0040
        L_0x0044:
            r6 = move-exception
        L_0x0045:
            r6 = 0
        L_0x0046:
            if (r6 != 0) goto L_0x005b
            java.lang.String r0 = r5.s
            if (r0 != 0) goto L_0x005b
            java.lang.String r0 = r5.f
            r5.s = r0
            android.os.Handler r0 = r5.t
            java.lang.Runnable r1 = r5.u
            r3 = 60000(0xea60, double:2.9644E-319)
            r0.postDelayed(r1, r3)
            goto L_0x007c
        L_0x005b:
            r0 = 0
            if (r6 == 0) goto L_0x0061
            r5.s = r0
            goto L_0x007c
        L_0x0061:
            java.lang.String r3 = r5.s
            r5.f(r3)
            r5.s = r0
            java.util.Date r0 = r5.d()
            java.lang.String r0 = r5.a((java.util.Date) r0)
            if (r0 == 0) goto L_0x007c
            r5.d(r0)
            com.baidu.location.indoor.a$a r0 = r5.r
            if (r0 == 0) goto L_0x007c
            r0.a(r1)
        L_0x007c:
            r5.e = r2
            com.baidu.location.indoor.a$a r0 = r5.r
            if (r0 == 0) goto L_0x0085
            r0.a(r6)
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.a.a(boolean):void");
    }

    public boolean a(String str) {
        String str2 = this.f;
        return str2 != null && str2.equalsIgnoreCase(str) && !this.q.isEmpty();
    }

    public boolean a(String str, C0010a aVar) {
        if (!this.e) {
            this.r = aVar;
            this.e = true;
            this.f = str;
            try {
                String a2 = a(new Date());
                if (a2 == null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (a.get(str) == null || currentTimeMillis - a.get(str).longValue() > 86400000) {
                        a.put(str, Long.valueOf(currentTimeMillis));
                        b(v.a().c());
                    }
                } else {
                    d(a2);
                    if (this.r != null) {
                        this.r.a(true);
                    }
                    this.e = false;
                }
            } catch (Exception e2) {
                this.e = false;
            }
        }
        return false;
    }

    public boolean b() {
        HashSet<String> hashSet = this.q;
        return hashSet != null && !hashSet.isEmpty();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r0 = r2.q;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean b(java.lang.String r3) {
        /*
            r2 = this;
            java.lang.String r0 = r2.f
            r1 = 0
            if (r0 == 0) goto L_0x001a
            java.util.HashSet<java.lang.String> r0 = r2.q
            if (r0 == 0) goto L_0x001a
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0010
            goto L_0x001a
        L_0x0010:
            java.util.HashSet<java.lang.String> r0 = r2.q
            boolean r3 = r0.contains(r3)
            if (r3 == 0) goto L_0x001a
            r3 = 1
            return r3
        L_0x001a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.a.b(java.lang.String):boolean");
    }

    public void c() {
        this.f = null;
        this.q.clear();
    }
}
