package io.openinstall.sdk;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class g {
    private Boolean a;
    private Boolean b;
    private Boolean c;
    private Boolean d;
    private Long e;
    private String f;

    public static g b(String str) {
        g gVar = new g();
        if (TextUtils.isEmpty(str)) {
            return gVar;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("wakeupStatsEnabled")) {
                gVar.a(Boolean.valueOf(jSONObject.optBoolean("wakeupStatsEnabled", true)));
            }
            if (jSONObject.has("aliveStatsEnabled")) {
                gVar.c(Boolean.valueOf(jSONObject.optBoolean("aliveStatsEnabled", true)));
            }
            if (jSONObject.has("registerStatsEnabled")) {
                gVar.b(Boolean.valueOf(jSONObject.optBoolean("registerStatsEnabled", true)));
            }
            if (jSONObject.has("eventStatsEnabled")) {
                gVar.c(Boolean.valueOf(jSONObject.optBoolean("eventStatsEnabled", true)));
            }
            if (jSONObject.has("reportPeriod")) {
                gVar.a(Long.valueOf(jSONObject.optLong("reportPeriod")));
            }
            if (jSONObject.has("installId")) {
                gVar.a(jSONObject.optString("installId"));
            }
            return gVar;
        } catch (JSONException e2) {
            return gVar;
        }
    }

    private boolean d(Boolean bool) {
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public Boolean a() {
        return this.a;
    }

    public void a(g gVar) {
        this.a = gVar.a();
        this.b = gVar.e();
        this.c = gVar.c();
        this.d = gVar.e();
        this.e = gVar.g();
        this.f = gVar.h();
    }

    public void a(Boolean bool) {
        this.a = bool;
    }

    public void a(Long l) {
        this.e = l;
    }

    public void a(String str) {
        this.f = str;
    }

    public void b(Boolean bool) {
        this.c = bool;
    }

    public boolean b() {
        return d(this.a);
    }

    public Boolean c() {
        return this.c;
    }

    public void c(Boolean bool) {
        this.d = bool;
    }

    public boolean d() {
        return d(this.c);
    }

    public Boolean e() {
        return this.d;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        g gVar = (g) obj;
        Boolean bool = this.a;
        if (bool == null ? gVar.a != null : !bool.equals(gVar.a)) {
            return false;
        }
        Boolean bool2 = this.b;
        if (bool2 == null ? gVar.b != null : !bool2.equals(gVar.b)) {
            return false;
        }
        Boolean bool3 = this.c;
        if (bool3 == null ? gVar.c != null : !bool3.equals(gVar.c)) {
            return false;
        }
        Boolean bool4 = this.d;
        if (bool4 == null ? gVar.d != null : !bool4.equals(gVar.d)) {
            return false;
        }
        Long l = this.e;
        if (l == null ? gVar.e != null : !l.equals(gVar.e)) {
            return false;
        }
        String str = this.f;
        return str != null ? str.equals(gVar.f) : gVar.f == null;
    }

    public boolean f() {
        return d(this.d);
    }

    public Long g() {
        return this.e;
    }

    public String h() {
        return this.f;
    }

    public int hashCode() {
        Boolean bool = this.a;
        int i = 0;
        int hashCode = (bool != null ? bool.hashCode() : 0) * 31;
        Boolean bool2 = this.b;
        int hashCode2 = (hashCode + (bool2 != null ? bool2.hashCode() : 0)) * 31;
        Boolean bool3 = this.c;
        int hashCode3 = (hashCode2 + (bool3 != null ? bool3.hashCode() : 0)) * 31;
        Boolean bool4 = this.d;
        int hashCode4 = (hashCode3 + (bool4 != null ? bool4.hashCode() : 0)) * 31;
        Long l = this.e;
        int hashCode5 = (hashCode4 + (l != null ? l.hashCode() : 0)) * 31;
        String str = this.f;
        if (str != null) {
            i = str.hashCode();
        }
        return hashCode5 + i;
    }

    public String i() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("wakeupStatsEnabled", this.a);
            jSONObject.put("registerStatsEnabled", this.c);
            jSONObject.put("eventStatsEnabled", this.d);
            jSONObject.put("reportPeriod", this.e);
            jSONObject.put("installId", this.f);
        } catch (JSONException e2) {
        }
        return jSONObject.toString();
    }
}
