package com.baidu.platform.core.a;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictResult;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b extends d {
    boolean b = false;
    String c = null;

    private boolean a(String str, DistrictResult districtResult) {
        JSONObject optJSONObject;
        JSONArray optJSONArray;
        JSONArray jSONArray;
        JSONArray optJSONArray2;
        int length;
        JSONArray jSONArray2;
        int i;
        JSONArray jSONArray3;
        String str2 = str;
        DistrictResult districtResult2 = districtResult;
        if (str2 == null || "".equals(str2) || districtResult2 == null) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject optJSONObject2 = jSONObject.optJSONObject("result");
            JSONObject optJSONObject3 = jSONObject.optJSONObject("city_result");
            if (optJSONObject2 == null || optJSONObject3 == null) {
                return false;
            } else if (optJSONObject2.optInt("error") != 0 || (optJSONObject = optJSONObject3.optJSONObject("content")) == null) {
                return false;
            } else {
                JSONObject optJSONObject4 = optJSONObject.optJSONObject("sgeo");
                if (!(optJSONObject4 == null || (optJSONArray = optJSONObject4.optJSONArray("geo_elements")) == null || optJSONArray.length() <= 0)) {
                    ArrayList arrayList = new ArrayList();
                    int i2 = 0;
                    while (i2 < optJSONArray.length()) {
                        JSONObject optJSONObject5 = optJSONArray.optJSONObject(i2);
                        if (optJSONObject5 == null || (optJSONArray2 = optJSONObject5.optJSONArray("point")) == null || (length = optJSONArray2.length()) <= 0) {
                            jSONArray = optJSONArray;
                        } else {
                            ArrayList arrayList2 = new ArrayList();
                            int i3 = 0;
                            int i4 = 0;
                            int i5 = 0;
                            while (i3 < length) {
                                int optInt = optJSONArray2.optInt(i3);
                                if (i3 % 2 == 0) {
                                    i5 += optInt;
                                    jSONArray2 = optJSONArray;
                                    jSONArray3 = optJSONArray2;
                                    i = length;
                                } else {
                                    i4 += optInt;
                                    jSONArray2 = optJSONArray;
                                    jSONArray3 = optJSONArray2;
                                    i = length;
                                    arrayList2.add(CoordUtil.mc2ll(new GeoPoint((double) i4, (double) i5)));
                                }
                                i3++;
                                optJSONArray = jSONArray2;
                                optJSONArray2 = jSONArray3;
                                length = i;
                            }
                            jSONArray = optJSONArray;
                            arrayList.add(arrayList2);
                        }
                        i2++;
                        optJSONArray = jSONArray;
                    }
                    if (arrayList.size() > 0) {
                        districtResult2.setPolylines(arrayList);
                        districtResult2.setCenterPt(CoordUtil.decodeLocation(optJSONObject.optString("geo")));
                        districtResult2.setCityCode(optJSONObject.optInt("code"));
                        districtResult2.setCityName(optJSONObject.optString("cname"));
                        districtResult2.error = SearchResult.ERRORNO.NO_ERROR;
                        return true;
                    }
                }
                districtResult2.setCityName(optJSONObject.optString("uid"));
                this.c = optJSONObject.optString("cname");
                districtResult2.setCenterPt(CoordUtil.decodeLocation(optJSONObject.optString("geo")));
                districtResult2.setCityCode(optJSONObject.optInt("code"));
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x007d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean b(java.lang.String r6, com.baidu.mapapi.search.district.DistrictResult r7) {
        /*
            r5 = this;
            r0 = 0
            if (r6 == 0) goto L_0x0092
            java.lang.String r1 = ""
            boolean r1 = r6.equals(r1)
            if (r1 != 0) goto L_0x0092
            if (r7 != 0) goto L_0x000f
            goto L_0x0092
        L_0x000f:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x008e }
            r1.<init>(r6)     // Catch:{ JSONException -> 0x008e }
            java.lang.String r6 = "result"
            org.json.JSONObject r6 = r1.optJSONObject(r6)
            java.lang.String r2 = "content"
            org.json.JSONObject r1 = r1.optJSONObject(r2)
            if (r6 == 0) goto L_0x008d
            if (r1 != 0) goto L_0x0025
            goto L_0x008d
        L_0x0025:
            java.lang.String r2 = "error"
            int r6 = r6.optInt(r2)
            if (r6 == 0) goto L_0x002e
            return r0
        L_0x002e:
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.lang.String r0 = r5.c
            r2 = 0
            if (r0 == 0) goto L_0x0047
            java.lang.String r0 = "geo"
            java.lang.String r0 = r1.optString(r0)     // Catch:{ Exception -> 0x0043 }
            java.util.List r0 = com.baidu.mapapi.model.CoordUtil.decodeLocationList2D(r0)     // Catch:{ Exception -> 0x0043 }
            goto L_0x0048
        L_0x0043:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0047:
            r0 = r2
        L_0x0048:
            if (r0 == 0) goto L_0x0077
            java.util.Iterator r0 = r0.iterator()
        L_0x004e:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0077
            java.lang.Object r1 = r0.next()
            java.util.List r1 = (java.util.List) r1
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.Iterator r1 = r1.iterator()
        L_0x0063:
            boolean r4 = r1.hasNext()
            if (r4 == 0) goto L_0x0073
            java.lang.Object r4 = r1.next()
            com.baidu.mapapi.model.LatLng r4 = (com.baidu.mapapi.model.LatLng) r4
            r3.add(r4)
            goto L_0x0063
        L_0x0073:
            r6.add(r3)
            goto L_0x004e
        L_0x0077:
            int r0 = r6.size()
            if (r0 <= 0) goto L_0x0080
            r7.setPolylines(r6)
        L_0x0080:
            java.lang.String r6 = r5.c
            r7.setCityName(r6)
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r6 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.NO_ERROR
            r7.error = r6
            r5.c = r2
            r6 = 1
            return r6
        L_0x008d:
            return r0
        L_0x008e:
            r6 = move-exception
            r6.printStackTrace()
        L_0x0092:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.platform.core.a.b.b(java.lang.String, com.baidu.mapapi.search.district.DistrictResult):boolean");
    }

    public SearchResult a(String str) {
        DistrictResult districtResult = new DistrictResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        districtResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return districtResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        districtResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return districtResult;
                    }
                }
                if (!a(str, districtResult, false)) {
                    if (this.b) {
                        b(str, districtResult);
                    } else if (!a(str, districtResult)) {
                        districtResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                    }
                }
                return districtResult;
            } catch (Exception e) {
            }
        }
        districtResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return districtResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetDistricSearchResultListener)) {
            ((OnGetDistricSearchResultListener) obj).onGetDistrictResult((DistrictResult) searchResult);
        }
    }

    public void a(boolean z) {
        this.b = z;
    }
}
