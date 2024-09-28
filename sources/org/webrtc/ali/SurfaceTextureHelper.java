package org.webrtc.ali;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import org.webrtc.ali.EglBase;

public class SurfaceTextureHelper {
    private static final String TAG = "SurfaceTextureHelper";
    private final EglBase eglBase;
    private final Handler handler;
    /* access modifiers changed from: private */
    public boolean hasPendingTexture;
    /* access modifiers changed from: private */
    public boolean isQuitting;
    /* access modifiers changed from: private */
    public volatile boolean isTextureInUse;
    /* access modifiers changed from: private */
    public OnTextureFrameAvailableListener listener;
    private final int oesTextureId;
    /* access modifiers changed from: private */
    public OnTextureFrameAvailableListener pendingListener;
    final Runnable setListenerRunnable;
    private final SurfaceTexture surfaceTexture;
    /* access modifiers changed from: private */
    public YuvConverter yuvConverter;

    public interface OnTextureFrameAvailableListener {
        void onTextureFrameAvailable(int i, float[] fArr, long j);
    }

    public static SurfaceTextureHelper create(final String threadName, final EglBase.Context sharedContext) {
        HandlerThread thread = new HandlerThread(threadName);
        thread.start();
        final Handler handler2 = new Handler(thread.getLooper());
        return (SurfaceTextureHelper) ThreadUtils.invokeAtFrontUninterruptibly(handler2, new Callable<SurfaceTextureHelper>() {
            public SurfaceTextureHelper call() {
                try {
                    return new SurfaceTextureHelper(sharedContext, handler2);
                } catch (RuntimeException e) {
                    Logging.e(SurfaceTextureHelper.TAG, threadName + " create failure", e);
                    return null;
                }
            }
        });
    }

    private SurfaceTextureHelper(EglBase.Context sharedContext, Handler handler2) {
        this.hasPendingTexture = false;
        this.isTextureInUse = false;
        this.isQuitting = false;
        this.setListenerRunnable = new Runnable() {
            public void run() {
                Logging.d(SurfaceTextureHelper.TAG, "Setting listener to " + SurfaceTextureHelper.this.pendingListener);
                SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.this;
                OnTextureFrameAvailableListener unused = surfaceTextureHelper.listener = surfaceTextureHelper.pendingListener;
                OnTextureFrameAvailableListener unused2 = SurfaceTextureHelper.this.pendingListener = null;
                if (SurfaceTextureHelper.this.hasPendingTexture) {
                    SurfaceTextureHelper.this.updateTexImage();
                    boolean unused3 = SurfaceTextureHelper.this.hasPendingTexture = false;
                }
            }
        };
        if (handler2.getLooper().getThread() == Thread.currentThread()) {
            this.handler = handler2;
            EglBase create = EglBase.create(sharedContext, EglBase.CONFIG_PIXEL_BUFFER);
            this.eglBase = create;
            try {
                create.createDummyPbufferSurface();
                this.eglBase.makeCurrent();
                this.oesTextureId = GlUtil.generateTexture(36197);
                SurfaceTexture surfaceTexture2 = new SurfaceTexture(this.oesTextureId);
                this.surfaceTexture = surfaceTexture2;
                surfaceTexture2.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        boolean unused = SurfaceTextureHelper.this.hasPendingTexture = true;
                        SurfaceTextureHelper.this.tryDeliverTextureFrame();
                    }
                });
            } catch (RuntimeException e) {
                this.eglBase.release();
                handler2.getLooper().quit();
                throw e;
            }
        } else {
            throw new IllegalStateException("SurfaceTextureHelper must be created on the handler thread");
        }
    }

    public void startListening(OnTextureFrameAvailableListener listener2) {
        if (this.listener == null && this.pendingListener == null) {
            this.pendingListener = listener2;
            this.handler.post(this.setListenerRunnable);
            return;
        }
        throw new IllegalStateException("SurfaceTextureHelper listener has already been set.");
    }

    public void stopListening() {
        Logging.d(TAG, "stopListening()");
        this.handler.removeCallbacks(this.setListenerRunnable);
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, (Runnable) new Runnable() {
            public void run() {
                OnTextureFrameAvailableListener unused = SurfaceTextureHelper.this.listener = null;
                OnTextureFrameAvailableListener unused2 = SurfaceTextureHelper.this.pendingListener = null;
            }
        });
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.surfaceTexture;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public void returnTextureFrame() {
        this.handler.post(new Runnable() {
            public void run() {
                boolean unused = SurfaceTextureHelper.this.isTextureInUse = false;
                if (SurfaceTextureHelper.this.isQuitting) {
                    SurfaceTextureHelper.this.release();
                } else {
                    SurfaceTextureHelper.this.tryDeliverTextureFrame();
                }
            }
        });
    }

    public boolean isTextureInUse() {
        return this.isTextureInUse;
    }

    public void dispose() {
        Logging.d(TAG, "dispose()");
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, (Runnable) new Runnable() {
            public void run() {
                boolean unused = SurfaceTextureHelper.this.isQuitting = true;
                if (!SurfaceTextureHelper.this.isTextureInUse) {
                    SurfaceTextureHelper.this.release();
                }
            }
        });
    }

    public void textureToYUV(ByteBuffer buf, int width, int height, int stride, int textureId, float[] transformMatrix) {
        if (textureId == this.oesTextureId) {
            final ByteBuffer byteBuffer = buf;
            final int i = width;
            final int i2 = height;
            final int i3 = stride;
            final int i4 = textureId;
            final float[] fArr = transformMatrix;
            ThreadUtils.invokeAtFrontUninterruptibly(this.handler, (Runnable) new Runnable() {
                public void run() {
                    if (SurfaceTextureHelper.this.yuvConverter == null) {
                        YuvConverter unused = SurfaceTextureHelper.this.yuvConverter = new YuvConverter();
                    }
                    SurfaceTextureHelper.this.yuvConverter.convert(byteBuffer, i, i2, i3, i4, fArr);
                }
            });
            return;
        }
        throw new IllegalStateException("textureToByteBuffer called with unexpected textureId");
    }

    /* access modifiers changed from: private */
    public void updateTexImage() {
        synchronized (EglBase.lock) {
            this.surfaceTexture.updateTexImage();
        }
    }

    /* access modifiers changed from: private */
    public void tryDeliverTextureFrame() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        } else if (!this.isQuitting && this.hasPendingTexture && !this.isTextureInUse && this.listener != null) {
            this.isTextureInUse = true;
            this.hasPendingTexture = false;
            updateTexImage();
            float[] transformMatrix = new float[16];
            this.surfaceTexture.getTransformMatrix(transformMatrix);
            this.listener.onTextureFrameAvailable(this.oesTextureId, transformMatrix, this.surfaceTexture.getTimestamp());
        }
    }

    /* access modifiers changed from: private */
    public void release() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        } else if (this.isTextureInUse || !this.isQuitting) {
            throw new IllegalStateException("Unexpected release.");
        } else {
            YuvConverter yuvConverter2 = this.yuvConverter;
            if (yuvConverter2 != null) {
                yuvConverter2.release();
            }
            GLES20.glDeleteTextures(1, new int[]{this.oesTextureId}, 0);
            this.surfaceTexture.release();
            this.eglBase.release();
            this.handler.getLooper().quit();
        }
    }
}
