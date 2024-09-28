package im.bclpbkiauv.messenger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import com.baidu.mapapi.UIMsg;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.audioinfo.AudioInfo;
import im.bclpbkiauv.messenger.voip.VoIPService;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.EmbedBottomSheet;
import im.bclpbkiauv.ui.components.PhotoFilterView;
import im.bclpbkiauv.ui.components.PipRoundVideoView;
import im.bclpbkiauv.ui.components.Point;
import im.bclpbkiauv.ui.components.VideoPlayer;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MediaController implements AudioManager.OnAudioFocusChangeListener, NotificationCenter.NotificationCenterDelegate, SensorEventListener {
    private static final int AUDIO_FOCUSED = 2;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static volatile MediaController Instance = null;
    public static final String MIME_TYPE = "video/avc";
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private static final float VOLUME_DUCK = 0.2f;
    private static final float VOLUME_NORMAL = 1.0f;
    public static AlbumEntry allMediaAlbumEntry;
    public static ArrayList<AlbumEntry> allMediaAlbums = new ArrayList<>();
    public static ArrayList<AlbumEntry> allPhotoAlbums = new ArrayList<>();
    public static AlbumEntry allPhotosAlbumEntry;
    public static AlbumEntry allVideosAlbumEntry;
    private static Runnable broadcastPhotosRunnable;
    private static final String[] projectionPhotos;
    private static final String[] projectionVideo;
    /* access modifiers changed from: private */
    public static Runnable refreshGalleryRunnable;
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private boolean allowStartRecord;
    private int audioFocus = 0;
    private AudioInfo audioInfo;
    /* access modifiers changed from: private */
    public VideoPlayer audioPlayer = null;
    /* access modifiers changed from: private */
    public AudioRecord audioRecorder;
    /* access modifiers changed from: private */
    public Activity baseActivity;
    /* access modifiers changed from: private */
    public boolean callInProgress;
    private boolean cancelCurrentVideoConversion = false;
    private int countLess;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout currentAspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public float currentAspectRatioFrameLayoutRatio;
    private boolean currentAspectRatioFrameLayoutReady;
    /* access modifiers changed from: private */
    public int currentAspectRatioFrameLayoutRotation;
    private float currentPlaybackSpeed = 1.0f;
    private int currentPlaylistNum;
    /* access modifiers changed from: private */
    public TextureView currentTextureView;
    /* access modifiers changed from: private */
    public FrameLayout currentTextureViewContainer;
    private boolean downloadingCurrentMessage;
    /* access modifiers changed from: private */
    public ExternalObserver externalObserver;
    private View feedbackView;
    /* access modifiers changed from: private */
    public ByteBuffer fileBuffer;
    /* access modifiers changed from: private */
    public DispatchQueue fileEncodingQueue;
    private BaseFragment flagSecureFragment;
    private boolean forceLoopCurrentPlaylist;
    private HashMap<String, MessageObject> generatingWaveform = new HashMap<>();
    private MessageObject goingToShowMessageObject;
    private float[] gravity = new float[3];
    private float[] gravityFast = new float[3];
    private Sensor gravitySensor;
    private int hasAudioFocus;
    private boolean ignoreOnPause;
    private boolean ignoreProximity;
    private boolean inputFieldHasText;
    /* access modifiers changed from: private */
    public InternalObserver internalObserver;
    /* access modifiers changed from: private */
    public boolean isDrawingWasReady;
    /* access modifiers changed from: private */
    public boolean isPaused = false;
    private int lastChatAccount;
    private long lastChatEnterTime;
    private long lastChatLeaveTime;
    private ArrayList<Long> lastChatVisibleMessages;
    private long lastMediaCheckTime;
    private int lastMessageId;
    /* access modifiers changed from: private */
    public long lastProgress = 0;
    private float lastProximityValue = -100.0f;
    private TLRPC.EncryptedChat lastSecretChat;
    private long lastTimestamp = 0;
    private TLRPC.User lastUser;
    private float[] linearAcceleration = new float[3];
    private Sensor linearSensor;
    private String[] mediaProjections;
    /* access modifiers changed from: private */
    public PipRoundVideoView pipRoundVideoView;
    /* access modifiers changed from: private */
    public int pipSwitchingState;
    private boolean playMusicAgain;
    private boolean playerWasReady;
    /* access modifiers changed from: private */
    public MessageObject playingMessageObject;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist = new ArrayList<>();
    private float previousAccValue;
    private Timer progressTimer = null;
    private final Object progressTimerSync = new Object();
    private boolean proximityHasDifferentValues;
    private Sensor proximitySensor;
    private boolean proximityTouched;
    private PowerManager.WakeLock proximityWakeLock;
    private ChatActivity raiseChat;
    private boolean raiseToEarRecord;
    private int raisedToBack;
    private int raisedToTop;
    private int raisedToTopSign;
    /* access modifiers changed from: private */
    public int recordBufferSize = 1280;
    /* access modifiers changed from: private */
    public ArrayList<ByteBuffer> recordBuffers = new ArrayList<>();
    private long recordDialogId;
    /* access modifiers changed from: private */
    public DispatchQueue recordQueue;
    private MessageObject recordReplyingMessageObject;
    /* access modifiers changed from: private */
    public Runnable recordRunnable = new Runnable() {
        /* JADX WARNING: Removed duplicated region for block: B:43:0x010a  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x010d  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r17 = this;
                r1 = r17
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                if (r0 == 0) goto L_0x015b
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                boolean r0 = r0.isEmpty()
                r2 = 0
                if (r0 != 0) goto L_0x002e
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                java.lang.Object r0 = r0.get(r2)
                java.nio.ByteBuffer r0 = (java.nio.ByteBuffer) r0
                im.bclpbkiauv.messenger.MediaController r3 = im.bclpbkiauv.messenger.MediaController.this
                java.util.ArrayList r3 = r3.recordBuffers
                r3.remove(r2)
                r3 = r0
                goto L_0x0040
            L_0x002e:
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                int r0 = r0.recordBufferSize
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r0)
                java.nio.ByteOrder r3 = java.nio.ByteOrder.nativeOrder()
                r0.order(r3)
                r3 = r0
            L_0x0040:
                r3.rewind()
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                android.media.AudioRecord r0 = r0.audioRecorder
                int r4 = r3.capacity()
                int r4 = r0.read(r3, r4)
                if (r4 <= 0) goto L_0x0133
                r3.limit(r4)
                r5 = 0
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00ed }
                long r7 = r0.samplesCount     // Catch:{ Exception -> 0x00ed }
                int r0 = r4 / 2
                long r9 = (long) r0     // Catch:{ Exception -> 0x00ed }
                long r7 = r7 + r9
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00ed }
                long r9 = r0.samplesCount     // Catch:{ Exception -> 0x00ed }
                double r9 = (double) r9     // Catch:{ Exception -> 0x00ed }
                double r11 = (double) r7     // Catch:{ Exception -> 0x00ed }
                double r9 = r9 / r11
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00ed }
                short[] r0 = r0.recordSamples     // Catch:{ Exception -> 0x00ed }
                int r0 = r0.length     // Catch:{ Exception -> 0x00ed }
                double r11 = (double) r0     // Catch:{ Exception -> 0x00ed }
                double r9 = r9 * r11
                int r0 = (int) r9     // Catch:{ Exception -> 0x00ed }
                im.bclpbkiauv.messenger.MediaController r9 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00ed }
                short[] r9 = r9.recordSamples     // Catch:{ Exception -> 0x00ed }
                int r9 = r9.length     // Catch:{ Exception -> 0x00ed }
                int r9 = r9 - r0
                if (r0 == 0) goto L_0x00a7
                im.bclpbkiauv.messenger.MediaController r10 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00a3 }
                short[] r10 = r10.recordSamples     // Catch:{ Exception -> 0x00a3 }
                int r10 = r10.length     // Catch:{ Exception -> 0x00a3 }
                float r10 = (float) r10     // Catch:{ Exception -> 0x00a3 }
                float r11 = (float) r0     // Catch:{ Exception -> 0x00a3 }
                float r10 = r10 / r11
                r11 = 0
                r12 = 0
            L_0x008c:
                if (r12 >= r0) goto L_0x00a7
                im.bclpbkiauv.messenger.MediaController r13 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00a3 }
                short[] r13 = r13.recordSamples     // Catch:{ Exception -> 0x00a3 }
                im.bclpbkiauv.messenger.MediaController r14 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00a3 }
                short[] r14 = r14.recordSamples     // Catch:{ Exception -> 0x00a3 }
                int r15 = (int) r11     // Catch:{ Exception -> 0x00a3 }
                short r14 = r14[r15]     // Catch:{ Exception -> 0x00a3 }
                r13[r12] = r14     // Catch:{ Exception -> 0x00a3 }
                float r11 = r11 + r10
                int r12 = r12 + 1
                goto L_0x008c
            L_0x00a3:
                r0 = move-exception
                r16 = r3
                goto L_0x00f0
            L_0x00a7:
                r10 = r0
                r11 = 0
                float r12 = (float) r4
                r13 = 1073741824(0x40000000, float:2.0)
                float r12 = r12 / r13
                float r13 = (float) r9
                float r12 = r12 / r13
                r13 = 0
            L_0x00b0:
                int r14 = r4 / 2
                if (r13 >= r14) goto L_0x00e2
                short r14 = r3.getShort()     // Catch:{ Exception -> 0x00ed }
                r15 = 2500(0x9c4, float:3.503E-42)
                if (r14 <= r15) goto L_0x00c3
                int r15 = r14 * r14
                r16 = r3
                double r2 = (double) r15
                double r5 = r5 + r2
                goto L_0x00c5
            L_0x00c3:
                r16 = r3
            L_0x00c5:
                int r2 = (int) r11
                if (r13 != r2) goto L_0x00dc
                im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00eb }
                short[] r2 = r2.recordSamples     // Catch:{ Exception -> 0x00eb }
                int r2 = r2.length     // Catch:{ Exception -> 0x00eb }
                if (r10 >= r2) goto L_0x00dc
                im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00eb }
                short[] r2 = r2.recordSamples     // Catch:{ Exception -> 0x00eb }
                r2[r10] = r14     // Catch:{ Exception -> 0x00eb }
                float r11 = r11 + r12
                int r10 = r10 + 1
            L_0x00dc:
                int r13 = r13 + 1
                r3 = r16
                r2 = 0
                goto L_0x00b0
            L_0x00e2:
                r16 = r3
                im.bclpbkiauv.messenger.MediaController r2 = im.bclpbkiauv.messenger.MediaController.this     // Catch:{ Exception -> 0x00eb }
                long unused = r2.samplesCount = r7     // Catch:{ Exception -> 0x00eb }
                goto L_0x00f3
            L_0x00eb:
                r0 = move-exception
                goto L_0x00f0
            L_0x00ed:
                r0 = move-exception
                r16 = r3
            L_0x00f0:
                im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00f3:
                r2 = r16
                r3 = 0
                r2.position(r3)
                double r7 = (double) r4
                double r7 = r5 / r7
                r9 = 4611686018427387904(0x4000000000000000, double:2.0)
                double r7 = r7 / r9
                double r7 = java.lang.Math.sqrt(r7)
                r0 = r2
                int r9 = r2.capacity()
                if (r4 == r9) goto L_0x010b
                r3 = 1
            L_0x010b:
                if (r4 == 0) goto L_0x011b
                im.bclpbkiauv.messenger.MediaController r9 = im.bclpbkiauv.messenger.MediaController.this
                im.bclpbkiauv.messenger.DispatchQueue r9 = r9.fileEncodingQueue
                im.bclpbkiauv.messenger.-$$Lambda$MediaController$2$oUu_j144HKERgwGN6oE2fY89lJg r10 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$2$oUu_j144HKERgwGN6oE2fY89lJg
                r10.<init>(r0, r3)
                r9.postRunnable(r10)
            L_0x011b:
                im.bclpbkiauv.messenger.MediaController r9 = im.bclpbkiauv.messenger.MediaController.this
                im.bclpbkiauv.messenger.DispatchQueue r9 = r9.recordQueue
                im.bclpbkiauv.messenger.MediaController r10 = im.bclpbkiauv.messenger.MediaController.this
                java.lang.Runnable r10 = r10.recordRunnable
                r9.postRunnable(r10)
                im.bclpbkiauv.messenger.-$$Lambda$MediaController$2$rHKD0-niOYMO64B9KV0ZVRKT8Yk r9 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$2$rHKD0-niOYMO64B9KV0ZVRKT8Yk
                r9.<init>(r7)
                im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r9)
                goto L_0x015b
            L_0x0133:
                r2 = r3
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                java.util.ArrayList r0 = r0.recordBuffers
                r0.add(r2)
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                int r0 = r0.sendAfterDone
                r3 = 3
                if (r0 == r3) goto L_0x015b
                im.bclpbkiauv.messenger.MediaController r0 = im.bclpbkiauv.messenger.MediaController.this
                int r3 = r0.sendAfterDone
                im.bclpbkiauv.messenger.MediaController r5 = im.bclpbkiauv.messenger.MediaController.this
                boolean r5 = r5.sendAfterDoneNotify
                im.bclpbkiauv.messenger.MediaController r6 = im.bclpbkiauv.messenger.MediaController.this
                int r6 = r6.sendAfterDoneScheduleDate
                r0.stopRecordingInternal(r3, r5, r6)
            L_0x015b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.AnonymousClass2.run():void");
        }

        public /* synthetic */ void lambda$run$1$MediaController$2(ByteBuffer finalBuffer, boolean flush) {
            while (finalBuffer.hasRemaining()) {
                int oldLimit = -1;
                if (finalBuffer.remaining() > MediaController.this.fileBuffer.remaining()) {
                    oldLimit = finalBuffer.limit();
                    finalBuffer.limit(MediaController.this.fileBuffer.remaining() + finalBuffer.position());
                }
                MediaController.this.fileBuffer.put(finalBuffer);
                if (MediaController.this.fileBuffer.position() == MediaController.this.fileBuffer.limit() || flush) {
                    MediaController mediaController = MediaController.this;
                    if (mediaController.writeFrame(mediaController.fileBuffer, !flush ? MediaController.this.fileBuffer.limit() : finalBuffer.position()) != 0) {
                        MediaController.this.fileBuffer.rewind();
                        MediaController mediaController2 = MediaController.this;
                        long unused = mediaController2.recordTimeCount = mediaController2.recordTimeCount + ((long) ((MediaController.this.fileBuffer.limit() / 2) / 16));
                    }
                }
                if (oldLimit != -1) {
                    finalBuffer.limit(oldLimit);
                }
            }
            MediaController.this.recordQueue.postRunnable(new Runnable(finalBuffer) {
                private final /* synthetic */ ByteBuffer f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaController.AnonymousClass2.this.lambda$null$0$MediaController$2(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$MediaController$2(ByteBuffer finalBuffer) {
            MediaController.this.recordBuffers.add(finalBuffer);
        }

        public /* synthetic */ void lambda$run$2$MediaController$2(double amplitude) {
            NotificationCenter.getInstance(MediaController.this.recordingCurrentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Integer.valueOf(MediaController.this.recordingGuid), Long.valueOf(System.currentTimeMillis() - MediaController.this.recordStartTime), Double.valueOf(amplitude));
        }
    };
    /* access modifiers changed from: private */
    public short[] recordSamples = new short[1024];
    /* access modifiers changed from: private */
    public Runnable recordStartRunnable;
    /* access modifiers changed from: private */
    public long recordStartTime;
    /* access modifiers changed from: private */
    public long recordTimeCount;
    /* access modifiers changed from: private */
    public TLRPC.TL_document recordingAudio;
    private File recordingAudioFile;
    /* access modifiers changed from: private */
    public int recordingCurrentAccount;
    /* access modifiers changed from: private */
    public int recordingGuid = -1;
    private boolean resumeAudioOnFocusGain;
    /* access modifiers changed from: private */
    public long samplesCount;
    /* access modifiers changed from: private */
    public float seekToProgressPending;
    /* access modifiers changed from: private */
    public int sendAfterDone;
    /* access modifiers changed from: private */
    public boolean sendAfterDoneNotify;
    /* access modifiers changed from: private */
    public int sendAfterDoneScheduleDate;
    private SensorManager sensorManager;
    private boolean sensorsStarted;
    private Runnable setLoadingRunnable = new Runnable() {
        public void run() {
            if (MediaController.this.playingMessageObject != null) {
                FileLoader.getInstance(MediaController.this.playingMessageObject.currentAccount).setLoadingVideo(MediaController.this.playingMessageObject.getDocument(), true, false);
            }
        }
    };
    private ArrayList<MessageObject> shuffledPlaylist = new ArrayList<>();
    /* access modifiers changed from: private */
    public int startObserverToken;
    private StopMediaObserverRunnable stopMediaObserverRunnable;
    /* access modifiers changed from: private */
    public final Object sync = new Object();
    private long timeSinceRaise;
    private boolean useFrontSpeaker;
    private boolean videoConvertFirstWrite = true;
    private ArrayList<MessageObject> videoConvertQueue = new ArrayList<>();
    private final Object videoConvertSync = new Object();
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;
    private final Object videoQueueSync = new Object();
    private ArrayList<MessageObject> voiceMessagesPlaylist;
    private SparseArray<MessageObject> voiceMessagesPlaylistMap;
    private boolean voiceMessagesPlaylistUnread;

    public static class AudioEntry {
        public String author;
        public int duration;
        public String genre;
        public long id;
        public MessageObject messageObject;
        public String path;
        public String title;
    }

    public static class SavedFilterState {
        public float blurAngle;
        public float blurExcludeBlurSize;
        public Point blurExcludePoint;
        public float blurExcludeSize;
        public int blurType;
        public float contrastValue;
        public PhotoFilterView.CurvesToolValue curvesToolValue = new PhotoFilterView.CurvesToolValue();
        public float enhanceValue;
        public float exposureValue;
        public float fadeValue;
        public float grainValue;
        public float highlightsValue;
        public float saturationValue;
        public float shadowsValue;
        public float sharpenValue;
        public int tintHighlightsColor;
        public int tintShadowsColor;
        public float vignetteValue;
        public float warmthValue;
    }

    public static native int isOpusFile(String str);

    private native int startRecord(String str);

    private native void stopRecord();

    /* access modifiers changed from: private */
    public native int writeFrame(ByteBuffer byteBuffer, int i);

    public native byte[] getWaveform(String str);

    public native byte[] getWaveform2(short[] sArr, int i);

    private class AudioBuffer {
        ByteBuffer buffer;
        byte[] bufferBytes;
        int finished;
        long pcmOffset;
        int size;

        public AudioBuffer(int capacity) {
            this.buffer = ByteBuffer.allocateDirect(capacity);
            this.bufferBytes = new byte[capacity];
        }
    }

    static {
        String[] strArr = new String[6];
        strArr[0] = "_id";
        strArr[1] = "bucket_id";
        strArr[2] = "bucket_display_name";
        strArr[3] = "_data";
        String str = "date_modified";
        strArr[4] = Build.VERSION.SDK_INT > 28 ? str : "datetaken";
        strArr[5] = "orientation";
        projectionPhotos = strArr;
        String[] strArr2 = new String[6];
        strArr2[0] = "_id";
        strArr2[1] = "bucket_id";
        strArr2[2] = "bucket_display_name";
        strArr2[3] = "_data";
        if (Build.VERSION.SDK_INT <= 28) {
            str = "datetaken";
        }
        strArr2[4] = str;
        strArr2[5] = "duration";
        projectionVideo = strArr2;
    }

    public static class AlbumEntry {
        public int bucketId;
        public String bucketName;
        public PhotoEntry coverPhoto;
        public ArrayList<PhotoEntry> photos = new ArrayList<>();
        public SparseArray<PhotoEntry> photosByIds = new SparseArray<>();
        public boolean videoOnly;

        public AlbumEntry(int bucketId2, String bucketName2, PhotoEntry coverPhoto2) {
            this.bucketId = bucketId2;
            this.bucketName = bucketName2;
            this.coverPhoto = coverPhoto2;
        }

        public void addPhoto(PhotoEntry photoEntry) {
            this.photos.add(photoEntry);
            this.photosByIds.put(photoEntry.imageId, photoEntry);
        }
    }

    public static class PhotoEntry {
        public int bucketId;
        public boolean canDeleteAfter;
        public CharSequence caption;
        public long dateTaken;
        public int duration;
        public VideoEditedInfo editedInfo;
        public ArrayList<TLRPC.MessageEntity> entities;
        public int imageId;
        public String imagePath;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isMuted;
        public boolean isPainted;
        public boolean isVideo;
        public int orientation;
        public String path;
        public SavedFilterState savedFilterState;
        public ArrayList<TLRPC.InputDocument> stickers = new ArrayList<>();
        public String thumbPath;
        public int ttl;

        public PhotoEntry(int bucketId2, int imageId2, long dateTaken2, String path2, int orientation2, boolean isVideo2) {
            this.bucketId = bucketId2;
            this.imageId = imageId2;
            this.dateTaken = dateTaken2;
            this.path = path2;
            if (isVideo2) {
                this.duration = orientation2;
            } else {
                this.orientation = orientation2;
            }
            this.isVideo = isVideo2;
        }

        public void reset() {
            this.isFiltered = false;
            this.isPainted = false;
            this.isCropped = false;
            this.ttl = 0;
            this.imagePath = null;
            if (!this.isVideo) {
                this.thumbPath = null;
            }
            this.editedInfo = null;
            this.caption = null;
            this.entities = null;
            this.savedFilterState = null;
            this.stickers.clear();
        }

        public String toString() {
            return "PhotoEntry{bucketId=" + this.bucketId + ", imageId=" + this.imageId + ", dateTaken=" + this.dateTaken + ", duration=" + this.duration + ", path='" + this.path + '\'' + ", orientation=" + this.orientation + ", thumbPath='" + this.thumbPath + '\'' + ", imagePath='" + this.imagePath + '\'' + ", editedInfo=" + this.editedInfo + ", isVideo=" + this.isVideo + ", caption=" + this.caption + ", entities=" + this.entities + ", isFiltered=" + this.isFiltered + ", isPainted=" + this.isPainted + ", isCropped=" + this.isCropped + ", isMuted=" + this.isMuted + ", ttl=" + this.ttl + ", canDeleteAfter=" + this.canDeleteAfter + ", savedFilterState=" + this.savedFilterState + ", stickers=" + this.stickers + '}';
        }
    }

    public static class SearchImage {
        public CharSequence caption;
        public int date;
        public TLRPC.Document document;
        public ArrayList<TLRPC.MessageEntity> entities;
        public int height;
        public String id;
        public String imagePath;
        public String imageUrl;
        public TLRPC.BotInlineResult inlineResult;
        public boolean isCropped;
        public boolean isFiltered;
        public boolean isPainted;
        public HashMap<String, String> params;
        public TLRPC.Photo photo;
        public TLRPC.PhotoSize photoSize;
        public SavedFilterState savedFilterState;
        public int size;
        public ArrayList<TLRPC.InputDocument> stickers = new ArrayList<>();
        public String thumbPath;
        public TLRPC.PhotoSize thumbPhotoSize;
        public String thumbUrl;
        public int ttl;
        public int type;
        public int width;

        public void reset() {
            this.isFiltered = false;
            this.isPainted = false;
            this.isCropped = false;
            this.ttl = 0;
            this.imagePath = null;
            this.thumbPath = null;
            this.caption = null;
            this.entities = null;
            this.savedFilterState = null;
            this.stickers.clear();
        }

        public String getAttachName() {
            TLRPC.PhotoSize photoSize2 = this.photoSize;
            if (photoSize2 != null) {
                return FileLoader.getAttachFileName(photoSize2);
            }
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return FileLoader.getAttachFileName(document2);
            }
            return Utilities.MD5(this.imageUrl) + "." + ImageLoader.getHttpUrlExtension(this.imageUrl, "jpg");
        }

        public String getPathToAttach() {
            TLRPC.PhotoSize photoSize2 = this.photoSize;
            if (photoSize2 != null) {
                return FileLoader.getPathToAttach(photoSize2, true).getAbsolutePath();
            }
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return FileLoader.getPathToAttach(document2, true).getAbsolutePath();
            }
            return this.imageUrl;
        }
    }

    private class InternalObserver extends ContentObserver {
        public InternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }
    }

    private class ExternalObserver extends ContentObserver {
        public ExternalObserver() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            MediaController.this.processMediaObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
    }

    private class GalleryObserverInternal extends ContentObserver {
        public GalleryObserverInternal() {
            super((Handler) null);
        }

        private void scheduleReloadRunnable() {
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = new Runnable() {
                public final void run() {
                    MediaController.GalleryObserverInternal.this.lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal();
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public /* synthetic */ void lambda$scheduleReloadRunnable$0$MediaController$GalleryObserverInternal() {
            if (PhotoViewer.getInstance().isVisible()) {
                scheduleReloadRunnable();
                return;
            }
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            scheduleReloadRunnable();
        }
    }

    private class GalleryObserverExternal extends ContentObserver {
        public GalleryObserverExternal() {
            super((Handler) null);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (MediaController.refreshGalleryRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(MediaController.refreshGalleryRunnable);
            }
            AndroidUtilities.runOnUIThread(MediaController.refreshGalleryRunnable = $$Lambda$MediaController$GalleryObserverExternal$QsD9VhHDdid_ljGRGX23BRiyEU.INSTANCE, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        static /* synthetic */ void lambda$onChange$0() {
            Runnable unused = MediaController.refreshGalleryRunnable = null;
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public static void checkGallery() {
        AlbumEntry albumEntry;
        if (Build.VERSION.SDK_INT >= 24 && (albumEntry = allPhotosAlbumEntry) != null) {
            Utilities.globalQueue.postRunnable(new Runnable(albumEntry.photos.size()) {
                private final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    MediaController.lambda$checkGallery$0(this.f$0);
                }
            }, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0030, code lost:
        if (r3 != null) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0032, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003a, code lost:
        if (r3 == null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0043, code lost:
        if (im.bclpbkiauv.messenger.ApplicationLoader.applicationContext.checkSelfPermission(im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE) != 0) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
        r3 = android.provider.MediaStore.Images.Media.query(im.bclpbkiauv.messenger.ApplicationLoader.applicationContext.getContentResolver(), android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new java.lang.String[]{"COUNT(_id)"}, (java.lang.String) null, (java.lang.String[]) null, (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0059, code lost:
        if (r3 == null) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005f, code lost:
        if (r3.moveToNext() == false) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0065, code lost:
        r2 = r2 + r3.getInt(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0066, code lost:
        if (r3 == null) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0068, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        im.bclpbkiauv.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0070, code lost:
        if (r3 == null) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0073, code lost:
        if (r12 == r2) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0075, code lost:
        r0 = refreshGalleryRunnable;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0077, code lost:
        if (r0 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0079, code lost:
        im.bclpbkiauv.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        refreshGalleryRunnable = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x007f, code lost:
        loadGalleryPhotosAlbums(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0083, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0084, code lost:
        if (r3 != null) goto L_0x0086;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0086, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0089, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$checkGallery$0(int r12) {
        /*
            java.lang.String r0 = "COUNT(_id)"
            java.lang.String r1 = "android.permission.READ_EXTERNAL_STORAGE"
            r2 = 0
            r3 = 0
            r4 = 0
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0036 }
            int r5 = r5.checkSelfPermission(r1)     // Catch:{ all -> 0x0036 }
            if (r5 != 0) goto L_0x0030
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0036 }
            android.content.ContentResolver r6 = r5.getContentResolver()     // Catch:{ all -> 0x0036 }
            android.net.Uri r7 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x0036 }
            java.lang.String[] r8 = new java.lang.String[]{r0}     // Catch:{ all -> 0x0036 }
            r9 = 0
            r10 = 0
            r11 = 0
            android.database.Cursor r5 = android.provider.MediaStore.Images.Media.query(r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0036 }
            r3 = r5
            if (r3 == 0) goto L_0x0030
            boolean r5 = r3.moveToNext()     // Catch:{ all -> 0x0036 }
            if (r5 == 0) goto L_0x0030
            int r5 = r3.getInt(r4)     // Catch:{ all -> 0x0036 }
            int r2 = r2 + r5
        L_0x0030:
            if (r3 == 0) goto L_0x003d
        L_0x0032:
            r3.close()
            goto L_0x003d
        L_0x0036:
            r5 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x008a }
            if (r3 == 0) goto L_0x003d
            goto L_0x0032
        L_0x003d:
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x006c }
            int r1 = r5.checkSelfPermission(r1)     // Catch:{ all -> 0x006c }
            if (r1 != 0) goto L_0x0066
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x006c }
            android.content.ContentResolver r5 = r1.getContentResolver()     // Catch:{ all -> 0x006c }
            android.net.Uri r6 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x006c }
            java.lang.String[] r7 = new java.lang.String[]{r0}     // Catch:{ all -> 0x006c }
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x006c }
            r3 = r0
            if (r3 == 0) goto L_0x0066
            boolean r0 = r3.moveToNext()     // Catch:{ all -> 0x006c }
            if (r0 == 0) goto L_0x0066
            int r0 = r3.getInt(r4)     // Catch:{ all -> 0x006c }
            int r2 = r2 + r0
        L_0x0066:
            if (r3 == 0) goto L_0x0073
        L_0x0068:
            r3.close()
            goto L_0x0073
        L_0x006c:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0073
            goto L_0x0068
        L_0x0073:
            if (r12 == r2) goto L_0x0082
            java.lang.Runnable r0 = refreshGalleryRunnable
            if (r0 == 0) goto L_0x007f
            im.bclpbkiauv.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            r0 = 0
            refreshGalleryRunnable = r0
        L_0x007f:
            loadGalleryPhotosAlbums(r4)
        L_0x0082:
            return
        L_0x0083:
            r0 = move-exception
            if (r3 == 0) goto L_0x0089
            r3.close()
        L_0x0089:
            throw r0
        L_0x008a:
            r0 = move-exception
            if (r3 == 0) goto L_0x0090
            r3.close()
        L_0x0090:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.lambda$checkGallery$0(int):void");
    }

    private final class StopMediaObserverRunnable implements Runnable {
        public int currentObserverToken;

        private StopMediaObserverRunnable() {
            this.currentObserverToken = 0;
        }

        public void run() {
            if (this.currentObserverToken == MediaController.this.startObserverToken) {
                try {
                    if (MediaController.this.internalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.internalObserver);
                        InternalObserver unused = MediaController.this.internalObserver = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                try {
                    if (MediaController.this.externalObserver != null) {
                        ApplicationLoader.applicationContext.getContentResolver().unregisterContentObserver(MediaController.this.externalObserver);
                        ExternalObserver unused2 = MediaController.this.externalObserver = null;
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        }
    }

    public static MediaController getInstance() {
        MediaController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    MediaController mediaController = new MediaController();
                    localInstance = mediaController;
                    Instance = mediaController;
                }
            }
        }
        return localInstance;
    }

    public MediaController() {
        DispatchQueue dispatchQueue = new DispatchQueue("recordQueue");
        this.recordQueue = dispatchQueue;
        dispatchQueue.setPriority(10);
        DispatchQueue dispatchQueue2 = new DispatchQueue("fileEncodingQueue");
        this.fileEncodingQueue = dispatchQueue2;
        dispatchQueue2.setPriority(10);
        this.recordQueue.postRunnable(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$1$MediaController();
            }
        });
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$2$MediaController();
            }
        });
        this.fileBuffer = ByteBuffer.allocateDirect(1920);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                MediaController.this.lambda$new$3$MediaController();
            }
        });
        String[] strArr = new String[7];
        strArr[0] = "_data";
        strArr[1] = "_display_name";
        strArr[2] = "bucket_display_name";
        strArr[3] = Build.VERSION.SDK_INT > 28 ? "date_modified" : "datetaken";
        strArr[4] = "title";
        strArr[5] = "width";
        strArr[6] = "height";
        this.mediaProjections = strArr;
        ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, new GalleryObserverExternal());
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        try {
            contentResolver.registerContentObserver(MediaStore.Video.Media.INTERNAL_CONTENT_URI, true, new GalleryObserverInternal());
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
    }

    public /* synthetic */ void lambda$new$1$MediaController() {
        try {
            int minBufferSize = AudioRecord.getMinBufferSize(AudioEditConstant.ExportSampleRate, 16, 2);
            this.recordBufferSize = minBufferSize;
            if (minBufferSize <= 0) {
                this.recordBufferSize = 1280;
            }
            for (int a = 0; a < 5; a++) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                buffer.order(ByteOrder.nativeOrder());
                this.recordBuffers.add(buffer);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$new$2$MediaController() {
        try {
            this.currentPlaybackSpeed = MessagesController.getGlobalMainSettings().getFloat("playbackSpeed", 1.0f);
            SensorManager sensorManager2 = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
            this.sensorManager = sensorManager2;
            this.linearSensor = sensorManager2.getDefaultSensor(10);
            Sensor defaultSensor = this.sensorManager.getDefaultSensor(9);
            this.gravitySensor = defaultSensor;
            if (this.linearSensor == null || defaultSensor == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("gravity or linear sensor not found");
                }
                this.accelerometerSensor = this.sensorManager.getDefaultSensor(1);
                this.linearSensor = null;
                this.gravitySensor = null;
            }
            this.proximitySensor = this.sensorManager.getDefaultSensor(8);
            this.proximityWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(32, "proximity");
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    AndroidUtilities.runOnUIThread(new Runnable(state) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MediaController.AnonymousClass3.this.lambda$onCallStateChanged$0$MediaController$3(this.f$1);
                        }
                    });
                }

                public /* synthetic */ void lambda$onCallStateChanged$0$MediaController$3(int state) {
                    if (state == 1) {
                        MediaController mediaController = MediaController.this;
                        if (mediaController.isPlayingMessage(mediaController.playingMessageObject) && !MediaController.this.isMessagePaused()) {
                            MediaController mediaController2 = MediaController.this;
                            mediaController2.lambda$startAudioAgain$5$MediaController(mediaController2.playingMessageObject);
                        } else if (!(MediaController.this.recordStartRunnable == null && MediaController.this.recordingAudio == null)) {
                            MediaController.this.stopRecording(2, false, 0);
                        }
                        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.getInstance();
                        if (embedBottomSheet != null) {
                            embedBottomSheet.pause();
                        }
                        boolean unused = MediaController.this.callInProgress = true;
                    } else if (state == 0) {
                        boolean unused2 = MediaController.this.callInProgress = false;
                    } else if (state == 2) {
                        EmbedBottomSheet embedBottomSheet2 = EmbedBottomSheet.getInstance();
                        if (embedBottomSheet2 != null) {
                            embedBottomSheet2.pause();
                        }
                        boolean unused3 = MediaController.this.callInProgress = true;
                    }
                }
            };
            TelephonyManager mgr = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (mgr != null) {
                mgr.listen(phoneStateListener, 32);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    public /* synthetic */ void lambda$new$3$MediaController() {
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.didReceiveNewMessages);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.removeAllMessagesFromDialog);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.musicDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == -1) {
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
            }
            this.hasAudioFocus = 0;
            this.audioFocus = 0;
        } else if (focusChange == 1) {
            this.audioFocus = 2;
            if (this.resumeAudioOnFocusGain) {
                this.resumeAudioOnFocusGain = false;
                if (isPlayingMessage(getPlayingMessageObject()) && isMessagePaused()) {
                    playMessage(getPlayingMessageObject());
                }
            }
        } else if (focusChange == -3) {
            this.audioFocus = 1;
        } else if (focusChange == -2) {
            this.audioFocus = 0;
            if (isPlayingMessage(getPlayingMessageObject()) && !isMessagePaused()) {
                lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
                this.resumeAudioOnFocusGain = true;
            }
        }
        setPlayerVolume();
    }

    private void setPlayerVolume() {
        float volume;
        try {
            if (this.audioFocus != 1) {
                volume = 1.0f;
            } else {
                volume = VOLUME_DUCK;
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(volume);
            } else if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void startProgressTimer(final MessageObject currentPlayingMessageObject) {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            String fileName = currentPlayingMessageObject.getFileName();
            Timer timer = new Timer();
            this.progressTimer = timer;
            timer.schedule(new TimerTask() {
                public void run() {
                    synchronized (MediaController.this.sync) {
                        AndroidUtilities.runOnUIThread(new Runnable(currentPlayingMessageObject) {
                            private final /* synthetic */ MessageObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                MediaController.AnonymousClass4.this.lambda$run$0$MediaController$4(this.f$1);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$run$0$MediaController$4(MessageObject currentPlayingMessageObject) {
                    long progress;
                    long duration;
                    float value;
                    if (currentPlayingMessageObject == null) {
                        return;
                    }
                    if (!(MediaController.this.audioPlayer == null && MediaController.this.videoPlayer == null) && !MediaController.this.isPaused) {
                        try {
                            float value2 = 0.0f;
                            if (MediaController.this.videoPlayer != null) {
                                duration = MediaController.this.videoPlayer.getDuration();
                                progress = MediaController.this.videoPlayer.getCurrentPosition();
                                if (progress < 0) {
                                    return;
                                }
                                if (duration > 0) {
                                    value = ((float) MediaController.this.videoPlayer.getBufferedPosition()) / ((float) duration);
                                    if (duration >= 0) {
                                        value2 = ((float) progress) / ((float) duration);
                                    }
                                    if (value2 >= 1.0f) {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else {
                                duration = MediaController.this.audioPlayer.getDuration();
                                progress = MediaController.this.audioPlayer.getCurrentPosition();
                                float value3 = duration >= 0 ? ((float) progress) / ((float) duration) : 0.0f;
                                float bufferedValue = ((float) MediaController.this.audioPlayer.getBufferedPosition()) / ((float) duration);
                                if (duration != C.TIME_UNSET && progress >= 0) {
                                    if (MediaController.this.seekToProgressPending == 0.0f) {
                                        value2 = value3;
                                        value = bufferedValue;
                                    } else {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            }
                            long unused = MediaController.this.lastProgress = progress;
                            currentPlayingMessageObject.audioPlayerDuration = (int) (duration / 1000);
                            currentPlayingMessageObject.audioProgress = value2;
                            currentPlayingMessageObject.audioProgressSec = (int) (MediaController.this.lastProgress / 1000);
                            currentPlayingMessageObject.bufferedProgress = value;
                            NotificationCenter.getInstance(currentPlayingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(currentPlayingMessageObject.getId()), Float.valueOf(value2));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }
            }, 0, 17);
        }
    }

    private void stopProgressTimer() {
        synchronized (this.progressTimerSync) {
            if (this.progressTimer != null) {
                try {
                    this.progressTimer.cancel();
                    this.progressTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    public void cleanup() {
        cleanupPlayer(false, true);
        this.audioInfo = null;
        this.playMusicAgain = false;
        for (int a = 0; a < 3; a++) {
            DownloadController.getInstance(a).cleanup();
        }
        this.videoConvertQueue.clear();
        this.playlist.clear();
        this.shuffledPlaylist.clear();
        this.generatingWaveform.clear();
        this.voiceMessagesPlaylist = null;
        this.voiceMessagesPlaylistMap = null;
        cancelVideoConvert((MessageObject) null);
    }

    public void startMediaObserver() {
        ApplicationLoader.applicationHandler.removeCallbacks(this.stopMediaObserverRunnable);
        this.startObserverToken++;
        try {
            if (this.internalObserver == null) {
                ContentResolver contentResolver = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ExternalObserver externalObserver2 = new ExternalObserver();
                this.externalObserver = externalObserver2;
                contentResolver.registerContentObserver(uri, false, externalObserver2);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            if (this.externalObserver == null) {
                ContentResolver contentResolver2 = ApplicationLoader.applicationContext.getContentResolver();
                Uri uri2 = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                InternalObserver internalObserver2 = new InternalObserver();
                this.internalObserver = internalObserver2;
                contentResolver2.registerContentObserver(uri2, false, internalObserver2);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    public void stopMediaObserver() {
        if (this.stopMediaObserverRunnable == null) {
            this.stopMediaObserverRunnable = new StopMediaObserverRunnable();
        }
        this.stopMediaObserverRunnable.currentObserverToken = this.startObserverToken;
        ApplicationLoader.applicationHandler.postDelayed(this.stopMediaObserverRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x005c, code lost:
        if (r6.toLowerCase().contains("screenshot") == false) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0068, code lost:
        if (r7.toLowerCase().contains("screenshot") != false) goto L_0x0082;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0074, code lost:
        if (r8.toLowerCase().contains("screenshot") != false) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processMediaObserver(android.net.Uri r17) {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            android.graphics.Point r0 = im.bclpbkiauv.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ Exception -> 0x00da }
            r3 = r0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00da }
            android.content.ContentResolver r4 = r0.getContentResolver()     // Catch:{ Exception -> 0x00da }
            java.lang.String[] r6 = r1.mediaProjections     // Catch:{ Exception -> 0x00da }
            r7 = 0
            r8 = 0
            java.lang.String r9 = "date_added DESC LIMIT 1"
            r5 = r17
            android.database.Cursor r0 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x00da }
            r2 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x00da }
            r0.<init>()     // Catch:{ Exception -> 0x00da }
            r4 = r0
            if (r2 == 0) goto L_0x00bf
        L_0x0023:
            boolean r0 = r2.moveToNext()     // Catch:{ Exception -> 0x00da }
            if (r0 == 0) goto L_0x00bc
            java.lang.String r0 = ""
            r5 = r0
            r0 = 0
            java.lang.String r0 = r2.getString(r0)     // Catch:{ Exception -> 0x00da }
            r6 = r0
            r0 = 1
            java.lang.String r7 = r2.getString(r0)     // Catch:{ Exception -> 0x00da }
            r8 = 2
            java.lang.String r8 = r2.getString(r8)     // Catch:{ Exception -> 0x00da }
            r9 = 3
            long r9 = r2.getLong(r9)     // Catch:{ Exception -> 0x00da }
            r11 = 4
            java.lang.String r11 = r2.getString(r11)     // Catch:{ Exception -> 0x00da }
            r12 = 5
            int r12 = r2.getInt(r12)     // Catch:{ Exception -> 0x00da }
            r13 = 6
            int r13 = r2.getInt(r13)     // Catch:{ Exception -> 0x00da }
            java.lang.String r14 = "screenshot"
            if (r6 == 0) goto L_0x005e
            java.lang.String r15 = r6.toLowerCase()     // Catch:{ Exception -> 0x00da }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00da }
            if (r15 != 0) goto L_0x0082
        L_0x005e:
            if (r7 == 0) goto L_0x006a
            java.lang.String r15 = r7.toLowerCase()     // Catch:{ Exception -> 0x00da }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00da }
            if (r15 != 0) goto L_0x0082
        L_0x006a:
            if (r8 == 0) goto L_0x0076
            java.lang.String r15 = r8.toLowerCase()     // Catch:{ Exception -> 0x00da }
            boolean r15 = r15.contains(r14)     // Catch:{ Exception -> 0x00da }
            if (r15 != 0) goto L_0x0082
        L_0x0076:
            if (r11 == 0) goto L_0x00ba
            java.lang.String r15 = r11.toLowerCase()     // Catch:{ Exception -> 0x00da }
            boolean r14 = r15.contains(r14)     // Catch:{ Exception -> 0x00da }
            if (r14 == 0) goto L_0x00ba
        L_0x0082:
            if (r12 == 0) goto L_0x0086
            if (r13 != 0) goto L_0x0096
        L_0x0086:
            android.graphics.BitmapFactory$Options r14 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00b2 }
            r14.<init>()     // Catch:{ Exception -> 0x00b2 }
            r14.inJustDecodeBounds = r0     // Catch:{ Exception -> 0x00b2 }
            android.graphics.BitmapFactory.decodeFile(r6, r14)     // Catch:{ Exception -> 0x00b2 }
            int r0 = r14.outWidth     // Catch:{ Exception -> 0x00b2 }
            r12 = r0
            int r0 = r14.outHeight     // Catch:{ Exception -> 0x00b2 }
            r13 = r0
        L_0x0096:
            if (r12 <= 0) goto L_0x00aa
            if (r13 <= 0) goto L_0x00aa
            int r0 = r3.x     // Catch:{ Exception -> 0x00b2 }
            if (r12 != r0) goto L_0x00a2
            int r0 = r3.y     // Catch:{ Exception -> 0x00b2 }
            if (r13 == r0) goto L_0x00aa
        L_0x00a2:
            int r0 = r3.x     // Catch:{ Exception -> 0x00b2 }
            if (r13 != r0) goto L_0x00b1
            int r0 = r3.y     // Catch:{ Exception -> 0x00b2 }
            if (r12 != r0) goto L_0x00b1
        L_0x00aa:
            java.lang.Long r0 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x00b2 }
            r4.add(r0)     // Catch:{ Exception -> 0x00b2 }
        L_0x00b1:
            goto L_0x00ba
        L_0x00b2:
            r0 = move-exception
            java.lang.Long r14 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x00da }
            r4.add(r14)     // Catch:{ Exception -> 0x00da }
        L_0x00ba:
            goto L_0x0023
        L_0x00bc:
            r2.close()     // Catch:{ Exception -> 0x00da }
        L_0x00bf:
            boolean r0 = r4.isEmpty()     // Catch:{ Exception -> 0x00da }
            if (r0 != 0) goto L_0x00cd
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$APBpCdbXCsX-Lxw4nQfuh-pdduA r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$APBpCdbXCsX-Lxw4nQfuh-pdduA     // Catch:{ Exception -> 0x00da }
            r0.<init>(r4)     // Catch:{ Exception -> 0x00da }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x00da }
        L_0x00cd:
            if (r2 == 0) goto L_0x00d5
            r2.close()     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00d5
        L_0x00d3:
            r0 = move-exception
            goto L_0x00e4
        L_0x00d5:
            goto L_0x00e4
        L_0x00d6:
            r0 = move-exception
            r3 = r2
            r2 = r0
            goto L_0x00e5
        L_0x00da:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x00d6 }
            if (r2 == 0) goto L_0x00d5
            r2.close()     // Catch:{ Exception -> 0x00d3 }
            goto L_0x00d5
        L_0x00e4:
            return
        L_0x00e5:
            if (r3 == 0) goto L_0x00ed
            r3.close()     // Catch:{ Exception -> 0x00eb }
            goto L_0x00ed
        L_0x00eb:
            r0 = move-exception
            goto L_0x00ee
        L_0x00ed:
        L_0x00ee:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.processMediaObserver(android.net.Uri):void");
    }

    public /* synthetic */ void lambda$processMediaObserver$4$MediaController(ArrayList screenshotDates) {
        NotificationCenter.getInstance(this.lastChatAccount).postNotificationName(NotificationCenter.screenshotTook, new Object[0]);
        checkScreenshots(screenshotDates);
    }

    private void checkScreenshots(ArrayList<Long> dates) {
        if (dates != null && !dates.isEmpty() && this.lastChatEnterTime != 0) {
            if (this.lastUser != null || (this.lastSecretChat instanceof TLRPC.TL_encryptedChat)) {
                boolean send = false;
                for (int a = 0; a < dates.size(); a++) {
                    Long date = dates.get(a);
                    if ((this.lastMediaCheckTime == 0 || date.longValue() > this.lastMediaCheckTime) && date.longValue() >= this.lastChatEnterTime && (this.lastChatLeaveTime == 0 || date.longValue() <= this.lastChatLeaveTime + AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                        this.lastMediaCheckTime = Math.max(this.lastMediaCheckTime, date.longValue());
                        send = true;
                    }
                }
                if (!send) {
                    return;
                }
                if (this.lastSecretChat != null) {
                    SecretChatHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastSecretChat, this.lastChatVisibleMessages, (TLRPC.Message) null);
                } else {
                    SendMessagesHelper.getInstance(this.lastChatAccount).sendScreenshotMessage(this.lastUser, this.lastMessageId, (TLRPC.Message) null);
                }
            }
        }
    }

    public void setLastVisibleMessageIds(int account, long enterTime, long leaveTime, TLRPC.User user, TLRPC.EncryptedChat encryptedChat, ArrayList<Long> visibleMessages, int visibleMessage) {
        this.lastChatEnterTime = enterTime;
        this.lastChatLeaveTime = leaveTime;
        this.lastChatAccount = account;
        this.lastSecretChat = encryptedChat;
        this.lastUser = user;
        this.lastMessageId = visibleMessage;
        this.lastChatVisibleMessages = visibleMessages;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        MessageObject messageObject;
        ArrayList<MessageObject> arrayList;
        if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.httpFileDidLoad) {
            String fileName = args[0];
            if (this.downloadingCurrentMessage && (messageObject = this.playingMessageObject) != null && messageObject.currentAccount == account && FileLoader.getAttachFileName(this.playingMessageObject.getDocument()).equals(fileName)) {
                this.playMusicAgain = true;
                playMessage(this.playingMessageObject);
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            if (!args[2].booleanValue()) {
                int channelId = args[1].intValue();
                ArrayList<Integer> markAsDeletedMessages = args[0];
                MessageObject messageObject2 = this.playingMessageObject;
                if (messageObject2 != null && channelId == messageObject2.messageOwner.to_id.channel_id && markAsDeletedMessages.contains(Integer.valueOf(this.playingMessageObject.getId()))) {
                    cleanupPlayer(true, true);
                }
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 != null && !arrayList2.isEmpty() && channelId == this.voiceMessagesPlaylist.get(0).messageOwner.to_id.channel_id) {
                    for (int a = 0; a < markAsDeletedMessages.size(); a++) {
                        Integer key = markAsDeletedMessages.get(a);
                        MessageObject messageObject3 = this.voiceMessagesPlaylistMap.get(key.intValue());
                        this.voiceMessagesPlaylistMap.remove(key.intValue());
                        if (messageObject3 != null) {
                            this.voiceMessagesPlaylist.remove(messageObject3);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.removeAllMessagesFromDialog) {
            long did = args[0].longValue();
            MessageObject messageObject4 = this.playingMessageObject;
            if (messageObject4 != null && messageObject4.getDialogId() == did) {
                cleanupPlayer(false, true);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            long did2 = args[0].longValue();
            MessageObject messageObject5 = this.playingMessageObject;
            if (messageObject5 != null && messageObject5.isMusic() && this.playingMessageObject.getDialogId() == did2 && !this.playingMessageObject.scheduled) {
                ArrayList<MessageObject> arrayList3 = args[1];
                this.playlist.addAll(0, arrayList3);
                if (SharedConfig.shuffleMusic) {
                    buildShuffledPlayList();
                    this.currentPlaylistNum = 0;
                    return;
                }
                this.currentPlaylistNum += arrayList3.size();
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue() && (arrayList = this.voiceMessagesPlaylist) != null && !arrayList.isEmpty() && args[0].longValue() == this.voiceMessagesPlaylist.get(0).getDialogId()) {
                ArrayList<MessageObject> arr = args[1];
                for (int a2 = 0; a2 < arr.size(); a2++) {
                    MessageObject messageObject6 = arr.get(a2);
                    if ((messageObject6.isVoice() || messageObject6.isRoundVideo()) && (!this.voiceMessagesPlaylistUnread || (messageObject6.isContentUnread() && !messageObject6.isOut()))) {
                        this.voiceMessagesPlaylist.add(messageObject6);
                        this.voiceMessagesPlaylistMap.put(messageObject6.getId(), messageObject6);
                    }
                }
            }
        } else if (id == NotificationCenter.playerDidStartPlaying) {
            if (!getInstance().isCurrentPlayer(args[0])) {
                getInstance().lambda$startAudioAgain$5$MediaController(getInstance().getPlayingMessageObject());
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRecordingAudio() {
        return (this.recordStartRunnable == null && this.recordingAudio == null) ? false : true;
    }

    private boolean isNearToSensor(float value) {
        return value < 5.0f && value != this.proximitySensor.getMaximumRange();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r1.playingMessageObject;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isRecordingOrListeningByProximity() {
        /*
            r1 = this;
            boolean r0 = r1.proximityTouched
            if (r0 == 0) goto L_0x001e
            boolean r0 = r1.isRecordingAudio()
            if (r0 != 0) goto L_0x001c
            im.bclpbkiauv.messenger.MessageObject r0 = r1.playingMessageObject
            if (r0 == 0) goto L_0x001e
            boolean r0 = r0.isVoice()
            if (r0 != 0) goto L_0x001c
            im.bclpbkiauv.messenger.MessageObject r0 = r1.playingMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x001e
        L_0x001c:
            r0 = 1
            goto L_0x001f
        L_0x001e:
            r0 = 0
        L_0x001f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.isRecordingOrListeningByProximity():boolean");
    }

    public void onSensorChanged(SensorEvent event) {
        PowerManager.WakeLock wakeLock;
        PowerManager.WakeLock wakeLock2;
        PowerManager.WakeLock wakeLock3;
        PowerManager.WakeLock wakeLock4;
        PowerManager.WakeLock wakeLock5;
        int sign;
        boolean goodValue;
        int i;
        SensorEvent sensorEvent = event;
        if (this.sensorsStarted && VoIPService.getSharedInstance() == null) {
            if (sensorEvent.sensor == this.proximitySensor) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("proximity changed to " + sensorEvent.values[0] + " max value = " + this.proximitySensor.getMaximumRange());
                }
                float f = this.lastProximityValue;
                if (f == -100.0f) {
                    this.lastProximityValue = sensorEvent.values[0];
                } else if (f != sensorEvent.values[0]) {
                    this.proximityHasDifferentValues = true;
                }
                if (this.proximityHasDifferentValues) {
                    this.proximityTouched = isNearToSensor(sensorEvent.values[0]);
                }
            } else if (sensorEvent.sensor == this.accelerometerSensor) {
                double alpha = this.lastTimestamp == 0 ? 0.9800000190734863d : 1.0d / ((((double) (sensorEvent.timestamp - this.lastTimestamp)) / 1.0E9d) + 1.0d);
                this.lastTimestamp = sensorEvent.timestamp;
                float[] fArr = this.gravity;
                fArr[0] = (float) ((((double) fArr[0]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[0])));
                float[] fArr2 = this.gravity;
                fArr2[1] = (float) ((((double) fArr2[1]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[1])));
                float[] fArr3 = this.gravity;
                fArr3[2] = (float) ((((double) fArr3[2]) * alpha) + ((1.0d - alpha) * ((double) sensorEvent.values[2])));
                this.gravityFast[0] = (this.gravity[0] * 0.8f) + (sensorEvent.values[0] * 0.19999999f);
                this.gravityFast[1] = (this.gravity[1] * 0.8f) + (sensorEvent.values[1] * 0.19999999f);
                this.gravityFast[2] = (this.gravity[2] * 0.8f) + (sensorEvent.values[2] * 0.19999999f);
                this.linearAcceleration[0] = sensorEvent.values[0] - this.gravity[0];
                this.linearAcceleration[1] = sensorEvent.values[1] - this.gravity[1];
                this.linearAcceleration[2] = sensorEvent.values[2] - this.gravity[2];
            } else if (sensorEvent.sensor == this.linearSensor) {
                this.linearAcceleration[0] = sensorEvent.values[0];
                this.linearAcceleration[1] = sensorEvent.values[1];
                this.linearAcceleration[2] = sensorEvent.values[2];
            } else if (sensorEvent.sensor == this.gravitySensor) {
                float[] fArr4 = this.gravityFast;
                float[] fArr5 = this.gravity;
                float f2 = sensorEvent.values[0];
                fArr5[0] = f2;
                fArr4[0] = f2;
                float[] fArr6 = this.gravityFast;
                float[] fArr7 = this.gravity;
                float f3 = sensorEvent.values[1];
                fArr7[1] = f3;
                fArr6[1] = f3;
                float[] fArr8 = this.gravityFast;
                float[] fArr9 = this.gravity;
                float f4 = sensorEvent.values[2];
                fArr9[2] = f4;
                fArr8[2] = f4;
            }
            if (sensorEvent.sensor == this.linearSensor || sensorEvent.sensor == this.gravitySensor || sensorEvent.sensor == this.accelerometerSensor) {
                float[] fArr10 = this.gravity;
                float f5 = fArr10[0];
                float[] fArr11 = this.linearAcceleration;
                float val = (f5 * fArr11[0]) + (fArr10[1] * fArr11[1]) + (fArr10[2] * fArr11[2]);
                if (this.raisedToBack != 6 && ((val > 0.0f && this.previousAccValue > 0.0f) || (val < 0.0f && this.previousAccValue < 0.0f))) {
                    if (val > 0.0f) {
                        goodValue = val > 15.0f;
                        sign = 1;
                    } else {
                        goodValue = val < -15.0f;
                        sign = 2;
                    }
                    int i2 = this.raisedToTopSign;
                    if (i2 == 0 || i2 == sign) {
                        if (goodValue && this.raisedToBack == 0 && ((i = this.raisedToTopSign) == 0 || i == sign)) {
                            int i3 = this.raisedToTop;
                            if (i3 < 6 && !this.proximityTouched) {
                                this.raisedToTopSign = sign;
                                int i4 = i3 + 1;
                                this.raisedToTop = i4;
                                if (i4 == 6) {
                                    this.countLess = 0;
                                }
                            }
                        } else {
                            if (!goodValue) {
                                this.countLess++;
                            }
                            if (!(this.raisedToTopSign == sign && this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                                this.raisedToBack = 0;
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                            }
                        }
                    } else if (this.raisedToTop != 6 || !goodValue) {
                        if (!goodValue) {
                            this.countLess++;
                        }
                        if (!(this.countLess != 10 && this.raisedToTop == 6 && this.raisedToBack == 0)) {
                            this.raisedToTop = 0;
                            this.raisedToTopSign = 0;
                            this.raisedToBack = 0;
                            this.countLess = 0;
                        }
                    } else {
                        int i5 = this.raisedToBack;
                        if (i5 < 6) {
                            int i6 = i5 + 1;
                            this.raisedToBack = i6;
                            if (i6 == 6) {
                                this.raisedToTop = 0;
                                this.raisedToTopSign = 0;
                                this.countLess = 0;
                                this.timeSinceRaise = System.currentTimeMillis();
                                if (BuildVars.LOGS_ENABLED && BuildVars.DEBUG_PRIVATE_VERSION) {
                                    FileLog.d("motion detected");
                                }
                            }
                        }
                    }
                }
                this.previousAccValue = val;
                float[] fArr12 = this.gravityFast;
                this.accelerometerVertical = fArr12[1] > 2.5f && Math.abs(fArr12[2]) < 4.0f && Math.abs(this.gravityFast[0]) > 1.5f;
            }
            if (this.raisedToBack != 6 || !this.accelerometerVertical || !this.proximityTouched || NotificationsController.audioManager.isWiredHeadsetOn()) {
                boolean z = this.proximityTouched;
                if (z) {
                    if (this.playingMessageObject != null && !ApplicationLoader.mainInterfacePaused && ((this.playingMessageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen by proximity only");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock3 = this.proximityWakeLock) != null && !wakeLock3.isHeld()) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!z) {
                    if (this.raiseToEarRecord) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop record");
                        }
                        stopRecording(2, false, 0);
                        this.raiseToEarRecord = false;
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock2 = this.proximityWakeLock) != null && wakeLock2.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    } else if (this.useFrontSpeaker) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("stop listen");
                        }
                        this.useFrontSpeaker = false;
                        startAudioAgain(true);
                        this.ignoreOnPause = false;
                        if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                            this.proximityWakeLock.release();
                        }
                    }
                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("sensor values reached");
                }
                if (this.playingMessageObject != null || this.recordStartRunnable != null || this.recordingAudio != null || PhotoViewer.getInstance().isVisible() || !ApplicationLoader.isScreenOn || this.inputFieldHasText || !this.allowStartRecord || this.raiseChat == null || this.callInProgress) {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null && ((messageObject.isVoice() || this.playingMessageObject.isRoundVideo()) && !this.useFrontSpeaker)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("start listen");
                        }
                        if (this.proximityHasDifferentValues && (wakeLock4 = this.proximityWakeLock) != null && !wakeLock4.isHeld()) {
                            this.proximityWakeLock.acquire();
                        }
                        setUseFrontSpeaker(true);
                        startAudioAgain(false);
                        this.ignoreOnPause = true;
                    }
                } else if (!this.raiseToEarRecord) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("start record");
                    }
                    this.useFrontSpeaker = true;
                    if (!this.raiseChat.playFirstUnreadVoiceMessage()) {
                        this.raiseToEarRecord = true;
                        this.useFrontSpeaker = false;
                        startRecording(this.raiseChat.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getClassGuid());
                    }
                    if (this.useFrontSpeaker) {
                        setUseFrontSpeaker(true);
                    }
                    this.ignoreOnPause = true;
                    if (this.proximityHasDifferentValues && (wakeLock5 = this.proximityWakeLock) != null && !wakeLock5.isHeld()) {
                        this.proximityWakeLock.acquire();
                    }
                }
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
            }
            if (this.timeSinceRaise != 0 && this.raisedToBack == 6 && Math.abs(System.currentTimeMillis() - this.timeSinceRaise) > 1000) {
                this.raisedToBack = 0;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.timeSinceRaise = 0;
            }
        }
    }

    private void setUseFrontSpeaker(boolean value) {
        this.useFrontSpeaker = value;
        AudioManager audioManager = NotificationsController.audioManager;
        if (this.useFrontSpeaker) {
            audioManager.setBluetoothScoOn(false);
            audioManager.setSpeakerphoneOn(false);
            return;
        }
        audioManager.setSpeakerphoneOn(true);
    }

    public void startRecordingIfFromSpeaker() {
        ChatActivity chatActivity;
        if (this.useFrontSpeaker && (chatActivity = this.raiseChat) != null && this.allowStartRecord) {
            this.raiseToEarRecord = true;
            startRecording(chatActivity.getCurrentAccount(), this.raiseChat.getDialogId(), (MessageObject) null, this.raiseChat.getClassGuid());
            this.ignoreOnPause = true;
        }
    }

    private void startAudioAgain(boolean paused) {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            int i = 0;
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.audioRouteChanged, Boolean.valueOf(this.useFrontSpeaker));
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                if (!this.useFrontSpeaker) {
                    i = 3;
                }
                videoPlayer2.setStreamType(i);
                if (!paused) {
                    this.videoPlayer.play();
                } else {
                    lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
                }
            } else {
                boolean post = this.audioPlayer != null;
                MessageObject currentMessageObject = this.playingMessageObject;
                float progress = this.playingMessageObject.audioProgress;
                cleanupPlayer(false, true);
                currentMessageObject.audioProgress = progress;
                playMessage(currentMessageObject);
                if (!paused) {
                    return;
                }
                if (post) {
                    AndroidUtilities.runOnUIThread(new Runnable(currentMessageObject) {
                        private final /* synthetic */ MessageObject f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            MediaController.this.lambda$startAudioAgain$5$MediaController(this.f$1);
                        }
                    }, 100);
                } else {
                    lambda$startAudioAgain$5$MediaController(currentMessageObject);
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setInputFieldHasText(boolean value) {
        this.inputFieldHasText = value;
    }

    public void setAllowStartRecord(boolean value) {
        this.allowStartRecord = value;
    }

    public void startRaiseToEarSensors(ChatActivity chatActivity) {
        if (chatActivity == null) {
            return;
        }
        if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null) {
            this.raiseChat = chatActivity;
            if (!SharedConfig.raiseToSpeak) {
                MessageObject messageObject = this.playingMessageObject;
                if (messageObject == null) {
                    return;
                }
                if (!messageObject.isVoice() && !this.playingMessageObject.isRoundVideo()) {
                    return;
                }
            }
            if (!this.sensorsStarted) {
                float[] fArr = this.gravity;
                fArr[2] = 0.0f;
                fArr[1] = 0.0f;
                fArr[0] = 0.0f;
                float[] fArr2 = this.linearAcceleration;
                fArr2[2] = 0.0f;
                fArr2[1] = 0.0f;
                fArr2[0] = 0.0f;
                float[] fArr3 = this.gravityFast;
                fArr3[2] = 0.0f;
                fArr3[1] = 0.0f;
                fArr3[0] = 0.0f;
                this.lastTimestamp = 0;
                this.previousAccValue = 0.0f;
                this.raisedToTop = 0;
                this.raisedToTopSign = 0;
                this.countLess = 0;
                this.raisedToBack = 0;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        MediaController.this.lambda$startRaiseToEarSensors$6$MediaController();
                    }
                });
                this.sensorsStarted = true;
            }
        }
    }

    public /* synthetic */ void lambda$startRaiseToEarSensors$6$MediaController() {
        Sensor sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT);
        }
        Sensor sensor2 = this.linearSensor;
        if (sensor2 != null) {
            this.sensorManager.registerListener(this, sensor2, UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.registerListener(this, sensor3, UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
    }

    public void stopRaiseToEarSensors(ChatActivity chatActivity, boolean fromChat) {
        PowerManager.WakeLock wakeLock;
        if (this.ignoreOnPause) {
            this.ignoreOnPause = false;
            return;
        }
        stopRecording(fromChat ? 2 : 0, false, 0);
        if (this.sensorsStarted && !this.ignoreOnPause) {
            if ((this.accelerometerSensor != null || (this.gravitySensor != null && this.linearAcceleration != null)) && this.proximitySensor != null && this.raiseChat == chatActivity) {
                this.raiseChat = null;
                this.sensorsStarted = false;
                this.accelerometerVertical = false;
                this.proximityTouched = false;
                this.raiseToEarRecord = false;
                this.useFrontSpeaker = false;
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public final void run() {
                        MediaController.this.lambda$stopRaiseToEarSensors$7$MediaController();
                    }
                });
                if (this.proximityHasDifferentValues && (wakeLock = this.proximityWakeLock) != null && wakeLock.isHeld()) {
                    this.proximityWakeLock.release();
                }
            }
        }
    }

    public /* synthetic */ void lambda$stopRaiseToEarSensors$7$MediaController() {
        Sensor sensor = this.linearSensor;
        if (sensor != null) {
            this.sensorManager.unregisterListener(this, sensor);
        }
        Sensor sensor2 = this.gravitySensor;
        if (sensor2 != null) {
            this.sensorManager.unregisterListener(this, sensor2);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.unregisterListener(this, sensor3);
        }
        this.sensorManager.unregisterListener(this, this.proximitySensor);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService, false, false);
    }

    public void cleanupPlayer(boolean notify, boolean stopService, boolean byVoiceEnd, boolean transferPlayerToPhotoViewer) {
        PipRoundVideoView pipRoundVideoView2;
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            try {
                videoPlayer2.releasePlayer(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.audioPlayer = null;
        } else {
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
                this.currentAspectRatioFrameLayoutReady = false;
                this.isDrawingWasReady = false;
                this.currentTextureView = null;
                this.goingToShowMessageObject = null;
                if (transferPlayerToPhotoViewer) {
                    PhotoViewer.getInstance().injectVideoPlayer(this.videoPlayer);
                    MessageObject messageObject = this.playingMessageObject;
                    this.goingToShowMessageObject = messageObject;
                    NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, true);
                } else {
                    long position = videoPlayer3.getCurrentPosition();
                    MessageObject messageObject2 = this.playingMessageObject;
                    if (messageObject2 != null && messageObject2.isVideo() && position > 0 && position != C.TIME_UNSET) {
                        this.playingMessageObject.audioProgressMs = (int) position;
                        NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingGoingToStop, this.playingMessageObject, false);
                    }
                    this.videoPlayer.releasePlayer(true);
                    this.videoPlayer = null;
                }
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                if (this.playingMessageObject != null && !transferPlayerToPhotoViewer) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
            }
        }
        stopProgressTimer();
        this.lastProgress = 0;
        this.isPaused = false;
        if (!this.useFrontSpeaker && !SharedConfig.raiseToSpeak) {
            ChatActivity chat = this.raiseChat;
            stopRaiseToEarSensors(this.raiseChat, false);
            this.raiseChat = chat;
        }
        MessageObject messageObject3 = this.playingMessageObject;
        if (messageObject3 != null) {
            if (this.downloadingCurrentMessage) {
                FileLoader.getInstance(messageObject3.currentAccount).cancelLoadFile(this.playingMessageObject.getDocument());
            }
            MessageObject lastFile = this.playingMessageObject;
            if (notify) {
                this.playingMessageObject.resetPlayingProgress();
                NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
            }
            this.playingMessageObject = null;
            this.downloadingCurrentMessage = false;
            if (notify) {
                NotificationsController.audioManager.abandonAudioFocus(this);
                this.hasAudioFocus = 0;
                int index = -1;
                ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
                if (arrayList != null) {
                    if (byVoiceEnd) {
                        int indexOf = arrayList.indexOf(lastFile);
                        index = indexOf;
                        if (indexOf >= 0) {
                            this.voiceMessagesPlaylist.remove(index);
                            this.voiceMessagesPlaylistMap.remove(lastFile.getId());
                            if (this.voiceMessagesPlaylist.isEmpty()) {
                                this.voiceMessagesPlaylist = null;
                                this.voiceMessagesPlaylistMap = null;
                            }
                        }
                    }
                    this.voiceMessagesPlaylist = null;
                    this.voiceMessagesPlaylistMap = null;
                }
                ArrayList<MessageObject> arrayList2 = this.voiceMessagesPlaylist;
                if (arrayList2 == null || index >= arrayList2.size()) {
                    if ((lastFile.isVoice() || lastFile.isRoundVideo()) && lastFile.getId() != 0) {
                        startRecordingIfFromSpeaker();
                    }
                    NotificationCenter.getInstance(lastFile.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidReset, Integer.valueOf(lastFile.getId()), Boolean.valueOf(stopService));
                    this.pipSwitchingState = 0;
                    PipRoundVideoView pipRoundVideoView3 = this.pipRoundVideoView;
                    if (pipRoundVideoView3 != null) {
                        pipRoundVideoView3.close(true);
                        this.pipRoundVideoView = null;
                    }
                } else {
                    MessageObject nextVoiceMessage = this.voiceMessagesPlaylist.get(index);
                    playMessage(nextVoiceMessage);
                    if (!nextVoiceMessage.isRoundVideo() && (pipRoundVideoView2 = this.pipRoundVideoView) != null) {
                        pipRoundVideoView2.close(true);
                        this.pipRoundVideoView = null;
                    }
                }
            }
            if (stopService) {
                ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, MusicPlayerService.class));
            }
        }
    }

    public boolean isGoingToShowMessageObject(MessageObject messageObject) {
        return this.goingToShowMessageObject == messageObject;
    }

    public void resetGoingToShowMessageObject() {
        this.goingToShowMessageObject = null;
    }

    private boolean isSamePlayingMessage(MessageObject messageObject) {
        MessageObject messageObject2 = this.playingMessageObject;
        if (messageObject2 != null && messageObject2.getDialogId() == messageObject.getDialogId() && this.playingMessageObject.getId() == messageObject.getId()) {
            if ((this.playingMessageObject.eventId == 0) == (messageObject.eventId == 0)) {
                return true;
            }
        }
        return false;
    }

    public boolean seekToProgress(MessageObject messageObject, float progress) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            if (this.audioPlayer != null) {
                long duration = this.audioPlayer.getDuration();
                if (duration == C.TIME_UNSET) {
                    this.seekToProgressPending = progress;
                } else {
                    int seekTo = (int) (((float) duration) * progress);
                    this.audioPlayer.seekTo((long) seekTo);
                    this.lastProgress = (long) seekTo;
                }
            } else if (this.videoPlayer != null) {
                this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * progress));
            }
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidSeek, Integer.valueOf(this.playingMessageObject.getId()), Float.valueOf(progress));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public MessageObject getPlayingMessageObject() {
        return this.playingMessageObject;
    }

    public int getPlayingMessageObjectNum() {
        return this.currentPlaylistNum;
    }

    private void buildShuffledPlayList() {
        if (!this.playlist.isEmpty()) {
            ArrayList<MessageObject> all = new ArrayList<>(this.playlist);
            this.shuffledPlaylist.clear();
            all.remove(this.currentPlaylistNum);
            this.shuffledPlaylist.add(this.playlist.get(this.currentPlaylistNum));
            int count = all.size();
            for (int a = 0; a < count; a++) {
                int index = Utilities.random.nextInt(all.size());
                this.shuffledPlaylist.add(all.get(index));
                all.remove(index);
            }
        }
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current) {
        return setPlaylist(messageObjects, current, true);
    }

    public boolean setPlaylist(ArrayList<MessageObject> messageObjects, MessageObject current, boolean loadMusic) {
        if (this.playingMessageObject == current) {
            return playMessage(current);
        }
        this.forceLoopCurrentPlaylist = !loadMusic;
        this.playMusicAgain = !this.playlist.isEmpty();
        this.playlist.clear();
        for (int a = messageObjects.size() - 1; a >= 0; a--) {
            MessageObject messageObject = messageObjects.get(a);
            if (messageObject.isMusic()) {
                this.playlist.add(messageObject);
            }
        }
        int indexOf = this.playlist.indexOf(current);
        this.currentPlaylistNum = indexOf;
        if (indexOf == -1) {
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.currentPlaylistNum = this.playlist.size();
            this.playlist.add(current);
        }
        if (current.isMusic() && !current.scheduled) {
            if (SharedConfig.shuffleMusic) {
                buildShuffledPlayList();
                this.currentPlaylistNum = 0;
            }
            if (loadMusic) {
                MediaDataController.getInstance(current.currentAccount).loadMusic(current.getDialogId(), this.playlist.get(0).getIdWithChannel());
            }
        }
        return playMessage(current);
    }

    public void playNextMessage() {
        playNextMessageWithoutOrder(false);
    }

    public boolean findMessageInPlaylistAndPlay(MessageObject messageObject) {
        int index = this.playlist.indexOf(messageObject);
        if (index == -1) {
            return playMessage(messageObject);
        }
        playMessageAtIndex(index);
        return true;
    }

    public void playMessageAtIndex(int index) {
        int i = this.currentPlaylistNum;
        if (i >= 0 && i < this.playlist.size()) {
            this.currentPlaylistNum = index;
            this.playMusicAgain = true;
            MessageObject messageObject = this.playingMessageObject;
            if (messageObject != null) {
                messageObject.resetPlayingProgress();
            }
            playMessage(this.playlist.get(this.currentPlaylistNum));
        }
    }

    /* access modifiers changed from: private */
    public void playNextMessageWithoutOrder(boolean byStop) {
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!byStop || (!(SharedConfig.repeatMode == 2 || (SharedConfig.repeatMode == 1 && currentPlayList.size() == 1)) || this.forceLoopCurrentPlaylist)) {
            boolean last = false;
            if (SharedConfig.playOrderReversed) {
                int i = this.currentPlaylistNum + 1;
                this.currentPlaylistNum = i;
                if (i >= currentPlayList.size()) {
                    this.currentPlaylistNum = 0;
                    last = true;
                }
            } else {
                int i2 = this.currentPlaylistNum - 1;
                this.currentPlaylistNum = i2;
                if (i2 < 0) {
                    this.currentPlaylistNum = currentPlayList.size() - 1;
                    last = true;
                }
            }
            if (!last || !byStop || SharedConfig.repeatMode != 0 || this.forceLoopCurrentPlaylist) {
                int i3 = this.currentPlaylistNum;
                if (i3 >= 0 && i3 < currentPlayList.size()) {
                    MessageObject messageObject = this.playingMessageObject;
                    if (messageObject != null) {
                        messageObject.resetPlayingProgress();
                    }
                    this.playMusicAgain = true;
                    playMessage(currentPlayList.get(this.currentPlaylistNum));
                }
            } else if (this.audioPlayer != null || this.videoPlayer != null) {
                VideoPlayer videoPlayer2 = this.audioPlayer;
                if (videoPlayer2 != null) {
                    try {
                        videoPlayer2.releasePlayer(true);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    this.audioPlayer = null;
                } else {
                    VideoPlayer videoPlayer3 = this.videoPlayer;
                    if (videoPlayer3 != null) {
                        this.currentAspectRatioFrameLayout = null;
                        this.currentTextureViewContainer = null;
                        this.currentAspectRatioFrameLayoutReady = false;
                        this.currentTextureView = null;
                        videoPlayer3.releasePlayer(true);
                        this.videoPlayer = null;
                        try {
                            this.baseActivity.getWindow().clearFlags(128);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                        FileLoader.getInstance(this.playingMessageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                    }
                }
                stopProgressTimer();
                this.lastProgress = 0;
                this.isPaused = true;
                this.playingMessageObject.audioProgress = 0.0f;
                this.playingMessageObject.audioProgressSec = 0;
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingProgressDidChanged, Integer.valueOf(this.playingMessageObject.getId()), 0);
                NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            }
        } else {
            cleanupPlayer(false, false);
            MessageObject messageObject2 = currentPlayList.get(this.currentPlaylistNum);
            messageObject2.audioProgress = 0.0f;
            messageObject2.audioProgressSec = 0;
            playMessage(messageObject2);
        }
    }

    public void playPreviousMessage() {
        int i;
        ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
        if (!currentPlayList.isEmpty() && (i = this.currentPlaylistNum) >= 0 && i < currentPlayList.size()) {
            MessageObject currentSong = currentPlayList.get(this.currentPlaylistNum);
            if (currentSong.audioProgressSec > 10) {
                seekToProgress(currentSong, 0.0f);
                return;
            }
            if (SharedConfig.playOrderReversed) {
                int i2 = this.currentPlaylistNum - 1;
                this.currentPlaylistNum = i2;
                if (i2 < 0) {
                    this.currentPlaylistNum = currentPlayList.size() - 1;
                }
            } else {
                int i3 = this.currentPlaylistNum + 1;
                this.currentPlaylistNum = i3;
                if (i3 >= currentPlayList.size()) {
                    this.currentPlaylistNum = 0;
                }
            }
            int i4 = this.currentPlaylistNum;
            if (i4 >= 0 && i4 < currentPlayList.size()) {
                this.playMusicAgain = true;
                playMessage(currentPlayList.get(this.currentPlaylistNum));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkIsNextMediaFileDownloaded() {
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null && messageObject.isMusic()) {
            checkIsNextMusicFileDownloaded(this.playingMessageObject.currentAccount);
        }
    }

    private void checkIsNextVoiceFileDownloaded(int currentAccount) {
        ArrayList<MessageObject> arrayList = this.voiceMessagesPlaylist;
        if (arrayList != null && arrayList.size() >= 2) {
            MessageObject nextAudio = this.voiceMessagesPlaylist.get(1);
            File file = null;
            if (nextAudio.messageOwner.attachPath != null && nextAudio.messageOwner.attachPath.length() > 0) {
                file = new File(nextAudio.messageOwner.attachPath);
                if (!file.exists()) {
                    file = null;
                }
            }
            File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
            if (cacheFile == null || !cacheFile.exists()) {
            }
            if (cacheFile != null && cacheFile != file && !cacheFile.exists()) {
                FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
            }
        }
    }

    private void checkIsNextMusicFileDownloaded(int currentAccount) {
        int nextIndex;
        if (DownloadController.getInstance(currentAccount).canDownloadNextTrack()) {
            ArrayList<MessageObject> currentPlayList = SharedConfig.shuffleMusic ? this.shuffledPlaylist : this.playlist;
            if (currentPlayList != null && currentPlayList.size() >= 2) {
                if (SharedConfig.playOrderReversed) {
                    nextIndex = this.currentPlaylistNum + 1;
                    if (nextIndex >= currentPlayList.size()) {
                        nextIndex = 0;
                    }
                } else {
                    nextIndex = this.currentPlaylistNum - 1;
                    if (nextIndex < 0) {
                        nextIndex = currentPlayList.size() - 1;
                    }
                }
                if (nextIndex >= 0 && nextIndex < currentPlayList.size()) {
                    MessageObject nextAudio = currentPlayList.get(nextIndex);
                    File file = null;
                    if (!TextUtils.isEmpty(nextAudio.messageOwner.attachPath)) {
                        file = new File(nextAudio.messageOwner.attachPath);
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                    File cacheFile = file != null ? file : FileLoader.getPathToMessage(nextAudio.messageOwner);
                    if (cacheFile == null || !cacheFile.exists()) {
                    }
                    if (cacheFile != null && cacheFile != file && !cacheFile.exists() && nextAudio.isMusic()) {
                        FileLoader.getInstance(currentAccount).loadFile(nextAudio.getDocument(), nextAudio, 0, 0);
                    }
                }
            }
        }
    }

    public void setVoiceMessagesPlaylist(ArrayList<MessageObject> playlist2, boolean unread) {
        this.voiceMessagesPlaylist = playlist2;
        if (playlist2 != null) {
            this.voiceMessagesPlaylistUnread = unread;
            this.voiceMessagesPlaylistMap = new SparseArray<>();
            for (int a = 0; a < this.voiceMessagesPlaylist.size(); a++) {
                MessageObject messageObject = this.voiceMessagesPlaylist.get(a);
                this.voiceMessagesPlaylistMap.put(messageObject.getId(), messageObject);
            }
        }
    }

    private void checkAudioFocus(MessageObject messageObject) {
        int neededAudioFocus;
        int result;
        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
            neededAudioFocus = 1;
        } else if (this.useFrontSpeaker != 0) {
            neededAudioFocus = 3;
        } else {
            neededAudioFocus = 2;
        }
        if (this.hasAudioFocus != neededAudioFocus) {
            this.hasAudioFocus = neededAudioFocus;
            if (neededAudioFocus == 3) {
                result = NotificationsController.audioManager.requestAudioFocus(this, 0, 1);
            } else {
                result = NotificationsController.audioManager.requestAudioFocus(this, 3, neededAudioFocus == 2 ? 3 : 1);
            }
            if (result == 1) {
                this.audioFocus = 2;
            }
        }
    }

    public void setCurrentVideoVisible(boolean visible) {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        if (aspectRatioFrameLayout != null) {
            if (visible) {
                PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                if (pipRoundVideoView2 != null) {
                    this.pipSwitchingState = 2;
                    pipRoundVideoView2.close(true);
                    this.pipRoundVideoView = null;
                } else if (aspectRatioFrameLayout != null) {
                    if (aspectRatioFrameLayout.getParent() == null) {
                        this.currentTextureViewContainer.addView(this.currentAspectRatioFrameLayout);
                    }
                    this.videoPlayer.setTextureView(this.currentTextureView);
                }
            } else if (aspectRatioFrameLayout.getParent() != null) {
                this.pipSwitchingState = 1;
                this.currentTextureViewContainer.removeView(this.currentAspectRatioFrameLayout);
            } else {
                if (this.pipRoundVideoView == null) {
                    try {
                        PipRoundVideoView pipRoundVideoView3 = new PipRoundVideoView();
                        this.pipRoundVideoView = pipRoundVideoView3;
                        pipRoundVideoView3.show(this.baseActivity, new Runnable() {
                            public final void run() {
                                MediaController.this.lambda$setCurrentVideoVisible$8$MediaController();
                            }
                        });
                    } catch (Exception e) {
                        this.pipRoundVideoView = null;
                    }
                }
                PipRoundVideoView pipRoundVideoView4 = this.pipRoundVideoView;
                if (pipRoundVideoView4 != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView4.getTextureView());
                }
            }
        }
    }

    public /* synthetic */ void lambda$setCurrentVideoVisible$8$MediaController() {
        cleanupPlayer(true, true);
    }

    public void setTextureView(TextureView textureView, AspectRatioFrameLayout aspectRatioFrameLayout, FrameLayout container, boolean set) {
        if (textureView != null) {
            boolean z = true;
            if (!set && this.currentTextureView == textureView) {
                this.pipSwitchingState = 1;
                this.currentTextureView = null;
                this.currentAspectRatioFrameLayout = null;
                this.currentTextureViewContainer = null;
            } else if (this.videoPlayer != null && textureView != this.currentTextureView) {
                if (aspectRatioFrameLayout == null || !aspectRatioFrameLayout.isDrawingReady()) {
                    z = false;
                }
                this.isDrawingWasReady = z;
                this.currentTextureView = textureView;
                PipRoundVideoView pipRoundVideoView2 = this.pipRoundVideoView;
                if (pipRoundVideoView2 != null) {
                    this.videoPlayer.setTextureView(pipRoundVideoView2.getTextureView());
                } else {
                    this.videoPlayer.setTextureView(textureView);
                }
                this.currentAspectRatioFrameLayout = aspectRatioFrameLayout;
                this.currentTextureViewContainer = container;
                if (this.currentAspectRatioFrameLayoutReady && aspectRatioFrameLayout != null && aspectRatioFrameLayout != null) {
                    aspectRatioFrameLayout.setAspectRatio(this.currentAspectRatioFrameLayoutRatio, this.currentAspectRatioFrameLayoutRotation);
                }
            }
        }
    }

    public boolean hasFlagSecureFragment() {
        return this.flagSecureFragment != null;
    }

    public void setFlagSecure(BaseFragment parentFragment, boolean set) {
        if (set) {
            try {
                parentFragment.getParentActivity().getWindow().setFlags(8192, 8192);
            } catch (Exception e) {
            }
            this.flagSecureFragment = parentFragment;
        } else if (this.flagSecureFragment == parentFragment) {
            try {
                parentFragment.getParentActivity().getWindow().clearFlags(8192);
            } catch (Exception e2) {
            }
            this.flagSecureFragment = null;
        }
    }

    public void setBaseActivity(Activity activity, boolean set) {
        if (set) {
            this.baseActivity = activity;
        } else if (this.baseActivity == activity) {
            this.baseActivity = null;
        }
    }

    public void setFeedbackView(View view, boolean set) {
        if (set) {
            this.feedbackView = view;
        } else if (this.feedbackView == view) {
            this.feedbackView = null;
        }
    }

    public void setPlaybackSpeed(float speed) {
        this.currentPlaybackSpeed = speed;
        VideoPlayer videoPlayer2 = this.audioPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setPlaybackSpeed(speed);
        } else {
            VideoPlayer videoPlayer3 = this.videoPlayer;
            if (videoPlayer3 != null) {
                videoPlayer3.setPlaybackSpeed(speed);
            }
        }
        MessagesController.getGlobalMainSettings().edit().putFloat("playbackSpeed", speed).commit();
    }

    public float getPlaybackSpeed() {
        return this.currentPlaybackSpeed;
    }

    /* access modifiers changed from: private */
    public void updateVideoState(MessageObject messageObject, int[] playCount, boolean destroyAtEnd, boolean playWhenReady, int playbackState) {
        MessageObject messageObject2;
        if (this.videoPlayer != null) {
            if (playbackState == 4 || playbackState == 1) {
                try {
                    this.baseActivity.getWindow().clearFlags(128);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    this.baseActivity.getWindow().addFlags(128);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            if (playbackState == 3) {
                this.playerWasReady = true;
                MessageObject messageObject3 = this.playingMessageObject;
                if (messageObject3 != null && (messageObject3.isVideo() || this.playingMessageObject.isRoundVideo())) {
                    AndroidUtilities.cancelRunOnUIThread(this.setLoadingRunnable);
                    FileLoader.getInstance(messageObject.currentAccount).removeLoadingVideo(this.playingMessageObject.getDocument(), true, false);
                }
                this.currentAspectRatioFrameLayoutReady = true;
            } else if (playbackState == 2) {
                if (playWhenReady && (messageObject2 = this.playingMessageObject) != null) {
                    if (!messageObject2.isVideo() && !this.playingMessageObject.isRoundVideo()) {
                        return;
                    }
                    if (this.playerWasReady) {
                        this.setLoadingRunnable.run();
                    } else {
                        AndroidUtilities.runOnUIThread(this.setLoadingRunnable, 1000);
                    }
                }
            } else if (this.videoPlayer.isPlaying() && playbackState == 4) {
                if (!this.playingMessageObject.isVideo() || destroyAtEnd || (playCount != null && playCount[0] >= 4)) {
                    cleanupPlayer(true, true, true, false);
                    return;
                }
                this.videoPlayer.seekTo(0);
                if (playCount != null) {
                    playCount[0] = playCount[0] + 1;
                }
            }
        }
    }

    public void injectVideoPlayer(VideoPlayer player, final MessageObject messageObject) {
        if (player != null && messageObject != null) {
            FileLoader.getInstance(messageObject.currentAccount).setLoadingVideoForPlayer(messageObject.getDocument(), true);
            this.playerWasReady = false;
            this.playlist.clear();
            this.shuffledPlaylist.clear();
            this.videoPlayer = player;
            this.playingMessageObject = messageObject;
            player.setDelegate(new VideoPlayer.VideoPlayerDelegate((int[]) null, true) {
                public void onStateChanged(boolean playWhenReady, int playbackState) {
                    MediaController.this.updateVideoState(messageObject, null, true, playWhenReady, playbackState);
                }

                public void onError(Exception e) {
                    FileLog.e((Throwable) e);
                }

                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    int unused = MediaController.this.currentAspectRatioFrameLayoutRotation = unappliedRotationDegrees;
                    if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                        int temp = width;
                        width = height;
                        height = temp;
                    }
                    float unused2 = MediaController.this.currentAspectRatioFrameLayoutRatio = height == 0 ? 1.0f : (((float) width) * pixelWidthHeightRatio) / ((float) height);
                    if (MediaController.this.currentAspectRatioFrameLayout != null) {
                        MediaController.this.currentAspectRatioFrameLayout.setAspectRatio(MediaController.this.currentAspectRatioFrameLayoutRatio, MediaController.this.currentAspectRatioFrameLayoutRotation);
                    }
                }

                public void onRenderedFirstFrame() {
                    if (MediaController.this.currentAspectRatioFrameLayout != null && !MediaController.this.currentAspectRatioFrameLayout.isDrawingReady()) {
                        boolean unused = MediaController.this.isDrawingWasReady = true;
                        MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                        MediaController.this.currentTextureViewContainer.setTag(1);
                    }
                }

                public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                    if (MediaController.this.videoPlayer == null) {
                        return false;
                    }
                    if (MediaController.this.pipSwitchingState == 2) {
                        if (MediaController.this.currentAspectRatioFrameLayout != null) {
                            if (MediaController.this.isDrawingWasReady) {
                                MediaController.this.currentAspectRatioFrameLayout.setDrawingReady(true);
                            }
                            if (MediaController.this.currentAspectRatioFrameLayout.getParent() == null) {
                                MediaController.this.currentTextureViewContainer.addView(MediaController.this.currentAspectRatioFrameLayout);
                            }
                            if (MediaController.this.currentTextureView.getSurfaceTexture() != surfaceTexture) {
                                MediaController.this.currentTextureView.setSurfaceTexture(surfaceTexture);
                            }
                            MediaController.this.videoPlayer.setTextureView(MediaController.this.currentTextureView);
                        }
                        int unused = MediaController.this.pipSwitchingState = 0;
                        return true;
                    } else if (MediaController.this.pipSwitchingState == 1) {
                        if (MediaController.this.baseActivity != null) {
                            if (MediaController.this.pipRoundVideoView == null) {
                                try {
                                    PipRoundVideoView unused2 = MediaController.this.pipRoundVideoView = new PipRoundVideoView();
                                    MediaController.this.pipRoundVideoView.show(MediaController.this.baseActivity, new Runnable() {
                                        public final void run() {
                                            MediaController.AnonymousClass5.this.lambda$onSurfaceDestroyed$0$MediaController$5();
                                        }
                                    });
                                } catch (Exception e) {
                                    PipRoundVideoView unused3 = MediaController.this.pipRoundVideoView = null;
                                }
                            }
                            if (MediaController.this.pipRoundVideoView != null) {
                                if (MediaController.this.pipRoundVideoView.getTextureView().getSurfaceTexture() != surfaceTexture) {
                                    MediaController.this.pipRoundVideoView.getTextureView().setSurfaceTexture(surfaceTexture);
                                }
                                MediaController.this.videoPlayer.setTextureView(MediaController.this.pipRoundVideoView.getTextureView());
                            }
                        }
                        int unused4 = MediaController.this.pipSwitchingState = 0;
                        return true;
                    } else if (!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isInjectingVideoPlayer()) {
                        return false;
                    } else {
                        PhotoViewer.getInstance().injectVideoPlayerSurface(surfaceTexture);
                        return true;
                    }
                }

                public /* synthetic */ void lambda$onSurfaceDestroyed$0$MediaController$5() {
                    MediaController.this.cleanupPlayer(true, true);
                }

                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            });
            this.currentAspectRatioFrameLayoutReady = false;
            TextureView textureView = this.currentTextureView;
            if (textureView != null) {
                this.videoPlayer.setTextureView(textureView);
            }
            checkAudioFocus(messageObject);
            setPlayerVolume();
            this.isPaused = false;
            this.lastProgress = 0;
            this.playingMessageObject = messageObject;
            if (!SharedConfig.raiseToSpeak) {
                startRaiseToEarSensors(this.raiseChat);
            }
            startProgressTimer(this.playingMessageObject);
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingDidStart, messageObject);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:136:0x02c5  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02ca  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02d9  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x03bf  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x046b  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0483  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x04c6 A[SYNTHETIC, Splitter:B:211:0x04c6] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0590  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x05a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean playMessage(im.bclpbkiauv.messenger.MessageObject r31) {
        /*
            r30 = this;
            r1 = r30
            r2 = r31
            r3 = 0
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            if (r2 != 0) goto L_0x000c
            return r3
        L_0x000c:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r1.audioPlayer
            r5 = 1
            if (r0 != 0) goto L_0x0015
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r1.videoPlayer
            if (r0 == 0) goto L_0x002c
        L_0x0015:
            boolean r0 = r30.isSamePlayingMessage(r31)
            if (r0 == 0) goto L_0x002c
            boolean r0 = r1.isPaused
            if (r0 == 0) goto L_0x0022
            r30.resumeAudio(r31)
        L_0x0022:
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.raiseToSpeak
            if (r0 != 0) goto L_0x002b
            im.bclpbkiauv.ui.ChatActivity r0 = r1.raiseChat
            r1.startRaiseToEarSensors(r0)
        L_0x002b:
            return r5
        L_0x002c:
            boolean r0 = r31.isOut()
            if (r0 != 0) goto L_0x0041
            boolean r0 = r31.isContentUnread()
            if (r0 == 0) goto L_0x0041
            int r0 = r2.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            r0.markMessageContentAsRead(r2)
        L_0x0041:
            boolean r0 = r1.playMusicAgain
            r6 = r0 ^ 1
            im.bclpbkiauv.messenger.MessageObject r7 = r1.playingMessageObject
            if (r7 == 0) goto L_0x004f
            r6 = 0
            if (r0 != 0) goto L_0x004f
            r7.resetPlayingProgress()
        L_0x004f:
            r1.cleanupPlayer(r6, r3)
            r1.playMusicAgain = r3
            r7 = 0
            r1.seekToProgressPending = r7
            r0 = 0
            r8 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r2.messageOwner
            java.lang.String r9 = r9.attachPath
            if (r9 == 0) goto L_0x0086
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r2.messageOwner
            java.lang.String r9 = r9.attachPath
            int r9 = r9.length()
            if (r9 <= 0) goto L_0x0086
            java.io.File r9 = new java.io.File
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r2.messageOwner
            java.lang.String r10 = r10.attachPath
            r9.<init>(r10)
            r0 = r9
            boolean r8 = r0.exists()
            if (r8 != 0) goto L_0x0080
            r0 = 0
            r29 = r8
            r8 = r0
            r0 = r29
            goto L_0x008b
        L_0x0080:
            r29 = r8
            r8 = r0
            r0 = r29
            goto L_0x008b
        L_0x0086:
            r29 = r8
            r8 = r0
            r0 = r29
        L_0x008b:
            if (r8 == 0) goto L_0x008f
            r9 = r8
            goto L_0x0095
        L_0x008f:
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r2.messageOwner
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getPathToMessage(r9)
        L_0x0095:
            boolean r10 = im.bclpbkiauv.messenger.SharedConfig.streamMedia
            if (r10 == 0) goto L_0x00ba
            boolean r10 = r31.isMusic()
            if (r10 != 0) goto L_0x00b1
            boolean r10 = r31.isRoundVideo()
            if (r10 != 0) goto L_0x00b1
            boolean r10 = r31.isVideo()
            if (r10 == 0) goto L_0x00ba
            boolean r10 = r31.canStreamVideo()
            if (r10 == 0) goto L_0x00ba
        L_0x00b1:
            long r10 = r31.getDialogId()
            int r11 = (int) r10
            if (r11 == 0) goto L_0x00ba
            r10 = 1
            goto L_0x00bb
        L_0x00ba:
            r10 = 0
        L_0x00bb:
            r11 = 0
            r13 = 0
            if (r9 == 0) goto L_0x0127
            if (r9 == r8) goto L_0x0127
            boolean r14 = r9.exists()
            r15 = r14
            if (r14 != 0) goto L_0x0128
            if (r10 != 0) goto L_0x0128
            int r0 = r2.currentAccount
            im.bclpbkiauv.messenger.FileLoader r0 = im.bclpbkiauv.messenger.FileLoader.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r31.getDocument()
            r0.loadFile(r4, r2, r3, r3)
            r1.downloadingCurrentMessage = r5
            r1.isPaused = r3
            r1.lastProgress = r11
            r1.audioInfo = r13
            r1.playingMessageObject = r2
            boolean r0 = r31.isMusic()
            if (r0 == 0) goto L_0x00fd
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.MusicPlayerService> r7 = im.bclpbkiauv.messenger.MusicPlayerService.class
            r0.<init>(r4, r7)
            r4 = r0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00f8 }
            r0.startService(r4)     // Catch:{ all -> 0x00f8 }
            goto L_0x00fc
        L_0x00f8:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00fc:
            goto L_0x010b
        L_0x00fd:
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.MusicPlayerService> r7 = im.bclpbkiauv.messenger.MusicPlayerService.class
            r0.<init>(r4, r7)
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r4.stopService(r0)
        L_0x010b:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.playingMessageObject
            int r0 = r0.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r4 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingPlayStateChanged
            java.lang.Object[] r7 = new java.lang.Object[r5]
            im.bclpbkiauv.messenger.MessageObject r11 = r1.playingMessageObject
            int r11 = r11.getId()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r7[r3] = r11
            r0.postNotificationName(r4, r7)
            return r5
        L_0x0127:
            r15 = r0
        L_0x0128:
            r1.downloadingCurrentMessage = r3
            boolean r0 = r31.isMusic()
            if (r0 == 0) goto L_0x0136
            int r0 = r2.currentAccount
            r1.checkIsNextMusicFileDownloaded(r0)
            goto L_0x013b
        L_0x0136:
            int r0 = r2.currentAccount
            r1.checkIsNextVoiceFileDownloaded(r0)
        L_0x013b:
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r0 = r1.currentAspectRatioFrameLayout
            if (r0 == 0) goto L_0x0144
            r1.isDrawingWasReady = r3
            r0.setDrawingReady(r3)
        L_0x0144:
            boolean r14 = r31.isVideo()
            boolean r0 = r31.isRoundVideo()
            java.lang.String r7 = "hchat://"
            java.lang.String r11 = "&reference="
            java.lang.String r12 = "&name="
            java.lang.String r3 = "&rid="
            java.lang.String r13 = "&mime="
            java.lang.String r5 = "&size="
            r17 = r6
            java.lang.String r6 = "&dc="
            r18 = r10
            java.lang.String r10 = "&hash="
            r19 = r4
            java.lang.String r4 = "&id="
            r20 = r7
            java.lang.String r7 = "?account="
            r21 = 1065353216(0x3f800000, float:1.0)
            r22 = r11
            java.lang.String r11 = "UTF-8"
            r23 = r12
            java.lang.String r12 = "other"
            if (r0 != 0) goto L_0x02e7
            if (r14 == 0) goto L_0x0181
            r25 = r8
            r24 = r14
            r8 = r1
            r14 = r2
            r1 = r23
            r2 = 0
            goto L_0x02f0
        L_0x0181:
            im.bclpbkiauv.ui.components.PipRoundVideoView r0 = r1.pipRoundVideoView
            if (r0 == 0) goto L_0x018f
            r24 = r14
            r14 = 1
            r0.close(r14)
            r14 = 0
            r1.pipRoundVideoView = r14
            goto L_0x0191
        L_0x018f:
            r24 = r14
        L_0x0191:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = new im.bclpbkiauv.ui.components.VideoPlayer     // Catch:{ Exception -> 0x02ad }
            r0.<init>()     // Catch:{ Exception -> 0x02ad }
            r1.audioPlayer = r0     // Catch:{ Exception -> 0x02ad }
            im.bclpbkiauv.messenger.MediaController$7 r14 = new im.bclpbkiauv.messenger.MediaController$7     // Catch:{ Exception -> 0x02ad }
            r14.<init>(r2)     // Catch:{ Exception -> 0x02ad }
            r0.setDelegate(r14)     // Catch:{ Exception -> 0x02ad }
            if (r15 == 0) goto L_0x01c4
            boolean r0 = r2.mediaExists     // Catch:{ Exception -> 0x01be }
            if (r0 != 0) goto L_0x01b0
            if (r9 == r8) goto L_0x01b0
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$DDUoWR1m6BVcGoE-FF_kxBtk46s r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$DDUoWR1m6BVcGoE-FF_kxBtk46s     // Catch:{ Exception -> 0x01be }
            r0.<init>(r9)     // Catch:{ Exception -> 0x01be }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x01be }
        L_0x01b0:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r1.audioPlayer     // Catch:{ Exception -> 0x01be }
            android.net.Uri r3 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x01be }
            r0.preparePlayer(r3, r12)     // Catch:{ Exception -> 0x01be }
            r3 = r1
            r25 = r8
            goto L_0x0261
        L_0x01be:
            r0 = move-exception
            r3 = r1
            r25 = r8
            goto L_0x02b1
        L_0x01c4:
            int r0 = r2.currentAccount     // Catch:{ Exception -> 0x02ad }
            im.bclpbkiauv.messenger.FileLoader r0 = im.bclpbkiauv.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x02ad }
            int r0 = r0.getFileReference(r2)     // Catch:{ Exception -> 0x02ad }
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r31.getDocument()     // Catch:{ Exception -> 0x02ad }
            r25 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02aa }
            r8.<init>()     // Catch:{ Exception -> 0x02aa }
            r8.append(r7)     // Catch:{ Exception -> 0x02aa }
            int r7 = r2.currentAccount     // Catch:{ Exception -> 0x02aa }
            r8.append(r7)     // Catch:{ Exception -> 0x02aa }
            r8.append(r4)     // Catch:{ Exception -> 0x02aa }
            long r1 = r14.id     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r10)     // Catch:{ Exception -> 0x02a6 }
            long r1 = r14.access_hash     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r6)     // Catch:{ Exception -> 0x02a6 }
            int r1 = r14.dc_id     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r5)     // Catch:{ Exception -> 0x02a6 }
            int r1 = r14.size     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r13)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r1 = r14.mime_type     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r1 = java.net.URLEncoder.encode(r1, r11)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r3)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r0)     // Catch:{ Exception -> 0x02a6 }
            r1 = r23
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r1 = im.bclpbkiauv.messenger.FileLoader.getDocumentFileName(r14)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r1 = java.net.URLEncoder.encode(r1, r11)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            r2 = r22
            r8.append(r2)     // Catch:{ Exception -> 0x02a6 }
            byte[] r1 = r14.file_reference     // Catch:{ Exception -> 0x02a6 }
            if (r1 == 0) goto L_0x022f
            byte[] r1 = r14.file_reference     // Catch:{ Exception -> 0x02a6 }
            goto L_0x0233
        L_0x022f:
            r1 = 0
            byte[] r2 = new byte[r1]     // Catch:{ Exception -> 0x02a6 }
            r1 = r2
        L_0x0233:
            java.lang.String r1 = im.bclpbkiauv.messenger.Utilities.bytesToHex(r1)     // Catch:{ Exception -> 0x02a6 }
            r8.append(r1)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r1 = r8.toString()     // Catch:{ Exception -> 0x02a6 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02a6 }
            r2.<init>()     // Catch:{ Exception -> 0x02a6 }
            r8 = r20
            r2.append(r8)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r3 = r31.getFileName()     // Catch:{ Exception -> 0x02a6 }
            r2.append(r3)     // Catch:{ Exception -> 0x02a6 }
            r2.append(r1)     // Catch:{ Exception -> 0x02a6 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x02a6 }
            android.net.Uri r2 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x02a6 }
            r3 = r30
            im.bclpbkiauv.ui.components.VideoPlayer r4 = r3.audioPlayer     // Catch:{ Exception -> 0x02a4 }
            r4.preparePlayer(r2, r12)     // Catch:{ Exception -> 0x02a4 }
        L_0x0261:
            boolean r0 = r31.isVoice()     // Catch:{ Exception -> 0x02a4 }
            if (r0 == 0) goto L_0x0282
            float r0 = r3.currentPlaybackSpeed     // Catch:{ Exception -> 0x02a4 }
            int r0 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1))
            if (r0 <= 0) goto L_0x0274
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r3.audioPlayer     // Catch:{ Exception -> 0x02a4 }
            float r1 = r3.currentPlaybackSpeed     // Catch:{ Exception -> 0x02a4 }
            r0.setPlaybackSpeed(r1)     // Catch:{ Exception -> 0x02a4 }
        L_0x0274:
            r1 = 0
            r3.audioInfo = r1     // Catch:{ Exception -> 0x02a4 }
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r0 = r3.playlist     // Catch:{ Exception -> 0x02a4 }
            r0.clear()     // Catch:{ Exception -> 0x02a4 }
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r0 = r3.shuffledPlaylist     // Catch:{ Exception -> 0x02a4 }
            r0.clear()     // Catch:{ Exception -> 0x02a4 }
            goto L_0x028d
        L_0x0282:
            im.bclpbkiauv.messenger.audioinfo.AudioInfo r0 = im.bclpbkiauv.messenger.audioinfo.AudioInfo.getAudioInfo(r9)     // Catch:{ Exception -> 0x0289 }
            r3.audioInfo = r0     // Catch:{ Exception -> 0x0289 }
            goto L_0x028d
        L_0x0289:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x02a4 }
        L_0x028d:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r3.audioPlayer     // Catch:{ Exception -> 0x02a4 }
            boolean r1 = r3.useFrontSpeaker     // Catch:{ Exception -> 0x02a4 }
            if (r1 == 0) goto L_0x0295
            r1 = 0
            goto L_0x0296
        L_0x0295:
            r1 = 3
        L_0x0296:
            r0.setStreamType(r1)     // Catch:{ Exception -> 0x02a4 }
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r3.audioPlayer     // Catch:{ Exception -> 0x02a4 }
            r0.play()     // Catch:{ Exception -> 0x02a4 }
            r8 = r3
            r4 = r15
            r1 = r25
            goto L_0x048a
        L_0x02a4:
            r0 = move-exception
            goto L_0x02b1
        L_0x02a6:
            r0 = move-exception
            r3 = r30
            goto L_0x02b1
        L_0x02aa:
            r0 = move-exception
            r3 = r1
            goto L_0x02b1
        L_0x02ad:
            r0 = move-exception
            r3 = r1
            r25 = r8
        L_0x02b1:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r14 = r31
            int r1 = r14.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r1 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r1)
            int r2 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            im.bclpbkiauv.messenger.MessageObject r4 = r3.playingMessageObject
            if (r4 == 0) goto L_0x02ca
            int r4 = r4.getId()
            goto L_0x02cb
        L_0x02ca:
            r4 = 0
        L_0x02cb:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r6 = 0
            r5[r6] = r4
            r1.postNotificationName(r2, r5)
            im.bclpbkiauv.ui.components.VideoPlayer r1 = r3.audioPlayer
            if (r1 == 0) goto L_0x02e6
            r2 = 1
            r1.releasePlayer(r2)
            r1 = 0
            r3.audioPlayer = r1
            r3.isPaused = r6
            r3.playingMessageObject = r1
            r3.downloadingCurrentMessage = r6
        L_0x02e6:
            return r6
        L_0x02e7:
            r25 = r8
            r24 = r14
            r8 = r1
            r14 = r2
            r1 = r23
            r2 = 0
        L_0x02f0:
            int r0 = r14.currentAccount
            im.bclpbkiauv.messenger.FileLoader r0 = im.bclpbkiauv.messenger.FileLoader.getInstance(r0)
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r31.getDocument()
            r23 = r1
            r1 = 1
            r0.setLoadingVideoForPlayer(r2, r1)
            r1 = 0
            r8.playerWasReady = r1
            if (r24 == 0) goto L_0x0319
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r14.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 != 0) goto L_0x0317
            float r0 = r14.audioProgress
            r1 = 1036831949(0x3dcccccd, float:0.1)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 > 0) goto L_0x0317
            goto L_0x0319
        L_0x0317:
            r0 = 0
            goto L_0x031a
        L_0x0319:
            r0 = 1
        L_0x031a:
            r1 = r0
            if (r24 == 0) goto L_0x032d
            int r0 = r31.getDuration()
            r2 = 30
            if (r0 > r2) goto L_0x032d
            r2 = 1
            int[] r0 = new int[r2]
            r16 = 0
            r0[r16] = r2
            goto L_0x032e
        L_0x032d:
            r0 = 0
        L_0x032e:
            r2 = r0
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r0 = r8.playlist
            r0.clear()
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r0 = r8.shuffledPlaylist
            r0.clear()
            im.bclpbkiauv.ui.components.VideoPlayer r0 = new im.bclpbkiauv.ui.components.VideoPlayer
            r0.<init>()
            r8.videoPlayer = r0
            r26 = r3
            im.bclpbkiauv.messenger.MediaController$6 r3 = new im.bclpbkiauv.messenger.MediaController$6
            r3.<init>(r14, r2, r1)
            r0.setDelegate(r3)
            r3 = 0
            r8.currentAspectRatioFrameLayoutReady = r3
            im.bclpbkiauv.ui.components.PipRoundVideoView r0 = r8.pipRoundVideoView
            if (r0 != 0) goto L_0x0373
            int r0 = r14.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            r3 = r1
            r27 = r2
            long r1 = r31.getDialogId()
            r28 = r3
            boolean r3 = r14.scheduled
            boolean r0 = r0.isDialogVisible(r1, r3)
            if (r0 != 0) goto L_0x0369
            goto L_0x0377
        L_0x0369:
            android.view.TextureView r0 = r8.currentTextureView
            if (r0 == 0) goto L_0x039e
            im.bclpbkiauv.ui.components.VideoPlayer r1 = r8.videoPlayer
            r1.setTextureView(r0)
            goto L_0x039e
        L_0x0373:
            r28 = r1
            r27 = r2
        L_0x0377:
            im.bclpbkiauv.ui.components.PipRoundVideoView r0 = r8.pipRoundVideoView
            if (r0 != 0) goto L_0x0391
            im.bclpbkiauv.ui.components.PipRoundVideoView r0 = new im.bclpbkiauv.ui.components.PipRoundVideoView     // Catch:{ Exception -> 0x038d }
            r0.<init>()     // Catch:{ Exception -> 0x038d }
            r8.pipRoundVideoView = r0     // Catch:{ Exception -> 0x038d }
            android.app.Activity r1 = r8.baseActivity     // Catch:{ Exception -> 0x038d }
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$1Nmm75IVqhy6hWfsrk8lhUOqOD4 r2 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$1Nmm75IVqhy6hWfsrk8lhUOqOD4     // Catch:{ Exception -> 0x038d }
            r2.<init>()     // Catch:{ Exception -> 0x038d }
            r0.show(r1, r2)     // Catch:{ Exception -> 0x038d }
            goto L_0x0391
        L_0x038d:
            r0 = move-exception
            r1 = 0
            r8.pipRoundVideoView = r1
        L_0x0391:
            im.bclpbkiauv.ui.components.PipRoundVideoView r0 = r8.pipRoundVideoView
            if (r0 == 0) goto L_0x039e
            im.bclpbkiauv.ui.components.VideoPlayer r1 = r8.videoPlayer
            android.view.TextureView r0 = r0.getTextureView()
            r1.setTextureView(r0)
        L_0x039e:
            if (r15 == 0) goto L_0x03bf
            boolean r0 = r14.mediaExists
            if (r0 != 0) goto L_0x03b1
            r1 = r25
            if (r9 == r1) goto L_0x03b3
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$faCXGML4f46t2hJmYX8M27HQsnY r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$faCXGML4f46t2hJmYX8M27HQsnY
            r0.<init>(r9)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x03b3
        L_0x03b1:
            r1 = r25
        L_0x03b3:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer
            android.net.Uri r2 = android.net.Uri.fromFile(r9)
            r0.preparePlayer(r2, r12)
            r4 = r15
            goto L_0x0465
        L_0x03bf:
            r1 = r25
            int r0 = r14.currentAccount     // Catch:{ Exception -> 0x0460 }
            im.bclpbkiauv.messenger.FileLoader r0 = im.bclpbkiauv.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x0460 }
            int r0 = r0.getFileReference(r14)     // Catch:{ Exception -> 0x0460 }
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r31.getDocument()     // Catch:{ Exception -> 0x0460 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0460 }
            r3.<init>()     // Catch:{ Exception -> 0x0460 }
            r3.append(r7)     // Catch:{ Exception -> 0x0460 }
            int r7 = r14.currentAccount     // Catch:{ Exception -> 0x0460 }
            r3.append(r7)     // Catch:{ Exception -> 0x0460 }
            r3.append(r4)     // Catch:{ Exception -> 0x0460 }
            r4 = r15
            long r14 = r2.id     // Catch:{ Exception -> 0x045e }
            r3.append(r14)     // Catch:{ Exception -> 0x045e }
            r3.append(r10)     // Catch:{ Exception -> 0x045e }
            long r14 = r2.access_hash     // Catch:{ Exception -> 0x045e }
            r3.append(r14)     // Catch:{ Exception -> 0x045e }
            r3.append(r6)     // Catch:{ Exception -> 0x045e }
            int r6 = r2.dc_id     // Catch:{ Exception -> 0x045e }
            r3.append(r6)     // Catch:{ Exception -> 0x045e }
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            int r5 = r2.size     // Catch:{ Exception -> 0x045e }
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            r3.append(r13)     // Catch:{ Exception -> 0x045e }
            java.lang.String r5 = r2.mime_type     // Catch:{ Exception -> 0x045e }
            java.lang.String r5 = java.net.URLEncoder.encode(r5, r11)     // Catch:{ Exception -> 0x045e }
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            r5 = r26
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            r3.append(r0)     // Catch:{ Exception -> 0x045e }
            r5 = r23
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            java.lang.String r5 = im.bclpbkiauv.messenger.FileLoader.getDocumentFileName(r2)     // Catch:{ Exception -> 0x045e }
            java.lang.String r5 = java.net.URLEncoder.encode(r5, r11)     // Catch:{ Exception -> 0x045e }
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            r5 = r22
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            byte[] r5 = r2.file_reference     // Catch:{ Exception -> 0x045e }
            if (r5 == 0) goto L_0x042d
            byte[] r5 = r2.file_reference     // Catch:{ Exception -> 0x045e }
            goto L_0x0431
        L_0x042d:
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x045e }
            r5 = r6
        L_0x0431:
            java.lang.String r5 = im.bclpbkiauv.messenger.Utilities.bytesToHex(r5)     // Catch:{ Exception -> 0x045e }
            r3.append(r5)     // Catch:{ Exception -> 0x045e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x045e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x045e }
            r5.<init>()     // Catch:{ Exception -> 0x045e }
            r6 = r20
            r5.append(r6)     // Catch:{ Exception -> 0x045e }
            java.lang.String r6 = r31.getFileName()     // Catch:{ Exception -> 0x045e }
            r5.append(r6)     // Catch:{ Exception -> 0x045e }
            r5.append(r3)     // Catch:{ Exception -> 0x045e }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x045e }
            android.net.Uri r5 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x045e }
            im.bclpbkiauv.ui.components.VideoPlayer r6 = r8.videoPlayer     // Catch:{ Exception -> 0x045e }
            r6.preparePlayer(r5, r12)     // Catch:{ Exception -> 0x045e }
            goto L_0x0465
        L_0x045e:
            r0 = move-exception
            goto L_0x0462
        L_0x0460:
            r0 = move-exception
            r4 = r15
        L_0x0462:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0465:
            boolean r0 = r31.isRoundVideo()
            if (r0 == 0) goto L_0x0483
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer
            boolean r2 = r8.useFrontSpeaker
            if (r2 == 0) goto L_0x0473
            r2 = 0
            goto L_0x0474
        L_0x0473:
            r2 = 3
        L_0x0474:
            r0.setStreamType(r2)
            float r0 = r8.currentPlaybackSpeed
            int r2 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x0489
            im.bclpbkiauv.ui.components.VideoPlayer r2 = r8.videoPlayer
            r2.setPlaybackSpeed(r0)
            goto L_0x0489
        L_0x0483:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer
            r2 = 3
            r0.setStreamType(r2)
        L_0x0489:
        L_0x048a:
            r30.checkAudioFocus(r31)
            r30.setPlayerVolume()
            r2 = 0
            r8.isPaused = r2
            r2 = 0
            r8.lastProgress = r2
            r2 = r31
            r8.playingMessageObject = r2
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.raiseToSpeak
            if (r0 != 0) goto L_0x04a4
            im.bclpbkiauv.ui.ChatActivity r0 = r8.raiseChat
            r8.startRaiseToEarSensors(r0)
        L_0x04a4:
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject
            r8.startProgressTimer(r0)
            int r0 = r2.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r0 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r0)
            int r3 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingDidStart
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 0
            r6[r5] = r2
            r0.postNotificationName(r3, r6)
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer
            r5 = 1000(0x3e8, double:4.94E-321)
            r10 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r3 = 2
            if (r0 == 0) goto L_0x0531
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            float r0 = r0.audioProgress     // Catch:{ Exception -> 0x0501 }
            r7 = 0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x0500
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer     // Catch:{ Exception -> 0x0501 }
            long r12 = r0.getDuration()     // Catch:{ Exception -> 0x0501 }
            int r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x04e2
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x0501 }
            long r10 = (long) r0     // Catch:{ Exception -> 0x0501 }
            long r12 = r10 * r5
        L_0x04e2:
            float r0 = (float) r12     // Catch:{ Exception -> 0x0501 }
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            float r5 = r5.audioProgress     // Catch:{ Exception -> 0x0501 }
            float r0 = r0 * r5
            int r0 = (int) r0     // Catch:{ Exception -> 0x0501 }
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            int r5 = r5.audioProgressMs     // Catch:{ Exception -> 0x0501 }
            if (r5 == 0) goto L_0x04fa
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            int r5 = r5.audioProgressMs     // Catch:{ Exception -> 0x0501 }
            r0 = r5
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject     // Catch:{ Exception -> 0x0501 }
            r6 = 0
            r5.audioProgressMs = r6     // Catch:{ Exception -> 0x0501 }
        L_0x04fa:
            im.bclpbkiauv.ui.components.VideoPlayer r5 = r8.videoPlayer     // Catch:{ Exception -> 0x0501 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x0501 }
            r5.seekTo(r6)     // Catch:{ Exception -> 0x0501 }
        L_0x0500:
            goto L_0x052b
        L_0x0501:
            r0 = move-exception
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject
            r6 = 0
            r5.audioProgress = r6
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject
            r6 = 0
            r5.audioProgressSec = r6
            int r5 = r2.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r5)
            int r7 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingProgressDidChanged
            java.lang.Object[] r3 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.MessageObject r10 = r8.playingMessageObject
            int r10 = r10.getId()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r3[r6] = r10
            r6 = 1
            r3[r6] = r19
            r5.postNotificationName(r7, r3)
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x052b:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.videoPlayer
            r0.play()
            goto L_0x0586
        L_0x0531:
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.audioPlayer
            if (r0 == 0) goto L_0x0586
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject     // Catch:{ Exception -> 0x0560 }
            float r0 = r0.audioProgress     // Catch:{ Exception -> 0x0560 }
            r7 = 0
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 == 0) goto L_0x055f
            im.bclpbkiauv.ui.components.VideoPlayer r0 = r8.audioPlayer     // Catch:{ Exception -> 0x0560 }
            long r12 = r0.getDuration()     // Catch:{ Exception -> 0x0560 }
            int r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0551
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject     // Catch:{ Exception -> 0x0560 }
            int r0 = r0.getDuration()     // Catch:{ Exception -> 0x0560 }
            long r10 = (long) r0     // Catch:{ Exception -> 0x0560 }
            long r12 = r10 * r5
        L_0x0551:
            float r0 = (float) r12     // Catch:{ Exception -> 0x0560 }
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject     // Catch:{ Exception -> 0x0560 }
            float r5 = r5.audioProgress     // Catch:{ Exception -> 0x0560 }
            float r0 = r0 * r5
            int r0 = (int) r0     // Catch:{ Exception -> 0x0560 }
            im.bclpbkiauv.ui.components.VideoPlayer r5 = r8.audioPlayer     // Catch:{ Exception -> 0x0560 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x0560 }
            r5.seekTo(r6)     // Catch:{ Exception -> 0x0560 }
        L_0x055f:
            goto L_0x0586
        L_0x0560:
            r0 = move-exception
            im.bclpbkiauv.messenger.MessageObject r5 = r8.playingMessageObject
            r5.resetPlayingProgress()
            int r5 = r2.currentAccount
            im.bclpbkiauv.messenger.NotificationCenter r5 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r5)
            int r6 = im.bclpbkiauv.messenger.NotificationCenter.messagePlayingProgressDidChanged
            java.lang.Object[] r3 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.MessageObject r7 = r8.playingMessageObject
            int r7 = r7.getId()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r10 = 0
            r3[r10] = r7
            r7 = 1
            r3[r7] = r19
            r5.postNotificationName(r6, r3)
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0586:
            im.bclpbkiauv.messenger.MessageObject r0 = r8.playingMessageObject
            if (r0 == 0) goto L_0x05a5
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x05a5
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.MusicPlayerService> r5 = im.bclpbkiauv.messenger.MusicPlayerService.class
            r0.<init>(r3, r5)
            r3 = r0
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05a0 }
            r0.startService(r3)     // Catch:{ all -> 0x05a0 }
            goto L_0x05a4
        L_0x05a0:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05a4:
            goto L_0x05b3
        L_0x05a5:
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.MusicPlayerService> r5 = im.bclpbkiauv.messenger.MusicPlayerService.class
            r0.<init>(r3, r5)
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r3.stopService(r0)
        L_0x05b3:
            r3 = 1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.playMessage(im.bclpbkiauv.messenger.MessageObject):boolean");
    }

    public /* synthetic */ void lambda$playMessage$9$MediaController() {
        cleanupPlayer(true, true);
    }

    public AudioInfo getAudioInfo() {
        return this.audioInfo;
    }

    public void toggleShuffleMusic(int type) {
        boolean oldShuffle = SharedConfig.shuffleMusic;
        SharedConfig.toggleShuffleMusic(type);
        if (oldShuffle == SharedConfig.shuffleMusic) {
            return;
        }
        if (SharedConfig.shuffleMusic) {
            buildShuffledPlayList();
            this.currentPlaylistNum = 0;
            return;
        }
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null) {
            int indexOf = this.playlist.indexOf(messageObject);
            this.currentPlaylistNum = indexOf;
            if (indexOf == -1) {
                this.playlist.clear();
                this.shuffledPlaylist.clear();
                cleanupPlayer(true, true);
            }
        }
    }

    public boolean isCurrentPlayer(VideoPlayer player) {
        return this.videoPlayer == player || this.audioPlayer == player;
    }

    /* renamed from: pauseMessage */
    public boolean lambda$startAudioAgain$5$MediaController(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        stopProgressTimer();
        try {
            if (this.audioPlayer != null) {
                this.audioPlayer.pause();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.pause();
            }
            this.isPaused = true;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.isPaused = false;
            return false;
        }
    }

    public boolean resumeAudio(MessageObject messageObject) {
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || this.playingMessageObject == null || !isSamePlayingMessage(messageObject)) {
            return false;
        }
        try {
            startProgressTimer(this.playingMessageObject);
            if (this.audioPlayer != null) {
                this.audioPlayer.play();
            } else if (this.videoPlayer != null) {
                this.videoPlayer.play();
            }
            checkAudioFocus(messageObject);
            this.isPaused = false;
            NotificationCenter.getInstance(this.playingMessageObject.currentAccount).postNotificationName(NotificationCenter.messagePlayingPlayStateChanged, Integer.valueOf(this.playingMessageObject.getId()));
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public boolean isVideoDrawingReady() {
        AspectRatioFrameLayout aspectRatioFrameLayout = this.currentAspectRatioFrameLayout;
        return aspectRatioFrameLayout != null && aspectRatioFrameLayout.isDrawingReady();
    }

    public ArrayList<MessageObject> getPlaylist() {
        return this.playlist;
    }

    public boolean isPlayingMessage(MessageObject messageObject) {
        MessageObject messageObject2;
        if ((this.audioPlayer == null && this.videoPlayer == null) || messageObject == null || (messageObject2 = this.playingMessageObject) == null) {
            return false;
        }
        if (messageObject2.eventId != 0 && this.playingMessageObject.eventId == messageObject.eventId) {
            return !this.downloadingCurrentMessage;
        }
        if (isSamePlayingMessage(messageObject)) {
            return !this.downloadingCurrentMessage;
        }
        return false;
    }

    public boolean isPlayingMessageAndReadyToDraw(MessageObject messageObject) {
        return this.isDrawingWasReady && isPlayingMessage(messageObject);
    }

    public boolean isMessagePaused() {
        return this.isPaused || this.downloadingCurrentMessage;
    }

    public boolean isDownloadingCurrentMessage() {
        return this.downloadingCurrentMessage;
    }

    public void setReplyingMessage(MessageObject reply_to_msg) {
        this.recordReplyingMessageObject = reply_to_msg;
    }

    public void startRecording(int currentAccount, long dialog_id, MessageObject reply_to_msg, int guid) {
        boolean paused = false;
        MessageObject messageObject = this.playingMessageObject;
        if (messageObject != null && isPlayingMessage(messageObject) && !isMessagePaused()) {
            paused = true;
            lambda$startAudioAgain$5$MediaController(this.playingMessageObject);
        }
        try {
            this.feedbackView.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        DispatchQueue dispatchQueue = this.recordQueue;
        $$Lambda$MediaController$iyPmWz_LS5GN8C0YBHzGEI54EUI r2 = new Runnable(currentAccount, guid, dialog_id, reply_to_msg) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ MessageObject f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
            }

            public final void run() {
                MediaController.this.lambda$startRecording$16$MediaController(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        this.recordStartRunnable = r2;
        dispatchQueue.postRunnable(r2, paused ? 500 : 50);
    }

    public /* synthetic */ void lambda$startRecording$16$MediaController(int currentAccount, int guid, long dialog_id, MessageObject reply_to_msg) {
        if (this.audioRecorder != null) {
            AndroidUtilities.runOnUIThread(new Runnable(currentAccount, guid) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$12$MediaController(this.f$1, this.f$2);
                }
            });
            return;
        }
        this.sendAfterDone = 0;
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        this.recordingAudio = tL_document;
        this.recordingGuid = guid;
        tL_document.file_reference = new byte[0];
        this.recordingAudio.dc_id = Integer.MIN_VALUE;
        this.recordingAudio.id = (long) SharedConfig.getLastLocalId();
        this.recordingAudio.user_id = UserConfig.getInstance(currentAccount).getClientUserId();
        this.recordingAudio.mime_type = "audio/ogg";
        this.recordingAudio.file_reference = new byte[0];
        SharedConfig.saveConfig();
        File file = new File(FileLoader.getDirectory(4), FileLoader.getAttachFileName(this.recordingAudio));
        this.recordingAudioFile = file;
        try {
            if (startRecord(file.getAbsolutePath()) == 0) {
                AndroidUtilities.runOnUIThread(new Runnable(currentAccount, guid) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        MediaController.this.lambda$null$13$MediaController(this.f$1, this.f$2);
                    }
                });
                return;
            }
            this.audioRecorder = new AudioRecord(0, AudioEditConstant.ExportSampleRate, 16, 2, this.recordBufferSize * 10);
            this.recordStartTime = System.currentTimeMillis();
            this.recordTimeCount = 0;
            this.samplesCount = 0;
            this.recordDialogId = dialog_id;
            this.recordingCurrentAccount = currentAccount;
            this.recordReplyingMessageObject = reply_to_msg;
            this.fileBuffer.rewind();
            this.audioRecorder.startRecording();
            this.recordQueue.postRunnable(this.recordRunnable);
            AndroidUtilities.runOnUIThread(new Runnable(currentAccount, guid) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$15$MediaController(this.f$1, this.f$2);
                }
            });
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.recordingAudio = null;
            stopRecord();
            this.recordingAudioFile.delete();
            this.recordingAudioFile = null;
            try {
                this.audioRecorder.release();
                this.audioRecorder = null;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            AndroidUtilities.runOnUIThread(new Runnable(currentAccount, guid) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MediaController.this.lambda$null$14$MediaController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$MediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    public /* synthetic */ void lambda$null$13$MediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    public /* synthetic */ void lambda$null$14$MediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStartError, Integer.valueOf(guid));
    }

    public /* synthetic */ void lambda$null$15$MediaController(int currentAccount, int guid) {
        this.recordStartRunnable = null;
        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.recordStarted, Integer.valueOf(guid));
    }

    public void generateWaveform(MessageObject messageObject) {
        String id = messageObject.getId() + "_" + messageObject.getDialogId();
        String path = FileLoader.getPathToMessage(messageObject.messageOwner).getAbsolutePath();
        if (!this.generatingWaveform.containsKey(id)) {
            this.generatingWaveform.put(id, messageObject);
            Utilities.globalQueue.postRunnable(new Runnable(path, id, messageObject) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ MessageObject f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    MediaController.this.lambda$generateWaveform$18$MediaController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$generateWaveform$18$MediaController(String path, String id, MessageObject messageObject) {
        AndroidUtilities.runOnUIThread(new Runnable(id, getWaveform(path), messageObject) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ byte[] f$2;
            private final /* synthetic */ MessageObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaController.this.lambda$null$17$MediaController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$MediaController(String id, byte[] waveform, MessageObject messageObject) {
        MessageObject messageObject1 = this.generatingWaveform.remove(id);
        if (messageObject1 != null && waveform != null) {
            int a = 0;
            while (true) {
                if (a >= messageObject1.getDocument().attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute attribute = messageObject1.getDocument().attributes.get(a);
                if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                    attribute.waveform = waveform;
                    attribute.flags |= 4;
                    break;
                }
                a++;
            }
            TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
            messagesRes.messages.add(messageObject1.messageOwner);
            MessagesStorage.getInstance(messageObject1.currentAccount).putMessages((TLRPC.messages_Messages) messagesRes, messageObject1.getDialogId(), -1, 0, false, messageObject.scheduled);
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(messageObject1);
            NotificationCenter.getInstance(messageObject1.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject1.getDialogId()), arrayList);
        }
    }

    /* access modifiers changed from: private */
    public void stopRecordingInternal(int send, boolean notify, int scheduleDate) {
        if (send != 0) {
            this.fileEncodingQueue.postRunnable(new Runnable(this.recordingAudio, this.recordingAudioFile, send, notify, scheduleDate) {
                private final /* synthetic */ TLRPC.TL_document f$1;
                private final /* synthetic */ File f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ int f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    MediaController.this.lambda$stopRecordingInternal$20$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        } else {
            File file = this.recordingAudioFile;
            if (file != null) {
                file.delete();
            }
        }
        try {
            if (this.audioRecorder != null) {
                this.audioRecorder.release();
                this.audioRecorder = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.recordingAudio = null;
        this.recordingAudioFile = null;
    }

    public /* synthetic */ void lambda$stopRecordingInternal$20$MediaController(TLRPC.TL_document audioToSend, File recordingAudioFileToSend, int send, boolean notify, int scheduleDate) {
        stopRecord();
        AndroidUtilities.runOnUIThread(new Runnable(audioToSend, recordingAudioFileToSend, send, notify, scheduleDate) {
            private final /* synthetic */ TLRPC.TL_document f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                MediaController.this.lambda$null$19$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$MediaController(TLRPC.TL_document audioToSend, File recordingAudioFileToSend, int send, boolean notify, int scheduleDate) {
        char c;
        TLRPC.TL_document tL_document = audioToSend;
        int i = send;
        tL_document.date = ConnectionsManager.getInstance(this.recordingCurrentAccount).getCurrentTime();
        tL_document.size = (int) recordingAudioFileToSend.length();
        TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
        attributeAudio.voice = true;
        short[] sArr = this.recordSamples;
        attributeAudio.waveform = getWaveform2(sArr, sArr.length);
        if (attributeAudio.waveform != null) {
            attributeAudio.flags |= 4;
        }
        long duration = this.recordTimeCount;
        attributeAudio.duration = (int) (this.recordTimeCount / 1000);
        tL_document.attributes.add(attributeAudio);
        if (duration > 700) {
            if (i == 1) {
                long j = duration;
                c = 1;
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = attributeAudio;
                SendMessagesHelper.getInstance(this.recordingCurrentAccount).sendMessage(audioToSend, (VideoEditedInfo) null, recordingAudioFileToSend.getAbsolutePath(), this.recordDialogId, this.recordReplyingMessageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, 0, (Object) null);
            } else {
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio2 = attributeAudio;
                c = 1;
            }
            NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
            int i2 = NotificationCenter.audioDidSent;
            Object[] objArr = new Object[3];
            objArr[0] = Integer.valueOf(this.recordingGuid);
            String str = null;
            int i3 = send;
            objArr[c] = i3 == 2 ? audioToSend : null;
            if (i3 == 2) {
                str = recordingAudioFileToSend.getAbsolutePath();
            }
            objArr[2] = str;
            instance.postNotificationName(i2, objArr);
            return;
        }
        TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio3 = attributeAudio;
        int i4 = i;
        NotificationCenter.getInstance(this.recordingCurrentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Integer.valueOf(this.recordingGuid), false);
        recordingAudioFileToSend.delete();
    }

    public void stopRecording(int send, boolean notify, int scheduleDate) {
        Runnable runnable = this.recordStartRunnable;
        if (runnable != null) {
            this.recordQueue.cancelRunnable(runnable);
            this.recordStartRunnable = null;
        }
        this.recordQueue.postRunnable(new Runnable(send, notify, scheduleDate) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                MediaController.this.lambda$stopRecording$22$MediaController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$stopRecording$22$MediaController(int send, boolean notify, int scheduleDate) {
        if (this.sendAfterDone == 3) {
            this.sendAfterDone = 0;
            stopRecordingInternal(send, notify, scheduleDate);
            return;
        }
        AudioRecord audioRecord = this.audioRecorder;
        if (audioRecord != null) {
            try {
                this.sendAfterDone = send;
                this.sendAfterDoneNotify = notify;
                this.sendAfterDoneScheduleDate = scheduleDate;
                audioRecord.stop();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                File file = this.recordingAudioFile;
                if (file != null) {
                    file.delete();
                }
            }
            if (send == 0) {
                stopRecordingInternal(0, false, 0);
            }
            try {
                this.feedbackView.performHapticFeedback(3, 2);
            } catch (Exception e2) {
            }
            AndroidUtilities.runOnUIThread(new Runnable(send) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    MediaController.this.lambda$null$21$MediaController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$21$MediaController(int send) {
        NotificationCenter instance = NotificationCenter.getInstance(this.recordingCurrentAccount);
        int i = NotificationCenter.recordStopped;
        Object[] objArr = new Object[2];
        int i2 = 0;
        objArr[0] = Integer.valueOf(this.recordingGuid);
        if (send == 2) {
            i2 = 1;
        }
        objArr[1] = Integer.valueOf(i2);
        instance.postNotificationName(i, objArr);
    }

    public static void saveFile(String fullPath, Context context, int type, String name, String mime) {
        File file;
        AlertDialog progressDialog;
        String str = fullPath;
        Context context2 = context;
        if (str != null) {
            if (str == null || fullPath.length() == 0) {
                file = null;
            } else {
                File file2 = new File(fullPath);
                if (!file2.exists() || AndroidUtilities.isInternalUri(Uri.fromFile(file2))) {
                    file = null;
                } else {
                    file = file2;
                }
            }
            if (file != null) {
                File sourceFile = file;
                boolean[] cancelled = {false};
                if (sourceFile.exists()) {
                    AlertDialog progressDialog2 = null;
                    if (context2 == null || type == 0) {
                        progressDialog = null;
                    } else {
                        try {
                            progressDialog2 = new AlertDialog(context, 2);
                            progressDialog2.setMessage(LocaleController.getString("Loading", R.string.Loading));
                            progressDialog2.setCanceledOnTouchOutside(false);
                            progressDialog2.setCancelable(true);
                            progressDialog2.setOnCancelListener(new DialogInterface.OnCancelListener(cancelled) {
                                private final /* synthetic */ boolean[] f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void onCancel(DialogInterface dialogInterface) {
                                    MediaController.lambda$saveFile$23(this.f$0, dialogInterface);
                                }
                            });
                            progressDialog2.show();
                            progressDialog = progressDialog2;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            progressDialog = progressDialog2;
                        }
                    }
                    new Thread(new Runnable(type, name, sourceFile, cancelled, progressDialog, mime) {
                        private final /* synthetic */ int f$0;
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ File f$2;
                        private final /* synthetic */ boolean[] f$3;
                        private final /* synthetic */ AlertDialog f$4;
                        private final /* synthetic */ String f$5;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            MediaController.lambda$saveFile$26(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    }).start();
                }
            }
        }
    }

    static /* synthetic */ void lambda$saveFile$23(boolean[] cancelled, DialogInterface dialog) {
        cancelled[0] = true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0153  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:120:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x014d A[Catch:{ Exception -> 0x018b }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$saveFile$26(int r22, java.lang.String r23, java.io.File r24, boolean[] r25, im.bclpbkiauv.ui.actionbar.AlertDialog r26, java.lang.String r27) {
        /*
            r1 = r22
            r2 = r23
            r3 = r26
            r4 = 2
            r5 = 0
            if (r1 != 0) goto L_0x0016
            java.io.File r0 = im.bclpbkiauv.messenger.AndroidUtilities.generatePicturePath()     // Catch:{ Exception -> 0x0011 }
            r6 = r0
            goto L_0x0099
        L_0x0011:
            r0 = move-exception
            r12 = r24
            goto L_0x018e
        L_0x0016:
            r0 = 1
            if (r1 != r0) goto L_0x0020
            java.io.File r0 = im.bclpbkiauv.messenger.AndroidUtilities.generateVideoPath()     // Catch:{ Exception -> 0x0011 }
            r6 = r0
            goto L_0x0099
        L_0x0020:
            if (r1 != r4) goto L_0x0029
            java.lang.String r0 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x0011 }
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)     // Catch:{ Exception -> 0x0011 }
            goto L_0x002f
        L_0x0029:
            java.lang.String r0 = android.os.Environment.DIRECTORY_MUSIC     // Catch:{ Exception -> 0x0011 }
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)     // Catch:{ Exception -> 0x0011 }
        L_0x002f:
            r0.mkdir()     // Catch:{ Exception -> 0x0011 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0011 }
            r6.<init>(r0, r2)     // Catch:{ Exception -> 0x0011 }
            boolean r7 = r6.exists()     // Catch:{ Exception -> 0x0011 }
            if (r7 == 0) goto L_0x0099
            r7 = 46
            int r7 = r2.lastIndexOf(r7)     // Catch:{ Exception -> 0x0011 }
            r8 = 0
        L_0x0044:
            r9 = 10
            if (r8 >= r9) goto L_0x0099
            r9 = -1
            java.lang.String r10 = ")"
            java.lang.String r11 = "("
            if (r7 == r9) goto L_0x0072
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0011 }
            r9.<init>()     // Catch:{ Exception -> 0x0011 }
            java.lang.String r12 = r2.substring(r5, r7)     // Catch:{ Exception -> 0x0011 }
            r9.append(r12)     // Catch:{ Exception -> 0x0011 }
            r9.append(r11)     // Catch:{ Exception -> 0x0011 }
            int r11 = r8 + 1
            r9.append(r11)     // Catch:{ Exception -> 0x0011 }
            r9.append(r10)     // Catch:{ Exception -> 0x0011 }
            java.lang.String r10 = r2.substring(r7)     // Catch:{ Exception -> 0x0011 }
            r9.append(r10)     // Catch:{ Exception -> 0x0011 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0011 }
            goto L_0x0089
        L_0x0072:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0011 }
            r9.<init>()     // Catch:{ Exception -> 0x0011 }
            r9.append(r2)     // Catch:{ Exception -> 0x0011 }
            r9.append(r11)     // Catch:{ Exception -> 0x0011 }
            int r11 = r8 + 1
            r9.append(r11)     // Catch:{ Exception -> 0x0011 }
            r9.append(r10)     // Catch:{ Exception -> 0x0011 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0011 }
        L_0x0089:
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x0011 }
            r10.<init>(r0, r9)     // Catch:{ Exception -> 0x0011 }
            r6 = r10
            boolean r10 = r6.exists()     // Catch:{ Exception -> 0x0011 }
            if (r10 != 0) goto L_0x0096
            goto L_0x0099
        L_0x0096:
            int r8 = r8 + 1
            goto L_0x0044
        L_0x0099:
            boolean r0 = r6.exists()     // Catch:{ Exception -> 0x0011 }
            if (r0 != 0) goto L_0x00a2
            r6.createNewFile()     // Catch:{ Exception -> 0x0011 }
        L_0x00a2:
            r7 = 1
            long r8 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0011 }
            r10 = 500(0x1f4, double:2.47E-321)
            long r8 = r8 - r10
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0141 }
            r12 = r24
            r0.<init>(r12)     // Catch:{ Exception -> 0x013f }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x013f }
            r19 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ all -> 0x012e }
            r0.<init>(r6)     // Catch:{ all -> 0x012e }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ all -> 0x012e }
            r20 = r0
            long r13 = r19.size()     // Catch:{ all -> 0x011d }
            r14 = r13
            r16 = 0
            r10 = r16
        L_0x00cb:
            int r0 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r0 >= 0) goto L_0x0111
            boolean r0 = r25[r5]     // Catch:{ all -> 0x011d }
            if (r0 == 0) goto L_0x00d4
            goto L_0x0112
        L_0x00d4:
            long r4 = r14 - r10
            r0 = 4096(0x1000, double:2.0237E-320)
            long r17 = java.lang.Math.min(r0, r4)     // Catch:{ all -> 0x011d }
            r13 = r20
            r4 = r14
            r14 = r19
            r15 = r10
            r13.transferFrom(r14, r15, r17)     // Catch:{ all -> 0x011d }
            if (r3 == 0) goto L_0x0108
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x011d }
            r15 = 500(0x1f4, double:2.47E-321)
            long r13 = r13 - r15
            int r17 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r17 > 0) goto L_0x010a
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x011d }
            r8 = r13
            float r13 = (float) r10     // Catch:{ all -> 0x011d }
            float r14 = (float) r4     // Catch:{ all -> 0x011d }
            float r13 = r13 / r14
            r14 = 1120403456(0x42c80000, float:100.0)
            float r13 = r13 * r14
            int r13 = (int) r13     // Catch:{ all -> 0x011d }
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$z_Bag33Fz6Xv9EMKSudRUa90OSk r14 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$z_Bag33Fz6Xv9EMKSudRUa90OSk     // Catch:{ all -> 0x011d }
            r14.<init>(r13)     // Catch:{ all -> 0x011d }
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r14)     // Catch:{ all -> 0x011d }
            goto L_0x010a
        L_0x0108:
            r15 = 500(0x1f4, double:2.47E-321)
        L_0x010a:
            long r10 = r10 + r0
            r1 = r22
            r14 = r4
            r4 = 2
            r5 = 0
            goto L_0x00cb
        L_0x0111:
            r4 = r14
        L_0x0112:
            if (r20 == 0) goto L_0x0117
            r20.close()     // Catch:{ all -> 0x012e }
        L_0x0117:
            if (r19 == 0) goto L_0x011c
            r19.close()     // Catch:{ Exception -> 0x013f }
        L_0x011c:
            goto L_0x0148
        L_0x011d:
            r0 = move-exception
            r1 = r0
            throw r1     // Catch:{ all -> 0x0120 }
        L_0x0120:
            r0 = move-exception
            r4 = r0
            if (r20 == 0) goto L_0x012d
            r20.close()     // Catch:{ all -> 0x0128 }
            goto L_0x012d
        L_0x0128:
            r0 = move-exception
            r5 = r0
            r1.addSuppressed(r5)     // Catch:{ all -> 0x012e }
        L_0x012d:
            throw r4     // Catch:{ all -> 0x012e }
        L_0x012e:
            r0 = move-exception
            r1 = r0
            throw r1     // Catch:{ all -> 0x0131 }
        L_0x0131:
            r0 = move-exception
            r4 = r0
            if (r19 == 0) goto L_0x013e
            r19.close()     // Catch:{ all -> 0x0139 }
            goto L_0x013e
        L_0x0139:
            r0 = move-exception
            r5 = r0
            r1.addSuppressed(r5)     // Catch:{ Exception -> 0x013f }
        L_0x013e:
            throw r4     // Catch:{ Exception -> 0x013f }
        L_0x013f:
            r0 = move-exception
            goto L_0x0144
        L_0x0141:
            r0 = move-exception
            r12 = r24
        L_0x0144:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x018b }
            r7 = 0
        L_0x0148:
            r1 = 0
            boolean r0 = r25[r1]     // Catch:{ Exception -> 0x018b }
            if (r0 == 0) goto L_0x0151
            r6.delete()     // Catch:{ Exception -> 0x018b }
            r7 = 0
        L_0x0151:
            if (r7 == 0) goto L_0x0188
            r4 = 2
            r1 = r22
            if (r1 != r4) goto L_0x017e
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0186 }
            java.lang.String r4 = "download"
            java.lang.Object r0 = r0.getSystemService(r4)     // Catch:{ Exception -> 0x0186 }
            r13 = r0
            android.app.DownloadManager r13 = (android.app.DownloadManager) r13     // Catch:{ Exception -> 0x0186 }
            java.lang.String r14 = r6.getName()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r15 = r6.getName()     // Catch:{ Exception -> 0x0186 }
            r16 = 0
            java.lang.String r18 = r6.getAbsolutePath()     // Catch:{ Exception -> 0x0186 }
            long r19 = r6.length()     // Catch:{ Exception -> 0x0186 }
            r21 = 1
            r17 = r27
            r13.addCompletedDownload(r14, r15, r16, r17, r18, r19, r21)     // Catch:{ Exception -> 0x0186 }
            goto L_0x018a
        L_0x017e:
            android.net.Uri r0 = android.net.Uri.fromFile(r6)     // Catch:{ Exception -> 0x0186 }
            im.bclpbkiauv.messenger.AndroidUtilities.addMediaToGallery((android.net.Uri) r0)     // Catch:{ Exception -> 0x0186 }
            goto L_0x018a
        L_0x0186:
            r0 = move-exception
            goto L_0x018e
        L_0x0188:
            r1 = r22
        L_0x018a:
            goto L_0x0191
        L_0x018b:
            r0 = move-exception
            r1 = r22
        L_0x018e:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0191:
            if (r3 == 0) goto L_0x019b
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$ohrraUAA-f5alot_DCXLfwevqy0 r0 = new im.bclpbkiauv.messenger.-$$Lambda$MediaController$ohrraUAA-f5alot_DCXLfwevqy0
            r0.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x019b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.lambda$saveFile$26(int, java.lang.String, java.io.File, boolean[], im.bclpbkiauv.ui.actionbar.AlertDialog, java.lang.String):void");
    }

    static /* synthetic */ void lambda$null$24(AlertDialog finalProgress, int progress) {
        try {
            finalProgress.setProgress(progress);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ void lambda$null$25(AlertDialog finalProgress) {
        try {
            finalProgress.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static boolean isWebp(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream inputStream2 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[12];
            if (inputStream2.read(header, 0, 12) == 12) {
                String str = new String(header).toLowerCase();
                if (str.startsWith("riff") && str.endsWith("webp")) {
                    if (inputStream2 != null) {
                        try {
                            inputStream2.close();
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    }
                    return true;
                }
            }
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
        return false;
    }

    public static boolean isGif(Uri uri) {
        InputStream inputStream = null;
        try {
            InputStream inputStream2 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            byte[] header = new byte[3];
            if (inputStream2.read(header, 0, 3) != 3 || !new String(header).equalsIgnoreCase("gif")) {
                if (inputStream2 != null) {
                    try {
                        inputStream2.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                return false;
            }
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e22) {
                    FileLog.e((Throwable) e22);
                }
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0038, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0039, code lost:
        if (r2 != null) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0043, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getFileName(android.net.Uri r9) {
        /*
            java.lang.String r0 = "_display_name"
            r1 = 0
            java.lang.String r2 = r9.getScheme()
            java.lang.String r3 = "content"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0048
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0044 }
            android.content.ContentResolver r3 = r2.getContentResolver()     // Catch:{ Exception -> 0x0044 }
            java.lang.String[] r5 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x0044 }
            r6 = 0
            r7 = 0
            r8 = 0
            r4 = r9
            android.database.Cursor r2 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0044 }
            boolean r3 = r2.moveToFirst()     // Catch:{ all -> 0x0036 }
            if (r3 == 0) goto L_0x0030
            int r0 = r2.getColumnIndex(r0)     // Catch:{ all -> 0x0036 }
            java.lang.String r0 = r2.getString(r0)     // Catch:{ all -> 0x0036 }
            r1 = r0
        L_0x0030:
            if (r2 == 0) goto L_0x0035
            r2.close()     // Catch:{ Exception -> 0x0044 }
        L_0x0035:
            goto L_0x0048
        L_0x0036:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0038 }
        L_0x0038:
            r3 = move-exception
            if (r2 == 0) goto L_0x0043
            r2.close()     // Catch:{ all -> 0x003f }
            goto L_0x0043
        L_0x003f:
            r4 = move-exception
            r0.addSuppressed(r4)     // Catch:{ Exception -> 0x0044 }
        L_0x0043:
            throw r3     // Catch:{ Exception -> 0x0044 }
        L_0x0044:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0048:
            if (r1 != 0) goto L_0x005d
            java.lang.String r1 = r9.getPath()
            r0 = 47
            int r0 = r1.lastIndexOf(r0)
            r2 = -1
            if (r0 == r2) goto L_0x005d
            int r2 = r0 + 1
            java.lang.String r1 = r1.substring(r2)
        L_0x005d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.getFileName(android.net.Uri):java.lang.String");
    }

    public static String copyFileToCache(Uri uri, String ext) {
        InputStream inputStream = null;
        FileOutputStream output = null;
        try {
            String name = FileLoader.fixFileName(getFileName(uri));
            if (name == null) {
                int id = SharedConfig.getLastLocalId();
                SharedConfig.saveConfig();
                name = String.format(Locale.US, "%d.%s", new Object[]{Integer.valueOf(id), ext});
            }
            File f = new File(FileLoader.getDirectory(4), "sharing/");
            f.mkdirs();
            File f2 = new File(f, name);
            if (AndroidUtilities.isInternalUri(Uri.fromFile(f2))) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception e22) {
                        FileLog.e((Throwable) e22);
                    }
                }
                return null;
            }
            InputStream inputStream2 = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            FileOutputStream output2 = new FileOutputStream(f2);
            byte[] buffer = new byte[CacheDataSink.DEFAULT_BUFFER_SIZE];
            while (true) {
                int read = inputStream2.read(buffer);
                int len = read;
                if (read == -1) {
                    break;
                }
                output2.write(buffer, 0, len);
            }
            String absolutePath = f2.getAbsolutePath();
            if (inputStream2 != null) {
                try {
                    inputStream2.close();
                } catch (Exception e23) {
                    FileLog.e((Throwable) e23);
                }
            }
            try {
                output2.close();
            } catch (Exception e24) {
                FileLog.e((Throwable) e24);
            }
            return absolutePath;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e25) {
                    FileLog.e((Throwable) e25);
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e26) {
                    FileLog.e((Throwable) e26);
                }
            }
            return null;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e27) {
                    FileLog.e((Throwable) e27);
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e28) {
                    FileLog.e((Throwable) e28);
                }
            }
            throw th;
        }
    }

    public static void loadGalleryPhotosAlbums(int guid) {
        Thread thread = new Thread(new Runnable(guid) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                MediaController.lambda$loadGalleryPhotosAlbums$28(this.f$0);
            }
        });
        thread.setPriority(1);
        thread.start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:126:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0292 A[SYNTHETIC, Splitter:B:128:0x0292] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02ab A[SYNTHETIC, Splitter:B:137:0x02ab] */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x02bd A[Catch:{ all -> 0x044e }] */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02e2 A[Catch:{ all -> 0x044e }] */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x02e5 A[Catch:{ all -> 0x044e }] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02ee A[SYNTHETIC, Splitter:B:158:0x02ee] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x006a A[SYNTHETIC, Splitter:B:16:0x006a] */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0443 A[SYNTHETIC, Splitter:B:216:0x0443] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0456 A[SYNTHETIC, Splitter:B:225:0x0456] */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x046d A[LOOP:2: B:230:0x0467->B:232:0x046d, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x009c A[Catch:{ all -> 0x029d }] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x009f A[Catch:{ all -> 0x029d }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a8 A[SYNTHETIC, Splitter:B:32:0x00a8] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$loadGalleryPhotosAlbums$28(int r40) {
        /*
            java.lang.String r1 = "AllMedia"
            java.lang.String r2 = "datetaken"
            java.lang.String r3 = "_data"
            java.lang.String r4 = "bucket_display_name"
            java.lang.String r5 = "bucket_id"
            java.lang.String r6 = "_id"
            java.lang.String r7 = "datetaken DESC"
            java.lang.String r8 = "android.permission.READ_EXTERNAL_STORAGE"
            java.lang.String r9 = "date_modified"
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r13 = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r12 = r0
            r10 = 0
            r11 = 0
            r16 = 0
            r17 = 0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0054 }
            r0.<init>()     // Catch:{ Exception -> 0x0054 }
            java.lang.String r18 = android.os.Environment.DIRECTORY_DCIM     // Catch:{ Exception -> 0x0054 }
            java.io.File r18 = android.os.Environment.getExternalStoragePublicDirectory(r18)     // Catch:{ Exception -> 0x0054 }
            r19 = r2
            java.lang.String r2 = r18.getAbsolutePath()     // Catch:{ Exception -> 0x0052 }
            r0.append(r2)     // Catch:{ Exception -> 0x0052 }
            java.lang.String r2 = "/Camera/"
            r0.append(r2)     // Catch:{ Exception -> 0x0052 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0052 }
            r17 = r0
            r2 = r17
            goto L_0x005c
        L_0x0052:
            r0 = move-exception
            goto L_0x0057
        L_0x0054:
            r0 = move-exception
            r19 = r2
        L_0x0057:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r2 = r17
        L_0x005c:
            r17 = 0
            r18 = 0
            r20 = 0
            r21 = r7
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x029d }
            r7 = 23
            if (r0 < r7) goto L_0x0088
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x007f }
            if (r0 < r7) goto L_0x0077
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x007f }
            int r0 = r0.checkSelfPermission(r8)     // Catch:{ all -> 0x007f }
            if (r0 != 0) goto L_0x0077
            goto L_0x0088
        L_0x0077:
            r39 = r3
            r27 = r9
            r29 = r11
            goto L_0x0290
        L_0x007f:
            r0 = move-exception
            r39 = r3
            r27 = r9
            r29 = r11
            goto L_0x02a6
        L_0x0088:
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x029d }
            android.content.ContentResolver r24 = r0.getContentResolver()     // Catch:{ all -> 0x029d }
            android.net.Uri r25 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x029d }
            java.lang.String[] r26 = projectionPhotos     // Catch:{ all -> 0x029d }
            r27 = 0
            r28 = 0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x029d }
            r7 = 28
            if (r0 <= r7) goto L_0x009f
            r29 = r9
            goto L_0x00a1
        L_0x009f:
            r29 = r21
        L_0x00a1:
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r24, r25, r26, r27, r28, r29)     // Catch:{ all -> 0x029d }
            r7 = r0
            if (r7 == 0) goto L_0x0284
            int r0 = r7.getColumnIndex(r6)     // Catch:{ all -> 0x0276 }
            int r20 = r7.getColumnIndex(r5)     // Catch:{ all -> 0x0276 }
            r24 = r20
            int r20 = r7.getColumnIndex(r4)     // Catch:{ all -> 0x0276 }
            r25 = r20
            int r20 = r7.getColumnIndex(r3)     // Catch:{ all -> 0x0276 }
            r26 = r20
            r27 = r9
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0270 }
            r28 = r10
            r10 = 28
            if (r9 <= r10) goto L_0x00cb
            r9 = r27
            goto L_0x00cd
        L_0x00cb:
            r9 = r19
        L_0x00cd:
            int r9 = r7.getColumnIndex(r9)     // Catch:{ all -> 0x0264 }
            java.lang.String r10 = "orientation"
            int r10 = r7.getColumnIndex(r10)     // Catch:{ all -> 0x0264 }
        L_0x00d7:
            boolean r20 = r7.moveToNext()     // Catch:{ all -> 0x0264 }
            if (r20 == 0) goto L_0x0253
            int r31 = r7.getInt(r0)     // Catch:{ all -> 0x0264 }
            r20 = r0
            r0 = r24
            int r24 = r7.getInt(r0)     // Catch:{ all -> 0x0264 }
            r37 = r24
            r24 = r0
            r0 = r25
            java.lang.String r25 = r7.getString(r0)     // Catch:{ all -> 0x0264 }
            r38 = r25
            r25 = r0
            r0 = r26
            java.lang.String r26 = r7.getString(r0)     // Catch:{ all -> 0x0264 }
            r39 = r26
            long r32 = r7.getLong(r9)     // Catch:{ all -> 0x0264 }
            int r35 = r7.getInt(r10)     // Catch:{ all -> 0x0264 }
            r26 = r0
            r0 = r39
            if (r0 == 0) goto L_0x0237
            int r29 = r0.length()     // Catch:{ all -> 0x0264 }
            if (r29 != 0) goto L_0x011f
            r39 = r3
            r30 = r7
            r34 = r9
            r36 = r10
            r29 = r11
            goto L_0x0245
        L_0x011f:
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r39 = new im.bclpbkiauv.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x0264 }
            r36 = 0
            r29 = r39
            r30 = r37
            r34 = r0
            r29.<init>(r30, r31, r32, r34, r35, r36)     // Catch:{ all -> 0x0264 }
            r29 = r39
            if (r28 != 0) goto L_0x0161
            r30 = r7
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r7 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0156 }
            r34 = r9
            java.lang.String r9 = "AllPhotos"
            r36 = r10
            r10 = 2131689743(0x7f0f010f, float:1.900851E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r10)     // Catch:{ all -> 0x0156 }
            r10 = r29
            r29 = r11
            r11 = 0
            r7.<init>(r11, r9, r10)     // Catch:{ all -> 0x014d }
            r14.add(r11, r7)     // Catch:{ all -> 0x0191 }
            goto L_0x016d
        L_0x014d:
            r0 = move-exception
            r39 = r3
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x0156:
            r0 = move-exception
            r29 = r11
            r39 = r3
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x0161:
            r30 = r7
            r34 = r9
            r36 = r10
            r10 = r29
            r29 = r11
            r7 = r28
        L_0x016d:
            if (r16 != 0) goto L_0x0199
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r9 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0191 }
            r39 = r3
            r11 = 2131689742(0x7f0f010e, float:1.9008508E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r11)     // Catch:{ all -> 0x018b }
            r11 = 0
            r9.<init>(r11, r3, r10)     // Catch:{ all -> 0x018b }
            r3 = r9
            r15.add(r11, r3)     // Catch:{ all -> 0x0183 }
            goto L_0x019d
        L_0x0183:
            r0 = move-exception
            r16 = r3
            r10 = r7
            r20 = r30
            goto L_0x02a6
        L_0x018b:
            r0 = move-exception
            r10 = r7
            r20 = r30
            goto L_0x02a6
        L_0x0191:
            r0 = move-exception
            r39 = r3
            r10 = r7
            r20 = r30
            goto L_0x02a6
        L_0x0199:
            r39 = r3
            r3 = r16
        L_0x019d:
            r7.addPhoto(r10)     // Catch:{ all -> 0x022c }
            r3.addPhoto(r10)     // Catch:{ all -> 0x022c }
            r9 = r37
            java.lang.Object r11 = r13.get(r9)     // Catch:{ all -> 0x022c }
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r11 = (im.bclpbkiauv.messenger.MediaController.AlbumEntry) r11     // Catch:{ all -> 0x022c }
            if (r11 != 0) goto L_0x01e0
            r16 = r3
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r3 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x01d7 }
            r28 = r7
            r7 = r38
            r3.<init>(r9, r7, r10)     // Catch:{ all -> 0x0225 }
            r11 = r3
            r13.put(r9, r11)     // Catch:{ all -> 0x0225 }
            if (r17 != 0) goto L_0x01d3
            if (r2 == 0) goto L_0x01d3
            if (r0 == 0) goto L_0x01d3
            boolean r3 = r0.startsWith(r2)     // Catch:{ all -> 0x0225 }
            if (r3 == 0) goto L_0x01d3
            r3 = 0
            r15.add(r3, r11)     // Catch:{ all -> 0x0225 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0225 }
            r17 = r3
            goto L_0x01e6
        L_0x01d3:
            r15.add(r11)     // Catch:{ all -> 0x0225 }
            goto L_0x01e6
        L_0x01d7:
            r0 = move-exception
            r28 = r7
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x01e0:
            r16 = r3
            r28 = r7
            r7 = r38
        L_0x01e6:
            r11.addPhoto(r10)     // Catch:{ all -> 0x0225 }
            java.lang.Object r3 = r12.get(r9)     // Catch:{ all -> 0x0225 }
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r3 = (im.bclpbkiauv.messenger.MediaController.AlbumEntry) r3     // Catch:{ all -> 0x0225 }
            if (r3 != 0) goto L_0x0214
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r11 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x0225 }
            r11.<init>(r9, r7, r10)     // Catch:{ all -> 0x0225 }
            r3 = r11
            r12.put(r9, r3)     // Catch:{ all -> 0x0225 }
            if (r18 != 0) goto L_0x0211
            if (r2 == 0) goto L_0x0211
            if (r0 == 0) goto L_0x0211
            boolean r11 = r0.startsWith(r2)     // Catch:{ all -> 0x0225 }
            if (r11 == 0) goto L_0x0211
            r11 = 0
            r14.add(r11, r3)     // Catch:{ all -> 0x0225 }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0225 }
            r18 = r11
            goto L_0x0214
        L_0x0211:
            r14.add(r3)     // Catch:{ all -> 0x0225 }
        L_0x0214:
            r3.addPhoto(r10)     // Catch:{ all -> 0x0225 }
            r0 = r20
            r11 = r29
            r7 = r30
            r9 = r34
            r10 = r36
            r3 = r39
            goto L_0x00d7
        L_0x0225:
            r0 = move-exception
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x022c:
            r0 = move-exception
            r16 = r3
            r28 = r7
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x0237:
            r39 = r3
            r30 = r7
            r34 = r9
            r36 = r10
            r29 = r11
            r9 = r37
            r7 = r38
        L_0x0245:
            r0 = r20
            r11 = r29
            r7 = r30
            r9 = r34
            r10 = r36
            r3 = r39
            goto L_0x00d7
        L_0x0253:
            r20 = r0
            r39 = r3
            r30 = r7
            r34 = r9
            r36 = r10
            r29 = r11
            r10 = r28
            r20 = r30
            goto L_0x0290
        L_0x0264:
            r0 = move-exception
            r39 = r3
            r30 = r7
            r29 = r11
            r10 = r28
            r20 = r30
            goto L_0x02a6
        L_0x0270:
            r0 = move-exception
            r39 = r3
            r30 = r7
            goto L_0x027d
        L_0x0276:
            r0 = move-exception
            r39 = r3
            r30 = r7
            r27 = r9
        L_0x027d:
            r28 = r10
            r29 = r11
            r20 = r30
            goto L_0x02a6
        L_0x0284:
            r39 = r3
            r30 = r7
            r27 = r9
            r28 = r10
            r29 = r11
            r20 = r30
        L_0x0290:
            if (r20 == 0) goto L_0x02b6
            r20.close()     // Catch:{ Exception -> 0x0296 }
            goto L_0x02ae
        L_0x0296:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02ae
        L_0x029d:
            r0 = move-exception
            r39 = r3
            r27 = r9
            r28 = r10
            r29 = r11
        L_0x02a6:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x04a3 }
            if (r20 == 0) goto L_0x02b6
            r20.close()     // Catch:{ Exception -> 0x02af }
        L_0x02ae:
            goto L_0x02b6
        L_0x02af:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02ae
        L_0x02b6:
            r3 = r10
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x044e }
            r7 = 23
            if (r0 < r7) goto L_0x02ce
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x044e }
            if (r0 < r7) goto L_0x02ca
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x044e }
            int r0 = r0.checkSelfPermission(r8)     // Catch:{ all -> 0x044e }
            if (r0 != 0) goto L_0x02ca
            goto L_0x02ce
        L_0x02ca:
            r11 = r29
            goto L_0x0441
        L_0x02ce:
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x044e }
            android.content.ContentResolver r30 = r0.getContentResolver()     // Catch:{ all -> 0x044e }
            android.net.Uri r31 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI     // Catch:{ all -> 0x044e }
            java.lang.String[] r32 = projectionVideo     // Catch:{ all -> 0x044e }
            r33 = 0
            r34 = 0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x044e }
            r7 = 28
            if (r0 <= r7) goto L_0x02e5
            r35 = r27
            goto L_0x02e7
        L_0x02e5:
            r35 = r21
        L_0x02e7:
            android.database.Cursor r0 = android.provider.MediaStore.Images.Media.query(r30, r31, r32, r33, r34, r35)     // Catch:{ all -> 0x044e }
            r7 = r0
            if (r7 == 0) goto L_0x043b
            int r0 = r7.getColumnIndex(r6)     // Catch:{ all -> 0x0433 }
            int r5 = r7.getColumnIndex(r5)     // Catch:{ all -> 0x0433 }
            int r4 = r7.getColumnIndex(r4)     // Catch:{ all -> 0x0433 }
            r6 = r39
            int r6 = r7.getColumnIndex(r6)     // Catch:{ all -> 0x0433 }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0433 }
            r9 = 28
            if (r8 <= r9) goto L_0x0309
            r8 = r27
            goto L_0x030b
        L_0x0309:
            r8 = r19
        L_0x030b:
            int r8 = r7.getColumnIndex(r8)     // Catch:{ all -> 0x0433 }
            java.lang.String r9 = "duration"
            int r9 = r7.getColumnIndex(r9)     // Catch:{ all -> 0x0433 }
            r11 = r29
        L_0x0317:
            boolean r10 = r7.moveToNext()     // Catch:{ all -> 0x042d }
            if (r10 == 0) goto L_0x0420
            int r26 = r7.getInt(r0)     // Catch:{ all -> 0x042d }
            int r10 = r7.getInt(r5)     // Catch:{ all -> 0x042d }
            java.lang.String r19 = r7.getString(r4)     // Catch:{ all -> 0x042d }
            r20 = r19
            java.lang.String r19 = r7.getString(r6)     // Catch:{ all -> 0x042d }
            r21 = r19
            long r27 = r7.getLong(r8)     // Catch:{ all -> 0x042d }
            long r22 = r7.getLong(r9)     // Catch:{ all -> 0x042d }
            r19 = r0
            r0 = r21
            if (r0 == 0) goto L_0x0405
            int r21 = r0.length()     // Catch:{ all -> 0x042d }
            if (r21 != 0) goto L_0x0352
            r25 = r1
            r33 = r4
            r32 = r5
            r21 = r6
            r24 = r7
            r7 = 0
            goto L_0x0412
        L_0x0352:
            im.bclpbkiauv.messenger.MediaController$PhotoEntry r21 = new im.bclpbkiauv.messenger.MediaController$PhotoEntry     // Catch:{ all -> 0x042d }
            r24 = 1000(0x3e8, double:4.94E-321)
            r33 = r4
            r32 = r5
            long r4 = r22 / r24
            int r5 = (int) r4     // Catch:{ all -> 0x042d }
            r31 = 1
            r24 = r21
            r25 = r10
            r29 = r0
            r30 = r5
            r24.<init>(r25, r26, r27, r29, r30, r31)     // Catch:{ all -> 0x042d }
            r4 = r21
            if (r11 != 0) goto L_0x0392
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r5 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x042d }
            r21 = r6
            java.lang.String r6 = "AllVideos"
            r24 = r7
            r7 = 2131689747(0x7f0f0113, float:1.9008518E38)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x03a9 }
            r7 = 0
            r5.<init>(r7, r6, r4)     // Catch:{ all -> 0x03a9 }
            r11 = r5
            r5 = 1
            r11.videoOnly = r5     // Catch:{ all -> 0x03a9 }
            r5 = 0
            if (r16 == 0) goto L_0x038a
            int r5 = r5 + 1
        L_0x038a:
            if (r3 == 0) goto L_0x038e
            int r5 = r5 + 1
        L_0x038e:
            r15.add(r5, r11)     // Catch:{ all -> 0x03a9 }
            goto L_0x0396
        L_0x0392:
            r21 = r6
            r24 = r7
        L_0x0396:
            if (r16 != 0) goto L_0x03ae
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r5 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x03a9 }
            r6 = 2131689742(0x7f0f010e, float:1.9008508E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x03a9 }
            r6 = 0
            r5.<init>(r6, r7, r4)     // Catch:{ all -> 0x03a9 }
            r15.add(r6, r5)     // Catch:{ all -> 0x03ff }
            goto L_0x03b0
        L_0x03a9:
            r0 = move-exception
            r20 = r24
            goto L_0x0451
        L_0x03ae:
            r5 = r16
        L_0x03b0:
            r11.addPhoto(r4)     // Catch:{ all -> 0x03ff }
            r5.addPhoto(r4)     // Catch:{ all -> 0x03ff }
            java.lang.Object r6 = r13.get(r10)     // Catch:{ all -> 0x03ff }
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r6 = (im.bclpbkiauv.messenger.MediaController.AlbumEntry) r6     // Catch:{ all -> 0x03ff }
            if (r6 != 0) goto L_0x03e7
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r7 = new im.bclpbkiauv.messenger.MediaController$AlbumEntry     // Catch:{ all -> 0x03ff }
            r25 = r1
            r1 = r20
            r7.<init>(r10, r1, r4)     // Catch:{ all -> 0x03ff }
            r6 = r7
            r13.put(r10, r6)     // Catch:{ all -> 0x03ff }
            if (r17 != 0) goto L_0x03e2
            if (r2 == 0) goto L_0x03e2
            if (r0 == 0) goto L_0x03e2
            boolean r7 = r0.startsWith(r2)     // Catch:{ all -> 0x03ff }
            if (r7 == 0) goto L_0x03e2
            r7 = 0
            r15.add(r7, r6)     // Catch:{ all -> 0x03ff }
            java.lang.Integer r16 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x03ff }
            r17 = r16
            goto L_0x03ec
        L_0x03e2:
            r7 = 0
            r15.add(r6)     // Catch:{ all -> 0x03ff }
            goto L_0x03ec
        L_0x03e7:
            r25 = r1
            r1 = r20
            r7 = 0
        L_0x03ec:
            r6.addPhoto(r4)     // Catch:{ all -> 0x03ff }
            r16 = r5
            r0 = r19
            r6 = r21
            r7 = r24
            r1 = r25
            r5 = r32
            r4 = r33
            goto L_0x0317
        L_0x03ff:
            r0 = move-exception
            r16 = r5
            r20 = r24
            goto L_0x0451
        L_0x0405:
            r25 = r1
            r33 = r4
            r32 = r5
            r21 = r6
            r24 = r7
            r1 = r20
            r7 = 0
        L_0x0412:
            r0 = r19
            r6 = r21
            r7 = r24
            r1 = r25
            r5 = r32
            r4 = r33
            goto L_0x0317
        L_0x0420:
            r19 = r0
            r33 = r4
            r32 = r5
            r21 = r6
            r24 = r7
            r20 = r24
            goto L_0x0441
        L_0x042d:
            r0 = move-exception
            r24 = r7
            r20 = r24
            goto L_0x0451
        L_0x0433:
            r0 = move-exception
            r24 = r7
            r20 = r24
            r11 = r29
            goto L_0x0451
        L_0x043b:
            r24 = r7
            r20 = r24
            r11 = r29
        L_0x0441:
            if (r20 == 0) goto L_0x0461
            r20.close()     // Catch:{ Exception -> 0x0447 }
            goto L_0x0459
        L_0x0447:
            r0 = move-exception
            r1 = r0
            r0 = r1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0459
        L_0x044e:
            r0 = move-exception
            r11 = r29
        L_0x0451:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0490 }
            if (r20 == 0) goto L_0x0461
            r20.close()     // Catch:{ Exception -> 0x045a }
        L_0x0459:
            goto L_0x0461
        L_0x045a:
            r0 = move-exception
            r1 = r0
            r0 = r1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0459
        L_0x0461:
            r0 = r11
            r1 = r16
            r4 = r17
            r5 = 0
        L_0x0467:
            int r6 = r15.size()
            if (r5 >= r6) goto L_0x047d
            java.lang.Object r6 = r15.get(r5)
            im.bclpbkiauv.messenger.MediaController$AlbumEntry r6 = (im.bclpbkiauv.messenger.MediaController.AlbumEntry) r6
            java.util.ArrayList<im.bclpbkiauv.messenger.MediaController$PhotoEntry> r6 = r6.photos
            im.bclpbkiauv.messenger.-$$Lambda$MediaController$lXM3uremfGGdcL8CwPW5U3tZhCY r7 = im.bclpbkiauv.messenger.$$Lambda$MediaController$lXM3uremfGGdcL8CwPW5U3tZhCY.INSTANCE
            java.util.Collections.sort(r6, r7)
            int r5 = r5 + 1
            goto L_0x0467
        L_0x047d:
            r17 = 0
            r10 = r40
            r11 = r15
            r5 = r12
            r12 = r14
            r6 = r13
            r13 = r4
            r7 = r14
            r14 = r1
            r8 = r15
            r15 = r3
            r16 = r0
            broadcastNewPhotos(r10, r11, r12, r13, r14, r15, r16, r17)
            return
        L_0x0490:
            r0 = move-exception
            r5 = r12
            r6 = r13
            r7 = r14
            r8 = r15
            r1 = r0
            if (r20 == 0) goto L_0x04a2
            r20.close()     // Catch:{ Exception -> 0x049c }
            goto L_0x04a2
        L_0x049c:
            r0 = move-exception
            r4 = r0
            r0 = r4
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04a2:
            throw r1
        L_0x04a3:
            r0 = move-exception
            r5 = r12
            r6 = r13
            r7 = r14
            r8 = r15
            r1 = r0
            if (r20 == 0) goto L_0x04b5
            r20.close()     // Catch:{ Exception -> 0x04af }
            goto L_0x04b5
        L_0x04af:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04b5:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.lambda$loadGalleryPhotosAlbums$28(int):void");
    }

    static /* synthetic */ int lambda$null$27(PhotoEntry o1, PhotoEntry o2) {
        if (o1.dateTaken < o2.dateTaken) {
            return 1;
        }
        if (o1.dateTaken > o2.dateTaken) {
            return -1;
        }
        return 0;
    }

    private static void broadcastNewPhotos(int guid, ArrayList<AlbumEntry> mediaAlbumsSorted, ArrayList<AlbumEntry> photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal, int delay) {
        Runnable runnable = broadcastPhotosRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        $$Lambda$MediaController$OTM04VCNMb1eNcrNxrHDlVBeZXI r1 = new Runnable(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal) {
            private final /* synthetic */ int f$0;
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ Integer f$3;
            private final /* synthetic */ MediaController.AlbumEntry f$4;
            private final /* synthetic */ MediaController.AlbumEntry f$5;
            private final /* synthetic */ MediaController.AlbumEntry f$6;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MediaController.lambda$broadcastNewPhotos$29(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        };
        broadcastPhotosRunnable = r1;
        AndroidUtilities.runOnUIThread(r1, (long) delay);
    }

    static /* synthetic */ void lambda$broadcastNewPhotos$29(int guid, ArrayList mediaAlbumsSorted, ArrayList photoAlbumsSorted, Integer cameraAlbumIdFinal, AlbumEntry allMediaAlbumFinal, AlbumEntry allPhotosAlbumFinal, AlbumEntry allVideosAlbumFinal) {
        if (PhotoViewer.getInstance().isVisible()) {
            broadcastNewPhotos(guid, mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal, allMediaAlbumFinal, allPhotosAlbumFinal, allVideosAlbumFinal, 1000);
            return;
        }
        allMediaAlbums = mediaAlbumsSorted;
        allPhotoAlbums = photoAlbumsSorted;
        broadcastPhotosRunnable = null;
        allPhotosAlbumEntry = allPhotosAlbumFinal;
        allMediaAlbumEntry = allMediaAlbumFinal;
        allVideosAlbumEntry = allVideosAlbumFinal;
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).postNotificationName(NotificationCenter.albumsDidLoad, Integer.valueOf(guid), mediaAlbumsSorted, photoAlbumsSorted, cameraAlbumIdFinal);
        }
    }

    public void scheduleVideoConvert(MessageObject messageObject) {
        scheduleVideoConvert(messageObject, false);
    }

    public boolean scheduleVideoConvert(MessageObject messageObject, boolean isEmpty) {
        if (messageObject == null || messageObject.videoEditedInfo == null) {
            return false;
        }
        if (isEmpty && !this.videoConvertQueue.isEmpty()) {
            return false;
        }
        if (isEmpty) {
            new File(messageObject.messageOwner.attachPath).delete();
        }
        this.videoConvertQueue.add(messageObject);
        if (this.videoConvertQueue.size() == 1) {
            startVideoConvertFromQueue();
        }
        return true;
    }

    public void cancelVideoConvert(MessageObject messageObject) {
        if (messageObject == null) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = true;
            }
        } else if (!this.videoConvertQueue.isEmpty()) {
            int a = 0;
            while (a < this.videoConvertQueue.size()) {
                MessageObject object = this.videoConvertQueue.get(a);
                if (object.getId() != messageObject.getId() || object.currentAccount != messageObject.currentAccount) {
                    a++;
                } else if (a == 0) {
                    synchronized (this.videoConvertSync) {
                        this.cancelCurrentVideoConversion = true;
                    }
                    return;
                } else {
                    this.videoConvertQueue.remove(a);
                    return;
                }
            }
        }
    }

    private boolean startVideoConvertFromQueue() {
        if (this.videoConvertQueue.isEmpty()) {
            return false;
        }
        synchronized (this.videoConvertSync) {
            this.cancelCurrentVideoConversion = false;
        }
        MessageObject messageObject = this.videoConvertQueue.get(0);
        Intent intent = new Intent(ApplicationLoader.applicationContext, VideoEncodingService.class);
        intent.putExtra("path", messageObject.messageOwner.attachPath);
        intent.putExtra("currentAccount", messageObject.currentAccount);
        if (messageObject.messageOwner.media.document != null) {
            int a = 0;
            while (true) {
                if (a >= messageObject.messageOwner.media.document.attributes.size()) {
                    break;
                } else if (messageObject.messageOwner.media.document.attributes.get(a) instanceof TLRPC.TL_documentAttributeAnimated) {
                    intent.putExtra("gif", true);
                    break;
                } else {
                    a++;
                }
            }
        }
        if (messageObject.getId() != 0) {
            try {
                ApplicationLoader.applicationContext.startService(intent);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        VideoConvertRunnable.runConversion(messageObject);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        r1 = r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.media.MediaCodecInfo selectCodec(java.lang.String r10) {
        /*
            int r0 = android.media.MediaCodecList.getCodecCount()
            r1 = 0
            r2 = 0
        L_0x0006:
            if (r2 >= r0) goto L_0x0042
            android.media.MediaCodecInfo r3 = android.media.MediaCodecList.getCodecInfoAt(r2)
            boolean r4 = r3.isEncoder()
            if (r4 != 0) goto L_0x0013
            goto L_0x003f
        L_0x0013:
            java.lang.String[] r4 = r3.getSupportedTypes()
            int r5 = r4.length
            r6 = 0
        L_0x0019:
            if (r6 >= r5) goto L_0x003f
            r7 = r4[r6]
            boolean r8 = r7.equalsIgnoreCase(r10)
            if (r8 == 0) goto L_0x003c
            r1 = r3
            java.lang.String r8 = r1.getName()
            if (r8 == 0) goto L_0x003c
            java.lang.String r9 = "OMX.SEC.avc.enc"
            boolean r9 = r8.equals(r9)
            if (r9 != 0) goto L_0x0033
            return r1
        L_0x0033:
            java.lang.String r9 = "OMX.SEC.AVC.Encoder"
            boolean r9 = r8.equals(r9)
            if (r9 == 0) goto L_0x003c
            return r1
        L_0x003c:
            int r6 = r6 + 1
            goto L_0x0019
        L_0x003f:
            int r2 = r2 + 1
            goto L_0x0006
        L_0x0042:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.selectCodec(java.lang.String):android.media.MediaCodecInfo");
    }

    private static boolean isRecognizedFormat(int colorFormat) {
        if (colorFormat == 39 || colorFormat == 2130706688) {
            return true;
        }
        switch (colorFormat) {
            case 19:
            case 20:
            case 21:
                return true;
            default:
                return false;
        }
    }

    public static int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
        int lastColorFormat = 0;
        for (int colorFormat : capabilities.colorFormats) {
            if (isRecognizedFormat(colorFormat)) {
                lastColorFormat = colorFormat;
                if (!codecInfo.getName().equals("OMX.SEC.AVC.Encoder") || colorFormat != 19) {
                    return colorFormat;
                }
            }
        }
        return lastColorFormat;
    }

    private int findTrack(MediaExtractor extractor, boolean audio) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            String mime = extractor.getTrackFormat(i).getString("mime");
            if (audio) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            } else if (mime.startsWith("video/")) {
                return i;
            }
        }
        return -5;
    }

    private void didWriteData(MessageObject messageObject, File file, boolean last, long availableSize, boolean error) {
        boolean firstWrite = this.videoConvertFirstWrite;
        if (firstWrite) {
            this.videoConvertFirstWrite = false;
        }
        AndroidUtilities.runOnUIThread(new Runnable(error, last, messageObject, file, firstWrite, availableSize) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ MessageObject f$3;
            private final /* synthetic */ File f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ long f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                MediaController.this.lambda$didWriteData$30$MediaController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$didWriteData$30$MediaController(boolean error, boolean last, MessageObject messageObject, File file, boolean firstWrite, long availableSize) {
        if (error || last) {
            synchronized (this.videoConvertSync) {
                this.cancelCurrentVideoConversion = false;
            }
            this.videoConvertQueue.remove(messageObject);
            startVideoConvertFromQueue();
        }
        if (error) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingFailed, messageObject, file.toString());
            return;
        }
        if (firstWrite) {
            NotificationCenter.getInstance(messageObject.currentAccount).postNotificationName(NotificationCenter.filePreparingStarted, messageObject, file.toString());
        }
        NotificationCenter instance = NotificationCenter.getInstance(messageObject.currentAccount);
        int i = NotificationCenter.fileNewChunkAvailable;
        Object[] objArr = new Object[4];
        objArr[0] = messageObject;
        objArr[1] = file.toString();
        objArr[2] = Long.valueOf(availableSize);
        objArr[3] = Long.valueOf(last ? file.length() : 0);
        instance.postNotificationName(i, objArr);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e9, code lost:
        if (r2[r4 + 3] != 1) goto L_0x00ef;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(im.bclpbkiauv.messenger.MessageObject r35, android.media.MediaExtractor r36, im.bclpbkiauv.messenger.video.MP4Builder r37, android.media.MediaCodec.BufferInfo r38, long r39, long r41, java.io.File r43, boolean r44) throws java.lang.Exception {
        /*
            r34 = this;
            r7 = r34
            r8 = r36
            r9 = r37
            r10 = r38
            r11 = r39
            r13 = 0
            int r14 = r7.findTrack(r8, r13)
            r6 = 1
            if (r44 == 0) goto L_0x0017
            int r0 = r7.findTrack(r8, r6)
            goto L_0x0018
        L_0x0017:
            r0 = -1
        L_0x0018:
            r4 = r0
            r0 = -1
            r1 = -1
            r2 = 0
            r3 = 0
            java.lang.String r5 = "max-input-size"
            r6 = 0
            if (r14 < 0) goto L_0x003f
            r8.selectTrack(r14)
            android.media.MediaFormat r15 = r8.getTrackFormat(r14)
            int r0 = r9.addTrack(r15, r13)
            int r3 = r15.getInteger(r5)
            int r17 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r17 <= 0) goto L_0x003a
            r8.seekTo(r11, r13)
            goto L_0x003d
        L_0x003a:
            r8.seekTo(r6, r13)
        L_0x003d:
            r15 = r0
            goto L_0x0040
        L_0x003f:
            r15 = r0
        L_0x0040:
            if (r4 < 0) goto L_0x0067
            r8.selectTrack(r4)
            android.media.MediaFormat r0 = r8.getTrackFormat(r4)
            r13 = 1
            int r1 = r9.addTrack(r0, r13)
            int r5 = r0.getInteger(r5)
            int r3 = java.lang.Math.max(r5, r3)
            int r5 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x005f
            r5 = 0
            r8.seekTo(r11, r5)
            goto L_0x0063
        L_0x005f:
            r5 = 0
            r8.seekTo(r6, r5)
        L_0x0063:
            r13 = r1
            r18 = r3
            goto L_0x006a
        L_0x0067:
            r13 = r1
            r18 = r3
        L_0x006a:
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r18)
            r19 = -1
            if (r4 >= 0) goto L_0x0076
            if (r14 < 0) goto L_0x0075
            goto L_0x0076
        L_0x0075:
            return r19
        L_0x0076:
            r0 = -1
            r34.checkConversionCanceled()
            r21 = r2
        L_0x007d:
            if (r21 != 0) goto L_0x01f8
            r34.checkConversionCanceled()
            r2 = 0
            r3 = 0
            int r6 = r8.readSampleData(r5, r3)
            r10.size = r6
            int r7 = r36.getSampleTrackIndex()
            if (r7 != r14) goto L_0x0093
            r3 = r15
            r6 = r3
            goto L_0x009a
        L_0x0093:
            if (r7 != r4) goto L_0x0098
            r3 = r13
            r6 = r3
            goto L_0x009a
        L_0x0098:
            r3 = -1
            r6 = r3
        L_0x009a:
            r3 = -1
            if (r6 == r3) goto L_0x01cc
            int r3 = android.os.Build.VERSION.SDK_INT
            r24 = r2
            r2 = 21
            if (r3 >= r2) goto L_0x00ae
            r2 = 0
            r5.position(r2)
            int r2 = r10.size
            r5.limit(r2)
        L_0x00ae:
            if (r7 == r4) goto L_0x012e
            byte[] r2 = r5.array()
            if (r2 == 0) goto L_0x0127
            int r3 = r5.arrayOffset()
            int r25 = r5.limit()
            int r25 = r3 + r25
            r26 = -1
            r27 = r3
            r28 = r3
            r3 = r26
            r26 = r4
            r4 = r27
        L_0x00cc:
            r27 = r13
            int r13 = r25 + -4
            if (r4 > r13) goto L_0x0124
            byte r13 = r2[r4]
            if (r13 != 0) goto L_0x00ec
            int r13 = r4 + 1
            byte r13 = r2[r13]
            if (r13 != 0) goto L_0x00ec
            int r13 = r4 + 2
            byte r13 = r2[r13]
            if (r13 != 0) goto L_0x00ec
            int r13 = r4 + 3
            byte r13 = r2[r13]
            r29 = r15
            r15 = 1
            if (r13 == r15) goto L_0x00f3
            goto L_0x00ef
        L_0x00ec:
            r29 = r15
            r15 = 1
        L_0x00ef:
            int r13 = r25 + -4
            if (r4 != r13) goto L_0x011b
        L_0x00f3:
            r13 = -1
            if (r3 == r13) goto L_0x011a
            int r13 = r4 - r3
            int r15 = r25 + -4
            if (r4 == r15) goto L_0x00fe
            r15 = 4
            goto L_0x00ff
        L_0x00fe:
            r15 = 0
        L_0x00ff:
            int r13 = r13 - r15
            int r15 = r13 >> 24
            byte r15 = (byte) r15
            r2[r3] = r15
            int r15 = r3 + 1
            int r8 = r13 >> 16
            byte r8 = (byte) r8
            r2[r15] = r8
            int r8 = r3 + 2
            int r15 = r13 >> 8
            byte r15 = (byte) r15
            r2[r8] = r15
            int r8 = r3 + 3
            byte r15 = (byte) r13
            r2[r8] = r15
            r3 = r4
            goto L_0x011b
        L_0x011a:
            r3 = r4
        L_0x011b:
            int r4 = r4 + 1
            r8 = r36
            r13 = r27
            r15 = r29
            goto L_0x00cc
        L_0x0124:
            r29 = r15
            goto L_0x0134
        L_0x0127:
            r26 = r4
            r27 = r13
            r29 = r15
            goto L_0x0134
        L_0x012e:
            r26 = r4
            r27 = r13
            r29 = r15
        L_0x0134:
            int r2 = r10.size
            if (r2 < 0) goto L_0x013f
            long r2 = r36.getSampleTime()
            r10.presentationTimeUs = r2
            goto L_0x0145
        L_0x013f:
            r2 = 0
            r10.size = r2
            r2 = 1
            r24 = r2
        L_0x0145:
            int r2 = r10.size
            if (r2 <= 0) goto L_0x01b6
            if (r24 != 0) goto L_0x01b6
            if (r7 != r14) goto L_0x015c
            r2 = 0
            int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x015c
            int r2 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
            if (r2 != 0) goto L_0x015c
            long r0 = r10.presentationTimeUs
            r30 = r0
            goto L_0x015e
        L_0x015c:
            r30 = r0
        L_0x015e:
            r0 = 0
            int r2 = (r41 > r0 ? 1 : (r41 == r0 ? 0 : -1))
            if (r2 < 0) goto L_0x017b
            long r0 = r10.presentationTimeUs
            int r2 = (r0 > r41 ? 1 : (r0 == r41 ? 0 : -1))
            if (r2 >= 0) goto L_0x016b
            goto L_0x017b
        L_0x016b:
            r0 = 1
            r2 = r0
            r17 = r5
            r16 = r6
            r15 = r26
            r0 = r30
            r8 = 0
            r22 = 1
            r25 = 0
            goto L_0x01c3
        L_0x017b:
            r8 = 0
            r10.offset = r8
            int r0 = r36.getSampleFlags()
            r10.flags = r0
            long r32 = r9.writeSampleData(r6, r5, r10, r8)
            r22 = 0
            int r0 = (r32 > r22 ? 1 : (r32 == r22 ? 0 : -1))
            if (r0 == 0) goto L_0x01a7
            r3 = 0
            r13 = 0
            r0 = r34
            r1 = r35
            r2 = r43
            r17 = r5
            r15 = r26
            r4 = r32
            r16 = r6
            r25 = r22
            r22 = 1
            r6 = r13
            r0.didWriteData(r1, r2, r3, r4, r6)
            goto L_0x01b1
        L_0x01a7:
            r17 = r5
            r16 = r6
            r15 = r26
            r25 = r22
            r22 = 1
        L_0x01b1:
            r2 = r24
            r0 = r30
            goto L_0x01c3
        L_0x01b6:
            r17 = r5
            r16 = r6
            r15 = r26
            r8 = 0
            r22 = 1
            r25 = 0
            r2 = r24
        L_0x01c3:
            if (r2 != 0) goto L_0x01c8
            r36.advance()
        L_0x01c8:
            r24 = r2
            r2 = -1
            goto L_0x01e6
        L_0x01cc:
            r24 = r2
            r17 = r5
            r16 = r6
            r27 = r13
            r29 = r15
            r8 = 0
            r22 = 1
            r25 = 0
            r15 = r4
            r2 = -1
            if (r7 != r2) goto L_0x01e3
            r3 = 1
            r24 = r3
            goto L_0x01e6
        L_0x01e3:
            r36.advance()
        L_0x01e6:
            if (r24 == 0) goto L_0x01eb
            r3 = 1
            r21 = r3
        L_0x01eb:
            r8 = r36
            r4 = r15
            r5 = r17
            r6 = r25
            r13 = r27
            r15 = r29
            goto L_0x007d
        L_0x01f8:
            r17 = r5
            r27 = r13
            r29 = r15
            r15 = r4
            if (r14 < 0) goto L_0x0207
            r2 = r36
            r2.unselectTrack(r14)
            goto L_0x0209
        L_0x0207:
            r2 = r36
        L_0x0209:
            if (r15 < 0) goto L_0x020e
            r2.unselectTrack(r15)
        L_0x020e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.readAndWriteTracks(im.bclpbkiauv.messenger.MessageObject, android.media.MediaExtractor, im.bclpbkiauv.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, java.io.File, boolean):long");
    }

    private static class VideoConvertRunnable implements Runnable {
        private MessageObject messageObject;

        private VideoConvertRunnable(MessageObject message) {
            this.messageObject = message;
        }

        public void run() {
            boolean unused = MediaController.getInstance().convertVideo(this.messageObject);
        }

        public static void runConversion(MessageObject obj) {
            new Thread(new Runnable() {
                public final void run() {
                    MediaController.VideoConvertRunnable.lambda$runConversion$0(MessageObject.this);
                }
            }).start();
        }

        static /* synthetic */ void lambda$runConversion$0(MessageObject obj) {
            try {
                Thread th = new Thread(new VideoConvertRunnable(obj), "VideoConvertRunnable");
                th.start();
                th.join();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void checkConversionCanceled() {
        boolean cancelConversion;
        synchronized (this.videoConvertSync) {
            cancelConversion = this.cancelCurrentVideoConversion;
        }
        if (cancelConversion) {
            throw new RuntimeException("canceled conversion");
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: im.bclpbkiauv.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v123, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v168, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v215, resolved type: java.io.File} */
    /* JADX WARNING: type inference failed for: r3v98 */
    /* JADX WARNING: type inference failed for: r7v41 */
    /* JADX WARNING: type inference failed for: r7v44 */
    /* JADX WARNING: type inference failed for: r3v105 */
    /* JADX WARNING: type inference failed for: r7v64 */
    /* JADX WARNING: type inference failed for: r7v65 */
    /* JADX WARNING: type inference failed for: r7v103 */
    /* JADX WARNING: type inference failed for: r3v214 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x02e3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x02e4, code lost:
        r4 = r0;
        r49 = r9;
        r2 = r10;
        r89 = r13;
        r1 = r15;
        r10 = r35;
        r3 = r45;
        r8 = r47;
        r47 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x02f5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x02f6, code lost:
        r4 = r0;
        r49 = r9;
        r2 = r10;
        r89 = r13;
        r1 = r15;
        r10 = r35;
        r3 = r45;
        r91 = r47;
        r47 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x0594, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0595, code lost:
        r4 = r0;
        r2 = r10;
        r89 = r13;
        r1 = r15;
        r3 = r45;
        r47 = r65;
        r10 = r8;
        r8 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x05a3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x05a4, code lost:
        r5 = r0;
        r85 = r10;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r1 = null;
        r2 = null;
        r4 = null;
        r3 = null;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x05dc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x05dd, code lost:
        r5 = r0;
        r3 = r6;
        r85 = r10;
        r2 = r12;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r1 = null;
        r4 = null;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0630, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0631, code lost:
        r5 = r0;
        r1 = r6;
        r3 = r7;
        r85 = r10;
        r2 = r12;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r4 = null;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x068e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x068f, code lost:
        r5 = r0;
        r1 = r6;
        r85 = r10;
        r2 = r12;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r4 = r36;
        r3 = r50;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x073f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0740, code lost:
        r4 = r0;
        r2 = r10;
        r89 = r13;
        r1 = r15;
        r3 = r45;
        r47 = r65;
        r10 = r66;
        r8 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x074f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0750, code lost:
        r5 = r0;
        r1 = r6;
        r85 = r10;
        r2 = r12;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r4 = r36;
        r3 = r50;
        r47 = r65;
        r10 = r66;
        r91 = r67;
        r11 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x08c0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x08c1, code lost:
        r9 = r7;
        r89 = r13;
        r88 = r28;
        r47 = r65;
        r11 = r78;
        r5 = r0;
        r3 = r50;
        r85 = r10;
        r2 = r12;
        r4 = r36;
        r10 = r66;
        r91 = r67;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x08df, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x08e0, code lost:
        r89 = r13;
        r47 = r65;
        r4 = r0;
        r2 = r10;
        r1 = r15;
        r3 = r45;
        r10 = r66;
        r8 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0960, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0961, code lost:
        r4 = r0;
        r2 = r10;
        r1 = r15;
        r3 = r45;
        r10 = r66;
        r8 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x096c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x096d, code lost:
        r1 = r5;
        r3 = r8;
        r85 = r10;
        r2 = r12;
        r4 = r65;
        r10 = r66;
        r91 = r67;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x09f2, code lost:
        r29 = r6;
        r50 = r8;
        r2 = r24;
        r24 = r26;
        r25 = r27;
        r27 = r28;
        r30 = r36;
        r60 = r48;
        r36 = r65;
        r8 = r66;
        r1 = r79;
        r61 = r80;
        r59 = r81;
        r3 = r82;
        r58 = r84;
        r28 = r88;
        r13 = r89;
        r6 = r5;
        r66 = r11;
        r65 = r47;
        r11 = r78;
        r94 = r34;
        r34 = r9;
        r9 = r94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0bcd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0bce, code lost:
        r5 = r0;
        r3 = r8;
        r2 = r12;
        r4 = r65;
        r1 = r92;
        r91 = r67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0cc4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0cc5, code lost:
        r4 = r0;
        r1 = r15;
        r3 = r45;
        r8 = r67;
        r2 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0dc3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0dc4, code lost:
        r8 = r4;
        r1 = r15;
        r3 = r45;
        r2 = r85;
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0dcd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0dce, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:?, code lost:
        im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0e95, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0e96, code lost:
        r8 = r4;
        r1 = r15;
        r3 = r45;
        r2 = r85;
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0e9f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0ea0, code lost:
        r91 = r4;
        r5 = r0;
        r4 = r65;
        r1 = r7;
        r3 = r8;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0ecc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0ecd, code lost:
        r4 = r0;
        r1 = r15;
        r3 = r45;
        r8 = r67;
        r2 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0f0b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0f0c, code lost:
        r91 = r67;
        r5 = r0;
        r4 = r65;
        r1 = r92;
        r3 = r8;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0fc2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0fc3, code lost:
        r85 = r10;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
        r5 = r0;
        r4 = r36;
        r1 = r6;
        r3 = r50;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0fde, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0fdf, code lost:
        r3 = r4;
        r85 = r10;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
        r5 = r0;
        r1 = r6;
        r3 = r7;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x103d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x103e, code lost:
        r85 = r10;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
        r5 = r0;
        r3 = r7;
        r2 = r12;
        r1 = null;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x1057, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x1058, code lost:
        r85 = r10;
        r89 = r13;
        r88 = r28;
        r9 = r34;
        r47 = r65;
        r11 = r66;
        r91 = r67;
        r10 = r8;
        r5 = r0;
        r2 = r12;
        r1 = null;
        r4 = null;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x1071, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x1072, code lost:
        r85 = r10;
        r89 = r13;
        r47 = r65;
        r10 = r8;
        r4 = r0;
        r1 = r15;
        r3 = r45;
        r8 = r67;
        r2 = r85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x10cb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x10cc, code lost:
        r88 = r7;
        r49 = r9;
        r85 = r10;
        r89 = r13;
        r9 = r34;
        r10 = r35;
        r91 = r47;
        r47 = r12;
        r5 = r0;
        r1 = null;
        r2 = null;
        r4 = null;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x1101, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x1102, code lost:
        r49 = r9;
        r85 = r10;
        r89 = r13;
        r10 = r35;
        r91 = r47;
        r47 = r12;
        r4 = r0;
        r1 = r15;
        r3 = r45;
        r2 = r85;
        r8 = r91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x1118, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x1119, code lost:
        r88 = r7;
        r49 = r9;
        r85 = r10;
        r89 = r13;
        r9 = r34;
        r10 = r35;
        r91 = r47;
        r47 = r12;
        r5 = r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x02e3 A[ExcHandler: all (r0v83 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:98:0x02dd] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04df A[SYNTHETIC, Splitter:B:187:0x04df] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0508  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0520  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0566 A[Catch:{ Exception -> 0x1083, all -> 0x1071 }] */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0568 A[Catch:{ Exception -> 0x1083, all -> 0x1071 }] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0572 A[Catch:{ Exception -> 0x1083, all -> 0x1071 }] */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0575 A[Catch:{ Exception -> 0x1083, all -> 0x1071 }] */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0586  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0594 A[ExcHandler: all (r0v76 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r65 
      PHI: (r65v22 'rotateRender' int) = (r65v2 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int) binds: [B:264:0x0670, B:246:0x0628, B:247:?, B:229:0x05ce, B:230:?, B:231:0x05d7, B:232:?, B:216:0x0589, B:217:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:216:0x0589] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x05ce A[SYNTHETIC, Splitter:B:229:0x05ce] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0628 A[SYNTHETIC, Splitter:B:246:0x0628] */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0648 A[SYNTHETIC, Splitter:B:251:0x0648] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0670 A[SYNTHETIC, Splitter:B:264:0x0670] */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x06a7  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06b7 A[Catch:{ Exception -> 0x0fc2, all -> 0x1071 }] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x073f A[ExcHandler: all (r0v68 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r66 
      PHI: (r66v23 'resultHeight' int) = (r66v22 'resultHeight' int), (r66v22 'resultHeight' int), (r66v22 'resultHeight' int), (r66v22 'resultHeight' int), (r66v22 'resultHeight' int), (r66v22 'resultHeight' int), (r66v24 'resultHeight' int) binds: [B:328:0x07c7, B:329:?, B:318:0x07ad, B:319:?, B:311:0x0787, B:312:?, B:284:0x06d0] A[DONT_GENERATE, DONT_INLINE], Splitter:B:284:0x06d0] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08df A[ExcHandler: all (r0v64 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:304:0x0775] */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x0960 A[ExcHandler: all (r0v60 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r10 r66 r67 
      PHI: (r10v72 'extractor' android.media.MediaExtractor) = (r10v32 'extractor' android.media.MediaExtractor), (r10v32 'extractor' android.media.MediaExtractor), (r10v32 'extractor' android.media.MediaExtractor), (r10v30 'extractor' android.media.MediaExtractor), (r10v30 'extractor' android.media.MediaExtractor), (r10v30 'extractor' android.media.MediaExtractor) binds: [B:399:0x0aab, B:400:?, B:382:0x0a48, B:357:0x0948, B:339:0x084e, B:340:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r66v19 'resultHeight' int) = (r66v3 'resultHeight' int), (r66v3 'resultHeight' int), (r66v3 'resultHeight' int), (r66v21 'resultHeight' int), (r66v22 'resultHeight' int), (r66v22 'resultHeight' int) binds: [B:399:0x0aab, B:400:?, B:382:0x0a48, B:357:0x0948, B:339:0x084e, B:340:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r67v20 'startTime' long) = (r67v4 'startTime' long), (r67v4 'startTime' long), (r67v4 'startTime' long), (r67v3 'startTime' long), (r67v3 'startTime' long), (r67v3 'startTime' long) binds: [B:399:0x0aab, B:400:?, B:382:0x0a48, B:357:0x0948, B:339:0x084e, B:340:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:339:0x084e] */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0c34 A[Catch:{ Exception -> 0x0f0b, all -> 0x0ecc }] */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x0c36 A[Catch:{ Exception -> 0x0f0b, all -> 0x0ecc }] */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0cc4 A[ExcHandler: all (r0v38 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r85 
      PHI: (r85v21 'extractor' android.media.MediaExtractor) = (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v30 'extractor' android.media.MediaExtractor) binds: [B:517:0x0cf5, B:507:0x0ce6, B:508:?, B:491:0x0c95, B:448:0x0bad] A[DONT_GENERATE, DONT_INLINE], Splitter:B:448:0x0bad] */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0d2e  */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x0da7  */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0dbe  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0dc3 A[ExcHandler: all (r0v40 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r4 
      PHI: (r4v58 'startTime' long) = (r4v60 'startTime' long), (r4v60 'startTime' long), (r4v84 'startTime' long) binds: [B:562:0x0dbf, B:563:?, B:544:0x0d4e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:544:0x0d4e] */
    /* JADX WARNING: Removed duplicated region for block: B:589:0x0e4b A[Catch:{ Exception -> 0x0f70, all -> 0x0f65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0e57 A[Catch:{ Exception -> 0x0f70, all -> 0x0f65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0e8e A[Catch:{ Exception -> 0x0f70, all -> 0x0f65 }] */
    /* JADX WARNING: Removed duplicated region for block: B:603:0x0e95 A[Catch:{ Exception -> 0x0f70, all -> 0x0f65 }, ExcHandler: all (r0v42 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x0f70, all -> 0x0f65 }]), Splitter:B:558:0x0db9] */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0ecc A[Catch:{ Exception -> 0x0f70, all -> 0x0f65 }, ExcHandler: all (r0v37 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x0f70, all -> 0x0f65 }]), PHI: r85 
      PHI: (r85v20 'extractor' android.media.MediaExtractor) = (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v22 'extractor' android.media.MediaExtractor), (r85v25 'extractor' android.media.MediaExtractor) binds: [B:481:0x0c64, B:503:0x0ce0, B:504:?, B:513:0x0cf0, B:514:?, B:469:0x0c2e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:469:0x0c2e] */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x1071 A[ExcHandler: all (r0v27 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r65 r67 
      PHI: (r65v1 'rotateRender' int) = (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v2 'rotateRender' int), (r65v2 'rotateRender' int), (r65v2 'rotateRender' int), (r65v2 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int), (r65v0 'rotateRender' int) binds: [B:222:0x05be, B:225:0x05c5, B:226:?, B:239:0x0611, B:242:0x0620, B:243:?, B:257:0x0651, B:260:0x065b, B:261:?, B:273:0x06ac, B:251:0x0648, B:252:?, B:202:0x0555] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r67v1 'startTime' long) = (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v2 'startTime' long), (r67v21 'startTime' long) binds: [B:222:0x05be, B:225:0x05c5, B:226:?, B:239:0x0611, B:242:0x0620, B:243:?, B:257:0x0651, B:260:0x065b, B:261:?, B:273:0x06ac, B:251:0x0648, B:252:?, B:202:0x0555] A[DONT_GENERATE, DONT_INLINE], Splitter:B:222:0x05be] */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x1101 A[ExcHandler: all (r0v21 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:109:0x031f] */
    /* JADX WARNING: Removed duplicated region for block: B:667:0x1142 A[Catch:{ Exception -> 0x1168, all -> 0x115e }] */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x1147 A[Catch:{ Exception -> 0x1168, all -> 0x115e }] */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x114c A[Catch:{ Exception -> 0x1168, all -> 0x115e }] */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x1154 A[Catch:{ Exception -> 0x1168, all -> 0x115e }] */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x119c A[SYNTHETIC, Splitter:B:686:0x119c] */
    /* JADX WARNING: Removed duplicated region for block: B:692:0x11a9  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x12ff  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x1304 A[SYNTHETIC, Splitter:B:723:0x1304] */
    /* JADX WARNING: Removed duplicated region for block: B:729:0x1311  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x1350  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x1355 A[SYNTHETIC, Splitter:B:737:0x1355] */
    /* JADX WARNING: Removed duplicated region for block: B:743:0x1362  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean convertVideo(im.bclpbkiauv.messenger.MessageObject r97) {
        /*
            r96 = this;
            r12 = r96
            r13 = r97
            java.lang.String r14 = "time = "
            if (r13 == 0) goto L_0x13be
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r13.videoEditedInfo
            if (r1 != 0) goto L_0x0010
            r1 = 0
            goto L_0x13bf
        L_0x0010:
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r13.videoEditedInfo
            java.lang.String r1 = r1.originalPath
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = r13.videoEditedInfo
            long r10 = r2.startTime
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = r13.videoEditedInfo
            long r5 = r2.endTime
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = r13.videoEditedInfo
            int r2 = r2.resultWidth
            im.bclpbkiauv.messenger.VideoEditedInfo r3 = r13.videoEditedInfo
            int r3 = r3.resultHeight
            im.bclpbkiauv.messenger.VideoEditedInfo r4 = r13.videoEditedInfo
            int r4 = r4.rotationValue
            im.bclpbkiauv.messenger.VideoEditedInfo r7 = r13.videoEditedInfo
            int r15 = r7.originalWidth
            im.bclpbkiauv.messenger.VideoEditedInfo r7 = r13.videoEditedInfo
            int r9 = r7.originalHeight
            im.bclpbkiauv.messenger.VideoEditedInfo r7 = r13.videoEditedInfo
            int r7 = r7.framerate
            im.bclpbkiauv.messenger.VideoEditedInfo r8 = r13.videoEditedInfo
            int r8 = r8.bitrate
            r17 = 0
            r18 = r5
            long r5 = r97.getDialogId()
            int r6 = (int) r5
            if (r6 != 0) goto L_0x0045
            r6 = 1
            goto L_0x0046
        L_0x0045:
            r6 = 0
        L_0x0046:
            java.io.File r5 = new java.io.File
            r21 = r6
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r13.messageOwner
            java.lang.String r6 = r6.attachPath
            r5.<init>(r6)
            if (r1 != 0) goto L_0x0057
            java.lang.String r1 = ""
            r6 = r1
            goto L_0x0058
        L_0x0057:
            r6 = r1
        L_0x0058:
            int r1 = android.os.Build.VERSION.SDK_INT
            r22 = r7
            r7 = 18
            if (r1 >= r7) goto L_0x0076
            if (r3 <= r2) goto L_0x0076
            if (r2 == r15) goto L_0x0076
            if (r3 == r9) goto L_0x0076
            r1 = r3
            r3 = r2
            r2 = r1
            r4 = 90
            r17 = 270(0x10e, float:3.78E-43)
            r7 = r2
            r2 = r17
            r94 = r4
            r4 = r3
            r3 = r94
            goto L_0x00ba
        L_0x0076:
            int r1 = android.os.Build.VERSION.SDK_INT
            r7 = 20
            if (r1 <= r7) goto L_0x00b2
            r1 = 90
            if (r4 != r1) goto L_0x008f
            r1 = r3
            r3 = r2
            r2 = r1
            r4 = 0
            r17 = 270(0x10e, float:3.78E-43)
            r7 = r2
            r2 = r17
            r94 = r4
            r4 = r3
            r3 = r94
            goto L_0x00ba
        L_0x008f:
            r1 = 180(0xb4, float:2.52E-43)
            if (r4 != r1) goto L_0x009f
            r17 = 180(0xb4, float:2.52E-43)
            r4 = 0
            r7 = r2
            r2 = r17
            r94 = r4
            r4 = r3
            r3 = r94
            goto L_0x00ba
        L_0x009f:
            r1 = 270(0x10e, float:3.78E-43)
            if (r4 != r1) goto L_0x00b2
            r1 = r3
            r3 = r2
            r2 = r1
            r4 = 0
            r17 = 90
            r7 = r2
            r2 = r17
            r94 = r4
            r4 = r3
            r3 = r94
            goto L_0x00ba
        L_0x00b2:
            r7 = r2
            r2 = r17
            r94 = r4
            r4 = r3
            r3 = r94
        L_0x00ba:
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r17 = r2
            java.lang.String r2 = "videoconvert"
            r24 = r3
            r3 = 0
            android.content.SharedPreferences r2 = r1.getSharedPreferences(r2, r3)
            java.io.File r1 = new java.io.File
            r1.<init>(r6)
            r31 = r1
            int r1 = r97.getId()
            java.lang.String r3 = "isPreviousOk"
            if (r1 == 0) goto L_0x0141
            r1 = 1
            boolean r20 = r2.getBoolean(r3, r1)
            android.content.SharedPreferences$Editor r1 = r2.edit()
            r26 = r2
            r2 = 0
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r3, r2)
            r1.commit()
            boolean r1 = r31.canRead()
            if (r1 == 0) goto L_0x0108
            if (r20 != 0) goto L_0x00f3
            goto L_0x0108
        L_0x00f3:
            r35 = r4
            r36 = r5
            r37 = r6
            r4 = r14
            r32 = r17
            r5 = r21
            r21 = r22
            r33 = r24
            r17 = r26
            r6 = r3
            r14 = r7
            r7 = 1
            goto L_0x0155
        L_0x0108:
            r14 = 1
            r27 = 0
            r23 = 1
            r25 = 1
            r1 = r96
            r32 = r17
            r17 = r26
            r2 = r97
            r34 = r3
            r33 = r24
            r3 = r5
            r35 = r4
            r4 = r14
            r36 = r5
            r37 = r6
            r14 = r21
            r5 = r27
            r21 = r22
            r22 = r14
            r14 = r7
            r7 = r23
            r1.didWriteData(r2, r3, r4, r5, r7)
            android.content.SharedPreferences$Editor r1 = r17.edit()
            r6 = r34
            r7 = 1
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r6, r7)
            r1.commit()
            r1 = 0
            return r1
        L_0x0141:
            r35 = r4
            r36 = r5
            r37 = r6
            r4 = r14
            r32 = r17
            r5 = r21
            r21 = r22
            r33 = r24
            r17 = r2
            r6 = r3
            r14 = r7
            r7 = 1
        L_0x0155:
            r12.videoConvertFirstWrite = r7
            r20 = 0
            long r38 = java.lang.System.currentTimeMillis()
            if (r14 == 0) goto L_0x138f
            r3 = r35
            if (r3 == 0) goto L_0x137b
            r1 = 0
            r2 = 0
            android.media.MediaCodec$BufferInfo r22 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x12e0, all -> 0x12c6 }
            r22.<init>()     // Catch:{ Exception -> 0x12e0, all -> 0x12c6 }
            r34 = r22
            im.bclpbkiauv.messenger.video.Mp4Movie r22 = new im.bclpbkiauv.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x12e0, all -> 0x12c6 }
            r22.<init>()     // Catch:{ Exception -> 0x12e0, all -> 0x12c6 }
            r35 = r22
            r22 = r6
            r6 = r35
            r12 = r36
            r6.setCacheFile(r12)     // Catch:{ Exception -> 0x12ad, all -> 0x1293 }
            r36 = r12
            r12 = r33
            r6.setRotation(r12)     // Catch:{ Exception -> 0x1279, all -> 0x125f }
            r6.setSize(r14, r3)     // Catch:{ Exception -> 0x1279, all -> 0x125f }
            im.bclpbkiauv.messenger.video.MP4Builder r7 = new im.bclpbkiauv.messenger.video.MP4Builder     // Catch:{ Exception -> 0x1279, all -> 0x125f }
            r7.<init>()     // Catch:{ Exception -> 0x1279, all -> 0x125f }
            im.bclpbkiauv.messenger.video.MP4Builder r7 = r7.createMovie(r6, r5)     // Catch:{ Exception -> 0x1279, all -> 0x125f }
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x1243, all -> 0x1227 }
            r1.<init>()     // Catch:{ Exception -> 0x1243, all -> 0x1227 }
            r2 = r1
            r1 = r37
            r2.setDataSource(r1)     // Catch:{ Exception -> 0x120a, all -> 0x11ed }
            r96.checkConversionCanceled()     // Catch:{ Exception -> 0x120a, all -> 0x11ed }
            r40 = -1
            r24 = r4
            if (r14 != r15) goto L_0x02b3
            if (r3 != r9) goto L_0x02b3
            r4 = r32
            if (r4 != 0) goto L_0x0299
            r37 = r1
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r13.videoEditedInfo     // Catch:{ Exception -> 0x027a, all -> 0x025e }
            boolean r1 = r1.roundVideo     // Catch:{ Exception -> 0x027a, all -> 0x025e }
            if (r1 != 0) goto L_0x0243
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x027a, all -> 0x025e }
            r27 = r2
            r2 = 18
            if (r1 < r2) goto L_0x01d8
            int r1 = (r10 > r40 ? 1 : (r10 == r40 ? 0 : -1))
            if (r1 == 0) goto L_0x01d8
            r35 = r3
            r42 = r5
            r13 = r8
            r46 = r9
            r47 = r10
            r43 = r15
            r44 = r22
            r45 = r24
            r33 = r27
            r32 = r37
            r22 = r6
            r15 = r7
            r37 = r12
            r12 = r4
            goto L_0x02cd
        L_0x01d8:
            r1 = -1
            if (r8 == r1) goto L_0x01de
            r16 = 1
            goto L_0x01e0
        L_0x01de:
            r16 = 0
        L_0x01e0:
            r32 = r37
            r1 = r96
            r33 = r27
            r2 = r97
            r35 = r3
            r3 = r33
            r37 = r12
            r13 = r24
            r12 = r4
            r4 = r7
            r42 = r5
            r5 = r34
            r45 = r13
            r43 = r15
            r44 = r22
            r13 = 1
            r22 = r6
            r15 = r7
            r6 = r10
            r13 = r8
            r46 = r9
            r8 = r18
            r47 = r10
            r10 = r36
            r11 = r16
            r1.readAndWriteTracks(r2, r3, r4, r5, r6, r8, r10, r11)     // Catch:{ Exception -> 0x0230, all -> 0x021d }
            r89 = r13
            r1 = r33
            r10 = r35
            r49 = r36
            r91 = r47
            r47 = r12
            goto L_0x1196
        L_0x021d:
            r0 = move-exception
            r4 = r0
            r89 = r13
            r1 = r15
            r2 = r33
            r10 = r35
            r49 = r36
            r3 = r45
            r8 = r47
            r47 = r12
            goto L_0x134e
        L_0x0230:
            r0 = move-exception
            r4 = r0
            r89 = r13
            r1 = r15
            r2 = r33
            r10 = r35
            r49 = r36
            r3 = r45
            r91 = r47
            r47 = r12
            goto L_0x12f8
        L_0x0243:
            r33 = r2
            r35 = r3
            r42 = r5
            r13 = r8
            r46 = r9
            r47 = r10
            r43 = r15
            r44 = r22
            r45 = r24
            r32 = r37
            r22 = r6
            r15 = r7
            r37 = r12
            r12 = r4
            goto L_0x02cd
        L_0x025e:
            r0 = move-exception
            r33 = r2
            r42 = r5
            r46 = r9
            r43 = r15
            r32 = r37
            r15 = r7
            r37 = r12
            r47 = r4
            r89 = r8
            r8 = r10
            r1 = r15
            r49 = r36
            r4 = r0
            r10 = r3
            r3 = r24
            goto L_0x134e
        L_0x027a:
            r0 = move-exception
            r33 = r2
            r42 = r5
            r46 = r9
            r43 = r15
            r44 = r22
            r32 = r37
            r15 = r7
            r37 = r12
            r47 = r4
            r89 = r8
            r91 = r10
            r1 = r15
            r49 = r36
            r4 = r0
            r10 = r3
            r3 = r24
            goto L_0x12f8
        L_0x0299:
            r32 = r1
            r33 = r2
            r35 = r3
            r42 = r5
            r13 = r8
            r46 = r9
            r47 = r10
            r37 = r12
            r43 = r15
            r44 = r22
            r45 = r24
            r12 = r4
            r22 = r6
            r15 = r7
            goto L_0x02cd
        L_0x02b3:
            r33 = r2
            r35 = r3
            r42 = r5
            r13 = r8
            r46 = r9
            r47 = r10
            r37 = r12
            r43 = r15
            r44 = r22
            r45 = r24
            r12 = r32
            r32 = r1
            r22 = r6
            r15 = r7
        L_0x02cd:
            r1 = 0
            r8 = r96
            r10 = r33
            r9 = r36
            int r2 = r8.findTrack(r10, r1)     // Catch:{ Exception -> 0x11da, all -> 0x11c5 }
            r11 = r2
            r1 = -1
            if (r13 == r1) goto L_0x0307
            r1 = 1
            int r2 = r8.findTrack(r10, r1)     // Catch:{ Exception -> 0x02f5, all -> 0x02e3 }
            r1 = r2
            goto L_0x0308
        L_0x02e3:
            r0 = move-exception
            r4 = r0
            r49 = r9
            r2 = r10
            r89 = r13
            r1 = r15
            r10 = r35
            r3 = r45
            r8 = r47
            r47 = r12
            goto L_0x134e
        L_0x02f5:
            r0 = move-exception
            r4 = r0
            r49 = r9
            r2 = r10
            r89 = r13
            r1 = r15
            r10 = r35
            r3 = r45
            r91 = r47
            r47 = r12
            goto L_0x12f8
        L_0x0307:
            r1 = -1
        L_0x0308:
            r7 = r1
            if (r11 < 0) goto L_0x1186
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = -1
            r24 = 0
            r25 = 0
            r27 = 0
            r28 = 0
            r29 = -5
            r30 = -5
            r33 = 0
            java.lang.String r36 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x1118, all -> 0x1101 }
            java.lang.String r36 = r36.toLowerCase()     // Catch:{ Exception -> 0x1118, all -> 0x1101 }
            r49 = r36
            r36 = r1
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10e6, all -> 0x1101 }
            r50 = r2
            java.lang.String r2 = "lge"
            r51 = r4
            java.lang.String r4 = "video/avc"
            r52 = r3
            r3 = 18
            if (r1 >= r3) goto L_0x040e
            android.media.MediaCodecInfo r1 = selectCodec(r4)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            int r3 = selectColorFormat(r1, r4)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x03e4
            java.lang.String r54 = r1.getName()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r55 = r54
            r54 = r3
            java.lang.String r3 = "OMX.qcom."
            r56 = r5
            r5 = r55
            boolean r3 = r5.contains(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x0377
            r3 = 1
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r33 = r3
            r3 = 16
            if (r6 != r3) goto L_0x0374
            r6 = r49
            boolean r3 = r6.equals(r2)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 != 0) goto L_0x0371
            java.lang.String r3 = "nokia"
            boolean r3 = r6.equals(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x03aa
        L_0x0371:
            r28 = 1
            goto L_0x03aa
        L_0x0374:
            r6 = r49
            goto L_0x03aa
        L_0x0377:
            r6 = r49
            java.lang.String r3 = "OMX.Intel."
            boolean r3 = r5.contains(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x0385
            r3 = 2
            r33 = r3
            goto L_0x03aa
        L_0x0385:
            java.lang.String r3 = "OMX.MTK.VIDEO.ENCODER.AVC"
            boolean r3 = r5.equals(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x0391
            r3 = 3
            r33 = r3
            goto L_0x03aa
        L_0x0391:
            java.lang.String r3 = "OMX.SEC.AVC.Encoder"
            boolean r3 = r5.equals(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x039f
            r3 = 4
            r28 = 1
            r33 = r3
            goto L_0x03aa
        L_0x039f:
            java.lang.String r3 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r3 = r5.equals(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x03aa
            r3 = 5
            r33 = r3
        L_0x03aa:
            boolean r3 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            if (r3 == 0) goto L_0x03db
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r3.<init>()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r49 = r5
            java.lang.String r5 = "codec = "
            r3.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r5 = r1.getName()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r3.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r5 = " manufacturer = "
            r3.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r3.append(r6)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r5 = "device = "
            r3.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r5 = android.os.Build.MODEL     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r3.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            im.bclpbkiauv.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            goto L_0x03dd
        L_0x03db:
            r49 = r5
        L_0x03dd:
            r3 = r33
            r5 = r54
            r33 = r28
            goto L_0x041b
        L_0x03e4:
            r54 = r3
            r56 = r5
            r6 = r49
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r3 = "no supported color format"
            r2.<init>(r3)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            throw r2     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
        L_0x03f2:
            r0 = move-exception
            r5 = r0
            r88 = r7
            r49 = r9
            r85 = r10
            r89 = r13
            r9 = r34
            r10 = r35
            r1 = r36
            r91 = r47
            r2 = r50
            r4 = r51
            r3 = r52
            r47 = r12
            goto L_0x1132
        L_0x040e:
            r56 = r5
            r6 = r49
            r1 = 2130708361(0x7f000789, float:1.701803E38)
            r3 = r1
            r5 = r3
            r3 = r33
            r33 = r28
        L_0x041b:
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x10cb, all -> 0x1101 }
            if (r1 == 0) goto L_0x0433
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r1.<init>()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r8 = "colorFormat = "
            r1.append(r8)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            r1.append(r5)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
            im.bclpbkiauv.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x03f2, all -> 0x02e3 }
        L_0x0433:
            r1 = r35
            r8 = 0
            r28 = r8
            r8 = r35
            int r35 = r14 * r8
            r49 = r9
            r9 = 3
            int r35 = r35 * 3
            r9 = 2
            int r35 = r35 / 2
            if (r3 != 0) goto L_0x048a
            int r2 = r8 % 16
            if (r2 == 0) goto L_0x04cf
            int r2 = r8 % 16
            r53 = 16
            int r2 = 16 - r2
            int r1 = r1 + r2
            int r2 = r1 - r8
            int r2 = r2 * r14
            int r28 = r2 * 5
            int r28 = r28 / 4
            int r35 = r35 + r28
            r9 = r1
            r53 = r35
            r35 = r2
            goto L_0x04d4
        L_0x0462:
            r0 = move-exception
            r4 = r0
            r2 = r10
            r89 = r13
            r1 = r15
            r3 = r45
            r10 = r8
            r8 = r47
            r47 = r12
            goto L_0x134e
        L_0x0471:
            r0 = move-exception
            r5 = r0
            r88 = r7
            r85 = r10
            r89 = r13
            r9 = r34
            r1 = r36
            r91 = r47
            r2 = r50
            r4 = r51
            r3 = r52
            r10 = r8
            r47 = r12
            goto L_0x1132
        L_0x048a:
            r9 = 1
            if (r3 != r9) goto L_0x04a9
            java.lang.String r9 = r6.toLowerCase()     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            boolean r2 = r9.equals(r2)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            if (r2 != 0) goto L_0x04cf
            int r2 = r14 * r8
            int r2 = r2 + 2047
            r2 = r2 & -2048(0xfffffffffffff800, float:NaN)
            int r9 = r14 * r8
            int r9 = r2 - r9
            int r35 = r35 + r9
            r53 = r35
            r35 = r9
            r9 = r1
            goto L_0x04d4
        L_0x04a9:
            r2 = 5
            if (r3 != r2) goto L_0x04ad
            goto L_0x04cf
        L_0x04ad:
            r2 = 3
            if (r3 != r2) goto L_0x04cf
            java.lang.String r2 = "baidu"
            boolean r2 = r6.equals(r2)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            if (r2 == 0) goto L_0x04cf
            int r2 = r8 % 16
            r9 = 16
            int r2 = 16 - r2
            int r1 = r1 + r2
            int r2 = r1 - r8
            int r2 = r2 * r14
            int r9 = r2 * 5
            int r9 = r9 / 4
            int r35 = r35 + r9
            r9 = r1
            r53 = r35
            r35 = r2
            goto L_0x04d4
        L_0x04cf:
            r9 = r1
            r53 = r35
            r35 = r28
        L_0x04d4:
            r10.selectTrack(r11)     // Catch:{ Exception -> 0x10b2, all -> 0x109e }
            android.media.MediaFormat r1 = r10.getTrackFormat(r11)     // Catch:{ Exception -> 0x10b2, all -> 0x109e }
            r2 = r1
            r1 = 0
            if (r7 < 0) goto L_0x0508
            r10.selectTrack(r7)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            android.media.MediaFormat r28 = r10.getTrackFormat(r7)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            r58 = r28
            r28 = r1
            java.lang.String r1 = "max-input-size"
            r59 = r3
            r3 = r58
            int r1 = r3.getInteger(r1)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            java.nio.ByteBuffer r58 = java.nio.ByteBuffer.allocateDirect(r1)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            r28 = r58
            r58 = r1
            r1 = 1
            int r60 = r15.addTrack(r3, r1)     // Catch:{ Exception -> 0x0471, all -> 0x0462 }
            r30 = r60
            r3 = r28
            r1 = r30
            goto L_0x0510
        L_0x0508:
            r28 = r1
            r59 = r3
            r3 = r28
            r1 = r30
        L_0x0510:
            r30 = r6
            r28 = r7
            r6 = 0
            r66 = r11
            r65 = r12
            r11 = r47
            int r47 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r47 <= 0) goto L_0x0550
            r6 = 0
            r10.seekTo(r11, r6)     // Catch:{ Exception -> 0x0535, all -> 0x0527 }
            r67 = r11
            goto L_0x0558
        L_0x0527:
            r0 = move-exception
            r4 = r0
            r2 = r10
            r89 = r13
            r1 = r15
            r3 = r45
            r47 = r65
            r10 = r8
            r8 = r11
            goto L_0x134e
        L_0x0535:
            r0 = move-exception
            r5 = r0
            r85 = r10
            r91 = r11
            r89 = r13
            r88 = r28
            r9 = r34
            r1 = r36
            r2 = r50
            r4 = r51
            r3 = r52
            r47 = r65
            r11 = r66
            r10 = r8
            goto L_0x1132
        L_0x0550:
            r6 = 0
            r67 = r11
            r11 = 0
            r10.seekTo(r11, r6)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
        L_0x0558:
            android.media.MediaFormat r6 = android.media.MediaFormat.createVideoFormat(r4, r14, r8)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            r11 = r6
            java.lang.String r6 = "color-format"
            r11.setInteger(r6, r5)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            java.lang.String r6 = "bitrate"
            if (r13 <= 0) goto L_0x0568
            r7 = r13
            goto L_0x056b
        L_0x0568:
            r7 = 921600(0xe1000, float:1.291437E-39)
        L_0x056b:
            r11.setInteger(r6, r7)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            java.lang.String r6 = "frame-rate"
            if (r21 == 0) goto L_0x0575
            r7 = r21
            goto L_0x0577
        L_0x0575:
            r7 = 25
        L_0x0577:
            r11.setInteger(r6, r7)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            java.lang.String r6 = "i-frame-interval"
            r7 = 2
            r11.setInteger(r6, r7)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            r7 = 18
            if (r6 >= r7) goto L_0x05be
            java.lang.String r6 = "stride"
            int r7 = r14 + 32
            r11.setInteger(r6, r7)     // Catch:{ Exception -> 0x05a3, all -> 0x0594 }
            java.lang.String r6 = "slice-height"
            r11.setInteger(r6, r8)     // Catch:{ Exception -> 0x05a3, all -> 0x0594 }
            goto L_0x05be
        L_0x0594:
            r0 = move-exception
            r4 = r0
            r2 = r10
            r89 = r13
            r1 = r15
            r3 = r45
            r47 = r65
            r10 = r8
            r8 = r67
            goto L_0x134e
        L_0x05a3:
            r0 = move-exception
            r5 = r0
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r1 = r36
            r2 = r50
            r4 = r51
            r3 = r52
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            goto L_0x1132
        L_0x05be:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r4)     // Catch:{ Exception -> 0x1083, all -> 0x1071 }
            r12 = r6
            r6 = 0
            r7 = 1
            r12.configure(r11, r6, r6, r7)     // Catch:{ Exception -> 0x1057, all -> 0x1071 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1057, all -> 0x1071 }
            r6 = 18
            if (r7 < r6) goto L_0x060f
            im.bclpbkiauv.messenger.video.InputSurface r6 = new im.bclpbkiauv.messenger.video.InputSurface     // Catch:{ Exception -> 0x05f5, all -> 0x0594 }
            android.view.Surface r7 = r12.createInputSurface()     // Catch:{ Exception -> 0x05f5, all -> 0x0594 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x05f5, all -> 0x0594 }
            r6.makeCurrent()     // Catch:{ Exception -> 0x05dc, all -> 0x0594 }
            r7 = r6
            goto L_0x0611
        L_0x05dc:
            r0 = move-exception
            r5 = r0
            r3 = r6
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r9 = r34
            r1 = r36
            r4 = r51
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            goto L_0x1132
        L_0x05f5:
            r0 = move-exception
            r5 = r0
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r9 = r34
            r1 = r36
            r4 = r51
            r3 = r52
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            goto L_0x1132
        L_0x060f:
            r7 = r52
        L_0x0611:
            r12.start()     // Catch:{ Exception -> 0x103d, all -> 0x1071 }
            java.lang.String r6 = "mime"
            java.lang.String r6 = r2.getString(r6)     // Catch:{ Exception -> 0x103d, all -> 0x1071 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createDecoderByType(r6)     // Catch:{ Exception -> 0x103d, all -> 0x1071 }
            r58 = r4
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1023, all -> 0x1071 }
            r60 = r5
            r5 = 18
            if (r4 < r5) goto L_0x0648
            im.bclpbkiauv.messenger.video.OutputSurface r4 = new im.bclpbkiauv.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0630, all -> 0x0594 }
            r4.<init>()     // Catch:{ Exception -> 0x0630, all -> 0x0594 }
            r5 = r65
            goto L_0x064f
        L_0x0630:
            r0 = move-exception
            r5 = r0
            r1 = r6
            r3 = r7
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r9 = r34
            r4 = r51
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            goto L_0x1132
        L_0x0648:
            im.bclpbkiauv.messenger.video.OutputSurface r4 = new im.bclpbkiauv.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1023, all -> 0x1071 }
            r5 = r65
            r4.<init>(r14, r8, r5)     // Catch:{ Exception -> 0x1009, all -> 0x0ff7 }
        L_0x064f:
            r65 = r5
            android.view.Surface r5 = r4.getSurface()     // Catch:{ Exception -> 0x0fde, all -> 0x1071 }
            r36 = r4
            r50 = r7
            r4 = 0
            r7 = 0
            r6.configure(r2, r5, r4, r7)     // Catch:{ Exception -> 0x0fc2, all -> 0x1071 }
            r6.start()     // Catch:{ Exception -> 0x0fc2, all -> 0x1071 }
            r51 = 2500(0x9c4, float:3.503E-42)
            r4 = 0
            r5 = 0
            r7 = 0
            r61 = r2
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0fc2, all -> 0x1071 }
            r52 = r7
            r7 = 21
            if (r2 >= r7) goto L_0x06a7
            java.nio.ByteBuffer[] r2 = r6.getInputBuffers()     // Catch:{ Exception -> 0x068e, all -> 0x0594 }
            r4 = r2
            java.nio.ByteBuffer[] r2 = r12.getOutputBuffers()     // Catch:{ Exception -> 0x068e, all -> 0x0594 }
            r5 = r2
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x068e, all -> 0x0594 }
            r62 = r4
            r4 = 18
            if (r2 >= r4) goto L_0x068b
            java.nio.ByteBuffer[] r2 = r12.getInputBuffers()     // Catch:{ Exception -> 0x068e, all -> 0x0594 }
            r52 = r2
            r23 = r62
            goto L_0x06ac
        L_0x068b:
            r23 = r62
            goto L_0x06ac
        L_0x068e:
            r0 = move-exception
            r5 = r0
            r1 = r6
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r9 = r34
            r4 = r36
            r3 = r50
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            goto L_0x1132
        L_0x06a7:
            r2 = r4
            r4 = 18
            r23 = r2
        L_0x06ac:
            r96.checkConversionCanceled()     // Catch:{ Exception -> 0x0fc2, all -> 0x1071 }
            r2 = r25
            r25 = r24
            r24 = r5
        L_0x06b5:
            if (r25 != 0) goto L_0x0f98
            r96.checkConversionCanceled()     // Catch:{ Exception -> 0x0fc2, all -> 0x1071 }
            if (r2 != 0) goto L_0x09b0
            r63 = 0
            int r64 = r10.getSampleTrackIndex()     // Catch:{ Exception -> 0x0990, all -> 0x0981 }
            r76 = r64
            r7 = r66
            r4 = r76
            if (r4 != r7) goto L_0x0769
            r78 = r7
            r66 = r8
            r7 = 2500(0x9c4, double:1.235E-320)
            int r5 = r6.dequeueInputBuffer(r7)     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            if (r5 < 0) goto L_0x0711
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            r8 = 21
            if (r7 >= r8) goto L_0x06df
            r7 = r23[r5]     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            goto L_0x06e3
        L_0x06df:
            java.nio.ByteBuffer r7 = r6.getInputBuffer(r5)     // Catch:{ Exception -> 0x074f, all -> 0x073f }
        L_0x06e3:
            r8 = 0
            int r69 = r10.readSampleData(r7, r8)     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            r8 = r69
            if (r8 >= 0) goto L_0x06fd
            r71 = 0
            r72 = 0
            r73 = 0
            r75 = 4
            r69 = r6
            r70 = r5
            r69.queueInputBuffer(r70, r71, r72, r73, r75)     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            r2 = 1
            goto L_0x0711
        L_0x06fd:
            r71 = 0
            long r73 = r10.getSampleTime()     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            r75 = 0
            r69 = r6
            r70 = r5
            r72 = r8
            r69.queueInputBuffer(r70, r71, r72, r73, r75)     // Catch:{ Exception -> 0x074f, all -> 0x073f }
            r10.advance()     // Catch:{ Exception -> 0x074f, all -> 0x073f }
        L_0x0711:
            r79 = r1
            r82 = r3
            r1 = r4
            r87 = r6
            r89 = r13
            r88 = r28
            r8 = r50
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r7 = -1
            r13 = 21
            r76 = 0
            r65 = r36
            r36 = r30
            r94 = r34
            r34 = r9
            r9 = r94
            r95 = r78
            r78 = r11
            r11 = r95
            goto L_0x0942
        L_0x073f:
            r0 = move-exception
            r4 = r0
            r2 = r10
            r89 = r13
            r1 = r15
            r3 = r45
            r47 = r65
            r10 = r66
            r8 = r67
            goto L_0x134e
        L_0x074f:
            r0 = move-exception
            r5 = r0
            r1 = r6
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r9 = r34
            r4 = r36
            r3 = r50
            r47 = r65
            r10 = r66
            r91 = r67
            r11 = r78
            goto L_0x1132
        L_0x0769:
            r78 = r7
            r66 = r8
            r7 = r28
            r5 = -1
            if (r7 == r5) goto L_0x090f
            if (r4 != r7) goto L_0x090f
            r8 = 0
            int r5 = r10.readSampleData(r3, r8)     // Catch:{ Exception -> 0x08ef, all -> 0x08df }
            r28 = r7
            r7 = r34
            r7.size = r5     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            r34 = r4
            r4 = 21
            if (r5 >= r4) goto L_0x07a9
            r3.position(r8)     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            int r5 = r7.size     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            r3.limit(r5)     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            goto L_0x07a9
        L_0x0790:
            r0 = move-exception
            r5 = r0
            r1 = r6
            r9 = r7
            r85 = r10
            r2 = r12
            r89 = r13
            r88 = r28
            r4 = r36
            r3 = r50
            r47 = r65
            r10 = r66
            r91 = r67
            r11 = r78
            goto L_0x1132
        L_0x07a9:
            int r5 = r7.size     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            if (r5 < 0) goto L_0x07b8
            long r4 = r10.getSampleTime()     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            r7.presentationTimeUs = r4     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            r10.advance()     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            r8 = r2
            goto L_0x07bd
        L_0x07b8:
            r4 = 0
            r7.size = r4     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            r2 = 1
            r8 = r2
        L_0x07bd:
            int r2 = r7.size     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            if (r2 <= 0) goto L_0x088e
            r4 = 0
            int r2 = (r18 > r4 ? 1 : (r18 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x07fb
            long r4 = r7.presentationTimeUs     // Catch:{ Exception -> 0x0790, all -> 0x073f }
            int r2 = (r4 > r18 ? 1 : (r4 == r18 ? 0 : -1))
            if (r2 >= 0) goto L_0x07ce
            goto L_0x07fb
        L_0x07ce:
            r79 = r1
            r82 = r3
            r87 = r6
            r89 = r13
            r88 = r28
            r83 = r34
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r13 = 21
            r76 = 0
            r34 = r9
            r65 = r36
            r9 = r7
            r36 = r30
            r30 = r8
            r8 = r50
            r94 = r78
            r78 = r11
            r11 = r94
            goto L_0x08b9
        L_0x07fb:
            r2 = 0
            r7.offset = r2     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            int r4 = r10.getSampleFlags()     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            r7.flags = r4     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            long r4 = r15.writeSampleData(r1, r3, r7, r2)     // Catch:{ Exception -> 0x08c0, all -> 0x08df }
            r69 = r4
            r47 = 0
            int r2 = (r69 > r47 ? 1 : (r69 == r47 ? 0 : -1))
            if (r2 == 0) goto L_0x0862
            r4 = 0
            r71 = 0
            r79 = r1
            r1 = r96
            r80 = r61
            r2 = r97
            r82 = r3
            r81 = r59
            r3 = r49
            r83 = r34
            r5 = r36
            r84 = r58
            r26 = 21
            r34 = 18
            r87 = r6
            r36 = r30
            r76 = r47
            r48 = r60
            r47 = r65
            r65 = r5
            r5 = r69
            r30 = r8
            r34 = r9
            r89 = r13
            r88 = r28
            r8 = r50
            r13 = 21
            r9 = r7
            r94 = r78
            r78 = r11
            r11 = r94
            r7 = r71
            r1.didWriteData(r2, r3, r4, r5, r7)     // Catch:{ Exception -> 0x0852, all -> 0x0960 }
            goto L_0x088d
        L_0x0852:
            r0 = move-exception
            r5 = r0
            r3 = r8
            r85 = r10
            r2 = r12
            r4 = r65
            r10 = r66
            r91 = r67
            r1 = r87
            goto L_0x1132
        L_0x0862:
            r79 = r1
            r82 = r3
            r87 = r6
            r89 = r13
            r88 = r28
            r83 = r34
            r76 = r47
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r13 = 21
            r34 = r9
            r65 = r36
            r9 = r7
            r36 = r30
            r30 = r8
            r8 = r50
            r94 = r78
            r78 = r11
            r11 = r94
        L_0x088d:
            goto L_0x08b9
        L_0x088e:
            r79 = r1
            r82 = r3
            r87 = r6
            r89 = r13
            r88 = r28
            r83 = r34
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r13 = 21
            r76 = 0
            r34 = r9
            r65 = r36
            r9 = r7
            r36 = r30
            r30 = r8
            r8 = r50
            r94 = r78
            r78 = r11
            r11 = r94
        L_0x08b9:
            r2 = r30
            r1 = r83
            r7 = -1
            goto L_0x0942
        L_0x08c0:
            r0 = move-exception
            r87 = r6
            r9 = r7
            r89 = r13
            r88 = r28
            r8 = r50
            r47 = r65
            r11 = r78
            r65 = r36
            r5 = r0
            r3 = r8
            r85 = r10
            r2 = r12
            r4 = r65
            r10 = r66
            r91 = r67
            r1 = r87
            goto L_0x1132
        L_0x08df:
            r0 = move-exception
            r89 = r13
            r47 = r65
            r4 = r0
            r2 = r10
            r1 = r15
            r3 = r45
            r10 = r66
            r8 = r67
            goto L_0x134e
        L_0x08ef:
            r0 = move-exception
            r87 = r6
            r88 = r7
            r89 = r13
            r9 = r34
            r8 = r50
            r47 = r65
            r11 = r78
            r65 = r36
            r5 = r0
            r3 = r8
            r85 = r10
            r2 = r12
            r4 = r65
            r10 = r66
            r91 = r67
            r1 = r87
            goto L_0x1132
        L_0x090f:
            r79 = r1
            r82 = r3
            r83 = r4
            r87 = r6
            r88 = r7
            r89 = r13
            r8 = r50
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r13 = 21
            r76 = 0
            r65 = r36
            r36 = r30
            r94 = r34
            r34 = r9
            r9 = r94
            r95 = r78
            r78 = r11
            r11 = r95
            r1 = r83
            r7 = -1
            if (r1 != r7) goto L_0x0942
            r63 = 1
        L_0x0942:
            if (r63 == 0) goto L_0x097b
            r5 = r87
            r3 = 2500(0x9c4, double:1.235E-320)
            int r6 = r5.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            if (r6 < 0) goto L_0x09dc
            r71 = 0
            r72 = 0
            r73 = 0
            r75 = 4
            r69 = r5
            r70 = r6
            r69.queueInputBuffer(r70, r71, r72, r73, r75)     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            r2 = 1
            goto L_0x09dc
        L_0x0960:
            r0 = move-exception
            r4 = r0
            r2 = r10
            r1 = r15
            r3 = r45
            r10 = r66
            r8 = r67
            goto L_0x134e
        L_0x096c:
            r0 = move-exception
            r1 = r5
            r3 = r8
            r85 = r10
            r2 = r12
            r4 = r65
            r10 = r66
            r91 = r67
            r5 = r0
            goto L_0x1132
        L_0x097b:
            r5 = r87
            r3 = 2500(0x9c4, double:1.235E-320)
            goto L_0x09dc
        L_0x0981:
            r0 = move-exception
            r89 = r13
            r47 = r65
            r4 = r0
            r2 = r10
            r1 = r15
            r3 = r45
            r10 = r8
            r8 = r67
            goto L_0x134e
        L_0x0990:
            r0 = move-exception
            r5 = r6
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r66 = r8
            r65 = r36
            r8 = r50
            r1 = r5
            r3 = r8
            r85 = r10
            r2 = r12
            r4 = r65
            r10 = r66
            r91 = r67
            r5 = r0
            goto L_0x1132
        L_0x09b0:
            r79 = r1
            r82 = r3
            r5 = r6
            r78 = r11
            r89 = r13
            r88 = r28
            r84 = r58
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r11 = r66
            r3 = 2500(0x9c4, double:1.235E-320)
            r7 = -1
            r13 = 21
            r76 = 0
            r66 = r8
            r65 = r36
            r8 = r50
            r36 = r30
            r94 = r34
            r34 = r9
            r9 = r94
        L_0x09dc:
            r1 = r27 ^ 1
            r6 = 1
            r50 = r1
            r26 = r24
            r28 = r27
            r24 = r2
            r27 = r25
            r25 = r6
            r6 = r29
        L_0x09ed:
            if (r50 != 0) goto L_0x0a27
            if (r25 == 0) goto L_0x09f2
            goto L_0x0a27
        L_0x09f2:
            r29 = r6
            r50 = r8
            r2 = r24
            r24 = r26
            r25 = r27
            r27 = r28
            r30 = r36
            r60 = r48
            r36 = r65
            r8 = r66
            r1 = r79
            r61 = r80
            r59 = r81
            r3 = r82
            r58 = r84
            r28 = r88
            r13 = r89
            r4 = 18
            r7 = 21
            r6 = r5
            r66 = r11
            r65 = r47
            r11 = r78
            r94 = r34
            r34 = r9
            r9 = r94
            goto L_0x06b5
        L_0x0a27:
            r96.checkConversionCanceled()     // Catch:{ Exception -> 0x0f87, all -> 0x0f78 }
            int r1 = r12.dequeueOutputBuffer(r9, r3)     // Catch:{ Exception -> 0x0f87, all -> 0x0f78 }
            r2 = r1
            r1 = -2
            r4 = -3
            if (r2 != r7) goto L_0x0a46
            r3 = 0
            r13 = r2
            r92 = r5
            r1 = r6
            r85 = r10
            r2 = r26
            r4 = r27
            r10 = r66
            r6 = r84
            r55 = 2
            goto L_0x0c43
        L_0x0a46:
            if (r2 != r4) goto L_0x0a7a
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            if (r3 >= r13) goto L_0x0a66
            java.nio.ByteBuffer[] r3 = r12.getOutputBuffers()     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            r26 = r3
            r13 = r2
            r92 = r5
            r1 = r6
            r85 = r10
            r3 = r25
            r2 = r26
            r4 = r27
            r10 = r66
            r6 = r84
            r55 = 2
            goto L_0x0c43
        L_0x0a66:
            r13 = r2
            r92 = r5
            r1 = r6
            r85 = r10
            r3 = r25
            r2 = r26
            r4 = r27
            r10 = r66
            r6 = r84
            r55 = 2
            goto L_0x0c43
        L_0x0a7a:
            r3 = -5
            if (r2 != r1) goto L_0x0aa5
            android.media.MediaFormat r29 = r12.getOutputFormat()     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            r30 = r29
            if (r6 != r3) goto L_0x0a8f
            r3 = r30
            r1 = 0
            int r30 = r15.addTrack(r3, r1)     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            r6 = r30
            goto L_0x0a91
        L_0x0a8f:
            r3 = r30
        L_0x0a91:
            r13 = r2
            r92 = r5
            r1 = r6
            r85 = r10
            r3 = r25
            r2 = r26
            r4 = r27
            r10 = r66
            r6 = r84
            r55 = 2
            goto L_0x0c43
        L_0x0aa5:
            if (r2 < 0) goto L_0x0f41
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0f87, all -> 0x0f78 }
            if (r1 >= r13) goto L_0x0aae
            r1 = r26[r2]     // Catch:{ Exception -> 0x096c, all -> 0x0960 }
            goto L_0x0ab2
        L_0x0aae:
            java.nio.ByteBuffer r1 = r12.getOutputBuffer(r2)     // Catch:{ Exception -> 0x0f87, all -> 0x0f78 }
        L_0x0ab2:
            if (r1 == 0) goto L_0x0f19
            int r4 = r9.size     // Catch:{ Exception -> 0x0f87, all -> 0x0f78 }
            r7 = 1
            if (r4 <= r7) goto L_0x0c1e
            int r4 = r9.flags     // Catch:{ Exception -> 0x0c0c, all -> 0x0bfd }
            r55 = 2
            r4 = r4 & 2
            if (r4 != 0) goto L_0x0b33
            long r3 = r15.writeSampleData(r6, r1, r9, r7)     // Catch:{ Exception -> 0x0b21, all -> 0x0b12 }
            r58 = r3
            int r3 = (r58 > r76 ? 1 : (r58 == r76 ? 0 : -1))
            if (r3 == 0) goto L_0x0b00
            r4 = 0
            r7 = 0
            r3 = r1
            r13 = -2
            r1 = r96
            r13 = r2
            r2 = r97
            r90 = r3
            r3 = r49
            r92 = r5
            r93 = r6
            r5 = r58
            r85 = r10
            r10 = -1
            r1.didWriteData(r2, r3, r4, r5, r7)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            goto L_0x0b0a
        L_0x0ae5:
            r0 = move-exception
            r4 = r0
            r1 = r15
            r3 = r45
            r10 = r66
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0af2:
            r0 = move-exception
            r5 = r0
            r3 = r8
            r2 = r12
            r4 = r65
            r10 = r66
            r1 = r92
            r91 = r67
            goto L_0x1132
        L_0x0b00:
            r90 = r1
            r13 = r2
            r92 = r5
            r93 = r6
            r85 = r10
            r10 = -1
        L_0x0b0a:
            r10 = r66
            r6 = r84
            r4 = r90
            goto L_0x0c2c
        L_0x0b12:
            r0 = move-exception
            r85 = r10
            r4 = r0
            r1 = r15
            r3 = r45
            r10 = r66
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0b21:
            r0 = move-exception
            r92 = r5
            r85 = r10
            r5 = r0
            r3 = r8
            r2 = r12
            r4 = r65
            r10 = r66
            r1 = r92
            r91 = r67
            goto L_0x1132
        L_0x0b33:
            r90 = r1
            r13 = r2
            r92 = r5
            r93 = r6
            r85 = r10
            r10 = -1
            r1 = r93
            if (r1 != r3) goto L_0x0bf4
            int r2 = r9.size     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            int r3 = r9.offset     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            int r4 = r9.size     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            int r3 = r3 + r4
            r4 = r90
            r4.limit(r3)     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            int r3 = r9.offset     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            r4.position(r3)     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            r4.get(r2)     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            r3 = 0
            r5 = 0
            int r6 = r9.size     // Catch:{ Exception -> 0x0be6, all -> 0x0bd9 }
            r7 = 1
            int r6 = r6 - r7
        L_0x0b5d:
            if (r6 < 0) goto L_0x0ba9
            r10 = 3
            if (r6 <= r10) goto L_0x0ba9
            byte r10 = r2[r6]     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            if (r10 != r7) goto L_0x0ba4
            int r7 = r6 + -1
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            if (r7 != 0) goto L_0x0ba4
            int r7 = r6 + -2
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            if (r7 != 0) goto L_0x0ba4
            int r7 = r6 + -3
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            if (r7 != 0) goto L_0x0ba4
            int r7 = r6 + -3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            r3 = r7
            int r7 = r9.size     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            int r10 = r6 + -3
            int r7 = r7 - r10
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            r5 = r7
            int r7 = r6 + -3
            r10 = 0
            java.nio.ByteBuffer r7 = r3.put(r2, r10, r7)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            r7.position(r10)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            int r7 = r6 + -3
            int r10 = r9.size     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            int r30 = r6 + -3
            int r10 = r10 - r30
            java.nio.ByteBuffer r7 = r5.put(r2, r7, r10)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            r10 = 0
            r7.position(r10)     // Catch:{ Exception -> 0x0af2, all -> 0x0ae5 }
            goto L_0x0ba9
        L_0x0ba4:
            int r6 = r6 + -1
            r7 = 1
            r10 = -1
            goto L_0x0b5d
        L_0x0ba9:
            r10 = r66
            r6 = r84
            android.media.MediaFormat r7 = android.media.MediaFormat.createVideoFormat(r6, r14, r10)     // Catch:{ Exception -> 0x0bcd, all -> 0x0cc4 }
            if (r3 == 0) goto L_0x0bc2
            if (r5 == 0) goto L_0x0bc2
            r93 = r1
            java.lang.String r1 = "csd-0"
            r7.setByteBuffer(r1, r3)     // Catch:{ Exception -> 0x0bcd, all -> 0x0cc4 }
            java.lang.String r1 = "csd-1"
            r7.setByteBuffer(r1, r5)     // Catch:{ Exception -> 0x0bcd, all -> 0x0cc4 }
            goto L_0x0bc4
        L_0x0bc2:
            r93 = r1
        L_0x0bc4:
            r1 = 0
            int r30 = r15.addTrack(r7, r1)     // Catch:{ Exception -> 0x0bcd, all -> 0x0cc4 }
            r1 = r30
            goto L_0x0c2e
        L_0x0bcd:
            r0 = move-exception
            r5 = r0
            r3 = r8
            r2 = r12
            r4 = r65
            r1 = r92
            r91 = r67
            goto L_0x1132
        L_0x0bd9:
            r0 = move-exception
            r10 = r66
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0be6:
            r0 = move-exception
            r10 = r66
            r5 = r0
            r3 = r8
            r2 = r12
            r4 = r65
            r1 = r92
            r91 = r67
            goto L_0x1132
        L_0x0bf4:
            r93 = r1
            r10 = r66
            r6 = r84
            r4 = r90
            goto L_0x0c2c
        L_0x0bfd:
            r0 = move-exception
            r85 = r10
            r10 = r66
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0c0c:
            r0 = move-exception
            r92 = r5
            r85 = r10
            r10 = r66
            r5 = r0
            r3 = r8
            r2 = r12
            r4 = r65
            r1 = r92
            r91 = r67
            goto L_0x1132
        L_0x0c1e:
            r4 = r1
            r13 = r2
            r92 = r5
            r93 = r6
            r85 = r10
            r10 = r66
            r6 = r84
            r55 = 2
        L_0x0c2c:
            r1 = r93
        L_0x0c2e:
            int r2 = r9.flags     // Catch:{ Exception -> 0x0f0b, all -> 0x0ecc }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0c36
            r2 = 1
            goto L_0x0c37
        L_0x0c36:
            r2 = 0
        L_0x0c37:
            r27 = r2
            r2 = 0
            r12.releaseOutputBuffer(r13, r2)     // Catch:{ Exception -> 0x0f0b, all -> 0x0ecc }
            r3 = r25
            r2 = r26
            r4 = r27
        L_0x0c43:
            r5 = -1
            if (r13 == r5) goto L_0x0c5c
            r26 = r2
            r25 = r3
            r27 = r4
            r84 = r6
            r66 = r10
            r10 = r85
            r5 = r92
            r3 = 2500(0x9c4, double:1.235E-320)
            r7 = -1
            r13 = 21
            r6 = r1
            goto L_0x09ed
        L_0x0c5c:
            if (r28 != 0) goto L_0x0ee3
            r84 = r6
            r7 = r92
            r5 = 2500(0x9c4, double:1.235E-320)
            int r25 = r7.dequeueOutputBuffer(r9, r5)     // Catch:{ Exception -> 0x0ed7, all -> 0x0ecc }
            r66 = r25
            r5 = r66
            r6 = -1
            if (r5 != r6) goto L_0x0c81
            r25 = 0
            r66 = r1
            r86 = r2
            r50 = r25
            r91 = r67
            r68 = r3
            r67 = r4
            r3 = r65
            goto L_0x0ef3
        L_0x0c81:
            r6 = -3
            if (r5 != r6) goto L_0x0c92
            r66 = r1
            r86 = r2
            r91 = r67
            r68 = r3
            r67 = r4
            r3 = r65
            goto L_0x0ef3
        L_0x0c92:
            r6 = -2
            if (r5 != r6) goto L_0x0cda
            android.media.MediaFormat r6 = r7.getOutputFormat()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            boolean r25 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            if (r25 == 0) goto L_0x0cb6
            r66 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            r1.<init>()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            r86 = r2
            java.lang.String r2 = "newFormat = "
            r1.append(r2)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            r1.append(r6)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            im.bclpbkiauv.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            goto L_0x0cba
        L_0x0cb6:
            r66 = r1
            r86 = r2
        L_0x0cba:
            r91 = r67
            r68 = r3
            r67 = r4
            r3 = r65
            goto L_0x0ef3
        L_0x0cc4:
            r0 = move-exception
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0ccf:
            r0 = move-exception
            r5 = r0
            r1 = r7
            r3 = r8
            r2 = r12
            r4 = r65
            r91 = r67
            goto L_0x1132
        L_0x0cda:
            r66 = r1
            r86 = r2
            if (r5 < 0) goto L_0x0eab
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ed7, all -> 0x0ecc }
            r2 = 18
            if (r1 < r2) goto L_0x0cf0
            int r1 = r9.size     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            if (r1 == 0) goto L_0x0cec
            r1 = 1
            goto L_0x0ced
        L_0x0cec:
            r1 = 0
        L_0x0ced:
            r2 = r1
            r1 = r3
            goto L_0x0d00
        L_0x0cf0:
            int r1 = r9.size     // Catch:{ Exception -> 0x0ed7, all -> 0x0ecc }
            if (r1 != 0) goto L_0x0cfe
            r1 = r3
            long r2 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            int r6 = (r2 > r76 ? 1 : (r2 == r76 ? 0 : -1))
            if (r6 == 0) goto L_0x0cfc
            goto L_0x0cff
        L_0x0cfc:
            r2 = 0
            goto L_0x0d00
        L_0x0cfe:
            r1 = r3
        L_0x0cff:
            r2 = 1
        L_0x0d00:
            int r3 = (r18 > r76 ? 1 : (r18 == r76 ? 0 : -1))
            if (r3 <= 0) goto L_0x0d1f
            r3 = r1
            r6 = r2
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            int r25 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r25 < 0) goto L_0x0d21
            r1 = 1
            r2 = 1
            r6 = 0
            r24 = r1
            int r1 = r9.flags     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            r1 = r1 | 4
            r9.flags = r1     // Catch:{ Exception -> 0x0ccf, all -> 0x0cc4 }
            r1 = r24
            r94 = r6
            r6 = r2
            r2 = r94
            goto L_0x0d26
        L_0x0d1f:
            r3 = r1
            r6 = r2
        L_0x0d21:
            r2 = r6
            r1 = r24
            r6 = r28
        L_0x0d26:
            int r24 = (r67 > r76 ? 1 : (r67 == r76 ? 0 : -1))
            if (r24 <= 0) goto L_0x0da7
            int r24 = (r56 > r40 ? 1 : (r56 == r40 ? 0 : -1))
            if (r24 != 0) goto L_0x0da7
            r87 = r1
            r24 = r2
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0d9c, all -> 0x0d91 }
            int r25 = (r1 > r67 ? 1 : (r1 == r67 ? 0 : -1))
            if (r25 >= 0) goto L_0x0d75
            r2 = 0
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0d9c, all -> 0x0d91 }
            if (r1 == 0) goto L_0x0d68
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d9c, all -> 0x0d91 }
            r1.<init>()     // Catch:{ Exception -> 0x0d9c, all -> 0x0d91 }
            r24 = r2
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x0d9c, all -> 0x0d91 }
            r2 = r4
            r90 = r5
            r4 = r67
            r1.append(r4)     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            r67 = r2
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            r68 = r3
            long r2 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            r1.append(r2)     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            im.bclpbkiauv.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            goto L_0x0db5
        L_0x0d68:
            r24 = r2
            r90 = r5
            r94 = r67
            r68 = r3
            r67 = r4
            r4 = r94
            goto L_0x0db5
        L_0x0d75:
            r90 = r5
            r94 = r67
            r68 = r3
            r67 = r4
            r4 = r94
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0d86, all -> 0x0dc3 }
            r56 = r1
            r2 = r24
            goto L_0x0db7
        L_0x0d86:
            r0 = move-exception
            r91 = r4
            r1 = r7
            r3 = r8
            r2 = r12
            r4 = r65
            r5 = r0
            goto L_0x1132
        L_0x0d91:
            r0 = move-exception
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0d9c:
            r0 = move-exception
            r5 = r0
            r1 = r7
            r3 = r8
            r2 = r12
            r4 = r65
            r91 = r67
            goto L_0x1132
        L_0x0da7:
            r87 = r1
            r24 = r2
            r90 = r5
            r94 = r67
            r68 = r3
            r67 = r4
            r4 = r94
        L_0x0db5:
            r2 = r24
        L_0x0db7:
            r1 = r90
            r7.releaseOutputBuffer(r1, r2)     // Catch:{ Exception -> 0x0e9f, all -> 0x0e95 }
            if (r2 == 0) goto L_0x0e4b
            r3 = 0
            r65.awaitNewImage()     // Catch:{ Exception -> 0x0dcd, all -> 0x0dc3 }
            goto L_0x0dd4
        L_0x0dc3:
            r0 = move-exception
            r8 = r4
            r1 = r15
            r3 = r45
            r2 = r85
            r4 = r0
            goto L_0x134e
        L_0x0dcd:
            r0 = move-exception
            r24 = r0
            r3 = 1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r24)     // Catch:{ Exception -> 0x0e9f, all -> 0x0e95 }
        L_0x0dd4:
            if (r3 != 0) goto L_0x0e42
            r90 = r2
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0e9f, all -> 0x0e95 }
            r69 = r3
            r3 = 18
            if (r2 < r3) goto L_0x0e00
            r3 = r65
            r2 = 0
            r3.drawImage(r2)     // Catch:{ Exception -> 0x0df6, all -> 0x0e95 }
            r91 = r4
            long r4 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r24 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r24
            r8.setPresentationTime(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r8.swapBuffers()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            goto L_0x0e51
        L_0x0df6:
            r0 = move-exception
            r91 = r4
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0e00:
            r91 = r4
            r3 = r65
            r4 = 2500(0x9c4, double:1.235E-320)
            int r2 = r12.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            if (r2 < 0) goto L_0x0e38
            r4 = 1
            r3.drawImage(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.nio.ByteBuffer r24 = r3.getFrame()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r25 = r52[r2]     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r25.clear()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r26 = r48
            r27 = r14
            r28 = r10
            r29 = r35
            r30 = r33
            im.bclpbkiauv.messenger.Utilities.convertVideoFrame(r24, r25, r26, r27, r28, r29, r30)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r60 = 0
            long r4 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r64 = 0
            r58 = r12
            r59 = r2
            r61 = r53
            r62 = r4
            r58.queueInputBuffer(r59, r60, r61, r62, r64)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            goto L_0x0e51
        L_0x0e38:
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            if (r4 == 0) goto L_0x0e51
            java.lang.String r4 = "input buffer not available"
            im.bclpbkiauv.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            goto L_0x0e51
        L_0x0e42:
            r90 = r2
            r69 = r3
            r91 = r4
            r3 = r65
            goto L_0x0e51
        L_0x0e4b:
            r90 = r2
            r91 = r4
            r3 = r65
        L_0x0e51:
            int r2 = r9.flags     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0e8e
            r2 = 0
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            if (r4 == 0) goto L_0x0e61
            java.lang.String r4 = "decoder stream end"
            im.bclpbkiauv.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
        L_0x0e61:
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r5 = 18
            if (r4 < r5) goto L_0x0e6c
            r12.signalEndOfInputStream()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r4 = r6
            goto L_0x0e86
        L_0x0e6c:
            r4 = r6
            r5 = 2500(0x9c4, double:1.235E-320)
            int r24 = r12.dequeueInputBuffer(r5)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            if (r24 < 0) goto L_0x0e86
            r71 = 0
            r72 = 1
            long r5 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r75 = 4
            r69 = r12
            r70 = r24
            r73 = r5
            r69.queueInputBuffer(r70, r71, r72, r73, r75)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
        L_0x0e86:
            r50 = r2
            r28 = r4
            r24 = r87
            goto L_0x0ef3
        L_0x0e8e:
            r4 = r6
            r28 = r4
            r24 = r87
            goto L_0x0ef3
        L_0x0e95:
            r0 = move-exception
            r8 = r4
            r1 = r15
            r3 = r45
            r2 = r85
            r4 = r0
            goto L_0x134e
        L_0x0e9f:
            r0 = move-exception
            r91 = r4
            r3 = r65
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0eab:
            r1 = r5
            r91 = r67
            r68 = r3
            r67 = r4
            r3 = r65
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r4.<init>()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r4.append(r1)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            throw r2     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
        L_0x0ecc:
            r0 = move-exception
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0ed7:
            r0 = move-exception
            r3 = r65
            r91 = r67
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0ee3:
            r66 = r1
            r86 = r2
            r84 = r6
            r7 = r92
            r91 = r67
            r68 = r3
            r67 = r4
            r3 = r65
        L_0x0ef3:
            r65 = r3
            r5 = r7
            r6 = r66
            r27 = r67
            r25 = r68
            r26 = r86
            r67 = r91
            r3 = 2500(0x9c4, double:1.235E-320)
            r7 = -1
            r13 = 21
            r66 = r10
            r10 = r85
            goto L_0x09ed
        L_0x0f0b:
            r0 = move-exception
            r3 = r65
            r7 = r92
            r91 = r67
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0f19:
            r13 = r2
            r7 = r5
            r93 = r6
            r85 = r10
            r3 = r65
            r10 = r66
            r91 = r67
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r4.<init>()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r4.append(r13)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            throw r2     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
        L_0x0f41:
            r13 = r2
            r7 = r5
            r93 = r6
            r85 = r10
            r3 = r65
            r10 = r66
            r91 = r67
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r2.<init>()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r2.append(r13)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
            throw r1     // Catch:{ Exception -> 0x0f70, all -> 0x0f65 }
        L_0x0f65:
            r0 = move-exception
            r4 = r0
            r1 = r15
            r3 = r45
            r2 = r85
            r8 = r91
            goto L_0x134e
        L_0x0f70:
            r0 = move-exception
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0f78:
            r0 = move-exception
            r85 = r10
            r10 = r66
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x0f87:
            r0 = move-exception
            r7 = r5
            r85 = r10
            r3 = r65
            r10 = r66
            r91 = r67
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0f98:
            r79 = r1
            r82 = r3
            r7 = r6
            r85 = r10
            r78 = r11
            r89 = r13
            r88 = r28
            r3 = r36
            r81 = r59
            r48 = r60
            r80 = r61
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r36 = r30
            r8 = r50
            r94 = r34
            r34 = r9
            r9 = r94
            r4 = r3
            r7 = r8
            goto L_0x113b
        L_0x0fc2:
            r0 = move-exception
            r7 = r6
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r3 = r36
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r8 = r50
            r5 = r0
            r4 = r3
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0fde:
            r0 = move-exception
            r3 = r4
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r8 = r7
            r7 = r6
            r5 = r0
            r1 = r7
            r3 = r8
            r2 = r12
            goto L_0x1132
        L_0x0ff7:
            r0 = move-exception
            r47 = r5
            r85 = r10
            r89 = r13
            r10 = r8
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x1009:
            r0 = move-exception
            r47 = r5
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r11 = r66
            r91 = r67
            r10 = r8
            r8 = r7
            r7 = r6
            r5 = r0
            r1 = r7
            r3 = r8
            r2 = r12
            r4 = r51
            goto L_0x1132
        L_0x1023:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r8 = r7
            r7 = r6
            r5 = r0
            r1 = r7
            r3 = r8
            r2 = r12
            r4 = r51
            goto L_0x1132
        L_0x103d:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r8 = r7
            r5 = r0
            r3 = r8
            r2 = r12
            r1 = r36
            r4 = r51
            goto L_0x1132
        L_0x1057:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r5 = r0
            r2 = r12
            r1 = r36
            r4 = r51
            r3 = r52
            goto L_0x1132
        L_0x1071:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r47 = r65
            r10 = r8
            r4 = r0
            r1 = r15
            r3 = r45
            r8 = r67
            r2 = r85
            goto L_0x134e
        L_0x1083:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r88 = r28
            r9 = r34
            r47 = r65
            r11 = r66
            r91 = r67
            r10 = r8
            r5 = r0
            r1 = r36
            r2 = r50
            r4 = r51
            r3 = r52
            goto L_0x1132
        L_0x109e:
            r0 = move-exception
            r85 = r10
            r89 = r13
            r91 = r47
            r10 = r8
            r47 = r12
            r4 = r0
            r1 = r15
            r3 = r45
            r2 = r85
            r8 = r91
            goto L_0x134e
        L_0x10b2:
            r0 = move-exception
            r88 = r7
            r85 = r10
            r89 = r13
            r9 = r34
            r91 = r47
            r10 = r8
            r47 = r12
            r5 = r0
            r1 = r36
            r2 = r50
            r4 = r51
            r3 = r52
            goto L_0x1132
        L_0x10cb:
            r0 = move-exception
            r88 = r7
            r49 = r9
            r85 = r10
            r89 = r13
            r9 = r34
            r10 = r35
            r91 = r47
            r47 = r12
            r5 = r0
            r1 = r36
            r2 = r50
            r4 = r51
            r3 = r52
            goto L_0x1132
        L_0x10e6:
            r0 = move-exception
            r50 = r2
            r52 = r3
            r51 = r4
            r88 = r7
            r49 = r9
            r85 = r10
            r89 = r13
            r9 = r34
            r10 = r35
            r91 = r47
            r47 = r12
            r5 = r0
            r1 = r36
            goto L_0x1132
        L_0x1101:
            r0 = move-exception
            r49 = r9
            r85 = r10
            r89 = r13
            r10 = r35
            r91 = r47
            r47 = r12
            r4 = r0
            r1 = r15
            r3 = r45
            r2 = r85
            r8 = r91
            goto L_0x134e
        L_0x1118:
            r0 = move-exception
            r36 = r1
            r50 = r2
            r52 = r3
            r51 = r4
            r88 = r7
            r49 = r9
            r85 = r10
            r89 = r13
            r9 = r34
            r10 = r35
            r91 = r47
            r47 = r12
            r5 = r0
        L_0x1132:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x117c, all -> 0x1170 }
            r6 = 1
            r12 = r2
            r7 = r3
            r20 = r6
            r6 = r1
        L_0x113b:
            r1 = r85
            r1.unselectTrack(r11)     // Catch:{ Exception -> 0x1168, all -> 0x115e }
            if (r4 == 0) goto L_0x1145
            r4.release()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
        L_0x1145:
            if (r7 == 0) goto L_0x114a
            r7.release()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
        L_0x114a:
            if (r6 == 0) goto L_0x1152
            r6.stop()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
            r6.release()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
        L_0x1152:
            if (r12 == 0) goto L_0x115a
            r12.stop()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
            r12.release()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
        L_0x115a:
            r96.checkConversionCanceled()     // Catch:{ Exception -> 0x1168, all -> 0x115e }
            goto L_0x1195
        L_0x115e:
            r0 = move-exception
            r4 = r0
            r2 = r1
            r1 = r15
            r3 = r45
            r8 = r91
            goto L_0x134e
        L_0x1168:
            r0 = move-exception
            r4 = r0
            r2 = r1
            r1 = r15
            r3 = r45
            goto L_0x12f8
        L_0x1170:
            r0 = move-exception
            r1 = r85
            r4 = r0
            r2 = r1
            r1 = r15
            r3 = r45
            r8 = r91
            goto L_0x134e
        L_0x117c:
            r0 = move-exception
            r1 = r85
            r4 = r0
            r2 = r1
            r1 = r15
            r3 = r45
            goto L_0x12f8
        L_0x1186:
            r88 = r7
            r49 = r9
            r1 = r10
            r89 = r13
            r9 = r34
            r10 = r35
            r91 = r47
            r47 = r12
        L_0x1195:
        L_0x1196:
            r1.release()
            if (r15 == 0) goto L_0x11a5
            r15.finishMovie()     // Catch:{ Exception -> 0x11a0 }
            goto L_0x11a5
        L_0x11a0:
            r0 = move-exception
            r2 = r0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x11a5:
            boolean r2 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1329
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = r45
            r2.append(r3)
            long r3 = java.lang.System.currentTimeMillis()
            long r3 = r3 - r38
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            im.bclpbkiauv.messenger.FileLog.d(r2)
            goto L_0x1329
        L_0x11c5:
            r0 = move-exception
            r49 = r9
            r1 = r10
            r89 = r13
            r10 = r35
            r3 = r45
            r91 = r47
            r47 = r12
            r4 = r0
            r2 = r1
            r1 = r15
            r8 = r91
            goto L_0x134e
        L_0x11da:
            r0 = move-exception
            r49 = r9
            r1 = r10
            r89 = r13
            r10 = r35
            r3 = r45
            r91 = r47
            r47 = r12
            r4 = r0
            r2 = r1
            r1 = r15
            goto L_0x12f8
        L_0x11ed:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r37 = r12
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r1
            r1 = r2
            r10 = r3
            r3 = r4
            r15 = r7
            r4 = r0
            r1 = r15
            r8 = r91
            goto L_0x134e
        L_0x120a:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r37 = r12
            r43 = r15
            r44 = r22
            r47 = r32
            r49 = r36
            r32 = r1
            r1 = r2
            r10 = r3
            r3 = r4
            r15 = r7
            r4 = r0
            r1 = r15
            goto L_0x12f8
        L_0x1227:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r15 = r7
            r37 = r12
            r4 = r0
            r1 = r15
            r8 = r91
            goto L_0x134e
        L_0x1243:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r44 = r22
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r15 = r7
            r37 = r12
            r4 = r0
            r1 = r15
            goto L_0x12f8
        L_0x125f:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r12
            r4 = r0
            r8 = r91
            goto L_0x134e
        L_0x1279:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r44 = r22
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r12
            r4 = r0
            goto L_0x12f8
        L_0x1293:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r49 = r12
            r43 = r15
            r47 = r32
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r33
            r4 = r0
            r8 = r91
            goto L_0x134e
        L_0x12ad:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r49 = r12
            r43 = r15
            r44 = r22
            r47 = r32
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r33
            r4 = r0
            goto L_0x12f8
        L_0x12c6:
            r0 = move-exception
            r42 = r5
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r33
            r4 = r0
            r8 = r91
            goto L_0x134e
        L_0x12e0:
            r0 = move-exception
            r42 = r5
            r44 = r6
            r89 = r8
            r46 = r9
            r91 = r10
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r3 = r4
            r37 = r33
            r4 = r0
        L_0x12f8:
            r20 = 1
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x134a }
            if (r2 == 0) goto L_0x1302
            r2.release()
        L_0x1302:
            if (r1 == 0) goto L_0x130d
            r1.finishMovie()     // Catch:{ Exception -> 0x1308 }
            goto L_0x130d
        L_0x1308:
            r0 = move-exception
            r4 = r0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x130d:
            boolean r4 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x1329
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            long r5 = java.lang.System.currentTimeMillis()
            long r5 = r5 - r38
            r4.append(r5)
            java.lang.String r3 = r4.toString()
            im.bclpbkiauv.messenger.FileLog.d(r3)
        L_0x1329:
            android.content.SharedPreferences$Editor r1 = r17.edit()
            r2 = r44
            r3 = 1
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r2, r3)
            r1.commit()
            r4 = 1
            r5 = 0
            r1 = r96
            r2 = r97
            r3 = r49
            r8 = r91
            r7 = r20
            r1.didWriteData(r2, r3, r4, r5, r7)
            r1 = 1
            return r1
        L_0x134a:
            r0 = move-exception
            r8 = r91
            r4 = r0
        L_0x134e:
            if (r2 == 0) goto L_0x1353
            r2.release()
        L_0x1353:
            if (r1 == 0) goto L_0x135e
            r1.finishMovie()     // Catch:{ Exception -> 0x1359 }
            goto L_0x135e
        L_0x1359:
            r0 = move-exception
            r5 = r0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x135e:
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x137a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            long r6 = java.lang.System.currentTimeMillis()
            long r6 = r6 - r38
            r5.append(r6)
            java.lang.String r3 = r5.toString()
            im.bclpbkiauv.messenger.FileLog.d(r3)
        L_0x137a:
            throw r4
        L_0x137b:
            r42 = r5
            r2 = r6
            r89 = r8
            r46 = r9
            r8 = r10
            r43 = r15
            r47 = r32
            r49 = r36
            r32 = r37
            r10 = r3
            r37 = r33
            goto L_0x13a3
        L_0x138f:
            r42 = r5
            r2 = r6
            r89 = r8
            r46 = r9
            r8 = r10
            r43 = r15
            r47 = r32
            r10 = r35
            r49 = r36
            r32 = r37
            r37 = r33
        L_0x13a3:
            android.content.SharedPreferences$Editor r1 = r17.edit()
            r3 = 1
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r2, r3)
            r1.commit()
            r4 = 1
            r5 = 0
            r7 = 1
            r1 = r96
            r2 = r97
            r3 = r49
            r1.didWriteData(r2, r3, r4, r5, r7)
            r1 = 0
            return r1
        L_0x13be:
            r1 = 0
        L_0x13bf:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.MediaController.convertVideo(im.bclpbkiauv.messenger.MessageObject):boolean");
    }
}
