package com.alibaba.fastjson.serializer;

public class SerialContext {
    private int features;
    private final Object fieldName;
    private final Object object;
    private final SerialContext parent;

    public SerialContext(SerialContext parent2, Object object2, Object fieldName2, int features2) {
        this.parent = parent2;
        this.object = object2;
        this.fieldName = fieldName2;
        this.features = features2;
    }

    public SerialContext getParent() {
        return this.parent;
    }

    public Object getObject() {
        return this.object;
    }

    public Object getFieldName() {
        return this.fieldName;
    }

    public String getPath() {
        if (this.parent == null) {
            return "$";
        }
        if (this.fieldName instanceof Integer) {
            return this.parent.getPath() + "[" + this.fieldName + "]";
        }
        return this.parent.getPath() + "." + this.fieldName;
    }

    public String toString() {
        return getPath();
    }

    public boolean isEnabled(SerializerFeature feature) {
        return SerializerFeature.isEnabled(this.features, feature);
    }
}
