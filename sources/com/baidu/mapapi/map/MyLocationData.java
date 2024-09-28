package com.baidu.mapapi.map;

public class MyLocationData {
    public final float accuracy;
    public final float direction;
    public final double latitude;
    public final double longitude;
    public final int satellitesNum;
    public final float speed;

    public static class Builder {
        private double a;
        private double b;
        private float c;
        private float d;
        private float e;
        private int f;

        public Builder accuracy(float f2) {
            this.e = f2;
            return this;
        }

        public MyLocationData build() {
            return new MyLocationData(this.a, this.b, this.c, this.d, this.e, this.f);
        }

        public Builder direction(float f2) {
            this.d = f2;
            return this;
        }

        public Builder latitude(double d2) {
            this.a = d2;
            return this;
        }

        public Builder longitude(double d2) {
            this.b = d2;
            return this;
        }

        public Builder satellitesNum(int i) {
            this.f = i;
            return this;
        }

        public Builder speed(float f2) {
            this.c = f2;
            return this;
        }
    }

    MyLocationData(double d, double d2, float f, float f2, float f3, int i) {
        this.latitude = d;
        this.longitude = d2;
        this.speed = f;
        this.direction = f2;
        this.accuracy = f3;
        this.satellitesNum = i;
    }
}
