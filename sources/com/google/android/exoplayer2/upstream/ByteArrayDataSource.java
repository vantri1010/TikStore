package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class ByteArrayDataSource extends BaseDataSource {
    private int bytesRemaining;
    private final byte[] data;
    private boolean opened;
    private int readPosition;
    private Uri uri;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ByteArrayDataSource(byte[] data2) {
        super(false);
        boolean z = false;
        Assertions.checkNotNull(data2);
        Assertions.checkArgument(data2.length > 0 ? true : z);
        this.data = data2;
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        transferInitializing(dataSpec);
        this.readPosition = (int) dataSpec.position;
        int length = (int) (dataSpec.length == -1 ? ((long) this.data.length) - dataSpec.position : dataSpec.length);
        this.bytesRemaining = length;
        if (length <= 0 || this.readPosition + length > this.data.length) {
            throw new IOException("Unsatisfiable range: [" + this.readPosition + ", " + dataSpec.length + "], length: " + this.data.length);
        }
        this.opened = true;
        transferStarted(dataSpec);
        return (long) this.bytesRemaining;
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        int i = this.bytesRemaining;
        if (i == 0) {
            return -1;
        }
        int readLength2 = Math.min(readLength, i);
        System.arraycopy(this.data, this.readPosition, buffer, offset, readLength2);
        this.readPosition += readLength2;
        this.bytesRemaining -= readLength2;
        bytesTransferred(readLength2);
        return readLength2;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() throws IOException {
        if (this.opened) {
            this.opened = false;
            transferEnded();
        }
        this.uri = null;
    }
}
