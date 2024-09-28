package org.webrtc.ali;

import org.webrtc.ali.MediaStreamTrack;

public class RtpReceiver {
    private MediaStreamTrack cachedTrack;
    private long nativeObserver;
    final long nativeRtpReceiver;

    public interface Observer {
        void onFirstPacketReceived(MediaStreamTrack.MediaType mediaType);
    }

    private static native void free(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native long nativeGetTrack(long j);

    private static native String nativeId(long j);

    private static native long nativeSetObserver(long j, Observer observer);

    private static native boolean nativeSetParameters(long j, RtpParameters rtpParameters);

    private static native long nativeUnsetObserver(long j, long j2);

    public RtpReceiver(long nativeRtpReceiver2) {
        this.nativeRtpReceiver = nativeRtpReceiver2;
        this.cachedTrack = new MediaStreamTrack(nativeGetTrack(nativeRtpReceiver2));
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public boolean setParameters(RtpParameters parameters) {
        return nativeSetParameters(this.nativeRtpReceiver, parameters);
    }

    public RtpParameters getParameters() {
        return nativeGetParameters(this.nativeRtpReceiver);
    }

    public String id() {
        return nativeId(this.nativeRtpReceiver);
    }

    public void dispose() {
        this.cachedTrack.dispose();
        long j = this.nativeObserver;
        if (j != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, j);
            this.nativeObserver = 0;
        }
        free(this.nativeRtpReceiver);
    }

    public void SetObserver(Observer observer) {
        long j = this.nativeObserver;
        if (j != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, j);
        }
        this.nativeObserver = nativeSetObserver(this.nativeRtpReceiver, observer);
    }
}
