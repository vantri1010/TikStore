package com.google.android.exoplayer2.upstream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class ContentDataSource extends BaseDataSource {
    private AssetFileDescriptor assetFileDescriptor;
    private long bytesRemaining;
    private FileInputStream inputStream;
    private boolean opened;
    private final ContentResolver resolver;
    private Uri uri;

    public static class ContentDataSourceException extends IOException {
        public ContentDataSourceException(IOException cause) {
            super(cause);
        }
    }

    public ContentDataSource(Context context) {
        super(false);
        this.resolver = context.getContentResolver();
    }

    @Deprecated
    public ContentDataSource(Context context, TransferListener listener) {
        this(context);
        if (listener != null) {
            addTransferListener(listener);
        }
    }

    public long open(DataSpec dataSpec) throws ContentDataSourceException {
        try {
            this.uri = dataSpec.uri;
            transferInitializing(dataSpec);
            AssetFileDescriptor openAssetFileDescriptor = this.resolver.openAssetFileDescriptor(this.uri, "r");
            this.assetFileDescriptor = openAssetFileDescriptor;
            if (openAssetFileDescriptor != null) {
                this.inputStream = new FileInputStream(this.assetFileDescriptor.getFileDescriptor());
                long assetStartOffset = this.assetFileDescriptor.getStartOffset();
                long skipped = this.inputStream.skip(dataSpec.position + assetStartOffset) - assetStartOffset;
                if (skipped == dataSpec.position) {
                    long j = -1;
                    if (dataSpec.length != -1) {
                        this.bytesRemaining = dataSpec.length;
                    } else {
                        long assetFileDescriptorLength = this.assetFileDescriptor.getLength();
                        if (assetFileDescriptorLength == -1) {
                            FileChannel channel = this.inputStream.getChannel();
                            long channelSize = channel.size();
                            if (channelSize != 0) {
                                j = channelSize - channel.position();
                            }
                            this.bytesRemaining = j;
                        } else {
                            this.bytesRemaining = assetFileDescriptorLength - skipped;
                        }
                    }
                    this.opened = true;
                    transferStarted(dataSpec);
                    return this.bytesRemaining;
                }
                throw new EOFException();
            }
            throw new FileNotFoundException("Could not open file descriptor for: " + this.uri);
        } catch (IOException e) {
            throw new ContentDataSourceException(e);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws ContentDataSourceException {
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
                throw new ContentDataSourceException(e);
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
            throw new ContentDataSourceException(new EOFException());
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() throws ContentDataSourceException {
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
                throw new ContentDataSourceException(e);
            } catch (Throwable th) {
                this.assetFileDescriptor = null;
                if (this.opened) {
                    this.opened = false;
                    transferEnded();
                }
                throw th;
            }
        } catch (IOException e2) {
            throw new ContentDataSourceException(e2);
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
                throw new ContentDataSourceException(e3);
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
