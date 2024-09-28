package com.baidu.location.b;

import android.util.Base64;
import com.baidu.location.Jni;
import com.king.zxing.util.LogUtils;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class j {
    private IvParameterSpec a;
    private SecretKeySpec b;
    private Cipher c;
    private boolean d;

    private static class a {
        /* access modifiers changed from: private */
        public static j a = new j();
    }

    private j() {
        this.d = false;
        try {
            String str = Jni.getldkaiv();
            if (str != null && str.contains(LogUtils.VERTICAL)) {
                String[] split = str.split("\\|");
                this.a = new IvParameterSpec(split[1].getBytes("UTF-8"));
                this.b = new SecretKeySpec(split[0].getBytes("UTF-8"), "AES");
                this.c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                this.d = true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (UnsupportedEncodingException e3) {
            e3.printStackTrace();
        }
    }

    public static j a() {
        return a.a;
    }

    public synchronized String a(String str) {
        if (!this.d) {
            return null;
        }
        try {
            this.c.init(2, this.b, this.a);
            return new String(this.c.doFinal(Base64.decode(str, 0)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean b() {
        return this.d;
    }
}
