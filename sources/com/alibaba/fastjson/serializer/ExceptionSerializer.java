package com.alibaba.fastjson.serializer;

import java.lang.reflect.Type;

public class ExceptionSerializer extends JavaBeanSerializer {
    public ExceptionSerializer(Class<?> clazz) {
        super(clazz);
    }

    /* access modifiers changed from: protected */
    public boolean isWriteClassName(JSONSerializer serializer, Object obj, Type fieldType, Object fieldName) {
        return true;
    }
}
