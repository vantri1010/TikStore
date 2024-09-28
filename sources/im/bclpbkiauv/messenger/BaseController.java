package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.ConnectionsManager;

public class BaseController {
    protected int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int num) {
        this.parentAccountInstance = AccountInstance.getInstance(num);
        this.currentAccount = num;
    }

    /* access modifiers changed from: protected */
    public AccountInstance getAccountInstance() {
        return this.parentAccountInstance;
    }

    /* access modifiers changed from: protected */
    public MessagesController getMessagesController() {
        return this.parentAccountInstance.getMessagesController();
    }

    /* access modifiers changed from: protected */
    public ContactsController getContactsController() {
        return this.parentAccountInstance.getContactsController();
    }

    /* access modifiers changed from: protected */
    public MediaDataController getMediaDataController() {
        return this.parentAccountInstance.getMediaDataController();
    }

    /* access modifiers changed from: protected */
    public ConnectionsManager getConnectionsManager() {
        return this.parentAccountInstance.getConnectionsManager();
    }

    /* access modifiers changed from: protected */
    public LocationController getLocationController() {
        return this.parentAccountInstance.getLocationController();
    }

    /* access modifiers changed from: protected */
    public NotificationsController getNotificationsController() {
        return this.parentAccountInstance.getNotificationsController();
    }

    /* access modifiers changed from: protected */
    public NotificationCenter getNotificationCenter() {
        return this.parentAccountInstance.getNotificationCenter();
    }

    /* access modifiers changed from: protected */
    public UserConfig getUserConfig() {
        return this.parentAccountInstance.getUserConfig();
    }

    /* access modifiers changed from: protected */
    public MessagesStorage getMessagesStorage() {
        return this.parentAccountInstance.getMessagesStorage();
    }

    /* access modifiers changed from: protected */
    public DownloadController getDownloadController() {
        return this.parentAccountInstance.getDownloadController();
    }

    /* access modifiers changed from: protected */
    public SendMessagesHelper getSendMessagesHelper() {
        return this.parentAccountInstance.getSendMessagesHelper();
    }

    /* access modifiers changed from: protected */
    public SecretChatHelper getSecretChatHelper() {
        return this.parentAccountInstance.getSecretChatHelper();
    }

    /* access modifiers changed from: protected */
    public StatsController getStatsController() {
        return this.parentAccountInstance.getStatsController();
    }

    /* access modifiers changed from: protected */
    public FileLoader getFileLoader() {
        return this.parentAccountInstance.getFileLoader();
    }

    /* access modifiers changed from: protected */
    public FileRefController getFileRefController() {
        return this.parentAccountInstance.getFileRefController();
    }
}
