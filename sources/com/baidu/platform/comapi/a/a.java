package com.baidu.platform.comapi.a;

import android.content.Context;
import im.bclpbkiauv.ui.utils.NaviUtils;

public class a {
    private static int a = 621133959;

    public static boolean a(Context context) {
        return c(context);
    }

    private static int b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(NaviUtils.PN_BAIDU_MAP, 64).signatures[0].hashCode();
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean c(Context context) {
        return b(context) == a;
    }
}
