package im.bclpbkiauv.ui.hui.friendscircle.okhttphelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    protected static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest;

    static {
        messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            PrintStream printStream = System.err;
            printStream.println(MD5Utils.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        return getMD5String(password).equals(md5PwdStr);
    }

    public static String getFileMD5String(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        while (true) {
            int read = fis.read(buffer);
            int numRead = read;
            if (read > 0) {
                messagedigest.update(buffer, 0, numRead);
            } else {
                fis.close();
                return bufferToHex(messagedigest.digest());
            }
        }
    }

    public static String getFileMD5String_old(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        messagedigest.update(in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
        in.close();
        return bufferToHex(messagedigest.digest());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(n * 2);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char[] cArr = hexDigits;
        char c0 = cArr[(bt & 240) >> 4];
        char c1 = cArr[bt & 15];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static void main(String[] args) throws IOException {
        long begin = System.currentTimeMillis();
        File file = new File("C:/test.txt");
        if (!file.exists()) {
            System.out.println("不存在");
        }
        String md5 = getFileMD5String(file);
        long end = System.currentTimeMillis();
        PrintStream printStream = System.out;
        printStream.println("md5:" + md5 + " time:" + ((end - begin) / 1000) + "s");
        System.out.println(file.getPath());
    }
}
