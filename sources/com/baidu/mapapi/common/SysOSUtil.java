package com.baidu.mapapi.common;

import android.text.TextUtils;
import com.baidu.mapsdkplatform.comapi.util.h;
import com.king.zxing.util.LogUtils;

public class SysOSUtil {
    public static float getDensity() {
        return h.c;
    }

    public static int getDensityDpi() {
        return h.l();
    }

    public static String getDeviceID() {
        String p = h.p();
        return TextUtils.isEmpty(p) ? p : p.substring(0, p.indexOf(LogUtils.VERTICAL));
    }

    public static String getModuleFileName() {
        return h.o();
    }

    public static String getPhoneType() {
        return h.g();
    }

    public static int getScreenSizeX() {
        return h.h();
    }

    public static int getScreenSizeY() {
        return h.j();
    }
}
