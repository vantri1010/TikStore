package com.litesuits.orm.db.assit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.log.OrmLog;

public class Querier {
    private static final String TAG = Querier.class.getSimpleName();

    public static <T> T doQuery(SQLiteDatabase db, SQLStatement st, CursorParser<T> parser) {
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.d(str, "----> Query Start: " + st.toString());
        }
        Cursor cursor = db.rawQuery(st.sql, (String[]) st.bindArgs);
        if (cursor != null) {
            parser.process(db, cursor);
            if (OrmLog.isPrint) {
                String str2 = TAG;
                OrmLog.d(str2, "<---- Query End , cursor size : " + cursor.getCount());
            }
        } else if (OrmLog.isPrint) {
            OrmLog.e(TAG, "<---- Query End : cursor is null");
        }
        return parser.returnResult();
    }

    public static abstract class CursorParser<T> {
        private boolean parse = true;

        public abstract void parseEachCursor(SQLiteDatabase sQLiteDatabase, Cursor cursor) throws Exception;

        public final void process(SQLiteDatabase db, Cursor cursor) {
            try {
                cursor.moveToFirst();
                while (this.parse && !cursor.isAfterLast()) {
                    parseEachCursor(db, cursor);
                    cursor.moveToNext();
                }
                if (cursor == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (cursor == null) {
                    return;
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
            cursor.close();
        }

        public final void stopParse() {
            this.parse = false;
        }

        public T returnResult() {
            return null;
        }
    }
}
