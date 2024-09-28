package im.bclpbkiauv.messenger.utils;

import java.util.regex.Pattern;

public class RegexUtils {
    private static Pattern ptUrl = Pattern.compile(urlStr);
    private static final String urlStr = "(((https|http)?://)?([a-z0-9]+[.])|(www.))\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;一-龥]]+)+)?([.][a-z0-9]{0,}+|/?)";

    public static boolean firstLetterIsEnglishLetter(CharSequence chars) {
        if (chars == null || chars.length() <= 0) {
            return false;
        }
        Pattern compile = Pattern.compile("([a-zA-Z])");
        return compile.matcher(chars.charAt(0) + "").matches();
    }

    public static boolean hasLetterAndNumber(CharSequence str, boolean allowUnderLine) {
        boolean hasDigit = false;
        boolean hasLetter = false;
        for (int i = 0; i < str.length(); i++) {
            if (!hasDigit && Character.isDigit(str.charAt(i))) {
                hasDigit = true;
            }
            if (!hasLetter && Character.isLetter(str.charAt(i))) {
                hasLetter = true;
            }
            if (hasDigit && hasLetter) {
                break;
            }
        }
        return hasDigit && hasLetter && Pattern.matches(allowUnderLine ? "^[a-zA-Z0-9_]+$" : "^[a-zA-Z0-9]+$", str);
    }

    public static boolean hasLink(String str) {
        if (str == null) {
            return false;
        }
        int count = 0;
        while (ptUrl.matcher(str).find()) {
            count++;
        }
        if (count != 0) {
            return true;
        }
        return false;
    }
}
