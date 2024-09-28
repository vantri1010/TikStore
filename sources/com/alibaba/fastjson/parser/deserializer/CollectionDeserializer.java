package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CollectionDeserializer implements ObjectDeserializer {
    public static final CollectionDeserializer instance = new CollectionDeserializer();

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: java.lang.reflect.Type[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r7, java.lang.reflect.Type r8, java.lang.Object r9) {
        /*
            r6 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r7.getLexer()
            int r0 = r0.token()
            r1 = 0
            r2 = 8
            if (r0 != r2) goto L_0x0017
            com.alibaba.fastjson.parser.JSONLexer r0 = r7.getLexer()
            r2 = 16
            r0.nextToken(r2)
            return r1
        L_0x0017:
            java.lang.Class r0 = r6.getRawClass(r8)
            java.lang.Class<java.util.AbstractCollection> r2 = java.util.AbstractCollection.class
            r3 = 0
            if (r0 != r2) goto L_0x0026
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            goto L_0x0086
        L_0x0026:
            java.lang.Class<java.util.HashSet> r2 = java.util.HashSet.class
            boolean r2 = r0.isAssignableFrom(r2)
            if (r2 == 0) goto L_0x0034
            java.util.HashSet r1 = new java.util.HashSet
            r1.<init>()
            goto L_0x0086
        L_0x0034:
            java.lang.Class<java.util.LinkedHashSet> r2 = java.util.LinkedHashSet.class
            boolean r2 = r0.isAssignableFrom(r2)
            if (r2 == 0) goto L_0x0042
            java.util.LinkedHashSet r1 = new java.util.LinkedHashSet
            r1.<init>()
            goto L_0x0086
        L_0x0042:
            java.lang.Class<java.util.TreeSet> r2 = java.util.TreeSet.class
            boolean r2 = r0.isAssignableFrom(r2)
            if (r2 == 0) goto L_0x0050
            java.util.TreeSet r1 = new java.util.TreeSet
            r1.<init>()
            goto L_0x0086
        L_0x0050:
            java.lang.Class<java.util.ArrayList> r2 = java.util.ArrayList.class
            boolean r2 = r0.isAssignableFrom(r2)
            if (r2 == 0) goto L_0x005e
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            goto L_0x0086
        L_0x005e:
            java.lang.Class<java.util.EnumSet> r2 = java.util.EnumSet.class
            boolean r2 = r0.isAssignableFrom(r2)
            if (r2 == 0) goto L_0x007e
            boolean r1 = r8 instanceof java.lang.reflect.ParameterizedType
            if (r1 == 0) goto L_0x0074
            r1 = r8
            java.lang.reflect.ParameterizedType r1 = (java.lang.reflect.ParameterizedType) r1
            java.lang.reflect.Type[] r1 = r1.getActualTypeArguments()
            r1 = r1[r3]
            goto L_0x0076
        L_0x0074:
            java.lang.Class<java.lang.Object> r1 = java.lang.Object.class
        L_0x0076:
            r2 = r1
            java.lang.Class r2 = (java.lang.Class) r2
            java.util.EnumSet r1 = java.util.EnumSet.noneOf(r2)
            goto L_0x0086
        L_0x007e:
            java.lang.Object r2 = r0.newInstance()     // Catch:{ Exception -> 0x009a }
            java.util.Collection r2 = (java.util.Collection) r2     // Catch:{ Exception -> 0x009a }
            r1 = r2
        L_0x0086:
            boolean r2 = r8 instanceof java.lang.reflect.ParameterizedType
            if (r2 == 0) goto L_0x0094
            r2 = r8
            java.lang.reflect.ParameterizedType r2 = (java.lang.reflect.ParameterizedType) r2
            java.lang.reflect.Type[] r2 = r2.getActualTypeArguments()
            r2 = r2[r3]
            goto L_0x0096
        L_0x0094:
            java.lang.Class<java.lang.Object> r2 = java.lang.Object.class
        L_0x0096:
            r7.parseArray(r2, r1, r9)
            return r1
        L_0x009a:
            r2 = move-exception
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "create instane error, class "
            r4.append(r5)
            java.lang.String r5 = r0.getName()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.CollectionDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }

    public Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        }
        throw new JSONException("TODO");
    }

    public int getFastMatchToken() {
        return 14;
    }
}
