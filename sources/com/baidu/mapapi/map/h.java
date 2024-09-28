package com.baidu.mapapi.map;

import android.graphics.Point;

class h {
    public final double a;
    public final double b;
    public final double c;
    public final double d;
    public final double e;
    public final double f;

    public h(double d2, double d3, double d4, double d5) {
        this.a = d2;
        this.b = d4;
        this.c = d3;
        this.d = d5;
        this.e = (d2 + d3) / 2.0d;
        this.f = (d4 + d5) / 2.0d;
    }

    public boolean a(double d2, double d3) {
        return this.a <= d2 && d2 <= this.c && this.b <= d3 && d3 <= this.d;
    }

    public boolean a(double d2, double d3, double d4, double d5) {
        return d2 < this.c && this.a < d3 && d4 < this.d && this.b < d5;
    }

    public boolean a(Point point) {
        return a((double) point.x, (double) point.y);
    }

    public boolean a(h hVar) {
        return a(hVar.a, hVar.c, hVar.b, hVar.d);
    }

    public boolean b(h hVar) {
        return hVar.a >= this.a && hVar.c <= this.c && hVar.b >= this.b && hVar.d <= this.d;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("minX: " + this.a);
        sb.append(" minY: " + this.b);
        sb.append(" maxX: " + this.c);
        sb.append(" maxY: " + this.d);
        sb.append(" midX: " + this.e);
        sb.append(" midY: " + this.f);
        return sb.toString();
    }
}
