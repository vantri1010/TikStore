package com.baidu.platform.core.d;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.platform.base.d;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class k extends d {
    SuggestAddrInfo b = null;
    protected boolean c;

    private SuggestAddrInfo a(JSONObject jSONObject) {
        JSONObject optJSONObject;
        JSONObject jSONObject2;
        if (jSONObject == null || (optJSONObject = jSONObject.optJSONObject("traffic_pois")) == null) {
            return null;
        }
        JSONObject optJSONObject2 = optJSONObject.optJSONObject("option");
        JSONObject optJSONObject3 = optJSONObject.optJSONObject("content");
        if (!(optJSONObject2 == null || optJSONObject3 == null)) {
            JSONObject optJSONObject4 = optJSONObject2.optJSONObject("start_city");
            String optString = optJSONObject4 != null ? optJSONObject4.optString("cname") : null;
            JSONArray optJSONArray = optJSONObject2.optJSONArray("end_city");
            String optString2 = (optJSONArray == null || (jSONObject2 = (JSONObject) optJSONArray.opt(0)) == null) ? null : jSONObject2.optString("cname");
            JSONArray optJSONArray2 = optJSONObject2.optJSONArray("city_list");
            JSONArray optJSONArray3 = optJSONObject2.optJSONArray("prio_flag");
            if (!(optJSONArray2 == null || optJSONArray3 == null)) {
                int length = optJSONArray2.length();
                boolean[] zArr = new boolean[length];
                boolean[] zArr2 = new boolean[length];
                for (int i = 0; i < length; i++) {
                    int parseInt = Integer.parseInt(optJSONArray2.optString(i));
                    int parseInt2 = Integer.parseInt(optJSONArray3.optString(i));
                    boolean z = true;
                    zArr[i] = parseInt == 1;
                    if (parseInt2 != 1) {
                        z = false;
                    }
                    zArr2[i] = z;
                }
                SuggestAddrInfo suggestAddrInfo = new SuggestAddrInfo();
                for (int i2 = 0; i2 < length; i2++) {
                    if (!zArr2[i2]) {
                        if (zArr[i2]) {
                            if (i2 == 0) {
                                suggestAddrInfo.setSuggestStartCity(a(optJSONObject3.optJSONArray(TtmlNode.START)));
                            } else if (i2 != length - 1 || i2 <= 0) {
                                suggestAddrInfo.setSuggestWpCity(a(optJSONObject3, "multi_waypoints"));
                            } else {
                                suggestAddrInfo.setSuggestEndCity(a(optJSONObject3.optJSONArray(TtmlNode.END)));
                            }
                        } else if (i2 == 0) {
                            suggestAddrInfo.setSuggestStartNode(a(optJSONObject3.optJSONArray(TtmlNode.START), optString));
                        } else if (i2 != length - 1 || i2 <= 0) {
                            suggestAddrInfo.setSuggestWpNode(b(optJSONObject3, "multi_waypoints"));
                        } else {
                            suggestAddrInfo.setSuggestEndNode(a(optJSONObject3.optJSONArray(TtmlNode.END), optString2));
                        }
                    }
                }
                return suggestAddrInfo;
            }
        }
        return null;
    }

    private List<CityInfo> a(JSONArray jSONArray) {
        if (jSONArray == null || jSONArray.length() <= 0) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
            if (jSONObject != null) {
                CityInfo cityInfo = new CityInfo();
                cityInfo.num = jSONObject.optInt("num");
                cityInfo.city = jSONObject.optString("name");
                arrayList.add(cityInfo);
            }
        }
        arrayList.trimToSize();
        return arrayList;
    }

    private List<PoiInfo> a(JSONArray jSONArray, String str) {
        if (jSONArray == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = (JSONObject) jSONArray.opt(i);
            if (jSONObject != null) {
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.address = jSONObject.optString("addr");
                poiInfo.uid = jSONObject.optString("uid");
                poiInfo.name = jSONObject.optString("name");
                poiInfo.location = CoordUtil.decodeLocation(jSONObject.optString("geo"));
                poiInfo.city = str;
                arrayList.add(poiInfo);
            }
        }
        if (arrayList.size() > 0) {
            return arrayList;
        }
        return null;
    }

    private List<List<CityInfo>> a(JSONObject jSONObject, String str) {
        JSONArray optJSONArray;
        ArrayList arrayList = new ArrayList();
        if (jSONObject == null || (optJSONArray = jSONObject.optJSONArray(str)) == null) {
            return null;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            List<CityInfo> a = a((JSONArray) optJSONArray.opt(i));
            if (a != null) {
                arrayList.add(a);
            }
        }
        return arrayList;
    }

    private List<List<PoiInfo>> b(JSONObject jSONObject, String str) {
        JSONArray optJSONArray;
        ArrayList arrayList = new ArrayList();
        if (jSONObject == null || (optJSONArray = jSONObject.optJSONArray(str)) == null) {
            return null;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            List<PoiInfo> a = a(((JSONObject) optJSONArray.opt(i)).optJSONArray("way_ponits"), "");
            if (a != null) {
                arrayList.add(a);
            }
        }
        return arrayList;
    }

    private boolean b(String str) {
        if (str != null && str.length() > 0) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                JSONObject optJSONObject = jSONObject.optJSONObject("result");
                if (optJSONObject == null || optJSONObject.optInt("type") != 23 || optJSONObject.optInt("error") != 0) {
                    return false;
                }
                SuggestAddrInfo a = a(jSONObject);
                this.b = a;
                return a != null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: com.baidu.mapapi.search.route.DrivingRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: com.baidu.mapapi.search.route.WalkingRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: com.baidu.mapapi.search.route.TransitRouteResult} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.baidu.mapapi.search.core.SearchResult a(java.lang.String r4) {
        /*
            r3 = this;
            com.baidu.platform.base.SearchType r0 = r3.a()
            boolean r1 = r3.b(r4)
            r2 = 1
            if (r1 == 0) goto L_0x000e
            r3.c = r2
            goto L_0x0011
        L_0x000e:
            r1 = 0
            r3.c = r1
        L_0x0011:
            int[] r1 = com.baidu.platform.core.d.l.a
            int r0 = r0.ordinal()
            r0 = r1[r0]
            if (r0 == r2) goto L_0x0057
            r1 = 2
            if (r0 == r1) goto L_0x003d
            r1 = 3
            if (r0 == r1) goto L_0x0023
            r4 = 0
            goto L_0x0071
        L_0x0023:
            com.baidu.mapapi.search.route.WalkingRouteResult r0 = new com.baidu.mapapi.search.route.WalkingRouteResult
            r0.<init>()
            boolean r1 = r3.c
            if (r1 != 0) goto L_0x0033
            r1 = r3
            com.baidu.platform.core.d.o r1 = (com.baidu.platform.core.d.o) r1
            r1.a((java.lang.String) r4, (com.baidu.mapapi.search.route.WalkingRouteResult) r0)
            goto L_0x0070
        L_0x0033:
            com.baidu.mapapi.search.route.SuggestAddrInfo r4 = r3.b
            r0.setSuggestAddrInfo(r4)
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r4 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR
            r0.error = r4
            goto L_0x0070
        L_0x003d:
            com.baidu.mapapi.search.route.DrivingRouteResult r0 = new com.baidu.mapapi.search.route.DrivingRouteResult
            r0.<init>()
            boolean r1 = r3.c
            if (r1 != 0) goto L_0x004d
            r1 = r3
            com.baidu.platform.core.d.c r1 = (com.baidu.platform.core.d.c) r1
            r1.a((java.lang.String) r4, (com.baidu.mapapi.search.route.DrivingRouteResult) r0)
            goto L_0x0070
        L_0x004d:
            com.baidu.mapapi.search.route.SuggestAddrInfo r4 = r3.b
            r0.setSuggestAddrInfo(r4)
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r4 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR
            r0.error = r4
            goto L_0x0070
        L_0x0057:
            com.baidu.mapapi.search.route.TransitRouteResult r0 = new com.baidu.mapapi.search.route.TransitRouteResult
            r0.<init>()
            boolean r1 = r3.c
            if (r1 != 0) goto L_0x0067
            r1 = r3
            com.baidu.platform.core.d.m r1 = (com.baidu.platform.core.d.m) r1
            r1.a((java.lang.String) r4, (com.baidu.mapapi.search.route.TransitRouteResult) r0)
            goto L_0x0070
        L_0x0067:
            com.baidu.mapapi.search.route.SuggestAddrInfo r4 = r3.b
            r0.setSuggestAddrInfo(r4)
            com.baidu.mapapi.search.core.SearchResult$ERRORNO r4 = com.baidu.mapapi.search.core.SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR
            r0.error = r4
        L_0x0070:
            r4 = r0
        L_0x0071:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.platform.core.d.k.a(java.lang.String):com.baidu.mapapi.search.core.SearchResult");
    }

    public void a(SearchResult searchResult, Object obj) {
        if (obj != null && (obj instanceof OnGetRoutePlanResultListener)) {
            OnGetRoutePlanResultListener onGetRoutePlanResultListener = (OnGetRoutePlanResultListener) obj;
            int i = l.a[a().ordinal()];
            if (i == 1) {
                onGetRoutePlanResultListener.onGetTransitRouteResult((TransitRouteResult) searchResult);
            } else if (i == 2) {
                onGetRoutePlanResultListener.onGetDrivingRouteResult((DrivingRouteResult) searchResult);
            } else if (i == 3) {
                onGetRoutePlanResultListener.onGetWalkingRouteResult((WalkingRouteResult) searchResult);
            }
        }
    }
}
