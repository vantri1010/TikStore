package com.danikula.videocache.file;

import com.danikula.videocache.Cache;
import com.danikula.videocache.ProxyCacheException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileCache implements Cache {
    private static final String TEMP_POSTFIX = ".download";
    private RandomAccessFile dataFile;
    private final DiskUsage diskUsage;
    public File file;

    public FileCache(File file2) throws ProxyCacheException {
        this(file2, new UnlimitedDiskUsage());
    }

    public FileCache(File file2, DiskUsage diskUsage2) throws ProxyCacheException {
        File file3;
        if (diskUsage2 != null) {
            try {
                this.diskUsage = diskUsage2;
                Files.makeDir(file2.getParentFile());
                boolean completed = file2.exists();
                if (completed) {
                    file3 = file2;
                } else {
                    File parentFile = file2.getParentFile();
                    file3 = new File(parentFile, file2.getName() + TEMP_POSTFIX);
                }
                this.file = file3;
                this.dataFile = new RandomAccessFile(this.file, completed ? "r" : "rw");
            } catch (IOException e) {
                throw new ProxyCacheException("Error using file " + file2 + " as disc cache", e);
            }
        } else {
            throw new NullPointerException();
        }
    }

    public synchronized long available() throws ProxyCacheException {
        try {
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading length of file " + this.file, e);
        }
        return (long) ((int) this.dataFile.length());
    }

    public synchronized int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        try {
            this.dataFile.seek(offset);
        } catch (IOException e) {
            throw new ProxyCacheException(String.format("Error reading %d bytes with offset %d from file[%d bytes] to buffer[%d bytes]", new Object[]{Integer.valueOf(length), Long.valueOf(offset), Long.valueOf(available()), Integer.valueOf(buffer.length)}), e);
        }
        return this.dataFile.read(buffer, 0, length);
    }

    public synchronized void append(byte[] data, int length) throws ProxyCacheException {
        try {
            if (!isCompleted()) {
                this.dataFile.seek(available());
                this.dataFile.write(data, 0, length);
            } else {
                throw new ProxyCacheException("Error append cache: cache file " + this.file + " is completed!");
            }
        } catch (IOException e) {
            throw new ProxyCacheException(String.format("Error writing %d bytes to %s from buffer with size %d", new Object[]{Integer.valueOf(length), this.dataFile, Integer.valueOf(data.length)}), e);
        }
    }

    public synchronized void close() throws ProxyCacheException {
        try {
            this.dataFile.close();
            this.diskUsage.touch(this.file);
        } catch (IOException e) {
            throw new ProxyCacheException("Error closing file " + this.file, e);
        }
    }

    public synchronized void complete() throws ProxyCacheException {
        if (!isCompleted()) {
            close();
            File completedFile = new File(this.file.getParentFile(), this.file.getName().substring(0, this.file.getName().length() - TEMP_POSTFIX.length()));
            if (this.file.renameTo(completedFile)) {
                this.file = completedFile;
                try {
                    this.dataFile = new RandomAccessFile(this.file, "r");
                    this.diskUsage.touch(this.file);
                } catch (IOException e) {
                    throw new ProxyCacheException("Error opening " + this.file + " as disc cache", e);
                }
            } else {
                throw new ProxyCacheException("Error renaming file " + this.file + " to " + completedFile + " for completion!");
            }
        }
    }

    public synchronized boolean isCompleted() {
        return !isTempFile(this.file);
    }

    public File getFile() {
        return this.file;
    }

    private boolean isTempFile(File file2) {
        return file2.getName().endsWith(TEMP_POSTFIX);
    }
}
