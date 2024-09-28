package io.openinstall.sdk;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class y extends v {
    public y(Context context) {
        super(context);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0023 A[SYNTHETIC, Splitter:B:13:0x0023] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(java.io.File r5) throws java.io.IOException {
        /*
            r4 = this;
            r0 = 0
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0020 }
            java.lang.String r2 = "r"
            r1.<init>(r5, r2)     // Catch:{ all -> 0x0020 }
            long r2 = r1.length()     // Catch:{ all -> 0x001d }
            int r5 = (int) r2     // Catch:{ all -> 0x001d }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x001d }
            r1.readFully(r5)     // Catch:{ all -> 0x001d }
            java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x001d }
            r0.<init>(r5)     // Catch:{ all -> 0x001d }
            r1.close()     // Catch:{ IOException -> 0x001b }
            goto L_0x001c
        L_0x001b:
            r5 = move-exception
        L_0x001c:
            return r0
        L_0x001d:
            r5 = move-exception
            r0 = r1
            goto L_0x0021
        L_0x0020:
            r5 = move-exception
        L_0x0021:
            if (r0 == 0) goto L_0x0028
            r0.close()     // Catch:{ IOException -> 0x0027 }
            goto L_0x0028
        L_0x0027:
            r0 = move-exception
        L_0x0028:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.y.a(java.io.File):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0019 A[SYNTHETIC, Splitter:B:12:0x0019] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(java.io.File r3, java.lang.String r4) throws java.io.IOException {
        /*
            r2 = this;
            r0 = 0
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ all -> 0x0016 }
            r1.<init>(r3)     // Catch:{ all -> 0x0016 }
            byte[] r3 = r4.getBytes()     // Catch:{ all -> 0x0013 }
            r1.write(r3)     // Catch:{ all -> 0x0013 }
            r1.close()     // Catch:{ IOException -> 0x0011 }
            goto L_0x0012
        L_0x0011:
            r3 = move-exception
        L_0x0012:
            return
        L_0x0013:
            r3 = move-exception
            r0 = r1
            goto L_0x0017
        L_0x0016:
            r3 = move-exception
        L_0x0017:
            if (r0 == 0) goto L_0x001e
            r0.close()     // Catch:{ IOException -> 0x001d }
            goto L_0x001e
        L_0x001d:
            r4 = move-exception
        L_0x001e:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.y.a(java.io.File, java.lang.String):void");
    }

    public String b(String str) {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return null;
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        try {
            return a(new File(externalStorageDirectory.getAbsolutePath() + "/Installation", str));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean b(String str, String str2) {
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return false;
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        try {
            File file = new File(externalStorageDirectory.getAbsolutePath() + "/Installation", str);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            a(file, str2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
