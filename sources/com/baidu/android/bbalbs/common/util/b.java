package com.baidu.android.bbalbs.common.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.baidu.android.bbalbs.common.a.a;
import com.baidu.android.bbalbs.common.a.c;
import com.king.zxing.util.LogUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

final class b {
    private static final String e = a(new byte[]{81, 72, 116, 79, 75, 72, 69, 52, 76, 51, 103, 61}, new byte[]{82, 51, 104, 90, 83, 122, 65, 105, 101, 49, 107, 61});
    private static final String f = a(new byte[]{76, 67, 77, 53, 77, 70, 90, 73, 81, 107, 107, 61}, new byte[]{90, 105, 108, 121, 79, 68, 100, 81, 86, 121, 89, 61});
    private String a;
    private String b;
    private int c = 3;
    private int d;

    b() {
    }

    static b a(Context context, String str) {
        return b(context, str);
    }

    static b a(a aVar) {
        if (aVar != null) {
            b bVar = new b();
            bVar.a(aVar.a());
            bVar.b(aVar.b());
            return bVar;
        }
        throw new IllegalArgumentException("arg non-nullable is expected");
    }

    private static String a(byte[]... bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte[] a2 : bArr) {
            sb.append(new String(com.baidu.android.bbalbs.common.a.b.a(a2)));
        }
        return sb.toString();
    }

    static b b(Context context) {
        File d2 = d(context);
        if (!d2.exists()) {
            return null;
        }
        return d(d.a(d2));
    }

    private static b b(Context context, String str) {
        StringBuilder sb;
        b bVar = new b();
        boolean z = Build.VERSION.SDK_INT < 23;
        String a2 = d.a(context);
        if (z) {
            String e2 = e(context);
            if (TextUtils.isEmpty(e2)) {
                e2 = UUID.randomUUID().toString();
                c(context, e2);
            }
            sb = new StringBuilder();
            sb.append(a2);
            sb.append(e2);
        } else {
            sb = new StringBuilder();
            sb.append("com.baidu");
            sb.append(a2);
        }
        bVar.a(c.a(sb.toString().getBytes(), true));
        bVar.b(str);
        bVar.a(Build.VERSION.SDK_INT);
        return bVar;
    }

    private String b() {
        try {
            return new JSONObject().put(i("ZGV2aWNlaWQ="), this.a).put(i("ZmxhZw=="), this.b == null ? "0" : this.b).put(i("dmVy"), this.c).put(i("c2Rr"), this.d).toString();
        } catch (JSONException e2) {
            d.a((Throwable) e2);
            return null;
        }
    }

    static b c(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String optString = jSONObject.optString(i("ZmxhZw=="), "0");
            String string = jSONObject.getString(i("ZGV2aWNlaWQ="));
            int optInt = jSONObject.optInt(i("c2Rr"), 0);
            if (!TextUtils.isEmpty(string)) {
                b bVar = new b();
                bVar.a(string);
                bVar.b(optString);
                bVar.a(optInt);
                return bVar;
            }
        } catch (JSONException e2) {
            d.a((Throwable) e2);
        }
        return null;
    }

    private static void c(Context context, String str) {
        if (TextUtils.isEmpty(d.a(context, "XL5g0WZAHpIaKspIHIHYg5k")) && d.b(context)) {
            d.a(context, "XL5g0WZAHpIaKspIHIHYg5k", g(str));
        }
    }

    private boolean c(Context context) {
        String e2 = e(b());
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput("libcuid_v3.so", 0);
            fileOutputStream.write(e2.getBytes());
            fileOutputStream.flush();
            if (fileOutputStream == null) {
                return true;
            }
            try {
                fileOutputStream.close();
                return true;
            } catch (Exception e3) {
                d.a((Throwable) e3);
                return true;
            }
        } catch (Exception e4) {
            d.a((Throwable) e4);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e5) {
                    d.a((Throwable) e5);
                }
            }
            return false;
        } catch (Throwable th) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e6) {
                    d.a((Throwable) e6);
                }
            }
            throw th;
        }
    }

    static b d(String str) {
        return c(f(str));
    }

    private static File d(Context context) {
        return new File(context.getFilesDir(), "libcuid_v3.so");
    }

    private static String e(Context context) {
        return h(d.a(context, "XL5g0WZAHpIaKspIHIHYg5k"));
    }

    static String e(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return com.baidu.android.bbalbs.common.a.b.a(a.a(e, f, str.getBytes()), "utf-8");
        } catch (UnsupportedEncodingException | Exception e2) {
            d.a(e2);
            return "";
        }
    }

    static String f(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new String(a.b(e, f, com.baidu.android.bbalbs.common.a.b.a(str.getBytes())));
        } catch (Exception e2) {
            d.a((Throwable) e2);
            return "";
        }
    }

    static String g(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return com.baidu.android.bbalbs.common.a.b.a(a.a(f, e, str.getBytes()), "utf-8");
        } catch (UnsupportedEncodingException | Exception e2) {
            d.a(e2);
            return "";
        }
    }

    static String h(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new String(a.b(f, e, com.baidu.android.bbalbs.common.a.b.a(str.getBytes())));
        } catch (Exception e2) {
            d.a((Throwable) e2);
            return "";
        }
    }

    static String i(String str) {
        return new String(com.baidu.android.bbalbs.common.a.b.a(str.getBytes()));
    }

    public String a() {
        if (TextUtils.isEmpty(this.b)) {
            this.b = "0";
        }
        return this.a + LogUtils.VERTICAL + this.b;
    }

    public void a(int i) {
        this.d = i;
    }

    public void a(String str) {
        this.a = str;
    }

    /* access modifiers changed from: package-private */
    public boolean a(Context context) {
        return c(context);
    }

    public void b(String str) {
        this.b = str;
    }
}
