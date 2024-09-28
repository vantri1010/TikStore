package com.aliyun.security.yunceng.android.sdk.traceroute;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class d {
    public static final String a = "UNKNOWN";
    public static final String b = "WAP";
    public static final String c = "WIFI";

    public static String a(Context context) {
        if (context == null) {
            return b;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        if (manager == null) {
            return "ConnectivityManager not found";
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return a;
        }
        String type = networkInfo.getTypeName();
        if (type.equalsIgnoreCase(c)) {
            return c;
        }
        if (!type.equalsIgnoreCase("MOBILE")) {
            return null;
        }
        if (TextUtils.isEmpty(Proxy.getDefaultHost())) {
            return f(context);
        }
        return b;
    }

    public static Boolean b(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public static String c(Context context) {
        TelephonyManager telManager;
        String operator;
        if (!(context == null || (telManager = (TelephonyManager) context.getSystemService("phone")) == null || (operator = telManager.getSimOperator()) == null)) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                return "中国移动";
            }
            if (operator.equals("46001")) {
                return "中国联通";
            }
            if (operator.equals("46003")) {
                return "中国电信";
            }
        }
        return "未知运营商";
    }

    public static String d(Context context) {
        if (context == null) {
            return "127.0.0.1";
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager == null) {
            return "wifiManager not found";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return "wifiInfo not found";
        }
        int ipAddress = wifiInfo.getIpAddress();
        return String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)});
    }

    public static String a() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses();
                while (true) {
                    if (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String e(Context context) {
        if (context == null) {
            return "127.0.0.1";
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (wifiManager == null) {
            return "wifiManager not found";
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null) {
            return null;
        }
        int tmp = dhcpInfo.gateway;
        return String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(tmp & 255), Integer.valueOf((tmp >> 8) & 255), Integer.valueOf((tmp >> 16) & 255), Integer.valueOf((tmp >> 24) & 255)});
    }

    public static String a(String dns) {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            Process process2 = Runtime.getRuntime().exec("getprop net." + dns);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            while (true) {
                String readLine = reader2.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                str = str + line;
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
        return str.trim();
    }

    public static InetAddress b(String _dormain) {
        InetAddress remoteInet = null;
        try {
            remoteInet = InetAddress.getByName(_dormain);
            if (remoteInet == null || !remoteInet.getHostAddress().equals("0.0.0.0")) {
                return remoteInet;
            }
            return null;
        } catch (UnknownHostException e) {
        }
    }

    private static String f(Context context) {
        if (context == null) {
            return a;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return "TM==null";
        }
        switch (telephonyManager.getNetworkType()) {
            case 0:
                return a;
            case 1:
                return "2G";
            case 2:
                return "2G";
            case 3:
                return "3G";
            case 4:
                return "2G";
            case 5:
                return "3G";
            case 6:
                return "3G";
            case 7:
                return "2G";
            case 8:
                return "3G";
            case 9:
                return "3G";
            case 10:
                return "3G";
            case 11:
                return "2G";
            case 12:
                return "3G";
            case 14:
                return "3G";
            case 15:
                return "3G";
            default:
                return "4G";
        }
    }

    public static String a(InputStream is) {
        byte[] bytes = new byte[1024];
        String res = "";
        while (true) {
            try {
                int read = is.read(bytes);
                int len = read;
                if (read == -1) {
                    break;
                }
                res = res + new String(bytes, 0, len, "gbk");
            } catch (IOException e) {
                e.printStackTrace();
                if (is != null) {
                    is.close();
                }
            } catch (Throwable th) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                throw th;
            }
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
        return res;
    }
}
