package im.bclpbkiauv.messenger;

import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.FileLoadOperation;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileUploadOperation;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class FileLoader extends BaseController {
    private static volatile FileLoader[] Instance = new FileLoader[3];
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_CACHE = 4;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_VIDEO = 2;
    /* access modifiers changed from: private */
    public static volatile DispatchQueue fileLoaderQueue = new DispatchQueue("fileUploadQueue");
    private static SparseArray<File> mediaDirs = null;
    private ArrayList<FileLoadOperation> activeFileLoadOperation = new ArrayList<>();
    private SparseArray<LinkedList<FileLoadOperation>> audioLoadOperationQueues = new SparseArray<>();
    private SparseIntArray currentAudioLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentLoadOperationsCount = new SparseIntArray();
    private SparseIntArray currentPhotoLoadOperationsCount = new SparseIntArray();
    /* access modifiers changed from: private */
    public int currentUploadOperationsCount = 0;
    /* access modifiers changed from: private */
    public int currentUploadSmallOperationsCount = 0;
    /* access modifiers changed from: private */
    public FileLoaderDelegate delegate = null;
    private int lastReferenceId;
    private ConcurrentHashMap<String, FileLoadOperation> loadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, Boolean> loadOperationPathsUI = new ConcurrentHashMap<>(10, 1.0f, 2);
    private SparseArray<LinkedList<FileLoadOperation>> loadOperationQueues = new SparseArray<>();
    private HashMap<String, Boolean> loadingVideos = new HashMap<>();
    private ConcurrentHashMap<Integer, Object> parentObjectReferences = new ConcurrentHashMap<>();
    private SparseArray<LinkedList<FileLoadOperation>> photoLoadOperationQueues = new SparseArray<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, FileUploadOperation> uploadOperationPaths = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, FileUploadOperation> uploadOperationPathsEnc = new ConcurrentHashMap<>();
    /* access modifiers changed from: private */
    public LinkedList<FileUploadOperation> uploadOperationQueue = new LinkedList<>();
    private HashMap<String, Long> uploadSizes = new HashMap<>();
    /* access modifiers changed from: private */
    public LinkedList<FileUploadOperation> uploadSmallOperationQueue = new LinkedList<>();

    public interface FileLoaderDelegate {
        void fileDidFailedLoad(String str, int i);

        void fileDidFailedUpload(String str, boolean z);

        void fileDidLoaded(String str, File file, int i);

        void fileDidUploaded(String str, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j, boolean z);

        void fileLoadProgressChanged(String str, float f);

        void fileUploadProgressChanged(String str, float f, boolean z);
    }

    static /* synthetic */ int access$608(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$610(FileLoader x0) {
        int i = x0.currentUploadSmallOperationsCount;
        x0.currentUploadSmallOperationsCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$808(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$810(FileLoader x0) {
        int i = x0.currentUploadOperationsCount;
        x0.currentUploadOperationsCount = i - 1;
        return i;
    }

    public static FileLoader getInstance(int num) {
        FileLoader localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileLoader.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileLoader[] fileLoaderArr = Instance;
                    FileLoader fileLoader = new FileLoader(num);
                    localInstance = fileLoader;
                    fileLoaderArr[num] = fileLoader;
                }
            }
        }
        return localInstance;
    }

    public FileLoader(int instance) {
        super(instance);
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File checkDirectory(int type) {
        return mediaDirs.get(type);
    }

    public static File getDirectory(int type) {
        File dir = mediaDirs.get(type);
        if (dir == null && type != 4) {
            dir = mediaDirs.get(4);
        }
        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        return dir;
    }

    public int getFileReference(Object parentObject) {
        int reference = this.lastReferenceId;
        this.lastReferenceId = reference + 1;
        this.parentObjectReferences.put(Integer.valueOf(reference), parentObject);
        return reference;
    }

    public Object getParentObject(int reference) {
        return this.parentObjectReferences.get(Integer.valueOf(reference));
    }

    /* renamed from: setLoadingVideoInternal */
    public void lambda$setLoadingVideo$0$FileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? TtmlNode.TAG_P : "");
        this.loadingVideos.put(sb.toString(), true);
        getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
    }

    public void setLoadingVideo(TLRPC.Document document, boolean player, boolean schedule) {
        if (document != null) {
            if (schedule) {
                AndroidUtilities.runOnUIThread(new Runnable(document, player) {
                    private final /* synthetic */ TLRPC.Document f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileLoader.this.lambda$setLoadingVideo$0$FileLoader(this.f$1, this.f$2);
                    }
                });
            } else {
                lambda$setLoadingVideo$0$FileLoader(document, player);
            }
        }
    }

    public void setLoadingVideoForPlayer(TLRPC.Document document, boolean player) {
        if (document != null) {
            String key = getAttachFileName(document);
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(key);
            String str = "";
            sb.append(player ? str : TtmlNode.TAG_P);
            if (hashMap.containsKey(sb.toString())) {
                HashMap<String, Boolean> hashMap2 = this.loadingVideos;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(key);
                if (player) {
                    str = TtmlNode.TAG_P;
                }
                sb2.append(str);
                hashMap2.put(sb2.toString(), true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: removeLoadingVideoInternal */
    public void lambda$removeLoadingVideo$1$FileLoader(TLRPC.Document document, boolean player) {
        String key = getAttachFileName(document);
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(player ? TtmlNode.TAG_P : "");
        if (this.loadingVideos.remove(sb.toString()) != null) {
            getNotificationCenter().postNotificationName(NotificationCenter.videoLoadingStateChanged, key);
        }
    }

    public void removeLoadingVideo(TLRPC.Document document, boolean player, boolean schedule) {
        if (document != null) {
            if (schedule) {
                AndroidUtilities.runOnUIThread(new Runnable(document, player) {
                    private final /* synthetic */ TLRPC.Document f$1;
                    private final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileLoader.this.lambda$removeLoadingVideo$1$FileLoader(this.f$1, this.f$2);
                    }
                });
            } else {
                lambda$removeLoadingVideo$1$FileLoader(document, player);
            }
        }
    }

    public boolean isLoadingVideo(TLRPC.Document document, boolean player) {
        if (document != null) {
            HashMap<String, Boolean> hashMap = this.loadingVideos;
            StringBuilder sb = new StringBuilder();
            sb.append(getAttachFileName(document));
            sb.append(player ? TtmlNode.TAG_P : "");
            if (hashMap.containsKey(sb.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoadingVideoAny(TLRPC.Document document) {
        return isLoadingVideo(document, false) || isLoadingVideo(document, true);
    }

    public void cancelUploadFile(String location, boolean enc) {
        fileLoaderQueue.postRunnable(new Runnable(enc, location) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FileLoader.this.lambda$cancelUploadFile$2$FileLoader(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$cancelUploadFile$2$FileLoader(boolean enc, String location) {
        FileUploadOperation operation;
        if (!enc) {
            operation = this.uploadOperationPaths.get(location);
        } else {
            operation = this.uploadOperationPathsEnc.get(location);
        }
        this.uploadSizes.remove(location);
        if (operation != null) {
            this.uploadOperationPathsEnc.remove(location);
            this.uploadOperationQueue.remove(operation);
            this.uploadSmallOperationQueue.remove(operation);
            operation.cancel();
        }
    }

    public void checkUploadNewDataAvailable(String location, boolean encrypted, long newAvailableSize, long finalSize) {
        fileLoaderQueue.postRunnable(new Runnable(encrypted, location, newAvailableSize, finalSize) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ long f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
            }

            public final void run() {
                FileLoader.this.lambda$checkUploadNewDataAvailable$3$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$checkUploadNewDataAvailable$3$FileLoader(boolean encrypted, String location, long newAvailableSize, long finalSize) {
        FileUploadOperation operation;
        if (encrypted) {
            operation = this.uploadOperationPathsEnc.get(location);
        } else {
            operation = this.uploadOperationPaths.get(location);
        }
        if (operation != null) {
            operation.checkNewDataAvailable(newAvailableSize, finalSize);
        } else if (finalSize != 0) {
            this.uploadSizes.put(location, Long.valueOf(finalSize));
        }
    }

    public void onNetworkChanged(boolean slow) {
        fileLoaderQueue.postRunnable(new Runnable(slow) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FileLoader.this.lambda$onNetworkChanged$4$FileLoader(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onNetworkChanged$4$FileLoader(boolean slow) {
        for (Map.Entry<String, FileUploadOperation> entry : this.uploadOperationPaths.entrySet()) {
            entry.getValue().onNetworkChanged(slow);
        }
        for (Map.Entry<String, FileUploadOperation> entry2 : this.uploadOperationPathsEnc.entrySet()) {
            entry2.getValue().onNetworkChanged(slow);
        }
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type, boolean apply) {
        uploadFile(location, encrypted, small, 0, type, apply);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int type) {
        uploadFile(location, encrypted, small, 0, type, true);
    }

    public void uploadFile(String location, boolean encrypted, boolean small, int estimatedSize, int type, boolean apply) {
        if (location != null) {
            fileLoaderQueue.postRunnable(new Runnable(encrypted, location, estimatedSize, type, small, apply) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ boolean f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run() {
                    FileLoader.this.lambda$uploadFile$5$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }
    }

    public /* synthetic */ void lambda$uploadFile$5$FileLoader(boolean encrypted, String location, int estimatedSize, int type, boolean small, boolean apply) {
        if (encrypted) {
            if (this.uploadOperationPathsEnc.containsKey(location)) {
                return;
            }
        } else if (this.uploadOperationPaths.containsKey(location)) {
            return;
        }
        int esimated = estimatedSize;
        if (!(esimated == 0 || this.uploadSizes.get(location) == null)) {
            esimated = 0;
            this.uploadSizes.remove(location);
        }
        FileUploadOperation fileUploadOperation = new FileUploadOperation(this.currentAccount, location, encrypted, esimated, type);
        if (encrypted) {
            this.uploadOperationPathsEnc.put(location, fileUploadOperation);
        } else {
            this.uploadOperationPaths.put(location, fileUploadOperation);
        }
        final boolean z = encrypted;
        final String str = location;
        final boolean z2 = small;
        final boolean z3 = apply;
        fileUploadOperation.setDelegate(new FileUploadOperation.FileUploadOperationDelegate() {
            public void didFinishUploadingFile(FileUploadOperation operation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv) {
                FileLoader.fileLoaderQueue.postRunnable(new Runnable(z, str, z2, inputFile, inputEncryptedFile, key, iv, operation, z3) {
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ String f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ TLRPC.InputFile f$4;
                    private final /* synthetic */ TLRPC.InputEncryptedFile f$5;
                    private final /* synthetic */ byte[] f$6;
                    private final /* synthetic */ byte[] f$7;
                    private final /* synthetic */ FileUploadOperation f$8;
                    private final /* synthetic */ boolean f$9;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                        this.f$8 = r9;
                        this.f$9 = r10;
                    }

                    public final void run() {
                        FileLoader.AnonymousClass1.this.lambda$didFinishUploadingFile$0$FileLoader$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
                    }
                });
            }

            public /* synthetic */ void lambda$didFinishUploadingFile$0$FileLoader$1(boolean encrypted, String location, boolean small, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, FileUploadOperation operation, boolean apply) {
                FileUploadOperation operation12;
                FileUploadOperation operation122;
                String str = location;
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (small) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation122 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        operation122.start();
                    }
                } else {
                    FileLoader.access$810(FileLoader.this);
                    if (FileLoader.this.currentUploadOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                        FileLoader.access$808(FileLoader.this);
                        operation12.start();
                    }
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidUploaded(location, inputFile, inputEncryptedFile, key, iv, operation.getTotalFileSize(), apply);
                }
            }

            public void didFailedUploadingFile(FileUploadOperation operation) {
                FileLoader.fileLoaderQueue.postRunnable(new Runnable(z, str, z2) {
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ String f$2;
                    private final /* synthetic */ boolean f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        FileLoader.AnonymousClass1.this.lambda$didFailedUploadingFile$1$FileLoader$1(this.f$1, this.f$2, this.f$3);
                    }
                });
            }

            public /* synthetic */ void lambda$didFailedUploadingFile$1$FileLoader$1(boolean encrypted, String location, boolean small) {
                FileUploadOperation operation1;
                FileUploadOperation operation12;
                if (encrypted) {
                    FileLoader.this.uploadOperationPathsEnc.remove(location);
                } else {
                    FileLoader.this.uploadOperationPaths.remove(location);
                }
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileDidFailedUpload(location, encrypted);
                }
                if (small) {
                    FileLoader.access$610(FileLoader.this);
                    if (FileLoader.this.currentUploadSmallOperationsCount < 1 && (operation12 = (FileUploadOperation) FileLoader.this.uploadSmallOperationQueue.poll()) != null) {
                        FileLoader.access$608(FileLoader.this);
                        operation12.start();
                        return;
                    }
                    return;
                }
                FileLoader.access$810(FileLoader.this);
                if (FileLoader.this.currentUploadOperationsCount < 1 && (operation1 = (FileUploadOperation) FileLoader.this.uploadOperationQueue.poll()) != null) {
                    FileLoader.access$808(FileLoader.this);
                    operation1.start();
                }
            }

            public void didChangedUploadProgress(FileUploadOperation operation, float progress) {
                if (FileLoader.this.delegate != null) {
                    FileLoader.this.delegate.fileUploadProgressChanged(str, progress, z);
                }
            }
        });
        if (small) {
            int i = this.currentUploadSmallOperationsCount;
            if (i < 1) {
                this.currentUploadSmallOperationsCount = i + 1;
                fileUploadOperation.start();
                return;
            }
            this.uploadSmallOperationQueue.add(fileUploadOperation);
            return;
        }
        int i2 = this.currentUploadOperationsCount;
        if (i2 < 1) {
            this.currentUploadOperationsCount = i2 + 1;
            fileUploadOperation.start();
            return;
        }
        this.uploadOperationQueue.add(fileUploadOperation);
    }

    private LinkedList<FileLoadOperation> getAudioLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> audioLoadOperationQueue = this.audioLoadOperationQueues.get(datacenterId);
        if (audioLoadOperationQueue != null) {
            return audioLoadOperationQueue;
        }
        LinkedList<FileLoadOperation> audioLoadOperationQueue2 = new LinkedList<>();
        this.audioLoadOperationQueues.put(datacenterId, audioLoadOperationQueue2);
        return audioLoadOperationQueue2;
    }

    private LinkedList<FileLoadOperation> getPhotoLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> photoLoadOperationQueue = this.photoLoadOperationQueues.get(datacenterId);
        if (photoLoadOperationQueue != null) {
            return photoLoadOperationQueue;
        }
        LinkedList<FileLoadOperation> photoLoadOperationQueue2 = new LinkedList<>();
        this.photoLoadOperationQueues.put(datacenterId, photoLoadOperationQueue2);
        return photoLoadOperationQueue2;
    }

    private LinkedList<FileLoadOperation> getLoadOperationQueue(int datacenterId) {
        LinkedList<FileLoadOperation> loadOperationQueue = this.loadOperationQueues.get(datacenterId);
        if (loadOperationQueue != null) {
            return loadOperationQueue;
        }
        LinkedList<FileLoadOperation> loadOperationQueue2 = new LinkedList<>();
        this.loadOperationQueues.put(datacenterId, loadOperationQueue2);
        return loadOperationQueue2;
    }

    public void cancelLoadFile(TLRPC.Document document) {
        cancelLoadFile(document, (SecureDocument) null, (WebFile) null, (TLRPC.FileLocation) null, (String) null);
    }

    public void cancelLoadFile(SecureDocument document) {
        cancelLoadFile((TLRPC.Document) null, document, (WebFile) null, (TLRPC.FileLocation) null, (String) null);
    }

    public void cancelLoadFile(WebFile document) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, document, (TLRPC.FileLocation) null, (String) null);
    }

    public void cancelLoadFile(TLRPC.PhotoSize photo) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, photo.location, (String) null);
    }

    public void cancelLoadFile(TLRPC.FileLocation location, String ext) {
        cancelLoadFile((TLRPC.Document) null, (SecureDocument) null, (WebFile) null, location, ext);
    }

    private void cancelLoadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.FileLocation location, String locationExt) {
        String fileName;
        if (location != null || document != null || webDocument != null || secureDocument != null) {
            if (location != null) {
                fileName = getAttachFileName(location, locationExt);
            } else if (document != null) {
                fileName = getAttachFileName(document);
            } else if (secureDocument != null) {
                fileName = getAttachFileName(secureDocument);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = null;
            }
            if (fileName != null) {
                this.loadOperationPathsUI.remove(fileName);
                fileLoaderQueue.postRunnable(new Runnable(fileName, document, webDocument, secureDocument, location) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ TLRPC.Document f$2;
                    private final /* synthetic */ WebFile f$3;
                    private final /* synthetic */ SecureDocument f$4;
                    private final /* synthetic */ TLRPC.FileLocation f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void run() {
                        FileLoader.this.lambda$cancelLoadFile$6$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$cancelLoadFile$6$FileLoader(String fileName, TLRPC.Document document, WebFile webDocument, SecureDocument secureDocument, TLRPC.FileLocation location) {
        FileLoadOperation operation = this.loadOperationPaths.remove(fileName);
        if (operation != null) {
            int datacenterId = operation.getDatacenterId();
            if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
                if (!getAudioLoadOperationQueue(datacenterId).remove(operation)) {
                    SparseIntArray sparseIntArray = this.currentAudioLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
            } else if (secureDocument == null && location == null && !MessageObject.isImageWebDocument(webDocument)) {
                if (!getLoadOperationQueue(datacenterId).remove(operation)) {
                    SparseIntArray sparseIntArray2 = this.currentLoadOperationsCount;
                    sparseIntArray2.put(datacenterId, sparseIntArray2.get(datacenterId) - 1);
                }
                this.activeFileLoadOperation.remove(operation);
            } else if (!getPhotoLoadOperationQueue(datacenterId).remove(operation)) {
                SparseIntArray sparseIntArray3 = this.currentPhotoLoadOperationsCount;
                sparseIntArray3.put(datacenterId, sparseIntArray3.get(datacenterId) - 1);
            }
            operation.cancel();
        }
    }

    public boolean isLoadingFile(String fileName) {
        return this.loadOperationPathsUI.containsKey(fileName);
    }

    public float getBufferedProgressFromPosition(float position, String fileName) {
        FileLoadOperation loadOperation;
        if (!TextUtils.isEmpty(fileName) && (loadOperation = this.loadOperationPaths.get(fileName)) != null) {
            return loadOperation.getDownloadedLengthFromOffset(position);
        }
        return 0.0f;
    }

    public void loadFile(ImageLocation imageLocation, Object parentObject, String ext, int priority, int cacheType) {
        int cacheType2;
        ImageLocation imageLocation2 = imageLocation;
        if (imageLocation2 != null) {
            if (cacheType != 0 || (!imageLocation.isEncrypted() && (imageLocation2.photoSize == null || imageLocation.getSize() != 0))) {
                cacheType2 = cacheType;
            } else {
                cacheType2 = 1;
            }
            loadFile(imageLocation2.document, imageLocation2.secureDocument, imageLocation2.webFile, imageLocation2.location, imageLocation, parentObject, ext, imageLocation.getSize(), priority, cacheType2);
        }
    }

    public void loadFile(SecureDocument secureDocument, int priority) {
        if (secureDocument != null) {
            loadFile((TLRPC.Document) null, secureDocument, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, priority, 1);
        }
    }

    public void loadFile(TLRPC.Document document, Object parentObject, int priority, int cacheType) {
        if (document != null) {
            if (cacheType == 0 && document.key != null) {
                cacheType = 1;
            }
            loadFile(document, (SecureDocument) null, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, parentObject, (String) null, 0, priority, cacheType);
        }
    }

    public void loadFile(WebFile document, int priority, int cacheType) {
        loadFile((TLRPC.Document) null, (SecureDocument) null, document, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, (Object) null, (String) null, 0, priority, cacheType);
    }

    private void pauseCurrentFileLoadOperations(FileLoadOperation newOperation) {
        int a = 0;
        while (a < this.activeFileLoadOperation.size()) {
            FileLoadOperation operation = this.activeFileLoadOperation.get(a);
            if (operation != newOperation && operation.getDatacenterId() == newOperation.getDatacenterId()) {
                this.activeFileLoadOperation.remove(operation);
                a--;
                int datacenterId = operation.getDatacenterId();
                getLoadOperationQueue(datacenterId).add(0, operation);
                if (operation.wasStarted()) {
                    SparseIntArray sparseIntArray = this.currentLoadOperationsCount;
                    sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) - 1);
                }
                operation.pause();
            }
            a++;
        }
    }

    private FileLoadOperation loadFileInternal(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, FileLoadOperationStream stream, int streamOffset, boolean streamPriority, int cacheType) {
        String fileName;
        File storeDir;
        FileLoadOperation operation;
        int type;
        File storeDir2;
        LinkedList<FileLoadOperation> downloadQueue;
        TLRPC.Document document2 = document;
        SecureDocument secureDocument2 = secureDocument;
        WebFile webFile = webDocument;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = location;
        Object obj = parentObject;
        String str = locationExt;
        int i = priority;
        FileLoadOperationStream fileLoadOperationStream = stream;
        int i2 = streamOffset;
        boolean z = streamPriority;
        int i3 = cacheType;
        if (tL_fileLocationToBeDeprecated != null) {
            fileName = getAttachFileName(tL_fileLocationToBeDeprecated, str);
        } else if (secureDocument2 != null) {
            fileName = getAttachFileName(secureDocument);
        } else if (document2 != null) {
            fileName = getAttachFileName(document);
        } else if (webFile != null) {
            fileName = getAttachFileName(webDocument);
        } else {
            fileName = null;
        }
        if (fileName == null) {
            boolean z2 = z;
            int i4 = i;
            return null;
        } else if (fileName.contains("-2147483648")) {
            String str2 = fileName;
            boolean z3 = z;
            int i5 = i;
            return null;
        } else {
            if (i3 != 10 && !TextUtils.isEmpty(fileName) && !fileName.contains("-2147483648")) {
                this.loadOperationPathsUI.put(fileName, true);
            }
            FileLoadOperation operation2 = this.loadOperationPaths.get(fileName);
            if (operation2 != null) {
                if (i3 != 10 && operation2.isPreloadVideoOperation()) {
                    operation2.setIsPreloadVideoOperation(false);
                }
                if (fileLoadOperationStream != null || i > 0) {
                    int datacenterId = operation2.getDatacenterId();
                    LinkedList<FileLoadOperation> audioLoadOperationQueue = getAudioLoadOperationQueue(datacenterId);
                    LinkedList<FileLoadOperation> photoLoadOperationQueue = getPhotoLoadOperationQueue(datacenterId);
                    String str3 = fileName;
                    LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(datacenterId);
                    operation2.setForceRequest(true);
                    if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
                        downloadQueue = audioLoadOperationQueue;
                    } else if (secureDocument2 == null && tL_fileLocationToBeDeprecated == null && !MessageObject.isImageWebDocument(webDocument)) {
                        downloadQueue = loadOperationQueue;
                    } else {
                        downloadQueue = photoLoadOperationQueue;
                    }
                    if (downloadQueue != null) {
                        int index = downloadQueue.indexOf(operation2);
                        if (index >= 0) {
                            downloadQueue.remove(index);
                            if (fileLoadOperationStream == null) {
                                int i6 = index;
                                downloadQueue.add(0, operation2);
                            } else if (downloadQueue != audioLoadOperationQueue) {
                                int i7 = index;
                                if (downloadQueue != photoLoadOperationQueue) {
                                    if (operation2.start(fileLoadOperationStream, i2, z)) {
                                        SparseIntArray sparseIntArray = this.currentLoadOperationsCount;
                                        sparseIntArray.put(datacenterId, sparseIntArray.get(datacenterId) + 1);
                                    }
                                    if (operation2.wasStarted() && !this.activeFileLoadOperation.contains(operation2)) {
                                        if (fileLoadOperationStream != null) {
                                            pauseCurrentFileLoadOperations(operation2);
                                        }
                                        this.activeFileLoadOperation.add(operation2);
                                    }
                                } else if (operation2.start(fileLoadOperationStream, i2, z)) {
                                    SparseIntArray sparseIntArray2 = this.currentPhotoLoadOperationsCount;
                                    sparseIntArray2.put(datacenterId, sparseIntArray2.get(datacenterId) + 1);
                                }
                            } else if (operation2.start(fileLoadOperationStream, i2, z)) {
                                LinkedList<FileLoadOperation> linkedList = audioLoadOperationQueue;
                                SparseIntArray sparseIntArray3 = this.currentAudioLoadOperationsCount;
                                int i8 = index;
                                sparseIntArray3.put(datacenterId, sparseIntArray3.get(datacenterId) + 1);
                            } else {
                                int i9 = index;
                            }
                        } else {
                            int i10 = index;
                            if (fileLoadOperationStream != null) {
                                pauseCurrentFileLoadOperations(operation2);
                            }
                            operation2.start(fileLoadOperationStream, i2, z);
                            if (downloadQueue == loadOperationQueue && !this.activeFileLoadOperation.contains(operation2)) {
                                this.activeFileLoadOperation.add(operation2);
                            }
                        }
                    }
                } else {
                    String str4 = fileName;
                }
                return operation2;
            }
            String fileName2 = fileName;
            File tempDir = getDirectory(4);
            File storeDir3 = tempDir;
            if (secureDocument2 != null) {
                WebFile webFile2 = webDocument;
                ImageLocation imageLocation2 = imageLocation;
                int i11 = locationSize;
                storeDir = storeDir3;
                operation = new FileLoadOperation(secureDocument2);
                type = 3;
            } else if (tL_fileLocationToBeDeprecated != null) {
                WebFile webFile3 = webDocument;
                storeDir = storeDir3;
                operation = new FileLoadOperation(imageLocation, obj, str, locationSize);
                type = 0;
            } else {
                ImageLocation imageLocation3 = imageLocation;
                int i12 = locationSize;
                if (document2 != null) {
                    FileLoadOperation operation3 = new FileLoadOperation(document2, obj);
                    if (MessageObject.isVoiceDocument(document)) {
                        WebFile webFile4 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 1;
                    } else if (MessageObject.isVideoDocument(document)) {
                        WebFile webFile5 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 2;
                    } else {
                        WebFile webFile6 = webDocument;
                        storeDir = storeDir3;
                        operation = operation3;
                        type = 3;
                    }
                } else {
                    WebFile webFile7 = webDocument;
                    if (webFile7 != null) {
                        FileLoadOperation fileLoadOperation = operation2;
                        storeDir = storeDir3;
                        FileLoadOperation operation4 = new FileLoadOperation(this.currentAccount, webFile7);
                        if (MessageObject.isVoiceWebDocument(webDocument)) {
                            operation = operation4;
                            type = 1;
                        } else if (MessageObject.isVideoWebDocument(webDocument)) {
                            operation = operation4;
                            type = 2;
                        } else if (MessageObject.isImageWebDocument(webDocument)) {
                            operation = operation4;
                            type = 0;
                        } else {
                            operation = operation4;
                            type = 3;
                        }
                    } else {
                        FileLoadOperation operation5 = operation2;
                        storeDir = storeDir3;
                        type = 4;
                        operation = operation5;
                    }
                }
            }
            int type2 = cacheType;
            if (type2 == 0 || type2 == 10) {
                storeDir2 = getDirectory(type);
            } else {
                if (type2 == 2) {
                    operation.setEncryptFile(true);
                }
                storeDir2 = storeDir;
            }
            operation.setPaths(this.currentAccount, storeDir2, tempDir);
            if (type2 == 10) {
                operation.setIsPreloadVideoOperation(true);
            }
            final String finalFileName = fileName2;
            File file = storeDir2;
            final int finalType = type;
            int type3 = type;
            FileLoadOperation operation6 = operation;
            final TLRPC.Document document3 = document;
            final WebFile webFile8 = webDocument;
            final TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated2 = location;
            AnonymousClass2 r0 = new FileLoadOperation.FileLoadOperationDelegate() {
                public void didFinishLoadingFile(FileLoadOperation operation, File finalFile) {
                    if (operation.isPreloadVideoOperation() || !operation.isPreloadFinished()) {
                        if (!operation.isPreloadVideoOperation()) {
                            FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                            if (FileLoader.this.delegate != null) {
                                FileLoader.this.delegate.fileDidLoaded(finalFileName, finalFile, finalType);
                            }
                        }
                        FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), document3, webFile8, tL_fileLocationToBeDeprecated2, finalFileName);
                    }
                }

                public void didFailedLoadingFile(FileLoadOperation operation, int reason) {
                    FileLoader.this.loadOperationPathsUI.remove(finalFileName);
                    FileLoader.this.checkDownloadQueue(operation.getDatacenterId(), document3, webFile8, tL_fileLocationToBeDeprecated2, finalFileName);
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileDidFailedLoad(finalFileName, reason);
                    }
                }

                public void didChangedLoadProgress(FileLoadOperation operation, float progress) {
                    if (FileLoader.this.delegate != null) {
                        FileLoader.this.delegate.fileLoadProgressChanged(finalFileName, progress);
                    }
                }
            };
            operation6.setDelegate(r0);
            int datacenterId2 = operation6.getDatacenterId();
            LinkedList<FileLoadOperation> audioLoadOperationQueue2 = getAudioLoadOperationQueue(datacenterId2);
            LinkedList<FileLoadOperation> photoLoadOperationQueue2 = getPhotoLoadOperationQueue(datacenterId2);
            LinkedList<FileLoadOperation> loadOperationQueue2 = getLoadOperationQueue(datacenterId2);
            this.loadOperationPaths.put(fileName2, operation6);
            int i13 = priority;
            operation6.setPriority(i13);
            AnonymousClass2 r18 = r0;
            int maxCount = 1;
            if (type3 == 1) {
                if (i13 > 0) {
                    maxCount = 3;
                }
                String str5 = finalFileName;
                int count = this.currentAudioLoadOperationsCount.get(datacenterId2);
                if (fileLoadOperationStream != null || count < maxCount) {
                    int i14 = maxCount;
                    int i15 = finalType;
                    if (operation6.start(fileLoadOperationStream, streamOffset, streamPriority)) {
                        LinkedList<FileLoadOperation> linkedList2 = audioLoadOperationQueue2;
                        int i16 = type3;
                        this.currentAudioLoadOperationsCount.put(datacenterId2, count + 1);
                    } else {
                        int i17 = type3;
                    }
                } else {
                    addOperationToQueue(operation6, audioLoadOperationQueue2);
                    int maxCount2 = streamOffset;
                    int i18 = finalType;
                    LinkedList<FileLoadOperation> linkedList3 = audioLoadOperationQueue2;
                    int i19 = type3;
                    int finalType2 = streamPriority;
                }
            } else {
                int i20 = streamOffset;
                String str6 = finalFileName;
                int i21 = finalType;
                LinkedList<FileLoadOperation> linkedList4 = audioLoadOperationQueue2;
                int i22 = type3;
                int maxCount3 = 1;
                boolean z4 = streamPriority;
                if (tL_fileLocationToBeDeprecated != null || MessageObject.isImageWebDocument(webDocument)) {
                    int maxCount4 = i13 > 0 ? 6 : 2;
                    int count2 = this.currentPhotoLoadOperationsCount.get(datacenterId2);
                    if (fileLoadOperationStream == null && count2 >= maxCount4) {
                        addOperationToQueue(operation6, photoLoadOperationQueue2);
                    } else if (operation6.start(fileLoadOperationStream, i20, z4)) {
                        this.currentPhotoLoadOperationsCount.put(datacenterId2, count2 + 1);
                    }
                } else {
                    if (i13 > 0) {
                        maxCount3 = 4;
                    }
                    int count3 = this.currentLoadOperationsCount.get(datacenterId2);
                    if (fileLoadOperationStream != null || count3 < maxCount3) {
                        if (operation6.start(fileLoadOperationStream, i20, z4)) {
                            int i23 = maxCount3;
                            this.currentLoadOperationsCount.put(datacenterId2, count3 + 1);
                            this.activeFileLoadOperation.add(operation6);
                        }
                        if (!(operation6.wasStarted() == 0 || fileLoadOperationStream == null)) {
                            pauseCurrentFileLoadOperations(operation6);
                        }
                    } else {
                        addOperationToQueue(operation6, loadOperationQueue2);
                    }
                }
            }
            return operation6;
        }
    }

    private void addOperationToQueue(FileLoadOperation operation, LinkedList<FileLoadOperation> queue) {
        int priority = operation.getPriority();
        if (priority > 0) {
            int index = queue.size();
            int a = 0;
            int size = queue.size();
            while (true) {
                if (a >= size) {
                    break;
                } else if (queue.get(a).getPriority() < priority) {
                    index = a;
                    break;
                } else {
                    a++;
                }
            }
            queue.add(index, operation);
            return;
        }
        queue.add(operation);
    }

    private void loadFile(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        String fileName;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = location;
        if (tL_fileLocationToBeDeprecated != null) {
            fileName = getAttachFileName(tL_fileLocationToBeDeprecated, locationExt);
        } else {
            String str = locationExt;
            if (document != null) {
                fileName = getAttachFileName(document);
            } else if (webDocument != null) {
                fileName = getAttachFileName(webDocument);
            } else {
                fileName = null;
            }
        }
        if (cacheType != 10 && !TextUtils.isEmpty(fileName) && !fileName.contains("-2147483648")) {
            this.loadOperationPathsUI.put(fileName, true);
        }
        $$Lambda$FileLoader$ZhWOdj4LE5Z14G4zl5So2GcT3E r12 = r0;
        DispatchQueue dispatchQueue = fileLoaderQueue;
        $$Lambda$FileLoader$ZhWOdj4LE5Z14G4zl5So2GcT3E r0 = new Runnable(document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, cacheType) {
            private final /* synthetic */ TLRPC.Document f$1;
            private final /* synthetic */ int f$10;
            private final /* synthetic */ SecureDocument f$2;
            private final /* synthetic */ WebFile f$3;
            private final /* synthetic */ TLRPC.TL_fileLocationToBeDeprecated f$4;
            private final /* synthetic */ ImageLocation f$5;
            private final /* synthetic */ Object f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ int f$8;
            private final /* synthetic */ int f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
                this.f$9 = r10;
                this.f$10 = r11;
            }

            public final void run() {
                FileLoader.this.lambda$loadFile$7$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
            }
        };
        dispatchQueue.postRunnable(r12);
    }

    public /* synthetic */ void lambda$loadFile$7$FileLoader(TLRPC.Document document, SecureDocument secureDocument, WebFile webDocument, TLRPC.TL_fileLocationToBeDeprecated location, ImageLocation imageLocation, Object parentObject, String locationExt, int locationSize, int priority, int cacheType) {
        loadFileInternal(document, secureDocument, webDocument, location, imageLocation, parentObject, locationExt, locationSize, priority, (FileLoadOperationStream) null, 0, false, cacheType);
    }

    /* access modifiers changed from: protected */
    public FileLoadOperation loadStreamFile(FileLoadOperationStream stream, TLRPC.Document document, Object parentObject, int offset, boolean priority) {
        CountDownLatch semaphore = new CountDownLatch(1);
        FileLoadOperation[] result = new FileLoadOperation[1];
        fileLoaderQueue.postRunnable(new Runnable(result, document, parentObject, stream, offset, priority, semaphore) {
            private final /* synthetic */ FileLoadOperation[] f$1;
            private final /* synthetic */ TLRPC.Document f$2;
            private final /* synthetic */ Object f$3;
            private final /* synthetic */ FileLoadOperationStream f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ CountDownLatch f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                FileLoader.this.lambda$loadStreamFile$8$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
        try {
            semaphore.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return result[0];
    }

    public /* synthetic */ void lambda$loadStreamFile$8$FileLoader(FileLoadOperation[] result, TLRPC.Document document, Object parentObject, FileLoadOperationStream stream, int offset, boolean priority, CountDownLatch semaphore) {
        result[0] = loadFileInternal(document, (SecureDocument) null, (WebFile) null, (TLRPC.TL_fileLocationToBeDeprecated) null, (ImageLocation) null, parentObject, (String) null, 0, 1, stream, offset, priority, 0);
        semaphore.countDown();
    }

    /* access modifiers changed from: private */
    public void checkDownloadQueue(int datacenterId, TLRPC.Document document, WebFile webDocument, TLRPC.FileLocation location, String arg1) {
        fileLoaderQueue.postRunnable(new Runnable(datacenterId, arg1, document, webDocument, location) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ TLRPC.Document f$3;
            private final /* synthetic */ WebFile f$4;
            private final /* synthetic */ TLRPC.FileLocation f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                FileLoader.this.lambda$checkDownloadQueue$9$FileLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$checkDownloadQueue$9$FileLoader(int datacenterId, String arg1, TLRPC.Document document, WebFile webDocument, TLRPC.FileLocation location) {
        LinkedList<FileLoadOperation> audioLoadOperationQueue = getAudioLoadOperationQueue(datacenterId);
        LinkedList<FileLoadOperation> photoLoadOperationQueue = getPhotoLoadOperationQueue(datacenterId);
        LinkedList<FileLoadOperation> loadOperationQueue = getLoadOperationQueue(datacenterId);
        FileLoadOperation operation = this.loadOperationPaths.remove(arg1);
        if (MessageObject.isVoiceDocument(document) || MessageObject.isVoiceWebDocument(webDocument)) {
            int count = this.currentAudioLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count--;
                    this.currentAudioLoadOperationsCount.put(datacenterId, count);
                } else {
                    audioLoadOperationQueue.remove(operation);
                }
            }
            while (!audioLoadOperationQueue.isEmpty()) {
                if (count < (audioLoadOperationQueue.get(0).getPriority() != 0 ? 3 : 1)) {
                    FileLoadOperation operation2 = audioLoadOperationQueue.poll();
                    if (operation2 != null && operation2.start()) {
                        count++;
                        this.currentAudioLoadOperationsCount.put(datacenterId, count);
                    }
                } else {
                    return;
                }
            }
        } else if (location != null || MessageObject.isImageWebDocument(webDocument)) {
            int count2 = this.currentPhotoLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count2--;
                    this.currentPhotoLoadOperationsCount.put(datacenterId, count2);
                } else {
                    photoLoadOperationQueue.remove(operation);
                }
            }
            while (!photoLoadOperationQueue.isEmpty()) {
                if (count2 < (photoLoadOperationQueue.get(0).getPriority() != 0 ? 6 : 2)) {
                    FileLoadOperation operation3 = photoLoadOperationQueue.poll();
                    if (operation3 != null && operation3.start()) {
                        count2++;
                        this.currentPhotoLoadOperationsCount.put(datacenterId, count2);
                    }
                } else {
                    return;
                }
            }
        } else {
            int count3 = this.currentLoadOperationsCount.get(datacenterId);
            if (operation != null) {
                if (operation.wasStarted()) {
                    count3--;
                    this.currentLoadOperationsCount.put(datacenterId, count3);
                } else {
                    loadOperationQueue.remove(operation);
                }
                this.activeFileLoadOperation.remove(operation);
            }
            while (!loadOperationQueue.isEmpty()) {
                if (count3 < (loadOperationQueue.get(0).isForceRequest() ? 3 : 1)) {
                    FileLoadOperation operation4 = loadOperationQueue.poll();
                    if (operation4 != null && operation4.start()) {
                        count3++;
                        this.currentLoadOperationsCount.put(datacenterId, count3);
                        if (!this.activeFileLoadOperation.contains(operation4)) {
                            this.activeFileLoadOperation.add(operation4);
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    public void setDelegate(FileLoaderDelegate delegate2) {
        this.delegate = delegate2;
    }

    public static String getMessageFileName(TLRPC.Message message) {
        TLRPC.WebDocument document;
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return "";
        }
        if (message instanceof TLRPC.TL_messageService) {
            if (message.action.photo != null) {
                ArrayList<TLRPC.PhotoSize> sizes = message.action.photo.sizes;
                if (sizes.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(sizeFull3);
                }
            }
        } else if (message.media instanceof TLRPC.TL_messageMediaDocument) {
            return getAttachFileName(message.media.document);
        } else {
            if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes2 = message.media.photo.sizes;
                if (sizes2.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize())) != null) {
                    return getAttachFileName(sizeFull2);
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getAttachFileName(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes3 = message.media.webpage.photo.sizes;
                    if (sizes3.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                        return getAttachFileName(sizeFull);
                    }
                } else if (message.media instanceof TLRPC.TL_messageMediaInvoice) {
                    return getAttachFileName(((TLRPC.TL_messageMediaInvoice) message.media).photo);
                }
            } else if ((message.media instanceof TLRPC.TL_messageMediaInvoice) && (document = ((TLRPC.TL_messageMediaInvoice) message.media).photo) != null) {
                return Utilities.MD5(document.url) + "." + ImageLoader.getHttpUrlExtension(document.url, getMimeTypePart(document.mime_type));
            }
        }
        return "";
    }

    public static File getPathToMessage(TLRPC.Message message) {
        TLRPC.PhotoSize sizeFull;
        TLRPC.PhotoSize sizeFull2;
        TLRPC.PhotoSize sizeFull3;
        if (message == null) {
            return new File("");
        }
        if (!(message instanceof TLRPC.TL_messageService)) {
            boolean z = false;
            if (message.media instanceof TLRPC.TL_messageMediaDocument) {
                TLRPC.Document document = message.media.document;
                if (message.media.ttl_seconds != 0) {
                    z = true;
                }
                return getPathToAttach(document, z);
            } else if (message.media instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> sizes = message.media.photo.sizes;
                if (sizes.size() > 0 && (sizeFull2 = getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize())) != null) {
                    if (message.media.ttl_seconds != 0) {
                        z = true;
                    }
                    return getPathToAttach(sizeFull2, z);
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                if (message.media.webpage.document != null) {
                    return getPathToAttach(message.media.webpage.document);
                }
                if (message.media.webpage.photo != null) {
                    ArrayList<TLRPC.PhotoSize> sizes2 = message.media.webpage.photo.sizes;
                    if (sizes2.size() > 0 && (sizeFull = getClosestPhotoSizeWithSize(sizes2, AndroidUtilities.getPhotoSize())) != null) {
                        return getPathToAttach(sizeFull);
                    }
                }
            } else if (message.media instanceof TLRPC.TL_messageMediaInvoice) {
                return getPathToAttach(((TLRPC.TL_messageMediaInvoice) message.media).photo, true);
            }
        } else if (message.action.photo != null) {
            ArrayList<TLRPC.PhotoSize> sizes3 = message.action.photo.sizes;
            if (sizes3.size() > 0 && (sizeFull3 = getClosestPhotoSizeWithSize(sizes3, AndroidUtilities.getPhotoSize())) != null) {
                return getPathToAttach(sizeFull3);
            }
        }
        return new File("");
    }

    public static File getPathToAttach(TLObject attach) {
        return getPathToAttach(attach, (String) null, false);
    }

    public static File getPathToAttach(TLObject attach, boolean forceCache) {
        return getPathToAttach(attach, (String) null, forceCache);
    }

    public static File getPathToAttach(TLObject attach, String ext, boolean forceCache) {
        File dir = null;
        if (forceCache) {
            dir = getDirectory(4);
        } else if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            if (document.key != null) {
                dir = getDirectory(4);
            } else if (MessageObject.isVoiceDocument(document)) {
                dir = getDirectory(1);
            } else if (MessageObject.isVideoDocument(document)) {
                dir = getDirectory(2);
            } else {
                dir = getDirectory(3);
            }
        } else if (attach instanceof TLRPC.Photo) {
            return getPathToAttach(getClosestPhotoSizeWithSize(((TLRPC.Photo) attach).sizes, AndroidUtilities.getPhotoSize()), ext, forceCache);
        } else {
            if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photoSize = (TLRPC.PhotoSize) attach;
                if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                    dir = null;
                } else if (photoSize.location == null || photoSize.location.key != null || ((photoSize.location.volume_id == -2147483648L && photoSize.location.local_id < 0) || photoSize.size < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation fileLocation = (TLRPC.FileLocation) attach;
                if (fileLocation.key != null || (fileLocation.volume_id == -2147483648L && fileLocation.local_id < 0)) {
                    dir = getDirectory(4);
                } else {
                    dir = getDirectory(0);
                }
            } else if (attach instanceof WebFile) {
                WebFile document2 = (WebFile) attach;
                if (document2.mime_type.startsWith("image/")) {
                    dir = getDirectory(0);
                } else if (document2.mime_type.startsWith("audio/")) {
                    dir = getDirectory(1);
                } else if (document2.mime_type.startsWith("video/")) {
                    dir = getDirectory(2);
                } else {
                    dir = getDirectory(3);
                }
            } else if ((attach instanceof TLRPC.TL_secureFile) || (attach instanceof SecureDocument)) {
                dir = getDirectory(4);
            }
        }
        if (dir == null) {
            return new File("");
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side) {
        return getClosestPhotoSizeWithSize(sizes, side, false);
    }

    public static TLRPC.PhotoSize getClosestPhotoSizeWithSize(ArrayList<TLRPC.PhotoSize> sizes, int side, boolean byMinSide) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        int lastSide = 0;
        TLRPC.PhotoSize closestObject = null;
        for (int a = 0; a < sizes.size(); a++) {
            TLRPC.PhotoSize obj = sizes.get(a);
            if (obj != null && !(obj instanceof TLRPC.TL_photoSizeEmpty)) {
                if (byMinSide) {
                    int currentSide = obj.h >= obj.w ? obj.w : obj.h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (side > lastSide && lastSide < currentSide))) {
                        closestObject = obj;
                        lastSide = currentSide;
                    }
                } else {
                    int currentSide2 = obj.w >= obj.h ? obj.w : obj.h;
                    if (closestObject == null || ((side > 100 && closestObject.location != null && closestObject.location.dc_id == Integer.MIN_VALUE) || (obj instanceof TLRPC.TL_photoCachedSize) || (currentSide2 <= side && lastSide < currentSide2))) {
                        closestObject = obj;
                        lastSide = currentSide2;
                    }
                }
            }
        }
        return closestObject;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(46) + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String fixFileName(String fileName) {
        if (fileName != null) {
            return fileName.replaceAll("[\u0001-\u001f<>‮:\"/\\\\|?*]+", "").trim();
        }
        return fileName;
    }

    public static String getDocumentFileName(TLRPC.Document document) {
        String fileName = null;
        if (document != null) {
            if (document.file_name != null) {
                fileName = document.file_name;
            } else {
                for (int a = 0; a < document.attributes.size(); a++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(a);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                        fileName = documentAttribute.file_name;
                    }
                }
            }
        }
        String fileName2 = fixFileName(fileName);
        return fileName2 != null ? fileName2 : "";
    }

    public static String getMimeTypePart(String mime) {
        int lastIndexOf = mime.lastIndexOf(47);
        int index = lastIndexOf;
        if (lastIndexOf != -1) {
            return mime.substring(index + 1);
        }
        return "";
    }

    public static String getExtensionByMimeType(String mime) {
        if (mime == null) {
            return "";
        }
        char c = 65535;
        int hashCode = mime.hashCode();
        if (hashCode != 187091926) {
            if (hashCode != 1331848029) {
                if (hashCode == 2039520277 && mime.equals("video/x-matroska")) {
                    c = 1;
                }
            } else if (mime.equals(MimeTypes.VIDEO_MP4)) {
                c = 0;
            }
        } else if (mime.equals("audio/ogg")) {
            c = 2;
        }
        if (c == 0) {
            return ".mp4";
        }
        if (c == 1) {
            return ".mkv";
        }
        if (c != 2) {
            return "";
        }
        return ".ogg";
    }

    public static File getInternalCacheDir() {
        return ApplicationLoader.applicationContext.getCacheDir();
    }

    public static String getDocumentExtension(TLRPC.Document document) {
        String fileName = getDocumentFileName(document);
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = document.mime_type;
        }
        if (ext == null) {
            ext = "";
        }
        return ext.toUpperCase();
    }

    public static String getAttachFileName(TLObject attach) {
        return getAttachFileName(attach, (String) null);
    }

    public static String getAttachFileName(TLObject attach, String ext) {
        if (attach instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) attach;
            String docExt = null;
            if (0 == 0) {
                String docExt2 = getDocumentFileName(document);
                if (docExt2 != null) {
                    int lastIndexOf = docExt2.lastIndexOf(46);
                    int idx = lastIndexOf;
                    if (lastIndexOf != -1) {
                        docExt = docExt2.substring(idx);
                    }
                }
                docExt = "";
            }
            if (docExt.length() <= 1) {
                docExt = getExtensionByMimeType(document.mime_type);
            }
            if (docExt.length() > 1) {
                return document.dc_id + "_" + document.id + docExt;
            }
            return document.dc_id + "_" + document.id;
        } else if (attach instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) attach;
            return secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id + ".jpg";
        } else if (attach instanceof TLRPC.TL_secureFile) {
            TLRPC.TL_secureFile secureFile = (TLRPC.TL_secureFile) attach;
            return secureFile.dc_id + "_" + secureFile.id + ".jpg";
        } else if (attach instanceof WebFile) {
            WebFile document2 = (WebFile) attach;
            return Utilities.MD5(document2.url) + "." + ImageLoader.getHttpUrlExtension(document2.url, getMimeTypePart(document2.mime_type));
        } else {
            String str = "jpg";
            if (attach instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize photo = (TLRPC.PhotoSize) attach;
                if (photo.location == null || (photo.location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    return "";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(photo.location.volume_id);
                sb.append("_");
                sb.append(photo.location.local_id);
                sb.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb.append(str);
                return sb.toString();
            } else if (!(attach instanceof TLRPC.FileLocation) || (attach instanceof TLRPC.TL_fileLocationUnavailable)) {
                return "";
            } else {
                TLRPC.FileLocation location = (TLRPC.FileLocation) attach;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(location.volume_id);
                sb2.append("_");
                sb2.append(location.local_id);
                sb2.append(".");
                if (ext != null) {
                    str = ext;
                }
                sb2.append(str);
                return sb2.toString();
            }
        }
    }

    public void deleteFiles(ArrayList<File> files, int type) {
        if (files != null && !files.isEmpty()) {
            fileLoaderQueue.postRunnable(new Runnable(files, type) {
                private final /* synthetic */ ArrayList f$0;
                private final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoader.lambda$deleteFiles$10(this.f$0, this.f$1);
                }
            });
        }
    }

    static /* synthetic */ void lambda$deleteFiles$10(ArrayList files, int type) {
        for (int a = 0; a < files.size(); a++) {
            File file = (File) files.get(a);
            File encrypted = new File(file.getAbsolutePath() + ".enc");
            if (encrypted.exists()) {
                try {
                    if (!encrypted.delete()) {
                        encrypted.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    File internalCacheDir = getInternalCacheDir();
                    File key = new File(internalCacheDir, file.getName() + ".enc.key");
                    if (!key.delete()) {
                        key.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (file.exists()) {
                try {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            try {
                File parentFile = file.getParentFile();
                File qFile = new File(parentFile, "q_" + file.getName());
                if (qFile.exists() && !qFile.delete()) {
                    qFile.deleteOnExit();
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
        if (type == 2) {
            ImageLoader.getInstance().clearMemory();
        }
    }

    public static boolean isVideoMimeType(String mime) {
        return MimeTypes.VIDEO_MP4.equals(mime) || (SharedConfig.streamMkv && "video/x-matroska".equals(mime));
    }

    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        return copyFile(sourceFile, destFile, -1);
    }

    public static boolean copyFile(InputStream sourceFile, File destFile, int maxSize) throws IOException {
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int totalLen = 0;
        while (true) {
            int read = sourceFile.read(buf);
            int len = read;
            if (read <= 0) {
                break;
            }
            Thread.yield();
            out.write(buf, 0, len);
            totalLen += len;
            if (maxSize > 0 && totalLen >= maxSize) {
                break;
            }
        }
        out.getFD().sync();
        out.close();
        return true;
    }
}
