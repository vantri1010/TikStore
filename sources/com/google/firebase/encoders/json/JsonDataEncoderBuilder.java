package com.google.firebase.encoders.json;

import com.google.firebase.encoders.DataEncoder;
import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.ValueEncoderContext;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* compiled from: com.google.firebase:firebase-encoders-json@@16.0.0 */
public final class JsonDataEncoderBuilder implements EncoderConfig<JsonDataEncoderBuilder> {
    private static final ValueEncoder<Boolean> BOOLEAN_ENCODER = JsonDataEncoderBuilder$$Lambda$4.lambdaFactory$();
    private static final ValueEncoder<String> STRING_ENCODER = JsonDataEncoderBuilder$$Lambda$1.lambdaFactory$();
    private static final TimestampEncoder TIMESTAMP_ENCODER = new TimestampEncoder();
    /* access modifiers changed from: private */
    public final Map<Class<?>, ObjectEncoder<?>> objectEncoders = new HashMap();
    /* access modifiers changed from: private */
    public final Map<Class<?>, ValueEncoder<?>> valueEncoders = new HashMap();

    /* compiled from: com.google.firebase:firebase-encoders-json@@16.0.0 */
    private static final class TimestampEncoder implements ValueEncoder<Date> {
        private static final DateFormat rfc339;

        private TimestampEncoder() {
        }

        static {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            rfc339 = simpleDateFormat;
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public void encode(Date o, ValueEncoderContext ctx) throws EncodingException, IOException {
            ctx.add(rfc339.format(o));
        }
    }

    public JsonDataEncoderBuilder() {
        registerEncoder(String.class, (ValueEncoder) STRING_ENCODER);
        registerEncoder(Boolean.class, (ValueEncoder) BOOLEAN_ENCODER);
        registerEncoder(Date.class, (ValueEncoder) TIMESTAMP_ENCODER);
    }

    public <T> JsonDataEncoderBuilder registerEncoder(Class<T> clazz, ObjectEncoder<? super T> objectEncoder) {
        if (!this.objectEncoders.containsKey(clazz)) {
            this.objectEncoders.put(clazz, objectEncoder);
            return this;
        }
        throw new IllegalArgumentException("Encoder already registered for " + clazz.getName());
    }

    public <T> JsonDataEncoderBuilder registerEncoder(Class<T> clazz, ValueEncoder<? super T> encoder) {
        if (!this.valueEncoders.containsKey(clazz)) {
            this.valueEncoders.put(clazz, encoder);
            return this;
        }
        throw new IllegalArgumentException("Encoder already registered for " + clazz.getName());
    }

    public JsonDataEncoderBuilder configureWith(Configurator config) {
        config.configure(this);
        return this;
    }

    public DataEncoder build() {
        return new DataEncoder() {
            public void encode(Object o, Writer writer) throws IOException, EncodingException {
                JsonValueObjectEncoderContext encoderContext = new JsonValueObjectEncoderContext(writer, JsonDataEncoderBuilder.this.objectEncoders, JsonDataEncoderBuilder.this.valueEncoders);
                encoderContext.add(o);
                encoderContext.close();
            }

            public String encode(Object o) throws EncodingException {
                StringWriter stringWriter = new StringWriter();
                try {
                    encode(o, stringWriter);
                } catch (IOException e) {
                }
                return stringWriter.toString();
            }
        };
    }
}
