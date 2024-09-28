package im.bclpbkiauv.messenger;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.gms.location.LocationRequest;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.sqlite.SQLiteCursor;
import im.bclpbkiauv.sqlite.SQLiteDatabase;
import im.bclpbkiauv.sqlite.SQLitePreparedStatement;
import im.bclpbkiauv.tgnet.NativeByteBuffer;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static final int BACKGROUD_UPDATE_TIME = 30000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static volatile LocationController[] Instance = new LocationController[3];
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 1000;
    private static HashMap<LocationFetchCallback, Runnable> callbacks = new HashMap<>();
    private BDLocationListener bdLocationListener;
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyChats = new ArrayList<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyUsers = new ArrayList<>();
    /* access modifiers changed from: private */
    public BDLocation lastKnownLocation;
    private boolean lastLocationByBaiduMaps;
    /* access modifiers changed from: private */
    public long lastLocationSendTime;
    private long lastLocationStartTime;
    private LocationRequest locationRequest;
    private boolean locationSentSinceLastBaiduMapUpdate = true;
    public LongSparseArray<ArrayList<TLRPC.Message>> locationsCache = new LongSparseArray<>();
    private boolean lookingForPeopleNearby;
    private LocationClient mLocClient;
    private SparseIntArray requests = new SparseIntArray();
    private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray<>();
    public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean started;

    public interface LocationFetchCallback {
        void onLocationAddressAvailable(String str, String str2, BDLocation bDLocation);
    }

    public static class SharingLocationInfo {
        public long did;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int stopTime;
    }

    public static LocationController getInstance(int num) {
        LocationController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (LocationController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    LocationController[] locationControllerArr = Instance;
                    LocationController locationController = new LocationController(num);
                    localInstance = locationController;
                    locationControllerArr[num] = locationController;
                }
            }
        }
        return localInstance;
    }

    public class BDLocationListener extends BDAbstractLocationListener {
        public BDLocationListener() {
        }

        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                if (LocationController.this.lastKnownLocation != null) {
                    LatLng newLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                    LatLng lastLatLng = new LatLng(LocationController.this.lastKnownLocation.getLatitude(), LocationController.this.lastKnownLocation.getLongitude());
                    if (!LocationController.this.started && DistanceUtil.getDistance(newLatLng, lastLatLng) > 20.0d) {
                        LocationController.this.setLastKnownLocation(bdLocation);
                        long unused = LocationController.this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        return;
                    }
                    return;
                }
                LocationController.this.setLastKnownLocation(bdLocation);
            }
        }
    }

    public LocationController(int instance) {
        super(instance);
        initLocationClient();
        LocationRequest locationRequest2 = new LocationRequest();
        this.locationRequest = locationRequest2;
        locationRequest2.setPriority(100);
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocationController.this.lambda$new$0$LocationController();
            }
        });
        loadSharingLocations();
    }

    public /* synthetic */ void lambda$new$0$LocationController() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    private void initLocationClient() {
        LocationClient locationClient = new LocationClient(ApplicationLoader.applicationContext);
        this.mLocClient = locationClient;
        BDLocationListener bDLocationListener = new BDLocationListener();
        this.bdLocationListener = bDLocationListener;
        locationClient.registerLocationListener((BDAbstractLocationListener) bDLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType(CoordinateType.GCJ02);
        option.setScanSpan(1000);
        option.setIgnoreKillProcess(true);
        option.setIsNeedAddress(true);
        this.mLocClient.setLocOption(option);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ArrayList<TLRPC.Message> messages;
        ArrayList<TLRPC.Message> messages2;
        int i = id;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue()) {
                long did = args[0].longValue();
                if (isSharingLocation(did) && (messages2 = this.locationsCache.get(did)) != null) {
                    ArrayList<MessageObject> arr = args[1];
                    boolean added = false;
                    for (int a = 0; a < arr.size(); a++) {
                        MessageObject messageObject = arr.get(a);
                        if (messageObject.isLiveLocation()) {
                            added = true;
                            boolean replaced = false;
                            int b = 0;
                            while (true) {
                                if (b >= messages2.size()) {
                                    break;
                                } else if (messages2.get(b).from_id == messageObject.messageOwner.from_id) {
                                    replaced = true;
                                    messages2.set(b, messageObject.messageOwner);
                                    break;
                                } else {
                                    b++;
                                }
                            }
                            if (!replaced) {
                                messages2.add(messageObject.messageOwner);
                            }
                        }
                    }
                    if (added) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!args[2].booleanValue() && !this.sharingLocationsUI.isEmpty()) {
                ArrayList<Integer> markAsDeletedMessages = args[0];
                int channelId = args[1].intValue();
                ArrayList<Long> toRemove = null;
                for (int a2 = 0; a2 < this.sharingLocationsUI.size(); a2++) {
                    SharingLocationInfo info = this.sharingLocationsUI.get(a2);
                    if (channelId == (info.messageObject != null ? info.messageObject.getChannelId() : 0) && markAsDeletedMessages.contains(Integer.valueOf(info.mid))) {
                        if (toRemove == null) {
                            toRemove = new ArrayList<>();
                        }
                        toRemove.add(Long.valueOf(info.did));
                    }
                }
                if (toRemove != null) {
                    for (int a3 = 0; a3 < toRemove.size(); a3++) {
                        removeSharingLocation(toRemove.get(a3).longValue());
                    }
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long did2 = args[0].longValue();
            if (isSharingLocation(did2) && (messages = this.locationsCache.get(did2)) != null) {
                boolean updated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (int a4 = 0; a4 < messageObjects.size(); a4++) {
                    MessageObject messageObject2 = messageObjects.get(a4);
                    int b2 = 0;
                    while (true) {
                        if (b2 >= messages.size()) {
                            break;
                        } else if (messages.get(b2).from_id == messageObject2.messageOwner.from_id) {
                            if (!messageObject2.isLiveLocation()) {
                                messages.remove(b2);
                            } else {
                                messages.set(b2, messageObject2.messageOwner);
                            }
                            updated = true;
                        } else {
                            b2++;
                        }
                    }
                }
                if (updated) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did2), Integer.valueOf(this.currentAccount));
                }
            }
        }
    }

    public void startFusedLocationRequest(boolean permissionsGranted) {
        Utilities.stageQueue.postRunnable(new Runnable(permissionsGranted) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$startFusedLocationRequest$1$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startFusedLocationRequest$1$LocationController(boolean permissionsGranted) {
        if (!this.lookingForPeopleNearby && this.sharingLocations.isEmpty()) {
            return;
        }
        if (permissionsGranted) {
            setLastKnownLocation(this.mLocClient.getLastKnownLocation());
        } else {
            start();
        }
    }

    private void broadcastLastKnownLocation() {
        if (this.lastKnownLocation != null) {
            if (this.requests.size() != 0) {
                for (int a = 0; a < this.requests.size(); a++) {
                    getConnectionsManager().cancelRequest(this.requests.keyAt(a), false);
                }
                this.requests.clear();
            }
            int date = getConnectionsManager().getCurrentTime();
            float[] result = new float[1];
            for (int a2 = 0; a2 < this.sharingLocations.size(); a2++) {
                SharingLocationInfo info = this.sharingLocations.get(a2);
                if (!(info.messageObject.messageOwner.media == null || info.messageObject.messageOwner.media.geo == null)) {
                    int messageDate = info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date;
                    TLRPC.GeoPoint point = info.messageObject.messageOwner.media.geo;
                    if (Math.abs(date - messageDate) < 10) {
                        TLRPC.GeoPoint geoPoint = point;
                        Location.distanceBetween(point.lat, point._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), result);
                        if (result[0] < 1.0f) {
                        }
                    }
                }
                TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
                req.peer = getMessagesController().getInputPeer((int) info.did);
                req.id = info.mid;
                req.flags |= 16384;
                req.media = new TLRPC.TL_inputMediaGeoLive();
                req.media.stopped = false;
                req.media.geo_point = new TLRPC.TL_inputGeoPoint();
                req.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                req.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                int[] reqId = {getConnectionsManager().sendRequest(req, new RequestDelegate(info, reqId) {
                    private final /* synthetic */ LocationController.SharingLocationInfo f$1;
                    private final /* synthetic */ int[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LocationController.this.lambda$broadcastLastKnownLocation$3$LocationController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                })};
                this.requests.put(reqId[0], 0);
            }
            getConnectionsManager().resumeNetworkMaybe();
            stop();
        }
    }

    public /* synthetic */ void lambda$broadcastLastKnownLocation$3$LocationController(SharingLocationInfo info, int[] reqId, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            boolean updated = false;
            for (int a1 = 0; a1 < updates.updates.size(); a1++) {
                TLRPC.Update update = updates.updates.get(a1);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TLRPC.TL_updateEditMessage) update).message;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TLRPC.TL_updateEditChannelMessage) update).message;
                }
            }
            if (updated) {
                saveSharingLocation(info, 0);
            }
            getMessagesController().processUpdates(updates, false);
        } else if (error.text.equals("MESSAGE_ID_INVALID")) {
            this.sharingLocations.remove(info);
            this.sharingLocationsMap.remove(info.did);
            saveSharingLocation(info, 1);
            this.requests.delete(reqId[0]);
            AndroidUtilities.runOnUIThread(new Runnable(info) {
                private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$2$LocationController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$LocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    /* access modifiers changed from: protected */
    public void update() {
        if (!this.sharingLocations.isEmpty()) {
            int a = 0;
            while (a < this.sharingLocations.size()) {
                SharingLocationInfo info = this.sharingLocations.get(a);
                if (info.stopTime <= getConnectionsManager().getCurrentTime()) {
                    this.sharingLocations.remove(a);
                    this.sharingLocationsMap.remove(info.did);
                    saveSharingLocation(info, 1);
                    AndroidUtilities.runOnUIThread(new Runnable(info) {
                        private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationController.this.lambda$update$4$LocationController(this.f$1);
                        }
                    });
                    a--;
                }
                a++;
            }
            if (this.started == 0) {
                if (Math.abs(this.lastLocationSendTime - SystemClock.uptimeMillis()) > 30000) {
                    this.lastLocationStartTime = SystemClock.uptimeMillis();
                    start();
                }
            } else if (Math.abs(this.lastLocationStartTime - SystemClock.uptimeMillis()) > OkHttpUtils.DEFAULT_MILLISECONDS) {
                this.lastLocationSendTime = SystemClock.uptimeMillis();
                this.locationSentSinceLastBaiduMapUpdate = true;
                broadcastLastKnownLocation();
            }
        }
    }

    public /* synthetic */ void lambda$update$4$LocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void cleanup() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        this.locationsCache.clear();
        this.cacheRequests.clear();
        this.cachedNearbyUsers.clear();
        this.cachedNearbyChats.clear();
        stopService();
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$cleanup$5$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$5$LocationController() {
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        setLastKnownLocation((BDLocation) null);
        stop();
    }

    /* access modifiers changed from: private */
    public void setLastKnownLocation(BDLocation location) {
        this.lastKnownLocation = location;
        if (location != null) {
            AndroidUtilities.runOnUIThread($$Lambda$LocationController$n0Bacs9RivvwlnqeEHaHLj8vNg.INSTANCE);
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TLRPC.TL_peerLocated> u, ArrayList<TLRPC.TL_peerLocated> c) {
        this.cachedNearbyUsers = new ArrayList<>(u);
        this.cachedNearbyChats = new ArrayList<>(c);
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    /* access modifiers changed from: protected */
    public void addSharingLocation(long did, int mid, int period, TLRPC.Message message) {
        SharingLocationInfo info = new SharingLocationInfo();
        info.did = did;
        info.mid = mid;
        info.period = period;
        info.messageObject = new MessageObject(this.currentAccount, message, false);
        info.stopTime = getConnectionsManager().getCurrentTime() + period;
        SharingLocationInfo old = this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.put(did, info);
        if (old != null) {
            this.sharingLocations.remove(old);
        }
        this.sharingLocations.add(info);
        saveSharingLocation(info, 0);
        this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        AndroidUtilities.runOnUIThread(new Runnable(old, info) {
            private final /* synthetic */ LocationController.SharingLocationInfo f$1;
            private final /* synthetic */ LocationController.SharingLocationInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationController.this.lambda$addSharingLocation$7$LocationController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$addSharingLocation$7$LocationController(SharingLocationInfo old, SharingLocationInfo info) {
        if (old != null) {
            this.sharingLocationsUI.remove(old);
        }
        this.sharingLocationsUI.add(info);
        this.sharingLocationsMapUI.put(info.did, info);
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public boolean isSharingLocation(long did) {
        return this.sharingLocationsMapUI.indexOfKey(did) >= 0;
    }

    public SharingLocationInfo getSharingLocationInfo(long did) {
        return this.sharingLocationsMapUI.get(did);
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$loadSharingLocations$11$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$loadSharingLocations$11$LocationController() {
        ArrayList<SharingLocationInfo> result = new ArrayList<>();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        try {
            ArrayList<Integer> usersToLoad = new ArrayList<>();
            ArrayList<Integer> chatsToLoad = new ArrayList<>();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
            while (cursor.next()) {
                SharingLocationInfo info = new SharingLocationInfo();
                info.did = cursor.longValue(0);
                info.mid = cursor.intValue(1);
                info.stopTime = cursor.intValue(2);
                info.period = cursor.intValue(3);
                NativeByteBuffer data = cursor.byteBufferValue(4);
                if (data != null) {
                    info.messageObject = new MessageObject(this.currentAccount, TLRPC.Message.TLdeserialize(data, data.readInt32(false), false), false);
                    MessagesStorage.addUsersAndChatsFromMessage(info.messageObject.messageOwner, usersToLoad, chatsToLoad);
                    data.reuse();
                }
                result.add(info);
                int lower_id = (int) info.did;
                int i = (int) (info.did >> 32);
                if (lower_id != 0) {
                    if (lower_id < 0) {
                        if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                        }
                    } else if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                        usersToLoad.add(Integer.valueOf(lower_id));
                    }
                }
            }
            cursor.dispose();
            if (!chatsToLoad.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!result.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(users, chats, result) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocationController.this.lambda$null$10$LocationController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$LocationController(ArrayList users, ArrayList chats, ArrayList result) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        Utilities.stageQueue.postRunnable(new Runnable(result) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$null$9$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$LocationController(ArrayList result) {
        this.sharingLocations.addAll(result);
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            this.sharingLocationsMap.put(info.did, info);
        }
        AndroidUtilities.runOnUIThread(new Runnable(result) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$null$8$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$LocationController(ArrayList result) {
        this.sharingLocationsUI.addAll(result);
        for (int a = 0; a < result.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) result.get(a);
            this.sharingLocationsMapUI.put(info.did, info);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo info, int remove) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(remove, info) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ LocationController.SharingLocationInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationController.this.lambda$saveSharingLocation$12$LocationController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$saveSharingLocation$12$LocationController(int remove, SharingLocationInfo info) {
        if (remove == 2) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (remove == 1) {
            if (info != null) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                database.executeFast("DELETE FROM sharing_locations WHERE uid = " + info.did).stepThis().dispose();
            }
        } else if (info != null) {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(info.messageObject.messageOwner.getObjectSize());
            info.messageObject.messageOwner.serializeToStream(data);
            state.bindLong(1, info.did);
            state.bindInteger(2, info.mid);
            state.bindInteger(3, info.stopTime);
            state.bindInteger(4, info.period);
            state.bindByteBuffer(5, data);
            state.step();
            state.dispose();
            data.reuse();
        }
    }

    public void removeSharingLocation(long did) {
        Utilities.stageQueue.postRunnable(new Runnable(did) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$removeSharingLocation$15$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeSharingLocation$15$LocationController(long did) {
        SharingLocationInfo info = this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.remove(did);
        if (info != null) {
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer((int) info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$null$13$LocationController(tLObject, tL_error);
                }
            });
            this.sharingLocations.remove(info);
            saveSharingLocation(info, 1);
            AndroidUtilities.runOnUIThread(new Runnable(info) {
                private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$14$LocationController(this.f$1);
                }
            });
            if (this.sharingLocations.isEmpty()) {
                stop();
            }
        }
    }

    public /* synthetic */ void lambda$null$13$LocationController(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    public /* synthetic */ void lambda$null$14$LocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void startService() {
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void stopService() {
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
    }

    public void removeAllLocationSharings() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$removeAllLocationSharings$18$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$removeAllLocationSharings$18$LocationController() {
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer((int) info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$null$16$LocationController(tLObject, tL_error);
                }
            });
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation((SharingLocationInfo) null, 2);
        stop();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocationController.this.lambda$null$17$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$null$16$LocationController(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    public /* synthetic */ void lambda$null$17$LocationController() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setBaiduMapLocation(BDLocation location, boolean first) {
        if (location != null) {
            this.lastLocationByBaiduMaps = true;
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng lastLatLng = new LatLng(this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude());
            if (first || (this.lastKnownLocation != null && DistanceUtil.getDistance(newLatLng, lastLatLng) >= 20.0d)) {
                this.lastLocationSendTime = SystemClock.uptimeMillis() - 30000;
                this.locationSentSinceLastBaiduMapUpdate = false;
            } else if (this.locationSentSinceLastBaiduMapUpdate) {
                this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + 20000;
                this.locationSentSinceLastBaiduMapUpdate = false;
            }
            setLastKnownLocation(location);
        }
    }

    private void start() {
        if (!this.started) {
            this.lastLocationStartTime = SystemClock.uptimeMillis();
            this.started = true;
            this.mLocClient.start();
        }
    }

    private void stop() {
        if (!this.lookingForPeopleNearby) {
            this.started = false;
            this.mLocClient.stop();
        }
    }

    public void startLocationLookupForPeopleNearby(boolean stop) {
        Utilities.stageQueue.postRunnable(new Runnable(stop) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$startLocationLookupForPeopleNearby$19$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startLocationLookupForPeopleNearby$19$LocationController(boolean stop) {
        boolean z = !stop;
        this.lookingForPeopleNearby = z;
        if (z) {
            start();
        } else if (this.sharingLocations.isEmpty()) {
            stop();
        }
    }

    public BDLocation getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void loadLiveLocations(long did) {
        if (this.cacheRequests.indexOfKey(did) < 0) {
            this.cacheRequests.put(did, true);
            TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
            req.peer = getMessagesController().getInputPeer((int) did);
            req.limit = 100;
            getConnectionsManager().sendRequest(req, new RequestDelegate(did) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$loadLiveLocations$21$LocationController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadLiveLocations$21$LocationController(long did, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(did, response) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    LocationController.this.lambda$null$20$LocationController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$20$LocationController(long did, TLObject response) {
        this.cacheRequests.delete(did);
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        int a = 0;
        while (a < res.messages.size()) {
            if (!(res.messages.get(a).media instanceof TLRPC.TL_messageMediaGeoLive)) {
                res.messages.remove(a);
                a--;
            }
            a++;
        }
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        getMessagesController().putUsers(res.users, false);
        getMessagesController().putChats(res.chats, false);
        this.locationsCache.put(did, res.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
    }

    public static int getLocationsCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            count += getInstance(a).sharingLocationsUI.size();
        }
        return count;
    }

    public static void fetchLocationAddress(BDLocation location, LocationFetchCallback callback) {
        if (callback != null) {
            Runnable fetchLocationRunnable = callbacks.get(callback);
            if (fetchLocationRunnable != null) {
                Utilities.globalQueue.cancelRunnable(fetchLocationRunnable);
                callbacks.remove(callback);
            }
            if (location != null) {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                Runnable fetchLocationRunnable2 = new Runnable(callback) {
                    private final /* synthetic */ LocationController.LocationFetchCallback f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationController.lambda$fetchLocationAddress$23(BDLocation.this, this.f$1);
                    }
                };
                dispatchQueue.postRunnable(fetchLocationRunnable2, 300);
                callbacks.put(callback, fetchLocationRunnable2);
            } else if (callback != null) {
                callback.onLocationAddressAvailable((String) null, (String) null, (BDLocation) null);
            }
        }
    }

    static /* synthetic */ void lambda$fetchLocationAddress$23(BDLocation location, LocationFetchCallback callback) {
        String name;
        String displayName;
        try {
            List<Address> addresses = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getSystemDefaultLocale()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                boolean hasAny = false;
                StringBuilder nameBuilder = new StringBuilder();
                StringBuilder displayNameBuilder = new StringBuilder();
                String arg = address.getSubThoroughfare();
                if (!TextUtils.isEmpty(arg)) {
                    nameBuilder.append(arg);
                    hasAny = true;
                }
                String arg2 = address.getThoroughfare();
                if (!TextUtils.isEmpty(arg2)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg2);
                    hasAny = true;
                }
                if (!hasAny) {
                    String arg3 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg3)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg3);
                    }
                    String arg4 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg4)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg4);
                    }
                }
                String arg5 = address.getLocality();
                if (!TextUtils.isEmpty(arg5)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg5);
                }
                String arg6 = address.getCountryName();
                if (!TextUtils.isEmpty(arg6)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg6);
                }
                String arg7 = address.getCountryName();
                if (!TextUtils.isEmpty(arg7)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg7);
                }
                String arg8 = address.getLocality();
                if (!TextUtils.isEmpty(arg8)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg8);
                }
                if (!hasAny) {
                    String arg9 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg9)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg9);
                    }
                    String arg10 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg10)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg10);
                    }
                }
                name = nameBuilder.toString();
                displayName = displayNameBuilder.toString();
            } else {
                displayName = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
                String str = displayName;
                name = displayName;
            }
        } catch (Exception e) {
            displayName = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
            String str2 = displayName;
            name = displayName;
        }
        AndroidUtilities.runOnUIThread(new Runnable(name, displayName, location) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ BDLocation f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                LocationController.lambda$null$22(LocationController.LocationFetchCallback.this, this.f$1, this.f$2, this.f$3);
            }
        });
    }

    static /* synthetic */ void lambda$null$22(LocationFetchCallback callback, String nameFinal, String displayNameFinal, BDLocation location) {
        callbacks.remove(callback);
        if (callback != null) {
            callback.onLocationAddressAvailable(nameFinal, displayNameFinal, location);
        }
    }
}
