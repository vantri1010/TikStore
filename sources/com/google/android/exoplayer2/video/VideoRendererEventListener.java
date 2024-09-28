package com.google.android.exoplayer2.video;

import android.os.Handler;
import android.view.Surface;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public interface VideoRendererEventListener {
    void onDroppedFrames(int i, long j);

    void onRenderedFirstFrame(Surface surface);

    void onVideoDecoderInitialized(String str, long j, long j2);

    void onVideoDisabled(DecoderCounters decoderCounters);

    void onVideoEnabled(DecoderCounters decoderCounters);

    void onVideoInputFormatChanged(Format format);

    void onVideoSizeChanged(int i, int i2, int i3, float f);

    /* renamed from: com.google.android.exoplayer2.video.VideoRendererEventListener$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onVideoEnabled(VideoRendererEventListener _this, DecoderCounters counters) {
        }

        public static void $default$onVideoDecoderInitialized(VideoRendererEventListener _this, String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        }

        public static void $default$onVideoInputFormatChanged(VideoRendererEventListener _this, Format format) {
        }

        public static void $default$onDroppedFrames(VideoRendererEventListener _this, int count, long elapsedMs) {
        }

        public static void $default$onVideoSizeChanged(VideoRendererEventListener _this, int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        }

        public static void $default$onRenderedFirstFrame(VideoRendererEventListener _this, Surface surface) {
        }

        public static void $default$onVideoDisabled(VideoRendererEventListener _this, DecoderCounters counters) {
        }
    }

    public static final class EventDispatcher {
        private final Handler handler;
        private final VideoRendererEventListener listener;

        public EventDispatcher(Handler handler2, VideoRendererEventListener listener2) {
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
                        VideoRendererEventListener.EventDispatcher.this.lambda$enabled$0$VideoRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$enabled$0$VideoRendererEventListener$EventDispatcher(DecoderCounters decoderCounters) {
            this.listener.onVideoEnabled(decoderCounters);
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
                        VideoRendererEventListener.EventDispatcher.this.lambda$decoderInitialized$1$VideoRendererEventListener$EventDispatcher(this.f$1, this.f$2, this.f$3);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$decoderInitialized$1$VideoRendererEventListener$EventDispatcher(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            this.listener.onVideoDecoderInitialized(decoderName, initializedTimestampMs, initializationDurationMs);
        }

        public void inputFormatChanged(Format format) {
            if (this.listener != null) {
                this.handler.post(new Runnable(format) {
                    private final /* synthetic */ Format f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.lambda$inputFormatChanged$2$VideoRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$inputFormatChanged$2$VideoRendererEventListener$EventDispatcher(Format format) {
            this.listener.onVideoInputFormatChanged(format);
        }

        public void droppedFrames(int droppedFrameCount, long elapsedMs) {
            if (this.listener != null) {
                this.handler.post(new Runnable(droppedFrameCount, elapsedMs) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.lambda$droppedFrames$3$VideoRendererEventListener$EventDispatcher(this.f$1, this.f$2);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$droppedFrames$3$VideoRendererEventListener$EventDispatcher(int droppedFrameCount, long elapsedMs) {
            this.listener.onDroppedFrames(droppedFrameCount, elapsedMs);
        }

        public void videoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (this.listener != null) {
                this.handler.post(new Runnable(width, height, unappliedRotationDegrees, pixelWidthHeightRatio) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;
                    private final /* synthetic */ float f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.lambda$videoSizeChanged$4$VideoRendererEventListener$EventDispatcher(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$videoSizeChanged$4$VideoRendererEventListener$EventDispatcher(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            this.listener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }

        public void renderedFirstFrame(Surface surface) {
            if (this.listener != null) {
                this.handler.post(new Runnable(surface) {
                    private final /* synthetic */ Surface f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        VideoRendererEventListener.EventDispatcher.this.lambda$renderedFirstFrame$5$VideoRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$renderedFirstFrame$5$VideoRendererEventListener$EventDispatcher(Surface surface) {
            this.listener.onRenderedFirstFrame(surface);
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
                        VideoRendererEventListener.EventDispatcher.this.lambda$disabled$6$VideoRendererEventListener$EventDispatcher(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$disabled$6$VideoRendererEventListener$EventDispatcher(DecoderCounters counters) {
            counters.ensureUpdated();
            this.listener.onVideoDisabled(counters);
        }
    }
}
