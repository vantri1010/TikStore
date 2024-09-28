package im.bclpbkiauv.ui.utils;

import android.text.TextUtils;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import kotlin.UByte;

public class ChiperUtils {
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        try {
            String result = "";
            for (byte b : MessageDigest.getInstance("MD5").digest(string.getBytes())) {
                String temp = Integer.toHexString(b & UByte.MAX_VALUE);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result = result + temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encrypt(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        try {
            return new String(Base64.encode(encryptInternal(text), 0)).replaceAll("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] encryptInternal(String text) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec("Wu^tdJ92hFBqb7kz".getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("0392039203920300".getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, secretKeySpec, ivSpec);
        return cipher.doFinal(text.getBytes("UTF-8"));
    }

    public static String decrypt(String content) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }
        try {
            return new String(decryptInternal(content), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] decryptInternal(String content) throws Exception {
        byte[] byteMi = Base64.decode(content, 0);
        SecretKeySpec secretKeySpec = new SecretKeySpec("Wu^tdJ92hFBqb7kz".getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("0392039203920300".getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, ivSpec);
        return cipher.doFinal(byteMi);
    }

    public static byte[] decryptYunceng(String content) throws Exception {
        byte[] byteMi = Base64.decode(content, 0);
        SecretKeySpec secretKeySpec = new SecretKeySpec("2018201820182018".getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("1234567887654321".getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, secretKeySpec, ivSpec);
        return cipher.doFinal(byteMi);
    }
}
