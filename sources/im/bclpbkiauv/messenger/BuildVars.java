package im.bclpbkiauv.messenger;

import com.bjz.comm.net.SPConstant;

public class BuildVars {
    public static final boolean APP_ALLOW_REPOST_VIDEO = true;
    public static final boolean APP_ALLOW_SAVE_VIDEO = true;
    public static String APP_HASH = "fb9f0bb7fdd0760c354cc3d80cecb1d9";
    public static int APP_ID = 30915;
    public static final boolean APP_OPEN_MM = false;
    public static int BUILD_VERSION = 1;
    public static String BUILD_VERSION_STRING = BuildConfig.VERSION_NAME;
    public static String CHANNEL_NAME = ApplicationLoader.applicationContext.getResources().getString(R.string.channelName);
    public static boolean CHECK_UPDATES = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.check_updates);
    public static final String COMPANY_TAG = "Yixin";
    public static boolean DEBUG_PRIVATE_VERSION = true;
    public static boolean DEBUG_VERSION = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.debug_version);
    public static boolean DIGIT_COIN_ENABLE = false;
    public static int EDITION = 0;
    public static final int EDITION_PERSONAL = 0;
    public static final boolean ENABLE_CHANGE_SYSTEM_NAME = true;
    public static final boolean ENABLE_CHAT_ATTACH_LOCATION_TOGGLE = false;
    public static final boolean ENABLE_CHAT_DOCUMENT_TOGGLE = false;
    public static final boolean ENABLE_CHAT_HAD_EDIT = false;
    public static final boolean ENABLE_CHAT_SHOW_WHO_TAKE_RED_PACKET = false;
    public static final boolean ENABLE_EDIT_ACCOUNT = true;
    public static final boolean ENABLE_HOME_PAGE_BOTTOM_ITEM_SHOW_NET_PIC = false;
    public static final boolean ENABLE_HOME_PAGE_SHOW_WETAB_BOTTOM = false;
    public static boolean ENABLE_ME_ABOUT_APP = false;
    public static boolean ENABLE_ME_ONLINE_SERVICE = false;
    public static final String ENABLE_SETTING = "BW-570-";
    public static final boolean ENABLE_SHIELD_DELETE_MESSAGE = false;
    public static final boolean ENABLE_SHIELD_EDIT_MESSAGE = false;
    public static final boolean ENABLE_SHOW_GROUP_ONLINE_COUNTS = false;
    public static final boolean ENABLE_SHOW_GROUP_ONLINE_STATUS_ADMIN = false;
    public static final boolean ENABLE_SHOW_GROUP_ONLINE_STATUS_NORMAL_MEMBER = false;
    public static final boolean ENABLE_SHOW_SESSION_ACTIVE = false;
    public static final boolean ENABLE_SIGN_PAGE_AGREEMENT_CHECKBOX = false;
    public static final boolean ENABLE_SIGN_UP_PAGE_IS_FIRST = true;
    public static final boolean ENABLE_SIGN_UP_USER_AVATAR = false;
    public static final boolean ENABLE_SIGN_UP_USER_DATE_BIRTHDAY = false;
    public static final boolean ENABLE_SIGN_UP_USER_GENDER = false;
    public static final boolean ENABLE_SIGN_UP_USER_NICKNAME = true;
    public static final boolean ENABLE_SUPPORT_AUDIO_VIDEO_CALL = false;
    public static final boolean ENABLE_SUPPORT_IMAGE_CODE = false;
    public static final boolean ENABLE_SUPPORT_MODIFY_USER_GENDER_IN_USER_PAGE = false;
    public static final boolean ENABLE_SUPPORT_ONLINE_STATUE_SHOW = true;
    public static final boolean ENABLE_SUPPORT_OPEN_DEFAULT_LANGUAGE = false;
    public static final boolean ENABLE_SUPPORT_OPEN_DOH = false;
    public static final boolean ENABLE_SUPPORT_OPEN_INSTALL = false;
    public static final boolean ENABLE_SUPPORT_OPEN_OSS = true;
    public static final boolean ENABLE_SUPPORT_OPEN_UUID = true;
    public static final boolean ENABLE_SUPPORT_SECRET_CHAT = false;
    public static final boolean ENABLE_SUPPORT_SIGN_UP_CONFIRM_PASSWORD = false;
    public static final boolean ENABLE_SUPPORT_SIGN_UP_INVITE_CODE = false;
    public static final boolean ENABLE_TWO_STEP_CHECK = false;
    public static final boolean ENABLE_USE_GOOGLE_VERIFICATION_CODE = false;
    public static final String EN_APP_NAME = "Yixin";
    public static String HOCKEY_APP_HASH = "e283aac0-7c0f-4f2e-bcf7-90acc19903ed";
    public static String HOCKEY_APP_HASH_DEBUG = "f180c508-f49a-40bd-b8ac-50577ce9aff6";
    public static boolean IP_OFF = true;
    public static boolean LOGS_ENABLED = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.logs_enabled);
    public static boolean PHONE_CHECK = false;
    public static String PLAYSTORE_APP_URL = "";
    public static boolean RELEASE_VERSION = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.release);
    public static String SMS_HASH = "";
    public static final String SYSTEM_NAME = "số hệ thống";
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean VOIP_DEBUG = ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.voip_debug);
    public static boolean WALLET_ENABLE = true;
    public static boolean WALLET_RED_PACKET_ENABLE = true;

    static {
        if (ApplicationLoader.applicationContext != null) {
            ApplicationLoader.applicationContext.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0);
        }
    }
}
