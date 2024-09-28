package com.qiniu.android.dns.dns;

import com.qiniu.android.dns.dns.DnsResolver;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.HttpsURLConnection;

public class DohResolver extends DnsResolver {
    public DohResolver(String server) {
        super(server);
    }

    public DohResolver(String server, int timeout) {
        super(server, timeout);
    }

    public DohResolver(String server, int recordType, int timeout) {
        super(server, recordType, timeout);
    }

    public DohResolver(String[] servers, int recordType, int timeout) {
        super(servers, recordType, timeout);
    }

    public DohResolver(String[] servers, int recordType, int timeout, ExecutorService executorService) {
        super(servers, recordType, timeout, executorService);
    }

    /* access modifiers changed from: package-private */
    public DnsResponse request(DnsResolver.RequestCanceller canceller, String server, String host, int recordType) throws IOException {
        String str = server;
        double d = Math.random();
        DnsRequest request = new DnsRequest((short) ((int) (65535.0d * d)), recordType, host);
        byte[] requestData = request.toDnsQuestionData();
        HttpsURLConnection httpConn = (HttpsURLConnection) new URL(str).openConnection();
        httpConn.setConnectTimeout(3000);
        httpConn.setReadTimeout(this.timeout * 1000);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/dns-message");
        httpConn.setRequestProperty("Accept", "application/dns-message");
        httpConn.setRequestProperty("Accept-Encoding", "");
        httpConn.setRequestProperty("User-Agent", "Dalvik/2.1.0");
        final DataOutputStream bodyStream = new DataOutputStream(httpConn.getOutputStream());
        final HttpsURLConnection finalConnection = httpConn;
        canceller.addCancelAction(new Runnable() {
            public void run() {
                try {
                    finalConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    bodyStream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
        bodyStream.write(requestData);
        bodyStream.close();
        if (httpConn.getResponseCode() != 200) {
            return null;
        }
        int length = httpConn.getContentLength();
        if (length <= 0) {
            return null;
        } else if (length > 1048576) {
            double d2 = d;
            return null;
        } else {
            InputStream is = httpConn.getInputStream();
            byte[] responseData = new byte[length];
            int read = is.read(responseData);
            is.close();
            if (read <= 0) {
                return null;
            }
            double d3 = d;
            return new DnsResponse(str, 5, request, responseData);
        }
    }
}
