package com.baidu.platform.base;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.http.AsyncHttpClient;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapsdkplatform.comapi.util.AlgorithmUtil;
import com.baidu.mapsdkplatform.comapi.util.PermissionCheck;
import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import com.baidu.platform.core.a.b;
import com.baidu.platform.core.a.c;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class a {
    protected final Lock a = new ReentrantLock();
    /* access modifiers changed from: private */
    public AsyncHttpClient b = new AsyncHttpClient();
    private Handler c = new Handler(Looper.getMainLooper());
    private boolean d = true;
    private DistrictResult e = null;
    private SearchType f;

    /* access modifiers changed from: private */
    public String a(String str) {
        byte[] bArr = {102, 97, 105, 108, 100};
        try {
            bArr = AlgorithmUtil.getUrlNeedInfo(AppMD5.getUrlNeedInfo(), AppMD5.getUrlNeedInfo(), Base64.decode(str.getBytes(), 0));
        } catch (Exception e2) {
            Log.e("BaseSearch", "transform result failed", e2);
        }
        return new String(bArr).trim();
    }

    private void a(AsyncHttpClient asyncHttpClient, HttpClient.ProtoResultCallback protoResultCallback, SearchResult searchResult) {
        asyncHttpClient.get(new c(((DistrictResult) searchResult).getCityName()).a(this.f), protoResultCallback);
    }

    /* access modifiers changed from: private */
    public void a(HttpClient.HttpStateError httpStateError, d dVar, Object obj) {
        a(dVar.a("{SDK_InnerError:{httpStateError:" + httpStateError + "}}"), obj, dVar);
    }

    private void a(SearchResult searchResult, Object obj, d dVar) {
        this.c.post(new c(this, dVar, searchResult, obj));
    }

    /* access modifiers changed from: private */
    public void a(String str, d dVar, Object obj, AsyncHttpClient asyncHttpClient, HttpClient.ProtoResultCallback protoResultCallback) {
        SearchResult a2 = dVar.a(str);
        a2.status = b(str);
        if (a(dVar, a2)) {
            a(asyncHttpClient, protoResultCallback, a2);
        } else if (dVar instanceof b) {
            DistrictResult districtResult = this.e;
            if (districtResult != null) {
                DistrictResult districtResult2 = (DistrictResult) a2;
                districtResult2.setCityCode(districtResult.getCityCode());
                districtResult2.setCenterPt(this.e.getCenterPt());
            }
            a(a2, obj, dVar);
            this.d = true;
            this.e = null;
            ((b) dVar).a(false);
        } else {
            a(a2, obj, dVar);
        }
    }

    private boolean a(d dVar, SearchResult searchResult) {
        if (!(dVar instanceof b)) {
            return false;
        }
        DistrictResult districtResult = (DistrictResult) searchResult;
        if (SearchResult.ERRORNO.RESULT_NOT_FOUND != districtResult.error || districtResult.getCityName() == null || !this.d) {
            return false;
        }
        this.d = false;
        this.e = districtResult;
        ((b) dVar).a(true);
        return true;
    }

    private int b(String str) {
        JSONObject optJSONObject;
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                    return jSONObject.getInt(NotificationCompat.CATEGORY_STATUS);
                }
                if (jSONObject.has("status_sp")) {
                    return jSONObject.getInt("status_sp");
                }
                if (!jSONObject.has("result") || (optJSONObject = jSONObject.optJSONObject("result")) == null) {
                    return 10204;
                }
                return optJSONObject.optInt("error");
            } catch (JSONException e2) {
                Log.e("BaseSearch", "Create JSONObject failed when get response result status");
            }
        }
        return 10204;
    }

    /* access modifiers changed from: private */
    public void c(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has(NotificationCompat.CATEGORY_STATUS)) {
                if (!jSONObject.has("status_sp")) {
                    return;
                }
            }
            int i = jSONObject.has(NotificationCompat.CATEGORY_STATUS) ? jSONObject.getInt(NotificationCompat.CATEGORY_STATUS) : jSONObject.getInt("status_sp");
            if (i == 105 || i == 106) {
                int permissionCheck = PermissionCheck.permissionCheck();
                if (permissionCheck != 0) {
                    Log.e("BaseSearch", "permissionCheck result is: " + permissionCheck);
                }
            }
        } catch (JSONException e2) {
            Log.e("BaseSearch", "Parse json happened exception", e2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean a(e eVar, Object obj, d dVar) {
        if (dVar == null) {
            Log.e(a.class.getSimpleName(), "The SearchParser is null, must be applied.");
            return false;
        }
        SearchType a2 = dVar.a();
        this.f = a2;
        String a3 = eVar.a(a2);
        if (a3 == null) {
            Log.e("BaseSearch", "The sendurl is: " + a3);
            a(dVar.a("{SDK_InnerError:{PermissionCheckError:Error}}"), obj, dVar);
            return false;
        }
        this.b.get(a3, new b(this, dVar, obj));
        return true;
    }
}
