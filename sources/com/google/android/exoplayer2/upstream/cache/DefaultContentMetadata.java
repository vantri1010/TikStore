package com.google.android.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultContentMetadata implements ContentMetadata {
    public static final DefaultContentMetadata EMPTY = new DefaultContentMetadata(Collections.emptyMap());
    private static final int MAX_VALUE_LENGTH = 10485760;
    private int hashCode;
    private final Map<String, byte[]> metadata;

    public static DefaultContentMetadata readFromStream(DataInputStream input) throws IOException {
        int size = input.readInt();
        HashMap<String, byte[]> metadata2 = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String name = input.readUTF();
            int valueSize = input.readInt();
            if (valueSize < 0 || valueSize > MAX_VALUE_LENGTH) {
                throw new IOException("Invalid value size: " + valueSize);
            }
            byte[] value = new byte[valueSize];
            input.readFully(value);
            metadata2.put(name, value);
        }
        return new DefaultContentMetadata(metadata2);
    }

    public DefaultContentMetadata() {
        this(Collections.emptyMap());
    }

    private DefaultContentMetadata(Map<String, byte[]> metadata2) {
        this.metadata = Collections.unmodifiableMap(metadata2);
    }

    public DefaultContentMetadata copyWithMutationsApplied(ContentMetadataMutations mutations) {
        Map<String, byte[]> mutatedMetadata = applyMutations(this.metadata, mutations);
        if (isMetadataEqual(this.metadata, mutatedMetadata)) {
            return this;
        }
        return new DefaultContentMetadata(mutatedMetadata);
    }

    public void writeToStream(DataOutputStream output) throws IOException {
        output.writeInt(this.metadata.size());
        for (Map.Entry<String, byte[]> entry : this.metadata.entrySet()) {
            output.writeUTF(entry.getKey());
            byte[] value = entry.getValue();
            output.writeInt(value.length);
            output.write(value);
        }
    }

    public final byte[] get(String name, byte[] defaultValue) {
        if (!this.metadata.containsKey(name)) {
            return defaultValue;
        }
        byte[] bytes = this.metadata.get(name);
        return Arrays.copyOf(bytes, bytes.length);
    }

    public final String get(String name, String defaultValue) {
        if (this.metadata.containsKey(name)) {
            return new String(this.metadata.get(name), Charset.forName("UTF-8"));
        }
        return defaultValue;
    }

    public final long get(String name, long defaultValue) {
        if (this.metadata.containsKey(name)) {
            return ByteBuffer.wrap(this.metadata.get(name)).getLong();
        }
        return defaultValue;
    }

    public final boolean contains(String name) {
        return this.metadata.containsKey(name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return isMetadataEqual(this.metadata, ((DefaultContentMetadata) o).metadata);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 0;
            for (Map.Entry<String, byte[]> entry : this.metadata.entrySet()) {
                result += entry.getKey().hashCode() ^ Arrays.hashCode(entry.getValue());
            }
            this.hashCode = result;
        }
        return this.hashCode;
    }

    private static boolean isMetadataEqual(Map<String, byte[]> first, Map<String, byte[]> second) {
        if (first.size() != second.size()) {
            return false;
        }
        for (Map.Entry<String, byte[]> entry : first.entrySet()) {
            if (!Arrays.equals(entry.getValue(), second.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, byte[]> applyMutations(Map<String, byte[]> otherMetadata, ContentMetadataMutations mutations) {
        HashMap<String, byte[]> metadata2 = new HashMap<>(otherMetadata);
        removeValues(metadata2, mutations.getRemovedValues());
        addValues(metadata2, mutations.getEditedValues());
        return metadata2;
    }

    private static void removeValues(HashMap<String, byte[]> metadata2, List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            metadata2.remove(names.get(i));
        }
    }

    private static void addValues(HashMap<String, byte[]> metadata2, Map<String, Object> values) {
        for (String name : values.keySet()) {
            byte[] bytes = getBytes(values.get(name));
            if (bytes.length <= MAX_VALUE_LENGTH) {
                metadata2.put(name, bytes);
            } else {
                throw new IllegalArgumentException("The size of " + name + " (" + bytes.length + ") is greater than maximum allowed: " + MAX_VALUE_LENGTH);
            }
        }
    }

    private static byte[] getBytes(Object value) {
        if (value instanceof Long) {
            return ByteBuffer.allocate(8).putLong(((Long) value).longValue()).array();
        }
        if (value instanceof String) {
            return ((String) value).getBytes(Charset.forName("UTF-8"));
        }
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        throw new IllegalArgumentException();
    }
}
