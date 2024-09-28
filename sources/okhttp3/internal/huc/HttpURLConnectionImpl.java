package okhttp3.internal.huc;

import com.king.zxing.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.JavaNetHeaders;
import okhttp3.internal.Platform;
import okhttp3.internal.URLFilter;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpDate;
import okhttp3.internal.http.HttpEngine;
import okhttp3.internal.http.HttpMethod;
import okhttp3.internal.http.OkHeaders;
import okhttp3.internal.http.RequestException;
import okhttp3.internal.http.RetryableSink;
import okhttp3.internal.http.RouteException;
import okhttp3.internal.http.StatusLine;
import okhttp3.internal.http.StreamAllocation;
import okio.BufferedSink;
import okio.Sink;

public class HttpURLConnectionImpl extends HttpURLConnection {
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create((MediaType) null, new byte[0]);
    private static final Set<String> METHODS = new LinkedHashSet(Arrays.asList(new String[]{"OPTIONS", "GET", OkHttpUtils.METHOD.HEAD, "POST", OkHttpUtils.METHOD.PUT, OkHttpUtils.METHOD.DELETE, "TRACE", OkHttpUtils.METHOD.PATCH}));
    OkHttpClient client;
    private long fixedContentLength;
    private int followUpCount;
    Handshake handshake;
    protected HttpEngine httpEngine;
    protected IOException httpEngineFailure;
    private Headers.Builder requestHeaders;
    private Headers responseHeaders;
    private Route route;
    private URLFilter urlFilter;

    public HttpURLConnectionImpl(URL url, OkHttpClient client2) {
        super(url);
        this.requestHeaders = new Headers.Builder();
        this.fixedContentLength = -1;
        this.client = client2;
    }

    public HttpURLConnectionImpl(URL url, OkHttpClient client2, URLFilter urlFilter2) {
        this(url, client2);
        this.urlFilter = urlFilter2;
    }

    public final void connect() throws IOException {
        initHttpEngine();
        do {
        } while (!execute(false));
    }

    public final void disconnect() {
        HttpEngine httpEngine2 = this.httpEngine;
        if (httpEngine2 != null) {
            httpEngine2.cancel();
        }
    }

    public final InputStream getErrorStream() {
        try {
            HttpEngine response = getResponse();
            if (!HttpEngine.hasBody(response.getResponse()) || response.getResponse().code() < 400) {
                return null;
            }
            return response.getResponse().body().byteStream();
        } catch (IOException e) {
            return null;
        }
    }

    private Headers getHeaders() throws IOException {
        if (this.responseHeaders == null) {
            Response response = getResponse().getResponse();
            this.responseHeaders = response.headers().newBuilder().add(OkHeaders.SELECTED_PROTOCOL, response.protocol().toString()).add(OkHeaders.RESPONSE_SOURCE, responseSourceHeader(response)).build();
        }
        return this.responseHeaders;
    }

    private static String responseSourceHeader(Response response) {
        if (response.networkResponse() == null) {
            if (response.cacheResponse() == null) {
                return "NONE";
            }
            return "CACHE " + response.code();
        } else if (response.cacheResponse() == null) {
            return "NETWORK " + response.code();
        } else {
            return "CONDITIONAL_CACHE " + response.networkResponse().code();
        }
    }

    public final String getHeaderField(int position) {
        try {
            Headers headers = getHeaders();
            if (position >= 0) {
                if (position < headers.size()) {
                    return headers.value(position);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public final String getHeaderField(String fieldName) {
        if (fieldName != null) {
            return getHeaders().get(fieldName);
        }
        try {
            return StatusLine.get(getResponse().getResponse()).toString();
        } catch (IOException e) {
            return null;
        }
    }

    public final String getHeaderFieldKey(int position) {
        try {
            Headers headers = getHeaders();
            if (position >= 0) {
                if (position < headers.size()) {
                    return headers.name(position);
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public final Map<String, List<String>> getHeaderFields() {
        try {
            return JavaNetHeaders.toMultimap(getHeaders(), StatusLine.get(getResponse().getResponse()).toString());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    public final Map<String, List<String>> getRequestProperties() {
        if (!this.connected) {
            return JavaNetHeaders.toMultimap(this.requestHeaders.build(), (String) null);
        }
        throw new IllegalStateException("Cannot access request header fields after connection is set");
    }

    public final InputStream getInputStream() throws IOException {
        if (this.doInput) {
            HttpEngine response = getResponse();
            if (getResponseCode() < 400) {
                return response.getResponse().body().byteStream();
            }
            throw new FileNotFoundException(this.url.toString());
        }
        throw new ProtocolException("This protocol does not support input");
    }

    public final OutputStream getOutputStream() throws IOException {
        connect();
        BufferedSink sink = this.httpEngine.getBufferedRequestBody();
        if (sink == null) {
            throw new ProtocolException("method does not support a request body: " + this.method);
        } else if (!this.httpEngine.hasResponse()) {
            return sink.outputStream();
        } else {
            throw new ProtocolException("cannot write request body after response has been read");
        }
    }

    public final Permission getPermission() throws IOException {
        int hostPort;
        URL url = getURL();
        String hostname = url.getHost();
        if (url.getPort() != -1) {
            hostPort = url.getPort();
        } else {
            hostPort = HttpUrl.defaultPort(url.getProtocol());
        }
        if (usingProxy()) {
            InetSocketAddress proxyAddress = (InetSocketAddress) this.client.proxy().address();
            hostname = proxyAddress.getHostName();
            hostPort = proxyAddress.getPort();
        }
        return new SocketPermission(hostname + LogUtils.COLON + hostPort, "connect, resolve");
    }

    public final String getRequestProperty(String field) {
        if (field == null) {
            return null;
        }
        return this.requestHeaders.get(field);
    }

    public void setConnectTimeout(int timeoutMillis) {
        this.client = this.client.newBuilder().connectTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS).build();
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.client = this.client.newBuilder().followRedirects(followRedirects).build();
    }

    public boolean getInstanceFollowRedirects() {
        return this.client.followRedirects();
    }

    public int getConnectTimeout() {
        return this.client.connectTimeoutMillis();
    }

    public void setReadTimeout(int timeoutMillis) {
        this.client = this.client.newBuilder().readTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS).build();
    }

    public int getReadTimeout() {
        return this.client.readTimeoutMillis();
    }

    private void initHttpEngine() throws IOException {
        IOException e = this.httpEngineFailure;
        if (e != null) {
            throw e;
        } else if (this.httpEngine == null) {
            this.connected = true;
            try {
                if (this.doOutput) {
                    if (this.method.equals("GET")) {
                        this.method = "POST";
                    } else if (!HttpMethod.permitsRequestBody(this.method)) {
                        throw new ProtocolException(this.method + " does not support writing");
                    }
                }
                this.httpEngine = newHttpEngine(this.method, (StreamAllocation) null, (RetryableSink) null, (Response) null);
            } catch (IOException e2) {
                this.httpEngineFailure = e2;
                throw e2;
            }
        }
    }

    private HttpEngine newHttpEngine(String method, StreamAllocation streamAllocation, RetryableSink requestBody, Response priorResponse) throws MalformedURLException, UnknownHostException {
        Request.Builder builder = new Request.Builder().url(Internal.instance.getHttpUrlChecked(getURL().toString())).method(method, HttpMethod.requiresRequestBody(method) ? EMPTY_REQUEST_BODY : null);
        Headers headers = this.requestHeaders.build();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            builder.addHeader(headers.name(i), headers.value(i));
        }
        boolean bufferRequestBody = false;
        if (HttpMethod.permitsRequestBody(method)) {
            long j = this.fixedContentLength;
            if (j != -1) {
                builder.header("Content-Length", Long.toString(j));
            } else if (this.chunkLength > 0) {
                builder.header("Transfer-Encoding", "chunked");
            } else {
                bufferRequestBody = true;
            }
            if (headers.get("Content-Type") == null) {
                builder.header("Content-Type", "application/x-www-form-urlencoded");
            }
        }
        if (headers.get("User-Agent") == null) {
            builder.header("User-Agent", defaultUserAgent());
        }
        Request request = builder.build();
        OkHttpClient engineClient = this.client;
        if (Internal.instance.internalCache(engineClient) != null && !getUseCaches()) {
            engineClient = this.client.newBuilder().cache((Cache) null).build();
        }
        return new HttpEngine(engineClient, request, bufferRequestBody, true, false, streamAllocation, requestBody, priorResponse);
    }

    private String defaultUserAgent() {
        String agent = System.getProperty("http.agent");
        return agent != null ? Util.toHumanReadableAscii(agent) : Version.userAgent();
    }

    private HttpEngine getResponse() throws IOException {
        initHttpEngine();
        if (this.httpEngine.hasResponse()) {
            return this.httpEngine;
        }
        while (true) {
            if (execute(true)) {
                Response response = this.httpEngine.getResponse();
                Request followUp = this.httpEngine.followUpRequest();
                if (followUp == null) {
                    this.httpEngine.releaseStreamAllocation();
                    return this.httpEngine;
                }
                int i = this.followUpCount + 1;
                this.followUpCount = i;
                if (i <= 20) {
                    this.url = followUp.url().url();
                    this.requestHeaders = followUp.headers().newBuilder();
                    Sink requestBody = this.httpEngine.getRequestBody();
                    if (!followUp.method().equals(this.method)) {
                        requestBody = null;
                    }
                    if (requestBody == null || (requestBody instanceof RetryableSink)) {
                        StreamAllocation streamAllocation = this.httpEngine.close();
                        if (!this.httpEngine.sameConnection(followUp.url())) {
                            streamAllocation.release();
                            streamAllocation = null;
                        }
                        this.httpEngine = newHttpEngine(followUp.method(), streamAllocation, (RetryableSink) requestBody, response);
                    } else {
                        throw new HttpRetryException("Cannot retry streamed HTTP body", this.responseCode);
                    }
                } else {
                    throw new ProtocolException("Too many follow-up requests: " + this.followUpCount);
                }
            }
        }
    }

    private boolean execute(boolean readResponse) throws IOException {
        URLFilter uRLFilter = this.urlFilter;
        if (uRLFilter != null) {
            uRLFilter.checkURLPermitted(this.httpEngine.getRequest().url().url());
        }
        try {
            this.httpEngine.sendRequest();
            Connection connection = this.httpEngine.getConnection();
            if (connection != null) {
                this.route = connection.route();
                this.handshake = connection.handshake();
            } else {
                this.route = null;
                this.handshake = null;
            }
            if (readResponse) {
                this.httpEngine.readResponse();
            }
            if (0 != 0) {
                this.httpEngine.close().release();
            }
            return true;
        } catch (RequestException e) {
            IOException toThrow = e.getCause();
            this.httpEngineFailure = toThrow;
            throw toThrow;
        } catch (RouteException e2) {
            HttpEngine retryEngine = this.httpEngine.recover(e2.getLastConnectException());
            if (retryEngine != null) {
                this.httpEngine = retryEngine;
                if (0 != 0) {
                    retryEngine.close().release();
                }
                return false;
            }
            IOException toThrow2 = e2.getLastConnectException();
            this.httpEngineFailure = toThrow2;
            throw toThrow2;
        } catch (IOException e3) {
            HttpEngine retryEngine2 = this.httpEngine.recover(e3);
            if (retryEngine2 != null) {
                this.httpEngine = retryEngine2;
                if (0 != 0) {
                    retryEngine2.close().release();
                }
                return false;
            }
            this.httpEngineFailure = e3;
            throw e3;
        } catch (Throwable th) {
            if (1 != 0) {
                this.httpEngine.close().release();
            }
            throw th;
        }
    }

    public final boolean usingProxy() {
        Proxy proxy;
        Route route2 = this.route;
        if (route2 != null) {
            proxy = route2.proxy();
        } else {
            proxy = this.client.proxy();
        }
        return (proxy == null || proxy.type() == Proxy.Type.DIRECT) ? false : true;
    }

    public String getResponseMessage() throws IOException {
        return getResponse().getResponse().message();
    }

    public final int getResponseCode() throws IOException {
        return getResponse().getResponse().code();
    }

    public final void setRequestProperty(String field, String newValue) {
        if (this.connected) {
            throw new IllegalStateException("Cannot set request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (newValue == null) {
            Platform platform = Platform.get();
            platform.logW("Ignoring header " + field + " because its value was null.");
        } else if ("X-Android-Transports".equals(field) || "X-Android-Protocols".equals(field)) {
            setProtocols(newValue, false);
        } else {
            this.requestHeaders.set(field, newValue);
        }
    }

    public void setIfModifiedSince(long newValue) {
        super.setIfModifiedSince(newValue);
        if (this.ifModifiedSince != 0) {
            this.requestHeaders.set("If-Modified-Since", HttpDate.format(new Date(this.ifModifiedSince)));
        } else {
            this.requestHeaders.removeAll("If-Modified-Since");
        }
    }

    public final void addRequestProperty(String field, String value) {
        if (this.connected) {
            throw new IllegalStateException("Cannot add request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (value == null) {
            Platform platform = Platform.get();
            platform.logW("Ignoring header " + field + " because its value was null.");
        } else if ("X-Android-Transports".equals(field) || "X-Android-Protocols".equals(field)) {
            setProtocols(value, true);
        } else {
            this.requestHeaders.add(field, value);
        }
    }

    private void setProtocols(String protocolsString, boolean append) {
        List<Protocol> protocolsList = new ArrayList<>();
        if (append) {
            protocolsList.addAll(this.client.protocols());
        }
        String[] split = protocolsString.split(",", -1);
        int length = split.length;
        int i = 0;
        while (i < length) {
            try {
                protocolsList.add(Protocol.get(split[i]));
                i++;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        this.client = this.client.newBuilder().protocols(protocolsList).build();
    }

    public void setRequestMethod(String method) throws ProtocolException {
        if (METHODS.contains(method)) {
            this.method = method;
            return;
        }
        throw new ProtocolException("Expected one of " + METHODS + " but was " + method);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        setFixedLengthStreamingMode((long) contentLength);
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        } else if (this.chunkLength > 0) {
            throw new IllegalStateException("Already in chunked mode");
        } else if (contentLength >= 0) {
            this.fixedContentLength = contentLength;
            this.fixedContentLength = (int) Math.min(contentLength, 2147483647L);
        } else {
            throw new IllegalArgumentException("contentLength < 0");
        }
    }
}
