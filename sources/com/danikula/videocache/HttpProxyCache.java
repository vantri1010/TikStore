package com.danikula.videocache;

import android.text.TextUtils;
import com.danikula.videocache.file.FileCache;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Locale;

class HttpProxyCache extends ProxyCache {
    private static final float NO_CACHE_BARRIER = 0.2f;
    private final FileCache cache;
    private CacheListener listener;
    private final HttpUrlSource source;

    public HttpProxyCache(HttpUrlSource source2, FileCache cache2) {
        super(source2, cache2);
        this.cache = cache2;
        this.source = source2;
    }

    public void registerCacheListener(CacheListener cacheListener) {
        this.listener = cacheListener;
    }

    public void processRequest(GetRequest request, Socket socket) throws IOException, ProxyCacheException {
        OutputStream out = new BufferedOutputStream(socket.getOutputStream());
        out.write(newResponseHeaders(request).getBytes("UTF-8"));
        long offset = request.rangeOffset;
        if (isUseCache(request)) {
            responseWithCache(out, offset);
        } else {
            responseWithoutCache(out, offset);
        }
    }

    private boolean isUseCache(GetRequest request) throws ProxyCacheException {
        long sourceLength = this.source.length();
        boolean sourceLengthKnown = sourceLength > 0;
        long cacheAvailable = this.cache.available();
        if (!sourceLengthKnown || !request.partial || ((float) request.rangeOffset) <= ((float) cacheAvailable) + (((float) sourceLength) * NO_CACHE_BARRIER)) {
            return true;
        }
        return false;
    }

    private String newResponseHeaders(GetRequest request) throws IOException, ProxyCacheException {
        String str;
        GetRequest getRequest = request;
        String mime = this.source.getMime();
        boolean mimeKnown = !TextUtils.isEmpty(mime);
        long length = this.cache.isCompleted() ? this.cache.available() : this.source.length();
        boolean lengthKnown = length >= 0;
        long contentLength = getRequest.partial ? length - getRequest.rangeOffset : length;
        boolean addRange = lengthKnown && getRequest.partial;
        StringBuilder sb = new StringBuilder();
        sb.append(getRequest.partial ? "HTTP/1.1 206 PARTIAL CONTENT\n" : "HTTP/1.1 200 OK\n");
        sb.append("Accept-Ranges: bytes\n");
        String str2 = "";
        sb.append(lengthKnown ? format("Content-Length: %d\n", Long.valueOf(contentLength)) : str2);
        if (addRange) {
            long length2 = length;
            str = format("Content-Range: bytes %d-%d/%d\n", Long.valueOf(getRequest.rangeOffset), Long.valueOf(length2 - 1), Long.valueOf(length2));
        } else {
            str = str2;
        }
        sb.append(str);
        if (mimeKnown) {
            str2 = format("Content-Type: %s\n", mime);
        }
        sb.append(str2);
        sb.append("\n");
        return sb.toString();
    }

    private void responseWithCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        byte[] buffer = new byte[8192];
        while (true) {
            int read = read(buffer, offset, buffer.length);
            int readBytes = read;
            if (read != -1) {
                out.write(buffer, 0, readBytes);
                offset += (long) readBytes;
            } else {
                out.flush();
                return;
            }
        }
    }

    private void responseWithoutCache(OutputStream out, long offset) throws ProxyCacheException, IOException {
        HttpUrlSource newSourceNoCache = new HttpUrlSource(this.source);
        try {
            newSourceNoCache.open((long) ((int) offset));
            byte[] buffer = new byte[8192];
            while (true) {
                int read = newSourceNoCache.read(buffer);
                int readBytes = read;
                if (read != -1) {
                    out.write(buffer, 0, readBytes);
                    offset += (long) readBytes;
                } else {
                    out.flush();
                    return;
                }
            }
        } finally {
            newSourceNoCache.close();
        }
    }

    private String format(String pattern, Object... args) {
        return String.format(Locale.US, pattern, args);
    }

    /* access modifiers changed from: protected */
    public void onCachePercentsAvailableChanged(int percents) {
        CacheListener cacheListener = this.listener;
        if (cacheListener != null) {
            cacheListener.onCacheAvailable(this.cache.file, this.source.getUrl(), percents);
        }
    }
}
