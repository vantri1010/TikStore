package im.bclpbkiauv.ui.constants;

public interface Constants {
    public static final String API_VERSION = "0.0.1";
    public static final int PAGE_SIZE = 20;
    public static final int PAGE_START_OFFSET = 1;
    public static final String SDK_INIT_TAG = "SDKINIT ";
    public static final String URL_PRIVACY_POLICY = "file:///android_asset/h5/privacyAgreement/PrivacyAgreement.html";
    public static final String URL_USER_AGREEMENT = "file:///android_asset/h5/privacyAgreement/UserAgreement.html";

    public enum ChatSelectionPopMenuEnum {
        MSG_SEND_RETRY,
        MSG_DELETE,
        MSG_FORWARD,
        MSG_COPY1,
        MSG_COPY2,
        PIC_SAVE_TO_GALLERY1,
        PIC_SAVE_TO_GALLERY2,
        LANGUAGE_OR_THEME,
        MSG_SHARE,
        MSG_REPLAY,
        STICKER_OR_MASKS,
        MSG_DOWNLOAD,
        MSG_EDIT,
        MSG_PIN,
        MSG_UNPIN,
        CONTACT_ADD,
        MSG_FAVE_ADD,
        MSG_FAVE_REMOVE,
        MSG_COPY_LINK,
        MSG_REPORT,
        MSG_CANCEL_SENDING,
        QR_CODE_PARSE,
        CALL_BACK_OR_CALL_AGAIN,
        CALL1,
        CALL2,
        POLL_CANCEL,
        POLL_STOP,
        GIF_SAVE,
        GIF_REMOVE,
        MSG_SEND_NOW,
        MSG_SCHEDULE_EDIT_TIME,
        TRANSLATE,
        TRANSLATE_CANCEL
    }

    public static class DialogsFragmentTopMenuConfig {
        private static DialogsFragmentTopMenuConfig Activity;
        private static DialogsFragmentTopMenuConfig Announcement;
        private static DialogsFragmentTopMenuConfig Interaction;
        private static DialogsFragmentTopMenuConfig Notification;
        public final String accessHash;
        public String text;
        public final long userId;

        DialogsFragmentTopMenuConfig(int userId2, String accessHash2) {
            this.userId = (long) userId2;
            this.accessHash = accessHash2;
        }

        public static DialogsFragmentTopMenuConfig getNotification() {
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig = Notification;
            if (dialogsFragmentTopMenuConfig != null) {
                return dialogsFragmentTopMenuConfig;
            }
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig2 = new DialogsFragmentTopMenuConfig(774000, "687868799549883333");
            Notification = dialogsFragmentTopMenuConfig2;
            return dialogsFragmentTopMenuConfig2;
        }

        public static DialogsFragmentTopMenuConfig getActivity() {
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig = Activity;
            if (dialogsFragmentTopMenuConfig != null) {
                return dialogsFragmentTopMenuConfig;
            }
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig2 = new DialogsFragmentTopMenuConfig(771000, "6878687995360233124");
            Activity = dialogsFragmentTopMenuConfig2;
            return dialogsFragmentTopMenuConfig2;
        }

        public static DialogsFragmentTopMenuConfig getAnnouncement() {
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig = Announcement;
            if (dialogsFragmentTopMenuConfig != null) {
                return dialogsFragmentTopMenuConfig;
            }
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig2 = new DialogsFragmentTopMenuConfig(772000, "6878687995361233134");
            Announcement = dialogsFragmentTopMenuConfig2;
            return dialogsFragmentTopMenuConfig2;
        }

        public static DialogsFragmentTopMenuConfig getInteraction() {
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig = Interaction;
            if (dialogsFragmentTopMenuConfig != null) {
                return dialogsFragmentTopMenuConfig;
            }
            DialogsFragmentTopMenuConfig dialogsFragmentTopMenuConfig2 = new DialogsFragmentTopMenuConfig(773000, "6878687995361232324");
            Interaction = dialogsFragmentTopMenuConfig2;
            return dialogsFragmentTopMenuConfig2;
        }

        public static boolean isOfficalCode(int userId2) {
            return userId2 == 777000;
        }

        public static boolean isSystemCode(long dialogId) {
            return getNotification().userId == dialogId || getActivity().userId == dialogId || getAnnouncement().userId == dialogId || getInteraction().userId == dialogId;
        }

        public static boolean isSystemNotificationCode(long dialogId) {
            return getNotification().userId == dialogId;
        }

        public static boolean isSystemActivityCode(long dialogId) {
            return getActivity().userId == dialogId;
        }

        public static boolean isSystemAnnouncementCode(long dialogId) {
            return getAnnouncement().userId == dialogId;
        }

        public static boolean isSystemInteractionCode(long dialogId) {
            return getInteraction().userId == dialogId;
        }
    }
}
