package com.googlecode.mp4parser.h264.model;

public class AspectRatio {
    public static final AspectRatio Extended_SAR = new AspectRatio(255);
    private int value;

    private AspectRatio(int value2) {
        this.value = value2;
    }

    public static AspectRatio fromValue(int value2) {
        AspectRatio aspectRatio = Extended_SAR;
        if (value2 == aspectRatio.value) {
            return aspectRatio;
        }
        return new AspectRatio(value2);
    }

    public int getValue() {
        return this.value;
    }

    public String toString() {
        return "AspectRatio{" + "value=" + this.value + '}';
    }
}
