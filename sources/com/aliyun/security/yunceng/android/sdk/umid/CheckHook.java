package com.aliyun.security.yunceng.android.sdk.umid;

public class CheckHook {
    private native boolean CheckHookByNative();

    public boolean a() {
        return b() || CheckHookByNative();
    }

    private boolean b() {
        try {
            throw new Exception("");
        } catch (Exception e) {
            for (StackTraceElement frame : e.getStackTrace()) {
                if (frame.getClassName().contains("Xposed")) {
                    return true;
                }
            }
            return false;
        }
    }

    static {
        System.loadLibrary("yunceng");
    }
}
