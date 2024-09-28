package com.baidu.mapsdkplatform.comapi.map;

import android.content.Context;
import android.util.Log;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapsdkplatform.comapi.map.h;

class i extends HttpClient.ProtoResultCallback {
    final /* synthetic */ Context a;
    final /* synthetic */ String b;
    final /* synthetic */ h.a c;
    final /* synthetic */ h d;

    i(h hVar, Context context, String str, h.a aVar) {
        this.d = hVar;
        this.a = context;
        this.b = str;
        this.c = aVar;
    }

    public void onFailed(HttpClient.HttpStateError httpStateError) {
        String a2 = this.d.b(this.a, this.b);
        if (!this.d.a(a2)) {
            a2 = null;
        }
        h.a aVar = this.c;
        if (aVar != null) {
            aVar.a(httpStateError.ordinal(), httpStateError.name(), a2);
        }
        String b2 = h.a;
        Log.e(b2, "sendRequest onFailed error = " + httpStateError);
    }

    public void onSuccess(String str) {
        this.d.b(this.a, str, this.b, this.c);
    }
}
