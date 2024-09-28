package com.baidu.mapapi.utils;

import com.baidu.mapapi.model.LatLng;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.List;

public class AreaUtil {
    public static double calculateArea(LatLng latLng, LatLng latLng2) {
        if (!(latLng == null || latLng2 == null)) {
            LatLng latLng3 = new LatLng(latLng.latitude, latLng2.longitude);
            double distance = DistanceUtil.getDistance(latLng3, latLng2);
            double distance2 = DistanceUtil.getDistance(latLng, latLng3);
            if (!(distance == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || distance2 == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)) {
                return distance * distance2;
            }
        }
        return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public static double calculateArea(List<LatLng> list) {
        List<LatLng> list2 = list;
        double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        if (list2 == null || list.size() < 3) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        double d2 = 111319.49079327358d;
        int size = list.size();
        int i = 0;
        while (i < size) {
            LatLng latLng = list2.get(i);
            i++;
            LatLng latLng2 = list2.get(i % size);
            d += (((latLng.longitude * d2) * Math.cos(latLng.latitude * 0.017453292519943295d)) * (latLng2.latitude * 111319.49079327358d)) - ((latLng.latitude * d2) * ((latLng2.longitude * d2) * Math.cos(latLng2.latitude * 0.017453292519943295d)));
            d2 = 111319.49079327358d;
        }
        return (double) ((float) Math.abs(d / 2.0d));
    }
}
