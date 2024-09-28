package com.baidu.platform.core.b;

import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.d;
import org.json.JSONException;
import org.json.JSONObject;

public class b extends d {
    private static final String b = b.class.getSimpleName();
    private String c;

    private LatLng a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        double optDouble = jSONObject.optDouble("lat");
        double optDouble2 = jSONObject.optDouble("lng");
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(new LatLng(optDouble, optDouble2)) : new LatLng(optDouble, optDouble2);
    }

    private boolean a(String str, GeoCodeResult geoCodeResult) {
        if (TextUtils.isEmpty(str) || geoCodeResult == null) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            int optInt = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
            if (optInt != 0) {
                geoCodeResult.error = optInt != 1 ? optInt != 2 ? SearchResult.ERRORNO.RESULT_NOT_FOUND : SearchResult.ERRORNO.SEARCH_OPTION_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                return false;
            }
            JSONObject optJSONObject = jSONObject.optJSONObject("result");
            if (optJSONObject == null) {
                geoCodeResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                return false;
            }
            geoCodeResult.setLocation(a(optJSONObject.optJSONObject("location")));
            geoCodeResult.setAddress(this.c);
            geoCodeResult.setPrecise(optJSONObject.optInt("precise"));
            geoCodeResult.setConfidence(optJSONObject.optInt("confidence"));
            geoCodeResult.setLevel(optJSONObject.optString("level"));
            geoCodeResult.error = SearchResult.ERRORNO.NO_ERROR;
            return true;
        } catch (JSONException e) {
            geoCodeResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
            Log.e(b, "Parse GeoCodeResult catch JSONException", e);
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006f, code lost:
        if (r7.equals("NETWORK_ERROR") != false) goto L_0x0073;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.mapapi.search.core.SearchResult a(java.lang.String r7) {
        /*
            r6 = this;
            com.baidu.mapapi.search.geocode.GeoCodeResult r0 = new com.baidu.mapapi.search.geocode.GeoCodeResult
            r0.<init>()
            if (r7 == 0) goto L_0x0082
            java.lang.String r1 = ""
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0011
            goto L_0x0082
        L_0x0011:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0085 }
            r1.<init>(r7)     // Catch:{ JSONException -> 0x0085 }
            java.lang.String r2 = "SDK_InnerError"
            boolean r3 = r1.has(r2)
            r4 = 0
            if (r3 != 0) goto L_0x0031
            boolean r1 = r6.a(r7, r0, r4)
            if (r1 == 0) goto L_0x0026
            return r0
        L_0x0026:
            boolean r7 = r6.a((java.lang.String) r7, (com.baidu.mapapi.search.geocode.GeoCodeResult) r0)
            if (r7 != 0) goto L_0x0030
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.RESULT_NOT_FOUND
            r0.error = r7
        L_0x0030:
            return r0
        L_0x0031:
            org.json.JSONObject r7 = r1.optJSONObject(r2)
            java.lang.String r1 = "PermissionCheckError"
            boolean r1 = r7.has(r1)
            if (r1 == 0) goto L_0x0042
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.PERMISSION_UNFINISHED
        L_0x003f:
            r0.error = r7
            return r0
        L_0x0042:
            java.lang.String r1 = "httpStateError"
            boolean r2 = r7.has(r1)
            if (r2 == 0) goto L_0x0082
            java.lang.String r7 = r7.optString(r1)
            r1 = -1
            int r2 = r7.hashCode()
            r3 = -879828873(0xffffffffcb8ee077, float:-1.872715E7)
            r5 = 1
            if (r2 == r3) goto L_0x0069
            r3 = 1470557208(0x57a6ec18, float:3.6706589E14)
            if (r2 == r3) goto L_0x005f
            goto L_0x0072
        L_0x005f:
            java.lang.String r2 = "REQUEST_ERROR"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x0072
            r4 = 1
            goto L_0x0073
        L_0x0069:
            java.lang.String r2 = "NETWORK_ERROR"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x0072
            goto L_0x0073
        L_0x0072:
            r4 = -1
        L_0x0073:
            if (r4 == 0) goto L_0x007d
            if (r4 == r5) goto L_0x007a
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR
            goto L_0x007f
        L_0x007a:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.REQUEST_ERROR
            goto L_0x007f
        L_0x007d:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.NETWORK_ERROR
        L_0x007f:
            r0.error = r7
            return r0
        L_0x0082:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.RESULT_NOT_FOUND
            goto L_0x003f
        L_0x0085:
            r7 = move-exception
            java.lang.String r1 = b
            java.lang.String r2 = "JSONException caught"
            android.util.Log.e(r1, r2, r7)
            goto L_0x0082
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.platform.core.b.b.a(java.lang.String):com.baidu.mapapi.search.core.SearchResult");
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetGeoCoderResultListener)) {
            ((OnGetGeoCoderResultListener) obj).onGetGeoCodeResult((GeoCodeResult) searchResult);
        }
    }

    public void b(String str) {
        this.c = str;
    }
}
