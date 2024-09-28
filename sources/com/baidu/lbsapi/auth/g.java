package com.baidu.lbsapi.auth;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import com.aliyun.security.yunceng.android.sdk.traceroute.d;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class g {
    private Context a;
    private String b = null;
    private HashMap<String, String> c = null;
    private String d = null;

    public g(Context context) {
        this.a = context;
    }

    private String a(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (networkCapabilities == null) {
                    return "wifi";
                }
                return networkCapabilities.hasTransport(1) ? d.c : networkCapabilities.hasTransport(0) ? "CELLULAR" : networkCapabilities.hasTransport(3) ? "ETHERNET" : networkCapabilities.hasTransport(6) ? "LoWPAN" : networkCapabilities.hasTransport(4) ? "VPN" : networkCapabilities.hasTransport(5) ? "WifiAware" : "wifi";
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isAvailable()) {
                    String extraInfo = activeNetworkInfo.getExtraInfo();
                    return extraInfo != null ? (extraInfo.trim().toLowerCase().equals("cmwap") || extraInfo.trim().toLowerCase().equals("uniwap") || extraInfo.trim().toLowerCase().equals("3gwap") || extraInfo.trim().toLowerCase().equals("ctwap")) ? extraInfo.trim().toLowerCase().equals("ctwap") ? "ctwap" : "cmwap" : "wifi" : "wifi";
                }
            }
            return null;
        } catch (Exception e) {
            if (a.a) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v0, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: java.io.BufferedReader} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.io.BufferedReader} */
    /* JADX WARNING: type inference failed for: r5v16, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r5v17, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r5v23 */
    /* JADX WARNING: type inference failed for: r5v24 */
    /* JADX WARNING: type inference failed for: r5v29 */
    /* JADX WARNING: type inference failed for: r5v31 */
    /* JADX WARNING: type inference failed for: r5v33 */
    /* JADX WARNING: type inference failed for: r5v35 */
    /* JADX WARNING: type inference failed for: r5v36 */
    /* JADX WARNING: type inference failed for: r5v37 */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0161, code lost:
        if (com.baidu.lbsapi.auth.a.a == false) goto L_0x01c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0163, code lost:
        r14.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0192, code lost:
        if (com.baidu.lbsapi.auth.a.a == false) goto L_0x01c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x01c2, code lost:
        if (com.baidu.lbsapi.auth.a.a == false) goto L_0x01c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0124, code lost:
        r14 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0125, code lost:
        r5 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0128, code lost:
        r14 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0129, code lost:
        r5 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x012b, code lost:
        r14 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x012c, code lost:
        r5 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x012e, code lost:
        r14 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x012f, code lost:
        r5 = r8;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x016d A[Catch:{ all -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x018b A[SYNTHETIC, Splitter:B:112:0x018b] */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x019b A[Catch:{ all -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01bb A[SYNTHETIC, Splitter:B:125:0x01bb] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x01c7 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01f7  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0205  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x021e A[SYNTHETIC, Splitter:B:141:0x021e] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00b4 A[Catch:{ all -> 0x0109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00f4 A[Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00fa A[SYNTHETIC, Splitter:B:60:0x00fa] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0116 A[Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0124 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:7:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x013a A[Catch:{ all -> 0x0131 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x015a A[SYNTHETIC, Splitter:B:98:0x015a] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:92:0x0136=Splitter:B:92:0x0136, B:119:0x0197=Splitter:B:119:0x0197, B:106:0x0169=Splitter:B:106:0x0169} */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(javax.net.ssl.HttpsURLConnection r14) {
        /*
            r13 = this;
            java.lang.String r0 = "httpsPost failed,IOException:"
            java.lang.String r1 = "UTF-8"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "https Post start,url:"
            r2.append(r3)
            java.lang.String r3 = r13.b
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            com.baidu.lbsapi.auth.a.a(r2)
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r13.c
            if (r2 != 0) goto L_0x0027
            java.lang.String r14 = "httpsPost request paramters is null."
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r14)
            r13.d = r14
            return
        L_0x0027:
            r2 = 1
            r3 = 200(0xc8, float:2.8E-43)
            r4 = -1
            r5 = 0
            r6 = 0
            r7 = -11
            java.io.OutputStream r8 = r14.getOutputStream()     // Catch:{ MalformedURLException -> 0x0195, IOException -> 0x0167, Exception -> 0x0134 }
            java.io.BufferedWriter r9 = new java.io.BufferedWriter     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.io.OutputStreamWriter r10 = new java.io.OutputStreamWriter     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r10.<init>(r8, r1)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r9.<init>(r10)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.util.HashMap<java.lang.String, java.lang.String> r10 = r13.c     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.lang.String r10 = b(r10)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r9.write(r10)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.util.HashMap<java.lang.String, java.lang.String> r10 = r13.c     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.lang.String r10 = b(r10)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            com.baidu.lbsapi.auth.a.a(r10)     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r9.flush()     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r9.close()     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            r14.connect()     // Catch:{ MalformedURLException -> 0x012e, IOException -> 0x012b, Exception -> 0x0128, all -> 0x0124 }
            java.io.InputStream r9 = r14.getInputStream()     // Catch:{ IOException -> 0x00ad, all -> 0x00a9 }
            int r10 = r14.getResponseCode()     // Catch:{ IOException -> 0x00a5, all -> 0x00a1 }
            if (r3 != r10) goto L_0x0091
            java.io.BufferedReader r11 = new java.io.BufferedReader     // Catch:{ IOException -> 0x008d, all -> 0x0088 }
            java.io.InputStreamReader r12 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x008d, all -> 0x0088 }
            r12.<init>(r9, r1)     // Catch:{ IOException -> 0x008d, all -> 0x0088 }
            r11.<init>(r12)     // Catch:{ IOException -> 0x008d, all -> 0x0088 }
            java.lang.StringBuffer r1 = new java.lang.StringBuffer     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            r1.<init>()     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
        L_0x0071:
            int r5 = r11.read()     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            if (r5 == r4) goto L_0x007c
            char r5 = (char) r5     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            r1.append(r5)     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            goto L_0x0071
        L_0x007c:
            java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            r13.d = r1     // Catch:{ IOException -> 0x0086, all -> 0x0084 }
            r5 = r11
            goto L_0x0091
        L_0x0084:
            r1 = move-exception
            goto L_0x008a
        L_0x0086:
            r1 = move-exception
            goto L_0x008f
        L_0x0088:
            r1 = move-exception
            r11 = r5
        L_0x008a:
            r5 = r9
            goto L_0x010a
        L_0x008d:
            r1 = move-exception
            r11 = r5
        L_0x008f:
            r5 = r9
            goto L_0x00b0
        L_0x0091:
            if (r9 == 0) goto L_0x009b
            if (r5 == 0) goto L_0x009b
            r5.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
            r9.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x009b:
            if (r14 == 0) goto L_0x00f8
            r14.disconnect()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
            goto L_0x00f8
        L_0x00a1:
            r1 = move-exception
            r11 = r5
            r5 = r9
            goto L_0x00ab
        L_0x00a5:
            r1 = move-exception
            r11 = r5
            r5 = r9
            goto L_0x00af
        L_0x00a9:
            r1 = move-exception
            r11 = r5
        L_0x00ab:
            r10 = -1
            goto L_0x010a
        L_0x00ad:
            r1 = move-exception
            r11 = r5
        L_0x00af:
            r10 = -1
        L_0x00b0:
            boolean r2 = com.baidu.lbsapi.auth.a.a     // Catch:{ all -> 0x0109 }
            if (r2 == 0) goto L_0x00cf
            r1.printStackTrace()     // Catch:{ all -> 0x0109 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0109 }
            r2.<init>()     // Catch:{ all -> 0x0109 }
            java.lang.String r9 = "httpsPost parse failed;"
            r2.append(r9)     // Catch:{ all -> 0x0109 }
            java.lang.String r9 = r1.getMessage()     // Catch:{ all -> 0x0109 }
            r2.append(r9)     // Catch:{ all -> 0x0109 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0109 }
            com.baidu.lbsapi.auth.a.a(r2)     // Catch:{ all -> 0x0109 }
        L_0x00cf:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0109 }
            r2.<init>()     // Catch:{ all -> 0x0109 }
            r2.append(r0)     // Catch:{ all -> 0x0109 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x0109 }
            r2.append(r1)     // Catch:{ all -> 0x0109 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x0109 }
            java.lang.String r1 = com.baidu.lbsapi.auth.ErrorMessage.a(r7, r1)     // Catch:{ all -> 0x0109 }
            r13.d = r1     // Catch:{ all -> 0x0109 }
            if (r5 == 0) goto L_0x00f2
            if (r11 == 0) goto L_0x00f2
            r11.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
            r5.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x00f2:
            if (r14 == 0) goto L_0x00f7
            r14.disconnect()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x00f7:
            r2 = 0
        L_0x00f8:
            if (r8 == 0) goto L_0x0106
            r8.close()     // Catch:{ IOException -> 0x00fe }
            goto L_0x0106
        L_0x00fe:
            r14 = move-exception
            boolean r0 = com.baidu.lbsapi.auth.a.a
            if (r0 == 0) goto L_0x0106
            r14.printStackTrace()
        L_0x0106:
            r6 = r2
            goto L_0x01c5
        L_0x0109:
            r1 = move-exception
        L_0x010a:
            if (r5 == 0) goto L_0x0114
            if (r11 == 0) goto L_0x0114
            r11.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
            r5.close()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x0114:
            if (r14 == 0) goto L_0x0119
            r14.disconnect()     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x0119:
            throw r1     // Catch:{ MalformedURLException -> 0x0120, IOException -> 0x011d, Exception -> 0x011a, all -> 0x0124 }
        L_0x011a:
            r14 = move-exception
            r5 = r8
            goto L_0x0136
        L_0x011d:
            r14 = move-exception
            r5 = r8
            goto L_0x0169
        L_0x0120:
            r14 = move-exception
            r5 = r8
            goto L_0x0197
        L_0x0124:
            r14 = move-exception
            r5 = r8
            goto L_0x021c
        L_0x0128:
            r14 = move-exception
            r5 = r8
            goto L_0x0135
        L_0x012b:
            r14 = move-exception
            r5 = r8
            goto L_0x0168
        L_0x012e:
            r14 = move-exception
            r5 = r8
            goto L_0x0196
        L_0x0131:
            r14 = move-exception
            goto L_0x021c
        L_0x0134:
            r14 = move-exception
        L_0x0135:
            r10 = -1
        L_0x0136:
            boolean r0 = com.baidu.lbsapi.auth.a.a     // Catch:{ all -> 0x0131 }
            if (r0 == 0) goto L_0x013d
            r14.printStackTrace()     // Catch:{ all -> 0x0131 }
        L_0x013d:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0131 }
            r0.<init>()     // Catch:{ all -> 0x0131 }
            java.lang.String r1 = "httpsPost failed,Exception:"
            r0.append(r1)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r14.getMessage()     // Catch:{ all -> 0x0131 }
            r0.append(r14)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r0.toString()     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r7, r14)     // Catch:{ all -> 0x0131 }
            r13.d = r14     // Catch:{ all -> 0x0131 }
            if (r5 == 0) goto L_0x01c5
            r5.close()     // Catch:{ IOException -> 0x015e }
            goto L_0x01c5
        L_0x015e:
            r14 = move-exception
            boolean r0 = com.baidu.lbsapi.auth.a.a
            if (r0 == 0) goto L_0x01c5
        L_0x0163:
            r14.printStackTrace()
            goto L_0x01c5
        L_0x0167:
            r14 = move-exception
        L_0x0168:
            r10 = -1
        L_0x0169:
            boolean r1 = com.baidu.lbsapi.auth.a.a     // Catch:{ all -> 0x0131 }
            if (r1 == 0) goto L_0x0170
            r14.printStackTrace()     // Catch:{ all -> 0x0131 }
        L_0x0170:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0131 }
            r1.<init>()     // Catch:{ all -> 0x0131 }
            r1.append(r0)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r14.getMessage()     // Catch:{ all -> 0x0131 }
            r1.append(r14)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r1.toString()     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r7, r14)     // Catch:{ all -> 0x0131 }
            r13.d = r14     // Catch:{ all -> 0x0131 }
            if (r5 == 0) goto L_0x01c5
            r5.close()     // Catch:{ IOException -> 0x018f }
            goto L_0x01c5
        L_0x018f:
            r14 = move-exception
            boolean r0 = com.baidu.lbsapi.auth.a.a
            if (r0 == 0) goto L_0x01c5
            goto L_0x0163
        L_0x0195:
            r14 = move-exception
        L_0x0196:
            r10 = -1
        L_0x0197:
            boolean r0 = com.baidu.lbsapi.auth.a.a     // Catch:{ all -> 0x0131 }
            if (r0 == 0) goto L_0x019e
            r14.printStackTrace()     // Catch:{ all -> 0x0131 }
        L_0x019e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0131 }
            r0.<init>()     // Catch:{ all -> 0x0131 }
            java.lang.String r1 = "httpsPost failed,MalformedURLException:"
            r0.append(r1)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r14.getMessage()     // Catch:{ all -> 0x0131 }
            r0.append(r14)     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = r0.toString()     // Catch:{ all -> 0x0131 }
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r7, r14)     // Catch:{ all -> 0x0131 }
            r13.d = r14     // Catch:{ all -> 0x0131 }
            if (r5 == 0) goto L_0x01c5
            r5.close()     // Catch:{ IOException -> 0x01bf }
            goto L_0x01c5
        L_0x01bf:
            r14 = move-exception
            boolean r0 = com.baidu.lbsapi.auth.a.a
            if (r0 == 0) goto L_0x01c5
            goto L_0x0163
        L_0x01c5:
            if (r6 == 0) goto L_0x01f3
            if (r3 == r10) goto L_0x01f3
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r0 = "httpsPost failed,statusCode:"
            r14.append(r0)
            r14.append(r10)
            java.lang.String r14 = r14.toString()
            com.baidu.lbsapi.auth.a.a(r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r0)
            r14.append(r10)
            java.lang.String r14 = r14.toString()
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r7, r14)
            r13.d = r14
            return
        L_0x01f3:
            java.lang.String r14 = r13.d
            if (r14 != 0) goto L_0x0205
            java.lang.String r14 = "httpsPost failed,mResult is null"
            com.baidu.lbsapi.auth.a.a(r14)
            java.lang.String r14 = "httpsPost failed,internal error"
            java.lang.String r14 = com.baidu.lbsapi.auth.ErrorMessage.a(r4, r14)
            r13.d = r14
            return
        L_0x0205:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r0 = "httpsPost success end,parse result = "
            r14.append(r0)
            java.lang.String r0 = r13.d
            r14.append(r0)
            java.lang.String r14 = r14.toString()
            com.baidu.lbsapi.auth.a.a(r14)
            return
        L_0x021c:
            if (r5 == 0) goto L_0x022a
            r5.close()     // Catch:{ IOException -> 0x0222 }
            goto L_0x022a
        L_0x0222:
            r0 = move-exception
            boolean r1 = com.baidu.lbsapi.auth.a.a
            if (r1 == 0) goto L_0x022a
            r0.printStackTrace()
        L_0x022a:
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.lbsapi.auth.g.a(javax.net.ssl.HttpsURLConnection):void");
    }

    private static String b(HashMap<String, String> hashMap) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Map.Entry next : hashMap.entrySet()) {
            if (z) {
                z = false;
            } else {
                sb.append("&");
            }
            sb.append(URLEncoder.encode((String) next.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode((String) next.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    private HttpsURLConnection b() {
        String str;
        try {
            URL url = new URL(this.b);
            a.a("https URL: " + this.b);
            String a2 = a(this.a);
            if (a2 != null) {
                if (!a2.equals("")) {
                    a.a("checkNetwork = " + a2);
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) (a2.equals("cmwap") ? url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80))) : a2.equals("ctwap") ? url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80))) : url.openConnection());
                    httpsURLConnection.setHostnameVerifier(new h(this));
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setConnectTimeout(50000);
                    httpsURLConnection.setReadTimeout(50000);
                    return httpsURLConnection;
                }
            }
            a.c("Current network is not available.");
            this.d = ErrorMessage.a(-10, "Current network is not available.");
            return null;
        } catch (MalformedURLException e) {
            if (a.a) {
                e.printStackTrace();
                a.a(e.getMessage());
            }
            str = "Auth server could not be parsed as a URL.";
            this.d = ErrorMessage.a(-11, str);
            return null;
        } catch (Exception e2) {
            if (a.a) {
                e2.printStackTrace();
                a.a(e2.getMessage());
            }
            str = "Init httpsurlconnection failed.";
            this.d = ErrorMessage.a(-11, str);
            return null;
        }
    }

    private HashMap<String, String> c(HashMap<String, String> hashMap) {
        HashMap<String, String> hashMap2 = new HashMap<>();
        for (String str : hashMap.keySet()) {
            String str2 = str.toString();
            hashMap2.put(str2, hashMap.get(str2));
        }
        return hashMap2;
    }

    /* access modifiers changed from: protected */
    public String a(HashMap<String, String> hashMap) {
        HashMap<String, String> c2 = c(hashMap);
        this.c = c2;
        this.b = c2.get(ImagesContract.URL);
        HttpsURLConnection b2 = b();
        if (b2 == null) {
            a.c("syncConnect failed,httpsURLConnection is null");
        } else {
            a(b2);
        }
        return this.d;
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        a.a("checkNetwork start");
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.a.getSystemService("connectivity");
            if (connectivityManager == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= 29) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return networkCapabilities != null && networkCapabilities.hasCapability(12) && networkCapabilities.hasCapability(16);
            }
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
                return false;
            }
            a.a("checkNetwork end");
            return true;
        } catch (Exception e) {
            if (a.a) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
