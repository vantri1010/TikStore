package com.baidu.platform.core.c;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b extends d {
    private boolean a(String str, PoiIndoorResult poiIndoorResult) {
        SearchResult.ERRORNO errorno;
        if (str != null && !"".equals(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                int optInt = jSONObject.optInt("errNo");
                if (optInt != 0) {
                    if (optInt == 1) {
                        String optString = jSONObject.optString("Msg");
                        if (optString.contains("bid")) {
                            errorno = SearchResult.ERRORNO.POIINDOOR_BID_ERROR;
                        } else if (optString.contains("floor")) {
                            errorno = SearchResult.ERRORNO.POIINDOOR_FLOOR_ERROR;
                        }
                    } else if (optInt != 5) {
                        errorno = SearchResult.ERRORNO.POIINDOOR_SERVER_ERROR;
                    }
                    return false;
                }
                JSONObject optJSONObject = jSONObject.optJSONObject("data");
                if (optJSONObject == null) {
                    return false;
                }
                JSONArray optJSONArray = optJSONObject.optJSONArray("poi_list");
                if (optJSONArray == null || optJSONArray.length() <= 0) {
                    poiIndoorResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i);
                        if (jSONObject2 != null) {
                            PoiIndoorInfo poiIndoorInfo = new PoiIndoorInfo();
                            poiIndoorInfo.address = jSONObject2.optString("address");
                            poiIndoorInfo.bid = jSONObject2.optString("bd_id");
                            poiIndoorInfo.cid = jSONObject2.optInt("cid");
                            poiIndoorInfo.discount = jSONObject2.optInt("discount");
                            poiIndoorInfo.floor = jSONObject2.optString("floor");
                            poiIndoorInfo.name = jSONObject2.optString("name");
                            poiIndoorInfo.phone = jSONObject2.optString("phone");
                            poiIndoorInfo.price = (double) jSONObject2.optInt("price");
                            poiIndoorInfo.starLevel = jSONObject2.optInt("star_level");
                            poiIndoorInfo.tag = jSONObject2.optString("tag");
                            poiIndoorInfo.uid = jSONObject2.optString("uid");
                            poiIndoorInfo.groupNum = jSONObject2.optInt("tuan_nums");
                            int parseInt = Integer.parseInt(jSONObject2.optString("twp"));
                            if ((parseInt & 1) == 1) {
                                poiIndoorInfo.isGroup = true;
                            }
                            if ((parseInt & 2) == 1) {
                                poiIndoorInfo.isTakeOut = true;
                            }
                            if ((parseInt & 4) == 1) {
                                poiIndoorInfo.isWaited = true;
                            }
                            poiIndoorInfo.latLng = CoordUtil.mc2ll(new GeoPoint(jSONObject2.optDouble("pt_y"), jSONObject2.optDouble("pt_x")));
                            arrayList.add(poiIndoorInfo);
                        }
                    }
                    poiIndoorResult.error = SearchResult.ERRORNO.NO_ERROR;
                    poiIndoorResult.setmArrayPoiInfo(arrayList);
                }
                poiIndoorResult.pageNum = optJSONObject.optInt("page_num");
                poiIndoorResult.poiNum = optJSONObject.optInt("poi_num");
                errorno = SearchResult.ERRORNO.NO_ERROR;
                poiIndoorResult.error = errorno;
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public SearchResult a(String str) {
        PoiIndoorResult poiIndoorResult = new PoiIndoorResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        poiIndoorResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return poiIndoorResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        poiIndoorResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return poiIndoorResult;
                    }
                }
                if (!a(str, poiIndoorResult, false) && !a(str, poiIndoorResult)) {
                    poiIndoorResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                }
                return poiIndoorResult;
            } catch (Exception e) {
            }
        }
        poiIndoorResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return poiIndoorResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetPoiSearchResultListener)) {
            ((OnGetPoiSearchResultListener) obj).onGetPoiIndoorResult((PoiIndoorResult) searchResult);
        }
    }
}
