package com.litesuits.orm.db.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.Transaction;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.model.RelationKey;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class CascadeSQLiteImpl extends LiteOrm {
    public static final String TAG = CascadeSQLiteImpl.class.getSimpleName();
    public static final int TYPE_DELETE = 3;
    public static final int TYPE_INSERT = 1;
    public static final int TYPE_UPDATE = 2;

    protected CascadeSQLiteImpl(LiteOrm dataBase) {
        super(dataBase);
    }

    private CascadeSQLiteImpl(DataBaseConfig config) {
        super(config);
    }

    public static synchronized LiteOrm newInstance(DataBaseConfig config) {
        CascadeSQLiteImpl cascadeSQLiteImpl;
        synchronized (CascadeSQLiteImpl.class) {
            cascadeSQLiteImpl = new CascadeSQLiteImpl(config);
        }
        return cascadeSQLiteImpl;
    }

    public LiteOrm single() {
        if (this.otherDatabase == null) {
            this.otherDatabase = new SingleSQLiteImpl((LiteOrm) this);
        }
        return this.otherDatabase;
    }

    public LiteOrm cascade() {
        return this;
    }

    public long save(final Object entity) {
        acquireReference();
        try {
            Long rowID = (Long) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Long>() {
                public Long doTransaction(SQLiteDatabase db) throws Exception {
                    return Long.valueOf(CascadeSQLiteImpl.this.checkTableAndSaveRecursive(entity, db, new HashMap<>()));
                }
            });
            return rowID == null ? -1 : rowID.longValue();
        } finally {
            releaseReference();
        }
    }

    public <T> int save(Collection<T> collection) {
        acquireReference();
        try {
            return saveCollection(collection);
        } finally {
            releaseReference();
        }
    }

    public long insert(Object entity) {
        return insert(entity, (ConflictAlgorithm) null);
    }

    public long insert(final Object entity, final ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        long j = -1;
        try {
            Long rowID = (Long) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Long>() {
                public Long doTransaction(SQLiteDatabase db) throws Exception {
                    CascadeSQLiteImpl.this.mTableManager.checkOrCreateTable(db, entity);
                    return Long.valueOf(CascadeSQLiteImpl.this.insertRecursive(SQLBuilder.buildInsertSql(entity, conflictAlgorithm), entity, db, new HashMap()));
                }
            });
            if (rowID != null) {
                j = rowID.longValue();
            }
            return j;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            releaseReference();
        }
    }

    public <T> int insert(Collection<T> collection) {
        return insert(collection, (ConflictAlgorithm) null);
    }

    /* JADX INFO: finally extract failed */
    public <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            int insertCollection = insertCollection(collection, conflictAlgorithm);
            releaseReference();
            return insertCollection;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public int update(Object entity) {
        return update(entity, (ColumnsValue) null, (ConflictAlgorithm) null);
    }

    public int update(Object entity, ConflictAlgorithm conflictAlgorithm) {
        return update(entity, (ColumnsValue) null, conflictAlgorithm);
    }

    public int update(final Object entity, final ColumnsValue cvs, final ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        int i = -1;
        try {
            Integer rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    HashMap<String, Integer> handleMap = new HashMap<>();
                    SQLStatement stmt = SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm);
                    CascadeSQLiteImpl.this.mTableManager.checkOrCreateTable(db, entity);
                    return Integer.valueOf(CascadeSQLiteImpl.this.updateRecursive(stmt, entity, db, handleMap));
                }
            });
            if (rowID != null) {
                i = rowID.intValue();
            }
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            releaseReference();
        }
    }

    public <T> int update(Collection<T> collection) {
        return update(collection, (ColumnsValue) null, (ConflictAlgorithm) null);
    }

    public <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        return update(collection, (ColumnsValue) null, conflictAlgorithm);
    }

    /* JADX INFO: finally extract failed */
    public <T> int update(Collection<T> collection, ColumnsValue cvs, ConflictAlgorithm conflictAlgorithm) {
        acquireReference();
        try {
            int updateCollection = updateCollection(collection, cvs, conflictAlgorithm);
            releaseReference();
            return updateCollection;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public int delete(final Object entity) {
        acquireReference();
        try {
            Integer rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
                public Integer doTransaction(SQLiteDatabase db) throws Exception {
                    return Integer.valueOf(CascadeSQLiteImpl.this.checkTableAndDeleteRecursive(entity, db, new HashMap<>()));
                }
            });
            if (rowID != null) {
                int intValue = rowID.intValue();
                releaseReference();
                return intValue;
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

    public <T> int delete(Class<T> claxx) {
        return deleteAll(claxx);
    }

    /* JADX INFO: finally extract failed */
    public <T> int delete(Collection<T> collection) {
        acquireReference();
        try {
            int deleteCollectionIfTableHasCreated = deleteCollectionIfTableHasCreated(collection);
            releaseReference();
            return deleteCollectionIfTableHasCreated;
        } catch (Exception e) {
            e.printStackTrace();
            releaseReference();
            return -1;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public <T> int delete(Class<T> claxx, WhereBuilder where) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable((Class<?>) claxx);
            delete(query(QueryBuilder.create(claxx).columns(new String[]{table.key.column}).where(where)));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return -1;
    }

    public int delete(WhereBuilder where) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable((Class<?>) where.getTableClass());
            deleteCollectionIfTableHasCreated(query(QueryBuilder.create(where.getTableClass()).columns(new String[]{table.key.column}).where(where)));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return -1;
    }

    public <T> int deleteAll(Class<T> claxx) {
        acquireReference();
        try {
            EntityTable table = TableManager.getTable((Class<?>) claxx);
            return delete(query(QueryBuilder.create(claxx).columns(new String[]{table.key.column})));
        } finally {
            releaseReference();
        }
    }

    public <T> int delete(Class<T> claxx, long start, long end, String orderAscColumn) {
        acquireReference();
        if (start < 0 || end < start) {
            throw new RuntimeException("start must >=0 and smaller than end");
        }
        if (start != 0) {
            start--;
        }
        long end2 = end == 2147483647L ? -1 : end - start;
        try {
            EntityTable table = TableManager.getTable((Class<?>) claxx);
            QueryBuilder<T> create = QueryBuilder.create(claxx);
            return delete(query(create.limit(start + "," + end2).appendOrderAscBy(orderAscColumn).columns(new String[]{table.key.column})));
        } finally {
            releaseReference();
        }
    }

    public <T> ArrayList<T> query(Class<T> claxx) {
        return checkTableAndQuery(claxx, new QueryBuilder(claxx));
    }

    public <T> ArrayList<T> query(QueryBuilder<T> qb) {
        return checkTableAndQuery(qb.getQueryClass(), qb);
    }

    public <T> T queryById(long id, Class<T> claxx) {
        return queryById(String.valueOf(id), claxx);
    }

    public <T> T queryById(String id, Class<T> claxx) {
        ArrayList<T> list = checkTableAndQuery(claxx, new QueryBuilder(claxx).whereEquals(TableManager.getTable((Class<?>) claxx).key.column, String.valueOf(id)));
        if (!Checker.isEmpty((Collection<?>) list)) {
            return list.get(0);
        }
        return null;
    }

    private <T> ArrayList<T> checkTableAndQuery(Class<T> claxx, QueryBuilder builder) {
        acquireReference();
        ArrayList<T> list = new ArrayList<>();
        try {
            EntityTable table = TableManager.getTable(claxx, false);
            if (this.mTableManager.isSQLTableCreated(table.name)) {
                HashMap<String, Object> entityMap = new HashMap<>();
                HashMap hashMap = new HashMap();
                SQLiteDatabase db = this.mHelper.getReadableDatabase();
                final Class<T> cls = claxx;
                final EntityTable entityTable = table;
                final ArrayList<T> arrayList = list;
                final HashMap<String, Object> hashMap2 = entityMap;
                Querier.doQuery(db, builder.createStatement(), new Querier.CursorParser() {
                    public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                        T t = ClassUtil.newInstance(cls);
                        DataUtil.injectDataToObject(c, t, entityTable);
                        arrayList.add(t);
                        HashMap hashMap = hashMap2;
                        hashMap.put(entityTable.name + FieldUtil.get(entityTable.key.field, t), t);
                    }
                });
                Iterator i$ = list.iterator();
                while (i$.hasNext()) {
                    queryForMappingRecursive(i$.next(), db, hashMap, entityMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
        releaseReference();
        return list;
    }

    private void queryForMappingRecursive(Object obj1, SQLiteDatabase db, HashMap<String, Integer> queryMap, HashMap<String, Object> entityMap) throws IllegalAccessException, InstantiationException {
        HashMap<String, Integer> hashMap = queryMap;
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.getAssignedKeyObject(table1.key, obj1);
        String key = table1.name + key1;
        if (hashMap.get(key) == null) {
            hashMap.put(key, 1);
            if (table1.mappingList != null) {
                Iterator i$ = table1.mappingList.iterator();
                while (i$.hasNext()) {
                    MapProperty mp = i$.next();
                    if (mp.isToOne()) {
                        queryMapToOne(table1, key1, obj1, mp.field, db, queryMap, entityMap);
                    } else if (mp.isToMany()) {
                        queryMapToMany(table1, key1, obj1, mp.field, db, queryMap, entityMap);
                    }
                }
            }
        }
    }

    private void queryMapToOne(final EntityTable table1, Object key1, Object obj1, Field field, SQLiteDatabase db, HashMap<String, Integer> queryMap, HashMap<String, Object> entityMap) throws IllegalAccessException, InstantiationException {
        final EntityTable table2 = TableManager.getTable(field.getType());
        if (this.mTableManager.isSQLMapTableCreated(table1.name, table2.name)) {
            SQLStatement relationSql = SQLBuilder.buildQueryRelationSql(table1, table2, key1);
            final RelationKey relation = new RelationKey();
            Querier.doQuery(db, relationSql, new Querier.CursorParser() {
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    relation.key1 = c.getString(c.getColumnIndex(table1.name));
                    relation.key2 = c.getString(c.getColumnIndex(table2.name));
                    stopParse();
                }
            });
            if (relation.isOK()) {
                String key = table2.name + relation.key2;
                Object obj2 = entityMap.get(key);
                if (obj2 == null) {
                    obj2 = SQLBuilder.buildQueryMapEntitySql(table2, relation.key2).queryOneEntity(db, table2.claxx);
                    entityMap.put(key, obj2);
                }
                if (obj2 != null) {
                    FieldUtil.set(field, obj1, obj2);
                    queryForMappingRecursive(obj2, db, queryMap, entityMap);
                }
            }
        }
    }

    private void queryMapToMany(EntityTable table1, Object key1, Object obj1, Field field, SQLiteDatabase db, HashMap<String, Integer> queryMap, HashMap<String, Object> entityMap) throws IllegalAccessException, InstantiationException {
        Class<?> class2;
        ArrayList<Object> allList2;
        EntityTable entityTable = table1;
        Object obj = obj1;
        Field field2 = field;
        SQLiteDatabase sQLiteDatabase = db;
        HashMap<String, Object> hashMap = entityMap;
        if (Collection.class.isAssignableFrom(field.getType())) {
            class2 = FieldUtil.getGenericType(field);
        } else if (field.getType().isArray()) {
            class2 = FieldUtil.getComponentType(field);
        } else {
            HashMap<String, Integer> hashMap2 = queryMap;
            throw new RuntimeException("OneToMany and ManyToMany Relation, you must use collection or array object");
        }
        final EntityTable table2 = TableManager.getTable(class2);
        if (this.mTableManager.isSQLMapTableCreated(entityTable.name, table2.name)) {
            SQLStatement relationSql = SQLBuilder.buildQueryRelationSql(entityTable, table2, key1);
            final ArrayList<String> key2List = new ArrayList<>();
            Querier.doQuery(sQLiteDatabase, relationSql, new Querier.CursorParser() {
                public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                    key2List.add(c.getString(c.getColumnIndex(table2.name)));
                }
            });
            if (!Checker.isEmpty((Collection<?>) key2List)) {
                ArrayList<Object> allList22 = new ArrayList<>();
                for (int i = key2List.size() - 1; i >= 0; i--) {
                    Object obj2 = hashMap.get(table2.name + key2List.get(i));
                    if (obj2 != null) {
                        allList22.add(obj2);
                        key2List.remove(i);
                    }
                }
                int i2 = 0;
                int start = 0;
                while (start < key2List.size()) {
                    int i3 = i2 + 1;
                    int next = i3 * 999;
                    int i4 = i3;
                    int end = Math.min(key2List.size(), next);
                    List<String> subList = key2List.subList(start, end);
                    int start2 = next;
                    int i5 = next;
                    int i6 = end;
                    List<String> list = subList;
                    AnonymousClass8 r7 = r0;
                    SQLStatement entitySql = QueryBuilder.create(class2).whereIn(table2.key.column, subList.toArray(new String[subList.size()])).createStatement();
                    final Class<?> cls = class2;
                    ArrayList<Object> allList23 = allList22;
                    final EntityTable entityTable2 = table2;
                    ArrayList<String> key2List2 = key2List;
                    final ArrayList<Object> key2List3 = allList23;
                    SQLStatement relationSql2 = relationSql;
                    final HashMap<String, Object> hashMap3 = entityMap;
                    AnonymousClass8 r0 = new Querier.CursorParser() {
                        public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                            Object t = ClassUtil.newInstance(cls);
                            DataUtil.injectDataToObject(c, t, entityTable2);
                            key2List3.add(t);
                            HashMap hashMap = hashMap3;
                            hashMap.put(entityTable2.name + FieldUtil.get(entityTable2.key.field, t), t);
                        }
                    };
                    Querier.doQuery(sQLiteDatabase, entitySql, r7);
                    EntityTable entityTable3 = table1;
                    Object obj3 = key1;
                    i2 = i4;
                    start = start2;
                    allList22 = allList23;
                    key2List = key2List2;
                    relationSql = relationSql2;
                }
                ArrayList<Object> allList24 = allList22;
                ArrayList<String> arrayList = key2List;
                SQLStatement sQLStatement = relationSql;
                if (!Checker.isEmpty((Collection<?>) allList24)) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection coll = (Collection) ClassUtil.newCollectionForField(field);
                        allList2 = allList24;
                        coll.addAll(allList2);
                        FieldUtil.set(field2, obj, coll);
                    } else {
                        allList2 = allList24;
                        if (field.getType().isArray()) {
                            FieldUtil.set(field2, obj, allList2.toArray((Object[]) ClassUtil.newArray(class2, allList2.size())));
                        } else {
                            HashMap<String, Integer> hashMap4 = queryMap;
                            throw new RuntimeException("OneToMany and ManyToMany Relation, you must use collection or array object");
                        }
                    }
                    Iterator<Object> i$ = allList2.iterator();
                    while (i$.hasNext()) {
                        queryForMappingRecursive(i$.next(), sQLiteDatabase, queryMap, hashMap);
                    }
                    HashMap<String, Integer> hashMap5 = queryMap;
                    return;
                }
                HashMap<String, Integer> hashMap6 = queryMap;
                ArrayList<Object> arrayList2 = allList24;
                return;
            }
            SQLStatement sQLStatement2 = relationSql;
            HashMap<String, Integer> hashMap7 = queryMap;
            return;
        }
        HashMap<String, Integer> hashMap8 = queryMap;
    }

    private <T> int saveCollection(final Collection<T> collection) {
        Integer rowID;
        if (Checker.isEmpty((Collection<?>) collection) || (rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
            public Integer doTransaction(SQLiteDatabase db) throws Exception {
                HashMap<String, Integer> handleMap = new HashMap<>();
                Iterator<T> iterator = collection.iterator();
                Object entity = iterator.next();
                SQLStatement stmt = SQLBuilder.buildReplaceSql(entity);
                CascadeSQLiteImpl.this.mTableManager.checkOrCreateTable(db, entity);
                long unused = CascadeSQLiteImpl.this.insertRecursive(stmt, entity, db, handleMap);
                while (iterator.hasNext()) {
                    Object entity2 = iterator.next();
                    stmt.bindArgs = SQLBuilder.buildInsertSqlArgsOnly(entity2);
                    long unused2 = CascadeSQLiteImpl.this.insertRecursive(stmt, entity2, db, handleMap);
                }
                return Integer.valueOf(collection.size());
            }
        })) == null) {
            return -1;
        }
        return rowID.intValue();
    }

    private <T> int insertCollection(final Collection<T> collection, final ConflictAlgorithm conflictAlgorithm) {
        Integer rowID;
        if (Checker.isEmpty((Collection<?>) collection) || (rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
            public Integer doTransaction(SQLiteDatabase db) throws Exception {
                HashMap<String, Integer> handleMap = new HashMap<>();
                Iterator<T> iterator = collection.iterator();
                Object entity = iterator.next();
                SQLStatement stmt = SQLBuilder.buildInsertSql(entity, conflictAlgorithm);
                CascadeSQLiteImpl.this.mTableManager.checkOrCreateTable(db, entity);
                long unused = CascadeSQLiteImpl.this.insertRecursive(stmt, entity, db, handleMap);
                while (iterator.hasNext()) {
                    Object entity2 = iterator.next();
                    stmt.bindArgs = SQLBuilder.buildInsertSqlArgsOnly(entity2);
                    long unused2 = CascadeSQLiteImpl.this.insertRecursive(stmt, entity2, db, handleMap);
                }
                return Integer.valueOf(collection.size());
            }
        })) == null) {
            return -1;
        }
        return rowID.intValue();
    }

    private <T> int updateCollection(final Collection<T> collection, final ColumnsValue cvs, final ConflictAlgorithm conflictAlgorithm) {
        Integer rowID;
        if (Checker.isEmpty((Collection<?>) collection) || (rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
            public Integer doTransaction(SQLiteDatabase db) throws Exception {
                HashMap<String, Integer> handleMap = new HashMap<>();
                Iterator<T> iterator = collection.iterator();
                Object entity = iterator.next();
                SQLStatement stmt = SQLBuilder.buildUpdateSql(entity, cvs, conflictAlgorithm);
                CascadeSQLiteImpl.this.mTableManager.checkOrCreateTable(db, entity);
                int unused = CascadeSQLiteImpl.this.updateRecursive(stmt, entity, db, handleMap);
                while (iterator.hasNext()) {
                    Object entity2 = iterator.next();
                    stmt.bindArgs = SQLBuilder.buildUpdateSqlArgsOnly(entity2, cvs);
                    int unused2 = CascadeSQLiteImpl.this.updateRecursive(stmt, entity2, db, handleMap);
                }
                return Integer.valueOf(collection.size());
            }
        })) == null) {
            return -1;
        }
        return rowID.intValue();
    }

    private <T> int deleteCollectionIfTableHasCreated(final Collection<T> collection) {
        Integer rowID;
        if (Checker.isEmpty((Collection<?>) collection)) {
            return -1;
        }
        final Iterator<T> iterator = collection.iterator();
        final Object entity = iterator.next();
        if (!this.mTableManager.isSQLTableCreated(TableManager.getTable(entity).name) || (rowID = (Integer) Transaction.execute(this.mHelper.getWritableDatabase(), new Transaction.Worker<Integer>() {
            public Integer doTransaction(SQLiteDatabase db) throws Exception {
                HashMap<String, Integer> handleMap = new HashMap<>();
                SQLStatement stmt = SQLBuilder.buildDeleteSql(entity);
                int unused = CascadeSQLiteImpl.this.deleteRecursive(stmt, entity, db, handleMap);
                while (iterator.hasNext()) {
                    Object next = iterator.next();
                    stmt.bindArgs = CascadeSQLiteImpl.getDeleteStatementArgs(next);
                    int unused2 = CascadeSQLiteImpl.this.deleteRecursive(stmt, next, db, handleMap);
                }
                return Integer.valueOf(collection.size());
            }
        })) == null) {
            return -1;
        }
        return rowID.intValue();
    }

    public static Object[] getDeleteStatementArgs(Object entity) throws IllegalAccessException {
        EntityTable table = TableManager.getTable(entity);
        if (table.key != null) {
            return new String[]{String.valueOf(FieldUtil.get(table.key.field, entity))};
        } else if (Checker.isEmpty((Map<?, ?>) table.pmap)) {
            return null;
        } else {
            Object[] args = new Object[table.pmap.size()];
            int i = 0;
            for (Property p : table.pmap.values()) {
                args[i] = FieldUtil.get(p.field, entity);
                i++;
            }
            return args;
        }
    }

    private long handleEntityRecursive(int type, SQLStatement stmt, Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        long rowID;
        Object key1;
        int i = type;
        SQLStatement sQLStatement = stmt;
        Object obj = obj1;
        SQLiteDatabase sQLiteDatabase = db;
        HashMap<String, Integer> hashMap = handleMap;
        EntityTable table1 = TableManager.getTable(obj1);
        Object key12 = FieldUtil.get(table1.key.field, obj);
        if (hashMap.get(table1.name + key12) != null) {
            return -1;
        }
        boolean insertNew = true;
        if (i == 1) {
            long rowID2 = sQLStatement.execInsert(sQLiteDatabase, obj);
            key1 = FieldUtil.get(table1.key.field, obj);
            rowID = rowID2;
        } else if (i == 2) {
            key1 = key12;
            rowID = (long) sQLStatement.execUpdate(sQLiteDatabase);
        } else if (i != 3) {
            key1 = key12;
            rowID = -1;
        } else {
            key1 = key12;
            rowID = (long) sQLStatement.execDelete(sQLiteDatabase);
        }
        hashMap.put(table1.name + key1, 1);
        if (i == 3) {
            insertNew = false;
        }
        handleMapping(key1, obj1, db, insertNew, handleMap);
        return rowID;
    }

    /* access modifiers changed from: private */
    public int updateRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);
        if (handleMap.get(table1.name + key1) != null) {
            return -1;
        }
        int rowID = stmt.execUpdate(db);
        Object key12 = FieldUtil.get(table1.key.field, obj1);
        handleMap.put(table1.name + key12, 1);
        handleMapping(key12, obj1, db, true, handleMap);
        return rowID;
    }

    /* access modifiers changed from: private */
    public int deleteRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);
        if (handleMap.get(table1.name + key1) != null) {
            return -1;
        }
        int rowID = stmt.execDelete(db);
        handleMap.put(table1.name + key1, 1);
        handleMapping(key1, obj1, db, false, handleMap);
        return rowID;
    }

    /* access modifiers changed from: private */
    public long insertRecursive(SQLStatement stmt, Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        EntityTable table1 = TableManager.getTable(obj1);
        Object key1 = FieldUtil.get(table1.key.field, obj1);
        if (handleMap.get(table1.name + key1) != null) {
            return -1;
        }
        long rowID = stmt.execInsert(db, obj1);
        Object key12 = FieldUtil.get(table1.key.field, obj1);
        handleMap.put(table1.name + key12, 1);
        handleMapping(key12, obj1, db, true, handleMap);
        return rowID;
    }

    /* access modifiers changed from: private */
    public long checkTableAndSaveRecursive(Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        this.mTableManager.checkOrCreateTable(db, obj1);
        return insertRecursive(SQLBuilder.buildReplaceSql(obj1), obj1, db, handleMap);
    }

    /* access modifiers changed from: private */
    public int checkTableAndDeleteRecursive(Object obj1, SQLiteDatabase db, HashMap<String, Integer> handleMap) throws Exception {
        if (this.mTableManager.isSQLTableCreated(TableManager.getTable(obj1).name)) {
            return deleteRecursive(SQLBuilder.buildDeleteSql(obj1), obj1, db, handleMap);
        }
        return -1;
    }

    private void handleMapping(Object key1, Object obj1, SQLiteDatabase db, boolean insertNew, HashMap<String, Integer> handleMap) throws Exception {
        Object obj = obj1;
        EntityTable table1 = TableManager.getTable(obj1);
        if (table1.mappingList != null) {
            Iterator i$ = table1.mappingList.iterator();
            while (i$.hasNext()) {
                MapProperty map = i$.next();
                if (map.isToOne()) {
                    Object obj2 = FieldUtil.get(map.field, obj);
                    handleMapToOne(table1, TableManager.getTable(map.field.getType()), key1, obj2, db, insertNew, handleMap);
                } else if (map.isToMany()) {
                    Object array = FieldUtil.get(map.field, obj);
                    if (ClassUtil.isCollection(map.field.getType())) {
                        handleMapToMany(table1, TableManager.getTable(FieldUtil.getGenericType(map.field)), key1, (Collection) array, db, insertNew, handleMap);
                    } else if (ClassUtil.isArray(map.field.getType())) {
                        EntityTable table2 = TableManager.getTable(FieldUtil.getComponentType(map.field));
                        Collection<?> coll = null;
                        if (array != null) {
                            coll = Arrays.asList((Object[]) array);
                        }
                        handleMapToMany(table1, table2, key1, coll, db, insertNew, handleMap);
                    } else {
                        throw new RuntimeException("OneToMany and ManyToMany Relation, you must use collection or array object");
                    }
                } else {
                    continue;
                }
            }
        }
    }

    private void handleMapToOne(EntityTable table1, EntityTable table2, Object key1, Object obj2, SQLiteDatabase db, boolean insertNew, HashMap<String, Integer> handleMap) throws Exception {
        SQLStatement st;
        if (obj2 != null) {
            if (insertNew) {
                checkTableAndSaveRecursive(obj2, db, handleMap);
            } else {
                checkTableAndDeleteRecursive(obj2, db, handleMap);
            }
        }
        String mapTableName = TableManager.getMapTableName(table1, table2);
        this.mTableManager.checkOrCreateMappingTable(db, mapTableName, table1.name, table2.name);
        SQLBuilder.buildMappingDeleteSql(mapTableName, key1, table1).execDelete(db);
        if (insertNew && obj2 != null && (st = SQLBuilder.buildMappingToOneSql(mapTableName, key1, FieldUtil.get(table2.key.field, obj2), table1, table2)) != null) {
            st.execInsert(db);
        }
    }

    private void handleMapToMany(EntityTable table1, EntityTable table2, Object key1, Collection coll, SQLiteDatabase db, boolean insertNew, HashMap<String, Integer> handleMap) throws Exception {
        if (coll != null) {
            for (Object obj2 : coll) {
                if (obj2 != null) {
                    if (insertNew) {
                        checkTableAndSaveRecursive(obj2, db, handleMap);
                    } else {
                        checkTableAndDeleteRecursive(obj2, db, handleMap);
                    }
                }
            }
        }
        String tableName = TableManager.getMapTableName(table1, table2);
        this.mTableManager.checkOrCreateMappingTable(db, tableName, table1.name, table2.name);
        SQLBuilder.buildMappingDeleteSql(tableName, key1, table1).execDelete(db);
        if (insertNew && !Checker.isEmpty((Collection<?>) coll)) {
            ArrayList<SQLStatement> sqlList = SQLBuilder.buildMappingToManySql(key1, table1, table2, coll);
            if (!Checker.isEmpty((Collection<?>) sqlList)) {
                Iterator i$ = sqlList.iterator();
                while (i$.hasNext()) {
                    i$.next().execInsert(db);
                }
            }
        }
    }
}
