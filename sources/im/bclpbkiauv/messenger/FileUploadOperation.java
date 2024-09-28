package im.bclpbkiauv.messenger;

import android.content.SharedPreferences;
import android.util.SparseArray;
import android.util.SparseIntArray;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray<>();
    private int currentAccount;
    private long currentFileId;
    private int currentPartNum;
    private int currentType;
    private int currentUploadRequetsCount;
    private FileUploadOperationDelegate delegate;
    private int estimatedSize;
    private String fileKey;
    private int fingerprint;
    private ArrayList<byte[]> freeRequestIvs;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
    private int lastSavedPartNum;
    private int maxRequestsCount;
    private boolean nextPartFirst;
    private int operationGuid;
    private SharedPreferences preferences;
    private byte[] readBuffer;
    private long readBytesCount;
    private int requestNum;
    private SparseIntArray requestTokens = new SparseIntArray();
    private int saveInfoTimes;
    private boolean slowNetwork;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private int uploadChunkSize = 65536;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;

    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    private class UploadCachedResult {
        /* access modifiers changed from: private */
        public long bytesOffset;
        /* access modifiers changed from: private */
        public byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int instance, String location, boolean encrypted, int estimated, int type) {
        this.currentAccount = instance;
        this.uploadingFilePath = location;
        this.isEncrypted = encrypted;
        this.estimatedSize = estimated;
        this.currentType = type;
        this.uploadFirstPartLater = estimated != 0 && !encrypted;
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public void setDelegate(FileUploadOperationDelegate fileUploadOperationDelegate) {
        this.delegate = fileUploadOperationDelegate;
    }

    public void start() {
        if (this.state == 0) {
            this.state = 1;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    FileUploadOperation.this.lambda$start$0$FileUploadOperation();
                }
            });
        }
    }

    public /* synthetic */ void lambda$start$0$FileUploadOperation() {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload on slow network = " + this.slowNetwork);
        }
        int count = this.slowNetwork ? 1 : 8;
        for (int a = 0; a < count; a++) {
            startUploadRequest();
        }
    }

    /* access modifiers changed from: protected */
    public void onNetworkChanged(boolean slow) {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new Runnable(slow) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileUploadOperation.this.lambda$onNetworkChanged$1$FileUploadOperation(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNetworkChanged$1$FileUploadOperation(boolean slow) {
        int i;
        if (this.slowNetwork != slow) {
            this.slowNetwork = slow;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("network changed to slow = " + this.slowNetwork);
            }
            int a = 0;
            while (true) {
                i = 1;
                if (a >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
                a++;
            }
            this.requestTokens.clear();
            cleanup();
            this.isLastPart = false;
            this.nextPartFirst = false;
            this.requestNum = 0;
            this.currentPartNum = 0;
            this.readBytesCount = 0;
            this.uploadedBytesCount = 0;
            this.saveInfoTimes = 0;
            this.key = null;
            this.iv = null;
            this.ivChange = null;
            this.currentUploadRequetsCount = 0;
            this.lastSavedPartNum = 0;
            this.uploadFirstPartLater = false;
            this.cachedResults.clear();
            this.operationGuid++;
            if (!this.slowNetwork) {
                i = 8;
            }
            int count = i;
            for (int a2 = 0; a2 < count; a2++) {
                startUploadRequest();
            }
        }
    }

    public void cancel() {
        if (this.state != 3) {
            this.state = 2;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    FileUploadOperation.this.lambda$cancel$2$FileUploadOperation();
                }
            });
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        }
    }

    public /* synthetic */ void lambda$cancel$2$FileUploadOperation() {
        for (int a = 0; a < this.requestTokens.size(); a++) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
        }
    }

    private void cleanup() {
        if (this.preferences == null) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        }
        SharedPreferences.Editor edit = this.preferences.edit();
        SharedPreferences.Editor remove = edit.remove(this.fileKey + "_time");
        SharedPreferences.Editor remove2 = remove.remove(this.fileKey + "_size");
        SharedPreferences.Editor remove3 = remove2.remove(this.fileKey + "_uploaded");
        SharedPreferences.Editor remove4 = remove3.remove(this.fileKey + "_id");
        SharedPreferences.Editor remove5 = remove4.remove(this.fileKey + "_iv");
        SharedPreferences.Editor remove6 = remove5.remove(this.fileKey + "_key");
        remove6.remove(this.fileKey + "_ivc").commit();
        try {
            if (this.stream != null) {
                this.stream.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void checkNewDataAvailable(long newAvailableSize, long finalSize) {
        Utilities.stageQueue.postRunnable(new Runnable(finalSize, newAvailableSize) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                FileUploadOperation.this.lambda$checkNewDataAvailable$3$FileUploadOperation(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$checkNewDataAvailable$3$FileUploadOperation(long finalSize, long newAvailableSize) {
        if (!(this.estimatedSize == 0 || finalSize == 0)) {
            this.estimatedSize = 0;
            this.totalFileSize = finalSize;
            calcTotalPartsCount();
            if (!this.uploadFirstPartLater && this.started) {
                storeFileUploadInfo();
            }
        }
        this.availableSize = finalSize > 0 ? finalSize : newAvailableSize;
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }

    private void storeFileUploadInfo() {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(this.fileKey + "_time", this.uploadStartTime);
        editor.putLong(this.fileKey + "_size", this.totalFileSize);
        editor.putLong(this.fileKey + "_id", this.currentFileId);
        editor.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            editor.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            editor.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        editor.commit();
    }

    private void calcTotalPartsCount() {
        if (!this.uploadFirstPartLater) {
            long j = this.totalFileSize;
            int i = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((j + ((long) i)) - 1)) / i;
        } else if (this.isBigFile) {
            long j2 = this.totalFileSize;
            int i2 = this.uploadChunkSize;
            this.totalPartsCount = (((int) (((j2 - ((long) i2)) + ((long) i2)) - 1)) / i2) + 1;
        } else {
            int i3 = this.uploadChunkSize;
            this.totalPartsCount = (((int) (((this.totalFileSize - 1024) + ((long) i3)) - 1)) / i3) + 1;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveBigFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startUploadRequest() {
        /*
            r31 = this;
            r12 = r31
            int r0 = r12.state
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return
        L_0x0008:
            r12.started = r1     // Catch:{ Exception -> 0x04ce }
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            r3 = 1024(0x400, float:1.435E-42)
            r4 = 0
            r6 = 32
            r7 = 0
            if (r0 != 0) goto L_0x0358
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x04ce }
            java.lang.String r8 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04ce }
            r0.<init>(r8)     // Catch:{ Exception -> 0x04ce }
            r8 = r0
            android.net.Uri r0 = android.net.Uri.fromFile(r8)     // Catch:{ Exception -> 0x04ce }
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r0)     // Catch:{ Exception -> 0x04ce }
            if (r0 != 0) goto L_0x034f
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04ce }
            java.lang.String r9 = "r"
            r0.<init>(r8, r9)     // Catch:{ Exception -> 0x04ce }
            r12.stream = r0     // Catch:{ Exception -> 0x04ce }
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x003a
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            long r9 = (long) r0     // Catch:{ Exception -> 0x04ce }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04ce }
            goto L_0x0040
        L_0x003a:
            long r9 = r8.length()     // Catch:{ Exception -> 0x04ce }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04ce }
        L_0x0040:
            long r9 = r12.totalFileSize     // Catch:{ Exception -> 0x04ce }
            r13 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x004b
            r12.isBigFile = r1     // Catch:{ Exception -> 0x04ce }
        L_0x004b:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0052
            r9 = 32
            goto L_0x0054
        L_0x0052:
            r9 = 128(0x80, double:6.32E-322)
        L_0x0054:
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04ce }
            r15 = 3072000(0x2ee000, double:1.5177697E-317)
            long r13 = r13 + r15
            r17 = 1
            long r13 = r13 - r17
            long r13 = r13 / r15
            long r9 = java.lang.Math.max(r9, r13)     // Catch:{ Exception -> 0x04ce }
            int r0 = (int) r9     // Catch:{ Exception -> 0x04ce }
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ce }
            int r0 = r3 % r0
            if (r0 == 0) goto L_0x0075
            r0 = 64
        L_0x006c:
            int r9 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            if (r9 <= r0) goto L_0x0073
            int r0 = r0 * 2
            goto L_0x006c
        L_0x0073:
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ce }
        L_0x0075:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x007c
            r0 = 32
            goto L_0x007e
        L_0x007c:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x007e:
            int r9 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            int r0 = r0 / r9
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x04ce }
            r12.maxRequestsCount = r0     // Catch:{ Exception -> 0x04ce }
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x00a3
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ce }
            int r9 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04ce }
            r0.<init>(r9)     // Catch:{ Exception -> 0x04ce }
            r12.freeRequestIvs = r0     // Catch:{ Exception -> 0x04ce }
            r0 = 0
        L_0x0095:
            int r9 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04ce }
            if (r0 >= r9) goto L_0x00a3
            java.util.ArrayList<byte[]> r9 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04ce }
            byte[] r10 = new byte[r6]     // Catch:{ Exception -> 0x04ce }
            r9.add(r10)     // Catch:{ Exception -> 0x04ce }
            int r0 = r0 + 1
            goto L_0x0095
        L_0x00a3:
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            int r0 = r0 * 1024
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ce }
            r31.calcTotalPartsCount()     // Catch:{ Exception -> 0x04ce }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x04ce }
            r12.readBuffer = r0     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r0.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r9 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04ce }
            r0.append(r9)     // Catch:{ Exception -> 0x04ce }
            boolean r9 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r9 == 0) goto L_0x00c3
            java.lang.String r9 = "enc"
            goto L_0x00c5
        L_0x00c3:
            java.lang.String r9 = ""
        L_0x00c5:
            r0.append(r9)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r0 = im.bclpbkiauv.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x04ce }
            r12.fileKey = r0     // Catch:{ Exception -> 0x04ce }
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r9.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r10 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r9.append(r10)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r10 = "_size"
            r9.append(r10)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x04ce }
            long r9 = r0.getLong(r9, r4)     // Catch:{ Exception -> 0x04ce }
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x04ce }
            r15 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 / r15
            int r0 = (int) r13     // Catch:{ Exception -> 0x04ce }
            r12.uploadStartTime = r0     // Catch:{ Exception -> 0x04ce }
            r0 = 0
            boolean r11 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04ce }
            if (r11 != 0) goto L_0x02a3
            boolean r11 = r12.nextPartFirst     // Catch:{ Exception -> 0x04ce }
            if (r11 != 0) goto L_0x02a3
            int r11 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r11 != 0) goto L_0x02a3
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04ce }
            int r11 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r11 != 0) goto L_0x02a3
            android.content.SharedPreferences r11 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r13.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r14 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r13.append(r14)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r14 = "_id"
            r13.append(r14)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x04ce }
            long r13 = r11.getLong(r13, r4)     // Catch:{ Exception -> 0x04ce }
            r12.currentFileId = r13     // Catch:{ Exception -> 0x04ce }
            android.content.SharedPreferences r11 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r13.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r14 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r13.append(r14)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r14 = "_time"
            r13.append(r14)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x04ce }
            int r11 = r11.getInt(r13, r7)     // Catch:{ Exception -> 0x04ce }
            android.content.SharedPreferences r13 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r14.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r15 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r14.append(r15)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r15 = "_uploaded"
            r14.append(r15)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x04ce }
            long r13 = r13.getLong(r14, r4)     // Catch:{ Exception -> 0x04ce }
            boolean r15 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            r3 = 0
            if (r15 == 0) goto L_0x01b5
            android.content.SharedPreferences r15 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r2.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r2.append(r1)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = "_iv"
            r2.append(r1)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = r15.getString(r1, r3)     // Catch:{ Exception -> 0x04ce }
            android.content.SharedPreferences r2 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r15.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r4 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r15.append(r4)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r4 = "_key"
            r15.append(r4)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r4 = r15.toString()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r2 = r2.getString(r4, r3)     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x01b4
            if (r2 == 0) goto L_0x01b4
            byte[] r4 = im.bclpbkiauv.messenger.Utilities.hexToBytes(r2)     // Catch:{ Exception -> 0x04ce }
            r12.key = r4     // Catch:{ Exception -> 0x04ce }
            byte[] r4 = im.bclpbkiauv.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x04ce }
            r12.iv = r4     // Catch:{ Exception -> 0x04ce }
            byte[] r5 = r12.key     // Catch:{ Exception -> 0x04ce }
            if (r5 == 0) goto L_0x01b2
            if (r4 == 0) goto L_0x01b2
            byte[] r5 = r12.key     // Catch:{ Exception -> 0x04ce }
            int r5 = r5.length     // Catch:{ Exception -> 0x04ce }
            if (r5 != r6) goto L_0x01b2
            int r5 = r4.length     // Catch:{ Exception -> 0x04ce }
            if (r5 != r6) goto L_0x01b2
            byte[] r5 = new byte[r6]     // Catch:{ Exception -> 0x04ce }
            r12.ivChange = r5     // Catch:{ Exception -> 0x04ce }
            java.lang.System.arraycopy(r4, r7, r5, r7, r6)     // Catch:{ Exception -> 0x04ce }
            goto L_0x01b5
        L_0x01b2:
            r0 = 1
            goto L_0x01b5
        L_0x01b4:
            r0 = 1
        L_0x01b5:
            if (r0 != 0) goto L_0x02a0
            if (r11 == 0) goto L_0x02a0
            boolean r1 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x01c7
            int r1 = r12.uploadStartTime     // Catch:{ Exception -> 0x04ce }
            r2 = 86400(0x15180, float:1.21072E-40)
            int r1 = r1 - r2
            if (r11 >= r1) goto L_0x01c7
            r11 = 0
            goto L_0x01d8
        L_0x01c7:
            boolean r1 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r1 != 0) goto L_0x01d8
            float r1 = (float) r11     // Catch:{ Exception -> 0x04ce }
            int r2 = r12.uploadStartTime     // Catch:{ Exception -> 0x04ce }
            float r2 = (float) r2     // Catch:{ Exception -> 0x04ce }
            r4 = 1168687104(0x45a8c000, float:5400.0)
            float r2 = r2 - r4
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 >= 0) goto L_0x01d8
            r11 = 0
        L_0x01d8:
            if (r11 == 0) goto L_0x02a1
            r1 = 0
            int r4 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x029e
            r12.readBytesCount = r13     // Catch:{ Exception -> 0x04ce }
            int r1 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            long r1 = (long) r1     // Catch:{ Exception -> 0x04ce }
            long r1 = r13 / r1
            int r2 = (int) r1     // Catch:{ Exception -> 0x04ce }
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04ce }
            boolean r1 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r1 != 0) goto L_0x025b
            r1 = 0
        L_0x01ef:
            long r2 = (long) r1     // Catch:{ Exception -> 0x04ce }
            long r4 = r12.readBytesCount     // Catch:{ Exception -> 0x04ce }
            int r15 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            long r6 = (long) r15     // Catch:{ Exception -> 0x04ce }
            long r4 = r4 / r6
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x025a
            java.io.RandomAccessFile r2 = r12.stream     // Catch:{ Exception -> 0x04ce }
            byte[] r3 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            int r2 = r2.read(r3)     // Catch:{ Exception -> 0x04ce }
            r3 = 0
            boolean r4 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r4 == 0) goto L_0x0210
            int r4 = r2 % 16
            if (r4 == 0) goto L_0x0210
            int r4 = r2 % 16
            int r4 = 16 - r4
            int r3 = r3 + r4
        L_0x0210:
            im.bclpbkiauv.tgnet.NativeByteBuffer r4 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04ce }
            int r5 = r2 + r3
            r4.<init>((int) r5)     // Catch:{ Exception -> 0x04ce }
            int r5 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            if (r2 != r5) goto L_0x0223
            int r5 = r12.totalPartsCount     // Catch:{ Exception -> 0x04ce }
            int r6 = r12.currentPartNum     // Catch:{ Exception -> 0x04ce }
            r7 = 1
            int r6 = r6 + r7
            if (r5 != r6) goto L_0x0226
        L_0x0223:
            r5 = 1
            r12.isLastPart = r5     // Catch:{ Exception -> 0x04ce }
        L_0x0226:
            byte[] r5 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            r6 = 0
            r4.writeBytes(r5, r6, r2)     // Catch:{ Exception -> 0x04ce }
            boolean r5 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r5 == 0) goto L_0x0251
            r5 = 0
        L_0x0231:
            if (r5 >= r3) goto L_0x023a
            r6 = 0
            r4.writeByte((int) r6)     // Catch:{ Exception -> 0x04ce }
            int r5 = r5 + 1
            goto L_0x0231
        L_0x023a:
            java.nio.ByteBuffer r5 = r4.buffer     // Catch:{ Exception -> 0x04ce }
            byte[] r6 = r12.key     // Catch:{ Exception -> 0x04ce }
            byte[] r7 = r12.ivChange     // Catch:{ Exception -> 0x04ce }
            r25 = 1
            r26 = 1
            r27 = 0
            int r28 = r2 + r3
            r22 = r5
            r23 = r6
            r24 = r7
            im.bclpbkiauv.messenger.Utilities.aesIgeEncryption(r22, r23, r24, r25, r26, r27, r28)     // Catch:{ Exception -> 0x04ce }
        L_0x0251:
            r4.reuse()     // Catch:{ Exception -> 0x04ce }
            int r1 = r1 + 1
            r6 = 32
            r7 = 0
            goto L_0x01ef
        L_0x025a:
            goto L_0x02a1
        L_0x025b:
            java.io.RandomAccessFile r1 = r12.stream     // Catch:{ Exception -> 0x04ce }
            r1.seek(r13)     // Catch:{ Exception -> 0x04ce }
            boolean r1 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x02a1
            android.content.SharedPreferences r1 = r12.preferences     // Catch:{ Exception -> 0x04ce }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ce }
            r2.<init>()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r4 = r12.fileKey     // Catch:{ Exception -> 0x04ce }
            r2.append(r4)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r4 = "_ivc"
            r2.append(r4)     // Catch:{ Exception -> 0x04ce }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = r1.getString(r2, r3)     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x0295
            byte[] r2 = im.bclpbkiauv.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x04ce }
            r12.ivChange = r2     // Catch:{ Exception -> 0x04ce }
            if (r2 == 0) goto L_0x028c
            int r2 = r2.length     // Catch:{ Exception -> 0x04ce }
            r3 = 32
            if (r2 == r3) goto L_0x029d
        L_0x028c:
            r0 = 1
            r2 = 0
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04ce }
            r2 = 0
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04ce }
            goto L_0x029d
        L_0x0295:
            r0 = 1
            r2 = 0
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04ce }
            r2 = 0
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04ce }
        L_0x029d:
            goto L_0x02a1
        L_0x029e:
            r0 = 1
            goto L_0x02a1
        L_0x02a0:
            r0 = 1
        L_0x02a1:
            r1 = r0
            goto L_0x02a5
        L_0x02a3:
            r0 = 1
            r1 = r0
        L_0x02a5:
            if (r1 == 0) goto L_0x02e8
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x02d1
            r2 = 32
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x04ce }
            r12.iv = r0     // Catch:{ Exception -> 0x04ce }
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x04ce }
            r12.key = r0     // Catch:{ Exception -> 0x04ce }
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x04ce }
            r12.ivChange = r0     // Catch:{ Exception -> 0x04ce }
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x04ce }
            byte[] r2 = r12.iv     // Catch:{ Exception -> 0x04ce }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04ce }
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x04ce }
            byte[] r2 = r12.key     // Catch:{ Exception -> 0x04ce }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04ce }
            byte[] r0 = r12.iv     // Catch:{ Exception -> 0x04ce }
            byte[] r2 = r12.ivChange     // Catch:{ Exception -> 0x04ce }
            r3 = 32
            r4 = 0
            java.lang.System.arraycopy(r0, r4, r2, r4, r3)     // Catch:{ Exception -> 0x04ce }
        L_0x02d1:
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x04ce }
            long r2 = r0.nextLong()     // Catch:{ Exception -> 0x04ce }
            r12.currentFileId = r2     // Catch:{ Exception -> 0x04ce }
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04ce }
            if (r0 != 0) goto L_0x02e8
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04ce }
            if (r0 != 0) goto L_0x02e8
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r0 != 0) goto L_0x02e8
            r31.storeFileUploadInfo()     // Catch:{ Exception -> 0x04ce }
        L_0x02e8:
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0324
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x0320 }
            r2 = 64
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0320 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x0320 }
            r4 = 32
            r5 = 0
            java.lang.System.arraycopy(r3, r5, r2, r5, r4)     // Catch:{ Exception -> 0x0320 }
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x0320 }
            java.lang.System.arraycopy(r3, r5, r2, r4, r4)     // Catch:{ Exception -> 0x0320 }
            byte[] r3 = r0.digest(r2)     // Catch:{ Exception -> 0x0320 }
            r4 = 0
        L_0x0308:
            r5 = 4
            if (r4 >= r5) goto L_0x031f
            int r5 = r12.fingerprint     // Catch:{ Exception -> 0x0320 }
            byte r6 = r3[r4]     // Catch:{ Exception -> 0x0320 }
            int r7 = r4 + 4
            byte r7 = r3[r7]     // Catch:{ Exception -> 0x0320 }
            r6 = r6 ^ r7
            r6 = r6 & 255(0xff, float:3.57E-43)
            int r7 = r4 * 8
            int r6 = r6 << r7
            r5 = r5 | r6
            r12.fingerprint = r5     // Catch:{ Exception -> 0x0320 }
            int r4 = r4 + 1
            goto L_0x0308
        L_0x031f:
            goto L_0x0324
        L_0x0320:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04ce }
        L_0x0324:
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04ce }
            r12.uploadedBytesCount = r2     // Catch:{ Exception -> 0x04ce }
            int r0 = r12.currentPartNum     // Catch:{ Exception -> 0x04ce }
            r12.lastSavedPartNum = r0     // Catch:{ Exception -> 0x04ce }
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0358
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0342
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            int r2 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            long r2 = (long) r2     // Catch:{ Exception -> 0x04ce }
            r0.seek(r2)     // Catch:{ Exception -> 0x04ce }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            long r2 = (long) r0     // Catch:{ Exception -> 0x04ce }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04ce }
            goto L_0x034b
        L_0x0342:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            r2 = 1024(0x400, double:5.06E-321)
            r0.seek(r2)     // Catch:{ Exception -> 0x04ce }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04ce }
        L_0x034b:
            r2 = 1
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04ce }
            goto L_0x0358
        L_0x034f:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04ce }
            java.lang.String r1 = "trying to upload internal file"
            r0.<init>(r1)     // Catch:{ Exception -> 0x04ce }
            throw r0     // Catch:{ Exception -> 0x04ce }
        L_0x0358:
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0369
            long r0 = r12.readBytesCount     // Catch:{ Exception -> 0x04ce }
            int r2 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            long r2 = (long) r2     // Catch:{ Exception -> 0x04ce }
            long r0 = r0 + r2
            long r2 = r12.availableSize     // Catch:{ Exception -> 0x04ce }
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0369
            return
        L_0x0369:
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0390
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x04ce }
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r0 == 0) goto L_0x0382
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            byte[] r1 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            int r0 = r0.read(r1)     // Catch:{ Exception -> 0x04ce }
            r3 = 0
            goto L_0x038d
        L_0x0382:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            byte[] r1 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            int r0 = r0.read(r1, r3, r2)     // Catch:{ Exception -> 0x04ce }
        L_0x038d:
            r12.currentPartNum = r3     // Catch:{ Exception -> 0x04ce }
            goto L_0x0398
        L_0x0390:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04ce }
            byte[] r1 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            int r0 = r0.read(r1)     // Catch:{ Exception -> 0x04ce }
        L_0x0398:
            r1 = -1
            if (r0 != r1) goto L_0x039c
            return
        L_0x039c:
            r2 = 0
            boolean r3 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r3 == 0) goto L_0x03aa
            int r3 = r0 % 16
            if (r3 == 0) goto L_0x03aa
            int r3 = r0 % 16
            int r3 = 16 - r3
            int r2 = r2 + r3
        L_0x03aa:
            im.bclpbkiauv.tgnet.NativeByteBuffer r3 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04ce }
            int r4 = r0 + r2
            r3.<init>((int) r4)     // Catch:{ Exception -> 0x04ce }
            boolean r4 = r12.nextPartFirst     // Catch:{ Exception -> 0x04ce }
            if (r4 != 0) goto L_0x03c5
            int r4 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04ce }
            if (r0 != r4) goto L_0x03c5
            int r4 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r4 != 0) goto L_0x03d3
            int r4 = r12.totalPartsCount     // Catch:{ Exception -> 0x04ce }
            int r5 = r12.currentPartNum     // Catch:{ Exception -> 0x04ce }
            r6 = 1
            int r5 = r5 + r6
            if (r4 != r5) goto L_0x03d3
        L_0x03c5:
            boolean r4 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04ce }
            if (r4 == 0) goto L_0x03d0
            r4 = 1
            r12.nextPartFirst = r4     // Catch:{ Exception -> 0x04ce }
            r4 = 0
            r12.uploadFirstPartLater = r4     // Catch:{ Exception -> 0x04ce }
            goto L_0x03d3
        L_0x03d0:
            r4 = 1
            r12.isLastPart = r4     // Catch:{ Exception -> 0x04ce }
        L_0x03d3:
            byte[] r4 = r12.readBuffer     // Catch:{ Exception -> 0x04ce }
            r5 = 0
            r3.writeBytes(r4, r5, r0)     // Catch:{ Exception -> 0x04ce }
            boolean r4 = r12.isEncrypted     // Catch:{ Exception -> 0x04ce }
            if (r4 == 0) goto L_0x040c
            r4 = 0
        L_0x03de:
            if (r4 >= r2) goto L_0x03e7
            r5 = 0
            r3.writeByte((int) r5)     // Catch:{ Exception -> 0x04ce }
            int r4 = r4 + 1
            goto L_0x03de
        L_0x03e7:
            java.nio.ByteBuffer r5 = r3.buffer     // Catch:{ Exception -> 0x04ce }
            byte[] r6 = r12.key     // Catch:{ Exception -> 0x04ce }
            byte[] r7 = r12.ivChange     // Catch:{ Exception -> 0x04ce }
            r8 = 1
            r9 = 1
            r10 = 0
            int r11 = r0 + r2
            im.bclpbkiauv.messenger.Utilities.aesIgeEncryption(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x04ce }
            java.util.ArrayList<byte[]> r4 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04ce }
            r5 = 0
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x04ce }
            byte[] r4 = (byte[]) r4     // Catch:{ Exception -> 0x04ce }
            byte[] r6 = r12.ivChange     // Catch:{ Exception -> 0x04ce }
            r7 = 32
            java.lang.System.arraycopy(r6, r5, r4, r5, r7)     // Catch:{ Exception -> 0x04ce }
            java.util.ArrayList<byte[]> r6 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04ce }
            r6.remove(r5)     // Catch:{ Exception -> 0x04ce }
            r13 = r4
            goto L_0x040e
        L_0x040c:
            r4 = 0
            r13 = r4
        L_0x040e:
            boolean r4 = r12.isBigFile     // Catch:{ Exception -> 0x04ce }
            if (r4 == 0) goto L_0x0431
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveBigFilePart r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x04ce }
            r4.<init>()     // Catch:{ Exception -> 0x04ce }
            int r5 = r12.currentPartNum     // Catch:{ Exception -> 0x04ce }
            r6 = r5
            r4.file_part = r5     // Catch:{ Exception -> 0x04ce }
            long r7 = r12.currentFileId     // Catch:{ Exception -> 0x04ce }
            r4.file_id = r7     // Catch:{ Exception -> 0x04ce }
            int r5 = r12.estimatedSize     // Catch:{ Exception -> 0x04ce }
            if (r5 == 0) goto L_0x0427
            r4.file_total_parts = r1     // Catch:{ Exception -> 0x04ce }
            goto L_0x042b
        L_0x0427:
            int r1 = r12.totalPartsCount     // Catch:{ Exception -> 0x04ce }
            r4.file_total_parts = r1     // Catch:{ Exception -> 0x04ce }
        L_0x042b:
            r4.bytes = r3     // Catch:{ Exception -> 0x04ce }
            r1 = r4
            r14 = r1
            r15 = r6
            goto L_0x0445
        L_0x0431:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveFilePart r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x04ce }
            r1.<init>()     // Catch:{ Exception -> 0x04ce }
            int r4 = r12.currentPartNum     // Catch:{ Exception -> 0x04ce }
            r6 = r4
            r1.file_part = r4     // Catch:{ Exception -> 0x04ce }
            long r4 = r12.currentFileId     // Catch:{ Exception -> 0x04ce }
            r1.file_id = r4     // Catch:{ Exception -> 0x04ce }
            r1.bytes = r3     // Catch:{ Exception -> 0x04ce }
            r4 = r1
            r1 = r4
            r14 = r1
            r15 = r6
        L_0x0445:
            boolean r1 = r12.isLastPart     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x045d
            boolean r1 = r12.nextPartFirst     // Catch:{ Exception -> 0x04ce }
            if (r1 == 0) goto L_0x045d
            r1 = 0
            r12.nextPartFirst = r1     // Catch:{ Exception -> 0x04ce }
            int r1 = r12.totalPartsCount     // Catch:{ Exception -> 0x04ce }
            r4 = 1
            int r1 = r1 - r4
            r12.currentPartNum = r1     // Catch:{ Exception -> 0x04ce }
            java.io.RandomAccessFile r1 = r12.stream     // Catch:{ Exception -> 0x04ce }
            long r4 = r12.totalFileSize     // Catch:{ Exception -> 0x04ce }
            r1.seek(r4)     // Catch:{ Exception -> 0x04ce }
        L_0x045d:
            long r4 = r12.readBytesCount     // Catch:{ Exception -> 0x04ce }
            long r6 = (long) r0     // Catch:{ Exception -> 0x04ce }
            long r4 = r4 + r6
            r12.readBytesCount = r4     // Catch:{ Exception -> 0x04ce }
            int r1 = r12.currentPartNum
            r2 = 1
            int r1 = r1 + r2
            r12.currentPartNum = r1
            int r1 = r12.currentUploadRequetsCount
            int r1 = r1 + r2
            r12.currentUploadRequetsCount = r1
            int r1 = r12.requestNum
            int r2 = r1 + 1
            r12.requestNum = r2
            r11 = r1
            int r1 = r15 + r0
            long r9 = (long) r1
            int r1 = r14.getObjectSize()
            r2 = 4
            int r16 = r1 + 4
            int r8 = r12.operationGuid
            boolean r1 = r12.slowNetwork
            if (r1 == 0) goto L_0x048a
            r1 = 4
            r17 = r1
            goto L_0x0492
        L_0x048a:
            int r1 = r11 % 4
            int r1 = r1 << 16
            r2 = 4
            r1 = r1 | r2
            r17 = r1
        L_0x0492:
            int r1 = r12.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r22 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r1)
            im.bclpbkiauv.messenger.-$$Lambda$FileUploadOperation$Lgpy89ox3Mk2T-EB2qq11I2BYFI r24 = new im.bclpbkiauv.messenger.-$$Lambda$FileUploadOperation$Lgpy89ox3Mk2T-EB2qq11I2BYFI
            r1 = r24
            r2 = r31
            r3 = r8
            r4 = r16
            r5 = r13
            r6 = r11
            r7 = r0
            r18 = r8
            r8 = r15
            r19 = r9
            r21 = r0
            r0 = r11
            r11 = r14
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r11)
            r25 = 0
            im.bclpbkiauv.messenger.-$$Lambda$FileUploadOperation$YPupy4jGsBePKJi2hz8ojKRhHX0 r1 = new im.bclpbkiauv.messenger.-$$Lambda$FileUploadOperation$YPupy4jGsBePKJi2hz8ojKRhHX0
            r1.<init>()
            r27 = 10
            r28 = 2147483647(0x7fffffff, float:NaN)
            r30 = 1
            r23 = r14
            r26 = r1
            r29 = r17
            int r1 = r22.sendRequest(r23, r24, r25, r26, r27, r28, r29, r30)
            android.util.SparseIntArray r2 = r12.requestTokens
            r2.put(r0, r1)
            return
        L_0x04ce:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 4
            r12.state = r1
            im.bclpbkiauv.messenger.FileUploadOperation$FileUploadOperationDelegate r1 = r12.delegate
            r1.didFailedUploadingFile(r12)
            r31.cleanup()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileUploadOperation.startUploadRequest():void");
    }

    public /* synthetic */ void lambda$startUploadRequest$4$FileUploadOperation(int currentOperationGuid, int requestSize, byte[] currentRequestIv, int requestNumFinal, int currentRequestBytes, int currentRequestPartNum, long currentRequestBytesOffset, TLObject finalRequest, TLObject response, TLRPC.TL_error error) {
        long size;
        int i;
        TLRPC.InputEncryptedFile result;
        TLRPC.InputFile result2;
        int i2 = requestSize;
        byte[] bArr = currentRequestIv;
        int i3 = currentRequestPartNum;
        TLObject tLObject = response;
        if (currentOperationGuid == this.operationGuid) {
            int networkType = tLObject != null ? tLObject.networkType : ApplicationLoader.getCurrentNetworkType();
            int i4 = this.currentType;
            if (i4 == 50331648) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 3, (long) i2);
            } else if (i4 == 33554432) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 2, (long) i2);
            } else if (i4 == 16777216) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 4, (long) i2);
            } else if (i4 == 67108864) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 5, (long) i2);
            }
            if (bArr != null) {
                this.freeRequestIvs.add(bArr);
            }
            this.requestTokens.delete(requestNumFinal);
            if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
                long j = currentRequestBytesOffset;
                if (finalRequest != null) {
                    FileLog.e("23123");
                }
                this.state = 4;
                this.delegate.didFailedUploadingFile(this);
                cleanup();
            } else if (this.state == 1) {
                this.uploadedBytesCount += (long) currentRequestBytes;
                int i5 = this.estimatedSize;
                if (i5 != 0) {
                    size = Math.max(this.availableSize, (long) i5);
                } else {
                    size = this.totalFileSize;
                }
                this.delegate.didChangedUploadProgress(this, ((float) this.uploadedBytesCount) / ((float) size));
                int i6 = this.currentUploadRequetsCount - 1;
                this.currentUploadRequetsCount = i6;
                if (this.isLastPart && i6 == 0 && this.state == 1) {
                    this.state = 3;
                    if (this.key == null) {
                        if (this.isBigFile) {
                            result2 = new TLRPC.TL_inputFileBig();
                        } else {
                            TLRPC.InputFile tL_inputFile = new TLRPC.TL_inputFile();
                            tL_inputFile.md5_checksum = "";
                            result2 = tL_inputFile;
                        }
                        result2.parts = this.currentPartNum;
                        result2.id = this.currentFileId;
                        String str = this.uploadingFilePath;
                        result2.name = str.substring(str.lastIndexOf("/") + 1);
                        long j2 = size;
                        i = ConnectionsManager.FileTypeAudio;
                        this.delegate.didFinishUploadingFile(this, result2, (TLRPC.InputEncryptedFile) null, (byte[]) null, (byte[]) null);
                        cleanup();
                    } else {
                        i = ConnectionsManager.FileTypeAudio;
                        if (this.isBigFile) {
                            result = new TLRPC.TL_inputEncryptedFileBigUploaded();
                        } else {
                            TLRPC.InputEncryptedFile tL_inputEncryptedFileUploaded = new TLRPC.TL_inputEncryptedFileUploaded();
                            tL_inputEncryptedFileUploaded.md5_checksum = "";
                            result = tL_inputEncryptedFileUploaded;
                        }
                        result.parts = this.currentPartNum;
                        result.id = this.currentFileId;
                        result.key_fingerprint = this.fingerprint;
                        this.delegate.didFinishUploadingFile(this, (TLRPC.InputFile) null, result, this.key, this.iv);
                        cleanup();
                    }
                    int i7 = this.currentType;
                    if (i7 == i) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                        long j3 = currentRequestBytesOffset;
                    } else if (i7 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                        long j4 = currentRequestBytesOffset;
                    } else if (i7 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                        long j5 = currentRequestBytesOffset;
                    } else if (i7 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                        long j6 = currentRequestBytesOffset;
                    } else {
                        long j7 = currentRequestBytesOffset;
                    }
                } else {
                    if (this.currentUploadRequetsCount < this.maxRequestsCount) {
                        if (this.estimatedSize != 0 || this.uploadFirstPartLater || this.nextPartFirst) {
                            long j8 = currentRequestBytesOffset;
                        } else {
                            if (this.saveInfoTimes >= 4) {
                                this.saveInfoTimes = 0;
                            }
                            int i8 = this.lastSavedPartNum;
                            if (i3 == i8) {
                                this.lastSavedPartNum = i8 + 1;
                                long offsetToSave = currentRequestBytesOffset;
                                byte[] ivToSave = currentRequestIv;
                                while (true) {
                                    UploadCachedResult uploadCachedResult = this.cachedResults.get(this.lastSavedPartNum);
                                    UploadCachedResult result3 = uploadCachedResult;
                                    if (uploadCachedResult == null) {
                                        break;
                                    }
                                    offsetToSave = result3.bytesOffset;
                                    ivToSave = result3.iv;
                                    this.cachedResults.remove(this.lastSavedPartNum);
                                    this.lastSavedPartNum++;
                                }
                                if ((this.isBigFile && offsetToSave % 1048576 == 0) || (!this.isBigFile && this.saveInfoTimes == 0)) {
                                    SharedPreferences.Editor editor = this.preferences.edit();
                                    editor.putLong(this.fileKey + "_uploaded", offsetToSave);
                                    if (this.isEncrypted) {
                                        editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(ivToSave));
                                    }
                                    editor.commit();
                                }
                                long j9 = currentRequestBytesOffset;
                            } else {
                                UploadCachedResult result4 = new UploadCachedResult();
                                long unused = result4.bytesOffset = currentRequestBytesOffset;
                                if (bArr != null) {
                                    byte[] unused2 = result4.iv = new byte[32];
                                    System.arraycopy(bArr, 0, result4.iv, 0, 32);
                                }
                                this.cachedResults.put(i3, result4);
                            }
                            this.saveInfoTimes++;
                        }
                        startUploadRequest();
                        return;
                    }
                    long j10 = currentRequestBytesOffset;
                }
            }
        }
    }

    public /* synthetic */ void lambda$startUploadRequest$6$FileUploadOperation() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                FileUploadOperation.this.lambda$null$5$FileUploadOperation();
            }
        });
    }

    public /* synthetic */ void lambda$null$5$FileUploadOperation() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
