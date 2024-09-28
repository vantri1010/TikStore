package io.openinstall.sdk;

public class az implements bb {
    private final Exception a;

    public az(Exception exc) {
        this.a = exc;
    }

    public boolean e() {
        return true;
    }

    public String f() {
        return this.a.getMessage();
    }
}
