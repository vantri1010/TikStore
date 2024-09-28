package org.webrtc.ali;

public class AudioTrack extends MediaStreamTrack {
    private static native void nativeSetVolume(long j, double d);

    public AudioTrack(long nativeTrack) {
        super(nativeTrack);
    }

    public void setVolume(double volume) {
        nativeSetVolume(this.nativeTrack, volume);
    }
}
