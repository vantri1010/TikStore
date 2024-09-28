package com.danikula.videocache;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class ByteArrayCache implements Cache {
    private volatile boolean completed;
    private volatile byte[] data;

    public ByteArrayCache() {
        this(new byte[0]);
    }

    public ByteArrayCache(byte[] data2) {
        this.data = (byte[]) Preconditions.checkNotNull(data2);
    }

    public int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        if (offset >= ((long) this.data.length)) {
            return -1;
        }
        if (offset <= 2147483647L) {
            return new ByteArrayInputStream(this.data).read(buffer, (int) offset, length);
        }
        throw new IllegalArgumentException("Too long offset for memory cache " + offset);
    }

    public long available() throws ProxyCacheException {
        return (long) this.data.length;
    }

    public void append(byte[] newData, int length) throws ProxyCacheException {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(length >= 0 && length <= newData.length);
        byte[] appendedData = Arrays.copyOf(this.data, this.data.length + length);
        System.arraycopy(newData, 0, appendedData, this.data.length, length);
        this.data = appendedData;
    }

    public void close() throws ProxyCacheException {
    }

    public void complete() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
