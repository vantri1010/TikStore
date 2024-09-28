package com.litesuits.orm.db.assit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.Transaction;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class SQLStatement implements Serializable {
    public static final int IN_TOP_LIMIT = 999;
    public static final short NONE = -1;
    public static final short NORMAL = 0;
    /* access modifiers changed from: private */
    public static final String TAG = SQLStatement.class.getSimpleName();
    private static final long serialVersionUID = -3790876762607683712L;
    public Object[] bindArgs;
    private SQLiteStatement mStatement;
    public String sql;

    public SQLStatement() {
    }

    public SQLStatement(String sql2, Object[] args) {
        this.sql = sql2;
        this.bindArgs = args;
    }

    /* access modifiers changed from: protected */
    public void bind(int i, Object o) throws IOException {
        if (o == null) {
            this.mStatement.bindNull(i);
        } else if ((o instanceof CharSequence) || (o instanceof Boolean) || (o instanceof Character)) {
            this.mStatement.bindString(i, String.valueOf(o));
        } else if ((o instanceof Float) || (o instanceof Double)) {
            this.mStatement.bindDouble(i, ((Number) o).doubleValue());
        } else if (o instanceof Number) {
            this.mStatement.bindLong(i, ((Number) o).longValue());
        } else if (o instanceof Date) {
            this.mStatement.bindLong(i, ((Date) o).getTime());
        } else if (o instanceof byte[]) {
            this.mStatement.bindBlob(i, (byte[]) o);
        } else if (o instanceof Serializable) {
            this.mStatement.bindBlob(i, DataUtil.objectToByte(o));
        } else {
            this.mStatement.bindNull(i);
        }
    }

    public long execInsert(SQLiteDatabase db) throws IOException, IllegalAccessException {
        return execInsertWithMapping(db, (Object) null, (TableManager) null);
    }

    public long execInsert(SQLiteDatabase db, Object entity) throws IOException, IllegalAccessException {
        return execInsertWithMapping(db, entity, (TableManager) null);
    }

    /* JADX INFO: finally extract failed */
    public long execInsertWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws IllegalAccessException, IOException {
        printSQL();
        this.mStatement = db.compileStatement(this.sql);
        Object keyObj = null;
        if (!Checker.isEmpty(this.bindArgs)) {
            keyObj = this.bindArgs[0];
            int i = 0;
            while (true) {
                Object[] objArr = this.bindArgs;
                if (i >= objArr.length) {
                    break;
                }
                bind(i + 1, objArr[i]);
                i++;
            }
        }
        try {
            long rowID = this.mStatement.executeInsert();
            realease();
            if (OrmLog.isPrint) {
                String str = TAG;
                OrmLog.i(str, "SQL Execute Insert RowID --> " + rowID + "    sql: " + this.sql);
            }
            if (entity != null) {
                FieldUtil.setKeyValueIfneed(entity, TableManager.getTable(entity).key, keyObj, rowID);
            }
            if (tableManager != null) {
                mapRelationToDb(entity, true, true, db, tableManager);
            }
            return rowID;
        } catch (Throwable th) {
            realease();
            throw th;
        }
    }

    public int execInsertCollection(SQLiteDatabase db, Collection<?> list) {
        return execInsertCollectionWithMapping(db, list, (TableManager) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x00f6 A[Catch:{ all -> 0x00e7 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int execInsertCollectionWithMapping(android.database.sqlite.SQLiteDatabase r17, java.util.Collection<?> r18, com.litesuits.orm.db.TableManager r19) {
        /*
            r16 = this;
            r7 = r16
            r16.printSQL()
            r17.beginTransaction()
            boolean r0 = com.litesuits.orm.log.OrmLog.isPrint
            if (r0 == 0) goto L_0x0013
            java.lang.String r0 = TAG
            java.lang.String r1 = "----> BeginTransaction[insert col]"
            com.litesuits.orm.log.OrmLog.i((java.lang.String) r0, (java.lang.String) r1)
        L_0x0013:
            r1 = 0
            java.lang.String r0 = r7.sql     // Catch:{ Exception -> 0x00ef, all -> 0x00eb }
            r8 = r17
            android.database.sqlite.SQLiteStatement r0 = r8.compileStatement(r0)     // Catch:{ Exception -> 0x00e9 }
            r7.mStatement = r0     // Catch:{ Exception -> 0x00e9 }
            java.util.Iterator r0 = r18.iterator()     // Catch:{ Exception -> 0x00e9 }
            r2 = 1
            r9 = r2
        L_0x0024:
            boolean r2 = r0.hasNext()     // Catch:{ Exception -> 0x00e9 }
            if (r2 == 0) goto L_0x00a6
            android.database.sqlite.SQLiteStatement r2 = r7.mStatement     // Catch:{ Exception -> 0x00e9 }
            r2.clearBindings()     // Catch:{ Exception -> 0x00e9 }
            java.lang.Object r2 = r0.next()     // Catch:{ Exception -> 0x00e9 }
            r10 = r2
            if (r1 != 0) goto L_0x003d
            com.litesuits.orm.db.model.EntityTable r2 = com.litesuits.orm.db.TableManager.getTable((java.lang.Object) r10)     // Catch:{ Exception -> 0x00e9 }
            r1 = r2
            r11 = r1
            goto L_0x003e
        L_0x003d:
            r11 = r1
        L_0x003e:
            r1 = 1
            r2 = 0
            com.litesuits.orm.db.model.Primarykey r3 = r11.key     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            if (r3 == 0) goto L_0x0053
            com.litesuits.orm.db.model.Primarykey r3 = r11.key     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            java.lang.Object r3 = com.litesuits.orm.db.utils.FieldUtil.getAssignedKeyObject(r3, r10)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r2 = r3
            int r3 = r1 + 1
            r7.bind(r1, r2)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r12 = r2
            r1 = r3
            goto L_0x0054
        L_0x0053:
            r12 = r2
        L_0x0054:
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r2 = r11.pmap     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            boolean r2 = com.litesuits.orm.db.assit.Checker.isEmpty((java.util.Map<?, ?>) r2)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            if (r2 != 0) goto L_0x0081
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r2 = r11.pmap     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            java.util.Collection r2 = r2.values()     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
        L_0x0066:
            boolean r3 = r2.hasNext()     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            if (r3 == 0) goto L_0x007f
            java.lang.Object r3 = r2.next()     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            com.litesuits.orm.db.model.Property r3 = (com.litesuits.orm.db.model.Property) r3     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            int r4 = r1 + 1
            java.lang.reflect.Field r5 = r3.field     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            java.lang.Object r5 = com.litesuits.orm.db.utils.FieldUtil.get(r5, r10)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r7.bind(r1, r5)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r1 = r4
            goto L_0x0066
        L_0x007f:
            r13 = r1
            goto L_0x0082
        L_0x0081:
            r13 = r1
        L_0x0082:
            android.database.sqlite.SQLiteStatement r1 = r7.mStatement     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            long r1 = r1.executeInsert()     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r14 = r1
            com.litesuits.orm.db.model.Primarykey r1 = r11.key     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            com.litesuits.orm.db.utils.FieldUtil.setKeyValueIfneed(r10, r1, r12, r14)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            if (r19 == 0) goto L_0x009e
            r3 = 1
            r1 = r16
            r2 = r10
            r4 = r9
            r5 = r17
            r6 = r19
            r1.mapRelationToDb(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x00a3, all -> 0x00a0 }
            r1 = 0
            r9 = r1
        L_0x009e:
            r1 = r11
            goto L_0x0024
        L_0x00a0:
            r0 = move-exception
            r1 = r11
            goto L_0x0109
        L_0x00a3:
            r0 = move-exception
            r1 = r11
            goto L_0x00f2
        L_0x00a6:
            boolean r2 = com.litesuits.orm.log.OrmLog.isPrint     // Catch:{ Exception -> 0x00e9 }
            if (r2 == 0) goto L_0x00ce
            java.lang.String r2 = TAG     // Catch:{ Exception -> 0x00e9 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e9 }
            r3.<init>()     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r4 = "Exec insert ["
            r3.append(r4)     // Catch:{ Exception -> 0x00e9 }
            int r4 = r18.size()     // Catch:{ Exception -> 0x00e9 }
            r3.append(r4)     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r4 = "] rows , SQL: "
            r3.append(r4)     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r4 = r7.sql     // Catch:{ Exception -> 0x00e9 }
            r3.append(r4)     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x00e9 }
            com.litesuits.orm.log.OrmLog.i((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ Exception -> 0x00e9 }
        L_0x00ce:
            r17.setTransactionSuccessful()     // Catch:{ Exception -> 0x00e9 }
            boolean r2 = com.litesuits.orm.log.OrmLog.isPrint     // Catch:{ Exception -> 0x00e9 }
            if (r2 == 0) goto L_0x00dc
            java.lang.String r2 = TAG     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r3 = "----> BeginTransaction[insert col] Successful"
            com.litesuits.orm.log.OrmLog.i((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ Exception -> 0x00e9 }
        L_0x00dc:
            int r2 = r18.size()     // Catch:{ Exception -> 0x00e9 }
            r16.realease()
            r17.endTransaction()
            return r2
        L_0x00e7:
            r0 = move-exception
            goto L_0x0109
        L_0x00e9:
            r0 = move-exception
            goto L_0x00f2
        L_0x00eb:
            r0 = move-exception
            r8 = r17
            goto L_0x0109
        L_0x00ef:
            r0 = move-exception
            r8 = r17
        L_0x00f2:
            boolean r2 = com.litesuits.orm.log.OrmLog.isPrint     // Catch:{ all -> 0x00e7 }
            if (r2 == 0) goto L_0x00fd
            java.lang.String r2 = TAG     // Catch:{ all -> 0x00e7 }
            java.lang.String r3 = "----> BeginTransaction[insert col] Failling"
            com.litesuits.orm.log.OrmLog.e((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ all -> 0x00e7 }
        L_0x00fd:
            r0.printStackTrace()     // Catch:{ all -> 0x00e7 }
            r16.realease()
            r17.endTransaction()
            r0 = -1
            return r0
        L_0x0109:
            r16.realease()
            r17.endTransaction()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.litesuits.orm.db.assit.SQLStatement.execInsertCollectionWithMapping(android.database.sqlite.SQLiteDatabase, java.util.Collection, com.litesuits.orm.db.TableManager):int");
    }

    public int execUpdate(SQLiteDatabase db) throws IOException {
        return execUpdateWithMapping(db, (Object) null, (TableManager) null);
    }

    public int execUpdateWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws IOException {
        int rows;
        printSQL();
        this.mStatement = db.compileStatement(this.sql);
        if (!Checker.isEmpty(this.bindArgs)) {
            int i = 0;
            while (true) {
                Object[] objArr = this.bindArgs;
                if (i >= objArr.length) {
                    break;
                }
                bind(i + 1, objArr[i]);
                i++;
            }
        }
        if (Build.VERSION.SDK_INT < 11) {
            this.mStatement.execute();
            rows = 0;
        } else {
            rows = this.mStatement.executeUpdateDelete();
        }
        realease();
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.i(str, "SQL Execute update, changed rows --> " + rows);
        }
        if (!(tableManager == null || entity == null)) {
            mapRelationToDb(entity, true, true, db, tableManager);
        }
        return rows;
    }

    public int execUpdateCollection(SQLiteDatabase db, Collection<?> list, ColumnsValue cvs) {
        return execUpdateCollectionWithMapping(db, list, cvs, (TableManager) null);
    }

    /* JADX INFO: finally extract failed */
    public int execUpdateCollectionWithMapping(SQLiteDatabase db, Collection<?> list, ColumnsValue cvs, TableManager tableManager) {
        printSQL();
        db.beginTransaction();
        if (OrmLog.isPrint) {
            OrmLog.d(TAG, "----> BeginTransaction[update col]");
        }
        try {
            this.mStatement = db.compileStatement(this.sql);
            boolean mapTableCheck = true;
            EntityTable table = null;
            for (Object obj : list) {
                this.mStatement.clearBindings();
                if (table == null) {
                    table = TableManager.getTable(obj);
                }
                Object[] buildUpdateSqlArgsOnly = SQLBuilder.buildUpdateSqlArgsOnly(obj, cvs);
                this.bindArgs = buildUpdateSqlArgsOnly;
                if (!Checker.isEmpty(buildUpdateSqlArgsOnly)) {
                    for (int i = 0; i < this.bindArgs.length; i++) {
                        bind(i + 1, this.bindArgs[i]);
                    }
                }
                this.mStatement.execute();
                if (tableManager != null) {
                    mapRelationToDb(obj, true, mapTableCheck, db, tableManager);
                    mapTableCheck = false;
                }
            }
            if (OrmLog.isPrint) {
                String str = TAG;
                OrmLog.i(str, "Exec update [" + list.size() + "] rows , SQL: " + this.sql);
            }
            db.setTransactionSuccessful();
            if (OrmLog.isPrint) {
                OrmLog.d(TAG, "----> BeginTransaction[update col] Successful");
            }
            int size = list.size();
            realease();
            db.endTransaction();
            return size;
        } catch (Exception e) {
            if (OrmLog.isPrint) {
                OrmLog.e(TAG, "----> BeginTransaction[update col] Failling");
            }
            e.printStackTrace();
            realease();
            db.endTransaction();
            return -1;
        } catch (Throwable th) {
            realease();
            db.endTransaction();
            throw th;
        }
    }

    public int execDelete(SQLiteDatabase db) throws IOException {
        return execDeleteWithMapping(db, (Object) null, (TableManager) null);
    }

    public int execDeleteWithMapping(SQLiteDatabase db, Object entity, TableManager tableManager) throws IOException {
        int nums;
        printSQL();
        this.mStatement = db.compileStatement(this.sql);
        if (this.bindArgs != null) {
            int i = 0;
            while (true) {
                Object[] objArr = this.bindArgs;
                if (i >= objArr.length) {
                    break;
                }
                bind(i + 1, objArr[i]);
                i++;
            }
        }
        if (Build.VERSION.SDK_INT < 11) {
            this.mStatement.execute();
            nums = 0;
        } else {
            nums = this.mStatement.executeUpdateDelete();
        }
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.v(str, "SQL execute delete, changed rows--> " + nums);
        }
        realease();
        if (!(tableManager == null || entity == null)) {
            mapRelationToDb(entity, false, false, db, tableManager);
        }
        return nums;
    }

    public int execDeleteCollection(SQLiteDatabase db, Collection<?> collection) throws IOException {
        return execDeleteCollectionWithMapping(db, collection, (TableManager) null);
    }

    public int execDeleteCollectionWithMapping(SQLiteDatabase db, final Collection<?> collection, final TableManager tableManager) throws IOException {
        int nums;
        printSQL();
        this.mStatement = db.compileStatement(this.sql);
        if (this.bindArgs != null) {
            int i = 0;
            while (true) {
                Object[] objArr = this.bindArgs;
                if (i >= objArr.length) {
                    break;
                }
                bind(i + 1, objArr[i]);
                i++;
            }
        }
        if (Build.VERSION.SDK_INT < 11) {
            this.mStatement.execute();
            nums = collection.size();
        } else {
            nums = this.mStatement.executeUpdateDelete();
        }
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.v(str, "SQL execute delete, changed rows --> " + nums);
        }
        realease();
        if (tableManager != null) {
            Boolean suc = (Boolean) Transaction.execute(db, new Transaction.Worker<Boolean>() {
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    boolean mapTableCheck = true;
                    for (Object o : collection) {
                        SQLStatement.this.mapRelationToDb(o, false, mapTableCheck, db, tableManager);
                        mapTableCheck = false;
                    }
                    return true;
                }
            });
            if (OrmLog.isPrint) {
                String str2 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Exec delete collection mapping: ");
                sb.append((suc == null || !suc.booleanValue()) ? "失败" : "成功");
                OrmLog.i(str2, sb.toString());
            }
        }
        return nums;
    }

    public boolean execute(SQLiteDatabase db) {
        printSQL();
        try {
            this.mStatement = db.compileStatement(this.sql);
            if (this.bindArgs != null) {
                for (int i = 0; i < this.bindArgs.length; i++) {
                    bind(i + 1, this.bindArgs[i]);
                }
            }
            this.mStatement.execute();
            realease();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            realease();
            return false;
        } catch (Throwable th) {
            realease();
            throw th;
        }
    }

    public long queryForLong(SQLiteDatabase db) {
        printSQL();
        long count = 0;
        try {
            this.mStatement = db.compileStatement(this.sql);
            if (this.bindArgs != null) {
                for (int i = 0; i < this.bindArgs.length; i++) {
                    bind(i + 1, this.bindArgs[i]);
                }
            }
            count = this.mStatement.simpleQueryForLong();
            if (OrmLog.isPrint) {
                String str = TAG;
                OrmLog.i(str, "SQL execute query for count --> " + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            realease();
            throw th;
        }
        realease();
        return count;
    }

    public <T> ArrayList<T> query(SQLiteDatabase db, final Class<T> claxx) {
        printSQL();
        final ArrayList<T> list = new ArrayList<>();
        try {
            final EntityTable table = TableManager.getTable(claxx, false);
            Querier.doQuery(db, this, new Querier.CursorParser() {
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    T t = ClassUtil.newInstance(claxx);
                    DataUtil.injectDataToObject(c, t, table);
                    list.add(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public <T> T queryOneEntity(SQLiteDatabase db, final Class<T> claxx) {
        printSQL();
        final EntityTable table = TableManager.getTable(claxx, false);
        return Querier.doQuery(db, this, new Querier.CursorParser<T>() {
            T t;

            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                T newInstance = ClassUtil.newInstance(claxx);
                this.t = newInstance;
                DataUtil.injectDataToObject(c, newInstance, table);
                stopParse();
            }

            public T returnResult() {
                return this.t;
            }
        });
    }

    public String toString() {
        return "SQLStatement [sql=" + this.sql + ", bindArgs=" + Arrays.toString(this.bindArgs) + ", mStatement=" + this.mStatement + "]";
    }

    /* access modifiers changed from: private */
    public void mapRelationToDb(Object entity, boolean insertNew, boolean tableCheck, SQLiteDatabase db, TableManager tableManager) {
        MapInfo mapTable = SQLBuilder.buildMappingInfo(entity, insertNew, tableManager);
        if (mapTable != null && !mapTable.isEmpty()) {
            final boolean z = insertNew;
            final boolean z2 = tableCheck;
            final MapInfo mapInfo = mapTable;
            final TableManager tableManager2 = tableManager;
            Transaction.execute(db, new Transaction.Worker<Boolean>() {
                public Boolean doTransaction(SQLiteDatabase db) throws Exception {
                    if (z && z2) {
                        Iterator i$ = mapInfo.tableList.iterator();
                        while (i$.hasNext()) {
                            MapInfo.MapTable table = i$.next();
                            tableManager2.checkOrCreateMappingTable(db, table.name, table.column1, table.column2);
                        }
                    }
                    if (mapInfo.delOldRelationSQL != null) {
                        Iterator i$2 = mapInfo.delOldRelationSQL.iterator();
                        while (i$2.hasNext()) {
                            long rowId = (long) i$2.next().execDelete(db);
                            if (OrmLog.isPrint) {
                                String access$100 = SQLStatement.TAG;
                                OrmLog.v(access$100, "Exec delete mapping success, nums: " + rowId);
                            }
                        }
                    }
                    if (z && mapInfo.mapNewRelationSQL != null) {
                        Iterator i$3 = mapInfo.mapNewRelationSQL.iterator();
                        while (i$3.hasNext()) {
                            long rowId2 = i$3.next().execInsert(db);
                            if (OrmLog.isPrint) {
                                String access$1002 = SQLStatement.TAG;
                                OrmLog.v(access$1002, "Exec save mapping success, nums: " + rowId2);
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }

    private void printSQL() {
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.d(str, "SQL Execute: [" + this.sql + "] ARGS--> " + Arrays.toString(this.bindArgs));
        }
    }

    private void realease() {
        SQLiteStatement sQLiteStatement = this.mStatement;
        if (sQLiteStatement != null) {
            sQLiteStatement.close();
        }
        this.bindArgs = null;
        this.mStatement = null;
    }
}
