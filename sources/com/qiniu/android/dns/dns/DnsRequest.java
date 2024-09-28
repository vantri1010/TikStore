package com.qiniu.android.dns.dns;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.IDN;

class DnsRequest extends DnsMessage {
    private final String host;
    private final int recordType;

    /* access modifiers changed from: package-private */
    public int getRecordType() {
        return this.recordType;
    }

    /* access modifiers changed from: package-private */
    public String getHost() {
        return this.host;
    }

    DnsRequest(short messageId, int recordType2, String host2) {
        this(messageId, 0, 1, recordType2, host2);
    }

    DnsRequest(short messageId, int opCode, int rd, int recordType2, String host2) {
        this.messageId = messageId;
        this.opCode = opCode;
        this.rd = rd;
        this.recordType = recordType2;
        this.host = host2;
    }

    /* access modifiers changed from: package-private */
    public byte[] toDnsQuestionData() throws IOException {
        String str = this.host;
        if (str == null || str.length() == 0) {
            throw new IOException("host can not empty");
        } else if (this.opCode != 0 && this.opCode != 1 && this.opCode != 2 && this.opCode != 5) {
            throw new IOException("opCode is not valid");
        } else if (this.rd == 0 || this.rd == 1) {
            int i = this.recordType;
            if (i == 1 || i == 28 || i == 5 || i == 16) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeShort(this.messageId);
                dos.writeByte((this.opCode << 3) + this.rd);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(1);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(0);
                String[] split = this.host.split("[.。．｡]");
                int length = split.length;
                int i2 = 0;
                while (i2 < length) {
                    String s = split[i2];
                    if (s.length() <= 63) {
                        byte[] buffer = IDN.toASCII(s).getBytes();
                        dos.write(buffer.length);
                        dos.write(buffer, 0, buffer.length);
                        i2++;
                    } else {
                        throw new IOException("host part is too long");
                    }
                }
                dos.writeByte(0);
                dos.writeByte(0);
                dos.writeByte(this.recordType);
                dos.writeByte(0);
                dos.writeByte(1);
                return baos.toByteArray();
            }
            throw new IOException("recordType is not valid");
        } else {
            throw new IOException("rd is not valid");
        }
    }
}
