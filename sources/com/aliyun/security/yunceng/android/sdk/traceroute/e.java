package com.aliyun.security.yunceng.android.sdk.traceroute;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class e {
    public static String a(String host, String log) {
        StringBuilder logRes = new StringBuilder();
        if (log.contains("timeout")) {
            logRes.append("ping: cannot resolve " + host + ": Timeout");
        } else if (log.contains("unknown")) {
            logRes.append("ping: cannot resolve " + host + ": Unknown host");
        } else {
            a(log, logRes);
        }
        return logRes.toString();
    }

    public static void a(String log, StringBuilder logRes) {
        String hostIp = d(log);
        List<String> bytesList = b(log);
        List<String> ttlList = c(log);
        List<String> timeList = a(log);
        List<String> icmpList = e(log);
        int len = timeList.size();
        for (int i = 0; i < len - 1; i++) {
            logRes.append(bytesList.get(i) + "bytes from " + hostIp + ": icmp_seq=#" + icmpList.get(i) + " ttl=" + ttlList.get(i) + " time=" + timeList.get(i) + "ms" + "\n");
        }
        logRes.append(bytesList.get(len - 1) + "bytes from " + hostIp + ": icmp_seq=#" + icmpList.get(len - 1) + " ttl=" + ttlList.get(len - 1) + " time=" + timeList.get(len - 1) + "ms");
    }

    private static List<String> a(String log) {
        List<String> timeList = new ArrayList<>();
        Matcher m = Pattern.compile("(?<==)([\\.0-9\\s]+)(?=ms)").matcher(log);
        while (m.find()) {
            timeList.add(m.group().toString().trim());
        }
        return timeList;
    }

    private static List<String> b(String log) {
        List<String> bytesList = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=\\D)([\\s0-9]+)(?=bytes)").matcher(log);
        while (m.find()) {
            String string = m.group().toString().trim();
            if (m.group().toString().trim().matches("\\d+")) {
                bytesList.add(string);
            }
        }
        return bytesList;
    }

    private static List<String> c(String log) {
        List<String> ttlList = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=ttl=)([0-9]+)(?=\\s)").matcher(log);
        while (m.find()) {
            ttlList.add(m.group().toString().trim());
        }
        return ttlList;
    }

    private static String d(String log) {
        String hostIp = null;
        Matcher m = Pattern.compile("(?<=\\()([\\d]+\\.)+[\\d]+(?=\\))").matcher(log);
        while (m.find()) {
            hostIp = m.group().toString().trim();
        }
        return hostIp;
    }

    private static List<String> e(String log) {
        List<String> icmpList = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=icmp_seq=)([0-9]+)(?=\\s)").matcher(log);
        while (m.find()) {
            icmpList.add(m.group().toString().trim());
        }
        return icmpList;
    }
}
