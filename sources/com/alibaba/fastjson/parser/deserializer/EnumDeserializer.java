package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EnumDeserializer implements ObjectDeserializer {
    private final Class<?> enumClass;
    private final Map<String, Enum> nameMap = new HashMap();
    private final Map<Integer, Enum> ordinalMap = new HashMap();

    public EnumDeserializer(Class<?> enumClass2) {
        this.enumClass = enumClass2;
        try {
            for (Object value : (Object[]) enumClass2.getMethod("values", new Class[0]).invoke((Object) null, new Object[0])) {
                Enum e = (Enum) value;
                this.ordinalMap.put(Integer.valueOf(e.ordinal()), e);
                this.nameMap.put(e.name(), e);
            }
        } catch (Exception e2) {
            throw new JSONException("init enum values error, " + enumClass2.getName());
        }
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        try {
            JSONLexer lexer = parser.getLexer();
            if (lexer.token() == 2) {
                Object value = Integer.valueOf(lexer.intValue());
                lexer.nextToken(16);
                T e = this.ordinalMap.get(value);
                if (e != null) {
                    return e;
                }
                throw new JSONException("parse enum " + this.enumClass.getName() + " error, value : " + value);
            } else if (lexer.token() == 4) {
                String strVal = lexer.stringVal();
                lexer.nextToken(16);
                if (strVal.length() == 0) {
                    return (Object) null;
                }
                Enum enumR = this.nameMap.get(strVal);
                return Enum.valueOf(this.enumClass, strVal);
            } else if (lexer.token() == 8) {
                lexer.nextToken(16);
                return null;
            } else {
                Object value2 = parser.parse();
                throw new JSONException("parse enum " + this.enumClass.getName() + " error, value : " + value2);
            }
        } catch (JSONException e2) {
            throw e2;
        } catch (Throwable e3) {
            throw new JSONException(e3.getMessage(), e3);
        }
    }

    public int getFastMatchToken() {
        return 2;
    }
}
