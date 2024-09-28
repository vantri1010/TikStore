package io.openinstall.sdk;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import org.json.JSONException;

public class aw {
    private bb a(HttpURLConnection httpURLConnection) throws IOException, JSONException {
        if (httpURLConnection.getResponseCode() != 200) {
            return new ba(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage());
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), ac.c));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                sb.append(readLine);
            } else {
                String sb2 = sb.toString();
                bufferedReader.close();
                return ay.a(sb2);
            }
        }
    }

    private void a(HttpURLConnection httpURLConnection, byte[] bArr) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
        bufferedOutputStream.write(bArr);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0090  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public io.openinstall.sdk.bb a(java.lang.String r3, java.lang.String r4, byte[] r5, java.util.HashMap<java.lang.String, java.lang.String> r6) {
        /*
            r2 = this;
            r0 = 0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0082 }
            r1.<init>()     // Catch:{ Exception -> 0x0082 }
            r1.append(r3)     // Catch:{ Exception -> 0x0082 }
            java.lang.String r3 = "?"
            r1.append(r3)     // Catch:{ Exception -> 0x0082 }
            r1.append(r4)     // Catch:{ Exception -> 0x0082 }
            java.lang.String r3 = r1.toString()     // Catch:{ Exception -> 0x0082 }
            java.net.URL r4 = new java.net.URL     // Catch:{ Exception -> 0x0082 }
            r4.<init>(r3)     // Catch:{ Exception -> 0x0082 }
            java.net.URLConnection r3 = r4.openConnection()     // Catch:{ Exception -> 0x0082 }
            java.net.HttpURLConnection r3 = (java.net.HttpURLConnection) r3     // Catch:{ Exception -> 0x0082 }
            r4 = 1
            r3.setDoInput(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r3.setDoOutput(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r4 = 0
            r3.setUseCaches(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.String r4 = "POST"
            r3.setRequestMethod(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r4 = 5000(0x1388, float:7.006E-42)
            r3.setConnectTimeout(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r3.setReadTimeout(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            int r4 = r5.length     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r3.setFixedLengthStreamingMode(r4)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.util.Set r4 = r6.entrySet()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
        L_0x0044:
            boolean r6 = r4.hasNext()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            if (r6 == 0) goto L_0x0060
            java.lang.Object r6 = r4.next()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.Object r0 = r6.getKey()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.Object r6 = r6.getValue()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r3.setRequestProperty(r0, r6)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            goto L_0x0044
        L_0x0060:
            java.lang.String r4 = "Connection"
            java.lang.String r6 = "close"
            r3.setRequestProperty(r4, r6)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r3.connect()     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            r2.a(r3, r5)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            io.openinstall.sdk.bb r4 = r2.a(r3)     // Catch:{ Exception -> 0x007d, all -> 0x007a }
            if (r3 == 0) goto L_0x0079
            r3.disconnect()
        L_0x0079:
            return r4
        L_0x007a:
            r4 = move-exception
            r0 = r3
            goto L_0x008e
        L_0x007d:
            r4 = move-exception
            r0 = r3
            goto L_0x0083
        L_0x0080:
            r4 = move-exception
            goto L_0x008e
        L_0x0082:
            r4 = move-exception
        L_0x0083:
            io.openinstall.sdk.az r3 = new io.openinstall.sdk.az     // Catch:{ all -> 0x0080 }
            r3.<init>(r4)     // Catch:{ all -> 0x0080 }
            if (r0 == 0) goto L_0x008d
            r0.disconnect()
        L_0x008d:
            return r3
        L_0x008e:
            if (r0 == 0) goto L_0x0093
            r0.disconnect()
        L_0x0093:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.aw.a(java.lang.String, java.lang.String, byte[], java.util.HashMap):io.openinstall.sdk.bb");
    }
}
