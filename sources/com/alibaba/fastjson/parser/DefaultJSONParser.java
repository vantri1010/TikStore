package com.alibaba.fastjson.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.deserializer.CollectionResolveFieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.ExtraTypeProvider;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ListResolveFieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.MapResolveFieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.IntegerCodec;
import com.alibaba.fastjson.serializer.StringCodec;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultJSONParser extends AbstractJSONParser implements Closeable {
    public static final int NONE = 0;
    public static final int NeedToResolve = 1;
    public static final int TypeNameRedirect = 2;
    private static final Set<Class<?>> primitiveClasses;
    protected ParserConfig config;
    protected ParseContext context;
    private ParseContext[] contextArray;
    private int contextArrayIndex;
    private DateFormat dateFormat;
    private String dateFormatPattern;
    private List<ExtraProcessor> extraProcessors;
    private List<ExtraTypeProvider> extraTypeProviders;
    protected final Object input;
    protected final JSONLexer lexer;
    private int resolveStatus;
    private List<ResolveTask> resolveTaskList;
    protected final SymbolTable symbolTable;

    static {
        HashSet hashSet = new HashSet();
        primitiveClasses = hashSet;
        hashSet.add(Boolean.TYPE);
        primitiveClasses.add(Byte.TYPE);
        primitiveClasses.add(Short.TYPE);
        primitiveClasses.add(Integer.TYPE);
        primitiveClasses.add(Long.TYPE);
        primitiveClasses.add(Float.TYPE);
        primitiveClasses.add(Double.TYPE);
        primitiveClasses.add(Boolean.class);
        primitiveClasses.add(Byte.class);
        primitiveClasses.add(Short.class);
        primitiveClasses.add(Integer.class);
        primitiveClasses.add(Long.class);
        primitiveClasses.add(Float.class);
        primitiveClasses.add(Double.class);
        primitiveClasses.add(BigInteger.class);
        primitiveClasses.add(BigDecimal.class);
        primitiveClasses.add(String.class);
    }

    public String getDateFomartPattern() {
        return this.dateFormatPattern;
    }

    public DateFormat getDateFormat() {
        if (this.dateFormat == null) {
            this.dateFormat = new SimpleDateFormat(this.dateFormatPattern);
        }
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat2) {
        this.dateFormatPattern = dateFormat2;
        this.dateFormat = null;
    }

    public void setDateFomrat(DateFormat dateFormat2) {
        this.dateFormat = dateFormat2;
    }

    public DefaultJSONParser(String input2) {
        this(input2, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
    }

    public DefaultJSONParser(String input2, ParserConfig config2) {
        this((Object) input2, (JSONLexer) new JSONScanner(input2, JSON.DEFAULT_PARSER_FEATURE), config2);
    }

    public DefaultJSONParser(String input2, ParserConfig config2, int features) {
        this((Object) input2, (JSONLexer) new JSONScanner(input2, features), config2);
    }

    public DefaultJSONParser(char[] input2, int length, ParserConfig config2, int features) {
        this((Object) input2, (JSONLexer) new JSONScanner(input2, length, features), config2);
    }

    public DefaultJSONParser(JSONLexer lexer2) {
        this(lexer2, ParserConfig.getGlobalInstance());
    }

    public DefaultJSONParser(JSONLexer lexer2, ParserConfig config2) {
        this((Object) null, lexer2, config2);
    }

    public DefaultJSONParser(Object input2, JSONLexer lexer2, ParserConfig config2) {
        this.dateFormatPattern = JSON.DEFFAULT_DATE_FORMAT;
        this.contextArray = new ParseContext[8];
        this.contextArrayIndex = 0;
        this.resolveStatus = 0;
        this.extraTypeProviders = null;
        this.extraProcessors = null;
        this.lexer = lexer2;
        this.input = input2;
        this.config = config2;
        this.symbolTable = config2.getSymbolTable();
        lexer2.nextToken(12);
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public String getInput() {
        Object obj = this.input;
        if (obj instanceof char[]) {
            return new String((char[]) this.input);
        }
        return obj.toString();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0205, code lost:
        setResolveStatus(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x020b, code lost:
        if (r1.context == null) goto L_0x0214;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x020f, code lost:
        if ((r3 instanceof java.lang.Integer) != false) goto L_0x0214;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0211, code lost:
        popContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0214, code lost:
        r11 = r1.config.getDeserializer((java.lang.reflect.Type) r10).deserialze(r1, r10, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x021e, code lost:
        setContext(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0221, code lost:
        return r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:?, code lost:
        r4.nextToken(4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x022e, code lost:
        if (r4.token() != 4) goto L_0x02de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0230, code lost:
        r0 = r4.stringVal();
        r4.nextToken(13);
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x023e, code lost:
        if ("@".equals(r0) == false) goto L_0x0269;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x0244, code lost:
        if (getContext() == null) goto L_0x02c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0246, code lost:
        r10 = getContext();
        r13 = r10.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0250, code lost:
        if ((r13 instanceof java.lang.Object[]) != false) goto L_0x0267;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0254, code lost:
        if ((r13 instanceof java.util.Collection) == false) goto L_0x0257;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x025b, code lost:
        if (r10.getParentContext() == null) goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x025d, code lost:
        r9 = r10.getParentContext().getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x0267, code lost:
        r9 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x026f, code lost:
        if ("..".equals(r0) == false) goto L_0x028e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0271, code lost:
        r10 = r5.getParentContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0279, code lost:
        if (r10.getObject() == null) goto L_0x0281;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x027b, code lost:
        r9 = r10.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0281, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r10, r0));
        setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0294, code lost:
        if ("$".equals(r0) == false) goto L_0x02bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0296, code lost:
        r10 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x029b, code lost:
        if (r10.getParentContext() == null) goto L_0x02a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x029d, code lost:
        r10 = r10.getParentContext();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x02a7, code lost:
        if (r10.getObject() == null) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x02a9, code lost:
        r9 = r10.getObject();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x02af, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r10, r0));
        setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x02bc, code lost:
        addResolveTask(new com.alibaba.fastjson.parser.DefaultJSONParser.ResolveTask(r5, r0));
        setResolveStatus(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x02cc, code lost:
        if (r4.token() != 13) goto L_0x02d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x02ce, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x02d3, code lost:
        setContext(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x02d7, code lost:
        return r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x02dd, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x02fc, code lost:
        throw new com.alibaba.fastjson.JSONException("illegal ref, " + com.alibaba.fastjson.parser.JSONToken.name(r4.token()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0384, code lost:
        if (r6 != '}') goto L_0x0397;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0386, code lost:
        r4.next();
        r4.resetStringPosition();
        r4.nextToken();
        setContext(r18, r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x0392, code lost:
        setContext(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0396, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x03b5, code lost:
        throw new com.alibaba.fastjson.JSONException("syntax error, position at " + r4.pos() + ", name " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01c4, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01cd, code lost:
        if (r4.token() != 13) goto L_0x0205;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x01cf, code lost:
        r4.nextToken(16);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01d2, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:?, code lost:
        r11 = r1.config.getDeserializer((java.lang.reflect.Type) r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01db, code lost:
        if ((r11 instanceof com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer) == false) goto L_0x01e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01dd, code lost:
        r0 = ((com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer) r11).createInstance(r1, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01e5, code lost:
        if (r0 != null) goto L_0x01f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01e9, code lost:
        if (r10 != java.lang.Cloneable.class) goto L_0x01f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01eb, code lost:
        r0 = new java.util.HashMap();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01f6, code lost:
        r0 = r10.newInstance();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01f7, code lost:
        setContext(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01fb, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object parseObject(java.util.Map r18, java.lang.Object r19) {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            r3 = r19
            com.alibaba.fastjson.parser.JSONLexer r4 = r1.lexer
            int r0 = r4.token()
            r5 = 8
            if (r0 != r5) goto L_0x0015
            r4.next()
            r0 = 0
            return r0
        L_0x0015:
            int r0 = r4.token()
            r5 = 12
            r6 = 16
            if (r0 == r5) goto L_0x0041
            int r0 = r4.token()
            if (r0 != r6) goto L_0x0026
            goto L_0x0041
        L_0x0026:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "syntax error, expect {, actual "
            r5.append(r6)
            java.lang.String r6 = r4.tokenName()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r0.<init>(r5)
            throw r0
        L_0x0041:
            com.alibaba.fastjson.parser.ParseContext r5 = r17.getContext()
            r0 = 0
            r7 = r0
        L_0x0047:
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r0 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            com.alibaba.fastjson.parser.Feature r8 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x04c9 }
            boolean r8 = r1.isEnabled(r8)     // Catch:{ all -> 0x04c9 }
            r9 = 44
            if (r8 == 0) goto L_0x0066
        L_0x0058:
            if (r0 != r9) goto L_0x0066
            r4.next()     // Catch:{ all -> 0x04c9 }
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r8 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r0 = r8
            goto L_0x0058
        L_0x0066:
            r8 = 0
            r15 = 125(0x7d, float:1.75E-43)
            java.lang.String r10 = ", name "
            java.lang.String r11 = "expect ':' at "
            r6 = 58
            r13 = 34
            java.lang.String r12 = "syntax error"
            if (r0 != r13) goto L_0x00a7
            com.alibaba.fastjson.parser.SymbolTable r14 = r1.symbolTable     // Catch:{ all -> 0x04c9 }
            java.lang.String r14 = r4.scanSymbol(r14, r13)     // Catch:{ all -> 0x04c9 }
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r16 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r0 = r16
            if (r0 != r6) goto L_0x0088
            goto L_0x0198
        L_0x0088:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            int r11 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            r9.append(r14)     // Catch:{ all -> 0x04c9 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x04c9 }
            r6.<init>(r9)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x00a7:
            if (r0 != r15) goto L_0x00b7
            r4.next()     // Catch:{ all -> 0x04c9 }
            r4.resetStringPosition()     // Catch:{ all -> 0x04c9 }
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r2
        L_0x00b7:
            r14 = 39
            if (r0 != r14) goto L_0x00f4
            com.alibaba.fastjson.parser.Feature r15 = com.alibaba.fastjson.parser.Feature.AllowSingleQuotes     // Catch:{ all -> 0x04c9 }
            boolean r15 = r1.isEnabled(r15)     // Catch:{ all -> 0x04c9 }
            if (r15 == 0) goto L_0x00ee
            com.alibaba.fastjson.parser.SymbolTable r15 = r1.symbolTable     // Catch:{ all -> 0x04c9 }
            java.lang.String r14 = r4.scanSymbol(r15, r14)     // Catch:{ all -> 0x04c9 }
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r15 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r0 = r15
            if (r0 != r6) goto L_0x00d5
            goto L_0x0198
        L_0x00d5:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            int r10 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x04c9 }
            r6.<init>(r9)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x00ee:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r6.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x00f4:
            r14 = 26
            if (r0 == r14) goto L_0x04c3
            if (r0 == r9) goto L_0x04bd
            r14 = 48
            if (r0 < r14) goto L_0x0102
            r14 = 57
            if (r0 <= r14) goto L_0x0106
        L_0x0102:
            r14 = 45
            if (r0 != r14) goto L_0x0146
        L_0x0106:
            r4.resetStringPosition()     // Catch:{ all -> 0x04c9 }
            r4.scanNumber()     // Catch:{ all -> 0x04c9 }
            int r14 = r4.token()     // Catch:{ all -> 0x04c9 }
            r15 = 2
            if (r14 != r15) goto L_0x0118
            java.lang.Number r14 = r4.integerValue()     // Catch:{ all -> 0x04c9 }
            goto L_0x011e
        L_0x0118:
            r14 = 1
            java.lang.Number r15 = r4.decimalValue(r14)     // Catch:{ all -> 0x04c9 }
            r14 = r15
        L_0x011e:
            char r15 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r0 = r15
            if (r0 != r6) goto L_0x0127
            goto L_0x0198
        L_0x0127:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            int r11 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            r9.append(r14)     // Catch:{ all -> 0x04c9 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x04c9 }
            r6.<init>(r9)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x0146:
            r14 = 123(0x7b, float:1.72E-43)
            if (r0 == r14) goto L_0x018f
            r14 = 91
            if (r0 != r14) goto L_0x014f
            goto L_0x018f
        L_0x014f:
            com.alibaba.fastjson.parser.Feature r14 = com.alibaba.fastjson.parser.Feature.AllowUnQuotedFieldNames     // Catch:{ all -> 0x04c9 }
            boolean r14 = r1.isEnabled(r14)     // Catch:{ all -> 0x04c9 }
            if (r14 == 0) goto L_0x0189
            com.alibaba.fastjson.parser.SymbolTable r14 = r1.symbolTable     // Catch:{ all -> 0x04c9 }
            java.lang.String r14 = r4.scanSymbolUnQuoted(r14)     // Catch:{ all -> 0x04c9 }
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r15 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r0 = r15
            if (r0 != r6) goto L_0x0168
            goto L_0x0198
        L_0x0168:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            r9.append(r11)     // Catch:{ all -> 0x04c9 }
            int r10 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            java.lang.String r10 = ", actual "
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            r9.append(r0)     // Catch:{ all -> 0x04c9 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x04c9 }
            r6.<init>(r9)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x0189:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r6.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x018f:
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            java.lang.Object r6 = r17.parse()     // Catch:{ all -> 0x04c9 }
            r14 = r6
            r8 = 1
        L_0x0198:
            if (r8 != 0) goto L_0x01a0
            r4.next()     // Catch:{ all -> 0x04c9 }
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
        L_0x01a0:
            char r6 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r4.resetStringPosition()     // Catch:{ all -> 0x04c9 }
            java.lang.String r0 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY     // Catch:{ all -> 0x04c9 }
            r11 = 13
            if (r14 != r0) goto L_0x0222
            com.alibaba.fastjson.parser.SymbolTable r0 = r1.symbolTable     // Catch:{ all -> 0x04c9 }
            java.lang.String r0 = r4.scanSymbol(r0, r13)     // Catch:{ all -> 0x04c9 }
            r9 = r0
            java.lang.Class r0 = com.alibaba.fastjson.util.TypeUtils.loadClass(r9)     // Catch:{ all -> 0x04c9 }
            r10 = r0
            if (r10 != 0) goto L_0x01c4
            java.lang.String r0 = com.alibaba.fastjson.JSON.DEFAULT_TYPE_KEY     // Catch:{ all -> 0x04c9 }
            r2.put(r0, r9)     // Catch:{ all -> 0x04c9 }
            r6 = 16
            goto L_0x0047
        L_0x01c4:
            r0 = 16
            r4.nextToken(r0)     // Catch:{ all -> 0x04c9 }
            int r12 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r12 != r11) goto L_0x0205
            r4.nextToken(r0)     // Catch:{ all -> 0x04c9 }
            r0 = 0
            com.alibaba.fastjson.parser.ParserConfig r11 = r1.config     // Catch:{ Exception -> 0x01fc }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r11 = r11.getDeserializer((java.lang.reflect.Type) r10)     // Catch:{ Exception -> 0x01fc }
            boolean r12 = r11 instanceof com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer     // Catch:{ Exception -> 0x01fc }
            if (r12 == 0) goto L_0x01e5
            r12 = r11
            com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer r12 = (com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer) r12     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r12 = r12.createInstance(r1, r10)     // Catch:{ Exception -> 0x01fc }
            r0 = r12
        L_0x01e5:
            if (r0 != 0) goto L_0x01f7
            java.lang.Class<java.lang.Cloneable> r12 = java.lang.Cloneable.class
            if (r10 != r12) goto L_0x01f2
            java.util.HashMap r12 = new java.util.HashMap     // Catch:{ Exception -> 0x01fc }
            r12.<init>()     // Catch:{ Exception -> 0x01fc }
            r0 = r12
            goto L_0x01f7
        L_0x01f2:
            java.lang.Object r12 = r10.newInstance()     // Catch:{ Exception -> 0x01fc }
            r0 = r12
        L_0x01f7:
            r1.setContext(r5)
            return r0
        L_0x01fc:
            r0 = move-exception
            com.alibaba.fastjson.JSONException r11 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.String r12 = "create instance error"
            r11.<init>(r12, r0)     // Catch:{ all -> 0x04c9 }
            throw r11     // Catch:{ all -> 0x04c9 }
        L_0x0205:
            r0 = 2
            r1.setResolveStatus(r0)     // Catch:{ all -> 0x04c9 }
            com.alibaba.fastjson.parser.ParseContext r0 = r1.context     // Catch:{ all -> 0x04c9 }
            if (r0 == 0) goto L_0x0214
            boolean r0 = r3 instanceof java.lang.Integer     // Catch:{ all -> 0x04c9 }
            if (r0 != 0) goto L_0x0214
            r17.popContext()     // Catch:{ all -> 0x04c9 }
        L_0x0214:
            com.alibaba.fastjson.parser.ParserConfig r0 = r1.config     // Catch:{ all -> 0x04c9 }
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r0 = r0.getDeserializer((java.lang.reflect.Type) r10)     // Catch:{ all -> 0x04c9 }
            java.lang.Object r11 = r0.deserialze(r1, r10, r3)     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r11
        L_0x0222:
            java.lang.String r0 = "$ref"
            if (r14 != r0) goto L_0x02fd
            r0 = 4
            r4.nextToken(r0)     // Catch:{ all -> 0x04c9 }
            int r9 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r9 != r0) goto L_0x02de
            java.lang.String r0 = r4.stringVal()     // Catch:{ all -> 0x04c9 }
            r4.nextToken(r11)     // Catch:{ all -> 0x04c9 }
            r9 = 0
            java.lang.String r10 = "@"
            boolean r10 = r10.equals(r0)     // Catch:{ all -> 0x04c9 }
            if (r10 == 0) goto L_0x0269
            com.alibaba.fastjson.parser.ParseContext r10 = r17.getContext()     // Catch:{ all -> 0x04c9 }
            if (r10 == 0) goto L_0x02c8
            com.alibaba.fastjson.parser.ParseContext r10 = r17.getContext()     // Catch:{ all -> 0x04c9 }
            java.lang.Object r13 = r10.getObject()     // Catch:{ all -> 0x04c9 }
            boolean r15 = r13 instanceof java.lang.Object[]     // Catch:{ all -> 0x04c9 }
            if (r15 != 0) goto L_0x0267
            boolean r15 = r13 instanceof java.util.Collection     // Catch:{ all -> 0x04c9 }
            if (r15 == 0) goto L_0x0257
            goto L_0x0267
        L_0x0257:
            com.alibaba.fastjson.parser.ParseContext r15 = r10.getParentContext()     // Catch:{ all -> 0x04c9 }
            if (r15 == 0) goto L_0x0268
            com.alibaba.fastjson.parser.ParseContext r15 = r10.getParentContext()     // Catch:{ all -> 0x04c9 }
            java.lang.Object r15 = r15.getObject()     // Catch:{ all -> 0x04c9 }
            r9 = r15
            goto L_0x0268
        L_0x0267:
            r9 = r13
        L_0x0268:
            goto L_0x02c8
        L_0x0269:
            java.lang.String r10 = ".."
            boolean r10 = r10.equals(r0)     // Catch:{ all -> 0x04c9 }
            if (r10 == 0) goto L_0x028e
            com.alibaba.fastjson.parser.ParseContext r10 = r5.getParentContext()     // Catch:{ all -> 0x04c9 }
            java.lang.Object r13 = r10.getObject()     // Catch:{ all -> 0x04c9 }
            if (r13 == 0) goto L_0x0281
            java.lang.Object r13 = r10.getObject()     // Catch:{ all -> 0x04c9 }
            r9 = r13
            goto L_0x028d
        L_0x0281:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r13 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x04c9 }
            r13.<init>(r10, r0)     // Catch:{ all -> 0x04c9 }
            r1.addResolveTask(r13)     // Catch:{ all -> 0x04c9 }
            r13 = 1
            r1.setResolveStatus(r13)     // Catch:{ all -> 0x04c9 }
        L_0x028d:
            goto L_0x02c8
        L_0x028e:
            java.lang.String r10 = "$"
            boolean r10 = r10.equals(r0)     // Catch:{ all -> 0x04c9 }
            if (r10 == 0) goto L_0x02bc
            r10 = r5
        L_0x0297:
            com.alibaba.fastjson.parser.ParseContext r13 = r10.getParentContext()     // Catch:{ all -> 0x04c9 }
            if (r13 == 0) goto L_0x02a3
            com.alibaba.fastjson.parser.ParseContext r13 = r10.getParentContext()     // Catch:{ all -> 0x04c9 }
            r10 = r13
            goto L_0x0297
        L_0x02a3:
            java.lang.Object r13 = r10.getObject()     // Catch:{ all -> 0x04c9 }
            if (r13 == 0) goto L_0x02af
            java.lang.Object r13 = r10.getObject()     // Catch:{ all -> 0x04c9 }
            r9 = r13
            goto L_0x02bb
        L_0x02af:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r13 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x04c9 }
            r13.<init>(r10, r0)     // Catch:{ all -> 0x04c9 }
            r1.addResolveTask(r13)     // Catch:{ all -> 0x04c9 }
            r13 = 1
            r1.setResolveStatus(r13)     // Catch:{ all -> 0x04c9 }
        L_0x02bb:
            goto L_0x02c8
        L_0x02bc:
            com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask r10 = new com.alibaba.fastjson.parser.DefaultJSONParser$ResolveTask     // Catch:{ all -> 0x04c9 }
            r10.<init>(r5, r0)     // Catch:{ all -> 0x04c9 }
            r1.addResolveTask(r10)     // Catch:{ all -> 0x04c9 }
            r15 = 1
            r1.setResolveStatus(r15)     // Catch:{ all -> 0x04c9 }
        L_0x02c8:
            int r10 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r10 != r11) goto L_0x02d8
            r10 = 16
            r4.nextToken(r10)     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r9
        L_0x02d8:
            com.alibaba.fastjson.JSONException r10 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r10.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r10     // Catch:{ all -> 0x04c9 }
        L_0x02de:
            com.alibaba.fastjson.JSONException r0 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            java.lang.String r10 = "illegal ref, "
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            int r10 = r4.token()     // Catch:{ all -> 0x04c9 }
            java.lang.String r10 = com.alibaba.fastjson.parser.JSONToken.name(r10)     // Catch:{ all -> 0x04c9 }
            r9.append(r10)     // Catch:{ all -> 0x04c9 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x04c9 }
            r0.<init>(r9)     // Catch:{ all -> 0x04c9 }
            throw r0     // Catch:{ all -> 0x04c9 }
        L_0x02fd:
            r15 = 1
            if (r7 != 0) goto L_0x0305
            r17.setContext(r18, r19)     // Catch:{ all -> 0x04c9 }
            r0 = 1
            r7 = r0
        L_0x0305:
            java.lang.Class r0 = r18.getClass()     // Catch:{ all -> 0x04c9 }
            java.lang.Class<com.alibaba.fastjson.JSONObject> r15 = com.alibaba.fastjson.JSONObject.class
            if (r0 != r15) goto L_0x0317
            if (r14 != 0) goto L_0x0312
            java.lang.String r0 = "null"
            goto L_0x0316
        L_0x0312:
            java.lang.String r0 = r14.toString()     // Catch:{ all -> 0x04c9 }
        L_0x0316:
            r14 = r0
        L_0x0317:
            java.lang.String r0 = "syntax error, position at "
            if (r6 != r13) goto L_0x0347
            r4.scanString()     // Catch:{ all -> 0x04c9 }
            java.lang.String r11 = r4.stringVal()     // Catch:{ all -> 0x04c9 }
            r12 = r11
            com.alibaba.fastjson.parser.Feature r13 = com.alibaba.fastjson.parser.Feature.AllowISO8601DateFormat     // Catch:{ all -> 0x04c9 }
            boolean r13 = r4.isEnabled(r13)     // Catch:{ all -> 0x04c9 }
            if (r13 == 0) goto L_0x0342
            com.alibaba.fastjson.parser.JSONScanner r13 = new com.alibaba.fastjson.parser.JSONScanner     // Catch:{ all -> 0x04c9 }
            r13.<init>(r11)     // Catch:{ all -> 0x04c9 }
            boolean r15 = r13.scanISO8601DateIfMatch()     // Catch:{ all -> 0x04c9 }
            if (r15 == 0) goto L_0x033f
            java.util.Calendar r15 = r13.getCalendar()     // Catch:{ all -> 0x04c9 }
            java.util.Date r15 = r15.getTime()     // Catch:{ all -> 0x04c9 }
            r12 = r15
        L_0x033f:
            r13.close()     // Catch:{ all -> 0x04c9 }
        L_0x0342:
            r2.put(r14, r12)     // Catch:{ all -> 0x04c9 }
            goto L_0x0371
        L_0x0347:
            r13 = 48
            if (r6 < r13) goto L_0x034f
            r13 = 57
            if (r6 <= r13) goto L_0x0353
        L_0x034f:
            r13 = 45
            if (r6 != r13) goto L_0x03b6
        L_0x0353:
            r4.scanNumber()     // Catch:{ all -> 0x04c9 }
            int r11 = r4.token()     // Catch:{ all -> 0x04c9 }
            r12 = 2
            if (r11 != r12) goto L_0x0363
            java.lang.Number r11 = r4.integerValue()     // Catch:{ all -> 0x04c9 }
            r12 = r11
            goto L_0x036e
        L_0x0363:
            com.alibaba.fastjson.parser.Feature r11 = com.alibaba.fastjson.parser.Feature.UseBigDecimal     // Catch:{ all -> 0x04c9 }
            boolean r11 = r1.isEnabled(r11)     // Catch:{ all -> 0x04c9 }
            java.lang.Number r11 = r4.decimalValue(r11)     // Catch:{ all -> 0x04c9 }
            r12 = r11
        L_0x036e:
            r2.put(r14, r12)     // Catch:{ all -> 0x04c9 }
        L_0x0371:
            r4.skipWhitespace()     // Catch:{ all -> 0x04c9 }
            char r11 = r4.getCurrent()     // Catch:{ all -> 0x04c9 }
            r6 = r11
            if (r6 != r9) goto L_0x0382
            r4.next()     // Catch:{ all -> 0x04c9 }
            r12 = 16
            goto L_0x049a
        L_0x0382:
            r9 = 125(0x7d, float:1.75E-43)
            if (r6 != r9) goto L_0x0397
            r4.next()     // Catch:{ all -> 0x04c9 }
            r4.resetStringPosition()     // Catch:{ all -> 0x04c9 }
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            r17.setContext(r18, r19)     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r2
        L_0x0397:
            com.alibaba.fastjson.JSONException r9 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r11.<init>()     // Catch:{ all -> 0x04c9 }
            r11.append(r0)     // Catch:{ all -> 0x04c9 }
            int r0 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r11.append(r0)     // Catch:{ all -> 0x04c9 }
            r11.append(r10)     // Catch:{ all -> 0x04c9 }
            r11.append(r14)     // Catch:{ all -> 0x04c9 }
            java.lang.String r0 = r11.toString()     // Catch:{ all -> 0x04c9 }
            r9.<init>(r0)     // Catch:{ all -> 0x04c9 }
            throw r9     // Catch:{ all -> 0x04c9 }
        L_0x03b6:
            r9 = 91
            if (r6 != r9) goto L_0x03e9
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            com.alibaba.fastjson.JSONArray r0 = new com.alibaba.fastjson.JSONArray     // Catch:{ all -> 0x04c9 }
            r0.<init>()     // Catch:{ all -> 0x04c9 }
            r1.parseArray((java.util.Collection) r0, (java.lang.Object) r14)     // Catch:{ all -> 0x04c9 }
            r9 = r0
            r2.put(r14, r9)     // Catch:{ all -> 0x04c9 }
            int r10 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r10 != r11) goto L_0x03d7
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r2
        L_0x03d7:
            int r10 = r4.token()     // Catch:{ all -> 0x04c9 }
            r11 = 16
            if (r10 != r11) goto L_0x03e3
            r12 = 16
            goto L_0x049a
        L_0x03e3:
            com.alibaba.fastjson.JSONException r10 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r10.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r10     // Catch:{ all -> 0x04c9 }
        L_0x03e9:
            r9 = 123(0x7b, float:1.72E-43)
            if (r6 != r9) goto L_0x046c
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            if (r3 == 0) goto L_0x03fc
            java.lang.Class r0 = r19.getClass()     // Catch:{ all -> 0x04c9 }
            java.lang.Class<java.lang.Integer> r9 = java.lang.Integer.class
            if (r0 != r9) goto L_0x03fc
            r12 = 1
            goto L_0x03fd
        L_0x03fc:
            r12 = 0
        L_0x03fd:
            r0 = r12
            com.alibaba.fastjson.JSONObject r9 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x04c9 }
            r9.<init>()     // Catch:{ all -> 0x04c9 }
            r10 = 0
            if (r0 != 0) goto L_0x040b
            com.alibaba.fastjson.parser.ParseContext r12 = r1.setContext(r5, r9, r14)     // Catch:{ all -> 0x04c9 }
            r10 = r12
        L_0x040b:
            java.lang.Object r12 = r1.parseObject(r9, r14)     // Catch:{ all -> 0x04c9 }
            if (r10 == 0) goto L_0x0416
            if (r9 == r12) goto L_0x0416
            r10.setObject(r2)     // Catch:{ all -> 0x04c9 }
        L_0x0416:
            java.lang.String r13 = r14.toString()     // Catch:{ all -> 0x04c9 }
            r1.checkMapResolve(r2, r13)     // Catch:{ all -> 0x04c9 }
            java.lang.Class r13 = r18.getClass()     // Catch:{ all -> 0x04c9 }
            java.lang.Class<com.alibaba.fastjson.JSONObject> r15 = com.alibaba.fastjson.JSONObject.class
            if (r13 != r15) goto L_0x042d
            java.lang.String r13 = r14.toString()     // Catch:{ all -> 0x04c9 }
            r2.put(r13, r12)     // Catch:{ all -> 0x04c9 }
            goto L_0x0430
        L_0x042d:
            r2.put(r14, r12)     // Catch:{ all -> 0x04c9 }
        L_0x0430:
            if (r0 == 0) goto L_0x0435
            r1.setContext(r5, r12, r14)     // Catch:{ all -> 0x04c9 }
        L_0x0435:
            int r13 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r13 != r11) goto L_0x0446
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r2
        L_0x0446:
            int r11 = r4.token()     // Catch:{ all -> 0x04c9 }
            r13 = 16
            if (r11 != r13) goto L_0x0451
            r12 = 16
            goto L_0x049a
        L_0x0451:
            com.alibaba.fastjson.JSONException r11 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r13.<init>()     // Catch:{ all -> 0x04c9 }
            java.lang.String r15 = "syntax error, "
            r13.append(r15)     // Catch:{ all -> 0x04c9 }
            java.lang.String r15 = r4.tokenName()     // Catch:{ all -> 0x04c9 }
            r13.append(r15)     // Catch:{ all -> 0x04c9 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x04c9 }
            r11.<init>(r13)     // Catch:{ all -> 0x04c9 }
            throw r11     // Catch:{ all -> 0x04c9 }
        L_0x046c:
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            java.lang.Object r9 = r17.parse()     // Catch:{ all -> 0x04c9 }
            java.lang.Class r12 = r18.getClass()     // Catch:{ all -> 0x04c9 }
            java.lang.Class<com.alibaba.fastjson.JSONObject> r13 = com.alibaba.fastjson.JSONObject.class
            if (r12 != r13) goto L_0x0480
            java.lang.String r12 = r14.toString()     // Catch:{ all -> 0x04c9 }
            r14 = r12
        L_0x0480:
            r2.put(r14, r9)     // Catch:{ all -> 0x04c9 }
            int r12 = r4.token()     // Catch:{ all -> 0x04c9 }
            if (r12 != r11) goto L_0x0491
            r4.nextToken()     // Catch:{ all -> 0x04c9 }
            r1.setContext(r5)
            return r2
        L_0x0491:
            int r11 = r4.token()     // Catch:{ all -> 0x04c9 }
            r12 = 16
            if (r11 != r12) goto L_0x049e
        L_0x049a:
            r6 = 16
            goto L_0x0047
        L_0x049e:
            com.alibaba.fastjson.JSONException r11 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c9 }
            r12.<init>()     // Catch:{ all -> 0x04c9 }
            r12.append(r0)     // Catch:{ all -> 0x04c9 }
            int r0 = r4.pos()     // Catch:{ all -> 0x04c9 }
            r12.append(r0)     // Catch:{ all -> 0x04c9 }
            r12.append(r10)     // Catch:{ all -> 0x04c9 }
            r12.append(r14)     // Catch:{ all -> 0x04c9 }
            java.lang.String r0 = r12.toString()     // Catch:{ all -> 0x04c9 }
            r11.<init>(r0)     // Catch:{ all -> 0x04c9 }
            throw r11     // Catch:{ all -> 0x04c9 }
        L_0x04bd:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r6.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x04c3:
            com.alibaba.fastjson.JSONException r6 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x04c9 }
            r6.<init>(r12)     // Catch:{ all -> 0x04c9 }
            throw r6     // Catch:{ all -> 0x04c9 }
        L_0x04c9:
            r0 = move-exception
            r1.setContext(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseObject(java.util.Map, java.lang.Object):java.lang.Object");
    }

    public ParserConfig getConfig() {
        return this.config;
    }

    public void setConfig(ParserConfig config2) {
        this.config = config2;
    }

    public <T> T parseObject(Class<T> clazz) {
        return parseObject((Type) clazz);
    }

    public <T> T parseObject(Type type) {
        if (this.lexer.token() == 8) {
            this.lexer.nextToken();
            return null;
        }
        if (this.lexer.token() == 4) {
            type = TypeUtils.unwrap(type);
            if (type == byte[].class) {
                Object bytesValue = this.lexer.bytesValue();
                this.lexer.nextToken();
                return bytesValue;
            } else if (type == char[].class) {
                String strVal = this.lexer.stringVal();
                this.lexer.nextToken();
                return strVal.toCharArray();
            }
        }
        try {
            return this.config.getDeserializer(type).deserialze(this, type, (Object) null);
        } catch (JSONException e) {
            throw e;
        } catch (Throwable e2) {
            throw new JSONException(e2.getMessage(), e2);
        }
    }

    public <T> List<T> parseArray(Class<T> clazz) {
        List<T> array = new ArrayList<>();
        parseArray((Class<?>) clazz, (Collection) array);
        return array;
    }

    public void parseArray(Class<?> clazz, Collection array) {
        parseArray((Type) clazz, array);
    }

    public void parseArray(Type type, Collection array) {
        parseArray(type, array, (Object) null);
    }

    /* JADX INFO: finally extract failed */
    public void parseArray(Type type, Collection array, Object fieldName) {
        ObjectDeserializer deserializer;
        Object val;
        Object obj;
        if (this.lexer.token() == 21 || this.lexer.token() == 22) {
            this.lexer.nextToken();
        }
        if (this.lexer.token() == 14) {
            if (Integer.TYPE == type) {
                deserializer = IntegerCodec.instance;
                this.lexer.nextToken(2);
            } else if (String.class == type) {
                deserializer = StringCodec.instance;
                this.lexer.nextToken(4);
            } else {
                deserializer = this.config.getDeserializer(type);
                this.lexer.nextToken(deserializer.getFastMatchToken());
            }
            ParseContext context2 = getContext();
            setContext(array, fieldName);
            int i = 0;
            while (true) {
                try {
                    if (isEnabled(Feature.AllowArbitraryCommas)) {
                        while (this.lexer.token() == 16) {
                            this.lexer.nextToken();
                        }
                    }
                    if (this.lexer.token() == 15) {
                        setContext(context2);
                        this.lexer.nextToken(16);
                        return;
                    }
                    if (Integer.TYPE == type) {
                        array.add(IntegerCodec.instance.deserialze(this, (Type) null, (Object) null));
                    } else if (String.class == type) {
                        if (this.lexer.token() == 4) {
                            obj = this.lexer.stringVal();
                            this.lexer.nextToken(16);
                        } else {
                            Object obj2 = parse();
                            if (obj2 == null) {
                                obj = null;
                            } else {
                                obj = obj2.toString();
                            }
                        }
                        array.add(obj);
                    } else {
                        if (this.lexer.token() == 8) {
                            this.lexer.nextToken();
                            val = null;
                        } else {
                            val = deserializer.deserialze(this, type, Integer.valueOf(i));
                        }
                        array.add(val);
                        checkListResolve(array);
                    }
                    if (this.lexer.token() == 16) {
                        this.lexer.nextToken(deserializer.getFastMatchToken());
                    }
                    i++;
                } catch (Throwable th) {
                    setContext(context2);
                    throw th;
                }
            }
        } else {
            throw new JSONException("exepct '[', but " + JSONToken.name(this.lexer.token()));
        }
    }

    public Object[] parseArray(Type[] types) {
        String value;
        Type[] typeArr = types;
        int i = 8;
        Object obj = null;
        if (this.lexer.token() == 8) {
            this.lexer.nextToken(16);
            return null;
        }
        int i2 = 14;
        if (this.lexer.token() == 14) {
            Object[] list = new Object[typeArr.length];
            if (typeArr.length == 0) {
                this.lexer.nextToken(15);
                if (this.lexer.token() == 15) {
                    this.lexer.nextToken(16);
                    return new Object[0];
                }
                throw new JSONException("syntax error");
            }
            this.lexer.nextToken(2);
            int i3 = 0;
            while (i3 < typeArr.length) {
                if (this.lexer.token() == i) {
                    value = null;
                    this.lexer.nextToken(16);
                } else {
                    Type type = typeArr[i3];
                    if (type == Integer.TYPE || type == Integer.class) {
                        if (this.lexer.token() == 2) {
                            Integer valueOf = Integer.valueOf(this.lexer.intValue());
                            this.lexer.nextToken(16);
                            value = valueOf;
                        } else {
                            value = TypeUtils.cast(parse(), type, this.config);
                        }
                    } else if (type != String.class) {
                        boolean isArray = false;
                        Class<?> componentType = null;
                        if (i3 == typeArr.length - 1 && (type instanceof Class)) {
                            Class<?> clazz = (Class) type;
                            isArray = clazz.isArray();
                            componentType = clazz.getComponentType();
                        }
                        if (!isArray || this.lexer.token() == i2) {
                            obj = null;
                            value = this.config.getDeserializer(type).deserialze(this, type, (Object) null);
                        } else {
                            List<Object> varList = new ArrayList<>();
                            ObjectDeserializer derializer = this.config.getDeserializer((Type) componentType);
                            int fastMatch = derializer.getFastMatchToken();
                            if (this.lexer.token() != 15) {
                                while (true) {
                                    varList.add(derializer.deserialze(this, type, obj));
                                    if (this.lexer.token() != 16) {
                                        break;
                                    }
                                    this.lexer.nextToken(fastMatch);
                                    obj = null;
                                }
                                if (this.lexer.token() != 15) {
                                    throw new JSONException("syntax error :" + JSONToken.name(this.lexer.token()));
                                }
                            }
                            value = TypeUtils.cast((Object) varList, type, this.config);
                            obj = null;
                        }
                    } else if (this.lexer.token() == 4) {
                        String stringVal = this.lexer.stringVal();
                        this.lexer.nextToken(16);
                        value = stringVal;
                    } else {
                        value = TypeUtils.cast(parse(), type, this.config);
                    }
                }
                list[i3] = value;
                if (this.lexer.token() == 15) {
                    break;
                } else if (this.lexer.token() == 16) {
                    if (i3 == typeArr.length - 1) {
                        this.lexer.nextToken(15);
                    } else {
                        this.lexer.nextToken(2);
                    }
                    i3++;
                    i = 8;
                    i2 = 14;
                } else {
                    throw new JSONException("syntax error :" + JSONToken.name(this.lexer.token()));
                }
            }
            if (this.lexer.token() == 15) {
                this.lexer.nextToken(16);
                return list;
            }
            throw new JSONException("syntax error");
        }
        throw new JSONException("syntax error : " + this.lexer.tokenName());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: com.alibaba.fastjson.parser.deserializer.FieldDeserializer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void parseObject(java.lang.Object r13) {
        /*
            r12 = this;
            java.lang.Class r0 = r13.getClass()
            com.alibaba.fastjson.parser.ParserConfig r1 = r12.config
            java.util.Map r1 = r1.getFieldDeserializers(r0)
            com.alibaba.fastjson.parser.JSONLexer r2 = r12.lexer
            int r2 = r2.token()
            r3 = 16
            r4 = 12
            if (r2 == r4) goto L_0x003c
            com.alibaba.fastjson.parser.JSONLexer r2 = r12.lexer
            int r2 = r2.token()
            if (r2 != r3) goto L_0x001f
            goto L_0x003c
        L_0x001f:
            com.alibaba.fastjson.JSONException r2 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "syntax error, expect {, actual "
            r3.append(r4)
            com.alibaba.fastjson.parser.JSONLexer r4 = r12.lexer
            java.lang.String r4 = r4.tokenName()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x003c:
            com.alibaba.fastjson.parser.JSONLexer r2 = r12.lexer
            com.alibaba.fastjson.parser.SymbolTable r4 = r12.symbolTable
            java.lang.String r2 = r2.scanSymbol(r4)
            r4 = 13
            if (r2 != 0) goto L_0x0068
            com.alibaba.fastjson.parser.JSONLexer r5 = r12.lexer
            int r5 = r5.token()
            if (r5 != r4) goto L_0x0057
            com.alibaba.fastjson.parser.JSONLexer r4 = r12.lexer
            r4.nextToken(r3)
            return
        L_0x0057:
            com.alibaba.fastjson.parser.JSONLexer r5 = r12.lexer
            int r5 = r5.token()
            if (r5 != r3) goto L_0x0068
            com.alibaba.fastjson.parser.Feature r5 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas
            boolean r5 = r12.isEnabled(r5)
            if (r5 == 0) goto L_0x0068
            goto L_0x003c
        L_0x0068:
            java.lang.Object r5 = r1.get(r2)
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer r5 = (com.alibaba.fastjson.parser.deserializer.FieldDeserializer) r5
            if (r5 != 0) goto L_0x009b
            if (r2 == 0) goto L_0x009b
            java.util.Set r6 = r1.entrySet()
            java.util.Iterator r6 = r6.iterator()
        L_0x007a:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x009b
            java.lang.Object r7 = r6.next()
            java.util.Map$Entry r7 = (java.util.Map.Entry) r7
            java.lang.Object r8 = r7.getKey()
            java.lang.String r8 = (java.lang.String) r8
            boolean r8 = r2.equalsIgnoreCase(r8)
            if (r8 == 0) goto L_0x009a
            java.lang.Object r8 = r7.getValue()
            r5 = r8
            com.alibaba.fastjson.parser.deserializer.FieldDeserializer r5 = (com.alibaba.fastjson.parser.deserializer.FieldDeserializer) r5
            goto L_0x009b
        L_0x009a:
            goto L_0x007a
        L_0x009b:
            if (r5 != 0) goto L_0x00de
            com.alibaba.fastjson.parser.Feature r6 = com.alibaba.fastjson.parser.Feature.IgnoreNotMatch
            boolean r6 = r12.isEnabled(r6)
            if (r6 == 0) goto L_0x00bb
            com.alibaba.fastjson.parser.JSONLexer r6 = r12.lexer
            r6.nextTokenWithColon()
            r12.parse()
            com.alibaba.fastjson.parser.JSONLexer r6 = r12.lexer
            int r6 = r6.token()
            if (r6 != r4) goto L_0x003c
            com.alibaba.fastjson.parser.JSONLexer r3 = r12.lexer
            r3.nextToken()
            return
        L_0x00bb:
            com.alibaba.fastjson.JSONException r3 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "setter not found, class "
            r4.append(r6)
            java.lang.String r6 = r0.getName()
            r4.append(r6)
            java.lang.String r6 = ", property "
            r4.append(r6)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x00de:
            java.lang.Class r6 = r5.getFieldClass()
            java.lang.reflect.Type r7 = r5.getFieldType()
            java.lang.Class r8 = java.lang.Integer.TYPE
            r9 = 2
            r10 = 0
            if (r6 != r8) goto L_0x00f8
            com.alibaba.fastjson.parser.JSONLexer r8 = r12.lexer
            r8.nextTokenWithColon(r9)
            com.alibaba.fastjson.serializer.IntegerCodec r8 = com.alibaba.fastjson.serializer.IntegerCodec.instance
            java.lang.Object r8 = r8.deserialze(r12, r7, r10)
            goto L_0x012b
        L_0x00f8:
            java.lang.Class<java.lang.String> r8 = java.lang.String.class
            if (r6 != r8) goto L_0x0107
            com.alibaba.fastjson.parser.JSONLexer r8 = r12.lexer
            r9 = 4
            r8.nextTokenWithColon(r9)
            java.lang.Object r8 = com.alibaba.fastjson.serializer.StringCodec.deserialze(r12)
            goto L_0x012b
        L_0x0107:
            java.lang.Class r8 = java.lang.Long.TYPE
            if (r6 != r8) goto L_0x0117
            com.alibaba.fastjson.parser.JSONLexer r8 = r12.lexer
            r8.nextTokenWithColon(r9)
            com.alibaba.fastjson.serializer.LongCodec r8 = com.alibaba.fastjson.serializer.LongCodec.instance
            java.lang.Object r8 = r8.deserialze(r12, r7, r10)
            goto L_0x012b
        L_0x0117:
            com.alibaba.fastjson.parser.ParserConfig r8 = r12.config
            com.alibaba.fastjson.parser.deserializer.ObjectDeserializer r8 = r8.getDeserializer(r6, r7)
            com.alibaba.fastjson.parser.JSONLexer r9 = r12.lexer
            int r11 = r8.getFastMatchToken()
            r9.nextTokenWithColon(r11)
            java.lang.Object r9 = r8.deserialze(r12, r7, r10)
            r8 = r9
        L_0x012b:
            r5.setValue((java.lang.Object) r13, (java.lang.Object) r8)
            com.alibaba.fastjson.parser.JSONLexer r6 = r12.lexer
            int r6 = r6.token()
            if (r6 != r3) goto L_0x0138
            goto L_0x003c
        L_0x0138:
            com.alibaba.fastjson.parser.JSONLexer r6 = r12.lexer
            int r6 = r6.token()
            if (r6 != r4) goto L_0x0146
            com.alibaba.fastjson.parser.JSONLexer r4 = r12.lexer
            r4.nextToken(r3)
            return
        L_0x0146:
            goto L_0x003c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseObject(java.lang.Object):void");
    }

    public Object parseArrayWithType(Type collectionType) {
        if (this.lexer.token() == 8) {
            this.lexer.nextToken();
            return null;
        }
        Type[] actualTypes = ((ParameterizedType) collectionType).getActualTypeArguments();
        if (actualTypes.length == 1) {
            Type actualTypeArgument = actualTypes[0];
            if (actualTypeArgument instanceof Class) {
                List<Object> array = new ArrayList<>();
                parseArray((Class<?>) (Class) actualTypeArgument, (Collection) array);
                return array;
            } else if (actualTypeArgument instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) actualTypeArgument;
                Type upperBoundType = wildcardType.getUpperBounds()[0];
                if (!Object.class.equals(upperBoundType)) {
                    List<Object> array2 = new ArrayList<>();
                    parseArray((Class<?>) (Class) upperBoundType, (Collection) array2);
                    return array2;
                } else if (wildcardType.getLowerBounds().length == 0) {
                    return parse();
                } else {
                    throw new JSONException("not support type : " + collectionType);
                }
            } else {
                if (actualTypeArgument instanceof TypeVariable) {
                    TypeVariable<?> typeVariable = (TypeVariable) actualTypeArgument;
                    Type[] bounds = typeVariable.getBounds();
                    if (bounds.length == 1) {
                        Type boundType = bounds[0];
                        if (boundType instanceof Class) {
                            List<Object> array3 = new ArrayList<>();
                            parseArray((Class<?>) (Class) boundType, (Collection) array3);
                            return array3;
                        }
                    } else {
                        throw new JSONException("not support : " + typeVariable);
                    }
                }
                if (actualTypeArgument instanceof ParameterizedType) {
                    List<Object> array4 = new ArrayList<>();
                    parseArray((Type) (ParameterizedType) actualTypeArgument, (Collection) array4);
                    return array4;
                }
                throw new JSONException("TODO : " + collectionType);
            }
        } else {
            throw new JSONException("not support type " + collectionType);
        }
    }

    public int getResolveStatus() {
        return this.resolveStatus;
    }

    public void setResolveStatus(int resolveStatus2) {
        this.resolveStatus = resolveStatus2;
    }

    public Object getObject(String path) {
        for (int i = 0; i < this.contextArrayIndex; i++) {
            if (path.equals(this.contextArray[i].getPath())) {
                return this.contextArray[i].getObject();
            }
        }
        return null;
    }

    public void checkListResolve(Collection array) {
        if (this.resolveStatus != 1) {
            return;
        }
        if (array instanceof List) {
            ResolveTask task = getLastResolveTask();
            task.setFieldDeserializer(new ListResolveFieldDeserializer(this, (List) array, array.size() - 1));
            task.setOwnerContext(this.context);
            setResolveStatus(0);
            return;
        }
        ResolveTask task2 = getLastResolveTask();
        task2.setFieldDeserializer(new CollectionResolveFieldDeserializer(this, array));
        task2.setOwnerContext(this.context);
        setResolveStatus(0);
    }

    public void checkMapResolve(Map object, String fieldName) {
        if (this.resolveStatus == 1) {
            MapResolveFieldDeserializer fieldResolver = new MapResolveFieldDeserializer(object, fieldName);
            ResolveTask task = getLastResolveTask();
            task.setFieldDeserializer(fieldResolver);
            task.setOwnerContext(this.context);
            setResolveStatus(0);
        }
    }

    public Object parseObject(Map object) {
        return parseObject(object, (Object) null);
    }

    public JSONObject parseObject() {
        JSONObject object = new JSONObject();
        parseObject((Map) object);
        return object;
    }

    public final void parseArray(Collection array) {
        parseArray(array, (Object) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.Number} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.Number} */
    /* JADX WARNING: type inference failed for: r8v4, types: [java.util.Map, com.alibaba.fastjson.JSONObject] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void parseArray(java.util.Collection r14, java.lang.Object r15) {
        /*
            r13 = this;
            com.alibaba.fastjson.parser.JSONLexer r0 = r13.getLexer()
            int r1 = r0.token()
            r2 = 21
            if (r1 == r2) goto L_0x0014
            int r1 = r0.token()
            r2 = 22
            if (r1 != r2) goto L_0x0017
        L_0x0014:
            r0.nextToken()
        L_0x0017:
            int r1 = r0.token()
            r2 = 14
            if (r1 != r2) goto L_0x012c
            r1 = 4
            r0.nextToken(r1)
            com.alibaba.fastjson.parser.ParseContext r3 = r13.getContext()
            r13.setContext(r14, r15)
            r4 = 0
            r5 = 0
            r6 = r5
            r7 = r6
            r8 = r7
        L_0x002f:
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.AllowArbitraryCommas     // Catch:{ all -> 0x0127 }
            boolean r9 = r13.isEnabled(r9)     // Catch:{ all -> 0x0127 }
            r10 = 16
            if (r9 == 0) goto L_0x0043
        L_0x0039:
            int r9 = r0.token()     // Catch:{ all -> 0x0127 }
            if (r9 != r10) goto L_0x0043
            r0.nextToken()     // Catch:{ all -> 0x0127 }
            goto L_0x0039
        L_0x0043:
            int r9 = r0.token()     // Catch:{ all -> 0x0127 }
            r11 = 2
            if (r9 == r11) goto L_0x010c
            r11 = 3
            if (r9 == r11) goto L_0x00f4
            if (r9 == r1) goto L_0x00c9
            r11 = 6
            if (r9 == r11) goto L_0x00c2
            r11 = 7
            if (r9 == r11) goto L_0x00bb
            r11 = 8
            if (r9 == r11) goto L_0x00b6
            r11 = 12
            if (r9 == r11) goto L_0x00a2
            r11 = 20
            if (r9 == r11) goto L_0x0096
            r11 = 23
            if (r9 == r11) goto L_0x0090
            if (r9 == r2) goto L_0x007d
            r11 = 15
            if (r9 == r11) goto L_0x0072
            java.lang.Object r9 = r13.parse()     // Catch:{ all -> 0x0127 }
            r5 = r9
            goto L_0x0114
        L_0x0072:
            r1 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
            r13.setContext(r3)
            return
        L_0x007d:
            r7 = r8
            com.alibaba.fastjson.JSONArray r8 = new com.alibaba.fastjson.JSONArray     // Catch:{ all -> 0x0127 }
            r8.<init>()     // Catch:{ all -> 0x0127 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0127 }
            r13.parseArray((java.util.Collection) r8, (java.lang.Object) r9)     // Catch:{ all -> 0x0127 }
            r5 = r8
            r12 = r8
            r8 = r7
            r7 = r12
            goto L_0x0114
        L_0x0090:
            r5 = 0
            r0.nextToken(r1)     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x0096:
            r1 = r5
            r2 = r6
            r5 = r7
            r6 = r8
            com.alibaba.fastjson.JSONException r7 = new com.alibaba.fastjson.JSONException     // Catch:{ all -> 0x0127 }
            java.lang.String r8 = "unclosed jsonArray"
            r7.<init>(r8)     // Catch:{ all -> 0x0127 }
            throw r7     // Catch:{ all -> 0x0127 }
        L_0x00a2:
            r6 = r8
            com.alibaba.fastjson.JSONObject r8 = new com.alibaba.fastjson.JSONObject     // Catch:{ all -> 0x0127 }
            r8.<init>()     // Catch:{ all -> 0x0127 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0127 }
            java.lang.Object r9 = r13.parseObject(r8, r9)     // Catch:{ all -> 0x0127 }
            r5 = r9
            r12 = r8
            r8 = r6
            r6 = r12
            goto L_0x0114
        L_0x00b6:
            r5 = 0
            r0.nextToken(r1)     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x00bb:
            java.lang.Boolean r9 = java.lang.Boolean.FALSE     // Catch:{ all -> 0x0127 }
            r5 = r9
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x00c2:
            java.lang.Boolean r9 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x0127 }
            r5 = r9
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x00c9:
            java.lang.String r8 = r0.stringVal()     // Catch:{ all -> 0x0127 }
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.AllowISO8601DateFormat     // Catch:{ all -> 0x0127 }
            boolean r9 = r0.isEnabled(r9)     // Catch:{ all -> 0x0127 }
            if (r9 == 0) goto L_0x00f2
            com.alibaba.fastjson.parser.JSONScanner r6 = new com.alibaba.fastjson.parser.JSONScanner     // Catch:{ all -> 0x0127 }
            r6.<init>(r8)     // Catch:{ all -> 0x0127 }
            boolean r9 = r6.scanISO8601DateIfMatch()     // Catch:{ all -> 0x0127 }
            if (r9 == 0) goto L_0x00ed
            java.util.Calendar r9 = r6.getCalendar()     // Catch:{ all -> 0x0127 }
            java.util.Date r9 = r9.getTime()     // Catch:{ all -> 0x0127 }
            r5 = r9
            goto L_0x00ee
        L_0x00ed:
            r5 = r8
        L_0x00ee:
            r6.close()     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x00f2:
            r5 = r8
            goto L_0x0114
        L_0x00f4:
            com.alibaba.fastjson.parser.Feature r9 = com.alibaba.fastjson.parser.Feature.UseBigDecimal     // Catch:{ all -> 0x0127 }
            boolean r9 = r0.isEnabled(r9)     // Catch:{ all -> 0x0127 }
            if (r9 == 0) goto L_0x0103
            r9 = 1
            java.lang.Number r9 = r0.decimalValue(r9)     // Catch:{ all -> 0x0127 }
            r5 = r9
            goto L_0x0108
        L_0x0103:
            r5 = 0
            java.lang.Number r5 = r0.decimalValue(r5)     // Catch:{ all -> 0x0127 }
        L_0x0108:
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
            goto L_0x0114
        L_0x010c:
            java.lang.Number r5 = r0.integerValue()     // Catch:{ all -> 0x0127 }
            r0.nextToken(r10)     // Catch:{ all -> 0x0127 }
        L_0x0114:
            r14.add(r5)     // Catch:{ all -> 0x0127 }
            r13.checkListResolve(r14)     // Catch:{ all -> 0x0127 }
            int r9 = r0.token()     // Catch:{ all -> 0x0127 }
            if (r9 != r10) goto L_0x0123
            r0.nextToken(r1)     // Catch:{ all -> 0x0127 }
        L_0x0123:
            int r4 = r4 + 1
            goto L_0x002f
        L_0x0127:
            r1 = move-exception
            r13.setContext(r3)
            throw r1
        L_0x012c:
            com.alibaba.fastjson.JSONException r1 = new com.alibaba.fastjson.JSONException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "syntax error, expect [, actual "
            r2.append(r3)
            int r3 = r0.token()
            java.lang.String r3 = com.alibaba.fastjson.parser.JSONToken.name(r3)
            r2.append(r3)
            java.lang.String r3 = ", pos "
            r2.append(r3)
            int r3 = r0.pos()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.DefaultJSONParser.parseArray(java.util.Collection, java.lang.Object):void");
    }

    public ParseContext getContext() {
        return this.context;
    }

    public void addResolveTask(ResolveTask task) {
        if (this.resolveTaskList == null) {
            this.resolveTaskList = new ArrayList(2);
        }
        this.resolveTaskList.add(task);
    }

    public ResolveTask getLastResolveTask() {
        List<ResolveTask> list = this.resolveTaskList;
        return list.get(list.size() - 1);
    }

    public List<ExtraProcessor> getExtraProcessors() {
        if (this.extraProcessors == null) {
            this.extraProcessors = new ArrayList(2);
        }
        return this.extraProcessors;
    }

    public List<ExtraProcessor> getExtraProcessorsDirect() {
        return this.extraProcessors;
    }

    public List<ExtraTypeProvider> getExtraTypeProviders() {
        if (this.extraTypeProviders == null) {
            this.extraTypeProviders = new ArrayList(2);
        }
        return this.extraTypeProviders;
    }

    public List<ExtraTypeProvider> getExtraTypeProvidersDirect() {
        return this.extraTypeProviders;
    }

    public void setContext(ParseContext context2) {
        if (!isEnabled(Feature.DisableCircularReferenceDetect)) {
            this.context = context2;
        }
    }

    public void popContext() {
        if (!isEnabled(Feature.DisableCircularReferenceDetect)) {
            this.context = this.context.getParentContext();
            ParseContext[] parseContextArr = this.contextArray;
            int i = this.contextArrayIndex;
            parseContextArr[i - 1] = null;
            this.contextArrayIndex = i - 1;
        }
    }

    public ParseContext setContext(Object object, Object fieldName) {
        if (isEnabled(Feature.DisableCircularReferenceDetect)) {
            return null;
        }
        return setContext(this.context, object, fieldName);
    }

    public ParseContext setContext(ParseContext parent, Object object, Object fieldName) {
        if (isEnabled(Feature.DisableCircularReferenceDetect)) {
            return null;
        }
        ParseContext parseContext = new ParseContext(parent, object, fieldName);
        this.context = parseContext;
        addContext(parseContext);
        return this.context;
    }

    private void addContext(ParseContext context2) {
        int i = this.contextArrayIndex;
        this.contextArrayIndex = i + 1;
        ParseContext[] parseContextArr = this.contextArray;
        if (i >= parseContextArr.length) {
            ParseContext[] newArray = new ParseContext[((parseContextArr.length * 3) / 2)];
            System.arraycopy(parseContextArr, 0, newArray, 0, parseContextArr.length);
            this.contextArray = newArray;
        }
        this.contextArray[i] = context2;
    }

    public Object parse() {
        return parse((Object) null);
    }

    public Object parseKey() {
        if (this.lexer.token() != 18) {
            return parse((Object) null);
        }
        String value = this.lexer.stringVal();
        this.lexer.nextToken(16);
        return value;
    }

    public Object parse(Object fieldName) {
        JSONLexer lexer2 = getLexer();
        int i = lexer2.token();
        if (i == 2) {
            Number intValue = lexer2.integerValue();
            lexer2.nextToken();
            return intValue;
        } else if (i == 3) {
            Object value = lexer2.decimalValue(isEnabled(Feature.UseBigDecimal));
            lexer2.nextToken();
            return value;
        } else if (i == 4) {
            String stringLiteral = lexer2.stringVal();
            lexer2.nextToken(16);
            if (lexer2.isEnabled(Feature.AllowISO8601DateFormat)) {
                JSONScanner iso8601Lexer = new JSONScanner(stringLiteral);
                try {
                    if (iso8601Lexer.scanISO8601DateIfMatch()) {
                        return iso8601Lexer.getCalendar().getTime();
                    }
                    iso8601Lexer.close();
                } finally {
                    iso8601Lexer.close();
                }
            }
            return stringLiteral;
        } else if (i == 12) {
            return parseObject(new JSONObject(), fieldName);
        } else {
            if (i != 14) {
                switch (i) {
                    case 6:
                        lexer2.nextToken();
                        return Boolean.TRUE;
                    case 7:
                        lexer2.nextToken();
                        return Boolean.FALSE;
                    case 8:
                        lexer2.nextToken();
                        return null;
                    case 9:
                        lexer2.nextToken(18);
                        if (lexer2.token() == 18) {
                            lexer2.nextToken(10);
                            accept(10);
                            long time = lexer2.integerValue().longValue();
                            accept(2);
                            accept(11);
                            return new Date(time);
                        }
                        throw new JSONException("syntax error");
                    default:
                        switch (i) {
                            case 20:
                                if (lexer2.isBlankInput()) {
                                    return null;
                                }
                                throw new JSONException("unterminated json string, pos " + lexer2.getBufferPosition());
                            case 21:
                                lexer2.nextToken();
                                HashSet<Object> set = new HashSet<>();
                                parseArray((Collection) set, fieldName);
                                return set;
                            case 22:
                                lexer2.nextToken();
                                TreeSet<Object> treeSet = new TreeSet<>();
                                parseArray((Collection) treeSet, fieldName);
                                return treeSet;
                            case 23:
                                lexer2.nextToken();
                                return null;
                            default:
                                throw new JSONException("syntax error, pos " + lexer2.getBufferPosition());
                        }
                }
            } else {
                JSONArray array = new JSONArray();
                parseArray((Collection) array, fieldName);
                return array;
            }
        }
    }

    public void config(Feature feature, boolean state) {
        getLexer().config(feature, state);
    }

    public boolean isEnabled(Feature feature) {
        return getLexer().isEnabled(feature);
    }

    public JSONLexer getLexer() {
        return this.lexer;
    }

    public final void accept(int token) {
        JSONLexer lexer2 = getLexer();
        if (lexer2.token() == token) {
            lexer2.nextToken();
            return;
        }
        throw new JSONException("syntax error, expect " + JSONToken.name(token) + ", actual " + JSONToken.name(lexer2.token()));
    }

    public final void accept(int token, int nextExpectToken) {
        JSONLexer lexer2 = getLexer();
        if (lexer2.token() == token) {
            lexer2.nextToken(nextExpectToken);
            return;
        }
        throw new JSONException("syntax error, expect " + JSONToken.name(token) + ", actual " + JSONToken.name(lexer2.token()));
    }

    public void close() {
        JSONLexer lexer2 = getLexer();
        try {
            if (isEnabled(Feature.AutoCloseSource)) {
                if (lexer2.token() != 20) {
                    throw new JSONException("not close json text, token : " + JSONToken.name(lexer2.token()));
                }
            }
        } finally {
            lexer2.close();
        }
    }

    public void handleResovleTask(Object value) {
        Object refValue;
        List<ResolveTask> list = this.resolveTaskList;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                ResolveTask task = this.resolveTaskList.get(i);
                FieldDeserializer fieldDeser = task.getFieldDeserializer();
                if (fieldDeser != null) {
                    Object object = null;
                    if (task.getOwnerContext() != null) {
                        object = task.getOwnerContext().getObject();
                    }
                    String ref = task.getReferenceValue();
                    if (ref.startsWith("$")) {
                        refValue = getObject(ref);
                    } else {
                        refValue = task.getContext().getObject();
                    }
                    fieldDeser.setValue(object, refValue);
                }
            }
        }
    }

    public static class ResolveTask {
        private final ParseContext context;
        private FieldDeserializer fieldDeserializer;
        private ParseContext ownerContext;
        private final String referenceValue;

        public ResolveTask(ParseContext context2, String referenceValue2) {
            this.context = context2;
            this.referenceValue = referenceValue2;
        }

        public ParseContext getContext() {
            return this.context;
        }

        public String getReferenceValue() {
            return this.referenceValue;
        }

        public FieldDeserializer getFieldDeserializer() {
            return this.fieldDeserializer;
        }

        public void setFieldDeserializer(FieldDeserializer fieldDeserializer2) {
            this.fieldDeserializer = fieldDeserializer2;
        }

        public ParseContext getOwnerContext() {
            return this.ownerContext;
        }

        public void setOwnerContext(ParseContext ownerContext2) {
            this.ownerContext = ownerContext2;
        }
    }
}
