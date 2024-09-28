package com.baidu.platform.comapi.pano;

import android.net.Uri;
import android.text.TextUtils;
import com.baidu.mapapi.http.AsyncHttpClient;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class a {
    AsyncHttpClient a = new AsyncHttpClient();

    /* renamed from: com.baidu.platform.comapi.pano.a$a  reason: collision with other inner class name */
    public interface C0022a<T> {
        void a(HttpClient.HttpStateError httpStateError);

        void a(T t);
    }

    /* access modifiers changed from: private */
    public c a(String str) {
        if (str == null || str.equals("")) {
            return new c(PanoStateError.PANO_NOT_FOUND);
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject optJSONObject = jSONObject.optJSONObject("result");
            if (optJSONObject == null) {
                return new c(PanoStateError.PANO_NOT_FOUND);
            }
            if (optJSONObject.optInt("error") != 0) {
                return new c(PanoStateError.PANO_UID_ERROR);
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("content");
            if (optJSONArray == null) {
                return new c(PanoStateError.PANO_NOT_FOUND);
            }
            c cVar = null;
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject optJSONObject2 = optJSONArray.optJSONObject(i).optJSONObject("poiinfo");
                if (optJSONObject2 != null) {
                    cVar = new c(PanoStateError.PANO_NO_ERROR);
                    cVar.a(optJSONObject2.optString("PID"));
                    cVar.a(optJSONObject2.optInt("hasstreet"));
                }
            }
            return cVar;
        } catch (JSONException e) {
            e.printStackTrace();
            return new c(PanoStateError.PANO_NOT_FOUND);
        }
    }

    private String a(Uri.Builder builder) {
        String uri = builder.build().toString();
        Uri.Builder buildUpon = Uri.parse(uri + HttpClient.getPhoneInfo()).buildUpon();
        buildUpon.appendQueryParameter("sign", AppMD5.getSignMD5String(buildUpon.build().getEncodedQuery()));
        return buildUpon.build().toString();
    }

    private void a(Uri.Builder builder, String str, String str2) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            builder.appendQueryParameter(str, str2);
        }
    }

    public void a(String str, C0022a<c> aVar) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HttpClient.isHttpsEnable ? "https" : "http");
        builder.encodedAuthority("api.map.baidu.com");
        builder.path("/sdkproxy/lbs_androidsdk/pano/v1/");
        a(builder, "qt", "poi");
        a(builder, "uid", str);
        a(builder, "action", "0");
        String authToken = HttpClient.getAuthToken();
        if (authToken == null) {
            aVar.a(new c(PanoStateError.PANO_NO_TOKEN));
            return;
        }
        a(builder, "token", authToken);
        this.a.get(a(builder), new b(this, aVar));
    }
}
