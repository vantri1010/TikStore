package com.litesuits.orm.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.Querier;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Primarykey;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.model.SQLiteColumn;
import com.litesuits.orm.db.model.SQLiteTable;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.log.OrmLog;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TableManager {
    private static final String[] ID = {TtmlNode.ATTR_ID, "_id"};
    /* access modifiers changed from: private */
    public static final String TAG = TableManager.class.getSimpleName();
    private static final HashMap<String, EntityTable> mEntityTableMap = new HashMap<>();
    private String dbName = "";
    /* access modifiers changed from: private */
    public final HashMap<String, SQLiteTable> mSqlTableMap = new HashMap<>();

    public TableManager(String dbName2, SQLiteDatabase db) {
        this.dbName = dbName2;
        initSqlTable(db);
    }

    public void initSqlTable(SQLiteDatabase db) {
        initAllTablesFromSQLite(db);
    }

    public void clearSqlTable() {
        synchronized (this.mSqlTableMap) {
            this.mSqlTableMap.clear();
        }
    }

    public void release() {
        clearSqlTable();
        mEntityTableMap.clear();
    }

    public EntityTable checkOrCreateTable(SQLiteDatabase db, Object entity) {
        return checkOrCreateTable(db, (Class) entity.getClass());
    }

    public synchronized EntityTable checkOrCreateTable(SQLiteDatabase db, Class claxx) {
        EntityTable table;
        table = getTable((Class<?>) claxx);
        if (!checkExistAndColumns(db, table) && createTable(db, table)) {
            putNewSqlTableIntoMap(table);
        }
        return table;
    }

    public synchronized void checkOrCreateMappingTable(SQLiteDatabase db, String tableName, String column1, String column2) {
        EntityTable table = getMappingTable(tableName, column1, column2);
        if (!checkExistAndColumns(db, table) && createTable(db, table)) {
            putNewSqlTableIntoMap(table);
        }
    }

    public boolean isSQLMapTableCreated(String tableName1, String tableName2) {
        return this.mSqlTableMap.get(getMapTableName(tableName1, tableName2)) != null;
    }

    public boolean isSQLTableCreated(String tableName) {
        return this.mSqlTableMap.get(tableName) != null;
    }

    private boolean checkExistAndColumns(SQLiteDatabase db, EntityTable entityTable) {
        SQLiteTable sqlTable = this.mSqlTableMap.get(entityTable.name);
        if (sqlTable != null) {
            if (OrmLog.isPrint) {
                String str = TAG;
                OrmLog.d(str, "Table [" + entityTable.name + "] Exist");
            }
            if (!sqlTable.isTableChecked) {
                sqlTable.isTableChecked = true;
                if (OrmLog.isPrint) {
                    String str2 = TAG;
                    OrmLog.i(str2, "Table [" + entityTable.name + "] check column now.");
                }
                if (entityTable.key != null && sqlTable.columns.get(entityTable.key.column) == null) {
                    SQLBuilder.buildDropTable(sqlTable.name).execute(db);
                    if (OrmLog.isPrint) {
                        String str3 = TAG;
                        OrmLog.i(str3, "Table [" + entityTable.name + "] Primary Key has changed, " + "so drop and recreate it later.");
                    }
                    return false;
                } else if (entityTable.pmap != null) {
                    ArrayList<String> newColumns = new ArrayList<>();
                    for (String col : entityTable.pmap.keySet()) {
                        if (sqlTable.columns.get(col) == null) {
                            newColumns.add(col);
                        }
                    }
                    if (!Checker.isEmpty((Collection<?>) newColumns)) {
                        Iterator i$ = newColumns.iterator();
                        while (i$.hasNext()) {
                            sqlTable.columns.put(i$.next(), 1);
                        }
                        int sum = insertNewColunms(db, entityTable.name, newColumns);
                        if (OrmLog.isPrint) {
                            if (sum > 0) {
                                String str4 = TAG;
                                OrmLog.i(str4, "Table [" + entityTable.name + "] add " + sum + " new column ： " + newColumns);
                            } else {
                                String str5 = TAG;
                                OrmLog.e(str5, "Table [" + entityTable.name + "] add " + sum + " new column error ： " + newColumns);
                            }
                        }
                    }
                }
            }
            return true;
        }
        if (OrmLog.isPrint) {
            String str6 = TAG;
            OrmLog.d(str6, "Table [" + entityTable.name + "] Not Exist");
        }
        return false;
    }

    private void putNewSqlTableIntoMap(EntityTable table) {
        if (OrmLog.isPrint) {
            String str = TAG;
            OrmLog.i(str, "Table [" + table.name + "] Create Success");
        }
        SQLiteTable sqlTable = new SQLiteTable();
        sqlTable.name = table.name;
        sqlTable.columns = new HashMap<>();
        if (table.key != null) {
            sqlTable.columns.put(table.key.column, 1);
        }
        if (table.pmap != null) {
            for (String col : table.pmap.keySet()) {
                sqlTable.columns.put(col, 1);
            }
        }
        sqlTable.isTableChecked = true;
        this.mSqlTableMap.put(sqlTable.name, sqlTable);
    }

    private void initAllTablesFromSQLite(SQLiteDatabase db) {
        synchronized (this.mSqlTableMap) {
            if (Checker.isEmpty((Map<?, ?>) this.mSqlTableMap)) {
                if (OrmLog.isPrint) {
                    OrmLog.i(TAG, "Initialize SQL table start--------------------->");
                }
                SQLStatement st = SQLBuilder.buildTableObtainAll();
                final EntityTable table = getTable(SQLiteTable.class, false);
                Querier.doQuery(db, st, new Querier.CursorParser() {
                    public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                        SQLiteTable sqlTable = new SQLiteTable();
                        DataUtil.injectDataToObject(c, sqlTable, table);
                        ArrayList<String> colS = TableManager.this.getAllColumnsFromSQLite(db, sqlTable.name);
                        if (Checker.isEmpty((Collection<?>) colS)) {
                            OrmLog.e(TableManager.TAG, "读数据库失败了，开始解析建表语句");
                            colS = TableManager.this.transformSqlToColumns(sqlTable.sql);
                        }
                        sqlTable.columns = new HashMap<>();
                        Iterator i$ = colS.iterator();
                        while (i$.hasNext()) {
                            sqlTable.columns.put(i$.next(), 1);
                        }
                        if (OrmLog.isPrint) {
                            String access$000 = TableManager.TAG;
                            OrmLog.i(access$000, "Find One SQL Table: " + sqlTable);
                            String access$0002 = TableManager.TAG;
                            OrmLog.i(access$0002, "Table Column: " + colS);
                        }
                        TableManager.this.mSqlTableMap.put(sqlTable.name, sqlTable);
                    }
                });
                if (OrmLog.isPrint) {
                    String str = TAG;
                    OrmLog.i(str, "Initialize SQL table end  ---------------------> " + this.mSqlTableMap.size());
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.Integer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int insertNewColunms(android.database.sqlite.SQLiteDatabase r3, final java.lang.String r4, final java.util.List<java.lang.String> r5) {
        /*
            r2 = this;
            r0 = 0
            boolean r1 = com.litesuits.orm.db.assit.Checker.isEmpty((java.util.Collection<?>) r5)
            if (r1 != 0) goto L_0x0013
            com.litesuits.orm.db.TableManager$2 r1 = new com.litesuits.orm.db.TableManager$2
            r1.<init>(r5, r4)
            java.lang.Object r1 = com.litesuits.orm.db.assit.Transaction.execute(r3, r1)
            r0 = r1
            java.lang.Integer r0 = (java.lang.Integer) r0
        L_0x0013:
            if (r0 != 0) goto L_0x0017
            r1 = 0
            goto L_0x001b
        L_0x0017:
            int r1 = r0.intValue()
        L_0x001b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.litesuits.orm.db.TableManager.insertNewColunms(android.database.sqlite.SQLiteDatabase, java.lang.String, java.util.List):int");
    }

    private boolean createTable(SQLiteDatabase db, EntityTable table) {
        return SQLBuilder.buildCreateTable(table).execute(db);
    }

    public ArrayList<String> getAllColumnsFromSQLite(SQLiteDatabase db, String tableName) {
        final EntityTable table = getTable(SQLiteColumn.class, false);
        final ArrayList<String> list = new ArrayList<>();
        Querier.doQuery(db, SQLBuilder.buildColumnsObtainAll(tableName), new Querier.CursorParser() {
            public void parseEachCursor(SQLiteDatabase db, Cursor c) throws Exception {
                SQLiteColumn col = new SQLiteColumn();
                DataUtil.injectDataToObject(c, col, table);
                list.add(col.name);
            }
        });
        return list;
    }

    public ArrayList<String> transformSqlToColumns(String sql) {
        if (sql == null) {
            return null;
        }
        int start = sql.indexOf(SQLBuilder.PARENTHESES_LEFT);
        int end = sql.lastIndexOf(SQLBuilder.PARENTHESES_RIGHT);
        if (start <= 0 || end <= 0) {
            return null;
        }
        String sql2 = sql.substring(start + 1, end);
        String[] cloumns = sql2.split(",");
        ArrayList<String> colList = new ArrayList<>();
        for (String col : cloumns) {
            String col2 = col.trim();
            int endS = col2.indexOf(" ");
            if (endS > 0) {
                col2 = col2.substring(0, endS);
            }
            colList.add(col2);
        }
        OrmLog.e(TAG, "降级：语义分析表结构（" + colList.toString() + " , Origin SQL is: " + sql2);
        return colList;
    }

    private static EntityTable getEntityTable(String name) {
        return mEntityTableMap.get(name);
    }

    private static EntityTable putEntityTable(String tableName, EntityTable entity) {
        return mEntityTableMap.put(tableName, entity);
    }

    private EntityTable getMappingTable(String tableName, String column1, String column2) {
        EntityTable table = getEntityTable(this.dbName + tableName);
        if (table != null) {
            return table;
        }
        EntityTable table2 = new EntityTable();
        table2.name = tableName;
        table2.pmap = new LinkedHashMap<>();
        table2.pmap.put(column1, (Object) null);
        table2.pmap.put(column2, (Object) null);
        putEntityTable(this.dbName + tableName, table2);
        return table2;
    }

    public static EntityTable getTable(Object entity) {
        return getTable(entity.getClass(), true);
    }

    public static EntityTable getTable(Class<?> claxx) {
        return getTable(claxx, true);
    }

    public static synchronized EntityTable getTable(Class<?> claxx, boolean needPK) {
        EntityTable table;
        synchronized (TableManager.class) {
            table = getEntityTable(claxx.getName());
            if (table == null) {
                table = new EntityTable();
                table.claxx = claxx;
                table.name = getTableName(claxx);
                table.pmap = new LinkedHashMap<>();
                for (Field f : FieldUtil.getAllDeclaredFields(claxx)) {
                    if (!FieldUtil.isInvalid(f)) {
                        Column col = (Column) f.getAnnotation(Column.class);
                        Property p = new Property(col != null ? col.value() : f.getName(), f);
                        PrimaryKey key = (PrimaryKey) f.getAnnotation(PrimaryKey.class);
                        if (key != null) {
                            table.key = new Primarykey(p, key.value());
                            checkPrimaryKey(table.key);
                        } else {
                            Mapping mapping = (Mapping) f.getAnnotation(Mapping.class);
                            if (mapping != null) {
                                table.addMapping(new MapProperty(p, mapping.value()));
                            } else {
                                table.pmap.put(p.column, p);
                            }
                        }
                    }
                }
                if (table.key == null) {
                    Iterator i$ = table.pmap.keySet().iterator();
                    while (true) {
                        if (!i$.hasNext()) {
                            break;
                        }
                        String col2 = i$.next();
                        String[] arr$ = ID;
                        int len$ = arr$.length;
                        int i$2 = 0;
                        while (true) {
                            if (i$2 >= len$) {
                                break;
                            }
                            if (arr$[i$2].equalsIgnoreCase(col2)) {
                                Property p2 = table.pmap.get(col2);
                                if (p2.field.getType() == String.class) {
                                    table.pmap.remove(col2);
                                    table.key = new Primarykey(p2, AssignType.BY_MYSELF);
                                    break;
                                } else if (FieldUtil.isNumber(p2.field.getType())) {
                                    table.pmap.remove(col2);
                                    table.key = new Primarykey(p2, AssignType.AUTO_INCREMENT);
                                    break;
                                }
                            }
                            i$2++;
                        }
                        if (table.key != null) {
                            break;
                        }
                    }
                }
                if (needPK) {
                    if (table.key == null) {
                        throw new RuntimeException("你必须为[" + table.claxx.getSimpleName() + "]设置主键(you must set the primary key...)" + "\n 提示：在对象的属性上加PrimaryKey注解来设置主键。");
                    }
                }
                putEntityTable(claxx.getName(), table);
            }
        }
        return table;
    }

    private static void checkPrimaryKey(Primarykey key) {
        if (key.isAssignedBySystem()) {
            if (!FieldUtil.isNumber(key.field.getType())) {
                throw new RuntimeException(AssignType.AUTO_INCREMENT + " Auto increment primary key must be a number ...\n " + "错误提示：自增主键必须设置为数字类型");
            }
        } else if (!key.isAssignedByMyself()) {
            throw new RuntimeException(" Primary key without Assign Type ...\n 错误提示：主键无类型");
        } else if (String.class != key.field.getType() && !FieldUtil.isNumber(key.field.getType())) {
            throw new RuntimeException(AssignType.BY_MYSELF + " Custom primary key must be string or number ...\n " + "错误提示：自定义主键值必须为String或者Number类型");
        }
    }

    public static String getTableName(Class<?> claxx) {
        Table anno = (Table) claxx.getAnnotation(Table.class);
        if (anno != null) {
            return anno.value();
        }
        return claxx.getName().replaceAll("\\.", "_");
    }

    public static String getMapTableName(Class c1, Class c2) {
        return getMapTableName(getTableName(c1), getTableName(c2));
    }

    public static String getMapTableName(EntityTable t1, EntityTable t2) {
        return getMapTableName(t1.name, t2.name);
    }

    public static String getMapTableName(String tableName1, String tableName2) {
        if (tableName1.compareTo(tableName2) < 0) {
            return tableName1 + "_" + tableName2;
        }
        return tableName2 + "_" + tableName1;
    }
}
