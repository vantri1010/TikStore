package com.alivc.rtc.device.utils;

public class IntUtils {
    public static byte[] getBytes(int i) {
        byte[] bInt = new byte[4];
        bInt[3] = (byte) (i % 256);
        int value = i >> 8;
        bInt[2] = (byte) (value % 256);
        int value2 = value >> 8;
        bInt[1] = (byte) (value2 % 256);
        bInt[0] = (byte) ((value2 >> 8) % 256);
        return bInt;
    }

    public static byte[] getBytes(byte[] buffer, int i) {
        if (buffer.length != 4) {
            return null;
        }
        buffer[3] = (byte) (i % 256);
        int value = i >> 8;
        buffer[2] = (byte) (value % 256);
        int value2 = value >> 8;
        buffer[1] = (byte) (value2 % 256);
        buffer[0] = (byte) ((value2 >> 8) % 256);
        return buffer;
    }
}
