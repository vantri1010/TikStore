package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.util.TypeUtils;
import java.lang.reflect.Type;
import java.math.BigDecimal;

public class NumberDeserializer implements ObjectDeserializer {
    public static final NumberDeserializer instance = new NumberDeserializer();

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 2) {
            if (clazz == Double.TYPE || clazz == Double.class) {
                String val = lexer.numberString();
                lexer.nextToken(16);
                return Double.valueOf(Double.parseDouble(val));
            }
            long val2 = lexer.longValue();
            lexer.nextToken(16);
            if (clazz == Short.TYPE || clazz == Short.class) {
                return Short.valueOf((short) ((int) val2));
            }
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                return Byte.valueOf((byte) ((int) val2));
            }
            if (val2 < -2147483648L || val2 > 2147483647L) {
                return Long.valueOf(val2);
            }
            return Integer.valueOf((int) val2);
        } else if (lexer.token() != 3) {
            Object value = parser.parse();
            if (value == null) {
                return null;
            }
            if (clazz == Double.TYPE || clazz == Double.class) {
                return TypeUtils.castToDouble(value);
            }
            if (clazz == Short.TYPE || clazz == Short.class) {
                return TypeUtils.castToShort(value);
            }
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                return TypeUtils.castToByte(value);
            }
            return TypeUtils.castToBigDecimal(value);
        } else if (clazz == Double.TYPE || clazz == Double.class) {
            String val3 = lexer.numberString();
            lexer.nextToken(16);
            return Double.valueOf(Double.parseDouble(val3));
        } else {
            BigDecimal val4 = lexer.decimalValue();
            lexer.nextToken(16);
            if (clazz == Short.TYPE || clazz == Short.class) {
                return Short.valueOf(val4.shortValue());
            }
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                return Byte.valueOf(val4.byteValue());
            }
            return val4;
        }
    }

    public int getFastMatchToken() {
        return 2;
    }
}
