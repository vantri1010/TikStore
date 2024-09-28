package com.baidu.mapsdkplatform.comapi.commonutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class a {
    private static final boolean a = (Build.VERSION.SDK_INT >= 8);

    public static Bitmap a(String str, Context context) {
        try {
            InputStream open = context.getAssets().open(str);
            if (open != null) {
                return BitmapFactory.decodeStream(open);
            }
            return null;
        } catch (Exception e) {
            return BitmapFactory.decodeFile(b("assets/" + str, str, context));
        }
    }

    private static void a(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] bArr = new byte[4096];
        while (true) {
            try {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e2) {
                }
            }
        }
        fileOutputStream.flush();
    }

    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v1, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r0v2, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r0v3, types: [java.io.FileOutputStream] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0087 A[SYNTHETIC, Splitter:B:35:0x0087] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008c A[Catch:{ IOException -> 0x0060 }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0093 A[SYNTHETIC, Splitter:B:41:0x0093] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009b A[Catch:{ IOException -> 0x0097 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(java.lang.String r6, java.lang.String r7, android.content.Context r8) {
        /*
            r0 = 0
            android.content.res.AssetManager r1 = r8.getAssets()     // Catch:{ Exception -> 0x006f, all -> 0x006c }
            java.io.InputStream r1 = r1.open(r6)     // Catch:{ Exception -> 0x006f, all -> 0x006c }
            if (r1 == 0) goto L_0x005a
            int r2 = r1.available()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r1.read(r2)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r4.<init>()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.io.File r5 = r8.getFilesDir()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.lang.String r5 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r4.append(r5)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.lang.String r5 = "/"
            r4.append(r5)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r4.append(r7)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            boolean r4 = r3.exists()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            if (r4 == 0) goto L_0x003e
            r3.delete()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
        L_0x003e:
            r3.createNewFile()     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r4.<init>(r3)     // Catch:{ Exception -> 0x0056, all -> 0x0052 }
            r4.write(r2)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r4.close()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r0 = r4
            goto L_0x005a
        L_0x004e:
            r6 = move-exception
            goto L_0x0054
        L_0x0050:
            r0 = move-exception
            goto L_0x0058
        L_0x0052:
            r6 = move-exception
            r4 = r0
        L_0x0054:
            r0 = r1
            goto L_0x0091
        L_0x0056:
            r2 = move-exception
            r4 = r0
        L_0x0058:
            r0 = r1
            goto L_0x0071
        L_0x005a:
            if (r1 == 0) goto L_0x0062
            r1.close()     // Catch:{ IOException -> 0x0060 }
            goto L_0x0062
        L_0x0060:
            r6 = move-exception
            goto L_0x0068
        L_0x0062:
            if (r0 == 0) goto L_0x008f
            r0.close()     // Catch:{ IOException -> 0x0060 }
            goto L_0x008f
        L_0x0068:
            r6.printStackTrace()
            goto L_0x008f
        L_0x006c:
            r6 = move-exception
            r4 = r0
            goto L_0x0091
        L_0x006f:
            r1 = move-exception
            r4 = r0
        L_0x0071:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0090 }
            r1.<init>()     // Catch:{ all -> 0x0090 }
            java.lang.String r2 = "assets/"
            r1.append(r2)     // Catch:{ all -> 0x0090 }
            r1.append(r6)     // Catch:{ all -> 0x0090 }
            java.lang.String r6 = r1.toString()     // Catch:{ all -> 0x0090 }
            b(r6, r7, r8)     // Catch:{ all -> 0x0090 }
            if (r0 == 0) goto L_0x008a
            r0.close()     // Catch:{ IOException -> 0x0060 }
        L_0x008a:
            if (r4 == 0) goto L_0x008f
            r4.close()     // Catch:{ IOException -> 0x0060 }
        L_0x008f:
            return
        L_0x0090:
            r6 = move-exception
        L_0x0091:
            if (r0 == 0) goto L_0x0099
            r0.close()     // Catch:{ IOException -> 0x0097 }
            goto L_0x0099
        L_0x0097:
            r7 = move-exception
            goto L_0x009f
        L_0x0099:
            if (r4 == 0) goto L_0x00a2
            r4.close()     // Catch:{ IOException -> 0x0097 }
            goto L_0x00a2
        L_0x009f:
            r7.printStackTrace()
        L_0x00a2:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.commonutils.a.a(java.lang.String, java.lang.String, android.content.Context):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00af A[SYNTHETIC, Splitter:B:33:0x00af] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b9 A[SYNTHETIC, Splitter:B:38:0x00b9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String b(java.lang.String r8, java.lang.String r9, android.content.Context r10) {
        /*
            java.lang.String r0 = "/"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.io.File r2 = r10.getFilesDir()
            java.lang.String r2 = r2.getAbsolutePath()
            r1.<init>(r2)
            boolean r2 = a
            if (r2 == 0) goto L_0x0018
            java.lang.String r2 = r10.getPackageCodePath()
            goto L_0x001a
        L_0x0018:
            java.lang.String r2 = ""
        L_0x001a:
            r3 = 0
            java.util.zip.ZipFile r4 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x00a1 }
            r4.<init>(r2)     // Catch:{ Exception -> 0x00a1 }
            int r2 = r9.lastIndexOf(r0)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            if (r2 <= 0) goto L_0x005e
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.io.File r10 = r10.getFilesDir()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r10 = r10.getAbsolutePath()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r5.<init>(r10)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r10 = 0
            java.lang.String r10 = r9.substring(r10, r2)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            int r2 = r2 + 1
            int r6 = r9.length()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r9 = r9.substring(r2, r6)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r6.<init>()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r7 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r6.append(r7)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r6.append(r0)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r6.append(r10)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r10 = r6.toString()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r2.<init>(r10, r9)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            goto L_0x0072
        L_0x005e:
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.io.File r10 = r10.getFilesDir()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r2 = "assets"
            r5.<init>(r10, r2)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.lang.String r10 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r2.<init>(r10, r9)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
        L_0x0072:
            r5.mkdirs()     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.util.zip.ZipEntry r9 = r4.getEntry(r8)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            if (r9 != 0) goto L_0x0081
            r4.close()     // Catch:{ IOException -> 0x007f }
            goto L_0x0080
        L_0x007f:
            r8 = move-exception
        L_0x0080:
            return r3
        L_0x0081:
            java.io.InputStream r9 = r4.getInputStream(r9)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            java.io.FileOutputStream r10 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r10.<init>(r2)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            a((java.io.InputStream) r9, (java.io.FileOutputStream) r10)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r1.append(r0)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r1.append(r8)     // Catch:{ Exception -> 0x009c, all -> 0x0099 }
            r4.close()     // Catch:{ IOException -> 0x0097 }
            goto L_0x00b2
        L_0x0097:
            r8 = move-exception
            goto L_0x00b2
        L_0x0099:
            r8 = move-exception
            r3 = r4
            goto L_0x00b7
        L_0x009c:
            r8 = move-exception
            r3 = r4
            goto L_0x00a2
        L_0x009f:
            r8 = move-exception
            goto L_0x00b7
        L_0x00a1:
            r8 = move-exception
        L_0x00a2:
            java.lang.Class<com.baidu.mapsdkplatform.comapi.commonutils.a> r9 = com.baidu.mapsdkplatform.comapi.commonutils.a.class
            java.lang.String r9 = r9.getSimpleName()     // Catch:{ all -> 0x009f }
            java.lang.String r10 = "copyAssetsError"
            android.util.Log.e(r9, r10, r8)     // Catch:{ all -> 0x009f }
            if (r3 == 0) goto L_0x00b2
            r3.close()     // Catch:{ IOException -> 0x0097 }
        L_0x00b2:
            java.lang.String r8 = r1.toString()
            return r8
        L_0x00b7:
            if (r3 == 0) goto L_0x00be
            r3.close()     // Catch:{ IOException -> 0x00bd }
            goto L_0x00be
        L_0x00bd:
            r9 = move-exception
        L_0x00be:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.commonutils.a.b(java.lang.String, java.lang.String, android.content.Context):java.lang.String");
    }
}
