package org.webrtc.ali;

class FramerateBitrateAdjuster extends BaseBitrateAdjuster {
    private static final int INITIAL_FPS = 30;

    FramerateBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps, int targetFps) {
        if (this.targetFps == 0) {
            targetFps = 30;
        }
        super.setTargets(targetBitrateBps, targetFps);
        this.targetBitrateBps *= 30 / this.targetFps;
    }
}
