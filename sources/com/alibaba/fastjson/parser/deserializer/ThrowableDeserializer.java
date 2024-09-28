package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.ParserConfig;
import java.lang.reflect.Constructor;

public class ThrowableDeserializer extends JavaBeanDeserializer {
    public ThrowableDeserializer(ParserConfig mapping, Class<?> clazz) {
        super(mapping, clazz);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: java.lang.StackTraceElement[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.lang.Throwable} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r17, java.lang.reflect.Type r18, java.lang.Object r19) {
        /*
            r16 = this;
            r1 = r16
            r2 = r17
            r3 = r18
            com.alibaba.fastjson.parser.JSONLexer r4 = r17.getLexer()
            int r0 = r4.token()
            r5 = 0
            r6 = 8
            if (r0 != r6) goto L_0x0017
            r4.nextToken()
            return r5
        L_0x0017:
            int r0 = r17.getResolveStatus()
            r7 = 2
            java.lang.String r8 = "syntax error"
            if (r0 != r7) goto L_0x0025
            r0 = 0
            r2.setResolveStatus(r0)
            goto L_0x002d
        L_0x0025:
            int r0 = r4.token()
            r7 = 12
            if (r0 != r7) goto L_0x0118
        L_0x002d:
            r0 = 0
            r7 = 0
            if (r3 == 0) goto L_0x0041
            boolean r9 = r3 instanceof java.lang.Class
            if (r9 == 0) goto L_0x0041
            r9 = r3
            java.lang.Class r9 = (java.lang.Class) r9
            java.lang.Class<java.lang.Throwable> r10 = java.lang.Throwable.class
            boolean r10 = r10.isAssignableFrom(r9)
            if (r10 == 0) goto L_0x0041
            r7 = r9
        L_0x0041:
            r9 = 0
            r10 = 0
            java.util.HashMap r11 = new java.util.HashMap
            r11.<init>()
        L_0x0048:
            com.alibaba.fastjson.parser.SymbolTable r12 = r17.getSymbolTable()
            java.lang.String r12 = r4.scanSymbol(r12)
            r13 = 13
            r14 = 16
            if (r12 != 0) goto L_0x0071
            int r15 = r4.token()
            if (r15 != r13) goto L_0x0062
            r4.nextToken(r14)
            r5 = r0
            goto L_0x00ef
        L_0x0062:
            int r15 = r4.token()
            if (r15 != r14) goto L_0x0071
            com.alibaba.fastjson.parser.Feature r15 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            boolean r15 = r4.isEnabled(r15)
            if (r15 == 0) goto L_0x0071
            goto L_0x0048
        L_0x0071:
            r15 = 4
            r4.nextTokenWithColon(r15)
            java.lang.String r13 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY
            boolean r13 = r13.equals(r12)
            if (r13 == 0) goto L_0x0096
            int r13 = r4.token()
            if (r13 != r15) goto L_0x0090
            java.lang.String r13 = r4.stringVal()
            java.lang.Class r7 = com.alibaba.fastjson.util.TypeUtils.loadClass(r13)
            r4.nextToken(r14)
            goto L_0x00e3
        L_0x0090:
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException
            r5.<init>(r8)
            throw r5
        L_0x0096:
            java.lang.String r13 = "message"
            boolean r13 = r13.equals(r12)
            if (r13 == 0) goto L_0x00ba
            int r13 = r4.token()
            if (r13 != r6) goto L_0x00a6
            r9 = 0
            goto L_0x00b0
        L_0x00a6:
            int r13 = r4.token()
            if (r13 != r15) goto L_0x00b4
            java.lang.String r9 = r4.stringVal()
        L_0x00b0:
            r4.nextToken()
            goto L_0x00e3
        L_0x00b4:
            com.alibaba.fastjson.JSONException r5 = new com.alibaba.fastjson.JSONException
            r5.<init>(r8)
            throw r5
        L_0x00ba:
            java.lang.String r13 = "cause"
            boolean r15 = r13.equals(r12)
            if (r15 == 0) goto L_0x00ca
            java.lang.Object r13 = r1.deserialze(r2, r5, r13)
            r0 = r13
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            goto L_0x00e3
        L_0x00ca:
            java.lang.String r13 = "stackTrace"
            boolean r13 = r13.equals(r12)
            if (r13 == 0) goto L_0x00dc
            java.lang.Class<java.lang.StackTraceElement[]> r13 = java.lang.StackTraceElement[].class
            java.lang.Object r13 = r2.parseObject(r13)
            r10 = r13
            java.lang.StackTraceElement[] r10 = (java.lang.StackTraceElement[]) r10
            goto L_0x00e3
        L_0x00dc:
            java.lang.Object r13 = r17.parse()
            r11.put(r12, r13)
        L_0x00e3:
            int r13 = r4.token()
            r15 = 13
            if (r13 != r15) goto L_0x0116
            r4.nextToken(r14)
            r5 = r0
        L_0x00ef:
            r6 = 0
            if (r7 != 0) goto L_0x00f8
            java.lang.Exception r0 = new java.lang.Exception
            r0.<init>(r9, r5)
            goto L_0x0107
        L_0x00f8:
            java.lang.Throwable r0 = r1.createException(r9, r5, r7)     // Catch:{ Exception -> 0x010d }
            r6 = r0
            if (r6 != 0) goto L_0x0105
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x010d }
            r0.<init>(r9, r5)     // Catch:{ Exception -> 0x010d }
            goto L_0x0106
        L_0x0105:
            r0 = r6
        L_0x0106:
        L_0x0107:
            if (r10 == 0) goto L_0x010c
            r0.setStackTrace(r10)
        L_0x010c:
            return r0
        L_0x010d:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r8 = new com.alibaba.fastjson.JSONException
            java.lang.String r12 = "create instance error"
            r8.<init>(r12, r0)
            throw r8
        L_0x0116:
            goto L_0x0048
        L_0x0118:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            r0.<init>(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.ThrowableDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object):java.lang.Object");
    }

    private Throwable createException(String message, Throwable cause, Class<?> exClass) throws Exception {
        Constructor<?> defaultConstructor = null;
        Constructor<?> messageConstructor = null;
        Constructor<?> causeConstructor = null;
        for (Constructor<?> constructor : exClass.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                defaultConstructor = constructor;
            } else if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == String.class) {
                messageConstructor = constructor;
            } else if (constructor.getParameterTypes().length == 2 && constructor.getParameterTypes()[0] == String.class && constructor.getParameterTypes()[1] == Throwable.class) {
                causeConstructor = constructor;
            }
        }
        if (causeConstructor != null) {
            return (Throwable) causeConstructor.newInstance(new Object[]{message, cause});
        } else if (messageConstructor != null) {
            return (Throwable) messageConstructor.newInstance(new Object[]{message});
        } else if (defaultConstructor != null) {
            return (Throwable) defaultConstructor.newInstance(new Object[0]);
        } else {
            return null;
        }
    }

    public int getFastMatchToken() {
        return 12;
    }
}
