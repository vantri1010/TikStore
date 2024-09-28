package im.bclpbkiauv.messenger.support.customtabsclient.shared;

import android.content.ComponentName;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsClient;
import im.bclpbkiauv.messenger.support.customtabs.CustomTabsServiceConnection;
import java.lang.ref.WeakReference;

public class ServiceConnection extends CustomTabsServiceConnection {
    private WeakReference<ServiceConnectionCallback> mConnectionCallback;

    public ServiceConnection(ServiceConnectionCallback connectionCallback) {
        this.mConnectionCallback = new WeakReference<>(connectionCallback);
    }

    public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
        ServiceConnectionCallback connectionCallback = (ServiceConnectionCallback) this.mConnectionCallback.get();
        if (connectionCallback != null) {
            connectionCallback.onServiceConnected(client);
        }
    }

    public void onServiceDisconnected(ComponentName name) {
        ServiceConnectionCallback connectionCallback = (ServiceConnectionCallback) this.mConnectionCallback.get();
        if (connectionCallback != null) {
            connectionCallback.onServiceDisconnected();
        }
    }
}
