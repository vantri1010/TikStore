package com.alibaba.fastjson;

import androidx.core.view.PointerIconCompat;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

public class JSONWriter implements Closeable, Flushable {
    private JSONStreamContext context;
    private JSONSerializer serializer;
    private SerializeWriter writer;

    public JSONWriter(Writer out) {
        SerializeWriter serializeWriter = new SerializeWriter(out);
        this.writer = serializeWriter;
        this.serializer = new JSONSerializer(serializeWriter);
    }

    public void config(SerializerFeature feature, boolean state) {
        this.writer.config(feature, state);
    }

    public void startObject() {
        if (this.context != null) {
            beginStructure();
        }
        this.context = new JSONStreamContext(this.context, 1001);
        this.writer.write('{');
    }

    public void endObject() {
        this.writer.write('}');
        endStructure();
    }

    public void writeKey(String key) {
        writeObject(key);
    }

    public void writeValue(Object object) {
        writeObject(object);
    }

    public void writeObject(String object) {
        beforeWrite();
        this.serializer.write(object);
        afterWriter();
    }

    public void writeObject(Object object) {
        beforeWrite();
        this.serializer.write(object);
        afterWriter();
    }

    public void startArray() {
        if (this.context != null) {
            beginStructure();
        }
        this.context = new JSONStreamContext(this.context, PointerIconCompat.TYPE_WAIT);
        this.writer.write('[');
    }

    private void beginStructure() {
        int state = this.context.getState();
        switch (state) {
            case 1001:
            case PointerIconCompat.TYPE_WAIT:
                return;
            case 1002:
                this.writer.write(':');
                return;
            case 1005:
                this.writer.write(',');
                return;
            default:
                throw new JSONException("illegal state : " + state);
        }
    }

    public void endArray() {
        this.writer.write(']');
        endStructure();
    }

    private void endStructure() {
        JSONStreamContext parent = this.context.getParent();
        this.context = parent;
        if (parent != null) {
            int state = parent.getState();
            int newState = -1;
            if (state == 1001) {
                newState = 1002;
            } else if (state == 1002) {
                newState = 1003;
            } else if (state == 1004) {
                newState = 1005;
            }
            if (newState != -1) {
                this.context.setState(newState);
            }
        }
    }

    private void beforeWrite() {
        JSONStreamContext jSONStreamContext = this.context;
        if (jSONStreamContext != null) {
            int state = jSONStreamContext.getState();
            if (state == 1002) {
                this.writer.write(':');
            } else if (state == 1003) {
                this.writer.write(',');
            } else if (state == 1005) {
                this.writer.write(',');
            }
        }
    }

    private void afterWriter() {
        JSONStreamContext jSONStreamContext = this.context;
        if (jSONStreamContext != null) {
            int newState = -1;
            switch (jSONStreamContext.getState()) {
                case 1001:
                case 1003:
                    newState = 1002;
                    break;
                case 1002:
                    newState = 1003;
                    break;
                case PointerIconCompat.TYPE_WAIT:
                    newState = 1005;
                    break;
            }
            if (newState != -1) {
                this.context.setState(newState);
            }
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
