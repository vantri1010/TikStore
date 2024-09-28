package com.aliyun.security.yunceng.android.sdk.umid;

import com.king.zxing.util.LogUtils;
import java.io.File;

public class c {
    public boolean a() {
        return b() || c();
    }

    private boolean b() {
        for (String path : new String[]{"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"}) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    private boolean c() {
        for (String pathDir : System.getenv("PATH").split(LogUtils.COLON)) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }
}
