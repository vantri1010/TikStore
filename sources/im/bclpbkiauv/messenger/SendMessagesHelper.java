package im.bclpbkiauv.messenger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.support.SparseLongArray;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NativeByteBuffer;
import im.bclpbkiauv.tgnet.QuickAckDelegate;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCContacts;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SendMessagesHelper extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[3];
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap<>();
    private SparseArray<TLRPC.Message> editingMessages = new SparseArray<>();
    private LocationProvider locationProvider = new LocationProvider(new LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap<>(SendMessagesHelper.this.waitingForLocation);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    });
    private SparseArray<TLRPC.Message> sendingMessages = new SparseArray<>();
    private LongSparseArray<Integer> sendingMessagesIdDialogs = new LongSparseArray<>();
    private SparseArray<MessageObject> unsentMessages = new SparseArray<>();
    private SparseArray<TLRPC.Message> uploadMessages = new SparseArray<>();
    private LongSparseArray<Integer> uploadingMessagesIdDialogs = new LongSparseArray<>();
    private LongSparseArray<Long> voteSendTime = new LongSparseArray<>();
    private HashMap<String, Boolean> waitingForCallback = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, MessageObject> waitingForLocation = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, byte[]> waitingForVote = new HashMap<>();

    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<TLRPC.MessageEntity> entities;
        public TLRPC.BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<TLRPC.InputDocument> masks;
        public HashMap<String, String> params;
        public String path;
        public MediaController.SearchImage searchImage;
        public int ttl;
        public Uri uri;
        public VideoEditedInfo videoEditedInfo;
    }

    static {
        int cores;
        if (Build.VERSION.SDK_INT >= 17) {
            cores = Runtime.getRuntime().availableProcessors();
        } else {
            cores = 2;
        }
        mediaSendThreadPool = new ThreadPoolExecutor(cores, cores, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    private static class MediaSendPrepareWorker {
        public volatile String parentObject;
        public volatile TLRPC.TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }
    }

    public static class LocationProvider {
        /* access modifiers changed from: private */
        public LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        /* access modifiers changed from: private */
        public Location lastKnownLocation;
        private LocationManager locationManager;
        /* access modifiers changed from: private */
        public Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("found location " + location);
                    }
                    Location unused = LocationProvider.this.lastKnownLocation = location;
                    if (location.getAccuracy() < 100.0f) {
                        if (LocationProvider.this.delegate != null) {
                            LocationProvider.this.delegate.onLocationAcquired(location);
                        }
                        if (LocationProvider.this.locationQueryCancelRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        }

        public LocationProvider() {
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        /* access modifiers changed from: private */
        public void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            try {
                Location lastKnownLocation2 = this.locationManager.getLastKnownLocation("gps");
                this.lastKnownLocation = lastKnownLocation2;
                if (lastKnownLocation2 == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            AnonymousClass1 r0 = new Runnable() {
                public void run() {
                    if (LocationProvider.this.locationQueryCancelRunnable == this) {
                        if (LocationProvider.this.delegate != null) {
                            if (LocationProvider.this.lastKnownLocation != null) {
                                LocationProvider.this.delegate.onLocationAcquired(LocationProvider.this.lastKnownLocation);
                            } else {
                                LocationProvider.this.delegate.onUnableLocationAcquire();
                            }
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            };
            this.locationQueryCancelRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }

        public void stop() {
            if (this.locationManager != null) {
                Runnable runnable = this.locationQueryCancelRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                cleanup();
            }
        }
    }

    protected class DelayedMessageSendAfterRequest {
        public DelayedMessage delayedMessage;
        public MessageObject msgObj;
        public ArrayList<MessageObject> msgObjs;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public TLObject request;
        public boolean scheduled;

        protected DelayedMessageSendAfterRequest() {
        }
    }

    protected class DelayedMessage {
        public TLRPC.EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<TLRPC.InputMedia> inputMedias;
        public TLRPC.InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<TLRPC.PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<TLRPC.Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public TLRPC.PhotoSize photoSize;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public boolean scheduled;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;
        public ArrayList<VideoEditedInfo> videoEditedInfos;

        public DelayedMessage(long peer2) {
            this.peer = peer2;
        }

        public void initForGroup(long id) {
            this.type = 4;
            this.groupId = id;
            this.messageObjects = new ArrayList<>();
            this.messages = new ArrayList<>();
            this.inputMedias = new ArrayList<>();
            this.originalPaths = new ArrayList<>();
            this.parentObjects = new ArrayList<>();
            this.extraHashMap = new HashMap<>();
            this.locations = new ArrayList<>();
            this.httpLocations = new ArrayList<>();
            this.videoEditedInfos = new ArrayList<>();
        }

        public void addDelayedRequest(TLObject req, MessageObject msgObj, String originalPath2, Object parentObject2, DelayedMessage delayedMessage, boolean scheduled2) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObj = msgObj;
            request.originalPath = originalPath2;
            request.delayedMessage = delayedMessage;
            request.parentObject = parentObject2;
            request.scheduled = scheduled2;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(request);
        }

        public void addDelayedRequest(TLObject req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths2, ArrayList<Object> parentObjects2, DelayedMessage delayedMessage, boolean scheduled2) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObjs = msgObjs;
            request.originalPaths = originalPaths2;
            request.delayedMessage = delayedMessage;
            request.parentObjects = parentObjects2;
            request.scheduled = scheduled2;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(request);
        }

        public void sendDelayedRequests() {
            if (this.requests != null) {
                int i = this.type;
                if (i == 4 || i == 0) {
                    int size = this.requests.size();
                    for (int a = 0; a < size; a++) {
                        DelayedMessageSendAfterRequest request = this.requests.get(a);
                        if (request.request instanceof TLRPC.TL_messages_sendEncryptedMultiMedia) {
                            SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) request.request, this);
                        } else if (request.request instanceof TLRPC.TL_messages_sendMultiMedia) {
                            SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) request.request, request.msgObjs, request.originalPaths, request.parentObjects, request.delayedMessage, request.scheduled);
                        } else {
                            SendMessagesHelper.this.performSendMessageRequest(request.request, request.msgObj, request.originalPath, request.delayedMessage, request.parentObject, request.scheduled);
                        }
                    }
                    this.requests = null;
                }
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int a = 0; a < this.messageObjects.size(); a++) {
                    MessageObject obj2 = this.messageObjects.get(a);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(obj2.messageOwner, obj2.scheduled);
                    obj2.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj2.getId()));
                    SendMessagesHelper.this.processSentMessage(obj2.getId());
                    SendMessagesHelper.this.removeFromUploadingMessages(obj2.getId(), this.scheduled);
                }
                HashMap access$800 = SendMessagesHelper.this.delayedMessages;
                access$800.remove("group_" + this.groupId);
            } else {
                SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(this.obj.messageOwner, this.obj.scheduled);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
                SendMessagesHelper.this.removeFromUploadingMessages(this.obj.getId(), this.scheduled);
            }
            sendDelayedRequests();
        }
    }

    public static SendMessagesHelper getInstance(int num) {
        SendMessagesHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    SendMessagesHelper[] sendMessagesHelperArr = Instance;
                    SendMessagesHelper sendMessagesHelper = new SendMessagesHelper(num);
                    localInstance = sendMessagesHelper;
                    sendMessagesHelperArr[num] = sendMessagesHelper;
                }
            }
        }
        return localInstance;
    }

    public SendMessagesHelper(int instance) {
        super(instance);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                SendMessagesHelper.this.lambda$new$0$SendMessagesHelper();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$SendMessagesHelper() {
        getNotificationCenter().addObserver(this, NotificationCenter.FileDidUpload);
        getNotificationCenter().addObserver(this, NotificationCenter.FileDidFailUpload);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingStarted);
        getNotificationCenter().addObserver(this, NotificationCenter.fileNewChunkAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.editingMessages.clear();
        this.sendingMessagesIdDialogs.clear();
        this.uploadMessages.clear();
        this.uploadingMessagesIdDialogs.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.waitingForVote.clear();
        this.locationProvider.stop();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v107, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputMedia} */
    /* JADX WARNING: type inference failed for: r0v71, types: [im.bclpbkiauv.tgnet.TLObject] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r28, int r29, java.lang.Object... r30) {
        /*
            r27 = this;
            r9 = r27
            r10 = r28
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.FileDidUpload
            java.lang.String r11 = "_t"
            r13 = 2
            r1 = 0
            r8 = 1
            if (r10 != r0) goto L_0x0336
            r0 = r30[r1]
            r7 = r0
            java.lang.String r7 = (java.lang.String) r7
            r0 = r30[r8]
            r6 = r0
            im.bclpbkiauv.tgnet.TLRPC$InputFile r6 = (im.bclpbkiauv.tgnet.TLRPC.InputFile) r6
            r0 = r30[r13]
            r5 = r0
            im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile r5 = (im.bclpbkiauv.tgnet.TLRPC.InputEncryptedFile) r5
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r0 = r9.delayedMessages
            java.lang.Object r0 = r0.get(r7)
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            if (r4 == 0) goto L_0x032e
            r0 = 0
            r3 = r0
        L_0x0029:
            int r0 = r4.size()
            if (r3 >= r0) goto L_0x031d
            java.lang.Object r0 = r4.get(r3)
            r2 = r0
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r2 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r2
            r0 = 0
            im.bclpbkiauv.tgnet.TLObject r1 = r2.sendRequest
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMedia
            if (r1 == 0) goto L_0x0045
            im.bclpbkiauv.tgnet.TLObject r1 = r2.sendRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMedia r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMedia) r1
            im.bclpbkiauv.tgnet.TLRPC$InputMedia r0 = r1.media
            r1 = r0
            goto L_0x0065
        L_0x0045:
            im.bclpbkiauv.tgnet.TLObject r1 = r2.sendRequest
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_editMessage
            if (r1 == 0) goto L_0x0053
            im.bclpbkiauv.tgnet.TLObject r1 = r2.sendRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_editMessage r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_editMessage) r1
            im.bclpbkiauv.tgnet.TLRPC$InputMedia r0 = r1.media
            r1 = r0
            goto L_0x0065
        L_0x0053:
            im.bclpbkiauv.tgnet.TLObject r1 = r2.sendRequest
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r1 == 0) goto L_0x0064
            java.util.HashMap<java.lang.Object, java.lang.Object> r1 = r2.extraHashMap
            java.lang.Object r1 = r1.get(r7)
            r0 = r1
            im.bclpbkiauv.tgnet.TLRPC$InputMedia r0 = (im.bclpbkiauv.tgnet.TLRPC.InputMedia) r0
            r1 = r0
            goto L_0x0065
        L_0x0064:
            r1 = r0
        L_0x0065:
            java.lang.String r0 = "_i"
            if (r6 == 0) goto L_0x0219
            if (r1 == 0) goto L_0x0219
            int r8 = r2.type
            if (r8 != 0) goto L_0x00a5
            r1.file = r6
            im.bclpbkiauv.tgnet.TLObject r8 = r2.sendRequest
            im.bclpbkiauv.messenger.MessageObject r0 = r2.obj
            java.lang.String r14 = r2.originalPath
            r17 = 1
            r18 = 0
            java.lang.Object r12 = r2.parentObject
            boolean r13 = r2.scheduled
            r19 = r0
            r0 = r27
            r15 = r1
            r1 = r8
            r8 = r2
            r2 = r19
            r23 = r3
            r3 = r14
            r14 = r4
            r4 = r8
            r10 = r5
            r5 = r17
            r24 = r6
            r6 = r18
            r25 = r7
            r7 = r12
            r12 = r8
            r26 = r10
            r10 = 1
            r8 = r13
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6, r7, r8)
            r7 = r24
            r3 = r25
            goto L_0x020d
        L_0x00a5:
            r15 = r1
            r12 = r2
            r23 = r3
            r14 = r4
            r26 = r5
            r24 = r6
            r25 = r7
            r10 = 1
            int r1 = r12.type
            if (r1 != r10) goto L_0x0104
            im.bclpbkiauv.tgnet.TLRPC$InputFile r0 = r15.file
            if (r0 != 0) goto L_0x00e6
            r7 = r24
            r15.file = r7
            im.bclpbkiauv.tgnet.TLRPC$InputFile r0 = r15.thumb
            if (r0 != 0) goto L_0x00d2
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r12.photoSize
            if (r0 == 0) goto L_0x00d2
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r12.photoSize
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r0 = r0.location
            if (r0 == 0) goto L_0x00d2
            r9.performSendDelayedMessage(r12)
            r3 = r25
            goto L_0x020d
        L_0x00d2:
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest
            im.bclpbkiauv.messenger.MessageObject r2 = r12.obj
            java.lang.String r3 = r12.originalPath
            r4 = 0
            java.lang.Object r5 = r12.parentObject
            boolean r6 = r12.scheduled
            r0 = r27
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6)
            r3 = r25
            goto L_0x020d
        L_0x00e6:
            r7 = r24
            r15.thumb = r7
            int r0 = r15.flags
            r1 = 4
            r0 = r0 | r1
            r15.flags = r0
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest
            im.bclpbkiauv.messenger.MessageObject r2 = r12.obj
            java.lang.String r3 = r12.originalPath
            r4 = 0
            java.lang.Object r5 = r12.parentObject
            boolean r6 = r12.scheduled
            r0 = r27
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6)
            r3 = r25
            goto L_0x020d
        L_0x0104:
            r7 = r24
            int r1 = r12.type
            r2 = 2
            if (r1 != r2) goto L_0x0156
            im.bclpbkiauv.tgnet.TLRPC$InputFile r0 = r15.file
            if (r0 != 0) goto L_0x013a
            r15.file = r7
            im.bclpbkiauv.tgnet.TLRPC$InputFile r0 = r15.thumb
            if (r0 != 0) goto L_0x0126
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r12.photoSize
            if (r0 == 0) goto L_0x0126
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = r12.photoSize
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r0 = r0.location
            if (r0 == 0) goto L_0x0126
            r9.performSendDelayedMessage(r12)
            r3 = r25
            goto L_0x020d
        L_0x0126:
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest
            im.bclpbkiauv.messenger.MessageObject r2 = r12.obj
            java.lang.String r3 = r12.originalPath
            r4 = 0
            java.lang.Object r5 = r12.parentObject
            boolean r6 = r12.scheduled
            r0 = r27
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6)
            r3 = r25
            goto L_0x020d
        L_0x013a:
            r15.thumb = r7
            int r0 = r15.flags
            r1 = 4
            r0 = r0 | r1
            r15.flags = r0
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest
            im.bclpbkiauv.messenger.MessageObject r2 = r12.obj
            java.lang.String r3 = r12.originalPath
            r4 = 0
            java.lang.Object r5 = r12.parentObject
            boolean r6 = r12.scheduled
            r0 = r27
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6)
            r3 = r25
            goto L_0x020d
        L_0x0156:
            int r1 = r12.type
            r2 = 3
            if (r1 != r2) goto L_0x0171
            r15.file = r7
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest
            im.bclpbkiauv.messenger.MessageObject r2 = r12.obj
            java.lang.String r3 = r12.originalPath
            r4 = 0
            java.lang.Object r5 = r12.parentObject
            boolean r6 = r12.scheduled
            r0 = r27
            r0.performSendMessageRequest(r1, r2, r3, r4, r5, r6)
            r3 = r25
            goto L_0x020d
        L_0x0171:
            int r1 = r12.type
            r2 = 4
            if (r1 != r2) goto L_0x020b
            boolean r1 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaUploadedDocument
            if (r1 == 0) goto L_0x0202
            im.bclpbkiauv.tgnet.TLRPC$InputFile r1 = r15.file
            if (r1 != 0) goto L_0x01da
            r15.file = r7
            java.util.HashMap<java.lang.Object, java.lang.Object> r1 = r12.extraHashMap
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = r25
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.Object r0 = r1.get(r0)
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r1 = r12.messageObjects
            int r1 = r1.indexOf(r0)
            java.util.HashMap<java.lang.Object, java.lang.Object> r2 = r12.extraHashMap
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            java.lang.Object r2 = r2.get(r4)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r2
            r12.photoSize = r2
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r2 = r12.messageObjects
            java.lang.Object r2 = r2.get(r1)
            im.bclpbkiauv.messenger.MessageObject r2 = (im.bclpbkiauv.messenger.MessageObject) r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.attachPath
            r9.stopVideoService(r2)
            im.bclpbkiauv.tgnet.TLRPC$InputFile r2 = r15.thumb
            if (r2 != 0) goto L_0x01d5
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = r12.photoSize
            if (r2 == 0) goto L_0x01d5
            r12.performMediaUpload = r10
            r9.performSendDelayedMessage(r12, r1)
            goto L_0x01d9
        L_0x01d5:
            r2 = 0
            r9.uploadMultiMedia(r12, r15, r2, r3)
        L_0x01d9:
            goto L_0x020d
        L_0x01da:
            r3 = r25
            r15.thumb = r7
            int r0 = r15.flags
            r1 = 4
            r0 = r0 | r1
            r15.flags = r0
            java.util.HashMap<java.lang.Object, java.lang.Object> r0 = r12.extraHashMap
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            java.lang.String r2 = "_o"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.Object r0 = r0.get(r1)
            java.lang.String r0 = (java.lang.String) r0
            r1 = 0
            r9.uploadMultiMedia(r12, r15, r1, r0)
            goto L_0x020d
        L_0x0202:
            r3 = r25
            r1 = 0
            r15.file = r7
            r9.uploadMultiMedia(r12, r15, r1, r3)
            goto L_0x020d
        L_0x020b:
            r3 = r25
        L_0x020d:
            r1 = r23
            r14.remove(r1)
            int r0 = r1 + -1
            r8 = r11
            r13 = r26
            goto L_0x030f
        L_0x0219:
            r15 = r1
            r12 = r2
            r1 = r3
            r14 = r4
            r26 = r5
            r3 = r7
            r10 = 1
            r7 = r6
            if (r26 == 0) goto L_0x030b
            im.bclpbkiauv.tgnet.TLObject r2 = r12.sendEncryptedRequest
            if (r2 == 0) goto L_0x030b
            r2 = 0
            int r4 = r12.type
            r5 = 4
            if (r4 != r5) goto L_0x029f
            im.bclpbkiauv.tgnet.TLObject r4 = r12.sendEncryptedRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r4
            java.util.HashMap<java.lang.Object, java.lang.Object> r5 = r12.extraHashMap
            java.lang.Object r5 = r5.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile r5 = (im.bclpbkiauv.tgnet.TLRPC.InputEncryptedFile) r5
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile> r6 = r4.files
            int r6 = r6.indexOf(r5)
            if (r6 < 0) goto L_0x029b
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile> r8 = r4.files
            r13 = r26
            r8.set(r6, r13)
            r8 = r11
            long r10 = r5.id
            r16 = 1
            int r18 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x0291
            java.util.HashMap<java.lang.Object, java.lang.Object> r10 = r12.extraHashMap
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r3)
            r11.append(r0)
            java.lang.String r0 = r11.toString()
            java.lang.Object r0 = r10.get(r0)
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            java.util.HashMap<java.lang.Object, java.lang.Object> r10 = r12.extraHashMap
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r3)
            r11.append(r8)
            java.lang.String r11 = r11.toString()
            java.lang.Object r10 = r10.get(r11)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r10 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r10
            r12.photoSize = r10
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r10 = r12.messageObjects
            java.lang.Object r10 = r10.get(r6)
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.attachPath
            r9.stopVideoService(r10)
        L_0x0291:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage> r0 = r4.messages
            java.lang.Object r0 = r0.get(r6)
            r2 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessage) r2
            goto L_0x029e
        L_0x029b:
            r8 = r11
            r13 = r26
        L_0x029e:
            goto L_0x02a7
        L_0x029f:
            r8 = r11
            r13 = r26
            im.bclpbkiauv.tgnet.TLObject r0 = r12.sendEncryptedRequest
            r2 = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessage) r2
        L_0x02a7:
            if (r2 == 0) goto L_0x0305
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaVideo
            if (r0 != 0) goto L_0x02bb
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaPhoto
            if (r0 != 0) goto L_0x02bb
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument
            if (r0 == 0) goto L_0x02c9
        L_0x02bb:
            r0 = 5
            r0 = r30[r0]
            java.lang.Long r0 = (java.lang.Long) r0
            long r4 = r0.longValue()
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            int r6 = (int) r4
            r0.size = r6
        L_0x02c9:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            r4 = 3
            r5 = r30[r4]
            byte[] r5 = (byte[]) r5
            byte[] r5 = (byte[]) r5
            r0.key = r5
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r0 = r2.media
            r4 = 4
            r5 = r30[r4]
            byte[] r5 = (byte[]) r5
            byte[] r5 = (byte[]) r5
            r0.iv = r5
            int r0 = r12.type
            if (r0 != r4) goto L_0x02e8
            r0 = 0
            r9.uploadMultiMedia(r12, r0, r13, r3)
            goto L_0x0305
        L_0x02e8:
            im.bclpbkiauv.messenger.SecretChatHelper r16 = r27.getSecretChatHelper()
            im.bclpbkiauv.messenger.MessageObject r0 = r12.obj
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r4 = r12.encryptedChat
            java.lang.String r5 = r12.originalPath
            im.bclpbkiauv.messenger.MessageObject r6 = r12.obj
            r17 = r2
            r18 = r0
            r19 = r4
            r20 = r13
            r21 = r5
            r22 = r6
            r16.performSendEncryptedRequest(r17, r18, r19, r20, r21, r22)
        L_0x0305:
            r14.remove(r1)
            int r0 = r1 + -1
            goto L_0x030f
        L_0x030b:
            r8 = r11
            r13 = r26
            r0 = r1
        L_0x030f:
            r1 = 1
            int r0 = r0 + r1
            r10 = r28
            r6 = r7
            r11 = r8
            r5 = r13
            r4 = r14
            r8 = 1
            r13 = 2
            r7 = r3
            r3 = r0
            goto L_0x0029
        L_0x031d:
            r1 = r3
            r14 = r4
            r13 = r5
            r3 = r7
            r7 = r6
            boolean r0 = r14.isEmpty()
            if (r0 == 0) goto L_0x0332
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r0 = r9.delayedMessages
            r0.remove(r3)
            goto L_0x0332
        L_0x032e:
            r14 = r4
            r13 = r5
            r3 = r7
            r7 = r6
        L_0x0332:
            r6 = r28
            goto L_0x06bb
        L_0x0336:
            r8 = r11
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.FileDidFailUpload
            r6 = r28
            if (r6 != r0) goto L_0x0385
            r0 = r30[r1]
            java.lang.String r0 = (java.lang.String) r0
            r1 = 1
            r2 = r30[r1]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r1 = r2.booleanValue()
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r2 = r9.delayedMessages
            java.lang.Object r2 = r2.get(r0)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x0383
            r3 = 0
        L_0x0355:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x0378
            java.lang.Object r4 = r2.get(r3)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r4 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r4
            if (r1 == 0) goto L_0x0367
            im.bclpbkiauv.tgnet.TLObject r5 = r4.sendEncryptedRequest
            if (r5 != 0) goto L_0x036d
        L_0x0367:
            if (r1 != 0) goto L_0x0375
            im.bclpbkiauv.tgnet.TLObject r5 = r4.sendRequest
            if (r5 == 0) goto L_0x0375
        L_0x036d:
            r4.markAsError()
            r2.remove(r3)
            int r3 = r3 + -1
        L_0x0375:
            r4 = 1
            int r3 = r3 + r4
            goto L_0x0355
        L_0x0378:
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x0383
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r3 = r9.delayedMessages
            r3.remove(r0)
        L_0x0383:
            goto L_0x06bb
        L_0x0385:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.filePreparingStarted
            if (r6 != r0) goto L_0x0409
            r0 = r30[r1]
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            int r1 = r0.getId()
            if (r1 != 0) goto L_0x0394
            return
        L_0x0394:
            r1 = 1
            r2 = r30[r1]
            r1 = r2
            java.lang.String r1 = (java.lang.String) r1
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r2 = r9.delayedMessages
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.attachPath
            java.lang.Object r2 = r2.get(r3)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x0407
            r3 = 0
        L_0x03a9:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x03f8
            java.lang.Object r4 = r2.get(r3)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r4 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r4
            int r5 = r4.type
            r7 = 4
            if (r5 != r7) goto L_0x03e7
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r5 = r4.messageObjects
            int r5 = r5.indexOf(r0)
            java.util.HashMap<java.lang.Object, java.lang.Object> r7 = r4.extraHashMap
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r0.messageOwner
            java.lang.String r11 = r11.attachPath
            r10.append(r11)
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            java.lang.Object r7 = r7.get(r8)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r7
            r4.photoSize = r7
            r7 = 1
            r4.performMediaUpload = r7
            r9.performSendDelayedMessage(r4, r5)
            r2.remove(r3)
            goto L_0x03f8
        L_0x03e7:
            im.bclpbkiauv.messenger.MessageObject r5 = r4.obj
            if (r5 != r0) goto L_0x03f5
            r5 = 0
            r4.videoEditedInfo = r5
            r9.performSendDelayedMessage(r4)
            r2.remove(r3)
            goto L_0x03f8
        L_0x03f5:
            int r3 = r3 + 1
            goto L_0x03a9
        L_0x03f8:
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x0407
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r3 = r9.delayedMessages
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.attachPath
            r3.remove(r4)
        L_0x0407:
            goto L_0x06bb
        L_0x0409:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fileNewChunkAvailable
            if (r6 != r0) goto L_0x0514
            r0 = r30[r1]
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            int r2 = r0.getId()
            if (r2 != 0) goto L_0x0418
            return
        L_0x0418:
            r2 = 1
            r3 = r30[r2]
            r2 = r3
            java.lang.String r2 = (java.lang.String) r2
            r3 = 2
            r3 = r30[r3]
            java.lang.Long r3 = (java.lang.Long) r3
            long r3 = r3.longValue()
            r5 = 3
            r5 = r30[r5]
            java.lang.Long r5 = (java.lang.Long) r5
            long r7 = r5.longValue()
            long r10 = r0.getDialogId()
            int r5 = (int) r10
            if (r5 != 0) goto L_0x0439
            r12 = 1
            goto L_0x043a
        L_0x0439:
            r12 = 0
        L_0x043a:
            im.bclpbkiauv.messenger.FileLoader r10 = r27.getFileLoader()
            r11 = r2
            r13 = r3
            r15 = r7
            r10.checkUploadNewDataAvailable(r11, r12, r13, r15)
            r10 = 0
            int r1 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r1 == 0) goto L_0x0512
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.attachPath
            r9.stopVideoService(r1)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r1 = r9.delayedMessages
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r0.messageOwner
            java.lang.String r5 = r5.attachPath
            java.lang.Object r1 = r1.get(r5)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 == 0) goto L_0x0510
            r5 = 0
        L_0x0460:
            int r10 = r1.size()
            if (r5 >= r10) goto L_0x050d
            java.lang.Object r10 = r1.get(r5)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r10 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r10
            int r11 = r10.type
            java.lang.String r13 = "ve"
            r14 = 4
            if (r11 != r14) goto L_0x04c5
            r11 = 0
        L_0x0475:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r14 = r10.messageObjects
            int r14 = r14.size()
            if (r11 >= r14) goto L_0x04c1
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r14 = r10.messageObjects
            java.lang.Object r14 = r14.get(r11)
            im.bclpbkiauv.messenger.MessageObject r14 = (im.bclpbkiauv.messenger.MessageObject) r14
            if (r14 != r0) goto L_0x04bc
            r15 = 0
            r14.videoEditedInfo = r15
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r14.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r15 = r15.params
            r15.remove(r13)
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r14.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r13 = r13.media
            im.bclpbkiauv.tgnet.TLRPC$Document r13 = r13.document
            int r15 = (int) r7
            r13.size = r15
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r14.messageOwner
            r13.add(r15)
            im.bclpbkiauv.messenger.MessagesStorage r15 = r27.getMessagesStorage()
            r17 = 0
            r18 = 1
            r19 = 0
            r20 = 0
            r22 = r1
            boolean r1 = r14.scheduled
            r16 = r13
            r21 = r1
            r15.putMessages((java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message>) r16, (boolean) r17, (boolean) r18, (boolean) r19, (int) r20, (boolean) r21)
            goto L_0x04c3
        L_0x04bc:
            r22 = r1
            int r11 = r11 + 1
            goto L_0x0475
        L_0x04c1:
            r22 = r1
        L_0x04c3:
            r11 = 0
            goto L_0x0507
        L_0x04c5:
            r22 = r1
            im.bclpbkiauv.messenger.MessageObject r1 = r10.obj
            if (r1 != r0) goto L_0x0506
            im.bclpbkiauv.messenger.MessageObject r1 = r10.obj
            r11 = 0
            r1.videoEditedInfo = r11
            im.bclpbkiauv.messenger.MessageObject r1 = r10.obj
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r1.params
            r1.remove(r13)
            im.bclpbkiauv.messenger.MessageObject r1 = r10.obj
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r1.document
            int r11 = (int) r7
            r1.size = r11
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            im.bclpbkiauv.messenger.MessageObject r11 = r10.obj
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r11.messageOwner
            r1.add(r11)
            im.bclpbkiauv.messenger.MessagesStorage r13 = r27.getMessagesStorage()
            r15 = 0
            r16 = 1
            r17 = 0
            r18 = 0
            im.bclpbkiauv.messenger.MessageObject r11 = r10.obj
            boolean r11 = r11.scheduled
            r14 = r1
            r19 = r11
            r13.putMessages((java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message>) r14, (boolean) r15, (boolean) r16, (boolean) r17, (int) r18, (boolean) r19)
            goto L_0x0512
        L_0x0506:
            r11 = 0
        L_0x0507:
            int r5 = r5 + 1
            r1 = r22
            goto L_0x0460
        L_0x050d:
            r22 = r1
            goto L_0x0512
        L_0x0510:
            r22 = r1
        L_0x0512:
            goto L_0x06bb
        L_0x0514:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.filePreparingFailed
            if (r6 != r0) goto L_0x0586
            r0 = r30[r1]
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            int r1 = r0.getId()
            if (r1 != 0) goto L_0x0523
            return
        L_0x0523:
            r1 = 1
            r2 = r30[r1]
            r1 = r2
            java.lang.String r1 = (java.lang.String) r1
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.attachPath
            r9.stopVideoService(r2)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r2 = r9.delayedMessages
            java.lang.Object r2 = r2.get(r1)
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            if (r2 == 0) goto L_0x0584
            r3 = 0
        L_0x053b:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x0579
            java.lang.Object r4 = r2.get(r3)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r4 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r4
            int r5 = r4.type
            r7 = 4
            if (r5 != r7) goto L_0x056a
            r5 = 0
        L_0x054d:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r7 = r4.messages
            int r7 = r7.size()
            if (r5 >= r7) goto L_0x0569
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r7 = r4.messageObjects
            java.lang.Object r7 = r7.get(r5)
            if (r7 != r0) goto L_0x0566
            r4.markAsError()
            r2.remove(r3)
            int r3 = r3 + -1
            goto L_0x0569
        L_0x0566:
            int r5 = r5 + 1
            goto L_0x054d
        L_0x0569:
            goto L_0x0576
        L_0x056a:
            im.bclpbkiauv.messenger.MessageObject r5 = r4.obj
            if (r5 != r0) goto L_0x0576
            r4.markAsError()
            r2.remove(r3)
            int r3 = r3 + -1
        L_0x0576:
            r4 = 1
            int r3 = r3 + r4
            goto L_0x053b
        L_0x0579:
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x0584
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r3 = r9.delayedMessages
            r3.remove(r1)
        L_0x0584:
            goto L_0x06bb
        L_0x0586:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.httpFileDidLoad
            if (r6 != r0) goto L_0x0660
            r0 = r30[r1]
            r7 = r0
            java.lang.String r7 = (java.lang.String) r7
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r0 = r9.delayedMessages
            java.lang.Object r0 = r0.get(r7)
            r8 = r0
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            if (r8 == 0) goto L_0x065d
            r0 = 0
            r10 = r0
        L_0x059c:
            int r0 = r8.size()
            if (r10 >= r0) goto L_0x0655
            java.lang.Object r0 = r8.get(r10)
            r11 = r0
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r11 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r11
            r0 = -1
            int r1 = r11.type
            if (r1 != 0) goto L_0x05b5
            r0 = 0
            im.bclpbkiauv.messenger.MessageObject r1 = r11.obj
            r13 = r0
            r14 = r1
            r12 = 2
            goto L_0x05de
        L_0x05b5:
            int r1 = r11.type
            r12 = 2
            if (r1 != r12) goto L_0x05c0
            r0 = 1
            im.bclpbkiauv.messenger.MessageObject r1 = r11.obj
            r13 = r0
            r14 = r1
            goto L_0x05de
        L_0x05c0:
            int r1 = r11.type
            r2 = 4
            if (r1 != r2) goto L_0x05db
            java.util.HashMap<java.lang.Object, java.lang.Object> r1 = r11.extraHashMap
            java.lang.Object r1 = r1.get(r7)
            im.bclpbkiauv.messenger.MessageObject r1 = (im.bclpbkiauv.messenger.MessageObject) r1
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r1.getDocument()
            if (r2 == 0) goto L_0x05d7
            r0 = 1
            r13 = r0
            r14 = r1
            goto L_0x05de
        L_0x05d7:
            r0 = 0
            r13 = r0
            r14 = r1
            goto L_0x05de
        L_0x05db:
            r1 = 0
            r13 = r0
            r14 = r1
        L_0x05de:
            if (r13 != 0) goto L_0x061f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = im.bclpbkiauv.messenger.Utilities.MD5(r7)
            r0.append(r1)
            java.lang.String r1 = "."
            r0.append(r1)
            java.lang.String r1 = "file"
            java.lang.String r1 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r7, r1)
            r0.append(r1)
            java.lang.String r15 = r0.toString()
            java.io.File r2 = new java.io.File
            r0 = 4
            java.io.File r1 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r0)
            r2.<init>(r1, r15)
            im.bclpbkiauv.messenger.DispatchQueue r5 = im.bclpbkiauv.messenger.Utilities.globalQueue
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$X4rqdNqYPDRr9WUZrHD5LWO-PEc r4 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$X4rqdNqYPDRr9WUZrHD5LWO-PEc
            r0 = r4
            r1 = r27
            r3 = r14
            r12 = r4
            r4 = r11
            r16 = r8
            r8 = r5
            r5 = r7
            r0.<init>(r2, r3, r4, r5)
            r8.postRunnable(r12)
            r0 = 1
            r3 = 4
            goto L_0x064f
        L_0x061f:
            r16 = r8
            r0 = 1
            if (r13 != r0) goto L_0x064e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = im.bclpbkiauv.messenger.Utilities.MD5(r7)
            r1.append(r2)
            java.lang.String r2 = ".gif"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.io.File r2 = new java.io.File
            r3 = 4
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r3)
            r2.<init>(r4, r1)
            im.bclpbkiauv.messenger.DispatchQueue r4 = im.bclpbkiauv.messenger.Utilities.globalQueue
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$oy4jux7LLHEYQTD3bQ3POLJTnVc r5 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$oy4jux7LLHEYQTD3bQ3POLJTnVc
            r5.<init>(r11, r2, r14)
            r4.postRunnable(r5)
            goto L_0x064f
        L_0x064e:
            r3 = 4
        L_0x064f:
            int r10 = r10 + 1
            r8 = r16
            goto L_0x059c
        L_0x0655:
            r16 = r8
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r0 = r9.delayedMessages
            r0.remove(r7)
            goto L_0x065f
        L_0x065d:
            r16 = r8
        L_0x065f:
            goto L_0x06bb
        L_0x0660:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fileDidLoad
            if (r6 != r0) goto L_0x068b
            r0 = r30[r1]
            java.lang.String r0 = (java.lang.String) r0
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r1 = r9.delayedMessages
            java.lang.Object r1 = r1.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 == 0) goto L_0x0694
            r2 = 0
        L_0x0673:
            int r3 = r1.size()
            if (r2 >= r3) goto L_0x0685
            java.lang.Object r3 = r1.get(r2)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r3 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r3
            r9.performSendDelayedMessage(r3)
            int r2 = r2 + 1
            goto L_0x0673
        L_0x0685:
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r2 = r9.delayedMessages
            r2.remove(r0)
            goto L_0x0694
        L_0x068b:
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.httpFileDidFailedLoad
            if (r6 == r0) goto L_0x0695
            int r0 = im.bclpbkiauv.messenger.NotificationCenter.fileDidFailToLoad
            if (r6 != r0) goto L_0x0694
            goto L_0x0695
        L_0x0694:
            goto L_0x06bb
        L_0x0695:
            r0 = r30[r1]
            java.lang.String r0 = (java.lang.String) r0
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r1 = r9.delayedMessages
            java.lang.Object r1 = r1.get(r0)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 == 0) goto L_0x06bb
            r2 = 0
        L_0x06a4:
            int r3 = r1.size()
            if (r2 >= r3) goto L_0x06b6
            java.lang.Object r3 = r1.get(r2)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r3 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r3
            r3.markAsError()
            int r2 = r2 + 1
            goto L_0x06a4
        L_0x06b6:
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r2 = r9.delayedMessages
            r2.remove(r0)
        L_0x06bb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$SendMessagesHelper(File cacheFile, MessageObject messageObject, DelayedMessage message, String path) {
        AndroidUtilities.runOnUIThread(new Runnable(generatePhotoSizes(cacheFile.toString(), (Uri) null, false), messageObject, cacheFile, message, path) {
            private final /* synthetic */ TLRPC.TL_photo f$1;
            private final /* synthetic */ MessageObject f$2;
            private final /* synthetic */ File f$3;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;
            private final /* synthetic */ String f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$1$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$SendMessagesHelper(TLRPC.TL_photo photo, MessageObject messageObject, File cacheFile, DelayedMessage message, String path) {
        if (photo != null) {
            messageObject.messageOwner.media.photo = photo;
            messageObject.messageOwner.attachPath = cacheFile.toString();
            ArrayList<TLRPC.Message> messages = new ArrayList<>();
            messages.add(messageObject.messageOwner);
            getMessagesStorage().putMessages(messages, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            message.photoSize = (TLRPC.PhotoSize) photo.sizes.get(photo.sizes.size() - 1);
            message.locationParent = photo;
            message.httpLocation = null;
            if (message.type == 4) {
                message.performMediaUpload = true;
                performSendDelayedMessage(message, message.messageObjects.indexOf(messageObject));
                return;
            }
            performSendDelayedMessage(message);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("can't load image " + path + " to file " + cacheFile.toString());
        }
        message.markAsError();
    }

    public /* synthetic */ void lambda$didReceivedNotification$4$SendMessagesHelper(DelayedMessage message, File cacheFile, MessageObject messageObject) {
        TLRPC.Document document = message.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (document.thumbs.get(0).location instanceof TLRPC.TL_fileLocationUnavailable)) {
            try {
                Bitmap bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), (Uri) null, 90.0f, 90.0f, true);
                if (bitmap != null) {
                    document.thumbs.clear();
                    ArrayList<TLRPC.PhotoSize> arrayList = document.thumbs;
                    if (message.sendEncryptedRequest != null) {
                        z = true;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z));
                    bitmap.recycle();
                }
            } catch (Exception e) {
                document.thumbs.clear();
                FileLog.e((Throwable) e);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(message, cacheFile, document, messageObject) {
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ TLRPC.Document f$3;
            private final /* synthetic */ MessageObject f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$3$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage message, File cacheFile, TLRPC.Document document, MessageObject messageObject) {
        message.httpLocation = null;
        message.obj.messageOwner.attachPath = cacheFile.toString();
        if (!document.thumbs.isEmpty()) {
            message.photoSize = document.thumbs.get(0);
            message.locationParent = document;
        }
        ArrayList<TLRPC.Message> messages = new ArrayList<>();
        messages.add(messageObject.messageOwner);
        getMessagesStorage().putMessages(messages, false, true, false, 0, messageObject.scheduled);
        message.performMediaUpload = true;
        performSendDelayedMessage(message);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, message.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject object) {
        object.cancelEditing = true;
        object.messageOwner.media = object.previousMedia;
        object.messageOwner.message = object.previousCaption;
        object.messageOwner.entities = object.previousCaptionEntities;
        object.messageOwner.attachPath = object.previousAttachPath;
        object.messageOwner.send_state = 0;
        object.previousMedia = null;
        object.previousCaption = null;
        object.previousCaptionEntities = null;
        object.previousAttachPath = null;
        object.videoEditedInfo = null;
        object.type = -1;
        object.setType();
        object.caption = null;
        object.generateCaption();
        ArrayList<TLRPC.Message> arr = new ArrayList<>();
        arr.add(object.messageOwner);
        getMessagesStorage().putMessages(arr, false, true, false, 0, object.scheduled);
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(object);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(object.getDialogId()), arrayList);
    }

    public void cancelSendingMessage(MessageObject object) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(object);
        cancelSendingMessage(arrayList);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x01ed, code lost:
        r14 = r28;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancelSendingMessage(java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r35) {
        /*
            r34 = this;
            r0 = r34
            r1 = r35
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r10 = 0
            r14 = r5
            r15 = r6
            r16 = r7
            r17 = r8
        L_0x001f:
            int r5 = r35.size()
            r6 = 1
            if (r10 >= r5) goto L_0x020a
            java.lang.Object r5 = r1.get(r10)
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            boolean r7 = r5.scheduled
            if (r7 == 0) goto L_0x0036
            r7 = 1
            long r8 = r5.getDialogId()
            goto L_0x003a
        L_0x0036:
            r7 = r16
            r8 = r17
        L_0x003a:
            int r11 = r5.getId()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r4.add(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r11 = r11.to_id
            int r15 = r11.channel_id
            int r11 = r5.getId()
            boolean r12 = r5.scheduled
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r0.removeFromSendingMessages(r11, r12)
            if (r11 == 0) goto L_0x0060
            im.bclpbkiauv.tgnet.ConnectionsManager r12 = r34.getConnectionsManager()
            int r13 = r11.reqId
            r12.cancelRequest(r13, r6)
        L_0x0060:
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r12 = r0.delayedMessages
            java.util.Set r12 = r12.entrySet()
            java.util.Iterator r12 = r12.iterator()
        L_0x006a:
            boolean r13 = r12.hasNext()
            if (r13 == 0) goto L_0x01fa
            java.lang.Object r13 = r12.next()
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13
            java.lang.Object r16 = r13.getValue()
            r6 = r16
            java.util.ArrayList r6 = (java.util.ArrayList) r6
            r16 = 0
            r24 = r8
            r8 = r16
        L_0x0084:
            int r9 = r6.size()
            if (r8 >= r9) goto L_0x01e5
            java.lang.Object r9 = r6.get(r8)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r9 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r9
            r26 = r11
            int r11 = r9.type
            r27 = r12
            r12 = 4
            if (r11 != r12) goto L_0x019e
            r11 = -1
            r12 = 0
            r16 = 0
            r33 = r16
            r16 = r11
            r11 = r33
        L_0x00a3:
            r17 = r12
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r12 = r9.messageObjects
            int r12 = r12.size()
            if (r11 >= r12) goto L_0x00db
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r12 = r9.messageObjects
            java.lang.Object r12 = r12.get(r11)
            im.bclpbkiauv.messenger.MessageObject r12 = (im.bclpbkiauv.messenger.MessageObject) r12
            r28 = r14
            int r14 = r12.getId()
            r17 = r12
            int r12 = r5.getId()
            if (r14 != r12) goto L_0x00d4
            r12 = r11
            int r14 = r5.getId()
            r16 = r12
            boolean r12 = r5.scheduled
            r0.removeFromUploadingMessages(r14, r12)
            r11 = r16
            r12 = r17
            goto L_0x00e1
        L_0x00d4:
            int r11 = r11 + 1
            r12 = r17
            r14 = r28
            goto L_0x00a3
        L_0x00db:
            r28 = r14
            r11 = r16
            r12 = r17
        L_0x00e1:
            if (r11 < 0) goto L_0x0197
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r14 = r9.messageObjects
            r14.remove(r11)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r14 = r9.messages
            r14.remove(r11)
            java.util.ArrayList<java.lang.String> r14 = r9.originalPaths
            r14.remove(r11)
            im.bclpbkiauv.tgnet.TLObject r14 = r9.sendRequest
            if (r14 == 0) goto L_0x0102
            im.bclpbkiauv.tgnet.TLObject r14 = r9.sendRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia r14 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMultiMedia) r14
            r29 = r15
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia> r15 = r14.multi_media
            r15.remove(r11)
            goto L_0x0112
        L_0x0102:
            r29 = r15
            im.bclpbkiauv.tgnet.TLObject r14 = r9.sendEncryptedRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r14 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r14
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage> r15 = r14.messages
            r15.remove(r11)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile> r15 = r14.files
            r15.remove(r11)
        L_0x0112:
            im.bclpbkiauv.messenger.MediaController r14 = im.bclpbkiauv.messenger.MediaController.getInstance()
            r14.cancelVideoConvert(r5)
            java.util.HashMap<java.lang.Object, java.lang.Object> r14 = r9.extraHashMap
            java.lang.Object r14 = r14.get(r12)
            java.lang.String r14 = (java.lang.String) r14
            if (r14 == 0) goto L_0x0126
            r2.add(r14)
        L_0x0126:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r15 = r9.messageObjects
            boolean r15 = r15.isEmpty()
            if (r15 == 0) goto L_0x0136
            r9.sendDelayedRequests()
            r30 = r11
            r31 = r12
            goto L_0x0196
        L_0x0136:
            int r15 = r9.finalGroupMessage
            r30 = r11
            int r11 = r5.getId()
            if (r15 != r11) goto L_0x0189
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r11 = r9.messageObjects
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r15 = r9.messageObjects
            int r15 = r15.size()
            r16 = 1
            int r15 = r15 + -1
            java.lang.Object r11 = r11.get(r15)
            im.bclpbkiauv.messenger.MessageObject r11 = (im.bclpbkiauv.messenger.MessageObject) r11
            int r15 = r11.getId()
            r9.finalGroupMessage = r15
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r11.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r15 = r15.params
            r31 = r12
            java.lang.String r12 = "final"
            r32 = r14
            java.lang.String r14 = "1"
            r15.put(r12, r14)
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_messages r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_messages
            r12.<init>()
            java.util.ArrayList r14 = r12.messages
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r11.messageOwner
            r14.add(r15)
            im.bclpbkiauv.messenger.MessagesStorage r16 = r34.getMessagesStorage()
            long r14 = r9.peer
            r20 = -2
            r21 = 0
            r22 = 0
            r17 = r12
            r18 = r14
            r23 = r7
            r16.putMessages((im.bclpbkiauv.tgnet.TLRPC.messages_Messages) r17, (long) r18, (int) r20, (int) r21, (boolean) r22, (boolean) r23)
            goto L_0x018d
        L_0x0189:
            r31 = r12
            r32 = r14
        L_0x018d:
            boolean r11 = r3.contains(r9)
            if (r11 != 0) goto L_0x0196
            r3.add(r9)
        L_0x0196:
            goto L_0x01ed
        L_0x0197:
            r30 = r11
            r31 = r12
            r29 = r15
            goto L_0x01ed
        L_0x019e:
            r28 = r14
            r29 = r15
            im.bclpbkiauv.messenger.MessageObject r11 = r9.obj
            int r11 = r11.getId()
            int r12 = r5.getId()
            if (r11 != r12) goto L_0x01d9
            int r11 = r5.getId()
            boolean r12 = r5.scheduled
            r0.removeFromUploadingMessages(r11, r12)
            r6.remove(r8)
            r9.sendDelayedRequests()
            im.bclpbkiauv.messenger.MediaController r11 = im.bclpbkiauv.messenger.MediaController.getInstance()
            im.bclpbkiauv.messenger.MessageObject r12 = r9.obj
            r11.cancelVideoConvert(r12)
            int r11 = r6.size()
            if (r11 != 0) goto L_0x01ed
            java.lang.Object r11 = r13.getKey()
            r2.add(r11)
            im.bclpbkiauv.tgnet.TLObject r11 = r9.sendEncryptedRequest
            if (r11 == 0) goto L_0x01ed
            r14 = 1
            goto L_0x01ef
        L_0x01d9:
            int r8 = r8 + 1
            r11 = r26
            r12 = r27
            r14 = r28
            r15 = r29
            goto L_0x0084
        L_0x01e5:
            r26 = r11
            r27 = r12
            r28 = r14
            r29 = r15
        L_0x01ed:
            r14 = r28
        L_0x01ef:
            r8 = r24
            r11 = r26
            r12 = r27
            r15 = r29
            r6 = 1
            goto L_0x006a
        L_0x01fa:
            r24 = r8
            r26 = r11
            r28 = r14
            r29 = r15
            int r10 = r10 + 1
            r16 = r7
            r17 = r24
            goto L_0x001f
        L_0x020a:
            r5 = 0
        L_0x020b:
            int r6 = r2.size()
            if (r5 >= r6) goto L_0x0239
            java.lang.Object r6 = r2.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r7 = "http"
            boolean r7 = r6.startsWith(r7)
            if (r7 == 0) goto L_0x0227
            im.bclpbkiauv.messenger.ImageLoader r7 = im.bclpbkiauv.messenger.ImageLoader.getInstance()
            r7.cancelLoadHttpFile(r6)
            goto L_0x022e
        L_0x0227:
            im.bclpbkiauv.messenger.FileLoader r7 = r34.getFileLoader()
            r7.cancelUploadFile(r6, r14)
        L_0x022e:
            r0.stopVideoService(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r7 = r0.delayedMessages
            r7.remove(r6)
            int r5 = r5 + 1
            goto L_0x020b
        L_0x0239:
            r5 = 0
            int r6 = r3.size()
        L_0x023e:
            r7 = 0
            if (r5 >= r6) goto L_0x024e
            java.lang.Object r8 = r3.get(r5)
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r8 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r8
            r9 = 1
            r0.sendReadyToSendGroup(r8, r7, r9)
            int r5 = r5 + 1
            goto L_0x023e
        L_0x024e:
            r9 = 1
            int r5 = r35.size()
            if (r5 != r9) goto L_0x0275
            java.lang.Object r5 = r1.get(r7)
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            boolean r5 = r5.isEditing()
            if (r5 == 0) goto L_0x0275
            java.lang.Object r5 = r1.get(r7)
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.previousMedia
            if (r5 == 0) goto L_0x0275
            java.lang.Object r5 = r1.get(r7)
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            r0.revertEditingMessageObject(r5)
            goto L_0x0285
        L_0x0275:
            im.bclpbkiauv.messenger.MessagesController r5 = r34.getMessagesController()
            r7 = 0
            r8 = 0
            r12 = 0
            r6 = r4
            r9 = r17
            r11 = r15
            r13 = r16
            r5.deleteMessages(r6, r7, r8, r9, r11, r12, r13)
        L_0x0285:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.cancelSendingMessage(java.util.ArrayList):void");
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.TL_document) null, (String) null, (HashMap<String, String>) null, true, messageObject);
            }
            return false;
        } else if (messageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) {
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (!(messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping) && !(messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend)) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        } else {
            if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken) {
                sendScreenshotMessage(getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.messageOwner.reply_to_msg_id, messageObject.messageOwner);
            }
            if (unsent) {
                this.unsentMessages.put(messageObject.getId(), messageObject);
            }
            sendMessage(messageObject);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(id);
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        TLRPC.WebPage webPage;
        ArrayList<TLRPC.MessageEntity> entities;
        HashMap<String, String> params;
        MessageObject messageObject2 = messageObject;
        long j = did;
        if (messageObject2 != null) {
            if (messageObject2.messageOwner.media == null || (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty) || (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGame) || (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) {
                long j2 = j;
                if (messageObject2.messageOwner.message != null) {
                    if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) {
                        webPage = messageObject2.messageOwner.media.webpage;
                    } else {
                        webPage = null;
                    }
                    if (messageObject2.messageOwner.entities == null || messageObject2.messageOwner.entities.isEmpty()) {
                        entities = null;
                    } else {
                        ArrayList<TLRPC.MessageEntity> entities2 = new ArrayList<>();
                        for (int a = 0; a < messageObject2.messageOwner.entities.size(); a++) {
                            TLRPC.MessageEntity entity = messageObject2.messageOwner.entities.get(a);
                            if ((entity instanceof TLRPC.TL_messageEntityBold) || (entity instanceof TLRPC.TL_messageEntityItalic) || (entity instanceof TLRPC.TL_messageEntityPre) || (entity instanceof TLRPC.TL_messageEntityCode) || (entity instanceof TLRPC.TL_messageEntityTextUrl)) {
                                entities2.add(entity);
                            }
                        }
                        entities = entities2;
                    }
                    sendMessage(messageObject2.messageOwner.message, did, messageObject2.replyMessageObject, webPage, true, entities, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (((int) j2) != 0) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(messageObject2);
                    sendMessage(arrayList, did, true, 0);
                }
            } else {
                if (((int) j) != 0 || messageObject2.messageOwner.to_id == null || (!(messageObject2.messageOwner.media.photo instanceof TLRPC.TL_photo) && !(messageObject2.messageOwner.media.document instanceof TLRPC.TL_document))) {
                    params = null;
                } else {
                    HashMap<String, String> params2 = new HashMap<>();
                    params2.put("parentObject", "sent_" + messageObject2.messageOwner.to_id.channel_id + "_" + messageObject.getId());
                    params = params2;
                }
                if (messageObject2.messageOwner.media.photo instanceof TLRPC.TL_photo) {
                    sendMessage((TLRPC.TL_photo) messageObject2.messageOwner.media.photo, (String) null, did, messageObject2.replyMessageObject, messageObject2.messageOwner.message, messageObject2.messageOwner.entities, (TLRPC.ReplyMarkup) null, params, true, 0, messageObject2.messageOwner.media.ttl_seconds, messageObject);
                    long j3 = did;
                } else if (messageObject2.messageOwner.media.document instanceof TLRPC.TL_document) {
                    sendMessage((TLRPC.TL_document) messageObject2.messageOwner.media.document, (VideoEditedInfo) null, messageObject2.messageOwner.attachPath, did, messageObject2.replyMessageObject, messageObject2.messageOwner.message, messageObject2.messageOwner.entities, (TLRPC.ReplyMarkup) null, params, true, 0, messageObject2.messageOwner.media.ttl_seconds, messageObject);
                    long j4 = did;
                } else {
                    if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) {
                        long j5 = did;
                    } else if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) {
                        long j6 = did;
                    } else if (messageObject2.messageOwner.media.phone_number != null) {
                        TLRPC.User user = new TLRPC.TL_userContact_old2();
                        user.phone = messageObject2.messageOwner.media.phone_number;
                        user.first_name = messageObject2.messageOwner.media.first_name;
                        user.last_name = messageObject2.messageOwner.media.last_name;
                        user.id = messageObject2.messageOwner.media.user_id;
                        sendMessage(user, did, messageObject2.replyMessageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                        long j7 = did;
                        return;
                    } else if (((int) did) != 0) {
                        ArrayList arrayList2 = new ArrayList();
                        arrayList2.add(messageObject2);
                        sendMessage(arrayList2, did, true, 0);
                        return;
                    } else {
                        return;
                    }
                    sendMessage(messageObject2.messageOwner.media, did, messageObject2.replyMessageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
            }
        }
    }

    public void sendScreenshotMessage(TLRPC.User user, int messageId, TLRPC.Message resendMessage) {
        TLRPC.Message message;
        TLRPC.User user2 = user;
        int i = messageId;
        TLRPC.Message message2 = resendMessage;
        if (user2 == null || i == 0) {
        } else if (user2.id == getUserConfig().getClientUserId()) {
        } else {
            TLRPC.TL_messages_sendScreenshotNotification req = new TLRPC.TL_messages_sendScreenshotNotification();
            req.peer = new TLRPC.TL_inputPeerUser();
            req.peer.access_hash = user2.access_hash;
            req.peer.user_id = user2.id;
            if (message2 != null) {
                req.reply_to_msg_id = i;
                req.random_id = message2.random_id;
                message = resendMessage;
            } else {
                TLRPC.Message tL_messageService = new TLRPC.TL_messageService();
                tL_messageService.random_id = getNextRandomId();
                tL_messageService.dialog_id = (long) user2.id;
                tL_messageService.unread = true;
                tL_messageService.out = true;
                int newMessageId = getUserConfig().getNewMessageId();
                tL_messageService.id = newMessageId;
                tL_messageService.local_id = newMessageId;
                tL_messageService.from_id = getUserConfig().getClientUserId();
                tL_messageService.flags |= 256;
                tL_messageService.flags |= 8;
                tL_messageService.reply_to_msg_id = i;
                tL_messageService.to_id = new TLRPC.TL_peerUser();
                tL_messageService.to_id.user_id = user2.id;
                tL_messageService.date = getConnectionsManager().getCurrentTime();
                tL_messageService.action = new TLRPC.TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
                message = tL_messageService;
            }
            req.random_id = message.random_id;
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message, false);
            newMsgObj.messageOwner.send_state = 1;
            ArrayList arrayList = new ArrayList();
            arrayList.add(newMsgObj);
            getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message);
            getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList2, false, true, false, 0, false);
            performSendMessageRequest(req, newMsgObj, (String) null, (DelayedMessage) null, (Object) null, false);
        }
    }

    public void sendSticker(TLRPC.Document document, long peer, MessageObject replyingMessageObject, Object parentObject, boolean notify, int scheduleDate) {
        TLRPC.Document document2;
        TLRPC.Document document3 = document;
        long j = peer;
        if (document3 != null) {
            if (((int) j) != 0) {
                document2 = document3;
            } else if (getMessagesController().getEncryptedChat(Integer.valueOf((int) (j >> 32))) != null) {
                TLRPC.TL_document_layer82 newDocument = new TLRPC.TL_document_layer82();
                newDocument.id = document3.id;
                newDocument.access_hash = document3.access_hash;
                newDocument.date = document3.date;
                newDocument.mime_type = document3.mime_type;
                newDocument.file_reference = document3.file_reference;
                if (newDocument.file_reference == null) {
                    newDocument.file_reference = new byte[0];
                }
                newDocument.size = document3.size;
                newDocument.dc_id = document3.dc_id;
                newDocument.attributes = new ArrayList(document3.attributes);
                if (newDocument.mime_type == null) {
                    newDocument.mime_type = "";
                }
                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document3.thumbs, 90);
                if (thumb instanceof TLRPC.TL_photoSize) {
                    File file = FileLoader.getPathToAttach(thumb, true);
                    if (file.exists()) {
                        try {
                            int length = (int) file.length();
                            byte[] arr = new byte[((int) file.length())];
                            new RandomAccessFile(file, "r").readFully(arr);
                            TLRPC.PhotoSize newThumb = new TLRPC.TL_photoCachedSize();
                            TLRPC.TL_fileLocation_layer82 fileLocation = new TLRPC.TL_fileLocation_layer82();
                            fileLocation.dc_id = thumb.location.dc_id;
                            fileLocation.volume_id = thumb.location.volume_id;
                            fileLocation.local_id = thumb.location.local_id;
                            fileLocation.secret = thumb.location.secret;
                            newThumb.location = fileLocation;
                            newThumb.size = thumb.size;
                            newThumb.w = thumb.w;
                            newThumb.h = thumb.h;
                            newThumb.type = thumb.type;
                            newThumb.bytes = arr;
                            newDocument.thumbs.add(newThumb);
                            newDocument.flags = 1 | newDocument.flags;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                }
                if (newDocument.thumbs.isEmpty()) {
                    TLRPC.PhotoSize thumb2 = new TLRPC.TL_photoSizeEmpty();
                    thumb2.type = "s";
                    newDocument.thumbs.add(thumb2);
                }
                document2 = newDocument;
            } else {
                return;
            }
            if (document2 instanceof TLRPC.TL_document) {
                sendMessage((TLRPC.TL_document) document2, (VideoEditedInfo) null, (String) null, peer, replyingMessageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate, 0, parentObject);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:247:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x076c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0781  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0790  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x07e0  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x080a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r50, long r51, boolean r53, int r54) {
        /*
            r49 = this;
            r15 = r49
            r14 = r50
            r12 = r51
            r11 = r54
            if (r14 == 0) goto L_0x08d9
            boolean r0 = r50.isEmpty()
            if (r0 == 0) goto L_0x0017
            r3 = r12
            r5 = r14
            r0 = r15
            r37 = 0
            goto L_0x08de
        L_0x0017:
            int r4 = (int) r12
            r0 = 0
            if (r4 == 0) goto L_0x08bc
            im.bclpbkiauv.messenger.MessagesController r1 = r49.getMessagesController()
            int r2 = (int) r12
            im.bclpbkiauv.tgnet.TLRPC$Peer r2 = r1.getPeer(r2)
            r1 = 0
            r3 = 0
            r5 = 1
            r6 = 1
            r7 = 1
            r8 = 1
            if (r4 <= 0) goto L_0x004b
            im.bclpbkiauv.messenger.MessagesController r9 = r49.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r9 = r9.getUser(r10)
            if (r9 != 0) goto L_0x003c
            r10 = 0
            return r10
        L_0x003c:
            r9 = 0
            r19 = r1
            r20 = r3
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r10 = r9
            goto L_0x007f
        L_0x004b:
            im.bclpbkiauv.messenger.MessagesController r9 = r49.getMessagesController()
            int r10 = -r4
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            boolean r10 = im.bclpbkiauv.messenger.ChatObject.isChannel(r9)
            if (r10 == 0) goto L_0x0062
            boolean r1 = r9.megagroup
            boolean r3 = r9.signatures
        L_0x0062:
            boolean r5 = im.bclpbkiauv.messenger.ChatObject.canSendStickers(r9)
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.canSendMedia(r9)
            boolean r8 = im.bclpbkiauv.messenger.ChatObject.canSendEmbed(r9)
            boolean r7 = im.bclpbkiauv.messenger.ChatObject.canSendPolls(r9)
            r19 = r1
            r20 = r3
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r10 = r9
        L_0x007f:
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            r3 = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r7 = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r8 = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r9 = r6
            im.bclpbkiauv.messenger.MessagesController r6 = r49.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r6 = r6.getInputPeer(r4)
            r25 = 0
            im.bclpbkiauv.messenger.UserConfig r17 = r49.getUserConfig()
            r18 = r4
            int r4 = r17.getClientUserId()
            r17 = r0
            r27 = r1
            long r0 = (long) r4
            r28 = r5
            int r29 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r29 != 0) goto L_0x00c2
            r0 = 1
            goto L_0x00c3
        L_0x00c2:
            r0 = 0
        L_0x00c3:
            r1 = r6
            r6 = r0
            r0 = 0
            r30 = r8
            r31 = r9
            r9 = r28
            r8 = r7
            r7 = r27
            r27 = r17
        L_0x00d1:
            int r5 = r50.size()
            if (r0 >= r5) goto L_0x08a0
            java.lang.Object r5 = r14.get(r0)
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            int r17 = r5.getId()
            if (r17 <= 0) goto L_0x0814
            boolean r17 = r5.needDrawBluredPreview()
            if (r17 == 0) goto L_0x0108
            r14 = r0
            r41 = r1
            r45 = r4
            r17 = r5
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r32 = r18
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            goto L_0x0831
        L_0x0108:
            r29 = r6
            if (r21 != 0) goto L_0x0175
            boolean r32 = r5.isSticker()
            if (r32 != 0) goto L_0x0124
            boolean r32 = r5.isAnimatedSticker()
            if (r32 != 0) goto L_0x0124
            boolean r32 = r5.isGif()
            if (r32 != 0) goto L_0x0124
            boolean r32 = r5.isGame()
            if (r32 == 0) goto L_0x0175
        L_0x0124:
            if (r27 != 0) goto L_0x0152
            r6 = 8
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.isActionBannedByDefault(r10, r6)
            if (r6 == 0) goto L_0x0131
            r32 = 4
            goto L_0x0133
        L_0x0131:
            r32 = 1
        L_0x0133:
            r27 = r32
            r33 = r0
            r41 = r1
            r45 = r4
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0886
        L_0x0152:
            r33 = r0
            r41 = r1
            r45 = r4
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0880
        L_0x0175:
            if (r22 != 0) goto L_0x01d5
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r6 != 0) goto L_0x0187
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r6 == 0) goto L_0x01d5
        L_0x0187:
            if (r27 != 0) goto L_0x01b2
            r6 = 7
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.isActionBannedByDefault(r10, r6)
            if (r6 == 0) goto L_0x0192
            r6 = 5
            goto L_0x0193
        L_0x0192:
            r6 = 2
        L_0x0193:
            r27 = r6
            r33 = r0
            r41 = r1
            r45 = r4
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0886
        L_0x01b2:
            r33 = r0
            r41 = r1
            r45 = r4
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0880
        L_0x01d5:
            if (r23 != 0) goto L_0x022e
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r6 == 0) goto L_0x022e
            if (r27 != 0) goto L_0x020b
            r6 = 10
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.isActionBannedByDefault(r10, r6)
            if (r6 == 0) goto L_0x01eb
            r6 = 6
            goto L_0x01ec
        L_0x01eb:
            r6 = 3
        L_0x01ec:
            r27 = r6
            r33 = r0
            r41 = r1
            r45 = r4
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0886
        L_0x020b:
            r33 = r0
            r41 = r1
            r45 = r4
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r6 = r29
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x0880
        L_0x022e:
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_message r34 = new im.bclpbkiauv.tgnet.TLRPC$TL_message
            r34.<init>()
            r35 = r34
            long r36 = r5.getDialogId()
            r38 = r9
            r34 = r10
            long r9 = (long) r4
            int r39 = (r36 > r9 ? 1 : (r36 == r9 ? 0 : -1))
            if (r39 != 0) goto L_0x0253
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            int r9 = r9.from_id
            im.bclpbkiauv.messenger.UserConfig r10 = r49.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r9 != r10) goto L_0x0253
            r9 = 1
            goto L_0x0254
        L_0x0253:
            r9 = 0
        L_0x0254:
            r36 = r9
            boolean r9 = r5.isForwarded()
            if (r9 == 0) goto L_0x02b4
            im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader
            r9.<init>()
            r10 = r35
            r10.fwd_from = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r10.fwd_from
            r35 = r6
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            int r6 = r6.flags
            r9.flags = r6
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.from_id
            r6.from_id = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.date
            r6.date = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.channel_id
            r6.channel_id = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.channel_post
            r6.channel_post = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            java.lang.String r9 = r9.post_author
            r6.post_author = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            java.lang.String r9 = r9.from_name
            r6.from_name = r9
            r6 = 4
            r10.flags = r6
            r33 = r7
            goto L_0x0386
        L_0x02b4:
            r10 = r35
            r35 = r6
            if (r36 != 0) goto L_0x0384
            im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageFwdHeader
            r6.<init>()
            r10.fwd_from = r6
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r5.getId()
            r6.channel_post = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r6.flags
            r32 = 4
            r9 = r9 | 4
            r6.flags = r9
            boolean r6 = r5.isFromUser()
            if (r6 == 0) goto L_0x02ec
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            int r9 = r9.from_id
            r6.from_id = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r6.flags
            r28 = 1
            r9 = r9 | 1
            r6.flags = r9
            goto L_0x031e
        L_0x02ec:
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r9 = r9.to_id
            int r9 = r9.channel_id
            r6.channel_id = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r6.flags
            r33 = 2
            r9 = r9 | 2
            r6.flags = r9
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            boolean r6 = r6.post
            if (r6 == 0) goto L_0x031e
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.from_id
            if (r6 <= 0) goto L_0x031e
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            int r9 = r9.from_id
            r6.from_id = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r6.flags
            r28 = 1
            r9 = r9 | 1
            r6.flags = r9
        L_0x031e:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            java.lang.String r6 = r6.post_author
            if (r6 == 0) goto L_0x0339
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            java.lang.String r9 = r9.post_author
            r6.post_author = r9
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r9 = r6.flags
            r17 = 8
            r9 = r9 | 8
            r6.flags = r9
            r33 = r7
            goto L_0x037a
        L_0x0339:
            boolean r6 = r5.isOutOwner()
            if (r6 != 0) goto L_0x0378
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.from_id
            if (r6 <= 0) goto L_0x0378
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            boolean r6 = r6.post
            if (r6 == 0) goto L_0x0378
            im.bclpbkiauv.messenger.MessagesController r6 = r49.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            int r9 = r9.from_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            im.bclpbkiauv.tgnet.TLRPC$User r6 = r6.getUser(r9)
            if (r6 == 0) goto L_0x0375
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r9 = r10.fwd_from
            r33 = r7
            java.lang.String r7 = r6.first_name
            java.lang.String r15 = r6.last_name
            java.lang.String r7 = im.bclpbkiauv.messenger.ContactsController.formatName(r7, r15)
            r9.post_author = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r7 = r10.fwd_from
            int r9 = r7.flags
            r15 = 8
            r9 = r9 | r15
            r7.flags = r9
            goto L_0x037a
        L_0x0375:
            r33 = r7
            goto L_0x037a
        L_0x0378:
            r33 = r7
        L_0x037a:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.date
            r10.date = r6
            r6 = 4
            r10.flags = r6
            goto L_0x0386
        L_0x0384:
            r33 = r7
        L_0x0386:
            long r6 = (long) r4
            int r9 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r9 != 0) goto L_0x03a7
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            if (r6 == 0) goto L_0x03a7
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r7 = r6.flags
            r7 = r7 | 16
            r6.flags = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r7 = r5.getId()
            r6.saved_from_msg_id = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r7 = r7.to_id
            r6.saved_from_peer = r7
        L_0x03a7:
            if (r24 != 0) goto L_0x03b9
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaWebPage
            if (r6 == 0) goto L_0x03b9
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty
            r6.<init>()
            r10.media = r6
            goto L_0x03bf
        L_0x03b9:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            r10.media = r6
        L_0x03bf:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r10.media
            if (r6 == 0) goto L_0x03c9
            int r6 = r10.flags
            r6 = r6 | 512(0x200, float:7.175E-43)
            r10.flags = r6
        L_0x03c9:
            if (r19 == 0) goto L_0x03d2
            int r6 = r10.flags
            r7 = -2147483648(0xffffffff80000000, float:-0.0)
            r6 = r6 | r7
            r10.flags = r6
        L_0x03d2:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.via_bot_id
            if (r6 == 0) goto L_0x03e4
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.via_bot_id
            r10.via_bot_id = r6
            int r6 = r10.flags
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r10.flags = r6
        L_0x03e4:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            java.lang.String r6 = r6.message
            r10.message = r6
            int r6 = r5.getId()
            r10.fwd_msg_id = r6
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            java.lang.String r6 = r6.attachPath
            r10.attachPath = r6
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r6 = r6.entities
            r10.entities = r6
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_replyInlineMarkup
            if (r6 == 0) goto L_0x04aa
            int r6 = r10.flags
            r6 = r6 | 64
            r10.flags = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_replyInlineMarkup r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_replyInlineMarkup
            r6.<init>()
            r10.reply_markup = r6
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r7 = r7.reply_markup
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow> r7 = r7.rows
            int r7 = r7.size()
        L_0x041c:
            if (r6 >= r7) goto L_0x04a5
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r9 = r9.reply_markup
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow> r9 = r9.rows
            java.lang.Object r9 = r9.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow r9 = (im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonRow) r9
            r15 = 0
            r17 = 0
            r32 = r4
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r4 = r9.buttons
            int r4 = r4.size()
            r48 = r17
            r17 = r7
            r7 = r48
        L_0x043b:
            if (r7 >= r4) goto L_0x0497
            r37 = r4
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r4 = r9.buttons
            java.lang.Object r4 = r4.get(r7)
            im.bclpbkiauv.tgnet.TLRPC$KeyboardButton r4 = (im.bclpbkiauv.tgnet.TLRPC.KeyboardButton) r4
            r39 = r9
            boolean r9 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonUrlAuth
            if (r9 != 0) goto L_0x0455
            boolean r9 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonUrl
            if (r9 != 0) goto L_0x0455
            boolean r9 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonSwitchInline
            if (r9 == 0) goto L_0x048e
        L_0x0455:
            boolean r9 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonUrlAuth
            if (r9 == 0) goto L_0x047a
            im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonUrlAuth r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r9.<init>()
            int r12 = r4.flags
            r9.flags = r12
            java.lang.String r12 = r4.fwd_text
            if (r12 == 0) goto L_0x046d
            java.lang.String r12 = r4.fwd_text
            r9.fwd_text = r12
            r9.text = r12
            goto L_0x0471
        L_0x046d:
            java.lang.String r12 = r4.text
            r9.text = r12
        L_0x0471:
            java.lang.String r12 = r4.url
            r9.url = r12
            int r12 = r4.button_id
            r9.button_id = r12
            r4 = r9
        L_0x047a:
            if (r15 != 0) goto L_0x0489
            im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow r9 = new im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow
            r9.<init>()
            r15 = r9
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r9 = r10.reply_markup
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow> r9 = r9.rows
            r9.add(r15)
        L_0x0489:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r9 = r15.buttons
            r9.add(r4)
        L_0x048e:
            int r7 = r7 + 1
            r12 = r51
            r4 = r37
            r9 = r39
            goto L_0x043b
        L_0x0497:
            r37 = r4
            r39 = r9
            int r6 = r6 + 1
            r12 = r51
            r7 = r17
            r4 = r32
            goto L_0x041c
        L_0x04a5:
            r32 = r4
            r17 = r7
            goto L_0x04ac
        L_0x04aa:
            r32 = r4
        L_0x04ac:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r4 = r10.entities
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x04ba
            int r4 = r10.flags
            r4 = r4 | 128(0x80, float:1.794E-43)
            r10.flags = r4
        L_0x04ba:
            java.lang.String r4 = r10.attachPath
            if (r4 != 0) goto L_0x04c2
            java.lang.String r4 = ""
            r10.attachPath = r4
        L_0x04c2:
            im.bclpbkiauv.messenger.UserConfig r4 = r49.getUserConfig()
            int r4 = r4.getNewMessageId()
            r10.id = r4
            r10.local_id = r4
            r4 = 1
            r10.out = r4
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r5.messageOwner
            long r6 = r4.grouped_id
            r12 = r6
            r39 = 0
            int r4 = (r6 > r39 ? 1 : (r6 == r39 ? 0 : -1))
            if (r4 == 0) goto L_0x0506
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r5.messageOwner
            long r6 = r4.grouped_id
            java.lang.Object r4 = r3.get(r6)
            java.lang.Long r4 = (java.lang.Long) r4
            if (r4 != 0) goto L_0x04f9
            java.security.SecureRandom r6 = im.bclpbkiauv.messenger.Utilities.random
            long r6 = r6.nextLong()
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r5.messageOwner
            long r6 = r6.grouped_id
            r3.put(r6, r4)
        L_0x04f9:
            long r6 = r4.longValue()
            r10.grouped_id = r6
            int r6 = r10.flags
            r7 = 131072(0x20000, float:1.83671E-40)
            r6 = r6 | r7
            r10.flags = r6
        L_0x0506:
            int r4 = r50.size()
            r6 = 1
            int r4 = r4 - r6
            if (r0 == r4) goto L_0x0529
            int r4 = r0 + 1
            java.lang.Object r4 = r14.get(r4)
            im.bclpbkiauv.messenger.MessageObject r4 = (im.bclpbkiauv.messenger.MessageObject) r4
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r4.messageOwner
            long r6 = r6.grouped_id
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            r15 = r3
            r17 = r4
            long r3 = r9.grouped_id
            int r9 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r9 == 0) goto L_0x052a
            r6 = 1
            r35 = r6
            goto L_0x052a
        L_0x0529:
            r15 = r3
        L_0x052a:
            int r3 = r2.channel_id
            if (r3 == 0) goto L_0x0544
            if (r19 != 0) goto L_0x0544
            if (r20 == 0) goto L_0x053b
            im.bclpbkiauv.messenger.UserConfig r3 = r49.getUserConfig()
            int r3 = r3.getClientUserId()
            goto L_0x053e
        L_0x053b:
            int r3 = r2.channel_id
            int r3 = -r3
        L_0x053e:
            r10.from_id = r3
            r3 = 1
            r10.post = r3
            goto L_0x0554
        L_0x0544:
            im.bclpbkiauv.messenger.UserConfig r3 = r49.getUserConfig()
            int r3 = r3.getClientUserId()
            r10.from_id = r3
            int r3 = r10.flags
            r3 = r3 | 256(0x100, float:3.59E-43)
            r10.flags = r3
        L_0x0554:
            long r3 = r10.random_id
            int r6 = (r3 > r39 ? 1 : (r3 == r39 ? 0 : -1))
            if (r6 != 0) goto L_0x0560
            long r3 = r49.getNextRandomId()
            r10.random_id = r3
        L_0x0560:
            long r3 = r10.random_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r8.add(r3)
            long r3 = r10.random_id
            r6 = r31
            r6.put(r3, r10)
            int r3 = r10.fwd_msg_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = r30
            r4.add(r3)
            if (r11 == 0) goto L_0x057f
            r3 = r11
            goto L_0x0587
        L_0x057f:
            im.bclpbkiauv.tgnet.ConnectionsManager r3 = r49.getConnectionsManager()
            int r3 = r3.getCurrentTime()
        L_0x0587:
            r10.date = r3
            boolean r3 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputPeerChannel
            if (r3 == 0) goto L_0x059b
            if (r19 != 0) goto L_0x059b
            if (r11 != 0) goto L_0x05b4
            r3 = 1
            r10.views = r3
            int r3 = r10.flags
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r10.flags = r3
            goto L_0x05b4
        L_0x059b:
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r5.messageOwner
            int r3 = r3.flags
            r3 = r3 & 1024(0x400, float:1.435E-42)
            if (r3 == 0) goto L_0x05b1
            if (r11 != 0) goto L_0x05b1
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r5.messageOwner
            int r3 = r3.views
            r10.views = r3
            int r3 = r10.flags
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r10.flags = r3
        L_0x05b1:
            r3 = 1
            r10.unread = r3
        L_0x05b4:
            r17 = r6
            r6 = r51
            r10.dialog_id = r6
            r10.to_id = r2
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isVoiceMessage(r10)
            if (r3 != 0) goto L_0x05c8
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoMessage(r10)
            if (r3 == 0) goto L_0x05dc
        L_0x05c8:
            boolean r3 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputPeerChannel
            if (r3 == 0) goto L_0x05d9
            int r3 = r5.getChannelId()
            if (r3 == 0) goto L_0x05d9
            boolean r3 = r5.isContentUnread()
            r10.media_unread = r3
            goto L_0x05dc
        L_0x05d9:
            r3 = 1
            r10.media_unread = r3
        L_0x05dc:
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r3 = r3.to_id
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_peerChannel
            if (r3 == 0) goto L_0x05ed
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r3 = r3.to_id
            int r3 = r3.channel_id
            int r3 = -r3
            r10.ttl = r3
        L_0x05ed:
            im.bclpbkiauv.messenger.MessageObject r3 = new im.bclpbkiauv.messenger.MessageObject
            r9 = r49
            r30 = r2
            int r2 = r9.currentAccount
            r31 = r15
            r15 = 1
            r3.<init>(r2, r10, r15)
            r15 = r3
            if (r11 == 0) goto L_0x0600
            r2 = 1
            goto L_0x0601
        L_0x0600:
            r2 = 0
        L_0x0601:
            r15.scheduled = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r15.messageOwner
            r3 = 1
            r2.send_state = r3
            r2 = r33
            r2.add(r15)
            r3 = r38
            r3.add(r10)
            r33 = r15
            if (r11 == 0) goto L_0x0618
            r15 = 1
            goto L_0x0619
        L_0x0618:
            r15 = 0
        L_0x0619:
            r9.putToSendingMessages(r10, r15)
            r15 = 0
            boolean r37 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r37 == 0) goto L_0x065b
            r37 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r38 = r15
            java.lang.String r15 = "forward message user_id = "
            r10.append(r15)
            int r15 = r1.user_id
            r10.append(r15)
            java.lang.String r15 = " chat_id = "
            r10.append(r15)
            int r15 = r1.chat_id
            r10.append(r15)
            java.lang.String r15 = " channel_id = "
            r10.append(r15)
            int r15 = r1.channel_id
            r10.append(r15)
            java.lang.String r15 = " access_hash = "
            r10.append(r15)
            r15 = r8
            long r8 = r1.access_hash
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            im.bclpbkiauv.messenger.FileLog.d(r8)
            goto L_0x0660
        L_0x065b:
            r37 = r10
            r38 = r15
            r15 = r8
        L_0x0660:
            if (r35 == 0) goto L_0x0668
            int r8 = r3.size()
            if (r8 > 0) goto L_0x06b2
        L_0x0668:
            int r8 = r3.size()
            r9 = 100
            if (r8 == r9) goto L_0x06b2
            int r8 = r50.size()
            r9 = 1
            int r8 = r8 - r9
            if (r0 == r8) goto L_0x06b2
            int r8 = r50.size()
            int r8 = r8 - r9
            if (r0 == r8) goto L_0x0694
            int r8 = r0 + 1
            java.lang.Object r8 = r14.get(r8)
            im.bclpbkiauv.messenger.MessageObject r8 = (im.bclpbkiauv.messenger.MessageObject) r8
            long r8 = r8.getDialogId()
            long r41 = r5.getDialogId()
            int r10 = (r8 > r41 ? 1 : (r8 == r41 ? 0 : -1))
            if (r10 == 0) goto L_0x0694
            goto L_0x06b2
        L_0x0694:
            r33 = r0
            r41 = r1
            r42 = r2
            r40 = r3
            r46 = r4
            r3 = r6
            r5 = r14
            r28 = r15
            r6 = r29
            r45 = r32
            r37 = 0
            r39 = 1
            r0 = r49
            r29 = r17
            r32 = r18
            goto L_0x0880
        L_0x06b2:
            im.bclpbkiauv.messenger.MessagesStorage r41 = r49.getMessagesStorage()
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>(r3)
            r43 = 0
            r44 = 1
            r45 = 0
            r46 = 0
            if (r11 == 0) goto L_0x06c8
            r47 = 1
            goto L_0x06ca
        L_0x06c8:
            r47 = 0
        L_0x06ca:
            r42 = r8
            r41.putMessages((java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message>) r42, (boolean) r43, (boolean) r44, (boolean) r45, (int) r46, (boolean) r47)
            im.bclpbkiauv.messenger.MessagesController r8 = r49.getMessagesController()
            if (r11 == 0) goto L_0x06d7
            r9 = 1
            goto L_0x06d8
        L_0x06d7:
            r9 = 0
        L_0x06d8:
            r8.updateInterfaceWithMessages(r6, r2, r9)
            im.bclpbkiauv.messenger.NotificationCenter r8 = r49.getNotificationCenter()
            int r9 = im.bclpbkiauv.messenger.NotificationCenter.dialogsNeedReload
            r41 = r0
            r10 = 0
            java.lang.Object[] r0 = new java.lang.Object[r10]
            r8.postNotificationName(r9, r0)
            im.bclpbkiauv.messenger.UserConfig r0 = r49.getUserConfig()
            r0.saveConfig(r10)
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_forwardMessages r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_forwardMessages
            r0.<init>()
            r10 = r0
            r10.to_peer = r1
            int r0 = (r12 > r39 ? 1 : (r12 == r39 ? 0 : -1))
            if (r0 == 0) goto L_0x06fe
            r0 = 1
            goto L_0x06ff
        L_0x06fe:
            r0 = 0
        L_0x06ff:
            r10.grouped = r0
            if (r53 == 0) goto L_0x0726
            r0 = r49
            int r8 = r0.currentAccount
            android.content.SharedPreferences r8 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r8)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "silent_"
            r9.append(r0)
            r9.append(r6)
            java.lang.String r0 = r9.toString()
            r9 = 0
            boolean r0 = r8.getBoolean(r0, r9)
            if (r0 == 0) goto L_0x0724
            goto L_0x0726
        L_0x0724:
            r0 = 0
            goto L_0x0727
        L_0x0726:
            r0 = 1
        L_0x0727:
            r10.silent = r0
            if (r11 == 0) goto L_0x0733
            r10.schedule_date = r11
            int r0 = r10.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r10.flags = r0
        L_0x0733:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_peerChannel
            if (r0 == 0) goto L_0x076c
            im.bclpbkiauv.messenger.MessagesController r0 = r49.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r8 = r8.to_id
            int r8 = r8.channel_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r0.getChat(r8)
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel
            r8.<init>()
            r10.from_peer = r8
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r8 = r10.from_peer
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r9 = r9.to_id
            int r9 = r9.channel_id
            r8.channel_id = r9
            if (r0 == 0) goto L_0x0769
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r8 = r10.from_peer
            r39 = r5
            long r5 = r0.access_hash
            r8.access_hash = r5
            goto L_0x076b
        L_0x0769:
            r39 = r5
        L_0x076b:
            goto L_0x0775
        L_0x076c:
            r39 = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r10.from_peer = r0
        L_0x0775:
            r7 = r15
            r10.random_id = r7
            r10.id = r4
            int r0 = r50.size()
            r5 = 1
            if (r0 != r5) goto L_0x0790
            r6 = 0
            java.lang.Object r0 = r14.get(r6)
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x0791
            r0 = 1
            goto L_0x0792
        L_0x0790:
            r6 = 0
        L_0x0791:
            r0 = 0
        L_0x0792:
            r10.with_my_score = r0
            r28 = r7
            r8 = r3
            r40 = r3
            r9 = r2
            r42 = r2
            r7 = r17
            r15 = r39
            r39 = 1
            r5 = r19
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = r49.getConnectionsManager()
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$ZovLiOWozB1TSfZhWARH5uW6PTU r3 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$ZovLiOWozB1TSfZhWARH5uW6PTU
            r14 = r41
            r0 = r3
            r41 = r1
            r1 = r49
            r43 = r12
            r12 = r2
            r13 = r3
            r2 = r51
            r46 = r4
            r45 = r32
            r32 = r18
            r4 = r54
            r6 = r10
            r16 = r37
            r37 = 0
            r10 = r30
            r11 = r6
            r48 = r15
            r15 = r6
            r6 = r29
            r29 = r17
            r17 = r48
            r0.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11)
            r0 = 68
            r12.sendRequest(r15, r13, r0)
            int r0 = r50.size()
            int r0 = r0 + -1
            if (r14 == r0) goto L_0x080a
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            r5 = r50
            r7 = r0
            r9 = r1
            r8 = r2
            r46 = r3
            r29 = r4
            r33 = r14
            r0 = r49
            r3 = r51
            goto L_0x0886
        L_0x080a:
            r0 = r49
            r5 = r50
            r3 = r51
            r33 = r14
            goto L_0x0880
        L_0x0814:
            r14 = r0
            r41 = r1
            r45 = r4
            r17 = r5
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r32 = r18
            r46 = r30
            r29 = r31
            r37 = 0
            r39 = 1
            r30 = r2
            r31 = r3
        L_0x0831:
            r0 = r17
            int r1 = r0.type
            if (r1 != 0) goto L_0x0877
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0877
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            if (r1 == 0) goto L_0x084c
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r1.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r1 = r1.webpage
            goto L_0x084d
        L_0x084c:
            r1 = 0
        L_0x084d:
            java.lang.CharSequence r2 = r0.messageText
            java.lang.String r8 = r2.toString()
            r11 = 0
            if (r1 == 0) goto L_0x0858
            r13 = 1
            goto L_0x0859
        L_0x0858:
            r13 = 0
        L_0x0859:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            r15 = 0
            r16 = 0
            r7 = r49
            r9 = r51
            r3 = r51
            r12 = r1
            r5 = r50
            r33 = r14
            r14 = r2
            r2 = r0
            r0 = r49
            r17 = r53
            r18 = r54
            r7.sendMessage(r8, r9, r11, r12, r13, r14, r15, r16, r17, r18)
            goto L_0x0880
        L_0x0877:
            r5 = r50
            r3 = r51
            r2 = r0
            r33 = r14
            r0 = r49
        L_0x0880:
            r8 = r28
            r9 = r40
            r7 = r42
        L_0x0886:
            int r1 = r33 + 1
            r11 = r54
            r15 = r0
            r0 = r1
            r12 = r3
            r14 = r5
            r2 = r30
            r3 = r31
            r18 = r32
            r10 = r34
            r1 = r41
            r4 = r45
            r30 = r46
            r31 = r29
            goto L_0x00d1
        L_0x08a0:
            r33 = r0
            r41 = r1
            r45 = r4
            r42 = r7
            r28 = r8
            r40 = r9
            r34 = r10
            r5 = r14
            r0 = r15
            r32 = r18
            r46 = r30
            r29 = r31
            r30 = r2
            r31 = r3
            r3 = r12
            goto L_0x08d8
        L_0x08bc:
            r17 = r0
            r32 = r4
            r3 = r12
            r5 = r14
            r0 = r15
            r1 = 0
        L_0x08c4:
            int r2 = r50.size()
            if (r1 >= r2) goto L_0x08d6
            java.lang.Object r2 = r5.get(r1)
            im.bclpbkiauv.messenger.MessageObject r2 = (im.bclpbkiauv.messenger.MessageObject) r2
            r0.processForwardFromMyName(r2, r3)
            int r1 = r1 + 1
            goto L_0x08c4
        L_0x08d6:
            r27 = r17
        L_0x08d8:
            return r27
        L_0x08d9:
            r3 = r12
            r5 = r14
            r0 = r15
            r37 = 0
        L_0x08de:
            return r37
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    public /* synthetic */ void lambda$sendMessage$9$SendMessagesHelper(long peer, int scheduleDate, boolean isMegagroupFinal, boolean toMyself, LongSparseArray messagesByRandomIdsFinal, ArrayList newMsgObjArr, ArrayList newMsgArr, TLRPC.Peer to_id, TLRPC.TL_messages_forwardMessages req, TLObject response, TLRPC.TL_error error) {
        SendMessagesHelper sendMessagesHelper;
        Integer value;
        SparseLongArray newMessagesByIds;
        TLRPC.Updates updates;
        int i;
        TLRPC.Message message;
        int sentCount;
        int i2 = scheduleDate;
        ArrayList arrayList = newMsgObjArr;
        ArrayList arrayList2 = newMsgArr;
        TLRPC.TL_error tL_error = error;
        if (tL_error == null) {
            SparseLongArray newMessagesByIds2 = new SparseLongArray();
            TLRPC.Updates updates2 = (TLRPC.Updates) response;
            int a1 = 0;
            while (a1 < updates2.updates.size()) {
                TLRPC.Update update = updates2.updates.get(a1);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    TLRPC.TL_updateMessageID updateMessageID = (TLRPC.TL_updateMessageID) update;
                    newMessagesByIds2.put(updateMessageID.id, updateMessageID.random_id);
                    updates2.updates.remove(a1);
                    a1--;
                }
                a1++;
            }
            Integer value2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(peer));
            if (i2 != 0) {
                long j = peer;
                value = 0;
            } else if (value2 == null) {
                Integer value3 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, peer));
                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(peer), value3);
                value = value3;
            } else {
                long j2 = peer;
                value = value2;
            }
            int a12 = 0;
            int sentCount2 = 0;
            while (a12 < updates2.updates.size()) {
                TLRPC.Update update2 = updates2.updates.get(a12);
                if ((update2 instanceof TLRPC.TL_updateNewMessage) || (update2 instanceof TLRPC.TL_updateNewChannelMessage) || (update2 instanceof TLRPC.TL_updateNewScheduledMessage)) {
                    updates2.updates.remove(a12);
                    int a13 = a12 - 1;
                    if (update2 instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.TL_updateNewMessage updateNewMessage = (TLRPC.TL_updateNewMessage) update2;
                        getMessagesController().processNewDifferenceParams(-1, updateNewMessage.pts, -1, updateNewMessage.pts_count);
                        message = updateNewMessage.message;
                    } else if (update2 instanceof TLRPC.TL_updateNewScheduledMessage) {
                        message = ((TLRPC.TL_updateNewScheduledMessage) update2).message;
                    } else {
                        TLRPC.TL_updateNewChannelMessage updateNewChannelMessage = (TLRPC.TL_updateNewChannelMessage) update2;
                        TLRPC.Message message2 = updateNewChannelMessage.message;
                        TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage = updateNewChannelMessage;
                        getMessagesController().processNewChannelDifferenceParams(updateNewChannelMessage.pts, updateNewChannelMessage.pts_count, message2.to_id.channel_id);
                        if (isMegagroupFinal) {
                            message2.flags |= Integer.MIN_VALUE;
                        }
                        message = message2;
                    }
                    ImageLoader.saveMessageThumbs(message);
                    if (i2 == 0) {
                        message.unread = value.intValue() < message.id;
                    }
                    if (toMyself) {
                        message.out = true;
                        message.unread = false;
                        message.media_unread = false;
                    }
                    long random_id = newMessagesByIds2.get(message.id);
                    if (random_id != 0) {
                        TLRPC.Message newMsgObj1 = (TLRPC.Message) messagesByRandomIdsFinal.get(random_id);
                        if (newMsgObj1 == null) {
                            sentCount = sentCount2;
                            newMessagesByIds = newMessagesByIds2;
                            updates = updates2;
                            i = 1;
                        } else {
                            int index = arrayList.indexOf(newMsgObj1);
                            if (index == -1) {
                                sentCount = sentCount2;
                                newMessagesByIds = newMessagesByIds2;
                                updates = updates2;
                                i = 1;
                            } else {
                                MessageObject msgObj1 = (MessageObject) arrayList2.get(index);
                                arrayList.remove(index);
                                arrayList2.remove(index);
                                int oldId = newMsgObj1.id;
                                int i3 = index;
                                ArrayList<TLRPC.Message> sentMessages = new ArrayList<>();
                                sentMessages.add(message);
                                long j3 = random_id;
                                TLRPC.Update update3 = update2;
                                updateMediaPaths(msgObj1, message, message.id, (String) null, true);
                                int existFlags = msgObj1.getMediaExistanceFlags();
                                newMsgObj1.id = message.id;
                                $$Lambda$SendMessagesHelper$EEL3dHo3eSywtjIGUhW1mT1M_9s r14 = r0;
                                TLRPC.Message message3 = newMsgObj1;
                                newMessagesByIds = newMessagesByIds2;
                                updates = updates2;
                                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                                i = 1;
                                TLRPC.Message message4 = message;
                                $$Lambda$SendMessagesHelper$EEL3dHo3eSywtjIGUhW1mT1M_9s r0 = new Runnable(newMsgObj1, oldId, to_id, scheduleDate, sentMessages, peer, message, existFlags) {
                                    private final /* synthetic */ TLRPC.Message f$1;
                                    private final /* synthetic */ int f$2;
                                    private final /* synthetic */ TLRPC.Peer f$3;
                                    private final /* synthetic */ int f$4;
                                    private final /* synthetic */ ArrayList f$5;
                                    private final /* synthetic */ long f$6;
                                    private final /* synthetic */ TLRPC.Message f$7;
                                    private final /* synthetic */ int f$8;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                        this.f$4 = r5;
                                        this.f$5 = r6;
                                        this.f$6 = r7;
                                        this.f$7 = r9;
                                        this.f$8 = r10;
                                    }

                                    public final void run() {
                                        SendMessagesHelper.this.lambda$null$6$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                                    }
                                };
                                storageQueue.postRunnable(r14);
                                a12 = a13;
                                sentCount2++;
                            }
                        }
                    } else {
                        TLRPC.Update update4 = update2;
                        sentCount = sentCount2;
                        newMessagesByIds = newMessagesByIds2;
                        updates = updates2;
                        TLRPC.Message message5 = message;
                        i = 1;
                    }
                    sentCount2 = sentCount;
                    a12 = a13;
                } else {
                    newMessagesByIds = newMessagesByIds2;
                    updates = updates2;
                    i = 1;
                }
                a12 += i;
                long j4 = peer;
                arrayList = newMsgObjArr;
                arrayList2 = newMsgArr;
                updates2 = updates;
                newMessagesByIds2 = newMessagesByIds;
                i2 = scheduleDate;
            }
            int sentCount3 = sentCount2;
            SparseLongArray sparseLongArray = newMessagesByIds2;
            TLRPC.Updates updates3 = updates2;
            if (!updates3.updates.isEmpty()) {
                getMessagesController().processUpdates(updates3, false);
            }
            getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, sentCount3);
            sendMessagesHelper = this;
            TLRPC.TL_messages_forwardMessages tL_messages_forwardMessages = req;
        } else {
            sendMessagesHelper = this;
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.TL_messages_forwardMessages f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$7$SendMessagesHelper(this.f$1, this.f$2);
                }
            });
        }
        for (int a14 = 0; a14 < newMsgObjArr.size(); a14++) {
            TLRPC.Message newMsgObj12 = (TLRPC.Message) newMsgObjArr.get(a14);
            int i4 = scheduleDate;
            getMessagesStorage().markMessageAsSendError(newMsgObj12, i4 != 0);
            AndroidUtilities.runOnUIThread(new Runnable(newMsgObj12, i4) {
                private final /* synthetic */ TLRPC.Message f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$8$SendMessagesHelper(this.f$1, this.f$2);
                }
            });
        }
        int i5 = scheduleDate;
        ArrayList arrayList3 = newMsgObjArr;
    }

    public /* synthetic */ void lambda$null$6$SendMessagesHelper(TLRPC.Message newMsgObj1, int oldId, TLRPC.Peer to_id, int scheduleDate, ArrayList sentMessages, long peer, TLRPC.Message message, int existFlags) {
        TLRPC.Message message2 = newMsgObj1;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(oldId), message2.id, 0, false, to_id.channel_id, scheduleDate != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, true, false, 0, scheduleDate != 0);
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj1, peer, oldId, message, existFlags, scheduleDate) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC.Message f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$5$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(TLRPC.Message newMsgObj1, long peer, int oldId, TLRPC.Message message, int existFlags, int scheduleDate) {
        boolean z = false;
        newMsgObj1.send_state = 0;
        getMediaDataController().increasePeerRaiting(peer);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[7];
        objArr[0] = Integer.valueOf(oldId);
        objArr[1] = Integer.valueOf(message.id);
        objArr[2] = message;
        objArr[3] = Long.valueOf(peer);
        objArr[4] = 0L;
        objArr[5] = Integer.valueOf(existFlags);
        objArr[6] = Boolean.valueOf(scheduleDate != 0);
        notificationCenter.postNotificationName(i, objArr);
        processSentMessage(oldId);
        if (scheduleDate != 0) {
            z = true;
        }
        removeFromSendingMessages(oldId, z);
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(TLRPC.TL_error error, TLRPC.TL_messages_forwardMessages req) {
        AlertsCreator.processError(this.currentAccount, error, (BaseFragment) null, req, new Object[0]);
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(TLRPC.Message newMsgObj1, int scheduleDate) {
        newMsgObj1.send_state = 2;
        boolean z = true;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj1.id));
        processSentMessage(newMsgObj1.id);
        int i = newMsgObj1.id;
        if (scheduleDate == 0) {
            z = false;
        }
        removeFromSendingMessages(i, z);
    }

    private void writePreviousMessageData(TLRPC.Message message, SerializedData data) {
        message.media.serializeToStream(data);
        String str = "";
        data.writeString(message.message != null ? message.message : str);
        if (message.attachPath != null) {
            str = message.attachPath;
        }
        data.writeString(str);
        int size = message.entities.size();
        int count = size;
        data.writeInt32(size);
        for (int a = 0; a < count; a++) {
            message.entities.get(a).serializeToStream(data);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v49, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v17, resolved type: im.bclpbkiauv.messenger.VideoEditedInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v36, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v43, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v57, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v41, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v42, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v43, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX WARNING: type inference failed for: r22v2 */
    /* JADX WARNING: type inference failed for: r1v20 */
    /* JADX WARNING: type inference failed for: r1v21 */
    /* JADX WARNING: type inference failed for: r22v6 */
    /* JADX WARNING: type inference failed for: r18v6 */
    /* JADX WARNING: type inference failed for: r35v14 */
    /* JADX WARNING: type inference failed for: r18v10 */
    /* JADX WARNING: type inference failed for: r1v69 */
    /* JADX WARNING: type inference failed for: r18v11 */
    /* JADX WARNING: type inference failed for: r22v10 */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0191, code lost:
        if (r13.type == 2) goto L_0x0193;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0153 A[Catch:{ Exception -> 0x0122 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void editMessageMedia(im.bclpbkiauv.messenger.MessageObject r31, im.bclpbkiauv.tgnet.TLRPC.TL_photo r32, im.bclpbkiauv.messenger.VideoEditedInfo r33, im.bclpbkiauv.tgnet.TLRPC.TL_document r34, java.lang.String r35, java.util.HashMap<java.lang.String, java.lang.String> r36, boolean r37, java.lang.Object r38) {
        /*
            r30 = this;
            r12 = r30
            r13 = r31
            r1 = r32
            r2 = r34
            r3 = r35
            r14 = r38
            java.lang.String r0 = "originalPath"
            if (r13 != 0) goto L_0x0011
            return
        L_0x0011:
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r13.messageOwner
            r4 = 0
            r13.cancelEditing = r4
            r5 = -1
            r6 = 0
            long r7 = r31.getDialogId()     // Catch:{ Exception -> 0x068c }
            r10 = r7
            java.lang.String r7 = "http"
            if (r37 == 0) goto L_0x0095
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r13.messageOwner     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media     // Catch:{ Exception -> 0x0089 }
            boolean r8 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto     // Catch:{ Exception -> 0x0089 }
            if (r8 == 0) goto L_0x0036
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r13.messageOwner     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$Photo r8 = r8.photo     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r8     // Catch:{ Exception -> 0x0089 }
            r1 = r8
            r5 = 2
            r8 = r33
            goto L_0x004d
        L_0x0036:
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r13.messageOwner     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ Exception -> 0x0089 }
            im.bclpbkiauv.tgnet.TLRPC$TL_document r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r8     // Catch:{ Exception -> 0x0089 }
            r2 = r8
            boolean r8 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r2)     // Catch:{ Exception -> 0x007d }
            if (r8 != 0) goto L_0x004a
            if (r33 == 0) goto L_0x0048
            goto L_0x004a
        L_0x0048:
            r5 = 7
            goto L_0x004b
        L_0x004a:
            r5 = 3
        L_0x004b:
            im.bclpbkiauv.messenger.VideoEditedInfo r8 = r13.videoEditedInfo     // Catch:{ Exception -> 0x007d }
        L_0x004d:
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r15.params     // Catch:{ Exception -> 0x0071 }
            java.lang.String r9 = r15.message     // Catch:{ Exception -> 0x0065 }
            r13.editingMessage = r9     // Catch:{ Exception -> 0x0065 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r9 = r15.entities     // Catch:{ Exception -> 0x0065 }
            r13.editingMessageEntities = r9     // Catch:{ Exception -> 0x0065 }
            java.lang.String r9 = r15.attachPath     // Catch:{ Exception -> 0x0065 }
            r3 = r9
            r9 = r1
            r21 = r6
            r6 = r2
            r29 = r5
            r5 = r3
            r3 = r29
            goto L_0x0171
        L_0x0065:
            r0 = move-exception
            r9 = r1
            r7 = r2
            r5 = r3
            r25 = r4
            r20 = r8
            r19 = r15
            goto L_0x0696
        L_0x0071:
            r0 = move-exception
            r25 = r36
            r9 = r1
            r7 = r2
            r5 = r3
            r20 = r8
            r19 = r15
            goto L_0x0696
        L_0x007d:
            r0 = move-exception
            r20 = r33
            r25 = r36
            r9 = r1
            r7 = r2
            r5 = r3
            r19 = r15
            goto L_0x0696
        L_0x0089:
            r0 = move-exception
            r20 = r33
            r25 = r36
            r9 = r1
            r7 = r2
            r5 = r3
            r19 = r15
            goto L_0x0696
        L_0x0095:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r15.media     // Catch:{ Exception -> 0x068c }
            r13.previousMedia = r4     // Catch:{ Exception -> 0x068c }
            java.lang.String r4 = r15.message     // Catch:{ Exception -> 0x068c }
            r13.previousCaption = r4     // Catch:{ Exception -> 0x068c }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r4 = r15.entities     // Catch:{ Exception -> 0x068c }
            r13.previousCaptionEntities = r4     // Catch:{ Exception -> 0x068c }
            java.lang.String r4 = r15.attachPath     // Catch:{ Exception -> 0x068c }
            r13.previousAttachPath = r4     // Catch:{ Exception -> 0x068c }
            im.bclpbkiauv.tgnet.SerializedData r4 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ Exception -> 0x068c }
            r8 = 1
            r4.<init>((boolean) r8)     // Catch:{ Exception -> 0x068c }
            r12.writePreviousMessageData(r15, r4)     // Catch:{ Exception -> 0x068c }
            im.bclpbkiauv.tgnet.SerializedData r8 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ Exception -> 0x068c }
            int r9 = r4.length()     // Catch:{ Exception -> 0x068c }
            r8.<init>((int) r9)     // Catch:{ Exception -> 0x068c }
            r12.writePreviousMessageData(r15, r8)     // Catch:{ Exception -> 0x068c }
            if (r36 != 0) goto L_0x00c2
            java.util.HashMap r9 = new java.util.HashMap     // Catch:{ Exception -> 0x0089 }
            r9.<init>()     // Catch:{ Exception -> 0x0089 }
            goto L_0x00c4
        L_0x00c2:
            r9 = r36
        L_0x00c4:
            r19 = r4
            java.lang.String r4 = "prevMedia"
            r20 = r5
            byte[] r5 = r8.toByteArray()     // Catch:{ Exception -> 0x0681 }
            r21 = r6
            r6 = 0
            java.lang.String r5 = android.util.Base64.encodeToString(r5, r6)     // Catch:{ Exception -> 0x0681 }
            r9.put(r4, r5)     // Catch:{ Exception -> 0x0681 }
            r8.cleanup()     // Catch:{ Exception -> 0x0681 }
            if (r1 == 0) goto L_0x012e
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPhoto r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0122 }
            r4.<init>()     // Catch:{ Exception -> 0x0122 }
            r15.media = r4     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r15.media     // Catch:{ Exception -> 0x0122 }
            int r5 = r4.flags     // Catch:{ Exception -> 0x0122 }
            r6 = 3
            r5 = r5 | r6
            r4.flags = r5     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r15.media     // Catch:{ Exception -> 0x0122 }
            r4.photo = r1     // Catch:{ Exception -> 0x0122 }
            r5 = 2
            if (r3 == 0) goto L_0x0102
            int r4 = r35.length()     // Catch:{ Exception -> 0x0122 }
            if (r4 <= 0) goto L_0x0102
            boolean r4 = r3.startsWith(r7)     // Catch:{ Exception -> 0x0122 }
            if (r4 == 0) goto L_0x0102
            r15.attachPath = r3     // Catch:{ Exception -> 0x0122 }
            goto L_0x0162
        L_0x0102:
            java.util.ArrayList r4 = r1.sizes     // Catch:{ Exception -> 0x0122 }
            java.util.ArrayList r6 = r1.sizes     // Catch:{ Exception -> 0x0122 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0122 }
            r18 = 1
            int r6 = r6 + -1
            java.lang.Object r4 = r4.get(r6)     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r4 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r4     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r4 = r4.location     // Catch:{ Exception -> 0x0122 }
            r6 = 1
            java.io.File r20 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r4, r6)     // Catch:{ Exception -> 0x0122 }
            java.lang.String r6 = r20.toString()     // Catch:{ Exception -> 0x0122 }
            r15.attachPath = r6     // Catch:{ Exception -> 0x0122 }
            goto L_0x0162
        L_0x0122:
            r0 = move-exception
            r20 = r33
            r7 = r2
            r5 = r3
            r25 = r9
            r19 = r15
            r9 = r1
            goto L_0x0696
        L_0x012e:
            if (r2 == 0) goto L_0x0160
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0122 }
            r4.<init>()     // Catch:{ Exception -> 0x0122 }
            r15.media = r4     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r15.media     // Catch:{ Exception -> 0x0122 }
            int r5 = r4.flags     // Catch:{ Exception -> 0x0122 }
            r6 = 3
            r5 = r5 | r6
            r4.flags = r5     // Catch:{ Exception -> 0x0122 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r15.media     // Catch:{ Exception -> 0x0122 }
            r4.document = r2     // Catch:{ Exception -> 0x0122 }
            boolean r4 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r34)     // Catch:{ Exception -> 0x0122 }
            if (r4 != 0) goto L_0x014f
            if (r33 == 0) goto L_0x014c
            goto L_0x014f
        L_0x014c:
            r4 = 7
            r5 = r4
            goto L_0x0151
        L_0x014f:
            r4 = 3
            r5 = r4
        L_0x0151:
            if (r33 == 0) goto L_0x015d
            java.lang.String r4 = r33.getString()     // Catch:{ Exception -> 0x0122 }
            java.lang.String r6 = "ve"
            r9.put(r6, r4)     // Catch:{ Exception -> 0x0122 }
        L_0x015d:
            r15.attachPath = r3     // Catch:{ Exception -> 0x0122 }
            goto L_0x0162
        L_0x0160:
            r5 = r20
        L_0x0162:
            r15.params = r9     // Catch:{ Exception -> 0x0681 }
            r4 = 3
            r15.send_state = r4     // Catch:{ Exception -> 0x0681 }
            r8 = r33
            r6 = r2
            r4 = r9
            r9 = r1
            r29 = r5
            r5 = r3
            r3 = r29
        L_0x0171:
            java.lang.String r1 = r15.attachPath     // Catch:{ Exception -> 0x0670 }
            if (r1 != 0) goto L_0x0184
            java.lang.String r1 = ""
            r15.attachPath = r1     // Catch:{ Exception -> 0x017a }
            goto L_0x0184
        L_0x017a:
            r0 = move-exception
            r25 = r4
            r7 = r6
            r20 = r8
            r19 = r15
            goto L_0x0696
        L_0x0184:
            r1 = 0
            r15.local_id = r1     // Catch:{ Exception -> 0x0670 }
            int r1 = r13.type     // Catch:{ Exception -> 0x0670 }
            r2 = 3
            if (r1 == r2) goto L_0x0193
            if (r8 != 0) goto L_0x0193
            int r1 = r13.type     // Catch:{ Exception -> 0x017a }
            r2 = 2
            if (r1 != r2) goto L_0x019e
        L_0x0193:
            java.lang.String r1 = r15.attachPath     // Catch:{ Exception -> 0x0670 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0670 }
            if (r1 != 0) goto L_0x019e
            r1 = 1
            r13.attachPathExists = r1     // Catch:{ Exception -> 0x017a }
        L_0x019e:
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r13.videoEditedInfo     // Catch:{ Exception -> 0x0670 }
            if (r1 == 0) goto L_0x01a7
            if (r8 != 0) goto L_0x01a7
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r13.videoEditedInfo     // Catch:{ Exception -> 0x017a }
            r8 = r1
        L_0x01a7:
            if (r37 != 0) goto L_0x0249
            java.lang.CharSequence r2 = r13.editingMessage     // Catch:{ Exception -> 0x023a }
            if (r2 == 0) goto L_0x01df
            java.lang.CharSequence r2 = r13.editingMessage     // Catch:{ Exception -> 0x017a }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x017a }
            r15.message = r2     // Catch:{ Exception -> 0x017a }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r2 = r13.editingMessageEntities     // Catch:{ Exception -> 0x017a }
            if (r2 == 0) goto L_0x01be
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r2 = r13.editingMessageEntities     // Catch:{ Exception -> 0x017a }
            r15.entities = r2     // Catch:{ Exception -> 0x017a }
            goto L_0x01d9
        L_0x01be:
            r2 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r2]     // Catch:{ Exception -> 0x017a }
            java.lang.CharSequence r2 = r13.editingMessage     // Catch:{ Exception -> 0x017a }
            r17 = 0
            r1[r17] = r2     // Catch:{ Exception -> 0x017a }
            im.bclpbkiauv.messenger.MediaDataController r2 = r30.getMediaDataController()     // Catch:{ Exception -> 0x017a }
            java.util.ArrayList r2 = r2.getEntities(r1)     // Catch:{ Exception -> 0x017a }
            if (r2 == 0) goto L_0x01d9
            boolean r19 = r2.isEmpty()     // Catch:{ Exception -> 0x017a }
            if (r19 != 0) goto L_0x01d9
            r15.entities = r2     // Catch:{ Exception -> 0x017a }
        L_0x01d9:
            r1 = 0
            r13.caption = r1     // Catch:{ Exception -> 0x017a }
            r31.generateCaption()     // Catch:{ Exception -> 0x017a }
        L_0x01df:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x023a }
            r1.<init>()     // Catch:{ Exception -> 0x023a }
            r1.add(r15)     // Catch:{ Exception -> 0x023a }
            im.bclpbkiauv.messenger.MessagesStorage r22 = r30.getMessagesStorage()     // Catch:{ Exception -> 0x023a }
            r24 = 0
            r25 = 1
            r26 = 0
            r27 = 0
            boolean r2 = r13.scheduled     // Catch:{ Exception -> 0x023a }
            r23 = r1
            r28 = r2
            r22.putMessages((java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message>) r23, (boolean) r24, (boolean) r25, (boolean) r26, (int) r27, (boolean) r28)     // Catch:{ Exception -> 0x023a }
            r2 = -1
            r13.type = r2     // Catch:{ Exception -> 0x023a }
            r31.setType()     // Catch:{ Exception -> 0x023a }
            r31.createMessageSendInfo()     // Catch:{ Exception -> 0x023a }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x023a }
            r2.<init>()     // Catch:{ Exception -> 0x023a }
            r2.add(r13)     // Catch:{ Exception -> 0x023a }
            r34 = r1
            im.bclpbkiauv.messenger.NotificationCenter r1 = r30.getNotificationCenter()     // Catch:{ Exception -> 0x023a }
            r19 = r15
            int r15 = im.bclpbkiauv.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x022d }
            r20 = r6
            r35 = r8
            r8 = 2
            java.lang.Object[] r6 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0260 }
            java.lang.Long r8 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x0260 }
            r17 = 0
            r6[r17] = r8     // Catch:{ Exception -> 0x0260 }
            r8 = 1
            r6[r8] = r2     // Catch:{ Exception -> 0x0260 }
            r1.postNotificationName(r15, r6)     // Catch:{ Exception -> 0x0260 }
            goto L_0x024f
        L_0x022d:
            r0 = move-exception
            r20 = r6
            r35 = r8
            r25 = r4
            r7 = r20
            r20 = r35
            goto L_0x0696
        L_0x023a:
            r0 = move-exception
            r20 = r6
            r35 = r8
            r19 = r15
            r25 = r4
            r7 = r20
            r20 = r35
            goto L_0x0696
        L_0x0249:
            r20 = r6
            r35 = r8
            r19 = r15
        L_0x024f:
            r1 = 0
            if (r4 == 0) goto L_0x0269
            boolean r2 = r4.containsKey(r0)     // Catch:{ Exception -> 0x0260 }
            if (r2 == 0) goto L_0x0269
            java.lang.Object r0 = r4.get(r0)     // Catch:{ Exception -> 0x0260 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0260 }
            r1 = r0
            goto L_0x026a
        L_0x0260:
            r0 = move-exception
            r25 = r4
            r7 = r20
            r20 = r35
            goto L_0x0696
        L_0x0269:
            r0 = r1
        L_0x026a:
            r1 = 0
            r6 = 8
            r2 = 1
            if (r3 < r2) goto L_0x0273
            r2 = 3
            if (r3 <= r2) goto L_0x0278
        L_0x0273:
            r2 = 5
            if (r3 < r2) goto L_0x0658
            if (r3 > r6) goto L_0x0658
        L_0x0278:
            r2 = 0
            r22 = 0
            r15 = 2
            if (r3 != r15) goto L_0x0378
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto r15 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x036f }
            r15.<init>()     // Catch:{ Exception -> 0x036f }
            if (r4 == 0) goto L_0x02e0
            java.lang.String r6 = "masks"
            java.lang.Object r6 = r4.get(r6)     // Catch:{ Exception -> 0x036f }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x036f }
            if (r6 == 0) goto L_0x02d7
            im.bclpbkiauv.tgnet.SerializedData r8 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ Exception -> 0x036f }
            r24 = r1
            byte[] r1 = im.bclpbkiauv.messenger.Utilities.hexToBytes(r6)     // Catch:{ Exception -> 0x036f }
            r8.<init>((byte[]) r1)     // Catch:{ Exception -> 0x036f }
            r1 = r8
            r8 = 0
            int r25 = r1.readInt32(r8)     // Catch:{ Exception -> 0x036f }
            r8 = r25
            r25 = 0
            r26 = r2
            r2 = r25
        L_0x02a8:
            if (r2 >= r8) goto L_0x02c7
            r25 = r4
            java.util.ArrayList r4 = r15.stickers     // Catch:{ Exception -> 0x0368 }
            r27 = r6
            r28 = r8
            r6 = 0
            int r8 = r1.readInt32(r6)     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r8 = im.bclpbkiauv.tgnet.TLRPC.InputDocument.TLdeserialize(r1, r8, r6)     // Catch:{ Exception -> 0x0368 }
            r4.add(r8)     // Catch:{ Exception -> 0x0368 }
            int r2 = r2 + 1
            r4 = r25
            r6 = r27
            r8 = r28
            goto L_0x02a8
        L_0x02c7:
            r25 = r4
            r27 = r6
            r28 = r8
            int r2 = r15.flags     // Catch:{ Exception -> 0x0368 }
            r4 = 1
            r2 = r2 | r4
            r15.flags = r2     // Catch:{ Exception -> 0x0368 }
            r1.cleanup()     // Catch:{ Exception -> 0x0368 }
            goto L_0x02e6
        L_0x02d7:
            r24 = r1
            r26 = r2
            r25 = r4
            r27 = r6
            goto L_0x02e6
        L_0x02e0:
            r24 = r1
            r26 = r2
            r25 = r4
        L_0x02e6:
            long r1 = r9.access_hash     // Catch:{ Exception -> 0x0368 }
            int r4 = (r1 > r22 ? 1 : (r1 == r22 ? 0 : -1))
            if (r4 != 0) goto L_0x02f5
            r1 = r15
            r2 = 1
            r6 = r3
            r29 = r2
            r2 = r1
            r1 = r29
            goto L_0x0324
        L_0x02f5:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0368 }
            r1.<init>()     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0368 }
            r2.<init>()     // Catch:{ Exception -> 0x0368 }
            r1.id = r2     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0368 }
            r6 = r3
            long r3 = r9.id     // Catch:{ Exception -> 0x0368 }
            r2.id = r3     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0368 }
            long r3 = r9.access_hash     // Catch:{ Exception -> 0x0368 }
            r2.access_hash = r3     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0368 }
            byte[] r3 = r9.file_reference     // Catch:{ Exception -> 0x0368 }
            r2.file_reference = r3     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0368 }
            byte[] r2 = r2.file_reference     // Catch:{ Exception -> 0x0368 }
            if (r2 != 0) goto L_0x0321
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0368 }
            r3 = 0
            byte[] r4 = new byte[r3]     // Catch:{ Exception -> 0x0368 }
            r2.file_reference = r4     // Catch:{ Exception -> 0x0368 }
        L_0x0321:
            r2 = r1
            r1 = r24
        L_0x0324:
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r3 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0368 }
            r3.<init>(r10)     // Catch:{ Exception -> 0x0368 }
            r4 = 0
            r3.type = r4     // Catch:{ Exception -> 0x0368 }
            r3.obj = r13     // Catch:{ Exception -> 0x0368 }
            r3.originalPath = r0     // Catch:{ Exception -> 0x0368 }
            r3.parentObject = r14     // Catch:{ Exception -> 0x0368 }
            r3.inputUploadMedia = r15     // Catch:{ Exception -> 0x0368 }
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x0368 }
            if (r5 == 0) goto L_0x0347
            int r4 = r5.length()     // Catch:{ Exception -> 0x0368 }
            if (r4 <= 0) goto L_0x0347
            boolean r4 = r5.startsWith(r7)     // Catch:{ Exception -> 0x0368 }
            if (r4 == 0) goto L_0x0347
            r3.httpLocation = r5     // Catch:{ Exception -> 0x0368 }
            goto L_0x035b
        L_0x0347:
            java.util.ArrayList r4 = r9.sizes     // Catch:{ Exception -> 0x0368 }
            java.util.ArrayList r7 = r9.sizes     // Catch:{ Exception -> 0x0368 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0368 }
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0368 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r4 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r4     // Catch:{ Exception -> 0x0368 }
            r3.photoSize = r4     // Catch:{ Exception -> 0x0368 }
            r3.locationParent = r9     // Catch:{ Exception -> 0x0368 }
        L_0x035b:
            r24 = r1
            r8 = r3
            r15 = r9
            r7 = r20
            r20 = r35
            r9 = r2
            r35 = r5
            goto L_0x04e3
        L_0x0368:
            r0 = move-exception
            r7 = r20
            r20 = r35
            goto L_0x0696
        L_0x036f:
            r0 = move-exception
            r25 = r4
            r7 = r20
            r20 = r35
            goto L_0x0696
        L_0x0378:
            r24 = r1
            r26 = r2
            r6 = r3
            r25 = r4
            r1 = 3
            if (r6 != r1) goto L_0x043e
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0433 }
            r1.<init>()     // Catch:{ Exception -> 0x0433 }
            r7 = r20
            java.lang.String r2 = r7.mime_type     // Catch:{ Exception -> 0x042a }
            r1.mime_type = r2     // Catch:{ Exception -> 0x042a }
            java.util.ArrayList r2 = r7.attributes     // Catch:{ Exception -> 0x042a }
            r1.attributes = r2     // Catch:{ Exception -> 0x042a }
            boolean r2 = r31.isGif()     // Catch:{ Exception -> 0x042a }
            if (r2 != 0) goto L_0x03b4
            if (r35 == 0) goto L_0x03a0
            r8 = r35
            boolean r2 = r8.muted     // Catch:{ Exception -> 0x03af }
            if (r2 != 0) goto L_0x03b6
            goto L_0x03a2
        L_0x03a0:
            r8 = r35
        L_0x03a2:
            r2 = 1
            r1.nosound_video = r2     // Catch:{ Exception -> 0x03af }
            boolean r2 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x03af }
            if (r2 == 0) goto L_0x03b6
            java.lang.String r2 = "nosound_video = true"
            im.bclpbkiauv.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x03af }
            goto L_0x03b6
        L_0x03af:
            r0 = move-exception
            r20 = r8
            goto L_0x0696
        L_0x03b4:
            r8 = r35
        L_0x03b6:
            long r2 = r7.access_hash     // Catch:{ Exception -> 0x0423 }
            int r4 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1))
            if (r4 != 0) goto L_0x03c1
            r2 = r1
            r3 = 1
            r35 = r5
            goto L_0x03f1
        L_0x03c1:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0423 }
            r2.<init>()     // Catch:{ Exception -> 0x0423 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0423 }
            r3.<init>()     // Catch:{ Exception -> 0x0423 }
            r2.id = r3     // Catch:{ Exception -> 0x0423 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0423 }
            r35 = r5
            long r4 = r7.id     // Catch:{ Exception -> 0x0462 }
            r3.id = r4     // Catch:{ Exception -> 0x0462 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0462 }
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x0462 }
            r3.access_hash = r4     // Catch:{ Exception -> 0x0462 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0462 }
            byte[] r4 = r7.file_reference     // Catch:{ Exception -> 0x0462 }
            r3.file_reference = r4     // Catch:{ Exception -> 0x0462 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0462 }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x0462 }
            if (r3 != 0) goto L_0x03ee
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0462 }
            r4 = 0
            byte[] r5 = new byte[r4]     // Catch:{ Exception -> 0x0462 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0462 }
        L_0x03ee:
            r3 = r2
            r3 = r24
        L_0x03f1:
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r4 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0462 }
            r4.<init>(r10)     // Catch:{ Exception -> 0x0462 }
            r5 = 1
            r4.type = r5     // Catch:{ Exception -> 0x0462 }
            r4.obj = r13     // Catch:{ Exception -> 0x0462 }
            r4.originalPath = r0     // Catch:{ Exception -> 0x0462 }
            r4.parentObject = r14     // Catch:{ Exception -> 0x0462 }
            r4.inputUploadMedia = r1     // Catch:{ Exception -> 0x0462 }
            r4.performMediaUpload = r3     // Catch:{ Exception -> 0x0462 }
            java.util.ArrayList r5 = r7.thumbs     // Catch:{ Exception -> 0x0462 }
            boolean r5 = r5.isEmpty()     // Catch:{ Exception -> 0x0462 }
            if (r5 != 0) goto L_0x0418
            java.util.ArrayList r5 = r7.thumbs     // Catch:{ Exception -> 0x0462 }
            r15 = 0
            java.lang.Object r5 = r5.get(r15)     // Catch:{ Exception -> 0x0462 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r5 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r5     // Catch:{ Exception -> 0x0462 }
            r4.photoSize = r5     // Catch:{ Exception -> 0x0462 }
            r4.locationParent = r7     // Catch:{ Exception -> 0x0462 }
        L_0x0418:
            r4.videoEditedInfo = r8     // Catch:{ Exception -> 0x0462 }
            r24 = r3
            r20 = r8
            r15 = r9
            r9 = r2
            r8 = r4
            goto L_0x04e3
        L_0x0423:
            r0 = move-exception
            r35 = r5
            r20 = r8
            goto L_0x0696
        L_0x042a:
            r0 = move-exception
            r8 = r35
            r35 = r5
            r20 = r8
            goto L_0x0696
        L_0x0433:
            r0 = move-exception
            r8 = r35
            r35 = r5
            r7 = r20
            r20 = r8
            goto L_0x0696
        L_0x043e:
            r8 = r35
            r35 = r5
            r7 = r20
            r1 = 7
            if (r6 != r1) goto L_0x04dc
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x04d4 }
            r2.<init>()     // Catch:{ Exception -> 0x04d4 }
            java.lang.String r3 = r7.mime_type     // Catch:{ Exception -> 0x04d4 }
            r2.mime_type = r3     // Catch:{ Exception -> 0x04d4 }
            java.util.ArrayList r3 = r7.attributes     // Catch:{ Exception -> 0x04d4 }
            r2.attributes = r3     // Catch:{ Exception -> 0x04d4 }
            long r3 = r7.access_hash     // Catch:{ Exception -> 0x04d4 }
            int r5 = (r3 > r22 ? 1 : (r3 == r22 ? 0 : -1))
            if (r5 != 0) goto L_0x0469
            r3 = r2
            boolean r4 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0462 }
            r20 = r8
            r15 = r9
            goto L_0x049a
        L_0x0462:
            r0 = move-exception
            r5 = r35
            r20 = r8
            goto L_0x0696
        L_0x0469:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x04d4 }
            r3.<init>()     // Catch:{ Exception -> 0x04d4 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x04d4 }
            r4.<init>()     // Catch:{ Exception -> 0x04d4 }
            r3.id = r4     // Catch:{ Exception -> 0x04d4 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x04d4 }
            r20 = r8
            r15 = r9
            long r8 = r7.id     // Catch:{ Exception -> 0x04c8 }
            r4.id = r8     // Catch:{ Exception -> 0x04c8 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x04c8 }
            long r8 = r7.access_hash     // Catch:{ Exception -> 0x04c8 }
            r4.access_hash = r8     // Catch:{ Exception -> 0x04c8 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x04c8 }
            byte[] r5 = r7.file_reference     // Catch:{ Exception -> 0x04c8 }
            r4.file_reference = r5     // Catch:{ Exception -> 0x04c8 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x04c8 }
            byte[] r4 = r4.file_reference     // Catch:{ Exception -> 0x04c8 }
            if (r4 != 0) goto L_0x0497
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x04c8 }
            r5 = 0
            byte[] r8 = new byte[r5]     // Catch:{ Exception -> 0x04c8 }
            r4.file_reference = r8     // Catch:{ Exception -> 0x04c8 }
        L_0x0497:
            r4 = r3
            r4 = r24
        L_0x049a:
            if (r1 != 0) goto L_0x04ce
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r5 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x04c8 }
            r5.<init>(r10)     // Catch:{ Exception -> 0x04c8 }
            r5.originalPath = r0     // Catch:{ Exception -> 0x04c8 }
            r8 = 2
            r5.type = r8     // Catch:{ Exception -> 0x04c8 }
            r5.obj = r13     // Catch:{ Exception -> 0x04c8 }
            java.util.ArrayList r9 = r7.thumbs     // Catch:{ Exception -> 0x04c8 }
            boolean r9 = r9.isEmpty()     // Catch:{ Exception -> 0x04c8 }
            if (r9 != 0) goto L_0x04bd
            java.util.ArrayList r9 = r7.thumbs     // Catch:{ Exception -> 0x04c8 }
            r8 = 0
            java.lang.Object r9 = r9.get(r8)     // Catch:{ Exception -> 0x04c8 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r9     // Catch:{ Exception -> 0x04c8 }
            r5.photoSize = r9     // Catch:{ Exception -> 0x04c8 }
            r5.locationParent = r7     // Catch:{ Exception -> 0x04c8 }
        L_0x04bd:
            r5.parentObject = r14     // Catch:{ Exception -> 0x04c8 }
            r5.inputUploadMedia = r2     // Catch:{ Exception -> 0x04c8 }
            r5.performMediaUpload = r4     // Catch:{ Exception -> 0x04c8 }
            r9 = r3
            r24 = r4
            r8 = r5
            goto L_0x04e3
        L_0x04c8:
            r0 = move-exception
            r5 = r35
            r9 = r15
            goto L_0x0696
        L_0x04ce:
            r9 = r3
            r24 = r4
            r8 = r21
            goto L_0x04e3
        L_0x04d4:
            r0 = move-exception
            r20 = r8
            r15 = r9
            r5 = r35
            goto L_0x0696
        L_0x04dc:
            r20 = r8
            r15 = r9
            r8 = r21
            r9 = r26
        L_0x04e3:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_editMessage r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x064c }
            r1.<init>()     // Catch:{ Exception -> 0x064c }
            r5 = r1
            int r1 = r31.getId()     // Catch:{ Exception -> 0x064c }
            r5.id = r1     // Catch:{ Exception -> 0x064c }
            im.bclpbkiauv.messenger.MessagesController r1 = r30.getMessagesController()     // Catch:{ Exception -> 0x064c }
            int r2 = (int) r10     // Catch:{ Exception -> 0x064c }
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r2)     // Catch:{ Exception -> 0x064c }
            r5.peer = r1     // Catch:{ Exception -> 0x064c }
            int r1 = r5.flags     // Catch:{ Exception -> 0x064c }
            r1 = r1 | 16384(0x4000, float:2.2959E-41)
            r5.flags = r1     // Catch:{ Exception -> 0x064c }
            r5.media = r9     // Catch:{ Exception -> 0x064c }
            boolean r1 = r13.scheduled     // Catch:{ Exception -> 0x064c }
            if (r1 == 0) goto L_0x0514
            im.bclpbkiauv.tgnet.TLRPC$Message r1 = r13.messageOwner     // Catch:{ Exception -> 0x04c8 }
            int r1 = r1.date     // Catch:{ Exception -> 0x04c8 }
            r5.schedule_date = r1     // Catch:{ Exception -> 0x04c8 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x04c8 }
            r2 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r2
            r5.flags = r1     // Catch:{ Exception -> 0x04c8 }
        L_0x0514:
            java.lang.CharSequence r1 = r13.editingMessage     // Catch:{ Exception -> 0x064c }
            if (r1 == 0) goto L_0x055d
            java.lang.CharSequence r1 = r13.editingMessage     // Catch:{ Exception -> 0x04c8 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04c8 }
            r5.message = r1     // Catch:{ Exception -> 0x04c8 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x04c8 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r5.flags = r1     // Catch:{ Exception -> 0x04c8 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r1 = r13.editingMessageEntities     // Catch:{ Exception -> 0x04c8 }
            if (r1 == 0) goto L_0x0536
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r1 = r13.editingMessageEntities     // Catch:{ Exception -> 0x04c8 }
            r5.entities = r1     // Catch:{ Exception -> 0x04c8 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x04c8 }
            r2 = 8
            r1 = r1 | r2
            r5.flags = r1     // Catch:{ Exception -> 0x04c8 }
            goto L_0x0558
        L_0x0536:
            r1 = 1
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x04c8 }
            java.lang.CharSequence r1 = r13.editingMessage     // Catch:{ Exception -> 0x04c8 }
            r3 = 0
            r2[r3] = r1     // Catch:{ Exception -> 0x04c8 }
            r1 = r2
            im.bclpbkiauv.messenger.MediaDataController r2 = r30.getMediaDataController()     // Catch:{ Exception -> 0x04c8 }
            java.util.ArrayList r2 = r2.getEntities(r1)     // Catch:{ Exception -> 0x04c8 }
            if (r2 == 0) goto L_0x0558
            boolean r3 = r2.isEmpty()     // Catch:{ Exception -> 0x04c8 }
            if (r3 != 0) goto L_0x0558
            r5.entities = r2     // Catch:{ Exception -> 0x04c8 }
            int r3 = r5.flags     // Catch:{ Exception -> 0x04c8 }
            r4 = 8
            r3 = r3 | r4
            r5.flags = r3     // Catch:{ Exception -> 0x04c8 }
        L_0x0558:
            r1 = 0
            r13.editingMessage = r1     // Catch:{ Exception -> 0x04c8 }
            r13.editingMessageEntities = r1     // Catch:{ Exception -> 0x04c8 }
        L_0x055d:
            if (r8 == 0) goto L_0x0561
            r8.sendRequest = r5     // Catch:{ Exception -> 0x04c8 }
        L_0x0561:
            r1 = 2
            r2 = r5
            r3 = 1
            if (r6 != r3) goto L_0x059a
            r4 = 0
            boolean r3 = r13.scheduled     // Catch:{ Exception -> 0x058e }
            r1 = r30
            r16 = r3
            r3 = r31
            r17 = r25
            r18 = r35
            r21 = r5
            r5 = r8
            r22 = r7
            r7 = r6
            r6 = r38
            r14 = r7
            r7 = r16
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0584 }
            r1 = r15
            goto L_0x0666
        L_0x0584:
            r0 = move-exception
            r9 = r15
            r25 = r17
            r5 = r18
            r7 = r22
            goto L_0x0696
        L_0x058e:
            r0 = move-exception
            r18 = r35
            r22 = r7
            r17 = r25
            r9 = r15
            r5 = r18
            goto L_0x0696
        L_0x059a:
            r18 = r35
            r21 = r5
            r14 = r6
            r22 = r7
            r17 = r25
            if (r14 != r1) goto L_0x05d7
            if (r24 == 0) goto L_0x05ad
            r12.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x0584 }
            r1 = r15
            goto L_0x0666
        L_0x05ad:
            r7 = 0
            r1 = 1
            boolean r6 = r13.scheduled     // Catch:{ Exception -> 0x05cc }
            r3 = r30
            r4 = r2
            r5 = r31
            r16 = r6
            r6 = r0
            r32 = r8
            r8 = r1
            r26 = r9
            r1 = r15
            r9 = r32
            r27 = r10
            r10 = r38
            r11 = r16
            r3.performSendMessageRequest(r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x05cc:
            r0 = move-exception
            r1 = r15
            r9 = r1
            r25 = r17
            r5 = r18
            r7 = r22
            goto L_0x0696
        L_0x05d7:
            r32 = r8
            r26 = r9
            r27 = r10
            r1 = r15
            r3 = 3
            if (r14 != r3) goto L_0x05fc
            if (r24 == 0) goto L_0x05ea
            r10 = r32
            r12.performSendDelayedMessage(r10)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x05ea:
            r10 = r32
            boolean r9 = r13.scheduled     // Catch:{ Exception -> 0x0611 }
            r3 = r30
            r4 = r2
            r5 = r31
            r6 = r0
            r7 = r10
            r8 = r38
            r3.performSendMessageRequest(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x05fc:
            r10 = r32
            r3 = 6
            if (r14 != r3) goto L_0x061b
            boolean r9 = r13.scheduled     // Catch:{ Exception -> 0x0611 }
            r3 = r30
            r4 = r2
            r5 = r31
            r6 = r0
            r7 = r10
            r8 = r38
            r3.performSendMessageRequest(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x0611:
            r0 = move-exception
            r9 = r1
            r25 = r17
            r5 = r18
            r7 = r22
            goto L_0x0696
        L_0x061b:
            r3 = 7
            if (r14 != r3) goto L_0x0633
            if (r24 == 0) goto L_0x0624
            r12.performSendDelayedMessage(r10)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x0624:
            boolean r9 = r13.scheduled     // Catch:{ Exception -> 0x0611 }
            r3 = r30
            r4 = r2
            r5 = r31
            r6 = r0
            r7 = r10
            r8 = r38
            r3.performSendMessageRequest(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x0633:
            r3 = 8
            if (r14 != r3) goto L_0x0666
            if (r24 == 0) goto L_0x063d
            r12.performSendDelayedMessage(r10)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x063d:
            boolean r9 = r13.scheduled     // Catch:{ Exception -> 0x0611 }
            r3 = r30
            r4 = r2
            r5 = r31
            r6 = r0
            r7 = r10
            r8 = r38
            r3.performSendMessageRequest(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0611 }
            goto L_0x0666
        L_0x064c:
            r0 = move-exception
            r18 = r35
            r22 = r7
            r1 = r15
            r17 = r25
            r9 = r1
            r5 = r18
            goto L_0x0696
        L_0x0658:
            r24 = r1
            r14 = r3
            r17 = r4
            r18 = r5
            r1 = r9
            r27 = r10
            r22 = r20
            r20 = r35
        L_0x0666:
            r9 = r1
            r4 = r17
            r5 = r18
            r8 = r20
            r6 = r22
            goto L_0x06a1
        L_0x0670:
            r0 = move-exception
            r17 = r4
            r18 = r5
            r22 = r6
            r1 = r9
            r19 = r15
            r20 = r8
            r25 = r17
            r7 = r22
            goto L_0x0696
        L_0x0681:
            r0 = move-exception
            r19 = r15
            r20 = r33
            r7 = r2
            r5 = r3
            r25 = r9
            r9 = r1
            goto L_0x0696
        L_0x068c:
            r0 = move-exception
            r19 = r15
            r20 = r33
            r25 = r36
            r9 = r1
            r7 = r2
            r5 = r3
        L_0x0696:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r30.revertEditingMessageObject(r31)
            r6 = r7
            r8 = r20
            r4 = r25
        L_0x06a1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.editMessageMedia(im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.tgnet.TLRPC$TL_photo, im.bclpbkiauv.messenger.VideoEditedInfo, im.bclpbkiauv.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, BaseFragment fragment, ArrayList<TLRPC.MessageEntity> entities, int scheduleDate, Runnable callback) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return 0;
        }
        TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
        req.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (message != null) {
            req.message = message;
            req.flags |= 2048;
            req.no_webpage = !searchLinks;
        }
        req.id = messageObject.getId();
        if (entities != null) {
            req.entities = entities;
            req.flags |= 8;
        }
        if (scheduleDate != 0) {
            req.schedule_date = scheduleDate;
            req.flags |= 32768;
        }
        return getConnectionsManager().sendRequest(req, new RequestDelegate(fragment, req, callback) {
            private final /* synthetic */ BaseFragment f$1;
            private final /* synthetic */ TLRPC.TL_messages_editMessage f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$editMessage$11$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$editMessage$11$SendMessagesHelper(BaseFragment fragment, TLRPC.TL_messages_editMessage req, Runnable callback, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(error, fragment, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_editMessage f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$10$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                }
            });
        }
        if (callback != null) {
            AndroidUtilities.runOnUIThread(callback);
        }
    }

    public /* synthetic */ void lambda$null$10$SendMessagesHelper(TLRPC.TL_error error, BaseFragment fragment, TLRPC.TL_messages_editMessage req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
    }

    /* access modifiers changed from: private */
    public void sendLocation(Location location) {
        TLRPC.TL_messageMediaGeo mediaGeo = new TLRPC.TL_messageMediaGeo();
        mediaGeo.geo = new TLRPC.TL_geoPoint();
        mediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        mediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Map.Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = entry.getValue();
            sendMessage((TLRPC.MessageMedia) mediaGeo, messageObject.getDialogId(), messageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton button) {
        if (messageObject != null && button != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(messageObject.getDialogId());
            sb.append("_");
            sb.append(messageObject.getId());
            sb.append("_");
            sb.append(Utilities.bytesToHex(button.data));
            sb.append("_");
            sb.append(button instanceof TLRPC.TL_keyboardButtonGame ? "1" : "0");
            this.waitingForLocation.put(sb.toString(), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(button.data));
        sb.append("_");
        sb.append(button instanceof TLRPC.TL_keyboardButtonGame ? "1" : "0");
        return this.waitingForLocation.containsKey(sb.toString());
    }

    public void sendNotificationCallback(long dialogId, int msgId, byte[] data) {
        AndroidUtilities.runOnUIThread(new Runnable(dialogId, msgId, data) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ byte[] f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$sendNotificationCallback$14$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$sendNotificationCallback$14$SendMessagesHelper(long dialogId, int msgId, byte[] data) {
        TLRPC.Chat chat;
        TLRPC.User user;
        long j = dialogId;
        int i = msgId;
        byte[] bArr = data;
        int lowerId = (int) j;
        String key = j + "_" + i + "_" + Utilities.bytesToHex(data) + "_" + 0;
        this.waitingForCallback.put(key, true);
        if (lowerId > 0) {
            if (getMessagesController().getUser(Integer.valueOf(lowerId)) == null && (user = getMessagesStorage().getUserSync(lowerId)) != null) {
                getMessagesController().putUser(user, true);
            }
        } else if (getMessagesController().getChat(Integer.valueOf(-lowerId)) == null && (chat = getMessagesStorage().getChatSync(-lowerId)) != null) {
            getMessagesController().putChat(chat, true);
        }
        TLRPC.TL_messages_getBotCallbackAnswer req = new TLRPC.TL_messages_getBotCallbackAnswer();
        req.peer = getMessagesController().getInputPeer(lowerId);
        req.msg_id = i;
        req.game = false;
        if (bArr != null) {
            req.flags |= 1;
            req.data = bArr;
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate(key) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$null$13$SendMessagesHelper(this.f$1, tLObject, tL_error);
            }
        }, 2);
        TLRPC.TL_messages_getBotCallbackAnswer tL_messages_getBotCallbackAnswer = req;
        String str = key;
        getMessagesController().markDialogAsRead(dialogId, msgId, msgId, 0, false, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$12$SendMessagesHelper(String key) {
        Boolean remove = this.waitingForCallback.remove(key);
    }

    public /* synthetic */ void lambda$null$13$SendMessagesHelper(String key, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(key) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$12$SendMessagesHelper(this.f$1);
            }
        });
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return this.waitingForVote.get("poll_" + messageObject.getPollId());
    }

    public int sendVote(MessageObject messageObject, TLRPC.TL_pollAnswer answer, Runnable finishRunnable) {
        if (messageObject == null) {
            return 0;
        }
        String key = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(key)) {
            return 0;
        }
        this.waitingForVote.put(key, answer != null ? answer.option : new byte[0]);
        TLRPC.TL_messages_sendVote req = new TLRPC.TL_messages_sendVote();
        req.msg_id = messageObject.getId();
        req.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (answer != null) {
            req.options.add(answer.option);
        }
        return getConnectionsManager().sendRequest(req, new RequestDelegate(messageObject, key, finishRunnable) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$sendVote$15$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendVote$15$SendMessagesHelper(MessageObject messageObject, final String key, final Runnable finishRunnable, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.uptimeMillis()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.this.waitingForVote.remove(key);
                Runnable runnable = finishRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public long getVoteSendTime(long pollId) {
        return this.voteSendTime.get(pollId, 0L).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence reaction, ChatActivity parentFragment) {
        if (messageObject != null && parentFragment != null) {
            TLRPC.TL_messages_sendReaction req = new TLRPC.TL_messages_sendReaction();
            req.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
            req.msg_id = messageObject.getId();
            if (reaction != null) {
                req.reaction = reaction.toString();
                req.flags |= 1;
            }
            getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendReaction$16$SendMessagesHelper(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendReaction$16$SendMessagesHelper(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    public void sendCallback(boolean cache, MessageObject messageObject, TLRPC.KeyboardButton button, ChatActivity parentFragment) {
        int type;
        boolean cacheFinal;
        MessageObject messageObject2 = messageObject;
        TLRPC.KeyboardButton keyboardButton = button;
        if (messageObject2 == null || keyboardButton == null) {
        } else if (parentFragment == null) {
        } else {
            if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) {
                cacheFinal = false;
                type = 3;
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) {
                cacheFinal = false;
                type = 1;
            } else {
                boolean cacheFinal2 = cache;
                if (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) {
                    cacheFinal = cacheFinal2;
                    type = 2;
                } else {
                    cacheFinal = cacheFinal2;
                    type = 0;
                }
            }
            String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data) + "_" + type;
            this.waitingForCallback.put(key, true);
            TLObject[] request = new TLObject[1];
            RequestDelegate requestDelegate = r0;
            $$Lambda$SendMessagesHelper$kOlniwWb4OxwDEaVYiFynjf2k r0 = new RequestDelegate(key, cacheFinal, messageObject, button, parentFragment, request) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ MessageObject f$3;
                private final /* synthetic */ TLRPC.KeyboardButton f$4;
                private final /* synthetic */ ChatActivity f$5;
                private final /* synthetic */ TLObject[] f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendCallback$18$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
                }
            };
            if (cacheFinal) {
                getMessagesStorage().getBotCache(key, requestDelegate);
            } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) {
                TLRPC.TL_messages_requestUrlAuth req = new TLRPC.TL_messages_requestUrlAuth();
                req.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                req.msg_id = messageObject.getId();
                req.button_id = keyboardButton.button_id;
                request[0] = req;
                getConnectionsManager().sendRequest(req, requestDelegate, 2);
            } else if (!(keyboardButton instanceof TLRPC.TL_keyboardButtonBuy)) {
                TLRPC.TL_messages_getBotCallbackAnswer req2 = new TLRPC.TL_messages_getBotCallbackAnswer();
                req2.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                req2.msg_id = messageObject.getId();
                req2.game = keyboardButton instanceof TLRPC.TL_keyboardButtonGame;
                if (keyboardButton.data != null) {
                    req2.flags |= 1;
                    req2.data = keyboardButton.data;
                }
                getConnectionsManager().sendRequest(req2, requestDelegate, 2);
            } else if ((messageObject2.messageOwner.media.flags & 4) == 0) {
                TLRPC.TL_payments_getPaymentForm req3 = new TLRPC.TL_payments_getPaymentForm();
                req3.msg_id = messageObject.getId();
                getConnectionsManager().sendRequest(req3, requestDelegate, 2);
            } else {
                TLRPC.TL_payments_getPaymentReceipt req4 = new TLRPC.TL_payments_getPaymentReceipt();
                req4.msg_id = messageObject2.messageOwner.media.receipt_msg_id;
                getConnectionsManager().sendRequest(req4, requestDelegate, 2);
            }
        }
    }

    public /* synthetic */ void lambda$sendCallback$18$SendMessagesHelper(String key, boolean cacheFinal, MessageObject messageObject, TLRPC.KeyboardButton button, ChatActivity parentFragment, TLObject[] request, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(key, cacheFinal, response, messageObject, button, parentFragment, request) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ MessageObject f$4;
            private final /* synthetic */ TLRPC.KeyboardButton f$5;
            private final /* synthetic */ ChatActivity f$6;
            private final /* synthetic */ TLObject[] f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$17$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String key, boolean cacheFinal, TLObject response, MessageObject messageObject, TLRPC.KeyboardButton button, ChatActivity parentFragment, TLObject[] request) {
        int uid;
        boolean z;
        String str = key;
        TLObject tLObject = response;
        MessageObject messageObject2 = messageObject;
        TLRPC.KeyboardButton keyboardButton = button;
        ChatActivity chatActivity = parentFragment;
        this.waitingForCallback.remove(str);
        if (cacheFinal && tLObject == null) {
            sendCallback(false, messageObject2, keyboardButton, chatActivity);
        } else if (tLObject == null) {
        } else {
            if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) {
                if (tLObject instanceof TLRPC.TL_urlAuthResultRequest) {
                    chatActivity.showRequestUrlAlert((TLRPC.TL_urlAuthResultRequest) tLObject, request[0], keyboardButton.url);
                } else if (tLObject instanceof TLRPC.TL_urlAuthResultAccepted) {
                    chatActivity.showOpenUrlAlert(((TLRPC.TL_urlAuthResultAccepted) tLObject).url, false);
                } else if (tLObject instanceof TLRPC.TL_urlAuthResultDefault) {
                    TLRPC.TL_urlAuthResultDefault tL_urlAuthResultDefault = (TLRPC.TL_urlAuthResultDefault) tLObject;
                    chatActivity.showOpenUrlAlert(keyboardButton.url, true);
                }
            } else if (!(keyboardButton instanceof TLRPC.TL_keyboardButtonBuy)) {
                TLRPC.TL_messages_botCallbackAnswer res = (TLRPC.TL_messages_botCallbackAnswer) tLObject;
                if (!cacheFinal && res.cache_time != 0) {
                    getMessagesStorage().saveBotCache(str, res);
                }
                TLRPC.TL_game tL_game = null;
                if (res.message != null) {
                    int uid2 = messageObject2.messageOwner.from_id;
                    if (messageObject2.messageOwner.via_bot_id != 0) {
                        uid2 = messageObject2.messageOwner.via_bot_id;
                    }
                    String name = null;
                    if (uid2 > 0) {
                        TLRPC.User user = getMessagesController().getUser(Integer.valueOf(uid2));
                        if (user != null) {
                            name = ContactsController.formatName(user.first_name, user.last_name);
                        }
                    } else {
                        TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-uid2));
                        if (chat != null) {
                            name = chat.title;
                        }
                    }
                    if (name == null) {
                        name = "bot";
                    }
                    if (!res.alert) {
                        chatActivity.showAlert(name, res.message);
                    } else if (parentFragment.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentFragment.getParentActivity());
                        builder.setTitle(name);
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
                        builder.setMessage(res.message);
                        chatActivity.showDialog(builder.create());
                    }
                } else if (res.url != null && parentFragment.getParentActivity() != null) {
                    int uid3 = messageObject2.messageOwner.from_id;
                    if (messageObject2.messageOwner.via_bot_id != 0) {
                        uid = messageObject2.messageOwner.via_bot_id;
                    } else {
                        uid = uid3;
                    }
                    TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(uid));
                    boolean verified = user2 != null && user2.verified;
                    if (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) {
                        if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                            tL_game = messageObject2.messageOwner.media.game;
                        }
                        TLRPC.TL_game game = tL_game;
                        if (game != null) {
                            String str2 = res.url;
                            if (!verified) {
                                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                                if (notificationsSettings.getBoolean("askgame_" + uid, true)) {
                                    z = true;
                                    parentFragment.showOpenGameAlert(game, messageObject, str2, z, uid);
                                    return;
                                }
                            }
                            z = false;
                            parentFragment.showOpenGameAlert(game, messageObject, str2, z, uid);
                            return;
                        }
                        return;
                    }
                    chatActivity.showOpenUrlAlert(res.url, false);
                }
            } else if (tLObject instanceof TLRPC.TL_payments_paymentForm) {
                getMessagesController().putUsers(((TLRPC.TL_payments_paymentForm) tLObject).users, false);
            } else {
                boolean z2 = tLObject instanceof TLRPC.TL_payments_paymentReceipt;
            }
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, TLRPC.KeyboardButton button) {
        int type;
        if (messageObject == null || button == null) {
            return false;
        }
        if (button instanceof TLRPC.TL_keyboardButtonUrlAuth) {
            type = 3;
        } else if ((button instanceof TLRPC.TL_keyboardButtonGame) != 0) {
            type = 1;
        } else if ((button instanceof TLRPC.TL_keyboardButtonBuy) != 0) {
            type = 2;
        } else {
            type = 0;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type);
    }

    public void sendEditMessageMedia(TLRPC.InputPeer peer, int id, TLRPC.InputMedia media) {
        if (peer != null) {
            TLRPCContacts.TL_EditMessageMedia request = new TLRPCContacts.TL_EditMessageMedia();
            request.peer = peer;
            request.id = id;
            request.media = media;
            getConnectionsManager().sendRequest(request, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendEditMessageMedia$19$SendMessagesHelper(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendEditMessageMedia$19$SendMessagesHelper(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    public void sendGame(TLRPC.InputPeer peer, TLRPC.TL_inputMediaGame game, long random_id, long taskId) {
        long newTaskId;
        if (peer != null && game != null) {
            TLRPC.TL_messages_sendMedia request = new TLRPC.TL_messages_sendMedia();
            request.peer = peer;
            if (request.peer instanceof TLRPC.TL_inputPeerChannel) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                request.silent = notificationsSettings.getBoolean("silent_" + (-peer.channel_id), false);
            } else if (request.peer instanceof TLRPC.TL_inputPeerChat) {
                SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                request.silent = notificationsSettings2.getBoolean("silent_" + (-peer.chat_id), false);
            } else {
                SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(this.currentAccount);
                request.silent = notificationsSettings3.getBoolean("silent_" + peer.user_id, false);
            }
            request.random_id = random_id != 0 ? random_id : getNextRandomId();
            request.message = "";
            request.media = game;
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    data = new NativeByteBuffer(peer.getObjectSize() + game.getObjectSize() + 4 + 8);
                    data.writeInt32(3);
                    data.writeInt64(random_id);
                    peer.serializeToStream(data);
                    game.serializeToStream(data);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                newTaskId = getMessagesStorage().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            getConnectionsManager().sendRequest(request, new RequestDelegate(newTaskId) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendGame$20$SendMessagesHelper(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendGame$20$SendMessagesHelper(long newTaskId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
        if (newTaskId != 0) {
            getMessagesStorage().removePendingTask(newTaskId);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        MessageObject messageObject = retryMessageObject;
        long dialogId = retryMessageObject.getDialogId();
        String str = messageObject.messageOwner.attachPath;
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, dialogId, str, (MessageObject) null, (TLRPC.WebPage) null, true, retryMessageObject, (ArrayList<TLRPC.MessageEntity>) null, messageObject.messageOwner.reply_markup, messageObject.messageOwner.params, !messageObject.messageOwner.silent, messageObject.scheduled ? messageObject.messageOwner.date : 0, 0, (Object) null);
    }

    public void sendMessage(TLRPC.User user, long peer, MessageObject reply_to_msg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, user, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, peer, (String) null, reply_to_msg, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, params, notify, scheduleDate, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate, int ttl, Object parentObject) {
        String str = caption;
        sendMessage((String) null, str, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, videoEditedInfo, (TLRPC.User) null, document, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, peer, path, reply_to_msg, (TLRPC.WebPage) null, true, (MessageObject) null, entities, replyMarkup, params, notify, scheduleDate, ttl, parentObject);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, TLRPC.WebPage webPage, boolean searchLinks, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage(message, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, peer, (String) null, reply_to_msg, webPage, searchLinks, (MessageObject) null, entities, replyMarkup, params, notify, scheduleDate, 0, (Object) null);
    }

    public void sendMessage(TLRPC.MessageMedia location, long peer, MessageObject reply_to_msg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage((String) null, (String) null, location, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, peer, (String) null, reply_to_msg, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, params, notify, scheduleDate, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_messageMediaPoll poll, long peer, MessageObject reply_to_msg, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, poll, peer, (String) null, reply_to_msg, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, params, notify, scheduleDate, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_game game, long peer, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, game, (TLRPC.TL_messageMediaPoll) null, peer, (String) null, (MessageObject) null, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, params, notify, scheduleDate, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_photo photo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<TLRPC.MessageEntity> entities, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> params, boolean notify, int scheduleDate, int ttl, Object parentObject) {
        String str = caption;
        sendMessage((String) null, str, (TLRPC.MessageMedia) null, photo, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, peer, path, reply_to_msg, (TLRPC.WebPage) null, true, (MessageObject) null, entities, replyMarkup, params, notify, scheduleDate, ttl, parentObject);
    }

    public void sendRedpaketTransfer(TLRPC.User user, long peer, String message, String caption) {
        TLRPC.EncryptedChat encryptedChat;
        TLRPC.User user2 = user;
        long j = peer;
        if ((user2 == null || user2.phone != null) && j != 0) {
            int lower_id = (int) j;
            int high_id = (int) (j >> 32);
            TLRPC.InputPeer sendToPeer = lower_id != 0 ? getMessagesController().getInputPeer(lower_id) : null;
            if (lower_id == 0) {
                TLRPC.EncryptedChat encryptedChat2 = getMessagesController().getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat2 != null) {
                    encryptedChat = encryptedChat2;
                } else {
                    return;
                }
            } else if (sendToPeer instanceof TLRPC.TL_inputPeerChannel) {
                TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(sendToPeer.channel_id));
                boolean isChannel = chat != null && !chat.megagroup;
                encryptedChat = null;
                boolean z = isChannel;
            } else {
                encryptedChat = null;
            }
            if (message == null) {
                return;
            }
            if (encryptedChat != null) {
                try {
                    new TLRPC.TL_message_secret();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new TLRPC.TL_message();
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v1, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v2, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v13, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v19, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v24, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v21, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v22, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v23, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v24, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v15, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v27, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v28, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v12, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v7, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v32, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v8, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v23, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v47, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v23, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v44, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v49, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v25, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v38, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v64, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v52, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v56, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v31, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v35, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v58, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v45, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v59, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v46, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v60, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v61, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v48, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v62, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v49, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v50, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v53, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v67, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v68, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v69, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v56, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v70, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v57, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v71, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v58, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v72, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v59, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v73, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v78, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v31, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v32, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v61, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v83, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v85, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v86, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v64, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v87, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v90, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v33, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v66, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v91, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v67, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v98, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v34, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v102, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v35, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v103, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v107, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v73, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v108, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v39, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v74, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v110, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v75, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v111, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v42, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v43, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v44, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v115, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v116, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v78, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v119, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v57, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v79, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v122, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v58, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v80, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v123, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v59, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v81, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v124, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v60, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v82, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v125, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v61, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v12, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v78, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v38, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v39, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v86, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v47, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v40, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v89, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v65, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v41, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v43, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v44, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v97, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v80, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v100, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v101, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v56, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v102, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v57, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v103, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v58, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v91, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v59, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v60, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v108, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v61, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v62, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v112, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v63, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v64, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v65, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v115, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v66, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v117, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v104, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v5, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v122, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v29, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v124, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v65, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v125, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v14, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v127, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v216, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v128, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v35, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v15, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v130, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v112, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v113, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v131, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v132, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v133, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v117, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v51, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v120, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v66, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v136, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v137, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v43, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v94, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v16, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v139, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v249, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v46, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v140, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v141, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v144, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v148, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v149, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v67, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v68, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v112, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v17, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v113, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v114, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v151, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v115, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v69, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v116, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v152, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v117, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v153, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v118, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v19, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v71, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v119, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v287, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v72, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v73, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v120, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v74, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v121, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v75, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v122, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v157, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v76, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v123, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v158, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v77, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v124, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaShareContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v20, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v126, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v159, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v127, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v160, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v161, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v57, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v149, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v164, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v165, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v60, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v130, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v21, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v61, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v169, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v63, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v170, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v172, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v158, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v326, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v174, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v67, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v176, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v177, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v129, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v75, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v130, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v181, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v131, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v133, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v23, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v174, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v79, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v184, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v137, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v32, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v138, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v24, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v186, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v168, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v25, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v187, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v169, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v140, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v26, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v188, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v170, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v141, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v27, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v189, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v171, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v142, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v143, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v190, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v174, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v144, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v191, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v175, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v145, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v151, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v192, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v176, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v146, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v193, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v178, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v147, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v10, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v194, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v179, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v148, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v195, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v180, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v149, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v33, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v28, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r77v12, resolved type: java.util.HashMap} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v29, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v143, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v197, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v150, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v30, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v144, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v120, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v151, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v198, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v189, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v152, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v145, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v199, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v153, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v121, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v146, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v200, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v154, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v148, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v147, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v201, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v155, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v149, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v148, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v202, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v156, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v150, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v15, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v35, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v11, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v12, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v203, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v125, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v151, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v157, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v14, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v15, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v204, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v126, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v152, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v158, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v123, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v205, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v151, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v127, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v153, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v159, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v104, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v124, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v206, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v152, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v128, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v154, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v160, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v125, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v207, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v153, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v129, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v105, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v155, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v161, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v126, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v208, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v154, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v130, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v160, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v162, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v127, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v209, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v155, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v131, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v179, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v163, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v128, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v210, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v156, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v132, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v183, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v164, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v129, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v212, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v157, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v133, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v184, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v165, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v16, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v17, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v18, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v19, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v20, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v213, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v158, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v134, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v110, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v186, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v166, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v21, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v131, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v214, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v159, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v135, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v187, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v167, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v22, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v174, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v160, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v136, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v215, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v161, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v137, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v203, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v168, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v216, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v162, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v138, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v169, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v208, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v170, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v139, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v78, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v218, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v191, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v209, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v163, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v171, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v215, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v192, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v24, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v399, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v197, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v140, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v79, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v219, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v193, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v210, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v164, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v173, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v141, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v80, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v117, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v220, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v194, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v211, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v165, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v174, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v142, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v81, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v221, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v195, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v212, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v166, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v175, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v143, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v119, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v222, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v196, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v213, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r45v167, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v82, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v176, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v177, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v120, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v197, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v214, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v83, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v224, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v178, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v179, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v145, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v180, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v121, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v226, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v199, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v215, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v84, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v38, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v146, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v227, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v200, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v216, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v85, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v181, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v39, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v147, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v182, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v228, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v201, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v217, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v86, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v183, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v228, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v39, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v40, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v148, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v184, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v229, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v202, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v218, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v87, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v149, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v230, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v204, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v219, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v88, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v185, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v238, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v44, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v40, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v41, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v150, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v231, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v206, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v220, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v89, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v186, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v151, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v187, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v127, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v207, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v221, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v90, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v152, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v189, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v208, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v91, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v222, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v191, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v153, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v234, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v209, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v192, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v92, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v223, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v154, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v193, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v248, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v210, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v93, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v224, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v194, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v195, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v211, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v249, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v94, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v225, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v196, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v197, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v212, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v226, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v95, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v250, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v200, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v202, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v213, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v203, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v227, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v96, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v253, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v206, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v214, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v214, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v228, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v97, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v255, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v216, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v159, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v215, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v217, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v229, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v98, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v257, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v216, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v217, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v160, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v218, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v261, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v230, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v99, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v271, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v219, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v272, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v273, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v161, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v220, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v264, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v231, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v100, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v275, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v221, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v162, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v222, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v221, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v232, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v101, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v267, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v19, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v279, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v163, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v223, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v273, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v223, resolved type: im.bclpbkiauv.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v233, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v102, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v20, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v164, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v224, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v274, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v234, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v103, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v165, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v225, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v275, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v235, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v104, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v50, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v228, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v51, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v218, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v426, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v122, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r69v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v219, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v114, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v427, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v166, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v236, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v105, resolved type: im.bclpbkiauv.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v230, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v279, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v53, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v54, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v116, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v55, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v117, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v56, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v118, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v57, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v58, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v120, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v167, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v244, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v308, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v237, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v106, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v59, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v252, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v60, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v122, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v61, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v288, resolved type: im.bclpbkiauv.tgnet.TLRPC$InputPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v220, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v430, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v169, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v170, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v67, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v68, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v69, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v70, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v71, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v121, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v123, resolved type: im.bclpbkiauv.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v293, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v294, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v295, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v296, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v298, resolved type: im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v127, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v23, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r22v0 */
    /* JADX WARNING: type inference failed for: r2v90 */
    /* JADX WARNING: type inference failed for: r2v122 */
    /* JADX WARNING: type inference failed for: r4v163 */
    /* JADX WARNING: type inference failed for: r4v168 */
    /* JADX WARNING: type inference failed for: r22v85 */
    /* JADX WARNING: type inference failed for: r22v90 */
    /* JADX WARNING: type inference failed for: r22v91 */
    /* JADX WARNING: type inference failed for: r22v92 */
    /* JADX WARNING: type inference failed for: r22v93 */
    /* JADX WARNING: type inference failed for: r22v98 */
    /* JADX WARNING: type inference failed for: r22v99 */
    /* JADX WARNING: type inference failed for: r22v103 */
    /* JADX WARNING: type inference failed for: r22v105 */
    /* JADX WARNING: type inference failed for: r22v106 */
    /* JADX WARNING: type inference failed for: r22v107 */
    /* JADX WARNING: type inference failed for: r11v215 */
    /* JADX WARNING: type inference failed for: r22v110 */
    /* JADX WARNING: type inference failed for: r22v111 */
    /* JADX WARNING: type inference failed for: r11v219 */
    /* JADX WARNING: type inference failed for: r22v113 */
    /* JADX WARNING: type inference failed for: r11v220 */
    /* JADX WARNING: type inference failed for: r22v115 */
    /* JADX WARNING: type inference failed for: r22v116 */
    /* JADX WARNING: type inference failed for: r22v117 */
    /* JADX WARNING: type inference failed for: r11v224 */
    /* JADX WARNING: type inference failed for: r26v201 */
    /* JADX WARNING: type inference failed for: r26v202 */
    /* JADX WARNING: type inference failed for: r26v206 */
    /* JADX WARNING: type inference failed for: r26v207 */
    /* JADX WARNING: type inference failed for: r26v208 */
    /* JADX WARNING: type inference failed for: r26v209 */
    /* JADX WARNING: type inference failed for: r26v210 */
    /* JADX WARNING: type inference failed for: r26v211 */
    /* JADX WARNING: type inference failed for: r26v212 */
    /* JADX WARNING: type inference failed for: r26v214 */
    /* JADX WARNING: type inference failed for: r26v215 */
    /* JADX WARNING: type inference failed for: r26v216 */
    /* JADX WARNING: type inference failed for: r2v292 */
    /* JADX WARNING: type inference failed for: r4v323 */
    /* JADX WARNING: type inference failed for: r4v324 */
    /* JADX WARNING: type inference failed for: r4v325 */
    /* JADX WARNING: type inference failed for: r37v16 */
    /* JADX WARNING: Code restructure failed: missing block: B:1422:0x23c7, code lost:
        r19 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1424:?, code lost:
        r5.media.w = r11.w;
        r5.media.h = r11.h;
        r5.media.duration = r11.duration;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1425:0x23da, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1426:0x23db, code lost:
        r26 = r69;
        r1 = r71;
        r4 = r0;
        r45 = r3;
        r69 = r19;
        r11 = r36;
        r3 = r44;
        r19 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0847, code lost:
        r2.attributes.remove(r1);
        r5 = new im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeSticker_layer55();
        r2.attributes.add(r5);
        r5.alt = r6.alt;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x085c, code lost:
        if (r6.stickerset == null) goto L_0x08ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0862, code lost:
        if ((r6.stickerset instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetShortName) == false) goto L_0x0891;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:?, code lost:
        r7 = r6.stickerset.short_name;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x0868, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x086b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x086c, code lost:
        r10 = r55;
        r33 = r56;
        r49 = r59;
        r51 = r60;
        r35 = r69;
        r25 = r77;
        r4 = r0;
        r1 = r9;
        r11 = r12;
        r52 = r15;
        r69 = r19;
        r9 = null;
        r45 = r22;
        r26 = r23;
        r3 = r31;
        r22 = r58;
        r19 = r61;
        r23 = r64;
        r12 = r2;
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0897, code lost:
        r18 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:?, code lost:
        r7 = getMediaDataController().getStickerSetName(r6.stickerset.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x08a4, code lost:
        if (android.text.TextUtils.isEmpty(r7) != false) goto L_0x08b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x08a6, code lost:
        r5.stickerset = new im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetShortName();
        r5.stickerset.short_name = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x08b2, code lost:
        r5.stickerset = new im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x08ba, code lost:
        r18 = r12;
        r5.stickerset = new im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x08c4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x08c5, code lost:
        r10 = r55;
        r33 = r56;
        r49 = r59;
        r51 = r60;
        r12 = r62;
        r35 = r69;
        r25 = r77;
        r4 = r0;
        r1 = r9;
        r2 = r13;
        r52 = r15;
        r11 = r18;
        r69 = r19;
        r9 = null;
        r45 = r22;
        r26 = r23;
        r3 = r31;
        r22 = r58;
        r19 = r61;
        r23 = r64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0dde, code lost:
        if (r14.type == 2) goto L_0x0de0;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll, code=java.util.HashMap<java.lang.String, java.lang.String>, for r64v0, types: [im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0290 A[SYNTHETIC, Splitter:B:102:0x0290] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x029e A[Catch:{ Exception -> 0x02dd }] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x1ba6 A[SYNTHETIC, Splitter:B:1128:0x1ba6] */
    /* JADX WARNING: Removed duplicated region for block: B:1133:0x1bd0 A[Catch:{ Exception -> 0x1bb1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1191:0x1dcf A[SYNTHETIC, Splitter:B:1191:0x1dcf] */
    /* JADX WARNING: Removed duplicated region for block: B:1196:0x1de3 A[SYNTHETIC, Splitter:B:1196:0x1de3] */
    /* JADX WARNING: Removed duplicated region for block: B:1201:0x1df4 A[Catch:{ Exception -> 0x1d8d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1203:0x1dfe A[Catch:{ Exception -> 0x1d8d }] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x03ad A[SYNTHETIC, Splitter:B:135:0x03ad] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x03b5 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:1418:0x23b9 A[SYNTHETIC, Splitter:B:1418:0x23b9] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x03c8 A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x03cd A[Catch:{ Exception -> 0x03da }] */
    /* JADX WARNING: Removed duplicated region for block: B:1436:0x241a A[SYNTHETIC, Splitter:B:1436:0x241a] */
    /* JADX WARNING: Removed duplicated region for block: B:1439:0x2455  */
    /* JADX WARNING: Removed duplicated region for block: B:1465:0x24a9 A[Catch:{ Exception -> 0x24b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1466:0x24ab A[Catch:{ Exception -> 0x24b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1586:0x27e4 A[SYNTHETIC, Splitter:B:1586:0x27e4] */
    /* JADX WARNING: Removed duplicated region for block: B:1593:0x27f6 A[Catch:{ Exception -> 0x27c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1594:0x27f8 A[Catch:{ Exception -> 0x27c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1673:0x2a6c A[Catch:{ Exception -> 0x2aaf }] */
    /* JADX WARNING: Removed duplicated region for block: B:1674:0x2a6e A[Catch:{ Exception -> 0x2aaf }] */
    /* JADX WARNING: Removed duplicated region for block: B:1698:0x2ae1 A[SYNTHETIC, Splitter:B:1698:0x2ae1] */
    /* JADX WARNING: Removed duplicated region for block: B:1704:0x2aec A[SYNTHETIC, Splitter:B:1704:0x2aec] */
    /* JADX WARNING: Removed duplicated region for block: B:1729:0x2b85  */
    /* JADX WARNING: Removed duplicated region for block: B:1732:0x2b93 A[SYNTHETIC, Splitter:B:1732:0x2b93] */
    /* JADX WARNING: Removed duplicated region for block: B:1784:0x2cfc A[Catch:{ Exception -> 0x2d82 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1787:0x2d15 A[Catch:{ Exception -> 0x2d82 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1789:0x2d25  */
    /* JADX WARNING: Removed duplicated region for block: B:1799:0x2d54 A[Catch:{ Exception -> 0x2d76 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1800:0x2d56 A[Catch:{ Exception -> 0x2d76 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1839:0x2e10 A[SYNTHETIC, Splitter:B:1839:0x2e10] */
    /* JADX WARNING: Removed duplicated region for block: B:1848:0x2e38 A[Catch:{ Exception -> 0x2e67 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1851:0x2e47 A[Catch:{ Exception -> 0x2e67 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1852:0x2e49 A[Catch:{ Exception -> 0x2e67 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1864:0x2eb3  */
    /* JADX WARNING: Removed duplicated region for block: B:1866:0x2edb A[SYNTHETIC, Splitter:B:1866:0x2edb] */
    /* JADX WARNING: Removed duplicated region for block: B:1889:0x2f30 A[SYNTHETIC, Splitter:B:1889:0x2f30] */
    /* JADX WARNING: Removed duplicated region for block: B:1894:0x2f4c A[Catch:{ Exception -> 0x2f3b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1901:0x2f64 A[Catch:{ Exception -> 0x2f3b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1904:0x2f70 A[Catch:{ Exception -> 0x2f3b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1905:0x2f72 A[Catch:{ Exception -> 0x2f3b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1908:0x2f86 A[Catch:{ Exception -> 0x2f3b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1916:0x2fcd  */
    /* JADX WARNING: Removed duplicated region for block: B:1954:0x307a A[Catch:{ Exception -> 0x309b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1985:0x328a  */
    /* JADX WARNING: Removed duplicated region for block: B:1986:0x328c  */
    /* JADX WARNING: Removed duplicated region for block: B:1989:0x3292  */
    /* JADX WARNING: Removed duplicated region for block: B:1999:0x2403 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x07ce A[Catch:{ Exception -> 0x0772 }] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x07ec A[Catch:{ Exception -> 0x07f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x081d A[SYNTHETIC, Splitter:B:304:0x081d] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0821 A[Catch:{ Exception -> 0x0905 }] */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08fd  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0ade A[SYNTHETIC, Splitter:B:413:0x0ade] */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0aed  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0aef A[SYNTHETIC, Splitter:B:420:0x0aef] */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0aff A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b60  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0b74  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0be0 A[Catch:{ Exception -> 0x0bc0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0bf2 A[Catch:{ Exception -> 0x0bc0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0c8e  */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0d7e A[SYNTHETIC, Splitter:B:550:0x0d7e] */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x0dc3  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0dc7  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0dd2 A[SYNTHETIC, Splitter:B:572:0x0dd2] */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0e28  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x0e2e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0e3b  */
    /* JADX WARNING: Removed duplicated region for block: B:616:0x0ee0  */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0ef2 A[SYNTHETIC, Splitter:B:619:0x0ef2] */
    /* JADX WARNING: Removed duplicated region for block: B:647:0x0fb7  */
    /* JADX WARNING: Removed duplicated region for block: B:682:0x1087  */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x1783  */
    /* JADX WARNING: Removed duplicated region for block: B:971:0x17a5 A[SYNTHETIC, Splitter:B:971:0x17a5] */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r56, java.lang.String r57, im.bclpbkiauv.tgnet.TLRPC.MessageMedia r58, im.bclpbkiauv.tgnet.TLRPC.TL_photo r59, im.bclpbkiauv.messenger.VideoEditedInfo r60, im.bclpbkiauv.tgnet.TLRPC.User r61, im.bclpbkiauv.tgnet.TLRPC.TL_document r62, im.bclpbkiauv.tgnet.TLRPC.TL_game r63, java.util.HashMap<java.lang.String, java.lang.String> r64, long r65, java.lang.String r67, im.bclpbkiauv.messenger.MessageObject r68, im.bclpbkiauv.tgnet.TLRPC.WebPage r69, boolean r70, im.bclpbkiauv.messenger.MessageObject r71, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.MessageEntity> r72, im.bclpbkiauv.tgnet.TLRPC.ReplyMarkup r73, java.util.HashMap<java.lang.String, java.lang.String> r74, boolean r75, int r76, int r77, java.lang.Object r78) {
        /*
            r55 = this;
            r10 = r55
            r1 = r56
            r2 = r58
            r3 = r59
            r4 = r61
            r5 = r62
            r11 = r63
            r6 = r64
            r12 = r65
            r14 = r67
            r15 = r68
            r7 = r69
            r9 = r71
            r8 = r72
            r15 = r74
            r5 = r77
            r16 = 0
            int r18 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x0027
            return
        L_0x0027:
            if (r1 != 0) goto L_0x0030
            if (r57 != 0) goto L_0x0030
            java.lang.String r18 = ""
            r19 = r18
            goto L_0x0032
        L_0x0030:
            r19 = r57
        L_0x0032:
            r18 = 0
            if (r15 == 0) goto L_0x004b
            java.lang.String r4 = "originalPath"
            boolean r4 = r15.containsKey(r4)
            if (r4 == 0) goto L_0x004b
            java.lang.String r4 = "originalPath"
            java.lang.Object r4 = r15.get(r4)
            r18 = r4
            java.lang.String r18 = (java.lang.String) r18
            r4 = r18
            goto L_0x004d
        L_0x004b:
            r4 = r18
        L_0x004d:
            r18 = 0
            r20 = 0
            r21 = 0
            r22 = -1
            r23 = r4
            int r4 = (int) r12
            r24 = 32
            long r2 = r12 >> r24
            int r3 = (int) r2
            r2 = 0
            r24 = 0
            r57 = r2
            if (r4 == 0) goto L_0x006d
            im.bclpbkiauv.messenger.MessagesController r2 = r55.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer(r4)
            goto L_0x006e
        L_0x006d:
            r2 = 0
        L_0x006e:
            if (r4 != 0) goto L_0x00bc
            im.bclpbkiauv.messenger.MessagesController r13 = r55.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r24 = r13.getEncryptedChat(r12)
            if (r24 != 0) goto L_0x00b5
            if (r9 == 0) goto L_0x00b2
            im.bclpbkiauv.messenger.MessagesStorage r12 = r55.getMessagesStorage()
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r9.messageOwner
            r27 = r3
            boolean r3 = r9.scheduled
            r12.markMessageAsSendError(r13, r3)
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r9.messageOwner
            r12 = 2
            r3.send_state = r12
            im.bclpbkiauv.messenger.NotificationCenter r3 = r55.getNotificationCenter()
            int r12 = im.bclpbkiauv.messenger.NotificationCenter.messageSendError
            r13 = 1
            java.lang.Object[] r13 = new java.lang.Object[r13]
            int r16 = r71.getId()
            java.lang.Integer r16 = java.lang.Integer.valueOf(r16)
            r17 = 0
            r13[r17] = r16
            r3.postNotificationName(r12, r13)
            int r3 = r71.getId()
            r10.processSentMessage(r3)
            goto L_0x00b4
        L_0x00b2:
            r27 = r3
        L_0x00b4:
            return
        L_0x00b5:
            r27 = r3
            r12 = r57
            r13 = r24
            goto L_0x00e0
        L_0x00bc:
            r27 = r3
            boolean r3 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputPeerChannel
            if (r3 == 0) goto L_0x00dc
            im.bclpbkiauv.messenger.MessagesController r3 = r55.getMessagesController()
            int r12 = r2.channel_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r3.getChat(r12)
            if (r3 == 0) goto L_0x00d8
            boolean r12 = r3.megagroup
            if (r12 != 0) goto L_0x00d8
            r12 = 1
            goto L_0x00d9
        L_0x00d8:
            r12 = 0
        L_0x00d9:
            r13 = r24
            goto L_0x00e0
        L_0x00dc:
            r12 = r57
            r13 = r24
        L_0x00e0:
            java.lang.String r3 = "http"
            java.lang.String r10 = "query_id"
            r28 = r4
            java.lang.String r4 = ""
            if (r9 == 0) goto L_0x037e
            r31 = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r9.messageOwner     // Catch:{ Exception -> 0x0354 }
            boolean r18 = r71.isForwarded()     // Catch:{ Exception -> 0x032b }
            if (r18 == 0) goto L_0x0117
            r18 = 4
            r11 = r2
            r8 = r5
            r35 = r7
            r57 = r10
            r32 = r12
            r10 = r15
            r69 = r19
            r2 = r31
            r26 = 4
            r15 = r58
            r7 = r59
            r5 = r62
            r12 = r1
            r1 = r6
            r6 = r61
            r54 = r18
            r18 = r3
            r3 = r54
            goto L_0x0a6e
        L_0x0117:
            r32 = r12
            int r12 = r9.type     // Catch:{ Exception -> 0x0304 }
            if (r12 == 0) goto L_0x0271
            boolean r12 = r71.isAnimatedEmoji()     // Catch:{ Exception -> 0x0304 }
            if (r12 == 0) goto L_0x0128
            r33 = r4
            r11 = 2
            goto L_0x0274
        L_0x0128:
            int r12 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 4
            if (r12 != r11) goto L_0x0141
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r11 = r2.media     // Catch:{ Exception -> 0x0304 }
            r22 = 1
            r12 = r61
            r18 = r62
            r33 = r4
            r4 = r11
            r26 = r22
            r11 = 2
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x0141:
            int r11 = r9.type     // Catch:{ Exception -> 0x0304 }
            r12 = 1
            if (r11 != r12) goto L_0x015e
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r11 = r2.media     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$Photo r11 = r11.photo     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r11 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r11     // Catch:{ Exception -> 0x0304 }
            r22 = 2
            r12 = r61
            r18 = r62
            r33 = r4
            r26 = r22
            r4 = r58
            r22 = r6
            r6 = r11
            r11 = 2
            goto L_0x028e
        L_0x015e:
            int r11 = r9.type     // Catch:{ Exception -> 0x0304 }
            r12 = 3
            if (r11 == r12) goto L_0x0259
            int r11 = r9.type     // Catch:{ Exception -> 0x0304 }
            r12 = 5
            if (r11 == r12) goto L_0x0259
            if (r60 == 0) goto L_0x016f
            r33 = r4
            r11 = 2
            goto L_0x025c
        L_0x016f:
            int r11 = r9.type     // Catch:{ Exception -> 0x0304 }
            r12 = 12
            if (r11 != r12) goto L_0x01df
            im.bclpbkiauv.tgnet.TLRPC$TL_userRequest_old2 r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x0304 }
            r11.<init>()     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r2.media     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r12 = r12.phone_number     // Catch:{ Exception -> 0x01b8 }
            r11.phone = r12     // Catch:{ Exception -> 0x01b8 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r2.media     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r12 = r12.first_name     // Catch:{ Exception -> 0x01b8 }
            r11.first_name = r12     // Catch:{ Exception -> 0x01b8 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r2.media     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r12 = r12.last_name     // Catch:{ Exception -> 0x01b8 }
            r11.last_name = r12     // Catch:{ Exception -> 0x01b8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x01b8 }
            r12.<init>()     // Catch:{ Exception -> 0x01b8 }
            r12.platform = r4     // Catch:{ Exception -> 0x01b8 }
            r12.reason = r4     // Catch:{ Exception -> 0x01b8 }
            r33 = r4
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r4 = r4.vcard     // Catch:{ Exception -> 0x01b8 }
            r12.text = r4     // Catch:{ Exception -> 0x01b8 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r4 = r11.restriction_reason     // Catch:{ Exception -> 0x01b8 }
            r4.add(r12)     // Catch:{ Exception -> 0x01b8 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x01b8 }
            int r4 = r4.user_id     // Catch:{ Exception -> 0x01b8 }
            r11.id = r4     // Catch:{ Exception -> 0x01b8 }
            r22 = 6
            r4 = r58
            r18 = r62
            r12 = r11
            r26 = r22
            r11 = 2
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x01b8:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r23 = r6
            r19 = r11
            r11 = r2
            r2 = r13
            goto L_0x3281
        L_0x01df:
            r33 = r4
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 8
            if (r4 == r11) goto L_0x0243
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 9
            if (r4 == r11) goto L_0x0243
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 13
            if (r4 == r11) goto L_0x0243
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 14
            if (r4 == r11) goto L_0x0243
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 15
            if (r4 != r11) goto L_0x0201
            r11 = 2
            goto L_0x0244
        L_0x0201:
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r11 = 2
            if (r4 != r11) goto L_0x021c
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$TL_document r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r4     // Catch:{ Exception -> 0x0304 }
            r22 = 8
            r12 = r61
            r18 = r4
            r26 = r22
            r4 = r58
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x021c:
            int r4 = r9.type     // Catch:{ Exception -> 0x0304 }
            r12 = 17
            if (r4 != r12) goto L_0x0236
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r4     // Catch:{ Exception -> 0x0304 }
            r22 = 10
            r6 = r59
            r12 = r61
            r18 = r62
            r26 = r22
            r22 = r4
            r4 = r58
            goto L_0x028e
        L_0x0236:
            r4 = r58
            r12 = r61
            r18 = r62
            r26 = r22
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x0243:
            r11 = 2
        L_0x0244:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$TL_document r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r4     // Catch:{ Exception -> 0x0304 }
            r22 = 7
            r12 = r61
            r18 = r4
            r26 = r22
            r4 = r58
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x0259:
            r33 = r4
            r11 = 2
        L_0x025c:
            r22 = 3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r2.media     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$TL_document r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r4     // Catch:{ Exception -> 0x0304 }
            r12 = r61
            r18 = r4
            r26 = r22
            r4 = r58
            r22 = r6
            r6 = r59
            goto L_0x028e
        L_0x0271:
            r33 = r4
            r11 = 2
        L_0x0274:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r9.messageOwner     // Catch:{ Exception -> 0x0304 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r4.media     // Catch:{ Exception -> 0x0304 }
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame     // Catch:{ Exception -> 0x0304 }
            if (r4 == 0) goto L_0x027d
            goto L_0x0280
        L_0x027d:
            java.lang.String r4 = r2.message     // Catch:{ Exception -> 0x0304 }
            r1 = r4
        L_0x0280:
            r22 = 0
            r4 = r58
            r12 = r61
            r18 = r62
            r26 = r22
            r22 = r6
            r6 = r59
        L_0x028e:
            if (r15 == 0) goto L_0x0298
            boolean r34 = r15.containsKey(r10)     // Catch:{ Exception -> 0x02dd }
            if (r34 == 0) goto L_0x0298
            r26 = 9
        L_0x0298:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r11 = r2.media     // Catch:{ Exception -> 0x02dd }
            int r11 = r11.ttl_seconds     // Catch:{ Exception -> 0x02dd }
            if (r11 <= 0) goto L_0x02c0
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r11 = r2.media     // Catch:{ Exception -> 0x02dd }
            int r11 = r11.ttl_seconds     // Catch:{ Exception -> 0x02dd }
            r5 = r11
            r11 = r2
            r8 = r5
            r35 = r7
            r57 = r10
            r10 = r15
            r5 = r18
            r69 = r19
            r2 = r31
            r18 = r3
            r15 = r4
            r7 = r6
            r6 = r12
            r3 = r26
            r4 = r33
            r26 = 4
            r12 = r1
            r1 = r22
            goto L_0x0a6e
        L_0x02c0:
            r11 = r2
            r8 = r5
            r35 = r7
            r57 = r10
            r10 = r15
            r5 = r18
            r69 = r19
            r2 = r31
            r18 = r3
            r15 = r4
            r7 = r6
            r6 = r12
            r3 = r26
            r4 = r33
            r26 = 4
            r12 = r1
            r1 = r22
            goto L_0x0a6e
        L_0x02dd:
            r0 = move-exception
            r10 = r55
            r51 = r60
            r33 = r1
            r11 = r2
            r25 = r5
            r49 = r6
            r35 = r7
            r1 = r9
            r2 = r13
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r26
            r3 = r31
            r19 = r12
            r12 = r18
            r26 = r23
            r23 = r22
            r22 = r4
            r4 = r0
            goto L_0x3281
        L_0x0304:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r11 = r2
            r25 = r5
            r35 = r7
            r1 = r9
            r2 = r13
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r6
            goto L_0x3281
        L_0x032b:
            r0 = move-exception
            r32 = r12
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r11 = r2
            r25 = r5
            r35 = r7
            r1 = r9
            r2 = r13
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r6
            goto L_0x3281
        L_0x0354:
            r0 = move-exception
            r32 = r12
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r25 = r5
            r35 = r7
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r6
            goto L_0x3281
        L_0x037e:
            r31 = r2
            r33 = r4
            r32 = r12
            if (r1 == 0) goto L_0x0429
            if (r13 == 0) goto L_0x038e
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0401 }
            r2.<init>()     // Catch:{ Exception -> 0x0401 }
            goto L_0x0393
        L_0x038e:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0401 }
            r2.<init>()     // Catch:{ Exception -> 0x0401 }
        L_0x0393:
            if (r13 == 0) goto L_0x03aa
            boolean r4 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webPagePending     // Catch:{ Exception -> 0x0304 }
            if (r4 == 0) goto L_0x03aa
            java.lang.String r4 = r7.url     // Catch:{ Exception -> 0x0304 }
            if (r4 == 0) goto L_0x03a8
            im.bclpbkiauv.tgnet.TLRPC$TL_webPageUrlPending r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x0304 }
            r4.<init>()     // Catch:{ Exception -> 0x0304 }
            java.lang.String r11 = r7.url     // Catch:{ Exception -> 0x0304 }
            r4.url = r11     // Catch:{ Exception -> 0x0304 }
            goto L_0x03ab
        L_0x03a8:
            r4 = 0
            goto L_0x03ab
        L_0x03aa:
            r4 = r7
        L_0x03ab:
            if (r4 != 0) goto L_0x03b5
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x03da }
            r7.<init>()     // Catch:{ Exception -> 0x03da }
            r2.media = r7     // Catch:{ Exception -> 0x03da }
            goto L_0x03c0
        L_0x03b5:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x03da }
            r7.<init>()     // Catch:{ Exception -> 0x03da }
            r2.media = r7     // Catch:{ Exception -> 0x03da }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r2.media     // Catch:{ Exception -> 0x03da }
            r7.webpage = r4     // Catch:{ Exception -> 0x03da }
        L_0x03c0:
            if (r15 == 0) goto L_0x03cd
            boolean r7 = r15.containsKey(r10)     // Catch:{ Exception -> 0x03da }
            if (r7 == 0) goto L_0x03cd
            r7 = 9
            r22 = r7
            goto L_0x03d0
        L_0x03cd:
            r7 = 0
            r22 = r7
        L_0x03d0:
            r2.message = r1     // Catch:{ Exception -> 0x03da }
            r12 = r2
            r7 = r4
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x03da:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r33 = r1
            r11 = r2
            r35 = r4
            r25 = r5
            r1 = r9
            r2 = r13
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r4 = r0
            r23 = r6
            goto L_0x3281
        L_0x0401:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r25 = r5
            r35 = r7
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r6
            goto L_0x3281
        L_0x0429:
            if (r6 == 0) goto L_0x0443
            if (r13 == 0) goto L_0x0433
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0401 }
            r2.<init>()     // Catch:{ Exception -> 0x0401 }
            goto L_0x0438
        L_0x0433:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0401 }
            r2.<init>()     // Catch:{ Exception -> 0x0401 }
        L_0x0438:
            r2.media = r6     // Catch:{ Exception -> 0x0304 }
            r22 = 10
            r12 = r2
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x0443:
            r2 = r58
            if (r2 == 0) goto L_0x04bf
            if (r13 == 0) goto L_0x044f
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0497 }
            r4.<init>()     // Catch:{ Exception -> 0x0497 }
            goto L_0x0454
        L_0x044f:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0497 }
            r4.<init>()     // Catch:{ Exception -> 0x0497 }
        L_0x0454:
            r4.media = r2     // Catch:{ Exception -> 0x0470 }
            if (r15 == 0) goto L_0x0467
            boolean r11 = r15.containsKey(r10)     // Catch:{ Exception -> 0x0470 }
            if (r11 == 0) goto L_0x0467
            r22 = 9
            r12 = r4
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x0467:
            r22 = 1
            r12 = r4
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x0470:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r33 = r1
            r11 = r4
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r4 = r0
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x0497:
            r0 = move-exception
            r10 = r55
            r49 = r59
            r51 = r60
            r12 = r62
            r4 = r0
            r33 = r1
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x04bf:
            r4 = r59
            if (r4 == 0) goto L_0x0591
            if (r13 == 0) goto L_0x04f3
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x04cb }
            r11.<init>()     // Catch:{ Exception -> 0x04cb }
            goto L_0x04f8
        L_0x04cb:
            r0 = move-exception
            r10 = r55
            r51 = r60
            r12 = r62
            r33 = r1
            r49 = r4
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r4 = r0
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x04f3:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x05e4 }
            r11.<init>()     // Catch:{ Exception -> 0x05e4 }
        L_0x04f8:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPhoto r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x056b }
            r12.<init>()     // Catch:{ Exception -> 0x056b }
            r11.media = r12     // Catch:{ Exception -> 0x056b }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r11.media     // Catch:{ Exception -> 0x056b }
            int r1 = r12.flags     // Catch:{ Exception -> 0x056b }
            r18 = 3
            r1 = r1 | 3
            r12.flags = r1     // Catch:{ Exception -> 0x056b }
            if (r8 == 0) goto L_0x050d
            r11.entities = r8     // Catch:{ Exception -> 0x056b }
        L_0x050d:
            if (r5 == 0) goto L_0x051f
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r11.media     // Catch:{ Exception -> 0x056b }
            r1.ttl_seconds = r5     // Catch:{ Exception -> 0x056b }
            r11.ttl = r5     // Catch:{ Exception -> 0x056b }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r11.media     // Catch:{ Exception -> 0x056b }
            int r12 = r1.flags     // Catch:{ Exception -> 0x056b }
            r18 = 4
            r12 = r12 | 4
            r1.flags = r12     // Catch:{ Exception -> 0x056b }
        L_0x051f:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r11.media     // Catch:{ Exception -> 0x056b }
            r1.photo = r4     // Catch:{ Exception -> 0x056b }
            if (r15 == 0) goto L_0x0530
            boolean r1 = r15.containsKey(r10)     // Catch:{ Exception -> 0x056b }
            if (r1 == 0) goto L_0x0530
            r1 = 9
            r22 = r1
            goto L_0x0533
        L_0x0530:
            r1 = 2
            r22 = r1
        L_0x0533:
            if (r14 == 0) goto L_0x0544
            int r1 = r67.length()     // Catch:{ Exception -> 0x056b }
            if (r1 <= 0) goto L_0x0544
            boolean r1 = r14.startsWith(r3)     // Catch:{ Exception -> 0x056b }
            if (r1 == 0) goto L_0x0544
            r11.attachPath = r14     // Catch:{ Exception -> 0x056b }
            goto L_0x0564
        L_0x0544:
            java.util.ArrayList r1 = r4.sizes     // Catch:{ Exception -> 0x056b }
            java.util.ArrayList r12 = r4.sizes     // Catch:{ Exception -> 0x056b }
            int r12 = r12.size()     // Catch:{ Exception -> 0x056b }
            r18 = 1
            int r12 = r12 + -1
            java.lang.Object r1 = r1.get(r12)     // Catch:{ Exception -> 0x056b }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r1 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r1     // Catch:{ Exception -> 0x056b }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r1 = r1.location     // Catch:{ Exception -> 0x056b }
            r12 = 1
            java.io.File r18 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r1, r12)     // Catch:{ Exception -> 0x056b }
            java.lang.String r12 = r18.toString()     // Catch:{ Exception -> 0x056b }
            r11.attachPath = r12     // Catch:{ Exception -> 0x056b }
        L_0x0564:
            r12 = r11
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x056b:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r51 = r60
            r12 = r62
            r49 = r4
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r4 = r0
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x0591:
            r11 = r63
            if (r11 == 0) goto L_0x060c
            im.bclpbkiauv.tgnet.TLRPC$TL_message r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x05e4 }
            r1.<init>()     // Catch:{ Exception -> 0x05e4 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaGame r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x05bd }
            r12.<init>()     // Catch:{ Exception -> 0x05bd }
            r1.media = r12     // Catch:{ Exception -> 0x05bd }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r12 = r1.media     // Catch:{ Exception -> 0x05bd }
            r12.game = r11     // Catch:{ Exception -> 0x05bd }
            if (r15 == 0) goto L_0x05b6
            boolean r12 = r15.containsKey(r10)     // Catch:{ Exception -> 0x05bd }
            if (r12 == 0) goto L_0x05b6
            r22 = 9
            r12 = r1
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x05b6:
            r12 = r1
            r4 = r33
            r26 = 4
            goto L_0x0987
        L_0x05bd:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r51 = r60
            r12 = r62
            r11 = r1
            r49 = r4
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r4 = r0
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x05e4:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r51 = r60
            r12 = r62
            r49 = r4
            r25 = r5
            r35 = r7
            r1 = r9
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r19 = r61
            r4 = r0
            r22 = r2
            r23 = r6
            r2 = r13
            goto L_0x3281
        L_0x060c:
            r1 = r61
            if (r1 == 0) goto L_0x0712
            if (r13 == 0) goto L_0x0640
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0618 }
            r12.<init>()     // Catch:{ Exception -> 0x0618 }
            goto L_0x0645
        L_0x0618:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r51 = r60
            r12 = r62
            r49 = r4
            r25 = r5
            r35 = r7
            r52 = r15
            r11 = r18
            r69 = r19
            r45 = r22
            r26 = r23
            r3 = r31
            r4 = r0
            r19 = r1
            r22 = r2
            r23 = r6
            r1 = r9
            r2 = r13
            r9 = r20
            goto L_0x3281
        L_0x0640:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x06ea }
            r12.<init>()     // Catch:{ Exception -> 0x06ea }
        L_0x0645:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaShareContact r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaShareContact     // Catch:{ Exception -> 0x06c3 }
            r2.<init>()     // Catch:{ Exception -> 0x06c3 }
            r12.media = r2     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r4 = r1.phone     // Catch:{ Exception -> 0x06c3 }
            r2.phone_number = r4     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r4 = r1.first_name     // Catch:{ Exception -> 0x06c3 }
            r2.first_name = r4     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r4 = r1.last_name     // Catch:{ Exception -> 0x06c3 }
            r2.last_name = r4     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            int r4 = r1.id     // Catch:{ Exception -> 0x06c3 }
            r2.user_id = r4     // Catch:{ Exception -> 0x06c3 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r2 = r1.restriction_reason     // Catch:{ Exception -> 0x06c3 }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x06c3 }
            if (r2 != 0) goto L_0x0691
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r2 = r1.restriction_reason     // Catch:{ Exception -> 0x06c3 }
            r4 = 0
            java.lang.Object r2 = r2.get(r4)     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_restrictionReason) r2     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r2 = r2.text     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r4 = "BEGIN:VCARD"
            boolean r2 = r2.startsWith(r4)     // Catch:{ Exception -> 0x06c3 }
            if (r2 == 0) goto L_0x0691
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r4 = r1.restriction_reason     // Catch:{ Exception -> 0x06c3 }
            r5 = 0
            java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x06c3 }
            im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_restrictionReason) r4     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r4 = r4.text     // Catch:{ Exception -> 0x06c3 }
            r2.vcard = r4     // Catch:{ Exception -> 0x06c3 }
            r4 = r33
            goto L_0x0697
        L_0x0691:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            r4 = r33
            r2.vcard = r4     // Catch:{ Exception -> 0x06c3 }
        L_0x0697:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r2 = r2.first_name     // Catch:{ Exception -> 0x06c3 }
            if (r2 != 0) goto L_0x06a3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            r2.first_name = r4     // Catch:{ Exception -> 0x06c3 }
            r1.first_name = r4     // Catch:{ Exception -> 0x06c3 }
        L_0x06a3:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            java.lang.String r2 = r2.last_name     // Catch:{ Exception -> 0x06c3 }
            if (r2 != 0) goto L_0x06af
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x06c3 }
            r2.last_name = r4     // Catch:{ Exception -> 0x06c3 }
            r1.last_name = r4     // Catch:{ Exception -> 0x06c3 }
        L_0x06af:
            if (r15 == 0) goto L_0x06bd
            boolean r2 = r15.containsKey(r10)     // Catch:{ Exception -> 0x06c3 }
            if (r2 == 0) goto L_0x06bd
            r22 = 9
            r26 = 4
            goto L_0x0987
        L_0x06bd:
            r22 = 6
            r26 = 4
            goto L_0x0987
        L_0x06c3:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r25 = r77
            r4 = r0
            r35 = r7
            r11 = r12
            r2 = r13
            r52 = r15
            r69 = r19
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r12 = r62
            r19 = r1
            r23 = r6
            r1 = r9
            r9 = r20
            goto L_0x3281
        L_0x06ea:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r12 = r62
            r25 = r77
            r4 = r0
            r35 = r7
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r1
            r23 = r6
            r1 = r9
            r9 = r20
            goto L_0x3281
        L_0x0712:
            r4 = r33
            r2 = r62
            r5 = r77
            if (r2 == 0) goto L_0x0981
            if (r13 == 0) goto L_0x0749
            im.bclpbkiauv.tgnet.TLRPC$TL_message_secret r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0722 }
            r12.<init>()     // Catch:{ Exception -> 0x0722 }
            goto L_0x074e
        L_0x0722:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r4 = r0
            r12 = r2
            r25 = r5
            r35 = r7
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r1
            r23 = r6
            r1 = r9
            r9 = r20
            goto L_0x3281
        L_0x0749:
            im.bclpbkiauv.tgnet.TLRPC$TL_message r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0959 }
            r12.<init>()     // Catch:{ Exception -> 0x0959 }
        L_0x074e:
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x092f }
            r1.<init>()     // Catch:{ Exception -> 0x092f }
            r12.media = r1     // Catch:{ Exception -> 0x092f }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r12.media     // Catch:{ Exception -> 0x092f }
            int r6 = r1.flags     // Catch:{ Exception -> 0x092f }
            r18 = 3
            r6 = r6 | 3
            r1.flags = r6     // Catch:{ Exception -> 0x092f }
            if (r5 == 0) goto L_0x0798
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r12.media     // Catch:{ Exception -> 0x0772 }
            r1.ttl_seconds = r5     // Catch:{ Exception -> 0x0772 }
            r12.ttl = r5     // Catch:{ Exception -> 0x0772 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r12.media     // Catch:{ Exception -> 0x0772 }
            int r6 = r1.flags     // Catch:{ Exception -> 0x0772 }
            r26 = 4
            r6 = r6 | 4
            r1.flags = r6     // Catch:{ Exception -> 0x0772 }
            goto L_0x079a
        L_0x0772:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r4 = r0
            r25 = r5
            r35 = r7
            r1 = r9
            r11 = r12
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            r12 = r2
            r2 = r13
            goto L_0x3281
        L_0x0798:
            r26 = 4
        L_0x079a:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = r12.media     // Catch:{ Exception -> 0x092f }
            r1.document = r2     // Catch:{ Exception -> 0x092f }
            if (r15 == 0) goto L_0x07ab
            boolean r1 = r15.containsKey(r10)     // Catch:{ Exception -> 0x0772 }
            if (r1 == 0) goto L_0x07ab
            r1 = 9
            r22 = r1
            goto L_0x07cc
        L_0x07ab:
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isVideoDocument(r62)     // Catch:{ Exception -> 0x092f }
            if (r1 != 0) goto L_0x07c9
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoDocument(r62)     // Catch:{ Exception -> 0x0772 }
            if (r1 != 0) goto L_0x07c9
            if (r60 == 0) goto L_0x07ba
            goto L_0x07c9
        L_0x07ba:
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isVoiceDocument(r62)     // Catch:{ Exception -> 0x0772 }
            if (r1 == 0) goto L_0x07c5
            r1 = 8
            r22 = r1
            goto L_0x07cc
        L_0x07c5:
            r1 = 7
            r22 = r1
            goto L_0x07cc
        L_0x07c9:
            r1 = 3
            r22 = r1
        L_0x07cc:
            if (r60 == 0) goto L_0x07e0
            java.lang.String r1 = r60.getString()     // Catch:{ Exception -> 0x0772 }
            if (r15 != 0) goto L_0x07da
            java.util.HashMap r6 = new java.util.HashMap     // Catch:{ Exception -> 0x0772 }
            r6.<init>()     // Catch:{ Exception -> 0x0772 }
            r15 = r6
        L_0x07da:
            java.lang.String r6 = "ve"
            r15.put(r6, r1)     // Catch:{ Exception -> 0x07f7 }
        L_0x07e0:
            if (r13 == 0) goto L_0x081d
            int r1 = r2.dc_id     // Catch:{ Exception -> 0x07f7 }
            if (r1 <= 0) goto L_0x081d
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x07f7 }
            if (r1 != 0) goto L_0x081d
            java.io.File r1 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r62)     // Catch:{ Exception -> 0x07f7 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x07f7 }
            r12.attachPath = r1     // Catch:{ Exception -> 0x07f7 }
            goto L_0x081f
        L_0x07f7:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r4 = r0
            r25 = r5
            r35 = r7
            r1 = r9
            r11 = r12
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            r12 = r2
            r2 = r13
            goto L_0x3281
        L_0x081d:
            r12.attachPath = r14     // Catch:{ Exception -> 0x0905 }
        L_0x081f:
            if (r13 == 0) goto L_0x08fd
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x0905 }
            if (r1 != 0) goto L_0x0832
            boolean r1 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r62)     // Catch:{ Exception -> 0x07f7 }
            if (r1 == 0) goto L_0x082e
            goto L_0x0832
        L_0x082e:
            r18 = r12
            goto L_0x08ff
        L_0x0832:
            r1 = 0
        L_0x0833:
            java.util.ArrayList r6 = r2.attributes     // Catch:{ Exception -> 0x0905 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0905 }
            if (r1 >= r6) goto L_0x08fa
            java.util.ArrayList r6 = r2.attributes     // Catch:{ Exception -> 0x0905 }
            java.lang.Object r6 = r6.get(r1)     // Catch:{ Exception -> 0x0905 }
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r6 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r6     // Catch:{ Exception -> 0x0905 }
            boolean r5 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeSticker     // Catch:{ Exception -> 0x0905 }
            if (r5 == 0) goto L_0x08ec
            java.util.ArrayList r5 = r2.attributes     // Catch:{ Exception -> 0x0905 }
            r5.remove(r1)     // Catch:{ Exception -> 0x0905 }
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x0905 }
            r5.<init>()     // Catch:{ Exception -> 0x0905 }
            java.util.ArrayList r7 = r2.attributes     // Catch:{ Exception -> 0x0905 }
            r7.add(r5)     // Catch:{ Exception -> 0x0905 }
            java.lang.String r7 = r6.alt     // Catch:{ Exception -> 0x0905 }
            r5.alt = r7     // Catch:{ Exception -> 0x0905 }
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r7 = r6.stickerset     // Catch:{ Exception -> 0x0905 }
            if (r7 == 0) goto L_0x08ba
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r7 = r6.stickerset     // Catch:{ Exception -> 0x0905 }
            boolean r7 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0905 }
            if (r7 == 0) goto L_0x0891
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r7 = r6.stickerset     // Catch:{ Exception -> 0x086b }
            java.lang.String r7 = r7.short_name     // Catch:{ Exception -> 0x086b }
            r18 = r12
            goto L_0x08a0
        L_0x086b:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r35 = r69
            r25 = r77
            r4 = r0
            r1 = r9
            r11 = r12
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            r12 = r2
            r2 = r13
            goto L_0x3281
        L_0x0891:
            im.bclpbkiauv.messenger.MediaDataController r7 = r55.getMediaDataController()     // Catch:{ Exception -> 0x0905 }
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r2 = r6.stickerset     // Catch:{ Exception -> 0x0905 }
            r18 = r12
            long r11 = r2.id     // Catch:{ Exception -> 0x08c4 }
            java.lang.String r2 = r7.getStickerSetName(r11)     // Catch:{ Exception -> 0x08c4 }
            r7 = r2
        L_0x08a0:
            boolean r2 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x08c4 }
            if (r2 != 0) goto L_0x08b2
            im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetShortName r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x08c4 }
            r2.<init>()     // Catch:{ Exception -> 0x08c4 }
            r5.stickerset = r2     // Catch:{ Exception -> 0x08c4 }
            im.bclpbkiauv.tgnet.TLRPC$InputStickerSet r2 = r5.stickerset     // Catch:{ Exception -> 0x08c4 }
            r2.short_name = r7     // Catch:{ Exception -> 0x08c4 }
            goto L_0x08b9
        L_0x08b2:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x08c4 }
            r2.<init>()     // Catch:{ Exception -> 0x08c4 }
            r5.stickerset = r2     // Catch:{ Exception -> 0x08c4 }
        L_0x08b9:
            goto L_0x08ff
        L_0x08ba:
            r18 = r12
            im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x08c4 }
            r2.<init>()     // Catch:{ Exception -> 0x08c4 }
            r5.stickerset = r2     // Catch:{ Exception -> 0x08c4 }
            goto L_0x08ff
        L_0x08c4:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r12 = r62
            r35 = r69
            r25 = r77
            r4 = r0
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            goto L_0x3281
        L_0x08ec:
            r18 = r12
            int r1 = r1 + 1
            r2 = r62
            r11 = r63
            r7 = r69
            r5 = r77
            goto L_0x0833
        L_0x08fa:
            r18 = r12
            goto L_0x08ff
        L_0x08fd:
            r18 = r12
        L_0x08ff:
            r7 = r69
            r12 = r18
            goto L_0x0987
        L_0x0905:
            r0 = move-exception
            r18 = r12
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r12 = r62
            r35 = r69
            r25 = r77
            r4 = r0
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            goto L_0x3281
        L_0x092f:
            r0 = move-exception
            r18 = r12
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r12 = r62
            r35 = r69
            r25 = r77
            r4 = r0
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            goto L_0x3281
        L_0x0959:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r12 = r62
            r35 = r69
            r25 = r77
            r4 = r0
            r1 = r9
            r2 = r13
            r52 = r15
            r11 = r18
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r23 = r64
            goto L_0x3281
        L_0x0981:
            r26 = 4
            r7 = r69
            r12 = r18
        L_0x0987:
            if (r8 == 0) goto L_0x09bf
            boolean r1 = r72.isEmpty()     // Catch:{ Exception -> 0x0998 }
            if (r1 != 0) goto L_0x09bf
            r12.entities = r8     // Catch:{ Exception -> 0x0998 }
            int r1 = r12.flags     // Catch:{ Exception -> 0x0998 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r12.flags = r1     // Catch:{ Exception -> 0x0998 }
            goto L_0x09bf
        L_0x0998:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r25 = r77
            r4 = r0
            r35 = r7
            r1 = r9
            r11 = r12
            r2 = r13
            r52 = r15
            r69 = r19
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r19 = r61
            r12 = r62
            r23 = r64
            goto L_0x3281
        L_0x09bf:
            r11 = r19
            if (r11 == 0) goto L_0x09ed
            r12.message = r11     // Catch:{ Exception -> 0x09c6 }
            goto L_0x09f3
        L_0x09c6:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r19 = r61
            r25 = r77
            r4 = r0
            r35 = r7
            r1 = r9
            r69 = r11
            r11 = r12
            r2 = r13
            r52 = r15
            r9 = r20
            r45 = r22
            r26 = r23
            r3 = r31
            r22 = r58
            r12 = r62
            r23 = r64
            goto L_0x3281
        L_0x09ed:
            java.lang.String r1 = r12.message     // Catch:{ Exception -> 0x325c }
            if (r1 != 0) goto L_0x09f3
            r12.message = r4     // Catch:{ Exception -> 0x09c6 }
        L_0x09f3:
            java.lang.String r1 = r12.attachPath     // Catch:{ Exception -> 0x325c }
            if (r1 != 0) goto L_0x09f9
            r12.attachPath = r4     // Catch:{ Exception -> 0x09c6 }
        L_0x09f9:
            im.bclpbkiauv.messenger.UserConfig r1 = r55.getUserConfig()     // Catch:{ Exception -> 0x325c }
            int r1 = r1.getNewMessageId()     // Catch:{ Exception -> 0x325c }
            r12.id = r1     // Catch:{ Exception -> 0x325c }
            r12.local_id = r1     // Catch:{ Exception -> 0x325c }
            r1 = 1
            r12.out = r1     // Catch:{ Exception -> 0x325c }
            if (r32 == 0) goto L_0x0a3a
            if (r31 == 0) goto L_0x0a3a
            r2 = r31
            int r1 = r2.channel_id     // Catch:{ Exception -> 0x0a14 }
            int r1 = -r1
            r12.from_id = r1     // Catch:{ Exception -> 0x0a14 }
            goto L_0x0a4c
        L_0x0a14:
            r0 = move-exception
            r10 = r55
            r33 = r56
            r49 = r59
            r51 = r60
            r19 = r61
            r25 = r77
            r4 = r0
            r3 = r2
            r35 = r7
            r1 = r9
            r69 = r11
            r11 = r12
            r2 = r13
            r52 = r15
            r9 = r20
            r45 = r22
            r26 = r23
            r22 = r58
            r12 = r62
            r23 = r64
            goto L_0x3281
        L_0x0a3a:
            r2 = r31
            im.bclpbkiauv.messenger.UserConfig r1 = r55.getUserConfig()     // Catch:{ Exception -> 0x3237 }
            int r1 = r1.getClientUserId()     // Catch:{ Exception -> 0x3237 }
            r12.from_id = r1     // Catch:{ Exception -> 0x3237 }
            int r1 = r12.flags     // Catch:{ Exception -> 0x3237 }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r12.flags = r1     // Catch:{ Exception -> 0x3237 }
        L_0x0a4c:
            im.bclpbkiauv.messenger.UserConfig r1 = r55.getUserConfig()     // Catch:{ Exception -> 0x3237 }
            r5 = 0
            r1.saveConfig(r5)     // Catch:{ Exception -> 0x3237 }
            r6 = r61
            r5 = r62
            r1 = r64
            r8 = r77
            r18 = r3
            r35 = r7
            r57 = r10
            r69 = r11
            r11 = r12
            r10 = r15
            r3 = r22
            r12 = r56
            r15 = r58
            r7 = r59
        L_0x0a6e:
            r22 = r5
            r19 = r6
            long r5 = r11.random_id     // Catch:{ Exception -> 0x3208 }
            int r31 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r31 != 0) goto L_0x0a9e
            long r5 = r55.getNextRandomId()     // Catch:{ Exception -> 0x0a7f }
            r11.random_id = r5     // Catch:{ Exception -> 0x0a7f }
            goto L_0x0a9e
        L_0x0a7f:
            r0 = move-exception
            r51 = r60
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r52 = r10
            r33 = r12
            r12 = r22
            r26 = r23
            r10 = r55
            r23 = r1
            r3 = r2
            r1 = r9
            r2 = r13
            r22 = r15
            r9 = r20
            goto L_0x3281
        L_0x0a9e:
            java.lang.String r5 = "bot"
            java.lang.String r6 = "bot_name"
            if (r10 == 0) goto L_0x0ad8
            boolean r31 = r10.containsKey(r5)     // Catch:{ Exception -> 0x0a7f }
            if (r31 == 0) goto L_0x0ad8
            if (r13 == 0) goto L_0x0abf
            java.lang.Object r31 = r10.get(r6)     // Catch:{ Exception -> 0x0a7f }
            r56 = r6
            r6 = r31
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x0a7f }
            r11.via_bot_name = r6     // Catch:{ Exception -> 0x0a7f }
            java.lang.String r6 = r11.via_bot_name     // Catch:{ Exception -> 0x0a7f }
            if (r6 != 0) goto L_0x0ad1
            r11.via_bot_name = r4     // Catch:{ Exception -> 0x0a7f }
            goto L_0x0ad1
        L_0x0abf:
            r56 = r6
            java.lang.Object r6 = r10.get(r5)     // Catch:{ Exception -> 0x0a7f }
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6     // Catch:{ Exception -> 0x0a7f }
            java.lang.Integer r6 = im.bclpbkiauv.messenger.Utilities.parseInt(r6)     // Catch:{ Exception -> 0x0a7f }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x0a7f }
            r11.via_bot_id = r6     // Catch:{ Exception -> 0x0a7f }
        L_0x0ad1:
            int r6 = r11.flags     // Catch:{ Exception -> 0x0a7f }
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r11.flags = r6     // Catch:{ Exception -> 0x0a7f }
            goto L_0x0ada
        L_0x0ad8:
            r56 = r6
        L_0x0ada:
            r11.params = r10     // Catch:{ Exception -> 0x3208 }
            if (r9 == 0) goto L_0x0ae9
            boolean r6 = r9.resendAsIs     // Catch:{ Exception -> 0x0a7f }
            if (r6 != 0) goto L_0x0ae3
            goto L_0x0ae9
        L_0x0ae3:
            r6 = r76
            r58 = r5
            goto L_0x0b65
        L_0x0ae9:
            r6 = r76
            if (r6 == 0) goto L_0x0aef
            r9 = r6
            goto L_0x0af9
        L_0x0aef:
            im.bclpbkiauv.tgnet.ConnectionsManager r31 = r55.getConnectionsManager()     // Catch:{ Exception -> 0x3208 }
            int r31 = r31.getCurrentTime()     // Catch:{ Exception -> 0x3208 }
            r9 = r31
        L_0x0af9:
            r11.date = r9     // Catch:{ Exception -> 0x31ec }
            boolean r9 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputPeerChannel     // Catch:{ Exception -> 0x31ec }
            if (r9 == 0) goto L_0x0b60
            if (r6 != 0) goto L_0x0b0c
            if (r32 == 0) goto L_0x0b0c
            r9 = 1
            r11.views = r9     // Catch:{ Exception -> 0x0b40 }
            int r9 = r11.flags     // Catch:{ Exception -> 0x0b40 }
            r9 = r9 | 1024(0x400, float:1.435E-42)
            r11.flags = r9     // Catch:{ Exception -> 0x0b40 }
        L_0x0b0c:
            im.bclpbkiauv.messenger.MessagesController r9 = r55.getMessagesController()     // Catch:{ Exception -> 0x0b40 }
            r58 = r5
            int r5 = r2.channel_id     // Catch:{ Exception -> 0x0b40 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0b40 }
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r9.getChat(r5)     // Catch:{ Exception -> 0x0b40 }
            if (r5 == 0) goto L_0x0b3f
            boolean r9 = r5.megagroup     // Catch:{ Exception -> 0x0b40 }
            if (r9 == 0) goto L_0x0b2e
            int r9 = r11.flags     // Catch:{ Exception -> 0x0b40 }
            r31 = -2147483648(0xffffffff80000000, float:-0.0)
            r9 = r9 | r31
            r11.flags = r9     // Catch:{ Exception -> 0x0b40 }
            r9 = 1
            r11.unread = r9     // Catch:{ Exception -> 0x0b40 }
            goto L_0x0b3f
        L_0x0b2e:
            r9 = 1
            r11.post = r9     // Catch:{ Exception -> 0x0b40 }
            boolean r9 = r5.signatures     // Catch:{ Exception -> 0x0b40 }
            if (r9 == 0) goto L_0x0b3f
            im.bclpbkiauv.messenger.UserConfig r9 = r55.getUserConfig()     // Catch:{ Exception -> 0x0b40 }
            int r9 = r9.getClientUserId()     // Catch:{ Exception -> 0x0b40 }
            r11.from_id = r9     // Catch:{ Exception -> 0x0b40 }
        L_0x0b3f:
            goto L_0x0b65
        L_0x0b40:
            r0 = move-exception
            r51 = r60
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r52 = r10
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r10 = r55
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0b60:
            r58 = r5
            r5 = 1
            r11.unread = r5     // Catch:{ Exception -> 0x31ec }
        L_0x0b65:
            int r5 = r11.flags     // Catch:{ Exception -> 0x31ec }
            r5 = r5 | 512(0x200, float:7.175E-43)
            r11.flags = r5     // Catch:{ Exception -> 0x31ec }
            r5 = r65
            r9 = 2
            r11.dialog_id = r5     // Catch:{ Exception -> 0x31d1 }
            r6 = r68
            if (r6 == 0) goto L_0x0be0
            if (r13 == 0) goto L_0x0bb0
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r6.messageOwner     // Catch:{ Exception -> 0x0b8e }
            r64 = r10
            long r9 = r5.random_id     // Catch:{ Exception -> 0x0bc0 }
            int r5 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x0bb2
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r6.messageOwner     // Catch:{ Exception -> 0x0bc0 }
            long r9 = r5.random_id     // Catch:{ Exception -> 0x0bc0 }
            r11.reply_to_random_id = r9     // Catch:{ Exception -> 0x0bc0 }
            int r5 = r11.flags     // Catch:{ Exception -> 0x0bc0 }
            r9 = 8
            r5 = r5 | r9
            r11.flags = r5     // Catch:{ Exception -> 0x0bc0 }
            goto L_0x0bb9
        L_0x0b8e:
            r0 = move-exception
            r64 = r10
            r10 = r55
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0bb0:
            r64 = r10
        L_0x0bb2:
            int r5 = r11.flags     // Catch:{ Exception -> 0x0bc0 }
            r9 = 8
            r5 = r5 | r9
            r11.flags = r5     // Catch:{ Exception -> 0x0bc0 }
        L_0x0bb9:
            int r5 = r68.getId()     // Catch:{ Exception -> 0x0bc0 }
            r11.reply_to_msg_id = r5     // Catch:{ Exception -> 0x0bc0 }
            goto L_0x0be2
        L_0x0bc0:
            r0 = move-exception
            r10 = r55
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0be0:
            r64 = r10
        L_0x0be2:
            r10 = r73
            if (r10 == 0) goto L_0x0bf0
            if (r13 != 0) goto L_0x0bf0
            int r5 = r11.flags     // Catch:{ Exception -> 0x0bc0 }
            r5 = r5 | 64
            r11.flags = r5     // Catch:{ Exception -> 0x0bc0 }
            r11.reply_markup = r10     // Catch:{ Exception -> 0x0bc0 }
        L_0x0bf0:
            if (r28 == 0) goto L_0x0c8e
            im.bclpbkiauv.messenger.MessagesController r5 = r55.getMessagesController()     // Catch:{ Exception -> 0x0bc0 }
            r9 = r28
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.getPeer(r9)     // Catch:{ Exception -> 0x0c6c }
            r11.to_id = r5     // Catch:{ Exception -> 0x0c6c }
            if (r9 <= 0) goto L_0x0c66
            im.bclpbkiauv.messenger.MessagesController r5 = r55.getMessagesController()     // Catch:{ Exception -> 0x0c6c }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0c6c }
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r10)     // Catch:{ Exception -> 0x0c6c }
            if (r5 != 0) goto L_0x0c58
            int r4 = r11.id     // Catch:{ Exception -> 0x0c36 }
            r10 = r55
            r10.processSentMessage(r4)     // Catch:{ Exception -> 0x0c16 }
            return
        L_0x0c16:
            r0 = move-exception
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r28 = r9
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0c36:
            r0 = move-exception
            r10 = r55
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r28 = r9
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0c58:
            r10 = r55
            r28 = r9
            r9 = 4
            boolean r9 = r5.bot     // Catch:{ Exception -> 0x0caa }
            if (r9 == 0) goto L_0x0c64
            r9 = 0
            r11.unread = r9     // Catch:{ Exception -> 0x0caa }
        L_0x0c64:
            goto L_0x0d79
        L_0x0c66:
            r10 = r55
            r28 = r9
            goto L_0x0d79
        L_0x0c6c:
            r0 = move-exception
            r10 = r55
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r28 = r9
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0c8e:
            r10 = r55
            im.bclpbkiauv.tgnet.TLRPC$TL_peerUser r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x31a2 }
            r5.<init>()     // Catch:{ Exception -> 0x31a2 }
            r11.to_id = r5     // Catch:{ Exception -> 0x31a2 }
            int r5 = r13.participant_id     // Catch:{ Exception -> 0x31a2 }
            im.bclpbkiauv.messenger.UserConfig r9 = r55.getUserConfig()     // Catch:{ Exception -> 0x31a2 }
            int r9 = r9.getClientUserId()     // Catch:{ Exception -> 0x31a2 }
            if (r5 != r9) goto L_0x0cc8
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r11.to_id     // Catch:{ Exception -> 0x0caa }
            int r9 = r13.admin_id     // Catch:{ Exception -> 0x0caa }
            r5.user_id = r9     // Catch:{ Exception -> 0x0caa }
            goto L_0x0cce
        L_0x0caa:
            r0 = move-exception
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r49 = r7
            r25 = r8
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0cc8:
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r11.to_id     // Catch:{ Exception -> 0x31a2 }
            int r9 = r13.participant_id     // Catch:{ Exception -> 0x31a2 }
            r5.user_id = r9     // Catch:{ Exception -> 0x31a2 }
        L_0x0cce:
            if (r8 == 0) goto L_0x0cd3
            r11.ttl = r8     // Catch:{ Exception -> 0x0caa }
            goto L_0x0cef
        L_0x0cd3:
            int r5 = r13.ttl     // Catch:{ Exception -> 0x31a2 }
            r11.ttl = r5     // Catch:{ Exception -> 0x31a2 }
            int r5 = r11.ttl     // Catch:{ Exception -> 0x31a2 }
            if (r5 == 0) goto L_0x0cef
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            if (r5 == 0) goto L_0x0cef
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            int r9 = r11.ttl     // Catch:{ Exception -> 0x0caa }
            r5.ttl_seconds = r9     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            int r9 = r5.flags     // Catch:{ Exception -> 0x0caa }
            r26 = 4
            r9 = r9 | 4
            r5.flags = r9     // Catch:{ Exception -> 0x0caa }
        L_0x0cef:
            int r5 = r11.ttl     // Catch:{ Exception -> 0x31a2 }
            if (r5 == 0) goto L_0x0d79
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0caa }
            if (r5 == 0) goto L_0x0d79
            boolean r5 = im.bclpbkiauv.messenger.MessageObject.isVoiceMessage(r11)     // Catch:{ Exception -> 0x0caa }
            if (r5 == 0) goto L_0x0d37
            r5 = 0
            r9 = 0
        L_0x0d01:
            r59 = r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0caa }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0caa }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0caa }
            if (r9 >= r5) goto L_0x0d2a
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r11.media     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0caa }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0caa }
            java.lang.Object r5 = r5.get(r9)     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r5 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r5     // Catch:{ Exception -> 0x0caa }
            boolean r14 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeAudio     // Catch:{ Exception -> 0x0caa }
            if (r14 == 0) goto L_0x0d23
            int r14 = r5.duration     // Catch:{ Exception -> 0x0caa }
            r5 = r14
            goto L_0x0d2c
        L_0x0d23:
            int r9 = r9 + 1
            r5 = r59
            r14 = r67
            goto L_0x0d01
        L_0x0d2a:
            r5 = r59
        L_0x0d2c:
            int r9 = r11.ttl     // Catch:{ Exception -> 0x0caa }
            int r14 = r5 + 1
            int r9 = java.lang.Math.max(r9, r14)     // Catch:{ Exception -> 0x0caa }
            r11.ttl = r9     // Catch:{ Exception -> 0x0caa }
            goto L_0x0d44
        L_0x0d37:
            boolean r5 = im.bclpbkiauv.messenger.MessageObject.isVideoMessage(r11)     // Catch:{ Exception -> 0x0caa }
            if (r5 != 0) goto L_0x0d45
            boolean r5 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoMessage(r11)     // Catch:{ Exception -> 0x0caa }
            if (r5 == 0) goto L_0x0d44
            goto L_0x0d45
        L_0x0d44:
            goto L_0x0d79
        L_0x0d45:
            r5 = 0
            r9 = 0
        L_0x0d47:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r14 = r11.media     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r14.document     // Catch:{ Exception -> 0x0caa }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r14 = r14.attributes     // Catch:{ Exception -> 0x0caa }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0caa }
            if (r9 >= r14) goto L_0x0d6d
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r14 = r11.media     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$Document r14 = r14.document     // Catch:{ Exception -> 0x0caa }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r14 = r14.attributes     // Catch:{ Exception -> 0x0caa }
            java.lang.Object r14 = r14.get(r9)     // Catch:{ Exception -> 0x0caa }
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r14 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r14     // Catch:{ Exception -> 0x0caa }
            r59 = r5
            boolean r5 = r14 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x0caa }
            if (r5 == 0) goto L_0x0d68
            int r5 = r14.duration     // Catch:{ Exception -> 0x0caa }
            goto L_0x0d6f
        L_0x0d68:
            int r9 = r9 + 1
            r5 = r59
            goto L_0x0d47
        L_0x0d6d:
            r59 = r5
        L_0x0d6f:
            int r9 = r11.ttl     // Catch:{ Exception -> 0x0caa }
            int r14 = r5 + 1
            int r9 = java.lang.Math.max(r9, r14)     // Catch:{ Exception -> 0x0caa }
            r11.ttl = r9     // Catch:{ Exception -> 0x0caa }
        L_0x0d79:
            r5 = r27
            r9 = 1
            if (r5 == r9) goto L_0x0db1
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.isVoiceMessage(r11)     // Catch:{ Exception -> 0x0d91 }
            if (r9 != 0) goto L_0x0d8d
            boolean r9 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoMessage(r11)     // Catch:{ Exception -> 0x0d91 }
            if (r9 == 0) goto L_0x0d8b
            goto L_0x0d8d
        L_0x0d8b:
            r9 = 1
            goto L_0x0db1
        L_0x0d8d:
            r9 = 1
            r11.media_unread = r9     // Catch:{ Exception -> 0x0d91 }
            goto L_0x0db1
        L_0x0d91:
            r0 = move-exception
            r51 = r60
            r52 = r64
            r4 = r0
            r45 = r3
            r27 = r5
            r49 = r7
            r25 = r8
            r33 = r12
            r9 = r20
            r12 = r22
            r26 = r23
            r23 = r1
            r3 = r2
            r2 = r13
            r22 = r15
            r1 = r71
            goto L_0x3281
        L_0x0db1:
            r11.send_state = r9     // Catch:{ Exception -> 0x3171 }
            im.bclpbkiauv.messenger.MessageObject r14 = new im.bclpbkiauv.messenger.MessageObject     // Catch:{ Exception -> 0x3171 }
            int r9 = r10.currentAccount     // Catch:{ Exception -> 0x3171 }
            r27 = r5
            r5 = 1
            r14.<init>((int) r9, (im.bclpbkiauv.tgnet.TLRPC.Message) r11, (im.bclpbkiauv.messenger.MessageObject) r6, (boolean) r5)     // Catch:{ Exception -> 0x31a2 }
            r5 = r65
            r9 = r76
            if (r9 == 0) goto L_0x0dc7
            r31 = r1
            r1 = 1
            goto L_0x0dca
        L_0x0dc7:
            r31 = r1
            r1 = 0
        L_0x0dca:
            r14.scheduled = r1     // Catch:{ Exception -> 0x3144 }
            boolean r1 = r14.isForwarded()     // Catch:{ Exception -> 0x3144 }
            if (r1 != 0) goto L_0x0e28
            int r1 = r14.type     // Catch:{ Exception -> 0x0e09 }
            r59 = r8
            r8 = 3
            if (r1 == r8) goto L_0x0de0
            if (r60 != 0) goto L_0x0de0
            int r1 = r14.type     // Catch:{ Exception -> 0x0dec }
            r8 = 2
            if (r1 != r8) goto L_0x0e2a
        L_0x0de0:
            java.lang.String r1 = r11.attachPath     // Catch:{ Exception -> 0x0dec }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0dec }
            if (r1 != 0) goto L_0x0e2a
            r1 = 1
            r14.attachPathExists = r1     // Catch:{ Exception -> 0x0dec }
            goto L_0x0e2a
        L_0x0dec:
            r0 = move-exception
            r25 = r59
            r51 = r60
            r52 = r64
            r1 = r71
            r4 = r0
            r45 = r3
            r49 = r7
            r33 = r12
            r9 = r14
            r12 = r22
            r26 = r23
            r23 = r31
            r3 = r2
            r2 = r13
            r22 = r15
            goto L_0x3281
        L_0x0e09:
            r0 = move-exception
            r59 = r8
            r25 = r59
            r51 = r60
            r52 = r64
            r1 = r71
            r4 = r0
            r45 = r3
            r49 = r7
            r33 = r12
            r9 = r14
            r12 = r22
            r26 = r23
            r23 = r31
            r3 = r2
            r2 = r13
            r22 = r15
            goto L_0x3281
        L_0x0e28:
            r59 = r8
        L_0x0e2a:
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r14.videoEditedInfo     // Catch:{ Exception -> 0x3117 }
            if (r1 == 0) goto L_0x0e34
            if (r60 != 0) goto L_0x0e34
            im.bclpbkiauv.messenger.VideoEditedInfo r1 = r14.videoEditedInfo     // Catch:{ Exception -> 0x0dec }
            r8 = r1
            goto L_0x0e36
        L_0x0e34:
            r8 = r60
        L_0x0e36:
            r36 = 0
            r1 = 0
            if (r64 == 0) goto L_0x0ee0
            r60 = r1
            java.lang.String r1 = "groupId"
            r74 = r8
            r8 = r64
            java.lang.Object r1 = r8.get(r1)     // Catch:{ Exception -> 0x0e9e }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0e9e }
            if (r1 == 0) goto L_0x0e6a
            java.lang.Long r20 = im.bclpbkiauv.messenger.Utilities.parseLong(r1)     // Catch:{ Exception -> 0x0e9e }
            long r38 = r20.longValue()     // Catch:{ Exception -> 0x0e9e }
            r61 = r38
            r64 = r3
            r33 = r4
            r3 = r61
            r11.grouped_id = r3     // Catch:{ Exception -> 0x0e81 }
            r61 = r1
            int r1 = r11.flags     // Catch:{ Exception -> 0x0e81 }
            r20 = 131072(0x20000, float:1.83671E-40)
            r1 = r1 | r20
            r11.flags = r1     // Catch:{ Exception -> 0x0e81 }
            r36 = r3
            goto L_0x0e70
        L_0x0e6a:
            r61 = r1
            r64 = r3
            r33 = r4
        L_0x0e70:
            java.lang.String r1 = "final"
            java.lang.Object r1 = r8.get(r1)     // Catch:{ Exception -> 0x0e81 }
            if (r1 == 0) goto L_0x0e7a
            r1 = 1
            goto L_0x0e7b
        L_0x0e7a:
            r1 = 0
        L_0x0e7b:
            r20 = r1
            r3 = r36
            goto L_0x0eee
        L_0x0e81:
            r0 = move-exception
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r4 = r0
            r3 = r2
            r49 = r7
            r52 = r8
            r33 = r12
            r2 = r13
            r9 = r14
            r12 = r22
            r26 = r23
            r23 = r31
            r22 = r15
            goto L_0x3281
        L_0x0e9e:
            r0 = move-exception
            r64 = r3
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r4 = r0
            r3 = r2
            r49 = r7
            r52 = r8
            r33 = r12
            r2 = r13
            r9 = r14
            r12 = r22
            r26 = r23
            r23 = r31
            r22 = r15
            goto L_0x3281
        L_0x0ebd:
            r0 = move-exception
            r74 = r8
            r8 = r64
            r64 = r3
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r4 = r0
            r3 = r2
            r49 = r7
            r52 = r8
            r33 = r12
            r2 = r13
            r9 = r14
            r12 = r22
            r26 = r23
            r23 = r31
            r22 = r15
            goto L_0x3281
        L_0x0ee0:
            r60 = r1
            r33 = r4
            r74 = r8
            r8 = r64
            r64 = r3
            r20 = r60
            r3 = r36
        L_0x0eee:
            int r1 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x0fb7
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0f93 }
            r1.<init>()     // Catch:{ Exception -> 0x0f93 }
            r1.add(r14)     // Catch:{ Exception -> 0x0f93 }
            java.util.ArrayList r25 = new java.util.ArrayList     // Catch:{ Exception -> 0x0f93 }
            r25.<init>()     // Catch:{ Exception -> 0x0f93 }
            r60 = r25
            r77 = r8
            r8 = r60
            r8.add(r11)     // Catch:{ Exception -> 0x0f71 }
            r43 = r14
            int r14 = r10.currentAccount     // Catch:{ Exception -> 0x0f51 }
            im.bclpbkiauv.messenger.MessagesStorage r36 = im.bclpbkiauv.messenger.MessagesStorage.getInstance(r14)     // Catch:{ Exception -> 0x0f51 }
            r38 = 0
            r39 = 1
            r40 = 0
            r41 = 0
            if (r9 == 0) goto L_0x0f1d
            r42 = 1
            goto L_0x0f1f
        L_0x0f1d:
            r42 = 0
        L_0x0f1f:
            r37 = r8
            r36.putMessages((java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.Message>) r37, (boolean) r38, (boolean) r39, (boolean) r40, (int) r41, (boolean) r42)     // Catch:{ Exception -> 0x0f51 }
            int r14 = r10.currentAccount     // Catch:{ Exception -> 0x0f51 }
            im.bclpbkiauv.messenger.MessagesController r14 = im.bclpbkiauv.messenger.MessagesController.getInstance(r14)     // Catch:{ Exception -> 0x0f51 }
            if (r9 == 0) goto L_0x0f30
            r25 = r8
            r8 = 1
            goto L_0x0f33
        L_0x0f30:
            r25 = r8
            r8 = 0
        L_0x0f33:
            r14.updateInterfaceWithMessages(r5, r1, r8)     // Catch:{ Exception -> 0x0f51 }
            if (r9 != 0) goto L_0x0f4b
            int r8 = r10.currentAccount     // Catch:{ Exception -> 0x0f51 }
            im.bclpbkiauv.messenger.NotificationCenter r8 = im.bclpbkiauv.messenger.NotificationCenter.getInstance(r8)     // Catch:{ Exception -> 0x0f51 }
            int r14 = im.bclpbkiauv.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0f51 }
            r60 = r1
            r36 = r7
            r1 = 0
            java.lang.Object[] r7 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x0fe2 }
            r8.postNotificationName(r14, r7)     // Catch:{ Exception -> 0x0fe2 }
            goto L_0x0f4f
        L_0x0f4b:
            r60 = r1
            r36 = r7
        L_0x0f4f:
            goto L_0x1048
        L_0x0f51:
            r0 = move-exception
            r36 = r7
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x0f71:
            r0 = move-exception
            r36 = r7
            r43 = r14
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x0f93:
            r0 = move-exception
            r36 = r7
            r77 = r8
            r43 = r14
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x0fb7:
            r36 = r7
            r77 = r8
            r43 = r14
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x30e9 }
            r1.<init>()     // Catch:{ Exception -> 0x30e9 }
            java.lang.String r7 = "group_"
            r1.append(r7)     // Catch:{ Exception -> 0x30e9 }
            r1.append(r3)     // Catch:{ Exception -> 0x30e9 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x30e9 }
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage>> r7 = r10.delayedMessages     // Catch:{ Exception -> 0x30e9 }
            java.lang.Object r7 = r7.get(r1)     // Catch:{ Exception -> 0x30e9 }
            java.util.ArrayList r7 = (java.util.ArrayList) r7     // Catch:{ Exception -> 0x30e9 }
            if (r7 == 0) goto L_0x1000
            r8 = 0
            java.lang.Object r14 = r7.get(r8)     // Catch:{ Exception -> 0x0fe2 }
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r14 = (im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage) r14     // Catch:{ Exception -> 0x0fe2 }
            r21 = r14
            goto L_0x1000
        L_0x0fe2:
            r0 = move-exception
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x1000:
            if (r21 != 0) goto L_0x1034
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r8 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0fe2 }
            r8.<init>(r5)     // Catch:{ Exception -> 0x0fe2 }
            r8.initForGroup(r3)     // Catch:{ Exception -> 0x1014 }
            r8.encryptedChat = r13     // Catch:{ Exception -> 0x1014 }
            if (r9 == 0) goto L_0x1010
            r14 = 1
            goto L_0x1011
        L_0x1010:
            r14 = 0
        L_0x1011:
            r8.scheduled = r14     // Catch:{ Exception -> 0x1014 }
            goto L_0x1036
        L_0x1014:
            r0 = move-exception
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r21 = r8
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x1034:
            r8 = r21
        L_0x1036:
            r14 = 0
            r8.performMediaUpload = r14     // Catch:{ Exception -> 0x30b9 }
            r14 = 0
            r8.photoSize = r14     // Catch:{ Exception -> 0x30b9 }
            r8.videoEditedInfo = r14     // Catch:{ Exception -> 0x30b9 }
            r8.httpLocation = r14     // Catch:{ Exception -> 0x30b9 }
            if (r20 == 0) goto L_0x1046
            int r14 = r11.id     // Catch:{ Exception -> 0x1014 }
            r8.finalGroupMessage = r14     // Catch:{ Exception -> 0x1014 }
        L_0x1046:
            r21 = r8
        L_0x1048:
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x30e9 }
            if (r1 == 0) goto L_0x1082
            if (r2 == 0) goto L_0x1082
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fe2 }
            r1.<init>()     // Catch:{ Exception -> 0x0fe2 }
            java.lang.String r7 = "send message user_id = "
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            int r7 = r2.user_id     // Catch:{ Exception -> 0x0fe2 }
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            java.lang.String r7 = " chat_id = "
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            int r7 = r2.chat_id     // Catch:{ Exception -> 0x0fe2 }
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            java.lang.String r7 = " channel_id = "
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            int r7 = r2.channel_id     // Catch:{ Exception -> 0x0fe2 }
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            java.lang.String r7 = " access_hash = "
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            long r7 = r2.access_hash     // Catch:{ Exception -> 0x0fe2 }
            r1.append(r7)     // Catch:{ Exception -> 0x0fe2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0fe2 }
            im.bclpbkiauv.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0fe2 }
        L_0x1082:
            r1 = 0
            java.lang.String r7 = "silent_"
            if (r64 == 0) goto L_0x2eb3
            r8 = r64
            r14 = 9
            if (r8 != r14) goto L_0x10b9
            if (r12 == 0) goto L_0x10b9
            if (r13 == 0) goto L_0x10b9
            r25 = r59
            r51 = r74
            r64 = r1
            r46 = r3
            r3 = r7
            r45 = r8
            r4 = r9
            r33 = r12
            r37 = r13
            r48 = r22
            r26 = r23
            r23 = r31
            r9 = r43
            r8 = r56
            r1 = r71
            r7 = r2
            r22 = r15
            r14 = r5
            r6 = r36
            r5 = r77
            r36 = r11
            goto L_0x2ed9
        L_0x10b9:
            r14 = 1
            if (r8 < r14) goto L_0x10bf
            r14 = 3
            if (r8 <= r14) goto L_0x10d0
        L_0x10bf:
            r14 = 5
            if (r8 < r14) goto L_0x10c6
            r14 = 8
            if (r8 <= r14) goto L_0x10d0
        L_0x10c6:
            r14 = 9
            if (r8 != r14) goto L_0x10cc
            if (r13 != 0) goto L_0x10d0
        L_0x10cc:
            r14 = 10
            if (r8 != r14) goto L_0x2c4b
        L_0x10d0:
            if (r13 != 0) goto L_0x20e2
            r25 = 0
            r14 = 1
            if (r8 != r14) goto L_0x11cd
            boolean r14 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue     // Catch:{ Exception -> 0x11af }
            if (r14 == 0) goto L_0x1116
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaVenue r14 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x10f8 }
            r14.<init>()     // Catch:{ Exception -> 0x10f8 }
            r64 = r1
            java.lang.String r1 = r15.address     // Catch:{ Exception -> 0x10f8 }
            r14.address = r1     // Catch:{ Exception -> 0x10f8 }
            java.lang.String r1 = r15.title     // Catch:{ Exception -> 0x10f8 }
            r14.title = r1     // Catch:{ Exception -> 0x10f8 }
            java.lang.String r1 = r15.provider     // Catch:{ Exception -> 0x10f8 }
            r14.provider = r1     // Catch:{ Exception -> 0x10f8 }
            java.lang.String r1 = r15.venue_id     // Catch:{ Exception -> 0x10f8 }
            r14.venue_id = r1     // Catch:{ Exception -> 0x10f8 }
            r1 = r33
            r14.venue_type = r1     // Catch:{ Exception -> 0x10f8 }
            r1 = r14
            goto L_0x113b
        L_0x10f8:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r33 = r12
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x1116:
            r64 = r1
            boolean r1 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive     // Catch:{ Exception -> 0x11af }
            if (r1 == 0) goto L_0x112e
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaGeoLive r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x10f8 }
            r1.<init>()     // Catch:{ Exception -> 0x10f8 }
            int r14 = r15.period     // Catch:{ Exception -> 0x10f8 }
            r1.period = r14     // Catch:{ Exception -> 0x10f8 }
            int r14 = r1.flags     // Catch:{ Exception -> 0x10f8 }
            r18 = 2
            r14 = r14 | 2
            r1.flags = r14     // Catch:{ Exception -> 0x10f8 }
            goto L_0x113b
        L_0x112e:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaGeoPoint r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x11af }
            r1.<init>()     // Catch:{ Exception -> 0x11af }
            java.lang.String r14 = r15.address     // Catch:{ Exception -> 0x11af }
            r1.address = r14     // Catch:{ Exception -> 0x11af }
            java.lang.String r14 = r15.title     // Catch:{ Exception -> 0x11af }
            r1.title = r14     // Catch:{ Exception -> 0x11af }
        L_0x113b:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputGeoPoint r14 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x11af }
            r14.<init>()     // Catch:{ Exception -> 0x11af }
            r1.geo_point = r14     // Catch:{ Exception -> 0x11af }
            im.bclpbkiauv.tgnet.TLRPC$InputGeoPoint r14 = r1.geo_point     // Catch:{ Exception -> 0x11af }
            r33 = r12
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r12 = r15.geo     // Catch:{ Exception -> 0x1193 }
            r37 = r13
            double r12 = r12.lat     // Catch:{ Exception -> 0x1176 }
            r14.lat = r12     // Catch:{ Exception -> 0x1176 }
            im.bclpbkiauv.tgnet.TLRPC$InputGeoPoint r12 = r1.geo_point     // Catch:{ Exception -> 0x1176 }
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r13 = r15.geo     // Catch:{ Exception -> 0x1176 }
            double r13 = r13._long     // Catch:{ Exception -> 0x1176 }
            r12._long = r13     // Catch:{ Exception -> 0x1176 }
            r13 = r67
            r38 = r2
            r39 = r3
            r61 = r7
            r12 = r21
            r24 = r22
            r4 = r23
            r7 = r36
            r14 = r43
            r23 = r8
            r8 = r9
            r36 = r11
            r22 = r15
            r11 = r64
            r15 = r78
            r9 = r1
            goto L_0x1afc
        L_0x1176:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x1193:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x11af:
            r0 = move-exception
            r33 = r12
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r2 = r13
            r12 = r22
            r26 = r23
            r23 = r31
            r49 = r36
            r9 = r43
            r22 = r15
            goto L_0x3281
        L_0x11cd:
            r64 = r1
            r37 = r13
            r1 = r33
            r33 = r12
            r12 = 2
            if (r8 == r12) goto L_0x1901
            r12 = 9
            if (r8 != r12) goto L_0x11f5
            if (r36 == 0) goto L_0x11f5
            r13 = r67
            r38 = r2
            r39 = r3
            r61 = r7
            r12 = r22
            r4 = r23
            r14 = r43
            r23 = r8
            r8 = r9
            r22 = r15
            r15 = r78
            goto L_0x1916
        L_0x11f5:
            r12 = 3
            if (r8 != r12) goto L_0x1441
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x141f }
            r1.<init>()     // Catch:{ Exception -> 0x141f }
            r12 = r22
            java.lang.String r13 = r12.mime_type     // Catch:{ Exception -> 0x13ff }
            r1.mime_type = r13     // Catch:{ Exception -> 0x13ff }
            java.util.ArrayList r13 = r12.attributes     // Catch:{ Exception -> 0x13ff }
            r1.attributes = r13     // Catch:{ Exception -> 0x13ff }
            boolean r13 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoDocument(r12)     // Catch:{ Exception -> 0x13ff }
            if (r13 != 0) goto L_0x1244
            if (r74 == 0) goto L_0x121a
            r13 = r74
            boolean r14 = r13.muted     // Catch:{ Exception -> 0x1229 }
            if (r14 != 0) goto L_0x1246
            boolean r14 = r13.roundVideo     // Catch:{ Exception -> 0x1229 }
            if (r14 != 0) goto L_0x1246
            goto L_0x121c
        L_0x121a:
            r13 = r74
        L_0x121c:
            r14 = 1
            r1.nosound_video = r14     // Catch:{ Exception -> 0x1229 }
            boolean r14 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x1229 }
            if (r14 == 0) goto L_0x1246
            java.lang.String r14 = "nosound_video = true"
            im.bclpbkiauv.messenger.FileLog.d(r14)     // Catch:{ Exception -> 0x1229 }
            goto L_0x1246
        L_0x1229:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r51 = r13
            r22 = r15
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r9 = r43
            goto L_0x3281
        L_0x1244:
            r13 = r74
        L_0x1246:
            if (r59 == 0) goto L_0x128d
            r14 = r59
            r1.ttl_seconds = r14     // Catch:{ Exception -> 0x1272 }
            r11.ttl = r14     // Catch:{ Exception -> 0x1272 }
            r22 = r15
            int r15 = r1.flags     // Catch:{ Exception -> 0x1259 }
            r18 = 2
            r15 = r15 | 2
            r1.flags = r15     // Catch:{ Exception -> 0x1259 }
            goto L_0x1291
        L_0x1259:
            r0 = move-exception
            r1 = r71
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r51 = r13
            r25 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r9 = r43
            goto L_0x3281
        L_0x1272:
            r0 = move-exception
            r22 = r15
            r1 = r71
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r51 = r13
            r25 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r9 = r43
            goto L_0x3281
        L_0x128d:
            r14 = r59
            r22 = r15
        L_0x1291:
            r38 = r2
            r39 = r3
            long r2 = r12.access_hash     // Catch:{ Exception -> 0x13e0 }
            int r4 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x12a2
            r2 = r1
            r3 = 1
            r25 = r2
            r59 = r14
            goto L_0x12ef
        L_0x12a2:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x13e0 }
            r2.<init>()     // Catch:{ Exception -> 0x13e0 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x13e0 }
            r3.<init>()     // Catch:{ Exception -> 0x13e0 }
            r2.id = r3     // Catch:{ Exception -> 0x13e0 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x13e0 }
            r59 = r14
            long r14 = r12.id     // Catch:{ Exception -> 0x136b }
            r3.id = r14     // Catch:{ Exception -> 0x136b }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x136b }
            long r14 = r12.access_hash     // Catch:{ Exception -> 0x136b }
            r3.access_hash = r14     // Catch:{ Exception -> 0x136b }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x136b }
            byte[] r4 = r12.file_reference     // Catch:{ Exception -> 0x136b }
            r3.file_reference = r4     // Catch:{ Exception -> 0x136b }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x136b }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x136b }
            if (r3 != 0) goto L_0x12ea
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x12d0 }
            r4 = 0
            byte[] r14 = new byte[r4]     // Catch:{ Exception -> 0x12d0 }
            r3.file_reference = r14     // Catch:{ Exception -> 0x12d0 }
            goto L_0x12ea
        L_0x12d0:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r45 = r8
            r51 = r13
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r9 = r43
            goto L_0x3281
        L_0x12ea:
            r3 = r2
            r25 = r3
            r3 = r64
        L_0x12ef:
            if (r21 != 0) goto L_0x1388
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r2 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x136b }
            r2.<init>(r5)     // Catch:{ Exception -> 0x136b }
            r4 = 1
            r2.type = r4     // Catch:{ Exception -> 0x134c }
            r14 = r43
            r2.obj = r14     // Catch:{ Exception -> 0x132f }
            r4 = r23
            r2.originalPath = r4     // Catch:{ Exception -> 0x1312 }
            r15 = r78
            r2.parentObject = r15     // Catch:{ Exception -> 0x13c5 }
            if (r9 == 0) goto L_0x130b
            r61 = r7
            r7 = 1
            goto L_0x130e
        L_0x130b:
            r61 = r7
            r7 = 0
        L_0x130e:
            r2.scheduled = r7     // Catch:{ Exception -> 0x13c5 }
            goto L_0x1392
        L_0x1312:
            r0 = move-exception
            r15 = r78
            r25 = r59
            r1 = r71
            r52 = r77
            r21 = r2
            r26 = r4
            r45 = r8
            r51 = r13
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x132f:
            r0 = move-exception
            r15 = r78
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r21 = r2
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            goto L_0x3281
        L_0x134c:
            r0 = move-exception
            r15 = r78
            r14 = r43
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r21 = r2
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            goto L_0x3281
        L_0x136b:
            r0 = move-exception
            r15 = r78
            r14 = r43
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            goto L_0x3281
        L_0x1388:
            r15 = r78
            r61 = r7
            r4 = r23
            r14 = r43
            r2 = r21
        L_0x1392:
            r2.inputUploadMedia = r1     // Catch:{ Exception -> 0x13c5 }
            r2.performMediaUpload = r3     // Catch:{ Exception -> 0x13c5 }
            java.util.ArrayList r7 = r12.thumbs     // Catch:{ Exception -> 0x13c5 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x13c5 }
            if (r7 != 0) goto L_0x13ae
            java.util.ArrayList r7 = r12.thumbs     // Catch:{ Exception -> 0x13c5 }
            r56 = r1
            r1 = 0
            java.lang.Object r7 = r7.get(r1)     // Catch:{ Exception -> 0x13c5 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r7     // Catch:{ Exception -> 0x13c5 }
            r2.photoSize = r7     // Catch:{ Exception -> 0x13c5 }
            r2.locationParent = r12     // Catch:{ Exception -> 0x13c5 }
            goto L_0x13b0
        L_0x13ae:
            r56 = r1
        L_0x13b0:
            r2.videoEditedInfo = r13     // Catch:{ Exception -> 0x13c5 }
            r23 = r8
            r8 = r9
            r24 = r12
            r74 = r13
            r9 = r25
            r7 = r36
            r13 = r67
            r12 = r2
            r36 = r11
            r11 = r3
            goto L_0x1afc
        L_0x13c5:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r52 = r77
            r21 = r2
            r26 = r4
            r45 = r8
            r51 = r13
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x13e0:
            r0 = move-exception
            r15 = r78
            r59 = r14
            r14 = r43
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            goto L_0x3281
        L_0x13ff:
            r0 = move-exception
            r13 = r74
            r22 = r15
            r14 = r43
            r15 = r78
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            goto L_0x3281
        L_0x141f:
            r0 = move-exception
            r13 = r74
            r12 = r22
            r14 = r43
            r22 = r15
            r15 = r78
            r25 = r59
            r1 = r71
            r52 = r77
            r4 = r0
            r3 = r2
            r45 = r8
            r51 = r13
            r9 = r14
            r26 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            goto L_0x3281
        L_0x1441:
            r13 = r74
            r38 = r2
            r39 = r3
            r61 = r7
            r12 = r22
            r4 = r23
            r14 = r43
            r22 = r15
            r15 = r78
            r2 = 6
            if (r8 != r2) goto L_0x1506
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaShareContact r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaShareContact     // Catch:{ Exception -> 0x14e9 }
            r2.<init>()     // Catch:{ Exception -> 0x14e9 }
            r7 = r19
            java.lang.String r3 = r7.phone     // Catch:{ Exception -> 0x14cc }
            r2.phone_number = r3     // Catch:{ Exception -> 0x14cc }
            java.lang.String r3 = r7.first_name     // Catch:{ Exception -> 0x14cc }
            r2.first_name = r3     // Catch:{ Exception -> 0x14cc }
            java.lang.String r3 = r7.last_name     // Catch:{ Exception -> 0x14cc }
            r2.last_name = r3     // Catch:{ Exception -> 0x14cc }
            r3 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaShareContact r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaShareContact) r3     // Catch:{ Exception -> 0x14cc }
            r74 = r13
            int r13 = r7.id     // Catch:{ Exception -> 0x14b1 }
            r3.user_id = r13     // Catch:{ Exception -> 0x14b1 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x14b1 }
            boolean r3 = r3.isEmpty()     // Catch:{ Exception -> 0x14b1 }
            if (r3 != 0) goto L_0x149b
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x14b1 }
            r13 = 0
            java.lang.Object r3 = r3.get(r13)     // Catch:{ Exception -> 0x14b1 }
            im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_restrictionReason) r3     // Catch:{ Exception -> 0x14b1 }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x14b1 }
            java.lang.String r13 = "BEGIN:VCARD"
            boolean r3 = r3.startsWith(r13)     // Catch:{ Exception -> 0x14b1 }
            if (r3 == 0) goto L_0x149b
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason> r1 = r7.restriction_reason     // Catch:{ Exception -> 0x14b1 }
            r3 = 0
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x14b1 }
            im.bclpbkiauv.tgnet.TLRPC$TL_restrictionReason r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_restrictionReason) r1     // Catch:{ Exception -> 0x14b1 }
            java.lang.String r1 = r1.text     // Catch:{ Exception -> 0x14b1 }
            r2.vcard = r1     // Catch:{ Exception -> 0x14b1 }
            goto L_0x149d
        L_0x149b:
            r2.vcard = r1     // Catch:{ Exception -> 0x14b1 }
        L_0x149d:
            r13 = r67
            r19 = r7
            r23 = r8
            r8 = r9
            r24 = r12
            r12 = r21
            r7 = r36
            r9 = r2
            r36 = r11
            r11 = r64
            goto L_0x1afc
        L_0x14b1:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r19 = r7
            r45 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x14cc:
            r0 = move-exception
            r74 = r13
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r19 = r7
            r45 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x14e9:
            r0 = move-exception
            r74 = r13
            r7 = r19
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r45 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1506:
            r74 = r13
            r7 = r19
            r1 = 7
            if (r8 == r1) goto L_0x166a
            r1 = 9
            if (r8 != r1) goto L_0x1518
            r19 = r7
            r3 = r8
            r2 = r31
            goto L_0x166f
        L_0x1518:
            r1 = 8
            if (r8 != r1) goto L_0x1610
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x15f4 }
            r1.<init>()     // Catch:{ Exception -> 0x15f4 }
            java.lang.String r2 = r12.mime_type     // Catch:{ Exception -> 0x15f4 }
            r1.mime_type = r2     // Catch:{ Exception -> 0x15f4 }
            java.util.ArrayList r2 = r12.attributes     // Catch:{ Exception -> 0x15f4 }
            r1.attributes = r2     // Catch:{ Exception -> 0x15f4 }
            if (r59 == 0) goto L_0x1553
            r2 = r59
            r1.ttl_seconds = r2     // Catch:{ Exception -> 0x1538 }
            r11.ttl = r2     // Catch:{ Exception -> 0x1538 }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1538 }
            r13 = 2
            r3 = r3 | r13
            r1.flags = r3     // Catch:{ Exception -> 0x1538 }
            goto L_0x1555
        L_0x1538:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r25 = r2
            r26 = r4
            r19 = r7
            r45 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1553:
            r2 = r59
        L_0x1555:
            r59 = r2
            long r2 = r12.access_hash     // Catch:{ Exception -> 0x15f4 }
            int r13 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r13 != 0) goto L_0x1565
            r2 = r1
            r3 = 1
            r25 = r2
            r19 = r7
            r13 = r8
            goto L_0x1598
        L_0x1565:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x15f4 }
            r2.<init>()     // Catch:{ Exception -> 0x15f4 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x15f4 }
            r3.<init>()     // Catch:{ Exception -> 0x15f4 }
            r2.id = r3     // Catch:{ Exception -> 0x15f4 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x15f4 }
            r19 = r7
            r13 = r8
            long r7 = r12.id     // Catch:{ Exception -> 0x15db }
            r3.id = r7     // Catch:{ Exception -> 0x15db }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x15db }
            long r7 = r12.access_hash     // Catch:{ Exception -> 0x15db }
            r3.access_hash = r7     // Catch:{ Exception -> 0x15db }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x15db }
            byte[] r7 = r12.file_reference     // Catch:{ Exception -> 0x15db }
            r3.file_reference = r7     // Catch:{ Exception -> 0x15db }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x15db }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x15db }
            if (r3 != 0) goto L_0x1593
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x15db }
            r7 = 0
            byte[] r8 = new byte[r7]     // Catch:{ Exception -> 0x15db }
            r3.file_reference = r8     // Catch:{ Exception -> 0x15db }
        L_0x1593:
            r3 = r2
            r25 = r3
            r3 = r64
        L_0x1598:
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r2 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x15db }
            r2.<init>(r5)     // Catch:{ Exception -> 0x15db }
            r7 = 3
            r2.type = r7     // Catch:{ Exception -> 0x15c0 }
            r2.obj = r14     // Catch:{ Exception -> 0x15c0 }
            r2.parentObject = r15     // Catch:{ Exception -> 0x15c0 }
            r2.inputUploadMedia = r1     // Catch:{ Exception -> 0x15c0 }
            r2.performMediaUpload = r3     // Catch:{ Exception -> 0x15c0 }
            if (r9 == 0) goto L_0x15ac
            r7 = 1
            goto L_0x15ad
        L_0x15ac:
            r7 = 0
        L_0x15ad:
            r2.scheduled = r7     // Catch:{ Exception -> 0x15c0 }
            r8 = r9
            r24 = r12
            r23 = r13
            r9 = r25
            r7 = r36
            r13 = r67
            r12 = r2
            r36 = r11
            r11 = r3
            goto L_0x1afc
        L_0x15c0:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r21 = r2
            r26 = r4
            r45 = r13
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x15db:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r45 = r13
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x15f4:
            r0 = move-exception
            r19 = r7
            r13 = r8
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r45 = r13
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1610:
            r19 = r7
            r13 = r8
            r3 = r13
            r1 = 10
            if (r3 != r1) goto L_0x1655
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPoll r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x163a }
            r1.<init>()     // Catch:{ Exception -> 0x163a }
            r2 = r31
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r7 = r2.poll     // Catch:{ Exception -> 0x1688 }
            r1.poll = r7     // Catch:{ Exception -> 0x1688 }
            r25 = r1
            r13 = r67
            r31 = r2
            r23 = r3
            r8 = r9
            r24 = r12
            r12 = r21
            r9 = r25
            r7 = r36
            r36 = r11
            r11 = r64
            goto L_0x1afc
        L_0x163a:
            r0 = move-exception
            r2 = r31
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r23 = r2
            r45 = r3
            r26 = r4
            r9 = r14
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1655:
            r2 = r31
            r13 = r67
            r23 = r3
            r8 = r9
            r24 = r12
            r12 = r21
            r9 = r25
            r7 = r36
            r36 = r11
            r11 = r64
            goto L_0x1afc
        L_0x166a:
            r19 = r7
            r3 = r8
            r2 = r31
        L_0x166f:
            r1 = 0
            if (r4 != 0) goto L_0x16a1
            r13 = r67
            if (r13 != 0) goto L_0x16a3
            long r7 = r12.access_hash     // Catch:{ Exception -> 0x1688 }
            int r18 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x167d
            goto L_0x16a3
        L_0x167d:
            r7 = 0
            r8 = r59
            r31 = r2
            r23 = r3
            r3 = r77
            goto L_0x177b
        L_0x1688:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r23 = r2
            r45 = r3
            r26 = r4
            r9 = r14
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x16a1:
            r13 = r67
        L_0x16a3:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x18e3 }
            r7.<init>()     // Catch:{ Exception -> 0x18e3 }
            if (r59 == 0) goto L_0x16ef
            r8 = r59
            r7.ttl_seconds = r8     // Catch:{ Exception -> 0x16d4 }
            r11.ttl = r8     // Catch:{ Exception -> 0x16d4 }
            r31 = r2
            int r2 = r7.flags     // Catch:{ Exception -> 0x16bb }
            r18 = 2
            r2 = r2 | 2
            r7.flags = r2     // Catch:{ Exception -> 0x16bb }
            goto L_0x16f3
        L_0x16bb:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r45 = r3
            r26 = r4
            r25 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x16d4:
            r0 = move-exception
            r31 = r2
            r1 = r71
            r51 = r74
            r52 = r77
            r45 = r3
            r26 = r4
            r25 = r8
            r9 = r14
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x16ef:
            r8 = r59
            r31 = r2
        L_0x16f3:
            boolean r2 = android.text.TextUtils.isEmpty(r67)     // Catch:{ Exception -> 0x18c5 }
            if (r2 != 0) goto L_0x176f
            java.lang.String r2 = r67.toLowerCase()     // Catch:{ Exception -> 0x1752 }
            r23 = r3
            java.lang.String r3 = "mp4"
            boolean r2 = r2.endsWith(r3)     // Catch:{ Exception -> 0x1737 }
            if (r2 == 0) goto L_0x1734
            if (r77 == 0) goto L_0x1714
            java.lang.String r2 = "forceDocument"
            r3 = r77
            boolean r2 = r3.containsKey(r2)     // Catch:{ Exception -> 0x171b }
            if (r2 == 0) goto L_0x1773
            goto L_0x1716
        L_0x1714:
            r3 = r77
        L_0x1716:
            r2 = 1
            r7.nosound_video = r2     // Catch:{ Exception -> 0x171b }
            goto L_0x1773
        L_0x171b:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r3
            r26 = r4
            r25 = r8
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1734:
            r3 = r77
            goto L_0x1773
        L_0x1737:
            r0 = move-exception
            r3 = r77
            r1 = r71
            r51 = r74
            r52 = r3
            r26 = r4
            r25 = r8
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1752:
            r0 = move-exception
            r23 = r3
            r3 = r77
            r1 = r71
            r51 = r74
            r52 = r3
            r26 = r4
            r25 = r8
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x176f:
            r23 = r3
            r3 = r77
        L_0x1773:
            java.lang.String r2 = r12.mime_type     // Catch:{ Exception -> 0x18a7 }
            r7.mime_type = r2     // Catch:{ Exception -> 0x18a7 }
            java.util.ArrayList r2 = r12.attributes     // Catch:{ Exception -> 0x18a7 }
            r7.attributes = r2     // Catch:{ Exception -> 0x18a7 }
        L_0x177b:
            r77 = r3
            long r2 = r12.access_hash     // Catch:{ Exception -> 0x188b }
            int r18 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x17a5
            r2 = r7
            boolean r3 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x178c }
            r25 = r2
            r59 = r8
            goto L_0x17f1
        L_0x178c:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r25 = r8
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x17a5:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x188b }
            r2.<init>()     // Catch:{ Exception -> 0x188b }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x188b }
            r3.<init>()     // Catch:{ Exception -> 0x188b }
            r2.id = r3     // Catch:{ Exception -> 0x188b }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x188b }
            r59 = r8
            long r8 = r12.id     // Catch:{ Exception -> 0x1861 }
            r3.id = r8     // Catch:{ Exception -> 0x1861 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x1861 }
            long r8 = r12.access_hash     // Catch:{ Exception -> 0x1861 }
            r3.access_hash = r8     // Catch:{ Exception -> 0x1861 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x1861 }
            byte[] r8 = r12.file_reference     // Catch:{ Exception -> 0x1861 }
            r3.file_reference = r8     // Catch:{ Exception -> 0x1861 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x1861 }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x1861 }
            if (r3 != 0) goto L_0x17ec
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x17d3 }
            r8 = 0
            byte[] r9 = new byte[r8]     // Catch:{ Exception -> 0x17d3 }
            r3.file_reference = r9     // Catch:{ Exception -> 0x17d3 }
            goto L_0x17ec
        L_0x17d3:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x17ec:
            r3 = r2
            r25 = r3
            r3 = r64
        L_0x17f1:
            if (r1 != 0) goto L_0x187c
            if (r7 == 0) goto L_0x187c
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r2 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1861 }
            r2.<init>(r5)     // Catch:{ Exception -> 0x1861 }
            r2.originalPath = r4     // Catch:{ Exception -> 0x1844 }
            r8 = 2
            r2.type = r8     // Catch:{ Exception -> 0x1844 }
            r2.obj = r14     // Catch:{ Exception -> 0x1844 }
            java.util.ArrayList r8 = r12.thumbs     // Catch:{ Exception -> 0x1844 }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x1844 }
            if (r8 != 0) goto L_0x1832
            java.util.ArrayList r8 = r12.thumbs     // Catch:{ Exception -> 0x1817 }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x1817 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r8 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r8     // Catch:{ Exception -> 0x1817 }
            r2.photoSize = r8     // Catch:{ Exception -> 0x1817 }
            r2.locationParent = r12     // Catch:{ Exception -> 0x1817 }
            goto L_0x1832
        L_0x1817:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r21 = r2
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1832:
            r2.parentObject = r15     // Catch:{ Exception -> 0x1844 }
            r2.inputUploadMedia = r7     // Catch:{ Exception -> 0x1844 }
            r2.performMediaUpload = r3     // Catch:{ Exception -> 0x1844 }
            r8 = r76
            if (r8 == 0) goto L_0x183e
            r9 = 1
            goto L_0x183f
        L_0x183e:
            r9 = 0
        L_0x183f:
            r2.scheduled = r9     // Catch:{ Exception -> 0x1817 }
            r21 = r2
            goto L_0x187e
        L_0x1844:
            r0 = move-exception
            r8 = r76
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r21 = r2
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1861:
            r0 = move-exception
            r8 = r76
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x187c:
            r8 = r76
        L_0x187e:
            r24 = r12
            r12 = r21
            r9 = r25
            r7 = r36
            r36 = r11
            r11 = r3
            goto L_0x1afc
        L_0x188b:
            r0 = move-exception
            r59 = r8
            r8 = r9
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x18a7:
            r0 = move-exception
            r77 = r3
            r59 = r8
            r8 = r9
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x18c5:
            r0 = move-exception
            r23 = r3
            r59 = r8
            r8 = r9
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x18e3:
            r0 = move-exception
            r31 = r2
            r23 = r3
            r8 = r9
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1901:
            r13 = r67
            r38 = r2
            r39 = r3
            r61 = r7
            r12 = r22
            r4 = r23
            r14 = r43
            r23 = r8
            r8 = r9
            r22 = r15
            r15 = r78
        L_0x1916:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x20b7 }
            r1.<init>()     // Catch:{ Exception -> 0x20b7 }
            if (r59 == 0) goto L_0x1943
            r2 = r59
            r1.ttl_seconds = r2     // Catch:{ Exception -> 0x192a }
            r11.ttl = r2     // Catch:{ Exception -> 0x192a }
            int r3 = r1.flags     // Catch:{ Exception -> 0x192a }
            r7 = 2
            r3 = r3 | r7
            r1.flags = r3     // Catch:{ Exception -> 0x192a }
            goto L_0x1945
        L_0x192a:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r25 = r2
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1943:
            r2 = r59
        L_0x1945:
            if (r77 == 0) goto L_0x1a34
            java.lang.String r3 = "masks"
            r7 = r77
            java.lang.Object r3 = r7.get(r3)     // Catch:{ Exception -> 0x19f8 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x19f8 }
            if (r3 == 0) goto L_0x19ef
            im.bclpbkiauv.tgnet.SerializedData r9 = new im.bclpbkiauv.tgnet.SerializedData     // Catch:{ Exception -> 0x19f8 }
            r59 = r2
            byte[] r2 = im.bclpbkiauv.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x19d2 }
            r9.<init>((byte[]) r2)     // Catch:{ Exception -> 0x19d2 }
            r2 = r9
            r9 = 0
            int r24 = r2.readInt32(r9)     // Catch:{ Exception -> 0x19d2 }
            r9 = r24
            r24 = 0
            r56 = r3
            r3 = r24
        L_0x196c:
            if (r3 >= r9) goto L_0x19a6
            r77 = r7
            java.util.ArrayList r7 = r1.stickers     // Catch:{ Exception -> 0x198b }
            r58 = r9
            r24 = r12
            r9 = 0
            int r12 = r2.readInt32(r9)     // Catch:{ Exception -> 0x19b7 }
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r12 = im.bclpbkiauv.tgnet.TLRPC.InputDocument.TLdeserialize(r2, r12, r9)     // Catch:{ Exception -> 0x19b7 }
            r7.add(r12)     // Catch:{ Exception -> 0x19b7 }
            int r3 = r3 + 1
            r9 = r58
            r7 = r77
            r12 = r24
            goto L_0x196c
        L_0x198b:
            r0 = move-exception
            r24 = r12
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x19a6:
            r77 = r7
            r58 = r9
            r24 = r12
            int r3 = r1.flags     // Catch:{ Exception -> 0x19b7 }
            r7 = 1
            r3 = r3 | r7
            r1.flags = r3     // Catch:{ Exception -> 0x19b7 }
            r2.cleanup()     // Catch:{ Exception -> 0x19b7 }
            goto L_0x1a38
        L_0x19b7:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r12 = r24
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x19d2:
            r0 = move-exception
            r77 = r7
            r24 = r12
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x19ef:
            r59 = r2
            r56 = r3
            r77 = r7
            r24 = r12
            goto L_0x1a38
        L_0x19f8:
            r0 = move-exception
            r59 = r2
            r77 = r7
            r24 = r12
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1a17:
            r0 = move-exception
            r59 = r2
            r24 = r12
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r9 = r14
            r45 = r23
            r23 = r31
            r49 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1a34:
            r59 = r2
            r24 = r12
        L_0x1a38:
            r7 = r36
            long r2 = r7.access_hash     // Catch:{ Exception -> 0x208a }
            int r9 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r9 != 0) goto L_0x1a47
            r2 = r1
            r3 = 1
            r25 = r2
            r36 = r11
            goto L_0x1a79
        L_0x1a47:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x208a }
            r2.<init>()     // Catch:{ Exception -> 0x208a }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x208a }
            r3.<init>()     // Catch:{ Exception -> 0x208a }
            r2.id = r3     // Catch:{ Exception -> 0x208a }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id     // Catch:{ Exception -> 0x208a }
            r36 = r11
            long r11 = r7.id     // Catch:{ Exception -> 0x205b }
            r3.id = r11     // Catch:{ Exception -> 0x205b }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id     // Catch:{ Exception -> 0x205b }
            long r11 = r7.access_hash     // Catch:{ Exception -> 0x205b }
            r3.access_hash = r11     // Catch:{ Exception -> 0x205b }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id     // Catch:{ Exception -> 0x205b }
            byte[] r9 = r7.file_reference     // Catch:{ Exception -> 0x205b }
            r3.file_reference = r9     // Catch:{ Exception -> 0x205b }
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id     // Catch:{ Exception -> 0x205b }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x205b }
            if (r3 != 0) goto L_0x1a74
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id     // Catch:{ Exception -> 0x1aae }
            r9 = 0
            byte[] r11 = new byte[r9]     // Catch:{ Exception -> 0x1aae }
            r3.file_reference = r11     // Catch:{ Exception -> 0x1aae }
        L_0x1a74:
            r3 = r2
            r25 = r3
            r3 = r64
        L_0x1a79:
            if (r21 != 0) goto L_0x1acb
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r2 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1aae }
            r2.<init>(r5)     // Catch:{ Exception -> 0x1aae }
            r9 = 0
            r2.type = r9     // Catch:{ Exception -> 0x1a8f }
            r2.obj = r14     // Catch:{ Exception -> 0x1a8f }
            r2.originalPath = r4     // Catch:{ Exception -> 0x1a8f }
            if (r8 == 0) goto L_0x1a8b
            r9 = 1
            goto L_0x1a8c
        L_0x1a8b:
            r9 = 0
        L_0x1a8c:
            r2.scheduled = r9     // Catch:{ Exception -> 0x1a8f }
            goto L_0x1acd
        L_0x1a8f:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r21 = r2
            r26 = r4
            r49 = r7
            r9 = r14
            r45 = r23
            r12 = r24
            r23 = r31
            r11 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1aae:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r49 = r7
            r9 = r14
            r45 = r23
            r12 = r24
            r23 = r31
            r11 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1acb:
            r2 = r21
        L_0x1acd:
            r2.inputUploadMedia = r1     // Catch:{ Exception -> 0x202a }
            r2.performMediaUpload = r3     // Catch:{ Exception -> 0x202a }
            if (r13 == 0) goto L_0x1ae4
            int r9 = r67.length()     // Catch:{ Exception -> 0x1a8f }
            if (r9 <= 0) goto L_0x1ae4
            r9 = r18
            boolean r9 = r13.startsWith(r9)     // Catch:{ Exception -> 0x1a8f }
            if (r9 == 0) goto L_0x1ae4
            r2.httpLocation = r13     // Catch:{ Exception -> 0x1a8f }
            goto L_0x1af8
        L_0x1ae4:
            java.util.ArrayList r9 = r7.sizes     // Catch:{ Exception -> 0x202a }
            java.util.ArrayList r11 = r7.sizes     // Catch:{ Exception -> 0x202a }
            int r11 = r11.size()     // Catch:{ Exception -> 0x202a }
            r12 = 1
            int r11 = r11 - r12
            java.lang.Object r9 = r9.get(r11)     // Catch:{ Exception -> 0x202a }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r9     // Catch:{ Exception -> 0x202a }
            r2.photoSize = r9     // Catch:{ Exception -> 0x202a }
            r2.locationParent = r7     // Catch:{ Exception -> 0x202a }
        L_0x1af8:
            r12 = r2
            r11 = r3
            r9 = r25
        L_0x1afc:
            int r1 = (r39 > r16 ? 1 : (r39 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1d51
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest     // Catch:{ Exception -> 0x1d2a }
            if (r1 == 0) goto L_0x1b2f
            im.bclpbkiauv.tgnet.TLObject r1 = r12.sendRequest     // Catch:{ Exception -> 0x1b10 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMultiMedia) r1     // Catch:{ Exception -> 0x1b10 }
            r18 = r7
            r7 = r36
            r2 = r38
            goto L_0x1bda
        L_0x1b10:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r49 = r7
            r21 = r12
            r9 = r14
            r45 = r23
            r12 = r24
            r23 = r31
            r11 = r36
            r2 = r37
            r3 = r38
            r4 = r0
            goto L_0x3281
        L_0x1b2f:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x1d2a }
            r1.<init>()     // Catch:{ Exception -> 0x1d2a }
            r2 = r38
            r1.peer = r2     // Catch:{ Exception -> 0x1d04 }
            if (r75 == 0) goto L_0x1b9b
            int r3 = r10.currentAccount     // Catch:{ Exception -> 0x1b7b }
            android.content.SharedPreferences r3 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x1b7b }
            r18 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1b5d }
            r7.<init>()     // Catch:{ Exception -> 0x1b5d }
            r13 = r61
            r7.append(r13)     // Catch:{ Exception -> 0x1b5d }
            r7.append(r5)     // Catch:{ Exception -> 0x1b5d }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x1b5d }
            r13 = 0
            boolean r3 = r3.getBoolean(r7, r13)     // Catch:{ Exception -> 0x1b5d }
            if (r3 == 0) goto L_0x1b5b
            goto L_0x1b9d
        L_0x1b5b:
            r3 = 0
            goto L_0x1b9e
        L_0x1b5d:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r21 = r12
            r9 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r11 = r36
            r2 = r37
            r4 = r0
            goto L_0x3281
        L_0x1b7b:
            r0 = move-exception
            r18 = r7
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r21 = r12
            r9 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r11 = r36
            r2 = r37
            r4 = r0
            goto L_0x3281
        L_0x1b9b:
            r18 = r7
        L_0x1b9d:
            r3 = 1
        L_0x1b9e:
            r1.silent = r3     // Catch:{ Exception -> 0x1ce0 }
            r7 = r36
            int r3 = r7.reply_to_msg_id     // Catch:{ Exception -> 0x1cbe }
            if (r3 == 0) goto L_0x1bce
            int r3 = r1.flags     // Catch:{ Exception -> 0x1bb1 }
            r13 = 1
            r3 = r3 | r13
            r1.flags = r3     // Catch:{ Exception -> 0x1bb1 }
            int r3 = r7.reply_to_msg_id     // Catch:{ Exception -> 0x1bb1 }
            r1.reply_to_msg_id = r3     // Catch:{ Exception -> 0x1bb1 }
            goto L_0x1bce
        L_0x1bb1:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r9 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r4 = r0
            goto L_0x3281
        L_0x1bce:
            if (r8 == 0) goto L_0x1bd8
            r1.schedule_date = r8     // Catch:{ Exception -> 0x1bb1 }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1bb1 }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r1.flags = r3     // Catch:{ Exception -> 0x1bb1 }
        L_0x1bd8:
            r12.sendRequest = r1     // Catch:{ Exception -> 0x1cbe }
        L_0x1bda:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r3 = r12.messageObjects     // Catch:{ Exception -> 0x1cbe }
            r3.add(r14)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<java.lang.Object> r3 = r12.parentObjects     // Catch:{ Exception -> 0x1cbe }
            r3.add(r15)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r12.locations     // Catch:{ Exception -> 0x1cbe }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r13 = r12.photoSize     // Catch:{ Exception -> 0x1cbe }
            r3.add(r13)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<im.bclpbkiauv.messenger.VideoEditedInfo> r3 = r12.videoEditedInfos     // Catch:{ Exception -> 0x1cbe }
            im.bclpbkiauv.messenger.VideoEditedInfo r13 = r12.videoEditedInfo     // Catch:{ Exception -> 0x1cbe }
            r3.add(r13)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<java.lang.String> r3 = r12.httpLocations     // Catch:{ Exception -> 0x1cbe }
            java.lang.String r13 = r12.httpLocation     // Catch:{ Exception -> 0x1cbe }
            r3.add(r13)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputMedia> r3 = r12.inputMedias     // Catch:{ Exception -> 0x1cbe }
            im.bclpbkiauv.tgnet.TLRPC$InputMedia r13 = r12.inputUploadMedia     // Catch:{ Exception -> 0x1cbe }
            r3.add(r13)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r3 = r12.messages     // Catch:{ Exception -> 0x1cbe }
            r3.add(r7)     // Catch:{ Exception -> 0x1cbe }
            java.util.ArrayList<java.lang.String> r3 = r12.originalPaths     // Catch:{ Exception -> 0x1cbe }
            r3.add(r4)     // Catch:{ Exception -> 0x1cbe }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x1cbe }
            r3.<init>()     // Catch:{ Exception -> 0x1cbe }
            r43 = r14
            long r13 = r7.random_id     // Catch:{ Exception -> 0x1c9e }
            r3.random_id = r13     // Catch:{ Exception -> 0x1c9e }
            r3.media = r9     // Catch:{ Exception -> 0x1c9e }
            r14 = r69
            r3.message = r14     // Catch:{ Exception -> 0x1c7c }
            r25 = r59
            r13 = r72
            if (r13 == 0) goto L_0x1c52
            boolean r21 = r72.isEmpty()     // Catch:{ Exception -> 0x1c34 }
            if (r21 != 0) goto L_0x1c52
            r3.entities = r13     // Catch:{ Exception -> 0x1c34 }
            r26 = r4
            int r4 = r3.flags     // Catch:{ Exception -> 0x1c60 }
            r21 = 1
            r4 = r4 | 1
            r3.flags = r4     // Catch:{ Exception -> 0x1c60 }
            goto L_0x1c54
        L_0x1c34:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1c52:
            r26 = r4
        L_0x1c54:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media     // Catch:{ Exception -> 0x1c60 }
            r4.add(r3)     // Catch:{ Exception -> 0x1c60 }
            r38 = r2
            r4 = r13
            r13 = r1
            goto L_0x1e02
        L_0x1c60:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            goto L_0x3281
        L_0x1c7c:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1c9e:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1cbe:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r43 = r14
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1ce0:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r43 = r14
            r7 = r36
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1d04:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r18 = r7
            r43 = r14
            r7 = r36
            r1 = r71
            r51 = r74
            r52 = r77
            r3 = r2
            r26 = r4
            r11 = r7
            r21 = r12
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1d2a:
            r0 = move-exception
            r25 = r59
            r13 = r72
            r18 = r7
            r43 = r14
            r7 = r36
            r1 = r71
            r51 = r74
            r52 = r77
            r26 = r4
            r11 = r7
            r21 = r12
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r3 = r38
            r9 = r43
            r4 = r0
            goto L_0x3281
        L_0x1d51:
            r25 = r59
            r13 = r61
            r26 = r4
            r18 = r7
            r43 = r14
            r7 = r36
            r2 = r38
            r14 = r69
            r4 = r72
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMedia r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x2004 }
            r1.<init>()     // Catch:{ Exception -> 0x2004 }
            r1.peer = r2     // Catch:{ Exception -> 0x2004 }
            if (r75 == 0) goto L_0x1dc6
            int r3 = r10.currentAccount     // Catch:{ Exception -> 0x1daa }
            android.content.SharedPreferences r3 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x1daa }
            r38 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1d8d }
            r2.<init>()     // Catch:{ Exception -> 0x1d8d }
            r2.append(r13)     // Catch:{ Exception -> 0x1d8d }
            r2.append(r5)     // Catch:{ Exception -> 0x1d8d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1d8d }
            r13 = 0
            boolean r2 = r3.getBoolean(r2, r13)     // Catch:{ Exception -> 0x1d8d }
            if (r2 == 0) goto L_0x1d8b
            goto L_0x1dc8
        L_0x1d8b:
            r2 = 0
            goto L_0x1dc9
        L_0x1d8d:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r3 = r38
            r9 = r43
            goto L_0x3281
        L_0x1daa:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r3 = r2
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r45 = r23
            r12 = r24
            r23 = r31
            r2 = r37
            r9 = r43
            goto L_0x3281
        L_0x1dc6:
            r38 = r2
        L_0x1dc8:
            r2 = 1
        L_0x1dc9:
            r1.silent = r2     // Catch:{ Exception -> 0x1fdd }
            int r2 = r7.reply_to_msg_id     // Catch:{ Exception -> 0x1fdd }
            if (r2 == 0) goto L_0x1dd9
            int r2 = r1.flags     // Catch:{ Exception -> 0x1d8d }
            r3 = 1
            r2 = r2 | r3
            r1.flags = r2     // Catch:{ Exception -> 0x1d8d }
            int r2 = r7.reply_to_msg_id     // Catch:{ Exception -> 0x1d8d }
            r1.reply_to_msg_id = r2     // Catch:{ Exception -> 0x1d8d }
        L_0x1dd9:
            long r2 = r7.random_id     // Catch:{ Exception -> 0x1fdd }
            r1.random_id = r2     // Catch:{ Exception -> 0x1fdd }
            r1.media = r9     // Catch:{ Exception -> 0x1fdd }
            r1.message = r14     // Catch:{ Exception -> 0x1fdd }
            if (r4 == 0) goto L_0x1df2
            boolean r2 = r72.isEmpty()     // Catch:{ Exception -> 0x1d8d }
            if (r2 != 0) goto L_0x1df2
            r1.entities = r4     // Catch:{ Exception -> 0x1d8d }
            int r2 = r1.flags     // Catch:{ Exception -> 0x1d8d }
            r3 = 8
            r2 = r2 | r3
            r1.flags = r2     // Catch:{ Exception -> 0x1d8d }
        L_0x1df2:
            if (r8 == 0) goto L_0x1dfc
            r1.schedule_date = r8     // Catch:{ Exception -> 0x1d8d }
            int r2 = r1.flags     // Catch:{ Exception -> 0x1d8d }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x1d8d }
        L_0x1dfc:
            if (r12 == 0) goto L_0x1e00
            r12.sendRequest = r1     // Catch:{ Exception -> 0x1d8d }
        L_0x1e00:
            r2 = r1
            r13 = r2
        L_0x1e02:
            int r1 = (r39 > r16 ? 1 : (r39 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1e22
            r10.performSendDelayedMessage(r12)     // Catch:{ Exception -> 0x1d8d }
            r51 = r74
            r52 = r77
            r50 = r7
            r4 = r8
            r49 = r18
            r53 = r19
            r3 = r23
            r48 = r24
            r69 = r26
            r23 = r31
            r44 = r38
            r46 = r39
            goto L_0x1fc1
        L_0x1e22:
            r3 = r23
            r1 = 1
            if (r3 != r1) goto L_0x1e55
            r1 = 0
            if (r8 == 0) goto L_0x1e2c
            r2 = 1
            goto L_0x1e2d
        L_0x1e2c:
            r2 = 0
        L_0x1e2d:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r1
            r60 = r12
            r61 = r78
            r62 = r2
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1e74 }
            r51 = r74
            r52 = r77
            r50 = r7
            r4 = r8
            r49 = r18
            r53 = r19
            r48 = r24
            r69 = r26
            r23 = r31
            r44 = r38
            r46 = r39
            goto L_0x1fc1
        L_0x1e55:
            r2 = 2
            if (r3 != r2) goto L_0x1eee
            if (r11 == 0) goto L_0x1e91
            r10.performSendDelayedMessage(r12)     // Catch:{ Exception -> 0x1e74 }
            r51 = r74
            r52 = r77
            r50 = r7
            r4 = r8
            r49 = r18
            r53 = r19
            r48 = r24
            r69 = r26
            r23 = r31
            r44 = r38
            r46 = r39
            goto L_0x1fc1
        L_0x1e74:
            r0 = move-exception
            r1 = r71
            r51 = r74
            r52 = r77
            r4 = r0
            r45 = r3
            r11 = r7
            r21 = r12
            r69 = r14
            r49 = r18
            r12 = r24
            r23 = r31
            r2 = r37
            r3 = r38
            r9 = r43
            goto L_0x3281
        L_0x1e91:
            r16 = 0
            r17 = 1
            if (r8 == 0) goto L_0x1e9a
            r21 = 1
            goto L_0x1e9c
        L_0x1e9a:
            r21 = 0
        L_0x1e9c:
            r23 = r31
            r1 = r55
            r44 = r38
            r29 = 2
            r2 = r13
            r30 = r77
            r45 = r3
            r46 = r39
            r3 = r43
            r69 = r26
            r4 = r69
            r6 = r24
            r5 = r16
            r48 = r6
            r8 = r19
            r6 = r17
            r50 = r7
            r49 = r18
            r7 = r12
            r51 = r74
            r53 = r8
            r52 = r30
            r8 = r78
            r16 = r9
            r15 = 2
            r9 = r21
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x1ed6 }
            r4 = r76
            r3 = r45
            goto L_0x1fc1
        L_0x1ed6:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r21 = r12
            r69 = r14
            r2 = r37
            r9 = r43
            r3 = r44
            r12 = r48
            r11 = r50
            r19 = r53
            goto L_0x3281
        L_0x1eee:
            r51 = r74
            r52 = r77
            r45 = r3
            r50 = r7
            r16 = r9
            r49 = r18
            r53 = r19
            r48 = r24
            r69 = r26
            r23 = r31
            r44 = r38
            r46 = r39
            r15 = 2
            r1 = 3
            if (r3 != r1) goto L_0x1f47
            if (r11 == 0) goto L_0x1f2d
            r10.performSendDelayedMessage(r12)     // Catch:{ Exception -> 0x1f13 }
            r4 = r76
            goto L_0x1fc1
        L_0x1f13:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r12
            r69 = r14
            r2 = r37
            r9 = r43
            r3 = r44
            r12 = r48
            r11 = r50
            r19 = r53
            goto L_0x3281
        L_0x1f2d:
            r4 = r76
            if (r4 == 0) goto L_0x1f33
            r1 = 1
            goto L_0x1f34
        L_0x1f33:
            r1 = 0
        L_0x1f34:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r69
            r60 = r12
            r61 = r78
            r62 = r1
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1f47:
            r4 = r76
            r1 = 6
            if (r3 != r1) goto L_0x1f64
            if (r4 == 0) goto L_0x1f50
            r1 = 1
            goto L_0x1f51
        L_0x1f50:
            r1 = 0
        L_0x1f51:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r69
            r60 = r12
            r61 = r78
            r62 = r1
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1f64:
            r1 = 7
            if (r3 != r1) goto L_0x1f86
            if (r11 == 0) goto L_0x1f6f
            if (r12 == 0) goto L_0x1f6f
            r10.performSendDelayedMessage(r12)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1f6f:
            if (r4 == 0) goto L_0x1f73
            r1 = 1
            goto L_0x1f74
        L_0x1f73:
            r1 = 0
        L_0x1f74:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r69
            r60 = r12
            r61 = r78
            r62 = r1
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1f86:
            r1 = 8
            if (r3 != r1) goto L_0x1fa7
            if (r11 == 0) goto L_0x1f90
            r10.performSendDelayedMessage(r12)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1f90:
            if (r4 == 0) goto L_0x1f94
            r1 = 1
            goto L_0x1f95
        L_0x1f94:
            r1 = 0
        L_0x1f95:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r69
            r60 = r12
            r61 = r78
            r62 = r1
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1f13 }
            goto L_0x1fc1
        L_0x1fa7:
            r1 = 10
            if (r3 != r1) goto L_0x1fc1
            if (r4 == 0) goto L_0x1faf
            r1 = 1
            goto L_0x1fb0
        L_0x1faf:
            r1 = 0
        L_0x1fb0:
            r56 = r55
            r57 = r13
            r58 = r43
            r59 = r69
            r60 = r12
            r61 = r78
            r62 = r1
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1f13 }
        L_0x1fc1:
            r26 = r69
            r1 = r71
            r45 = r3
            r21 = r12
            r69 = r14
            r11 = r33
            r7 = r35
            r2 = r37
            r9 = r43
            r12 = r50
            r5 = r52
            r19 = r53
            r14 = r65
            goto L_0x3083
        L_0x1fdd:
            r0 = move-exception
            r51 = r74
            r52 = r77
            r50 = r7
            r4 = r8
            r49 = r18
            r53 = r19
            r3 = r23
            r48 = r24
            r23 = r31
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r12
            r69 = r14
            r2 = r37
            r3 = r38
            r9 = r43
            r12 = r48
            r11 = r50
            goto L_0x3281
        L_0x2004:
            r0 = move-exception
            r51 = r74
            r52 = r77
            r50 = r7
            r4 = r8
            r49 = r18
            r53 = r19
            r3 = r23
            r48 = r24
            r23 = r31
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r12
            r69 = r14
            r9 = r43
            r12 = r48
            r11 = r50
            r3 = r2
            r2 = r37
            goto L_0x3281
        L_0x202a:
            r0 = move-exception
            r25 = r59
            r51 = r74
            r52 = r77
            r49 = r7
            r43 = r14
            r53 = r19
            r3 = r23
            r48 = r24
            r23 = r31
            r50 = r36
            r14 = r69
            r69 = r4
            r4 = r8
            r26 = r69
            r1 = r71
            r4 = r0
            r21 = r2
            r45 = r3
            r69 = r14
            r2 = r37
            r3 = r38
            r9 = r43
            r12 = r48
            r11 = r50
            goto L_0x3281
        L_0x205b:
            r0 = move-exception
            r25 = r59
            r51 = r74
            r52 = r77
            r49 = r7
            r43 = r14
            r53 = r19
            r3 = r23
            r48 = r24
            r23 = r31
            r50 = r36
            r14 = r69
            r69 = r4
            r4 = r8
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r2 = r37
            r3 = r38
            r9 = r43
            r12 = r48
            r11 = r50
            goto L_0x3281
        L_0x208a:
            r0 = move-exception
            r25 = r59
            r51 = r74
            r52 = r77
            r49 = r7
            r50 = r11
            r43 = r14
            r53 = r19
            r3 = r23
            r48 = r24
            r23 = r31
            r14 = r69
            r69 = r4
            r4 = r8
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r2 = r37
            r3 = r38
            r9 = r43
            r12 = r48
            goto L_0x3281
        L_0x20b7:
            r0 = move-exception
            r25 = r59
            r51 = r74
            r52 = r77
            r50 = r11
            r48 = r12
            r43 = r14
            r53 = r19
            r3 = r23
            r23 = r31
            r49 = r36
            r14 = r69
            r69 = r4
            r4 = r8
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r2 = r37
            r3 = r38
            r9 = r43
            goto L_0x3281
        L_0x20e2:
            r25 = r59
            r14 = r69
            r51 = r74
            r52 = r77
            r64 = r1
            r44 = r2
            r46 = r3
            r3 = r8
            r4 = r9
            r50 = r11
            r37 = r13
            r9 = r18
            r53 = r19
            r48 = r22
            r69 = r23
            r23 = r31
            r1 = r33
            r49 = r36
            r33 = r12
            r22 = r15
            r15 = 2
            r2 = r37
            int r5 = r2.layer     // Catch:{ Exception -> 0x2c2d }
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r5)     // Catch:{ Exception -> 0x2c2d }
            r6 = 73
            if (r5 < r6) goto L_0x2140
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x212a }
            r5.<init>()     // Catch:{ Exception -> 0x212a }
            r6 = r46
            int r8 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x2147
            r5.grouped_id = r6     // Catch:{ Exception -> 0x212a }
            int r8 = r5.flags     // Catch:{ Exception -> 0x212a }
            r11 = 131072(0x20000, float:1.83671E-40)
            r8 = r8 | r11
            r5.flags = r8     // Catch:{ Exception -> 0x212a }
            goto L_0x2147
        L_0x212a:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r9 = r43
            r3 = r44
            r12 = r48
            r11 = r50
            r19 = r53
            goto L_0x3281
        L_0x2140:
            r6 = r46
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage_layer45 r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x2c2d }
            r5.<init>()     // Catch:{ Exception -> 0x2c2d }
        L_0x2147:
            r12 = r50
            int r8 = r12.ttl     // Catch:{ Exception -> 0x2c11 }
            r5.ttl = r8     // Catch:{ Exception -> 0x2c11 }
            r11 = r72
            if (r11 == 0) goto L_0x2175
            boolean r8 = r72.isEmpty()     // Catch:{ Exception -> 0x2160 }
            if (r8 != 0) goto L_0x2175
            r5.entities = r11     // Catch:{ Exception -> 0x2160 }
            int r8 = r5.flags     // Catch:{ Exception -> 0x2160 }
            r8 = r8 | 128(0x80, float:1.794E-43)
            r5.flags = r8     // Catch:{ Exception -> 0x2160 }
            goto L_0x2175
        L_0x2160:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r11 = r12
            r69 = r14
            r9 = r43
            r3 = r44
            r12 = r48
            r19 = r53
            goto L_0x3281
        L_0x2175:
            r18 = r9
            long r8 = r12.reply_to_random_id     // Catch:{ Exception -> 0x2c11 }
            int r13 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r13 == 0) goto L_0x2188
            long r8 = r12.reply_to_random_id     // Catch:{ Exception -> 0x2160 }
            r5.reply_to_random_id = r8     // Catch:{ Exception -> 0x2160 }
            int r8 = r5.flags     // Catch:{ Exception -> 0x2160 }
            r9 = 8
            r8 = r8 | r9
            r5.flags = r8     // Catch:{ Exception -> 0x2160 }
        L_0x2188:
            int r8 = r5.flags     // Catch:{ Exception -> 0x2c11 }
            r8 = r8 | 512(0x200, float:7.175E-43)
            r5.flags = r8     // Catch:{ Exception -> 0x2c11 }
            r9 = r52
            if (r9 == 0) goto L_0x21c0
            r8 = r56
            java.lang.Object r13 = r9.get(r8)     // Catch:{ Exception -> 0x21a9 }
            if (r13 == 0) goto L_0x21c0
            java.lang.Object r8 = r9.get(r8)     // Catch:{ Exception -> 0x21a9 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x21a9 }
            r5.via_bot_name = r8     // Catch:{ Exception -> 0x21a9 }
            int r8 = r5.flags     // Catch:{ Exception -> 0x21a9 }
            r8 = r8 | 2048(0x800, float:2.87E-42)
            r5.flags = r8     // Catch:{ Exception -> 0x21a9 }
            goto L_0x21c0
        L_0x21a9:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r52 = r9
            r11 = r12
            r69 = r14
            r9 = r43
            r3 = r44
            r12 = r48
            r19 = r53
            goto L_0x3281
        L_0x21c0:
            r52 = r9
            long r8 = r12.random_id     // Catch:{ Exception -> 0x2c11 }
            r5.random_id = r8     // Catch:{ Exception -> 0x2c11 }
            r5.message = r1     // Catch:{ Exception -> 0x2c11 }
            r1 = 1
            if (r3 != r1) goto L_0x2299
            r1 = r22
            boolean r8 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue     // Catch:{ Exception -> 0x227f }
            if (r8 == 0) goto L_0x2208
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVenue r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x21f1 }
            r8.<init>()     // Catch:{ Exception -> 0x21f1 }
            r5.media = r8     // Catch:{ Exception -> 0x21f1 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x21f1 }
            java.lang.String r9 = r1.address     // Catch:{ Exception -> 0x21f1 }
            r8.address = r9     // Catch:{ Exception -> 0x21f1 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x21f1 }
            java.lang.String r9 = r1.title     // Catch:{ Exception -> 0x21f1 }
            r8.title = r9     // Catch:{ Exception -> 0x21f1 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x21f1 }
            java.lang.String r9 = r1.provider     // Catch:{ Exception -> 0x21f1 }
            r8.provider = r9     // Catch:{ Exception -> 0x21f1 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x21f1 }
            java.lang.String r9 = r1.venue_id     // Catch:{ Exception -> 0x21f1 }
            r8.venue_id = r9     // Catch:{ Exception -> 0x21f1 }
            goto L_0x220f
        L_0x21f1:
            r0 = move-exception
            r26 = r69
            r4 = r0
            r22 = r1
            r45 = r3
            r11 = r12
            r69 = r14
            r9 = r43
            r3 = r44
            r12 = r48
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x2208:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x227f }
            r8.<init>()     // Catch:{ Exception -> 0x227f }
            r5.media = r8     // Catch:{ Exception -> 0x227f }
        L_0x220f:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x227f }
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r9 = r1.geo     // Catch:{ Exception -> 0x227f }
            r36 = r12
            double r11 = r9.lat     // Catch:{ Exception -> 0x2267 }
            r8.lat = r11     // Catch:{ Exception -> 0x2267 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x2267 }
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r9 = r1.geo     // Catch:{ Exception -> 0x2267 }
            double r11 = r9._long     // Catch:{ Exception -> 0x2267 }
            r8._long = r11     // Catch:{ Exception -> 0x2267 }
            im.bclpbkiauv.messenger.SecretChatHelper r8 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x2267 }
            r9 = r43
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner     // Catch:{ Exception -> 0x2251 }
            r12 = 0
            r13 = 0
            r56 = r8
            r57 = r5
            r58 = r11
            r59 = r2
            r60 = r12
            r61 = r13
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2251 }
            r13 = r67
            r11 = r69
            r22 = r1
            r45 = r3
            r46 = r6
            r6 = r14
            r12 = r48
            r19 = r53
            r14 = r65
            r1 = r78
            goto L_0x2917
        L_0x2251:
            r0 = move-exception
            r26 = r69
            r4 = r0
            r22 = r1
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r12 = r48
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x2267:
            r0 = move-exception
            r9 = r43
            r26 = r69
            r4 = r0
            r22 = r1
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r12 = r48
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x227f:
            r0 = move-exception
            r36 = r12
            r9 = r43
            r26 = r69
            r4 = r0
            r22 = r1
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r12 = r48
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x2299:
            r36 = r12
            r1 = r22
            r9 = r43
            java.lang.String r8 = "parentObject"
            if (r3 == r15) goto L_0x2943
            r11 = 9
            if (r3 != r11) goto L_0x22c4
            r11 = r49
            if (r11 == 0) goto L_0x22c6
            r13 = r67
            r22 = r1
            r45 = r3
            r46 = r6
            r49 = r11
            r6 = r14
            r3 = r18
            r12 = r48
            r19 = r53
            r14 = r65
            r11 = r69
            r1 = r78
            goto L_0x2958
        L_0x22c4:
            r11 = r49
        L_0x22c6:
            r12 = 3
            if (r3 != r12) goto L_0x25ad
            r12 = r48
            java.util.ArrayList r13 = r12.thumbs     // Catch:{ Exception -> 0x258f }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r13 = r10.getThumbForSecretChat(r13)     // Catch:{ Exception -> 0x258f }
            im.bclpbkiauv.messenger.ImageLoader.fillPhotoSizeWithBytes(r13)     // Catch:{ Exception -> 0x258f }
            boolean r18 = im.bclpbkiauv.messenger.MessageObject.isNewGifDocument((im.bclpbkiauv.tgnet.TLRPC.Document) r12)     // Catch:{ Exception -> 0x258f }
            if (r18 != 0) goto L_0x2364
            boolean r18 = im.bclpbkiauv.messenger.MessageObject.isRoundVideoDocument(r12)     // Catch:{ Exception -> 0x234e }
            if (r18 == 0) goto L_0x22e6
            r22 = r1
            r49 = r11
            goto L_0x2368
        L_0x22e6:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVideo r15 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x234e }
            r15.<init>()     // Catch:{ Exception -> 0x234e }
            r5.media = r15     // Catch:{ Exception -> 0x234e }
            if (r13 == 0) goto L_0x232b
            byte[] r15 = r13.bytes     // Catch:{ Exception -> 0x2315 }
            if (r15 == 0) goto L_0x232b
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r15 = r5.media     // Catch:{ Exception -> 0x2315 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVideo r15 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r15     // Catch:{ Exception -> 0x2315 }
            r22 = r1
            byte[] r1 = r13.bytes     // Catch:{ Exception -> 0x2301 }
            r15.thumb = r1     // Catch:{ Exception -> 0x2301 }
            r49 = r11
            goto L_0x239f
        L_0x2301:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r49 = r11
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2315:
            r0 = move-exception
            r22 = r1
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r49 = r11
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x232b:
            r22 = r1
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x233a }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaVideo r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r1     // Catch:{ Exception -> 0x233a }
            r49 = r11
            r15 = 0
            byte[] r11 = new byte[r15]     // Catch:{ Exception -> 0x2384 }
            r1.thumb = r11     // Catch:{ Exception -> 0x2384 }
            goto L_0x239f
        L_0x233a:
            r0 = move-exception
            r49 = r11
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x234e:
            r0 = move-exception
            r22 = r1
            r49 = r11
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2364:
            r22 = r1
            r49 = r11
        L_0x2368:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x2575 }
            r1.<init>()     // Catch:{ Exception -> 0x2575 }
            r5.media = r1     // Catch:{ Exception -> 0x2575 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2575 }
            java.util.ArrayList r11 = r12.attributes     // Catch:{ Exception -> 0x2575 }
            r1.attributes = r11     // Catch:{ Exception -> 0x2575 }
            if (r13 == 0) goto L_0x2396
            byte[] r1 = r13.bytes     // Catch:{ Exception -> 0x2384 }
            if (r1 == 0) goto L_0x2396
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2384 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x2384 }
            byte[] r11 = r13.bytes     // Catch:{ Exception -> 0x2384 }
            r1.thumb = r11     // Catch:{ Exception -> 0x2384 }
            goto L_0x239f
        L_0x2384:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2396:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2575 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x2575 }
            r11 = 0
            byte[] r15 = new byte[r11]     // Catch:{ Exception -> 0x2575 }
            r1.thumb = r15     // Catch:{ Exception -> 0x2575 }
        L_0x239f:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2575 }
            r1.caption = r14     // Catch:{ Exception -> 0x2575 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2575 }
            java.lang.String r11 = "video/mp4"
            r1.mime_type = r11     // Catch:{ Exception -> 0x2575 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x2575 }
            int r11 = r12.size     // Catch:{ Exception -> 0x2575 }
            r1.size = r11     // Catch:{ Exception -> 0x2575 }
            r1 = 0
        L_0x23b1:
            java.util.ArrayList r11 = r12.attributes     // Catch:{ Exception -> 0x2575 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x2575 }
            if (r1 >= r11) goto L_0x2403
            java.util.ArrayList r11 = r12.attributes     // Catch:{ Exception -> 0x23f1 }
            java.lang.Object r11 = r11.get(r1)     // Catch:{ Exception -> 0x23f1 }
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r11 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r11     // Catch:{ Exception -> 0x23f1 }
            boolean r15 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x23f1 }
            if (r15 == 0) goto L_0x23ec
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r15 = r5.media     // Catch:{ Exception -> 0x23f1 }
            r19 = r14
            int r14 = r11.w     // Catch:{ Exception -> 0x23da }
            r15.w = r14     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r14 = r5.media     // Catch:{ Exception -> 0x23da }
            int r15 = r11.h     // Catch:{ Exception -> 0x23da }
            r14.h = r15     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r14 = r5.media     // Catch:{ Exception -> 0x23da }
            int r15 = r11.duration     // Catch:{ Exception -> 0x23da }
            r14.duration = r15     // Catch:{ Exception -> 0x23da }
            goto L_0x2405
        L_0x23da:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x23ec:
            r19 = r14
            int r1 = r1 + 1
            goto L_0x23b1
        L_0x23f1:
            r0 = move-exception
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r14
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2403:
            r19 = r14
        L_0x2405:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x255d }
            int r11 = r13.h     // Catch:{ Exception -> 0x255d }
            r1.thumb_h = r11     // Catch:{ Exception -> 0x255d }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r1 = r5.media     // Catch:{ Exception -> 0x255d }
            int r11 = r13.w     // Catch:{ Exception -> 0x255d }
            r1.thumb_w = r11     // Catch:{ Exception -> 0x255d }
            byte[] r1 = r12.key     // Catch:{ Exception -> 0x255d }
            if (r1 == 0) goto L_0x2455
            int r1 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x241a
            goto L_0x2455
        L_0x241a:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x23da }
            r1.<init>()     // Catch:{ Exception -> 0x23da }
            long r14 = r12.id     // Catch:{ Exception -> 0x23da }
            r1.id = r14     // Catch:{ Exception -> 0x23da }
            long r14 = r12.access_hash     // Catch:{ Exception -> 0x23da }
            r1.access_hash = r14     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x23da }
            byte[] r11 = r12.key     // Catch:{ Exception -> 0x23da }
            r8.key = r11     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x23da }
            byte[] r11 = r12.iv     // Catch:{ Exception -> 0x23da }
            r8.iv = r11     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.messenger.SecretChatHelper r8 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x23da }
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r9.messageOwner     // Catch:{ Exception -> 0x23da }
            r14 = 0
            r56 = r8
            r57 = r5
            r58 = r11
            r59 = r2
            r60 = r1
            r61 = r14
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x23da }
            r14 = r65
            r11 = r69
            r8 = r78
            r13 = r51
            goto L_0x2531
        L_0x2455:
            if (r21 != 0) goto L_0x251a
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r1 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x2504 }
            r14 = r65
            r1.<init>(r14)     // Catch:{ Exception -> 0x24f0 }
            r1.encryptedChat = r2     // Catch:{ Exception -> 0x24da }
            r11 = 1
            r1.type = r11     // Catch:{ Exception -> 0x24da }
            r1.sendEncryptedRequest = r5     // Catch:{ Exception -> 0x24da }
            r11 = r69
            r1.originalPath = r11     // Catch:{ Exception -> 0x24c4 }
            r1.obj = r9     // Catch:{ Exception -> 0x24c4 }
            if (r52 == 0) goto L_0x2499
            r56 = r13
            r13 = r52
            boolean r18 = r13.containsKey(r8)     // Catch:{ Exception -> 0x2483 }
            if (r18 == 0) goto L_0x249d
            java.lang.Object r8 = r13.get(r8)     // Catch:{ Exception -> 0x2483 }
            r1.parentObject = r8     // Catch:{ Exception -> 0x2483 }
            r8 = r78
            r52 = r13
            r13 = 2
            goto L_0x24a4
        L_0x2483:
            r0 = move-exception
            r4 = r0
            r21 = r1
            r45 = r3
            r26 = r11
            r52 = r13
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x2499:
            r56 = r13
            r13 = r52
        L_0x249d:
            r8 = r78
            r52 = r13
            r13 = 2
            r1.parentObject = r8     // Catch:{ Exception -> 0x24b0 }
        L_0x24a4:
            r13 = 1
            r1.performMediaUpload = r13     // Catch:{ Exception -> 0x24b0 }
            if (r4 == 0) goto L_0x24ab
            r13 = 1
            goto L_0x24ac
        L_0x24ab:
            r13 = 0
        L_0x24ac:
            r1.scheduled = r13     // Catch:{ Exception -> 0x24b0 }
            goto L_0x2524
        L_0x24b0:
            r0 = move-exception
            r4 = r0
            r21 = r1
            r45 = r3
            r26 = r11
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x24c4:
            r0 = move-exception
            r8 = r78
            r4 = r0
            r21 = r1
            r45 = r3
            r26 = r11
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x24da:
            r0 = move-exception
            r8 = r78
            r26 = r69
            r4 = r0
            r21 = r1
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x24f0:
            r0 = move-exception
            r8 = r78
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2504:
            r0 = move-exception
            r14 = r65
            r8 = r78
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x251a:
            r14 = r65
            r11 = r69
            r8 = r78
            r56 = r13
            r1 = r21
        L_0x2524:
            r13 = r51
            r1.videoEditedInfo = r13     // Catch:{ Exception -> 0x2547 }
            int r18 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x252f
            r10.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x2547 }
        L_0x252f:
            r21 = r1
        L_0x2531:
            r45 = r3
            r46 = r6
            r48 = r12
            r51 = r13
            r69 = r19
            r6 = r21
            r8 = r49
            r7 = r52
            r19 = r53
            r13 = r67
            goto L_0x2ae8
        L_0x2547:
            r0 = move-exception
            r4 = r0
            r21 = r1
            r45 = r3
            r26 = r11
            r51 = r13
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            r1 = r71
            goto L_0x3281
        L_0x255d:
            r0 = move-exception
            r14 = r65
            r8 = r78
            r13 = r51
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x2575:
            r0 = move-exception
            r8 = r78
            r19 = r14
            r13 = r51
            r14 = r65
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x258f:
            r0 = move-exception
            r8 = r78
            r22 = r1
            r49 = r11
            r19 = r14
            r13 = r51
            r14 = r65
            r26 = r69
            r1 = r71
            r4 = r0
            r45 = r3
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r53
            goto L_0x3281
        L_0x25ad:
            r22 = r1
            r49 = r11
            r19 = r14
            r12 = r48
            r13 = r51
            r14 = r65
            r11 = r69
            r1 = r78
            r13 = 6
            if (r3 != r13) goto L_0x262d
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaContact r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x2619 }
            r8.<init>()     // Catch:{ Exception -> 0x2619 }
            r5.media = r8     // Catch:{ Exception -> 0x2619 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x2619 }
            r46 = r6
            r13 = r53
            java.lang.String r6 = r13.phone     // Catch:{ Exception -> 0x2607 }
            r8.phone_number = r6     // Catch:{ Exception -> 0x2607 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2607 }
            java.lang.String r7 = r13.first_name     // Catch:{ Exception -> 0x2607 }
            r6.first_name = r7     // Catch:{ Exception -> 0x2607 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2607 }
            java.lang.String r7 = r13.last_name     // Catch:{ Exception -> 0x2607 }
            r6.last_name = r7     // Catch:{ Exception -> 0x2607 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2607 }
            int r7 = r13.id     // Catch:{ Exception -> 0x2607 }
            r6.user_id = r7     // Catch:{ Exception -> 0x2607 }
            im.bclpbkiauv.messenger.SecretChatHelper r6 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x2607 }
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r9.messageOwner     // Catch:{ Exception -> 0x2607 }
            r8 = 0
            r18 = 0
            r56 = r6
            r57 = r5
            r58 = r7
            r59 = r2
            r60 = r8
            r61 = r18
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2607 }
            r45 = r3
            r6 = r19
            r19 = r13
            r13 = r67
            goto L_0x2917
        L_0x2607:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r45 = r3
            r26 = r11
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r13
            goto L_0x3281
        L_0x2619:
            r0 = move-exception
            r13 = r53
            r1 = r71
            r4 = r0
            r45 = r3
            r26 = r11
            r69 = r19
            r11 = r36
            r3 = r44
            r19 = r13
            goto L_0x3281
        L_0x262d:
            r46 = r6
            r13 = r53
            r6 = 7
            if (r3 == r6) goto L_0x2733
            r6 = 9
            if (r3 != r6) goto L_0x2640
            if (r12 == 0) goto L_0x2640
            r6 = r19
            r19 = r13
            goto L_0x2737
        L_0x2640:
            r6 = 8
            if (r3 != r6) goto L_0x2727
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r6 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x2715 }
            r6.<init>(r14)     // Catch:{ Exception -> 0x2715 }
            r6.encryptedChat = r2     // Catch:{ Exception -> 0x2701 }
            r6.sendEncryptedRequest = r5     // Catch:{ Exception -> 0x2701 }
            r6.obj = r9     // Catch:{ Exception -> 0x2701 }
            r7 = 3
            r6.type = r7     // Catch:{ Exception -> 0x2701 }
            r6.parentObject = r1     // Catch:{ Exception -> 0x2701 }
            r7 = 1
            r6.performMediaUpload = r7     // Catch:{ Exception -> 0x2701 }
            if (r4 == 0) goto L_0x265b
            r7 = 1
            goto L_0x265c
        L_0x265b:
            r7 = 0
        L_0x265c:
            r6.scheduled = r7     // Catch:{ Exception -> 0x2701 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x2701 }
            r7.<init>()     // Catch:{ Exception -> 0x2701 }
            r5.media = r7     // Catch:{ Exception -> 0x2701 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2701 }
            java.util.ArrayList r8 = r12.attributes     // Catch:{ Exception -> 0x2701 }
            r7.attributes = r8     // Catch:{ Exception -> 0x2701 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2701 }
            r8 = r19
            r7.caption = r8     // Catch:{ Exception -> 0x26ed }
            java.util.ArrayList r7 = r12.thumbs     // Catch:{ Exception -> 0x26ed }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r10.getThumbForSecretChat(r7)     // Catch:{ Exception -> 0x26ed }
            if (r7 == 0) goto L_0x26a9
            im.bclpbkiauv.messenger.ImageLoader.fillPhotoSizeWithBytes(r7)     // Catch:{ Exception -> 0x26ed }
            r69 = r8
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x2697 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8     // Catch:{ Exception -> 0x2697 }
            r19 = r13
            byte[] r13 = r7.bytes     // Catch:{ Exception -> 0x26dd }
            r8.thumb = r13     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x26dd }
            int r13 = r7.h     // Catch:{ Exception -> 0x26dd }
            r8.thumb_h = r13     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x26dd }
            int r13 = r7.w     // Catch:{ Exception -> 0x26dd }
            r8.thumb_w = r13     // Catch:{ Exception -> 0x26dd }
            r56 = r7
            goto L_0x26c0
        L_0x2697:
            r0 = move-exception
            r19 = r13
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x26a9:
            r69 = r8
            r19 = r13
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r8 = r5.media     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r8 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8     // Catch:{ Exception -> 0x26dd }
            r56 = r7
            r13 = 0
            byte[] r7 = new byte[r13]     // Catch:{ Exception -> 0x26dd }
            r8.thumb = r7     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x26dd }
            r7.thumb_h = r13     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x26dd }
            r7.thumb_w = r13     // Catch:{ Exception -> 0x26dd }
        L_0x26c0:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x26dd }
            java.lang.String r8 = r12.mime_type     // Catch:{ Exception -> 0x26dd }
            r7.mime_type = r8     // Catch:{ Exception -> 0x26dd }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x26dd }
            int r8 = r12.size     // Catch:{ Exception -> 0x26dd }
            r7.size = r8     // Catch:{ Exception -> 0x26dd }
            r6.originalPath = r11     // Catch:{ Exception -> 0x26dd }
            r10.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x26dd }
            r13 = r67
            r45 = r3
            r48 = r12
            r8 = r49
            r7 = r52
            goto L_0x2ae8
        L_0x26dd:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x26ed:
            r0 = move-exception
            r19 = r13
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r6
            r69 = r8
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2701:
            r0 = move-exception
            r69 = r19
            r19 = r13
            r1 = r71
            r4 = r0
            r45 = r3
            r21 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2715:
            r0 = move-exception
            r69 = r19
            r19 = r13
            r1 = r71
            r4 = r0
            r45 = r3
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2727:
            r69 = r19
            r19 = r13
            r13 = r67
            r6 = r69
            r45 = r3
            goto L_0x2917
        L_0x2733:
            r6 = r19
            r19 = r13
        L_0x2737:
            boolean r7 = im.bclpbkiauv.messenger.MessageObject.isStickerDocument(r12)     // Catch:{ Exception -> 0x2931 }
            if (r7 != 0) goto L_0x28a2
            boolean r7 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r12)     // Catch:{ Exception -> 0x2931 }
            if (r7 == 0) goto L_0x2749
            r13 = r67
            r45 = r3
            goto L_0x28a6
        L_0x2749:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x2931 }
            r7.<init>()     // Catch:{ Exception -> 0x2931 }
            r5.media = r7     // Catch:{ Exception -> 0x2931 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2931 }
            java.util.ArrayList r13 = r12.attributes     // Catch:{ Exception -> 0x2931 }
            r7.attributes = r13     // Catch:{ Exception -> 0x2931 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2931 }
            r7.caption = r6     // Catch:{ Exception -> 0x2931 }
            java.util.ArrayList r7 = r12.thumbs     // Catch:{ Exception -> 0x2931 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r10.getThumbForSecretChat(r7)     // Catch:{ Exception -> 0x2931 }
            if (r7 == 0) goto L_0x2781
            im.bclpbkiauv.messenger.ImageLoader.fillPhotoSizeWithBytes(r7)     // Catch:{ Exception -> 0x277e }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r13 = r5.media     // Catch:{ Exception -> 0x277e }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r13 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r13     // Catch:{ Exception -> 0x277e }
            r45 = r3
            byte[] r3 = r7.bytes     // Catch:{ Exception -> 0x2923 }
            r13.thumb = r3     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            int r13 = r7.h     // Catch:{ Exception -> 0x2923 }
            r3.thumb_h = r13     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            int r13 = r7.w     // Catch:{ Exception -> 0x2923 }
            r3.thumb_w = r13     // Catch:{ Exception -> 0x2923 }
            r69 = r7
            goto L_0x2796
        L_0x277e:
            r0 = move-exception
            goto L_0x2934
        L_0x2781:
            r45 = r3
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2892 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3     // Catch:{ Exception -> 0x2892 }
            r69 = r7
            r13 = 0
            byte[] r7 = new byte[r13]     // Catch:{ Exception -> 0x2892 }
            r3.thumb = r7     // Catch:{ Exception -> 0x2892 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2892 }
            r3.thumb_h = r13     // Catch:{ Exception -> 0x2892 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2892 }
            r3.thumb_w = r13     // Catch:{ Exception -> 0x2892 }
        L_0x2796:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2892 }
            int r7 = r12.size     // Catch:{ Exception -> 0x2892 }
            r3.size = r7     // Catch:{ Exception -> 0x2892 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2892 }
            java.lang.String r7 = r12.mime_type     // Catch:{ Exception -> 0x2892 }
            r3.mime_type = r7     // Catch:{ Exception -> 0x2892 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x2892 }
            if (r3 != 0) goto L_0x283e
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r3 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x282c }
            r3.<init>(r14)     // Catch:{ Exception -> 0x282c }
            r3.originalPath = r11     // Catch:{ Exception -> 0x2818 }
            r3.sendEncryptedRequest = r5     // Catch:{ Exception -> 0x2818 }
            r7 = 2
            r3.type = r7     // Catch:{ Exception -> 0x2818 }
            r3.obj = r9     // Catch:{ Exception -> 0x2818 }
            if (r52 == 0) goto L_0x27d7
            r7 = r52
            boolean r13 = r7.containsKey(r8)     // Catch:{ Exception -> 0x27c5 }
            if (r13 == 0) goto L_0x27d9
            java.lang.Object r8 = r7.get(r8)     // Catch:{ Exception -> 0x27c5 }
            r3.parentObject = r8     // Catch:{ Exception -> 0x27c5 }
            goto L_0x27db
        L_0x27c5:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r21 = r3
            r69 = r6
            r52 = r7
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x27d7:
            r7 = r52
        L_0x27d9:
            r3.parentObject = r1     // Catch:{ Exception -> 0x2804 }
        L_0x27db:
            r3.encryptedChat = r2     // Catch:{ Exception -> 0x2804 }
            r8 = 1
            r3.performMediaUpload = r8     // Catch:{ Exception -> 0x2804 }
            r13 = r67
            if (r13 == 0) goto L_0x27f4
            int r8 = r67.length()     // Catch:{ Exception -> 0x27c5 }
            if (r8 <= 0) goto L_0x27f4
            r8 = r18
            boolean r8 = r13.startsWith(r8)     // Catch:{ Exception -> 0x27c5 }
            if (r8 == 0) goto L_0x27f4
            r3.httpLocation = r13     // Catch:{ Exception -> 0x27c5 }
        L_0x27f4:
            if (r4 == 0) goto L_0x27f8
            r8 = 1
            goto L_0x27f9
        L_0x27f8:
            r8 = 0
        L_0x27f9:
            r3.scheduled = r8     // Catch:{ Exception -> 0x27c5 }
            r10.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x27c5 }
            r21 = r3
            r52 = r7
            goto L_0x2876
        L_0x2804:
            r0 = move-exception
            r13 = r67
            r1 = r71
            r4 = r0
            r21 = r3
            r69 = r6
            r52 = r7
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2818:
            r0 = move-exception
            r13 = r67
            r7 = r52
            r1 = r71
            r4 = r0
            r21 = r3
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x282c:
            r0 = move-exception
            r13 = r67
            r7 = r52
            r1 = r71
            r4 = r0
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x283e:
            r13 = r67
            r7 = r52
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x2882 }
            r3.<init>()     // Catch:{ Exception -> 0x2882 }
            r52 = r7
            long r7 = r12.id     // Catch:{ Exception -> 0x2923 }
            r3.id = r7     // Catch:{ Exception -> 0x2923 }
            long r7 = r12.access_hash     // Catch:{ Exception -> 0x2923 }
            r3.access_hash = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2923 }
            byte[] r8 = r12.key     // Catch:{ Exception -> 0x2923 }
            r7.key = r8     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2923 }
            byte[] r8 = r12.iv     // Catch:{ Exception -> 0x2923 }
            r7.iv = r8     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.messenger.SecretChatHelper r7 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r9.messageOwner     // Catch:{ Exception -> 0x2923 }
            r18 = 0
            r56 = r7
            r57 = r5
            r58 = r8
            r59 = r2
            r60 = r3
            r61 = r18
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2923 }
        L_0x2876:
            r69 = r6
            r48 = r12
            r6 = r21
            r8 = r49
            r7 = r52
            goto L_0x2ae8
        L_0x2882:
            r0 = move-exception
            r52 = r7
            r1 = r71
            r4 = r0
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2892:
            r0 = move-exception
            r13 = r67
            r1 = r71
            r4 = r0
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x28a2:
            r13 = r67
            r45 = r3
        L_0x28a6:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x2923 }
            r3.<init>()     // Catch:{ Exception -> 0x2923 }
            r5.media = r3     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            long r7 = r12.id     // Catch:{ Exception -> 0x2923 }
            r3.id = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            int r7 = r12.date     // Catch:{ Exception -> 0x2923 }
            r3.date = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            long r7 = r12.access_hash     // Catch:{ Exception -> 0x2923 }
            r3.access_hash = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            java.lang.String r7 = r12.mime_type     // Catch:{ Exception -> 0x2923 }
            r3.mime_type = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            int r7 = r12.size     // Catch:{ Exception -> 0x2923 }
            r3.size = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            int r7 = r12.dc_id     // Catch:{ Exception -> 0x2923 }
            r3.dc_id = r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r5.media     // Catch:{ Exception -> 0x2923 }
            java.util.ArrayList r7 = r12.attributes     // Catch:{ Exception -> 0x2923 }
            r3.attributes = r7     // Catch:{ Exception -> 0x2923 }
            java.util.ArrayList r3 = r12.thumbs     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r10.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x2923 }
            if (r3 == 0) goto L_0x28e6
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r7 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r7     // Catch:{ Exception -> 0x2923 }
            r7.thumb = r3     // Catch:{ Exception -> 0x2923 }
            goto L_0x28fb
        L_0x28e6:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r7 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$TL_photoSizeEmpty r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x2923 }
            r8.<init>()     // Catch:{ Exception -> 0x2923 }
            r7.thumb = r8     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r7 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r7     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r7.thumb     // Catch:{ Exception -> 0x2923 }
            java.lang.String r8 = "s"
            r7.type = r8     // Catch:{ Exception -> 0x2923 }
        L_0x28fb:
            im.bclpbkiauv.messenger.SecretChatHelper r7 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x2923 }
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r9.messageOwner     // Catch:{ Exception -> 0x2923 }
            r18 = 0
            r24 = 0
            r56 = r7
            r57 = r5
            r58 = r8
            r59 = r2
            r60 = r18
            r61 = r24
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2923 }
        L_0x2917:
            r69 = r6
            r48 = r12
            r6 = r21
            r8 = r49
            r7 = r52
            goto L_0x2ae8
        L_0x2923:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2931:
            r0 = move-exception
            r13 = r67
        L_0x2934:
            r45 = r3
            r1 = r71
            r4 = r0
            r69 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            goto L_0x3281
        L_0x2943:
            r13 = r67
            r11 = r69
            r22 = r1
            r45 = r3
            r46 = r6
            r6 = r14
            r3 = r18
            r12 = r48
            r19 = r53
            r14 = r65
            r1 = r78
        L_0x2958:
            r48 = r12
            r7 = r49
            java.util.ArrayList r12 = r7.sizes     // Catch:{ Exception -> 0x2bfc }
            r18 = r3
            r3 = 0
            java.lang.Object r12 = r12.get(r3)     // Catch:{ Exception -> 0x2bfc }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r12 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r12     // Catch:{ Exception -> 0x2bfc }
            r3 = r12
            java.util.ArrayList r12 = r7.sizes     // Catch:{ Exception -> 0x2bfc }
            java.util.ArrayList r13 = r7.sizes     // Catch:{ Exception -> 0x2bf8 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x2bf8 }
            r24 = 1
            int r13 = r13 + -1
            java.lang.Object r12 = r12.get(r13)     // Catch:{ Exception -> 0x2bf8 }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r12 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r12     // Catch:{ Exception -> 0x2bf8 }
            im.bclpbkiauv.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x2bf8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x2bf8 }
            r13.<init>()     // Catch:{ Exception -> 0x2bf8 }
            r5.media = r13     // Catch:{ Exception -> 0x2bf8 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r13 = r5.media     // Catch:{ Exception -> 0x2bf8 }
            r13.caption = r6     // Catch:{ Exception -> 0x2bf8 }
            byte[] r13 = r3.bytes     // Catch:{ Exception -> 0x2bf8 }
            if (r13 == 0) goto L_0x29bb
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r13 = r5.media     // Catch:{ Exception -> 0x29a9 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r13     // Catch:{ Exception -> 0x29a9 }
            r69 = r6
            byte[] r6 = r3.bytes     // Catch:{ Exception -> 0x2999 }
            r13.thumb = r6     // Catch:{ Exception -> 0x2999 }
            r49 = r7
            goto L_0x29c8
        L_0x2999:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r49 = r7
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x29a9:
            r0 = move-exception
            r69 = r6
            r1 = r71
            r4 = r0
            r49 = r7
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x29bb:
            r69 = r6
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2be3 }
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r6 = (im.bclpbkiauv.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r6     // Catch:{ Exception -> 0x2be3 }
            r49 = r7
            r13 = 0
            byte[] r7 = new byte[r13]     // Catch:{ Exception -> 0x2bd0 }
            r6.thumb = r7     // Catch:{ Exception -> 0x2bd0 }
        L_0x29c8:
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2bd0 }
            int r7 = r3.h     // Catch:{ Exception -> 0x2bd0 }
            r6.thumb_h = r7     // Catch:{ Exception -> 0x2bd0 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2bd0 }
            int r7 = r3.w     // Catch:{ Exception -> 0x2bd0 }
            r6.thumb_w = r7     // Catch:{ Exception -> 0x2bd0 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2bd0 }
            int r7 = r12.w     // Catch:{ Exception -> 0x2bd0 }
            r6.w = r7     // Catch:{ Exception -> 0x2bd0 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2bd0 }
            int r7 = r12.h     // Catch:{ Exception -> 0x2bd0 }
            r6.h = r7     // Catch:{ Exception -> 0x2bd0 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r6 = r5.media     // Catch:{ Exception -> 0x2bd0 }
            int r7 = r12.size     // Catch:{ Exception -> 0x2bd0 }
            r6.size = r7     // Catch:{ Exception -> 0x2bd0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r6 = r12.location     // Catch:{ Exception -> 0x2bd0 }
            byte[] r6 = r6.key     // Catch:{ Exception -> 0x2bd0 }
            if (r6 == 0) goto L_0x2a40
            int r6 = (r46 > r16 ? 1 : (r46 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x29f1
            goto L_0x2a40
        L_0x29f1:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x2a32 }
            r6.<init>()     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r7 = r12.location     // Catch:{ Exception -> 0x2a32 }
            long r7 = r7.volume_id     // Catch:{ Exception -> 0x2a32 }
            r6.id = r7     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r7 = r12.location     // Catch:{ Exception -> 0x2a32 }
            long r7 = r7.secret     // Catch:{ Exception -> 0x2a32 }
            r6.access_hash = r7     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r12.location     // Catch:{ Exception -> 0x2a32 }
            byte[] r8 = r8.key     // Catch:{ Exception -> 0x2a32 }
            r7.key = r8     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r7 = r5.media     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r12.location     // Catch:{ Exception -> 0x2a32 }
            byte[] r8 = r8.iv     // Catch:{ Exception -> 0x2a32 }
            r7.iv = r8     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.messenger.SecretChatHelper r7 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x2a32 }
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r9.messageOwner     // Catch:{ Exception -> 0x2a32 }
            r13 = 0
            r56 = r7
            r57 = r5
            r58 = r8
            r59 = r2
            r60 = r6
            r61 = r13
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2a32 }
            r13 = r67
            r8 = r49
            r7 = r52
            goto L_0x2ae6
        L_0x2a32:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2a40:
            if (r21 != 0) goto L_0x2a94
            im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage r6 = new im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x2a84 }
            r6.<init>(r14)     // Catch:{ Exception -> 0x2a84 }
            r6.encryptedChat = r2     // Catch:{ Exception -> 0x2a72 }
            r7 = 0
            r6.type = r7     // Catch:{ Exception -> 0x2a72 }
            r6.originalPath = r11     // Catch:{ Exception -> 0x2a72 }
            r6.sendEncryptedRequest = r5     // Catch:{ Exception -> 0x2a72 }
            r6.obj = r9     // Catch:{ Exception -> 0x2a72 }
            if (r52 == 0) goto L_0x2a63
            r7 = r52
            boolean r13 = r7.containsKey(r8)     // Catch:{ Exception -> 0x2aaf }
            if (r13 == 0) goto L_0x2a65
            java.lang.Object r8 = r7.get(r8)     // Catch:{ Exception -> 0x2aaf }
            r6.parentObject = r8     // Catch:{ Exception -> 0x2aaf }
            goto L_0x2a67
        L_0x2a63:
            r7 = r52
        L_0x2a65:
            r6.parentObject = r1     // Catch:{ Exception -> 0x2aaf }
        L_0x2a67:
            r8 = 1
            r6.performMediaUpload = r8     // Catch:{ Exception -> 0x2aaf }
            if (r4 == 0) goto L_0x2a6e
            r8 = 1
            goto L_0x2a6f
        L_0x2a6e:
            r8 = 0
        L_0x2a6f:
            r6.scheduled = r8     // Catch:{ Exception -> 0x2aaf }
            goto L_0x2a98
        L_0x2a72:
            r0 = move-exception
            r7 = r52
            r1 = r71
            r4 = r0
            r21 = r6
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2a84:
            r0 = move-exception
            r7 = r52
            r1 = r71
            r4 = r0
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2a94:
            r7 = r52
            r6 = r21
        L_0x2a98:
            boolean r8 = android.text.TextUtils.isEmpty(r67)     // Catch:{ Exception -> 0x2bb9 }
            if (r8 != 0) goto L_0x2ac1
            r13 = r67
            r8 = r18
            boolean r8 = r13.startsWith(r8)     // Catch:{ Exception -> 0x2aaf }
            if (r8 == 0) goto L_0x2ac3
            r6.httpLocation = r13     // Catch:{ Exception -> 0x2aaf }
            r56 = r3
            r8 = r49
            goto L_0x2add
        L_0x2aaf:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r21 = r6
            r52 = r7
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2ac1:
            r13 = r67
        L_0x2ac3:
            r8 = r49
            java.util.ArrayList r1 = r8.sizes     // Catch:{ Exception -> 0x2b6e }
            r56 = r3
            java.util.ArrayList r3 = r8.sizes     // Catch:{ Exception -> 0x2b6e }
            int r3 = r3.size()     // Catch:{ Exception -> 0x2b6e }
            r18 = 1
            int r3 = r3 + -1
            java.lang.Object r1 = r1.get(r3)     // Catch:{ Exception -> 0x2b6e }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r1 = (im.bclpbkiauv.tgnet.TLRPC.PhotoSize) r1     // Catch:{ Exception -> 0x2b6e }
            r6.photoSize = r1     // Catch:{ Exception -> 0x2b6e }
            r6.locationParent = r8     // Catch:{ Exception -> 0x2b6e }
        L_0x2add:
            int r1 = (r46 > r16 ? 1 : (r46 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x2ae4
            r10.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x2af5 }
        L_0x2ae4:
            r21 = r6
        L_0x2ae6:
            r6 = r21
        L_0x2ae8:
            int r1 = (r46 > r16 ? 1 : (r46 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x2b85
            im.bclpbkiauv.tgnet.TLObject r1 = r6.sendEncryptedRequest     // Catch:{ Exception -> 0x2b6e }
            if (r1 == 0) goto L_0x2b09
            im.bclpbkiauv.tgnet.TLObject r1 = r6.sendEncryptedRequest     // Catch:{ Exception -> 0x2af5 }
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r1     // Catch:{ Exception -> 0x2af5 }
            goto L_0x2b10
        L_0x2af5:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r21 = r6
            r52 = r7
            r49 = r8
            r26 = r11
            r11 = r36
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2b09:
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x2b6e }
            r1.<init>()     // Catch:{ Exception -> 0x2b6e }
            r6.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x2b6e }
        L_0x2b10:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r3 = r6.messageObjects     // Catch:{ Exception -> 0x2b6e }
            r3.add(r9)     // Catch:{ Exception -> 0x2b6e }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r3 = r6.messages     // Catch:{ Exception -> 0x2b6e }
            r12 = r36
            r3.add(r12)     // Catch:{ Exception -> 0x2b59 }
            java.util.ArrayList<java.lang.String> r3 = r6.originalPaths     // Catch:{ Exception -> 0x2b59 }
            r3.add(r11)     // Catch:{ Exception -> 0x2b59 }
            r3 = 1
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x2b59 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage> r3 = r1.messages     // Catch:{ Exception -> 0x2b59 }
            r3.add(r5)     // Catch:{ Exception -> 0x2b59 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x2b59 }
            r3.<init>()     // Catch:{ Exception -> 0x2b59 }
            r18 = r5
            r49 = r8
            r5 = r45
            r8 = 3
            if (r5 != r8) goto L_0x2b39
            r16 = 1
        L_0x2b39:
            r52 = r7
            r7 = r16
            r3.id = r7     // Catch:{ Exception -> 0x2b48 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputEncryptedFile> r7 = r1.files     // Catch:{ Exception -> 0x2b48 }
            r7.add(r3)     // Catch:{ Exception -> 0x2b48 }
            r10.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x2b48 }
            goto L_0x2b8f
        L_0x2b48:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r45 = r5
            r21 = r6
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2b59:
            r0 = move-exception
            r52 = r7
            r49 = r8
            r5 = r45
            r1 = r71
            r4 = r0
            r21 = r6
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2b6e:
            r0 = move-exception
            r52 = r7
            r49 = r8
            r12 = r36
            r5 = r45
            r1 = r71
            r4 = r0
            r21 = r6
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2b85:
            r18 = r5
            r52 = r7
            r49 = r8
            r12 = r36
            r5 = r45
        L_0x2b8f:
            r1 = r71
            if (r1 != 0) goto L_0x2bab
            im.bclpbkiauv.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x2b9c }
            r7 = 0
            r3.cleanDraft(r14, r7)     // Catch:{ Exception -> 0x2b9c }
            goto L_0x2bab
        L_0x2b9c:
            r0 = move-exception
            r4 = r0
            r45 = r5
            r21 = r6
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2bab:
            r45 = r5
            r21 = r6
            r26 = r11
            r11 = r33
            r7 = r35
            r5 = r52
            goto L_0x3083
        L_0x2bb9:
            r0 = move-exception
            r13 = r67
            r52 = r7
            r12 = r36
            r5 = r45
            r1 = r71
            r4 = r0
            r21 = r6
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2bd0:
            r0 = move-exception
            r13 = r67
            r12 = r36
            r5 = r45
            r1 = r71
            r4 = r0
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2be3:
            r0 = move-exception
            r13 = r67
            r49 = r7
            r12 = r36
            r5 = r45
            r1 = r71
            r4 = r0
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2bf8:
            r0 = move-exception
            r13 = r67
            goto L_0x2bfd
        L_0x2bfc:
            r0 = move-exception
        L_0x2bfd:
            r69 = r6
            r49 = r7
            r12 = r36
            r5 = r45
            r1 = r71
            r4 = r0
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2c11:
            r0 = move-exception
            r13 = r67
            r11 = r69
            r5 = r3
            r69 = r14
            r9 = r43
            r19 = r53
            r14 = r65
            r1 = r71
            r4 = r0
            r45 = r5
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2c2d:
            r0 = move-exception
            r13 = r67
            r11 = r69
            r5 = r3
            r69 = r14
            r9 = r43
            r12 = r50
            r19 = r53
            r14 = r65
            r1 = r71
            r4 = r0
            r45 = r5
            r26 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2c4b:
            r25 = r59
            r51 = r74
            r52 = r77
            r64 = r1
            r44 = r2
            r46 = r3
            r3 = r7
            r4 = r9
            r33 = r12
            r2 = r13
            r48 = r22
            r49 = r36
            r9 = r43
            r13 = r67
            r1 = r71
            r12 = r11
            r22 = r15
            r11 = r23
            r23 = r31
            r14 = r5
            r5 = r8
            r6 = 4
            if (r5 != r6) goto L_0x2da9
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_forwardMessages r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x2d9c }
            r6.<init>()     // Catch:{ Exception -> 0x2d9c }
            r7 = r44
            r6.to_peer = r7     // Catch:{ Exception -> 0x2d90 }
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r1.messageOwner     // Catch:{ Exception -> 0x2d90 }
            boolean r8 = r8.with_my_score     // Catch:{ Exception -> 0x2d90 }
            r6.with_my_score = r8     // Catch:{ Exception -> 0x2d90 }
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r1.messageOwner     // Catch:{ Exception -> 0x2d90 }
            int r8 = r8.ttl     // Catch:{ Exception -> 0x2d90 }
            if (r8 == 0) goto L_0x2ccb
            im.bclpbkiauv.messenger.MessagesController r8 = r55.getMessagesController()     // Catch:{ Exception -> 0x2cc4 }
            r26 = r11
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r1.messageOwner     // Catch:{ Exception -> 0x2cb8 }
            int r11 = r11.ttl     // Catch:{ Exception -> 0x2cb8 }
            int r11 = -r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x2cb8 }
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = r8.getChat(r11)     // Catch:{ Exception -> 0x2cb8 }
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel r11 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x2cb8 }
            r11.<init>()     // Catch:{ Exception -> 0x2cb8 }
            r6.from_peer = r11     // Catch:{ Exception -> 0x2cb8 }
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r11 = r6.from_peer     // Catch:{ Exception -> 0x2cb8 }
            im.bclpbkiauv.tgnet.TLRPC$Message r13 = r1.messageOwner     // Catch:{ Exception -> 0x2cb8 }
            int r13 = r13.ttl     // Catch:{ Exception -> 0x2cb8 }
            int r13 = -r13
            r11.channel_id = r13     // Catch:{ Exception -> 0x2cb8 }
            if (r8 == 0) goto L_0x2cb5
            im.bclpbkiauv.tgnet.TLRPC$InputPeer r11 = r6.from_peer     // Catch:{ Exception -> 0x2cb8 }
            r37 = r2
            long r1 = r8.access_hash     // Catch:{ Exception -> 0x2d82 }
            r11.access_hash = r1     // Catch:{ Exception -> 0x2d82 }
            goto L_0x2cb7
        L_0x2cb5:
            r37 = r2
        L_0x2cb7:
            goto L_0x2cd6
        L_0x2cb8:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r45 = r5
            r3 = r7
            r11 = r12
            r12 = r48
            goto L_0x3281
        L_0x2cc4:
            r0 = move-exception
            r26 = r11
            r1 = r71
            goto L_0x2d93
        L_0x2ccb:
            r37 = r2
            r26 = r11
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x2d82 }
            r1.<init>()     // Catch:{ Exception -> 0x2d82 }
            r6.from_peer = r1     // Catch:{ Exception -> 0x2d82 }
        L_0x2cd6:
            if (r75 == 0) goto L_0x2cf7
            int r1 = r10.currentAccount     // Catch:{ Exception -> 0x2d82 }
            android.content.SharedPreferences r1 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r1)     // Catch:{ Exception -> 0x2d82 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2d82 }
            r2.<init>()     // Catch:{ Exception -> 0x2d82 }
            r2.append(r3)     // Catch:{ Exception -> 0x2d82 }
            r2.append(r14)     // Catch:{ Exception -> 0x2d82 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x2d82 }
            r3 = 0
            boolean r1 = r1.getBoolean(r2, r3)     // Catch:{ Exception -> 0x2d82 }
            if (r1 == 0) goto L_0x2cf5
            goto L_0x2cf7
        L_0x2cf5:
            r1 = 0
            goto L_0x2cf8
        L_0x2cf7:
            r1 = 1
        L_0x2cf8:
            r6.silent = r1     // Catch:{ Exception -> 0x2d82 }
            if (r4 == 0) goto L_0x2d04
            r6.schedule_date = r4     // Catch:{ Exception -> 0x2d82 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x2d82 }
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r6.flags = r1     // Catch:{ Exception -> 0x2d82 }
        L_0x2d04:
            java.util.ArrayList<java.lang.Long> r1 = r6.random_id     // Catch:{ Exception -> 0x2d82 }
            long r2 = r12.random_id     // Catch:{ Exception -> 0x2d82 }
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ Exception -> 0x2d82 }
            r1.add(r2)     // Catch:{ Exception -> 0x2d82 }
            int r1 = r71.getId()     // Catch:{ Exception -> 0x2d82 }
            if (r1 < 0) goto L_0x2d25
            java.util.ArrayList<java.lang.Integer> r1 = r6.id     // Catch:{ Exception -> 0x2d82 }
            int r2 = r71.getId()     // Catch:{ Exception -> 0x2d82 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x2d82 }
            r1.add(r2)     // Catch:{ Exception -> 0x2d82 }
            r1 = r71
            goto L_0x2d50
        L_0x2d25:
            r1 = r71
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner     // Catch:{ Exception -> 0x2d76 }
            int r2 = r2.fwd_msg_id     // Catch:{ Exception -> 0x2d76 }
            if (r2 == 0) goto L_0x2d3b
            java.util.ArrayList<java.lang.Integer> r2 = r6.id     // Catch:{ Exception -> 0x2d76 }
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner     // Catch:{ Exception -> 0x2d76 }
            int r3 = r3.fwd_msg_id     // Catch:{ Exception -> 0x2d76 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x2d76 }
            r2.add(r3)     // Catch:{ Exception -> 0x2d76 }
            goto L_0x2d50
        L_0x2d3b:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner     // Catch:{ Exception -> 0x2d76 }
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x2d76 }
            if (r2 == 0) goto L_0x2d50
            java.util.ArrayList<java.lang.Integer> r2 = r6.id     // Catch:{ Exception -> 0x2d76 }
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner     // Catch:{ Exception -> 0x2d76 }
            im.bclpbkiauv.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from     // Catch:{ Exception -> 0x2d76 }
            int r3 = r3.channel_post     // Catch:{ Exception -> 0x2d76 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x2d76 }
            r2.add(r3)     // Catch:{ Exception -> 0x2d76 }
        L_0x2d50:
            r2 = 0
            r3 = 0
            if (r4 == 0) goto L_0x2d56
            r8 = 1
            goto L_0x2d57
        L_0x2d56:
            r8 = 0
        L_0x2d57:
            r56 = r55
            r57 = r6
            r58 = r9
            r59 = r2
            r60 = r3
            r61 = r78
            r62 = r8
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2d76 }
            r45 = r5
            r44 = r7
            r11 = r33
            r7 = r35
            r2 = r37
            r5 = r52
            goto L_0x3083
        L_0x2d76:
            r0 = move-exception
            r4 = r0
            r45 = r5
            r3 = r7
            r11 = r12
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2d82:
            r0 = move-exception
            r1 = r71
            r4 = r0
            r45 = r5
            r3 = r7
            r11 = r12
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2d90:
            r0 = move-exception
            r26 = r11
        L_0x2d93:
            r4 = r0
            r45 = r5
            r3 = r7
            r11 = r12
            r12 = r48
            goto L_0x3281
        L_0x2d9c:
            r0 = move-exception
            r26 = r11
            r4 = r0
            r45 = r5
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2da9:
            r37 = r2
            r26 = r11
            r7 = r44
            r2 = 9
            if (r5 != r2) goto L_0x2ea3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendInlineBotResult r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x2e92 }
            r2.<init>()     // Catch:{ Exception -> 0x2e92 }
            r2.peer = r7     // Catch:{ Exception -> 0x2e92 }
            r45 = r5
            long r5 = r12.random_id     // Catch:{ Exception -> 0x2e83 }
            r2.random_id = r5     // Catch:{ Exception -> 0x2e83 }
            r6 = r58
            r5 = r52
            boolean r6 = r5.containsKey(r6)     // Catch:{ Exception -> 0x2e74 }
            if (r6 != 0) goto L_0x2dcc
            r6 = 1
            goto L_0x2dcd
        L_0x2dcc:
            r6 = 0
        L_0x2dcd:
            r2.hide_via = r6     // Catch:{ Exception -> 0x2e74 }
            int r6 = r12.reply_to_msg_id     // Catch:{ Exception -> 0x2e74 }
            if (r6 == 0) goto L_0x2dea
            int r6 = r2.flags     // Catch:{ Exception -> 0x2dde }
            r8 = 1
            r6 = r6 | r8
            r2.flags = r6     // Catch:{ Exception -> 0x2dde }
            int r6 = r12.reply_to_msg_id     // Catch:{ Exception -> 0x2dde }
            r2.reply_to_msg_id = r6     // Catch:{ Exception -> 0x2dde }
            goto L_0x2dea
        L_0x2dde:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r3 = r7
            r11 = r12
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2dea:
            if (r75 == 0) goto L_0x2e0b
            int r6 = r10.currentAccount     // Catch:{ Exception -> 0x2dde }
            android.content.SharedPreferences r6 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r6)     // Catch:{ Exception -> 0x2dde }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2dde }
            r8.<init>()     // Catch:{ Exception -> 0x2dde }
            r8.append(r3)     // Catch:{ Exception -> 0x2dde }
            r8.append(r14)     // Catch:{ Exception -> 0x2dde }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x2dde }
            r8 = 0
            boolean r3 = r6.getBoolean(r3, r8)     // Catch:{ Exception -> 0x2dde }
            if (r3 == 0) goto L_0x2e09
            goto L_0x2e0b
        L_0x2e09:
            r3 = 0
            goto L_0x2e0c
        L_0x2e0b:
            r3 = 1
        L_0x2e0c:
            r2.silent = r3     // Catch:{ Exception -> 0x2e74 }
            if (r4 == 0) goto L_0x2e18
            r2.schedule_date = r4     // Catch:{ Exception -> 0x2dde }
            int r3 = r2.flags     // Catch:{ Exception -> 0x2dde }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r2.flags = r3     // Catch:{ Exception -> 0x2dde }
        L_0x2e18:
            r3 = r57
            java.lang.Object r3 = r5.get(r3)     // Catch:{ Exception -> 0x2e74 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x2e74 }
            java.lang.Long r3 = im.bclpbkiauv.messenger.Utilities.parseLong(r3)     // Catch:{ Exception -> 0x2e74 }
            r36 = r12
            long r11 = r3.longValue()     // Catch:{ Exception -> 0x2e67 }
            r2.query_id = r11     // Catch:{ Exception -> 0x2e67 }
            java.lang.String r3 = "id"
            java.lang.Object r3 = r5.get(r3)     // Catch:{ Exception -> 0x2e67 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x2e67 }
            r2.id = r3     // Catch:{ Exception -> 0x2e67 }
            if (r1 != 0) goto L_0x2e43
            r3 = 1
            r2.clear_draft = r3     // Catch:{ Exception -> 0x2e67 }
            im.bclpbkiauv.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x2e67 }
            r6 = 0
            r3.cleanDraft(r14, r6)     // Catch:{ Exception -> 0x2e67 }
        L_0x2e43:
            r3 = 0
            r6 = 0
            if (r4 == 0) goto L_0x2e49
            r8 = 1
            goto L_0x2e4a
        L_0x2e49:
            r8 = 0
        L_0x2e4a:
            r56 = r55
            r57 = r2
            r58 = r9
            r59 = r3
            r60 = r6
            r61 = r78
            r62 = r8
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2e67 }
            r44 = r7
            r11 = r33
            r7 = r35
            r12 = r36
            r2 = r37
            goto L_0x3083
        L_0x2e67:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r3 = r7
            r11 = r36
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2e74:
            r0 = move-exception
            r36 = r12
            r4 = r0
            r52 = r5
            r3 = r7
            r11 = r36
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2e83:
            r0 = move-exception
            r36 = r12
            r5 = r52
            r4 = r0
            r3 = r7
            r11 = r36
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2e92:
            r0 = move-exception
            r45 = r5
            r36 = r12
            r5 = r52
            r4 = r0
            r3 = r7
            r11 = r36
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2ea3:
            r45 = r5
            r36 = r12
            r5 = r52
            r44 = r7
            r11 = r33
            r7 = r35
            r2 = r37
            goto L_0x3083
        L_0x2eb3:
            r8 = r56
            r25 = r59
            r45 = r64
            r51 = r74
            r64 = r1
            r46 = r3
            r3 = r7
            r4 = r9
            r33 = r12
            r37 = r13
            r48 = r22
            r26 = r23
            r23 = r31
            r9 = r43
            r1 = r71
            r7 = r2
            r22 = r15
            r14 = r5
            r6 = r36
            r5 = r77
            r36 = r11
        L_0x2ed9:
            if (r37 != 0) goto L_0x2fcd
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMessage r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x2fb9 }
            r2.<init>()     // Catch:{ Exception -> 0x2fb9 }
            r11 = r33
            r2.message = r11     // Catch:{ Exception -> 0x2fa5 }
            if (r1 != 0) goto L_0x2ee8
            r8 = 1
            goto L_0x2ee9
        L_0x2ee8:
            r8 = 0
        L_0x2ee9:
            r2.clear_draft = r8     // Catch:{ Exception -> 0x2fa5 }
            if (r75 == 0) goto L_0x2f1d
            int r8 = r10.currentAccount     // Catch:{ Exception -> 0x2f0c }
            android.content.SharedPreferences r8 = im.bclpbkiauv.messenger.MessagesController.getNotificationsSettings(r8)     // Catch:{ Exception -> 0x2f0c }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x2f0c }
            r12.<init>()     // Catch:{ Exception -> 0x2f0c }
            r12.append(r3)     // Catch:{ Exception -> 0x2f0c }
            r12.append(r14)     // Catch:{ Exception -> 0x2f0c }
            java.lang.String r3 = r12.toString()     // Catch:{ Exception -> 0x2f0c }
            r12 = 0
            boolean r3 = r8.getBoolean(r3, r12)     // Catch:{ Exception -> 0x2f0c }
            if (r3 == 0) goto L_0x2f0a
            goto L_0x2f1d
        L_0x2f0a:
            r3 = 0
            goto L_0x2f1e
        L_0x2f0c:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r49 = r6
            r3 = r7
            r33 = r11
            r11 = r36
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2f1d:
            r3 = 1
        L_0x2f1e:
            r2.silent = r3     // Catch:{ Exception -> 0x2fa5 }
            r2.peer = r7     // Catch:{ Exception -> 0x2fa5 }
            r49 = r6
            r44 = r7
            r12 = r36
            long r6 = r12.random_id     // Catch:{ Exception -> 0x2f94 }
            r2.random_id = r6     // Catch:{ Exception -> 0x2f94 }
            int r3 = r12.reply_to_msg_id     // Catch:{ Exception -> 0x2f94 }
            if (r3 == 0) goto L_0x2f4a
            int r3 = r2.flags     // Catch:{ Exception -> 0x2f3b }
            r6 = 1
            r3 = r3 | r6
            r2.flags = r3     // Catch:{ Exception -> 0x2f3b }
            int r3 = r12.reply_to_msg_id     // Catch:{ Exception -> 0x2f3b }
            r2.reply_to_msg_id = r3     // Catch:{ Exception -> 0x2f3b }
            goto L_0x2f4a
        L_0x2f3b:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r2 = r37
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2f4a:
            if (r70 != 0) goto L_0x2f4f
            r3 = 1
            r2.no_webpage = r3     // Catch:{ Exception -> 0x2f3b }
        L_0x2f4f:
            r3 = r72
            if (r3 == 0) goto L_0x2f62
            boolean r6 = r72.isEmpty()     // Catch:{ Exception -> 0x2f3b }
            if (r6 != 0) goto L_0x2f62
            r2.entities = r3     // Catch:{ Exception -> 0x2f3b }
            int r6 = r2.flags     // Catch:{ Exception -> 0x2f3b }
            r7 = 8
            r6 = r6 | r7
            r2.flags = r6     // Catch:{ Exception -> 0x2f3b }
        L_0x2f62:
            if (r4 == 0) goto L_0x2f6c
            r2.schedule_date = r4     // Catch:{ Exception -> 0x2f3b }
            int r6 = r2.flags     // Catch:{ Exception -> 0x2f3b }
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r2.flags = r6     // Catch:{ Exception -> 0x2f3b }
        L_0x2f6c:
            r6 = 0
            r7 = 0
            if (r4 == 0) goto L_0x2f72
            r8 = 1
            goto L_0x2f73
        L_0x2f72:
            r8 = 0
        L_0x2f73:
            r56 = r55
            r57 = r2
            r58 = r9
            r59 = r6
            r60 = r7
            r61 = r78
            r62 = r8
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x2f3b }
            if (r1 != 0) goto L_0x2f8e
            im.bclpbkiauv.messenger.MediaDataController r6 = r55.getMediaDataController()     // Catch:{ Exception -> 0x2f3b }
            r7 = 0
            r6.cleanDraft(r14, r7)     // Catch:{ Exception -> 0x2f3b }
        L_0x2f8e:
            r7 = r35
            r2 = r37
            goto L_0x3083
        L_0x2f94:
            r0 = move-exception
            r3 = r72
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r2 = r37
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2fa5:
            r0 = move-exception
            r3 = r72
            r49 = r6
            r12 = r36
            r4 = r0
            r52 = r5
            r3 = r7
            r33 = r11
            r11 = r12
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2fb9:
            r0 = move-exception
            r3 = r72
            r49 = r6
            r11 = r33
            r12 = r36
            r4 = r0
            r52 = r5
            r3 = r7
            r11 = r12
            r2 = r37
            r12 = r48
            goto L_0x3281
        L_0x2fcd:
            r3 = r72
            r49 = r6
            r44 = r7
            r11 = r33
            r12 = r36
            r2 = r37
            int r6 = r2.layer     // Catch:{ Exception -> 0x30aa }
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r6)     // Catch:{ Exception -> 0x30aa }
            r7 = 73
            if (r6 < r7) goto L_0x2ff6
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x2fe9 }
            r6.<init>()     // Catch:{ Exception -> 0x2fe9 }
            goto L_0x2ffb
        L_0x2fe9:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x2ff6:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage_layer45 r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x30aa }
            r6.<init>()     // Catch:{ Exception -> 0x30aa }
        L_0x2ffb:
            int r7 = r12.ttl     // Catch:{ Exception -> 0x30aa }
            r6.ttl = r7     // Catch:{ Exception -> 0x30aa }
            if (r3 == 0) goto L_0x300f
            boolean r7 = r72.isEmpty()     // Catch:{ Exception -> 0x2fe9 }
            if (r7 != 0) goto L_0x300f
            r6.entities = r3     // Catch:{ Exception -> 0x2fe9 }
            int r7 = r6.flags     // Catch:{ Exception -> 0x2fe9 }
            r7 = r7 | 128(0x80, float:1.794E-43)
            r6.flags = r7     // Catch:{ Exception -> 0x2fe9 }
        L_0x300f:
            long r3 = r12.reply_to_random_id     // Catch:{ Exception -> 0x30aa }
            int r7 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r7 == 0) goto L_0x3020
            long r3 = r12.reply_to_random_id     // Catch:{ Exception -> 0x2fe9 }
            r6.reply_to_random_id = r3     // Catch:{ Exception -> 0x2fe9 }
            int r3 = r6.flags     // Catch:{ Exception -> 0x2fe9 }
            r4 = 8
            r3 = r3 | r4
            r6.flags = r3     // Catch:{ Exception -> 0x2fe9 }
        L_0x3020:
            if (r5 == 0) goto L_0x3036
            java.lang.Object r3 = r5.get(r8)     // Catch:{ Exception -> 0x2fe9 }
            if (r3 == 0) goto L_0x3036
            java.lang.Object r3 = r5.get(r8)     // Catch:{ Exception -> 0x2fe9 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x2fe9 }
            r6.via_bot_name = r3     // Catch:{ Exception -> 0x2fe9 }
            int r3 = r6.flags     // Catch:{ Exception -> 0x2fe9 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r6.flags = r3     // Catch:{ Exception -> 0x2fe9 }
        L_0x3036:
            long r3 = r12.random_id     // Catch:{ Exception -> 0x30aa }
            r6.random_id = r3     // Catch:{ Exception -> 0x30aa }
            r6.message = r11     // Catch:{ Exception -> 0x30aa }
            r7 = r35
            if (r7 == 0) goto L_0x3058
            java.lang.String r3 = r7.url     // Catch:{ Exception -> 0x309b }
            if (r3 == 0) goto L_0x3058
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x309b }
            r3.<init>()     // Catch:{ Exception -> 0x309b }
            r6.media = r3     // Catch:{ Exception -> 0x309b }
            im.bclpbkiauv.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x309b }
            java.lang.String r4 = r7.url     // Catch:{ Exception -> 0x309b }
            r3.url = r4     // Catch:{ Exception -> 0x309b }
            int r3 = r6.flags     // Catch:{ Exception -> 0x309b }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r6.flags = r3     // Catch:{ Exception -> 0x309b }
            goto L_0x305f
        L_0x3058:
            im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x309b }
            r3.<init>()     // Catch:{ Exception -> 0x309b }
            r6.media = r3     // Catch:{ Exception -> 0x309b }
        L_0x305f:
            im.bclpbkiauv.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x309b }
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r9.messageOwner     // Catch:{ Exception -> 0x309b }
            r8 = 0
            r13 = 0
            r56 = r3
            r57 = r6
            r58 = r4
            r59 = r2
            r60 = r8
            r61 = r13
            r62 = r9
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x309b }
            if (r1 != 0) goto L_0x3082
            im.bclpbkiauv.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x309b }
            r4 = 0
            r3.cleanDraft(r14, r4)     // Catch:{ Exception -> 0x309b }
        L_0x3082:
        L_0x3083:
            r52 = r5
            r35 = r7
            r14 = r9
            r6 = r19
            r15 = r22
            r8 = r25
            r3 = r44
            r5 = r48
            r7 = r49
            r54 = r12
            r12 = r11
            r11 = r54
            goto L_0x32bd
        L_0x309b:
            r0 = move-exception
            r4 = r0
            r52 = r5
            r35 = r7
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x30aa:
            r0 = move-exception
            r7 = r35
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x30b9:
            r0 = move-exception
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r44 = r2
            r2 = r13
            r48 = r22
            r26 = r23
            r23 = r31
            r7 = r35
            r49 = r36
            r9 = r43
            r22 = r15
            r14 = r5
            r5 = r77
            r54 = r12
            r12 = r11
            r11 = r54
            r4 = r0
            r52 = r5
            r21 = r8
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x30e9:
            r0 = move-exception
            r25 = r59
            r45 = r64
            r1 = r71
            r51 = r74
            r44 = r2
            r2 = r13
            r48 = r22
            r26 = r23
            r23 = r31
            r7 = r35
            r49 = r36
            r9 = r43
            r22 = r15
            r14 = r5
            r5 = r77
            r54 = r12
            r12 = r11
            r11 = r54
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x3117:
            r0 = move-exception
            r25 = r59
            r1 = r71
            r44 = r2
            r45 = r3
            r49 = r7
            r2 = r13
            r9 = r14
            r48 = r22
            r26 = r23
            r23 = r31
            r7 = r35
            r22 = r15
            r14 = r5
            r5 = r64
            r54 = r12
            r12 = r11
            r11 = r54
            r51 = r60
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x3144:
            r0 = move-exception
            r1 = r71
            r44 = r2
            r45 = r3
            r49 = r7
            r25 = r8
            r2 = r13
            r9 = r14
            r48 = r22
            r26 = r23
            r23 = r31
            r7 = r35
            r22 = r15
            r14 = r5
            r5 = r64
            r54 = r12
            r12 = r11
            r11 = r54
            r51 = r60
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x3171:
            r0 = move-exception
            r44 = r2
            r45 = r3
            r27 = r5
            r49 = r7
            r25 = r8
            r2 = r13
            r48 = r22
            r26 = r23
            r7 = r35
            r5 = r64
            r23 = r1
            r22 = r15
            r14 = r65
            r1 = r71
            r54 = r12
            r12 = r11
            r11 = r54
            r51 = r60
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r9 = r20
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x31a2:
            r0 = move-exception
            r5 = r64
            r44 = r2
            r45 = r3
            r49 = r7
            r25 = r8
            r2 = r13
            r48 = r22
            r26 = r23
            r7 = r35
            r23 = r1
            r22 = r15
            r14 = r65
            r1 = r71
            r54 = r12
            r12 = r11
            r11 = r54
            r51 = r60
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r9 = r20
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x31d1:
            r0 = move-exception
            r44 = r2
            r45 = r3
            r49 = r7
            r25 = r8
            r2 = r13
            r48 = r22
            r26 = r23
            r7 = r35
            r23 = r1
            r22 = r15
            r1 = r71
            r14 = r5
            r5 = r10
            r10 = r55
            goto L_0x3222
        L_0x31ec:
            r0 = move-exception
            r44 = r2
            r45 = r3
            r49 = r7
            r25 = r8
            r5 = r10
            r2 = r13
            r48 = r22
            r26 = r23
            r7 = r35
            r10 = r55
            r23 = r1
            r22 = r15
            r14 = r65
            r1 = r71
            goto L_0x3222
        L_0x3208:
            r0 = move-exception
            r44 = r2
            r45 = r3
            r49 = r7
            r25 = r8
            r5 = r10
            r2 = r13
            r48 = r22
            r26 = r23
            r7 = r35
            r10 = r55
            r23 = r1
            r1 = r9
            r22 = r15
            r14 = r65
        L_0x3222:
            r54 = r12
            r12 = r11
            r11 = r54
            r51 = r60
            r4 = r0
            r52 = r5
            r33 = r11
            r11 = r12
            r9 = r20
            r3 = r44
            r12 = r48
            goto L_0x3281
        L_0x3237:
            r0 = move-exception
            r10 = r55
            r3 = r2
            r1 = r9
            r69 = r11
            r2 = r13
            r26 = r23
            r33 = r56
            r49 = r59
            r51 = r60
            r19 = r61
            r23 = r64
            r25 = r77
            r4 = r0
            r35 = r7
            r11 = r12
            r52 = r15
            r9 = r20
            r45 = r22
            r22 = r58
            r12 = r62
            goto L_0x3281
        L_0x325c:
            r0 = move-exception
            r10 = r55
            r1 = r9
            r69 = r11
            r2 = r13
            r26 = r23
            r3 = r31
            r33 = r56
            r49 = r59
            r51 = r60
            r19 = r61
            r23 = r64
            r25 = r77
            r4 = r0
            r35 = r7
            r11 = r12
            r52 = r15
            r9 = r20
            r45 = r22
            r22 = r58
            r12 = r62
        L_0x3281:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r4)
            im.bclpbkiauv.messenger.MessagesStorage r5 = r55.getMessagesStorage()
            if (r76 == 0) goto L_0x328c
            r13 = 1
            goto L_0x328d
        L_0x328c:
            r13 = 0
        L_0x328d:
            r5.markMessageAsSendError(r11, r13)
            if (r9 == 0) goto L_0x3297
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r9.messageOwner
            r6 = 2
            r5.send_state = r6
        L_0x3297:
            im.bclpbkiauv.messenger.NotificationCenter r5 = r55.getNotificationCenter()
            int r6 = im.bclpbkiauv.messenger.NotificationCenter.messageSendError
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]
            int r8 = r11.id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r13 = 0
            r7[r13] = r8
            r5.postNotificationName(r6, r7)
            int r5 = r11.id
            r10.processSentMessage(r5)
            r14 = r9
            r5 = r12
            r6 = r19
            r15 = r22
            r8 = r25
            r12 = r33
            r7 = r49
        L_0x32bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLRPC$MessageMedia, im.bclpbkiauv.tgnet.TLRPC$TL_photo, im.bclpbkiauv.messenger.VideoEditedInfo, im.bclpbkiauv.tgnet.TLRPC$User, im.bclpbkiauv.tgnet.TLRPC$TL_document, im.bclpbkiauv.tgnet.TLRPC$TL_game, im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.tgnet.TLRPC$WebPage, boolean, im.bclpbkiauv.messenger.MessageObject, java.util.ArrayList, im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object):void");
    }

    /* access modifiers changed from: private */
    public void performSendDelayedMessage(DelayedMessage message) {
        performSendDelayedMessage(message, -1);
    }

    private TLRPC.PhotoSize getThumbForSecretChat(ArrayList<TLRPC.PhotoSize> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        int a = 0;
        int N = arrayList.size();
        while (a < N) {
            TLRPC.PhotoSize size = arrayList.get(a);
            if (size == null || (size instanceof TLRPC.TL_photoStrippedSize) || (size instanceof TLRPC.TL_photoSizeEmpty) || size.location == null) {
                a++;
            } else {
                TLRPC.TL_photoSize photoSize = new TLRPC.TL_photoSize();
                photoSize.type = size.type;
                photoSize.w = size.w;
                photoSize.h = size.h;
                photoSize.size = size.size;
                photoSize.bytes = size.bytes;
                if (photoSize.bytes == null) {
                    photoSize.bytes = new byte[0];
                }
                photoSize.location = new TLRPC.TL_fileLocation_layer82();
                photoSize.location.dc_id = size.location.dc_id;
                photoSize.location.volume_id = size.location.volume_id;
                photoSize.location.local_id = size.location.local_id;
                photoSize.location.secret = size.location.secret;
                return photoSize;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void performSendDelayedMessage(DelayedMessage message, int index) {
        int index2;
        TLObject inputMedia;
        MessageObject messageObject;
        TLRPC.InputMedia media;
        TLRPC.InputMedia media2;
        TLRPC.InputMedia media3;
        DelayedMessage delayedMessage = message;
        boolean z = false;
        boolean z2 = true;
        if (delayedMessage.type == 0) {
            if (delayedMessage.httpLocation != null) {
                putToDelayedMessages(delayedMessage.httpLocation, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "file", this.currentAccount);
            } else if (delayedMessage.sendRequest != null) {
                String location = FileLoader.getPathToAttach(delayedMessage.photoSize).toString();
                putToDelayedMessages(location, delayedMessage);
                getFileLoader().uploadFile(location, false, true, 16777216);
                putToUploadingMessages(delayedMessage.obj);
            } else {
                String location2 = FileLoader.getPathToAttach(delayedMessage.photoSize).toString();
                if (!(delayedMessage.sendEncryptedRequest == null || delayedMessage.photoSize.location.dc_id == 0)) {
                    File file = new File(location2);
                    if (!file.exists()) {
                        location2 = FileLoader.getPathToAttach(delayedMessage.photoSize, true).toString();
                        file = new File(location2);
                    }
                    if (!file.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage.photoSize), delayedMessage);
                        getFileLoader().loadFile(ImageLocation.getForObject(delayedMessage.photoSize, delayedMessage.locationParent), delayedMessage.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(location2, delayedMessage);
                getFileLoader().uploadFile(location2, true, true, 16777216);
                putToUploadingMessages(delayedMessage.obj);
            }
        } else if (delayedMessage.type == 1) {
            if (delayedMessage.videoEditedInfo == null || !delayedMessage.videoEditedInfo.needConvert()) {
                if (delayedMessage.videoEditedInfo != null) {
                    if (delayedMessage.videoEditedInfo.file != null) {
                        if (delayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                            media3 = ((TLRPC.TL_messages_sendMedia) delayedMessage.sendRequest).media;
                        } else {
                            media3 = ((TLRPC.TL_messages_editMessage) delayedMessage.sendRequest).media;
                        }
                        media3.file = delayedMessage.videoEditedInfo.file;
                        delayedMessage.videoEditedInfo.file = null;
                    } else if (delayedMessage.videoEditedInfo.encryptedFile != null) {
                        TLRPC.TL_decryptedMessage decryptedMessage = (TLRPC.TL_decryptedMessage) delayedMessage.sendEncryptedRequest;
                        decryptedMessage.media.size = (int) delayedMessage.videoEditedInfo.estimatedSize;
                        decryptedMessage.media.key = delayedMessage.videoEditedInfo.key;
                        decryptedMessage.media.iv = delayedMessage.videoEditedInfo.iv;
                        getSecretChatHelper().performSendEncryptedRequest(decryptedMessage, delayedMessage.obj.messageOwner, delayedMessage.encryptedChat, delayedMessage.videoEditedInfo.encryptedFile, delayedMessage.originalPath, delayedMessage.obj);
                        delayedMessage.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                if (delayedMessage.sendRequest != null) {
                    if (delayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                        media2 = ((TLRPC.TL_messages_sendMedia) delayedMessage.sendRequest).media;
                    } else {
                        media2 = ((TLRPC.TL_messages_editMessage) delayedMessage.sendRequest).media;
                    }
                    if (media2.file == null) {
                        String location3 = delayedMessage.obj.messageOwner.attachPath;
                        TLRPC.Document document = delayedMessage.obj.getDocument();
                        if (location3 == null) {
                            location3 = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                        }
                        putToDelayedMessages(location3, delayedMessage);
                        if (delayedMessage.obj.videoEditedInfo == null || !delayedMessage.obj.videoEditedInfo.needConvert()) {
                            getFileLoader().uploadFile(location3, false, false, ConnectionsManager.FileTypeVideo);
                        } else {
                            getFileLoader().uploadFile(location3, false, false, document.size, ConnectionsManager.FileTypeVideo, true);
                        }
                        putToUploadingMessages(delayedMessage.obj);
                    } else {
                        String location4 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                        putToDelayedMessages(location4, delayedMessage);
                        getFileLoader().uploadFile(location4, false, true, 16777216);
                        putToUploadingMessages(delayedMessage.obj);
                    }
                } else {
                    String location5 = delayedMessage.obj.messageOwner.attachPath;
                    TLRPC.Document document2 = delayedMessage.obj.getDocument();
                    if (location5 == null) {
                        location5 = FileLoader.getDirectory(4) + "/" + document2.id + ".mp4";
                    }
                    if (delayedMessage.sendEncryptedRequest == null || document2.dc_id == 0 || new File(location5).exists()) {
                        putToDelayedMessages(location5, delayedMessage);
                        if (delayedMessage.obj.videoEditedInfo == null || !delayedMessage.obj.videoEditedInfo.needConvert()) {
                            getFileLoader().uploadFile(location5, true, false, ConnectionsManager.FileTypeVideo);
                        } else {
                            getFileLoader().uploadFile(location5, true, false, document2.size, ConnectionsManager.FileTypeVideo, true);
                        }
                        putToUploadingMessages(delayedMessage.obj);
                    } else {
                        putToDelayedMessages(FileLoader.getAttachFileName(document2), delayedMessage);
                        getFileLoader().loadFile(document2, delayedMessage.parentObject, 2, 0);
                        return;
                    }
                }
            } else {
                String location6 = delayedMessage.obj.messageOwner.attachPath;
                TLRPC.Document document3 = delayedMessage.obj.getDocument();
                if (location6 == null) {
                    location6 = FileLoader.getDirectory(4) + "/" + document3.id + ".mp4";
                }
                putToDelayedMessages(location6, delayedMessage);
                MediaController.getInstance().scheduleVideoConvert(delayedMessage.obj);
                putToUploadingMessages(delayedMessage.obj);
            }
        } else if (delayedMessage.type == 2) {
            if (delayedMessage.httpLocation != null) {
                putToDelayedMessages(delayedMessage.httpLocation, delayedMessage);
                ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "gif", this.currentAccount);
            } else if (delayedMessage.sendRequest != null) {
                if (delayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia) {
                    media = ((TLRPC.TL_messages_sendMedia) delayedMessage.sendRequest).media;
                } else {
                    media = ((TLRPC.TL_messages_editMessage) delayedMessage.sendRequest).media;
                }
                if (media.file == null) {
                    String location7 = delayedMessage.obj.messageOwner.attachPath;
                    putToDelayedMessages(location7, delayedMessage);
                    FileLoader fileLoader = getFileLoader();
                    if (delayedMessage.sendRequest != null) {
                        z2 = false;
                    }
                    fileLoader.uploadFile(location7, z2, false, ConnectionsManager.FileTypeFile);
                    putToUploadingMessages(delayedMessage.obj);
                } else if (media.thumb == null && delayedMessage.photoSize != null) {
                    String location8 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(location8, delayedMessage);
                    getFileLoader().uploadFile(location8, false, true, 16777216);
                    putToUploadingMessages(delayedMessage.obj);
                }
            } else {
                String location9 = delayedMessage.obj.messageOwner.attachPath;
                TLRPC.Document document4 = delayedMessage.obj.getDocument();
                if (delayedMessage.sendEncryptedRequest == null || document4.dc_id == 0 || new File(location9).exists()) {
                    putToDelayedMessages(location9, delayedMessage);
                    getFileLoader().uploadFile(location9, true, false, ConnectionsManager.FileTypeFile);
                    putToUploadingMessages(delayedMessage.obj);
                } else {
                    putToDelayedMessages(FileLoader.getAttachFileName(document4), delayedMessage);
                    getFileLoader().loadFile(document4, delayedMessage.parentObject, 2, 0);
                    return;
                }
            }
        } else if (delayedMessage.type == 3) {
            String location10 = delayedMessage.obj.messageOwner.attachPath;
            putToDelayedMessages(location10, delayedMessage);
            FileLoader fileLoader2 = getFileLoader();
            if (delayedMessage.sendRequest == null) {
                z = true;
            }
            fileLoader2.uploadFile(location10, z, true, ConnectionsManager.FileTypeAudio);
            putToUploadingMessages(delayedMessage.obj);
        } else if (delayedMessage.type == 4) {
            boolean add = index < 0;
            if (delayedMessage.performMediaUpload) {
                if (index < 0) {
                    index2 = delayedMessage.messageObjects.size() - 1;
                } else {
                    index2 = index;
                }
                MessageObject messageObject2 = delayedMessage.messageObjects.get(index2);
                if (messageObject2.getDocument() != null) {
                    if (delayedMessage.videoEditedInfo != null) {
                        String location11 = messageObject2.messageOwner.attachPath;
                        TLRPC.Document document5 = messageObject2.getDocument();
                        if (location11 == null) {
                            location11 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(location11, delayedMessage);
                        delayedMessage.extraHashMap.put(messageObject2, location11);
                        delayedMessage.extraHashMap.put(location11 + "_i", messageObject2);
                        if (delayedMessage.photoSize != null) {
                            delayedMessage.extraHashMap.put(location11 + "_t", delayedMessage.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject2);
                        delayedMessage.obj = messageObject2;
                        putToUploadingMessages(messageObject2);
                        MessageObject messageObject3 = messageObject2;
                    } else {
                        TLRPC.Document document6 = messageObject2.getDocument();
                        String documentLocation = messageObject2.messageOwner.attachPath;
                        if (documentLocation == null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(FileLoader.getDirectory(4));
                            sb.append("/");
                            messageObject = messageObject2;
                            sb.append(document6.id);
                            sb.append(".mp4");
                            documentLocation = sb.toString();
                        } else {
                            messageObject = messageObject2;
                        }
                        if (delayedMessage.sendRequest != null) {
                            TLRPC.InputMedia media4 = ((TLRPC.TL_messages_sendMultiMedia) delayedMessage.sendRequest).multi_media.get(index2).media;
                            if (media4.file == null) {
                                putToDelayedMessages(documentLocation, delayedMessage);
                                MessageObject messageObject4 = messageObject;
                                delayedMessage.extraHashMap.put(messageObject4, documentLocation);
                                delayedMessage.extraHashMap.put(documentLocation, media4);
                                delayedMessage.extraHashMap.put(documentLocation + "_i", messageObject4);
                                if (delayedMessage.photoSize != null) {
                                    delayedMessage.extraHashMap.put(documentLocation + "_t", delayedMessage.photoSize);
                                }
                                if (messageObject4.videoEditedInfo == null || !messageObject4.videoEditedInfo.needConvert()) {
                                    getFileLoader().uploadFile(documentLocation, false, false, ConnectionsManager.FileTypeVideo);
                                } else {
                                    getFileLoader().uploadFile(documentLocation, false, false, document6.size, ConnectionsManager.FileTypeVideo, true);
                                }
                                putToUploadingMessages(messageObject4);
                            } else {
                                MessageObject messageObject5 = messageObject;
                                String location12 = FileLoader.getDirectory(4) + "/" + delayedMessage.photoSize.location.volume_id + "_" + delayedMessage.photoSize.location.local_id + ".jpg";
                                putToDelayedMessages(location12, delayedMessage);
                                delayedMessage.extraHashMap.put(location12 + "_o", documentLocation);
                                delayedMessage.extraHashMap.put(messageObject5, location12);
                                delayedMessage.extraHashMap.put(location12, media4);
                                getFileLoader().uploadFile(location12, false, true, 16777216);
                                putToUploadingMessages(messageObject5);
                            }
                        } else {
                            MessageObject messageObject6 = messageObject;
                            putToDelayedMessages(documentLocation, delayedMessage);
                            delayedMessage.extraHashMap.put(messageObject6, documentLocation);
                            delayedMessage.extraHashMap.put(documentLocation, ((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest).files.get(index2));
                            delayedMessage.extraHashMap.put(documentLocation + "_i", messageObject6);
                            if (delayedMessage.photoSize != null) {
                                delayedMessage.extraHashMap.put(documentLocation + "_t", delayedMessage.photoSize);
                            }
                            if (messageObject6.videoEditedInfo == null || !messageObject6.videoEditedInfo.needConvert()) {
                                getFileLoader().uploadFile(documentLocation, true, false, ConnectionsManager.FileTypeVideo);
                            } else {
                                getFileLoader().uploadFile(documentLocation, true, false, document6.size, ConnectionsManager.FileTypeVideo, true);
                            }
                            putToUploadingMessages(messageObject6);
                        }
                    }
                    delayedMessage.videoEditedInfo = null;
                    delayedMessage.photoSize = null;
                } else {
                    MessageObject messageObject7 = messageObject2;
                    if (delayedMessage.httpLocation != null) {
                        putToDelayedMessages(delayedMessage.httpLocation, delayedMessage);
                        delayedMessage.extraHashMap.put(messageObject7, delayedMessage.httpLocation);
                        delayedMessage.extraHashMap.put(delayedMessage.httpLocation, messageObject7);
                        ImageLoader.getInstance().loadHttpFile(delayedMessage.httpLocation, "file", this.currentAccount);
                        delayedMessage.httpLocation = null;
                    } else {
                        if (delayedMessage.sendRequest != null) {
                            inputMedia = ((TLRPC.TL_messages_sendMultiMedia) delayedMessage.sendRequest).multi_media.get(index2).media;
                        } else {
                            inputMedia = ((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest).files.get(index2);
                        }
                        String location13 = FileLoader.getPathToAttach(delayedMessage.photoSize).toString();
                        putToDelayedMessages(location13, delayedMessage);
                        delayedMessage.extraHashMap.put(location13, inputMedia);
                        delayedMessage.extraHashMap.put(messageObject7, location13);
                        getFileLoader().uploadFile(location13, delayedMessage.sendEncryptedRequest != null, true, 16777216);
                        putToUploadingMessages(messageObject7);
                        delayedMessage.photoSize = null;
                    }
                }
                delayedMessage.performMediaUpload = false;
            } else {
                if (!delayedMessage.messageObjects.isEmpty()) {
                    putToSendingMessages(delayedMessage.messageObjects.get(delayedMessage.messageObjects.size() - 1).messageOwner, delayedMessage.finalGroupMessage != 0);
                }
                int i = index;
            }
            sendReadyToSendGroup(delayedMessage, add, true);
            return;
        }
        int i2 = index;
    }

    private void uploadMultiMedia(DelayedMessage message, TLRPC.InputMedia inputMedia, TLRPC.InputEncryptedFile inputEncryptedFile, String key) {
        Float valueOf = Float.valueOf(1.0f);
        if (inputMedia != null) {
            TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
            int a = 0;
            while (true) {
                if (a >= multiMedia.multi_media.size()) {
                    break;
                } else if (multiMedia.multi_media.get(a).media == inputMedia) {
                    putToSendingMessages(message.messages.get(a), message.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, key, valueOf, false);
                    break;
                } else {
                    a++;
                }
            }
            TLRPC.TL_messages_uploadMedia req = new TLRPC.TL_messages_uploadMedia();
            req.media = inputMedia;
            req.peer = ((TLRPC.TL_messages_sendMultiMedia) message.sendRequest).peer;
            getConnectionsManager().sendRequest(req, new RequestDelegate(inputMedia, message) {
                private final /* synthetic */ TLRPC.InputMedia f$1;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$uploadMultiMedia$22$SendMessagesHelper(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (inputEncryptedFile != null) {
            TLRPC.TL_messages_sendEncryptedMultiMedia multiMedia2 = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
            int a2 = 0;
            while (true) {
                if (a2 >= multiMedia2.files.size()) {
                    break;
                } else if (multiMedia2.files.get(a2) == inputEncryptedFile) {
                    putToSendingMessages(message.messages.get(a2), message.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, key, valueOf, false);
                    break;
                } else {
                    a2++;
                }
            }
            sendReadyToSendGroup(message, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$22$SendMessagesHelper(TLRPC.InputMedia inputMedia, DelayedMessage message, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(response, inputMedia, message) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.InputMedia f$2;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$21$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$21$SendMessagesHelper(im.bclpbkiauv.tgnet.TLObject r7, im.bclpbkiauv.tgnet.TLRPC.InputMedia r8, im.bclpbkiauv.messenger.SendMessagesHelper.DelayedMessage r9) {
        /*
            r6 = this;
            r0 = 0
            if (r7 == 0) goto L_0x0061
            r1 = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r1 = (im.bclpbkiauv.tgnet.TLRPC.MessageMedia) r1
            boolean r2 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaUploadedPhoto
            if (r2 == 0) goto L_0x0034
            boolean r2 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0034
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaPhoto
            r2.<init>()
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPhoto
            r3.<init>()
            r2.id = r3
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Photo r4 = r1.photo
            long r4 = r4.id
            r3.id = r4
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Photo r4 = r1.photo
            long r4 = r4.access_hash
            r3.access_hash = r4
            im.bclpbkiauv.tgnet.TLRPC$InputPhoto r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Photo r4 = r1.photo
            byte[] r4 = r4.file_reference
            r3.file_reference = r4
            r0 = r2
        L_0x0033:
            goto L_0x0061
        L_0x0034:
            boolean r2 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputMediaUploadedDocument
            if (r2 == 0) goto L_0x0033
            boolean r2 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r2 == 0) goto L_0x0033
            im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputMediaDocument
            r2.<init>()
            im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputDocument
            r3.<init>()
            r2.id = r3
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r1.document
            long r4 = r4.id
            r3.id = r4
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r1.document
            long r4 = r4.access_hash
            r3.access_hash = r4
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r3 = r2.id
            im.bclpbkiauv.tgnet.TLRPC$Document r4 = r1.document
            byte[] r4 = r4.file_reference
            r3.file_reference = r4
            r0 = r2
        L_0x0061:
            if (r0 == 0) goto L_0x009d
            int r1 = r8.ttl_seconds
            r2 = 1
            if (r1 == 0) goto L_0x0071
            int r1 = r8.ttl_seconds
            r0.ttl_seconds = r1
            int r1 = r0.flags
            r1 = r1 | r2
            r0.flags = r1
        L_0x0071:
            im.bclpbkiauv.tgnet.TLObject r1 = r9.sendRequest
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_sendMultiMedia) r1
            r3 = 0
        L_0x0076:
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0098
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media
            java.lang.Object r4 = r4.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_inputSingleMedia) r4
            im.bclpbkiauv.tgnet.TLRPC$InputMedia r4 = r4.media
            if (r4 != r8) goto L_0x0095
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media
            java.lang.Object r4 = r4.get(r3)
            im.bclpbkiauv.tgnet.TLRPC$TL_inputSingleMedia r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_inputSingleMedia) r4
            r4.media = r0
            goto L_0x0098
        L_0x0095:
            int r3 = r3 + 1
            goto L_0x0076
        L_0x0098:
            r3 = 0
            r6.sendReadyToSendGroup(r9, r3, r2)
            goto L_0x00a0
        L_0x009d:
            r9.markAsError()
        L_0x00a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.lambda$null$21$SendMessagesHelper(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$InputMedia, im.bclpbkiauv.messenger.SendMessagesHelper$DelayedMessage):void");
    }

    private void sendReadyToSendGroup(DelayedMessage message, boolean add, boolean check) {
        DelayedMessage maxDelayedMessage;
        if (message.messageObjects.isEmpty()) {
            message.markAsError();
            return;
        }
        String key = "group_" + message.groupId;
        if (message.finalGroupMessage == message.messageObjects.get(message.messageObjects.size() - 1).getId()) {
            if (add) {
                this.delayedMessages.remove(key);
                getMessagesStorage().putMessages(message.messages, false, true, false, 0, message.scheduled);
                getMessagesController().updateInterfaceWithMessages(message.peer, message.messageObjects, message.scheduled);
                if (!message.scheduled) {
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
            if (message.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia) {
                TLRPC.TL_messages_sendMultiMedia request = (TLRPC.TL_messages_sendMultiMedia) message.sendRequest;
                int a = 0;
                while (a < request.multi_media.size()) {
                    TLRPC.InputMedia inputMedia = request.multi_media.get(a).media;
                    if (!(inputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto) && !(inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument)) {
                        a++;
                    } else {
                        return;
                    }
                }
                if (check && (maxDelayedMessage = findMaxDelayedMessageForMessageId(message.finalGroupMessage, message.peer)) != null) {
                    maxDelayedMessage.addDelayedRequest(message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message, message.scheduled);
                    if (message.requests != null) {
                        maxDelayedMessage.requests.addAll(message.requests);
                        return;
                    }
                    return;
                }
            } else {
                TLRPC.TL_messages_sendEncryptedMultiMedia request2 = (TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                int a2 = 0;
                while (a2 < request2.files.size()) {
                    if (!(request2.files.get(a2) instanceof TLRPC.TL_inputEncryptedFile)) {
                        a2++;
                    } else {
                        return;
                    }
                }
            }
            if (message.sendRequest instanceof TLRPC.TL_messages_sendMultiMedia) {
                performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message, message.scheduled);
            } else {
                getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest, message);
            }
            message.sendDelayedRequests();
        } else if (add) {
            putToDelayedMessages(key, message);
        }
    }

    public /* synthetic */ void lambda$null$23$SendMessagesHelper(String path) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, path, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$24$SendMessagesHelper(String path) {
        AndroidUtilities.runOnUIThread(new Runnable(path) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$23$SendMessagesHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void stopVideoService(String path) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(path) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$stopVideoService$24$SendMessagesHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC.Message message, boolean scheduled) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable(message, scheduled) {
                private final /* synthetic */ TLRPC.Message f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$putToSendingMessages$25$SendMessagesHelper(this.f$1, this.f$2);
                }
            });
        } else {
            putToSendingMessages(message, scheduled, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$25$SendMessagesHelper(TLRPC.Message message, boolean scheduled) {
        putToSendingMessages(message, scheduled, true);
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC.Message message, boolean scheduled, boolean notify) {
        if (message != null) {
            if (message.id > 0) {
                this.editingMessages.put(message.id, message);
                return;
            }
            boolean contains = this.sendingMessages.indexOfKey(message.id) >= 0;
            removeFromUploadingMessages(message.id, scheduled);
            this.sendingMessages.put(message.id, message);
            if (!scheduled && !contains) {
                long did = MessageObject.getDialogId(message);
                LongSparseArray<Integer> longSparseArray = this.sendingMessagesIdDialogs;
                longSparseArray.put(did, Integer.valueOf(longSparseArray.get(did, 0).intValue() + 1));
                if (notify) {
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public TLRPC.Message removeFromSendingMessages(int mid, boolean scheduled) {
        TLRPC.Message message;
        long did;
        Integer currentCount;
        if (mid > 0) {
            message = this.editingMessages.get(mid);
            if (message != null) {
                this.editingMessages.remove(mid);
            }
        } else {
            message = this.sendingMessages.get(mid);
            if (message != null) {
                this.sendingMessages.remove(mid);
                if (!scheduled && (currentCount = this.sendingMessagesIdDialogs.get(did)) != null) {
                    int count = currentCount.intValue() - 1;
                    if (count <= 0) {
                        this.sendingMessagesIdDialogs.remove(did);
                    } else {
                        this.sendingMessagesIdDialogs.put((did = MessageObject.getDialogId(message)), Integer.valueOf(count));
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
        return message;
    }

    public int getSendingMessageId(long did) {
        for (int a = 0; a < this.sendingMessages.size(); a++) {
            TLRPC.Message message = this.sendingMessages.valueAt(a);
            if (message.dialog_id == did) {
                return message.id;
            }
        }
        for (int a2 = 0; a2 < this.uploadMessages.size(); a2++) {
            TLRPC.Message message2 = this.uploadMessages.valueAt(a2);
            if (message2.dialog_id == did) {
                return message2.id;
            }
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void putToUploadingMessages(MessageObject obj) {
        if (obj != null && obj.getId() <= 0 && !obj.scheduled) {
            TLRPC.Message message = obj.messageOwner;
            boolean contains = this.uploadMessages.indexOfKey(message.id) >= 0;
            this.uploadMessages.put(message.id, message);
            if (!contains) {
                long did = MessageObject.getDialogId(message);
                LongSparseArray<Integer> longSparseArray = this.uploadingMessagesIdDialogs;
                longSparseArray.put(did, Integer.valueOf(longSparseArray.get(did, 0).intValue() + 1));
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void removeFromUploadingMessages(int mid, boolean scheduled) {
        TLRPC.Message message;
        if (mid <= 0 && !scheduled && (message = this.uploadMessages.get(mid)) != null) {
            this.uploadMessages.remove(mid);
            long did = MessageObject.getDialogId(message);
            Integer currentCount = this.uploadingMessagesIdDialogs.get(did);
            if (currentCount != null) {
                int count = currentCount.intValue() - 1;
                if (count <= 0) {
                    this.uploadingMessagesIdDialogs.remove(did);
                } else {
                    this.uploadingMessagesIdDialogs.put(did, Integer.valueOf(count));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.indexOfKey(mid) >= 0 || this.editingMessages.indexOfKey(mid) >= 0;
    }

    public boolean isSendingMessageIdDialog(long did) {
        return this.sendingMessagesIdDialogs.get(did, 0).intValue() > 0;
    }

    public boolean isUploadingMessageIdDialog(long did) {
        return this.uploadingMessagesIdDialogs.get(did, 0).intValue() > 0;
    }

    /* access modifiers changed from: protected */
    public void performSendMessageRequestMulti(TLRPC.TL_messages_sendMultiMedia req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths, ArrayList<Object> parentObjects, DelayedMessage delayedMessage, boolean scheduled) {
        int size = msgObjs.size();
        for (int a = 0; a < size; a++) {
            ArrayList<MessageObject> arrayList = msgObjs;
            putToSendingMessages(msgObjs.get(a).messageOwner, scheduled);
        }
        ArrayList<MessageObject> arrayList2 = msgObjs;
        boolean z = scheduled;
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
        getConnectionsManager().sendRequest((TLObject) req, (RequestDelegate) new RequestDelegate(parentObjects, req, msgObjs, originalPaths, delayedMessage, scheduled) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$32$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
            }
        }, (QuickAckDelegate) null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$32$SendMessagesHelper(ArrayList parentObjects, TLRPC.TL_messages_sendMultiMedia req, ArrayList msgObjs, ArrayList originalPaths, DelayedMessage delayedMessage, boolean scheduled, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, parentObjects, req, msgObjs, originalPaths, delayedMessage, scheduled, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ ArrayList f$5;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$6;
            private final /* synthetic */ boolean f$7;
            private final /* synthetic */ TLObject f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$31$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(TLRPC.TL_error error, ArrayList parentObjects, TLRPC.TL_messages_sendMultiMedia req, ArrayList msgObjs, ArrayList originalPaths, DelayedMessage delayedMessage, boolean scheduled, TLObject response) {
        char c;
        TLRPC.Updates updates;
        int i;
        TLRPC.TL_error tL_error = error;
        ArrayList arrayList = parentObjects;
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
        ArrayList arrayList2 = msgObjs;
        ArrayList arrayList3 = originalPaths;
        boolean z = scheduled;
        if (tL_error != null && FileRefController.isFileRefError(tL_error.text)) {
            if (arrayList != null) {
                ArrayList<Object> arrayList4 = new ArrayList<>(arrayList);
                getFileRefController().requestReference(arrayList4, tL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList4, delayedMessage, Boolean.valueOf(scheduled));
                return;
            } else if (delayedMessage != null) {
                final TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = req;
                final DelayedMessage delayedMessage2 = delayedMessage;
                final ArrayList arrayList5 = msgObjs;
                final boolean z2 = scheduled;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int size = tL_messages_sendMultiMedia2.multi_media.size();
                        for (int a = 0; a < size; a++) {
                            if (delayedMessage2.parentObjects.get(a) != null) {
                                SendMessagesHelper.this.removeFromSendingMessages(((MessageObject) arrayList5.get(a)).getId(), z2);
                                TLRPC.TL_inputSingleMedia request = tL_messages_sendMultiMedia2.multi_media.get(a);
                                if (request.media instanceof TLRPC.TL_inputMediaPhoto) {
                                    request.media = delayedMessage2.inputMedias.get(a);
                                } else if (request.media instanceof TLRPC.TL_inputMediaDocument) {
                                    request.media = delayedMessage2.inputMedias.get(a);
                                }
                                DelayedMessage delayedMessage = delayedMessage2;
                                delayedMessage.videoEditedInfo = delayedMessage.videoEditedInfos.get(a);
                                DelayedMessage delayedMessage2 = delayedMessage2;
                                delayedMessage2.httpLocation = delayedMessage2.httpLocations.get(a);
                                DelayedMessage delayedMessage3 = delayedMessage2;
                                delayedMessage3.photoSize = delayedMessage3.locations.get(a);
                                delayedMessage2.performMediaUpload = true;
                                SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2, a);
                            }
                        }
                    }
                });
                return;
            }
        }
        boolean isSentError = false;
        if (tL_error == null) {
            SparseArray<TLRPC.Message> newMessages = new SparseArray<>();
            LongSparseArray<Integer> newIds = new LongSparseArray<>();
            TLRPC.Updates updates2 = (TLRPC.Updates) response;
            ArrayList<TLRPC.Update> updatesArr = ((TLRPC.Updates) response).updates;
            int a = 0;
            while (a < updatesArr.size()) {
                TLRPC.Update update = updatesArr.get(a);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    TLRPC.TL_updateMessageID updateMessageID = (TLRPC.TL_updateMessageID) update;
                    TLRPC.Update update2 = update;
                    newIds.put(updateMessageID.random_id, Integer.valueOf(updateMessageID.id));
                    updatesArr.remove(a);
                    a--;
                } else {
                    TLRPC.Update update3 = update;
                    if (update3 instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.TL_updateNewMessage newMessage = (TLRPC.TL_updateNewMessage) update3;
                        newMessages.put(newMessage.message.id, newMessage.message);
                        Utilities.stageQueue.postRunnable(new Runnable(newMessage) {
                            private final /* synthetic */ TLRPC.TL_updateNewMessage f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$null$26$SendMessagesHelper(this.f$1);
                            }
                        });
                        updatesArr.remove(a);
                        a--;
                    } else if (update3 instanceof TLRPC.TL_updateNewChannelMessage) {
                        TLRPC.TL_updateNewChannelMessage newMessage2 = (TLRPC.TL_updateNewChannelMessage) update3;
                        newMessages.put(newMessage2.message.id, newMessage2.message);
                        Utilities.stageQueue.postRunnable(new Runnable(newMessage2) {
                            private final /* synthetic */ TLRPC.TL_updateNewChannelMessage f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$null$27$SendMessagesHelper(this.f$1);
                            }
                        });
                        updatesArr.remove(a);
                        a--;
                    } else if (update3 instanceof TLRPC.TL_updateNewScheduledMessage) {
                        TLRPC.TL_updateNewScheduledMessage newMessage3 = (TLRPC.TL_updateNewScheduledMessage) update3;
                        newMessages.put(newMessage3.message.id, newMessage3.message);
                        updatesArr.remove(a);
                        a--;
                    }
                }
                a++;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= msgObjs.size()) {
                    updates = updates2;
                    LongSparseArray<Integer> longSparseArray = newIds;
                    SparseArray<TLRPC.Message> sparseArray = newMessages;
                    int i3 = i2;
                    c = 0;
                    break;
                }
                MessageObject msgObj = (MessageObject) arrayList2.get(i2);
                String originalPath = (String) arrayList3.get(i2);
                TLRPC.Message newMsgObj = msgObj.messageOwner;
                int oldId = newMsgObj.id;
                ArrayList<TLRPC.Message> sentMessages = new ArrayList<>();
                String attachPath = newMsgObj.attachPath;
                int oldId2 = oldId;
                ArrayList<TLRPC.Update> updatesArr2 = updatesArr;
                Integer id = newIds.get(newMsgObj.random_id);
                if (id == null) {
                    updates = updates2;
                    SparseArray<TLRPC.Message> sparseArray2 = newMessages;
                    String str = attachPath;
                    MessageObject messageObject = msgObj;
                    TLRPC.Message message = newMsgObj;
                    int i4 = i2;
                    ArrayList<TLRPC.Message> arrayList6 = sentMessages;
                    c = 0;
                    LongSparseArray<Integer> longSparseArray2 = newIds;
                    isSentError = true;
                    break;
                }
                TLRPC.Message message2 = newMessages.get(id.intValue());
                if (message2 == null) {
                    TLRPC.Message message3 = message2;
                    updates = updates2;
                    SparseArray<TLRPC.Message> sparseArray3 = newMessages;
                    String str2 = attachPath;
                    MessageObject messageObject2 = msgObj;
                    TLRPC.Message message4 = newMsgObj;
                    int i5 = i2;
                    ArrayList<TLRPC.Message> arrayList7 = sentMessages;
                    c = 0;
                    LongSparseArray<Integer> longSparseArray3 = newIds;
                    isSentError = true;
                    break;
                }
                ArrayList<TLRPC.Message> sentMessages2 = sentMessages;
                sentMessages2.add(message2);
                LongSparseArray<Integer> newIds2 = newIds;
                ArrayList<TLRPC.Message> sentMessages3 = sentMessages2;
                TLRPC.Message message5 = message2;
                TLRPC.Updates updates3 = updates2;
                SparseArray<TLRPC.Message> newMessages2 = newMessages;
                String str3 = attachPath;
                updateMediaPaths(msgObj, message5, message2.id, originalPath, false);
                int existFlags = msgObj.getMediaExistanceFlags();
                TLRPC.Message message6 = message5;
                newMsgObj.id = message6.id;
                if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                    message6.flags |= Integer.MIN_VALUE;
                }
                long grouped_id = message6.grouped_id;
                if (!z) {
                    Integer value = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message6.dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(getMessagesStorage().getDialogReadMax(message6.out, message6.dialog_id));
                        getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message6.dialog_id), value);
                    }
                    message6.unread = value.intValue() < message6.id;
                }
                if (0 == 0) {
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    newMsgObj.send_state = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId2), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id), Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    $$Lambda$SendMessagesHelper$TcvJ0OMFfdd71aNCR9mkmqbMdvs r11 = r0;
                    MessageObject messageObject3 = msgObj;
                    TLRPC.Message message7 = newMsgObj;
                    i = i2;
                    $$Lambda$SendMessagesHelper$TcvJ0OMFfdd71aNCR9mkmqbMdvs r0 = new Runnable(newMsgObj, oldId2, scheduled, sentMessages3, grouped_id, existFlags) {
                        private final /* synthetic */ TLRPC.Message f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ boolean f$3;
                        private final /* synthetic */ ArrayList f$4;
                        private final /* synthetic */ long f$5;
                        private final /* synthetic */ int f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r8;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$29$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                        }
                    };
                    storageQueue.postRunnable(r11);
                } else {
                    MessageObject messageObject4 = msgObj;
                    TLRPC.Message message8 = newMsgObj;
                    i = i2;
                }
                i2 = i + 1;
                ArrayList arrayList8 = parentObjects;
                TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia3 = req;
                arrayList3 = originalPaths;
                newIds = newIds2;
                updatesArr = updatesArr2;
                updates2 = updates3;
                newMessages = newMessages2;
            }
            Utilities.stageQueue.postRunnable(new Runnable(updates) {
                private final /* synthetic */ TLRPC.Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$30$SendMessagesHelper(this.f$1);
                }
            });
            TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia4 = req;
        } else {
            c = 0;
            AlertsCreator.processError(this.currentAccount, tL_error, (BaseFragment) null, req, new Object[0]);
            isSentError = true;
        }
        if (isSentError) {
            for (int i6 = 0; i6 < msgObjs.size(); i6++) {
                TLRPC.Message newMsgObj2 = ((MessageObject) arrayList2.get(i6)).messageOwner;
                getMessagesStorage().markMessageAsSendError(newMsgObj2, z);
                newMsgObj2.send_state = 2;
                NotificationCenter notificationCenter = getNotificationCenter();
                int i7 = NotificationCenter.messageSendError;
                Object[] objArr = new Object[1];
                objArr[c] = Integer.valueOf(newMsgObj2.id);
                notificationCenter.postNotificationName(i7, objArr);
                processSentMessage(newMsgObj2.id);
                removeFromSendingMessages(newMsgObj2.id, z);
            }
        }
    }

    public /* synthetic */ void lambda$null$26$SendMessagesHelper(TLRPC.TL_updateNewMessage newMessage) {
        getMessagesController().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$27$SendMessagesHelper(TLRPC.TL_updateNewChannelMessage newMessage) {
        getMessagesController().processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$29$SendMessagesHelper(TLRPC.Message newMsgObj, int oldId, boolean scheduled, ArrayList sentMessages, long grouped_id, int existFlags) {
        TLRPC.Message message = newMsgObj;
        getMessagesStorage().updateMessageStateAndId(message.random_id, Integer.valueOf(oldId), message.id, 0, false, message.to_id.channel_id, scheduled ? 1 : 0);
        boolean z = scheduled;
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, true, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj, oldId, grouped_id, existFlags, z) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$28$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(TLRPC.Message newMsgObj, int oldId, long grouped_id, int existFlags, boolean scheduled) {
        getMediaDataController().increasePeerRaiting(newMsgObj.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id), Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId, scheduled);
    }

    public /* synthetic */ void lambda$null$30$SendMessagesHelper(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    /* access modifiers changed from: private */
    public void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage delayedMessage, Object parentObject, boolean scheduled) {
        performSendMessageRequest(req, msgObj, originalPath, (DelayedMessage) null, false, delayedMessage, parentObject, scheduled);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int messageId, long dialogId) {
        DelayedMessage maxDelayedMessage = null;
        int maxDalyedMessageId = Integer.MIN_VALUE;
        for (Map.Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = entry.getValue();
            int size = messages.size();
            for (int a = 0; a < size; a++) {
                DelayedMessage delayedMessage = messages.get(a);
                if ((delayedMessage.type == 4 || delayedMessage.type == 0) && delayedMessage.peer == dialogId) {
                    int mid = 0;
                    if (delayedMessage.obj != null) {
                        mid = delayedMessage.obj.getId();
                    } else if (delayedMessage.messageObjects != null && !delayedMessage.messageObjects.isEmpty()) {
                        mid = delayedMessage.messageObjects.get(delayedMessage.messageObjects.size() - 1).getId();
                    }
                    if (mid != 0 && mid > messageId && maxDelayedMessage == null && maxDalyedMessageId < mid) {
                        maxDelayedMessage = delayedMessage;
                        maxDalyedMessageId = mid;
                    }
                }
            }
        }
        return maxDelayedMessage;
    }

    /* access modifiers changed from: protected */
    public void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage parentMessage, boolean check, DelayedMessage delayedMessage, Object parentObject, boolean scheduled) {
        DelayedMessage maxDelayedMessage;
        TLObject tLObject = req;
        DelayedMessage delayedMessage2 = parentMessage;
        if ((tLObject instanceof TLRPC.TL_messages_editMessage) || !check || (maxDelayedMessage = findMaxDelayedMessageForMessageId(msgObj.getId(), msgObj.getDialogId())) == null) {
            TLRPC.Message newMsgObj = msgObj.messageOwner;
            boolean z = scheduled;
            putToSendingMessages(newMsgObj, z);
            $$Lambda$SendMessagesHelper$xSPYuusWWXYyDtbMLl6j5izKwU r14 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SendMessagesHelper$xSPYuusWWXYyDtbMLl6j5izKwU r0 = new RequestDelegate(req, parentObject, msgObj, originalPath, parentMessage, check, delayedMessage, z, newMsgObj) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ Object f$2;
                private final /* synthetic */ MessageObject f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ TLRPC.Message f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$44$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tL_error);
                }
            };
            TLRPC.Message newMsgObj2 = newMsgObj;
            newMsgObj2.reqId = connectionsManager.sendRequest(tLObject, (RequestDelegate) r14, (QuickAckDelegate) new QuickAckDelegate(newMsgObj2) {
                private final /* synthetic */ TLRPC.Message f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$46$SendMessagesHelper(this.f$1);
                }
            }, (tLObject instanceof TLRPC.TL_messages_sendMessage ? 128 : 0) | 68);
            if (delayedMessage2 != null) {
                parentMessage.sendDelayedRequests();
                return;
            }
            return;
        }
        maxDelayedMessage.addDelayedRequest(req, msgObj, originalPath, parentObject, delayedMessage, delayedMessage2 != null ? delayedMessage2.scheduled : false);
        if (delayedMessage2 != null && delayedMessage2.requests != null) {
            maxDelayedMessage.requests.addAll(delayedMessage2.requests);
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$44$SendMessagesHelper(TLObject req, Object parentObject, MessageObject msgObj, String originalPath, DelayedMessage parentMessage, boolean check, DelayedMessage delayedMessage, boolean scheduled, TLRPC.Message newMsgObj, TLObject response, TLRPC.TL_error error) {
        TLObject tLObject = req;
        Object obj = parentObject;
        TLRPC.TL_error tL_error = error;
        if (tL_error != null && (((tLObject instanceof TLRPC.TL_messages_sendMedia) || (tLObject instanceof TLRPC.TL_messages_editMessage)) && FileRefController.isFileRefError(tL_error.text))) {
            if (obj != null) {
                getFileRefController().requestReference(parentObject, tLObject, msgObj, originalPath, parentMessage, Boolean.valueOf(check), delayedMessage, Boolean.valueOf(scheduled));
                return;
            } else if (delayedMessage != null) {
                final TLRPC.Message message = newMsgObj;
                final boolean z = scheduled;
                final TLObject tLObject2 = req;
                final DelayedMessage delayedMessage2 = delayedMessage;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        SendMessagesHelper.this.removeFromSendingMessages(message.id, z);
                        TLObject tLObject = tLObject2;
                        if (tLObject instanceof TLRPC.TL_messages_sendMedia) {
                            TLRPC.TL_messages_sendMedia request = (TLRPC.TL_messages_sendMedia) tLObject;
                            if (request.media instanceof TLRPC.TL_inputMediaPhoto) {
                                request.media = delayedMessage2.inputUploadMedia;
                            } else if (request.media instanceof TLRPC.TL_inputMediaDocument) {
                                request.media = delayedMessage2.inputUploadMedia;
                            }
                        } else if (tLObject instanceof TLRPC.TL_messages_editMessage) {
                            TLRPC.TL_messages_editMessage request2 = (TLRPC.TL_messages_editMessage) tLObject;
                            if (request2.media instanceof TLRPC.TL_inputMediaPhoto) {
                                request2.media = delayedMessage2.inputUploadMedia;
                            } else if (request2.media instanceof TLRPC.TL_inputMediaDocument) {
                                request2.media = delayedMessage2.inputUploadMedia;
                            }
                        }
                        delayedMessage2.performMediaUpload = true;
                        SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2);
                    }
                });
                return;
            }
        }
        if (tLObject instanceof TLRPC.TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable(error, newMsgObj, response, msgObj, originalPath, scheduled, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.Message f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ MessageObject f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ TLObject f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$35$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(error, newMsgObj, response, msgObj, scheduled, originalPath, req) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.Message f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ MessageObject f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ TLObject f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$43$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TLRPC.TL_error error, TLRPC.Message newMsgObj, TLObject response, MessageObject msgObj, String originalPath, boolean scheduled, TLObject req) {
        TLRPC.Message message;
        TLRPC.Message message2 = newMsgObj;
        boolean z = scheduled;
        if (error == null) {
            String attachPath = message2.attachPath;
            TLRPC.Updates updates = (TLRPC.Updates) response;
            ArrayList<TLRPC.Update> updatesArr = ((TLRPC.Updates) response).updates;
            int a = 0;
            while (true) {
                if (a >= updatesArr.size()) {
                    message = null;
                    break;
                }
                TLRPC.Update update = updatesArr.get(a);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    message = ((TLRPC.TL_updateEditMessage) update).message;
                    break;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    message = ((TLRPC.TL_updateEditChannelMessage) update).message;
                    break;
                } else if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                    message = ((TLRPC.TL_updateNewScheduledMessage) update).message;
                    break;
                } else {
                    a++;
                }
            }
            if (message != null) {
                ImageLoader.saveMessageThumbs(message);
                updateMediaPaths(msgObj, message, message.id, originalPath, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable(updates, message2, z) {
                private final /* synthetic */ TLRPC.Updates f$1;
                private final /* synthetic */ TLRPC.Message f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$34$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                }
            });
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(attachPath);
            }
            MessageObject messageObject = msgObj;
            TLObject tLObject = req;
            return;
        }
        AlertsCreator.processError(this.currentAccount, error, (BaseFragment) null, req, new Object[0]);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(message2.attachPath);
        }
        removeFromSendingMessages(message2.id, z);
        revertEditingMessageObject(msgObj);
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TLRPC.Updates updates, TLRPC.Message newMsgObj, boolean scheduled) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj, scheduled) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$33$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TLRPC.Message newMsgObj, boolean scheduled) {
        processSentMessage(newMsgObj.id);
        removeFromSendingMessages(newMsgObj.id, scheduled);
    }

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TLRPC.TL_error error, TLRPC.Message newMsgObj, TLObject response, MessageObject msgObj, boolean scheduled, String originalPath, TLObject req) {
        boolean isSentError;
        String attachPath;
        ArrayList<TLRPC.Message> sentMessages;
        int existFlags;
        TLRPC.Message message;
        int existFlags2;
        TLRPC.Message message2;
        TLRPC.Message message3;
        TLRPC.TL_error tL_error = error;
        TLRPC.Message message4 = newMsgObj;
        TLObject tLObject = response;
        boolean z = scheduled;
        boolean isSentError2 = false;
        if (tL_error == null) {
            int oldId = message4.id;
            ArrayList<TLRPC.Message> sentMessages2 = new ArrayList<>();
            String attachPath2 = message4.attachPath;
            if (tLObject instanceof TLRPC.TL_updateShortSentMessage) {
                TLRPC.TL_updateShortSentMessage res = (TLRPC.TL_updateShortSentMessage) tLObject;
                TLRPC.TL_updateShortSentMessage res2 = res;
                attachPath = attachPath2;
                sentMessages = sentMessages2;
                updateMediaPaths(msgObj, (TLRPC.Message) null, res.id, (String) null, false);
                int existFlags3 = msgObj.getMediaExistanceFlags();
                int i = res2.id;
                message4.id = i;
                message4.local_id = i;
                message4.date = res2.date;
                message4.entities = res2.entities;
                message4.out = res2.out;
                if (res2.media != null) {
                    message4.media = res2.media;
                    message4.flags |= 512;
                    ImageLoader.saveMessageThumbs(newMsgObj);
                }
                if ((res2.media instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(res2.message)) {
                    message4.message = res2.message;
                }
                if (!message4.entities.isEmpty()) {
                    message4.flags |= 128;
                }
                Utilities.stageQueue.postRunnable(new Runnable(res2) {
                    private final /* synthetic */ TLRPC.TL_updateShortSentMessage f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$36$SendMessagesHelper(this.f$1);
                    }
                });
                sentMessages.add(message4);
                existFlags = existFlags3;
                isSentError = false;
            } else {
                attachPath = attachPath2;
                sentMessages = sentMessages2;
                if (tLObject instanceof TLRPC.Updates) {
                    TLRPC.Updates updates = (TLRPC.Updates) tLObject;
                    ArrayList<TLRPC.Update> updatesArr = ((TLRPC.Updates) tLObject).updates;
                    int a = 0;
                    while (true) {
                        if (a >= updatesArr.size()) {
                            message = null;
                            break;
                        }
                        TLRPC.Update update = updatesArr.get(a);
                        if (update instanceof TLRPC.TL_updateNewMessage) {
                            TLRPC.TL_updateNewMessage newMessage = (TLRPC.TL_updateNewMessage) update;
                            TLRPC.Message message5 = newMessage.message;
                            sentMessages.add(message5);
                            Utilities.stageQueue.postRunnable(new Runnable(newMessage) {
                                private final /* synthetic */ TLRPC.TL_updateNewMessage f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    SendMessagesHelper.this.lambda$null$37$SendMessagesHelper(this.f$1);
                                }
                            });
                            updatesArr.remove(a);
                            message = message5;
                            break;
                        } else if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                            TLRPC.TL_updateNewChannelMessage newMessage2 = (TLRPC.TL_updateNewChannelMessage) update;
                            TLRPC.Message message6 = newMessage2.message;
                            TLRPC.Message message7 = message6;
                            sentMessages.add(message6);
                            if ((message4.flags & Integer.MIN_VALUE) != 0) {
                                message2 = message7;
                                newMessage2.message.flags |= Integer.MIN_VALUE;
                            } else {
                                message2 = message7;
                            }
                            Utilities.stageQueue.postRunnable(new Runnable(newMessage2) {
                                private final /* synthetic */ TLRPC.TL_updateNewChannelMessage f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    SendMessagesHelper.this.lambda$null$38$SendMessagesHelper(this.f$1);
                                }
                            });
                            updatesArr.remove(a);
                            message = message2;
                        } else if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                            TLRPC.TL_updateNewScheduledMessage newMessage3 = (TLRPC.TL_updateNewScheduledMessage) update;
                            TLRPC.Message message8 = newMessage3.message;
                            TLRPC.Message message9 = message8;
                            sentMessages.add(message8);
                            if ((message4.flags & Integer.MIN_VALUE) != 0) {
                                message3 = message9;
                                newMessage3.message.flags |= Integer.MIN_VALUE;
                            } else {
                                message3 = message9;
                            }
                            updatesArr.remove(a);
                            message = message3;
                        } else {
                            a++;
                        }
                    }
                    if (message != null) {
                        ImageLoader.saveMessageThumbs(message);
                        if (!z) {
                            Integer value = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                            if (value == null) {
                                value = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value);
                            }
                            message.unread = value.intValue() < message.id;
                        }
                        ArrayList<TLRPC.Update> arrayList = updatesArr;
                        updateMediaPaths(msgObj, message, message.id, originalPath, false);
                        existFlags2 = msgObj.getMediaExistanceFlags();
                        message4.id = message.id;
                        if (!(message4.message == null || message.message == null)) {
                            message4.message = message.message;
                            AndroidUtilities.runOnUIThread(new Runnable(message) {
                                private final /* synthetic */ TLRPC.Message f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    SendMessagesHelper.this.lambda$null$39$SendMessagesHelper(this.f$1);
                                }
                            });
                        }
                    } else {
                        isSentError2 = true;
                        existFlags2 = 0;
                    }
                    Utilities.stageQueue.postRunnable(new Runnable(updates) {
                        private final /* synthetic */ TLRPC.Updates f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$40$SendMessagesHelper(this.f$1);
                        }
                    });
                    existFlags = existFlags2;
                    isSentError = isSentError2;
                } else {
                    existFlags = 0;
                    isSentError = false;
                }
            }
            if (MessageObject.isLiveLocationMessage(newMsgObj)) {
                getLocationController().addSharingLocation(message4.dialog_id, message4.id, message4.media.period, newMsgObj);
            }
            if (!isSentError) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message4.send_state = 0;
                getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(message4.id), message4, Long.valueOf(message4.dialog_id), 0L, Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
                $$Lambda$SendMessagesHelper$dYh3CdtgPESkF58A1AeEUEwWMjM r11 = r0;
                ArrayList<TLRPC.Message> arrayList2 = sentMessages;
                ArrayList<TLRPC.Message> arrayList3 = sentMessages;
                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                int i2 = oldId;
                $$Lambda$SendMessagesHelper$dYh3CdtgPESkF58A1AeEUEwWMjM r0 = new Runnable(newMsgObj, oldId, scheduled, arrayList2, existFlags, attachPath) {
                    private final /* synthetic */ TLRPC.Message f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ ArrayList f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ String f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$42$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                };
                storageQueue.postRunnable(r11);
            } else {
                ArrayList<TLRPC.Message> arrayList4 = sentMessages;
            }
            TLObject tLObject2 = req;
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, (BaseFragment) null, req, new Object[0]);
            isSentError = true;
        }
        if (isSentError) {
            getMessagesStorage().markMessageAsSendError(message4, z);
            message4.send_state = 2;
            getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message4.id));
            processSentMessage(message4.id);
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(message4.attachPath);
            }
            removeFromSendingMessages(message4.id, z);
        }
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(TLRPC.TL_updateShortSentMessage res) {
        getMessagesController().processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(TLRPC.TL_updateNewMessage newMessage) {
        getMessagesController().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$38$SendMessagesHelper(TLRPC.TL_updateNewChannelMessage newMessage) {
        getMessagesController().processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TLRPC.Message finalMessage) {
        getNotificationCenter().postNotificationName(NotificationCenter.updateChatNewmsgMentionText, finalMessage);
    }

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TLRPC.Message newMsgObj, int oldId, boolean scheduled, ArrayList sentMessages, int existFlags, String attachPath) {
        TLRPC.Message message = newMsgObj;
        getMessagesStorage().updateMessageStateAndId(message.random_id, Integer.valueOf(oldId), message.id, 0, false, message.to_id.channel_id, scheduled ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) sentMessages, true, true, false, 0, scheduled);
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj, oldId, existFlags, scheduled) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$41$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(attachPath);
            return;
        }
        String str = attachPath;
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TLRPC.Message newMsgObj, int oldId, int existFlags, boolean scheduled) {
        getMediaDataController().increasePeerRaiting(newMsgObj.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), 0L, Integer.valueOf(existFlags), Boolean.valueOf(scheduled));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId, scheduled);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$46$SendMessagesHelper(TLRPC.Message newMsgObj) {
        AndroidUtilities.runOnUIThread(new Runnable(newMsgObj, newMsgObj.id) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$45$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$45$SendMessagesHelper(TLRPC.Message newMsgObj, int msg_id) {
        newMsgObj.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
    }

    private void updateMediaPaths(MessageObject newMsgObj, TLRPC.Message sentMessage, int newMsgId, String originalPath, boolean post) {
        String str;
        byte[] oldWaveform;
        String str2;
        TLRPC.PhotoSize size2;
        File cacheFile2;
        String newKey;
        TLRPC.PhotoSize strippedNew;
        TLRPC.PhotoSize strippedNew2;
        TLRPC.PhotoSize strippedNew3;
        MessageObject messageObject = newMsgObj;
        TLRPC.Message message = sentMessage;
        String str3 = originalPath;
        boolean z = post;
        TLRPC.Message newMsg = messageObject.messageOwner;
        if (newMsg.media != null) {
            TLRPC.PhotoSize strippedOld = null;
            TLRPC.PhotoSize strippedNew4 = null;
            TLObject photoObject = null;
            if (newMsg.media.photo != null) {
                strippedOld = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.photo.sizes, 40);
                if (message == null || message.media == null || message.media.photo == null) {
                    strippedNew4 = strippedOld;
                } else {
                    strippedNew4 = FileLoader.getClosestPhotoSizeWithSize(message.media.photo.sizes, 40);
                }
                photoObject = newMsg.media.photo;
            } else if (newMsg.media.document != null) {
                strippedOld = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.document.thumbs, 40);
                if (message == null || message.media == null || message.media.document == null) {
                    strippedNew3 = strippedOld;
                } else {
                    strippedNew3 = FileLoader.getClosestPhotoSizeWithSize(message.media.document.thumbs, 40);
                }
                photoObject = newMsg.media.document;
            } else if (newMsg.media.webpage != null) {
                if (newMsg.media.webpage.photo != null) {
                    strippedOld = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.webpage.photo.sizes, 40);
                    if (message == null || message.media == null || message.media.webpage == null || message.media.webpage.photo == null) {
                        strippedNew2 = strippedOld;
                    } else {
                        strippedNew2 = FileLoader.getClosestPhotoSizeWithSize(message.media.webpage.photo.sizes, 40);
                    }
                    photoObject = newMsg.media.webpage.photo;
                } else if (newMsg.media.webpage.document != null) {
                    strippedOld = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.webpage.document.thumbs, 40);
                    if (message == null || message.media == null || message.media.webpage == null || message.media.webpage.document == null) {
                        strippedNew = strippedOld;
                    } else {
                        strippedNew = FileLoader.getClosestPhotoSizeWithSize(message.media.webpage.document.thumbs, 40);
                    }
                    photoObject = newMsg.media.webpage.document;
                }
            }
            if (!(strippedNew4 instanceof TLRPC.TL_photoStrippedSize) || !(strippedOld instanceof TLRPC.TL_photoStrippedSize)) {
                int i = newMsgId;
            } else {
                String oldKey = "stripped" + FileRefController.getKeyForParentObject(newMsgObj);
                if (message != null) {
                    newKey = "stripped" + FileRefController.getKeyForParentObject(sentMessage);
                    int i2 = newMsgId;
                } else {
                    newKey = "strippedmessage" + newMsgId + "_" + newMsgObj.getChannelId();
                }
                ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForObject(strippedNew4, photoObject), z);
            }
        } else {
            int i3 = newMsgId;
        }
        if (message != null) {
            long j = -2147483648L;
            if ((message.media instanceof TLRPC.TL_messageMediaPhoto) && message.media.photo != null && (newMsg.media instanceof TLRPC.TL_messageMediaPhoto) && newMsg.media.photo != null) {
                if (message.media.ttl_seconds == 0 && !messageObject.scheduled) {
                    getMessagesStorage().putSentFile(str3, message.media.photo, 0, "sent_" + message.to_id.channel_id + "_" + message.id);
                }
                if (newMsg.media.photo.sizes.size() != 1 || !(newMsg.media.photo.sizes.get(0).location instanceof TLRPC.TL_fileLocationUnavailable)) {
                    int a = 0;
                    while (a < message.media.photo.sizes.size()) {
                        TLRPC.PhotoSize size = message.media.photo.sizes.get(a);
                        if (size != null && size.location != null && !(size instanceof TLRPC.TL_photoSizeEmpty) && size.type != null) {
                            int b = 0;
                            while (true) {
                                if (b >= newMsg.media.photo.sizes.size()) {
                                    break;
                                }
                                size2 = newMsg.media.photo.sizes.get(b);
                                if (size2 == null || size2.location == null || size2.type == null || ((size2.location.volume_id != j || !size.type.equals(size2.type)) && !(size.w == size2.w && size.h == size2.h))) {
                                    b++;
                                    int i4 = newMsgId;
                                    String str4 = originalPath;
                                    j = -2147483648L;
                                }
                            }
                            String fileName = size2.location.volume_id + "_" + size2.location.local_id;
                            String fileName2 = size.location.volume_id + "_" + size.location.local_id;
                            if (!fileName.equals(fileName2)) {
                                File cacheFile = new File(FileLoader.getDirectory(4), fileName + ".jpg");
                                if (message.media.ttl_seconds != 0 || (message.media.photo.sizes.size() != 1 && size.w <= 90 && size.h <= 90)) {
                                    cacheFile2 = new File(FileLoader.getDirectory(4), fileName2 + ".jpg");
                                } else {
                                    cacheFile2 = FileLoader.getPathToAttach(size);
                                }
                                cacheFile.renameTo(cacheFile2);
                                ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, ImageLocation.getForPhoto(size, message.media.photo), z);
                                size2.location = size.location;
                                size2.size = size.size;
                            }
                        }
                        a++;
                        int i5 = newMsgId;
                        String str5 = originalPath;
                        j = -2147483648L;
                    }
                } else {
                    newMsg.media.photo.sizes = message.media.photo.sizes;
                }
                message.message = newMsg.message;
                message.attachPath = newMsg.attachPath;
                newMsg.media.photo.id = message.media.photo.id;
                newMsg.media.photo.access_hash = message.media.photo.access_hash;
                String str6 = originalPath;
            } else if (!(message.media instanceof TLRPC.TL_messageMediaDocument) || message.media.document == null || !(newMsg.media instanceof TLRPC.TL_messageMediaDocument) || newMsg.media.document == null) {
                String str7 = originalPath;
                if ((message.media instanceof TLRPC.TL_messageMediaContact) && (newMsg.media instanceof TLRPC.TL_messageMediaContact)) {
                    newMsg.media = message.media;
                } else if (message.media instanceof TLRPC.TL_messageMediaWebPage) {
                    newMsg.media = message.media;
                } else if (message.media instanceof TLRPC.TL_messageMediaGeo) {
                    message.media.geo.lat = newMsg.media.geo.lat;
                    message.media.geo._long = newMsg.media.geo._long;
                } else if (message.media instanceof TLRPC.TL_messageMediaGame) {
                    newMsg.media = message.media;
                    if ((newMsg.media instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(message.message)) {
                        newMsg.entities = message.entities;
                        newMsg.message = message.message;
                    }
                } else if (message.media instanceof TLRPC.TL_messageMediaPoll) {
                    newMsg.media = message.media;
                }
            } else {
                if (message.media.ttl_seconds == 0) {
                    boolean isVideo = MessageObject.isVideoMessage(sentMessage);
                    if (!isVideo && !MessageObject.isGifMessage(sentMessage)) {
                        str2 = originalPath;
                    } else if (MessageObject.isGifDocument(message.media.document) == MessageObject.isGifDocument(newMsg.media.document)) {
                        if (!messageObject.scheduled) {
                            str = originalPath;
                            getMessagesStorage().putSentFile(str, message.media.document, 2, "sent_" + message.to_id.channel_id + "_" + message.id);
                        } else {
                            str = originalPath;
                        }
                        if (isVideo) {
                            message.attachPath = newMsg.attachPath;
                        }
                    } else {
                        str2 = originalPath;
                    }
                    if (!MessageObject.isVoiceMessage(sentMessage) && !MessageObject.isRoundVideoMessage(sentMessage) && !messageObject.scheduled) {
                        getMessagesStorage().putSentFile(str, message.media.document, 1, "sent_" + message.to_id.channel_id + "_" + message.id);
                    }
                } else {
                    str = originalPath;
                }
                TLRPC.PhotoSize size22 = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.document.thumbs, 320);
                TLRPC.PhotoSize size3 = FileLoader.getClosestPhotoSizeWithSize(message.media.document.thumbs, 320);
                if (size22 != null && size22.location != null && size22.location.volume_id == -2147483648L && size3 != null && size3.location != null && !(size3 instanceof TLRPC.TL_photoSizeEmpty) && !(size22 instanceof TLRPC.TL_photoSizeEmpty)) {
                    String fileName3 = size22.location.volume_id + "_" + size22.location.local_id;
                    String fileName22 = size3.location.volume_id + "_" + size3.location.local_id;
                    if (!fileName3.equals(fileName22)) {
                        new File(FileLoader.getDirectory(4), fileName3 + ".jpg").renameTo(new File(FileLoader.getDirectory(4), fileName22 + ".jpg"));
                        ImageLoader.getInstance().replaceImageInCache(fileName3, fileName22, ImageLocation.getForDocument(size3, message.media.document), z);
                        size22.location = size3.location;
                        size22.size = size3.size;
                    }
                } else if (size22 != null && MessageObject.isStickerMessage(sentMessage) && size22.location != null) {
                    size3.location = size22.location;
                } else if (size22 == null || ((size22 != null && (size22.location instanceof TLRPC.TL_fileLocationUnavailable)) || (size22 instanceof TLRPC.TL_photoSizeEmpty))) {
                    newMsg.media.document.thumbs = message.media.document.thumbs;
                }
                newMsg.media.document.dc_id = message.media.document.dc_id;
                newMsg.media.document.id = message.media.document.id;
                newMsg.media.document.access_hash = message.media.document.access_hash;
                int a2 = 0;
                while (true) {
                    if (a2 >= newMsg.media.document.attributes.size()) {
                        oldWaveform = null;
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = newMsg.media.document.attributes.get(a2);
                    if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                        oldWaveform = attribute.waveform;
                        break;
                    }
                    a2++;
                }
                newMsg.media.document.attributes = message.media.document.attributes;
                if (oldWaveform != null) {
                    for (int a3 = 0; a3 < newMsg.media.document.attributes.size(); a3++) {
                        TLRPC.DocumentAttribute attribute2 = newMsg.media.document.attributes.get(a3);
                        if (attribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                            attribute2.waveform = oldWaveform;
                            attribute2.flags |= 4;
                        }
                    }
                }
                newMsg.media.document.size = message.media.document.size;
                newMsg.media.document.mime_type = message.media.document.mime_type;
                if ((message.flags & 4) == 0 && MessageObject.isOut(sentMessage)) {
                    if (MessageObject.isNewGifDocument(message.media.document)) {
                        getMediaDataController().addRecentGif(message.media.document, message.date);
                    } else if (MessageObject.isStickerDocument(message.media.document) || MessageObject.isAnimatedStickerDocument(message.media.document)) {
                        getMediaDataController().addRecentSticker(0, sentMessage, message.media.document, message.date, false);
                    }
                }
                if (newMsg.attachPath == null || !newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath())) {
                    message.attachPath = newMsg.attachPath;
                    message.message = newMsg.message;
                    return;
                }
                File cacheFile3 = new File(newMsg.attachPath);
                File cacheFile22 = FileLoader.getPathToAttach(message.media.document, message.media.ttl_seconds != 0);
                if (!cacheFile3.renameTo(cacheFile22)) {
                    if (cacheFile3.exists()) {
                        message.attachPath = newMsg.attachPath;
                    } else {
                        messageObject.attachPathExists = false;
                    }
                    messageObject.mediaExists = cacheFile22.exists();
                    message.message = newMsg.message;
                } else if (MessageObject.isVideoMessage(sentMessage)) {
                    messageObject.attachPathExists = true;
                } else {
                    messageObject.mediaExists = messageObject.attachPathExists;
                    messageObject.attachPathExists = false;
                    newMsg.attachPath = "";
                    if (str != null && str.startsWith("http")) {
                        getMessagesStorage().addRecentLocalFile(str, cacheFile22.toString(), newMsg.media.document);
                    }
                }
            }
        }
    }

    private void putToDelayedMessages(String location, DelayedMessage message) {
        ArrayList<DelayedMessage> arrayList = this.delayedMessages.get(location);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.delayedMessages.put(location, arrayList);
        }
        arrayList.add(message);
    }

    /* access modifiers changed from: protected */
    public ArrayList<DelayedMessage> getDelayedMessages(String location) {
        return this.delayedMessages.get(location);
    }

    public long getNextRandomId() {
        long val = 0;
        while (val == 0) {
            val = Utilities.random.nextLong();
        }
        return val;
    }

    public void checkUnsentMessages() {
        getMessagesStorage().getUnsentMessages(1000);
    }

    /* access modifiers changed from: protected */
    public void processUnsentMessages(ArrayList<TLRPC.Message> messages, ArrayList<TLRPC.Message> scheduledMessages, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, ArrayList<TLRPC.EncryptedChat> encryptedChats) {
        AndroidUtilities.runOnUIThread(new Runnable(users, chats, encryptedChats, messages, scheduledMessages) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ ArrayList f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$processUnsentMessages$47$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processUnsentMessages$47$SendMessagesHelper(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList messages, ArrayList scheduledMessages) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        for (int a = 0; a < messages.size(); a++) {
            retrySendMessage(new MessageObject(this.currentAccount, (TLRPC.Message) messages.get(a), false), true);
        }
        if (scheduledMessages != null) {
            for (int a2 = 0; a2 < scheduledMessages.size(); a2++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) scheduledMessages.get(a2), false);
                messageObject.scheduled = true;
                retrySendMessage(messageObject, true);
            }
        }
    }

    public TLRPC.TL_photo generatePhotoSizes(String path, Uri imageUri, boolean blnOriginalImg) {
        return generatePhotoSizes((TLRPC.TL_photo) null, path, imageUri, blnOriginalImg);
    }

    public TLRPC.TL_photo generatePhotoSizes(TLRPC.TL_photo photo, String path, Uri imageUri, boolean blnOriginalImg) {
        Bitmap bitmap;
        boolean isPng;
        TLRPC.PhotoSize size;
        TLRPC.TL_photo photo2;
        String str = path;
        Uri uri = imageUri;
        Bitmap bitmap2 = ImageLoader.loadBitmap(str, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (bitmap2 == null) {
            bitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        } else {
            bitmap = bitmap2;
        }
        if (str != null) {
            isPng = str.endsWith(".png");
        } else {
            isPng = false;
        }
        ArrayList<TLRPC.PhotoSize> sizes = new ArrayList<>();
        TLRPC.PhotoSize size2 = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, true, isPng);
        if (size2 != null) {
            sizes.add(size2);
        }
        if (blnOriginalImg) {
            try {
                size = ImageLoader.SaveImageWithOriginalInternal((TLRPC.PhotoSize) null, str, false);
            } catch (Throwable th) {
                FileLog.e(th);
                size = null;
            }
        } else {
            size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, isPng);
        }
        if (size != null) {
            sizes.add(size);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (sizes.isEmpty()) {
            return null;
        }
        getUserConfig().saveConfig(false);
        if (photo == null) {
            photo2 = new TLRPC.TL_photo();
        } else {
            photo2 = photo;
        }
        photo2.date = getConnectionsManager().getCurrentTime();
        photo2.sizes = sizes;
        photo2.file_reference = new byte[0];
        return photo2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v82, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: java.lang.String} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0170 A[SYNTHETIC, Splitter:B:105:0x0170] */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0188 A[SYNTHETIC, Splitter:B:111:0x0188] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ef  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01f5  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0249  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0255 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x030c  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x043a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0519  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x051c  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0527  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x052d  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0537  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x053f  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0543  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(im.bclpbkiauv.messenger.AccountInstance r46, java.lang.String r47, java.lang.String r48, android.net.Uri r49, java.lang.String r50, long r51, im.bclpbkiauv.messenger.MessageObject r53, java.lang.CharSequence r54, java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC.MessageEntity> r55, im.bclpbkiauv.messenger.MessageObject r56, boolean r57, boolean r58, int r59) {
        /*
            r0 = r47
            r1 = r48
            r2 = r49
            r3 = r50
            r4 = 0
            if (r0 == 0) goto L_0x0011
            int r5 = r47.length()
            if (r5 != 0) goto L_0x0014
        L_0x0011:
            if (r2 != 0) goto L_0x0014
            return r4
        L_0x0014:
            if (r2 == 0) goto L_0x001d
            boolean r5 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r49)
            if (r5 == 0) goto L_0x001d
            return r4
        L_0x001d:
            if (r0 == 0) goto L_0x002f
            java.io.File r5 = new java.io.File
            r5.<init>(r0)
            android.net.Uri r5 = android.net.Uri.fromFile(r5)
            boolean r5 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r5)
            if (r5 == 0) goto L_0x002f
            return r4
        L_0x002f:
            android.webkit.MimeTypeMap r15 = android.webkit.MimeTypeMap.getSingleton()
            r5 = 0
            r6 = 0
            if (r2 == 0) goto L_0x0057
            r7 = 0
            if (r3 == 0) goto L_0x003e
            java.lang.String r6 = r15.getExtensionFromMimeType(r3)
        L_0x003e:
            if (r6 != 0) goto L_0x0044
            java.lang.String r6 = "txt"
            goto L_0x0045
        L_0x0044:
            r7 = 1
        L_0x0045:
            java.lang.String r0 = im.bclpbkiauv.messenger.MediaController.copyFileToCache(r2, r6)
            if (r0 != 0) goto L_0x004c
            return r4
        L_0x004c:
            if (r7 != 0) goto L_0x0053
            r6 = 0
            r18 = r6
            r6 = r0
            goto L_0x005a
        L_0x0053:
            r18 = r6
            r6 = r0
            goto L_0x005a
        L_0x0057:
            r18 = r6
            r6 = r0
        L_0x005a:
            java.io.File r0 = new java.io.File
            r0.<init>(r6)
            r19 = r0
            boolean r0 = r19.exists()
            if (r0 == 0) goto L_0x0577
            long r7 = r19.length()
            r12 = 0
            int r0 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1))
            if (r0 != 0) goto L_0x0077
            r24 = r6
            r29 = r15
            goto L_0x057b
        L_0x0077:
            r10 = r51
            int r0 = (int) r10
            if (r0 != 0) goto L_0x007e
            r0 = 1
            goto L_0x007f
        L_0x007e:
            r0 = 0
        L_0x007f:
            r14 = r0
            if (r14 != 0) goto L_0x0084
            r0 = 1
            goto L_0x0085
        L_0x0084:
            r0 = 0
        L_0x0085:
            r26 = r0
            java.lang.String r9 = r19.getName()
            java.lang.String r0 = ""
            r8 = -1
            if (r18 == 0) goto L_0x0095
            r0 = r18
            r27 = r0
            goto L_0x00a8
        L_0x0095:
            r4 = 46
            int r4 = r6.lastIndexOf(r4)
            if (r4 == r8) goto L_0x00a6
            int r8 = r4 + 1
            java.lang.String r0 = r6.substring(r8)
            r27 = r0
            goto L_0x00a8
        L_0x00a6:
            r27 = r0
        L_0x00a8:
            java.lang.String r4 = r27.toLowerCase()
            r8 = 0
            r17 = 0
            r20 = 0
            r21 = 0
            java.lang.String r11 = "mp3"
            boolean r0 = r4.equals(r11)
            java.lang.String r10 = "flac"
            r22 = r11
            java.lang.String r11 = "opus"
            java.lang.String r12 = "m4a"
            java.lang.String r13 = "ogg"
            if (r0 != 0) goto L_0x0195
            boolean r0 = r4.equals(r12)
            if (r0 == 0) goto L_0x00cd
            goto L_0x0195
        L_0x00cd:
            boolean r0 = r4.equals(r11)
            if (r0 != 0) goto L_0x00eb
            boolean r0 = r4.equals(r13)
            if (r0 != 0) goto L_0x00eb
            boolean r0 = r4.equals(r10)
            if (r0 == 0) goto L_0x00e0
            goto L_0x00eb
        L_0x00e0:
            r2 = r8
            r3 = r17
            r30 = r20
            r7 = r21
            r23 = 0
            goto L_0x01b7
        L_0x00eb:
            r25 = 0
            android.media.MediaMetadataRetriever r0 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x016a }
            r0.<init>()     // Catch:{ Exception -> 0x016a }
            r25 = r0
            java.lang.String r0 = r19.getAbsolutePath()     // Catch:{ Exception -> 0x0163, all -> 0x015e }
            r7 = r25
            r7.setDataSource(r0)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            r0 = 9
            java.lang.String r0 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            if (r0 == 0) goto L_0x0133
            long r2 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            float r2 = (float) r2     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            r3 = 1148846080(0x447a0000, float:1000.0)
            float r2 = r2 / r3
            double r2 = (double) r2     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            double r2 = java.lang.Math.ceil(r2)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            int r2 = (int) r2
            r3 = 7
            java.lang.String r3 = r7.extractMetadata(r3)     // Catch:{ Exception -> 0x012d, all -> 0x0125 }
            r17 = r3
            r3 = 2
            java.lang.String r21 = r7.extractMetadata(r3)     // Catch:{ Exception -> 0x012d, all -> 0x0125 }
            r3 = r21
            r21 = r2
            r8 = r3
            goto L_0x0133
        L_0x0125:
            r0 = move-exception
            r21 = r2
            r25 = r7
            r2 = r0
            goto L_0x0186
        L_0x012d:
            r0 = move-exception
            r21 = r2
            r25 = r7
            goto L_0x016b
        L_0x0133:
            if (r56 != 0) goto L_0x0149
            boolean r2 = r4.equals(r13)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            if (r2 == 0) goto L_0x0149
            java.lang.String r2 = r19.getAbsolutePath()     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            int r2 = im.bclpbkiauv.messenger.MediaController.isOpusFile(r2)     // Catch:{ Exception -> 0x015a, all -> 0x0155 }
            r3 = 1
            if (r2 != r3) goto L_0x0149
            r2 = 1
            r20 = r2
        L_0x0149:
            r7.release()     // Catch:{ Exception -> 0x014e }
            goto L_0x017c
        L_0x014e:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x017c
        L_0x0155:
            r0 = move-exception
            r2 = r0
            r25 = r7
            goto L_0x0186
        L_0x015a:
            r0 = move-exception
            r25 = r7
            goto L_0x016b
        L_0x015e:
            r0 = move-exception
            r7 = r25
            r2 = r0
            goto L_0x0186
        L_0x0163:
            r0 = move-exception
            r7 = r25
            goto L_0x016b
        L_0x0167:
            r0 = move-exception
            r2 = r0
            goto L_0x0186
        L_0x016a:
            r0 = move-exception
        L_0x016b:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0167 }
            if (r25 == 0) goto L_0x017b
            r25.release()     // Catch:{ Exception -> 0x0174 }
            goto L_0x017b
        L_0x0174:
            r0 = move-exception
            r2 = r0
            r0 = r2
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x017c
        L_0x017b:
        L_0x017c:
            r2 = r8
            r3 = r17
            r30 = r20
            r7 = r21
            r23 = 0
            goto L_0x01b7
        L_0x0186:
            if (r25 == 0) goto L_0x0193
            r25.release()     // Catch:{ Exception -> 0x018c }
            goto L_0x0193
        L_0x018c:
            r0 = move-exception
            r3 = r0
            r0 = r3
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0194
        L_0x0193:
        L_0x0194:
            throw r2
        L_0x0195:
            im.bclpbkiauv.messenger.audioinfo.AudioInfo r0 = im.bclpbkiauv.messenger.audioinfo.AudioInfo.getAudioInfo(r19)
            if (r0 == 0) goto L_0x01ae
            long r2 = r0.getDuration()
            r23 = 0
            int r7 = (r2 > r23 ? 1 : (r2 == r23 ? 0 : -1))
            if (r7 == 0) goto L_0x01b0
            java.lang.String r8 = r0.getArtist()
            java.lang.String r17 = r0.getTitle()
            goto L_0x01b0
        L_0x01ae:
            r23 = 0
        L_0x01b0:
            r2 = r8
            r3 = r17
            r30 = r20
            r7 = r21
        L_0x01b7:
            java.lang.String r8 = ""
            if (r7 == 0) goto L_0x01ef
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            r5 = r0
            r5.duration = r7
            r5.title = r3
            r5.performer = r2
            java.lang.String r0 = r5.title
            if (r0 != 0) goto L_0x01cd
            r5.title = r8
        L_0x01cd:
            int r0 = r5.flags
            r17 = 1
            r0 = r0 | 1
            r5.flags = r0
            java.lang.String r0 = r5.performer
            if (r0 != 0) goto L_0x01db
            r5.performer = r8
        L_0x01db:
            int r0 = r5.flags
            r17 = 2
            r0 = r0 | 2
            r5.flags = r0
            if (r30 == 0) goto L_0x01eb
            r31 = r2
            r2 = 1
            r5.voice = r2
            goto L_0x01ed
        L_0x01eb:
            r31 = r2
        L_0x01ed:
            r2 = r5
            goto L_0x01f2
        L_0x01ef:
            r31 = r2
            r2 = r5
        L_0x01f2:
            r0 = 0
            if (r1 == 0) goto L_0x0249
            java.lang.String r5 = "attheme"
            boolean r5 = r1.endsWith(r5)
            if (r5 == 0) goto L_0x0205
            r0 = 1
            r32 = r0
            r20 = r9
            r21 = r10
            goto L_0x0251
        L_0x0205:
            if (r2 == 0) goto L_0x0229
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            r17 = r0
            java.lang.String r0 = "audio"
            r5.append(r0)
            r20 = r9
            r21 = r10
            long r9 = r19.length()
            r5.append(r9)
            java.lang.String r0 = r5.toString()
            r1 = r0
            r32 = r17
            goto L_0x0251
        L_0x0229:
            r17 = r0
            r20 = r9
            r21 = r10
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r8)
            long r9 = r19.length()
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            r1 = r0
            r32 = r17
            goto L_0x0251
        L_0x0249:
            r17 = r0
            r20 = r9
            r21 = r10
            r32 = r17
        L_0x0251:
            r0 = 0
            r5 = 0
            if (r32 != 0) goto L_0x02ee
            if (r14 != 0) goto L_0x02ee
            im.bclpbkiauv.messenger.MessagesStorage r9 = r46.getMessagesStorage()
            if (r14 != 0) goto L_0x025f
            r10 = 1
            goto L_0x0260
        L_0x025f:
            r10 = 4
        L_0x0260:
            java.lang.Object[] r9 = r9.getSentFile(r1, r10)
            if (r9 == 0) goto L_0x0274
            r10 = 0
            r17 = r9[r10]
            r0 = r17
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r0
            r10 = 1
            r17 = r9[r10]
            r5 = r17
            java.lang.String r5 = (java.lang.String) r5
        L_0x0274:
            if (r0 != 0) goto L_0x02b7
            boolean r10 = r6.equals(r1)
            if (r10 != 0) goto L_0x02b7
            if (r14 != 0) goto L_0x02b7
            im.bclpbkiauv.messenger.MessagesStorage r10 = r46.getMessagesStorage()
            r17 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            r33 = r7
            r25 = r8
            long r7 = r19.length()
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            if (r14 != 0) goto L_0x029f
            r7 = 1
            goto L_0x02a0
        L_0x029f:
            r7 = 4
        L_0x02a0:
            java.lang.Object[] r9 = r10.getSentFile(r0, r7)
            if (r9 == 0) goto L_0x02b3
            r7 = 0
            r0 = r9[r7]
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r0
            r7 = 1
            r8 = r9[r7]
            r5 = r8
            java.lang.String r5 = (java.lang.String) r5
            r7 = r9
            goto L_0x02c0
        L_0x02b3:
            r7 = r9
            r0 = r17
            goto L_0x02c0
        L_0x02b7:
            r17 = r0
            r33 = r7
            r25 = r8
            r7 = r9
            r0 = r17
        L_0x02c0:
            r17 = 0
            r34 = 0
            r10 = r25
            r25 = -1
            r8 = r14
            r36 = r20
            r9 = r0
            r38 = r10
            r37 = r21
            r10 = r6
            r47 = r3
            r48 = r5
            r3 = r11
            r5 = r22
            r11 = r17
            r39 = r1
            r17 = r7
            r7 = r12
            r1 = r13
            r41 = r14
            r40 = r15
            r14 = r23
            r12 = r34
            ensureMediaThumbExists(r8, r9, r10, r11, r12)
            r34 = r48
            goto L_0x030a
        L_0x02ee:
            r39 = r1
            r47 = r3
            r33 = r7
            r38 = r8
            r3 = r11
            r7 = r12
            r1 = r13
            r41 = r14
            r40 = r15
            r36 = r20
            r37 = r21
            r14 = r23
            r25 = -1
            r8 = r5
            r5 = r22
            r34 = r8
        L_0x030a:
            if (r0 != 0) goto L_0x051c
            im.bclpbkiauv.tgnet.TLRPC$TL_document r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_document
            r8.<init>()
            r8.id = r14
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = r46.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r8.date = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r9 = r0
            r13 = r36
            r9.file_name = r13
            r10 = 0
            byte[] r0 = new byte[r10]
            r8.file_reference = r0
            java.util.ArrayList r0 = r8.attributes
            r0.add(r9)
            long r11 = r19.length()
            int r0 = (int) r11
            r8.size = r0
            r8.dc_id = r10
            if (r2 == 0) goto L_0x0341
            java.util.ArrayList r0 = r8.attributes
            r0.add(r2)
        L_0x0341:
            int r0 = r27.length()
            java.lang.String r10 = "application/octet-stream"
            java.lang.String r11 = "image/webp"
            if (r0 == 0) goto L_0x03d1
            int r0 = r4.hashCode()
            r12 = 5
            r14 = 3
            switch(r0) {
                case 106458: goto L_0x0382;
                case 108272: goto L_0x037a;
                case 109967: goto L_0x0372;
                case 3145576: goto L_0x0368;
                case 3418175: goto L_0x0360;
                case 3645340: goto L_0x0355;
                default: goto L_0x0354;
            }
        L_0x0354:
            goto L_0x038a
        L_0x0355:
            java.lang.String r0 = "webp"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0354
            r0 = 0
            goto L_0x038b
        L_0x0360:
            boolean r0 = r4.equals(r3)
            if (r0 == 0) goto L_0x0354
            r0 = 1
            goto L_0x038b
        L_0x0368:
            r1 = r37
            boolean r0 = r4.equals(r1)
            if (r0 == 0) goto L_0x0354
            r0 = 5
            goto L_0x038b
        L_0x0372:
            boolean r0 = r4.equals(r1)
            if (r0 == 0) goto L_0x0354
            r0 = 4
            goto L_0x038b
        L_0x037a:
            boolean r0 = r4.equals(r5)
            if (r0 == 0) goto L_0x0354
            r0 = 2
            goto L_0x038b
        L_0x0382:
            boolean r0 = r4.equals(r7)
            if (r0 == 0) goto L_0x0354
            r0 = 3
            goto L_0x038b
        L_0x038a:
            r0 = -1
        L_0x038b:
            if (r0 == 0) goto L_0x03cb
            r1 = 1
            if (r0 == r1) goto L_0x03c4
            r1 = 2
            if (r0 == r1) goto L_0x03bd
            if (r0 == r14) goto L_0x03b6
            r1 = 4
            if (r0 == r1) goto L_0x03af
            if (r0 == r12) goto L_0x03a8
            r1 = r40
            java.lang.String r0 = r1.getMimeTypeFromExtension(r4)
            if (r0 == 0) goto L_0x03a5
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03a5:
            r8.mime_type = r10
            goto L_0x03d0
        L_0x03a8:
            r1 = r40
            java.lang.String r0 = "audio/flac"
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03af:
            r1 = r40
            java.lang.String r0 = "audio/ogg"
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03b6:
            r1 = r40
            java.lang.String r0 = "audio/m4a"
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03bd:
            r1 = r40
            java.lang.String r0 = "audio/mpeg"
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03c4:
            r1 = r40
            java.lang.String r0 = "audio/opus"
            r8.mime_type = r0
            goto L_0x03d0
        L_0x03cb:
            r1 = r40
            r8.mime_type = r11
        L_0x03d0:
            goto L_0x03d5
        L_0x03d1:
            r1 = r40
            r8.mime_type = r10
        L_0x03d5:
            java.lang.String r0 = r8.mime_type
            java.lang.String r3 = "image/gif"
            boolean r0 = r0.equals(r3)
            r5 = 0
            r7 = 1119092736(0x42b40000, float:90.0)
            if (r0 == 0) goto L_0x0430
            if (r56 == 0) goto L_0x03f2
            long r14 = r56.getGroupIdForUse()
            r16 = 0
            int r0 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r0 != 0) goto L_0x03ef
            goto L_0x03f2
        L_0x03ef:
            r12 = r41
            goto L_0x0432
        L_0x03f2:
            java.lang.String r0 = r19.getAbsolutePath()     // Catch:{ Exception -> 0x0429 }
            r10 = 1
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r0, r5, r7, r7, r10)     // Catch:{ Exception -> 0x0429 }
            if (r0 == 0) goto L_0x0426
            java.lang.String r10 = "animation.gif"
            r9.file_name = r10     // Catch:{ Exception -> 0x0429 }
            java.util.ArrayList r10 = r8.attributes     // Catch:{ Exception -> 0x0429 }
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated r12 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x0429 }
            r12.<init>()     // Catch:{ Exception -> 0x0429 }
            r10.add(r12)     // Catch:{ Exception -> 0x0429 }
            r10 = 55
            r12 = r41
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r10 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r0, r7, r7, r10, r12)     // Catch:{ Exception -> 0x0424 }
            if (r10 == 0) goto L_0x0420
            java.util.ArrayList r14 = r8.thumbs     // Catch:{ Exception -> 0x0424 }
            r14.add(r10)     // Catch:{ Exception -> 0x0424 }
            int r14 = r8.flags     // Catch:{ Exception -> 0x0424 }
            r15 = 1
            r14 = r14 | r15
            r8.flags = r14     // Catch:{ Exception -> 0x0424 }
        L_0x0420:
            r0.recycle()     // Catch:{ Exception -> 0x0424 }
            goto L_0x0428
        L_0x0424:
            r0 = move-exception
            goto L_0x042c
        L_0x0426:
            r12 = r41
        L_0x0428:
            goto L_0x0432
        L_0x0429:
            r0 = move-exception
            r12 = r41
        L_0x042c:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0432
        L_0x0430:
            r12 = r41
        L_0x0432:
            java.lang.String r0 = r8.mime_type
            boolean r0 = r0.equals(r11)
            if (r0 == 0) goto L_0x04ae
            if (r26 == 0) goto L_0x04ae
            if (r56 != 0) goto L_0x04ae
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r10 = r0
            r14 = 1
            r10.inJustDecodeBounds = r14     // Catch:{ Exception -> 0x046d }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x046d }
            java.lang.String r14 = "r"
            r0.<init>(r6, r14)     // Catch:{ Exception -> 0x046d }
            java.nio.channels.FileChannel r40 = r0.getChannel()     // Catch:{ Exception -> 0x046d }
            java.nio.channels.FileChannel$MapMode r41 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x046d }
            r42 = 0
            int r14 = r6.length()     // Catch:{ Exception -> 0x046d }
            long r14 = (long) r14     // Catch:{ Exception -> 0x046d }
            r44 = r14
            java.nio.MappedByteBuffer r14 = r40.map(r41, r42, r44)     // Catch:{ Exception -> 0x046d }
            int r15 = r14.limit()     // Catch:{ Exception -> 0x046d }
            r7 = 1
            im.bclpbkiauv.messenger.Utilities.loadWebpImage(r5, r14, r15, r10, r7)     // Catch:{ Exception -> 0x046d }
            r0.close()     // Catch:{ Exception -> 0x046d }
            goto L_0x0471
        L_0x046d:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0471:
            int r0 = r10.outWidth
            if (r0 == 0) goto L_0x04ab
            int r0 = r10.outHeight
            if (r0 == 0) goto L_0x04ab
            int r0 = r10.outWidth
            r7 = 800(0x320, float:1.121E-42)
            if (r0 > r7) goto L_0x04ab
            int r0 = r10.outHeight
            if (r0 > r7) goto L_0x04ab
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r7 = r38
            r0.alt = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty r14 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty
            r14.<init>()
            r0.stickerset = r14
            java.util.ArrayList r14 = r8.attributes
            r14.add(r0)
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeImageSize r14 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeImageSize
            r14.<init>()
            int r15 = r10.outWidth
            r14.w = r15
            int r15 = r10.outHeight
            r14.h = r15
            java.util.ArrayList r15 = r8.attributes
            r15.add(r14)
            goto L_0x04b0
        L_0x04ab:
            r7 = r38
            goto L_0x04b0
        L_0x04ae:
            r7 = r38
        L_0x04b0:
            java.lang.String r0 = r8.mime_type
            java.lang.String r10 = "image/"
            boolean r0 = r0.startsWith(r10)
            if (r0 == 0) goto L_0x0519
            java.lang.String r0 = r8.mime_type
            boolean r0 = r0.equals(r3)
            if (r0 != 0) goto L_0x0519
            java.lang.String r0 = r8.mime_type
            boolean r0 = r0.equals(r11)
            if (r0 != 0) goto L_0x0519
            if (r56 == 0) goto L_0x04d9
            long r10 = r56.getGroupIdForUse()
            r14 = 0
            int r0 = (r10 > r14 ? 1 : (r10 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x04d7
            goto L_0x04d9
        L_0x04d7:
            r10 = 1
            goto L_0x051a
        L_0x04d9:
            java.lang.String r0 = r19.getAbsolutePath()     // Catch:{ Exception -> 0x0513 }
            r3 = 1119092736(0x42b40000, float:90.0)
            r10 = 1
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r0, r5, r3, r3, r10)     // Catch:{ Exception -> 0x0511 }
            if (r0 == 0) goto L_0x050f
            r21 = 1119092736(0x42b40000, float:90.0)
            r22 = 1119092736(0x42b40000, float:90.0)
            r23 = 55
            java.lang.String r3 = r8.mime_type     // Catch:{ Exception -> 0x0513 }
            java.lang.String r5 = "image/png"
            boolean r25 = r3.equals(r5)     // Catch:{ Exception -> 0x0513 }
            r20 = r0
            r24 = r12
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r20, (float) r21, (float) r22, (int) r23, (boolean) r24, (boolean) r25)     // Catch:{ Exception -> 0x0513 }
            if (r3 == 0) goto L_0x050a
            java.util.ArrayList r5 = r8.thumbs     // Catch:{ Exception -> 0x0513 }
            r5.add(r3)     // Catch:{ Exception -> 0x0513 }
            int r5 = r8.flags     // Catch:{ Exception -> 0x0513 }
            r10 = 1
            r5 = r5 | r10
            r8.flags = r5     // Catch:{ Exception -> 0x0511 }
            goto L_0x050b
        L_0x050a:
            r10 = 1
        L_0x050b:
            r0.recycle()     // Catch:{ Exception -> 0x0511 }
            goto L_0x0510
        L_0x050f:
            r10 = 1
        L_0x0510:
            goto L_0x051a
        L_0x0511:
            r0 = move-exception
            goto L_0x0515
        L_0x0513:
            r0 = move-exception
            r10 = 1
        L_0x0515:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x051a
        L_0x0519:
            r10 = 1
        L_0x051a:
            r0 = r8
            goto L_0x0525
        L_0x051c:
            r13 = r36
            r7 = r38
            r1 = r40
            r12 = r41
            r10 = 1
        L_0x0525:
            if (r54 == 0) goto L_0x052d
            java.lang.String r3 = r54.toString()
            r14 = r3
            goto L_0x052e
        L_0x052d:
            r14 = r7
        L_0x052e:
            r3 = r12
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r15 = r5
            if (r39 == 0) goto L_0x053f
            java.lang.String r5 = "originalPath"
            r11 = r39
            r15.put(r5, r11)
            goto L_0x0541
        L_0x053f:
            r11 = r39
        L_0x0541:
            if (r57 == 0) goto L_0x054a
            java.lang.String r5 = "forceDocument"
            java.lang.String r7 = "1"
            r15.put(r5, r7)
        L_0x054a:
            r21 = r33
            r20 = 1
            r7 = r0
            r8 = r6
            r10 = r34
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$HHynOPWmdGOLUESA1p4hcpAngIE r22 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$HHynOPWmdGOLUESA1p4hcpAngIE
            r23 = r4
            r4 = r22
            r5 = r56
            r24 = r6
            r6 = r46
            r9 = r15
            r25 = r11
            r11 = r51
            r28 = r13
            r13 = r53
            r29 = r1
            r1 = r15
            r15 = r55
            r16 = r58
            r17 = r59
            r4.<init>(r6, r7, r8, r9, r10, r11, r13, r14, r15, r16, r17)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r22)
            return r20
        L_0x0577:
            r24 = r6
            r29 = r15
        L_0x057b:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.prepareSendingDocumentInternal(im.bclpbkiauv.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, im.bclpbkiauv.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, im.bclpbkiauv.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$48(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities, boolean notify, int scheduleDate) {
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, documentFinal, pathFinal, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(documentFinal, (VideoEditedInfo) null, pathFinal, dialog_id, reply_to_msg, captionFinal, entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, 0, parentFinal);
        }
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String path, String originalPath, Uri uri, String caption, String mine, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        ArrayList<Uri> uris;
        String str = path;
        String str2 = originalPath;
        Uri uri2 = uri;
        if ((str != null && str2 != null) || uri2 != null) {
            ArrayList<String> paths = new ArrayList<>();
            ArrayList<String> originalPaths = new ArrayList<>();
            if (uri2 != null) {
                ArrayList<Uri> uris2 = new ArrayList<>();
                uris2.add(uri2);
                uris = uris2;
            } else {
                uris = null;
            }
            if (str != null) {
                paths.add(str);
                originalPaths.add(str2);
            }
            ArrayList<String> arrayList = originalPaths;
            prepareSendingDocuments(accountInstance, paths, originalPaths, uris, caption, mine, dialog_id, reply_to_msg, inputContent, editingMessageObject, notify, scheduleDate);
        }
    }

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> messageObjects, long dialog_id, MessageObject reply_to_msg, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        new Thread(new Runnable(messageObjects, dialog_id, accountInstance, editingMessageObject, reply_to_msg, notify, scheduleDate) {
            private final /* synthetic */ ArrayList f$0;
            private final /* synthetic */ long f$1;
            private final /* synthetic */ AccountInstance f$2;
            private final /* synthetic */ MessageObject f$3;
            private final /* synthetic */ MessageObject f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ int f$6;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                SendMessagesHelper.lambda$prepareSendingAudioDocuments$50(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        }).start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: java.lang.Object[]} */
    /* JADX WARNING: type inference failed for: r2v7, types: [im.bclpbkiauv.tgnet.TLRPC$Document] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a3 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$50(java.util.ArrayList r24, long r25, im.bclpbkiauv.messenger.AccountInstance r27, im.bclpbkiauv.messenger.MessageObject r28, im.bclpbkiauv.messenger.MessageObject r29, boolean r30, int r31) {
        /*
            r12 = r25
            int r14 = r24.size()
            r0 = 0
            r15 = r0
        L_0x0008:
            if (r15 >= r14) goto L_0x00c9
            r11 = r24
            java.lang.Object r0 = r11.get(r15)
            r10 = r0
            im.bclpbkiauv.messenger.MessageObject r10 = (im.bclpbkiauv.messenger.MessageObject) r10
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r10.messageOwner
            java.lang.String r0 = r0.attachPath
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            r16 = r1
            int r1 = (int) r12
            r2 = 0
            r3 = 1
            if (r1 != 0) goto L_0x0025
            r1 = 1
            goto L_0x0026
        L_0x0025:
            r1 = 0
        L_0x0026:
            r17 = r1
            if (r0 == 0) goto L_0x0044
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r4 = "audio"
            r1.append(r4)
            long r4 = r16.length()
            r1.append(r4)
            java.lang.String r0 = r1.toString()
            r1 = r0
            goto L_0x0045
        L_0x0044:
            r1 = r0
        L_0x0045:
            r0 = 0
            r4 = 0
            if (r17 != 0) goto L_0x006e
            im.bclpbkiauv.messenger.MessagesStorage r5 = r27.getMessagesStorage()
            if (r17 != 0) goto L_0x0051
            r6 = 1
            goto L_0x0052
        L_0x0051:
            r6 = 4
        L_0x0052:
            java.lang.Object[] r18 = r5.getSentFile(r1, r6)
            if (r18 == 0) goto L_0x006e
            r2 = r18[r2]
            r0 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r0
            r2 = r18[r3]
            java.lang.String r2 = (java.lang.String) r2
            r7 = 0
            r8 = 0
            r4 = r17
            r5 = r0
            r6 = r1
            ensureMediaThumbExists(r4, r5, r6, r7, r8)
            r18 = r2
            goto L_0x0070
        L_0x006e:
            r18 = r4
        L_0x0070:
            if (r0 != 0) goto L_0x007e
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r10.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            r0 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r0
            r19 = r0
            goto L_0x0080
        L_0x007e:
            r19 = r0
        L_0x0080:
            if (r17 == 0) goto L_0x0096
            r0 = 32
            long r2 = r12 >> r0
            int r0 = (int) r2
            im.bclpbkiauv.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
            if (r2 != 0) goto L_0x0096
            return
        L_0x0096:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r9 = r0
            if (r1 == 0) goto L_0x00a3
            java.lang.String r0 = "originalPath"
            r9.put(r0, r1)
        L_0x00a3:
            r3 = r19
            r6 = r18
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Fxe2d7npngnS1C36sbeUbjXzebI r20 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Fxe2d7npngnS1C36sbeUbjXzebI
            r0 = r20
            r21 = r1
            r1 = r28
            r2 = r27
            r4 = r10
            r5 = r9
            r7 = r25
            r22 = r9
            r9 = r29
            r23 = r10
            r10 = r30
            r11 = r31
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r20)
            int r15 = r15 + 1
            goto L_0x0008
        L_0x00c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$50(java.util.ArrayList, long, im.bclpbkiauv.messenger.AccountInstance, im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$49(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, MessageObject messageObject, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, boolean notify, int scheduleDate) {
        MessageObject messageObject2 = messageObject;
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, documentFinal, messageObject2.messageOwner.attachPath, params, false, parentFinal);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(documentFinal, (VideoEditedInfo) null, messageObject2.messageOwner.attachPath, dialog_id, reply_to_msg, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, 0, parentFinal);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String caption, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                new Thread(new Runnable(paths, accountInstance, originalPaths, mime, dialog_id, reply_to_msg, caption, editingMessageObject, notify, scheduleDate, uris, inputContent) {
                    private final /* synthetic */ ArrayList f$0;
                    private final /* synthetic */ AccountInstance f$1;
                    private final /* synthetic */ ArrayList f$10;
                    private final /* synthetic */ InputContentInfoCompat f$11;
                    private final /* synthetic */ ArrayList f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ long f$4;
                    private final /* synthetic */ MessageObject f$5;
                    private final /* synthetic */ String f$6;
                    private final /* synthetic */ MessageObject f$7;
                    private final /* synthetic */ boolean f$8;
                    private final /* synthetic */ int f$9;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r7;
                        this.f$6 = r8;
                        this.f$7 = r9;
                        this.f$8 = r10;
                        this.f$9 = r11;
                        this.f$10 = r12;
                        this.f$11 = r13;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingDocuments$51(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
                    }
                }).start();
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$51(ArrayList paths, AccountInstance accountInstance, ArrayList originalPaths, String mime, long dialog_id, MessageObject reply_to_msg, String caption, MessageObject editingMessageObject, boolean notify, int scheduleDate, ArrayList uris, InputContentInfoCompat inputContent) {
        ArrayList arrayList = paths;
        ArrayList arrayList2 = uris;
        boolean error = false;
        if (arrayList != null) {
            for (int a = 0; a < paths.size(); a++) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList.get(a), (String) originalPaths.get(a), (Uri) null, mime, dialog_id, reply_to_msg, caption, (ArrayList<TLRPC.MessageEntity>) null, editingMessageObject, false, notify, scheduleDate)) {
                    error = true;
                }
            }
            ArrayList arrayList3 = originalPaths;
        } else {
            ArrayList arrayList4 = originalPaths;
        }
        if (arrayList2 != null) {
            for (int a2 = 0; a2 < uris.size(); a2++) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList2.get(a2), mime, dialog_id, reply_to_msg, caption, (ArrayList<TLRPC.MessageEntity>) null, editingMessageObject, false, notify, scheduleDate)) {
                    error = true;
                }
            }
        }
        if (inputContent != null) {
            inputContent.releasePermission();
        }
        if (error) {
            ToastUtils.show((int) R.string.UnsupportedAttachment);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<TLRPC.MessageEntity> entities, ArrayList<TLRPC.InputDocument> stickers, InputContentInfoCompat inputContent, int ttl, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        ArrayList<TLRPC.InputDocument> arrayList = stickers;
        SendingMediaInfo info = new SendingMediaInfo();
        info.path = imageFilePath;
        info.uri = imageUri;
        if (caption != null) {
            info.caption = caption.toString();
        }
        info.entities = entities;
        info.ttl = ttl;
        if (arrayList != null && !stickers.isEmpty()) {
            info.masks = new ArrayList<>(arrayList);
        }
        ArrayList<SendingMediaInfo> infos = new ArrayList<>();
        infos.add(info);
        prepareSendingMedia(accountInstance, infos, dialog_id, reply_to_msg, inputContent, false, false, editingMessageObject, notify, scheduleDate, false);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, TLRPC.BotInlineResult result, HashMap<String, String> params, long dialog_id, MessageObject reply_to_msg, boolean notify, int scheduleDate) {
        TLRPC.BotInlineResult botInlineResult = result;
        if (botInlineResult != null) {
            if (botInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto) {
                new Thread(new Runnable(dialog_id, result, accountInstance, params, reply_to_msg, notify, scheduleDate) {
                    private final /* synthetic */ long f$0;
                    private final /* synthetic */ TLRPC.BotInlineResult f$1;
                    private final /* synthetic */ AccountInstance f$2;
                    private final /* synthetic */ HashMap f$3;
                    private final /* synthetic */ MessageObject f$4;
                    private final /* synthetic */ boolean f$5;
                    private final /* synthetic */ int f$6;

                    {
                        this.f$0 = r1;
                        this.f$1 = r3;
                        this.f$2 = r4;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingBotContextResult$53(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                }).run();
            } else if (botInlineResult.send_message instanceof TLRPC.TL_botInlineMessageText) {
                TLRPC.WebPage webPage = null;
                if (((int) dialog_id) == 0) {
                    int a = 0;
                    while (true) {
                        if (a >= botInlineResult.send_message.entities.size()) {
                            break;
                        }
                        TLRPC.MessageEntity entity = botInlineResult.send_message.entities.get(a);
                        if (entity instanceof TLRPC.TL_messageEntityUrl) {
                            webPage = new TLRPC.TL_webPagePending();
                            webPage.url = botInlineResult.send_message.message.substring(entity.offset, entity.offset + entity.length);
                            break;
                        }
                        a++;
                    }
                }
                accountInstance.getSendMessagesHelper().sendMessage(botInlineResult.send_message.message, dialog_id, reply_to_msg, webPage, !botInlineResult.send_message.no_webpage, botInlineResult.send_message.entities, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
            } else {
                long j = dialog_id;
                if (botInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue) {
                    TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
                    venue.geo = botInlineResult.send_message.geo;
                    venue.address = botInlineResult.send_message.address;
                    venue.title = botInlineResult.send_message.title;
                    venue.provider = botInlineResult.send_message.provider;
                    venue.venue_id = botInlineResult.send_message.venue_id;
                    String str = botInlineResult.send_message.venue_type;
                    venue.venue_id = str;
                    venue.venue_type = str;
                    if (venue.venue_type == null) {
                        venue.venue_type = "";
                    }
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) venue, dialog_id, reply_to_msg, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
                } else if (botInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) {
                    if (botInlineResult.send_message.period != 0) {
                        TLRPC.TL_messageMediaGeoLive location = new TLRPC.TL_messageMediaGeoLive();
                        location.period = botInlineResult.send_message.period;
                        location.geo = botInlineResult.send_message.geo;
                        accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) location, dialog_id, reply_to_msg, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
                        return;
                    }
                    TLRPC.TL_messageMediaGeo location2 = new TLRPC.TL_messageMediaGeo();
                    location2.geo = botInlineResult.send_message.geo;
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) location2, dialog_id, reply_to_msg, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
                } else if (botInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaContact) {
                    TLRPC.TL_user tL_user = new TLRPC.TL_user();
                    tL_user.phone = botInlineResult.send_message.phone_number;
                    tL_user.first_name = botInlineResult.send_message.first_name;
                    tL_user.last_name = botInlineResult.send_message.last_name;
                    TLRPC.TL_restrictionReason reason = new TLRPC.TL_restrictionReason();
                    reason.text = botInlineResult.send_message.vcard;
                    reason.platform = "";
                    reason.reason = "";
                    tL_user.restriction_reason.add(reason);
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.User) tL_user, dialog_id, reply_to_msg, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
                }
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v58, types: [im.bclpbkiauv.tgnet.TLRPC$Photo] */
    /* JADX WARNING: type inference failed for: r1v61, types: [im.bclpbkiauv.tgnet.TLRPC$Document] */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$53(long r21, im.bclpbkiauv.tgnet.TLRPC.BotInlineResult r23, im.bclpbkiauv.messenger.AccountInstance r24, java.util.HashMap r25, im.bclpbkiauv.messenger.MessageObject r26, boolean r27, int r28) {
        /*
            r14 = r21
            r11 = r23
            r10 = r25
            int r0 = (int) r14
            r2 = 1
            if (r0 != 0) goto L_0x000c
            r0 = 1
            goto L_0x000d
        L_0x000c:
            r0 = 0
        L_0x000d:
            r16 = r0
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = r11.type
            java.lang.String r7 = "game"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x0059
            int r1 = (int) r14
            if (r1 != 0) goto L_0x0021
            return
        L_0x0021:
            im.bclpbkiauv.tgnet.TLRPC$TL_game r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_game
            r1.<init>()
            r5 = r1
            java.lang.String r1 = r11.title
            r5.title = r1
            java.lang.String r1 = r11.description
            r5.description = r1
            java.lang.String r1 = r11.id
            r5.short_name = r1
            im.bclpbkiauv.tgnet.TLRPC$Photo r1 = r11.photo
            r5.photo = r1
            im.bclpbkiauv.tgnet.TLRPC$Photo r1 = r5.photo
            if (r1 != 0) goto L_0x0042
            im.bclpbkiauv.tgnet.TLRPC$TL_photoEmpty r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoEmpty
            r1.<init>()
            r5.photo = r1
        L_0x0042:
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r11.document
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_document
            if (r1 == 0) goto L_0x0051
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r11.document
            r5.document = r1
            int r1 = r5.flags
            r1 = r1 | r2
            r5.flags = r1
        L_0x0051:
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x0059:
            boolean r6 = r11 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_botInlineMediaResult
            if (r6 == 0) goto L_0x00a3
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r11.document
            if (r1 == 0) goto L_0x007c
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r11.document
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_document
            if (r1 == 0) goto L_0x0074
            im.bclpbkiauv.tgnet.TLRPC$Document r1 = r11.document
            r3 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_document r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r3
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x0074:
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x007c:
            im.bclpbkiauv.tgnet.TLRPC$Photo r1 = r11.photo
            if (r1 == 0) goto L_0x009b
            im.bclpbkiauv.tgnet.TLRPC$Photo r1 = r11.photo
            boolean r1 = r1 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photo
            if (r1 == 0) goto L_0x0093
            im.bclpbkiauv.tgnet.TLRPC$Photo r1 = r11.photo
            r4 = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r4
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x0093:
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x009b:
            r17 = r3
            r18 = r4
            r19 = r5
            goto L_0x0459
        L_0x00a3:
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r6 = r11.content
            if (r6 == 0) goto L_0x0453
            java.io.File r6 = new java.io.File
            r7 = 4
            java.io.File r8 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r7)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r12 = r11.content
            java.lang.String r12 = r12.url
            java.lang.String r12 = im.bclpbkiauv.messenger.Utilities.MD5(r12)
            r9.append(r12)
            java.lang.String r12 = "."
            r9.append(r12)
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r13 = r11.content
            java.lang.String r13 = r13.url
            java.lang.String r7 = "file"
            java.lang.String r13 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r13, r7)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            r6.<init>(r8, r9)
            boolean r8 = r6.exists()
            if (r8 == 0) goto L_0x00e3
            java.lang.String r0 = r6.getAbsolutePath()
            r8 = r0
            goto L_0x00e8
        L_0x00e3:
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r8 = r11.content
            java.lang.String r0 = r8.url
            r8 = r0
        L_0x00e8:
            java.lang.String r0 = r11.type
            int r9 = r0.hashCode()
            java.lang.String r13 = "voice"
            java.lang.String r2 = "video"
            java.lang.String r1 = "audio"
            r17 = r3
            java.lang.String r3 = "gif"
            r18 = r4
            java.lang.String r4 = "sticker"
            r19 = r5
            switch(r9) {
                case -1890252483: goto L_0x0137;
                case 102340: goto L_0x012f;
                case 3143036: goto L_0x0127;
                case 93166550: goto L_0x011f;
                case 106642994: goto L_0x0115;
                case 112202875: goto L_0x010d;
                case 112386354: goto L_0x0105;
                default: goto L_0x0104;
            }
        L_0x0104:
            goto L_0x013f
        L_0x0105:
            boolean r0 = r0.equals(r13)
            if (r0 == 0) goto L_0x0104
            r0 = 1
            goto L_0x0140
        L_0x010d:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0104
            r0 = 3
            goto L_0x0140
        L_0x0115:
            java.lang.String r9 = "photo"
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x0104
            r0 = 6
            goto L_0x0140
        L_0x011f:
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0104
            r0 = 0
            goto L_0x0140
        L_0x0127:
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0104
            r0 = 2
            goto L_0x0140
        L_0x012f:
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0104
            r0 = 5
            goto L_0x0140
        L_0x0137:
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0104
            r0 = 4
            goto L_0x0140
        L_0x013f:
            r0 = -1
        L_0x0140:
            java.lang.String r9 = "x"
            r5 = 0
            switch(r0) {
                case 0: goto L_0x01a1;
                case 1: goto L_0x01a1;
                case 2: goto L_0x01a1;
                case 3: goto L_0x01a1;
                case 4: goto L_0x01a1;
                case 5: goto L_0x01a1;
                case 6: goto L_0x014c;
                default: goto L_0x0147;
            }
        L_0x0147:
            r20 = r6
            r0 = r8
            goto L_0x0459
        L_0x014c:
            boolean r0 = r6.exists()
            if (r0 == 0) goto L_0x015c
            im.bclpbkiauv.messenger.SendMessagesHelper r0 = r24.getSendMessagesHelper()
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r4 = r0.generatePhotoSizes(r8, r5, r1)
            goto L_0x015e
        L_0x015c:
            r4 = r18
        L_0x015e:
            if (r4 != 0) goto L_0x019c
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_photo
            r0.<init>()
            r4 = r0
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = r24.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r4.date = r0
            r1 = 0
            byte[] r0 = new byte[r1]
            r4.file_reference = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_photoSize r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r2 = im.bclpbkiauv.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r1 = r2[r1]
            r0.w = r1
            r1 = 1
            r3 = r2[r1]
            r0.h = r3
            r0.size = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r9
            java.util.ArrayList r1 = r4.sizes
            r1.add(r0)
            r18 = r4
            r0 = r8
            goto L_0x0459
        L_0x019c:
            r18 = r4
            r0 = r8
            goto L_0x0459
        L_0x01a1:
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_document
            r0.<init>()
            r17 = r0
            r20 = r6
            r5 = 0
            r14 = r17
            r14.id = r5
            r5 = 0
            r14.size = r5
            r14.dc_id = r5
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r6 = r11.content
            java.lang.String r6 = r6.mime_type
            r14.mime_type = r6
            byte[] r6 = new byte[r5]
            r14.file_reference = r6
            im.bclpbkiauv.tgnet.ConnectionsManager r5 = r24.getConnectionsManager()
            int r5 = r5.getCurrentTime()
            r14.date = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename
            r5.<init>()
            java.util.ArrayList r6 = r14.attributes
            r6.add(r5)
            java.lang.String r6 = r11.type
            int r15 = r6.hashCode()
            switch(r15) {
                case -1890252483: goto L_0x0205;
                case 102340: goto L_0x01fd;
                case 3143036: goto L_0x01f5;
                case 93166550: goto L_0x01ed;
                case 112202875: goto L_0x01e5;
                case 112386354: goto L_0x01dd;
                default: goto L_0x01dc;
            }
        L_0x01dc:
            goto L_0x020d
        L_0x01dd:
            boolean r1 = r6.equals(r13)
            if (r1 == 0) goto L_0x01dc
            r1 = 1
            goto L_0x020e
        L_0x01e5:
            boolean r1 = r6.equals(r2)
            if (r1 == 0) goto L_0x01dc
            r1 = 4
            goto L_0x020e
        L_0x01ed:
            boolean r1 = r6.equals(r1)
            if (r1 == 0) goto L_0x01dc
            r1 = 2
            goto L_0x020e
        L_0x01f5:
            boolean r1 = r6.equals(r7)
            if (r1 == 0) goto L_0x01dc
            r1 = 3
            goto L_0x020e
        L_0x01fd:
            boolean r1 = r6.equals(r3)
            if (r1 == 0) goto L_0x01dc
            r1 = 0
            goto L_0x020e
        L_0x0205:
            boolean r1 = r6.equals(r4)
            if (r1 == 0) goto L_0x01dc
            r1 = 5
            goto L_0x020e
        L_0x020d:
            r1 = -1
        L_0x020e:
            if (r1 == 0) goto L_0x03ab
            r3 = 1
            if (r1 == r3) goto L_0x0393
            r3 = 2
            if (r1 == r3) goto L_0x0365
            r3 = 3
            if (r1 == r3) goto L_0x0335
            r3 = 1119092736(0x42b40000, float:90.0)
            r4 = 4
            if (r1 == r4) goto L_0x02b3
            r4 = 5
            if (r1 == r4) goto L_0x0223
            goto L_0x0410
        L_0x0223:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeSticker
            r1.<init>()
            java.lang.String r4 = ""
            r1.alt = r4
            im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputStickerSetEmpty
            r4.<init>()
            r1.stickerset = r4
            java.util.ArrayList r4 = r14.attributes
            r4.add(r1)
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeImageSize r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeImageSize
            r4.<init>()
            int[] r6 = im.bclpbkiauv.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r13 = 0
            r15 = r6[r13]
            r4.w = r15
            r13 = 1
            r15 = r6[r13]
            r4.h = r15
            java.util.ArrayList r13 = r14.attributes
            r13.add(r4)
            java.lang.String r13 = "sticker.webp"
            r5.file_name = r13
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r13 = r11.thumb     // Catch:{ all -> 0x02ad }
            if (r13 == 0) goto L_0x02ab
            java.io.File r13 = new java.io.File     // Catch:{ all -> 0x02ad }
            r15 = 4
            java.io.File r15 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r15)     // Catch:{ all -> 0x02ad }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ad }
            r0.<init>()     // Catch:{ all -> 0x02ad }
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x02ad }
            java.lang.String r2 = r2.url     // Catch:{ all -> 0x02ad }
            java.lang.String r2 = im.bclpbkiauv.messenger.Utilities.MD5(r2)     // Catch:{ all -> 0x02ad }
            r0.append(r2)     // Catch:{ all -> 0x02ad }
            r0.append(r12)     // Catch:{ all -> 0x02ad }
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x02ad }
            java.lang.String r2 = r2.url     // Catch:{ all -> 0x02ad }
            java.lang.String r12 = "webp"
            java.lang.String r2 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r2, r12)     // Catch:{ all -> 0x02ad }
            r0.append(r2)     // Catch:{ all -> 0x02ad }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x02ad }
            r13.<init>(r15, r0)     // Catch:{ all -> 0x02ad }
            java.lang.String r0 = r13.getAbsolutePath()     // Catch:{ all -> 0x02ad }
            r2 = 0
            r12 = 1
            android.graphics.Bitmap r2 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r0, r2, r3, r3, r12)     // Catch:{ all -> 0x02ad }
            if (r2 == 0) goto L_0x02ab
            r12 = 55
            r13 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r12, r13)     // Catch:{ all -> 0x02ad }
            if (r3 == 0) goto L_0x02a8
            java.util.ArrayList r12 = r14.thumbs     // Catch:{ all -> 0x02ad }
            r12.add(r3)     // Catch:{ all -> 0x02ad }
            int r12 = r14.flags     // Catch:{ all -> 0x02ad }
            r13 = 1
            r12 = r12 | r13
            r14.flags = r12     // Catch:{ all -> 0x02ad }
        L_0x02a8:
            r2.recycle()     // Catch:{ all -> 0x02ad }
        L_0x02ab:
            goto L_0x0410
        L_0x02ad:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0410
        L_0x02b3:
            java.lang.String r1 = "video.mp4"
            r5.file_name = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            int[] r2 = im.bclpbkiauv.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r4 = 0
            r6 = r2[r4]
            r1.w = r6
            r4 = 1
            r6 = r2[r4]
            r1.h = r6
            int r6 = im.bclpbkiauv.messenger.MessageObject.getInlineResultDuration(r23)
            r1.duration = r6
            r1.supports_streaming = r4
            java.util.ArrayList r4 = r14.attributes
            r4.add(r1)
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r4 = r11.thumb     // Catch:{ all -> 0x032f }
            if (r4 == 0) goto L_0x032d
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x032f }
            r6 = 4
            java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r6)     // Catch:{ all -> 0x032f }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x032f }
            r13.<init>()     // Catch:{ all -> 0x032f }
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x032f }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x032f }
            java.lang.String r15 = im.bclpbkiauv.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x032f }
            r13.append(r15)     // Catch:{ all -> 0x032f }
            r13.append(r12)     // Catch:{ all -> 0x032f }
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r12 = r11.thumb     // Catch:{ all -> 0x032f }
            java.lang.String r12 = r12.url     // Catch:{ all -> 0x032f }
            java.lang.String r15 = "jpg"
            java.lang.String r12 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r12, r15)     // Catch:{ all -> 0x032f }
            r13.append(r12)     // Catch:{ all -> 0x032f }
            java.lang.String r12 = r13.toString()     // Catch:{ all -> 0x032f }
            r4.<init>(r6, r12)     // Catch:{ all -> 0x032f }
            java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ all -> 0x032f }
            r0 = 0
            r6 = 1
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r4, r0, r3, r3, r6)     // Catch:{ all -> 0x032f }
            if (r0 == 0) goto L_0x032d
            r6 = 55
            r12 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r6, r12)     // Catch:{ all -> 0x032f }
            if (r3 == 0) goto L_0x032a
            java.util.ArrayList r6 = r14.thumbs     // Catch:{ all -> 0x032f }
            r6.add(r3)     // Catch:{ all -> 0x032f }
            int r6 = r14.flags     // Catch:{ all -> 0x032f }
            r12 = 1
            r6 = r6 | r12
            r14.flags = r6     // Catch:{ all -> 0x032f }
        L_0x032a:
            r0.recycle()     // Catch:{ all -> 0x032f }
        L_0x032d:
            goto L_0x0410
        L_0x032f:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0410
        L_0x0335:
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x0361
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "file."
            r1.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r2 = r11.content
            java.lang.String r2 = r2.mime_type
            int r3 = r0 + 1
            java.lang.String r2 = r2.substring(r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r5.file_name = r1
            goto L_0x0410
        L_0x0361:
            r5.file_name = r7
            goto L_0x0410
        L_0x0365:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = im.bclpbkiauv.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            java.lang.String r1 = r11.title
            r0.title = r1
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            java.lang.String r1 = r11.description
            if (r1 == 0) goto L_0x0388
            java.lang.String r1 = r11.description
            r0.performer = r1
            int r1 = r0.flags
            r2 = 2
            r1 = r1 | r2
            r0.flags = r1
        L_0x0388:
            java.lang.String r1 = "audio.mp3"
            r5.file_name = r1
            java.util.ArrayList r1 = r14.attributes
            r1.add(r0)
            goto L_0x0410
        L_0x0393:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = im.bclpbkiauv.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r5.file_name = r1
            java.util.ArrayList r1 = r14.attributes
            r1.add(r0)
            goto L_0x0410
        L_0x03ab:
            r6 = 55
            java.lang.String r1 = "animation.gif"
            r5.file_name = r1
            java.lang.String r1 = "mp4"
            boolean r2 = r8.endsWith(r1)
            if (r2 == 0) goto L_0x03c9
            java.lang.String r2 = "video/mp4"
            r14.mime_type = r2
            java.util.ArrayList r2 = r14.attributes
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated
            r3.<init>()
            r2.add(r3)
            goto L_0x03cd
        L_0x03c9:
            java.lang.String r2 = "image/gif"
            r14.mime_type = r2
        L_0x03cd:
            r2 = 90
            if (r16 == 0) goto L_0x03d4
            r3 = 90
            goto L_0x03d6
        L_0x03d4:
            r3 = 320(0x140, float:4.48E-43)
        L_0x03d6:
            boolean r1 = r8.endsWith(r1)     // Catch:{ all -> 0x040b }
            if (r1 == 0) goto L_0x03e2
            r1 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r8, r1)     // Catch:{ all -> 0x040b }
            goto L_0x03ea
        L_0x03e2:
            r1 = 1
            float r4 = (float) r3     // Catch:{ all -> 0x040b }
            float r12 = (float) r3     // Catch:{ all -> 0x040b }
            r0 = 0
            android.graphics.Bitmap r0 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r8, r0, r4, r12, r1)     // Catch:{ all -> 0x040b }
        L_0x03ea:
            if (r0 == 0) goto L_0x040a
            float r1 = (float) r3     // Catch:{ all -> 0x040b }
            float r4 = (float) r3     // Catch:{ all -> 0x040b }
            if (r3 <= r2) goto L_0x03f3
            r2 = 80
            goto L_0x03f5
        L_0x03f3:
            r2 = 55
        L_0x03f5:
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r1 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r4, r2, r6)     // Catch:{ all -> 0x040b }
            if (r1 == 0) goto L_0x0407
            java.util.ArrayList r2 = r14.thumbs     // Catch:{ all -> 0x040b }
            r2.add(r1)     // Catch:{ all -> 0x040b }
            int r2 = r14.flags     // Catch:{ all -> 0x040b }
            r4 = 1
            r2 = r2 | r4
            r14.flags = r2     // Catch:{ all -> 0x040b }
        L_0x0407:
            r0.recycle()     // Catch:{ all -> 0x040b }
        L_0x040a:
            goto L_0x0410
        L_0x040b:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0410:
            java.lang.String r0 = r5.file_name
            if (r0 != 0) goto L_0x0416
            r5.file_name = r7
        L_0x0416:
            java.lang.String r0 = r14.mime_type
            if (r0 != 0) goto L_0x041e
            java.lang.String r0 = "application/octet-stream"
            r14.mime_type = r0
        L_0x041e:
            java.util.ArrayList r0 = r14.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x044f
            im.bclpbkiauv.tgnet.TLRPC$TL_photoSize r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = im.bclpbkiauv.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r2 = 0
            r3 = r1[r2]
            r0.w = r3
            r3 = 1
            r4 = r1[r3]
            r0.h = r4
            r0.size = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            r0.type = r9
            java.util.ArrayList r2 = r14.thumbs
            r2.add(r0)
            int r2 = r14.flags
            r3 = 1
            r2 = r2 | r3
            r14.flags = r2
        L_0x044f:
            r0 = r8
            r17 = r14
            goto L_0x0459
        L_0x0453:
            r17 = r3
            r18 = r4
            r19 = r5
        L_0x0459:
            r4 = r0
            r2 = r17
            r12 = r18
            r13 = r19
            if (r10 == 0) goto L_0x046f
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x046f
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r1 = r11.content
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r10.put(r3, r1)
        L_0x046f:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$y3o7YyCExwoSw2IVQ6gA6T0lafU r14 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$y3o7YyCExwoSw2IVQ6gA6T0lafU
            r1 = r14
            r3 = r24
            r5 = r21
            r7 = r26
            r8 = r23
            r9 = r25
            r10 = r27
            r11 = r28
            r1.<init>(r3, r4, r5, r7, r8, r9, r10, r11, r12, r13)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$53(long, im.bclpbkiauv.tgnet.TLRPC$BotInlineResult, im.bclpbkiauv.messenger.AccountInstance, java.util.HashMap, im.bclpbkiauv.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$52(TLRPC.TL_document finalDocument, AccountInstance accountInstance, String finalPathFinal, long dialog_id, MessageObject reply_to_msg, TLRPC.BotInlineResult result, HashMap params, boolean notify, int scheduleDate, TLRPC.TL_photo finalPhoto, TLRPC.TL_game finalGame) {
        TLRPC.BotInlineResult botInlineResult = result;
        if (finalDocument != null) {
            accountInstance.getSendMessagesHelper().sendMessage(finalDocument, (VideoEditedInfo) null, finalPathFinal, dialog_id, reply_to_msg, botInlineResult.send_message.message, botInlineResult.send_message.entities, botInlineResult.send_message.reply_markup, params, notify, scheduleDate, 0, result);
        } else if (finalPhoto != null) {
            accountInstance.getSendMessagesHelper().sendMessage(finalPhoto, botInlineResult.content != null ? botInlineResult.content.url : null, dialog_id, reply_to_msg, botInlineResult.send_message.message, botInlineResult.send_message.entities, botInlineResult.send_message.reply_markup, params, notify, scheduleDate, 0, result);
        } else if (finalGame != null) {
            accountInstance.getSendMessagesHelper().sendMessage(finalGame, dialog_id, botInlineResult.send_message.reply_markup, params, notify, scheduleDate);
        }
    }

    private static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static void prepareSendingText(AccountInstance accountInstance, String text, long dialog_id, boolean notify, int scheduleDate) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new Runnable(text, accountInstance, dialog_id, notify, scheduleDate) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ AccountInstance f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
            }

            public final void run() {
                Utilities.stageQueue.postRunnable(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ AccountInstance f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                            private final /* synthetic */ String f$0;
                            private final /* synthetic */ AccountInstance f$1;
                            private final /* synthetic */ long f$2;
                            private final /* synthetic */ boolean f$3;
                            private final /* synthetic */ int f$4;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r5;
                                this.f$4 = r6;
                            }

                            public final void run() {
                                SendMessagesHelper.lambda$null$54(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$54(String text, AccountInstance accountInstance, long dialog_id, boolean notify, int scheduleDate) {
        String textFinal = getTrimmedString(text);
        if (textFinal.length() != 0) {
            int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
            for (int a = 0; a < count; a++) {
                accountInstance.getSendMessagesHelper().sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, notify, scheduleDate);
            }
        }
    }

    public static void ensureMediaThumbExists(boolean isEncrypted, TLObject object, String path, Uri uri, long startTime) {
        Bitmap thumb;
        boolean smallExists;
        Bitmap bitmap;
        TLRPC.PhotoSize size;
        TLObject tLObject = object;
        String str = path;
        Uri uri2 = uri;
        if (tLObject instanceof TLRPC.TL_photo) {
            TLRPC.TL_photo photo = (TLRPC.TL_photo) tLObject;
            TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
            if (smallSize instanceof TLRPC.TL_photoStrippedSize) {
                smallExists = true;
            } else {
                smallExists = FileLoader.getPathToAttach(smallSize, true).exists();
            }
            TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
            boolean bigExists = FileLoader.getPathToAttach(bigSize, false).exists();
            if (!smallExists || !bigExists) {
                Bitmap bitmap2 = ImageLoader.loadBitmap(str, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                if (bitmap2 == null) {
                    bitmap = ImageLoader.loadBitmap(str, uri2, 800.0f, 800.0f, true);
                } else {
                    bitmap = bitmap2;
                }
                if (!bigExists && (size = ImageLoader.scaleAndSaveImage(bigSize, bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false)) != bigSize) {
                    photo.sizes.add(0, size);
                }
                if (!smallExists) {
                    TLRPC.PhotoSize photoSize = bigSize;
                    TLRPC.PhotoSize size2 = ImageLoader.scaleAndSaveImage(smallSize, bitmap, 90.0f, 90.0f, 55, true);
                    if (size2 != smallSize) {
                        photo.sizes.add(0, size2);
                    }
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } else if (tLObject instanceof TLRPC.TL_document) {
            TLRPC.TL_document document = (TLRPC.TL_document) tLObject;
            if ((MessageObject.isVideoDocument(document) || MessageObject.isNewGifDocument((TLRPC.Document) document)) && MessageObject.isDocumentHasThumb(document)) {
                int side = 320;
                TLRPC.PhotoSize photoSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (!(photoSize2 instanceof TLRPC.TL_photoStrippedSize) && !FileLoader.getPathToAttach(photoSize2, true).exists()) {
                    Bitmap thumb2 = createVideoThumbnail(str, startTime);
                    if (thumb2 == null) {
                        thumb = ThumbnailUtils.createVideoThumbnail(str, 1);
                    } else {
                        thumb = thumb2;
                    }
                    if (isEncrypted) {
                        side = 90;
                    }
                    document.thumbs.set(0, ImageLoader.scaleAndSaveImage(photoSize2, thumb, (float) side, (float) side, side > 90 ? 80 : 55, false));
                }
            }
        }
    }

    private static String getKeyForPhotoSize(TLRPC.PhotoSize photoSize, Bitmap[] bitmap, boolean blur) {
        int photoWidth;
        TLRPC.PhotoSize photoSize2 = photoSize;
        if (photoSize2 == null) {
            return null;
        }
        if (AndroidUtilities.isTablet()) {
            photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
        } else if (photoSize2.w >= photoSize2.h) {
            photoWidth = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(64.0f);
        } else {
            photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
        }
        int photoHeight = AndroidUtilities.dp(100.0f) + photoWidth;
        if (photoWidth > AndroidUtilities.getPhotoSize()) {
            photoWidth = AndroidUtilities.getPhotoSize();
        }
        if (photoHeight > AndroidUtilities.getPhotoSize()) {
            photoHeight = AndroidUtilities.getPhotoSize();
        }
        float scale = ((float) photoSize2.w) / ((float) photoWidth);
        int w = (int) (((float) photoSize2.w) / scale);
        int h = (int) (((float) photoSize2.h) / scale);
        if (w == 0) {
            w = AndroidUtilities.dp(150.0f);
        }
        if (h == 0) {
            h = AndroidUtilities.dp(150.0f);
        }
        if (h > photoHeight) {
            float scale2 = (float) h;
            h = photoHeight;
            w = (int) (((float) w) / (scale2 / ((float) h)));
        } else if (h < AndroidUtilities.dp(120.0f)) {
            h = AndroidUtilities.dp(120.0f);
            float hScale = ((float) photoSize2.h) / ((float) h);
            if (((float) photoSize2.w) / hScale < ((float) photoWidth)) {
                w = (int) (((float) photoSize2.w) / hScale);
            }
        }
        if (bitmap != null) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                File file = FileLoader.getPathToAttach(photoSize);
                FileInputStream is = new FileInputStream(file);
                BitmapFactory.decodeStream(is, (Rect) null, opts);
                is.close();
                float scaleFactor = Math.max(((float) opts.outWidth) / ((float) w), ((float) opts.outHeight) / ((float) h));
                if (scaleFactor < 1.0f) {
                    scaleFactor = 1.0f;
                }
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = (int) scaleFactor;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                if (Build.VERSION.SDK_INT >= 21) {
                    FileInputStream is2 = new FileInputStream(file);
                    bitmap[0] = BitmapFactory.decodeStream(is2, (Rect) null, opts);
                    is2.close();
                }
            } catch (Throwable th) {
            }
        }
        return String.format(Locale.US, blur ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", new Object[]{Long.valueOf(photoSize2.location.volume_id), Integer.valueOf(photoSize2.location.local_id), Integer.valueOf((int) (((float) w) / AndroidUtilities.density)), Integer.valueOf((int) (((float) h) / AndroidUtilities.density))});
    }

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> media, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, boolean forceDocument, boolean groupPhotos, MessageObject editingMessageObject, boolean notify, int scheduleDate, boolean blnOriginalImg) {
        if (!media.isEmpty()) {
            mediaSendQueue.postRunnable(new Runnable(media, dialog_id, accountInstance, forceDocument, groupPhotos, blnOriginalImg, editingMessageObject, reply_to_msg, notify, scheduleDate, inputContent) {
                private final /* synthetic */ ArrayList f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ InputContentInfoCompat f$10;
                private final /* synthetic */ AccountInstance f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ MessageObject f$6;
                private final /* synthetic */ MessageObject f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ int f$9;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                    this.f$9 = r11;
                    this.f$10 = r12;
                }

                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingMedia$63(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
                }
            });
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v50, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v90, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r6v52 */
    /* JADX WARNING: type inference failed for: r2v73 */
    /* JADX WARNING: type inference failed for: r5v46, types: [im.bclpbkiauv.tgnet.TLRPC$Photo] */
    /* JADX WARNING: type inference failed for: r2v122, types: [im.bclpbkiauv.tgnet.TLRPC$Document] */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0a98, code lost:
        if (r2.endsWith(r14) != false) goto L_0x0a9d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0275, code lost:
        if (r8 >= 73) goto L_0x027a;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x03c5 A[SYNTHETIC, Splitter:B:138:0x03c5] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x03d0 A[SYNTHETIC, Splitter:B:141:0x03d0] */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x03de A[Catch:{ Exception -> 0x0428 }] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x041c  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0457  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0485  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x04ab  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x04bb  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0809  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x08f8  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0923  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0974  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0b16  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0b60  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00cf A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0d75  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0da1  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0e65  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0e74  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0e7a  */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0e85  */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0ee5  */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0ef1  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0921 A[EDGE_INSN: B:528:0x0921->B:324:0x0921 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:533:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0268  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingMedia$63(java.util.ArrayList r62, long r63, im.bclpbkiauv.messenger.AccountInstance r65, boolean r66, boolean r67, boolean r68, im.bclpbkiauv.messenger.MessageObject r69, im.bclpbkiauv.messenger.MessageObject r70, boolean r71, int r72, androidx.core.view.inputmethod.InputContentInfoCompat r73) {
        /*
            r1 = r62
            r14 = r63
            r13 = r68
            long r18 = java.lang.System.currentTimeMillis()
            int r12 = r62.size()
            int r0 = (int) r14
            if (r0 != 0) goto L_0x0013
            r0 = 1
            goto L_0x0014
        L_0x0013:
            r0 = 0
        L_0x0014:
            r10 = r0
            r0 = 0
            if (r10 == 0) goto L_0x0033
            r2 = 32
            long r2 = r14 >> r2
            int r3 = (int) r2
            im.bclpbkiauv.messenger.MessagesController r2 = r65.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r4)
            if (r2 == 0) goto L_0x0033
            int r4 = r2.layer
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r4)
            r8 = r0
            goto L_0x0034
        L_0x0033:
            r8 = r0
        L_0x0034:
            java.lang.String r6 = "_ori"
            java.lang.String r7 = ".webp"
            r5 = 73
            java.lang.String r4 = ".gif"
            r20 = 3
            java.lang.String r2 = "_"
            if (r10 == 0) goto L_0x0050
            if (r8 < r5) goto L_0x0045
            goto L_0x0050
        L_0x0045:
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
            goto L_0x0245
        L_0x0050:
            if (r66 != 0) goto L_0x023c
            if (r67 == 0) goto L_0x023c
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r16 = 0
            r9 = r16
        L_0x005d:
            if (r9 >= r12) goto L_0x022e
            java.lang.Object r16 = r1.get(r9)
            r11 = r16
            im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo r11 = (im.bclpbkiauv.messenger.SendMessagesHelper.SendingMediaInfo) r11
            im.bclpbkiauv.messenger.MediaController$SearchImage r3 = r11.searchImage
            if (r3 != 0) goto L_0x020f
            boolean r3 = r11.isVideo
            if (r3 != 0) goto L_0x020f
            java.lang.String r3 = r11.path
            java.lang.String r5 = r11.path
            if (r5 != 0) goto L_0x0088
            r23 = r3
            android.net.Uri r3 = r11.uri
            if (r3 == 0) goto L_0x008a
            android.net.Uri r3 = r11.uri
            java.lang.String r5 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r3)
            android.net.Uri r3 = r11.uri
            java.lang.String r3 = r3.toString()
            goto L_0x008c
        L_0x0088:
            r23 = r3
        L_0x008a:
            r3 = r23
        L_0x008c:
            if (r5 == 0) goto L_0x00b4
            boolean r23 = r5.endsWith(r4)
            if (r23 != 0) goto L_0x00a7
            boolean r23 = r5.endsWith(r7)
            if (r23 == 0) goto L_0x00b4
            r23 = r0
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
            goto L_0x021a
        L_0x00a7:
            r23 = r0
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
            goto L_0x021a
        L_0x00b4:
            r23 = r4
            java.lang.String r4 = r11.path
            r24 = r7
            android.net.Uri r7 = r11.uri
            boolean r4 = im.bclpbkiauv.messenger.ImageLoader.shouldSendImageAsDocument(r4, r7)
            if (r4 == 0) goto L_0x00cf
            r30 = r2
            r32 = r6
            r31 = r23
            r33 = r24
            r14 = 0
            r23 = r0
            goto L_0x021a
        L_0x00cf:
            if (r5 != 0) goto L_0x00ff
            android.net.Uri r4 = r11.uri
            if (r4 == 0) goto L_0x00ff
            android.net.Uri r4 = r11.uri
            boolean r4 = im.bclpbkiauv.messenger.MediaController.isGif(r4)
            if (r4 != 0) goto L_0x00f2
            android.net.Uri r4 = r11.uri
            boolean r4 = im.bclpbkiauv.messenger.MediaController.isWebp(r4)
            if (r4 == 0) goto L_0x00ff
            r30 = r2
            r32 = r6
            r31 = r23
            r33 = r24
            r14 = 0
            r23 = r0
            goto L_0x021a
        L_0x00f2:
            r30 = r2
            r32 = r6
            r31 = r23
            r33 = r24
            r14 = 0
            r23 = r0
            goto L_0x021a
        L_0x00ff:
            if (r5 == 0) goto L_0x0148
            java.io.File r4 = new java.io.File
            r4.<init>(r5)
            if (r13 != 0) goto L_0x0126
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            long r13 = r4.length()
            r7.append(r13)
            r7.append(r2)
            long r13 = r4.lastModified()
            r7.append(r13)
            java.lang.String r3 = r7.toString()
            goto L_0x0146
        L_0x0126:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r3)
            long r13 = r4.length()
            r7.append(r13)
            r7.append(r2)
            long r13 = r4.lastModified()
            r7.append(r13)
            r7.append(r6)
            java.lang.String r3 = r7.toString()
        L_0x0146:
            r13 = r3
            goto L_0x014a
        L_0x0148:
            r3 = 0
            r13 = r3
        L_0x014a:
            r3 = 0
            r4 = 0
            if (r10 != 0) goto L_0x01c7
            int r7 = r11.ttl
            if (r7 != 0) goto L_0x01c7
            im.bclpbkiauv.messenger.MessagesStorage r7 = r65.getMessagesStorage()
            if (r10 != 0) goto L_0x015a
            r14 = 0
            goto L_0x015b
        L_0x015a:
            r14 = 3
        L_0x015b:
            java.lang.Object[] r7 = r7.getSentFile(r13, r14)
            if (r7 == 0) goto L_0x016d
            r14 = 0
            r15 = r7[r14]
            r3 = r15
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r3
            r14 = 1
            r15 = r7[r14]
            r4 = r15
            java.lang.String r4 = (java.lang.String) r4
        L_0x016d:
            if (r3 != 0) goto L_0x01a0
            android.net.Uri r14 = r11.uri
            if (r14 == 0) goto L_0x01a0
            im.bclpbkiauv.messenger.MessagesStorage r14 = r65.getMessagesStorage()
            android.net.Uri r15 = r11.uri
            java.lang.String r15 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r15)
            r25 = r2
            if (r10 != 0) goto L_0x0183
            r2 = 0
            goto L_0x0184
        L_0x0183:
            r2 = 3
        L_0x0184:
            java.lang.Object[] r7 = r14.getSentFile(r15, r2)
            if (r7 == 0) goto L_0x019b
            r2 = 0
            r14 = r7[r2]
            r2 = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r2
            r3 = 1
            r14 = r7[r3]
            r3 = r14
            java.lang.String r3 = (java.lang.String) r3
            r14 = r2
            r15 = r3
            r26 = r7
            goto L_0x01a6
        L_0x019b:
            r14 = r3
            r15 = r4
            r26 = r7
            goto L_0x01a6
        L_0x01a0:
            r25 = r2
            r14 = r3
            r15 = r4
            r26 = r7
        L_0x01a6:
            java.lang.String r4 = r11.path
            android.net.Uri r7 = r11.uri
            r27 = 0
            r3 = r25
            r2 = r10
            r30 = r3
            r3 = r14
            r31 = r23
            r16 = r5
            r22 = r15
            r15 = 73
            r5 = r7
            r32 = r6
            r33 = r24
            r6 = r27
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r7 = r22
            goto L_0x01d5
        L_0x01c7:
            r30 = r2
            r16 = r5
            r32 = r6
            r31 = r23
            r33 = r24
            r15 = 73
            r14 = r3
            r7 = r4
        L_0x01d5:
            im.bclpbkiauv.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = new im.bclpbkiauv.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r6 = 0
            r2.<init>()
            r5 = r2
            r0.put(r11, r5)
            if (r14 == 0) goto L_0x01e9
            r5.parentObject = r7
            r5.photo = r14
            r23 = r0
            r14 = r6
            goto L_0x021a
        L_0x01e9:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r3 = 1
            r2.<init>(r3)
            r5.sync = r2
            java.util.concurrent.ThreadPoolExecutor r4 = mediaSendThreadPool
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$9Kp0NSV4t3W_yJthNmcUrIfQayw r3 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$9Kp0NSV4t3W_yJthNmcUrIfQayw
            r2 = r3
            r15 = r3
            r3 = r5
            r23 = r0
            r0 = r4
            r4 = r65
            r24 = r5
            r5 = r11
            r25 = r14
            r14 = r6
            r6 = r68
            r26 = r7
            r7 = r10
            r2.<init>(r4, r5, r6, r7)
            r0.execute(r15)
            goto L_0x021a
        L_0x020f:
            r23 = r0
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
        L_0x021a:
            int r9 = r9 + 1
            r14 = r63
            r13 = r68
            r0 = r23
            r2 = r30
            r4 = r31
            r6 = r32
            r7 = r33
            r5 = 73
            goto L_0x005d
        L_0x022e:
            r23 = r0
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
            r15 = r23
            goto L_0x0247
        L_0x023c:
            r30 = r2
            r31 = r4
            r32 = r6
            r33 = r7
            r14 = 0
        L_0x0245:
            r0 = 0
            r15 = r0
        L_0x0247:
            r2 = 0
            r4 = 0
            r0 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r11 = 0
            r13 = 0
            r16 = 0
            r23 = r2
            r25 = r4
            r27 = r11
            r11 = r6
            r6 = r16
            r60 = r13
            r13 = r0
            r0 = r60
            r61 = r9
            r9 = r7
            r7 = r61
        L_0x0266:
            if (r6 >= r12) goto L_0x0e4e
            java.lang.Object r4 = r1.get(r6)
            r5 = r4
            im.bclpbkiauv.messenger.SendMessagesHelper$SendingMediaInfo r5 = (im.bclpbkiauv.messenger.SendMessagesHelper.SendingMediaInfo) r5
            if (r67 == 0) goto L_0x0291
            if (r10 == 0) goto L_0x0278
            r4 = 73
            if (r8 < r4) goto L_0x0291
            goto L_0x027a
        L_0x0278:
            r4 = 73
        L_0x027a:
            r4 = 1
            if (r12 <= r4) goto L_0x0291
            int r4 = r0 % 10
            if (r4 != 0) goto L_0x0291
            java.security.SecureRandom r4 = im.bclpbkiauv.messenger.Utilities.random
            long r28 = r4.nextLong()
            r23 = r28
            r25 = r28
            r0 = 0
            r16 = r0
            r34 = r23
            goto L_0x0295
        L_0x0291:
            r16 = r0
            r34 = r23
        L_0x0295:
            im.bclpbkiauv.messenger.MediaController$SearchImage r0 = r5.searchImage
            java.lang.String r4 = "video/mp4"
            java.lang.String r14 = "1"
            java.lang.String r2 = "final"
            java.lang.String r3 = "groupId"
            java.lang.String r1 = "mp4"
            r28 = r12
            java.lang.String r12 = "originalPath"
            r38 = r13
            java.lang.String r13 = ""
            r39 = 4
            if (r0 == 0) goto L_0x06be
            im.bclpbkiauv.messenger.MediaController$SearchImage r0 = r5.searchImage
            int r0 = r0.type
            r40 = r6
            java.lang.String r6 = "jpg"
            r41 = r7
            java.lang.String r7 = "."
            r42 = r8
            r8 = 1
            if (r0 != r8) goto L_0x0510
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r14 = r0
            r0 = 0
            r23 = 0
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r5.searchImage
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_document
            if (r2 == 0) goto L_0x02dd
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r5.searchImage
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            r0 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_document r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r0
            r2 = 1
            java.io.File r3 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r0, r2)
            goto L_0x0308
        L_0x02dd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r3 = r5.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r3 = im.bclpbkiauv.messenger.Utilities.MD5(r3)
            r2.append(r3)
            r2.append(r7)
            im.bclpbkiauv.messenger.MediaController$SearchImage r3 = r5.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r3 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r3, r6)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            java.io.File r8 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r39)
            r3.<init>(r8, r2)
        L_0x0308:
            if (r0 != 0) goto L_0x048c
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_document r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_document
            r8.<init>()
            r43 = r11
            r44 = r12
            r11 = 0
            r8.id = r11
            r11 = 0
            byte[] r0 = new byte[r11]
            r8.file_reference = r0
            im.bclpbkiauv.tgnet.ConnectionsManager r0 = r65.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r8.date = r0
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r11 = r0
            java.lang.String r0 = "animation.gif"
            r11.file_name = r0
            java.util.ArrayList r0 = r8.attributes
            r0.add(r11)
            im.bclpbkiauv.messenger.MediaController$SearchImage r0 = r5.searchImage
            int r0 = r0.size
            r8.size = r0
            r12 = 0
            r8.dc_id = r12
            java.lang.String r0 = r3.toString()
            boolean r0 = r0.endsWith(r1)
            if (r0 == 0) goto L_0x0356
            r8.mime_type = r4
            java.util.ArrayList r0 = r8.attributes
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated
            r4.<init>()
            r0.add(r4)
            goto L_0x035a
        L_0x0356:
            java.lang.String r0 = "image/gif"
            r8.mime_type = r0
        L_0x035a:
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x0363
            r2 = r3
            r12 = r3
            goto L_0x0365
        L_0x0363:
            r0 = 0
            r12 = r0
        L_0x0365:
            if (r2 != 0) goto L_0x039e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r3 = r5.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r3 = im.bclpbkiauv.messenger.Utilities.MD5(r3)
            r0.append(r3)
            r0.append(r7)
            im.bclpbkiauv.messenger.MediaController$SearchImage r3 = r5.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r3 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r3, r6)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r39)
            r3.<init>(r4, r0)
            r2 = r3
            boolean r3 = r2.exists()
            if (r3 != 0) goto L_0x039c
            r2 = 0
            r13 = r2
            goto L_0x039f
        L_0x039c:
            r13 = r2
            goto L_0x039f
        L_0x039e:
            r13 = r2
        L_0x039f:
            if (r13 == 0) goto L_0x0444
            if (r10 != 0) goto L_0x03b9
            int r0 = r5.ttl     // Catch:{ Exception -> 0x03ab }
            if (r0 == 0) goto L_0x03a8
            goto L_0x03b9
        L_0x03a8:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x03bb
        L_0x03ab:
            r0 = move-exception
            r45 = r15
            r46 = r40
            r47 = r41
            r22 = 73
            r24 = 0
            r15 = r5
            goto L_0x0440
        L_0x03b9:
            r0 = 90
        L_0x03bb:
            java.lang.String r2 = r13.getAbsolutePath()     // Catch:{ Exception -> 0x0434 }
            boolean r1 = r2.endsWith(r1)     // Catch:{ Exception -> 0x0434 }
            if (r1 == 0) goto L_0x03d0
            java.lang.String r1 = r13.getAbsolutePath()     // Catch:{ Exception -> 0x03ab }
            r2 = 1
            android.graphics.Bitmap r1 = android.media.ThumbnailUtils.createVideoThumbnail(r1, r2)     // Catch:{ Exception -> 0x03ab }
            r7 = 0
            goto L_0x03dc
        L_0x03d0:
            java.lang.String r1 = r13.getAbsolutePath()     // Catch:{ Exception -> 0x0434 }
            float r2 = (float) r0
            float r3 = (float) r0
            r4 = 1
            r7 = 0
            android.graphics.Bitmap r1 = im.bclpbkiauv.messenger.ImageLoader.loadBitmap(r1, r7, r2, r3, r4)     // Catch:{ Exception -> 0x0428 }
        L_0x03dc:
            if (r1 == 0) goto L_0x041c
            float r3 = (float) r0     // Catch:{ Exception -> 0x0428 }
            float r4 = (float) r0     // Catch:{ Exception -> 0x0428 }
            r2 = 90
            if (r0 <= r2) goto L_0x03e9
            r2 = 80
            r6 = 80
            goto L_0x03ed
        L_0x03e9:
            r2 = 55
            r6 = 55
        L_0x03ed:
            java.lang.String r2 = r13.getAbsolutePath()     // Catch:{ Exception -> 0x0428 }
            java.lang.String r7 = ".png"
            boolean r7 = r2.endsWith(r7)     // Catch:{ Exception -> 0x0434 }
            r2 = r1
            r22 = 73
            r45 = r15
            r15 = r5
            r5 = r6
            r46 = r40
            r6 = r10
            r47 = r41
            r24 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage((android.graphics.Bitmap) r2, (float) r3, (float) r4, (int) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x041a }
            if (r2 == 0) goto L_0x0416
            java.util.ArrayList r3 = r8.thumbs     // Catch:{ Exception -> 0x041a }
            r3.add(r2)     // Catch:{ Exception -> 0x041a }
            int r3 = r8.flags     // Catch:{ Exception -> 0x041a }
            r4 = 1
            r3 = r3 | r4
            r8.flags = r3     // Catch:{ Exception -> 0x041a }
        L_0x0416:
            r1.recycle()     // Catch:{ Exception -> 0x041a }
            goto L_0x0427
        L_0x041a:
            r0 = move-exception
            goto L_0x0440
        L_0x041c:
            r24 = r7
            r45 = r15
            r46 = r40
            r47 = r41
            r22 = 73
            r15 = r5
        L_0x0427:
            goto L_0x044f
        L_0x0428:
            r0 = move-exception
            r24 = r7
            r45 = r15
            r46 = r40
            r47 = r41
            r22 = 73
            goto L_0x043f
        L_0x0434:
            r0 = move-exception
            r45 = r15
            r46 = r40
            r47 = r41
            r22 = 73
            r24 = 0
        L_0x043f:
            r15 = r5
        L_0x0440:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x044f
        L_0x0444:
            r45 = r15
            r46 = r40
            r47 = r41
            r22 = 73
            r24 = 0
            r15 = r5
        L_0x044f:
            java.util.ArrayList r0 = r8.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0485
            im.bclpbkiauv.tgnet.TLRPC$TL_photoSize r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r1 = r15.searchImage
            int r1 = r1.width
            r0.w = r1
            im.bclpbkiauv.messenger.MediaController$SearchImage r1 = r15.searchImage
            int r1 = r1.height
            r0.h = r1
            r1 = 0
            r0.size = r1
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            java.lang.String r2 = "x"
            r0.type = r2
            java.util.ArrayList r2 = r8.thumbs
            r2.add(r0)
            int r2 = r8.flags
            r17 = 1
            r2 = r2 | 1
            r8.flags = r2
            goto L_0x0488
        L_0x0485:
            r1 = 0
            r17 = 1
        L_0x0488:
            r0 = r8
            r21 = r12
            goto L_0x04a0
        L_0x048c:
            r43 = r11
            r44 = r12
            r45 = r15
            r46 = r40
            r47 = r41
            r1 = 0
            r17 = 1
            r22 = 73
            r24 = 0
            r15 = r5
            r21 = r3
        L_0x04a0:
            r5 = r0
            r13 = r42
            r8 = r23
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r12 = r2.imageUrl
            if (r21 != 0) goto L_0x04b0
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            goto L_0x04b4
        L_0x04b0:
            java.lang.String r2 = r21.toString()
        L_0x04b4:
            r6 = r2
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04c4
            im.bclpbkiauv.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            r11 = r44
            r14.put(r11, r2)
        L_0x04c4:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$HptwJq6Dnz0cCTHXPAMY2lC1iXA r29 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$HptwJq6Dnz0cCTHXPAMY2lC1iXA
            r2 = r29
            r3 = r69
            r4 = r65
            r7 = r14
            r11 = r9
            r1 = r10
            r9 = r63
            r50 = r11
            r49 = r43
            r11 = r70
            r17 = r12
            r12 = r15
            r52 = r13
            r51 = r38
            r13 = r71
            r24 = r14
            r14 = r72
            r2.<init>(r4, r5, r6, r7, r8, r9, r11, r12, r13, r14)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r29)
            r42 = r1
            r0 = r16
            r38 = r32
            r22 = r33
            r29 = r45
            r33 = r46
            r7 = r47
            r11 = r49
            r9 = r50
            r13 = r51
            r53 = r52
            r39 = 0
            r40 = 1
            r44 = 0
            r45 = 73
            r51 = r34
            r35 = r30
            r34 = r31
            goto L_0x0e34
        L_0x0510:
            r50 = r9
            r1 = r10
            r49 = r11
            r11 = r12
            r45 = r15
            r51 = r38
            r46 = r40
            r47 = r41
            r52 = r42
            r22 = 73
            r15 = r5
            r0 = 1
            r4 = 0
            r17 = 0
            im.bclpbkiauv.messenger.MediaController$SearchImage r5 = r15.searchImage
            im.bclpbkiauv.tgnet.TLRPC$Photo r5 = r5.photo
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photo
            if (r5 == 0) goto L_0x0537
            im.bclpbkiauv.messenger.MediaController$SearchImage r5 = r15.searchImage
            im.bclpbkiauv.tgnet.TLRPC$Photo r5 = r5.photo
            r4 = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r4 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r4
            goto L_0x053b
        L_0x0537:
            if (r1 != 0) goto L_0x053b
            int r5 = r15.ttl
        L_0x053b:
            if (r4 != 0) goto L_0x0617
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r8 = r15.searchImage
            java.lang.String r8 = r8.imageUrl
            java.lang.String r8 = im.bclpbkiauv.messenger.Utilities.MD5(r8)
            r5.append(r8)
            r5.append(r7)
            im.bclpbkiauv.messenger.MediaController$SearchImage r8 = r15.searchImage
            java.lang.String r8 = r8.imageUrl
            java.lang.String r8 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r8, r6)
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            java.io.File r8 = new java.io.File
            java.io.File r9 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r39)
            r8.<init>(r9, r5)
            boolean r9 = r8.exists()
            if (r9 == 0) goto L_0x058d
            long r9 = r8.length()
            r36 = 0
            int r12 = (r9 > r36 ? 1 : (r9 == r36 ? 0 : -1))
            if (r12 == 0) goto L_0x058d
            im.bclpbkiauv.messenger.SendMessagesHelper r9 = r65.getSendMessagesHelper()
            java.lang.String r10 = r8.toString()
            r12 = r68
            r21 = r1
            r1 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r4 = r9.generatePhotoSizes(r10, r1, r12)
            if (r4 == 0) goto L_0x0592
            r0 = 0
            goto L_0x0592
        L_0x058d:
            r12 = r68
            r21 = r1
            r1 = 0
        L_0x0592:
            if (r4 != 0) goto L_0x0613
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r10 = r15.searchImage
            java.lang.String r10 = r10.thumbUrl
            java.lang.String r10 = im.bclpbkiauv.messenger.Utilities.MD5(r10)
            r9.append(r10)
            r9.append(r7)
            im.bclpbkiauv.messenger.MediaController$SearchImage r7 = r15.searchImage
            java.lang.String r7 = r7.thumbUrl
            java.lang.String r6 = im.bclpbkiauv.messenger.ImageLoader.getHttpUrlExtension(r7, r6)
            r9.append(r6)
            java.lang.String r5 = r9.toString()
            java.io.File r6 = new java.io.File
            java.io.File r7 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r39)
            r6.<init>(r7, r5)
            boolean r7 = r6.exists()
            if (r7 == 0) goto L_0x05d1
            im.bclpbkiauv.messenger.SendMessagesHelper r7 = r65.getSendMessagesHelper()
            java.lang.String r8 = r6.toString()
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r4 = r7.generatePhotoSizes(r8, r1, r12)
        L_0x05d1:
            if (r4 != 0) goto L_0x060f
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_photo
            r7.<init>()
            r4 = r7
            im.bclpbkiauv.tgnet.ConnectionsManager r7 = r65.getConnectionsManager()
            int r7 = r7.getCurrentTime()
            r4.date = r7
            r10 = 0
            byte[] r7 = new byte[r10]
            r4.file_reference = r7
            im.bclpbkiauv.tgnet.TLRPC$TL_photoSize r7 = new im.bclpbkiauv.tgnet.TLRPC$TL_photoSize
            r7.<init>()
            im.bclpbkiauv.messenger.MediaController$SearchImage r8 = r15.searchImage
            int r8 = r8.width
            r7.w = r8
            im.bclpbkiauv.messenger.MediaController$SearchImage r8 = r15.searchImage
            int r8 = r8.height
            r7.h = r8
            r7.size = r10
            im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable r8 = new im.bclpbkiauv.tgnet.TLRPC$TL_fileLocationUnavailable
            r8.<init>()
            r7.location = r8
            java.lang.String r8 = "x"
            r7.type = r8
            java.util.ArrayList r8 = r4.sizes
            r8.add(r7)
            r24 = r4
            goto L_0x061f
        L_0x060f:
            r10 = 0
            r24 = r4
            goto L_0x061f
        L_0x0613:
            r10 = 0
            r24 = r4
            goto L_0x061f
        L_0x0617:
            r12 = r68
            r21 = r1
            r1 = 0
            r10 = 0
            r24 = r4
        L_0x061f:
            if (r24 == 0) goto L_0x0695
            r5 = r24
            r9 = r17
            r6 = r0
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            r8 = r4
            im.bclpbkiauv.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.imageUrl
            if (r4 == 0) goto L_0x0639
            im.bclpbkiauv.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.imageUrl
            r8.put(r11, r4)
        L_0x0639:
            if (r67 == 0) goto L_0x066d
            int r4 = r16 + 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r13)
            r29 = r2
            r1 = r34
            r7.append(r1)
            java.lang.String r7 = r7.toString()
            r8.put(r3, r7)
            r3 = 10
            if (r4 == r3) goto L_0x0661
            int r3 = r28 + -1
            r13 = r46
            if (r13 != r3) goto L_0x065e
            goto L_0x0663
        L_0x065e:
            r16 = r4
            goto L_0x0671
        L_0x0661:
            r13 = r46
        L_0x0663:
            r7 = r29
            r8.put(r7, r14)
            r25 = 0
            r16 = r4
            goto L_0x0671
        L_0x066d:
            r1 = r34
            r13 = r46
        L_0x0671:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$NUjVeUpbmlZhp8xbgLNTaZpJKn8 r23 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$NUjVeUpbmlZhp8xbgLNTaZpJKn8
            r3 = r1
            r2 = r23
            r54 = r3
            r3 = r69
            r4 = r65
            r7 = r15
            r1 = r8
            r14 = 0
            r10 = r63
            r29 = r1
            r1 = r12
            r12 = r70
            r1 = r13
            r13 = r71
            r46 = r1
            r1 = 0
            r14 = r72
            r2.<init>(r4, r5, r6, r7, r8, r9, r10, r12, r13, r14)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r23)
            goto L_0x0698
        L_0x0695:
            r54 = r34
            r1 = 0
        L_0x0698:
            r0 = r16
            r42 = r21
            r35 = r30
            r34 = r31
            r38 = r32
            r22 = r33
            r29 = r45
            r33 = r46
            r7 = r47
            r11 = r49
            r9 = r50
            r13 = r51
            r53 = r52
            r51 = r54
            r39 = 0
            r40 = 1
            r44 = 0
            r45 = 73
            goto L_0x0e34
        L_0x06be:
            r0 = r1
            r46 = r6
            r47 = r7
            r52 = r8
            r50 = r9
            r21 = r10
            r49 = r11
            r11 = r12
            r45 = r15
            r54 = r34
            r51 = r38
            r1 = 0
            r22 = 73
            r7 = r2
            r15 = r5
            boolean r2 = r15.isVideo
            if (r2 == 0) goto L_0x0a4a
            r17 = 0
            r29 = 0
            if (r66 == 0) goto L_0x06e4
            r2 = 0
            r12 = r2
            goto L_0x06f2
        L_0x06e4:
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = r15.videoEditedInfo
            if (r2 == 0) goto L_0x06eb
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = r15.videoEditedInfo
            goto L_0x06f1
        L_0x06eb:
            java.lang.String r2 = r15.path
            im.bclpbkiauv.messenger.VideoEditedInfo r2 = createCompressionSettings(r2)
        L_0x06f1:
            r12 = r2
        L_0x06f2:
            if (r66 != 0) goto L_0x09f8
            if (r12 != 0) goto L_0x0715
            java.lang.String r2 = r15.path
            boolean r0 = r2.endsWith(r0)
            if (r0 == 0) goto L_0x06ff
            goto L_0x0715
        L_0x06ff:
            r42 = r21
            r41 = r30
            r1 = r45
            r48 = r46
            r53 = r52
            r56 = r54
            r44 = 0
            r45 = 73
            r52 = r12
            r55 = r15
            goto L_0x0a0c
        L_0x0715:
            java.lang.String r0 = r15.path
            java.lang.String r2 = r15.path
            java.io.File r5 = new java.io.File
            r5.<init>(r2)
            r34 = r5
            r5 = 0
            r8 = 0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r2)
            r10 = r2
            long r1 = r34.length()
            r9.append(r1)
            r1 = r30
            r9.append(r1)
            r30 = r3
            long r2 = r34.lastModified()
            r9.append(r2)
            java.lang.String r2 = r9.toString()
            if (r12 == 0) goto L_0x07a5
            boolean r8 = r12.muted
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r9 = r12.estimatedDuration
            r3.append(r9)
            r3.append(r1)
            long r9 = r12.startTime
            r3.append(r9)
            r3.append(r1)
            long r9 = r12.endTime
            r3.append(r9)
            boolean r9 = r12.muted
            if (r9 == 0) goto L_0x076d
            java.lang.String r9 = "_m"
            goto L_0x076e
        L_0x076d:
            r9 = r13
        L_0x076e:
            r3.append(r9)
            java.lang.String r2 = r3.toString()
            int r3 = r12.resultWidth
            int r9 = r12.originalWidth
            if (r3 == r9) goto L_0x078f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r1)
            int r9 = r12.resultWidth
            r3.append(r9)
            java.lang.String r2 = r3.toString()
        L_0x078f:
            long r9 = r12.startTime
            r35 = 0
            int r3 = (r9 > r35 ? 1 : (r9 == r35 ? 0 : -1))
            if (r3 < 0) goto L_0x079c
            long r9 = r12.startTime
            r36 = r9
            goto L_0x079e
        L_0x079c:
            r36 = 0
        L_0x079e:
            r5 = r36
            r10 = r2
            r35 = r8
            r8 = r5
            goto L_0x07a9
        L_0x07a5:
            r10 = r2
            r35 = r8
            r8 = r5
        L_0x07a9:
            r2 = 0
            r3 = 0
            if (r21 != 0) goto L_0x07f9
            int r5 = r15.ttl
            if (r5 != 0) goto L_0x07f9
            im.bclpbkiauv.messenger.MessagesStorage r5 = r65.getMessagesStorage()
            if (r21 != 0) goto L_0x07b9
            r6 = 2
            goto L_0x07ba
        L_0x07b9:
            r6 = 5
        L_0x07ba:
            java.lang.Object[] r36 = r5.getSentFile(r10, r6)
            if (r36 == 0) goto L_0x07ec
            r5 = 0
            r6 = r36[r5]
            r37 = r6
            im.bclpbkiauv.tgnet.TLRPC$TL_document r37 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r37
            r6 = 1
            r2 = r36[r6]
            r38 = r2
            java.lang.String r38 = (java.lang.String) r38
            java.lang.String r5 = r15.path
            r40 = 0
            r2 = r21
            r41 = r1
            r1 = r30
            r3 = r37
            r30 = r0
            r0 = r4
            r4 = r5
            r5 = r40
            r42 = r7
            r40 = r14
            r14 = 1
            r6 = r8
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r2 = r37
            goto L_0x0807
        L_0x07ec:
            r41 = r1
            r42 = r7
            r40 = r14
            r1 = r30
            r14 = 1
            r30 = r0
            r0 = r4
            goto L_0x0805
        L_0x07f9:
            r41 = r1
            r42 = r7
            r40 = r14
            r1 = r30
            r14 = 1
            r30 = r0
            r0 = r4
        L_0x0805:
            r38 = r3
        L_0x0807:
            if (r2 != 0) goto L_0x08f8
            java.lang.String r3 = r15.path
            android.graphics.Bitmap r3 = createVideoThumbnail(r3, r8)
            if (r3 != 0) goto L_0x0817
            java.lang.String r4 = r15.path
            android.graphics.Bitmap r3 = android.media.ThumbnailUtils.createVideoThumbnail(r4, r14)
        L_0x0817:
            r4 = 0
            if (r3 == 0) goto L_0x084a
            if (r21 != 0) goto L_0x082e
            int r5 = r15.ttl
            if (r5 == 0) goto L_0x0821
            goto L_0x082e
        L_0x0821:
            int r5 = r3.getWidth()
            int r6 = r3.getHeight()
            int r5 = java.lang.Math.max(r5, r6)
            goto L_0x0830
        L_0x082e:
            r5 = 90
        L_0x0830:
            float r6 = (float) r5
            float r7 = (float) r5
            r14 = 90
            if (r5 <= r14) goto L_0x0839
            r14 = 80
            goto L_0x083b
        L_0x0839:
            r14 = 55
        L_0x083b:
            r17 = r5
            r5 = r21
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r4 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r3, r6, r7, r14, r5)
            r6 = 0
            r7 = 1
            java.lang.String r29 = getKeyForPhotoSize(r4, r6, r7)
            goto L_0x084c
        L_0x084a:
            r5 = r21
        L_0x084c:
            im.bclpbkiauv.tgnet.TLRPC$TL_document r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_document
            r6.<init>()
            r2 = r6
            r6 = 0
            byte[] r7 = new byte[r6]
            r2.file_reference = r7
            if (r4 == 0) goto L_0x0864
            java.util.ArrayList r6 = r2.thumbs
            r6.add(r4)
            int r6 = r2.flags
            r7 = 1
            r6 = r6 | r7
            r2.flags = r6
        L_0x0864:
            r2.mime_type = r0
            im.bclpbkiauv.messenger.UserConfig r0 = r65.getUserConfig()
            r6 = 0
            r0.saveConfig(r6)
            if (r5 == 0) goto L_0x0884
            r0 = 66
            r14 = r52
            if (r14 < r0) goto L_0x087d
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            r7 = 1
            goto L_0x088e
        L_0x087d:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r0.<init>()
            r7 = 1
            goto L_0x088e
        L_0x0884:
            r14 = r52
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo r0 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            r7 = 1
            r0.supports_streaming = r7
        L_0x088e:
            java.util.ArrayList r6 = r2.attributes
            r6.add(r0)
            if (r12 == 0) goto L_0x08df
            boolean r6 = r12.needConvert()
            if (r6 == 0) goto L_0x08df
            boolean r6 = r12.muted
            if (r6 == 0) goto L_0x08af
            java.lang.String r6 = r15.path
            fillVideoAttribute(r6, r0, r12)
            int r6 = r0.w
            r12.originalWidth = r6
            int r6 = r0.h
            r12.originalHeight = r6
            r36 = r8
            goto L_0x08ba
        L_0x08af:
            r36 = r8
            long r7 = r12.estimatedDuration
            r43 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r43
            int r6 = (int) r7
            r0.duration = r6
        L_0x08ba:
            int r6 = r12.rotationValue
            r7 = 90
            if (r6 == r7) goto L_0x08d0
            int r6 = r12.rotationValue
            r7 = 270(0x10e, float:3.78E-43)
            if (r6 != r7) goto L_0x08c7
            goto L_0x08d0
        L_0x08c7:
            int r6 = r12.resultWidth
            r0.w = r6
            int r6 = r12.resultHeight
            r0.h = r6
            goto L_0x08d8
        L_0x08d0:
            int r6 = r12.resultHeight
            r0.w = r6
            int r6 = r12.resultWidth
            r0.h = r6
        L_0x08d8:
            long r6 = r12.estimatedSize
            int r7 = (int) r6
            r2.size = r7
            r7 = 0
            goto L_0x08f4
        L_0x08df:
            r36 = r8
            boolean r6 = r34.exists()
            if (r6 == 0) goto L_0x08ee
            long r6 = r34.length()
            int r7 = (int) r6
            r2.size = r7
        L_0x08ee:
            java.lang.String r6 = r15.path
            r7 = 0
            fillVideoAttribute(r6, r0, r7)
        L_0x08f4:
            r0 = r2
            r21 = r3
            goto L_0x0902
        L_0x08f8:
            r36 = r8
            r5 = r21
            r14 = r52
            r7 = 0
            r0 = r2
            r21 = r17
        L_0x0902:
            if (r12 == 0) goto L_0x092d
            boolean r2 = r12.muted
            if (r2 == 0) goto L_0x092d
            r2 = 0
            r3 = 0
            java.util.ArrayList r4 = r0.attributes
            int r4 = r4.size()
        L_0x0910:
            if (r3 >= r4) goto L_0x0921
            java.util.ArrayList r6 = r0.attributes
            java.lang.Object r6 = r6.get(r3)
            boolean r6 = r6 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeAnimated
            if (r6 == 0) goto L_0x091e
            r2 = 1
            goto L_0x0921
        L_0x091e:
            int r3 = r3 + 1
            goto L_0x0910
        L_0x0921:
            if (r2 != 0) goto L_0x092d
            java.util.ArrayList r3 = r0.attributes
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated
            r4.<init>()
            r3.add(r4)
        L_0x092d:
            if (r12 == 0) goto L_0x0961
            boolean r2 = r12.needConvert()
            if (r2 == 0) goto L_0x0961
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "-2147483648_"
            r2.append(r3)
            int r3 = im.bclpbkiauv.messenger.SharedConfig.getLastLocalId()
            r2.append(r3)
            java.lang.String r3 = ".mp4"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r39)
            r3.<init>(r4, r2)
            im.bclpbkiauv.messenger.SharedConfig.saveConfig()
            java.lang.String r4 = r3.getAbsolutePath()
            r30 = r4
        L_0x0961:
            r8 = r0
            r6 = r11
            r11 = r38
            r24 = r10
            r9 = r30
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r3 = r21
            r4 = r29
            if (r10 == 0) goto L_0x0977
            r2.put(r6, r10)
        L_0x0977:
            if (r35 != 0) goto L_0x09af
            if (r67 == 0) goto L_0x09af
            int r6 = r16 + 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r13)
            r39 = r3
            r43 = r4
            r3 = r54
            r7.append(r3)
            java.lang.String r7 = r7.toString()
            r2.put(r1, r7)
            r1 = 10
            if (r6 == r1) goto L_0x09a2
            int r1 = r28 + -1
            r13 = r46
            if (r13 != r1) goto L_0x09a0
            goto L_0x09a4
        L_0x09a0:
            r1 = r6
            goto L_0x09b9
        L_0x09a2:
            r13 = r46
        L_0x09a4:
            r7 = r40
            r1 = r42
            r2.put(r1, r7)
            r25 = 0
            r1 = r6
            goto L_0x09b9
        L_0x09af:
            r39 = r3
            r43 = r4
            r13 = r46
            r3 = r54
            r1 = r16
        L_0x09b9:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Qd6xNimQ3fj2PAcdjZEjo23uawY r23 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Qd6xNimQ3fj2PAcdjZEjo23uawY
            r40 = r2
            r2 = r23
            r42 = r5
            r5 = r69
            r6 = r65
            r16 = 1
            r44 = 0
            r7 = r12
            r46 = r10
            r10 = r40
            r52 = r12
            r48 = r13
            r12 = r63
            r54 = r1
            r53 = r14
            r1 = 1
            r14 = r70
            r55 = r15
            r1 = r45
            r45 = 73
            r16 = r71
            r17 = r72
            r56 = r3
            r3 = r39
            r4 = r43
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r23)
            r21 = r1
            r16 = r54
            r1 = r55
            goto L_0x0a2c
        L_0x09f8:
            r42 = r21
            r41 = r30
            r1 = r45
            r48 = r46
            r53 = r52
            r56 = r54
            r44 = 0
            r45 = 73
            r52 = r12
            r55 = r15
        L_0x0a0c:
            r15 = r55
            java.lang.String r3 = r15.path
            java.lang.String r4 = r15.path
            r5 = 0
            r6 = 0
            java.lang.String r10 = r15.caption
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r11 = r15.entities
            r2 = r65
            r7 = r63
            r9 = r70
            r12 = r69
            r13 = r66
            r14 = r71
            r21 = r1
            r1 = r15
            r15 = r72
            prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
        L_0x0a2c:
            r0 = r16
            r29 = r21
            r34 = r31
            r38 = r32
            r22 = r33
            r35 = r41
            r7 = r47
            r33 = r48
            r11 = r49
            r9 = r50
            r13 = r51
            r51 = r56
            r39 = 0
            r40 = 1
            goto L_0x0e34
        L_0x0a4a:
            r10 = r3
            r8 = r7
            r6 = r11
            r7 = r14
            r1 = r15
            r42 = r21
            r41 = r30
            r21 = r45
            r48 = r46
            r53 = r52
            r56 = r54
            r44 = 0
            r45 = 73
            java.lang.String r0 = r1.path
            java.lang.String r2 = r1.path
            if (r2 != 0) goto L_0x0a75
            android.net.Uri r3 = r1.uri
            if (r3 == 0) goto L_0x0a75
            android.net.Uri r3 = r1.uri
            java.lang.String r2 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r3)
            android.net.Uri r3 = r1.uri
            java.lang.String r0 = r3.toString()
        L_0x0a75:
            r3 = 0
            if (r66 != 0) goto L_0x0afd
            java.lang.String r4 = r1.path
            android.net.Uri r5 = r1.uri
            boolean r4 = im.bclpbkiauv.messenger.ImageLoader.shouldSendImageAsDocument(r4, r5)
            if (r4 == 0) goto L_0x0a88
            r15 = r31
            r14 = r33
            goto L_0x0b01
        L_0x0a88:
            if (r2 == 0) goto L_0x0ab3
            r15 = r31
            boolean r4 = r2.endsWith(r15)
            if (r4 != 0) goto L_0x0a9b
            r14 = r33
            boolean r4 = r2.endsWith(r14)
            if (r4 == 0) goto L_0x0ab7
            goto L_0x0a9d
        L_0x0a9b:
            r14 = r33
        L_0x0a9d:
            boolean r4 = r2.endsWith(r15)
            if (r4 == 0) goto L_0x0aa8
            java.lang.String r4 = "gif"
            r27 = r4
            goto L_0x0aad
        L_0x0aa8:
            java.lang.String r4 = "webp"
            r27 = r4
        L_0x0aad:
            r3 = 1
            r12 = r2
            r17 = r3
            goto L_0x0b14
        L_0x0ab3:
            r15 = r31
            r14 = r33
        L_0x0ab7:
            if (r2 != 0) goto L_0x0af9
            android.net.Uri r4 = r1.uri
            if (r4 == 0) goto L_0x0af9
            android.net.Uri r4 = r1.uri
            boolean r4 = im.bclpbkiauv.messenger.MediaController.isGif(r4)
            if (r4 == 0) goto L_0x0ada
            r3 = 1
            android.net.Uri r4 = r1.uri
            java.lang.String r0 = r4.toString()
            android.net.Uri r4 = r1.uri
            java.lang.String r5 = "gif"
            java.lang.String r2 = im.bclpbkiauv.messenger.MediaController.copyFileToCache(r4, r5)
            java.lang.String r27 = "gif"
            r12 = r2
            r17 = r3
            goto L_0x0b14
        L_0x0ada:
            android.net.Uri r4 = r1.uri
            boolean r4 = im.bclpbkiauv.messenger.MediaController.isWebp(r4)
            if (r4 == 0) goto L_0x0af9
            r3 = 1
            android.net.Uri r4 = r1.uri
            java.lang.String r0 = r4.toString()
            android.net.Uri r4 = r1.uri
            java.lang.String r5 = "webp"
            java.lang.String r2 = im.bclpbkiauv.messenger.MediaController.copyFileToCache(r4, r5)
            java.lang.String r27 = "webp"
            r12 = r2
            r17 = r3
            goto L_0x0b14
        L_0x0af9:
            r12 = r2
            r17 = r3
            goto L_0x0b14
        L_0x0afd:
            r15 = r31
            r14 = r33
        L_0x0b01:
            r3 = 1
            if (r2 == 0) goto L_0x0b0e
            java.io.File r4 = new java.io.File
            r4.<init>(r2)
            java.lang.String r4 = im.bclpbkiauv.messenger.FileLoader.getFileExtension(r4)
            goto L_0x0b0f
        L_0x0b0e:
            r4 = r13
        L_0x0b0f:
            r27 = r4
            r12 = r2
            r17 = r3
        L_0x0b14:
            if (r17 == 0) goto L_0x0b60
            r11 = r51
            if (r11 != 0) goto L_0x0b33
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r13 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r11 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r9 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r7 = r2
            goto L_0x0b3a
        L_0x0b33:
            r13 = r11
            r7 = r47
            r11 = r49
            r9 = r50
        L_0x0b3a:
            r13.add(r12)
            r11.add(r0)
            java.lang.String r2 = r1.caption
            r9.add(r2)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r2 = r1.entities
            r7.add(r2)
            r22 = r14
            r34 = r15
            r0 = r16
            r29 = r21
            r38 = r32
            r35 = r41
            r33 = r48
            r51 = r56
            r39 = 0
            r40 = 1
            goto L_0x0e34
        L_0x0b60:
            r11 = r51
            if (r12 == 0) goto L_0x0bc3
            java.io.File r2 = new java.io.File
            r2.<init>(r12)
            r9 = r68
            r5 = r48
            if (r9 != 0) goto L_0x0b97
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r46 = r5
            long r4 = r2.length()
            r3.append(r4)
            r5 = r41
            r3.append(r5)
            r24 = r6
            r40 = r7
            long r6 = r2.lastModified()
            r3.append(r6)
            java.lang.String r0 = r3.toString()
            r6 = r32
            goto L_0x0bc1
        L_0x0b97:
            r46 = r5
            r24 = r6
            r40 = r7
            r5 = r41
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r6 = r2.length()
            r3.append(r6)
            r3.append(r5)
            long r6 = r2.lastModified()
            r3.append(r6)
            r6 = r32
            r3.append(r6)
            java.lang.String r0 = r3.toString()
        L_0x0bc1:
            r7 = r0
            goto L_0x0bd1
        L_0x0bc3:
            r9 = r68
            r24 = r6
            r40 = r7
            r6 = r32
            r5 = r41
            r46 = r48
            r0 = 0
            r7 = r0
        L_0x0bd1:
            r0 = 0
            r2 = 0
            if (r21 == 0) goto L_0x0c0f
            r4 = r21
            java.lang.Object r3 = r4.get(r1)
            im.bclpbkiauv.messenger.SendMessagesHelper$MediaSendPrepareWorker r3 = (im.bclpbkiauv.messenger.SendMessagesHelper.MediaSendPrepareWorker) r3
            r21 = r2
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r2 = r3.photo
            r29 = r4
            java.lang.String r4 = r3.parentObject
            if (r2 != 0) goto L_0x0bf5
            java.util.concurrent.CountDownLatch r0 = r3.sync     // Catch:{ Exception -> 0x0bed }
            r0.await()     // Catch:{ Exception -> 0x0bed }
            goto L_0x0bf1
        L_0x0bed:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0bf1:
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r2 = r3.photo
            java.lang.String r4 = r3.parentObject
        L_0x0bf5:
            r21 = r4
            r35 = r5
            r38 = r6
            r51 = r11
            r33 = r14
            r34 = r15
            r11 = r40
            r15 = r46
            r14 = r7
            r7 = r2
            r60 = r24
            r24 = r12
            r12 = r60
            goto L_0x0cd8
        L_0x0c0f:
            r29 = r21
            r21 = r2
            if (r42 != 0) goto L_0x0c9c
            int r2 = r1.ttl
            if (r2 != 0) goto L_0x0c9c
            im.bclpbkiauv.messenger.MessagesStorage r2 = r65.getMessagesStorage()
            if (r42 != 0) goto L_0x0c21
            r3 = 0
            goto L_0x0c22
        L_0x0c21:
            r3 = 3
        L_0x0c22:
            java.lang.Object[] r2 = r2.getSentFile(r7, r3)
            if (r2 == 0) goto L_0x0c36
            r3 = 0
            r4 = r2[r3]
            r0 = r4
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r0
            r3 = 1
            r4 = r2[r3]
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            r21 = r3
        L_0x0c36:
            if (r0 != 0) goto L_0x0c6a
            android.net.Uri r3 = r1.uri
            if (r3 == 0) goto L_0x0c6a
            im.bclpbkiauv.messenger.MessagesStorage r3 = r65.getMessagesStorage()
            android.net.Uri r4 = r1.uri
            java.lang.String r4 = im.bclpbkiauv.messenger.AndroidUtilities.getPath(r4)
            r30 = r0
            if (r42 != 0) goto L_0x0c4c
            r0 = 0
            goto L_0x0c4d
        L_0x0c4c:
            r0 = 3
        L_0x0c4d:
            java.lang.Object[] r2 = r3.getSentFile(r4, r0)
            if (r2 == 0) goto L_0x0c63
            r3 = 0
            r0 = r2[r3]
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_photo) r0
            r3 = 1
            r4 = r2[r3]
            r3 = r4
            java.lang.String r3 = (java.lang.String) r3
            r21 = r2
            r30 = r3
            goto L_0x0c72
        L_0x0c63:
            r0 = r30
            r30 = r21
            r21 = r2
            goto L_0x0c72
        L_0x0c6a:
            r30 = r0
            r0 = r30
            r30 = r21
            r21 = r2
        L_0x0c72:
            java.lang.String r4 = r1.path
            android.net.Uri r3 = r1.uri
            r31 = 0
            r2 = r42
            r33 = r3
            r3 = r0
            r35 = r5
            r34 = r15
            r15 = r46
            r5 = r33
            r38 = r6
            r51 = r11
            r33 = r14
            r11 = r40
            r14 = r7
            r60 = r24
            r24 = r12
            r12 = r60
            r6 = r31
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r2 = r30
            goto L_0x0cb3
        L_0x0c9c:
            r35 = r5
            r38 = r6
            r51 = r11
            r33 = r14
            r34 = r15
            r11 = r40
            r15 = r46
            r14 = r7
            r60 = r24
            r24 = r12
            r12 = r60
            r2 = r21
        L_0x0cb3:
            if (r0 != 0) goto L_0x0cd5
            im.bclpbkiauv.messenger.SendMessagesHelper r3 = r65.getSendMessagesHelper()
            java.lang.String r4 = r1.path
            android.net.Uri r5 = r1.uri
            im.bclpbkiauv.tgnet.TLRPC$TL_photo r0 = r3.generatePhotoSizes(r4, r5, r9)
            if (r42 == 0) goto L_0x0cd1
            boolean r3 = r1.canDeleteAfter
            if (r3 == 0) goto L_0x0cd1
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r1.path
            r3.<init>(r4)
            r3.delete()
        L_0x0cd1:
            r7 = r0
            r21 = r2
            goto L_0x0cd8
        L_0x0cd5:
            r7 = r0
            r21 = r2
        L_0x0cd8:
            if (r7 == 0) goto L_0x0de7
            r6 = r7
            r9 = r21
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r5 = r0
            r2 = 1
            android.graphics.Bitmap[] r4 = new android.graphics.Bitmap[r2]
            java.lang.String[] r3 = new java.lang.String[r2]
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r0 = r1.masks
            if (r0 == 0) goto L_0x0cf6
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r0 = r1.masks
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0cf6
            r0 = 1
            goto L_0x0cf7
        L_0x0cf6:
            r0 = 0
        L_0x0cf7:
            r7.has_stickers = r0
            if (r0 == 0) goto L_0x0d3f
            im.bclpbkiauv.tgnet.SerializedData r0 = new im.bclpbkiauv.tgnet.SerializedData
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r2 = r1.masks
            int r2 = r2.size()
            int r2 = r2 * 20
            int r2 = r2 + 4
            r0.<init>((int) r2)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r2 = r1.masks
            int r2 = r2.size()
            r0.writeInt32(r2)
            r2 = 0
        L_0x0d14:
            r30 = r7
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r7 = r1.masks
            int r7 = r7.size()
            if (r2 >= r7) goto L_0x0d2e
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$InputDocument> r7 = r1.masks
            java.lang.Object r7 = r7.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$InputDocument r7 = (im.bclpbkiauv.tgnet.TLRPC.InputDocument) r7
            r7.serializeToStream(r0)
            int r2 = r2 + 1
            r7 = r30
            goto L_0x0d14
        L_0x0d2e:
            byte[] r2 = r0.toByteArray()
            java.lang.String r2 = im.bclpbkiauv.messenger.Utilities.bytesToHex(r2)
            java.lang.String r7 = "masks"
            r5.put(r7, r2)
            r0.cleanup()
            goto L_0x0d41
        L_0x0d3f:
            r30 = r7
        L_0x0d41:
            if (r14 == 0) goto L_0x0d46
            r5.put(r12, r14)
        L_0x0d46:
            if (r67 == 0) goto L_0x0d55
            int r0 = r62.size()     // Catch:{ Exception -> 0x0d52 }
            r12 = 1
            if (r0 != r12) goto L_0x0d50
            goto L_0x0d56
        L_0x0d50:
            r7 = 0
            goto L_0x0d6d
        L_0x0d52:
            r0 = move-exception
            r12 = 1
            goto L_0x0d6f
        L_0x0d55:
            r12 = 1
        L_0x0d56:
            java.util.ArrayList r0 = r6.sizes     // Catch:{ Exception -> 0x0d6e }
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x0d6e }
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)     // Catch:{ Exception -> 0x0d6e }
            if (r0 == 0) goto L_0x0d6c
            r7 = 0
            java.lang.String r2 = getKeyForPhotoSize(r0, r4, r7)     // Catch:{ Exception -> 0x0d6a }
            r3[r7] = r2     // Catch:{ Exception -> 0x0d6a }
            goto L_0x0d6d
        L_0x0d6a:
            r0 = move-exception
            goto L_0x0d70
        L_0x0d6c:
            r7 = 0
        L_0x0d6d:
            goto L_0x0d73
        L_0x0d6e:
            r0 = move-exception
        L_0x0d6f:
            r7 = 0
        L_0x0d70:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0d73:
            if (r67 == 0) goto L_0x0da1
            int r0 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            r22 = r14
            r13 = r56
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            r5.put(r10, r2)
            r2 = 10
            if (r0 == r2) goto L_0x0d99
            int r2 = r28 + -1
            if (r15 != r2) goto L_0x0d96
            goto L_0x0d99
        L_0x0d96:
            r16 = r0
            goto L_0x0da5
        L_0x0d99:
            r5.put(r8, r11)
            r25 = 0
            r16 = r0
            goto L_0x0da5
        L_0x0da1:
            r22 = r14
            r13 = r56
        L_0x0da5:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$K__IVX5ABgCRpdYLU63AAgUuT6E r0 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$K__IVX5ABgCRpdYLU63AAgUuT6E
            r2 = r0
            r23 = r3
            r3 = r4
            r31 = r4
            r4 = r23
            r32 = r5
            r5 = r69
            r36 = r6
            r6 = r65
            r39 = 0
            r7 = r36
            r8 = r32
            r58 = r51
            r10 = r63
            r59 = r24
            r40 = 1
            r12 = r70
            r51 = r13
            r13 = r1
            r55 = r1
            r1 = r22
            r22 = r33
            r14 = r71
            r33 = r15
            r15 = r72
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r0)
            r0 = r16
            r7 = r47
            r11 = r49
            r9 = r50
            r13 = r58
            goto L_0x0e34
        L_0x0de7:
            r55 = r1
            r30 = r7
            r1 = r14
            r59 = r24
            r22 = r33
            r58 = r51
            r51 = r56
            r39 = 0
            r40 = 1
            r33 = r15
            r15 = r58
            if (r15 != 0) goto L_0x0e17
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r9 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            goto L_0x0e1e
        L_0x0e17:
            r13 = r15
            r7 = r47
            r11 = r49
            r9 = r50
        L_0x0e1e:
            r2 = r59
            r13.add(r2)
            r11.add(r1)
            r3 = r55
            java.lang.String r0 = r3.caption
            r9.add(r0)
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r0 = r3.entities
            r7.add(r0)
            r0 = r16
        L_0x0e34:
            int r6 = r33 + 1
            r1 = r62
            r33 = r22
            r12 = r28
            r15 = r29
            r31 = r34
            r30 = r35
            r32 = r38
            r10 = r42
            r14 = r44
            r23 = r51
            r8 = r53
            goto L_0x0266
        L_0x0e4e:
            r33 = r6
            r47 = r7
            r53 = r8
            r50 = r9
            r42 = r10
            r49 = r11
            r28 = r12
            r29 = r15
            r15 = r13
            r1 = 0
            int r3 = (r25 > r1 ? 1 : (r25 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0e74
            r1 = r25
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Nh_QI_PMJNChPpyXWyyCdSnb0DU r3 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$Nh_QI_PMJNChPpyXWyyCdSnb0DU
            r14 = r65
            r13 = r72
            r3.<init>(r1, r13)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r3)
            goto L_0x0e78
        L_0x0e74:
            r14 = r65
            r13 = r72
        L_0x0e78:
            if (r73 == 0) goto L_0x0e7d
            r73.releasePermission()
        L_0x0e7d:
            if (r15 == 0) goto L_0x0ee5
            boolean r1 = r15.isEmpty()
            if (r1 != 0) goto L_0x0ee5
            r1 = 0
        L_0x0e86:
            int r2 = r15.size()
            if (r1 >= r2) goto L_0x0edc
            java.lang.Object r2 = r15.get(r1)
            r3 = r2
            java.lang.String r3 = (java.lang.String) r3
            r12 = r49
            java.lang.Object r2 = r12.get(r1)
            r4 = r2
            java.lang.String r4 = (java.lang.String) r4
            r5 = 0
            r11 = r50
            java.lang.Object r2 = r11.get(r1)
            r10 = r2
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            r9 = r47
            java.lang.Object r2 = r9.get(r1)
            r16 = r2
            java.util.ArrayList r16 = (java.util.ArrayList) r16
            r2 = r65
            r6 = r27
            r7 = r63
            r17 = r9
            r9 = r70
            r20 = r11
            r11 = r16
            r16 = r12
            r12 = r69
            r13 = r66
            r14 = r71
            r21 = r15
            r15 = r72
            prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
            int r1 = r1 + 1
            r14 = r65
            r13 = r72
            r49 = r16
            r47 = r17
            r50 = r20
            r15 = r21
            goto L_0x0e86
        L_0x0edc:
            r21 = r15
            r17 = r47
            r16 = r49
            r20 = r50
            goto L_0x0eed
        L_0x0ee5:
            r21 = r15
            r17 = r47
            r16 = r49
            r20 = r50
        L_0x0eed:
            boolean r1 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x0f0c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "total send time = "
            r1.append(r2)
            long r2 = java.lang.System.currentTimeMillis()
            long r2 = r2 - r18
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            im.bclpbkiauv.messenger.FileLog.d(r1)
        L_0x0f0c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.lambda$prepareSendingMedia$63(java.util.ArrayList, long, im.bclpbkiauv.messenger.AccountInstance, boolean, boolean, boolean, im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$57(MediaSendPrepareWorker worker, AccountInstance accountInstance, SendingMediaInfo info, boolean blnOriginalImg, boolean isEncrypted) {
        worker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(info.path, info.uri, blnOriginalImg);
        if (isEncrypted && info.canDeleteAfter) {
            new File(info.path).delete();
        }
        worker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$58(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        SendingMediaInfo sendingMediaInfo = info;
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, documentFinal, pathFinal, params, false, parentFinal);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(documentFinal, (VideoEditedInfo) null, pathFinal, dialog_id, reply_to_msg, sendingMediaInfo.caption, sendingMediaInfo.entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, 0, parentFinal);
    }

    static /* synthetic */ void lambda$null$59(MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_photo photoFinal, boolean needDownloadHttpFinal, SendingMediaInfo info, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, boolean notify, int scheduleDate) {
        SendingMediaInfo sendingMediaInfo = info;
        String str = null;
        if (editingMessageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (needDownloadHttpFinal) {
                str = sendingMediaInfo.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessageMedia(editingMessageObject, photoFinal, (VideoEditedInfo) null, (TLRPC.TL_document) null, str, params, false, parentFinal);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (needDownloadHttpFinal) {
            str = sendingMediaInfo.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(photoFinal, str, dialog_id, reply_to_msg, sendingMediaInfo.caption, sendingMediaInfo.entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, sendingMediaInfo.ttl, parentFinal);
    }

    static /* synthetic */ void lambda$null$60(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        Bitmap bitmap = thumbFinal;
        String str = thumbKeyFinal;
        SendingMediaInfo sendingMediaInfo = info;
        if (!(bitmap == null || str == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), str);
        }
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, (TLRPC.TL_photo) null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, sendingMediaInfo.caption, sendingMediaInfo.entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, sendingMediaInfo.ttl, parentFinal);
    }

    static /* synthetic */ void lambda$null$61(Bitmap[] bitmapFinal, String[] keyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, TLRPC.TL_photo photoFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info, boolean notify, int scheduleDate) {
        SendingMediaInfo sendingMediaInfo = info;
        if (!(bitmapFinal[0] == null || keyFinal[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal[0]), keyFinal[0]);
        }
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, photoFinal, (VideoEditedInfo) null, (TLRPC.TL_document) null, (String) null, params, false, parentFinal);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(photoFinal, (String) null, dialog_id, reply_to_msg, sendingMediaInfo.caption, sendingMediaInfo.entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, sendingMediaInfo.ttl, parentFinal);
    }

    static /* synthetic */ void lambda$null$62(AccountInstance accountInstance, long lastGroupIdFinal, int scheduleDate) {
        SendMessagesHelper instance = accountInstance.getSendMessagesHelper();
        HashMap<String, ArrayList<DelayedMessage>> hashMap = instance.delayedMessages;
        ArrayList<DelayedMessage> arrayList = hashMap.get("group_" + lastGroupIdFinal);
        if (arrayList != null && !arrayList.isEmpty()) {
            DelayedMessage message = arrayList.get(0);
            MessageObject prevMessage = message.messageObjects.get(message.messageObjects.size() - 1);
            message.finalGroupMessage = prevMessage.getId();
            prevMessage.messageOwner.params.put("final", "1");
            TLRPC.TL_messages_messages messagesRes = new TLRPC.TL_messages_messages();
            messagesRes.messages.add(prevMessage.messageOwner);
            accountInstance.getMessagesStorage().putMessages((TLRPC.messages_Messages) messagesRes, message.peer, -2, 0, false, scheduleDate != 0);
            instance.sendReadyToSendGroup(message, true, true);
        }
    }

    private static void fillVideoAttribute(String videoPath, TLRPC.TL_documentAttributeVideo attributeVideo, VideoEditedInfo videoEditedInfo) {
        String rotation;
        boolean infoObtained = false;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
            mediaMetadataRetriever2.setDataSource(videoPath);
            String width = mediaMetadataRetriever2.extractMetadata(18);
            if (width != null) {
                attributeVideo.w = Integer.parseInt(width);
            }
            String height = mediaMetadataRetriever2.extractMetadata(19);
            if (height != null) {
                attributeVideo.h = Integer.parseInt(height);
            }
            String duration = mediaMetadataRetriever2.extractMetadata(9);
            if (duration != null) {
                attributeVideo.duration = (int) Math.ceil((double) (((float) Long.parseLong(duration)) / 1000.0f));
            }
            if (Build.VERSION.SDK_INT >= 17 && (rotation = mediaMetadataRetriever2.extractMetadata(24)) != null) {
                int val = Utilities.parseInt(rotation).intValue();
                if (videoEditedInfo != null) {
                    videoEditedInfo.rotationValue = val;
                } else if (val == 90 || val == 270) {
                    int temp = attributeVideo.w;
                    attributeVideo.w = attributeVideo.h;
                    attributeVideo.h = temp;
                }
            }
            infoObtained = true;
            try {
                mediaMetadataRetriever2.release();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        } catch (Throwable th) {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            throw th;
        }
        if (!infoObtained) {
            try {
                MediaPlayer mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
                if (mp != null) {
                    attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                    attributeVideo.w = mp.getVideoWidth();
                    attributeVideo.h = mp.getVideoHeight();
                    mp.release();
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
        }
    }

    private static Bitmap createVideoThumbnail(String filePath, long time) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(time, 1);
            try {
                retriever.release();
            } catch (RuntimeException e) {
            }
        } catch (Exception e2) {
            retriever.release();
        } catch (Throwable th) {
            try {
                retriever.release();
            } catch (RuntimeException e3) {
            }
            throw th;
        }
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }

    /* JADX WARNING: Removed duplicated region for block: B:90:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static im.bclpbkiauv.messenger.VideoEditedInfo createCompressionSettings(java.lang.String r20) {
        /*
            r1 = r20
            java.lang.String r0 = "video/avc"
            r2 = 9
            int[] r2 = new int[r2]
            im.bclpbkiauv.ui.components.AnimatedFileDrawable.getVideoInfo(r1, r2)
            r3 = 0
            r3 = r2[r3]
            r4 = 0
            if (r3 != 0) goto L_0x001d
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "video hasn't avc1 atom"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x001c:
            return r4
        L_0x001d:
            r3 = 3
            r5 = r2[r3]
            r3 = r2[r3]
            r6 = 4
            r6 = r2[r6]
            float r6 = (float) r6
            r7 = 6
            r7 = r2[r7]
            long r7 = (long) r7
            r9 = 5
            r9 = r2[r9]
            long r9 = (long) r9
            r11 = 7
            r11 = r2[r11]
            r12 = 900000(0xdbba0, float:1.261169E-39)
            if (r3 <= r12) goto L_0x0039
            r3 = 900000(0xdbba0, float:1.261169E-39)
        L_0x0039:
            int r12 = android.os.Build.VERSION.SDK_INT
            r13 = 18
            if (r12 >= r13) goto L_0x00b9
            android.media.MediaCodecInfo r12 = im.bclpbkiauv.messenger.MediaController.selectCodec(r0)     // Catch:{ Exception -> 0x00b7 }
            if (r12 != 0) goto L_0x004f
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00b7 }
            if (r0 == 0) goto L_0x004e
            java.lang.String r0 = "no codec info for video/avc"
            im.bclpbkiauv.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x00b7 }
        L_0x004e:
            return r4
        L_0x004f:
            java.lang.String r13 = r12.getName()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r14 = "OMX.google.h264.encoder"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.ST.VFM.H264Enc"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.Exynos.avc.enc"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.MARVELL.VIDEO.HW.CODA7542ENCODER"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.MARVELL.VIDEO.H264ENCODER"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.k3.video.encoder.avc"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 != 0) goto L_0x009d
            java.lang.String r14 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r14 = r13.equals(r14)     // Catch:{ Exception -> 0x00b7 }
            if (r14 == 0) goto L_0x008c
            goto L_0x009d
        L_0x008c:
            int r0 = im.bclpbkiauv.messenger.MediaController.selectColorFormat(r12, r0)     // Catch:{ Exception -> 0x00b7 }
            if (r0 != 0) goto L_0x009c
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00b7 }
            if (r0 == 0) goto L_0x009b
            java.lang.String r0 = "no color format for video/avc"
            im.bclpbkiauv.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x00b7 }
        L_0x009b:
            return r4
        L_0x009c:
            goto L_0x00b9
        L_0x009d:
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00b7 }
            if (r0 == 0) goto L_0x00b6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b7 }
            r0.<init>()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r14 = "unsupported encoder = "
            r0.append(r14)     // Catch:{ Exception -> 0x00b7 }
            r0.append(r13)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00b7 }
            im.bclpbkiauv.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x00b7 }
        L_0x00b6:
            return r4
        L_0x00b7:
            r0 = move-exception
            return r4
        L_0x00b9:
            im.bclpbkiauv.messenger.VideoEditedInfo r0 = new im.bclpbkiauv.messenger.VideoEditedInfo
            r0.<init>()
            r12 = -1
            r0.startTime = r12
            r0.endTime = r12
            r0.bitrate = r3
            r0.originalPath = r1
            r0.framerate = r11
            double r12 = (double) r6
            double r12 = java.lang.Math.ceil(r12)
            long r12 = (long) r12
            r0.estimatedDuration = r12
            r4 = 1
            r12 = r2[r4]
            r0.originalWidth = r12
            r0.resultWidth = r12
            r12 = 2
            r13 = r2[r12]
            r0.originalHeight = r13
            r0.resultHeight = r13
            r13 = 8
            r13 = r2[r13]
            r0.rotationValue = r13
            android.content.SharedPreferences r13 = im.bclpbkiauv.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r14 = "compress_video2"
            int r14 = r13.getInt(r14, r4)
            int r15 = r0.originalWidth
            r12 = 1280(0x500, float:1.794E-42)
            if (r15 > r12) goto L_0x0124
            int r15 = r0.originalHeight
            if (r15 <= r12) goto L_0x00fb
            goto L_0x0124
        L_0x00fb:
            int r12 = r0.originalWidth
            r15 = 848(0x350, float:1.188E-42)
            if (r12 > r15) goto L_0x0122
            int r12 = r0.originalHeight
            if (r12 <= r15) goto L_0x0106
            goto L_0x0122
        L_0x0106:
            int r12 = r0.originalWidth
            r15 = 640(0x280, float:8.97E-43)
            if (r12 > r15) goto L_0x0120
            int r12 = r0.originalHeight
            if (r12 <= r15) goto L_0x0111
            goto L_0x0120
        L_0x0111:
            int r12 = r0.originalWidth
            r15 = 480(0x1e0, float:6.73E-43)
            if (r12 > r15) goto L_0x011e
            int r12 = r0.originalHeight
            if (r12 <= r15) goto L_0x011c
            goto L_0x011e
        L_0x011c:
            r12 = 1
            goto L_0x0125
        L_0x011e:
            r12 = 2
            goto L_0x0125
        L_0x0120:
            r12 = 3
            goto L_0x0125
        L_0x0122:
            r12 = 4
            goto L_0x0125
        L_0x0124:
            r12 = 5
        L_0x0125:
            if (r14 < r12) goto L_0x0129
            int r14 = r12 + -1
        L_0x0129:
            int r15 = r12 + -1
            if (r14 == r15) goto L_0x0191
            if (r14 == 0) goto L_0x0146
            if (r14 == r4) goto L_0x0140
            r4 = 2
            if (r14 == r4) goto L_0x013a
            r4 = 2500000(0x2625a0, float:3.503246E-39)
            r15 = 1151336448(0x44a00000, float:1280.0)
            goto L_0x014c
        L_0x013a:
            r15 = 1146355712(0x44540000, float:848.0)
            r4 = 1100000(0x10c8e0, float:1.541428E-39)
            goto L_0x014c
        L_0x0140:
            r15 = 1142947840(0x44200000, float:640.0)
            r4 = 900000(0xdbba0, float:1.261169E-39)
            goto L_0x014c
        L_0x0146:
            r15 = 1138229248(0x43d80000, float:432.0)
            r4 = 400000(0x61a80, float:5.6052E-40)
        L_0x014c:
            r17 = r2
            int r2 = r0.originalWidth
            r18 = r7
            int r7 = r0.originalHeight
            if (r2 <= r7) goto L_0x0159
            int r2 = r0.originalWidth
            goto L_0x015b
        L_0x0159:
            int r2 = r0.originalHeight
        L_0x015b:
            float r2 = (float) r2
            float r2 = r15 / r2
            int r7 = r0.originalWidth
            float r7 = (float) r7
            float r7 = r7 * r2
            r8 = 1073741824(0x40000000, float:2.0)
            float r7 = r7 / r8
            int r7 = java.lang.Math.round(r7)
            r16 = 2
            int r7 = r7 * 2
            r0.resultWidth = r7
            int r7 = r0.originalHeight
            float r7 = (float) r7
            float r7 = r7 * r2
            float r7 = r7 / r8
            int r7 = java.lang.Math.round(r7)
            int r7 = r7 * 2
            r0.resultHeight = r7
            if (r3 == 0) goto L_0x0195
            float r7 = (float) r5
            float r7 = r7 / r2
            int r7 = (int) r7
            int r3 = java.lang.Math.min(r4, r7)
            int r7 = r3 / 8
            float r7 = (float) r7
            float r7 = r7 * r6
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r8
            long r7 = (long) r7
            goto L_0x0197
        L_0x0191:
            r17 = r2
            r18 = r7
        L_0x0195:
            r7 = r18
        L_0x0197:
            int r2 = r12 + -1
            if (r14 != r2) goto L_0x01b4
            int r2 = r0.originalWidth
            r0.resultWidth = r2
            int r2 = r0.originalHeight
            r0.resultHeight = r2
            r0.bitrate = r5
            java.io.File r2 = new java.io.File
            r2.<init>(r1)
            long r1 = r2.length()
            int r2 = (int) r1
            long r1 = (long) r2
            r0.estimatedSize = r1
            r15 = r3
            goto L_0x01cd
        L_0x01b4:
            r0.bitrate = r3
            long r1 = r9 + r7
            int r2 = (int) r1
            long r1 = (long) r2
            r0.estimatedSize = r1
            long r1 = r0.estimatedSize
            r15 = r3
            long r3 = r0.estimatedSize
            r18 = 32768(0x8000, double:1.61895E-319)
            long r3 = r3 / r18
            r18 = 16
            long r3 = r3 * r18
            long r1 = r1 + r3
            r0.estimatedSize = r1
        L_0x01cd:
            long r1 = r0.estimatedSize
            r3 = 0
            int r16 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r16 != 0) goto L_0x01d9
            r1 = 1
            r0.estimatedSize = r1
        L_0x01d9:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.createCompressionSettings(java.lang.String):im.bclpbkiauv.messenger.VideoEditedInfo");
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo info, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<TLRPC.MessageEntity> entities, int ttl, MessageObject editingMessageObject, boolean notify, int scheduleDate) {
        if (videoPath != null && videoPath.length() != 0) {
            $$Lambda$SendMessagesHelper$bqzeLZVH3kzmGTnFhaCP2Qsk_I0 r19 = r0;
            $$Lambda$SendMessagesHelper$bqzeLZVH3kzmGTnFhaCP2Qsk_I0 r0 = new Runnable(info, videoPath, dialog_id, duration, ttl, accountInstance, height, width, estimatedSize, caption, editingMessageObject, reply_to_msg, entities, notify, scheduleDate) {
                private final /* synthetic */ VideoEditedInfo f$0;
                private final /* synthetic */ String f$1;
                private final /* synthetic */ MessageObject f$10;
                private final /* synthetic */ MessageObject f$11;
                private final /* synthetic */ ArrayList f$12;
                private final /* synthetic */ boolean f$13;
                private final /* synthetic */ int f$14;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ AccountInstance f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ long f$8;
                private final /* synthetic */ CharSequence f$9;

                {
                    this.f$0 = r4;
                    this.f$1 = r5;
                    this.f$2 = r6;
                    this.f$3 = r8;
                    this.f$4 = r10;
                    this.f$5 = r11;
                    this.f$6 = r12;
                    this.f$7 = r13;
                    this.f$8 = r14;
                    this.f$9 = r16;
                    this.f$10 = r17;
                    this.f$11 = r18;
                    this.f$12 = r19;
                    this.f$13 = r20;
                    this.f$14 = r21;
                }

                public final void run() {
                    VideoEditedInfo videoEditedInfo = this.f$0;
                    VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
                    SendMessagesHelper.lambda$prepareSendingVideo$65(videoEditedInfo2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14);
                }
            };
            new Thread(r19).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:118:0x0311  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x035f  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0151  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingVideo$65(im.bclpbkiauv.messenger.VideoEditedInfo r36, java.lang.String r37, long r38, long r40, int r42, im.bclpbkiauv.messenger.AccountInstance r43, int r44, int r45, long r46, java.lang.CharSequence r48, im.bclpbkiauv.messenger.MessageObject r49, im.bclpbkiauv.messenger.MessageObject r50, java.util.ArrayList r51, boolean r52, int r53) {
        /*
            r14 = r37
            r12 = r38
            r10 = r40
            r15 = r44
            r9 = r45
            if (r36 == 0) goto L_0x000f
            r0 = r36
            goto L_0x0013
        L_0x000f:
            im.bclpbkiauv.messenger.VideoEditedInfo r0 = createCompressionSettings(r37)
        L_0x0013:
            r8 = r0
            int r0 = (int) r12
            if (r0 != 0) goto L_0x0019
            r0 = 1
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            r5 = r0
            if (r8 == 0) goto L_0x0023
            boolean r0 = r8.roundVideo
            if (r0 == 0) goto L_0x0023
            r0 = 1
            goto L_0x0024
        L_0x0023:
            r0 = 0
        L_0x0024:
            r4 = r0
            r16 = 0
            r17 = 0
            if (r8 != 0) goto L_0x0069
            java.lang.String r0 = "mp4"
            boolean r0 = r14.endsWith(r0)
            if (r0 != 0) goto L_0x0069
            if (r4 == 0) goto L_0x003c
            r34 = r4
            r33 = r5
            r35 = r8
            goto L_0x006f
        L_0x003c:
            r3 = 0
            r6 = 0
            r18 = 0
            r0 = r43
            r1 = r37
            r2 = r37
            r7 = r4
            r4 = r6
            r33 = r5
            r5 = r38
            r34 = r7
            r7 = r50
            r35 = r8
            r8 = r48
            r9 = r51
            r10 = r49
            r11 = r18
            r12 = r52
            r13 = r53
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13)
            r8 = r33
            r13 = r34
            r12 = r35
            goto L_0x0390
        L_0x0069:
            r34 = r4
            r33 = r5
            r35 = r8
        L_0x006f:
            r8 = r37
            r0 = r37
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            r9 = r1
            r1 = 0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r4 = r9.length()
            r3.append(r4)
            java.lang.String r10 = "_"
            r3.append(r10)
            long r4 = r9.lastModified()
            r3.append(r4)
            java.lang.String r0 = r3.toString()
            java.lang.String r11 = ""
            r12 = r35
            if (r12 == 0) goto L_0x0102
            r13 = r34
            if (r13 != 0) goto L_0x00ed
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r4 = r40
            r3.append(r4)
            r3.append(r10)
            r19 = r8
            long r7 = r12.startTime
            r3.append(r7)
            r3.append(r10)
            long r7 = r12.endTime
            r3.append(r7)
            boolean r7 = r12.muted
            if (r7 == 0) goto L_0x00ca
            java.lang.String r7 = "_m"
            goto L_0x00cb
        L_0x00ca:
            r7 = r11
        L_0x00cb:
            r3.append(r7)
            java.lang.String r0 = r3.toString()
            int r3 = r12.resultWidth
            int r7 = r12.originalWidth
            if (r3 == r7) goto L_0x00f1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r10)
            int r7 = r12.resultWidth
            r3.append(r7)
            java.lang.String r0 = r3.toString()
            goto L_0x00f1
        L_0x00ed:
            r4 = r40
            r19 = r8
        L_0x00f1:
            long r7 = r12.startTime
            r20 = 0
            int r3 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r3 < 0) goto L_0x00fd
            long r7 = r12.startTime
            r20 = r7
        L_0x00fd:
            r1 = r20
            r7 = r0
            r2 = r1
            goto L_0x010a
        L_0x0102:
            r4 = r40
            r19 = r8
            r13 = r34
            r7 = r0
            r2 = r1
        L_0x010a:
            r0 = 0
            r1 = 0
            r8 = r33
            if (r8 != 0) goto L_0x0149
            if (r42 != 0) goto L_0x0149
            im.bclpbkiauv.messenger.MessagesStorage r6 = r43.getMessagesStorage()
            if (r8 != 0) goto L_0x011c
            r23 = r0
            r0 = 2
            goto L_0x0121
        L_0x011c:
            r22 = 5
            r23 = r0
            r0 = 5
        L_0x0121:
            java.lang.Object[] r6 = r6.getSentFile(r7, r0)
            if (r6 == 0) goto L_0x0146
            r0 = 0
            r22 = r6[r0]
            im.bclpbkiauv.tgnet.TLRPC$TL_document r22 = (im.bclpbkiauv.tgnet.TLRPC.TL_document) r22
            r0 = 1
            r23 = r6[r0]
            java.lang.String r23 = (java.lang.String) r23
            r24 = 0
            r0 = r8
            r1 = r22
            r33 = r2
            r2 = r37
            r3 = r24
            r4 = r33
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r0 = r22
            r1 = r23
            goto L_0x014f
        L_0x0146:
            r33 = r2
            goto L_0x014d
        L_0x0149:
            r23 = r0
            r33 = r2
        L_0x014d:
            r0 = r23
        L_0x014f:
            if (r0 != 0) goto L_0x0311
            r2 = r33
            android.graphics.Bitmap r4 = createVideoThumbnail(r14, r2)
            if (r4 != 0) goto L_0x015e
            r5 = 1
            android.graphics.Bitmap r4 = android.media.ThumbnailUtils.createVideoThumbnail(r14, r5)
        L_0x015e:
            r5 = 90
            if (r8 != 0) goto L_0x0168
            if (r42 == 0) goto L_0x0165
            goto L_0x0168
        L_0x0165:
            r6 = 320(0x140, float:4.48E-43)
            goto L_0x016a
        L_0x0168:
            r6 = 90
        L_0x016a:
            r28 = r0
            float r0 = (float) r6
            r33 = r2
            float r2 = (float) r6
            if (r6 <= r5) goto L_0x0175
            r3 = 80
            goto L_0x0177
        L_0x0175:
            r3 = 55
        L_0x0177:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r0 = im.bclpbkiauv.messenger.ImageLoader.scaleAndSaveImage(r4, r0, r2, r3, r8)
            if (r4 == 0) goto L_0x024b
            if (r0 == 0) goto L_0x024b
            if (r13 == 0) goto L_0x0247
            r2 = 21
            if (r8 == 0) goto L_0x01e6
            r23 = 7
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 >= r2) goto L_0x018e
            r24 = 0
            goto L_0x0190
        L_0x018e:
            r24 = 1
        L_0x0190:
            int r25 = r4.getWidth()
            int r26 = r4.getHeight()
            int r27 = r4.getRowBytes()
            r22 = r4
            im.bclpbkiauv.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r0.location
            r29 = r6
            long r5 = r3.volume_id
            r2.append(r5)
            r2.append(r10)
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r0.location
            int r3 = r3.local_id
            r2.append(r3)
            java.lang.String r3 = "@%d_%d_b2"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 / r6
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r3[r6] = r5
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 / r6
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 1
            r3[r6] = r5
            java.lang.String r17 = java.lang.String.format(r2, r3)
            goto L_0x024d
        L_0x01e6:
            r29 = r6
            r23 = 3
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 >= r2) goto L_0x01f1
            r24 = 0
            goto L_0x01f3
        L_0x01f1:
            r24 = 1
        L_0x01f3:
            int r25 = r4.getWidth()
            int r26 = r4.getHeight()
            int r27 = r4.getRowBytes()
            r22 = r4
            im.bclpbkiauv.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r0.location
            long r5 = r3.volume_id
            r2.append(r5)
            r2.append(r10)
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r0.location
            int r3 = r3.local_id
            r2.append(r3)
            java.lang.String r3 = "@%d_%d_b"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 / r6
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r3[r6] = r5
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 / r6
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 1
            r3[r6] = r5
            java.lang.String r17 = java.lang.String.format(r2, r3)
            goto L_0x024d
        L_0x0247:
            r29 = r6
            r4 = 0
            goto L_0x024d
        L_0x024b:
            r29 = r6
        L_0x024d:
            im.bclpbkiauv.tgnet.TLRPC$TL_document r2 = new im.bclpbkiauv.tgnet.TLRPC$TL_document
            r2.<init>()
            if (r0 == 0) goto L_0x025f
            java.util.ArrayList r3 = r2.thumbs
            r3.add(r0)
            int r3 = r2.flags
            r5 = 1
            r3 = r3 | r5
            r2.flags = r3
        L_0x025f:
            r3 = 0
            byte[] r5 = new byte[r3]
            r2.file_reference = r5
            java.lang.String r5 = "video/mp4"
            r2.mime_type = r5
            im.bclpbkiauv.messenger.UserConfig r5 = r43.getUserConfig()
            r5.saveConfig(r3)
            if (r8 == 0) goto L_0x029c
            r3 = 32
            long r5 = r38 >> r3
            int r3 = (int) r5
            im.bclpbkiauv.messenger.MessagesController r5 = r43.getMessagesController()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r6)
            if (r5 != 0) goto L_0x0286
            return
        L_0x0286:
            int r6 = r5.layer
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.getPeerLayerVersion(r6)
            r10 = 66
            if (r6 < r10) goto L_0x0296
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo
            r6.<init>()
            goto L_0x029b
        L_0x0296:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r6 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r6.<init>()
        L_0x029b:
            goto L_0x02a5
        L_0x029c:
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo r3 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            r6 = r3
            r3 = 1
            r6.supports_streaming = r3
        L_0x02a5:
            r6.round_message = r13
            java.util.ArrayList r3 = r2.attributes
            r3.add(r6)
            if (r12 == 0) goto L_0x02f9
            boolean r3 = r12.needConvert()
            if (r3 == 0) goto L_0x02f9
            boolean r3 = r12.muted
            if (r3 == 0) goto L_0x02cf
            java.util.ArrayList r3 = r2.attributes
            im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new im.bclpbkiauv.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r3.add(r5)
            fillVideoAttribute(r14, r6, r12)
            int r3 = r6.w
            r12.originalWidth = r3
            int r3 = r6.h
            r12.originalHeight = r3
            r5 = r4
            goto L_0x02d7
        L_0x02cf:
            r20 = 1000(0x3e8, double:4.94E-321)
            r5 = r4
            long r3 = r40 / r20
            int r4 = (int) r3
            r6.duration = r4
        L_0x02d7:
            int r3 = r12.rotationValue
            r4 = 90
            if (r3 == r4) goto L_0x02ed
            int r3 = r12.rotationValue
            r4 = 270(0x10e, float:3.78E-43)
            if (r3 != r4) goto L_0x02e6
            r3 = r45
            goto L_0x02ef
        L_0x02e6:
            r3 = r45
            r6.w = r3
            r6.h = r15
            goto L_0x02f3
        L_0x02ed:
            r3 = r45
        L_0x02ef:
            r6.w = r15
            r6.h = r3
        L_0x02f3:
            r3 = r46
            int r10 = (int) r3
            r2.size = r10
            goto L_0x030d
        L_0x02f9:
            r5 = r4
            r3 = r46
            boolean r10 = r9.exists()
            if (r10 == 0) goto L_0x0309
            long r3 = r9.length()
            int r4 = (int) r3
            r2.size = r4
        L_0x0309:
            r3 = 0
            fillVideoAttribute(r14, r6, r3)
        L_0x030d:
            r0 = r2
            r2 = r17
            goto L_0x0317
        L_0x0311:
            r28 = r0
            r5 = r16
            r2 = r17
        L_0x0317:
            if (r12 == 0) goto L_0x034b
            boolean r3 = r12.needConvert()
            if (r3 == 0) goto L_0x034b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "-2147483648_"
            r3.append(r4)
            int r4 = im.bclpbkiauv.messenger.SharedConfig.getLastLocalId()
            r3.append(r4)
            java.lang.String r4 = ".mp4"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.io.File r4 = new java.io.File
            r6 = 4
            java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getDirectory(r6)
            r4.<init>(r6, r3)
            im.bclpbkiauv.messenger.SharedConfig.saveConfig()
            java.lang.String r6 = r4.getAbsolutePath()
            goto L_0x034d
        L_0x034b:
            r6 = r19
        L_0x034d:
            r21 = r0
            r24 = r1
            r3 = r7
            r22 = r6
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            r16 = r5
            r17 = r2
            if (r48 == 0) goto L_0x0366
            java.lang.String r10 = r48.toString()
            r28 = r10
            goto L_0x0368
        L_0x0366:
            r28 = r11
        L_0x0368:
            if (r7 == 0) goto L_0x036f
            java.lang.String r10 = "originalPath"
            r4.put(r10, r7)
        L_0x036f:
            im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$8VR0TLbP78eBuzA3s0rqqkPvAkE r10 = new im.bclpbkiauv.messenger.-$$Lambda$SendMessagesHelper$8VR0TLbP78eBuzA3s0rqqkPvAkE
            r15 = r10
            r18 = r49
            r19 = r43
            r20 = r12
            r23 = r4
            r25 = r38
            r27 = r50
            r29 = r51
            r30 = r52
            r31 = r53
            r32 = r42
            r15.<init>(r16, r17, r18, r19, r20, r21, r22, r23, r24, r25, r27, r28, r29, r30, r31, r32)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r10)
            r17 = r2
            r16 = r5
        L_0x0390:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.SendMessagesHelper.lambda$prepareSendingVideo$65(im.bclpbkiauv.messenger.VideoEditedInfo, java.lang.String, long, long, int, im.bclpbkiauv.messenger.AccountInstance, int, int, long, java.lang.CharSequence, im.bclpbkiauv.messenger.MessageObject, im.bclpbkiauv.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$64(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities, boolean notify, int scheduleDate, int ttl) {
        Bitmap bitmap = thumbFinal;
        String str = thumbKeyFinal;
        if (!(bitmap == null || str == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap), str);
        }
        if (editingMessageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(editingMessageObject, (TLRPC.TL_photo) null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, captionFinal, entities, (TLRPC.ReplyMarkup) null, params, notify, scheduleDate, ttl, parentFinal);
        }
    }
}
