package com.baidu.mapapi.utils;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import java.util.List;

public class SpatialRelationUtil {
    private static LatLng a(LatLng latLng, LatLng latLng2, LatLng latLng3) {
        GeoPoint ll2mc = CoordUtil.ll2mc(latLng);
        GeoPoint ll2mc2 = CoordUtil.ll2mc(latLng2);
        GeoPoint ll2mc3 = CoordUtil.ll2mc(latLng3);
        double sqrt = Math.sqrt(((ll2mc2.getLongitudeE6() - ll2mc.getLongitudeE6()) * (ll2mc2.getLongitudeE6() - ll2mc.getLongitudeE6())) + ((ll2mc2.getLatitudeE6() - ll2mc.getLatitudeE6()) * (ll2mc2.getLatitudeE6() - ll2mc.getLatitudeE6())));
        double longitudeE6 = (((ll2mc2.getLongitudeE6() - ll2mc.getLongitudeE6()) * (ll2mc3.getLongitudeE6() - ll2mc.getLongitudeE6())) + ((ll2mc2.getLatitudeE6() - ll2mc.getLatitudeE6()) * (ll2mc3.getLatitudeE6() - ll2mc.getLatitudeE6()))) / (sqrt * sqrt);
        return CoordUtil.mc2ll(new GeoPoint(ll2mc.getLatitudeE6() + ((ll2mc2.getLatitudeE6() - ll2mc.getLatitudeE6()) * longitudeE6), ll2mc.getLongitudeE6() + ((ll2mc2.getLongitudeE6() - ll2mc.getLongitudeE6()) * longitudeE6)));
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [java.util.List, java.util.List<com.baidu.mapapi.model.LatLng>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.baidu.mapapi.model.LatLng getNearestPointFromLine(java.util.List<com.baidu.mapapi.model.LatLng> r12, com.baidu.mapapi.model.LatLng r13) {
        /*
            r0 = 0
            if (r12 == 0) goto L_0x0099
            int r1 = r12.size()
            if (r1 == 0) goto L_0x0099
            if (r13 != 0) goto L_0x000d
            goto L_0x0099
        L_0x000d:
            r1 = 0
        L_0x000e:
            int r2 = r12.size()
            int r2 = r2 + -1
            if (r1 >= r2) goto L_0x0099
            java.lang.Object r2 = r12.get(r1)
            com.baidu.mapapi.model.LatLng r2 = (com.baidu.mapapi.model.LatLng) r2
            int r3 = r1 + 1
            java.lang.Object r4 = r12.get(r3)
            com.baidu.mapapi.model.LatLng r4 = (com.baidu.mapapi.model.LatLng) r4
            com.baidu.mapapi.model.LatLng r2 = a(r2, r4, r13)
            double r4 = r2.latitude
            java.lang.Object r6 = r12.get(r1)
            com.baidu.mapapi.model.LatLng r6 = (com.baidu.mapapi.model.LatLng) r6
            double r6 = r6.latitude
            double r4 = r4 - r6
            double r6 = r2.latitude
            java.lang.Object r8 = r12.get(r3)
            com.baidu.mapapi.model.LatLng r8 = (com.baidu.mapapi.model.LatLng) r8
            double r8 = r8.latitude
            double r6 = r6 - r8
            double r4 = r4 * r6
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 > 0) goto L_0x0063
            double r4 = r2.longitude
            java.lang.Object r8 = r12.get(r1)
            com.baidu.mapapi.model.LatLng r8 = (com.baidu.mapapi.model.LatLng) r8
            double r8 = r8.longitude
            double r4 = r4 - r8
            double r8 = r2.longitude
            java.lang.Object r10 = r12.get(r3)
            com.baidu.mapapi.model.LatLng r10 = (com.baidu.mapapi.model.LatLng) r10
            double r10 = r10.longitude
            double r8 = r8 - r10
            double r4 = r4 * r8
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 > 0) goto L_0x0063
            goto L_0x0087
        L_0x0063:
            java.lang.Object r2 = r12.get(r1)
            com.baidu.mapapi.model.LatLng r2 = (com.baidu.mapapi.model.LatLng) r2
            double r4 = com.baidu.mapapi.utils.DistanceUtil.getDistance(r13, r2)
            java.lang.Object r2 = r12.get(r3)
            com.baidu.mapapi.model.LatLng r2 = (com.baidu.mapapi.model.LatLng) r2
            double r6 = com.baidu.mapapi.utils.DistanceUtil.getDistance(r13, r2)
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x0080
            java.lang.Object r1 = r12.get(r1)
            goto L_0x0084
        L_0x0080:
            java.lang.Object r1 = r12.get(r3)
        L_0x0084:
            com.baidu.mapapi.model.LatLng r1 = (com.baidu.mapapi.model.LatLng) r1
            r2 = r1
        L_0x0087:
            if (r0 == 0) goto L_0x0095
            double r4 = com.baidu.mapapi.utils.DistanceUtil.getDistance(r13, r2)
            double r6 = com.baidu.mapapi.utils.DistanceUtil.getDistance(r13, r0)
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 >= 0) goto L_0x0096
        L_0x0095:
            r0 = r2
        L_0x0096:
            r1 = r3
            goto L_0x000e
        L_0x0099:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.utils.SpatialRelationUtil.getNearestPointFromLine(java.util.List, com.baidu.mapapi.model.LatLng):com.baidu.mapapi.model.LatLng");
    }

    public static boolean isCircleContainsPoint(LatLng latLng, int i, LatLng latLng2) {
        return (latLng == null || i == 0 || latLng2 == null || DistanceUtil.getDistance(latLng, latLng2) > ((double) i)) ? false : true;
    }

    public static boolean isPolygonContainsPoint(List<LatLng> list, LatLng latLng) {
        if (list == null || list.size() == 0 || latLng == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (latLng.longitude == list.get(i).longitude && latLng.latitude == list.get(i).latitude) {
                return true;
            }
        }
        int size = list.size();
        int i2 = 0;
        int i3 = 0;
        while (i2 < size) {
            LatLng latLng2 = list.get(i2);
            i2++;
            LatLng latLng3 = list.get(i2 % size);
            if (latLng2.latitude != latLng3.latitude && latLng.latitude >= Math.min(latLng2.latitude, latLng3.latitude) && latLng.latitude < Math.max(latLng2.latitude, latLng3.latitude)) {
                double d = (((latLng.latitude - latLng2.latitude) * (latLng3.longitude - latLng2.longitude)) / (latLng3.latitude - latLng2.latitude)) + latLng2.longitude;
                if (d == latLng.longitude) {
                    return true;
                }
                if (d < latLng.longitude) {
                    i3++;
                }
            }
        }
        return i3 % 2 == 1;
    }
}
