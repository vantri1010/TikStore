package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.IOException;
import java.lang.reflect.Type;

public class LongCodec implements ObjectSerializer, ObjectDeserializer {
    public static LongCodec instance = new LongCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (object != null) {
            long value = ((Long) object).longValue();
            out.writeLong(value);
            if (serializer.isEnabled(SerializerFeature.WriteClassName) && value <= 2147483647L && value >= -2147483648L && fieldType != Long.class) {
                out.write('L');
            }
        } else if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
            out.write('0');
        } else {
            out.writeNull();
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 2) {
            long longValue = lexer.longValue();
            lexer.nextToken(16);
            return Long.valueOf(longValue);
        }
        Object value = parser.parse();
        if (value == null) {
            return null;
        }
        return TypeUtils.castToLong(value);
    }

    public int getFastMatchToken() {
        return 2;
    }
}
