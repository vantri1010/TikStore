package com.google.android.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public final class SimpleCache implements Cache {
    private static final String TAG = "SimpleCache";
    private static boolean cacheFolderLockingDisabled;
    private static final HashSet<File> lockedCacheDirs = new HashSet<>();
    private final File cacheDir;
    /* access modifiers changed from: private */
    public final CacheEvictor evictor;
    private final CachedContentIndex index;
    private final HashMap<String, ArrayList<Cache.Listener>> listeners;
    private boolean released;
    private long totalSpace;

    public static synchronized boolean isCacheFolderLocked(File cacheFolder) {
        boolean contains;
        synchronized (SimpleCache.class) {
            contains = lockedCacheDirs.contains(cacheFolder.getAbsoluteFile());
        }
        return contains;
    }

    @Deprecated
    public static synchronized void disableCacheFolderLocking() {
        synchronized (SimpleCache.class) {
            cacheFolderLockingDisabled = true;
            lockedCacheDirs.clear();
        }
    }

    public SimpleCache(File cacheDir2, CacheEvictor evictor2) {
        this(cacheDir2, evictor2, (byte[]) null, false);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public SimpleCache(File cacheDir2, CacheEvictor evictor2, byte[] secretKey) {
        this(cacheDir2, evictor2, secretKey, secretKey != null);
    }

    public SimpleCache(File cacheDir2, CacheEvictor evictor2, byte[] secretKey, boolean encrypt) {
        this(cacheDir2, evictor2, new CachedContentIndex(cacheDir2, secretKey, encrypt));
    }

    SimpleCache(File cacheDir2, CacheEvictor evictor2, CachedContentIndex index2) {
        if (lockFolder(cacheDir2)) {
            this.cacheDir = cacheDir2;
            this.evictor = evictor2;
            this.index = index2;
            this.listeners = new HashMap<>();
            final ConditionVariable conditionVariable = new ConditionVariable();
            new Thread("SimpleCache.initialize()") {
                public void run() {
                    synchronized (SimpleCache.this) {
                        conditionVariable.open();
                        SimpleCache.this.initialize();
                        SimpleCache.this.evictor.onCacheInitialized();
                    }
                }
            }.start();
            conditionVariable.block();
            return;
        }
        throw new IllegalStateException("Another SimpleCache instance uses the folder: " + cacheDir2);
    }

    public synchronized void release() {
        if (!this.released) {
            this.listeners.clear();
            removeStaleSpans();
            try {
                this.index.store();
                unlockFolder(this.cacheDir);
                this.released = true;
            } catch (Cache.CacheException e) {
                try {
                    Log.e(TAG, "Storing index file failed", e);
                } finally {
                    unlockFolder(this.cacheDir);
                    this.released = true;
                }
            }
        }
    }

    public synchronized NavigableSet<CacheSpan> addListener(String key, Cache.Listener listener) {
        Assertions.checkState(!this.released);
        ArrayList<Cache.Listener> listenersForKey = this.listeners.get(key);
        if (listenersForKey == null) {
            listenersForKey = new ArrayList<>();
            this.listeners.put(key, listenersForKey);
        }
        listenersForKey.add(listener);
        return getCachedSpans(key);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeListener(java.lang.String r3, com.google.android.exoplayer2.upstream.cache.Cache.Listener r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.released     // Catch:{ all -> 0x0021 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r2)
            return
        L_0x0007:
            java.util.HashMap<java.lang.String, java.util.ArrayList<com.google.android.exoplayer2.upstream.cache.Cache$Listener>> r0 = r2.listeners     // Catch:{ all -> 0x0021 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x0021 }
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x0021 }
            if (r0 == 0) goto L_0x001f
            r0.remove(r4)     // Catch:{ all -> 0x0021 }
            boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x0021 }
            if (r1 == 0) goto L_0x001f
            java.util.HashMap<java.lang.String, java.util.ArrayList<com.google.android.exoplayer2.upstream.cache.Cache$Listener>> r1 = r2.listeners     // Catch:{ all -> 0x0021 }
            r1.remove(r3)     // Catch:{ all -> 0x0021 }
        L_0x001f:
            monitor-exit(r2)
            return
        L_0x0021:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.upstream.cache.SimpleCache.removeListener(java.lang.String, com.google.android.exoplayer2.upstream.cache.Cache$Listener):void");
    }

    public synchronized NavigableSet<CacheSpan> getCachedSpans(String key) {
        TreeSet treeSet;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent != null) {
            if (!cachedContent.isEmpty()) {
                treeSet = new TreeSet(cachedContent.getSpans());
            }
        }
        treeSet = new TreeSet();
        return treeSet;
    }

    public synchronized Set<String> getKeys() {
        Assertions.checkState(!this.released);
        return new HashSet(this.index.getKeys());
    }

    public synchronized long getCacheSpace() {
        Assertions.checkState(!this.released);
        return this.totalSpace;
    }

    public synchronized SimpleCacheSpan startReadWrite(String key, long position) throws InterruptedException, Cache.CacheException {
        SimpleCacheSpan span;
        while (true) {
            span = startReadWriteNonBlocking(key, position);
            if (span == null) {
                wait();
            }
        }
        return span;
    }

    public synchronized SimpleCacheSpan startReadWriteNonBlocking(String key, long position) throws Cache.CacheException {
        Assertions.checkState(!this.released);
        SimpleCacheSpan cacheSpan = getSpan(key, position);
        if (cacheSpan.isCached) {
            try {
                SimpleCacheSpan newCacheSpan = this.index.get(key).touch(cacheSpan);
                notifySpanTouched(cacheSpan, newCacheSpan);
                return newCacheSpan;
            } catch (Cache.CacheException e) {
                return cacheSpan;
            }
        } else {
            CachedContent cachedContent = this.index.getOrAdd(key);
            if (cachedContent.isLocked()) {
                return null;
            }
            cachedContent.setLocked(true);
            return cacheSpan;
        }
    }

    public synchronized File startFile(String key, long position, long length) throws Cache.CacheException {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
            removeStaleSpans();
        }
        this.evictor.onStartFile(this, key, position, length);
        return SimpleCacheSpan.getCacheFile(this.cacheDir, cachedContent.id, position, System.currentTimeMillis());
    }

    public synchronized void commitFile(File file, long length) throws Cache.CacheException {
        boolean z = true;
        Assertions.checkState(!this.released);
        if (file.exists()) {
            if (length == 0) {
                file.delete();
                return;
            }
            SimpleCacheSpan span = SimpleCacheSpan.createCacheEntry(file, length, this.index);
            Assertions.checkState(span != null);
            CachedContent cachedContent = this.index.get(span.key);
            Assertions.checkNotNull(cachedContent);
            Assertions.checkState(cachedContent.isLocked());
            long contentLength = ContentMetadata.CC.getContentLength(cachedContent.getMetadata());
            if (contentLength != -1) {
                if (span.position + span.length > contentLength) {
                    z = false;
                }
                Assertions.checkState(z);
            }
            addSpan(span);
            this.index.store();
            notifyAll();
        }
    }

    public synchronized void releaseHoleSpan(CacheSpan holeSpan) {
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(holeSpan.key);
        Assertions.checkNotNull(cachedContent);
        Assertions.checkState(cachedContent.isLocked());
        cachedContent.setLocked(false);
        this.index.maybeRemove(cachedContent.key);
        notifyAll();
    }

    public synchronized void removeSpan(CacheSpan span) {
        Assertions.checkState(!this.released);
        removeSpanInternal(span);
    }

    public synchronized boolean isCached(String key, long position, long length) {
        boolean z;
        z = true;
        Assertions.checkState(!this.released);
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null || cachedContent.getCachedBytesLength(position, length) < length) {
            z = false;
        }
        return z;
    }

    public synchronized long getCachedLength(String key, long position, long length) {
        CachedContent cachedContent;
        Assertions.checkState(!this.released);
        cachedContent = this.index.get(key);
        return cachedContent != null ? cachedContent.getCachedBytesLength(position, length) : -length;
    }

    public synchronized void applyContentMetadataMutations(String key, ContentMetadataMutations mutations) throws Cache.CacheException {
        Assertions.checkState(!this.released);
        this.index.applyContentMetadataMutations(key, mutations);
        this.index.store();
    }

    public synchronized ContentMetadata getContentMetadata(String key) {
        Assertions.checkState(!this.released);
        return this.index.getContentMetadata(key);
    }

    private SimpleCacheSpan getSpan(String key, long position) throws Cache.CacheException {
        SimpleCacheSpan span;
        CachedContent cachedContent = this.index.get(key);
        if (cachedContent == null) {
            return SimpleCacheSpan.createOpenHole(key, position);
        }
        while (true) {
            span = cachedContent.getSpan(position);
            if (!span.isCached || span.file.exists()) {
                return span;
            }
            removeStaleSpans();
        }
        return span;
    }

    /* access modifiers changed from: private */
    public void initialize() {
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
            return;
        }
        this.index.load();
        loadDirectory(this.cacheDir, true);
        this.index.removeEmpty();
        try {
            this.index.store();
        } catch (Cache.CacheException e) {
            Log.e(TAG, "Storing index file failed", e);
        }
    }

    private void loadDirectory(File directory, boolean isRootDirectory) {
        File[] files = directory.listFiles();
        if (files != null) {
            if (isRootDirectory || files.length != 0) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (fileName.indexOf(46) == -1) {
                        loadDirectory(file, false);
                    } else if (!isRootDirectory || !CachedContentIndex.FILE_NAME.equals(fileName)) {
                        long fileLength = file.length();
                        SimpleCacheSpan span = fileLength > 0 ? SimpleCacheSpan.createCacheEntry(file, fileLength, this.index) : null;
                        if (span != null) {
                            addSpan(span);
                        } else {
                            file.delete();
                        }
                    }
                }
                return;
            }
            directory.delete();
        }
    }

    private void addSpan(SimpleCacheSpan span) {
        this.index.getOrAdd(span.key).addSpan(span);
        this.totalSpace += span.length;
        notifySpanAdded(span);
    }

    private void removeSpanInternal(CacheSpan span) {
        CachedContent cachedContent = this.index.get(span.key);
        if (cachedContent != null && cachedContent.removeSpan(span)) {
            this.totalSpace -= span.length;
            this.index.maybeRemove(cachedContent.key);
            notifySpanRemoved(span);
        }
    }

    private void removeStaleSpans() {
        ArrayList<CacheSpan> spansToBeRemoved = new ArrayList<>();
        for (CachedContent cachedContent : this.index.getAll()) {
            Iterator<SimpleCacheSpan> it = cachedContent.getSpans().iterator();
            while (it.hasNext()) {
                CacheSpan span = it.next();
                if (!span.file.exists()) {
                    spansToBeRemoved.add(span);
                }
            }
        }
        for (int i = 0; i < spansToBeRemoved.size(); i++) {
            removeSpanInternal(spansToBeRemoved.get(i));
        }
    }

    private void notifySpanRemoved(CacheSpan span) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanRemoved(this, span);
            }
        }
        this.evictor.onSpanRemoved(this, span);
    }

    private void notifySpanAdded(SimpleCacheSpan span) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(span.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanAdded(this, span);
            }
        }
        this.evictor.onSpanAdded(this, span);
    }

    private void notifySpanTouched(SimpleCacheSpan oldSpan, CacheSpan newSpan) {
        ArrayList<Cache.Listener> keyListeners = this.listeners.get(oldSpan.key);
        if (keyListeners != null) {
            for (int i = keyListeners.size() - 1; i >= 0; i--) {
                keyListeners.get(i).onSpanTouched(this, oldSpan, newSpan);
            }
        }
        this.evictor.onSpanTouched(this, oldSpan, newSpan);
    }

    private static synchronized boolean lockFolder(File cacheDir2) {
        synchronized (SimpleCache.class) {
            if (cacheFolderLockingDisabled) {
                return true;
            }
            boolean add = lockedCacheDirs.add(cacheDir2.getAbsoluteFile());
            return add;
        }
    }

    private static synchronized void unlockFolder(File cacheDir2) {
        synchronized (SimpleCache.class) {
            if (!cacheFolderLockingDisabled) {
                lockedCacheDirs.remove(cacheDir2.getAbsoluteFile());
            }
        }
    }
}
