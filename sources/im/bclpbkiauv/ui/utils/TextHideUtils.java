package im.bclpbkiauv.ui.utils;

import android.text.TextUtils;

public class TextHideUtils {
    public static String hide(String src, String replacement, int begin, int end) {
        try {
            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(src)) {
                if (src.length() == 1 && begin == 0 && end >= 0) {
                    sb.append(src.replaceAll(".", replacement));
                } else {
                    String preStr = src.substring(0, begin);
                    String midStr = src.substring(begin, end);
                    String sufStr = src.substring(end);
                    if (!TextUtils.isEmpty(preStr)) {
                        sb.append(preStr);
                    }
                    if (!TextUtils.isEmpty(midStr)) {
                        sb.append(midStr.replaceAll(".", replacement));
                    }
                    if (!TextUtils.isEmpty(sufStr)) {
                        sb.append(sufStr);
                    }
                }
            }
            return sb.toString();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hide(String src, int begin, int end) {
        return hide(src, "*", begin, end);
    }

    public static String hideAll(String src, String replacement) {
        return hide(src, replacement, 0, src.length());
    }

    public static String hideAll(String src) {
        return hideAll(src, "*");
    }
}
