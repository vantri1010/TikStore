package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONStreamAware;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class JSONSerializer {
    private List<AfterFilter> afterFilters;
    private List<BeforeFilter> beforeFilters;
    private final SerializeConfig config;
    private SerialContext context;
    private DateFormat dateFormat;
    private String dateFormatPattern;
    private String indent;
    private int indentCount;
    private List<NameFilter> nameFilters;
    private final SerializeWriter out;
    private List<PropertyFilter> propertyFilters;
    private List<PropertyPreFilter> propertyPreFilters;
    private IdentityHashMap<Object, SerialContext> references;
    private List<ValueFilter> valueFilters;

    public JSONSerializer() {
        this(new SerializeWriter(), SerializeConfig.getGlobalInstance());
    }

    public JSONSerializer(SerializeWriter out2) {
        this(out2, SerializeConfig.getGlobalInstance());
    }

    public JSONSerializer(SerializeConfig config2) {
        this(new SerializeWriter(), config2);
    }

    public JSONSerializer(SerializeWriter out2, SerializeConfig config2) {
        this.beforeFilters = null;
        this.afterFilters = null;
        this.propertyFilters = null;
        this.valueFilters = null;
        this.nameFilters = null;
        this.propertyPreFilters = null;
        this.indentCount = 0;
        this.indent = "\t";
        this.references = null;
        this.out = out2;
        this.config = config2;
    }

    public String getDateFormatPattern() {
        DateFormat dateFormat2 = this.dateFormat;
        if (dateFormat2 instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) dateFormat2).toPattern();
        }
        return this.dateFormatPattern;
    }

    public DateFormat getDateFormat() {
        if (this.dateFormat == null && this.dateFormatPattern != null) {
            this.dateFormat = new SimpleDateFormat(this.dateFormatPattern);
        }
        return this.dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat2) {
        this.dateFormat = dateFormat2;
        if (this.dateFormatPattern != null) {
            this.dateFormatPattern = null;
        }
    }

    public void setDateFormat(String dateFormat2) {
        this.dateFormatPattern = dateFormat2;
        if (this.dateFormat != null) {
            this.dateFormat = null;
        }
    }

    public SerialContext getContext() {
        return this.context;
    }

    public void setContext(SerialContext context2) {
        this.context = context2;
    }

    public void setContext(SerialContext parent, Object object, Object fieldName, int features) {
        if (!isEnabled(SerializerFeature.DisableCircularReferenceDetect)) {
            this.context = new SerialContext(parent, object, fieldName, features);
            if (this.references == null) {
                this.references = new IdentityHashMap<>();
            }
            this.references.put(object, this.context);
        }
    }

    public final boolean isWriteClassName(Type fieldType, Object obj) {
        if (!this.out.isEnabled(SerializerFeature.WriteClassName)) {
            return false;
        }
        if (fieldType == null && isEnabled(SerializerFeature.NotWriteRootClassName)) {
            if (this.context.getParent() == null) {
                return false;
            }
        }
        return true;
    }

    public SerialContext getSerialContext(Object object) {
        IdentityHashMap<Object, SerialContext> identityHashMap = this.references;
        if (identityHashMap == null) {
            return null;
        }
        return identityHashMap.get(object);
    }

    public boolean containsReference(Object value) {
        IdentityHashMap<Object, SerialContext> identityHashMap = this.references;
        if (identityHashMap == null) {
            return false;
        }
        return identityHashMap.containsKey(value);
    }

    public void writeReference(Object object) {
        SerialContext context2 = getContext();
        if (object == context2.getObject()) {
            this.out.write("{\"$ref\":\"@\"}");
            return;
        }
        SerialContext parentContext = context2.getParent();
        if (parentContext == null || object != parentContext.getObject()) {
            SerialContext rootContext = context2;
            while (rootContext.getParent() != null) {
                rootContext = rootContext.getParent();
            }
            if (object == rootContext.getObject()) {
                this.out.write("{\"$ref\":\"$\"}");
                return;
            }
            String path = getSerialContext(object).getPath();
            this.out.write("{\"$ref\":\"");
            this.out.write(path);
            this.out.write("\"}");
            return;
        }
        this.out.write("{\"$ref\":\"..\"}");
    }

    public List<ValueFilter> getValueFilters() {
        if (this.valueFilters == null) {
            this.valueFilters = new ArrayList();
        }
        return this.valueFilters;
    }

    public List<ValueFilter> getValueFiltersDirect() {
        return this.valueFilters;
    }

    public int getIndentCount() {
        return this.indentCount;
    }

    public void incrementIndent() {
        this.indentCount++;
    }

    public void decrementIdent() {
        this.indentCount--;
    }

    public void println() {
        this.out.write(10);
        for (int i = 0; i < this.indentCount; i++) {
            this.out.write(this.indent);
        }
    }

    public List<BeforeFilter> getBeforeFilters() {
        if (this.beforeFilters == null) {
            this.beforeFilters = new ArrayList();
        }
        return this.beforeFilters;
    }

    public List<BeforeFilter> getBeforeFiltersDirect() {
        return this.beforeFilters;
    }

    public List<AfterFilter> getAfterFilters() {
        if (this.afterFilters == null) {
            this.afterFilters = new ArrayList();
        }
        return this.afterFilters;
    }

    public List<AfterFilter> getAfterFiltersDirect() {
        return this.afterFilters;
    }

    public List<NameFilter> getNameFilters() {
        if (this.nameFilters == null) {
            this.nameFilters = new ArrayList();
        }
        return this.nameFilters;
    }

    public List<NameFilter> getNameFiltersDirect() {
        return this.nameFilters;
    }

    public List<PropertyPreFilter> getPropertyPreFilters() {
        if (this.propertyPreFilters == null) {
            this.propertyPreFilters = new ArrayList();
        }
        return this.propertyPreFilters;
    }

    public List<PropertyPreFilter> getPropertyPreFiltersDirect() {
        return this.propertyPreFilters;
    }

    public List<PropertyFilter> getPropertyFilters() {
        if (this.propertyFilters == null) {
            this.propertyFilters = new ArrayList();
        }
        return this.propertyFilters;
    }

    public List<PropertyFilter> getPropertyFiltersDirect() {
        return this.propertyFilters;
    }

    public SerializeWriter getWriter() {
        return this.out;
    }

    public String toString() {
        return this.out.toString();
    }

    public void config(SerializerFeature feature, boolean state) {
        this.out.config(feature, state);
    }

    public boolean isEnabled(SerializerFeature feature) {
        return this.out.isEnabled(feature);
    }

    public void writeNull() {
        this.out.writeNull();
    }

    public SerializeConfig getMapping() {
        return this.config;
    }

    public static final void write(Writer out2, Object object) {
        SerializeWriter writer = new SerializeWriter();
        try {
            new JSONSerializer(writer).write(object);
            writer.writeTo(out2);
            writer.close();
        } catch (IOException ex) {
            throw new JSONException(ex.getMessage(), ex);
        } catch (Throwable th) {
            writer.close();
            throw th;
        }
    }

    public static final void write(SerializeWriter out2, Object object) {
        new JSONSerializer(out2).write(object);
    }

    public final void write(Object object) {
        if (object == null) {
            this.out.writeNull();
            return;
        }
        try {
            getObjectWriter(object.getClass()).write(this, object, (Object) null, (Type) null);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    public final void writeWithFieldName(Object object, Object fieldName) {
        writeWithFieldName(object, fieldName, (Type) null, 0);
    }

    /* access modifiers changed from: protected */
    public final void writeKeyValue(char seperator, String key, Object value) {
        if (seperator != 0) {
            this.out.write(seperator);
        }
        this.out.writeFieldName(key);
        write(value);
    }

    public final void writeWithFieldName(Object object, Object fieldName, Type fieldType, int features) {
        if (object == null) {
            try {
                this.out.writeNull();
            } catch (IOException e) {
                throw new JSONException(e.getMessage(), e);
            }
        } else {
            getObjectWriter(object.getClass()).write(this, object, fieldName, fieldType);
        }
    }

    public final void writeWithFormat(Object object, String format) {
        if (object instanceof Date) {
            DateFormat dateFormat2 = getDateFormat();
            if (dateFormat2 == null) {
                dateFormat2 = new SimpleDateFormat(format);
            }
            this.out.writeString(dateFormat2.format((Date) object));
            return;
        }
        write(object);
    }

    public final void write(String text) {
        StringCodec.instance.write(this, text);
    }

    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer writer = (ObjectSerializer) this.config.get(clazz);
        if (writer != null) {
            return writer;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, MapSerializer.instance);
        } else if (List.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, ListSerializer.instance);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, CollectionSerializer.instance);
        } else if (Date.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, DateSerializer.instance);
        } else if (JSONAware.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, JSONAwareSerializer.instance);
        } else if (JSONSerializable.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, JSONSerializableSerializer.instance);
        } else if (JSONStreamAware.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, JSONStreamAwareSerializer.instance);
        } else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
            this.config.put(clazz, EnumSerializer.instance);
        } else if (clazz.isArray()) {
            Class<?> componentType = clazz.getComponentType();
            this.config.put(clazz, new ArraySerializer(componentType, getObjectWriter(componentType)));
        } else if (Throwable.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, new ExceptionSerializer(clazz));
        } else if (TimeZone.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, TimeZoneCodec.instance);
        } else if (Charset.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, CharsetCodec.instance);
        } else if (Enumeration.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, EnumerationSeriliazer.instance);
        } else if (Calendar.class.isAssignableFrom(clazz)) {
            this.config.put(clazz, CalendarCodec.instance);
        } else {
            boolean isCglibProxy = false;
            boolean isJavassistProxy = false;
            Class<?>[] arr$ = clazz.getInterfaces();
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                Class<?> item = arr$[i$];
                if (item.getName().equals("net.sf.cglib.proxy.Factory") || item.getName().equals("org.springframework.cglib.proxy.Factory")) {
                    isCglibProxy = true;
                } else if (item.getName().equals("javassist.util.proxy.ProxyObject")) {
                    isJavassistProxy = true;
                    break;
                } else {
                    i$++;
                }
            }
            isCglibProxy = true;
            if (isCglibProxy || isJavassistProxy) {
                ObjectSerializer superWriter = getObjectWriter(clazz.getSuperclass());
                this.config.put(clazz, superWriter);
                return superWriter;
            } else if (Proxy.isProxyClass(clazz)) {
                SerializeConfig serializeConfig = this.config;
                serializeConfig.put(clazz, serializeConfig.createJavaBeanSerializer(clazz));
            } else {
                SerializeConfig serializeConfig2 = this.config;
                serializeConfig2.put(clazz, serializeConfig2.createJavaBeanSerializer(clazz));
            }
        }
        return (ObjectSerializer) this.config.get(clazz);
    }

    public void close() {
        this.out.close();
    }
}
