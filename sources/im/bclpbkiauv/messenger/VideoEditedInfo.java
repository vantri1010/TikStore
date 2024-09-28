package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLRPC;
import java.util.Locale;

public class VideoEditedInfo {
    public int bitrate;
    public TLRPC.InputEncryptedFile encryptedFile;
    public float end;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public TLRPC.InputFile file;
    public int framerate = 24;
    public byte[] iv;
    public byte[] key;
    public boolean muted;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public boolean roundVideo;
    public float start;
    public long startTime;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Integer.valueOf(this.framerate), this.originalPath});
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0085 A[Catch:{ Exception -> 0x00aa }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean parseString(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.String r0 = "_"
            int r1 = r9.length()
            r2 = 0
            r3 = 6
            if (r1 >= r3) goto L_0x000b
            return r2
        L_0x000b:
            java.lang.String[] r1 = r9.split(r0)     // Catch:{ Exception -> 0x00aa }
            int r4 = r1.length     // Catch:{ Exception -> 0x00aa }
            r5 = 10
            r6 = 1
            if (r4 < r5) goto L_0x00a9
            r4 = r1[r6]     // Catch:{ Exception -> 0x00aa }
            long r4 = java.lang.Long.parseLong(r4)     // Catch:{ Exception -> 0x00aa }
            r8.startTime = r4     // Catch:{ Exception -> 0x00aa }
            r4 = 2
            r4 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            long r4 = java.lang.Long.parseLong(r4)     // Catch:{ Exception -> 0x00aa }
            r8.endTime = r4     // Catch:{ Exception -> 0x00aa }
            r4 = 3
            r4 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x00aa }
            r8.rotationValue = r4     // Catch:{ Exception -> 0x00aa }
            r4 = 4
            r4 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x00aa }
            r8.originalWidth = r4     // Catch:{ Exception -> 0x00aa }
            r4 = 5
            r4 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ Exception -> 0x00aa }
            r8.originalHeight = r4     // Catch:{ Exception -> 0x00aa }
            r3 = r1[r3]     // Catch:{ Exception -> 0x00aa }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x00aa }
            r8.bitrate = r3     // Catch:{ Exception -> 0x00aa }
            r3 = 7
            r3 = r1[r3]     // Catch:{ Exception -> 0x00aa }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x00aa }
            r8.resultWidth = r3     // Catch:{ Exception -> 0x00aa }
            r3 = 8
            r3 = r1[r3]     // Catch:{ Exception -> 0x00aa }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x00aa }
            r8.resultHeight = r3     // Catch:{ Exception -> 0x00aa }
            int r3 = r1.length     // Catch:{ Exception -> 0x00aa }
            r4 = 11
            if (r3 < r4) goto L_0x006d
            r3 = 9
            r3 = r1[r3]     // Catch:{ Exception -> 0x006c }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x006c }
            r8.framerate = r3     // Catch:{ Exception -> 0x006c }
            goto L_0x006d
        L_0x006c:
            r3 = move-exception
        L_0x006d:
            int r3 = r8.framerate     // Catch:{ Exception -> 0x00aa }
            if (r3 <= 0) goto L_0x007b
            int r3 = r8.framerate     // Catch:{ Exception -> 0x00aa }
            r4 = 400(0x190, float:5.6E-43)
            if (r3 <= r4) goto L_0x0078
            goto L_0x007b
        L_0x0078:
            r3 = 10
            goto L_0x0081
        L_0x007b:
            r3 = 9
            r4 = 25
            r8.framerate = r4     // Catch:{ Exception -> 0x00aa }
        L_0x0081:
            r4 = r3
        L_0x0082:
            int r5 = r1.length     // Catch:{ Exception -> 0x00aa }
            if (r4 >= r5) goto L_0x00a9
            java.lang.String r5 = r8.originalPath     // Catch:{ Exception -> 0x00aa }
            if (r5 != 0) goto L_0x008e
            r5 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            r8.originalPath = r5     // Catch:{ Exception -> 0x00aa }
            goto L_0x00a6
        L_0x008e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00aa }
            r5.<init>()     // Catch:{ Exception -> 0x00aa }
            java.lang.String r7 = r8.originalPath     // Catch:{ Exception -> 0x00aa }
            r5.append(r7)     // Catch:{ Exception -> 0x00aa }
            r5.append(r0)     // Catch:{ Exception -> 0x00aa }
            r7 = r1[r4]     // Catch:{ Exception -> 0x00aa }
            r5.append(r7)     // Catch:{ Exception -> 0x00aa }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00aa }
            r8.originalPath = r5     // Catch:{ Exception -> 0x00aa }
        L_0x00a6:
            int r4 = r4 + 1
            goto L_0x0082
        L_0x00a9:
            return r6
        L_0x00aa:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.VideoEditedInfo.parseString(java.lang.String):boolean");
    }

    public boolean needConvert() {
        boolean z = this.roundVideo;
        if (z) {
            if (z) {
                if (this.startTime <= 0) {
                    long j = this.endTime;
                    if (j == -1 || j == this.estimatedDuration) {
                        return false;
                    }
                }
            }
            return false;
        }
        return true;
    }
}
