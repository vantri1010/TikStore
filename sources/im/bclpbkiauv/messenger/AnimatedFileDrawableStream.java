package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.concurrent.CountDownLatch;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC.Document document;
    private int lastOffset;
    private FileLoadOperation loadOperation;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC.Document d, Object p, int a, boolean prev) {
        this.document = d;
        this.parentObject = p;
        this.currentAccount = a;
        this.preview = prev;
        this.loadOperation = FileLoader.getInstance(a).loadStreamFile(this, this.document, this.parentObject, 0, this.preview);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        if (r0 != 0) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0 = r9.loadOperation.getDownloadedLengthFromOffset(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0018, code lost:
        if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0020, code lost:
        if (r9.loadOperation.isPaused() != false) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0024, code lost:
        if (r9.lastOffset != r10) goto L_0x002a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0028, code lost:
        if (r9.preview == false) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002a, code lost:
        im.bclpbkiauv.messenger.FileLoader.getInstance(r9.currentAccount).loadStreamFile(r9, r9.document, r9.parentObject, r10, r9.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003b, code lost:
        r1 = r9.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003d, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0040, code lost:
        if (r9.canceled == false) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0042, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0043, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0044, code lost:
        r9.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004c, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004f, code lost:
        if (r9.preview != false) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0051, code lost:
        im.bclpbkiauv.messenger.FileLoader.getInstance(r9.currentAccount).setLoadingVideo(r9.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x005c, code lost:
        r9.waitingForLoad = true;
        r9.countDownLatch.await();
        r9.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0069, code lost:
        r9.lastOffset = r10 + r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x006e, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x006f, code lost:
        im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0072, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        if (r11 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r10, int r11) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.sync
            monitor-enter(r0)
            boolean r1 = r9.canceled     // Catch:{ all -> 0x0073 }
            r2 = 0
            if (r1 == 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            if (r11 != 0) goto L_0x000e
            return r2
        L_0x000e:
            r0 = 0
        L_0x000f:
            if (r0 != 0) goto L_0x0069
            im.bclpbkiauv.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x006e }
            int r1 = r1.getDownloadedLengthFromOffset(r10, r11)     // Catch:{ Exception -> 0x006e }
            r0 = r1
            if (r0 != 0) goto L_0x000f
            im.bclpbkiauv.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x006e }
            boolean r1 = r1.isPaused()     // Catch:{ Exception -> 0x006e }
            if (r1 != 0) goto L_0x002a
            int r1 = r9.lastOffset     // Catch:{ Exception -> 0x006e }
            if (r1 != r10) goto L_0x002a
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x006e }
            if (r1 == 0) goto L_0x003b
        L_0x002a:
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x006e }
            im.bclpbkiauv.messenger.FileLoader r3 = im.bclpbkiauv.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x006e }
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r9.document     // Catch:{ Exception -> 0x006e }
            java.lang.Object r6 = r9.parentObject     // Catch:{ Exception -> 0x006e }
            boolean r8 = r9.preview     // Catch:{ Exception -> 0x006e }
            r4 = r9
            r7 = r10
            r3.loadStreamFile(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x006e }
        L_0x003b:
            java.lang.Object r1 = r9.sync     // Catch:{ Exception -> 0x006e }
            monitor-enter(r1)     // Catch:{ Exception -> 0x006e }
            boolean r3 = r9.canceled     // Catch:{ all -> 0x0066 }
            if (r3 == 0) goto L_0x0044
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            return r2
        L_0x0044:
            java.util.concurrent.CountDownLatch r3 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0066 }
            r4 = 1
            r3.<init>(r4)     // Catch:{ all -> 0x0066 }
            r9.countDownLatch = r3     // Catch:{ all -> 0x0066 }
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x006e }
            if (r1 != 0) goto L_0x005c
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x006e }
            im.bclpbkiauv.messenger.FileLoader r1 = im.bclpbkiauv.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x006e }
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r9.document     // Catch:{ Exception -> 0x006e }
            r1.setLoadingVideo(r3, r2, r4)     // Catch:{ Exception -> 0x006e }
        L_0x005c:
            r9.waitingForLoad = r4     // Catch:{ Exception -> 0x006e }
            java.util.concurrent.CountDownLatch r1 = r9.countDownLatch     // Catch:{ Exception -> 0x006e }
            r1.await()     // Catch:{ Exception -> 0x006e }
            r9.waitingForLoad = r2     // Catch:{ Exception -> 0x006e }
            goto L_0x000f
        L_0x0066:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0066 }
            throw r2     // Catch:{ Exception -> 0x006e }
        L_0x0069:
            int r1 = r10 + r0
            r9.lastOffset = r1     // Catch:{ Exception -> 0x006e }
            goto L_0x0072
        L_0x006e:
            r1 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0072:
            return r0
        L_0x0073:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0073 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean removeLoading) {
        synchronized (this.sync) {
            if (this.countDownLatch != null) {
                this.countDownLatch.countDown();
                if (removeLoading && !this.canceled && !this.preview) {
                    FileLoader.getInstance(this.currentAccount).removeLoadingVideo(this.document, false, true);
                }
            }
            this.canceled = true;
        }
    }

    public void reset() {
        synchronized (this.sync) {
            this.canceled = false;
        }
    }

    public TLRPC.Document getDocument() {
        return this.document;
    }

    public Object getParentObject() {
        return this.document;
    }

    public boolean isPreview() {
        return this.preview;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public boolean isWaitingForLoad() {
        return this.waitingForLoad;
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch2 = this.countDownLatch;
        if (countDownLatch2 != null) {
            countDownLatch2.countDown();
        }
    }
}
