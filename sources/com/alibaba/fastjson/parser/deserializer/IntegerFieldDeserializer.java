package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Type;
import java.util.Map;

public class IntegerFieldDeserializer extends FieldDeserializer {
    public IntegerFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        Object obj;
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 2) {
            int val = lexer.intValue();
            lexer.nextToken(16);
            if (object == null) {
                fieldValues.put(this.fieldInfo.getName(), Integer.valueOf(val));
            } else {
                setValue(object, val);
            }
        } else {
            if (lexer.token() == 8) {
                obj = null;
                lexer.nextToken(16);
            } else {
                obj = TypeUtils.castToInt(parser.parse());
            }
            if (obj != null || getFieldClass() != Integer.TYPE) {
                if (object == null) {
                    fieldValues.put(this.fieldInfo.getName(), obj);
                } else {
                    setValue(object, obj);
                }
            }
        }
    }

    public int getFastMatchToken() {
        return 2;
    }
}
