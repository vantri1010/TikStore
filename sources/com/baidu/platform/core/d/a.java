package com.baidu.platform.core.d;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class a extends d {
    private LatLng a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        LatLng latLng = new LatLng(jSONObject.optDouble("lat"), jSONObject.optDouble("lng"));
        return SDKInitializer.getCoordType() == CoordType.GCJ02 ? CoordTrans.baiduToGcj(latLng) : latLng;
    }

    private RouteNode a(JSONObject jSONObject, String str, String str2) {
        JSONObject optJSONObject;
        if (jSONObject == null || str == null || "".equals(str) || (optJSONObject = jSONObject.optJSONObject(str)) == null) {
            return null;
        }
        RouteNode routeNode = new RouteNode();
        routeNode.setTitle(optJSONObject.optString("cname"));
        routeNode.setUid(optJSONObject.optString("uid"));
        JSONObject optJSONObject2 = optJSONObject.optJSONObject(str2);
        if (optJSONObject2 != null) {
            LatLng latLng = new LatLng(optJSONObject2.optDouble("lat"), optJSONObject2.optDouble("lng"));
            if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                latLng = CoordTrans.baiduToGcj(latLng);
            }
            routeNode.setLocation(latLng);
        }
        return routeNode;
    }

    private List<BikingRouteLine.BikingStep> a(JSONArray jSONArray) {
        boolean z = true;
        boolean z2 = jSONArray == null;
        int length = jSONArray.length();
        if (length > 0) {
            z = false;
        }
        if (z || z2) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                BikingRouteLine.BikingStep bikingStep = new BikingRouteLine.BikingStep();
                bikingStep.setDirection(optJSONObject.optInt("direction") * 30);
                bikingStep.setDistance(optJSONObject.optInt("distance"));
                bikingStep.setDuration(optJSONObject.optInt("duration"));
                bikingStep.setName(optJSONObject.optString("name"));
                bikingStep.setTurnType(optJSONObject.optString("turn_type"));
                bikingStep.setEntrance(RouteNode.location(a(optJSONObject.optJSONObject("stepOriginLocation"))));
                bikingStep.setExit(RouteNode.location(a(optJSONObject.optJSONObject("stepDestinationLocation"))));
                String optString = optJSONObject.optString("instructions");
                if (optString != null && optString.length() >= 4) {
                    optString = optString.replaceAll("</?[a-z]>", "");
                }
                bikingStep.setInstructions(optString);
                bikingStep.setEntranceInstructions(optJSONObject.optString("stepOriginInstruction"));
                bikingStep.setExitInstructions(optJSONObject.optString("stepDestinationInstruction"));
                bikingStep.setPathString(optJSONObject.optString("path"));
                arrayList.add(bikingStep);
            }
        }
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    private boolean a(String str, BikingRouteResult bikingRouteResult) {
        JSONArray optJSONArray;
        if (str != null && str.length() > 0) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                int optInt = jSONObject.optInt("status_sdk");
                if (optInt == 0) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("result");
                    if (optJSONObject == null) {
                        return false;
                    }
                    int optInt2 = jSONObject.optInt("type");
                    if (optInt2 == 1) {
                        bikingRouteResult.setSuggestAddrInfo(b(optJSONObject));
                        bikingRouteResult.error = SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR;
                    } else if (optInt2 != 2 || (optJSONArray = optJSONObject.optJSONArray("routes")) == null || optJSONArray.length() <= 0) {
                        return false;
                    } else {
                        RouteNode a = a(optJSONObject, TtmlNode.ATTR_TTS_ORIGIN, "originPt");
                        RouteNode a2 = a(optJSONObject, "destination", "destinationPt");
                        ArrayList arrayList = new ArrayList();
                        int i = 0;
                        while (i < optJSONArray.length()) {
                            BikingRouteLine bikingRouteLine = new BikingRouteLine();
                            try {
                                JSONObject optJSONObject2 = optJSONArray.optJSONObject(i);
                                if (optJSONObject2 == null) {
                                    return false;
                                }
                                bikingRouteLine.setStarting(a);
                                bikingRouteLine.setTerminal(a2);
                                bikingRouteLine.setDistance(optJSONObject2.optInt("distance"));
                                bikingRouteLine.setDuration(optJSONObject2.optInt("duration"));
                                bikingRouteLine.setSteps(a(optJSONObject2.optJSONArray("steps")));
                                arrayList.add(bikingRouteLine);
                                i++;
                            } catch (Exception e) {
                            }
                        }
                        bikingRouteResult.setRouteLines(arrayList);
                    }
                    return true;
                } else if (optInt != 1) {
                    if (optInt == 2) {
                        bikingRouteResult.error = SearchResult.ERRORNO.SEARCH_OPTION_ERROR;
                    }
                    return false;
                } else {
                    bikingRouteResult.error = SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                    return true;
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    private SuggestAddrInfo b(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        SuggestAddrInfo suggestAddrInfo = new SuggestAddrInfo();
        JSONObject optJSONObject = jSONObject.optJSONObject(TtmlNode.ATTR_TTS_ORIGIN);
        JSONObject optJSONObject2 = jSONObject.optJSONObject("destination");
        if (optJSONObject != null) {
            int optInt = optJSONObject.optInt("listType");
            String optString = optJSONObject.optString("cityName");
            if (optInt == 1) {
                suggestAddrInfo.setSuggestStartCity(a(optJSONObject, "content"));
            } else if (optInt == 0) {
                suggestAddrInfo.setSuggestStartNode(b(optJSONObject, "content", optString));
            }
        }
        if (optJSONObject2 != null) {
            int optInt2 = optJSONObject2.optInt("listType");
            String optString2 = optJSONObject2.optString("cityName");
            if (optInt2 == 1) {
                suggestAddrInfo.setSuggestEndCity(a(optJSONObject2, "content"));
            } else if (optInt2 == 0) {
                suggestAddrInfo.setSuggestEndNode(b(optJSONObject2, "content", optString2));
            }
        }
        return suggestAddrInfo;
    }

    private List<PoiInfo> b(JSONObject jSONObject, String str, String str2) {
        JSONArray optJSONArray;
        if (!(jSONObject == null || str == null || "".equals(str) || (optJSONArray = jSONObject.optJSONArray(str)) == null)) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i);
                if (jSONObject2 != null) {
                    PoiInfo poiInfo = new PoiInfo();
                    if (jSONObject2.has("address")) {
                        poiInfo.address = jSONObject2.optString("address");
                    }
                    poiInfo.uid = jSONObject2.optString("uid");
                    poiInfo.name = jSONObject2.optString("name");
                    JSONObject optJSONObject = jSONObject2.optJSONObject("location");
                    if (optJSONObject != null) {
                        poiInfo.location = new LatLng(optJSONObject.optDouble("lat"), optJSONObject.optDouble("lng"));
                        if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                            poiInfo.location = CoordTrans.baiduToGcj(poiInfo.location);
                        }
                    }
                    poiInfo.city = str2;
                    arrayList.add(poiInfo);
                }
            }
            if (arrayList.size() > 0) {
                return arrayList;
            }
        }
        return null;
    }

    public SearchResult a(String str) {
        BikingRouteResult bikingRouteResult = new BikingRouteResult();
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        bikingRouteResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return bikingRouteResult;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        bikingRouteResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return bikingRouteResult;
                    }
                }
                if (!a(str, bikingRouteResult, false) && !a(str, bikingRouteResult)) {
                    bikingRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                }
                return bikingRouteResult;
            } catch (Exception e) {
            }
        }
        bikingRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
        return bikingRouteResult;
    }

    public List<CityInfo> a(JSONObject jSONObject, String str) {
        JSONArray optJSONArray;
        if (jSONObject == null || str == null || str.equals("") || (optJSONArray = jSONObject.optJSONArray(str)) == null || optJSONArray.length() <= 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < optJSONArray.length(); i++) {
            JSONObject jSONObject2 = (JSONObject) optJSONArray.opt(i);
            if (jSONObject2 != null) {
                CityInfo cityInfo = new CityInfo();
                cityInfo.num = jSONObject2.optInt("number");
                cityInfo.city = jSONObject2.optString("name");
                arrayList.add(cityInfo);
            }
        }
        arrayList.trimToSize();
        return arrayList;
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetRoutePlanResultListener)) {
            ((OnGetRoutePlanResultListener) obj).onGetBikingRouteResult((BikingRouteResult) searchResult);
        }
    }
}
