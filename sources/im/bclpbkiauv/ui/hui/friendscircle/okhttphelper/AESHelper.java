package im.bclpbkiauv.ui.hui.friendscircle.okhttphelper;

import java.io.PrintStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {
    final String KEY_ALGORITHM = "AES";
    final String algorithmStr = "AES/CBC/PKCS7Padding";
    private Cipher cipher;
    byte[] iv;
    private Key key;

    public byte[] PKCS7Padding(byte[] inputByte) throws Exception {
        try {
            int length = inputByte.length;
            int leftLength = 16 - (length % 16 == 0 ? 16 : length % 16);
            byte[] arrayReturn = new byte[(length + leftLength)];
            byte[] plusbyte = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
            if (leftLength > 0) {
                for (int i = 0; i < length + leftLength; i++) {
                    if (i < length) {
                        arrayReturn[i] = inputByte[i];
                    } else {
                        arrayReturn[i] = plusbyte[leftLength];
                    }
                }
                this.iv = arrayReturn;
                return arrayReturn;
            }
            this.iv = arrayReturn;
            return inputByte;
        } catch (Exception e) {
            throw new Exception("数据异常，PKCS5Padding填充模式错误，异常抛出！" + e.getMessage());
        }
    }

    public void init(byte[] keyBytes) {
        try {
            keyBytes = PKCS7Padding(keyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.key = new SecretKeySpec(keyBytes, "AES");
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
        } catch (NoSuchPaddingException e3) {
            e3.printStackTrace();
        } catch (NoSuchProviderException e4) {
            e4.printStackTrace();
        }
    }

    public byte[] encrypt(byte[] content, byte[] keyBytes) {
        init(keyBytes);
        PrintStream printStream = System.out;
        printStream.println("IV：" + new String(this.iv));
        try {
            this.cipher.init(1, this.key, new IvParameterSpec(this.iv));
            return this.cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
        init(keyBytes);
        PrintStream printStream = System.out;
        printStream.println("IV：" + new String(this.iv));
        try {
            this.cipher.init(2, this.key, new IvParameterSpec(this.iv));
            return this.cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
