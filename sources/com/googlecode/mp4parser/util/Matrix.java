package com.googlecode.mp4parser.util;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.nio.ByteBuffer;

public class Matrix {
    public static final Matrix ROTATE_0 = new Matrix(1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    public static final Matrix ROTATE_180 = new Matrix(-1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, -1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    public static final Matrix ROTATE_270 = new Matrix(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, -1.0d, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    public static final Matrix ROTATE_90 = new Matrix(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, -1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 1.0d, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    double a;
    double b;
    double c;
    double d;
    double tx;
    double ty;
    double u;
    double v;
    double w;

    public Matrix(double a2, double b2, double c2, double d2, double u2, double v2, double w2, double tx2, double ty2) {
        this.u = u2;
        this.v = v2;
        this.w = w2;
        this.a = a2;
        this.b = b2;
        this.c = c2;
        this.d = d2;
        this.tx = tx2;
        this.ty = ty2;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Matrix matrix = (Matrix) o;
        if (Double.compare(matrix.a, this.a) == 0 && Double.compare(matrix.b, this.b) == 0 && Double.compare(matrix.c, this.c) == 0 && Double.compare(matrix.d, this.d) == 0 && Double.compare(matrix.tx, this.tx) == 0 && Double.compare(matrix.ty, this.ty) == 0 && Double.compare(matrix.u, this.u) == 0 && Double.compare(matrix.v, this.v) == 0 && Double.compare(matrix.w, this.w) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.u);
        long temp2 = Double.doubleToLongBits(this.v);
        long temp3 = Double.doubleToLongBits(this.w);
        long temp4 = Double.doubleToLongBits(this.a);
        long temp5 = Double.doubleToLongBits(this.b);
        long temp6 = Double.doubleToLongBits(this.c);
        long temp7 = Double.doubleToLongBits(this.d);
        long temp8 = Double.doubleToLongBits(this.tx);
        long temp9 = Double.doubleToLongBits(this.ty);
        return (((((((((((((((((int) ((temp >>> 32) ^ temp)) * 31) + ((int) ((temp2 >>> 32) ^ temp2))) * 31) + ((int) ((temp3 >>> 32) ^ temp3))) * 31) + ((int) ((temp4 >>> 32) ^ temp4))) * 31) + ((int) ((temp5 >>> 32) ^ temp5))) * 31) + ((int) ((temp6 >>> 32) ^ temp6))) * 31) + ((int) ((temp7 >>> 32) ^ temp7))) * 31) + ((int) ((temp8 >>> 32) ^ temp8))) * 31) + ((int) ((temp9 >>> 32) ^ temp9));
    }

    public String toString() {
        if (equals(ROTATE_0)) {
            return "Rotate 0°";
        }
        if (equals(ROTATE_90)) {
            return "Rotate 90°";
        }
        if (equals(ROTATE_180)) {
            return "Rotate 180°";
        }
        if (equals(ROTATE_270)) {
            return "Rotate 270°";
        }
        return "Matrix{u=" + this.u + ", v=" + this.v + ", w=" + this.w + ", a=" + this.a + ", b=" + this.b + ", c=" + this.c + ", d=" + this.d + ", tx=" + this.tx + ", ty=" + this.ty + '}';
    }

    public static Matrix fromFileOrder(double a2, double b2, double u2, double c2, double d2, double v2, double tx2, double ty2, double w2) {
        return new Matrix(a2, b2, c2, d2, u2, v2, w2, tx2, ty2);
    }

    public static Matrix fromByteBuffer(ByteBuffer byteBuffer) {
        return fromFileOrder(IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint1616(byteBuffer), IsoTypeReader.readFixedPoint0230(byteBuffer));
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.a);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.b);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.u);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.c);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.d);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.v);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.tx);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.ty);
        IsoTypeWriter.writeFixedPoint0230(byteBuffer, this.w);
    }
}
