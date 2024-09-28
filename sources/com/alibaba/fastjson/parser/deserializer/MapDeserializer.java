package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParseContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapDeserializer implements ObjectDeserializer {
    public static final MapDeserializer instance = new MapDeserializer();

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 8) {
            lexer.nextToken(16);
            return null;
        }
        Map<Object, Object> map = createMap(type);
        ParseContext context = parser.getContext();
        try {
            parser.setContext(context, map, fieldName);
            return deserialze(parser, type, fieldName, map);
        } finally {
            parser.setContext(context);
        }
    }

    /* access modifiers changed from: protected */
    public Object deserialze(DefaultJSONParser parser, Type type, Object fieldName, Map map) {
        if (!(type instanceof ParameterizedType)) {
            return parser.parseObject(map, fieldName);
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type keyType = parameterizedType.getActualTypeArguments()[0];
        Type valueType = parameterizedType.getActualTypeArguments()[1];
        if (String.class == keyType) {
            return parseMap(parser, map, valueType, fieldName);
        }
        return parseMap(parser, map, keyType, valueType, fieldName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
        r4 = r10.getConfig().getDeserializer((java.lang.reflect.Type) r5);
        r0.nextToken(16);
        r10.setResolveStatus(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0118, code lost:
        if (r1 == null) goto L_0x0121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x011c, code lost:
        if ((r13 instanceof java.lang.Integer) != false) goto L_0x0121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x011e, code lost:
        r10.popContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0121, code lost:
        r6 = (java.util.Map) r4.deserialze(r10, r5, r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0127, code lost:
        r10.setContext(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x012a, code lost:
        return r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Map parseMap(com.alibaba.fastjson.parser.DefaultJSONParser r10, java.util.Map<java.lang.String, java.lang.Object> r11, java.lang.reflect.Type r12, java.lang.Object r13) {
        /*
            com.alibaba.fastjson.parser.JSONLexer r0 = r10.getLexer()
            int r1 = r0.token()
            r2 = 12
            if (r1 != r2) goto L_0x0192
            com.alibaba.fastjson.parser.ParseContext r1 = r10.getContext()
        L_0x0010:
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r2 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            com.alibaba.fastjson.parser.Feature r3 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x018d }
            boolean r3 = r10.isEnabled(r3)     // Catch:{ all -> 0x018d }
            if (r3 == 0) goto L_0x002f
        L_0x001f:
            r3 = 44
            if (r2 != r3) goto L_0x002f
            r0.next()     // Catch:{ all -> 0x018d }
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r3 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            r2 = r3
            goto L_0x001f
        L_0x002f:
            java.lang.String r3 = "expect ':' at "
            r4 = 58
            r5 = 34
            r6 = 16
            if (r2 != r5) goto L_0x0066
            com.alibaba.fastjson.parser.SymbolTable r7 = r10.getSymbolTable()     // Catch:{ all -> 0x018d }
            java.lang.String r7 = r0.scanSymbol(r7, r5)     // Catch:{ all -> 0x018d }
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r8 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            r2 = r8
            if (r2 != r4) goto L_0x004d
            goto L_0x00d2
        L_0x004d:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x018d }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x018d }
            r5.<init>()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            int r3 = r0.pos()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            java.lang.String r3 = r5.toString()     // Catch:{ all -> 0x018d }
            r4.<init>(r3)     // Catch:{ all -> 0x018d }
            throw r4     // Catch:{ all -> 0x018d }
        L_0x0066:
            r7 = 125(0x7d, float:1.75E-43)
            if (r2 != r7) goto L_0x0078
            r0.next()     // Catch:{ all -> 0x018d }
            r0.resetStringPosition()     // Catch:{ all -> 0x018d }
            r0.nextToken(r6)     // Catch:{ all -> 0x018d }
            r10.setContext(r1)
            return r11
        L_0x0078:
            java.lang.String r7 = "syntax error"
            r8 = 39
            if (r2 != r8) goto L_0x00b8
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.AllowSingleQuotes     // Catch:{ all -> 0x018d }
            boolean r9 = r10.isEnabled(r9)     // Catch:{ all -> 0x018d }
            if (r9 == 0) goto L_0x00b2
            com.alibaba.fastjson.parser.SymbolTable r7 = r10.getSymbolTable()     // Catch:{ all -> 0x018d }
            java.lang.String r7 = r0.scanSymbol(r7, r8)     // Catch:{ all -> 0x018d }
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r8 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            r2 = r8
            if (r2 != r4) goto L_0x0099
            goto L_0x00d2
        L_0x0099:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x018d }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x018d }
            r5.<init>()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            int r3 = r0.pos()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            java.lang.String r3 = r5.toString()     // Catch:{ all -> 0x018d }
            r4.<init>(r3)     // Catch:{ all -> 0x018d }
            throw r4     // Catch:{ all -> 0x018d }
        L_0x00b2:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x018d }
            r3.<init>(r7)     // Catch:{ all -> 0x018d }
            throw r3     // Catch:{ all -> 0x018d }
        L_0x00b8:
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowUnQuotedFieldNames     // Catch:{ all -> 0x018d }
            boolean r8 = r10.isEnabled(r8)     // Catch:{ all -> 0x018d }
            if (r8 == 0) goto L_0x0187
            com.alibaba.fastjson.parser.SymbolTable r7 = r10.getSymbolTable()     // Catch:{ all -> 0x018d }
            java.lang.String r7 = r0.scanSymbolUnQuoted(r7)     // Catch:{ all -> 0x018d }
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r8 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            r2 = r8
            if (r2 != r4) goto L_0x0166
        L_0x00d2:
            r0.next()     // Catch:{ all -> 0x018d }
            r0.skipWhitespace()     // Catch:{ all -> 0x018d }
            char r3 = r0.getCurrent()     // Catch:{ all -> 0x018d }
            r2 = r3
            r0.resetStringPosition()     // Catch:{ all -> 0x018d }
            java.lang.String r3 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY     // Catch:{ all -> 0x018d }
            r4 = 13
            if (r7 != r3) goto L_0x012b
            com.alibaba.fastjson.parser.SymbolTable r3 = r10.getSymbolTable()     // Catch:{ all -> 0x018d }
            java.lang.String r3 = r0.scanSymbol(r3, r5)     // Catch:{ all -> 0x018d }
            java.lang.Class r5 = com.alibaba.fastjson.util.TypeUtils.loadClass(r3)     // Catch:{ all -> 0x018d }
            java.lang.Class r8 = r11.getClass()     // Catch:{ all -> 0x018d }
            if (r5 != r8) goto L_0x0109
            r0.nextToken(r6)     // Catch:{ all -> 0x018d }
            int r8 = r0.token()     // Catch:{ all -> 0x018d }
            if (r8 != r4) goto L_0x0010
            r0.nextToken(r6)     // Catch:{ all -> 0x018d }
            r10.setContext(r1)
            return r11
        L_0x0109:
            com.alibaba.fastjson.parser.ParserConfig r4 = r10.getConfig()     // Catch:{ all -> 0x018d }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r4 = r4.getDeserializer((java.lang.reflect.Type) r5)     // Catch:{ all -> 0x018d }
            r0.nextToken(r6)     // Catch:{ all -> 0x018d }
            r6 = 2
            r10.setResolveStatus(r6)     // Catch:{ all -> 0x018d }
            if (r1 == 0) goto L_0x0121
            boolean r6 = r13 instanceof java.lang.Integer     // Catch:{ all -> 0x018d }
            if (r6 != 0) goto L_0x0121
            r10.popContext()     // Catch:{ all -> 0x018d }
        L_0x0121:
            java.lang.Object r6 = r4.deserialze(r10, r5, r13)     // Catch:{ all -> 0x018d }
            java.util.Map r6 = (java.util.Map) r6     // Catch:{ all -> 0x018d }
            r10.setContext(r1)
            return r6
        L_0x012b:
            r0.nextToken()     // Catch:{ all -> 0x018d }
            int r3 = r0.token()     // Catch:{ all -> 0x018d }
            r5 = 8
            if (r3 != r5) goto L_0x013b
            r3 = 0
            r0.nextToken()     // Catch:{ all -> 0x018d }
            goto L_0x013f
        L_0x013b:
            java.lang.Object r3 = r10.parseObject((java.lang.reflect.Type) r12)     // Catch:{ all -> 0x018d }
        L_0x013f:
            r11.put(r7, r3)     // Catch:{ all -> 0x018d }
            r10.checkMapResolve(r11, r7)     // Catch:{ all -> 0x018d }
            r10.setContext(r1, r3, r7)     // Catch:{ all -> 0x018d }
            int r5 = r0.token()     // Catch:{ all -> 0x018d }
            r6 = 20
            if (r5 == r6) goto L_0x0161
            r6 = 15
            if (r5 != r6) goto L_0x0155
            goto L_0x0161
        L_0x0155:
            if (r5 != r4) goto L_0x015f
            r0.nextToken()     // Catch:{ all -> 0x018d }
            r10.setContext(r1)
            return r11
        L_0x015f:
            goto L_0x0010
        L_0x0161:
            r10.setContext(r1)
            return r11
        L_0x0166:
            com.alibaba.fastjson.JSONException r4 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x018d }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x018d }
            r5.<init>()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            int r3 = r0.pos()     // Catch:{ all -> 0x018d }
            r5.append(r3)     // Catch:{ all -> 0x018d }
            java.lang.String r3 = ", actual "
            r5.append(r3)     // Catch:{ all -> 0x018d }
            r5.append(r2)     // Catch:{ all -> 0x018d }
            java.lang.String r3 = r5.toString()     // Catch:{ all -> 0x018d }
            r4.<init>(r3)     // Catch:{ all -> 0x018d }
            throw r4     // Catch:{ all -> 0x018d }
        L_0x0187:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x018d }
            r3.<init>(r7)     // Catch:{ all -> 0x018d }
            throw r3     // Catch:{ all -> 0x018d }
        L_0x018d:
            r2 = move-exception
            r10.setContext(r1)
            throw r2
        L_0x0192:
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "syntax error, expect {, actual "
            r2.append(r3)
            int r3 = r0.token()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.MapDeserializer.parseMap(com.alibaba.fastjson.parser.DefaultJSONParser, java.util.Map, java.lang.reflect.Type, java.lang.Object):java.util.Map");
    }

    public static Object parseMap(DefaultJSONParser parser, Map<Object, Object> map, Type keyType, Type valueType, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 12 || lexer.token() == 16) {
            ObjectDeserializer keyDeserializer = parser.getConfig().getDeserializer(keyType);
            ObjectDeserializer valueDeserializer = parser.getConfig().getDeserializer(valueType);
            lexer.nextToken(keyDeserializer.getFastMatchToken());
            ParseContext context = parser.getContext();
            while (lexer.token() != 13) {
                try {
                    if (lexer.token() != 4 || !lexer.isRef()) {
                        if (map.size() == 0 && lexer.token() == 4 && JSON.DEFAULT_TYPE_KEY.equals(lexer.stringVal())) {
                            lexer.nextTokenWithColon(4);
                            lexer.nextToken(16);
                            if (lexer.token() == 13) {
                                lexer.nextToken();
                                return map;
                            }
                            lexer.nextToken(keyDeserializer.getFastMatchToken());
                        }
                        Object key = keyDeserializer.deserialze(parser, keyType, (Object) null);
                        if (lexer.token() == 17) {
                            lexer.nextToken(valueDeserializer.getFastMatchToken());
                            map.put(key, valueDeserializer.deserialze(parser, valueType, key));
                            if (lexer.token() == 16) {
                                lexer.nextToken(keyDeserializer.getFastMatchToken());
                            }
                        } else {
                            throw new JSONException("syntax error, expect :, actual " + lexer.token());
                        }
                    } else {
                        Object object = null;
                        lexer.nextTokenWithColon(4);
                        if (lexer.token() == 4) {
                            String ref = lexer.stringVal();
                            if ("..".equals(ref)) {
                                object = context.getParentContext().getObject();
                            } else if ("$".equals(ref)) {
                                ParseContext rootContext = context;
                                while (rootContext.getParentContext() != null) {
                                    rootContext = rootContext.getParentContext();
                                }
                                object = rootContext.getObject();
                            } else {
                                parser.addResolveTask(new DefaultJSONParser.ResolveTask(context, ref));
                                parser.setResolveStatus(1);
                            }
                            lexer.nextToken(13);
                            if (lexer.token() == 13) {
                                lexer.nextToken(16);
                                parser.setContext(context);
                                return object;
                            }
                            throw new JSONException("illegal ref");
                        }
                        throw new JSONException("illegal ref, " + JSONToken.name(lexer.token()));
                    }
                } finally {
                    parser.setContext(context);
                }
            }
            lexer.nextToken(16);
            parser.setContext(context);
            return map;
        }
        throw new JSONException("syntax error, expect {, actual " + lexer.tokenName());
    }

    /* access modifiers changed from: protected */
    public Map<Object, Object> createMap(Type type) {
        if (type == Properties.class) {
            return new Properties();
        }
        if (type == Hashtable.class) {
            return new Hashtable();
        }
        if (type == IdentityHashMap.class) {
            return new IdentityHashMap();
        }
        if (type == SortedMap.class || type == TreeMap.class) {
            return new TreeMap();
        }
        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap();
        }
        if (type == Map.class || type == HashMap.class) {
            return new HashMap();
        }
        if (type == LinkedHashMap.class) {
            return new LinkedHashMap();
        }
        if (type instanceof ParameterizedType) {
            return createMap(((ParameterizedType) type).getRawType());
        }
        Class<?> clazz = (Class) type;
        if (!clazz.isInterface()) {
            try {
                return (Map) clazz.newInstance();
            } catch (Exception e) {
                throw new JSONException("unsupport type " + type, e);
            }
        } else {
            throw new JSONException("unsupport type " + type);
        }
    }

    public int getFastMatchToken() {
        return 12;
    }
}
