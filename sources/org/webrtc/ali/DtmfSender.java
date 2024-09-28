package org.webrtc.ali;

public class DtmfSender {
    final long nativeDtmfSender;

    private static native void free(long j);

    private static native boolean nativeCanInsertDtmf(long j);

    private static native int nativeDuration(long j);

    private static native boolean nativeInsertDtmf(long j, String str, int i, int i2);

    private static native int nativeInterToneGap(long j);

    private static native String nativeTones(long j);

    public DtmfSender(long nativeDtmfSender2) {
        this.nativeDtmfSender = nativeDtmfSender2;
    }

    public boolean canInsertDtmf() {
        return nativeCanInsertDtmf(this.nativeDtmfSender);
    }

    public boolean insertDtmf(String tones, int duration, int interToneGap) {
        return nativeInsertDtmf(this.nativeDtmfSender, tones, duration, interToneGap);
    }

    public String tones() {
        return nativeTones(this.nativeDtmfSender);
    }

    public int duration() {
        return nativeDuration(this.nativeDtmfSender);
    }

    public int interToneGap() {
        return nativeInterToneGap(this.nativeDtmfSender);
    }

    public void dispose() {
        free(this.nativeDtmfSender);
    }
}
