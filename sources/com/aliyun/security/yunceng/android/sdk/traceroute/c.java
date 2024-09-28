package com.aliyun.security.yunceng.android.sdk.traceroute;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class c {
    private static final int c = 4;
    public InetAddress a;
    private a b;
    private int d = 6000;
    private final long[] e = new long[4];

    public interface a {
        void b(String str);
    }

    public c(InetAddress remote, a listener) {
        this.a = remote;
        this.b = listener;
    }

    public boolean a(String host, String port) {
        InetAddress inetAddress = this.a;
        if (inetAddress == null || !a(inetAddress, port)) {
            return false;
        }
        return true;
    }

    private boolean a(InetAddress inetAddress, String port) {
        InetAddress inetAddress2 = inetAddress;
        boolean isConnected = true;
        if (inetAddress2 == null || port == null) {
            return false;
        }
        InetSocketAddress socketAddress = new InetSocketAddress(inetAddress2, Integer.valueOf(port).intValue());
        int flag = 0;
        this.b.b(", \"TcpCheck\":{");
        int i = 0;
        while (true) {
            if (i >= 4) {
                break;
            }
            a(socketAddress, this.d, i);
            long[] jArr = this.e;
            if (jArr[i] != -1) {
                if (jArr[i] == -2 && i > 0 && jArr[i - 1] == -2) {
                    flag = -2;
                    break;
                }
            } else {
                this.d += 4000;
                if (i > 0 && jArr[i - 1] == -1) {
                    flag = -1;
                    break;
                }
            }
            i++;
        }
        long _min = 99999;
        long _max = 99999;
        long time = 99999;
        int count = 0;
        if (flag == -1) {
            isConnected = false;
        } else if (flag == -2) {
            isConnected = false;
        } else {
            long[] jArr2 = this.e;
            _min = jArr2[0];
            _max = jArr2[0];
            time = 0;
            for (int i2 = 0; i2 < 4; i2++) {
                long[] jArr3 = this.e;
                if (_min > jArr3[i2]) {
                    _min = jArr3[i2];
                }
                long[] jArr4 = this.e;
                if (_max < jArr4[i2]) {
                    _max = jArr4[i2];
                }
                long[] jArr5 = this.e;
                if (jArr5[i2] > 0) {
                    time += jArr5[i2];
                    count++;
                }
            }
            if (count > 0) {
                time /= (long) count;
            }
        }
        this.b.b("\"max\":" + _max);
        this.b.b(", \"min\":" + _min);
        this.b.b(", \"avg\":" + time);
        this.b.b("}");
        return isConnected;
    }

    private void a(InetSocketAddress socketAddress, int timeOut, int index) {
        Socket socket = null;
        try {
            socket = new Socket();
            long start = System.currentTimeMillis();
            socket.connect(socketAddress, timeOut);
            this.e[index] = System.currentTimeMillis() - start;
            try {
                socket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        } catch (SocketTimeoutException e3) {
            this.e[index] = -1;
            e3.printStackTrace();
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e4) {
            this.e[index] = -2;
            e4.printStackTrace();
            if (socket != null) {
                socket.close();
            }
        } catch (Throwable th) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }
}
