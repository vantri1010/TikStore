package im.bclpbkiauv.messenger;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.tgnet.TLRPC;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

public class FileStreamLoadOperation extends BaseDataSource implements FileLoadOperationStream {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int currentOffset;
    private TLRPC.Document document;
    private RandomAccessFile file;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Object parentObject;
    private Uri uri;

    public FileStreamLoadOperation() {
        super(false);
    }

    @Deprecated
    public FileStreamLoadOperation(TransferListener listener) {
        this();
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    public long open(DataSpec dataSpec) throws IOException {
        Uri uri2 = dataSpec.uri;
        this.uri = uri2;
        int intValue = Utilities.parseInt(uri2.getQueryParameter("account")).intValue();
        this.currentAccount = intValue;
        this.parentObject = FileLoader.getInstance(intValue).getParentObject(Utilities.parseInt(this.uri.getQueryParameter("rid")).intValue());
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        this.document = tL_document;
        tL_document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.document.id = Utilities.parseLong(this.uri.getQueryParameter(TtmlNode.ATTR_ID)).longValue();
        this.document.size = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        this.document.dc_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        this.document.mime_type = this.uri.getQueryParameter("mime");
        this.document.file_reference = Utilities.hexToBytes(this.uri.getQueryParameter("reference"));
        TLRPC.TL_documentAttributeFilename filename = new TLRPC.TL_documentAttributeFilename();
        filename.file_name = this.uri.getQueryParameter("name");
        this.document.attributes.add(filename);
        if (this.document.mime_type.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
            this.document.attributes.add(new TLRPC.TL_documentAttributeVideo());
        } else if (this.document.mime_type.startsWith(MimeTypes.BASE_TYPE_AUDIO)) {
            this.document.attributes.add(new TLRPC.TL_documentAttributeAudio());
        }
        FileLoader instance = FileLoader.getInstance(this.currentAccount);
        TLRPC.Document document2 = this.document;
        Object obj = this.parentObject;
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = instance.loadStreamFile(this, document2, obj, i, false);
        long j = dataSpec.length == -1 ? ((long) this.document.size) - dataSpec.position : dataSpec.length;
        this.bytesRemaining = j;
        if (j >= 0) {
            this.opened = true;
            transferStarted(dataSpec);
            if (this.loadOperation != null) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(this.loadOperation.getCurrentFile(), "r");
                this.file = randomAccessFile;
                randomAccessFile.seek((long) this.currentOffset);
            }
            return this.bytesRemaining;
        }
        throw new EOFException();
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        int availableLength = 0;
        if (j < ((long) readLength)) {
            readLength = (int) j;
        }
        while (availableLength == 0) {
            try {
                if (!this.opened) {
                    break;
                }
                availableLength = this.loadOperation.getDownloadedLengthFromOffset(this.currentOffset, readLength);
                if (availableLength == 0) {
                    FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, this.currentOffset, false);
                    CountDownLatch countDownLatch2 = new CountDownLatch(1);
                    this.countDownLatch = countDownLatch2;
                    countDownLatch2.await();
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        if (!this.opened) {
            return 0;
        }
        this.file.readFully(buffer, offset, availableLength);
        this.currentOffset += availableLength;
        this.bytesRemaining -= (long) availableLength;
        bytesTransferred(availableLength);
        return availableLength;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        FileLoadOperation fileLoadOperation = this.loadOperation;
        if (fileLoadOperation != null) {
            fileLoadOperation.removeStreamListener(this);
        }
        RandomAccessFile randomAccessFile = this.file;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            transferEnded();
        }
        CountDownLatch countDownLatch2 = this.countDownLatch;
        if (countDownLatch2 != null) {
            countDownLatch2.countDown();
        }
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch2 = this.countDownLatch;
        if (countDownLatch2 != null) {
            countDownLatch2.countDown();
        }
    }
}
