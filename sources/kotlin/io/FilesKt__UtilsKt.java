package kotlin.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u001a(\u0010\t\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a(\u0010\r\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\u00012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0002\u001a8\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\u001a\b\u0002\u0010\u0012\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00150\u0013\u001a&\u0010\u0016\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u001a\n\u0010\u0019\u001a\u00020\u000f*\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010\u001a\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\n\u0010\u001c\u001a\u00020\u0002*\u00020\u0002\u001a\u001d\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00020\u001d*\b\u0012\u0004\u0012\u00020\u00020\u001dH\u0002¢\u0006\u0002\b\u001e\u001a\u0011\u0010\u001c\u001a\u00020\u001f*\u00020\u001fH\u0002¢\u0006\u0002\b\u001e\u001a\u0012\u0010 \u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0014\u0010\"\u001a\u0004\u0018\u00010\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010#\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010$\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0002\u001a\u0012\u0010&\u001a\u00020\u0002*\u00020\u00022\u0006\u0010%\u001a\u00020\u0001\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0002\u001a\u0012\u0010'\u001a\u00020\u000f*\u00020\u00022\u0006\u0010\u001b\u001a\u00020\u0001\u001a\u0012\u0010(\u001a\u00020\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002\u001a\u001b\u0010)\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0002H\u0002¢\u0006\u0002\b*\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0004\"\u0015\u0010\u0007\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\b\u0010\u0004¨\u0006+"}, d2 = {"extension", "", "Ljava/io/File;", "getExtension", "(Ljava/io/File;)Ljava/lang/String;", "invariantSeparatorsPath", "getInvariantSeparatorsPath", "nameWithoutExtension", "getNameWithoutExtension", "createTempDir", "prefix", "suffix", "directory", "createTempFile", "copyRecursively", "", "target", "overwrite", "onError", "Lkotlin/Function2;", "Ljava/io/IOException;", "Lkotlin/io/OnErrorAction;", "copyTo", "bufferSize", "", "deleteRecursively", "endsWith", "other", "normalize", "", "normalize$FilesKt__UtilsKt", "Lkotlin/io/FilePathComponents;", "relativeTo", "base", "relativeToOrNull", "relativeToOrSelf", "resolve", "relative", "resolveSibling", "startsWith", "toRelativeString", "toRelativeStringOrNull", "toRelativeStringOrNull$FilesKt__UtilsKt", "kotlin-stdlib"}, k = 5, mv = {1, 1, 15}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: Utils.kt */
class FilesKt__UtilsKt extends FilesKt__FileTreeWalkKt {
    public static /* synthetic */ File createTempDir$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "tmp";
        }
        if ((i & 2) != 0) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return FilesKt.createTempDir(str, str2, file);
    }

    public static final File createTempDir(String prefix, String suffix, File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File dir = File.createTempFile(prefix, suffix, directory);
        dir.delete();
        if (dir.mkdir()) {
            Intrinsics.checkExpressionValueIsNotNull(dir, "dir");
            return dir;
        }
        throw new IOException("Unable to create temporary directory " + dir + '.');
    }

    public static /* synthetic */ File createTempFile$default(String str, String str2, File file, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "tmp";
        }
        if ((i & 2) != 0) {
            str2 = null;
        }
        if ((i & 4) != 0) {
            file = null;
        }
        return FilesKt.createTempFile(str, str2, file);
    }

    public static final File createTempFile(String prefix, String suffix, File directory) {
        Intrinsics.checkParameterIsNotNull(prefix, "prefix");
        File createTempFile = File.createTempFile(prefix, suffix, directory);
        Intrinsics.checkExpressionValueIsNotNull(createTempFile, "File.createTempFile(prefix, suffix, directory)");
        return createTempFile;
    }

    public static final String getExtension(File $this$extension) {
        Intrinsics.checkParameterIsNotNull($this$extension, "$this$extension");
        String name = $this$extension.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "name");
        return StringsKt.substringAfterLast(name, '.', "");
    }

    public static final String getInvariantSeparatorsPath(File $this$invariantSeparatorsPath) {
        Intrinsics.checkParameterIsNotNull($this$invariantSeparatorsPath, "$this$invariantSeparatorsPath");
        if (File.separatorChar != '/') {
            String path = $this$invariantSeparatorsPath.getPath();
            Intrinsics.checkExpressionValueIsNotNull(path, "path");
            return StringsKt.replace$default(path, File.separatorChar, '/', false, 4, (Object) null);
        }
        String path2 = $this$invariantSeparatorsPath.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path2, "path");
        return path2;
    }

    public static final String getNameWithoutExtension(File $this$nameWithoutExtension) {
        Intrinsics.checkParameterIsNotNull($this$nameWithoutExtension, "$this$nameWithoutExtension");
        String name = $this$nameWithoutExtension.getName();
        Intrinsics.checkExpressionValueIsNotNull(name, "name");
        return StringsKt.substringBeforeLast$default(name, ".", (String) null, 2, (Object) null);
    }

    public static final String toRelativeString(File $this$toRelativeString, File base) {
        Intrinsics.checkParameterIsNotNull($this$toRelativeString, "$this$toRelativeString");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String relativeStringOrNull$FilesKt__UtilsKt = toRelativeStringOrNull$FilesKt__UtilsKt($this$toRelativeString, base);
        if (relativeStringOrNull$FilesKt__UtilsKt != null) {
            return relativeStringOrNull$FilesKt__UtilsKt;
        }
        throw new IllegalArgumentException("this and base files have different roots: " + $this$toRelativeString + " and " + base + '.');
    }

    public static final File relativeTo(File $this$relativeTo, File base) {
        Intrinsics.checkParameterIsNotNull($this$relativeTo, "$this$relativeTo");
        Intrinsics.checkParameterIsNotNull(base, "base");
        return new File(FilesKt.toRelativeString($this$relativeTo, base));
    }

    public static final File relativeToOrSelf(File $this$relativeToOrSelf, File base) {
        Intrinsics.checkParameterIsNotNull($this$relativeToOrSelf, "$this$relativeToOrSelf");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($this$relativeToOrSelf, base);
        return p1 != null ? new File(p1) : $this$relativeToOrSelf;
    }

    public static final File relativeToOrNull(File $this$relativeToOrNull, File base) {
        Intrinsics.checkParameterIsNotNull($this$relativeToOrNull, "$this$relativeToOrNull");
        Intrinsics.checkParameterIsNotNull(base, "base");
        String p1 = toRelativeStringOrNull$FilesKt__UtilsKt($this$relativeToOrNull, base);
        if (p1 != null) {
            return new File(p1);
        }
        return null;
    }

    private static final String toRelativeStringOrNull$FilesKt__UtilsKt(File $this$toRelativeStringOrNull, File base) {
        FilePathComponents thisComponents = normalize$FilesKt__UtilsKt(FilesKt.toComponents($this$toRelativeStringOrNull));
        FilePathComponents baseComponents = normalize$FilesKt__UtilsKt(FilesKt.toComponents(base));
        if (!Intrinsics.areEqual((Object) thisComponents.getRoot(), (Object) baseComponents.getRoot())) {
            return null;
        }
        int baseCount = baseComponents.getSize();
        int thisCount = thisComponents.getSize();
        File file = $this$toRelativeStringOrNull;
        int i = 0;
        int maxSameCount = Math.min(thisCount, baseCount);
        while (i < maxSameCount && Intrinsics.areEqual((Object) thisComponents.getSegments().get(i), (Object) baseComponents.getSegments().get(i))) {
            i++;
        }
        int sameCount = i;
        StringBuilder res = new StringBuilder();
        int i2 = baseCount - 1;
        if (i2 >= sameCount) {
            while (!Intrinsics.areEqual((Object) baseComponents.getSegments().get(i2).getName(), (Object) "..")) {
                res.append("..");
                if (i2 != sameCount) {
                    res.append(File.separatorChar);
                }
                if (i2 != sameCount) {
                    i2--;
                }
            }
            return null;
        }
        if (sameCount < thisCount) {
            if (sameCount < baseCount) {
                res.append(File.separatorChar);
            }
            String str = File.separator;
            Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
            CollectionsKt.joinTo$default(CollectionsKt.drop(thisComponents.getSegments(), sameCount), res, str, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 124, (Object) null);
        }
        return res.toString();
    }

    public static /* synthetic */ File copyTo$default(File file, File file2, boolean z, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            z = false;
        }
        if ((i2 & 4) != 0) {
            i = 8192;
        }
        return FilesKt.copyTo(file, file2, z, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0083, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        kotlin.io.CloseableKt.closeFinally(r5, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0087, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x008a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x008b, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x008e, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.io.File copyTo(java.io.File r10, java.io.File r11, boolean r12, int r13) {
        /*
            java.lang.String r0 = "$this$copyTo"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r0)
            java.lang.String r0 = "target"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r11, r0)
            boolean r0 = r10.exists()
            if (r0 == 0) goto L_0x008f
            boolean r0 = r11.exists()
            if (r0 == 0) goto L_0x0033
            if (r12 == 0) goto L_0x0029
            boolean r0 = r11.delete()
            if (r0 == 0) goto L_0x001f
            goto L_0x0033
        L_0x001f:
            kotlin.io.FileAlreadyExistsException r0 = new kotlin.io.FileAlreadyExistsException
            java.lang.String r1 = "Tried to overwrite the destination, but failed to delete it."
            r0.<init>(r10, r11, r1)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        L_0x0029:
            kotlin.io.FileAlreadyExistsException r0 = new kotlin.io.FileAlreadyExistsException
            java.lang.String r1 = "The destination file already exists."
            r0.<init>(r10, r11, r1)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        L_0x0033:
            boolean r0 = r10.isDirectory()
            if (r0 == 0) goto L_0x004a
            boolean r0 = r11.mkdirs()
            if (r0 == 0) goto L_0x0040
            goto L_0x007f
        L_0x0040:
            kotlin.io.FileSystemException r0 = new kotlin.io.FileSystemException
            java.lang.String r1 = "Failed to create target directory."
            r0.<init>(r10, r11, r1)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        L_0x004a:
            java.io.File r0 = r11.getParentFile()
            if (r0 == 0) goto L_0x0053
            r0.mkdirs()
        L_0x0053:
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r10)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            r2 = r1
            java.lang.Throwable r2 = (java.lang.Throwable) r2
            r3 = r0
            java.io.FileInputStream r3 = (java.io.FileInputStream) r3     // Catch:{ all -> 0x0088 }
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ all -> 0x0088 }
            r5.<init>(r11)     // Catch:{ all -> 0x0088 }
            java.io.Closeable r5 = (java.io.Closeable) r5     // Catch:{ all -> 0x0088 }
            java.lang.Throwable r1 = (java.lang.Throwable) r1     // Catch:{ all -> 0x0088 }
            r6 = r5
            java.io.FileOutputStream r6 = (java.io.FileOutputStream) r6     // Catch:{ all -> 0x0081 }
            r7 = 0
            r8 = r3
            java.io.InputStream r8 = (java.io.InputStream) r8     // Catch:{ all -> 0x0081 }
            r9 = r6
            java.io.OutputStream r9 = (java.io.OutputStream) r9     // Catch:{ all -> 0x0081 }
            kotlin.io.ByteStreamsKt.copyTo(r8, r9, r13)     // Catch:{ all -> 0x0081 }
            kotlin.io.CloseableKt.closeFinally(r5, r1)     // Catch:{ all -> 0x0088 }
            kotlin.io.CloseableKt.closeFinally(r0, r2)
        L_0x007f:
            return r11
        L_0x0081:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0083 }
        L_0x0083:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r5, r1)     // Catch:{ all -> 0x0088 }
            throw r2     // Catch:{ all -> 0x0088 }
        L_0x0088:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x008a }
        L_0x008a:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        L_0x008f:
            kotlin.io.NoSuchFileException r0 = new kotlin.io.NoSuchFileException
            r5 = 0
            r7 = 2
            r8 = 0
            java.lang.String r6 = "The source file doesn't exist."
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__UtilsKt.copyTo(java.io.File, java.io.File, boolean, int):java.io.File");
    }

    public static /* synthetic */ boolean copyRecursively$default(File file, File file2, boolean z, Function2 function2, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        if ((i & 4) != 0) {
            function2 = FilesKt__UtilsKt$copyRecursively$1.INSTANCE;
        }
        return FilesKt.copyRecursively(file, file2, z, function2);
    }

    public static final boolean copyRecursively(File $this$copyRecursively, File target, boolean overwrite, Function2<? super File, ? super IOException, ? extends OnErrorAction> onError) {
        boolean stillExists;
        Intrinsics.checkParameterIsNotNull($this$copyRecursively, "$this$copyRecursively");
        Intrinsics.checkParameterIsNotNull(target, "target");
        Intrinsics.checkParameterIsNotNull(onError, "onError");
        if ($this$copyRecursively.exists()) {
            try {
                Iterator<File> it = FilesKt.walkTopDown($this$copyRecursively).onFail(new FilesKt__UtilsKt$copyRecursively$2(onError)).iterator();
                while (it.hasNext()) {
                    File src = it.next();
                    if (src.exists()) {
                        File dstFile = new File(target, FilesKt.toRelativeString(src, $this$copyRecursively));
                        if (dstFile.exists() && (!src.isDirectory() || !dstFile.isDirectory())) {
                            if (!overwrite) {
                                stillExists = true;
                            } else if (dstFile.isDirectory()) {
                                stillExists = !FilesKt.deleteRecursively(dstFile);
                            } else {
                                stillExists = !dstFile.delete();
                            }
                            if (stillExists) {
                                if (((OnErrorAction) onError.invoke(dstFile, new FileAlreadyExistsException(src, dstFile, "The destination file already exists."))) == OnErrorAction.TERMINATE) {
                                    return false;
                                }
                            }
                        }
                        if (src.isDirectory()) {
                            dstFile.mkdirs();
                        } else if (FilesKt.copyTo$default(src, dstFile, overwrite, 0, 4, (Object) null).length() != src.length() && ((OnErrorAction) onError.invoke(src, new IOException("Source file wasn't copied completely, length of destination file differs."))) == OnErrorAction.TERMINATE) {
                            return false;
                        }
                    } else if (((OnErrorAction) onError.invoke(src, new NoSuchFileException(src, (File) null, "The source file doesn't exist.", 2, (DefaultConstructorMarker) null))) == OnErrorAction.TERMINATE) {
                        return false;
                    }
                }
                return true;
            } catch (TerminateException e) {
                return false;
            }
        } else if (((OnErrorAction) onError.invoke($this$copyRecursively, new NoSuchFileException($this$copyRecursively, (File) null, "The source file doesn't exist.", 2, (DefaultConstructorMarker) null))) != OnErrorAction.TERMINATE) {
            return true;
        } else {
            return false;
        }
    }

    public static final boolean deleteRecursively(File $this$deleteRecursively) {
        Intrinsics.checkParameterIsNotNull($this$deleteRecursively, "$this$deleteRecursively");
        boolean accumulator$iv = true;
        for (File it : FilesKt.walkBottomUp($this$deleteRecursively)) {
            accumulator$iv = (it.delete() || !it.exists()) && accumulator$iv;
        }
        return accumulator$iv;
    }

    public static final boolean startsWith(File $this$startsWith, File other) {
        Intrinsics.checkParameterIsNotNull($this$startsWith, "$this$startsWith");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt.toComponents($this$startsWith);
        FilePathComponents otherComponents = FilesKt.toComponents(other);
        if (!(!Intrinsics.areEqual((Object) components.getRoot(), (Object) otherComponents.getRoot())) && components.getSize() >= otherComponents.getSize()) {
            return components.getSegments().subList(0, otherComponents.getSize()).equals(otherComponents.getSegments());
        }
        return false;
    }

    public static final boolean startsWith(File $this$startsWith, String other) {
        Intrinsics.checkParameterIsNotNull($this$startsWith, "$this$startsWith");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return FilesKt.startsWith($this$startsWith, new File(other));
    }

    public static final boolean endsWith(File $this$endsWith, File other) {
        Intrinsics.checkParameterIsNotNull($this$endsWith, "$this$endsWith");
        Intrinsics.checkParameterIsNotNull(other, "other");
        FilePathComponents components = FilesKt.toComponents($this$endsWith);
        FilePathComponents otherComponents = FilesKt.toComponents(other);
        if (otherComponents.isRooted()) {
            return Intrinsics.areEqual((Object) $this$endsWith, (Object) other);
        }
        int shift = components.getSize() - otherComponents.getSize();
        if (shift < 0) {
            return false;
        }
        return components.getSegments().subList(shift, components.getSize()).equals(otherComponents.getSegments());
    }

    public static final boolean endsWith(File $this$endsWith, String other) {
        Intrinsics.checkParameterIsNotNull($this$endsWith, "$this$endsWith");
        Intrinsics.checkParameterIsNotNull(other, "other");
        return FilesKt.endsWith($this$endsWith, new File(other));
    }

    public static final File normalize(File $this$normalize) {
        Intrinsics.checkParameterIsNotNull($this$normalize, "$this$normalize");
        FilePathComponents $this$with = FilesKt.toComponents($this$normalize);
        File root = $this$with.getRoot();
        String str = File.separator;
        Intrinsics.checkExpressionValueIsNotNull(str, "File.separator");
        return FilesKt.resolve(root, CollectionsKt.joinToString$default(normalize$FilesKt__UtilsKt((List<? extends File>) $this$with.getSegments()), str, (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null));
    }

    private static final FilePathComponents normalize$FilesKt__UtilsKt(FilePathComponents $this$normalize) {
        return new FilePathComponents($this$normalize.getRoot(), normalize$FilesKt__UtilsKt((List<? extends File>) $this$normalize.getSegments()));
    }

    private static final List<File> normalize$FilesKt__UtilsKt(List<? extends File> $this$normalize) {
        List list = new ArrayList($this$normalize.size());
        for (File file : $this$normalize) {
            String name = file.getName();
            if (name != null) {
                int hashCode = name.hashCode();
                if (hashCode != 46) {
                    if (hashCode == 1472 && name.equals("..")) {
                        if (list.isEmpty() || !(!Intrinsics.areEqual((Object) ((File) CollectionsKt.last(list)).getName(), (Object) ".."))) {
                            list.add(file);
                        } else {
                            list.remove(list.size() - 1);
                        }
                    }
                } else if (name.equals(".")) {
                }
            }
            list.add(file);
        }
        return list;
    }

    public static final File resolve(File $this$resolve, File relative) {
        Intrinsics.checkParameterIsNotNull($this$resolve, "$this$resolve");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        if (FilesKt.isRooted(relative)) {
            return relative;
        }
        String baseName = $this$resolve.toString();
        Intrinsics.checkExpressionValueIsNotNull(baseName, "this.toString()");
        if ((baseName.length() == 0) || StringsKt.endsWith$default((CharSequence) baseName, File.separatorChar, false, 2, (Object) null)) {
            return new File(baseName + relative);
        }
        return new File(baseName + File.separatorChar + relative);
    }

    public static final File resolve(File $this$resolve, String relative) {
        Intrinsics.checkParameterIsNotNull($this$resolve, "$this$resolve");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return FilesKt.resolve($this$resolve, new File(relative));
    }

    public static final File resolveSibling(File $this$resolveSibling, File relative) {
        Intrinsics.checkParameterIsNotNull($this$resolveSibling, "$this$resolveSibling");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        FilePathComponents components = FilesKt.toComponents($this$resolveSibling);
        return FilesKt.resolve(FilesKt.resolve(components.getRoot(), components.getSize() == 0 ? new File("..") : components.subPath(0, components.getSize() - 1)), relative);
    }

    public static final File resolveSibling(File $this$resolveSibling, String relative) {
        Intrinsics.checkParameterIsNotNull($this$resolveSibling, "$this$resolveSibling");
        Intrinsics.checkParameterIsNotNull(relative, "relative");
        return FilesKt.resolveSibling($this$resolveSibling, new File(relative));
    }
}
