package im.bclpbkiauv.ui.components.paint;

import android.graphics.RectF;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.DispatchQueue;
import im.bclpbkiauv.messenger.FileLog;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

public class Slice {
    private RectF bounds;
    private File file;

    public Slice(ByteBuffer data, RectF rect, DispatchQueue queue) {
        this.bounds = rect;
        try {
            this.file = File.createTempFile("paint", ".bin", ApplicationLoader.applicationContext.getCacheDir());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.file != null) {
            storeData(data);
        }
    }

    public void cleanResources() {
        File file2 = this.file;
        if (file2 != null) {
            file2.delete();
            this.file = null;
        }
    }

    private void storeData(ByteBuffer data) {
        try {
            byte[] input = data.array();
            FileOutputStream fos = new FileOutputStream(this.file);
            Deflater deflater = new Deflater(1, true);
            deflater.setInput(input, data.arrayOffset(), data.remaining());
            deflater.finish();
            byte[] buf = new byte[1024];
            while (!deflater.finished()) {
                fos.write(buf, 0, deflater.deflate(buf));
            }
            deflater.end();
            fos.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x004c A[Catch:{ Exception -> 0x0054 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0035 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x002f A[EDGE_INSN: B:19:0x002f->B:9:0x002f ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x0020 A[Catch:{ Exception -> 0x0054 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x002b A[Catch:{ Exception -> 0x0054 }, LOOP:1: B:6:0x0023->B:8:0x002b, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.nio.ByteBuffer getData() {
        /*
            r9 = this;
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r1 = new byte[r0]     // Catch:{ Exception -> 0x0054 }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x0054 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0054 }
            java.io.File r3 = r9.file     // Catch:{ Exception -> 0x0054 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0054 }
            java.io.ByteArrayOutputStream r3 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0054 }
            r3.<init>()     // Catch:{ Exception -> 0x0054 }
            java.util.zip.Inflater r4 = new java.util.zip.Inflater     // Catch:{ Exception -> 0x0054 }
            r5 = 1
            r4.<init>(r5)     // Catch:{ Exception -> 0x0054 }
        L_0x0018:
            int r5 = r2.read(r1)     // Catch:{ Exception -> 0x0054 }
            r6 = -1
            r7 = 0
            if (r5 == r6) goto L_0x0023
            r4.setInput(r1, r7, r5)     // Catch:{ Exception -> 0x0054 }
        L_0x0023:
            int r6 = r0.length     // Catch:{ Exception -> 0x0054 }
            int r6 = r4.inflate(r0, r7, r6)     // Catch:{ Exception -> 0x0054 }
            r8 = r6
            if (r6 == 0) goto L_0x002f
            r3.write(r0, r7, r8)     // Catch:{ Exception -> 0x0054 }
            goto L_0x0023
        L_0x002f:
            boolean r6 = r4.finished()     // Catch:{ Exception -> 0x0054 }
            if (r6 == 0) goto L_0x004c
            r4.end()     // Catch:{ Exception -> 0x0054 }
            byte[] r5 = r3.toByteArray()     // Catch:{ Exception -> 0x0054 }
            int r6 = r3.size()     // Catch:{ Exception -> 0x0054 }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.wrap(r5, r7, r6)     // Catch:{ Exception -> 0x0054 }
            r3.close()     // Catch:{ Exception -> 0x0054 }
            r2.close()     // Catch:{ Exception -> 0x0054 }
            return r5
        L_0x004c:
            boolean r6 = r4.needsInput()     // Catch:{ Exception -> 0x0054 }
            if (r6 == 0) goto L_0x0053
            goto L_0x0018
        L_0x0053:
            goto L_0x0018
        L_0x0054:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.paint.Slice.getData():java.nio.ByteBuffer");
    }

    public int getX() {
        return (int) this.bounds.left;
    }

    public int getY() {
        return (int) this.bounds.top;
    }

    public int getWidth() {
        return (int) this.bounds.width();
    }

    public int getHeight() {
        return (int) this.bounds.height();
    }

    public RectF getBounds() {
        return new RectF(this.bounds);
    }
}
