package com.baidu.lbsapi.auth;

import androidx.core.app.NotificationCompat;
import org.json.JSONException;
import org.json.JSONObject;

class ErrorMessage {
    ErrorMessage() {
    }

    static String a(int i, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(NotificationCompat.CATEGORY_STATUS, i);
            jSONObject.put("message", str);
        } catch (JSONException e) {
        }
        return jSONObject.toString();
    }

    static String a(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(NotificationCompat.CATEGORY_STATUS, -1);
            jSONObject.put("message", str);
        } catch (JSONException e) {
        }
        return jSONObject.toString();
    }
}
