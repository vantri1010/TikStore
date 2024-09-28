package com.baidu.location;

import android.text.TextUtils;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;

public final class LocationClientOption {
    public static final int GpsFirst = 1;
    public static final int GpsOnly = 3;
    public static final int LOC_SENSITIVITY_HIGHT = 1;
    public static final int LOC_SENSITIVITY_LOW = 3;
    public static final int LOC_SENSITIVITY_MIDDLE = 2;
    public static final int MIN_AUTO_NOTIFY_INTERVAL = 10000;
    public static final int MIN_SCAN_SPAN = 1000;
    public static final int NetWorkFirst = 2;
    protected LocationMode a;
    public String addrType = "detail";
    public float autoNotifyLocSensitivity = 0.5f;
    public int autoNotifyMaxInterval = 0;
    public int autoNotifyMinDistance = 0;
    public int autoNotifyMinTimeInterval = 0;
    public String coorType = CoordinateType.GCJ02;
    public boolean disableLocCache = true;
    public boolean enableSimulateGps = false;
    public boolean isIgnoreCacheException = true;
    public boolean isIgnoreKillProcess = true;
    public boolean isNeedAltitude = false;
    public boolean isNeedAptag = false;
    public boolean isNeedAptagd = false;
    public boolean isNeedNewVersionRgc = false;
    public boolean isNeedPoiRegion = false;
    public boolean isNeedRegular = false;
    public boolean isOnceLocation = false;
    public boolean location_change_notify = false;
    public boolean mIsNeedDeviceDirect = false;
    public boolean openGps = false;
    public int priority = 1;
    public String prodName = "SDK6.0";
    public int scanSpan = 0;
    public String serviceName = "com.baidu.location.service_v2.9";
    public int timeOut = 12000;
    public int wifiCacheTimeOut = Integer.MAX_VALUE;

    /* renamed from: com.baidu.location.LocationClientOption$1  reason: invalid class name */
    /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[LocationMode.values().length];
            a = iArr;
            try {
                iArr[LocationMode.Hight_Accuracy.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                a[LocationMode.Battery_Saving.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                a[LocationMode.Device_Sensors.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum BDLocationPurpose {
        SignIn,
        Sport,
        Transport
    }

    public enum LocationMode {
        Hight_Accuracy,
        Battery_Saving,
        Device_Sensors
    }

    public LocationClientOption() {
    }

    public LocationClientOption(LocationClientOption locationClientOption) {
        this.coorType = locationClientOption.coorType;
        this.addrType = locationClientOption.addrType;
        this.openGps = locationClientOption.openGps;
        this.scanSpan = locationClientOption.scanSpan;
        this.timeOut = locationClientOption.timeOut;
        this.prodName = locationClientOption.prodName;
        this.priority = locationClientOption.priority;
        this.location_change_notify = locationClientOption.location_change_notify;
        this.serviceName = locationClientOption.serviceName;
        this.disableLocCache = locationClientOption.disableLocCache;
        this.isIgnoreCacheException = locationClientOption.isIgnoreCacheException;
        this.isIgnoreKillProcess = locationClientOption.isIgnoreKillProcess;
        this.enableSimulateGps = locationClientOption.enableSimulateGps;
        this.a = locationClientOption.a;
        this.isNeedAptag = locationClientOption.isNeedAptag;
        this.isNeedAptagd = locationClientOption.isNeedAptagd;
        this.isNeedPoiRegion = locationClientOption.isNeedPoiRegion;
        this.isNeedRegular = locationClientOption.isNeedRegular;
        this.mIsNeedDeviceDirect = locationClientOption.mIsNeedDeviceDirect;
        this.isNeedAltitude = locationClientOption.isNeedAltitude;
        this.autoNotifyMaxInterval = locationClientOption.autoNotifyMaxInterval;
        this.autoNotifyLocSensitivity = locationClientOption.autoNotifyLocSensitivity;
        this.autoNotifyMinTimeInterval = locationClientOption.autoNotifyMinTimeInterval;
        this.autoNotifyMinDistance = locationClientOption.autoNotifyMinDistance;
        this.wifiCacheTimeOut = locationClientOption.wifiCacheTimeOut;
        this.isNeedNewVersionRgc = locationClientOption.isNeedNewVersionRgc;
        this.isOnceLocation = locationClientOption.isOnceLocation;
    }

    public void SetIgnoreCacheException(boolean z) {
        this.isIgnoreCacheException = z;
    }

    /* access modifiers changed from: package-private */
    public int a() {
        return this.autoNotifyMaxInterval;
    }

    /* access modifiers changed from: package-private */
    public float b() {
        return this.autoNotifyLocSensitivity;
    }

    public void disableCache(boolean z) {
        this.disableLocCache = z;
    }

    public String getAddrType() {
        return this.addrType;
    }

    public int getAutoNotifyMinDistance() {
        return this.autoNotifyMinDistance;
    }

    public int getAutoNotifyMinTimeInterval() {
        return this.autoNotifyMinTimeInterval;
    }

    public String getCoorType() {
        return this.coorType;
    }

    public LocationMode getLocationMode() {
        return this.a;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getProdName() {
        return this.prodName;
    }

    public int getScanSpan() {
        return this.scanSpan;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public int getTimeOut() {
        return this.timeOut;
    }

    public boolean isDisableCache() {
        return this.disableLocCache;
    }

    public boolean isLocationNotify() {
        return this.location_change_notify;
    }

    public boolean isOnceLocation() {
        return this.isOnceLocation;
    }

    public boolean isOpenGps() {
        return this.openGps;
    }

    public boolean optionEquals(LocationClientOption locationClientOption) {
        return this.coorType.equals(locationClientOption.coorType) && this.addrType.equals(locationClientOption.addrType) && this.openGps == locationClientOption.openGps && this.scanSpan == locationClientOption.scanSpan && this.timeOut == locationClientOption.timeOut && this.prodName.equals(locationClientOption.prodName) && this.location_change_notify == locationClientOption.location_change_notify && this.priority == locationClientOption.priority && this.disableLocCache == locationClientOption.disableLocCache && this.isIgnoreCacheException == locationClientOption.isIgnoreCacheException && this.isNeedNewVersionRgc == locationClientOption.isNeedNewVersionRgc && this.isIgnoreKillProcess == locationClientOption.isIgnoreKillProcess && this.isNeedAptag == locationClientOption.isNeedAptag && this.isNeedAptagd == locationClientOption.isNeedAptagd && this.isNeedPoiRegion == locationClientOption.isNeedPoiRegion && this.isNeedRegular == locationClientOption.isNeedRegular && this.mIsNeedDeviceDirect == locationClientOption.mIsNeedDeviceDirect && this.autoNotifyMaxInterval == locationClientOption.autoNotifyMaxInterval && this.autoNotifyLocSensitivity == locationClientOption.autoNotifyLocSensitivity && this.autoNotifyMinTimeInterval == locationClientOption.autoNotifyMinTimeInterval && this.autoNotifyMinDistance == locationClientOption.autoNotifyMinDistance && this.wifiCacheTimeOut == locationClientOption.wifiCacheTimeOut && this.isOnceLocation == locationClientOption.isOnceLocation && this.isNeedAltitude == locationClientOption.isNeedAltitude && this.a == locationClientOption.a;
    }

    @Deprecated
    public void setAddrType(String str) {
        if (!TextUtils.isEmpty(str)) {
            setIsNeedAddress("all".equals(str));
        }
    }

    public void setCoorType(String str) {
        String lowerCase = str.toLowerCase();
        if (lowerCase.equals(CoordinateType.GCJ02) || lowerCase.equals(BDLocation.BDLOCATION_GCJ02_TO_BD09) || lowerCase.equals("bd09ll")) {
            this.coorType = lowerCase;
        }
    }

    public void setEnableSimulateGps(boolean z) {
        this.enableSimulateGps = z;
    }

    public void setIgnoreKillProcess(boolean z) {
        this.isIgnoreKillProcess = z;
    }

    public void setIsNeedAddress(boolean z) {
        this.addrType = z ? "all" : "noaddr";
    }

    public void setIsNeedAltitude(boolean z) {
        this.isNeedAltitude = z;
    }

    public void setIsNeedLocationDescribe(boolean z) {
        this.isNeedAptag = z;
    }

    public void setIsNeedLocationPoiList(boolean z) {
        this.isNeedAptagd = z;
    }

    public void setLocationMode(LocationMode locationMode) {
        int i = AnonymousClass1.a[locationMode.ordinal()];
        if (i == 1) {
            this.openGps = true;
            this.priority = 1;
        } else if (i == 2) {
            this.openGps = false;
            this.priority = 2;
        } else if (i == 3) {
            this.priority = 3;
            this.openGps = true;
        } else {
            throw new IllegalArgumentException("Illegal this mode : " + locationMode);
        }
        this.a = locationMode;
    }

    public void setLocationNotify(boolean z) {
        this.location_change_notify = z;
    }

    public void setLocationPurpose(BDLocationPurpose bDLocationPurpose) {
        if (bDLocationPurpose == null) {
            return;
        }
        if (bDLocationPurpose == BDLocationPurpose.SignIn) {
            setLocationMode(LocationMode.Hight_Accuracy);
            setLocationNotify(false);
            setScanSpan(0);
            setNeedNewVersionRgc(true);
            setIsNeedAddress(true);
            setIsNeedLocationPoiList(true);
            setIsNeedAltitude(true);
            setIsNeedLocationDescribe(true);
            setWifiCacheTimeOut(10000);
            return;
        }
        if (bDLocationPurpose == BDLocationPurpose.Sport) {
            setLocationMode(LocationMode.Hight_Accuracy);
            setLocationNotify(false);
            setScanSpan(3000);
        } else if (bDLocationPurpose == BDLocationPurpose.Transport) {
            setLocationMode(LocationMode.Hight_Accuracy);
            setLocationNotify(true);
            setScanSpan(1000);
        } else {
            return;
        }
        setNeedNewVersionRgc(true);
        setIsNeedAddress(true);
        setIsNeedLocationPoiList(false);
        setIsNeedAltitude(true);
        setIsNeedLocationDescribe(false);
        setWifiCacheTimeOut(1000);
    }

    public void setNeedDeviceDirect(boolean z) {
        this.mIsNeedDeviceDirect = z;
    }

    public void setNeedNewVersionRgc(boolean z) {
        this.isNeedNewVersionRgc = z;
    }

    public void setOnceLocation(boolean z) {
        this.isOnceLocation = z;
    }

    public void setOpenAutoNotifyMode() {
        setOpenAutoNotifyMode(0, 0, 1);
    }

    public void setOpenAutoNotifyMode(int i, int i2, int i3) {
        float f;
        int i4 = 180000;
        if (i > 180000) {
            i4 = i + 1000;
        }
        if (i4 >= 10000) {
            if (i3 == 1) {
                f = 0.5f;
            } else if (i3 == 2) {
                f = 0.3f;
            } else if (i3 == 3) {
                f = 0.1f;
            } else {
                throw new IllegalArgumentException("Illegal this locSensitivity : " + i3);
            }
            this.autoNotifyLocSensitivity = f;
            this.autoNotifyMaxInterval = i4;
            this.autoNotifyMinTimeInterval = i;
            this.autoNotifyMinDistance = i2;
            return;
        }
        throw new IllegalArgumentException("Illegal this maxLocInterval : " + i4 + " , maxLocInterval must >= " + 10000);
    }

    public void setOpenGps(boolean z) {
        this.openGps = z;
    }

    @Deprecated
    public void setPriority(int i) {
        if (i == 1 || i == 2) {
            this.priority = i;
        }
    }

    public void setProdName(String str) {
        if (str.length() > 64) {
            str = str.substring(0, 64);
        }
        this.prodName = str;
    }

    public void setScanSpan(int i) {
        if (i >= 0) {
            this.scanSpan = i;
        }
    }

    @Deprecated
    public void setSema(boolean z, boolean z2, boolean z3) {
        this.isNeedAptag = z;
        this.isNeedPoiRegion = z2;
        this.isNeedRegular = z3;
    }

    public void setServiceName(String str) {
        this.serviceName = str;
    }

    public void setTimeOut(int i) {
        this.timeOut = i;
    }

    public void setWifiCacheTimeOut(int i) {
        if (i >= 10000) {
            this.wifiCacheTimeOut = i;
        }
    }
}
