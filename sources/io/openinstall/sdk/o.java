package io.openinstall.sdk;

import java.io.Serializable;

public class o implements Serializable {
    private String a;
    private String b;

    public o(String str) {
        this.a = str;
    }

    public String a() {
        return this.a;
    }

    public void a(String str) {
        this.b = str;
    }

    public String b() {
        return this.b;
    }
}
