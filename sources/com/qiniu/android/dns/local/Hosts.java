package com.qiniu.android.dns.local;

import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Hosts {
    private final Map<String, LinkedList<Value>> hosts = new ConcurrentHashMap();

    public synchronized Record[] query(Domain domain, NetworkInfo info) {
        LinkedList<Value> values = this.hosts.get(domain.domain);
        if (values != null) {
            if (!values.isEmpty()) {
                if (values.size() > 1) {
                    values.remove(0);
                    values.add(values.get(0));
                }
                return toRecords(filter(values, info));
            }
        }
        return null;
    }

    private LinkedList<Value> filter(LinkedList<Value> origin, NetworkInfo info) {
        if (origin == null) {
            return null;
        }
        LinkedList<Value> normal = new LinkedList<>();
        LinkedList<Value> special = new LinkedList<>();
        Iterator it = origin.iterator();
        while (it.hasNext()) {
            Value v = (Value) it.next();
            if (v.provider == 0) {
                normal.add(v);
            } else if (v.provider == info.provider) {
                special.add(v);
            }
        }
        if (special.size() != 0) {
            return special;
        }
        return normal;
    }

    private Record[] toRecords(LinkedList<Value> vals) {
        if (vals == null) {
            return null;
        }
        int size = vals.size();
        List<Record> records = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Value v = vals.get(i);
            if (!(v == null || v.record == null || v.record.value == null)) {
                records.add(v.record);
            }
        }
        return (Record[]) records.toArray(new Record[records.size()]);
    }

    public synchronized Hosts put(String domain, Value val) {
        LinkedList<Value> vals = this.hosts.get(domain);
        if (vals == null) {
            vals = new LinkedList<>();
        }
        vals.add(val);
        this.hosts.put(domain, vals);
        return this;
    }

    public Hosts put(String domain, Record record) {
        put(domain, new Value(record));
        return this;
    }

    public static class Value {
        public final int provider;
        public final Record record;

        public Value(Record record2, int provider2) {
            this.record = record2;
            this.provider = provider2;
        }

        public Value(Record record2) {
            this(record2, 0);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Value)) {
                return false;
            }
            Value another = (Value) o;
            Record record2 = this.record;
            Record record3 = another.record;
            if (record2 == record3) {
                return true;
            }
            if (record2 == null || record3 == null || !record2.value.equals(another.record.value) || this.provider != another.provider) {
                return false;
            }
            return true;
        }
    }
}
