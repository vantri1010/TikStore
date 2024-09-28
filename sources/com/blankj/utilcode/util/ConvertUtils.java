package com.blankj.utilcode.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.blankj.utilcode.constant.TimeConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import kotlin.UByte;

public final class ConvertUtils {
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String bytes2Bits(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            for (int j = 7; j >= 0; j--) {
                sb.append(((aByte >> j) & 1) == 0 ? '0' : '1');
            }
        }
        return sb.toString();
    }

    public static byte[] bits2Bytes(String bits) {
        int lenMod = bits.length() % 8;
        int byteLen = bits.length() / 8;
        if (lenMod != 0) {
            for (int i = lenMod; i < 8; i++) {
                bits = "0" + bits;
            }
            byteLen++;
        }
        byte[] bytes = new byte[byteLen];
        for (int i2 = 0; i2 < byteLen; i2++) {
            for (int j = 0; j < 8; j++) {
                bytes[i2] = (byte) (bytes[i2] << 1);
                bytes[i2] = (byte) (bytes[i2] | (bits.charAt((i2 * 8) + j) - '0'));
            }
        }
        return bytes;
    }

    public static char[] bytes2Chars(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return null;
        }
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & UByte.MAX_VALUE);
        }
        return chars;
    }

    public static byte[] chars2Bytes(char[] chars) {
        if (chars == null || chars.length <= 0) {
            return null;
        }
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static String bytes2HexString(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return "";
        }
        char[] ret = new char[(len << 1)];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int j2 = j + 1;
            char[] cArr = hexDigits;
            ret[j] = cArr[(bytes[i] >> 4) & 15];
            j = j2 + 1;
            ret[j2] = cArr[bytes[i] & 15];
        }
        return new String(ret);
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (isSpace(hexString)) {
            return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len++;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[(len >> 1)];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) ((hex2Int(hexBytes[i]) << 4) | hex2Int(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Int(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        }
        if (hexChar >= 'A' && hexChar <= 'F') {
            return (hexChar - 'A') + 10;
        }
        throw new IllegalArgumentException();
    }

    public static long memorySize2Byte(long memorySize, int unit) {
        if (memorySize < 0) {
            return -1;
        }
        return ((long) unit) * memorySize;
    }

    public static double byte2MemorySize(long byteSize, int unit) {
        if (byteSize < 0) {
            return -1.0d;
        }
        return ((double) byteSize) / ((double) unit);
    }

    public static String byte2FitMemorySize(long byteSize) {
        if (byteSize < 0) {
            return "shouldn't be less than zero!";
        }
        if (byteSize < 1024) {
            return String.format("%.3fB", new Object[]{Double.valueOf((double) byteSize)});
        } else if (byteSize < 1048576) {
            return String.format("%.3fKB", new Object[]{Double.valueOf(((double) byteSize) / 1024.0d)});
        } else if (byteSize < 1073741824) {
            return String.format("%.3fMB", new Object[]{Double.valueOf(((double) byteSize) / 1048576.0d)});
        } else {
            return String.format("%.3fGB", new Object[]{Double.valueOf(((double) byteSize) / 1.073741824E9d)});
        }
    }

    public static long timeSpan2Millis(long timeSpan, int unit) {
        return ((long) unit) * timeSpan;
    }

    public static long millis2TimeSpan(long millis, int unit) {
        return millis / ((long) unit);
    }

    public static String millis2FitTimeSpan(long millis, int precision) {
        if (millis <= 0 || precision <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        int[] unitLen = {TimeConstants.DAY, TimeConstants.HOUR, TimeConstants.MIN, 1000, 1};
        int precision2 = Math.min(precision, 5);
        for (int i = 0; i < precision2; i++) {
            if (millis >= ((long) unitLen[i])) {
                long mode = millis / ((long) unitLen[i]);
                millis -= ((long) unitLen[i]) * mode;
                sb.append(mode);
                sb.append(units[i]);
            }
        }
        return sb.toString();
    }

    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            while (true) {
                int read = is.read(b, 0, 1024);
                int len = read;
                if (read != -1) {
                    os.write(b, 0, len);
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            is.close();
            return os;
        } catch (IOException e2) {
            e2.printStackTrace();
            try {
                is.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            return null;
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    public ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    public static byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            return null;
        }
        return input2OutputStream(is).toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            return null;
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            os.write(bytes);
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return os;
        } catch (IOException e2) {
            e2.printStackTrace();
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public static String inputStream2String(InputStream is, String charsetName) {
        if (is == null || isSpace(charsetName)) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = input2OutputStream(is);
            if (baos == null) {
                return "";
            }
            return baos.toString(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static InputStream string2InputStream(String string, String charsetName) {
        if (string == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null || isSpace(charsetName)) {
            return "";
        }
        try {
            return new String(outputStream2Bytes(out), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (string == null || isSpace(charsetName)) {
            return null;
        }
        try {
            return bytes2OutputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytes2Bitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(Utils.getApp().getResources(), bitmap);
    }

    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        if (drawable == null) {
            return null;
        }
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    public static Drawable bytes2Drawable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    public static Bitmap view2Bitmap(View view) {
        Bitmap bitmap;
        if (view == null) {
            return null;
        }
        boolean drawingCacheEnabled = view.isDrawingCacheEnabled();
        boolean willNotCacheDrawing = view.willNotCacheDrawing();
        view.setDrawingCacheEnabled(true);
        view.setWillNotCacheDrawing(false);
        Bitmap drawingCache = view.getDrawingCache();
        if (drawingCache == null) {
            view.layout(0, 0, view.getWidth(), view.getHeight());
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
        } else {
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCacheDrawing);
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        return bitmap;
    }

    public static int dp2px(float dpValue) {
        return (int) ((dpValue * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dp(float pxValue) {
        return (int) ((pxValue / Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(float spValue) {
        return (int) ((spValue * Resources.getSystem().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return (int) ((pxValue / Resources.getSystem().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
