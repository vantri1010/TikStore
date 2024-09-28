package com.alibaba.fastjson.serializer;

public final class JSONSerializerContext {
    public static final int DEFAULT_TABLE_SIZE = 128;
    private final Entry[] buckets;
    private final int indexMask;

    public JSONSerializerContext() {
        this(128);
    }

    public JSONSerializerContext(int tableSize) {
        this.indexMask = tableSize - 1;
        this.buckets = new Entry[tableSize];
    }

    public final boolean put(Object o) {
        int hash = System.identityHashCode(o);
        int bucket = this.indexMask & hash;
        for (Entry entry = this.buckets[bucket]; entry != null; entry = entry.next) {
            if (o == entry.object) {
                return true;
            }
        }
        this.buckets[bucket] = new Entry(o, hash, this.buckets[bucket]);
        return false;
    }

    protected static final class Entry {
        public final int hashCode;
        public Entry next;
        public final Object object;

        public Entry(Object object2, int hash, Entry next2) {
            this.object = object2;
            this.next = next2;
            this.hashCode = hash;
        }
    }
}
