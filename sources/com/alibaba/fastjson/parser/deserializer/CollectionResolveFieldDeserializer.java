package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public final class CollectionResolveFieldDeserializer extends FieldDeserializer {
    private final Collection collection;

    public CollectionResolveFieldDeserializer(DefaultJSONParser parser, Collection collection2) {
        super((Class<?>) null, (FieldInfo) null);
        this.collection = collection2;
    }

    public void setValue(Object object, Object value) {
        this.collection.add(value);
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> map) {
    }
}
