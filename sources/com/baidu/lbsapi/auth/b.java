package com.baidu.lbsapi.auth;

import android.content.Context;
import android.content.pm.PackageManager;
import com.king.zxing.util.LogUtils;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

class b {

    static class a {
        public static String a(byte[] bArr) {
            char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            for (int i = 0; i < bArr.length; i++) {
                sb.append(cArr[(bArr[i] & 240) >> 4]);
                sb.append(cArr[bArr[i] & 15]);
            }
            return sb.toString();
        }
    }

    static String a() {
        return Locale.getDefault().getLanguage();
    }

    protected static String a(Context context) {
        String packageName = context.getPackageName();
        String a2 = a(context, packageName);
        return a2 + ";" + packageName;
    }

    private static String a(Context context, String str) {
        String str2;
        try {
            str2 = a((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(context.getPackageManager().getPackageInfo(str, 64).signatures[0].toByteArray())));
        } catch (PackageManager.NameNotFoundException | CertificateException e) {
            str2 = "";
        }
        if (str2 == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str2.length(); i++) {
            stringBuffer.append(str2.charAt(i));
            if (i > 0 && i % 2 == 1 && i < str2.length() - 1) {
                stringBuffer.append(LogUtils.COLON);
            }
        }
        return stringBuffer.toString();
    }

    static String a(X509Certificate x509Certificate) {
        try {
            return a.a(a(x509Certificate.getEncoded()));
        } catch (CertificateEncodingException e) {
            return null;
        }
    }

    static byte[] a(byte[] bArr) {
        try {
            return MessageDigest.getInstance("SHA1").digest(bArr);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    protected static String[] b(Context context) {
        String packageName = context.getPackageName();
        String[] b = b(context, packageName);
        if (b == null || b.length <= 0) {
            return null;
        }
        int length = b.length;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = b[i] + ";" + packageName;
            if (a.a) {
                a.a("mcode" + strArr[i]);
            }
        }
        return strArr;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0050  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String[] b(android.content.Context r6, java.lang.String r7) {
        /*
            r0 = 0
            r1 = 0
            android.content.pm.PackageManager r6 = r6.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            r2 = 64
            android.content.pm.PackageInfo r6 = r6.getPackageInfo(r7, r2)     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            android.content.pm.Signature[] r6 = r6.signatures     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            if (r6 == 0) goto L_0x003e
            int r7 = r6.length     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            if (r7 <= 0) goto L_0x003e
            int r7 = r6.length     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            java.lang.String[] r7 = new java.lang.String[r7]     // Catch:{ NameNotFoundException -> 0x0042, CertificateException -> 0x0040 }
            r2 = 0
        L_0x0017:
            int r3 = r6.length     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            if (r2 >= r3) goto L_0x0044
            java.lang.String r3 = "X.509"
            java.security.cert.CertificateFactory r3 = java.security.cert.CertificateFactory.getInstance(r3)     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            java.io.ByteArrayInputStream r4 = new java.io.ByteArrayInputStream     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            r5 = r6[r2]     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            byte[] r5 = r5.toByteArray()     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            r4.<init>(r5)     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            java.security.cert.Certificate r3 = r3.generateCertificate(r4)     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            java.security.cert.X509Certificate r3 = (java.security.cert.X509Certificate) r3     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            java.lang.String r3 = a((java.security.cert.X509Certificate) r3)     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            r7[r2] = r3     // Catch:{ NameNotFoundException -> 0x003c, CertificateException -> 0x003a }
            int r2 = r2 + 1
            goto L_0x0017
        L_0x003a:
            r6 = move-exception
            goto L_0x0044
        L_0x003c:
            r6 = move-exception
            goto L_0x0044
        L_0x003e:
            r7 = r0
            goto L_0x0044
        L_0x0040:
            r6 = move-exception
            goto L_0x003e
        L_0x0042:
            r6 = move-exception
            goto L_0x003e
        L_0x0044:
            if (r7 == 0) goto L_0x0095
            int r6 = r7.length
            if (r6 <= 0) goto L_0x0095
            int r6 = r7.length
            java.lang.String[] r0 = new java.lang.String[r6]
            r6 = 0
        L_0x004d:
            int r2 = r7.length
            if (r6 >= r2) goto L_0x0095
            r2 = r7[r6]
            if (r2 == 0) goto L_0x0092
            r2 = r7[r6]
            int r2 = r2.length()
            if (r2 != 0) goto L_0x005d
            goto L_0x0092
        L_0x005d:
            java.lang.StringBuffer r2 = new java.lang.StringBuffer
            r2.<init>()
            r3 = 0
        L_0x0063:
            r4 = r7[r6]
            int r4 = r4.length()
            if (r3 >= r4) goto L_0x008c
            r4 = r7[r6]
            char r4 = r4.charAt(r3)
            r2.append(r4)
            if (r3 <= 0) goto L_0x0089
            int r4 = r3 % 2
            r5 = 1
            if (r4 != r5) goto L_0x0089
            r4 = r7[r6]
            int r4 = r4.length()
            int r4 = r4 - r5
            if (r3 >= r4) goto L_0x0089
            java.lang.String r4 = ":"
            r2.append(r4)
        L_0x0089:
            int r3 = r3 + 1
            goto L_0x0063
        L_0x008c:
            java.lang.String r2 = r2.toString()
            r0[r6] = r2
        L_0x0092:
            int r6 = r6 + 1
            goto L_0x004d
        L_0x0095:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.lbsapi.auth.b.b(android.content.Context, java.lang.String):java.lang.String[]");
    }
}
