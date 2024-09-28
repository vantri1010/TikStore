package com.baidu.location.e;

import java.util.Locale;

public class a {
    public int a;
    public long b;
    public int c;
    public int d;
    public int e;
    public int f;
    public long g;
    public int h;
    public char i;
    public int j;
    public int k;
    public String l;
    public String m;
    private boolean n;

    public a() {
        this.a = -1;
        this.b = -1;
        this.c = -1;
        this.d = -1;
        this.e = Integer.MAX_VALUE;
        this.f = Integer.MAX_VALUE;
        this.g = 0;
        this.h = -1;
        this.i = '0';
        this.j = Integer.MAX_VALUE;
        this.k = 0;
        this.l = null;
        this.m = null;
        this.n = false;
        this.g = System.currentTimeMillis();
    }

    public a(int i2, long j2, int i3, int i4, int i5, char c2, int i6) {
        this.a = -1;
        this.b = -1;
        this.c = -1;
        this.d = -1;
        this.e = Integer.MAX_VALUE;
        this.f = Integer.MAX_VALUE;
        this.g = 0;
        this.h = -1;
        this.i = '0';
        this.j = Integer.MAX_VALUE;
        this.k = 0;
        this.l = null;
        this.m = null;
        this.n = false;
        this.a = i2;
        this.b = j2;
        this.c = i3;
        this.d = i4;
        this.h = i5;
        this.i = c2;
        this.g = System.currentTimeMillis();
        this.j = i6;
    }

    public a(a aVar) {
        this(aVar.a, aVar.b, aVar.c, aVar.d, aVar.h, aVar.i, aVar.j);
        this.g = aVar.g;
    }

    public boolean a() {
        long currentTimeMillis = System.currentTimeMillis();
        long j2 = this.g;
        return currentTimeMillis - j2 > 0 && currentTimeMillis - j2 < 3000;
    }

    public boolean a(a aVar) {
        return this.a == aVar.a && this.b == aVar.b && this.d == aVar.d && this.c == aVar.c;
    }

    public boolean b() {
        return this.a > -1 && this.b > 0;
    }

    public boolean c() {
        return this.a == -1 && this.b == -1 && this.d == -1 && this.c == -1;
    }

    public boolean d() {
        return this.a > -1 && this.b > -1 && this.d == -1 && this.c == -1;
    }

    public boolean e() {
        return this.a > -1 && this.b > -1 && this.d > -1 && this.c > -1;
    }

    public void f() {
        this.n = true;
    }

    public String g() {
        return String.format(Locale.CHINA, "%d|%d|%d|%d", new Object[]{Integer.valueOf(this.c), Integer.valueOf(this.d), Integer.valueOf(this.a), Long.valueOf(this.b)});
    }

    public String h() {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&nw=");
        stringBuffer.append(this.i);
        stringBuffer.append(String.format(Locale.CHINA, "&cl=%d|%d|%d|%d&cl_s=%d&clp=%d", new Object[]{Integer.valueOf(this.c), Integer.valueOf(this.d), Integer.valueOf(this.a), Long.valueOf(this.b), Integer.valueOf(this.h), Integer.valueOf(this.k)}));
        if (this.j != Integer.MAX_VALUE) {
            stringBuffer.append("&cl_cs=");
            stringBuffer.append(this.j);
        }
        if (this.n) {
            stringBuffer.append("&newcl=1");
        }
        if (this.m != null) {
            stringBuffer.append("&clnrs=");
            stringBuffer.append(this.m);
        }
        return stringBuffer.toString();
    }

    public String i() {
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&nw2=");
        stringBuffer.append(this.i);
        stringBuffer.append(String.format(Locale.CHINA, "&cl2=%d|%d|%d|%d&cl_s2=%d&clp2=%d", new Object[]{Integer.valueOf(this.c), Integer.valueOf(this.d), Integer.valueOf(this.a), Long.valueOf(this.b), Integer.valueOf(this.h), Integer.valueOf(this.k)}));
        if (this.j != Integer.MAX_VALUE) {
            stringBuffer.append("&cl_cs2=");
            stringBuffer.append(this.j);
        }
        if (this.m != null) {
            stringBuffer.append("&clnrs2=");
            stringBuffer.append(this.m);
        }
        return stringBuffer.toString();
    }
}
