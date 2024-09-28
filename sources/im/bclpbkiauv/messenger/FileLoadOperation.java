package im.bclpbkiauv.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import im.bclpbkiauv.messenger.FileLoadOperation;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NativeByteBuffer;
import im.bclpbkiauv.tgnet.QuickAckDelegate;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.WriteToSocketDelegate;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import kotlin.UByte;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 12288;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 4;
    private static final int preloadMaxBytes = 2097152;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileGzipTemp;
    private File cacheFileParts;
    private File cacheFilePreload;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnDatacenterId;
    private SparseArray<TLRPC.TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private int foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    protected TLRPC.InputFileLocation location;
    private int moovFound;
    private int nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private int nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private Object parentObject;
    private volatile boolean paused;
    private boolean preloadFinished;
    private int preloadNotRequestedBytesCount;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer = new byte[16];
    private int preloadTempBufferCount;
    private SparseArray<PreloadRange> preloadedBytesRanges;
    private int priority;
    private RequestInfo priorityRequestInfo;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private SparseIntArray requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state = 0;
    private File storePath;
    private ArrayList<FileLoadOperationStream> streamListeners;
    private int streamPriorityStartOffset;
    private int streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private int totalBytesCount;
    private int totalPreloadedBytes;
    private boolean ungzip;
    private WebFile webFile;
    private TLRPC.InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    protected static class RequestInfo {
        /* access modifiers changed from: private */
        public int offset;
        /* access modifiers changed from: private */
        public int requestToken;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_file response;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_cdnFile responseCdn;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_webFile responseWeb;

        protected RequestInfo() {
        }
    }

    public static class Range {
        /* access modifiers changed from: private */
        public int end;
        /* access modifiers changed from: private */
        public int start;

        private Range(int s, int e) {
            this.start = s;
            this.end = e;
        }
    }

    private static class PreloadRange {
        /* access modifiers changed from: private */
        public int fileOffset;
        /* access modifiers changed from: private */
        public int length;
        private int start;

        private PreloadRange(int o, int s, int l) {
            this.fileOffset = o;
            this.start = s;
            this.length = l;
        }
    }

    public FileLoadOperation(ImageLocation imageLocation, Object parent, String extension, int size) {
        this.parentObject = parent;
        if (imageLocation.isEncrypted()) {
            TLRPC.TL_inputEncryptedFileLocation tL_inputEncryptedFileLocation = new TLRPC.TL_inputEncryptedFileLocation();
            this.location = tL_inputEncryptedFileLocation;
            tL_inputEncryptedFileLocation.id = imageLocation.location.volume_id;
            this.location.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.access_hash = imageLocation.access_hash;
            this.iv = new byte[32];
            byte[] bArr = imageLocation.iv;
            byte[] bArr2 = this.iv;
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            TLRPC.TL_inputPeerPhotoFileLocation tL_inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
            this.location = tL_inputPeerPhotoFileLocation;
            tL_inputPeerPhotoFileLocation.id = imageLocation.location.volume_id;
            this.location.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.big = imageLocation.photoPeerBig;
            this.location.peer = imageLocation.photoPeer;
        } else if (imageLocation.stickerSet != null) {
            TLRPC.TL_inputStickerSetThumb tL_inputStickerSetThumb = new TLRPC.TL_inputStickerSetThumb();
            this.location = tL_inputStickerSetThumb;
            tL_inputStickerSetThumb.id = imageLocation.location.volume_id;
            this.location.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.stickerset = imageLocation.stickerSet;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                TLRPC.TL_inputPhotoFileLocation tL_inputPhotoFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                this.location = tL_inputPhotoFileLocation;
                tL_inputPhotoFileLocation.id = imageLocation.photoId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
            } else {
                TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                this.location = tL_inputDocumentFileLocation;
                tL_inputDocumentFileLocation.id = imageLocation.documentId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
            }
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
        } else {
            TLRPC.TL_inputFileLocation tL_inputFileLocation = new TLRPC.TL_inputFileLocation();
            this.location = tL_inputFileLocation;
            tL_inputFileLocation.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.secret = imageLocation.access_hash;
            this.location.file_reference = imageLocation.file_reference;
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
            this.allowDisordererFileSave = true;
        }
        this.ungzip = imageLocation.lottieAnimation;
        int i = imageLocation.dc_id;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        this.currentType = 16777216;
        this.totalBytesCount = size;
        this.ext = extension != null ? extension : "jpg";
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        TLRPC.TL_inputSecureFileLocation tL_inputSecureFileLocation = new TLRPC.TL_inputSecureFileLocation();
        this.location = tL_inputSecureFileLocation;
        tL_inputSecureFileLocation.id = secureDocument.secureFile.id;
        this.location.access_hash = secureDocument.secureFile.access_hash;
        this.datacenterId = secureDocument.secureFile.dc_id;
        this.totalBytesCount = secureDocument.secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = ConnectionsManager.FileTypeFile;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int instance, WebFile webDocument) {
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = webDocument.size;
        int i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String defaultExt = FileLoader.getMimeTypePart(webDocument.mime_type);
        if (webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = ConnectionsManager.FileTypeAudio;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = ConnectionsManager.FileTypeVideo;
        } else {
            this.currentType = ConnectionsManager.FileTypeFile;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d4 A[Catch:{ Exception -> 0x00fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00d9 A[Catch:{ Exception -> 0x00fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f2 A[Catch:{ Exception -> 0x00fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(im.bclpbkiauv.tgnet.TLRPC.Document r8, java.lang.Object r9) {
        /*
            r7 = this;
            r7.<init>()
            r0 = 16
            byte[] r1 = new byte[r0]
            r7.preloadTempBuffer = r1
            r1 = 0
            r7.state = r1
            r2 = 1
            r7.parentObject = r9     // Catch:{ Exception -> 0x00fb }
            boolean r3 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEncrypted     // Catch:{ Exception -> 0x00fb }
            java.lang.String r4 = ""
            if (r3 == 0) goto L_0x0041
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFileLocation r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x00fb }
            r3.<init>()     // Catch:{ Exception -> 0x00fb }
            r7.location = r3     // Catch:{ Exception -> 0x00fb }
            long r5 = r8.id     // Catch:{ Exception -> 0x00fb }
            r3.id = r5     // Catch:{ Exception -> 0x00fb }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            long r5 = r8.access_hash     // Catch:{ Exception -> 0x00fb }
            r3.access_hash = r5     // Catch:{ Exception -> 0x00fb }
            int r3 = r8.dc_id     // Catch:{ Exception -> 0x00fb }
            r7.datacenterId = r3     // Catch:{ Exception -> 0x00fb }
            r7.initialDatacenterId = r3     // Catch:{ Exception -> 0x00fb }
            r3 = 32
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x00fb }
            r7.iv = r3     // Catch:{ Exception -> 0x00fb }
            byte[] r3 = r8.iv     // Catch:{ Exception -> 0x00fb }
            byte[] r5 = r7.iv     // Catch:{ Exception -> 0x00fb }
            byte[] r6 = r7.iv     // Catch:{ Exception -> 0x00fb }
            int r6 = r6.length     // Catch:{ Exception -> 0x00fb }
            java.lang.System.arraycopy(r3, r1, r5, r1, r6)     // Catch:{ Exception -> 0x00fb }
            byte[] r3 = r8.key     // Catch:{ Exception -> 0x00fb }
            r7.key = r3     // Catch:{ Exception -> 0x00fb }
            goto L_0x008d
        L_0x0041:
            boolean r3 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_document     // Catch:{ Exception -> 0x00fb }
            if (r3 == 0) goto L_0x008d
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocumentFileLocation r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x00fb }
            r3.<init>()     // Catch:{ Exception -> 0x00fb }
            r7.location = r3     // Catch:{ Exception -> 0x00fb }
            long r5 = r8.id     // Catch:{ Exception -> 0x00fb }
            r3.id = r5     // Catch:{ Exception -> 0x00fb }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            long r5 = r8.access_hash     // Catch:{ Exception -> 0x00fb }
            r3.access_hash = r5     // Catch:{ Exception -> 0x00fb }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            byte[] r5 = r8.file_reference     // Catch:{ Exception -> 0x00fb }
            r3.file_reference = r5     // Catch:{ Exception -> 0x00fb }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            r3.thumb_size = r4     // Catch:{ Exception -> 0x00fb }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x00fb }
            if (r3 != 0) goto L_0x006c
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x00fb }
            byte[] r5 = new byte[r1]     // Catch:{ Exception -> 0x00fb }
            r3.file_reference = r5     // Catch:{ Exception -> 0x00fb }
        L_0x006c:
            int r3 = r8.dc_id     // Catch:{ Exception -> 0x00fb }
            r7.datacenterId = r3     // Catch:{ Exception -> 0x00fb }
            r7.initialDatacenterId = r3     // Catch:{ Exception -> 0x00fb }
            r7.allowDisordererFileSave = r2     // Catch:{ Exception -> 0x00fb }
            r3 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r5 = r8.attributes     // Catch:{ Exception -> 0x00fb }
            int r5 = r5.size()     // Catch:{ Exception -> 0x00fb }
        L_0x007b:
            if (r3 >= r5) goto L_0x008d
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r6 = r8.attributes     // Catch:{ Exception -> 0x00fb }
            java.lang.Object r6 = r6.get(r3)     // Catch:{ Exception -> 0x00fb }
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x00fb }
            if (r6 == 0) goto L_0x008a
            r7.supportsPreloading = r2     // Catch:{ Exception -> 0x00fb }
            goto L_0x008d
        L_0x008a:
            int r3 = r3 + 1
            goto L_0x007b
        L_0x008d:
            java.lang.String r3 = "application/x-tgsticker"
            java.lang.String r5 = r8.mime_type     // Catch:{ Exception -> 0x00fb }
            boolean r3 = r3.equals(r5)     // Catch:{ Exception -> 0x00fb }
            r7.ungzip = r3     // Catch:{ Exception -> 0x00fb }
            int r3 = r8.size     // Catch:{ Exception -> 0x00fb }
            r7.totalBytesCount = r3     // Catch:{ Exception -> 0x00fb }
            byte[] r5 = r7.key     // Catch:{ Exception -> 0x00fb }
            if (r5 == 0) goto L_0x00ac
            r5 = 0
            int r6 = r3 % 16
            if (r6 == 0) goto L_0x00ac
            int r6 = r3 % 16
            int r0 = r0 - r6
            r7.bytesCountPadding = r0     // Catch:{ Exception -> 0x00fb }
            int r3 = r3 + r0
            r7.totalBytesCount = r3     // Catch:{ Exception -> 0x00fb }
        L_0x00ac:
            java.lang.String r0 = im.bclpbkiauv.messenger.FileLoader.getDocumentFileName(r8)     // Catch:{ Exception -> 0x00fb }
            r7.ext = r0     // Catch:{ Exception -> 0x00fb }
            if (r0 == 0) goto L_0x00c8
            r3 = 46
            int r0 = r0.lastIndexOf(r3)     // Catch:{ Exception -> 0x00fb }
            r3 = r0
            r5 = -1
            if (r0 != r5) goto L_0x00bf
            goto L_0x00c8
        L_0x00bf:
            java.lang.String r0 = r7.ext     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = r0.substring(r3)     // Catch:{ Exception -> 0x00fb }
            r7.ext = r0     // Catch:{ Exception -> 0x00fb }
            goto L_0x00ca
        L_0x00c8:
            r7.ext = r4     // Catch:{ Exception -> 0x00fb }
        L_0x00ca:
            java.lang.String r0 = "audio/ogg"
            java.lang.String r3 = r8.mime_type     // Catch:{ Exception -> 0x00fb }
            boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x00fb }
            if (r0 == 0) goto L_0x00d9
            r0 = 50331648(0x3000000, float:3.761582E-37)
            r7.currentType = r0     // Catch:{ Exception -> 0x00fb }
            goto L_0x00ea
        L_0x00d9:
            java.lang.String r0 = r8.mime_type     // Catch:{ Exception -> 0x00fb }
            boolean r0 = im.bclpbkiauv.messenger.FileLoader.isVideoMimeType(r0)     // Catch:{ Exception -> 0x00fb }
            if (r0 == 0) goto L_0x00e6
            r0 = 33554432(0x2000000, float:9.403955E-38)
            r7.currentType = r0     // Catch:{ Exception -> 0x00fb }
            goto L_0x00ea
        L_0x00e6:
            r0 = 67108864(0x4000000, float:1.5046328E-36)
            r7.currentType = r0     // Catch:{ Exception -> 0x00fb }
        L_0x00ea:
            java.lang.String r0 = r7.ext     // Catch:{ Exception -> 0x00fb }
            int r0 = r0.length()     // Catch:{ Exception -> 0x00fb }
            if (r0 > r2) goto L_0x00fa
            java.lang.String r0 = r8.mime_type     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = im.bclpbkiauv.messenger.FileLoader.getExtensionByMimeType(r0)     // Catch:{ Exception -> 0x00fb }
            r7.ext = r0     // Catch:{ Exception -> 0x00fb }
        L_0x00fa:
            goto L_0x0102
        L_0x00fb:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r7.onFail(r2, r1)
        L_0x0102:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileLoadOperation.<init>(im.bclpbkiauv.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    public void setEncryptFile(boolean value) {
        this.encryptFile = value;
        if (value) {
            this.allowDisordererFileSave = false;
        }
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPriority(int value) {
        this.priority = value;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPaths(int instance, File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
        this.currentAccount = instance;
    }

    public boolean wasStarted() {
        return this.started && !this.paused;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void removePart(ArrayList<Range> ranges, int start, int end) {
        if (ranges != null && end >= start) {
            int count = ranges.size();
            boolean modified = false;
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                Range range = ranges.get(a);
                if (start == range.end) {
                    int unused = range.end = end;
                    modified = true;
                    break;
                } else if (end == range.start) {
                    int unused2 = range.start = start;
                    modified = true;
                    break;
                } else {
                    a++;
                }
            }
            Collections.sort(ranges, $$Lambda$FileLoadOperation$A_5CRILBocvIDzkFC9UGTNJ5loU.INSTANCE);
            int a2 = 0;
            while (a2 < ranges.size() - 1) {
                Range r1 = ranges.get(a2);
                Range r2 = ranges.get(a2 + 1);
                if (r1.end == r2.start) {
                    int unused3 = r1.end = r2.end;
                    ranges.remove(a2 + 1);
                    a2--;
                }
                a2++;
            }
            if (!modified) {
                ranges.add(new Range(start, end));
            }
        }
    }

    static /* synthetic */ int lambda$removePart$0(Range o1, Range o2) {
        if (o1.start > o2.start) {
            return 1;
        }
        if (o1.start < o2.start) {
            return -1;
        }
        return 0;
    }

    private void addPart(ArrayList<Range> ranges, int start, int end, boolean save) {
        if (ranges != null && end >= start) {
            boolean modified = false;
            int count = ranges.size();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                Range range = ranges.get(a);
                if (start <= range.start) {
                    if (end >= range.end) {
                        ranges.remove(a);
                        modified = true;
                        break;
                    } else if (end > range.start) {
                        int unused = range.start = end;
                        modified = true;
                        break;
                    }
                } else if (end < range.end) {
                    ranges.add(0, new Range(range.start, start));
                    modified = true;
                    int unused2 = range.start = end;
                    break;
                } else if (start < range.end) {
                    int unused3 = range.end = start;
                    modified = true;
                    break;
                }
                a++;
            }
            if (!save) {
                return;
            }
            if (modified) {
                try {
                    this.filePartsStream.seek(0);
                    int count2 = ranges.size();
                    this.filePartsStream.writeInt(count2);
                    for (int a2 = 0; a2 < count2; a2++) {
                        Range range2 = ranges.get(a2);
                        this.filePartsStream.writeInt(range2.start);
                        this.filePartsStream.writeInt(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
                if (arrayList != null) {
                    int count3 = arrayList.size();
                    for (int a3 = 0; a3 < count3; a3++) {
                        this.streamListeners.get(a3).newDataAvailable();
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + start + " - " + end);
            }
        }
    }

    /* access modifiers changed from: protected */
    public File getCurrentFile() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        File[] result = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable(result, countDownLatch) {
            private final /* synthetic */ File[] f$1;
            private final /* synthetic */ CountDownLatch f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FileLoadOperation.this.lambda$getCurrentFile$1$FileLoadOperation(this.f$1, this.f$2);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return result[0];
    }

    public /* synthetic */ void lambda$getCurrentFile$1$FileLoadOperation(File[] result, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            result[0] = this.cacheFileFinal;
        } else {
            result[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, int offset, int length) {
        if (ranges == null || this.state == 3 || ranges.isEmpty()) {
            int count = this.downloadedBytes;
            if (count == 0) {
                return length;
            }
            return Math.min(length, Math.max(count - offset, 0));
        }
        int count2 = ranges.size();
        Range minRange = null;
        int availableLength = length;
        int a = 0;
        while (true) {
            if (a >= count2) {
                break;
            }
            Range range = ranges.get(a);
            if (offset <= range.start && (minRange == null || range.start < minRange.start)) {
                minRange = range;
            }
            if (range.start <= offset && range.end > offset) {
                availableLength = 0;
                break;
            }
            a++;
        }
        if (availableLength == 0) {
            return 0;
        }
        if (minRange != null) {
            return Math.min(length, minRange.start - offset);
        }
        return Math.min(length, Math.max(this.totalBytesCount - offset, 0));
    }

    /* access modifiers changed from: protected */
    public float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        int i = this.totalBytesCount;
        if (i == 0 || ranges == null) {
            return 0.0f;
        }
        return (((float) getDownloadedLengthFromOffsetInternal(ranges, (int) (((float) i) * progress), i)) / ((float) this.totalBytesCount)) + progress;
    }

    /* access modifiers changed from: protected */
    public int getDownloadedLengthFromOffset(int offset, int length) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] result = new int[1];
        Utilities.stageQueue.postRunnable(new Runnable(result, offset, length, countDownLatch) {
            private final /* synthetic */ int[] f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ CountDownLatch f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                FileLoadOperation.this.lambda$getDownloadedLengthFromOffset$2$FileLoadOperation(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
        }
        return result[0];
    }

    public /* synthetic */ void lambda$getDownloadedLengthFromOffset$2$FileLoadOperation(int[] result, int offset, int length, CountDownLatch countDownLatch) {
        result[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, offset, length);
        countDownLatch.countDown();
    }

    public String getFileName() {
        if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
        }
        return Utilities.MD5(this.webFile.url) + "." + this.ext;
    }

    /* access modifiers changed from: protected */
    public void removeStreamListener(FileLoadOperationStream operation) {
        Utilities.stageQueue.postRunnable(new Runnable(operation) {
            private final /* synthetic */ FileLoadOperationStream f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FileLoadOperation.this.lambda$removeStreamListener$3$FileLoadOperation(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeStreamListener$3$FileLoadOperation(FileLoadOperationStream operation) {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            arrayList.remove(operation);
        }
    }

    private void copyNotLoadedRanges() {
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList<>(this.notLoadedBytesRanges);
        }
    }

    public void pause() {
        if (this.state == 1) {
            this.paused = true;
        }
    }

    public boolean start() {
        return start((FileLoadOperationStream) null, 0, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:190:0x05ca A[SYNTHETIC, Splitter:B:190:0x05ca] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0669  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x06c3  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x06eb  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0717  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0756  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x07ca A[Catch:{ Exception -> 0x07d1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x07df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(im.bclpbkiauv.messenger.FileLoadOperationStream r32, int r33, boolean r34) {
        /*
            r31 = this;
            r7 = r31
            int r0 = r7.currentDownloadChunkSize
            if (r0 != 0) goto L_0x0019
            int r0 = r7.totalBytesCount
            r1 = 1048576(0x100000, float:1.469368E-39)
            if (r0 < r1) goto L_0x000f
            r0 = 131072(0x20000, float:1.83671E-40)
            goto L_0x0012
        L_0x000f:
            r0 = 32768(0x8000, float:4.5918E-41)
        L_0x0012:
            r7.currentDownloadChunkSize = r0
            int r0 = r7.totalBytesCount
            r0 = 4
            r7.currentMaxDownloadRequests = r0
        L_0x0019:
            int r0 = r7.state
            r8 = 1
            r9 = 0
            if (r0 == 0) goto L_0x0021
            r0 = 1
            goto L_0x0022
        L_0x0021:
            r0 = 0
        L_0x0022:
            r10 = r0
            boolean r11 = r7.paused
            r7.paused = r9
            if (r32 == 0) goto L_0x003e
            im.bclpbkiauv.messenger.DispatchQueue r0 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$MtJCrWedutY32aN32ckwJLkXNjk r12 = new im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$MtJCrWedutY32aN32ckwJLkXNjk
            r1 = r12
            r2 = r31
            r3 = r34
            r4 = r33
            r5 = r32
            r6 = r10
            r1.<init>(r3, r4, r5, r6)
            r0.postRunnable(r12)
            goto L_0x004c
        L_0x003e:
            if (r11 == 0) goto L_0x004c
            if (r10 == 0) goto L_0x004c
            im.bclpbkiauv.messenger.DispatchQueue r0 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$OeCdQTFwLVtC1tqg-uM26jflKS0 r1 = new im.bclpbkiauv.messenger.-$$Lambda$OeCdQTFwLVtC1tqg-uM26jflKS0
            r1.<init>()
            r0.postRunnable(r1)
        L_0x004c:
            if (r10 == 0) goto L_0x004f
            return r11
        L_0x004f:
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r7.location
            if (r0 != 0) goto L_0x005b
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            if (r0 != 0) goto L_0x005b
            r7.onFail(r8, r9)
            return r9
        L_0x005b:
            int r0 = r7.currentDownloadChunkSize
            int r1 = r33 / r0
            int r1 = r1 * r0
            r7.streamStartOffset = r1
            boolean r1 = r7.allowDisordererFileSave
            if (r1 == 0) goto L_0x007b
            int r1 = r7.totalBytesCount
            if (r1 <= 0) goto L_0x007b
            if (r1 <= r0) goto L_0x007b
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notRequestedBytesRanges = r0
        L_0x007b:
            r0 = 0
            r1 = 0
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r3 = r7.webLocation
            java.lang.String r4 = ".iv.enc"
            java.lang.String r5 = ".iv"
            java.lang.String r6 = ".enc"
            java.lang.String r12 = ".temp.enc"
            java.lang.String r13 = ".temp"
            java.lang.String r14 = "."
            r15 = 0
            if (r3 == 0) goto L_0x0114
            im.bclpbkiauv.messenger.WebFile r3 = r7.webFile
            java.lang.String r3 = r3.url
            java.lang.String r3 = im.bclpbkiauv.messenger.Utilities.MD5(r3)
            boolean r8 = r7.encryptFile
            if (r8 == 0) goto L_0x00d6
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r14)
            java.lang.String r12 = r7.ext
            r8.append(r12)
            r8.append(r6)
            java.lang.String r6 = r8.toString()
            byte[] r8 = r7.key
            if (r8 == 0) goto L_0x010f
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r4)
            java.lang.String r2 = r8.toString()
            goto L_0x010f
        L_0x00d6:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            r6.append(r14)
            java.lang.String r8 = r7.ext
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            byte[] r8 = r7.key
            if (r8 == 0) goto L_0x010e
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r5)
            java.lang.String r2 = r8.toString()
            r5 = r4
            goto L_0x010f
        L_0x010e:
            r5 = r4
        L_0x010f:
            r3 = r0
            r17 = r10
            goto L_0x0367
        L_0x0114:
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r7.location
            r17 = r10
            long r9 = r3.volume_id
            java.lang.String r3 = ".pt"
            java.lang.String r8 = ".preload"
            r19 = r0
            java.lang.String r0 = "_"
            int r20 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r20 == 0) goto L_0x025c
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x025c
            int r9 = r7.datacenterId
            r10 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r9 == r10) goto L_0x0256
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r20 = -2147483648(0xffffffff80000000, double:NaN)
            int r22 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r22 == 0) goto L_0x0256
            int r9 = r7.datacenterId
            if (r9 != 0) goto L_0x0143
            goto L_0x0256
        L_0x0143:
            boolean r9 = r7.encryptFile
            if (r9 == 0) goto L_0x01b2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r8 = r5.volume_id
            r3.append(r8)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r5 = r7.location
            int r5 = r5.local_id
            r3.append(r5)
            r3.append(r12)
            java.lang.String r5 = r3.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.volume_id
            r3.append(r8)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r8 = r7.location
            int r8 = r8.local_id
            r3.append(r8)
            r3.append(r14)
            java.lang.String r8 = r7.ext
            r3.append(r8)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            byte[] r3 = r7.key
            if (r3 == 0) goto L_0x01ae
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.volume_id
            r3.append(r8)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r3.append(r0)
            r3.append(r4)
            java.lang.String r2 = r3.toString()
            r3 = r19
            goto L_0x0367
        L_0x01ae:
            r3 = r19
            goto L_0x0367
        L_0x01b2:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r7.location
            long r9 = r6.volume_id
            r4.append(r9)
            r4.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r7.location
            int r6 = r6.local_id
            r4.append(r6)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r6.append(r9)
            r6.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            r6.append(r9)
            r6.append(r14)
            java.lang.String r9 = r7.ext
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            byte[] r9 = r7.key
            if (r9 == 0) goto L_0x0212
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r12 = r10.volume_id
            r9.append(r12)
            r9.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r10 = r7.location
            int r10 = r10.local_id
            r9.append(r10)
            r9.append(r5)
            java.lang.String r2 = r9.toString()
        L_0x0212:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges
            if (r5 == 0) goto L_0x0234
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r5.append(r9)
            r5.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            r5.append(r9)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            goto L_0x0236
        L_0x0234:
            r3 = r19
        L_0x0236:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r5.append(r9)
            r5.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r5.append(r0)
            r5.append(r8)
            java.lang.String r1 = r5.toString()
            r5 = r4
            goto L_0x0367
        L_0x0256:
            r3 = 1
            r4 = 0
            r7.onFail(r3, r4)
            return r4
        L_0x025c:
            int r10 = r7.datacenterId
            if (r10 == 0) goto L_0x0807
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r9 = r10.id
            int r14 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r14 != 0) goto L_0x026c
            r4 = 0
            r5 = 1
            goto L_0x0809
        L_0x026c:
            boolean r9 = r7.encryptFile
            if (r9 == 0) goto L_0x02d2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r5 = r7.datacenterId
            r3.append(r5)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r8 = r5.id
            r3.append(r8)
            r3.append(r12)
            java.lang.String r5 = r3.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r8 = r7.datacenterId
            r3.append(r8)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.id
            r3.append(r8)
            java.lang.String r8 = r7.ext
            r3.append(r8)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            byte[] r3 = r7.key
            if (r3 == 0) goto L_0x02ce
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r8 = r7.datacenterId
            r3.append(r8)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r8 = r0.id
            r3.append(r8)
            r3.append(r4)
            java.lang.String r2 = r3.toString()
            r3 = r19
            goto L_0x0367
        L_0x02ce:
            r3 = r19
            goto L_0x0367
        L_0x02d2:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r6 = r7.datacenterId
            r4.append(r6)
            r4.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r7.location
            long r9 = r6.id
            r4.append(r9)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r9 = r7.datacenterId
            r6.append(r9)
            r6.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.id
            r6.append(r9)
            java.lang.String r9 = r7.ext
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            byte[] r9 = r7.key
            if (r9 == 0) goto L_0x0329
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            int r10 = r7.datacenterId
            r9.append(r10)
            r9.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r12 = r10.id
            r9.append(r12)
            r9.append(r5)
            java.lang.String r2 = r9.toString()
        L_0x0329:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges
            if (r5 == 0) goto L_0x0349
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r9 = r7.datacenterId
            r5.append(r9)
            r5.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.id
            r5.append(r9)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            goto L_0x034b
        L_0x0349:
            r3 = r19
        L_0x034b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r9 = r7.datacenterId
            r5.append(r9)
            r5.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r9 = r0.id
            r5.append(r9)
            r5.append(r8)
            java.lang.String r1 = r5.toString()
            r5 = r4
        L_0x0367:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r4 = r7.currentMaxDownloadRequests
            r0.<init>(r4)
            r7.requestInfos = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            int r4 = r7.currentMaxDownloadRequests
            r8 = 1
            int r4 = r4 - r8
            r0.<init>(r4)
            r7.delayedRequestInfos = r0
            r7.state = r8
            java.lang.Object r0 = r7.parentObject
            boolean r4 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_theme
            if (r4 == 0) goto L_0x03a9
            im.bclpbkiauv.tgnet.TLRPC$TL_theme r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_theme) r0
            java.io.File r4 = new java.io.File
            java.io.File r8 = im.bclpbkiauv.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "remote"
            r9.append(r10)
            long r12 = r0.id
            r9.append(r12)
            java.lang.String r10 = ".attheme"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r4.<init>(r8, r9)
            r7.cacheFileFinal = r4
            goto L_0x03b2
        L_0x03a9:
            java.io.File r0 = new java.io.File
            java.io.File r4 = r7.storePath
            r0.<init>(r4, r6)
            r7.cacheFileFinal = r0
        L_0x03b2:
            java.io.File r0 = r7.cacheFileFinal
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x03d1
            int r4 = r7.totalBytesCount
            if (r4 == 0) goto L_0x03d1
            long r8 = (long) r4
            java.io.File r4 = r7.cacheFileFinal
            long r12 = r4.length()
            int r4 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x03d1
            java.io.File r4 = r7.cacheFileFinal
            r4.delete()
            r0 = 0
            r4 = r0
            goto L_0x03d2
        L_0x03d1:
            r4 = r0
        L_0x03d2:
            if (r4 != 0) goto L_0x07f0
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            r0.<init>(r8, r5)
            r7.cacheFileTemp = r0
            boolean r0 = r7.ungzip
            if (r0 == 0) goto L_0x03fb
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r10 = ".gz"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.<init>(r8, r9)
            r7.cacheFileGzipTemp = r0
        L_0x03fb:
            r9 = 0
            boolean r0 = r7.encryptFile
            r10 = 32
            java.lang.String r12 = "rws"
            if (r0 == 0) goto L_0x0478
            java.io.File r0 = new java.io.File
            java.io.File r8 = im.bclpbkiauv.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            java.lang.String r14 = ".key"
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r0.<init>(r8, r13)
            r13 = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0474 }
            r0.<init>(r13, r12)     // Catch:{ Exception -> 0x0474 }
            r14 = r0
            long r19 = r13.length()     // Catch:{ Exception -> 0x0474 }
            byte[] r0 = new byte[r10]     // Catch:{ Exception -> 0x0474 }
            r7.encryptKey = r0     // Catch:{ Exception -> 0x0474 }
            r8 = 16
            byte[] r10 = new byte[r8]     // Catch:{ Exception -> 0x0474 }
            r7.encryptIv = r10     // Catch:{ Exception -> 0x0474 }
            int r10 = (r19 > r15 ? 1 : (r19 == r15 ? 0 : -1))
            if (r10 <= 0) goto L_0x044b
            r22 = 48
            long r22 = r19 % r22
            int r10 = (r22 > r15 ? 1 : (r22 == r15 ? 0 : -1))
            if (r10 != 0) goto L_0x044b
            r10 = 32
            r15 = 0
            r14.read(r0, r15, r10)     // Catch:{ Exception -> 0x0474 }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x0474 }
            r14.read(r0, r15, r8)     // Catch:{ Exception -> 0x0474 }
            goto L_0x0464
        L_0x044b:
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x0474 }
            byte[] r10 = r7.encryptKey     // Catch:{ Exception -> 0x0474 }
            r0.nextBytes(r10)     // Catch:{ Exception -> 0x0474 }
            java.security.SecureRandom r0 = im.bclpbkiauv.messenger.Utilities.random     // Catch:{ Exception -> 0x0474 }
            byte[] r10 = r7.encryptIv     // Catch:{ Exception -> 0x0474 }
            r0.nextBytes(r10)     // Catch:{ Exception -> 0x0474 }
            byte[] r0 = r7.encryptKey     // Catch:{ Exception -> 0x0474 }
            r14.write(r0)     // Catch:{ Exception -> 0x0474 }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x0474 }
            r14.write(r0)     // Catch:{ Exception -> 0x0474 }
            r9 = 1
        L_0x0464:
            java.nio.channels.FileChannel r0 = r14.getChannel()     // Catch:{ Exception -> 0x046c }
            r0.close()     // Catch:{ Exception -> 0x046c }
            goto L_0x0470
        L_0x046c:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0474 }
        L_0x0470:
            r14.close()     // Catch:{ Exception -> 0x0474 }
            goto L_0x0478
        L_0x0474:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0478:
            r10 = 1
            boolean[] r0 = new boolean[r10]
            r8 = 0
            r0[r8] = r8
            r10 = r0
            boolean r0 = r7.supportsPreloading
            r15 = 0
            if (r0 == 0) goto L_0x05e5
            if (r1 == 0) goto L_0x05e5
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            r0.<init>(r8, r1)
            r7.cacheFilePreload = r0
            r16 = 0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05b1 }
            java.io.File r8 = r7.cacheFilePreload     // Catch:{ Exception -> 0x05b1 }
            r0.<init>(r8, r12)     // Catch:{ Exception -> 0x05b1 }
            r7.preloadStream = r0     // Catch:{ Exception -> 0x05b1 }
            long r19 = r0.length()     // Catch:{ Exception -> 0x05b1 }
            r0 = 0
            r8 = 1
            r7.preloadStreamFileOffset = r8     // Catch:{ Exception -> 0x05b1 }
            long r13 = (long) r0     // Catch:{ Exception -> 0x05b1 }
            long r13 = r19 - r13
            r26 = 1
            int r8 = (r13 > r26 ? 1 : (r13 == r26 ? 0 : -1))
            if (r8 <= 0) goto L_0x059f
            java.io.RandomAccessFile r8 = r7.preloadStream     // Catch:{ Exception -> 0x05b1 }
            byte r8 = r8.readByte()     // Catch:{ Exception -> 0x05b1 }
            if (r8 == 0) goto L_0x04b5
            r8 = 1
            goto L_0x04b6
        L_0x04b5:
            r8 = 0
        L_0x04b6:
            r13 = 0
            r10[r13] = r8     // Catch:{ Exception -> 0x05b1 }
            int r0 = r0 + 1
        L_0x04bb:
            long r13 = (long) r0     // Catch:{ Exception -> 0x05b1 }
            int r18 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r18 >= 0) goto L_0x0597
            long r13 = (long) r0     // Catch:{ Exception -> 0x05b1 }
            long r13 = r19 - r13
            r24 = 4
            int r18 = (r13 > r24 ? 1 : (r13 == r24 ? 0 : -1))
            if (r18 >= 0) goto L_0x04d2
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
            goto L_0x05a6
        L_0x04d2:
            java.io.RandomAccessFile r13 = r7.preloadStream     // Catch:{ Exception -> 0x05b1 }
            int r13 = r13.readInt()     // Catch:{ Exception -> 0x05b1 }
            int r0 = r0 + 4
            r14 = r9
            long r8 = (long) r0
            long r8 = r19 - r8
            r24 = 4
            int r26 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
            if (r26 < 0) goto L_0x0590
            if (r13 < 0) goto L_0x0590
            int r8 = r7.totalBytesCount     // Catch:{ Exception -> 0x0588 }
            if (r13 <= r8) goto L_0x04f2
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05a6
        L_0x04f2:
            java.io.RandomAccessFile r8 = r7.preloadStream     // Catch:{ Exception -> 0x0588 }
            int r8 = r8.readInt()     // Catch:{ Exception -> 0x0588 }
            int r0 = r0 + 4
            r26 = r4
            r9 = r5
            long r4 = (long) r0
            long r4 = r19 - r4
            r28 = r9
            r27 = r10
            long r9 = (long) r8
            int r29 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r29 < 0) goto L_0x05a6
            int r4 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x05af }
            if (r8 <= r4) goto L_0x050f
            goto L_0x05a6
        L_0x050f:
            im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange r4 = new im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x05af }
            r4.<init>(r0, r13, r8)     // Catch:{ Exception -> 0x05af }
            int r0 = r0 + r8
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05af }
            long r9 = (long) r0     // Catch:{ Exception -> 0x05af }
            r5.seek(r9)     // Catch:{ Exception -> 0x05af }
            long r9 = (long) r0     // Catch:{ Exception -> 0x05af }
            long r9 = r19 - r9
            r29 = 12
            int r5 = (r9 > r29 ? 1 : (r9 == r29 ? 0 : -1))
            if (r5 >= 0) goto L_0x0526
            goto L_0x05a6
        L_0x0526:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05af }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05af }
            r7.foundMoovSize = r5     // Catch:{ Exception -> 0x05af }
            if (r5 == 0) goto L_0x0540
            int r5 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05af }
            int r9 = r7.totalBytesCount     // Catch:{ Exception -> 0x05af }
            r10 = 2
            int r9 = r9 / r10
            if (r5 <= r9) goto L_0x0539
            goto L_0x053a
        L_0x0539:
            r10 = 1
        L_0x053a:
            r7.moovFound = r10     // Catch:{ Exception -> 0x05af }
            int r5 = r7.foundMoovSize     // Catch:{ Exception -> 0x05af }
            r7.preloadNotRequestedBytesCount = r5     // Catch:{ Exception -> 0x05af }
        L_0x0540:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05af }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05af }
            r7.nextPreloadDownloadOffset = r5     // Catch:{ Exception -> 0x05af }
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05af }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05af }
            r7.nextAtomOffset = r5     // Catch:{ Exception -> 0x05af }
            int r0 = r0 + 12
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x05af }
            if (r5 != 0) goto L_0x055d
            android.util.SparseArray r5 = new android.util.SparseArray     // Catch:{ Exception -> 0x05af }
            r5.<init>()     // Catch:{ Exception -> 0x05af }
            r7.preloadedBytesRanges = r5     // Catch:{ Exception -> 0x05af }
        L_0x055d:
            android.util.SparseIntArray r5 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05af }
            if (r5 != 0) goto L_0x0568
            android.util.SparseIntArray r5 = new android.util.SparseIntArray     // Catch:{ Exception -> 0x05af }
            r5.<init>()     // Catch:{ Exception -> 0x05af }
            r7.requestedPreloadedBytesRanges = r5     // Catch:{ Exception -> 0x05af }
        L_0x0568:
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x05af }
            r5.put(r13, r4)     // Catch:{ Exception -> 0x05af }
            android.util.SparseIntArray r5 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05af }
            r9 = 1
            r5.put(r13, r9)     // Catch:{ Exception -> 0x05af }
            int r5 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x05af }
            int r5 = r5 + r8
            r7.totalPreloadedBytes = r5     // Catch:{ Exception -> 0x05af }
            int r5 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x05af }
            int r9 = r8 + 20
            int r5 = r5 + r9
            r7.preloadStreamFileOffset = r5     // Catch:{ Exception -> 0x05af }
            r9 = r14
            r4 = r26
            r10 = r27
            r5 = r28
            goto L_0x04bb
        L_0x0588:
            r0 = move-exception
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05b9
        L_0x0590:
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05a6
        L_0x0597:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
            goto L_0x05a6
        L_0x059f:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05a6:
            java.io.RandomAccessFile r4 = r7.preloadStream     // Catch:{ Exception -> 0x05af }
            int r5 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x05af }
            long r8 = (long) r5     // Catch:{ Exception -> 0x05af }
            r4.seek(r8)     // Catch:{ Exception -> 0x05af }
            goto L_0x05bc
        L_0x05af:
            r0 = move-exception
            goto L_0x05b9
        L_0x05b1:
            r0 = move-exception
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05b9:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05bc:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x05ec
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r0 = r7.preloadedBytesRanges
            if (r0 != 0) goto L_0x05ec
            r7.cacheFilePreload = r15
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05e0 }
            if (r0 == 0) goto L_0x05df
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05d4 }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x05d4 }
            r0.close()     // Catch:{ Exception -> 0x05d4 }
            goto L_0x05d8
        L_0x05d4:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x05e0 }
        L_0x05d8:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05e0 }
            r0.close()     // Catch:{ Exception -> 0x05e0 }
            r7.preloadStream = r15     // Catch:{ Exception -> 0x05e0 }
        L_0x05df:
            goto L_0x05ec
        L_0x05e0:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x05ec
        L_0x05e5:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05ec:
            if (r3 == 0) goto L_0x065f
            java.io.File r0 = new java.io.File
            java.io.File r4 = r7.tempPath
            r0.<init>(r4, r3)
            r7.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0658 }
            java.io.File r4 = r7.cacheFileParts     // Catch:{ Exception -> 0x0658 }
            r0.<init>(r4, r12)     // Catch:{ Exception -> 0x0658 }
            r7.filePartsStream = r0     // Catch:{ Exception -> 0x0658 }
            long r4 = r0.length()     // Catch:{ Exception -> 0x0658 }
            r8 = 8
            long r8 = r4 % r8
            r19 = 4
            int r0 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x0655
            long r4 = r4 - r19
            java.io.RandomAccessFile r0 = r7.filePartsStream     // Catch:{ Exception -> 0x0658 }
            int r0 = r0.readInt()     // Catch:{ Exception -> 0x0658 }
            long r8 = (long) r0     // Catch:{ Exception -> 0x0658 }
            r19 = 2
            long r19 = r4 / r19
            int r10 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1))
            if (r10 > 0) goto L_0x0652
            r8 = 0
        L_0x0620:
            if (r8 >= r0) goto L_0x064f
            java.io.RandomAccessFile r9 = r7.filePartsStream     // Catch:{ Exception -> 0x0658 }
            int r9 = r9.readInt()     // Catch:{ Exception -> 0x0658 }
            java.io.RandomAccessFile r10 = r7.filePartsStream     // Catch:{ Exception -> 0x0658 }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x0658 }
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r13 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0658 }
            im.bclpbkiauv.messenger.FileLoadOperation$Range r15 = new im.bclpbkiauv.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0658 }
            r19 = r1
            r1 = 0
            r15.<init>(r9, r10)     // Catch:{ Exception -> 0x064d }
            r13.add(r15)     // Catch:{ Exception -> 0x064d }
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r1 = r7.notRequestedBytesRanges     // Catch:{ Exception -> 0x064d }
            im.bclpbkiauv.messenger.FileLoadOperation$Range r13 = new im.bclpbkiauv.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x064d }
            r15 = 0
            r13.<init>(r9, r10)     // Catch:{ Exception -> 0x064d }
            r1.add(r13)     // Catch:{ Exception -> 0x064d }
            int r8 = r8 + 1
            r1 = r19
            r15 = 0
            goto L_0x0620
        L_0x064d:
            r0 = move-exception
            goto L_0x065b
        L_0x064f:
            r19 = r1
            goto L_0x0657
        L_0x0652:
            r19 = r1
            goto L_0x0657
        L_0x0655:
            r19 = r1
        L_0x0657:
            goto L_0x0661
        L_0x0658:
            r0 = move-exception
            r19 = r1
        L_0x065b:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0661
        L_0x065f:
            r19 = r1
        L_0x0661:
            java.io.File r0 = r7.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x06c3
            if (r14 == 0) goto L_0x0672
            java.io.File r0 = r7.cacheFileTemp
            r0.delete()
            goto L_0x06e7
        L_0x0672:
            java.io.File r0 = r7.cacheFileTemp
            long r0 = r0.length()
            if (r2 == 0) goto L_0x068b
            int r4 = r7.currentDownloadChunkSize
            long r4 = (long) r4
            long r4 = r0 % r4
            r8 = 0
            int r10 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x068b
            r4 = 0
            r7.downloadedBytes = r4
            r7.requestedBytesCount = r4
            goto L_0x069b
        L_0x068b:
            java.io.File r4 = r7.cacheFileTemp
            long r4 = r4.length()
            int r5 = (int) r4
            int r4 = r7.currentDownloadChunkSize
            int r5 = r5 / r4
            int r5 = r5 * r4
            r7.downloadedBytes = r5
            r7.requestedBytesCount = r5
        L_0x069b:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            if (r4 == 0) goto L_0x06c2
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x06c2
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            im.bclpbkiauv.messenger.FileLoadOperation$Range r5 = new im.bclpbkiauv.messenger.FileLoadOperation$Range
            int r9 = r7.downloadedBytes
            int r10 = r7.totalBytesCount
            r13 = 0
            r5.<init>(r9, r10)
            r4.add(r5)
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r4 = r7.notRequestedBytesRanges
            im.bclpbkiauv.messenger.FileLoadOperation$Range r5 = new im.bclpbkiauv.messenger.FileLoadOperation$Range
            int r9 = r7.downloadedBytes
            int r10 = r7.totalBytesCount
            r5.<init>(r9, r10)
            r4.add(r5)
        L_0x06c2:
            goto L_0x06e7
        L_0x06c3:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x06e7
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x06e7
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            im.bclpbkiauv.messenger.FileLoadOperation$Range r1 = new im.bclpbkiauv.messenger.FileLoadOperation$Range
            int r4 = r7.totalBytesCount
            r5 = 0
            r8 = 0
            r1.<init>(r8, r4)
            r0.add(r1)
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            im.bclpbkiauv.messenger.FileLoadOperation$Range r1 = new im.bclpbkiauv.messenger.FileLoadOperation$Range
            int r4 = r7.totalBytesCount
            r1.<init>(r8, r4)
            r0.add(r1)
        L_0x06e7:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0713
            int r1 = r7.totalBytesCount
            r7.downloadedBytes = r1
            int r0 = r0.size()
            r1 = 0
        L_0x06f4:
            if (r1 >= r0) goto L_0x070f
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            java.lang.Object r4 = r4.get(r1)
            im.bclpbkiauv.messenger.FileLoadOperation$Range r4 = (im.bclpbkiauv.messenger.FileLoadOperation.Range) r4
            int r5 = r7.downloadedBytes
            int r9 = r4.end
            int r10 = r4.start
            int r9 = r9 - r10
            int r5 = r5 - r9
            r7.downloadedBytes = r5
            int r1 = r1 + 1
            goto L_0x06f4
        L_0x070f:
            int r1 = r7.downloadedBytes
            r7.requestedBytesCount = r1
        L_0x0713:
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0754
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0733
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start preloading file to temp = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileTemp
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
            goto L_0x0754
        L_0x0733:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start loading file to temp = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileTemp
            r0.append(r1)
            java.lang.String r1 = " final = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileFinal
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0754:
            if (r2 == 0) goto L_0x079c
            java.io.File r0 = new java.io.File
            java.io.File r1 = r7.tempPath
            r0.<init>(r1, r2)
            r7.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0793 }
            java.io.File r1 = r7.cacheIvTemp     // Catch:{ Exception -> 0x0793 }
            r0.<init>(r1, r12)     // Catch:{ Exception -> 0x0793 }
            r7.fiv = r0     // Catch:{ Exception -> 0x0793 }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0793 }
            if (r0 == 0) goto L_0x0792
            if (r14 != 0) goto L_0x0792
            java.io.File r0 = r7.cacheIvTemp     // Catch:{ Exception -> 0x0793 }
            long r0 = r0.length()     // Catch:{ Exception -> 0x0793 }
            r4 = 0
            int r9 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x078d
            r9 = 32
            long r9 = r0 % r9
            int r13 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r13 != 0) goto L_0x078d
            java.io.RandomAccessFile r4 = r7.fiv     // Catch:{ Exception -> 0x0793 }
            byte[] r5 = r7.iv     // Catch:{ Exception -> 0x0793 }
            r8 = 32
            r9 = 0
            r4.read(r5, r9, r8)     // Catch:{ Exception -> 0x0793 }
            goto L_0x0792
        L_0x078d:
            r4 = 0
            r7.downloadedBytes = r4     // Catch:{ Exception -> 0x0793 }
            r7.requestedBytesCount = r4     // Catch:{ Exception -> 0x0793 }
        L_0x0792:
            goto L_0x079c
        L_0x0793:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r7.downloadedBytes = r1
            r7.requestedBytesCount = r1
        L_0x079c:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x07bd
            int r0 = r7.downloadedBytes
            if (r0 == 0) goto L_0x07bd
            int r0 = r7.totalBytesCount
            if (r0 <= 0) goto L_0x07bd
            r31.copyNotLoadedRanges()
            im.bclpbkiauv.messenger.FileLoadOperation$FileLoadOperationDelegate r0 = r7.delegate
            r1 = 1065353216(0x3f800000, float:1.0)
            int r4 = r7.downloadedBytes
            float r4 = (float) r4
            int r5 = r7.totalBytesCount
            float r5 = (float) r5
            float r4 = r4 / r5
            float r1 = java.lang.Math.min(r1, r4)
            r0.didChangedLoadProgress(r7, r1)
        L_0x07bd:
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07d1 }
            java.io.File r1 = r7.cacheFileTemp     // Catch:{ Exception -> 0x07d1 }
            r0.<init>(r1, r12)     // Catch:{ Exception -> 0x07d1 }
            r7.fileOutputStream = r0     // Catch:{ Exception -> 0x07d1 }
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x07d1 }
            if (r1 == 0) goto L_0x07d0
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x07d1 }
            long r4 = (long) r1     // Catch:{ Exception -> 0x07d1 }
            r0.seek(r4)     // Catch:{ Exception -> 0x07d1 }
        L_0x07d0:
            goto L_0x07d5
        L_0x07d1:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07d5:
            java.io.RandomAccessFile r0 = r7.fileOutputStream
            if (r0 != 0) goto L_0x07df
            r1 = 1
            r4 = 0
            r7.onFail(r1, r4)
            return r4
        L_0x07df:
            r1 = 1
            r7.started = r1
            im.bclpbkiauv.messenger.DispatchQueue r0 = im.bclpbkiauv.messenger.Utilities.stageQueue
            im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$Ees8qrU-ExnchoOUFjSjqeNWZ48 r4 = new im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$Ees8qrU-ExnchoOUFjSjqeNWZ48
            r5 = r27
            r4.<init>(r5)
            r0.postRunnable(r4)
            r5 = 1
            goto L_0x0806
        L_0x07f0:
            r19 = r1
            r26 = r4
            r28 = r5
            r1 = 1
            r7.started = r1
            r4 = 0
            r7.onFinishLoadingFile(r4)     // Catch:{ Exception -> 0x07ff }
            r5 = 1
            goto L_0x0806
        L_0x07ff:
            r0 = move-exception
            r1 = r0
            r0 = r1
            r5 = 1
            r7.onFail(r5, r4)
        L_0x0806:
            return r5
        L_0x0807:
            r4 = 0
            r5 = 1
        L_0x0809:
            r7.onFail(r5, r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileLoadOperation.start(im.bclpbkiauv.messenger.FileLoadOperationStream, int, boolean):boolean");
    }

    public /* synthetic */ void lambda$start$4$FileLoadOperation(boolean steamPriority, int streamOffset, FileLoadOperationStream stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (steamPriority) {
            int i = this.currentDownloadChunkSize;
            int offset = (streamOffset / i) * i;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == offset)) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.priorityRequestInfo.offset + this.currentDownloadChunkSize);
                if (this.priorityRequestInfo.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.priorityRequestInfo.requestToken, true);
                    this.requestsCount--;
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get cancel request at offset " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (this.priorityRequestInfo == null) {
                this.streamPriorityStartOffset = offset;
            }
        } else {
            int i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (streamOffset / i2) * i2;
        }
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            if (!(this.preloadedBytesRanges == null || getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) != 0 || this.preloadedBytesRanges.get(this.streamStartOffset) == null)) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    public /* synthetic */ void lambda$start$5$FileLoadOperation(boolean[] preloaded) {
        if (this.totalBytesCount == 0 || ((!this.isPreloadVideoOperation || !preloaded[0]) && this.downloadedBytes != this.totalBytesCount)) {
            startDownloadRequest();
            return;
        }
        try {
            onFinishLoadingFile(false);
        } catch (Exception e) {
            onFail(true, 0);
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setIsPreloadVideoOperation(boolean value) {
        if (this.isPreloadVideoOperation == value) {
            return;
        }
        if (value && this.totalBytesCount <= 2097152) {
            return;
        }
        if (value || !this.isPreloadVideoOperation) {
            this.isPreloadVideoOperation = value;
        } else if (this.state == 3) {
            this.isPreloadVideoOperation = value;
            this.state = 0;
            this.preloadFinished = false;
            start();
        } else if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new Runnable(value) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoadOperation.this.lambda$setIsPreloadVideoOperation$6$FileLoadOperation(this.f$1);
                }
            });
        } else {
            this.isPreloadVideoOperation = value;
        }
    }

    public /* synthetic */ void lambda$setIsPreloadVideoOperation$6$FileLoadOperation(boolean value) {
        this.requestedBytesCount = 0;
        clearOperaion((RequestInfo) null, true);
        this.isPreloadVideoOperation = value;
        startDownloadRequest();
    }

    public boolean isPreloadVideoOperation() {
        return this.isPreloadVideoOperation;
    }

    public boolean isPreloadFinished() {
        return this.preloadFinished;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                FileLoadOperation.this.lambda$cancel$7$FileLoadOperation();
            }
        });
    }

    public /* synthetic */ void lambda$cancel$7$FileLoadOperation() {
        if (this.state != 3 && this.state != 2) {
            if (this.requestInfos != null) {
                for (int a = 0; a < this.requestInfos.size(); a++) {
                    RequestInfo requestInfo = this.requestInfos.get(a);
                    if (requestInfo.requestToken != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    }
                }
            }
            onFail(false, 1);
        }
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            if (this.preloadStream != null) {
                try {
                    this.preloadStream.getChannel().close();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Exception e6) {
            FileLog.e((Throwable) e6);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Exception e7) {
                    FileLog.e((Throwable) e7);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Exception e8) {
            FileLog.e((Throwable) e8);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Exception e9) {
            FileLog.e((Throwable) e9);
        }
        if (this.delayedRequestInfos != null) {
            for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(a);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile(boolean increment) {
        boolean renameResult;
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.isPreloadVideoOperation) {
                this.preloadFinished = true;
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("finished preloading file to " + this.cacheFileTemp + " loaded " + this.totalPreloadedBytes + " of " + this.totalBytesCount);
                }
            } else {
                File file = this.cacheIvTemp;
                if (file != null) {
                    file.delete();
                    this.cacheIvTemp = null;
                }
                File file2 = this.cacheFileParts;
                if (file2 != null) {
                    file2.delete();
                    this.cacheFileParts = null;
                }
                File file3 = this.cacheFilePreload;
                if (file3 != null) {
                    file3.delete();
                    this.cacheFilePreload = null;
                }
                if (this.cacheFileTemp != null) {
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(this.cacheFileTemp));
                            FileLoader.copyFile(gzipInputStream, this.cacheFileGzipTemp, 2097152);
                            gzipInputStream.close();
                            this.cacheFileTemp.delete();
                            this.cacheFileTemp = this.cacheFileGzipTemp;
                            this.ungzip = false;
                        } catch (ZipException e) {
                            this.ungzip = false;
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to ungzip temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal);
                            }
                        }
                    }
                    if (!this.ungzip) {
                        if (this.parentObject instanceof TLRPC.TL_theme) {
                            try {
                                renameResult = AndroidUtilities.copyFile(this.cacheFileTemp, this.cacheFileFinal);
                            } catch (Exception e3) {
                                FileLog.e((Throwable) e3);
                                renameResult = false;
                            }
                        } else {
                            renameResult = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                        }
                        if (!renameResult) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                            }
                            int i = this.renameRetryCount + 1;
                            this.renameRetryCount = i;
                            if (i < 3) {
                                this.state = 1;
                                Utilities.stageQueue.postRunnable(new Runnable(increment) {
                                    private final /* synthetic */ boolean f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        FileLoadOperation.this.lambda$onFinishLoadingFile$8$FileLoadOperation(this.f$1);
                                    }
                                }, 200);
                                return;
                            }
                            this.cacheFileFinal = this.cacheFileTemp;
                        }
                    } else {
                        onFail(false, 0);
                        return;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finished downloading file to " + this.cacheFileFinal);
                }
                if (increment) {
                    int i2 = this.currentType;
                    if (i2 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i2 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i2 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i2 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                }
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    public /* synthetic */ void lambda$onFinishLoadingFile$8$FileLoadOperation(boolean increment) {
        try {
            onFinishLoadingFile(increment);
        } catch (Exception e) {
            onFail(false, 0);
        }
    }

    private void delayRequestInfo(RequestInfo requestInfo) {
        this.delayedRequestInfos.add(requestInfo);
        if (requestInfo.response != null) {
            requestInfo.response.disableFree = true;
        } else if (requestInfo.responseWeb != null) {
            requestInfo.responseWeb.disableFree = true;
        } else if (requestInfo.responseCdn != null) {
            requestInfo.responseCdn.disableFree = true;
        }
    }

    private int findNextPreloadDownloadOffset(int atomOffset, int partOffset, NativeByteBuffer partBuffer) {
        int partSize = partBuffer.limit();
        while (true) {
            if (atomOffset < partOffset - (this.preloadTempBuffer != null ? 16 : 0) || atomOffset >= partOffset + partSize) {
                return 0;
            }
            if (atomOffset >= (partOffset + partSize) - 16) {
                this.preloadTempBufferCount = (partOffset + partSize) - atomOffset;
                partBuffer.position(partBuffer.limit() - this.preloadTempBufferCount);
                partBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                return partOffset + partSize;
            }
            if (this.preloadTempBufferCount != 0) {
                partBuffer.position(0);
                byte[] bArr = this.preloadTempBuffer;
                int i = this.preloadTempBufferCount;
                partBuffer.readBytes(bArr, i, 16 - i, false);
                this.preloadTempBufferCount = 0;
            } else {
                partBuffer.position(atomOffset - partOffset);
                partBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
            }
            byte[] bArr2 = this.preloadTempBuffer;
            int atomSize = ((bArr2[0] & UByte.MAX_VALUE) << 24) + ((bArr2[1] & UByte.MAX_VALUE) << 16) + ((bArr2[2] & UByte.MAX_VALUE) << 8) + (bArr2[3] & UByte.MAX_VALUE);
            if (atomSize == 0) {
                return 0;
            }
            if (atomSize == 1) {
                atomSize = ((bArr2[12] & UByte.MAX_VALUE) << 24) + ((bArr2[13] & UByte.MAX_VALUE) << 16) + ((bArr2[14] & UByte.MAX_VALUE) << 8) + (bArr2[15] & UByte.MAX_VALUE);
            }
            byte[] bArr3 = this.preloadTempBuffer;
            if (bArr3[4] == 109 && bArr3[5] == 111 && bArr3[6] == 111 && bArr3[7] == 118) {
                return -atomSize;
            }
            if (atomSize + atomOffset >= partOffset + partSize) {
                return atomSize + atomOffset;
            }
            atomOffset += atomSize;
        }
        return 0;
    }

    private void requestFileOffsets(int offset) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLRPC.TL_upload_getCdnFileHashes req = new TLRPC.TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileLoadOperation.this.lambda$requestFileOffsets$9$FileLoadOperation(tLObject, tL_error);
                }
            }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    public /* synthetic */ void lambda$requestFileOffsets$9$FileLoadOperation(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray<>();
            }
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a);
                this.cdnHashes.put(hash.offset, hash);
            }
        }
        int a2 = 0;
        while (a2 < this.delayedRequestInfos.size()) {
            RequestInfo delayedRequestInfo = this.delayedRequestInfos.get(a2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == delayedRequestInfo.offset) {
                this.delayedRequestInfos.remove(a2);
                if (processRequestResult(delayedRequestInfo, (TLRPC.TL_error) null)) {
                    return;
                }
                if (delayedRequestInfo.response != null) {
                    delayedRequestInfo.response.disableFree = false;
                    delayedRequestInfo.response.freeResources();
                    return;
                } else if (delayedRequestInfo.responseWeb != null) {
                    delayedRequestInfo.responseWeb.disableFree = false;
                    delayedRequestInfo.responseWeb.freeResources();
                    return;
                } else if (delayedRequestInfo.responseCdn != null) {
                    delayedRequestInfo.responseCdn.disableFree = false;
                    delayedRequestInfo.responseCdn.freeResources();
                    return;
                } else {
                    return;
                }
            } else {
                a2++;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01e9 A[Catch:{ Exception -> 0x04ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01f7 A[Catch:{ Exception -> 0x04ec }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(im.bclpbkiauv.messenger.FileLoadOperation.RequestInfo r29, im.bclpbkiauv.tgnet.TLRPC.TL_error r30) {
        /*
            r28 = this;
            r1 = r28
            r2 = r30
            int r0 = r1.state
            java.lang.String r3 = " offset "
            r4 = 1
            r5 = 0
            if (r0 == r4) goto L_0x0032
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0031
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "trying to write to finished file "
            r0.append(r4)
            java.io.File r4 = r1.cacheFileFinal
            r0.append(r4)
            r0.append(r3)
            int r3 = r29.offset
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0031:
            return r5
        L_0x0032:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            r6 = r29
            r0.remove(r6)
            java.lang.String r0 = " secret = "
            java.lang.String r7 = " volume_id = "
            java.lang.String r8 = " access_hash = "
            java.lang.String r9 = " local_id = "
            java.lang.String r11 = " id = "
            if (r2 != 0) goto L_0x04f7
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r12 = r1.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            if (r12 != 0) goto L_0x0055
            int r12 = r1.downloadedBytes     // Catch:{ Exception -> 0x04ec }
            int r13 = r29.offset     // Catch:{ Exception -> 0x04ec }
            if (r12 == r13) goto L_0x0055
            r28.delayRequestInfo(r29)     // Catch:{ Exception -> 0x04ec }
            return r5
        L_0x0055:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r12 = r29.response     // Catch:{ Exception -> 0x04ec }
            if (r12 == 0) goto L_0x0062
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r12 = r29.response     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ec }
            goto L_0x007d
        L_0x0062:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_webFile r12 = r29.responseWeb     // Catch:{ Exception -> 0x04ec }
            if (r12 == 0) goto L_0x006f
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_webFile r12 = r29.responseWeb     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ec }
            goto L_0x007d
        L_0x006f:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r12 = r29.responseCdn     // Catch:{ Exception -> 0x04ec }
            if (r12 == 0) goto L_0x007c
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r12 = r29.responseCdn     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ec }
            goto L_0x007d
        L_0x007c:
            r12 = 0
        L_0x007d:
            if (r12 == 0) goto L_0x04e5
            int r13 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            if (r13 != 0) goto L_0x0088
            r6 = r12
            goto L_0x04e6
        L_0x0088:
            int r13 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            boolean r14 = r1.isCdn     // Catch:{ Exception -> 0x04ec }
            r10 = 131072(0x20000, float:1.83671E-40)
            if (r14 == 0) goto L_0x00b0
            int r14 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r14 = r14 / r10
            int r15 = r14 * r10
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_fileHash> r10 = r1.cdnHashes     // Catch:{ Exception -> 0x04ec }
            if (r10 == 0) goto L_0x00a6
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_fileHash> r10 = r1.cdnHashes     // Catch:{ Exception -> 0x04ec }
            java.lang.Object r10 = r10.get(r15)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$TL_fileHash r10 = (im.bclpbkiauv.tgnet.TLRPC.TL_fileHash) r10     // Catch:{ Exception -> 0x04ec }
            goto L_0x00a7
        L_0x00a6:
            r10 = 0
        L_0x00a7:
            if (r10 != 0) goto L_0x00b0
            r28.delayRequestInfo(r29)     // Catch:{ Exception -> 0x04ec }
            r1.requestFileOffsets(r15)     // Catch:{ Exception -> 0x04ec }
            return r4
        L_0x00b0:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r10 = r29.responseCdn     // Catch:{ Exception -> 0x04ec }
            r14 = 13
            r15 = 14
            r18 = 15
            r19 = 12
            if (r10 == 0) goto L_0x00f4
            int r10 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r10 = r10 / 16
            byte[] r4 = r1.cdnIv     // Catch:{ Exception -> 0x04ec }
            r5 = r10 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5     // Catch:{ Exception -> 0x04ec }
            r4[r18] = r5     // Catch:{ Exception -> 0x04ec }
            byte[] r4 = r1.cdnIv     // Catch:{ Exception -> 0x04ec }
            int r5 = r10 >> 8
            r5 = r5 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5     // Catch:{ Exception -> 0x04ec }
            r4[r15] = r5     // Catch:{ Exception -> 0x04ec }
            byte[] r4 = r1.cdnIv     // Catch:{ Exception -> 0x04ec }
            int r5 = r10 >> 16
            r5 = r5 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5     // Catch:{ Exception -> 0x04ec }
            r4[r14] = r5     // Catch:{ Exception -> 0x04ec }
            byte[] r4 = r1.cdnIv     // Catch:{ Exception -> 0x04ec }
            int r5 = r10 >> 24
            r5 = r5 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5     // Catch:{ Exception -> 0x04ec }
            r4[r19] = r5     // Catch:{ Exception -> 0x04ec }
            java.nio.ByteBuffer r4 = r12.buffer     // Catch:{ Exception -> 0x04ec }
            byte[] r5 = r1.cdnKey     // Catch:{ Exception -> 0x04ec }
            byte[] r14 = r1.cdnIv     // Catch:{ Exception -> 0x04ec }
            int r15 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            r6 = 0
            im.bclpbkiauv.messenger.Utilities.aesCtrDecryption(r4, r5, r14, r6, r15)     // Catch:{ Exception -> 0x04ec }
        L_0x00f4:
            boolean r4 = r1.isPreloadVideoOperation     // Catch:{ Exception -> 0x04ec }
            if (r4 == 0) goto L_0x0206
            java.io.RandomAccessFile r0 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            int r4 = r29.offset     // Catch:{ Exception -> 0x04ec }
            r0.writeInt(r4)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r0 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            r0.writeInt(r13)     // Catch:{ Exception -> 0x04ec }
            int r0 = r1.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ec }
            int r0 = r0 + 8
            r1.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r0 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x04ec }
            java.nio.ByteBuffer r4 = r12.buffer     // Catch:{ Exception -> 0x04ec }
            r0.write(r4)     // Catch:{ Exception -> 0x04ec }
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x04ec }
            if (r4 == 0) goto L_0x0143
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ec }
            r4.<init>()     // Catch:{ Exception -> 0x04ec }
            java.lang.String r5 = "save preload file part "
            r4.append(r5)     // Catch:{ Exception -> 0x04ec }
            java.io.File r5 = r1.cacheFilePreload     // Catch:{ Exception -> 0x04ec }
            r4.append(r5)     // Catch:{ Exception -> 0x04ec }
            r4.append(r3)     // Catch:{ Exception -> 0x04ec }
            int r3 = r29.offset     // Catch:{ Exception -> 0x04ec }
            r4.append(r3)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r3 = " size "
            r4.append(r3)     // Catch:{ Exception -> 0x04ec }
            r4.append(r13)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x04ec }
        L_0x0143:
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r3 = r1.preloadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            if (r3 != 0) goto L_0x014e
            android.util.SparseArray r3 = new android.util.SparseArray     // Catch:{ Exception -> 0x04ec }
            r3.<init>()     // Catch:{ Exception -> 0x04ec }
            r1.preloadedBytesRanges = r3     // Catch:{ Exception -> 0x04ec }
        L_0x014e:
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r3 = r1.preloadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            int r4 = r29.offset     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange r5 = new im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x04ec }
            int r6 = r1.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ec }
            int r7 = r29.offset     // Catch:{ Exception -> 0x04ec }
            r8 = 0
            r5.<init>(r6, r7, r13)     // Catch:{ Exception -> 0x04ec }
            r3.put(r4, r5)     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.totalPreloadedBytes     // Catch:{ Exception -> 0x04ec }
            int r3 = r3 + r13
            r1.totalPreloadedBytes = r3     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ec }
            int r3 = r3 + r13
            r1.preloadStreamFileOffset = r3     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.moovFound     // Catch:{ Exception -> 0x04ec }
            if (r3 != 0) goto L_0x01b0
            int r3 = r1.nextAtomOffset     // Catch:{ Exception -> 0x04ec }
            int r4 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.findNextPreloadDownloadOffset(r3, r4, r12)     // Catch:{ Exception -> 0x04ec }
            if (r3 >= 0) goto L_0x01a4
            int r3 = r3 * -1
            int r4 = r1.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            int r4 = r4 + r5
            r1.nextPreloadDownloadOffset = r4     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            r6 = 2
            int r5 = r5 / r6
            if (r4 >= r5) goto L_0x0197
            r4 = 1048576(0x100000, float:1.469368E-39)
            int r4 = r4 + r3
            r1.foundMoovSize = r4     // Catch:{ Exception -> 0x04ec }
            r1.preloadNotRequestedBytesCount = r4     // Catch:{ Exception -> 0x04ec }
            r4 = 1
            r1.moovFound = r4     // Catch:{ Exception -> 0x04ec }
            goto L_0x01a0
        L_0x0197:
            r4 = 2097152(0x200000, float:2.938736E-39)
            r1.foundMoovSize = r4     // Catch:{ Exception -> 0x04ec }
            r1.preloadNotRequestedBytesCount = r4     // Catch:{ Exception -> 0x04ec }
            r4 = 2
            r1.moovFound = r4     // Catch:{ Exception -> 0x04ec }
        L_0x01a0:
            r4 = -1
            r1.nextPreloadDownloadOffset = r4     // Catch:{ Exception -> 0x04ec }
            goto L_0x01ae
        L_0x01a4:
            int r4 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            int r4 = r3 / r4
            int r5 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            int r4 = r4 * r5
            r1.nextPreloadDownloadOffset = r4     // Catch:{ Exception -> 0x04ec }
        L_0x01ae:
            r1.nextAtomOffset = r3     // Catch:{ Exception -> 0x04ec }
        L_0x01b0:
            java.io.RandomAccessFile r3 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            int r4 = r1.foundMoovSize     // Catch:{ Exception -> 0x04ec }
            r3.writeInt(r4)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r3 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            int r4 = r1.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ec }
            r3.writeInt(r4)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r3 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            int r4 = r1.nextAtomOffset     // Catch:{ Exception -> 0x04ec }
            r3.writeInt(r4)     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ec }
            int r3 = r3 + 12
            r1.preloadStreamFileOffset = r3     // Catch:{ Exception -> 0x04ec }
            int r3 = r1.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ec }
            if (r3 == 0) goto L_0x01e6
            int r3 = r1.moovFound     // Catch:{ Exception -> 0x04ec }
            if (r3 == 0) goto L_0x01d7
            int r3 = r1.foundMoovSize     // Catch:{ Exception -> 0x04ec }
            if (r3 < 0) goto L_0x01e6
        L_0x01d7:
            int r3 = r1.totalPreloadedBytes     // Catch:{ Exception -> 0x04ec }
            r4 = 2097152(0x200000, float:2.938736E-39)
            if (r3 > r4) goto L_0x01e6
            int r3 = r1.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ec }
            int r4 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r3 < r4) goto L_0x01e4
            goto L_0x01e6
        L_0x01e4:
            r3 = 0
            goto L_0x01e7
        L_0x01e6:
            r3 = 1
        L_0x01e7:
            if (r3 == 0) goto L_0x01f7
            java.io.RandomAccessFile r4 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            r5 = 0
            r4.seek(r5)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r4 = r1.preloadStream     // Catch:{ Exception -> 0x04ec }
            r5 = 1
            r4.write(r5)     // Catch:{ Exception -> 0x04ec }
            goto L_0x0202
        L_0x01f7:
            int r4 = r1.moovFound     // Catch:{ Exception -> 0x04ec }
            if (r4 == 0) goto L_0x0202
            int r4 = r1.foundMoovSize     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            int r4 = r4 - r5
            r1.foundMoovSize = r4     // Catch:{ Exception -> 0x04ec }
        L_0x0202:
            r6 = r12
            r10 = r13
            goto L_0x046f
        L_0x0206:
            int r4 = r1.downloadedBytes     // Catch:{ Exception -> 0x04ec }
            int r4 = r4 + r13
            r1.downloadedBytes = r4     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r5 <= 0) goto L_0x0217
            int r5 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r4 < r5) goto L_0x0215
            r4 = 1
            goto L_0x0216
        L_0x0215:
            r4 = 0
        L_0x0216:
            goto L_0x0232
        L_0x0217:
            int r5 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            if (r13 != r5) goto L_0x0231
            int r5 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r5 == r4) goto L_0x0224
            int r5 = r1.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ec }
            int r4 = r4 % r5
            if (r4 == 0) goto L_0x022f
        L_0x0224:
            int r4 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r4 <= 0) goto L_0x0231
            int r4 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.downloadedBytes     // Catch:{ Exception -> 0x04ec }
            if (r4 > r5) goto L_0x022f
            goto L_0x0231
        L_0x022f:
            r4 = 0
            goto L_0x0232
        L_0x0231:
            r4 = 1
        L_0x0232:
            byte[] r5 = r1.key     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x025f
            java.nio.ByteBuffer r5 = r12.buffer     // Catch:{ Exception -> 0x04ec }
            byte[] r6 = r1.key     // Catch:{ Exception -> 0x04ec }
            byte[] r10 = r1.iv     // Catch:{ Exception -> 0x04ec }
            r24 = 0
            r25 = 1
            r26 = 0
            int r27 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            r21 = r5
            r22 = r6
            r23 = r10
            im.bclpbkiauv.messenger.Utilities.aesIgeEncryption(r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x04ec }
            if (r4 == 0) goto L_0x025f
            int r5 = r1.bytesCountPadding     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x025f
            int r5 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            int r6 = r1.bytesCountPadding     // Catch:{ Exception -> 0x04ec }
            int r5 = r5 - r6
            r12.limit(r5)     // Catch:{ Exception -> 0x04ec }
        L_0x025f:
            boolean r5 = r1.encryptFile     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x029f
            int r5 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r5 = r5 / 16
            byte[] r6 = r1.encryptIv     // Catch:{ Exception -> 0x04ec }
            r10 = r5 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ec }
            r6[r18] = r10     // Catch:{ Exception -> 0x04ec }
            byte[] r6 = r1.encryptIv     // Catch:{ Exception -> 0x04ec }
            int r10 = r5 >> 8
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ec }
            r14 = 14
            r6[r14] = r10     // Catch:{ Exception -> 0x04ec }
            byte[] r6 = r1.encryptIv     // Catch:{ Exception -> 0x04ec }
            int r10 = r5 >> 16
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ec }
            r14 = 13
            r6[r14] = r10     // Catch:{ Exception -> 0x04ec }
            byte[] r6 = r1.encryptIv     // Catch:{ Exception -> 0x04ec }
            int r10 = r5 >> 24
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ec }
            r6[r19] = r10     // Catch:{ Exception -> 0x04ec }
            java.nio.ByteBuffer r6 = r12.buffer     // Catch:{ Exception -> 0x04ec }
            byte[] r10 = r1.encryptKey     // Catch:{ Exception -> 0x04ec }
            byte[] r14 = r1.encryptIv     // Catch:{ Exception -> 0x04ec }
            int r15 = r12.limit()     // Catch:{ Exception -> 0x04ec }
            r16 = r5
            r5 = 0
            im.bclpbkiauv.messenger.Utilities.aesCtrDecryption(r6, r10, r14, r5, r15)     // Catch:{ Exception -> 0x04ec }
        L_0x029f:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r5 = r1.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x02d1
            java.io.RandomAccessFile r5 = r1.fileOutputStream     // Catch:{ Exception -> 0x04ec }
            int r6 = r29.offset     // Catch:{ Exception -> 0x04ec }
            long r14 = (long) r6     // Catch:{ Exception -> 0x04ec }
            r5.seek(r14)     // Catch:{ Exception -> 0x04ec }
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x02d1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ec }
            r5.<init>()     // Catch:{ Exception -> 0x04ec }
            java.lang.String r6 = "save file part "
            r5.append(r6)     // Catch:{ Exception -> 0x04ec }
            java.io.File r6 = r1.cacheFileFinal     // Catch:{ Exception -> 0x04ec }
            r5.append(r6)     // Catch:{ Exception -> 0x04ec }
            r5.append(r3)     // Catch:{ Exception -> 0x04ec }
            int r3 = r29.offset     // Catch:{ Exception -> 0x04ec }
            r5.append(r3)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r3 = r5.toString()     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x04ec }
        L_0x02d1:
            java.io.RandomAccessFile r3 = r1.fileOutputStream     // Catch:{ Exception -> 0x04ec }
            java.nio.channels.FileChannel r3 = r3.getChannel()     // Catch:{ Exception -> 0x04ec }
            java.nio.ByteBuffer r5 = r12.buffer     // Catch:{ Exception -> 0x04ec }
            r3.write(r5)     // Catch:{ Exception -> 0x04ec }
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r5 = r1.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            int r6 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r10 = r29.offset     // Catch:{ Exception -> 0x04ec }
            int r10 = r10 + r13
            r14 = 1
            r1.addPart(r5, r6, r10, r14)     // Catch:{ Exception -> 0x04ec }
            boolean r5 = r1.isCdn     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x0437
            int r5 = r29.offset     // Catch:{ Exception -> 0x04ec }
            r6 = 131072(0x20000, float:1.83671E-40)
            int r5 = r5 / r6
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r6 = r1.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ec }
            int r6 = r6.size()     // Catch:{ Exception -> 0x04ec }
            r10 = 1
            r14 = 0
        L_0x02fe:
            if (r14 >= r6) goto L_0x031d
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r15 = r1.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ec }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLoadOperation$Range r15 = (im.bclpbkiauv.messenger.FileLoadOperation.Range) r15     // Catch:{ Exception -> 0x04ec }
            r16 = r3
            int r3 = r15.start     // Catch:{ Exception -> 0x04ec }
            if (r3 > r5) goto L_0x0318
            int r3 = r15.end     // Catch:{ Exception -> 0x04ec }
            if (r5 > r3) goto L_0x0318
            r10 = 0
            goto L_0x031f
        L_0x0318:
            int r14 = r14 + 1
            r3 = r16
            goto L_0x02fe
        L_0x031d:
            r16 = r3
        L_0x031f:
            if (r10 != 0) goto L_0x042e
            r3 = 131072(0x20000, float:1.83671E-40)
            int r14 = r5 * r3
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r15 = r1.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            int r15 = r1.getDownloadedLengthFromOffsetInternal(r15, r14, r3)     // Catch:{ Exception -> 0x04ec }
            if (r15 == 0) goto L_0x0425
            if (r15 == r3) goto L_0x0345
            int r3 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r3 <= 0) goto L_0x0338
            int r3 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            int r3 = r3 - r14
            if (r15 == r3) goto L_0x0345
        L_0x0338:
            int r3 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r3 > 0) goto L_0x033f
            if (r4 == 0) goto L_0x033f
            goto L_0x0345
        L_0x033f:
            r18 = r4
            r6 = r12
            r10 = r13
            goto L_0x043d
        L_0x0345:
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_fileHash> r3 = r1.cdnHashes     // Catch:{ Exception -> 0x04ec }
            java.lang.Object r3 = r3.get(r14)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$TL_fileHash r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_fileHash) r3     // Catch:{ Exception -> 0x04ec }
            r18 = r4
            java.io.RandomAccessFile r4 = r1.fileReadStream     // Catch:{ Exception -> 0x04ec }
            if (r4 != 0) goto L_0x0369
            r4 = 131072(0x20000, float:1.83671E-40)
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x04ec }
            r1.cdnCheckBytes = r4     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04ec }
            r17 = r6
            java.io.File r6 = r1.cacheFileTemp     // Catch:{ Exception -> 0x04ec }
            r19 = r10
            java.lang.String r10 = "r"
            r4.<init>(r6, r10)     // Catch:{ Exception -> 0x04ec }
            r1.fileReadStream = r4     // Catch:{ Exception -> 0x04ec }
            goto L_0x036d
        L_0x0369:
            r17 = r6
            r19 = r10
        L_0x036d:
            java.io.RandomAccessFile r4 = r1.fileReadStream     // Catch:{ Exception -> 0x04ec }
            r6 = r12
            r10 = r13
            long r12 = (long) r14     // Catch:{ Exception -> 0x04ec }
            r4.seek(r12)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r4 = r1.fileReadStream     // Catch:{ Exception -> 0x04ec }
            byte[] r12 = r1.cdnCheckBytes     // Catch:{ Exception -> 0x04ec }
            r13 = 0
            r4.readFully(r12, r13, r15)     // Catch:{ Exception -> 0x04ec }
            byte[] r4 = r1.cdnCheckBytes     // Catch:{ Exception -> 0x04ec }
            byte[] r4 = im.bclpbkiauv.messenger.Utilities.computeSHA256(r4, r13, r15)     // Catch:{ Exception -> 0x04ec }
            byte[] r12 = r3.hash     // Catch:{ Exception -> 0x04ec }
            boolean r12 = java.util.Arrays.equals(r4, r12)     // Catch:{ Exception -> 0x04ec }
            if (r12 != 0) goto L_0x0414
            boolean r12 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04ec }
            if (r12 == 0) goto L_0x0407
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r12 = r1.location     // Catch:{ Exception -> 0x04ec }
            if (r12 == 0) goto L_0x03df
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ec }
            r12.<init>()     // Catch:{ Exception -> 0x04ec }
            java.lang.String r13 = "invalid cdn hash "
            r12.append(r13)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r13 = r1.location     // Catch:{ Exception -> 0x04ec }
            r12.append(r13)     // Catch:{ Exception -> 0x04ec }
            r12.append(r11)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r11 = r1.location     // Catch:{ Exception -> 0x04ec }
            r13 = r3
            r20 = r4
            long r3 = r11.id     // Catch:{ Exception -> 0x04ec }
            r12.append(r3)     // Catch:{ Exception -> 0x04ec }
            r12.append(r9)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r1.location     // Catch:{ Exception -> 0x04ec }
            int r3 = r3.local_id     // Catch:{ Exception -> 0x04ec }
            r12.append(r3)     // Catch:{ Exception -> 0x04ec }
            r12.append(r8)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r1.location     // Catch:{ Exception -> 0x04ec }
            long r3 = r3.access_hash     // Catch:{ Exception -> 0x04ec }
            r12.append(r3)     // Catch:{ Exception -> 0x04ec }
            r12.append(r7)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r1.location     // Catch:{ Exception -> 0x04ec }
            long r3 = r3.volume_id     // Catch:{ Exception -> 0x04ec }
            r12.append(r3)     // Catch:{ Exception -> 0x04ec }
            r12.append(r0)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r1.location     // Catch:{ Exception -> 0x04ec }
            long r3 = r0.secret     // Catch:{ Exception -> 0x04ec }
            r12.append(r3)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r0 = r12.toString()     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x04ec }
            goto L_0x040a
        L_0x03df:
            r13 = r3
            r20 = r4
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r0 = r1.webLocation     // Catch:{ Exception -> 0x04ec }
            if (r0 == 0) goto L_0x040a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ec }
            r0.<init>()     // Catch:{ Exception -> 0x04ec }
            java.lang.String r3 = "invalid cdn hash  "
            r0.append(r3)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r3 = r1.webLocation     // Catch:{ Exception -> 0x04ec }
            r0.append(r3)     // Catch:{ Exception -> 0x04ec }
            r0.append(r11)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r3 = r28.getFileName()     // Catch:{ Exception -> 0x04ec }
            r0.append(r3)     // Catch:{ Exception -> 0x04ec }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x04ec }
            goto L_0x040a
        L_0x0407:
            r13 = r3
            r20 = r4
        L_0x040a:
            r3 = 0
            r1.onFail(r3, r3)     // Catch:{ Exception -> 0x04ec }
            java.io.File r0 = r1.cacheFileTemp     // Catch:{ Exception -> 0x04ec }
            r0.delete()     // Catch:{ Exception -> 0x04ec }
            return r3
        L_0x0414:
            r13 = r3
            r20 = r4
            android.util.SparseArray<im.bclpbkiauv.tgnet.TLRPC$TL_fileHash> r0 = r1.cdnHashes     // Catch:{ Exception -> 0x04ec }
            r0.remove(r14)     // Catch:{ Exception -> 0x04ec }
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r1.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ec }
            int r3 = r5 + 1
            r4 = 0
            r1.addPart(r0, r5, r3, r4)     // Catch:{ Exception -> 0x04ec }
            goto L_0x043d
        L_0x0425:
            r18 = r4
            r17 = r6
            r19 = r10
            r6 = r12
            r10 = r13
            goto L_0x043d
        L_0x042e:
            r18 = r4
            r17 = r6
            r19 = r10
            r6 = r12
            r10 = r13
            goto L_0x043d
        L_0x0437:
            r16 = r3
            r18 = r4
            r6 = r12
            r10 = r13
        L_0x043d:
            java.io.RandomAccessFile r0 = r1.fiv     // Catch:{ Exception -> 0x04ec }
            if (r0 == 0) goto L_0x044f
            java.io.RandomAccessFile r0 = r1.fiv     // Catch:{ Exception -> 0x04ec }
            r3 = 0
            r0.seek(r3)     // Catch:{ Exception -> 0x04ec }
            java.io.RandomAccessFile r0 = r1.fiv     // Catch:{ Exception -> 0x04ec }
            byte[] r3 = r1.iv     // Catch:{ Exception -> 0x04ec }
            r0.write(r3)     // Catch:{ Exception -> 0x04ec }
        L_0x044f:
            int r0 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            if (r0 <= 0) goto L_0x046d
            int r0 = r1.state     // Catch:{ Exception -> 0x04ec }
            r3 = 1
            if (r0 != r3) goto L_0x046d
            r28.copyNotLoadedRanges()     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLoadOperation$FileLoadOperationDelegate r0 = r1.delegate     // Catch:{ Exception -> 0x04ec }
            r3 = 1065353216(0x3f800000, float:1.0)
            int r4 = r1.downloadedBytes     // Catch:{ Exception -> 0x04ec }
            float r4 = (float) r4     // Catch:{ Exception -> 0x04ec }
            int r5 = r1.totalBytesCount     // Catch:{ Exception -> 0x04ec }
            float r5 = (float) r5     // Catch:{ Exception -> 0x04ec }
            float r4 = r4 / r5
            float r3 = java.lang.Math.min(r3, r4)     // Catch:{ Exception -> 0x04ec }
            r0.didChangedLoadProgress(r1, r3)     // Catch:{ Exception -> 0x04ec }
        L_0x046d:
            r3 = r18
        L_0x046f:
            r0 = 0
        L_0x0470:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r4 = r1.delayedRequestInfos     // Catch:{ Exception -> 0x04ec }
            int r4 = r4.size()     // Catch:{ Exception -> 0x04ec }
            if (r0 >= r4) goto L_0x04da
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r4 = r1.delayedRequestInfos     // Catch:{ Exception -> 0x04ec }
            java.lang.Object r4 = r4.get(r0)     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo r4 = (im.bclpbkiauv.messenger.FileLoadOperation.RequestInfo) r4     // Catch:{ Exception -> 0x04ec }
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r5 = r1.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ec }
            if (r5 != 0) goto L_0x0490
            int r5 = r1.downloadedBytes     // Catch:{ Exception -> 0x04ec }
            int r7 = r4.offset     // Catch:{ Exception -> 0x04ec }
            if (r5 != r7) goto L_0x048d
            goto L_0x0490
        L_0x048d:
            int r0 = r0 + 1
            goto L_0x0470
        L_0x0490:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r5 = r1.delayedRequestInfos     // Catch:{ Exception -> 0x04ec }
            r5.remove(r0)     // Catch:{ Exception -> 0x04ec }
            r5 = 0
            boolean r5 = r1.processRequestResult(r4, r5)     // Catch:{ Exception -> 0x04ec }
            if (r5 != 0) goto L_0x04da
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r5 = r4.response     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x04b1
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r5 = r4.response     // Catch:{ Exception -> 0x04ec }
            r7 = 0
            r5.disableFree = r7     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r5 = r4.response     // Catch:{ Exception -> 0x04ec }
            r5.freeResources()     // Catch:{ Exception -> 0x04ec }
            goto L_0x04da
        L_0x04b1:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_webFile r5 = r4.responseWeb     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x04c6
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_webFile r5 = r4.responseWeb     // Catch:{ Exception -> 0x04ec }
            r7 = 0
            r5.disableFree = r7     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_webFile r5 = r4.responseWeb     // Catch:{ Exception -> 0x04ec }
            r5.freeResources()     // Catch:{ Exception -> 0x04ec }
            goto L_0x04da
        L_0x04c6:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r5 = r4.responseCdn     // Catch:{ Exception -> 0x04ec }
            if (r5 == 0) goto L_0x04da
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r5 = r4.responseCdn     // Catch:{ Exception -> 0x04ec }
            r7 = 0
            r5.disableFree = r7     // Catch:{ Exception -> 0x04ec }
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_cdnFile r5 = r4.responseCdn     // Catch:{ Exception -> 0x04ec }
            r5.freeResources()     // Catch:{ Exception -> 0x04ec }
        L_0x04da:
            if (r3 == 0) goto L_0x04e1
            r0 = 1
            r1.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x04ec }
            goto L_0x04f4
        L_0x04e1:
            r28.startDownloadRequest()     // Catch:{ Exception -> 0x04ec }
            goto L_0x04f4
        L_0x04e5:
            r6 = r12
        L_0x04e6:
            r0 = 1
            r1.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x04ec }
            r3 = 0
            return r3
        L_0x04ec:
            r0 = move-exception
            r3 = 0
            r1.onFail(r3, r3)
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04f4:
            r3 = 0
            goto L_0x05f2
        L_0x04f7:
            java.lang.String r3 = r2.text
            java.lang.String r4 = "FILE_MIGRATE_"
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x0536
            java.lang.String r0 = r2.text
            java.lang.String r3 = ""
            java.lang.String r4 = r0.replace(r4, r3)
            java.util.Scanner r0 = new java.util.Scanner
            r0.<init>(r4)
            r5 = r0
            r5.useDelimiter(r3)
            int r0 = r5.nextInt()     // Catch:{ Exception -> 0x051b }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x051b }
            goto L_0x051e
        L_0x051b:
            r0 = move-exception
            r3 = 0
            r0 = r3
        L_0x051e:
            if (r0 != 0) goto L_0x0525
            r3 = 0
            r1.onFail(r3, r3)
            goto L_0x0533
        L_0x0525:
            r3 = 0
            int r6 = r0.intValue()
            r1.datacenterId = r6
            r1.downloadedBytes = r3
            r1.requestedBytesCount = r3
            r28.startDownloadRequest()
        L_0x0533:
            r3 = 0
            goto L_0x05f2
        L_0x0536:
            java.lang.String r3 = r2.text
            java.lang.String r4 = "OFFSET_INVALID"
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x0560
            int r0 = r1.downloadedBytes
            int r3 = r1.currentDownloadChunkSize
            int r0 = r0 % r3
            if (r0 != 0) goto L_0x055a
            r0 = 1
            r1.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x054e }
            r3 = 0
            goto L_0x05f2
        L_0x054e:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r3 = 0
            r1.onFail(r3, r3)
            goto L_0x05f2
        L_0x055a:
            r3 = 0
            r1.onFail(r3, r3)
            goto L_0x05f2
        L_0x0560:
            r3 = 0
            java.lang.String r4 = r2.text
            java.lang.String r5 = "RETRY_LIMIT"
            boolean r4 = r4.contains(r5)
            if (r4 == 0) goto L_0x0571
            r0 = 2
            r1.onFail(r3, r0)
            goto L_0x05f2
        L_0x0571:
            boolean r3 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x05ee
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r3 = r1.location
            java.lang.String r4 = " "
            if (r3 == 0) goto L_0x05c7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = r2.text
            r3.append(r5)
            r3.append(r4)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r4 = r1.location
            r3.append(r4)
            r3.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r4 = r1.location
            long r4 = r4.id
            r3.append(r4)
            r3.append(r9)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r4 = r1.location
            int r4 = r4.local_id
            r3.append(r4)
            r3.append(r8)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r4 = r1.location
            long r4 = r4.access_hash
            r3.append(r4)
            r3.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r4 = r1.location
            long r4 = r4.volume_id
            r3.append(r4)
            r3.append(r0)
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r0 = r1.location
            long r4 = r0.secret
            r3.append(r4)
            java.lang.String r0 = r3.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x05ee
        L_0x05c7:
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r0 = r1.webLocation
            if (r0 == 0) goto L_0x05ee
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = r2.text
            r0.append(r3)
            r0.append(r4)
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r3 = r1.webLocation
            r0.append(r3)
            r0.append(r11)
            java.lang.String r3 = r28.getFileName()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r0)
        L_0x05ee:
            r3 = 0
            r1.onFail(r3, r3)
        L_0x05f2:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileLoadOperation.processRequestResult(im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo, im.bclpbkiauv.tgnet.TLRPC$TL_error):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFail(boolean thread, int reason) {
        cleanup();
        this.state = 2;
        if (thread) {
            Utilities.stageQueue.postRunnable(new Runnable(reason) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoadOperation.this.lambda$onFail$10$FileLoadOperation(this.f$1);
                }
            });
        } else {
            this.delegate.didFailedLoadingFile(this, reason);
        }
    }

    public /* synthetic */ void lambda$onFail$10$FileLoadOperation(int reason) {
        this.delegate.didFailedLoadingFile(this, reason);
    }

    private void clearOperaion(RequestInfo currentInfo, boolean preloadChanged) {
        int minOffset = Integer.MAX_VALUE;
        for (int a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            }
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int a2 = 0; a2 < this.delayedRequestInfos.size(); a2++) {
            RequestInfo info2 = this.delayedRequestInfos.get(a2);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info2.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info2.offset, info2.offset + this.currentDownloadChunkSize);
            }
            if (info2.response != null) {
                info2.response.disableFree = false;
                info2.response.freeResources();
            } else if (info2.responseWeb != null) {
                info2.responseWeb.disableFree = false;
                info2.responseWeb.freeResources();
            } else if (info2.responseCdn != null) {
                info2.responseCdn.disableFree = false;
                info2.responseCdn.freeResources();
            }
            minOffset = Math.min(info2.offset, minOffset);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!preloadChanged && this.isPreloadVideoOperation) {
            this.requestedBytesCount = this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = minOffset;
            this.requestedBytesCount = minOffset;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
            this.requestingReference = true;
            Object obj = this.parentObject;
            if (obj instanceof MessageObject) {
                MessageObject messageObject = (MessageObject) obj;
                if (messageObject.getId() < 0 && messageObject.messageOwner.media.webpage != null) {
                    this.parentObject = messageObject.messageOwner.media.webpage;
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r19 = this;
            r1 = r19
            boolean r0 = r1.paused
            if (r0 != 0) goto L_0x02c9
            int r0 = r1.state
            r2 = 1
            if (r0 != r2) goto L_0x02c9
            int r0 = r1.streamPriorityStartOffset
            r3 = 2097152(0x200000, float:2.938736E-39)
            if (r0 != 0) goto L_0x003c
            boolean r0 = r1.nextPartWasPreloaded
            if (r0 != 0) goto L_0x0026
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            int r0 = r0.size()
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r4 = r1.delayedRequestInfos
            int r4 = r4.size()
            int r0 = r0 + r4
            int r4 = r1.currentMaxDownloadRequests
            if (r0 >= r4) goto L_0x02c9
        L_0x0026:
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 == 0) goto L_0x003c
            int r0 = r1.requestedBytesCount
            if (r0 > r3) goto L_0x02c9
            int r0 = r1.moovFound
            if (r0 == 0) goto L_0x003c
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x003c
            goto L_0x02c9
        L_0x003c:
            r0 = 1
            int r4 = r1.streamPriorityStartOffset
            r5 = 0
            if (r4 != 0) goto L_0x0061
            boolean r4 = r1.nextPartWasPreloaded
            if (r4 != 0) goto L_0x0061
            boolean r4 = r1.isPreloadVideoOperation
            if (r4 == 0) goto L_0x004e
            int r4 = r1.moovFound
            if (r4 == 0) goto L_0x0061
        L_0x004e:
            int r4 = r1.totalBytesCount
            if (r4 <= 0) goto L_0x0061
            int r4 = r1.currentMaxDownloadRequests
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r6 = r1.requestInfos
            int r6 = r6.size()
            int r4 = r4 - r6
            int r0 = java.lang.Math.max(r5, r4)
            r4 = r0
            goto L_0x0062
        L_0x0061:
            r4 = r0
        L_0x0062:
            r0 = 0
            r6 = r0
        L_0x0064:
            if (r6 >= r4) goto L_0x02c8
            boolean r0 = r1.isPreloadVideoOperation
            r7 = 2
            if (r0 == 0) goto L_0x00fd
            int r0 = r1.moovFound
            if (r0 == 0) goto L_0x0074
            int r0 = r1.preloadNotRequestedBytesCount
            if (r0 > 0) goto L_0x0074
            return
        L_0x0074:
            int r0 = r1.nextPreloadDownloadOffset
            r8 = -1
            if (r0 != r8) goto L_0x00b4
            r0 = 0
            r8 = 0
            int r9 = r1.currentDownloadChunkSize
            int r9 = r3 / r9
            int r9 = r9 + r7
        L_0x0080:
            if (r9 == 0) goto L_0x00a6
            android.util.SparseIntArray r10 = r1.requestedPreloadedBytesRanges
            int r10 = r10.get(r0, r5)
            if (r10 != 0) goto L_0x008c
            r8 = 1
            goto L_0x00a6
        L_0x008c:
            int r10 = r1.currentDownloadChunkSize
            int r0 = r0 + r10
            int r11 = r1.totalBytesCount
            if (r0 <= r11) goto L_0x0094
            goto L_0x00a6
        L_0x0094:
            int r12 = r1.moovFound
            if (r12 != r7) goto L_0x00a3
            int r12 = r10 * 8
            if (r0 != r12) goto L_0x00a3
            r12 = 1048576(0x100000, float:1.469368E-39)
            int r11 = r11 - r12
            int r11 = r11 / r10
            int r11 = r11 * r10
            r0 = r11
        L_0x00a3:
            int r9 = r9 + -1
            goto L_0x0080
        L_0x00a6:
            if (r8 != 0) goto L_0x00b3
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r10 = r1.requestInfos
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x00b3
            r1.onFinishLoadingFile(r5)
        L_0x00b3:
            goto L_0x00b6
        L_0x00b4:
            int r0 = r1.nextPreloadDownloadOffset
        L_0x00b6:
            android.util.SparseIntArray r8 = r1.requestedPreloadedBytesRanges
            if (r8 != 0) goto L_0x00c1
            android.util.SparseIntArray r8 = new android.util.SparseIntArray
            r8.<init>()
            r1.requestedPreloadedBytesRanges = r8
        L_0x00c1:
            android.util.SparseIntArray r8 = r1.requestedPreloadedBytesRanges
            r8.put(r0, r2)
            boolean r8 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r8 == 0) goto L_0x00f3
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "start next preload from "
            r8.append(r9)
            r8.append(r0)
            java.lang.String r9 = " size "
            r8.append(r9)
            int r9 = r1.totalBytesCount
            r8.append(r9)
            java.lang.String r9 = " for "
            r8.append(r9)
            java.io.File r9 = r1.cacheFilePreload
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            im.bclpbkiauv.messenger.FileLog.d(r8)
        L_0x00f3:
            int r8 = r1.preloadNotRequestedBytesCount
            int r9 = r1.currentDownloadChunkSize
            int r8 = r8 - r9
            r1.preloadNotRequestedBytesCount = r8
            r8 = r0
            goto L_0x015e
        L_0x00fd:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r1.notRequestedBytesRanges
            if (r0 == 0) goto L_0x015b
            int r0 = r1.streamPriorityStartOffset
            if (r0 == 0) goto L_0x0106
            goto L_0x0108
        L_0x0106:
            int r0 = r1.streamStartOffset
        L_0x0108:
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r8 = r1.notRequestedBytesRanges
            int r8 = r8.size()
            r9 = 2147483647(0x7fffffff, float:NaN)
            r10 = 2147483647(0x7fffffff, float:NaN)
            r11 = 0
        L_0x0115:
            if (r11 >= r8) goto L_0x014d
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r12 = r1.notRequestedBytesRanges
            java.lang.Object r12 = r12.get(r11)
            im.bclpbkiauv.messenger.FileLoadOperation$Range r12 = (im.bclpbkiauv.messenger.FileLoadOperation.Range) r12
            if (r0 == 0) goto L_0x0142
            int r13 = r12.start
            if (r13 > r0) goto L_0x0132
            int r13 = r12.end
            if (r13 <= r0) goto L_0x0132
            r10 = r0
            r9 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x014d
        L_0x0132:
            int r13 = r12.start
            if (r0 >= r13) goto L_0x0142
            int r13 = r12.start
            if (r13 >= r10) goto L_0x0142
            int r10 = r12.start
        L_0x0142:
            int r13 = r12.start
            int r9 = java.lang.Math.min(r9, r13)
            int r11 = r11 + 1
            goto L_0x0115
        L_0x014d:
            r11 = 2147483647(0x7fffffff, float:NaN)
            if (r10 == r11) goto L_0x0155
            r11 = r10
            r0 = r11
            goto L_0x0159
        L_0x0155:
            if (r9 == r11) goto L_0x02c8
            r11 = r9
            r0 = r11
        L_0x0159:
            r8 = r0
            goto L_0x015e
        L_0x015b:
            int r0 = r1.requestedBytesCount
            r8 = r0
        L_0x015e:
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 != 0) goto L_0x016c
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$Range> r0 = r1.notRequestedBytesRanges
            if (r0 == 0) goto L_0x016c
            int r9 = r1.currentDownloadChunkSize
            int r9 = r9 + r8
            r1.addPart(r0, r8, r9, r5)
        L_0x016c:
            int r0 = r1.totalBytesCount
            if (r0 <= 0) goto L_0x0174
            if (r8 < r0) goto L_0x0174
            goto L_0x02c8
        L_0x0174:
            int r0 = r1.totalBytesCount
            if (r0 <= 0) goto L_0x0187
            int r9 = r4 + -1
            if (r6 == r9) goto L_0x0187
            if (r0 <= 0) goto L_0x0184
            int r9 = r1.currentDownloadChunkSize
            int r9 = r9 + r8
            if (r9 < r0) goto L_0x0184
            goto L_0x0187
        L_0x0184:
            r18 = 0
            goto L_0x0189
        L_0x0187:
            r18 = 1
        L_0x0189:
            int r0 = r1.requestsCount
            int r0 = r0 % r7
            if (r0 != 0) goto L_0x0191
            r17 = 2
            goto L_0x0197
        L_0x0191:
            r7 = 65538(0x10002, float:9.1838E-41)
            r17 = 65538(0x10002, float:9.1838E-41)
        L_0x0197:
            boolean r0 = r1.isForceRequest
            if (r0 == 0) goto L_0x019e
            r0 = 32
            goto L_0x019f
        L_0x019e:
            r0 = 0
        L_0x019f:
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r7 = r1.webLocation
            boolean r7 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputWebFileGeoPointLocation
            if (r7 != 0) goto L_0x01a7
            r0 = r0 | 2
        L_0x01a7:
            boolean r7 = r1.isCdn
            if (r7 == 0) goto L_0x01bf
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_getCdnFile r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_getCdnFile
            r7.<init>()
            byte[] r9 = r1.cdnToken
            r7.file_token = r9
            r7.offset = r8
            int r9 = r1.currentDownloadChunkSize
            r7.limit = r9
            r9 = r7
            r0 = r0 | 1
            r7 = r0
            goto L_0x020a
        L_0x01bf:
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r7 = r1.webLocation
            if (r7 == 0) goto L_0x01d5
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_getWebFile r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_getWebFile
            r7.<init>()
            im.bclpbkiauv.tgnet.TLRPC$InputWebFileLocation r9 = r1.webLocation
            r7.location = r9
            r7.offset = r8
            int r9 = r1.currentDownloadChunkSize
            r7.limit = r9
            r9 = r7
            r7 = r0
            goto L_0x020a
        L_0x01d5:
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_getFile
            r7.<init>()
            int r9 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r9 = im.bclpbkiauv.messenger.MessagesController.getInstance(r9)
            int r10 = r1.currentAccount
            im.bclpbkiauv.messenger.UserConfig r10 = im.bclpbkiauv.messenger.UserConfig.getInstance(r10)
            int r10 = r10.getClientUserId()
            im.bclpbkiauv.tgnet.TLRPC$UserFull r9 = r9.getUserFull(r10)
            boolean r10 = r9 instanceof im.bclpbkiauv.tgnet.TLRPCContacts.CL_userFull_v1
            if (r10 == 0) goto L_0x01fd
            r10 = r9
            im.bclpbkiauv.tgnet.TLRPCContacts$CL_userFull_v1 r10 = (im.bclpbkiauv.tgnet.TLRPCContacts.CL_userFull_v1) r10
            im.bclpbkiauv.tgnet.TLRPCContacts$CL_userFull_v1_Bean r10 = r10.getExtendBean()
            boolean r10 = r10.vip
            r7.isCdnVip = r10
        L_0x01fd:
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r10 = r1.location
            r7.location = r10
            r7.offset = r8
            int r10 = r1.currentDownloadChunkSize
            r7.limit = r10
            r10 = r7
            r7 = r0
            r9 = r10
        L_0x020a:
            int r0 = r1.requestedBytesCount
            int r10 = r1.currentDownloadChunkSize
            int r0 = r0 + r10
            r1.requestedBytesCount = r0
            im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo r0 = new im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo
            r0.<init>()
            r15 = r0
            java.util.ArrayList<im.bclpbkiauv.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            r0.add(r15)
            int unused = r15.offset = r8
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0279
            boolean r0 = r1.supportsPreloading
            if (r0 == 0) goto L_0x0279
            java.io.RandomAccessFile r0 = r1.preloadStream
            if (r0 == 0) goto L_0x0279
            android.util.SparseArray<im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange> r0 = r1.preloadedBytesRanges
            if (r0 == 0) goto L_0x0279
            int r10 = r15.offset
            java.lang.Object r0 = r0.get(r10)
            r10 = r0
            im.bclpbkiauv.messenger.FileLoadOperation$PreloadRange r10 = (im.bclpbkiauv.messenger.FileLoadOperation.PreloadRange) r10
            if (r10 == 0) goto L_0x0279
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_upload_file
            r0.<init>()
            im.bclpbkiauv.tgnet.TLRPC.TL_upload_file unused = r15.response = r0
            im.bclpbkiauv.tgnet.NativeByteBuffer r0 = new im.bclpbkiauv.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0278 }
            int r11 = r10.length     // Catch:{ Exception -> 0x0278 }
            r0.<init>((int) r11)     // Catch:{ Exception -> 0x0278 }
            java.io.RandomAccessFile r11 = r1.preloadStream     // Catch:{ Exception -> 0x0278 }
            int r12 = r10.fileOffset     // Catch:{ Exception -> 0x0278 }
            long r12 = (long) r12     // Catch:{ Exception -> 0x0278 }
            r11.seek(r12)     // Catch:{ Exception -> 0x0278 }
            java.io.RandomAccessFile r11 = r1.preloadStream     // Catch:{ Exception -> 0x0278 }
            java.nio.channels.FileChannel r11 = r11.getChannel()     // Catch:{ Exception -> 0x0278 }
            java.nio.ByteBuffer r12 = r0.buffer     // Catch:{ Exception -> 0x0278 }
            r11.read(r12)     // Catch:{ Exception -> 0x0278 }
            java.nio.ByteBuffer r11 = r0.buffer     // Catch:{ Exception -> 0x0278 }
            r11.position(r5)     // Catch:{ Exception -> 0x0278 }
            im.bclpbkiauv.tgnet.TLRPC$TL_upload_file r11 = r15.response     // Catch:{ Exception -> 0x0278 }
            r11.bytes = r0     // Catch:{ Exception -> 0x0278 }
            im.bclpbkiauv.messenger.DispatchQueue r11 = im.bclpbkiauv.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x0278 }
            im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$NKQ0_AR7O2eOODZpDYihaqQo60o r12 = new im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$NKQ0_AR7O2eOODZpDYihaqQo60o     // Catch:{ Exception -> 0x0278 }
            r12.<init>(r15)     // Catch:{ Exception -> 0x0278 }
            r11.postRunnable(r12)     // Catch:{ Exception -> 0x0278 }
            goto L_0x02c2
        L_0x0278:
            r0 = move-exception
        L_0x0279:
            int r0 = r1.streamPriorityStartOffset
            if (r0 == 0) goto L_0x029b
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0297
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "frame get offset = "
            r0.append(r10)
            int r10 = r1.streamPriorityStartOffset
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0297:
            r1.streamPriorityStartOffset = r5
            r1.priorityRequestInfo = r15
        L_0x029b:
            int r0 = r1.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r10 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r0)
            im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$1pwuUjdEMePLXadf9NVku_QQFDg r12 = new im.bclpbkiauv.messenger.-$$Lambda$FileLoadOperation$1pwuUjdEMePLXadf9NVku_QQFDg
            r12.<init>(r15, r9)
            r13 = 0
            r14 = 0
            boolean r0 = r1.isCdn
            if (r0 == 0) goto L_0x02af
            int r0 = r1.cdnDatacenterId
            goto L_0x02b1
        L_0x02af:
            int r0 = r1.datacenterId
        L_0x02b1:
            r16 = r0
            r11 = r9
            r3 = r15
            r15 = r7
            int r0 = r10.sendRequest(r11, r12, r13, r14, r15, r16, r17, r18)
            int unused = r3.requestToken = r0
            int r0 = r1.requestsCount
            int r0 = r0 + r2
            r1.requestsCount = r0
        L_0x02c2:
            int r6 = r6 + 1
            r3 = 2097152(0x200000, float:2.938736E-39)
            goto L_0x0064
        L_0x02c8:
            return
        L_0x02c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    public /* synthetic */ void lambda$startDownloadRequest$11$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC.TL_error) null);
        requestInfo.response.freeResources();
    }

    public /* synthetic */ void lambda$startDownloadRequest$13$FileLoadOperation(RequestInfo requestInfo, TLObject request, TLObject response, TLRPC.TL_error error) {
        if (this.requestInfos.contains(requestInfo)) {
            if (requestInfo == this.priorityRequestInfo) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (error != null) {
                if (FileRefController.isFileRefError(error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((request instanceof TLRPC.TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
                    startDownloadRequest();
                    return;
                }
            }
            if (response instanceof TLRPC.TL_upload_fileCdnRedirect) {
                TLRPC.TL_upload_fileCdnRedirect res = (TLRPC.TL_upload_fileCdnRedirect) response;
                if (!res.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new SparseArray<>();
                    }
                    for (int a1 = 0; a1 < res.file_hashes.size(); a1++) {
                        TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) res.file_hashes.get(a1);
                        this.cdnHashes.put(hash.offset, hash);
                    }
                }
                if (res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16 || res.encryption_key.length != 32) {
                    TLRPC.TL_error error2 = new TLRPC.TL_error();
                    error2.text = "bad redirect response";
                    error2.code = 400;
                    processRequestResult(requestInfo, error2);
                    return;
                }
                this.isCdn = true;
                if (this.notCheckedCdnRanges == null) {
                    ArrayList<Range> arrayList = new ArrayList<>();
                    this.notCheckedCdnRanges = arrayList;
                    arrayList.add(new Range(0, maxCdnParts));
                }
                this.cdnDatacenterId = res.dc_id;
                this.cdnIv = res.encryption_iv;
                this.cdnKey = res.encryption_key;
                this.cdnToken = res.file_token;
                clearOperaion(requestInfo, false);
                startDownloadRequest();
            } else if (!(response instanceof TLRPC.TL_upload_cdnFileReuploadNeeded)) {
                if (response instanceof TLRPC.TL_upload_file) {
                    TLRPC.TL_upload_file unused = requestInfo.response = (TLRPC.TL_upload_file) response;
                } else if (response instanceof TLRPC.TL_upload_webFile) {
                    TLRPC.TL_upload_webFile unused2 = requestInfo.responseWeb = (TLRPC.TL_upload_webFile) response;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = requestInfo.responseWeb.size;
                    }
                } else {
                    TLRPC.TL_upload_cdnFile unused3 = requestInfo.responseCdn = (TLRPC.TL_upload_cdnFile) response;
                }
                if (response != null) {
                    int i = this.currentType;
                    if (i == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                    } else if (i == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                    } else if (i == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                    } else if (i == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TLRPC.TL_upload_reuploadCdnFile req = new TLRPC.TL_upload_reuploadCdnFile();
                req.file_token = this.cdnToken;
                req.request_token = ((TLRPC.TL_upload_cdnFileReuploadNeeded) response).request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate(requestInfo) {
                    private final /* synthetic */ FileLoadOperation.RequestInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileLoadOperation.this.lambda$null$12$FileLoadOperation(this.f$1, tLObject, tL_error);
                    }
                }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$12$FileLoadOperation(RequestInfo requestInfo, TLObject response1, TLRPC.TL_error error1) {
        this.reuploadingCdn = false;
        if (error1 == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response1;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray<>();
                }
                for (int a1 = 0; a1 < vector.objects.size(); a1++) {
                    TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a1);
                    this.cdnHashes.put(hash.offset, hash);
                }
            }
            startDownloadRequest();
        } else if (error1.text.equals("FILE_TOKEN_INVALID") || error1.text.equals("REQUEST_TOKEN_INVALID")) {
            this.isCdn = false;
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate2) {
        this.delegate = delegate2;
    }
}
