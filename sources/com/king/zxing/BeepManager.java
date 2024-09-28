package com.king.zxing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.Closeable;

public final class BeepManager implements MediaPlayer.OnErrorListener, Closeable {
    private static final float BEEP_VOLUME = 0.1f;
    private static final String TAG = BeepManager.class.getSimpleName();
    private static final long VIBRATE_DURATION = 200;
    private final Activity activity;
    private MediaPlayer mediaPlayer = null;
    private boolean playBeep;
    private boolean vibrate;

    BeepManager(Activity activity2) {
        this.activity = activity2;
        updatePrefs();
    }

    public void setVibrate(boolean vibrate2) {
        this.vibrate = vibrate2;
    }

    public void setPlayBeep(boolean playBeep2) {
        this.playBeep = playBeep2;
    }

    /* access modifiers changed from: package-private */
    public synchronized void updatePrefs() {
        shouldBeep(PreferenceManager.getDefaultSharedPreferences(this.activity), this.activity);
        if (this.playBeep && this.mediaPlayer == null) {
            this.activity.setVolumeControlStream(3);
            this.mediaPlayer = buildMediaPlayer(this.activity);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void playBeepSoundAndVibrate() {
        if (this.playBeep && this.mediaPlayer != null) {
            this.mediaPlayer.start();
        }
        if (this.vibrate) {
            ((Vibrator) this.activity.getSystemService("vibrator")).vibrate(VIBRATE_DURATION);
        }
    }

    private static boolean shouldBeep(SharedPreferences prefs, Context activity2) {
        boolean shouldPlayBeep = prefs.getBoolean(Preferences.KEY_PLAY_BEEP, false);
        if (!shouldPlayBeep || ((AudioManager) activity2.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).getRingerMode() == 2) {
            return shouldPlayBeep;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003d, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003e, code lost:
        if (r7 != null) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0048, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.media.MediaPlayer buildMediaPlayer(android.content.Context r9) {
        /*
            r8 = this;
            android.media.MediaPlayer r0 = new android.media.MediaPlayer
            r0.<init>()
            android.content.res.Resources r1 = r9.getResources()     // Catch:{ IOException -> 0x0049 }
            int r2 = com.king.zxing.R.raw.zxl_beep     // Catch:{ IOException -> 0x0049 }
            android.content.res.AssetFileDescriptor r1 = r1.openRawResourceFd(r2)     // Catch:{ IOException -> 0x0049 }
            r7 = r1
            java.io.FileDescriptor r2 = r7.getFileDescriptor()     // Catch:{ all -> 0x003b }
            long r3 = r7.getStartOffset()     // Catch:{ all -> 0x003b }
            long r5 = r7.getLength()     // Catch:{ all -> 0x003b }
            r1 = r0
            r1.setDataSource(r2, r3, r5)     // Catch:{ all -> 0x003b }
            r0.setOnErrorListener(r8)     // Catch:{ all -> 0x003b }
            r1 = 3
            r0.setAudioStreamType(r1)     // Catch:{ all -> 0x003b }
            r1 = 0
            r0.setLooping(r1)     // Catch:{ all -> 0x003b }
            r1 = 1036831949(0x3dcccccd, float:0.1)
            r0.setVolume(r1, r1)     // Catch:{ all -> 0x003b }
            r0.prepare()     // Catch:{ all -> 0x003b }
            if (r7 == 0) goto L_0x003a
            r7.close()     // Catch:{ IOException -> 0x0049 }
        L_0x003a:
            return r0
        L_0x003b:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003d }
        L_0x003d:
            r2 = move-exception
            if (r7 == 0) goto L_0x0048
            r7.close()     // Catch:{ all -> 0x0044 }
            goto L_0x0048
        L_0x0044:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x0049 }
        L_0x0048:
            throw r2     // Catch:{ IOException -> 0x0049 }
        L_0x0049:
            r1 = move-exception
            com.king.zxing.util.LogUtils.w((java.lang.Throwable) r1)
            r0.release()
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.king.zxing.BeepManager.buildMediaPlayer(android.content.Context):android.media.MediaPlayer");
    }

    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == 100) {
            this.activity.finish();
        } else {
            close();
            updatePrefs();
        }
        return true;
    }

    public synchronized void close() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }
}
