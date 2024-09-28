package com.google.android.exoplayer2.upstream.cache;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.AtomicFile;
import com.google.android.exoplayer2.util.ReusableBufferedOutputStream;
import com.google.android.exoplayer2.util.Util;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class CachedContentIndex {
    public static final String FILE_NAME = "cached_content_index.exi";
    private static final int FLAG_ENCRYPTED_INDEX = 1;
    private static final int VERSION = 2;
    private final AtomicFile atomicFile;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private boolean changed;
    private final Cipher cipher;
    private final boolean encrypt;
    private final SparseArray<String> idToKey;
    private final HashMap<String, CachedContent> keyToContent;
    private final SparseBooleanArray removedIds;
    private final SecretKeySpec secretKeySpec;

    public CachedContentIndex(File cacheDir) {
        this(cacheDir, (byte[]) null);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public CachedContentIndex(File cacheDir, byte[] secretKey) {
        this(cacheDir, secretKey, secretKey != null);
    }

    public CachedContentIndex(File cacheDir, byte[] secretKey, boolean encrypt2) {
        this.encrypt = encrypt2;
        if (secretKey != null) {
            Assertions.checkArgument(secretKey.length == 16);
            try {
                this.cipher = getCipher();
                this.secretKeySpec = new SecretKeySpec(secretKey, "AES");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                throw new IllegalStateException(e);
            }
        } else {
            Assertions.checkState(!encrypt2);
            this.cipher = null;
            this.secretKeySpec = null;
        }
        this.keyToContent = new HashMap<>();
        this.idToKey = new SparseArray<>();
        this.removedIds = new SparseBooleanArray();
        this.atomicFile = new AtomicFile(new File(cacheDir, FILE_NAME));
    }

    public void load() {
        Assertions.checkState(!this.changed);
        if (!readFile()) {
            this.atomicFile.delete();
            this.keyToContent.clear();
            this.idToKey.clear();
        }
    }

    public void store() throws Cache.CacheException {
        if (this.changed) {
            writeFile();
            this.changed = false;
            int removedIdCount = this.removedIds.size();
            for (int i = 0; i < removedIdCount; i++) {
                this.idToKey.remove(this.removedIds.keyAt(i));
            }
            this.removedIds.clear();
        }
    }

    public CachedContent getOrAdd(String key) {
        CachedContent cachedContent = this.keyToContent.get(key);
        return cachedContent == null ? addNew(key) : cachedContent;
    }

    public CachedContent get(String key) {
        return this.keyToContent.get(key);
    }

    public Collection<CachedContent> getAll() {
        return this.keyToContent.values();
    }

    public int assignIdForKey(String key) {
        return getOrAdd(key).id;
    }

    public String getKeyForId(int id) {
        return this.idToKey.get(id);
    }

    public void maybeRemove(String key) {
        CachedContent cachedContent = this.keyToContent.get(key);
        if (cachedContent != null && cachedContent.isEmpty() && !cachedContent.isLocked()) {
            this.keyToContent.remove(key);
            this.changed = true;
            this.idToKey.put(cachedContent.id, (Object) null);
            this.removedIds.put(cachedContent.id, true);
        }
    }

    public void removeEmpty() {
        String[] keys = new String[this.keyToContent.size()];
        this.keyToContent.keySet().toArray(keys);
        for (String key : keys) {
            maybeRemove(key);
        }
    }

    public Set<String> getKeys() {
        return this.keyToContent.keySet();
    }

    public void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) {
        if (getOrAdd(key).applyMetadataMutations(mutations)) {
            this.changed = true;
        }
    }

    public ContentMetadata getContentMetadata(String key) {
        CachedContent cachedContent = get(key);
        return cachedContent != null ? cachedContent.getMetadata() : DefaultContentMetadata.EMPTY;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x006a A[Catch:{ IOException -> 0x00a4, all -> 0x009d }, LOOP:0: B:30:0x0068->B:31:0x006a, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0086  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean readFile() {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            java.io.BufferedInputStream r2 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            com.google.android.exoplayer2.util.AtomicFile r3 = r11.atomicFile     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            java.io.InputStream r3 = r3.openRead()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r2.<init>(r3)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            java.io.DataInputStream r3 = new java.io.DataInputStream     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r3.<init>(r2)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r0 = r3
            int r3 = r0.readInt()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            if (r3 < 0) goto L_0x0097
            r4 = 2
            if (r3 <= r4) goto L_0x001e
            goto L_0x0097
        L_0x001e:
            int r5 = r0.readInt()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r6 = r5 & 1
            r7 = 1
            if (r6 == 0) goto L_0x005c
            javax.crypto.Cipher r6 = r11.cipher     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            if (r6 != 0) goto L_0x0031
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
            return r1
        L_0x0031:
            r6 = 16
            byte[] r6 = new byte[r6]     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r0.readFully(r6)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            javax.crypto.spec.IvParameterSpec r8 = new javax.crypto.spec.IvParameterSpec     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r8.<init>(r6)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            javax.crypto.Cipher r9 = r11.cipher     // Catch:{ InvalidKeyException -> 0x0055, InvalidAlgorithmParameterException -> 0x0053 }
            javax.crypto.spec.SecretKeySpec r10 = r11.secretKeySpec     // Catch:{ InvalidKeyException -> 0x0055, InvalidAlgorithmParameterException -> 0x0053 }
            r9.init(r4, r10, r8)     // Catch:{ InvalidKeyException -> 0x0055, InvalidAlgorithmParameterException -> 0x0053 }
            java.io.DataInputStream r4 = new java.io.DataInputStream     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            javax.crypto.CipherInputStream r9 = new javax.crypto.CipherInputStream     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            javax.crypto.Cipher r10 = r11.cipher     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r9.<init>(r2, r10)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r4.<init>(r9)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r0 = r4
        L_0x0052:
            goto L_0x0062
        L_0x0053:
            r4 = move-exception
            goto L_0x0056
        L_0x0055:
            r4 = move-exception
        L_0x0056:
            java.lang.IllegalStateException r7 = new java.lang.IllegalStateException     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r7.<init>(r4)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            throw r7     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
        L_0x005c:
            boolean r4 = r11.encrypt     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            if (r4 == 0) goto L_0x0052
            r11.changed = r7     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
        L_0x0062:
            int r4 = r0.readInt()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r6 = 0
            r8 = 0
        L_0x0068:
            if (r8 >= r4) goto L_0x0079
            com.google.android.exoplayer2.upstream.cache.CachedContent r9 = com.google.android.exoplayer2.upstream.cache.CachedContent.readFromStream(r3, r0)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r11.add(r9)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            int r10 = r9.headerHashCode(r3)     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            int r6 = r6 + r10
            int r8 = r8 + 1
            goto L_0x0068
        L_0x0079:
            int r8 = r0.readInt()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            int r9 = r0.read()     // Catch:{ IOException -> 0x00a4, all -> 0x009d }
            r10 = -1
            if (r9 != r10) goto L_0x0086
            r9 = 1
            goto L_0x0087
        L_0x0086:
            r9 = 0
        L_0x0087:
            if (r8 != r6) goto L_0x0091
            if (r9 != 0) goto L_0x008c
            goto L_0x0091
        L_0x008c:
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
            return r7
        L_0x0091:
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
            return r1
        L_0x0097:
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
            return r1
        L_0x009d:
            r1 = move-exception
            if (r0 == 0) goto L_0x00a3
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
        L_0x00a3:
            throw r1
        L_0x00a4:
            r2 = move-exception
            if (r0 == 0) goto L_0x00ab
            com.google.android.exoplayer2.util.Util.closeQuietly((java.io.Closeable) r0)
        L_0x00ab:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.CachedContentIndex.readFile():boolean");
    }

    private void writeFile() throws Cache.CacheException {
        DataOutputStream output = null;
        try {
            OutputStream outputStream = this.atomicFile.startWrite();
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(outputStream);
            } else {
                this.bufferedOutputStream.reset(outputStream);
            }
            output = new DataOutputStream(this.bufferedOutputStream);
            output.writeInt(2);
            output.writeInt(this.encrypt ? 1 : 0);
            if (this.encrypt) {
                byte[] initializationVector = new byte[16];
                new Random().nextBytes(initializationVector);
                output.write(initializationVector);
                try {
                    this.cipher.init(1, this.secretKeySpec, new IvParameterSpec(initializationVector));
                    output.flush();
                    output = new DataOutputStream(new CipherOutputStream(this.bufferedOutputStream, this.cipher));
                } catch (InvalidKeyException e) {
                    e = e;
                } catch (InvalidAlgorithmParameterException e2) {
                    e = e2;
                }
            }
            output.writeInt(this.keyToContent.size());
            int hashCode = 0;
            for (CachedContent cachedContent : this.keyToContent.values()) {
                cachedContent.writeToStream(output);
                hashCode += cachedContent.headerHashCode(2);
            }
            output.writeInt(hashCode);
            this.atomicFile.endWrite(output);
            Util.closeQuietly((Closeable) null);
            return;
            throw new IllegalStateException(e);
        } catch (IOException e3) {
            throw new Cache.CacheException((Throwable) e3);
        } catch (Throwable th) {
            Util.closeQuietly((Closeable) output);
            throw th;
        }
    }

    private CachedContent addNew(String key) {
        CachedContent cachedContent = new CachedContent(getNewId(this.idToKey), key);
        add(cachedContent);
        this.changed = true;
        return cachedContent;
    }

    private void add(CachedContent cachedContent) {
        this.keyToContent.put(cachedContent.key, cachedContent);
        this.idToKey.put(cachedContent.id, cachedContent.key);
    }

    private static Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (Util.SDK_INT == 18) {
            try {
                return Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
            } catch (Throwable th) {
            }
        }
        return Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }

    public static int getNewId(SparseArray<String> idToKey2) {
        int size = idToKey2.size();
        int id = size == 0 ? 0 : idToKey2.keyAt(size - 1) + 1;
        if (id < 0) {
            int id2 = 0;
            while (id < size && id == idToKey2.keyAt(id)) {
                id2 = id + 1;
            }
        }
        return id;
    }
}
