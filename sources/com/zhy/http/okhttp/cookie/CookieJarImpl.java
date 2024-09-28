package com.zhy.http.okhttp.cookie;

import com.zhy.http.okhttp.cookie.store.CookieStore;
import com.zhy.http.okhttp.utils.Exceptions;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore2) {
        if (cookieStore2 == null) {
            Exceptions.illegalArgument("cookieStore can not be null.", new Object[0]);
        }
        this.cookieStore = cookieStore2;
    }

    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookieStore.add(url, cookies);
    }

    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return this.cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }
}
