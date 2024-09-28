package im.bclpbkiauv.messenger;

import android.os.SystemClock;
import im.bclpbkiauv.messenger.FileRefController;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private long lastCleanupTime = SystemClock.uptimeMillis();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC.TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();

    private class Requester {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public boolean completed;
        /* access modifiers changed from: private */
        public TLRPC.InputFileLocation location;
        /* access modifiers changed from: private */
        public String locationKey;

        private Requester() {
        }
    }

    private class CachedResult {
        /* access modifiers changed from: private */
        public long firstQueryTime;
        /* access modifiers changed from: private */
        public long lastQueryTime;
        /* access modifiers changed from: private */
        public TLObject response;

        private CachedResult() {
        }
    }

    public static FileRefController getInstance(int num) {
        FileRefController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileRefController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController = new FileRefController(num);
                    localInstance = fileRefController;
                    fileRefControllerArr[num] = fileRefController;
                }
            }
        }
        return localInstance;
    }

    public FileRefController(int instance) {
        super(instance);
    }

    public static String getKeyForParentObject(Object parentObject) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            int channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (parentObject instanceof TLRPC.Message) {
            TLRPC.Message message = (TLRPC.Message) parentObject;
            int channelId2 = message.to_id != null ? message.to_id.channel_id : 0;
            return "message" + message.id + "_" + channelId2;
        } else if (parentObject instanceof TLRPC.WebPage) {
            return "webpage" + ((TLRPC.WebPage) parentObject).id;
        } else if (parentObject instanceof TLRPC.User) {
            return "user" + ((TLRPC.User) parentObject).id;
        } else if (parentObject instanceof TLRPC.Chat) {
            return "chat" + ((TLRPC.Chat) parentObject).id;
        } else if (parentObject instanceof String) {
            return "str" + ((String) parentObject);
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            return "set" + ((TLRPC.TL_messages_stickerSet) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            return "set" + ((TLRPC.StickerSetCovered) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            return "set" + ((TLRPC.InputStickerSet) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            return "wallpaper" + ((TLRPC.TL_wallPaper) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_theme) {
            return "theme" + ((TLRPC.TL_theme) parentObject).id;
        } else if (parentObject == null) {
            return null;
        } else {
            return "" + parentObject;
        }
    }

    public void requestReference(Object parentObject, Object... args) {
        TLRPC.InputFileLocation location;
        String locationKey;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start loading request reference for parent = " + parentObject + " args = " + args[0]);
        }
        if (args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_inputSingleMedia req = args[0];
            if (req.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument = (TLRPC.TL_inputMediaDocument) req.media;
                locationKey = "file_" + mediaDocument.id.id;
                location = new TLRPC.TL_inputDocumentFileLocation();
                location.id = mediaDocument.id.id;
            } else if (req.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto = (TLRPC.TL_inputMediaPhoto) req.media;
                location = new TLRPC.TL_inputPhotoFileLocation();
                location.id = mediaPhoto.id.id;
                locationKey = "photo_" + mediaPhoto.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TLRPC.TL_messages_sendMultiMedia) {
            TLRPC.TL_messages_sendMultiMedia req2 = args[0];
            ArrayList<Object> parentObjects = (ArrayList) parentObject;
            this.multiMediaCache.put(req2, args);
            int size = req2.multi_media.size();
            for (int a = 0; a < size; a++) {
                TLRPC.TL_inputSingleMedia media = req2.multi_media.get(a);
                Object parentObject2 = parentObjects.get(a);
                if (parentObject2 != null) {
                    requestReference(parentObject2, media, req2);
                }
            }
            return;
        } else if (args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia req3 = args[0];
            if (req3.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument2 = (TLRPC.TL_inputMediaDocument) req3.media;
                locationKey = "file_" + mediaDocument2.id.id;
                location = new TLRPC.TL_inputDocumentFileLocation();
                location.id = mediaDocument2.id.id;
            } else if (req3.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto2 = (TLRPC.TL_inputMediaPhoto) req3.media;
                location = new TLRPC.TL_inputPhotoFileLocation();
                location.id = mediaPhoto2.id.id;
                locationKey = "photo_" + mediaPhoto2.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage req4 = args[0];
            if (req4.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument3 = (TLRPC.TL_inputMediaDocument) req4.media;
                locationKey = "file_" + mediaDocument3.id.id;
                location = new TLRPC.TL_inputDocumentFileLocation();
                location.id = mediaDocument3.id.id;
            } else if (req4.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto3 = (TLRPC.TL_inputMediaPhoto) req4.media;
                location = new TLRPC.TL_inputPhotoFileLocation();
                location.id = mediaPhoto3.id.id;
                locationKey = "photo_" + mediaPhoto3.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif req5 = args[0];
            locationKey = "file_" + req5.id.id;
            location = new TLRPC.TL_inputDocumentFileLocation();
            location.id = req5.id.id;
        } else if (args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker req6 = args[0];
            locationKey = "file_" + req6.id.id;
            location = new TLRPC.TL_inputDocumentFileLocation();
            location.id = req6.id.id;
        } else if (args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker req7 = args[0];
            locationKey = "file_" + req7.id.id;
            location = new TLRPC.TL_inputDocumentFileLocation();
            location.id = req7.id.id;
        } else if (args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers req8 = args[0];
            if (req8.media instanceof TLRPC.TL_inputStickeredMediaDocument) {
                TLRPC.TL_inputStickeredMediaDocument mediaDocument4 = (TLRPC.TL_inputStickeredMediaDocument) req8.media;
                locationKey = "file_" + mediaDocument4.id.id;
                location = new TLRPC.TL_inputDocumentFileLocation();
                location.id = mediaDocument4.id.id;
            } else if (req8.media instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                TLRPC.TL_inputStickeredMediaPhoto mediaPhoto4 = (TLRPC.TL_inputStickeredMediaPhoto) req8.media;
                location = new TLRPC.TL_inputPhotoFileLocation();
                location.id = mediaPhoto4.id.id;
                locationKey = "photo_" + mediaPhoto4.id.id;
            } else {
                sendErrorToObject(args, 0);
                return;
            }
        } else if (args[0] instanceof TLRPC.TL_inputFileLocation) {
            location = (TLRPC.TL_inputFileLocation) args[0];
            locationKey = "loc_" + location.local_id + "_" + location.volume_id;
        } else if (args[0] instanceof TLRPC.TL_inputDocumentFileLocation) {
            location = (TLRPC.TL_inputDocumentFileLocation) args[0];
            locationKey = "file_" + location.id;
        } else if (args[0] instanceof TLRPC.TL_inputPhotoFileLocation) {
            location = (TLRPC.TL_inputPhotoFileLocation) args[0];
            locationKey = "photo_" + location.id;
        } else {
            sendErrorToObject(args, 0);
            return;
        }
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            if (messageObject.getRealId() < 0 && messageObject.messageOwner.media.webpage != null) {
                parentObject = messageObject.messageOwner.media.webpage;
            }
        }
        String parentKey = getKeyForParentObject(parentObject);
        if (parentKey == null) {
            sendErrorToObject(args, 0);
            return;
        }
        Requester requester = new Requester();
        Object[] unused = requester.args = args;
        TLRPC.InputFileLocation unused2 = requester.location = location;
        String unused3 = requester.locationKey = locationKey;
        int added = 0;
        ArrayList<Requester> arrayList = this.locationRequester.get(locationKey);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.locationRequester.put(locationKey, arrayList);
            added = 0 + 1;
        }
        arrayList.add(requester);
        ArrayList<Requester> arrayList2 = this.parentRequester.get(parentKey);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            this.parentRequester.put(parentKey, arrayList2);
            added++;
        }
        arrayList2.add(requester);
        if (added == 2) {
            cleanupCache();
            CachedResult cachedResult = getCachedResponse(locationKey);
            if (cachedResult == null) {
                CachedResult cachedResult2 = getCachedResponse(parentKey);
                if (cachedResult2 != null) {
                    if (!onRequestComplete(locationKey, parentKey, cachedResult2.response, false)) {
                        this.responseCache.remove(parentKey);
                    } else {
                        return;
                    }
                }
            } else if (!onRequestComplete(locationKey, parentKey, cachedResult.response, false)) {
                this.responseCache.remove(locationKey);
            } else {
                return;
            }
            requestReferenceFromServer(parentObject, locationKey, parentKey, args);
        }
    }

    private void requestReferenceFromServer(Object parentObject, String locationKey, String parentKey, Object[] args) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            int channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages req = new TLRPC.TL_messages_getScheduledMessages();
                req.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
                req.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$0$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages req2 = new TLRPC.TL_channels_getMessages();
                req2.channel = getMessagesController().getInputChannel(channelId);
                req2.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req2, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$1$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else {
                TLRPC.TL_messages_getMessages req3 = new TLRPC.TL_messages_getMessages();
                req3.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req3, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$2$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) parentObject;
            TLRPC.TL_account_getWallPaper req4 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaper inputWallPaper = new TLRPC.TL_inputWallPaper();
            inputWallPaper.id = wallPaper.id;
            inputWallPaper.access_hash = wallPaper.access_hash;
            req4.wallpaper = inputWallPaper;
            getConnectionsManager().sendRequest(req4, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$3$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) parentObject;
            TLRPC.TL_account_getTheme req5 = new TLRPC.TL_account_getTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = theme.id;
            inputTheme.access_hash = theme.access_hash;
            req5.theme = inputTheme;
            req5.format = "android";
            getConnectionsManager().sendRequest(req5, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$4$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.WebPage) {
            TLRPC.TL_messages_getWebPage req6 = new TLRPC.TL_messages_getWebPage();
            req6.url = ((TLRPC.WebPage) parentObject).url;
            req6.hash = 0;
            getConnectionsManager().sendRequest(req6, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$5$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.User) {
            TLRPC.TL_users_getUsers req7 = new TLRPC.TL_users_getUsers();
            req7.id.add(getMessagesController().getInputUser((TLRPC.User) parentObject));
            getConnectionsManager().sendRequest(req7, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$6$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) parentObject;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats req8 = new TLRPC.TL_messages_getChats();
                req8.id.add(Integer.valueOf(chat.id));
                getConnectionsManager().sendRequest(req8, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$7$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channels_getChannels req9 = new TLRPC.TL_channels_getChannels();
                req9.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(req9, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$8$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            }
        } else if (parentObject instanceof String) {
            String string = (String) parentObject;
            if ("wallpaper".equals(string)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_account_getWallPapers(), new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$9$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (string.startsWith("gif")) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$10$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if ("recent".equals(string)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$11$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if ("fav".equals(string)) {
                getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$12$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (string.startsWith("avatar_")) {
                int id = Utilities.parseInt(string).intValue();
                if (id > 0) {
                    TLRPC.TL_photos_getUserPhotos req10 = new TLRPC.TL_photos_getUserPhotos();
                    req10.limit = 80;
                    req10.offset = 0;
                    req10.max_id = 0;
                    req10.user_id = getMessagesController().getInputUser(id);
                    getConnectionsManager().sendRequest(req10, new RequestDelegate(locationKey, parentKey) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$13$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                TLRPC.TL_messages_search req11 = new TLRPC.TL_messages_search();
                req11.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                req11.limit = 80;
                req11.offset_id = 0;
                req11.q = "";
                req11.peer = getMessagesController().getInputPeer(id);
                getConnectionsManager().sendRequest(req11, new RequestDelegate(locationKey, parentKey) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileRefController.this.lambda$requestReferenceFromServer$14$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                    }
                });
            } else if (string.startsWith("sent_")) {
                String[] params = string.split("_");
                if (params.length == 3) {
                    int channelId2 = Utilities.parseInt(params[1]).intValue();
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req12 = new TLRPC.TL_channels_getMessages();
                        req12.channel = getMessagesController().getInputChannel(channelId2);
                        req12.id.add(Utilities.parseInt(params[2]));
                        getConnectionsManager().sendRequest(req12, new RequestDelegate(locationKey, parentKey) {
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                FileRefController.this.lambda$requestReferenceFromServer$15$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                            }
                        });
                        return;
                    }
                    TLRPC.TL_messages_getMessages req13 = new TLRPC.TL_messages_getMessages();
                    req13.id.add(Utilities.parseInt(params[2]));
                    getConnectionsManager().sendRequest(req13, new RequestDelegate(locationKey, parentKey) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ String f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FileRefController.this.lambda$requestReferenceFromServer$16$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                sendErrorToObject(args, 0);
            }
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) parentObject;
            TLRPC.TL_messages_getStickerSet req14 = new TLRPC.TL_messages_getStickerSet();
            req14.stickerset = new TLRPC.TL_inputStickerSetID();
            req14.stickerset.id = stickerSet.set.id;
            req14.stickerset.access_hash = stickerSet.set.access_hash;
            getConnectionsManager().sendRequest(req14, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$17$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered stickerSet2 = (TLRPC.StickerSetCovered) parentObject;
            TLRPC.TL_messages_getStickerSet req15 = new TLRPC.TL_messages_getStickerSet();
            req15.stickerset = new TLRPC.TL_inputStickerSetID();
            req15.stickerset.id = stickerSet2.set.id;
            req15.stickerset.access_hash = stickerSet2.set.access_hash;
            getConnectionsManager().sendRequest(req15, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$18$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            TLRPC.TL_messages_getStickerSet req16 = new TLRPC.TL_messages_getStickerSet();
            req16.stickerset = (TLRPC.InputStickerSet) parentObject;
            getConnectionsManager().sendRequest(req16, new RequestDelegate(locationKey, parentKey) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileRefController.this.lambda$requestReferenceFromServer$19$FileRefController(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else {
            sendErrorToObject(args, 0);
        }
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$0$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$1$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$2$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$3$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$4$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$5$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$6$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$7$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$8$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$9$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$10$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$11$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$12$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$13$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$14$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$15$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$16$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$17$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$18$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    public /* synthetic */ void lambda$requestReferenceFromServer$19$FileRefController(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true);
    }

    private void onUpdateObjectReference(Requester requester, byte[] file_reference, TLRPC.InputFileLocation locationReplacement) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            Object[] objects = this.multiMediaCache.get(multiMedia);
            if (objects != null) {
                TLRPC.TL_inputSingleMedia req = (TLRPC.TL_inputSingleMedia) requester.args[0];
                if (req.media instanceof TLRPC.TL_inputMediaDocument) {
                    ((TLRPC.TL_inputMediaDocument) req.media).id.file_reference = file_reference;
                } else if (req.media instanceof TLRPC.TL_inputMediaPhoto) {
                    ((TLRPC.TL_inputMediaPhoto) req.media).id.file_reference = file_reference;
                }
                int index = multiMedia.multi_media.indexOf(req);
                if (index >= 0) {
                    ArrayList<Object> parentObjects = (ArrayList) objects[3];
                    parentObjects.set(index, (Object) null);
                    boolean done = true;
                    for (int a = 0; a < parentObjects.size(); a++) {
                        if (parentObjects.get(a) != null) {
                            done = false;
                        }
                    }
                    if (done) {
                        this.multiMediaCache.remove(multiMedia);
                        AndroidUtilities.runOnUIThread(new Runnable(multiMedia, objects) {
                            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
                            private final /* synthetic */ Object[] f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                FileRefController.this.lambda$onUpdateObjectReference$20$FileRefController(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }
        } else if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia req2 = (TLRPC.TL_messages_sendMedia) requester.args[0];
            if (req2.media instanceof TLRPC.TL_inputMediaDocument) {
                ((TLRPC.TL_inputMediaDocument) req2.media).id.file_reference = file_reference;
            } else if (req2.media instanceof TLRPC.TL_inputMediaPhoto) {
                ((TLRPC.TL_inputMediaPhoto) req2.media).id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new Runnable(requester) {
                private final /* synthetic */ FileRefController.Requester f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$onUpdateObjectReference$21$FileRefController(this.f$1);
                }
            });
        } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage req3 = (TLRPC.TL_messages_editMessage) requester.args[0];
            if (req3.media instanceof TLRPC.TL_inputMediaDocument) {
                ((TLRPC.TL_inputMediaDocument) req3.media).id.file_reference = file_reference;
            } else if (req3.media instanceof TLRPC.TL_inputMediaPhoto) {
                ((TLRPC.TL_inputMediaPhoto) req3.media).id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new Runnable(requester) {
                private final /* synthetic */ FileRefController.Requester f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$onUpdateObjectReference$22$FileRefController(this.f$1);
                }
            });
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif req4 = (TLRPC.TL_messages_saveGif) requester.args[0];
            req4.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req4, $$Lambda$FileRefController$ZBnyWdChWuCNWq2byus5yIjROgM.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker req5 = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
            req5.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req5, $$Lambda$FileRefController$k9Z9zkrr3WkcMLqSAwQdZQrkqA.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker req6 = (TLRPC.TL_messages_faveSticker) requester.args[0];
            req6.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req6, $$Lambda$FileRefController$p3RdyZcZf6vflNlAZomH2UAMtn0.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers req7 = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
            if (req7.media instanceof TLRPC.TL_inputStickeredMediaDocument) {
                ((TLRPC.TL_inputStickeredMediaDocument) req7.media).id.file_reference = file_reference;
            } else if (req7.media instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                ((TLRPC.TL_inputStickeredMediaPhoto) req7.media).id.file_reference = file_reference;
            }
            getConnectionsManager().sendRequest(req7, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (locationReplacement != null) {
                fileLoadOperation.location = locationReplacement;
            } else {
                requester.location.file_reference = file_reference;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$20$FileRefController(TLRPC.TL_messages_sendMultiMedia multiMedia, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = multiMedia;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$21$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    public /* synthetic */ void lambda$onUpdateObjectReference$22$FileRefController(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, ((Boolean) requester.args[6]).booleanValue());
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$23(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject response, TLRPC.TL_error error) {
    }

    private void sendErrorToObject(Object[] args, int reason) {
        if (args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia req = args[1];
            Object[] objects = this.multiMediaCache.get(req);
            if (objects != null) {
                this.multiMediaCache.remove(req);
                AndroidUtilities.runOnUIThread(new Runnable(req, objects) {
                    private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
                    private final /* synthetic */ Object[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        FileRefController.this.lambda$sendErrorToObject$26$FileRefController(this.f$1, this.f$2);
                    }
                });
            }
        } else if ((args[0] instanceof TLRPC.TL_messages_sendMedia) || (args[0] instanceof TLRPC.TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new Runnable(args) {
                private final /* synthetic */ Object[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileRefController.this.lambda$sendErrorToObject$27$FileRefController(this.f$1);
                }
            });
        } else if (args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(args[0], args[1]);
        } else if (reason == 0) {
            TLRPC.TL_error error = new TLRPC.TL_error();
            error.text = "not found parent object to request reference";
            error.code = 400;
            if (args[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = args[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(args[2], error);
            }
        } else if (reason == 1 && (args[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = args[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    public /* synthetic */ void lambda$sendErrorToObject$26$FileRefController(TLRPC.TL_messages_sendMultiMedia req, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    public /* synthetic */ void lambda$sendErrorToObject$27$FileRefController(Object[] args) {
        getSendMessagesHelper().performSendMessageRequest(args[0], args[1], args[2], args[3], args[4].booleanValue(), args[5], (Object) null, args[6].booleanValue());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r7v0 */
    /* JADX WARNING: type inference failed for: r7v47 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r7v1, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r27, java.lang.String r28, im.bclpbkiauv.tgnet.TLObject r29, boolean r30) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = 0
            r6 = 0
            r7 = 1
            if (r2 == 0) goto L_0x004a
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.FileRefController$Requester>> r8 = r0.parentRequester
            java.lang.Object r8 = r8.get(r2)
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            if (r8 == 0) goto L_0x004a
            r9 = 0
            int r10 = r8.size()
        L_0x001c:
            if (r9 >= r10) goto L_0x0040
            java.lang.Object r11 = r8.get(r9)
            im.bclpbkiauv.messenger.FileRefController$Requester r11 = (im.bclpbkiauv.messenger.FileRefController.Requester) r11
            boolean r12 = r11.completed
            if (r12 == 0) goto L_0x002b
            goto L_0x003d
        L_0x002b:
            java.lang.String r12 = r11.locationKey
            if (r30 == 0) goto L_0x0035
            if (r4 != 0) goto L_0x0035
            r13 = 1
            goto L_0x0036
        L_0x0035:
            r13 = 0
        L_0x0036:
            boolean r12 = r0.onRequestComplete(r12, r6, r3, r13)
            if (r12 == 0) goto L_0x003d
            r4 = 1
        L_0x003d:
            int r9 = r9 + 1
            goto L_0x001c
        L_0x0040:
            if (r4 == 0) goto L_0x0045
            r0.putReponseToCache(r2, r3)
        L_0x0045:
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.FileRefController$Requester>> r9 = r0.parentRequester
            r9.remove(r2)
        L_0x004a:
            r8 = 0
            r9 = 0
            r10 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.FileRefController$Requester>> r11 = r0.locationRequester
            java.lang.Object r11 = r11.get(r1)
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            if (r11 != 0) goto L_0x0058
            return r4
        L_0x0058:
            r12 = 0
            int r13 = r11.size()
        L_0x005d:
            if (r12 >= r13) goto L_0x04a2
            java.lang.Object r14 = r11.get(r12)
            im.bclpbkiauv.messenger.FileRefController$Requester r14 = (im.bclpbkiauv.messenger.FileRefController.Requester) r14
            boolean r15 = r14.completed
            if (r15 == 0) goto L_0x0071
            r2 = r6
            r25 = r11
            r5 = 0
            goto L_0x0499
        L_0x0071:
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_inputFileLocation
            if (r15 == 0) goto L_0x007d
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation[] r9 = new im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[r7]
            boolean[] r10 = new boolean[r7]
        L_0x007d:
            boolean unused = r14.completed = r7
            boolean r15 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.messages_Messages
            if (r15 == 0) goto L_0x01cf
            r15 = r3
            im.bclpbkiauv.tgnet.TLRPC$messages_Messages r15 = (im.bclpbkiauv.tgnet.TLRPC.messages_Messages) r15
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r6 = r15.messages
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x01ca
            r6 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r7 = r15.messages
            int r7 = r7.size()
        L_0x0096:
            if (r6 >= r7) goto L_0x019a
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r5 = r15.messages
            java.lang.Object r5 = r5.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = (im.bclpbkiauv.tgnet.TLRPC.Message) r5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            if (r2 == 0) goto L_0x010d
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            if (r2 == 0) goto L_0x00bb
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            r23 = r7
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r7, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r8 = r2
            goto L_0x0122
        L_0x00bb:
            r23 = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r2 = r2.game
            if (r2 == 0) goto L_0x00e5
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r2 = r2.game
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.document
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r7, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r2 != 0) goto L_0x00e3
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r7 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r7 = r7.game
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.photo
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Photo) r7, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r8 = r2
            goto L_0x0122
        L_0x00e3:
            r8 = r2
            goto L_0x0122
        L_0x00e5:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r2 = r2.photo
            if (r2 == 0) goto L_0x00f9
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r2 = r2.photo
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Photo) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r7, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r8 = r2
            goto L_0x0122
        L_0x00f9:
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x0122
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = r2.webpage
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.WebPage) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r7, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r8 = r2
            goto L_0x0122
        L_0x010d:
            r23 = r7
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r5.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r2 == 0) goto L_0x0122
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r5.action
            im.bclpbkiauv.tgnet.TLRPC$Photo r2 = r2.photo
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r7 = r14.location
            byte[] r2 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Photo) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r7, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r8 = r2
        L_0x0122:
            if (r8 == 0) goto L_0x018e
            if (r30 == 0) goto L_0x0187
            im.bclpbkiauv.tgnet.TLRPC$Peer r2 = r5.to_id
            if (r2 == 0) goto L_0x016b
            im.bclpbkiauv.tgnet.TLRPC$Peer r2 = r5.to_id
            int r2 = r2.channel_id
            if (r2 == 0) goto L_0x016b
            r2 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r7 = r15.chats
            int r7 = r7.size()
        L_0x0137:
            if (r2 >= r7) goto L_0x0164
            r17 = r7
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r7 = r15.chats
            java.lang.Object r7 = r7.get(r2)
            im.bclpbkiauv.tgnet.TLRPC$Chat r7 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r7
            r24 = r8
            int r8 = r7.id
            r25 = r11
            im.bclpbkiauv.tgnet.TLRPC$Peer r11 = r5.to_id
            int r11 = r11.channel_id
            if (r8 != r11) goto L_0x015b
            boolean r8 = r7.megagroup
            if (r8 == 0) goto L_0x016f
            int r8 = r5.flags
            r11 = -2147483648(0xffffffff80000000, float:-0.0)
            r8 = r8 | r11
            r5.flags = r8
            goto L_0x016f
        L_0x015b:
            int r2 = r2 + 1
            r7 = r17
            r8 = r24
            r11 = r25
            goto L_0x0137
        L_0x0164:
            r17 = r7
            r24 = r8
            r25 = r11
            goto L_0x016f
        L_0x016b:
            r24 = r8
            r25 = r11
        L_0x016f:
            im.bclpbkiauv.messenger.MessagesStorage r17 = r26.getMessagesStorage()
            int r2 = r0.currentAccount
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r7 = r15.users
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r8 = r15.chats
            r22 = 0
            r18 = r5
            r19 = r2
            r20 = r7
            r21 = r8
            r17.replaceMessageIfExists(r18, r19, r20, r21, r22)
            goto L_0x018b
        L_0x0187:
            r24 = r8
            r25 = r11
        L_0x018b:
            r8 = r24
            goto L_0x019e
        L_0x018e:
            r24 = r8
            r25 = r11
            int r6 = r6 + 1
            r2 = r28
            r7 = r23
            goto L_0x0096
        L_0x019a:
            r23 = r7
            r25 = r11
        L_0x019e:
            if (r8 != 0) goto L_0x01cc
            im.bclpbkiauv.messenger.MessagesStorage r17 = r26.getMessagesStorage()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Message> r2 = r15.messages
            r5 = 0
            java.lang.Object r2 = r2.get(r5)
            r18 = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r18 = (im.bclpbkiauv.tgnet.TLRPC.Message) r18
            int r2 = r0.currentAccount
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$User> r5 = r15.users
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Chat> r6 = r15.chats
            r22 = 1
            r19 = r2
            r20 = r5
            r21 = r6
            r17.replaceMessageIfExists(r18, r19, r20, r21, r22)
            boolean r2 = im.bclpbkiauv.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x01cc
            java.lang.String r2 = "file ref not found in messages, replacing message"
            im.bclpbkiauv.messenger.FileLog.d(r2)
            goto L_0x01cc
        L_0x01ca:
            r25 = r11
        L_0x01cc:
            r2 = 0
            goto L_0x0480
        L_0x01cf:
            r25 = r11
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.WebPage
            if (r2 == 0) goto L_0x01e3
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$WebPage r2 = (im.bclpbkiauv.tgnet.TLRPC.WebPage) r2
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r5 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.WebPage) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r5, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            r2 = 0
            goto L_0x0480
        L_0x01e3:
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_account_wallPapers
            if (r2 == 0) goto L_0x021c
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_account_wallPapers r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_account_wallPapers) r2
            r5 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$WallPaper> r6 = r2.wallpapers
            int r6 = r6.size()
        L_0x01f1:
            if (r5 >= r6) goto L_0x020b
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$WallPaper> r7 = r2.wallpapers
            java.lang.Object r7 = r7.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r7 = (im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper) r7
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r7.document
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r7, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0208
            goto L_0x020b
        L_0x0208:
            int r5 = r5 + 1
            goto L_0x01f1
        L_0x020b:
            if (r8 == 0) goto L_0x0219
            if (r30 == 0) goto L_0x0219
            im.bclpbkiauv.messenger.MessagesStorage r5 = r26.getMessagesStorage()
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$WallPaper> r6 = r2.wallpapers
            r7 = 1
            r5.putWallpapers(r6, r7)
        L_0x0219:
            r2 = 0
            goto L_0x0480
        L_0x021c:
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper
            if (r2 == 0) goto L_0x0244
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_wallPaper r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_wallPaper) r2
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r2.document
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r5, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0241
            if (r30 == 0) goto L_0x0241
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r5.add(r2)
            im.bclpbkiauv.messenger.MessagesStorage r6 = r26.getMessagesStorage()
            r7 = 0
            r6.putWallpapers(r5, r7)
        L_0x0241:
            r2 = 0
            goto L_0x0480
        L_0x0244:
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_theme
            if (r2 == 0) goto L_0x0264
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_theme r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_theme) r2
            im.bclpbkiauv.tgnet.TLRPC$Document r5 = r2.document
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r5, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0261
            if (r30 == 0) goto L_0x0261
            im.bclpbkiauv.messenger.-$$Lambda$FileRefController$SlTF7uaeCaSsXS6szs49tXukE1c r5 = new im.bclpbkiauv.messenger.-$$Lambda$FileRefController$SlTF7uaeCaSsXS6szs49tXukE1c
            r5.<init>()
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r5)
        L_0x0261:
            r2 = 0
            goto L_0x0480
        L_0x0264:
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.Vector
            if (r2 == 0) goto L_0x030a
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$Vector r2 = (im.bclpbkiauv.tgnet.TLRPC.Vector) r2
            java.util.ArrayList<java.lang.Object> r5 = r2.objects
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0305
            r5 = 0
            java.util.ArrayList<java.lang.Object> r6 = r2.objects
            int r6 = r6.size()
        L_0x027a:
            if (r5 >= r6) goto L_0x0300
            java.util.ArrayList<java.lang.Object> r7 = r2.objects
            java.lang.Object r7 = r7.get(r5)
            boolean r11 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r11 == 0) goto L_0x02be
            r11 = r7
            im.bclpbkiauv.tgnet.TLRPC$User r11 = (im.bclpbkiauv.tgnet.TLRPC.User) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.User) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r30 == 0) goto L_0x02b5
            if (r8 == 0) goto L_0x02b5
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r15.add(r11)
            r17 = r2
            im.bclpbkiauv.messenger.MessagesStorage r2 = r26.getMessagesStorage()
            r18 = r6
            r19 = r8
            r6 = 0
            r8 = 1
            r2.putUsersAndChats(r15, r6, r8, r8)
            im.bclpbkiauv.messenger.-$$Lambda$FileRefController$wSNyLryZH_pMC9RNO0-LV1d_Y2E r2 = new im.bclpbkiauv.messenger.-$$Lambda$FileRefController$wSNyLryZH_pMC9RNO0-LV1d_Y2E
            r2.<init>(r11)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r2)
            goto L_0x02bb
        L_0x02b5:
            r17 = r2
            r18 = r6
            r19 = r8
        L_0x02bb:
            r8 = r19
            goto L_0x02f5
        L_0x02be:
            r17 = r2
            r18 = r6
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
            if (r2 == 0) goto L_0x02f5
            r2 = r7
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r2
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r6 = r14.location
            byte[] r6 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Chat) r2, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r6, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r30 == 0) goto L_0x02f1
            if (r6 == 0) goto L_0x02f1
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r8.add(r2)
            im.bclpbkiauv.messenger.MessagesStorage r11 = r26.getMessagesStorage()
            r19 = r6
            r6 = 1
            r15 = 0
            r11.putUsersAndChats(r15, r8, r6, r6)
            im.bclpbkiauv.messenger.-$$Lambda$FileRefController$M4GYbr8uqT7JT24r8QUWD1fR_6Y r6 = new im.bclpbkiauv.messenger.-$$Lambda$FileRefController$M4GYbr8uqT7JT24r8QUWD1fR_6Y
            r6.<init>(r2)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r6)
            goto L_0x02f3
        L_0x02f1:
            r19 = r6
        L_0x02f3:
            r8 = r19
        L_0x02f5:
            if (r8 == 0) goto L_0x02f8
            goto L_0x0307
        L_0x02f8:
            int r5 = r5 + 1
            r2 = r17
            r6 = r18
            goto L_0x027a
        L_0x0300:
            r17 = r2
            r18 = r6
            goto L_0x0307
        L_0x0305:
            r17 = r2
        L_0x0307:
            r2 = 0
            goto L_0x0480
        L_0x030a:
            boolean r2 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_chats
            if (r2 == 0) goto L_0x036f
            r2 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_chats r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_chats) r2
            java.util.ArrayList r5 = r2.chats
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x036a
            r5 = 0
            java.util.ArrayList r6 = r2.chats
            int r6 = r6.size()
        L_0x0320:
            if (r5 >= r6) goto L_0x0364
            java.util.ArrayList r7 = r2.chats
            java.lang.Object r7 = r7.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$Chat r7 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r7
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Chat) r7, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x035a
            if (r30 == 0) goto L_0x0354
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r11.add(r7)
            im.bclpbkiauv.messenger.MessagesStorage r15 = r26.getMessagesStorage()
            r17 = r2
            r16 = r6
            r2 = 0
            r6 = 1
            r15.putUsersAndChats(r2, r11, r6, r6)
            im.bclpbkiauv.messenger.-$$Lambda$FileRefController$0lX32cTLhsGEF29vEI00KiLOnoo r6 = new im.bclpbkiauv.messenger.-$$Lambda$FileRefController$0lX32cTLhsGEF29vEI00KiLOnoo
            r6.<init>(r7)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r6)
            goto L_0x036d
        L_0x0354:
            r17 = r2
            r16 = r6
            r2 = 0
            goto L_0x036d
        L_0x035a:
            r17 = r2
            r16 = r6
            r2 = 0
            int r5 = r5 + 1
            r2 = r17
            goto L_0x0320
        L_0x0364:
            r17 = r2
            r16 = r6
            r2 = 0
            goto L_0x036d
        L_0x036a:
            r17 = r2
            r2 = 0
        L_0x036d:
            goto L_0x0480
        L_0x036f:
            r2 = 0
            boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_savedGifs
            if (r5 == 0) goto L_0x03ad
            r5 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_savedGifs r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_savedGifs) r5
            r6 = 0
            java.util.ArrayList r7 = r5.gifs
            int r7 = r7.size()
        L_0x037e:
            if (r6 >= r7) goto L_0x0396
            java.util.ArrayList r11 = r5.gifs
            java.lang.Object r11 = r11.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0393
            goto L_0x0396
        L_0x0393:
            int r6 = r6 + 1
            goto L_0x037e
        L_0x0396:
            if (r30 == 0) goto L_0x03ab
            im.bclpbkiauv.messenger.MediaDataController r17 = r26.getMediaDataController()
            r18 = 0
            java.util.ArrayList r6 = r5.gifs
            r20 = 1
            r21 = 0
            r22 = 1
            r19 = r6
            r17.processLoadedRecentDocuments(r18, r19, r20, r21, r22)
        L_0x03ab:
            goto L_0x0480
        L_0x03ad:
            boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet
            if (r5 == 0) goto L_0x03e1
            r5 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_stickerSet r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_stickerSet) r5
            if (r8 != 0) goto L_0x03d5
            r6 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r7 = r5.documents
            int r7 = r7.size()
        L_0x03bd:
            if (r6 >= r7) goto L_0x03d5
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r11 = r5.documents
            java.lang.Object r11 = r11.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x03d2
            goto L_0x03d5
        L_0x03d2:
            int r6 = r6 + 1
            goto L_0x03bd
        L_0x03d5:
            if (r30 == 0) goto L_0x03df
            im.bclpbkiauv.messenger.-$$Lambda$FileRefController$tWnJywLRN1V2KXACkJMKtRNXFsI r6 = new im.bclpbkiauv.messenger.-$$Lambda$FileRefController$tWnJywLRN1V2KXACkJMKtRNXFsI
            r6.<init>(r5)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r6)
        L_0x03df:
            goto L_0x0480
        L_0x03e1:
            boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_recentStickers
            if (r5 == 0) goto L_0x041d
            r5 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_recentStickers r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_recentStickers) r5
            r6 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r7 = r5.stickers
            int r7 = r7.size()
        L_0x03ef:
            if (r6 >= r7) goto L_0x0407
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r11 = r5.stickers
            java.lang.Object r11 = r11.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0404
            goto L_0x0407
        L_0x0404:
            int r6 = r6 + 1
            goto L_0x03ef
        L_0x0407:
            if (r30 == 0) goto L_0x041c
            im.bclpbkiauv.messenger.MediaDataController r17 = r26.getMediaDataController()
            r18 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Document> r6 = r5.stickers
            r20 = 0
            r21 = 0
            r22 = 1
            r19 = r6
            r17.processLoadedRecentDocuments(r18, r19, r20, r21, r22)
        L_0x041c:
            goto L_0x0480
        L_0x041d:
            boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messages_favedStickers
            if (r5 == 0) goto L_0x0459
            r5 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_messages_favedStickers r5 = (im.bclpbkiauv.tgnet.TLRPC.TL_messages_favedStickers) r5
            r6 = 0
            java.util.ArrayList r7 = r5.stickers
            int r7 = r7.size()
        L_0x042b:
            if (r6 >= r7) goto L_0x0443
            java.util.ArrayList r11 = r5.stickers
            java.lang.Object r11 = r11.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Document r11 = (im.bclpbkiauv.tgnet.TLRPC.Document) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Document) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x0440
            goto L_0x0443
        L_0x0440:
            int r6 = r6 + 1
            goto L_0x042b
        L_0x0443:
            if (r30 == 0) goto L_0x047f
            im.bclpbkiauv.messenger.MediaDataController r17 = r26.getMediaDataController()
            r18 = 2
            java.util.ArrayList r6 = r5.stickers
            r20 = 0
            r21 = 0
            r22 = 1
            r19 = r6
            r17.processLoadedRecentDocuments(r18, r19, r20, r21, r22)
            goto L_0x047f
        L_0x0459:
            boolean r5 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.photos_Photos
            if (r5 == 0) goto L_0x047f
            r5 = r3
            im.bclpbkiauv.tgnet.TLRPC$photos_Photos r5 = (im.bclpbkiauv.tgnet.TLRPC.photos_Photos) r5
            r6 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Photo> r7 = r5.photos
            int r7 = r7.size()
        L_0x0467:
            if (r6 >= r7) goto L_0x0480
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$Photo> r11 = r5.photos
            java.lang.Object r11 = r11.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$Photo r11 = (im.bclpbkiauv.tgnet.TLRPC.Photo) r11
            im.bclpbkiauv.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r8 = r0.getFileReference((im.bclpbkiauv.tgnet.TLRPC.Photo) r11, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r10, (im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[]) r9)
            if (r8 == 0) goto L_0x047c
            goto L_0x0480
        L_0x047c:
            int r6 = r6 + 1
            goto L_0x0467
        L_0x047f:
        L_0x0480:
            if (r8 == 0) goto L_0x0490
            if (r9 == 0) goto L_0x0488
            r5 = 0
            r6 = r9[r5]
            goto L_0x048a
        L_0x0488:
            r5 = 0
            r6 = r2
        L_0x048a:
            r0.onUpdateObjectReference(r14, r8, r6)
            r4 = 1
            r7 = 1
            goto L_0x0499
        L_0x0490:
            r5 = 0
            java.lang.Object[] r6 = r14.args
            r7 = 1
            r0.sendErrorToObject(r6, r7)
        L_0x0499:
            int r12 = r12 + 1
            r6 = r2
            r11 = r25
            r2 = r28
            goto L_0x005d
        L_0x04a2:
            r25 = r11
            java.util.HashMap<java.lang.String, java.util.ArrayList<im.bclpbkiauv.messenger.FileRefController$Requester>> r2 = r0.locationRequester
            r2.remove(r1)
            if (r4 == 0) goto L_0x04ae
            r0.putReponseToCache(r1, r3)
        L_0x04ae:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, im.bclpbkiauv.tgnet.TLObject, boolean):boolean");
    }

    public /* synthetic */ void lambda$onRequestComplete$29$FileRefController(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$30$FileRefController(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$31$FileRefController(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    public /* synthetic */ void lambda$onRequestComplete$32$FileRefController(TLRPC.TL_messages_stickerSet stickerSet) {
        getMediaDataController().replaceStickerSet(stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.uptimeMillis() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.uptimeMillis();
            ArrayList<String> keysToDelete = null;
            for (Map.Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.uptimeMillis() - entry.getValue().firstQueryTime) >= 600000) {
                    if (keysToDelete == null) {
                        keysToDelete = new ArrayList<>();
                    }
                    keysToDelete.add(entry.getKey());
                }
            }
            if (keysToDelete != null) {
                int size = keysToDelete.size();
                for (int a = 0; a < size; a++) {
                    this.responseCache.remove(keysToDelete.get(a));
                }
            }
        }
    }

    private CachedResult getCachedResponse(String key) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null || Math.abs(SystemClock.uptimeMillis() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(key);
        return null;
    }

    private void putReponseToCache(String key, TLObject response) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            TLObject unused = cachedResult.response = response;
            long unused2 = cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(key, cachedResult);
        }
        long unused3 = cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC.Document document, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (document == null || location == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputDocumentFileLocation)) {
            int a = 0;
            int size = document.thumbs.size();
            while (a < size) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(a);
                byte[] result = getFileReference(photoSize, location, needReplacement);
                if (needReplacement != null && needReplacement[0]) {
                    replacement[0] = new TLRPC.TL_inputDocumentFileLocation();
                    replacement[0].id = document.id;
                    replacement[0].volume_id = location.volume_id;
                    replacement[0].local_id = location.local_id;
                    replacement[0].access_hash = document.access_hash;
                    replacement[0].file_reference = document.file_reference;
                    replacement[0].thumb_size = photoSize.type;
                    return document.file_reference;
                } else if (result != null) {
                    return result;
                } else {
                    a++;
                }
            }
        } else if (document.id == location.id) {
            return document.file_reference;
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getPeerReferenceReplacement(im.bclpbkiauv.tgnet.TLRPC.User r5, im.bclpbkiauv.tgnet.TLRPC.Chat r6, boolean r7, im.bclpbkiauv.tgnet.TLRPC.InputFileLocation r8, im.bclpbkiauv.tgnet.TLRPC.InputFileLocation[] r9, boolean[] r10) {
        /*
            r4 = this;
            r0 = 0
            if (r10 == 0) goto L_0x005a
            boolean r1 = r10[r0]
            if (r1 == 0) goto L_0x005a
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            r1.<init>()
            r9[r0] = r1
            r1 = r9[r0]
            long r2 = r8.volume_id
            r1.id = r2
            r1 = r9[r0]
            long r2 = r8.volume_id
            r1.volume_id = r2
            r1 = r9[r0]
            int r2 = r8.local_id
            r1.local_id = r2
            r1 = r9[r0]
            r1.big = r7
            if (r5 == 0) goto L_0x0035
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerUser r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerUser
            r1.<init>()
            int r2 = r5.id
            r1.user_id = r2
            long r2 = r5.access_hash
            r1.access_hash = r2
            goto L_0x0054
        L_0x0035:
            boolean r1 = im.bclpbkiauv.messenger.ChatObject.isChannel(r6)
            if (r1 == 0) goto L_0x0046
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChat r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChat
            r1.<init>()
            int r2 = r6.id
            r1.chat_id = r2
            goto L_0x0054
        L_0x0046:
            im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel r1 = new im.bclpbkiauv.tgnet.TLRPC$TL_inputPeerChannel
            r1.<init>()
            int r2 = r6.id
            r1.channel_id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            r2 = r1
        L_0x0054:
            r0 = r9[r0]
            r0.peer = r1
            r0 = 1
            return r0
        L_0x005a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.FileRefController.getPeerReferenceReplacement(im.bclpbkiauv.tgnet.TLRPC$User, im.bclpbkiauv.tgnet.TLRPC$Chat, boolean, im.bclpbkiauv.tgnet.TLRPC$InputFileLocation, im.bclpbkiauv.tgnet.TLRPC$InputFileLocation[], boolean[]):boolean");
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (user == null || user.photo == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(user.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(user.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (chat == null || chat.photo == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(chat.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(chat.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (photo == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputPhotoFileLocation)) {
            if (location instanceof TLRPC.TL_inputFileLocation) {
                int a = 0;
                int size = photo.sizes.size();
                while (a < size) {
                    TLRPC.PhotoSize photoSize = photo.sizes.get(a);
                    byte[] result = getFileReference(photoSize, location, needReplacement);
                    if (needReplacement != null && needReplacement[0]) {
                        replacement[0] = new TLRPC.TL_inputPhotoFileLocation();
                        replacement[0].id = photo.id;
                        replacement[0].volume_id = location.volume_id;
                        replacement[0].local_id = location.local_id;
                        replacement[0].access_hash = photo.access_hash;
                        replacement[0].file_reference = photo.file_reference;
                        replacement[0].thumb_size = photoSize.type;
                        return photo.file_reference;
                    } else if (result != null) {
                        return result;
                    } else {
                        a++;
                    }
                }
            }
            return null;
        } else if (photo.id == location.id) {
            return photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (photoSize == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, location, needReplacement);
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (fileLocation == null || !(location instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != location.local_id || fileLocation.volume_id != location.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && needReplacement != null) {
            needReplacement[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC.WebPage webpage, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        byte[] result = getFileReference(webpage.document, location, needReplacement, replacement);
        if (result != null) {
            return result;
        }
        byte[] result2 = getFileReference(webpage.photo, location, needReplacement, replacement);
        if (result2 != null) {
            return result2;
        }
        if (result2 != null || webpage.cached_page == null) {
            return null;
        }
        int size2 = webpage.cached_page.documents.size();
        for (int b = 0; b < size2; b++) {
            byte[] result3 = getFileReference(webpage.cached_page.documents.get(b), location, needReplacement, replacement);
            if (result3 != null) {
                return result3;
            }
        }
        int size22 = webpage.cached_page.photos.size();
        for (int b2 = 0; b2 < size22; b2++) {
            byte[] result4 = getFileReference(webpage.cached_page.photos.get(b2), location, needReplacement, replacement);
            if (result4 != null) {
                return result4;
            }
        }
        return null;
    }

    public static boolean isFileRefError(String error) {
        return "FILEREF_EXPIRED".equals(error) || "FILE_REFERENCE_EXPIRED".equals(error) || "FILE_REFERENCE_EMPTY".equals(error) || (error != null && error.startsWith("FILE_REFERENCE_"));
    }
}
