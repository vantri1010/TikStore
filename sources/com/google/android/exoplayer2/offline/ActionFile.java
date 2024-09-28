package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.Util;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class ActionFile {
    static final int VERSION = 0;
    private final File actionFile;
    private final AtomicFile atomicFile;

    public ActionFile(File actionFile2) {
        this.actionFile = actionFile2;
        this.atomicFile = new AtomicFile(actionFile2);
    }

    public DownloadAction[] load() throws IOException {
        if (!this.actionFile.exists()) {
            return new DownloadAction[0];
        }
        InputStream inputStream = null;
        try {
            inputStream = this.atomicFile.openRead();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int version = dataInputStream.readInt();
            if (version <= 0) {
                int actionCount = dataInputStream.readInt();
                DownloadAction[] actions = new DownloadAction[actionCount];
                for (int i = 0; i < actionCount; i++) {
                    actions[i] = DownloadAction.deserializeFromStream(dataInputStream);
                }
                return actions;
            }
            throw new IOException("Unsupported action file version: " + version);
        } finally {
            Util.closeQuietly((Closeable) inputStream);
        }
    }

    public void store(DownloadAction... downloadActions) throws IOException {
        DataOutputStream output = null;
        try {
            DataOutputStream output2 = new DataOutputStream(this.atomicFile.startWrite());
            output2.writeInt(0);
            output2.writeInt(downloadActions.length);
            for (DownloadAction action : downloadActions) {
                action.serializeToStream(output2);
            }
            this.atomicFile.endWrite(output2);
            output = null;
        } finally {
            Util.closeQuietly((Closeable) output);
        }
    }
}
