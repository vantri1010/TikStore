package com.litesuits.orm.db;

import android.database.sqlite.SQLiteDatabase;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.RelationKey;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface DataBase {
    void close();

    SQLStatement createSQLStatement(String str, Object[] objArr);

    int delete(WhereBuilder whereBuilder);

    <T> int delete(Class<T> cls);

    <T> int delete(Class<T> cls, long j, long j2, String str);

    <T> int delete(Class<T> cls, WhereBuilder whereBuilder);

    int delete(Object obj);

    <T> int delete(Collection<T> collection);

    <T> int deleteAll(Class<T> cls);

    boolean deleteDatabase();

    boolean deleteDatabase(File file);

    boolean dropTable(Class<?> cls);

    @Deprecated
    boolean dropTable(Object obj);

    boolean dropTable(String str);

    boolean execute(SQLiteDatabase sQLiteDatabase, SQLStatement sQLStatement);

    DataBaseConfig getDataBaseConfig();

    SQLiteDatabase getReadableDatabase();

    SQLiteHelper getSQLiteHelper();

    TableManager getTableManager();

    SQLiteDatabase getWritableDatabase();

    <T> int insert(Collection<T> collection);

    <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);

    long insert(Object obj);

    long insert(Object obj, ConflictAlgorithm conflictAlgorithm);

    <E, T> boolean mapping(Collection<E> collection, Collection<T> collection2);

    SQLiteDatabase openOrCreateDatabase();

    SQLiteDatabase openOrCreateDatabase(String str, SQLiteDatabase.CursorFactory cursorFactory);

    <T> ArrayList<T> query(QueryBuilder<T> queryBuilder);

    <T> ArrayList<T> query(Class<T> cls);

    <T> T queryById(long j, Class<T> cls);

    <T> T queryById(String str, Class<T> cls);

    long queryCount(QueryBuilder queryBuilder);

    <T> long queryCount(Class<T> cls);

    ArrayList<RelationKey> queryRelation(Class cls, Class cls2, List<String> list);

    <T> int save(Collection<T> collection);

    long save(Object obj);

    int update(WhereBuilder whereBuilder, ColumnsValue columnsValue, ConflictAlgorithm conflictAlgorithm);

    int update(Object obj);

    int update(Object obj, ColumnsValue columnsValue, ConflictAlgorithm conflictAlgorithm);

    int update(Object obj, ConflictAlgorithm conflictAlgorithm);

    <T> int update(Collection<T> collection);

    <T> int update(Collection<T> collection, ColumnsValue columnsValue, ConflictAlgorithm conflictAlgorithm);

    <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm);
}
