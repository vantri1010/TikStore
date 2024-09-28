package com.google.android.exoplayer2.drm;

public interface DefaultDrmSessionEventListener {
    void onDrmKeysLoaded();

    void onDrmKeysRemoved();

    void onDrmKeysRestored();

    void onDrmSessionAcquired();

    void onDrmSessionManagerError(Exception exc);

    void onDrmSessionReleased();

    /* renamed from: com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onDrmSessionAcquired(DefaultDrmSessionEventListener _this) {
        }

        public static void $default$onDrmSessionReleased(DefaultDrmSessionEventListener _this) {
        }
    }
}
