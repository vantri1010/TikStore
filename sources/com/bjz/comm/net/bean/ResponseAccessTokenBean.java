package com.bjz.comm.net.bean;

public class ResponseAccessTokenBean {
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String scope;
    private String session_key;
    private String session_secret;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String access_token2) {
        this.access_token = access_token2;
    }

    public int getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(int expires_in2) {
        this.expires_in = expires_in2;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token2) {
        this.refresh_token = refresh_token2;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope2) {
        this.scope = scope2;
    }

    public String getSession_key() {
        return this.session_key;
    }

    public void setSession_key(String session_key2) {
        this.session_key = session_key2;
    }

    public String getSession_secret() {
        return this.session_secret;
    }

    public void setSession_secret(String session_secret2) {
        this.session_secret = session_secret2;
    }

    public String toString() {
        return "ResponseAccessTokenBean{access_token='" + this.access_token + '\'' + ", expires_in=" + this.expires_in + ", refresh_token='" + this.refresh_token + '\'' + ", scope='" + this.scope + '\'' + ", session_key='" + this.session_key + '\'' + ", session_secret='" + this.session_secret + '\'' + '}';
    }
}
