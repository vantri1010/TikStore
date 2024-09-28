package com.aliyun.security.yunceng.android.sdk.umid;

import android.os.Debug;
import com.aliyun.security.yunceng.android.sdk.YunCeng;

public class CheckDebug {
    private native int detect_debug_raw();

    public boolean a() {
        boolean ret = false;
        if (Debug.isDebuggerConnected()) {
            YunCeng.reportInfo(22, "debug_os", "debug_os", 1);
            ret = true;
        }
        detect_debug_raw();
        return ret;
    }

    static {
        System.loadLibrary("yunceng");
    }
}
