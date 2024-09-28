package com.litesuits.orm.db.impl;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.CollSpliter;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;
import java.util.ArrayList;
import java.util.Collection;

public final class SingleSQLiteImpl extends LiteOrm {
    public static final String TAG = SingleSQLiteImpl.class.getSimpleName();

    protected SingleSQLiteImpl(LiteOrm dataBase) {
        super(dataBase);
    }

    private SingleSQLiteImpl(DataBaseConfig config) {
        super(config);
    }

    public static synchronized LiteOrm newInstance(DataBaseConfig config) {
        SingleSQLiteImpl singleSQLiteImpl;
        synchronized (SingleSQLiteImpl.class) {
            singleSQLiteImpl = new SingleSQLiteImpl(config);
        }
        return singleSQLiteImpl;
    }

    public LiteOrm single() {
        return this;
    }

    public LiteOrm cascade() {
        if (this.otherDatabase == null) {
            this.otherDatabase = new CascadeSQLiteImpl((LiteOrm) this);
        }
        return this.otherDatabase;
    }

    /* JADX INFO: finally extract failed */
    public long save(Object entity) {
        acquireReference();
        try {
            SQLiteDatabase db = this.mHelper.getWritableDatabase();
            this.mTableManager.checkOrCreateTable(db, entity);
            long execInsert = SQLBuilder.buildReplaceSql(entity).execInsert(db, entity);
            releaseReference();
            return execInsert;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public <T> int save(Collection<T> collection) {
        acquireReference();
        try {
            if (!Checker.isEmpty((Collection<?>) collection)) {
                SQLiteDatabase db = this.mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                this.mTableManager.checkOrCreateTable(db, entity);
                int execInsertCollection = SQLBuilder.buildReplaceAllSql(entity).execInsertCollection(db, collection);
                releaseReference();
                return execInsertCollection;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return -1;
    }

    public long insert(Object entity) {
        return insert(entity, (ConflictAlgorithm) null);
    }

    /* JADX INFO: finally extract failed */
    public long insert(Object entity, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = this.mHelper.getWritableDatabase();
            this.mTableManager.checkOrCreateTable(db, entity);
            long execInsert = SQLBuilder.buildInsertSql(entity, conflictAlgorithm).execInsert(db, entity);
            releaseReference();
            return execInsert;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public <T> int insert(Collection<T> collection) {
        return insert(collection, (ConflictAlgorithm) null);
    }

    public <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            if (!Checker.isEmpty((Collection<?>) collection)) {
                SQLiteDatabase db = this.mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                SQLStatement stmt = SQLBuilder.buildInsertAllSql(entity, conflictAlgorithm);
                this.mTableManager.checkOrCreateTable(db, entity);
                int execInsertCollection = stmt.execInsertCollection(db, collection);
                releaseReference();
                return execInsertCollection;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return -1;
    }

    public int update(Object entity) {
        return update(entity, (ColumnsValue) null, (ConflictAlgorithm) null);
    }

    public int update(Object entity, ConflictAlgorithm conflictAlgorithm) {
        return update(entity, (ColumnsValue) null, conflictAlgorithm);
    }

    /* JADX INFO: finally extract failed */
    public int update(Object entity, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            SQLiteDatabase db = this.mHelper.getWritableDatabase();
            this.mTableManager.checkOrCreateTable(db, entity);
            int execUpdate = SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm).execUpdate(db);
            releaseReference();
            return execUpdate;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public <T> int update(Collection<T> collection) {
        return update(collection, (ColumnsValue) null, (ConflictAlgorithm) null);
    }

    public <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        return update(collection, (ColumnsValue) null, conflictAlgorithm);
    }

    public <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            if (!Checker.isEmpty((Collection<?>) collection)) {
                SQLiteDatabase db = this.mHelper.getWritableDatabase();
                Object entity = collection.iterator().next();
                this.mTableManager.checkOrCreateTable(db, entity);
                int execUpdateCollection = SQLBuilder.buildUpdateAllSql(entity, cvs, conflictAlgorithm).execUpdateCollection(db, collection, cvs);
                releaseReference();
                return execUpdateCollection;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return -1;
    }

    public int delete(Object entity) {
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(entity).name)) {
            return -1;
        }
        acquireReference();
        try {
            return SQLBuilder.buildDeleteSql(entity).execDelete(this.mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            releaseReference();
        }
    }

    public <T> int delete(Class<T> claxx) {
        return deleteAll(claxx);
    }

    public <T> int delete(Collection<T> collection) {
        Throwable th;
        acquireReference();
        try {
            if (!Checker.isEmpty((Collection<?>) collection)) {
                if (this.mTableManager.isSQLTableCreated(TableManager.getTable((Object) collection.iterator().next()).name)) {
                    final SQLiteDatabase db = this.mHelper.getWritableDatabase();
                    db.beginTransaction();
                    try {
                        int rows = CollSpliter.split(collection, 999, new CollSpliter.Spliter<T>() {
                            public int oneSplit(ArrayList<T> list) throws Exception {
                                return SQLBuilder.buildDeleteSql((Collection<?>) list).execDeleteCollection(db, list);
                            }
                        });
                        try {
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            releaseReference();
                            return rows;
                        } catch (Throwable th2) {
                            th = th2;
                            db.endTransaction();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        db.endTransaction();
                        throw th;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th4) {
            releaseReference();
            throw th4;
        }
        releaseReference();
        return -1;
    }

    @Deprecated
    public <T> int delete(Class<T> cls, WhereBuilder where) {
        return delete(where);
    }

    public int delete(WhereBuilder where) {
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(where.getTableClass(), false).name)) {
            return -1;
        }
        acquireReference();
        try {
            return where.createStatementDelete().execDelete(this.mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            releaseReference();
        }
    }

    public <T> int deleteAll(Class<T> claxx) {
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(claxx, false).name)) {
            return -1;
        }
        acquireReference();
        try {
            return SQLBuilder.buildDeleteAllSql(claxx).execDelete(this.mHelper.getWritableDatabase());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            releaseReference();
        }
    }

    public <T> int delete(Class<T> claxx, long start, long end, String orderAscColumn) {
        Exception e;
        long end2;
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(claxx, false).name)) {
            return -1;
        }
        acquireReference();
        if (start < 0 || end < start) {
            try {
                throw new RuntimeException("start must >=0 and smaller than end");
            } catch (Exception e2) {
                end2 = end;
                e = e2;
                try {
                    e.printStackTrace();
                    releaseReference();
                    long j = end2;
                    return -1;
                } catch (Throwable th) {
                    th = th;
                    releaseReference();
                    throw th;
                }
            } catch (Throwable th2) {
                long j2 = end;
                th = th2;
                releaseReference();
                throw th;
            }
        } else {
            if (start != 0) {
                start--;
            }
            end2 = end == 2147483647L ? -1 : end - start;
            try {
                int execDelete = SQLBuilder.buildDeleteSql(claxx, start, end2, orderAscColumn).execDelete(this.mHelper.getWritableDatabase());
                releaseReference();
                return execDelete;
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
                releaseReference();
                long j3 = end2;
                return -1;
            }
        }
    }

    public <T> ArrayList<T> query(Class<T> claxx) {
        return query(new QueryBuilder(claxx));
    }

    public <T> ArrayList<T> query(QueryBuilder<T> qb) {
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(qb.getQueryClass(), false).name)) {
            return new ArrayList<>();
        }
        acquireReference();
        try {
            return qb.createStatement().query(this.mHelper.getReadableDatabase(), qb.getQueryClass());
        } finally {
            releaseReference();
        }
    }

    public <T> T queryById(long id, Class<T> claxx) {
        return queryById(String.valueOf(id), claxx);
    }

    public <T> T queryById(String id, Class<T> claxx) {
        EntityTable table = TableManager.getTable(claxx, false);
        if (!this.mTableManager.isSQLTableCreated(table.name)) {
            return null;
        }
        acquireReference();
        try {
            QueryBuilder queryBuilder = new QueryBuilder(claxx);
            ArrayList<T> list = queryBuilder.where(table.key.column + "=?", id).createStatement().query(this.mHelper.getReadableDatabase(), claxx);
            if (!Checker.isEmpty((Collection<?>) list)) {
                return list.get(0);
            }
            releaseReference();
            return null;
        } finally {
            releaseReference();
        }
    }
}
