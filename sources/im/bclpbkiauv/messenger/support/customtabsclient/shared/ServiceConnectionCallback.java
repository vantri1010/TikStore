package im.bclpbkiauv.messenger.support.customtabsclient.shared;

import im.bclpbkiauv.messenger.support.customtabs.CustomTabsClient;

public interface ServiceConnectionCallback {
    void onServiceConnected(CustomTabsClient customTabsClient);

    void onServiceDisconnected();
}
