package com.baidu.location.indoor.mapversion.a;

import com.blankj.utilcode.constant.TimeConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class e implements Runnable {
    final /* synthetic */ d a;

    e(d dVar) {
        this.a = dVar;
    }

    public void run() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("http://loc.map.baidu.com/check_indoor_data_update").openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setReadTimeout(TimeConstants.MIN);
            httpURLConnection.setConnectTimeout(TimeConstants.MIN);
            StringBuilder sb = new StringBuilder();
            sb.append("&data_type=ps");
            sb.append("&bid=");
            sb.append(this.a.e);
            if (this.a.f != null) {
                sb.append("&md5=");
                sb.append(this.a.f);
            }
            httpURLConnection.getOutputStream().write(sb.toString().getBytes());
            if (httpURLConnection.getResponseCode() == 200) {
                String headerField = httpURLConnection.getHeaderField("Hash");
                if (headerField != null && (this.a.f == null || !headerField.equalsIgnoreCase(this.a.f))) {
                    String unused = this.a.f = headerField;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    FileWriter fileWriter = new FileWriter(new File(this.a.c, this.a.e));
                    fileWriter.write(headerField + "\n");
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        fileWriter.write(readLine);
                        fileWriter.write("\n");
                    }
                    fileWriter.flush();
                    fileWriter.close();
                }
                boolean unused2 = this.a.d();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            boolean unused3 = this.a.d = false;
            throw th;
        }
        boolean unused4 = this.a.d = false;
    }
}
