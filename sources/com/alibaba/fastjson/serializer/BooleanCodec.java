package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;

public class BooleanCodec implements ObjectSerializer, ObjectDeserializer {
    public static final BooleanCodec instance = new BooleanCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();
        Boolean value = (Boolean) object;
        if (value == null) {
            if (out.isEnabled(SerializerFeature.WriteNullBooleanAsFalse)) {
                out.write("false");
            } else {
                out.writeNull();
            }
        } else if (value.booleanValue()) {
            out.write("true");
        } else {
            out.write("false");
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 6) {
            lexer.nextToken(16);
            return Boolean.TRUE;
        } else if (lexer.token() == 7) {
            lexer.nextToken(16);
            return Boolean.FALSE;
        } else if (lexer.token() == 2) {
            int intValue = lexer.intValue();
            lexer.nextToken(16);
            if (intValue == 1) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } else {
            Object value = parser.parse();
            if (value == null) {
                return null;
            }
            return TypeUtils.castToBoolean(value);
        }
    }

    public int getFastMatchToken() {
        return 6;
    }
}
