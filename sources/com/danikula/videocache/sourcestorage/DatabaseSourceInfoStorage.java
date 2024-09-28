package com.danikula.videocache.sourcestorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.danikula.videocache.Preconditions;
import com.danikula.videocache.SourceInfo;

class DatabaseSourceInfoStorage extends SQLiteOpenHelper implements SourceInfoStorage {
    private static final String[] ALL_COLUMNS = {COLUMN_ID, "url", COLUMN_LENGTH, COLUMN_MIME};
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_MIME = "mime";
    private static final String COLUMN_URL = "url";
    private static final String CREATE_SQL = "CREATE TABLE SourceInfo (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,url TEXT NOT NULL,mime TEXT,length INTEGER);";
    private static final String TABLE = "SourceInfo";

    DatabaseSourceInfoStorage(Context context) {
        super(context, "AndroidVideoCache.db", (SQLiteDatabase.CursorFactory) null, 1);
        Preconditions.checkNotNull(context);
    }

    public void onCreate(SQLiteDatabase db) {
        Preconditions.checkNotNull(db);
        db.execSQL(CREATE_SQL);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Should not be called. There is no any migration");
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002d A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.danikula.videocache.SourceInfo get(java.lang.String r10) {
        /*
            r9 = this;
            com.danikula.videocache.Preconditions.checkNotNull(r10)
            r0 = 0
            android.database.sqlite.SQLiteDatabase r1 = r9.getReadableDatabase()     // Catch:{ all -> 0x0031 }
            java.lang.String r2 = "SourceInfo"
            java.lang.String[] r3 = ALL_COLUMNS     // Catch:{ all -> 0x0031 }
            java.lang.String r4 = "url=?"
            r5 = 1
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ all -> 0x0031 }
            r6 = 0
            r5[r6] = r10     // Catch:{ all -> 0x0031 }
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0031 }
            r0 = r1
            if (r0 == 0) goto L_0x002a
            boolean r1 = r0.moveToFirst()     // Catch:{ all -> 0x0031 }
            if (r1 != 0) goto L_0x0025
            goto L_0x002a
        L_0x0025:
            com.danikula.videocache.SourceInfo r1 = r9.convert((android.database.Cursor) r0)     // Catch:{ all -> 0x0031 }
            goto L_0x002b
        L_0x002a:
            r1 = 0
        L_0x002b:
            if (r0 == 0) goto L_0x0030
            r0.close()
        L_0x0030:
            return r1
        L_0x0031:
            r1 = move-exception
            if (r0 == 0) goto L_0x0037
            r0.close()
        L_0x0037:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.danikula.videocache.sourcestorage.DatabaseSourceInfoStorage.get(java.lang.String):com.danikula.videocache.SourceInfo");
    }

    public void put(String url, SourceInfo sourceInfo) {
        Preconditions.checkAllNotNull(url, sourceInfo);
        boolean exist = get(url) != null;
        ContentValues contentValues = convert(sourceInfo);
        if (exist) {
            getWritableDatabase().update(TABLE, contentValues, "url=?", new String[]{url});
        } else {
            getWritableDatabase().insert(TABLE, (String) null, contentValues);
        }
    }

    public void release() {
        close();
    }

    private SourceInfo convert(Cursor cursor) {
        return new SourceInfo(cursor.getString(cursor.getColumnIndexOrThrow("url")), cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LENGTH)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIME)));
    }

    private ContentValues convert(SourceInfo sourceInfo) {
        ContentValues values = new ContentValues();
        values.put("url", sourceInfo.url);
        values.put(COLUMN_LENGTH, Long.valueOf(sourceInfo.length));
        values.put(COLUMN_MIME, sourceInfo.mime);
        return values;
    }
}
