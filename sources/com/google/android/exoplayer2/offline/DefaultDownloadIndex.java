package com.google.android.exoplayer2.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadStateCursor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DefaultDownloadIndex implements DownloadIndex {
    private static final String DATABASE_NAME = "exoplayer_internal.db";
    private final DatabaseProvider databaseProvider;
    private DownloadStateTable downloadStateTable;

    public interface DatabaseProvider {
        void close();

        SQLiteDatabase getReadableDatabase();

        SQLiteDatabase getWritableDatabase();
    }

    public DefaultDownloadIndex(Context context) {
        this((DatabaseProvider) new DefaultDatabaseProvider(context));
    }

    public DefaultDownloadIndex(DatabaseProvider databaseProvider2) {
        this.databaseProvider = databaseProvider2;
    }

    public void release() {
        this.databaseProvider.close();
    }

    public DownloadState getDownloadState(String id) {
        return getDownloadStateTable().get(id);
    }

    public DownloadStateCursor getDownloadStates(int... states) {
        return getDownloadStateTable().get(states);
    }

    public void putDownloadState(DownloadState downloadState) {
        getDownloadStateTable().replace(downloadState);
    }

    public void removeDownloadState(String id) {
        getDownloadStateTable().delete(id);
    }

    private DownloadStateTable getDownloadStateTable() {
        if (this.downloadStateTable == null) {
            this.downloadStateTable = new DownloadStateTable(this.databaseProvider);
        }
        return this.downloadStateTable;
    }

    static boolean doesTableExist(DatabaseProvider databaseProvider2, String tableName) {
        if (DatabaseUtils.queryNumEntries(databaseProvider2.getReadableDatabase(), "sqlite_master", "tbl_name = ?", new String[]{tableName}) > 0) {
            return true;
        }
        return false;
    }

    private static final class DownloadStateCursorImpl implements DownloadStateCursor {
        private final Cursor cursor;

        public /* synthetic */ boolean isAfterLast() {
            return DownloadStateCursor.CC.$default$isAfterLast(this);
        }

        public /* synthetic */ boolean isBeforeFirst() {
            return DownloadStateCursor.CC.$default$isBeforeFirst(this);
        }

        public /* synthetic */ boolean isFirst() {
            return DownloadStateCursor.CC.$default$isFirst(this);
        }

        public /* synthetic */ boolean isLast() {
            return DownloadStateCursor.CC.$default$isLast(this);
        }

        public /* synthetic */ boolean moveToFirst() {
            return DownloadStateCursor.CC.$default$moveToFirst(this);
        }

        public /* synthetic */ boolean moveToLast() {
            return DownloadStateCursor.CC.$default$moveToLast(this);
        }

        public /* synthetic */ boolean moveToNext() {
            return DownloadStateCursor.CC.$default$moveToNext(this);
        }

        public /* synthetic */ boolean moveToPrevious() {
            return DownloadStateCursor.CC.$default$moveToPrevious(this);
        }

        private DownloadStateCursorImpl(Cursor cursor2) {
            this.cursor = cursor2;
        }

        public DownloadState getDownloadState() {
            return DownloadStateTable.getDownloadState(this.cursor);
        }

        public int getCount() {
            return this.cursor.getCount();
        }

        public int getPosition() {
            return this.cursor.getPosition();
        }

        public boolean moveToPosition(int position) {
            return this.cursor.moveToPosition(position);
        }

        public void close() {
            this.cursor.close();
        }

        public boolean isClosed() {
            return this.cursor.isClosed();
        }
    }

    static final class DownloadStateTable {
        private static final String[] COLUMNS = {"id", COLUMN_TYPE, COLUMN_URI, COLUMN_CACHE_KEY, "state", COLUMN_DOWNLOAD_PERCENTAGE, COLUMN_DOWNLOADED_BYTES, COLUMN_TOTAL_BYTES, COLUMN_FAILURE_REASON, COLUMN_STOP_FLAGS, COLUMN_START_TIME_MS, COLUMN_UPDATE_TIME_MS, COLUMN_STREAM_KEYS, COLUMN_CUSTOM_METADATA};
        private static final String COLUMN_CACHE_KEY = "cache_key";
        private static final String COLUMN_CUSTOM_METADATA = "custom_metadata";
        private static final String COLUMN_DOWNLOADED_BYTES = "downloaded_bytes";
        private static final String COLUMN_DOWNLOAD_PERCENTAGE = "download_percentage";
        private static final String COLUMN_FAILURE_REASON = "failure_reason";
        private static final String COLUMN_ID = "id";
        private static final int COLUMN_INDEX_CACHE_KEY = 3;
        private static final int COLUMN_INDEX_CUSTOM_METADATA = 13;
        private static final int COLUMN_INDEX_DOWNLOADED_BYTES = 6;
        private static final int COLUMN_INDEX_DOWNLOAD_PERCENTAGE = 5;
        private static final int COLUMN_INDEX_FAILURE_REASON = 8;
        private static final int COLUMN_INDEX_ID = 0;
        private static final int COLUMN_INDEX_START_TIME_MS = 10;
        private static final int COLUMN_INDEX_STATE = 4;
        private static final int COLUMN_INDEX_STOP_FLAGS = 9;
        private static final int COLUMN_INDEX_STREAM_KEYS = 12;
        private static final int COLUMN_INDEX_TOTAL_BYTES = 7;
        private static final int COLUMN_INDEX_TYPE = 1;
        private static final int COLUMN_INDEX_UPDATE_TIME_MS = 11;
        private static final int COLUMN_INDEX_URI = 2;
        private static final String COLUMN_SELECTION_ID = "id = ?";
        private static final String COLUMN_START_TIME_MS = "start_time_ms";
        private static final String COLUMN_STATE = "state";
        private static final String COLUMN_STOP_FLAGS = "stop_flags";
        private static final String COLUMN_STREAM_KEYS = "stream_keys";
        private static final String COLUMN_TOTAL_BYTES = "total_bytes";
        private static final String COLUMN_TYPE = "title";
        private static final String COLUMN_UPDATE_TIME_MS = "update_time_ms";
        private static final String COLUMN_URI = "subtitle";
        private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ExoPlayerDownloadStates (id TEXT PRIMARY KEY NOT NULL,title TEXT NOT NULL,subtitle TEXT NOT NULL,cache_key TEXT,state INTEGER NOT NULL,download_percentage REAL NOT NULL,downloaded_bytes INTEGER NOT NULL,total_bytes INTEGER NOT NULL,failure_reason INTEGER NOT NULL,stop_flags INTEGER NOT NULL,start_time_ms INTEGER NOT NULL,update_time_ms INTEGER NOT NULL,stream_keys TEXT NOT NULL,custom_metadata BLOB NOT NULL)";
        private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ExoPlayerDownloadStates";
        static final String TABLE_NAME = "ExoPlayerDownloadStates";
        static final int TABLE_VERSION = 1;
        private final DatabaseProvider databaseProvider;

        public DownloadStateTable(DatabaseProvider databaseProvider2) {
            this.databaseProvider = databaseProvider2;
            VersionTable versionTable = new VersionTable(databaseProvider2);
            int version = versionTable.getVersion(0);
            if (!DefaultDownloadIndex.doesTableExist(databaseProvider2, TABLE_NAME) || version == 0 || version > 1) {
                SQLiteDatabase writableDatabase = databaseProvider2.getWritableDatabase();
                writableDatabase.beginTransaction();
                try {
                    writableDatabase.execSQL(SQL_DROP_TABLE);
                    writableDatabase.execSQL(SQL_CREATE_TABLE);
                    versionTable.setVersion(0, 1);
                    writableDatabase.setTransactionSuccessful();
                } finally {
                    writableDatabase.endTransaction();
                }
            } else if (version < 1) {
                throw new IllegalStateException();
            }
        }

        public void replace(DownloadState downloadState) {
            ContentValues values = new ContentValues();
            values.put("id", downloadState.id);
            values.put(COLUMN_TYPE, downloadState.type);
            values.put(COLUMN_URI, downloadState.uri.toString());
            values.put(COLUMN_CACHE_KEY, downloadState.cacheKey);
            values.put("state", Integer.valueOf(downloadState.state));
            values.put(COLUMN_DOWNLOAD_PERCENTAGE, Float.valueOf(downloadState.downloadPercentage));
            values.put(COLUMN_DOWNLOADED_BYTES, Long.valueOf(downloadState.downloadedBytes));
            values.put(COLUMN_TOTAL_BYTES, Long.valueOf(downloadState.totalBytes));
            values.put(COLUMN_FAILURE_REASON, Integer.valueOf(downloadState.failureReason));
            values.put(COLUMN_STOP_FLAGS, Integer.valueOf(downloadState.stopFlags));
            values.put(COLUMN_START_TIME_MS, Long.valueOf(downloadState.startTimeMs));
            values.put(COLUMN_UPDATE_TIME_MS, Long.valueOf(downloadState.updateTimeMs));
            values.put(COLUMN_STREAM_KEYS, encodeStreamKeys(downloadState.streamKeys));
            values.put(COLUMN_CUSTOM_METADATA, downloadState.customMetadata);
            this.databaseProvider.getWritableDatabase().replace(TABLE_NAME, (String) null, values);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0033, code lost:
            if (r1 != null) goto L_0x0035;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0039, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x003a, code lost:
            r2.addSuppressed(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
            throw r3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public com.google.android.exoplayer2.offline.DownloadState get(java.lang.String r6) {
            /*
                r5 = this;
                r0 = 1
                java.lang.String[] r0 = new java.lang.String[r0]
                r1 = 0
                r0[r1] = r6
                java.lang.String r1 = "id = ?"
                android.database.Cursor r1 = r5.query(r1, r0)
                int r2 = r1.getCount()     // Catch:{ all -> 0x0030 }
                if (r2 != 0) goto L_0x0019
                r2 = 0
                if (r1 == 0) goto L_0x0018
                r1.close()
            L_0x0018:
                return r2
            L_0x0019:
                r1.moveToNext()     // Catch:{ all -> 0x0030 }
                com.google.android.exoplayer2.offline.DownloadState r2 = getDownloadState(r1)     // Catch:{ all -> 0x0030 }
                java.lang.String r3 = r2.id     // Catch:{ all -> 0x0030 }
                boolean r3 = r6.equals(r3)     // Catch:{ all -> 0x0030 }
                com.google.android.exoplayer2.util.Assertions.checkState(r3)     // Catch:{ all -> 0x0030 }
                if (r1 == 0) goto L_0x002f
                r1.close()
            L_0x002f:
                return r2
            L_0x0030:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x0032 }
            L_0x0032:
                r3 = move-exception
                if (r1 == 0) goto L_0x003d
                r1.close()     // Catch:{ all -> 0x0039 }
                goto L_0x003d
            L_0x0039:
                r4 = move-exception
                r2.addSuppressed(r4)
            L_0x003d:
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.DefaultDownloadIndex.DownloadStateTable.get(java.lang.String):com.google.android.exoplayer2.offline.DownloadState");
        }

        public DownloadStateCursor get(int... states) {
            String selection = null;
            if (states.length > 0) {
                StringBuilder selectionBuilder = new StringBuilder();
                selectionBuilder.append("state");
                selectionBuilder.append(" IN (");
                for (int i = 0; i < states.length; i++) {
                    if (i > 0) {
                        selectionBuilder.append(',');
                    }
                    selectionBuilder.append(states[i]);
                }
                selectionBuilder.append(')');
                selection = selectionBuilder.toString();
            }
            return new DownloadStateCursorImpl(query(selection, (String[]) null));
        }

        public void delete(String id) {
            this.databaseProvider.getWritableDatabase().delete(TABLE_NAME, COLUMN_SELECTION_ID, new String[]{id});
        }

        private Cursor query(String selection, String[] selectionArgs) {
            return this.databaseProvider.getReadableDatabase().query(TABLE_NAME, COLUMNS, selection, selectionArgs, (String) null, (String) null, "start_time_ms ASC");
        }

        /* access modifiers changed from: private */
        public static DownloadState getDownloadState(Cursor cursor) {
            Cursor cursor2 = cursor;
            return new DownloadState(cursor2.getString(0), cursor2.getString(1), Uri.parse(cursor2.getString(2)), cursor2.getString(3), cursor2.getInt(4), cursor2.getFloat(5), cursor2.getLong(6), cursor2.getLong(7), cursor2.getInt(8), cursor2.getInt(9), cursor2.getLong(10), cursor2.getLong(11), decodeStreamKeys(cursor2.getString(12)), cursor2.getBlob(13));
        }

        private static String encodeStreamKeys(StreamKey[] streamKeys) {
            StringBuilder stringBuilder = new StringBuilder();
            for (StreamKey streamKey : streamKeys) {
                stringBuilder.append(streamKey.periodIndex);
                stringBuilder.append('.');
                stringBuilder.append(streamKey.groupIndex);
                stringBuilder.append('.');
                stringBuilder.append(streamKey.trackIndex);
                stringBuilder.append(',');
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 1);
            }
            return stringBuilder.toString();
        }

        private static StreamKey[] decodeStreamKeys(String encodedStreamKeys) {
            if (encodedStreamKeys.isEmpty()) {
                return new StreamKey[0];
            }
            String[] streamKeysStrings = Util.split(encodedStreamKeys, ",");
            int streamKeysCount = streamKeysStrings.length;
            StreamKey[] streamKeys = new StreamKey[streamKeysCount];
            for (int i = 0; i < streamKeysCount; i++) {
                String[] indices = Util.split(streamKeysStrings[i], "\\.");
                Assertions.checkState(indices.length == 3);
                streamKeys[i] = new StreamKey(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]), Integer.parseInt(indices[2]));
            }
            return streamKeys;
        }
    }

    static final class VersionTable {
        private static final String COLUMN_FEATURE = "feature";
        private static final String COLUMN_VERSION = "version";
        public static final int FEATURE_CACHE = 1;
        public static final int FEATURE_OFFLINE = 0;
        private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ExoPlayerVersions (feature INTEGER PRIMARY KEY NOT NULL,version INTEGER NOT NULL)";
        private static final String TABLE_NAME = "ExoPlayerVersions";
        private final DatabaseProvider databaseProvider;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        private @interface Feature {
        }

        public VersionTable(DatabaseProvider databaseProvider2) {
            this.databaseProvider = databaseProvider2;
            if (!DefaultDownloadIndex.doesTableExist(databaseProvider2, TABLE_NAME)) {
                databaseProvider2.getWritableDatabase().execSQL(SQL_CREATE_TABLE);
            }
        }

        public void setVersion(int feature, int version) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FEATURE, Integer.valueOf(feature));
            values.put(COLUMN_VERSION, Integer.valueOf(version));
            this.databaseProvider.getWritableDatabase().replace(TABLE_NAME, (String) null, values);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0040, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
            if (r0 != null) goto L_0x0043;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
            r1.addSuppressed(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x004b, code lost:
            throw r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getVersion(int r11) {
            /*
                r10 = this;
                java.lang.String r8 = "feature = ?"
                r0 = 1
                java.lang.String[] r4 = new java.lang.String[r0]
                java.lang.String r0 = java.lang.Integer.toString(r11)
                r9 = 0
                r4[r9] = r0
                com.google.android.exoplayer2.offline.DefaultDownloadIndex$DatabaseProvider r0 = r10.databaseProvider
                android.database.sqlite.SQLiteDatabase r0 = r0.getReadableDatabase()
                java.lang.String r1 = "version"
                java.lang.String[] r2 = new java.lang.String[]{r1}
                java.lang.String r1 = "ExoPlayerVersions"
                r5 = 0
                r6 = 0
                r7 = 0
                r3 = r8
                android.database.Cursor r0 = r0.query(r1, r2, r3, r4, r5, r6, r7)
                int r1 = r0.getCount()     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0031
                if (r0 == 0) goto L_0x0030
                r0.close()
            L_0x0030:
                return r9
            L_0x0031:
                r0.moveToNext()     // Catch:{ all -> 0x003e }
                int r1 = r0.getInt(r9)     // Catch:{ all -> 0x003e }
                if (r0 == 0) goto L_0x003d
                r0.close()
            L_0x003d:
                return r1
            L_0x003e:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x0040 }
            L_0x0040:
                r2 = move-exception
                if (r0 == 0) goto L_0x004b
                r0.close()     // Catch:{ all -> 0x0047 }
                goto L_0x004b
            L_0x0047:
                r3 = move-exception
                r1.addSuppressed(r3)
            L_0x004b:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.DefaultDownloadIndex.VersionTable.getVersion(int):int");
        }
    }

    private static final class DefaultDatabaseProvider extends SQLiteOpenHelper implements DatabaseProvider {
        public DefaultDatabaseProvider(Context context) {
            super(context, DefaultDownloadIndex.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }

        public synchronized void close() {
            super.close();
        }

        public SQLiteDatabase getWritableDatabase() {
            return super.getWritableDatabase();
        }

        public SQLiteDatabase getReadableDatabase() {
            return super.getReadableDatabase();
        }
    }
}
