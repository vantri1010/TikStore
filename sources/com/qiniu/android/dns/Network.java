package com.qiniu.android.dns;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public final class Network {
    private static String previousIp = "";

    public static String getIp() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("114.114.114.114"), 53);
            InetAddress local = socket.getLocalAddress();
            socket.close();
            return local.getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static synchronized boolean isNetworkChanged() {
        synchronized (Network.class) {
            String nowIp = getIp();
            if (nowIp.equals(previousIp)) {
                return false;
            }
            previousIp = nowIp;
            return true;
        }
    }
}
