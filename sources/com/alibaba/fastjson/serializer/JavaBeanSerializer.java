package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaBeanSerializer implements ObjectSerializer {
    private int features;
    private final FieldSerializer[] getters;
    private final FieldSerializer[] sortedGetters;

    public FieldSerializer[] getGetters() {
        return this.getters;
    }

    public JavaBeanSerializer(Class<?> clazz) {
        this(clazz, (Map<String, String>) null);
    }

    public JavaBeanSerializer(Class<?> clazz, String... aliasList) {
        this(clazz, createAliasMap(aliasList));
    }

    static Map<String, String> createAliasMap(String... aliasList) {
        Map<String, String> aliasMap = new HashMap<>();
        for (String alias : aliasList) {
            aliasMap.put(alias, alias);
        }
        return aliasMap;
    }

    public JavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) {
        this.features = 0;
        this.features = TypeUtils.getSerializeFeatures(clazz);
        List<FieldSerializer> getterList = new ArrayList<>();
        for (FieldInfo fieldInfo : TypeUtils.computeGetters(clazz, aliasMap, false)) {
            getterList.add(createFieldSerializer(fieldInfo));
        }
        this.getters = (FieldSerializer[]) getterList.toArray(new FieldSerializer[getterList.size()]);
        List<FieldSerializer> getterList2 = new ArrayList<>();
        for (FieldInfo fieldInfo2 : TypeUtils.computeGetters(clazz, aliasMap, true)) {
            getterList2.add(createFieldSerializer(fieldInfo2));
        }
        this.sortedGetters = (FieldSerializer[]) getterList2.toArray(new FieldSerializer[getterList2.size()]);
    }

    /* access modifiers changed from: protected */
    public boolean isWriteClassName(JSONSerializer serializer, Object obj, Type fieldType, Object fieldName) {
        return serializer.isWriteClassName(fieldType, obj);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0189, code lost:
        if (((java.lang.Boolean) r0).booleanValue() == false) goto L_0x018c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void write(com.alibaba.fastjson.serializer.JSONSerializer r24, java.lang.Object r25, java.lang.Object r26, java.lang.reflect.Type r27) throws java.io.IOException {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            r3 = r25
            r4 = r26
            r5 = r27
            com.alibaba.fastjson.serializer.SerializeWriter r6 = r24.getWriter()
            if (r3 != 0) goto L_0x0014
            r6.writeNull()
            return
        L_0x0014:
            boolean r0 = r23.writeReference(r24, r25)
            if (r0 == 0) goto L_0x001b
            return
        L_0x001b:
            com.alibaba.fastjson.serializer.SerializerFeature r0 = com.alibaba.fastjson.serializer.SerializerFeature.SortField
            boolean r0 = r6.isEnabled(r0)
            if (r0 == 0) goto L_0x0027
            com.alibaba.fastjson.serializer.FieldSerializer[] r0 = r1.sortedGetters
            r7 = r0
            goto L_0x002a
        L_0x0027:
            com.alibaba.fastjson.serializer.FieldSerializer[] r0 = r1.getters
            r7 = r0
        L_0x002a:
            com.alibaba.fastjson.serializer.SerialContext r8 = r24.getContext()
            int r0 = r1.features
            r2.setContext(r8, r3, r4, r0)
            boolean r9 = r23.isWriteAsArray(r24)
            if (r9 == 0) goto L_0x003c
            r0 = 91
            goto L_0x003e
        L_0x003c:
            r0 = 123(0x7b, float:1.72E-43)
        L_0x003e:
            if (r9 == 0) goto L_0x0043
            r10 = 93
            goto L_0x0045
        L_0x0043:
            r10 = 125(0x7d, float:1.75E-43)
        L_0x0045:
            r6.append((char) r0)     // Catch:{ Exception -> 0x0203 }
            int r11 = r7.length     // Catch:{ Exception -> 0x0203 }
            if (r11 <= 0) goto L_0x0059
            com.alibaba.fastjson.serializer.SerializerFeature r11 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x0203 }
            boolean r11 = r6.isEnabled(r11)     // Catch:{ Exception -> 0x0203 }
            if (r11 == 0) goto L_0x0059
            r24.incrementIndent()     // Catch:{ Exception -> 0x0203 }
            r24.println()     // Catch:{ Exception -> 0x0203 }
        L_0x0059:
            r11 = 0
            boolean r12 = r1.isWriteClassName(r2, r3, r5, r4)     // Catch:{ Exception -> 0x0203 }
            if (r12 == 0) goto L_0x0073
            java.lang.Class r12 = r25.getClass()     // Catch:{ Exception -> 0x0203 }
            if (r12 == r5) goto L_0x0073
            java.lang.String r13 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY     // Catch:{ Exception -> 0x0203 }
            r6.writeFieldName(r13)     // Catch:{ Exception -> 0x0203 }
            java.lang.Class r13 = r25.getClass()     // Catch:{ Exception -> 0x0203 }
            r2.write((java.lang.Object) r13)     // Catch:{ Exception -> 0x0203 }
            r11 = 1
        L_0x0073:
            r13 = 44
            if (r11 == 0) goto L_0x007a
            r14 = 44
            goto L_0x007b
        L_0x007a:
            r14 = 0
        L_0x007b:
            char r15 = com.alibaba.fastjson.serializer.FilterUtils.writeBefore(r2, r3, r14)     // Catch:{ Exception -> 0x0203 }
            if (r15 != r13) goto L_0x0084
            r16 = 1
            goto L_0x0086
        L_0x0084:
            r16 = 0
        L_0x0086:
            r11 = r16
            r16 = 0
            r12 = r16
        L_0x008c:
            int r13 = r7.length     // Catch:{ Exception -> 0x0203 }
            if (r12 >= r13) goto L_0x01da
            r13 = r7[r12]     // Catch:{ Exception -> 0x0203 }
            r17 = r0
            com.alibaba.fastjson.serializer.SerializerFeature r0 = com.alibaba.fastjson.serializer.SerializerFeature.SkipTransientField     // Catch:{ Exception -> 0x0203 }
            boolean r0 = r2.isEnabled(r0)     // Catch:{ Exception -> 0x0203 }
            if (r0 == 0) goto L_0x00ad
            java.lang.reflect.Field r0 = r13.getField()     // Catch:{ Exception -> 0x0203 }
            if (r0 == 0) goto L_0x00ad
            int r18 = r0.getModifiers()     // Catch:{ Exception -> 0x0203 }
            boolean r18 = java.lang.reflect.Modifier.isTransient(r18)     // Catch:{ Exception -> 0x0203 }
            if (r18 == 0) goto L_0x00ad
            goto L_0x018c
        L_0x00ad:
            java.lang.String r0 = r13.getName()     // Catch:{ Exception -> 0x0203 }
            boolean r0 = com.alibaba.fastjson.serializer.FilterUtils.applyName(r2, r3, r0)     // Catch:{ Exception -> 0x0203 }
            if (r0 != 0) goto L_0x00b9
            goto L_0x018c
        L_0x00b9:
            java.lang.Object r0 = r13.getPropertyValue(r3)     // Catch:{ Exception -> 0x0203 }
            java.lang.String r1 = r13.getName()     // Catch:{ Exception -> 0x0203 }
            boolean r1 = com.alibaba.fastjson.serializer.FilterUtils.apply(r2, r3, r1, r0)     // Catch:{ Exception -> 0x0203 }
            if (r1 != 0) goto L_0x00c9
            goto L_0x018c
        L_0x00c9:
            java.lang.String r1 = r13.getName()     // Catch:{ Exception -> 0x0203 }
            java.lang.String r1 = com.alibaba.fastjson.serializer.FilterUtils.processKey(r2, r3, r1, r0)     // Catch:{ Exception -> 0x0203 }
            r18 = r0
            java.lang.String r4 = r13.getName()     // Catch:{ Exception -> 0x0203 }
            java.lang.Object r4 = com.alibaba.fastjson.serializer.FilterUtils.processValue(r2, r3, r4, r0)     // Catch:{ Exception -> 0x0203 }
            r0 = r4
            if (r0 != 0) goto L_0x00f0
            if (r9 != 0) goto L_0x00f0
            boolean r4 = r13.isWriteNull()     // Catch:{ Exception -> 0x0203 }
            if (r4 != 0) goto L_0x00f0
            com.alibaba.fastjson.serializer.SerializerFeature r4 = com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue     // Catch:{ Exception -> 0x0203 }
            boolean r4 = r2.isEnabled(r4)     // Catch:{ Exception -> 0x0203 }
            if (r4 != 0) goto L_0x00f0
            goto L_0x018c
        L_0x00f0:
            if (r0 == 0) goto L_0x018f
            com.alibaba.fastjson.serializer.SerializerFeature r4 = com.alibaba.fastjson.serializer.SerializerFeature.NotWriteDefaultValue     // Catch:{ Exception -> 0x0203 }
            boolean r4 = r2.isEnabled(r4)     // Catch:{ Exception -> 0x0203 }
            if (r4 == 0) goto L_0x018f
            com.alibaba.fastjson.util.FieldInfo r4 = r13.fieldInfo     // Catch:{ Exception -> 0x0203 }
            java.lang.Class r4 = r4.getFieldClass()     // Catch:{ Exception -> 0x0203 }
            java.lang.Class r5 = java.lang.Byte.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x0113
            boolean r5 = r0 instanceof java.lang.Byte     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x0113
            r5 = r0
            java.lang.Byte r5 = (java.lang.Byte) r5     // Catch:{ Exception -> 0x0203 }
            byte r5 = r5.byteValue()     // Catch:{ Exception -> 0x0203 }
            if (r5 != 0) goto L_0x0113
            goto L_0x018c
        L_0x0113:
            java.lang.Class r5 = java.lang.Short.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x0126
            boolean r5 = r0 instanceof java.lang.Short     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x0126
            r5 = r0
            java.lang.Short r5 = (java.lang.Short) r5     // Catch:{ Exception -> 0x0203 }
            short r5 = r5.shortValue()     // Catch:{ Exception -> 0x0203 }
            if (r5 != 0) goto L_0x0126
            goto L_0x018c
        L_0x0126:
            java.lang.Class r5 = java.lang.Integer.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x0138
            boolean r5 = r0 instanceof java.lang.Integer     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x0138
            r5 = r0
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ Exception -> 0x0203 }
            int r5 = r5.intValue()     // Catch:{ Exception -> 0x0203 }
            if (r5 != 0) goto L_0x0138
            goto L_0x018c
        L_0x0138:
            java.lang.Class r5 = java.lang.Long.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x014e
            boolean r5 = r0 instanceof java.lang.Long     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x014e
            r5 = r0
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ Exception -> 0x0203 }
            long r19 = r5.longValue()     // Catch:{ Exception -> 0x0203 }
            r21 = 0
            int r5 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r5 != 0) goto L_0x014e
            goto L_0x018c
        L_0x014e:
            java.lang.Class r5 = java.lang.Float.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x0164
            boolean r5 = r0 instanceof java.lang.Float     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x0164
            r5 = r0
            java.lang.Float r5 = (java.lang.Float) r5     // Catch:{ Exception -> 0x0203 }
            float r5 = r5.floatValue()     // Catch:{ Exception -> 0x0203 }
            r19 = 0
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 != 0) goto L_0x0164
            goto L_0x018c
        L_0x0164:
            java.lang.Class r5 = java.lang.Double.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x017a
            boolean r5 = r0 instanceof java.lang.Double     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x017a
            r5 = r0
            java.lang.Double r5 = (java.lang.Double) r5     // Catch:{ Exception -> 0x0203 }
            double r19 = r5.doubleValue()     // Catch:{ Exception -> 0x0203 }
            r21 = 0
            int r5 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r5 != 0) goto L_0x017a
            goto L_0x018c
        L_0x017a:
            java.lang.Class r5 = java.lang.Boolean.TYPE     // Catch:{ Exception -> 0x0203 }
            if (r4 != r5) goto L_0x018f
            boolean r5 = r0 instanceof java.lang.Boolean     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x018f
            r5 = r0
            java.lang.Boolean r5 = (java.lang.Boolean) r5     // Catch:{ Exception -> 0x0203 }
            boolean r5 = r5.booleanValue()     // Catch:{ Exception -> 0x0203 }
            if (r5 != 0) goto L_0x018f
        L_0x018c:
            r4 = 44
            goto L_0x01cc
        L_0x018f:
            if (r11 == 0) goto L_0x01a2
            r4 = 44
            r6.append((char) r4)     // Catch:{ Exception -> 0x0203 }
            com.alibaba.fastjson.serializer.SerializerFeature r5 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x0203 }
            boolean r5 = r6.isEnabled(r5)     // Catch:{ Exception -> 0x0203 }
            if (r5 == 0) goto L_0x01a4
            r24.println()     // Catch:{ Exception -> 0x0203 }
            goto L_0x01a4
        L_0x01a2:
            r4 = 44
        L_0x01a4:
            java.lang.String r5 = r13.getName()     // Catch:{ Exception -> 0x0203 }
            if (r1 == r5) goto L_0x01b5
            if (r9 != 0) goto L_0x01af
            r6.writeFieldName(r1)     // Catch:{ Exception -> 0x0203 }
        L_0x01af:
            r2.write((java.lang.Object) r0)     // Catch:{ Exception -> 0x0203 }
            r5 = r18
            goto L_0x01cb
        L_0x01b5:
            r5 = r18
            if (r5 == r0) goto L_0x01c2
            if (r9 != 0) goto L_0x01be
            r13.writePrefix(r2)     // Catch:{ Exception -> 0x0203 }
        L_0x01be:
            r2.write((java.lang.Object) r0)     // Catch:{ Exception -> 0x0203 }
            goto L_0x01cb
        L_0x01c2:
            if (r9 != 0) goto L_0x01c8
            r13.writeProperty(r2, r0)     // Catch:{ Exception -> 0x0203 }
            goto L_0x01cb
        L_0x01c8:
            r13.writeValue(r2, r0)     // Catch:{ Exception -> 0x0203 }
        L_0x01cb:
            r11 = 1
        L_0x01cc:
            int r12 = r12 + 1
            r1 = r23
            r4 = r26
            r5 = r27
            r0 = r17
            r13 = 44
            goto L_0x008c
        L_0x01da:
            r17 = r0
            r4 = 44
            if (r11 == 0) goto L_0x01e3
            r12 = 44
            goto L_0x01e4
        L_0x01e3:
            r12 = 0
        L_0x01e4:
            com.alibaba.fastjson.serializer.FilterUtils.writeAfter(r2, r3, r12)     // Catch:{ Exception -> 0x0203 }
            int r0 = r7.length     // Catch:{ Exception -> 0x0203 }
            if (r0 <= 0) goto L_0x01f8
            com.alibaba.fastjson.serializer.SerializerFeature r0 = com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat     // Catch:{ Exception -> 0x0203 }
            boolean r0 = r6.isEnabled(r0)     // Catch:{ Exception -> 0x0203 }
            if (r0 == 0) goto L_0x01f8
            r24.decrementIdent()     // Catch:{ Exception -> 0x0203 }
            r24.println()     // Catch:{ Exception -> 0x0203 }
        L_0x01f8:
            r6.append((char) r10)     // Catch:{ Exception -> 0x0203 }
            r2.setContext(r8)
            return
        L_0x0201:
            r0 = move-exception
            goto L_0x020c
        L_0x0203:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0201 }
            java.lang.String r4 = "write javaBean error"
            r1.<init>(r4, r0)     // Catch:{ all -> 0x0201 }
            throw r1     // Catch:{ all -> 0x0201 }
        L_0x020c:
            r2.setContext(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.serializer.JavaBeanSerializer.write(com.alibaba.fastjson.serializer.JSONSerializer, java.lang.Object, java.lang.Object, java.lang.reflect.Type):void");
    }

    public boolean writeReference(JSONSerializer serializer, Object object) {
        SerialContext context = serializer.getContext();
        if ((context != null && context.isEnabled(SerializerFeature.DisableCircularReferenceDetect)) || !serializer.containsReference(object)) {
            return false;
        }
        serializer.writeReference(object);
        return true;
    }

    public FieldSerializer createFieldSerializer(FieldInfo fieldInfo) {
        if (fieldInfo.getFieldClass() == Number.class) {
            return new NumberFieldSerializer(fieldInfo);
        }
        return new ObjectFieldSerializer(fieldInfo);
    }

    public boolean isWriteAsArray(JSONSerializer serializer) {
        if (!SerializerFeature.isEnabled(this.features, SerializerFeature.BeanToArray) && !serializer.isEnabled(SerializerFeature.BeanToArray)) {
            return false;
        }
        return true;
    }
}
