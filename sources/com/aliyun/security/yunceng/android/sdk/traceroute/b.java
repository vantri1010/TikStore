package com.aliyun.security.yunceng.android.sdk.traceroute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class b {
    private static final String c = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
    C0003b a;
    private final int b;

    /* renamed from: com.aliyun.security.yunceng.android.sdk.traceroute.b$b  reason: collision with other inner class name */
    public interface C0003b {
        void c(String str);
    }

    public b(int theSendCount, C0003b listener) {
        this.a = listener;
        this.b = theSendCount;
    }

    private String a(a ping, boolean isNeedL) {
        String cmd = "ping -i 0.2 -c ";
        if (isNeedL) {
            cmd = "ping -i 0.2 -s 8185 -c  ";
        }
        Process process = null;
        String out = "";
        BufferedReader reader = null;
        try {
            Process process2 = Runtime.getRuntime().exec(cmd + this.b + " " + ping.a());
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            while (true) {
                String readLine = reader2.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                out = out + line;
            }
            reader2.close();
            process2.waitFor();
            try {
                reader2.close();
                process2.destroy();
            } catch (Exception e) {
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (reader != null) {
                reader.close();
            }
            process.destroy();
        } catch (InterruptedException e3) {
            e3.printStackTrace();
            if (reader != null) {
                reader.close();
            }
            process.destroy();
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e4) {
                    throw th;
                }
            }
            process.destroy();
            throw th;
        }
        return out;
    }

    private String a(String log) {
        Matcher m = Pattern.compile("(?<=\\D)([\\s0-9]+)(?=% packet loss)").matcher(log);
        if (!m.find()) {
            return "100";
        }
        String string = m.group().toString().trim();
        if (m.group().toString().trim().matches("\\d+")) {
            return string;
        }
        return "100";
    }

    private List<String> b(String log) {
        List<String> timeList = new ArrayList<>();
        Matcher m = Pattern.compile("(?<==)([\\.0-9\\s]+)(?=ms)").matcher(log);
        while (m.find()) {
            timeList.add(m.group().toString().trim());
        }
        return timeList;
    }

    private String c(String log) {
        long min = 99999;
        long max = 99999;
        long avg = 99999;
        long sum = 0;
        List<String> timeList = b(log);
        int len = timeList.size();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                int t = Float.valueOf(timeList.get(i)).intValue();
                sum += (long) t;
                if (min > ((long) t)) {
                    min = (long) t;
                }
                if (max < ((long) t)) {
                    max = (long) t;
                }
            }
            avg = sum / ((long) len);
        }
        String loss = a(log);
        return ", \"Ping\":{\"max\":" + max + ", \"min\":" + min + ", \"avg\":" + avg + ", \"loss\":" + loss + "}";
    }

    public void a(String host, boolean isNeedL) {
        C0003b bVar;
        String status = a(new a(host), isNeedL);
        if (Pattern.compile(c).matcher(status).find() && (bVar = this.a) != null) {
            bVar.c(c(status));
        }
    }

    private class a {
        private static final String c = "(?<=\\().*?(?=\\))";
        private String b;

        public String a() {
            return this.b;
        }

        public a(String host) {
            this.b = host;
            Matcher m = Pattern.compile(c).matcher(host);
            if (m.find()) {
                this.b = m.group();
            }
        }
    }
}
