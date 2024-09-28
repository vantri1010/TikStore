package com.qiniu.android.dns.dns;

import java.util.concurrent.ExecutorService;

public class DnsUdpResolver extends DnsResolver {
    private static final int DnsUdpPort = 53;

    public DnsUdpResolver(String serverIP) {
        super(serverIP);
    }

    public DnsUdpResolver(String serverIP, int timeout) {
        super(serverIP, timeout);
    }

    public DnsUdpResolver(String serverIP, int recordType, int timeout) {
        super(serverIP, recordType, timeout);
    }

    public DnsUdpResolver(String[] serverIPs, int recordType, int timeout) {
        super(serverIPs, recordType, timeout);
    }

    public DnsUdpResolver(String[] serverIPs, int recordType, int timeout, ExecutorService executorService) {
        super(serverIPs, recordType, timeout, executorService);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x006e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.qiniu.android.dns.dns.DnsResponse request(com.qiniu.android.dns.dns.DnsResolver.RequestCanceller r17, java.lang.String r18, java.lang.String r19, int r20) throws java.io.IOException {
        /*
            r16 = this;
            r1 = r16
            double r2 = java.lang.Math.random()
            r4 = 4679239875398991872(0x40efffe000000000, double:65535.0)
            double r4 = r4 * r2
            int r0 = (int) r4
            short r4 = (short) r0
            com.qiniu.android.dns.dns.DnsRequest r0 = new com.qiniu.android.dns.dns.DnsRequest
            r5 = r19
            r6 = r20
            r0.<init>(r4, r6, r5)
            r7 = r0
            byte[] r8 = r7.toDnsQuestionData()
            java.net.InetAddress r9 = java.net.InetAddress.getByName(r18)
            r10 = 0
            java.net.DatagramSocket r0 = new java.net.DatagramSocket     // Catch:{ all -> 0x0067 }
            r0.<init>()     // Catch:{ all -> 0x0067 }
            r10 = r0
            java.net.DatagramPacket r0 = new java.net.DatagramPacket     // Catch:{ all -> 0x0067 }
            int r11 = r8.length     // Catch:{ all -> 0x0067 }
            r12 = 53
            r0.<init>(r8, r11, r9, r12)     // Catch:{ all -> 0x0067 }
            int r11 = r1.timeout     // Catch:{ all -> 0x0067 }
            int r11 = r11 * 1000
            r10.setSoTimeout(r11)     // Catch:{ all -> 0x0067 }
            r11 = r10
            com.qiniu.android.dns.dns.DnsUdpResolver$1 r12 = new com.qiniu.android.dns.dns.DnsUdpResolver$1     // Catch:{ all -> 0x0067 }
            r12.<init>(r11)     // Catch:{ all -> 0x0067 }
            r13 = r17
            r13.addCancelAction(r12)     // Catch:{ all -> 0x0065 }
            r10.send(r0)     // Catch:{ all -> 0x0065 }
            java.net.DatagramPacket r12 = new java.net.DatagramPacket     // Catch:{ all -> 0x0065 }
            r14 = 1500(0x5dc, float:2.102E-42)
            byte[] r15 = new byte[r14]     // Catch:{ all -> 0x0065 }
            r12.<init>(r15, r14)     // Catch:{ all -> 0x0065 }
            r0 = r12
            r10.receive(r0)     // Catch:{ all -> 0x0065 }
            com.qiniu.android.dns.dns.DnsResponse r12 = new com.qiniu.android.dns.dns.DnsResponse     // Catch:{ all -> 0x0065 }
            r14 = 4
            byte[] r15 = r0.getData()     // Catch:{ all -> 0x0065 }
            r1 = r18
            r12.<init>(r1, r14, r7, r15)     // Catch:{ all -> 0x0063 }
            r10.close()
            return r12
        L_0x0063:
            r0 = move-exception
            goto L_0x006c
        L_0x0065:
            r0 = move-exception
            goto L_0x006a
        L_0x0067:
            r0 = move-exception
            r13 = r17
        L_0x006a:
            r1 = r18
        L_0x006c:
            if (r10 == 0) goto L_0x0071
            r10.close()
        L_0x0071:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qiniu.android.dns.dns.DnsUdpResolver.request(com.qiniu.android.dns.dns.DnsResolver$RequestCanceller, java.lang.String, java.lang.String, int):com.qiniu.android.dns.dns.DnsResponse");
    }
}
