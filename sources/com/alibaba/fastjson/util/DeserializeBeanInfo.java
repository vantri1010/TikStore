package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeserializeBeanInfo {
    private Constructor<?> creatorConstructor;
    private Constructor<?> defaultConstructor;
    private Method factoryMethod;
    private final List<FieldInfo> fieldList = new ArrayList();
    private int parserFeatures = 0;
    private final List<FieldInfo> sortedFieldList = new ArrayList();

    public DeserializeBeanInfo(Class<?> clazz) {
        this.parserFeatures = TypeUtils.getParserFeatures(clazz);
    }

    public Constructor<?> getDefaultConstructor() {
        return this.defaultConstructor;
    }

    public void setDefaultConstructor(Constructor<?> defaultConstructor2) {
        this.defaultConstructor = defaultConstructor2;
    }

    public Constructor<?> getCreatorConstructor() {
        return this.creatorConstructor;
    }

    public void setCreatorConstructor(Constructor<?> createConstructor) {
        this.creatorConstructor = createConstructor;
    }

    public Method getFactoryMethod() {
        return this.factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod2) {
        this.factoryMethod = factoryMethod2;
    }

    public List<FieldInfo> getFieldList() {
        return this.fieldList;
    }

    public List<FieldInfo> getSortedFieldList() {
        return this.sortedFieldList;
    }

    public boolean add(FieldInfo field) {
        for (FieldInfo item : this.fieldList) {
            if (item.getName().equals(field.getName()) && (!item.isGetOnly() || field.isGetOnly())) {
                return false;
            }
        }
        this.fieldList.add(field);
        this.sortedFieldList.add(field);
        Collections.sort(this.sortedFieldList);
        return true;
    }

    public static DeserializeBeanInfo computeSetters(Class<?> clazz, Type type) {
        String propertyName;
        Field[] arr$;
        String propertyName2;
        int serialzeFeatures;
        int ordinal;
        JSONField annotation;
        Method method;
        String methodName;
        int i;
        String propertyName3;
        Field field;
        int serialzeFeatures2;
        int ordinal2;
        JSONField fieldAnnotation;
        JSONField fieldAnnotation2;
        Class<?> cls = clazz;
        DeserializeBeanInfo beanInfo = new DeserializeBeanInfo(cls);
        Constructor<?> defaultConstructor2 = getDefaultConstructor(clazz);
        if (defaultConstructor2 != null) {
            TypeUtils.setAccessible(defaultConstructor2);
            beanInfo.setDefaultConstructor(defaultConstructor2);
        } else if (defaultConstructor2 == null && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            Constructor<?> creatorConstructor2 = getCreatorConstructor(clazz);
            if (creatorConstructor2 != null) {
                TypeUtils.setAccessible(creatorConstructor2);
                beanInfo.setCreatorConstructor(creatorConstructor2);
                int i2 = 0;
                while (i2 < creatorConstructor2.getParameterTypes().length) {
                    Annotation[] arr$2 = creatorConstructor2.getParameterAnnotations()[i2];
                    int len$ = arr$2.length;
                    int i$ = 0;
                    while (true) {
                        if (i$ >= len$) {
                            fieldAnnotation2 = null;
                            break;
                        }
                        Annotation paramAnnotation = arr$2[i$];
                        if (paramAnnotation instanceof JSONField) {
                            fieldAnnotation2 = (JSONField) paramAnnotation;
                            break;
                        }
                        i$++;
                    }
                    if (fieldAnnotation2 != null) {
                        beanInfo.add(new FieldInfo(fieldAnnotation2.name(), clazz, creatorConstructor2.getParameterTypes()[i2], creatorConstructor2.getGenericParameterTypes()[i2], TypeUtils.getField(cls, fieldAnnotation2.name()), fieldAnnotation2.ordinal(), SerializerFeature.of(fieldAnnotation2.serialzeFeatures())));
                        i2++;
                    } else {
                        throw new JSONException("illegal json creator");
                    }
                }
                return beanInfo;
            }
            Method factoryMethod2 = getFactoryMethod(clazz);
            if (factoryMethod2 != null) {
                TypeUtils.setAccessible(factoryMethod2);
                beanInfo.setFactoryMethod(factoryMethod2);
                int i3 = 0;
                while (i3 < factoryMethod2.getParameterTypes().length) {
                    Annotation[] arr$3 = factoryMethod2.getParameterAnnotations()[i3];
                    int len$2 = arr$3.length;
                    int i$2 = 0;
                    while (true) {
                        if (i$2 >= len$2) {
                            fieldAnnotation = null;
                            break;
                        }
                        Annotation paramAnnotation2 = arr$3[i$2];
                        if (paramAnnotation2 instanceof JSONField) {
                            fieldAnnotation = (JSONField) paramAnnotation2;
                            break;
                        }
                        i$2++;
                    }
                    if (fieldAnnotation != null) {
                        beanInfo.add(new FieldInfo(fieldAnnotation.name(), clazz, factoryMethod2.getParameterTypes()[i3], factoryMethod2.getGenericParameterTypes()[i3], TypeUtils.getField(cls, fieldAnnotation.name()), fieldAnnotation.ordinal(), SerializerFeature.of(fieldAnnotation.serialzeFeatures())));
                        i3++;
                    } else {
                        throw new JSONException("illegal json creator");
                    }
                }
                return beanInfo;
            }
            throw new JSONException("default constructor not found. " + cls);
        }
        Method[] arr$4 = clazz.getMethods();
        int len$3 = arr$4.length;
        int i$3 = 0;
        while (i$3 < len$3) {
            Method method2 = arr$4[i$3];
            int ordinal3 = 0;
            int serialzeFeatures3 = 0;
            String methodName2 = method2.getName();
            if (methodName2.length() >= 4 && !Modifier.isStatic(method2.getModifiers()) && ((method2.getReturnType().equals(Void.TYPE) || method2.getReturnType().equals(cls)) && method2.getParameterTypes().length == 1)) {
                JSONField annotation2 = (JSONField) method2.getAnnotation(JSONField.class);
                if (annotation2 == null) {
                    annotation = TypeUtils.getSupperMethodAnnotation(cls, method2);
                } else {
                    annotation = annotation2;
                }
                if (annotation == null) {
                    methodName = methodName2;
                    method = method2;
                    i = 4;
                } else if (annotation.deserialize()) {
                    int ordinal4 = annotation.ordinal();
                    int serialzeFeatures4 = SerializerFeature.of(annotation.serialzeFeatures());
                    if (annotation.name().length() != 0) {
                        String propertyName4 = annotation.name();
                        String str = propertyName4;
                        String propertyName5 = methodName2;
                        beanInfo.add(new FieldInfo(propertyName4, method2, (Field) null, clazz, type, ordinal4, serialzeFeatures4));
                        TypeUtils.setAccessible(method2);
                    } else {
                        methodName = methodName2;
                        method = method2;
                        i = 4;
                        ordinal3 = ordinal4;
                        serialzeFeatures3 = serialzeFeatures4;
                    }
                }
                if (methodName.startsWith("set")) {
                    char c3 = methodName.charAt(3);
                    if (Character.isUpperCase(c3)) {
                        if (TypeUtils.compatibleWithJavaBean) {
                            propertyName3 = TypeUtils.decapitalize(methodName.substring(3));
                        } else {
                            propertyName3 = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(i);
                        }
                    } else if (c3 == '_') {
                        propertyName3 = methodName.substring(i);
                    } else if (c3 == 'f') {
                        propertyName3 = methodName.substring(3);
                    } else if (methodName.length() < 5 || !Character.isUpperCase(methodName.charAt(i))) {
                    } else {
                        propertyName3 = TypeUtils.decapitalize(methodName.substring(3));
                    }
                    Field field2 = TypeUtils.getField(cls, propertyName3);
                    if (field2 == null && method.getParameterTypes()[0] == Boolean.TYPE) {
                        field = TypeUtils.getField(cls, "is" + Character.toUpperCase(propertyName3.charAt(0)) + propertyName3.substring(1));
                    } else {
                        field = field2;
                    }
                    if (field != null) {
                        JSONField fieldAnnotation3 = (JSONField) field.getAnnotation(JSONField.class);
                        if (fieldAnnotation3 != null) {
                            ordinal2 = fieldAnnotation3.ordinal();
                            serialzeFeatures2 = SerializerFeature.of(fieldAnnotation3.serialzeFeatures());
                            if (fieldAnnotation3.name().length() != 0) {
                                FieldInfo fieldInfo = r0;
                                char c = c3;
                                Field field3 = field;
                                FieldInfo fieldInfo2 = new FieldInfo(fieldAnnotation3.name(), method, field, clazz, type, ordinal2, serialzeFeatures2);
                                beanInfo.add(fieldInfo);
                            } else {
                                Field field4 = field;
                                beanInfo.add(new FieldInfo(propertyName3, method, (Field) null, clazz, type, ordinal2, serialzeFeatures2));
                                TypeUtils.setAccessible(method);
                            }
                        } else {
                            Field field5 = field;
                        }
                    } else {
                        Field field6 = field;
                    }
                    ordinal2 = ordinal3;
                    serialzeFeatures2 = serialzeFeatures3;
                    beanInfo.add(new FieldInfo(propertyName3, method, (Field) null, clazz, type, ordinal2, serialzeFeatures2));
                    TypeUtils.setAccessible(method);
                }
            }
            i$3++;
            cls = clazz;
        }
        Field[] arr$5 = clazz.getFields();
        int len$4 = arr$5.length;
        int i$4 = 0;
        while (i$4 < len$4) {
            Field field7 = arr$5[i$4];
            if (Modifier.isStatic(field7.getModifiers())) {
                arr$ = arr$5;
            } else {
                boolean contains = false;
                for (FieldInfo item : beanInfo.getFieldList()) {
                    if (item.getName().equals(field7.getName())) {
                        contains = true;
                    }
                }
                if (contains) {
                    arr$ = arr$5;
                } else {
                    String propertyName6 = field7.getName();
                    JSONField fieldAnnotation4 = (JSONField) field7.getAnnotation(JSONField.class);
                    if (fieldAnnotation4 != null) {
                        int ordinal5 = fieldAnnotation4.ordinal();
                        int serialzeFeatures5 = SerializerFeature.of(fieldAnnotation4.serialzeFeatures());
                        if (fieldAnnotation4.name().length() != 0) {
                            ordinal = ordinal5;
                            serialzeFeatures = serialzeFeatures5;
                            propertyName2 = fieldAnnotation4.name();
                        } else {
                            ordinal = ordinal5;
                            serialzeFeatures = serialzeFeatures5;
                            propertyName2 = propertyName6;
                        }
                    } else {
                        ordinal = 0;
                        serialzeFeatures = 0;
                        propertyName2 = propertyName6;
                    }
                    FieldInfo fieldInfo3 = r0;
                    arr$ = arr$5;
                    FieldInfo fieldInfo4 = new FieldInfo(propertyName2, (Method) null, field7, clazz, type, ordinal, serialzeFeatures);
                    beanInfo.add(fieldInfo3);
                }
            }
            i$4++;
            arr$5 = arr$;
        }
        int i4 = 4;
        Method[] arr$6 = clazz.getMethods();
        int len$5 = arr$6.length;
        int i$5 = 0;
        while (i$5 < len$5) {
            Method method3 = arr$6[i$5];
            String methodName3 = method3.getName();
            if (methodName3.length() >= i4) {
                if (!Modifier.isStatic(method3.getModifiers())) {
                    if (methodName3.startsWith("get") && Character.isUpperCase(methodName3.charAt(3))) {
                        if (method3.getParameterTypes().length == 0) {
                            if (Collection.class.isAssignableFrom(method3.getReturnType()) || Map.class.isAssignableFrom(method3.getReturnType())) {
                                JSONField annotation3 = (JSONField) method3.getAnnotation(JSONField.class);
                                if (annotation3 == null || annotation3.name().length() <= 0) {
                                    propertyName = Character.toLowerCase(methodName3.charAt(3)) + methodName3.substring(i4);
                                } else {
                                    propertyName = annotation3.name();
                                }
                                FieldInfo fieldInfo5 = r0;
                                FieldInfo fieldInfo6 = new FieldInfo(propertyName, method3, (Field) null, clazz, type);
                                beanInfo.add(fieldInfo5);
                                TypeUtils.setAccessible(method3);
                            }
                        }
                    }
                }
            }
            i$5++;
            i4 = 4;
        }
        return beanInfo;
    }

    public static Constructor<?> getDefaultConstructor(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        Constructor<?> defaultConstructor2 = null;
        Constructor<?>[] arr$ = clazz.getDeclaredConstructors();
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            Constructor<?> constructor = arr$[i$];
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor2 = constructor;
                break;
            }
            i$++;
        }
        if (defaultConstructor2 != null || !clazz.isMemberClass() || Modifier.isStatic(clazz.getModifiers())) {
            return defaultConstructor2;
        }
        for (Constructor<?> constructor2 : clazz.getDeclaredConstructors()) {
            if (constructor2.getParameterTypes().length == 1 && constructor2.getParameterTypes()[0].equals(clazz.getDeclaringClass())) {
                return constructor2;
            }
        }
        return defaultConstructor2;
    }

    public static Constructor<?> getCreatorConstructor(Class<?> clazz) {
        Constructor<?>[] arr$ = clazz.getDeclaredConstructors();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Constructor<?> constructor = arr$[i$];
            if (((JSONCreator) constructor.getAnnotation(JSONCreator.class)) == null) {
                i$++;
            } else if (0 == 0) {
                return constructor;
            } else {
                throw new JSONException("multi-json creator");
            }
        }
        return null;
    }

    public static Method getFactoryMethod(Class<?> clazz) {
        Method[] arr$ = clazz.getDeclaredMethods();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Method method = arr$[i$];
            if (!Modifier.isStatic(method.getModifiers()) || !clazz.isAssignableFrom(method.getReturnType()) || ((JSONCreator) method.getAnnotation(JSONCreator.class)) == null) {
                i$++;
            } else if (0 == 0) {
                return method;
            } else {
                throw new JSONException("multi-json creator");
            }
        }
        return null;
    }

    public int getParserFeatures() {
        return this.parserFeatures;
    }
}
