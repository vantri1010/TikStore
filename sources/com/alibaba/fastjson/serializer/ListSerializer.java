package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public final class ListSerializer implements ObjectSerializer {
    public static final ListSerializer instance = new ListSerializer();

    public final void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        Type elementType;
        boolean writeClassName;
        JSONSerializer jSONSerializer = serializer;
        Object obj = object;
        Object obj2 = fieldName;
        Type type = fieldType;
        boolean writeClassName2 = jSONSerializer.isEnabled(SerializerFeature.WriteClassName);
        SerializeWriter out = serializer.getWriter();
        int i = 0;
        if (!writeClassName2 || !(type instanceof ParameterizedType)) {
            elementType = null;
        } else {
            elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        if (obj != null) {
            List<?> list = (List) obj;
            if (list.size() == 0) {
                out.append((CharSequence) "[]");
                return;
            }
            SerialContext context = serializer.getContext();
            jSONSerializer.setContext(context, obj, obj2, 0);
            try {
                char c = ',';
                if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                    try {
                        out.append('[');
                        serializer.incrementIndent();
                        int i2 = 0;
                        for (Object item : list) {
                            if (i2 != 0) {
                                out.append(c);
                            }
                            serializer.println();
                            if (item == null) {
                                serializer.getWriter().writeNull();
                            } else if (jSONSerializer.containsReference(item)) {
                                jSONSerializer.writeReference(item);
                            } else {
                                ObjectSerializer itemSerializer = jSONSerializer.getObjectWriter(item.getClass());
                                jSONSerializer.setContext(new SerialContext(context, obj, obj2, i));
                                itemSerializer.write(jSONSerializer, item, Integer.valueOf(i2), elementType);
                            }
                            i2++;
                            i = 0;
                            c = ',';
                        }
                        serializer.decrementIdent();
                        serializer.println();
                        out.append(']');
                        jSONSerializer.setContext(context);
                    } catch (Throwable th) {
                        th = th;
                        boolean z = writeClassName2;
                        jSONSerializer.setContext(context);
                        throw th;
                    }
                } else {
                    out.append('[');
                    int i3 = 0;
                    for (Object item2 : list) {
                        if (i3 != 0) {
                            out.append(',');
                        }
                        if (item2 == null) {
                            out.append((CharSequence) "null");
                            writeClassName = writeClassName2;
                        } else {
                            Class<?> clazz = item2.getClass();
                            if (clazz == Integer.class) {
                                out.writeInt(((Integer) item2).intValue());
                                writeClassName = writeClassName2;
                            } else if (clazz == Long.class) {
                                long val = ((Long) item2).longValue();
                                if (writeClassName2) {
                                    writeClassName = writeClassName2;
                                    try {
                                        out.writeLongAndChar(val, 'L');
                                    } catch (Throwable th2) {
                                        th = th2;
                                        jSONSerializer.setContext(context);
                                        throw th;
                                    }
                                } else {
                                    writeClassName = writeClassName2;
                                    out.writeLong(val);
                                }
                            } else {
                                writeClassName = writeClassName2;
                                jSONSerializer.setContext(new SerialContext(context, obj, obj2, 0));
                                if (jSONSerializer.containsReference(item2)) {
                                    jSONSerializer.writeReference(item2);
                                } else {
                                    jSONSerializer.getObjectWriter(item2.getClass()).write(jSONSerializer, item2, Integer.valueOf(i3), elementType);
                                }
                            }
                        }
                        i3++;
                        Type type2 = fieldType;
                        writeClassName2 = writeClassName;
                    }
                    out.append(']');
                    jSONSerializer.setContext(context);
                }
            } catch (Throwable th3) {
                th = th3;
                boolean z2 = writeClassName2;
                jSONSerializer.setContext(context);
                throw th;
            }
        } else if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
            out.write("[]");
        } else {
            out.writeNull();
        }
    }
}
