package org.webrtc.ali;

public class RtpSender {
    private MediaStreamTrack cachedTrack;
    private final DtmfSender dtmfSender;
    final long nativeRtpSender;
    private boolean ownsTrack = true;

    private static native void free(long j);

    private static native long nativeGetDtmfSender(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native long nativeGetTrack(long j);

    private static native String nativeId(long j);

    private static native boolean nativeSetParameters(long j, RtpParameters rtpParameters);

    private static native boolean nativeSetTrack(long j, long j2);

    public RtpSender(long nativeRtpSender2) {
        this.nativeRtpSender = nativeRtpSender2;
        long track = nativeGetTrack(nativeRtpSender2);
        DtmfSender dtmfSender2 = null;
        this.cachedTrack = track != 0 ? new MediaStreamTrack(track) : null;
        long nativeDtmfSender = nativeGetDtmfSender(nativeRtpSender2);
        this.dtmfSender = nativeDtmfSender != 0 ? new DtmfSender(nativeDtmfSender) : dtmfSender2;
    }

    public boolean setTrack(MediaStreamTrack track, boolean takeOwnership) {
        if (!nativeSetTrack(this.nativeRtpSender, track == null ? 0 : track.nativeTrack)) {
            return false;
        }
        MediaStreamTrack mediaStreamTrack = this.cachedTrack;
        if (mediaStreamTrack != null && this.ownsTrack) {
            mediaStreamTrack.dispose();
        }
        this.cachedTrack = track;
        this.ownsTrack = takeOwnership;
        return true;
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public boolean setParameters(RtpParameters parameters) {
        return nativeSetParameters(this.nativeRtpSender, parameters);
    }

    public RtpParameters getParameters() {
        return nativeGetParameters(this.nativeRtpSender);
    }

    public String id() {
        return nativeId(this.nativeRtpSender);
    }

    public DtmfSender dtmf() {
        return this.dtmfSender;
    }

    public void dispose() {
        DtmfSender dtmfSender2 = this.dtmfSender;
        if (dtmfSender2 != null) {
            dtmfSender2.dispose();
        }
        MediaStreamTrack mediaStreamTrack = this.cachedTrack;
        if (mediaStreamTrack != null && this.ownsTrack) {
            mediaStreamTrack.dispose();
        }
        free(this.nativeRtpSender);
    }
}
