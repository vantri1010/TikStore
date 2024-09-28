package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONScanner;
import java.lang.reflect.Type;
import java.sql.Time;

public class TimeDeserializer implements ObjectDeserializer {
    public static final TimeDeserializer instance = new TimeDeserializer();

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        long longVal;
        JSONLexer lexer = parser.getLexer();
        if (lexer.token() == 16) {
            lexer.nextToken(4);
            if (lexer.token() == 4) {
                lexer.nextTokenWithColon(2);
                if (lexer.token() == 2) {
                    long time = lexer.longValue();
                    lexer.nextToken(13);
                    if (lexer.token() == 13) {
                        lexer.nextToken(16);
                        return new Time(time);
                    }
                    throw new JSONException("syntax error");
                }
                throw new JSONException("syntax error");
            }
            throw new JSONException("syntax error");
        }
        Object val = parser.parse();
        if (val == null) {
            return null;
        }
        if (val instanceof Time) {
            return val;
        }
        if (val instanceof Number) {
            return new Time(((Number) val).longValue());
        }
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            JSONScanner dateLexer = new JSONScanner(strVal);
            if (dateLexer.scanISO8601DateIfMatch()) {
                longVal = dateLexer.getCalendar().getTimeInMillis();
            } else {
                longVal = Long.parseLong(strVal);
            }
            dateLexer.close();
            return new Time(longVal);
        }
        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return 2;
    }
}
