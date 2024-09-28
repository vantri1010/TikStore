package im.bclpbkiauv.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

public class NotificationCenter {
    public static final int FileDidFailUpload;
    public static final int FileDidUpload;
    public static final int FileLoadProgressChanged;
    public static final int FileUploadProgressChanged;
    private static volatile NotificationCenter[] Instance = new NotificationCenter[3];
    public static final int albumsDidLoad;
    public static final int appDidLogIn;
    public static final int appDidLogout;
    public static final int archivedStickersCountDidLoad;
    public static final int audioDidSent;
    public static final int audioRecordTooShort;
    public static final int audioRouteChanged;
    public static final int authInfoUploadSuccess;
    public static final int bandCardNeedReload;
    public static final int bindBankCardSuccess;
    public static final int bindBankSuccess;
    public static final int blockedUsersDidLoad;
    public static final int botInfoDidLoad;
    public static final int botKeyboardDidLoad;
    public static final int cameraInitied;
    public static final int cdnVipBuySuccess;
    public static final int channelRightsUpdated;
    public static final int chatDidCreated;
    public static final int chatDidFailCreate;
    public static final int chatInfoCantLoad;
    public static final int chatInfoDidLoad;
    public static final int chatOnlineCountDidLoad;
    public static final int chatSearchResultsAvailable;
    public static final int chatSearchResultsLoading;
    public static final int closeChats;
    public static final int closeInCallActivity;
    public static final int closeOtherAppActivities;
    public static final int closeSearchByActiveAction;
    public static final int configLoaded;
    public static final int contactAboutPhonebookLoaded;
    public static final int contactApplieReceived;
    public static final int contactApplyUpdateCount;
    public static final int contactApplyUpdateReceived;
    public static final int contactApplyUpdateState;
    public static final int contactRelationShip;
    public static final int contactsDidLoad;
    public static final int contactsImported;
    public static final int deleteTransSuccess;
    public static final int dialogPhotosLoaded;
    public static final int dialogsNeedReload;
    public static final int dialogsUnreadCounterChanged;
    public static final int didCreatedNewDeleteTask;
    public static final int didEndedCall;
    public static final int didReceiveCall;
    public static final int didReceiveNewMessages = 1;
    public static final int didReceiveSmsCode;
    public static final int didReceivedWebpages;
    public static final int didReceivedWebpagesInUpdates;
    public static final int didRemoveTwoStepPassword;
    public static final int didReplacedPhotoInMemCache;
    public static final int didSetNewTheme;
    public static final int didSetNewWallpapper;
    public static final int didSetPasscode;
    public static final int didSetTwoStepPassword;
    public static final int didStartedCall;
    public static final int didUpdateConnectionState;
    public static final int didUpdatePollResults;
    public static final int didUpdateReactions;
    public static final int didUpdatedMessagesViews;
    public static final int emojiDidLoad;
    public static final int encryptedChatCreated;
    public static final int encryptedChatUpdated;
    public static final int fcDeleteReplyItem;
    public static final int fcFollowStatusUpdate;
    public static final int fcIgnoreOrDeleteItem;
    public static final int fcIgnoreUser;
    public static final int fcLikeStatusUpdate;
    public static final int fcPermissionStatusUpdate;
    public static final int fcPublishSuccess;
    public static final int fcReplyItem;
    public static final int featuredStickersDidLoad;
    public static final int fileDidFailToLoad;
    public static final int fileDidLoad;
    public static final int fileNewChunkAvailable;
    public static final int filePreparingFailed;
    public static final int filePreparingStarted;
    public static final int folderBecomeEmpty;
    public static final int folderWebView;
    public static final int getAccountConfigSuccess;
    public static final int getAccountInfoSuccess;
    public static final int getBackupIpStatus;
    private static volatile NotificationCenter globalInstance;
    public static final int goingToPreviewTheme;
    public static final int groupStickersDidLoad;
    public static final int groupingChanged;
    public static final int hasNewContactsToImport;
    public static final int hideAVideoFloatWindow;
    public static final int historyCleared;
    public static final int httpFileDidFailedLoad;
    public static final int httpFileDidLoad;
    public static final int liveLocationsCacheChanged;
    public static final int liveLocationsChanged;
    public static final int livefinishnotify;
    public static final int liverestartnotify;
    public static final int livestatechange;
    public static final int locationPermissionGranted;
    public static final int loginPasswordSetSuccess;
    public static final int mainUserInfoChanged;
    public static final int mediaCountDidLoad;
    public static final int mediaCountsDidLoad;
    public static final int mediaDidLoad;
    public static final int messagePlayingDidReset;
    public static final int messagePlayingDidSeek;
    public static final int messagePlayingDidStart;
    public static final int messagePlayingGoingToStop;
    public static final int messagePlayingPlayStateChanged;
    public static final int messagePlayingProgressDidChanged;
    public static final int messageReceivedByAck;
    public static final int messageReceivedByServer;
    public static final int messageSendError;
    public static final int messagesDeleted;
    public static final int messagesDidLoad;
    public static final int messagesRead;
    public static final int messagesReadContent;
    public static final int messagesReadEncrypted;
    public static final int musicDidLoad;
    public static final int needDeleteDialog;
    public static final int needReloadArchivedStickers;
    public static final int needReloadRecentDialogsSearch;
    public static final int needSetDayNightTheme;
    public static final int needShowAlert;
    public static final int needShowPlayServicesAlert;
    public static final int newDraftReceived;
    public static final int newEmojiSuggestionsAvailable;
    public static final int newLocationAvailable;
    public static final int newPeopleNearbyAvailable;
    public static final int newSessionReceived;
    public static final int notificationsCountUpdated;
    public static final int notificationsSettingsUpdated;
    public static final int openArticle;
    public static final int openedChatChanged;
    public static final int orderCancelSuccessful;
    public static final int orderConfirmPaySuccessful;
    public static final int orderPutCoinsSuccessful;
    public static final int payPasswordInterrupt;
    public static final int payPasswordModifySuccess;
    public static final int payPasswordResetSuccess;
    public static final int payPasswordSetSuccess;
    public static final int paymentFinished;
    public static final int paymentPasswordDidSet;
    public static final int peerSettingsDidLoad;
    public static final int pinnedLiveMessage;
    public static final int pinnedMessageDidLoad;
    public static final int playerDidStartPlaying;
    public static final int privacyRulesUpdated;
    public static final int proxyCheckDone;
    public static final int proxySettingsChanged;
    public static final int pushMessagesUpdated;
    public static final int pushRemoteOpenChat;
    public static final int realNameAuthSuccess;
    public static final int realNameCancelSuccess;
    public static final int realNameModifySuccess;
    public static final int receivedAVideoCallAccept;
    public static final int receivedAVideoCallBusy;
    public static final int receivedAVideoCallChangeVoice;
    public static final int receivedAVideoCallReady;
    public static final int receivedAVideoCallRequest;
    public static final int recentDocumentsDidLoad;
    public static final int recentImagesDidLoad;
    public static final int rechargeSuccess;
    public static final int recordProgressChanged;
    public static final int recordStartError;
    public static final int recordStarted;
    public static final int recordStopped;
    public static final int reecivedAVideoDiscarded;
    public static final int releaseTransSuccess;
    public static final int reloadHints;
    public static final int reloadInlineHints;
    public static final int reloadInterface;
    public static final int removeAllMessagesFromDialog;
    public static final int replaceMessagesObjects;
    public static final int replyMessagesDidLoad;
    public static final int saveGallerySetChanged;
    public static final int scheduledMessagesUpdated;
    public static final int screenchangenotify;
    public static final int screenshotTook;
    public static final int selectedTopicSuccess;
    public static final int selectedTopicSuccessToPublish;
    public static final int sendingMessagesChanged;
    public static final int startAllHeavyOperations;
    public static final int stickersDidLoad;
    public static final int stopAllHeavyOperations;
    public static final int stopEncodingService;
    public static final int suggestedLangpack;
    public static final int themeListUpdated;
    public static final int themeUploadError;
    public static final int themeUploadedToServer;
    private static int totalEvents;
    public static final int unbindBankCardSuccess;
    public static final int updateChatNewmsgMentionText;
    public static final int updateInterfaces;
    public static final int updateMentionsCount;
    public static final int updateMessageMedia;
    public static final int userFriendsCircleUpdate;
    public static final int userFullInfoDidLoad;
    public static final int userInfoDidLoad;
    public static final int videoLoadingStateChanged;
    public static final int walletFindPasswordSuccess;
    public static final int walletInfoNeedReload;
    public static final int wallpapersDidLoad;
    public static final int wallpapersNeedReload;
    public static final int wasUnableToFindCurrentLocation;
    public static final int weChatPaySuccess;
    public static final int withdrawalSuccess;
    private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray<>();
    private int[] allowedNotifications;
    private boolean animationInProgress;
    private int broadcasting = 0;
    private int currentAccount;
    private int currentHeavyOperationFlags;
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);
    private SparseArray<ArrayList<Object>> observers = new SparseArray<>();
    private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray<>();

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int i, int i2, Object... objArr);
    }

    static {
        totalEvents = 1;
        int i = 1 + 1;
        totalEvents = i;
        int i2 = i + 1;
        totalEvents = i2;
        updateInterfaces = i;
        int i3 = i2 + 1;
        totalEvents = i3;
        dialogsNeedReload = i2;
        int i4 = i3 + 1;
        totalEvents = i4;
        closeChats = i3;
        int i5 = i4 + 1;
        totalEvents = i5;
        messagesDeleted = i4;
        int i6 = i5 + 1;
        totalEvents = i6;
        historyCleared = i5;
        int i7 = i6 + 1;
        totalEvents = i7;
        messagesRead = i6;
        int i8 = i7 + 1;
        totalEvents = i8;
        messagesDidLoad = i7;
        int i9 = i8 + 1;
        totalEvents = i9;
        messageReceivedByAck = i8;
        int i10 = i9 + 1;
        totalEvents = i10;
        messageReceivedByServer = i9;
        int i11 = i10 + 1;
        totalEvents = i11;
        messageSendError = i10;
        int i12 = i11 + 1;
        totalEvents = i12;
        contactsDidLoad = i11;
        int i13 = i12 + 1;
        totalEvents = i13;
        contactsImported = i12;
        int i14 = i13 + 1;
        totalEvents = i14;
        hasNewContactsToImport = i13;
        int i15 = i14 + 1;
        totalEvents = i15;
        chatDidCreated = i14;
        int i16 = i15 + 1;
        totalEvents = i16;
        chatDidFailCreate = i15;
        int i17 = i16 + 1;
        totalEvents = i17;
        chatInfoDidLoad = i16;
        int i18 = i17 + 1;
        totalEvents = i18;
        chatInfoCantLoad = i17;
        int i19 = i18 + 1;
        totalEvents = i19;
        mediaDidLoad = i18;
        int i20 = i19 + 1;
        totalEvents = i20;
        mediaCountDidLoad = i19;
        int i21 = i20 + 1;
        totalEvents = i21;
        mediaCountsDidLoad = i20;
        int i22 = i21 + 1;
        totalEvents = i22;
        encryptedChatUpdated = i21;
        int i23 = i22 + 1;
        totalEvents = i23;
        messagesReadEncrypted = i22;
        int i24 = i23 + 1;
        totalEvents = i24;
        encryptedChatCreated = i23;
        int i25 = i24 + 1;
        totalEvents = i25;
        dialogPhotosLoaded = i24;
        int i26 = i25 + 1;
        totalEvents = i26;
        folderBecomeEmpty = i25;
        int i27 = i26 + 1;
        totalEvents = i27;
        removeAllMessagesFromDialog = i26;
        int i28 = i27 + 1;
        totalEvents = i28;
        notificationsSettingsUpdated = i27;
        int i29 = i28 + 1;
        totalEvents = i29;
        blockedUsersDidLoad = i28;
        int i30 = i29 + 1;
        totalEvents = i30;
        openedChatChanged = i29;
        int i31 = i30 + 1;
        totalEvents = i31;
        didCreatedNewDeleteTask = i30;
        int i32 = i31 + 1;
        totalEvents = i32;
        mainUserInfoChanged = i31;
        int i33 = i32 + 1;
        totalEvents = i33;
        privacyRulesUpdated = i32;
        int i34 = i33 + 1;
        totalEvents = i34;
        updateMessageMedia = i33;
        int i35 = i34 + 1;
        totalEvents = i35;
        recentImagesDidLoad = i34;
        int i36 = i35 + 1;
        totalEvents = i36;
        replaceMessagesObjects = i35;
        int i37 = i36 + 1;
        totalEvents = i37;
        didSetPasscode = i36;
        int i38 = i37 + 1;
        totalEvents = i38;
        didSetTwoStepPassword = i37;
        int i39 = i38 + 1;
        totalEvents = i39;
        didRemoveTwoStepPassword = i38;
        int i40 = i39 + 1;
        totalEvents = i40;
        replyMessagesDidLoad = i39;
        int i41 = i40 + 1;
        totalEvents = i41;
        pinnedMessageDidLoad = i40;
        int i42 = i41 + 1;
        totalEvents = i42;
        newSessionReceived = i41;
        int i43 = i42 + 1;
        totalEvents = i43;
        didReceivedWebpages = i42;
        int i44 = i43 + 1;
        totalEvents = i44;
        didReceivedWebpagesInUpdates = i43;
        int i45 = i44 + 1;
        totalEvents = i45;
        stickersDidLoad = i44;
        int i46 = i45 + 1;
        totalEvents = i46;
        featuredStickersDidLoad = i45;
        int i47 = i46 + 1;
        totalEvents = i47;
        groupStickersDidLoad = i46;
        int i48 = i47 + 1;
        totalEvents = i48;
        messagesReadContent = i47;
        int i49 = i48 + 1;
        totalEvents = i49;
        botInfoDidLoad = i48;
        int i50 = i49 + 1;
        totalEvents = i50;
        userInfoDidLoad = i49;
        int i51 = i50 + 1;
        totalEvents = i51;
        userFullInfoDidLoad = i50;
        int i52 = i51 + 1;
        totalEvents = i52;
        botKeyboardDidLoad = i51;
        int i53 = i52 + 1;
        totalEvents = i53;
        chatSearchResultsAvailable = i52;
        int i54 = i53 + 1;
        totalEvents = i54;
        chatSearchResultsLoading = i53;
        int i55 = i54 + 1;
        totalEvents = i55;
        musicDidLoad = i54;
        int i56 = i55 + 1;
        totalEvents = i56;
        needShowAlert = i55;
        int i57 = i56 + 1;
        totalEvents = i57;
        needShowPlayServicesAlert = i56;
        int i58 = i57 + 1;
        totalEvents = i58;
        didUpdatedMessagesViews = i57;
        int i59 = i58 + 1;
        totalEvents = i59;
        needReloadRecentDialogsSearch = i58;
        int i60 = i59 + 1;
        totalEvents = i60;
        peerSettingsDidLoad = i59;
        int i61 = i60 + 1;
        totalEvents = i61;
        wasUnableToFindCurrentLocation = i60;
        int i62 = i61 + 1;
        totalEvents = i62;
        reloadHints = i61;
        int i63 = i62 + 1;
        totalEvents = i63;
        reloadInlineHints = i62;
        int i64 = i63 + 1;
        totalEvents = i64;
        newDraftReceived = i63;
        int i65 = i64 + 1;
        totalEvents = i65;
        recentDocumentsDidLoad = i64;
        int i66 = i65 + 1;
        totalEvents = i66;
        needReloadArchivedStickers = i65;
        int i67 = i66 + 1;
        totalEvents = i67;
        archivedStickersCountDidLoad = i66;
        int i68 = i67 + 1;
        totalEvents = i68;
        paymentFinished = i67;
        int i69 = i68 + 1;
        totalEvents = i69;
        channelRightsUpdated = i68;
        int i70 = i69 + 1;
        totalEvents = i70;
        openArticle = i69;
        int i71 = i70 + 1;
        totalEvents = i71;
        updateMentionsCount = i70;
        int i72 = i71 + 1;
        totalEvents = i72;
        didUpdatePollResults = i71;
        int i73 = i72 + 1;
        totalEvents = i73;
        chatOnlineCountDidLoad = i72;
        int i74 = i73 + 1;
        totalEvents = i74;
        videoLoadingStateChanged = i73;
        int i75 = i74 + 1;
        totalEvents = i75;
        newPeopleNearbyAvailable = i74;
        int i76 = i75 + 1;
        totalEvents = i76;
        stopAllHeavyOperations = i75;
        int i77 = i76 + 1;
        totalEvents = i77;
        startAllHeavyOperations = i76;
        int i78 = i77 + 1;
        totalEvents = i78;
        sendingMessagesChanged = i77;
        int i79 = i78 + 1;
        totalEvents = i79;
        didUpdateReactions = i78;
        int i80 = i79 + 1;
        totalEvents = i80;
        scheduledMessagesUpdated = i79;
        int i81 = i80 + 1;
        totalEvents = i81;
        httpFileDidLoad = i80;
        int i82 = i81 + 1;
        totalEvents = i82;
        httpFileDidFailedLoad = i81;
        int i83 = i82 + 1;
        totalEvents = i83;
        didUpdateConnectionState = i82;
        int i84 = i83 + 1;
        totalEvents = i84;
        FileDidUpload = i83;
        int i85 = i84 + 1;
        totalEvents = i85;
        FileDidFailUpload = i84;
        int i86 = i85 + 1;
        totalEvents = i86;
        FileUploadProgressChanged = i85;
        int i87 = i86 + 1;
        totalEvents = i87;
        FileLoadProgressChanged = i86;
        int i88 = i87 + 1;
        totalEvents = i88;
        fileDidLoad = i87;
        int i89 = i88 + 1;
        totalEvents = i89;
        fileDidFailToLoad = i88;
        int i90 = i89 + 1;
        totalEvents = i90;
        filePreparingStarted = i89;
        int i91 = i90 + 1;
        totalEvents = i91;
        fileNewChunkAvailable = i90;
        int i92 = i91 + 1;
        totalEvents = i92;
        filePreparingFailed = i91;
        int i93 = i92 + 1;
        totalEvents = i93;
        dialogsUnreadCounterChanged = i92;
        int i94 = i93 + 1;
        totalEvents = i94;
        messagePlayingProgressDidChanged = i93;
        int i95 = i94 + 1;
        totalEvents = i95;
        messagePlayingDidReset = i94;
        int i96 = i95 + 1;
        totalEvents = i96;
        messagePlayingPlayStateChanged = i95;
        int i97 = i96 + 1;
        totalEvents = i97;
        messagePlayingDidStart = i96;
        int i98 = i97 + 1;
        totalEvents = i98;
        messagePlayingDidSeek = i97;
        int i99 = i98 + 1;
        totalEvents = i99;
        messagePlayingGoingToStop = i98;
        int i100 = i99 + 1;
        totalEvents = i100;
        recordProgressChanged = i99;
        int i101 = i100 + 1;
        totalEvents = i101;
        recordStarted = i100;
        int i102 = i101 + 1;
        totalEvents = i102;
        recordStartError = i101;
        int i103 = i102 + 1;
        totalEvents = i103;
        recordStopped = i102;
        int i104 = i103 + 1;
        totalEvents = i104;
        screenshotTook = i103;
        int i105 = i104 + 1;
        totalEvents = i105;
        albumsDidLoad = i104;
        int i106 = i105 + 1;
        totalEvents = i106;
        audioDidSent = i105;
        int i107 = i106 + 1;
        totalEvents = i107;
        audioRecordTooShort = i106;
        int i108 = i107 + 1;
        totalEvents = i108;
        audioRouteChanged = i107;
        int i109 = i108 + 1;
        totalEvents = i109;
        didStartedCall = i108;
        int i110 = i109 + 1;
        totalEvents = i110;
        didEndedCall = i109;
        int i111 = i110 + 1;
        totalEvents = i111;
        closeInCallActivity = i110;
        int i112 = i111 + 1;
        totalEvents = i112;
        appDidLogout = i111;
        int i113 = i112 + 1;
        totalEvents = i113;
        appDidLogIn = i112;
        int i114 = i113 + 1;
        totalEvents = i114;
        configLoaded = i113;
        int i115 = i114 + 1;
        totalEvents = i115;
        needDeleteDialog = i114;
        int i116 = i115 + 1;
        totalEvents = i116;
        newEmojiSuggestionsAvailable = i115;
        int i117 = i116 + 1;
        totalEvents = i117;
        themeUploadedToServer = i116;
        int i118 = i117 + 1;
        totalEvents = i118;
        themeUploadError = i117;
        int i119 = i118 + 1;
        totalEvents = i119;
        pushMessagesUpdated = i118;
        int i120 = i119 + 1;
        totalEvents = i120;
        stopEncodingService = i119;
        int i121 = i120 + 1;
        totalEvents = i121;
        wallpapersDidLoad = i120;
        int i122 = i121 + 1;
        totalEvents = i122;
        wallpapersNeedReload = i121;
        int i123 = i122 + 1;
        totalEvents = i123;
        didReceiveSmsCode = i122;
        int i124 = i123 + 1;
        totalEvents = i124;
        didReceiveCall = i123;
        int i125 = i124 + 1;
        totalEvents = i125;
        emojiDidLoad = i124;
        int i126 = i125 + 1;
        totalEvents = i126;
        closeOtherAppActivities = i125;
        int i127 = i126 + 1;
        totalEvents = i127;
        cameraInitied = i126;
        int i128 = i127 + 1;
        totalEvents = i128;
        didReplacedPhotoInMemCache = i127;
        int i129 = i128 + 1;
        totalEvents = i129;
        didSetNewTheme = i128;
        int i130 = i129 + 1;
        totalEvents = i130;
        themeListUpdated = i129;
        int i131 = i130 + 1;
        totalEvents = i131;
        needSetDayNightTheme = i130;
        int i132 = i131 + 1;
        totalEvents = i132;
        goingToPreviewTheme = i131;
        int i133 = i132 + 1;
        totalEvents = i133;
        locationPermissionGranted = i132;
        int i134 = i133 + 1;
        totalEvents = i134;
        reloadInterface = i133;
        int i135 = i134 + 1;
        totalEvents = i135;
        suggestedLangpack = i134;
        int i136 = i135 + 1;
        totalEvents = i136;
        didSetNewWallpapper = i135;
        int i137 = i136 + 1;
        totalEvents = i137;
        proxySettingsChanged = i136;
        int i138 = i137 + 1;
        totalEvents = i138;
        proxyCheckDone = i137;
        int i139 = i138 + 1;
        totalEvents = i139;
        liveLocationsChanged = i138;
        int i140 = i139 + 1;
        totalEvents = i140;
        newLocationAvailable = i139;
        int i141 = i140 + 1;
        totalEvents = i141;
        liveLocationsCacheChanged = i140;
        int i142 = i141 + 1;
        totalEvents = i142;
        notificationsCountUpdated = i141;
        int i143 = i142 + 1;
        totalEvents = i143;
        playerDidStartPlaying = i142;
        int i144 = i143 + 1;
        totalEvents = i144;
        closeSearchByActiveAction = i143;
        int i145 = i144 + 1;
        totalEvents = i145;
        pushRemoteOpenChat = i144;
        int i146 = i145 + 1;
        totalEvents = i146;
        saveGallerySetChanged = i145;
        int i147 = i146 + 1;
        totalEvents = i147;
        loginPasswordSetSuccess = i146;
        int i148 = i147 + 1;
        totalEvents = i148;
        releaseTransSuccess = i147;
        int i149 = i148 + 1;
        totalEvents = i149;
        deleteTransSuccess = i148;
        int i150 = i149 + 1;
        totalEvents = i150;
        authInfoUploadSuccess = i149;
        int i151 = i150 + 1;
        totalEvents = i151;
        payPasswordSetSuccess = i150;
        int i152 = i151 + 1;
        totalEvents = i152;
        payPasswordModifySuccess = i151;
        int i153 = i152 + 1;
        totalEvents = i153;
        payPasswordResetSuccess = i152;
        int i154 = i153 + 1;
        totalEvents = i154;
        payPasswordInterrupt = i153;
        int i155 = i154 + 1;
        totalEvents = i155;
        bindBankSuccess = i154;
        int i156 = i155 + 1;
        totalEvents = i156;
        getAccountInfoSuccess = i155;
        int i157 = i156 + 1;
        totalEvents = i157;
        getAccountConfigSuccess = i156;
        int i158 = i157 + 1;
        totalEvents = i158;
        walletFindPasswordSuccess = i157;
        int i159 = i158 + 1;
        totalEvents = i159;
        orderCancelSuccessful = i158;
        int i160 = i159 + 1;
        totalEvents = i160;
        orderConfirmPaySuccessful = i159;
        int i161 = i160 + 1;
        totalEvents = i161;
        orderPutCoinsSuccessful = i160;
        int i162 = i161 + 1;
        totalEvents = i162;
        contactApplyUpdateReceived = i161;
        int i163 = i162 + 1;
        totalEvents = i163;
        contactApplieReceived = i162;
        int i164 = i163 + 1;
        totalEvents = i164;
        contactApplyUpdateState = i163;
        int i165 = i164 + 1;
        totalEvents = i165;
        contactApplyUpdateCount = i164;
        int i166 = i165 + 1;
        totalEvents = i166;
        contactAboutPhonebookLoaded = i165;
        int i167 = i166 + 1;
        totalEvents = i167;
        contactRelationShip = i166;
        int i168 = i167 + 1;
        totalEvents = i168;
        userFriendsCircleUpdate = i167;
        int i169 = i168 + 1;
        totalEvents = i169;
        fcFollowStatusUpdate = i168;
        int i170 = i169 + 1;
        totalEvents = i170;
        fcPermissionStatusUpdate = i169;
        int i171 = i170 + 1;
        totalEvents = i171;
        fcIgnoreOrDeleteItem = i170;
        int i172 = i171 + 1;
        totalEvents = i172;
        fcIgnoreUser = i171;
        int i173 = i172 + 1;
        totalEvents = i173;
        fcLikeStatusUpdate = i172;
        int i174 = i173 + 1;
        totalEvents = i174;
        fcReplyItem = i173;
        int i175 = i174 + 1;
        totalEvents = i175;
        fcDeleteReplyItem = i174;
        int i176 = i175 + 1;
        totalEvents = i176;
        selectedTopicSuccess = i175;
        int i177 = i176 + 1;
        totalEvents = i177;
        selectedTopicSuccessToPublish = i176;
        int i178 = i177 + 1;
        totalEvents = i178;
        fcPublishSuccess = i177;
        int i179 = i178 + 1;
        totalEvents = i179;
        getBackupIpStatus = i178;
        int i180 = i179 + 1;
        totalEvents = i180;
        receivedAVideoCallRequest = i179;
        int i181 = i180 + 1;
        totalEvents = i181;
        receivedAVideoCallReady = i180;
        int i182 = i181 + 1;
        totalEvents = i182;
        reecivedAVideoDiscarded = i181;
        int i183 = i182 + 1;
        totalEvents = i183;
        receivedAVideoCallAccept = i182;
        int i184 = i183 + 1;
        totalEvents = i184;
        receivedAVideoCallBusy = i183;
        int i185 = i184 + 1;
        totalEvents = i185;
        hideAVideoFloatWindow = i184;
        int i186 = i185 + 1;
        totalEvents = i186;
        receivedAVideoCallChangeVoice = i185;
        int i187 = i186 + 1;
        totalEvents = i187;
        livestatechange = i186;
        int i188 = i187 + 1;
        totalEvents = i188;
        livefinishnotify = i187;
        int i189 = i188 + 1;
        totalEvents = i189;
        liverestartnotify = i188;
        int i190 = i189 + 1;
        totalEvents = i190;
        screenchangenotify = i189;
        int i191 = i190 + 1;
        totalEvents = i191;
        realNameAuthSuccess = i190;
        int i192 = i191 + 1;
        totalEvents = i192;
        realNameModifySuccess = i191;
        int i193 = i192 + 1;
        totalEvents = i193;
        realNameCancelSuccess = i192;
        int i194 = i193 + 1;
        totalEvents = i194;
        bindBankCardSuccess = i193;
        int i195 = i194 + 1;
        totalEvents = i195;
        unbindBankCardSuccess = i194;
        int i196 = i195 + 1;
        totalEvents = i196;
        rechargeSuccess = i195;
        int i197 = i196 + 1;
        totalEvents = i197;
        withdrawalSuccess = i196;
        int i198 = i197 + 1;
        totalEvents = i198;
        weChatPaySuccess = i197;
        int i199 = i198 + 1;
        totalEvents = i199;
        groupingChanged = i198;
        int i200 = i199 + 1;
        totalEvents = i200;
        updateChatNewmsgMentionText = i199;
        int i201 = i200 + 1;
        totalEvents = i201;
        pinnedLiveMessage = i200;
        int i202 = i201 + 1;
        totalEvents = i202;
        cdnVipBuySuccess = i201;
        int i203 = i202 + 1;
        totalEvents = i203;
        folderWebView = i202;
        int i204 = i203 + 1;
        totalEvents = i204;
        walletInfoNeedReload = i203;
        int i205 = i204 + 1;
        totalEvents = i205;
        paymentPasswordDidSet = i204;
        totalEvents = i205 + 1;
        bandCardNeedReload = i205;
    }

    private class DelayedPost {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public int id;

        private DelayedPost(int id2, Object[] args2) {
            this.id = id2;
            this.args = args2;
        }
    }

    public static NotificationCenter getInstance(int num) {
        NotificationCenter localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    NotificationCenter[] notificationCenterArr = Instance;
                    NotificationCenter notificationCenter = new NotificationCenter(num);
                    localInstance = notificationCenter;
                    notificationCenterArr[num] = notificationCenter;
                }
            }
        }
        return localInstance;
    }

    public static NotificationCenter getGlobalInstance() {
        NotificationCenter localInstance = globalInstance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = globalInstance;
                if (localInstance == null) {
                    NotificationCenter notificationCenter = new NotificationCenter(-1);
                    localInstance = notificationCenter;
                    globalInstance = notificationCenter;
                }
            }
        }
        return localInstance;
    }

    public NotificationCenter(int account) {
        this.currentAccount = account;
    }

    public void setAllowedNotificationsDutingAnimation(int[] notifications) {
        this.allowedNotifications = notifications;
    }

    public void setAnimationInProgress(boolean value) {
        if (value) {
            getGlobalInstance().postNotificationName(stopAllHeavyOperations, 512);
        } else {
            getGlobalInstance().postNotificationName(startAllHeavyOperations, 512);
        }
        this.animationInProgress = value;
        if (!value && !this.delayedPosts.isEmpty()) {
            for (int a = 0; a < this.delayedPosts.size(); a++) {
                DelayedPost delayedPost = this.delayedPosts.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            this.delayedPosts.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return this.animationInProgress;
    }

    public int getCurrentHeavyOperationFlags() {
        return this.currentHeavyOperationFlags;
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = id == startAllHeavyOperations || id == stopAllHeavyOperations;
        if (!allowDuringAnimation && this.allowedNotifications != null) {
            int a = 0;
            while (true) {
                int[] iArr = this.allowedNotifications;
                if (a >= iArr.length) {
                    break;
                } else if (iArr[a] == id) {
                    allowDuringAnimation = true;
                    break;
                } else {
                    a++;
                }
            }
        }
        if (id == startAllHeavyOperations) {
            this.currentHeavyOperationFlags &= ~args[0].intValue();
        } else if (id == stopAllHeavyOperations) {
            this.currentHeavyOperationFlags |= args[0].intValue();
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("postNotificationName allowed only from MAIN thread");
        } else if (allowDuringAnimation || !this.animationInProgress) {
            this.broadcasting++;
            ArrayList<Object> objects = this.observers.get(id);
            if (objects != null && !objects.isEmpty()) {
                for (int a = 0; a < objects.size(); a++) {
                    ((NotificationCenterDelegate) objects.get(a)).didReceivedNotification(id, this.currentAccount, args);
                }
            }
            int i = this.broadcasting - 1;
            this.broadcasting = i;
            if (i == 0) {
                if (this.removeAfterBroadcast.size() != 0) {
                    for (int a2 = 0; a2 < this.removeAfterBroadcast.size(); a2++) {
                        int key = this.removeAfterBroadcast.keyAt(a2);
                        ArrayList<Object> arrayList = this.removeAfterBroadcast.get(key);
                        for (int b = 0; b < arrayList.size(); b++) {
                            removeObserver(arrayList.get(b), key);
                        }
                    }
                    this.removeAfterBroadcast.clear();
                }
                if (this.addAfterBroadcast.size() != 0) {
                    for (int a3 = 0; a3 < this.addAfterBroadcast.size(); a3++) {
                        int key2 = this.addAfterBroadcast.keyAt(a3);
                        ArrayList<Object> arrayList2 = this.addAfterBroadcast.get(key2);
                        for (int b2 = 0; b2 < arrayList2.size(); b2++) {
                            addObserver(arrayList2.get(b2), key2);
                        }
                    }
                    this.addAfterBroadcast.clear();
                }
            }
        } else {
            this.delayedPosts.add(new DelayedPost(id, args));
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("delay post notification " + id + " with args count = " + args.length);
            }
        }
    }

    public void addObserver(Object observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("addObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<Object> arrayList = this.addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<Object> objects = this.observers.get(id);
            if (objects == null) {
                SparseArray<ArrayList<Object>> sparseArray = this.observers;
                ArrayList<Object> arrayList2 = new ArrayList<>();
                objects = arrayList2;
                sparseArray.put(id, arrayList2);
            }
            if (!objects.contains(observer)) {
                objects.add(observer);
            }
        }
    }

    public void removeObserver(Object observer, int id) {
        if (BuildVars.DEBUG_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new RuntimeException("removeObserver allowed only from MAIN thread");
        } else if (this.broadcasting != 0) {
            ArrayList<Object> arrayList = this.removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
        } else {
            ArrayList<Object> objects = this.observers.get(id);
            if (objects != null) {
                objects.remove(observer);
            }
        }
    }

    public boolean hasObservers(int id) {
        return this.observers.indexOfKey(id) >= 0;
    }
}
