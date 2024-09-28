package com.baidu.platform.base;

import android.text.TextUtils;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.platform.core.b.e;

class b extends HttpClient.ProtoResultCallback {
    final /* synthetic */ d a;
    final /* synthetic */ Object b;
    final /* synthetic */ a c;

    b(a aVar, d dVar, Object obj) {
        this.c = aVar;
        this.a = dVar;
        this.b = obj;
    }

    public void onFailed(HttpClient.HttpStateError httpStateError) {
        this.c.a(httpStateError, this.a, this.b);
    }

    public void onSuccess(String str) {
        String a2 = this.a instanceof e ? this.c.a(str) : "";
        String str2 = !TextUtils.isEmpty(a2) ? a2 : str;
        this.c.c(str2);
        a aVar = this.c;
        aVar.a(str2, this.a, this.b, aVar.b, this);
    }
}
