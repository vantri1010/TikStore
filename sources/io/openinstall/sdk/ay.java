package io.openinstall.sdk;

import androidx.core.app.NotificationCompat;
import org.json.JSONException;
import org.json.JSONObject;

public class ay implements bb {
    private int a;
    private String b;
    private String c;
    private String d;

    public static ay a(String str) throws JSONException {
        ay ayVar = new ay();
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("code") && !jSONObject.isNull("code")) {
            ayVar.a(jSONObject.optInt("code"));
        }
        if (jSONObject.has("config") && !jSONObject.isNull("config")) {
            ayVar.d(jSONObject.optString("config"));
        }
        if (jSONObject.has(TtmlNode.TAG_BODY) && !jSONObject.isNull(TtmlNode.TAG_BODY)) {
            ayVar.c(jSONObject.optString(TtmlNode.TAG_BODY));
        }
        if (jSONObject.has(NotificationCompat.CATEGORY_MESSAGE) && !jSONObject.isNull(NotificationCompat.CATEGORY_MESSAGE)) {
            ayVar.b(jSONObject.optString(NotificationCompat.CATEGORY_MESSAGE));
        }
        return ayVar;
    }

    public int a() {
        return this.a;
    }

    public void a(int i) {
        this.a = i;
    }

    public String b() {
        return this.c;
    }

    public void b(String str) {
        this.c = str;
    }

    public String c() {
        return this.b;
    }

    public void c(String str) {
        this.b = str;
    }

    public String d() {
        return this.d;
    }

    public void d(String str) {
        this.d = str;
    }

    public boolean e() {
        return false;
    }

    public String f() {
        return this.c;
    }
}
