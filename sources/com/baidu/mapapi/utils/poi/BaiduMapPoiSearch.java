package com.baidu.mapapi.utils.poi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.b;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.comapi.pano.a;
import com.baidu.platform.comapi.pano.c;
import com.google.android.exoplayer2.C;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.List;

public class BaiduMapPoiSearch {
    private static boolean a = true;

    private static void a(PoiParaOption poiParaOption, Context context) {
        Uri parse = Uri.parse("http://api.map.baidu.com/place/detail?" + "uid=" + poiParaOption.a + "&output=html" + "&src=" + context.getPackageName());
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        intent.setData(parse);
        context.startActivity(intent);
    }

    private static void b(PoiParaOption poiParaOption, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/place/search?");
        sb.append("query=");
        sb.append(poiParaOption.b);
        sb.append("&location=");
        LatLng latLng = poiParaOption.c;
        if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
            latLng = CoordTrans.gcjToBaidu(latLng);
        }
        sb.append(latLng.latitude);
        sb.append(",");
        sb.append(latLng.longitude);
        sb.append("&radius=");
        sb.append(poiParaOption.d);
        sb.append("&output=html");
        sb.append("&src=");
        sb.append(context.getPackageName());
        Uri parse = Uri.parse(sb.toString());
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        intent.setData(parse);
        context.startActivity(intent);
    }

    /* access modifiers changed from: private */
    public static void b(String str, Context context) {
        if (str == null || str.length() == 0) {
            throw new RuntimeException("BDMapSDKException: pano id can not be null.");
        } else if (context != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("baidumap://map/streetscape?");
            sb.append("panoid=");
            sb.append(str);
            sb.append("&pid=");
            sb.append(str);
            sb.append("&panotype=");
            sb.append("street");
            sb.append("&src=");
            sb.append("sdk_[" + context.getPackageName() + "]");
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
            intent.setFlags(C.ENCODING_PCM_MU_LAW);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return;
            }
            throw new RuntimeException("BDMapSDKException: BaiduMap app is not installed.");
        } else {
            throw new RuntimeException("BDMapSDKException: context cannot be null.");
        }
    }

    public static boolean dispatchPoiToBaiduMap(List<DispathcPoiData> list, Context context) throws Exception {
        String str;
        if (list.isEmpty() || list.size() <= 0) {
            throw new NullPointerException("BDMapSDKException: dispatch poidata is null");
        }
        int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
        if (baiduMapVersion == 0) {
            str = "BaiduMap app is not installed.";
        } else if (baiduMapVersion >= 840) {
            return b.a(list, context, 6);
        } else {
            str = "Baidumap app version is too lowl.Version is greater than 8.4";
        }
        Log.e("baidumapsdk", str);
        return false;
    }

    public static void finish(Context context) {
        if (context != null) {
            b.a(context);
        }
    }

    public static void openBaiduMapPanoShow(String str, Context context) {
        new a().a(str, (a.C0022a<c>) new a(context));
    }

    public static boolean openBaiduMapPoiDetialsPage(PoiParaOption poiParaOption, Context context) {
        if (poiParaOption == null || context == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (poiParaOption.a == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: poi uid can not be null.");
        } else if (poiParaOption.a.equals("")) {
            Log.e(BaiduMapRoutePlan.class.getName(), "poi uid can not be empty string");
            return false;
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    a(poiParaOption, context);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 810) {
                return b.a(poiParaOption, context, 3);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                if (a) {
                    a(poiParaOption, context);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.1");
            }
        }
    }

    public static boolean openBaiduMapPoiNearbySearch(PoiParaOption poiParaOption, Context context) {
        if (poiParaOption == null || context == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: para or context can not be null.");
        } else if (poiParaOption.b == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: poi search key can not be null.");
        } else if (poiParaOption.c == null) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: poi search center can not be null.");
        } else if (poiParaOption.c.longitude == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || poiParaOption.c.latitude == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: poi search center longitude or latitude can not be 0.");
        } else if (poiParaOption.d == 0) {
            throw new IllegalPoiSearchArgumentException("BDMapSDKException: poi search radius larger than 0.");
        } else if (poiParaOption.b.equals("")) {
            Log.e(BaiduMapRoutePlan.class.getName(), "poi key can not be empty string");
            return false;
        } else {
            int baiduMapVersion = OpenClientUtil.getBaiduMapVersion(context);
            if (baiduMapVersion == 0) {
                Log.e("baidumapsdk", "BaiduMap app is not installed.");
                if (a) {
                    b(poiParaOption, context);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: BaiduMap app is not installed.");
            } else if (baiduMapVersion >= 810) {
                return b.a(poiParaOption, context, 4);
            } else {
                Log.e("baidumapsdk", "Baidumap app version is too lowl.Version is greater than 8.1");
                if (a) {
                    b(poiParaOption, context);
                    return true;
                }
                throw new IllegalPoiSearchArgumentException("BDMapSDKException: Baidumap app version is too lowl.Version is greater than 8.1");
            }
        }
    }

    public static void setSupportWebPoi(boolean z) {
        a = z;
    }
}
