package com.alivc.component.capture;

public class JNIUtils {
    public static int AudioFormatFromNative(int format) {
        if (format == 1) {
            return 2;
        }
        if (format == 0) {
            return 3;
        }
        if (format == 7) {
            return 4;
        }
        return 2;
    }

    public static int AudioChannelFromNative(int channel) {
        if (channel == 2) {
            return 12;
        }
        return 16;
    }

    public static int AudioFormatToNative(int format) {
        if (format == 2) {
            return 1;
        }
        if (format == 3) {
            return 0;
        }
        if (format == 4) {
            return 7;
        }
        return 1;
    }

    public static int AudioChannelToNative(int channel) {
        if (channel == 12) {
            return 2;
        }
        return 1;
    }

    static int VideoFormatFromNative(int format) {
        if (format == 7) {
            return 17;
        }
        if (format == 5) {
            return 35;
        }
        if (format == 12) {
            return 40;
        }
        return 17;
    }

    public static int VideoFormatToNative(int format) {
        if (format == 17) {
            return 7;
        }
        if (format == 35) {
            return 5;
        }
        if (format == 40) {
            return 12;
        }
        return 7;
    }
}
