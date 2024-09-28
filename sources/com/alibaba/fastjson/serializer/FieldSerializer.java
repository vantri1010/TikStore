package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;
import com.king.zxing.util.LogUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import kotlin.text.Typography;

public abstract class FieldSerializer {
    private final String double_quoted_fieldPrefix;
    protected final FieldInfo fieldInfo;
    private final String single_quoted_fieldPrefix;
    private final String un_quoted_fieldPrefix;
    private boolean writeNull = false;

    public abstract void writeProperty(JSONSerializer jSONSerializer, Object obj) throws Exception;

    public abstract void writeValue(JSONSerializer jSONSerializer, Object obj) throws Exception;

    public FieldSerializer(FieldInfo fieldInfo2) {
        this.fieldInfo = fieldInfo2;
        fieldInfo2.setAccessible(true);
        this.double_quoted_fieldPrefix = Typography.quote + fieldInfo2.getName() + "\":";
        this.single_quoted_fieldPrefix = '\'' + fieldInfo2.getName() + "':";
        StringBuilder sb = new StringBuilder();
        sb.append(fieldInfo2.getName());
        sb.append(LogUtils.COLON);
        this.un_quoted_fieldPrefix = sb.toString();
        JSONField annotation = (JSONField) fieldInfo2.getAnnotation(JSONField.class);
        if (annotation != null) {
            for (SerializerFeature feature : annotation.serialzeFeatures()) {
                if (feature == SerializerFeature.WriteMapNullValue) {
                    this.writeNull = true;
                }
            }
        }
    }

    public boolean isWriteNull() {
        return this.writeNull;
    }

    public Field getField() {
        return this.fieldInfo.getField();
    }

    public String getName() {
        return this.fieldInfo.getName();
    }

    public Method getMethod() {
        return this.fieldInfo.getMethod();
    }

    public void writePrefix(JSONSerializer serializer) throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (!serializer.isEnabled(SerializerFeature.QuoteFieldNames)) {
            out.write(this.un_quoted_fieldPrefix);
        } else if (serializer.isEnabled(SerializerFeature.UseSingleQuotes)) {
            out.write(this.single_quoted_fieldPrefix);
        } else {
            out.write(this.double_quoted_fieldPrefix);
        }
    }

    public Object getPropertyValue(Object object) throws Exception {
        try {
            return this.fieldInfo.get(object);
        } catch (Exception ex) {
            throw new JSONException("get property error。 " + this.fieldInfo.gerQualifiedName(), ex);
        }
    }
}
