package com.blankj.utilcode.util;

import android.os.Build;
import android.text.Html;
import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public final class EncodeUtils {
    private EncodeUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String urlEncode(String input) {
        return urlEncode(input, "UTF-8");
    }

    public static String urlEncode(String input, String charsetName) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            return URLEncoder.encode(input, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String urlDecode(String input) {
        return urlDecode(input, "UTF-8");
    }

    public static String urlDecode(String input, String charsetName) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            return URLDecoder.decode(input.replaceAll("%(?![0-9a-fA-F]{2})", "%25").replaceAll("\\+", "%2B"), charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] base64Encode(String input) {
        return base64Encode(input.getBytes());
    }

    public static byte[] base64Encode(byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        return Base64.encode(input, 2);
    }

    public static String base64Encode2String(byte[] input) {
        if (input == null || input.length == 0) {
            return "";
        }
        return Base64.encodeToString(input, 2);
    }

    public static byte[] base64Decode(String input) {
        if (input == null || input.length() == 0) {
            return new byte[0];
        }
        return Base64.decode(input, 2);
    }

    public static byte[] base64Decode(byte[] input) {
        if (input == null || input.length == 0) {
            return new byte[0];
        }
        return Base64.decode(input, 2);
    }

    public static String htmlEncode(CharSequence input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == '\"') {
                sb.append("&quot;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c != '\'') {
                sb.append(c);
            } else {
                sb.append("&#39;");
            }
        }
        return sb.toString();
    }

    public static CharSequence htmlDecode(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(input, 0);
        }
        return Html.fromHtml(input);
    }

    public static String binEncode(String input) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char i : input.toCharArray()) {
            stringBuilder.append(Integer.toBinaryString(i));
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public static String binDecode(String input) {
        String[] splitted = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String i : splitted) {
            sb.append((char) Integer.parseInt(i.replace(" ", ""), 2));
        }
        return sb.toString();
    }
}
