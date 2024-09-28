package com.litesuits.orm.db.assit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private OnUpdateListener onUpdateListener;

    public interface OnUpdateListener {
        void onUpdate(SQLiteDatabase sQLiteDatabase, int i, int i2);
    }

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, OnUpdateListener onUpdateListener2) {
        super(context, name, factory, version);
        this.onUpdateListener = onUpdateListener2;
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        OnUpdateListener onUpdateListener2 = this.onUpdateListener;
        if (onUpdateListener2 != null) {
            onUpdateListener2.onUpdate(db, oldVersion, newVersion);
        }
    }
}
