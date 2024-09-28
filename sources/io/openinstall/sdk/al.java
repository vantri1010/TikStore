package io.openinstall.sdk;

import android.content.ClipData;
import android.os.Build;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class al {
    private String a;
    private String b;
    private int c = 0;
    private boolean d;

    public static al a(ClipData clipData) {
        String str;
        boolean d2;
        ClipData.Item itemAt;
        String str2 = null;
        if (clipData == null) {
            return null;
        }
        al alVar = new al();
        if (clipData.getItemCount() <= 0 || (itemAt = clipData.getItemAt(0)) == null) {
            str = null;
        } else {
            String htmlText = Build.VERSION.SDK_INT >= 16 ? itemAt.getHtmlText() : null;
            if (itemAt.getText() != null) {
                str2 = itemAt.getText().toString();
            }
            str = str2;
            str2 = htmlText;
        }
        if (str2 != null) {
            if (str2.contains(ca.d)) {
                alVar.b(str2);
                alVar.b(2);
            }
            alVar.a(d(str2));
        }
        if (str != null) {
            if (str.contains(ca.d)) {
                alVar.a(str);
                alVar.b(1);
                d2 = d(str);
            } else {
                String b2 = bz.b(str);
                if (b2.contains(ca.d)) {
                    alVar.a(str);
                    alVar.b(1);
                }
                d2 = d(b2);
            }
            alVar.a(d2);
        }
        return alVar;
    }

    public static al c(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        al alVar = new al();
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("pbText")) {
                alVar.a(jSONObject.optString("pbText"));
            }
            if (jSONObject.has("pbHtml")) {
                alVar.b(jSONObject.optString("pbHtml"));
            }
            if (jSONObject.has("pbType")) {
                alVar.a(jSONObject.optInt("pbType"));
            }
            return alVar;
        } catch (JSONException e) {
            return null;
        }
    }

    private static boolean d(String str) {
        if (!str.contains(ca.e)) {
            return false;
        }
        long j = 0;
        try {
            int indexOf = str.indexOf(ca.e) + ca.e.length();
            j = Long.parseLong(str.substring(indexOf, str.indexOf("-", indexOf)));
        } catch (Exception e) {
        }
        return System.currentTimeMillis() < j;
    }

    public String a() {
        return this.a;
    }

    public void a(int i) {
        this.c = i;
    }

    public void a(String str) {
        this.a = str;
    }

    public void a(boolean z) {
        this.d = z;
    }

    public String b() {
        return this.b;
    }

    public void b(int i) {
        this.c = i | this.c;
    }

    public void b(String str) {
        this.b = str;
    }

    public int c() {
        return this.c;
    }

    public boolean c(int i) {
        return (i & this.c) != 0;
    }

    public String d() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("pbText", this.a);
            jSONObject.put("pbHtml", this.b);
            jSONObject.put("pbType", this.c);
        } catch (JSONException e) {
        }
        return jSONObject.toString();
    }
}
