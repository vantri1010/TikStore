package kotlin.io;

import com.baidu.mapapi.UIMsg;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000z\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010\u0005\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a!\u0010\n\u001a\u00020\u000b*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001a!\u0010\u000e\u001a\u00020\u000f*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\rH\b\u001aB\u0010\u0010\u001a\u00020\u0001*\u00020\u000226\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001aJ\u0010\u0010\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0017\u001a\u00020\r26\u0010\u0011\u001a2\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0015\u0012\u0013\u0012\u00110\r¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u0016\u0012\u0004\u0012\u00020\u00010\u0012\u001a7\u0010\u0018\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2!\u0010\u0011\u001a\u001d\u0012\u0013\u0012\u00110\u0007¢\u0006\f\b\u0013\u0012\b\b\u0014\u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\u00010\u0019\u001a\r\u0010\u001b\u001a\u00020\u001c*\u00020\u0002H\b\u001a\r\u0010\u001d\u001a\u00020\u001e*\u00020\u0002H\b\u001a\u0017\u0010\u001f\u001a\u00020 *\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a\n\u0010!\u001a\u00020\u0004*\u00020\u0002\u001a\u001a\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00070#*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0014\u0010$\u001a\u00020\u0007*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010%\u001a\u00020&*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u001a?\u0010'\u001a\u0002H(\"\u0004\b\u0000\u0010(*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\t2\u0018\u0010)\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070*\u0012\u0004\u0012\u0002H(0\u0019H\bø\u0001\u0000¢\u0006\u0002\u0010,\u001a\u0012\u0010-\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004\u001a\u001c\u0010.\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t\u001a\u0017\u0010/\u001a\u000200*\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tH\b\u0002\b\n\u0006\b\u0011(+0\u0001¨\u00061"}, d2 = {"appendBytes", "", "Ljava/io/File;", "array", "", "appendText", "text", "", "charset", "Ljava/nio/charset/Charset;", "bufferedReader", "Ljava/io/BufferedReader;", "bufferSize", "", "bufferedWriter", "Ljava/io/BufferedWriter;", "forEachBlock", "action", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "buffer", "bytesRead", "blockSize", "forEachLine", "Lkotlin/Function1;", "line", "inputStream", "Ljava/io/FileInputStream;", "outputStream", "Ljava/io/FileOutputStream;", "printWriter", "Ljava/io/PrintWriter;", "readBytes", "readLines", "", "readText", "reader", "Ljava/io/InputStreamReader;", "useLines", "T", "block", "Lkotlin/sequences/Sequence;", "Requires newer compiler version to be inlined correctly.", "(Ljava/io/File;Ljava/nio/charset/Charset;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "writeBytes", "writeText", "writer", "Ljava/io/OutputStreamWriter;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 15}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: FileReadWrite.kt */
class FilesKt__FileReadWriteKt extends FilesKt__FilePathComponentsKt {
    static /* synthetic */ InputStreamReader reader$default(File $this$reader, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return new InputStreamReader(new FileInputStream($this$reader), charset);
    }

    private static final InputStreamReader reader(File $this$reader, Charset charset) {
        return new InputStreamReader(new FileInputStream($this$reader), charset);
    }

    static /* synthetic */ BufferedReader bufferedReader$default(File $this$bufferedReader, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != 0) {
            bufferSize = 8192;
        }
        Reader inputStreamReader = new InputStreamReader(new FileInputStream($this$bufferedReader), charset);
        return inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    private static final BufferedReader bufferedReader(File $this$bufferedReader, Charset charset, int bufferSize) {
        Reader inputStreamReader = new InputStreamReader(new FileInputStream($this$bufferedReader), charset);
        return inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, bufferSize);
    }

    static /* synthetic */ OutputStreamWriter writer$default(File $this$writer, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return new OutputStreamWriter(new FileOutputStream($this$writer), charset);
    }

    private static final OutputStreamWriter writer(File $this$writer, Charset charset) {
        return new OutputStreamWriter(new FileOutputStream($this$writer), charset);
    }

    static /* synthetic */ BufferedWriter bufferedWriter$default(File $this$bufferedWriter, Charset charset, int bufferSize, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        if ((i & 2) != 0) {
            bufferSize = 8192;
        }
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($this$bufferedWriter), charset);
        return outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    private static final BufferedWriter bufferedWriter(File $this$bufferedWriter, Charset charset, int bufferSize) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($this$bufferedWriter), charset);
        return outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, bufferSize);
    }

    static /* synthetic */ PrintWriter printWriter$default(File $this$printWriter, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($this$printWriter), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    private static final PrintWriter printWriter(File $this$printWriter, Charset charset) {
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream($this$printWriter), charset);
        return new PrintWriter(outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192));
    }

    /* JADX INFO: finally extract failed */
    public static final byte[] readBytes(File $this$readBytes) {
        Throwable th;
        File file = $this$readBytes;
        Intrinsics.checkParameterIsNotNull(file, "$this$readBytes");
        Closeable fileInputStream = new FileInputStream(file);
        Throwable th2 = null;
        try {
            FileInputStream input = (FileInputStream) fileInputStream;
            int offset = 0;
            long length = $this$readBytes.length();
            long length2 = length;
            if (length2 <= ((long) Integer.MAX_VALUE)) {
                int remaining = (int) length;
                byte[] result = new byte[remaining];
                while (remaining > 0) {
                    int read = input.read(result, offset, remaining);
                    if (read < 0) {
                        break;
                    }
                    remaining -= read;
                    offset += read;
                }
                if (remaining > 0) {
                    byte[] copyOf = Arrays.copyOf(result, offset);
                    Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
                    result = copyOf;
                } else {
                    int extraByte = input.read();
                    if (extraByte != -1) {
                        ExposingBufferByteArrayOutputStream extra = new ExposingBufferByteArrayOutputStream(UIMsg.k_event.V_WM_ROTATE);
                        extra.write(extraByte);
                        FileInputStream fileInputStream2 = input;
                        ByteStreamsKt.copyTo$default(input, extra, 0, 2, (Object) null);
                        int resultingSize = result.length + extra.size();
                        if (resultingSize >= 0) {
                            byte[] buffer = extra.getBuffer();
                            byte[] copyOf2 = Arrays.copyOf(result, resultingSize);
                            Intrinsics.checkExpressionValueIsNotNull(copyOf2, "java.util.Arrays.copyOf(this, newSize)");
                            result = ArraysKt.copyInto(buffer, copyOf2, result.length, 0, extra.size());
                        } else {
                            throw new OutOfMemoryError("File " + file + " is too big to fit in memory.");
                        }
                    }
                }
                CloseableKt.closeFinally(fileInputStream, th2);
                return result;
            }
            throw new OutOfMemoryError("File " + file + " is too big (" + length2 + " bytes) to fit in memory.");
        } catch (Throwable th3) {
            Throwable th4 = th3;
            CloseableKt.closeFinally(fileInputStream, th);
            throw th4;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0024, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void writeBytes(java.io.File r4, byte[] r5) {
        /*
            java.lang.String r0 = "$this$writeBytes"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            java.lang.String r0 = "array"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r0.<init>(r4)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r2 = r0
            java.io.FileOutputStream r2 = (java.io.FileOutputStream) r2     // Catch:{ all -> 0x0021 }
            r3 = 0
            r2.write(r5)     // Catch:{ all -> 0x0021 }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0021 }
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return
        L_0x0021:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0023 }
        L_0x0023:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.writeBytes(java.io.File, byte[]):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0024, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0025, code lost:
        kotlin.io.CloseableKt.closeFinally(r0, r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void appendBytes(java.io.File r4, byte[] r5) {
        /*
            java.lang.String r0 = "$this$appendBytes"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            java.lang.String r0 = "array"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            r1 = 1
            r0.<init>(r4, r1)
            java.io.Closeable r0 = (java.io.Closeable) r0
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r2 = r0
            java.io.FileOutputStream r2 = (java.io.FileOutputStream) r2     // Catch:{ all -> 0x0022 }
            r3 = 0
            r2.write(r5)     // Catch:{ all -> 0x0022 }
            kotlin.Unit r2 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0022 }
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            return
        L_0x0022:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0024 }
        L_0x0024:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r0, r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.appendBytes(java.io.File, byte[]):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0030, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002d, code lost:
        kotlin.io.CloseableKt.closeFinally(r1, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String readText(java.io.File r5, java.nio.charset.Charset r6) {
        /*
            java.lang.String r0 = "$this$readText"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r5, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r0)
            java.io.FileInputStream r0 = new java.io.FileInputStream
            r0.<init>(r5)
            java.io.InputStream r0 = (java.io.InputStream) r0
            java.io.InputStreamReader r1 = new java.io.InputStreamReader
            r1.<init>(r0, r6)
            java.io.Closeable r1 = (java.io.Closeable) r1
            r0 = 0
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            r2 = r1
            java.io.InputStreamReader r2 = (java.io.InputStreamReader) r2     // Catch:{ all -> 0x002a }
            r3 = 0
            r4 = r2
            java.io.Reader r4 = (java.io.Reader) r4     // Catch:{ all -> 0x002a }
            java.lang.String r4 = kotlin.io.TextStreamsKt.readText(r4)     // Catch:{ all -> 0x002a }
            kotlin.io.CloseableKt.closeFinally(r1, r0)
            return r4
        L_0x002a:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x002c }
        L_0x002c:
            r2 = move-exception
            kotlin.io.CloseableKt.closeFinally(r1, r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.readText(java.io.File, java.nio.charset.Charset):java.lang.String");
    }

    public static /* synthetic */ String readText$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return FilesKt.readText(file, charset);
    }

    public static final void writeText(File $this$writeText, String text, Charset charset) {
        Intrinsics.checkParameterIsNotNull($this$writeText, "$this$writeText");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        byte[] bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        FilesKt.writeBytes($this$writeText, bytes);
    }

    public static /* synthetic */ void writeText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.writeText(file, str, charset);
    }

    public static final void appendText(File $this$appendText, String text, Charset charset) {
        Intrinsics.checkParameterIsNotNull($this$appendText, "$this$appendText");
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        byte[] bytes = text.getBytes(charset);
        Intrinsics.checkExpressionValueIsNotNull(bytes, "(this as java.lang.String).getBytes(charset)");
        FilesKt.appendBytes($this$appendText, bytes);
    }

    public static /* synthetic */ void appendText$default(File file, String str, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.appendText(file, str, charset);
    }

    public static final void forEachBlock(File $this$forEachBlock, Function2<? super byte[], ? super Integer, Unit> action) {
        Intrinsics.checkParameterIsNotNull($this$forEachBlock, "$this$forEachBlock");
        Intrinsics.checkParameterIsNotNull(action, "action");
        FilesKt.forEachBlock($this$forEachBlock, 4096, action);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003a, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
        kotlin.io.CloseableKt.closeFinally(r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final void forEachBlock(java.io.File r7, int r8, kotlin.jvm.functions.Function2<? super byte[], ? super java.lang.Integer, kotlin.Unit> r9) {
        /*
            java.lang.String r0 = "$this$forEachBlock"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r0)
            java.lang.String r0 = "action"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
            r0 = 512(0x200, float:7.175E-43)
            int r0 = kotlin.ranges.RangesKt.coerceAtLeast((int) r8, (int) r0)
            byte[] r0 = new byte[r0]
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r7)
            java.io.Closeable r1 = (java.io.Closeable) r1
            r2 = 0
            java.lang.Throwable r2 = (java.lang.Throwable) r2
            r3 = r1
            java.io.FileInputStream r3 = (java.io.FileInputStream) r3     // Catch:{ all -> 0x0038 }
            r4 = 0
        L_0x0020:
            int r5 = r3.read(r0)     // Catch:{ all -> 0x0038 }
            if (r5 > 0) goto L_0x002f
            kotlin.Unit r3 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0038 }
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            return
        L_0x002f:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0038 }
            r9.invoke(r0, r6)     // Catch:{ all -> 0x0038 }
            goto L_0x0020
        L_0x0038:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x003a }
        L_0x003a:
            r3 = move-exception
            kotlin.io.CloseableKt.closeFinally(r1, r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.forEachBlock(java.io.File, int, kotlin.jvm.functions.Function2):void");
    }

    public static /* synthetic */ void forEachLine$default(File file, Charset charset, Function1 function1, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        FilesKt.forEachLine(file, charset, function1);
    }

    public static final void forEachLine(File $this$forEachLine, Charset charset, Function1<? super String, Unit> action) {
        Intrinsics.checkParameterIsNotNull($this$forEachLine, "$this$forEachLine");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        Intrinsics.checkParameterIsNotNull(action, "action");
        TextStreamsKt.forEachLine(new BufferedReader(new InputStreamReader(new FileInputStream($this$forEachLine), charset)), action);
    }

    private static final FileInputStream inputStream(File $this$inputStream) {
        return new FileInputStream($this$inputStream);
    }

    private static final FileOutputStream outputStream(File $this$outputStream) {
        return new FileOutputStream($this$outputStream);
    }

    public static /* synthetic */ List readLines$default(File file, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return FilesKt.readLines(file, charset);
    }

    public static final List<String> readLines(File $this$readLines, Charset charset) {
        Intrinsics.checkParameterIsNotNull($this$readLines, "$this$readLines");
        Intrinsics.checkParameterIsNotNull(charset, "charset");
        ArrayList result = new ArrayList();
        FilesKt.forEachLine($this$readLines, charset, new FilesKt__FileReadWriteKt$readLines$1(result));
        return result;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005b, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005c, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0063, code lost:
        if (kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0) == false) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x006b, code lost:
        kotlin.io.CloseableKt.closeFinally(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006e, code lost:
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0071, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ java.lang.Object useLines$default(java.io.File r6, java.nio.charset.Charset r7, kotlin.jvm.functions.Function1 r8, int r9, java.lang.Object r10) {
        /*
            r10 = 1
            r9 = r9 & r10
            if (r9 == 0) goto L_0x0006
            java.nio.charset.Charset r7 = kotlin.text.Charsets.UTF_8
        L_0x0006:
            r9 = 0
            java.lang.String r0 = "$this$useLines"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r0)
            java.lang.String r0 = "charset"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r7, r0)
            java.lang.String r0 = "block"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r0)
            r0 = 8192(0x2000, float:1.14794E-41)
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r6)
            java.io.InputStream r1 = (java.io.InputStream) r1
            java.io.InputStreamReader r2 = new java.io.InputStreamReader
            r2.<init>(r1, r7)
            java.io.Reader r2 = (java.io.Reader) r2
            boolean r1 = r2 instanceof java.io.BufferedReader
            if (r1 == 0) goto L_0x002d
            java.io.BufferedReader r2 = (java.io.BufferedReader) r2
            goto L_0x0033
        L_0x002d:
            java.io.BufferedReader r1 = new java.io.BufferedReader
            r1.<init>(r2, r0)
            r2 = r1
        L_0x0033:
            java.io.Closeable r2 = (java.io.Closeable) r2
            r0 = 0
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            r1 = 0
            r3 = r2
            java.io.BufferedReader r3 = (java.io.BufferedReader) r3     // Catch:{ all -> 0x0059 }
            r4 = 0
            kotlin.sequences.Sequence r5 = kotlin.io.TextStreamsKt.lineSequence(r3)     // Catch:{ all -> 0x0059 }
            java.lang.Object r5 = r8.invoke(r5)     // Catch:{ all -> 0x0059 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r10)
            boolean r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r10, r10, r1)
            if (r1 == 0) goto L_0x0052
            kotlin.io.CloseableKt.closeFinally(r2, r0)
            goto L_0x0055
        L_0x0052:
            r2.close()
        L_0x0055:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r10)
            return r5
        L_0x0059:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x005b }
        L_0x005b:
            r3 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r10)
            boolean r1 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r10, r10, r1)
            if (r1 != 0) goto L_0x006b
            r2.close()     // Catch:{ all -> 0x0069 }
            goto L_0x006e
        L_0x0069:
            r0 = move-exception
            goto L_0x006e
        L_0x006b:
            kotlin.io.CloseableKt.closeFinally(r2, r0)
        L_0x006e:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r10)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines$default(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1, int, java.lang.Object):java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0056, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0057, code lost:
        kotlin.jvm.internal.InlineMarker.finallyStart(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005e, code lost:
        if (kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0) == false) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0066, code lost:
        kotlin.io.CloseableKt.closeFinally(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0069, code lost:
        kotlin.jvm.internal.InlineMarker.finallyEnd(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x006c, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final <T> T useLines(java.io.File r8, java.nio.charset.Charset r9, kotlin.jvm.functions.Function1<? super kotlin.sequences.Sequence<java.lang.String>, ? extends T> r10) {
        /*
            r0 = 0
            java.lang.String r1 = "$this$useLines"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r1)
            java.lang.String r1 = "charset"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r1)
            java.lang.String r1 = "block"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r10, r1)
            java.io.FileInputStream r1 = new java.io.FileInputStream
            r1.<init>(r8)
            java.io.InputStream r1 = (java.io.InputStream) r1
            java.io.InputStreamReader r2 = new java.io.InputStreamReader
            r2.<init>(r1, r9)
            java.io.Reader r2 = (java.io.Reader) r2
            boolean r1 = r2 instanceof java.io.BufferedReader
            if (r1 == 0) goto L_0x0025
            java.io.BufferedReader r2 = (java.io.BufferedReader) r2
            goto L_0x002d
        L_0x0025:
            java.io.BufferedReader r1 = new java.io.BufferedReader
            r3 = 8192(0x2000, float:1.14794E-41)
            r1.<init>(r2, r3)
            r2 = r1
        L_0x002d:
            java.io.Closeable r2 = (java.io.Closeable) r2
            r1 = 0
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            r3 = 0
            r4 = 1
            r5 = r2
            java.io.BufferedReader r5 = (java.io.BufferedReader) r5     // Catch:{ all -> 0x0054 }
            r6 = 0
            kotlin.sequences.Sequence r7 = kotlin.io.TextStreamsKt.lineSequence(r5)     // Catch:{ all -> 0x0054 }
            java.lang.Object r7 = r10.invoke(r7)     // Catch:{ all -> 0x0054 }
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            boolean r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3)
            if (r3 == 0) goto L_0x004d
            kotlin.io.CloseableKt.closeFinally(r2, r1)
            goto L_0x0050
        L_0x004d:
            r2.close()
        L_0x0050:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            return r7
        L_0x0054:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0056 }
        L_0x0056:
            r5 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            boolean r3 = kotlin.internal.PlatformImplementationsKt.apiVersionIsAtLeast(r4, r4, r3)
            if (r3 != 0) goto L_0x0066
            r2.close()     // Catch:{ all -> 0x0064 }
            goto L_0x0069
        L_0x0064:
            r1 = move-exception
            goto L_0x0069
        L_0x0066:
            kotlin.io.CloseableKt.closeFinally(r2, r1)
        L_0x0069:
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FileReadWriteKt.useLines(java.io.File, java.nio.charset.Charset, kotlin.jvm.functions.Function1):java.lang.Object");
    }
}
