package com.baidu.lbsapi.auth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.baidu.android.bbalbs.common.util.CommonParam;
import com.baidu.lbsapi.auth.c;
import com.coremedia.iso.boxes.AuthorBox;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class LBSAuthManager {
    public static final int CODE_AUTHENTICATE_SUCC = 0;
    public static final int CODE_AUTHENTICATING = 602;
    public static final int CODE_INNER_ERROR = -1;
    public static final int CODE_KEY_NOT_EXIST = 101;
    public static final int CODE_NETWORK_FAILED = -11;
    public static final int CODE_NETWORK_INVALID = -10;
    public static final int CODE_UNAUTHENTICATE = 601;
    public static final String VERSION = "1.0.23";
    /* access modifiers changed from: private */
    public static Context a;
    /* access modifiers changed from: private */
    public static m d = null;
    private static int e = 0;
    /* access modifiers changed from: private */
    public static Hashtable<String, LBSAuthManagerListener> f = new Hashtable<>();
    private static LBSAuthManager g;
    private c b = null;
    private e c = null;
    private boolean h = false;
    private final Handler i = new i(this, Looper.getMainLooper());

    private LBSAuthManager(Context context) {
        a = context;
        m mVar = d;
        if (mVar != null && !mVar.isAlive()) {
            d = null;
        }
        a.b("BaiduApiAuth SDK Version:1.0.23");
        d();
    }

    private int a(String str) {
        int i2 = -1;
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, -1);
            }
            i2 = jSONObject.getInt(NotificationCompat.CATEGORY_STATUS);
            if (jSONObject.has("current") && i2 == 0) {
                long j = jSONObject.getLong("current");
                long currentTimeMillis = System.currentTimeMillis();
                if (((double) (currentTimeMillis - j)) / 3600000.0d < 24.0d) {
                    if (this.h) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        if (!simpleDateFormat.format(Long.valueOf(currentTimeMillis)).equals(simpleDateFormat.format(Long.valueOf(j)))) {
                        }
                    }
                }
                i2 = CODE_UNAUTHENTICATE;
            }
            if (jSONObject.has("current") && i2 == 602) {
                if (((double) ((System.currentTimeMillis() - jSONObject.getLong("current")) / 1000)) > 180.0d) {
                    return CODE_UNAUTHENTICATE;
                }
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return i2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: java.io.InputStreamReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: java.io.InputStreamReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: java.io.InputStreamReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: java.io.InputStreamReader} */
    /* JADX WARNING: type inference failed for: r1v0, types: [java.io.InputStreamReader] */
    /* JADX WARNING: type inference failed for: r1v3, types: [java.io.InputStreamReader] */
    /* JADX WARNING: type inference failed for: r1v16 */
    /* JADX WARNING: type inference failed for: r1v17 */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0079, code lost:
        if (r6 == null) goto L_0x008d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x008a, code lost:
        if (r6 == null) goto L_0x008d;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0087  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(int r6) throws java.io.IOException {
        /*
            r5 = this;
            r0 = 0
            java.io.File r1 = new java.io.File     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            r2.<init>()     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.lang.String r3 = "/proc/"
            r2.append(r3)     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            r2.append(r6)     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.lang.String r6 = "/cmdline"
            r2.append(r6)     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.lang.String r6 = r2.toString()     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            r1.<init>(r6)     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            r6.<init>(r1)     // Catch:{ FileNotFoundException -> 0x007c, IOException -> 0x006b, all -> 0x0056 }
            java.io.InputStreamReader r1 = new java.io.InputStreamReader     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004b }
            r1.<init>(r6)     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004b }
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0048, IOException -> 0x0045, all -> 0x0040 }
            r2.<init>(r1)     // Catch:{ FileNotFoundException -> 0x0048, IOException -> 0x0045, all -> 0x0040 }
            java.lang.String r0 = r2.readLine()     // Catch:{ FileNotFoundException -> 0x003e, IOException -> 0x003c, all -> 0x003a }
            r2.close()
            r1.close()
        L_0x0035:
            r6.close()
            goto L_0x008d
        L_0x003a:
            r0 = move-exception
            goto L_0x005b
        L_0x003c:
            r3 = move-exception
            goto L_0x006f
        L_0x003e:
            r3 = move-exception
            goto L_0x0080
        L_0x0040:
            r2 = move-exception
            r4 = r2
            r2 = r0
            r0 = r4
            goto L_0x005b
        L_0x0045:
            r2 = move-exception
            r2 = r0
            goto L_0x006f
        L_0x0048:
            r2 = move-exception
            r2 = r0
            goto L_0x0080
        L_0x004b:
            r1 = move-exception
            r2 = r0
            r0 = r1
            r1 = r2
            goto L_0x005b
        L_0x0050:
            r1 = move-exception
            r1 = r0
            goto L_0x006e
        L_0x0053:
            r1 = move-exception
            r1 = r0
            goto L_0x007f
        L_0x0056:
            r6 = move-exception
            r1 = r0
            r2 = r1
            r0 = r6
            r6 = r2
        L_0x005b:
            if (r2 == 0) goto L_0x0060
            r2.close()
        L_0x0060:
            if (r1 == 0) goto L_0x0065
            r1.close()
        L_0x0065:
            if (r6 == 0) goto L_0x006a
            r6.close()
        L_0x006a:
            throw r0
        L_0x006b:
            r6 = move-exception
            r6 = r0
            r1 = r6
        L_0x006e:
            r2 = r1
        L_0x006f:
            if (r2 == 0) goto L_0x0074
            r2.close()
        L_0x0074:
            if (r1 == 0) goto L_0x0079
            r1.close()
        L_0x0079:
            if (r6 == 0) goto L_0x008d
            goto L_0x0035
        L_0x007c:
            r6 = move-exception
            r6 = r0
            r1 = r6
        L_0x007f:
            r2 = r1
        L_0x0080:
            if (r2 == 0) goto L_0x0085
            r2.close()
        L_0x0085:
            if (r1 == 0) goto L_0x008a
            r1.close()
        L_0x008a:
            if (r6 == 0) goto L_0x008d
            goto L_0x0035
        L_0x008d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.lbsapi.auth.LBSAuthManager.a(int):java.lang.String");
    }

    private String a(Context context) {
        String str;
        try {
            str = a(Process.myPid());
        } catch (IOException e2) {
            str = null;
        }
        return str != null ? str : a.getPackageName();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003b, code lost:
        if (r6.equals(r1) != false) goto L_0x003d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(android.content.Context r6, java.lang.String r7) {
        /*
            r5 = this;
            java.lang.String r0 = "无法在AndroidManifest.xml中获取com.baidu.android.lbs.API_KEY的值"
            java.lang.String r1 = ""
            java.lang.String r2 = r6.getPackageName()
            r3 = 101(0x65, float:1.42E-43)
            android.content.pm.PackageManager r6 = r6.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0053 }
            r4 = 128(0x80, float:1.794E-43)
            android.content.pm.ApplicationInfo r6 = r6.getApplicationInfo(r2, r4)     // Catch:{ NameNotFoundException -> 0x0053 }
            android.os.Bundle r2 = r6.metaData     // Catch:{ NameNotFoundException -> 0x0053 }
            if (r2 != 0) goto L_0x002d
            java.util.Hashtable<java.lang.String, com.baidu.lbsapi.auth.LBSAuthManagerListener> r6 = f     // Catch:{ NameNotFoundException -> 0x0053 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ NameNotFoundException -> 0x0053 }
            com.baidu.lbsapi.auth.LBSAuthManagerListener r6 = (com.baidu.lbsapi.auth.LBSAuthManagerListener) r6     // Catch:{ NameNotFoundException -> 0x0053 }
            if (r6 == 0) goto L_0x0065
            java.lang.String r2 = "AndroidManifest.xml的application中没有meta-data标签"
            java.lang.String r2 = com.baidu.lbsapi.auth.ErrorMessage.a(r3, r2)     // Catch:{ NameNotFoundException -> 0x0053 }
            r6.onAuthResult(r3, r2)     // Catch:{ NameNotFoundException -> 0x0053 }
            goto L_0x0065
        L_0x002d:
            android.os.Bundle r6 = r6.metaData     // Catch:{ NameNotFoundException -> 0x0053 }
            java.lang.String r2 = "com.baidu.lbsapi.API_KEY"
            java.lang.String r6 = r6.getString(r2)     // Catch:{ NameNotFoundException -> 0x0053 }
            if (r6 == 0) goto L_0x003d
            boolean r1 = r6.equals(r1)     // Catch:{ NameNotFoundException -> 0x0050 }
            if (r1 == 0) goto L_0x004e
        L_0x003d:
            java.util.Hashtable<java.lang.String, com.baidu.lbsapi.auth.LBSAuthManagerListener> r1 = f     // Catch:{ NameNotFoundException -> 0x0050 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ NameNotFoundException -> 0x0050 }
            com.baidu.lbsapi.auth.LBSAuthManagerListener r1 = (com.baidu.lbsapi.auth.LBSAuthManagerListener) r1     // Catch:{ NameNotFoundException -> 0x0050 }
            if (r1 == 0) goto L_0x004e
            java.lang.String r2 = com.baidu.lbsapi.auth.ErrorMessage.a(r3, r0)     // Catch:{ NameNotFoundException -> 0x0050 }
            r1.onAuthResult(r3, r2)     // Catch:{ NameNotFoundException -> 0x0050 }
        L_0x004e:
            r1 = r6
            goto L_0x0065
        L_0x0050:
            r1 = move-exception
            r1 = r6
            goto L_0x0054
        L_0x0053:
            r6 = move-exception
        L_0x0054:
            java.util.Hashtable<java.lang.String, com.baidu.lbsapi.auth.LBSAuthManagerListener> r6 = f
            java.lang.Object r6 = r6.get(r7)
            com.baidu.lbsapi.auth.LBSAuthManagerListener r6 = (com.baidu.lbsapi.auth.LBSAuthManagerListener) r6
            if (r6 == 0) goto L_0x0065
            java.lang.String r7 = com.baidu.lbsapi.auth.ErrorMessage.a(r3, r0)
            r6.onAuthResult(r3, r7)
        L_0x0065:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.lbsapi.auth.LBSAuthManager.a(android.content.Context, java.lang.String):java.lang.String");
    }

    /* access modifiers changed from: private */
    public synchronized void a(String str, String str2) {
        if (str == null) {
            str = e();
        }
        Message obtainMessage = this.i.obtainMessage();
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, -1);
            }
            if (!jSONObject.has("current")) {
                jSONObject.put("current", System.currentTimeMillis());
            }
            c(jSONObject.toString());
            if (jSONObject.has("current")) {
                jSONObject.remove("current");
            }
            obtainMessage.what = jSONObject.getInt(NotificationCompat.CATEGORY_STATUS);
            obtainMessage.obj = jSONObject.toString();
            Bundle bundle = new Bundle();
            bundle.putString("listenerKey", str2);
            obtainMessage.setData(bundle);
            this.i.sendMessage(obtainMessage);
        } catch (JSONException e2) {
            e2.printStackTrace();
            obtainMessage.what = -1;
            obtainMessage.obj = new JSONObject();
            Bundle bundle2 = new Bundle();
            bundle2.putString("listenerKey", str2);
            obtainMessage.setData(bundle2);
            this.i.sendMessage(obtainMessage);
        }
        if (d != null) {
            d.c();
        }
        e--;
        a.a("httpRequest called mAuthCounter-- = " + e);
        if (e == 0 && d != null) {
            d.a();
            d = null;
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z, String str, Hashtable<String, String> hashtable, String str2) {
        String str3;
        String str4;
        int i2;
        StringBuilder sb;
        String a2 = a(a, str2);
        if (a2 != null && !a2.equals("")) {
            HashMap hashMap = new HashMap();
            hashMap.put(ImagesContract.URL, "https://api.map.baidu.com/sdkcs/verify");
            a.a("url:https://api.map.baidu.com/sdkcs/verify");
            hashMap.put("output", "json");
            hashMap.put("ak", a2);
            a.a("ak:" + a2);
            hashMap.put("mcode", b.a(a));
            hashMap.put("from", "lbs_yunsdk");
            if (hashtable != null && hashtable.size() > 0) {
                for (Map.Entry next : hashtable.entrySet()) {
                    String str5 = (String) next.getKey();
                    String str6 = (String) next.getValue();
                    if (!TextUtils.isEmpty(str5) && !TextUtils.isEmpty(str6)) {
                        hashMap.put(str5, str6);
                    }
                }
            }
            try {
                str3 = CommonParam.getCUID(a);
            } catch (Exception e2) {
                a.a("get cuid failed");
                e2.printStackTrace();
                str3 = "";
            }
            a.a("cuid:" + str3);
            if (!TextUtils.isEmpty(str3)) {
                hashMap.put("cuid", str3);
            } else {
                hashMap.put("cuid", "");
            }
            hashMap.put("pcn", a.getPackageName());
            hashMap.put("version", VERSION);
            hashMap.put("macaddr", "");
            try {
                str4 = b.a();
            } catch (Exception e3) {
                str4 = "";
            }
            if (!TextUtils.isEmpty(str4)) {
                hashMap.put("language", str4);
            } else {
                hashMap.put("language", "");
            }
            if (z) {
                if (z) {
                    sb = new StringBuilder();
                    i2 = 1;
                } else {
                    sb = new StringBuilder();
                    i2 = 0;
                }
                sb.append(i2);
                sb.append("");
                hashMap.put("force", sb.toString());
            }
            if (str == null) {
                hashMap.put("from_service", "");
            } else {
                hashMap.put("from_service", str);
            }
            c cVar = new c(a);
            this.b = cVar;
            cVar.a((HashMap<String, String>) hashMap, (c.a<String>) new k(this, str2));
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z, String str, Hashtable<String, String> hashtable, String[] strArr, String str2) {
        String str3;
        String str4;
        int i2;
        StringBuilder sb;
        String a2 = a(a, str2);
        if (a2 != null && !a2.equals("")) {
            HashMap hashMap = new HashMap();
            hashMap.put(ImagesContract.URL, "https://api.map.baidu.com/sdkcs/verify");
            hashMap.put("output", "json");
            hashMap.put("ak", a2);
            hashMap.put("from", "lbs_yunsdk");
            if (hashtable != null && hashtable.size() > 0) {
                for (Map.Entry next : hashtable.entrySet()) {
                    String str5 = (String) next.getKey();
                    String str6 = (String) next.getValue();
                    if (!TextUtils.isEmpty(str5) && !TextUtils.isEmpty(str6)) {
                        hashMap.put(str5, str6);
                    }
                }
            }
            try {
                str3 = CommonParam.getCUID(a);
            } catch (Exception e2) {
                str3 = "";
            }
            if (!TextUtils.isEmpty(str3)) {
                hashMap.put("cuid", str3);
            } else {
                hashMap.put("cuid", "");
            }
            hashMap.put("pcn", a.getPackageName());
            hashMap.put("version", VERSION);
            hashMap.put("macaddr", "");
            try {
                str4 = b.a();
            } catch (Exception e3) {
                str4 = "";
            }
            if (!TextUtils.isEmpty(str4)) {
                hashMap.put("language", str4);
            } else {
                hashMap.put("language", "");
            }
            if (z) {
                if (z) {
                    sb = new StringBuilder();
                    i2 = 1;
                } else {
                    sb = new StringBuilder();
                    i2 = 0;
                }
                sb.append(i2);
                sb.append("");
                hashMap.put("force", sb.toString());
            }
            if (str == null) {
                hashMap.put("from_service", "");
            } else {
                hashMap.put("from_service", str);
            }
            e eVar = new e(a);
            this.c = eVar;
            eVar.a(hashMap, strArr, new l(this, str2));
        }
    }

    /* access modifiers changed from: private */
    public boolean b(String str) {
        String str2;
        String a2 = a(a, str);
        try {
            JSONObject jSONObject = new JSONObject(e());
            if (!jSONObject.has("ak")) {
                return true;
            }
            str2 = jSONObject.getString("ak");
            return (a2 == null || str2 == null || a2.equals(str2)) ? false : true;
        } catch (JSONException e2) {
            e2.printStackTrace();
            str2 = "";
        }
    }

    private void c(String str) {
        Context context = a;
        context.getSharedPreferences("authStatus_" + a(a), 0).edit().putString(NotificationCompat.CATEGORY_STATUS, str).commit();
    }

    private void d() {
        synchronized (LBSAuthManager.class) {
            if (d == null) {
                m mVar = new m(AuthorBox.TYPE);
                d = mVar;
                mVar.start();
                while (d.a == null) {
                    try {
                        a.a("wait for create auth thread.");
                        Thread.sleep(3);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    private String e() {
        Context context = a;
        return context.getSharedPreferences("authStatus_" + a(a), 0).getString(NotificationCompat.CATEGORY_STATUS, "{\"status\":601}");
    }

    public static LBSAuthManager getInstance(Context context) {
        if (g == null) {
            synchronized (LBSAuthManager.class) {
                if (g == null) {
                    g = new LBSAuthManager(context);
                }
            }
        } else if (context != null) {
            a = context;
        } else if (a.a) {
            a.c("input context is null");
            new RuntimeException("here").printStackTrace();
        }
        return g;
    }

    public int authenticate(boolean z, String str, Hashtable<String, String> hashtable, LBSAuthManagerListener lBSAuthManagerListener) {
        synchronized (LBSAuthManager.class) {
            boolean z2 = false;
            if (hashtable != null) {
                String str2 = hashtable.get("zero_auth");
                if (str2 != null) {
                    if (Integer.valueOf(str2).intValue() == 1) {
                        z2 = true;
                    }
                }
            }
            this.h = z2;
            String str3 = System.currentTimeMillis() + "";
            if (lBSAuthManagerListener != null) {
                f.put(str3, lBSAuthManagerListener);
            }
            String a2 = a(a, str3);
            if (a2 != null) {
                if (!a2.equals("")) {
                    e++;
                    a.a(" mAuthCounter  ++ = " + e);
                    String e2 = e();
                    a.a("getAuthMessage from cache:" + e2);
                    int a3 = a(e2);
                    if (a3 == 601) {
                        try {
                            c(new JSONObject().put(NotificationCompat.CATEGORY_STATUS, CODE_AUTHENTICATING).toString());
                        } catch (JSONException e3) {
                            e3.printStackTrace();
                        }
                    }
                    d();
                    if (d != null) {
                        if (d.a != null) {
                            a.a("mThreadLooper.mHandler = " + d.a);
                            d.a.post(new j(this, a3, z, str3, str, hashtable));
                            return a3;
                        }
                    }
                    return -1;
                }
            }
            return 101;
        }
    }

    public String getCUID() {
        Context context = a;
        if (context == null) {
            return "";
        }
        try {
            return CommonParam.getCUID(context);
        } catch (Exception e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public String getKey() {
        Context context = a;
        if (context == null) {
            return "";
        }
        try {
            return getPublicKey(context);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public String getMCode() {
        Context context = a;
        return context == null ? "" : b.a(context);
    }

    public String getPublicKey(Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString("com.baidu.lbsapi.API_KEY");
    }
}
