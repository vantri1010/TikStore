package io.openinstall.sdk;

import android.text.TextUtils;
import com.fm.openinstall.model.AppData;
import io.openinstall.sdk.bf;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class bx extends bc {
    public bx(h hVar) {
        super(hVar);
    }

    /* access modifiers changed from: protected */
    public AppData a(String str) throws JSONException {
        AppData appData = new AppData();
        if (TextUtils.isEmpty(str)) {
            return appData;
        }
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("c")) {
            appData.setChannel(jSONObject.optString("c"));
        }
        if (jSONObject.has("d")) {
            appData.setData(jSONObject.optString("d"));
        }
        return appData;
    }

    /* access modifiers changed from: protected */
    public AppData b(String str) throws JSONException {
        AppData appData = new AppData();
        if (TextUtils.isEmpty(str)) {
            return appData;
        }
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has("channelCode")) {
            appData.setChannel(jSONObject.optString("channelCode"));
        }
        if (jSONObject.has("bind")) {
            appData.setData(jSONObject.optString("bind"));
        }
        return appData;
    }

    /* access modifiers changed from: protected */
    public void c() {
        e().execute(new by(this));
    }

    /* access modifiers changed from: package-private */
    public abstract int n();

    /* access modifiers changed from: package-private */
    public abstract String o();

    /* access modifiers changed from: protected */
    public void p() {
        m().a(o());
    }

    /* access modifiers changed from: protected */
    public abstract bf q();

    /* renamed from: r */
    public bf call() throws Exception {
        p();
        g().a(o(), (long) n());
        if (!g().c()) {
            return bf.a.REQUEST_TIMEOUT.a();
        }
        if (g().b()) {
            return q();
        }
        return bf.a.INIT_ERROR.a(h().a("FM_init_msg"));
    }
}
