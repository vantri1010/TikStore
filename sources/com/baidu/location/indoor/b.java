package com.baidu.location.indoor;

import com.baidu.location.b.v;

class b implements Runnable {
    final /* synthetic */ a a;

    b(a aVar) {
        this.a = aVar;
    }

    public void run() {
        if (this.a.s != null) {
            a aVar = this.a;
            String unused = aVar.f = aVar.s;
            this.a.b(v.a().c());
        }
    }
}
