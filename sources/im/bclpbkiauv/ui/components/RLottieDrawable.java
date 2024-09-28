package im.bclpbkiauv.ui.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RLottieDrawable extends BitmapDrawable implements Animatable {
    private static byte[] buffer = new byte[4096];
    private static ExecutorService loadFrameRunnableQueue = Executors.newCachedThreadPool();
    /* access modifiers changed from: private */
    public static ThreadPoolExecutor lottieCacheGenerateQueue;
    private static byte[] readBuffer = new byte[65536];
    /* access modifiers changed from: private */
    public static final Handler uiHandler = new Handler(Looper.getMainLooper());
    private boolean applyTransformation;
    private boolean applyingLayerColors;
    /* access modifiers changed from: private */
    public int autoRepeat;
    private int autoRepeatPlayCount;
    /* access modifiers changed from: private */
    public volatile Bitmap backgroundBitmap;
    /* access modifiers changed from: private */
    public Runnable cacheGenerateTask;
    /* access modifiers changed from: private */
    public int currentFrame;
    private View currentParentView;
    private boolean decodeSingleFrame;
    /* access modifiers changed from: private */
    public boolean destroyWhenDone;
    private final Rect dstRect;
    private boolean forceFrameRedraw;
    /* access modifiers changed from: private */
    public int height;
    /* access modifiers changed from: private */
    public volatile boolean isRecycled;
    /* access modifiers changed from: private */
    public volatile boolean isRunning;
    private long lastFrameTime;
    private Runnable loadFrameRunnable;
    /* access modifiers changed from: private */
    public Runnable loadFrameTask;
    /* access modifiers changed from: private */
    public final int[] metaData;
    /* access modifiers changed from: private */
    public volatile long nativePtr;
    /* access modifiers changed from: private */
    public boolean needGenerateCache;
    private HashMap<String, Integer> newColorUpdates;
    /* access modifiers changed from: private */
    public volatile boolean nextFrameIsLast;
    /* access modifiers changed from: private */
    public volatile Bitmap nextRenderingBitmap;
    private ArrayList<WeakReference<View>> parentViews;
    /* access modifiers changed from: private */
    public volatile HashMap<String, Integer> pendingColorUpdates;
    private volatile Bitmap renderingBitmap;
    private float scaleX;
    private float scaleY;
    /* access modifiers changed from: private */
    public boolean shouldLimitFps;
    /* access modifiers changed from: private */
    public boolean singleFrameDecoded;
    private int timeBetweenFrames;
    /* access modifiers changed from: private */
    public Runnable uiRunnable;
    /* access modifiers changed from: private */
    public Runnable uiRunnableCacheFinished;
    /* access modifiers changed from: private */
    public Runnable uiRunnableGenerateCache;
    private Runnable uiRunnableLastFrame;
    /* access modifiers changed from: private */
    public Runnable uiRunnableNoFrame;
    private HashMap<Integer, Integer> vibrationPattern;
    /* access modifiers changed from: private */
    public int width;

    private static native long create(String str, int[] iArr, boolean z, int[] iArr2);

    /* access modifiers changed from: private */
    public static native void createCache(long j, Bitmap bitmap, int i, int i2, int i3);

    private static native long createWithJson(String str, String str2, int[] iArr);

    private static native void destroy(long j);

    /* access modifiers changed from: private */
    public static native int getFrame(long j, int i, Bitmap bitmap, int i2, int i3, int i4);

    /* access modifiers changed from: private */
    public static native void setLayerColor(long j, String str, int i);

    static /* synthetic */ int access$2808(RLottieDrawable x0) {
        int i = x0.autoRepeatPlayCount;
        x0.autoRepeatPlayCount = i + 1;
        return i;
    }

    private void checkRunningTasks() {
        Runnable runnable = this.cacheGenerateTask;
        if (runnable != null && lottieCacheGenerateQueue.remove(runnable)) {
            this.cacheGenerateTask = null;
        }
        if (!hasParentView() && this.nextRenderingBitmap != null && this.loadFrameTask != null) {
            this.loadFrameTask = null;
            this.nextRenderingBitmap = null;
        }
    }

    /* access modifiers changed from: private */
    public void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null && this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
        }
        if (this.nativePtr == 0) {
            recycleResources();
            return;
        }
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }

    private void recycleResources() {
        if (this.renderingBitmap != null) {
            this.renderingBitmap.recycle();
            this.renderingBitmap = null;
        }
        if (this.backgroundBitmap != null) {
            this.backgroundBitmap.recycle();
            this.backgroundBitmap = null;
        }
    }

    public RLottieDrawable(File file, int w, int h, boolean precache, boolean limitFps) {
        this(file, w, h, precache, limitFps, (int[]) null);
    }

    public RLottieDrawable(File file, int w, int h, boolean precache, boolean limitFps, int[] colorReplacement) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                boolean unused2 = RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.lottieCacheGenerateQueue.execute(RLottieDrawable.this.cacheGenerateTask = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    });
                }
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            Bitmap unused = RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        if (RLottieDrawable.this.needGenerateCache) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            boolean unused2 = RLottieDrawable.this.needGenerateCache = false;
                            return;
                        }
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        int unused3 = RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            boolean unused4 = RLottieDrawable.this.needGenerateCache = true;
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        Bitmap unused5 = rLottieDrawable.nextRenderingBitmap = rLottieDrawable.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.metaData[0]) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                boolean unused6 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$2808(RLottieDrawable.this);
                            } else {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                int unused7 = rLottieDrawable2.currentFrame = rLottieDrawable2.currentFrame + framesPerUpdates;
                                boolean unused8 = RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            int unused9 = RLottieDrawable.this.currentFrame = 0;
                            boolean unused10 = RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            int unused11 = RLottieDrawable.this.currentFrame = 0;
                            boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.access$2808(RLottieDrawable.this);
                        } else {
                            boolean unused13 = RLottieDrawable.this.nextFrameIsLast = true;
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
        this.width = w;
        this.height = h;
        this.shouldLimitFps = limitFps;
        getPaint().setFlags(2);
        this.nativePtr = create(file.getAbsolutePath(), this.metaData, precache, colorReplacement);
        if (precache && lottieCacheGenerateQueue == null) {
            lottieCacheGenerateQueue = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        }
        if (this.nativePtr == 0) {
            file.delete();
        }
        if (this.shouldLimitFps && this.metaData[1] < 60) {
            this.shouldLimitFps = false;
        }
        this.timeBetweenFrames = Math.max(this.shouldLimitFps ? 33 : 16, (int) (1000.0f / ((float) this.metaData[1])));
    }

    public RLottieDrawable(int rawRes, String name, int w, int h) {
        this(rawRes, name, w, h, true);
    }

    public RLottieDrawable(int rawRes, String name, int w, int h, boolean startDecode) {
        this.metaData = new int[3];
        this.newColorUpdates = new HashMap<>();
        this.pendingColorUpdates = new HashMap<>();
        this.autoRepeat = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.dstRect = new Rect();
        this.parentViews = new ArrayList<>();
        this.uiRunnableNoFrame = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableCacheFinished = new Runnable() {
            public void run() {
                Runnable unused = RLottieDrawable.this.cacheGenerateTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnable = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableLastFrame = new Runnable() {
            public void run() {
                boolean unused = RLottieDrawable.this.singleFrameDecoded = true;
                boolean unused2 = RLottieDrawable.this.isRunning = false;
                RLottieDrawable.this.invalidateInternal();
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }
        };
        this.uiRunnableGenerateCache = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled && !RLottieDrawable.this.destroyWhenDone && RLottieDrawable.this.nativePtr != 0) {
                    RLottieDrawable.lottieCacheGenerateQueue.execute(RLottieDrawable.this.cacheGenerateTask = new Runnable() {
                        public final void run() {
                            RLottieDrawable.AnonymousClass5.this.lambda$run$0$RLottieDrawable$5();
                        }
                    });
                }
                Runnable unused = RLottieDrawable.this.loadFrameTask = null;
                RLottieDrawable.this.decodeFrameFinishedInternal();
            }

            public /* synthetic */ void lambda$run$0$RLottieDrawable$5() {
                if (RLottieDrawable.this.cacheGenerateTask != null) {
                    RLottieDrawable.createCache(RLottieDrawable.this.nativePtr, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableCacheFinished);
                }
            }
        };
        this.loadFrameRunnable = new Runnable() {
            public void run() {
                if (!RLottieDrawable.this.isRecycled) {
                    if (RLottieDrawable.this.nativePtr == 0) {
                        RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableNoFrame);
                        return;
                    }
                    if (RLottieDrawable.this.backgroundBitmap == null) {
                        try {
                            Bitmap unused = RLottieDrawable.this.backgroundBitmap = Bitmap.createBitmap(RLottieDrawable.this.width, RLottieDrawable.this.height, Bitmap.Config.ARGB_8888);
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (RLottieDrawable.this.backgroundBitmap != null) {
                        if (RLottieDrawable.this.needGenerateCache) {
                            RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnableGenerateCache);
                            boolean unused2 = RLottieDrawable.this.needGenerateCache = false;
                            return;
                        }
                        try {
                            if (!RLottieDrawable.this.pendingColorUpdates.isEmpty()) {
                                for (Map.Entry<String, Integer> entry : RLottieDrawable.this.pendingColorUpdates.entrySet()) {
                                    RLottieDrawable.setLayerColor(RLottieDrawable.this.nativePtr, entry.getKey(), entry.getValue().intValue());
                                }
                                RLottieDrawable.this.pendingColorUpdates.clear();
                            }
                        } catch (Exception e2) {
                        }
                        int unused3 = RLottieDrawable.getFrame(RLottieDrawable.this.nativePtr, RLottieDrawable.this.currentFrame, RLottieDrawable.this.backgroundBitmap, RLottieDrawable.this.width, RLottieDrawable.this.height, RLottieDrawable.this.backgroundBitmap.getRowBytes());
                        if (RLottieDrawable.this.metaData[2] != 0) {
                            boolean unused4 = RLottieDrawable.this.needGenerateCache = true;
                            RLottieDrawable.this.metaData[2] = 0;
                        }
                        RLottieDrawable rLottieDrawable = RLottieDrawable.this;
                        Bitmap unused5 = rLottieDrawable.nextRenderingBitmap = rLottieDrawable.backgroundBitmap;
                        int framesPerUpdates = RLottieDrawable.this.shouldLimitFps ? 2 : 1;
                        if (RLottieDrawable.this.currentFrame + framesPerUpdates < RLottieDrawable.this.metaData[0]) {
                            if (RLottieDrawable.this.autoRepeat == 3) {
                                boolean unused6 = RLottieDrawable.this.nextFrameIsLast = true;
                                RLottieDrawable.access$2808(RLottieDrawable.this);
                            } else {
                                RLottieDrawable rLottieDrawable2 = RLottieDrawable.this;
                                int unused7 = rLottieDrawable2.currentFrame = rLottieDrawable2.currentFrame + framesPerUpdates;
                                boolean unused8 = RLottieDrawable.this.nextFrameIsLast = false;
                            }
                        } else if (RLottieDrawable.this.autoRepeat == 1) {
                            int unused9 = RLottieDrawable.this.currentFrame = 0;
                            boolean unused10 = RLottieDrawable.this.nextFrameIsLast = false;
                        } else if (RLottieDrawable.this.autoRepeat == 2) {
                            int unused11 = RLottieDrawable.this.currentFrame = 0;
                            boolean unused12 = RLottieDrawable.this.nextFrameIsLast = true;
                            RLottieDrawable.access$2808(RLottieDrawable.this);
                        } else {
                            boolean unused13 = RLottieDrawable.this.nextFrameIsLast = true;
                        }
                    }
                    RLottieDrawable.uiHandler.post(RLottieDrawable.this.uiRunnable);
                }
            }
        };
        try {
            InputStream inputStream = ApplicationLoader.applicationContext.getResources().openRawResource(rawRes);
            int totalRead = 0;
            while (true) {
                int read = inputStream.read(buffer, 0, buffer.length);
                int readLen = read;
                if (read <= 0) {
                    break;
                }
                if (readBuffer.length < totalRead + readLen) {
                    byte[] newBuffer = new byte[(readBuffer.length * 2)];
                    System.arraycopy(readBuffer, 0, newBuffer, 0, totalRead);
                    readBuffer = newBuffer;
                }
                System.arraycopy(buffer, 0, readBuffer, totalRead, readLen);
                totalRead += readLen;
            }
            String jsonString = new String(readBuffer, 0, totalRead);
            inputStream.close();
            this.width = w;
            this.height = h;
            getPaint().setFlags(2);
            this.nativePtr = createWithJson(jsonString, name, this.metaData);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            this.autoRepeat = 0;
            if (startDecode) {
                setAllowDecodeSingleFrame(true);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void addParentView(View view) {
        if (view != null) {
            int a = 0;
            int N = this.parentViews.size();
            while (a < N) {
                if (this.parentViews.get(a).get() != view) {
                    if (this.parentViews.get(a).get() == null) {
                        this.parentViews.remove(a);
                        N--;
                        a--;
                    }
                    a++;
                } else {
                    return;
                }
            }
            this.parentViews.add(0, new WeakReference(view));
        }
    }

    public void removeParentView(View view) {
        if (view != null) {
            int a = 0;
            int N = this.parentViews.size();
            while (a < N) {
                View v = (View) this.parentViews.get(a).get();
                if (v == view || v == null) {
                    this.parentViews.remove(a);
                    N--;
                    a--;
                }
                a++;
            }
        }
    }

    private boolean hasParentView() {
        if (getCallback() != null) {
            return true;
        }
        int N = this.parentViews.size();
        for (int a = 0; a < N; a = (a - 1) + 1) {
            if (((View) this.parentViews.get(a).get()) != null) {
                return true;
            }
            this.parentViews.remove(a);
            N--;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void invalidateInternal() {
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            View view = (View) this.parentViews.get(a).get();
            if (view != null) {
                view.invalidate();
            } else {
                this.parentViews.remove(a);
                N--;
                a--;
            }
            a++;
        }
        if (getCallback() != null) {
            invalidateSelf();
        }
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.decodeSingleFrame = value;
        if (value) {
            scheduleNextGetFrame();
        }
    }

    public void recycle() {
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            if (this.nativePtr != 0) {
                destroy(this.nativePtr);
                this.nativePtr = 0;
            }
            recycleResources();
            return;
        }
        this.destroyWhenDone = true;
    }

    public void setAutoRepeat(int value) {
        if (this.autoRepeat != 2 || value != 3 || this.currentFrame == 0) {
            this.autoRepeat = value;
        }
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
        if (this.isRunning) {
            return;
        }
        if (this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) {
            this.isRunning = true;
            scheduleNextGetFrame();
            invalidateInternal();
        }
    }

    public boolean restart() {
        if (this.autoRepeat < 2 || this.autoRepeatPlayCount == 0) {
            return false;
        }
        this.autoRepeatPlayCount = 0;
        this.autoRepeat = 2;
        start();
        return true;
    }

    public void setVibrationPattern(HashMap<Integer, Integer> pattern) {
        this.vibrationPattern = pattern;
    }

    public void beginApplyLayerColors() {
        this.applyingLayerColors = true;
    }

    public void commitApplyLayerColors() {
        if (this.applyingLayerColors) {
            this.applyingLayerColors = false;
            if (!this.isRunning && this.decodeSingleFrame) {
                if (this.currentFrame <= 2) {
                    this.currentFrame = 0;
                }
                this.nextFrameIsLast = false;
                this.singleFrameDecoded = false;
                if (!scheduleNextGetFrame()) {
                    this.forceFrameRedraw = true;
                }
            }
            invalidateInternal();
        }
    }

    public void setLayerColor(String layerName, int color) {
        this.newColorUpdates.put(layerName, Integer.valueOf(color));
        if (!this.applyingLayerColors && !this.isRunning && this.decodeSingleFrame) {
            if (this.currentFrame <= 2) {
                this.currentFrame = 0;
            }
            this.nextFrameIsLast = false;
            this.singleFrameDecoded = false;
            if (!scheduleNextGetFrame()) {
                this.forceFrameRedraw = true;
            }
        }
        invalidateInternal();
    }

    private boolean scheduleNextGetFrame() {
        if (this.cacheGenerateTask != null || this.loadFrameTask != null || this.nextRenderingBitmap != null || this.nativePtr == 0 || this.destroyWhenDone) {
            return false;
        }
        if (!this.isRunning) {
            boolean z = this.decodeSingleFrame;
            if (!z) {
                return false;
            }
            if (z && this.singleFrameDecoded) {
                return false;
            }
        }
        if (!this.newColorUpdates.isEmpty()) {
            this.pendingColorUpdates.putAll(this.newColorUpdates);
            this.newColorUpdates.clear();
        }
        ExecutorService executorService = loadFrameRunnableQueue;
        Runnable runnable = this.loadFrameRunnable;
        this.loadFrameTask = runnable;
        executorService.execute(runnable);
        return true;
    }

    public void stop() {
        this.isRunning = false;
    }

    public void setProgress(float progress) {
        if (progress < 0.0f) {
            progress = 0.0f;
        } else if (progress > 1.0f) {
            progress = 1.0f;
        }
        this.currentFrame = (int) (((float) this.metaData[0]) * progress);
        this.nextFrameIsLast = false;
        invalidateSelf();
    }

    public void setCurrentFrame(int frame) {
        this.currentFrame = frame;
        this.nextFrameIsLast = false;
        invalidateSelf();
    }

    public void setCurrentParentView(View view) {
        this.currentParentView = view;
    }

    private boolean isCurrentParentViewMaster() {
        if (getCallback() != null) {
            return true;
        }
        int a = 0;
        int N = this.parentViews.size();
        while (a < N) {
            if (this.parentViews.get(a).get() == null) {
                this.parentViews.remove(a);
                N--;
                a = (a - 1) + 1;
            } else if (this.parentViews.get(a).get() == this.currentParentView) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public int getIntrinsicHeight() {
        return this.height;
    }

    public int getIntrinsicWidth() {
        return this.width;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.applyTransformation = true;
    }

    public void draw(Canvas canvas) {
        Integer force;
        if (this.nativePtr != 0 && !this.destroyWhenDone) {
            long now = SystemClock.uptimeMillis();
            long timeDiff = Math.abs(now - this.lastFrameTime);
            if (this.isRunning) {
                if (this.renderingBitmap == null && this.nextRenderingBitmap == null) {
                    scheduleNextGetFrame();
                } else if (this.nextRenderingBitmap != null && ((this.renderingBitmap == null || timeDiff >= ((long) (this.timeBetweenFrames - 6))) && isCurrentParentViewMaster())) {
                    HashMap<Integer, Integer> hashMap = this.vibrationPattern;
                    if (!(hashMap == null || this.currentParentView == null || (force = hashMap.get(Integer.valueOf(this.currentFrame - 1))) == null)) {
                        this.currentParentView.performHapticFeedback(force.intValue() == 1 ? 0 : 3, 2);
                    }
                    this.backgroundBitmap = this.renderingBitmap;
                    this.renderingBitmap = this.nextRenderingBitmap;
                    if (this.nextFrameIsLast) {
                        stop();
                    }
                    this.loadFrameTask = null;
                    this.singleFrameDecoded = true;
                    this.nextRenderingBitmap = null;
                    this.lastFrameTime = now;
                    scheduleNextGetFrame();
                }
            } else if (this.forceFrameRedraw || (this.decodeSingleFrame && timeDiff >= ((long) (this.timeBetweenFrames - 6)) && this.nextRenderingBitmap != null)) {
                this.backgroundBitmap = this.renderingBitmap;
                this.renderingBitmap = this.nextRenderingBitmap;
                this.loadFrameTask = null;
                this.singleFrameDecoded = true;
                this.nextRenderingBitmap = null;
                this.lastFrameTime = now;
                if (this.forceFrameRedraw) {
                    this.singleFrameDecoded = false;
                    this.forceFrameRedraw = false;
                }
                scheduleNextGetFrame();
            }
            if (this.renderingBitmap != null) {
                if (this.applyTransformation) {
                    this.dstRect.set(getBounds());
                    this.scaleX = ((float) this.dstRect.width()) / ((float) this.width);
                    this.scaleY = ((float) this.dstRect.height()) / ((float) this.height);
                    this.applyTransformation = false;
                }
                canvas.translate((float) this.dstRect.left, (float) this.dstRect.top);
                canvas.scale(this.scaleX, this.scaleY);
                canvas.drawBitmap(this.renderingBitmap, 0.0f, 0.0f, getPaint());
                if (this.isRunning) {
                    invalidateInternal();
                }
            }
        }
    }

    public int getMinimumHeight() {
        return this.height;
    }

    public int getMinimumWidth() {
        return this.width;
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
        if (this.renderingBitmap != null) {
            return this.renderingBitmap;
        }
        if (this.nextRenderingBitmap != null) {
            return this.nextRenderingBitmap;
        }
        return null;
    }

    public boolean hasBitmap() {
        return (this.nativePtr == 0 || (this.renderingBitmap == null && this.nextRenderingBitmap == null)) ? false : true;
    }
}
