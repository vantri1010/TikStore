package com.baidu.platform.comapi.pano;

import com.baidu.mapapi.http.HttpClient;
import com.baidu.platform.comapi.pano.a;

class b extends HttpClient.ProtoResultCallback {
    final /* synthetic */ a.C0022a a;
    final /* synthetic */ a b;

    b(a aVar, a.C0022a aVar2) {
        this.b = aVar;
        this.a = aVar2;
    }

    public void onFailed(HttpClient.HttpStateError httpStateError) {
        this.a.a(httpStateError);
    }

    public void onSuccess(String str) {
        this.a.a(this.b.a(str));
    }
}
