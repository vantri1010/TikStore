package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import android.util.Base64;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.net.URLDecoder;

public final class DataSchemeDataSource extends BaseDataSource {
    public static final String SCHEME_DATA = "data";
    private int bytesRead;
    private byte[] data;
    private DataSpec dataSpec;

    public DataSchemeDataSource() {
        super(false);
    }

    public long open(DataSpec dataSpec2) throws IOException {
        transferInitializing(dataSpec2);
        this.dataSpec = dataSpec2;
        Uri uri = dataSpec2.uri;
        String scheme = uri.getScheme();
        if ("data".equals(scheme)) {
            String[] uriParts = Util.split(uri.getSchemeSpecificPart(), ",");
            if (uriParts.length == 2) {
                String dataString = uriParts[1];
                if (uriParts[0].contains(";base64")) {
                    try {
                        this.data = Base64.decode(dataString, 0);
                    } catch (IllegalArgumentException e) {
                        throw new ParserException("Error while parsing Base64 encoded string: " + dataString, e);
                    }
                } else {
                    this.data = Util.getUtf8Bytes(URLDecoder.decode(dataString, C.ASCII_NAME));
                }
                transferStarted(dataSpec2);
                return (long) this.data.length;
            }
            throw new ParserException("Unexpected URI format: " + uri);
        }
        throw new ParserException("Unsupported scheme: " + scheme);
    }

    public int read(byte[] buffer, int offset, int readLength) {
        if (readLength == 0) {
            return 0;
        }
        int remainingBytes = this.data.length - this.bytesRead;
        if (remainingBytes == 0) {
            return -1;
        }
        int readLength2 = Math.min(readLength, remainingBytes);
        System.arraycopy(this.data, this.bytesRead, buffer, offset, readLength2);
        this.bytesRead += readLength2;
        bytesTransferred(readLength2);
        return readLength2;
    }

    public Uri getUri() {
        DataSpec dataSpec2 = this.dataSpec;
        if (dataSpec2 != null) {
            return dataSpec2.uri;
        }
        return null;
    }

    public void close() throws IOException {
        if (this.data != null) {
            this.data = null;
            transferEnded();
        }
        this.dataSpec = null;
    }
}
