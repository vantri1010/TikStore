package io.openinstall.sdk;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class p {
    private final File a;
    private final File b;
    private int c = (a(this.a) + a(this.b));

    public p(Context context, String str) {
        this.a = new File(context.getFilesDir(), str);
        File filesDir = context.getFilesDir();
        this.b = new File(filesDir, str + ".t");
    }

    private int a(File file) {
        String b2 = u.b(file);
        int i = 0;
        int i2 = 0;
        while (true) {
            int indexOf = b2.indexOf(";", i);
            if (indexOf == -1) {
                return i2;
            }
            i2++;
            i = indexOf + ";".length();
        }
    }

    private int a(String str, int i) {
        int i2 = 0;
        int i3 = 0;
        do {
            int indexOf = str.indexOf(";", i2);
            if (indexOf == -1) {
                break;
            }
            i3++;
            i2 = ";".length() + indexOf;
        } while (i3 < i);
        return i2;
    }

    private String b(l lVar) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(lVar.d())) {
            sb.append(lVar.d());
            sb.append(",");
        }
        if (lVar.e() != null) {
            sb.append(lVar.e());
            sb.append(",");
        }
        if (lVar.f() != null) {
            sb.append(lVar.f());
            sb.append(",");
        }
        if (lVar.g() != null && lVar.g().size() > 0) {
            try {
                JSONObject jSONObject = new JSONObject();
                for (Map.Entry next : lVar.g().entrySet()) {
                    jSONObject.put((String) next.getKey(), next.getValue());
                }
                sb.append(bz.c(jSONObject.toString()));
                sb.append(",");
            } catch (JSONException e) {
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            sb.append(";");
        }
        String sb2 = sb.toString();
        int length = sb2.getBytes(ac.c).length;
        if (length >= 1024 && cb.a) {
            cb.b("效果点 %s 过长 : %d", lVar.d(), Integer.valueOf(length));
        }
        return sb2;
    }

    public void a(l lVar) {
        a(b(lVar));
        this.c++;
    }

    public void a(String str) {
        u.a(this.a, str, true);
        this.a.length();
    }

    public boolean a() {
        return this.c <= 0;
    }

    public boolean b() {
        return this.c >= 300;
    }

    public void c() {
        int a2 = a(this.b);
        u.a(this.b, "", false);
        this.c -= a2;
    }

    public void d() {
        this.a.delete();
        this.b.delete();
        this.c = 0;
    }

    public String e() {
        int a2 = a(this.b);
        String b2 = u.b(this.b);
        if (a2 > 150) {
            return b2;
        }
        String b3 = u.b(this.a);
        int a3 = a(b3, 300 - a2);
        String str = b2 + b3.substring(0, a3);
        u.a(this.b, str, false);
        u.a(this.a, b3.substring(a3), false);
        return str;
    }
}
