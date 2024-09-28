package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Enumeration;

public class EnumerationSeriliazer implements ObjectSerializer {
    public static EnumerationSeriliazer instance = new EnumerationSeriliazer();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (object != null) {
            Type elementType = null;
            if (serializer.isEnabled(SerializerFeature.WriteClassName) && (fieldType instanceof ParameterizedType)) {
                elementType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
            }
            Enumeration<?> e = (Enumeration) object;
            SerialContext context = serializer.getContext();
            serializer.setContext(context, object, fieldName, 0);
            int i = 0;
            try {
                out.append('[');
                while (e.hasMoreElements()) {
                    Object item = e.nextElement();
                    int i2 = i + 1;
                    if (i != 0) {
                        out.append(',');
                    }
                    if (item == null) {
                        out.writeNull();
                    } else {
                        serializer.getObjectWriter(item.getClass()).write(serializer, item, Integer.valueOf(i2 - 1), elementType);
                    }
                    i = i2;
                }
                out.append(']');
            } finally {
                serializer.setContext(context);
            }
        } else if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }
}
