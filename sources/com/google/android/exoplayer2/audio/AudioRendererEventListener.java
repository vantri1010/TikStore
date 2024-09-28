package com.google.android.exoplayer2.audio;

import android.os.Handler;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;

public interface AudioRendererEventListener {
    void onAudioDecoderInitialized(String str, long j, long j2);

    void onAudioDisabled(DecoderCounters decoderCounters);

    void onAudioEnabled(DecoderCounters decoderCounters);

    void onAudioInputFormatChanged(Format format);

    void onAudioSessionId(int i);

    void onAudioSinkUnderrun(int i, long j, long j2);

    /* renamed from: com.google.android.exoplayer2.audio.AudioRendererEventListener$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onAudioEnabled(AudioRendererEventListener _this, DecoderCounters counters) {
        }

        public static void $default$onAudioSessionId(AudioRendererEventListener _this, int audioSessionId) {
        }

        public static void $default$onAudioDecoderInitialized(AudioRendererEventListener _this, String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        }

        public static void $default$onAudioInputFormatChanged(AudioRendererEventListener _this, Format format) {
        }

        public static void $default$onAudioSinkUnderrun(AudioRendererEventListener _this, int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        }

        public static void $default$onAudioDisabled(AudioRendererEventListener _this, DecoderCounters counters) {
        }
    }

    public static final class EventDispatcher {
        private final Handler handler;
        private final AudioRendererEventListener listener;

        public EventDispatcher(Handler handler2, AudioRendererEventListener listener2) {
            this.handler = listener2 != null ? (Handler) Assertions.checkNotNull(handler2) : null;
            this.listener = listener2;
        }

        public void enabled(DecoderCounters decoderCounters) {
            if (this.listener != null) {
                this.handler.post(new Runnable(decoderCounters) {
                    private final /* synthetic */ DecoderCounters f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$enabled$0$AudioRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$enabled$0$AudioRendererEventListener$EventDispatcher(DecoderCounters decoderCounters) {
            this.listener.onAudioEnabled(decoderCounters);
        }

        public void decoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            if (this.listener != null) {
                this.handler.post(new Runnable(decoderName, initializedTimestampMs, initializationDurationMs) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ long f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$decoderInitialized$1$AudioRendererEventListener$EventDispatcher(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$decoderInitialized$1$AudioRendererEventListener$EventDispatcher(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            this.listener.onAudioDecoderInitialized(decoderName, initializedTimestampMs, initializationDurationMs);
        }

        public void inputFormatChanged(Format format) {
            if (this.listener != null) {
                this.handler.post(new Runnable(format) {
                    private final /* synthetic */ Format f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$inputFormatChanged$2$AudioRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$inputFormatChanged$2$AudioRendererEventListener$EventDispatcher(Format format) {
            this.listener.onAudioInputFormatChanged(format);
        }

        public void audioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            if (this.listener != null) {
                this.handler.post(new Runnable(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ long f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$audioTrackUnderrun$3$AudioRendererEventListener$EventDispatcher(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$audioTrackUnderrun$3$AudioRendererEventListener$EventDispatcher(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            this.listener.onAudioSinkUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
        }

        public void disabled(DecoderCounters counters) {
            counters.ensureUpdated();
            if (this.listener != null) {
                this.handler.post(new Runnable(counters) {
                    private final /* synthetic */ DecoderCounters f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$disabled$4$AudioRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$disabled$4$AudioRendererEventListener$EventDispatcher(DecoderCounters counters) {
            counters.ensureUpdated();
            this.listener.onAudioDisabled(counters);
        }

        public void audioSessionId(int audioSessionId) {
            if (this.listener != null) {
                this.handler.post(new Runnable(audioSessionId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AudioRendererEventListener.EventDispatcher.this.lambda$audioSessionId$5$AudioRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$audioSessionId$5$AudioRendererEventListener$EventDispatcher(int audioSessionId) {
            this.listener.onAudioSessionId(audioSessionId);
        }
    }
}
