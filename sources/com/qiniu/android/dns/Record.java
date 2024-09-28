package com.qiniu.android.dns;

import java.util.Date;
import java.util.Locale;

public final class Record {
    public static final int TTL_Forever = -1;
    public static final int TTL_MIN_SECONDS = 60;
    public static final int TYPE_A = 1;
    public static final int TYPE_AAAA = 28;
    public static final int TYPE_CNAME = 5;
    public static final int TYPE_TXT = 16;
    public final String server;
    public final int source;
    public final long timeStamp;
    public final int ttl;
    public final int type;
    public final String value;

    public static class Source {
        public static final int Custom = 1;
        public static final int DnspodEnterprise = 2;
        public static final int Doh = 5;
        public static final int System = 3;
        public static final int Udp = 4;
        public static final int Unknown = 0;
    }

    public Record(String value2, int type2, int ttl2) {
        this.value = value2;
        this.type = type2;
        this.ttl = ttl2;
        this.timeStamp = new Date().getTime() / 1000;
        this.source = 0;
        this.server = null;
    }

    public Record(String value2, int type2, int ttl2, long timeStamp2, int source2) {
        this.value = value2;
        this.type = type2;
        this.ttl = Math.max(ttl2, 60);
        this.timeStamp = timeStamp2;
        this.source = source2;
        this.server = null;
    }

    public Record(String value2, int type2, int ttl2, long timeStamp2, int source2, String server2) {
        this.value = value2;
        this.type = type2;
        this.ttl = ttl2 >= 60 ? ttl2 : 60;
        this.timeStamp = timeStamp2;
        this.source = source2;
        this.server = server2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Record)) {
            return false;
        }
        Record another = (Record) o;
        if (this.value.equals(another.value) && this.type == another.type && this.ttl == another.ttl && this.timeStamp == another.timeStamp) {
            return true;
        }
        return false;
    }

    public boolean isA() {
        return this.type == 1;
    }

    public boolean isAAAA() {
        return this.type == 28;
    }

    public boolean isCname() {
        return this.type == 5;
    }

    public boolean isExpired() {
        return isExpired(System.currentTimeMillis() / 1000);
    }

    public boolean isExpired(long time) {
        int i = this.ttl;
        if (i != -1 && this.timeStamp + ((long) i) < time) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "{type:%s, value:%s, source:%s, server:%s, timestamp:%d, ttl:%d}", new Object[]{Integer.valueOf(this.type), this.value, Integer.valueOf(this.source), this.server, Long.valueOf(this.timeStamp), Integer.valueOf(this.ttl)});
    }
}
