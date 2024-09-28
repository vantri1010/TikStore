package org.webrtc.ali;

import org.webrtc.ali.Logging;

public class CallSessionFileRotatingLogSink {
    private long nativeSink;

    private static native long nativeAddSink(String str, int i, int i2);

    private static native void nativeDeleteSink(long j);

    private static native byte[] nativeGetLogData(String str);

    public static byte[] getLogData(String dirPath) {
        return nativeGetLogData(dirPath);
    }

    public CallSessionFileRotatingLogSink(String dirPath, int maxFileSize, Logging.Severity severity) {
        this.nativeSink = nativeAddSink(dirPath, maxFileSize, severity.ordinal());
    }

    public void dispose() {
        long j = this.nativeSink;
        if (j != 0) {
            nativeDeleteSink(j);
            this.nativeSink = 0;
        }
    }
}
