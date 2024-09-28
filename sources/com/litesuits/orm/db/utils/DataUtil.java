package com.litesuits.orm.db.utils;

import android.database.Cursor;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.Property;
import com.litesuits.orm.log.OrmLog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataUtil implements Serializable {
    public static final String BLOB = " BLOB ";
    public static final int CLASS_TYPE_BOOLEAN = 2;
    public static final int CLASS_TYPE_BYTE = 8;
    public static final int CLASS_TYPE_BYTE_ARRAY = 9;
    public static final int CLASS_TYPE_CHAR = 10;
    public static final int CLASS_TYPE_DATE = 11;
    public static final int CLASS_TYPE_DOUBLE = 3;
    public static final int CLASS_TYPE_FLOAT = 4;
    public static final int CLASS_TYPE_INT = 6;
    public static final int CLASS_TYPE_LONG = 5;
    public static final int CLASS_TYPE_NONE = 0;
    public static final int CLASS_TYPE_SERIALIZABLE = 12;
    public static final int CLASS_TYPE_SHORT = 7;
    public static final int CLASS_TYPE_STRING = 1;
    public static final int CLASS_TYPE_UNKNOWN = 13;
    public static final int FIELD_TYPE_BLOB = 4;
    public static final int FIELD_TYPE_DATE = 5;
    public static final int FIELD_TYPE_LONG = 1;
    public static final int FIELD_TYPE_NULL = 0;
    public static final int FIELD_TYPE_REAL = 2;
    public static final int FIELD_TYPE_SERIALIZABLE = 6;
    public static final int FIELD_TYPE_STRING = 3;
    public static final String INTEGER = " INTEGER ";
    public static final String NULL = " NULL ";
    public static final String REAL = " REAL ";
    public static final String TAG = DataUtil.class.getSimpleName();
    public static final String TEXT = " TEXT ";
    private static final long serialVersionUID = 6668874253056236676L;

    public static int getType(Object obj) {
        if (obj == null) {
            return 0;
        }
        if ((obj instanceof CharSequence) || (obj instanceof Boolean) || (obj instanceof Character)) {
            return 3;
        }
        if ((obj instanceof Float) || (obj instanceof Double)) {
            return 2;
        }
        if (obj instanceof Number) {
            return 1;
        }
        if (obj instanceof Date) {
            return 5;
        }
        if (obj instanceof byte[]) {
            return 4;
        }
        if (obj instanceof Serializable) {
            return 6;
        }
        return 0;
    }

    public static String getSQLDataType(int classType) {
        switch (classType) {
            case 1:
            case 2:
            case 10:
                return TEXT;
            case 3:
            case 4:
                return REAL;
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
                return INTEGER;
            default:
                return BLOB;
        }
    }

    public static void injectDataToObject(Cursor c, Object entity, EntityTable table) throws Exception {
        int size = c.getColumnCount();
        for (int i = 0; i < size; i++) {
            String col = c.getColumnName(i);
            Property p = null;
            if (!Checker.isEmpty((Map<?, ?>) table.pmap)) {
                p = table.pmap.get(col);
            }
            if (p == null && table.key != null && col.equals(table.key.column)) {
                p = table.key;
            }
            if (p != null) {
                Field f = p.field;
                f.setAccessible(true);
                switch (p.classType) {
                    case 1:
                        f.set(entity, c.getString(i));
                        break;
                    case 2:
                        f.set(entity, Boolean.valueOf(Boolean.parseBoolean(c.getString(i))));
                        break;
                    case 3:
                        f.set(entity, Double.valueOf(c.getDouble(i)));
                        break;
                    case 4:
                        f.set(entity, Float.valueOf(c.getFloat(i)));
                        break;
                    case 5:
                        f.set(entity, Long.valueOf(c.getLong(i)));
                        break;
                    case 6:
                        f.set(entity, Integer.valueOf(c.getInt(i)));
                        break;
                    case 7:
                        f.set(entity, Short.valueOf(c.getShort(i)));
                        break;
                    case 8:
                        if (c.getString(i) == null) {
                            break;
                        } else {
                            f.set(entity, Byte.valueOf(Byte.parseByte(c.getString(i))));
                            break;
                        }
                    case 9:
                        f.set(entity, c.getBlob(i));
                        break;
                    case 10:
                        String value = c.getString(i);
                        if (Checker.isEmpty((CharSequence) value)) {
                            break;
                        } else {
                            f.set(entity, Character.valueOf(value.charAt(0)));
                            break;
                        }
                    case 11:
                        Long time = Long.valueOf(c.getLong(i));
                        if (time == null) {
                            break;
                        } else {
                            f.set(entity, new Date(time.longValue()));
                            break;
                        }
                    case 12:
                        byte[] bytes = c.getBlob(i);
                        if (bytes == null) {
                            break;
                        } else {
                            f.set(entity, byteToObject(bytes));
                            break;
                        }
                }
            } else if (OrmLog.isPrint) {
                String str = TAG;
                OrmLog.w(str, "数据库字段[" + col + "]已在实体中被移除");
            }
        }
    }

    public static int getFieldClassType(Field f) {
        Class type = f.getType();
        if (CharSequence.class.isAssignableFrom(type)) {
            return 1;
        }
        if (Boolean.TYPE.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return 2;
        }
        if (Double.TYPE.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return 3;
        }
        if (Float.TYPE.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return 4;
        }
        if (Long.TYPE.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return 5;
        }
        if (Integer.TYPE.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return 6;
        }
        if (Short.TYPE.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return 7;
        }
        if (Byte.TYPE.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return 8;
        }
        if (byte[].class.isAssignableFrom(type) || Byte[].class.isAssignableFrom(type)) {
            return 9;
        }
        if (Character.TYPE.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            return 10;
        }
        if (Date.class.isAssignableFrom(type)) {
            return 11;
        }
        if (Serializable.class.isAssignableFrom(type)) {
            return 12;
        }
        return 13;
    }

    public static Object byteToObject(byte[] bytes) throws Exception {
        ObjectInputStream ois = null;
        try {
            ObjectInputStream ois2 = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object readObject = ois2.readObject();
            ois2.close();
            return readObject;
        } catch (Throwable th) {
            if (ois != null) {
                ois.close();
            }
            throw th;
        }
    }

    public static byte[] objectToByte(Object obj) throws IOException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos2 = new ObjectOutputStream(bos);
            oos2.writeObject(obj);
            byte[] byteArray = bos.toByteArray();
            oos2.close();
            return byteArray;
        } catch (Throwable th) {
            if (oos != null) {
                oos.close();
            }
            throw th;
        }
    }

    public static List<?> arrayToList(Object[] array) {
        return Arrays.asList(array);
    }

    public static Object[] arrayToList(Collection<?> coll) {
        return coll.toArray();
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array2 : rest) {
            System.arraycopy(array2, 0, result, offset, array2.length);
            offset += array2.length;
        }
        return result;
    }
}
