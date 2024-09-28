package im.bclpbkiauv.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.king.zxing.util.CodeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.UByte;

public class Utilities {
    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static Pattern pattern = Pattern.compile("[\\-0-9]+");
    public static volatile DispatchQueue phoneBookQueue = new DispatchQueue("phoneBookQueue");
    public static SecureRandom random = new SecureRandom();
    public static volatile DispatchQueue searchQueue = new DispatchQueue("searchQueue");
    public static volatile DispatchQueue stageQueue = new DispatchQueue("stageQueue");

    public static native void aesCbcEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2, int i3);

    private static native void aesCbcEncryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, int i4);

    public static native void aesCtrDecryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, int i, int i2);

    public static native void aesCtrDecryptionByteArray(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3);

    private static native void aesIgeEncryption(ByteBuffer byteBuffer, byte[] bArr, byte[] bArr2, boolean z, int i, int i2);

    public static native void blurBitmap(Object obj, int i, int i2, int i3, int i4, int i5);

    public static native void calcCDT(ByteBuffer byteBuffer, int i, int i2, ByteBuffer byteBuffer2);

    public static native void clearDir(String str, int i, long j);

    public static native int convertVideoFrame(ByteBuffer byteBuffer, ByteBuffer byteBuffer2, int i, int i2, int i3, int i4, int i5);

    public static native long getDirSize(String str, int i);

    public static native boolean loadWebpImage(Bitmap bitmap, ByteBuffer byteBuffer, int i, BitmapFactory.Options options, boolean z);

    public static native int needInvert(Object obj, int i, int i2, int i3, int i4);

    private static native int pbkdf2(byte[] bArr, byte[] bArr2, byte[] bArr3, int i);

    public static native int pinBitmap(Bitmap bitmap);

    public static native String readlink(String str);

    public static native void stackBlurBitmap(Bitmap bitmap, int i);

    public static native void unpinBitmap(Bitmap bitmap);

    static {
        try {
            FileInputStream sUrandomIn = new FileInputStream(new File("/dev/urandom"));
            byte[] buffer = new byte[1024];
            sUrandomIn.read(buffer);
            sUrandomIn.close();
            random.setSeed(buffer);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Bitmap blurWallpaper(Bitmap src) {
        Bitmap b;
        if (src == null) {
            return null;
        }
        if (src.getHeight() > src.getWidth()) {
            b = Bitmap.createBitmap(Math.round((((float) src.getWidth()) * 450.0f) / ((float) src.getHeight())), CodeUtils.DEFAULT_REQ_WIDTH, Bitmap.Config.ARGB_8888);
        } else {
            b = Bitmap.createBitmap(CodeUtils.DEFAULT_REQ_WIDTH, Math.round((((float) src.getHeight()) * 450.0f) / ((float) src.getWidth())), Bitmap.Config.ARGB_8888);
        }
        Paint paint = new Paint(2);
        new Canvas(b).drawBitmap(src, (Rect) null, new Rect(0, 0, b.getWidth(), b.getHeight()), paint);
        stackBlurBitmap(b, 12);
        return b;
    }

    public static void aesIgeEncryption(ByteBuffer buffer, byte[] key, byte[] iv, boolean encrypt, boolean changeIv, int offset, int length) {
        aesIgeEncryption(buffer, key, changeIv ? iv : (byte[]) iv.clone(), encrypt, offset, length);
    }

    public static void aesCbcEncryptionByteArraySafe(byte[] buffer, byte[] key, byte[] iv, int offset, int length, int n, int encrypt) {
        aesCbcEncryptionByteArray(buffer, key, (byte[]) iv.clone(), offset, length, n, encrypt);
    }

    public static Integer parseInt(CharSequence value) {
        if (value == null) {
            return 0;
        }
        int val = 0;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                val = Integer.parseInt(matcher.group(0));
            }
        } catch (Exception e) {
        }
        return Integer.valueOf(val);
    }

    public static Long parseLong(String value) {
        if (value == null) {
            return 0L;
        }
        long val = 0;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                val = Long.parseLong(matcher.group(0));
            }
        } catch (Exception e) {
        }
        return Long.valueOf(val);
    }

    public static String parseIntToString(String value) {
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            char[] cArr = hexArray;
            hexChars[j * 2] = cArr[v >>> 4];
            hexChars[(j * 2) + 1] = cArr[v & 15];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            return null;
        }
        int len = hex.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean isGoodPrime(byte[] prime, int g) {
        int val;
        if (g < 2 || g > 7 || prime.length != 256 || prime[0] >= 0) {
            return false;
        }
        BigInteger dhBI = new BigInteger(1, prime);
        if (g == 2) {
            if (dhBI.mod(BigInteger.valueOf(8)).intValue() != 7) {
                return false;
            }
        } else if (g == 3) {
            if (dhBI.mod(BigInteger.valueOf(3)).intValue() != 2) {
                return false;
            }
        } else if (g == 5) {
            int val2 = dhBI.mod(BigInteger.valueOf(5)).intValue();
            if (!(val2 == 1 || val2 == 4)) {
                return false;
            }
        } else if (g == 6) {
            int val3 = dhBI.mod(BigInteger.valueOf(24)).intValue();
            if (!(val3 == 19 || val3 == 23)) {
                return false;
            }
        } else if (!(g != 7 || (val = dhBI.mod(BigInteger.valueOf(7)).intValue()) == 3 || val == 5 || val == 6)) {
            return false;
        }
        if (bytesToHex(prime).equals("C71CAEB9C6B1C9048E6C522F70F13F73980D40238E3E21C14934D037563D930F48198A0AA7C14058229493D22530F4DBFA336F6E0AC925139543AED44CCE7C3720FD51F69458705AC68CD4FE6B6B13ABDC9746512969328454F18FAF8C595F642477FE96BB2A941D5BCD1D4AC8CC49880708FA9B378E3C4F3A9060BEE67CF9A4A4A695811051907E162753B56B0F6B410DBA74D8A84B2A14B3144E0EF1284754FD17ED950D5965B4B9DD46582DB1178D169C6BC465B0D6FF9CA3928FEF5B9AE4E418FC15E83EBEA0F87FA9FF5EED70050DED2849F47BF959D956850CE929851F0D8115F635B105EE2E4E15D04B2454BF6F4FADF034B10403119CD8E3B92FCC5B")) {
            return true;
        }
        BigInteger dhBI2 = dhBI.subtract(BigInteger.valueOf(1)).divide(BigInteger.valueOf(2));
        if (!dhBI.isProbablePrime(30) || !dhBI2.isProbablePrime(30)) {
            return false;
        }
        return true;
    }

    public static boolean isGoodGaAndGb(BigInteger g_a, BigInteger p) {
        return g_a.compareTo(BigInteger.valueOf(1)) > 0 && g_a.compareTo(p.subtract(BigInteger.valueOf(1))) < 0;
    }

    public static boolean arraysEquals(byte[] arr1, int offset1, byte[] arr2, int offset2) {
        if (arr1 == null || arr2 == null || offset1 < 0 || offset2 < 0 || arr1.length - offset1 > arr2.length - offset2 || arr1.length - offset1 < 0 || arr2.length - offset2 < 0) {
            return false;
        }
        boolean result = true;
        for (int a = offset1; a < arr1.length; a++) {
            if (arr1[a + offset1] != arr2[a + offset2]) {
                result = false;
            }
        }
        return result;
    }

    public static byte[] computeSHA1(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[20];
        }
    }

    /* JADX INFO: finally extract failed */
    public static byte[] computeSHA1(ByteBuffer convertme, int offset, int len) {
        int oldp = convertme.position();
        int oldl = convertme.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            convertme.position(offset);
            convertme.limit(len);
            md.update(convertme);
            byte[] digest = md.digest();
            convertme.limit(oldl);
            convertme.position(oldp);
            return digest;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            convertme.limit(oldl);
            convertme.position(oldp);
            return new byte[20];
        } catch (Throwable th) {
            convertme.limit(oldl);
            convertme.position(oldp);
            throw th;
        }
    }

    public static byte[] computeSHA1(ByteBuffer convertme) {
        return computeSHA1(convertme, 0, convertme.limit());
    }

    public static byte[] computeSHA1(byte[] convertme) {
        return computeSHA1(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme) {
        return computeSHA256(convertme, 0, convertme.length);
    }

    public static byte[] computeSHA256(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA256(byte[]... args) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (int a = 0; a < args.length; a++) {
                md.update(args[a], 0, args[a].length);
            }
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[32];
        }
    }

    public static byte[] computeSHA512(byte[] convertme) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    public static byte[] computeSHA512(byte[] convertme, byte[] convertme2) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            md.update(convertme2, 0, convertme2.length);
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    public static byte[] computePBKDF2(byte[] password, byte[] salt) {
        byte[] dst = new byte[64];
        pbkdf2(password, salt, dst, DefaultOggSeeker.MATCH_BYTE_RANGE);
        return dst;
    }

    public static byte[] computeSHA512(byte[] convertme, byte[] convertme2, byte[] convertme3) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(convertme, 0, convertme.length);
            md.update(convertme2, 0, convertme2.length);
            md.update(convertme3, 0, convertme3.length);
            return md.digest();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new byte[64];
        }
    }

    /* JADX INFO: finally extract failed */
    public static byte[] computeSHA256(byte[] b1, int o1, int l1, ByteBuffer b2, int o2, int l2) {
        int oldp = b2.position();
        int oldl = b2.limit();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(b1, o1, l1);
            b2.position(o2);
            b2.limit(l2);
            md.update(b2);
            byte[] digest = md.digest();
            b2.limit(oldl);
            b2.position(oldp);
            return digest;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            b2.limit(oldl);
            b2.position(oldp);
            return new byte[32];
        } catch (Throwable th) {
            b2.limit(oldl);
            b2.position(oldp);
            throw th;
        }
    }

    public static long bytesToLong(byte[] bytes) {
        return (((long) bytes[7]) << 56) + ((((long) bytes[6]) & 255) << 48) + ((((long) bytes[5]) & 255) << 40) + ((((long) bytes[4]) & 255) << 32) + ((((long) bytes[3]) & 255) << 24) + ((((long) bytes[2]) & 255) << 16) + ((((long) bytes[1]) & 255) << 8) + (((long) bytes[0]) & 255);
    }

    public static int bytesToInt(byte[] bytes) {
        return ((bytes[3] & UByte.MAX_VALUE) << 24) + ((bytes[2] & UByte.MAX_VALUE) << 16) + ((bytes[1] & UByte.MAX_VALUE) << 8) + (bytes[0] & UByte.MAX_VALUE);
    }

    public static String MD5(String md5) {
        if (md5 == null) {
            return null;
        }
        try {
            byte[] array = MessageDigest.getInstance("MD5").digest(AndroidUtilities.getStringBytes(md5));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & UByte.MAX_VALUE) | UByte.MIN_VALUE).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }
}
