package com.google.android.datatransport.runtime.scheduling.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
final class SchemaManager extends SQLiteOpenHelper {
    private static final String CREATE_CONTEXTS_SQL_V1 = "CREATE TABLE transport_contexts (_id INTEGER PRIMARY KEY, backend_name TEXT NOT NULL, priority INTEGER NOT NULL, next_request_ms INTEGER NOT NULL)";
    private static final String CREATE_CONTEXT_BACKEND_PRIORITY_INDEX_V1 = "CREATE UNIQUE INDEX contexts_backend_priority on transport_contexts(backend_name, priority)";
    private static final String CREATE_EVENTS_SQL_V1 = "CREATE TABLE events (_id INTEGER PRIMARY KEY, context_id INTEGER NOT NULL, transport_name TEXT NOT NULL, timestamp_ms INTEGER NOT NULL, uptime_ms INTEGER NOT NULL, payload BLOB NOT NULL, code INTEGER, num_attempts INTEGER NOT NULL,FOREIGN KEY (context_id) REFERENCES transport_contexts(_id) ON DELETE CASCADE)";
    private static final String CREATE_EVENT_BACKEND_INDEX_V1 = "CREATE INDEX events_backend_id on events(context_id)";
    private static final String CREATE_EVENT_METADATA_SQL_V1 = "CREATE TABLE event_metadata (_id INTEGER PRIMARY KEY, event_id INTEGER NOT NULL, name TEXT NOT NULL, value TEXT NOT NULL,FOREIGN KEY (event_id) REFERENCES events(_id) ON DELETE CASCADE)";
    private static final String DB_NAME = "com.google.android.datatransport.events";
    private static final String DROP_CONTEXTS_SQL = "DROP TABLE transport_contexts";
    private static final String DROP_EVENTS_SQL = "DROP TABLE events";
    private static final String DROP_EVENT_METADATA_SQL = "DROP TABLE event_metadata";
    private static final List<Migration> INCREMENTAL_MIGRATIONS;
    private static final Migration MIGRATE_TO_V1 = SchemaManager$$Lambda$1.lambdaFactory$();
    private static final Migration MIGRATE_TO_V2 = SchemaManager$$Lambda$2.lambdaFactory$();
    private static final Migration MIGRATE_TO_V3;
    static int SCHEMA_VERSION = 3;
    private boolean configured = false;
    private final int schemaVersion;

    /* compiled from: com.google.android.datatransport:transport-runtime@@2.2.0 */
    public interface Migration {
        void upgrade(SQLiteDatabase sQLiteDatabase);
    }

    static {
        Migration lambdaFactory$ = SchemaManager$$Lambda$3.lambdaFactory$();
        MIGRATE_TO_V3 = lambdaFactory$;
        INCREMENTAL_MIGRATIONS = Arrays.asList(new Migration[]{MIGRATE_TO_V1, MIGRATE_TO_V2, lambdaFactory$});
    }

    static /* synthetic */ void lambda$static$0(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_SQL_V1);
        db.execSQL(CREATE_EVENT_METADATA_SQL_V1);
        db.execSQL(CREATE_CONTEXTS_SQL_V1);
        db.execSQL(CREATE_EVENT_BACKEND_INDEX_V1);
        db.execSQL(CREATE_CONTEXT_BACKEND_PRIORITY_INDEX_V1);
    }

    static /* synthetic */ void lambda$static$1(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE transport_contexts ADD COLUMN extras BLOB");
        db.execSQL("CREATE UNIQUE INDEX contexts_backend_priority_extras on transport_contexts(backend_name, priority, extras)");
        db.execSQL("DROP INDEX contexts_backend_priority");
    }

    @Inject
    SchemaManager(Context context, @Named("SCHEMA_VERSION") int schemaVersion2) {
        super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, schemaVersion2);
        this.schemaVersion = schemaVersion2;
    }

    public void onConfigure(SQLiteDatabase db) {
        this.configured = true;
        db.rawQuery("PRAGMA busy_timeout=0;", new String[0]).close();
        if (Build.VERSION.SDK_INT >= 16) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    private void ensureConfigured(SQLiteDatabase db) {
        if (!this.configured) {
            onConfigure(db);
        }
    }

    public void onCreate(SQLiteDatabase db) {
        ensureConfigured(db);
        upgrade(db, 0, this.schemaVersion);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ensureConfigured(db);
        upgrade(db, oldVersion, newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_SQL);
        db.execSQL(DROP_EVENT_METADATA_SQL);
        db.execSQL(DROP_CONTEXTS_SQL);
        onCreate(db);
    }

    public void onOpen(SQLiteDatabase db) {
        ensureConfigured(db);
    }

    private void upgrade(SQLiteDatabase db, int fromVersion, int toVersion) {
        if (toVersion <= INCREMENTAL_MIGRATIONS.size()) {
            for (int version = fromVersion; version < toVersion; version++) {
                INCREMENTAL_MIGRATIONS.get(version).upgrade(db);
            }
            return;
        }
        throw new IllegalArgumentException("Migration from " + fromVersion + " to " + toVersion + " was requested, but cannot be performed. Only " + INCREMENTAL_MIGRATIONS.size() + " migrations are provided");
    }
}
