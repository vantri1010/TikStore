package com.qiniu.android.dns;

import com.qiniu.android.dns.local.Hosts;
import com.qiniu.android.dns.util.LruCache;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public final class DnsManager {
    private final LruCache<String, Record[]> cache;
    private final Hosts hosts;
    private volatile int index;
    private volatile NetworkInfo info;
    public QueryErrorHandler queryErrorHandler;
    private final IResolver[] resolvers;
    private final RecordSorter sorter;

    public interface QueryErrorHandler {
        void queryError(Exception exc, String str);
    }

    public DnsManager(NetworkInfo info2, IResolver[] resolvers2) {
        this(info2, resolvers2, (RecordSorter) null);
    }

    public DnsManager(NetworkInfo info2, IResolver[] resolvers2, RecordSorter sorter2) {
        this.hosts = new Hosts();
        this.info = null;
        this.index = 0;
        this.info = info2 == null ? NetworkInfo.normal : info2;
        this.resolvers = (IResolver[]) resolvers2.clone();
        this.cache = new LruCache<>();
        this.sorter = sorter2 == null ? new DummySorter() : sorter2;
    }

    private static Record[] trimCname(Record[] records) {
        ArrayList<Record> a = new ArrayList<>(records.length);
        for (Record r : records) {
            if (r != null && (r.isA() || r.isAAAA())) {
                a.add(r);
            }
        }
        return (Record[]) a.toArray(new Record[a.size()]);
    }

    private static String[] records2Ip(Record[] records) {
        if (records == null || records.length == 0) {
            return null;
        }
        ArrayList<String> a = new ArrayList<>(records.length);
        for (Record r : records) {
            a.add(r.value);
        }
        if (a.size() == 0) {
            return null;
        }
        return (String[]) a.toArray(new String[a.size()]);
    }

    private static Record[] filterInvalidRecords(Record[] records) {
        if (records == null || records.length == 0) {
            return null;
        }
        ArrayList<Record> ret = new ArrayList<>(records.length);
        for (Record r : records) {
            if (r != null && r.value != null && r.value.length() > 0 && !r.isExpired()) {
                ret.add(r);
            }
        }
        if (ret.size() == 0) {
            return null;
        }
        return (Record[]) ret.toArray(new Record[ret.size()]);
    }

    public static boolean validIP(String ip) {
        if (ip == null || ip.length() < 7 || ip.length() > 15 || ip.contains("-")) {
            return false;
        }
        try {
            int y = ip.indexOf(46);
            if (y != -1 && Integer.parseInt(ip.substring(0, y)) > 255) {
                return false;
            }
            int y2 = y + 1;
            int x = ip.indexOf(46, y2);
            if (x != -1 && Integer.parseInt(ip.substring(y2, x)) > 255) {
                return false;
            }
            int x2 = x + 1;
            int y3 = ip.indexOf(46, x2);
            if (y3 == -1 || Integer.parseInt(ip.substring(x2, y3)) <= 255 || Integer.parseInt(ip.substring(y3 + 1, ip.length() - 1)) <= 255 || ip.charAt(ip.length() - 1) == '.') {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean needHttpDns() {
        try {
            String id = TimeZone.getDefault().getID();
            if ("Asia/Shanghai".equals(id) || "Asia/Chongqing".equals(id) || "Asia/Harbin".equals(id) || "Asia/Urumqi".equals(id)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Record[] queryRecords(String domain) throws IOException {
        return queryRecords(new Domain(domain));
    }

    public Record[] queryRecords(Domain domain) throws IOException {
        if (domain == null) {
            throw new IOException("null domain");
        } else if (domain.domain == null || domain.domain.trim().length() == 0) {
            throw new IOException("empty domain " + domain.domain);
        } else if (validIP(domain.domain)) {
            return new Record[]{new Record(domain.domain, 1, -1, new Date().getTime(), 0)};
        } else {
            return this.sorter.sort(queryRecordInternal(domain));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0050, code lost:
        r0 = null;
        r1 = null;
        r3 = r12.index;
        r4 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.qiniu.android.dns.Record[] queryRecordInternal(com.qiniu.android.dns.Domain r13) throws java.io.IOException {
        /*
            r12 = this;
            boolean r0 = r13.hostsFirst
            if (r0 == 0) goto L_0x0016
            com.qiniu.android.dns.local.Hosts r0 = r12.hosts
            com.qiniu.android.dns.NetworkInfo r1 = r12.info
            com.qiniu.android.dns.Record[] r0 = r0.query(r13, r1)
            com.qiniu.android.dns.Record[] r0 = filterInvalidRecords(r0)
            if (r0 == 0) goto L_0x0016
            int r1 = r0.length
            if (r1 <= 0) goto L_0x0016
            return r0
        L_0x0016:
            com.qiniu.android.dns.util.LruCache<java.lang.String, com.qiniu.android.dns.Record[]> r0 = r12.cache
            monitor-enter(r0)
            com.qiniu.android.dns.NetworkInfo r1 = r12.info     // Catch:{ all -> 0x011a }
            com.qiniu.android.dns.NetworkInfo r2 = com.qiniu.android.dns.NetworkInfo.normal     // Catch:{ all -> 0x011a }
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x011a }
            r2 = 0
            if (r1 == 0) goto L_0x0039
            boolean r1 = com.qiniu.android.dns.Network.isNetworkChanged()     // Catch:{ all -> 0x011a }
            if (r1 == 0) goto L_0x0039
            com.qiniu.android.dns.util.LruCache<java.lang.String, com.qiniu.android.dns.Record[]> r1 = r12.cache     // Catch:{ all -> 0x011a }
            r1.clear()     // Catch:{ all -> 0x011a }
            com.qiniu.android.dns.IResolver[] r1 = r12.resolvers     // Catch:{ all -> 0x011a }
            monitor-enter(r1)     // Catch:{ all -> 0x011a }
            r12.index = r2     // Catch:{ all -> 0x0036 }
            monitor-exit(r1)     // Catch:{ all -> 0x0036 }
            goto L_0x004f
        L_0x0036:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0036 }
            throw r2     // Catch:{ all -> 0x011a }
        L_0x0039:
            com.qiniu.android.dns.util.LruCache<java.lang.String, com.qiniu.android.dns.Record[]> r1 = r12.cache     // Catch:{ all -> 0x011a }
            java.lang.String r3 = r13.domain     // Catch:{ all -> 0x011a }
            java.lang.Object r1 = r1.get(r3)     // Catch:{ all -> 0x011a }
            com.qiniu.android.dns.Record[] r1 = (com.qiniu.android.dns.Record[]) r1     // Catch:{ all -> 0x011a }
            com.qiniu.android.dns.Record[] r3 = filterInvalidRecords(r1)     // Catch:{ all -> 0x011a }
            r1 = r3
            if (r1 == 0) goto L_0x004f
            int r3 = r1.length     // Catch:{ all -> 0x011a }
            if (r3 <= 0) goto L_0x004f
            monitor-exit(r0)     // Catch:{ all -> 0x011a }
            return r1
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x011a }
            r0 = 0
            r1 = 0
            int r3 = r12.index
            r4 = 0
        L_0x0055:
            com.qiniu.android.dns.IResolver[] r5 = r12.resolvers
            int r6 = r5.length
            if (r4 >= r6) goto L_0x00cc
            int r6 = r3 + r4
            int r5 = r5.length
            int r6 = r6 % r5
            com.qiniu.android.dns.NetworkInfo r5 = r12.info
            java.lang.String r7 = com.qiniu.android.dns.Network.getIp()
            com.qiniu.android.dns.IResolver[] r8 = r12.resolvers     // Catch:{ DomainNotOwn -> 0x00c7, IOException -> 0x008a, Exception -> 0x0070 }
            r8 = r8[r6]     // Catch:{ DomainNotOwn -> 0x00c7, IOException -> 0x008a, Exception -> 0x0070 }
            com.qiniu.android.dns.NetworkInfo r9 = r12.info     // Catch:{ DomainNotOwn -> 0x00c7, IOException -> 0x008a, Exception -> 0x0070 }
            com.qiniu.android.dns.Record[] r8 = r8.resolve(r13, r9)     // Catch:{ DomainNotOwn -> 0x00c7, IOException -> 0x008a, Exception -> 0x0070 }
            r0 = r8
            goto L_0x0098
        L_0x0070:
            r8 = move-exception
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 9
            if (r9 < r10) goto L_0x007d
            java.io.IOException r9 = new java.io.IOException
            r9.<init>(r8)
            r1 = r9
        L_0x007d:
            r8.printStackTrace()
            com.qiniu.android.dns.DnsManager$QueryErrorHandler r9 = r12.queryErrorHandler
            if (r9 == 0) goto L_0x0099
            java.lang.String r10 = r13.domain
            r9.queryError(r8, r10)
            goto L_0x0099
        L_0x008a:
            r8 = move-exception
            r1 = r8
            r8.printStackTrace()
            com.qiniu.android.dns.DnsManager$QueryErrorHandler r9 = r12.queryErrorHandler
            if (r9 == 0) goto L_0x0098
            java.lang.String r10 = r13.domain
            r9.queryError(r8, r10)
        L_0x0098:
        L_0x0099:
            java.lang.String r8 = com.qiniu.android.dns.Network.getIp()
            com.qiniu.android.dns.NetworkInfo r9 = r12.info
            if (r9 != r5) goto L_0x00cc
            if (r0 == 0) goto L_0x00a6
            int r9 = r0.length
            if (r9 != 0) goto L_0x00cc
        L_0x00a6:
            boolean r9 = r7.equals(r8)
            if (r9 == 0) goto L_0x00cc
            com.qiniu.android.dns.IResolver[] r9 = r12.resolvers
            monitor-enter(r9)
            int r10 = r12.index     // Catch:{ all -> 0x00c4 }
            if (r10 != r3) goto L_0x00c2
            int r10 = r12.index     // Catch:{ all -> 0x00c4 }
            int r10 = r10 + 1
            r12.index = r10     // Catch:{ all -> 0x00c4 }
            int r10 = r12.index     // Catch:{ all -> 0x00c4 }
            com.qiniu.android.dns.IResolver[] r11 = r12.resolvers     // Catch:{ all -> 0x00c4 }
            int r11 = r11.length     // Catch:{ all -> 0x00c4 }
            if (r10 != r11) goto L_0x00c2
            r12.index = r2     // Catch:{ all -> 0x00c4 }
        L_0x00c2:
            monitor-exit(r9)     // Catch:{ all -> 0x00c4 }
            goto L_0x00c9
        L_0x00c4:
            r2 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00c4 }
            throw r2
        L_0x00c7:
            r8 = move-exception
        L_0x00c9:
            int r4 = r4 + 1
            goto L_0x0055
        L_0x00cc:
            if (r0 == 0) goto L_0x00f0
            int r2 = r0.length
            if (r2 != 0) goto L_0x00d2
            goto L_0x00f0
        L_0x00d2:
            com.qiniu.android.dns.Record[] r0 = trimCname(r0)
            int r2 = r0.length
            if (r2 == 0) goto L_0x00e8
            com.qiniu.android.dns.util.LruCache<java.lang.String, com.qiniu.android.dns.Record[]> r2 = r12.cache
            monitor-enter(r2)
            com.qiniu.android.dns.util.LruCache<java.lang.String, com.qiniu.android.dns.Record[]> r4 = r12.cache     // Catch:{ all -> 0x00e5 }
            java.lang.String r5 = r13.domain     // Catch:{ all -> 0x00e5 }
            r4.put(r5, r0)     // Catch:{ all -> 0x00e5 }
            monitor-exit(r2)     // Catch:{ all -> 0x00e5 }
            return r0
        L_0x00e5:
            r4 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00e5 }
            throw r4
        L_0x00e8:
            java.net.UnknownHostException r2 = new java.net.UnknownHostException
            java.lang.String r4 = "no A/AAAA records"
            r2.<init>(r4)
            throw r2
        L_0x00f0:
            boolean r2 = r13.hostsFirst
            if (r2 != 0) goto L_0x0106
            com.qiniu.android.dns.local.Hosts r2 = r12.hosts
            com.qiniu.android.dns.NetworkInfo r4 = r12.info
            com.qiniu.android.dns.Record[] r2 = r2.query(r13, r4)
            com.qiniu.android.dns.Record[] r2 = filterInvalidRecords(r2)
            if (r2 == 0) goto L_0x0106
            int r4 = r2.length
            if (r4 == 0) goto L_0x0106
            return r2
        L_0x0106:
            if (r1 != 0) goto L_0x0119
            java.net.UnknownHostException r2 = new java.net.UnknownHostException
            java.lang.String r4 = r13.domain
            r2.<init>(r4)
            com.qiniu.android.dns.DnsManager$QueryErrorHandler r4 = r12.queryErrorHandler
            if (r4 == 0) goto L_0x0118
            java.lang.String r5 = r13.domain
            r4.queryError(r2, r5)
        L_0x0118:
            throw r2
        L_0x0119:
            throw r1
        L_0x011a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x011a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qiniu.android.dns.DnsManager.queryRecordInternal(com.qiniu.android.dns.Domain):com.qiniu.android.dns.Record[]");
    }

    public InetAddress[] queryInetAdress(Domain domain) throws IOException {
        String[] ips = records2Ip(queryRecords(domain));
        if (ips == null || ips.length == 0) {
            return null;
        }
        InetAddress[] addresses = new InetAddress[ips.length];
        for (int i = 0; i < ips.length; i++) {
            addresses[i] = InetAddress.getByName(ips[i]);
        }
        return addresses;
    }

    public void onNetworkChange(NetworkInfo info2) {
        clearCache();
        this.info = info2 == null ? NetworkInfo.normal : info2;
        synchronized (this.resolvers) {
            this.index = 0;
        }
    }

    private void clearCache() {
        synchronized (this.cache) {
            this.cache.clear();
        }
    }

    public DnsManager putHosts(String domain, Record record, int provider) {
        this.hosts.put(domain, new Hosts.Value(new Record(record.value, record.type, record.ttl, record.timeStamp, 1, record.server), provider));
        return this;
    }

    public DnsManager putHosts(String domain, int type, String ip, int provider) {
        putHosts(domain, new Record(ip, type, -1, new Date().getTime() / 1000, 1), provider);
        return this;
    }

    public DnsManager putHosts(String domain, int type, String ip) {
        putHosts(domain, type, ip, 0);
        return this;
    }

    public DnsManager putHosts(String domain, String ipv4) {
        putHosts(domain, 1, ipv4);
        return this;
    }

    private static class DummySorter implements RecordSorter {
        private AtomicInteger pos;

        private DummySorter() {
            this.pos = new AtomicInteger();
        }

        public Record[] sort(Record[] records) {
            return records;
        }
    }
}
