package org.webrtc.ali;

import org.webrtc.ali.VideoCapturer;

class AndroidVideoTrackSourceObserver implements VideoCapturer.CapturerObserver {
    private final long nativeSource;

    private native void nativeCapturerStarted(long j, boolean z);

    private native void nativeCapturerStopped(long j);

    private native void nativeOnByteBufferFrameCaptured(long j, byte[] bArr, int i, int i2, int i3, int i4, long j2);

    private native void nativeOnTextureFrameCaptured(long j, int i, int i2, int i3, float[] fArr, int i4, long j2);

    public AndroidVideoTrackSourceObserver(long nativeSource2) {
        this.nativeSource = nativeSource2;
    }

    public void onCapturerStarted(boolean success) {
        nativeCapturerStarted(this.nativeSource, success);
    }

    public void onCapturerStopped() {
        nativeCapturerStopped(this.nativeSource);
    }

    public void onByteBufferFrameCaptured(byte[] data, int width, int height, int rotation, long timeStamp) {
        nativeOnByteBufferFrameCaptured(this.nativeSource, data, data.length, width, height, rotation, timeStamp);
    }

    public void onTextureFrameCaptured(int width, int height, int oesTextureId, float[] transformMatrix, int rotation, long timestamp) {
        nativeOnTextureFrameCaptured(this.nativeSource, width, height, oesTextureId, transformMatrix, rotation, timestamp);
    }
}
