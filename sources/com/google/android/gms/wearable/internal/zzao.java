package com.google.android.gms.wearable.internal;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.api.internal.ListenerHolders;
import com.google.android.gms.common.internal.PendingResultUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelClient;
import java.io.InputStream;
import java.io.OutputStream;

public final class zzao extends ChannelClient {
    private final zzaj zzcg = new zzaj();

    public zzao(Context context, GoogleApi.Settings settings) {
        super(context, settings);
    }

    public zzao(Activity activity, GoogleApi.Settings settings) {
        super(activity, settings);
    }

    public final Task<ChannelClient.Channel> openChannel(String str, String str2) {
        return PendingResultUtil.toTask(this.zzcg.openChannel(asGoogleApiClient(), str, str2), zzap.zzbx);
    }

    public final Task<Void> registerChannelCallback(ChannelClient.ChannelCallback channelCallback) {
        Preconditions.checkNotNull(channelCallback, "listener is null");
        ListenerHolder createListenerHolder = ListenerHolders.createListenerHolder(channelCallback, getLooper(), "ChannelListener");
        IntentFilter[] intentFilterArr = {zzgj.zzc("com.google.android.gms.wearable.CHANNEL_EVENT")};
        zzas zzas = new zzas(channelCallback);
        return doRegisterEventListener(new zzat(zzas, (String) null, intentFilterArr, createListenerHolder, ListenerHolders.createListenerHolder(zzas, getLooper(), "ChannelListener")), new zzau(zzas, (String) null, createListenerHolder.getListenerKey()));
    }

    public final Task<Boolean> unregisterChannelCallback(ChannelClient.ChannelCallback channelCallback) {
        return doUnregisterEventListener(ListenerHolders.createListenerHolder(channelCallback, getLooper(), "ChannelListener").getListenerKey());
    }

    private static zzay zza(ChannelClient.Channel channel) {
        Preconditions.checkNotNull(channel, "channel must not be null");
        return (zzay) channel;
    }

    /* access modifiers changed from: private */
    public static zzay zza(Channel channel) {
        Preconditions.checkNotNull(channel, "channel must not be null");
        return (zzay) channel;
    }

    public final Task<Void> close(ChannelClient.Channel channel) {
        return PendingResultUtil.toVoidTask(zza(channel).close(asGoogleApiClient()));
    }

    public final Task<Void> close(ChannelClient.Channel channel, int i) {
        return PendingResultUtil.toVoidTask(zza(channel).close(asGoogleApiClient(), i));
    }

    public final Task<InputStream> getInputStream(ChannelClient.Channel channel) {
        return PendingResultUtil.toTask(zza(channel).getInputStream(asGoogleApiClient()), zzaq.zzbx);
    }

    public final Task<OutputStream> getOutputStream(ChannelClient.Channel channel) {
        return PendingResultUtil.toTask(zza(channel).getOutputStream(asGoogleApiClient()), zzar.zzbx);
    }

    public final Task<Void> receiveFile(ChannelClient.Channel channel, Uri uri, boolean z) {
        return PendingResultUtil.toVoidTask(zza(channel).receiveFile(asGoogleApiClient(), uri, z));
    }

    public final Task<Void> sendFile(ChannelClient.Channel channel, Uri uri) {
        return PendingResultUtil.toVoidTask(zza(channel).sendFile(asGoogleApiClient(), uri));
    }

    public final Task<Void> sendFile(ChannelClient.Channel channel, Uri uri, long j, long j2) {
        return PendingResultUtil.toVoidTask(zza(channel).sendFile(asGoogleApiClient(), uri, j, j2));
    }

    public final Task<Void> registerChannelCallback(ChannelClient.Channel channel, ChannelClient.ChannelCallback channelCallback) {
        String zzc = ((zzay) channel).zzc();
        Preconditions.checkNotNull(channelCallback, "listener is null");
        Looper looper = getLooper();
        String valueOf = String.valueOf(zzc);
        ListenerHolder createListenerHolder = ListenerHolders.createListenerHolder(channelCallback, looper, valueOf.length() != 0 ? "ChannelListener:".concat(valueOf) : new String("ChannelListener:"));
        IntentFilter[] intentFilterArr = {zzgj.zzc("com.google.android.gms.wearable.CHANNEL_EVENT")};
        zzas zzas = new zzas(channelCallback);
        return doRegisterEventListener(new zzat(zzas, zzc, intentFilterArr, createListenerHolder, ListenerHolders.createListenerHolder(zzas, getLooper(), "ChannelListener")), new zzau(zzas, zzc, createListenerHolder.getListenerKey()));
    }

    public final Task<Boolean> unregisterChannelCallback(ChannelClient.Channel channel, ChannelClient.ChannelCallback channelCallback) {
        String zzc = zza(channel).zzc();
        Looper looper = getLooper();
        String valueOf = String.valueOf(zzc);
        return doUnregisterEventListener(ListenerHolders.createListenerHolder(channelCallback, looper, valueOf.length() != 0 ? "ChannelListener:".concat(valueOf) : new String("ChannelListener:")).getListenerKey());
    }
}
