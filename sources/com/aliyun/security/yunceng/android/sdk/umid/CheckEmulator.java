package com.aliyun.security.yunceng.android.sdk.umid;

import java.io.File;
import java.io.FileInputStream;

public class CheckEmulator {
    private native int detect_cache_raw();

    public int a() {
        try {
            return detect_cache_raw();
        } catch (Exception e) {
            return 1000;
        }
    }

    private boolean b() {
        File cpuInfo = new File("/proc/cpuinfo");
        if (!cpuInfo.exists()) {
            return false;
        }
        try {
            FileInputStream in = new FileInputStream(cpuInfo);
            byte[] bytes = new byte[512];
            int count = in.read(bytes);
            in.close();
            if (count != 0) {
                return new String(bytes).contains("arch64");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean c() {
        byte[] header;
        byte[] header2;
        File libcFile = new File("/system/lib/libc.so");
        if (libcFile.exists() && (header2 = a(libcFile)) != null && header2[4] == 2) {
            return true;
        }
        File libcFile64 = new File("/system/lib64/libc.so");
        if (!libcFile64.exists() || (header = a(libcFile64)) == null || header[4] != 2) {
            return false;
        }
        return true;
    }

    private static byte[] a(File libFile) {
        if (libFile != null && libFile.exists()) {
            try {
                FileInputStream in = new FileInputStream(libFile);
                byte[] bytes = new byte[16];
                int count = in.read(bytes);
                in.close();
                if (bytes[0] == Byte.MAX_VALUE && bytes[1] == 69 && bytes[2] == 76 && bytes[3] == 70 && count == 16) {
                    return bytes;
                }
                return null;
            } catch (Exception e) {
            }
        }
        return null;
    }

    static {
        System.loadLibrary("yunceng");
    }
}
