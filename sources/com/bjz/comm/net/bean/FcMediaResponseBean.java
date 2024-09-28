package com.bjz.comm.net.bean;

import java.io.Serializable;

public class FcMediaResponseBean implements Serializable {
    private String FileName;
    private String Hash;
    private String Name;

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getHash() {
        return this.Hash;
    }

    public void setHash(String hash) {
        this.Hash = hash;
    }

    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String fileName) {
        this.FileName = fileName;
    }

    public String toString() {
        return "FcMediaResponseBean{Name='" + this.Name + '\'' + ", Hash='" + this.Hash + '\'' + ", FileName='" + this.FileName + '\'' + '}';
    }
}
