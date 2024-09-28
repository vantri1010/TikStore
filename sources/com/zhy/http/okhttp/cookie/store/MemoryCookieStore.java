package com.zhy.http.okhttp.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = this.allCookies.get(url.host());
        if (oldCookies != null) {
            Iterator<Cookie> itOld = oldCookies.iterator();
            for (Cookie name : cookies) {
                String va = name.name();
                while (va != null && itOld.hasNext()) {
                    String v = itOld.next().name();
                    if (v != null && va.equals(v)) {
                        itOld.remove();
                    }
                }
            }
            oldCookies.addAll(cookies);
            return;
        }
        this.allCookies.put(url.host(), cookies);
    }

    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = this.allCookies.get(uri.host());
        if (cookies != null) {
            return cookies;
        }
        List<Cookie> cookies2 = new ArrayList<>();
        this.allCookies.put(uri.host(), cookies2);
        return cookies2;
    }

    public boolean removeAll() {
        this.allCookies.clear();
        return true;
    }

    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        for (String url : this.allCookies.keySet()) {
            cookies.addAll(this.allCookies.get(url));
        }
        return cookies;
    }

    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = this.allCookies.get(uri.host());
        if (cookie != null) {
            return cookies.remove(cookie);
        }
        return false;
    }
}
