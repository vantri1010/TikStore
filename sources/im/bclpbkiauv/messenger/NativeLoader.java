package im.bclpbkiauv.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NativeLoader {
    private static final String LIB_NAME = "tmessages.31";
    private static final String LIB_SO_NAME = "libtmessages.31.so";
    private static final int LIB_VERSION = 31;
    private static final String LOCALE_LIB_SO_NAME = "libtmessages.31loc.so";
    private static volatile boolean nativeLoaded = false;
    private String crashPath = "";

    private static native void init(String str, boolean z);

    private static File getNativeLibraryDir(Context context) {
        File f = null;
        if (context != null) {
            try {
                f = new File((String) ApplicationInfo.class.getField("nativeLibraryDir").get(context.getApplicationInfo()));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        if (f == null) {
            f = new File(context.getApplicationInfo().dataDir, "lib");
        }
        if (f.isDirectory()) {
            return f;
        }
        return null;
    }

    private static boolean loadFromZip(Context context, File destDir, File destLocalFile, String folder) {
        try {
            for (File file : destDir.listFiles()) {
                file.delete();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ZipFile zipFile = null;
        InputStream stream = null;
        try {
            zipFile = new ZipFile(context.getApplicationInfo().sourceDir);
            ZipEntry entry = zipFile.getEntry("lib/" + folder + "/" + LIB_SO_NAME);
            if (entry != null) {
                stream = zipFile.getInputStream(entry);
                OutputStream out = new FileOutputStream(destLocalFile);
                byte[] buf = new byte[4096];
                while (true) {
                    int read = stream.read(buf);
                    int len = read;
                    if (read <= 0) {
                        break;
                    }
                    Thread.yield();
                    out.write(buf, 0, len);
                }
                out.close();
                destLocalFile.setReadable(true, false);
                destLocalFile.setExecutable(true, false);
                destLocalFile.setWritable(true);
                try {
                    System.load(destLocalFile.getAbsolutePath());
                    nativeLoaded = true;
                } catch (Error e2) {
                    FileLog.e((Throwable) e2);
                }
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                }
                try {
                    zipFile.close();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
                return true;
            }
            throw new Exception("Unable to find file in apk:lib/" + folder + "/" + LIB_NAME);
        } catch (Exception e5) {
            FileLog.e((Throwable) e5);
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e6) {
                    FileLog.e((Throwable) e6);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e7) {
                    FileLog.e((Throwable) e7);
                }
            }
            return false;
        } catch (Throwable th) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e8) {
                    FileLog.e((Throwable) e8);
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e9) {
                    FileLog.e((Throwable) e9);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00e9 A[Catch:{ all -> 0x001d }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0103  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void initNativeLibs(android.content.Context r8) {
        /*
            java.lang.Class<im.bclpbkiauv.messenger.NativeLoader> r0 = im.bclpbkiauv.messenger.NativeLoader.class
            monitor-enter(r0)
            boolean r1 = nativeLoaded     // Catch:{ all -> 0x0118 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)
            return
        L_0x0009:
            r1 = 1
            java.lang.String r2 = "tmessages.31"
            java.lang.System.loadLibrary(r2)     // Catch:{ Error -> 0x0020 }
            nativeLoaded = r1     // Catch:{ Error -> 0x0020 }
            boolean r2 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x0020 }
            if (r2 == 0) goto L_0x001b
            java.lang.String r2 = "loaded normal lib"
            im.bclpbkiauv.messenger.FileLog.d(r2)     // Catch:{ Error -> 0x0020 }
        L_0x001b:
            monitor-exit(r0)
            return
        L_0x001d:
            r2 = move-exception
            goto L_0x0106
        L_0x0020:
            r2 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001d }
            java.lang.String r2 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "x86_64"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x0035
            java.lang.String r3 = "x86_64"
            goto L_0x0094
        L_0x0035:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "arm64-v8a"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x0042
            java.lang.String r3 = "arm64-v8a"
            goto L_0x0094
        L_0x0042:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "armeabi-v7a"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x004f
            java.lang.String r3 = "armeabi-v7a"
            goto L_0x0094
        L_0x004f:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "armeabi"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x005c
            java.lang.String r3 = "armeabi"
            goto L_0x0094
        L_0x005c:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "x86"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x006b
            java.lang.String r3 = "x86"
            goto L_0x0094
        L_0x006b:
            java.lang.String r3 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = "mips"
            boolean r3 = r3.equalsIgnoreCase(r4)     // Catch:{ Exception -> 0x0095 }
            if (r3 == 0) goto L_0x0078
            java.lang.String r3 = "mips"
            goto L_0x0094
        L_0x0078:
            java.lang.String r3 = "armeabi"
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0095 }
            if (r4 == 0) goto L_0x0094
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0095 }
            r4.<init>()     // Catch:{ Exception -> 0x0095 }
            java.lang.String r5 = "Unsupported arch: "
            r4.append(r5)     // Catch:{ Exception -> 0x0095 }
            java.lang.String r5 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x0095 }
            r4.append(r5)     // Catch:{ Exception -> 0x0095 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0095 }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ Exception -> 0x0095 }
        L_0x0094:
            goto L_0x009b
        L_0x0095:
            r2 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x001d }
            java.lang.String r3 = "armeabi"
        L_0x009b:
            java.lang.String r2 = "os.arch"
            java.lang.String r2 = java.lang.System.getProperty(r2)     // Catch:{ all -> 0x001d }
            if (r2 == 0) goto L_0x00af
            java.lang.String r4 = "686"
            boolean r4 = r2.contains(r4)     // Catch:{ all -> 0x001d }
            if (r4 == 0) goto L_0x00af
            java.lang.String r4 = "x86"
            r3 = r4
        L_0x00af:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x001d }
            java.io.File r5 = r8.getFilesDir()     // Catch:{ all -> 0x001d }
            java.lang.String r6 = "lib"
            r4.<init>(r5, r6)     // Catch:{ all -> 0x001d }
            r4.mkdirs()     // Catch:{ all -> 0x001d }
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x001d }
            java.lang.String r6 = "libtmessages.31loc.so"
            r5.<init>(r4, r6)     // Catch:{ all -> 0x001d }
            boolean r6 = r5.exists()     // Catch:{ all -> 0x001d }
            if (r6 == 0) goto L_0x00e5
            boolean r6 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Error -> 0x00de }
            if (r6 == 0) goto L_0x00d3
            java.lang.String r6 = "Load local lib"
            im.bclpbkiauv.messenger.FileLog.d(r6)     // Catch:{ Error -> 0x00de }
        L_0x00d3:
            java.lang.String r6 = r5.getAbsolutePath()     // Catch:{ Error -> 0x00de }
            java.lang.System.load(r6)     // Catch:{ Error -> 0x00de }
            nativeLoaded = r1     // Catch:{ Error -> 0x00de }
            monitor-exit(r0)
            return
        L_0x00de:
            r6 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x001d }
            r5.delete()     // Catch:{ all -> 0x001d }
        L_0x00e5:
            boolean r6 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x001d }
            if (r6 == 0) goto L_0x00fd
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x001d }
            r6.<init>()     // Catch:{ all -> 0x001d }
            java.lang.String r7 = "Library not found, arch = "
            r6.append(r7)     // Catch:{ all -> 0x001d }
            r6.append(r3)     // Catch:{ all -> 0x001d }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x001d }
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r6)     // Catch:{ all -> 0x001d }
        L_0x00fd:
            boolean r6 = loadFromZip(r8, r4, r5, r3)     // Catch:{ all -> 0x001d }
            if (r6 == 0) goto L_0x0105
            monitor-exit(r0)
            return
        L_0x0105:
            goto L_0x0109
        L_0x0106:
            r2.printStackTrace()     // Catch:{ all -> 0x0118 }
        L_0x0109:
            java.lang.String r2 = "tmessages.31"
            java.lang.System.loadLibrary(r2)     // Catch:{ Error -> 0x0112 }
            nativeLoaded = r1     // Catch:{ Error -> 0x0112 }
            goto L_0x0116
        L_0x0112:
            r1 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0118 }
        L_0x0116:
            monitor-exit(r0)
            return
        L_0x0118:
            r8 = move-exception
            monitor-exit(r0)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.NativeLoader.initNativeLibs(android.content.Context):void");
    }
}
