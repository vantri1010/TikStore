package com.litesuits.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteClosable;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.CollSpliter;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.impl.CascadeSQLiteImpl;
import com.litesuits.orm.db.impl.SingleSQLiteImpl;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.RelationKey;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class LiteOrm extends SQLiteClosable implements DataBase {
    public static final String TAG = LiteOrm.class.getSimpleName();
    protected DataBaseConfig mConfig;
    protected SQLiteHelper mHelper;
    /* access modifiers changed from: protected */
    public TableManager mTableManager;
    protected LiteOrm otherDatabase;

    public abstract LiteOrm cascade();

    public abstract LiteOrm single();

    protected LiteOrm(LiteOrm dataBase) {
        this.mHelper = dataBase.mHelper;
        this.mConfig = dataBase.mConfig;
        this.mTableManager = dataBase.mTableManager;
        this.otherDatabase = dataBase;
    }

    protected LiteOrm(DataBaseConfig config) {
        config.context = config.context.getApplicationContext();
        if (config.dbName == null) {
            config.dbName = DataBaseConfig.DEFAULT_DB_NAME;
        }
        if (config.dbVersion <= 0) {
            config.dbVersion = 1;
        }
        this.mConfig = config;
        setDebugged(config.debugged);
        openOrCreateDatabase();
    }

    public SQLiteDatabase openOrCreateDatabase() {
        initDatabasePath(this.mConfig.dbName);
        if (this.mHelper != null) {
            justRelease();
        }
        this.mHelper = new SQLiteHelper(this.mConfig.context.getApplicationContext(), this.mConfig.dbName, (SQLiteDatabase.CursorFactory) null, this.mConfig.dbVersion, this.mConfig.onUpdateListener);
        this.mTableManager = new TableManager(this.mConfig.dbName, this.mHelper.getReadableDatabase());
        return this.mHelper.getWritableDatabase();
    }

    private void initDatabasePath(String path) {
        String str = TAG;
        OrmLog.i(str, "create  database path: " + path);
        String path2 = this.mConfig.context.getDatabasePath(this.mConfig.dbName).getPath();
        String str2 = TAG;
        OrmLog.i(str2, "context database path: " + path2);
        File dbp = new File(path2).getParentFile();
        if (dbp != null && !dbp.exists()) {
            boolean mks = dbp.mkdirs();
            String str3 = TAG;
            OrmLog.i(str3, "create database, parent file mkdirs: " + mks + "  path:" + dbp.getAbsolutePath());
        }
    }

    public static LiteOrm newSingleInstance(Context context, String dbName) {
        return newSingleInstance(new DataBaseConfig(context, dbName));
    }

    public static synchronized LiteOrm newSingleInstance(DataBaseConfig config) {
        LiteOrm newInstance;
        synchronized (LiteOrm.class) {
            newInstance = SingleSQLiteImpl.newInstance(config);
        }
        return newInstance;
    }

    public static LiteOrm newCascadeInstance(Context context, String dbName) {
        return newCascadeInstance(new DataBaseConfig(context, dbName));
    }

    public static synchronized LiteOrm newCascadeInstance(DataBaseConfig config) {
        LiteOrm newInstance;
        synchronized (LiteOrm.class) {
            newInstance = CascadeSQLiteImpl.newInstance(config);
        }
        return newInstance;
    }

    public void setDebugged(boolean debugged) {
        this.mConfig.debugged = debugged;
        OrmLog.isPrint = debugged;
    }

    public ArrayList<RelationKey> queryRelation(Class class1, Class class2, List<String> key1List) {
        acquireReference();
        ArrayList<RelationKey> rList = new ArrayList<>();
        try {
            EntityTable table1 = TableManager.getTable((Class<?>) class1);
            EntityTable table2 = TableManager.getTable((Class<?>) class2);
            try {
                if (this.mTableManager.isSQLMapTableCreated(table1.name, table2.name)) {
                    final Class cls = class1;
                    final Class cls2 = class2;
                    final List<String> list = key1List;
                    final EntityTable entityTable = table1;
                    final EntityTable entityTable2 = table2;
                    final ArrayList<RelationKey> arrayList = rList;
                    try {
                        CollSpliter.split(key1List, 999, new CollSpliter.Spliter<String>() {
                            public int oneSplit(ArrayList<String> arrayList) throws Exception {
                                Querier.doQuery(LiteOrm.this.mHelper.getReadableDatabase(), SQLBuilder.buildQueryRelationSql(cls, cls2, (List<String>) list), new Querier.CursorParser() {
                                    public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                                        RelationKey relation = new RelationKey();
                                        relation.key1 = c.getString(c.getColumnIndex(entityTable.name));
                                        relation.key2 = c.getString(c.getColumnIndex(entityTable2.name));
                                        arrayList.add(relation);
                                    }
                                });
                                return 0;
                            }
                        });
                    } catch (Exception e) {
                        e = e;
                    }
                } else {
                    List<String> list2 = key1List;
                }
            } catch (Exception e2) {
                e = e2;
                List<String> list3 = key1List;
                try {
                    e.printStackTrace();
                    releaseReference();
                    return rList;
                } catch (Throwable th) {
                    th = th;
                    releaseReference();
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                List<String> list4 = key1List;
                releaseReference();
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            List<String> list32 = key1List;
            e.printStackTrace();
            releaseReference();
            return rList;
        } catch (Throwable th3) {
            th = th3;
            List<String> list42 = key1List;
            releaseReference();
            throw th;
        }
        releaseReference();
        return rList;
    }

    public <E, T> boolean mapping(Collection<E> col1, Collection<T> col2) {
        if (Checker.isEmpty((Collection<?>) col1) || Checker.isEmpty((Collection<?>) col2)) {
            return false;
        }
        acquireReference();
        try {
            return keepMapping(col1, col2) | keepMapping(col2, col1);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            releaseReference();
        }
    }

    public SQLStatement createSQLStatement(String sql, Object[] bindArgs) {
        return new SQLStatement(sql, bindArgs);
    }

    public boolean execute(SQLiteDatabase db, SQLStatement statement) {
        acquireReference();
        if (statement != null) {
            try {
                boolean execute = statement.execute(db);
                releaseReference();
                return execute;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                releaseReference();
                throw th;
            }
        }
        releaseReference();
        return false;
    }

    @Deprecated
    public boolean dropTable(Object entity) {
        return dropTable(entity.getClass());
    }

    public boolean dropTable(Class<?> claxx) {
        return dropTable(TableManager.getTable(claxx, false).name);
    }

    /* JADX INFO: finally extract failed */
    public boolean dropTable(String tableName) {
        acquireReference();
        try {
            boolean execute = SQLBuilder.buildDropTable(tableName).execute(this.mHelper.getWritableDatabase());
            releaseReference();
            return execute;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return false;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public <T> long queryCount(Class<T> claxx) {
        return queryCount(new QueryBuilder(claxx));
    }

    /* JADX INFO: finally extract failed */
    public long queryCount(QueryBuilder qb) {
        acquireReference();
        try {
            if (this.mTableManager.isSQLTableCreated(qb.getTableName())) {
                long queryForLong = qb.createStatementForCount().queryForLong(this.mHelper.getReadableDatabase());
                releaseReference();
                return queryForLong;
            }
            releaseReference();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    /* JADX INFO: finally extract failed */
    public int update(WhereBuilder where, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            int execUpdate = SQLBuilder.buildUpdateSql(where, cvs, conflictAlgorithm).execUpdate(this.mHelper.getWritableDatabase());
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

    public synchronized SQLiteDatabase getReadableDatabase() {
        return this.mHelper.getReadableDatabase();
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        return this.mHelper.getWritableDatabase();
    }

    public TableManager getTableManager() {
        return this.mTableManager;
    }

    public SQLiteHelper getSQLiteHelper() {
        return this.mHelper;
    }

    public DataBaseConfig getDataBaseConfig() {
        return this.mConfig;
    }

    public SQLiteDatabase openOrCreateDatabase(String path, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(this.mConfig.context.getDatabasePath(this.mConfig.dbName).getPath(), factory);
    }

    public boolean deleteDatabase() {
        String path = this.mHelper.getWritableDatabase().getPath();
        justRelease();
        String str = TAG;
        OrmLog.i(str, "data has cleared. delete Database path: " + path);
        return deleteDatabase(new File(path));
    }

    /* JADX INFO: finally extract failed */
    public boolean deleteDatabase(File file) {
        acquireReference();
        if (file != null) {
            try {
                boolean deleted = file.delete() | new File(file.getPath() + "-journal").delete() | new File(file.getPath() + "-shm").delete() | new File(file.getPath() + "-wal").delete();
                File dir = file.getParentFile();
                if (dir != null) {
                    final String prefix = file.getName() + "-mj";
                    for (File masterJournal : dir.listFiles(new FileFilter() {
                        public boolean accept(File candidate) {
                            return candidate.getName().startsWith(prefix);
                        }
                    })) {
                        deleted |= masterJournal.delete();
                    }
                }
                releaseReference();
                return deleted;
            } catch (Exception e) {
                e.printStackTrace();
                releaseReference();
                return false;
            } catch (Throwable th) {
                releaseReference();
                throw th;
            }
        } else {
            throw new IllegalArgumentException("file must not be null");
        }
    }

    public synchronized void close() {
        releaseReference();
    }

    /* access modifiers changed from: protected */
    public void onAllReferencesReleased() {
        justRelease();
    }

    /* access modifiers changed from: protected */
    public void justRelease() {
        SQLiteHelper sQLiteHelper = this.mHelper;
        if (sQLiteHelper != null) {
            sQLiteHelper.getWritableDatabase().close();
            this.mHelper.close();
            this.mHelper = null;
        }
        TableManager tableManager = this.mTableManager;
        if (tableManager != null) {
            tableManager.release();
            this.mTableManager = null;
        }
    }

    public static int releaseMemory() {
        return SQLiteDatabase.releaseMemory();
    }

    private <E, T> boolean keepMapping(Collection<E> col1, Collection<T> col2) throws IllegalAccessException, InstantiationException {
        Class itemClass;
        Iterator i$;
        HashMap<Object, ArrayList> collMap;
        EntityTable table1;
        ArrayList col;
        Class claxx1;
        Object key1;
        Class claxx12 = col1.iterator().next().getClass();
        Class claxx2 = col2.iterator().next().getClass();
        EntityTable table12 = TableManager.getTable((Class<?>) claxx12);
        EntityTable table2 = TableManager.getTable((Class<?>) claxx2);
        if (table12.mappingList != null) {
            Iterator i$2 = table12.mappingList.iterator();
            while (i$2.hasNext()) {
                MapProperty mp = i$2.next();
                Class fieldClass = mp.field.getType();
                if (!mp.isToMany()) {
                    itemClass = fieldClass;
                } else if (ClassUtil.isCollection(fieldClass)) {
                    itemClass = FieldUtil.getGenericType(mp.field);
                } else if (fieldClass.isArray()) {
                    itemClass = FieldUtil.getComponentType(mp.field);
                } else {
                    throw new RuntimeException("OneToMany and ManyToMany Relation, Must use collection or array object");
                }
                if (itemClass == claxx2) {
                    ArrayList<String> key1List = new ArrayList<>();
                    HashMap<String, Object> map1 = new HashMap<>();
                    for (Object o1 : col1) {
                        if (!(o1 == null || (key1 = FieldUtil.get(table12.key.field, o1)) == null)) {
                            key1List.add(key1.toString());
                            map1.put(key1.toString(), o1);
                        }
                    }
                    ArrayList<RelationKey> relationKeys = queryRelation(claxx12, claxx2, key1List);
                    if (!Checker.isEmpty((Collection<?>) relationKeys)) {
                        HashMap<String, Object> map2 = new HashMap<>();
                        for (Object o2 : col2) {
                            if (o2 != null) {
                                Object key2 = FieldUtil.get(table2.key.field, o2);
                                if (key2 != null) {
                                    claxx1 = claxx12;
                                    map2.put(key2.toString(), o2);
                                } else {
                                    claxx1 = claxx12;
                                }
                            } else {
                                claxx1 = claxx12;
                            }
                            claxx12 = claxx1;
                        }
                        HashMap<Object, ArrayList> collMap2 = new HashMap<>();
                        Iterator i$3 = relationKeys.iterator();
                        while (i$3.hasNext()) {
                            RelationKey m = i$3.next();
                            Object obj1 = map1.get(m.key1);
                            Class claxx22 = claxx2;
                            Object obj2 = map2.get(m.key2);
                            if (obj1 == null || obj2 == null) {
                                table1 = table12;
                            } else if (mp.isToMany()) {
                                ArrayList col3 = collMap2.get(obj1);
                                if (col3 == null) {
                                    table1 = table12;
                                    col = new ArrayList();
                                    collMap2.put(obj1, col);
                                } else {
                                    table1 = table12;
                                    col = col3;
                                }
                                col.add(obj2);
                            } else {
                                table1 = table12;
                                FieldUtil.set(mp.field, obj1, obj2);
                            }
                            claxx2 = claxx22;
                            table12 = table1;
                        }
                        EntityTable entityTable = table12;
                        if (!Checker.isEmpty((Map<?, ?>) collMap2)) {
                            Iterator i$4 = collMap2.entrySet().iterator();
                            while (i$4.hasNext()) {
                                Map.Entry<Object, ArrayList> entry = i$4.next();
                                Object obj12 = entry.getKey();
                                Collection tempColl = entry.getValue();
                                if (ClassUtil.isCollection(itemClass)) {
                                    Collection col4 = (Collection) FieldUtil.get(mp.field, obj12);
                                    if (col4 == null) {
                                        collMap = collMap2;
                                        FieldUtil.set(mp.field, obj12, tempColl);
                                    } else {
                                        collMap = collMap2;
                                        col4.addAll(tempColl);
                                    }
                                    i$ = i$4;
                                } else {
                                    collMap = collMap2;
                                    if (ClassUtil.isArray(itemClass)) {
                                        Object[] tempArray = (Object[]) ClassUtil.newArray(itemClass, tempColl.size());
                                        tempColl.toArray(tempArray);
                                        Object[] array = (Object[]) FieldUtil.get(mp.field, obj12);
                                        if (array == null) {
                                            i$ = i$4;
                                            FieldUtil.set(mp.field, obj12, tempArray);
                                        } else {
                                            i$ = i$4;
                                            Object[] objArr = tempArray;
                                            FieldUtil.set(mp.field, obj12, DataUtil.concat(array, tempArray));
                                        }
                                    } else {
                                        i$ = i$4;
                                    }
                                }
                                collMap2 = collMap;
                                i$4 = i$;
                            }
                            Iterator it = i$4;
                            return true;
                        }
                        return true;
                    }
                }
                claxx12 = claxx12;
                claxx2 = claxx2;
                table12 = table12;
            }
            Class cls = claxx12;
            Class cls2 = claxx2;
            EntityTable entityTable2 = table12;
            return false;
        }
        Class cls3 = claxx12;
        Class cls4 = claxx2;
        EntityTable entityTable3 = table12;
        return false;
    }
}
