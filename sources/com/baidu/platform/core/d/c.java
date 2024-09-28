package com.baidu.platform.core.d;

import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.common.Logger;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.TaxiInfo;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c extends k {
    private RouteNode a(JSONArray jSONArray, List<RouteNode> list) {
        int length;
        if (jSONArray != null && (length = jSONArray.length()) > 0) {
            for (int i = 0; i < length; i++) {
                JSONObject optJSONObject = jSONArray.optJSONObject(i);
                if (optJSONObject != null) {
                    RouteNode a = a(optJSONObject);
                    if (i == length - 1) {
                        return a;
                    }
                    list.add(a);
                }
            }
        }
        return null;
    }

    private RouteNode a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        RouteNode routeNode = new RouteNode();
        routeNode.setTitle(jSONObject.optString("wd"));
        routeNode.setUid(jSONObject.optString("uid"));
        GeoPoint geoPoint = new GeoPoint(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        JSONArray optJSONArray = jSONObject.optJSONArray("spt");
        if (optJSONArray != null) {
            geoPoint.setLongitudeE6((double) optJSONArray.optInt(0));
            geoPoint.setLatitudeE6((double) optJSONArray.optInt(1));
        }
        routeNode.setLocation(CoordUtil.mc2ll(geoPoint));
        return routeNode;
    }

    private List<LatLng> a(JSONArray jSONArray) {
        int length;
        if (jSONArray == null || (length = jSONArray.length()) < 6) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double d2 = 0.0d;
        for (int i = 5; i < length; i++) {
            if (i % 2 != 0) {
                d2 += (double) jSONArray.optInt(i);
            } else {
                d += (double) jSONArray.optInt(i);
                arrayList.add(CoordUtil.mc2ll(new GeoPoint(d, d2)));
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep> a(org.json.JSONArray r20, org.json.JSONArray r21) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            if (r1 == 0) goto L_0x014f
            int r3 = r20.length()
            if (r3 > 0) goto L_0x0010
            goto L_0x014f
        L_0x0010:
            r4 = 0
            if (r2 == 0) goto L_0x001c
            int r6 = r21.length()
            if (r6 > 0) goto L_0x001a
            goto L_0x001d
        L_0x001a:
            r7 = 1
            goto L_0x001e
        L_0x001c:
            r6 = 0
        L_0x001d:
            r7 = 0
        L_0x001e:
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r9 = 0
            r10 = 0
        L_0x0025:
            if (r9 >= r3) goto L_0x014e
            org.json.JSONObject r11 = r1.optJSONObject(r9)
            if (r11 != 0) goto L_0x0030
            r13 = 1
            goto L_0x014a
        L_0x0030:
            com.baidu.mapapi.search.route.DrivingRouteLine$DrivingStep r12 = new com.baidu.mapapi.search.route.DrivingRouteLine$DrivingStep
            r12.<init>()
            java.lang.String r13 = "distance"
            int r13 = r11.optInt(r13)
            r12.setDistance(r13)
            java.lang.String r13 = "direction"
            int r13 = r11.optInt(r13)
            int r13 = r13 * 30
            r12.setDirection(r13)
            java.lang.String r13 = "instructions"
            java.lang.String r13 = r11.optString(r13)
            if (r13 == 0) goto L_0x006c
            int r14 = r13.length()
            r15 = 4
            if (r14 < r15) goto L_0x006c
            java.lang.String r14 = ""
            java.lang.String r15 = "/?[a-zA-Z]{1,10};"
            java.lang.String r13 = r13.replaceAll(r15, r14)
            java.lang.String r15 = "<[^>]*>"
            java.lang.String r13 = r13.replaceAll(r15, r14)
            java.lang.String r15 = "[(/>)<]"
            java.lang.String r13 = r13.replaceAll(r15, r14)
        L_0x006c:
            r12.setInstructions(r13)
            java.lang.String r13 = "start_instructions"
            java.lang.String r13 = r11.optString(r13)
            if (r13 != 0) goto L_0x00db
            int r13 = r12.getDistance()
            r14 = 1000(0x3e8, float:1.401E-42)
            java.lang.String r15 = " - "
            if (r13 >= r14) goto L_0x0099
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            r14.append(r13)
            java.lang.String r13 = "米"
            r14.append(r13)
            java.lang.String r13 = r14.toString()
            r16 = r6
            goto L_0x00b9
        L_0x0099:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            r16 = r6
            double r5 = (double) r13
            r17 = 4652007308841189376(0x408f400000000000, double:1000.0)
            double r5 = r5 / r17
            r14.append(r5)
            java.lang.String r5 = "公里"
            r14.append(r5)
            java.lang.String r5 = r14.toString()
            r13 = r5
        L_0x00b9:
            int r5 = r8.size()
            if (r10 > r5) goto L_0x00dd
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r6 = r10 + -1
            java.lang.Object r6 = r8.get(r6)
            com.baidu.mapapi.search.route.DrivingRouteLine$DrivingStep r6 = (com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep) r6
            java.lang.String r6 = r6.getExitInstructions()
            r5.append(r6)
            r5.append(r13)
            java.lang.String r13 = r5.toString()
            goto L_0x00dd
        L_0x00db:
            r16 = r6
        L_0x00dd:
            r12.setEntranceInstructions(r13)
            java.lang.String r5 = "end_instructions"
            java.lang.String r5 = r11.optString(r5)
            r12.setExitInstructions(r5)
            java.lang.String r5 = "turn"
            int r5 = r11.optInt(r5)
            r12.setNumTurns(r5)
            java.lang.String r5 = "road_level"
            int r5 = r11.optInt(r5)
            r12.setRoadLevel(r5)
            java.lang.String r5 = "spath"
            org.json.JSONArray r5 = r11.optJSONArray(r5)
            java.util.List r5 = r0.a((org.json.JSONArray) r5)
            r12.setPathList(r5)
            if (r5 == 0) goto L_0x0133
            com.baidu.mapapi.search.core.RouteNode r6 = new com.baidu.mapapi.search.core.RouteNode
            r6.<init>()
            java.lang.Object r11 = r5.get(r4)
            com.baidu.mapapi.model.LatLng r11 = (com.baidu.mapapi.model.LatLng) r11
            r6.setLocation(r11)
            r12.setEntrance(r6)
            com.baidu.mapapi.search.core.RouteNode r6 = new com.baidu.mapapi.search.core.RouteNode
            r6.<init>()
            int r11 = r5.size()
            r13 = 1
            int r11 = r11 - r13
            java.lang.Object r5 = r5.get(r11)
            com.baidu.mapapi.model.LatLng r5 = (com.baidu.mapapi.model.LatLng) r5
            r6.setLocation(r5)
            r12.setExit(r6)
            goto L_0x0134
        L_0x0133:
            r13 = 1
        L_0x0134:
            r6 = r16
            if (r7 == 0) goto L_0x0145
            if (r9 >= r6) goto L_0x0145
            org.json.JSONObject r5 = r2.optJSONObject(r9)
            int[] r5 = r0.b((org.json.JSONObject) r5)
            r12.setTrafficList(r5)
        L_0x0145:
            int r10 = r10 + 1
            r8.add(r12)
        L_0x014a:
            int r9 = r9 + 1
            goto L_0x0025
        L_0x014e:
            return r8
        L_0x014f:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.platform.core.d.c.a(org.json.JSONArray, org.json.JSONArray):java.util.List");
    }

    private List<TaxiInfo> b(String str) {
        if (str != null && str.length() > 0) {
            ArrayList arrayList = new ArrayList();
            try {
                JSONArray jSONArray = new JSONArray(str);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    if (jSONObject != null) {
                        TaxiInfo taxiInfo = new TaxiInfo();
                        String optString = jSONObject.optString("total_price");
                        if (optString != null) {
                            if (!optString.equals("")) {
                                taxiInfo.setTotalPrice(Float.parseFloat(optString));
                                arrayList.add(taxiInfo);
                            }
                        }
                        taxiInfo.setTotalPrice(0.0f);
                        arrayList.add(taxiInfo);
                    }
                }
                return arrayList;
            } catch (JSONException e) {
                if (Logger.debugEnable()) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private List<DrivingRouteLine.DrivingStep> b(JSONArray jSONArray, List<DrivingRouteLine.DrivingStep> list) {
        int length;
        if (jSONArray == null || (length = jSONArray.length()) <= 0 || list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            if (optJSONObject != null) {
                int optInt = optJSONObject.optInt("n");
                int optInt2 = optJSONObject.optInt("s");
                for (int i2 = 0; i2 < optInt; i2++) {
                    int i3 = optInt2 + i2;
                    if (i3 < list.size()) {
                        arrayList.add(list.get(i3));
                    }
                }
            }
        }
        return arrayList;
    }

    private boolean b(String str, DrivingRouteResult drivingRouteResult) {
        JSONObject jSONObject;
        JSONArray jSONArray;
        String str2 = str;
        DrivingRouteResult drivingRouteResult2 = drivingRouteResult;
        boolean z = false;
        if (str2 == null || "".equals(str2)) {
            return false;
        }
        try {
            JSONObject jSONObject2 = new JSONObject(str2);
            JSONObject optJSONObject = jSONObject2.optJSONObject("result");
            if (optJSONObject == null) {
                return false;
            }
            int optInt = optJSONObject.optInt("error");
            if (optInt == 0) {
                JSONObject optJSONObject2 = jSONObject2.optJSONObject("cars");
                if (optJSONObject2 == null) {
                    return false;
                }
                JSONObject optJSONObject3 = optJSONObject2.optJSONObject("option");
                JSONObject optJSONObject4 = optJSONObject2.optJSONObject("content");
                if (optJSONObject3 == null || optJSONObject4 == null) {
                    return false;
                }
                RouteNode a = a(optJSONObject3.optJSONObject(TtmlNode.START));
                ArrayList arrayList = new ArrayList();
                RouteNode a2 = a(optJSONObject3.optJSONArray(TtmlNode.END), (List<RouteNode>) arrayList);
                List<DrivingRouteLine.DrivingStep> a3 = a(optJSONObject4.optJSONArray("steps"), optJSONObject4.optJSONArray("stepts"));
                ArrayList arrayList2 = new ArrayList();
                JSONArray optJSONArray = optJSONObject4.optJSONArray("routes");
                if (optJSONArray == null) {
                    return false;
                }
                int i = 0;
                while (i < optJSONArray.length()) {
                    DrivingRouteLine drivingRouteLine = new DrivingRouteLine();
                    JSONObject optJSONObject5 = optJSONArray.optJSONObject(i);
                    if (optJSONObject5 == null) {
                        jSONObject = optJSONObject4;
                        jSONArray = optJSONArray;
                    } else {
                        JSONArray optJSONArray2 = optJSONObject5.optJSONArray("legs");
                        if (optJSONArray2 == null) {
                            return z;
                        }
                        int length = optJSONArray2.length();
                        ArrayList arrayList3 = new ArrayList();
                        jSONObject = optJSONObject4;
                        jSONArray = optJSONArray;
                        int i2 = 0;
                        int i3 = 0;
                        int i4 = 0;
                        while (i3 < length) {
                            int i5 = length;
                            JSONObject optJSONObject6 = optJSONArray2.optJSONObject(i3);
                            JSONArray jSONArray2 = optJSONArray2;
                            if (optJSONObject6 != null) {
                                i4 += optJSONObject6.optInt("distance");
                                i2 += optJSONObject6.optInt("duration");
                                List<DrivingRouteLine.DrivingStep> b = b(optJSONObject6.optJSONArray("stepis"), a3);
                                if (b != null) {
                                    arrayList3.addAll(b);
                                }
                            }
                            i3++;
                            length = i5;
                            optJSONArray2 = jSONArray2;
                        }
                        drivingRouteLine.setStarting(a);
                        drivingRouteLine.setTerminal(a2);
                        if (arrayList.size() == 0) {
                            drivingRouteLine.setWayPoints((List<RouteNode>) null);
                        } else {
                            drivingRouteLine.setWayPoints(arrayList);
                        }
                        drivingRouteLine.setDistance(i4);
                        drivingRouteLine.setDuration(i2);
                        drivingRouteLine.setCongestionDistance(optJSONObject5.optInt("congestion_length"));
                        drivingRouteLine.setLightNum(optJSONObject5.optInt("light_num"));
                        drivingRouteLine.setToll(optJSONObject5.optInt("toll"));
                        if (arrayList3.size() == 0) {
                            drivingRouteLine.setSteps((List) null);
                        } else {
                            drivingRouteLine.setSteps(arrayList3);
                        }
                        arrayList2.add(drivingRouteLine);
                    }
                    i++;
                    optJSONArray = jSONArray;
                    optJSONObject4 = jSONObject;
                    z = false;
                }
                drivingRouteResult2.setRouteLines(arrayList2);
                drivingRouteResult2.setTaxiInfos(b(optJSONObject4.optString("taxis")));
                return true;
            } else if (optInt != 4) {
                return false;
            } else {
                drivingRouteResult2.error = SearchResult.ERRORNO.ST_EN_TOO_NEAR;
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int[] b(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        JSONArray optJSONArray = jSONObject.optJSONArray(TtmlNode.END);
        JSONArray optJSONArray2 = jSONObject.optJSONArray(NotificationCompat.CATEGORY_STATUS);
        if (optJSONArray == null || optJSONArray2 == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int length = optJSONArray.length();
        int length2 = optJSONArray2.length();
        int i = 0;
        while (i < length) {
            int optInt = optJSONArray.optInt(i);
            int optInt2 = i < length2 ? optJSONArray2.optInt(i) : 0;
            for (int i2 = 0; i2 < optInt; i2++) {
                arrayList.add(Integer.valueOf(optInt2));
            }
            i++;
        }
        int[] iArr = new int[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            iArr[i3] = ((Integer) arrayList.get(i3)).intValue();
        }
        return iArr;
    }

    public void a(String str, DrivingRouteResult drivingRouteResult) {
        if (str != null && !str.equals("")) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("SDK_InnerError")) {
                    JSONObject optJSONObject = jSONObject.optJSONObject("SDK_InnerError");
                    if (optJSONObject.has("PermissionCheckError")) {
                        drivingRouteResult.error = SearchResult.ERRORNO.PERMISSION_UNFINISHED;
                        return;
                    } else if (optJSONObject.has("httpStateError")) {
                        String optString = optJSONObject.optString("httpStateError");
                        drivingRouteResult.error = optString.equals("NETWORK_ERROR") ? SearchResult.ERRORNO.NETWORK_ERROR : optString.equals("REQUEST_ERROR") ? SearchResult.ERRORNO.REQUEST_ERROR : SearchResult.ERRORNO.SEARCH_SERVER_INTERNAL_ERROR;
                        return;
                    }
                }
                if (!a(str, drivingRouteResult, false) && !b(str, drivingRouteResult)) {
                    drivingRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
                    return;
                }
                return;
            } catch (Exception e) {
            }
        }
        drivingRouteResult.error = SearchResult.ERRORNO.RESULT_NOT_FOUND;
    }
}
