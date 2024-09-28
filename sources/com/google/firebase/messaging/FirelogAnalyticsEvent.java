package com.google.firebase.messaging;

import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.common.internal.Preconditions;
import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.io.IOException;

/* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
final class FirelogAnalyticsEvent {
    private final String zza;
    private final Intent zzb;

    /* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
    static class zza implements ObjectEncoder<FirelogAnalyticsEvent> {
        zza() {
        }

        public final /* synthetic */ void encode(Object obj, Object obj2) throws EncodingException, IOException {
            FirelogAnalyticsEvent firelogAnalyticsEvent = (FirelogAnalyticsEvent) obj;
            ObjectEncoderContext objectEncoderContext = (ObjectEncoderContext) obj2;
            Intent zza = firelogAnalyticsEvent.zza();
            objectEncoderContext.add("ttl", zzo.zzf(zza));
            objectEncoderContext.add(NotificationCompat.CATEGORY_EVENT, (Object) firelogAnalyticsEvent.zzb());
            objectEncoderContext.add("instanceId", (Object) zzo.zzc());
            objectEncoderContext.add("priority", zzo.zzm(zza));
            objectEncoderContext.add(RemoteConfigConstants.RequestFieldKey.PACKAGE_NAME, (Object) zzo.zzb());
            objectEncoderContext.add("sdkPlatform", (Object) "ANDROID");
            objectEncoderContext.add("messageType", (Object) zzo.zzk(zza));
            String zzj = zzo.zzj(zza);
            if (zzj != null) {
                objectEncoderContext.add("messageId", (Object) zzj);
            }
            String zzl = zzo.zzl(zza);
            if (zzl != null) {
                objectEncoderContext.add("topic", (Object) zzl);
            }
            String zzg = zzo.zzg(zza);
            if (zzg != null) {
                objectEncoderContext.add("collapseKey", (Object) zzg);
            }
            if (zzo.zzi(zza) != null) {
                objectEncoderContext.add("analyticsLabel", (Object) zzo.zzi(zza));
            }
            if (zzo.zzh(zza) != null) {
                objectEncoderContext.add("composerLabel", (Object) zzo.zzh(zza));
            }
            String zzd = zzo.zzd();
            if (zzd != null) {
                objectEncoderContext.add("projectNumber", (Object) zzd);
            }
        }
    }

    /* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
    static final class zzb implements ObjectEncoder<zzc> {
        zzb() {
        }

        public final /* synthetic */ void encode(Object obj, Object obj2) throws EncodingException, IOException {
            ((ObjectEncoderContext) obj2).add("messaging_client_event", (Object) ((zzc) obj).zza());
        }
    }

    FirelogAnalyticsEvent(String str, Intent intent) {
        this.zza = Preconditions.checkNotEmpty(str, "evenType must be non-null");
        this.zzb = (Intent) Preconditions.checkNotNull(intent, "intent must be non-null");
    }

    /* compiled from: com.google.firebase:firebase-messaging@@20.1.0 */
    static final class zzc {
        private final FirelogAnalyticsEvent zza;

        zzc(FirelogAnalyticsEvent firelogAnalyticsEvent) {
            this.zza = (FirelogAnalyticsEvent) Preconditions.checkNotNull(firelogAnalyticsEvent);
        }

        /* access modifiers changed from: package-private */
        public final FirelogAnalyticsEvent zza() {
            return this.zza;
        }
    }

    /* access modifiers changed from: package-private */
    public final Intent zza() {
        return this.zzb;
    }

    /* access modifiers changed from: package-private */
    public final String zzb() {
        return this.zza;
    }
}
