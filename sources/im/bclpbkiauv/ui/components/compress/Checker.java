package im.bclpbkiauv.ui.components.compress;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

enum Checker {
    SINGLE;
    
    private static final String JPG = ".jpg";
    private static final String TAG = "Luban";
    private final byte[] JPEG_SIGNATURE;

    /* access modifiers changed from: package-private */
    public boolean isJPG(InputStream is) {
        return isJPG(toByteArray(is));
    }

    /* access modifiers changed from: package-private */
    public int getOrientation(InputStream is) {
        return getOrientation(toByteArray(is));
    }

    private boolean isJPG(byte[] data) {
        if (data == null || data.length < 3) {
            return false;
        }
        return Arrays.equals(this.JPEG_SIGNATURE, new byte[]{data[0], data[1], data[2]});
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x006e, code lost:
        if (r2 <= 8) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0070, code lost:
        r3 = pack(r12, r1, 4, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0077, code lost:
        if (r3 == 1229531648) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x007c, code lost:
        if (r3 == 1296891946) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x007e, code lost:
        android.util.Log.e(TAG, "Invalid byte order");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0083, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0084, code lost:
        if (r3 != 1229531648) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0086, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0088, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0089, code lost:
        r5 = pack(r12, r1 + 4, 4, r4) + 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0092, code lost:
        if (r5 < 10) goto L_0x00d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0094, code lost:
        if (r5 <= r2) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0097, code lost:
        r1 = r1 + r5;
        r2 = r2 - r5;
        r5 = pack(r12, r1 - 2, 2, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x009f, code lost:
        r10 = r5 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00a1, code lost:
        if (r5 <= 0) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00a5, code lost:
        if (r2 < 12) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ad, code lost:
        if (pack(r12, r1, 2, r4) != 274) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00af, code lost:
        r5 = pack(r12, r1 + 8, 2, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00b5, code lost:
        if (r5 == 1) goto L_0x00ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00b8, code lost:
        if (r5 == 3) goto L_0x00cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00bb, code lost:
        if (r5 == 6) goto L_0x00c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00bd, code lost:
        if (r5 == 8) goto L_0x00c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00bf, code lost:
        android.util.Log.e(TAG, "Unsupported orientation");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00c4, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00c5, code lost:
        return 270;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00c8, code lost:
        return 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00cb, code lost:
        return 180;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00ce, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00cf, code lost:
        r1 = r1 + 12;
        r2 = r2 - 12;
        r5 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00d5, code lost:
        android.util.Log.e(TAG, "Invalid offset");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00da, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00db, code lost:
        android.util.Log.e(TAG, "Orientation not found");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x00e0, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getOrientation(byte[] r12) {
        /*
            r11 = this;
            r0 = 0
            if (r12 != 0) goto L_0x0004
            return r0
        L_0x0004:
            r1 = 0
            r2 = 0
        L_0x0006:
            int r3 = r1 + 3
            int r4 = r12.length
            r5 = 4
            java.lang.String r6 = "Luban"
            r7 = 1
            r8 = 8
            r9 = 2
            if (r3 >= r4) goto L_0x006e
            int r3 = r1 + 1
            byte r1 = r12[r1]
            r4 = 255(0xff, float:3.57E-43)
            r1 = r1 & r4
            if (r1 != r4) goto L_0x006d
            byte r1 = r12[r3]
            r1 = r1 & r4
            if (r1 != r4) goto L_0x0022
            r1 = r3
            goto L_0x0006
        L_0x0022:
            int r3 = r3 + 1
            r4 = 216(0xd8, float:3.03E-43)
            if (r1 == r4) goto L_0x006b
            if (r1 != r7) goto L_0x002b
            goto L_0x006b
        L_0x002b:
            r4 = 217(0xd9, float:3.04E-43)
            if (r1 == r4) goto L_0x0069
            r4 = 218(0xda, float:3.05E-43)
            if (r1 != r4) goto L_0x0034
            goto L_0x0069
        L_0x0034:
            int r2 = r11.pack(r12, r3, r9, r0)
            if (r2 < r9) goto L_0x0063
            int r4 = r3 + r2
            int r10 = r12.length
            if (r4 <= r10) goto L_0x0040
            goto L_0x0063
        L_0x0040:
            r4 = 225(0xe1, float:3.15E-43)
            if (r1 != r4) goto L_0x005f
            if (r2 < r8) goto L_0x005f
            int r4 = r3 + 2
            int r4 = r11.pack(r12, r4, r5, r0)
            r10 = 1165519206(0x45786966, float:3974.5874)
            if (r4 != r10) goto L_0x005f
            int r4 = r3 + 6
            int r4 = r11.pack(r12, r4, r9, r0)
            if (r4 != 0) goto L_0x005f
            int r3 = r3 + 8
            int r2 = r2 + -8
            r1 = r3
            goto L_0x006e
        L_0x005f:
            int r3 = r3 + r2
            r2 = 0
            r1 = r3
            goto L_0x0006
        L_0x0063:
            java.lang.String r4 = "Invalid length"
            android.util.Log.e(r6, r4)
            return r0
        L_0x0069:
            r1 = r3
            goto L_0x006e
        L_0x006b:
            r1 = r3
            goto L_0x0006
        L_0x006d:
            r1 = r3
        L_0x006e:
            if (r2 <= r8) goto L_0x00db
            int r3 = r11.pack(r12, r1, r5, r0)
            r4 = 1229531648(0x49492a00, float:823968.0)
            if (r3 == r4) goto L_0x0084
            r10 = 1296891946(0x4d4d002a, float:2.14958752E8)
            if (r3 == r10) goto L_0x0084
            java.lang.String r4 = "Invalid byte order"
            android.util.Log.e(r6, r4)
            return r0
        L_0x0084:
            if (r3 != r4) goto L_0x0088
            r4 = 1
            goto L_0x0089
        L_0x0088:
            r4 = 0
        L_0x0089:
            int r10 = r1 + 4
            int r5 = r11.pack(r12, r10, r5, r4)
            int r5 = r5 + r9
            r10 = 10
            if (r5 < r10) goto L_0x00d5
            if (r5 <= r2) goto L_0x0097
            goto L_0x00d5
        L_0x0097:
            int r1 = r1 + r5
            int r2 = r2 - r5
            int r10 = r1 + -2
            int r5 = r11.pack(r12, r10, r9, r4)
        L_0x009f:
            int r10 = r5 + -1
            if (r5 <= 0) goto L_0x00db
            r5 = 12
            if (r2 < r5) goto L_0x00db
            int r3 = r11.pack(r12, r1, r9, r4)
            r5 = 274(0x112, float:3.84E-43)
            if (r3 != r5) goto L_0x00cf
            int r5 = r1 + 8
            int r5 = r11.pack(r12, r5, r9, r4)
            if (r5 == r7) goto L_0x00ce
            r7 = 3
            if (r5 == r7) goto L_0x00cb
            r7 = 6
            if (r5 == r7) goto L_0x00c8
            if (r5 == r8) goto L_0x00c5
            java.lang.String r7 = "Unsupported orientation"
            android.util.Log.e(r6, r7)
            return r0
        L_0x00c5:
            r0 = 270(0x10e, float:3.78E-43)
            return r0
        L_0x00c8:
            r0 = 90
            return r0
        L_0x00cb:
            r0 = 180(0xb4, float:2.52E-43)
            return r0
        L_0x00ce:
            return r0
        L_0x00cf:
            int r1 = r1 + 12
            int r2 = r2 + -12
            r5 = r10
            goto L_0x009f
        L_0x00d5:
            java.lang.String r7 = "Invalid offset"
            android.util.Log.e(r6, r7)
            return r0
        L_0x00db:
            java.lang.String r3 = "Orientation not found"
            android.util.Log.e(r6, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.compress.Checker.getOrientation(byte[]):int");
    }

    /* access modifiers changed from: package-private */
    public String extSuffix(InputStreamProvider input) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input.open(), (Rect) null, options);
            return options.outMimeType.replace("image/", ".");
        } catch (Exception e) {
            return JPG;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean needCompress(int leastCompressSize, String path) {
        if (leastCompressSize <= 0) {
            return true;
        }
        File source = new File(path);
        if (!source.exists() || source.length() <= ((long) (leastCompressSize << 10))) {
            return false;
        }
        return true;
    }

    private int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        while (true) {
            int length2 = length - 1;
            if (length <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & 255);
            offset += step;
            length = length2;
        }
    }

    private byte[] toByteArray(InputStream is) {
        if (is == null) {
            return new byte[0];
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        while (true) {
            try {
                int read = is.read(data, 0, data.length);
                int read2 = read;
                if (read != -1) {
                    buffer.write(data, 0, read2);
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                    }
                }
            } catch (Exception e2) {
                byte[] bArr = new byte[0];
                try {
                    buffer.close();
                } catch (IOException e3) {
                }
                return bArr;
            } catch (Throwable th) {
                try {
                    buffer.close();
                } catch (IOException e4) {
                }
                throw th;
            }
        }
        buffer.close();
        return buffer.toByteArray();
    }
}
