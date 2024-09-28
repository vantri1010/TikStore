package okhttp3;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.Cookie;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;

public final class JavaNetCookieJar implements CookieJar {
    private final CookieHandler cookieHandler;

    public JavaNetCookieJar(CookieHandler cookieHandler2) {
        this.cookieHandler = cookieHandler2;
    }

    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (this.cookieHandler != null) {
            List<String> cookieStrings = new ArrayList<>();
            for (Cookie cookie : cookies) {
                cookieStrings.add(cookie.toString());
            }
            try {
                this.cookieHandler.put(url.uri(), Collections.singletonMap("Set-Cookie", cookieStrings));
            } catch (IOException e) {
                Logger logger = Internal.logger;
                Level level = Level.WARNING;
                logger.log(level, "Saving cookies failed for " + url.resolve("/..."), e);
            }
        }
    }

    public List<Cookie> loadForRequest(HttpUrl url) {
        try {
            List<Cookie> cookies = null;
            for (Map.Entry<String, List<String>> entry : this.cookieHandler.get(url.uri(), Collections.emptyMap()).entrySet()) {
                String key = entry.getKey();
                if (("Cookie".equalsIgnoreCase(key) || "Cookie2".equalsIgnoreCase(key)) && !entry.getValue().isEmpty()) {
                    for (String header : entry.getValue()) {
                        if (cookies == null) {
                            cookies = new ArrayList<>();
                        }
                        cookies.addAll(decodeHeaderAsJavaNetCookies(url, header));
                    }
                }
            }
            if (cookies != null) {
                return Collections.unmodifiableList(cookies);
            }
            return Collections.emptyList();
        } catch (IOException e) {
            Logger logger = Internal.logger;
            Level level = Level.WARNING;
            logger.log(level, "Loading cookies failed for " + url.resolve("/..."), e);
            return Collections.emptyList();
        }
    }

    private List<Cookie> decodeHeaderAsJavaNetCookies(HttpUrl url, String header) {
        List<Cookie> result = new ArrayList<>();
        int pos = 0;
        int limit = header.length();
        while (pos < limit) {
            int pairEnd = Util.delimiterOffset(header, pos, limit, ";,");
            int equalsSign = Util.delimiterOffset(header, pos, pairEnd, '=');
            String name = Util.trimSubstring(header, pos, equalsSign);
            if (!name.startsWith("$")) {
                String value = equalsSign < pairEnd ? Util.trimSubstring(header, equalsSign + 1, pairEnd) : "";
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                result.add(new Cookie.Builder().name(name).value(value).domain(url.host()).build());
            }
            pos = pairEnd + 1;
        }
        return result;
    }
}
