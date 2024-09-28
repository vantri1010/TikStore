package com.baidu.lbsapi.auth;

import java.util.HashMap;

class d implements Runnable {
    final /* synthetic */ c a;

    d(c cVar) {
        this.a = cVar;
    }

    public void run() {
        a.a("postWithHttps start Thread id = " + String.valueOf(Thread.currentThread().getId()));
        this.a.a(new g(this.a.a).a((HashMap<String, String>) this.a.b));
    }
}
