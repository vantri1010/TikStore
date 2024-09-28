package com.baidu.location.a;

import android.content.Context;
import android.util.Log;
import com.baidu.lbsapi.auth.LBSAuthManager;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.Hashtable;
import org.json.JSONObject;

public class a implements LBSAuthManagerListener {
    private static Object a = new Object();
    private static a b = null;
    private int c = 0;
    private Context d = null;
    private long e = 0;
    private String f = null;

    public static a a() {
        a aVar;
        synchronized (a) {
            if (b == null) {
                b = new a();
            }
            aVar = b;
        }
        return aVar;
    }

    public static String b(Context context) {
        try {
            return LBSAuthManager.getInstance(context).getPublicKey(context);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String c(Context context) {
        try {
            return LBSAuthManager.getInstance(context).getMCode();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public void a(Context context) {
        this.d = context;
        LBSAuthManager.getInstance(context).authenticate(false, "lbs_locsdk", (Hashtable<String, String>) null, this);
        this.e = System.currentTimeMillis();
    }

    public boolean b() {
        int i = this.c;
        boolean z = i == 0 || i == 602 || i == 601 || i == -10 || i == -11;
        if (this.d != null) {
            long currentTimeMillis = System.currentTimeMillis() - this.e;
            if (!z ? currentTimeMillis < 0 || currentTimeMillis > OkHttpUtils.DEFAULT_MILLISECONDS : currentTimeMillis > 86400000) {
                LBSAuthManager.getInstance(this.d).authenticate(false, "lbs_locsdk", (Hashtable<String, String>) null, this);
                this.e = System.currentTimeMillis();
            }
        }
        return z;
    }

    public void onAuthResult(int i, String str) {
        this.c = i;
        if (i == 0) {
            Log.i(com.baidu.location.g.a.a, "LocationAuthManager Authentication AUTHENTICATE_SUCC");
        } else {
            String str2 = com.baidu.location.g.a.a;
            Log.i(str2, "LocationAuthManager Authentication Error errorcode = " + i + " , msg = " + str);
        }
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.getString("token") != null) {
                    this.f = jSONObject.getString("token");
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
