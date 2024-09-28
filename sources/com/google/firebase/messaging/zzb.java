package com.google.firebase.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.util.concurrent.atomic.AtomicInteger;

/* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
public final class zzb {
    private static final AtomicInteger zza = new AtomicInteger((int) SystemClock.elapsedRealtime());

    static zza zza(Context context, zzn zzn) {
        Uri uri;
        Intent intent;
        PendingIntent pendingIntent;
        PendingIntent pendingIntent2;
        Bundle zza2 = zza(context.getPackageManager(), context.getPackageName());
        String packageName = context.getPackageName();
        String zzb = zzb(context, zzn.zza("gcm.n.android_channel_id"), zza2);
        Resources resources = context.getResources();
        PackageManager packageManager = context.getPackageManager();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, zzb);
        builder.setContentTitle(zza(packageName, zzn, packageManager, resources));
        String zza3 = zzn.zza(resources, packageName, "gcm.n.body");
        if (!TextUtils.isEmpty(zza3)) {
            builder.setContentText(zza3);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(zza3));
        }
        builder.setSmallIcon(zza(packageManager, resources, packageName, zzn.zza("gcm.n.icon"), zza2));
        String zzb2 = zzn.zzb();
        Integer num = null;
        if (TextUtils.isEmpty(zzb2)) {
            uri = null;
        } else if ("default".equals(zzb2) || resources.getIdentifier(zzb2, "raw", packageName) == 0) {
            uri = RingtoneManager.getDefaultUri(2);
        } else {
            StringBuilder sb = new StringBuilder(String.valueOf(packageName).length() + 24 + String.valueOf(zzb2).length());
            sb.append("android.resource://");
            sb.append(packageName);
            sb.append("/raw/");
            sb.append(zzb2);
            uri = Uri.parse(sb.toString());
        }
        if (uri != null) {
            builder.setSound(uri);
        }
        String zza4 = zzn.zza("gcm.n.click_action");
        if (!TextUtils.isEmpty(zza4)) {
            intent = new Intent(zza4);
            intent.setPackage(packageName);
            intent.setFlags(C.ENCODING_PCM_MU_LAW);
        } else {
            Uri zza5 = zzn.zza();
            if (zza5 != null) {
                intent = new Intent("android.intent.action.VIEW");
                intent.setPackage(packageName);
                intent.setData(zza5);
            } else {
                Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
                if (launchIntentForPackage == null) {
                    Log.w("FirebaseMessaging", "No activity found to launch app");
                }
                intent = launchIntentForPackage;
            }
        }
        if (intent == null) {
            pendingIntent = null;
        } else {
            intent.addFlags(ConnectionsManager.FileTypeFile);
            intent.putExtras(zzn.zze());
            pendingIntent = PendingIntent.getActivity(context, zza.incrementAndGet(), intent, 1073741824);
            if (zzn.zzb("google.c.a.e")) {
                pendingIntent = zza(context, new Intent("com.google.firebase.messaging.NOTIFICATION_OPEN").putExtras(zzn.zzf()).putExtra("pending_intent", pendingIntent));
            }
        }
        builder.setContentIntent(pendingIntent);
        if (!zzn.zzb("google.c.a.e")) {
            pendingIntent2 = null;
        } else {
            pendingIntent2 = zza(context, new Intent("com.google.firebase.messaging.NOTIFICATION_DISMISS").putExtras(zzn.zzf()));
        }
        if (pendingIntent2 != null) {
            builder.setDeleteIntent(pendingIntent2);
        }
        Integer zza6 = zza(context, zzn.zza("gcm.n.color"), zza2);
        if (zza6 != null) {
            builder.setColor(zza6.intValue());
        }
        int i = 1;
        builder.setAutoCancel(!zzn.zzb("gcm.n.sticky"));
        builder.setLocalOnly(zzn.zzb("gcm.n.local_only"));
        String zza7 = zzn.zza("gcm.n.ticker");
        if (zza7 != null) {
            builder.setTicker(zza7);
        }
        Integer zzc = zzn.zzc("gcm.n.notification_priority");
        if (zzc == null) {
            zzc = null;
        } else if (zzc.intValue() < -2 || zzc.intValue() > 2) {
            String valueOf = String.valueOf(zzc);
            StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 72);
            sb2.append("notificationPriority is invalid ");
            sb2.append(valueOf);
            sb2.append(". Skipping setting notificationPriority.");
            Log.w("FirebaseMessaging", sb2.toString());
            zzc = null;
        }
        if (zzc != null) {
            builder.setPriority(zzc.intValue());
        }
        Integer zzc2 = zzn.zzc("gcm.n.visibility");
        if (zzc2 == null) {
            zzc2 = null;
        } else if (zzc2.intValue() < -1 || zzc2.intValue() > 1) {
            String valueOf2 = String.valueOf(zzc2);
            StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf2).length() + 53);
            sb3.append("visibility is invalid: ");
            sb3.append(valueOf2);
            sb3.append(". Skipping setting visibility.");
            Log.w("NotificationParams", sb3.toString());
            zzc2 = null;
        }
        if (zzc2 != null) {
            builder.setVisibility(zzc2.intValue());
        }
        Integer zzc3 = zzn.zzc("gcm.n.notification_count");
        if (zzc3 != null) {
            if (zzc3.intValue() < 0) {
                String valueOf3 = String.valueOf(zzc3);
                StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf3).length() + 67);
                sb4.append("notificationCount is invalid: ");
                sb4.append(valueOf3);
                sb4.append(". Skipping setting notificationCount.");
                Log.w("FirebaseMessaging", sb4.toString());
            } else {
                num = zzc3;
            }
        }
        if (num != null) {
            builder.setNumber(num.intValue());
        }
        Long zzd = zzn.zzd("gcm.n.event_time");
        if (zzd != null) {
            builder.setShowWhen(true);
            builder.setWhen(zzd.longValue());
        }
        long[] zzc4 = zzn.zzc();
        if (zzc4 != null) {
            builder.setVibrate(zzc4);
        }
        int[] zzd2 = zzn.zzd();
        if (zzd2 != null) {
            builder.setLights(zzd2[0], zzd2[1], zzd2[2]);
        }
        if (!zzn.zzb("gcm.n.default_sound")) {
            i = 0;
        }
        if (zzn.zzb("gcm.n.default_vibrate_timings")) {
            i |= 2;
        }
        if (zzn.zzb("gcm.n.default_light_settings")) {
            i |= 4;
        }
        builder.setDefaults(i);
        String zza8 = zzn.zza("gcm.n.tag");
        if (TextUtils.isEmpty(zza8)) {
            long uptimeMillis = SystemClock.uptimeMillis();
            StringBuilder sb5 = new StringBuilder(37);
            sb5.append("FCM-Notification:");
            sb5.append(uptimeMillis);
            zza8 = sb5.toString();
        }
        return new zza(builder, zza8, 0);
    }

    private static CharSequence zza(String str, zzn zzn, PackageManager packageManager, Resources resources) {
        String zza2 = zzn.zza(resources, str, "gcm.n.title");
        if (!TextUtils.isEmpty(zza2)) {
            return zza2;
        }
        try {
            return packageManager.getApplicationInfo(str, 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 35);
            sb.append("Couldn't get own application info: ");
            sb.append(valueOf);
            Log.e("FirebaseMessaging", sb.toString());
            return "";
        }
    }

    private static boolean zza(Resources resources, int i) {
        if (Build.VERSION.SDK_INT != 26) {
            return true;
        }
        try {
            if (!(resources.getDrawable(i, (Resources.Theme) null) instanceof AdaptiveIconDrawable)) {
                return true;
            }
            StringBuilder sb = new StringBuilder(77);
            sb.append("Adaptive icons cannot be used in notifications. Ignoring icon id: ");
            sb.append(i);
            Log.e("FirebaseMessaging", sb.toString());
            return false;
        } catch (Resources.NotFoundException e) {
            StringBuilder sb2 = new StringBuilder(66);
            sb2.append("Couldn't find resource ");
            sb2.append(i);
            sb2.append(", treating it as an invalid icon");
            Log.e("FirebaseMessaging", sb2.toString());
            return false;
        }
    }

    private static int zza(PackageManager packageManager, Resources resources, String str, String str2, Bundle bundle) {
        if (!TextUtils.isEmpty(str2)) {
            int identifier = resources.getIdentifier(str2, "drawable", str);
            if (identifier != 0 && zza(resources, identifier)) {
                return identifier;
            }
            int identifier2 = resources.getIdentifier(str2, "mipmap", str);
            if (identifier2 != 0 && zza(resources, identifier2)) {
                return identifier2;
            }
            StringBuilder sb = new StringBuilder(String.valueOf(str2).length() + 61);
            sb.append("Icon resource ");
            sb.append(str2);
            sb.append(" not found. Notification will use default icon.");
            Log.w("FirebaseMessaging", sb.toString());
        }
        int i = bundle.getInt("com.google.firebase.messaging.default_notification_icon", 0);
        if (i == 0 || !zza(resources, i)) {
            try {
                i = packageManager.getApplicationInfo(str, 0).icon;
            } catch (PackageManager.NameNotFoundException e) {
                String valueOf = String.valueOf(e);
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 35);
                sb2.append("Couldn't get own application info: ");
                sb2.append(valueOf);
                Log.w("FirebaseMessaging", sb2.toString());
            }
        }
        if (i == 0 || !zza(resources, i)) {
            return 17301651;
        }
        return i;
    }

    private static Integer zza(Context context, String str, Bundle bundle) {
        if (Build.VERSION.SDK_INT < 21) {
            return null;
        }
        if (!TextUtils.isEmpty(str)) {
            try {
                return Integer.valueOf(Color.parseColor(str));
            } catch (IllegalArgumentException e) {
                StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 56);
                sb.append("Color is invalid: ");
                sb.append(str);
                sb.append(". Notification will use default color.");
                Log.w("FirebaseMessaging", sb.toString());
            }
        }
        int i = bundle.getInt("com.google.firebase.messaging.default_notification_color", 0);
        if (i != 0) {
            try {
                return Integer.valueOf(ContextCompat.getColor(context, i));
            } catch (Resources.NotFoundException e2) {
                Log.w("FirebaseMessaging", "Cannot find the color resource referenced in AndroidManifest.");
            }
        }
        return null;
    }

    private static Bundle zza(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 128);
            if (!(applicationInfo == null || applicationInfo.metaData == null)) {
                return applicationInfo.metaData;
            }
        } catch (PackageManager.NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 35);
            sb.append("Couldn't get own application info: ");
            sb.append(valueOf);
            Log.w("FirebaseMessaging", sb.toString());
        }
        return Bundle.EMPTY;
    }

    private static String zzb(Context context, String str, Bundle bundle) {
        if (Build.VERSION.SDK_INT < 26) {
            return null;
        }
        try {
            if (context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).targetSdkVersion < 26) {
                return null;
            }
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            if (!TextUtils.isEmpty(str)) {
                if (notificationManager.getNotificationChannel(str) != null) {
                    return str;
                }
                StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 122);
                sb.append("Notification Channel requested (");
                sb.append(str);
                sb.append(") has not been created by the app. Manifest configuration, or default, value will be used.");
                Log.w("FirebaseMessaging", sb.toString());
            }
            String string = bundle.getString("com.google.firebase.messaging.default_notification_channel_id");
            if (TextUtils.isEmpty(string)) {
                Log.w("FirebaseMessaging", "Missing Default Notification Channel metadata in AndroidManifest. Default value will be used.");
            } else if (notificationManager.getNotificationChannel(string) != null) {
                return string;
            } else {
                Log.w("FirebaseMessaging", "Notification Channel set in AndroidManifest.xml has not been created by the app. Default value will be used.");
            }
            if (notificationManager.getNotificationChannel("fcm_fallback_notification_channel") == null) {
                notificationManager.createNotificationChannel(new NotificationChannel("fcm_fallback_notification_channel", context.getString(context.getResources().getIdentifier("fcm_fallback_notification_channel_label", "string", context.getPackageName())), 3));
            }
            return "fcm_fallback_notification_channel";
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static PendingIntent zza(Context context, Intent intent) {
        return PendingIntent.getBroadcast(context, zza.incrementAndGet(), new Intent("com.google.firebase.MESSAGING_EVENT").setComponent(new ComponentName(context, "com.google.firebase.iid.FirebaseInstanceIdReceiver")).putExtra("wrapped_intent", intent), 1073741824);
    }
}
