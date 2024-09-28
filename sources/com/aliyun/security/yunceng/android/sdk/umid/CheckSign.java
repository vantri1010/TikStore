package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;

public class CheckSign {
    private native boolean CheckByNative(Context context, String str);

    public boolean a(Context ct) {
        try {
            return CheckByNative(ct, ct.getPackageManager().getPackageInfo(ct.getPackageName(), 64).signatures[0].toCharsString());
        } catch (Exception e) {
            return true;
        }
    }

    static {
        System.loadLibrary("yunceng");
    }
}
