package org.webrtc.ali;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

class DynamicBitrateAdjuster extends BaseBitrateAdjuster {
    private static final double BITRATE_ADJUSTMENT_MAX_SCALE = 4.0d;
    private static final double BITRATE_ADJUSTMENT_SEC = 3.0d;
    private static final int BITRATE_ADJUSTMENT_STEPS = 20;
    private static final double BITS_PER_BYTE = 8.0d;
    private int bitrateAdjustmentScaleExp = 0;
    private double deviationBytes = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    private double timeSinceLastAdjustmentMs = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;

    DynamicBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps, int targetFps) {
        if (this.targetBitrateBps > 0 && targetBitrateBps < this.targetBitrateBps) {
            this.deviationBytes = (this.deviationBytes * ((double) targetBitrateBps)) / ((double) this.targetBitrateBps);
        }
        super.setTargets(targetBitrateBps, targetFps);
    }

    public void reportEncodedFrame(int size) {
        if (this.targetFps != 0) {
            this.deviationBytes += ((double) size) - ((((double) this.targetBitrateBps) / BITS_PER_BYTE) / ((double) this.targetFps));
            this.timeSinceLastAdjustmentMs += 1000.0d / ((double) this.targetFps);
            double deviationThresholdBytes = ((double) this.targetBitrateBps) / BITS_PER_BYTE;
            double deviationCap = BITRATE_ADJUSTMENT_SEC * deviationThresholdBytes;
            double min = Math.min(this.deviationBytes, deviationCap);
            this.deviationBytes = min;
            double max = Math.max(min, -deviationCap);
            this.deviationBytes = max;
            if (this.timeSinceLastAdjustmentMs > 3000.0d) {
                if (max > deviationThresholdBytes) {
                    int i = this.bitrateAdjustmentScaleExp - ((int) ((max / deviationThresholdBytes) + 0.5d));
                    this.bitrateAdjustmentScaleExp = i;
                    this.bitrateAdjustmentScaleExp = Math.max(i, -20);
                    this.deviationBytes = deviationThresholdBytes;
                } else if (max < (-deviationThresholdBytes)) {
                    int i2 = this.bitrateAdjustmentScaleExp + ((int) (((-max) / deviationThresholdBytes) + 0.5d));
                    this.bitrateAdjustmentScaleExp = i2;
                    this.bitrateAdjustmentScaleExp = Math.min(i2, 20);
                    this.deviationBytes = -deviationThresholdBytes;
                }
                this.timeSinceLastAdjustmentMs = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            }
        }
    }

    public int getAdjustedBitrateBps() {
        return (int) (((double) this.targetBitrateBps) * Math.pow(BITRATE_ADJUSTMENT_MAX_SCALE, ((double) this.bitrateAdjustmentScaleExp) / 20.0d));
    }
}
