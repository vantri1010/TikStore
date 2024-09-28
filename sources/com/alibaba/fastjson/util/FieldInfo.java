package com.alibaba.fastjson.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class FieldInfo implements Comparable<FieldInfo> {
    private final Class<?> declaringClass;
    private final Field field;
    private final Class<?> fieldClass;
    private final Type fieldType;
    private boolean getOnly;
    private final Method method;
    private final String name;
    private int ordinal;

    public FieldInfo(String name2, Class<?> declaringClass2, Class<?> fieldClass2, Type fieldType2, Field field2, int ordinal2, int serialzeFeatures) {
        this.ordinal = 0;
        this.getOnly = false;
        this.name = name2;
        this.declaringClass = declaringClass2;
        this.fieldClass = fieldClass2;
        this.fieldType = fieldType2;
        this.method = null;
        this.field = field2;
        this.ordinal = ordinal2;
        if (field2 != null) {
            TypeUtils.setAccessible(field2);
        }
    }

    public FieldInfo(String name2, Method method2, Field field2) {
        this(name2, method2, field2, (Class<?>) null, (Type) null);
    }

    public FieldInfo(String name2, Method method2, Field field2, int ordinal2, int serialzeFeatures) {
        this(name2, method2, field2, (Class<?>) null, (Type) null, ordinal2, serialzeFeatures);
    }

    public FieldInfo(String name2, Method method2, Field field2, Class<?> clazz, Type type) {
        this(name2, method2, field2, clazz, type, 0, 0);
    }

    public FieldInfo(String name2, Method method2, Field field2, Class<?> clazz, Type type, int ordinal2, int serialzeFeatures) {
        Class<?> fieldClass2;
        Type fieldType2;
        Type genericFieldType;
        this.ordinal = 0;
        this.getOnly = false;
        this.name = name2;
        this.method = method2;
        this.field = field2;
        this.ordinal = ordinal2;
        if (method2 != null) {
            TypeUtils.setAccessible(method2);
        }
        if (field2 != null) {
            TypeUtils.setAccessible(field2);
        }
        if (method2 != null) {
            if (method2.getParameterTypes().length == 1) {
                fieldClass2 = method2.getParameterTypes()[0];
                fieldType2 = method2.getGenericParameterTypes()[0];
            } else {
                fieldClass2 = method2.getReturnType();
                fieldType2 = method2.getGenericReturnType();
                this.getOnly = true;
            }
            this.declaringClass = method2.getDeclaringClass();
        } else {
            fieldClass2 = field2.getType();
            fieldType2 = field2.getGenericType();
            this.declaringClass = field2.getDeclaringClass();
        }
        if (clazz == null || fieldClass2 != Object.class || !(fieldType2 instanceof TypeVariable) || (genericFieldType = getInheritGenericType(clazz, (TypeVariable) fieldType2)) == null) {
            Type genericFieldType2 = getFieldType(clazz, type, fieldType2);
            if (genericFieldType2 != fieldType2) {
                if (genericFieldType2 instanceof ParameterizedType) {
                    fieldClass2 = TypeUtils.getClass(genericFieldType2);
                } else if (genericFieldType2 instanceof Class) {
                    fieldClass2 = TypeUtils.getClass(genericFieldType2);
                }
            }
            this.fieldType = genericFieldType2;
            this.fieldClass = fieldClass2;
            return;
        }
        this.fieldClass = TypeUtils.getClass(genericFieldType);
        this.fieldType = genericFieldType;
    }

    public static Type getFieldType(Class<?> clazz, Type type, Type fieldType2) {
        if (clazz == null || type == null) {
            return fieldType2;
        }
        if (fieldType2 instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) fieldType2).getGenericComponentType();
            Type componentTypeX = getFieldType(clazz, type, componentType);
            if (componentType != componentTypeX) {
                return Array.newInstance(TypeUtils.getClass(componentTypeX), 0).getClass();
            }
            return fieldType2;
        } else if (!TypeUtils.isGenericParamType(type)) {
            return fieldType2;
        } else {
            if (fieldType2 instanceof TypeVariable) {
                ParameterizedType paramType = (ParameterizedType) TypeUtils.getGenericParamType(type);
                Class<?> parameterizedClass = TypeUtils.getClass(paramType);
                TypeVariable<?> typeVar = (TypeVariable) fieldType2;
                for (int i = 0; i < parameterizedClass.getTypeParameters().length; i++) {
                    if (parameterizedClass.getTypeParameters()[i].getName().equals(typeVar.getName())) {
                        return paramType.getActualTypeArguments()[i];
                    }
                }
            }
            if (fieldType2 instanceof ParameterizedType) {
                ParameterizedType parameterizedFieldType = (ParameterizedType) fieldType2;
                Type[] arguments = parameterizedFieldType.getActualTypeArguments();
                boolean changed = false;
                for (int i2 = 0; i2 < arguments.length; i2++) {
                    Type feildTypeArguement = arguments[i2];
                    if (feildTypeArguement instanceof TypeVariable) {
                        TypeVariable<?> typeVar2 = (TypeVariable) feildTypeArguement;
                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            for (int j = 0; j < clazz.getTypeParameters().length; j++) {
                                if (clazz.getTypeParameters()[j].getName().equals(typeVar2.getName())) {
                                    arguments[i2] = parameterizedType.getActualTypeArguments()[j];
                                    changed = true;
                                }
                            }
                        }
                    }
                }
                if (changed) {
                    return new ParameterizedTypeImpl(arguments, parameterizedFieldType.getOwnerType(), parameterizedFieldType.getRawType());
                }
            }
            return fieldType2;
        }
    }

    public static Type getInheritGenericType(Class<?> clazz, TypeVariable<?> typeVariable) {
        Type type;
        GenericDeclaration gd = typeVariable.getGenericDeclaration();
        do {
            type = clazz.getGenericSuperclass();
            if (type == null) {
                return null;
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                if (ptype.getRawType() == gd) {
                    TypeVariable<?>[] tvs = gd.getTypeParameters();
                    Type[] types = ptype.getActualTypeArguments();
                    for (int i = 0; i < tvs.length; i++) {
                        if (tvs[i] == typeVariable) {
                            return types[i];
                        }
                    }
                    return null;
                }
            }
            clazz = TypeUtils.getClass(type);
        } while (type != null);
        return null;
    }

    public String toString() {
        return this.name;
    }

    public Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    public Class<?> getFieldClass() {
        return this.fieldClass;
    }

    public Type getFieldType() {
        return this.fieldType;
    }

    public String getName() {
        return this.name;
    }

    public String gerQualifiedName() {
        Member member = getMember();
        return member.getDeclaringClass().getName() + "." + member.getName();
    }

    public Member getMember() {
        Method method2 = this.method;
        if (method2 != null) {
            return method2;
        }
        return this.field;
    }

    public Method getMethod() {
        return this.method;
    }

    public Field getField() {
        return this.field;
    }

    public int compareTo(FieldInfo o) {
        int i = this.ordinal;
        int i2 = o.ordinal;
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        return this.name.compareTo(o.name);
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Field field2;
        T annotation = null;
        Method method2 = this.method;
        if (method2 != null) {
            annotation = method2.getAnnotation(annotationClass);
        }
        if (annotation != null || (field2 = this.field) == null) {
            return annotation;
        }
        return field2.getAnnotation(annotationClass);
    }

    public Object get(Object javaObject) throws IllegalAccessException, InvocationTargetException {
        Method method2 = this.method;
        if (method2 != null) {
            return method2.invoke(javaObject, new Object[0]);
        }
        return this.field.get(javaObject);
    }

    public void set(Object javaObject, Object value) throws IllegalAccessException, InvocationTargetException {
        Method method2 = this.method;
        if (method2 != null) {
            method2.invoke(javaObject, new Object[]{value});
            return;
        }
        this.field.set(javaObject, value);
    }

    public void setAccessible(boolean flag) throws SecurityException {
        Method method2 = this.method;
        if (method2 != null) {
            TypeUtils.setAccessible(method2);
        } else {
            TypeUtils.setAccessible(this.field);
        }
    }

    public boolean isGetOnly() {
        return this.getOnly;
    }
}
