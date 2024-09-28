package com.google.android.exoplayer2.ext.opus;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;

public final class OpusLibrary {
    public static native String opusGetVersion();

    public static native boolean opusIsSecureDecodeSupported();

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.opus");
    }

    private OpusLibrary() {
    }

    public static String getVersion() {
        return opusGetVersion();
    }
}
