package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DownloadAction {
    public static final String TYPE_DASH = "dash";
    public static final String TYPE_HLS = "hls";
    public static final String TYPE_PROGRESSIVE = "progressive";
    public static final String TYPE_SS = "ss";
    private static final int VERSION = 2;
    public final String customCacheKey;
    public final byte[] data;
    public final String id;
    public final boolean isRemoveAction;
    public final List<StreamKey> keys;
    public final String type;
    public final Uri uri;

    public static DownloadAction fromByteArray(byte[] data2) throws IOException {
        return deserializeFromStream(new ByteArrayInputStream(data2));
    }

    public static DownloadAction deserializeFromStream(InputStream input) throws IOException {
        return readFromStream(new DataInputStream(input));
    }

    public static DownloadAction createDownloadAction(String type2, Uri uri2, List<StreamKey> keys2, String customCacheKey2, byte[] data2) {
        return new DownloadAction(type2, uri2, false, keys2, customCacheKey2, data2);
    }

    public static DownloadAction createRemoveAction(String type2, Uri uri2, String customCacheKey2) {
        return new DownloadAction(type2, uri2, true, Collections.emptyList(), customCacheKey2, (byte[]) null);
    }

    private DownloadAction(String type2, Uri uri2, boolean isRemoveAction2, List<StreamKey> keys2, String customCacheKey2, byte[] data2) {
        this.id = customCacheKey2 != null ? customCacheKey2 : uri2.toString();
        this.type = type2;
        this.uri = uri2;
        this.isRemoveAction = isRemoveAction2;
        this.customCacheKey = customCacheKey2;
        if (isRemoveAction2) {
            Assertions.checkArgument(keys2.isEmpty());
            Assertions.checkArgument(data2 == null);
            this.keys = Collections.emptyList();
            this.data = Util.EMPTY_BYTE_ARRAY;
            return;
        }
        ArrayList<StreamKey> mutableKeys = new ArrayList<>(keys2);
        Collections.sort(mutableKeys);
        this.keys = Collections.unmodifiableList(mutableKeys);
        this.data = data2 != null ? Arrays.copyOf(data2, data2.length) : Util.EMPTY_BYTE_ARRAY;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            serializeToStream(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public boolean isSameMedia(DownloadAction other) {
        return this.id.equals(other.id);
    }

    public List<StreamKey> getKeys() {
        return this.keys;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DownloadAction)) {
            return false;
        }
        DownloadAction that = (DownloadAction) o;
        if (!this.id.equals(that.id) || !this.type.equals(that.type) || !this.uri.equals(that.uri) || this.isRemoveAction != that.isRemoveAction || !this.keys.equals(that.keys) || !Util.areEqual(this.customCacheKey, that.customCacheKey) || !Arrays.equals(this.data, that.data)) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        int result = ((((((((this.type.hashCode() * 31) + this.id.hashCode()) * 31) + this.uri.hashCode()) * 31) + (this.isRemoveAction ? 1 : 0)) * 31) + this.keys.hashCode()) * 31;
        String str = this.customCacheKey;
        return ((result + (str != null ? str.hashCode() : 0)) * 31) + Arrays.hashCode(this.data);
    }

    public final void serializeToStream(OutputStream output) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(output);
        dataOutputStream.writeUTF(this.type);
        dataOutputStream.writeInt(2);
        dataOutputStream.writeUTF(this.uri.toString());
        dataOutputStream.writeBoolean(this.isRemoveAction);
        dataOutputStream.writeInt(this.data.length);
        dataOutputStream.write(this.data);
        dataOutputStream.writeInt(this.keys.size());
        for (int i = 0; i < this.keys.size(); i++) {
            StreamKey key = this.keys.get(i);
            dataOutputStream.writeInt(key.periodIndex);
            dataOutputStream.writeInt(key.groupIndex);
            dataOutputStream.writeInt(key.trackIndex);
        }
        dataOutputStream.writeBoolean(this.customCacheKey != null);
        String str = this.customCacheKey;
        if (str != null) {
            dataOutputStream.writeUTF(str);
        }
        dataOutputStream.flush();
    }

    private static DownloadAction readFromStream(DataInputStream input) throws IOException {
        byte[] data2;
        String customCacheKey2;
        DataInputStream dataInputStream = input;
        String type2 = input.readUTF();
        int version = input.readInt();
        Uri uri2 = Uri.parse(input.readUTF());
        boolean isRemoveAction2 = input.readBoolean();
        int dataLength = input.readInt();
        if (dataLength != 0) {
            byte[] data3 = new byte[dataLength];
            dataInputStream.readFully(data3);
            if (isRemoveAction2) {
                data2 = null;
            } else {
                data2 = data3;
            }
        } else {
            data2 = null;
        }
        boolean isLegacySegmented = false;
        boolean isLegacyProgressive = version == 0 && TYPE_PROGRESSIVE.equals(type2);
        List<StreamKey> keys2 = new ArrayList<>();
        if (!isLegacyProgressive) {
            int keyCount = input.readInt();
            for (int i = 0; i < keyCount; i++) {
                keys2.add(readKey(type2, version, dataInputStream));
            }
        }
        if (version < 2 && (TYPE_DASH.equals(type2) || TYPE_HLS.equals(type2) || TYPE_SS.equals(type2))) {
            isLegacySegmented = true;
        }
        if (!isLegacySegmented) {
            customCacheKey2 = input.readBoolean() ? input.readUTF() : null;
        } else {
            customCacheKey2 = null;
        }
        return new DownloadAction(type2, uri2, isRemoveAction2, keys2, customCacheKey2, data2);
    }

    private static StreamKey readKey(String type2, int version, DataInputStream input) throws IOException {
        int trackIndex;
        int groupIndex;
        int periodIndex;
        if ((TYPE_HLS.equals(type2) || TYPE_SS.equals(type2)) && version == 0) {
            periodIndex = 0;
            groupIndex = input.readInt();
            trackIndex = input.readInt();
        } else {
            periodIndex = input.readInt();
            groupIndex = input.readInt();
            trackIndex = input.readInt();
        }
        return new StreamKey(periodIndex, groupIndex, trackIndex);
    }
}
