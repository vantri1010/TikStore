package com.ta.utdid2.b.a;

import com.ta.utdid2.b.a.b;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;

public class d {
    private static final Object b = new Object();
    private File a;

    /* renamed from: a  reason: collision with other field name */
    private final Object f15a = new Object();

    /* renamed from: a  reason: collision with other field name */
    private HashMap<File, a> f16a = new HashMap<>();

    public d(String str) {
        if (str == null || str.length() <= 0) {
            throw new RuntimeException("Directory can not be empty");
        }
        this.a = new File(str);
    }

    private File a(File file, String str) {
        if (str.indexOf(File.separatorChar) < 0) {
            return new File(file, str);
        }
        throw new IllegalArgumentException("File " + str + " contains a path separator");
    }

    private File a() {
        File file;
        synchronized (this.f15a) {
            file = this.a;
        }
        return file;
    }

    private File b(String str) {
        File a2 = a();
        return a(a2, str + ".xml");
    }

    /* JADX WARNING: type inference failed for: r2v1 */
    /* JADX WARNING: type inference failed for: r2v2, types: [java.util.Map] */
    /* JADX WARNING: type inference failed for: r2v4 */
    /* JADX WARNING: type inference failed for: r2v14 */
    /* JADX WARNING: type inference failed for: r2v15, types: [java.io.FileInputStream] */
    /* JADX WARNING: type inference failed for: r2v17 */
    /* JADX WARNING: type inference failed for: r2v18 */
    /* JADX WARNING: type inference failed for: r2v19 */
    /* JADX WARNING: type inference failed for: r2v20 */
    /* JADX WARNING: type inference failed for: r2v27 */
    /* JADX WARNING: type inference failed for: r2v28 */
    /* JADX WARNING: type inference failed for: r2v29 */
    /* JADX WARNING: type inference failed for: r2v30 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0088 A[SYNTHETIC, Splitter:B:53:0x0088] */
    public com.ta.utdid2.b.a.b a(java.lang.String r6, int r7) {
        /*
            r5 = this;
            java.io.File r6 = r5.b((java.lang.String) r6)
            java.lang.Object r0 = b
            monitor-enter(r0)
            java.util.HashMap<java.io.File, com.ta.utdid2.b.a.d$a> r1 = r5.f16a     // Catch:{ all -> 0x00cb }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x00cb }
            com.ta.utdid2.b.a.d$a r1 = (com.ta.utdid2.b.a.d.a) r1     // Catch:{ all -> 0x00cb }
            if (r1 == 0) goto L_0x0019
            boolean r2 = r1.d()     // Catch:{ all -> 0x00cb }
            if (r2 != 0) goto L_0x0019
            monitor-exit(r0)     // Catch:{ all -> 0x00cb }
            return r1
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x00cb }
            java.io.File r0 = a(r6)
            boolean r2 = r0.exists()
            if (r2 == 0) goto L_0x002b
            r6.delete()
            r0.renameTo(r6)
        L_0x002b:
            boolean r0 = r6.exists()
            r2 = 0
            if (r0 == 0) goto L_0x00a8
            boolean r0 = r6.canRead()
            if (r0 == 0) goto L_0x00a8
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ XmlPullParserException -> 0x0068, Exception -> 0x005c }
            r0.<init>(r6)     // Catch:{ XmlPullParserException -> 0x0068, Exception -> 0x005c }
            java.util.HashMap r2 = com.ta.utdid2.b.a.e.a(r0)     // Catch:{ XmlPullParserException -> 0x0055, Exception -> 0x0050, all -> 0x004d }
            r0.close()     // Catch:{ XmlPullParserException -> 0x0055, Exception -> 0x0050, all -> 0x004d }
            r0.close()     // Catch:{ all -> 0x004b }
        L_0x0049:
            goto L_0x00a8
        L_0x004b:
            r0 = move-exception
            goto L_0x0049
        L_0x004d:
            r6 = move-exception
            r2 = r0
            goto L_0x008e
        L_0x0050:
            r3 = move-exception
            r4 = r2
            r2 = r0
            r0 = r4
            goto L_0x005e
        L_0x0055:
            r3 = move-exception
            r4 = r2
            r2 = r0
            r0 = r4
            goto L_0x006a
        L_0x005a:
            r6 = move-exception
            goto L_0x008e
        L_0x005c:
            r0 = move-exception
            r0 = r2
        L_0x005e:
            if (r2 == 0) goto L_0x0066
            r2.close()     // Catch:{ all -> 0x0064 }
        L_0x0063:
            goto L_0x0066
        L_0x0064:
            r2 = move-exception
            goto L_0x0063
        L_0x0066:
            r2 = r0
            goto L_0x00a8
        L_0x0068:
            r0 = move-exception
            r0 = r2
        L_0x006a:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0096, all -> 0x0085 }
            r3.<init>(r6)     // Catch:{ Exception -> 0x0096, all -> 0x0085 }
            int r2 = r3.available()     // Catch:{ Exception -> 0x0082, all -> 0x007f }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0082, all -> 0x007f }
            r3.read(r2)     // Catch:{ Exception -> 0x0082, all -> 0x007f }
            r3.close()     // Catch:{ all -> 0x007d }
        L_0x007c:
            goto L_0x00a0
        L_0x007d:
            r2 = move-exception
            goto L_0x007c
        L_0x007f:
            r6 = move-exception
            r2 = r3
            goto L_0x0086
        L_0x0082:
            r2 = move-exception
            r2 = r3
            goto L_0x0097
        L_0x0085:
            r6 = move-exception
        L_0x0086:
            if (r2 == 0) goto L_0x008d
            r2.close()     // Catch:{ all -> 0x008c }
            goto L_0x008d
        L_0x008c:
            r7 = move-exception
        L_0x008d:
            throw r6     // Catch:{ all -> 0x005a }
        L_0x008e:
            if (r2 == 0) goto L_0x0095
            r2.close()     // Catch:{ all -> 0x0094 }
            goto L_0x0095
        L_0x0094:
            r7 = move-exception
        L_0x0095:
            throw r6
        L_0x0096:
            r3 = move-exception
        L_0x0097:
            if (r2 == 0) goto L_0x009f
            r2.close()     // Catch:{ all -> 0x009d }
        L_0x009c:
            goto L_0x009f
        L_0x009d:
            r3 = move-exception
            goto L_0x009c
        L_0x009f:
            r3 = r2
        L_0x00a0:
            if (r3 == 0) goto L_0x0066
            r3.close()     // Catch:{ all -> 0x00a6 }
            goto L_0x0063
        L_0x00a6:
            r2 = move-exception
            goto L_0x0063
        L_0x00a8:
            java.lang.Object r3 = b
            monitor-enter(r3)
            if (r1 == 0) goto L_0x00b1
            r1.a((java.util.Map) r2)     // Catch:{ all -> 0x00c8 }
            goto L_0x00c6
        L_0x00b1:
            java.util.HashMap<java.io.File, com.ta.utdid2.b.a.d$a> r0 = r5.f16a     // Catch:{ all -> 0x00c8 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x00c8 }
            r1 = r0
            com.ta.utdid2.b.a.d$a r1 = (com.ta.utdid2.b.a.d.a) r1     // Catch:{ all -> 0x00c8 }
            if (r1 != 0) goto L_0x00c6
            com.ta.utdid2.b.a.d$a r1 = new com.ta.utdid2.b.a.d$a     // Catch:{ all -> 0x00c8 }
            r1.<init>(r6, r7, r2)     // Catch:{ all -> 0x00c8 }
            java.util.HashMap<java.io.File, com.ta.utdid2.b.a.d$a> r7 = r5.f16a     // Catch:{ all -> 0x00c8 }
            r7.put(r6, r1)     // Catch:{ all -> 0x00c8 }
        L_0x00c6:
            monitor-exit(r3)     // Catch:{ all -> 0x00c8 }
            return r1
        L_0x00c8:
            r6 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00c8 }
            throw r6
        L_0x00cb:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00cb }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.b.a.d.a(java.lang.String, int):com.ta.utdid2.b.a.b");
    }

    /* access modifiers changed from: private */
    public static File a(File file) {
        return new File(file.getPath() + ".bak");
    }

    private static final class a implements b {
        private static final Object c = new Object();
        private Map a;

        /* renamed from: a  reason: collision with other field name */
        private WeakHashMap<b.C0001b, Object> f17a;
        private final File b;

        /* renamed from: c  reason: collision with other field name */
        private final int f18c;

        /* renamed from: c  reason: collision with other field name */
        private final File f19c;
        private boolean j = false;

        a(File file, int i, Map map) {
            this.b = file;
            this.f19c = d.a(file);
            this.f18c = i;
            this.a = map == null ? new HashMap() : map;
            this.f17a = new WeakHashMap<>();
        }

        public boolean b() {
            if (this.b == null || !new File(this.b.getAbsolutePath()).exists()) {
                return false;
            }
            return true;
        }

        public void a(boolean z) {
            synchronized (this) {
                this.j = z;
            }
        }

        public boolean d() {
            boolean z;
            synchronized (this) {
                z = this.j;
            }
            return z;
        }

        public void a(Map map) {
            if (map != null) {
                synchronized (this) {
                    this.a = map;
                }
            }
        }

        public Map<String, ?> getAll() {
            HashMap hashMap;
            synchronized (this) {
                hashMap = new HashMap(this.a);
            }
            return hashMap;
        }

        public String getString(String key, String defValue) {
            String str;
            synchronized (this) {
                str = (String) this.a.get(key);
                if (str == null) {
                    str = defValue;
                }
            }
            return str;
        }

        public long getLong(String key, long defValue) {
            long longValue;
            synchronized (this) {
                Long l = (Long) this.a.get(key);
                longValue = l != null ? l.longValue() : defValue;
            }
            return longValue;
        }

        /* renamed from: com.ta.utdid2.b.a.d$a$a  reason: collision with other inner class name */
        public final class C0002a implements b.a {
            private final Map<String, Object> b = new HashMap();
            private boolean k = false;

            public C0002a() {
            }

            public b.a a(String str, String str2) {
                synchronized (this) {
                    this.b.put(str, str2);
                }
                return this;
            }

            public b.a a(String str, int i) {
                synchronized (this) {
                    this.b.put(str, Integer.valueOf(i));
                }
                return this;
            }

            public b.a a(String str, long j) {
                synchronized (this) {
                    this.b.put(str, Long.valueOf(j));
                }
                return this;
            }

            public b.a a(String str, float f) {
                synchronized (this) {
                    this.b.put(str, Float.valueOf(f));
                }
                return this;
            }

            public b.a a(String str, boolean z) {
                synchronized (this) {
                    this.b.put(str, Boolean.valueOf(z));
                }
                return this;
            }

            public b.a a(String str) {
                synchronized (this) {
                    this.b.put(str, this);
                }
                return this;
            }

            public b.a b() {
                synchronized (this) {
                    this.k = true;
                }
                return this;
            }

            public boolean commit() {
                boolean z;
                ArrayList arrayList;
                HashSet<b.C0001b> hashSet;
                boolean a2;
                synchronized (d.a()) {
                    z = a.a(a.this).size() > 0;
                    arrayList = null;
                    if (z) {
                        arrayList = new ArrayList();
                        hashSet = new HashSet<>(a.a(a.this).keySet());
                    } else {
                        hashSet = null;
                    }
                    synchronized (this) {
                        if (this.k) {
                            a.a(a.this).clear();
                            this.k = false;
                        }
                        for (Map.Entry next : this.b.entrySet()) {
                            String str = (String) next.getKey();
                            Object value = next.getValue();
                            if (value == this) {
                                a.a(a.this).remove(str);
                            } else {
                                a.a(a.this).put(str, value);
                            }
                            if (z) {
                                arrayList.add(str);
                            }
                        }
                        this.b.clear();
                    }
                    a2 = a.a(a.this);
                    if (a2) {
                        a.this.a(true);
                    }
                }
                if (z) {
                    for (int size = arrayList.size() - 1; size >= 0; size--) {
                        String str2 = (String) arrayList.get(size);
                        for (b.C0001b bVar : hashSet) {
                            if (bVar != null) {
                                bVar.a(a.this, str2);
                            }
                        }
                    }
                }
                return a2;
            }
        }

        public b.a a() {
            return new C0002a();
        }

        private FileOutputStream a(File file) {
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

        private boolean e() {
            if (this.b.exists()) {
                if (this.f19c.exists()) {
                    this.b.delete();
                } else if (!this.b.renameTo(this.f19c)) {
                    return false;
                }
            }
            try {
                FileOutputStream a2 = a(this.b);
                if (a2 == null) {
                    return false;
                }
                e.a(this.a, (OutputStream) a2);
                a2.close();
                this.f19c.delete();
                return true;
            } catch (Exception e) {
                if (this.b.exists()) {
                    this.b.delete();
                }
                return false;
            }
        }
    }
}
