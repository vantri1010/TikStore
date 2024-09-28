package com.qiniu.android.dns.local;

import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.Record;
import com.qiniu.android.dns.dns.DnsUdpResolver;
import java.io.IOException;

public final class HijackingDetectWrapper implements IResolver {
    private final DnsUdpResolver resolver;

    public HijackingDetectWrapper(DnsUdpResolver r) {
        this.resolver = r;
    }

    public Record[] resolve(Domain domain, NetworkInfo info) throws IOException {
        Record[] records = this.resolver.resolve(domain, info);
        int i = 0;
        if (domain.hasCname) {
            boolean cname = false;
            String server = null;
            int length = records.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                Record r = records[i2];
                if (r.isCname()) {
                    cname = true;
                    server = r.server;
                    break;
                }
                i2++;
            }
            if (!cname) {
                throw new DnshijackingException(domain.domain, server);
            }
        }
        if (domain.maxTtl != 0) {
            int length2 = records.length;
            while (i < length2) {
                Record r2 = records[i];
                if (r2.isCname() || r2.ttl <= domain.maxTtl) {
                    i++;
                } else {
                    throw new DnshijackingException(domain.domain, r2.server, r2.ttl);
                }
            }
        }
        return records;
    }
}
