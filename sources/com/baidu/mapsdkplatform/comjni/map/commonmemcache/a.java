package com.baidu.mapsdkplatform.comjni.map.commonmemcache;

public class a {
    private long a;
    private JNICommonMemCache b;

    public a() {
        this.a = 0;
        this.b = null;
        this.b = new JNICommonMemCache();
    }

    public long a() {
        if (this.a == 0) {
            this.a = this.b.Create();
        }
        return this.a;
    }

    public void b() {
        long j = this.a;
        if (j != 0) {
            this.b.Init(j);
        }
    }
}
