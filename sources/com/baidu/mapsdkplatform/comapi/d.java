package com.baidu.mapsdkplatform.comapi;

import java.io.File;
import java.io.FilenameFilter;

class d implements FilenameFilter {
    final /* synthetic */ String a;
    final /* synthetic */ NativeLoader b;

    d(NativeLoader nativeLoader, String str) {
        this.b = nativeLoader;
        this.a = str;
    }

    public boolean accept(File file, String str) {
        return str != null && str.contains("libBaiduMapSDK_") && !str.contains(this.a);
    }
}
