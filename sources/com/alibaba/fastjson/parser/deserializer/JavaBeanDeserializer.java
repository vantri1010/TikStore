package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.FilterUtils;
import com.alibaba.fastjson.util.DeserializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class JavaBeanDeserializer implements ObjectDeserializer {
    private DeserializeBeanInfo beanInfo;
    private final Class<?> clazz;
    private final Map<String, FieldDeserializer> feildDeserializerMap;
    private final List<FieldDeserializer> fieldDeserializers;
    private final List<FieldDeserializer> sortedFieldDeserializers;

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz2) {
        this(config, clazz2, clazz2);
    }

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz2, Type type) {
        this.feildDeserializerMap = new IdentityHashMap();
        this.fieldDeserializers = new ArrayList();
        this.sortedFieldDeserializers = new ArrayList();
        this.clazz = clazz2;
        DeserializeBeanInfo computeSetters = DeserializeBeanInfo.computeSetters(clazz2, type);
        this.beanInfo = computeSetters;
        for (FieldInfo fieldInfo : computeSetters.getFieldList()) {
            addFieldDeserializer(config, clazz2, fieldInfo);
        }
        for (FieldInfo fieldInfo2 : this.beanInfo.getSortedFieldList()) {
            this.sortedFieldDeserializers.add(this.feildDeserializerMap.get(fieldInfo2.getName().intern()));
        }
    }

    public Map<String, FieldDeserializer> getFieldDeserializerMap() {
        return this.feildDeserializerMap;
    }

    private void addFieldDeserializer(ParserConfig mapping, Class<?> clazz2, FieldInfo fieldInfo) {
        String interName = fieldInfo.getName().intern();
        FieldDeserializer fieldDeserializer = createFieldDeserializer(mapping, clazz2, fieldInfo);
        this.feildDeserializerMap.put(interName, fieldDeserializer);
        this.fieldDeserializers.add(fieldDeserializer);
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz2, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz2, fieldInfo);
    }

    public Object createInstance(DefaultJSONParser parser, Type type) {
        Object object;
        if ((type instanceof Class) && this.clazz.isInterface()) {
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{(Class) type}, new JSONObject());
        } else if (this.beanInfo.getDefaultConstructor() == null) {
            return null;
        } else {
            try {
                Constructor<?> constructor = this.beanInfo.getDefaultConstructor();
                if (constructor.getParameterTypes().length == 0) {
                    object = constructor.newInstance(new Object[0]);
                } else {
                    object = constructor.newInstance(new Object[]{parser.getContext().getObject()});
                }
                if (parser.isEnabled(Feature.InitStringFieldAsEmpty)) {
                    for (FieldInfo fieldInfo : this.beanInfo.getFieldList()) {
                        if (fieldInfo.getFieldClass() == String.class) {
                            try {
                                fieldInfo.set(object, "");
                            } catch (Exception e) {
                                throw new JSONException("create instance error, class " + this.clazz.getName(), e);
                            }
                        }
                    }
                }
                return object;
            } catch (Exception e2) {
                throw new JSONException("create instance error, class " + this.clazz.getName(), e2);
            }
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, (Object) null);
    }

    public <T> T deserialzeArrayMapping(DefaultJSONParser parser, Type type, Object fieldName, Object object) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 14) {
            Object object2 = createInstance(parser, type);
            int size = this.sortedFieldDeserializers.size();
            int i = 0;
            while (i < size) {
                char seperator = i == size + -1 ? ']' : ',';
                FieldDeserializer fieldDeser = this.sortedFieldDeserializers.get(i);
                Class<?> fieldClass = fieldDeser.getFieldClass();
                if (fieldClass == Integer.TYPE) {
                    fieldDeser.setValue(object2, lexer.scanInt(seperator));
                    DefaultJSONParser defaultJSONParser = parser;
                } else if (fieldClass == String.class) {
                    fieldDeser.setValue(object2, lexer.scanString(seperator));
                    DefaultJSONParser defaultJSONParser2 = parser;
                } else if (fieldClass == Long.TYPE) {
                    fieldDeser.setValue(object2, lexer.scanLong(seperator));
                    DefaultJSONParser defaultJSONParser3 = parser;
                } else if (fieldClass.isEnum()) {
                    fieldDeser.setValue(object2, (Object) lexer.scanEnum(fieldClass, parser.getSymbolTable(), seperator));
                    DefaultJSONParser defaultJSONParser4 = parser;
                } else {
                    lexer.nextToken(14);
                    fieldDeser.setValue(object2, parser.parseObject(fieldDeser.getFieldType()));
                    if (seperator == ']') {
                        if (lexer.token() == 15) {
                            lexer.nextToken(16);
                        } else {
                            throw new JSONException("syntax error");
                        }
                    } else if (seperator == ',' && lexer.token() != 16) {
                        throw new JSONException("syntax error");
                    }
                }
                i++;
            }
            DefaultJSONParser defaultJSONParser5 = parser;
            lexer.nextToken(16);
            return object2;
        }
        DefaultJSONParser defaultJSONParser6 = parser;
        throw new JSONException("error");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01e2, code lost:
        if (r2 == null) goto L_0x01e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x01e4, code lost:
        r2.setObject(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01e7, code lost:
        r8.setContext(r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x01ea, code lost:
        return r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0246, code lost:
        r1 = r0;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02a4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02c5, code lost:
        throw new com.alibaba.fastjson.JSONException("create instance error, " + r7.beanInfo.getCreatorConstructor().toGenericString(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02dd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02fe, code lost:
        throw new com.alibaba.fastjson.JSONException("create factory method error, " + r7.beanInfo.getFactoryMethod().toString(), r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02ff, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x0300, code lost:
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0343, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error, unexpect token " + com.alibaba.fastjson.parser.JSONToken.name(r11.token()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x034f, code lost:
        r2.setObject(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00e6, code lost:
        r11.nextTokenWithColon(4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00ed, code lost:
        if (r11.token() != 4) goto L_0x017f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00ef, code lost:
        r3 = r11.stringVal();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00f9, code lost:
        if ("@".equals(r3) == false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00fb, code lost:
        r1 = r14.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0107, code lost:
        if ("..".equals(r3) == false) goto L_0x0125;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0109, code lost:
        r4 = r14.getParentContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0111, code lost:
        if (r4.getObject() == null) goto L_0x0119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0113, code lost:
        r1 = r4.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0119, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r4, r3));
        r8.setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x012b, code lost:
        if ("$".equals(r3) == false) goto L_0x0152;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x012d, code lost:
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0132, code lost:
        if (r4.getParentContext() == null) goto L_0x013a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0134, code lost:
        r4 = r4.getParentContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x013e, code lost:
        if (r4.getObject() == null) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0140, code lost:
        r1 = r4.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0146, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r4, r3));
        r8.setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0152, code lost:
        r8.addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r14, r3));
        r8.setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x015d, code lost:
        r11.nextToken(13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0165, code lost:
        if (r11.token() != 13) goto L_0x0177;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0167, code lost:
        r11.nextToken(16);
        r8.setContext(r14, r1, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x016e, code lost:
        if (r2 == null) goto L_0x0173;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0170, code lost:
        r2.setObject(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0173, code lost:
        r8.setContext(r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0176, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x017e, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x019d, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref, " + com.alibaba.fastjson.parser.JSONToken.name(r11.token()));
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:141:0x024d, B:160:0x0295, B:169:0x02ce] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x034f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r20, java.lang.reflect.Type r21, java.lang.Object r22, java.lang.Object r23) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            r9 = r21
            r10 = r22
            r1 = r23
            java.lang.Class<com.alibaba.fastjson.JSON> r0 = com.alibaba.fastjson.JSON.class
            if (r9 == r0) goto L_0x0356
            java.lang.Class<com.alibaba.fastjson.JSONObject> r0 = com.alibaba.fastjson.JSONObject.class
            if (r9 != r0) goto L_0x0014
            goto L_0x0356
        L_0x0014:
            com.alibaba.fastjson.parser.JSONLexer r11 = r20.getLexer()
            int r0 = r11.token()
            r2 = 8
            r12 = 0
            r13 = 16
            if (r0 != r2) goto L_0x0027
            r11.nextToken(r13)
            return r12
        L_0x0027:
            com.alibaba.fastjson.parser.ParseContext r0 = r20.getContext()
            if (r1 == 0) goto L_0x0035
            if (r0 == 0) goto L_0x0035
            com.alibaba.fastjson.parser.ParseContext r0 = r0.getParentContext()
            r14 = r0
            goto L_0x0036
        L_0x0035:
            r14 = r0
        L_0x0036:
            r2 = 0
            r0 = 0
            int r3 = r11.token()     // Catch:{ all -> 0x034c }
            r15 = 13
            if (r3 != r15) goto L_0x0054
            r11.nextToken(r13)     // Catch:{ all -> 0x034c }
            if (r1 != 0) goto L_0x004a
            java.lang.Object r3 = r19.createInstance(r20, r21)     // Catch:{ all -> 0x034c }
            r1 = r3
        L_0x004a:
            if (r2 == 0) goto L_0x0050
            r2.setObject(r1)
        L_0x0050:
            r8.setContext(r14)
            return r1
        L_0x0054:
            int r3 = r11.token()     // Catch:{ all -> 0x034c }
            r4 = 14
            if (r3 != r4) goto L_0x006f
            boolean r3 = r7.isSupportArrayToBean(r11)     // Catch:{ all -> 0x034c }
            if (r3 == 0) goto L_0x006f
            java.lang.Object r3 = r19.deserialzeArrayMapping(r20, r21, r22, r23)     // Catch:{ all -> 0x034c }
            if (r2 == 0) goto L_0x006b
            r2.setObject(r1)
        L_0x006b:
            r8.setContext(r14)
            return r3
        L_0x006f:
            int r3 = r11.token()     // Catch:{ all -> 0x034c }
            r4 = 12
            if (r3 == r4) goto L_0x00b0
            int r3 = r11.token()     // Catch:{ all -> 0x034c }
            if (r3 == r13) goto L_0x00b0
            java.lang.StringBuffer r3 = new java.lang.StringBuffer     // Catch:{ all -> 0x034c }
            r3.<init>()     // Catch:{ all -> 0x034c }
            java.lang.String r4 = "syntax error, expect {, actual "
            r3.append(r4)     // Catch:{ all -> 0x034c }
            java.lang.String r4 = r11.tokenName()     // Catch:{ all -> 0x034c }
            r3.append(r4)     // Catch:{ all -> 0x034c }
            java.lang.String r4 = ", pos "
            r3.append(r4)     // Catch:{ all -> 0x034c }
            int r4 = r11.pos()     // Catch:{ all -> 0x034c }
            r3.append(r4)     // Catch:{ all -> 0x034c }
            boolean r4 = r10 instanceof java.lang.String     // Catch:{ all -> 0x034c }
            if (r4 == 0) goto L_0x00a6
            java.lang.String r4 = ", fieldName "
            r3.append(r4)     // Catch:{ all -> 0x034c }
            r3.append(r10)     // Catch:{ all -> 0x034c }
        L_0x00a6:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x034c }
            java.lang.String r5 = r3.toString()     // Catch:{ all -> 0x034c }
            r4.<init>(r5)     // Catch:{ all -> 0x034c }
            throw r4     // Catch:{ all -> 0x034c }
        L_0x00b0:
            int r3 = r20.getResolveStatus()     // Catch:{ all -> 0x034c }
            r4 = 2
            if (r3 != r4) goto L_0x00bb
            r3 = 0
            r8.setResolveStatus(r3)     // Catch:{ all -> 0x034c }
        L_0x00bb:
            com.alibaba.fastjson.parser.SymbolTable r3 = r20.getSymbolTable()     // Catch:{ all -> 0x034a }
            java.lang.String r3 = r11.scanSymbol(r3)     // Catch:{ all -> 0x034a }
            r6 = r3
            if (r6 != 0) goto L_0x00e0
            int r3 = r11.token()     // Catch:{ all -> 0x034a }
            if (r3 != r15) goto L_0x00d1
            r11.nextToken(r13)     // Catch:{ all -> 0x034a }
            goto L_0x01cd
        L_0x00d1:
            int r3 = r11.token()     // Catch:{ all -> 0x034a }
            if (r3 != r13) goto L_0x00e0
            com.alibaba.fastjson.parser.Feature r3 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x034a }
            boolean r3 = r8.isEnabled(r3)     // Catch:{ all -> 0x034a }
            if (r3 == 0) goto L_0x00e0
            goto L_0x00bb
        L_0x00e0:
            java.lang.String r3 = "$ref"
            r4 = 4
            r5 = 1
            if (r3 != r6) goto L_0x019e
            r11.nextTokenWithColon(r4)     // Catch:{ all -> 0x034a }
            int r3 = r11.token()     // Catch:{ all -> 0x034a }
            if (r3 != r4) goto L_0x017f
            java.lang.String r3 = r11.stringVal()     // Catch:{ all -> 0x034a }
            java.lang.String r4 = "@"
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x034a }
            if (r4 == 0) goto L_0x0101
            java.lang.Object r4 = r14.getObject()     // Catch:{ all -> 0x034a }
            r1 = r4
            goto L_0x015d
        L_0x0101:
            java.lang.String r4 = ".."
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x034a }
            if (r4 == 0) goto L_0x0125
            com.alibaba.fastjson.parser.ParseContext r4 = r14.getParentContext()     // Catch:{ all -> 0x034a }
            java.lang.Object r12 = r4.getObject()     // Catch:{ all -> 0x034a }
            if (r12 == 0) goto L_0x0119
            java.lang.Object r5 = r4.getObject()     // Catch:{ all -> 0x034a }
            r1 = r5
            goto L_0x0124
        L_0x0119:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r12 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x034a }
            r12.<init>(r4, r3)     // Catch:{ all -> 0x034a }
            r8.addResolveTask(r12)     // Catch:{ all -> 0x034a }
            r8.setResolveStatus(r5)     // Catch:{ all -> 0x034a }
        L_0x0124:
            goto L_0x015d
        L_0x0125:
            java.lang.String r4 = "$"
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x034a }
            if (r4 == 0) goto L_0x0152
            r4 = r14
        L_0x012e:
            com.alibaba.fastjson.parser.ParseContext r12 = r4.getParentContext()     // Catch:{ all -> 0x034a }
            if (r12 == 0) goto L_0x013a
            com.alibaba.fastjson.parser.ParseContext r12 = r4.getParentContext()     // Catch:{ all -> 0x034a }
            r4 = r12
            goto L_0x012e
        L_0x013a:
            java.lang.Object r12 = r4.getObject()     // Catch:{ all -> 0x034a }
            if (r12 == 0) goto L_0x0146
            java.lang.Object r5 = r4.getObject()     // Catch:{ all -> 0x034a }
            r1 = r5
            goto L_0x0151
        L_0x0146:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r12 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x034a }
            r12.<init>(r4, r3)     // Catch:{ all -> 0x034a }
            r8.addResolveTask(r12)     // Catch:{ all -> 0x034a }
            r8.setResolveStatus(r5)     // Catch:{ all -> 0x034a }
        L_0x0151:
            goto L_0x015d
        L_0x0152:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r4 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x034a }
            r4.<init>(r14, r3)     // Catch:{ all -> 0x034a }
            r8.addResolveTask(r4)     // Catch:{ all -> 0x034a }
            r8.setResolveStatus(r5)     // Catch:{ all -> 0x034a }
        L_0x015d:
            r11.nextToken(r15)     // Catch:{ all -> 0x034a }
            int r3 = r11.token()     // Catch:{ all -> 0x034a }
            if (r3 != r15) goto L_0x0177
            r11.nextToken(r13)     // Catch:{ all -> 0x034a }
            r8.setContext(r14, r1, r10)     // Catch:{ all -> 0x034a }
            if (r2 == 0) goto L_0x0173
            r2.setObject(r1)
        L_0x0173:
            r8.setContext(r14)
            return r1
        L_0x0177:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x034a }
            java.lang.String r4 = "illegal ref"
            r3.<init>(r4)     // Catch:{ all -> 0x034a }
            throw r3     // Catch:{ all -> 0x034a }
        L_0x017f:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x034a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x034a }
            r4.<init>()     // Catch:{ all -> 0x034a }
            java.lang.String r5 = "illegal ref, "
            r4.append(r5)     // Catch:{ all -> 0x034a }
            int r5 = r11.token()     // Catch:{ all -> 0x034a }
            java.lang.String r5 = com.alibaba.fastjson.parser.JSONToken.name(r5)     // Catch:{ all -> 0x034a }
            r4.append(r5)     // Catch:{ all -> 0x034a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x034a }
            r3.<init>(r4)     // Catch:{ all -> 0x034a }
            throw r3     // Catch:{ all -> 0x034a }
        L_0x019e:
            java.lang.String r3 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY     // Catch:{ all -> 0x034a }
            if (r3 != r6) goto L_0x01f3
            r11.nextTokenWithColon(r4)     // Catch:{ all -> 0x034a }
            int r3 = r11.token()     // Catch:{ all -> 0x034a }
            if (r3 != r4) goto L_0x01eb
            java.lang.String r3 = r11.stringVal()     // Catch:{ all -> 0x034a }
            r11.nextToken(r13)     // Catch:{ all -> 0x034a }
            boolean r4 = r9 instanceof java.lang.Class     // Catch:{ all -> 0x034a }
            if (r4 == 0) goto L_0x01d2
            r4 = r9
            java.lang.Class r4 = (java.lang.Class) r4     // Catch:{ all -> 0x034a }
            java.lang.String r4 = r4.getName()     // Catch:{ all -> 0x034a }
            boolean r4 = r3.equals(r4)     // Catch:{ all -> 0x034a }
            if (r4 == 0) goto L_0x01d2
            int r4 = r11.token()     // Catch:{ all -> 0x034a }
            if (r4 != r15) goto L_0x00bb
            r11.nextToken()     // Catch:{ all -> 0x034a }
        L_0x01cd:
            r16 = r1
            r1 = r0
            goto L_0x0249
        L_0x01d2:
            java.lang.Class r4 = com.alibaba.fastjson.util.TypeUtils.loadClass(r3)     // Catch:{ all -> 0x034a }
            com.alibaba.fastjson.parser.ParserConfig r5 = r20.getConfig()     // Catch:{ all -> 0x034a }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r5 = r5.getDeserializer((java.lang.reflect.Type) r4)     // Catch:{ all -> 0x034a }
            java.lang.Object r12 = r5.deserialze(r8, r4, r10)     // Catch:{ all -> 0x034a }
            if (r2 == 0) goto L_0x01e7
            r2.setObject(r1)
        L_0x01e7:
            r8.setContext(r14)
            return r12
        L_0x01eb:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x034a }
            java.lang.String r4 = "syntax error"
            r3.<init>(r4)     // Catch:{ all -> 0x034a }
            throw r3     // Catch:{ all -> 0x034a }
        L_0x01f3:
            if (r1 != 0) goto L_0x0214
            if (r0 != 0) goto L_0x0214
            java.lang.Object r3 = r19.createInstance(r20, r21)     // Catch:{ all -> 0x034a }
            r1 = r3
            if (r1 != 0) goto L_0x020a
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ all -> 0x034a }
            java.util.List<com.alibaba.fastjson.parser.deserializer.FieldDeserializer> r4 = r7.fieldDeserializers     // Catch:{ all -> 0x034a }
            int r4 = r4.size()     // Catch:{ all -> 0x034a }
            r3.<init>(r4)     // Catch:{ all -> 0x034a }
            r0 = r3
        L_0x020a:
            com.alibaba.fastjson.parser.ParseContext r3 = r8.setContext(r14, r1, r10)     // Catch:{ all -> 0x034a }
            r2 = r3
            r16 = r1
            r17 = r2
            goto L_0x0218
        L_0x0214:
            r16 = r1
            r17 = r2
        L_0x0218:
            r1 = r19
            r2 = r20
            r3 = r6
            r4 = r16
            r5 = r21
            r18 = r6
            r6 = r0
            boolean r1 = r1.parseField(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0344 }
            if (r1 != 0) goto L_0x0234
            int r2 = r11.token()     // Catch:{ all -> 0x0344 }
            if (r2 != r15) goto L_0x031f
            r11.nextToken()     // Catch:{ all -> 0x0344 }
            goto L_0x0246
        L_0x0234:
            int r2 = r11.token()     // Catch:{ all -> 0x0344 }
            if (r2 != r13) goto L_0x023c
            goto L_0x031f
        L_0x023c:
            int r2 = r11.token()     // Catch:{ all -> 0x0344 }
            if (r2 != r15) goto L_0x030f
            r11.nextToken(r13)     // Catch:{ all -> 0x0344 }
        L_0x0246:
            r1 = r0
            r2 = r17
        L_0x0249:
            if (r16 != 0) goto L_0x0303
            if (r1 != 0) goto L_0x0268
            java.lang.Object r0 = r19.createInstance(r20, r21)     // Catch:{ all -> 0x02ff }
            r3 = r0
            if (r2 != 0) goto L_0x025e
            com.alibaba.fastjson.parser.ParseContext r0 = r8.setContext(r14, r3, r10)     // Catch:{ all -> 0x025a }
            r2 = r0
            goto L_0x025e
        L_0x025a:
            r0 = move-exception
            r1 = r3
            goto L_0x034d
        L_0x025e:
            if (r2 == 0) goto L_0x0264
            r2.setObject(r3)
        L_0x0264:
            r8.setContext(r14)
            return r3
        L_0x0268:
            com.alibaba.fastjson.util.DeserializeBeanInfo r0 = r7.beanInfo     // Catch:{ all -> 0x02ff }
            java.util.List r0 = r0.getFieldList()     // Catch:{ all -> 0x02ff }
            r3 = r0
            int r0 = r3.size()     // Catch:{ all -> 0x02ff }
            r4 = r0
            java.lang.Object[] r0 = new java.lang.Object[r4]     // Catch:{ all -> 0x02ff }
            r5 = r0
            r0 = 0
        L_0x0278:
            if (r0 >= r4) goto L_0x028d
            java.lang.Object r6 = r3.get(r0)     // Catch:{ all -> 0x02ff }
            com.alibaba.fastjson.util.FieldInfo r6 = (com.alibaba.fastjson.util.FieldInfo) r6     // Catch:{ all -> 0x02ff }
            java.lang.String r13 = r6.getName()     // Catch:{ all -> 0x02ff }
            java.lang.Object r13 = r1.get(r13)     // Catch:{ all -> 0x02ff }
            r5[r0] = r13     // Catch:{ all -> 0x02ff }
            int r0 = r0 + 1
            goto L_0x0278
        L_0x028d:
            com.alibaba.fastjson.util.DeserializeBeanInfo r0 = r7.beanInfo     // Catch:{ all -> 0x02ff }
            java.lang.reflect.Constructor r0 = r0.getCreatorConstructor()     // Catch:{ all -> 0x02ff }
            if (r0 == 0) goto L_0x02c6
            com.alibaba.fastjson.util.DeserializeBeanInfo r0 = r7.beanInfo     // Catch:{ Exception -> 0x02a4 }
            java.lang.reflect.Constructor r0 = r0.getCreatorConstructor()     // Catch:{ Exception -> 0x02a4 }
            java.lang.Object r0 = r0.newInstance(r5)     // Catch:{ Exception -> 0x02a4 }
            r16 = r0
            r0 = r16
            goto L_0x0305
        L_0x02a4:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x02ff }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ff }
            r12.<init>()     // Catch:{ all -> 0x02ff }
            java.lang.String r13 = "create instance error, "
            r12.append(r13)     // Catch:{ all -> 0x02ff }
            com.alibaba.fastjson.util.DeserializeBeanInfo r13 = r7.beanInfo     // Catch:{ all -> 0x02ff }
            java.lang.reflect.Constructor r13 = r13.getCreatorConstructor()     // Catch:{ all -> 0x02ff }
            java.lang.String r13 = r13.toGenericString()     // Catch:{ all -> 0x02ff }
            r12.append(r13)     // Catch:{ all -> 0x02ff }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x02ff }
            r6.<init>(r12, r0)     // Catch:{ all -> 0x02ff }
            throw r6     // Catch:{ all -> 0x02ff }
        L_0x02c6:
            com.alibaba.fastjson.util.DeserializeBeanInfo r0 = r7.beanInfo     // Catch:{ all -> 0x02ff }
            java.lang.reflect.Method r0 = r0.getFactoryMethod()     // Catch:{ all -> 0x02ff }
            if (r0 == 0) goto L_0x0303
            com.alibaba.fastjson.util.DeserializeBeanInfo r0 = r7.beanInfo     // Catch:{ Exception -> 0x02dd }
            java.lang.reflect.Method r0 = r0.getFactoryMethod()     // Catch:{ Exception -> 0x02dd }
            java.lang.Object r0 = r0.invoke(r12, r5)     // Catch:{ Exception -> 0x02dd }
            r16 = r0
            r0 = r16
            goto L_0x0305
        L_0x02dd:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x02ff }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ff }
            r12.<init>()     // Catch:{ all -> 0x02ff }
            java.lang.String r13 = "create factory method error, "
            r12.append(r13)     // Catch:{ all -> 0x02ff }
            com.alibaba.fastjson.util.DeserializeBeanInfo r13 = r7.beanInfo     // Catch:{ all -> 0x02ff }
            java.lang.reflect.Method r13 = r13.getFactoryMethod()     // Catch:{ all -> 0x02ff }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x02ff }
            r12.append(r13)     // Catch:{ all -> 0x02ff }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x02ff }
            r6.<init>(r12, r0)     // Catch:{ all -> 0x02ff }
            throw r6     // Catch:{ all -> 0x02ff }
        L_0x02ff:
            r0 = move-exception
            r1 = r16
            goto L_0x034d
        L_0x0303:
            r0 = r16
        L_0x0305:
            if (r2 == 0) goto L_0x030b
            r2.setObject(r0)
        L_0x030b:
            r8.setContext(r14)
            return r0
        L_0x030f:
            int r2 = r11.token()     // Catch:{ all -> 0x0344 }
            r3 = 18
            if (r2 == r3) goto L_0x0325
            int r2 = r11.token()     // Catch:{ all -> 0x0344 }
            r3 = 1
            if (r2 == r3) goto L_0x0325
        L_0x031f:
            r1 = r16
            r2 = r17
            goto L_0x00bb
        L_0x0325:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0344 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0344 }
            r3.<init>()     // Catch:{ all -> 0x0344 }
            java.lang.String r4 = "syntax error, unexpect token "
            r3.append(r4)     // Catch:{ all -> 0x0344 }
            int r4 = r11.token()     // Catch:{ all -> 0x0344 }
            java.lang.String r4 = com.alibaba.fastjson.parser.JSONToken.name(r4)     // Catch:{ all -> 0x0344 }
            r3.append(r4)     // Catch:{ all -> 0x0344 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0344 }
            r2.<init>(r3)     // Catch:{ all -> 0x0344 }
            throw r2     // Catch:{ all -> 0x0344 }
        L_0x0344:
            r0 = move-exception
            r1 = r16
            r2 = r17
            goto L_0x034d
        L_0x034a:
            r0 = move-exception
            goto L_0x034d
        L_0x034c:
            r0 = move-exception
        L_0x034d:
            if (r2 == 0) goto L_0x0352
            r2.setObject(r1)
        L_0x0352:
            r8.setContext(r14)
            throw r0
        L_0x0356:
            java.lang.Object r0 = r20.parse()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object, java.lang.Object):java.lang.Object");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: com.alibaba.fastjson.parser.deserializer.FieldDeserializer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean parseField(com.alibaba.fastjson.parser.DefaultJSONParser r6, java.lang.String r7, java.lang.Object r8, java.lang.reflect.Type r9, java.util.Map<java.lang.String, java.lang.Object> r10) {
        /*
            r5 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r6.getLexer()
            java.util.Map<java.lang.String, com.alibaba.fastjson.parser.deserializer.FieldDeserializer> r1 = r5.feildDeserializerMap
            java.lang.Object r1 = r1.get(r7)
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer r1 = (com.alibaba.fastjson.parser.deserializer.FieldDeserializer) r1
            if (r1 != 0) goto L_0x0039
            java.util.Map<java.lang.String, com.alibaba.fastjson.parser.deserializer.FieldDeserializer> r2 = r5.feildDeserializerMap
            java.util.Set r2 = r2.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0018:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0039
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.lang.Object r4 = r3.getKey()
            java.lang.String r4 = (java.lang.String) r4
            boolean r4 = r4.equalsIgnoreCase(r7)
            if (r4 == 0) goto L_0x0038
            java.lang.Object r4 = r3.getValue()
            r1 = r4
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer r1 = (com.alibaba.fastjson.parser.deserializer.FieldDeserializer) r1
            goto L_0x0039
        L_0x0038:
            goto L_0x0018
        L_0x0039:
            if (r1 != 0) goto L_0x0040
            r5.parseExtra(r6, r8, r7)
            r2 = 0
            return r2
        L_0x0040:
            int r2 = r1.getFastMatchToken()
            r0.nextTokenWithColon(r2)
            r1.parseField(r6, r8, r9, r10)
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer.parseField(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.String, java.lang.Object, java.lang.reflect.Type, java.util.Map):boolean");
    }

    /* access modifiers changed from: package-private */
    public void parseExtra(DefaultJSONParser parser, Object object, String key) {
        Object value;
        JSONLexer lexer = parser.getLexer();
        if (lexer.isEnabled(Feature.IgnoreNotMatch)) {
            lexer.nextTokenWithColon();
            Type type = FilterUtils.getExtratype(parser, object, key);
            if (type == null) {
                value = parser.parse();
            } else {
                value = parser.parseObject(type);
            }
            FilterUtils.processExtra(parser, object, key, value);
            return;
        }
        throw new JSONException("setter not found, class " + this.clazz.getName() + ", property " + key);
    }

    public int getFastMatchToken() {
        return 12;
    }

    public final boolean isSupportArrayToBean(JSONLexer lexer) {
        return Feature.isEnabled(this.beanInfo.getParserFeatures(), Feature.SupportArrayToBean) || lexer.isEnabled(Feature.SupportArrayToBean);
    }
}
