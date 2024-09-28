package com.baidu.location.b;

import com.baidu.location.g.k;
import java.io.File;

class f extends Thread {
    final /* synthetic */ d a;

    f(d dVar) {
        this.a = dVar;
    }

    public void run() {
        String unused = this.a.a(new File(k.k() + "/baidu/tempdata", "intime.dat"), "https://itsdata.map.baidu.com/long-conn-gps/sdk.php");
    }
}
