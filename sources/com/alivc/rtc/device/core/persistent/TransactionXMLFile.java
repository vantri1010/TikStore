package com.alivc.rtc.device.core.persistent;

import com.alivc.rtc.device.core.persistent.MySharedPreferences;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class TransactionXMLFile {
    /* access modifiers changed from: private */
    public static final Object GLOBAL_COMMIT_LOCK = new Object();
    public static final int MODE_PRIVATE = 0;
    private File mPreferencesDir;
    private final Object mSync = new Object();
    private HashMap<File, MySharedPreferencesImpl> sSharedPrefs = new HashMap<>();

    public TransactionXMLFile(String dir) {
        if (dir == null || dir.length() <= 0) {
            throw new RuntimeException("Directory can not be empty");
        }
        this.mPreferencesDir = new File(dir);
    }

    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException("File " + name + " contains a path separator");
    }

    private File getPreferencesDir() {
        File file;
        synchronized (this.mSync) {
            file = this.mPreferencesDir;
        }
        return file;
    }

    private File getSharedPrefsFile(String name) {
        File preferencesDir = getPreferencesDir();
        return makeFilename(preferencesDir, name + ".xml");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        r1 = null;
        r3 = makeBackupFile(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0023, code lost:
        if (r3.exists() == false) goto L_0x002b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        r0.delete();
        r3.renameTo(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002b, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0030, code lost:
        if (r0.exists() == false) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        if (r0.canRead() == false) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r1 = new java.io.FileInputStream(r0);
        r4 = com.alivc.rtc.device.core.persistent.XmlUtils.readMapXml(r1);
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004d, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0050, code lost:
        if (r1 != null) goto L_0x0052;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r1 = new java.io.FileInputStream(r0);
        r1.read(new byte[r1.available()]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006f, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0070, code lost:
        if (r1 != null) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0079, code lost:
        if (r1 != null) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0082, code lost:
        if (r1 != null) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0088, code lost:
        if (r1 != null) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0090, code lost:
        r5 = r4;
        r4 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0094, code lost:
        monitor-enter(GLOBAL_COMMIT_LOCK);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0095, code lost:
        if (r2 != null) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        r2.replace(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x009b, code lost:
        r2 = r8.sSharedPrefs.get(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00a4, code lost:
        if (r2 == null) goto L_0x00a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00a6, code lost:
        r2 = new com.alivc.rtc.device.core.persistent.TransactionXMLFile.MySharedPreferencesImpl(r0, r10, r5);
        r8.sSharedPrefs.put(r0, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00b2, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.alivc.rtc.device.core.persistent.MySharedPreferences getMySharedPreferences(java.lang.String r9, int r10) {
        /*
            r8 = this;
            java.io.File r0 = r8.getSharedPrefsFile(r9)
            java.lang.Object r1 = GLOBAL_COMMIT_LOCK
            monitor-enter(r1)
            java.util.HashMap<java.io.File, com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl> r2 = r8.sSharedPrefs     // Catch:{ all -> 0x00b6 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ all -> 0x00b6 }
            com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl r2 = (com.alivc.rtc.device.core.persistent.TransactionXMLFile.MySharedPreferencesImpl) r2     // Catch:{ all -> 0x00b6 }
            if (r2 == 0) goto L_0x0019
            boolean r3 = r2.hasFileChanged()     // Catch:{ all -> 0x00b6 }
            if (r3 != 0) goto L_0x0019
            monitor-exit(r1)     // Catch:{ all -> 0x00b6 }
            return r2
        L_0x0019:
            monitor-exit(r1)     // Catch:{ all -> 0x00b6 }
            r1 = 0
            java.io.File r3 = makeBackupFile(r0)
            boolean r4 = r3.exists()
            if (r4 == 0) goto L_0x002b
            r0.delete()
            r3.renameTo(r0)
        L_0x002b:
            r4 = 0
            boolean r5 = r0.exists()
            if (r5 == 0) goto L_0x0090
            boolean r5 = r0.canRead()
            if (r5 == 0) goto L_0x0090
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ XmlPullParserException -> 0x0058, Exception -> 0x004f }
            r5.<init>(r0)     // Catch:{ XmlPullParserException -> 0x0058, Exception -> 0x004f }
            r1 = r5
            java.util.HashMap r5 = com.alivc.rtc.device.core.persistent.XmlUtils.readMapXml(r1)     // Catch:{ XmlPullParserException -> 0x0058, Exception -> 0x004f }
            r4 = r5
            r1.close()     // Catch:{ XmlPullParserException -> 0x0058, Exception -> 0x004f }
            r1.close()     // Catch:{ all -> 0x004b }
        L_0x004a:
            goto L_0x0090
        L_0x004b:
            r5 = move-exception
            goto L_0x004a
        L_0x004d:
            r5 = move-exception
            goto L_0x0079
        L_0x004f:
            r5 = move-exception
            if (r1 == 0) goto L_0x0090
            r1.close()     // Catch:{ all -> 0x0056 }
            goto L_0x004a
        L_0x0056:
            r5 = move-exception
            goto L_0x004a
        L_0x0058:
            r5 = move-exception
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0081, all -> 0x006f }
            r6.<init>(r0)     // Catch:{ Exception -> 0x0081, all -> 0x006f }
            r1 = r6
            int r6 = r1.available()     // Catch:{ Exception -> 0x0081, all -> 0x006f }
            byte[] r6 = new byte[r6]     // Catch:{ Exception -> 0x0081, all -> 0x006f }
            r1.read(r6)     // Catch:{ Exception -> 0x0081, all -> 0x006f }
            r1.close()     // Catch:{ all -> 0x006d }
        L_0x006c:
            goto L_0x0088
        L_0x006d:
            r6 = move-exception
            goto L_0x006c
        L_0x006f:
            r6 = move-exception
            if (r1 == 0) goto L_0x0077
            r1.close()     // Catch:{ all -> 0x0076 }
            goto L_0x0077
        L_0x0076:
            r7 = move-exception
        L_0x0077:
            throw r6     // Catch:{ all -> 0x004d }
        L_0x0079:
            if (r1 == 0) goto L_0x0080
            r1.close()     // Catch:{ all -> 0x007f }
            goto L_0x0080
        L_0x007f:
            r6 = move-exception
        L_0x0080:
            throw r5
        L_0x0081:
            r6 = move-exception
            if (r1 == 0) goto L_0x0088
            r1.close()     // Catch:{ all -> 0x006d }
            goto L_0x006c
        L_0x0088:
            if (r1 == 0) goto L_0x0090
            r1.close()     // Catch:{ all -> 0x008e }
            goto L_0x004a
        L_0x008e:
            r5 = move-exception
            goto L_0x004a
        L_0x0090:
            r5 = r4
            r4 = r1
            java.lang.Object r6 = GLOBAL_COMMIT_LOCK
            monitor-enter(r6)
            if (r2 == 0) goto L_0x009b
            r2.replace(r5)     // Catch:{ all -> 0x00b3 }
            goto L_0x00b1
        L_0x009b:
            java.util.HashMap<java.io.File, com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl> r1 = r8.sSharedPrefs     // Catch:{ all -> 0x00b3 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x00b3 }
            com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl r1 = (com.alivc.rtc.device.core.persistent.TransactionXMLFile.MySharedPreferencesImpl) r1     // Catch:{ all -> 0x00b3 }
            r2 = r1
            if (r2 != 0) goto L_0x00b1
            com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl r1 = new com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl     // Catch:{ all -> 0x00b3 }
            r1.<init>(r0, r10, r5)     // Catch:{ all -> 0x00b3 }
            r2 = r1
            java.util.HashMap<java.io.File, com.alivc.rtc.device.core.persistent.TransactionXMLFile$MySharedPreferencesImpl> r1 = r8.sSharedPrefs     // Catch:{ all -> 0x00b3 }
            r1.put(r0, r2)     // Catch:{ all -> 0x00b3 }
        L_0x00b1:
            monitor-exit(r6)     // Catch:{ all -> 0x00b3 }
            return r2
        L_0x00b3:
            r1 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00b3 }
            throw r1
        L_0x00b6:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00b6 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alivc.rtc.device.core.persistent.TransactionXMLFile.getMySharedPreferences(java.lang.String, int):com.alivc.rtc.device.core.persistent.MySharedPreferences");
    }

    /* access modifiers changed from: private */
    public static File makeBackupFile(File prefsFile) {
        return new File(prefsFile.getPath() + ".bak");
    }

    private static final class MySharedPreferencesImpl implements MySharedPreferences {
        private static final Object mContent = new Object();
        private boolean hasChange = false;
        private final File mBackupFile;
        private final File mFile;
        /* access modifiers changed from: private */
        public WeakHashMap<MySharedPreferences.OnSharedPreferenceChangeListener, Object> mListeners;
        /* access modifiers changed from: private */
        public Map mMap;
        private final int mMode;

        MySharedPreferencesImpl(File file, int mode, Map initialContents) {
            this.mFile = file;
            this.mBackupFile = TransactionXMLFile.makeBackupFile(file);
            this.mMode = mode;
            this.mMap = initialContents != null ? initialContents : new HashMap();
            this.mListeners = new WeakHashMap<>();
        }

        public boolean checkFile() {
            if (this.mFile == null || !new File(this.mFile.getAbsolutePath()).exists()) {
                return false;
            }
            return true;
        }

        public void setHasChange(boolean hasChange2) {
            synchronized (this) {
                this.hasChange = hasChange2;
            }
        }

        public boolean hasFileChanged() {
            boolean z;
            synchronized (this) {
                z = this.hasChange;
            }
            return z;
        }

        public void replace(Map newContents) {
            if (newContents != null) {
                synchronized (this) {
                    this.mMap = newContents;
                }
            }
        }

        public void registerOnSharedPreferenceChangeListener(MySharedPreferences.OnSharedPreferenceChangeListener listener) {
            synchronized (this) {
                this.mListeners.put(listener, mContent);
            }
        }

        public void unregisterOnSharedPreferenceChangeListener(MySharedPreferences.OnSharedPreferenceChangeListener listener) {
            synchronized (this) {
                this.mListeners.remove(listener);
            }
        }

        public Map<String, ?> getAll() {
            HashMap hashMap;
            synchronized (this) {
                hashMap = new HashMap(this.mMap);
            }
            return hashMap;
        }

        public String getString(String key, String defValue) {
            String str;
            synchronized (this) {
                String v = (String) this.mMap.get(key);
                str = v != null ? v : defValue;
            }
            return str;
        }

        public int getInt(String key, int defValue) {
            int intValue;
            synchronized (this) {
                Integer v = (Integer) this.mMap.get(key);
                intValue = v != null ? v.intValue() : defValue;
            }
            return intValue;
        }

        public long getLong(String key, long defValue) {
            long longValue;
            synchronized (this) {
                Long v = (Long) this.mMap.get(key);
                longValue = v != null ? v.longValue() : defValue;
            }
            return longValue;
        }

        public float getFloat(String key, float defValue) {
            float floatValue;
            synchronized (this) {
                Float v = (Float) this.mMap.get(key);
                floatValue = v != null ? v.floatValue() : defValue;
            }
            return floatValue;
        }

        public boolean getBoolean(String key, boolean defValue) {
            boolean booleanValue;
            synchronized (this) {
                Boolean v = (Boolean) this.mMap.get(key);
                booleanValue = v != null ? v.booleanValue() : defValue;
            }
            return booleanValue;
        }

        public boolean contains(String key) {
            boolean containsKey;
            synchronized (this) {
                containsKey = this.mMap.containsKey(key);
            }
            return containsKey;
        }

        public MySharedPreferences.MyEditor edit() {
            return new EditorImpl();
        }

        private FileOutputStream createFileOutputStream(File file) {
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                if (!file.getParentFile().mkdir()) {
                    return null;
                }
                try {
                    return new FileOutputStream(file);
                } catch (FileNotFoundException e2) {
                    return null;
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean writeFileLocked() {
            if (this.mFile.exists()) {
                if (this.mBackupFile.exists()) {
                    this.mFile.delete();
                } else if (!this.mFile.renameTo(this.mBackupFile)) {
                    return false;
                }
            }
            try {
                FileOutputStream str = createFileOutputStream(this.mFile);
                if (str == null) {
                    return false;
                }
                XmlUtils.writeMapXml(this.mMap, str);
                str.close();
                this.mBackupFile.delete();
                return true;
            } catch (Exception e) {
                if (this.mFile.exists()) {
                    this.mFile.delete();
                }
                return false;
            }
        }

        public final class EditorImpl implements MySharedPreferences.MyEditor {
            private boolean mClear = false;
            private final Map<String, Object> mModified = new HashMap();

            public EditorImpl() {
            }

            public MySharedPreferences.MyEditor putString(String key, String value) {
                synchronized (this) {
                    this.mModified.put(key, value);
                }
                return this;
            }

            public MySharedPreferences.MyEditor putInt(String key, int value) {
                synchronized (this) {
                    this.mModified.put(key, Integer.valueOf(value));
                }
                return this;
            }

            public MySharedPreferences.MyEditor putLong(String key, long value) {
                synchronized (this) {
                    this.mModified.put(key, Long.valueOf(value));
                }
                return this;
            }

            public MySharedPreferences.MyEditor putFloat(String key, float value) {
                synchronized (this) {
                    this.mModified.put(key, Float.valueOf(value));
                }
                return this;
            }

            public MySharedPreferences.MyEditor putBoolean(String key, boolean value) {
                synchronized (this) {
                    this.mModified.put(key, Boolean.valueOf(value));
                }
                return this;
            }

            public MySharedPreferences.MyEditor remove(String key) {
                synchronized (this) {
                    this.mModified.put(key, this);
                }
                return this;
            }

            public MySharedPreferences.MyEditor clear() {
                synchronized (this) {
                    this.mClear = true;
                }
                return this;
            }

            public boolean commit() {
                boolean hasListeners;
                boolean returnValue;
                List<String> keysModified = null;
                Set<MySharedPreferences.OnSharedPreferenceChangeListener> listeners = null;
                synchronized (TransactionXMLFile.GLOBAL_COMMIT_LOCK) {
                    hasListeners = MySharedPreferencesImpl.this.mListeners.size() > 0;
                    if (hasListeners) {
                        keysModified = new ArrayList<>();
                        listeners = new HashSet<>(MySharedPreferencesImpl.this.mListeners.keySet());
                    }
                    synchronized (this) {
                        if (this.mClear) {
                            MySharedPreferencesImpl.this.mMap.clear();
                            this.mClear = false;
                        }
                        for (Map.Entry<String, Object> e : this.mModified.entrySet()) {
                            String k = e.getKey();
                            Object v = e.getValue();
                            if (v == this) {
                                MySharedPreferencesImpl.this.mMap.remove(k);
                            } else {
                                MySharedPreferencesImpl.this.mMap.put(k, v);
                            }
                            if (hasListeners) {
                                keysModified.add(k);
                            }
                        }
                        this.mModified.clear();
                    }
                    returnValue = MySharedPreferencesImpl.this.writeFileLocked();
                    if (returnValue) {
                        MySharedPreferencesImpl.this.setHasChange(true);
                    }
                }
                if (hasListeners) {
                    for (int i = keysModified.size() - 1; i >= 0; i--) {
                        String key = keysModified.get(i);
                        for (MySharedPreferences.OnSharedPreferenceChangeListener listener : listeners) {
                            if (listener != null) {
                                listener.onSharedPreferenceChanged(MySharedPreferencesImpl.this, key);
                            }
                        }
                    }
                }
                return returnValue;
            }
        }
    }
}
