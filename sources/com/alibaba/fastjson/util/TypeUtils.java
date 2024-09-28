package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TypeUtils {
    public static boolean compatibleWithJavaBean = false;
    private static ConcurrentMap<String, Class<?>> mappings = new ConcurrentHashMap();
    private static boolean setAccessibleEnable = true;

    static {
        addBaseClassMappings();
    }

    public static final String castToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static final Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Byte.valueOf(((Number) value).byteValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() != 0 && !"null".equals(strVal)) {
                return Byte.valueOf(Byte.parseByte(strVal));
            }
            return null;
        }
        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Character castToChar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if (strVal.length() == 1) {
                return Character.valueOf(strVal.charAt(0));
            }
            throw new JSONException("can not cast to byte, value : " + value);
        }
        throw new JSONException("can not cast to byte, value : " + value);
    }

    public static final Short castToShort(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Short.valueOf(((Number) value).shortValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() != 0 && !"null".equals(strVal)) {
                return Short.valueOf(Short.parseShort(strVal));
            }
            return null;
        }
        throw new JSONException("can not cast to short, value : " + value);
    }

    public static final BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }
        return new BigDecimal(strVal);
    }

    public static final BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        if ((value instanceof Float) || (value instanceof Double)) {
            return BigInteger.valueOf(((Number) value).longValue());
        }
        String strVal = value.toString();
        if (strVal.length() == 0) {
            return null;
        }
        return new BigInteger(strVal);
    }

    public static final Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Float.valueOf(((Number) value).floatValue());
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() != 0 && !"null".equals(strVal)) {
                return Float.valueOf(Float.parseFloat(strVal));
            }
            return null;
        }
        throw new JSONException("can not cast to float, value : " + value);
    }

    public static final Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Double.valueOf(((Number) value).doubleValue());
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (strVal.length() != 0 && !"null".equals(strVal)) {
                return Double.valueOf(Double.parseDouble(strVal));
            }
            return null;
        }
        throw new JSONException("can not cast to double, value : " + value);
    }

    public static final Date castToDate(Object value) {
        String format;
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        long longValue = -1;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.indexOf(45) != -1) {
                if (strVal.length() == JSON.DEFFAULT_DATE_FORMAT.length()) {
                    format = JSON.DEFFAULT_DATE_FORMAT;
                } else if (strVal.length() == 10) {
                    format = "yyyy-MM-dd";
                } else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                    format = "yyyy-MM-dd HH:mm:ss";
                } else {
                    format = "yyyy-MM-dd HH:mm:ss.SSS";
                }
                try {
                    return new SimpleDateFormat(format).parse(strVal);
                } catch (ParseException e) {
                    throw new JSONException("can not cast to Date, value : " + strVal);
                }
            } else if (strVal.length() == 0) {
                return null;
            } else {
                longValue = Long.parseLong(strVal);
            }
        }
        if (longValue >= 0) {
            return new Date(longValue);
        }
        throw new JSONException("can not cast to Date, value : " + value);
    }

    public static final java.sql.Date castToSqlDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new java.sql.Date(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        if (value instanceof Date) {
            return new java.sql.Date(((Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue > 0) {
            return new java.sql.Date(longValue);
        }
        throw new JSONException("can not cast to Date, value : " + value);
    }

    public static final Timestamp castToTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return new Timestamp(((Calendar) value).getTimeInMillis());
        }
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }
        long longValue = 0;
        if (value instanceof Number) {
            longValue = ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            longValue = Long.parseLong(strVal);
        }
        if (longValue > 0) {
            return new Timestamp(longValue);
        }
        throw new JSONException("can not cast to Date, value : " + value);
    }

    public static final Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0 || "null".equals(strVal)) {
                return null;
            }
            try {
                return Long.valueOf(Long.parseLong(strVal));
            } catch (NumberFormatException e) {
                JSONScanner dateParser = new JSONScanner(strVal);
                Calendar calendar = null;
                if (dateParser.scanISO8601DateIfMatch(false)) {
                    calendar = dateParser.getCalendar();
                }
                dateParser.close();
                if (calendar != null) {
                    return Long.valueOf(calendar.getTimeInMillis());
                }
            }
        }
        throw new JSONException("can not cast to long, value : " + value);
    }

    public static final Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() != 0 && !"null".equals(strVal)) {
                return Integer.valueOf(Integer.parseInt(strVal));
            }
            return null;
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final byte[] castToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof String) {
            return Base64.decodeFast((String) value);
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            boolean z = true;
            if (((Number) value).intValue() != 1) {
                z = false;
            }
            return Boolean.valueOf(z);
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.length() == 0) {
                return null;
            }
            if ("true".equalsIgnoreCase(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal)) {
                return Boolean.FALSE;
            }
            if ("1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("0".equals(strVal)) {
                return Boolean.FALSE;
            }
            if ("null".equals(strVal)) {
                return null;
            }
        }
        throw new JSONException("can not cast to int, value : " + value);
    }

    public static final <T> T castToJavaBean(Object obj, Class<T> clazz) {
        return cast(obj, clazz, ParserConfig.getGlobalInstance());
    }

    public static final <T> T cast(Object obj, Class<T> clazz, ParserConfig mapping) {
        Calendar calendar;
        if (obj == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        } else if (clazz == obj.getClass()) {
            return obj;
        } else {
            if (!(obj instanceof Map)) {
                if (clazz.isArray()) {
                    if (obj instanceof Collection) {
                        Collection<Object> collection = (Collection) obj;
                        int index = 0;
                        Object array = Array.newInstance(clazz.getComponentType(), collection.size());
                        for (Object item : collection) {
                            Array.set(array, index, cast(item, clazz.getComponentType(), mapping));
                            index++;
                        }
                        return array;
                    } else if (clazz == byte[].class) {
                        return castToBytes(obj);
                    }
                }
                if (clazz.isAssignableFrom(obj.getClass())) {
                    return obj;
                }
                if (clazz == Boolean.TYPE || clazz == Boolean.class) {
                    return castToBoolean(obj);
                }
                if (clazz == Byte.TYPE || clazz == Byte.class) {
                    return castToByte(obj);
                }
                if (clazz == Short.TYPE || clazz == Short.class) {
                    return castToShort(obj);
                }
                if (clazz == Integer.TYPE || clazz == Integer.class) {
                    return castToInt(obj);
                }
                if (clazz == Long.TYPE || clazz == Long.class) {
                    return castToLong(obj);
                }
                if (clazz == Float.TYPE || clazz == Float.class) {
                    return castToFloat(obj);
                }
                if (clazz == Double.TYPE || clazz == Double.class) {
                    return castToDouble(obj);
                }
                if (clazz == String.class) {
                    return castToString(obj);
                }
                if (clazz == BigDecimal.class) {
                    return castToBigDecimal(obj);
                }
                if (clazz == BigInteger.class) {
                    return castToBigInteger(obj);
                }
                if (clazz == Date.class) {
                    return castToDate(obj);
                }
                if (clazz == java.sql.Date.class) {
                    return castToSqlDate(obj);
                }
                if (clazz == Timestamp.class) {
                    return castToTimestamp(obj);
                }
                if (clazz.isEnum()) {
                    return castToEnum(obj, clazz, mapping);
                }
                if (Calendar.class.isAssignableFrom(clazz)) {
                    Date date = castToDate(obj);
                    if (clazz == Calendar.class) {
                        calendar = Calendar.getInstance();
                    } else {
                        try {
                            calendar = clazz.newInstance();
                        } catch (Exception e) {
                            throw new JSONException("can not cast to : " + clazz.getName(), e);
                        }
                    }
                    calendar.setTime(date);
                    return calendar;
                } else if ((obj instanceof String) && ((String) obj).length() == 0) {
                    return null;
                } else {
                    throw new JSONException("can not cast to : " + clazz.getName());
                }
            } else if (clazz == Map.class) {
                return obj;
            } else {
                Map map = (Map) obj;
                if (clazz != Object.class || map.containsKey(JSON.DEFAULT_TYPE_KEY)) {
                    return castToJavaBean((Map) obj, clazz, mapping);
                }
                return obj;
            }
        }
    }

    public static final <T> T castToEnum(Object obj, Class<T> clazz, ParserConfig mapping) {
        try {
            if (obj instanceof String) {
                String name = (String) obj;
                if (name.length() == 0) {
                    return null;
                }
                return Enum.valueOf(clazz, name);
            }
            if (obj instanceof Number) {
                int ordinal = ((Number) obj).intValue();
                for (Object value : (Object[]) clazz.getMethod("values", new Class[0]).invoke((Object) null, new Object[0])) {
                    Enum e = (Enum) value;
                    if (e.ordinal() == ordinal) {
                        return e;
                    }
                }
            }
            throw new JSONException("can not cast to : " + clazz.getName());
        } catch (Exception ex) {
            throw new JSONException("can not cast to : " + clazz.getName(), ex);
        }
    }

    public static final <T> T cast(Object obj, Type type, ParserConfig mapping) {
        if (obj == null) {
            return null;
        }
        if (type instanceof Class) {
            return cast(obj, (Class) type, mapping);
        }
        if (type instanceof ParameterizedType) {
            return cast(obj, (ParameterizedType) type, mapping);
        }
        if ((obj instanceof String) && ((String) obj).length() == 0) {
            return null;
        }
        if (type instanceof TypeVariable) {
            return obj;
        }
        throw new JSONException("can not cast to : " + type);
    }

    public static final <T> T cast(Object obj, ParameterizedType type, ParserConfig mapping) {
        Collection collection;
        Type rawTye = type.getRawType();
        if (rawTye == Set.class || rawTye == HashSet.class || rawTye == TreeSet.class || rawTye == List.class || rawTye == ArrayList.class) {
            Type itemType = type.getActualTypeArguments()[0];
            if (obj instanceof Iterable) {
                if (rawTye == Set.class || rawTye == HashSet.class) {
                    collection = new HashSet();
                } else if (rawTye == TreeSet.class) {
                    collection = new TreeSet();
                } else {
                    collection = new ArrayList();
                }
                for (Object item : (Iterable) obj) {
                    collection.add(cast(item, itemType, mapping));
                }
                return collection;
            }
        }
        if (rawTye == Map.class || rawTye == HashMap.class) {
            Type keyType = type.getActualTypeArguments()[0];
            Type valueType = type.getActualTypeArguments()[1];
            if (obj instanceof Map) {
                Map map = new HashMap();
                for (Map.Entry entry : ((Map) obj).entrySet()) {
                    map.put(cast(entry.getKey(), keyType, mapping), cast(entry.getValue(), valueType, mapping));
                }
                return map;
            }
        }
        if ((obj instanceof String) && ((String) obj).length() == 0) {
            return null;
        }
        if (type.getActualTypeArguments().length == 1 && (type.getActualTypeArguments()[0] instanceof WildcardType)) {
            return cast(obj, rawTye, mapping);
        }
        throw new JSONException("can not cast to : " + type);
    }

    public static final <T> T castToJavaBean(Map<String, Object> map, Class<T> clazz, ParserConfig mapping) {
        JSONObject object;
        int lineNumber;
        Map<String, Object> map2 = map;
        Class<T> cls = clazz;
        ParserConfig mapping2 = mapping;
        if (cls == StackTraceElement.class) {
            try {
                String declaringClass = (String) map2.get("className");
                String methodName = (String) map2.get("methodName");
                String fileName = (String) map2.get("fileName");
                Number value = (Number) map2.get("lineNumber");
                if (value == null) {
                    lineNumber = 0;
                } else {
                    lineNumber = value.intValue();
                }
                return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
            } catch (Exception e) {
                e = e;
                throw new JSONException(e.getMessage(), e);
            }
        } else {
            Object iClassObject = map2.get(JSON.DEFAULT_TYPE_KEY);
            if (iClassObject instanceof String) {
                String className = (String) iClassObject;
                Class<?> loadClazz = loadClass(className);
                if (loadClazz == null) {
                    throw new ClassNotFoundException(className + " not found");
                } else if (!loadClazz.equals(cls)) {
                    return castToJavaBean(map2, loadClazz, mapping2);
                }
            }
            if (clazz.isInterface()) {
                if (map2 instanceof JSONObject) {
                    object = (JSONObject) map2;
                } else {
                    object = new JSONObject(map2);
                }
                return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{cls}, object);
            }
            if (mapping2 == null) {
                mapping2 = ParserConfig.getGlobalInstance();
            }
            try {
                Map<String, FieldDeserializer> setters = mapping2.getFieldDeserializers(cls);
                Constructor<T> constructor = cls.getDeclaredConstructor(new Class[0]);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                T object2 = constructor.newInstance(new Object[0]);
                for (Map.Entry<String, FieldDeserializer> entry : setters.entrySet()) {
                    String key = entry.getKey();
                    FieldDeserializer fieldDeser = entry.getValue();
                    if (map2.containsKey(key)) {
                        Object value2 = map2.get(key);
                        Method method = fieldDeser.getMethod();
                        if (method != null) {
                            method.invoke(object2, new Object[]{cast(value2, method.getGenericParameterTypes()[0], mapping2)});
                        } else {
                            Field field = fieldDeser.getField();
                            field.set(object2, cast(value2, field.getGenericType(), mapping2));
                        }
                    }
                }
                return object2;
            } catch (Exception e2) {
                e = e2;
                throw new JSONException(e.getMessage(), e);
            }
        }
    }

    public static void addClassMapping(String className, Class<?> clazz) {
        if (className == null) {
            className = clazz.getName();
        }
        mappings.put(className, clazz);
    }

    public static void addBaseClassMappings() {
        mappings.put("byte", Byte.TYPE);
        mappings.put("short", Short.TYPE);
        mappings.put("int", Integer.TYPE);
        mappings.put("long", Long.TYPE);
        mappings.put("float", Float.TYPE);
        mappings.put("double", Double.TYPE);
        mappings.put("boolean", Boolean.TYPE);
        mappings.put("char", Character.TYPE);
        mappings.put("[byte", byte[].class);
        mappings.put("[short", short[].class);
        mappings.put("[int", int[].class);
        mappings.put("[long", long[].class);
        mappings.put("[float", float[].class);
        mappings.put("[double", double[].class);
        mappings.put("[boolean", boolean[].class);
        mappings.put("[char", char[].class);
        mappings.put(HashMap.class.getName(), HashMap.class);
    }

    public static void clearClassMapping() {
        mappings.clear();
        addBaseClassMappings();
    }

    public static Class<?> loadClass(String className) {
        if (className == null || className.length() == 0) {
            return null;
        }
        Class<?> clazz = (Class) mappings.get(className);
        if (clazz != null) {
            return clazz;
        }
        if (className.charAt(0) == '[') {
            return Array.newInstance(loadClass(className.substring(1)), 0).getClass();
        }
        if (className.startsWith("L") && className.endsWith(";")) {
            return loadClass(className.substring(1, className.length() - 1));
        }
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                Class<?> clazz2 = classLoader.loadClass(className);
                addClassMapping(className, clazz2);
                return clazz2;
            }
        } catch (Throwable th) {
        }
        try {
            clazz = Class.forName(className);
            addClassMapping(className, clazz);
            return clazz;
        } catch (Throwable th2) {
            return clazz;
        }
    }

    public static List<FieldInfo> computeGetters(Class<?> clazz, Map<String, String> aliasMap) {
        return computeGetters(clazz, aliasMap, true);
    }

    public static List<FieldInfo> computeGetters(Class<?> clazz, Map<String, String> aliasMap, boolean sorted) {
        int serialzeFeatures;
        int ordinal;
        String propertyName;
        int len$;
        Method[] arr$;
        JSONField annotation;
        int len$2;
        String propertyName2;
        int serialzeFeatures2;
        int ordinal2;
        JSONField fieldAnnotation;
        String propertyName3;
        int serialzeFeatures3;
        int ordinal3;
        String propertyName4;
        JSONField fieldAnnotation2;
        String propertyName5;
        Class<?> cls = clazz;
        Map<String, String> map = aliasMap;
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        Method[] arr$2 = clazz.getMethods();
        int len$3 = arr$2.length;
        int i$ = 0;
        while (i$ < len$3) {
            Method method = arr$2[i$];
            String methodName = method.getName();
            int ordinal4 = 0;
            int serialzeFeatures4 = 0;
            if (Modifier.isStatic(method.getModifiers())) {
                arr$ = arr$2;
                len$ = len$3;
            } else if (method.getReturnType().equals(Void.TYPE)) {
                arr$ = arr$2;
                len$ = len$3;
            } else if (method.getParameterTypes().length != 0) {
                arr$ = arr$2;
                len$ = len$3;
            } else if (method.getReturnType() == ClassLoader.class) {
                arr$ = arr$2;
                len$ = len$3;
            } else if (!method.getName().equals("getMetaClass") || !method.getReturnType().getName().equals("groovy.lang.MetaClass")) {
                JSONField annotation2 = (JSONField) method.getAnnotation(JSONField.class);
                if (annotation2 == null) {
                    annotation = getSupperMethodAnnotation(cls, method);
                } else {
                    annotation = annotation2;
                }
                if (annotation == null) {
                    arr$ = arr$2;
                } else if (!annotation.serialize()) {
                    arr$ = arr$2;
                    len$ = len$3;
                } else {
                    int ordinal5 = annotation.ordinal();
                    int serialzeFeatures5 = SerializerFeature.of(annotation.serialzeFeatures());
                    if (annotation.name().length() != 0) {
                        String propertyName6 = annotation.name();
                        if (map != null) {
                            String propertyName7 = map.get(propertyName6);
                            if (propertyName7 == null) {
                                arr$ = arr$2;
                                len$ = len$3;
                            } else {
                                propertyName5 = propertyName7;
                            }
                        } else {
                            propertyName5 = propertyName6;
                        }
                        FieldInfo fieldInfo = r6;
                        arr$ = arr$2;
                        FieldInfo fieldInfo2 = new FieldInfo(propertyName5, method, (Field) null, ordinal5, serialzeFeatures5);
                        fieldInfoMap.put(propertyName5, fieldInfo);
                        len$ = len$3;
                    } else {
                        arr$ = arr$2;
                        ordinal4 = ordinal5;
                        serialzeFeatures4 = serialzeFeatures5;
                    }
                }
                if (!methodName.startsWith("get")) {
                    len$ = len$3;
                    len$2 = 3;
                } else if (methodName.length() < 4) {
                    len$ = len$3;
                } else if (methodName.equals("getClass")) {
                    len$ = len$3;
                } else {
                    char c3 = methodName.charAt(3);
                    if (Character.isUpperCase(c3)) {
                        if (compatibleWithJavaBean) {
                            propertyName3 = decapitalize(methodName.substring(3));
                        } else {
                            propertyName3 = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                        }
                    } else if (c3 == '_') {
                        propertyName3 = methodName.substring(4);
                    } else if (c3 == 'f') {
                        propertyName3 = methodName.substring(3);
                    } else if (methodName.length() < 5 || !Character.isUpperCase(methodName.charAt(4))) {
                        len$ = len$3;
                    } else {
                        propertyName3 = decapitalize(methodName.substring(3));
                    }
                    if (isJSONTypeIgnore(cls, propertyName3)) {
                        len$ = len$3;
                    } else {
                        Field field = ParserConfig.getField(cls, propertyName3);
                        if (field == null || (fieldAnnotation2 = (JSONField) field.getAnnotation(JSONField.class)) == null) {
                            ordinal3 = ordinal4;
                            serialzeFeatures3 = serialzeFeatures4;
                        } else if (!fieldAnnotation2.serialize()) {
                            len$ = len$3;
                        } else {
                            int ordinal6 = fieldAnnotation2.ordinal();
                            int serialzeFeatures6 = SerializerFeature.of(fieldAnnotation2.serialzeFeatures());
                            if (fieldAnnotation2.name().length() != 0) {
                                propertyName3 = fieldAnnotation2.name();
                                if (map != null) {
                                    propertyName3 = map.get(propertyName3);
                                    if (propertyName3 == null) {
                                        len$ = len$3;
                                    } else {
                                        ordinal3 = ordinal6;
                                        serialzeFeatures3 = serialzeFeatures6;
                                    }
                                } else {
                                    ordinal3 = ordinal6;
                                    serialzeFeatures3 = serialzeFeatures6;
                                }
                            } else {
                                ordinal3 = ordinal6;
                                serialzeFeatures3 = serialzeFeatures6;
                            }
                        }
                        if (map != null) {
                            String propertyName8 = map.get(propertyName3);
                            if (propertyName8 == null) {
                                len$ = len$3;
                            } else {
                                propertyName4 = propertyName8;
                            }
                        } else {
                            propertyName4 = propertyName3;
                        }
                        FieldInfo fieldInfo3 = r6;
                        char c = c3;
                        len$ = len$3;
                        len$2 = 3;
                        Field field2 = field;
                        FieldInfo fieldInfo4 = new FieldInfo(propertyName4, method, field, ordinal3, serialzeFeatures3);
                        fieldInfoMap.put(propertyName4, fieldInfo3);
                        ordinal4 = ordinal3;
                        serialzeFeatures4 = serialzeFeatures3;
                    }
                }
                if (methodName.startsWith("is") && methodName.length() >= len$2) {
                    char c2 = methodName.charAt(2);
                    if (Character.isUpperCase(c2)) {
                        if (compatibleWithJavaBean) {
                            propertyName2 = decapitalize(methodName.substring(2));
                        } else {
                            propertyName2 = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(len$2);
                        }
                    } else if (c2 == '_') {
                        propertyName2 = methodName.substring(len$2);
                    } else if (c2 == 'f') {
                        propertyName2 = methodName.substring(2);
                    }
                    Field field3 = ParserConfig.getField(cls, propertyName2);
                    if (field3 == null) {
                        field3 = ParserConfig.getField(cls, methodName);
                    }
                    if (field3 == null || (fieldAnnotation = (JSONField) field3.getAnnotation(JSONField.class)) == null) {
                        ordinal2 = ordinal4;
                        serialzeFeatures2 = serialzeFeatures4;
                    } else if (fieldAnnotation.serialize()) {
                        int ordinal7 = fieldAnnotation.ordinal();
                        int serialzeFeatures7 = SerializerFeature.of(fieldAnnotation.serialzeFeatures());
                        if (fieldAnnotation.name().length() != 0) {
                            propertyName2 = fieldAnnotation.name();
                            if (map != null) {
                                propertyName2 = map.get(propertyName2);
                                if (propertyName2 != null) {
                                    ordinal2 = ordinal7;
                                    serialzeFeatures2 = serialzeFeatures7;
                                }
                            } else {
                                ordinal2 = ordinal7;
                                serialzeFeatures2 = serialzeFeatures7;
                            }
                        } else {
                            ordinal2 = ordinal7;
                            serialzeFeatures2 = serialzeFeatures7;
                        }
                    }
                    if (map == null || (propertyName2 = map.get(propertyName2)) != null) {
                        Field field4 = field3;
                        FieldInfo fieldInfo5 = r6;
                        FieldInfo fieldInfo6 = new FieldInfo(propertyName2, method, field3, ordinal2, serialzeFeatures2);
                        fieldInfoMap.put(propertyName2, fieldInfo5);
                    }
                }
            } else {
                arr$ = arr$2;
                len$ = len$3;
            }
            i$++;
            arr$2 = arr$;
            len$3 = len$;
        }
        int i = len$3;
        Field[] arr$3 = clazz.getFields();
        int len$4 = arr$3.length;
        int i$2 = 0;
        while (i$2 < len$4) {
            Field field5 = arr$3[i$2];
            if (!Modifier.isStatic(field5.getModifiers())) {
                JSONField fieldAnnotation3 = (JSONField) field5.getAnnotation(JSONField.class);
                String propertyName9 = field5.getName();
                if (fieldAnnotation3 == null) {
                    ordinal = 0;
                    serialzeFeatures = 0;
                } else if (fieldAnnotation3.serialize()) {
                    int ordinal8 = fieldAnnotation3.ordinal();
                    int serialzeFeatures8 = SerializerFeature.of(fieldAnnotation3.serialzeFeatures());
                    if (fieldAnnotation3.name().length() != 0) {
                        propertyName9 = fieldAnnotation3.name();
                        ordinal = ordinal8;
                        serialzeFeatures = serialzeFeatures8;
                    } else {
                        ordinal = ordinal8;
                        serialzeFeatures = serialzeFeatures8;
                    }
                }
                if (map != null) {
                    String propertyName10 = map.get(propertyName9);
                    if (propertyName10 != null) {
                        propertyName = propertyName10;
                    }
                } else {
                    propertyName = propertyName9;
                }
                if (!fieldInfoMap.containsKey(propertyName)) {
                    FieldInfo fieldInfo7 = r6;
                    FieldInfo fieldInfo8 = new FieldInfo(propertyName, (Method) null, field5, ordinal, serialzeFeatures);
                    fieldInfoMap.put(propertyName, fieldInfo7);
                }
            }
            i$2++;
            map = aliasMap;
        }
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        boolean containsAll = false;
        String[] orders = null;
        JSONType annotation3 = (JSONType) cls.getAnnotation(JSONType.class);
        if (annotation3 != null) {
            orders = annotation3.orders();
            if (orders == null || orders.length != fieldInfoMap.size()) {
                containsAll = false;
            } else {
                containsAll = true;
                String[] arr$4 = orders;
                int len$5 = arr$4.length;
                int i$3 = 0;
                while (true) {
                    if (i$3 >= len$5) {
                        break;
                    } else if (!fieldInfoMap.containsKey(arr$4[i$3])) {
                        containsAll = false;
                        break;
                    } else {
                        i$3++;
                    }
                }
            }
        }
        if (containsAll) {
            for (String item : orders) {
                fieldInfoList.add(fieldInfoMap.get(item));
            }
        } else {
            for (FieldInfo fieldInfo9 : fieldInfoMap.values()) {
                fieldInfoList.add(fieldInfo9);
            }
            if (sorted) {
                Collections.sort(fieldInfoList);
            }
        }
        return fieldInfoList;
    }

    public static JSONField getSupperMethodAnnotation(Class<?> clazz, Method method) {
        JSONField annotation;
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            for (Method interfaceMethod : interfaceClass.getMethods()) {
                if (interfaceMethod.getName().equals(method.getName()) && interfaceMethod.getParameterTypes().length == method.getParameterTypes().length) {
                    boolean match = true;
                    int i = 0;
                    while (true) {
                        if (i >= interfaceMethod.getParameterTypes().length) {
                            break;
                        } else if (!interfaceMethod.getParameterTypes()[i].equals(method.getParameterTypes()[i])) {
                            match = false;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (match && (annotation = (JSONField) interfaceMethod.getAnnotation(JSONField.class)) != null) {
                        return annotation;
                    }
                }
            }
        }
        return null;
    }

    private static boolean isJSONTypeIgnore(Class<?> clazz, String propertyName) {
        JSONType jsonType = (JSONType) clazz.getAnnotation(JSONType.class);
        if (!(jsonType == null || jsonType.ignores() == null)) {
            for (String item : jsonType.ignores()) {
                if (propertyName.equalsIgnoreCase(item)) {
                    return true;
                }
            }
        }
        if (clazz.getSuperclass() == Object.class || clazz.getSuperclass() == null || !isJSONTypeIgnore(clazz.getSuperclass(), propertyName)) {
            return false;
        }
        return true;
    }

    public static boolean isGenericParamType(Type type) {
        if (type instanceof ParameterizedType) {
            return true;
        }
        if (type instanceof Class) {
            return isGenericParamType(((Class) type).getGenericSuperclass());
        }
        return false;
    }

    public static Type getGenericParamType(Type type) {
        if (!(type instanceof ParameterizedType) && (type instanceof Class)) {
            return getGenericParamType(((Class) type).getGenericSuperclass());
        }
        return type;
    }

    public static Type unwrap(Type type) {
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType == Byte.TYPE) {
                return byte[].class;
            }
            if (componentType == Character.TYPE) {
                return char[].class;
            }
        }
        return type;
    }

    public static Class<?> getClass(Type type) {
        if (type.getClass() == Class.class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        }
        return Object.class;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        Class<? super Object> superclass = clazz.getSuperclass();
        if (superclass == null || superclass == Object.class) {
            return null;
        }
        return getField(superclass, fieldName);
    }

    public static int getSerializeFeatures(Class<?> clazz) {
        JSONType annotation = (JSONType) clazz.getAnnotation(JSONType.class);
        if (annotation == null) {
            return 0;
        }
        return SerializerFeature.of(annotation.serialzeFeatures());
    }

    public static int getParserFeatures(Class<?> clazz) {
        JSONType annotation = (JSONType) clazz.getAnnotation(JSONType.class);
        if (annotation == null) {
            return 0;
        }
        return Feature.of(annotation.parseFeatures());
    }

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    static void setAccessible(AccessibleObject obj) {
        if (setAccessibleEnable && !obj.isAccessible()) {
            try {
                obj.setAccessible(true);
            } catch (AccessControlException e) {
                setAccessibleEnable = false;
            }
        }
    }
}
