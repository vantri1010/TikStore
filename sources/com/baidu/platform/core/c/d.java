package com.baidu.platform.core.c;

import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class d extends com.baidu.platform.base.d {
    private static final String b = d.class.getSimpleName();
    private boolean c = false;

    private LatLng a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        double optDouble = jSONObject.optDouble("lat");
        double optDouble2 = jSONObject.optDouble("lng");
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(new LatLng(optDouble, optDouble2)) : new LatLng(optDouble, optDouble2);
    }

    private boolean a(String str, SearchResult searchResult) {
        JSONArray optJSONArray;
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.length() == 0 || jSONObject.optInt(NotificationCompat.CATEGORY_STATUS) != 0 || (optJSONArray = jSONObject.optJSONArray("result")) == null || optJSONArray.length() == 0) {
                return false;
            }
            return this.c ? a(optJSONArray, (PoiDetailSearchResult) searchResult) : a(optJSONArray, (PoiDetailResult) searchResult);
        } catch (JSONException e) {
            Log.e(b, "Parse detail search result error", e);
            return false;
        }
    }

    private boolean a(JSONArray jSONArray, PoiDetailResult poiDetailResult) {
        JSONObject jSONObject = (JSONObject) jSONArray.opt(0);
        if (jSONObject == null || jSONObject.length() == 0) {
            return false;
        }
        poiDetailResult.setName(jSONObject.optString("name"));
        poiDetailResult.setLocation(a(jSONObject.optJSONObject("location")));
        poiDetailResult.setAddress(jSONObject.optString("address"));
        poiDetailResult.setTelephone(jSONObject.optString("telephone"));
        poiDetailResult.setUid(jSONObject.optString("uid"));
        JSONObject optJSONObject = jSONObject.optJSONObject("detail_info");
        if (!(optJSONObject == null || optJSONObject.length() == 0)) {
            poiDetailResult.setTag(optJSONObject.optString("tag"));
            poiDetailResult.setDetailUrl(optJSONObject.optString("detail_url"));
            poiDetailResult.setType(optJSONObject.optString("type"));
            poiDetailResult.setPrice(optJSONObject.optDouble("price", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setOverallRating(optJSONObject.optDouble("overall_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setTasteRating(optJSONObject.optDouble("taste_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setServiceRating(optJSONObject.optDouble("service_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setEnvironmentRating(optJSONObject.optDouble("environment_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setFacilityRating(optJSONObject.optDouble("facility_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setHygieneRating(optJSONObject.optDouble("hygiene_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setTechnologyRating(optJSONObject.optDouble("technology_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
            poiDetailResult.setImageNum(optJSONObject.optInt("image_num"));
            poiDetailResult.setGrouponNum(optJSONObject.optInt("groupon_num", 0));
            poiDetailResult.setCommentNum(optJSONObject.optInt("comment_num", 0));
            poiDetailResult.setDiscountNum(optJSONObject.optInt("discount_num", 0));
            poiDetailResult.setFavoriteNum(optJSONObject.optInt("favorite_num", 0));
            poiDetailResult.setCheckinNum(optJSONObject.optInt("checkin_num", 0));
            poiDetailResult.setShopHours(optJSONObject.optString("shop_hours"));
        }
        poiDetailResult.error = SearchResult.ERRORNO.NO_ERROR;
        return true;
    }

    private boolean a(JSONArray jSONArray, PoiDetailSearchResult poiDetailSearchResult) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
            if (!(jSONObject == null || jSONObject.length() == 0)) {
                PoiDetailInfo poiDetailInfo = new PoiDetailInfo();
                poiDetailInfo.setName(jSONObject.optString("name"));
                poiDetailInfo.setLocation(a(jSONObject.optJSONObject("location")));
                poiDetailInfo.setAddress(jSONObject.optString("address"));
                poiDetailInfo.setProvince(jSONObject.optString("province"));
                poiDetailInfo.setCity(jSONObject.optString("city"));
                poiDetailInfo.setArea(jSONObject.optString("area"));
                poiDetailInfo.setTelephone(jSONObject.optString("telephone"));
                poiDetailInfo.setUid(jSONObject.optString("uid"));
                poiDetailInfo.setStreetId(jSONObject.optString("setStreetId"));
                poiDetailInfo.setDetail(jSONObject.optString("detail"));
                JSONObject optJSONObject = jSONObject.optJSONObject("detail_info");
                if (!(optJSONObject == null || optJSONObject.length() == 0)) {
                    poiDetailInfo.setDistance(optJSONObject.optInt("distance", 0));
                    poiDetailInfo.setType(optJSONObject.optString("type"));
                    poiDetailInfo.setTag(optJSONObject.optString("tag"));
                    poiDetailInfo.setDetailUrl(optJSONObject.optString("detail_url"));
                    poiDetailInfo.setPrice(optJSONObject.optDouble("price", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setShopHours(optJSONObject.optString("shop_hours"));
                    poiDetailInfo.setOverallRating(optJSONObject.optDouble("overall_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setTasteRating(optJSONObject.optDouble("taste_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setServiceRating(optJSONObject.optDouble("service_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setEnvironmentRating(optJSONObject.optDouble("environment_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setFacilityRating(optJSONObject.optDouble("facility_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setHygieneRating(optJSONObject.optDouble("hygiene_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setTechnologyRating(optJSONObject.optDouble("technology_rating", FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    poiDetailInfo.setImageNum(optJSONObject.optInt("image_num"));
                    poiDetailInfo.setGrouponNum(optJSONObject.optInt("groupon_num", 0));
                    poiDetailInfo.setCommentNum(optJSONObject.optInt("comment_num", 0));
                    poiDetailInfo.setDiscountNum(optJSONObject.optInt("discount_num", 0));
                    poiDetailInfo.setFavoriteNum(optJSONObject.optInt("favorite_num", 0));
                    poiDetailInfo.setCheckinNum(optJSONObject.optInt("checkin_num", 0));
                }
                arrayList.add(poiDetailInfo);
            }
        }
        poiDetailSearchResult.setPoiDetailInfoList(arrayList);
        poiDetailSearchResult.error = SearchResult.ERRORNO.NO_ERROR;
        return true;
    }

    public SearchResult a(String str) {
        SearchResult.ERRORNO errorno;
        SearchResult poiDetailSearchResult = this.c ? new PoiDetailSearchResult() : new PoiDetailResult();
        if (str != null && !str.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.length() == 0) {
                    poiDetailSearchResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                    return poiDetailSearchResult;
                } else if (!jSONObject.has("SDK_InnerError")) {
                    if (!a(str, poiDetailSearchResult)) {
                        poiDetailSearchResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                    }
                    return poiDetailSearchResult;
                } else {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (!(optJSONObject == null || optJSONObject.length() == 0)) {
                        if (optJSONObject.has("PermissionCheckError")) {
                            errorno = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                            poiDetailSearchResult.error = errorno;
                            return poiDetailSearchResult;
                        }
                        if (optJSONObject.has("httpStateError")) {
                            String optString = optJSONObject.optString("httpStateError");
                            char c2 = 65535;
                            int hashCode = optString.hashCode();
                            if (hashCode != -879828873) {
                                if (hashCode == 1470557208 && optString.equals("REQUEST_ERROR")) {
                                    c2 = 1;
                                }
                            } else if (optString.equals("NETWORK_ERROR")) {
                                c2 = 0;
                            }
                            poiDetailSearchResult.error = c2 != 0 ? c2 != 1 ? SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR : SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.NETWORK_ERROR;
                        }
                        return poiDetailSearchResult;
                    }
                }
            } catch (JSONException e) {
                Log.e(b, "Parse detail search result failed", e);
            }
        }
        errorno = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        poiDetailSearchResult.error = errorno;
        return poiDetailSearchResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetPoiSearchResultListener)) {
            OnGetPoiSearchResultListener onGetPoiSearchResultListener = (OnGetPoiSearchResultListener) obj;
            if (this.c) {
                onGetPoiSearchResultListener.onGetPoiDetailResult((PoiDetailSearchResult) searchResult);
            } else {
                onGetPoiSearchResultListener.onGetPoiDetailResult((PoiDetailResult) searchResult);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        this.c = z;
    }
}
