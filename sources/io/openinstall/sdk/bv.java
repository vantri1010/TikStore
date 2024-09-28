package io.openinstall.sdk;

import com.fm.openinstall.listener.ResultCallback;
import io.openinstall.sdk.bf;
import java.util.HashMap;

public class bv extends bc {
    private final o a;
    private final ResultCallback<Void> b;

    public bv(h hVar, o oVar, ResultCallback<Void> resultCallback) {
        super(hVar);
        this.a = oVar;
        this.b = resultCallback;
    }

    /* access modifiers changed from: protected */
    public void a(bf bfVar) {
        super.a(bfVar);
        ResultCallback<Void> resultCallback = this.b;
        if (resultCallback != null) {
            resultCallback.onResult(null, bfVar.c());
        }
    }

    /* renamed from: n */
    public bf call() throws Exception {
        if (g().b()) {
            HashMap hashMap = new HashMap();
            hashMap.put("sU", this.a.a());
            hashMap.put("sP", this.a.b());
            bb d = l().d(hashMap);
            if (!(d instanceof ay)) {
                d = l().d(hashMap);
            }
            a(d);
            return bf.a(d);
        } else if (g().d() != e.e) {
            return bf.a.REQUEST_TIMEOUT.a();
        } else {
            return bf.a.INIT_ERROR.a(h().a("FM_init_msg"));
        }
    }
}
