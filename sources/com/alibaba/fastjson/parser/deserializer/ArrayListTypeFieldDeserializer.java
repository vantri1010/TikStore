package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParseContext;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ArrayListTypeFieldDeserializer extends FieldDeserializer {
    private ObjectDeserializer deserializer;
    private int itemFastMatchToken;
    private final Type itemType;

    public ArrayListTypeFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
        if (getFieldType() instanceof ParameterizedType) {
            this.itemType = ((ParameterizedType) getFieldType()).getActualTypeArguments()[0];
        } else {
            this.itemType = Object.class;
        }
    }

    public int getFastMatchToken() {
        return 14;
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (parser.getLexer().token() == 8) {
            setValue(object, (String) null);
            return;
        }
        ArrayList list = new ArrayList();
        ParseContext context = parser.getContext();
        parser.setContext(context, object, this.fieldInfo.getName());
        parseArray(parser, objectType, list);
        parser.setContext(context);
        if (object == null) {
            fieldValues.put(this.fieldInfo.getName(), list);
        } else {
            setValue(object, (Object) list);
        }
    }

    /* JADX WARNING: type inference failed for: r5v8, types: [java.lang.reflect.Type] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void parseArray(com.alibaba.fastjson.parser.DefaultJSONParser r12, java.lang.reflect.Type r13, java.util.Collection r14) {
        /*
            r11 = this;
            java.lang.reflect.Type r0 = r11.itemType
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r1 = r11.deserializer
            boolean r2 = r0 instanceof java.lang.reflect.TypeVariable
            if (r2 == 0) goto L_0x005f
            boolean r2 = r13 instanceof java.lang.reflect.ParameterizedType
            if (r2 == 0) goto L_0x005f
            r2 = r0
            java.lang.reflect.TypeVariable r2 = (java.lang.reflect.TypeVariable) r2
            r3 = r13
            java.lang.reflect.ParameterizedType r3 = (java.lang.reflect.ParameterizedType) r3
            r4 = 0
            java.lang.reflect.Type r5 = r3.getRawType()
            boolean r5 = r5 instanceof java.lang.Class
            if (r5 == 0) goto L_0x0022
            java.lang.reflect.Type r5 = r3.getRawType()
            r4 = r5
            java.lang.Class r4 = (java.lang.Class) r4
        L_0x0022:
            r5 = -1
            if (r4 == 0) goto L_0x0046
            r6 = 0
            java.lang.reflect.TypeVariable[] r7 = r4.getTypeParameters()
            int r7 = r7.length
        L_0x002b:
            if (r6 >= r7) goto L_0x0046
            java.lang.reflect.TypeVariable[] r8 = r4.getTypeParameters()
            r8 = r8[r6]
            java.lang.String r9 = r8.getName()
            java.lang.String r10 = r2.getName()
            boolean r9 = r9.equals(r10)
            if (r9 == 0) goto L_0x0043
            r5 = r6
            goto L_0x0046
        L_0x0043:
            int r6 = r6 + 1
            goto L_0x002b
        L_0x0046:
            r6 = -1
            if (r5 == r6) goto L_0x005f
            java.lang.reflect.Type[] r6 = r3.getActualTypeArguments()
            r0 = r6[r5]
            java.lang.reflect.Type r6 = r11.itemType
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x005f
            com.alibaba.fastjson.parser.ParserConfig r6 = r12.getConfig()
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r1 = r6.getDeserializer((java.lang.reflect.Type) r0)
        L_0x005f:
            com.alibaba.fastjson.parser.JSONLexer r2 = r12.getLexer()
            int r3 = r2.token()
            r4 = 14
            if (r3 == r4) goto L_0x00a0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "exepct '[', but "
            r3.append(r4)
            int r4 = r2.token()
            java.lang.String r4 = com.alibaba.fastjson.parser.JSONToken.name(r4)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            if (r13 == 0) goto L_0x009a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r5 = ", type : "
            r4.append(r5)
            r4.append(r13)
            java.lang.String r3 = r4.toString()
        L_0x009a:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException
            r4.<init>(r3)
            throw r4
        L_0x00a0:
            if (r1 != 0) goto L_0x00b3
            com.alibaba.fastjson.parser.ParserConfig r3 = r12.getConfig()
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r3 = r3.getDeserializer((java.lang.reflect.Type) r0)
            r11.deserializer = r3
            r1 = r3
            int r3 = r3.getFastMatchToken()
            r11.itemFastMatchToken = r3
        L_0x00b3:
            int r3 = r11.itemFastMatchToken
            r2.nextToken(r3)
            r3 = 0
        L_0x00b9:
            com.alibaba.fastjson.parser.Feature r4 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            boolean r4 = r2.isEnabled(r4)
            r5 = 16
            if (r4 == 0) goto L_0x00cd
        L_0x00c3:
            int r4 = r2.token()
            if (r4 != r5) goto L_0x00cd
            r2.nextToken()
            goto L_0x00c3
        L_0x00cd:
            int r4 = r2.token()
            r6 = 15
            if (r4 != r6) goto L_0x00da
            r2.nextToken(r5)
            return
        L_0x00da:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            java.lang.Object r4 = r1.deserialze(r12, r0, r4)
            r14.add(r4)
            r12.checkListResolve(r14)
            int r6 = r2.token()
            if (r6 != r5) goto L_0x00f3
            int r5 = r11.itemFastMatchToken
            r2.nextToken(r5)
        L_0x00f3:
            int r3 = r3 + 1
            goto L_0x00b9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.ArrayListTypeFieldDeserializer.parseArray(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.util.Collection):void");
    }
}
