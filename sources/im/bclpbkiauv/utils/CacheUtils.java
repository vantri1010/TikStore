package im.bclpbkiauv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONArray;
import org.json.JSONObject;

public class CacheUtils {
    private static final int MAX_COUNT = Integer.MAX_VALUE;
    private static final int MAX_SIZE = 50000000;
    public static final int TIME_DAY = 86400;
    public static final int TIME_HOUR = 3600;
    private static Map<String, CacheUtils> mInstanceMap = new HashMap();
    /* access modifiers changed from: private */
    public ACacheManager mCache;

    public static CacheUtils get(Context ctx) {
        return get(ctx, "ACache");
    }

    public static CacheUtils get(Context ctx, String cacheName) {
        return get(new File(ctx.getCacheDir(), cacheName), 50000000, Integer.MAX_VALUE);
    }

    public static CacheUtils get(File cacheDir) {
        return get(cacheDir, 50000000, Integer.MAX_VALUE);
    }

    public static CacheUtils get(Context ctx, long max_zise, int max_count) {
        return get(new File(ctx.getCacheDir(), "ACache"), max_zise, max_count);
    }

    public static CacheUtils get(File cacheDir, long max_zise, int max_count) {
        Map<String, CacheUtils> map = mInstanceMap;
        CacheUtils manager = map.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager != null) {
            return manager;
        }
        CacheUtils manager2 = new CacheUtils(cacheDir, max_zise, max_count);
        Map<String, CacheUtils> map2 = mInstanceMap;
        map2.put(cacheDir.getAbsolutePath() + myPid(), manager2);
        return manager2;
    }

    private static String myPid() {
        return "_" + Process.myPid();
    }

    private CacheUtils(File cacheDir, long max_size, int max_count) {
        if (cacheDir.exists() || cacheDir.mkdirs()) {
            this.mCache = new ACacheManager(cacheDir, max_size, max_count);
            return;
        }
        throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
    }

    class xFileOutputStream extends FileOutputStream {
        File file;

        public xFileOutputStream(File file2) throws FileNotFoundException {
            super(file2);
            this.file = file2;
        }

        public void close() throws IOException {
            super.close();
            CacheUtils.this.mCache.put(this.file);
        }
    }

    public void put(String key, String value) {
        File file = this.mCache.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e = e;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e3) {
                    e = e3;
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            this.mCache.put(file);
            throw th;
        }
        this.mCache.put(file);
        e.printStackTrace();
        this.mCache.put(file);
    }

    public void put(String key, String value, int saveTime) {
        put(key, Utils.newStringWithDateInfo(saveTime, value));
    }

    public String getAsString(String key) {
        File file = this.mCache.get(key);
        if (!file.exists()) {
            return null;
        }
        BufferedReader in = null;
        try {
            BufferedReader in2 = new BufferedReader(new FileReader(file));
            String readString = "";
            while (true) {
                String readLine = in2.readLine();
                String currentLine = readLine;
                if (readLine == null) {
                    break;
                }
                readString = readString + currentLine;
            }
            if (!Utils.isDue(readString)) {
                String access$700 = Utils.clearDateInfo(readString);
                try {
                    in2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (0 != 0) {
                    remove(key);
                }
                return access$700;
            }
            try {
                in2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (1 != 0) {
                remove(key);
            }
            return null;
        } catch (IOException e3) {
            e3.printStackTrace();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (0 != 0) {
                remove(key);
            }
            return null;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            if (0 != 0) {
                remove(key);
            }
            throw th;
        }
    }

    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    public JSONObject getAsJSONObject(String key) {
        try {
            return new JSONObject(getAsString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    public JSONArray getAsJSONArray(String key) {
        try {
            return new JSONArray(getAsString(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void put(String key, byte[] value) {
        File file = this.mCache.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e = e;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e3) {
                    e = e3;
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            this.mCache.put(file);
            throw th;
        }
        this.mCache.put(file);
        e.printStackTrace();
        this.mCache.put(file);
    }

    public OutputStream put(String key) throws FileNotFoundException {
        return new xFileOutputStream(this.mCache.newFile(key));
    }

    public InputStream get(String key) throws FileNotFoundException {
        File file = this.mCache.get(key);
        if (!file.exists()) {
            return null;
        }
        return new FileInputStream(file);
    }

    public void put(String key, byte[] value, int saveTime) {
        put(key, Utils.newByteArrayWithDateInfo(saveTime, value));
    }

    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        try {
            File file = this.mCache.get(key);
            if (!file.exists()) {
                if (RAFile != null) {
                    try {
                        RAFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (0 != 0) {
                    remove(key);
                }
                return null;
            }
            RandomAccessFile RAFile2 = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[((int) RAFile2.length())];
            RAFile2.read(byteArray);
            if (!Utils.isDue(byteArray)) {
                byte[] access$1000 = Utils.clearDateInfo(byteArray);
                try {
                    RAFile2.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                if (0 != 0) {
                    remove(key);
                }
                return access$1000;
            }
            try {
                RAFile2.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            if (1 != 0) {
                remove(key);
            }
            return null;
        } catch (Exception e4) {
            e4.printStackTrace();
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            if (0 != 0) {
                remove(key);
            }
            return null;
        } catch (Throwable th) {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            if (0 != 0) {
                remove(key);
            }
            throw th;
        }
    }

    public void put(String key, Serializable value) {
        put(key, value, -1);
    }

    public void put(String key, Serializable value, int saveTime) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos2 = new ObjectOutputStream(baos);
            oos2.writeObject(value);
            byte[] data = baos.toByteArray();
            if (saveTime != -1) {
                put(key, data, saveTime);
            } else {
                put(key, data);
            }
            try {
                oos2.close();
            } catch (IOException e) {
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            oos.close();
        } catch (Throwable th) {
            try {
                oos.close();
            } catch (IOException e3) {
            }
            throw th;
        }
    }

    public Object getAsObject(String key) {
        byte[] data = getAsBinary(key);
        if (data == null) {
            return null;
        }
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream bais2 = new ByteArrayInputStream(data);
            ObjectInputStream ois2 = new ObjectInputStream(bais2);
            Object reObject = ois2.readObject();
            try {
                bais2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ois2.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return reObject;
        } catch (Exception e3) {
            e3.printStackTrace();
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            throw th;
        }
    }

    public void put(String key, Bitmap value) {
        put(key, Utils.Bitmap2Bytes(value));
    }

    public void put(String key, Bitmap value, int saveTime) {
        put(key, Utils.Bitmap2Bytes(value), saveTime);
    }

    public Bitmap getAsBitmap(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.Bytes2Bimap(getAsBinary(key));
    }

    public void put(String key, Drawable value) {
        put(key, Utils.drawable2Bitmap(value));
    }

    public void put(String key, Drawable value, int saveTime) {
        put(key, Utils.drawable2Bitmap(value), saveTime);
    }

    public Drawable getAsDrawable(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.bitmap2Drawable(Utils.Bytes2Bimap(getAsBinary(key)));
    }

    public File file(String key) {
        File f = this.mCache.newFile(key);
        if (f.exists()) {
            return f;
        }
        return null;
    }

    public boolean remove(String key) {
        return this.mCache.remove(key);
    }

    public void clear() {
        this.mCache.clear();
    }

    public class ACacheManager {
        /* access modifiers changed from: private */
        public final AtomicInteger cacheCount;
        protected File cacheDir;
        /* access modifiers changed from: private */
        public final AtomicLong cacheSize;
        private final int countLimit;
        /* access modifiers changed from: private */
        public final Map<File, Long> lastUsageDates;
        private final long sizeLimit;

        private ACacheManager(File cacheDir2, long sizeLimit2, int countLimit2) {
            this.lastUsageDates = Collections.synchronizedMap(new HashMap());
            this.cacheDir = cacheDir2;
            this.sizeLimit = sizeLimit2;
            this.countLimit = countLimit2;
            this.cacheSize = new AtomicLong();
            this.cacheCount = new AtomicInteger();
            calculateCacheSizeAndCacheCount();
        }

        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cachedFiles = ACacheManager.this.cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            size = (int) (((long) size) + ACacheManager.this.calculateSize(cachedFile));
                            count++;
                            ACacheManager.this.lastUsageDates.put(cachedFile, Long.valueOf(cachedFile.lastModified()));
                        }
                        ACacheManager.this.cacheSize.set((long) size);
                        ACacheManager.this.cacheCount.set(count);
                    }
                }
            }).start();
        }

        /* access modifiers changed from: private */
        public void put(File file) {
            int curCacheCount = this.cacheCount.get();
            while (curCacheCount + 1 > this.countLimit) {
                this.cacheSize.addAndGet(-removeNext());
                curCacheCount = this.cacheCount.addAndGet(-1);
            }
            this.cacheCount.addAndGet(1);
            long valueSize = calculateSize(file);
            long curCacheSize = this.cacheSize.get();
            while (curCacheSize + valueSize > this.sizeLimit) {
                curCacheSize = this.cacheSize.addAndGet(-removeNext());
            }
            this.cacheSize.addAndGet(valueSize);
            Long currentTime = Long.valueOf(System.currentTimeMillis());
            file.setLastModified(currentTime.longValue());
            this.lastUsageDates.put(file, currentTime);
        }

        /* access modifiers changed from: private */
        public File get(String key) {
            File file = newFile(key);
            Long currentTime = Long.valueOf(System.currentTimeMillis());
            file.setLastModified(currentTime.longValue());
            this.lastUsageDates.put(file, currentTime);
            return file;
        }

        /* access modifiers changed from: private */
        public File newFile(String key) {
            File file = this.cacheDir;
            return new File(file, key.hashCode() + "");
        }

        /* access modifiers changed from: private */
        public boolean remove(String key) {
            return get(key).delete();
        }

        /* access modifiers changed from: private */
        public void clear() {
            this.lastUsageDates.clear();
            this.cacheSize.set(0);
            File[] files = this.cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        private long removeNext() {
            if (this.lastUsageDates.isEmpty()) {
                return 0;
            }
            Long oldestUsage = null;
            File mostLongUsedFile = null;
            Set<Map.Entry<File, Long>> entries = this.lastUsageDates.entrySet();
            synchronized (this.lastUsageDates) {
                for (Map.Entry<File, Long> entry : entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage.longValue() < oldestUsage.longValue()) {
                            oldestUsage = lastValueUsage;
                            mostLongUsedFile = entry.getKey();
                        }
                    }
                }
            }
            long fileSize = calculateSize(mostLongUsedFile);
            if (mostLongUsedFile.delete()) {
                this.lastUsageDates.remove(mostLongUsedFile);
            }
            return fileSize;
        }

        /* access modifiers changed from: private */
        public long calculateSize(File file) {
            return file.length();
        }
    }

    private static class Utils {
        private static final char mSeparator = ' ';

        private Utils() {
        }

        /* access modifiers changed from: private */
        public static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /* access modifiers changed from: private */
        public static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromDate(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                if (System.currentTimeMillis() > (1000 * Long.valueOf(strs[1]).longValue()) + Long.valueOf(saveTimeStr).longValue()) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        public static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        /* access modifiers changed from: private */
        public static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[(data1.length + data2.length)];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        /* access modifiers changed from: private */
        public static String clearDateInfo(String strInfo) {
            if (strInfo == null || !hasDateInfo(strInfo.getBytes())) {
                return strInfo;
            }
            return strInfo.substring(strInfo.indexOf(32) + 1, strInfo.length());
        }

        /* access modifiers changed from: private */
        public static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            }
            return data;
        }

        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == 45 && indexOf(data, mSeparator) > 14;
        }

        private static String[] getDateInfoFromDate(byte[] data) {
            if (!hasDateInfo(data)) {
                return null;
            }
            return new String[]{new String(copyOfRange(data, 0, 13)), new String(copyOfRange(data, 14, indexOf(data, mSeparator)))};
        }

        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength >= 0) {
                byte[] copy = new byte[newLength];
                System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
                return copy;
            }
            throw new IllegalArgumentException(from + " > " + to);
        }

        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

        /* access modifiers changed from: private */
        public static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /* access modifiers changed from: private */
        public static Bitmap Bytes2Bimap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /* access modifiers changed from: private */
        public static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(w, h, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
            return bitmap;
        }

        /* access modifiers changed from: private */
        public static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            new BitmapDrawable(bm).setTargetDensity(bm.getDensity());
            return new BitmapDrawable(bm);
        }
    }
}
