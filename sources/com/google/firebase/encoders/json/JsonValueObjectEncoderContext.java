package com.google.firebase.encoders.json;

import android.util.Base64;
import android.util.JsonWriter;
import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.ValueEncoderContext;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/* compiled from: com.google.firebase:firebase-encoders-json@@16.0.0 */
final class JsonValueObjectEncoderContext implements ObjectEncoderContext, ValueEncoderContext {
    private boolean active = true;
    private JsonValueObjectEncoderContext childContext = null;
    private final JsonWriter jsonWriter;
    private final Map<Class<?>, ObjectEncoder<?>> objectEncoders;
    private final Map<Class<?>, ValueEncoder<?>> valueEncoders;

    JsonValueObjectEncoderContext(Writer writer, Map<Class<?>, ObjectEncoder<?>> objectEncoders2, Map<Class<?>, ValueEncoder<?>> valueEncoders2) {
        this.jsonWriter = new JsonWriter(writer);
        this.objectEncoders = objectEncoders2;
        this.valueEncoders = valueEncoders2;
    }

    private JsonValueObjectEncoderContext(JsonValueObjectEncoderContext anotherContext) {
        this.jsonWriter = anotherContext.jsonWriter;
        this.objectEncoders = anotherContext.objectEncoders;
        this.valueEncoders = anotherContext.valueEncoders;
    }

    public JsonValueObjectEncoderContext add(String name, Object o) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.name(name);
        if (o != null) {
            return add(o);
        }
        this.jsonWriter.nullValue();
        return this;
    }

    public JsonValueObjectEncoderContext add(String name, double value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.name(name);
        return add(value);
    }

    public JsonValueObjectEncoderContext add(String name, int value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.name(name);
        return add(value);
    }

    public JsonValueObjectEncoderContext add(String name, long value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.name(name);
        return add(value);
    }

    public JsonValueObjectEncoderContext add(String name, boolean value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.name(name);
        return add(value);
    }

    public ObjectEncoderContext nested(String name) throws IOException {
        maybeUnNest();
        this.childContext = new JsonValueObjectEncoderContext(this);
        this.jsonWriter.name(name);
        this.jsonWriter.beginObject();
        return this.childContext;
    }

    public JsonValueObjectEncoderContext add(String value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.value(value);
        return this;
    }

    public JsonValueObjectEncoderContext add(double value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.value(value);
        return this;
    }

    public JsonValueObjectEncoderContext add(int value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.value((long) value);
        return this;
    }

    public JsonValueObjectEncoderContext add(long value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.value(value);
        return this;
    }

    public JsonValueObjectEncoderContext add(boolean value) throws IOException, EncodingException {
        maybeUnNest();
        this.jsonWriter.value(value);
        return this;
    }

    public JsonValueObjectEncoderContext add(byte[] bytes) throws IOException, EncodingException {
        maybeUnNest();
        if (bytes == null) {
            this.jsonWriter.nullValue();
        } else {
            this.jsonWriter.value(Base64.encodeToString(bytes, 2));
        }
        return this;
    }

    /* access modifiers changed from: package-private */
    public JsonValueObjectEncoderContext add(Object o) throws IOException, EncodingException {
        if (o == null) {
            this.jsonWriter.nullValue();
            return this;
        } else if (o instanceof Number) {
            this.jsonWriter.value((Number) o);
            return this;
        } else {
            int i = 0;
            if (o.getClass().isArray()) {
                if (o instanceof byte[]) {
                    return add((byte[]) o);
                }
                this.jsonWriter.beginArray();
                if (o instanceof int[]) {
                    int[] iArr = (int[]) o;
                    int length = iArr.length;
                    while (i < length) {
                        this.jsonWriter.value((long) iArr[i]);
                        i++;
                    }
                } else if (o instanceof long[]) {
                    long[] jArr = (long[]) o;
                    int length2 = jArr.length;
                    while (i < length2) {
                        add(jArr[i]);
                        i++;
                    }
                } else if (o instanceof double[]) {
                    double[] dArr = (double[]) o;
                    int length3 = dArr.length;
                    while (i < length3) {
                        this.jsonWriter.value(dArr[i]);
                        i++;
                    }
                } else if (o instanceof boolean[]) {
                    boolean[] zArr = (boolean[]) o;
                    int length4 = zArr.length;
                    while (i < length4) {
                        this.jsonWriter.value(zArr[i]);
                        i++;
                    }
                } else if (o instanceof Number[]) {
                    Number[] numberArr = (Number[]) o;
                    int length5 = numberArr.length;
                    while (i < length5) {
                        add((Object) numberArr[i]);
                        i++;
                    }
                } else {
                    Object[] objArr = (Object[]) o;
                    int length6 = objArr.length;
                    while (i < length6) {
                        add(objArr[i]);
                        i++;
                    }
                }
                this.jsonWriter.endArray();
                return this;
            } else if (o instanceof Collection) {
                this.jsonWriter.beginArray();
                for (Object elem : (Collection) o) {
                    add(elem);
                }
                this.jsonWriter.endArray();
                return this;
            } else if (o instanceof Map) {
                this.jsonWriter.beginObject();
                for (Map.Entry<Object, Object> entry : ((Map) o).entrySet()) {
                    Object key = entry.getKey();
                    try {
                        add((String) key, entry.getValue());
                    } catch (ClassCastException ex) {
                        throw new EncodingException(String.format("Only String keys are currently supported in maps, got %s of type %s instead.", new Object[]{key, key.getClass()}), ex);
                    }
                }
                this.jsonWriter.endObject();
                return this;
            } else {
                ObjectEncoder<Object> objectEncoder = this.objectEncoders.get(o.getClass());
                if (objectEncoder != null) {
                    this.jsonWriter.beginObject();
                    objectEncoder.encode(o, this);
                    this.jsonWriter.endObject();
                    return this;
                }
                ValueEncoder<Object> valueEncoder = this.valueEncoders.get(o.getClass());
                if (valueEncoder != null) {
                    valueEncoder.encode(o, this);
                    return this;
                } else if (o instanceof Enum) {
                    add(((Enum) o).name());
                    return this;
                } else {
                    throw new EncodingException("Couldn't find encoder for type " + o.getClass().getCanonicalName());
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void close() throws IOException {
        maybeUnNest();
        this.jsonWriter.flush();
    }

    private void maybeUnNest() throws IOException {
        if (this.active) {
            JsonValueObjectEncoderContext jsonValueObjectEncoderContext = this.childContext;
            if (jsonValueObjectEncoderContext != null) {
                jsonValueObjectEncoderContext.maybeUnNest();
                this.childContext.active = false;
                this.childContext = null;
                this.jsonWriter.endObject();
                return;
            }
            return;
        }
        throw new IllegalStateException("Parent context used since this context was created. Cannot use this context anymore.");
    }
}
