package com.baidu.platform.core.e;

import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapapi.search.share.PoiDetailShareURLOption;
import com.baidu.platform.base.e;
import com.google.android.gms.common.internal.ImagesContract;

public class c extends e {
    public c(PoiDetailShareURLOption poiDetailShareURLOption) {
        a(poiDetailShareURLOption);
    }

    private void a(PoiDetailShareURLOption poiDetailShareURLOption) {
        this.a.a(ImagesContract.URL, ("http://wapmap.baidu.com/s?tn=Detail&pid=" + poiDetailShareURLOption.mUid + "&smsf=3") + HttpClient.getPhoneInfo());
        b(false);
        a(false);
    }

    public String a(com.baidu.platform.domain.c cVar) {
        return cVar.p();
    }
}
