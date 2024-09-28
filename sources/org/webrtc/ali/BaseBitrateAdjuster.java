package org.webrtc.ali;

class BaseBitrateAdjuster implements BitrateAdjuster {
    protected int targetBitrateBps = 0;
    protected int targetFps = 0;

    BaseBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps2, int targetFps2) {
        this.targetBitrateBps = targetBitrateBps2;
        this.targetFps = targetFps2;
    }

    public void reportEncodedFrame(int size) {
    }

    public int getAdjustedBitrateBps() {
        return this.targetBitrateBps;
    }

    public int getAdjustedFramerate() {
        return this.targetFps;
    }
}
