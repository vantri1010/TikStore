package org.webrtc.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.webrtc.ali.ContextUtils;
import org.webrtc.utils.NetworkMonitorAutoDetect;

public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static NetworkMonitor instance;
    private NetworkMonitorAutoDetect autoDetector;
    private NetworkMonitorAutoDetect.ConnectionType currentConnectionType = NetworkMonitorAutoDetect.ConnectionType.CONNECTION_UNKNOWN;
    private final ArrayList<Long> nativeNetworkObservers = new ArrayList<>();
    private final ArrayList<NetworkObserver> networkObservers = new ArrayList<>();

    public interface NetworkObserver {
        void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType connectionType);
    }

    private NetworkMonitor() {
    }

    @Deprecated
    public static void init(Context context) {
    }

    public static NetworkMonitor getInstance() {
        if (instance == null) {
            instance = new NetworkMonitor();
        }
        return instance;
    }

    public static void setAutoDetectConnectivityState(boolean shouldAutoDetect) {
        getInstance().setAutoDetectConnectivityStateInternal(shouldAutoDetect);
    }

    private static void assertIsTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected to be true");
        }
    }

    public void startMonitoring(long nativeObserver) {
        Log.d(TAG, "Start monitoring from native observer " + nativeObserver);
        this.nativeNetworkObservers.add(Long.valueOf(nativeObserver));
        setAutoDetectConnectivityStateInternal(true);
    }

    public void stopMonitoring(long nativeObserver) {
        Log.d(TAG, "Stop monitoring from native observer " + nativeObserver);
        setAutoDetectConnectivityStateInternal(false);
        this.nativeNetworkObservers.remove(Long.valueOf(nativeObserver));
    }

    public boolean networkBindingSupported() {
        NetworkMonitorAutoDetect networkMonitorAutoDetect = this.autoDetector;
        return networkMonitorAutoDetect != null && networkMonitorAutoDetect.supportNetworkCallback();
    }

    private static int androidSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    public NetworkMonitorAutoDetect.ConnectionType getCurrentConnectionType() {
        return this.currentConnectionType;
    }

    private long getCurrentDefaultNetId() {
        NetworkMonitorAutoDetect networkMonitorAutoDetect = this.autoDetector;
        if (networkMonitorAutoDetect == null) {
            return -1;
        }
        return networkMonitorAutoDetect.getDefaultNetId();
    }

    private void destroyAutoDetector() {
        NetworkMonitorAutoDetect networkMonitorAutoDetect = this.autoDetector;
        if (networkMonitorAutoDetect != null) {
            networkMonitorAutoDetect.destroy();
            this.autoDetector = null;
        }
    }

    private void setAutoDetectConnectivityStateInternal(boolean shouldAutoDetect) {
        if (!shouldAutoDetect) {
            destroyAutoDetector();
        } else if (this.autoDetector == null) {
            NetworkMonitorAutoDetect networkMonitorAutoDetect = new NetworkMonitorAutoDetect(new NetworkMonitorAutoDetect.Observer() {
                public void onConnectionTypeChanged(NetworkMonitorAutoDetect.ConnectionType newConnectionType) {
                    NetworkMonitor.this.updateCurrentConnectionType(newConnectionType);
                }

                public void onNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation networkInfo) {
                    NetworkMonitor.this.notifyObserversOfNetworkConnect(networkInfo);
                }

                public void onNetworkDisconnect(long networkHandle) {
                    NetworkMonitor.this.notifyObserversOfNetworkDisconnect(networkHandle);
                }
            }, ContextUtils.getApplicationContext());
            this.autoDetector = networkMonitorAutoDetect;
            updateCurrentConnectionType(NetworkMonitorAutoDetect.getConnectionType(networkMonitorAutoDetect.getCurrentNetworkState()));
            updateActiveNetworkList();
        }
    }

    /* access modifiers changed from: private */
    public void updateCurrentConnectionType(NetworkMonitorAutoDetect.ConnectionType newConnectionType) {
        this.currentConnectionType = newConnectionType;
        notifyObserversOfConnectionTypeChange(newConnectionType);
    }

    private void notifyObserversOfConnectionTypeChange(NetworkMonitorAutoDetect.ConnectionType newConnectionType) {
        Iterator<Long> it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            it.next().longValue();
        }
        Iterator<NetworkObserver> it2 = this.networkObservers.iterator();
        while (it2.hasNext()) {
            it2.next().onConnectionTypeChanged(newConnectionType);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkConnect(NetworkMonitorAutoDetect.NetworkInformation networkInfo) {
        Iterator<Long> it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            it.next().longValue();
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkDisconnect(long networkHandle) {
        Iterator<Long> it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            it.next().longValue();
        }
    }

    private void updateActiveNetworkList() {
        List<NetworkMonitorAutoDetect.NetworkInformation> networkInfoList = this.autoDetector.getActiveNetworkList();
        if (networkInfoList != null && networkInfoList.size() != 0) {
            NetworkMonitorAutoDetect.NetworkInformation[] networkInfos = (NetworkMonitorAutoDetect.NetworkInformation[]) networkInfoList.toArray(new NetworkMonitorAutoDetect.NetworkInformation[networkInfoList.size()]);
            Iterator<Long> it = this.nativeNetworkObservers.iterator();
            while (it.hasNext()) {
                it.next().longValue();
            }
        }
    }

    public static void addNetworkObserver(NetworkObserver observer) {
        getInstance().addNetworkObserverInternal(observer);
    }

    private void addNetworkObserverInternal(NetworkObserver observer) {
        this.networkObservers.add(observer);
    }

    public static void removeNetworkObserver(NetworkObserver observer) {
        getInstance().removeNetworkObserverInternal(observer);
    }

    private void removeNetworkObserverInternal(NetworkObserver observer) {
        this.networkObservers.remove(observer);
    }

    public static boolean isOnline() {
        return getInstance().getCurrentConnectionType() != NetworkMonitorAutoDetect.ConnectionType.CONNECTION_NONE;
    }

    static void resetInstanceForTests() {
        instance = new NetworkMonitor();
    }

    public static NetworkMonitorAutoDetect getAutoDetectorForTest() {
        return getInstance().autoDetector;
    }
}
