package com.baidu.lbsapi.auth;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

class c {
    /* access modifiers changed from: private */
    public Context a;
    /* access modifiers changed from: private */
    public HashMap<String, String> b = null;
    private a<String> c = null;

    interface a<Result> {
        void a(Result result);
    }

    protected c(Context context) {
        this.a = context;
    }

    private HashMap<String, String> a(HashMap<String, String> hashMap) {
        HashMap<String, String> hashMap2 = new HashMap<>();
        for (String str : hashMap.keySet()) {
            String str2 = str.toString();
            hashMap2.put(str2, hashMap.get(str2));
        }
        return hashMap2;
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        JSONObject jSONObject;
        if (str == null) {
            str = "";
        }
        try {
            jSONObject = new JSONObject(str);
            if (!jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, -1);
            }
        } catch (JSONException e) {
            jSONObject = new JSONObject();
            try {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, -1);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        a<String> aVar = this.c;
        if (aVar != null) {
            aVar.a(jSONObject.toString());
        }
    }

    /* access modifiers changed from: protected */
    public void a(HashMap<String, String> hashMap, a<String> aVar) {
        this.b = a(hashMap);
        this.c = aVar;
        new Thread(new d(this)).start();
    }
}
