package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.upstream.DataSource;

public final class FileDataSourceFactory implements DataSource.Factory {
    private final TransferListener listener;

    public FileDataSourceFactory() {
        this((TransferListener) null);
    }

    public FileDataSourceFactory(TransferListener listener2) {
        this.listener = listener2;
    }

    public DataSource createDataSource() {
        FileDataSource dataSource = new FileDataSource();
        TransferListener transferListener = this.listener;
        if (transferListener != null) {
            dataSource.addTransferListener(transferListener);
        }
        return dataSource;
    }
}
