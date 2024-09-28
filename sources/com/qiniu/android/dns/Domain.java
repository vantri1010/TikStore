package com.qiniu.android.dns;

public final class Domain {
    public final String domain;
    public final boolean hasCname;
    public final boolean hostsFirst;
    public final int maxTtl;

    public Domain(String domain2, boolean hasCname2, boolean hostsFirst2) {
        this(domain2, hasCname2, hostsFirst2, 0);
    }

    public Domain(String domain2, boolean hasCname2, boolean hostsFirst2, int maxTtl2) {
        this.domain = domain2;
        this.hasCname = hasCname2;
        this.hostsFirst = hostsFirst2;
        this.maxTtl = maxTtl2;
    }

    public Domain(String domain2, boolean hasCname2) {
        this(domain2, hasCname2, false, 0);
    }

    public Domain(String domain2) {
        this(domain2, false, false, 0);
    }
}
