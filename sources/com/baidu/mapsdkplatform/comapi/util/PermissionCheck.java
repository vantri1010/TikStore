package com.baidu.mapsdkplatform.comapi.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.lbsapi.auth.LBSAuthManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import java.util.Hashtable;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.utils.RecvStatsLogKey;

public class PermissionCheck {
    public static int a = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    public static int b = 202;
    public static int c = 252;
    /* access modifiers changed from: private */
    public static final String d = PermissionCheck.class.getSimpleName();
    /* access modifiers changed from: private */
    public static Context e;
    /* access modifiers changed from: private */
    public static String f;
    private static Hashtable<String, String> g;
    private static LBSAuthManager h = null;
    private static LBSAuthManagerListener i = null;
    /* access modifiers changed from: private */
    public static c j = null;
    /* access modifiers changed from: private */
    public static int k = LBSAuthManager.CODE_UNAUTHENTICATE;

    private static class a implements LBSAuthManagerListener {
        private a() {
        }

        public void onAuthResult(int i, String str) {
            if (str == null) {
                Log.e(PermissionCheck.d, "The result is null");
                int permissionCheck = PermissionCheck.permissionCheck();
                String a = PermissionCheck.d;
                Log.d(a, "onAuthResult try permissionCheck result is: " + permissionCheck);
                return;
            }
            b bVar = new b();
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                    bVar.a = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                }
                if (jSONObject.has("appid")) {
                    bVar.c = jSONObject.optString("appid");
                }
                if (jSONObject.has("uid")) {
                    bVar.b = jSONObject.optString("uid");
                }
                if (jSONObject.has("message")) {
                    bVar.d = jSONObject.optString("message");
                }
                if (jSONObject.has("token")) {
                    bVar.e = jSONObject.optString("token");
                }
                if (jSONObject.has("ak_permission")) {
                    bVar.f = jSONObject.optInt("ak_permission");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int unused = PermissionCheck.k = bVar.a;
            if (PermissionCheck.j != null) {
                PermissionCheck.j.a(bVar);
            }
        }
    }

    public static class b {
        public int a = 0;
        public String b = "-1";
        public String c = "-1";
        public String d = "";
        public String e;
        public int f;

        public String toString() {
            return String.format("=============================================\n----------------- 鉴权错误信息 ------------\nsha1;package:%s\nkey:%s\nerrorcode: %d uid: %s appid %s msg: %s\n请仔细核查 SHA1、package与key申请信息是否对应，key是否删除，平台是否匹配\nerrorcode为230时，请参考论坛链接：\nhttp://bbs.lbsyun.baidu.com/forum.php?mod=viewthread&tid=106461\n=============================================\n", new Object[]{a.a(PermissionCheck.e), PermissionCheck.f, Integer.valueOf(this.a), this.b, this.c, this.d});
        }
    }

    public interface c {
        void a(b bVar);
    }

    public static void destory() {
        j = null;
        e = null;
        i = null;
    }

    public static int getPermissionResult() {
        return k;
    }

    public static void init(Context context) {
        ApplicationInfo applicationInfo;
        String str;
        e = context;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(e.getPackageName(), 128);
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            f = applicationInfo.metaData.getString("com.baidu.lbsapi.API_KEY");
        }
        if (g == null) {
            g = new Hashtable<>();
        }
        if (h == null) {
            h = LBSAuthManager.getInstance(e);
        }
        if (i == null) {
            i = new a();
        }
        try {
            str = context.getPackageManager().getPackageInfo(e.getPackageName(), 0).applicationInfo.loadLabel(e.getPackageManager()).toString();
        } catch (Exception e3) {
            e3.printStackTrace();
            str = "";
        }
        Bundle b2 = h.b();
        if (b2 != null) {
            g.put("mb", b2.getString("mb"));
            g.put(RecvStatsLogKey.KEY_OS, b2.getString(RecvStatsLogKey.KEY_OS));
            g.put("sv", b2.getString("sv"));
            g.put("imt", "1");
            g.put("net", b2.getString("net"));
            g.put("cpu", b2.getString("cpu"));
            g.put("glr", b2.getString("glr"));
            g.put("glv", b2.getString("glv"));
            g.put("resid", b2.getString("resid"));
            g.put("appid", "-1");
            g.put("ver", "1");
            g.put("screen", String.format("(%d,%d)", new Object[]{Integer.valueOf(b2.getInt("screen_x")), Integer.valueOf(b2.getInt("screen_y"))}));
            g.put("dpi", String.format("(%d,%d)", new Object[]{Integer.valueOf(b2.getInt("dpi_x")), Integer.valueOf(b2.getInt("dpi_y"))}));
            g.put("pcn", b2.getString("pcn"));
            g.put("cuid", b2.getString("cuid"));
            g.put("name", str);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized int permissionCheck() {
        /*
            java.lang.Class<com.baidu.mapsdkplatform.comapi.util.PermissionCheck> r0 = com.baidu.mapsdkplatform.comapi.util.PermissionCheck.class
            monitor-enter(r0)
            com.baidu.lbsapi.auth.LBSAuthManager r1 = h     // Catch:{ all -> 0x0065 }
            r2 = 0
            if (r1 == 0) goto L_0x0037
            com.baidu.lbsapi.auth.LBSAuthManagerListener r1 = i     // Catch:{ all -> 0x0065 }
            if (r1 == 0) goto L_0x0037
            android.content.Context r1 = e     // Catch:{ all -> 0x0065 }
            if (r1 != 0) goto L_0x0011
            goto L_0x0037
        L_0x0011:
            com.baidu.lbsapi.auth.LBSAuthManager r1 = h     // Catch:{ all -> 0x0065 }
            java.lang.String r3 = "lbs_androidmapsdk"
            java.util.Hashtable<java.lang.String, java.lang.String> r4 = g     // Catch:{ all -> 0x0065 }
            com.baidu.lbsapi.auth.LBSAuthManagerListener r5 = i     // Catch:{ all -> 0x0065 }
            int r1 = r1.authenticate(r2, r3, r4, r5)     // Catch:{ all -> 0x0065 }
            if (r1 == 0) goto L_0x0035
            java.lang.String r2 = d     // Catch:{ all -> 0x0065 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r3.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r4 = "permission check result is: "
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            r3.append(r1)     // Catch:{ all -> 0x0065 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0065 }
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x0065 }
        L_0x0035:
            monitor-exit(r0)
            return r1
        L_0x0037:
            java.lang.String r1 = d     // Catch:{ all -> 0x0065 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r3.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r4 = "The authManager is: "
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            com.baidu.lbsapi.auth.LBSAuthManager r4 = h     // Catch:{ all -> 0x0065 }
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            java.lang.String r4 = "; the authCallback is: "
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            com.baidu.lbsapi.auth.LBSAuthManagerListener r4 = i     // Catch:{ all -> 0x0065 }
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            java.lang.String r4 = "; the mContext is: "
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            android.content.Context r4 = e     // Catch:{ all -> 0x0065 }
            r3.append(r4)     // Catch:{ all -> 0x0065 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0065 }
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x0065 }
            monitor-exit(r0)
            return r2
        L_0x0065:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.util.PermissionCheck.permissionCheck():int");
    }

    public static void setPermissionCheckResultListener(c cVar) {
        j = cVar;
    }
}
