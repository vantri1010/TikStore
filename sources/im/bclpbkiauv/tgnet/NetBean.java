package im.bclpbkiauv.tgnet;

class NetBean {
    private String dDomain;
    private String dName;
    private String dPort;

    NetBean(String dName2, String dDomain2, String dPort2) {
        this.dName = dName2;
        this.dDomain = dDomain2;
        this.dPort = dPort2;
    }

    /* access modifiers changed from: package-private */
    public String getdName() {
        return this.dName;
    }

    /* access modifiers changed from: package-private */
    public String getdDomain() {
        return this.dDomain;
    }

    /* access modifiers changed from: package-private */
    public String getdPort() {
        return this.dPort;
    }
}
