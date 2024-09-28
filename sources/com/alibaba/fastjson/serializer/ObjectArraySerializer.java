package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;

public class ObjectArraySerializer implements ObjectSerializer {
    public static final ObjectArraySerializer instance = new ObjectArraySerializer();

    public final void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        SerializeWriter out = serializer.getWriter();
        Object[] array = (Object[]) obj;
        if (obj != null) {
            int size = array.length;
            int end = size - 1;
            if (end == -1) {
                out.append((CharSequence) "[]");
                return;
            }
            SerialContext context = serializer.getContext();
            jSONSerializer.setContext(context, obj, fieldName, 0);
            Class<?> preClazz = null;
            ObjectSerializer preWriter = null;
            try {
                out.append('[');
                if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                    serializer.incrementIndent();
                    serializer.println();
                    for (int i = 0; i < size; i++) {
                        if (i != 0) {
                            out.write(',');
                            serializer.println();
                        }
                        jSONSerializer.write(array[i]);
                    }
                    serializer.decrementIdent();
                    serializer.println();
                    out.write(']');
                    return;
                }
                for (int i2 = 0; i2 < end; i2++) {
                    Object item = array[i2];
                    if (item == null) {
                        out.append((CharSequence) "null,");
                    } else {
                        if (jSONSerializer.containsReference(item)) {
                            jSONSerializer.writeReference(item);
                        } else {
                            Class<?> clazz = item.getClass();
                            if (clazz == preClazz) {
                                preWriter.write(jSONSerializer, item, (Object) null, (Type) null);
                            } else {
                                preClazz = clazz;
                                preWriter = jSONSerializer.getObjectWriter(clazz);
                                preWriter.write(jSONSerializer, item, (Object) null, (Type) null);
                            }
                        }
                        out.append(',');
                    }
                }
                Object item2 = array[end];
                if (item2 == null) {
                    out.append((CharSequence) "null]");
                } else {
                    if (jSONSerializer.containsReference(item2)) {
                        jSONSerializer.writeReference(item2);
                    } else {
                        jSONSerializer.writeWithFieldName(item2, Integer.valueOf(end));
                    }
                    out.append(']');
                }
                jSONSerializer.setContext(context);
            } finally {
                jSONSerializer.setContext(context);
            }
        } else if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }
}
