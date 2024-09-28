package com.socks.library.klog;

import android.util.Log;

public class BaseLog {
    public static void printDefault(int type, String tag, String msg) {
        int index = 0;
        int countOfSub = msg.length() / 4000;
        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                printSub(type, tag, msg.substring(index, index + 4000));
                index += 4000;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
            return;
        }
        printSub(type, tag, msg);
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case 1:
                Log.v(tag, sub);
                return;
            case 2:
                Log.d(tag, sub);
                return;
            case 3:
                Log.i(tag, sub);
                return;
            case 4:
                Log.w(tag, sub);
                return;
            case 5:
                Log.e(tag, sub);
                return;
            case 6:
                Log.wtf(tag, sub);
                return;
            default:
                return;
        }
    }
}
