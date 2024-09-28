package com.google.firebase.messaging;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.iid.zzaq;
import java.util.ArrayDeque;
import java.util.Queue;

/* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
public class FirebaseMessagingService extends zzc {
    private static final Queue<String> zza = new ArrayDeque(10);

    public void onMessageReceived(RemoteMessage remoteMessage) {
    }

    public void onDeletedMessages() {
    }

    public void onMessageSent(String str) {
    }

    public void onSendError(String str, Exception exc) {
    }

    public void onNewToken(String str) {
    }

    /* access modifiers changed from: protected */
    public final Intent zza(Intent intent) {
        return zzaq.zza().zzb();
    }

    public final boolean zzb(Intent intent) {
        if (!"com.google.firebase.messaging.NOTIFICATION_OPEN".equals(intent.getAction())) {
            return false;
        }
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("pending_intent");
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e("FirebaseMessaging", "Notification pending intent canceled");
            }
        }
        if (!zzo.zzd(intent)) {
            return true;
        }
        zzo.zza(intent);
        return true;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00f8, code lost:
        if (r1.equals("gcm") != false) goto L_0x0106;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzc(android.content.Intent r13) {
        /*
            r12 = this;
            java.lang.String r0 = r13.getAction()
            java.lang.String r1 = "com.google.android.c2dm.intent.RECEIVE"
            boolean r1 = r1.equals(r0)
            java.lang.String r2 = "FirebaseMessaging"
            if (r1 != 0) goto L_0x0059
            java.lang.String r1 = "com.google.firebase.messaging.RECEIVE_DIRECT_BOOT"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0017
            goto L_0x0059
        L_0x0017:
            java.lang.String r1 = "com.google.firebase.messaging.NOTIFICATION_DISMISS"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0029
            boolean r0 = com.google.firebase.messaging.zzo.zzd(r13)
            if (r0 == 0) goto L_0x0058
            com.google.firebase.messaging.zzo.zzb(r13)
            return
        L_0x0029:
            java.lang.String r1 = "com.google.firebase.messaging.NEW_TOKEN"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x003b
            java.lang.String r0 = "token"
            java.lang.String r13 = r13.getStringExtra(r0)
            r12.onNewToken(r13)
            return
        L_0x003b:
            java.lang.String r0 = "Unknown intent action: "
            java.lang.String r13 = r13.getAction()
            java.lang.String r13 = java.lang.String.valueOf(r13)
            int r1 = r13.length()
            if (r1 == 0) goto L_0x0050
            java.lang.String r13 = r0.concat(r13)
            goto L_0x0055
        L_0x0050:
            java.lang.String r13 = new java.lang.String
            r13.<init>(r0)
        L_0x0055:
            android.util.Log.d(r2, r13)
        L_0x0058:
            return
        L_0x0059:
            java.lang.String r0 = "google.message_id"
            java.lang.String r1 = r13.getStringExtra(r0)
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            r4 = 0
            r5 = 2
            if (r3 == 0) goto L_0x006e
            com.google.android.gms.tasks.Task r3 = com.google.android.gms.tasks.Tasks.forResult(r4)
            goto L_0x007e
        L_0x006e:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            r3.putString(r0, r1)
            com.google.firebase.iid.zzv r6 = com.google.firebase.iid.zzv.zza((android.content.Context) r12)
            com.google.android.gms.tasks.Task r3 = r6.zza(r5, r3)
        L_0x007e:
            boolean r6 = android.text.TextUtils.isEmpty(r1)
            r7 = 1
            r8 = 3
            r9 = 0
            if (r6 == 0) goto L_0x008b
            r1 = 0
            goto L_0x00c9
        L_0x008b:
            java.util.Queue<java.lang.String> r6 = zza
            boolean r6 = r6.contains(r1)
            if (r6 == 0) goto L_0x00b4
            boolean r6 = android.util.Log.isLoggable(r2, r8)
            if (r6 == 0) goto L_0x00b2
            java.lang.String r6 = "Received duplicate message: "
            java.lang.String r1 = java.lang.String.valueOf(r1)
            int r10 = r1.length()
            if (r10 == 0) goto L_0x00aa
            java.lang.String r1 = r6.concat(r1)
            goto L_0x00af
        L_0x00aa:
            java.lang.String r1 = new java.lang.String
            r1.<init>(r6)
        L_0x00af:
            android.util.Log.d(r2, r1)
        L_0x00b2:
            r1 = 1
            goto L_0x00c9
        L_0x00b4:
            java.util.Queue<java.lang.String> r6 = zza
            int r6 = r6.size()
            r10 = 10
            if (r6 < r10) goto L_0x00c3
            java.util.Queue<java.lang.String> r6 = zza
            r6.remove()
        L_0x00c3:
            java.util.Queue<java.lang.String> r6 = zza
            r6.add(r1)
            r1 = 0
        L_0x00c9:
            if (r1 != 0) goto L_0x01cc
            java.lang.String r1 = "message_type"
            java.lang.String r1 = r13.getStringExtra(r1)
            java.lang.String r6 = "gcm"
            if (r1 != 0) goto L_0x00d7
            r1 = r6
        L_0x00d7:
            r10 = -1
            int r11 = r1.hashCode()
            switch(r11) {
                case -2062414158: goto L_0x00fb;
                case 102161: goto L_0x00f4;
                case 814694033: goto L_0x00ea;
                case 814800675: goto L_0x00e0;
                default: goto L_0x00df;
            }
        L_0x00df:
            goto L_0x0105
        L_0x00e0:
            java.lang.String r6 = "send_event"
            boolean r6 = r1.equals(r6)
            if (r6 == 0) goto L_0x00df
            r9 = 2
            goto L_0x0106
        L_0x00ea:
            java.lang.String r6 = "send_error"
            boolean r6 = r1.equals(r6)
            if (r6 == 0) goto L_0x00df
            r9 = 3
            goto L_0x0106
        L_0x00f4:
            boolean r6 = r1.equals(r6)
            if (r6 == 0) goto L_0x00df
            goto L_0x0106
        L_0x00fb:
            java.lang.String r6 = "deleted_messages"
            boolean r6 = r1.equals(r6)
            if (r6 == 0) goto L_0x00df
            r9 = 1
            goto L_0x0106
        L_0x0105:
            r9 = -1
        L_0x0106:
            if (r9 == 0) goto L_0x0157
            if (r9 == r7) goto L_0x0152
            if (r9 == r5) goto L_0x0149
            if (r9 == r8) goto L_0x012a
            java.lang.String r13 = "Received message with unknown type: "
            java.lang.String r0 = java.lang.String.valueOf(r1)
            int r1 = r0.length()
            if (r1 == 0) goto L_0x011f
            java.lang.String r13 = r13.concat(r0)
            goto L_0x0125
        L_0x011f:
            java.lang.String r0 = new java.lang.String
            r0.<init>(r13)
            r13 = r0
        L_0x0125:
            android.util.Log.w(r2, r13)
            goto L_0x01cc
        L_0x012a:
            java.lang.String r0 = r13.getStringExtra(r0)
            if (r0 != 0) goto L_0x0138
            java.lang.String r0 = "message_id"
            java.lang.String r0 = r13.getStringExtra(r0)
        L_0x0138:
            com.google.firebase.messaging.SendException r1 = new com.google.firebase.messaging.SendException
            java.lang.String r4 = "error"
            java.lang.String r13 = r13.getStringExtra(r4)
            r1.<init>(r13)
            r12.onSendError(r0, r1)
            goto L_0x01cc
        L_0x0149:
            java.lang.String r13 = r13.getStringExtra(r0)
            r12.onMessageSent(r13)
            goto L_0x01cc
        L_0x0152:
            r12.onDeletedMessages()
            goto L_0x01cc
        L_0x0157:
            boolean r0 = com.google.firebase.messaging.zzo.zzd(r13)
            if (r0 == 0) goto L_0x0160
            com.google.firebase.messaging.zzo.zza((android.content.Intent) r13, (com.google.android.datatransport.Transport<java.lang.String>) r4)
        L_0x0160:
            boolean r0 = com.google.firebase.messaging.zzo.zze(r13)
            if (r0 == 0) goto L_0x0182
            com.google.android.datatransport.TransportFactory r0 = com.google.firebase.messaging.FirebaseMessaging.zza     // Catch:{ NullPointerException -> 0x017c }
            java.lang.String r1 = "FCM_CLIENT_EVENT_LOGGING"
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            java.lang.String r5 = "json"
            com.google.android.datatransport.Encoding r5 = com.google.android.datatransport.Encoding.of(r5)     // Catch:{ NullPointerException -> 0x017c }
            com.google.android.datatransport.Transformer r6 = com.google.firebase.messaging.zzk.zza     // Catch:{ NullPointerException -> 0x017c }
            com.google.android.datatransport.Transport r0 = r0.getTransport(r1, r4, r5, r6)     // Catch:{ NullPointerException -> 0x017c }
            com.google.firebase.messaging.zzo.zza((android.content.Intent) r13, (com.google.android.datatransport.Transport<java.lang.String>) r0)     // Catch:{ NullPointerException -> 0x017c }
            goto L_0x0182
        L_0x017c:
            r0 = move-exception
            java.lang.String r0 = "TransportFactory is null. Skip exporting message delivery metrics to Big Query"
            android.util.Log.e(r2, r0)
        L_0x0182:
            android.os.Bundle r0 = r13.getExtras()
            if (r0 != 0) goto L_0x018e
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
        L_0x018e:
            java.lang.String r1 = "androidx.contentpager.content.wakelockid"
            r0.remove(r1)
            boolean r1 = com.google.firebase.messaging.zzn.zza((android.os.Bundle) r0)
            if (r1 == 0) goto L_0x01c4
            com.google.firebase.messaging.zzn r1 = new com.google.firebase.messaging.zzn
            r1.<init>(r0)
            java.util.concurrent.ExecutorService r4 = java.util.concurrent.Executors.newSingleThreadExecutor()
            com.google.firebase.messaging.zzd r5 = new com.google.firebase.messaging.zzd
            r5.<init>(r12, r1, r4)
            boolean r1 = r5.zza()     // Catch:{ all -> 0x01bf }
            if (r1 == 0) goto L_0x01b1
            r4.shutdown()
            goto L_0x01cc
        L_0x01b1:
            r4.shutdown()
            boolean r1 = com.google.firebase.messaging.zzo.zzd(r13)
            if (r1 == 0) goto L_0x01c4
            com.google.firebase.messaging.zzo.zzc(r13)
            goto L_0x01c4
        L_0x01bf:
            r13 = move-exception
            r4.shutdown()
            throw r13
        L_0x01c4:
            com.google.firebase.messaging.RemoteMessage r13 = new com.google.firebase.messaging.RemoteMessage
            r13.<init>(r0)
            r12.onMessageReceived(r13)
        L_0x01cc:
            r0 = 1
            java.util.concurrent.TimeUnit r13 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ ExecutionException -> 0x01d8, InterruptedException -> 0x01d6, TimeoutException -> 0x01d4 }
            com.google.android.gms.tasks.Tasks.await(r3, r0, r13)     // Catch:{ ExecutionException -> 0x01d8, InterruptedException -> 0x01d6, TimeoutException -> 0x01d4 }
            return
        L_0x01d4:
            r13 = move-exception
            goto L_0x01d9
        L_0x01d6:
            r13 = move-exception
            goto L_0x01d9
        L_0x01d8:
            r13 = move-exception
        L_0x01d9:
            java.lang.String r13 = java.lang.String.valueOf(r13)
            java.lang.String r0 = java.lang.String.valueOf(r13)
            int r0 = r0.length()
            int r0 = r0 + 20
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>(r0)
            java.lang.String r0 = "Message ack failed: "
            r1.append(r0)
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            android.util.Log.w(r2, r13)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.messaging.FirebaseMessagingService.zzc(android.content.Intent):void");
    }
}
