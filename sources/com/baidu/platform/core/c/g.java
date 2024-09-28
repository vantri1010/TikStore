package com.baidu.platform.core.c;

import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiChildrenInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.d;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class g extends d {
    private static final String b = g.class.getSimpleName();
    private int c;
    private int d;

    g(int i, int i2) {
        this.c = i;
        this.d = i2;
    }

    private LatLng a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        double optDouble = jSONObject.optDouble("lat");
        double optDouble2 = jSONObject.optDouble("lng");
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(new LatLng(optDouble, optDouble2)) : new LatLng(optDouble, optDouble2);
    }

    private boolean a(String str, PoiResult poiResult) {
        if (str != null && !str.equals("") && !str.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                int optInt = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                if (optInt == 0) {
                    return a(jSONObject, poiResult);
                }
                poiResult.error = optInt != 1 ? optInt != 2 ? SearchResult.ERRORNO.RESULT_NOT_FOUND : SearchResult.ERRORNO.SEARCH_OPTION_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                return false;
            } catch (JSONException e) {
                Log.e(b, "Parse poi search failed", e);
                poiResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
            }
        }
        return false;
    }

    private boolean a(JSONObject jSONObject, PoiResult poiResult) {
        if (!(jSONObject == null || jSONObject.length() == 0)) {
            poiResult.error = SearchResult.ERRORNO.NO_ERROR;
            JSONArray optJSONArray = jSONObject.optJSONArray("results");
            if (optJSONArray == null || optJSONArray.length() <= 0) {
                poiResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
            } else {
                int optInt = jSONObject.optInt("total");
                poiResult.setTotalPoiNum(optInt);
                int length = optJSONArray.length();
                poiResult.setCurrentPageCapacity(length);
                poiResult.setCurrentPageNum(this.c);
                if (length != 0) {
                    int i = this.d;
                    poiResult.setTotalPageNum((optInt / i) + (optInt % i > 0 ? 1 : 0));
                }
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i2);
                    if (!(jSONObject2 == null || jSONObject2.length() == 0)) {
                        PoiInfo poiInfo = new PoiInfo();
                        poiInfo.setName(jSONObject2.optString("name"));
                        poiInfo.setAddress(jSONObject2.optString("address"));
                        poiInfo.setProvince(jSONObject2.optString("province"));
                        poiInfo.setCity(jSONObject2.optString("city"));
                        poiInfo.setArea(jSONObject2.optString("area"));
                        poiInfo.setStreetId(jSONObject2.optString("street_id"));
                        poiInfo.setUid(jSONObject2.optString("uid"));
                        poiInfo.setPhoneNum(jSONObject2.optString("telephone"));
                        poiInfo.setDetail(jSONObject2.optInt("detail"));
                        poiInfo.setLocation(a(jSONObject2.optJSONObject("location")));
                        String optString = jSONObject2.optString("detail_info");
                        if (!(optString == null || optString.length() == 0)) {
                            poiInfo.setPoiDetailInfo(b(optString));
                        }
                        arrayList.add(poiInfo);
                    }
                }
                poiResult.setPoiInfo(arrayList);
                return true;
            }
        }
        return false;
    }

    private PoiDetailInfo b(String str) {
        PoiDetailInfo poiDetailInfo = new PoiDetailInfo();
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.length() == 0) {
                return null;
            }
            poiDetailInfo.setDistance(jSONObject.optInt("distance", 0));
            poiDetailInfo.setTag(jSONObject.optString("tag"));
            poiDetailInfo.setDetailUrl(jSONObject.optString("detail_url"));
            poiDetailInfo.setType(jSONObject.optString("type"));
            poiDetailInfo.setPrice(jSONObject.optDouble("price", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setOverallRating(jSONObject.optDouble("overall_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setTasteRating(jSONObject.optDouble("taste_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setServiceRating(jSONObject.optDouble("service_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setEnvironmentRating(jSONObject.optDouble("environment_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setFacilityRating(jSONObject.optDouble("facility_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setHygieneRating(jSONObject.optDouble("hygiene_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setTechnologyRating(jSONObject.optDouble("technology_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailInfo.setImageNum(jSONObject.optInt("image_num"));
            poiDetailInfo.setGrouponNum(jSONObject.optInt("groupon_num"));
            poiDetailInfo.setCommentNum(jSONObject.optInt("comment_num"));
            poiDetailInfo.setDiscountNum(jSONObject.optInt("discount_num"));
            poiDetailInfo.setFavoriteNum(jSONObject.optInt("favorite_num"));
            poiDetailInfo.setCheckinNum(jSONObject.optInt("checkin_num"));
            poiDetailInfo.setShopHours(jSONObject.optString("shop_hours"));
            poiDetailInfo.naviLocation = a(jSONObject.optJSONObject("navi_location"));
            SearchType a = a();
            if (SearchType.POI_IN_CITY_SEARCH == a || SearchType.POI_NEAR_BY_SEARCH == a) {
                poiDetailInfo.setPoiChildrenInfoList(b(jSONObject));
            }
            return poiDetailInfo;
        } catch (JSONException e) {
            Log.e(b, "Parse poi search detail info failed", e);
            return null;
        }
    }

    private List<PoiChildrenInfo> b(JSONObject jSONObject) {
        JSONArray optJSONArray = jSONObject.optJSONArray("children");
        if (optJSONArray == null || optJSONArray.length() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (!(optJSONObject == null || optJSONObject.length() == 0)) {
                PoiChildrenInfo poiChildrenInfo = new PoiChildrenInfo();
                poiChildrenInfo.setUid(optJSONObject.optString("uid"));
                poiChildrenInfo.setName(optJSONObject.optString("name"));
                poiChildrenInfo.setShowName(optJSONObject.optString("show_name"));
                poiChildrenInfo.setTag(optJSONObject.optString("tag"));
                poiChildrenInfo.setLocation(a(optJSONObject.optJSONObject("location")));
                poiChildrenInfo.setAddress(optJSONObject.optString("address"));
                arrayList.add(poiChildrenInfo);
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0063, code lost:
        if (r7.equals("NETWORK_ERROR") != false) goto L_0x0067;
     */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.mapapi.search.core.SearchResult a(java.lang.String r7) {
        /*
            r6 = this;
            com.baidu.mapapi.search.poi.PoiResult r0 = new com.baidu.mapapi.search.poi.PoiResult
            r0.<init>()
            if (r7 == 0) goto L_0x0093
            java.lang.String r1 = ""
            boolean r1 = r7.equals(r1)
            if (r1 != 0) goto L_0x0093
            boolean r1 = r7.isEmpty()
            if (r1 == 0) goto L_0x0017
            goto L_0x0093
        L_0x0017:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x008b }
            r1.<init>(r7)     // Catch:{ JSONException -> 0x008b }
            java.lang.String r2 = "SDK_InnerError"
            boolean r3 = r1.has(r2)
            r4 = 0
            if (r3 == 0) goto L_0x0076
            org.json.JSONObject r1 = r1.optJSONObject(r2)
            java.lang.String r2 = "PermissionCheckError"
            boolean r2 = r1.has(r2)
            if (r2 == 0) goto L_0x0036
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.PERMISSION_UNFINISHED
        L_0x0033:
            r0.error = r7
            return r0
        L_0x0036:
            java.lang.String r2 = "httpStateError"
            boolean r3 = r1.has(r2)
            if (r3 == 0) goto L_0x0076
            java.lang.String r7 = r1.optString(r2)
            r1 = -1
            int r2 = r7.hashCode()
            r3 = -879828873(0xffffffffcb8ee077, float:-1.872715E7)
            r5 = 1
            if (r2 == r3) goto L_0x005d
            r3 = 1470557208(0x57a6ec18, float:3.6706589E14)
            if (r2 == r3) goto L_0x0053
            goto L_0x0066
        L_0x0053:
            java.lang.String r2 = "REQUEST_ERROR"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x0066
            r4 = 1
            goto L_0x0067
        L_0x005d:
            java.lang.String r2 = "NETWORK_ERROR"
            boolean r7 = r7.equals(r2)
            if (r7 == 0) goto L_0x0066
            goto L_0x0067
        L_0x0066:
            r4 = -1
        L_0x0067:
            if (r4 == 0) goto L_0x0071
            if (r4 == r5) goto L_0x006e
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR
            goto L_0x0073
        L_0x006e:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.REQUEST_ERROR
            goto L_0x0073
        L_0x0071:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.NETWORK_ERROR
        L_0x0073:
            r0.error = r7
            return r0
        L_0x0076:
            boolean r1 = r6.a(r7, r0, r4)
            if (r1 == 0) goto L_0x007d
            return r0
        L_0x007d:
            boolean r7 = r6.a((java.lang.String) r7, (com.baidu.mapapi.search.poi.PoiResult) r0)
            if (r7 == 0) goto L_0x0086
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.NO_ERROR
            goto L_0x0088
        L_0x0086:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.RESULT_NOT_FOUND
        L_0x0088:
            r0.error = r7
            return r0
        L_0x008b:
            r7 = move-exception
            java.lang.String r1 = b
            java.lang.String r2 = "Parse poi search error"
            android.util.Log.e(r1, r2, r7)
        L_0x0093:
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r7 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.RESULT_NOT_FOUND
            goto L_0x0033
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.platform.core.c.g.a(java.lang.String):com.baidu.mapapi.search.core.SearchResult");
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetPoiSearchResultListener)) {
            int i = h.a[a().ordinal()];
            if (i == 1 || i == 2 || i == 3) {
                ((OnGetPoiSearchResultListener) obj).onGetPoiResult((PoiResult) searchResult);
            }
        }
    }
}
