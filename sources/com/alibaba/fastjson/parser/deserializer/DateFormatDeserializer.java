package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class DateFormatDeserializer extends AbstractDateDeserializer implements ObjectDeserializer {
    public static final DateFormatDeserializer instance = new DateFormatDeserializer();

    /* access modifiers changed from: protected */
    public <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            return new SimpleDateFormat(strVal);
        }
        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return 4;
    }
}
