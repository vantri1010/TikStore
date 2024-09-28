package im.bclpbkiauv.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import im.bclpbkiauv.messenger.R;

public class NaviUtils {
    public static final String PN_BAIDU_MAP = "com.baidu.BaiduMap";
    public static final String PN_GAODE_MAP = "com.autonavi.minimap";
    public static final String PN_TENCENT_MAP = "com.tencent.map";

    public static void startBaiduNavi(Context context, String sName, double sLat, double sLng, String dName, double dLat, double dLng) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage(PN_BAIDU_MAP);
        intent.setData(Uri.parse("baidumap://map/direction?mode=driving&" + "origin=latlng:" + sLat + "," + sLng + "|name:" + sName + "&destination=latlng:" + dLat + "," + dLng + "|name:" + dName));
        context.startActivity(intent);
    }

    public static void startGaodeNavi(Context context, String sName, double sLat, double sLng, String dName, double dLat, double dLng) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage(PN_GAODE_MAP);
        intent.setData(Uri.parse(("amapuri://route/plan?sourceApplication=" + context.getResources().getString(R.string.AppName)) + "&sname=" + sName + "&slat=" + sLat + "&slon=" + sLng + "&dname=" + dName + "&dlat=" + dLat + "&dlon=" + dLng + "&dev=0" + "&t=0"));
        context.startActivity(intent);
    }

    public static void startTencentNavi(Context context, String sName, double sLat, double sLng, String dName, double dLat, double dLng) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage(PN_TENCENT_MAP);
        intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&policy=0&referer=zhongshuo" + "&from=" + sName + "&fromcoord=" + sLat + "," + sLng + "&to=" + dName + "&tocoord=" + dLat + "," + dLng));
        context.startActivity(intent);
    }
}
