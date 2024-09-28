package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapSerializer implements ObjectSerializer {
    public static MapSerializer instance = new MapSerializer();

    /* JADX INFO: finally extract failed */
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        SerializeWriter out = serializer.getWriter();
        if (obj == null) {
            out.writeNull();
            return;
        }
        Map<?, ?> map = (Map) obj;
        if (out.isEnabled(SerializerFeature.SortField) && !(map instanceof SortedMap) && !(map instanceof LinkedHashMap)) {
            try {
                map = new TreeMap<>(map);
            } catch (Exception e) {
            }
        }
        if (serializer.containsReference(object)) {
            serializer.writeReference(object);
            return;
        }
        SerialContext parent = serializer.getContext();
        jSONSerializer.setContext(parent, obj, fieldName, 0);
        try {
            out.write('{');
            serializer.incrementIndent();
            Class<?> preClazz = null;
            ObjectSerializer preWriter = null;
            boolean first = true;
            if (out.isEnabled(SerializerFeature.WriteClassName)) {
                out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
                out.writeString(object.getClass().getName());
                first = false;
            }
            for (Map.Entry entry : map.entrySet()) {
                Object value = entry.getValue();
                Object entryKey = entry.getKey();
                List<PropertyPreFilter> preFilters = serializer.getPropertyPreFiltersDirect();
                if (preFilters != null && preFilters.size() > 0) {
                    if (entryKey != null) {
                        if (!(entryKey instanceof String)) {
                            if ((entryKey.getClass().isPrimitive() || (entryKey instanceof Number)) && !FilterUtils.applyName(jSONSerializer, obj, JSON.toJSONString(entryKey))) {
                            }
                        }
                    }
                    if (!FilterUtils.applyName(jSONSerializer, obj, (String) entryKey)) {
                    }
                }
                List<PropertyFilter> propertyFilters = serializer.getPropertyFiltersDirect();
                if (propertyFilters != null && propertyFilters.size() > 0) {
                    if (entryKey != null) {
                        if (!(entryKey instanceof String)) {
                            if ((entryKey.getClass().isPrimitive() || (entryKey instanceof Number)) && !FilterUtils.apply(jSONSerializer, obj, JSON.toJSONString(entryKey), value)) {
                            }
                        }
                    }
                    if (!FilterUtils.apply(jSONSerializer, obj, (String) entryKey, value)) {
                    }
                }
                List<NameFilter> nameFilters = serializer.getNameFiltersDirect();
                if (nameFilters != null && nameFilters.size() > 0) {
                    if (entryKey != null) {
                        if (!(entryKey instanceof String)) {
                            if (entryKey.getClass().isPrimitive() || (entryKey instanceof Number)) {
                                entryKey = FilterUtils.processKey(jSONSerializer, obj, JSON.toJSONString(entryKey), value);
                            }
                        }
                    }
                    entryKey = FilterUtils.processKey(jSONSerializer, obj, (String) entryKey, value);
                }
                List<ValueFilter> valueFilters = serializer.getValueFiltersDirect();
                if (valueFilters != null && valueFilters.size() > 0) {
                    if (entryKey != null) {
                        if (!(entryKey instanceof String)) {
                            if (entryKey.getClass().isPrimitive() || (entryKey instanceof Number)) {
                                value = FilterUtils.processValue(jSONSerializer, obj, JSON.toJSONString(entryKey), value);
                            }
                        }
                    }
                    value = FilterUtils.processValue(jSONSerializer, obj, (String) entryKey, value);
                }
                if (value != null || jSONSerializer.isEnabled(SerializerFeature.WriteMapNullValue)) {
                    if (entryKey instanceof String) {
                        String key = (String) entryKey;
                        if (!first) {
                            out.write(',');
                        }
                        if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                            serializer.println();
                        }
                        out.writeFieldName(key, true);
                    } else {
                        if (!first) {
                            out.write(',');
                        }
                        if (!out.isEnabled(SerializerFeature.BrowserCompatible)) {
                            if (!out.isEnabled(SerializerFeature.WriteNonStringKeyAsString)) {
                                jSONSerializer.write(entryKey);
                                out.write(':');
                            }
                        }
                        jSONSerializer.write(JSON.toJSONString(entryKey));
                        out.write(':');
                    }
                    first = false;
                    if (value == null) {
                        out.writeNull();
                    } else {
                        Class<?> clazz = value.getClass();
                        if (clazz == preClazz) {
                            preWriter.write(jSONSerializer, value, entryKey, (Type) null);
                        } else {
                            preClazz = clazz;
                            preWriter = jSONSerializer.getObjectWriter(clazz);
                            preWriter.write(jSONSerializer, value, entryKey, (Type) null);
                        }
                    }
                }
            }
            jSONSerializer.setContext(parent);
            serializer.decrementIdent();
            if (out.isEnabled(SerializerFeature.PrettyFormat) && map.size() > 0) {
                serializer.println();
            }
            out.write('}');
        } catch (Throwable th) {
            jSONSerializer.setContext(parent);
            throw th;
        }
    }
}
