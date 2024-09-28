package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Type;
import java.util.Map;

public class LongFieldDeserializer extends FieldDeserializer {
    private final ObjectDeserializer fieldValueDeserilizer;

    public LongFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
        this.fieldValueDeserilizer = mapping.getDeserializer(fieldInfo);
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        Object obj;
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 2) {
            long val = lexer.longValue();
            lexer.nextToken(16);
            if (object == null) {
                fieldValues.put(this.fieldInfo.getName(), Long.valueOf(val));
            } else {
                setValue(object, val);
            }
        } else {
            if (lexer.token() == 8) {
                obj = null;
                lexer.nextToken(16);
            } else {
                obj = TypeUtils.castToLong(parser.parse());
            }
            if (obj != null || getFieldClass() != Long.TYPE) {
                if (object == null) {
                    fieldValues.put(this.fieldInfo.getName(), obj);
                } else {
                    setValue(object, obj);
                }
            }
        }
    }

    public int getFastMatchToken() {
        return this.fieldValueDeserilizer.getFastMatchToken();
    }
}
