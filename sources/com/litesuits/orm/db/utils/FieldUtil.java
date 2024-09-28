package com.litesuits.orm.db.utils;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.model.Primarykey;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class FieldUtil {
    public static boolean isIgnored(Field f) {
        return f.getAnnotation(Ignore.class) != null;
    }

    public static boolean isInvalid(Field f) {
        return (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) || isIgnored(f) || f.isSynthetic();
    }

    public static boolean isLong(Field field) {
        return field.getType() == Long.TYPE || field.getType() == Long.class;
    }

    public static boolean isInteger(Field field) {
        return field.getType() == Integer.TYPE || field.getType() != Integer.class;
    }

    public static boolean isSerializable(Field f) {
        for (Class<?> c : f.getType().getInterfaces()) {
            if (Serializable.class == c) {
                return true;
            }
        }
        return false;
    }

    public static void set(Field f, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        f.set(obj, value);
    }

    public static Object get(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        return f.get(obj);
    }

    public static Class<?> getGenericType(Field f) {
        Type type = f.getGenericType();
        if (type instanceof ParameterizedType) {
            Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type2 instanceof Class) {
                return (Class) type2;
            }
            return null;
        } else if (type instanceof Class) {
            return (Class) type;
        } else {
            return null;
        }
    }

    public static Class<?> getComponentType(Field f) {
        return f.getType().getComponentType();
    }

    public static Object getAssignedKeyObject(Primarykey key, Object entity) throws IllegalArgumentException, IllegalAccessException {
        Object obj = get(key.field, entity);
        if (key.isAssignedByMyself() || (key.isAssignedBySystem() && obj != null && ((Number) obj).longValue() > 0)) {
            return obj;
        }
        return null;
    }

    public static boolean setKeyValueIfneed(Object entity, Primarykey key, Object keyObj, long rowID) throws IllegalArgumentException, IllegalAccessException {
        if (key == null || !key.isAssignedBySystem()) {
            return false;
        }
        if (keyObj != null && ((Number) keyObj).longValue() >= 1) {
            return false;
        }
        setNumber(entity, key.field, rowID);
        return true;
    }

    public static List<Field> getAllDeclaredFields(Class<?> cls) {
        LinkedList<Field> fieldList = new LinkedList<>();
        Class<? super Object> claxx = cls;
        while (claxx != null && claxx != Object.class) {
            Field[] fs = claxx.getDeclaredFields();
            for (Field f : fs) {
                if (!isInvalid(f)) {
                    fieldList.addLast(f);
                }
            }
            claxx = claxx.getSuperclass();
        }
        return fieldList;
    }

    public static void setNumber(Object o, Field field, long n) throws IllegalAccessException {
        field.setAccessible(true);
        Class claxx = field.getType();
        if (claxx == Long.TYPE) {
            field.setLong(o, n);
        } else if (claxx == Integer.TYPE) {
            field.setInt(o, (int) n);
        } else if (claxx == Short.TYPE) {
            field.setShort(o, (short) ((int) n));
        } else if (claxx == Byte.TYPE) {
            field.setByte(o, (byte) ((int) n));
        } else if (claxx == Long.class) {
            field.set(o, new Long(n));
        } else if (claxx == Integer.class) {
            field.set(o, new Integer((int) n));
        } else if (claxx == Short.class) {
            field.set(o, new Short((short) ((int) n)));
        } else if (claxx == Byte.class) {
            field.set(o, new Byte((byte) ((int) n)));
        } else {
            throw new RuntimeException("field is not a number class");
        }
    }

    public static boolean isNumber(Class<?> claxx) {
        return claxx == Long.TYPE || claxx == Long.class || claxx == Integer.TYPE || claxx == Integer.class || claxx == Short.TYPE || claxx == Short.class || claxx == Byte.TYPE || claxx == Byte.class;
    }
}
