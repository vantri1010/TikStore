package com.bjz.comm.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DigitalTokenBean implements Parcelable {
    public static final Parcelable.Creator<DigitalTokenBean> CREATOR = new Parcelable.Creator<DigitalTokenBean>() {
        public DigitalTokenBean createFromParcel(Parcel in) {
            return new DigitalTokenBean(in);
        }

        public DigitalTokenBean[] newArray(int size) {
            return new DigitalTokenBean[size];
        }
    };
    private String clientNonce;
    private boolean digitalWallet;
    private int expire;
    private int id;
    private String serverNonce;
    private String token;

    public DigitalTokenBean() {
    }

    public DigitalTokenBean(String token2, String clientNonce2, String serverNonce2, boolean digitalWallet2, int expire2) {
        this.id = this.id;
        this.token = token2;
        this.clientNonce = clientNonce2;
        this.serverNonce = serverNonce2;
        this.digitalWallet = digitalWallet2;
        this.expire = expire2;
    }

    public DigitalTokenBean(int id2, String token2, String clientNonce2, String serverNonce2, boolean digitalWallet2, int expire2) {
        this.id = id2;
        this.token = token2;
        this.clientNonce = clientNonce2;
        this.serverNonce = serverNonce2;
        this.digitalWallet = digitalWallet2;
        this.expire = expire2;
    }

    protected DigitalTokenBean(Parcel in) {
        this.id = in.readInt();
        this.token = in.readString();
        this.clientNonce = in.readString();
        this.serverNonce = in.readString();
        this.digitalWallet = in.readByte() != 0;
        this.expire = in.readInt();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token2) {
        this.token = token2;
    }

    public String getClientNonce() {
        return this.clientNonce;
    }

    public void setClientNonce(String clientNonce2) {
        this.clientNonce = clientNonce2;
    }

    public String getServerNonce() {
        return this.serverNonce;
    }

    public void setServerNonce(String serverNonce2) {
        this.serverNonce = serverNonce2;
    }

    public boolean isDigitalWallet() {
        return this.digitalWallet;
    }

    public void setDigitalWallet(boolean digitalWallet2) {
        this.digitalWallet = digitalWallet2;
    }

    public int getExpire() {
        return this.expire;
    }

    public void setExpire(int expire2) {
        this.expire = expire2;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.token);
        dest.writeString(this.clientNonce);
        dest.writeString(this.serverNonce);
        dest.writeByte(this.digitalWallet ? (byte) 1 : 0);
        dest.writeInt(this.expire);
    }

    public String toString() {
        return "DigitalTokenBean{id='" + this.id + '\'' + "token='" + this.token + '\'' + ", clientNonce='" + this.clientNonce + '\'' + ", serverNonce='" + this.serverNonce + '\'' + ", digitalWallet='" + this.digitalWallet + '\'' + ", expire=" + this.expire + '}';
    }
}
