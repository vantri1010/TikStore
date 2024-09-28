package com.baidu.platform.core.d;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.IndoorRouteLine;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.platform.base.d;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class f extends d {
    private LatLng a(JSONObject jSONObject, String str) {
        JSONArray optJSONArray = jSONObject.optJSONArray(str);
        if (optJSONArray == null) {
            return null;
        }
        GeoPoint geoPoint = new GeoPoint(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        geoPoint.setLatitudeE6(optJSONArray.optDouble(1));
        geoPoint.setLongitudeE6(optJSONArray.optDouble(0));
        return CoordUtil.mc2ll(geoPoint);
    }

    private boolean a(String str, IndoorRouteResult indoorRouteResult) {
        JSONObject optJSONObject;
        JSONObject optJSONObject2;
        int i;
        JSONArray jSONArray;
        IndoorRouteLine indoorRouteLine;
        String str2;
        String str3;
        IndoorRouteLine indoorRouteLine2;
        int i2;
        JSONArray jSONArray2;
        String str4;
        String str5;
        JSONArray jSONArray3;
        SearchResult.ERRORNO errorno;
        String str6 = str;
        IndoorRouteResult indoorRouteResult2 = indoorRouteResult;
        if (str6 == null || "".equals(str6)) {
            return false;
        }
        try {
            JSONObject optJSONObject3 = new JSONObject(str6).optJSONObject("indoor_navi");
            if (optJSONObject3 == null || (optJSONObject = optJSONObject3.optJSONObject("option")) == null) {
                return false;
            }
            int optInt = optJSONObject.optInt("error");
            if (optInt != 0) {
                if (optInt == 6) {
                    errorno = SearchResult.ERRORNO.INDOOR_ROUTE_NO_IN_BUILDING;
                } else if (optInt != 7) {
                    return false;
                } else {
                    errorno = SearchResult.ERRORNO.INDOOR_ROUTE_NO_IN_SAME_BUILDING;
                }
                indoorRouteResult2.error = errorno;
                return true;
            }
            JSONArray optJSONArray = optJSONObject3.optJSONArray("routes");
            if (optJSONArray == null || (optJSONObject2 = optJSONArray.optJSONObject(0)) == null) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            JSONArray optJSONArray2 = optJSONObject2.optJSONArray("legs");
            if (optJSONArray2 == null) {
                return false;
            }
            int i3 = 0;
            while (i3 < optJSONArray2.length()) {
                IndoorRouteLine indoorRouteLine3 = new IndoorRouteLine();
                JSONObject optJSONObject4 = optJSONArray2.optJSONObject(i3);
                if (optJSONObject4 == null) {
                    jSONArray = optJSONArray2;
                    i = i3;
                } else {
                    String str7 = "distance";
                    indoorRouteLine3.setDistance(optJSONObject4.optInt(str7));
                    String str8 = "duration";
                    indoorRouteLine3.setDuration(optJSONObject4.optInt(str8));
                    String str9 = "sstart_location";
                    indoorRouteLine3.setStarting(RouteNode.location(a(optJSONObject4, str9)));
                    String str10 = "send_location";
                    indoorRouteLine3.setTerminal(RouteNode.location(a(optJSONObject4, str10)));
                    JSONArray optJSONArray3 = optJSONObject4.optJSONArray("steps");
                    if (optJSONArray3 != null) {
                        ArrayList arrayList2 = new ArrayList();
                        int i4 = 0;
                        while (i4 < optJSONArray3.length()) {
                            IndoorRouteLine.IndoorRouteStep indoorRouteStep = new IndoorRouteLine.IndoorRouteStep();
                            JSONObject optJSONObject5 = optJSONArray3.optJSONObject(i4);
                            if (optJSONObject5 != null) {
                                indoorRouteStep.setDistance(optJSONObject5.optInt(str7));
                                indoorRouteStep.setDuration(optJSONObject5.optInt(str8));
                                indoorRouteStep.setBuildingId(optJSONObject5.optString("buildingid"));
                                indoorRouteStep.setFloorId(optJSONObject5.optString("floorid"));
                                indoorRouteStep.setEntrace(RouteNode.location(a(optJSONObject5, str9)));
                                indoorRouteStep.setExit(RouteNode.location(a(optJSONObject5, str10)));
                                JSONArray optJSONArray4 = optJSONObject5.optJSONArray("spath");
                                if (optJSONArray4 != null) {
                                    jSONArray3 = optJSONArray2;
                                    ArrayList arrayList3 = new ArrayList();
                                    jSONArray2 = optJSONArray3;
                                    str4 = str7;
                                    int i5 = 5;
                                    double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                                    double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                                    while (i5 < optJSONArray4.length()) {
                                        double optDouble = d + optJSONArray4.optDouble(i5 + 1);
                                        String str11 = str9;
                                        double optDouble2 = d2 + optJSONArray4.optDouble(i5);
                                        JSONArray jSONArray4 = optJSONArray4;
                                        GeoPoint geoPoint = new GeoPoint(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
                                        geoPoint.setLatitudeE6(optDouble);
                                        geoPoint.setLongitudeE6(optDouble2);
                                        LatLng mc2ll = CoordUtil.mc2ll(geoPoint);
                                        arrayList3.add(Double.valueOf(mc2ll.latitude));
                                        arrayList3.add(Double.valueOf(mc2ll.longitude));
                                        i5 += 2;
                                        optJSONArray4 = jSONArray4;
                                        i3 = i3;
                                        indoorRouteLine3 = indoorRouteLine3;
                                        d = optDouble;
                                        d2 = optDouble2;
                                        str8 = str8;
                                        str9 = str11;
                                        str10 = str10;
                                    }
                                    i2 = i3;
                                    indoorRouteLine2 = indoorRouteLine3;
                                    str5 = str8;
                                    str3 = str9;
                                    str2 = str10;
                                    indoorRouteStep.setPath(arrayList3);
                                    indoorRouteStep.setInstructions(optJSONObject5.optString("instructions"));
                                    JSONArray optJSONArray5 = optJSONObject5.optJSONArray("pois");
                                    if (optJSONArray5 != null) {
                                        ArrayList arrayList4 = new ArrayList();
                                        for (int i6 = 0; i6 < optJSONArray5.length(); i6++) {
                                            JSONObject optJSONObject6 = optJSONArray5.optJSONObject(i6);
                                            if (optJSONObject6 != null) {
                                                IndoorRouteLine.IndoorRouteStep.IndoorStepNode indoorStepNode = new IndoorRouteLine.IndoorRouteStep.IndoorStepNode();
                                                indoorStepNode.setDetail(optJSONObject6.optString("detail"));
                                                indoorStepNode.setName(optJSONObject6.optString("name"));
                                                indoorStepNode.setType(optJSONObject6.optInt("type"));
                                                indoorStepNode.setLocation(a(optJSONObject6, "location"));
                                                arrayList4.add(indoorStepNode);
                                            }
                                        }
                                        indoorRouteStep.setStepNodes(arrayList4);
                                    }
                                    arrayList2.add(indoorRouteStep);
                                    i4++;
                                    optJSONArray2 = jSONArray3;
                                    str8 = str5;
                                    str7 = str4;
                                    optJSONArray3 = jSONArray2;
                                    i3 = i2;
                                    indoorRouteLine3 = indoorRouteLine2;
                                    str9 = str3;
                                    str10 = str2;
                                }
                            }
                            jSONArray3 = optJSONArray2;
                            i2 = i3;
                            indoorRouteLine2 = indoorRouteLine3;
                            jSONArray2 = optJSONArray3;
                            str4 = str7;
                            str5 = str8;
                            str3 = str9;
                            str2 = str10;
                            i4++;
                            optJSONArray2 = jSONArray3;
                            str8 = str5;
                            str7 = str4;
                            optJSONArray3 = jSONArray2;
                            i3 = i2;
                            indoorRouteLine3 = indoorRouteLine2;
                            str9 = str3;
                            str10 = str2;
                        }
                        jSONArray = optJSONArray2;
                        i = i3;
                        IndoorRouteLine indoorRouteLine4 = indoorRouteLine3;
                        if (arrayList2.size() > 0) {
                            indoorRouteLine = indoorRouteLine4;
                            indoorRouteLine.setSteps(arrayList2);
                        } else {
                            indoorRouteLine = indoorRouteLine4;
                        }
                    } else {
                        jSONArray = optJSONArray2;
                        i = i3;
                        indoorRouteLine = indoorRouteLine3;
                    }
                    arrayList.add(indoorRouteLine);
                }
                i3 = i + 1;
                optJSONArray2 = jSONArray;
            }
            indoorRouteResult2.setRouteLines(arrayList);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public SearchResult a(String str) {
        IndoorRouteResult indoorRouteResult = new IndoorRouteResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        indoorRouteResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return indoorRouteResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        indoorRouteResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return indoorRouteResult;
                    }
                }
                if (!a(str, indoorRouteResult)) {
                    indoorRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                }
                return indoorRouteResult;
            } catch (Exception e) {
            }
        }
        indoorRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return indoorRouteResult;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetRoutePlanResultListener)) {
            ((OnGetRoutePlanResultListener) obj).onGetIndoorRouteResult((IndoorRouteResult) searchResult);
        }
    }
}
