package im.bclpbkiauv.ui.hui.friendscircle.okhttphelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

public class EncodeUtils {
    public static String byte2Hex(byte[] enc0) {
        String str;
        StringBuilder sb = new StringBuilder("");
        for (byte b : enc0) {
            String strHex = Integer.toHexString(b & UByte.MAX_VALUE);
            if (strHex.length() == 1) {
                str = "0" + strHex;
            } else {
                str = strHex;
            }
            sb.append(str);
        }
        return sb.toString().trim();
    }

    public static byte[] hex2Byte(String hex) {
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            int m = (i * 2) + 1;
            ret[i] = Byte.valueOf((byte) Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, m + 1)).intValue()).byteValue();
        }
        return ret;
    }

    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        String result = null;
        try {
            InputStream is2 = new FileInputStream(path);
            byte[] data = new byte[is2.available()];
            is2.read(data);
            result = Base64.encodeToString(data, 0);
            try {
                is2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (is != null) {
                is.close();
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        return result;
    }

    public static Bitmap stringtoBitmap(String strBase64) {
        String strTmp;
        if (strBase64.startsWith("data:image")) {
            strTmp = strBase64.substring(strBase64.indexOf("base64,") + 7);
        } else {
            strTmp = strBase64;
        }
        try {
            byte[] bitmapArray = Base64.decode(strTmp, 0);
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
