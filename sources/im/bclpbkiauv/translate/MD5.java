package im.bclpbkiauv.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String md5(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input.getBytes("utf-8"));
            return byteArrayToHex(messageDigest.digest());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String md5(File file) {
        try {
            if (!file.isFile()) {
                PrintStream printStream = System.err;
                printStream.println("文件" + file.getAbsolutePath() + "不存在或者不是文件");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            String result = md5((InputStream) in);
            in.close();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String md5(InputStream in) {
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            while (true) {
                int read = in.read(buffer);
                int read2 = read;
                if (read != -1) {
                    messagedigest.update(buffer, 0, read2);
                } else {
                    in.close();
                    return byteArrayToHex(messagedigest.digest());
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            return null;
        }
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] resultCharArray = new char[(byteArray.length * 2)];
        int index = 0;
        for (byte b : byteArray) {
            int index2 = index + 1;
            char[] cArr = hexDigits;
            resultCharArray[index] = cArr[(b >>> 4) & 15];
            index = index2 + 1;
            resultCharArray[index2] = cArr[b & 15];
        }
        return new String(resultCharArray);
    }
}
