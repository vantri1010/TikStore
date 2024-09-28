package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONScanner;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

public class SqlDateDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {
    public static final SqlDateDeserializer instance = new SqlDateDeserializer();

    /* access modifiers changed from: protected */
    public <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
        long longVal;
        if (val == null) {
            return null;
        }
        if (val instanceof Date) {
            return new java.sql.Date(((Date) val).getTime());
        }
        if (val instanceof Number) {
            return new java.sql.Date(((Number) val).longValue());
        }
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            JSONScanner dateLexer = new JSONScanner(strVal);
            try {
                if (dateLexer.scanISO8601DateIfMatch()) {
                    longVal = dateLexer.getCalendar().getTimeInMillis();
                } else {
                    java.sql.Date sqlDate = new java.sql.Date(parser.getDateFormat().parse(strVal).getTime());
                    dateLexer.close();
                    return sqlDate;
                }
            } catch (ParseException e) {
                longVal = Long.parseLong(strVal);
            } catch (Throwable th) {
                dateLexer.close();
                throw th;
            }
            dateLexer.close();
            return new java.sql.Date(longVal);
        }
        throw new JSONException("parse error : " + val);
    }

    public int getFastMatchToken() {
        return 2;
    }
}
