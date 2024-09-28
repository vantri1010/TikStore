package com.zhy.http.okhttp.callback;

import java.io.File;
import okhttp3.Response;

public abstract class FileCallBack extends Callback<File> {
    private String destFileDir;
    private String destFileName;

    public FileCallBack(String destFileDir2, String destFileName2) {
        this.destFileDir = destFileDir2;
        this.destFileName = destFileName2;
    }

    public File parseNetworkResponse(Response response, int id) throws Exception {
        return saveFile(response, id);
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x009b A[Catch:{ IOException -> 0x009f }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a2 A[SYNTHETIC, Splitter:B:41:0x00a2] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.io.File saveFile(okhttp3.Response r18, int r19) throws java.io.IOException {
        /*
            r17 = this;
            r8 = r17
            r1 = 0
            r0 = 2048(0x800, float:2.87E-42)
            byte[] r9 = new byte[r0]
            r2 = 0
            r3 = 0
            okhttp3.ResponseBody r0 = r18.body()     // Catch:{ all -> 0x0090 }
            java.io.InputStream r0 = r0.byteStream()     // Catch:{ all -> 0x0090 }
            r10 = r0
            okhttp3.ResponseBody r0 = r18.body()     // Catch:{ all -> 0x008c }
            long r5 = r0.contentLength()     // Catch:{ all -> 0x008c }
            r0 = 0
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x008c }
            java.lang.String r7 = r8.destFileDir     // Catch:{ all -> 0x008c }
            r4.<init>(r7)     // Catch:{ all -> 0x008c }
            r11 = r4
            boolean r4 = r11.exists()     // Catch:{ all -> 0x008c }
            if (r4 != 0) goto L_0x002d
            r11.mkdirs()     // Catch:{ all -> 0x008c }
        L_0x002d:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x008c }
            java.lang.String r7 = r8.destFileName     // Catch:{ all -> 0x008c }
            r4.<init>(r11, r7)     // Catch:{ all -> 0x008c }
            r12 = r4
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ all -> 0x008c }
            r4.<init>(r12)     // Catch:{ all -> 0x008c }
            r13 = r4
            r3 = r2
            r1 = r0
        L_0x003d:
            int r0 = r10.read(r9)     // Catch:{ all -> 0x0086 }
            r14 = r0
            r3 = -1
            if (r0 == r3) goto L_0x0068
            long r3 = (long) r14
            long r15 = r1 + r3
            r0 = 0
            r13.write(r9, r0, r14)     // Catch:{ all -> 0x0080 }
            r3 = r15
            com.zhy.http.okhttp.OkHttpUtils r0 = com.zhy.http.okhttp.OkHttpUtils.getInstance()     // Catch:{ all -> 0x0080 }
            java.util.concurrent.Executor r0 = r0.getDelivery()     // Catch:{ all -> 0x0080 }
            com.zhy.http.okhttp.callback.FileCallBack$1 r7 = new com.zhy.http.okhttp.callback.FileCallBack$1     // Catch:{ all -> 0x0080 }
            r1 = r7
            r2 = r17
            r8 = r7
            r7 = r19
            r1.<init>(r3, r5, r7)     // Catch:{ all -> 0x0080 }
            r0.execute(r8)     // Catch:{ all -> 0x0080 }
            r8 = r17
            r3 = r14
            r1 = r15
            goto L_0x003d
        L_0x0068:
            r13.flush()     // Catch:{ all -> 0x0080 }
            okhttp3.ResponseBody r0 = r18.body()     // Catch:{ IOException -> 0x0079 }
            r0.close()     // Catch:{ IOException -> 0x0079 }
            if (r10 == 0) goto L_0x0078
            r10.close()     // Catch:{ IOException -> 0x0079 }
        L_0x0078:
            goto L_0x007a
        L_0x0079:
            r0 = move-exception
        L_0x007a:
            r13.close()     // Catch:{ IOException -> 0x007e }
            goto L_0x007f
        L_0x007e:
            r0 = move-exception
        L_0x007f:
            return r12
        L_0x0080:
            r0 = move-exception
            r4 = r0
            r1 = r10
            r3 = r13
            r2 = r14
            goto L_0x0092
        L_0x0086:
            r0 = move-exception
            r4 = r0
            r2 = r3
            r1 = r10
            r3 = r13
            goto L_0x0092
        L_0x008c:
            r0 = move-exception
            r4 = r0
            r1 = r10
            goto L_0x0092
        L_0x0090:
            r0 = move-exception
            r4 = r0
        L_0x0092:
            okhttp3.ResponseBody r0 = r18.body()     // Catch:{ IOException -> 0x009f }
            r0.close()     // Catch:{ IOException -> 0x009f }
            if (r1 == 0) goto L_0x009e
            r1.close()     // Catch:{ IOException -> 0x009f }
        L_0x009e:
            goto L_0x00a0
        L_0x009f:
            r0 = move-exception
        L_0x00a0:
            if (r3 == 0) goto L_0x00a8
            r3.close()     // Catch:{ IOException -> 0x00a6 }
            goto L_0x00a8
        L_0x00a6:
            r0 = move-exception
            goto L_0x00a9
        L_0x00a8:
        L_0x00a9:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.zhy.http.okhttp.callback.FileCallBack.saveFile(okhttp3.Response, int):java.io.File");
    }
}
