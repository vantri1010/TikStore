package com.baidu.mapapi.http;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import com.baidu.mapapi.JNIInitializer;
import com.baidu.mapapi.common.Logger;
import com.baidu.mapsdkplatform.comapi.util.h;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpClient {
    public static boolean isHttpsEnable = true;
    HttpURLConnection a;
    private String b = null;
    private String c = null;
    private int d;
    private int e;
    private String f;
    private ProtoResultCallback g;

    public enum HttpStateError {
        NO_ERROR,
        NETWORK_ERROR,
        INNER_ERROR,
        REQUEST_ERROR,
        SERVER_ERROR
    }

    public static abstract class ProtoResultCallback {
        public abstract void onFailed(HttpStateError httpStateError);

        public abstract void onSuccess(String str);
    }

    public HttpClient(String str, ProtoResultCallback protoResultCallback) {
        this.f = str;
        this.g = protoResultCallback;
    }

    private HttpURLConnection a() {
        HttpURLConnection httpURLConnection;
        try {
            URL url = new URL(this.b);
            if (isHttpsEnable) {
                httpURLConnection = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(new b(this));
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
            httpURLConnection.setRequestMethod(this.f);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(this.d);
            httpURLConnection.setReadTimeout(this.e);
            return httpURLConnection;
        } catch (Exception e2) {
            Log.e("HttpClient", "url connect failed");
            if (Logger.debugEnable()) {
                e2.printStackTrace();
                return null;
            }
            Logger.logW("HttpClient", e2.getMessage());
            return null;
        }
    }

    public static String getAuthToken() {
        return h.d;
    }

    public static String getPhoneInfo() {
        return h.c();
    }

    /* access modifiers changed from: protected */
    public boolean checkNetwork() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) JNIInitializer.getCachedContext().getSystemService("connectivity");
            if (connectivityManager == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return networkCapabilities != null && networkCapabilities.hasCapability(12) && networkCapabilities.hasCapability(16);
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
        } catch (Exception e2) {
            if (Logger.debugEnable()) {
                e2.printStackTrace();
            } else {
                Logger.logW("HttpClient", e2.getMessage());
            }
            e2.printStackTrace();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010b A[Catch:{ all -> 0x0136 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x010f A[Catch:{ all -> 0x0136 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0130 A[Catch:{ Exception -> 0x014b }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0145 A[Catch:{ Exception -> 0x014b }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[Catch:{ Exception -> 0x014b }, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void request(java.lang.String r7) {
        /*
            r6 = this;
            r6.b = r7
            boolean r7 = r6.checkNetwork()
            if (r7 != 0) goto L_0x0010
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g
            com.baidu.mapapi.http.HttpClient$HttpStateError r0 = com.baidu.mapapi.http.HttpClient.HttpStateError.NETWORK_ERROR
            r7.onFailed(r0)
            return
        L_0x0010:
            java.net.HttpURLConnection r7 = r6.a()
            r6.a = r7
            java.lang.String r0 = "HttpClient"
            if (r7 != 0) goto L_0x0027
            java.lang.String r7 = "url connection failed"
            android.util.Log.e(r0, r7)
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g
            com.baidu.mapapi.http.HttpClient$HttpStateError r0 = com.baidu.mapapi.http.HttpClient.HttpStateError.INNER_ERROR
            r7.onFailed(r0)
            return
        L_0x0027:
            java.lang.String r7 = r6.b
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 == 0) goto L_0x0037
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g
            com.baidu.mapapi.http.HttpClient$HttpStateError r0 = com.baidu.mapapi.http.HttpClient.HttpStateError.REQUEST_ERROR
            r7.onFailed(r0)
            return
        L_0x0037:
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            r7.connect()     // Catch:{ Exception -> 0x014b }
            r7 = 0
            java.net.HttpURLConnection r1 = r6.a     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            int r1 = r1.getResponseCode()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r2 = 200(0xc8, float:2.8E-43)
            if (r2 != r1) goto L_0x0098
            java.net.HttpURLConnection r1 = r6.a     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.io.InputStream r1 = r1.getInputStream()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            java.lang.String r4 = "UTF-8"
            r3.<init>(r1, r4)     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            java.lang.StringBuffer r7 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x0089 }
            r7.<init>()     // Catch:{ Exception -> 0x0089 }
        L_0x005e:
            int r3 = r2.read()     // Catch:{ Exception -> 0x0089 }
            r4 = -1
            if (r3 == r4) goto L_0x006a
            char r3 = (char) r3     // Catch:{ Exception -> 0x0089 }
            r7.append(r3)     // Catch:{ Exception -> 0x0089 }
            goto L_0x005e
        L_0x006a:
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0089 }
            r6.c = r7     // Catch:{ Exception -> 0x0089 }
            if (r1 == 0) goto L_0x0078
            r2.close()     // Catch:{ Exception -> 0x014b }
            r1.close()     // Catch:{ Exception -> 0x014b }
        L_0x0078:
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            if (r7 == 0) goto L_0x0081
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            r7.disconnect()     // Catch:{ Exception -> 0x014b }
        L_0x0081:
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g
            java.lang.String r0 = r6.c
            r7.onSuccess(r0)
            return
        L_0x0089:
            r7 = move-exception
            goto L_0x0105
        L_0x008c:
            r2 = move-exception
            r5 = r2
            r2 = r7
            r7 = r5
            goto L_0x0137
        L_0x0092:
            r2 = move-exception
            r5 = r2
            r2 = r7
            r7 = r5
            goto L_0x0105
        L_0x0098:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r2.<init>()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r3 = "responseCode is: "
            r2.append(r3)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r2.append(r1)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            android.util.Log.e(r0, r2)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r2 = 500(0x1f4, float:7.0E-43)
            if (r1 < r2) goto L_0x00b3
            com.baidu.mapapi.http.HttpClient$HttpStateError r2 = com.baidu.mapapi.http.HttpClient.HttpStateError.SERVER_ERROR     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            goto L_0x00bc
        L_0x00b3:
            r2 = 400(0x190, float:5.6E-43)
            if (r1 < r2) goto L_0x00ba
            com.baidu.mapapi.http.HttpClient$HttpStateError r2 = com.baidu.mapapi.http.HttpClient.HttpStateError.REQUEST_ERROR     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            goto L_0x00bc
        L_0x00ba:
            com.baidu.mapapi.http.HttpClient$HttpStateError r2 = com.baidu.mapapi.http.HttpClient.HttpStateError.INNER_ERROR     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
        L_0x00bc:
            boolean r3 = com.baidu.mapapi.common.Logger.debugEnable()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            if (r3 == 0) goto L_0x00d0
            java.net.HttpURLConnection r1 = r6.a     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.io.InputStream r1 = r1.getErrorStream()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r3 = r1.toString()     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            com.baidu.mapapi.common.Logger.logW(r0, r3)     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            goto L_0x00ed
        L_0x00d0:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r3.<init>()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r4 = "Get response from server failed, http response code="
            r3.append(r4)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r3.append(r1)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r1 = ", error="
            r3.append(r1)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r3.append(r2)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            com.baidu.mapapi.common.Logger.logW(r0, r1)     // Catch:{ Exception -> 0x0101, all -> 0x00fc }
            r1 = r7
        L_0x00ed:
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r3 = r6.g     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            r3.onFailed(r2)     // Catch:{ Exception -> 0x0092, all -> 0x008c }
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            if (r7 == 0) goto L_0x00fb
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            r7.disconnect()     // Catch:{ Exception -> 0x014b }
        L_0x00fb:
            return
        L_0x00fc:
            r1 = move-exception
            r2 = r7
            r7 = r1
            r1 = r2
            goto L_0x0137
        L_0x0101:
            r1 = move-exception
            r2 = r7
            r7 = r1
            r1 = r2
        L_0x0105:
            boolean r3 = com.baidu.mapapi.common.Logger.debugEnable()     // Catch:{ all -> 0x0136 }
            if (r3 == 0) goto L_0x010f
            r7.printStackTrace()     // Catch:{ all -> 0x0136 }
            goto L_0x0116
        L_0x010f:
            java.lang.String r3 = r7.getMessage()     // Catch:{ all -> 0x0136 }
            com.baidu.mapapi.common.Logger.logW(r0, r3)     // Catch:{ all -> 0x0136 }
        L_0x0116:
            java.lang.String r3 = "Catch exception. INNER_ERROR"
            android.util.Log.e(r0, r3, r7)     // Catch:{ all -> 0x0136 }
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g     // Catch:{ all -> 0x0136 }
            com.baidu.mapapi.http.HttpClient$HttpStateError r3 = com.baidu.mapapi.http.HttpClient.HttpStateError.INNER_ERROR     // Catch:{ all -> 0x0136 }
            r7.onFailed(r3)     // Catch:{ all -> 0x0136 }
            if (r1 == 0) goto L_0x012c
            if (r2 == 0) goto L_0x012c
            r2.close()     // Catch:{ Exception -> 0x014b }
            r1.close()     // Catch:{ Exception -> 0x014b }
        L_0x012c:
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            if (r7 == 0) goto L_0x0135
            java.net.HttpURLConnection r7 = r6.a     // Catch:{ Exception -> 0x014b }
            r7.disconnect()     // Catch:{ Exception -> 0x014b }
        L_0x0135:
            return
        L_0x0136:
            r7 = move-exception
        L_0x0137:
            if (r1 == 0) goto L_0x0141
            if (r2 == 0) goto L_0x0141
            r2.close()     // Catch:{ Exception -> 0x014b }
            r1.close()     // Catch:{ Exception -> 0x014b }
        L_0x0141:
            java.net.HttpURLConnection r1 = r6.a     // Catch:{ Exception -> 0x014b }
            if (r1 == 0) goto L_0x014a
            java.net.HttpURLConnection r1 = r6.a     // Catch:{ Exception -> 0x014b }
            r1.disconnect()     // Catch:{ Exception -> 0x014b }
        L_0x014a:
            throw r7     // Catch:{ Exception -> 0x014b }
        L_0x014b:
            r7 = move-exception
            boolean r1 = com.baidu.mapapi.common.Logger.debugEnable()
            if (r1 == 0) goto L_0x0156
            r7.printStackTrace()
            goto L_0x015d
        L_0x0156:
            java.lang.String r1 = r7.getMessage()
            com.baidu.mapapi.common.Logger.logW(r0, r1)
        L_0x015d:
            java.lang.String r1 = "Catch connection exception, INNER_ERROR"
            android.util.Log.e(r0, r1, r7)
            com.baidu.mapapi.http.HttpClient$ProtoResultCallback r7 = r6.g
            com.baidu.mapapi.http.HttpClient$HttpStateError r0 = com.baidu.mapapi.http.HttpClient.HttpStateError.INNER_ERROR
            r7.onFailed(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.http.HttpClient.request(java.lang.String):void");
    }

    public void setMaxTimeOut(int i) {
        this.d = i;
    }

    public void setReadTimeOut(int i) {
        this.e = i;
    }
}
