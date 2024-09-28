package com.blankj.utilcode.util;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;

public final class UriUtils {
    private UriUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Uri file2Uri(File file) {
        if (file == null) {
            throw new NullPointerException("Argument 'file' of type File (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(Utils.getApp(), Utils.getApp().getPackageName() + ".utilcode.provider", file);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x019c A[Catch:{ Exception -> 0x0209 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x019f A[Catch:{ Exception -> 0x0209 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.File uri2File(android.net.Uri r25) {
        /*
            r1 = r25
            if (r1 == 0) goto L_0x0378
            java.lang.String r0 = r25.toString()
            java.lang.String r2 = "UriUtils"
            android.util.Log.d(r2, r0)
            java.lang.String r3 = r25.getAuthority()
            java.lang.String r4 = r25.getScheme()
            java.lang.String r5 = r25.getPath()
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 24
            java.lang.String r7 = "/"
            r8 = 0
            if (r0 < r6) goto L_0x008e
            if (r5 == 0) goto L_0x008e
            java.lang.String r0 = "/external"
            java.lang.String r6 = "/external_path"
            java.lang.String[] r0 = new java.lang.String[]{r0, r6}
            int r6 = r0.length
            r9 = 0
        L_0x002e:
            if (r9 >= r6) goto L_0x008e
            r10 = r0[r9]
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r11.append(r7)
            java.lang.String r11 = r11.toString()
            boolean r11 = r5.startsWith(r11)
            if (r11 == 0) goto L_0x008b
            java.io.File r11 = new java.io.File
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.io.File r13 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r13 = r13.getAbsolutePath()
            r12.append(r13)
            java.lang.String r13 = ""
            java.lang.String r13 = r5.replace(r10, r13)
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r11.<init>(r12)
            boolean r12 = r11.exists()
            if (r12 == 0) goto L_0x008b
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = r25.toString()
            r6.append(r7)
            java.lang.String r7 = " -> "
            r6.append(r7)
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            android.util.Log.d(r2, r6)
            return r11
        L_0x008b:
            int r9 = r9 + 1
            goto L_0x002e
        L_0x008e:
            java.lang.String r0 = "file"
            boolean r0 = r0.equals(r4)
            r6 = 0
            if (r0 == 0) goto L_0x00b8
            if (r5 == 0) goto L_0x009f
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            return r0
        L_0x009f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = r25.toString()
            r0.append(r7)
            java.lang.String r7 = " parse failed. -> 0"
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
            return r6
        L_0x00b8:
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            java.lang.String r10 = "content"
            if (r0 < r9) goto L_0x034f
            android.app.Application r0 = com.blankj.utilcode.util.Utils.getApp()
            boolean r0 = android.provider.DocumentsContract.isDocumentUri(r0, r1)
            if (r0 == 0) goto L_0x034c
            java.lang.String r0 = "com.android.externalstorage.documents"
            boolean r0 = r0.equals(r3)
            java.lang.String r9 = ":"
            r11 = 1
            if (r0 == 0) goto L_0x0265
            java.lang.String r10 = android.provider.DocumentsContract.getDocumentId(r25)
            java.lang.String[] r9 = r10.split(r9)
            r12 = r9[r8]
            java.lang.String r0 = "primary"
            boolean r0 = r0.equalsIgnoreCase(r12)
            if (r0 == 0) goto L_0x0105
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.io.File r6 = android.os.Environment.getExternalStorageDirectory()
            r2.append(r6)
            r2.append(r7)
            r6 = r9[r11]
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            return r0
        L_0x0105:
            android.app.Application r0 = com.blankj.utilcode.util.Utils.getApp()
            java.lang.String r13 = "storage"
            java.lang.Object r0 = r0.getSystemService(r13)
            r13 = r0
            android.os.storage.StorageManager r13 = (android.os.storage.StorageManager) r13
            java.lang.String r0 = "android.os.storage.StorageVolume"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ Exception -> 0x0220 }
            java.lang.Class r14 = r13.getClass()     // Catch:{ Exception -> 0x0220 }
            java.lang.String r15 = "getVolumeList"
            java.lang.Class[] r6 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x0220 }
            java.lang.reflect.Method r6 = r14.getMethod(r15, r6)     // Catch:{ Exception -> 0x0220 }
            java.lang.String r14 = "getUuid"
            java.lang.Class[] r15 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x0220 }
            java.lang.reflect.Method r14 = r0.getMethod(r14, r15)     // Catch:{ Exception -> 0x0220 }
            java.lang.String r15 = "getState"
            java.lang.Class[] r11 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x0220 }
            java.lang.reflect.Method r11 = r0.getMethod(r15, r11)     // Catch:{ Exception -> 0x0220 }
            java.lang.String r15 = "getPath"
            r16 = r5
            java.lang.Class[] r5 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x021a }
            java.lang.reflect.Method r5 = r0.getMethod(r15, r5)     // Catch:{ Exception -> 0x021a }
            java.lang.String r15 = "isPrimary"
            r17 = r10
            java.lang.Class[] r10 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x0216 }
            java.lang.reflect.Method r10 = r0.getMethod(r15, r10)     // Catch:{ Exception -> 0x0216 }
            java.lang.String r15 = "isEmulated"
            java.lang.Class[] r1 = new java.lang.Class[r8]     // Catch:{ Exception -> 0x0216 }
            java.lang.reflect.Method r1 = r0.getMethod(r15, r1)     // Catch:{ Exception -> 0x0216 }
            java.lang.Object[] r15 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0216 }
            java.lang.Object r15 = r6.invoke(r13, r15)     // Catch:{ Exception -> 0x0216 }
            int r18 = java.lang.reflect.Array.getLength(r15)     // Catch:{ Exception -> 0x0216 }
            r19 = r18
            r18 = 0
            r8 = r18
        L_0x0160:
            r18 = r0
            r0 = r19
            if (r8 >= r0) goto L_0x020b
            java.lang.Object r19 = java.lang.reflect.Array.get(r15, r8)     // Catch:{ Exception -> 0x0216 }
            r20 = r19
            r19 = r0
            java.lang.String r0 = "mounted"
            r21 = r6
            r22 = r13
            r6 = 0
            java.lang.Object[] r13 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0209 }
            r6 = r20
            java.lang.Object r13 = r11.invoke(r6, r13)     // Catch:{ Exception -> 0x0209 }
            boolean r0 = r0.equals(r13)     // Catch:{ Exception -> 0x0209 }
            if (r0 != 0) goto L_0x0197
            java.lang.String r0 = "mounted_ro"
            r20 = r15
            r13 = 0
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0209 }
            java.lang.Object r13 = r11.invoke(r6, r15)     // Catch:{ Exception -> 0x0209 }
            boolean r0 = r0.equals(r13)     // Catch:{ Exception -> 0x0209 }
            if (r0 == 0) goto L_0x0195
            goto L_0x0199
        L_0x0195:
            r0 = 0
            goto L_0x019a
        L_0x0197:
            r20 = r15
        L_0x0199:
            r0 = 1
        L_0x019a:
            if (r0 != 0) goto L_0x019f
            r24 = r1
            goto L_0x01fb
        L_0x019f:
            r13 = 0
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0209 }
            java.lang.Object r13 = r10.invoke(r6, r15)     // Catch:{ Exception -> 0x0209 }
            java.lang.Boolean r13 = (java.lang.Boolean) r13     // Catch:{ Exception -> 0x0209 }
            boolean r13 = r13.booleanValue()     // Catch:{ Exception -> 0x0209 }
            if (r13 == 0) goto L_0x01c0
            r13 = 0
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0209 }
            java.lang.Object r13 = r1.invoke(r6, r15)     // Catch:{ Exception -> 0x0209 }
            java.lang.Boolean r13 = (java.lang.Boolean) r13     // Catch:{ Exception -> 0x0209 }
            boolean r13 = r13.booleanValue()     // Catch:{ Exception -> 0x0209 }
            if (r13 == 0) goto L_0x01c0
            r24 = r1
            goto L_0x01fb
        L_0x01c0:
            r13 = 0
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0209 }
            java.lang.Object r13 = r14.invoke(r6, r15)     // Catch:{ Exception -> 0x0209 }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ Exception -> 0x0209 }
            if (r13 == 0) goto L_0x01f7
            boolean r15 = r13.equals(r12)     // Catch:{ Exception -> 0x0209 }
            if (r15 == 0) goto L_0x01f7
            java.io.File r15 = new java.io.File     // Catch:{ Exception -> 0x0209 }
            r23 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0209 }
            r0.<init>()     // Catch:{ Exception -> 0x0209 }
            r24 = r1
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0209 }
            java.lang.Object r1 = r5.invoke(r6, r1)     // Catch:{ Exception -> 0x0209 }
            r0.append(r1)     // Catch:{ Exception -> 0x0209 }
            r0.append(r7)     // Catch:{ Exception -> 0x0209 }
            r1 = 1
            r1 = r9[r1]     // Catch:{ Exception -> 0x0209 }
            r0.append(r1)     // Catch:{ Exception -> 0x0209 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0209 }
            r15.<init>(r0)     // Catch:{ Exception -> 0x0209 }
            return r15
        L_0x01f7:
            r23 = r0
            r24 = r1
        L_0x01fb:
            int r8 = r8 + 1
            r0 = r18
            r15 = r20
            r6 = r21
            r13 = r22
            r1 = r24
            goto L_0x0160
        L_0x0209:
            r0 = move-exception
            goto L_0x0227
        L_0x020b:
            r19 = r0
            r24 = r1
            r21 = r6
            r22 = r13
            r20 = r15
            goto L_0x024b
        L_0x0216:
            r0 = move-exception
            r22 = r13
            goto L_0x0227
        L_0x021a:
            r0 = move-exception
            r17 = r10
            r22 = r13
            goto L_0x0227
        L_0x0220:
            r0 = move-exception
            r16 = r5
            r17 = r10
            r22 = r13
        L_0x0227:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = r25.toString()
            r1.append(r5)
            java.lang.String r5 = " parse failed. "
            r1.append(r5)
            java.lang.String r5 = r0.toString()
            r1.append(r5)
            java.lang.String r5 = " -> 1_0"
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r2, r1)
        L_0x024b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r25.toString()
            r0.append(r1)
            java.lang.String r1 = " parse failed. -> 1_0"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
            r1 = 0
            return r1
        L_0x0265:
            r16 = r5
            java.lang.String r0 = "com.android.providers.downloads.documents"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x02c0
            java.lang.String r1 = android.provider.DocumentsContract.getDocumentId(r25)
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x02a6
            java.lang.String r0 = "content://downloads/public_downloads"
            android.net.Uri r0 = android.net.Uri.parse(r0)     // Catch:{ NumberFormatException -> 0x0292 }
            java.lang.Long r5 = java.lang.Long.valueOf(r1)     // Catch:{ NumberFormatException -> 0x0292 }
            long r5 = r5.longValue()     // Catch:{ NumberFormatException -> 0x0292 }
            android.net.Uri r0 = android.content.ContentUris.withAppendedId(r0, r5)     // Catch:{ NumberFormatException -> 0x0292 }
            java.lang.String r5 = "1_1"
            java.io.File r2 = getFileFromUri(r0, r5)     // Catch:{ NumberFormatException -> 0x0292 }
            return r2
        L_0x0292:
            r0 = move-exception
            java.lang.String r5 = "raw:"
            boolean r5 = r1.startsWith(r5)
            if (r5 == 0) goto L_0x02a6
            java.io.File r2 = new java.io.File
            r5 = 4
            java.lang.String r5 = r1.substring(r5)
            r2.<init>(r5)
            return r2
        L_0x02a6:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = r25.toString()
            r0.append(r5)
            java.lang.String r5 = " parse failed. -> 1_1"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
            r2 = 0
            return r2
        L_0x02c0:
            java.lang.String r0 = "com.android.providers.media.documents"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0321
            java.lang.String r0 = android.provider.DocumentsContract.getDocumentId(r25)
            java.lang.String[] r1 = r0.split(r9)
            r5 = 0
            r6 = r1[r5]
            java.lang.String r5 = "image"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x02de
            android.net.Uri r2 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            goto L_0x02f3
        L_0x02de:
            java.lang.String r5 = "video"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x02e9
            android.net.Uri r2 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            goto L_0x02f3
        L_0x02e9:
            java.lang.String r5 = "audio"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0307
            android.net.Uri r2 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        L_0x02f3:
            java.lang.String r5 = "_id=?"
            r7 = 1
            java.lang.String[] r8 = new java.lang.String[r7]
            r7 = r1[r7]
            r9 = 0
            r8[r9] = r7
            r7 = r8
            java.lang.String r8 = "_id=?"
            java.lang.String r9 = "1_2"
            java.io.File r8 = getFileFromUri(r2, r8, r7, r9)
            return r8
        L_0x0307:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = r25.toString()
            r5.append(r7)
            java.lang.String r7 = " parse failed. -> 1_2"
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            android.util.Log.d(r2, r5)
            r2 = 0
            return r2
        L_0x0321:
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x0330
            java.lang.String r0 = "1_3"
            r1 = r25
            java.io.File r0 = getFileFromUri(r1, r0)
            return r0
        L_0x0330:
            r1 = r25
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = r25.toString()
            r0.append(r5)
            java.lang.String r5 = " parse failed. -> 1_4"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
            r2 = 0
            return r2
        L_0x034c:
            r16 = r5
            goto L_0x0351
        L_0x034f:
            r16 = r5
        L_0x0351:
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x035e
            java.lang.String r0 = "2"
            java.io.File r0 = getFileFromUri(r1, r0)
            return r0
        L_0x035e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = r25.toString()
            r0.append(r5)
            java.lang.String r5 = " parse failed. -> 3"
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
            r2 = 0
            return r2
        L_0x0378:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r2 = "Argument 'uri' of type Uri (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.UriUtils.uri2File(android.net.Uri):java.io.File");
    }

    private static File getFileFromUri(Uri uri, String code) {
        return getFileFromUri(uri, (String) null, (String[]) null, code);
    }

    private static File getFileFromUri(Uri uri, String selection, String[] selectionArgs, String code) {
        Cursor cursor = Utils.getApp().getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, (String) null);
        if (cursor == null) {
            Log.d("UriUtils", uri.toString() + " parse failed(cursor is null). -> " + code);
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                }
                Log.d("UriUtils", uri.toString() + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                cursor.close();
                return null;
            }
            Log.d("UriUtils", uri.toString() + " parse failed(moveToFirst return false). -> " + code);
            cursor.close();
            return null;
        } catch (Exception e) {
            Log.d("UriUtils", uri.toString() + " parse failed. -> " + code);
            return null;
        } finally {
            cursor.close();
        }
    }
}
