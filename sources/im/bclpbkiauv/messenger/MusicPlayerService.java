package im.bclpbkiauv.messenger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.RemoteControlClient;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.audioinfo.AudioInfo;
import im.bclpbkiauv.ui.LaunchActivity;
import java.io.File;

public class MusicPlayerService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private static final int ID_NOTIFICATION = 5;
    public static final String NOTIFY_CLOSE = "im.bclpbkiauv.android.musicplayer.close";
    public static final String NOTIFY_NEXT = "im.bclpbkiauv.android.musicplayer.next";
    public static final String NOTIFY_PAUSE = "im.bclpbkiauv.android.musicplayer.pause";
    public static final String NOTIFY_PLAY = "im.bclpbkiauv.android.musicplayer.play";
    public static final String NOTIFY_PREVIOUS = "im.bclpbkiauv.android.musicplayer.previous";
    public static final String NOTIFY_SEEK = "im.bclpbkiauv.android.musicplayer.seek";
    private static boolean supportBigNotifications = (Build.VERSION.SDK_INT >= 16);
    private static boolean supportLockScreenControls;
    private Bitmap albumArtPlaceholder;
    private AudioManager audioManager;
    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.media.AUDIO_BECOMING_NOISY".equals(intent.getAction())) {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    };
    private ImageReceiver imageReceiver;
    private String loadingFilePath;
    private MediaSession mediaSession;
    private int notificationMessageID;
    private PlaybackState.Builder playbackState;
    /* access modifiers changed from: private */
    public RemoteControlClient remoteControlClient;

    static {
        boolean z = true;
        if (Build.VERSION.SDK_INT >= 21 && TextUtils.isEmpty(AndroidUtilities.getSystemProperty("ro.miui.ui.version.code"))) {
            z = false;
        }
        supportLockScreenControls = z;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        this.audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad);
        }
        ImageReceiver imageReceiver2 = new ImageReceiver((View) null);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setDelegate(new ImageReceiver.ImageReceiverDelegate() {
            public final void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2) {
                MusicPlayerService.this.lambda$onCreate$0$MusicPlayerService(imageReceiver, z, z2);
            }

            public /* synthetic */ void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        });
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession = new MediaSession(this, "AppAudioPlayer");
            this.playbackState = new PlaybackState.Builder();
            this.albumArtPlaceholder = Bitmap.createBitmap(AndroidUtilities.dp(102.0f), AndroidUtilities.dp(102.0f), Bitmap.Config.ARGB_8888);
            Drawable placeholder = getResources().getDrawable(R.drawable.nocover_big);
            placeholder.setBounds(0, 0, this.albumArtPlaceholder.getWidth(), this.albumArtPlaceholder.getHeight());
            placeholder.draw(new Canvas(this.albumArtPlaceholder));
            this.mediaSession.setCallback(new MediaSession.Callback() {
                public void onPlay() {
                    MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onPause() {
                    MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
                }

                public void onSkipToNext() {
                    MediaController.getInstance().playNextMessage();
                }

                public void onSkipToPrevious() {
                    MediaController.getInstance().playPreviousMessage();
                }

                public void onSeekTo(long pos) {
                    MessageObject object = MediaController.getInstance().getPlayingMessageObject();
                    if (object != null) {
                        MediaController.getInstance().seekToProgress(object, ((float) (pos / 1000)) / ((float) object.getDuration()));
                        MusicPlayerService.this.updatePlaybackState(pos);
                    }
                }

                public void onStop() {
                }
            });
            this.mediaSession.setActive(true);
        }
        registerReceiver(this.headsetPlugReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        super.onCreate();
    }

    public /* synthetic */ void lambda$onCreate$0$MusicPlayerService(ImageReceiver imageReceiver2, boolean set, boolean thumb) {
        if (set && !TextUtils.isEmpty(this.loadingFilePath)) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, true);
            }
            this.loadingFilePath = null;
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                if ((getPackageName() + ".STOP_PLAYER").equals(intent.getAction())) {
                    MediaController.getInstance().cleanupPlayer(true, true);
                    return 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    MusicPlayerService.this.stopSelf();
                }
            });
            return 1;
        }
        if (supportLockScreenControls) {
            ComponentName remoteComponentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
            try {
                if (this.remoteControlClient == null) {
                    this.audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                    Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
                    mediaButtonIntent.setComponent(remoteComponentName);
                    RemoteControlClient remoteControlClient2 = new RemoteControlClient(PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0));
                    this.remoteControlClient = remoteControlClient2;
                    this.audioManager.registerRemoteControlClient(remoteControlClient2);
                }
                this.remoteControlClient.setTransportControlFlags(PsExtractor.PRIVATE_STREAM_1);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        createNotification(messageObject, false);
        return 1;
    }

    private Bitmap loadArtworkFromUrl(String artworkUrl, boolean big, boolean tryLoad) {
        String httpFileName = ImageLoader.getHttpFileName(artworkUrl);
        File path = ImageLoader.getHttpFilePath(artworkUrl, "jpg");
        if (path.exists()) {
            String absolutePath = path.getAbsolutePath();
            float f = 600.0f;
            float f2 = big ? 600.0f : 100.0f;
            if (!big) {
                f = 100.0f;
            }
            return ImageLoader.loadBitmap(absolutePath, (Uri) null, f2, f, false);
        }
        if (tryLoad) {
            this.loadingFilePath = path.getAbsolutePath();
            if (!big) {
                this.imageReceiver.setImage(artworkUrl, "48_48", (Drawable) null, (String) null, 0);
            }
        } else {
            this.loadingFilePath = null;
        }
        return null;
    }

    private void createNotification(MessageObject messageObject, boolean forBitmap) {
        AudioInfo audioInfo;
        int i;
        int i2;
        Bitmap albumArt;
        Bitmap albumArt2;
        MessageObject messageObject2 = messageObject;
        String songName = messageObject.getMusicTitle();
        String authorName = messageObject.getMusicAuthor();
        AudioInfo audioInfo2 = MediaController.getInstance().getAudioInfo();
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        intent.setAction("com.tmessages.openplayer");
        intent.addCategory("android.intent.category.LAUNCHER");
        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, 0);
        String artworkUrl = messageObject2.getArtworkUrl(true);
        String artworkUrlBig = messageObject2.getArtworkUrl(false);
        long duration = (long) (messageObject.getDuration() * 1000);
        Bitmap albumArt3 = audioInfo2 != null ? audioInfo2.getSmallCover() : null;
        Bitmap fullAlbumArt = audioInfo2 != null ? audioInfo2.getCover() : null;
        this.loadingFilePath = null;
        this.imageReceiver.setImageBitmap((Drawable) null);
        if (albumArt3 != null || TextUtils.isEmpty(artworkUrl)) {
            this.loadingFilePath = FileLoader.getPathToAttach(messageObject.getDocument()).getAbsolutePath();
        } else {
            fullAlbumArt = loadArtworkFromUrl(artworkUrlBig, true, !forBitmap);
            if (fullAlbumArt == null) {
                Bitmap loadArtworkFromUrl = loadArtworkFromUrl(artworkUrl, false, !forBitmap);
                albumArt3 = loadArtworkFromUrl;
                fullAlbumArt = loadArtworkFromUrl;
            } else {
                albumArt3 = loadArtworkFromUrl(artworkUrlBig, false, !forBitmap);
            }
        }
        Bitmap albumArt4 = albumArt3;
        String str = "";
        if (Build.VERSION.SDK_INT >= 21) {
            boolean isPlaying = !MediaController.getInstance().isMessagePaused();
            Intent intent2 = intent;
            PendingIntent pendingPrev = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), C.ENCODING_PCM_MU_LAW);
            Context applicationContext = getApplicationContext();
            Intent intent3 = new Intent(this, getClass());
            StringBuilder sb = new StringBuilder();
            String str2 = artworkUrl;
            sb.append(getPackageName());
            sb.append(".STOP_PLAYER");
            PendingIntent pendingStop = PendingIntent.getService(applicationContext, 0, intent3.setAction(sb.toString()), C.ENCODING_PCM_MU_LAW);
            String str3 = artworkUrlBig;
            PendingIntent pendingPlaypause = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(isPlaying ? NOTIFY_PAUSE : NOTIFY_PLAY).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), C.ENCODING_PCM_MU_LAW);
            long duration2 = duration;
            PendingIntent pendingNext = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), C.ENCODING_PCM_MU_LAW);
            PendingIntent pendingSeek = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_SEEK).setComponent(new ComponentName(this, MusicPlayerReceiver.class)), C.ENCODING_PCM_MU_LAW);
            Notification.Builder bldr = new Notification.Builder(this);
            PendingIntent pendingIntent = pendingStop;
            PendingIntent pendingIntent2 = pendingSeek;
            bldr.setSmallIcon(R.drawable.player).setOngoing(isPlaying).setContentTitle(songName).setContentText(authorName).setSubText(audioInfo2 != null ? audioInfo2.getAlbum() : null).setContentIntent(contentIntent).setDeleteIntent(pendingStop).setShowWhen(false).setCategory(NotificationCompat.CATEGORY_TRANSPORT).setPriority(2).setStyle(new Notification.MediaStyle().setMediaSession(this.mediaSession.getSessionToken()).setShowActionsInCompactView(new int[]{0, 1, 2}));
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationsController.checkOtherNotificationsChannel();
                bldr.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            }
            if (albumArt4 != null) {
                albumArt = albumArt4;
                bldr.setLargeIcon(albumArt);
            } else {
                albumArt = albumArt4;
                bldr.setLargeIcon(this.albumArtPlaceholder);
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                audioInfo = audioInfo2;
                albumArt2 = albumArt;
                this.playbackState.setState(6, 0, 1.0f).setActions(0);
                bldr.addAction(new Notification.Action.Builder(R.drawable.ic_action_previous, str, pendingPrev).build()).addAction(new Notification.Action.Builder(R.drawable.loading_animation2, str, (PendingIntent) null).build()).addAction(new Notification.Action.Builder(R.drawable.ic_action_next, str, pendingNext).build());
            } else {
                audioInfo = audioInfo2;
                albumArt2 = albumArt;
                this.playbackState.setState(isPlaying ? 3 : 2, ((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, isPlaying ? 1.0f : 0.0f).setActions(822);
                bldr.addAction(new Notification.Action.Builder(R.drawable.ic_action_previous, str, pendingPrev).build()).addAction(new Notification.Action.Builder(isPlaying ? R.drawable.ic_action_pause : R.drawable.ic_action_play, str, pendingPlaypause).build()).addAction(new Notification.Action.Builder(R.drawable.ic_action_next, str, pendingNext).build());
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
            this.mediaSession.setMetadata(new MediaMetadata.Builder().putBitmap("android.media.metadata.ALBUM_ART", fullAlbumArt).putString("android.media.metadata.ALBUM_ARTIST", authorName).putLong("android.media.metadata.DURATION", duration2).putString("android.media.metadata.TITLE", songName).putString("android.media.metadata.ALBUM", audioInfo != null ? audioInfo.getAlbum() : null).build());
            bldr.setVisibility(1);
            Notification notification = bldr.build();
            if (isPlaying) {
                startForeground(5, notification);
            } else {
                stopForeground(false);
                ((NotificationManager) getSystemService("notification")).notify(5, notification);
            }
            Bitmap bitmap = albumArt2;
        } else {
            audioInfo = audioInfo2;
            Intent intent4 = intent;
            String str4 = artworkUrl;
            String str5 = artworkUrlBig;
            long j = duration;
            Bitmap albumArt5 = albumArt4;
            RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_small_notification);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.player_big_notification);
            }
            Notification notification2 = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.player).setContentIntent(contentIntent).setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(songName).build();
            notification2.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification2.bigContentView = expandedView;
            }
            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }
            if (albumArt5 != null) {
                Bitmap albumArt6 = albumArt5;
                notification2.contentView.setImageViewBitmap(R.id.player_album_art, albumArt6);
                if (supportBigNotifications) {
                    notification2.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt6);
                }
            } else {
                notification2.contentView.setImageViewResource(R.id.player_album_art, R.drawable.nocover_small);
                if (supportBigNotifications) {
                    notification2.bigContentView.setImageViewResource(R.id.player_album_art, R.drawable.nocover_big);
                }
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                notification2.contentView.setViewVisibility(R.id.player_pause, 8);
                notification2.contentView.setViewVisibility(R.id.player_play, 8);
                notification2.contentView.setViewVisibility(R.id.player_next, 8);
                notification2.contentView.setViewVisibility(R.id.player_previous, 8);
                notification2.contentView.setViewVisibility(R.id.player_progress_bar, 0);
                if (supportBigNotifications) {
                    notification2.bigContentView.setViewVisibility(R.id.player_pause, 8);
                    notification2.bigContentView.setViewVisibility(R.id.player_play, 8);
                    notification2.bigContentView.setViewVisibility(R.id.player_next, 8);
                    notification2.bigContentView.setViewVisibility(R.id.player_previous, 8);
                    notification2.bigContentView.setViewVisibility(R.id.player_progress_bar, 0);
                }
            } else {
                notification2.contentView.setViewVisibility(R.id.player_progress_bar, 8);
                notification2.contentView.setViewVisibility(R.id.player_next, 0);
                notification2.contentView.setViewVisibility(R.id.player_previous, 0);
                if (supportBigNotifications) {
                    notification2.bigContentView.setViewVisibility(R.id.player_next, 0);
                    notification2.bigContentView.setViewVisibility(R.id.player_previous, 0);
                    i2 = 8;
                    notification2.bigContentView.setViewVisibility(R.id.player_progress_bar, 8);
                } else {
                    i2 = 8;
                }
                if (MediaController.getInstance().isMessagePaused()) {
                    notification2.contentView.setViewVisibility(R.id.player_pause, i2);
                    notification2.contentView.setViewVisibility(R.id.player_play, 0);
                    if (supportBigNotifications) {
                        notification2.bigContentView.setViewVisibility(R.id.player_pause, i2);
                        notification2.bigContentView.setViewVisibility(R.id.player_play, 0);
                    }
                } else {
                    notification2.contentView.setViewVisibility(R.id.player_pause, 0);
                    notification2.contentView.setViewVisibility(R.id.player_play, 8);
                    if (supportBigNotifications) {
                        notification2.bigContentView.setViewVisibility(R.id.player_pause, 0);
                        notification2.bigContentView.setViewVisibility(R.id.player_play, 8);
                    }
                }
            }
            notification2.contentView.setTextViewText(R.id.player_song_name, songName);
            notification2.contentView.setTextViewText(R.id.player_author_name, authorName);
            if (supportBigNotifications) {
                notification2.bigContentView.setTextViewText(R.id.player_song_name, songName);
                notification2.bigContentView.setTextViewText(R.id.player_author_name, authorName);
                RemoteViews remoteViews = notification2.bigContentView;
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    str = audioInfo.getAlbum();
                }
                remoteViews.setTextViewText(R.id.player_album_title, str);
            }
            notification2.flags |= 2;
            startForeground(5, notification2);
        }
        if (this.remoteControlClient != null) {
            int currentID = MediaController.getInstance().getPlayingMessageObject().getId();
            if (this.notificationMessageID != currentID) {
                this.notificationMessageID = currentID;
                RemoteControlClient.MetadataEditor metadataEditor = this.remoteControlClient.editMetadata(true);
                i = 2;
                metadataEditor.putString(2, authorName);
                metadataEditor.putString(7, songName);
                if (audioInfo != null && !TextUtils.isEmpty(audioInfo.getAlbum())) {
                    metadataEditor.putString(1, audioInfo.getAlbum());
                }
                metadataEditor.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                if (fullAlbumArt != null) {
                    try {
                        metadataEditor.putBitmap(100, fullAlbumArt);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                metadataEditor.apply();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (MusicPlayerService.this.remoteControlClient != null && MediaController.getInstance().getPlayingMessageObject() != null) {
                            if (((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) == C.TIME_UNSET) {
                                AndroidUtilities.runOnUIThread(this, 500);
                                return;
                            }
                            RemoteControlClient.MetadataEditor metadataEditor = MusicPlayerService.this.remoteControlClient.editMetadata(false);
                            metadataEditor.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
                            metadataEditor.apply();
                            int i = 2;
                            if (Build.VERSION.SDK_INT >= 18) {
                                RemoteControlClient access$100 = MusicPlayerService.this.remoteControlClient;
                                if (!MediaController.getInstance().isMessagePaused()) {
                                    i = 3;
                                }
                                access$100.setPlaybackState(i, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                                return;
                            }
                            RemoteControlClient access$1002 = MusicPlayerService.this.remoteControlClient;
                            if (!MediaController.getInstance().isMessagePaused()) {
                                i = 3;
                            }
                            access$1002.setPlaybackState(i);
                        }
                    }
                }, 1000);
            } else {
                i = 2;
            }
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.remoteControlClient.setPlaybackState(8);
                String str6 = songName;
                return;
            }
            RemoteControlClient.MetadataEditor metadataEditor2 = this.remoteControlClient.editMetadata(false);
            metadataEditor2.putLong(9, ((long) MediaController.getInstance().getPlayingMessageObject().audioPlayerDuration) * 1000);
            metadataEditor2.apply();
            if (Build.VERSION.SDK_INT >= 18) {
                RemoteControlClient remoteControlClient2 = this.remoteControlClient;
                if (!MediaController.getInstance().isMessagePaused()) {
                    i = 3;
                }
                int i3 = currentID;
                String str7 = songName;
                remoteControlClient2.setPlaybackState(i, Math.max(((long) MediaController.getInstance().getPlayingMessageObject().audioProgressSec) * 1000, 100), MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
                return;
            }
            String str8 = songName;
            RemoteControlClient remoteControlClient3 = this.remoteControlClient;
            if (!MediaController.getInstance().isMessagePaused()) {
                i = 3;
            }
            remoteControlClient3.setPlaybackState(i);
            return;
        }
    }

    /* access modifiers changed from: private */
    public void updatePlaybackState(long seekTo) {
        if (Build.VERSION.SDK_INT >= 21) {
            boolean isPlaying = !MediaController.getInstance().isMessagePaused();
            float f = 1.0f;
            if (MediaController.getInstance().isDownloadingCurrentMessage()) {
                this.playbackState.setState(6, 0, 1.0f).setActions(0);
            } else {
                PlaybackState.Builder builder = this.playbackState;
                int i = isPlaying ? 3 : 2;
                if (!isPlaying) {
                    f = 0.0f;
                }
                builder.setState(i, seekTo, f).setActions(822);
            }
            this.mediaSession.setPlaybackState(this.playbackState.build());
        }
    }

    public void setListeners(RemoteViews view) {
        view.setOnClickPendingIntent(R.id.player_previous, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PREVIOUS), 134217728));
        view.setOnClickPendingIntent(R.id.player_close, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_CLOSE), 134217728));
        view.setOnClickPendingIntent(R.id.player_pause, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PAUSE), 134217728));
        view.setOnClickPendingIntent(R.id.player_next, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_NEXT), 134217728));
        view.setOnClickPendingIntent(R.id.player_play, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(NOTIFY_PLAY), 134217728));
    }

    public void onDestroy() {
        unregisterReceiver(this.headsetPlugReceiver);
        super.onDestroy();
        RemoteControlClient remoteControlClient2 = this.remoteControlClient;
        if (remoteControlClient2 != null) {
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient2.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mediaSession.release();
        }
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidSeek);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileDidLoad);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String str;
        String str2;
        if (id == NotificationCenter.messagePlayingPlayStateChanged) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null) {
                createNotification(messageObject, false);
            } else {
                stopSelf();
            }
        } else if (id == NotificationCenter.messagePlayingDidSeek) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (this.remoteControlClient != null && Build.VERSION.SDK_INT >= 18) {
                this.remoteControlClient.setPlaybackState(MediaController.getInstance().isMessagePaused() ? 2 : 3, ((long) Math.round(((float) messageObject2.audioPlayerDuration) * args[1].floatValue())) * 1000, MediaController.getInstance().isMessagePaused() ? 0.0f : 1.0f);
            }
        } else if (id == NotificationCenter.httpFileDidLoad) {
            String path = args[0];
            MessageObject messageObject3 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject3 != null && (str2 = this.loadingFilePath) != null && str2.equals(path)) {
                createNotification(messageObject3, false);
            }
        } else if (id == NotificationCenter.fileDidLoad) {
            String path2 = args[0];
            MessageObject messageObject4 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject4 != null && (str = this.loadingFilePath) != null && str.equals(path2)) {
                createNotification(messageObject4, false);
            }
        }
    }
}
