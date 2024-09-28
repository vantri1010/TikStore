package com.baidu.platform.core.d;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.TaxiInfo;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class m extends k {
    private RouteNode a(JSONObject jSONObject, String str) {
        if (jSONObject == null || str == null || "".equals(str)) {
            return null;
        }
        JSONObject optJSONObject = jSONObject.optJSONObject(str);
        RouteNode routeNode = new RouteNode();
        routeNode.setTitle(optJSONObject.optString("wd"));
        routeNode.setUid(optJSONObject.optString("uid"));
        routeNode.setLocation(CoordUtil.decodeLocation(optJSONObject.optString("pt")));
        return routeNode;
    }

    private TaxiInfo a(JSONObject jSONObject) {
        float f;
        float f2;
        float f3;
        if (jSONObject == null) {
            return null;
        }
        TaxiInfo taxiInfo = new TaxiInfo();
        JSONArray optJSONArray = jSONObject.optJSONArray("detail");
        if (optJSONArray == null || optJSONArray.length() <= 0) {
            return null;
        }
        int length = optJSONArray.length();
        int i = 0;
        while (true) {
            f = 0.0f;
            if (i >= length) {
                f2 = 0.0f;
                f3 = 0.0f;
                break;
            }
            JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i);
            if (jSONObject2 != null && jSONObject2.optString("desc").contains("白天")) {
                f2 = (float) jSONObject2.optDouble("km_price");
                f3 = (float) jSONObject2.optDouble("start_price");
                f = (float) jSONObject2.optDouble("total_price");
                break;
            }
            i++;
        }
        taxiInfo.setDesc(jSONObject.optString("remark"));
        taxiInfo.setDistance(jSONObject.optInt("distance"));
        taxiInfo.setDuration(jSONObject.optInt("duration"));
        taxiInfo.setTotalPrice(f);
        taxiInfo.setStartPrice(f3);
        taxiInfo.setPerKMPrice(f2);
        return taxiInfo;
    }

    private String b(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        boolean z = false;
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '<') {
                z = true;
            } else if (charArray[i] == '>') {
                z = false;
            } else if (!z) {
                sb.append(charArray[i]);
            }
        }
        return sb.toString();
    }

    private boolean b(String str, TransitRouteResult transitRouteResult) {
        RouteNode routeNode;
        JSONArray jSONArray;
        RouteNode routeNode2;
        JSONArray jSONArray2;
        SearchResult.ERRORNO errorno;
        String str2 = str;
        TransitRouteResult transitRouteResult2 = transitRouteResult;
        int i = 0;
        if (str2 == null || str.length() <= 0) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            JSONObject optJSONObject = jSONObject.optJSONObject("result");
            if (optJSONObject == null) {
                return false;
            }
            int optInt = optJSONObject.optInt("error");
            if (optInt != 0) {
                if (optInt == 1) {
                    errorno = SearchResult.ERRORNO.ST_EN_TOO_NEAR;
                } else if (optInt != 200) {
                    return false;
                } else {
                    errorno = SearchResult.ERRORNO.NOT_SUPPORT_BUS_2CITY;
                }
                transitRouteResult2.error = errorno;
                return true;
            }
            JSONObject optJSONObject2 = jSONObject.optJSONObject("bus");
            if (optJSONObject2 == null) {
                return false;
            }
            JSONObject optJSONObject3 = optJSONObject2.optJSONObject("taxi");
            if (optJSONObject3 != null) {
                transitRouteResult2.setTaxiInfo(a(optJSONObject3));
            }
            JSONObject optJSONObject4 = optJSONObject2.optJSONObject("option");
            if (optJSONObject4 == null) {
                return false;
            }
            RouteNode a = a(optJSONObject4, TtmlNode.START);
            RouteNode a2 = a(optJSONObject4, TtmlNode.END);
            JSONArray optJSONArray = optJSONObject2.optJSONArray("routes");
            if (optJSONArray == null || optJSONArray.length() <= 0) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            while (i2 < optJSONArray.length()) {
                JSONObject jSONObject2 = (JSONObject) ((JSONObject) optJSONArray.opt(i2)).optJSONArray("legs").opt(i);
                if (jSONObject2 != null) {
                    TransitRouteLine transitRouteLine = new TransitRouteLine();
                    transitRouteLine.setDistance(jSONObject2.optInt("distance"));
                    transitRouteLine.setDuration(jSONObject2.optInt("duration"));
                    transitRouteLine.setStarting(a);
                    transitRouteLine.setTerminal(a2);
                    JSONArray optJSONArray2 = jSONObject2.optJSONArray("steps");
                    if (optJSONArray2 != null && optJSONArray2.length() > 0) {
                        ArrayList arrayList2 = new ArrayList();
                        int i3 = 0;
                        while (i3 < optJSONArray2.length()) {
                            JSONArray optJSONArray3 = optJSONArray2.optJSONObject(i3).optJSONArray("step");
                            if (optJSONArray3 == null || optJSONArray3.length() <= 0) {
                                jSONArray2 = optJSONArray;
                                routeNode2 = a2;
                            } else {
                                JSONObject optJSONObject5 = optJSONArray3.optJSONObject(i);
                                TransitRouteLine.TransitStep transitStep = new TransitRouteLine.TransitStep();
                                transitStep.setEntrace(RouteNode.location(CoordUtil.decodeLocation(optJSONObject5.optString("start_location"))));
                                transitStep.setExit(RouteNode.location(CoordUtil.decodeLocation(optJSONObject5.optString("end_location"))));
                                jSONArray2 = optJSONArray;
                                routeNode2 = a2;
                                transitStep.setStepType(optJSONObject5.optInt("type") == 5 ? TransitRouteLine.TransitStep.TransitRouteStepType.WAKLING : TransitRouteLine.TransitStep.TransitRouteStepType.BUSLINE);
                                transitStep.setInstructions(b(optJSONObject5.optString("instructions")));
                                transitStep.setDistance(optJSONObject5.optInt("distance"));
                                transitStep.setDuration(optJSONObject5.optInt("duration"));
                                transitStep.setPathString(optJSONObject5.optString("path"));
                                if (optJSONObject5.has("vehicle")) {
                                    transitStep.setVehicleInfo(c(optJSONObject5.optString("vehicle")));
                                    JSONObject optJSONObject6 = optJSONObject5.optJSONObject("vehicle");
                                    transitStep.getEntrance().setUid(optJSONObject6.optString("start_uid"));
                                    transitStep.getEntrance().setTitle(optJSONObject6.optString("start_name"));
                                    transitStep.getExit().setUid(optJSONObject6.optString("end_uid"));
                                    transitStep.getExit().setTitle(optJSONObject6.optString("end_name"));
                                    Integer valueOf = Integer.valueOf(optJSONObject6.optInt("type"));
                                    transitStep.setStepType((valueOf == null || valueOf.intValue() != 1) ? TransitRouteLine.TransitStep.TransitRouteStepType.BUSLINE : TransitRouteLine.TransitStep.TransitRouteStepType.SUBWAY);
                                }
                                arrayList2.add(transitStep);
                            }
                            i3++;
                            optJSONArray = jSONArray2;
                            a2 = routeNode2;
                            i = 0;
                        }
                        jSONArray = optJSONArray;
                        routeNode = a2;
                        transitRouteLine.setSteps(arrayList2);
                        arrayList.add(transitRouteLine);
                        i2++;
                        optJSONArray = jSONArray;
                        a2 = routeNode;
                        i = 0;
                    }
                }
                jSONArray = optJSONArray;
                routeNode = a2;
                i2++;
                optJSONArray = jSONArray;
                a2 = routeNode;
                i = 0;
            }
            transitRouteResult2.setRoutelines(arrayList);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private VehicleInfo c(String str) {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jSONObject = null;
        }
        if (jSONObject == null) {
            return null;
        }
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.setZonePrice(jSONObject.optInt("zone_price"));
        vehicleInfo.setTotalPrice(jSONObject.optInt("total_price"));
        vehicleInfo.setTitle(jSONObject.optString("name"));
        vehicleInfo.setPassStationNum(jSONObject.optInt("stop_num"));
        vehicleInfo.setUid(jSONObject.optString("uid"));
        return vehicleInfo;
    }

    public void a(String str, TransitRouteResult transitRouteResult) {
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        transitRouteResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        transitRouteResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return;
                    }
                }
                if (!a(str, transitRouteResult, false) && !b(str, transitRouteResult)) {
                    transitRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                    return;
                }
                return;
            } catch (Exception e) {
            }
        }
        transitRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
    }
}
