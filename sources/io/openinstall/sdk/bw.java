package io.openinstall.sdk;

import android.net.Uri;
import io.openinstall.sdk.bf;
import java.util.HashMap;

public class bw extends bc {
    private final Uri a;

    public bw(h hVar, Uri uri) {
        super(hVar);
        this.a = uri;
    }

    /* renamed from: n */
    public bf call() {
        if (!j().b()) {
            if (cb.a) {
                cb.a("wakeupStatsEnabled is disable", new Object[0]);
            }
            return bf.a.REQUEST_ERROR.a("wakeupStatsEnabled is disable");
        }
        HashMap hashMap = new HashMap();
        Uri uri = this.a;
        if (uri != null) {
            hashMap.put("ul", uri.toString());
        }
        bb c = l().c(hashMap);
        a(c);
        return bf.a(c);
    }
}
