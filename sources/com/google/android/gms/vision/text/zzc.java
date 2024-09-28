package com.google.android.gms.vision.text;

import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.gms.internal.vision.zzr;

final class zzc {
    static Rect zza(Text text) {
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MIN_VALUE;
        int i3 = Integer.MAX_VALUE;
        int i4 = Integer.MAX_VALUE;
        for (Point point : text.getCornerPoints()) {
            i3 = Math.min(i3, point.x);
            i = Math.max(i, point.x);
            i4 = Math.min(i4, point.y);
            i2 = Math.max(i2, point.y);
        }
        return new Rect(i3, i4, i, i2);
    }

    static Point[] zza(zzr zzr) {
        Point[] pointArr = new Point[4];
        double sin = Math.sin(Math.toRadians((double) zzr.zzdh));
        double cos = Math.cos(Math.toRadians((double) zzr.zzdh));
        pointArr[0] = new Point(zzr.left, zzr.top);
        pointArr[1] = new Point((int) (((double) zzr.left) + (((double) zzr.width) * cos)), (int) (((double) zzr.top) + (((double) zzr.width) * sin)));
        pointArr[2] = new Point((int) (((double) pointArr[1].x) - (((double) zzr.height) * sin)), (int) (((double) pointArr[1].y) + (((double) zzr.height) * cos)));
        pointArr[3] = new Point(pointArr[0].x + (pointArr[2].x - pointArr[1].x), pointArr[0].y + (pointArr[2].y - pointArr[1].y));
        return pointArr;
    }
}
