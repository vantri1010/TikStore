package io.openinstall.sdk;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;

public class w extends v {
    public w(Context context) {
        super(context);
    }

    private String c(String str) {
        return str + DefaultHlsExtractorFactory.MP3_FILE_EXTENSION;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0052, code lost:
        if (r9.isClosed() == false) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006a, code lost:
        if (r9.isClosed() == false) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006c, code lost:
        r9.close();
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0066  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String b(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.String r0 = "title"
            android.content.Context r1 = r8.a
            android.content.ContentResolver r2 = r1.getContentResolver()
            android.net.Uri r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            r1 = 0
            java.lang.String r4 = "_display_name"
            java.lang.String[] r4 = new java.lang.String[]{r4, r0}     // Catch:{ Exception -> 0x0062, all -> 0x0055 }
            java.lang.String r5 = "_display_name=?"
            r6 = 1
            java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ Exception -> 0x0062, all -> 0x0055 }
            r7 = 0
            java.lang.String r9 = r8.c(r9)     // Catch:{ Exception -> 0x0062, all -> 0x0055 }
            r6[r7] = r9     // Catch:{ Exception -> 0x0062, all -> 0x0055 }
            r7 = 0
            android.database.Cursor r9 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0062, all -> 0x0055 }
            if (r9 == 0) goto L_0x004c
            int r2 = r9.getCount()     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            if (r2 <= 0) goto L_0x004c
            r9.moveToFirst()     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            if (r0 < 0) goto L_0x004c
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            java.lang.String r0 = io.openinstall.sdk.bz.a(r0)     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            if (r9 == 0) goto L_0x0046
            boolean r1 = r9.isClosed()
            if (r1 != 0) goto L_0x0046
            r9.close()
        L_0x0046:
            return r0
        L_0x0047:
            r0 = move-exception
            r1 = r9
            goto L_0x0056
        L_0x004a:
            r0 = move-exception
            goto L_0x0064
        L_0x004c:
            if (r9 == 0) goto L_0x006f
            boolean r0 = r9.isClosed()
            if (r0 != 0) goto L_0x006f
            goto L_0x006c
        L_0x0055:
            r0 = move-exception
        L_0x0056:
            if (r1 == 0) goto L_0x0061
            boolean r9 = r1.isClosed()
            if (r9 != 0) goto L_0x0061
            r1.close()
        L_0x0061:
            throw r0
        L_0x0062:
            r9 = move-exception
            r9 = r1
        L_0x0064:
            if (r9 == 0) goto L_0x006f
            boolean r0 = r9.isClosed()
            if (r0 != 0) goto L_0x006f
        L_0x006c:
            r9.close()
        L_0x006f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.w.b(java.lang.String):java.lang.String");
    }

    public boolean b(String str, String str2) {
        if (a(str) != null) {
            return true;
        }
        ContentResolver contentResolver = this.a.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put("relative_path", "Notifications/Installation");
        contentValues.put("_display_name", c(str));
        contentValues.put("title", bz.c(str2));
        try {
            return contentResolver.insert(uri, contentValues) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
