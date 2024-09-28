package com.litesuits.orm.db.assit;

import android.util.SparseArray;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.annotation.Check;
import com.litesuits.orm.db.annotation.Collate;
import com.litesuits.orm.db.annotation.Conflict;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.Temporary;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.annotation.UniqueCombine;
import com.litesuits.orm.db.assit.CollSpliter;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.MapInfo;
import com.litesuits.orm.db.model.MapProperty;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SQLBuilder {
    public static final String AND = " AND ";
    public static final String ASC = " ASC ";
    public static final String ASTERISK = "*";
    public static final String BLANK = " ";
    public static final String CHECK = "CHECK ";
    public static final String COLLATE = "COLLATE ";
    public static final String COMMA = ",";
    public static final String COMMA_HOLDER = ",?";
    public static final String CREATE = "CREATE ";
    public static final String DEFAULT = "DEFAULT ";
    public static final String DELETE_FROM = "DELETE FROM ";
    public static final String DESC = " DESC ";
    public static final String DROP_TABLE = "DROP TABLE ";
    public static final String EQUALS_HOLDER = "=?";
    public static final String FROM = " FROM ";
    public static final String HOLDER = "?";
    public static final String IN = " IN ";
    public static final String INSERT = "INSERT ";
    public static final String INTO = "INTO ";
    public static final String LIMIT = " LIMIT ";
    public static final String NOT = " NOT ";
    public static final String NOT_NULL = "NOT NULL ";
    public static final String ON_CONFLICT = "ON CONFLICT ";
    public static final String OR = " OR ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String PARENTHESES_LEFT = "(";
    public static final String PARENTHESES_RIGHT = ")";
    public static final String PRAGMA_TABLE_INFO = "PRAGMA table_info(";
    public static final String PRIMARY_KEY = "PRIMARY KEY ";
    public static final String PRIMARY_KEY_AUTOINCREMENT = "PRIMARY KEY AUTOINCREMENT ";
    public static final String REPLACE = "REPLACE ";
    public static final String SELECT = "SELECT ";
    public static final String SELECT_ANY_FROM = "SELECT * FROM ";
    public static final String SELECT_MAX = "SELECT MAX ";
    public static final String SELECT_TABLES = "SELECT * FROM sqlite_master WHERE type='table' ORDER BY name";
    public static final String SET = " SET ";
    public static final String TABLE_IF_NOT_EXISTS = "TABLE IF NOT EXISTS ";
    public static final String TEMP = "TEMP ";
    public static final String TWO_HOLDER = "(?,?)";
    public static final int TYPE_INSERT = 1;
    public static final int TYPE_REPLACE = 2;
    public static final int TYPE_UPDATE = 3;
    public static final String UNIQUE = "UNIQUE ";
    public static final String UPDATE = "UPDATE ";
    public static final String VALUES = "VALUES";
    public static final String WHERE = " WHERE ";

    public static SQLStatement buildTableObtainAll() {
        return new SQLStatement(SELECT_TABLES, (Object[]) null);
    }

    public static SQLStatement buildColumnsObtainAll(String table) {
        return new SQLStatement(PRAGMA_TABLE_INFO + table + PARENTHESES_RIGHT, (Object[]) null);
    }

    public static SQLStatement buildGetLastRowId(EntityTable table) {
        return new SQLStatement("SELECT MAX (" + table.key.column + PARENTHESES_RIGHT + " FROM " + table.name, (Object[]) null);
    }

    public static SQLStatement buildDropTable(EntityTable table) {
        return new SQLStatement(DROP_TABLE + table.name, (Object[]) null);
    }

    public static SQLStatement buildDropTable(String tableName) {
        return new SQLStatement(DROP_TABLE + tableName, (Object[]) null);
    }

    public static SQLStatement buildCreateTable(EntityTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append(CREATE);
        if (table.getAnnotation(Temporary.class) != null) {
            sb.append(TEMP);
        }
        sb.append(TABLE_IF_NOT_EXISTS);
        sb.append(table.name);
        sb.append(PARENTHESES_LEFT);
        boolean hasKey = false;
        if (table.key != null) {
            hasKey = true;
            if (table.key.assign == AssignType.AUTO_INCREMENT) {
                sb.append(table.key.column);
                sb.append(DataUtil.INTEGER);
                sb.append(PRIMARY_KEY_AUTOINCREMENT);
            } else {
                sb.append(table.key.column);
                sb.append(DataUtil.getSQLDataType(table.key.classType));
                sb.append(PRIMARY_KEY);
            }
        }
        if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
            if (hasKey) {
                sb.append(",");
            }
            boolean needComma = false;
            SparseArray<ArrayList<String>> combineUniqueMap = null;
            for (Map.Entry<String, Property> en : table.pmap.entrySet()) {
                if (needComma) {
                    sb.append(",");
                } else {
                    needComma = true;
                }
                String key = en.getKey();
                sb.append(key);
                if (en.getValue() == null) {
                    sb.append(DataUtil.TEXT);
                } else {
                    Field f = en.getValue().field;
                    sb.append(DataUtil.getSQLDataType(en.getValue().classType));
                    if (f.getAnnotation(NotNull.class) != null) {
                        sb.append(NOT_NULL);
                    }
                    if (f.getAnnotation(Default.class) != null) {
                        sb.append(DEFAULT);
                        sb.append(((Default) f.getAnnotation(Default.class)).value());
                        sb.append(" ");
                    }
                    if (f.getAnnotation(Unique.class) != null) {
                        sb.append(UNIQUE);
                    }
                    if (f.getAnnotation(Conflict.class) != null) {
                        sb.append(ON_CONFLICT);
                        sb.append(((Conflict) f.getAnnotation(Conflict.class)).value().getSql());
                        sb.append(" ");
                    }
                    if (f.getAnnotation(Check.class) != null) {
                        sb.append("CHECK (");
                        sb.append(((Check) f.getAnnotation(Check.class)).value());
                        sb.append(PARENTHESES_RIGHT);
                        sb.append(" ");
                    }
                    if (f.getAnnotation(Collate.class) != null) {
                        sb.append(COLLATE);
                        sb.append(((Collate) f.getAnnotation(Collate.class)).value());
                        sb.append(" ");
                    }
                    UniqueCombine uc = (UniqueCombine) f.getAnnotation(UniqueCombine.class);
                    if (uc != null) {
                        if (combineUniqueMap == null) {
                            combineUniqueMap = new SparseArray<>();
                        }
                        ArrayList<String> list = combineUniqueMap.get(uc.value());
                        if (list == null) {
                            list = new ArrayList<>();
                            combineUniqueMap.put(uc.value(), list);
                        }
                        list.add(key);
                    }
                }
            }
            if (combineUniqueMap != null) {
                int nsize = combineUniqueMap.size();
                for (int i = 0; i < nsize; i++) {
                    ArrayList<String> list2 = combineUniqueMap.valueAt(i);
                    if (list2.size() > 1) {
                        sb.append(",");
                        sb.append(UNIQUE);
                        sb.append(PARENTHESES_LEFT);
                        int size = list2.size();
                        for (int j = 0; j < size; j++) {
                            if (j != 0) {
                                sb.append(",");
                            }
                            sb.append(list2.get(j));
                        }
                        sb.append(PARENTHESES_RIGHT);
                    }
                }
            }
        }
        sb.append(PARENTHESES_RIGHT);
        return new SQLStatement(sb.toString(), (Object[]) null);
    }

    public static SQLStatement buildInsertSql(Object entity, ConflictAlgorithm algorithm) {
        return buildInsertSql(entity, true, 1, algorithm);
    }

    public static SQLStatement buildInsertAllSql(Object entity, ConflictAlgorithm algorithm) {
        return buildInsertSql(entity, false, 1, algorithm);
    }

    public static SQLStatement buildReplaceSql(Object entity) {
        return buildInsertSql(entity, true, 2, (ConflictAlgorithm) null);
    }

    public static SQLStatement buildReplaceAllSql(Object entity) {
        return buildInsertSql(entity, false, 2, (ConflictAlgorithm) null);
    }

    private static SQLStatement buildInsertSql(Object entity, boolean needValue, int type, ConflictAlgorithm algorithm) {
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable(entity);
            StringBuilder sql = new StringBuilder(128);
            if (type != 2) {
                sql.append(INSERT);
                if (algorithm != null) {
                    sql.append(algorithm.getAlgorithm());
                    sql.append(INTO);
                } else {
                    sql.append(INTO);
                }
            } else {
                sql.append(REPLACE);
                sql.append(INTO);
            }
            sql.append(table.name);
            sql.append(PARENTHESES_LEFT);
            sql.append(table.key.column);
            StringBuilder value = new StringBuilder();
            value.append(PARENTHESES_RIGHT);
            value.append(VALUES);
            value.append(PARENTHESES_LEFT);
            value.append("?");
            int size = 1;
            int i = 0;
            if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
                size = 1 + table.pmap.size();
            }
            Object[] args = null;
            if (needValue) {
                args = new Object[size];
                args[0] = FieldUtil.getAssignedKeyObject(table.key, entity);
                i = 0 + 1;
            }
            if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
                for (Map.Entry<String, Property> en : table.pmap.entrySet()) {
                    sql.append(",");
                    sql.append(en.getKey());
                    value.append(",?");
                    if (needValue) {
                        args[i] = FieldUtil.get(en.getValue().field, entity);
                    }
                    i++;
                }
            }
            sql.append(value);
            sql.append(PARENTHESES_RIGHT);
            stmt.bindArgs = args;
            stmt.sql = sql.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public static Object[] buildInsertSqlArgsOnly(Object entity) throws IllegalAccessException {
        EntityTable table = TableManager.getTable(entity);
        int size = 1;
        if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
            size = 1 + table.pmap.size();
        }
        Object[] args = new Object[size];
        int i = 0 + 1;
        args[0] = FieldUtil.getAssignedKeyObject(table.key, entity);
        if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
            for (Property p : table.pmap.values()) {
                args[i] = FieldUtil.get(p.field, entity);
                i++;
            }
        }
        return args;
    }

    public static SQLStatement buildUpdateSql(Object entity, ColumnsValue cvs, ConflictAlgorithm algorithm) {
        return buildUpdateSql(entity, cvs, algorithm, true);
    }

    public static SQLStatement buildUpdateAllSql(Object entity, ColumnsValue cvs, ConflictAlgorithm algorithm) {
        return buildUpdateSql(entity, cvs, algorithm, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d2 A[Catch:{ Exception -> 0x00f4 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.litesuits.orm.db.assit.SQLStatement buildUpdateSql(java.lang.Object r11, com.litesuits.orm.db.model.ColumnsValue r12, com.litesuits.orm.db.model.ConflictAlgorithm r13, boolean r14) {
        /*
            com.litesuits.orm.db.assit.SQLStatement r0 = new com.litesuits.orm.db.assit.SQLStatement
            r0.<init>()
            com.litesuits.orm.db.model.EntityTable r1 = com.litesuits.orm.db.TableManager.getTable((java.lang.Object) r11)     // Catch:{ Exception -> 0x00f4 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f4 }
            r3 = 128(0x80, float:1.794E-43)
            r2.<init>(r3)     // Catch:{ Exception -> 0x00f4 }
            java.lang.String r3 = "UPDATE "
            r2.append(r3)     // Catch:{ Exception -> 0x00f4 }
            if (r13 == 0) goto L_0x001e
            java.lang.String r3 = r13.getAlgorithm()     // Catch:{ Exception -> 0x00f4 }
            r2.append(r3)     // Catch:{ Exception -> 0x00f4 }
        L_0x001e:
            java.lang.String r3 = r1.name     // Catch:{ Exception -> 0x00f4 }
            r2.append(r3)     // Catch:{ Exception -> 0x00f4 }
            java.lang.String r3 = " SET "
            r2.append(r3)     // Catch:{ Exception -> 0x00f4 }
            r3 = 1
            r4 = 0
            r5 = 0
            java.lang.String r6 = ","
            java.lang.String r7 = "=?"
            if (r12 == 0) goto L_0x007b
            boolean r8 = r12.checkColumns()     // Catch:{ Exception -> 0x00f4 }
            if (r8 == 0) goto L_0x007b
            if (r14 == 0) goto L_0x0040
            java.lang.String[] r8 = r12.columns     // Catch:{ Exception -> 0x00f4 }
            int r8 = r8.length     // Catch:{ Exception -> 0x00f4 }
            int r3 = r3 + r8
            java.lang.Object[] r8 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x00f4 }
            r5 = r8
        L_0x0040:
            java.lang.String[] r8 = r12.columns     // Catch:{ Exception -> 0x00f4 }
            int r8 = r8.length     // Catch:{ Exception -> 0x00f4 }
            if (r4 >= r8) goto L_0x00d0
            if (r4 <= 0) goto L_0x004a
            r2.append(r6)     // Catch:{ Exception -> 0x00f4 }
        L_0x004a:
            java.lang.String[] r8 = r12.columns     // Catch:{ Exception -> 0x00f4 }
            r8 = r8[r4]     // Catch:{ Exception -> 0x00f4 }
            r2.append(r8)     // Catch:{ Exception -> 0x00f4 }
            r2.append(r7)     // Catch:{ Exception -> 0x00f4 }
            if (r14 == 0) goto L_0x0078
            java.lang.String[] r8 = r12.columns     // Catch:{ Exception -> 0x00f4 }
            r8 = r8[r4]     // Catch:{ Exception -> 0x00f4 }
            java.lang.Object r8 = r12.getValue(r8)     // Catch:{ Exception -> 0x00f4 }
            r5[r4] = r8     // Catch:{ Exception -> 0x00f4 }
            r8 = r5[r4]     // Catch:{ Exception -> 0x00f4 }
            if (r8 != 0) goto L_0x0078
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r8 = r1.pmap     // Catch:{ Exception -> 0x00f4 }
            java.lang.String[] r9 = r12.columns     // Catch:{ Exception -> 0x00f4 }
            r9 = r9[r4]     // Catch:{ Exception -> 0x00f4 }
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x00f4 }
            com.litesuits.orm.db.model.Property r8 = (com.litesuits.orm.db.model.Property) r8     // Catch:{ Exception -> 0x00f4 }
            java.lang.reflect.Field r8 = r8.field     // Catch:{ Exception -> 0x00f4 }
            java.lang.Object r8 = com.litesuits.orm.db.utils.FieldUtil.get(r8, r11)     // Catch:{ Exception -> 0x00f4 }
            r5[r4] = r8     // Catch:{ Exception -> 0x00f4 }
        L_0x0078:
            int r4 = r4 + 1
            goto L_0x0040
        L_0x007b:
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r8 = r1.pmap     // Catch:{ Exception -> 0x00f4 }
            boolean r8 = com.litesuits.orm.db.assit.Checker.isEmpty((java.util.Map<?, ?>) r8)     // Catch:{ Exception -> 0x00f4 }
            if (r8 != 0) goto L_0x00cb
            if (r14 == 0) goto L_0x008f
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r8 = r1.pmap     // Catch:{ Exception -> 0x00f4 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x00f4 }
            int r3 = r3 + r8
            java.lang.Object[] r8 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x00f4 }
            r5 = r8
        L_0x008f:
            java.util.LinkedHashMap<java.lang.String, com.litesuits.orm.db.model.Property> r8 = r1.pmap     // Catch:{ Exception -> 0x00f4 }
            java.util.Set r8 = r8.entrySet()     // Catch:{ Exception -> 0x00f4 }
            java.util.Iterator r8 = r8.iterator()     // Catch:{ Exception -> 0x00f4 }
        L_0x0099:
            boolean r9 = r8.hasNext()     // Catch:{ Exception -> 0x00f4 }
            if (r9 == 0) goto L_0x00ca
            java.lang.Object r9 = r8.next()     // Catch:{ Exception -> 0x00f4 }
            java.util.Map$Entry r9 = (java.util.Map.Entry) r9     // Catch:{ Exception -> 0x00f4 }
            if (r4 <= 0) goto L_0x00aa
            r2.append(r6)     // Catch:{ Exception -> 0x00f4 }
        L_0x00aa:
            java.lang.Object r10 = r9.getKey()     // Catch:{ Exception -> 0x00f4 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x00f4 }
            r2.append(r10)     // Catch:{ Exception -> 0x00f4 }
            r2.append(r7)     // Catch:{ Exception -> 0x00f4 }
            if (r14 == 0) goto L_0x00c6
            java.lang.Object r10 = r9.getValue()     // Catch:{ Exception -> 0x00f4 }
            com.litesuits.orm.db.model.Property r10 = (com.litesuits.orm.db.model.Property) r10     // Catch:{ Exception -> 0x00f4 }
            java.lang.reflect.Field r10 = r10.field     // Catch:{ Exception -> 0x00f4 }
            java.lang.Object r10 = com.litesuits.orm.db.utils.FieldUtil.get(r10, r11)     // Catch:{ Exception -> 0x00f4 }
            r5[r4] = r10     // Catch:{ Exception -> 0x00f4 }
        L_0x00c6:
            int r4 = r4 + 1
            goto L_0x0099
        L_0x00ca:
            goto L_0x00d0
        L_0x00cb:
            if (r14 == 0) goto L_0x00d0
            java.lang.Object[] r6 = new java.lang.Object[r3]     // Catch:{ Exception -> 0x00f4 }
            r5 = r6
        L_0x00d0:
            if (r14 == 0) goto L_0x00dc
            int r6 = r3 + -1
            com.litesuits.orm.db.model.Primarykey r8 = r1.key     // Catch:{ Exception -> 0x00f4 }
            java.lang.Object r8 = com.litesuits.orm.db.utils.FieldUtil.getAssignedKeyObject(r8, r11)     // Catch:{ Exception -> 0x00f4 }
            r5[r6] = r8     // Catch:{ Exception -> 0x00f4 }
        L_0x00dc:
            java.lang.String r6 = " WHERE "
            r2.append(r6)     // Catch:{ Exception -> 0x00f4 }
            com.litesuits.orm.db.model.Primarykey r6 = r1.key     // Catch:{ Exception -> 0x00f4 }
            java.lang.String r6 = r6.column     // Catch:{ Exception -> 0x00f4 }
            r2.append(r6)     // Catch:{ Exception -> 0x00f4 }
            r2.append(r7)     // Catch:{ Exception -> 0x00f4 }
            java.lang.String r6 = r2.toString()     // Catch:{ Exception -> 0x00f4 }
            r0.sql = r6     // Catch:{ Exception -> 0x00f4 }
            r0.bindArgs = r5     // Catch:{ Exception -> 0x00f4 }
            goto L_0x00f8
        L_0x00f4:
            r1 = move-exception
            r1.printStackTrace()
        L_0x00f8:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.litesuits.orm.db.assit.SQLBuilder.buildUpdateSql(java.lang.Object, com.litesuits.orm.db.model.ColumnsValue, com.litesuits.orm.db.model.ConflictAlgorithm, boolean):com.litesuits.orm.db.assit.SQLStatement");
    }

    public static Object[] buildUpdateSqlArgsOnly(Object entity, ColumnsValue cvs) throws IllegalAccessException {
        Object[] args;
        EntityTable table = TableManager.getTable(entity);
        int size = 1;
        int i = 0;
        if (cvs != null && cvs.checkColumns()) {
            size = 1 + cvs.columns.length;
            args = new Object[size];
            while (i < cvs.columns.length) {
                args[i] = cvs.getValue(cvs.columns[i]);
                if (args[i] == null) {
                    args[i] = FieldUtil.get(table.pmap.get(cvs.columns[i]).field, entity);
                }
                i++;
            }
        } else if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
            size = 1 + table.pmap.size();
            args = new Object[size];
            for (Map.Entry<String, Property> en : table.pmap.entrySet()) {
                args[i] = FieldUtil.get(en.getValue().field, entity);
                i++;
            }
        } else {
            args = new Object[1];
        }
        args[size - 1] = FieldUtil.getAssignedKeyObject(table.key, entity);
        return args;
    }

    public static SQLStatement buildUpdateSql(WhereBuilder where, ColumnsValue cvs, ConflictAlgorithm algorithm) {
        Object[] args;
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable((Class<?>) where.getTableClass());
            StringBuilder sql = new StringBuilder(128);
            sql.append(UPDATE);
            if (algorithm != null) {
                sql.append(algorithm.getAlgorithm());
            }
            sql.append(table.name);
            sql.append(SET);
            if (cvs == null || !cvs.checkColumns()) {
                args = where.getWhereArgs();
            } else {
                Object[] wArgs = where.getWhereArgs();
                if (wArgs != null) {
                    args = new Object[(cvs.columns.length + wArgs.length)];
                } else {
                    args = new Object[cvs.columns.length];
                }
                int i = 0;
                while (i < cvs.columns.length) {
                    if (i > 0) {
                        sql.append(",");
                    }
                    sql.append(cvs.columns[i]);
                    sql.append("=?");
                    args[i] = cvs.getValue(cvs.columns[i]);
                    i++;
                }
                if (wArgs != null) {
                    Object[] arr$ = wArgs;
                    int len$ = arr$.length;
                    int i$ = 0;
                    while (i$ < len$) {
                        args[i] = arr$[i$];
                        i$++;
                        i++;
                    }
                }
            }
            sql.append(where.createWhereString());
            stmt.sql = sql.toString();
            stmt.bindArgs = args;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public static SQLStatement buildDeleteSql(Object entity) {
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable(entity);
            if (table.key != null) {
                stmt.sql = "DELETE FROM " + table.name + " WHERE " + table.key.column + "=?";
                stmt.bindArgs = new String[]{String.valueOf(FieldUtil.get(table.key.field, entity))};
            } else if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
                StringBuilder sb = new StringBuilder();
                sb.append("DELETE FROM ");
                sb.append(table.name);
                sb.append(" WHERE ");
                Object[] args = new Object[table.pmap.size()];
                int i = 0;
                for (Map.Entry<String, Property> en : table.pmap.entrySet()) {
                    if (i == 0) {
                        sb.append(en.getKey());
                        sb.append("=?");
                    } else {
                        sb.append(" AND ");
                        sb.append(en.getKey());
                        sb.append("=?");
                    }
                    args[i] = FieldUtil.get(en.getValue().field, entity);
                    i++;
                }
                stmt.sql = sb.toString();
                stmt.bindArgs = args;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public static SQLStatement buildDeleteSql(Collection<?> collection) {
        SQLStatement stmt = new SQLStatement();
        try {
            StringBuilder sb = new StringBuilder(256);
            EntityTable table = null;
            Object[] args = new Object[collection.size()];
            int i = 0;
            for (Object entity : collection) {
                if (i == 0) {
                    table = TableManager.getTable(entity);
                    sb.append("DELETE FROM ");
                    sb.append(table.name);
                    sb.append(" WHERE ");
                    sb.append(table.key.column);
                    sb.append(IN);
                    sb.append(PARENTHESES_LEFT);
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
                args[i] = FieldUtil.get(table.key.field, entity);
                i++;
            }
            sb.append(PARENTHESES_RIGHT);
            stmt.sql = sb.toString();
            stmt.bindArgs = args;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public static SQLStatement buildDeleteAllSql(Class<?> claxx) {
        SQLStatement stmt = new SQLStatement();
        EntityTable table = TableManager.getTable(claxx);
        stmt.sql = "DELETE FROM " + table.name;
        return stmt;
    }

    public static SQLStatement buildDeleteSql(Class<?> claxx, long start, long end, String orderAscColumn) {
        SQLStatement stmt = new SQLStatement();
        EntityTable table = TableManager.getTable(claxx);
        String key = table.key.column;
        String orderBy = Checker.isEmpty((CharSequence) orderAscColumn) ? key : orderAscColumn;
        stmt.sql = "DELETE FROM " + table.name + " WHERE " + key + IN + PARENTHESES_LEFT + "SELECT " + key + " FROM " + table.name + " ORDER BY " + orderBy + ASC + " LIMIT " + start + "," + end + PARENTHESES_RIGHT;
        return stmt;
    }

    public static SQLStatement buildAddColumnSql(String tableName, String column) {
        SQLStatement stmt = new SQLStatement();
        stmt.sql = "ALTER TABLE " + tableName + " ADD COLUMN " + column;
        return stmt;
    }

    public static MapInfo buildDelAllMappingSql(Class claxx) {
        EntityTable table1 = TableManager.getTable((Class<?>) claxx);
        if (Checker.isEmpty((Collection<?>) table1.mappingList)) {
            return null;
        }
        try {
            MapInfo mapInfo = new MapInfo();
            Iterator i$ = table1.mappingList.iterator();
            while (i$.hasNext()) {
                EntityTable table2 = TableManager.getTable((Class<?>) getTypeByRelation(i$.next()));
                mapInfo.addTable(new MapInfo.MapTable(TableManager.getMapTableName(table1, table2), table1.name, table2.name));
                mapInfo.addDelOldRelationSQL(buildMappingDeleteAllSql(table1, table2));
            }
            return mapInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MapInfo buildMappingInfo(Object entity, boolean insertNew, TableManager tableManager) {
        Object mapObject;
        ArrayList<SQLStatement> sqlList;
        EntityTable table1 = TableManager.getTable(entity);
        if (!Checker.isEmpty((Collection<?>) table1.mappingList)) {
            try {
                Object key1 = FieldUtil.get(table1.key.field, entity);
                if (key1 == null) {
                    return null;
                }
                MapInfo mapInfo = new MapInfo();
                Iterator i$ = table1.mappingList.iterator();
                while (i$.hasNext()) {
                    MapProperty map = i$.next();
                    EntityTable table2 = TableManager.getTable((Class<?>) getTypeByRelation(map));
                    mapInfo.addTable(new MapInfo.MapTable(TableManager.getMapTableName(table1, table2), table1.name, table2.name));
                    if (tableManager.isSQLMapTableCreated(table1.name, table2.name)) {
                        mapInfo.addDelOldRelationSQL(buildMappingDeleteSql(key1, table1, table2));
                    }
                    if (insertNew && (mapObject = FieldUtil.get(map.field, entity)) != null) {
                        if (map.isToMany()) {
                            if (mapObject instanceof Collection) {
                                sqlList = buildMappingToManySql(key1, table1, table2, (Collection) mapObject);
                            } else if (mapObject instanceof Object[]) {
                                sqlList = buildMappingToManySql(key1, table1, table2, Arrays.asList((Object[]) mapObject));
                            } else {
                                throw new RuntimeException("OneToMany and ManyToMany Relation, You must use array or collection object");
                            }
                            if (Checker.isEmpty((Collection<?>) sqlList)) {
                                mapInfo.addNewRelationSQL(sqlList);
                            }
                        } else {
                            SQLStatement st = buildMappingToOneSql(key1, table1, table2, mapObject);
                            if (st != null) {
                                mapInfo.addNewRelationSQL(st);
                            }
                        }
                    }
                }
                return mapInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Class getTypeByRelation(MapProperty mp) {
        if (!mp.isToMany()) {
            return mp.field.getType();
        }
        Class c = mp.field.getType();
        if (ClassUtil.isCollection(c)) {
            return FieldUtil.getGenericType(mp.field);
        }
        if (ClassUtil.isArray(c)) {
            return FieldUtil.getComponentType(mp.field);
        }
        throw new RuntimeException("OneToMany and ManyToMany Relation, you must use collection or array object");
    }

    private static SQLStatement buildMappingDeleteAllSql(EntityTable table1, EntityTable table2) throws IllegalArgumentException, IllegalAccessException {
        if (table2 == null) {
            return null;
        }
        String mapTableName = TableManager.getMapTableName(table1, table2);
        SQLStatement stmt = new SQLStatement();
        stmt.sql = "DELETE FROM " + mapTableName;
        return stmt;
    }

    public static SQLStatement buildMappingDeleteSql(Object key1, EntityTable table1, EntityTable table2) throws IllegalArgumentException, IllegalAccessException {
        if (table2 != null) {
            return buildMappingDeleteSql(TableManager.getMapTableName(table1, table2), key1, table1);
        }
        return null;
    }

    public static SQLStatement buildMappingDeleteSql(String mapTableName, Object key1, EntityTable table1) throws IllegalArgumentException, IllegalAccessException {
        if (mapTableName == null) {
            return null;
        }
        SQLStatement stmt = new SQLStatement();
        stmt.sql = "DELETE FROM " + mapTableName + " WHERE " + table1.name + "=?";
        stmt.bindArgs = new Object[]{key1};
        return stmt;
    }

    public static <T> ArrayList<SQLStatement> buildMappingToManySql(final Object key1, final EntityTable table1, final EntityTable table2, Collection<T> coll) throws Exception {
        final ArrayList<SQLStatement> sqlList = new ArrayList<>();
        CollSpliter.split(coll, 499, new CollSpliter.Spliter<T>() {
            public int oneSplit(ArrayList<T> list) throws Exception {
                SQLStatement sql = SQLBuilder.buildMappingToManySqlFragment(key1, table1, table2, list);
                if (sql == null) {
                    return 0;
                }
                sqlList.add(sql);
                return 0;
            }
        });
        return sqlList;
    }

    /* access modifiers changed from: private */
    public static SQLStatement buildMappingToManySqlFragment(Object key1, EntityTable table1, EntityTable table2, Collection<?> coll) throws IllegalArgumentException, IllegalAccessException {
        String mapTableName = TableManager.getMapTableName(table1, table2);
        if (Checker.isEmpty(coll)) {
            return null;
        }
        boolean isF = true;
        StringBuilder values = new StringBuilder(128);
        ArrayList<String> list = new ArrayList<>();
        String key1Str = String.valueOf(key1);
        for (Object o : coll) {
            Object key2 = FieldUtil.getAssignedKeyObject(table2.key, o);
            if (key2 != null) {
                if (isF) {
                    values.append(TWO_HOLDER);
                    isF = false;
                } else {
                    values.append(",");
                    values.append(TWO_HOLDER);
                }
                list.add(key1Str);
                list.add(String.valueOf(key2));
            }
        }
        Object[] args = list.toArray(new String[list.size()]);
        if (Checker.isEmpty(args)) {
            return null;
        }
        SQLStatement stmt = new SQLStatement();
        stmt.sql = "REPLACE INTO " + mapTableName + PARENTHESES_LEFT + table1.name + "," + table2.name + PARENTHESES_RIGHT + VALUES + values;
        stmt.bindArgs = args;
        return stmt;
    }

    public static SQLStatement buildMappingToOneSql(Object key1, EntityTable table1, EntityTable table2, Object obj) throws IllegalArgumentException, IllegalAccessException {
        Object key2 = FieldUtil.getAssignedKeyObject(table2.key, obj);
        if (key2 != null) {
            return buildMappingToOneSql(TableManager.getMapTableName(table1, table2), key1, key2, table1, table2);
        }
        return null;
    }

    public static SQLStatement buildMappingToOneSql(String mapTableName, Object key1, Object key2, EntityTable table1, EntityTable table2) throws IllegalArgumentException, IllegalAccessException {
        if (key2 == null) {
            return null;
        }
        StringBuilder sql = new StringBuilder(128);
        sql.append(INSERT);
        sql.append(INTO);
        sql.append(mapTableName);
        sql.append(PARENTHESES_LEFT);
        sql.append(table1.name);
        sql.append(",");
        sql.append(table2.name);
        sql.append(PARENTHESES_RIGHT);
        sql.append(VALUES);
        sql.append(TWO_HOLDER);
        SQLStatement stmt = new SQLStatement();
        stmt.sql = sql.toString();
        stmt.bindArgs = new Object[]{key1, key2};
        return stmt;
    }

    public static SQLStatement buildQueryRelationSql(Class class1, Class class2, List<String> key1List) {
        return buildQueryRelationSql(class1, class2, key1List, (List<String>) null);
    }

    private static SQLStatement buildQueryRelationSql(Class class1, Class class2, List<String> key1List, List<String> key2List) {
        EntityTable table1 = TableManager.getTable((Class<?>) class1);
        EntityTable table2 = TableManager.getTable((Class<?>) class2);
        QueryBuilder builder = new QueryBuilder(class1).queryMappingInfo(class2);
        ArrayList<String> keyList = new ArrayList<>();
        StringBuilder sb = null;
        if (!Checker.isEmpty((Collection<?>) key1List)) {
            sb = new StringBuilder();
            sb.append(table1.name);
            sb.append(IN);
            sb.append(PARENTHESES_LEFT);
            int size = key1List.size();
            for (int i = 0; i < size; i++) {
                if (i == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(PARENTHESES_RIGHT);
            keyList.addAll(key1List);
        }
        if (!Checker.isEmpty((Collection<?>) key2List)) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(" AND ");
            }
            sb.append(table2.name);
            sb.append(IN);
            sb.append(PARENTHESES_LEFT);
            int size2 = key2List.size();
            for (int i2 = 0; i2 < size2; i2++) {
                if (i2 == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(PARENTHESES_RIGHT);
            keyList.addAll(key2List);
        }
        if (sb != null) {
            builder.where(sb.toString(), keyList.toArray(new String[keyList.size()]));
        }
        return builder.createStatement();
    }

    public static SQLStatement buildQueryRelationSql(EntityTable table1, EntityTable table2, Object key1) {
        SQLStatement sqlStatement = new SQLStatement();
        sqlStatement.sql = SELECT_ANY_FROM + TableManager.getMapTableName(table1, table2) + " WHERE " + table1.name + "=?";
        sqlStatement.bindArgs = new String[]{String.valueOf(key1)};
        return sqlStatement;
    }

    public static SQLStatement buildQueryMapEntitySql(EntityTable table2, Object key2) {
        SQLStatement sqlStatement = new SQLStatement();
        sqlStatement.sql = SELECT_ANY_FROM + table2.name + " WHERE " + table2.key.column + "=?";
        sqlStatement.bindArgs = new String[]{String.valueOf(key2)};
        return sqlStatement;
    }
}
