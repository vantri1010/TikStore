package com.litesuits.orm.db;

import android.content.Context;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.SQLiteHelper;

public class DataBaseConfig {
    public static final String DEFAULT_DB_NAME = "liteorm.db";
    public static final int DEFAULT_DB_VERSION = 1;
    public Context context;
    public String dbName;
    public int dbVersion;
    public boolean debugged;
    public SQLiteHelper.OnUpdateListener onUpdateListener;

    public DataBaseConfig(Context context2) {
        this(context2, DEFAULT_DB_NAME);
    }

    public DataBaseConfig(Context context2, String dbName2) {
        this(context2, dbName2, false, 1, (SQLiteHelper.OnUpdateListener) null);
    }

    public DataBaseConfig(Context context2, String dbName2, boolean debugged2, int dbVersion2, SQLiteHelper.OnUpdateListener onUpdateListener2) {
        this.debugged = false;
        this.dbName = DEFAULT_DB_NAME;
        this.dbVersion = 1;
        this.context = context2.getApplicationContext();
        if (!Checker.isEmpty((CharSequence) dbName2)) {
            this.dbName = dbName2;
        }
        if (dbVersion2 > 1) {
            this.dbVersion = dbVersion2;
        }
        this.debugged = debugged2;
        this.onUpdateListener = onUpdateListener2;
    }

    public String toString() {
        return "DataBaseConfig [mContext=" + this.context + ", mDbName=" + this.dbName + ", mDbVersion=" + this.dbVersion + ", mOnUpdateListener=" + this.onUpdateListener + "]";
    }
}
