package com.stripe.android.net;

import com.baidu.mapapi.UIMsg;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.exception.PermissionException;
import com.stripe.android.exception.RateLimitException;
import com.stripe.android.model.Token;
import com.stripe.android.net.ErrorParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class StripeApiHandler {
    public static final String CHARSET = "UTF-8";
    private static final String DNS_CACHE_TTL_PROPERTY_NAME = "networkaddress.cache.ttl";
    static final String GET = "GET";
    public static final String LIVE_API_BASE = "https://api.stripe.com";
    static final String POST = "POST";
    private static final SSLSocketFactory SSL_SOCKET_FACTORY = new StripeSSLSocketFactory();
    public static final String TOKENS = "tokens";
    public static final String VERSION = "3.5.0";

    @Retention(RetentionPolicy.SOURCE)
    @interface RestMethod {
    }

    public static Token createToken(Map<String, Object> cardParams, RequestOptions options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        return requestToken(POST, getApiUrl(), cardParams, options);
    }

    public static Token retrieveToken(RequestOptions options, String tokenId) throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException {
        try {
            return requestToken(GET, getRetrieveTokenApiUrl(tokenId), (Map<String, Object>) null, options);
        } catch (CardException cardException) {
            throw new APIException(cardException.getMessage(), cardException.getRequestId(), cardException.getStatusCode(), cardException);
        }
    }

    static String createQuery(Map<String, Object> params) throws UnsupportedEncodingException, InvalidRequestException {
        StringBuilder queryStringBuffer = new StringBuilder();
        for (Parameter param : flattenParams(params)) {
            if (queryStringBuffer.length() > 0) {
                queryStringBuffer.append("&");
            }
            queryStringBuffer.append(urlEncodePair(param.key, param.value));
        }
        return queryStringBuffer.toString();
    }

    static Map<String, String> getHeaders(RequestOptions options) {
        Map<String, String> headers = new HashMap<>();
        String apiVersion = options.getApiVersion();
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Accept", "application/json");
        headers.put("User-Agent", String.format("Stripe/v1 JavaBindings/%s", new Object[]{VERSION}));
        headers.put("Authorization", String.format("Bearer %s", new Object[]{options.getPublishableApiKey()}));
        Map<String, String> propertyMap = new HashMap<>();
        for (String propertyName : new String[]{"os.name", "os.version", "os.arch", "java.version", "java.vendor", "java.vm.version", "java.vm.vendor"}) {
            propertyMap.put(propertyName, System.getProperty(propertyName));
        }
        propertyMap.put("bindings.version", VERSION);
        propertyMap.put("lang", "Java");
        propertyMap.put("publisher", "Stripe");
        headers.put("X-Stripe-Client-User-Agent", new JSONObject(propertyMap).toString());
        if (apiVersion != null) {
            headers.put("Stripe-Version", apiVersion);
        }
        if (options.getIdempotencyKey() != null) {
            headers.put("Idempotency-Key", options.getIdempotencyKey());
        }
        return headers;
    }

    static String getApiUrl() {
        return String.format("%s/v1/%s", new Object[]{LIVE_API_BASE, TOKENS});
    }

    static String getRetrieveTokenApiUrl(String tokenId) {
        return String.format("%s/%s", new Object[]{getApiUrl(), tokenId});
    }

    private static String formatURL(String url, String query) {
        if (query == null || query.isEmpty()) {
            return url;
        }
        String separator = "?";
        if (url.contains(separator)) {
            separator = "&";
        }
        return String.format("%s%s%s", new Object[]{url, separator, query});
    }

    private static HttpURLConnection createGetConnection(String url, String query, RequestOptions options) throws IOException {
        HttpURLConnection conn = createStripeConnection(formatURL(url, query), options);
        conn.setRequestMethod(GET);
        return conn;
    }

    private static HttpURLConnection createPostConnection(String url, String query, RequestOptions options) throws IOException {
        HttpURLConnection conn = createStripeConnection(url, options);
        conn.setDoOutput(true);
        conn.setRequestMethod(POST);
        conn.setRequestProperty("Content-Type", String.format("application/x-www-form-urlencoded;charset=%s", new Object[]{"UTF-8"}));
        OutputStream output = null;
        try {
            output = conn.getOutputStream();
            output.write(query.getBytes("UTF-8"));
            return conn;
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private static HttpURLConnection createStripeConnection(String url, RequestOptions options) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT);
        conn.setReadTimeout(80000);
        conn.setUseCaches(false);
        for (Map.Entry<String, String> header : getHeaders(options).entrySet()) {
            conn.setRequestProperty(header.getKey(), header.getValue());
        }
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(SSL_SOCKET_FACTORY);
        }
        return conn;
    }

    private static Token requestToken(String method, String url, Map<String, Object> params, RequestOptions options) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        if (options == null) {
            return null;
        }
        String originalDNSCacheTTL = null;
        Boolean allowedToSetTTL = true;
        try {
            originalDNSCacheTTL = Security.getProperty(DNS_CACHE_TTL_PROPERTY_NAME);
            Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "0");
        } catch (SecurityException e) {
            allowedToSetTTL = false;
        }
        if (!options.getPublishableApiKey().trim().isEmpty()) {
            try {
                StripeResponse response = getStripeResponse(method, url, params, options);
                int rCode = response.getResponseCode();
                String rBody = response.getResponseBody();
                String requestId = null;
                Map<String, List<String>> headers = response.getResponseHeaders();
                List<String> requestIdList = headers == null ? null : headers.get("Request-Id");
                if (requestIdList != null && requestIdList.size() > 0) {
                    requestId = requestIdList.get(0);
                }
                if (rCode < 200 || rCode >= 300) {
                    handleAPIError(rBody, rCode, requestId);
                }
                Token parseToken = TokenParser.parseToken(rBody);
                if (allowedToSetTTL.booleanValue()) {
                    if (originalDNSCacheTTL == null) {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                    } else {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                    }
                }
                return parseToken;
            } catch (JSONException e2) {
                if (allowedToSetTTL.booleanValue()) {
                    if (originalDNSCacheTTL == null) {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                    } else {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                    }
                }
                return null;
            } catch (Throwable th) {
                if (allowedToSetTTL.booleanValue()) {
                    if (originalDNSCacheTTL == null) {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, "-1");
                    } else {
                        Security.setProperty(DNS_CACHE_TTL_PROPERTY_NAME, originalDNSCacheTTL);
                    }
                }
                throw th;
            }
        } else {
            throw new AuthenticationException("No API key provided. (HINT: set your API key using 'Stripe.apiKey = <API-KEY>'. You can generate API keys from the Stripe web interface. See https://stripe.com/api for details or email support@stripe.com if you have questions.", (String) null, 0);
        }
    }

    private static StripeResponse getStripeResponse(String method, String url, Map<String, Object> params, RequestOptions options) throws InvalidRequestException, APIConnectionException, APIException {
        try {
            return makeURLConnectionRequest(method, url, createQuery(params), options);
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException("Unable to encode parameters to UTF-8. Please contact support@stripe.com for assistance.", (String) null, (String) null, 0, e);
        }
    }

    private static List<Parameter> flattenParams(Map<String, Object> params) throws InvalidRequestException {
        return flattenParamsMap(params, (String) null);
    }

    private static List<Parameter> flattenParamsList(List<Object> params, String keyPrefix) throws InvalidRequestException {
        List<Parameter> flatParams = new LinkedList<>();
        String newPrefix = String.format("%s[]", new Object[]{keyPrefix});
        if (params.isEmpty()) {
            flatParams.add(new Parameter(keyPrefix, ""));
        } else {
            for (Object flattenParamsValue : params) {
                flatParams.addAll(flattenParamsValue(flattenParamsValue, newPrefix));
            }
        }
        return flatParams;
    }

    private static List<Parameter> flattenParamsMap(Map<String, Object> params, String keyPrefix) throws InvalidRequestException {
        List<Parameter> flatParams = new LinkedList<>();
        if (params == null) {
            return flatParams;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String newPrefix = key;
            if (keyPrefix != null) {
                newPrefix = String.format("%s[%s]", new Object[]{keyPrefix, key});
            }
            flatParams.addAll(flattenParamsValue(value, newPrefix));
        }
        return flatParams;
    }

    private static List<Parameter> flattenParamsValue(Object value, String keyPrefix) throws InvalidRequestException {
        if (value instanceof Map) {
            return flattenParamsMap((Map) value, keyPrefix);
        }
        if (value instanceof List) {
            return flattenParamsList((List) value, keyPrefix);
        }
        if ("".equals(value)) {
            throw new InvalidRequestException("You cannot set '" + keyPrefix + "' to an empty string. We interpret empty strings as null in requests. You may set '" + keyPrefix + "' to null to delete the property.", keyPrefix, (String) null, 0, (Throwable) null);
        } else if (value == null) {
            List<Parameter> flatParams = new LinkedList<>();
            flatParams.add(new Parameter(keyPrefix, ""));
            return flatParams;
        } else {
            List<Parameter> flatParams2 = new LinkedList<>();
            flatParams2.add(new Parameter(keyPrefix, value.toString()));
            return flatParams2;
        }
    }

    private static void handleAPIError(String rBody, int rCode, String requestId) throws InvalidRequestException, AuthenticationException, CardException, APIException {
        ErrorParser.StripeError stripeError = ErrorParser.parseError(rBody);
        if (rCode != 429) {
            switch (rCode) {
                case 400:
                    throw new InvalidRequestException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), (Throwable) null);
                case 401:
                    throw new AuthenticationException(stripeError.message, requestId, Integer.valueOf(rCode));
                case 402:
                    throw new CardException(stripeError.message, requestId, stripeError.code, stripeError.param, stripeError.decline_code, stripeError.charge, Integer.valueOf(rCode), (Throwable) null);
                case 403:
                    throw new PermissionException(stripeError.message, requestId, Integer.valueOf(rCode));
                case UIMsg.l_ErrorNo.NETWORK_ERROR_404 /*404*/:
                    throw new InvalidRequestException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), (Throwable) null);
                default:
                    throw new APIException(stripeError.message, requestId, Integer.valueOf(rCode), (Throwable) null);
            }
        } else {
            throw new RateLimitException(stripeError.message, stripeError.param, requestId, Integer.valueOf(rCode), (Throwable) null);
        }
    }

    private static String urlEncodePair(String k, String v) throws UnsupportedEncodingException {
        return String.format("%s=%s", new Object[]{urlEncode(k), urlEncode(v)});
    }

    private static String urlEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return URLEncoder.encode(str, "UTF-8");
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0028 A[Catch:{ IOException -> 0x0074, all -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0040 A[Catch:{ IOException -> 0x0074, all -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0052 A[Catch:{ IOException -> 0x0074, all -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x005b A[Catch:{ IOException -> 0x0074, all -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x006e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.stripe.android.net.StripeResponse makeURLConnectionRequest(java.lang.String r8, java.lang.String r9, java.lang.String r10, com.stripe.android.net.RequestOptions r11) throws com.stripe.android.exception.APIConnectionException {
        /*
            r0 = 0
            r1 = -1
            r2 = 0
            r3 = 1
            int r4 = r8.hashCode()     // Catch:{ IOException -> 0x0074 }
            r5 = 70454(0x11336, float:9.8727E-41)
            if (r4 == r5) goto L_0x001d
            r5 = 2461856(0x2590a0, float:3.449795E-39)
            if (r4 == r5) goto L_0x0013
        L_0x0012:
            goto L_0x0026
        L_0x0013:
            java.lang.String r4 = "POST"
            boolean r4 = r8.equals(r4)     // Catch:{ IOException -> 0x0074 }
            if (r4 == 0) goto L_0x0012
            r1 = 1
            goto L_0x0026
        L_0x001d:
            java.lang.String r4 = "GET"
            boolean r4 = r8.equals(r4)     // Catch:{ IOException -> 0x0074 }
            if (r4 == 0) goto L_0x0012
            r1 = 0
        L_0x0026:
            if (r1 == 0) goto L_0x0040
            if (r1 != r3) goto L_0x0030
            java.net.HttpURLConnection r1 = createPostConnection(r9, r10, r11)     // Catch:{ IOException -> 0x0074 }
            r0 = r1
            goto L_0x0046
        L_0x0030:
            com.stripe.android.exception.APIConnectionException r1 = new com.stripe.android.exception.APIConnectionException     // Catch:{ IOException -> 0x0074 }
            java.lang.String r4 = "Unrecognized HTTP method %s. This indicates a bug in the Stripe bindings. Please contact support@stripe.com for assistance."
            java.lang.Object[] r5 = new java.lang.Object[r3]     // Catch:{ IOException -> 0x0074 }
            r5[r2] = r8     // Catch:{ IOException -> 0x0074 }
            java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch:{ IOException -> 0x0074 }
            r1.<init>(r4)     // Catch:{ IOException -> 0x0074 }
            throw r1     // Catch:{ IOException -> 0x0074 }
        L_0x0040:
            java.net.HttpURLConnection r1 = createGetConnection(r9, r10, r11)     // Catch:{ IOException -> 0x0074 }
            r0 = r1
        L_0x0046:
            int r1 = r0.getResponseCode()     // Catch:{ IOException -> 0x0074 }
            r4 = 200(0xc8, float:2.8E-43)
            if (r1 < r4) goto L_0x005b
            r4 = 300(0x12c, float:4.2E-43)
            if (r1 >= r4) goto L_0x005b
            java.io.InputStream r4 = r0.getInputStream()     // Catch:{ IOException -> 0x0074 }
            java.lang.String r4 = getResponseBody(r4)     // Catch:{ IOException -> 0x0074 }
            goto L_0x0063
        L_0x005b:
            java.io.InputStream r4 = r0.getErrorStream()     // Catch:{ IOException -> 0x0074 }
            java.lang.String r4 = getResponseBody(r4)     // Catch:{ IOException -> 0x0074 }
        L_0x0063:
            java.util.Map r5 = r0.getHeaderFields()     // Catch:{ IOException -> 0x0074 }
            com.stripe.android.net.StripeResponse r6 = new com.stripe.android.net.StripeResponse     // Catch:{ IOException -> 0x0074 }
            r6.<init>(r1, r4, r5)     // Catch:{ IOException -> 0x0074 }
            if (r0 == 0) goto L_0x0071
            r0.disconnect()
        L_0x0071:
            return r6
        L_0x0072:
            r1 = move-exception
            goto L_0x0090
        L_0x0074:
            r1 = move-exception
            com.stripe.android.exception.APIConnectionException r4 = new com.stripe.android.exception.APIConnectionException     // Catch:{ all -> 0x0072 }
            java.lang.String r5 = "IOException during API request to Stripe (%s): %s Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripestatus, or let us know at support@stripe.com."
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0072 }
            java.lang.String r7 = getApiUrl()     // Catch:{ all -> 0x0072 }
            r6[r2] = r7     // Catch:{ all -> 0x0072 }
            java.lang.String r2 = r1.getMessage()     // Catch:{ all -> 0x0072 }
            r6[r3] = r2     // Catch:{ all -> 0x0072 }
            java.lang.String r2 = java.lang.String.format(r5, r6)     // Catch:{ all -> 0x0072 }
            r4.<init>(r2, r1)     // Catch:{ all -> 0x0072 }
            throw r4     // Catch:{ all -> 0x0072 }
        L_0x0090:
            if (r0 == 0) goto L_0x0095
            r0.disconnect()
        L_0x0095:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.stripe.android.net.StripeApiHandler.makeURLConnectionRequest(java.lang.String, java.lang.String, java.lang.String, com.stripe.android.net.RequestOptions):com.stripe.android.net.StripeResponse");
    }

    private static String getResponseBody(InputStream responseStream) throws IOException {
        String rBody = new Scanner(responseStream, "UTF-8").useDelimiter("\\A").next();
        responseStream.close();
        return rBody;
    }

    private static final class Parameter {
        public final String key;
        public final String value;

        public Parameter(String key2, String value2) {
            this.key = key2;
            this.value = value2;
        }
    }
}
