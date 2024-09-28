package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class ListResolveFieldDeserializer extends FieldDeserializer {
    private final int index;
    private final List list;
    private final DefaultJSONParser parser;

    public ListResolveFieldDeserializer(DefaultJSONParser parser2, List list2, int index2) {
        super((Class<?>) null, (FieldInfo) null);
        this.parser = parser2;
        this.index = index2;
        this.list = list2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000d, code lost:
        r0 = (com.alibaba.fastjson.JSONArray) r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setValue(java.lang.Object r6, java.lang.Object r7) {
        /*
            r5 = this;
            java.util.List r0 = r5.list
            int r1 = r5.index
            r0.set(r1, r7)
            java.util.List r0 = r5.list
            boolean r1 = r0 instanceof com.alibaba.fastjson.JSONArray
            if (r1 == 0) goto L_0x0038
            com.alibaba.fastjson.JSONArray r0 = (com.alibaba.fastjson.JSONArray) r0
            java.lang.Object r1 = r0.getRelatedArray()
            if (r1 == 0) goto L_0x0038
            int r2 = java.lang.reflect.Array.getLength(r1)
            int r3 = r5.index
            if (r2 <= r3) goto L_0x0038
            java.lang.reflect.Type r3 = r0.getComponentType()
            if (r3 == 0) goto L_0x0032
            java.lang.reflect.Type r3 = r0.getComponentType()
            com.alibaba.fastjson.parser.DefaultJSONParser r4 = r5.parser
            com.alibaba.fastjson.parser.ParserConfig r4 = r4.getConfig()
            java.lang.Object r3 = com.alibaba.fastjson.util.TypeUtils.cast((java.lang.Object) r7, (java.lang.reflect.Type) r3, (com.alibaba.fastjson.parser.ParserConfig) r4)
            goto L_0x0033
        L_0x0032:
            r3 = r7
        L_0x0033:
            int r4 = r5.index
            java.lang.reflect.Array.set(r1, r4, r3)
        L_0x0038:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.ListResolveFieldDeserializer.setValue(java.lang.Object, java.lang.Object):void");
    }

    public void parseField(DefaultJSONParser parser2, Object object, Type objectType, Map<String, Object> map) {
    }
}
