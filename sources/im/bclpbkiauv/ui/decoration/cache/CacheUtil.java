package im.bclpbkiauv.ui.decoration.cache;

import android.util.LruCache;
import android.util.SparseArray;
import java.lang.ref.SoftReference;

public class CacheUtil<T> implements CacheInterface<T> {
    private LruCache<Integer, T> mLruCache;
    private SparseArray<SoftReference<T>> mSoftCache;
    private boolean mUseCache = true;

    public CacheUtil() {
        initLruCache();
    }

    public void isCacheable(boolean b) {
        this.mUseCache = b;
    }

    private void initLruCache() {
        this.mLruCache = new LruCache<Integer, T>(2097152) {
            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, Integer key, T oldValue, T newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    public void put(int position, T t) {
        if (this.mUseCache) {
            this.mLruCache.put(Integer.valueOf(position), t);
        }
    }

    public T get(int position) {
        if (!this.mUseCache) {
            return null;
        }
        return this.mLruCache.get(Integer.valueOf(position));
    }

    public void remove(int position) {
        if (this.mUseCache) {
            this.mLruCache.remove(Integer.valueOf(position));
        }
    }

    public void clean() {
        this.mLruCache.evictAll();
    }
}
