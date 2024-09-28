package com.baidu.mapsdkplatform.comapi.map;

import android.content.Context;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.NetworkUtil;
import com.baidu.mapapi.http.AsyncHttpClient;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapsdkplatform.comapi.util.SyncSysInfo;
import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class h {
    /* access modifiers changed from: private */
    public static final String a = h.class.getSimpleName();
    private AsyncHttpClient b;

    public interface a {
        void a(int i, String str, String str2);

        void a(String str);

        void a(boolean z, String str);
    }

    private static class b {
        /* access modifiers changed from: private */
        public static final h a = new h((i) null);
    }

    private h() {
        this.b = new AsyncHttpClient();
    }

    /* synthetic */ h(i iVar) {
        this();
    }

    public static h a() {
        return b.a;
    }

    private String a(Context context, String str) {
        String str2 = "null";
        if (!a(b(context, str))) {
            return str2;
        }
        try {
            FileInputStream openFileInput = context.openFileInput("server_custom_style_" + str + ".json");
            JsonReader jsonReader = new JsonReader(new InputStreamReader(openFileInput));
            try {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (jsonReader.nextName().equals("md5")) {
                        str2 = jsonReader.nextString();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
                try {
                    jsonReader.close();
                    openFileInput.close();
                } catch (IOException e) {
                    Log.e(a, "Close custom style failed", e);
                }
                return str2;
            } catch (IOException e2) {
                Log.e(a, "Read custom style failed", e2);
                try {
                    jsonReader.close();
                    openFileInput.close();
                } catch (IOException e3) {
                    Log.e(a, "Close custom style failed", e3);
                }
                return str2;
            } catch (Throwable th) {
                try {
                    jsonReader.close();
                    openFileInput.close();
                } catch (IOException e4) {
                    Log.e(a, "Close custom style failed", e4);
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
            Log.e(a, "Open custom style failed", e5);
            return str2;
        }
    }

    private String a(Context context, String str, boolean z) {
        String a2 = a(context, str);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("style_id", str);
        linkedHashMap.put("type", z ? "publish" : "edit");
        linkedHashMap.put("md5", a2);
        linkedHashMap.put("token", SyncSysInfo.getAuthToken());
        String str2 = a((Map<String, String>) linkedHashMap) + SyncSysInfo.getPhoneInfo();
        return c() + "?" + (str2 + "&sign=" + AppMD5.getSignMD5String(str2));
    }

    private String a(Map<String, String> map) {
        if (map.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String next : map.keySet()) {
            String encodeUrlParamsValue = AppMD5.encodeUrlParamsValue(map.get(next));
            if (i != 0) {
                sb.append("&");
            }
            sb.append(next);
            sb.append("=");
            sb.append(encodeUrlParamsValue);
            i++;
        }
        return sb.toString();
    }

    private void a(Context context, String str, String str2, a aVar) {
        this.b.get(str, new i(this, context, str2, aVar));
    }

    private void a(Context context, String str, boolean z, a aVar) {
        String b2 = b(context, str);
        if (!a(b2)) {
            b2 = null;
        }
        if (aVar != null) {
            aVar.a(b2);
        }
        if (!NetworkUtil.isNetworkAvailable(context)) {
            if (aVar != null) {
                aVar.a(HttpClient.HttpStateError.NETWORK_ERROR.ordinal(), HttpClient.HttpStateError.NETWORK_ERROR.name(), b2);
            }
        } else if (!TextUtils.isEmpty(str)) {
            String a2 = a(context, str, z);
            if (TextUtils.isEmpty(a2)) {
                Log.e(a, "build request url failed");
            } else {
                a(context, a2, str, aVar);
            }
        }
    }

    private boolean a(int i, String str, String str2) {
        return (103 != i || !a(str2)) && i == 0;
    }

    private boolean a(Context context, JSONObject jSONObject, String str) {
        String str2;
        String str3;
        String jSONObject2;
        File file = new File(b(context, str));
        if (file.exists()) {
            file.delete();
        }
        try {
            if (file.createNewFile()) {
                file.createNewFile();
            }
            String optString = jSONObject.optString("json");
            String optString2 = jSONObject.optString("md5", "null");
            JSONObject jSONObject3 = new JSONObject();
            try {
                jSONObject3.put("json", optString);
                jSONObject3.put("md5", optString2);
                jSONObject2 = jSONObject3.toString();
            } catch (JSONException e) {
                e = e;
                str2 = a;
                str3 = "build style data failed";
                Log.e(str2, str3, e);
                return false;
            }
            try {
                FileOutputStream openFileOutput = context.openFileOutput("server_custom_style_" + str + ".json", 0);
                openFileOutput.write(jSONObject2.getBytes());
                openFileOutput.flush();
                openFileOutput.close();
                return true;
            } catch (IOException e2) {
                e = e2;
                str2 = a;
                str3 = "write style data into file failed";
                Log.e(str2, str3, e);
                return false;
            }
        } catch (IOException e3) {
            e = e3;
            str2 = a;
            str3 = "create custom file failed";
            Log.e(str2, str3, e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean a(String str) {
        if (str == null) {
            return false;
        }
        return new File(str).exists();
    }

    /* access modifiers changed from: private */
    public String b(Context context, String str) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "server_custom_style_" + str + ".json";
    }

    /* access modifiers changed from: private */
    public void b(Context context, String str, String str2, a aVar) {
        String b2 = b(context, str2);
        String str3 = a(b2) ? b2 : null;
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (a(jSONObject.optInt(NotificationCompat.CATEGORY_STATUS), jSONObject.optString("message"), b2)) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("data");
                    if (optJSONObject == null || optJSONObject.length() == 0) {
                        Log.e(a, "custom style data is null");
                        if (aVar != null) {
                            aVar.a(HttpClient.HttpStateError.SERVER_ERROR.ordinal(), "custom style data is null", str3);
                            return;
                        }
                        return;
                    }
                    boolean a2 = a(context, optJSONObject, str2);
                    if (aVar != null) {
                        if (!a(b2)) {
                            b2 = null;
                        }
                        if (a2) {
                            aVar.a(true, b2);
                        } else {
                            aVar.a(HttpClient.HttpStateError.INNER_ERROR.ordinal(), "write style data into file failed", b2);
                        }
                    }
                } else if (aVar != null) {
                    aVar.a(false, str3);
                }
            } catch (JSONException e) {
                Log.e(a, "parse response result failed", e);
                if (aVar != null) {
                    aVar.a(HttpClient.HttpStateError.INNER_ERROR.ordinal(), "parse response result failed", str3);
                }
            }
        } else if (aVar != null) {
            aVar.a(HttpClient.HttpStateError.SERVER_ERROR.ordinal(), HttpClient.HttpStateError.SERVER_ERROR.name(), str3);
        }
    }

    private String c() {
        return HttpClient.isHttpsEnable ? "https://api.map.baidu.com/sdkproxy/v2/lbs_androidsdk/custom/v2/getjsonstyle" : "http://api.map.baidu.com/sdkproxy/v2/lbs_androidsdk/custom/v2/getjsonstyle";
    }

    public void a(Context context, String str, a aVar) {
        a(context, str, true, aVar);
    }
}
