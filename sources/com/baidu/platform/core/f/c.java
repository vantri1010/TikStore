package com.baidu.platform.core.f;

import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiChildrenInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c extends d {
    private static final String b = c.class.getSimpleName();

    private LatLng a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        double optDouble = jSONObject.optDouble("lat");
        double optDouble2 = jSONObject.optDouble("lng");
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(new LatLng(optDouble, optDouble2)) : new LatLng(optDouble, optDouble2);
    }

    private List<PoiChildrenInfo> a(JSONArray jSONArray) {
        if (jSONArray == null || jSONArray.length() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            if (!(optJSONObject == null || optJSONObject.length() == 0)) {
                PoiChildrenInfo poiChildrenInfo = new PoiChildrenInfo();
                poiChildrenInfo.setUid(optJSONObject.optString("uid"));
                poiChildrenInfo.setName(optJSONObject.optString("name"));
                poiChildrenInfo.setShowName(optJSONObject.optString("show_name"));
                poiChildrenInfo.setTag(optJSONObject.optString("tag"));
                poiChildrenInfo.setAddress(optJSONObject.optString("address"));
                arrayList.add(poiChildrenInfo);
            }
        }
        return arrayList;
    }

    private boolean a(String str, SuggestionResult suggestionResult) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.length() != 0) {
                int optInt = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                if (optInt == 0) {
                    return a(jSONObject, suggestionResult);
                }
                suggestionResult.error = optInt != 1 ? optInt != 2 ? SearchResult.ERRORNO.RESULT_NOT_FOUND : SearchResult.ERRORNO.SEARCH_OPTION_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                return false;
            }
        } catch (JSONException e) {
            Log.e(b, "Parse sug search error", e);
        }
        suggestionResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return false;
    }

    private boolean a(JSONObject jSONObject, SuggestionResult suggestionResult) {
        if (!(jSONObject == null || jSONObject.length() == 0)) {
            suggestionResult.error = SearchResult.ERRORNO.NO_ERROR;
            JSONArray optJSONArray = jSONObject.optJSONArray("result");
            if (optJSONArray == null || optJSONArray.length() == 0) {
                suggestionResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < optJSONArray.length(); i++) {
                    JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i);
                    if (!(jSONObject2 == null || jSONObject2.length() == 0)) {
                        SuggestionResult.SuggestionInfo suggestionInfo = new SuggestionResult.SuggestionInfo();
                        suggestionInfo.setKey(jSONObject2.optString("name"));
                        suggestionInfo.setCity(jSONObject2.optString("city"));
                        suggestionInfo.setDistrict(jSONObject2.optString("district"));
                        suggestionInfo.setUid(jSONObject2.optString("uid"));
                        suggestionInfo.setTag(jSONObject2.optString("tag"));
                        suggestionInfo.setAddress(jSONObject2.optString("address"));
                        suggestionInfo.setPt(a(jSONObject2.optJSONObject("location")));
                        JSONArray optJSONArray2 = jSONObject2.optJSONArray("children");
                        if (!(optJSONArray2 == null || optJSONArray2.length() == 0)) {
                            suggestionInfo.setPoiChildrenInfoList(a(optJSONArray2));
                        }
                        arrayList.add(suggestionInfo);
                    }
                }
                suggestionResult.setSuggestionInfo(arrayList);
                return true;
            }
        }
        return false;
    }

    public SearchResult a(String str) {
        SearchResult.ERRORNO errorno;
        SuggestionResult suggestionResult = new SuggestionResult();
        if (str != null && !str.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.length() != 0) {
                    if (jSONObject.has("SDK_InnerError")) {
                        JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                        if (optJSONObject.has("PermissionCheckError")) {
                            errorno = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                            suggestionResult.error = errorno;
                            return suggestionResult;
                        } else if (optJSONObject.has("httpStateError")) {
                            String optString = optJSONObject.optString("httpStateError");
                            char c = 65535;
                            int hashCode = optString.hashCode();
                            if (hashCode != -879828873) {
                                if (hashCode == 1470557208 && optString.equals("REQUEST_ERROR")) {
                                    c = 1;
                                }
                            } else if (optString.equals("NETWORK_ERROR")) {
                                c = 0;
                            }
                            suggestionResult.error = c != 0 ? c != 1 ? SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR : SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.NETWORK_ERROR;
                            return suggestionResult;
                        }
                    }
                    if (!a(str, suggestionResult, true)) {
                        a(str, suggestionResult);
                    }
                    return suggestionResult;
                }
            } catch (JSONException e) {
                Log.e(b, "Parse suggestion search result error", e);
            }
        }
        errorno = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        suggestionResult.error = errorno;
        return suggestionResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetSuggestionResultListener)) {
            ((OnGetSuggestionResultListener) obj).onGetSuggestionResult((SuggestionResult) searchResult);
        }
    }
}
