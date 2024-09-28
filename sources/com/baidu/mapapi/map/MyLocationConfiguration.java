package com.baidu.mapapi.map;

import android.graphics.Color;

public class MyLocationConfiguration {
    public int accuracyCircleFillColor = 4521984;
    public int accuracyCircleStrokeColor = 4653056;
    public final BitmapDescriptor customMarker;
    public final boolean enableDirection;
    public final LocationMode locationMode;

    public enum LocationMode {
        NORMAL,
        FOLLOWING,
        COMPASS
    }

    public MyLocationConfiguration(LocationMode locationMode2, boolean z, BitmapDescriptor bitmapDescriptor) {
        this.locationMode = locationMode2 == null ? LocationMode.NORMAL : locationMode2;
        this.enableDirection = z;
        this.customMarker = bitmapDescriptor;
        this.accuracyCircleFillColor = a(this.accuracyCircleFillColor);
        this.accuracyCircleStrokeColor = a(this.accuracyCircleStrokeColor);
    }

    public MyLocationConfiguration(LocationMode locationMode2, boolean z, BitmapDescriptor bitmapDescriptor, int i, int i2) {
        this.locationMode = locationMode2 == null ? LocationMode.NORMAL : locationMode2;
        this.enableDirection = z;
        this.customMarker = bitmapDescriptor;
        this.accuracyCircleFillColor = a(i);
        this.accuracyCircleStrokeColor = a(i2);
    }

    private int a(int i) {
        int i2 = (65280 & i) >> 8;
        return Color.argb((-16777216 & i) >> 24, i & 255, i2, (16711680 & i) >> 16);
    }
}
