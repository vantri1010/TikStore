package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.DateDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

public class CalendarCodec implements ObjectSerializer, ObjectDeserializer {
    public static final CalendarCodec instance = new CalendarCodec();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        serializer.write((Object) ((Calendar) object).getTime());
    }

    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        T deserialze = DateDeserializer.instance.deserialze(parser, type, fieldName);
        if (deserialze instanceof Calendar) {
            return deserialze;
        }
        Date date = (Date) deserialze;
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public int getFastMatchToken() {
        return 2;
    }
}
