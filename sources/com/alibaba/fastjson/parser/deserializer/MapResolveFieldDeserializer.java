package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Type;
import java.util.Map;

public final class MapResolveFieldDeserializer extends FieldDeserializer {
    private final String key;
    private final Map map;

    public MapResolveFieldDeserializer(Map map2, String index) {
        super((Class<?>) null, (FieldInfo) null);
        this.key = index;
        this.map = map2;
    }

    public void setValue(Object object, Object value) {
        this.map.put(this.key, value);
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> map2) {
    }
}
