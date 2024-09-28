package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public abstract class FieldDeserializer {
    protected final Class<?> clazz;
    protected final FieldInfo fieldInfo;

    public abstract void parseField(DefaultJSONParser defaultJSONParser, Object obj, Type type, Map<String, Object> map);

    public FieldDeserializer(Class<?> clazz2, FieldInfo fieldInfo2) {
        this.clazz = clazz2;
        this.fieldInfo = fieldInfo2;
    }

    public Method getMethod() {
        return this.fieldInfo.getMethod();
    }

    public Field getField() {
        return this.fieldInfo.getField();
    }

    public Class<?> getFieldClass() {
        return this.fieldInfo.getFieldClass();
    }

    public Type getFieldType() {
        return this.fieldInfo.getFieldType();
    }

    public int getFastMatchToken() {
        return 0;
    }

    public void setValue(Object object, boolean value) {
        setValue(object, (Object) Boolean.valueOf(value));
    }

    public void setValue(Object object, int value) {
        setValue(object, (Object) Integer.valueOf(value));
    }

    public void setValue(Object object, long value) {
        setValue(object, (Object) Long.valueOf(value));
    }

    public void setValue(Object object, String value) {
        setValue(object, (Object) value);
    }

    public void setValue(Object object, Object value) {
        Method method = this.fieldInfo.getMethod();
        if (method != null) {
            try {
                if (this.fieldInfo.isGetOnly()) {
                    if (Map.class.isAssignableFrom(method.getReturnType())) {
                        Map map = (Map) method.invoke(object, new Object[0]);
                        if (map != null) {
                            map.putAll((Map) value);
                        }
                        return;
                    }
                    Collection collection = (Collection) method.invoke(object, new Object[0]);
                    if (collection != null) {
                        collection.addAll((Collection) value);
                    }
                } else if (value != null || !this.fieldInfo.getFieldClass().isPrimitive()) {
                    method.invoke(object, new Object[]{value});
                }
            } catch (Exception e) {
                throw new JSONException("set property error, " + this.fieldInfo.getName(), e);
            }
        } else {
            Field field = this.fieldInfo.getField();
            if (field != null) {
                try {
                    field.set(object, value);
                } catch (Exception e2) {
                    throw new JSONException("set property error, " + this.fieldInfo.getName(), e2);
                }
            }
        }
    }
}
