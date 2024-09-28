package com.danikula.videocache;

public class SourceInfo {
    public final long length;
    public final String mime;
    public final String url;

    public SourceInfo(String url2, long length2, String mime2) {
        this.url = url2;
        this.length = length2;
        this.mime = mime2;
    }

    public String toString() {
        return "SourceInfo{url='" + this.url + '\'' + ", length=" + this.length + ", mime='" + this.mime + '\'' + '}';
    }
}
