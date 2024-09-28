package com.alibaba.fastjson.parser.deserializer;

public class ArrayDeserializer implements ObjectDeserializer {
    public static final ArrayDeserializer instance = new ArrayDeserializer();

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: java.lang.reflect.Type[]} */
    /* JADX WARNING: type inference failed for: r2v2, types: [java.lang.reflect.Type] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r13, java.lang.reflect.Type r14, java.lang.Object r15) {
        /*
            r12 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r13.getLexer()
            int r1 = r0.token()
            r2 = 16
            r3 = 8
            if (r1 != r3) goto L_0x0013
            r0.nextToken(r2)
            r1 = 0
            return r1
        L_0x0013:
            int r1 = r0.token()
            r3 = 4
            if (r1 != r3) goto L_0x0022
            byte[] r1 = r0.bytesValue()
            r0.nextToken(r2)
            return r1
        L_0x0022:
            boolean r1 = r14 instanceof java.lang.reflect.GenericArrayType
            if (r1 == 0) goto L_0x0082
            r1 = r14
            java.lang.reflect.GenericArrayType r1 = (java.lang.reflect.GenericArrayType) r1
            java.lang.reflect.Type r2 = r1.getGenericComponentType()
            boolean r3 = r2 instanceof java.lang.reflect.TypeVariable
            if (r3 == 0) goto L_0x007e
            r3 = r2
            java.lang.reflect.TypeVariable r3 = (java.lang.reflect.TypeVariable) r3
            com.alibaba.fastjson.parser.ParseContext r4 = r13.getContext()
            java.lang.reflect.Type r4 = r4.getType()
            boolean r5 = r4 instanceof java.lang.reflect.ParameterizedType
            if (r5 == 0) goto L_0x007b
            r5 = r4
            java.lang.reflect.ParameterizedType r5 = (java.lang.reflect.ParameterizedType) r5
            java.lang.reflect.Type r6 = r5.getRawType()
            r7 = 0
            boolean r8 = r6 instanceof java.lang.Class
            if (r8 == 0) goto L_0x0070
            r8 = r6
            java.lang.Class r8 = (java.lang.Class) r8
            java.lang.reflect.TypeVariable[] r8 = r8.getTypeParameters()
            r9 = 0
        L_0x0054:
            int r10 = r8.length
            if (r9 >= r10) goto L_0x0070
            r10 = r8[r9]
            java.lang.String r10 = r10.getName()
            java.lang.String r11 = r3.getName()
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x006d
            java.lang.reflect.Type[] r10 = r5.getActualTypeArguments()
            r7 = r10[r9]
        L_0x006d:
            int r9 = r9 + 1
            goto L_0x0054
        L_0x0070:
            boolean r8 = r7 instanceof java.lang.Class
            if (r8 == 0) goto L_0x0078
            r8 = r7
            java.lang.Class r8 = (java.lang.Class) r8
            goto L_0x007a
        L_0x0078:
            java.lang.Class<java.lang.Object> r8 = java.lang.Object.class
        L_0x007a:
            goto L_0x007d
        L_0x007b:
            java.lang.Class<java.lang.Object> r8 = java.lang.Object.class
        L_0x007d:
            goto L_0x0081
        L_0x007e:
            r8 = r2
            java.lang.Class r8 = (java.lang.Class) r8
        L_0x0081:
            goto L_0x008a
        L_0x0082:
            r1 = r14
            java.lang.Class r1 = (java.lang.Class) r1
            java.lang.Class r2 = r1.getComponentType()
            r8 = r2
        L_0x008a:
            com.alibaba.fastjson.JSONArray r1 = new com.alibaba.fastjson.JSONArray
            r1.<init>()
            r13.parseArray(r8, r1, r15)
            java.lang.Object r3 = r12.toObjectArray(r13, r8, r1)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.ArrayDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [java.lang.reflect.Type, java.lang.Class<?>, java.lang.Class] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T> T toObjectArray(com.alibaba.fastjson.parser.DefaultJSONParser r11, java.lang.Class<?> r12, com.alibaba.fastjson.JSONArray r13) {
        /*
            r10 = this;
            if (r13 != 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            int r0 = r13.size()
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r12, r0)
            r2 = 0
        L_0x000d:
            if (r2 >= r0) goto L_0x0065
            java.lang.Object r3 = r13.get(r2)
            if (r3 != r13) goto L_0x0019
            java.lang.reflect.Array.set(r1, r2, r1)
            goto L_0x0062
        L_0x0019:
            boolean r4 = r12.isArray()
            if (r4 == 0) goto L_0x0032
            boolean r4 = r12.isInstance(r3)
            if (r4 == 0) goto L_0x0027
            r4 = r3
            goto L_0x002e
        L_0x0027:
            r4 = r3
            com.alibaba.fastjson.JSONArray r4 = (com.alibaba.fastjson.JSONArray) r4
            java.lang.Object r4 = r10.toObjectArray(r11, r12, r4)
        L_0x002e:
            java.lang.reflect.Array.set(r1, r2, r4)
            goto L_0x0062
        L_0x0032:
            r4 = 0
            boolean r5 = r3 instanceof com.alibaba.fastjson.JSONArray
            if (r5 == 0) goto L_0x0055
            r5 = 0
            r6 = r3
            com.alibaba.fastjson.JSONArray r6 = (com.alibaba.fastjson.JSONArray) r6
            int r7 = r6.size()
            r8 = 0
        L_0x0040:
            if (r8 >= r7) goto L_0x004f
            java.lang.Object r9 = r6.get(r8)
            if (r9 != r13) goto L_0x004c
            r6.set(r2, r1)
            r5 = 1
        L_0x004c:
            int r8 = r8 + 1
            goto L_0x0040
        L_0x004f:
            if (r5 == 0) goto L_0x0055
            java.lang.Object[] r4 = r6.toArray()
        L_0x0055:
            if (r4 != 0) goto L_0x005f
            com.alibaba.fastjson.parser.ParserConfig r5 = r11.getConfig()
            java.lang.Object r4 = com.alibaba.fastjson.util.TypeUtils.cast((java.lang.Object) r3, r12, (com.alibaba.fastjson.parser.ParserConfig) r5)
        L_0x005f:
            java.lang.reflect.Array.set(r1, r2, r4)
        L_0x0062:
            int r2 = r2 + 1
            goto L_0x000d
        L_0x0065:
            r13.setRelatedArray(r1)
            r13.setComponentType(r12)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.ArrayDeserializer.toObjectArray(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.Class, com.alibaba.fastjson.JSONArray):java.lang.Object");
    }

    public int getFastMatchToken() {
        return 14;
    }
}
