package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Type;
import java.util.Collection;

public class ObjectFieldSerializer extends FieldSerializer {
    private String format;
    private RuntimeSerializerInfo runtimeInfo;
    boolean writeEnumUsingToString = false;
    boolean writeNullBooleanAsFalse = false;
    boolean writeNullListAsEmpty = false;
    boolean writeNullStringAsEmpty = false;
    boolean writeNumberAsZero = false;

    public ObjectFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);
        JSONField annotation = (JSONField) fieldInfo.getAnnotation(JSONField.class);
        if (annotation != null) {
            String format2 = annotation.format();
            this.format = format2;
            if (format2.trim().length() == 0) {
                this.format = null;
            }
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteNullNumberAsZero) {
                    this.writeNumberAsZero = true;
                } else if (feature == SerializerFeature.WriteNullStringAsEmpty) {
                    this.writeNullStringAsEmpty = true;
                } else if (feature == SerializerFeature.WriteNullBooleanAsFalse) {
                    this.writeNullBooleanAsFalse = true;
                } else if (feature == SerializerFeature.WriteNullListAsEmpty) {
                    this.writeNullListAsEmpty = true;
                } else if (feature == SerializerFeature.WriteEnumUsingToString) {
                    this.writeEnumUsingToString = true;
                }
            }
        }
    }

    public void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception {
        writePrefix(serializer);
        writeValue(serializer, propertyValue);
    }

    public void writeValue(JSONSerializer serializer, Object propertyValue) throws Exception {
        Class<?> runtimeFieldClass;
        String str = this.format;
        if (str != null) {
            serializer.writeWithFormat(propertyValue, str);
            return;
        }
        if (this.runtimeInfo == null) {
            if (propertyValue == null) {
                runtimeFieldClass = this.fieldInfo.getFieldClass();
            } else {
                runtimeFieldClass = propertyValue.getClass();
            }
            this.runtimeInfo = new RuntimeSerializerInfo(serializer.getObjectWriter(runtimeFieldClass), runtimeFieldClass);
        }
        RuntimeSerializerInfo runtimeInfo2 = this.runtimeInfo;
        if (propertyValue == null) {
            if (this.writeNumberAsZero && Number.class.isAssignableFrom(runtimeInfo2.runtimeFieldClass)) {
                serializer.getWriter().write('0');
            } else if (this.writeNullStringAsEmpty && String.class == runtimeInfo2.runtimeFieldClass) {
                serializer.getWriter().write("\"\"");
            } else if (this.writeNullBooleanAsFalse && Boolean.class == runtimeInfo2.runtimeFieldClass) {
                serializer.getWriter().write("false");
            } else if (!this.writeNullListAsEmpty || !Collection.class.isAssignableFrom(runtimeInfo2.runtimeFieldClass)) {
                runtimeInfo2.fieldSerializer.write(serializer, (Object) null, this.fieldInfo.getName(), (Type) null);
            } else {
                serializer.getWriter().write("[]");
            }
        } else if (!this.writeEnumUsingToString || !runtimeInfo2.runtimeFieldClass.isEnum()) {
            Class<?> valueClass = propertyValue.getClass();
            if (valueClass == runtimeInfo2.runtimeFieldClass) {
                runtimeInfo2.fieldSerializer.write(serializer, propertyValue, this.fieldInfo.getName(), this.fieldInfo.getFieldType());
            } else {
                serializer.getObjectWriter(valueClass).write(serializer, propertyValue, this.fieldInfo.getName(), this.fieldInfo.getFieldType());
            }
        } else {
            serializer.getWriter().writeString(((Enum) propertyValue).name());
        }
    }

    static class RuntimeSerializerInfo {
        ObjectSerializer fieldSerializer;
        Class<?> runtimeFieldClass;

        public RuntimeSerializerInfo(ObjectSerializer fieldSerializer2, Class<?> runtimeFieldClass2) {
            this.fieldSerializer = fieldSerializer2;
            this.runtimeFieldClass = runtimeFieldClass2;
        }
    }
}
