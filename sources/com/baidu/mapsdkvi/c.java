package com.baidu.mapsdkvi;

import android.net.NetworkInfo;

public class c {
    public String a;
    public int b;
    public int c;

    public c(NetworkInfo networkInfo) {
        this.a = networkInfo.getTypeName();
        this.b = networkInfo.getType();
        int i = d.a[networkInfo.getState().ordinal()];
        if (i == 1) {
            this.c = 2;
        } else if (i != 2) {
            this.c = 0;
        } else {
            this.c = 1;
        }
    }
}
