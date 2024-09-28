package im.bclpbkiauv.ui.components;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.AnimatedFileDrawableStream;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AnimatedFileDrawable extends BitmapDrawable implements Animatable {
    public static final int PARAM_NUM_AUDIO_FRAME_SIZE = 5;
    public static final int PARAM_NUM_BITRATE = 3;
    public static final int PARAM_NUM_COUNT = 9;
    public static final int PARAM_NUM_DURATION = 4;
    public static final int PARAM_NUM_FRAMERATE = 7;
    public static final int PARAM_NUM_HEIGHT = 2;
    public static final int PARAM_NUM_IS_AVC = 0;
    public static final int PARAM_NUM_ROTATION = 8;
    public static final int PARAM_NUM_VIDEO_FRAME_SIZE = 6;
    public static final int PARAM_NUM_WIDTH = 1;
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.DiscardPolicy());
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private RectF actualDrawRect = new RectF();
    private boolean applyTransformation;
    /* access modifiers changed from: private */
    public Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public int backgroundBitmapTime;
    /* access modifiers changed from: private */
    public BitmapShader backgroundShader;
    /* access modifiers changed from: private */
    public int currentAccount;
    /* access modifiers changed from: private */
    public DispatchQueue decodeQueue;
    private boolean decodeSingleFrame;
    /* access modifiers changed from: private */
    public boolean decoderCreated;
    /* access modifiers changed from: private */
    public boolean destroyWhenDone;
    private final Rect dstRect = new Rect();
    /* access modifiers changed from: private */
    public int invalidateAfter = 50;
    /* access modifiers changed from: private */
    public volatile boolean isRecycled;
    private volatile boolean isRunning;
    /* access modifiers changed from: private */
    public long lastFrameDecodeTime;
    private long lastFrameTime;
    /* access modifiers changed from: private */
    public int lastTimeStamp;
    private Runnable loadFrameRunnable = new Runnable() {
        public void run() {
            if (!AnimatedFileDrawable.this.isRecycled) {
                if (!AnimatedFileDrawable.this.decoderCreated && AnimatedFileDrawable.this.nativePtr == 0) {
                    AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
                    animatedFileDrawable.nativePtr = AnimatedFileDrawable.createDecoder(animatedFileDrawable.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.currentAccount, AnimatedFileDrawable.this.streamFileSize, AnimatedFileDrawable.this.stream, false);
                    boolean unused = AnimatedFileDrawable.this.decoderCreated = true;
                }
                try {
                    if (AnimatedFileDrawable.this.nativePtr == 0 && AnimatedFileDrawable.this.metaData[0] != 0) {
                        if (AnimatedFileDrawable.this.metaData[1] != 0) {
                            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                            return;
                        }
                    }
                    if (AnimatedFileDrawable.this.backgroundBitmap == null && AnimatedFileDrawable.this.metaData[0] > 0 && AnimatedFileDrawable.this.metaData[1] > 0) {
                        Bitmap unused2 = AnimatedFileDrawable.this.backgroundBitmap = Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Bitmap.Config.ARGB_8888);
                        if (!(AnimatedFileDrawable.this.backgroundShader != null || AnimatedFileDrawable.this.backgroundBitmap == null || AnimatedFileDrawable.this.roundRadius == 0)) {
                            BitmapShader unused3 = AnimatedFileDrawable.this.backgroundShader = new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                boolean seekWas = false;
                if (AnimatedFileDrawable.this.pendingSeekTo >= 0) {
                    AnimatedFileDrawable.this.metaData[3] = (int) AnimatedFileDrawable.this.pendingSeekTo;
                    long seekTo = AnimatedFileDrawable.this.pendingSeekTo;
                    synchronized (AnimatedFileDrawable.this.sync) {
                        long unused4 = AnimatedFileDrawable.this.pendingSeekTo = -1;
                    }
                    seekWas = true;
                    if (AnimatedFileDrawable.this.stream != null) {
                        AnimatedFileDrawable.this.stream.reset();
                    }
                    AnimatedFileDrawable.seekToMs(AnimatedFileDrawable.this.nativePtr, seekTo, true);
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    long unused5 = AnimatedFileDrawable.this.lastFrameDecodeTime = System.currentTimeMillis();
                    if (AnimatedFileDrawable.getVideoFrame(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData, AnimatedFileDrawable.this.backgroundBitmap.getRowBytes(), false) == 0) {
                        AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (seekWas) {
                        int unused6 = AnimatedFileDrawable.this.lastTimeStamp = AnimatedFileDrawable.this.metaData[3];
                    }
                    int unused7 = AnimatedFileDrawable.this.backgroundBitmapTime = AnimatedFileDrawable.this.metaData[3];
                }
            }
            AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
        }
    };
    /* access modifiers changed from: private */
    public Runnable loadFrameTask;
    protected final Runnable mInvalidateTask = new Runnable() {
        public final void run() {
            AnimatedFileDrawable.this.lambda$new$0$AnimatedFileDrawable();
        }
    };
    private final Runnable mStartTask = new Runnable() {
        public final void run() {
            AnimatedFileDrawable.this.lambda$new$1$AnimatedFileDrawable();
        }
    };
    /* access modifiers changed from: private */
    public final int[] metaData = new int[5];
    public volatile long nativePtr;
    /* access modifiers changed from: private */
    public Bitmap nextRenderingBitmap;
    /* access modifiers changed from: private */
    public int nextRenderingBitmapTime;
    /* access modifiers changed from: private */
    public BitmapShader nextRenderingShader;
    /* access modifiers changed from: private */
    public View parentView;
    /* access modifiers changed from: private */
    public File path;
    /* access modifiers changed from: private */
    public boolean pendingRemoveLoading;
    /* access modifiers changed from: private */
    public int pendingRemoveLoadingFramesReset;
    /* access modifiers changed from: private */
    public volatile long pendingSeekTo = -1;
    /* access modifiers changed from: private */
    public volatile long pendingSeekToUI = -1;
    private boolean recycleWithSecond;
    /* access modifiers changed from: private */
    public Bitmap renderingBitmap;
    private int renderingBitmapTime;
    private BitmapShader renderingShader;
    /* access modifiers changed from: private */
    public int roundRadius;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    /* access modifiers changed from: private */
    public View secondParentView;
    private Matrix shaderMatrix = new Matrix();
    /* access modifiers changed from: private */
    public boolean singleFrameDecoded;
    /* access modifiers changed from: private */
    public AnimatedFileDrawableStream stream;
    /* access modifiers changed from: private */
    public long streamFileSize;
    /* access modifiers changed from: private */
    public final Object sync = new Object();
    /* access modifiers changed from: private */
    public Runnable uiRunnable = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr == 0) {
                if (AnimatedFileDrawable.this.renderingBitmap != null) {
                    AnimatedFileDrawable.this.renderingBitmap.recycle();
                    Bitmap unused = AnimatedFileDrawable.this.renderingBitmap = null;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.backgroundBitmap.recycle();
                    Bitmap unused2 = AnimatedFileDrawable.this.backgroundBitmap = null;
                }
                if (AnimatedFileDrawable.this.decodeQueue != null) {
                    AnimatedFileDrawable.this.decodeQueue.recycle();
                    DispatchQueue unused3 = AnimatedFileDrawable.this.decodeQueue = null;
                    return;
                }
                return;
            }
            if (AnimatedFileDrawable.this.stream != null && AnimatedFileDrawable.this.pendingRemoveLoading) {
                FileLoader.getInstance(AnimatedFileDrawable.this.currentAccount).removeLoadingVideo(AnimatedFileDrawable.this.stream.getDocument(), false, false);
            }
            if (AnimatedFileDrawable.this.pendingRemoveLoadingFramesReset <= 0) {
                boolean unused4 = AnimatedFileDrawable.this.pendingRemoveLoading = true;
            } else {
                AnimatedFileDrawable.access$1010(AnimatedFileDrawable.this);
            }
            boolean unused5 = AnimatedFileDrawable.this.singleFrameDecoded = true;
            Runnable unused6 = AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable animatedFileDrawable = AnimatedFileDrawable.this;
            Bitmap unused7 = animatedFileDrawable.nextRenderingBitmap = animatedFileDrawable.backgroundBitmap;
            AnimatedFileDrawable animatedFileDrawable2 = AnimatedFileDrawable.this;
            int unused8 = animatedFileDrawable2.nextRenderingBitmapTime = animatedFileDrawable2.backgroundBitmapTime;
            AnimatedFileDrawable animatedFileDrawable3 = AnimatedFileDrawable.this;
            BitmapShader unused9 = animatedFileDrawable3.nextRenderingShader = animatedFileDrawable3.backgroundShader;
            if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp) {
                int unused10 = AnimatedFileDrawable.this.lastTimeStamp = 0;
            }
            if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0) {
                AnimatedFileDrawable animatedFileDrawable4 = AnimatedFileDrawable.this;
                int unused11 = animatedFileDrawable4.invalidateAfter = animatedFileDrawable4.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp;
            }
            if (AnimatedFileDrawable.this.pendingSeekToUI >= 0 && AnimatedFileDrawable.this.pendingSeekTo == -1) {
                long unused12 = AnimatedFileDrawable.this.pendingSeekToUI = -1;
                int unused13 = AnimatedFileDrawable.this.invalidateAfter = 0;
            }
            AnimatedFileDrawable animatedFileDrawable5 = AnimatedFileDrawable.this;
            int unused14 = animatedFileDrawable5.lastTimeStamp = animatedFileDrawable5.metaData[3];
            if (AnimatedFileDrawable.this.secondParentView != null) {
                AnimatedFileDrawable.this.secondParentView.invalidate();
            } else if (AnimatedFileDrawable.this.parentView != null) {
                AnimatedFileDrawable.this.parentView.invalidate();
            }
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };
    /* access modifiers changed from: private */
    public Runnable uiRunnableNoFrame = new Runnable() {
        public void run() {
            if (AnimatedFileDrawable.this.destroyWhenDone && AnimatedFileDrawable.this.nativePtr != 0) {
                AnimatedFileDrawable.destroyDecoder(AnimatedFileDrawable.this.nativePtr);
                AnimatedFileDrawable.this.nativePtr = 0;
            }
            if (AnimatedFileDrawable.this.nativePtr == 0) {
                if (AnimatedFileDrawable.this.renderingBitmap != null) {
                    AnimatedFileDrawable.this.renderingBitmap.recycle();
                    Bitmap unused = AnimatedFileDrawable.this.renderingBitmap = null;
                }
                if (AnimatedFileDrawable.this.backgroundBitmap != null) {
                    AnimatedFileDrawable.this.backgroundBitmap.recycle();
                    Bitmap unused2 = AnimatedFileDrawable.this.backgroundBitmap = null;
                }
                if (AnimatedFileDrawable.this.decodeQueue != null) {
                    AnimatedFileDrawable.this.decodeQueue.recycle();
                    DispatchQueue unused3 = AnimatedFileDrawable.this.decodeQueue = null;
                    return;
                }
                return;
            }
            Runnable unused4 = AnimatedFileDrawable.this.loadFrameTask = null;
            AnimatedFileDrawable.this.scheduleNextGetFrame();
        }
    };
    private boolean useSharedQueue;

    /* access modifiers changed from: private */
    public static native long createDecoder(String str, int[] iArr, int i, long j, Object obj, boolean z);

    /* access modifiers changed from: private */
    public static native void destroyDecoder(long j);

    /* access modifiers changed from: private */
    public static native int getVideoFrame(long j, Bitmap bitmap, int[] iArr, int i, boolean z);

    public static native void getVideoInfo(String str, int[] iArr);

    private static native void prepareToSeek(long j);

    /* access modifiers changed from: private */
    public static native void seekToMs(long j, long j2, boolean z);

    private static native void stopDecoder(long j);

    static /* synthetic */ int access$1010(AnimatedFileDrawable x0) {
        int i = x0.pendingRemoveLoadingFramesReset;
        x0.pendingRemoveLoadingFramesReset = i - 1;
        return i;
    }

    public /* synthetic */ void lambda$new$0$AnimatedFileDrawable() {
        View view = this.secondParentView;
        if (view != null) {
            view.invalidate();
            return;
        }
        View view2 = this.parentView;
        if (view2 != null) {
            view2.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$1$AnimatedFileDrawable() {
        View view = this.secondParentView;
        if (view != null) {
            view.invalidate();
            return;
        }
        View view2 = this.parentView;
        if (view2 != null) {
            view2.invalidate();
        }
    }

    public AnimatedFileDrawable(File file, boolean createDecoder, long streamSize, TLRPC.Document document, Object parentObject, int account, boolean preview) {
        long j = streamSize;
        TLRPC.Document document2 = document;
        int i = account;
        this.path = file;
        this.streamFileSize = j;
        this.currentAccount = i;
        getPaint().setFlags(2);
        if (j == 0 || document2 == null) {
            Object obj = parentObject;
            boolean z = preview;
        } else {
            this.stream = new AnimatedFileDrawableStream(document2, parentObject, i, preview);
        }
        if (createDecoder) {
            this.nativePtr = createDecoder(file.getAbsolutePath(), this.metaData, this.currentAccount, this.streamFileSize, this.stream, preview);
            this.decoderCreated = true;
        }
    }

    public Bitmap getFrameAtTime(long ms) {
        if (!this.decoderCreated || this.nativePtr == 0) {
            return null;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(false);
            this.stream.reset();
        }
        seekToMs(this.nativePtr, ms, false);
        if (this.backgroundBitmap == null) {
            int[] iArr = this.metaData;
            this.backgroundBitmap = Bitmap.createBitmap(iArr[0], iArr[1], Bitmap.Config.ARGB_8888);
        }
        long j = this.nativePtr;
        Bitmap bitmap = this.backgroundBitmap;
        if (getVideoFrame(j, bitmap, this.metaData, bitmap.getRowBytes(), true) != 0) {
            return this.backgroundBitmap;
        }
        return null;
    }

    public void setParentView(View view) {
        if (this.parentView == null) {
            this.parentView = view;
        }
    }

    public void setSecondParentView(View view) {
        this.secondParentView = view;
        if (view == null && this.recycleWithSecond) {
            recycle();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
            scheduleNextGetFrame();
        }
    }

    public void seekTo(long ms, boolean removeLoading) {
        synchronized (this.sync) {
            this.pendingSeekTo = ms;
            this.pendingSeekToUI = ms;
            prepareToSeek(this.nativePtr);
            if (this.decoderCreated && this.stream != null) {
                this.stream.cancel(removeLoading);
                this.pendingRemoveLoading = removeLoading;
                this.pendingRemoveLoadingFramesReset = removeLoading ? 0 : 10;
            }
        }
    }

    public void recycle() {
        if (this.secondParentView != null) {
            this.recycleWithSecond = true;
            return;
        }
        this.isRunning = false;
        this.isRecycled = true;
        if (this.loadFrameTask == null) {
            if (this.nativePtr != 0) {
                destroyDecoder(this.nativePtr);
                this.nativePtr = 0;
            }
            Bitmap bitmap = this.renderingBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.renderingBitmap = null;
            }
            Bitmap bitmap2 = this.nextRenderingBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.nextRenderingBitmap = null;
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            if (dispatchQueue != null) {
                dispatchQueue.recycle();
                this.decodeQueue = null;
            }
        } else {
            this.destroyWhenDone = true;
        }
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
    }

    public void resetStream(boolean stop) {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        if (animatedFileDrawableStream != null) {
            animatedFileDrawableStream.cancel(true);
        }
        if (this.nativePtr == 0) {
            return;
        }
        if (stop) {
            stopDecoder(this.nativePtr);
        } else {
            prepareToSeek(this.nativePtr);
        }
    }

    protected static void runOnUiThread(Runnable task) {
        if (Looper.myLooper() == uiHandler.getLooper()) {
            task.run();
        } else {
            uiHandler.post(task);
        }
    }

    public void setUseSharedQueue(boolean value) {
        this.useSharedQueue = value;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    public int getOpacity() {
        return -2;
    }

    public void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            scheduleNextGetFrame();
            runOnUiThread(this.mStartTask);
        }
    }

    public float getCurrentProgress() {
        if (this.metaData[4] == 0) {
            return 0.0f;
        }
        if (this.pendingSeekToUI >= 0) {
            return ((float) this.pendingSeekToUI) / ((float) this.metaData[4]);
        }
        int[] iArr = this.metaData;
        return ((float) iArr[3]) / ((float) iArr[4]);
    }

    public int getCurrentProgressMs() {
        if (this.pendingSeekToUI >= 0) {
            return (int) this.pendingSeekToUI;
        }
        int i = this.nextRenderingBitmapTime;
        return i != 0 ? i : this.renderingBitmapTime;
    }

    public int getDurationMs() {
        return this.metaData[4];
    }

    /* access modifiers changed from: private */
    public void scheduleNextGetFrame() {
        if (this.loadFrameTask != null) {
            return;
        }
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            if (!this.isRunning) {
                boolean z = this.decodeSingleFrame;
                if (!z) {
                    return;
                }
                if (z && this.singleFrameDecoded) {
                    return;
                }
            }
            long ms = 0;
            if (this.lastFrameDecodeTime != 0) {
                int i = this.invalidateAfter;
                ms = Math.min((long) i, Math.max(0, ((long) i) - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
            }
            if (this.useSharedQueue) {
                ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = executor;
                Runnable runnable = this.loadFrameRunnable;
                this.loadFrameTask = runnable;
                scheduledThreadPoolExecutor.schedule(runnable, ms, TimeUnit.MILLISECONDS);
                return;
            }
            if (this.decodeQueue == null) {
                this.decodeQueue = new DispatchQueue("decodeQueue" + this);
            }
            DispatchQueue dispatchQueue = this.decodeQueue;
            Runnable runnable2 = this.loadFrameRunnable;
            this.loadFrameTask = runnable2;
            dispatchQueue.postRunnable(runnable2, ms);
        }
    }

    public boolean isLoadingStream() {
        AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
        return animatedFileDrawableStream != null && animatedFileDrawableStream.isWaitingForLoad();
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    public int getIntrinsicWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return width;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap;
        Canvas canvas2 = canvas;
        if ((this.nativePtr != 0 || !this.decoderCreated) && !this.destroyWhenDone) {
            long now = System.currentTimeMillis();
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && (this.renderingBitmap == null || Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter))) {
                    this.renderingBitmap = this.nextRenderingBitmap;
                    this.renderingBitmapTime = this.nextRenderingBitmapTime;
                    this.renderingShader = this.nextRenderingShader;
                    this.nextRenderingBitmap = null;
                    this.nextRenderingBitmapTime = 0;
                    this.nextRenderingShader = null;
                    this.lastFrameTime = now;
                }
            } else if (!this.isRunning && this.decodeSingleFrame && Math.abs(now - this.lastFrameTime) >= ((long) this.invalidateAfter) && (bitmap = this.nextRenderingBitmap) != null) {
                this.renderingBitmap = bitmap;
                this.renderingBitmapTime = this.nextRenderingBitmapTime;
                this.renderingShader = this.nextRenderingShader;
                this.nextRenderingBitmap = null;
                this.nextRenderingBitmapTime = 0;
                this.nextRenderingShader = null;
                this.lastFrameTime = now;
            }
            Bitmap bitmap2 = this.renderingBitmap;
            if (bitmap2 != null) {
                if (this.applyTransformation) {
                    int bitmapW = bitmap2.getWidth();
                    int bitmapH = this.renderingBitmap.getHeight();
                    int[] iArr = this.metaData;
                    if (iArr[2] == 90 || iArr[2] == 270) {
                        int temp = bitmapW;
                        bitmapW = bitmapH;
                        bitmapH = temp;
                    }
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) bitmapW);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) bitmapH);
                    this.applyTransformation = false;
                }
                if (this.roundRadius != 0) {
                    float max = Math.max(this.scaleX, this.scaleY);
                    if (this.renderingShader == null) {
                        this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    }
                    Paint paint = getPaint();
                    paint.setShader(this.renderingShader);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate((float) this.dstRect.left, (float) this.dstRect.top);
                    int[] iArr2 = this.metaData;
                    if (iArr2[2] == 90) {
                        this.shaderMatrix.preRotate(90.0f);
                        this.shaderMatrix.preTranslate(0.0f, (float) (-this.dstRect.width()));
                    } else if (iArr2[2] == 180) {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (iArr2[2] == 270) {
                        this.shaderMatrix.preRotate(270.0f);
                        this.shaderMatrix.preTranslate((float) (-this.dstRect.height()), 0.0f);
                    }
                    this.shaderMatrix.preScale(this.scaleX, this.scaleY);
                    this.renderingShader.setLocalMatrix(this.shaderMatrix);
                    RectF rectF = this.actualDrawRect;
                    int i = this.roundRadius;
                    canvas2.drawRoundRect(rectF, (float) i, (float) i, paint);
                } else {
                    canvas2.translate((float) this.dstRect.left, (float) this.dstRect.top);
                    int[] iArr3 = this.metaData;
                    if (iArr3[2] == 90) {
                        canvas2.rotate(90.0f);
                        canvas2.translate(0.0f, (float) (-this.dstRect.width()));
                    } else if (iArr3[2] == 180) {
                        canvas2.rotate(180.0f);
                        canvas2.translate((float) (-this.dstRect.width()), (float) (-this.dstRect.height()));
                    } else if (iArr3[2] == 270) {
                        canvas2.rotate(270.0f);
                        canvas2.translate((float) (-this.dstRect.height()), 0.0f);
                    }
                    canvas2.scale(this.scaleX, this.scaleY);
                    canvas2.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                }
                if (this.isRunning) {
                    long timeToNextFrame = Math.max(1, (((long) this.invalidateAfter) - (now - this.lastFrameTime)) - 17);
                    uiHandler.removeCallbacks(this.mInvalidateTask);
                    uiHandler.postDelayed(this.mInvalidateTask, Math.min(timeToNextFrame, (long) this.invalidateAfter));
                }
            }
        }
    }

    public int getMinimumHeight() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[0] : iArr[1];
        }
        int height = i;
        if (height == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return height;
    }

    public int getMinimumWidth() {
        int i = 0;
        if (this.decoderCreated) {
            int[] iArr = this.metaData;
            i = (iArr[2] == 90 || iArr[2] == 270) ? this.metaData[1] : iArr[0];
        }
        int width = i;
        if (width == 0) {
            return AndroidUtilities.dp(100.0f);
        }
        return width;
    }

    public Bitmap getRenderingBitmap() {
        return this.renderingBitmap;
    }

    public Bitmap getNextRenderingBitmap() {
        return this.nextRenderingBitmap;
    }

    public Bitmap getBackgroundBitmap() {
        return this.backgroundBitmap;
    }

    public Bitmap getAnimatedBitmap() {
        Bitmap bitmap = this.renderingBitmap;
        if (bitmap != null) {
            return bitmap;
        }
        Bitmap bitmap2 = this.nextRenderingBitmap;
        if (bitmap2 != null) {
            return bitmap2;
        }
        return null;
    }

    public void setActualDrawRect(float x, float y, float width, float height) {
        this.actualDrawRect.set(x, y, x + width, y + height);
    }

    public void setRoundRadius(int value) {
        this.roundRadius = value;
        getPaint().setFlags(3);
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }

    public int getOrientation() {
        return this.metaData[2];
    }

    public AnimatedFileDrawable makeCopy() {
        AnimatedFileDrawable drawable;
        if (this.stream != null) {
            File file = this.path;
            long j = this.streamFileSize;
            TLRPC.Document document = this.stream.getDocument();
            Object parentObject = this.stream.getParentObject();
            int i = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream = this.stream;
            drawable = new AnimatedFileDrawable(file, false, j, document, parentObject, i, animatedFileDrawableStream != null && animatedFileDrawableStream.isPreview());
        } else {
            File file2 = this.path;
            long j2 = this.streamFileSize;
            int i2 = this.currentAccount;
            AnimatedFileDrawableStream animatedFileDrawableStream2 = this.stream;
            drawable = new AnimatedFileDrawable(file2, false, j2, (TLRPC.Document) null, (Object) null, i2, animatedFileDrawableStream2 != null && animatedFileDrawableStream2.isPreview());
        }
        int[] iArr = drawable.metaData;
        int[] iArr2 = this.metaData;
        iArr[0] = iArr2[0];
        iArr[1] = iArr2[1];
        return drawable;
    }
}
