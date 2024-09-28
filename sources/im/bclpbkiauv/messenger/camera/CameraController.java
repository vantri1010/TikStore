package im.bclpbkiauv.messenger.camera;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Base64;
import com.serenegiant.usb.UVCCamera;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.Bitmaps;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.camera.CameraController;
import im.bclpbkiauv.tgnet.SerializedData;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CameraController implements MediaRecorder.OnInfoListener {
    private static final int CORE_POOL_SIZE = 1;
    private static volatile CameraController Instance = null;
    private static final int KEEP_ALIVE_SECONDS = 60;
    private static final int MAX_POOL_SIZE = 1;
    protected ArrayList<String> availableFlashModes = new ArrayList<>();
    protected volatile ArrayList<CameraInfo> cameraInfos;
    private boolean cameraInitied;
    private boolean loadingCameras;
    private ArrayList<Runnable> onFinishCameraInitRunnables = new ArrayList<>();
    private VideoTakeCallback onVideoTakeCallback;
    private String recordedFile;
    private MediaRecorder recorder;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());

    public interface VideoTakeCallback {
        void onFinishVideoRecording(String str, long j);
    }

    public static CameraController getInstance() {
        CameraController localInstance = Instance;
        if (localInstance == null) {
            synchronized (CameraController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    CameraController cameraController = new CameraController();
                    localInstance = cameraController;
                    Instance = cameraController;
                }
            }
        }
        return localInstance;
    }

    public void cancelOnInitRunnable(Runnable onInitRunnable) {
        this.onFinishCameraInitRunnables.remove(onInitRunnable);
    }

    public void initCamera(Runnable onInitRunnable) {
        if (onInitRunnable != null && !this.onFinishCameraInitRunnables.contains(onInitRunnable)) {
            this.onFinishCameraInitRunnables.add(onInitRunnable);
        }
        if (!this.loadingCameras && !this.cameraInitied) {
            this.loadingCameras = true;
            this.threadPool.execute(new Runnable() {
                public final void run() {
                    CameraController.this.lambda$initCamera$3$CameraController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$initCamera$3$CameraController() {
        String cache;
        Camera.CameraInfo info;
        List<Camera.Size> list;
        String str;
        CameraController cameraController = this;
        String str2 = "cameraCache";
        try {
            if (cameraController.cameraInfos == null) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                String cache2 = preferences.getString(str2, (String) null);
                Comparator<Size> comparator = $$Lambda$CameraController$Fc6o8R93pDikkjqXtCWr4V42Y9E.INSTANCE;
                ArrayList<CameraInfo> result = new ArrayList<>();
                if (cache2 != null) {
                    SerializedData serializedData = new SerializedData(Base64.decode(cache2, 0));
                    int count = serializedData.readInt32(false);
                    for (int a = 0; a < count; a++) {
                        CameraInfo cameraInfo = new CameraInfo(serializedData.readInt32(false), serializedData.readInt32(false));
                        int pCount = serializedData.readInt32(false);
                        for (int b = 0; b < pCount; b++) {
                            cameraInfo.previewSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        int pCount2 = serializedData.readInt32(false);
                        for (int b2 = 0; b2 < pCount2; b2++) {
                            cameraInfo.pictureSizes.add(new Size(serializedData.readInt32(false), serializedData.readInt32(false)));
                        }
                        result.add(cameraInfo);
                        Collections.sort(cameraInfo.previewSizes, comparator);
                        Collections.sort(cameraInfo.pictureSizes, comparator);
                    }
                    serializedData.cleanup();
                    String str3 = cache2;
                } else {
                    int count2 = Camera.getNumberOfCameras();
                    Camera.CameraInfo info2 = new Camera.CameraInfo();
                    int bufferSize = 4;
                    int cameraId = 0;
                    while (cameraId < count2) {
                        Camera.getCameraInfo(cameraId, info2);
                        CameraInfo cameraInfo2 = new CameraInfo(cameraId, info2.facing);
                        if (ApplicationLoader.mainInterfacePaused) {
                            if (ApplicationLoader.externalInterfacePaused) {
                                throw new RuntimeException("app paused");
                            }
                        }
                        try {
                            Camera camera = Camera.open(cameraInfo2.getCameraId());
                            Camera.Parameters params = camera.getParameters();
                            List<Camera.Size> list2 = params.getSupportedPreviewSizes();
                            int a2 = 0;
                            while (true) {
                                cache = cache2;
                                info = info2;
                                if (a2 >= list2.size()) {
                                    break;
                                }
                                Camera.Size size = list2.get(a2);
                                List<Camera.Size> list3 = list2;
                                if (size.width == 1280) {
                                    if (size.height != 720) {
                                        str = str2;
                                        a2++;
                                        cameraController = this;
                                        cache2 = cache;
                                        info2 = info;
                                        list2 = list3;
                                        str2 = str;
                                    }
                                }
                                if (size.height >= 2160 || size.width >= 2160) {
                                    str = str2;
                                    a2++;
                                    cameraController = this;
                                    cache2 = cache;
                                    info2 = info;
                                    list2 = list3;
                                    str2 = str;
                                } else {
                                    str = str2;
                                    cameraInfo2.previewSizes.add(new Size(size.width, size.height));
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("preview size = " + size.width + " " + size.height);
                                    }
                                    a2++;
                                    cameraController = this;
                                    cache2 = cache;
                                    info2 = info;
                                    list2 = list3;
                                    str2 = str;
                                }
                            }
                            String str4 = str2;
                            List<Camera.Size> list4 = list2;
                            List<Camera.Size> list5 = params.getSupportedPictureSizes();
                            int a3 = 0;
                            while (a3 < list5.size()) {
                                Camera.Size size2 = list5.get(a3);
                                if (size2.width == 1280) {
                                    if (size2.height != 720) {
                                        list = list5;
                                        a3++;
                                        list5 = list;
                                    }
                                }
                                if ("samsung".equals(Build.MANUFACTURER) && "jflteuc".equals(Build.PRODUCT)) {
                                    if (size2.width >= 2048) {
                                        list = list5;
                                        a3++;
                                        list5 = list;
                                    }
                                }
                                list = list5;
                                cameraInfo2.pictureSizes.add(new Size(size2.width, size2.height));
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("picture size = " + size2.width + " " + size2.height);
                                }
                                a3++;
                                list5 = list;
                            }
                            camera.release();
                            result.add(cameraInfo2);
                            Collections.sort(cameraInfo2.previewSizes, comparator);
                            Collections.sort(cameraInfo2.pictureSizes, comparator);
                            bufferSize += ((cameraInfo2.previewSizes.size() + cameraInfo2.pictureSizes.size()) * 8) + 8;
                            cameraId++;
                            cameraController = this;
                            cache2 = cache;
                            info2 = info;
                            str2 = str4;
                        } catch (Exception e) {
                            cameraController = this;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    CameraController.this.lambda$null$2$CameraController();
                                }
                            });
                        }
                    }
                    String str5 = str2;
                    String str6 = cache2;
                    Camera.CameraInfo cameraInfo3 = info2;
                    SerializedData serializedData2 = new SerializedData(bufferSize);
                    serializedData2.writeInt32(result.size());
                    for (int a4 = 0; a4 < count2; a4++) {
                        CameraInfo cameraInfo4 = result.get(a4);
                        serializedData2.writeInt32(cameraInfo4.cameraId);
                        serializedData2.writeInt32(cameraInfo4.frontCamera);
                        int pCount3 = cameraInfo4.previewSizes.size();
                        serializedData2.writeInt32(pCount3);
                        for (int b3 = 0; b3 < pCount3; b3++) {
                            Size size3 = cameraInfo4.previewSizes.get(b3);
                            serializedData2.writeInt32(size3.mWidth);
                            serializedData2.writeInt32(size3.mHeight);
                        }
                        int pCount4 = cameraInfo4.pictureSizes.size();
                        serializedData2.writeInt32(pCount4);
                        for (int b4 = 0; b4 < pCount4; b4++) {
                            Size size4 = cameraInfo4.pictureSizes.get(b4);
                            serializedData2.writeInt32(size4.mWidth);
                            serializedData2.writeInt32(size4.mHeight);
                        }
                    }
                    preferences.edit().putString(str5, Base64.encodeToString(serializedData2.toByteArray(), 0)).commit();
                    serializedData2.cleanup();
                }
                cameraController = this;
                cameraController.cameraInfos = result;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    CameraController.this.lambda$null$1$CameraController();
                }
            });
        } catch (Exception e2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    CameraController.this.lambda$null$2$CameraController();
                }
            });
        }
    }

    static /* synthetic */ int lambda$null$0(Size o1, Size o2) {
        if (o1.mWidth < o2.mWidth) {
            return 1;
        }
        if (o1.mWidth > o2.mWidth) {
            return -1;
        }
        if (o1.mHeight < o2.mHeight) {
            return 1;
        }
        if (o1.mHeight > o2.mHeight) {
            return -1;
        }
        return 0;
    }

    public /* synthetic */ void lambda$null$1$CameraController() {
        this.loadingCameras = false;
        this.cameraInitied = true;
        if (!this.onFinishCameraInitRunnables.isEmpty()) {
            for (int a = 0; a < this.onFinishCameraInitRunnables.size(); a++) {
                this.onFinishCameraInitRunnables.get(a).run();
            }
            this.onFinishCameraInitRunnables.clear();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
    }

    public /* synthetic */ void lambda$null$2$CameraController() {
        this.onFinishCameraInitRunnables.clear();
        this.loadingCameras = false;
        this.cameraInitied = false;
    }

    public boolean isCameraInitied() {
        return this.cameraInitied && this.cameraInfos != null && !this.cameraInfos.isEmpty();
    }

    public void runOnThreadPool(Runnable runnable) {
        this.threadPool.execute(runnable);
    }

    public void close(CameraSession session, CountDownLatch countDownLatch, Runnable beforeDestroyRunnable) {
        session.destroy();
        this.threadPool.execute(new Runnable(beforeDestroyRunnable, session, countDownLatch) {
            private final /* synthetic */ Runnable f$0;
            private final /* synthetic */ CameraSession f$1;
            private final /* synthetic */ CountDownLatch f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraController.lambda$close$4(this.f$0, this.f$1, this.f$2);
            }
        });
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ void lambda$close$4(Runnable beforeDestroyRunnable, CameraSession session, CountDownLatch countDownLatch) {
        if (beforeDestroyRunnable != null) {
            beforeDestroyRunnable.run();
        }
        if (session.cameraInfo.camera != null) {
            try {
                session.cameraInfo.camera.stopPreview();
                session.cameraInfo.camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) null);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                session.cameraInfo.camera.release();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            session.cameraInfo.camera = null;
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    public ArrayList<CameraInfo> getCameras() {
        return this.cameraInfos;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0067, code lost:
        if (r2 <= 8) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0069, code lost:
        r3 = pack(r10, r1, 4, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0070, code lost:
        if (r3 == 1229531648) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0075, code lost:
        if (r3 == 1296891946) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0077, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0078, code lost:
        if (r3 != 1229531648) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x007b, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007c, code lost:
        r4 = r6;
        r5 = pack(r10, r1 + 4, 4, r4) + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0086, code lost:
        if (r5 < 10) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0088, code lost:
        if (r5 <= r2) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008b, code lost:
        r1 = r1 + r5;
        r2 = r2 - r5;
        r5 = pack(r10, r1 - 2, 2, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0093, code lost:
        r6 = r5 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0095, code lost:
        if (r5 <= 0) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0099, code lost:
        if (r2 < 12) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a1, code lost:
        if (pack(r10, r1, 2, r4) != 274) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00a3, code lost:
        r5 = pack(r10, r1 + 8, 2, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00aa, code lost:
        if (r5 == 3) goto L_0x00b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00ad, code lost:
        if (r5 == 6) goto L_0x00b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00af, code lost:
        if (r5 == 8) goto L_0x00b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00b1, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00b2, code lost:
        return 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00b5, code lost:
        return 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00b8, code lost:
        return 180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00bb, code lost:
        r1 = r1 + 12;
        r2 = r2 - 12;
        r5 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00c1, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00c2, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getOrientation(byte[] r10) {
        /*
            r0 = 0
            if (r10 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
            r2 = 0
        L_0x0006:
            int r3 = r1 + 3
            int r4 = r10.length
            r5 = 4
            r6 = 1
            r7 = 8
            r8 = 2
            if (r3 >= r4) goto L_0x0067
            int r3 = r1 + 1
            byte r1 = r10[r1]
            r4 = 255(0xff, float:3.57E-43)
            r1 = r1 & r4
            if (r1 != r4) goto L_0x0066
            byte r1 = r10[r3]
            r1 = r1 & r4
            if (r1 != r4) goto L_0x0020
            r1 = r3
            goto L_0x0006
        L_0x0020:
            int r3 = r3 + 1
            r4 = 216(0xd8, float:3.03E-43)
            if (r1 == r4) goto L_0x0064
            if (r1 != r6) goto L_0x0029
            goto L_0x0064
        L_0x0029:
            r4 = 217(0xd9, float:3.04E-43)
            if (r1 == r4) goto L_0x0062
            r4 = 218(0xda, float:3.05E-43)
            if (r1 != r4) goto L_0x0032
            goto L_0x0062
        L_0x0032:
            int r2 = pack(r10, r3, r8, r0)
            if (r2 < r8) goto L_0x0061
            int r4 = r3 + r2
            int r9 = r10.length
            if (r4 <= r9) goto L_0x003e
            goto L_0x0061
        L_0x003e:
            r4 = 225(0xe1, float:3.15E-43)
            if (r1 != r4) goto L_0x005d
            if (r2 < r7) goto L_0x005d
            int r4 = r3 + 2
            int r4 = pack(r10, r4, r5, r0)
            r9 = 1165519206(0x45786966, float:3974.5874)
            if (r4 != r9) goto L_0x005d
            int r4 = r3 + 6
            int r4 = pack(r10, r4, r8, r0)
            if (r4 != 0) goto L_0x005d
            int r3 = r3 + 8
            int r2 = r2 + -8
            r1 = r3
            goto L_0x0067
        L_0x005d:
            int r3 = r3 + r2
            r2 = 0
            r1 = r3
            goto L_0x0006
        L_0x0061:
            return r0
        L_0x0062:
            r1 = r3
            goto L_0x0067
        L_0x0064:
            r1 = r3
            goto L_0x0006
        L_0x0066:
            r1 = r3
        L_0x0067:
            if (r2 <= r7) goto L_0x00c2
            int r3 = pack(r10, r1, r5, r0)
            r4 = 1229531648(0x49492a00, float:823968.0)
            if (r3 == r4) goto L_0x0078
            r9 = 1296891946(0x4d4d002a, float:2.14958752E8)
            if (r3 == r9) goto L_0x0078
            return r0
        L_0x0078:
            if (r3 != r4) goto L_0x007b
            goto L_0x007c
        L_0x007b:
            r6 = 0
        L_0x007c:
            r4 = r6
            int r6 = r1 + 4
            int r5 = pack(r10, r6, r5, r4)
            int r5 = r5 + r8
            r6 = 10
            if (r5 < r6) goto L_0x00c1
            if (r5 <= r2) goto L_0x008b
            goto L_0x00c1
        L_0x008b:
            int r1 = r1 + r5
            int r2 = r2 - r5
            int r6 = r1 + -2
            int r5 = pack(r10, r6, r8, r4)
        L_0x0093:
            int r6 = r5 + -1
            if (r5 <= 0) goto L_0x00c2
            r5 = 12
            if (r2 < r5) goto L_0x00c2
            int r3 = pack(r10, r1, r8, r4)
            r5 = 274(0x112, float:3.84E-43)
            if (r3 != r5) goto L_0x00bb
            int r5 = r1 + 8
            int r5 = pack(r10, r5, r8, r4)
            r8 = 3
            if (r5 == r8) goto L_0x00b8
            r8 = 6
            if (r5 == r8) goto L_0x00b5
            if (r5 == r7) goto L_0x00b2
            return r0
        L_0x00b2:
            r0 = 270(0x10e, float:3.78E-43)
            return r0
        L_0x00b5:
            r0 = 90
            return r0
        L_0x00b8:
            r0 = 180(0xb4, float:2.52E-43)
            return r0
        L_0x00bb:
            int r1 = r1 + 12
            int r2 = r2 + -12
            r5 = r6
            goto L_0x0093
        L_0x00c1:
            return r0
        L_0x00c2:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.camera.CameraController.getOrientation(byte[]):int");
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (true) {
            int length2 = length - 1;
            if (length <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & 255);
            offset += step;
            length = length2;
        }
    }

    public boolean takePicture(File path, CameraSession session, Runnable callback) {
        if (session == null) {
            return false;
        }
        CameraInfo info = session.cameraInfo;
        boolean flipFront = session.isFlipFront();
        try {
            info.camera.takePicture((Camera.ShutterCallback) null, (Camera.PictureCallback) null, new Camera.PictureCallback(path, info, flipFront, callback) {
                private final /* synthetic */ File f$0;
                private final /* synthetic */ CameraInfo f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onPictureTaken(byte[] bArr, Camera camera) {
                    CameraController.lambda$takePicture$5(this.f$0, this.f$1, this.f$2, this.f$3, bArr, camera);
                }
            });
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    static /* synthetic */ void lambda$takePicture$5(File path, CameraInfo info, boolean flipFront, Runnable callback, byte[] data, Camera camera1) {
        File file = path;
        byte[] bArr = data;
        Bitmap bitmap = null;
        int size = (int) (((float) AndroidUtilities.getPhotoSize()) / AndroidUtilities.density);
        String key = String.format(Locale.US, "%s@%d_%d", new Object[]{Utilities.MD5(path.getAbsolutePath()), Integer.valueOf(size), Integer.valueOf(size)});
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            float scaleFactor = Math.max(((float) options.outWidth) / ((float) AndroidUtilities.getPhotoSize()), ((float) options.outHeight) / ((float) AndroidUtilities.getPhotoSize()));
            if (scaleFactor < 1.0f) {
                scaleFactor = 1.0f;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) scaleFactor;
            options.inPurgeable = true;
            bitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            if (info.frontCamera != 0 && flipFront) {
                try {
                    Matrix matrix = new Matrix();
                    matrix.setRotate((float) getOrientation(data));
                    matrix.postScale(-1.0f, 1.0f);
                    Bitmap scaled = Bitmaps.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (scaled != bitmap) {
                        bitmap.recycle();
                    }
                    FileOutputStream outputStream = new FileOutputStream(path);
                    scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.getFD().sync();
                    outputStream.close();
                    if (scaled != null) {
                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(scaled), key);
                    }
                    if (callback != null) {
                        callback.run();
                        return;
                    }
                    return;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            FileOutputStream outputStream2 = new FileOutputStream(path);
            outputStream2.write(bArr);
            outputStream2.flush();
            outputStream2.getFD().sync();
            outputStream2.close();
            if (bitmap != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), key);
            }
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        if (callback != null) {
            callback.run();
        }
    }

    public void startPreview(CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new Runnable() {
                public final void run() {
                    CameraController.lambda$startPreview$6(CameraSession.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$startPreview$6(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.startPreview();
    }

    public void stopPreview(CameraSession session) {
        if (session != null) {
            this.threadPool.execute(new Runnable() {
                public final void run() {
                    CameraController.lambda$stopPreview$7(CameraSession.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$stopPreview$7(CameraSession session) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        camera.stopPreview();
    }

    public void openRound(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable configureCallback) {
        if (session != null && texture != null) {
            this.threadPool.execute(new Runnable(configureCallback, texture, callback) {
                private final /* synthetic */ Runnable f$1;
                private final /* synthetic */ SurfaceTexture f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    CameraController.lambda$openRound$8(CameraSession.this, this.f$1, this.f$2, this.f$3);
                }
            });
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.d("failed to open round " + session + " tex = " + texture);
        }
    }

    static /* synthetic */ void lambda$openRound$8(CameraSession session, Runnable configureCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start creating round camera session");
            }
            if (camera == null) {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            }
            Camera.Parameters parameters = camera.getParameters();
            session.configureRoundCamera();
            if (configureCallback != null) {
                configureCallback.run();
            }
            camera.setPreviewTexture(texture);
            camera.startPreview();
            if (callback != null) {
                AndroidUtilities.runOnUIThread(callback);
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("round camera session created");
            }
        } catch (Exception e) {
            session.cameraInfo.camera = null;
            if (camera != null) {
                camera.release();
            }
            FileLog.e((Throwable) e);
        }
    }

    public void open(CameraSession session, SurfaceTexture texture, Runnable callback, Runnable prestartCallback) {
        if (session != null && texture != null) {
            this.threadPool.execute(new Runnable(session, prestartCallback, texture, callback) {
                private final /* synthetic */ CameraSession f$1;
                private final /* synthetic */ Runnable f$2;
                private final /* synthetic */ SurfaceTexture f$3;
                private final /* synthetic */ Runnable f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    CameraController.this.lambda$open$9$CameraController(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }
    }

    public /* synthetic */ void lambda$open$9$CameraController(CameraSession session, Runnable prestartCallback, SurfaceTexture texture, Runnable callback) {
        Camera camera = session.cameraInfo.camera;
        if (camera == null) {
            try {
                CameraInfo cameraInfo = session.cameraInfo;
                Camera open = Camera.open(session.cameraInfo.cameraId);
                cameraInfo.camera = open;
                camera = open;
            } catch (Exception e) {
                session.cameraInfo.camera = null;
                if (camera != null) {
                    camera.release();
                }
                FileLog.e((Throwable) e);
                return;
            }
        }
        List<String> rawFlashModes = camera.getParameters().getSupportedFlashModes();
        this.availableFlashModes.clear();
        if (rawFlashModes != null) {
            for (int a = 0; a < rawFlashModes.size(); a++) {
                String rawFlashMode = rawFlashModes.get(a);
                if (rawFlashMode.equals("off") || rawFlashMode.equals("on") || rawFlashMode.equals("auto")) {
                    this.availableFlashModes.add(rawFlashMode);
                }
            }
            session.checkFlashMode(this.availableFlashModes.get(0));
        }
        if (prestartCallback != null) {
            prestartCallback.run();
        }
        session.configurePhotoCamera();
        camera.setPreviewTexture(texture);
        camera.startPreview();
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    public void recordVideo(CameraSession session, File path, VideoTakeCallback callback, Runnable onVideoStartRecord) {
        CameraSession cameraSession = session;
        if (cameraSession != null) {
            CameraInfo info = cameraSession.cameraInfo;
            this.threadPool.execute(new Runnable(info.camera, session, path, info, callback, onVideoStartRecord) {
                private final /* synthetic */ Camera f$1;
                private final /* synthetic */ CameraSession f$2;
                private final /* synthetic */ File f$3;
                private final /* synthetic */ CameraInfo f$4;
                private final /* synthetic */ CameraController.VideoTakeCallback f$5;
                private final /* synthetic */ Runnable f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    CameraController.this.lambda$recordVideo$10$CameraController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    public /* synthetic */ void lambda$recordVideo$10$CameraController(Camera camera, CameraSession session, File path, CameraInfo info, VideoTakeCallback callback, Runnable onVideoStartRecord) {
        if (camera != null) {
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(session.getCurrentFlashMode().equals("on") ? "torch" : "off");
                camera.setParameters(params);
            } catch (Exception e) {
                try {
                    FileLog.e((Throwable) e);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                    return;
                }
            }
            camera.unlock();
            try {
                MediaRecorder mediaRecorder = new MediaRecorder();
                this.recorder = mediaRecorder;
                mediaRecorder.setCamera(camera);
                this.recorder.setVideoSource(1);
                this.recorder.setAudioSource(5);
                session.configureRecorder(1, this.recorder);
                this.recorder.setOutputFile(path.getAbsolutePath());
                this.recorder.setMaxFileSize(1073741824);
                this.recorder.setVideoFrameRate(30);
                this.recorder.setMaxDuration(0);
                Size pictureSize = chooseOptimalSize(info.getPictureSizes(), 720, UVCCamera.DEFAULT_PREVIEW_HEIGHT, new Size(16, 9));
                this.recorder.setVideoEncodingBitRate(1800000);
                this.recorder.setVideoSize(pictureSize.getWidth(), pictureSize.getHeight());
                this.recorder.setOnInfoListener(this);
                this.recorder.prepare();
                this.recorder.start();
                this.onVideoTakeCallback = callback;
                this.recordedFile = path.getAbsolutePath();
                if (onVideoStartRecord != null) {
                    AndroidUtilities.runOnUIThread(onVideoStartRecord);
                }
            } catch (Exception e3) {
                this.recorder.release();
                this.recorder = null;
                FileLog.e((Throwable) e3);
            }
        }
    }

    private void finishRecordingVideo() {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        long duration = 0;
        try {
            MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
            mediaMetadataRetriever2.setDataSource(this.recordedFile);
            String d = mediaMetadataRetriever2.extractMetadata(9);
            if (d != null) {
                duration = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
            }
            try {
                mediaMetadataRetriever2.release();
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e3) {
                    e = e3;
                }
            }
        } catch (Throwable th) {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            throw th;
        }
        long duration2 = duration;
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
        File cacheFile = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(cacheFile));
        } catch (Throwable e5) {
            FileLog.e(e5);
        }
        SharedConfig.saveConfig();
        AndroidUtilities.runOnUIThread(new Runnable(cacheFile, bitmap, duration2) {
            private final /* synthetic */ File f$1;
            private final /* synthetic */ Bitmap f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CameraController.this.lambda$finishRecordingVideo$11$CameraController(this.f$1, this.f$2, this.f$3);
            }
        });
        FileLog.e((Throwable) e);
        long duration22 = duration;
        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1);
        File cacheFile2 = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(cacheFile2));
        SharedConfig.saveConfig();
        AndroidUtilities.runOnUIThread(new Runnable(cacheFile2, bitmap2, duration22) {
            private final /* synthetic */ File f$1;
            private final /* synthetic */ Bitmap f$2;
            private final /* synthetic */ long f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                CameraController.this.lambda$finishRecordingVideo$11$CameraController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$finishRecordingVideo$11$CameraController(File cacheFile, Bitmap bitmap, long durationFinal) {
        if (this.onVideoTakeCallback != null) {
            String path = cacheFile.getAbsolutePath();
            if (bitmap != null) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), Utilities.MD5(path));
            }
            this.onVideoTakeCallback.onFinishVideoRecording(path, durationFinal);
            this.onVideoTakeCallback = null;
        }
    }

    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        if (what == 800 || what == 801 || what == 1) {
            MediaRecorder tempRecorder = this.recorder;
            this.recorder = null;
            if (tempRecorder != null) {
                tempRecorder.stop();
                tempRecorder.release();
            }
            if (this.onVideoTakeCallback != null) {
                finishRecordingVideo();
            }
        }
    }

    public void stopVideoRecording(CameraSession session, boolean abandon) {
        this.threadPool.execute(new Runnable(session, abandon) {
            private final /* synthetic */ CameraSession f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CameraController.this.lambda$stopVideoRecording$13$CameraController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$stopVideoRecording$13$CameraController(CameraSession session, boolean abandon) {
        try {
            Camera camera = session.cameraInfo.camera;
            if (!(camera == null || this.recorder == null)) {
                MediaRecorder tempRecorder = this.recorder;
                this.recorder = null;
                try {
                    tempRecorder.stop();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    tempRecorder.release();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                try {
                    session.stopVideoRecording();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            try {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode("off");
                camera.setParameters(params);
            } catch (Exception e5) {
                FileLog.e((Throwable) e5);
            }
            this.threadPool.execute(new Runnable(camera, session) {
                private final /* synthetic */ Camera f$0;
                private final /* synthetic */ CameraSession f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    CameraController.lambda$null$12(this.f$0, this.f$1);
                }
            });
            if (abandon || this.onVideoTakeCallback == null) {
                this.onVideoTakeCallback = null;
            } else {
                finishRecordingVideo();
            }
        } catch (Exception e6) {
        }
    }

    static /* synthetic */ void lambda$null$12(Camera camera, CameraSession session) {
        try {
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(session.getCurrentFlashMode());
            camera.setParameters(params);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Size chooseOptimalSize(List<Size> choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (int a = 0; a < choices.size(); a++) {
            Size option = choices.get(a);
            if (option.getHeight() == (option.getWidth() * h) / w && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return (Size) Collections.min(bigEnough, new CompareSizesByArea());
        }
        return (Size) Collections.max(choices, new CompareSizesByArea());
    }

    static class CompareSizesByArea implements Comparator<Size> {
        CompareSizesByArea() {
        }

        public int compare(Size lhs, Size rhs) {
            return Long.signum((((long) lhs.getWidth()) * ((long) lhs.getHeight())) - (((long) rhs.getWidth()) * ((long) rhs.getHeight())));
        }
    }
}
