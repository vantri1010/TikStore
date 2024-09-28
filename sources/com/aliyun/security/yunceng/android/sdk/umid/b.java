package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.os.Process;
import android.telephony.TelephonyManager;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.List;

public class b {
    private Context a = null;

    public b(Context content) {
        this.a = content;
    }

    private String b() {
        try {
            if (this.a.checkPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid()) == -1) {
                return "";
            }
            List<?> cells = (List) TelephonyManager.class.getMethod("getAllCellInfo", new Class[0]).invoke((TelephonyManager) this.a.getSystemService("phone"), new Object[0]);
            if (cells != null) {
                return cells.toString();
            }
            return "";
        } catch (Exception e) {
        }
    }

    public String a() {
        return b();
    }
}
