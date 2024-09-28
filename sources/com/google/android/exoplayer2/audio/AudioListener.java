package com.google.android.exoplayer2.audio;

public interface AudioListener {
    void onAudioAttributesChanged(AudioAttributes audioAttributes);

    void onAudioSessionId(int i);

    void onVolumeChanged(float f);

    /* renamed from: com.google.android.exoplayer2.audio.AudioListener$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onAudioSessionId(AudioListener _this, int audioSessionId) {
        }

        public static void $default$onAudioAttributesChanged(AudioListener _this, AudioAttributes audioAttributes) {
        }

        public static void $default$onVolumeChanged(AudioListener _this, float volume) {
        }
    }
}
