package com.alibaba.fastjson.parser;

import java.lang.reflect.Type;

public class ParseContext {
    private final Object fieldName;
    private Object object;
    private final ParseContext parent;
    private Type type;

    public ParseContext(ParseContext parent2, Object object2, Object fieldName2) {
        this.parent = parent2;
        this.object = object2;
        this.fieldName = fieldName2;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        this.type = type2;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object2) {
        this.object = object2;
    }

    public ParseContext getParentContext() {
        return this.parent;
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
}
