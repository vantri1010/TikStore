package okhttp3.logging;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

public final class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile Level level;
    private final Logger logger;

    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public interface Logger {
        public static final Logger DEFAULT = new Logger() {
            public void log(String message) {
                Platform.get().log(4, message, (Throwable) null);
            }
        };

        void log(String str);
    }

    public HttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLoggingInterceptor(Logger logger2) {
        this.level = Level.NONE;
        this.logger = logger2;
    }

    public HttpLoggingInterceptor setLevel(Level level2) {
        if (level2 != null) {
            this.level = level2;
            return this;
        }
        throw new NullPointerException("level == null. Use Level.NONE instead.");
    }

    public Level getLevel() {
        return this.level;
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        String str;
        String str2;
        String requestStartMessage;
        boolean logBody;
        String str3;
        String str4;
        String bodySize;
        long contentLength;
        String str5;
        char c;
        String str6;
        String str7;
        String str8;
        int count;
        String requestStartMessage2;
        Interceptor.Chain chain2 = chain;
        Level level2 = this.level;
        Request request = chain.request();
        if (level2 == Level.NONE) {
            return chain2.proceed(request);
        }
        boolean hasRequestBody = false;
        boolean logBody2 = level2 == Level.BODY;
        boolean logHeaders = logBody2 || level2 == Level.HEADERS;
        RequestBody requestBody = request.body();
        if (requestBody != null) {
            hasRequestBody = true;
        }
        Connection connection = chain.connection();
        StringBuilder sb = new StringBuilder();
        sb.append("--> ");
        sb.append(request.method());
        sb.append(' ');
        sb.append(request.url());
        if (connection != null) {
            str = " " + connection.protocol();
        } else {
            str = "";
        }
        sb.append(str);
        String requestStartMessage3 = sb.toString();
        String str9 = "-byte body)";
        if (logHeaders || !hasRequestBody) {
            str2 = "";
            requestStartMessage = requestStartMessage3;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(requestStartMessage3);
            sb2.append(" (");
            str2 = "";
            sb2.append(requestBody.contentLength());
            sb2.append(str9);
            requestStartMessage = sb2.toString();
        }
        this.logger.log(requestStartMessage);
        if (logHeaders) {
            if (hasRequestBody) {
                if (requestBody.contentType() != null) {
                    Logger logger2 = this.logger;
                    StringBuilder sb3 = new StringBuilder();
                    Level level3 = level2;
                    sb3.append("Content-Type: ");
                    sb3.append(requestBody.contentType());
                    logger2.log(sb3.toString());
                }
                if (requestBody.contentLength() != -1) {
                    Logger logger3 = this.logger;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Content-Length: ");
                    str8 = str9;
                    str7 = " (";
                    sb4.append(requestBody.contentLength());
                    logger3.log(sb4.toString());
                } else {
                    str8 = str9;
                    str7 = " (";
                }
            } else {
                str8 = str9;
                str7 = " (";
            }
            Headers headers = request.headers();
            int i = 0;
            int count2 = headers.size();
            while (i < count2) {
                String name = headers.name(i);
                Connection connection2 = connection;
                if ("Content-Type".equalsIgnoreCase(name) || "Content-Length".equalsIgnoreCase(name)) {
                    requestStartMessage2 = requestStartMessage;
                    count = count2;
                } else {
                    Logger logger4 = this.logger;
                    requestStartMessage2 = requestStartMessage;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(name);
                    sb5.append(": ");
                    count = count2;
                    sb5.append(headers.value(i));
                    logger4.log(sb5.toString());
                }
                i++;
                connection = connection2;
                requestStartMessage = requestStartMessage2;
                count2 = count;
            }
            String str10 = requestStartMessage;
            int i2 = count2;
            if (!logBody2) {
                str9 = str8;
                str4 = str2;
                boolean z = hasRequestBody;
                logBody = logBody2;
                str3 = str7;
            } else if (!hasRequestBody) {
                Headers headers2 = headers;
                str9 = str8;
                str4 = str2;
                boolean z2 = hasRequestBody;
                logBody = logBody2;
                str3 = str7;
            } else if (bodyHasUnknownEncoding(request.headers())) {
                this.logger.log("--> END " + request.method() + " (encoded body omitted)");
                str9 = str8;
                str4 = str2;
                boolean z3 = hasRequestBody;
                logBody = logBody2;
                str3 = str7;
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                Headers headers3 = headers;
                str4 = str2;
                this.logger.log(str4);
                if (isPlaintext(buffer)) {
                    boolean z4 = hasRequestBody;
                    this.logger.log(buffer.readString(charset));
                    Logger logger5 = this.logger;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("--> END ");
                    sb6.append(request.method());
                    String str11 = str7;
                    sb6.append(str11);
                    Charset charset2 = charset;
                    MediaType mediaType = contentType;
                    sb6.append(requestBody.contentLength());
                    str9 = str8;
                    sb6.append(str9);
                    logger5.log(sb6.toString());
                    str3 = str11;
                    logBody = logBody2;
                } else {
                    Charset charset3 = charset;
                    str9 = str8;
                    str3 = str7;
                    MediaType mediaType2 = contentType;
                    Logger logger6 = this.logger;
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("--> END ");
                    sb7.append(request.method());
                    sb7.append(" (binary ");
                    logBody = logBody2;
                    sb7.append(requestBody.contentLength());
                    sb7.append("-byte body omitted)");
                    logger6.log(sb7.toString());
                }
            }
            this.logger.log("--> END " + request.method());
        } else {
            Connection connection3 = connection;
            logBody = logBody2;
            String str12 = requestStartMessage;
            str4 = str2;
            boolean z5 = hasRequestBody;
            str3 = " (";
        }
        long startNs = System.nanoTime();
        try {
            Response response = chain2.proceed(request);
            String str13 = str4;
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();
            long j = startNs;
            long contentLength2 = responseBody.contentLength();
            if (contentLength2 != -1) {
                Request request2 = request;
                StringBuilder sb8 = new StringBuilder();
                sb8.append(contentLength2);
                RequestBody requestBody2 = requestBody;
                sb8.append("-byte");
                bodySize = sb8.toString();
            } else {
                RequestBody requestBody3 = requestBody;
                bodySize = "unknown-length";
            }
            Logger logger7 = this.logger;
            String str14 = str13;
            StringBuilder sb9 = new StringBuilder();
            String str15 = str9;
            sb9.append("<-- ");
            sb9.append(response.code());
            if (response.message().isEmpty()) {
                contentLength = contentLength2;
                str5 = str14;
                c = ' ';
            } else {
                StringBuilder sb10 = new StringBuilder();
                contentLength = contentLength2;
                c = ' ';
                sb10.append(' ');
                sb10.append(response.message());
                str5 = sb10.toString();
            }
            sb9.append(str5);
            sb9.append(c);
            sb9.append(response.request().url());
            sb9.append(str3);
            sb9.append(tookMs);
            sb9.append("ms");
            if (!logHeaders) {
                str6 = ", " + bodySize + " body";
            } else {
                str6 = str14;
            }
            sb9.append(str6);
            sb9.append(')');
            logger7.log(sb9.toString());
            if (logHeaders) {
                Headers headers4 = response.headers();
                int count3 = headers4.size();
                for (int i3 = 0; i3 < count3; i3++) {
                    this.logger.log(headers4.name(i3) + ": " + headers4.value(i3));
                }
                if (!logBody) {
                    String str16 = bodySize;
                    Headers headers5 = headers4;
                } else if (!HttpHeaders.hasBody(response)) {
                    long j2 = tookMs;
                    String str17 = bodySize;
                    Headers headers6 = headers4;
                } else if (bodyHasUnknownEncoding(response.headers())) {
                    this.logger.log("<-- END HTTP (encoded body omitted)");
                    long j3 = tookMs;
                    String str18 = bodySize;
                } else {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE);
                    Buffer buffer2 = source.buffer();
                    Long gzippedLength = null;
                    if ("gzip".equalsIgnoreCase(headers4.get("Content-Encoding"))) {
                        gzippedLength = Long.valueOf(buffer2.size());
                        GzipSource gzippedResponseBody = null;
                        try {
                            gzippedResponseBody = new GzipSource(buffer2.clone());
                            buffer2 = new Buffer();
                            buffer2.writeAll(gzippedResponseBody);
                            gzippedResponseBody.close();
                        } catch (Throwable th) {
                            if (gzippedResponseBody != null) {
                                gzippedResponseBody.close();
                            }
                            throw th;
                        }
                    }
                    Charset charset4 = UTF8;
                    MediaType contentType2 = responseBody.contentType();
                    if (contentType2 != null) {
                        charset4 = contentType2.charset(UTF8);
                    }
                    if (!isPlaintext(buffer2)) {
                        long j4 = tookMs;
                        this.logger.log(str14);
                        Logger logger8 = this.logger;
                        StringBuilder sb11 = new StringBuilder();
                        sb11.append("<-- END HTTP (binary ");
                        String str19 = bodySize;
                        Headers headers7 = headers4;
                        sb11.append(buffer2.size());
                        sb11.append("-byte body omitted)");
                        logger8.log(sb11.toString());
                        return response;
                    }
                    String str20 = bodySize;
                    Headers headers8 = headers4;
                    String str21 = str14;
                    if (contentLength != 0) {
                        this.logger.log(str21);
                        this.logger.log(buffer2.clone().readString(charset4));
                    }
                    if (gzippedLength != null) {
                        this.logger.log("<-- END HTTP (" + buffer2.size() + "-byte, " + gzippedLength + "-gzipped-byte body)");
                    } else {
                        this.logger.log("<-- END HTTP (" + buffer2.size() + str15);
                    }
                }
                this.logger.log("<-- END HTTP");
            } else {
                String str22 = bodySize;
            }
            return response;
        } catch (Exception e) {
            Request request3 = request;
            long j5 = startNs;
            RequestBody requestBody4 = requestBody;
            Exception e2 = e;
            this.logger.log("<-- HTTP FAILED: " + e2);
            throw e2;
        }
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = 64;
            if (buffer.size() < 64) {
                byteCount = buffer.size();
            }
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    return true;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity") && !contentEncoding.equalsIgnoreCase("gzip");
    }
}
