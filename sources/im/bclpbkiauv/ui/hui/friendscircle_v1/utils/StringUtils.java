package im.bclpbkiauv.ui.hui.friendscircle_v1.utils;

import android.text.TextUtils;

public class StringUtils {
    public static String handleTextName(String str, int maxLen) {
        int maxLen2 = maxLen + 1;
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count++;
            } else {
                count += 2;
            }
            if (maxLen2 == count || (item >= 128 && maxLen2 + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen2) {
            return str;
        }
        return str.substring(0, endIndex) + "...";
    }
}
