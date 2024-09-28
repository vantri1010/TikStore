package com.baidu.platform.core.b;

import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class e extends d {
    private PoiInfo.ParentPoiInfo a(JSONObject jSONObject) {
        if (jSONObject == null || jSONObject.length() == 0) {
            return null;
        }
        PoiInfo.ParentPoiInfo parentPoiInfo = new PoiInfo.ParentPoiInfo();
        parentPoiInfo.setParentPoiAddress(jSONObject.optString("addr"));
        parentPoiInfo.setParentPoiDirection(jSONObject.optString("direction"));
        parentPoiInfo.setParentPoiDistance(jSONObject.optInt("distance"));
        parentPoiInfo.setParentPoiName(jSONObject.optString("name"));
        parentPoiInfo.setParentPoiTag(jSONObject.optString("tag"));
        parentPoiInfo.setParentPoiUid(jSONObject.optString("uid"));
        parentPoiInfo.setParentPoiLocation(c(jSONObject, "point"));
        return parentPoiInfo;
    }

    private ReverseGeoCodeResult.AddressComponent a(JSONObject jSONObject, String str) {
        JSONObject optJSONObject;
        if (jSONObject == null || TextUtils.isEmpty(str) || (optJSONObject = jSONObject.optJSONObject(str)) == null) {
            return null;
        }
        ReverseGeoCodeResult.AddressComponent addressComponent = new ReverseGeoCodeResult.AddressComponent();
        addressComponent.city = optJSONObject.optString("city");
        addressComponent.setTown(optJSONObject.optString("town"));
        addressComponent.district = optJSONObject.optString("district");
        addressComponent.province = optJSONObject.optString("province");
        addressComponent.adcode = optJSONObject.optInt("adcode");
        addressComponent.street = optJSONObject.optString("street");
        addressComponent.streetNumber = optJSONObject.optString("street_number");
        addressComponent.countryName = optJSONObject.optString("country");
        addressComponent.countryCode = optJSONObject.optInt("country_code");
        addressComponent.setDirection(optJSONObject.optString("direction"));
        addressComponent.setDistance(optJSONObject.optString("distance"));
        return addressComponent;
    }

    private List<PoiInfo> a(JSONObject jSONObject, String str, String str2) {
        JSONArray optJSONArray;
        if (jSONObject == null || str == null || "".equals(str) || (optJSONArray = jSONObject.optJSONArray(str)) == null || optJSONArray.length() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.setAddress(optJSONObject.optString("addr"));
                poiInfo.setPhoneNum(optJSONObject.optString("tel"));
                poiInfo.setUid(optJSONObject.optString("uid"));
                poiInfo.setPostCode(optJSONObject.optString("zip"));
                poiInfo.setName(optJSONObject.optString("name"));
                poiInfo.setLocation(c(optJSONObject, "point"));
                poiInfo.setCity(str2);
                poiInfo.setDirection(optJSONObject.optString("direction"));
                poiInfo.setDistance(optJSONObject.optInt("distance"));
                poiInfo.setTag(optJSONObject.optString("tag"));
                poiInfo.setParentPoi(a(optJSONObject.optJSONObject("parent_poi")));
                arrayList.add(poiInfo);
            }
        }
        return arrayList;
    }

    private boolean a(String str, ReverseGeoCodeResult reverseGeoCodeResult) {
        if (str != null) {
            try {
                if (str.length() > 0) {
                    JSONObject jSONObject = new JSONObject(str);
                    int optInt = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                    if (optInt != 0) {
                        reverseGeoCodeResult.error = optInt != 1 ? optInt != 2 ? SearchResult.ERRORNO.RESULT_NOT_FOUND : SearchResult.ERRORNO.SEARCH_OPTION_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return false;
                    } else if (a(jSONObject, reverseGeoCodeResult)) {
                        return true;
                    } else {
                        reverseGeoCodeResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                reverseGeoCodeResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                return false;
            }
        }
        reverseGeoCodeResult.error = SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
        return false;
    }

    private boolean a(JSONObject jSONObject, ReverseGeoCodeResult reverseGeoCodeResult) {
        JSONObject optJSONObject;
        String str;
        if (jSONObject == null || (optJSONObject = jSONObject.optJSONObject("result")) == null) {
            return false;
        }
        reverseGeoCodeResult.setCityCode(optJSONObject.optInt("cityCode"));
        reverseGeoCodeResult.setAddress(optJSONObject.optString("formatted_address"));
        reverseGeoCodeResult.setBusinessCircle(optJSONObject.optString("business"));
        ReverseGeoCodeResult.AddressComponent a = a(optJSONObject, "addressComponent");
        reverseGeoCodeResult.setAddressDetail(a);
        reverseGeoCodeResult.setLocation(d(optJSONObject, "location"));
        if (a != null) {
            str = a.city;
            reverseGeoCodeResult.setAdcode(a.adcode);
        } else {
            str = "";
        }
        reverseGeoCodeResult.setPoiList(a(optJSONObject, "pois", str));
        reverseGeoCodeResult.setSematicDescription(optJSONObject.optString("sematic_description"));
        reverseGeoCodeResult.setPoiRegionsInfoList(b(optJSONObject, "poiRegions"));
        reverseGeoCodeResult.error = SearchResult.ERRORNO.NO_ERROR;
        return true;
    }

    private List<ReverseGeoCodeResult.PoiRegionsInfo> b(JSONObject jSONObject, String str) {
        JSONArray optJSONArray;
        if (jSONObject == null || TextUtils.isEmpty(str) || (optJSONArray = jSONObject.optJSONArray(str)) == null || optJSONArray.length() == 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                ReverseGeoCodeResult.PoiRegionsInfo poiRegionsInfo = new ReverseGeoCodeResult.PoiRegionsInfo();
                poiRegionsInfo.setDirectionDesc(optJSONObject.optString("direction_desc"));
                poiRegionsInfo.setRegionName(optJSONObject.optString("name"));
                poiRegionsInfo.setRegionTag(optJSONObject.optString("tag"));
                arrayList.add(poiRegionsInfo);
            }
        }
        return arrayList;
    }

    private LatLng c(JSONObject jSONObject, String str) {
        JSONObject optJSONObject;
        if (jSONObject == null || str == null || "".equals(str) || (optJSONObject = jSONObject.optJSONObject(str)) == null) {
            return null;
        }
        LatLng latLng = new LatLng(optJSONObject.optDouble("y"), optJSONObject.optDouble("x"));
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(latLng) : latLng;
    }

    private LatLng d(JSONObject jSONObject, String str) {
        JSONObject optJSONObject;
        if (jSONObject == null || str == null || "".equals(str) || (optJSONObject = jSONObject.optJSONObject(str)) == null) {
            return null;
        }
        LatLng latLng = new LatLng(optJSONObject.optDouble("lat"), optJSONObject.optDouble("lng"));
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(latLng) : latLng;
    }

    public SearchResult a(String str) {
        ReverseGeoCodeResult reverseGeoCodeResult = new ReverseGeoCodeResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        reverseGeoCodeResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return reverseGeoCodeResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        reverseGeoCodeResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return reverseGeoCodeResult;
                    }
                }
                if (!a(str, reverseGeoCodeResult, true)) {
                    a(str, reverseGeoCodeResult);
                }
                return reverseGeoCodeResult;
            } catch (Exception e) {
            }
        }
        reverseGeoCodeResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return reverseGeoCodeResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetGeoCoderResultListener)) {
            ((OnGetGeoCoderResultListener) obj).onGetReverseGeoCodeResult((ReverseGeoCodeResult) searchResult);
        }
    }
}
