package com.qiniu.android.dns.util;

import java.io.IOException;

public final class Hex {
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static byte[] decodeHex(char[] data) throws HexDecodeException {
        int len = data.length;
        if ((len & 1) == 0) {
            byte[] out = new byte[(len >> 1)];
            int i = 0;
            int j = 0;
            while (j < len) {
                int j2 = j + 1;
                j = j2 + 1;
                out[i] = (byte) (((toDigit(data[j], j) << 4) | toDigit(data[j2], j2)) & 255);
                i++;
            }
            return out;
        }
        throw new HexDecodeException("Odd number of characters.");
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[(l << 1)];
        int j = 0;
        for (int i = 0; i < l; i++) {
            int j2 = j + 1;
            out[j] = toDigits[(data[i] & 240) >>> 4];
            j = j2 + 1;
            out[j2] = toDigits[data[i] & 15];
        }
        return out;
    }

    public static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    protected static int toDigit(char ch, int index) throws HexDecodeException {
        int digit = Character.digit(ch, 16);
        if (digit != -1) {
            return digit;
        }
        throw new HexDecodeException("Illegal hexadecimal character " + ch + " at index " + index);
    }

    public static class HexDecodeException extends IOException {
        public HexDecodeException(String msg) {
            super(msg);
        }
    }
}
