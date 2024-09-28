package io.openinstall.sdk;

public class ao implements Cloneable {
    private final at a;
    private final as b;

    public ao(as asVar, at atVar) {
        this.a = atVar;
        this.b = asVar;
    }

    public ao(at atVar) {
        this((as) null, atVar);
    }

    public at a() {
        return this.a;
    }

    public void a(byte[] bArr) {
        as asVar = this.b;
        if (asVar != null) {
            asVar.a(bArr);
        } else {
            this.a.a(bArr);
        }
    }

    public as b() {
        return this.b;
    }

    public byte[] c() {
        as asVar = this.b;
        return asVar != null ? asVar.d() : this.a.g;
    }

    /* renamed from: d */
    public ao clone() {
        as asVar = this.b;
        return new ao(asVar == null ? null : asVar.clone(), this.a.clone());
    }
}
