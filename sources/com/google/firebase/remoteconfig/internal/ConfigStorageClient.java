package com.google.firebase.remoteconfig.internal;

import android.content.Context;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class ConfigStorageClient {
    private static final String JSON_STRING_ENCODING = "UTF-8";
    private static final Map<String, ConfigStorageClient> clientInstances = new HashMap();
    private final Context context;
    private final String fileName;

    private ConfigStorageClient(Context context2, String fileName2) {
        this.context = context2;
        this.fileName = fileName2;
    }

    public synchronized Void write(ConfigContainer container) throws IOException {
        FileOutputStream outputStream = this.context.openFileOutput(this.fileName, 0);
        try {
            outputStream.write(container.toString().getBytes("UTF-8"));
        } finally {
            outputStream.close();
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002c, code lost:
        return r4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003a A[Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }] */
    @javax.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.google.firebase.remoteconfig.internal.ConfigContainer read() throws java.io.IOException {
        /*
            r5 = this;
            monitor-enter(r5)
            r0 = 0
            android.content.Context r1 = r5.context     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            java.lang.String r2 = r5.fileName     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            java.io.FileInputStream r1 = r1.openFileInput(r2)     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            r0 = r1
            int r1 = r0.available()     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            byte[] r1 = new byte[r1]     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            r2 = 0
            int r3 = r1.length     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            r0.read(r1, r2, r3)     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            java.lang.String r2 = new java.lang.String     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            java.lang.String r3 = "UTF-8"
            r2.<init>(r1, r3)     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            r3.<init>(r2)     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            com.google.firebase.remoteconfig.internal.ConfigContainer r4 = com.google.firebase.remoteconfig.internal.ConfigContainer.copyOf(r3)     // Catch:{ JSONException -> 0x0036, FileNotFoundException -> 0x0034, all -> 0x002d }
            if (r0 == 0) goto L_0x002b
            r0.close()     // Catch:{ all -> 0x003e }
        L_0x002b:
            monitor-exit(r5)
            return r4
        L_0x002d:
            r1 = move-exception
            if (r0 == 0) goto L_0x0033
            r0.close()     // Catch:{ all -> 0x003e }
        L_0x0033:
            throw r1     // Catch:{ all -> 0x003e }
        L_0x0034:
            r1 = move-exception
            goto L_0x0037
        L_0x0036:
            r1 = move-exception
        L_0x0037:
            r2 = 0
            if (r0 == 0) goto L_0x0041
            r0.close()     // Catch:{ all -> 0x003e }
            goto L_0x0041
        L_0x003e:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        L_0x0041:
            monitor-exit(r5)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.remoteconfig.internal.ConfigStorageClient.read():com.google.firebase.remoteconfig.internal.ConfigContainer");
    }

    public synchronized Void clear() {
        this.context.deleteFile(this.fileName);
        return null;
    }

    public static synchronized ConfigStorageClient getInstance(Context context2, String fileName2) {
        ConfigStorageClient configStorageClient;
        synchronized (ConfigStorageClient.class) {
            if (!clientInstances.containsKey(fileName2)) {
                clientInstances.put(fileName2, new ConfigStorageClient(context2, fileName2));
            }
            configStorageClient = clientInstances.get(fileName2);
        }
        return configStorageClient;
    }

    public static synchronized void clearInstancesForTest() {
        synchronized (ConfigStorageClient.class) {
            clientInstances.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public String getFileName() {
        return this.fileName;
    }
}
