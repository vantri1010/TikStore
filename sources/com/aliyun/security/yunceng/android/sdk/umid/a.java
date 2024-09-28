package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import com.king.zxing.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

public class a {
    private Context a = null;

    public a(Context context) {
        this.a = context;
    }

    private List<String> b() {
        List<String> apps = new ArrayList<>();
        try {
            for (PackageInfo pack : this.a.getPackageManager().getInstalledPackages(0)) {
                ApplicationInfo app = pack.applicationInfo;
                if ((app.flags & 1) == 0) {
                    apps.add(app.loadLabel(this.a.getPackageManager()).toString());
                }
            }
            return apps;
        } catch (Exception e) {
            return apps;
        }
    }

    private String a(List<String> list, String conjunction) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    public String a() {
        return a(b(), LogUtils.VERTICAL);
    }
}
