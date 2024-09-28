package com.qiniu.android.dns.dns;

class DnsMessage {
    static final int OpCodeIQuery = 1;
    static final int OpCodeQuery = 0;
    static final int OpCodeStatus = 2;
    static final int OpCodeUpdate = 5;
    protected short messageId = 0;
    protected int opCode = 0;
    protected int ra = 0;
    protected int rd = 1;

    DnsMessage() {
    }

    /* access modifiers changed from: package-private */
    public int getMessageId() {
        return this.messageId;
    }

    /* access modifiers changed from: package-private */
    public int getOpCode() {
        return this.opCode;
    }

    /* access modifiers changed from: package-private */
    public int getRD() {
        return this.rd;
    }

    /* access modifiers changed from: package-private */
    public int getRA() {
        return this.ra;
    }
}
