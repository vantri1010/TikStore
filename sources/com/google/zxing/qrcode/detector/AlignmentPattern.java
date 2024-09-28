package com.google.zxing.qrcode.detector;

import com.google.zxing.ResultPoint;

public final class AlignmentPattern extends ResultPoint {
    private final float estimatedModuleSize;

    AlignmentPattern(float posX, float posY, float estimatedModuleSize2) {
        super(posX, posY);
        this.estimatedModuleSize = estimatedModuleSize2;
    }

    /* access modifiers changed from: package-private */
    public boolean aboutEquals(float moduleSize, float i, float j) {
        if (Math.abs(i - getY()) > moduleSize || Math.abs(j - getX()) > moduleSize) {
            return false;
        }
        float abs = Math.abs(moduleSize - this.estimatedModuleSize);
        float moduleSizeDiff = abs;
        if (abs <= 1.0f || moduleSizeDiff <= this.estimatedModuleSize) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public AlignmentPattern combineEstimate(float i, float j, float newModuleSize) {
        return new AlignmentPattern((getX() + j) / 2.0f, (getY() + i) / 2.0f, (this.estimatedModuleSize + newModuleSize) / 2.0f);
    }
}
