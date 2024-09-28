package org.webrtc.ali;

public class MediaSource {
    final long nativeSource;

    public enum State {
        INITIALIZING,
        LIVE,
        ENDED,
        MUTED
    }

    private static native void free(long j);

    private static native State nativeState(long j);

    public MediaSource(long nativeSource2) {
        this.nativeSource = nativeSource2;
    }

    public State state() {
        return nativeState(this.nativeSource);
    }

    public void dispose() {
        free(this.nativeSource);
    }
}
