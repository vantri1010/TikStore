package com.qiniu.android.dns.dns;

import com.king.zxing.util.LogUtils;
import com.qiniu.android.dns.Record;
import java.io.IOException;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kotlin.UByte;

class DnsResponse extends DnsMessage {
    private int aa;
    private List<Record> additionalArray;
    private List<Record> answerArray;
    private List<Record> authorityArray;
    private int rCode;
    private byte[] recordData;
    private DnsRequest request;
    private String server;
    private int source;
    private long timestamp;

    DnsResponse(String server2, int source2, DnsRequest request2, byte[] recordData2) throws IOException {
        if (recordData2 == null || recordData2.length == 0) {
            throw new IOException("response data is empty");
        }
        this.server = server2;
        this.source = source2;
        this.request = request2;
        this.recordData = recordData2;
        this.timestamp = new Date().getTime() / 1000;
        parse();
    }

    private void parse() throws IOException {
        if (this.recordData.length >= 12) {
            parseHeader();
            int index = parseQuestion();
            RecordResource answer = new RecordResource("answer", readRecordDataInt16(6), index);
            parseResourceRecord(answer);
            this.answerArray = answer.records;
            int index2 = index + answer.length;
            RecordResource authority = new RecordResource("authority", readRecordDataInt16(8), index2);
            parseResourceRecord(authority);
            this.authorityArray = authority.records;
            RecordResource additional = new RecordResource("additional", readRecordDataInt16(10), index2 + authority.length);
            parseResourceRecord(additional);
            this.additionalArray = additional.records;
            return;
        }
        throw new IOException("response data too small");
    }

    private void parseHeader() throws IOException {
        this.messageId = readRecordDataInt16(0);
        if (this.messageId == this.request.messageId) {
            int field0 = readRecordDataInt8(2);
            if ((readRecordDataInt8(2) & 128) != 0) {
                this.opCode = (field0 >> 3) & 7;
                this.aa = (field0 >> 2) & 1;
                this.rd = field0 & 1;
                int field1 = readRecordDataInt8(3);
                this.ra = (field1 >> 7) & 1;
                this.rCode = field1 & 15;
                return;
            }
            throw new IOException("not a response data");
        }
        throw new IOException("question id error");
    }

    private int parseQuestion() throws IOException {
        int index = 12;
        int qdCount = readRecordDataInt16(4);
        while (qdCount > 0) {
            RecordName recordName = getNameFrom(index);
            if (recordName != null) {
                index += recordName.skipLength + 4;
                qdCount--;
            } else {
                throw new IOException("read Question error");
            }
        }
        return index;
    }

    private void parseResourceRecord(RecordResource resource) throws IOException {
        int rdLength;
        RecordResource recordResource = resource;
        int index = resource.from;
        int count = resource.count;
        while (count > 0) {
            RecordName recordName = getNameFrom(index);
            if (recordName != null) {
                int index2 = index + recordName.skipLength;
                int type = readRecordDataInt16(index2);
                int index3 = index2 + 2;
                int clazz = readRecordDataInt16(index3);
                int index4 = index3 + 2;
                int ttl = readRecordDataInt32(index4);
                int index5 = index4 + 4;
                int rdLength2 = readRecordDataInt16(index5);
                int index6 = index5 + 2;
                String value = readData(type, index6, rdLength2);
                if (clazz != 1) {
                    rdLength = rdLength2;
                } else if (type == 5 || type == this.request.getRecordType()) {
                    rdLength = rdLength2;
                    recordResource.addRecord(new Record(value, type, ttl, this.timestamp, this.source, this.server));
                } else {
                    rdLength = rdLength2;
                }
                index = index6 + rdLength;
                count--;
            } else {
                throw new IOException("read " + resource.name + " error");
            }
        }
        int unused = recordResource.length = index - resource.from;
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x001a  */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0033  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.qiniu.android.dns.dns.DnsResponse.RecordName getNameFrom(int r10) throws java.io.IOException {
        /*
            r9 = this;
            r0 = 0
            r1 = r10
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            com.qiniu.android.dns.dns.DnsResponse$RecordName r3 = new com.qiniu.android.dns.dns.DnsResponse$RecordName
            r4 = 0
            r3.<init>()
            r5 = 128(0x80, float:1.794E-43)
        L_0x000f:
            int r0 = r9.readRecordDataInt8(r1)
            r6 = r0 & 192(0xc0, float:2.69E-43)
            r7 = 192(0xc0, float:2.69E-43)
            r8 = 1
            if (r6 != r7) goto L_0x0033
            int r6 = r3.skipLength
            if (r6 >= r8) goto L_0x0026
            int r6 = r1 + 2
            int r6 = r6 - r10
            int unused = r3.skipLength = r6
        L_0x0026:
            r6 = r0 & 63
            int r6 = r6 << 8
            int r7 = r1 + 1
            int r7 = r9.readRecordDataInt8(r7)
            r1 = r6 | r7
            goto L_0x005c
        L_0x0033:
            r6 = r0 & 192(0xc0, float:2.69E-43)
            if (r6 <= 0) goto L_0x0038
            return r4
        L_0x0038:
            int r1 = r1 + 1
            if (r0 <= 0) goto L_0x005c
            int r6 = r2.length()
            if (r6 <= 0) goto L_0x0047
            java.lang.String r6 = "."
            r2.append(r6)
        L_0x0047:
            byte[] r6 = r9.recordData
            int r7 = r1 + r0
            byte[] r6 = java.util.Arrays.copyOfRange(r6, r1, r7)
            java.lang.String r7 = new java.lang.String
            r7.<init>(r6)
            java.lang.String r7 = java.net.IDN.toUnicode(r7)
            r2.append(r7)
            int r1 = r1 + r0
        L_0x005c:
            if (r0 <= 0) goto L_0x0062
            int r5 = r5 + -1
            if (r5 > 0) goto L_0x000f
        L_0x0062:
            java.lang.String r4 = r2.toString()
            java.lang.String unused = r3.name = r4
            int r4 = r3.skipLength
            if (r4 >= r8) goto L_0x0074
            int r4 = r1 - r10
            int unused = r3.skipLength = r4
        L_0x0074:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qiniu.android.dns.dns.DnsResponse.getNameFrom(int):com.qiniu.android.dns.dns.DnsResponse$RecordName");
    }

    private String readData(int recordType, int from, int length) throws IOException {
        if (recordType != 1) {
            if (recordType != 5) {
                if (recordType != 16) {
                    if (recordType != 28 || length != 16) {
                        return null;
                    }
                    StringBuilder builder = new StringBuilder();
                    int i = 0;
                    while (i < 16) {
                        builder.append(i > 0 ? LogUtils.COLON : "");
                        builder.append(readRecordDataInt8(from + i));
                        builder.append(readRecordDataInt8(from + i + 1));
                        i += 2;
                    }
                    return builder.toString();
                } else if (length <= 0) {
                    return null;
                } else {
                    int i2 = from + length;
                    byte[] bArr = this.recordData;
                    if (i2 < bArr.length) {
                        return IDN.toUnicode(new String(Arrays.copyOfRange(bArr, from, from + length)));
                    }
                    return null;
                }
            } else if (length > 1) {
                return getNameFrom(from).name;
            } else {
                return null;
            }
        } else if (length != 4) {
            return null;
        } else {
            StringBuilder builder2 = new StringBuilder();
            builder2.append(readRecordDataInt8(from));
            for (int i3 = 1; i3 < 4; i3++) {
                builder2.append(".");
                builder2.append(readRecordDataInt8(from + i3));
            }
            return builder2.toString();
        }
    }

    private int readRecordDataInt8(int from) throws IOException {
        byte[] bArr = this.recordData;
        if (from < bArr.length) {
            return bArr[from] & UByte.MAX_VALUE;
        }
        throw new IOException("read response data out of range");
    }

    private short readRecordDataInt16(int from) throws IOException {
        int i = from + 1;
        byte[] bArr = this.recordData;
        if (i < bArr.length) {
            return (short) (((bArr[from] & UByte.MAX_VALUE) << 8) + (bArr[from + 1] & 255));
        }
        throw new IOException("read response data out of range");
    }

    private int readRecordDataInt32(int from) throws IOException {
        int i = from + 3;
        byte[] bArr = this.recordData;
        if (i < bArr.length) {
            return ((bArr[from] & UByte.MAX_VALUE) << 24) + ((bArr[from + 1] & UByte.MAX_VALUE) << 16) + ((bArr[from + 2] & UByte.MAX_VALUE) << 8) + (bArr[from + 3] & 255);
        }
        throw new IOException("read response data out of range");
    }

    /* access modifiers changed from: package-private */
    public int getAA() {
        return this.aa;
    }

    /* access modifiers changed from: package-private */
    public int getRCode() {
        return this.rCode;
    }

    /* access modifiers changed from: package-private */
    public List<Record> getAnswerArray() {
        return this.answerArray;
    }

    /* access modifiers changed from: package-private */
    public List<Record> getAdditionalArray() {
        return this.additionalArray;
    }

    /* access modifiers changed from: package-private */
    public List<Record> getAuthorityArray() {
        return this.authorityArray;
    }

    public String toString() {
        return String.format(Locale.getDefault(), "{messageId:%d, rd:%d, ra:%d, aa:%d, rCode:%d, server:%s, request:%s, answerArray:%s, authorityArray:%s, additionalArray:%s}", new Object[]{Short.valueOf(this.messageId), Integer.valueOf(this.rd), Integer.valueOf(this.ra), Integer.valueOf(this.aa), Integer.valueOf(this.rCode), this.server, this.request, this.answerArray, this.authorityArray, this.additionalArray});
    }

    private static class RecordResource {
        /* access modifiers changed from: private */
        public final int count;
        /* access modifiers changed from: private */
        public final int from;
        /* access modifiers changed from: private */
        public int length;
        /* access modifiers changed from: private */
        public final String name;
        /* access modifiers changed from: private */
        public final List<Record> records;

        private RecordResource(String name2, int count2, int from2) {
            this.name = name2;
            this.count = count2;
            this.from = from2;
            this.length = 0;
            this.records = new ArrayList();
        }

        /* access modifiers changed from: private */
        public void addRecord(Record record) {
            if (record != null) {
                this.records.add(record);
            }
        }
    }

    private static class RecordName {
        /* access modifiers changed from: private */
        public String name;
        /* access modifiers changed from: private */
        public int skipLength;

        private RecordName() {
        }
    }
}
