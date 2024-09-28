package com.baidu.mapapi;

import com.baidu.mapsdkplatform.comapi.util.c;

public class PermissionUtils {

    private static class a {
        /* access modifiers changed from: private */
        public static final PermissionUtils a = new PermissionUtils();
    }

    private PermissionUtils() {
    }

    public static PermissionUtils getInstance() {
        return a.a;
    }

    public boolean isIndoorNaviAuthorized() {
        return c.a().b();
    }
}
