package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RawResourceDataSource extends BaseDataSource {
    public static final String RAW_RESOURCE_SCHEME = "rawresource";
    private AssetFileDescriptor assetFileDescriptor;
    private long bytesRemaining;
    private InputStream inputStream;
    private boolean opened;
    private final Resources resources;
    private Uri uri;

    public static class RawResourceDataSourceException extends IOException {
        public RawResourceDataSourceException(String message) {
            super(message);
        }

        public RawResourceDataSourceException(IOException e) {
            super(e);
        }
    }

    public static Uri buildRawResourceUri(int rawResourceId) {
        return Uri.parse("rawresource:///" + rawResourceId);
    }

    public RawResourceDataSource(Context context) {
        super(false);
        this.resources = context.getResources();
    }

    @Deprecated
    public RawResourceDataSource(Context context, TransferListener listener) {
        this(context);
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    public long open(DataSpec dataSpec) throws RawResourceDataSourceException {
        try {
            Uri uri2 = dataSpec.uri;
            this.uri = uri2;
            if (TextUtils.equals(RAW_RESOURCE_SCHEME, uri2.getScheme())) {
                int resourceId = Integer.parseInt(this.uri.getLastPathSegment());
                transferInitializing(dataSpec);
                this.assetFileDescriptor = this.resources.openRawResourceFd(resourceId);
                FileInputStream fileInputStream = new FileInputStream(this.assetFileDescriptor.getFileDescriptor());
                this.inputStream = fileInputStream;
                fileInputStream.skip(this.assetFileDescriptor.getStartOffset());
                if (this.inputStream.skip(dataSpec.position) >= dataSpec.position) {
                    long j = -1;
                    if (dataSpec.length != -1) {
                        this.bytesRemaining = dataSpec.length;
                    } else {
                        long assetFileDescriptorLength = this.assetFileDescriptor.getLength();
                        if (assetFileDescriptorLength != -1) {
                            j = assetFileDescriptorLength - dataSpec.position;
                        }
                        this.bytesRemaining = j;
                    }
                    this.opened = true;
                    transferStarted(dataSpec);
                    return this.bytesRemaining;
                }
                throw new EOFException();
            }
            throw new RawResourceDataSourceException("URI must use scheme rawresource");
        } catch (NumberFormatException e) {
            throw new RawResourceDataSourceException("Resource identifier must be an integer.");
        } catch (IOException e2) {
            throw new RawResourceDataSourceException(e2);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws RawResourceDataSourceException {
        int bytesToRead;
        if (readLength == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        if (j == -1) {
            bytesToRead = readLength;
        } else {
            try {
                bytesToRead = (int) Math.min(j, (long) readLength);
            } catch (IOException e) {
                throw new RawResourceDataSourceException(e);
            }
        }
        int bytesRead = this.inputStream.read(buffer, offset, bytesToRead);
        if (bytesRead != -1) {
            long j2 = this.bytesRemaining;
            if (j2 != -1) {
                this.bytesRemaining = j2 - ((long) bytesRead);
            }
            bytesTransferred(bytesRead);
            return bytesRead;
        } else if (this.bytesRemaining == -1) {
            return -1;
        } else {
            throw new RawResourceDataSourceException((IOException) new EOFException());
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() throws RawResourceDataSourceException {
        this.uri = null;
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            this.inputStream = null;
            try {
                if (this.assetFileDescriptor != null) {
                    this.assetFileDescriptor.close();
                }
                this.assetFileDescriptor = null;
                if (this.opened) {
                    this.opened = false;
                    transferEnded();
                }
            } catch (IOException e) {
                throw new RawResourceDataSourceException(e);
            } catch (Throwable th) {
                this.assetFileDescriptor = null;
                if (this.opened) {
                    this.opened = false;
                    transferEnded();
                }
                throw th;
            }
        } catch (IOException e2) {
            throw new RawResourceDataSourceException(e2);
        } catch (Throwable th2) {
            this.inputStream = null;
            try {
                if (this.assetFileDescriptor != null) {
                    this.assetFileDescriptor.close();
                }
                this.assetFileDescriptor = null;
                if (this.opened) {
                    this.opened = false;
                    transferEnded();
                }
                throw th2;
            } catch (IOException e3) {
                throw new RawResourceDataSourceException(e3);
            } catch (Throwable th3) {
                this.assetFileDescriptor = null;
                if (this.opened) {
                    this.opened = false;
                    transferEnded();
                }
                throw th3;
            }
        }
    }
}
