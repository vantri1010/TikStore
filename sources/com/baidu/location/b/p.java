package com.baidu.location.b;

import com.baidu.location.e.j;
import com.baidu.location.f;

class p implements Runnable {
    final /* synthetic */ o a;

    p(o oVar) {
        this.a = oVar;
    }

    public void run() {
        if (j.j() || this.a.a(f.getServiceContext())) {
            this.a.a(v.a().c());
        }
    }
}
