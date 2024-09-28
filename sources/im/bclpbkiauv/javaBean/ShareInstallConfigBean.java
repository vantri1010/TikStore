package im.bclpbkiauv.javaBean;

import java.io.Serializable;

public class ShareInstallConfigBean implements Serializable {
    private long AccessHash;
    private String ProxyUid;
    private String UserName;

    public ShareInstallConfigBean(String proxyUid, long accessHash) {
        this.ProxyUid = proxyUid;
        this.AccessHash = accessHash;
    }

    public ShareInstallConfigBean(String proxyUid, long accessHash, String userName) {
        this.ProxyUid = proxyUid;
        this.AccessHash = accessHash;
        this.UserName = userName;
    }

    public String getProxyUid() {
        return this.ProxyUid;
    }

    public void setProxyUid(String proxyUid) {
        this.ProxyUid = proxyUid;
    }

    public long getAccessHash() {
        return this.AccessHash;
    }

    public void setAccessHash(long accessHash) {
        this.AccessHash = accessHash;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String toString() {
        return "ShareInstallConfigBean{ProxyUid='" + this.ProxyUid + '\'' + ", AccessHash=" + this.AccessHash + ", UserName='" + this.UserName + '\'' + '}';
    }
}
