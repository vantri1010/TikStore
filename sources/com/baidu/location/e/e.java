package com.baidu.location.e;

import com.aliyun.security.yunceng.android.sdk.traceroute.d;

public final class e {
    public static String a(int i) {
        if (j.j()) {
            return d.c;
        }
        switch (i) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return "3G";
            case 13:
                return "4G";
            default:
                return "unknown";
        }
    }
}
