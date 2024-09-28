package com.blankj.utilcode.util;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {
    private static final int BUFFER_LEN = 8192;

    private ZipUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean zipFiles(Collection<String> srcFiles, String zipFilePath) throws IOException {
        return zipFiles(srcFiles, zipFilePath, (String) null);
    }

    public static boolean zipFiles(Collection<String> srcFilePaths, String zipFilePath, String comment) throws IOException {
        if (srcFilePaths == null || zipFilePath == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            ZipOutputStream zos2 = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (String srcFile : srcFilePaths) {
                if (!zipFile(getFileByPath(srcFile), "", zos2, comment)) {
                    zos2.finish();
                    zos2.close();
                    return false;
                }
            }
            zos2.finish();
            zos2.close();
            return true;
        } catch (Throwable th) {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
            throw th;
        }
    }

    public static boolean zipFiles(Collection<File> srcFiles, File zipFile) throws IOException {
        return zipFiles(srcFiles, zipFile, (String) null);
    }

    public static boolean zipFiles(Collection<File> srcFiles, File zipFile, String comment) throws IOException {
        if (srcFiles == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            ZipOutputStream zos2 = new ZipOutputStream(new FileOutputStream(zipFile));
            for (File srcFile : srcFiles) {
                if (!zipFile(srcFile, "", zos2, comment)) {
                    zos2.finish();
                    zos2.close();
                    return false;
                }
            }
            zos2.finish();
            zos2.close();
            return true;
        } catch (Throwable th) {
            if (zos != null) {
                zos.finish();
                zos.close();
            }
            throw th;
        }
    }

    public static boolean zipFile(String srcFilePath, String zipFilePath) throws IOException {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), (String) null);
    }

    public static boolean zipFile(String srcFilePath, String zipFilePath, String comment) throws IOException {
        return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), comment);
    }

    public static boolean zipFile(File srcFile, File zipFile) throws IOException {
        return zipFile(srcFile, zipFile, (String) null);
    }

    public static boolean zipFile(File srcFile, File zipFile, String comment) throws IOException {
        if (srcFile == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zos = null;
        try {
            ZipOutputStream zos2 = new ZipOutputStream(new FileOutputStream(zipFile));
            boolean zipFile2 = zipFile(srcFile, "", zos2, comment);
            zos2.close();
            return zipFile2;
        } catch (Throwable th) {
            if (zos != null) {
                zos.close();
            }
            throw th;
        }
    }

    private static boolean zipFile(File srcFile, String rootPath, ZipOutputStream zos, String comment) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(rootPath);
        sb.append(isSpace(rootPath) ? "" : File.separator);
        sb.append(srcFile.getName());
        String rootPath2 = sb.toString();
        if (srcFile.isDirectory()) {
            File[] fileList = srcFile.listFiles();
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath2 + '/');
                entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
                return true;
            }
            for (File file : fileList) {
                if (!zipFile(file, rootPath2, zos, comment)) {
                    return false;
                }
            }
            return true;
        }
        InputStream is = null;
        try {
            InputStream is2 = new BufferedInputStream(new FileInputStream(srcFile));
            ZipEntry entry2 = new ZipEntry(rootPath2);
            entry2.setComment(comment);
            zos.putNextEntry(entry2);
            byte[] buffer = new byte[8192];
            while (true) {
                int read = is2.read(buffer, 0, 8192);
                int len = read;
                if (read != -1) {
                    zos.write(buffer, 0, len);
                } else {
                    zos.closeEntry();
                    is2.close();
                    return true;
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                is.close();
            }
            throw th;
        }
    }

    public static List<File> unzipFile(String zipFilePath, String destDirPath) throws IOException {
        return unzipFileByKeyword(zipFilePath, destDirPath, (String) null);
    }

    public static List<File> unzipFile(File zipFile, File destDir) throws IOException {
        return unzipFileByKeyword(zipFile, destDir, (String) null);
    }

    public static List<File> unzipFileByKeyword(String zipFilePath, String destDirPath, String keyword) throws IOException {
        return unzipFileByKeyword(getFileByPath(zipFilePath), getFileByPath(destDirPath), keyword);
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:17:0x0061=Splitter:B:17:0x0061, B:7:0x0026=Splitter:B:7:0x0026} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.io.File> unzipFileByKeyword(java.io.File r12, java.io.File r13, java.lang.String r14) throws java.io.IOException {
        /*
            if (r12 == 0) goto L_0x00ac
            if (r13 != 0) goto L_0x0006
            goto L_0x00ac
        L_0x0006:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.zip.ZipFile r1 = new java.util.zip.ZipFile
            r1.<init>(r12)
            java.util.Enumeration r2 = r1.entries()
            boolean r3 = isSpace(r14)     // Catch:{ all -> 0x00a7 }
            java.lang.String r4 = " is dangerous!"
            java.lang.String r5 = "entryName: "
            java.lang.String r6 = "ZipUtils"
            java.lang.String r7 = "../"
            java.lang.String r8 = "/"
            java.lang.String r9 = "\\"
            if (r3 == 0) goto L_0x0061
        L_0x0026:
            boolean r3 = r2.hasMoreElements()     // Catch:{ all -> 0x00a7 }
            if (r3 == 0) goto L_0x00a2
            java.lang.Object r3 = r2.nextElement()     // Catch:{ all -> 0x00a7 }
            java.util.zip.ZipEntry r3 = (java.util.zip.ZipEntry) r3     // Catch:{ all -> 0x00a7 }
            java.lang.String r10 = r3.getName()     // Catch:{ all -> 0x00a7 }
            java.lang.String r10 = r10.replace(r9, r8)     // Catch:{ all -> 0x00a7 }
            boolean r11 = r10.contains(r7)     // Catch:{ all -> 0x00a7 }
            if (r11 == 0) goto L_0x0056
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a7 }
            r11.<init>()     // Catch:{ all -> 0x00a7 }
            r11.append(r5)     // Catch:{ all -> 0x00a7 }
            r11.append(r10)     // Catch:{ all -> 0x00a7 }
            r11.append(r4)     // Catch:{ all -> 0x00a7 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00a7 }
            android.util.Log.e(r6, r11)     // Catch:{ all -> 0x00a7 }
            goto L_0x0026
        L_0x0056:
            boolean r11 = unzipChildFile(r13, r0, r1, r3, r10)     // Catch:{ all -> 0x00a7 }
            if (r11 != 0) goto L_0x0060
            r1.close()
            return r0
        L_0x0060:
            goto L_0x0026
        L_0x0061:
            boolean r3 = r2.hasMoreElements()     // Catch:{ all -> 0x00a7 }
            if (r3 == 0) goto L_0x00a2
            java.lang.Object r3 = r2.nextElement()     // Catch:{ all -> 0x00a7 }
            java.util.zip.ZipEntry r3 = (java.util.zip.ZipEntry) r3     // Catch:{ all -> 0x00a7 }
            java.lang.String r10 = r3.getName()     // Catch:{ all -> 0x00a7 }
            java.lang.String r10 = r10.replace(r9, r8)     // Catch:{ all -> 0x00a7 }
            boolean r11 = r10.contains(r7)     // Catch:{ all -> 0x00a7 }
            if (r11 == 0) goto L_0x0091
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a7 }
            r11.<init>()     // Catch:{ all -> 0x00a7 }
            r11.append(r5)     // Catch:{ all -> 0x00a7 }
            r11.append(r10)     // Catch:{ all -> 0x00a7 }
            r11.append(r4)     // Catch:{ all -> 0x00a7 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00a7 }
            android.util.Log.e(r6, r11)     // Catch:{ all -> 0x00a7 }
            goto L_0x0061
        L_0x0091:
            boolean r11 = r10.contains(r14)     // Catch:{ all -> 0x00a7 }
            if (r11 == 0) goto L_0x00a1
            boolean r11 = unzipChildFile(r13, r0, r1, r3, r10)     // Catch:{ all -> 0x00a7 }
            if (r11 != 0) goto L_0x00a1
            r1.close()
            return r0
        L_0x00a1:
            goto L_0x0061
        L_0x00a2:
            r1.close()
            return r0
        L_0x00a7:
            r3 = move-exception
            r1.close()
            throw r3
        L_0x00ac:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.ZipUtils.unzipFileByKeyword(java.io.File, java.io.File, java.lang.String):java.util.List");
    }

    private static boolean unzipChildFile(File destDir, List<File> files, ZipFile zip, ZipEntry entry, String name) throws IOException {
        File file = new File(destDir, name);
        files.add(file);
        if (entry.isDirectory()) {
            return createOrExistsDir(file);
        }
        if (!createOrExistsFile(file)) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            InputStream in2 = new BufferedInputStream(zip.getInputStream(entry));
            OutputStream out2 = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[8192];
            while (true) {
                int read = in2.read(buffer);
                int len = read;
                if (read != -1) {
                    out2.write(buffer, 0, len);
                } else {
                    in2.close();
                    out2.close();
                    return true;
                }
            }
        } catch (Throwable th) {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            throw th;
        }
    }

    public static List<String> getFilesPath(String zipFilePath) throws IOException {
        return getFilesPath(getFileByPath(zipFilePath));
    }

    public static List<String> getFilesPath(File zipFile) throws IOException {
        if (zipFile == null) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            String entryName = ((ZipEntry) entries.nextElement()).getName().replace("\\", "/");
            if (entryName.contains("../")) {
                Log.e("ZipUtils", "entryName: " + entryName + " is dangerous!");
                paths.add(entryName);
            } else {
                paths.add(entryName);
            }
        }
        zip.close();
        return paths;
    }

    public static List<String> getComments(String zipFilePath) throws IOException {
        return getComments(getFileByPath(zipFilePath));
    }

    public static List<String> getComments(File zipFile) throws IOException {
        if (zipFile == null) {
            return null;
        }
        List<String> comments = new ArrayList<>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            comments.add(((ZipEntry) entries.nextElement()).getComment());
        }
        zip.close();
        return comments;
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
