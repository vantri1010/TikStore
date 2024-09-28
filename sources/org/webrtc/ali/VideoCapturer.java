package org.webrtc.ali;

import android.content.Context;

public interface VideoCapturer {

    public interface CapturerObserver {
        void onByteBufferFrameCaptured(byte[] bArr, int i, int i2, int i3, long j);

        void onCapturerStarted(boolean z);

        void onCapturerStopped();

        void onTextureFrameCaptured(int i, int i2, int i3, float[] fArr, int i4, long j);
    }

    void changeCaptureFormat(int i, int i2, int i3);

    void dispose();

    void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver);

    boolean isScreencast();

    void startCapture(int i, int i2, int i3);

    void stopCapture() throws InterruptedException;
}
